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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsCycleAssembler;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsCycleParseRecord;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsFlickerAssembler;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsFlickerParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
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
 *       covering the happy path, the two single-half files, and the parse errors that
 *       {@code GrammarDriver} throws as a {@code ParseCancellationException}.</li>
 *   <li>{@link ReaderAgainstC} - the reader checked clause by clause against {@code ui-visuals.c}:
 *       the shipped file's 38 cycles at their upstream step counts, the missing-file and
 *       hard-error contract, the convenience methods' {@code getFirst()} unwrap, the colour field
 *       (first character only, unknown is white, a blank is dark, empty is refused), and each place
 *       the grammar or assembler is stricter or looser than C (the field after the name, block
 *       order, and the group, cycle and step caps C enforces).</li>
 *   <li>{@link CycleAssemblerUnit} - the cycle assembler in isolation, reaching the refusal and
 *       grouping branches that grammar-valid input cannot (an empty colour string is impossible to
 *       lex, so it can only be injected by hand).</li>
 *   <li>{@link FlickerAssemblerUnit} - the flicker assembler in isolation, at the shallow depth the
 *       opaque {@link FlickerTable} allows.</li>
 * </ul>
 * <p>
 * <strong>Reading the flicker table.</strong> {@link FlickerAssemblerUnit} reads the table's
 * {@code byAttr} map by reflection and each cycle's steps through {@code ColourCycle.getSteps()},
 * which reaches the per-step colour mapping, the three-step padding and the base-attribute
 * collision behaviour. {@link FlickerTable#getAttr} is the read path now; its frame wrap and
 * absent-key rule are pinned in {@code FlickerTableTest}, so the reflection here could be replaced
 * by {@code getAttr} calls without losing coverage.
 *
 * <p>Test class VisualsReaderTest coded before 261105, updated on 261105 for the removal of the
 * soft-error path, updated on 261108 for the lenient colour field (COLOUR_LINE in the lexer, first
 * character only in both assemblers),
 * updated on 261108 for the `FlickerTable.getAttr` read path (Coverage ceiling note replaced).
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
     * does. Grammar-valid input can never reach an assembler refusal (see
     * {@link CycleAssemblerUnit}), so these cases are either fully clean or parse failures.
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

            assertEquals(1, result.items().size());
            assertNotNull(result.items().getFirst());
        }

        @Test
        void neitherHalfReportsErrorsSoGameConstantsWouldPopulateBothTables() throws IOException {
            VisualsReader reader = new VisualsReader();

            // Both halves load the real file cleanly, so MonsterDataLoader.loadVisualTables() can
            // register both tables.
            assertDoesNotThrow(() -> reader.parseCyclerWithResults(REAL_FILE));
            assertDoesNotThrow(() -> reader.parseFlickerWithResults(REAL_FILE));
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
        void flickerOnlyFileStopsTheCyclerLoadAsInC() throws IOException {
            // No cycle blocks means no groups: visuals_cycler_new(0) returns NULL, so
            // visuals_file_parser_finish answers PARSE_ERROR_INTERNAL.
            Path file = write("flick-only.txt", flickerBlock('d', "dark", 'd', 'R'));

            assertThrows(DataFileParseException.class,
                    () -> new VisualsReader().parseCyclerWithResults(file.toString()));
        }

        @Test
        void cycleOnlyFileYieldsAFlickerTableWithNoErrors() throws IOException {
            Path file = write("cyc-only.txt", cycleBlock("fancy", "mine", 'r', 'g'));

            ParseResult<FlickerTable> result = new VisualsReader().parseFlickerWithResults(file.toString());

            assertNotNull(result.items().getFirst(), "no flicker blocks -> an empty-but-present table");
        }

        // ---- Parse errors: the load throws ---------------------------------

        @Test
        void flickerBlockWithNoColourLinesIsAHardError() throws IOException {
            // flickerBlock requires (flickerColour)+ ; a bare header then EOF is a syntax error.
            Path file = write("no-flick-colour.txt", "flicker:d:dark\n");

            assertThrows(ParseCancellationException.class, () -> new VisualsReader().parseFlickerWithResults(file.toString()));

        }

        @Test
        void cycleBlockWithNoColourLinesIsAHardError() throws IOException {
            // cycleBlock requires (cycleColour)+ likewise.
            Path file = write("no-cyc-colour.txt", "cycle:fancy:mine\n");

            assertThrows(ParseCancellationException.class, () -> new VisualsReader().parseCyclerWithResults(file.toString()));

        }

        @Test
        void anUnlistedColourCodeIsNotALexerError() throws IOException {
            // 'q' is not one of the 28 colour codes, but COLOUR_LINE takes any value up to the colon or
            // the end of the line, so it reaches the assembler, which reads it as white as C does.
            Path file = write("bad-code.txt", "flicker:d:dark\nflicker-color:q\n");

            ParseResult<FlickerTable> result = assertDoesNotThrow(
                    () -> new VisualsReader().parseFlickerWithResults(file.toString()));

            assertEquals(1, result.items().size());
        }

        @Test
        void anEmptyColourValueIsAHardError() throws IOException {
            // COLOUR_LINE needs at least one character, matching C's strlen(color_code) == 0 refusal.
            Path file = write("empty-code.txt", "flicker:d:dark\nflicker-color:\n");

            assertThrows(ParseCancellationException.class, () -> new VisualsReader().parseFlickerWithResults(file.toString()));

        }

        @Test
        void fileWithNoBlocksIsAHardError() throws IOException {
            // The file rule is (flickerBlock | cycleBlock)+ EOF - at least one block is mandatory.
            Path file = write("empty.txt", "# just a comment\n");

            assertThrows(ParseCancellationException.class, () -> new VisualsReader().parseCyclerWithResults(file.toString()));

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
     * <p>Class ReaderAgainstC coded on 261019, commented in full on 261019, colour-field tests
     * rewritten on 261108 once the lexer stopped refusing unlisted colours.
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
            Path file = write("bad.txt", "cycle:fancy:mine\ncycle-color:\n");
            VisualsReader reader = new VisualsReader();

            assertThrows(ParseCancellationException.class, () -> reader.parseCyclerWithResults(file.toString()));
            assertThrows(ParseCancellationException.class, () -> reader.parseFlickerWithResults(file.toString()));

        }

        @Test
        void theConvenienceMethodsPassTheParseErrorStraightThrough() throws IOException {
            // parseCycler and parseFlicker unwrap with items().getFirst(), but the parse error is
            // thrown before the unwrap, so it reaches the caller unchanged.
            Path file = write("bad2.txt", "cycle:fancy:mine\ncycle-color:\n");

            assertThrows(ParseCancellationException.class, () -> new VisualsReader().parseCycler(file.toString()));
            assertThrows(ParseCancellationException.class, () -> new VisualsReader().parseFlicker(file.toString()));
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

        // ---- The colour field: first character only, and it never fails ----

        /**
         * The three steps stored for {@code base} after parsing {@code content} as a flicker file.
         * {@link FlickerTable} has no accessor yet, so its private {@code byAttr} map is read by
         * reflection, as {@link FlickerAssemblerUnit} does.
         */
        @SuppressWarnings("unchecked")
        private List<ColourEnum> flickerSteps(String name, String content, ColourEnum base) throws IOException {
            FlickerTable table = new VisualsReader().parseFlickerWithResults(write(name, content).toString())
                    .items().getFirst();
            try {
                java.lang.reflect.Field f = FlickerTable.class.getDeclaredField("byAttr");
                f.setAccessible(true);
                ColourCycle cycle = ((Map<ColourEnum, ColourCycle>) f.get(table)).get(base);
                assertNotNull(cycle, "no cycle for " + base);
                return List.copyOf(cycle.getSteps());
            } catch (ReflectiveOperationException e) {
                throw new AssertionError("FlickerTable.byAttr not readable", e);
            }
        }

        @Test
        void anUnlistedColourLetterOnACycleColourReadsAsWhiteAsInC() throws IOException {
            // color_char_to_attr falls through to COLOUR_WHITE for a character not in color_table, so
            // "cycle-color:q" loads as a white step.
            VisualsCycler cycler = cycler("q.txt", "cycle:g:n\ncycle-color:q\n");

            assertEquals(ColourEnum.COLOUR_WHITE, cycler.getByGroup("g").get("n").getColour(0));
        }

        @Test
        void aColourWrittenAsAWordIsItsFirstLetterAsInC() throws IOException {
            // The handlers pass only color_code[0] to color_char_to_attr, so "red" is r (red), "Red" is
            // R (light red, not the colour named Red) and "Mauve" is M (mustard).
            VisualsCycler cycler = cycler("word.txt",
                    "cycle:g:n\ncycle-color:red\ncycle-color:Red\ncycle-color:Mauve\n");

            ColourCycle cycle = cycler.getByGroup("g").get("n");
            assertEquals(ColourEnum.COLOUR_RED, cycle.getColour(0));
            assertEquals(ColourEnum.COLOUR_LIGHT_RED, cycle.getColour(1));
            assertEquals(ColourEnum.COLOUR_MUSTARD, cycle.getColour(2));
        }

        @Test
        void aColourWrittenAsAWordOnAFlickerStepIsItsFirstLetterAsInC() throws IOException {
            List<ColourEnum> steps = flickerSteps("word-step.txt",
                    "flicker:d:name\nflicker-color:Red\nflicker-color:Mauve\nflicker-color:q\n",
                    ColourEnum.COLOUR_DARK);

            assertEquals(List.of(ColourEnum.COLOUR_LIGHT_RED, ColourEnum.COLOUR_MUSTARD, ColourEnum.COLOUR_WHITE),
                    steps);
        }

        @Test
        void aMultiLetterBaseColourOnAFlickerHeaderIsItsFirstLetterAsInC() throws IOException {
            // visuals_parse_flicker reads color_code[0] only, so "flicker:dd:name" selects dark and
            // "flicker:Red:name" selects R (light red).
            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK),
                    flickerSteps("dd.txt", "flicker:dd:name\nflicker-color:r\n", ColourEnum.COLOUR_DARK));
            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK),
                    flickerSteps("red-key.txt", "flicker:Red:name\nflicker-color:r\n", ColourEnum.COLOUR_LIGHT_RED));
        }

        @Test
        void anUnlistedBaseColourOnAFlickerHeaderKeysTheTableByWhiteAsInC() throws IOException {
            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK),
                    flickerSteps("q-key.txt", "flicker:q:name\nflicker-color:r\n", ColourEnum.COLOUR_WHITE));
        }

        @Test
        void aBlankAfterAColourLetterIsHarmlessAsInC() throws IOException {
            // strtok on ':' leaves "r " as the token and only its first character is read, so a
            // trailing blank changes nothing. COLOUR_LINE keeps the blank; the assembler ignores it.
            VisualsCycler cycler = cycler("trailing.txt", "cycle:g:n\ncycle-color:r \n");
            List<ColourEnum> flicker = flickerSteps("trailing-flicker.txt",
                    "flicker:d:name\nflicker-color:r \n", ColourEnum.COLOUR_DARK);

            assertEquals(ColourEnum.COLOUR_RED, cycler.getByGroup("g").get("n").getColour(0));
            assertEquals(ColourEnum.COLOUR_RED, flicker.getFirst());
        }

        @Test
        void aLoneBlankIsDarkAsInC() throws IOException {
            // color_char_to_attr answers COLOUR_DARK for ' ', and the handlers refuse only an empty value.
            VisualsCycler cycler = cycler("blank.txt", "cycle:g:n\ncycle-color: \n");

            assertEquals(ColourEnum.COLOUR_DARK, cycler.getByGroup("g").get("n").getColour(0));
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

            assertThrows(ParseCancellationException.class, () -> new VisualsReader()
                    .parseFlickerWithResults(write("late.txt", body).toString()));

        }

        @Test
        void aColourLineBeforeAnyHeaderIsAHardErrorHereWhereCWritesToCycleZero() throws IOException {
            // flicker_cycle_index starts at 0, so C stores a leading flicker-color: against the dark
            // cycle. (A leading cycle-color: dereferences cycles[0], which is NULL, in C.)
            assertThrows(ParseCancellationException.class, () -> new VisualsReader().parseFlickerWithResults(
                    write("early.txt", "flicker-color:d\n" + EndToEnd.flickerBlock('w', "white", 'w')).toString()));

        }

        @Test
        void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
            // parser_parse skips leading whitespace; the shared CommentsAndEol rules do not.
            assertThrows(ParseCancellationException.class, () -> new VisualsReader().parseCyclerWithResults(
                    write("indent.txt", "cycle:g:n\n  cycle-color:r\n").toString()));

        }

        // ---- The caps C enforces (matched by VisualsCycleAssembler from 261030) ----

        @Test
        void aThirtyThirdCycleColourStopsTheLoadAsInC() throws IOException {
            // visuals_parse_cycle_color returns PARSE_ERROR_TOO_MANY_ENTRIES once cycle_step_index
            // reaches max_steps (32), which is fatal in C.
            Path file = write("thirty-three.txt", cycle("g", "n", 33));

            assertThrows(DataFileParseException.class,
                    () -> new VisualsReader().parseCyclerWithResults(file.toString()));
        }

        @Test
        void aNinthGroupIsDroppedAsInC() throws IOException {
            // group_names holds VISUALS_GROUPS_MAX (8) names; a ninth distinct name gets no slot, its
            // cycles fall outside group_cycles and visuals_parse_context_convert never sees them.
            StringBuilder body = new StringBuilder();
            for (int i = 1; i <= 9; i++) {
                body.append(cycle("g" + i, "n", 1));
            }
            VisualsCycler cycler = cycler("nine-groups.txt", body.toString());

            assertEquals(8, cycler.getKeys().size());
            assertNull(cycler.getByGroup("g9"));
        }

        @Test
        void aSixtyFifthCycleInAGroupIsDroppedAsInC() throws IOException {
            // VISUALS_CYCLES_MAX is 64 per group; the 65th distinct name finds no free slot in C.
            StringBuilder body = new StringBuilder();
            for (int i = 1; i <= 65; i++) {
                body.append(cycle("g", "n" + i, 1));
            }
            VisualsCycler cycler = cycler("sixty-five.txt", body.toString());

            assertEquals(64, cycler.getByGroup("g").size());
            assertFalse(cycler.getByGroup("g").containsKey("n65"));
        }

        @Test
        void aFiveHundredAndThirteenthCycleBlockStopsTheLoadAsInC() throws IOException {
            // max_cycles is 8 * 64 + 1 and cycles_index starts at 1, so the 513th cycle: line makes
            // visuals_parse_cycle return PARSE_ERROR_TOO_MANY_ENTRIES. Every line counts, dropped or
            // not, so the ninth group here does not keep the count down.
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < 513; i++) {
                body.append(cycle("g" + (i % 9), "n" + i, 1));
            }
            Path file = write("five-thirteen.txt", body.toString());

            assertThrows(DataFileParseException.class,
                    () -> new VisualsReader().parseCyclerWithResults(file.toString()));
        }
    }

    // ----------------------------------------------------------------------
    // Cycle assembler in isolation: the branches grammar-valid input can't reach.
    // ----------------------------------------------------------------------

    /**
     * Direct {@link VisualsCycleAssembler} tests. One of the assembler's branches is unreachable
     * through the reader: an empty colour string cannot be lexed (COLOUR_LINE needs at least one
     * character). Injecting hand-built {@link VisualsCycleParseRecord}s is the only way to cover it,
     * and the same way reaches colours the reader would pass through whole. Every assembler
     * error stops the load, as in C; the full C-derived set is in {@code VisualsCycleAssemblerTest}.
     *
     * <p>Class CycleAssemblerUnit coded before 261030, updated on 261030 when the assembler's soft
     * errors became fatal, commented in full on 261030.
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
            List<VisualsCycler> out = new VisualsCycleAssembler().assemble(List.of(
                    record("g1", "a", List.of("r")),
                    record("g1", "b", List.of("g")),
                    record("g2", "c", List.of("b"))), "test.txt");

            VisualsCycler cycler = out.getFirst();
            assertEquals(2, cycler.getKeys().size());
            Map<String, ?> g1 = cycler.getByGroup("g1");
            assertEquals(2, g1.size());
            assertTrue(g1.containsKey("a") && g1.containsKey("b"));
            assertTrue(cycler.getByGroup("g2").containsKey("c"));
        }

        @Test
        void emptyColourStringStopsTheLoadAsInC() {
            // visuals_parse_cycle_color: strlen(color_code) == 0 is PARSE_ERROR_INVALID_COLOR.

            assertThrows(DataFileParseException.class, () -> new VisualsCycleAssembler()
                    .assemble(List.of(record("g", "bad", List.of("r", ""))), "test.txt"));
        }

        @Test
        void oneBadRecordStopsTheLoadEvenAlongsideAGoodOne() {
            // C's first handler error ends the whole file: no partial cycler comes back.

            assertThrows(DataFileParseException.class, () -> new VisualsCycleAssembler().assemble(List.of(
                    record("g", "good", List.of("r", "g")),
                    record("g", "bad", List.of(""))), "test.txt"));
        }

        @Test
        void unknownColourCharIsReadAsWhiteAsInC() {
            // 'q' is not one of the 28 colour codes; color_char_to_attr falls through to
            // COLOUR_WHITE ("we assume white").
            List<VisualsCycler> out = new VisualsCycleAssembler()
                    .assemble(List.of(record("g", "q", List.of("q"))), "test.txt");

            assertEquals(ColourEnum.COLOUR_WHITE, out.getFirst().getByGroup("g").get("q").getColour(0));
        }

        @Test
        void emptyInputStopsTheLoadAsInC() {
            // visuals_cycler_new(0) returns NULL, so visuals_file_parser_finish answers
            // PARSE_ERROR_INTERNAL.

            assertThrows(DataFileParseException.class,
                    () -> new VisualsCycleAssembler().assemble(List.of(), "test.txt"));
        }
    }

    // ----------------------------------------------------------------------
    // Flicker assembler in isolation, read back through FlickerTable's private map.
    // ----------------------------------------------------------------------

    /**
     * Direct {@link VisualsFlickerAssembler} tests. {@link FlickerTable} has no accessor yet, so
     * {@link #cycleFor} reads its private {@code byAttr} map by reflection. Expected values come
     * from C's {@code ui-visuals.c}: the table is three steps wide ({@code ui_visuals_module_init()}),
     * {@code visuals_flicker_set_color()} drops a step at index 3 or above, an unwritten slot stays
     * zero ({@code COLOUR_DARK}), and {@code visuals_parse_flicker()} resets only the step index, so a
     * repeated base colour is written over step by step. Colour codes resolve as
     * {@code color_char_to_attr()} ({@code z-color.c}) does: unknown is white, space is dark.
     *
     * @author Rowan Crowther
     */
    @Nested
    class FlickerAssemblerUnit {

        private static VisualsFlickerParseRecord record(String base, List<String> steps) {
            return new VisualsFlickerParseRecord("name", base, steps, 1);
        }

        private static FlickerTable assemble(VisualsFlickerParseRecord... records) {
            List<FlickerTable> out = new VisualsFlickerAssembler().assemble(List.of(records), "test.txt");
            assertEquals(1, out.size());
            return out.getFirst();
        }

        @SuppressWarnings("unchecked")
        private static Map<ColourEnum, ColourCycle> byAttr(FlickerTable table) {
            try {
                java.lang.reflect.Field f = FlickerTable.class.getDeclaredField("byAttr");
                f.setAccessible(true);
                return (Map<ColourEnum, ColourCycle>) f.get(table);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError("FlickerTable.byAttr not readable", e);
            }
        }

        private static List<ColourEnum> cycleFor(FlickerTable table, ColourEnum base) {
            ColourCycle cycle = byAttr(table).get(base);
            assertNotNull(cycle, "no cycle for " + base);
            return List.copyOf(cycle.getSteps());
        }

        @Test
        void threeStepsLoadInOrder() {
            FlickerTable t = assemble(record("r", List.of("r", "o", "y")));

            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void aFourthStepIsDroppedAsCSetColorIgnoresIndexThree() {
            FlickerTable t = assemble(record("r", List.of("r", "o", "y", "w")));

            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void manyStepsStillGiveExactlyThree() {
            FlickerTable t = assemble(record("b", List.of("b", "B", "g", "G", "v", "V")));

            assertEquals(List.of(ColourEnum.COLOUR_BLUE, ColourEnum.COLOUR_LIGHT_BLUE, ColourEnum.COLOUR_GREEN),
                    cycleFor(t, ColourEnum.COLOUR_BLUE));
        }

        @Test
        void twoStepsArePaddedWithDark() {
            FlickerTable t = assemble(record("r", List.of("r", "o")));

            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_DARK),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void oneStepIsPaddedWithTwoDarks() {
            FlickerTable t = assemble(record("r", List.of("y")));

            assertEquals(List.of(ColourEnum.COLOUR_YELLOW, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void noStepsGivesAnAllDarkCycleAsCsZeroedRowDoes() {
            // Unreachable through the grammar (flickerColour is '+'), but C's row is all zero here.
            FlickerTable t = assemble(record("r", List.of()));

            assertEquals(List.of(ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void aRepeatedBaseWithFewerStepsKeepsTheEarlierTail() {
            FlickerTable t = assemble(record("r", List.of("r", "o", "y")),
                    record("r", List.of("g")));

            assertEquals(List.of(ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void aRepeatedBaseOverAPaddedCycleOverwritesTheDarkPadding() {
            FlickerTable t = assemble(record("r", List.of("r")),
                    record("r", List.of("g", "b")));

            assertEquals(List.of(ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_BLUE, ColourEnum.COLOUR_DARK),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void aRepeatedBaseWithFourStepsStillStopsAtThree() {
            FlickerTable t = assemble(record("r", List.of("r", "o", "y")),
                    record("r", List.of("g", "b", "v", "w")));

            assertEquals(List.of(ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_BLUE, ColourEnum.COLOUR_VIOLET),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void aThirdBlockForTheSameBaseMergesOnceMore() {
            FlickerTable t = assemble(record("r", List.of("r", "o", "y")),
                    record("r", List.of("g", "b")),
                    record("r", List.of("w")));

            assertEquals(List.of(ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_BLUE, ColourEnum.COLOUR_YELLOW),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void differentBasesDoNotInterfere() {
            FlickerTable t = assemble(record("r", List.of("r", "o", "y")),
                    record("b", List.of("g")));

            assertEquals(2, byAttr(t).size());
            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW),
                    cycleFor(t, ColourEnum.COLOUR_RED));
            assertEquals(List.of(ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK),
                    cycleFor(t, ColourEnum.COLOUR_BLUE));
        }

        @Test
        void caseMattersForTheBaseCode() {
            // 'b' is Blue and 'B' is Light Blue in C's color_table: two separate rows.
            FlickerTable t = assemble(record("b", List.of("b")),
                    record("B", List.of("B")));

            assertEquals(ColourEnum.COLOUR_BLUE, cycleFor(t, ColourEnum.COLOUR_BLUE).getFirst());
            assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, cycleFor(t, ColourEnum.COLOUR_LIGHT_BLUE).getFirst());
        }

        @Test
        void anUnknownStepCodeBecomesWhiteAsInColorCharToAttr() {
            FlickerTable t = assemble(record("r", List.of("x", "r", "o")));

            assertEquals(List.of(ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_RED, ColourEnum.COLOUR_ORANGE),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void anUnknownBaseCodeKeysOnWhiteAsInColorCharToAttr() {
            FlickerTable t = assemble(record("x", List.of("r", "o", "y")));

            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW),
                    cycleFor(t, ColourEnum.COLOUR_WHITE));
        }

        @Test
        void aSpaceStepCodeIsDarkAsInColorCharToAttr() {
            FlickerTable t = assemble(record("r", List.of(" ", "r", "o")));

            assertEquals(List.of(ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_RED, ColourEnum.COLOUR_ORANGE),
                    cycleFor(t, ColourEnum.COLOUR_RED));
        }

        @Test
        void realFileGivesTwentyEightThreeStepCycles() throws IOException {
            List<FlickerTable> tables = new ArrayList<>();
            List<VisualsFlickerParseRecord> records = new ArrayList<>();
            for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
                if (line.startsWith("flicker:")) {
                    records.add(record(line.substring(8, 9), new ArrayList<>()));
                } else if (line.startsWith("flicker-color:")) {
                    records.getLast().colours().add(line.substring(14, 15));
                }
            }
            tables.addAll(new VisualsFlickerAssembler().assemble(records, REAL_FILE));

            Map<ColourEnum, ColourCycle> map = byAttr(tables.getFirst());
            assertEquals(FLICKER_GROUP_CYCLES, map.size());
            map.values().forEach(c -> assertEquals(3, c.getSize()));
        }

        @Test
        void realFileDarkAndWhiteBlocksLoadTheirShippedSteps() throws IOException {
            // visuals.txt: flicker:d -> d,D,R and flicker:w -> w,W,B; C writes each into its
            // colors_per_cycle-3 row in order.
            FlickerTable t = new VisualsReader().parseFlicker(REAL_FILE);

            assertEquals(List.of(ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_LIGHT_DARK, ColourEnum.COLOUR_LIGHT_RED),
                    cycleFor(t, ColourEnum.COLOUR_DARK));
            assertEquals(List.of(ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_LIGHT_SLATE, ColourEnum.COLOUR_LIGHT_BLUE),
                    cycleFor(t, ColourEnum.COLOUR_WHITE));
        }

        @Test
        void realFileGivesNoCycleForShade() throws IOException {
            // No flicker: line can name Shade (' ' resolves to Dark first), so its C row is never written.
            FlickerTable t = new VisualsReader().parseFlicker(REAL_FILE);

            assertFalse(byAttr(t).containsKey(ColourEnum.COLOUR_SHADE));
        }

        @Test
        void anUnknownBaseAndAWhiteBaseShareOneRow() {
            // color_char_to_attr gives COLOUR_WHITE for both 'x' and 'w', so C's second block
            // writes over row 1 from step 0.
            FlickerTable t = assemble(record("x", List.of("r", "o", "y")),
                    record("w", List.of("g")));

            assertEquals(1, byAttr(t).size());
            assertEquals(List.of(ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW),
                    cycleFor(t, ColourEnum.COLOUR_WHITE));
        }

        @Test
        void aSpaceBaseCodeKeysOnDarkAsInColorCharToAttr() {
            FlickerTable t = assemble(record(" ", List.of("r", "o", "y")));

            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW),
                    cycleFor(t, ColourEnum.COLOUR_DARK));
        }

        @Test
        void producesExactlyOneTableWrappedInAList() {
            List<FlickerTable> out = new VisualsFlickerAssembler()
                    .assemble(List.of(record("d", List.of("d", "D", "R"))), "test.txt");

            assertEquals(1, out.size());
            assertNotNull(out.getFirst());
        }

        @Test
        void emptyInputStillYieldsAPresentTable() {
            List<FlickerTable> out = new VisualsFlickerAssembler().assemble(List.of(), "test.txt");

            assertEquals(1, out.size());
            assertNotNull(out.getFirst());
        }

        @Test
        void twoRecordsSharingABaseAttributeDoNotThrow() {
            // Both records key on 'd'. C writes the second over the first step by step, so the
            // padded third step of the first block survives: [d,R,dark] then w,W gives [w,W,dark].
            FlickerTable t = assemble(record("d", List.of("d", "R")),
                    record("d", List.of("w", "W")));

            assertEquals(List.of(ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_LIGHT_SLATE, ColourEnum.COLOUR_DARK),
                    cycleFor(t, ColourEnum.COLOUR_DARK));
        }
    }
}
