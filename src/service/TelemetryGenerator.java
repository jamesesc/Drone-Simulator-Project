package service;

import Model.TelemetryData;
import java.util.Random;

/**
 * A class that is purely and sole responsible for generating telemetry data for the
 * drone object.
 * Generating values such as Latitude, Longitude, Velocity, Orientation, Altitude,
 * Starting position, starting height.
 *
 * @version Fall 2025
 */
public class TelemetryGenerator {
    /*-- Objects --*/

    /** Random object to use as a random generator */
    private static final Random myRandomGenerator = new Random();


    /*-- Constant --*/

    /** Represent the spawn configuration */
    private static final int SPAWN_RADIUS_FEET = 50;

    /** Represent the min starting altitude spawn */
    private static final int MIN_START_ALTITUDE = 25;

    /** Represent the max starting altitude spawn */
    private static final int MAX_START_ALTITUDE = 75;


    /*-- Converted all those probability stats in Enum as TA suggested --*/

    /**
     * Enum that represents the different Altitude angle ranges for flying up/down behavior.
     */
    private enum AltitudeAngle {
        /*-- Different Altitude category --*/

        /** Represent the common category, with an angle of 0-15, and an 80% probability */
        COMMON(0, 15, 80),
        /** Represent the occasional category, with an angle of 16-45, and a 17 % probability */
        OCCASIONAL(16, 45, 17),
        /** Represent the rare category, with an angle of 46-90, and a 3 % probability */
        RARE(46, 90, 3);


        /*-- Fields --*/

        /** Represent the min angle */
        private final int myMinAngle;

        /** Represent the max angle */
        private final int myMaxAngle;

        /** Represent the weight probability */
        private final int myWeight;

        /**
         * Constructor to create the altitude angle enum.
         *
         * @param theMinAngle represent the min angle.
         * @param theMaxAngle represent the max angle.
         * @param theWeight represent the probability weight.
         */
        AltitudeAngle(final int theMinAngle, final int theMaxAngle, final int theWeight) {
            myMinAngle = theMinAngle;
            myMaxAngle = theMaxAngle;
            myWeight = theWeight;
        }

        /**
         * Getter method to get the min angle.
         *
         * @return the min angle of the category as an int.
         */
        public int getMinAngle() {
            return myMinAngle;
        }

        /**
         * Getter method to get the max angle.
         *
         * @return the max angle of the category as an int.
         */
        public int getMaxAngle() {
            return myMaxAngle;
        }

        /**
         * Getter method to get the weight angle category.
         *
         * @return the weight of the category as an int.
         */
        public static int[] getWeights() {
            AltitudeAngle[] angles = values();
            int[] weights = new int[angles.length];
            for (int i = 0; i < angles.length; i++) {
                weights[i] = angles[i].myWeight;
            }
            return weights;
        }
    }

    /**
     * Enum that represents the different Turn angle ranges for turning behavior.
     */
    private enum TurnAngle {
        /*-- Different Turn category --*/

        /** Represent the small turn category, with an angle of 0-45, and a 70% probability */
        SMALL(0, 45, 70),
        /** Represent the medium turn category, with an angle of 46-135, and a 25% probability */
        MEDIUM(46, 135, 25),
        /** Represent the large turn category, with an angle of 136-180, and a 5% probability */
        LARGE(136, 180, 5);


        /*-- Fields --*/

        /** Represent the min turn angle */
        private final int myMinTurn;

        /** Represent the max turn angle */
        private final int myMaxTurn;

        /** Represent the weight probability */
        private final int myWeight;

        /**
         * Constructor to create the turn angle enum.
         *
         * @param theMinTurn represent the minimum turn angle.
         * @param theMaxTurn represent the maximum turn angle.
         * @param theWeight  represent the probability weight.
         */
        TurnAngle(final int theMinTurn, final int theMaxTurn, final int theWeight) {
            myMinTurn = theMinTurn;
            myMaxTurn = theMaxTurn;
            myWeight = theWeight;
        }

        /**
         * Getter method to get the minimum turn angle.
         *
         * @return the minimum turn angle of the category as an int.
         */
        public int getMyMinTurn() {
            return myMinTurn;
        }

        /**
         * Getter method to get the maximum turn angle.
         *
         * @return the maximum turn angle of the category as an int.
         */
        public int getMyMaxTurn() {
            return myMaxTurn;
        }

        /**
         * Getter method to get the weight of the turn angle category.
         *
         * @return the weight of the category as an int.
         */
        public static int[] getWeights() {
            TurnAngle[] turns = values();
            int[] weights = new int[turns.length];
            for (int i = 0; i < turns.length; i++) {
                weights[i] = turns[i].myWeight;
            }
            return weights;
        }
    }


