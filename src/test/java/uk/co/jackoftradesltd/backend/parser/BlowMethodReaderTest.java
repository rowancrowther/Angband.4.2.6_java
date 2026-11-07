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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
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
 * The interesting boundaries here are mostly on the assembler side. {@code blow_methods.txt} is
 * structurally simple - a {@code name:} followed by directives that C reads in any order, with
 * {@code act:} and {@code desc:} able to repeat - so the tests concentrate on the places where
 * C semantics have to be reproduced by hand:
 * <ul>
 *   <li>the <em>truthiness</em> of {@code cut:}/{@code stun:}/{@code miss:}/{@code phys:}
 *       ([C] {@code mon-init.c} does {@code val ? true : false}, so any non-zero is true, not
 *       just {@code 1}), and a flag that is never named, which stays false;</li>
 *   <li>the <em>optional</em> {@code msg:} directive ([C] registers it as {@code "msg ?str msg"},
 *       and a record that omits it keeps the zeroed {@code msgt}, i.e. {@code MSG_GENERIC});</li>
 *   <li>the <em>order</em> of the list and of each record's {@code act:} lines, both of which C
 *       fills from the head; and</li>
 *   <li>the <em>joining</em> of repeated {@code desc:} lines.</li>
 * </ul>
 * The {@code msg:} directive has two distinct "absent" shapes in this grammar - the line missing
 * entirely, and a bare {@code msg:} with no text - because {@code FREE_TEXT_EOL} lets the lexer
 * pop back out of an empty free-text run. Both must land on {@code MSG_GENERIC}.
 *
 * <p>Class BlowMethodReaderTest coded before 261104, commented in full on 261107 (the records
 * C accepts but the grammar once refused, and the {@code act:} order, tested 261107).
 *
 * @author Rowan Crowther
 */
class BlowMethodReaderTest {

    private static final String REAL_FILE = "lib/gamedata/blow_methods.txt";

    @TempDir
    Path tempDir;


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

