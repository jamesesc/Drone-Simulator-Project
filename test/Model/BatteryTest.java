package Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatteryTest {
    @Test
    void batteryLevel() {
        Battery b = new Battery();
        b.setLevel(90);

        assertEquals(90, b.getLevel());
    }

    @Test
    void batteryLowLevel() {
        Battery b = new Battery();
        assertAll(() -> {
            assertThrows(IllegalArgumentException.class, () -> b.setLevel(-10));
            assertThrows(IllegalArgumentException.class, () -> b.setLevel(200));
        });
    }

    @Test
    void batteryRecharge() {
        Battery b = new Battery();
        b.setLevel(50);
        b.recharge();
        assertEquals(65, b.getLevel());
    }

    @Test
    void batteryMaxLevelRecharge() {
        Battery b = new Battery();
        b.setLevel(100);
        b.recharge();
        assertEquals(100, b.getLevel());
    }

    @Test
    void testDrainVelocityZero() {
        Battery battery = new Battery();
        battery.setLevel(50);

        battery.drain(0);

        assertEquals(49, battery.getLevel());
    }

    @Test
    void testDrainFormulaRoundsZero() {
        Battery battery = new Battery();
        battery.setLevel(50);

        battery.drain(5);

        assertEquals(49, battery.getLevel());
    }

    @Test
    void testDrain() {
        Battery battery = new Battery();
        battery.setLevel(100);

        battery.drain(20);

        assertEquals(98, battery.getLevel());
    }

    @Test
    void testDrainMax() {
        Battery battery = new Battery();
        battery.setLevel(100);

        battery.drain(200);

        assertEquals(70, battery.getLevel());
    }

    @Test
    void testDrainBelowZero() {
        Battery battery = new Battery();
        battery.setLevel(5);

        battery.drain(100);

        assertEquals(0, battery.getLevel());
    }

    @Test
    void testDrainBeforeMax() {
        Battery battery = new Battery();
        battery.setLevel(50);

        battery.drain(70);

        assertEquals(26, battery.getLevel());
    }

    @Test
    void testDrainAtMax() {
        Battery battery = new Battery();
        battery.setLevel(80);

        battery.drain(80);

        assertEquals(50, battery.getLevel());
    }
}
