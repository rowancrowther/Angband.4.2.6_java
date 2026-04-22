package uk.co.jackoftrades.middle.enums;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EffectInfoEnumTest {
    private static final EffectInfoEnum none = EffectInfoEnum.EFINFO_NONE;
    private static final EffectInfoEnum dice = EffectInfoEnum.EFINFO_DICE;
    private static final EffectInfoEnum heal = EffectInfoEnum.EFINFO_HEAL;
    private static final EffectInfoEnum eIConst = EffectInfoEnum.EFINFO_CONST;
    private static final EffectInfoEnum food = EffectInfoEnum.EFINFO_FOOD;
    private static final EffectInfoEnum cure = EffectInfoEnum.EFINFO_CURE;
    private static final EffectInfoEnum timed = EffectInfoEnum.EFINFO_TIMED;
    private static final EffectInfoEnum stat = EffectInfoEnum.EFINFO_STAT;
    private static final EffectInfoEnum seen = EffectInfoEnum.EFINFO_SEEN;
    private static final EffectInfoEnum summ = EffectInfoEnum.EFINFO_SUMM;
    private static final EffectInfoEnum tele = EffectInfoEnum.EFINFO_TELE;
    private static final EffectInfoEnum quake = EffectInfoEnum.EFINFO_QUAKE;
    private static final EffectInfoEnum ball = EffectInfoEnum.EFINFO_BALL;
    private static final EffectInfoEnum spot = EffectInfoEnum.EFINFO_SPOT;
    private static final EffectInfoEnum breath = EffectInfoEnum.EFINFO_BREATH;
    private static final EffectInfoEnum EIShort = EffectInfoEnum.EFINFO_SHORT;
    private static final EffectInfoEnum lash = EffectInfoEnum.EFINFO_LASH;
    private static final EffectInfoEnum bolt = EffectInfoEnum.EFINFO_BOLT;
    private static final EffectInfoEnum boltd = EffectInfoEnum.EFINFO_BOLTD;
    private static final EffectInfoEnum touch = EffectInfoEnum.EFINFO_TOUCH;

    static ArrayList<EffectInfoEnum> allValues;

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
        for (EffectInfoEnum value : EffectInfoEnum.values()) {
            if (!allValues.contains(value))
                fail("Effect Info " + value.toString() + " not found in manually created list.");
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(none, EffectInfoEnum.valueOf("EFINFO_NONE")),
                () -> assertEquals(dice, EffectInfoEnum.valueOf("EFINFO_DICE")),
                () -> assertEquals(heal, EffectInfoEnum.valueOf("EFINFO_HEAL")),
                () -> assertEquals(eIConst, EffectInfoEnum.valueOf("EFINFO_CONST")),
                () -> assertEquals(food, EffectInfoEnum.valueOf("EFINFO_FOOD")),
                () -> assertEquals(cure, EffectInfoEnum.valueOf("EFINFO_CURE")),
                () -> assertEquals(timed, EffectInfoEnum.valueOf("EFINFO_TIMED")),
                () -> assertEquals(stat, EffectInfoEnum.valueOf("EFINFO_STAT")),
                () -> assertEquals(seen, EffectInfoEnum.valueOf("EFINFO_SEEN")),
                () -> assertEquals(summ, EffectInfoEnum.valueOf("EFINFO_SUMM")),
                () -> assertEquals(tele, EffectInfoEnum.valueOf("EFINFO_TELE")),
                () -> assertEquals(quake, EffectInfoEnum.valueOf("EFINFO_QUAKE")),
                () -> assertEquals(ball, EffectInfoEnum.valueOf("EFINFO_BALL")),
                () -> assertEquals(spot, EffectInfoEnum.valueOf("EFINFO_SPOT")),
                () -> assertEquals(breath, EffectInfoEnum.valueOf("EFINFO_BREATH")),
                () -> assertEquals(EIShort, EffectInfoEnum.valueOf("EFINFO_SHORT")),
                () -> assertEquals(lash, EffectInfoEnum.valueOf("EFINFO_LASH")),
                () -> assertEquals(bolt, EffectInfoEnum.valueOf("EFINFO_BOLT")),
                () -> assertEquals(boltd, EffectInfoEnum.valueOf("EFINFO_BOLTD")),
                () -> assertEquals(touch, EffectInfoEnum.valueOf("EFINFO_TOUCH"))
        );
    }
}