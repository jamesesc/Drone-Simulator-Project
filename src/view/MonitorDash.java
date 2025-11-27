package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
     * Represents the Timer Controller for the simulation.
     */
    private final TimerManager myTimerManager = new TimerManager();
    /**
     * Represents the Drone Controller for the simulation.
     */
    private final DroneFleetManager myFleetManager = new DroneFleetManager();
    /**
     * Represents the Update UI Controller for the simulation.
     */
    private final UpdateUIManager myUpdateUIManager = new UpdateUIManager(this, myFleetManager);
    /**
     * Represents the Simulation Scheduler Controller for the simulation.
     */
    SimulationScheduler scheduler = new SimulationScheduler(myTimerManager, myFleetManager, myUpdateUIManager);
    /**
     * Represent the Organizer Controller for the simulation.
     */
    private final DroneMonitorApp myController = new DroneMonitorApp(myTimerManager, scheduler);

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
    CONSTANTS
     ===============================*/

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
        //If the array is null, do nothing
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

        Scene scene = new Scene(root, 800, 700);
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
    void swapRightPanel(boolean theBigStatsBox) {
        if (!theBigStatsBox) {
            myTopLeft.deselectAll();
        }

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
        // A MenuBar that will hold all the Menu
        MenuBar menuBar = new MenuBar();


        // -- Menu 1: The File ---
        Menu fileMenu = new Menu("File");

        // ---- File Menu Item 1: Export ----
        Menu exportMenu = new Menu("Export");

        // ---- Export SubItem: PDF, CSV ----
        MenuItem pdfItem = new MenuItem("PDF");
        MenuItem csvItem = new MenuItem("CSV");

        // Adding each sub-item to the Export Menu
        exportMenu.getItems().addAll(pdfItem, csvItem);

        // ---- File Menu Item 2: Exit ----
        MenuItem exitItem = new MenuItem("Exit");  // MenuItem, not Menu
        // Exit Item action when click
        exitItem.setOnAction(_ -> {
            endGame(); // Ensure everything is shut down
            thePrimaryStage.close();
        });

        // Adding the Exit Item to the File Menu
        fileMenu.getItems().addAll(exportMenu, exitItem);

        // -- Menu 2: The Setting --
        Menu settingMenu = new Menu("Settings");

        // ---- File Setting Item 1: Drone Count ----
        Menu droneCountMenu = new Menu("Drone Count");

        // ---- Drone Count SubItem: 3 Drones, 5 Drones, 10 Drones, Custom ----
        MenuItem droneCount3 = new MenuItem("3 Drones");
        MenuItem droneCount5 = new MenuItem("5 Drones");
        MenuItem droneCount10 = new MenuItem("10 Drones");
        MenuItem customDroneCount = new MenuItem("Custom...");

        // Event Action for each Sub-item
        droneCount3.setOnAction(_ -> changeDroneCount(3));
        droneCount5.setOnAction(_ -> changeDroneCount(5));
        droneCount10.setOnAction(_ -> changeDroneCount(10));
        customDroneCount.setOnAction(_ -> changeDroneCountCustom());

        // Adding each sub-item to the DroneCount Menu
        droneCountMenu.getItems().addAll(droneCount3, droneCount5, droneCount10, customDroneCount);


        // ---- File Setting Item 2: Probability ----
        Menu probabilityMenu = new Menu("Probability Settings");

        // ---- Probability SubItem: Velocity, Altitude, Orientation ----
        MenuItem velocityProbability = new MenuItem("Velocity Probability");
        MenuItem altitudeProbability = new MenuItem("Altitude Probability");
        MenuItem orientationProbability = new MenuItem("Orientation Probability");


        // Adding each sub-item to the Probability Menu
        probabilityMenu.getItems().addAll(velocityProbability, altitudeProbability, orientationProbability);

        // -- File Setting Item 3: Theme --
        Menu themeMenu = new Menu("Theme");

        // ---- Theme SubItem: Dark, White, Special ----
        MenuItem darkTheme = new MenuItem("Dark Theme");
        MenuItem lightTheme = new MenuItem("Light Theme");
        MenuItem customTheme = new MenuItem("Special");

        // Adding each sub-item to the Theme Menu
        themeMenu.getItems().addAll(darkTheme, lightTheme, customTheme);

        // -- File Setting Item 4: Sound --
        Menu soundMenu = new Menu("Sound");

        // ---- Sound SubItem: Enable, Disable, Sound ----
        MenuItem enableSound = new MenuItem("Enable Sounds");
        MenuItem disableSound = new MenuItem("Disable Sounds");
        MenuItem volume = new MenuItem("Volume...");

        // Adding each sub-item to the Sound Menu
        soundMenu.getItems().addAll(enableSound, disableSound, volume);

        // Adding all the Sub Menu to the Setting Menu
        settingMenu.getItems().addAll(droneCountMenu, probabilityMenu, themeMenu, soundMenu);


        // -- Menu 3: The Sim Control ---
        Menu simMenu = new Menu("Simulation");

        // ---- Sound Submenu: Start, Pause, Stop ----
        MenuItem startItem = new MenuItem("Start");
        MenuItem pauseItem = new MenuItem("Pause");
        MenuItem stopItem = new MenuItem("Stop");

        // Default states
        pauseItem.setDisable(true);
        stopItem.setDisable(true);

        // Start Action
        startItem.setOnAction(_ -> {
            startGame();
            startItem.setDisable(true);
            droneCountMenu.setDisable(true); // Prevent changing drones while running
            probabilityMenu.setDisable(true);
            stopItem.setDisable(false);
            pauseItem.setDisable(false);
        });

        // Pause Action
        pauseItem.setOnAction(_ -> {
            togglePauseGame();
            // Update text based on state
            if (myIsPaused) {
                pauseItem.setText("Resume");
            } else {
                pauseItem.setText("Pause");
            }
        });

        // Stop Action
        stopItem.setOnAction(_ -> {
            endGame();
            startItem.setDisable(false);
            droneCountMenu.setDisable(false); // Re-enable drone settings
            stopItem.setDisable(true);
            probabilityMenu.setDisable(false);
            pauseItem.setDisable(true);
            pauseItem.setText("Pause"); // Reset text
        });

        // Adding all the Sub Menu to the Sim Menu
        simMenu.getItems().addAll(startItem, pauseItem, stopItem);


        // -- Menu 4: The Help ---
        Menu helpSetting = new Menu("Help");

        // ---- Help Submenu: About, License, Version ----
        MenuItem aboutItem = new MenuItem("About");
        MenuItem licenseItem = new MenuItem("License");
        MenuItem versionItem = new MenuItem("Version");

        // Adding all the Sub Menu to the Help Menu
        helpSetting.getItems().addAll(aboutItem, licenseItem, versionItem);

        // Adding all the menus to the MenuBar
        menuBar.getMenus().addAll(fileMenu, settingMenu, simMenu, helpSetting);

        return menuBar;
    }


    /* ====================================
    GETTER METHOD
     ====================================*/

    /**
     * Getter method to get the FleetManager
     *
     * @return the DroneFleetManager to use.
     */
    public DroneFleetManager getFleetManager() {
        return myFleetManager;
    }


    /* ====================================
    HELPER METHODS
     ====================================*/

    /**
     * Help handle change the number of drone count
     *
     * @param theNewDroneCount is the new number for the amount of drones.
     */
    private void changeDroneCount(int theNewDroneCount) {
        // Stopping the Sim, safety insurance
        endGame();

        // Updating the backend for the new drone count
        myFleetManager.updateDroneCount(theNewDroneCount);

        // Updating the UI by clearing the monitor, clearing the drone map, remaking the stats panel
        myTopLeft.clearAllDrones();
        myDrones.clear();
        myTopRight.recreateDroneCards();

        System.out.println("MonitorDash: Drone count changed to " + theNewDroneCount);
    }

    /**
     * Help method to handle a pop screen for a custom amount
     */
    private void changeDroneCountCustom() {
        // Pop up setting
        TextInputDialog inputChat = new TextInputDialog("3");
        inputChat.setTitle("Custom Drone Count");
        inputChat.setHeaderText("Enter number of drones:");
        inputChat.setContentText("Count:");

        // Method to show the dialog box and waiting the user for input
        Optional<String> result = inputChat.showAndWait();

        // Handles the user input actions
        if (result.isPresent()) {
            // User entered, check if input is valid
            try {
                int count = Integer.parseInt(result.get());

                // Values for the allowed min and max drones user can create
                final int MIN_DRONES_ALLOWED = 1;
                final int MAX_NUMBER_DRONES = 50;

                if (count >= MIN_DRONES_ALLOWED && count <= MAX_NUMBER_DRONES) {
                    changeDroneCount(count);
                }
            } catch (NumberFormatException e) {
                // Input is bad, retry again
            }
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

    /* ====================================
    Simulation Phases
     ====================================*/

    /**
     * Tell the controller DroneMonitorApp to start the game.
     */
    private void startGame() {
        myController.startSim();
        myTopLeft.getDroneDisplay().setStyle(null);
        System.out.print("MonitorDash: Started Game");
    }

    /**
     * Tell the controller DroneMonitorApp to toggle pausing.
     */
    private void togglePauseGame() {
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
    private void endGame() {
        myController.stopSim();
        myTopLeft.getDroneDisplay().setStyle("-fx-background-color: null;");
        // Clearing the drones on the screen
        myTopLeft.clearAllDrones();
        myTopRight.recreateDroneCards();
        myDrones.clear();
        System.out.println("MonitorDash: stopped game");
    }

    //Start the application
    public static void main(final String[] theArgs) {
        launch(theArgs);
    }
}