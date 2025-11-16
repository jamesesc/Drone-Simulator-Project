package Simulation;

import Model.TelemetryData;
import controller.TelemetryGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryGeneratorTest {

    @Test
    void telemetryDataNotNull() {
        TelemetryGenerator telemetryGenTest = new TelemetryGenerator();

        TelemetryData telemetryDataResult = telemetryGenTest.generateTelemetryData();

        assertNotNull(telemetryDataResult);
    }


    @Test
    void telemetryDataValid() {
        TelemetryGenerator telemetryGenTest = new TelemetryGenerator();

        TelemetryData telemetryDataResult = telemetryGenTest.generateTelemetryData();

        final double latitudeChecker = telemetryDataResult.getAltitude();
        final double longitudeChecker = telemetryDataResult.getLongitude();
        final double altitudeChecker = telemetryDataResult.getAltitude();
        final double orientationChecker = telemetryDataResult.getOrientation();
        final double velocityChecker = telemetryDataResult.getVelocity();

        assertTrue(latitudeChecker <= 100 && latitudeChecker >= 0);
        assertTrue(longitudeChecker <= 100 && longitudeChecker >= 0);
        assertTrue(altitudeChecker <= 100 && altitudeChecker >= 0);
        assertTrue(orientationChecker <= 100 && orientationChecker >= 0);
        assertTrue(velocityChecker <= 100 && velocityChecker >= 0);
    }

    @Test
    void multipleTelemetryData() {
        TelemetryGenerator telemetryGenTest = new TelemetryGenerator();

        TelemetryData telemetryDataResult = telemetryGenTest.generateTelemetryData();

        final double latitudeChecker1 = telemetryDataResult.getAltitude();

        telemetryDataResult = telemetryGenTest.generateTelemetryData();

        final double latitudeChecker2 = telemetryDataResult.getAltitude();

        assertNotEquals(latitudeChecker1, latitudeChecker2);
    }

    @Test
    void twoTelemetryDataNotTheSame() {
        TelemetryGenerator telemetryGenTest = new TelemetryGenerator();

        TelemetryData telemetryData1 = telemetryGenTest.generateTelemetryData();
        TelemetryData telemetryData2 = telemetryGenTest.generateTelemetryData();

        assertNotSame(telemetryData1, telemetryData2);
    }
}