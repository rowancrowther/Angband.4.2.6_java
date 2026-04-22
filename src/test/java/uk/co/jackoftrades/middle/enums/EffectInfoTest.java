package uk.co.jackoftrades.middle.enums;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EffectInfoTest {
    private static final EffectInfo none = EffectInfo.EFINFO_NONE;
    private static final EffectInfo dice = EffectInfo.EFINFO_DICE;
    private static final EffectInfo heal = EffectInfo.EFINFO_HEAL;
    private static final EffectInfo eIConst = EffectInfo.EFINFO_CONST;
    private static final EffectInfo food = EffectInfo.EFINFO_FOOD;
    private static final EffectInfo cure = EffectInfo.EFINFO_CURE;
    private static final EffectInfo timed = EffectInfo.EFINFO_TIMED;
    private static final EffectInfo stat = EffectInfo.EFINFO_STAT;
    private static final EffectInfo seen = EffectInfo.EFINFO_SEEN;
    private static final EffectInfo summ = EffectInfo.EFINFO_SUMM;
    private static final EffectInfo tele = EffectInfo.EFINFO_TELE;
    private static final EffectInfo quake = EffectInfo.EFINFO_QUAKE;
    private static final EffectInfo ball = EffectInfo.EFINFO_BALL;
    private static final EffectInfo spot = EffectInfo.EFINFO_SPOT;
    private static final EffectInfo breath = EffectInfo.EFINFO_BREATH;
    private static final EffectInfo EIShort = EffectInfo.EFINFO_SHORT;
    private static final EffectInfo lash = EffectInfo.EFINFO_LASH;
    private static final EffectInfo bolt = EffectInfo.EFINFO_BOLT;
    private static final EffectInfo boltd = EffectInfo.EFINFO_BOLTD;
    private static final EffectInfo touch = EffectInfo.EFINFO_TOUCH;

    static ArrayList<EffectInfo> allValues;

    @BeforeAll
    static void setValues() {
        allValues = new ArrayList<>();

        allValues.add(none);
        allValues.add(dice);
        allValues.add(heal);
        allValues.add(eIConst);
        allValues.add(food);
        allValues.add(cure);
        allValues.add(timed);
        allValues.add(stat);
        allValues.add(seen);
        allValues.add(summ);
        allValues.add(tele);
        allValues.add(quake);
        allValues.add(ball);
        allValues.add(spot);
        allValues.add(breath);
        allValues.add(EIShort);
        allValues.add(lash);
        allValues.add(bolt);
        allValues.add(boltd);
        allValues.add(touch);
    }

    @Test
    void values() {
        for (EffectInfo value : EffectInfo.values()) {
            if (!allValues.contains(value))
                fail("Effect Info " + value.toString() + " not found in manually created list.");
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(none, EffectInfo.valueOf("EFINFO_NONE")),
                () -> assertEquals(dice, EffectInfo.valueOf("EFINFO_DICE")),
                () -> assertEquals(heal, EffectInfo.valueOf("EFINFO_HEAL")),
                () -> assertEquals(eIConst, EffectInfo.valueOf("EFINFO_CONST")),
                () -> assertEquals(food, EffectInfo.valueOf("EFINFO_FOOD")),
                () -> assertEquals(cure, EffectInfo.valueOf("EFINFO_CURE")),
                () -> assertEquals(timed, EffectInfo.valueOf("EFINFO_TIMED")),
                () -> assertEquals(stat, EffectInfo.valueOf("EFINFO_STAT")),
                () -> assertEquals(seen, EffectInfo.valueOf("EFINFO_SEEN")),
                () -> assertEquals(summ, EffectInfo.valueOf("EFINFO_SUMM")),
                () -> assertEquals(tele, EffectInfo.valueOf("EFINFO_TELE")),
                () -> assertEquals(quake, EffectInfo.valueOf("EFINFO_QUAKE")),
                () -> assertEquals(ball, EffectInfo.valueOf("EFINFO_BALL")),
                () -> assertEquals(spot, EffectInfo.valueOf("EFINFO_SPOT")),
                () -> assertEquals(breath, EffectInfo.valueOf("EFINFO_BREATH")),
                () -> assertEquals(EIShort, EffectInfo.valueOf("EFINFO_SHORT")),
                () -> assertEquals(lash, EffectInfo.valueOf("EFINFO_LASH")),
                () -> assertEquals(bolt, EffectInfo.valueOf("EFINFO_BOLT")),
                () -> assertEquals(boltd, EffectInfo.valueOf("EFINFO_BOLTD")),
                () -> assertEquals(touch, EffectInfo.valueOf("EFINFO_TOUCH"))
        );
    }
}