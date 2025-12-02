package view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import controller.DroneMonitorApp;
import Model.AnomalyRecord;
import Model.Drone;

public class MonitorDash  {
    /* =============
    DEPENDENCIES
     ===============*/

    /** Represents the Controller that the UI talks to */
    private final DroneMonitorApp myController;

    /** Represents the Sound Manager for the UI */
    private final SoundManager mySoundManager;


    /* =============
    STATE
     ===============*/

    /** Map of all the drones currently in use by the GUI. */
    final Map<Integer, Drone> myDrones = new ConcurrentHashMap<>();

    /** Whether the game is paused. True = paused, False = not paused. */
    private boolean myIsPaused = false;


    /* =============
    SECTIONS OF THE GUI
     ===============*/

    /** Represent the UI Drone display */
    private final TopLeftDroneDisplay myTopLeft;

    /** Represent the Drone Stats Panel display */
    private final TopRightStats myTopRight;

    /** Represent the Anomaly Log display */
    private final BottomTable myBottomSide;

    /** Represent the Database Manager */
    private DatabasePopup myDatabase;

    /** Represent the whole window application */
    private Scene myScene;


    /* =============
    CONSTRUCTOR
     ===============*/

    /**
     * Constructor of MyJavaFXApp
     */
    public MonitorDash(final DroneMonitorApp theController) {
        myController = Objects.requireNonNull(theController, "Controller can't be null");

        mySoundManager = new SoundManager();

        // Initializing all the parts and pieces of the UI
        myTopLeft = new TopLeftDroneDisplay(this);
        myBottomSide = new BottomTable();
        myTopRight = new TopRightStats(this);
    }


    /* =====================
    BUILDING THE APPLICATION
    ========================*/

    /**
     * Initial setup for our application.
     *
     * @param thePrimaryStage Our primary JavaFX stage.
     */
    public void initializeSimulation(final Stage thePrimaryStage) {
        //Setting up the root to hold all the UI components
        BorderPane root = setupLayout(thePrimaryStage);

        // Creating the Window Application
        myScene = new Scene(root, 800, 700);
        applyStylesheet("dark_theme.css");

        // Creating the Popup window for Database Manager
        myDatabase = new DatabasePopup(thePrimaryStage);

        // Stage Configuration
        thePrimaryStage.setTitle("Drone Simulation");
        thePrimaryStage.setScene(myScene);
        thePrimaryStage.show();

        // Stuff the program runs after its build
        Platform.runLater(() -> {
            swapRightPanel(false); //Don't delete this part
        });
    }

    /**
     * Private helper to set up the BorderPane of the Application.
     *
     * @param thePrimaryStage is the stage that this border pane is applying to.
     * @return a BorderPane for the application.
     */
    private BorderPane setupLayout(final Stage thePrimaryStage) {
        //Main VBox that holds everything
        VBox mainBox = new VBox(10);
        mainBox.getStyleClass().add("main-box");

        //The top and bottom of the GUI
        HBox topSide = new HBox(10);
        VBox.setVgrow(topSide, Priority.ALWAYS);
        HBox.setHgrow(myTopRight, Priority.NEVER);

        mainBox.getChildren().addAll(topSide, myBottomSide);
        topSide.getChildren().addAll(myTopLeft, myTopRight);

        //Menu bar
        AppMenuBar menuBar = new AppMenuBar(this, thePrimaryStage);

        // Creating the Border root, and adding the components to it
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(mainBox);

        return root;
    }

    /* =====================
       VIEW UPDATES
    ========================*/

