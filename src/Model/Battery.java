package Model;

import java.util.Random;

/**
 * A battery object class that has a composite relationship with the drone object.
 * It is used to handle all the functionality and logic for the drone battery.
 *
 * @version Fall 2025
 */
public class Battery {
    /*-- Fields --*/

    /** Random generator to help generate random battery level */
    private static final Random randomNumGen = new Random();

    /** Represent the Battery Level of the Drone */
    private int myBatteryLevel;

    /** Represent the recharge batter level */
    private static final int RECHARGE_RATE = 15;


    /*-- Constant --*/

    /** The multiplier use to calculate the drain based on velocity */
    private static final double DRAIN_COEFFICIENT = 0.005;

    /** The exponent used to represent the square velocity */
    private static final double VELOCITY_EXPONENT = 2.0;

    /** The max amount a battery can drain in a single update*/
    private static final int MAX_DRAIN_AMOUNT = 30;

    /** The min amount a battery can drain in a single update */
    private static final int MIN_DRAIN_AMOUNT = 1;

    /** The absolute max battery level */
    private static final int MAX_BATTERY_LEVEL = 100;

    /** The absolute min battery level */
    private static final int MIN_BATTERY_LEVEL = 0;

    /**
     * AN enum that represent the various battery level.
     */
    public enum BatteryLevel {
        /*-- Enum Types of Battery Level and its respective probability --*/

        /** Low battery level: 0-19%, with a 1% probability. */
        LOW(0, 19, 1),

        /** Medium battery level: 20-49%, with a 14% probability. */
        MEDIUM(20, 49, 14),

        /** Full battery level: 50-100%, with an 85% probability. */
        FULL(50, 100, 85);

        /*-- Fields --*/

        /** Represent the min battery level */
        private final int myMin;

        /** Represent the max battery level */
        private final int myMax;

        /** Represent the probability of that range */
        private final int myProbability;

        /**
         * Constructor to initialize the Battery Level Enum.
         *
         * @param theMin represent the min battery level.
         * @param theMax represent the max battery level.
         * @param theProbability represent the probability for that battery level range.
         */
        BatteryLevel(final int theMin, final int theMax, final int theProbability) {
            myMin = theMin;
            myMax = theMax;
            myProbability = theProbability;
        }

        /**
         * Getter method to get the min battery level.
         *
         * @return an int of the min battery level.
         */
        public int getMin() {
            return myMin;
        }

        /**
         * Getter method to get the max battery level.
         *
         * @return ant int of the max battery level.
         */
        public int getMax() {
            return myMax;
        }

        /**
         * Getter method to get the probability percentage of the battery level ranges.
         *
         * @return an int of the probability percent of the level range.
         */
        public int getProbability() {
            return myProbability;
        }
    }

    /*-- Constructor --*/

    /**
     * Emptiness constructor for the Battery Object.
     * It first initializes starting battery level.
     */
    public Battery() {
        initializeBatteryLevel();
    }


    /*-- Getters --*/

    /** Getter method to get the battery level */
    public int getLevel() {
        return myBatteryLevel;
    }


    /*-- Setters --*/

    /**
     * Setter method to set the level of the battery level
     *
     * @param theNewBatteryLevel is the new battery level that were setting battery level to.
     */
    public void setLevel(final int theNewBatteryLevel) {
        if (theNewBatteryLevel < MIN_BATTERY_LEVEL || theNewBatteryLevel > MAX_BATTERY_LEVEL) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }

        myBatteryLevel = theNewBatteryLevel;
    }


    /*-- Logic --*/

    /**
     * Recharge the battery by setting it back to 100.
     */
    public void recharge() {
        myBatteryLevel += RECHARGE_RATE;

        // Ensure we don't exceed 100%
        if (myBatteryLevel > MAX_BATTERY_LEVEL) {
            myBatteryLevel = MAX_BATTERY_LEVEL;
        }
    }

    /**
     * Method that randomly weight the drone battery level when first initialize
     */
    private void initializeBatteryLevel() {
        // Represent the total probability (should be 100)
        int totalProb = 0;

        // Loop through the probability array and add up the probability
        for (BatteryLevel level : BatteryLevel.values()) {
            totalProb += level.getProbability();
        }

        // Choosing a random number between the total probability
        int randomLevel = randomNumGen.nextInt(totalProb);
        // Used to know which range we are in that we randomly generated
        int selectedRangeIndex = 0;

        /* Going through a probability array, subtracting the prob level until its below 0
         and assign rangeIndex to the range we found*/
        for (int i = 0; i < BatteryLevel.values().length; i++) {
            BatteryLevel level = BatteryLevel.values()[i];
            randomLevel -= level.getProbability();

            if (randomLevel < 0) {
                selectedRangeIndex = i;
                break;
            }
        }

        // Storing the range min and max
        BatteryLevel selectedLevel = BatteryLevel.values()[selectedRangeIndex];
        int min = selectedLevel.getMin();
        int max = selectedLevel.getMax();

        // Base on the right category, we just choose a random num between those bounds
        // +1 because random(19) only includes 0-18... so we need to be one higher
        int batteryLevel = min + randomNumGen.nextInt(max - min + 1); // Inclusive

        // Setting the battery level to the new generated batter level
        setLevel(batteryLevel);
    }

    /**
     * Method to update the drone Battery base on the velocity of the drone.
     *
     * @param theVelocity is the velocity of the drone.
     */
    public void drain(final double theVelocity) {
        /* Velocity Power => Battery Consumption Chart
        60-100 m/s: High (Cap at 30)
        30-60 m/s: Ranges...
        10-30 m/s: Ranges...
        0-10 m/s: Low (min = 1)
         */

        // Battery Decrease Formula
        int batteryDecrease = (int) (DRAIN_COEFFICIENT * Math.pow(theVelocity, VELOCITY_EXPONENT));

        // Min Cap, and Max Cap
        if (batteryDecrease == 0) {
            batteryDecrease = MIN_DRAIN_AMOUNT;
        } else if (batteryDecrease > MAX_DRAIN_AMOUNT) {
            batteryDecrease = MAX_DRAIN_AMOUNT;
        }
        myBatteryLevel -= batteryDecrease;

        if (myBatteryLevel < MIN_BATTERY_LEVEL) {
            myBatteryLevel = MIN_BATTERY_LEVEL;
        }
    }
}