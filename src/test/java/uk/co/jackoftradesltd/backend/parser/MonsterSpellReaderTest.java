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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.monsters.MonsterSpellType;
import uk.co.jackoftradesltd.middle.monsters.Summon;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link MonsterSpellReader}: file text -&gt;
 * {@code MonsterSpellLexer}/{@code MonsterSpellGrammar} -&gt; {@code MonsterSpellParseRecord} -&gt;
 * {@code MonsterSpellAssembler} -&gt; {@link MonsterSpellType}.
 *
 * <p>{@code monster_spell.txt} is the most structurally awkward file in the suite, and the
 * tests concentrate on the four places where that structure has to reproduce a C behaviour
 * that no other grammar exercises:
 * <ul>
 *   <li><b>The implicit level-0 block.</b> [C] {@code parse_mon_spell_name} in {@code mon-init.c}
 *       allocates a {@code monster_spell_level} the moment a {@code name:} is seen, so the
 *       {@code lore:}/{@code message-*:} lines that appear <em>before</em> the first
 *       {@code power-cutoff:} belong to a level whose power is 0. Every one of the 91 shipped
 *       records relies on this.</li>
 *   <li><b>The optional {@code msgt:} directive.</b> 49 of the 91 records omit it entirely; in
 *       [C] they keep the zeroed {@code msgt} rather than failing.</li>
 *   <li><b>The teleport effect subtypes.</b> {@code monster_spell.txt} is the only data file in
 *       the game that supplies a subtype to {@code TELEPORT}/{@code TELEPORT_TO}, and
 *       [C] {@code effect_subtype} ({@code effects.c}) accepts exactly one string for each -
 *       {@code AWAY} for the former, {@code SELF} for the latter - and rejects everything else,
 *       including the other's string and {@code NONE}.</li>
 *   <li><b>Repeated free-text directives.</b> [C] {@code string_append}s successive
 *       {@code lore:}/{@code message-*:} lines within one level rather than overwriting.</li>
 * </ul>
 *
 * <p><b>Observability note.</b> {@link MonsterSpellType} and {@code MonsterSpellLevel} expose no
 * accessors, so the value assertions read their private fields reflectively through {@link #field}.
 * The tests added on 261017 do this to pin what the reader hands back - file order, and the hit,
 * message type, effect count and level powers of all 91 shipped records, worked out from the
 * upstream file rather than from the Java - and the places the grammar and assembler differ from
 * C (see {@code docs/grammar_issues.md} and {@code docs/Assembler_Issues.md}). The older tests
 * above them still assert accept/reject only.
 *
 * @author Rowan Crowther
 *
 * <p>Class MonsterSpellReaderTest coded before 261017, commented in full on 261017.
 */
class MonsterSpellReaderTest {

    private static final String REAL_FILE = "lib/gamedata/monster_spell.txt";

    /**
     * The shipped file's record-count header, and the number of {@code name:} blocks in it.
     */
    private static final int REAL_FILE_RECORDS = 91;

    /**
     * One row per {@code name:} block of the upstream {@code lib/gamedata/monster_spell.txt}, in
     * file order, worked out from that file and not from the Java: spell name, {@code msgt:} text
     * ("" if the record has none), {@code hit:}, the number of {@code effect:} lines, and the level
     * powers C's chain would hold. C allocates a level at {@code name:} time with power 0, and each
     * {@code power-cutoff:} appends another, so the list always starts with 0.
     */
    private static final String[][] UPSTREAM = {
            {"SHRIEK", "SHRIEK", "100", "2", "0"},
            {"WHIP", "", "100", "1", "0"},
            {"SPIT", "", "100", "1", "0"},
            {"SHOT", "", "50", "1", "0,25,50"},
            {"ARROW", "", "50", "1", "0,16,32,48"},
            {"BOLT", "", "50", "1", "0,25,50"},
            {"BR_ACID", "BR_ACID", "100", "1", "0"},
            {"BR_ELEC", "BR_ELEC", "100", "1", "0"},
            {"BR_FIRE", "BR_FIRE", "100", "1", "0"},
            {"BR_COLD", "BR_FROST", "100", "1", "0"},
            {"BR_POIS", "BR_GAS", "100", "1", "0"},
            {"BR_NETH", "BR_NETHER", "100", "1", "0"},
            {"BR_LIGHT", "BR_LIGHT", "100", "1", "0"},
            {"BR_DARK", "BR_DARK", "100", "1", "0"},
            {"BR_SOUN", "BR_SOUND", "100", "1", "0"},
            {"BR_CHAO", "BR_CHAOS", "100", "1", "0"},
            {"BR_DISE", "BR_DISEN", "100", "1", "0"},
            {"BR_NEXU", "BR_NEXUS", "100", "1", "0"},
            {"BR_TIME", "BR_TIME", "100", "1", "0"},
            {"BR_INER", "BR_INERTIA", "100", "1", "0"},
            {"BR_GRAV", "BR_GRAVITY", "100", "1", "0"},
            {"BR_SHAR", "BR_SHARDS", "100", "1", "0"},
            {"BR_PLAS", "BR_PLASMA", "100", "1", "0"},
            {"BR_WALL", "BR_FORCE", "100", "1", "0"},
            {"BR_MANA", "", "100", "1", "0"},
            {"BOULDER", "", "60", "1", "0"},
            {"WEAVE", "CREATE_TRAP", "100", "1", "0"},
            {"BA_ACID", "", "100", "1", "0"},
            {"BA_ELEC", "", "100", "1", "0"},
            {"BA_FIRE", "", "100", "1", "0,80"},
            {"BA_COLD", "", "100", "1", "0,80"},
            {"BA_POIS", "", "100", "1", "0,60"},
            {"BA_SHAR", "", "100", "1", "0,70"},
            {"BA_NETH", "", "100", "1", "0,80"},
            {"BA_WATE", "", "100", "1", "0"},
            {"BA_MANA", "", "100", "1", "0"},
            {"BA_HOLY", "", "100", "1", "0"},
            {"BA_DARK", "", "100", "1", "0,70"},
            {"BA_LIGHT", "", "100", "1", "0,70"},
            {"STORM", "", "100", "3", "0,80"},
            {"DRAIN_MANA", "", "100", "1", "0"},
            {"MIND_BLAST", "", "100", "2", "0"},
            {"BRAIN_SMASH", "", "100", "5", "0"},
            {"WOUND", "", "100", "2", "0,16,32,50,80"},
            {"BO_ACID", "", "100", "1", "0"},
            {"BO_ELEC", "", "100", "1", "0"},
            {"BO_FIRE", "", "100", "1", "0"},
            {"BO_COLD", "", "100", "1", "0"},
            {"BO_POIS", "", "100", "1", "0"},
            {"BO_NETH", "", "100", "1", "0"},
            {"BO_WATE", "", "100", "1", "0"},
            {"BO_MANA", "", "100", "1", "0"},
            {"BO_PLAS", "", "100", "1", "0"},
            {"BO_ICE", "", "100", "1", "0"},
            {"MISSILE", "", "100", "1", "0"},
            {"BE_ELEC", "", "100", "1", "0"},
            {"BE_NETH", "", "100", "1", "0,80"},
            {"SCARE", "CAST_FEAR", "100", "1", "0"},
            {"BLIND", "", "100", "1", "0"},
            {"CONF", "", "100", "1", "0"},
            {"SLOW", "", "100", "1", "0"},
            {"HOLD", "", "100", "1", "0"},
            {"HASTE", "", "100", "1", "0"},
            {"HEAL", "", "100", "1", "0"},
            {"HEAL_KIN", "", "100", "1", "0"},
            {"BLINK", "", "100", "1", "0"},
            {"TPORT", "", "100", "1", "0"},
            {"TELE_TO", "TELEPORT", "100", "1", "0"},
            {"TELE_SELF_TO", "TELEPORT", "100", "1", "0"},
            {"TELE_AWAY", "TELEPORT", "100", "1", "0"},
            {"TELE_LEVEL", "TPLEVEL", "100", "1", "0"},
            {"DARKNESS", "", "100", "2", "0"},
            {"TRAPS", "CREATE_TRAP", "100", "1", "0"},
            {"FORGET", "", "100", "1", "0"},
            {"SHAPECHANGE", "", "100", "1", "0"},
            {"S_KIN", "SUM_MONSTER", "100", "1", "0"},
            {"S_MONSTER", "SUM_MONSTER", "100", "1", "0"},
            {"S_MONSTERS", "SUM_MONSTER", "100", "1", "0"},
            {"S_ANIMAL", "SUM_ANIMAL", "100", "1", "0"},
            {"S_SPIDER", "SUM_SPIDER", "100", "1", "0"},
            {"S_HOUND", "SUM_HOUND", "100", "1", "0"},
            {"S_HYDRA", "SUM_HYDRA", "100", "1", "0"},
            {"S_AINU", "SUM_AINU", "100", "1", "0"},
            {"S_DEMON", "SUM_DEMON", "100", "1", "0"},
            {"S_UNDEAD", "SUM_UNDEAD", "100", "1", "0"},
            {"S_DRAGON", "SUM_DRAGON", "100", "1", "0"},
            {"S_HI_DEMON", "SUM_HI_DEMON", "100", "1", "0"},
            {"S_HI_UNDEAD", "SUM_HI_UNDEAD", "100", "1", "0"},
            {"S_HI_DRAGON", "SUM_HI_DRAGON", "100", "1", "0"},
            {"S_WRAITH", "SUM_WRAITH", "100", "1", "0"},
            {"S_UNIQUE", "SUM_UNIQUE", "100", "1", "0"},
    };

    private static Object savedPains;
    private static Object savedBases;
    private static Object savedSummons;

    @TempDir
    Path tempDir;

    /**
     * Seed the globals the effect assembler reaches into. Two of the shipped spells carry a
     * {@code SUMMON} effect, which resolves through {@link MonsterRegistry#lookupSummon}; loading
     * summons needs monster bases, which need monster pains. Cheaper than a full
     * {@code GameConstants.init()}, and the same seeding trick {@code CurseReaderTest} uses.
     */
    @BeforeAll
    static void seed() throws Exception {
        List<MonsterPain> pains = new PainReader()
                .parseWithResults("lib/gamedata/pain.txt").items();
        savedPains = setStatic("monsterPains", pains);

        List<MonsterBase> bases = new MonsterBaseReader()
                .parseWithResults("lib/gamedata/monster_base.txt").items();
        savedBases = setStatic("monsterBases", bases);

        List<Summon> summons = new SummonReader()
                .parseWithResults("lib/gamedata/summon.txt").items();
        savedSummons = setStatic("summons", summons);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
        setStatic("summons", savedSummons);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    // ---- fixture helpers -------------------------------------------------

    /**
     * Prefix a body with the {@code record-count:} header the {@code file} rule requires.
     */
    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * Render one minimal but complete spell block: a name, a hit chance, and the level-0
     * lore/message lines that every record needs in order to have a level at all.
     *
     * @param name  the RSF_ name, without its prefix
     * @param msgt  the message type without its MSG_ prefix, or {@code null} to omit the line
     * @param extra any additional directives to splice in before the lore block
     */
    private static String entry(String name, String msgt, String extra) {
        return "name:" + name + "\n"
                + (msgt == null ? "" : "msgt:" + msgt + "\n")
                + "hit:100\n"
                + (extra == null ? "" : extra)
                + "lore:do something\n"
                + "lore-color-base:Orange\n"
                + "message-vis:{name} does something.\n"
                + "message-invis:Something happens.\n";
    }

    private ParseResult<MonsterSpellType> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new MonsterSpellReader().parseWithResults(file.toString());
    }

    // ---- Happy path: the real shipped file -------------------------------

    @Test
    void cleanLoadOfTheRealFileYieldsAllNinetyOneSpells() throws IOException {
        ParseResult<MonsterSpellType> result = new MonsterSpellReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(REAL_FILE_RECORDS, result.items().size());
    }

    @Test
    void everySpellNameInTheRealFileResolvesToTheRsfEnum() throws IOException {
        // The assembler drops a record whose name fails MonsterSpell.valueOf("RSF_" + name), so a
        // full count with no errors is proof that all 91 names matched list-mon-spells.h.
        ParseResult<MonsterSpellType> result = new MonsterSpellReader().parseWithResults(REAL_FILE);

        assertEquals(REAL_FILE_RECORDS, result.items().size());
        assertTrue(result.errors().isEmpty());
    }

    // ---- The optional msgt: directive ------------------------------------

    @Test
    void omittedMsgtLineDoesNotDropTheRecord() throws IOException {
        // [C] never runs parse_mon_spell_message_type for an absent line, leaving the mem_zalloc'd
        // msgt. 49 of the 91 shipped records rely on this; treating "" as an invalid message type
        // silently discarded more than half the spell table.
        ParseResult<MonsterSpellType> result =
                load("no-msgt.txt", withHeader(1, entry("BLINK", null, null)));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size(), "a missing msgt: must not drop the record");
    }

    @Test
    void presentMsgtLineIsStillResolvedAndValidated() throws IOException {
        ParseResult<MonsterSpellType> good =
                load("good-msgt.txt", withHeader(1, entry("BLINK", "TELEPORT", null)));
        assertFalse(good.hasErrors(), () -> good.errors().toString());
        assertEquals(1, good.items().size());

        ParseResult<MonsterSpellType> bad =
                load("bad-msgt.txt", withHeader(1, entry("BLINK", "NOT_A_MESSAGE", null)));
        assertTrue(bad.items().isEmpty(), "an unrecognised msgt: still drops the record");
        assertTrue(bad.errors().stream().anyMatch(e -> e.contains("invalid message type")),
                () -> bad.errors().toString());
    }

    // ---- Record-level validation -----------------------------------------

    @Test
    void unknownSpellNameIsReportedAndDropped() throws IOException {
        ParseResult<MonsterSpellType> result =
                load("bad-name.txt", withHeader(1, entry("NOT_A_SPELL", "TELEPORT", null)));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid name")),
                () -> result.errors().toString());
    }

    @Test
    void recordCountHeaderMismatchIsReported() throws IOException {
        // The header is a port-ism rather than a C feature, validated softly by GrammarDriver.
        ParseResult<MonsterSpellType> result =
                load("bad-count.txt", withHeader(7, entry("BLINK", "TELEPORT", null)));

        assertTrue(result.hasErrors(), "a declared count that disagrees with the body must be flagged");
    }

    // ---- Effect subtypes: the teleport pair ------------------------------

    @Test
    void teleportAwayAndTeleportToSelfBothResolve() throws IOException {
        // The two shipped uses, and the only place in lib/gamedata where TELEPORT/TELEPORT_TO
        // are given a subtype at all.
        ParseResult<MonsterSpellType> away =
                load("tele-away.txt", withHeader(1, entry("TELE_AWAY", "TELEPORT",
                        "effect:TELEPORT:AWAY\ndice:100\n")));
        assertFalse(away.hasErrors(), () -> away.errors().toString());
        assertEquals(1, away.items().size());

        ParseResult<MonsterSpellType> self =
                load("tele-self.txt", withHeader(1, entry("TELE_SELF_TO", "TELEPORT",
                        "effect:TELEPORT_TO:SELF\n")));
        assertFalse(self.hasErrors(), () -> self.errors().toString());
        assertEquals(1, self.items().size());
    }

    @Test
    void teleportSubtypesAreNotInterchangeable() throws IOException {
        // [C] effect_subtype tests one string per effect and falls through to its -1 error return
        // for anything else, so SELF on TELEPORT and AWAY on TELEPORT_TO are both invalid. An
        // enum-based lookup accepted either on either, which is why this is worth pinning.
        // Each -1 from effect_subtype is an error from grab_effect_data, and C quits on it, so the
        // load stops with an exception naming the effect's own line.
        String selfFile = withHeader(1, entry("TELE_AWAY", "TELEPORT", "effect:TELEPORT:SELF\n"));
        DataFileParseException selfOnTeleport = assertThrows(DataFileParseException.class,
                () -> load("tele-cross-1.txt", selfFile), "SELF is not valid on TELEPORT");
        assertTrue(selfOnTeleport.getMessage().contains("Effect starting at line: "
                + lineOf(selfFile, "effect:TELEPORT:SELF")), selfOnTeleport::getMessage);
        assertTrue(selfOnTeleport.getMessage().contains("illegal teleport value: SELF"),
                selfOnTeleport::getMessage);

        String awayFile = withHeader(1, entry("TELE_SELF_TO", "TELEPORT", "effect:TELEPORT_TO:AWAY\n"));
        DataFileParseException awayOnTeleportTo = assertThrows(DataFileParseException.class,
                () -> load("tele-cross-2.txt", awayFile), "AWAY is not valid on TELEPORT_TO");
        assertTrue(awayOnTeleportTo.getMessage().contains("Effect starting at line: "
                + lineOf(awayFile, "effect:TELEPORT_TO:AWAY")), awayOnTeleportTo::getMessage);
        assertTrue(awayOnTeleportTo.getMessage().contains("illegal teleport to value: AWAY"),
                awayOnTeleportTo::getMessage);
    }

    @Test
    void teleportWithNoSubtypeIsAccepted() throws IOException {
        // A missing subtype is not an error: [C] leaves effect->subtype at 0, i.e. the flag off.
        // The port has to route this through the subtype switch anyway so that the false-valued
        // wrapper is built rather than a bare EST_NONE.
        ParseResult<MonsterSpellType> result =
                load("tele-bare.txt", withHeader(1, entry("BLINK", "TELEPORT",
                        "effect:TELEPORT\ndice:10\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    @Test
    void unknownTeleportSubtypeStopsTheLoad() {
        // effect_subtype returns -1 for SIDEWAYS, grab_effect_data reports it, and C quits
        String file = withHeader(1, entry("TELE_AWAY", "TELEPORT", "effect:TELEPORT:SIDEWAYS\n"));
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("tele-bad.txt", file));

        assertTrue(e.getMessage().contains("Effect starting at line: "
                + lineOf(file, "effect:TELEPORT:SIDEWAYS")), e::getMessage);
        assertTrue(e.getMessage().contains("illegal teleport value: SIDEWAYS"), e::getMessage);
    }


    /**
     * @param text   a whole data file
     * @param needle a line (or the start of one) that occurs in it
     * @return the 1-based number of the first line of {@code text} that starts with {@code needle}
     */
    private static int lineOf(String text, String needle) {
        String[] lines = text.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith(needle)) return i + 1;
        }
        throw new AssertionError("no line starting with " + needle);
    }

    // ---- Structure: levels and repeated free text ------------------------

    @Test
    void loreBeforeAnyPowerCutoffFormsTheImplicitLevelZero() throws IOException {
        // No power-cutoff: line at all - the record still has to produce one level, because [C]
        // allocates it at name: time. If the grammar required a power-cutoff to open a level
        // block, this record would fail to parse.
        ParseResult<MonsterSpellType> result =
                load("level-zero.txt", withHeader(1, entry("BLINK", "TELEPORT", null)));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    @Test
    void aPowerCutoffOpensAFurtherLevelWithoutDisturbingLevelZero() throws IOException {
        String body = entry("BLINK", "TELEPORT", null)
                + "power-cutoff:40\n"
                + "lore:do something impressive\n"
                + "lore-color-base:Light Red\n"
                + "message-vis:{name} does something impressive.\n";
        ParseResult<MonsterSpellType> result = load("two-levels.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    @Test
    void repeatedLoreAndMessageLinesDoNotBreakTheRecord() throws IOException {
        // [C] string_appends each successive line rather than overwriting. Without accessors the
        // best this can prove is that the grammar's (...)+ loop accepts the repetition; the
        // concatenation itself is asserted in the TODO block below.
        String body = "name:BLINK\nmsgt:TELEPORT\nhit:100\n"
                + "lore:do something\nlore: and then something else\n"
                + "lore-color-base:Orange\n"
                + "message-vis:{name} acts.\nmessage-vis: Twice.\n"
                + "message-invis:Something happens.\n";
        ParseResult<MonsterSpellType> result = load("repeated.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    @Test
    void multipleSpellsInOneFileAreAllKept() throws IOException {
        String body = entry("BLINK", "TELEPORT", null)
                + entry("TPORT", "TELEPORT", null)
                + entry("DARKNESS", null, null);
        ParseResult<MonsterSpellType> result = load("three.txt", withHeader(3, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(3, result.items().size());
    }

    // ---- Reflection helpers -----------------------------------------------

    /**
     * Read a private field of a domain object, which has no getters.
     */
    private static Object field(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> levelsOf(MonsterSpellType spell) throws Exception {
        return (List<Object>) field(spell, "levels");
    }

    @SuppressWarnings("unchecked")
    private static List<Object> effectsOf(MonsterSpellType spell) throws Exception {
        return (List<Object>) field(spell, "effects");
    }

    private static List<Integer> powersOf(MonsterSpellType spell) throws Exception {
        List<Integer> powers = new ArrayList<>();
        for (Object level : levelsOf(spell)) {
            powers.add((Integer) field(level, "power"));
        }
        return powers;
    }

    // ---- The reader's contract: values, order and the two error channels --

    @Test
    void realFileComesBackInFileOrder() throws IOException {
        // C's mon_spell_parser pushes each record on the head of a chain, so monster_spells runs
        // last record first. Nothing observable depends on it for the shipped data, because
        // monster_spell_by_index() looks a spell up by index and every name appears once. The Java
        // list is in file order, which is what this pins.
        ParseResult<MonsterSpellType> result = new MonsterSpellReader().parseWithResults(REAL_FILE);

        assertEquals(UPSTREAM.length, result.items().size());
        for (int i = 0; i < UPSTREAM.length; i++) {
            Object index = null;
            try {
                index = field(result.items().get(i), "index");
            } catch (Exception e) {
                fail(e);
            }
            assertEquals("RSF_" + UPSTREAM[i][0], index.toString(), "record " + i);
        }
    }

    @Test
    void everyRecordOfTheRealFileAgreesWithTheUpstreamFile() throws Exception {
        ParseResult<MonsterSpellType> result = new MonsterSpellReader().parseWithResults(REAL_FILE);
        assertEquals(UPSTREAM.length, result.items().size());

        for (int i = 0; i < UPSTREAM.length; i++) {
            String[] row = UPSTREAM[i];
            MonsterSpellType spell = result.items().get(i);

            // A record with no msgt: keeps C's zeroed msgt, which is MSG_GENERIC (index 0 of
            // list-message.h).
            String expectedMsgT = "MSG_" + (row[1].isEmpty() ? "GENERIC" : row[1]);
            assertEquals(expectedMsgT, field(spell, "msgT").toString(), row[0] + " msgT");
            assertEquals(Integer.parseInt(row[2]), field(spell, "hit"), row[0] + " hit");
            assertEquals(Integer.parseInt(row[3]), effectsOf(spell).size(), row[0] + " effects");

            List<Integer> expectedPowers = new ArrayList<>();
            for (String p : row[4].split(",")) {
                expectedPowers.add(Integer.parseInt(p));
            }
            assertEquals(expectedPowers, powersOf(spell), row[0] + " level powers");
        }
    }

    @Test
    void parseReturnsTheItemsAndDropsTheErrors() throws IOException {
        // The Reader contract: parse() is parseWithResults().items(). A record skipped for a bad
        // name goes missing without a trace, which is why callers that care use parseWithResults.
        String body = entry("BLINK", "TELEPORT", null) + entry("NOT_A_SPELL", null, null);
        Path file = tempDir.resolve("parse-only.txt");
        Files.writeString(file, withHeader(2, body));

        List<MonsterSpellType> items = new MonsterSpellReader().parse(file.toString());

        assertEquals(1, items.size());
    }

    @Test
    void missingFileIsAnIoExceptionWhereCQuits() {
        // C's run_parse_mon_spell calls parse_file_quit_not_found, which quit()s. Here the caller
        // gets the exception and decides.
        assertThrows(IOException.class, () -> new MonsterSpellReader()
                .parseWithResults(tempDir.resolve("no-such-file.txt").toString()));
    }

    @Test
    void syntaxErrorIsAHardErrorThatLoadsNothing() throws IOException {
        // No record-count: header, so the file rule cannot start. C has no header at all, so this
        // is a Java-only way to fail, but it shows the hard channel: no items, the message kept.
        ParseResult<MonsterSpellType> result =
                load("no-header.txt", entry("BLINK", "TELEPORT", null));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aDirectiveBeforeAnyNameIsAHardError() throws IOException {
        // C: every parse_mon_spell_* handler returns PARSE_ERROR_MISSING_RECORD_HEADER when no
        // name: has been seen. Java agrees, by way of the file rule starting a record at name:.
        ParseResult<MonsterSpellType> result = load("no-name.txt",
                withHeader(1, "hit:100\n" + entry("BLINK", "TELEPORT", null)));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aNonNumericHitIsAHardError() throws IOException {
        // C: parser_getuint fails on text with PARSE_ERROR_NOT_NUMBER and the load stops.
        ParseResult<MonsterSpellType> result = load("hit-text.txt", withHeader(1,
                "name:BLINK\nmsgt:TELEPORT\nhit:lots\nlore:x\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void anUnknownSpellNameCostsOnlyItsOwnRecord() throws IOException {
        // C stops the whole load at the first bad record (PARSE_ERROR_INVALID_SPELL_NAME). Here
        // the assembler reports it and the neighbours still load.
        String body = entry("BLINK", "TELEPORT", null)
                + entry("NOT_A_SPELL", null, null)
                + entry("TPORT", "TELEPORT", null);
        ParseResult<MonsterSpellType> result = load("one-bad.txt", withHeader(3, body));

        assertEquals(2, result.items().size());
        assertEquals(1, result.errors().size(), () -> result.errors().toString());
    }

    @Test
    void aWrongRecordCountIsASoftErrorAndTheRecordsStillLoad() throws IOException {
        ParseResult<MonsterSpellType> result =
                load("count-keeps.txt", withHeader(7, entry("BLINK", "TELEPORT", null)));

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("record-count")),
                () -> result.errors().toString());
    }

    @Test
    void duplicateSpellNamesAreBothKeptInFileOrder() throws Exception {
        // C holds both too, but its chain runs last-first, so monster_spell_by_index() finds the
        // later of the two. The Java list is in file order, so a lookup written over it has to take
        // the last match to agree; see docs/Assembler_Issues.md.
        String first = "name:BLINK\nmsgt:TELEPORT\nhit:10\nlore:first\n";
        String second = "name:BLINK\nmsgt:TELEPORT\nhit:20\nlore:second\n";
        ParseResult<MonsterSpellType> result = load("dupes.txt", withHeader(2, first + second));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals(10, field(result.items().get(0), "hit"));
        assertEquals(20, field(result.items().get(1), "hit"));
    }

    @Test
    void repeatedLoreAndMessageLinesAreConcatenatedWithoutASeparator() throws Exception {
        // C: string_append() copies the new text straight onto the end, so a leading space has to
        // be in the data file. The text after the colon is kept exactly, leading space included.
        String body = "name:BLINK\nmsgt:TELEPORT\nhit:100\n"
                + "lore:do something\nlore: and then something else\n"
                + "message-vis:{name} acts.\nmessage-vis: Twice.\n";
        ParseResult<MonsterSpellType> result = load("append.txt", withHeader(1, body));
        assertFalse(result.hasErrors(), () -> result.errors().toString());

        Object level = levelsOf(result.items().get(0)).get(0);
        assertEquals("do something and then something else", field(level, "loreDesc"));
        assertEquals("{name} acts. Twice.", field(level, "message"));
    }

    @Test
    void directivesMayComeInAnyOrderAfterTheName() throws Exception {
        // C: every directive after name: just fills the last record, in any order. The lore and
        // message lines are kept together here; apart, they split the level (see below).
        String body = "name:BLINK\nlore:text\nmessage-vis:seen\nhit:60\nmsgt:TELEPORT\n"
                + "effect:TELEPORT:AWAY\ndice:100\n";
        ParseResult<MonsterSpellType> result = load("any-order.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        MonsterSpellType spell = result.items().get(0);
        assertEquals(60, field(spell, "hit"));
        assertEquals("MSG_TELEPORT", field(spell, "msgT").toString());
        assertEquals(1, effectsOf(spell).size());
        assertEquals("seen", field(levelsOf(spell).get(0), "message"));
    }

    // ---- Where the grammar differs from C (docs/grammar_issues.md) --------

    @Test
    void aRecordWithOnlyANameIsASyntaxErrorHereWhereCAcceptsIt() throws IOException {
        // C: parse_mon_spell_name needs nothing after it. The grammar's (...)+ wants one directive.
        ParseResult<MonsterSpellType> result =
                load("name-only.txt", withHeader(1, "name:BLINK\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void loreAfterAnEffectOpensASecondLevelHereWhereCAppendsToTheFirst() throws Exception {
        // C: every lore:/message-*: line goes onto the last level in the chain, whatever came
        // between. The grammar closes the level block at the effect, so the later lore starts a new
        // block, which the level assembler gives power 0 (a second level at the same power).
        String body = "name:BLINK\nmsgt:TELEPORT\nhit:100\nlore:one\n"
                + "effect:TELEPORT:AWAY\ndice:100\nlore: two\n";
        ParseResult<MonsterSpellType> result = load("lore-split.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        List<Object> levels = levelsOf(result.items().get(0));
        assertEquals(2, levels.size(), "C would hold one level with lore \"one two\"");
        assertEquals("one", field(levels.get(0), "loreDesc"));
        assertEquals(" two", field(levels.get(1), "loreDesc"));
    }

    @Test
    void aHitOrMsgtLineBetweenTwoLoreLinesAlsoSplitsTheLevel() throws Exception {
        // The same split as above, caused by a plain directive instead of an effect: any
        // non-text directive closes the grammar's level block.
        String body = "name:BLINK\nlore:one\nhit:100\nlore: two\n";
        ParseResult<MonsterSpellType> result = load("lore-split-hit.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, levelsOf(result.items().get(0)).size(), "C would hold one level");
    }

    @Test
    void twoPowerCutoffsInARowAreASyntaxErrorHereWhereCAcceptsAnEmptyLevel() throws IOException {
        String body = entry("BLINK", "TELEPORT", null)
                + "power-cutoff:10\npower-cutoff:20\nlore:x\n";
        ParseResult<MonsterSpellType> result = load("empty-level.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void effectYxAndDiceOnTheSameEffectAreASyntaxErrorHereWhereCTakesBoth() throws IOException {
        // C: effect-yx and dice both attach to the last effect, in either order.
        ParseResult<MonsterSpellType> result = load("yx-dice.txt", withHeader(1,
                entry("BLINK", "TELEPORT", "effect:TELEPORT:AWAY\neffect-yx:1:2\ndice:100\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aDiceLineBeforeAnyEffectIsASyntaxErrorHereWhereCIgnoresIt() throws IOException {
        // C: parse_mon_spell_dice returns PARSE_ERROR_NONE when the record has no effect yet,
        // "assume that this is human and not parser error".
        ParseResult<MonsterSpellType> result = load("orphan-dice.txt", withHeader(1,
                entry("BLINK", "TELEPORT", "dice:100\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void effectMsgIsAcceptedHereWhereCHasNoSuchDirective() throws IOException {
        // C's mon_spell_parser registers no effect-msg: line, so it is PARSE_ERROR_UNDEFINED_DIRECTIVE.
        // The shared effect block takes it.
        ParseResult<MonsterSpellType> result = load("effect-msg.txt", withHeader(1,
                entry("BLINK", "TELEPORT", "effect:TELEPORT:AWAY\ndice:100\neffect-msg:Hello\n")));

        assertEquals(1, result.items().size(), () -> result.errors().toString());
    }

    @Test
    void aNegativeHitIsAcceptedHereWhereCRejectsIt() throws Exception {
        // C: hit is "uint", and parser.c rejects a token starting with '-' (PARSE_ERROR_NOT_NUMBER).
        String body = "name:BLINK\nmsgt:TELEPORT\nhit:-5\nlore:x\n";
        ParseResult<MonsterSpellType> result = load("neg-hit.txt", withHeader(1, body));

        assertEquals(1, result.items().size(), () -> result.errors().toString());
        assertEquals(-5, field(result.items().get(0), "hit"));
    }

    // ---- Where the assembler differs from C (docs/Assembler_Issues.md) ----

    @Test
    void anAbsentHitDropsTheRecordHereWhereCLeavesItZero() throws IOException {
        // C: mem_zalloc leaves hit at 0 when there is no hit: line.
        String body = "name:BLINK\nmsgt:TELEPORT\nlore:x\n";
        ParseResult<MonsterSpellType> result = load("no-hit.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("hit")),
                () -> result.errors().toString());
    }

    @Test
    void anUnknownLoreColourDropsTheLevelHereWhereCReadsWhite() throws Exception {
        // C: color_text_to_attr() and color_char_to_attr() fall back to white, so no colour is ever
        // invalid. Here the level assembler reports it and skips the level, but still returns a
        // list, so the spell loads with a level missing - the opposite of that class's own
        // all-or-nothing Javadoc.
        String body = "name:BLINK\nmsgt:TELEPORT\nhit:100\nlore:x\nlore-color-base:Nonsense\n";
        ParseResult<MonsterSpellType> result = load("bad-colour.txt", withHeader(1, body));

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid base colour")),
                () -> result.errors().toString());
        assertEquals(0, levelsOf(result.items().get(0)).size());
    }

    @Test
    void msgtIsExactCaseAndNameOnlyHereWhereCIsCaseBlindWithANumericForm() throws IOException {
        // C: message_lookup_by_name() uses my_stricmp and accepts a number below MSG_MAX.
        for (String msgt : new String[]{"teleport", "6"}) {
            ParseResult<MonsterSpellType> result = load("msgt-" + msgt + ".txt",
                    withHeader(1, "name:BLINK\nmsgt:" + msgt + "\nhit:100\nlore:x\n"));
            assertTrue(result.items().isEmpty(), "msgt:" + msgt);
            assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid message type")),
                    () -> result.errors().toString());
        }
    }

    @Test
    void msgtNoneIsAcceptedHereWhereCHasNoSuchMessage() throws Exception {
        // MSG_NONE is a Java-only constant (an accepted divergence), but it makes msgt:NONE load.
        ParseResult<MonsterSpellType> result = load("msgt-none.txt",
                withHeader(1, "name:BLINK\nmsgt:NONE\nhit:100\nlore:x\n"));

        assertEquals(1, result.items().size(), () -> result.errors().toString());
        assertEquals("MSG_NONE", field(result.items().get(0), "msgT").toString());
    }

    @Test
    void aLeadingPowerCutoffLeavesNoImplicitLevelZeroHereWhereCKeepsIt() throws Exception {
        // C: the level made at name: time (power 0, no text) is always first, so a record that
        // opens with power-cutoff:20 has levels [0, 20]. Here the block that carries the cutoff is
        // the first level. No shipped record starts this way.
        String body = "name:BLINK\nmsgt:TELEPORT\nhit:100\npower-cutoff:20\nlore:x\n";
        ParseResult<MonsterSpellType> result = load("lead-cutoff.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of(20), powersOf(result.items().get(0)),
                "C would hold [0, 20]");
    }

    @Test
    void aRecordWithNoTextHasNoLevelHereWhereCAlwaysHasOne() throws Exception {
        // C: spell->level is allocated at name: time, and mon-spell.c dereferences it without a
        // check. Every shipped record has lore, so this cannot happen with the real file.
        String body = "name:BLINK\nmsgt:TELEPORT\nhit:100\neffect:TELEPORT:AWAY\ndice:100\n";
        ParseResult<MonsterSpellType> result = load("no-text.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(0, levelsOf(result.items().get(0)).size(), "C would hold one empty level");
    }

    /*
     * Still blocked on domain accessors - or on the colour model:
     *
     *   1. Colour resolution - assert loreAttr is the Orange colour, and what a level with no
     *      lore-color-* line holds. C's zeroed lore_attr is COLOUR_DARK; the port leaves null.
     *   2. teleportAwayAndTeleportToSelfBothResolve - reach the effect's EffectSubTypeWrapper and
     *      assert getTeleportMonsterMayCast() / getTeleportToMonsterMayCast() are true, and that
     *      the discriminator is EST_TELEPORT / EST_TELEPORT_TO respectively. This is the one that
     *      would have caught the teleportTo(...) copy-paste slip.
     *   3. teleportWithNoSubtypeIsAccepted - assert the flag is false rather than the wrapper
     *      being EST_NONE.
     */
}
