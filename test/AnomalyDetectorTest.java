import Model.Drone;
import Model.TelemetryData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnomalyDetectorTest {
    AnomalyDetector myAnomalyDetector = new AnomalyDetector();

    final int BATTERY_THRESHOLD = 15;
    final double DROP_THRESHOLD = 50.0;
    final double JUMP_THRESHOLD = 70.0;
    final double TURN_THRESHOLD = 50.0;
    final double VELOCITY_THRESHOLD = 50.0;
    final double UPSIDE_DOWN = 360.0;

    //Tests for boolean isBatteryLow(theDrone)
    @Test
    void lowTestOneHundred() {
        Drone testDrone = new Drone();
        testDrone.setBatteryLevel(100);

        boolean result = myAnomalyDetector.isBatteryLow(testDrone);

        Assertions.assertFalse(result);
    }

    @Test
    void lowTestLow() {
        Drone testDrone = new Drone();
        testDrone.setBatteryLevel(BATTERY_THRESHOLD);

        boolean result = myAnomalyDetector.isBatteryLow(testDrone);

        Assertions.assertTrue(result);
    }

    @Test
    void lowTestNegative() {
        Drone testDrone = new Drone();
        testDrone.setBatteryLevel(-BATTERY_THRESHOLD);

        boolean result = myAnomalyDetector.isBatteryLow(testDrone);

        Assertions.assertTrue(result);
    }

    //Tests for boolean isBatteryEmpty(theDrone)
    @Test
    void zeroTestPositive() {
        Drone testDrone = new Drone();
        testDrone.setBatteryLevel(100);

        boolean result = myAnomalyDetector.isBatteryEmpty(testDrone);

        Assertions.assertFalse(result);
    }

    @Test
    void zeroTestZero() {
        Drone testDrone = new Drone();
        testDrone.setBatteryLevel(0);

        boolean result = myAnomalyDetector.isBatteryEmpty(testDrone);

        Assertions.assertTrue(result);
    }

    @Test
    void zeroTestNegative() {
        Drone testDrone = new Drone();
        testDrone.setBatteryLevel(-100);

        boolean result = myAnomalyDetector.isBatteryEmpty(testDrone);

        Assertions.assertFalse(result);
    }

    //Tests for boolean isBatteryNegative(theDrone)
    @Test
    void negativeTestPositive() {
        Drone testDrone = new Drone();
        testDrone.setBatteryLevel(100);

        boolean result = myAnomalyDetector.isBatteryNegative(testDrone);

        Assertions.assertFalse(result);
    }

    @Test
    void negativeTestZero() {
        Drone testDrone = new Drone();
        testDrone.setBatteryLevel(0);

        boolean result = myAnomalyDetector.isBatteryNegative(testDrone);

        Assertions.assertFalse(result);
    }

    @Test
    void negativeTestNegative() {
        Drone testDrone = new Drone();
        testDrone.setBatteryLevel(-100);

        boolean result = myAnomalyDetector.isBatteryNegative(testDrone);

        Assertions.assertTrue(result);
    }

    //Tests for boolean detectSuddenDrop(Telemetry prior, Telemetry current)
    @Test
    void droneJumpBelowThreshold() {
        TelemetryData prior = new TelemetryData();
        TelemetryData current = new TelemetryData();
        prior.setAltitude(0.0);
        current.setAltitude(JUMP_THRESHOLD / 2.0);

        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
        Assertions.assertFalse(result);
    }

    @Test
    void droneJumpBeyondThreshold() {
        TelemetryData prior = new TelemetryData();
        TelemetryData current = new TelemetryData();
        prior.setAltitude(0.0);
        current.setAltitude(JUMP_THRESHOLD + 100.0);

        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
        Assertions.assertTrue(result);
    }

    @Test
    void droneDropBeyondThreshold() {
        TelemetryData prior = new TelemetryData();
        TelemetryData current = new TelemetryData();
        prior.setAltitude(0.0);
        current.setAltitude(-DROP_THRESHOLD - 100.0);

        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
        Assertions.assertTrue(result);
    }

    @Test
    void droneDropAltitude() {
        TelemetryData prior = new TelemetryData();
        TelemetryData current = new TelemetryData();
        prior.setAltitude(0.0);
        current.setAltitude(-DROP_THRESHOLD);

        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
        Assertions.assertTrue(result);
    }

    @Test
    void droneNoAltitudeChange() {
        TelemetryData prior = new TelemetryData();
        TelemetryData current = new TelemetryData();
        prior.setAltitude(0.0);
        current.setAltitude(0.0);

        boolean result = myAnomalyDetector.detectSuddenDropJump(prior, current);
        Assertions.assertFalse(result);
    }

    //Tests for boolean detectSharpTurns(Telemetry prior, Telemetry current)
    @Test
    void droneTurnBelowThreshold() {
        TelemetryData prior = new TelemetryData();
        TelemetryData current = new TelemetryData();
        prior.setOrientation(0.0);
        current.setOrientation(TURN_THRESHOLD / 2.0);

        boolean result = myAnomalyDetector.detectSharpTurns(prior, current);
        Assertions.assertFalse(result);
    }

    @Test
    void droneTurnBeyondThreshold() {
        TelemetryData prior = new TelemetryData();
        TelemetryData current = new TelemetryData();
        prior.setOrientation(0.0);
        current.setOrientation(TURN_THRESHOLD + 100.0);

        boolean result = myAnomalyDetector.detectSharpTurns(prior, current);
        Assertions.assertTrue(result);
    }

    @Test
    void droneTurnNegativeThreshold() {
        TelemetryData prior = new TelemetryData();
        TelemetryData current = new TelemetryData();
        prior.setOrientation(0.0);
        current.setOrientation(-TURN_THRESHOLD);

        boolean result = myAnomalyDetector.detectSharpTurns(prior, current);
        Assertions.assertTrue(result);
    }

    @Test
    void droneNoTurn() {
        TelemetryData prior = new TelemetryData();
        TelemetryData current = new TelemetryData();
        prior.setOrientation(0.0);
        current.setOrientation(0.0);

        boolean result = myAnomalyDetector.detectSharpTurns(prior, current);
        Assertions.assertFalse(result);
    }

    //Tests for boolean detectTooFast(Telemetry)
    @Test
    void droneVelocityLow() {
        TelemetryData testData = new TelemetryData();
        testData.setVelocity(VELOCITY_THRESHOLD / 2.0);

        boolean result = myAnomalyDetector.detectTooFast(testData);
        Assertions.assertFalse(result);
    }

    @Test
    void droneVelocityBeyondThreshold() {
        TelemetryData testData = new TelemetryData();
        testData.setVelocity(VELOCITY_THRESHOLD + 100.0);

        boolean result = myAnomalyDetector.detectTooFast(testData);
        Assertions.assertTrue(result);
    }

    @Test
    void droneVelocityThreshold() {
        TelemetryData testData = new TelemetryData();
        testData.setVelocity(VELOCITY_THRESHOLD);

        boolean result = myAnomalyDetector.detectTooFast(testData);
        Assertions.assertTrue(result);
    }

    @Test
    void droneVelocityNegative() {
        TelemetryData testData = new TelemetryData();
        testData.setVelocity(-VELOCITY_THRESHOLD / 2.0);

        boolean result = myAnomalyDetector.detectTooFast(testData);
        Assertions.assertFalse(result);
    }

    //Tests for boolean isUpsideDown(TelemetryData)
    @Test
    void droneNotUpsideDown() {
        TelemetryData testData = new TelemetryData();
        testData.setOrientation(0.0);

        boolean result = myAnomalyDetector.isUpsideDown(testData);
        Assertions.assertFalse(result);
    }

    @Test
    void droneUpsideDownThreshold() {
        TelemetryData testData = new TelemetryData();
        testData.setOrientation(UPSIDE_DOWN);

        boolean result = myAnomalyDetector.isUpsideDown(testData);
        Assertions.assertTrue(result);
    }

    @Test
    void droneUpsideDownBeyondThreshold() {
        TelemetryData testData = new TelemetryData();
        testData.setOrientation(UPSIDE_DOWN + 100.0);

        boolean result = myAnomalyDetector.isUpsideDown(testData);
        Assertions.assertTrue(result);
    }

    @Test
    void droneUpsideDownBeyondNegative() {
        TelemetryData testData = new TelemetryData();
        testData.setOrientation(-UPSIDE_DOWN - 100.0);

        boolean result = myAnomalyDetector.isUpsideDown(testData);
        Assertions.assertTrue(result);
    }

    //Tests for boolean isFlyingBackwards(TelemetryData)
    @Test
    void droneBackwards() {
        TelemetryData testData = new TelemetryData();
        testData.setVelocity(-15.0);

        boolean result = myAnomalyDetector.isFlyingBackwards(testData);
        Assertions.assertTrue(result);
    }

    @Test
    void droneForward() {
        TelemetryData testData = new TelemetryData();
        testData.setVelocity(15.0);

        boolean result = myAnomalyDetector.isFlyingBackwards(testData);
        Assertions.assertFalse(result);
    }

    @Test
    void droneVelocityZero() {
        TelemetryData testData = new TelemetryData();
        testData.setVelocity(0.0);

        boolean result = myAnomalyDetector.isFlyingBackwards(testData);
        Assertions.assertFalse(result);
    }
}
