package view;

import Model.Drone;
import Model.TelemetryData;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TopLeftDroneDisplay extends VBox {
    /**
     * Minimum bound for longitude.
     */
    private static final double MIN_LONGITUDE = -250;
    /**
     * Maximum bound for longitude.
     */
    private static final double MAX_LONGITUDE = 250;
    /**
     * Minimum bound for latitude.
     */
    private static final double MIN_LATITUDE = -250;
    /**
     * Maximum bound for latitude.
     */
    private static final double MAX_LATITUDE = 250;
    /**
     * How much we're multiplying the drone size by, in case
     * we want to make it bigger or smaller.
     */
    private static final double SIZE_SCALER = 1.2;
    /**
     * Minimum size a drone can be in our GUI.
     */
    private static final double MIN_DRONE_SIZE = 10;
    /**
     * Maximum size a drone can be in our GUI.
     */
    private static final double MAX_DRONE_SIZE = 50;

    /**
     * Where the drones will be displayed.
     */
    private final Pane myDroneDisplay;
    /**
     * What each drone will look like.
     */
    private final Image myDroneImage = new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("Assets/pointer.png")));
    /**
     * A label representing how much time's gone on in the simulation.
     */
    private final Label myTimeLabel = new Label("Time: ");
    /**
     * Map of Images representing Drones (Drone ID -> ImageView), concurrent since
     * singleton stuff makes us gotta worry about multiple threads.
     */
    private final Map<Integer, ImageView> myDroneViews = new ConcurrentHashMap<>();

    TopLeftDroneDisplay() {
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);

        //Making our drone display
        myDroneDisplay = new Pane();
        myDroneDisplay.getStyleClass().add("drone-display");

        //Clip to make sure that the drones in drone
        //display aren't displayed over its rounded edges
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        clip.widthProperty().bind(myDroneDisplay.widthProperty());
        clip.heightProperty().bind(myDroneDisplay.heightProperty());
        myDroneDisplay.setClip(clip);

        //The box holding our drone display and time label (so it looks nice :))
        VBox droneBox = new VBox();
        droneBox.getStyleClass().add("rounded-box");
        VBox.setVgrow(droneBox, Priority.ALWAYS);
        VBox.setVgrow(myDroneDisplay, Priority.ALWAYS);
        myDroneDisplay.setMinHeight(0);

        //Making the time label stylish
        myTimeLabel.getStyleClass().add("box-header");

        //Adding children to their respective parents
        droneBox.getChildren().addAll(myTimeLabel, myDroneDisplay);
        getChildren().add(droneBox);
    }

    /**
     * Helper method for refreshDroneDisplay.
     * Moves the corresponding Drone ImageView for the drone
     *
     * @param theDrone The drone we are moving.
     */
    private void moveDroneView(Drone theDrone) {
        //Store the drone for RegionBox lookups or in case user clicks it
        MonitorDash.getInstance().myDrones.put(theDrone.getDroneID(), theDrone);
        TelemetryData data = theDrone.getDroneTelemetry();

        Platform.runLater(() -> {
            double displayWidth = myDroneDisplay.getWidth();
            double displayHeight = myDroneDisplay.getHeight();

            //Create or retrieve the ImageView
            ImageView droneView = myDroneViews.computeIfAbsent(theDrone.getDroneID(), id -> {
                ImageView view = new ImageView(myDroneImage);
                view.setPreserveRatio(true);
                Tooltip.install(view, new Tooltip("Drone " + id));

                //Clicking opens large stats
                view.setOnMouseClicked(_ -> {
                    MonitorDash.getInstance().updateStatsTextLarge(theDrone);
                    MonitorDash.getInstance().swapRightPanel(true);
                });

                //Add the image view to where the drones will be displayed
                myDroneDisplay.getChildren().add(view);

                //Initial placement/size/rotation/orientation
                double initSize = Math.min(Math.max(MIN_DRONE_SIZE, data.getAltitude() * SIZE_SCALER), MAX_DRONE_SIZE);
                view.setLayoutX(((data.getLongitude() - MIN_LONGITUDE) / (MAX_LONGITUDE - MIN_LONGITUDE))
                        * displayWidth - initSize / 2);
                view.setLayoutY(((data.getLatitude() - MIN_LATITUDE) / (MAX_LATITUDE - MIN_LATITUDE))
                        * displayHeight - initSize / 2);
                view.setFitWidth(initSize);
                view.setFitHeight(initSize);
                view.setRotate(data.getOrientation());
                return view;
            });

            //Target animation values
            double targetSize = Math.min(Math.max(MIN_DRONE_SIZE, data.getAltitude() * SIZE_SCALER), MAX_DRONE_SIZE);
            double targetX = ((data.getLongitude() - MIN_LONGITUDE) / (MAX_LONGITUDE - MIN_LONGITUDE))
                    * displayWidth - targetSize / 2;
            double targetY = ((data.getLatitude() - MIN_LATITUDE) / (MAX_LATITUDE - MIN_LATITUDE))
                    * displayHeight - targetSize / 2;

            //Transition animation
            double currentAngle = droneView.getRotate();
            double targetAngle = data.getOrientation();

            // Compute the shortest path rotation
            double delta = ((targetAngle - currentAngle + 540) % 360) - 180;
            double shortestAngle = currentAngle + delta;

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(droneView.fitWidthProperty(), targetSize, Interpolator.EASE_BOTH),
                            new KeyValue(droneView.fitHeightProperty(), targetSize, Interpolator.EASE_BOTH),
                            new KeyValue(droneView.layoutXProperty(), targetX, Interpolator.EASE_BOTH),
                            new KeyValue(droneView.layoutYProperty(), targetY, Interpolator.EASE_BOTH),
                            new KeyValue(droneView.rotateProperty(), shortestAngle, Interpolator.EASE_BOTH)
                    )
            );
            timeline.play();

            MonitorDash.getInstance().updateStatsText(theDrone);
        });
    }

    /**
     * Updates the time label with the specified time.
     *
     * @param theTime The current time.
     */
    public void updateTime(final int theTime) {
        // Will update to the UI thread safely
        Platform.runLater(() -> myTimeLabel.setText("Time: " + theTime));
    }

    /**
     * Refresh the drone display for a single drone.
     *
     * @param drone The drone to show
     */
    public void refreshDroneDisplay(final Drone drone) {
        //If a drone or its contents are null, do nothing
        if (drone == null || drone.getDroneTelemetry() == null) {
            return;
        }

        moveDroneView(drone);
    }

    public Pane getDroneDisplay() { return myDroneDisplay; }
}
