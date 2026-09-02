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

import org.junit.jupiter.api.BeforeEach;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerClass#copy()}, the method {@code PlayerBirth.playerGenerate} hands a player its
 * own class definition through ({@code PlayerBirth.java:427}).
 *
 * <p><b>There is no C function to compare against, and that is the point.</b> C's
 * {@code player_generate} writes the shared pointer — {@code p->class = c}
 * ({@code player-birth.c:989}) — because a class record is read-only data held once by the registry.
 * The port copies instead, so the expected behaviour is not a number taken from C but the property
 * that makes the copy worth having: the two objects must agree on every field, and the copy must
 * share no structure through which a player could write back into the registry's template.
 *
 * <p>Two of the cases therefore mutate the <em>source</em> after copying and assert the copy did not
 * move. That is the failure a shallow copy actually produces: it is invisible while one character
 * exists, and shows up as one character's class data following another's.
 *
 * <p>{@link ClassMagic} is deliberately shared by reference, so it is asserted with
 * {@code assertSame} rather than treated as an omission — C shares the same spell data across every
 * caster of a class, and what a particular character knows lives on the player.
 *
 * <p>{@code PlayerClass} publishes no accessor for its name, titles or starting items, so those
 * three are read by reflection, as {@code PlayerBodyCopyTest} reads slots.
 *
 * <p>Class PlayerClassCopyTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
class PlayerClassCopyTest {

    /**
     * Titles of the source class; mutable, so a shared list can be detected.
     */
    private List<String> titles;
    /**
     * Stat adjustments of the source class; mutable for the same reason.
     */
    private Map<Stats, Integer> stats;
    /**
     * Flat skill values of the source class.
     */
    private Map<PlayerSkill, Integer> classSkills;
    /**
     * Per-level skill values of the source class.
     */
    private Map<PlayerSkill, Integer> extraSkills;
    /**
     * Starting equipment of the source class.
     */
    private List<StartItem> startItems;
    /**
     * The source's object flags, held here so the test can write to them after copying.
     */
    private Flag<ObjectFlag> oFlags;
    /**
     * The source's player flags, held for the same reason.
     */
    private Flag<PlayerFlag> pFlags;
    /**
     * The source's spellcasting definition, held to assert the copy shares it.
     */
    private ClassMagic magic;
    /**
     * The class being copied.
     */
    private PlayerClass source;

    /**
     * Reads a private field of a {@code PlayerClass}, for the three the class does not publish.
     *
     * @param playerClass the class to read
     * @param name        the field's name
     * @return the field's value
     * @throws ReflectiveOperationException if the field is absent, which a rename would cause
     */
    private static Object field(PlayerClass playerClass, String name)
            throws ReflectiveOperationException {
        Field f = PlayerClass.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(playerClass);
    }

    /**
     * A source class with a distinct value in every field, so that a mis-ordered constructor
     * argument shows up rather than cancelling out.
     */
    @BeforeEach
    void setUp() {
        titles = new ArrayList<>(List.of("Novice", "Adept"));
        stats = new EnumMap<>(Stats.class);
        stats.put(Stats.STAT_STR, 3);
        stats.put(Stats.STAT_INT, -2);
        classSkills = new EnumMap<>(PlayerSkill.class);
        classSkills.put(PlayerSkill.SKILL_STEALTH, 7);
        extraSkills = new EnumMap<>(PlayerSkill.class);
        extraSkills.put(PlayerSkill.SKILL_STEALTH, 11);
        startItems = new ArrayList<>(List.of(
                new StartItem(TValue.TV_SWORD, "Dagger", 1, 1, List.of())));

        oFlags = new Flag<>(ObjectFlag.class);
        oFlags.on(ObjectFlag.OF_FREE_ACT);
        pFlags = new Flag<>(PlayerFlag.class);
        pFlags.on(PlayerFlag.PF_BRAVERY_30);

        MagicRealm arcane = new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell",
                TValue.TV_MAGIC_BOOK);
        magic = new ClassMagic(1, 300, 1, List.of(
                new MagicBook(TValue.TV_MAGIC_BOOK, "Magic for Beginners", false, 1, arcane, null,
                        0, 0, 0, 0,
                        List.of(new MagicSpell("Magic Missile", 1, 1, 25, 0, List.of(), "")))));

