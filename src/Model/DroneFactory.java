package Model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A drone factory class that help and manage creating specific types of drones
 * base on the given string provided.
 *
 * @version Fall 2025
 */
public class DroneFactory {
    /*-- Fields --*/

    /** A AtomicInteger to ensure full thread and unique Drone ID for each drone */
    private static final AtomicInteger DRONE_COUNTER = new AtomicInteger(1);

    /**
     * Method that creates drones base on the given string.
     *
     * @param theType is the type of drones that's being requested to make.
     * @return a Drone object base on the given string.
     */
    public static Drone createDrone(final String theType) {
        int newID = DRONE_COUNTER.getAndIncrement();
        return switch (theType) {
            case "A" -> new DroneA(newID);
            case "B" -> new DroneB(newID);
            default -> throw new IllegalStateException("Unexpected value: " + theType);
        };
    }

    /**
     * A non-static method to create a drone base on the given type.
     *
     * @param theType represent the drone being requested in string.
     * @return the drone object base on the given string.
     */
    public Drone createDroneNonStatic(final String theType) {
        return createDrone(theType);
    }

    /**
     * Method to reset the ID counter for all drones.
     */
    public void resetIdCounter() {
        DRONE_COUNTER.set(1);
    }
}
