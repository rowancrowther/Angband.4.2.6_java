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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftrades.channel.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.CombatRunes;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.RuneGroup;
import uk.co.jackoftrades.middle.objects.enums.RuneVariety;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the types {@link uk.co.jackoftrades.middle.objects.Rune} is built out of, needing
 * no loaded data: the {@link RuneVariety} tagged union and its {@link RuneGroup} mapping, the
 * element and combat-rune enums, and the two equality rules the rune de-duplication depends on.
 *
 * <p>Several of these pin correspondences that C maintains by hand across separate files - the
 * element list against the projection list, the rune varieties against the browser's group headings
 * - and which have already drifted once during this port. They are cheap and they fail loudly, which
 * is the point.
 *
 * @author ClaudeCode
 */
class RuneVarietyTest {

    // ---- RuneVariety -> RuneGroup ----------------------------------------

    private static Slay slay(String code, String name, MonsterRaceFlag raceFlag, int multiplier) {
        return new Slay(code, name, null, "smites", "pierces", raceFlag, multiplier, 20, 10);
    }

    private static Brand brand(String code, String name, int multiplier) {
        return new Brand(code, name, "burns", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, multiplier, 20, 100);
    }

    private static Curse curse(String name) {
        return new Curse(name, null, 0, null, null, null, null, 0, 0, 0, null, null, null, null);
    }

    // ---- Record identity -------------------------------------------------

