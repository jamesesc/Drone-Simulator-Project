package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryDataTest {

    @Test
    void getLatitude() {
        TelemetryData telDataTest = new TelemetryData();

        telDataTest.setLatitude(50);

        assertEquals(50,telDataTest.getLatitude());
    }

    @Test
    void getLongitude() {
        TelemetryData telDataTest = new TelemetryData();

        telDataTest.setLongitude(25);

        assertEquals(25, telDataTest.getLongitude());


    }

    @Test
    void getAltitude() {
        TelemetryData telDataTest = new TelemetryData();

        telDataTest.setAltitude(67);

        assertEquals(67, telDataTest.getAltitude());

    }

    @Test
    void getOrientation() {
        TelemetryData telDataTest = new TelemetryData();

        telDataTest.setOrientation(100);

        assertEquals(100, telDataTest.getOrientation());
    }

    @Test
    void getVelocity() {
        TelemetryData telDataTest = new TelemetryData();

        telDataTest.setVelocity(5);

        assertEquals(5, telDataTest.getVelocity());

    }

}