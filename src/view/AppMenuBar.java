package view;

import database.AnomalyDB;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Build the Simulation App Menu Bar.
 *
 * @author James Escudero
 * @version Autumn 2025
 */
public class AppMenuBar extends MenuBar {
    /*-- Dependency Classes --*/

    /** Reference to the Main UI Display */
    private final MonitorDash myMonitor;

    /** The Primary Stage for the Simulation application */
    private final Stage myPrimaryStage;
    
    private Menu myDroneCountMenu;


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
        txtExportItem.setOnAction(_ -> myMonitor.myBottomSide.exportToTXTDialog(myPrimaryStage));
        MenuItem csvExportItem = new MenuItem("CSV");
        csvExportItem.setOnAction(_ -> myMonitor.myBottomSide.exportToCSVDialog(myPrimaryStage));
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

        // Setting SubItem: DroneCount, Theme, Sound
        buildDroneCountMenuItem(settingMenu);
        buildThemeMenuItem(settingMenu);
        buildSoundMenuItem(settingMenu);

        return settingMenu;
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
        customDroneItem.setOnAction(_ -> myMonitor.changeDroneCountCustom());
        // Adding each sub-item to the DroneCount Menu
        myDroneCountMenu.getItems().addAll(threeDroneItem, fiveDroneItem, tenDroneItem, customDroneItem);

        // Adding all the Sub Menu to the Setting Menu
        theSettingMenu.getItems().addAll(myDroneCountMenu);
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
        enableSoundItem.setOnAction(_ -> myMonitor.myIsMuted = false);
        MenuItem disableSoundItem = new MenuItem("Disable Sounds");
        disableSoundItem.setOnAction(_ -> myMonitor.myIsMuted = true);
        MenuItem volume = new MenuItem("Volume...");
        volume.setOnAction(_ -> myMonitor.showVolumePopup());
        MenuItem testSoundItem = new MenuItem("Test Sound");
        testSoundItem.setOnAction(_ -> myMonitor.playNotificationSound());
        // Adding each sub-item to the Sound Menu
        soundMenu.getItems().addAll(enableSoundItem, disableSoundItem, volume, testSoundItem);

        // Adding all the Sub Menu to the Setting Menu
        theSettingMenu.getItems().addAll(soundMenu);
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
        MenuItem pauseItem = new MenuItem("Pause");
        MenuItem stopItem = new MenuItem("Stop");

        // Default states
        pauseItem.setDisable(true);
        stopItem.setDisable(true);

        // Start Action
        startItem.setOnAction(_ -> {
            myMonitor.startGame();
            startItem.setDisable(true);
            pauseItem.setDisable(false);
            stopItem.setDisable(false);
            myDroneCountMenu.setDisable(true);
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
            myDroneCountMenu.setDisable(false); 
            stopItem.setDisable(true);
            myDroneCountMenu.setDisable(false);
            pauseItem.setDisable(true);
            pauseItem.setText("Pause"); // Reset text
        });

        // Adding all the Sub Menu to the Sim Menu
        simMenu.getItems().addAll(startItem, pauseItem, stopItem);

        return simMenu;
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