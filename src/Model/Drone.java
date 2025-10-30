package Model;

/**
 * A class that represents the drone that we're simulating on.
 * Uses another object called Telemetry Data which acts as the storage
 * for the drone Telemetry data.
 *
 * @author James Escudero
 * @version Fall 2025
 */
public class Drone {

    /* Fields */

    /** A telemetry data object class used to store all the drone Telemetry data */
    private TelemetryData myTelemetryData = new TelemetryData();

    /** An int that is use as drone counter for all created drone objects */
    private static int droneCounter = 0;

    /** An int that represent the drone individual id */
    private int myDroneID;

    /** An int that represents the Battery level the drone has */
    private int myBattery;

    /** A boolean that represent whether the drone is on or no */
    private boolean myIsDroneOn;

    /** Constant that represent the amount of power the Battery */
    private static final int BATTERY_DECREASE = 10;


    /* CONSTRUCTORS */

    /** A non-arg constructor that initializes the drone id, and set the drone status to on */
    public Drone() {
        myDroneID = droneCounter;
        myIsDroneOn = true;
        droneCounter++;
    }

    /**
     * An arg constructor that takes a telemetry data and sets that telemetry data to the drone
     *
     * @param droneTelemetryData represents the wanted telemetry data to assign to the drone.
     */
    public Drone(TelemetryData droneTelemetryData) {
        myTelemetryData = droneTelemetryData;
        myDroneID += 1;
        myIsDroneOn = true;
    }


    /* GETTERS */

    /** A getter to get the drone battery level */
    public int getBatteryLevel() {
        return myBattery;
    }

    /** A getter to get the drone ID */
    public int getDroneID() {
        return myDroneID;
    }

    /** A getter to get the drone Telemetry Data */
    public TelemetryData getDroneTelemetryData() {
        return myTelemetryData;
    }

    /** A getter to get whether the drone is on or not */
    public boolean isDroneOn() {
        return myIsDroneOn;
    }


    /* METHODS */

    /**
     * A setter to set the Drone Battery Level.
     *
     * @param theNewBatteryLevel represents the new drone battery level.
     */
    public void setBatteryLevel(final int theNewBatteryLevel) {
        myBattery = theNewBatteryLevel;
    }

    /** A method to handle in decrementing the battery */
    protected void decrementBattery() {
        myBattery -= BATTERY_DECREASE;
    }

    /**
     * A setter to update the drone Telemetry Data
     *
     * @param theNewTelemetryData represent the telemetry data that is going to update the drone telemetry data.
     */
    public void updateTelemetryData(final TelemetryData theNewTelemetryData) {
        myTelemetryData = theNewTelemetryData;
    }
}