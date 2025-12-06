package Model;

import java.util.Objects;

/**
 * AN abstract class that will be implemented drone for the basic logic
 * and functionality for every drone.
 *
 * @version Fall 2025
 */
abstract class AbstractDrone implements Drone {
    /*-- Fields --*/

    /** A Telemetry Data object class used to store all the drone Telemetry data */
    protected final TelemetryData myTelemetryData;

    /** A Battery object class used to handle the drone battery functionality */
    protected final Battery myBattery;

    /** An int that represent the drone individual id */
    protected final int myDroneID;

    /** Represent the drone state */
    protected DroneState myDroneState;

    /*-- Common constants --*/

    /** The battery low threshold to lower down */
    protected static final int BATTERY_LOW_THRESHOLD = 10;

    /** The percentage for a full battery */
    protected static final int BATTERY_FULL = 100;

    /* CONSTRUCTORS */

    /**
     * An abstract constructor that initializes the drone id, and set the drone status to on.
     *
     * @param theID represent the drone unique id.
     */
    protected AbstractDrone(int theID) {
        myDroneID = theID;
        myBattery = new Battery();
        myTelemetryData = new TelemetryData();
        myDroneState = DroneState.INACTIVE;
    }

    @Override
    public int getBatteryLevel() {
        return myBattery.getLevel();
    }

    @Override
    public int getDroneID() {
        return myDroneID;
    }

    @Override
    public TelemetryData getDroneTelemetry() {
        return new TelemetryData(myTelemetryData);
    }

    @Override
    public DroneState isDroneOn() {
        return myDroneState;
    }

    @Override
    public void setDroneState(Enum<DroneState> theDroneState) {
        if (theDroneState == null) {
            throw new NullPointerException("Drone state cannot be null");
        }
        this.myDroneState = (DroneState) theDroneState;
    }

    @Override
    public void setBatteryLevel(int theNewBatteryLevel) {
        if (theNewBatteryLevel <= 0 || theNewBatteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100, got: " + theNewBatteryLevel);
        }
        myBattery.setLevel(theNewBatteryLevel);
    }

    /*-- Abstract methods to be implemented by subclasses --*/

    /**
     * Handles the takeoff state of the drone.
     */
    abstract void handleTakeoffState();

    /**
     * Handles the fly state of the drone.
     *
     * @param theNewTelemetryData represent the new telmetery data that the drone will update next.
     */
    abstract void flyOperation(final TelemetryData theNewTelemetryData);

    /**
     * Handles the landing state of the drone.
     */
    abstract void landingOperation();

    /**
     * Handles the charging state of the drone.
     */
    public void chargingOperation() {
        myTelemetryData.setAltitude(0);
        myTelemetryData.setVelocity(0);
        myBattery.recharge();
        if (myBattery.getLevel() >= BATTERY_FULL) {
            myDroneState = DroneState.TAKEOFF;
        }
    }

    /**
     * Share method to handle the different state of the drone and its course of action base on the current state.
     *
     * @param theNewTelemetryData represent the telemetry data that is going to update the drone telemetry data.
     */
    @Override
    public void updateDroneNextMove(TelemetryData theNewTelemetryData) {
        Objects.requireNonNull(theNewTelemetryData, "Telemetry Data cannot be null");

        // Ensuring that every move, the battery is draining
        myBattery.drain(theNewTelemetryData.getVelocity());

        // Switch statements to handle all the different state
        switch (myDroneState) {
            case INACTIVE:
                myDroneState = DroneState.STARTING;
                break;
            case STARTING:
                myDroneState = DroneState.FLYING;
                flyOperation(theNewTelemetryData);
                break;
            case FLYING:
                flyOperation(theNewTelemetryData);
                break;
            case LANDING:
                landingOperation();
                break;
            case CHARGING:
                chargingOperation();
                break;
            case TAKEOFF:
                handleTakeoffState();
                break;
        }
    }

    /**
     * A method that handles the battery drain simulation.
     */
    public void simulateBatteryDrain() {
        myBattery.drain(myTelemetryData.getVelocity());
    }
}