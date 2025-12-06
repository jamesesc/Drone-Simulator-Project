package Model;

import java.util.Random;


/**
 * A class that represents the drone that we're simulating on.
 * Uses another object called Telemetry Data which acts as the storage
 * for the drone Telemetry data.
 * As well, use a battery object that handles the internal battery of
 * the drone object.
 *
 * @version Fall 2025
 */
public class DroneB extends AbstractDrone {
    /*-- Constant --*/

    /** The height to reach before switching back to FLYING */
    private static final int SAFE_ALTITUDE = 30;

    /** How fast the drone goes up/down per tick */
    private static final double VERTICAL_SPEED = 15.0;

    /*-- Fields --*/

    /** Random object to use in class */
    private final Random myRdn = new Random();

    /** Represents the drones hesitations to move **/
    private boolean myHesitation = false;

    /**
     * Constructor for DroneB to initialize the object.
     *
     * @param theID represent the unique id of the drone.
     */
    public DroneB(int theID) {
        super(theID);
    }


    /*-- Override different drone state behavior --*/

    @Override
    public void handleTakeoffState() {
        double newAlt = myTelemetryData.getAltitude();
        if (myHesitation) {
            newAlt = myTelemetryData.getAltitude() + VERTICAL_SPEED;
        }

        myHesitation = !myHesitation;

        // Check if we reached safe height
        if (newAlt >= SAFE_ALTITUDE) {
            newAlt = SAFE_ALTITUDE;
            myDroneState = DroneState.FLYING;
        }

        myTelemetryData.setAltitude(newAlt);
        myTelemetryData.setVelocity(VERTICAL_SPEED);
    }


    @Override
    public void flyOperation(final TelemetryData theTelemetryData) {
        // Assigning the new generated telemetry data to the drone
        if (myHesitation) {
            myTelemetryData.setLatitude(theTelemetryData.getLatitude());
            myTelemetryData.setLongitude(theTelemetryData.getLongitude());
            myTelemetryData.setOrientation(theTelemetryData.getOrientation());
            myTelemetryData.setAltitude(theTelemetryData.getAltitude());
            myTelemetryData.setVelocity(theTelemetryData.getVelocity());
        }

        myHesitation = !myHesitation;

        // When flying, check if the battery level isn't below 10, if so, switch to charge mode
        if (myBattery.getLevel() <= BATTERY_LOW_THRESHOLD) {
            myDroneState = DroneState.LANDING;
        }
    }

    @Override
    public void landingOperation() {
        // Decreasing alt by the current altitude
        double newAlt = myTelemetryData.getAltitude() - VERTICAL_SPEED - myRdn.nextDouble(0.0, VERTICAL_SPEED);

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
}