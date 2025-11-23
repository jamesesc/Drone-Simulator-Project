package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import controller.*;
import controller.DroneMonitorApp;
import Model.AnomalyRecord;
import Model.Drone;

/**
 * A Singleton class which houses our Graphical User Interface for the application.
 */
public class MonitorDash extends Application {
   /* ===============================
    Class Objects for the back end
     ================================= */

    /**
     * Represent the Controller for the simulation.
     */
    private DroneMonitorApp myController;

    /* ===============================
    FIELDS FOR GUI ELEMENTS - Generally don't touch them
     ================================= */

    /**
     * Singleton variable for our JavaFX App instance.
     */
    private volatile static MonitorDash myInstance;
    /**
     * Map of all the drones currently in use by the GUI.
     */
    final Map<Integer, Drone> myDrones = new ConcurrentHashMap<>();

    /*==============================
    FIELDS THAT ARE ACTUAL VARIABLES
     ===============================*/

    /**
     * Whether the game is paused. True = paused, False = not paused.
     */
    private boolean myIsPaused = false;

    /* =============
    SECTIONS OF THE GUI
     ===============*/
    TopLeftDroneDisplay myTopLeft = new TopLeftDroneDisplay();
    BottomTable myBottomSide = new BottomTable();
    TopRightStats myTopRight = new TopRightStats();

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
        myTopLeft.updateTime(theTime);
    }

    /**
     * Refresh the drone display for a single drone.
     *
     * @param drone The drone to show
     */
    public void refreshDroneDisplay(final Drone drone) {
        myTopLeft.refreshDroneDisplay(drone);
    }

    /**
     * Refresh the drone display for multiple drones.
     *
     * @param theDrones Drones you are showing
     */
    public void refreshDroneDisplay(final Drone[] theDrones) {
            //If the array is null, do nothing
            if (theDrones == null) return;

            //Call single-drone version iteratively
            for (Drone drone : theDrones) {
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

        myTopRight.updateStatsTextLarge(theDrone);
    }

    /**
     * Update the small stats boxes at the top right of the GUI.
     *
     * @param theDrone The drone whose data we want to display.
     */
    public void updateStatsText(final Drone theDrone) {
        myTopRight.updateStatsText(theDrone);
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
        myBottomSide.addAnomalyRecord(theRecord);
    }

    /**
     * Add anomaly records to the table in the GUI.
     *
     * @param theRecords The anomaly records we'll be adding.
     */
    public void addAnomalyRecord(List<AnomalyRecord> theRecords) {
        if (theRecords == null || theRecords.isEmpty()) { return; }

        for (AnomalyRecord record : theRecords) {
            myBottomSide.addAnomalyRecord(record);
        }
    }

    /**
     * Clears the anomaly table in the GUI, then replaces its contents with the given List.
     *
     * @param theRecords What we want the contents to be.
     */
    public void refreshAnomalyRecords(List<AnomalyRecord> theRecords) {
        if (theRecords == null || theRecords.isEmpty()) { return; }

        myBottomSide.getAnomalyTable().getItems().clear();

        myBottomSide.refreshAnomalyRecords(theRecords);
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

        VBox.setVgrow(topSide, Priority.ALWAYS);
        mainBox.getChildren().addAll(topSide, myBottomSide);

        HBox.setHgrow(myTopRight, Priority.NEVER);

        //Adding the respective sections to either the top or bottom
        topSide.getChildren().addAll(myTopLeft, myTopRight);

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

        //BACK END
        TimerManager timerManager = new TimerManager();
        DroneFleetManager fleetManager = new DroneFleetManager();

        UpdateUIManager updaterManager = new UpdateUIManager(this, fleetManager);

        SimulationScheduler scheduler = new SimulationScheduler(
                timerManager,
                fleetManager,
                updaterManager
        );

        myController = new DroneMonitorApp(timerManager, scheduler);

        //Stuff the program runs after its build
        Platform.runLater(() -> {
            swapRightPanel(false); //Don't delete this part

            AnomalyRecord record = new AnomalyRecord("dn", 1, 2.0, "dn", "dn");
            addAnomalyRecord(record);
        });
    }

    /**
     * Swaps between displaying the small stats boxes and the big stats box.
     *
     * @param theBigStatsBox True = show big stats box, False = show small stats boxes.
     */
    void swapRightPanel(boolean theBigStatsBox) {
        if (theBigStatsBox != myTopRight.getShowingStats()) {
            myTopRight.swapRightPanel(theBigStatsBox);
        }
    }

    /* ====================================
    MENU BAR
     ====================================*/

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
        pauseMenu.setDisable(true);
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
            pauseMenu.setDisable(false);
        });

        stopLabel.setOnMouseClicked(_ -> {
            endGame();
            startMenu.setDisable(true);
            stopMenu.setDisable(true);
            pauseMenu.setDisable(true);
        });

        //Adding menus to the MenuBar
        menuBar.getMenus().addAll(exitMenu, startMenu, pauseMenu, stopMenu);

        return menuBar;
    }

    /**
     * Tell the controller DroneMonitorApp to end the game.
     */
    private void endGame() {
        myController.stopSim();
        myTopLeft.getDroneDisplay().setStyle("-fx-background-color: grey;");
        System.out.println("MonitorDash: stopped game");
    }

    /**
     * Tell the controller DroneMonitorApp to toggle pausing.
     */
    private void togglePauseGame() {
        System.out.println("MonitorDash: toggled pause");
        if (myIsPaused) {
            myController.pauseSim();
            myTopLeft.getDroneDisplay().setStyle("-fx-background-color: grey;");
        } else {
            myController.continueSim();
            myTopLeft.getDroneDisplay().setStyle("-fx-background-color: red;");
        }

        myIsPaused = !myIsPaused;
    }

    /**
     * Tell the controller DroneMonitorApp to start the game.
     */
    private void startGame() {
        myController.startSim();
        System.out.print("MonitorDash: Started Game");
    }

    //Start the application
    public static void main(final String[] theArgs) {
        launch(theArgs);
    }
}