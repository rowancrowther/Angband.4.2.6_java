package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocTest {

    @Test
    void testEquals() {
        Loc result = new Loc(1, 3);
        Boolean Equals = result.equals(new Loc(1, 3));
        assertTrue(Equals);
    }

    @Test
    void isZero() {
        assertTrue(Loc.zero.isZero());
    }

    @Test
    void sum() {
        Loc a = new Loc(15, 21);
        Loc b = new Loc(-10, 7);

        Loc sum = a.sum(b);

        assertTrue(sum.equals(new Loc(5, 28)));
    }

    @Test
    void diff() {
        Loc a = new Loc(15, 21);
        Loc b = new Loc(-10, 7);

        Loc diff = a.diff(b);

        assertTrue(diff.equals(new Loc(25, 14)));
    }

    @Test
    void randLoc() {
        Loc start = new Loc(10, 15);
        Loc result = start.randLoc(10, 5);

        assertAll(
                () -> assertTrue(result.getX() < 20),
                () -> assertTrue(result.getX() >= 0),
                () -> assertTrue(result.getY() < 20),
                () -> assertTrue(result.getY() >= 10)
        );
    }

    @Test
    void locOffset() {
        Loc start = new Loc(10, 15);
        Loc expected = new Loc(13, 13);

        assertTrue(expected.equals(start.locOffset(3, -2)));
    }
}