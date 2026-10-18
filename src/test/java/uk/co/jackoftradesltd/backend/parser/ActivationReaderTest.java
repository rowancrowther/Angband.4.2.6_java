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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.Activation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link ActivationReader} (grammar-suite reader-track R4).
 *
 * <p>The happy-path tests run against the real {@code lib/gamedata/activation.txt}; the
 * error-path tests write small malformed files to a temp directory. They exercise the two
 * outcomes of the collect-and-report design end to end:
 * <ul>
 *   <li>a clean file yields all items and no errors;</li>
 *   <li>a parse error (checkpoint 1) or a data error (checkpoint 2) yields <em>no</em> items
 *       and a populated error list carrying the source line.</li>
 * </ul>
 *
 * <p>{@link Activation} exposes only {@code getName()} and {@code getPower()}, so the other
 * fields ({@code aim}, {@code level}, {@code message}, {@code desc} and the effect list) are read by
 * reflection, as {@code ActivationIndexTest} reads {@code index}.
 *
 * <p>Record counts are no longer asserted against a hard-coded sentinel: under C12 the
 * data file declares its own {@code record-count}, and the reader treats a declared-vs-actual
 * mismatch as a soft error. A clean load therefore <em>is</em> the count assertion.
 *
 * <p>The second half pins the reader's own contract against {@code act_parser} in
 * {@code obj-init.c}. The totals and spot-checked records are worked out from the upstream
 * {@code activation.txt} (163 records, 57 of them aimed, level sum 5173, power sum 1758, 312 effects,
 * 48 messages, 116 records with a description); the list is in C's last-first order, which
 * {@code finish_parse_act} produces by filling {@code activations} from the head of a chain that
 * {@code parse_act_name} builds by prepending; the directives may come in any order and a repeated
 * {@code aim:}, {@code level:} or {@code power:} overwrites. The last group pins each place the
 * grammar or assembler differs from C, each named for the C behaviour it departs from, so that a fix
 * shows up here as a test to flip. The differences are written up in {@code docs/grammar_issues.md}
 * and {@code docs/Assembler_Issues.md}.
 *
 * <p>Class ActivationReaderTest coded before 261017, extended and commented in full on 261017.
 *
 * @author Rowan Crowther
 */
class ActivationReaderTest {

    /**
     * The real shipped data file, relative to the Gradle working directory (project root).
     */
    private static final String REAL_FILE = "lib/gamedata/activation.txt";

    @TempDir
    Path tempDir;

    /**
     * Writes {@code content} to a file in the temp dir and returns its absolute path.
     */
    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    @Test
    void parseWithResultLoadsAllRecordsCleanly() throws IOException {
        ParseResult<Activation> result = new ActivationReader().parseWithResult(REAL_FILE);

        // No errors implies the file's declared record-count matched the records found.
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertFalse(result.items().isEmpty());
        // C's activations[] runs in reverse file order, so the last record, DRAGON_POWER, is first
        assertEquals("DRAGON_POWER", result.items().get(0).getName());
    }

    @Test
    void parseShimReturnsTheItems() throws IOException {
        List<Activation> activations = new ActivationReader().parse(REAL_FILE);

        assertFalse(activations.isEmpty());
        // C's activations[] runs in reverse file order, so the last record, DRAGON_POWER, is first
        assertEquals("DRAGON_POWER", activations.get(0).getName());
    }

