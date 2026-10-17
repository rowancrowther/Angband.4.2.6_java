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
import uk.co.jackoftradesltd.middle.combat.BlowMethod;
import uk.co.jackoftradesltd.middle.enums.MessageType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link BlowMethodReader}: file text -&gt;
 * {@code BlowMethodLexer}/{@code BlowMethodGrammar} -&gt; {@code BlowMethodParseRecord} -&gt;
 * {@code BlowMethodAssembler} -&gt; {@link BlowMethod}.
 * <p>
 * The interesting boundaries here are all on the assembler side. {@code blow_methods.txt} is
 * structurally simple - eight fixed directives, only {@code act:} repeating - so the tests
 * concentrate on the two places where C semantics have to be reproduced by hand:
 * <ul>
 *   <li>the <em>truthiness</em> of {@code cut:}/{@code stun:}/{@code miss:}/{@code phys:}
 *       ([C] {@code mon-init.c} does {@code val ? true : false}, so any non-zero is true, not
 *       just {@code 1}); and</li>
 *   <li>the <em>optional</em> {@code msg:} directive ([C] registers it as {@code "msg ?str msg"},
 *       and a record that omits it keeps the zeroed {@code msgt}, i.e. {@code MSG_GENERIC}).</li>
 * </ul>
 * The {@code msg:} directive has two distinct "absent" shapes in this grammar - the line missing
 * entirely, and a bare {@code msg:} with no text - because {@code FREE_TEXT_EOL} lets the lexer
 * pop back out of an empty free-text run. Both must land on {@code MSG_GENERIC}.
 *
 * @author Rowan Crowther
 */
class BlowMethodReaderTest {

    private static final String REAL_FILE = "lib/gamedata/blow_methods.txt";

    @TempDir
    Path tempDir;

    /**
     * Prefix a body with the {@code record-count:} header the {@code file} rule requires.
     */
    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * Render one complete blow-method block in directive order, with a single {@code act:}.
     */
    private static String entry(String name, int cut, int stun, int miss, int phys,
                                String msg, String desc) {
        return "name:" + name + "\n"
                + "cut:" + cut + "\n"
                + "stun:" + stun + "\n"
                + "miss:" + miss + "\n"
                + "phys:" + phys + "\n"
                + (msg == null ? "" : "msg:" + msg + "\n")
                + "act:does something to {target}\n"
                + "desc:" + desc + "\n";
    }

