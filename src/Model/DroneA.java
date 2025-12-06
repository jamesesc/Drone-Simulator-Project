package Model;

import java.util.Objects;

/**
 * Version A of a class that represents the drone that we're simulating on.
 * Uses another object called Telemetry Data which acts as the storage
 * for the drone Telemetry data.
 * As well, use a battery object that handles the internal battery of
 * the drone object.
 *
 * @version Fall 2025
 */
public class DroneA implements Drone {
    /*-- Fields --*/

    /** A Telemetry Data object class used to store all the drone Telemetry data */
    private final TelemetryData myTelemetryData;

    /** A Battery object class used to handle the drone battery functionality */
    private final Battery myBattery;

    /** An int that represent the drone individual id */
    private final int myDroneID;

    /** Represent the drone state */
    private DroneState myDroneState;


    /*-- Constants for the logic --*/

    /** The height to reach before switching back to FLYING */
    private static final int SAFE_ALTITUDE = 25;

    /** How fast the drone goes up/down per tick */
    private static final double VERTICAL_SPEED = 30.0;

    /** The battery low threshold to lower down */
    private static final int BATTERY_LOW_THRESHOLD = 10;

    /** The percentage for a full battery */
    private static final int BATTERY_FULL = 100;


    /* CONSTRUCTORS */

    /**
     * A non-arg constructor that initializes the drone id, and set the drone status to on.
     */
    public DroneA(final int theID) {
        // Using Java atomic increments that ensures each drone get unique ID in multi-thread simulation
        myDroneState = DroneState.INACTIVE;
        myBattery = new Battery();
        myTelemetryData = new TelemetryData();
        myDroneID = theID;
    }

    /*-- Getters --*/

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
        // Creating a copy of the drone telemetry data
        return new TelemetryData(myTelemetryData);
    }

    @Override
    public DroneState isDroneOn() {
        return myDroneState;
    }


    /*-- Setters --*/

    @Override
    public void setDroneState(final Enum<DroneState> theDroneState) {
        if (theDroneState == null) {
            throw new NullPointerException("Drone state cannot be null");
        }

        myDroneState = (DroneState) theDroneState;
    }

    @Override
    public void setBatteryLevel(final int theNewBatteryLevel) {
        if (theNewBatteryLevel <= 0 || theNewBatteryLevel > 100) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100, got: " + theNewBatteryLevel);
        }

        myBattery.setLevel(theNewBatteryLevel);
    }

    @Override
    public void updateDroneNextMove(final TelemetryData theNewTelemetryData) {
        Objects.requireNonNull(theNewTelemetryData, "Telemetry Data cannot be null");

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


    /*-- Helper methods for each action --*/

    /**
     * Handles the flying operation of the drone.
     *
     * @param theTelemetryData is the new updated telemetry data.
     */
    private void flyOperation(final TelemetryData theTelemetryData) {
        // Assigning the new generated telemetry data to the drone
        myTelemetryData.setLatitude(theTelemetryData.getLatitude());
        myTelemetryData.setLongitude(theTelemetryData.getLongitude());
        myTelemetryData.setOrientation(theTelemetryData.getOrientation());
        myTelemetryData.setAltitude(theTelemetryData.getAltitude());
        myTelemetryData.setVelocity(theTelemetryData.getVelocity());

        // When flying, check if the batter level isn't below 10, if so, switch to charge mode
        if (myBattery.getLevel() <= BATTERY_LOW_THRESHOLD) {
            myDroneState = DroneState.LANDING;
        }
    }

    /**
     * Handles the landing operation of the drone.
     */
    private void landingOperation() {
        // Decreasing alt by the current altitude
        double newAlt = myTelemetryData.getAltitude() - VERTICAL_SPEED;

        // Safety pro-cation that drone land on 0, nothing more
        if (newAlt <= 0) {
            newAlt = 0;
            myDroneState = DroneState.CHARGING;
            myTelemetryData.setVelocity(0);
        }

        // Only updating the altitude and making the velocity
        myTelemetryData.setAltitude(newAlt);
        myTelemetryData.setVelocity(0);
    }

    /**
     * Handles the charging phase of the drone.
     */
    private void chargingOperation() {
        // Ensuring that the drone stay on the drone
        myTelemetryData.setAltitude(0);
        myTelemetryData.setVelocity(0);

        myBattery.recharge();

        // Checking if the time passed since charging, if so, change into takeoff
        if (myBattery.getLevel() >= BATTERY_FULL) {
            myDroneState = DroneState.TAKEOFF;
        }
    }

    /**
     * Handles the taking off the drone.
     */
    private void handleTakeoffState() {
        double newAlt = myTelemetryData.getAltitude() + VERTICAL_SPEED;

        // Check if we reached safe height
        if (newAlt >= SAFE_ALTITUDE) {
            newAlt = SAFE_ALTITUDE;
            myDroneState = DroneState.FLYING;
        }

        myTelemetryData.setAltitude(newAlt);
        myTelemetryData.setVelocity(VERTICAL_SPEED);
    }


    /*-- Logic --*/

    /**
     * A method that handles the battery drain simulation.
     */
    public void simulateBatteryDrain() {
        myBattery.drain(myTelemetryData.getVelocity());
    }
}