    /**
     * Enum that represents different speed categories for the drone flight.
     */
    private enum SpeedCategory {
        /*-- Different Speed category --*/

        /** Represents the leisurely speed category, with a speed range of 0-5, with custom transition weights. */
        LEISURELY(0, 5, new int[]{20, 50, 25, 4, 1}),
        /** Represents the slow speed category, with a speed range of 6-15, with custom transition weights. */
        SLOW(6, 15, new int[]{10, 30, 50, 8, 2}),
        /** Represents the cruising speed category, with a speed range of 16-30, with custom transition weights. */
        CRUISING(16, 30, new int[]{5, 20, 50, 22, 3}),
        /** Represents the fast speed category, with a speed range of 31-45, with custom transition weights. */
        FAST(31, 45, new int[]{2, 10, 40, 40, 8}),
        /** Represents the hyper speed category, with a speed range of 46-50, with custom transition weights. */
        HYPER(46, 50, new int[]{1, 5, 25, 50, 19});


        /*-- Fields --*/

        /** Represents the minimum speed */
        private final int myMinSpeed;

        /** Represents the maximum speed */
        private final int myMaxSpeed;

        /** Represents the transition weights */
        private final int[] myTransitionWeights;

        /**
         * Constructor to create the speed category enum.
         *
         * @param theMinSpeed represent the minimum speed of the category.
         * @param theMaxSpeed represent the maximum speed of the category.
         * @param theTransitionWeights represent the transition weights for each category.
         */
        SpeedCategory(final int theMinSpeed, final int theMaxSpeed, final int[] theTransitionWeights) {
            myMinSpeed = theMinSpeed;
            myMaxSpeed = theMaxSpeed;
            myTransitionWeights = theTransitionWeights;
        }

        /**
         * Getter method to get the minimum speed.
         *
         * @return the minimum speed of the category as an int.
         */
        public int getMinSpeed() {
            return myMinSpeed;
        }

        /**
         * Getter method to get the maximum speed.
         *
         * @return the maximum speed of the category as an int.
         */
        public int getMaxSpeed() {
            return myMaxSpeed;
        }

        /**
         * Getter method to get the transition weights.
         *
         * @return the transition weights of the category as an int array.
         */
        public int[] getTransitionWeights() {
            return myTransitionWeights;
        }

        /**
         * Method that checks if a given theVelocity belongs to this speed category or not
         *
         * @param theVelocity the Velocity to check.
         * @return true if the Velocity is within the speed range, otherwise false.
         */
        public boolean contains(final double theVelocity) {
            return theVelocity >= myMinSpeed && theVelocity <= myMaxSpeed;
        }

        /**
         * Returns the SpeedCategory based on the given theVelocity.
         *
         * @param theVelocity the theVelocity to categorize.
         * @return the SpeedCategory that contains the theVelocity, or LEISURELY as default.
         */
        public static SpeedCategory fromVelocity(final double theVelocity) {
            SpeedCategory returnSpeed = LEISURELY;
            for (SpeedCategory category : values()) {
                if (category.contains(theVelocity)) {
                    returnSpeed = category;
                }
            }
            return returnSpeed;
        }
    }


    /**
     * Generates the starting spawn position.
     * It will generate a random spawn point within the circle spawn area.
     *
     * @return a TelemetryData object of lat and long values.
     */
    public TelemetryData generateStartPosition() {
        TelemetryData startPositionTelemetry = new TelemetryData();

        // Randomly generating a random angle of a circle
        double randomAngle = myRandomGenerator.nextDouble() * 2 * Math.PI;
        // Randomly generating a distance between the 0-50 units
        double randomDistance = Math.sqrt(myRandomGenerator.nextDouble()) * SPAWN_RADIUS_FEET;

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
        int altitude = myRandomGenerator.nextInt(MAX_START_ALTITUDE - MIN_START_ALTITUDE + 1) + MIN_START_ALTITUDE;
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

        generateLatitude(newTelemetry, thePrevTelemetry);
        generateLongitude(newTelemetry, thePrevTelemetry);
        generateAltitude(newTelemetry, thePrevTelemetry);
        generateOrientation(newTelemetry, thePrevTelemetry);
        generateVelocity(newTelemetry, thePrevTelemetry);

        return newTelemetry;
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

        // Selecting the right index base the given altitude angle
        int selectedIndex = selectWeightedIndex(AltitudeAngle.getWeights());
        AltitudeAngle selectedAngle = AltitudeAngle.values()[selectedIndex];

        // int represents the random generated magnitude
        int magnitude = randomInRange(selectedAngle.getMinAngle(), selectedAngle.getMaxAngle());

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

        theNextTelemetry.setAltitude(Math.max(0, newAltitude));
    }

