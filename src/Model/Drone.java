package Model;

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

    /** A Telemetry Data object class used to store all the drone Telemetry data */
    private TelemetryData myTelemetryData = new TelemetryData();

    /** A Battery object class used to handle the drone battery functionality */
    private final Battery myBattery = new Battery();

    /** An int that is use as drone counter for all created drone objects */
    private static int myDroneCounter = 0;

    /** An int that represent the drone individual id */
    private int myDroneID;

    /** A boolean that represent whether the drone is on or no */
    private boolean myDroneStatus;

    /* CONSTRUCTORS */

    /** A non-arg constructor that initializes the drone id, and set the drone status to on */
    public Drone() {
        myDroneID = myDroneCounter;
        myDroneCounter++;
        myDroneStatus = true;
    }

    /**
     * An arg constructor that takes a telemetry data and sets that telemetry data to the drone
     *
     * @param droneTelemetryData represents the wanted telemetry data to assign to the drone.
     */
    public Drone(TelemetryData droneTelemetryData) {
        super();
        myTelemetryData = droneTelemetryData;
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
        return myTelemetryData;
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
        myBattery.setLevel(theNewBatteryLevel);
    }

    /**
     * A setter to update the drone Telemetry Data
     *
     * @param theNewTelemetryData represent the telemetry data that is going to update the drone telemetry data.
     */
    public void updateTelemetryData(final TelemetryData theNewTelemetryData) {
        myTelemetryData = theNewTelemetryData;
    }


    /* LOGIC */

    /**
     * A method that handles the battery drain simulation
     */
    public void simulateBatteryDrain() {
        myBattery.drain(myTelemetryData.getVelocity());
    }
}