package service;

import Model.TelemetryData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A class that is purely and sole resposnbiel for generating telemetry data for the
 * drone object.
 * Generating values such as Latitude, Longitude, Velocity, Orientation, Altitude,
 * Starting position, starting height.
 *
 * @author James Escudero
 * @version Fall 2025
 */
public class TelemetryGenerator {

    /** Random object to use as a random generator */
    private static final Random myRandomGenerator = new Random();

    /**
     * Generates the starting spawn position.
     * It will generate a random spawn point within the circle spawn area.
     *
     * @return a TelemetryData object of lat and long values.
     */
    public TelemetryData generateStartPosition() {

        TelemetryData startPositionTelemetry = new TelemetryData();

        // The spawn radius for the drone is a circle
        final int radius = 50; // aka 50 ft

        // Randomly generating a random angle of a circle
        double randomAngle = myRandomGenerator.nextDouble() * 2 * Math.PI;
        // Randomly generating a distance between the 0-50 units
        double randomDistance = Math.sqrt(myRandomGenerator.nextDouble()) * radius; // 0 to units

        // Base on the random distance + random Angle -> Convert into official Lat, and Long position
        double latOffset = randomDistance * Math.cos(randomAngle);
        double longOffset = randomDistance * Math.sin(randomAngle);

        // Setting those calculated position into the telemetry data object
        startPositionTelemetry.setLatitude(latOffset);
        startPositionTelemetry.setLongitude(longOffset);

        // Returning the randomly generated position
        return startPositionTelemetry;
    }

    /**
     * Generates the starting altitude that the drones fly up to after 3 seconds.
     *
     * @return a TelemetryData with only the altitude being generated.
     */
    public TelemetryData generateStartAltitude() {
        TelemetryData droneSetupTelemetry = new TelemetryData();

        // Altitude generates between 25-75 feet.
        int altitude = myRandomGenerator.nextInt(51) + 25; // 51 = 75 - 25 + 1
        droneSetupTelemetry.setAltitude(altitude);

        return droneSetupTelemetry;
    }

    /**
     * Generates Telemetry Data for all Telemetry values base on the previous telemetry value.
     *
     * @param thePrevTelemetry is the current telemetry data.
     * @return a new telemetry data that is base on the pass telemetry data.
     */
    public TelemetryData generateTelemetryData(final TelemetryData thePrevTelemetry) {
        final TelemetryData newTelemetry = new TelemetryData();
        generateAll(newTelemetry, thePrevTelemetry);
        return newTelemetry;
    }

    /**
     * Helper method to call each telemetry value generator.
     *
     * @param theNextTelemetry is the telemetry data we will output back.
     * @param thePrevTelemetry is the previous telemetry that was passed into.
     */
    private void generateAll(final TelemetryData theNextTelemetry, TelemetryData thePrevTelemetry) {
        generateLatitude(theNextTelemetry, thePrevTelemetry);
        generateLongitude(theNextTelemetry, thePrevTelemetry);
        generateAltitude(theNextTelemetry, thePrevTelemetry);
        generateOrientation(theNextTelemetry, thePrevTelemetry);
        generateVelocity(theNextTelemetry, thePrevTelemetry);
    }

    /**
     * Calculates the latitude Telemetry Data base on the previous velocity and orientation Telemetry values.
     *
     * @param theNextTelemetry is the telemetry data we will output back.
     * @param thePrevTelemetry is the previous telemetry that was passed into.
     */
    private void generateLatitude(final TelemetryData theNextTelemetry, final TelemetryData thePrevTelemetry) {
        // Converting the drone current orientation to radians
        double orientationInRadians = Math.toRadians(thePrevTelemetry.getOrientation());

        // Calculate how much we moved vertically (aka the Y-axis)
        double changeInY = thePrevTelemetry.getVelocity() * Math.cos(orientationInRadians);

        // Adding the change to the old Position to the new TelemetryData output
        theNextTelemetry.setLatitude(thePrevTelemetry.getLatitude() + changeInY);
    }

    /**
     * Calculates the longitude Telemetry Data base on the previous velocity and orientation Telemetry values.
     *
     * @param theNextTelemetry is the telemetry data we will output back.
     * @param thePrevTelemetry is the previous telemetry that was passed into.
     */
    private void generateLongitude(final TelemetryData theNextTelemetry, final TelemetryData thePrevTelemetry) {
        double orientationInRadians = Math.toRadians(thePrevTelemetry.getOrientation());

        // Calculate how much we moved vertically (aka the x-axis)
        double changeInX = thePrevTelemetry.getVelocity() * Math.sin(orientationInRadians);

        theNextTelemetry.setLongitude(thePrevTelemetry.getLongitude() + changeInX);
    }

