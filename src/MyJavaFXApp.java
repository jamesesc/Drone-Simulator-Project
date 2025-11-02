import Model.Drone;
import Model.TelemetryData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MyJavaFXApp extends Application {
    private TextArea anomalyText;
    private TextArea statsText;
    private volatile static MyJavaFXApp instance;

    /**
     * Constructor of MyJavaFXApp, for Singleton stuff.
     */
    private MyJavaFXApp() {}

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
            String add = "\nTime: " + theRecord.getTime() +
                    "\nMethod check failed: " + theRecord.getMethod() +
                    "\nID for drone (null if multiple): " + theRecord.getID() + "\n";
            anomalyText.appendText(add);
        });
    }

    /**
     * What drone you are showing the stats for.
     *
     * @param theDrone The drone whose stats you want to display.
     */
    public void updateStatsText(Drone theDrone) {
        Platform.runLater(() -> {
            TelemetryData data = theDrone.getMyDroneTelemetryData();
            String replace = "Drone Statistics:" +
                    "\nID: " + theDrone.getDroneID() +
                    "\nBattery: " + theDrone.getBatteryLevel() +
                    "\nLongitude: " + data.getLongitude() +
                    "\nLatitude: " + data.getLatitude() +
                    "\nAltitude: " + data.getAltitude() +
                    "\nVelocity: " + data.getVelocity() +
                    "\nOrientation: " + data.getOrientation();
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
        anomalyText = new TextArea("Anomalies detected: ");
        anomalyText.setWrapText(true);
        anomalyText.setEditable(false);

        statsText = new TextArea("Drone Statistics: ");
        statsText.setWrapText(true);
        statsText.setEditable(false);

        //Main container
        HBox mainBox = new HBox(10);

        //Drone display
        Region droneDisplay = new Region();
        droneDisplay.setStyle("-fx-background-color: red;");
        HBox.setHgrow(droneDisplay, Priority.ALWAYS);

        //Container for right 2 regions
        VBox rightSide = new VBox(10);
        rightSide.setPrefWidth(275);

        //Regions for right hand side
        VBox anomalyBox = new VBox();
        anomalyBox.setStyle("-fx-background-color: white;");
        BorderStroke anomalyBorderStroke = new BorderStroke(
                Color.RED, BorderStrokeStyle.SOLID,
                new CornerRadii(0), new BorderWidths(2));
        Border anomalyBorder = new Border(anomalyBorderStroke);
        anomalyBox.setBorder(anomalyBorder);
        anomalyBox.setPrefHeight(450);
        VBoxSetup(anomalyBox, anomalyText);

        VBox statsBox = new VBox();
        statsBox.setStyle("-fx-background-color: white;");
        BorderStroke statsBorderStroke = new BorderStroke(
                Color.BLUE, BorderStrokeStyle.SOLID,
                new CornerRadii(0), new BorderWidths(2));
        Border statsBorder = new Border(statsBorderStroke);
        statsBox.setBorder(statsBorder);
        VBoxSetup(statsBox, statsText);

        //Adding children to their parents
        rightSide.getChildren().addAll(anomalyBox, statsBox);
        mainBox.getChildren().addAll(droneDisplay, rightSide);

        //Making a scene and configuring it
        Scene scene = new Scene(mainBox, 800, 600);
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
            data.setLatitude(1.0);
            data.setLongitude(2.0);
            data.setAltitude(3.0);
            data.setVelocity(4.0);
            data.setOrientation(5.0);
            Drone drone = new Drone(data);
            updateStatsText(drone);
        });
    }

    /**
     * Setup for some of our VBoxes, method used to reduce duplicate code.
     *
     * @param anomalyBox The vbox you're setting up.
     * @param anomalyText The text you're using in the setup.
     */
    private void VBoxSetup(VBox anomalyBox, TextArea anomalyText) {
        VBox.setVgrow(anomalyBox, Priority.ALWAYS);

        ScrollPane anomalyScrollPane = new ScrollPane();
        anomalyScrollPane.setContent(anomalyText);
        anomalyScrollPane.setFitToHeight(true);
        anomalyScrollPane.setFitToWidth(true);
        VBox.setVgrow(anomalyScrollPane, Priority.ALWAYS);

        anomalyBox.getChildren().addAll(anomalyScrollPane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}