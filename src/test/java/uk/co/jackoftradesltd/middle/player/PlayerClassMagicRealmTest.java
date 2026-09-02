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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.magic.MagicSpell;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerClass#magicRealm()}, the port of C's {@code class_magic_realms}, and the
 * accessors {@code calcBonuses} reads a class through.
 *
 * <p><b>The realms decide a caster's mana</b>, so how many there are matters more than which they
 * are: {@code averageSpellStat} averages the governing stat's index across the set, and then divides
 * by its size ({@code player-calcs.c:1247-1259}). A duplicate that survived would drag the average
 * toward one realm's stat and make a class's mana depend on how many books happened to name the same
 * school. An empty set would divide by zero.
 *
 * <p>A realm is a property of a book, and a class's books commonly repeat one — so the deduplication
 * is the method's entire job, and it is done by realm <em>name</em>, matching C's
 * {@code lookup_realm}. Two distinct objects with one name are one realm.
 *
 * <p>Class PlayerClassMagicRealmTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class PlayerClassMagicRealmTest {

    /**
     * A realm with the given name and governing stat.
     *
     * @param name the realm's name, which is what deduplication compares
     * @param stat the governing stat
     * @return the realm
     */
    private static MagicRealm realm(String name, Stats stat) {
        return new MagicRealm(name, stat, "cast", "spell", TValue.TV_MAGIC_BOOK);
    }

    /**
     * A one-spell book in the given realm.
     *
     * @param name  the book's name
     * @param realm the realm its spells belong to
     * @return the book
     */
    private static MagicBook book(String name, MagicRealm realm) {
        return new MagicBook(TValue.TV_MAGIC_BOOK, name, false, 1, realm, null, 0, 0, 0, 0,
                List.of(new MagicSpell("a spell", 1, 25, 1, 0, List.of(), "")));
    }

    /**
     * A class whose only interesting property is its magic.
     *
     * @param magic the class's spellcasting profile
     * @return the class
     */
    private static PlayerClass playerClass(ClassMagic magic) {
        return new PlayerClass("Tester", List.of(), Map.of(), Map.of(), Map.of(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 5, 30, 5,
                List.of(), magic);
    }

    /**
     * Two books naming one realm give one realm. This is the shipped case for most casters and the
     * reason the method cannot simply map books to realms.
     */
    @Test
    @DisplayName("books sharing a realm collapse to one")
    void duplicatesCollapse() {
        MagicRealm arcane = realm("arcane", Stats.STAT_INT);
        PlayerClass c = playerClass(new ClassMagic(1, 300, 2,
                List.of(book("first", arcane), book("second", arcane))));

        Set<MagicRealm> realms = c.magicRealm();

        assertAll(
                () -> assertEquals(1, realms.size()),
                () -> assertTrue(realms.contains(arcane)));
    }

    /**
     * Deduplication is by name, not by identity — two realm objects that share a name are the same
     * realm, which is how C's {@code lookup_realm} behaves.
     */
    @Test
    @DisplayName("realms are the same when their names match, whatever the objects")
    void deduplicatedByName() {
        PlayerClass c = playerClass(new ClassMagic(1, 300, 2,
                List.of(book("first", realm("arcane", Stats.STAT_INT)),
                        book("second", realm("arcane", Stats.STAT_INT)))));

        assertEquals(1, c.magicRealm().size());
    }

    /**
     * A class spanning two schools keeps both, because its mana is meant to be held to the mean of
     * the two governing stats rather than to either alone.
     */
    @Test
    @DisplayName("genuinely different realms are both kept")
    void distinctRealmsKept() {
        PlayerClass c = playerClass(new ClassMagic(1, 300, 2,
                List.of(book("arcane book", realm("arcane", Stats.STAT_INT)),
                        book("divine book", realm("divine", Stats.STAT_WIS)))));

        assertEquals(2, c.magicRealm().size());
    }

    /**
     * A non-caster has no realms at all. The caller must not divide by the size — {@code calcMana}
     * establishes that the class is literate before it asks, and this is the state that makes that
     * ordering necessary.
     */
    @Test
    @DisplayName("a class with no spells has no realms")
    void nonCasterHasNoRealms() {
        assertTrue(playerClass(ClassMagic.NONE).magicRealm().isEmpty());
    }

    /**
     * The figures {@code calcBonuses} and {@code calcBlows} read, each through its own accessor.
     * Distinct values throughout, because several are small integers whose names are easy to cross.
     */
    @Test
    @DisplayName("the combat and skill accessors read their own fields")
    void accessorsReadTheirOwnFields() {
        PlayerClass c = new PlayerClass("Tester", List.of(),
                Map.of(Stats.STAT_STR, 3),
                Map.of(PlayerSkill.SKILL_STEALTH, 7),
                Map.of(PlayerSkill.SKILL_STEALTH, 11),
                0, 0, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                6, 30, 5, List.of(), ClassMagic.NONE);

        assertAll(
                () -> assertEquals(6, c.getMaxAttacks()),
                () -> assertEquals(30, c.getMinWeight()),
                () -> assertEquals(5, c.getAttMultiply()),
                () -> assertEquals(3, c.getStatsAdj(Stats.STAT_STR)),
                () -> assertEquals(7, c.getSkill(PlayerSkill.SKILL_STEALTH)),
                () -> assertEquals(11, c.getXSkill(PlayerSkill.SKILL_STEALTH)),
                () -> assertEquals(ClassMagic.NONE, c.getMagic()));
    }
}
