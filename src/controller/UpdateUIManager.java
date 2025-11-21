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
    /** A MonitorDash Object responsible for displaying the UI */
    private final MonitorDash myUI;

    /** A DroneFleetManager Object responsible for handling the fleet of drones */
    private final DroneFleetManager myDroneFleetManager;

    /** Public constructor to initialize the UI and Drone Fleet Manager */
    public UpdateUIManager(final MonitorDash theUI, final DroneFleetManager theDroneFleetManager) {
        myUI = theUI;
        myDroneFleetManager = theDroneFleetManager;
    }


    /** Updates the UI to reflect the latest changes to the drone telemetry data */
    public void updateDroneDisplay() {
        // Updating the Big Stats
        myUI.updateStatsText(myDroneFleetManager.getDroneFleet());
        // Updating the Display
        myUI.refreshDroneDisplay(myDroneFleetManager.getDroneFleet());
    }

    /** Updates the UI to display the current timer */
    public void updateTimer(final int theTime) {
        myUI.updateTime(theTime);
    }
}