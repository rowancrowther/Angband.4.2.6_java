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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.middle.game.event.projection.Projection;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.ObjectProperty;
import uk.co.jackoftrades.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftrades.middle.objects.Rune;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.CombatRunes;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.RuneGroup;
import uk.co.jackoftrades.middle.objects.enums.RuneVariety;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Rune#initRunes()} against the real shipped data files, which is the only way to see
 * whether the port agrees with the original: the rune list is entirely derived, so every count in it
 * is a consequence of a rule ported from C's {@code init_rune} ({@code src/obj-knowledge.c}) meeting
 * the data in {@code lib/gamedata}.
 *
 * <p>The expected numbers below were read off the C source and the data files rather than off this
 * implementation, so they fail if the port drifts <em>or</em> if the data changes underneath it. The
 * two that carry the most information are the brand and slay counts, since those are the two
 * varieties that de-duplicate, by different rules — ten brands collapse to five runes by name, and
 * eleven slays collapse to eight by {@link Slay#sameMonsterSlain}.
 *
 * <p>{@code initRunes} reads five registries, and the readers that fill them have their own
 * dependencies (object properties resolve UI entries; slays resolve monster bases, which resolve
 * pains; curses resolve summons and object bases). {@link #seed()} loads that whole chain in
 * dependency order through the real readers, exactly as the reader suites do, and {@link #restore()}
 * puts the previous contents back so nothing leaks to another suite.
 *
 * <p>{@link ObjectRegistry} has a {@code setRunes} but no {@code getRunes}, so the list is read back
 * through {@link #loadedRunes()} by reflection.
 *
 * @author ClaudeCode
 */
class RuneInitTest {

    private static final String UI_RENDERER_FILE = "lib/gamedata/ui_entry_renderer.txt";
    private static final String UI_BASE_FILE = "lib/gamedata/ui_entry_base.txt";
    private static final String UI_ENTRY_FILE = "lib/gamedata/ui_entry.txt";
    private static final String PROPERTY_FILE = "lib/gamedata/object_property.txt";
    private static final String PROJECTION_FILE = "lib/gamedata/projection.txt";
    private static final String BRAND_FILE = "lib/gamedata/brand.txt";
    private static final String SLAY_FILE = "lib/gamedata/slay.txt";
    private static final String CURSE_FILE = "lib/gamedata/curse.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String MONSTER_BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";
    private static final String OBJECT_BASE_FILE = "lib/gamedata/object_base.txt";

    /**
     * The flag subtypes C's {@code init_rune} skips - placeholders, and properties that describe the
     * object rather than something the player can learn about themselves.
     */
    private static final Set<ObjectFlagType> EXCLUDED_SUBTYPES = EnumSet.of(
            ObjectFlagType.OFT_NONE, ObjectFlagType.OFT_MAX, ObjectFlagType.OFT_LIGHT,
            ObjectFlagType.OFT_DIG, ObjectFlagType.OFT_THROW, ObjectFlagType.OFT_CURSE_ONLY);

    private static Object savedRenderers;
    private static Object savedUiBases;
    private static Object savedUiEntries;
    private static Object savedProperties;
    private static Object savedProjections;
    private static Object savedBrands;
    private static Object savedSlays;
    private static Object savedCurses;
    private static Object savedPains;
    private static Object savedMonsterBases;
    private static Object savedSummons;
    private static Object savedObjectBases;

    @BeforeAll
    static void seed() throws Exception {
        // UI entries first: the object-property assembler resolves its bindui targets against them.
        savedRenderers = setStatic("uiEntryRenderers",
                new UIEntryRendererReader().parseWithResults(UI_RENDERER_FILE).items());
        savedUiBases = setStatic("uiEntryBases",
                new UIEntryBaseReader().parseWithResults(UI_BASE_FILE).items());
        savedUiEntries = setStatic("uiEntries",
                new UIEntryReader().parseWithResults(UI_ENTRY_FILE).items());

        // Monsters next: summons resolve bases, bases resolve pains, slays resolve bases.
        savedPains = setStatic("monsterPains",
                new PainReader().parseWithResults(PAIN_FILE).items());
        savedMonsterBases = setStatic("monsterBases",
                new MonsterBaseReader().parseWithResults(MONSTER_BASE_FILE).items());
        savedSummons = setStatic("summons",
                new SummonReader().parseWithResults(SUMMON_FILE).items());
        savedObjectBases = setStatic("objectBases",
                new ObjectBaseReader().parseWithResults(OBJECT_BASE_FILE).items());

        // The five lists initRunes actually reads.
        savedProperties = setStatic("objectProperties",
                new ObjectPropertyReader().parseWithResults(PROPERTY_FILE).items());
        savedProjections = setStatic("projections",
                new ProjectionReader().parseWithResults(PROJECTION_FILE).items());
        savedBrands = setStatic("brands",
                new BrandReader().parseWithResults(BRAND_FILE).items());
        savedSlays = setStatic("slays",
                new SlayReader().parseWithResults(SLAY_FILE).items());
        savedCurses = setStatic("curses",
                new CurseReader().parseWithResults(CURSE_FILE).items());
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("uiEntryRenderers", savedRenderers);
        setStatic("uiEntryBases", savedUiBases);
        setStatic("uiEntries", savedUiEntries);
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedMonsterBases);
        setStatic("summons", savedSummons);
        setStatic("objectBases", savedObjectBases);
        setStatic("objectProperties", savedProperties);
        setStatic("projections", savedProjections);
        setStatic("brands", savedBrands);
        setStatic("slays", savedSlays);
        setStatic("curses", savedCurses);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    /**
     * Runs {@code initRunes} and reads the resulting list back out of the registry, which exposes no
     * getter for it.
     */
    @SuppressWarnings("unchecked")
    private static List<Rune> loadedRunes() throws Exception {
        Rune.initRunes();
        Field f = RegistrySeeding.resolve("allRunes");
        f.setAccessible(true);
        return new ArrayList<>((List<Rune>) f.get(null));
    }

    private static List<RuneVariety> varietiesOf(RuneGroup group) throws Exception {
        return loadedRunes().stream()
                .map(Rune::getVariety)
                .filter(v -> v.group() == group)
                .toList();
    }

    // ---- The lookup initRunes depends on ---------------------------------

    /**
     * The five stats are declared {@code type:stat} in {@code object_property.txt}, but
     * {@link ObjectModifier} lists them alongside the modifiers, so {@code initRunes} asks for them
     * as {@code OBJ_PROPERTY_MOD}. C accommodates this with an explicit branch in
     * {@code lookup_obj_property} - "Special case - stats count as mods" - which compares the raw
     * index and ignores the mismatched type.
     *
     * <p>The port cannot take that branch. It compares whole {@link ObjectPropertyTypeWrapper}s, and
     * the wrapper's {@code equals} rejects a discriminator mismatch before it looks at the payload,
     * so a {@code MOD}-tagged query can never match a {@code STAT}-tagged property. Isolated here
     * because the failure otherwise surfaces as every {@code initRunes} test throwing the same
     * exception.
     */
    @Test
    void statBackedModifiersResolveThroughTheStatsCountAsModsPath() {
        for (ObjectModifier stat : List.of(ObjectModifier.OM_STR, ObjectModifier.OM_INT,
                ObjectModifier.OM_WIS, ObjectModifier.OM_DEX, ObjectModifier.OM_CON)) {
            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(
                    ObjPropertyType.OBJ_PROPERTY_MOD, stat);

            assertNotNull(
                    ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_MOD, wrapper),
                    () -> stat + " is declared type:stat, and must still resolve when asked for as "
                            + "a modifier");
        }
    }

    // ---- The whole list --------------------------------------------------

    @Test
    void initRunesBuildsAListWithoutThrowing() throws Exception {
        assertFalse(loadedRunes().isEmpty());
    }

    @Test
    void everyRuneStartsWithNoAutoInscription() throws Exception {
        assertTrue(loadedRunes().stream().allMatch(r -> r.getNote() == null),
                "a fresh rune list should carry no inscriptions - C zeroes the note quark");
    }

    @Test
    void noTwoRunesShareASubject() throws Exception {
        List<RuneVariety> varieties = loadedRunes().stream().map(Rune::getVariety).toList();
        Set<RuneVariety> distinct = new HashSet<>(varieties);

        assertEquals(varieties.size(), distinct.size(),
                "each property should yield exactly one rune; a duplicate means a de-duplication "
                        + "rule missed a group");
    }

    @Test
    void initRunesIsIdempotent() throws Exception {
        int first = loadedRunes().size();
        int second = loadedRunes().size();

        assertEquals(first, second, "re-running init must not accumulate or consume state");
    }

    // ---- Group ordering --------------------------------------------------

    /**
     * C's knowledge browser buckets the rune list with a single run-length pass and never sorts, so
     * a group appearing in two separate stretches would render as two panel entries with the same
     * heading. The order also has to be C's, since that is the order the player sees.
     */
    @Test
    void runesAreGroupedInCsSectionOrderWithNoGroupSplitInTwo() throws Exception {
        List<RuneGroup> groups = loadedRunes().stream().map(r -> r.getVariety().group()).toList();

        List<RuneGroup> runs = new ArrayList<>();
        for (RuneGroup g : groups) {
            if (runs.isEmpty() || runs.get(runs.size() - 1) != g) {
                runs.add(g);
            }
        }

        assertEquals(List.of(RuneGroup.COMBAT, RuneGroup.MODIFIERS, RuneGroup.RESIST,
                        RuneGroup.BRAND, RuneGroup.SLAY, RuneGroup.CURSE, RuneGroup.OTHER), runs,
                "each group must appear exactly once, in C's init_rune order");
    }

    // ---- Combat ----------------------------------------------------------

    @Test
    void combatRunesAreTheThreeEnchantmentsInOrderAndExcludeTheSentinel() throws Exception {
        List<RuneVariety> combat = varietiesOf(RuneGroup.COMBAT);

        assertEquals(3, combat.size());
        assertEquals(List.of(
                        new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A),
                        new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H),
                        new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D)),
                combat);
    }

    // ---- Modifiers -------------------------------------------------------

    /**
     * C loops {@code i < OBJ_MOD_MAX} over an enum with no {@code NONE}, so every modifier gets a
     * rune: five stats from {@code list-stats.h} plus eleven from
     * {@code list-object-modifiers.h}.
     */
    @Test
    void everyModifierGetsExactlyOneRune() throws Exception {
        List<RuneVariety> mods = varietiesOf(RuneGroup.MODIFIERS);

        assertEquals(16, mods.size());

        Set<ObjectModifier> covered = new HashSet<>();
        for (RuneVariety v : mods) {
            covered.add(((RuneVariety.ModKey) v).key());
        }
        Set<ObjectModifier> expected = EnumSet.allOf(ObjectModifier.class);
        expected.remove(ObjectModifier.OM_NONE);
        expected.remove(ObjectModifier.OM_MAX);

        assertEquals(expected, covered);
    }

    @Test
    void modifierRunesCarryTheirResolvedProperty() throws Exception {
        for (RuneVariety v : varietiesOf(RuneGroup.MODIFIERS)) {
            RuneVariety.ModKey key = (RuneVariety.ModKey) v;
            assertNotNull(key.property(), () -> "no property held for " + key.key());
            assertNotNull(key.property().getName(), () -> "no name for " + key.key());
        }
    }

    // ---- Resistances -----------------------------------------------------

    /**
     * C bounds this loop at {@code ELEM_HIGH_MAX}, which is {@code ELEM_DISEN + 1} - the four base
     * elements plus the nine high ones, and nothing past disenchantment.
     */
    @Test
    void resistRunesCoverAcidThroughDisenchantmentAndNoFurther() throws Exception {
        List<RuneVariety> resists = varietiesOf(RuneGroup.RESIST);

        assertEquals(13, resists.size());
        assertEquals(List.of(
                        ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC, ElementEnum.ELEM_FIRE,
                        ElementEnum.ELEM_COLD, ElementEnum.ELEM_POIS, ElementEnum.ELEM_LIGHT,
                        ElementEnum.ELEM_DARK, ElementEnum.ELEM_SOUND, ElementEnum.ELEM_SHARD,
                        ElementEnum.ELEM_NEXUS, ElementEnum.ELEM_NETHER, ElementEnum.ELEM_CHAOS,
                        ElementEnum.ELEM_DISEN),
                resists.stream().map(v -> ((RuneVariety.ResistKey) v).key()).toList());
    }

    @Test
    void resistRunesCarryTheMatchingProjection() throws Exception {
        for (RuneVariety v : varietiesOf(RuneGroup.RESIST)) {
            RuneVariety.ResistKey key = (RuneVariety.ResistKey) v;
            Projection projection = key.projection();

            assertNotNull(projection, () -> "no projection held for " + key.key());
            assertEquals("PROJ_" + key.key().name().substring(5),
                    projection.getProjection().name(),
                    "the projection held must be the one for this element");
        }
    }

    // ---- Brands ----------------------------------------------------------

    /**
     * {@code brand.txt} ships ten brands - a x3 and a x2 of each of acid, lightning, fire, cold and
     * poison - and C gives one rune per distinct name, so five.
     */
    @Test
    void brandsWithTheSameNameShareOneRune() throws Exception {
        List<RuneVariety> brands = varietiesOf(RuneGroup.BRAND);

        assertEquals(10, ObjectRegistry.getBrands().size(), "guard: brand.txt as shipped");
        assertEquals(5, brands.size());
        assertEquals(List.of("acid", "lightning", "fire", "cold", "poison"),
                brands.stream().map(v -> ((RuneVariety.BrandKey) v).key().getName()).toList());
    }

    @Test
    void everyBrandNameInTheDataHasARune() throws Exception {
        Set<String> runeNames = new HashSet<>();
        for (RuneVariety v : varietiesOf(RuneGroup.BRAND)) {
            runeNames.add(((RuneVariety.BrandKey) v).key().getName());
        }

        Set<String> dataNames = new HashSet<>();
        for (Brand brand : ObjectRegistry.getBrands()) {
            dataNames.add(brand.getName());
        }

        assertEquals(dataNames, runeNames,
                "de-duplication must drop duplicates, never whole names");
    }

    // ---- Slays -----------------------------------------------------------

    /**
     * {@code slay.txt} ships eleven slays across eight race flags - demons, dragons and undead each
     * appear at two strengths - and C groups by {@code same_monsters_slain} rather than by name, so
     * eight.
     */
    @Test
    void slaysKillingTheSameMonstersShareOneRune() throws Exception {
        List<RuneVariety> slays = varietiesOf(RuneGroup.SLAY);

        assertEquals(11, ObjectRegistry.getSlays().size(), "guard: slay.txt as shipped");
        assertEquals(8, slays.size());
        assertEquals(List.of("evil creatures", "animals", "orcs", "trolls", "giants", "demons",
                        "dragons", "undead"),
                slays.stream().map(v -> ((RuneVariety.SlayKey) v).key().getName()).toList());
    }

    /**
     * The grouping rule is the point of the slay loop: no two slay runes may kill the same monsters,
     * and every slay in the data must be covered by one.
     */
    @Test
    void slayRunesPartitionTheSlaysExactly() throws Exception {
        List<Slay> representatives = varietiesOf(RuneGroup.SLAY).stream()
                .map(v -> ((RuneVariety.SlayKey) v).key())
                .toList();

        for (int i = 0; i < representatives.size(); i++) {
            for (int j = i + 1; j < representatives.size(); j++) {
                Slay a = representatives.get(i);
                Slay b = representatives.get(j);
                assertFalse(a.sameMonsterSlain(b),
                        () -> "two runes for the same monsters: " + a.getCode() + " and "
                                + b.getCode());
            }
        }

        for (Slay slay : ObjectRegistry.getSlays()) {
            assertTrue(representatives.stream().anyMatch(r -> r.sameMonsterSlain(slay)),
                    () -> "no rune covers " + slay.getCode());
        }
    }

    // ---- Curses ----------------------------------------------------------

    @Test
    void everyCurseGetsItsOwnRune() throws Exception {
        List<RuneVariety> curses = varietiesOf(RuneGroup.CURSE);

        assertEquals(27, ObjectRegistry.getCurses().size(), "guard: curse.txt as shipped");
        assertEquals(ObjectRegistry.getCurses().size(), curses.size(),
                "curses are never grouped");
        assertEquals(ObjectRegistry.getCurses().stream().map(Curse::getName).toList(),
                curses.stream().map(v -> ((RuneVariety.CurseKey) v).key().getName()).toList(),
                "and they keep data-file order");
    }

    // ---- Flags -----------------------------------------------------------

    /**
     * Flags are filtered on their property's subtype rather than counted outright, so this derives
     * the expectation from the loaded properties the same way C derives it - which means it keeps
     * working if {@code object_property.txt} changes.
     */
    @Test
    void flagRunesAreExactlyTheFlagsWithALearnableSubtype() throws Exception {
        List<RuneVariety> flags = varietiesOf(RuneGroup.OTHER);

        long expected = ObjectRegistry.getObjectProperties().stream()
                .filter(p -> p.getType() == uk.co.jackoftrades.middle.objects.enums
                        .ObjPropertyType.OBJ_PROPERTY_FLAG)
                .filter(p -> !EXCLUDED_SUBTYPES.contains(p.getSubtype()))
                .count();

        assertEquals(expected, flags.size());
        assertEquals(27, flags.size(),
                "guard: 38 flags in object_property.txt, less those with an excluded subtype");
    }

    /**
     * The whole list, as a single number: three combat, sixteen modifiers, thirteen resists, five
     * brands, eight slays, twenty-seven curses and twenty-seven flags.
     */
    @Test
    void theRuneListIsNinetyNineRunesLong() throws Exception {
        assertEquals(99, loadedRunes().size());
    }

    @Test
    void noFlagRuneCarriesAnExcludedSubtype() throws Exception {
        for (RuneVariety v : varietiesOf(RuneGroup.OTHER)) {
            ObjectProperty property = ((RuneVariety.FlagKey) v).property();

            assertFalse(EXCLUDED_SUBTYPES.contains(property.getSubtype()),
                    () -> property.getName() + " has excluded subtype " + property.getSubtype());
        }
    }

    // ---- Failure behaviour -----------------------------------------------

    /**
     * C treats an unresolvable subject as impossible - a missing element projection is caught when
     * {@code projection.txt} is parsed, and a property lookup is dereferenced unguarded - so the
     * port throws rather than quietly building a shorter list.
     */
    @Test
    void initRunesThrowsWhenAPropertyCannotBeResolved() throws Exception {
        Object saved = setStatic("objectProperties", List.<ObjectProperty>of());
        try {
            assertThrows(RuntimeException.class, Rune::initRunes);
        } finally {
            setStatic("objectProperties", saved);
        }
    }

    @Test
    void initRunesThrowsWhenAnElementHasNoProjection() throws Exception {
        Object saved = setStatic("projections", List.<Projection>of());
        try {
            assertThrows(RuntimeException.class, Rune::initRunes);
        } finally {
            setStatic("projections", saved);
        }
    }
}
