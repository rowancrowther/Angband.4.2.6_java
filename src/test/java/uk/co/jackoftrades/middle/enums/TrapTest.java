package uk.co.jackoftrades.middle.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TrapTest {
    private final Trap none = Trap.TRF_NONE;
    private final Trap glyph = Trap.TRF_GLYPH;
    private final Trap trap = Trap.TRF_TRAP;
    private final Trap visible = Trap.TRF_VISIBLE;
    private final Trap invisible = Trap.TRF_INVISIBLE;
    private final Trap floor = Trap.TRF_FLOOR;
    private final Trap down = Trap.TRF_DOWN;
    private final Trap pit = Trap.TRF_PIT;
    private final Trap oneTime = Trap.TRF_ONETIME;
    private final Trap magical = Trap.TRF_MAGICAL;
    private final Trap saveThrow = Trap.TRF_SAVE_THROW;
    private final Trap saveArmour = Trap.TRF_SAVE_ARMOUR;
    private final Trap lock = Trap.TRF_LOCK;
    private final Trap delay = Trap.TRF_DELAY;
    private final Trap web = Trap.TRF_WEB;

    ArrayList<Trap> allValues;

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();
        allValues.add(none);
        allValues.add(glyph);
        allValues.add(trap);
        allValues.add(visible);
        allValues.add(invisible);
        allValues.add(floor);
        allValues.add(down);
        allValues.add(pit);
        allValues.add(oneTime);
        allValues.add(magical);
        allValues.add(saveThrow);
        allValues.add(saveArmour);
        allValues.add(lock);
        allValues.add(delay);
        allValues.add(web);
    }

    @Test
    void getDescription() {
        assertAll(
                () -> assertEquals("", none.getDescription()),
                () -> assertEquals("Is a glyph", glyph.getDescription()),
                () -> assertEquals("Is a player trap", trap.getDescription()),
                () -> assertEquals("Is visible", visible.getDescription()),
                () -> assertEquals("Is invisible", invisible.getDescription()),
                () -> assertEquals("Can be set on a floor", floor.getDescription()),
                () -> assertEquals("Takes the player down a level", down.getDescription()),
                () -> assertEquals("Moves the player onto the trap", pit.getDescription()),
                () -> assertEquals("Disappears after being activated", oneTime.getDescription()),
                () -> assertEquals("Has magical activation _absence of this flag means physical),", magical.getDescription()),
                () -> assertEquals("Allows a save from all effects by standard saving throw", saveThrow.getDescription()),
                () -> assertEquals("Allows a save from all effects due to AC", saveArmour.getDescription()),
                () -> assertEquals("Is a door lock", lock.getDescription()),
                () -> assertEquals("Has a delayed effect", delay.getDescription()),
                () -> assertEquals("Is a web", web.getDescription())
        );
    }

    @Test
    void values() {
        for (Trap t : Trap.values()) {
            if (!allValues.contains(t))
                fail("Trap: " + t.toString() + " not found in manual list.");
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(none, Trap.valueOf("TRF_NONE")),
                () -> assertEquals(glyph, Trap.valueOf("TRF_GLYPH")),
                () -> assertEquals(trap, Trap.valueOf("TRF_TRAP")),
                () -> assertEquals(visible, Trap.valueOf("TRF_VISIBLE")),
                () -> assertEquals(invisible, Trap.valueOf("TRF_INVISIBLE")),
                () -> assertEquals(floor, Trap.valueOf("TRF_FLOOR")),
                () -> assertEquals(down, Trap.valueOf("TRF_DOWN")),
                () -> assertEquals(pit, Trap.valueOf("TRF_PIT")),
                () -> assertEquals(oneTime, Trap.valueOf("TRF_ONETIME")),
                () -> assertEquals(magical, Trap.valueOf("TRF_MAGICAL")),
                () -> assertEquals(saveThrow, Trap.valueOf("TRF_SAVE_THROW")),
                () -> assertEquals(saveArmour, Trap.valueOf("TRF_SAVE_ARMOUR")),
                () -> assertEquals(lock, Trap.valueOf("TRF_LOCK")),
                () -> assertEquals(delay, Trap.valueOf("TRF_DELAY")),
                () -> assertEquals(web, Trap.valueOf("TRF_WEB"))
        );
    }
}