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

package uk.co.jackoftradesltd.middle.monsters;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link MonsterSpellLevel} against {@code struct monster_spell_level} in {@code monster.h}.
 *
 * <p>The expected values come from the C struct and from how {@code mon-init.c} and
 * {@code mon-spell.c} use it, not from the Java: the struct has {@code power}, {@code lore_desc},
 * the three {@code uint8_t} colours, and the four message strings, all of which a zeroed
 * {@code mem_zalloc()} leaves as 0 or {@code NULL} (colour 0 is {@code COLOUR_DARK}), and
 * {@code do_mon_spell()} only rolls a save when {@code save_message} is non-null. The class is a
 * plain holder, so the cases are one value per field, the unset (zeroed) tier, and the power
 * boundaries the tier-selection loop in {@code mon-spell.c} depends on.
 *
 * <p>Class MonsterSpellLevelTest coded and commented in full on 261027.
 */
class MonsterSpellLevelTest {

    private static MonsterSpellLevel full() {
        return new MonsterSpellLevel(20, "lore", ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_WHITE,
                ColourEnum.COLOUR_DARK, "seen", "unseen", "miss", "save");
    }

    @Test
    void everyFieldRoundTripsThroughItsOwnGetter() {
        MonsterSpellLevel level = full();

        assertEquals(20, level.getPower());
        assertEquals("lore", level.getLoreDesc());
        assertEquals(ColourEnum.COLOUR_ORANGE, level.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_WHITE, level.getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttrImmune());
        assertEquals("seen", level.getMessage());
        assertEquals("unseen", level.getBlindMessage());
        assertEquals("miss", level.getMissMessage());
        assertEquals("save", level.getSaveMessage());
    }

    @Test
    void theFourMessagesAreNotSwapped() {
        MonsterSpellLevel level = new MonsterSpellLevel(0, null, ColourEnum.COLOUR_DARK,
                ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK, "a", "b", "c", "d");

        assertEquals("a", level.getMessage());
        assertEquals("b", level.getBlindMessage());
        assertEquals("c", level.getMissMessage());
        assertEquals("d", level.getSaveMessage());
    }

    @Test
    void theThreeColoursAreNotSwapped() {
        MonsterSpellLevel level = new MonsterSpellLevel(0, null, ColourEnum.COLOUR_ORANGE,
                ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_DARK, null, null, null, null);

        assertSame(ColourEnum.COLOUR_ORANGE, level.getLoreAttr());
        assertSame(ColourEnum.COLOUR_WHITE, level.getLoreAttrResist());
        assertSame(ColourEnum.COLOUR_DARK, level.getLoreAttrImmune());
    }

    @Test
    void aZeroedTierHasPowerZeroNoTextAndDarkColours() {
        // what parse_mon_spell_name() allocates in C: mem_zalloc(), every pointer NULL, every colour 0
        MonsterSpellLevel level = new MonsterSpellLevel(0, null, ColourEnum.COLOUR_DARK,
                ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK, null, null, null, null);

        assertEquals(0, level.getPower());
        assertNull(level.getLoreDesc());
        assertNull(level.getMessage());
        assertNull(level.getBlindMessage());
        assertNull(level.getMissMessage());
        assertNull(level.getSaveMessage());
        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttrImmune());
    }

    @Test
    void aNullSaveMessageStaysNullSoTheSaveIsNotAttempted() {
        MonsterSpellLevel level = new MonsterSpellLevel(10, "lore", ColourEnum.COLOUR_WHITE,
                ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_WHITE, "seen", "unseen", "miss", null);

        assertNull(level.getSaveMessage());
        assertEquals("seen", level.getMessage());
    }

    @Test
    void anEmptyStringIsKeptAsEmptyAndNotTurnedIntoNull() {
        // C's "" and NULL are different (an unset save_message means no save); the holder must not conflate them
        MonsterSpellLevel level = new MonsterSpellLevel(1, "", ColourEnum.COLOUR_WHITE,
                ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_WHITE, "", "", "", "");

        assertEquals("", level.getLoreDesc());
        assertEquals("", level.getMessage());
        assertEquals("", level.getBlindMessage());
        assertEquals("", level.getMissMessage());
        assertEquals("", level.getSaveMessage());
    }

    @Test
    void powerKeepsTheFullIntRangeWithNoClamp() {
        // C's power is a plain int, and the loop in mon-spell.c compares it with >= on spell_power
        assertEquals(0, new MonsterSpellLevel(0, null, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK,
                ColourEnum.COLOUR_DARK, null, null, null, null).getPower());
        assertEquals(Integer.MAX_VALUE, new MonsterSpellLevel(Integer.MAX_VALUE, null, ColourEnum.COLOUR_DARK,
                ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK, null, null, null, null).getPower());
        assertEquals(Integer.MIN_VALUE, new MonsterSpellLevel(Integer.MIN_VALUE, null, ColourEnum.COLOUR_DARK,
                ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK, null, null, null, null).getPower());
        assertEquals(-1, new MonsterSpellLevel(-1, null, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK,
                ColourEnum.COLOUR_DARK, null, null, null, null).getPower());
    }

    @Test
    void theHolderDoesNotCheckThatPowersRise() {
        // C's chain is built by appending whatever power-cutoff: says, so a lower power after a higher one loads
        MonsterSpellLevel high = new MonsterSpellLevel(50, null, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK,
                ColourEnum.COLOUR_DARK, null, null, null, null);
        MonsterSpellLevel low = new MonsterSpellLevel(5, null, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK,
                ColourEnum.COLOUR_DARK, null, null, null, null);

        assertEquals(50, high.getPower());
        assertEquals(5, low.getPower());
    }

    @Test
    void theTierSelectionRuleUsesGreaterThanOrEqualOnTheNextPower() {
        // mon-spell.c: while (level->next && race->spell_power >= level->next->power) level = level->next
        MonsterSpellLevel[] tiers = {
                new MonsterSpellLevel(0, "base", ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK,
                        null, null, null, null),
                new MonsterSpellLevel(10, "mid", ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK,
                        null, null, null, null),
                new MonsterSpellLevel(20, "top", ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK,
                        null, null, null, null)
        };

        assertEquals("base", pick(tiers, 0).getLoreDesc());
        assertEquals("base", pick(tiers, 9).getLoreDesc());
        assertEquals("mid", pick(tiers, 10).getLoreDesc());
        assertEquals("mid", pick(tiers, 19).getLoreDesc());
        assertEquals("top", pick(tiers, 20).getLoreDesc());
        assertEquals("top", pick(tiers, 1000).getLoreDesc());
        assertEquals("base", pick(tiers, -5).getLoreDesc());
    }

    /** The C walk, written out over an array, so the getters are shown to support it. */
    private static MonsterSpellLevel pick(MonsterSpellLevel[] tiers, int spellPower) {
        int at = 0;
        while (at + 1 < tiers.length && spellPower >= tiers[at + 1].getPower()) at++;
        return tiers[at];
    }

    @Test
    void twoTiersBuiltFromTheSameValuesAreIndependentObjects() {
        MonsterSpellLevel a = full();
        MonsterSpellLevel b = full();

        assertNotSame(a, b);
        assertEquals(a.getLoreDesc(), b.getLoreDesc());
        assertEquals(a.getPower(), b.getPower());
    }
}
