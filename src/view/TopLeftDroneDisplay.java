package view;

import Model.Drone;
import Model.TelemetryData;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

class TopLeftDroneDisplay extends VBox {

    // Camera Setting
    /** Represent the zoom scale; 1.0 = 1 pixel  */
    private double ZOOM_SCALE = 1.0;
    // Represents the world X coordinate (the center of screen)
    private double CAM_X = 0.0;
    // Represents the world Y coordinate (the center of screen)
    private double CAM_Y = 0.0;

    // Configuration
    /** Represents the min zoom level */
    private static final double MIN_ZOOM = 0.1;
    /** Represents the max zoom level */
    private static final double MAX_ZOOM = 5.0;
    /** Represents the min Drone size */
    private static final double MIN_DRONE_SIZE = 20;
    /** Represents the max Drone size */
    private static final double MAX_DRONE_SIZE = 80;
    /* The width of the DroneShape polygon (-10 to 10 = 20px width) */
    private static final double BASE_SHAPE_WIDTH = 20.0;

    // Dragging Fields
    /** Represents the drag mouse in the x direction */
    private double lastMouseX;
    /** Represents the drag mouse in the y direction */
    private double lastMouseY;

    // Scene Graph
    /** Represents the "window"; its fix size, and clips the content */
    private final Pane myViewport;
    /** Represents the "map" (infinite size, and can scale/moves around) */
    private final Pane myWorld;


    // Drone Color Settings
    /** Represents the selected drone the user is looking at */
    private DroneShape mySelectedDrone;
    /** Represents the default drone color */
    private static final Color COLOR_DEFAULT = Color.WHITE;
    /** Represents the color when a drone is selected */
    private static final Color COLOR_SELECTED = Color.DEEPSKYBLUE;

    //Drone animation tracking
    /*
     * Map to track the active Timeline for each drone ID
     */
    private final Map<Integer, Timeline> activeTimelines = new ConcurrentHashMap<>();

    /**
     * Represents the time label of the simulation.
     */
    private final Label myTimeLabel = new Label("Time: 00:00:00");

    /**
     * Map of Images representing Drones (Drone ID -> ImageView), concurrent since
     * singleton stuff makes us gotta worry about multiple threads.
     */
    private final Map<Integer, DroneShape> myDroneViews = new ConcurrentHashMap<>();

    /**
     * Constructor for the TopLeftDroneDisplay, that initialize and set up the display up.
     */
    public TopLeftDroneDisplay() {

        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);

        // Initialize the viewport
        myViewport = new Pane();
        myViewport.getStyleClass().add("drone-display");
        // This allows to capture the clicks on empty space
        myViewport.setPickOnBounds(true);
        myViewport.setStyle("-fx-background-color: rgba(0,0,0,0.01);");

        // Clipping the viewpoint so that the world doesn't spill out
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(myViewport.widthProperty());
        clip.heightProperty().bind(myViewport.heightProperty());
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        myViewport.setClip(clip);

        // Initialize the world (aka the container for drones)
        myWorld = new Pane();
        // Handling the layout manually by transformation
        myWorld.setManaged(false);
        myViewport.getChildren().add(myWorld);

        // Setting up the UI Container
        VBox droneBox = new VBox();
        droneBox.getStyleClass().add("rounded-box");
        VBox.setVgrow(droneBox, Priority.ALWAYS);
        VBox.setVgrow(myViewport, Priority.ALWAYS);
        myViewport.setMinHeight(0);

