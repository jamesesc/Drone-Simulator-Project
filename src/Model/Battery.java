package Model;

import java.util.Random;

/**
 * A battery object class that has a composite relationship with the drone object.
 * It is used to handle all the functionality and logic for the drone battery.
 *
 * @author James Escudero
 * @version Autumn 2025
 */
public class Battery {

    /* FIELDS */

    /** Random generator to help generate random battery level */
    private static final Random randomNumGen = new Random();

    /** Represent the Battery Level of the Drone */
    private int myBattery;


    /* CONSTANTS */

    /** The multiplier use to calculate the drain based on velocity */
    private static final double DRAIN_COEFFICIENT = 0.005;

    /** The exponent used to represent the square velocity */
    private static final double VELOCITY_COEFFICIENT = 2.0;

    /** The max amount a battery can drain in a single update*/
    private static final int MAX_DRAIN_AMOUNT = 30;

    /** The min amount a battery can drain in a single update */
    private static final int MIN_DRAIN_AMOUNT = 1;

    /** The absolute max battery level */
    private static final int MAX_BATTERY_LEVEL = 100;

    /** The absolute min battery level */
    private static final int MIN_BATTERY_LEVEL = 0;

    /* The range that battery can randomly generate */
    private static final int[][] BATTERY_LEVEL_RANGE = {
            {0, 19}, // Low
            {20, 49}, // Medium
            {50, 100} // Full
    };

    /** The probability for each range to occur (related to BATTERY_LEVEL_RANGE) */
    private static final int[] PROB_BATTERY_LEVEL = {1, 14, 85};


    /* CONSTRUCTOR */

    /**
     * Emptiness constructor for the Battery Object.
     * It first initializes starting battery level.
     */
    public Battery() {
        initializeBatteryLevel();
    }


    /* GETTERS */

    /** Getter method to get the battery level */
    public int getLevel() {
        return myBattery;
    }


    /* SETTER */

    /**
     * Setter method to set the level of the battery level
     *
     * @param theNewBatteryLevel is the new battery level that were setting battery level to.
     */
    public void setLevel(final int theNewBatteryLevel) {
        if (theNewBatteryLevel <= MIN_BATTERY_LEVEL || theNewBatteryLevel > MAX_BATTERY_LEVEL) {
            throw new IllegalArgumentException("Battery level must be between 0 and 100");
        }

        myBattery = theNewBatteryLevel;
    }

    /* LOGIC */

    /** Method that randomly weight the drone battery level when first initialize */
    private void initializeBatteryLevel() {
        // Represent the total probability (should be 100)
        int totalProb = 0;

        // Loop through the probability array and add up the probability
        for (int probabilityWeight : PROB_BATTERY_LEVEL) {
            totalProb += probabilityWeight;
        }

        // Choosing a random number between the total probability
        int randomLevel = randomNumGen.nextInt(totalProb);
        // Used to know which range we are in that we randomly generated
        int selectedRangeIndex = 0;

        /* Going through a probability array, subtracting the prob level until its below 0
         and assign rangeIndex to the range we found*/
        for (int i = 0; i < PROB_BATTERY_LEVEL.length; i++) {
            randomLevel -= PROB_BATTERY_LEVEL[i];

            if (randomLevel < 0) {
                selectedRangeIndex = i;
                break;
            }
        }

        // Storing the range min and max
        int min = BATTERY_LEVEL_RANGE[selectedRangeIndex][0];
        int max = BATTERY_LEVEL_RANGE[selectedRangeIndex][1];

        // Base on the right category, just choose a random num between those bounds
        // +1 because random(19) only includes 0-18... so we need to be one higher
        int batterLevel = min + randomNumGen.nextInt(max - min + 1); // Inclusive

        // Setting the battery level to the new generated batter level
        setLevel(batterLevel);
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
        int batteryDecrease = (int) (DRAIN_COEFFICIENT * Math.pow(theVelocity, VELOCITY_COEFFICIENT));

        // Min Cap, and Max Cap
        if (batteryDecrease == 0) {
            batteryDecrease = MIN_DRAIN_AMOUNT;
        } else if (batteryDecrease > MAX_DRAIN_AMOUNT) {
            batteryDecrease = MAX_DRAIN_AMOUNT;
        }
        myBattery -= batteryDecrease;

        if (myBattery < MIN_BATTERY_LEVEL) {
            myBattery = MIN_BATTERY_LEVEL;
        }
    }
}