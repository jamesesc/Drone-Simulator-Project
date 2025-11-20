package controller;

import Model.TelemetryData;

import java.util.*;

public class TelemetryGenerator {

    private static final Random randomNumGenerator = new Random();

    public TelemetryData generateTelemetryData(final TelemetryData currentTelemetry) {
        final TelemetryData telemetryData = new TelemetryData();
        generateAll(telemetryData, currentTelemetry);
        return telemetryData;
    }

    private void generateAll(final TelemetryData theTelemetryData, TelemetryData currentTelemetry) {
        generateLatitude(theTelemetryData, currentTelemetry);
        generateLongitude(theTelemetryData, currentTelemetry);
        generateAltitude(theTelemetryData, currentTelemetry);
        generateOrientation(theTelemetryData, currentTelemetry);
        generateVelocity(theTelemetryData, currentTelemetry);
    }


    public TelemetryData generateStartPosition() {
        // The Area where it can spawn is in a circle

        TelemetryData returnTelemetry = new TelemetryData();

        int radius = 50; // 50 Feet

        int spawnArea = (int) (Math.PI * Math.pow(radius, 2));

        double randomAngle = randomNumGenerator.nextDouble() * 2 * Math.PI; // For 0 to 2pi raidens
        double randomDistance = randomNumGenerator.nextDouble() * 50; // 0 to 50 units

        double latOffset = randomDistance * Math.cos(randomAngle);
        double longOffset = randomDistance * Math.sin(randomAngle);

        returnTelemetry.setLatitude(latOffset);
        returnTelemetry.setLongitude(longOffset);

        return returnTelemetry;
    }


    // latitude = y, Vertical
    // longitude = x, Horizontal

    private void generateLatitude(final TelemetryData theTelemetryData, final TelemetryData theCurrentTelemetry) {
        // Converting the drone current orientation to radians
        double orientationInRadians = Math.toRadians(theCurrentTelemetry.getOrientation());

        // Calculate how much we moved vertically (aka the Y-axis)
        double changeInY = theCurrentTelemetry.getVelocity() * Math.cos(orientationInRadians);

        // Adding the change to the old Position to the new TelemetryData output
        theTelemetryData.setLatitude(theCurrentTelemetry.getLatitude() + changeInY);
    }

    private void generateLongitude(final TelemetryData theTelemetryData, final TelemetryData theCurrentTelemetry) {
        double orientationInRadians = Math.toRadians(theCurrentTelemetry.getOrientation());

        // Calculate how much we moved vertically (aka the x-axis)
        double changeInX = theCurrentTelemetry.getVelocity() * Math.sin(orientationInRadians);

        theTelemetryData.setLongitude(theCurrentTelemetry.getLongitude() + changeInX);
    }

    public TelemetryData generateStartAltitude() {
        TelemetryData droneSetupTelemetry = new TelemetryData();

        // attitude generates between 25-75 feet.
        int altitude = randomNumGenerator.nextInt(51) + 25; // 51 = 75 - 25 + 1
        droneSetupTelemetry.setAltitude(altitude);

        return droneSetupTelemetry;
    }

    private void generateAltitude(final TelemetryData theNewTelemetry, final TelemetryData theCurrentTelemetry) {
        double velocity = theCurrentTelemetry.getVelocity();
        if (velocity <= 0) {
            theNewTelemetry.setAltitude(theCurrentTelemetry.getAltitude());
            return;
        }

        // Define magnitude buckets (always positive)
        List<int[]> magnitudeBuckets = Arrays.asList(
                new int[]{0, 15},    // Common
                new int[]{16, 45},   // Occasion
                new int[]{46, 90}    // Rare
        );

        int[] weights = {80, 17, 3}; // Common, Occasion, Rare

        // Weighted random selection
        int totalWeight = 100; // 80+17+3
        int randomValue = randomNumGenerator.nextInt(totalWeight);
        int selectedIndex = 0;
        for (int w : weights) {
            randomValue -= w;
            if (randomValue < 0) break;
            selectedIndex++;
        }

        // getting the magnitude range
        int[] range = magnitudeBuckets.get(selectedIndex);
        int minMag = range[0];
        int maxMag = range[1];

        // int represents the random generated magnitude
        int magnitude = minMag + randomNumGenerator.nextInt(maxMag - minMag + 1);

        // Randomly choosing to either climb (+) or dive (-), EXCEPT: if magnitude == 0 then we stay level
        int angleDegrees;
        if (magnitude == 0) {
            angleDegrees = 0;
        } else {
            boolean climb = randomNumGenerator.nextBoolean();
            angleDegrees = climb ? magnitude : -magnitude;
        }

        // Compute altitude change
        double altitudeChange = velocity * Math.sin(Math.toRadians(angleDegrees));
        double newAltitude = theCurrentTelemetry.getAltitude() + altitudeChange;

        theNewTelemetry.setAltitude(newAltitude);
    }

