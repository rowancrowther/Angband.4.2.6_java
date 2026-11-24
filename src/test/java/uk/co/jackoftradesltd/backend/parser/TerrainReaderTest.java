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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link TerrainReader} (grammar-suite reader track R4),
 * driving {@code TerrainFeatureLexer}/{@code TerrainFeatureGrammar} through the
 * shared {@link GrammarDriver} into {@link Feature} objects.
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/terrain.txt}
 * (25 records); a clean load is itself the assertion that every {@code code:}/{@code mimic:}/
 * {@code flags:}/{@code resist-flag:} resolved and every {@code graphics:} tokenised - including
 * the {@code :}-glyph rubble terrains that the {@code AngbandDisplayCharacter} glyph/separator
 * split now handles. The remaining tests each inject a single defect into a minimal fixture, one
 * per fatal branch in {@link uk.co.jackoftradesltd.backend.parser.terrainfeature.TerrainFeatureAssembler},
 * plus the hard "missing record-count" grammar error and a soft record-count mismatch.
 *
 * <p>An assembler defect is fatal, as every {@code parse_feat_*} error is in C: the
 * {@link DataFileParseException} comes straight out of {@code parseWithResults}, nothing loads, and
 * a later bad record is never reached. A hard grammar error fails the whole file closed (empty items,
 * errors carried).
 *
 * <p>Assembler-branch tests rewritten on 261030 for the fatal-error model.
 *
 * @author Rowan Crowther
 */
class TerrainReaderTest {

    private static final String REAL_FILE = "lib/gamedata/terrain.txt";

    /**
     * A minimal, valid terrain block: the three mandatory header directives only
     * ({@code code}/{@code name}/{@code graphics}), resolving to {@code FEAT_FLOOR}.
     */
    private static final String VALID_RECORD =
            "code:FLOOR\nname:open floor\ngraphics:.:w\n";

    @TempDir
    Path tempDir;

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    // ---- Happy path -------------------------------------------------------

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAll25Features() throws IOException {
        ParseResult<Feature> result = new TerrainReader().parseWithResults(REAL_FILE);

        assertEquals(25, result.items().size(), () -> "loaded: " + result.items().size());

        // Spot-check the first record and a flag-bearing one via the getters Feature exposes.
        Feature none = result.items().get(0);
        assertEquals(TerrainFlags.FEAT_NONE, none.getCodeFlags());
        assertEquals("unknown grid", none.getName());
        assertTrue(result.items().get(1).isFloor(), "second record (open floor) should carry TF_FLOOR");
    }

    /**
     * Regression guard for the {@code AngbandDisplayCharacter} glyph/separator fix: a terrain whose
     * display glyph is itself a colon ({@code graphics:::W}, as RUBBLE/PASS_RUBBLE ship) must
     * tokenise - glyph {@code :}, separator {@code :}, colour {@code W} - and load cleanly. Before
     * the fix this produced a {@code token recognition error at ':'} on every such line.
     */
    @Test
    void colonGlyphTerrainTokenisesAndLoads() throws IOException {
        String path = tempFile("colon-glyph.txt",
                "code:RUBBLE\nname:pile of rubble\ngraphics:::W\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertEquals(TerrainFlags.FEAT_RUBBLE, result.items().get(0).getCodeFlags());
    }

    // ---- Hard grammar error (fail closed) --------------------------------

    // ---- Soft record-count mismatch --------------------------------------

    // ---- One test per assembler fatal branch -----------------------------

    /** Assert the load throws and the message names each fragment. */
    private void assertFatal(String name, String content, String... fragments) throws IOException {
        String path = tempFile(name, content);
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new TerrainReader().parseWithResults(path));
        for (String fragment : fragments) {
            assertTrue(e.getMessage().contains(fragment), e::getMessage);
        }
    }

    /** C: {@code lookup_feat_code} returns -1 and {@code parse_feat_code} fails with {@code PARSE_ERROR_OUT_OF_BOUNDS}. */
    @Test
    void unknownTerrainCodeIsFatal() throws IOException {
        assertFatal("bad-code.txt", "code:NOTACODE\nname:x\ngraphics:.:w\n",
                "illegal terrain feature value: NOTACODE", "line: 1");
    }

    /** C: the same lookup in {@code parse_feat_mimic}, so the same error. */
    @Test
    void unknownMimicCodeIsFatal() throws IOException {
        assertFatal("bad-mimic.txt",
                "code:FLOOR\nname:open floor\ngraphics:.:w\nmimic:NOTAMIMIC\n",
                "illegal mimic value: NOTAMIMIC");
    }

    /** C: {@code grab_flag} fails and {@code parse_feat_flags} returns {@code PARSE_ERROR_INVALID_FLAG}. */
    @Test
    void unknownTerrainFlagIsFatal() throws IOException {
        assertFatal("bad-flag.txt",
                "code:FLOOR\nname:open floor\ngraphics:.:w\nflags:NOTAFLAG\n",
                "illegal terrain flag value: NOTAFLAG");
    }

