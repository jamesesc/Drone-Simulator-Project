package Model;

/**
 * An interface that defines the external behavior of a Drone object.
 * Implementations must handle telemetry updates, battery interactions,
 * state transitions, and ID tracking.
 *
 * @version Fall 2025
 */
public interface Drone {
    enum DroneState {
        INACTIVE,
        STARTING,   // Used for the beginning of the sim
        FLYING,     // Random movements
        LANDING,    // When the Battery is dead, descend on that spot
        CHARGING,   // It's on the ground and charge to 6 seconds
        TAKEOFF     // Fully Recharged, will fly up to a safe height
    }

    /*-- Getters --*/

    /**
     * A getter to get the drone battery level.
     *
     * @return the Battery level of the drone as an int.
     */
    int getBatteryLevel();

    /**
     * A getter to get the drone ID.
     *
     * @return the Drone ID as a String.
     */
    int getDroneID();

    /**
     * A getter to get the drone Telemetry Data.
     *
     * @return the Drone Telemetry Data as a TelemetryData Object.
     */
    TelemetryData getDroneTelemetry();

    /**
     * A getter to get whether the drone is on or not.
     *
     * @return the drone status as a Enum<DroneState>.
     */
    DroneState isDroneOn();


    /*-- Setters --*/

    /**
     * A setter to set the Drone On status.
     *
     * @param theDroneState represent whether the drone is on or not.
     * @throws NullPointerException if theDroneState is null.
     */
    void setDroneState(Enum<DroneA.DroneState> theDroneState);

    /**
     * A setter to set the Drone Battery Level.
     *
     * @param theNewBatteryLevel represents the new drone battery level.
     * @throws IllegalArgumentException if theNewBatteryLevel is less than 0 or greater than 100.
     */
    void setBatteryLevel(int theNewBatteryLevel);

    /**
     * A setter to update the drone Telemetry Data.
     *
     * @param theNewTelemetryData represent the telemetry data that is going to update the drone telemetry data.
     * @throws NullPointerException if theNewTelemetryData is null.
     */
    void updateDroneNextMove(TelemetryData theNewTelemetryData);

    /*-- Logic --*/

    /**
     * A method that handles the battery drain simulation.
     */
    void simulateBatteryDrain();
}