    @Test
    void unresolvableEffectDropsTheWholeActivation() throws IOException {
        // NOTAREALEFFECT is not a known effect type. Under the suite-wide effect-drop policy,
        // a record whose effect fails to resolve is discarded in full: EffectAssembler returns
        // null and ActivationAssembler skips the activation, so no items load and a single error
        // is reported. The record-count header is line 1, so the effect: directive is line 6, and
        // EffectAssembler tags the error against the effect's own line.
        String path = tempFile("bad-data.txt",
                "record-count:1\nname:TEST_BAD\naim:0\nlevel:5\npower:1\neffect:NOTAREALEFFECT\ndesc:bad activation\n");

        ParseResult<Activation> result = new ActivationReader().parseWithResult(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertEquals(1, result.errors().size());
        String error = result.errors().get(0);
        assertTrue(error.contains("invalid effect type"), error);
        assertTrue(error.contains("NOTAREALEFFECT"), error);
        assertTrue(error.contains("line: 6"), error);
    }

    @Test
    void parseErrorYieldsNoItemsAndAReportedError() throws IOException {
        // Header is present, but a record must begin with "name:"; this one starts
        // with "power:" — a syntax error (checkpoint 1).
        String path = tempFile("bad-syntax.txt", "record-count:1\npower:5\n");

        ParseResult<Activation> result = new ActivationReader().parseWithResult(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void oversizedLevelIsReportedAsMalformedThroughTheFullPipeline() throws IOException {
        // The lexer's INTEGER token accepts an arbitrarily long run of digits, so a value too
        // large for a Java int lexes and parses cleanly and only fails when the assembler
        // converts the raw string. This proves the assembler's malformed-number branch is
        // reachable end to end, not just by hand-built records. The record-count matches (one
        // record parsed), so the only error is the failed conversion, and the bad record is
        // dropped, leaving no items.
        String path = tempFile("big-level.txt",
                "record-count:1\nname:TEST_BIG\naim:0\nlevel:99999999999999\npower:1\ndesc:huge\n");

        ParseResult<Activation> result = new ActivationReader().parseWithResult(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().get(0).contains("malformed level"), result.errors().toString());
    }

    @Test
    void recordCountMismatchIsReportedButRecordsStillLoad() throws IOException {
        // The header declares 5 records but the file holds only 1. Under GrammarDriver's
        // partial-results contract this is a soft error: the mismatch is reported, but the
        // records that did parse still load. (Contrast the old hard-error behaviour, where a
        // mismatch discarded the whole file.) The single record here parses and assembles
        // cleanly, so it survives alongside the reported count error.
        String path = tempFile("bad-count.txt",
                "record-count:5\nname:TEST_OK\naim:0\nlevel:5\npower:1\neffect:CURE:POISONED\ndesc:fine\n");

        ParseResult<Activation> result = new ActivationReader().parseWithResult(path);

        assertTrue(result.hasErrors());
        assertEquals(1, result.items().size());
        assertEquals("TEST_OK", result.items().get(0).getName());
        String error = result.errors().get(0);
        assertTrue(error.contains("declares 5"), error);
        assertTrue(error.contains("contains 1"), error);
    }

    // ---- fixture helpers -------------------------------------------------

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * A complete, valid single record named {@code T}, with {@code extra} appended after its lines.
     * The {@code CURE:POISONED} effect resolves without any registry being seeded.
     */
    private static String record(String extra) {
        return "name:T\naim:0\nlevel:1\npower:1\neffect:CURE:POISONED\n" + extra;
    }

    private ParseResult<Activation> load(String name, String content) throws IOException {
        return new ActivationReader().parseWithResult(tempFile(name, content));
    }

    /**
     * Loads {@code content} as a file of {@code count} records and returns its only activation,
     * failing if there were errors or the count of items is not 1.
     */
    private Activation loadOne(String content) throws IOException {
        ParseResult<Activation> result = load("one.txt", withHeader(1, content));
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        return result.items().get(0);
    }

    /**
     * Asserts the load failed closed: a hard error, so no items at all.
     */
    private void assertHardError(String content) throws IOException {
        ParseResult<Activation> result = load("hard.txt", content);
        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), () -> result.items().size() + " items loaded");
    }

    private static Object field(Activation a, String name) throws Exception {
        Field f = Activation.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(a);
    }

    private static boolean aim(Activation a) throws Exception {
        return (boolean) field(a, "aim");
    }

    private static int level(Activation a) throws Exception {
        return (int) field(a, "level");
    }

    private static String message(Activation a) throws Exception {
        return (String) field(a, "message");
    }

    private static String desc(Activation a) throws Exception {
        return (String) field(a, "desc");
    }

    private static int effectCount(Activation a) throws Exception {
        return ((List<?>) field(a, "effects")).size();
    }

    private static Activation named(List<Activation> all, String name) {
        return all.stream().filter(a -> a.getName().equals(name)).findFirst().orElseThrow();
    }

    /**
     * The {@code name:} values of {@link #REAL_FILE} in file order, read line by line with no
     * parser involved.
     */
    private static List<String> namesInTheFile() throws IOException {
        List<String> names = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.startsWith("name:")) {
                names.add(line.substring("name:".length()));
            }
        }
        return names;
    }

    // ---- The shipped file against the upstream file -----------------------

    @Test
    void theShippedFileLoadsEveryRecordInCsLastFirstOrder() throws IOException {
        List<Activation> items = new ActivationReader().parseWithResult(REAL_FILE).items();

        List<String> expected = namesInTheFile().reversed();
        assertEquals(163, expected.size());
        assertEquals(expected, items.stream().map(Activation::getName).toList());
        assertEquals("DRAGON_POWER", items.getFirst().getName());
        assertEquals("CURE_POISON", items.getLast().getName());
    }

    @Test
    void theShippedFileTotalsAgreeWithTheUpstreamFile() throws Exception {
        List<Activation> items = new ActivationReader().parseWithResult(REAL_FILE).items();

        int aimed = 0, levels = 0, powers = 0, effects = 0, messages = 0, descriptions = 0;
        for (Activation a : items) {
            if (aim(a)) aimed++;
            levels += level(a);
            powers += a.getPower();
            effects += effectCount(a);
            if (!message(a).isEmpty()) messages++;
            if (!desc(a).isEmpty()) descriptions++;
        }

        // Worked out from the upstream activation.txt: 57 aim:1, level and power sums, 312 effect:
        // lines, 48 msg: lines (one per record), 118 desc: lines on 116 records.
        assertEquals(57, aimed);
        assertEquals(5173, levels);
        assertEquals(1758, powers);
        assertEquals(312, effects);
        assertEquals(48, messages);
        assertEquals(116, descriptions);
    }

    @Test
    void spotCheckedRecordsAgreeWithTheUpstreamFile() throws Exception {
        List<Activation> items = new ActivationReader().parseWithResult(REAL_FILE).items();

        Activation curePoison = named(items, "CURE_POISON");
        assertFalse(aim(curePoison));
        assertEquals(5, level(curePoison));
        assertEquals(1, curePoison.getPower());
        assertEquals(1, effectCount(curePoison));
        assertEquals("neutralizes poison", desc(curePoison));
        assertEquals("", message(curePoison));

        Activation fireBolt = named(items, "FIRE_BOLT");
        assertTrue(aim(fireBolt));
        assertEquals(1, level(fireBolt));
        assertEquals(5, fireBolt.getPower());
        assertEquals(1, effectCount(fireBolt));
        assertEquals("Your {kind} {is} covered in fire...", message(fireBolt));
        assertEquals("", desc(fireBolt));

        Activation dragonPower = named(items, "DRAGON_POWER");
        assertTrue(aim(dragonPower));
        assertEquals(57, level(dragonPower));
        assertEquals(25, dragonPower.getPower());
        assertEquals(1, effectCount(dragonPower));

        // Two effect: lines, each followed by an effect-yx: line.
        Activation detectTreasure = named(items, "DETECT_TREASURE");
        assertEquals(2, effectCount(detectTreasure));
        assertEquals(5, level(detectTreasure));
        assertEquals(6, detectTreasure.getPower());
        assertEquals("detects gold and objects nearby", desc(detectTreasure));

        // dice: with an expr: line.
        Activation protEvil = named(items, "PROTEVIL");
        assertEquals(40, level(protEvil));
        assertEquals(1, effectCount(protEvil));
        assertEquals("The {kind} let{s} out a shrill wail...", message(protEvil));
    }

    @Test
    void parseGivesTheSameListAsParseWithResult() throws IOException {
        ActivationReader reader = new ActivationReader();

        List<String> viaParse = reader.parse(REAL_FILE).stream().map(Activation::getName).toList();
        List<String> viaResult = reader.parseWithResult(REAL_FILE).items().stream()
                .map(Activation::getName).toList();

        assertEquals(viaResult, viaParse);
    }

    @Test
    void aMissingFileIsAnIoExceptionWhereCQuits() {
        // run_parse_act calls parse_file_quit_not_found, which quits; here the caller gets an
        // IOException to decide that for itself.
        assertThrows(IOException.class,
                () -> new ActivationReader().parseWithResult(tempDir.resolve("absent.txt").toString()));
        assertThrows(IOException.class,
                () -> new ActivationReader().parse(tempDir.resolve("absent.txt").toString()));
    }

    // ---- The reader's contract -------------------------------------------

    @Test
    void aRepeatedAimLevelOrPowerKeepsTheLastValue() throws Exception {
        // parse_act_aim, parse_act_level and parse_act_power each overwrite their field.
        Activation a = loadOne("name:T\naim:0\naim:1\nlevel:3\nlevel:9\npower:4\npower:7\n"
                + "effect:CURE:POISONED\n");

        assertTrue(aim(a));
        assertEquals(9, level(a));
        assertEquals(7, a.getPower());
    }

    @Test
    void directivesMayComeInAnyOrderAfterTheName() throws Exception {
        // The handlers only need the record header, so desc: and msg: may precede the effect and
        // the numbers may come last.
        Activation a = loadOne("name:T\ndesc:d\neffect:CURE:POISONED\nmsg:m\npower:7\nlevel:3\naim:1\n");

        assertTrue(aim(a));
        assertEquals(3, level(a));
        assertEquals(7, a.getPower());
        assertEquals("m", message(a));
        assertEquals("d", desc(a));
        assertEquals(1, effectCount(a));
    }

    @Test
    void anAbsentAimLevelAndPowerAreFalseAndZero() throws Exception {
        // mem_zalloc leaves the three at false, 0, 0.
        Activation a = loadOne("name:T\neffect:CURE:POISONED\n");

        assertFalse(aim(a));
        assertEquals(0, level(a));
        assertEquals(0, a.getPower());
    }

    @Test
    void severalEffectsAreKept() throws Exception {
        Activation a = loadOne(record("effect:CURE:BLIND\neffect:CURE:CONFUSED\n"));

        assertEquals(3, effectCount(a));
    }

    @Test
    void twoRecordsSharingANameBothLoadWithTheLaterFirst() throws Exception {
        // C keeps both; findact walks activations[] from slot 1, which is the later record.
        ParseResult<Activation> result = load("dup.txt", withHeader(2,
                "name:T\nlevel:1\neffect:CURE:POISONED\n\nname:T\nlevel:2\neffect:CURE:POISONED\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals(2, level(result.items().get(0)));
        assertEquals(1, level(result.items().get(1)));
    }

    @Test
    void aColonInADescriptionOrMessageIsKept() throws Exception {
        // desc and msg are the last field of their line, so parser_parse hands over the whole rest.
        Activation a = loadOne(record("msg:a:b\ndesc:c:d\n"));

        assertEquals("a:b", message(a));
        assertEquals("c:d", desc(a));
    }

    @Test
    void aDescriptionKeepsItsLeadingSpace() throws Exception {
        // The shipped file continues a description with "desc: could go"; the space is data.
        Activation a = loadOne(record("desc: could go\n"));

        assertEquals(" could go", desc(a));
    }

    @Test
    void commentsAndBlankLinesAreIgnored() throws Exception {
        Activation a = loadOne("# a comment\n\nname:T\n# another\nlevel:4\n\neffect:CURE:POISONED\n");

        assertEquals(4, level(a));
    }

    @Test
    void anEmptyDescOrMsgIsAHardErrorAsInC() throws IOException {
        // strtok(sp, "") on an empty remainder gives NULL, so parser_parse returns
        // PARSE_ERROR_MISSING_FIELD for the str field.
        assertHardError(withHeader(1, record("desc:\n")));
        assertHardError(withHeader(1, record("msg:\n")));
    }

    @Test
    void aNumericDirectiveWithNoNumberIsAHardError() throws IOException {
        assertHardError(withHeader(1, "name:T\naim:\neffect:CURE:POISONED\n"));
        assertHardError(withHeader(1, "name:T\nlevel:\neffect:CURE:POISONED\n"));
        assertHardError(withHeader(1, "name:T\npower:\neffect:CURE:POISONED\n"));
    }

    @Test
    void aDirectiveBeforeTheNameIsAHardError() throws IOException {
        // PARSE_ERROR_MISSING_RECORD_HEADER in C.
        assertHardError(withHeader(1, "aim:1\nname:T\neffect:CURE:POISONED\n"));
        assertHardError(withHeader(1, "desc:d\nname:T\neffect:CURE:POISONED\n"));
    }

    @Test
    void aMissingRecordCountHeaderIsAHardError() throws IOException {
        // The header is a Java-only addition and the grammar requires it.
        assertHardError("name:T\nlevel:1\neffect:CURE:POISONED\n");
    }

    @Test
    void anUnreadableRecordAfterAGoodOneStillCostsTheWholeFile() throws IOException {
        // A syntax error anywhere is fatal for the file, where C would have loaded the records
        // before it and then quit.
        assertHardError(withHeader(2, record("\nname:U\nlevel:x\neffect:CURE:POISONED\n")));
    }

    @Test
    void aBadRecordIsDroppedAndTheRestStillLoad() throws IOException {
        // The first record's level fits the lexer but not an int; the assembler drops that record
        // alone, the count matches, and the second record loads.
        ParseResult<Activation> result = load("partial.txt", withHeader(2,
                "name:BAD\nlevel:99999999999\neffect:CURE:POISONED\n\nname:GOOD\nlevel:2\n"
                        + "effect:CURE:POISONED\n"));

        assertEquals(1, result.errors().size(), result.errors().toString());
        assertTrue(result.errors().get(0).contains("malformed level"), result.errors().get(0));
        assertEquals(List.of("GOOD"), result.items().stream().map(Activation::getName).toList());
    }

    @Test
    void aPowerTooLargeForAnIntDropsTheRecordWithASoftError() throws IOException {
        ParseResult<Activation> result = load("bigpower.txt",
                withHeader(1, "name:BAD\npower:99999999999\neffect:CURE:POISONED\n"));

        assertEquals(1, result.errors().size(), result.errors().toString());
        assertTrue(result.errors().get(0).contains("malformed power"), result.errors().get(0));
        assertTrue(result.items().isEmpty());
    }

    // ---- Where the grammar and assembler differ from C -------------------

    @Test
    void aSecondDescLineReplacesTheFirstHereWhereCAppendsIt() throws Exception {
        // parse_act_desc runs act->desc = string_append(act->desc, ...), so C gives "ab".
        Activation a = loadOne(record("desc:a\ndesc:b\n"));

        assertEquals("b", desc(a));
    }

    @Test
    void theShippedTeleLongAndResistAllLoseTheirFirstDescLine() throws Exception {
        List<Activation> items = new ActivationReader().parseWithResult(REAL_FILE).items();

        // C: "teleports you randomly about 60 per cent of the largest distance you could go"
        assertEquals(" could go", desc(named(items, "TELE_LONG")));
        // C: "grants temporary resistance to acid, electricity, fire, cold and poison for 1d20+20 turns"
        assertEquals(" for 1d20+20 turns", desc(named(items, "RESIST_ALL")));
    }

    @Test
    void aSecondMsgLineReplacesTheFirstHereWhereCAppendsIt() throws Exception {
        // parse_act_msg runs act->message = string_append(act->message, ...), so C gives "ab".
        Activation a = loadOne(record("msg:a\nmsg:b\n"));

        assertEquals("b", message(a));
    }

    @Test
    void aRecordWithOnlyANameIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        // parse_act_name builds a complete record on its own.
        assertHardError(withHeader(1, "name:T\n"));
    }

    @Test
    void aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing() throws IOException {
        // finish_parse_act copes with an empty chain: act_max becomes 1, only the blank slot.
        assertHardError("record-count:0\n");
    }

    @Test
    void aLowerCaseNameIsAHardErrorHereWhereCTakesAnyString() throws IOException {
        // name is "str name" in C; the lexer takes only upper case, digits and underscore.
        assertHardError(withHeader(1, "name:lower\neffect:CURE:POISONED\n"));
        assertHardError(withHeader(1, "name:TWO WORDS\neffect:CURE:POISONED\n"));
    }

    @Test
    void aNameWithTrailingBlanksIsTrimmedHereWhereCKeepsThem() throws IOException {
        // C keeps "T  " as the name; the lexer stops at the blank and the end-of-line rule eats it.
        Activation a = loadOne("name:T  \neffect:CURE:POISONED\n");

        assertEquals("T", a.getName());
    }

    @Test
    void aNegativePowerIsAcceptedHereWhereCRejectsAUint() throws Exception {
        // "power uint power": parser_parse refuses a token starting with '-'.
        Activation a = loadOne(record("power:-3\n"));

        assertEquals(-3, a.getPower());
    }

    @Test
    void aNegativeAimIsAcceptedAndReadAsFalseHereWhereCRejectsAUint() throws Exception {
        Activation a = loadOne(record("aim:-1\n"));

        assertFalse(aim(a));
    }

    @Test
    void anAimOtherThanExactlyOneIsFalseHereWhereCReadsAnyNonZeroAsTrue() throws Exception {
        // parse_act_aim: act->aim = val ? true : false.
        assertFalse(aim(loadOne(record("aim:2\n"))));
        assertFalse(aim(loadOne(record("aim:01\n"))));
        assertTrue(aim(loadOne(record("aim:1\n"))));
    }

    @Test
    void aLeadingZeroLevelIsReadAsDecimalHereWhereCReadsOctal() throws Exception {
        // strtol(tok, &z, 0): 010 is 8 in C.
        Activation a = loadOne(record("level:010\n"));

        assertEquals(10, level(a));
    }

    @Test
    void aHexLevelIsAHardErrorHereWhereCReadsItAsSixteen() throws IOException {
        assertHardError(withHeader(1, record("level:0x10\n")));
    }

    @Test
    void anAbsentMsgOrDescIsAnEmptyStringHereWhereCHoldsNull() throws Exception {
        // cmd-obj.c tests !activation->message and obj-info.c tests activation->desc for NULL.
        Activation a = loadOne(record(""));

        assertEquals("", message(a));
        assertEquals("", desc(a));
    }

    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
        // parser_parse skips leading white space; the shared CommentsAndEol rules do not.
        assertHardError(withHeader(1, "name:T\n  level:4\neffect:CURE:POISONED\n"));
    }

    @Test
    void aDiceLineBeforeAnyEffectIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        // parse_act_dice: "If there is no effect, assume that this is human and not parser error."
        assertHardError(withHeader(1, "name:T\ndice:20\neffect:CURE:POISONED\n"));
    }

    @Test
    void anEffectYxLineBeforeAnyEffectIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError(withHeader(1, "name:T\neffect-yx:22:40\neffect:CURE:POISONED\n"));
    }

    @Test
    void effectYxAndDiceOnTheSameEffectAreAHardErrorHereWhereCTakesBoth() throws IOException {
        assertHardError(withHeader(1, "name:T\neffect:CURE:POISONED\neffect-yx:22:40\ndice:20\n"));
        assertHardError(withHeader(1, "name:T\neffect:CURE:POISONED\ndice:20\neffect-yx:22:40\n"));
    }

    @Test
    void effectMsgIsAcceptedHereWhereCHasNoSuchDirective() throws Exception {
        // act_parser registers no effect-msg, so C gives PARSE_ERROR_UNDEFINED_DIRECTIVE.
        Activation a = loadOne(record("effect-msg:hello\n"));

        assertEquals(1, effectCount(a));
    }
}