    /**
     * Generates Telemetry data of altitude that is base on the velocity and
     *
     * @param theNextTelemetry is the telemetry data we will output back.
     * @param thePrevTelemetry is the previous telemetry that was passed into.
     */
    private void generateAltitude(final TelemetryData theNextTelemetry, final TelemetryData thePrevTelemetry) {
        // Storing the previous velocity
        double velocity = thePrevTelemetry.getVelocity();

        // Valid checking if it's 0, we keep it 0
        if (velocity <= 0) {
            theNextTelemetry.setAltitude(thePrevTelemetry.getAltitude());
            return;
        }

        // Define magnitude buckets (always positive)
        List<int[]> magnitudeBuckets = Arrays.asList(
                new int[]{0, 15},    // Common
                new int[]{16, 45},   // Occasion
                new int[]{46, 90}    // Rare
        );

        // Probability Weights
        int[] weights = {80, 17, 3}; // Common, Occasion, Rare

        // Weighted random selection
        int totalWeight = 100; // 80+17+3
        int randomValue = myRandomGenerator.nextInt(totalWeight);
        int selectedIndex = 0;
        for (int w : weights) {
            randomValue -= w;
            if (randomValue < 0) break;
            selectedIndex++;
        }

        // Getting the magnitude range
        int[] range = magnitudeBuckets.get(selectedIndex);
        int minMag = range[0];
        int maxMag = range[1];

        // int represents the random generated magnitude
        int magnitude = minMag + myRandomGenerator.nextInt(maxMag - minMag + 1);

        // Randomly choosing to either climb (+) or dive (-), EXCEPT: if magnitude == 0 then we stay level
        int angleDegrees;
        if (magnitude == 0) {
            angleDegrees = 0;
        } else {
            boolean climb = myRandomGenerator.nextBoolean();
            angleDegrees = climb ? magnitude : -magnitude;
        }

        // Compute altitude change
        double altitudeChange = velocity * Math.sin(Math.toRadians(angleDegrees));
        double newAltitude = thePrevTelemetry.getAltitude() + altitudeChange;

        theNextTelemetry.setAltitude(newAltitude);
    }

    /**
     * Generates Orientation telemetry data that's base on its previous velocity.
     * Generating within a 360 degree directional.
     *
     * @param theNextTelemetry is the telemetry data we will output back.
     * @param thePrevTelemetry is the previous telemetry that was passed into.
     */
    private void generateOrientation(final TelemetryData theNextTelemetry, final TelemetryData thePrevTelemetry) {
        /* Velocity Structure
        -45 <-> +45 = 70% (Common)
        -135 ~ -46 and +46 ~ +135 = 25% (Occasion)
        -180 ~ -136 and +136 ~ +180 = 5% (Rare)
        */

        // Storing the current orientation
        int currentOrientation = (int) thePrevTelemetry.getOrientation();

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

        int randomValue = myRandomGenerator.nextInt(totalWeight);
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

        int direction = minTurn + myRandomGenerator.nextInt(maxTurn - minTurn + 1);

        boolean whatTurn = myRandomGenerator.nextBoolean();

        // ternary that takes turnRight var, and see if oes right to leave as on the true statement
        int turn = whatTurn ? direction : -direction;

        int newOrientation = currentOrientation + turn;

        newOrientation = (((newOrientation % 360) + 360) % 360);

        // Assigning new Velocity to the newTelemetry
        theNextTelemetry.setOrientation(newOrientation);
    }

    /**
     * Generates the velocity of the drone base on the current velocity with
     * various realistic to imitate realism of the drone behavior.
     *
     * @param theNextTelemetry is the telemetry data we will output back.
     * @param thePrevTelemetry is the previous telemetry that was passed into.
     */
    private void generateVelocity(final TelemetryData theNextTelemetry, final TelemetryData thePrevTelemetry) {
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
        final double currentVelocity = thePrevTelemetry.getVelocity();

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
        int randomVelocity = myRandomGenerator.nextInt(total);

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
        double uniform = myRandomGenerator.nextDouble(); // 0.0 to 1.0

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
        theNextTelemetry.setVelocity(Math.round(newVelocity));
    }
}