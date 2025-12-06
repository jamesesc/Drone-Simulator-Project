package view;

import Model.AnomalyRecord;
import Model.Drone;
import javafx.application.Platform;
import service.TimerManager;
import java.util.List;
import java.util.Objects;

/**
 * A class that handles and manage in updating the UI with the needed data for the simulation.
 * It is used by SimulationEngine to help and assist in updating the front end with new data.
 *
 * @version Autumn 2025
 */
public class UpdateUIManager implements SimulationListener {
    /*-- Dependency Injection --*/

    /** A MonitorDash Object responsible for displaying the UI */
    private final MonitorDash myUI;


    /*-- Constructor --*/

    /**
     * Public constructor to initialize the UI and Drone Fleet Manager
     *
     * @param theUI represent the UI the Sound Manager is for.
     */
    public UpdateUIManager(final MonitorDash theUI) {
        myUI = Objects.requireNonNull(theUI, "theUI can't be null");
    }

    /*-- Interface methods --*/

    @Override
    public void onDroneUpdate(Drone[] theDroneFleet) {
        Platform.runLater(() -> {
            // Updating the Big Stats
            myUI.updateStatsText(theDroneFleet);
            // Updating the Display
            myUI.refreshDroneDisplay(theDroneFleet);
        });
    }

    @Override
    public void onTimeUpdate(final int theTime) {
        if (theTime < 0) {
            throw new IllegalArgumentException("theTime can't be less than 0");
        }

        Platform.runLater(() -> {
            myUI.updateTime(theTime);
        });
    }

    @Override
    public void onAnomaliesDetected(final AnomalyRecord[] theAnomalies) {
        if (theAnomalies == null) {
            throw new NullPointerException("Anomaly array cannot be null");
        }

        Platform.runLater(() -> {
            myUI.addAnomalyRecord(List.of(theAnomalies));
        });
    }

    @Override
    public void onFleetReloaded(Drone[] theDroneFleet) {
        if (theDroneFleet != null) {
            Platform.runLater(() -> {
                myUI.reloadFleet(theDroneFleet);
            });
        }
    }

    @Override
    public void onStatusChanged(TimerManager.Status theStatus) {
        Platform.runLater(() -> {
            myUI.updateSimulationStatus(theStatus);
        });
    }

    @Override
    public void databaseManagerButtonPushed(List<String[]> theRecords) {
        Platform.runLater(() -> {
            myUI.showDatabasePopup(theRecords);
        });
    }
}