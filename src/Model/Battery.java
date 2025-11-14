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
    static final Random randomNumGen = new Random();

    /** Represent the Battery Level of the Drone */
    private int myBattery;


    /* CONSTANTS */

    /* The range that battery can randomly generate */
    static final int[][] BATTERY_LEVEL_RANGE = {
            {0, 19}, // Low
            {20, 49}, // Medium
            {50, 100} // Full
    };

    /** The probability for each range to occur (related to BATTERY_LEVEL_RANGE) */
    static final int[] PROB_BATTERY_LEVEL = {1, 14, 85};


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

    /** Setter method to set the level of the battery level */
    public void setLevel(final int theNewBattery) {
        myBattery = theNewBattery;
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

    /** Method to update the drone Batter */
    public void drain(final double theVelocity) {
        /* Velocity Power => Battery Consumption Chart
        60-100 m/s: High (Cap at 30)
        30-60 m/s: Ranges...
        10-30 m/s: Ranges...
        0-10 m/s: Low (min = 1)
         */

        // Battery Decrease Formula
        int batteryDecrease = (int) (0.005 * Math.pow(theVelocity, 2));

        // Min Cap, and Max Cap
        if (batteryDecrease == 0) {
            batteryDecrease = 1;
        } else if (batteryDecrease > 32) {
            batteryDecrease = 30;
        }
        myBattery -= batteryDecrease;
    }
}
