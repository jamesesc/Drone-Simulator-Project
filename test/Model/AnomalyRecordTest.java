package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AnomalyRecordTest {
    String EMPTY_STRING = "";

    @Test
    void emptyConstructor() {
        AnomalyRecord a = new AnomalyRecord();

        assertAll(() -> {
            assertEquals(EMPTY_STRING, a.getType());
            assertNull(a.getID());
            assertEquals(EMPTY_STRING, a.getDetails());
            assertEquals(0.0, a.getTime());
        });
    }

    @Test
    void fourParamConstructor() {
        AnomalyRecord a = new AnomalyRecord("Hello", 1, 2.0, "Bye");

        assertAll(() -> {
            assertEquals("Hello", a.getType());
            assertEquals(1, a.getID());
            assertEquals("Bye", a.getDetails());
            assertEquals(2.0, a.getTime());
        });
    }

    @Test
    void testSetters() {
        AnomalyRecord a = new AnomalyRecord("Bye", 2, 1.0, "Hello");

        a.setType("Hello");
        a.setID(1);
        a.setTime(2.0);
        a.setDetails("Bye");

        assertAll(() -> {
            assertEquals("Hello", a.getType());
            assertEquals(1, a.getID());
            assertEquals("Bye", a.getDetails());
            assertEquals(2.0, a.getTime());
        });
    }
}