    /**
     * Updates the time label with the specified time.
     *
     * @param theTime The current time.
     */
    public void updateTime(final int theTime) {
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
        //If the array is null, do nothing
        if (theDrones == null) return;

        //For each drone, do the non-array equivalent of this function
        for (Drone drone : theDrones) {
            updateStatsText(drone);
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


     /* =====================
       ANOMALY HANDLING
    ========================*/

    /**
     * Add an anomaly record to the table in the GUI.
     *
     * @param theRecord The anomaly record we'll be adding.
     */
    public void addAnomalyRecord(AnomalyRecord theRecord) {
        myBottomSide.addAnomalyRecord(theRecord);
        mySoundManager.playNotificationSound();
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
     * Clears the anomaly table in the GUI, then replaces its contents with the given List.
     *
     * @param theRecords What we want the contents to be.
     */
    public void refreshAnomalyRecords(List<AnomalyRecord> theRecords) {
        if (theRecords == null || theRecords.isEmpty()) { return; }

        myBottomSide.getAnomalyTable().getItems().clear();
        myBottomSide.refreshAnomalyRecords(theRecords);
        mySoundManager.playNotificationSound();
    }


    /* ====================================
    Simulation Phases
     ====================================*/

    /**
     * Tell the controller DroneMonitorApp to start the game.
     */
    public void startGame() {
        myController.startSim();
        reloadFleet(myController.getFleet());
        myTopLeft.getDroneDisplay().setStyle(null);

        System.out.print("MonitorDash: Started Game");
    }

    /**
     * Tell the controller DroneMonitorApp to toggle pausing.
     */
    public void togglePauseGame() {
        if (myIsPaused) {
            // Was paused, now resuming
            myController.continueSim();
            myTopLeft.setPausedMode(false);
            System.out.println("MonitorDash: Resumed");
        } else {
            // Was running, now pausing
            myController.pauseSim();
            myTopLeft.setPausedMode(true);
            System.out.println("MonitorDash: Paused");
        }

        myIsPaused = !myIsPaused;
    }


    /**
     * Tell the controller DroneMonitorApp to end the game.
     */
    public void endGame() {
        myController.stopSim();
        myTopLeft.getDroneDisplay().setStyle("-fx-background-color: null;");
        System.out.println("MonitorDash: stopped game");
    }

    /**
     * Help handle change the number of drone count
     *
     * @param theNewDroneCount is the new number for the amount of drones.
     */
    public void changeDroneCount(int theNewDroneCount) {
        // Stopping the Sim, safety insurance
        endGame();

        myController.changeDroneCount(theNewDroneCount);
        reloadFleet(myController.getFleet());
    }

    /**
     * Helper method to go through and update the Stats Panel for drone count changes.
     *
     * @param theDroneFleet represent the Drone Fleet.
     */
    private void reloadFleet(final Drone[] theDroneFleet) {
        // Cleaning the UI and Drone Map
        myTopLeft.clearAllDrones();
        myDrones.clear();

        // Ensures that theDroneFleet isn't null, if not, then add drone to map and recreate stats panel
        if (theDroneFleet != null) {
            for (Drone drone : theDroneFleet) {
                myDrones.put(drone.getDroneID(), drone);
            }
            myTopRight.recreateDroneCards(theDroneFleet);
        }
    }


    /* =====================
       UI INTERACTIONS
    ========================*/

    /**
     * Swaps between displaying the small stats boxes and the big stats box.
     *
     * @param theBigStatsBox True = show big stats box, False = show small stats boxes.
     */
    public void swapRightPanel(boolean theBigStatsBox) {
        if (!theBigStatsBox) {
            myTopLeft.deselectAll();
        }

        if (theBigStatsBox != myTopRight.getShowingStats()) {
            myTopRight.swapRightPanel(theBigStatsBox);
        }
    }

    /**
     * Tells the map to highlight a specific drone.
     *
     * @param theDroneID is the drone ID that's being used to which selected drone we are clicking.
     */
    public void selectDroneOnMap(final int theDroneID) {
        myTopLeft.selectDrone(theDroneID);
    }

    /**
     * Method that allow to show the database popup.
     */
    public void showDatabase() {
        myDatabase.show();
    }

    /**
     * Method that applies a CSS style sheet to the whole application.
     *
     * @param theCSSName represent the CSS class to use to update the stylesheet.
     */
    public void applyStylesheet(String theCSSName) {
        myScene.getStylesheets().clear();
        myScene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(theCSSName)).toExternalForm()
        );

        myBottomSide.applyStylesheet(theCSSName);
        myTopRight.applyStylesheet(theCSSName);
        myTopLeft.applyStylesheet(theCSSName);
    }

    /**
     * Method to export the anomaly log as a txt file.
     *
     * @param theStage represent the stage of the application.
     */
    public void exportToTXT(final Stage theStage) {
        myBottomSide.exportToTXTDialog(theStage);
    }

    /**
     * Method to export the anomaly log as a CSV file.
     *
     * @param theStage represent the stage of the application.
     */
    public void exportToCSV(final Stage theStage) {
        myBottomSide.exportToCSVDialog(theStage);
    }


    /* =====================
       GETTERS / SETTERS
    ========================*/

    /**
     * Method to see if isPaused on or not.
     *
     * @return a boolean of the Stage paused status.
     */
    public boolean isPaused() {
        return myIsPaused;
    }

    /**
     * Method to give the Sound Manager.
      */
    public SoundManager getSoundManager() {
        return mySoundManager;
    }
}