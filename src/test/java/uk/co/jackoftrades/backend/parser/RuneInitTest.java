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
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
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
 * <p>The list is read back through {@link #loadedRunes()} by reflection rather than through
 * {@link ObjectRegistry#getRunes()}, so that the tests below check what {@code initRunes} stored
 * rather than what the accessor reports - the accessors themselves are checked separately, against
 * that same stored list.
 *
 * @author Rowan Crowther
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

    // ---- Rune names ------------------------------------------------------

    /**
     * The resist names come from {@code projection.txt}, not from the element list - C fills the
     * rune's name with {@code projections[i].name}, and {@code list-elements.h} carries no name at
     * all. Most of the pairs read alike, which is what makes the distinction easy to lose; the two
     * that do not are the reason this is a full list rather than a spot check.
     */
    @Test
    void resistRuneNamesArePrefixedProjectionNames() throws Exception {
        assertEquals(List.of(
                        "resist acid", "resist lightning", "resist fire", "resist cold",
                        "resist poison", "resist light", "resist dark", "resist sound",
                        "resist shards", "resist nexus", "resist nether", "resist chaos",
                        "resist disenchantment"),
                varietiesOf(RuneGroup.RESIST).stream().map(RuneVariety::runeName).toList());
    }

    /**
     * Named separately because these two are the whole risk: {@code ELEM_ELEC} projects as
     * "lightning" and {@code ELEM_DISEN} as "disenchantment", so a resist rune named off the
     * element instead of the projection is wrong here and nowhere else, and would pass any spot
     * check that happened to pick fire.
     */
    @Test
    void theTwoElementsWhoseProjectionIsNamedDifferentlyUseTheProjectionName() throws Exception {
        for (RuneVariety v : varietiesOf(RuneGroup.RESIST)) {
            RuneVariety.ResistKey key = (RuneVariety.ResistKey) v;
            if (key.key() == ElementEnum.ELEM_ELEC) {
                assertEquals("resist lightning", key.runeName());
            } else if (key.key() == ElementEnum.ELEM_DISEN) {
                assertEquals("resist disenchantment", key.runeName());
            }
        }
    }

    /**
     * {@code "%s brand"} and {@code "slay %s"} - the suffix and the prefix are the other way round
     * between the two, which is the sort of thing a port silently normalises.
     */
    @Test
    void brandAndSlayRuneNamesMatchCsFormats() throws Exception {
        assertEquals(List.of("acid brand", "lightning brand", "fire brand", "cold brand",
                        "poison brand"),
                varietiesOf(RuneGroup.BRAND).stream().map(RuneVariety::runeName).toList());
        assertEquals(List.of("slay evil creatures", "slay animals", "slay orcs", "slay trolls",
                        "slay giants", "slay demons", "slay dragons", "slay undead"),
                varietiesOf(RuneGroup.SLAY).stream().map(RuneVariety::runeName).toList());
    }

    /**
     * Every curse gets its own rune, so the names are {@code curse.txt}'s in file order with
     * {@code " curse"} appended.
     */
    @Test
    void curseRuneNamesAreTheDataNamesWithCurseAppended() throws Exception {
        assertEquals(
                ObjectRegistry.getCurses().stream().map(c -> c.getName() + " curse").toList(),
                varietiesOf(RuneGroup.CURSE).stream().map(RuneVariety::runeName).toList());
    }

    /**
     * The three varieties C returns unadorned. Modifiers and flags take the name straight off the
     * property the lookup resolved, and the combat runes take theirs from the port of
     * {@code c_rune[]}, {@link CombatRunes#getDescription()}.
     */
    @Test
    void theUnwrappedVarietiesReturnTheirSubjectsNameAsIs() throws Exception {
        assertEquals(List.of("enchantment to armour", "enchantment to hit",
                        "enchantment to damage"),
                varietiesOf(RuneGroup.COMBAT).stream().map(RuneVariety::runeName).toList());

        for (RuneVariety v : varietiesOf(RuneGroup.MODIFIERS)) {
            RuneVariety.ModKey key = (RuneVariety.ModKey) v;
            assertEquals(key.property().getName(), key.runeName());
        }
        for (RuneVariety v : varietiesOf(RuneGroup.OTHER)) {
            RuneVariety.FlagKey key = (RuneVariety.FlagKey) v;
            assertEquals(key.property().getName(), key.runeName());
        }
    }

    /**
     * C's knowledge browser prints {@code rune_name(oid)} directly into a column and uses it as the
     * title of the detail page, with no null check anywhere - so every rune in the list must have
     * real text, whatever variety it is.
     */
    @Test
    void everyRuneInTheListHasANonBlankName() throws Exception {
        for (Rune rune : loadedRunes()) {
            String name = rune.getVariety().runeName();

            assertNotNull(name, () -> "no name for " + rune.getVariety());
            assertFalse(name.isBlank(), () -> "blank name for " + rune.getVariety());
        }
    }

    /**
     * Names are what the player picks a rune out by, so two runes sharing one would be two
     * indistinguishable lines in the knowledge menu. C does not enforce this - it falls out of the
     * data and of the de-duplication rules - which is exactly why it is worth asserting: it would
     * break quietly.
     */
    @Test
    void noTwoRunesShareAName() throws Exception {
        List<String> names = loadedRunes().stream()
                .map(r -> r.getVariety().runeName())
                .toList();

        assertEquals(names.size(), new HashSet<>(names).size(),
                "duplicate rune names would render as identical knowledge-menu entries");
    }

    // ---- The registry accessors ------------------------------------------

    /**
     * {@code getRunes}/{@code getMaxRunes} read the same list {@code setRunes} fills, so after
     * {@code initRunes} they must agree with it and with each other: {@code getMaxRunes} is the port
     * of C's {@code max_runes()}, which returns the {@code rune_max} set alongside {@code rune_list}
     * in {@code init_rune} ({@code [C] src/obj-knowledge.c:131, 230}).
     *
     */
    @Test
    void theRuneAccessorsAgreeWithTheStoredList() throws Exception {
        List<Rune> stored = loadedRunes();

        assertEquals(stored.size(), ObjectRegistry.getMaxRunes());
        assertEquals(stored.size(), ObjectRegistry.getRunes().size());
        assertEquals(stored, ObjectRegistry.getRunes(), "and in the same order");
        assertEquals(99, ObjectRegistry.getMaxRunes(), "the shipped data's rune count");
    }

    /**
     * The returned list is immutable: the knowledge menu and the savefile both identify a rune by
     * its position, so a caller must not be able to reorder or extend what it is handed.
     */
    @Test
    void getRunesHandsBackAnImmutableList() throws Exception {
        loadedRunes();
        List<Rune> runes = ObjectRegistry.getRunes();

        assertThrows(UnsupportedOperationException.class, () -> runes.add(runes.getFirst()));
        assertThrows(UnsupportedOperationException.class, runes::clear);
    }

    /**
     * A snapshot, not a view: {@code setRunes} publishes a new immutable list rather than refilling
     * the old one, so a caller holding an earlier result keeps the runes it asked for. This is the
     * one behaviour a reader cannot infer from the return type - an unmodifiable {@code List} that
     * tracked the registry would satisfy the same signature and the same immutability test.
     */
    @Test
    void anEarlierResultIsUnaffectedByALaterSetRunes() throws Exception {
        List<Rune> full = loadedRunes();
        List<Rune> taken = ObjectRegistry.getRunes();

        try {
            ObjectRegistry.setRunes(List.of(full.getFirst()));

            assertEquals(full.size(), taken.size(), "the list taken must not follow the registry");
            assertEquals(1, ObjectRegistry.getMaxRunes(), "though the registry itself has moved on");
        } finally {
            ObjectRegistry.setRunes(full);
        }
    }

    /**
     * The other half of that bargain, and the reason the lookups can call {@code getRunes} once per
     * lookup without paying for it: the accessor hands back the stored list itself, so repeated
     * calls return the same object and only a rebuild produces a different one.
     *
     * <p>That makes reference identity a complete test of "have the runes been rebuilt?", which in
     * turn means a caller never has to re-run {@code initRunes} to refresh a reference - calling the
     * accessor again is enough. An accessor that copied would fail the first assertion here while
     * satisfying every other test in this section.
     */
    @Test
    void getRunesReturnsTheSameListUntilTheRunesAreRebuilt() throws Exception {
        List<Rune> full = loadedRunes();

        assertSame(ObjectRegistry.getRunes(), ObjectRegistry.getRunes(),
                "repeated calls must not allocate - the lookups call this in a loop");

        List<Rune> before = ObjectRegistry.getRunes();
        try {
            ObjectRegistry.setRunes(List.of(full.getFirst()));

            assertNotSame(before, ObjectRegistry.getRunes(),
                    "a rebuild must be visible as a different list");
        } finally {
            ObjectRegistry.setRunes(full);
        }
    }

    /**
     * Immutability is structural only. The published lists hold the live runes, not copies of them,
     * so an auto-inscription set on one is visible through every list ever handed out - which is
     * what the knowledge menu needs, and the one thing a "snapshot" here does not freeze.
     */
    @Test
    void aSnapshotStillSeesLaterInscriptions() throws Exception {
        List<Rune> taken = loadedRunes();
        Rune rune = ObjectRegistry.getRunes().getFirst();

        try {
            rune.setNote("@w1");

            assertEquals("@w1", taken.getFirst().getNote(),
                    "the list is a snapshot of membership and order, not of rune state");
        } finally {
            rune.setNote(null);
        }
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

    // ---- The lookup ------------------------------------------------------

    /**
     * The baseline every caller of C's {@code rune_index} relies on: ask for a rune by the subject
     * it was built from and get that rune back. Each variety is asked for through the overload its
     * subject selects, which is what replaces C's {@code variety} argument - at every C call site
     * that argument is a literal, so overload resolution can carry it instead.
     *
     * <p>Failures are collected rather than thrown at the first one, so a broken variety reports as
     * itself rather than hiding every variety built after it.
     */
    @Test
    void everyRuneIsFoundByTheSubjectItHolds() throws Exception {
        List<RuneVariety> unreachable = new ArrayList<>();

        for (Rune rune : loadedRunes()) {
            RuneVariety held = rune.getVariety();
            Rune found = switch (held) {
                case RuneVariety.CombatKey k -> Rune.runeIndex(k.key());
                case RuneVariety.ModKey k -> Rune.runeIndex(k.key());
                case RuneVariety.ResistKey k -> Rune.runeIndex(k.key());
                case RuneVariety.BrandKey k -> Rune.runeIndex(k.key());
                case RuneVariety.SlayKey k -> Rune.runeIndex(k.key());
                case RuneVariety.CurseKey k -> Rune.runeIndex(k.key());
                case RuneVariety.FlagKey k -> Rune.runeIndex(k.key());
            };

            if (found != rune) unreachable.add(held);
        }

        assertEquals(List.of(), unreachable,
                "every rune must be findable by the subject it was built from");
    }

    /**
     * The lookups keyed on an enum must compare the enum, not the record wrapping it, so a caller
     * holding nothing but the constant - C's {@code rune_index(RUNE_VAR_COMBAT, COMBAT_RUNE_TO_A)} -
     * finds the rune. Spelled out with literals for the fixed varieties, since those call sites are
     * written by hand rather than derived from the list.
     */
    @Test
    void theFixedSubjectsAreFoundByTheirConstants() throws Exception {
        loadedRunes();

        for (CombatRunes combat : List.of(CombatRunes.COMBAT_RUNE_TO_A,
                CombatRunes.COMBAT_RUNE_TO_H, CombatRunes.COMBAT_RUNE_TO_D)) {
            Rune found = Rune.runeIndex(combat);

            assertNotNull(found, () -> "no rune found for " + combat);
            assertEquals(new RuneVariety.CombatKey(combat), found.getVariety());
        }

        Rune speed = Rune.runeIndex(ObjectModifier.OM_SPEED);
        assertNotNull(speed, "no rune found for OM_SPEED");
        assertEquals(ObjectModifier.OM_SPEED, ((RuneVariety.ModKey) speed.getVariety()).key());

        Rune fire = Rune.runeIndex(ElementEnum.ELEM_FIRE);
        assertNotNull(fire, "no rune found for ELEM_FIRE");
        assertEquals(ElementEnum.ELEM_FIRE, ((RuneVariety.ResistKey) fire.getVariety()).key());

        Rune feather = Rune.runeIndex(ObjectFlag.OF_FEATHER);
        assertNotNull(feather, "no rune found for OF_FEATHER");
        assertEquals(ObjectFlag.OF_FEATHER, ((RuneVariety.FlagKey) feather.getVariety()).key());
    }

    /**
     * De-duplication makes the brand held by a rune a <em>representative</em> of its name group, so
     * a caller holding any other member of that group - the x2 acid brand where the rune holds the
     * x3 - must still find the rune. C has no difficulty here because it stores only the name and
     * compares with {@code streq}.
     */
    @Test
    void aBrandFindsItsRuneEvenWhenItIsNotTheOneHeld() throws Exception {
        List<RuneVariety> runeVarieties = varietiesOf(RuneGroup.BRAND);

        for (Brand brand : ObjectRegistry.getBrands()) {
            if (brand.getName() == null) continue;

            RuneVariety expected = runeVarieties.stream()
                    .filter(v -> ((RuneVariety.BrandKey) v).key().getName().equals(brand.getName()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("no rune for brand " + brand.getName()));

            Rune found = Rune.runeIndex(brand);

            assertNotNull(found, () -> "no rune found for brand " + brand.getName());
            assertEquals(expected, found.getVariety(),
                    () -> "brand " + brand.getName() + " found the wrong rune");
        }
    }

    /**
     * The same point for slays, where the grouping is by {@link Slay#sameMonsterSlain} rather than
     * by name - so the group members a lookup has to tolerate need not even share a name.
     */
    @Test
    void aSlayFindsItsRuneEvenWhenItIsNotTheOneHeld() throws Exception {
        List<RuneVariety> runeVarieties = varietiesOf(RuneGroup.SLAY);

        for (Slay slay : ObjectRegistry.getSlays()) {
            if (slay.getName() == null) continue;

            RuneVariety expected = runeVarieties.stream()
                    .filter(v -> ((RuneVariety.SlayKey) v).key().sameMonsterSlain(slay))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("no rune for slay " + slay.getName()));

            Rune found = Rune.runeIndex(slay);

            assertNotNull(found, () -> "no rune found for slay " + slay.getName());
            assertEquals(expected, found.getVariety(),
                    () -> "slay " + slay.getName() + " found the wrong rune");
        }
    }

    /**
     * C returns {@code -1} for a subject with no rune - the flags {@code init_rune} skips, and the
     * elements above disenchantment. The port's equivalent is {@code null}.
     */
    @Test
    void anElementWithNoResistRuneIsNotFound() throws Exception {
        loadedRunes();

        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            if (element.isHasResistRune()) continue;

            assertNull(Rune.runeIndex(element),
                    () -> element + " carries no resist rune, so the lookup must find nothing");
        }
    }

    /**
     * The other half of C's {@code -1}: the flags {@code init_rune} skips because they describe the
     * object rather than the player, or only ever appear on curses. Asking for one is a legitimate
     * question with the answer "no rune", not a lookup failure.
     */
    @Test
    void aFlagWithNoRuneIsNotFound() throws Exception {
        loadedRunes();

        for (ObjectFlag flag : ObjectFlag.values()) {
            if (flag == ObjectFlag.OF_NONE || flag == ObjectFlag.OF_MAX) continue;

            ObjectProperty property = ObjectRegistry.lookupObjectProperty(
                    ObjPropertyType.OBJ_PROPERTY_FLAG,
                    new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag));
            if (property == null || !EXCLUDED_SUBTYPES.contains(property.getSubtype())) continue;

            assertNull(Rune.runeIndex(flag),
                    () -> flag + " is subtype " + property.getSubtype()
                            + ", which init_rune skips, so the lookup must find nothing");
        }
    }
}
