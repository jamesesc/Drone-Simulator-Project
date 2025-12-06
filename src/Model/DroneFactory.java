package Model;

import java.util.concurrent.atomic.AtomicInteger;

public class DroneFactory {
    private static final AtomicInteger DRONE_COUNTER = new AtomicInteger(1);

    public static Drone createDrone(String type) {
        int newID = DRONE_COUNTER.getAndIncrement();
        return switch (type) {
            case "A" -> new DroneA(newID);
            case "B" -> new DroneB(newID);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public Drone createDroneNonStatic(String type) {
        return createDrone(type);
    }

    public void resetIdCounter() {
        DRONE_COUNTER.set(1);
    }
}
