package controller;

import Model.AnomalyRecord;
import Model.Drone;
import view.MonitorDash;

import java.util.List;
import java.util.Objects;

/**
 * A class that handles and manage in updating the UI with the needed data for the simulation.
 * It is used by SimulationScheduler to help and assist in updating the front end with new data.
 *
 * @author James Escudero
 * @version Autumn 2025
 */
public class UpdateUIManager {
    /** A MonitorDash Object responsible for displaying the UI */
    private final MonitorDash myUI;

    /** A DroneFleetManager Object responsible for handling the fleet of drones */
    private final DroneFleetManager myDroneFleetManager;

    /** Public constructor to initialize the UI and Drone Fleet Manager */
    public UpdateUIManager(final MonitorDash theUI, final DroneFleetManager theDroneFleetManager) {
        myUI = Objects.requireNonNull(theUI, "theUI can't be null");
        myDroneFleetManager = Objects.requireNonNull(theDroneFleetManager, "theDroneFleetManager can't be null");
    }

    /** Updates the UI to reflect the latest changes to the drone telemetry data */
    public void updateDroneDisplay() {
        final Drone[] currentFleet = myDroneFleetManager.getDroneFleet();

        // Updating the Big Stats
        myUI.updateStatsText(currentFleet);
        // Updating the Display
        myUI.refreshDroneDisplay(currentFleet);
    }

    /**
     * Updates the UI to display the current timer.
     *
     * @param theTime represents the new elapsed time.
     * @throws IllegalArgumentException if the theTime is less than 0.
     */
    public void updateTimer(final int theTime) {
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
    public void updateAnomaly(final AnomalyRecord[] theAnomalies) {
        if (theAnomalies == null) {
            throw new NullPointerException("Anomaly array cannot be null");
        }

        myUI.addAnomalyRecord(List.of(theAnomalies));
    }

}