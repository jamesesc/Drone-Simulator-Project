package Model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class that represents the drone that we're simulating on.
 * Uses another object called Telemetry Data which acts as the storage
 * for the drone Telemetry data.
 * As well, use a battery object that handles the internal battery of
 * the drone object.
 *
 * @author James Escudero
 * @version Fall 2025
 */
public class Drone {

    /* Fields */

    /** Using a thread-safe counter for all created drone objects. */
    private static final AtomicInteger DRONE_COUNTER = new AtomicInteger(1);

    /** A Telemetry Data object class used to store all the drone Telemetry data */
    private TelemetryData myTelemetryData;

    /** A Battery object class used to handle the drone battery functionality */
    private final Battery myBattery;

    /** An int that represent the drone individual id */
    private int myDroneID;

    /** A boolean that represent whether the drone is on or no */
    private boolean myDroneStatus;

    /* CONSTRUCTORS */

    /** A non-arg constructor that initializes the drone id, and set the drone status to on */
    public Drone() {
        // Using Java atomic increments that ensures each drone get unique ID in multi-thread simulation
        myDroneID = DRONE_COUNTER.getAndIncrement();

        myDroneStatus = true;
        myBattery = new Battery();
        myTelemetryData = new TelemetryData();
    }

    /* GETTERS */

    /**
     * A getter to get the drone battery level
     *
     * @return the Battery level of the drone as an int.
     */
    public int getBatteryLevel() {
        return myBattery.getLevel();
    }

    /**
     * A getter to get the drone ID
     *
     * @return the Drone ID as a String.
     */
    public int getDroneID() {
        return myDroneID;
    }

    /**
     * A getter to get the drone Telemetry Data
     *
     * @return the Drone Telemetry Data as a TelemetryData Object.
     */
    public TelemetryData getDroneTelemetry() {
        // Creating a copy of the drone telemetry data
        return new TelemetryData(myTelemetryData);
    }

    /**
     * A getter to get whether the drone is on or not
     *
     * @return the drone status as a boolean.  
     */
    public boolean isDroneOn() {
        return myDroneStatus;
    }


    /* SETTERS */

    /**
     * A setter to set the Drone On status.
     *
     * @param theDroneStatus represent whether the drone is on or not.
     */
    public void setDroneStatus(final boolean theDroneStatus) {
        myDroneStatus = theDroneStatus;
    }

    /**
     * A setter to set the Drone Battery Level.
     *
     * @param theNewBatteryLevel represents the new drone battery level.
     */
    public void setBatteryLevel(final int theNewBatteryLevel) {
        if (theNewBatteryLevel <= 0 || theNewBatteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }

        myBattery.setLevel(theNewBatteryLevel);
    }

    /**
     * A setter to update the drone Telemetry Data
     *
     * @param theNewTelemetryData represent the telemetry data that is going to update the drone telemetry data.
     */
    public void updateTelemetryData(final TelemetryData theNewTelemetryData) {
        myTelemetryData = Objects.requireNonNull(theNewTelemetryData, "Telemetry Data cannot be null");
    }


    /* LOGIC */

    /**
     * A method that handles the battery drain simulation
     */
    public void simulateBatteryDrain() {
        myBattery.drain(myTelemetryData.getVelocity());
    }
}