        source = new PlayerClass("Tester", titles, stats, classSkills, extraSkills,
                9, 13, oFlags, pFlags, 6, 30, 5, startItems, magic);
    }

    /**
     * Every field arrives in the copy. Fourteen constructor arguments of which several are small
     * integers, so this is really a check that none of them has been crossed with its neighbour.
     *
     * @throws ReflectiveOperationException if a field has been renamed
     */
    @Test
    @DisplayName("the copy carries every field of the original")
    void everyFieldCarriedAcross() throws ReflectiveOperationException {
        PlayerClass copy = source.copy();

        assertAll(
                () -> assertEquals("Tester", field(copy, "name")),
                () -> assertEquals(List.of("Novice", "Adept"), field(copy, "titles")),
                () -> assertEquals(3, copy.getStatsAdj(Stats.STAT_STR)),
                () -> assertEquals(-2, copy.getStatsAdj(Stats.STAT_INT)),
                () -> assertEquals(7, copy.getSkill(PlayerSkill.SKILL_STEALTH)),
                () -> assertEquals(11, copy.getXSkill(PlayerSkill.SKILL_STEALTH)),
                () -> assertEquals(9, field(copy, "hpAdj")),
                () -> assertEquals(13, field(copy, "expAdj")),
                () -> assertTrue(copy.getoFlags().has(ObjectFlag.OF_FREE_ACT)),
                () -> assertTrue(copy.getpFlags().has(PlayerFlag.PF_BRAVERY_30)),
                () -> assertEquals(6, copy.getMaxAttacks()),
                () -> assertEquals(30, copy.getMinWeight()),
                () -> assertEquals(5, copy.getAttMultiply()),
                () -> assertEquals(startItems, field(copy, "startItems")),
                () -> assertSame(magic, copy.getMagic()));
    }

    /**
     * None of the five collections is the source's own object. The containers are what a player
     * could grow or clear, so they are the ones that must not be shared.
     *
     * @throws ReflectiveOperationException if a field has been renamed
     */
    @Test
    @DisplayName("no collection is shared with the original")
    void collectionsAreFreshObjects() throws ReflectiveOperationException {
        PlayerClass copy = source.copy();

        assertAll(
                () -> assertNotSame(titles, field(copy, "titles")),
                () -> assertNotSame(stats, field(copy, "stats")),
                () -> assertNotSame(classSkills, field(copy, "classSkills")),
                () -> assertNotSame(extraSkills, field(copy, "extraSkills")),
                () -> assertNotSame(startItems, field(copy, "startItems")));
    }

    /**
     * Writing to the source's collections after the copy leaves the copy where it was — the
     * registry-template case, stated as behaviour rather than as object identity.
     *
     * @throws ReflectiveOperationException if a field has been renamed
     */
    @Test
    @DisplayName("later changes to the original do not reach the copy")
    void copyIsUnaffectedByLaterSourceChanges() throws ReflectiveOperationException {
        PlayerClass copy = source.copy();

        titles.add("Master");
        stats.put(Stats.STAT_STR, 99);
        classSkills.put(PlayerSkill.SKILL_STEALTH, 99);
        extraSkills.put(PlayerSkill.SKILL_STEALTH, 99);
        startItems.clear();

        assertAll(
                () -> assertEquals(List.of("Novice", "Adept"), field(copy, "titles")),
                () -> assertEquals(3, copy.getStatsAdj(Stats.STAT_STR)),
                () -> assertEquals(7, copy.getSkill(PlayerSkill.SKILL_STEALTH)),
                () -> assertEquals(11, copy.getXSkill(PlayerSkill.SKILL_STEALTH)),
                () -> assertEquals(1, ((List<?>) field(copy, "startItems")).size()));
    }

    /**
     * The flag sets are filled by {@code copyFrom}, not aliased: turning a flag on in the source
     * afterwards, and turning off one it had, leaves the copy as it was. Aliasing here would be the
     * quietest of the failures — a class ability appearing on a character that never had it.
     */
    @Test
    @DisplayName("the flag sets are copied, not aliased")
    void flagSetsAreIndependent() {
        PlayerClass copy = source.copy();

        oFlags.on(ObjectFlag.OF_SEE_INVIS);
        oFlags.off(ObjectFlag.OF_FREE_ACT);
        pFlags.on(PlayerFlag.PF_FAST_SHOT);
        pFlags.off(PlayerFlag.PF_BRAVERY_30);

        assertAll(
                () -> assertTrue(copy.getoFlags().has(ObjectFlag.OF_FREE_ACT)),
                () -> assertFalse(copy.getoFlags().has(ObjectFlag.OF_SEE_INVIS)),
                () -> assertTrue(copy.getpFlags().has(PlayerFlag.PF_BRAVERY_30)),
                () -> assertFalse(copy.getpFlags().has(PlayerFlag.PF_FAST_SHOT)));
    }

    /**
     * The elements inside the copied lists are the source's own objects. That is correct rather than
     * an oversight — {@link StartItem} exposes no mutator, so sharing one costs nothing and matches
     * C, where the whole class record is shared.
     *
     * @throws ReflectiveOperationException if a field has been renamed
     */
    @Test
    @DisplayName("immutable elements are shared, not duplicated")
    void immutableElementsAreShared() throws ReflectiveOperationException {
        PlayerClass copy = source.copy();

        assertSame(startItems.get(0), ((List<?>) field(copy, "startItems")).get(0));
    }

    /**
     * A non-caster with nothing in any collection copies to an equally empty class. This is the
     * shipped warrior's shape, and the case where an accidental {@code null} or a shared empty
     * singleton would go unnoticed in the fuller fixture above.
     *
     * @throws ReflectiveOperationException if a field has been renamed
     */
    @Test
    @DisplayName("an empty non-caster copies cleanly")
    void emptyNonCasterCopies() throws ReflectiveOperationException {
        PlayerClass warrior = new PlayerClass("Warrior", new ArrayList<>(), new EnumMap<>(Stats.class),
                new EnumMap<>(PlayerSkill.class), new EnumMap<>(PlayerSkill.class), 9, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 6, 30, 5,
                new ArrayList<>(), ClassMagic.NONE);

        PlayerClass copy = warrior.copy();

        assertAll(
                () -> assertEquals("Warrior", field(copy, "name")),
                () -> assertTrue(((List<?>) field(copy, "titles")).isEmpty()),
                () -> assertTrue(((List<?>) field(copy, "startItems")).isEmpty()),
                () -> assertTrue(copy.getoFlags().isEmpty()),
                () -> assertTrue(copy.getpFlags().isEmpty()),
                () -> assertSame(ClassMagic.NONE, copy.getMagic()));
    }
}