        // A msg: that fails to resolve ends the load, so a full count plus no MSG_GENERIC is
        // proof that all 19 MSG_MON_* names matched the enum.
        assertEquals(19, result.items().size());
        assertTrue(result.items().stream().noneMatch(m -> m.getMesgT() == MessageType.MSG_GENERIC));
    }

    @Test
    void repeatedActLinesAllAccumulateInReverseFileOrderAsCPushesThem() throws IOException {
        ParseResult<BlowMethod> result = new BlowMethodReader().parseWithResults(REAL_FILE);

        BlowMethod insult = result.items().stream()
                .filter(m -> m.getName().equals("INSULT")).findFirst().orElseThrow();

        // The file gives "insults {target}!" first and "moons {target}!!!" last. C pushes each
        // act: on the head of meth->messages, so the last line is the head of its list.
        assertEquals(8, insult.getBlowMessage().size(), "INSULT ships eight act: lines");
        assertEquals("moons {target}!!!", insult.getBlowMessage().get(0), "last file line first");
        assertEquals("insults {target}!", insult.getBlowMessage().get(7), "first file line last");
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
        ParseResult<BlowMethod> result = load("dupes.txt", body);

        assertEquals(List.of("POKE", "PROD", "POKE"), names(result.items()));
        assertEquals("second", byName(result.items(), "POKE").getDesc());
    }

    @Test
    void aBadRecordInTheMiddleEndsTheWholeLoad() {
        // [C] parse_meth_message_type returns PARSE_ERROR_INVALID_MESSAGE, which stops the file
        // load, so no record survives - not even the good ones either side of the bad one.
        String body = entry("A", 0, 0, 1, 1, "MON_HIT", "a")
                + entry("BAD", 0, 0, 1, 1, "NO_SUCH_MESSAGE", "bad")
                + entry("C", 0, 0, 1, 1, "MON_HIT", "c");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("skip-middle.txt", body));
        assertTrue(e.getMessage().contains("line: 9"), e::getMessage);
    }

    @Test
    void actLinesWithinARecordReverseAsTheRecordsDo() throws IOException {
        // The records reverse, and so does each record's act: list: parse_meth_act_msg pushes on
        // the head, and mon-blows.c walks randint0(num_messages) steps along that list, so the
        // order decides which message a given random number picks. A record's lines never mix
        // with its neighbour's.
        String body = "name:ONE\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:a\nact:b\ndesc:one\n"
                + "name:TWO\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:c\nact:d\ndesc:two\n";
        ParseResult<BlowMethod> result = load("acts.txt", body);

        assertEquals(List.of("TWO", "ONE"), names(result.items()));
        assertEquals(List.of("b", "a"), byName(result.items(), "ONE").getBlowMessage());
        assertEquals(List.of("d", "c"), byName(result.items(), "TWO").getBlowMessage());
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

    /**
     * A syntax error is thrown from both entry points: {@code parse} no longer hides it as an
     * empty list. The input is a {@code cut:} line before any {@code name:}, which the grammar
     * cannot start a record with; a record that only lacks directives is no longer an error.
     *
     * <p>Function bothEntryPointsThrowOnASyntaxError coded on 261104, commented in full on 261107
     * (input changed 261107, as {@code name:POKE} then {@code cut:0} is now a whole record).
     * It replaces parseDropsTheErrorsSoASyntaxErrorLooksLikeAnEmptyList.
     */
    @Test
    void bothEntryPointsThrowOnASyntaxError() throws IOException {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file, "cut:0\n");

        assertThrows(ParseCancellationException.class, () -> new BlowMethodReader().parse(file.toString()));
        assertThrows(ParseCancellationException.class, () -> new BlowMethodReader().parseWithResults(file.toString()));
    }

    @Test
    void actBeforeAnyNameIsAHardError() throws IOException {
        // [C] parse_meth_act_msg returns PARSE_ERROR_MISSING_RECORD_HEADER when no name: has
        // been seen; the other handlers would assert. Either way the load fails.
        assertThrows(ParseCancellationException.class, () -> load("act-first.txt",
                "act:pokes {target}\n" + entry("POKE", 0, 0, 1, 1, "MON_HIT", "poke")));

    }

    // ---- Records as C reads them: any order, any directive missing -------

    @Test
    void repeatedDescLinesAreJoinedAsCStringAppendDoes() throws IOException {
        // [C] src/tests/parse/blowm.c test_desc0: "desc:hit" then "desc: something" gives
        // "hit something" - string_append joins the lines with nothing added between them, so
        // the space comes from the second value.
        String twice = "name:POKE\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:p\ndesc:hit\ndesc: something\n";
        ParseResult<BlowMethod> result = load("desc-twice.txt", twice);

        assertEquals("hit something", result.items().get(0).getDesc());
    }

    @Test
    void nameOnlyRecordLoadsWithEveryFlagFalseAsInC() throws IOException {
        // [C] src/tests/parse/blowm.c test_name0: a bare name: leaves every flag false, msgt 0
        // and no messages, and parses without error.
        ParseResult<BlowMethod> result = load("name-only.txt", "name:POKE\n");

        assertEquals(1, result.items().size());
        BlowMethod poke = result.items().get(0);
        assertEquals("POKE", poke.getName());
        assertFalse(poke.isCut());
        assertFalse(poke.isStun());
        assertFalse(poke.isMiss());
        assertFalse(poke.isPhys());
        assertEquals(MessageType.MSG_GENERIC, poke.getMesgT());
        assertTrue(poke.getBlowMessage().isEmpty());
    }

    @Test
    void aFlagLineLeftOutDoesNotDisturbTheOthers() throws IOException {
        // Each flag has its own handler in C, so naming one leaves the rest at zero.
        ParseResult<BlowMethod> result = load("lone-flags.txt",
                "name:CUTTER\ncut:1\n" + "name:STUNNER\nstun:1\n"
                        + "name:MISSER\nmiss:1\n" + "name:HITTER\nphys:1\n");

        BlowMethod cutter = byName(result.items(), "CUTTER");
        assertTrue(cutter.isCut());
        assertFalse(cutter.isStun());
        assertFalse(cutter.isMiss());
        assertFalse(cutter.isPhys());

        BlowMethod stunner = byName(result.items(), "STUNNER");
        assertFalse(stunner.isCut());
        assertTrue(stunner.isStun());
        assertFalse(stunner.isMiss());
        assertFalse(stunner.isPhys());

        BlowMethod misser = byName(result.items(), "MISSER");
        assertFalse(misser.isCut());
        assertFalse(misser.isStun());
        assertTrue(misser.isMiss());
        assertFalse(misser.isPhys());

        BlowMethod hitter = byName(result.items(), "HITTER");
        assertFalse(hitter.isCut());
        assertFalse(hitter.isStun());
        assertFalse(hitter.isMiss());
        assertTrue(hitter.isPhys());
    }

    @Test
    void aLaterLineOfTheSameFlagWinsAsInC() throws IOException {
        // Each cut: line assigns, so the last one decides.
        ParseResult<BlowMethod> result = load("flag-twice.txt", "name:POKE\ncut:1\ncut:0\nstun:0\nstun:1\n");

        assertFalse(result.items().get(0).isCut());
        assertTrue(result.items().get(0).isStun());
    }

    @Test
    void leadingZeroFlagReadsFalseAsInC() throws IOException {
        // [C] parser_getuint uses strtoul base 0, so "00" is the number 0 and the flag is false.
        String body = "name:POKE\ncut:00\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:p\ndesc:poke\n";
        ParseResult<BlowMethod> result = load("zero-zero.txt", body);

        assertFalse(result.items().get(0).isCut());
    }

    @Test
    void negativeFlagEndsTheLoadAsParserGetuintRefusesIt() {
        // [C] parser_getuint refuses a token starting with '-' as PARSE_ERROR_NOT_NUMBER. The
        // lexer's INTEGER lets the '-' through, so the assembler has to refuse it.
        String body = "name:POKE\ncut:0\nstun:-1\nmiss:1\nphys:1\nmsg:MON_HIT\nact:p\ndesc:poke\n";

        assertThrows(DataFileParseException.class, () -> load("negative.txt", body));
    }

    // ---- The optional msg: directive -------------------------------------

    @Test
    void omittedMsgLineDefaultsToGeneric() throws IOException {
        // [C] parse_meth_message_type simply returns without touching msgt when the value is
        // absent, leaving the mem_zalloc'd 0 == MSG_GENERIC.
        ParseResult<BlowMethod> result =
                load("no-msg.txt", entry("POKE", 0, 0, 1, 1, null, "poke"));

        assertEquals(MessageType.MSG_GENERIC, result.items().get(0).getMesgT());
    }

    @Test
    void bareMsgLineWithNoValueDefaultsToGeneric() throws IOException {
        // FREE_TEXT_EOL matches the newline straight after "msg:", so the optional STRING in the
        // msg rule never fires and the record carries a null msg. [C] parser_hasval is false here
        // too, so this must behave exactly like the omitted-line case above - not as an error.
        String bare = "name:POKE\ncut:0\nstun:0\nmiss:1\nphys:1\n"
                + "msg:\nact:pokes {target}\ndesc:poke\n";
        ParseResult<BlowMethod> result = load("bare-msg.txt", bare);

        assertEquals(1, result.items().size(), "a valueless msg: must not drop the record");
        assertEquals(MessageType.MSG_GENERIC, result.items().get(0).getMesgT());
    }

    @Test
    void unresolvableMsgEndsTheLoad() {
        // [C] parse_meth_message_type returns PARSE_ERROR_INVALID_MESSAGE.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-msg.txt", entry("POKE", 0, 0, 1, 1, "NO_SUCH_MESSAGE", "poke")));

        assertTrue(e.getMessage().contains("unrecognized MessageType NO_SUCH_MESSAGE"), e::getMessage);
    }

    @Test
    void oneBadMsgStopsLaterRecordsLoadingToo() {
        // [C] the first parser error ends the file load; later records are never read.
        String body = entry("BAD", 0, 0, 1, 1, "NO_SUCH_MESSAGE", "bad")
                + entry("GOOD", 0, 0, 1, 1, "MON_HIT", "good");

        assertThrows(DataFileParseException.class, () -> load("mixed.txt", body));
    }

    @Test
    void lowerCaseAndNumericMsgResolveAsMessageLookupByNameDoes() throws IOException {
        // [C] message_lookup_by_name matches names with my_stricmp and also takes an index:
        // 34 is MON_HIT's position in list-message.h.
        String body = entry("LOWER", 0, 0, 1, 1, "mon_hit", "lower")
                + entry("INDEX", 0, 0, 1, 1, "34", "index");
        ParseResult<BlowMethod> result = load("msg-forms.txt", body);

        assertEquals(MessageType.MSG_MON_HIT, byName(result.items(), "LOWER").getMesgT());
        assertEquals(MessageType.MSG_MON_HIT, byName(result.items(), "INDEX").getMesgT());
    }

    // ---- C truthiness of the four boolean directives ---------------------

    @Test
    void anyNonZeroValueMakesABooleanDirectiveTrue() throws IOException {
        // [C] does `val ? true : false`, so 2 is just as true as 1. The port reads the number in
        // base 0, as parser_getuint does, and tests it for non-zero.
        String body = "name:POKE\ncut:2\nstun:7\nmiss:1\nphys:1\n"
                + "msg:MON_HIT\nact:pokes {target}\ndesc:poke\n";
        ParseResult<BlowMethod> result = load("truthy.txt", body);

        BlowMethod poke = result.items().get(0);
        assertTrue(poke.isCut());
        assertTrue(poke.isStun());
    }

    // ---- Directive order and missing directives --------------------------

    @Test
    void missingDescLoadsAsCAcceptsIt() throws IOException {
        // [C] has no handler that requires desc:, so a record without one loads, with desc NULL.
        String noDesc = "name:POKE\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\nact:pokes {target}\n";
        ParseResult<BlowMethod> result = load("no-desc.txt", noDesc);

        assertEquals(1, result.items().size());
        assertEquals(List.of("pokes {target}"), result.items().get(0).getBlowMessage());
    }

    @Test
    void directivesAfterNameLoadInAnyOrderAsCReadsThem() throws IOException {
        // Each directive is its own handler in C, so stun: before cut: and desc: first are fine.
        String swapped = "name:POKE\ndesc:poke\nstun:1\ncut:0\nphys:1\nmiss:0\nact:p\nmsg:MON_HIT\n";
        ParseResult<BlowMethod> result = load("swapped.txt", swapped);

        BlowMethod poke = result.items().get(0);
        assertFalse(poke.isCut());
        assertTrue(poke.isStun());
        assertFalse(poke.isMiss());
        assertTrue(poke.isPhys());
        assertEquals(MessageType.MSG_MON_HIT, poke.getMesgT());
        assertEquals(List.of("p"), poke.getBlowMessage());
        assertEquals("poke", poke.getDesc());
    }

    @Test
    void zeroActLinesIsAccepted() throws IOException {
        // act: is a (…)* in the grammar and [C] never requires one, so a method with no action
        // string is legal - it just carries an empty message list.
        String noAct = "name:POKE\ncut:0\nstun:0\nmiss:1\nphys:1\nmsg:MON_HIT\ndesc:poke\n";
        ParseResult<BlowMethod> result = load("no-act.txt", noAct);

        assertTrue(result.items().get(0).getBlowMessage().isEmpty());
    }
}