    /** C: {@code lookup_flag} returns {@code FLAG_END} and {@code parse_feat_resist_flag} returns {@code PARSE_ERROR_INVALID_FLAG}. */
    @Test
    void unknownResistFlagIsFatal() throws IOException {
        assertFatal("bad-resist.txt",
                "code:FLOOR\nname:open floor\ngraphics:.:w\nresist-flag:NOTARESIST\n",
                "illegal monster race flag value: NOTARESIST");
    }

    /** C: {@code lookup_flag} starts at {@code FLAG_START} (1), so the {@code NONE} row is never matched. */
    @Test
    void resistFlagNoneIsFatal() throws IOException {
        assertFatal("none-resist.txt",
                "code:FLOOR\nname:open floor\ngraphics:.:w\nresist-flag:NONE\n",
                "sentinel monster race flag", "NONE");
    }

    /**
     * Accepted deviation (261027): C's {@code strtoul} result wraps into the 32-bit field, while the
     * port clamps a value above the {@code int} range to {@link Integer#MAX_VALUE}. It loads.
     */
    @Test
    void overflowingPriorityClampsToIntMax() throws IOException {
        String path = tempFile("big-priority.txt",
                "code:FLOOR\nname:open floor\ngraphics:.:w\npriority:99999999999999999999\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertEquals(Integer.MAX_VALUE, result.items().get(0).getPriority());
    }

    /** The clamped value is far above {@code DIGGING_MAX}, so C's {@code PARSE_ERROR_OUT_OF_BOUNDS} still applies. */
    @Test
    void overflowingDiggingIsOutOfRangeAndFatal() throws IOException {
        assertFatal("bad-digging.txt",
                "code:FLOOR\nname:open floor\ngraphics:.:w\ndigging:99999999999999999999\n",
                "out of range digging");
    }

    /** C: {@code parse_feat_digging} refuses anything outside {@code DIGGING_RUBBLE + 1} to {@code DIGGING_MAX}. */
    @Test
    void diggingZeroIsFatal() throws IOException {
        assertFatal("zero-digging.txt",
                "code:FLOOR\nname:open floor\ngraphics:.:w\ndigging:0\n",
                "out of range digging");
    }

    /** C: {@code parse_feat_name} returns {@code PARSE_ERROR_REPEATED_DIRECTIVE} on the slot's second name. */
    @Test
    void aRepeatedCodeWithASecondNameIsFatal() throws IOException {
        assertFatal("repeat-code.txt", VALID_RECORD
                        + "code:FLOOR\nname:another floor\ngraphics:.:w\n",
                "second name value: another floor", "FLOOR");
    }

    // ---- Digit-bearing FLAG token (FLAG_BODY digit tail) -----------------

    /**
     * Pins the shared {@code FLAG_BODY} rule ({@code ('A'..'Z') ('A'..'Z' | '_' | '0'..'9')*} in
     * {@code imports/Flags.g4}): a flag value whose tail carries digits must lex as a single
     * {@code FLAG} token and never split or collide with {@code INTEGER}. The digit tail is the
     * non-obvious case - no <em>terrain</em> flag ships with a digit, so this exercises a latent
     * lexer capability deliberately.
     *
     * <p>{@code resist-flag:RAND_50} resolves to the real {@code RF_RAND_50} monster-race flag, so a
     * clean, error-free single-record load is the proof: had the digit tail failed to lex we would
     * get a hard parse error, and had {@code RAND_50} failed to resolve we would get the assembler's
     * soft "Illegal resist flag" message with zero items - neither occurs here.
     */
    @Test
    void digitBearingFlagTokenisesAndResolves() throws IOException {
        String path = tempFile("digit-flag.txt",
                "code:FLOOR\nname:open floor\ngraphics:.:w\nresist-flag:RAND_50\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertEquals(1, result.items().size(), result.items()::toString);
        assertEquals(TerrainFlags.FEAT_FLOOR, result.items().get(0).getCodeFlags());
    }

    // ---- Two different defects at once -----------------------------------

    /**
     * C quits on the first bad line, so with a good record, a bad code and then a bad flag, the bad
     * code is the one reported and the flag is never reached. Nothing loads.
     */
    @Test
    void theFirstBadRecordStopsTheLoad() throws IOException {
        String content = String.join("\n",
                "code:FLOOR\nname:open floor\ngraphics:.:w",
                "code:NOTACODE\nname:x\ngraphics:.:w",
                "code:CLOSED\nname:closed door\ngraphics:+:w\nflags:NOTAFLAG",
                "");
        String path = tempFile("mixed.txt", content);

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new TerrainReader().parseWithResults(path));

        assertTrue(e.getMessage().contains("NOTACODE"), e::getMessage);
        assertFalse(e.getMessage().contains("NOTAFLAG"), e::getMessage);
    }

    // =====================================================================
    // Helpers for the contract and difference tests below
    // =====================================================================

    private ParseResult<Feature> load(String name, String content) throws IOException {
        return new TerrainReader().parseWithResults(tempFile(name, content));
    }

    /**
     * A grammar or lexer error: the load stops with a {@link ParseCancellationException}.
     *
     * <p>Function assertHardError coded before 261104, rewritten on 261104 for the removal of the
     * soft-error path: it now takes the fixture rather than a result, since a failed load has none.
     */
    private void assertHardError(String name, String content) {
        assertThrows(ParseCancellationException.class, () -> load(name, content));
    }

    // =====================================================================
    // The real file, worked out from the upstream terrain.txt and list-terrain.h
    // =====================================================================

    /**
     * Code and name of every {@code code:} record in the upstream file, in file order, which is the
     * order of {@code list-terrain.h}: the slot {@code f_info} files each one under.
     */
    private static final String[][] SHIPPED = {
            {"NONE", "unknown grid"}, {"FLOOR", "open floor"}, {"CLOSED", "closed door"},
            {"OPEN", "open door"}, {"BROKEN", "broken door"}, {"LESS", "up staircase"},
            {"MORE", "down staircase"}, {"STORE_GENERAL", "General Store"}, {"STORE_ARMOR", "Armoury"},
            {"STORE_WEAPON", "Weapon Smiths"}, {"STORE_BOOK", "Bookseller"},
            {"STORE_ALCHEMY", "Alchemy Shop"}, {"STORE_MAGIC", "Magic Shop"},
            {"STORE_BLACK", "Black Market"}, {"HOME", "Home"}, {"SECRET", "secret door"},
            {"RUBBLE", "pile of rubble"}, {"MAGMA", "magma vein"}, {"QUARTZ", "quartz vein"},
            {"MAGMA_K", "magma vein with treasure"}, {"QUARTZ_K", "quartz vein with treasure"},
            {"GRANITE", "granite wall"}, {"PERM", "permanent wall"}, {"LAVA", "lava"},
            {"PASS_RUBBLE", "pile of passable rubble"}
    };

    @Test
    void theRealFileLoadsEveryRecordInFileOrder() throws IOException {
        List<Feature> items = new TerrainReader().parseWithResults(REAL_FILE).items();

        assertEquals(SHIPPED.length, items.size());
        for (int i = 0; i < SHIPPED.length; i++) {
            assertEquals(TerrainFlags.valueOf("FEAT_" + SHIPPED[i][0]), items.get(i).getCodeFlags(),
                    "record " + i);
            assertEquals(SHIPPED[i][1], items.get(i).getName(), "record " + i);
        }
    }

    /** The upstream file has eight records with {@code TF_SHOP}, STORE_GENERAL to HOME. */
    @Test
    void theRealFileHasEightShopsAndOneMimic() throws IOException {
        List<Feature> items = new TerrainReader().parseWithResults(REAL_FILE).items();

        assertEquals(8, items.stream().filter(Feature::isShop).count());
        assertEquals(List.of(TerrainFlags.FEAT_SECRET),
                items.stream().filter(Feature::isMimicing).map(Feature::getCodeFlags).toList());
    }

    /**
     * The shipped file carries no {@code record-count:} header. It still differs from upstream in one
     * trailing space on a comment and the trailing space on the lava {@code walk-msg:} and
     * {@code run-msg:} lines, which this checks are kept. The trailing spaces are part of the C
     * message, as {@code str} takes the rest of the line.
     *
     * <p>Function theShippedFileHasNoHeaderAndKeepsTheLavaMessages coded on 261104, commented in full
     * on 261104. It replaces theShippedFileDiffersFromUpstreamInTheHeaderAndThreeTrailingSpaces.
     */
    @Test
    void theShippedFileHasNoHeaderAndKeepsTheLavaMessages() throws IOException {
        List<String> lines = Files.readAllLines(Path.of(REAL_FILE));

        assertEquals(0, lines.stream().filter(l -> l.startsWith("record-count:")).count());
        assertTrue(lines.contains("walk-msg:The lava will scald you!  Really step in?"));
        assertTrue(lines.contains("run-msg:Lava blocks your path.  Step into it?"));
    }

    // =====================================================================
    // The reader's contract
    // =====================================================================

    @Test
    void parseReturnsTheSameFeaturesAsParseWithResults() throws Exception {
        List<Feature> items = new TerrainReader().parse(REAL_FILE);

        // Feature.equals compares Flag by identity, so compare what each record is called instead.
        assertEquals(new TerrainReader().parseWithResults(REAL_FILE).items().stream().map(Feature::getName).toList(),
                items.stream().map(Feature::getName).toList());
    }

    /** C's {@code parse_file_quit_not_found} quits; the port throws so the loader can stop. */
    @Test
    void aMissingFileIsAnIOException() {
        assertThrows(IOException.class,
                () -> new TerrainReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    @Test
    void aHardGrammarErrorReturnsNoFeaturesAndTheMessages() throws IOException {
        assertHardError("hard.txt", VALID_RECORD + "nonsense:1\n");
    }

    // =====================================================================
    // What the grammar does the way C does
    // =====================================================================

    /** C: {@code parse_feat_priority}, {@code parse_feat_digging} and {@code parse_feat_mimic} assign, so the last wins. */
    @Test
    void aRepeatedPriorityDiggingOrMimicKeepsTheLastLine() throws IOException {
        ParseResult<Feature> r = load("repeat-assign.txt", VALID_RECORD
                + "priority:1\npriority:7\ndigging:2\ndigging:4\nmimic:NONE\nmimic:GRANITE\n");

        String text = r.items().get(0).toString();
        assertTrue(text.contains("priority=7"), text);
        assertTrue(text.contains("dig=4"), text);
        assertTrue(text.contains("mimic=FEAT_GRANITE"), text);
    }

    /** C: {@code parse_feat_desc} calls {@code string_append}, so lines join with no separator added. */
    @Test
    void descLinesJoinInFileOrderKeepingTheLeadingBlank() throws IOException {
        ParseResult<Feature> r = load("desc.txt", VALID_RECORD
                + "desc:An open floor or bare ground.  Can be walked across by anything, and can\n"
                + "desc: hold traps or items.\n");

        assertTrue(r.items().get(0).toString().contains(
                "description='An open floor or bare ground.  Can be walked across by anything, and can"
                        + " hold traps or items.'"), r.items().get(0)::toString);
    }

    /** C: {@code string_append} on a {@code walk-msg:} line that already has text, so the lines join with no separator. */
    @Test
    void repeatedMessageLinesAreJoinedAsInC() throws IOException {
        String[][] directives = {
                {"walk-msg", "walkMsg"}, {"run-msg", "runMsg"}, {"hurt-msg", "hurtMsg"},
                {"die-msg", "dieMsg"}, {"confused-msg", "confusedMsg"}
        };
        for (String[] d : directives) {
            ParseResult<Feature> r = load(d[0] + ".txt", VALID_RECORD
                    + d[0] + ":first\n" + d[0] + ":second\n");

            String text = r.items().get(0).toString();
            assertTrue(text.contains(d[1] + "='firstsecond'"), d[0] + ": " + text);
        }
    }

    /** C: the same append on {@code look-prefix:} and {@code look-in-preposition:}, then {@code finish_parse_feat} adds one trailing space. */
    @Test
    void repeatedLookLinesAreJoinedAsInC() throws IOException {
        ParseResult<Feature> r = load("look.txt", VALID_RECORD
                + "look-prefix:first\nlook-prefix:second\n"
                + "look-in-preposition:in\nlook-in-preposition:to\n");

        String text = r.items().get(0).toString();
        assertTrue(text.contains("lookPrefix='firstsecond '"), text);
        assertTrue(text.contains("lookInPreposition='into '"), text);
    }

    /** C: {@code name} is a {@code str}, which takes the rest of the line, colons included. */
    @Test
    void aColonInTheNameIsKept() throws IOException {
        ParseResult<Feature> r = load("colon-name.txt", "code:FLOOR\nname:a: b:c\ngraphics:.:w\n");

        assertEquals("a: b:c", r.items().get(0).getName());
    }

    /** C: any order of the optional lines is accepted after the three header lines. */
    @Test
    void optionalLinesMayComeInAnyOrder() throws IOException {
        ParseResult<Feature> r = load("order.txt", VALID_RECORD
                + "resist-flag:RAND_50\ndesc:d\nlook-prefix:a\nflags:LOS | PROJECT\npriority:3\n"
                + "flags:PASSABLE\n");

        assertTrue(r.items().get(0).isPassable());
        assertTrue(r.items().get(0).isLos());
    }

    // =====================================================================
    // Missing, repeated and out-of-order directives, as C (Issue_Approach_Order.md Stage 7a)
    // =====================================================================

    /**
     * C: {@code parse_feat_code} is the only line a record needs. A slot that never sees a
     * {@code graphics:} line keeps the zeroed {@code d_char} and {@code d_attr}, which are NUL and
     * {@code COLOUR_DARK}.
     *
     * <p>Function aRecordWithOnlyACodeLoadsAsInC coded on 261118, commented in full on 261118.
     */
    @Test
    void aRecordWithOnlyACodeLoadsAsInC() throws IOException {
        ParseResult<Feature> r = load("code-only.txt", "code:FLOOR\n");

        assertEquals(1, r.items().size());
        assertEquals(TerrainFlags.FEAT_FLOOR, r.items().get(0).getCodeFlags());
        assertEquals("", r.items().get(0).getName());
        assertNulAndDark(r.items().get(0));
    }

    /**
     * C: with no {@code graphics:} line the slot keeps NUL and {@code COLOUR_DARK}, whatever the
     * code or the name is. The floor and the name "open floor" have no special standing.
     *
     * <p>Function aRecordWithNoGraphicsLoadsAsNulAndDarkForAnyCodeAndName coded on 261118,
     * commented in full on 261118.
     */
    @Test
    void aRecordWithNoGraphicsLoadsAsNulAndDarkForAnyCodeAndName() throws IOException {
        String[][] cases = {
                {"code:FLOOR\nname:open floor\n", "open floor"},
                {"code:FLOOR\nname:lava\n", "lava"},
                {"code:CLOSED\nname:closed door\n", "closed door"},
                {"code:CLOSED\nname:open floor\n", "open floor"},
        };
        for (String[] c : cases) {
            ParseResult<Feature> r = load("no-graphics.txt", c[0]);

            assertEquals(1, r.items().size(), c[0]);
            assertEquals(c[1], r.items().get(0).getName(), c[0]);
            assertNulAndDark(r.items().get(0));
        }
    }

    /**
     * C: a {@code graphics:} line is assigned whatever the code or name, so a non-floor terrain
     * keeps the glyph and colour it was given and a real glyph in the dark colour is not mistaken
     * for an absent line.
     *
     * <p>Function aGraphicsLineIsUsedForAnyCodeAndKeepsADarkColour coded on 261118, commented in full
     * on 261118.
     */
    @Test
    void aGraphicsLineIsUsedForAnyCodeAndKeepsADarkColour() throws IOException {
        ParseResult<Feature> r = load("any-code.txt",
                "code:FLOOR\ngraphics:.:d\ncode:CLOSED\nname:closed door\ngraphics:+:U\n");

        assertEquals(new AngbandDisplayCharacter('.', ColourEnum.COLOUR_DARK),
                r.items().get(0).getDisplayCharacter());
        assertEquals(new AngbandDisplayCharacter('+', ColourEnum.COLOUR_LIGHT_UMBER),
                r.items().get(1).getDisplayCharacter());
    }

    @Test
    void aRecordWithNoNameLoadsWithAnEmptyName() throws IOException {
        ParseResult<Feature> r = load("no-name.txt", "code:FLOOR\ngraphics:.:w\n");

        assertEquals("", r.items().get(0).getName());
        assertEquals(new AngbandDisplayCharacter('.', ColourEnum.COLOUR_WHITE),
                r.items().get(0).getDisplayCharacter());
    }

    /** C: after {@code code:} the handlers run in whatever order the lines come. */
    @Test
    void graphicsBeforeTheNameLoadsAsInC() throws IOException {
        ParseResult<Feature> r = load("graphics-first.txt", "code:FLOOR\ngraphics:.:w\nname:open floor\n");

        assertEquals("open floor", r.items().get(0).getName());
        assertEquals(new AngbandDisplayCharacter('.', ColourEnum.COLOUR_WHITE),
                r.items().get(0).getDisplayCharacter());
    }

    /** C: {@code parse_feat_graphics} assigns, so a second line in the same record replaces the first. */
    @Test
    void aRepeatedGraphicsLineKeepsTheLastAsInC() throws IOException {
        ParseResult<Feature> r = load("two-graphics.txt", VALID_RECORD + "graphics:#:r\n");

        assertEquals(new AngbandDisplayCharacter('#', ColourEnum.COLOUR_RED),
                r.items().get(0).getDisplayCharacter());
    }

    /**
     * C: {@code parse_feat_code} picks the same {@code f_info} slot for a repeated code and
     * {@code parse_feat_graphics} assigns into it, so the four orders of "graphics or none" give: the
     * later line when there is one, otherwise what the slot already held.
     *
     * <p>Function aRepeatedCodeTakesTheLaterGraphicsOtherwiseKeepsTheEarlier coded on 261118,
     * commented in full on 261118.
     */
    @Test
    void aRepeatedCodeTakesTheLaterGraphicsOtherwiseKeepsTheEarlier() throws IOException {
        Feature noneThenLine = load("o1.txt", "code:FLOOR\ncode:FLOOR\ngraphics:#:r\n").items().get(0);
        Feature lineThenNone = load("o2.txt", "code:FLOOR\ngraphics:.:w\ncode:FLOOR\nflags:LOS\n").items().get(0);
        Feature lineThenLine = load("o3.txt", "code:FLOOR\ngraphics:.:w\ncode:FLOOR\ngraphics:#:r\n").items().get(0);
        Feature noneThenNone = load("o4.txt", "code:FLOOR\ncode:FLOOR\nflags:LOS\n").items().get(0);

        assertEquals(new AngbandDisplayCharacter('#', ColourEnum.COLOUR_RED), noneThenLine.getDisplayCharacter());
        assertEquals(new AngbandDisplayCharacter('.', ColourEnum.COLOUR_WHITE), lineThenNone.getDisplayCharacter());
        assertEquals(new AngbandDisplayCharacter('#', ColourEnum.COLOUR_RED), lineThenLine.getDisplayCharacter());
        assertNulAndDark(noneThenNone);
    }

    /** C: a header with no records runs {@code finish_parse_feat} over the zeroed table and loads. */
    @Test
    void anEmptyFileLoadsWithNoRecords() throws IOException {
        assertEquals(0, load("empty.txt", "").items().size());
    }

    /**
     * C: {@code parse_feat_name} returns {@code PARSE_ERROR_REPEATED_DIRECTIVE} when the slot's
     * name is already set, so a second {@code name:} refuses even with the same text and whatever
     * lines come between.
     *
     * <p>Function aSecondNameLineInOneRecordIsRefusedAsInC coded on 261118, commented in full on
     * 261118.
     */
    @Test
    void aSecondNameLineInOneRecordIsRefusedAsInC() throws IOException {
        assertFatal("two-names.txt", "code:FLOOR\nname:a\nname:b\ngraphics:.:w\n", "more than one name");
        assertFatal("same-name-twice.txt", "code:FLOOR\nname:a\nname:a\n", "more than one name");
        assertFatal("names-apart.txt", "code:FLOOR\nname:a\ngraphics:.:w\nflags:LOS\nname:b\n",
                "more than one name");
        assertFatal("three-names.txt", "code:FLOOR\nname:a\nname:b\nname:c\n", "more than one name");
    }

    /**
     * C: the name check is on the slot, so a second record with the same code and a name is refused
     * when the slot already has one, and accepted when it did not. Two codes with the same name are
     * separate slots.
     *
     * <p>Function aNameOnARepeatedCodeFollowsTheSlotAsInC coded on 261118, commented in full on
     * 261118.
     */
    @Test
    void aNameOnARepeatedCodeFollowsTheSlotAsInC() throws IOException {
        assertFatal("slot-a-b.txt", "code:FLOOR\nname:a\ncode:FLOOR\nname:b\n", "second name");
        assertEquals("a", load("slot-a-none.txt", "code:FLOOR\nname:a\ncode:FLOOR\nflags:LOS\n")
                .items().get(0).getName());
        assertEquals("b", load("slot-none-b.txt", "code:FLOOR\ncode:FLOOR\nname:b\n")
                .items().get(0).getName());
        ParseResult<Feature> two = load("two-codes.txt", "code:FLOOR\nname:a\ncode:CLOSED\nname:a\n");
        assertEquals(2, two.items().size());
        assertEquals("a", two.items().get(1).getName());
    }

    private void assertNulAndDark(Feature f) {
        assertEquals(new AngbandDisplayCharacter('\0', ColourEnum.COLOUR_DARK), f.getDisplayCharacter());
    }

    // =====================================================================
    // Flag lists (Stage 7d): C's parse_feat_flags splits with strtok(flags, " |")
    // =====================================================================

    /**
     * Every separator C's {@code strtok(flags, " |")} accepts between two names: any run of blanks
     * and bars with at least one character in it.
     */
    private static final List<String> SEPARATORS = List.of(" ", "    ", "|", " |", "| ", " | ", "  |  ",
            "||", " | | ", "| |", " |  | ");

    /**
     * Runs of blanks and bars with no name in them, which {@code strtok} skips at either end of a list.
     */
    private static final List<String> DELIMITER_RUNS = List.of(" ", "  ", "|", " | ", "||", " | | ", "|  |");

    /** A second record after the {@code flags:} line, to show the line ended cleanly. */
    private static final String GRANITE_TAIL = "code:GRANITE\nname:granite wall\ngraphics:#:W\n";

    /**
     * Loads the open-floor record with {@code flagsLine} (no line end of its own) followed by granite,
     * checks both records loaded and granite carries no flag, and returns the floor.
     */
    private Feature floorWithFlagsMidFile(String flagsLine) throws IOException {
        List<Feature> items = load("flags-mid.txt", VALID_RECORD + flagsLine + "\n" + GRANITE_TAIL).items();

        assertEquals(2, items.size(), flagsLine);
        assertEquals(TerrainFlags.FEAT_GRANITE, items.get(1).getCodeFlags(), flagsLine);
        assertTrue(items.get(1).getFlags().isEmpty(), flagsLine);
        return items.get(0);
    }

    @Test
    void anyRunOfBlanksAndBarsSeparatesFlagsAsInC() throws IOException {
        for (String sep : SEPARATORS) {
            Feature floor = floorWithFlagsMidFile("flags:LOS" + sep + "PASSABLE");
            assertTrue(floor.getFlags().has(TerrainFeatureFlags.TF_LOS), () -> "sep=[" + sep + "]");
            assertTrue(floor.getFlags().has(TerrainFeatureFlags.TF_PASSABLE), () -> "sep=[" + sep + "]");

            // A second name that C splits off reaches grab_flag and stops the load as
            // PARSE_ERROR_INVALID_FLAG; an unsplit line could not name it.
            assertFatal("flags-split.txt", VALID_RECORD + "flags:LOS" + sep + "WIBBLE\n",
                    "illegal terrain flag value: WIBBLE");
        }
    }

    @Test
    void aLeadingOrTrailingDelimiterOnFlagsLoadsAsInC() throws IOException {
        // strtok skips delimiters before the first name and finds nothing after the last.
        for (String run : DELIMITER_RUNS) {
            for (String line : List.of("flags:" + run + "LOS", "flags:LOS" + run)) {
                Feature floor = floorWithFlagsMidFile(line);
                assertTrue(floor.getFlags().has(TerrainFeatureFlags.TF_LOS), () -> "[" + line + "]");
                assertFalse(floor.getFlags().has(TerrainFeatureFlags.TF_PASSABLE), () -> "[" + line + "]");
            }
        }
    }

    @Test
    void aBareOrDelimiterOnlyFlagsLineLoadsWithNoFlagsAsInC() throws IOException {
        // "flags ?str flags": parse_feat_flags returns early when the value is absent, and strtok
        // finds no name in a value that is only blanks and bars.
        assertTrue(floorWithFlagsMidFile("flags:").getFlags().isEmpty());
        for (String run : DELIMITER_RUNS) {
            assertTrue(floorWithFlagsMidFile("flags:" + run).getFlags().isEmpty(), () -> "run=[" + run + "]");
        }
    }

    @Test
    void aFlagsLineWithNoFinalNewlineLoadsAsInC() throws IOException {
        // file_getl hands C an unterminated last line as it stands; the answer matches the same
        // line with a newline after it.
        List<String> named = List.of("flags:LOS", "flags:LOS PASSABLE", "flags:LOS | | PASSABLE",
                "flags:| LOS", "flags: LOS", "flags:LOS |", "flags:LOS ||");
        List<String> empty = List.of("flags:", "flags: ", "flags:|", "flags:||", "flags: | ", "flags:| |");

        for (String line : named) {
            List<Feature> items = load("flags-last.txt", VALID_RECORD + line).items();
            assertEquals(1, items.size(), line);
            assertTrue(items.get(0).getFlags().has(TerrainFeatureFlags.TF_LOS), () -> "[" + line + "]");
            assertEquals(line.contains("PASSABLE"), items.get(0).getFlags().has(TerrainFeatureFlags.TF_PASSABLE),
                    () -> "[" + line + "]");
        }
        for (String line : empty) {
            List<Feature> items = load("flags-last.txt", VALID_RECORD + line).items();
            assertEquals(1, items.size(), line);
            assertTrue(items.get(0).getFlags().isEmpty(), () -> "[" + line + "]");
        }
    }

    @Test
    void aSingleValueFlagDirectiveWithNoFinalNewlineLoadsAsInC() throws IOException {
        // code:, mimic: and resist-flag: share the flag mode; each reads its last line as C does.
        assertEquals(TerrainFlags.FEAT_FLOOR,
                load("code-last.txt", "code:FLOOR").items().get(0).getCodeFlags());
        assertEquals(TerrainFlags.FEAT_GRANITE,
                load("mimic-last.txt", VALID_RECORD + "mimic:GRANITE").items().get(0).getMimicTerrainFlags());
        assertEquals(MonsterRaceFlag.RF_IM_FIRE,
                load("resist-last.txt", VALID_RECORD + "resist-flag:IM_FIRE").items().get(0).getResistFlag());
    }

    @Test
    void aSecondNameOnASingleValueDirectiveIsRefusedAsInC() {
        // The flag mode is shared with these directives, and only flags: is a list. In C a second
        // name stays in the one value and fails its lookup: lookup_feat_code for code: and mimic:
        // (str), and lookup_flag against mon_race_flags for resist-flag: (a sym, split on ':' only).
        // Java refuses each at the parser, mid-file and as a last line with no final newline.
        List<String> bodies = List.of(
                "code:FLOOR GRANITE\nname:open floor\ngraphics:.:w\n",
                "code:FLOOR|GRANITE\nname:open floor\ngraphics:.:w\n",
                "code:FLOOR |\nname:open floor\ngraphics:.:w\n",
                "code:FLOOR GRANITE",
                VALID_RECORD + "mimic:FLOOR GRANITE\n",
                VALID_RECORD + "mimic:FLOOR|GRANITE\n",
                VALID_RECORD + "mimic:FLOOR |\n",
                VALID_RECORD + "mimic:FLOOR GRANITE",
                VALID_RECORD + "resist-flag:IM_FIRE IM_COLD\n",
                VALID_RECORD + "resist-flag:IM_FIRE|IM_COLD\n",
                VALID_RECORD + "resist-flag:IM_FIRE |\n",
                VALID_RECORD + "resist-flag:IM_FIRE IM_COLD");

        for (String body : bodies) {
            assertThrows(ParseCancellationException.class, () -> load("one-value.txt", body), body);
        }
    }

    // =====================================================================
    // Where the grammar is stricter than C (docs/grammar_issues.md)
    // =====================================================================

    /** C: {@code strtoul(tok, &z, 0)}, so {@code 0x3} is 3, {@code 0XA} is 10 and {@code 010} is 8. */
    @Test
    void aHexadecimalOrOctalPriorityLoadsAsInC() throws IOException {
        assertEquals(3, load("hex-priority.txt", VALID_RECORD + "priority:0x3\n").items().get(0).getPriority());
        assertEquals(10, load("hex-priority-upper.txt", VALID_RECORD + "priority:0XA\n").items().get(0).getPriority());
        assertEquals(8, load("octal-priority.txt", VALID_RECORD + "priority:010\n").items().get(0).getPriority());
    }

    /** C: {@code digging} is an {@code int} read in base 0, so {@code 0x4} is 4 and {@code 05} is 5. */
    @Test
    void aHexadecimalOrOctalDiggingLoadsAsInC() throws IOException {
        assertEquals(4, load("hex-digging.txt", VALID_RECORD + "digging:0x4\n").items().get(0).getDig());
        assertEquals(5, load("octal-digging.txt", VALID_RECORD + "digging:05\n").items().get(0).getDig());
    }

    /** C: {@code 0x6} is 6, which is outside the 1 to 5 that {@code digging} allows, so it is refused. */
    @Test
    void aHexadecimalDiggingOutOfRangeIsRefusedAsInC() throws IOException {
        assertFatal("hex-digging-range.txt", VALID_RECORD + "digging:0x6\n", "out of range digging");
    }

    /** C: a minus on {@code priority} is refused by {@code parser_getuint}, hex or not. */
    @Test
    void aMinusOnAHexadecimalPriorityIsRefusedAsInC() throws IOException {
        assertFatal("minus-hex-priority.txt", VALID_RECORD + "priority:-0x3\n", "negative priority");
    }

    /** C: {@code strtoul} stops at the {@code x} and {@code z != tok}, so {@code 3x} is 3. */
    @Test
    void textAfterThePriorityNumberIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError("junk-priority.txt", VALID_RECORD + "priority:3x\n");
    }

    /** C: an {@code int} is tokenised on a colon and nothing checks what follows the last field. */
    @Test
    void aSecondFieldAfterTheDiggingNumberIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError("junk-digging.txt", VALID_RECORD + "digging:2:junk\n");
    }

    /**
     * C: {@code code}, {@code mimic} and {@code flags} are {@code str} fields, which are not tokenised on a
     * colon, so {@code FLOOR:x} is the whole name and matches nothing. Only {@code resist-flag}, a
     * {@code sym}, drops what follows its colon.
     */
    @Test
    void aColonInACodeLineIsRefusedAsInC() {
        assertHardError("colon-code.txt", "code:FLOOR:x\nname:x\ngraphics:.:w\n");
    }

    /** See {@link #aColonInACodeLineIsRefusedAsInC}: {@code mimic} is a {@code str} too. */
    @Test
    void aColonInAMimicLineIsRefusedAsInC() {
        assertHardError("colon-mimic.txt", VALID_RECORD + "mimic:FLOOR:x\n");
    }

    /** See {@link #aColonInACodeLineIsRefusedAsInC}: {@code flags} is a {@code ?str} too. */
    @Test
    void aColonInAFlagsLineIsRefusedAsInC() {
        assertHardError("colon-flags.txt", VALID_RECORD + "flags:LOS:x\n");
    }

    /**
     * C: {@code code} and {@code mimic} are {@code str} fields looked up with {@code streq}, and
     * {@code resist-flag} is a {@code sym} looked up with {@code streq}, so a blank or tab after the name
     * is part of the name and matches nothing. {@code flags} is split with {@code strtok(" |")}, which
     * skips it, and that case is pinned by {@code aLeadingOrTrailingDelimiterOnFlagsLoadsAsInC}.
     *
     * <p>Function aTrailingBlankOnACodeMimicOrResistFlagLineIsRefusedAsInC coded on 261124, commented in
     * full on 261124.
     */
    @Test
    void aTrailingBlankOnACodeMimicOrResistFlagLineIsRefusedAsInC() {
        for (String blank : new String[]{" ", "\t"}) {
            assertHardError("blank-code.txt", "code:FLOOR" + blank + "\nname:x\ngraphics:.:w\n");
            assertHardError("blank-mimic.txt", VALID_RECORD + "mimic:FLOOR" + blank + "\n");
            assertHardError("blank-resist.txt", VALID_RECORD + "resist-flag:RAND_50" + blank + "\n");
        }
    }

    /** C: {@code resist-flag} is a {@code sym}, tokenised on a colon, so the text after it is ignored. */
    @Test
    void aSecondFieldAfterTheResistFlagIsIgnoredAsInC() throws IOException {
        Feature feature = load("junk-resist.txt", VALID_RECORD + "resist-flag:RAND_50:x\n").items().get(0);
        Feature plain = load("plain-resist.txt", VALID_RECORD + "resist-flag:RAND_50\n").items().get(0);

        assertEquals(plain.getResistFlag(), feature.getResistFlag());
    }

    /**
     * C: {@code color_text_to_attr} answers white for a name it does not know. The lexer takes any
     * colour text, so the load succeeds and {@code TerrainFeatureAssembler} supplies the white.
     */
    @Test
    void anUnknownColourNameDrawsWhiteAsInC() throws IOException {
        ParseResult<Feature> result = load("bad-colour.txt", "code:FLOOR\nname:x\ngraphics:.:nonsense\n");

        AngbandDisplayCharacter cell = result.items().getFirst().getDisplayCharacter();
        assertEquals('.', cell.getNarrowCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, cell.getAttributeColour());
    }

    /** C: {@code parser_parse} skips leading whitespace before it reads the directive, as {@code INDENT} does. */
    @Test
    void anIndentedDirectiveLoadsAsInC() throws IOException {
        assertEquals(3, load("indented.txt", VALID_RECORD + "  priority:3\n").items().get(0).getPriority());
    }

    // =====================================================================
    // Where the list differs from f_info
    // =====================================================================

    /**
     * C: {@code f_info} is allocated with {@code FEAT_MAX} slots and each record fills the slot of its
     * {@code code:}, so MORE (6) would come after FLOOR (1) whatever the file order. The list holds the
     * records in file order and nothing else.
     */
    @Test
    void recordsComeBackInFileOrderHereWhereCFilesEachUnderItsCode() throws IOException {
        ParseResult<Feature> r = load("order.txt", "code:MORE\nname:down staircase\ngraphics:>:w\n"
                        + "code:FLOOR\nname:open floor\ngraphics:.:w\n");

        assertEquals(List.of(TerrainFlags.FEAT_MORE, TerrainFlags.FEAT_FLOOR),
                r.items().stream().map(Feature::getCodeFlags).toList());
    }
}
