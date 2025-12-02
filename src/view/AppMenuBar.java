package view;

import database.AnomalyDB;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.util.Objects;

public class AppMenuBar extends MenuBar {
    MonitorDash myMonitor;
    Stage myPrimaryStage;

    public AppMenuBar(MonitorDash theMonitor, Stage theStage) {
        myMonitor = Objects.requireNonNull(theMonitor, "Monitor can't be null");
        myPrimaryStage = Objects.requireNonNull(theStage, "Stage can't be null");
        buildMenuBar();
    }




    /**
     * Setup for the GUI's menu bar.
     *
     * @return The GUI's menu bar.
     */
    private void buildMenuBar() {
        // A MenuBar that will hold all the Menu
        MenuBar menuBar = new MenuBar();


        // -- Menu 1: The File ---
        Menu fileMenu = new Menu("File");

        //Opening the database manager
        MenuItem databaseMenu = new MenuItem("Database Manager");
        databaseMenu.setOnAction(_ -> {
            myMonitor.showDatabase();
        });

        // ---- File Menu Item 1: Export ----
        Menu exportMenu = new Menu("Export Log");

        // ---- Export SubItem: PDF, CSV ----
        MenuItem txtItem = new MenuItem("TXT");
        txtItem.setOnAction(_ -> {
            myMonitor.myBottomSide.exportToTXTDialog(myPrimaryStage);
        });
        MenuItem csvItem = new MenuItem("CSV");
        csvItem.setOnAction(_ -> {
            myMonitor.myBottomSide.exportToCSVDialog(myPrimaryStage);
        });

        // Adding each sub-item to the Export Menu
        exportMenu.getItems().addAll(txtItem, csvItem);

        // ---- File Menu Item 2: Exit ----
        MenuItem exitItem = new MenuItem("Exit");  // MenuItem, not Menu
        // Exit Item action when click
        exitItem.setOnAction(_ -> {
            myMonitor.endGame(); // Ensure everything is shut down
            AnomalyDB.close();
            myPrimaryStage.close();
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
        droneCount3.setOnAction(_ -> myMonitor.changeDroneCount(3));
        droneCount5.setOnAction(_ -> myMonitor.changeDroneCount(5));
        droneCount10.setOnAction(_ -> myMonitor.changeDroneCount(10));
        customDroneCount.setOnAction(_ -> myMonitor.changeDroneCountCustom());

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
            myMonitor.applyStylesheet("dark_theme.css");
        });
        MenuItem lightTheme = new MenuItem("Light Theme");
        lightTheme.setOnAction(_ -> {
            myMonitor.applyStylesheet("light_theme.css");
        });
        MenuItem customTheme = new MenuItem("Fabulous");
        customTheme.setOnAction(_ -> {
            myMonitor.applyStylesheet("special_theme.css");
        });

        // Adding each sub-item to the Theme Menu
        themeMenu.getItems().addAll(darkTheme, lightTheme, customTheme);

        // -- File Setting Item 4: Sound --
        Menu soundMenu = new Menu("Sound");

        // ---- Sound SubItem: Enable, Disable, Sound ----
        MenuItem enableSound = new MenuItem("Enable Sounds");
        enableSound.setOnAction(_ -> {
            myMonitor.myIsMuted = false;
        });
        MenuItem disableSound = new MenuItem("Disable Sounds");
        disableSound.setOnAction(_ -> {
            myMonitor.myIsMuted = true;
        });
        MenuItem volume = new MenuItem("Volume...");
        volume.setOnAction(_ -> myMonitor.showVolumePopup());
        MenuItem testSound = new MenuItem("Test Sound");
        testSound.setOnAction(_ -> myMonitor.playNotificationSound());

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
            myMonitor.startGame();
            startItem.setDisable(true);
            droneCountMenu.setDisable(true); // Prevent changing drones while running
            probabilityMenu.setDisable(true);
            stopItem.setDisable(false);
            pauseItem.setDisable(false);
        });

        // Pause Action
        pauseItem.setOnAction(_ -> {
            myMonitor.togglePauseGame();
            // Update text based on state
            if (myMonitor.myIsPaused) {
                pauseItem.setText("Resume");
            } else {
                pauseItem.setText("Pause");
            }
        });

        // Stop Action
        stopItem.setOnAction(_ -> {
            myMonitor.endGame();
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
        this.getMenus().addAll(fileMenu, settingMenu, simMenu, helpSetting);
    }
}