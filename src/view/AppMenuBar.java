package view;

import database.AnomalyDB;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.TimerManager;
import java.util.Objects;
import java.util.Optional;

/**
 * Build the Simulation App Menu Bar.
 *
 * @version Autumn 2025
 */
public class AppMenuBar extends MenuBar {
    /*-- Dependency Classes --*/

    /** Reference to the Main UI Display */
    private final MonitorDash myMonitor;

    /** The Primary Stage for the Simulation application */
    private final Stage myPrimaryStage;


    /*-- Constants --*/

    /** Represent the Min Drones in a fleet, a user can change */
    private static final int MIN_DRONES_ALLOWED = 1;

    /** Represent the Max Drones in a fleet, a user can change */
    private static final int MAX_NUMBER_DRONES = 50;

    /*-- Fields --*/

    /** Reference to the DroneCountMenu Item */
    private Menu myDroneCountMenu;

    /** Reference to the TickSpeedMenu Item */
    private Menu myTickSpeedMenu;

    /** Reference to pause/resume menu item */
    private MenuItem myPauseItem;


    /*-- Constructor --*/

    /**
     * A constructor that construct the application menu bar base on the given UI and Stage.
     *
     * @param theMonitor represents the main UI display.
     * @param theStage represents the primary window stage
     * @throws NullPointerException if either given parameter is null.
     */
    public AppMenuBar(final MonitorDash theMonitor, final Stage theStage) {
        // Checks either parameter it's not null
        myMonitor = Objects.requireNonNull(theMonitor, "Monitor can't be null");
        myPrimaryStage = Objects.requireNonNull(theStage, "Stage can't be null");

        buildMenuBar();
    }


    /*-- Helper method to build all the menu item --*/

    /**
     * Sets up the application's menu bar by building all the menus.
     */
    private void buildMenuBar() {
        getMenus().addAll(
                buildFileMenu(),
                buildSettingMenu(),
                buildSimulationMenu(),
                buildHelpMenu()
        );
    }


    /*-- Helper method to build each individual menu item --*/

    /**
     * Build the File Menu.
     *
     * @return a menu that represents the File Menu.
     */
    private Menu buildFileMenu() {
        // The overall Menu
        final Menu fileMenu = new Menu("File");

        // MenuItem: Database Manager
        MenuItem databaseMenuItem = new MenuItem("Database Manager");
        databaseMenuItem.setOnAction(_ -> myMonitor.showDatabase());

        // MenuItem: Export Log
        Menu exportMenu = new Menu("Export Log");
        // Export SubItem: TXT, CSV
        MenuItem txtExportItem = new MenuItem("TXT");
        txtExportItem.setOnAction(_ -> myMonitor.exportToTXT(myPrimaryStage));
        MenuItem csvExportItem = new MenuItem("CSV");
        csvExportItem.setOnAction(_ -> myMonitor.exportToCSV(myPrimaryStage));
        // Adding each sub-item to the Export Menu
        exportMenu.getItems().addAll(txtExportItem, csvExportItem);

        // MenuItem: Exit
        MenuItem exitMenuItem = new MenuItem("Exit");
        // Exit simulation when click exit action
        exitMenuItem.setOnAction(_ -> {
            myMonitor.endGame(); // Ensure everything is shut down
            AnomalyDB.close();
            myPrimaryStage.close();
        });

        // Adding all Menu Items to the File Menu
        fileMenu.getItems().addAll(databaseMenuItem, exportMenu, exitMenuItem);
        return fileMenu;
    }


    /**
     * Build the Setting Menu.
     *
     * @return a menu that represents the Setting Menu.
     */
    private Menu buildSettingMenu() {
        // The overall Menu
        Menu settingMenu = new Menu("Settings");

        // Setting SubItem: TickSpeed, DroneCount, Theme, Sound
        buildTickMenuItem(settingMenu);
        buildDroneCountMenuItem(settingMenu);
        buildThemeMenuItem(settingMenu);
        buildSoundMenuItem(settingMenu);

        return settingMenu;
    }

