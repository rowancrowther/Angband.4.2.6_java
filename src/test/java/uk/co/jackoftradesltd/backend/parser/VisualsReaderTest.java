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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsCycleAssembler;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsCycleParseRecord;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsFlickerAssembler;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsFlickerParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.frontend.colour.ColourCycle;
import uk.co.jackoftradesltd.frontend.colour.FlickerTable;
import uk.co.jackoftradesltd.frontend.colour.VisualsCycler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full test suite for the visuals.txt parser. The visuals file feeds two independent aggregates
 * off one grammar: a {@link VisualsCycler} (named colour cycles, keyed group -> name) and a
 * {@link FlickerTable} (colour cycles keyed by base attribute). Both flow through the same pipeline:
 * <pre>
 *   visuals.txt -&gt; VisualsLexer/VisualsGrammar -&gt; {Cycle,Flicker}ParseRecord
 *               -&gt; {Cycle,Flicker}Assembler -&gt; VisualsCycler / FlickerTable
 * </pre>
 * <p>
 * Visuals is the odd reader out in two ways. First, each assembler folds the <em>whole</em> file
 * into a single aggregate, so both are wrapped as one-element {@code List}s to satisfy
 * {@link GrammarDriver}'s list-shaped contract and unwrapped with {@code items().getFirst()}.
 * Second, the file carries <em>no</em> {@code record-count:} header, so there are no header
 * mismatch cases to pin (unlike most other readers).
 * <p>
 * The suite is split four ways:
 * <ul>
 *   <li>{@link EndToEnd} - the real shipped file and synthetic files driven through the reader,
 *       covering the happy path, the two single-half files, and the hard (fail-closed) parse
 *       errors that {@code GrammarDriver} turns into an empty result plus error messages.</li>
 *   <li>{@link ReaderAgainstC} - the reader checked clause by clause against {@code ui-visuals.c}:
 *       the shipped file's 38 cycles at their upstream step counts, the missing-file and
 *       hard-error contract, the convenience methods' {@code getFirst()} unwrap, and each place the
 *       grammar or assembler is stricter or looser than C (colour codes, the field after the name,
 *       block order, and the group, cycle and step caps C enforces).</li>
 *   <li>{@link CycleAssemblerUnit} - the cycle assembler in isolation, reaching the soft-error and
 *       grouping branches that grammar-valid input cannot (an empty colour string is impossible to
 *       lex, so it can only be injected by hand).</li>
 *   <li>{@link FlickerAssemblerUnit} - the flicker assembler in isolation, at the shallow depth the
 *       opaque {@link FlickerTable} allows.</li>
 * </ul>
 * <p>
 * <strong>Coverage ceiling.</strong> {@link FlickerTable} exposes no accessor and
 * {@code ColourCycle} no getters, so nothing here can inspect a cycle's step colours or the flicker
 * table's keyed entries. That leaves the flicker half - including the per-step colour mapping and
 * the base-attribute collision behaviour - assertable only as "one aggregate, no errors". Once the
 * designed runtime accessors land ({@code FlickerTable.stepFor} / {@code ColourCycle.stepFor}),
 * those deeper properties become testable.
 *
 * @author Rowan Crowther
 */
class VisualsReaderTest {

    private static final String REAL_FILE = "lib/gamedata/visuals.txt";

    // Shipped visuals.txt census: 28 flicker blocks; 38 cycle blocks split fancy(10) + flicker(28).
    private static final int FANCY_CYCLES = 10;
    private static final int FLICKER_GROUP_CYCLES = 28;

    // ----------------------------------------------------------------------
    // End-to-end: file text driven through VisualsReader (grammar included).
    // ----------------------------------------------------------------------

    /**
     * Reader-level tests: everything here starts from raw file text and exercises the lexer, the
     * grammar and both assemblers together, exactly as {@code MonsterDataLoader.loadVisualTables()}
     * does. Grammar-valid input can never carry a soft error (see {@link CycleAssemblerUnit}), so
     * these cases are either fully clean or hard parse failures.
     *
     * @author Rowan Crowther
     */
    @Nested
    class EndToEnd {

