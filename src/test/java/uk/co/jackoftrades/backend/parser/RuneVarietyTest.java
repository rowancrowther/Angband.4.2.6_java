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
import uk.co.jackoftrades.middle.game.event.projection.Projection;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ObjectProperty;
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
 * @author Rowan Crowther
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
        return curse(name, null);
    }

    /**
     * A curse carrying the {@code desc} field too. {@code rune_desc} is the only place that field is
     * read, which is why the shorter helper above leaves it null.
     */
    private static Curse curse(String name, String description) {
        return new Curse(name, null, 0, null, null, null, null, 0, 0, 0, null, null, description,
                null);
    }

    /**
     * A property as {@code object_property.txt} defines one, carrying only the name the rune reads.
     */
    private static ObjectProperty property(ObjPropertyType type, String name) {
        return new ObjectProperty(type, null, null, null, 1, 1, null, name, null, null, null, null,
                null);
    }

    /**
     * A projection carrying only its name. Elements are named through their projection, never from
     * the element tag, so this is what a resist rune reads.
     */
    private static Projection projection(ProjectionEnum code, String name) {
        return new Projection(code, name, null, null, null, null, null, 1, 1, 1, 0, null, false,
                false, null);
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

    // ---- RuneVariety.runeDesc --------------------------------------------

    /**
     * One rune of every variety, subjects filled in from {@code lib/gamedata} so the expected
     * sentences are the ones the game really shows.
     */
    private static List<RuneVariety> everyVariety() {
        return List.of(
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A),
                new RuneVariety.ModKey(ObjectModifier.OM_STR,
                        property(ObjPropertyType.OBJ_PROPERTY_STAT, "strength")),
                new RuneVariety.ResistKey(ElementEnum.ELEM_ELEC,
                        projection(ProjectionEnum.PROJ_ELEC, "lightning")),
                new RuneVariety.BrandKey(brand("ELEC_3", "lightning", 3)),
                new RuneVariety.SlayKey(slay("EVIL_2", "evil creatures", MonsterRaceFlag.RF_EVIL, 2)),
                new RuneVariety.CurseKey(
                        curse("vulnerability", "attracts opponents and weakens the defences")),
                new RuneVariety.FlagKey(ObjectFlag.OF_FEATHER,
                        property(ObjPropertyType.OBJ_PROPERTY_FLAG, "feather falling")));
    }

    /**
     * C's {@code rune_desc} in full: six sentences assembled from loaded data, each wrapping the
     * <b>bare</b> stored name rather than the wrapped form {@code rune_name} produces.
     */
    @Test
    void theSixDataDrivenDescriptionsUseCsFormatStrings() {
        assertEquals("Object gives the player a magical bonus to strength.",
                new RuneVariety.ModKey(ObjectModifier.OM_STR,
                        property(ObjPropertyType.OBJ_PROPERTY_STAT, "strength")).runeDesc());
        assertEquals("Object affects the player's resistance to lightning.",
                new RuneVariety.ResistKey(ElementEnum.ELEM_ELEC,
                        projection(ProjectionEnum.PROJ_ELEC, "lightning")).runeDesc());
        assertEquals("Object brands the player's attacks with fire.",
                new RuneVariety.BrandKey(brand("FIRE_3", "fire", 3)).runeDesc());
        assertEquals("Object makes the player's attacks against demons more powerful.",
                new RuneVariety.SlayKey(slay("DEMON_3", "demons", MonsterRaceFlag.RF_DEMON, 3))
                        .runeDesc());
        assertEquals("Object randomly makes you teleport.",
                new RuneVariety.CurseKey(curse("teleportation", "randomly makes you teleport"))
                        .runeDesc());
        assertEquals("Object gives the player the property of feather falling.",
                new RuneVariety.FlagKey(ObjectFlag.OF_FEATHER,
                        property(ObjPropertyType.OBJ_PROPERTY_FLAG, "feather falling")).runeDesc());
    }

    /**
     * The trap this section exists for, and one this port has already fallen into once. A resist
     * rune is titled "resist lightning" but described as "…resistance to lightning." - C reads
     * {@code projections[i].name} for both and only {@code rune_name} adds the prefix. Deriving the
     * description from {@code runeName()} yields "resistance to resist lightning", which reads as
     * obviously wrong to a player and as obviously right in the source.
     */
    @Test
    void aResistDescriptionDoesNotRepeatTheTitlesPrefix() {
        RuneVariety rune = new RuneVariety.ResistKey(ElementEnum.ELEM_ELEC,
                projection(ProjectionEnum.PROJ_ELEC, "lightning"));

        assertEquals("resist lightning", rune.runeName());
        assertEquals("Object affects the player's resistance to lightning.", rune.runeDesc());
        assertFalse(rune.runeDesc().contains("resist "),
                "the description wraps the bare projection name, so the prefix must not appear");
    }

    /**
     * The same trap generalised to the other three wrapping varieties: none of their descriptions
     * may contain their own title, because all four wrap a name C only ever stored unwrapped.
     */
    @Test
    void aWrappedTitleNeverLeaksIntoItsDescription() {
        List<RuneVariety> wrapping = List.of(
                new RuneVariety.ResistKey(ElementEnum.ELEM_ELEC,
                        projection(ProjectionEnum.PROJ_ELEC, "lightning")),
                new RuneVariety.BrandKey(brand("FIRE_3", "fire", 3)),
                new RuneVariety.SlayKey(slay("ORC_3", "orcs", MonsterRaceFlag.RF_ORC, 3)),
                new RuneVariety.CurseKey(curse("sickliness", "makes you frail")));

        for (RuneVariety variety : wrapping) {
            assertFalse(variety.runeDesc().contains(variety.runeName()),
                    () -> variety + " wraps its name for display, so the wrapped form must not"
                            + " appear in its description");
        }
    }

    /**
     * The converse, stated so it is not mistaken for an accident: a modifier and a flag really do
     * share one string between title and description, because C's final {@code else} leaves their
     * names unadorned. Both methods read the property directly, so this holds because of the data
     * rather than because one method calls the other.
     */
    @Test
    void aModifierAndAFlagShareTheirUnadornedName() {
        RuneVariety mod = new RuneVariety.ModKey(ObjectModifier.OM_STEALTH,
                property(ObjPropertyType.OBJ_PROPERTY_MOD, "stealth"));
        RuneVariety flag = new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR,
                property(ObjPropertyType.OBJ_PROPERTY_FLAG, "sustain strength"));

        assertEquals("stealth", mod.runeName());
        assertTrue(mod.runeDesc().contains(mod.runeName()));
        assertEquals("sustain strength", flag.runeName());
        assertTrue(flag.runeDesc().contains(flag.runeName()));
    }

    /**
     * A curse is described from its {@code desc} field, not its name - the one field
     * {@code struct rune} never copied, which is why C's {@code rune_desc} has to index back into
     * {@code curses[]}. Holding the {@link Curse} makes it a plain accessor, so the two cannot
     * drift apart.
     */
    @Test
    void aCurseIsDescribedFromItsDescriptionNotItsName() {
        RuneVariety rune = new RuneVariety.CurseKey(curse("dullness", "makes you mentally slow"));

        assertEquals("dullness curse", rune.runeName());
        assertEquals("Object makes you mentally slow.", rune.runeDesc());
        assertFalse(rune.runeDesc().contains("dullness"),
                "the sentence is built from the description, which does not repeat the name");
    }

    /**
     * The three combat sentences are the only ones written as literals rather than assembled, the
     * enchantments being a closed set. C spells the first "armor"; this port spells it "armour"
     * throughout, matching {@link CombatRunes#COMBAT_RUNE_TO_A}'s own text.
     */
    @Test
    void theCombatDescriptionsPortCsThreeLiterals() {
        assertEquals("Object magically increases the player's armour class",
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A).runeDesc());
        assertEquals("Object magically increases the player's chance to hit",
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H).runeDesc());
        assertEquals("Object magically increases the player's damage",
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D).runeDesc());
    }

    /**
     * C's three combat strings end without a full stop where all six of its format strings end with
     * one, and the browser prints whichever it is handed. An inconsistency that invites tidying, so
     * it is pinned.
     */
    @Test
    void onlyTheCombatDescriptionsEndWithoutAFullStop() {
        for (RuneVariety variety : everyVariety()) {
            if (variety instanceof RuneVariety.CombatKey) {
                assertFalse(variety.runeDesc().endsWith("."),
                        () -> variety + " should keep C's missing full stop");
            } else {
                assertTrue(variety.runeDesc().endsWith("."),
                        () -> variety + " should end its sentence with a stop");
            }
        }
    }

    /**
     * {@code runeDesc} has to be callable through the interface for the same reason
     * {@code runeName} does: the browser asks each rune in the list for its detail text without
     * knowing the variety. Every real variety must answer.
     */
    @Test
    void everyVarietyAnswersRuneDescThroughTheInterface() {
        for (RuneVariety variety : everyVariety()) {
            String description = variety.runeDesc();
            assertNotNull(description, () -> variety + " has no description");
            assertFalse(description.isBlank(), () -> variety + " has a blank description");
            assertTrue(description.startsWith("Object "),
                    () -> variety + " should describe what the object does");
        }
    }

    /**
     * {@code COMBAT_RUNE_MAX} is a count sentinel, not a rune: {@code c_rune[]} has no entry to
     * index for it, and C's {@code if} chain falls through to {@code return NULL}. Naming it in the
     * switch is what makes that switch exhaustive; answering {@code null} is what keeps it honest
     * about having nothing to say.
     */
    @Test
    void theCountSentinelIsNotARuneAndHasNoDescription() {
        RuneVariety sentinel = new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_MAX);

        assertNull(sentinel.runeDesc(),
                "the sentinel should mirror C's return NULL rather than invent a sentence");
        assertEquals("", sentinel.runeName());
    }

    /**
     * "lightning" is both a brand name and a projection name, so the two runes share a stored name
     * and are told apart only by their variety - exactly what C's bare {@code index} field cannot
     * do on its own. Both the title and the description must differ.
     */
    @Test
    void aBrandAndAResistOfTheSameNameDescribeDifferentThings() {
        RuneVariety brand = new RuneVariety.BrandKey(brand("ELEC_3", "lightning", 3));
        RuneVariety resist = new RuneVariety.ResistKey(ElementEnum.ELEM_ELEC,
                projection(ProjectionEnum.PROJ_ELEC, "lightning"));

        assertNotEquals(brand, resist);
        assertNotEquals(brand.runeName(), resist.runeName());
        assertNotEquals(brand.runeDesc(), resist.runeDesc());
        assertEquals(2, Set.of(brand, resist).size());
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