    /**
     * Helper method that creates the 'Tick Speed' Menu Item.
     */
    private void buildTickMenuItem(final Menu theSettingMenu) {
        // MenuItem: Tick Speed
        myTickSpeedMenu = new Menu("Tick Speed");

        // Tick Speed SubItem: Slow, Normal, Fast, Custom
        MenuItem slowTickItem = new MenuItem("Slow (5s)");
        MenuItem normalTickItem = new MenuItem("Normal (3s)");
        MenuItem fastTickItem = new MenuItem("Fast (1s)");
        MenuItem customTickItem = new MenuItem("Custom...");

        // Event Action for each Sub-item
        slowTickItem.setOnAction(_ -> myMonitor.changeTickSpeed(5));
        normalTickItem.setOnAction(_ -> myMonitor.changeTickSpeed(3));
        fastTickItem.setOnAction(_ -> myMonitor.changeTickSpeed(1));
        customTickItem.setOnAction(_ -> handleCustomTickSpeedPopUp());

        // Adding each sub-item to the Tick Speed Menu
        myTickSpeedMenu.getItems().addAll(slowTickItem, normalTickItem, fastTickItem, customTickItem);

        // Adding all the Sub Menu to the Setting Menu
        theSettingMenu.getItems().addAll(myTickSpeedMenu);
    }

