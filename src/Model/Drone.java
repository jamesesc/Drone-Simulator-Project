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


    /* SETTERS */

    /**
     * A setter to set the Drone Battery Level.
     *
     * @param theNewBatteryLevel represents the new drone battery level.
     */
    public void setBatteryLevel(final int theNewBatteryLevel) {
        myBattery = theNewBatteryLevel;
    }

    /**
     * A setter to update the drone Telemetry Data
     *
     * @param theNewTelemetryData represent the telemetry data that is going to update the drone telemetry data.
     */
    public void updateTelemetryData(final TelemetryData theNewTelemetryData) {
        myTelemetryData = theNewTelemetryData;
    }


    /* METHODS */

    /** Method to update the drone Batter */
    public void updateDroneBattery() {
        /* Velocity Power => Battery Consumption Chart
        60-100 m/s: High (Cap at 30)
        30-60 m/s: Ranges...
        10-30 m/s: Ranges...
        0-10 m/s: Low (min = 1)
         */

        // Battery Decrease Formula
        int batteryDecrease = (int) (0.005 * Math.pow(myTelemetryData.getVelocity(), 2));

        // Min Cap, and Max Cap
        if (batteryDecrease == 0) {
            batteryDecrease = 1;
        } else if (batteryDecrease > 32) {
            batteryDecrease = 30;
        }
        myBattery -= batteryDecrease;
    }


    /* MAIN METHOD */

    /* Main method to do various testing usage */
    public static void main(String[] args) {
        testUpdate();
    }

    /* Helper method to test the updateBattery */
    private static void testUpdate() {
        Drone[] droneTestArray = new Drone[100];
        for (int i = 0; i < 100; i++) {
            droneTestArray[i] = new Drone();
            droneTestArray[i].setBatteryLevel(i);
            droneTestArray[i].myTelemetryData.setVelocity(i);
        }

        for (Drone drone : droneTestArray) {
            int beforeBatteryLevel = drone.getBatteryLevel();
            System.out.println("BEFORE UPDATE");
            System.out.println("Battery Level: " + beforeBatteryLevel + ", Velocity: " + drone.myTelemetryData.getVelocity());

            drone.updateDroneBattery();

            System.out.println("AFTER UPDATE");
            System.out.println("Battery Decrease: " + (beforeBatteryLevel - drone.getBatteryLevel()));
            System.out.println("Battery Level: " + drone.getBatteryLevel() + ", Velocity: " + drone.myTelemetryData.getVelocity());

            System.out.println("\n\n\n");
        }
    }
}