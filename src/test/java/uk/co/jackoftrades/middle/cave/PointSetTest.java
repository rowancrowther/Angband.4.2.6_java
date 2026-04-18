package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointSetTest {
    private PointSet ps;
    private final Loc loc0 = Loc.zero;
    private final Loc loc1 = loc0.locOffset(2, 5);
    private final Loc loc2 = loc1.randLoc(12, 73);

    @BeforeEach
    void setUp() {
        ps = new PointSet();
    }

    @Test
    void add() {
        Loc loc3 = new Loc(-1, -1);

        ps.add(loc0);
        ps.add(loc1);
        ps.add(loc2);

        assertAll(
                () -> assertTrue(ps.contains(loc0)),
                () -> assertTrue(ps.contains(loc1)),
                () -> assertTrue(ps.contains(loc2)),
                () -> assertFalse(ps.contains(loc3))
        );
    }

    @Test
    void contains() {
        add();
    }

    @Test
    void size() {
        ps.add(loc0);
        ps.add(loc1);
        ps.add(loc2);

        assertEquals(3, ps.size());
    }
}