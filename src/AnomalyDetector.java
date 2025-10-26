import Model.Drone;
import Model.TelemetryData;

import java.util.HashSet;
import java.util.Set;

public class AnomalyDetector {
    /**
     * What constitutes a battery being too low
     */
    final int BATTERY_THRESHOLD = 15;
    /**
     * The threshold for what constitutes a "sudden jump"
     */
    final double JUMP_THRESHOLD = 70.0;
    /**
     * The threshold for what constitutes a "sudden drop"
     */
    final double DROP_THRESHOLD = 50.0;
    /**
     * The threshold for what constitutes a "too sharp turn"
     */
    final double TURN_THRESHOLD = 50.0;
    /**
     * Determines what constitutes going "way too fast"
     */
    final double VELOCITY_THRESHOLD = 50.0;
    /**
     * The angle (>=) which constitutes a drone being upside down.
     * Assumed to be degrees and not radians.
     */
    final double UPSIDE_DOWN = 360.0;
    /**
     * The upper and lower bounds for latitude and longitude.
     * Lower bound latitude, lower bound longitude, upper bound latitude, upper bound longitude.
     */
    final double[] OUT_OF_BOUNDS = {-1000.0, -1000.0, 1000.0, 1000.0};
    /**
     * MARGIN OF ERROR, percentage of current expected needs to pass
     */
    final double TELEPORT_MARGIN_OF_ERROR = 1.5;

    record Location(double lat, double lon) {}

    /**
     * Detects any teleportation of a Drone. This method treats the drone as if
     * it's on a 2D grid, with longitude being x and latitude being y.
     *
     * @param thePrior The prior state of the drone.
     * @param theCurrent The current state of the drone.
     * @param theTimeStep Time difference between thePrior's data and theCurrent's data.
     * @return Whether there was teleportation.
     */
    public boolean detectTeleport(TelemetryData thePrior, TelemetryData theCurrent, double theTimeStep) {
        //Angles to radians
        double prior_angle_radians = Math.toRadians(thePrior.getOrientation());
        //delta x = v * cos(theta) * t, delta y = v * sin(theta) * t
        double dX = thePrior.getVelocity() * Math.cos(prior_angle_radians) * theTimeStep;
        double dY = thePrior.getVelocity() * Math.sin(prior_angle_radians) * theTimeStep;
        //Actual displacement
        double aDX = theCurrent.getLongitude() - thePrior.getLongitude();
        double aDY = theCurrent.getLatitude() - thePrior.getLatitude();
        //Pythagoras type deal
        double expected_distance = Math.sqrt(Math.pow(dX, 2)
                + Math.pow(dY, 2));
        double actual_distance = Math.sqrt(Math.pow(aDX, 2)
                + Math.pow(aDY, 2));
        //Avoiding false positives from zero movement
        double minThreshold = 0.01;

        double allowedDistance = Math.max(expected_distance * TELEPORT_MARGIN_OF_ERROR, minThreshold);

        return actual_distance > allowedDistance;
    }

    /**
     * Whether any drone shares the same longitude and latitude (drones can't be inside each other).
     *
     * @param theDrones The drones we are checking.
     * @return Whether a drone is inside another.
     */
    public boolean detectSharingLocations(Drone[] theDrones) {
        boolean result = false;

        Set<Location> seen = new HashSet<>();

        for (Drone drone : theDrones) {
            Location loc = new Location(
                    drone.getMyDroneTelemetryData().getLatitude(),
                    drone.getMyDroneTelemetryData().getLongitude()
            );

            if (seen.contains(loc)) {
                result = true;
            }
            seen.add(loc);
        }
        return result;
    }


    /**
     * Whether a drone is outside the given bounds.
     *
     * @param theState The state of the drone we're checking.
     * @return Whether the drone is out of bounds.
     */
    public boolean outOfBounds(TelemetryData theState) {
        boolean result = false;

        Location loc = new Location(
                theState.getLatitude(),
                theState.getLongitude()
        );

        if (loc.lon < OUT_OF_BOUNDS[1] || loc.lon > OUT_OF_BOUNDS[3]) {
            result = true;
        }
        if (loc.lat < OUT_OF_BOUNDS[0] || loc.lat > OUT_OF_BOUNDS[2]) {
            result = true;
        }

        return result;
    }
    /**
     * Using a drone's current data, determines whether a drone is flying backwards.
     *
     * @param theState The state of the drone
     * @return Whether the drone is upside down
     */
    public boolean isFlyingBackwards(TelemetryData theState) {
        return theState.getVelocity() < 0;
    }

    /**
     * Using a drone's current data, determines whether a drone is upside down.
     *
     * @param theState The state of the drone.
     * @return Whether the drone is upside down.
     */
    public boolean isUpsideDown(TelemetryData theState) {
        return Math.abs(theState.getOrientation()) >= UPSIDE_DOWN;
    }
    /**
     * Using a drone's prior telemetry data, and it's current telemetry data, determines
     * whether a drone is going way too fast.
     *
     * @param theState The state of the drone.
     * @return Whether the drone is going too fast.
     */
    public boolean detectTooFast(TelemetryData theState) {
        return Math.abs(theState.getVelocity()) >= VELOCITY_THRESHOLD;
    }

    /**
     * Using a drone's prior telemetry data, and it's current telemetry data, determines
     * whether a drone has made a turn that's way too sharp.
     *
     * @param thePriorState Former state of the drone.
     * @param theCurrentState Current state of the drone.
     * @return Whether the drone made a turn that's too sharp.
     */
    public boolean detectSharpTurns(TelemetryData thePriorState, TelemetryData theCurrentState) {
        return Math.abs(theCurrentState.getOrientation() - thePriorState.getOrientation()) >= TURN_THRESHOLD;
    }

    /**
     * Using a drone's prior telemetry data, and it's current telemetry data, determines whether
     * a drone has made a drop/jump that's way too sudden.
     *
     * @param thePriorState Former state of the drone.
     * @param theCurrentState Current state of the drone.
     * @return Whether the drone is too suddenly dropping.
     */
    public boolean detectSuddenDropJump(TelemetryData thePriorState, TelemetryData theCurrentState) {
        double change = theCurrentState.getAltitude() - thePriorState.getAltitude();
        return change <= -DROP_THRESHOLD || change >= JUMP_THRESHOLD;
    }


    /**
     * Detects whether the battery level is low (< BATTERY_THRESHOLD).
     * Returns true if it is low, false otherwise.
     *
     * @param theDrone The Drone whose battery we're checking.
     * @return Whether that drone's battery is low.
     */
    public boolean isBatteryLow(Drone theDrone) {
        return theDrone.getBatteryLevel() <= BATTERY_THRESHOLD;
    }

    /**
     * Detects whether a drone's battery is empty (== 0).
     * Returns true if it is low, false otherwise.
     * WARNING: Method may be superfluous, can delete if needed.
     *
     * @param theDrone The Drone whose battery we're checking.
     * @return Whether that drone's battery is empty.
     */
    public boolean isBatteryEmpty(Drone theDrone) {
        return theDrone.getBatteryLevel() == 0;
    }

    /**
     * Detects whether a drone's battery is negative (< 0).
     * Returns true if it is low, false otherwise.
     * WARNING: Method may be superfluous, can delete if needed.
     *
     * @param theDrone The Drone whose battery we're checking.
     * @return Whether that drone's battery is empty.
     */
    public boolean isBatteryNegative(Drone theDrone) {
        return theDrone.getBatteryLevel() < 0;
    }
}
