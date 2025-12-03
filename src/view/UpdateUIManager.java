package view;

import Model.AnomalyRecord;
import Model.Drone;
import service.TimerManager;
import java.util.List;
import java.util.Objects;

/**
 * A class that handles and manage in updating the UI with the needed data for the simulation.
 * It is used by SimulationEngine to help and assist in updating the front end with new data.
 *
 * @author James Escudero
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

    /**
     * Updates the UI to reflect the latest changes to the drone telemetry data
     */
    @Override
    public void onDroneUpdate(Drone[] theDroneFleet) {
        // Updating the Big Stats
        myUI.updateStatsText(theDroneFleet);
        // Updating the Display
        myUI.refreshDroneDisplay(theDroneFleet);
    }

    /**
     * Updates the UI to display the current timer.
     *
     * @param theTime represents the new elapsed time.
     * @throws IllegalArgumentException if the theTime is less than 0.
     */
    @Override
    public void onTimeUpdate(final int theTime) {
        if (theTime < 0) {
            throw new IllegalArgumentException("theTime can't be less than 0");
        }

        myUI.updateTime(theTime);
    }

    /**
     * Updates the UI to display the new Anomalies
     *
     * @param theAnomalies is the array of anomalies
     */
    @Override
    public void onAnomaliesDetected(final AnomalyRecord[] theAnomalies) {
        if (theAnomalies == null) {
            throw new NullPointerException("Anomaly array cannot be null");
        }

        myUI.addAnomalyRecord(List.of(theAnomalies));
    }

    /**
     * Updates the UI when to reload the drone fleet.
     *
     * @param theDroneFleet represent the drone fleet to reload to.
     */
    @Override
    public void onFleetReloaded(Drone[] theDroneFleet) {
        myUI.reloadFleet(theDroneFleet);
    }

    /**
     * Updates the UI the simulation status.
     *
     * @param theStatus represnt the new status of the simulation.
     */
    @Override
    public void onStatusChanged(TimerManager.Status theStatus) {
        myUI.updateSimulationStatus(theStatus);
    }
}