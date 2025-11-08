import Model.Drone;
import Model.TelemetryData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class MyJavaFXApp extends Application {
    private TextArea anomalyText;
    private TextArea statsText;
    private volatile static MyJavaFXApp instance;

    /**
     * Constructor of MyJavaFXApp, for Singleton stuff.
     */
    public MyJavaFXApp() { instance = this; }

    /**
     * Getter for the singleton instance of the Java Application.
     *
     * @return Singleton instance of Java application.
     */
    public static MyJavaFXApp getInstance() {
        if (instance == null) {
            synchronized (MyJavaFXApp.class) {
                if (instance == null) {
                    instance = new MyJavaFXApp();
                }
            }
        }
        return instance;
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
            anomalyText.appendText(add);
        });
    }

    /**
     * What drone you are showing the stats for.
     *
     * @param theDrone The drone whose stats you want to display.
     * @param theTimer The time duration of the simulator
     */
    public void updateStatsText(Drone theDrone, int theTimer) {
        Platform.runLater(() -> {
            TelemetryData data = theDrone.getDroneTelemetry();
            String replace = "ID: " + theDrone.getDroneID() +
                    "\nBattery: " + theDrone.getBatteryLevel() +
                    "\nLongitude: " + data.getLongitude() +
                    "\nLatitude: " + data.getLatitude() +
                    "\nAltitude: " + data.getAltitude() +
                    "\nVelocity: " + data.getVelocity() +
                    "\nOrientation: " + data.getOrientation()+
                    "\nTime: " + theTimer; // Added this timer to see if it works
            statsText.setText(replace);
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
        anomalyText = new TextArea();
        anomalyText.setWrapText(true);
        anomalyText.setEditable(false);
        anomalyText.getStyleClass().add("dark-text-area");
        anomalyText.setFont(Font.font("Helvetica", 14));

        statsText = new TextArea();
        statsText.setWrapText(true);
        statsText.setEditable(false);
        statsText.getStyleClass().add("dark-text-area");
        statsText.setFont(Font.font("Helvetica", 14));

        // Main content HBox
        HBox mainBox = new HBox(10);
        mainBox.getStyleClass().add("main-box");

        // Drone display
        Region droneDisplay = new Region();
        droneDisplay.getStyleClass().add("drone-display");
        HBox.setHgrow(droneDisplay, Priority.ALWAYS);

        // Right side container
        VBox rightSide = new VBox(10);
        rightSide.setPrefWidth(275);

        VBox anomalyBox = new VBox();
        anomalyBox.getStyleClass().add("rounded-box");
        VBoxSetup(anomalyBox, "Anomaly Reports", anomalyText);

        VBox statsBox = new VBox();
        statsBox.setPrefHeight(200);
        statsBox.setMinHeight(Control.USE_PREF_SIZE);
        statsBox.setMaxHeight(Control.USE_PREF_SIZE);
        statsBox.getStyleClass().add("rounded-box");
        VBoxSetup(statsBox, "Drone Statistics", statsText);

        rightSide.getChildren().addAll(anomalyBox, statsBox);
        mainBox.getChildren().addAll(droneDisplay, rightSide);

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

        // TODO: This is where the monitor is being used and handling the update
        DroneMonitorApp.testSimContinuous();

        //Examples for right hand side
        Platform.runLater(() -> {
            addAnomalyText(new AnomalyRecord("Test1", 0, 0.0));
            addAnomalyText(new AnomalyRecord("Test2", 1, 1.0));
            addAnomalyText(new AnomalyRecord("Test3", 2, 2.0));
            addAnomalyText(new AnomalyRecord("Test4", 2.0));
            addAnomalyText(new AnomalyRecord("Test4", 2.0));
            addAnomalyText(new AnomalyRecord("Test4", 2.0));
            addAnomalyText(new AnomalyRecord("Test4", 2.0));
//            TelemetryData data = new TelemetryData();
//            data.setLatitude(1.0); data.setLongitude(2.0); data.setAltitude(3.0);
//            data.setVelocity(4.0); data.setOrientation(5.0);
//            Drone drone = new Drone(data);
//            updateStatsText(drone);
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