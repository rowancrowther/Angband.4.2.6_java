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

package uk.co.jackoftradesltd.middle.monsters.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link MonsterSpellTypeEnum} against {@code enum mon_spell_type} and the {@code RST_DAMAGE}
 * macro in C's {@code mon-spell.h}, and the damage test C makes of a whole spell
 * ({@code mon_spell_has_damage()} in {@code mon-spell.c}) against the per-type
 * {@code isDamage()} here. The expected values are copied from the C header, not read back from
 * the Java enum. The per-type spell counts are in {@code MonsterSpellTableTest}.
 *
 * <p>Class MonsterSpellTypeEnumTest coded on 261014, commented in full on 261014.
 */
class MonsterSpellTypeEnumTest {

    /**
     * The constants of {@code enum mon_spell_type}, in header order, each with its bit value:
     * {@code RST_NONE} {@code 0x0000}, then one bit per type from {@code 0x0001} to
     * {@code 0x1000}.
     */
    private static final Object[][] C_ENUM = {
            {"RST_NONE", 0x0000},
            {"RST_BOLT", 0x0001},
            {"RST_BALL", 0x0002},
            {"RST_BREATH", 0x0004},
            {"RST_DIRECT", 0x0008},
            {"RST_ANNOY", 0x0010},
            {"RST_HASTE", 0x0020},
            {"RST_HEAL", 0x0040},
            {"RST_HEAL_OTHER", 0x0080},
            {"RST_TACTIC", 0x0100},
            {"RST_ESCAPE", 0x0200},
            {"RST_SUMMON", 0x0400},
            {"RST_INNATE", 0x0800},
            {"RST_ARCHERY", 0x1000},
    };

    /** C's {@code RST_DAMAGE}: {@code RST_BOLT | RST_BALL | RST_BREATH | RST_DIRECT}. */
    private static final int RST_DAMAGE = 0x0001 | 0x0002 | 0x0004 | 0x0008;

    /**
     * The rows of {@code list-mon-spells.h} whose type column names at least one of
     * {@code RST_BOLT}, {@code RST_BALL}, {@code RST_BREATH} or {@code RST_DIRECT}, so those for
     * which {@code mon_spell_has_damage()} is true. Fifty-four rows, taken from the header with
     * {@code grep}.
     */
    private static final List<String> C_DAMAGING_SPELLS = List.of(
            "WHIP", "SPIT", "SHOT", "ARROW", "BOLT",
            "BR_ACID", "BR_ELEC", "BR_FIRE", "BR_COLD", "BR_POIS", "BR_NETH", "BR_LIGHT",
            "BR_DARK", "BR_SOUN", "BR_CHAO", "BR_DISE", "BR_NEXU", "BR_TIME", "BR_INER",
            "BR_GRAV", "BR_SHAR", "BR_PLAS", "BR_WALL", "BR_MANA", "BOULDER",
            "BA_ACID", "BA_ELEC", "BA_FIRE", "BA_COLD", "BA_POIS", "BA_SHAR", "BA_NETH",
            "BA_WATE", "BA_MANA", "BA_HOLY", "BA_DARK", "BA_LIGHT", "STORM",
            "MIND_BLAST", "BRAIN_SMASH", "WOUND",
            "BO_ACID", "BO_ELEC", "BO_FIRE", "BO_COLD", "BO_POIS", "BO_NETH", "BO_WATE",
            "BO_MANA", "BO_PLAS", "BO_ICE", "MISSILE", "BE_ELEC", "BE_NETH");