    private ParseResult<BlowMethod> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new BlowMethodReader().parseWithResults(file.toString());
    }

    private static BlowMethod byName(List<BlowMethod> methods, String name) {
        return methods.stream().filter(m -> m.getName().equals(name)).findFirst().orElseThrow();
    }

    private static List<String> names(List<BlowMethod> methods) {
        return methods.stream().map(BlowMethod::getName).toList();
    }

    // ---- Happy path: the real shipped file -------------------------------

    @Test
    void cleanLoadOfTheRealFileYieldsNineteenBlowMethods() throws IOException {
        ParseResult<BlowMethod> result = new BlowMethodReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(19, result.items().size());
    }

    @Test
    void methodHitHasAllEightFieldsPopulated() throws IOException {
        ParseResult<BlowMethod> result = new BlowMethodReader().parseWithResults(REAL_FILE);

        BlowMethod hit = byName(result.items(), "HIT");
        assertEquals("HIT", hit.getName());
        assertTrue(hit.isCut());
        assertTrue(hit.isStun());
        assertTrue(hit.isMiss());
        assertTrue(hit.isPhys());
        assertEquals(MessageType.MSG_MON_HIT, hit.getMesgT());
        assertEquals(List.of("hits {target}"), hit.getBlowMessage());
        assertEquals("hit", hit.getDesc());
    }

    @Test
    void everyMessageTypeInTheRealFileResolves() throws IOException {
        ParseResult<BlowMethod> result = new BlowMethodReader().parseWithResults(REAL_FILE);

        // A record whose msg: fails to resolve is dropped by the assembler, so a full count plus
        // no MSG_GENERIC is proof that all 19 MSG_MON_* names matched the enum.
        assertEquals(19, result.items().size());
        assertTrue(result.items().stream().noneMatch(m -> m.getMesgT() == MessageType.MSG_GENERIC));
    }

    @Test
    void repeatedActLinesAllAccumulateInOrder() throws IOException {
        ParseResult<BlowMethod> result = new BlowMethodReader().parseWithResults(REAL_FILE);

        BlowMethod insult = result.items().stream()
                .filter(m -> m.getName().equals("INSULT")).findFirst().orElseThrow();

        assertEquals(8, insult.getBlowMessage().size(), "INSULT ships eight act: lines");
        assertEquals("insults {target}!", insult.getBlowMessage().get(0), "file order preserved");
        assertEquals("moons {target}!!!", insult.getBlowMessage().get(7));
    }

    @Test
    void nonPhysicalNonMissingMethodReadsAllFourFlagsFalse() throws IOException {
        ParseResult<BlowMethod> result = new BlowMethodReader().parseWithResults(REAL_FILE);

        BlowMethod drool = result.items().stream()
                .filter(m -> m.getName().equals("DROOL")).findFirst().orElseThrow();

        assertFalse(drool.isCut());
        assertFalse(drool.isStun());
        assertFalse(drool.isMiss());
        assertFalse(drool.isPhys());
    }

    // ---- List order: C's, which is the reverse of the file's -------------

    @Test
    void realFileComesBackInReverseFileOrderAsCFillsBlowMethods() throws IOException {
        // The order of the `name:` lines in upstream lib/gamedata/blow_methods.txt is HIT first,
        // MOAN last. parse_meth_name pushes each record on the head of a chain and
        // finish_parse_meth then fills blow_methods[1], [2], ... from that head, so slot 1 is
        // MOAN and the last slot is HIT. (finish_parse_eff counts down and keeps file order; this
        // one does not.)
        List<String> expected = List.of("MOAN", "INSULT", "BEG", "SPORE", "WAIL", "GAZE", "SPIT",
                "DROOL", "CRAWL", "ENGULF", "CRUSH", "BUTT", "STING", "BITE", "CLAW", "KICK",
                "PUNCH", "TOUCH", "HIT");

        assertEquals(expected, names(new BlowMethodReader().parseWithResults(REAL_FILE).items()));
    }

    @Test
    void parseReturnsTheSameMethodsInTheSameOrderAsParseWithResults() throws IOException {
        BlowMethodReader reader = new BlowMethodReader();

        assertEquals(names(reader.parseWithResults(REAL_FILE).items()),
                names(reader.parse(REAL_FILE)));
    }

    @Test
    void duplicateNamesPutTheLaterRecordFirstSoALookupFindsItAsFindmethDoes() throws IOException {
        // findmeth walks from blow_methods[1], the last record in the file, and stops at the
        // first name match - so with two records called POKE the later one wins.
        String body = entry("POKE", 0, 0, 1, 1, "MON_HIT", "first")
                + entry("PROD", 0, 0, 1, 1, "MON_HIT", "middle")
                + entry("POKE", 0, 0, 1, 1, "MON_HIT", "second");
        ParseResult<BlowMethod> result = load("dupes.txt", withHeader(3, body));

        assertEquals(List.of("POKE", "PROD", "POKE"), names(result.items()));
        assertEquals("second", byName(result.items(), "POKE").getDesc());
    }

    @Test
    void aSkippedRecordLeavesTheOthersInReverseFileOrder() throws IOException {
        String body = entry("A", 0, 0, 1, 1, "MON_HIT", "a")
                + entry("BAD", 0, 0, 1, 1, "NO_SUCH_MESSAGE", "bad")
                + entry("C", 0, 0, 1, 1, "MON_HIT", "c");
        ParseResult<BlowMethod> result = load("skip-middle.txt", withHeader(3, body));

        assertEquals(List.of("C", "A"), names(result.items()));
        assertTrue(result.hasErrors());
    }

    @Test
    void actLinesWithinARecordStayInFileOrderWhileTheRecordsReverse() throws IOException {
        // Only the records reverse. C also reverses each record's act: list, but that order is
        // never observable (the message is picked by random index), so the port keeps file order.
        String body = "name:ONE\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:a\nact:b\ndesc:one\n"
                + "name:TWO\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:c\nact:d\ndesc:two\n";
        ParseResult<BlowMethod> result = load("acts.txt", withHeader(2, body));

        assertEquals(List.of("TWO", "ONE"), names(result.items()));
        assertEquals(List.of("a", "b"), byName(result.items(), "ONE").getBlowMessage());
    }

    // ---- The reader's own contract ---------------------------------------

    @Test
    void missingFileIsAnIOExceptionNotAnEmptyResult() {
        // [C] parse_file_quit_not_found quits when the file is absent.
        assertThrows(IOException.class,
                () -> new BlowMethodReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
        assertThrows(IOException.class,
                () -> new BlowMethodReader().parse(tempDir.resolve("absent.txt").toString()));
    }

    @Test
    void parseDropsTheErrorsSoASyntaxErrorLooksLikeAnEmptyList() throws IOException {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file, withHeader(1, "name:POKE\ncut:0\n"));

        assertTrue(new BlowMethodReader().parse(file.toString()).isEmpty());
        assertTrue(new BlowMethodReader().parseWithResults(file.toString()).hasErrors());
    }

    @Test
    void actBeforeAnyNameIsAHardError() throws IOException {
        // [C] parse_meth_act_msg returns PARSE_ERROR_MISSING_RECORD_HEADER when no name: has
        // been seen; the other handlers would assert. Either way the load fails.
        ParseResult<BlowMethod> result = load("act-first.txt",
                withHeader(1, "act:pokes {target}\n" + entry("POKE", 0, 0, 1, 1, "MON_HIT", "poke")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Where the grammar is stricter than C (docs/grammar_issues.md) ---

    @Test
    void repeatedDescIsAHardErrorHereWhereCAppendsTheLines() throws IOException {
        // [C] parse_meth_desc does string_append, so two desc: lines would join. The grammar
        // closes a record on its one desc:, so the second is a syntax error.
        String twice = "name:POKE\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:p\ndesc:po\ndesc:ke\n";
        ParseResult<BlowMethod> result = load("desc-twice.txt", withHeader(1, twice));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void nameOnlyRecordIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        // [C] src/tests/parse/blowm.c test_name0: a bare name: leaves every flag false and
        // desc NULL, and parses without error.
        ParseResult<BlowMethod> result = load("name-only.txt", withHeader(1, "name:POKE\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void leadingZeroFlagReadsTrueHereWhereCReadsZero() throws IOException {
        // [C] parser_getuint uses strtoul base 0, so "00" is the number 0 and the flag is false.
        // The assembler tests the text against "0", so "00" is true. Pinned as a known gap.
        String body = "name:POKE\ncut:00\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:p\ndesc:poke\n";
        ParseResult<BlowMethod> result = load("zero-zero.txt", withHeader(1, body));

        assertTrue(result.items().get(0).isCut(), "C would give false");
    }

    // ---- The optional msg: directive -------------------------------------

    @Test
    void omittedMsgLineDefaultsToGeneric() throws IOException {
        // [C] parse_meth_message_type simply returns without touching msgt when the value is
        // absent, leaving the mem_zalloc'd 0 == MSG_GENERIC.
        ParseResult<BlowMethod> result =
                load("no-msg.txt", withHeader(1, entry("POKE", 0, 0, 1, 1, null, "poke")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(MessageType.MSG_GENERIC, result.items().get(0).getMesgT());
    }

    @Test
    void bareMsgLineWithNoValueDefaultsToGeneric() throws IOException {
        // FREE_TEXT_EOL matches the newline straight after "msg:", so the optional STRING in the
        // msg rule never fires and the record carries a null msg. [C] parser_hasval is false here
        // too, so this must behave exactly like the omitted-line case above - not as an error.
        String bare = "name:POKE\ncut:0\nstun:0\nmiss:1\nphys:1\n"
                + "msg:\nact:pokes {target}\ndesc:poke\n";
        ParseResult<BlowMethod> result = load("bare-msg.txt", withHeader(1, bare));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size(), "a valueless msg: must not drop the record");
        assertEquals(MessageType.MSG_GENERIC, result.items().get(0).getMesgT());
    }

    @Test
    void unresolvableMsgIsASoftErrorAndTheRecordIsSkipped() throws IOException {
        ParseResult<BlowMethod> result =
                load("bad-msg.txt", withHeader(1, entry("POKE", 0, 0, 1, 1, "NO_SUCH_MESSAGE", "poke")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unrecognized MessageType")),
                result.errors()::toString);
    }

    @Test
    void oneBadMsgDoesNotStopLaterRecordsLoading() throws IOException {
        // Partial-results contract: the assembler continues past the skipped record.
        String body = entry("BAD", 0, 0, 1, 1, "NO_SUCH_MESSAGE", "bad")
                + entry("GOOD", 0, 0, 1, 1, "MON_HIT", "good");
        ParseResult<BlowMethod> result = load("mixed.txt", withHeader(2, body));

        assertEquals(1, result.items().size());
        assertEquals("GOOD", result.items().get(0).getName());
        assertTrue(result.hasErrors());
    }

    // ---- C truthiness of the four boolean directives ---------------------

    @Test
    void anyNonZeroValueMakesABooleanDirectiveTrue() throws IOException {
        // [C] does `val ? true : false`, so 2 is just as true as 1. The port compares against the
        // string "0", which reproduces this for any positive value.
        String body = "name:POKE\ncut:2\nstun:7\nmiss:1\nphys:1\n"
                + "msg:MON_HIT\nact:pokes {target}\ndesc:poke\n";
        ParseResult<BlowMethod> result = load("truthy.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        BlowMethod poke = result.items().get(0);
        assertTrue(poke.isCut());
        assertTrue(poke.isStun());
    }

    // ---- record-count header (soft) --------------------------------------

    @Test
    void recordCountMismatchIsReportedButValidRecordsStillLoad() throws IOException {
        ParseResult<BlowMethod> result =
                load("bad-count.txt", withHeader(5, entry("POKE", 0, 0, 1, 1, "MON_HIT", "poke")));

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    // ---- Hard (fail-closed) parse errors: empty result + errors ----------

    @Test
    void missingDescIsAHardError() throws IOException {
        // desc is mandatory and closes the block, so dropping it leaves the blow rule unmatched.
        String noDesc = "name:POKE\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:pokes {target}\n";
        ParseResult<BlowMethod> result = load("no-desc.txt", withHeader(1, noDesc));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void outOfOrderDirectivesAreAHardError() throws IOException {
        // The blow rule is a fixed sequence, not a set - stun: before cut: cannot match.
        String swapped = "name:POKE\nstun:0\ncut:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:p\ndesc:poke\n";
        ParseResult<BlowMethod> result = load("swapped.txt", withHeader(1, swapped));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<BlowMethod> result =
                load("no-header.txt", entry("POKE", 0, 0, 1, 1, "MON_HIT", "poke"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void zeroActLinesIsAccepted() throws IOException {
        // act: is a (…)* in the grammar and [C] never requires one, so a method with no action
        // string is legal - it just carries an empty message list.
        String noAct = "name:POKE\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\ndesc:poke\n";
        ParseResult<BlowMethod> result = load("no-act.txt", withHeader(1, noAct));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).getBlowMessage().isEmpty());
    }
}
