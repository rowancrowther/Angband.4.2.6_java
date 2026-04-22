package uk.co.jackoftrades.background.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.background.colour.enums.ColourTranslation;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FlagTest {
    Flag<ColourTranslation> flagSet;
    Flag<ColourTranslation> empty;
    Flag<ColourTranslation> full;

    @BeforeEach
    void setUp() {
        flagSet = new Flag<>(ColourTranslation.class);
        full = new Flag<>(ColourTranslation.class);
        empty = new Flag<>(ColourTranslation.class);

        flagSet.init(ColourTranslation.ATTR_BLIND, ColourTranslation.ATTR_HIGH, ColourTranslation.ATTR_DARK);
        full.init(ColourTranslation.ATTR_FULL, ColourTranslation.ATTR_MONO, ColourTranslation.ATTR_VGA,
                ColourTranslation.ATTR_BLIND, ColourTranslation.ATTR_LIGHT, ColourTranslation.ATTR_DARK,
                ColourTranslation.ATTR_HIGH, ColourTranslation.ATTR_METAL, ColourTranslation.ATTR_MISC,
                ColourTranslation.ATTR_MAX);
    }

    @Test
    void has() {
        assertAll(
                () -> assertTrue(flagSet.has(ColourTranslation.ATTR_DARK)),
                () -> assertFalse(flagSet.has(ColourTranslation.ATTR_LIGHT))
        );
    }

    @Test
    void next() {
        assertAll(
                () -> assertEquals(ColourTranslation.ATTR_HIGH, flagSet.next(ColourTranslation.ATTR_DARK)),
                () -> assertEquals(ColourTranslation.ATTR_MAX, flagSet.next(ColourTranslation.ATTR_FULL)),
                () -> assertEquals(ColourTranslation.ATTR_MAX, flagSet.next(ColourTranslation.ATTR_HIGH))
        );
    }

    @Test
    void count() {
        assertEquals(3, flagSet.count());
    }

    @Test
    void isEmpty() {
        assertTrue(empty.isEmpty());
    }

    @Test
    void isFull() {
        assertTrue(full.isFull());
    }

    @Test
    void isInter() {
        assertAll(
                () -> assertTrue(full.isInter(flagSet)),
                () -> assertFalse(empty.isInter(flagSet))
        );
    }

    @Test
    void isSubset() {
        assertAll(
                () -> assertTrue(full.isSubset(flagSet)),
                () -> assertFalse(empty.isSubset(flagSet))
        );
    }

    @Test
    void isEqual() {
        Flag<ColourTranslation> test = new Flag<>(ColourTranslation.class);
        test.on(ColourTranslation.ATTR_HIGH);
        test.on(ColourTranslation.ATTR_BLIND);
        test.on(ColourTranslation.ATTR_DARK);

        assertAll(
                () -> assertTrue(flagSet.isEqual(test)),
                () -> assertFalse(flagSet.isEqual(full))
        );
    }

    @Test
    void on() {
        ColourTranslation flag = ColourTranslation.ATTR_FULL;

        assertAll(
                () -> assertTrue(flagSet.on(flag)),
                () -> assertFalse(flagSet.on(flag))
        );
    }

    @Test
    void off() {
        ColourTranslation flag = ColourTranslation.ATTR_DARK;

        assertAll(
                () -> assertTrue(flagSet.off(flag)),
                () -> assertFalse(flagSet.off(flag))
        );
    }

    @Test
    void wipe() {
        flagSet.wipe();

        assertTrue(flagSet.isEqual(empty));
    }

    @Test
    void setAll() {
        flagSet.setAll();

        assertTrue(flagSet.isEqual(full));
    }

    @Test
    void negate() {
        full.negate();

        assertTrue(full.isEqual(empty));
    }

    @Test
    void copy() {
        Flag<ColourTranslation> copyOfFull = full.copy();
        assertTrue(copyOfFull.isEqual(full));
    }

    @Test
    void union() {
        Flag<ColourTranslation> other = empty.copy();
        other.on(ColourTranslation.ATTR_FULL);

        boolean changes = flagSet.union(other);

        assertAll(
                () -> assertTrue(flagSet.has(ColourTranslation.ATTR_DARK)),
                () -> assertTrue(flagSet.has(ColourTranslation.ATTR_HIGH)),
                () -> assertTrue(flagSet.has(ColourTranslation.ATTR_BLIND)),
                () -> assertTrue(flagSet.has(ColourTranslation.ATTR_FULL)),
                () -> assertFalse(flagSet.has(ColourTranslation.ATTR_MAX)),
                () -> assertTrue(changes)
        );
    }

    @Test
    void inter() {
        Flag<ColourTranslation> other = empty.copy();
        other.on(ColourTranslation.ATTR_FULL);
        other.on(ColourTranslation.ATTR_MAX);
        other.on(ColourTranslation.ATTR_HIGH);
        boolean result = flagSet.inter(other);

        assertAll(
                () -> assertFalse(flagSet.has(ColourTranslation.ATTR_DARK)),
                () -> assertTrue(flagSet.has(ColourTranslation.ATTR_HIGH)),
                () -> assertFalse(flagSet.has(ColourTranslation.ATTR_BLIND)),
                () -> assertFalse(flagSet.has(ColourTranslation.ATTR_FULL)),
                () -> assertFalse(flagSet.has(ColourTranslation.ATTR_MAX)),
                () -> assertTrue(result)
        );
    }

    @Test
    void diff() {
        Flag<ColourTranslation> other = empty.copy();
        other.on(ColourTranslation.ATTR_FULL);
        other.on(ColourTranslation.ATTR_MAX);
        other.on(ColourTranslation.ATTR_HIGH);
        boolean result = flagSet.diff(other);

        assertAll(
                () -> assertTrue(result),
                () -> assertTrue(flagSet.has(ColourTranslation.ATTR_DARK)),
                () -> assertFalse(flagSet.has(ColourTranslation.ATTR_HIGH)),
                () -> assertTrue(flagSet.has(ColourTranslation.ATTR_BLIND)),
                () -> assertFalse(flagSet.has(ColourTranslation.ATTR_FULL)),
                () -> assertFalse(flagSet.has(ColourTranslation.ATTR_MAX))
        );
    }

    @Test
    void test1() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_MAX;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;

        assertAll(
                () -> assertTrue(flagSet.test(flag1, flag2, flag3)),
                () -> assertFalse(flagSet.test(flag4))
        );
    }

    @Test
    void test2() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_MAX;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;

        ArrayList<ColourTranslation> list1 = new ArrayList<>();
        list1.add(flag1);
        list1.add(flag2);
        list1.add(flag3);

        assertAll(
                () -> assertTrue(flagSet.test(list1)),
                () -> assertFalse(flagSet.test(flag4))
        );
    }

    @Test
    void testAll1() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_HIGH;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;

        assertAll(
                () -> assertTrue(flagSet.testAll(flag1, flag2)),
                () -> assertFalse(flagSet.testAll(flag1, flag3)),
                () -> assertFalse(flagSet.testAll(flag3, flag4))
        );
    }

    @Test
    void testAll2() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_HIGH;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;

        ArrayList<ColourTranslation> list1 = new ArrayList<>();
        ArrayList<ColourTranslation> list2 = new ArrayList<>();
        ArrayList<ColourTranslation> list3 = new ArrayList<>();

        list1.add(flag1);
        list1.add(flag2);
        list2.add(flag1);
        list2.add(flag3);
        list3.add(flag3);
        list3.add(flag4);

        assertAll(
                () -> assertTrue(flagSet.testAll(list1)),
                () -> assertFalse(flagSet.testAll(list2)),
                () -> assertFalse(flagSet.testAll(list3))
        );
    }

    @Test
    void clear() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;

        assertAll(
                () -> assertFalse(flagSet.clear(flag3, flag4)),
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.clear(flag1, flag3, flag4)),
                () -> assertFalse(flagSet.has(flag1))
        );
    }

    @Test
    void testClear() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;

        ArrayList<ColourTranslation> list2 = new ArrayList<>();
        ArrayList<ColourTranslation> list3 = new ArrayList<>();

        list2.add(flag1);
        list2.add(flag3);
        list3.add(flag3);
        list3.add(flag4);

        assertAll(
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertFalse(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertFalse(flagSet.clear(list3)),
                () -> assertTrue(flagSet.clear(list2)),
                () -> assertFalse(flagSet.has(flag1)),
                () -> assertFalse(flagSet.clear(list2))
        );
    }

    @Test
    void set() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_HIGH;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;

        assertAll(
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertFalse(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertFalse(flagSet.set(flag1, flag2)),
                () -> assertTrue(flagSet.set(flag2, flag3)),
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertTrue(flagSet.has(flag3))
        );
    }

    @Test
    void testSet() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_HIGH;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;

        ArrayList<ColourTranslation> list1 = new ArrayList<>();
        ArrayList<ColourTranslation> list2 = new ArrayList<>();

        list1.add(flag1);
        list1.add(flag2);
        list2.add(flag1);
        list2.add(flag3);

        assertAll(
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertFalse(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertFalse(flagSet.set(list1)),
                () -> assertTrue(flagSet.set(list2)),
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertTrue(flagSet.has(flag3))
        );
    }

    @Test
    void init() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_HIGH;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;
        ColourTranslation bFlag1 = ColourTranslation.ATTR_DARK;

        flagSet.init(flag1, flag2, flag3, flag4);

        assertAll(
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertTrue(flagSet.has(flag3)),
                () -> assertTrue(flagSet.has(flag4)),
                () -> assertFalse(flagSet.has(bFlag1))
        );
    }

    @Test
    void testInit() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_HIGH;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;
        ColourTranslation bFlag1 = ColourTranslation.ATTR_DARK;

        ArrayList<ColourTranslation> newList = new ArrayList<>();

        newList.add(flag1);
        newList.add(flag2);
        newList.add(flag3);

        assertAll(
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertFalse(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertTrue(flagSet.has(bFlag1))
        );

        flagSet.init(newList);

        assertAll(
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertTrue(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertFalse(flagSet.has(bFlag1))
        );
    }

    @Test
    void mask() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_HIGH;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;
        ColourTranslation bFlag1 = ColourTranslation.ATTR_DARK;

        Flag<ColourTranslation> masked = flagSet.copy();
        masked.mask(flag1, flag3);

        assertAll(
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertFalse(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertTrue(flagSet.has(bFlag1)),
                () -> assertFalse(flagSet.mask(flag1, flag2, bFlag1)),
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertFalse(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertTrue(flagSet.has(bFlag1)),
                () -> assertTrue(masked.has(flag1)),
                () -> assertFalse(masked.has(flag2)),
                () -> assertFalse(masked.has(flag3)),
                () -> assertFalse(masked.has(flag4)),
                () -> assertFalse(masked.has(bFlag1))
        );
    }

    @Test
    void testMask() {
        ColourTranslation flag1 = ColourTranslation.ATTR_BLIND;
        ColourTranslation flag2 = ColourTranslation.ATTR_HIGH;
        ColourTranslation flag3 = ColourTranslation.ATTR_FULL;
        ColourTranslation flag4 = ColourTranslation.ATTR_LIGHT;
        ColourTranslation bFlag1 = ColourTranslation.ATTR_DARK;

        ArrayList<ColourTranslation> noChangesMask = new ArrayList<>();
        noChangesMask.add(flag1);
        noChangesMask.add(flag2);
        noChangesMask.add(bFlag1);

        ArrayList<ColourTranslation> changesMask = new ArrayList<>();
        changesMask.add(flag1);
        changesMask.add(flag3);

        assertAll(
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertFalse(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertTrue(flagSet.has(bFlag1)),
                () -> assertFalse(flagSet.mask(noChangesMask)),
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertTrue(flagSet.has(flag2)),
                () -> assertFalse(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertTrue(flagSet.has(bFlag1)),
                () -> assertTrue(flagSet.mask(changesMask)),
                () -> assertTrue(flagSet.has(flag1)),
                () -> assertFalse(flagSet.has(flag2)),
                () -> assertFalse(flagSet.has(flag3)),
                () -> assertFalse(flagSet.has(flag4)),
                () -> assertFalse(flagSet.has(bFlag1))
        );
    }
}