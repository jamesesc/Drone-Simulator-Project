package view;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Control;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import controller.DroneMonitorApp;
import Model.AnomalyRecord;
import Model.Drone;
import Model.TelemetryData;

public class MonitorDash extends Application {
    /* ============================
    CONSTANTS - You can edit these
     ==============================*/
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
    private static final double SIZE_SCALER = 3;
    /**
     * Minimum size a drone can be in our GUI.
     */
    private static final double MIN_DRONE_SIZE = 10;
    /**
     * Maximum size a drone can be in our GUI.
     */
    private static final double MAX_DRONE_SIZE = 40;
    /**
     * How many drone stats are we going to have to display in the top-right.
     * NOTE: Constant for now, but in the future I want to be able to adjust for
     * adjustable drone counts (in case we simulate a drone crashing or something).
     */
    private final int DRONE_COUNT = 4;

    /* ===============================
    FIELDS FOR GUI ELEMENTS - Generally don't touch them
     ================================= */

    /**
     * The text area showing Drone anomalies.
     */
    private TableView<MonitorTableEntry> myAnomalyTable;
    /**
     * VBox containing the little boxes that show each drone's stats (holds myDroneBoxes).
     */
    private VBox myLiveTelemetry;
    /**
     * The text area showing Drone statistics.
     */
    private TextArea myStatsText;
    /**
     * Singleton variable for our JavaFX App instance.
     */
    private volatile static MonitorDash myInstance;
    /**
     * Where the drones will be displayed.
     */
    private Pane myDroneDisplay;
    /**
     * What each drone will look like.
     */
    private final Image myDroneImage = new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("pointer.png")));
    /**
     * A label representing how much time's gone on in the simulation.
     */
    private final Label myTimeLabel = new Label("Time: ");
    /**
     * Map of Images representing Drones (Drone ID -> ImageView), concurrent since
     * singleton stuff makes us gotta worry about multiple threads.
     */
    private final Map<Integer, ImageView> myDroneViews = new ConcurrentHashMap<>();
    /**
     * Map of all the small RegionBoxes for displaying their stats in the top-right.
     */
    private final Map<Integer, RegionBox> myDroneBoxes = new ConcurrentHashMap<>();
    /**
     * Map of all the drones currently in use by the GUI.
     */
    private final Map<Integer, Drone> myDrones = new ConcurrentHashMap<>();

    /*==============================
    FIELDS THAT ARE ACTUAL VARIABLES
     ===============================*/
    /**
     * Used by the method rightSwapPanel(), if true then show the big stats box.
     * Otherwise, show the little ones.
     */
    private boolean myShowingStats = false;

    /**
     * Whether the game is paused. True = paused, False = not paused.
     */
    private boolean myIsPaused = false;

    /* ==========================
    THE ONE RECORD WE GOT SO FAR
     ===========================*/

    /**
     * Record that makes our little stats boxes in the top-right
     * NOTE: We may want to make this a separate class at some point.
     *
     * @param theId
     * @param theHeader
     * @param theTextArea
     * @param theContainer
     */
    private record RegionBox(int theId, Label theHeader, TextArea theTextArea, VBox theContainer) {
        public RegionBox(int theID, String theTitle) {
            this(
                    theID,
                    new Label(theTitle),
                    new TextArea(),
                    new VBox()
            );

            theHeader.getStyleClass().add("box-header");

            theTextArea.setText("Battery:\nAltitude:\nPosition:");
            theTextArea.setWrapText(true);
            theTextArea.setEditable(false);
            theTextArea.setFocusTraversable(false);
            theTextArea.getStyleClass().add("dark-text-area");
            theTextArea.setFont(Font.font("Helvetica", 14));

            theContainer.setPrefHeight(120);
            theContainer.setMinHeight(Control.USE_PREF_SIZE);
            theContainer.setMaxHeight(Control.USE_PREF_SIZE);
            theContainer.getChildren().addAll(theHeader, theTextArea);

            theContainer.setOnMouseClicked(_ -> {
                Drone d = MonitorDash.getInstance().myDrones.get(theId);
                if (d != null) {
                    MonitorDash.getInstance().updateStatsTextLarge(d);
                    MonitorDash.getInstance().swapRightPanel(true);
                }
            });
        }

        void setText(String theText) {
            Platform.runLater(() -> theTextArea.setText(theText));
        }
    }

    /*======================
    INTERACTING WITH THE GUI
     =======================*/

    /**
     * Constructor of MyJavaFXApp, for Singleton stuff.
     */
    public MonitorDash() {
        myInstance = this;
    }

    /**
     * Getter for the singleton instance of the Java Application.
     *
     * @return Singleton instance of Java application.
     */
    public static MonitorDash getInstance() {
        if (myInstance == null) {
            synchronized (MonitorDash.class) {
                if (myInstance == null) {
                    myInstance = new MonitorDash();
                }
            }
        }
        return myInstance;
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

    /**
     * Refresh the drone display for multiple drones.
     *
     * @param myDrones Drones you are showing
     */
    public void refreshDroneDisplay(final Drone[] myDrones) {
            //If the array is null, do nothing
            if (myDrones == null) return;

            //Call single-drone version iteratively
            for (Drone drone : myDrones) {
                refreshDroneDisplay(drone);
            }
    }

    /**
     * Update the large stats box at the top-right of the GUI.
     *
     * @param theDrone The drone whose data we are looking at.
     */
    public void updateStatsTextLarge(final Drone theDrone) {
        if (theDrone == null || theDrone.getDroneTelemetry() == null) return;

        TelemetryData data = theDrone.getDroneTelemetry();

        String statsString = "Drone " + theDrone.getDroneID() +
                "\n==========================" +
                "\nBattery: " + theDrone.getBatteryLevel() +
                "\nAltitude: " + data.getAltitude() +
                "\nLatitude: " + data.getLatitude() +
                "\nLongitude: " + data.getLongitude() +
                "\nVelocity: " + data.getVelocity() +
                "\nOrientation: " + data.getOrientation() + "°";

        Platform.runLater(() -> myStatsText.setText(statsString));
        System.out.println("Working - MonitorDash - updateStatsTextLarge");
    }

    /**
     * Update the small stats boxes at the top right of the GUI.
     *
     * @param theDrone The drone whose data we want to display.
     */
    public void updateStatsText(final Drone theDrone) {
        //If our drone or its data is null, return nothing
        if (theDrone == null || theDrone.getDroneTelemetry() == null) return;

        //Build a string for our little stats box to use
        StringBuilder statsString = new StringBuilder();
        TelemetryData data = theDrone.getDroneTelemetry();

        statsString.append("Battery: ").append(theDrone.getBatteryLevel());
        statsString.append("\nAltitude: ").append(data.getAltitude());
        statsString.append("\nPosition: ").append(data.getLatitude())
                .append(" ").append(data.getLongitude());

        //Set the text of the stats box w/ the matching ID of the drone
        Platform.runLater(() -> {
            //Get the box first
            var box = myDroneBoxes.get(theDrone.getDroneID());

            //Check if the box actually exists before using it
            if (box != null) {
                box.setText(statsString.toString());
            } else {
                System.out.println("Warning: Drone ID " + theDrone.getDroneID() + " not found in UI map.");
            }
        });
    }

    /**
     * Update the small stats boxes at the top right of the GUI.
     *
     * @param theDrones All the drones whose data we want to display.
     */
    public void updateStatsText(final Drone[] theDrones) {
        //If the array is null do nothing
        if (theDrones == null) return;

        //For each drone, do the non-array equivalent of this function
        for (Drone drone : theDrones) {
            updateStatsText(drone);
        }
    }

    /**
     * Add an anomaly record to the table in the GUI.
     *
     * @param theRecord The anomaly record we'll be adding.
     */
    public void addAnomalyRecord(AnomalyRecord theRecord) {
        //Whether or not the ID is null, otherwise turn it into a String
        String idString = (theRecord.getID() == null) ? "—" : String.valueOf(theRecord.getID());

        //Turn the time into a string
        String timeString = Double.toString(theRecord.getTime());

        //Make a new AnomalyEntry record for our table
        MonitorTableEntry entry = new MonitorTableEntry(
                timeString,
                idString,
                theRecord.getType(),
                theRecord.getSeverity(),
                theRecord.getDetails()
        );

        Platform.runLater(() -> {
            //Add our entry and scroll to it
            myAnomalyTable.getItems().add(entry);
            myAnomalyTable.scrollTo(entry);
        });
    }

    /**
     * Add anomaly records to the table in the GUI.
     *
     * @param theRecords The anomaly records we'll be adding.
     */
    public void addAnomalyRecord(List<AnomalyRecord> theRecords) {
        if (theRecords == null || theRecords.isEmpty()) { return; }

        for (AnomalyRecord record : theRecords) {
            addAnomalyRecord(record);
        }
    }

    /**
     * Clears the anomaly table in the GUI, then replaces its contents with the given List.
     *
     * @param theRecords What we want the contents to be.
     */
    public void refreshAnomalyRecords(List<AnomalyRecord> theRecords) {
        if (theRecords == null || theRecords.isEmpty()) { return; }

        myAnomalyTable.getItems().clear();

        for (AnomalyRecord record : theRecords) {
            addAnomalyRecord(record);
        }
    }

    /**
     * Helper method for refreshDroneDisplay.
     * Moves the corresponding Drone ImageView for the drone
     *
     * @param theDrone The drone we are moving.
     */
    private void moveDroneView(Drone theDrone) {
        //Store the drone for RegionBox lookups or in case user clicks it
        myDrones.put(theDrone.getDroneID(), theDrone);
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
                    updateStatsTextLarge(theDrone);
                    swapRightPanel(true);
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
            double targetAngle = data.getOrientation();

            //Transition animation
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(droneView.fitWidthProperty(), targetSize, Interpolator.EASE_BOTH),
                            new KeyValue(droneView.fitHeightProperty(), targetSize, Interpolator.EASE_BOTH),
                            new KeyValue(droneView.layoutXProperty(), targetX, Interpolator.EASE_BOTH),
                            new KeyValue(droneView.layoutYProperty(), targetY, Interpolator.EASE_BOTH),
                            new KeyValue(droneView.rotateProperty(), targetAngle, Interpolator.EASE_BOTH)
                    )
            );
            timeline.play();

            updateStatsText(theDrone);
        });
    }

    /* =====================
    BUILDING THE APPLICATION
    ========================*/

    /**
     * Initial setup for our application.
     *
     * @param thePrimaryStage Our primary JavaFX stage.
     */
    public void start(final Stage thePrimaryStage) {
        //Main VBox that holds everything
        VBox mainBox = new VBox(10);
        mainBox.getStyleClass().add("main-box");

        //The top and bottom of the GUI
        HBox topSide = new HBox(10);
        HBox bottomSide = new HBox();
        bottomSide.setPrefHeight(150);
        bottomSide.setMinHeight(150);
        bottomSide.setMaxHeight(150);
        VBox.setVgrow(bottomSide, Priority.NEVER);
        HBox.setHgrow(bottomSide, Priority.ALWAYS);

        VBox.setVgrow(topSide, Priority.ALWAYS);
        mainBox.getChildren().addAll(topSide, bottomSide);

        //Building each section - top-left, top-right, and bottom
        VBox topLeft = buildTopLeft();
        StackPane rightSwapPane = buildTopRight();
        VBox anomalyBox = buildBottom();

        HBox.setHgrow(topLeft, Priority.ALWAYS);
        VBox.setVgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(rightSwapPane, Priority.NEVER);

        //Adding the respective sections to either the top or bottom
        topSide.getChildren().addAll(topLeft, rightSwapPane);

        bottomSide.setFillHeight(true);
        bottomSide.getChildren().add(anomalyBox);
        bottomSide.setMaxWidth(Double.MAX_VALUE);

        //Menu bar
        MenuBar menuBar = buildMenuBar(thePrimaryStage);

        //Setting up the root to hold all our stuff
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(mainBox);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("dark_theme.css")).toExternalForm()
        );

        thePrimaryStage.setTitle("Drone Simulation");
        thePrimaryStage.setScene(scene);
        thePrimaryStage.show();

        //Stuff the program runs after its build
        Platform.runLater(() -> {
            swapRightPanel(false); //Don't delete this part
        });
    }

    /**
     * Swaps between displaying the small stats boxes and the big stats box.
     *
     * @param theBigStatsBox True = show big stats box, False = show small stats boxes.
     */
    private void swapRightPanel(boolean theBigStatsBox) {
        if (theBigStatsBox == myShowingStats) return;

        myShowingStats = theBigStatsBox;

        //Hide the correct box
        if (theBigStatsBox) {
            myLiveTelemetry.setVisible(false);
            myStatsText.setVisible(true);
        } else {
            myStatsText.setVisible(false);
            myLiveTelemetry.setVisible(true);
        }
    }

    /* ====================================
    BUILDING OUT SPECIFIC SECTIONS

    (made these helpers so start() wouldn't
    be an infernal hellscape of code)
     ====================================*/

    /**
     * Helper method for start(), builds the top-left of the GUI.
     *
     * @return Returns a VBox containing the top-left of the GUI.
     */
    private VBox buildTopLeft() {
        //Top left vbox that's going to hold all our stuff
        VBox topLeft = new VBox();

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
        topLeft.getChildren().add(droneBox);

        return topLeft;
    }

    /**
     * Helper method for start(), builds the top-right of the GUI.
     *
     * @return A StackPane containing the top-right of the GUI.
     */
    private StackPane buildTopRight() {
        //Setup for the VBox that's going to hold our small drone stats boxes.
        myLiveTelemetry = new VBox();
        myLiveTelemetry.setPrefWidth(275);
        HBox.setHgrow(myLiveTelemetry, Priority.NEVER);
        myLiveTelemetry.getStyleClass().add("rounded-box");

        //Header for our main vbox
        Label header = new Label("Live Telemetry");
        header.getStyleClass().add("box-header");

        //VBox that holds each RegionBox
        VBox droneHolder = new VBox();
        droneHolder.setSpacing(10);

        //Make it so we can scroll through all our stats
        ScrollPane sPane = new ScrollPane();
        sPane.getStyleClass().add("dark-scroll-pane");
        sPane.setFitToHeight(true);
        sPane.setFitToWidth(true);
        sPane.setContent(droneHolder);
        VBox.setVgrow(sPane, Priority.ALWAYS);

        //Add a region box based on the number of drone boxes
        for (int i = 1; i <= DRONE_COUNT; i++) {
            RegionBox rb = new RegionBox(i, "Drone " + i);
            myDroneBoxes.put(i, rb);
            droneHolder.getChildren().add(rb.theContainer());
        }

        //Add children to our main top-right vbox
        myLiveTelemetry.getChildren().addAll(header, sPane);

        //Adjusting the big stats box
        myStatsText = new TextArea();
        myStatsText.setEditable(false);
        myStatsText.setWrapText(true);
        myStatsText.setVisible(false);
        myStatsText.getStyleClass().add("dark-text-area");
        myStatsText.setFont(Font.font("Helvetica", 14));
        myStatsText.setOnMouseClicked(_ -> swapRightPanel(false));

        //The pane that allows us to swap between big stats box and small stats boxes
        StackPane rightSwapPane = new StackPane();
        rightSwapPane.getChildren().addAll(myLiveTelemetry, myStatsText);

        //Makes it so the large stats box doesn't take up more width than it needs (bug fix)
        myStatsText.prefWidthProperty().bind(myLiveTelemetry.prefWidthProperty());

        return rightSwapPane;
    }

    /**
     * A helper method for start(), builds the bottom half of the GUI.
     *
     * @return A VBox containing the bottom half of the GUI.
     */
    private VBox buildBottom() {
        //The table that'll show us all our AnomalyRecords
        myAnomalyTable = new TableView<>();
        myAnomalyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        VBox.setVgrow(myAnomalyTable, Priority.ALWAYS);

        //Setting up each column of the Anomaly Table
        TableColumn<MonitorTableEntry, String> col1 = new TableColumn<>("Timestamp");
        TableColumn<MonitorTableEntry, String> col2 = new TableColumn<>("Drone ID");
        TableColumn<MonitorTableEntry, String> col3 = new TableColumn<>("Type");
        TableColumn<MonitorTableEntry, String> col4 = new TableColumn<>("Severity");
        TableColumn<MonitorTableEntry, String> col5 = new TableColumn<>("Details");

        col1.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        col2.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        col3.setCellValueFactory(new PropertyValueFactory<>("type"));
        col4.setCellValueFactory(new PropertyValueFactory<>("severity"));
        col5.setCellValueFactory(new PropertyValueFactory<>("details"));

        myAnomalyTable.getColumns().addAll(List.of(col1, col2, col3, col4, col5));
        myAnomalyTable.getStyleClass().add("dark-table");

        //Box that'll be holding everything at the bottom
        VBox anomalyBox = new VBox();
        anomalyBox.getStyleClass().add("rounded-box");
        VBox.setVgrow(anomalyBox, Priority.ALWAYS);

        //Header to make the bottom section look nice :)
        Label anomalyHeader = new Label("Anomaly Log");
        anomalyHeader.getStyleClass().add("box-header");

        //Size stuff
        VBox.setVgrow(myAnomalyTable, Priority.ALWAYS);
        anomalyBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(anomalyBox, Priority.ALWAYS);

        //Giving the main box for the bottom its children
        anomalyBox.getChildren().addAll(anomalyHeader, myAnomalyTable);

        return anomalyBox;
    }

    /**
     * Setup for the GUI's menu bar.
     *
     * @param thePrimaryStage The primary stage of the JavaFX GUI.
     * @return The GUI's menu bar.
     */
    private MenuBar buildMenuBar(final Stage thePrimaryStage) {
        MenuBar menuBar = new MenuBar();

        //Creating Menu buttons
        Menu exitMenu = new Menu("Exit");

        Menu startMenu = new Menu("");
        Label startLabel = new Label("Start");
        startMenu.setGraphic(startLabel);

        Menu pauseMenu = new Menu("");
        Label pauseLabel = new Label("Pause");
        pauseMenu.setGraphic(pauseLabel);

        Menu stopMenu = new Menu("");
        Label stopLabel = new Label("Stop");
        stopMenu.setDisable(true);
        stopMenu.setGraphic(stopLabel);

        //Functionality to buttons
        exitMenu.setOnAction(_ -> thePrimaryStage.close());

        pauseLabel.setOnMouseClicked(_ -> togglePauseGame());

        startLabel.setOnMouseClicked(_ -> {
            startGame();
            startMenu.setDisable(true);
            stopMenu.setDisable(false);
        });

        stopLabel.setOnMouseClicked(_ -> {
            endGame();
            startMenu.setDisable(false);
            stopMenu.setDisable(true);
        });

        //Adding menus to the MenuBar
        menuBar.getMenus().addAll(exitMenu, startMenu, pauseMenu, stopMenu);

        return menuBar;
    }

    /**
     * Tell the controller DroneMonitorApp to end the game.
     */
    private void endGame() {
        DroneMonitorApp.getInstance().stopSim();
        System.out.println("MonitorDash: stopped game");
    }

    /**
     * Tell the controller DroneMonitorApp to toggle pausing.
     */
    private void togglePauseGame() {
        System.out.println("MonitorDash: toggled pause");
        if (myIsPaused) {
            DroneMonitorApp.getInstance().pauseSim();
        } else {
            DroneMonitorApp.getInstance().continueSim();
        }

        myIsPaused = !myIsPaused;
    }

    /**
     * Tell the controller DroneMonitorApp to start the game.
     */
    private void startGame() {
        DroneMonitorApp.getInstance().startSim();
        System.out.print("MonitorDash: Started Game");
    }

    //Start the application
    public static void main(final String[] theArgs) {
        launch(theArgs);
    }
}