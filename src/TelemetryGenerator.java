import Model.TelemetryData;

import java.util.Random;

public class TelemetryGenerator {

    private final Random randomNumGenerator = new Random();

    public TelemetryData generateTelemetryData() {
        final TelemetryData telemetryData = new TelemetryData();
        generateAll(telemetryData);
        return telemetryData;
    }

    private void generateAll(final TelemetryData theTelemetryData) {
        generateLatitude(theTelemetryData);
        generateLongitude(theTelemetryData);
        generateAltitude(theTelemetryData);
        generateOrientation(theTelemetryData);
        generateVelocity(theTelemetryData);
    }

    private void generateLatitude(final TelemetryData theTelemetryData) {
        // Random class generate 0.0 to 1.0, we need * 100 to generate 0 to 100
        // This double is randomly generating a number between 0 and 100
        final double randomLatitude  = randomNumGenerator.nextDouble() * 100;
        theTelemetryData.setLatitude(randomLatitude);
    }

    private void generateLongitude(final TelemetryData theTelemetryData) {
        final double randomLongitude = randomNumGenerator.nextDouble() * 100;
        theTelemetryData.setLongitude(randomLongitude);
    }

    private void generateAltitude(final TelemetryData theTelemetryData) {
        final double randomAltitude = randomNumGenerator.nextDouble() * 100;
        theTelemetryData.setAltitude(randomAltitude);
    }

    private void generateOrientation(final TelemetryData theTelemetryData) {
        final double randomOrientation = randomNumGenerator.nextDouble() * 100;
        theTelemetryData.setOrientation(randomOrientation);
    }

    private void generateVelocity(final TelemetryData theTelemetryData) {
        final double randomVelocity = randomNumGenerator.nextDouble() * 100;
        theTelemetryData.setVelocity(randomVelocity);
    }
}