package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class DroneTest {

    @Test
    void getBatteryLevelTest() {
        Drone droneTest = new Drone();
        droneTest.setBatteryLevel(50);

        assertEquals(50, droneTest.getBatteryLevel());
    }

    @Test
    void getDroneIDTest() {
        Drone droneTest = new Drone();

        assertEquals(1, droneTest.getDroneID());
    }

    @Test
    void setBatteryLevelTest() {
        Drone droneTest = new Drone();
        droneTest.setBatteryLevel(20);

        assertEquals(20, droneTest.getBatteryLevel());

        droneTest.setBatteryLevel(50);

        assertEquals(50, droneTest.getBatteryLevel());
    }

    @Test
    void isDroneOnTest() {
        Drone droneTest = new Drone();

        assertSame(Drone.DroneState.STARTING, droneTest.isDroneOn());
    }

    @Test
    void decreaseBatteryTest() {
        Drone droneTest = new Drone();
        droneTest.setBatteryLevel(100);
        droneTest.simulateBatteryDrain();

        assertEquals(90, droneTest.getBatteryLevel());
    }
}