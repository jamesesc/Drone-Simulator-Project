package view;

import Model.AnomalyRecord;
import Model.Drone;
import service.TimerManager;

import java.util.List;

/**
 * Interface for listening to simulation events.
 *
 * @version Autumn 2025
 */
public interface SimulationListener {
    /**
     * Called when the elapsed time is updated.
     *
     * @param theElapsedTime the elapsed time in milliseconds.
     */
    void onTimeUpdate(int theElapsedTime);

    /**
     * Called when there is an update to the drone fleet.
     *
     * @param theFleet the updated array of drones.
     */
    void onDroneUpdate(Drone[] theFleet);

    /**
     * Called when anomalies are detected.
     *
     * @param anomaly the array of detected anomalies.
     */
    void onAnomaliesDetected(AnomalyRecord[] anomaly);

    /**
     * Called when the drone fleet is reloaded.
     *
     * @param theFleet the reloaded array of drones.
     */
    void onFleetReloaded(Drone[] theFleet);

    /**
     * Called when the status of the timer manager changes.
     *
     * @param theStatus the new status of the timer manager.
     */
    void onStatusChanged(TimerManager.Status theStatus);

    /**
     * Called when the menu item "Database Manager" is pressed in the main GUI menu bar.
     *
     * @param theRecords The records that the database manager will show.
     */
    void databaseManagerButtonPushed(List<String[]> theRecords);
}