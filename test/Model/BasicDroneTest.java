//package Model;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//class BasicDroneTest {
//    //TODO
//    @BeforeEach
//    void setup() {
//        // Reset only for test isolation
//        // BasicDrone.resetIdCounter();
//    }
//
//    @Test
//    void droneIDIncrements() {
//        BasicDrone d1 = new BasicDrone();
//        BasicDrone d2 = new BasicDrone();
//        BasicDrone d3 = new BasicDrone();
//
//        assertAll(() -> {
//            assertEquals(1, d1.getDroneID());
//            assertEquals(2, d2.getDroneID());
//            assertEquals(3, d3.getDroneID());
//        });
//    }
//
//    @Test
//    //TODO
//    void testResetIdCounter() {
////        BasicDrone d1 = new BasicDrone();
////        BasicDrone d2 = new BasicDrone();
////
////        BasicDrone.resetIdCounter();
////
////        BasicDrone d3 = new BasicDrone();
////
////        assertEquals(1, d3.getDroneID());
//    }
//
//    @Test
//    void getDroneTelemetry() {
//        TelemetryData telData = new TelemetryData(1, 1, 1, 1, 1);
//        BasicDrone testDrone = new BasicDrone();
//
//        testDrone.updateDroneNextMove(telData);
//        testDrone.updateDroneNextMove(telData);
//
//        assertEquals(telData, testDrone.getDroneTelemetry());
//    }
//
//    @Test
//    void testDroneState() {
//        BasicDrone droneTest = new BasicDrone();
//        droneTest.setDroneState(BasicDrone.DroneState.FLYING);
//
//        assertEquals(BasicDrone.DroneState.FLYING, droneTest.isDroneOn());
//    }
//
//    @Test
//    void testDroneStateNull() {
//        BasicDrone droneTest = new BasicDrone();
//        assertThrows(NullPointerException.class, () -> droneTest.setDroneState(null));
//    }
//
//    @Test
//    void batteryLowThresholdFlyOperation() {
//        BasicDrone droneTest = new BasicDrone();
//        droneTest.setDroneState(BasicDrone.DroneState.FLYING);
//        droneTest.setBatteryLevel(1);
//        droneTest.updateDroneNextMove(
//                new TelemetryData(1, 1, 1, 1, 1));
//        assertEquals(BasicDrone.DroneState.LANDING, droneTest.isDroneOn());
//    }
//
//    @Test
//    void droneLandingToCharging(){
//        BasicDrone drone = new BasicDrone();
//        drone.setDroneState(BasicDrone.DroneState.LANDING);
//        drone.updateDroneNextMove(
//                new TelemetryData(0, 0, 0, 0, 0));
//        assertEquals(BasicDrone.DroneState.CHARGING, drone.isDroneOn());
//    }
//
//    @Test
//    void droneLandingNewAlt() {
//        BasicDrone drone = new BasicDrone();
//        drone.setDroneState(BasicDrone.DroneState.FLYING);
//        drone.updateDroneNextMove(
//                new TelemetryData(0, 0, 30, 0, 0));
//        drone.setDroneState(BasicDrone.DroneState.LANDING);
//        drone.updateDroneNextMove(
//                new TelemetryData(0, 0, -10, 0, 30));
//        assertEquals(new TelemetryData(0, 0, 0, 0, 0), drone.getDroneTelemetry());
//    }
//
//    @Test
//    void droneLandingAgain() {
//        BasicDrone drone = new BasicDrone();
//        drone.setDroneState(BasicDrone.DroneState.FLYING);
//        drone.updateDroneNextMove(
//                new TelemetryData(0, 0, 40, 0, 0));
//        drone.setDroneState(BasicDrone.DroneState.LANDING);
//        drone.updateDroneNextMove(
//                new TelemetryData(0, 0, 10, 0, 0));
//        assertEquals(new TelemetryData(0, 0, 10, 0 ,0), drone.getDroneTelemetry());
//    }
//
//    @Test
//    void droneChargingToTakeoff() {
//        BasicDrone drone = new BasicDrone();
//        drone.setDroneState(BasicDrone.DroneState.CHARGING);
//        drone.setBatteryLevel(90);
//        drone.updateDroneNextMove(
//                new TelemetryData(0, 0, 10, 0, 10));
//        assertAll(() -> {
//            assertEquals(new TelemetryData(0, 0, 0, 0, 0), drone.getDroneTelemetry());
//            assertEquals(BasicDrone.DroneState.TAKEOFF, drone.isDroneOn());
//        });
//    }
//
//    @Test
//    void droneCharging() {
//        BasicDrone drone = new BasicDrone();
//        drone.setDroneState(BasicDrone.DroneState.CHARGING);
//        drone.setBatteryLevel(10);
//        drone.updateDroneNextMove(
//                new TelemetryData(0,0,0,0,0));
//        assertEquals(BasicDrone.DroneState.CHARGING, drone.isDroneOn());
//    }
//
//    @Test
//    void droneTakeoff() {
//        BasicDrone drone = new BasicDrone();
//        drone.setDroneState(BasicDrone.DroneState.TAKEOFF);
//        drone.updateDroneNextMove(
//                new TelemetryData(0,0,0,0,0));
//        assertEquals(new TelemetryData(0, 0, 25, 0, 30), drone.getDroneTelemetry());
//    }
//
//    @Test
//    void droneTakeoffBranch() {
//        BasicDrone drone = new BasicDrone();
//        drone.setDroneState(BasicDrone.DroneState.FLYING);
//        drone.updateDroneNextMove(new TelemetryData(0, 0, -10, 0, 0));
//        drone.setDroneState(BasicDrone.DroneState.TAKEOFF);
//        drone.updateDroneNextMove(
//                new TelemetryData(0,0,0,0,0));
//        assertEquals(new TelemetryData(0, 0, 20, 0, 30), drone.getDroneTelemetry());
//    }
//
//
//
//    @Test
//    void getBatteryLevelTest() {
//        BasicDrone droneTest = new BasicDrone();
//        droneTest.setBatteryLevel(50);
//
//        assertEquals(50, droneTest.getBatteryLevel());
//    }
//
//    @Test
//    void setBatteryLevelTest() {
//        BasicDrone droneTest = new BasicDrone();
//        droneTest.setBatteryLevel(20);
//
//        assertEquals(20, droneTest.getBatteryLevel());
//
//        droneTest.setBatteryLevel(50);
//
//        assertEquals(50, droneTest.getBatteryLevel());
//    }
//
//    @Test
//    void setBatteryLevelIllegal() {
//        BasicDrone droneTest = new BasicDrone();
//
//        assertAll(() -> {
//            assertThrows(IllegalArgumentException.class, () -> droneTest.setBatteryLevel(-1));
//            assertThrows(IllegalArgumentException.class, () -> droneTest.setBatteryLevel(101));
//        });
//    }
//
//
//    @Test
//    void isDroneOnTest() {
//        BasicDrone droneTest = new BasicDrone();
//
//        assertSame(BasicDrone.DroneState.INACTIVE, droneTest.isDroneOn());
//    }
//
//    @Test
//    void decreaseBatteryTest() {
//        BasicDrone droneTest = new BasicDrone();
//        droneTest.setBatteryLevel(100);
//        droneTest.simulateBatteryDrain();
//
//        assertEquals(99, droneTest.getBatteryLevel());
//    }
//}