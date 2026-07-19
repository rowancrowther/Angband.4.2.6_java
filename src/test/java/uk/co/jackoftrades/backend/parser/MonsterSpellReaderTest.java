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
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.MonsterSpellType;
import uk.co.jackoftrades.middle.monsters.Summon;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
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
 *   <li><b>The implicit level-0 block.</b> [C] {@code parse_mon_spell_name} ({@code mon-init.c:596})
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
 * <p><b>Observability note.</b> {@link MonsterSpellType} and {@code MonsterSpellLevel} currently
 * expose no accessors, so these tests can only assert on {@code items().size()} and
 * {@code errors()}. That is enough to pin every <em>accept/reject</em> decision above, but not
 * the values that result - see the {@code TODO} block at the foot of this class for the
 * assertions to add once getters exist.
 *
 * @author Rowan Crowther
 */
class MonsterSpellReaderTest {

    private static final String REAL_FILE = "lib/gamedata/monster_spell.txt";

    /**
     * The shipped file's record-count header, and the number of {@code name:} blocks in it.
     */
    private static final int REAL_FILE_RECORDS = 91;

    private static Object savedPains;
    private static Object savedBases;
    private static Object savedSummons;

    @TempDir
    Path tempDir;

    /**
     * Seed the globals the effect assembler reaches into. Two of the shipped spells carry a
     * {@code SUMMON} effect, which resolves through {@link GameConstants#lookupSummon}; loading
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
        Field f = GameConstants.class.getDeclaredField(field);
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
        ParseResult<MonsterSpellType> selfOnTeleport =
                load("tele-cross-1.txt", withHeader(1, entry("TELE_AWAY", "TELEPORT",
                        "effect:TELEPORT:SELF\n")));
        assertTrue(selfOnTeleport.items().isEmpty());
        assertTrue(selfOnTeleport.errors().stream()
                        .anyMatch(e -> e.contains("invalid teleport type: SELF")),
                () -> "SELF is not valid on TELEPORT: " + selfOnTeleport.errors());

        ParseResult<MonsterSpellType> awayOnTeleportTo =
                load("tele-cross-2.txt", withHeader(1, entry("TELE_SELF_TO", "TELEPORT",
                        "effect:TELEPORT_TO:AWAY\n")));
        assertTrue(awayOnTeleportTo.items().isEmpty());
        assertTrue(awayOnTeleportTo.errors().stream()
                        .anyMatch(e -> e.contains("invalid teleport to type: AWAY")),
                () -> "AWAY is not valid on TELEPORT_TO: " + awayOnTeleportTo.errors());
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
    void unknownTeleportSubtypeIsReportedAndDropsTheSpell() throws IOException {
        ParseResult<MonsterSpellType> result =
                load("tele-bad.txt", withHeader(1, entry("TELE_AWAY", "TELEPORT",
                        "effect:TELEPORT:SIDEWAYS\n")));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), "a bad effect fails the whole owning record");
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("teleport")),
                () -> result.errors().toString());
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

    /*
     * TODO - assertions blocked on domain accessors.
     *
     * MonsterSpellType (index, msgT, hit, effects, levels) and MonsterSpellLevel (power,
     * loreDesc, loreAttr, loreAttrResist, loreAttrImmune, message, blindMessage, missMessage,
     * saveMessage) currently expose no getters, so the tests above can only assert accept/reject.
     * Once accessors exist, these are the behaviours worth pinning - each one is a place where
     * the port could silently diverge from C:
     *
     *   1. omittedMsgtLineDoesNotDropTheRecord - assert the resulting msgT is the agreed default.
     *      [C] leaves the zeroed msgt, and list-message.h starts at MSG(GENERIC), so C's default
     *      is MSG_GENERIC; the port's MessageType inserts an extra MSG_NONE ahead of it. Whichever
     *      is chosen, BlowMethodAssembler must agree - it currently uses MSG_GENERIC.
     *   2. loreBeforeAnyPowerCutoffFormsTheImplicitLevelZero - assert exactly one level, with
     *      power == 0.
     *   3. aPowerCutoffOpensAFurtherLevelWithoutDisturbingLevelZero - assert two levels, powers
     *      0 then 40, and that the level-0 lore is unchanged by the second block.
     *   4. repeatedLoreAndMessageLinesDoNotBreakTheRecord - assert the lore reads
     *      "do something and then something else" and the vis message "{name} acts. Twice.".
     *   5. Colour resolution - assert loreAttr is the Orange ColourType, and that a level with no
     *      lore-color-* line falls to COLOUR_TYPE_DARK, matching C's zeroed lore_attr.
     *   6. teleportAwayAndTeleportToSelfBothResolve - reach the effect's EffectSubTypeWrapper and
     *      assert getTeleportMonsterMayCast() / getTeleportToMonsterMayCast() are true, and that
     *      the discriminator is EST_TELEPORT / EST_TELEPORT_TO respectively. This is the one that
     *      would have caught the teleportTo(...) copy-paste slip.
     *   7. teleportWithNoSubtypeIsAccepted - assert the flag is false rather than the wrapper
     *      being EST_NONE.
     *   8. hit: - assert the parsed value reaches the domain object (currently untested at all).
     */
}