    private void generateOrientation(final TelemetryData theNewTelemetry, final TelemetryData theCurrentTelemetry) {
        /* Velocity Structure
        -45 <-> +45 = 70% (Common)
        -135 ~ -46 and +46 ~ +135 = 25% (Occasion)
        -180 ~ -136 and +136 ~ +180 = 5% (Rare)
        */

        // Storing the current orientation
        int currentOrientation = (int) theCurrentTelemetry.getOrientation();

        // A list of int arrays for each range velocity category
        List<int[]> turnBuckets = new ArrayList<>();

        // Adding the range velocity interval
        turnBuckets.add(new int[] {0, 45});    // -+45(Common)
        turnBuckets.add(new int[] {46, 135});   // +-46~135 (Occasion)
        turnBuckets.add(new int[] {136, 180});  // +-136~180 (Rare)

        // Orientation Weight for each new radius
        int[] orientationWeightList = {70, 25, 5};

        int totalWeight = 0;

        // Totaling up the total totalWeight
        for (int weight : orientationWeightList) {
            totalWeight += weight;
        }

        int randomValue = randomNumGenerator.nextInt(totalWeight);
        int indexCounter = 0;


        for (int weight : orientationWeightList) {
            randomValue -= weight;
            if (randomValue < 0) {
                break;
            }
            indexCounter++;
        }

        // We have our new section
        int[] newTurn = turnBuckets.get(indexCounter);
        int minTurn = newTurn[0];
        int maxTurn = newTurn[1];

        int direction = minTurn + randomNumGenerator.nextInt(maxTurn - minTurn + 1);

        boolean whatTurn = randomNumGenerator.nextBoolean();

        // ternary that takes turnRight var, and see if oes right to leave as on the true statement
        int turn = whatTurn ? direction : -direction;

        int newOrientation = currentOrientation + turn;

        newOrientation = (((newOrientation % 360) + 360) % 360);

        // Assigning new Velocity to the newTelemetry
        theNewTelemetry.setOrientation(newOrientation);
    }

