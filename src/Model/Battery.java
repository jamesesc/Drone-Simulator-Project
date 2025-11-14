package Model;

import static Model.Drone.randomNumGen;

public class Battery {

    private static int myBattery;


    /* GETTERS */
    public int getLevel() {
        return myBattery;
    }

    public void setLevel(final int theNewBattery) {
        myBattery = theNewBattery;
    }

    /* METHODS */

    /** Method that randomly weight the drone battery level when first initialize */
    public void initializeBatteryLevel() {
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
