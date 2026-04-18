package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.Test;

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
}