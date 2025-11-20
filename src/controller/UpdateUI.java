package controller;

//import view.MyJavaFXApp;

import view.MonitorDash;

/**
 * A helper class that helps update the UI with certain info.
 *
 * @author James Escudero
 * @version Autumn 2025
 */
public class UpdateUI {
    /** Represents the instance object of the class */
    private static UpdateUI instance;

    /** Private constructor with nothing in it */
    private UpdateUI() {}

    /** Method to help ensures there's only one instance of the object */
    public static UpdateUI getInstance() {
        if (instance == null) {
            instance = new UpdateUI();
        }
        return instance;
    }

    /** Method that updates the UI with the drone telemetry data */
    public void updateDroneTelemetry() {
        System.out.println("Working: UpdateUI - updateDroneTelemetry");
        DroneFleetManger fleetManager = DroneFleetManger.getInstance();
        MonitorDash.getInstance().updateStatsText(fleetManager.getSpecificDrone(0));
    }

    //TODO: Update the UI with the timer display
    public void updateTimer(final int theTime) {
        MonitorDash.getInstance().updateTime(theTime);
    }
}