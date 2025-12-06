package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class DroneTest {
    DroneFactory factory = new DroneFactory();

    @BeforeEach
    void setup() {
        // Reset only for test isolation
        factory.resetIdCounter();
    }

    @Test
    void illegalFactoryArgument() {
        assertThrows(IllegalStateException.class, () -> factory.createDroneNonStatic("Hi"));
    }

    @Test
    void droneIDIncrements() {
        Drone d1 = factory.createDroneNonStatic("A");
        Drone d2 = factory.createDroneNonStatic("B");
        Drone d3 = factory.createDroneNonStatic("A");

        assertAll(() -> {
            assertEquals(1, d1.getDroneID());
            assertEquals(2, d2.getDroneID());
            assertEquals(3, d3.getDroneID());
        });
    }

    @Test
    void getDroneTelemetry() {
        TelemetryData telData = new TelemetryData(1, 1, 1, 1, 1);
        Drone testDrone = factory.createDroneNonStatic("A");
        Drone testDroneB = factory.createDroneNonStatic("B");

        testDrone.updateDroneNextMove(telData);
        testDrone.updateDroneNextMove(telData);

        testDroneB.updateDroneNextMove(telData);
        testDroneB.updateDroneNextMove(telData);
        testDroneB.updateDroneNextMove(telData);

        assertAll(() -> {
            assertEquals(telData, testDrone.getDroneTelemetry());
            assertEquals(telData, testDroneB.getDroneTelemetry());
        });
    }

    @Test
    void testDroneState() {
        Drone droneTest = factory.createDroneNonStatic("A");
        Drone droneTestB = factory.createDroneNonStatic("B");

        droneTest.setDroneState(Drone.DroneState.FLYING);
        droneTestB.setDroneState(Drone.DroneState.FLYING);

        assertAll(() -> {
            assertEquals(Drone.DroneState.FLYING, droneTest.isDroneOn());
            assertEquals(Drone.DroneState.FLYING, droneTestB.isDroneOn());
        });
    }

    @Test
    void testDroneStateNull() {
        Drone droneTest = factory.createDroneNonStatic("A");
        Drone droneTestB = factory.createDroneNonStatic("B");

        assertThrows(NullPointerException.class, () -> droneTest.setDroneState(null));
        assertThrows(NullPointerException.class, () -> droneTestB.setDroneState(null));
    }

    @Test
    void batteryLowThresholdFlyOperation() {
        Drone droneTest = factory.createDroneNonStatic("A");
        Drone droneTestB = factory.createDroneNonStatic("B");

        droneTest.setDroneState(Drone.DroneState.FLYING);
        droneTest.setBatteryLevel(1);
        droneTest.updateDroneNextMove(
                new TelemetryData(1, 1, 1, 1, 1));

        droneTestB.setDroneState(Drone.DroneState.FLYING);
        droneTestB.setBatteryLevel(1);
        droneTestB.updateDroneNextMove(
                new TelemetryData(1, 1, 1, 1, 1));

        assertAll(() -> {
            assertEquals(Drone.DroneState.LANDING, droneTest.isDroneOn());
            assertEquals(Drone.DroneState.LANDING, droneTestB.isDroneOn());
        });
    }

    @Test
    void droneLandingToCharging(){
        Drone drone = factory.createDroneNonStatic("A");
        Drone droneB = factory.createDroneNonStatic("B");

        drone.setDroneState(Drone.DroneState.LANDING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 0, 0, 0));

        droneB.setDroneState(Drone.DroneState.LANDING);
        droneB.updateDroneNextMove(
                new TelemetryData(0, 0, 0, 0, 0));

        assertAll(() -> {
            assertEquals(Drone.DroneState.CHARGING, drone.isDroneOn());
            assertEquals(Drone.DroneState.CHARGING, droneB.isDroneOn());
        });
    }

    @Test
    void droneLandingNewAlt() {
        Drone drone = factory.createDroneNonStatic("A");
        Drone droneB = factory.createDroneNonStatic("B");

        drone.setDroneState(Drone.DroneState.FLYING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 30, 0, 0));
        drone.setDroneState(Drone.DroneState.LANDING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, -10, 0, 30));

        droneB.setDroneState(Drone.DroneState.FLYING);
        droneB.updateDroneNextMove(
                new TelemetryData(0, 0, 30, 0, 0));
        droneB.setDroneState(Drone.DroneState.LANDING);
        droneB.updateDroneNextMove(
                new TelemetryData(0, 0, -10, 0, 30));

        assertAll(() -> {
            assertEquals(new TelemetryData(0, 0, 0, 0, 0), drone.getDroneTelemetry());
            assertEquals(new TelemetryData(0, 0, 0, 0, 0), droneB.getDroneTelemetry());
        });
    }

    @Test
    void droneLandingAgain() {
        Drone drone = factory.createDroneNonStatic("A");
        Drone droneB = factory.createDroneNonStatic("B");

        drone.setDroneState(Drone.DroneState.FLYING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 40, 0, 0));
        drone.setDroneState(Drone.DroneState.LANDING);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 10, 0, 0));

        droneB.setDroneState(Drone.DroneState.FLYING);
        droneB.updateDroneNextMove(
                new TelemetryData(0, 0, 40, 0, 0));
        droneB.setDroneState(Drone.DroneState.LANDING);
        droneB.updateDroneNextMove(
                new TelemetryData(0, 0, 10, 0, 0));

        assertAll(() -> {
            assertEquals(new TelemetryData(0, 0, 10, 0 ,0), drone.getDroneTelemetry());
            assertTrue(0 <= droneB.getDroneTelemetry().getAltitude() &&
                    droneB.getDroneTelemetry().getAltitude() <= 25);
        });
    }

    @Test
    void droneChargingToTakeoff() {
        Drone drone = factory.createDroneNonStatic("A");
        Drone droneB = factory.createDroneNonStatic("B");

        drone.setDroneState(Drone.DroneState.CHARGING);
        drone.setBatteryLevel(90);
        drone.updateDroneNextMove(
                new TelemetryData(0, 0, 10, 0, 10));

        droneB.setDroneState(Drone.DroneState.CHARGING);
        droneB.setBatteryLevel(90);
        droneB.updateDroneNextMove(
                new TelemetryData(0, 0, 10, 0, 10));

        assertAll(() -> {
            assertEquals(new TelemetryData(0, 0, 0, 0, 0), drone.getDroneTelemetry());
            assertEquals(Drone.DroneState.TAKEOFF, drone.isDroneOn());

            assertEquals(new TelemetryData(0, 0, 0, 0, 0), droneB.getDroneTelemetry());
            assertEquals(Drone.DroneState.TAKEOFF, droneB.isDroneOn());
        });
    }

    @Test
    void droneCharging() {
        Drone drone = factory.createDroneNonStatic("A");
        Drone droneB = factory.createDroneNonStatic("B");

        drone.setDroneState(Drone.DroneState.CHARGING);
        drone.setBatteryLevel(10);
        drone.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));

        droneB.setDroneState(Drone.DroneState.CHARGING);
        droneB.setBatteryLevel(10);
        droneB.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));

        assertAll(() -> {
            assertEquals(Drone.DroneState.CHARGING, drone.isDroneOn());
            assertEquals(Drone.DroneState.CHARGING, droneB.isDroneOn());
        });
    }

    @Test
    void droneTakeoff() {
        Drone drone = factory.createDroneNonStatic("A");
        Drone droneB = factory.createDroneNonStatic("B");

        drone.setDroneState(Drone.DroneState.TAKEOFF);
        drone.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));

        droneB.setDroneState(Drone.DroneState.TAKEOFF);
        droneB.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));
        droneB.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));

        assertAll(() -> {
            assertEquals(new TelemetryData(0, 0, 25, 0, 30), drone.getDroneTelemetry());
            assertEquals(new TelemetryData(0, 0, 15, 0, 15), droneB.getDroneTelemetry());
        });
    }

    @Test
    void droneTakeoffBranch() {
        Drone drone = factory.createDroneNonStatic("A");
        Drone droneB = factory.createDroneNonStatic("B");

        drone.setDroneState(Drone.DroneState.FLYING);
        drone.updateDroneNextMove(new TelemetryData(0, 0, -10, 0, 0));
        drone.setDroneState(Drone.DroneState.TAKEOFF);
        drone.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));

        droneB.setDroneState(Drone.DroneState.FLYING);
        droneB.updateDroneNextMove(new TelemetryData(0, 0, -10, 0, 0));
        droneB.updateDroneNextMove(new TelemetryData(0, 0, -10, 0, 0));
        droneB.setDroneState(Drone.DroneState.TAKEOFF);
        droneB.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));
        droneB.updateDroneNextMove(
                new TelemetryData(0,0,0,0,0));

        assertAll(() -> {
            assertEquals(new TelemetryData(0, 0, 20, 0, 30), drone.getDroneTelemetry());
            assertEquals(new TelemetryData(0, 0, 5, 0, 15), droneB.getDroneTelemetry());
        });
    }



    @Test
    void getBatteryLevelTest() {
        Drone droneTest = factory.createDroneNonStatic("A");
        Drone droneTestB = factory.createDroneNonStatic("B");

        droneTest.setBatteryLevel(50);
        droneTestB.setBatteryLevel(50);

        assertAll(() -> {
            assertEquals(50, droneTest.getBatteryLevel());
            assertEquals(50, droneTestB.getBatteryLevel());
        });
    }

    @Test
    void setBatteryLevelTest() {
        Drone droneTest = factory.createDroneNonStatic("A");
        Drone droneTestB = factory.createDroneNonStatic("B");

        droneTest.setBatteryLevel(20);
        droneTestB.setBatteryLevel(20);

        assertAll(() -> {
            assertEquals(20, droneTest.getBatteryLevel());
            assertEquals(20, droneTestB.getBatteryLevel());
        });


        droneTest.setBatteryLevel(50);
        droneTestB.setBatteryLevel(50);

        assertAll(() -> {
            assertEquals(50, droneTest.getBatteryLevel());
            assertEquals(50, droneTestB.getBatteryLevel());
        });
    }

    @Test
    void setBatteryLevelIllegal() {
        Drone droneTest = factory.createDroneNonStatic("A");
        Drone droneTestB = factory.createDroneNonStatic("B");

        assertAll(() -> {
            assertThrows(IllegalArgumentException.class, () -> droneTest.setBatteryLevel(-1));
            assertThrows(IllegalArgumentException.class, () -> droneTest.setBatteryLevel(101));
            assertThrows(IllegalArgumentException.class, () -> droneTestB.setBatteryLevel(-1));
            assertThrows(IllegalArgumentException.class, () -> droneTestB.setBatteryLevel(101));
        });
    }


    @Test
    void isDroneOnTest() {
        Drone droneTest = factory.createDroneNonStatic("A");
        Drone droneTestB = factory.createDroneNonStatic("B");

        assertAll(() -> {
            assertSame(Drone.DroneState.INACTIVE, droneTest.isDroneOn());
            assertSame(Drone.DroneState.INACTIVE, droneTestB.isDroneOn());
        });
    }

    @Test
    void decreaseBatteryTest() {
        Drone droneTest = factory.createDroneNonStatic("A");
        Drone droneTestB = factory.createDroneNonStatic("B");

        droneTest.setBatteryLevel(100);
        droneTest.simulateBatteryDrain();

        droneTestB.setBatteryLevel(100);
        droneTestB.simulateBatteryDrain();

        assertAll(() -> {
            assertEquals(99, droneTest.getBatteryLevel());
            assertEquals(99, droneTestB.getBatteryLevel());
        });
    }

    @Test
    void takeoffWithHesitationBranch() {
        Drone d = factory.createDroneNonStatic("B");
        d.setDroneState(Drone.DroneState.TAKEOFF);

        d.updateDroneNextMove(new TelemetryData(0,0,0,0,0));
        d.updateDroneNextMove(new TelemetryData(0,0,0,0,0));

        assertEquals(15.0, d.getDroneTelemetry().getAltitude());
    }

    @Test
    void takeoffReachesSafeAltitudeWithHesitationTrue() {
        Drone d = factory.createDroneNonStatic("B");
        d.setDroneState(Drone.DroneState.TAKEOFF);

        d.getDroneTelemetry().setAltitude(30.0 - 15.0);

        d.updateDroneNextMove(new TelemetryData(0,0,0,0,0));
        d.updateDroneNextMove(new TelemetryData(0,0,0,0,0));
        d.updateDroneNextMove(new TelemetryData(0,0,0,0,0));
        d.updateDroneNextMove(new TelemetryData(0,0,0,0,0));

        assertAll(() -> {
            assertEquals(30.0, d.getDroneTelemetry().getAltitude());
            assertEquals(Drone.DroneState.FLYING, d.isDroneOn());
        });
    }
}