    /**
     * Generates Orientation telemetry data that's base on its previous velocity.
     * Generating within a 360 degree directional.
     *
     * @param theNextTelemetry is the telemetry data we will output back.
     * @param thePrevTelemetry is the previous telemetry that was passed into.
     */
    private void generateOrientation(final TelemetryData theNextTelemetry, final TelemetryData thePrevTelemetry) {
        // Storing the current orientation
        int currentOrientation = (int) thePrevTelemetry.getOrientation();

        // Selecting  a turn angle category based on weighted probabilities
        int selectedIndex = selectWeightedIndex(TurnAngle.getWeights());
        TurnAngle selectedTurn = TurnAngle.values()[selectedIndex];

        // Generating a random turn within that category
        int turnMagnitude = randomInRange(selectedTurn.getMyMinTurn(), selectedTurn.getMyMaxTurn());

        // ternary that takes turnRight var, and see if oes right to leave as on the true statement
        int turn = myRandomGenerator.nextBoolean() ? turnMagnitude : -turnMagnitude;

        int newOrientation = convertAngle(currentOrientation + turn);

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
        // Storing the current velocity of the drone (will be used to determine the next velocity)
        double currentVelocity = thePrevTelemetry.getVelocity();

        // A double that represents our new anchor to help create our next probability speed
        double speedAnchor = Math.max(0, Math.min(50, currentVelocity));

        // Getting the speed category base on the speed anchor
        SpeedCategory currentCategory = SpeedCategory.fromVelocity(speedAnchor);

        // Selecting the next speed category to be used for weighted probability
        int nextCategoryIndex = selectWeightedIndex(currentCategory.getTransitionWeights());
        SpeedCategory nextCategory = SpeedCategory.values()[nextCategoryIndex];

        // Choosing the next velocity within the selected category
        double newVelocity = generateTriangularDistribution(
                nextCategory.getMinSpeed(),
                nextCategory.getMaxSpeed(),
                speedAnchor
        );

        // Assigning new Velocity to the newTelemetry
        theNextTelemetry.setVelocity(Math.round(newVelocity));
    }


    /*-- Helper Methods --*/

    /**
     * Helper method to select a random index based on weighted probabilities.
     *
     * @param theWeights represents the array of theWeights for each index.
     * @return an int that represent the random selected index.
     */
    private int selectWeightedIndex(final int[] theWeights) {
        int selectedIndex = 0;
        int totalWeight = sumArray(theWeights);
        int randomValue = myRandomGenerator.nextInt(totalWeight);

        for (int i = 0; i < theWeights.length; i++) {
            randomValue -= theWeights[i];
            if (randomValue < 0) {
                selectedIndex = i;
                break;
            }
        }
        return selectedIndex;
    }

    /**
     * Helper method to generate a random integer within a given range (p.s. inclusive).
     *
     * @param theMin represent the min value.
     * @param theMax represent the max value.
     * @return a random value between the given min and max value.
     */
    private int randomInRange(final int theMin, final int theMax) {
        return theMin + myRandomGenerator.nextInt(theMax - theMin + 1);
    }

    /**
     * Helper method to convert an angle to be within 0-359 degrees.
     *
     * @param theAngle represent the angle to convert into 359 degrees.
     * @return an int that represent the angle into 359 degrees.
     */
    private int convertAngle(final int theAngle) {
        return (((theAngle % 360) + 360) % 360);
    }

    /**
     * Helper method to generates a value using triangular distribution.
     *
     * @param theMin represent the min value.
     * @param theMax represent the max value.
     * @param theAnchor represent the peak of the distribution.
     * @return a generated value between the min and max and with considerations of the anchor.
     */
    private double generateTriangularDistribution(final double theMin, final double theMax, final double theAnchor) {
        double generatedNumber;
        double anchor = Math.max(theMin, Math.min(theMax, theAnchor));
        double uniform = myRandomGenerator.nextDouble();

        // Checking whether it either on the left or the right side of the anchor
        if (uniform < 0.5) {
            // The Left side of the triangle so the min to anchor
            generatedNumber =  theMin + Math.sqrt(uniform * 2) * (anchor - theMin);
        } else {
            // The right side of the triangle so the anchor to max
            generatedNumber = anchor + Math.sqrt((1 - uniform) * 2) * (theMax - anchor);
        }

        return generatedNumber;
    }

    /**
     * Helper method to help sum all elements in an integer theArray.
     *
     * @param theArray represent the given array of int to count the sum of.
     * @return all the sum elements in the given array.
     */
    private int sumArray(final int[] theArray) {
        int sum = 0;
        for (int value : theArray) {
            sum += value;
        }
        return sum;
    }
}