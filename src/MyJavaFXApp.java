import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Tooltip;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import Model.Drone;
import Model.TelemetryData;

public class MyJavaFXApp extends Application {
    /**
     * The text area showing Drone anomalies.
     */
    private TextArea myAnomalyText;
    /**
     * The text area showing Drone statistics.
     */
    private TextArea myStatsText;
    /**
     * Singleton variable for our JavaFX App instance.
     */
    private volatile static MyJavaFXApp myInstance;
    /**
     * Where the drones will be displayed.
     */
    private Pane myDroneDisplay;
    /**
     * What each drone will look like.
     */
    private final Image myDroneImage = new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("drone_image.png")));
    /**
     * Map of Images representing Drones (Drone ID -> ImageView), concurrent since
     * singleton stuff makes us gotta worry about multiple threads.
     */
    private final Map<Integer, ImageView> myDroneViews = new ConcurrentHashMap<>();
    /**
     * Minimum bound for longitude.
     */
    private static final double MIN_LONGITUDE = -1000;
    /**
     * Maximum bound for longitude.
     */
    private static final double MAX_LONGITUDE = 1000;
    /**
     * Minimum bound for latitude.
     */
    private static final double MIN_LATITUDE = -1000;
    /**
     * Maximum bound for latitude.
     */
    private static final double MAX_LATITUDE = 1000;
    /**
     * How much we're multiplying the drone size by, in case
     * we wanna make it bigger or smaller.
     */
    private static final double SIZE_SCALER = 20;

    /**
     * Constructor of MyJavaFXApp, for Singleton stuff.
     */
    public MyJavaFXApp() { myInstance = this; }

    /**
     * Getter for the singleton instance of the Java Application.
     *
     * @return Singleton instance of Java application.
     */
    public static MyJavaFXApp getInstance() {
        if (myInstance == null) {
            synchronized (MyJavaFXApp.class) {
                if (myInstance == null) {
                    myInstance = new MyJavaFXApp();
                }
            }
        }
        return myInstance;
    }

    /**
     * Refresh the drone display with the drones you want shown.
     *
     * @param drones Drones you are showing
     */
    public void refreshDroneDisplay(Drone[] drones) {
        Platform.runLater(() -> {
            //Remove images that don't exist anymore (just in case)
            Set<Integer> activeIds = new HashSet<>();
            for (Drone drone : drones) {
                activeIds.add(drone.getDroneID());
            }
            myDroneViews.keySet().removeIf(id -> {
                if (!activeIds.contains(id)) {
                    myDroneDisplay.getChildren().remove(myDroneViews.get(id));
                    return true;
                }
                return false;
            });

            //Getting width and height of drone display for size and location shit
            double displayWidth = myDroneDisplay.getWidth();
            double displayHeight = myDroneDisplay.getHeight();

            //Iterate through all the drones
            for (Drone drone : drones) {
                TelemetryData data = drone.getMyDroneTelemetryData();
                if (data == null) continue;

                //If a drone with the given ID isn't in our map, we make one with it
                ImageView droneView = myDroneViews.computeIfAbsent(drone.getDroneID(), id -> {
                    ImageView view = new ImageView(myDroneImage);
                    view.setPreserveRatio(true);
                    Tooltip.install(view, new Tooltip("Drone " + id));
                    view.setOnMouseClicked(_ -> updateStatsText(drone));
                    myDroneDisplay.getChildren().add(view);

                    //Our starting values for location, size, rotation
                    view.setLayoutX(((data.getLongitude() - MIN_LONGITUDE) / (MAX_LONGITUDE - MIN_LONGITUDE))
                            * displayWidth - Math.max(10, data.getAltitude() * SIZE_SCALER) / 2);
                    view.setLayoutY(((data.getLatitude() - MIN_LATITUDE) / (MAX_LATITUDE - MIN_LATITUDE))
                            * displayHeight - Math.max(10, data.getAltitude() * SIZE_SCALER) / 2);
                    view.setFitWidth(Math.max(10, data.getAltitude() * SIZE_SCALER));
                    view.setFitHeight(Math.max(10, data.getAltitude() * SIZE_SCALER));
                    view.setScaleX(1);
                    view.setScaleY(1);
                    view.setRotate(data.getOrientation());

                    return view;
                });

                //Where we want the drone to end up (size, location, angle)
                double targetSize = Math.max(10, data.getAltitude() * SIZE_SCALER);
                double targetX = ((data.getLongitude() - MIN_LONGITUDE) / (MAX_LONGITUDE - MIN_LONGITUDE))
                        * displayWidth - targetSize / 2;
                double targetY = ((data.getLatitude() - MIN_LATITUDE) / (MAX_LATITUDE - MIN_LATITUDE))
                        * displayHeight - targetSize / 2;
                double targetAngle = data.getOrientation();

                //Interpolating ourselves to our target shit
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
            }
        });
    }

    /**
     * Adds another anomaly record to the anomalies text field.
     *
     * @param theRecord What record you want info from to add to the field.
     */
    public void addAnomalyText(AnomalyRecord theRecord) {
        Platform.runLater(() -> {
            String add = "Time: " + theRecord.getTime() +
                    "\nMethod check failed: " + theRecord.getMethod() +
                    "\nID for drone (null if multiple): " + theRecord.getID() +
                    "\n=======================\n";
            myAnomalyText.appendText(add);
        });
    }

    /**
     * What drone you are showing the stats for.
     *
     * @param theDrone The drone whose stats you want to display.
     */
    public void updateStatsText(Drone theDrone) {
        Platform.runLater(() -> {
            TelemetryData data = theDrone.getDroneTelemetry();
            String replace = "ID: " + theDrone.getDroneID() +
                    "\nBattery: " + theDrone.getBatteryLevel() +
                    "\nLongitude: " + data.getLongitude() +
                    "\nLatitude: " + data.getLatitude() +
                    "\nAltitude: " + data.getAltitude() +
                    "\nVelocity: " + data.getVelocity() +
                    "\nOrientation: " + data.getOrientation();
            myStatsText.setText(replace);
        });
    }

    /**
     * Initial setup for our application.
     *
     * @param thePrimaryStage Our primary JavaFX stage.
     */
    @Override
    public void start(Stage thePrimaryStage) {
        // Editing text for anomalies and stats
        myAnomalyText = new TextArea();
        myAnomalyText.setWrapText(true);
        myAnomalyText.setEditable(false);
        myAnomalyText.getStyleClass().add("dark-text-area");
        myAnomalyText.setFont(Font.font("Helvetica", 14));

        myStatsText = new TextArea();
        myStatsText.setWrapText(true);
        myStatsText.setEditable(false);
        myStatsText.getStyleClass().add("dark-text-area");
        myStatsText.setFont(Font.font("Helvetica", 14));

        // Main content HBox
        HBox mainBox = new HBox(10);
        mainBox.getStyleClass().add("main-box");

        // Drone display
        myDroneDisplay = new Pane();
        myDroneDisplay.getStyleClass().add("drone-display");
        HBox.setHgrow(myDroneDisplay, Priority.ALWAYS);

        //Make sure drone display's children is hid behind it
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.setArcWidth(12);  // match CSS corner radius
        clip.setArcHeight(12);
        clip.widthProperty().bind(myDroneDisplay.widthProperty());
        clip.heightProperty().bind(myDroneDisplay.heightProperty());
        myDroneDisplay.setClip(clip);

        // Right side container
        VBox rightSide = new VBox(10);
        rightSide.setPrefWidth(275);

        VBox anomalyBox = new VBox();
        anomalyBox.getStyleClass().add("rounded-box");
        VBoxSetup(anomalyBox, "Anomaly Reports", myAnomalyText);

        VBox statsBox = new VBox();
        statsBox.setPrefHeight(200);
        statsBox.setMinHeight(Control.USE_PREF_SIZE);
        statsBox.setMaxHeight(Control.USE_PREF_SIZE);
        statsBox.getStyleClass().add("rounded-box");
        VBoxSetup(statsBox, "Drone Statistics", myStatsText);

        rightSide.getChildren().addAll(anomalyBox, statsBox);
        mainBox.getChildren().addAll(myDroneDisplay, rightSide);

        // MenuBar setup
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem clearItem = new MenuItem("Clear");
        MenuItem closeItem = new MenuItem("Close Sim.");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(_ -> thePrimaryStage.close());

        Menu pauseMenu = new Menu("Pause");
        /*
        doActionMenu.setOnShowing(e -> {
            doSomething();
        });
         */
        Menu endMenu = new Menu("End");

        fileMenu.getItems().addAll(clearItem, closeItem, exitItem);
        menuBar.getMenus().addAll(fileMenu, pauseMenu, endMenu);

        // Wrap shit in a BorderPane
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(mainBox);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("Odark_theme.css")).toExternalForm()
        );
        thePrimaryStage.setTitle("Drone Simulation");
        thePrimaryStage.setScene(scene);
        thePrimaryStage.show();

        //Examples for right hand side
        Platform.runLater(() -> {
            addAnomalyText(new AnomalyRecord("Test1", 0, 0.0));
            addAnomalyText(new AnomalyRecord("Test2", 1, 1.0));
            addAnomalyText(new AnomalyRecord("Test3", 2, 2.0));
            addAnomalyText(new AnomalyRecord("Test4", 2.0));
            addAnomalyText(new AnomalyRecord("Test4", 2.0));
            addAnomalyText(new AnomalyRecord("Test4", 2.0));
            addAnomalyText(new AnomalyRecord("Test4", 2.0));
            TelemetryData data = new TelemetryData();
            data.setLatitude(0.0); data.setLongitude(0.0); data.setAltitude(0.0);
            data.setVelocity(4.0); data.setOrientation(90.0);
            Drone drone1 = new Drone(data);

            Drone[] drones = {drone1};
            refreshDroneDisplay(drones);

            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(_ -> {
                data.setLatitude(500.0); data.setLongitude(100.0); data.setAltitude(1.0);
                data.setVelocity(4.0); data.setOrientation(180.0);
                drone1.updateTelemetryData(data);
                refreshDroneDisplay(drones);
            });
            pause.play();
        });
    }

    /**
     * Setup for some of our VBoxes, method used to reduce duplicate code.
     *
     * @param theVBox The vbox you're setting up.
     * @param theTextArea The text you're using in the setup.
     */
    private void VBoxSetup(VBox theVBox, String theHeaderText, TextArea theTextArea) {
        VBox.setVgrow(theVBox, Priority.ALWAYS);

        Label header = new Label(theHeaderText);
        header.getStyleClass().add("box-header");

        ScrollPane sPane = new ScrollPane();
        sPane.getStyleClass().add("dark-scroll-pane");

        sPane.setContent(theTextArea);
        sPane.setFitToHeight(true);
        sPane.setFitToWidth(true);
        VBox.setVgrow(sPane, Priority.ALWAYS);

        theVBox.getChildren().addAll(header, sPane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}