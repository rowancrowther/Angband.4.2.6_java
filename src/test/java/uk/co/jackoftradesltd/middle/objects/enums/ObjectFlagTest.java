/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.player.enums.SustainStat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link ObjectFlag}, the port of C's {@code OF_*} list in
 * {@code list-object-flags.h}, and for {@link ObjectFlag#getSustainStatFlag}, the object-flag half
 * of {@code sustain_flag()} in {@code obj-properties.c}.
 *
 * <p>The expected table is typed out from the C header, not read back from the enum, so a flag
 * moved, dropped or relabelled in the port fails here. Order is the thing C's header says must
 * never change, because savefiles store the flags by position.
 *
 * <p>Class ObjectFlagTest coded on 261005, commented in full on 261005.
 */
class ObjectFlagTest {

    /**
     * Name and label of every {@code OF()} entry, in the order {@code list-object-flags.h} lists
     * them. Position in this array plus one is the flag's index in C, since {@code OF_NONE} takes 0.
     */
    private static final String[][] C_TABLE = {
            {"OF_SUST_STR", " sStr"},
            {"OF_SUST_INT", " sInt"},
            {"OF_SUST_WIS", " sWis"},
            {"OF_SUST_DEX", " sDex"},
            {"OF_SUST_CON", " sCon"},
            {"OF_PROT_FEAR", "pFear"},
            {"OF_PROT_BLIND", "pBlnd"},
            {"OF_PROT_CONF", "pConf"},
            {"OF_PROT_STUN", "pStun"},
            {"OF_SLOW_DIGEST", "S.Dig"},
            {"OF_FEATHER", "Feath"},
            {"OF_REGEN", "Regen"},
            {"OF_TELEPATHY", "  ESP"},
            {"OF_SEE_INVIS", "S.Inv"},
            {"OF_FREE_ACT", "FrAct"},
            {"OF_HOLD_LIFE", "HLife"},
            {"OF_IMPACT", "Impct"},
            {"OF_BLESSED", " Bless"},
            {"OF_BURNS_OUT", "BuOut"},
            {"OF_TAKES_FUEL", "TFuel"},
            {"OF_NO_FUEL", "NFuel"},
            {"OF_IMPAIR_HP", "ImpHP"},
            {"OF_IMPAIR_MANA", "ImpSP"},
            {"OF_AFRAID", " Fear"},
            {"OF_NO_TELEPORT", "NoTel"},
            {"OF_AGGRAVATE", "Aggrv"},
            {"OF_DRAIN_EXP", "DrExp"},
            {"OF_STICKY", "Stick"},
            {"OF_FRAGILE", "Fragl"},
            {"OF_LIGHT_2", "Lght2"},
            {"OF_LIGHT_3", "Lght3"},
            {"OF_DIG_1", " Dig1"},
            {"OF_DIG_2", " Dig2"},
            {"OF_DIG_3", " Dig3"},
            {"OF_EXPLODE", "Expld"},
            {"OF_TRAP_IMMUNE", "TrpIm"},
            {"OF_THROWING", "Throw"},
            {"OF_MULTIPLY_WEIGHT", "MulWg"},
    };

    @Test
    void sentinelsBracketTheRealFlags() {
        assertEquals(0, ObjectFlag.OF_NONE.ordinal());
        assertEquals("", ObjectFlag.OF_NONE.getFlag());
        assertEquals(C_TABLE.length + 1, ObjectFlag.OF_MAX.ordinal());
        assertEquals("", ObjectFlag.OF_MAX.getFlag());
        assertEquals(C_TABLE.length + 2, ObjectFlag.values().length);
    }

    @Test
    void everyFlagSitsAtItsCIndexWithItsCLabel() {
        for (int i = 0; i < C_TABLE.length; i++) {
            ObjectFlag flag = ObjectFlag.values()[i + 1];
            assertEquals(C_TABLE[i][0], flag.name(), "name at C index " + (i + 1));
            assertEquals(C_TABLE[i][1], flag.getFlag(), "label of " + C_TABLE[i][0]);
        }
    }

    @Test
    void sustainsAreTheFirstFiveRealFlagsInStatOrder() {
        assertEquals(1, ObjectFlag.OF_SUST_STR.ordinal());
        assertEquals(2, ObjectFlag.OF_SUST_INT.ordinal());
        assertEquals(3, ObjectFlag.OF_SUST_WIS.ordinal());
        assertEquals(4, ObjectFlag.OF_SUST_DEX.ordinal());
        assertEquals(5, ObjectFlag.OF_SUST_CON.ordinal());
    }

    @Test
    void sustainStatFlagMapsEachStatToItsSustain() {
        assertSame(ObjectFlag.OF_SUST_STR, ObjectFlag.getSustainStatFlag(SustainStat.SUS_STAT_STR));
        assertSame(ObjectFlag.OF_SUST_INT, ObjectFlag.getSustainStatFlag(SustainStat.SUS_STAT_INT));
        assertSame(ObjectFlag.OF_SUST_WIS, ObjectFlag.getSustainStatFlag(SustainStat.SUS_STAT_WIS));
        assertSame(ObjectFlag.OF_SUST_DEX, ObjectFlag.getSustainStatFlag(SustainStat.SUS_STAT_DEX));
        assertSame(ObjectFlag.OF_SUST_CON, ObjectFlag.getSustainStatFlag(SustainStat.SUS_STAT_CON));
    }

    /**
     * C's {@code sustain_flag()} is stat + 1, so for each real stat the flag's index is the stat's
     * index plus one. Checking that arithmetic directly, rather than the switch, is what catches a
     * pair swapped in both the table and the switch.
     */
    @Test
    void sustainStatFlagIndexIsStatIndexPlusOne() {
        SustainStat[] stats = {SustainStat.SUS_STAT_STR, SustainStat.SUS_STAT_INT,
                SustainStat.SUS_STAT_WIS, SustainStat.SUS_STAT_DEX, SustainStat.SUS_STAT_CON};
        for (int stat = 0; stat < stats.length; stat++) {
            assertEquals(stat + 1, ObjectFlag.getSustainStatFlag(stats[stat]).ordinal());
        }
    }

    /**
     * C answers -1 outside 0 to 4; the port answers null for the two sentinels.
     */
    @Test
    void sustainStatFlagIsNullForTheSentinels() {
        assertNull(ObjectFlag.getSustainStatFlag(SustainStat.SUS_STAT_NONE));
        assertNull(ObjectFlag.getSustainStatFlag(SustainStat.SUS_STAT_MAX));
    }
}