    /**
     * Helper method to handle a pop screen for a custom tick speed.
     */
    private void handleCustomTickSpeedPopUp() {
        // Pop up setting
        TextInputDialog inputChat = new TextInputDialog("1");
        inputChat.setTitle("Custom Tick Speed");
        inputChat.setHeaderText("Enter tick speed in seconds:");
        inputChat.setContentText("Speed (1 - 10):");

        // Method to show the dialog box and waiting for user input
        while(true) {
            Optional<String> result = inputChat.showAndWait();

            if (result.isEmpty()) {
                break;
            }

            // Showing an error if bad input
            String input = result.get().trim();
            if (input.isEmpty()) {
                showErrorGuide(inputChat, "Please enter a number.");
                continue;
            }

            // Will continue until the user leaves or valid input
            try {
                int speed = Integer.parseInt(input);

                if (speed >= 1 && speed <= 10) {
                    myMonitor.changeTickSpeed(speed);
                    break;
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Please enter a number between 0.1 and 10.0 seconds");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                // Input is bad, retry again
                showErrorGuide(inputChat, "Invalid number.");
            }
        }
    }

    /**
     * Helper method that creates the 'DroneCount' Menu Item.
     */
    private void buildDroneCountMenuItem(final Menu theSettingMenu) {
        myDroneCountMenu = new Menu("Drone Count");
        // Drone Count SubItem: 3 Drones, 5 Drones, 10 Drones, Custom
        MenuItem threeDroneItem = new MenuItem("3 Drones");
        MenuItem fiveDroneItem = new MenuItem("5 Drones");
        MenuItem tenDroneItem = new MenuItem("10 Drones");
        MenuItem customDroneItem = new MenuItem("Custom...");
        // Event Action for each Sub-item
        threeDroneItem.setOnAction(_ -> myMonitor.changeDroneCount(3));
        fiveDroneItem.setOnAction(_ -> myMonitor.changeDroneCount(5));
        tenDroneItem.setOnAction(_ -> myMonitor.changeDroneCount(10));
        customDroneItem.setOnAction(_ -> handleCustomDroneCountPopUp());
        // Adding each sub-item to the DroneCount Menu
        myDroneCountMenu.getItems().addAll(threeDroneItem, fiveDroneItem, tenDroneItem, customDroneItem);

        // Adding all the Sub Menu to the Setting Menu
        theSettingMenu.getItems().addAll(myDroneCountMenu);
    }

    /**
     * Helper method to handle a pop screen for a custom amount of drones.
     */
    private void handleCustomDroneCountPopUp() {
        // Pop up setting
        TextInputDialog inputChat = new TextInputDialog("3");
        inputChat.setTitle("Custom Drone Count");
        inputChat.setHeaderText("Enter number of drones:");
        inputChat.setContentText("Count:");

        // Method to show the dialog box and waiting the user for input
        while(true) {
            Optional<String> result = inputChat.showAndWait();

            if (result.isEmpty()) {
                break;
            }

            // Showing an error if bad input
            String input = result.get().trim();
            if (input.isEmpty()) {
                showErrorGuide(inputChat, "Please enter a number.");
                continue;
            }

            // Will continue until user leave or valid input
            try {
                int count = Integer.parseInt(input);

                if (count >= MIN_DRONES_ALLOWED && count <= MAX_NUMBER_DRONES) {
                    myMonitor.changeDroneCount(count);
                    break;
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Please enter a number between " + MIN_DRONES_ALLOWED + " and " + MAX_NUMBER_DRONES);
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                // Input is bad, retry again
                showErrorGuide(inputChat, "Invalid number.");
            }

        }
    }

    /**
     * Helper method to show the User bad input.
     *
     * @param theDialog represent the user input.
     * @param theMessage represent the message base on the user input.
     */
     private void showErrorGuide(final TextInputDialog theDialog, String theMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, theMessage, ButtonType.OK);
        alert.setHeaderText(null);
        alert.initOwner(theDialog.getOwner());
        alert.showAndWait();
    }

    /**
     * Helper method that creates the 'Theme' Menu Item.
     */
    private void buildThemeMenuItem(final Menu theSettingMenu) {
        // MenuItem: Themes
        Menu themeMenu = new Menu("Theme");
        // Theme SubItem: Dark, White, Special
        MenuItem darkThemeItem = new MenuItem("Dark Theme");
        darkThemeItem.setOnAction(_ -> myMonitor.applyStylesheet("dark_theme.css"));
        MenuItem lightThemeItem = new MenuItem("Light Theme");
        lightThemeItem.setOnAction(_ -> myMonitor.applyStylesheet("light_theme.css"));
        MenuItem fabulousThemeItem = new MenuItem("Fabulous");
        fabulousThemeItem.setOnAction(_ -> myMonitor.applyStylesheet("special_theme.css"));
        // Adding each sub-item to the Theme Menu
        themeMenu.getItems().addAll(darkThemeItem, lightThemeItem, fabulousThemeItem);

        // Adding all the Sub Menu to the Setting Menu
        theSettingMenu.getItems().addAll(themeMenu);
    }

    /**
     * Helper method that creates the 'Sound' Menu Item.
     */
    private void buildSoundMenuItem(final Menu theSettingMenu) {
        // MenuItem: Sound
        Menu soundMenu = new Menu("Sound");
        // Sound SubItem: Enable, Disable, Sound
        MenuItem enableSoundItem = new MenuItem("Enable Sounds");
        enableSoundItem.setOnAction(_ -> myMonitor.getSoundManager().setMuted(false));

        MenuItem disableSoundItem = new MenuItem("Disable Sounds");
        disableSoundItem.setOnAction(_ -> myMonitor.getSoundManager().setMuted(true));
        MenuItem volume = new MenuItem("Volume...");
        volume.setOnAction(_ -> showVolumePopup());
        MenuItem testSoundItem = new MenuItem("Test Sound");
        testSoundItem.setOnAction(_ -> myMonitor.getSoundManager().playNotificationSound());
        // Adding each sub-item to the Sound Menu
        soundMenu.getItems().addAll(enableSoundItem, disableSoundItem, volume, testSoundItem);

        // Adding all the Sub Menu to the Setting Menu
        theSettingMenu.getItems().addAll(soundMenu);
    }

    /**
     * Helper method to show the Volume Popup
     */
    private void showVolumePopup() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Adjust Volume");
        dialog.setHeaderText("Set UI Audio Volume");

        // OK & Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        int currentVolume = myMonitor.getSoundManager().getVolume();

        // Slider
        Slider slider = new Slider(0, 100, currentVolume);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(25);
        slider.setBlockIncrement(1);

        Label valueLabel = new Label(String.valueOf(currentVolume));

        slider.valueProperty().addListener((_, _, newVal) ->
                valueLabel.setText(String.valueOf(newVal.intValue()))
        );

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
            myMonitor.getSoundManager().setVolume(newVol);
            System.out.println("Volume set to: " + newVol);
        });
    }


    /**
     * Build the Simulation Menu.
     *
     * @return a menu that represents the Simulation Menu.
     */
    private Menu buildSimulationMenu() {
        // The overall Menu
        Menu simMenu = new Menu("Simulation");

        // Simulation SubItem: Start, Pause, Stop
        MenuItem startItem = new MenuItem("Start");
        myPauseItem = new MenuItem("Pause");
        MenuItem stopItem = new MenuItem("Stop");

        // Default states
        myPauseItem.setDisable(true);
        stopItem.setDisable(true);

        // Start Action
        startItem.setOnAction(_ -> {
            myMonitor.startGame();
            startItem.setDisable(true);
            myPauseItem.setDisable(false);
            stopItem.setDisable(false);
            myDroneCountMenu.setDisable(true);
            myTickSpeedMenu.setDisable(true);
        });

        // Pause Action (Pure Push)
        myPauseItem.setOnAction(_ -> myMonitor.togglePauseGame());

        // Stop Action
        stopItem.setOnAction(_ -> {
            myMonitor.endGame();
            startItem.setDisable(false);
            myDroneCountMenu.setDisable(false);
            myTickSpeedMenu.setDisable(false);
            stopItem.setDisable(true);
            myPauseItem.setDisable(true);
            myPauseItem.setText("Pause"); // Reset text
        });

        // Adding all the Sub Menu to the Sim Menu
        simMenu.getItems().addAll(startItem, myPauseItem, stopItem);

        return simMenu;
    }

    /**
     * Method that updates the menu bar based on the current simulation status.
     * Called automatically when simulation status changes.
     *
     * @param theStatus the current simulation status
     */
    public void updateSimulationStatus(final TimerManager.Status theStatus) {
        // Update pause/resume menu item
        if (myPauseItem != null) {
            switch (theStatus) {
                case RUNNING:
                    myPauseItem.setText("Pause");
                    myPauseItem.setDisable(false);
                    break;
                case PAUSED:
                    myPauseItem.setText("Resume");
                    myPauseItem.setDisable(false);
                    break;
                case STOPPED:
                    myPauseItem.setText("Pause");
                    myPauseItem.setDisable(true);
                    break;
            }
        }
    }

    /**
     * Build the Helper Menu.
     *
     * @return a menu that represents the Helper Menu.
     */
    private Menu buildHelpMenu() {
        // The overall Menu
        Menu helpMenu = new Menu("Help");
        
        // Help SubItem: About, Version
        buildAboutMenuItem(helpMenu);
        buildVersionMenuItem(helpMenu);

        return helpMenu;
    }


    /**
     * Helper method that creates the 'About' Menu Item.
     */
    private void buildAboutMenuItem(final Menu theHelpMenu) {
        // The About Menu Item
        MenuItem aboutItem = new MenuItem("About");

        // About Menu Item Action
        aboutItem.setOnAction(_ -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About Application");
            alert.setHeaderText("Drone Application - About");
            alert.setContentText("""
                    An application used for simulating a fleet of drones.
                    Created by Oisin Perkins-Gilbert, Mankirat Mann, James Escudero
                    Created with Java, IntelliJ, and JavaFX
                    Made in 2025
                    Sound Effects taken from: https://pixabay.com/sound-effects/new-notification-010-352755/
                    Github: https://github.com/jamesesc/Drone-Simulator-Project""");
            alert.showAndWait();
        });

        // Adding all the Sub Menu to the Help Menu
        theHelpMenu.getItems().addAll(aboutItem);
    }

    /**
     * Helper method that creates the 'Version' Menu Item.
     */
    private void buildVersionMenuItem(final Menu theHelpMenu) {
        // The Version Menu Item
        MenuItem versionItem = new MenuItem("Version");

        // Version Menu Item Action
        versionItem.setOnAction(_ -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Application Version");
            alert.setHeaderText("Drone Application - Version");
            alert.setContentText("""
                    Current version: v5
                    Build date: 11/30/25
                    JavaFX Version: openjfx-25.0.1
                    Java Version: jdk-25""");
            alert.showAndWait();
        });

        // Adding all the Sub Menu to the Help Menu
        theHelpMenu.getItems().addAll(versionItem);
    }
}