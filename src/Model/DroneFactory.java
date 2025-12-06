package Model;

import java.util.concurrent.atomic.AtomicInteger;

public class DroneFactory {
    private static final AtomicInteger DRONE_COUNTER = new AtomicInteger(1);

    public static Drone createDrone(String type) {
        int newID = DRONE_COUNTER.getAndIncrement();
        return switch (type) {
            default -> new BasicDrone(newID);
        };
    }

    public void resetIdCounter() {
        DRONE_COUNTER.set(1);
    }
}
