package view;

import database.AnomalyDB;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import controller.*;
import controller.DroneMonitorApp;
import Model.AnomalyRecord;
import Model.Drone;
import javafx.util.Duration;

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

    /**
     * Whether the audio for our UI is muted or not.
     */
    private boolean myIsMuted = false;

    /**
     * Volume of our UI audio
     */
    private int myVolume = 100;


    /* =============
    SECTIONS OF THE GUI
     ===============*/
    TopLeftDroneDisplay myTopLeft = new TopLeftDroneDisplay();
    BottomTable myBottomSide = new BottomTable();
    TopRightStats myTopRight = new TopRightStats();
    DatabasePopup myDatabase;
    private Scene myScene;
    private MediaPlayer notificationPlayer;


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

        playNotificationSound();
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

        playNotificationSound();
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

        myScene = new Scene(root, 800, 700);
        myScene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("dark_theme.css")).toExternalForm()
        );

        myDatabase = new DatabasePopup(thePrimaryStage);

        thePrimaryStage.setTitle("Drone Simulation");
        thePrimaryStage.setScene(myScene);
        thePrimaryStage.show();

        //Stuff the program runs after its build
        Platform.runLater(() -> {
            swapRightPanel(false); //Don't delete this part

            AnomalyRecord test = new AnomalyRecord("What 9 + 10", 9, 10.0, "21", "you stupid");
            addAnomalyRecord(test);
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

    private void applyStylesheet(String cssName) {
        myScene.getStylesheets().clear();
        myScene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(cssName)).toExternalForm()
        );

        myBottomSide.applyStylesheet(cssName);
        myTopRight.applyStylesheet(cssName);
        myTopLeft.applyStylesheet(cssName);
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

        //Opening the database manager
        MenuItem databaseMenu = new MenuItem("Database Manager");
        databaseMenu.setOnAction(_ -> {
            showDatabase();
        });

        // ---- File Menu Item 1: Export ----
        Menu exportMenu = new Menu("Export Log");

        // ---- Export SubItem: PDF, CSV ----
        MenuItem txtItem = new MenuItem("TXT");
        txtItem.setOnAction(_ -> {
            myBottomSide.exportToTXTDialog(thePrimaryStage);
        });
        MenuItem csvItem = new MenuItem("CSV");
        csvItem.setOnAction(_ -> {
            myBottomSide.exportToCSVDialog(thePrimaryStage);
        });

        // Adding each sub-item to the Export Menu
        exportMenu.getItems().addAll(txtItem, csvItem);

        // ---- File Menu Item 2: Exit ----
        MenuItem exitItem = new MenuItem("Exit");  // MenuItem, not Menu
        // Exit Item action when click
        exitItem.setOnAction(_ -> {
            endGame(); // Ensure everything is shut down
            AnomalyDB.close();
            thePrimaryStage.close();
        });

        // Adding the Exit Item to the File Menu
        fileMenu.getItems().addAll(databaseMenu, exportMenu, exitItem);

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
        darkTheme.setOnAction(_ -> {
            applyStylesheet("dark_theme.css");
        });
        MenuItem lightTheme = new MenuItem("Light Theme");
        lightTheme.setOnAction(_ -> {
            applyStylesheet("light_theme.css");
        });
        MenuItem customTheme = new MenuItem("Fabulous");
        customTheme.setOnAction(_ -> {
            applyStylesheet("special_theme.css");
        });

        // Adding each sub-item to the Theme Menu
        themeMenu.getItems().addAll(darkTheme, lightTheme, customTheme);

        // -- File Setting Item 4: Sound --
        Menu soundMenu = new Menu("Sound");

        // ---- Sound SubItem: Enable, Disable, Sound ----
        MenuItem enableSound = new MenuItem("Enable Sounds");
        enableSound.setOnAction(_ -> {
            myIsMuted = false;
        });
        MenuItem disableSound = new MenuItem("Disable Sounds");
        disableSound.setOnAction(_ -> {
            myIsMuted = true;
        });
        MenuItem volume = new MenuItem("Volume...");
        volume.setOnAction(_ -> showVolumePopup());
        MenuItem testSound = new MenuItem("Test Sound");
        testSound.setOnAction(_ -> playNotificationSound());

        // Adding each sub-item to the Sound Menu
        soundMenu.getItems().addAll(enableSound, disableSound, volume, testSound);

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
        aboutItem.setOnAction(_ -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About Application");
            alert.setHeaderText("Drone Application - About");
            alert.setContentText("An application used for simulating a fleet of drones.\n" +
                    "Created by Oisin Perkins-Gilbert, Mankirat Mann, James Escudero\n" +
                    "Created with Java, IntelliJ, and JavaFX\n" +
                    "Made in 2025\n" +
                    "Sound Effects taken from: https://pixabay.com/sound-effects/new-notification-010-352755/\n" +
                    "Github: https://github.com/jamesesc/Drone-Simulator-Project");
            alert.showAndWait();
        });

        MenuItem versionItem = new MenuItem("Version");
        versionItem.setOnAction(_ -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Application Version");
            alert.setHeaderText("Drone Application - Version");
            alert.setContentText("Current version: v5\n" +
                    "Build date: 11/30/25\n" +
                    "JavaFX Version: openjfx-25.0.1\n" +
                    "Java Version: jdk-25");
            alert.showAndWait();
        });

        // Adding all the Sub Menu to the Help Menu
        helpSetting.getItems().addAll(aboutItem, versionItem);

        // Adding all the menus to the MenuBar
        menuBar.getMenus().addAll(fileMenu, settingMenu, simMenu, helpSetting);

        return menuBar;
    }

    private void showDatabase() {
        myDatabase.show();
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

    public void playNotificationSound() {
        if (myIsMuted) return;

        if (notificationPlayer == null) {
            URL url = getClass().getResource("Assets/notification.wav");
            if (url == null) {
                System.out.println("ERROR in playNotificationSound(): no notification.wav found");
                return;
            }

            try {
                Media media = new Media(url.toExternalForm());
                notificationPlayer = new MediaPlayer(media);

                notificationPlayer.setOnEndOfMedia(() -> {
                    notificationPlayer.stop();
                });

            } catch (Throwable theException) {
                System.err.println("Audio not working: Ask Oisin (that's me!) for his Run Configuration");

                //Mute when there's an error
                myIsMuted = true;
                return;
            }
        }

        MediaPlayer.Status status = notificationPlayer.getStatus();

        if (status == MediaPlayer.Status.PLAYING) {
            return;
        }

        notificationPlayer.setVolume(myVolume / 100.0);
        notificationPlayer.play();
    }

    private void showVolumePopup() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Adjust Volume");
        dialog.setHeaderText("Set UI Audio Volume");

        // OK & Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Slider
        Slider slider = new Slider(0, 100, myVolume);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(25);
        slider.setBlockIncrement(1);

        Label valueLabel = new Label(Integer.toString(myVolume));

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            valueLabel.setText(String.valueOf(newVal.intValue()));
        });

        // Main box for popup
        HBox box = new HBox(10, new Label("Volume:"), slider, valueLabel);
        dialog.getDialogPane().setContent(box);

        // Convert result to int
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return (int) slider.getValue();
            }
            return null;
        });

        // Show dialog
        Optional<Integer> result = dialog.showAndWait();

        result.ifPresent(newVol -> {
            myVolume = newVol;
            System.out.println("Volume set to: " + myVolume);
        });
    }

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