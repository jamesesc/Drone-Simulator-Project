package Model;

/**
 * Version A of a class that represents the drone that we're simulating on.
 * Uses another object called Telemetry Data which acts as the storage
 * for the drone Telemetry data.
 * As well, use a battery object that handles the internal battery of
 * the drone object.
 *
 * @version Fall 2025
 */
public class DroneA extends AbstractDrone {
    /*-- Constant --*/

    /** The height to reach before switching back to FLYING */
    private static final int SAFE_ALTITUDE = 25;

    /** How fast the drone goes up/down per tick */
    private static final double VERTICAL_SPEED = 30.0;

    /*-- Constructor --*/

    /**
     * Constructor for DroneA to initialize the object.
     *
     * @param theID represents the unique id of the drone.
     */
    public DroneA(int theID) {
        super(theID);
    }


    /*-- Override different drone state behavior --*/

    @Override
    void handleTakeoffState() {
        double newAlt = myTelemetryData.getAltitude() + VERTICAL_SPEED;

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

    @Override
    public void landingOperation() {
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
}