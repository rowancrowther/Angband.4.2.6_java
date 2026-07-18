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
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.combat.BlowMethod;
import uk.co.jackoftrades.middle.enums.MessageType;

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
 * The interesting seams here are all on the assembler side. {@code blow_methods.txt} is
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

    // ---- Happy path: the real shipped file -------------------------------

    @Test
    void cleanLoadOfTheRealFileYieldsNineteenBlowMethods() throws IOException {
        ParseResult<BlowMethod> result = new BlowMethodReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(19, result.items().size());
    }

    @Test
    void firstMethodHitHasAllEightFieldsPopulated() throws IOException {
        ParseResult<BlowMethod> result = new BlowMethodReader().parseWithResults(REAL_FILE);

        BlowMethod hit = result.items().get(0);
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
