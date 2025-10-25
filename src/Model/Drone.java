package Model;

public class Drone {
    private TelemetryData myDroneTelemetryData = new TelemetryData();

    private int myBattery;

    private int myDroneID;

    private boolean myIsDroneOn;

    private static final int BATTERY_DECREASE = 10;

    public Drone() {
        myDroneTelemetryData = null;
        myDroneID += 1;
        myIsDroneOn = true;

    }

    public Drone(TelemetryData droneTelemetryData) {
        myDroneTelemetryData = droneTelemetryData;
        myDroneID += 1;
        myIsDroneOn = true;
    }

    /* GETTERS */

    public int getBatteryLevel() {
        return myBattery;
    }

    public int getDroneID() {
        return myDroneID;
    }


    /* METHODS */

    public void setBatteryLevel(final int theNewBatteryLevel) {
        myBattery = theNewBatteryLevel;
    }

    protected void decrementBattery() {
        myBattery -= BATTERY_DECREASE;
    }

    public boolean isDroneOn() {
        return myIsDroneOn;
    }
}