        @TempDir
        Path tempDir;

        /**
         * One {@code flicker:<code>:<name>} block with its {@code flicker-color:} step lines.
         */
        private static String flickerBlock(char base, String name, char... steps) {
            StringBuilder sb = new StringBuilder("flicker:").append(base).append(':').append(name).append('\n');
            for (char c : steps) {
                sb.append("flicker-color:").append(c).append('\n');
            }
            return sb.toString();
        }

        /**
         * One {@code cycle:<group>:<name>} block with its {@code cycle-color:} step lines.
         */
        private static String cycleBlock(String group, String name, char... steps) {
            StringBuilder sb = new StringBuilder("cycle:").append(group).append(':').append(name).append('\n');
            for (char c : steps) {
                sb.append("cycle-color:").append(c).append('\n');
            }
            return sb.toString();
        }

        private Path write(String name, String content) throws IOException {
            Path file = tempDir.resolve(name);
            Files.writeString(file, content);
            return file;
        }

        // ---- Happy path: the real shipped file ---------------------------

        @Test
        void realFileLoadsCyclerWithNoErrorsAsASingleAggregate() throws IOException {
            ParseResult<VisualsCycler> result = new VisualsReader().parseCyclerWithResults(REAL_FILE);

            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertEquals(1, result.items().size(), "the cycler folds the whole file into one aggregate");
        }

        @Test
        void realFileCyclerHasTheTwoShippedGroupsAtTheirExpectedSizes() throws IOException {
            VisualsCycler cycler = new VisualsReader().parseCyclerWithResults(REAL_FILE).items().getFirst();

            assertEquals(2, cycler.getKeys().size(), () -> "groups: " + cycler.getKeys());
            assertTrue(cycler.getKeys().contains("fancy"));
            assertTrue(cycler.getKeys().contains("flicker"));
            assertEquals(FANCY_CYCLES, cycler.getByGroup("fancy").size(),
                    () -> "fancy names: " + cycler.getByGroup("fancy").keySet());
            assertEquals(FLICKER_GROUP_CYCLES, cycler.getByGroup("flicker").size());
        }

        @Test
        void realFileFancyGroupContainsANamedCycleFromTheFile() throws IOException {
            VisualsCycler cycler = new VisualsReader().parseCyclerWithResults(REAL_FILE).items().getFirst();

            // cycle:fancy:rainbow is the first fancy block in the shipped file.
            assertTrue(cycler.getByGroup("fancy").containsKey("rainbow"),
                    () -> "fancy names: " + cycler.getByGroup("fancy").keySet());
        }

        @Test
        void realFileLoadsFlickerTableWithNoErrorsAsASingleAggregate() throws IOException {
            ParseResult<FlickerTable> result = new VisualsReader().parseFlickerWithResults(REAL_FILE);

            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertEquals(1, result.items().size());
            assertNotNull(result.items().getFirst());
        }

        @Test
        void neitherHalfReportsErrorsSoGameConstantsWouldPopulateBothTables() throws IOException {
            VisualsReader reader = new VisualsReader();
            ParseResult<VisualsCycler> cyclers = reader.parseCyclerWithResults(REAL_FILE);
            ParseResult<FlickerTable> flickers = reader.parseFlickerWithResults(REAL_FILE);

            // Mirrors the hasErrors() bail-out guard in GameConstants.loadVisualTables().
            assertFalse(cyclers.hasErrors() || flickers.hasErrors(),
                    () -> "cycler=" + cyclers.errors() + " flicker=" + flickers.errors());
        }

        // ---- Convenience accessors (the getFirst() unwrap) ---------------

        @Test
        void convenienceParseCyclerReturnsTheAggregateDirectly() throws IOException {
            assertNotNull(new VisualsReader().parseCycler(REAL_FILE));
        }

        @Test
        void convenienceParseFlickerReturnsTheAggregateDirectly() throws IOException {
            assertNotNull(new VisualsReader().parseFlicker(REAL_FILE));
        }

        // ---- Synthetic happy paths ---------------------------------------