    /**
     * The fourteen constants are C's, in C's order: same names at the same positions, with no
     * extras, so the ordinal is one more than the position of the C bit.
     *
     * <p>Function constantsAreCsInCsOrder coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("The constants are enum mon_spell_type's, in order, with none extra")
    void constantsAreCsInCsOrder() {
        MonsterSpellTypeEnum[] actual = MonsterSpellTypeEnum.values();

        assertEquals(14, C_ENUM.length);
        assertEquals(C_ENUM.length, actual.length);
        for (int i = 0; i < C_ENUM.length; i++) {
            assertEquals(C_ENUM[i][0], actual[i].name(), "name at index " + i);
            assertEquals(i, MonsterSpellTypeEnum.valueOf((String) C_ENUM[i][0]).ordinal(),
                    "ordinal of " + C_ENUM[i][0]);
        }
    }

    /**
     * C's values are {@code RST_NONE = 0} then successive single bits. The ordinal of a type
     * other than {@code RST_NONE} therefore gives the C value as {@code 1 << (ordinal - 1)}, which
     * checks that the order is not just the names but the bits C gives them.
     *
     * <p>Function ordinalGivesTheCBitValue coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("1 << (ordinal - 1) is the C bit value, and RST_NONE is 0")
    void ordinalGivesTheCBitValue() {
        for (Object[] row : C_ENUM) {
            MonsterSpellTypeEnum type = MonsterSpellTypeEnum.valueOf((String) row[0]);
            int cValue = (Integer) row[1];
            int fromOrdinal = type == MonsterSpellTypeEnum.RST_NONE ? 0 : 1 << (type.ordinal() - 1);

            assertEquals(cValue, fromOrdinal, "bit value of " + type);
        }
    }

    /**
     * Per type: {@code isDamage()} is true when C's value has a bit in common with
     * {@code RST_DAMAGE}, which is for {@code RST_BOLT}, {@code RST_BALL}, {@code RST_BREATH} and
     * {@code RST_DIRECT} and for nothing else, {@code RST_NONE} included.
     *
     * <p>Function isDamageIsTheRstDamageMask coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("isDamage is true when the type's C bit is in RST_DAMAGE")
    void isDamageIsTheRstDamageMask() {
        assertEquals(0x000F, RST_DAMAGE);
        for (Object[] row : C_ENUM) {
            MonsterSpellTypeEnum type = MonsterSpellTypeEnum.valueOf((String) row[0]);
            boolean expected = ((Integer) row[1] & RST_DAMAGE) != 0;

            assertEquals(expected, type.isDamage(), "isDamage of " + type);
        }
    }

    /**
     * The boundaries of the mask: the four damaging types, the ones either side of it in the C
     * enum ({@code RST_NONE} below, {@code RST_ANNOY} above) and the last constant.
     *
     * <p>Function theMaskBoundariesAreNamed coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("The first and last damaging types and their neighbours")
    void theMaskBoundariesAreNamed() {
        assertFalse(MonsterSpellTypeEnum.RST_NONE.isDamage());
        assertTrue(MonsterSpellTypeEnum.RST_BOLT.isDamage());
        assertTrue(MonsterSpellTypeEnum.RST_DIRECT.isDamage());
        assertFalse(MonsterSpellTypeEnum.RST_ANNOY.isDamage());
        assertFalse(MonsterSpellTypeEnum.RST_ARCHERY.isDamage());
    }

    /**
     * Per spell: C's {@code mon_spell_has_damage()} is true for a row if any of its types is in
     * {@code RST_DAMAGE}; here that is {@code anyMatch(isDamage)} over the spell's types. The set
     * of spells for which it holds must be the fifty-four rows found in the header, no more and no
     * fewer. This includes {@code MIND_BLAST} and {@code BRAIN_SMASH}, which are also
     * {@code RST_ANNOY}, and leaves out {@code SLOW}, {@code BLINK} and every summon.
     *
     * <p>Function damagingSpellsAreTheCHeadersFiftyFour coded on 261014, commented in full on
     * 261014.
     */
    @Test
    @DisplayName("Exactly the 54 spells C's mon_spell_has_damage() accepts have a damaging type")
    void damagingSpellsAreTheCHeadersFiftyFour() {
        Set<String> expected = C_DAMAGING_SPELLS.stream()
                .map(name -> "RSF_" + name)
                .collect(Collectors.toSet());

        Set<String> actual = Arrays.stream(MonsterSpell.values())
                .filter(spell -> spell.getTypes().toList().stream()
                        .anyMatch(MonsterSpellTypeEnum::isDamage))
                .map(MonsterSpell::name)
                .collect(Collectors.toSet());

        assertEquals(54, C_DAMAGING_SPELLS.size());
        assertEquals(54, expected.size(), "the C list has no duplicate");
        assertEquals(expected, actual);
    }

    /**
     * A spell that is damaging only through one of two types: {@code RST_DIRECT | RST_ANNOY}
     * spells are damaging although {@code RST_ANNOY} is not, and the {@code RST_ARCHERY} spells
     * are damaging through {@code RST_BOLT} although {@code RST_ARCHERY} and {@code RST_INNATE}
     * are not. A spell with no damaging type ({@code SLOW}: {@code RST_ANNOY | RST_HASTE}) is not.
     *
     * <p>Function mixedTypeSpellsFollowTheirDamagingType coded on 261014, commented in full on
     * 261014.
     */
    @Test
    @DisplayName("A spell is damaging through any one of its types")
    void mixedTypeSpellsFollowTheirDamagingType() {
        assertEquals(EnumSet.of(MonsterSpellTypeEnum.RST_DIRECT, MonsterSpellTypeEnum.RST_ANNOY),
                EnumSet.copyOf(MonsterSpell.RSF_MIND_BLAST.getTypes().toList()));
        assertTrue(MonsterSpell.RSF_MIND_BLAST.getTypes().toList().stream()
                .anyMatch(MonsterSpellTypeEnum::isDamage));
        assertTrue(MonsterSpell.RSF_ARROW.getTypes().toList().stream()
                .anyMatch(MonsterSpellTypeEnum::isDamage));
        assertFalse(MonsterSpell.RSF_SLOW.getTypes().toList().stream()
                .anyMatch(MonsterSpellTypeEnum::isDamage));
    }
}
