package view;

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
import java.util.function.Consumer;

import controller.*;
import controller.DroneMonitorApp;
import Model.AnomalyRecord;
import Model.Drone;

/**
 * A Singleton class which houses our Graphical User Interface for the application.
 */
public class MonitorDash  {
    /* ===============================
    FIELDS FOR GUI ELEMENTS - Generally don't touch them
     ================================= */

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
    public boolean myIsPaused = false;

    /**
     * Whether the audio for our UI is muted or not.
     */
    public boolean myIsMuted = false;

    /**
     * Volume of our UI audio
     */
    private int myVolume = 100;


    private Consumer<Integer> myDroneCountChangeRequest;

    public void setMyDroneCountChangeRequest(Consumer<Integer> theDroneCountRequest) {
        myDroneCountChangeRequest = theDroneCountRequest;
    }














    /* =============
    SECTIONS OF THE GUI
     ===============*/
    TopLeftDroneDisplay myTopLeft = new TopLeftDroneDisplay(this);
    BottomTable myBottomSide = new BottomTable();
    TopRightStats myTopRight = new TopRightStats(this);
    DatabasePopup myDatabase;
    private Scene myScene;
    private MediaPlayer notificationPlayer;







    DroneMonitorApp myController;

    DroneFleetManager myFleet;




    /*======================
    INTERACTING WITH THE GUI
     =======================*/

    /**
     * Constructor of MyJavaFXApp
     */
    public MonitorDash() {

    }

    public void setController (DroneMonitorApp theController) {
        myController = Objects.requireNonNull(theController, "Controller cannot be null");
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
    public void initializeSimulation(final Stage thePrimaryStage) {
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
        AppMenuBar menuBar = new AppMenuBar(this, thePrimaryStage);

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
    public void swapRightPanel(boolean theBigStatsBox) {
        if (!theBigStatsBox) {
            myTopLeft.deselectAll();
        }

        if (theBigStatsBox != myTopRight.getShowingStats()) {
            myTopRight.swapRightPanel(theBigStatsBox);
        }
    }

    public void applyStylesheet(String cssName) {
        myScene.getStylesheets().clear();
        myScene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(cssName)).toExternalForm()
        );

        myBottomSide.applyStylesheet(cssName);
        myTopRight.applyStylesheet(cssName);
        myTopLeft.applyStylesheet(cssName);
    }











    public void showDatabase() {
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
        return myFleet;
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

    public void showVolumePopup() {
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
    public void changeDroneCount(int theNewDroneCount) {
        // Stopping the Sim, safety insurance
        endGame();

        // Updating the UI by clearing the monitor, clearing the drone map, remaking the stats panel
        myTopLeft.clearAllDrones();
        myDrones.clear();
        myTopRight.recreateDroneCards();

        // Telling the controller the new user input
        if (myDroneCountChangeRequest != null) {
            myDroneCountChangeRequest.accept(theNewDroneCount);
        }

        System.out.println("MonitorDash: Drone count changed to " + theNewDroneCount);
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
    public void startGame() {
        myController.startSim();
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
        // Clearing the drones on the screen
        myTopLeft.clearAllDrones();
        myTopRight.recreateDroneCards();
        myDrones.clear();
        System.out.println("MonitorDash: stopped game");
    }
}