        @Test
        void minimalSyntheticFileLoadsBothHalves() throws IOException {
            String body = flickerBlock('d', "dark flicker", 'd', 'D', 'R')
                    + cycleBlock("fancy", "mine", 'r', 'g', 'b');
            Path file = write("mini.txt", body);

            VisualsReader reader = new VisualsReader();
            VisualsCycler cycler = reader.parseCyclerWithResults(file.toString()).items().getFirst();
            ParseResult<FlickerTable> flicker = reader.parseFlickerWithResults(file.toString());

            assertEquals(List.of("fancy"), cycler.getKeys());
            assertTrue(cycler.getByGroup("fancy").containsKey("mine"));
            assertFalse(flicker.hasErrors(), () -> flicker.errors().toString());
            assertNotNull(flicker.items().getFirst());
        }

        @Test
        void interleavedFlickerAndCycleBlocksBothCollect() throws IOException {
            // The file rule alternates blocks freely; prove order-independence across the two halves.
            String body = cycleBlock("groupA", "one", 'r')
                    + flickerBlock('w', "white", 'w', 'W')
                    + cycleBlock("groupB", "two", 'g')
                    + flickerBlock('s', "slate", 's');
            Path file = write("mixed.txt", body);

            VisualsCycler cycler = new VisualsReader().parseCyclerWithResults(file.toString()).items().getFirst();

            assertEquals(2, cycler.getKeys().size(), () -> "groups: " + cycler.getKeys());
            assertTrue(cycler.getByGroup("groupA").containsKey("one"));
            assertTrue(cycler.getByGroup("groupB").containsKey("two"));
        }

        @Test
        void flickerOnlyFileYieldsAnEmptyCyclerButNoErrors() throws IOException {
            Path file = write("flick-only.txt", flickerBlock('d', "dark", 'd', 'R'));

            ParseResult<VisualsCycler> result = new VisualsReader().parseCyclerWithResults(file.toString());

            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertTrue(result.items().getFirst().getKeys().isEmpty(),
                    "no cycle blocks -> the cycler has no groups");
        }

        @Test
        void cycleOnlyFileYieldsAFlickerTableWithNoErrors() throws IOException {
            Path file = write("cyc-only.txt", cycleBlock("fancy", "mine", 'r', 'g'));

            ParseResult<FlickerTable> result = new VisualsReader().parseFlickerWithResults(file.toString());

            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertNotNull(result.items().getFirst(), "no flicker blocks -> an empty-but-present table");
        }

        // ---- Hard (fail-closed) parse errors: empty result + errors ------

