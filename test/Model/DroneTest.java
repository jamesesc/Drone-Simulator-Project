package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class DroneTest {
    @BeforeEach
    void setup() {
        // Reset only for test isolation
        Drone.resetIdCounter();
    }

    @Test
    void droneIDIncrements() {
        Drone d1 = new Drone();
        Drone d2 = new Drone();
        Drone d3 = new Drone();

        assertAll(() -> {
            assertEquals(1, d1.getDroneID());
            assertEquals(2, d2.getDroneID());
            assertEquals(3, d3.getDroneID());
        });
    }

    @Test
    void testResetIdCounter() {
        Drone d1 = new Drone();
        Drone d2 = new Drone();

        Drone.resetIdCounter();

        Drone d3 = new Drone();

        assertEquals(1, d3.getDroneID());
    }

    @Test
    void getDroneTelemetry() {
        TelemetryData telData = new TelemetryData(1, 1, 1, 1, 1);
        Drone testDrone = new Drone();

        testDrone.updateDroneNextMove(telData);
        testDrone.updateDroneNextMove(telData);

        assertEquals(telData, testDrone.getDroneTelemetry());
    }

    @Test
    void testDroneState() {
        Drone droneTest = new Drone();
        droneTest.setDroneState(Drone.DroneState.FLYING);

        assertEquals(Drone.DroneState.FLYING, droneTest.isDroneOn());
    }

    @Test
    void testDroneStateNull() {
        Drone droneTest = new Drone();
        assertThrows(NullPointerException.class, () -> droneTest.setDroneState(null));
    }

    @Test
    void batteryLowThresholdFlyOperation() {
        Drone droneTest = new Drone();
        droneTest.setDroneState(Drone.DroneState.FLYING);
        droneTest.setBatteryLevel(1);
        droneTest.updateDroneNextMove(
                new TelemetryData(1, 1, 1, 1, 1));
        assertEquals(Drone.DroneState.LANDING, droneTest.isDroneOn());
    }

    @Test
    void droneLandingToCharging(){
        Drone drone = new Drone();
        drone.setDroneState(Drone.DroneState.LANDING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 0, 0, 0));
        assertEquals(Drone.DroneState.CHARGING, drone.isDroneOn());
    }

    @Test
    void droneLandingNewAlt() {
        Drone drone = new Drone();
        drone.setDroneState(Drone.DroneState.FLYING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 30, 0, 0));
        drone.setDroneState(Drone.DroneState.LANDING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, -10, 0, 30));
        assertEquals(new TelemetryData(0, 0, 0, 0, 0), drone.getDroneTelemetry());
    }

    @Test
    void droneLandingAgain() {
        Drone drone = new Drone();
        drone.setDroneState(Drone.DroneState.FLYING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 40, 0, 0));
        drone.setDroneState(Drone.DroneState.LANDING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 10, 0, 0));
        assertEquals(new TelemetryData(0, 0, 10, 0 ,0), drone.getDroneTelemetry());
    }

    @Test
    void droneChargingToTakeoff() {
        Drone drone = new Drone();
        drone.setDroneState(Drone.DroneState.CHARGING);
        drone.setBatteryLevel(90);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 10, 0, 10));
        assertAll(() -> {
            assertEquals(new TelemetryData(0, 0, 0, 0, 0), drone.getDroneTelemetry());
            assertEquals(Drone.DroneState.TAKEOFF, drone.isDroneOn());
        });
    }

    @Test
    void droneCharging() {
        Drone drone = new Drone();
        drone.setDroneState(Drone.DroneState.CHARGING);
        drone.setBatteryLevel(10);
        drone.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));
        assertEquals(Drone.DroneState.CHARGING, drone.isDroneOn());
    }

    @Test
    void droneTakeoff() {
        Drone drone = new Drone();
        drone.setDroneState(Drone.DroneState.TAKEOFF);
        drone.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));
        assertEquals(new TelemetryData(0, 0, 25, 0, 30), drone.getDroneTelemetry());
    }

    @Test
    void droneTakeoffBranch() {
        Drone drone = new Drone();
        drone.setDroneState(Drone.DroneState.FLYING);
        drone.updateDroneNextMove(new TelemetryData(0, 0, -10, 0, 0));
        drone.setDroneState(Drone.DroneState.TAKEOFF);
        drone.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));
        assertEquals(new TelemetryData(0, 0, 20, 0, 30), drone.getDroneTelemetry());
    }



    @Test
    void getBatteryLevelTest() {
        Drone droneTest = new Drone();
        droneTest.setBatteryLevel(50);

        assertEquals(50, droneTest.getBatteryLevel());
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
    void setBatteryLevelIllegal() {
        Drone droneTest = new Drone();

        assertAll(() -> {
            assertThrows(IllegalArgumentException.class, () -> droneTest.setBatteryLevel(-1));
            assertThrows(IllegalArgumentException.class, () -> droneTest.setBatteryLevel(101));
        });
    }


    @Test
    void isDroneOnTest() {
        Drone droneTest = new Drone();

        assertSame(Drone.DroneState.INACTIVE, droneTest.isDroneOn());
    }

    @Test
    void decreaseBatteryTest() {
        Drone droneTest = new Drone();
        droneTest.setBatteryLevel(100);
        droneTest.simulateBatteryDrain();

        assertEquals(99, droneTest.getBatteryLevel());
    }
}