//import Model.Drone;
//import Model.TelemetryData;
//import service.AnomalyDetector;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//public class AnomalyDetectorTest {
//    AnomalyDetector myAnomalyDetector = new AnomalyDetector();
//
//    final int BATTERY_THRESHOLD = 15;
//    final double DROP_THRESHOLD = 50.0;
//    final double JUMP_THRESHOLD = 70.0;
//    final double TURN_THRESHOLD = 50.0;
//    final double VELOCITY_THRESHOLD = 50.0;
//    //Lower bound latitude, lower bound longitude, upper bound latitude, upper bound longitude.
//    final double[] OUT_OF_BOUNDS = {-1000.0, -1000.0, 1000.0, 1000.0};
//    final double TELEPORT_MARGIN_OF_ERROR = 1.5;
//
//    //Tests for boolean detectTeleport(TelemetryData thePrior, TelemetryData theCurrent, double theTimeStep)
//    @Test
//    void noTeleportingNoMovement() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//
//        prior.setLatitude(5.0);
//        prior.setLongitude(5.0);
//        prior.setOrientation(30);
//        prior.setVelocity(0.0);
//
//        current.setLatitude(5.0);
//        current.setLongitude(5.0);
//
//        boolean result = myAnomalyDetector.detectTeleport(prior, current, 2);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void teleporting() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//
//        prior.setLatitude(5.0);
//        prior.setLongitude(5.0);
//        prior.setOrientation(30);
//        prior.setVelocity(20.0);
//
//        current.setLatitude((5.0 + (20 * Math.cos((30 * Math.PI) / 180) * 2)) * TELEPORT_MARGIN_OF_ERROR * 2);
//        current.setLongitude(5.0 + (20 * Math.sin((30 * Math.PI) / 180) * 2) * TELEPORT_MARGIN_OF_ERROR * 2);
//
//        boolean result = myAnomalyDetector.detectTeleport(prior, current, 2);
//
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void noTeleporting() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//
//        prior.setLatitude(5.0);
//        prior.setLongitude(5.0);
//        prior.setOrientation(30);
//        prior.setVelocity(20.0);
//
//        current.setLatitude(5.0 + (20 * Math.cos((30 * Math.PI) / 180) * 2));
//        current.setLongitude(5.0 + (20 * Math.sin((30 * Math.PI) / 180) * 2));
//
//        boolean result = myAnomalyDetector.detectTeleport(prior, current, 2);
//
//        Assertions.assertFalse(result);
//    }
//
//    //Tests for boolean sharingLocations(Drone[] theDrones)
//    @Test
//    void sharingLongitudeAndLatitude() {
//        Drone drone1 = new Drone();
//        TelemetryData data1 = new TelemetryData();
//        Drone drone2 = new Drone();
//        TelemetryData data2 = new TelemetryData();
//
//        data1.setLatitude(1.0);
//        data1.setLongitude(1.0);
//
//        data2.setLatitude(1.0);
//        data2.setLongitude(1.0);
//
////        drone1.updateTelemetryData(data1);
////        drone2.updateTelemetryData(data2);
//
//        Drone[] drones = {drone1, drone2};
//        boolean result = myAnomalyDetector.detectSharingLocations(drones);
//
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void sharingLatitudeNotLongitude() {
//        Drone drone1 = new Drone();
//        TelemetryData data1 = new TelemetryData();
//        Drone drone2 = new Drone();
//        TelemetryData data2 = new TelemetryData();
//
//        data1.setLatitude(1.0);
//        data1.setLongitude(1.0);
//
//        data2.setLatitude(1.0);
//        data2.setLongitude(2.0);
//
////        drone1.updateTelemetryData(data1);
////        drone2.updateTelemetryData(data2);
//
//        Drone[] drones = {drone1, drone2};
//        boolean result = myAnomalyDetector.detectSharingLocations(drones);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void sharingLongitudeNotLatitude() {
//        Drone drone1 = new Drone();
//        TelemetryData data1 = new TelemetryData();
//        Drone drone2 = new Drone();
//        TelemetryData data2 = new TelemetryData();
//
//        data1.setLatitude(1.0);
//        data1.setLongitude(1.0);
//
//        data2.setLatitude(2.0);
//        data2.setLongitude(1.0);
//
////        drone1.updateTelemetryData(data1);
////        drone2.updateTelemetryData(data2);
//
//        Drone[] drones = {drone1, drone2};
//        boolean result = myAnomalyDetector.detectSharingLocations(drones);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void noSharingLocations() {
//        Drone drone1 = new Drone();
//        TelemetryData data1 = new TelemetryData();
//        Drone drone2 = new Drone();
//        TelemetryData data2 = new TelemetryData();
//
//        data1.setLatitude(1.0);
//        data1.setLongitude(1.0);
//
//        data2.setLatitude(2.0);
//        data2.setLongitude(2.0);
//
////        drone1.updateTelemetryData(data1);
////        drone2.updateTelemetryData(data2);
//
//        Drone[] drones = {drone1, drone2};
//        boolean result = myAnomalyDetector.detectSharingLocations(drones);
//
//        Assertions.assertFalse(result);
//    }
//
//    //Tests for boolean outOfBounds(TelemetryData theState)
//    @Test
//    void outOfBoundsAtLowerThreshold() {
//        TelemetryData testData = new TelemetryData();
//        testData.setLatitude(OUT_OF_BOUNDS[0] - 100.0);
//        testData.setLongitude(OUT_OF_BOUNDS[1] - 100.0);
//
//        boolean result = myAnomalyDetector.outOfBounds(testData);
//
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void outOfBoundsAtUpperThreshold() {
//        TelemetryData testData = new TelemetryData();
//        testData.setLatitude(OUT_OF_BOUNDS[2] + 100.0);
//        testData.setLongitude(OUT_OF_BOUNDS[3] + 100.0);
//
//        boolean result = myAnomalyDetector.outOfBounds(testData);
//
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void notOutOfBoundsAtUpperThreshold() {
//        TelemetryData testData = new TelemetryData();
//        testData.setLatitude(OUT_OF_BOUNDS[2]);
//        testData.setLongitude(OUT_OF_BOUNDS[3]);
//
//        boolean result = myAnomalyDetector.outOfBounds(testData);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void notOutOfBoundsAtLowerThreshold() {
//        TelemetryData testData = new TelemetryData();
//        testData.setLatitude(OUT_OF_BOUNDS[0]);
//        testData.setLongitude(OUT_OF_BOUNDS[1]);
//
//        boolean result = myAnomalyDetector.outOfBounds(testData);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void notOutOfBoundsLessThanLowerThreshold() {
//        TelemetryData testData = new TelemetryData();
//        testData.setLatitude(OUT_OF_BOUNDS[0] / 2.0);
//        testData.setLongitude(OUT_OF_BOUNDS[1] / 2.0);
//
//        boolean result = myAnomalyDetector.outOfBounds(testData);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void notOutOfBoundsLessThanUpperThreshold() {
//        TelemetryData testData = new TelemetryData();
//        testData.setLatitude(OUT_OF_BOUNDS[2] / 2.0);
//        testData.setLongitude(OUT_OF_BOUNDS[3] / 2.0);
//
//        boolean result = myAnomalyDetector.outOfBounds(testData);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void notOutOfBoundsAllZeroes() {
//        TelemetryData testData = new TelemetryData();
//        testData.setLatitude(0.0);
//        testData.setLongitude(0.0);
//
//        boolean result = myAnomalyDetector.outOfBounds(testData);
//
//        Assertions.assertFalse(result);
//    }
//
//    //Tests for boolean isBatteryLow(theDrone)
//    @Test
//    void lowTestOneHundred() {
//        Drone testDrone = new Drone();
//        testDrone.setBatteryLevel(100);
//
//        boolean result = myAnomalyDetector.isBatteryLow(testDrone);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void lowTestLow() {
//        Drone testDrone = new Drone();
//        testDrone.setBatteryLevel(BATTERY_THRESHOLD);
//
//        boolean result = myAnomalyDetector.isBatteryLow(testDrone);
//
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void lowTestNegative() {
//        Drone testDrone = new Drone();
//        testDrone.setBatteryLevel(-BATTERY_THRESHOLD);
//
//        boolean result = myAnomalyDetector.isBatteryLow(testDrone);
//
//        Assertions.assertTrue(result);
//    }
//
//    //Tests for boolean isBatteryEmpty(theDrone)
//    @Test
//    void zeroTestPositive() {
//        Drone testDrone = new Drone();
//        testDrone.setBatteryLevel(100);
//
//        boolean result = myAnomalyDetector.isBatteryEmpty(testDrone);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void zeroTestZero() {
//        Drone testDrone = new Drone();
//        testDrone.setBatteryLevel(0);
//
//        boolean result = myAnomalyDetector.isBatteryEmpty(testDrone);
//
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void zeroTestNegative() {
//        Drone testDrone = new Drone();
//        testDrone.setBatteryLevel(-100);
//
//        boolean result = myAnomalyDetector.isBatteryEmpty(testDrone);
//
//        Assertions.assertFalse(result);
//    }
//
//    //Tests for boolean isBatteryNegative(theDrone)
//    @Test
//    void negativeTestPositive() {
//        Drone testDrone = new Drone();
//        testDrone.setBatteryLevel(100);
//
//        boolean result = myAnomalyDetector.isBatteryNegative(testDrone);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void negativeTestZero() {
//        Drone testDrone = new Drone();
//        testDrone.setBatteryLevel(0);
//
//        boolean result = myAnomalyDetector.isBatteryNegative(testDrone);
//
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void negativeTestNegative() {
//        Drone testDrone = new Drone();
//        testDrone.setBatteryLevel(-100);
//
//        boolean result = myAnomalyDetector.isBatteryNegative(testDrone);
//
//        Assertions.assertTrue(result);
//    }
//
//    //Tests for boolean detectSuddenDrop(Telemetry prior, Telemetry current)
//    @Test
//    void droneJumpBelowThreshold() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//        prior.setAltitude(0.0);
//        current.setAltitude(JUMP_THRESHOLD / 2.0);
//
//        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void droneJumpBeyondThreshold() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//        prior.setAltitude(0.0);
//        current.setAltitude(JUMP_THRESHOLD + 100.0);
//
//        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void droneDropBeyondThreshold() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//        prior.setAltitude(0.0);
//        current.setAltitude(-DROP_THRESHOLD - 100.0);
//
//        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void droneDropAltitude() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//        prior.setAltitude(0.0);
//        current.setAltitude(-DROP_THRESHOLD);
//
//        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void droneNoAltitudeChange() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//        prior.setAltitude(0.0);
//        current.setAltitude(0.0);
//
//        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
//        Assertions.assertFalse(result);
//    }
//
//    //Tests for boolean detectSharpTurns(Telemetry prior, Telemetry current)
//    @Test
//    void droneTurnBelowThreshold() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//        prior.setOrientation(0.0);
//        current.setOrientation(TURN_THRESHOLD / 2.0);
//
//        boolean result = myAnomalyDetector.detectSharpTurns(prior, current);
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void droneTurnBeyondThreshold() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//        prior.setOrientation(0.0);
//        current.setOrientation(TURN_THRESHOLD + 100.0);
//
//        boolean result = myAnomalyDetector.detectSharpTurns(prior, current);
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void droneTurnNegativeThreshold() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//        prior.setOrientation(0.0);
//        current.setOrientation(-TURN_THRESHOLD);
//
//        boolean result = myAnomalyDetector.detectSharpTurns(prior, current);
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void droneNoTurn() {
//        TelemetryData prior = new TelemetryData();
//        TelemetryData current = new TelemetryData();
//        prior.setOrientation(0.0);
//        current.setOrientation(0.0);
//
//        boolean result = myAnomalyDetector.detectSharpTurns(prior, current);
//        Assertions.assertFalse(result);
//    }
//
//    //Tests for boolean detectTooFast(Telemetry)
//    @Test
//    void droneVelocityLow() {
//        TelemetryData testData = new TelemetryData();
//        testData.setVelocity(VELOCITY_THRESHOLD / 2.0);
//
//        boolean result = myAnomalyDetector.detectTooFast(testData);
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void droneVelocityBeyondThreshold() {
//        TelemetryData testData = new TelemetryData();
//        testData.setVelocity(VELOCITY_THRESHOLD + 100.0);
//
//        boolean result = myAnomalyDetector.detectTooFast(testData);
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void droneVelocityThreshold() {
//        TelemetryData testData = new TelemetryData();
//        testData.setVelocity(VELOCITY_THRESHOLD);
//
//        boolean result = myAnomalyDetector.detectTooFast(testData);
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void droneVelocityNegative() {
//        TelemetryData testData = new TelemetryData();
//        testData.setVelocity(-VELOCITY_THRESHOLD / 2.0);
//
//        boolean result = myAnomalyDetector.detectTooFast(testData);
//        Assertions.assertFalse(result);
//    }
//
//    //Tests for boolean isFlyingBackwards(TelemetryData)
//    @Test
//    void droneBackwards() {
//        TelemetryData testData = new TelemetryData();
//        testData.setVelocity(-15.0);
//
//        boolean result = myAnomalyDetector.isFlyingBackwards(testData);
//        Assertions.assertTrue(result);
//    }
//
//    @Test
//    void droneForward() {
//        TelemetryData testData = new TelemetryData();
//        testData.setVelocity(15.0);
//
//        boolean result = myAnomalyDetector.isFlyingBackwards(testData);
//        Assertions.assertFalse(result);
//    }
//
//    @Test
//    void droneVelocityZero() {
//        TelemetryData testData = new TelemetryData();
//        testData.setVelocity(0.0);
//
//        boolean result = myAnomalyDetector.isFlyingBackwards(testData);
//        Assertions.assertFalse(result);
//    }
//}
