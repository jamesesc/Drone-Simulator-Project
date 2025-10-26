import Model.Drone;

public class AnomalyDetector {
    /**
     * Detects whether the battery level is low (<15).
     * Returns true if it is low, false otherwise.
     *
     * @param myDrone The Drone whose battery we're checking.
     * @return Whether that drone's battery is low.
     */
    public boolean isBatteryLow(Drone myDrone) {
        return myDrone.getBatteryLevel() < 15;
    }

    /**
     * Detects whether a drone's battery is empty (== 0).
     * Returns true if it is low, false otherwise.
     * WARNING: Method may be superfluous, can delete if needed.
     *
     * @param myDrone The Drone whose battery we're checking.
     * @return Whether that drone's battery is empty.
     */
    public boolean isBatteryEmpty(Drone myDrone) {
        return myDrone.getBatteryLevel() == 0;
    }

    /**
     * Detects whether a drone's battery is negative (< 0).
     * Returns true if it is low, false otherwise.
     * WARNING: Method may be superfluous, can delete if needed.
     *
     * @param myDrone The Drone whose battery we're checking.
     * @return Whether that drone's battery is empty.
     */
    public boolean isBatteryNegative(Drone myDrone) {
        return myDrone.getBatteryLevel() < 0;
    }
}