    /**
     * Generates the velocity of the drone base on the current velocity with
     * various realistic to imitate realism of the drone behavior.
     *
     * @param theNewTelemetry represent the new Telemetry data to return.
     * @param theCurrentTelemetry represent the current Telemetry data.
     */
    private void generateVelocity(final TelemetryData theNewTelemetry, final TelemetryData theCurrentTelemetry) {
        /* Drone Velocity Structure:
        Range: 0-50 m/s
        Categories
          46-50 m/s: Hyper
          31-45 m/s: Fast
          16-30 m/s: Cruising
          6-15 m/s: Slow
          0-5 m/s: Leisurely
        */

        /* Pseudocode:
            Store current velocity.
            Safe check that current velocity is within the limits
            Search through speedBucket to figure out which category does currentVelocity belong to.
                After finding, return a string of the category.
            The string is used a key in a hashmap to figure out the weighted probability for the next velocity.
            Store the right list of velocity probability.
            Added up all the probability.
            Generate a random number within that added probability.
            Then figure out which category that new velocity it is.
            Then within the category, figure out base on a triangular distribution of the velocity
         */

        // Storing the current velocity of the drone (will be used to determine the next velocity)
        final double currentVelocity = theCurrentTelemetry.getVelocity();

        // Safe check that the currentVelocity is with in 0 and 50
        final double safeVelocity = Math.max(0, Math.min(50, currentVelocity));

        // String var to
        String rangeKey = null;

        // A list of int arrays for each range velocity category
        List<int[]> speedBuckets = new ArrayList<>();

        // Adding the range velocity interval
        speedBuckets.add(new int[] {0, 5});    // 0-5 m/s: Leisurely
        speedBuckets.add(new int[] {6, 15});   // 6-15 m/s: Slow
        speedBuckets.add(new int[] {16, 30});  // 16-30 m/s: Cruising
        speedBuckets.add(new int[] {31, 45});  // 31-45 m/s: Fast
        speedBuckets.add(new int[] {46, 50});  // 46-50 m/s: Hyper

        // For-Each loop to find in the velocityRange the range the currentVelocity is in
        for (int[] range : speedBuckets) {
            // Storing the min and max value of the range
            int min = range[0];
            int max = range[1];

            // Checkin if currentVelocity is within the range, if so, assign to our rangeKey
            if (safeVelocity >= min && safeVelocity <= max) {
                rangeKey = range[0] + "-" + range[1];
            }
        }

        // A map structure where key is a string and value is a List of integers
        Map<String, List<Integer>> speedCategoryWeights = new HashMap<>();

        // Putting in the hashmap the key of the potential ranges, and then their corresponding weighted chances
        speedCategoryWeights.put("0-5", Arrays.asList(20, 50 , 25, 4, 1));
        speedCategoryWeights.put("6-15", Arrays.asList(10, 30, 50, 8, 2));
        speedCategoryWeights.put("16-30", Arrays.asList(5, 20, 50, 22, 3));
        speedCategoryWeights.put("31-45", Arrays.asList(2, 10, 40, 40, 8));
        speedCategoryWeights.put("46-50", Arrays.asList(1, 5, 25, 50, 19));

        // Assigning the found key to the weight category and then assigning that List
        List<Integer> weightList = speedCategoryWeights.get(rangeKey);

        // Total represents the total weight in the given lists we got
        int total = 0;
        // Looping through the list and adding each weight up
        for (int individualWeight : weightList) {
            total += individualWeight;
        }

        // From the total we added, we're generating a random number in that range
        int randomVelocity = randomNumGenerator.nextInt(total);

        // int variable to keep count the category we're in
        int categorySectionCounter = 0;

        // For each loop to go through the weight List to find the newly generated number category
        for (int section : weightList) {
            // Subtracting the random number to our specific velocity structure
            randomVelocity -= section;

            // Will break out of loop if we found that our value is below 0
            if (randomVelocity < 0 ) {
                break;
            }

            // If the statement above is false, then we must add +1 to the counter for the next category
            categorySectionCounter++;
        }

        // Given our new category and its index, we then find our new range
        int[] newRange = speedBuckets.get(categorySectionCounter);

        // Storing the range min and max
        int newMin = newRange[0];
        int newMax = newRange[1];

        // A double that represents our new anchor to help create our next probability speed
        double speedAnchor = Math.max(newMin, Math.min(newMax, currentVelocity));

        // Used to implement a triangular distribution
        double uniform = randomNumGenerator.nextDouble(); // 0.0 to 1.0

        // A double var used to assign the new velocity to our newTelemetry object
        double newVelocity;

        // The implementation of triangular distribution base on which side to apply too
        if (uniform < 0.5) {
            // Left side of the triangle (from low to mode)
            newVelocity = newMin + Math.sqrt(uniform * 2) * (speedAnchor - newMin);
        } else {
            // Right side of the triangle (from mode to high)
            newVelocity = speedAnchor + Math.sqrt((1 - uniform) * 2) * (newMax - speedAnchor);
        }

        // Assigning new Velocity to the newTelemetry
        theNewTelemetry.setVelocity(Math.round(newVelocity));
    }
}