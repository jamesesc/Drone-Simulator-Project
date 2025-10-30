package Simulation;

import Model.TelemetryData;

import java.util.Random;

public class TelemetryGenerator {

    private static final Random randomNumGenerator = new Random();

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


        /* Velocity: 0-100 m/s
        60-100 m/s: High
        30-60 m/s: Average
        10-30 m/s: Average
        0-10 m/s: Low
        */

        int[][] velocityRange = {
                {0,10},
                {11, 30},
                {31, 60},
                {61, 101}
        };

        int[] velocityRangeWeight = {15, 45, 35, 5};


        theTelemetryData.setVelocity(velocityPicker(velocityRange, velocityRangeWeight));
    }

    private static double velocityPicker(final int[][] theVelocityRange, final int[] theVelocityRangeWeight) {
        int totalWeightSum = 0;
        for (int weight : theVelocityRangeWeight) {
            totalWeightSum += weight;
        }

        int velocityRangePicker = randomNumGenerator.nextInt(totalWeightSum);

        int velocityRangeIndex = 0;
        int totalSum = 0;

        for (int i = 0; i < theVelocityRangeWeight.length; i++) {
            totalSum += theVelocityRangeWeight[i];
            if (velocityRangePicker < totalSum) {
                velocityRangeIndex = i;
                break;
            } else {
                totalSum += theVelocityRangeWeight[i];
            }
        }

        // random.nextInt(upperBound - lowerBound) + lowerBound;

        int upperBound = theVelocityRange[velocityRangeIndex][1];
        int lowerBound = theVelocityRange[velocityRangeIndex][0];

        return Math.round(lowerBound + (upperBound - lowerBound) * randomNumGenerator.nextDouble());
    }

    public static void main(String[] args) {
        TelemetryData telemetryDataTest = new TelemetryData();
        TelemetryGenerator telemetryGenTest = new TelemetryGenerator();
        for (int i = 0; i < 100; i++) {
            telemetryGenTest.generateVelocity(telemetryDataTest);
            System.out.println(telemetryDataTest.getVelocity());
        }

    }
}