        @Test
        void flickerBlockWithNoColourLinesIsAHardError() throws IOException {
            // flickerBlock requires (flickerColour)+ ; a bare header then EOF is a syntax error.
            Path file = write("no-flick-colour.txt", "flicker:d:dark\n");

            ParseResult<FlickerTable> result = new VisualsReader().parseFlickerWithResults(file.toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        @Test
        void cycleBlockWithNoColourLinesIsAHardError() throws IOException {
            // cycleBlock requires (cycleColour)+ likewise.
            Path file = write("no-cyc-colour.txt", "cycle:fancy:mine\n");

            ParseResult<VisualsCycler> result = new VisualsReader().parseCyclerWithResults(file.toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        @Test
        void invalidColourCodeIsAHardLexerError() throws IOException {
            // 'q' is not one of the 28 COLOUR_CODE chars, so COLOUR_CHAR cannot lex it: hard, not soft.
            Path file = write("bad-code.txt", "flicker:d:dark\nflicker-color:q\n");

            ParseResult<FlickerTable> result = new VisualsReader().parseFlickerWithResults(file.toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        @Test
        void fileWithNoBlocksIsAHardError() throws IOException {
            // The file rule is (flickerBlock | cycleBlock)+ EOF - at least one block is mandatory.
            Path file = write("empty.txt", "# just a comment\n");

            ParseResult<VisualsCycler> result = new VisualsReader().parseCyclerWithResults(file.toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }
    }

    // ----------------------------------------------------------------------
    // The reader against ui-visuals.c: what matches, and each place Java differs.
    // ----------------------------------------------------------------------

    /**
     * Checks {@link VisualsReader} clause by clause against the visuals parser in {@code ui-visuals.c}
     * ({@code visuals_file_parser_init}, {@code visuals_file_parser_run}, the four
     * {@code visuals_parse_*} handlers and {@code visuals_parse_context_convert}). Expected values are
     * taken from C: the upstream {@code visuals.txt} census, the caps C declares
     * ({@code VISUALS_GROUPS_MAX} 8, {@code VISUALS_CYCLES_MAX} 64, {@code VISUALS_STEPS_MAX} 32), the
     * "replace a cycle of the same name" rule in {@code visuals_parse_cycle}, and
     * {@code color_char_to_attr}, which reads only the first character and never fails (an unknown
     * character is white).
     * <p>
     * Tests named {@code ...HereWhereC...} pin a difference: they assert what Java does today and the
     * comment says what C does. {@link FlickerTable} has no accessor, so the flicker half's
     * step-count differences (C keeps exactly three steps per colour) cannot be pinned here.
     *
     * <p>Class ReaderAgainstC coded on 261019, commented in full on 261019.
     *
     * @author Rowan Crowther
     */
    @Nested
    class ReaderAgainstC {

        @TempDir
        Path tempDir;

        private Path write(String name, String content) throws IOException {
            Path file = tempDir.resolve(name);
            Files.writeString(file, content);
            return file;
        }

        private VisualsCycler cycler(String name, String content) throws IOException {
            return new VisualsReader().parseCyclerWithResults(write(name, content).toString()).items().getFirst();
        }

        private static String cycle(String group, String name, int steps) {
            return EndToEnd.cycleBlock(group, name, "r".repeat(steps).toCharArray());
        }

        // ---- What matches C ------------------------------------------------

        @Test
        void everyShippedCycleLoadsWithTheStepCountInTheUpstreamFile() throws IOException {
            // Counted from lib/gamedata/visuals.txt in the upstream 4.2.6 tree: ten fancy cycles, then
            // one flicker-group cycle per base colour, each with three steps.
            Map<String, Integer> fancy = Map.of("rainbow", 14, "bile", 10, "storm", 15, "hell", 13, "ice", 6,
                    "swamp", 12, "crystal", 7, "thunder", 7, "law", 4, "balance", 16);
            VisualsCycler cycler = new VisualsReader().parseCycler(REAL_FILE);

            for (Map.Entry<String, Integer> entry : fancy.entrySet()) {
                ColourCycle cycle = cycler.getByGroup("fancy").get(entry.getKey());
                assertNotNull(cycle, entry.getKey());
                assertEquals(entry.getValue(), cycle.getSize(), entry.getKey());
            }
            for (String base : "dwsorgbuDWPyRGBUpvtmYiTVIMzZ".split("")) {
                ColourCycle cycle = cycler.getByGroup("flicker").get(base);
                assertNotNull(cycle, base);
                assertEquals(3, cycle.getSize(), base);
            }
        }

        @Test
        void aCycleKeepsItsColoursInFileOrder() throws IOException {
            // cycle:fancy:rainbow begins r, R, o, M; color_char_to_attr maps those to COLOUR_RED,
            // COLOUR_L_RED, COLOUR_ORANGE and COLOUR_MUSTARD, and the steps array is filled in order.
            ColourCycle rainbow = new VisualsReader().parseCycler(REAL_FILE).getByGroup("fancy").get("rainbow");

            assertEquals(ColourEnum.COLOUR_RED, rainbow.getColour(0));
            assertEquals(ColourEnum.COLOUR_LIGHT_RED, rainbow.getColour(1));
            assertEquals(ColourEnum.COLOUR_ORANGE, rainbow.getColour(2));
            assertEquals(ColourEnum.COLOUR_MUSTARD, rainbow.getColour(3));
        }

        @Test
        void aMissingFileIsAnIOExceptionWhereCQuits() {
            // run_parse_visuals is parse_file_quit_not_found(parser, "visuals"): C quits, Java throws.
            String missing = tempDir.resolve("no-such-visuals.txt").toString();

            assertThrows(IOException.class, () -> new VisualsReader().parseCyclerWithResults(missing));
            assertThrows(IOException.class, () -> new VisualsReader().parseFlickerWithResults(missing));
        }

        @Test
        void aHardErrorEmptiesBothHalvesAsCQuitsOnAnyHandlerError() throws IOException {
            // Every visuals_parse_* error is fatal in C. Both halves parse the whole file here, so
            // both carry the error and neither yields a table.
            Path file = write("bad.txt", "cycle:fancy:mine\ncycle-color:q\n");
            VisualsReader reader = new VisualsReader();

            ParseResult<VisualsCycler> cyclers = reader.parseCyclerWithResults(file.toString());
            ParseResult<FlickerTable> flickers = reader.parseFlickerWithResults(file.toString());

            assertTrue(cyclers.items().isEmpty());
            assertTrue(cyclers.hasErrors());
            assertTrue(flickers.items().isEmpty());
            assertTrue(flickers.hasErrors());
        }

        @Test
        void theConvenienceMethodsThrowNoSuchElementOnAHardError() throws IOException {
            // parseCycler and parseFlicker unwrap with items().getFirst(); a hard failure leaves the
            // list empty, so the unwrap throws rather than returning null.
            Path file = write("bad2.txt", "cycle:fancy:mine\ncycle-color:q\n");

            assertThrows(NoSuchElementException.class, () -> new VisualsReader().parseCycler(file.toString()));
            assertThrows(NoSuchElementException.class, () -> new VisualsReader().parseFlicker(file.toString()));
        }

        @Test
        void aRepeatedCycleNameInAGroupReplacesTheEarlierOneAsInC() throws IOException {
            // visuals_parse_cycle: "If a cycle with a matching name is found, the parsed cycle
            // replaces that one". The two-step cycle is lost and the three-step one is kept.
            VisualsCycler cycler = cycler("dup.txt", cycle("fancy", "x", 2) + cycle("fancy", "x", 3));

            assertEquals(1, cycler.getByGroup("fancy").size());
            assertEquals(3, cycler.getByGroup("fancy").get("x").getSize());
        }

        @Test
        void theSameNameInTwoGroupsIsKeptApartAsInC() throws IOException {
            VisualsCycler cycler = cycler("two-groups.txt", cycle("a", "x", 2) + cycle("b", "x", 3));

            assertEquals(2, cycler.getByGroup("a").get("x").getSize());
            assertEquals(3, cycler.getByGroup("b").get("x").getSize());
        }

        @Test
        void aGroupSplitAcrossTheFileIsOneGroupAsInC() throws IOException {
            // visuals_parse_cycle finds the group by name, so a later block for "a" joins the first.
            VisualsCycler cycler = cycler("split.txt", cycle("a", "one", 1) + cycle("b", "two", 1) + cycle("a", "three", 1));

            assertEquals(2, cycler.getKeys().size());
            assertEquals(2, cycler.getByGroup("a").size());
        }

        @Test
        void aCycleOfExactlyThirtyTwoColoursLoadsAsInC() throws IOException {
            // VISUALS_STEPS_MAX is 32: the 32nd cycle-color: line is the last one C accepts.
            VisualsCycler cycler = cycler("thirty-two.txt", cycle("g", "n", 32));

            assertEquals(32, cycler.getByGroup("g").get("n").getSize());
        }

        @Test
        void blankLinesAndCommentsBetweenBlocksAreSkipped() throws IOException {
            VisualsCycler cycler = cycler("spaced.txt",
                    "# header\n\n" + cycle("a", "one", 1) + "\n# between\n\n" + cycle("a", "two", 1));

            assertEquals(2, cycler.getByGroup("a").size());
        }

        // ---- Where Java differs: the colour field ---------------------------

        @Test
        void anUnlistedColourLetterOnACycleColourIsAHardErrorHereWhereCReadsItAsWhite() throws IOException {
            // color_char_to_attr falls through to COLOUR_WHITE for a character not in color_table, so
            // "cycle-color:q" loads as a white step in C. The lexer only knows the 28 listed letters.
            ParseResult<VisualsCycler> result = new VisualsReader()
                    .parseCyclerWithResults(write("q.txt", "cycle:g:n\ncycle-color:q\n").toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        @Test
        void aColourWrittenAsAWordIsAHardErrorHereWhereCTakesItsFirstLetter() throws IOException {
            // The handlers pass only color_code[0] to color_char_to_attr, so "cycle-color:red" is the
            // letter r (red) in C. The lexer reads one letter and then meets "ed".
            ParseResult<VisualsCycler> result = new VisualsReader()
                    .parseCyclerWithResults(write("word.txt", "cycle:g:n\ncycle-color:red\n").toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        @Test
        void aTwoLetterBaseColourOnAFlickerHeaderIsAHardErrorHereWhereCTakesTheFirst() throws IOException {
            // visuals_parse_flicker reads color_code[0] only, so "flicker:dd:name" selects dark in C.
            ParseResult<FlickerTable> result = new VisualsReader().parseFlickerWithResults(
                    write("dd.txt", "flicker:dd:name\nflicker-color:d\n").toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        @Test
        void aBlankAfterAColourLetterIsAHardErrorHereWhereCReadsTheLetter() throws IOException {
            // strtok on ':' leaves "d " as the token and only its first character is read, so a
            // trailing blank is harmless in C. COLOUR_EOL takes no blanks before the newline.
            ParseResult<FlickerTable> result = new VisualsReader().parseFlickerWithResults(
                    write("trailing.txt", "flicker:d:name\nflicker-color:d \n").toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        // ---- Where Java differs: the name field -----------------------------

        @Test
        void aFieldAfterTheCycleNameBecomesPartOfTheNameHereWhereCIgnoresIt() throws IOException {
            // "cycle sym group sym name": both are tokenized on ':', so the name is "rainbow" and
            // ":junk" is dropped. Here the name field takes the rest of the line.
            VisualsCycler cycler = cycler("junk.txt", "cycle:fancy:rainbow:junk\ncycle-color:r\n");

            assertTrue(cycler.getByGroup("fancy").containsKey("rainbow:junk"));
            assertFalse(cycler.getByGroup("fancy").containsKey("rainbow"));
        }

        // ---- Where Java differs: block order --------------------------------

        @Test
        void aFlickerColourAfterAnInterveningCycleBlockIsAHardErrorHereWhereCAttachesIt() throws IOException {
            // C keeps flicker_cycle_index and flicker_color_index in the parse context, so a
            // flicker-color: line extends the last flicker: header however many cycle: lines came
            // between. flickerBlock needs its colour lines to follow the header at once.
            String body = EndToEnd.flickerBlock('d', "dark", 'd') + cycle("g", "n", 1) + "flicker-color:D\n";

            ParseResult<FlickerTable> result = new VisualsReader()
                    .parseFlickerWithResults(write("late.txt", body).toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        @Test
        void aColourLineBeforeAnyHeaderIsAHardErrorHereWhereCWritesToCycleZero() throws IOException {
            // flicker_cycle_index starts at 0, so C stores a leading flicker-color: against the dark
            // cycle. (A leading cycle-color: dereferences cycles[0], which is NULL, in C.)
            ParseResult<FlickerTable> result = new VisualsReader().parseFlickerWithResults(
                    write("early.txt", "flicker-color:d\n" + EndToEnd.flickerBlock('w', "white", 'w')).toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        @Test
        void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
            // parser_parse skips leading whitespace; the shared CommentsAndEol rules do not.
            ParseResult<VisualsCycler> result = new VisualsReader().parseCyclerWithResults(
                    write("indent.txt", "cycle:g:n\n  cycle-color:r\n").toString());

            assertTrue(result.items().isEmpty());
            assertTrue(result.hasErrors());
        }

        // ---- Where Java differs: the caps C enforces ------------------------

        @Test
        void aThirtyThirdCycleColourLoadsHereWhereCRefusesTheFile() throws IOException {
            // visuals_parse_cycle_color returns PARSE_ERROR_TOO_MANY_ENTRIES once cycle_step_index
            // reaches max_steps (32), which is fatal in C. Java stores every step.
            ParseResult<VisualsCycler> result = new VisualsReader()
                    .parseCyclerWithResults(write("thirty-three.txt", cycle("g", "n", 33)).toString());

            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertEquals(33, result.items().getFirst().getByGroup("g").get("n").getSize());
        }

        @Test
        void aNinthGroupIsKeptHereWhereCDropsItsCycles() throws IOException {
            // group_names holds VISUALS_GROUPS_MAX (8) names; a ninth distinct name gets no slot, its
            // cycles fall outside group_cycles and visuals_parse_context_convert never sees them.
            StringBuilder body = new StringBuilder();
            for (int i = 1; i <= 9; i++) {
                body.append(cycle("g" + i, "n", 1));
            }
            VisualsCycler cycler = cycler("nine-groups.txt", body.toString());

            assertEquals(9, cycler.getKeys().size());
            assertNotNull(cycler.getByGroup("g9"));
        }

        @Test
        void aSixtyFifthCycleInAGroupIsKeptHereWhereCDropsIt() throws IOException {
            // VISUALS_CYCLES_MAX is 64 per group; the 65th distinct name finds no free slot in C.
            StringBuilder body = new StringBuilder();
            for (int i = 1; i <= 65; i++) {
                body.append(cycle("g", "n" + i, 1));
            }
            VisualsCycler cycler = cycler("sixty-five.txt", body.toString());

            assertEquals(65, cycler.getByGroup("g").size());
        }

        @Test
        void aFiveHundredAndThirteenthCycleBlockLoadsHereWhereCRefusesTheFile() throws IOException {
            // max_cycles is 8 * 64 + 1 and cycles_index starts at 1, so the 513th cycle: line makes
            // visuals_parse_cycle return PARSE_ERROR_TOO_MANY_ENTRIES. Nine groups keep it clear of
            // the per-group cap.
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < 513; i++) {
                body.append(cycle("g" + (i % 9), "n" + i, 1));
            }
            ParseResult<VisualsCycler> result = new VisualsReader()
                    .parseCyclerWithResults(write("five-thirteen.txt", body.toString()).toString());

            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertEquals(9, result.items().getFirst().getKeys().size());
        }
    }

    // ----------------------------------------------------------------------
    // Cycle assembler in isolation: the branches grammar-valid input can't reach.
    // ----------------------------------------------------------------------

    /**
     * Direct {@link VisualsCycleAssembler} tests. Two of the assembler's branches are unreachable
     * through the reader: an empty colour string cannot be lexed (COLOUR_CHAR needs one code char),
     * and an unknown code char is a hard lexer error before the assembler ever runs. Injecting
     * hand-built {@link VisualsCycleParseRecord}s is the only way to cover them.
     *
     * @author Rowan Crowther
     */
    @Nested
    class CycleAssemblerUnit {

        private static VisualsCycleParseRecord record(String group, String name, List<String> colours) {
            return new VisualsCycleParseRecord(group, name, colours, 1);
        }

        @Test
        void groupsRecordsByGroupThenName() {
            List<String> errors = new ArrayList<>();
            List<VisualsCycler> out = new VisualsCycleAssembler().assemble(List.of(
                    record("g1", "a", List.of("r")),
                    record("g1", "b", List.of("g")),
                    record("g2", "c", List.of("b"))), errors, "test.txt");

            assertTrue(errors.isEmpty(), errors::toString);
            VisualsCycler cycler = out.getFirst();
            assertEquals(2, cycler.getKeys().size());
            Map<String, ?> g1 = cycler.getByGroup("g1");
            assertEquals(2, g1.size());
            assertTrue(g1.containsKey("a") && g1.containsKey("b"));
            assertTrue(cycler.getByGroup("g2").containsKey("c"));
        }

        @Test
        void emptyColourStringIsASoftErrorAndSkipsTheRecord() {
            List<String> errors = new ArrayList<>();
            // The empty entry is the only reachable route into the soft-error channel.
            List<VisualsCycler> out = new VisualsCycleAssembler()
                    .assemble(List.of(record("g", "bad", List.of("r", ""))), errors, "test.txt");

            assertEquals(1, errors.size(), errors::toString);
            assertNull(out.getFirst().getByGroup("g"),
                    "the whole record is dropped, so its group never gets created");
        }

        @Test
        void oneGoodRecordSurvivesAlongsideOneSoftFailure() {
            List<String> errors = new ArrayList<>();
            List<VisualsCycler> out = new VisualsCycleAssembler().assemble(List.of(
                    record("g", "good", List.of("r", "g")),
                    record("g", "bad", List.of(""))), errors, "test.txt");

            assertEquals(1, errors.size(), errors::toString);
            Map<String, ?> g = out.getFirst().getByGroup("g");
            assertTrue(g.containsKey("good"));
            assertFalse(g.containsKey("bad"), "the soft-failed record is skipped");
        }

        @Test
        void unknownColourCharIsASoftErrorAndSkipsTheRecord() {
            // 'q' is not one of the 28 colour codes. getColourType now returns null on a miss (was
            // COLOUR_DARK), so the assembler flags it and drops the record rather than silently
            // mapping the step to Dark.
            List<String> errors = new ArrayList<>();
            List<VisualsCycler> out = new VisualsCycleAssembler()
                    .assemble(List.of(record("g", "q", List.of("q"))), errors, "test.txt");

            assertEquals(1, errors.size(), errors::toString);
            assertNull(out.getFirst().getByGroup("g"),
                    "the record with the unknown colour is dropped, so its group is never created");
        }

        @Test
        void emptyInputYieldsASingleEmptyCycler() {
            List<String> errors = new ArrayList<>();
            List<VisualsCycler> out = new VisualsCycleAssembler().assemble(List.of(), errors, "test.txt");

            assertEquals(1, out.size());
            assertTrue(out.getFirst().getKeys().isEmpty());
            assertTrue(errors.isEmpty());
        }
    }

    // ----------------------------------------------------------------------
    // Flicker assembler in isolation: as deep as the opaque FlickerTable allows.
    // ----------------------------------------------------------------------

    /**
     * Direct {@link VisualsFlickerAssembler} tests. {@link FlickerTable} has no accessor, so these
     * can only assert structural facts - one aggregate out, never null, no throw on the awkward
     * inputs. The per-step colour mapping and the base-attribute collision outcome are the two
     * properties worth pinning but cannot be until the table gains a read path.
     *
     * @author Rowan Crowther
     */
    @Nested
    class FlickerAssemblerUnit {

        private static VisualsFlickerParseRecord record(String base, List<String> steps) {
            return new VisualsFlickerParseRecord("name", base, steps, 1);
        }

        @Test
        void producesExactlyOneTableWrappedInAList() {
            List<String> errors = new ArrayList<>();
            List<FlickerTable> out = new VisualsFlickerAssembler()
                    .assemble(List.of(record("d", List.of("d", "D", "R"))), errors, "test.txt");

            assertEquals(1, out.size());
            assertNotNull(out.getFirst());
            assertTrue(errors.isEmpty());
        }

        @Test
        void emptyInputStillYieldsAPresentTable() {
            List<String> errors = new ArrayList<>();
            List<FlickerTable> out = new VisualsFlickerAssembler().assemble(List.of(), errors, "test.txt");

            assertEquals(1, out.size());
            assertNotNull(out.getFirst());
        }

        @Test
        void twoRecordsSharingABaseAttributeDoNotThrow() {
            // Both records key on 'd'; the byAttr map collapses them (last wins). We can only assert
            // it assembles without error - which entry survives is not observable through FlickerTable.
            List<String> errors = new ArrayList<>();
            List<FlickerTable> out = new VisualsFlickerAssembler().assemble(List.of(
                    record("d", List.of("d", "R")),
                    record("d", List.of("w", "W"))), errors, "test.txt");

            assertEquals(1, out.size());
            assertNotNull(out.getFirst());
            assertTrue(errors.isEmpty());
        }
    }
}
