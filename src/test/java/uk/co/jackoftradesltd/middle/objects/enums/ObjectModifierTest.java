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
import uk.co.jackoftradesltd.middle.enums.Stats;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ObjectModifier}, the port of C's {@code OBJ_MOD_*} enum, which
 * {@code obj-properties.h} builds from {@code list-stats.h} followed by
 * {@code list-object-modifiers.h}.
 *
 * <p>The expected table is typed out from those two C headers, not read back from the enum, so a
 * modifier moved, dropped or renamed in the port fails here. Order is the thing the C header says
 * must never change, because savefiles store the modifiers by position.
 *
 * <p>Class ObjectModifierTest coded on 261007, commented in full on 261007.
 */
class ObjectModifierTest {

    /**
     * Every real modifier in C's order: the five {@code STAT()} entries of {@code list-stats.h},
     * then the eleven {@code OBJ_MOD()} entries of {@code list-object-modifiers.h}. Position in
     * this array is the modifier's {@code OBJ_MOD_*} value in C, so it is one less than its
     * {@code ordinal()} here, since {@code OM_NONE} takes ordinal 0.
     */
    private static final String[] C_ORDER = {
            "OM_STR", "OM_INT", "OM_WIS", "OM_DEX", "OM_CON",
            "OM_STEALTH", "OM_SEARCH", "OM_INFRA", "OM_TUNNEL", "OM_SPEED", "OM_BLOWS",
            "OM_SHOTS", "OM_MIGHT", "OM_LIGHT", "OM_DAM_RED", "OM_MOVES"
    };

    /**
     * C has five stats plus eleven {@code OBJ_MOD()} entries, so {@code OBJ_MOD_MAX} is 16.
     */
    @Test
    void theListIsCsSixteenModifiersPlusItsTwoSentinels() {
        assertEquals(16, C_ORDER.length, "5 stats + 11 OBJ_MOD() entries = OBJ_MOD_MAX in C");
        assertEquals(C_ORDER.length + 2, ObjectModifier.values().length,
                "the real modifiers, plus OM_NONE and OM_MAX");
    }

    /**
     * {@code OM_NONE} first and {@code OM_MAX} last, the sentinel positions every loop relies on.
     */
    @Test
    void theSentinelsBracketTheList() {
        ObjectModifier[] all = ObjectModifier.values();
        assertSame(ObjectModifier.OM_NONE, all[0]);
        assertSame(ObjectModifier.OM_MAX, all[all.length - 1]);
    }

    /**
     * Each real constant sits at C's index plus one, name for name.
     */
    @Test
    void everyModifierSitsAtCsIndexPlusOne() {
        ObjectModifier[] all = ObjectModifier.values();
        for (int cIndex = 0; cIndex < C_ORDER.length; cIndex++) {
            assertEquals(C_ORDER[cIndex], all[cIndex + 1].name(),
                    "OBJ_MOD index " + cIndex + " in C");
        }
    }

    /**
     * C's {@code OBJ_MOD_MIN_STAT} is {@code OBJ_MOD_STR}, and {@code obj-randart.c} reaches a
     * stat's modifier as {@code OBJ_MOD_MIN_STAT + stat}. So the five stat modifiers must follow
     * {@code OM_STR} in stat order, which is what {@link Stats} lists.
     */
    @Test
    void theStatModifiersFollowOmStrInStatOrder() {
        Stats[] stats = {Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX, Stats.STAT_CON};
        ObjectModifier[] all = ObjectModifier.values();
        int minStat = ObjectModifier.OM_STR.ordinal();
        for (int i = 0; i < stats.length; i++) {
            assertEquals(stats[i].name().replace("STAT_", ""),
                    all[minStat + stats[i].getValue()].name().replace("OM_", ""),
                    "OBJ_MOD_MIN_STAT + stat index for " + stats[i]);
        }
    }

    /**
     * The first non-stat modifier follows the last stat, as C's two includes are concatenated.
     */
    @Test
    void theObjModEntriesStartRightAfterTheStats() {
        assertEquals(ObjectModifier.OM_CON.ordinal() + 1, ObjectModifier.OM_STEALTH.ordinal());
        assertEquals(ObjectModifier.OM_MOVES.ordinal() + 1, ObjectModifier.OM_MAX.ordinal());
    }

    /**
     * {@code valueOf} resolves every name, the lookup the data parsers lean on.
     */
    @Test
    void valueOfResolvesEveryRealName() {
        for (String name : C_ORDER) {
            assertEquals(name, ObjectModifier.valueOf(name).name());
        }
    }

    /**
     * C's {@code obj_mods} table holds the bare names {@code "STR"} to {@code "MOVES"}, and
     * {@code code_index_in_array} finds each at its {@code OBJ_MOD_*} index. The bare name must
     * find the same constant here, one ordinal above that index.
     */
    @Test
    void lookupFindsEveryBareNameAtCsIndexPlusOne() {
        for (int cIndex = 0; cIndex < C_ORDER.length; cIndex++) {
            String bare = C_ORDER[cIndex].substring("OM_".length());
            ObjectModifier found = ObjectModifier.lookup(bare);
            assertEquals(C_ORDER[cIndex], found.name(), "bare name " + bare);
            assertEquals(cIndex + 1, found.ordinal(), "bare name " + bare);
        }
    }

    /**
     * Accepted deviation: the {@code OM_} prefix is optional. C finds nothing for
     * {@code "OM_STR"}; here it finds the same constant as {@code "STR"}.
     */
    @Test
    void lookupAcceptsTheOptionalPrefix() {
        for (String name : C_ORDER) {
            assertSame(ObjectModifier.valueOf(name), ObjectModifier.lookup(name), name);
        }
    }

    /**
     * Accepted deviation: the sentinels can be found. C's table has no {@code NONE} or
     * {@code MAX}, so both find nothing there.
     */
    @Test
    void lookupFindsTheSentinels() {
        assertSame(ObjectModifier.OM_NONE, ObjectModifier.lookup("NONE"));
        assertSame(ObjectModifier.OM_NONE, ObjectModifier.lookup("OM_NONE"));
        assertSame(ObjectModifier.OM_MAX, ObjectModifier.lookup("MAX"));
        assertSame(ObjectModifier.OM_MAX, ObjectModifier.lookup("OM_MAX"));
    }

    /**
     * C's {@code streq} is case-sensitive, so a name in the wrong case finds nothing; so does a
     * name that is not in the table, an empty name, a doubled prefix and a name with stray blanks.
     */
    @Test
    void lookupRefusesWhatCRefuses() {
        assertNull(ObjectModifier.lookup("str"));
        assertNull(ObjectModifier.lookup("Str"));
        assertNull(ObjectModifier.lookup("om_str"));
        assertNull(ObjectModifier.lookup("OM_str"));
        assertNull(ObjectModifier.lookup("FOO"));
        assertNull(ObjectModifier.lookup(""));
        assertNull(ObjectModifier.lookup("OM_"));
        assertNull(ObjectModifier.lookup("OM_OM_STR"));
        assertNull(ObjectModifier.lookup(" STR"));
        assertNull(ObjectModifier.lookup("STR "));
        assertNull(ObjectModifier.lookup("STEALTH_"));
    }

    /**
     * C has no {@code null} case, since the parser always hands over a string; the port answers
     * {@code null} rather than throwing.
     */
    @Test
    void lookupOfNullIsNull() {
        assertNull(ObjectModifier.lookup(null));
    }
}
