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
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.magic.MagicBook;
import uk.co.jackoftrades.middle.player.PlayerClass;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link PlayerClassReader}: file text ->
 * {@code PlayerClassLexer}/{@code PlayerClassGrammar} ->
 * {@link uk.co.jackoftrades.backend.parser.playerclass.PlayerClassParseRecord} ->
 * {@link uk.co.jackoftrades.backend.parser.playerclass.PlayerClassAssembler} -> {@link PlayerClass}.
 *
 * <p>The class assembler is the deepest cross-referencing loader in the port: for casters it
 * descends class -&gt; magic -&gt; book -&gt; spell -&gt; effect, resolving three registries through
 * {@link GameConstants} along the way — magic {@link uk.co.jackoftrades.middle.magic.MagicRealm}s (a
 * book's {@code realm}), {@link uk.co.jackoftrades.middle.objects.ObjectBase}s (a book's object base,
 * used to synthesise the book's {@link uk.co.jackoftrades.middle.objects.ObjectKind}), and
 * {@link uk.co.jackoftrades.middle.player.PlayerShape}s (every {@code SHAPECHANGE} spell effect).
 * {@link #seed()} loads the real {@code realm.txt}, {@code object_base.txt} and {@code shape.txt}
 * through their own readers and injects them into the private static registries by reflection,
 * restoring them afterwards so no global state leaks to other suites.
 *
 * <p>The suite has three parts, mirroring what was asked of it:
 * <ol>
 *   <li><b>The happy path</b> — the real {@code class.txt} loads all nine classes with no errors
 *       (itself proof the whole descent resolved), plus targeted field checks and a synthetic
 *       minimal caster built from scratch.</li>
 *   <li><b>Every reachable soft-error site</b> — one focused test per place the pipeline can append
 *       a soft error and drop/keep a record. See the note below on what "reachable" means here.</li>
 *   <li><b>Two different errors on one path</b> — a single effect block carrying two distinct bad
 *       effects, proving {@code EffectAssembler} keeps going and reports both rather than failing
 *       fast on the first.</li>
 * </ol>
 *
 * <p><b>What is deliberately not tested:</b> the assemblers guard every numeric field with a
 * {@code NumberFormatException} catch, but each of those fields is lexed as an integer token
 * (INTEGER / DELIM_INTEGER / PROP_INT), so a non-numeric value is rejected by the <em>grammar</em>
 * as a hard syntax error and never reaches the {@code parseInt}. Those catches are therefore
 * defensive and unreachable through a data file; a file cannot exercise them. The same is true of the
 * stat/skill enum-name guards — the grammar hard-codes those key names ({@code STAT_STR},
 * {@code SKILL_TO_HIT_MELEE}, …), so they are always valid.
 *
 * <p>{@link PlayerClass} exposes no getters, so field-level assertions read its private fields
 * reflectively via {@link #field}.
 *
 * @author Rowan Crowther
 */
class PlayerClassReaderTest {

    private static final String CLASS_FILE = "lib/gamedata/class.txt";
    private static final String REALM_FILE = "lib/gamedata/realm.txt";
    private static final String OBJECT_BASE_FILE = "lib/gamedata/object_base.txt";
    private static final String SHAPE_FILE = "lib/gamedata/shape.txt";

    private static Object savedRealms, savedObjectBases, savedShapes;

    /**
     * The one shared load of the real file; the happy-path tests assert against this.
     */
    private static ParseResult<PlayerClass> result;

    @TempDir
    Path tempDir;

    /**
     * Seeds the three registries the class assembler resolves against — realms, object bases and
     * player shapes — then loads the real {@code class.txt} once for the happy-path tests.
     *
     * @author Rowan Crowther
     */
    @BeforeAll
    static void seed() throws Exception {
        savedRealms = setStatic("realms", new RealmReader().parse(REALM_FILE));
        savedObjectBases = setStatic("objectBases", new ObjectBaseReader().parse(OBJECT_BASE_FILE));
        savedShapes = setStatic("playerShapes", new ShapeReader().parse(SHAPE_FILE));

        result = new PlayerClassReader().parseWithResults(CLASS_FILE);
    }

    /**
     * Restores the registries mutated by {@link #seed()} so the shared static state on
     * {@link GameConstants} does not leak into other test suites.
     *
     * @author Rowan Crowther
     */
    @AfterAll
    static void restore() throws Exception {
        setStatic("realms", savedRealms);
        setStatic("objectBases", savedObjectBases);
        setStatic("playerShapes", savedShapes);
    }

    // ---- fixture + reflection helpers ------------------------------------

    /**
     * Writes {@code content} to a temp file and drives it through the reader.
     */
    private ParseResult<PlayerClass> load(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return new PlayerClassReader().parseWithResults(file.toString());
    }

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * True if any collected error contains {@code fragment}.
     */
    private static boolean hasError(ParseResult<PlayerClass> r, String fragment) {
        return r.errors().stream().anyMatch(s -> s.contains(fragment));
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = GameConstants.class.getDeclaredField(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(Object target, Class<?> owner, String name) throws Exception {
        Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    private static <T> T field(PlayerClass c, String name) throws Exception {
        return field(c, PlayerClass.class, name);
    }

    private static PlayerClass byName(List<PlayerClass> classes, String name) throws Exception {
        for (PlayerClass c : classes) {
            if (name.equals(field(c, "name"))) return c;
        }
        return null;
    }

    private static PlayerClass byName(String name) throws Exception {
        return byName(result.items(), name);
    }

    // =====================================================================
    // (1) HAPPY PATH
    // =====================================================================

    @Test
    void realFileLoadsAllNineClassesWithNoErrors() {
        // A clean load is the end-to-end proof: any unresolved realm, book base or shapechange
        // target would have appended a soft error rather than thrown.
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(9, result.items().size());
    }

    @Test
    void everyExpectedClassIsPresent() throws Exception {
        for (String name : List.of("Warrior", "Mage", "Druid", "Priest", "Necromancer",
                "Paladin", "Rogue", "Ranger", "Blackguard")) {
            assertNotNull(byName(name), () -> "missing class: " + name);
        }
    }

    @Test
    void warriorIsANonCasterWithItsScalarsAndFlags() throws Exception {
        PlayerClass warrior = byName("Warrior");
        assertNotNull(warrior);

        // Non-casters get the ClassMagic.NONE sentinel, not null.
        ClassMagic magic = field(warrior, "magic");
        assertSame(ClassMagic.NONE, magic);
        assertFalse(magic.isCaster());

        assertEquals(6, (int) field(warrior, "maxAttacks"));    // max-attacks:6
        assertEquals(30, (int) field(warrior, "minWeight"));    // min-weight:30
        assertEquals(5, (int) field(warrior, "attMultiplier")); // strength-multiplier:5

        List<String> titles = field(warrior, "titles");
        assertEquals(10, titles.size());
        assertEquals("Rookie", titles.getFirst());
        assertEquals("Lord", titles.getLast());

        Flag<PlayerFlag> pf = field(warrior, "pFlags");
        assertTrue(pf.has(PlayerFlag.PF_NO_MANA));       // player-flags:... NO_MANA ...
        assertTrue(pf.has(PlayerFlag.PF_SHIELD_BASH));
    }

    @Test
    void warriorStatsAndSkillsAreParsedIncludingNegatives() throws Exception {
        PlayerClass warrior = byName("Warrior");

        // stats:3:-2:-2:2:2  ->  STR INT WIS DEX CON
        Map<Stats, Integer> stats = field(warrior, "stats");
        assertEquals(3, (int) stats.get(Stats.STAT_STR));
        assertEquals(-2, (int) stats.get(Stats.STAT_INT));
        assertEquals(-2, (int) stats.get(Stats.STAT_WIS));
        assertEquals(2, (int) stats.get(Stats.STAT_DEX));
        assertEquals(2, (int) stats.get(Stats.STAT_CON));

        // skill-melee:70:45  -> base 70 into classSkills, increment 45 into extraSkills
        Map<PlayerSkill, Integer> base = field(warrior, "classSkills");
        Map<PlayerSkill, Integer> extra = field(warrior, "extraSkills");
        assertEquals(70, (int) base.get(PlayerSkill.SKILL_TO_HIT_MELEE));
        assertEquals(45, (int) extra.get(PlayerSkill.SKILL_TO_HIT_MELEE));
    }

    @Test
    void mageIsACasterWithAllFiveBooksAndTheirSpells() throws Exception {
        PlayerClass mage = byName("Mage");
        assertNotNull(mage);

        ClassMagic magic = field(mage, "magic");
        assertNotSame(ClassMagic.NONE, magic);
        assertTrue(magic.isCaster());

        // magic:1:300:5  -> firstSpellLevel 1, spellWeight 300, numBooks 5
        assertEquals(1, (int) field(magic, ClassMagic.class, "firstSpellLevel"));
        assertEquals(300, (int) field(magic, ClassMagic.class, "spellWeight"));
        assertEquals(5, (int) field(magic, ClassMagic.class, "numBooks"));

        List<MagicBook> books = field(magic, ClassMagic.class, "magicBooks");
        assertEquals(5, books.size());

        // [First Spells] declares 7 spells; totalSpells sums getNumOfSpells across the books.
        assertEquals(7, books.getFirst().getNumOfSpells());
        int expectedTotal = books.stream().mapToInt(MagicBook::getNumOfSpells).sum();
        assertEquals(expectedTotal, (int) field(magic, ClassMagic.class, "totalSpells"));
    }

    @Test
    void druidCastsAndItsShapechangeSpellsResolved() throws Exception {
        // The Druid's books contain Fox Form / Bear Form / Eagle's Flight etc. — each an
        // EF_SHAPECHANGE whose target must resolve against the seeded shape registry. Reaching a
        // caster here with the suite-wide no-error assertion intact proves those resolved.
        PlayerClass druid = byName("Druid");
        assertNotNull(druid);
        ClassMagic magic = field(druid, "magic");
        assertTrue(magic.isCaster());
        assertEquals(5, (int) field(magic, ClassMagic.class, "numBooks"));
    }

    @Test
    void aSyntheticMinimalCasterLoadsCleanlyFromScratch() throws Exception {
        // Build one caster from nothing but the mandatory pieces: name, magic line, one book, one
        // spell with a well-formed effect. Proves the whole caster path works on hand-built input,
        // not just the shipped file.
        ParseResult<PlayerClass> r = load("mini.txt", withHeader(1,
                "name:TestMage\n"
                        + "magic:1:100:1\n"
                        + "book:magic book:town:[Test Book]:1:arcane\n"
                        + "spell:Test Bolt:1:1:20:4\n"
                        + "effect:BOLT:MISSILE\n"
                        + "dice:1\n"));

        assertFalse(r.hasErrors(), () -> r.errors().toString());
        assertEquals(1, r.items().size());
        assertTrue(this.<ClassMagic>castMagic(r).isCaster());
    }

    private ClassMagic castMagic(ParseResult<PlayerClass> r) throws Exception {
        return field(r.items().getFirst(), "magic");
    }

    // =====================================================================
    // (2) EVERY REACHABLE SOFT-ERROR SITE  (one test per site)
    // =====================================================================

    // ---- PlayerClassAssembler: flag resolution ---------------------------

    @Test
    void unknownObjectFlagIsReportedAndTheClassDropped() throws IOException {
        ParseResult<PlayerClass> r = load("bad-oflag.txt",
                withHeader(1, "name:Weird\nobj-flags:NOTAFLAG\n"));

        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown obj-flag: NOTAFLAG"), r.errors()::toString);
    }

    @Test
    void unknownPlayerFlagIsReportedAndTheClassDropped() throws IOException {
        ParseResult<PlayerClass> r = load("bad-pflag.txt",
                withHeader(1, "name:Weird\nplayer-flags:NOTAFLAG\n"));

        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown player-flag: NOTAFLAG"), r.errors()::toString);
    }

    // ---- ClassEquipAssembler: item + option resolution -------------------

    @Test
    void invalidEquipTvalIsReportedButTheClassStillLoads() throws IOException {
        // Equipment failures are collected but do not drop the class (no continue in the assembler).
        ParseResult<PlayerClass> r = load("bad-tval.txt",
                withHeader(1, "name:Adventurer\nequip:notatval:Thing:1:1:none\ntitle:X\n"));

        assertEquals(1, r.items().size());
        assertTrue(hasError(r, "invalid TValue: notatval"), r.errors()::toString);
    }

    @Test
    void nonBirthEquipOptionIsRejected() throws IOException {
        // use_sound is an OP_INTERFACE option, not a birth option; only birth options are valid here.
        ParseResult<PlayerClass> r = load("nonbirth-opt.txt",
                withHeader(1, "name:Adventurer\nequip:food:Ration of Food:1:1:use_sound\n"));

        assertTrue(hasError(r, "Non-birth options are not supported: OPT_use_sound"),
                r.errors()::toString);
    }

    @Test
    void unknownEquipOptionIsRejected() throws IOException {
        ParseResult<PlayerClass> r = load("bad-opt.txt",
                withHeader(1, "name:Adventurer\nequip:food:Ration of Food:1:1:notanoption\n"));

        assertTrue(hasError(r, "Invalid birth option found: notanoption"), r.errors()::toString);
    }

    // ---- ClassSpellBookAssembler: base + realm resolution ----------------

    @Test
    void unknownBookObjectBaseIsReportedAndTheBookDropped() throws IOException {
        // "notabase" resolves to no tval, so the book is dropped ("unknown first parameter").
        ParseResult<PlayerClass> r = load("bad-base.txt", withHeader(1,
                "name:Caster\nmagic:1:100:1\n"
                        + "book:notabase:town:[B]:1:arcane\n"
                        + "spell:S:1:1:1:1\n"));

        // The error echoes the raw tval string ("TV_notabase") before fromName upper-cases it.
        assertTrue(hasError(r, "unknown first parameter: TV_notabase"), r.errors()::toString);
    }

    @Test
    void unknownBookRealmIsReportedAndTheBookDropped() throws IOException {
        ParseResult<PlayerClass> r = load("bad-realm.txt", withHeader(1,
                "name:Caster\nmagic:1:100:1\n"
                        + "book:magic book:town:[B]:1:notarealm\n"
                        + "spell:S:1:1:1:1\n"));

        assertTrue(hasError(r, "unknown realm: notarealm"), r.errors()::toString);
    }

    // ---- EffectAssembler (reached via a spell) ---------------------------

    @Test
    void unknownEffectTypeInASpellIsReported() throws IOException {
        ParseResult<PlayerClass> r = load("bad-effect.txt", withHeader(1,
                "name:Caster\nmagic:1:100:1\n"
                        + "book:magic book:town:[B]:1:arcane\n"
                        + "spell:S:1:1:1:1\n"
                        + "effect:NOTANEFFECT\n"));

        assertTrue(hasError(r, "invalid effect type: NOTANEFFECT"), r.errors()::toString);
    }

    @Test
    void unknownShapechangeTargetInASpellIsReported() throws IOException {
        // Exercises the EST_SHAPECHANGE registry lookup (C: shape_name_to_idx).
        ParseResult<PlayerClass> r = load("bad-shape.txt", withHeader(1,
                "name:Caster\nmagic:1:100:1\n"
                        + "book:magic book:town:[B]:1:arcane\n"
                        + "spell:S:1:1:1:1\n"
                        + "effect:SHAPECHANGE:notashape\n"));

        // The grammar upper-cases every effect sub-type token, so the target reaches the (case-
        // insensitive) shape lookup as NOTASHAPE — hence the upper-cased name in the message.
        assertTrue(hasError(r, "unknown player shape: NOTASHAPE"), r.errors()::toString);
    }

    @Test
    void invalidProjectionSubtypeInASpellIsReported() throws IOException {
        // BOLT's sub-type is a projection; NOTAPROJ is not one.
        ParseResult<PlayerClass> r = load("bad-proj.txt", withHeader(1,
                "name:Caster\nmagic:1:100:1\n"
                        + "book:magic book:town:[B]:1:arcane\n"
                        + "spell:S:1:1:1:1\n"
                        + "effect:BOLT:NOTAPROJ\n"));

        assertTrue(hasError(r, "invalid projection type: NOTAPROJ"), r.errors()::toString);
    }

    // ---- GrammarDriver: record-count + hard structural errors ------------

    @Test
    void recordCountMismatchIsReportedButTheValidClassStillLoads() throws IOException {
        ParseResult<PlayerClass> r = load("bad-count.txt",
                withHeader(5, "name:Solo\ntitle:X\n"));

        assertEquals(1, r.items().size());
        assertTrue(hasError(r, "record-count header declares 5"), r.errors()::toString);
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<PlayerClass> r = load("no-header.txt", "name:Solo\ntitle:X\n");

        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    @Test
    void aRecordWithNoNameLineIsAHardError() throws IOException {
        // A class record must open with name:; a leading directive has nothing to attach to.
        ParseResult<PlayerClass> r = load("no-name.txt", withHeader(1, "title:X\n"));

        assertTrue(r.items().isEmpty());
        assertTrue(r.hasErrors());
    }

    // =====================================================================
    // (3) TWO DIFFERENT ERRORS ON THE SAME PATH
    // =====================================================================

    @Test
    void twoBadEffectsInOneBlockAreBothReported() throws IOException {
        // EffectAssembler.assemble must not fail-fast: given two distinct bad effects in a single
        // spell's block it reports BOTH (an unknown effect type and a bad projection sub-type),
        // then fails the block as a whole. This pins the "keep going, collect every failure" contract.
        ParseResult<PlayerClass> r = load("two-effect-errors.txt", withHeader(1,
                "name:Caster\nmagic:1:100:1\n"
                        + "book:magic book:town:[B]:1:arcane\n"
                        + "spell:S:1:1:1:1\n"
                        + "effect:NOTANEFFECT\n"
                        + "effect:BOLT:NOTAPROJ\n"));

        assertTrue(hasError(r, "invalid effect type: NOTANEFFECT"), r.errors()::toString);
        assertTrue(hasError(r, "invalid projection type: NOTAPROJ"), r.errors()::toString);
    }

    @Test
    void twoBadEquipLinesInOneClassAreBothReported() throws IOException {
        // A second same-path demonstration on the equipment leg: an unresolvable tval and a
        // non-birth option on two equip lines of the same class are both collected.
        ParseResult<PlayerClass> r = load("two-equip-errors.txt", withHeader(1,
                "name:Adventurer\n"
                        + "equip:notatval:Thing:1:1:none\n"
                        + "equip:food:Ration of Food:1:1:use_sound\n"));

        assertTrue(hasError(r, "invalid TValue: notatval"), r.errors()::toString);
        assertTrue(hasError(r, "Non-birth options are not supported: OPT_use_sound"),
                r.errors()::toString);
    }
}