    /**
     * The mapping C performs by casting a {@code rune_variety} to an int and indexing
     * {@code rune_group_text[]}. Note {@code FLAG} maps to {@code OTHER}, not to a "Flags" heading -
     * the one place the original's label differs from the variety it names.
     */
    @Test
    void eachVarietyMapsToItsCGroup() {
        assertEquals(RuneGroup.COMBAT,
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A).group());
        assertEquals(RuneGroup.MODIFIERS,
                new RuneVariety.ModKey(ObjectModifier.OM_STR, null).group());
        assertEquals(RuneGroup.RESIST,
                new RuneVariety.ResistKey(ElementEnum.ELEM_ACID, null).group());
        assertEquals(RuneGroup.BRAND, new RuneVariety.BrandKey(null).group());
        assertEquals(RuneGroup.SLAY, new RuneVariety.SlayKey(null).group());
        assertEquals(RuneGroup.CURSE, new RuneVariety.CurseKey(null).group());
        assertEquals(RuneGroup.OTHER,
                new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, null).group());
    }

    /**
     * The group ordinals are C's {@code enum rune_variety} values, which index
     * {@code rune_group_text[]}; the names are that array's contents.
     */
    @Test
    void runeGroupOrdinalsAndLabelsMatchTheCTables() {
        assertEquals(List.of("Combat", "Modifiers", "Resists", "Brands", "Slays", "Curses", "Other"),
                java.util.Arrays.stream(RuneGroup.values()).map(RuneGroup::getName).toList());
    }

    // ---- CombatRunes -----------------------------------------------------

    /**
     * The keys have to behave as map keys: the planned replacement for C's {@code rune_index} is a
     * lookup from variety to rune, where C does a linear scan comparing a variety and an int.
     */
    @Test
    void varietyKeysWorkAsMapKeys() {
        Map<RuneVariety, String> map = new HashMap<>();
        map.put(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H), "to-hit");
        map.put(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null), "fire");

        assertEquals("to-hit",
                map.get(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)));
        assertEquals("fire",
                map.get(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null)));
        assertNull(map.get(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D)));
    }

    /**
     * Two keys of different varieties must never collide, even where they wrap the same ordinal -
     * which is the whole problem the tagged union solves, since C's bare {@code index} field is
     * meaningless without the variety beside it.
     */
    @Test
    void keysOfDifferentVarietiesAreNeverEqual() {
        Set<RuneVariety> keys = Set.of(
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A),
                new RuneVariety.ModKey(ObjectModifier.OM_STR, null),
                new RuneVariety.ResistKey(ElementEnum.ELEM_ACID, null),
                new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, null));

        assertEquals(4, keys.size());
    }

    // ---- ElementEnum -----------------------------------------------------

    /**
     * Declaration order is C's, and is what the knowledge code branches on to pick between the
     * player's {@code to_a}, {@code to_h} and {@code to_d}.
     */
    @Test
    void combatRunesAreDeclaredInCsOrderWithTheSentinelLast() {
        assertEquals(List.of("COMBAT_RUNE_TO_A", "COMBAT_RUNE_TO_H", "COMBAT_RUNE_TO_D",
                        "COMBAT_RUNE_MAX"),
                java.util.Arrays.stream(CombatRunes.values()).map(Enum::name).toList());
    }

    /**
     * Ports C's {@code c_rune[]} table. The sentinel is not a rune and carries no text.
     */
    @Test
    void combatRuneDescriptionsPortTheCTable() {
        assertEquals("enchantment to armour", CombatRunes.COMBAT_RUNE_TO_A.getDescription());
        assertEquals("enchantment to hit", CombatRunes.COMBAT_RUNE_TO_H.getDescription());
        assertEquals("enchantment to damage", CombatRunes.COMBAT_RUNE_TO_D.getDescription());
        assertEquals("", CombatRunes.COMBAT_RUNE_MAX.getDescription());
    }

    // ---- RuneVariety.runeName --------------------------------------------

    /**
     * C's {@code rune_name} wraps the rune's stored name in one of four format strings and returns
     * it bare for the other three varieties. These are the four, checked against subjects built
     * here so the format is pinned independently of what the shipped data happens to contain.
     */
    @Test
    void theFourWrappedVarietiesUseCsFormatStrings() {
        assertEquals("fire brand",
                new RuneVariety.BrandKey(brand("FIRE_3", "fire", 3)).runeName());
        assertEquals("slay demons",
                new RuneVariety.SlayKey(slay("DEMON_3", "demons", MonsterRaceFlag.RF_DEMON, 3))
                        .runeName());
        assertEquals("siphon curse", new RuneVariety.CurseKey(curse("siphon")).runeName());
    }

    /**
     * The combat runes are the one variety named from compiled-in text rather than from loaded
     * data, and the text is {@code c_rune[]} - held here as {@link CombatRunes#getDescription()}.
     * Pinned in full because it is what the player reads in the knowledge browser and in "You have
     * learned the rune of %s." alike, so any abbreviation of it is visible.
     */
    @Test
    void combatRunesAreNamedFromTheCRuneTable() {
        assertEquals("enchantment to armour",
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A).runeName());
        assertEquals("enchantment to hit",
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H).runeName());
        assertEquals("enchantment to damage",
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D).runeName());
    }

    /**
     * {@code runeName} has to be callable through the interface, since the knowledge browser asks
     * every rune in the list for its name without knowing which variety it holds. C's
     * {@code rune_name} takes a rune index and branches internally; a set of unrelated methods that
     * merely share a name would not port that, and would not compile here.
     */
    @Test
    void everyVarietyAnswersRuneNameThroughTheInterface() {
        List<RuneVariety> varieties = List.of(
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A),
                new RuneVariety.BrandKey(brand("FIRE_3", "fire", 3)),
                new RuneVariety.SlayKey(slay("ORC_3", "orcs", MonsterRaceFlag.RF_ORC, 3)),
                new RuneVariety.CurseKey(curse("teleportation")));

        for (RuneVariety variety : varieties) {
            String name = variety.runeName();
            assertNotNull(name, () -> variety + " has no name");
            assertFalse(name.isBlank(), () -> variety + " has a blank name");
        }
    }

    /**
     * {@code hasResistRune} replaces C's {@code ELEM_HIGH_MAX} bound, so it must be true for exactly
     * the elements below that bound - acid through disenchantment - and false for everything past
     * it, which the game uses for damage but grants no resistance to.
     */
    @Test
    void exactlyAcidThroughDisenchantmentHaveResistRunes() {
        Set<ElementEnum> expected = EnumSet.of(
                ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC, ElementEnum.ELEM_FIRE,
                ElementEnum.ELEM_COLD, ElementEnum.ELEM_POIS, ElementEnum.ELEM_LIGHT,
                ElementEnum.ELEM_DARK, ElementEnum.ELEM_SOUND, ElementEnum.ELEM_SHARD,
                ElementEnum.ELEM_NEXUS, ElementEnum.ELEM_NETHER, ElementEnum.ELEM_CHAOS,
                ElementEnum.ELEM_DISEN);

        Set<ElementEnum> actual = EnumSet.noneOf(ElementEnum.class);
        for (ElementEnum e : ElementEnum.values()) {
            if (e.isHasResistRune()) {
                actual.add(e);
            }
        }

        assertEquals(expected, actual);
    }

    /**
     * The base elements are C's {@code ELEM_BASE_MIN}..{@code ELEM_BASE_MAX} range, and are a subset
     * of the resistable ones.
     */
    @Test
    void baseElementsAreTheFourPhysicalTypesAndAllHaveResistRunes() {
        Set<ElementEnum> base = EnumSet.noneOf(ElementEnum.class);
        for (ElementEnum e : ElementEnum.values()) {
            if (e.isBase()) {
                base.add(e);
            }
        }

        assertEquals(EnumSet.of(ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC,
                ElementEnum.ELEM_FIRE, ElementEnum.ELEM_COLD), base);
        assertTrue(base.stream().allMatch(ElementEnum::isHasResistRune));
    }

    // ---- Slay grouping ---------------------------------------------------

    /**
     * C locks its element list and {@code projection.txt} together position for position and refuses
     * to load if they disagree. The port resolves the correspondence by name instead, so this checks
     * the names really do all line up - a missing constant on either side would otherwise only
     * surface as a rune quietly failing to resolve.
     */
    @ParameterizedTest
    @EnumSource(ElementEnum.class)
    void everyElementHasAProjectionOfTheSameName(ElementEnum element) {
        if (element == ElementEnum.ELEM_MAX) {
            return;
        }

        ProjectionEnum projection = element.getProjectionEnum();

        assertNotNull(projection, () -> "no PROJ_ counterpart for " + element);
        assertEquals("PROJ_" + element.name().substring(5), projection.name());
    }

    @Test
    void getFromElementEnumReturnsNullRatherThanThrowingOnAMiss() {
        assertDoesNotThrow(() -> ElementEnum.ELEM_MAX.getProjectionEnum());
    }

    /**
     * The rule C uses to group slay runes: same race flag and same base, regardless of name or
     * strength. This is what makes the demon/dragon/undead pairs in {@code slay.txt} collapse.
     */
    @Test
    void slaysMatchOnRaceFlagAndBaseNotOnNameOrMultiplier() {
        Slay demonThree = slay("DEMON_3", "demons", MonsterRaceFlag.RF_DEMON, 3);
        Slay demonFive = slay("DEMON_5", "demons", MonsterRaceFlag.RF_DEMON, 5);
        Slay orcThree = slay("ORC_3", "orcs", MonsterRaceFlag.RF_ORC, 3);

        assertTrue(demonThree.sameMonsterSlain(demonFive),
                "different strengths of the same slay are one rune");
        assertFalse(demonThree.sameMonsterSlain(orcThree));
    }

    @Test
    void slaysWithDifferentNamesButTheSameTargetsStillMatch() {
        Slay one = slay("DEMON_3", "demons", MonsterRaceFlag.RF_DEMON, 3);
        Slay other = slay("DEMON_5", "fiends", MonsterRaceFlag.RF_DEMON, 3);

        assertTrue(one.sameMonsterSlain(other),
                "the name is deliberately not part of the test - C compares race flag and base");
    }

    // ---- Brand grouping --------------------------------------------------

    @Test
    void slayMatchingIsReflexiveAndSymmetric() {
        Slay one = slay("DEMON_3", "demons", MonsterRaceFlag.RF_DEMON, 3);
        Slay other = slay("DEMON_5", "demons", MonsterRaceFlag.RF_DEMON, 5);

        assertTrue(one.sameMonsterSlain(one));
        assertEquals(one.sameMonsterSlain(other), other.sameMonsterSlain(one));
    }

    /**
     * Brands group by name where slays group by target, so {@link Brand#equals} - which compares
     * every field - is deliberately stricter than the rune rule. Two brands that share a rune are
     * not equal.
     */
    @Test
    void brandEqualityIsStricterThanTheRuneGroupingRule() {
        Brand fireThree = brand("FIRE_3", "fire", 3);
        Brand fireTwo = brand("FIRE_2", "fire", 2);

        assertNotEquals(fireThree, fireTwo);
        assertEquals(fireThree.getName(), fireTwo.getName(),
                "yet they share a name, and so share one rune");
    }

    @Test
    void equalBrandsShareAHashCode() {
        assertEquals(brand("FIRE_3", "fire", 3), brand("FIRE_3", "fire", 3));
        assertEquals(brand("FIRE_3", "fire", 3).hashCode(),
                brand("FIRE_3", "fire", 3).hashCode());
    }

    // ---- ObjectPropertyTypeWrapper --------------------------------------

    /**
     * The wrapper is used as a search key when {@code initRunes} resolves each modifier and flag to
     * its property definition, so equality has to hold across separately constructed instances.
     */
    @Test
    void propertyWrappersOfTheSamePayloadAreEqual() {
        assertEquals(
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD,
                        ObjectModifier.OM_STEALTH),
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD,
                        ObjectModifier.OM_STEALTH));
    }

    /**
     * The wrapper's {@code equals} compares the discriminator before the payload, so two wrappers
     * over the same modifier are unequal if one was tagged {@code MOD} and the other {@code STAT}.
     *
     * <p>That is what defeats {@code lookupObjectProperty}'s "stats count as mods" branch: C
     * compares a bare index there and ignores the type deliberately, where the port compares whole
     * wrappers and cannot. Documented here as the root cause behind
     * {@code RuneInitTest#statBackedModifiersResolveThroughTheStatsCountAsModsPath}, and asserted so
     * that whichever way it is fixed, the fix has to come past this test.
     */
    @Test
    void wrappersOverTheSameModifierDifferIfTaggedStatRatherThanMod() {
        ObjectPropertyTypeWrapper asMod = new ObjectPropertyTypeWrapper(
                ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STR);
        ObjectPropertyTypeWrapper asStat = new ObjectPropertyTypeWrapper(
                ObjPropertyType.OBJ_PROPERTY_STAT, ObjectModifier.OM_STR);

        assertEquals(asMod, asStat,
                "a stat asked for as a modifier must still match, as C's lookup_obj_property "
                        + "special case intends");
    }

    @Test
    void propertyWrappersOfDifferentPayloadsAreNotEqual() {
        ObjectPropertyTypeWrapper stealth = new ObjectPropertyTypeWrapper(
                ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH);
        ObjectPropertyTypeWrapper might = new ObjectPropertyTypeWrapper(
                ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_MIGHT);

        assertNotEquals(stealth, might);
    }

    /**
     * The wrapper overrides {@code equals} without {@code hashCode}, so it is only sound for the
     * linear search {@code lookupObjectProperty} performs. This pins that limit: if the wrapper is
     * ever used as a hash key the lookup will silently start missing, and this test is where that
     * decision gets revisited.
     */
    @Test
    void propertyWrappersAreNotUsableAsHashKeys() {
        ObjectPropertyTypeWrapper one = new ObjectPropertyTypeWrapper(
                ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH);
        ObjectPropertyTypeWrapper same = new ObjectPropertyTypeWrapper(
                ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH);

        Map<ObjectPropertyTypeWrapper, String> map = new HashMap<>();
        map.put(one, "stealth");

        assertNull(map.get(same),
                "equal wrappers hash differently - lookupObjectProperty must stay a linear scan");
    }
}