        // Setting up the Panel Title Section
        HBox headerBox = new HBox(10);
        Label titleLabel = new Label("Drone Monitor");
        titleLabel.getStyleClass().add("title-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Creating the Static Header Label
        Label timeLabel = new Label("TIME");
        timeLabel.getStyleClass().add("time-label-label");

        // Styling the time number itself
        myTimeLabel.setText("00:00:00");
        myTimeLabel.getStyleClass().add("time-label");

        //  Grouping up the Time Label and the TimeNumber
        HBox timeBox = new HBox(8, timeLabel, myTimeLabel);
        timeBox.setAlignment(Pos.CENTER);
        timeBox.getStyleClass().add("time-box");

        // Adding the title, spacer and timeBox together under headers
        headerBox.getChildren().addAll(titleLabel, spacer, timeBox);

        // Adding the HeaderBox, and viewport into the drone screen
        droneBox.getChildren().addAll(headerBox, myViewport);
        getChildren().add(droneBox);

        // Updating our viewport aka our camera on resize
        myViewport.widthProperty().addListener((_, _, _) ->
                updateCameraTransform());
        myViewport.heightProperty().addListener((_, _, _) ->
                updateCameraTransform());

        setupInputHandlers();
    }

    /**
     * Handles all the Mouse Interactions (Pan & Zoom).
     * Also affect the 'myWorld' transforms (NOT the individual drones).
     */
    private void setupInputHandlers() {
        // Zoom Actions
        myViewport.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            if (deltaY == 0) return;

            double zoomFactor;
            if (deltaY > 0) {
                zoomFactor = 1.1;
            } else {
                zoomFactor = 0.9;
            }
            ZOOM_SCALE = Math.max(MIN_ZOOM, Math.min(ZOOM_SCALE * zoomFactor, MAX_ZOOM));

            updateCameraTransform();
            event.consume();
        });

        // Drag Start
        myViewport.setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            myViewport.setCursor(Cursor.CLOSED_HAND);
            event.consume();
        });

        // Drag move
        myViewport.setOnMouseDragged(event -> {
            double changeX = event.getX() - lastMouseX;
            double changeY = event.getY() - lastMouseY;

            lastMouseX = event.getX();
            lastMouseY = event.getY();

            // Converting the screen pixels to our world units to shift camera
            CAM_X -= changeX / ZOOM_SCALE;
            // It's + because screen Y is inverted relative to the World Y
            CAM_Y += changeY / ZOOM_SCALE;

            updateCameraTransform();
            event.consume();
        });

        // Drag end
        myViewport.setOnMouseReleased(_ -> myViewport.setCursor(Cursor.DEFAULT));

        // Reset
        myViewport.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                CAM_X = 0;
                CAM_Y = 0;
                ZOOM_SCALE = 1.0;
                updateCameraTransform();
            } else if (event.getClickCount() == 1) { // Single Click = Deselect Drone
                if (mySelectedDrone != null) {
                    mySelectedDrone.setColor(Color.WHITE);
                    mySelectedDrone = null;
                    MonitorDash.getInstance().swapRightPanel(false);
                }
            }
        });
    }

    /**
     * Applying the current CAM_X, CAM_Y, and Zoom to the World Pane.
     * This handles and adjusts the movement to the entire map instantly.
     */
    private void updateCameraTransform() {
        double width = myViewport.getWidth();
        double height = myViewport.getHeight();

        // The world scale
        myWorld.setScaleX(ZOOM_SCALE);
        myWorld.setScaleY(ZOOM_SCALE);

        // Translating (aka movement of our world)
        // We want the World Point (CAM_X, -CAM_Y) to be at Screen Center (width/2, height/2).
        // Formula: Screen = (World - Cam) * Scale + Center

        // Since the myWorld is unmanaged, its default origin is (0,0) of Viewport.
        // We just need to shift it so (CamX, -CamY) aligns with Viewport Center.

        double centerX = width / 2.0;
        double centerY = height / 2.0;

        // World(0,0) position on screen:
        double originScreenX = centerX - (CAM_X * ZOOM_SCALE);
        // REMEMBER: -CAM_Y because Y is inverted
        double originScreenY = centerY - (-CAM_Y * ZOOM_SCALE);

        myWorld.setTranslateX(originScreenX);
        myWorld.setTranslateY(originScreenY);
    }

    /**
     * A method being called by Backend.
     * Updates the Drone's position INSIDE the World.
     */
    public void refreshDroneDisplay(Drone drone) {
        if (drone == null) return;
        MonitorDash.getInstance().myDrones.put(drone.getDroneID(), drone);

        Platform.runLater(() -> {
            // Check if new (for animation logic)
            boolean isNew = !myDroneViews.containsKey(drone.getDroneID());
            DroneShape view = getOrCreateView(drone);

            // Stop existing animation if running
            if (activeTimelines.containsKey(drone.getDroneID())) {
                activeTimelines.get(drone.getDroneID()).stop();
            }

            // Start new update
            Timeline newAnim = updateDronePosition(view, drone.getDroneTelemetry(), !isNew);

            // Store new animation
            if (newAnim != null) {
                activeTimelines.put(drone.getDroneID(), newAnim);
                // Clean up map when done
                newAnim.setOnFinished(_ -> activeTimelines.remove(drone.getDroneID()));
            }

            MonitorDash.getInstance().updateStatsText(drone);
        });
    }

    /**
     * It updates the position, size and orientation of the visual drone on display.
     *
     * @param theView is the imageview to update.
     * @param theData is the telemetry data of the drone to update for.
     * @param theAnimate true to animate transition, or false for instant update instead.
     */
    private Timeline updateDronePosition(final DroneShape theView, final TelemetryData theData, final boolean theAnimate) {
        Timeline returnAnimation;
        // Calculates the Target (Local World coordinates) (1 meter = 1 pixel)
        double targetX = theData.getLongitude();
        // PS: Invert Y (Sim Up is +Y, Screen Down is +Y)
        double targetY = -theData.getLatitude();

        // Calculating the size logic
        double altitudePercent = Math.min(Math.max(theData.getAltitude(), 0), 100) / 100.0;
        double targetPixelSize = MIN_DRONE_SIZE + (MAX_DRONE_SIZE - MIN_DRONE_SIZE) * altitudePercent;

        double targetScale = targetPixelSize / BASE_SHAPE_WIDTH;
        double targetAngle = theData.getOrientation();

        if (theAnimate) {
            // Smooth Animation
            double currentAngle = theView.getRotate();
            double delta = ((targetAngle - currentAngle + 540) % 360) - 180;
            double shortestAngle = currentAngle + delta;

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(theView.scaleXProperty(), targetScale, Interpolator.EASE_BOTH),
                            new KeyValue(theView.scaleYProperty(), targetScale, Interpolator.EASE_BOTH),
                            new KeyValue(theView.layoutXProperty(), targetX, Interpolator.EASE_BOTH),
                            new KeyValue(theView.layoutYProperty(), targetY, Interpolator.EASE_BOTH),
                            new KeyValue(theView.rotateProperty(), shortestAngle, Interpolator.EASE_BOTH)
                    )
            );
            timeline.play();
            returnAnimation = timeline;
        } else {
            // Instant Snap
            theView.setScaleX(targetScale);
            theView.setScaleY(targetScale);
            theView.setLayoutX(targetX);
            theView.setLayoutY(targetY);
            theView.setRotate(targetAngle);
            returnAnimation = null;
        }
        return returnAnimation;
    }

    /**
     * Use the existing image of the drone and maintains a display of the drones.
     *
     * @param theDrone which is used to get and create a view for it.
     * @return the image view of the drones in the display.
     */
    private DroneShape getOrCreateView(final Drone theDrone) {
        return myDroneViews.computeIfAbsent(theDrone.getDroneID(), id -> {
            DroneShape droneShape = new DroneShape();
            // Positioning the drone  manually
            droneShape.setManaged(false);
            droneShape.setColor(Color.WHITE);

            Tooltip.install(droneShape, new Tooltip("Drone " + id));

            droneShape.setOnMouseClicked(e -> {
                e.consume();

                // logic to only choice one drone
                if (mySelectedDrone == droneShape) {
                    // Click on the same drone, then we deselect it
                    droneShape.setColor(COLOR_DEFAULT);
                    mySelectedDrone = null;

                    // Close the detail view
                    MonitorDash.getInstance().swapRightPanel(false);
                } else {
                    // If we select different drone -> switching selected drone
                    if (mySelectedDrone != null) {
                        mySelectedDrone.setColor(COLOR_DEFAULT);
                    }

                    // Setting this new drone the blue color
                    mySelectedDrone = droneShape;
                    droneShape.setColor(COLOR_SELECTED);

                    // Updating the UI Panel
                    MonitorDash.getInstance().updateStatsTextLarge(theDrone);
                    MonitorDash.getInstance().swapRightPanel(true);
                }
            });

            // Adding the Drone to the WORLD, NOT Viewport
            myWorld.getChildren().add(droneShape);
            return droneShape;
        });
    }

    /**
     * Updating the time display with the given time value.
     *
     * @param theTimeInSeconds the current elapsed time for the display.
     */
    public void updateTime(final int theTimeInSeconds) {
        Platform.runLater(() -> {
            int h = theTimeInSeconds / 3600;
            int m = (theTimeInSeconds % 3600) / 60;
            int s = theTimeInSeconds % 60;

            // Only set the numbers, don't include "TIME:"
            myTimeLabel.setText(String.format("%02d:%02d:%02d", h, m, s));

            // Flashing ONLY myTimeLabel in light gray
            FadeTransition flash = new FadeTransition(Duration.millis(150), myTimeLabel);
            flash.setFromValue(1.0);
            flash.setToValue(0.5);
            flash.setCycleCount(2);
            flash.setAutoReverse(true);
            flash.play();
        });
    }

    /**
     * Highlights a specific drone on the map (Blue).
     *
     * @param theDroneID represent the specific drone that's being selected.
     */
    public void selectDrone(int theDroneID) {
        DroneShape shape = myDroneViews.get(theDroneID);
        if (shape == null) return;

        // 1. Reset the previous selection (Green)
        if (mySelectedDrone != null) {
            mySelectedDrone.setColor(Color.WHITE);
        }

        // 2. Select the new one (Blue)
        mySelectedDrone = shape;
        mySelectedDrone.setColor(COLOR_SELECTED);
    }

    /**
     * Helper to deselect whatever is currently blue.
     */
    public void deselectAll() {
        if (mySelectedDrone != null) {
            mySelectedDrone.setColor(COLOR_DEFAULT); // Turn it White
            mySelectedDrone = null;
        }
    }

    /**
     * Dims the screen to gray when paused.
     */
    public void setPausedMode(boolean isPaused) {
        if (isPaused) {
            // The world view is gray
            myViewport.setStyle("-fx-background-color: rgba(50, 50, 50, 0.4);");
            // Making the drones look dimmed
            myWorld.setOpacity(0.5);
            // Pausing all animation
            activeTimelines.values().forEach(Timeline::pause);

        } else {
            // Reset to invisible (but clickable) background
            myViewport.setStyle("-fx-background-color: rgba(255, 0, 0, 1);");
            // Full brightness
            myWorld.setOpacity(1.0);
            // Resuming all animation
            activeTimelines.values().forEach(Timeline::play);
        }
    }



    /**
     * Getter method that returns the viewpoint pane that contains the drone.
     *
     * @return the viewport pane which shows the current view of drones
     */
    public Pane getDroneDisplay() { return myViewport; }

    /**
     * Removes all drone images from the world but KEEPS the world pane intact.
     */
    public void clearAllDrones() {
        // Clear the visual nodes from the "World" pane
        myWorld.getChildren().clear();
        // Clear the map tracking them
        myDroneViews.clear();
        // Clearing our animation timeline
        activeTimelines.clear();
    }

    void applyStylesheet(String cssName) {
        this.getStylesheets().clear();
        this.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(cssName)).toExternalForm()
        );
    }
}