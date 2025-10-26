import Model.Drone;
import Model.TelemetryData;

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
