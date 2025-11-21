package controller;

import view.MonitorDash;

/**
 * A class that handles and manage in updating the UI with the needed data for the simulation.
 * It is used by SimulationScheduler to help and assist in updating the front end with new data.
 *
 * @author James Escudero
 * @version Autumn 2025
 */
public class UpdateUIManager {
    /** Represents the UpdateUIManager instance */
    private static UpdateUIManager instance;

    /** Private constructor with nothing in it */
    private UpdateUIManager() {}

    /**
     * A method to ensure only one UpdateUIManager instance is created.
     *
     * @return the single instance of UpdateUIManager.
     */
    public static UpdateUIManager getInstance() {
        if (instance == null) {
            instance = new UpdateUIManager();
        }
        return instance;
    }

    /** Updates the UI to reflect the latest changes to the drone telemetry data */
    public void updateDroneDisplay() {
        DroneFleetManager fleetManager = DroneFleetManager.getInstance();
        // Updating the Big Stats
        MonitorDash.getInstance().updateStatsTextLarge(fleetManager.getSpecificDrone(0));
        // Updating the Display
        MonitorDash.getInstance().refreshDroneDisplay(fleetManager.getDroneFleet());
    }

    /** Updates the UI to display the current timer */
    public void updateTimer(final int theTime) {
        MonitorDash.getInstance().updateTime(theTime);
    }
}