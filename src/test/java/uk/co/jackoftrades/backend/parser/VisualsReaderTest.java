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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.backend.parser.visuals.VisualsCycleAssembler;
import uk.co.jackoftrades.backend.parser.visuals.VisualsCycleParseRecord;
import uk.co.jackoftrades.backend.parser.visuals.VisualsFlickerAssembler;
import uk.co.jackoftrades.backend.parser.visuals.VisualsFlickerParseRecord;
import uk.co.jackoftrades.frontend.colour.FlickerTable;
import uk.co.jackoftrades.frontend.colour.VisualsCycler;

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
 * The suite is split three ways:
 * <ul>
 *   <li>{@link EndToEnd} - the real shipped file and synthetic files driven through the reader,
 *       covering the happy path, the two single-half files, and the hard (fail-closed) parse
 *       errors that {@code GrammarDriver} turns into an empty result plus error messages.</li>
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
     * grammar and both assemblers together, exactly as {@code GameConstants.loadVisualTables()}
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
                    record("g2", "c", List.of("b"))), errors);

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
                    .assemble(List.of(record("g", "bad", List.of("r", ""))), errors);

            assertEquals(1, errors.size(), errors::toString);
            assertNull(out.getFirst().getByGroup("g"),
                    "the whole record is dropped, so its group never gets created");
        }

        @Test
        void oneGoodRecordSurvivesAlongsideOneSoftFailure() {
            List<String> errors = new ArrayList<>();
            List<VisualsCycler> out = new VisualsCycleAssembler().assemble(List.of(
                    record("g", "good", List.of("r", "g")),
                    record("g", "bad", List.of(""))), errors);

            assertEquals(1, errors.size(), errors::toString);
            Map<String, ?> g = out.getFirst().getByGroup("g");
            assertTrue(g.containsKey("good"));
            assertFalse(g.containsKey("bad"), "the soft-failed record is skipped");
        }

        @Test
        void unknownColourCharIsASoftErrorAndSkipsTheRecord() {
            // 'q' is not one of the 28 colour codes. getColourType now returns null on a miss (was
            // COLOUR_TYPE_DARK), so the assembler flags it and drops the record rather than silently
            // mapping the step to Dark.
            List<String> errors = new ArrayList<>();
            List<VisualsCycler> out = new VisualsCycleAssembler()
                    .assemble(List.of(record("g", "q", List.of("q"))), errors);

            assertEquals(1, errors.size(), errors::toString);
            assertNull(out.getFirst().getByGroup("g"),
                    "the record with the unknown colour is dropped, so its group is never created");
        }

        @Test
        void emptyInputYieldsASingleEmptyCycler() {
            List<String> errors = new ArrayList<>();
            List<VisualsCycler> out = new VisualsCycleAssembler().assemble(List.of(), errors);

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
                    .assemble(List.of(record("d", List.of("d", "D", "R"))), errors);

            assertEquals(1, out.size());
            assertNotNull(out.getFirst());
            assertTrue(errors.isEmpty());
        }

        @Test
        void emptyInputStillYieldsAPresentTable() {
            List<String> errors = new ArrayList<>();
            List<FlickerTable> out = new VisualsFlickerAssembler().assemble(List.of(), errors);

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
                    record("d", List.of("w", "W"))), errors);

            assertEquals(1, out.size());
            assertNotNull(out.getFirst());
            assertTrue(errors.isEmpty());
        }
    }
}
