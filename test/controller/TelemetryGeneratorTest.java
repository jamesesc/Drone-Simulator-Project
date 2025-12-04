//package controller;
//
//import Model.TelemetryData;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TelemetryGeneratorTest {
//
//
//
//}
//
//
//public static void main(String[] args) {
//    testVelocityGen();
//
//    //testVelocityGenSimple();
//}
//
//// Helper method to test the simple velocity gen
//private static void testVelocityGenSimple() {
//    TelemetryData telemetryDataTest = new TelemetryData();
//    TelemetryGenerator telemetryGenTest = new TelemetryGenerator();
//    for (int i = 0; i < 100; i++) {
//        telemetryGenTest.generateVelocitySimple(telemetryDataTest);
//        System.out.println(telemetryDataTest.getVelocity());
//    }
//}
//
//// Helper method to verify for the functionality and correctness of generate velocity method
//private static void testVelocityGen() {
//    TelemetryData oldTelemetryData = new TelemetryData();
//
//    /* used to set the current velocity */
//    oldTelemetryData.setVelocity(50);
//
//    TelemetryGenerator telemetryGenTest = new TelemetryGenerator();
//
//    // for the # of test
//    int testNum = 1000000;
//
//    // Used to count the category
//    int leisurely = 0, slow = 0, cruising = 0, fast = 0, hyper = 0;
//
//    // A for loop to count the new generated value and which category it goes too
//    for (int i = 0; i < testNum; i++) {
//        TelemetryData newTelemetry = new TelemetryData();
//        telemetryGenTest.generateVelocity(newTelemetry, oldTelemetryData);
//        double newValue = newTelemetry.getVelocity();
//
//        // Adding up the new where to which category
//        if (newValue >= 0 && newValue <= 5) {
//            leisurely++;
//        } else if (newValue >= 6 && newValue <= 15) {
//            slow++;
//        } else if (newValue >= 16 && newValue <= 30) {
//            cruising++;
//        } else if (newValue >= 31 && newValue <= 45) {
//            fast++;
//        } else if (newValue >= 46 && newValue <= 50) {
//            hyper++;
//        }
//    }
//
//    // Printing out the output with the # and percentage it is out of 100
//    System.out.println("Leisurely (0-5):    " + leisurely + "  =  " + String.format("%.1f", leisurely * 100.0 / testNum) + "%");
//    System.out.println("Slow (6-15):        " + slow + "  =  " + String.format("%.1f", slow * 100.0 / testNum) + "%");
//    System.out.println("Cruising (16-30):   " + cruising + "  =  " + String.format("%.1f", cruising * 100.0 / testNum) + "%");
//    System.out.println("Fast (31-45):       " + fast + "  =  " + String.format("%.1f", fast * 100.0 / testNum) + "%");
//    System.out.println("Hyper (46-50):      " + hyper + "  =  " + String.format("%.1f", hyper * 100.0 / testNum) + "%");
//
//    // Checking if the # of tests equals to the number in testNum
//    System.out.println("Total count match: " + (testNum == (leisurely + slow + cruising + fast + hyper)));
//}