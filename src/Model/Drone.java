package Model;

import java.util.Random;

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

    /** Random generator to help generate random battery level */
    private static final Random randomNumGen = new Random();


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
    public TelemetryData getDroneTelemetry() {
        return myTelemetryData;
    }

    /** A getter to get whether the drone is on or not */
    public boolean isDroneOn() {
        return myIsDroneOn;
    }


    /* SETTERS */

    /**
     * A setter to set the Drone On status.
     *
     * @param theDroneStatus represent whether the drone is on or not.
     */
    public void setMyIsDroneOn(final boolean theDroneStatus) {
        myIsDroneOn = theDroneStatus;
    }

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

    /** Method that randomly weight the drone battery level when first initialize */
    private void initializeDroneSetting() {
        // The range that battery can randomly generate
        final int[][] batteryLevelRange = {
                {0, 19}, // Low
                {20, 49}, // Medium
                {50, 100} // Full
        };

        // The probability for each range to occur (related to the above array)
        final int[] probBatteryLevel = {1, 14, 85};

        // Represent the total probability (should be 100)
        int totalProb = 0;

        // Loop through the probability array and add up the probability
        for (int probabilityWeight : probBatteryLevel) {
            totalProb += probabilityWeight;
        }

        // Choosing a random number between the total probability
        int randomLevel = randomNumGen.nextInt(totalProb);
        // Used to know which range we are in that we randomly generated
        int selectedRangeIndex = 0;

        /* Going through a probability array, subtracting the prob level until its below 0
         and assign rangeIndex to the range we found*/
        for (int i = 0; i < probBatteryLevel.length; i++) {
            randomLevel -= probBatteryLevel[i];

            if (randomLevel < 0) {
                selectedRangeIndex = i;
                break;
            }
        }

        // Storing the range min and max
        int min = batteryLevelRange[selectedRangeIndex][0];
        int max = batteryLevelRange[selectedRangeIndex][1];

        // Base on the right category, just choose a random num between those bounds
        // +1 because random(19) only includes 0-18... so we need to be one higher
        int batterLevel = min + randomNumGen.nextInt(max - min + 1); // Inclusive

        // Setting the battery level to the new generated batter level
        setBatteryLevel(batterLevel);
    }

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
        // Testing Decrement
        // testUpdate();


    }

    /* Helper method to test the updateBattery, seeing the decrease of battery for each level */
    private static void testUpdate() {
        // An array of drones to test on
        Drone[] droneTestArray = new Drone[100];

        /* Looping through the array, initializing the drone, battery level, velocity and then seeing the difference
        when we called the updateBatterLevel */
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


    private static void testBatteryProb() {
        // Storing all ranges categories to count them up
        int low = 0, medium = 0, high = 0;
        // The number of tests to do
        int testNum = 1000000;

        /* Looping through until the numTest.
          For each loop, generate a new drone, and see what batter level it generated.
          From there, add it up to the appropriate category it belongs.
          */
        for (int i = 0; i < testNum; i++) {
            Model.Drone droneTest = new Model.Drone();

            // Seeing the individual battery level for each new drone
            // System.out.println(newTest.getBatteryLevel());

            // Storing the current drone battery level
            int batteryLvl = droneTest.getBatteryLevel();

            // Assigning the new generated batter to its correct range
            if (batteryLvl < 20) {
                low++;
            } else if (batteryLvl < 50) {
                medium++;
            } else {
                high++;
            }
        }

        // Printing the probability to see if it's right
        System.out.println("Low (0-19)   " + low + "  =  " + String.format("%.1f", low * 100.0 / testNum) + "%");
        System.out.println("Medium (20-49):        " + medium + "  =  " + String.format("%.1f", medium * 100.0 / testNum) + "%");
        System.out.println("High (50 - 100):   " + high + "  =  " + String.format("%.1f", high * 100.0 / testNum) + "%");
    }
}