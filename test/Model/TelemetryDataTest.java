//package Model;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TelemetryDataTest {
//    @Test
//    void emptyConstructor() {
//        TelemetryData telDataTest = new TelemetryData();
//
//        assertAll(() -> {
//            assertEquals(0, telDataTest.getAltitude());
//            assertEquals(0, telDataTest.getVelocity());
//            assertEquals(0, telDataTest.getLongitude());
//            assertEquals(0, telDataTest.getLatitude());
//            assertEquals(0, telDataTest.getOrientation());
//        });
//    }
//
//    @Test
//    void fullConstructor() {
//        TelemetryData telDataTest = new TelemetryData(1, 2, 3, 4, 5);
//
//        assertAll(() -> {
//            assertEquals(3, telDataTest.getAltitude());
//            assertEquals(5, telDataTest.getVelocity());
//            assertEquals(2, telDataTest.getLongitude());
//            assertEquals(1, telDataTest.getLatitude());
//            assertEquals(4, telDataTest.getOrientation());
//        });
//    }
//
//    @Test
//    void copyConstructor() {
//        TelemetryData telData = new TelemetryData(1, 2, 3, 4, 5);
//
//        TelemetryData telDataTest = new TelemetryData(telData);
//
//        assertAll(() -> {
//            assertEquals(3, telDataTest.getAltitude());
//            assertEquals(5, telDataTest.getVelocity());
//            assertEquals(2, telDataTest.getLongitude());
//            assertEquals(1, telDataTest.getLatitude());
//            assertEquals(4, telDataTest.getOrientation());
//        });
//    }
//
//    @Test
//    void copyConstructorCreatesIndependentObject() {
//        TelemetryData original = new TelemetryData(1, 2, 3, 4, 5);
//        TelemetryData copy = new TelemetryData(original);
//
//        original.setLatitude(999);
//
//        assertNotEquals(original.getLatitude(), copy.getLatitude());
//    }
//
//    @Test
//    void testCopyConstructorNull() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            new TelemetryData(null);
//        });
//    }
//
//    @Test
//    void getLatitude() {
//        TelemetryData telDataTest = new TelemetryData();
//
//        telDataTest.setLatitude(50);
//
//        assertEquals(50,telDataTest.getLatitude());
//    }
//
//    @Test
//    void getLongitude() {
//        TelemetryData telDataTest = new TelemetryData();
//
//        telDataTest.setLongitude(25);
//
//        assertEquals(25, telDataTest.getLongitude());
//
//
//    }
//
//    @Test
//    void getAltitude() {
//        TelemetryData telDataTest = new TelemetryData();
//
//        telDataTest.setAltitude(67);
//
//        assertEquals(67, telDataTest.getAltitude());
//
//    }
//
//    @Test
//    void getOrientation() {
//        TelemetryData telDataTest = new TelemetryData();
//
//        telDataTest.setOrientation(100);
//
//        assertEquals(100, telDataTest.getOrientation());
//    }
//
//    @Test
//    void getVelocity() {
//        TelemetryData telDataTest = new TelemetryData();
//
//        telDataTest.setVelocity(5);
//
//        assertEquals(5, telDataTest.getVelocity());
//
//    }
//
//    @Test
//    void testEqualsSameObject() {
//        TelemetryData t1 = new TelemetryData(1.0, 2.0, 3.0, 4.0, 5.0);
//        assertEquals(t1, t1);
//    }
//
//    @Test
//    void testEqualsEqualObjects() {
//        TelemetryData t1 = new TelemetryData(1.0, 2.0, 3.0, 4.0, 5.0);
//        TelemetryData t2 = new TelemetryData(1.0, 2.0, 3.0, 4.0, 5.0);
//
//        assertAll(() -> {
//            assertEquals(t1, t2);
//            assertEquals(t2, t1);
//        });
//    }
//
//    @Test
//    void testEqualsTransitive() {
//        TelemetryData a = new TelemetryData(1, 2, 3, 4, 5);
//        TelemetryData b = new TelemetryData(1, 2, 3, 4, 5);
//        TelemetryData c = new TelemetryData(1, 2, 3, 4, 5);
//
//        assertAll(() -> {
//            assertEquals(a, b);
//            assertEquals(b, c);
//            assertEquals(a, c);
//        });
//    }
//
//    @Test
//    void testEqualsNull() {
//        TelemetryData t1 = new TelemetryData();
//        assertNotEquals(null, t1);
//    }
//
//    @Test
//    void testEqualsDifferentClass() {
//        TelemetryData t1 = new TelemetryData();
//        String other = "NotTelemetry";
//        assertNotEquals(other, t1);
//    }
//
//    @Test
//    void testNotEqualsDifferentLatitude() {
//        TelemetryData t1 = new TelemetryData(1, 2, 3, 4, 5);
//        TelemetryData t2 = new TelemetryData(9, 2, 3, 4, 5);
//
//        assertNotEquals(t1, t2);
//    }
//
//    @Test
//    void testNotEqualsDifferentLongitude() {
//        TelemetryData t1 = new TelemetryData(1, 2, 3, 4, 5);
//        TelemetryData t2 = new TelemetryData(1, 9, 3, 4, 5);
//
//        assertNotEquals(t1, t2);
//    }
//
//    @Test
//    void testNotEqualsDifferentAltitude() {
//        TelemetryData t1 = new TelemetryData(1, 2, 3, 4, 5);
//        TelemetryData t2 = new TelemetryData(1, 2, 9, 4, 5);
//
//        assertNotEquals(t1, t2);
//    }
//
//    @Test
//    void testNotEqualsDifferentOrientation() {
//        TelemetryData t1 = new TelemetryData(1, 2, 3, 4, 5);
//        TelemetryData t2 = new TelemetryData(1, 2, 3, 9, 5);
//
//        assertNotEquals(t1, t2);
//    }
//
//    @Test
//    void testNotEqualsDifferentVelocity() {
//        TelemetryData t1 = new TelemetryData(1, 2, 3, 4, 5);
//        TelemetryData t2 = new TelemetryData(1, 2, 3, 4, 9);
//
//        assertNotEquals(t1, t2);
//    }
//
//    @Test
//    void testNotSameObject() {
//        TelemetryData t = new TelemetryData(1, 2, 3, 4, 5);
//        BasicDrone d = new BasicDrone();
//
//        assertNotEquals(d, t);
//    }
//
//    @Test
//    void testNotEqualNull() {
//        TelemetryData t = new TelemetryData(1, 2, 3, 4, 5);
//
//        assertNotEquals(null, t);
//    }
//}