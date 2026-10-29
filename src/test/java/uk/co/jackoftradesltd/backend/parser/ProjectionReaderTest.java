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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectionType;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.numerics.Random;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link ProjectionReader} (grammar-suite reader track R4), against
 * {@code projection_parser} in the C original's {@code obj-init.c}.
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/projection.txt}; a clean
 * load is itself the assertion that all 56 records resolved (every code/type/msgt/colour). The
 * error-path tests inject a single defect each, either into a minimal fixture or into a copy of
 * the shipped file. A lexer or grammar error gives an empty item list plus the parser's messages.
 * The assembler stops the load, as C quits, by throwing {@link DataFileParseException} out of
 * {@code parseWithResults}, for a misplaced, unknown or empty code in the first 25 records, an empty
 * code anywhere, and a count of {@code type:element} records other than 25. Its other faults are
 * soft: the bad record is skipped and the rest still loads, unless the skip leaves the element
 * count short. See {@link ProjectionAssemblerTest} for the cases driven from hand-built records.
 *
 * <p>The second half, added on 261018, pins the reader's contract: a missing file, the list in
 * file order, the {@code record-count:} header, and every field of the 56 shipped projections
 * against a table worked out from the upstream {@code projection.txt} by transcribing C's rules.
 * The remaining tests pin where the grammar and the assembler differ from C, each named
 * {@code ...WhereC...} and written up in {@code docs/grammar_issues.md} or
 * {@code docs/Assembler_Issues.md}; a fix there will turn the matching test red on purpose.
 *
 * <p>Test class ProjectionReaderTest coded before 261001, expanded and commented in full on 261018;
 * the tests that reach the assembler's stop-the-load path rewritten on 261021.
 *
 * @author Rowan Crowther
 */
class ProjectionReaderTest {

    private static final String REAL_FILE = "lib/gamedata/projection.txt";

    /**
     * A minimal, valid projection block (mandatory fields only).
     */
    private static final String ONE_RECORD =
            "code:ACID\ntype:element\ndesc:acid\nblind-desc:acid\nobvious:1\ncolor:Slate\n";

    @TempDir
    Path tempDir;

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAllProjections() throws IOException {
        ParseResult<Projection> result = new ProjectionReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(56, result.items().size());
        // Spot-check the first record (ACID) via the one getter Projection exposes.
        assertEquals("acid", result.items().get(0).getLashDescription());
    }

    @Test
    void aRecordCountMismatchDoesNotHideTheElementCountFailure() throws IOException {
        // Header over-declares: 5 vs the single record present. The header count is a Java-only
        // soft error (C's file has no such line), and a one-record file cannot satisfy the element
        // rule either: C's finish_parse_projection() quits unless exactly 25 records are of type
        // element, so the assembler stops the load. The exception message names the element count
        // only; the soft header error is not carried by it.
        DataFileParseException e = loadAndStop("bad-count.txt", "record-count:5\n" + ONE_RECORD);

        assertTrue(e.getMessage().contains("expected: 25 got: 1"), e.getMessage());
    }

    @Test
    void headerMismatchOnTheRealFileIsReportedButAllProjectionsStillLoad() throws IOException {
        // The shipped file with its header changed to over-declare by one: the mismatch is a soft
        // error and the 56 valid records, which satisfy the element rule, all still load.
        String path = tempFile("real-bad-count.txt",
                Files.readString(Path.of(REAL_FILE)).replace("record-count:56", "record-count:57"));

        ParseResult<Projection> result = new ProjectionReader().parseWithResults(path);

        assertEquals(56, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 57") &&
                        e.contains("contains 56")),
                result.errors()::toString);
    }

    @Test
    void unknownCodeInTheFirstRecordStopsTheLoad() throws IOException {
        // NOTACODE is not a ProjectionEnum value, so it cannot be element_names[0]: C quits on the
        // mismatch in parse_projection_code.
        DataFileParseException e = loadAndStop("bad-code.txt",
                "record-count:1\ncode:NOTACODE\ntype:element\ndesc:x\nblind-desc:x\nobvious:1\ncolor:Slate\n");

        assertTrue(e.getMessage().contains("NOTACODE"), e.getMessage());
    }

    @Test
    void missingRecordCountHeaderIsReportedWithNoItems() throws IOException {
        // No record-count directive: a grammar syntax error surfaced through ParseErrors.
        String path = tempFile("no-header.txt", ONE_RECORD);

        ParseResult<Projection> result = new ProjectionReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void unknownCodeAfterTheElementBlockIsStoredWithNoCodeAndNoError() throws IOException {
        // The shipped file plus one extra block whose code is not a ProjectionEnum value, with the
        // header raised to match. C does not read the code past the element block and keeps the
        // record, so the port stores it as PROJ_NONE and reports nothing.
        String extra = "\ncode:NOTACODE\ntype:monster\ndesc:x\nblind-desc:x\nobvious:1\ncolor:Slate\n";
        ParseResult<Projection> result = load("real-plus-bad-code.txt",
                shipped().replace("record-count:56", "record-count:57") + extra);

        assertEquals(57, result.items().size(), result.items()::toString);
        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(ProjectionEnum.PROJ_NONE, result.items().getLast().getProjection());
        assertEquals(ProjectionEnum.PROJ_ACID, result.items().getFirst().getProjection());
    }

    @Test
    void unknownTypeIsNotAnErrorButTheElementCountStopsTheLoad() throws IOException {
        // C keeps type as a string and only ever tests it against "element", so an unrecognised
        // type is not a fault: the block loads with no type. Here ELEC (position 2) has an unknown
        // type, so it no longer counts as an element, the file has 1, and the element rule stops
        // the load - with the element count as the only complaint.
        DataFileParseException e = loadAndStop("unknown-type.txt", String.join("\n",
                "record-count:2",
                "code:ACID\ntype:element\ndesc:x\nblind-desc:x\nobvious:1\ncolor:Slate",
                "code:ELEC\ntype:NOTATYPE\ndesc:x\nblind-desc:x\nobvious:1\ncolor:Blue",
                ""));

        assertTrue(e.getMessage().contains("expected: 25 got: 1"), e.getMessage());
        assertFalse(e.getMessage().contains("NOTATYPE"), e.getMessage());
    }

    // ---- The reader's own contract (init/run/finish_parse_projection) --------------------------

    private static String shipped() throws IOException {
        return Files.readString(Path.of(REAL_FILE));
    }

    private ParseResult<Projection> load(String name, String text) throws IOException {
        return new ProjectionReader().parseWithResults(tempFile(name, text));
    }

    /**
     * Loads {@code text} and returns the exception that stops the load, failing the test if the
     * reader returns a result instead.
     */
    private DataFileParseException loadAndStop(String name, String text) throws IOException {
        String path = tempFile(name, text);
        return assertThrows(DataFileParseException.class, () -> new ProjectionReader().parseWithResults(path));
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    private static void assertHardError(ParseResult<Projection> result) {
        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * {@code run_parse_projection} is {@code parse_file_quit_not_found}, which quits when the file
     * is missing; the port throws instead and leaves the quitting to {@code WorldDataLoader}.
     */
    @Test
    void aMissingFileIsAnIOExceptionWhereCQuits() {
        String missing = tempDir.resolve("no-such-file.txt").toString();

        assertThrows(IOException.class, () -> new ProjectionReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new ProjectionReader().parse(missing));
    }

    /**
     * {@code parse} is {@code parseWithResults} without the messages, so a hard grammar error and
     * an empty list look alike through it.
     */
    @Test
    void parseReturnsTheSameListAsParseWithResultsAndHidesTheErrors() throws IOException {
        List<Projection> viaParse = new ProjectionReader().parse(REAL_FILE);
        List<Projection> viaResults = new ProjectionReader().parseWithResults(REAL_FILE).items();

        assertEquals(56, viaParse.size());
        for (int i = 0; i < 56; i++) {
            assertEquals(viaResults.get(i).getProjection(), viaParse.get(i).getProjection());
        }
        assertTrue(new ProjectionReader().parse(tempFile("headerless.txt", ONE_RECORD)).isEmpty());
    }

    /**
     * An under-declared header is a soft error and all 56 projections load. (The over-declared
     * header is pinned above.) C has no header, so there is no C value to compare with.
     */
    @Test
    void anUnderDeclaredHeaderIsASoftErrorAndEveryProjectionLoads() throws IOException {
        ParseResult<Projection> result = load("under.txt", shipped().replace("record-count:56", "record-count:55"));

        assertEquals(56, result.items().size());
        assertEquals(1, result.errors().size(), result.errors()::toString);
        assertTrue(result.errors().getFirst().contains("declares 55") &&
                result.errors().getFirst().contains("contains 56"), result.errors()::toString);
    }

    /**
     * A header of 0 or -1 lexes as an integer, so it is the same soft mismatch; one that is not a
     * number is a hard error.
     */
    @Test
    void aZeroOrNegativeHeaderIsASoftMismatchAndANonNumberIsAHardError() throws IOException {
        for (String declared : List.of("0", "-1")) {
            ParseResult<Projection> result = load("count" + declared + ".txt",
                    shipped().replace("record-count:56", "record-count:" + declared));
            assertEquals(56, result.items().size(), declared);
            assertTrue(result.errors().getFirst().contains("declares " + declared), result.errors()::toString);
        }

        assertHardError(load("count-abc.txt", shipped().replace("record-count:56", "record-count:abc")));
    }

    /**
     * Every field of the 56 shipped projections, against the table worked out from the upstream
     * {@code projection.txt} by transcribing C's rules: {@code parse_random} for the denominator,
     * {@code message_lookup_by_name} for {@code msgt:}, {@code color_text_to_attr} and
     * {@code color_char_to_attr} for the colour, a {@code uint} compared with 1 for
     * {@code obvious:} and {@code wake:}, and the zero-filled struct for any line left out
     * (message 0 is {@code GENERIC}, colour 0 is Dark). The list order is the order of C's
     * {@code projections[]}, which is the file order.
     *
     * <p>One cell differs on purpose: the upstream file spells the {@code DARK_WEAK} colour
     * {@code L_DARK}, which is not a colour name, so C draws it White. The shipped file says
     * {@code Light Dark} (see {@link #theShippedDarkWeakColourIsLightDarkWhereUpstreamGivesWhite}).
     */
    @Test
    void everyFieldOfEveryShippedProjectionMatchesTheTableFromTheUpstreamFile() throws Exception {
        List<Projection> items = new ProjectionReader().parseWithResults(REAL_FILE).items();

        assertEquals(ROWS.size(), items.size());
        for (int i = 0; i < ROWS.size(); i++) {
            Row row = ROWS.get(i);
            Projection p = items.get(i);
            String where = "row " + i + " (" + row.code + ")";

            assertEquals(ProjectionEnum.valueOf("PROJ_" + row.code), p.getProjection(), where);
            assertEquals(row.name, field(p, "name"), where + " name");
            assertEquals(ProjectionType.valueOf("PT_" + row.type.toUpperCase()), p.getType(), where + " type");
            assertEquals(row.desc, field(p, "description"), where + " desc");
            assertEquals(row.playerDesc, field(p, "playerDescription"), where + " player-desc");
            assertEquals(row.blindDesc, field(p, "blindDescription"), where + " blind-desc");
            assertEquals(row.lashDesc, field(p, "lashDescription"), where + " lash-desc");
            assertEquals(row.numerator, (int) field(p, "numerator"), where + " numerator");
            assertDenominator(p, row.denBase, row.denDice, row.denSides, row.denMBonus, where);
            assertEquals(row.divisor, (int) field(p, "divisor"), where + " divisor");
            assertEquals(row.damageCap, (int) field(p, "damageCap"), where + " damage-cap");
            assertEquals(MessageType.valueOf(row.msgt), field(p, "msgt"), where + " msgt");
            assertEquals(row.obvious, (boolean) field(p, "isObvious"), where + " obvious");
            assertEquals(row.wake, (boolean) field(p, "willWake"), where + " wake");
            String colour = row.code.equals("DARK_WEAK") ? "Light Dark" : row.colour;
            assertEquals(colour, ColourEnum.attributeToString(field(p, "colour")), where + " colour");
        }
    }

    /**
     * C stores the denominator as a {@code random_value}. The port keeps a plain integer when the
     * value has no dice, sides or bonus, and a {@link Random} otherwise.
     */
    private static void assertDenominator(Projection p, int base, int dice, int sides, int mBonus,
                                          String where) throws Exception {
        Random dicey = field(p, "diceDenominator");
        if (dice == 0 && sides == 0 && mBonus == 0) {
            assertNull(dicey, where + " denominator should be a plain integer");
            assertEquals(base, (int) field(p, "denominator"), where + " denominator");
        } else {
            assertNotNull(dicey, where + " denominator should be dice");
            assertEquals(base, dicey.getBase(), where + " denominator base");
            assertEquals(dice, dicey.getDice(), where + " denominator dice");
            assertEquals(sides, dicey.getSides(), where + " denominator sides");
            assertEquals(mBonus, dicey.getMBonus(), where + " denominator m_bonus");
        }
    }

    /**
     * The shipped {@code projection.txt} has {@code color:Light Dark} for {@code DARK_WEAK}, where
     * the upstream file has {@code color:L_DARK}. {@code color_text_to_attr} finds no colour of that
     * name and answers White, so the Java data draws the projection in a different colour from C.
     * Putting the upstream spelling back gives White here too.
     */
    @Test
    void theShippedDarkWeakColourIsLightDarkWhereUpstreamGivesWhite() throws Exception {
        List<Projection> shippedItems = new ProjectionReader().parseWithResults(REAL_FILE).items();
        int darkWeak = ProjectionEnum.PROJ_DARK_WEAK.ordinal() - 1;
        assertEquals(ProjectionEnum.PROJ_DARK_WEAK, shippedItems.get(darkWeak).getProjection());
        assertEquals(ColourEnum.COLOUR_LIGHT_DARK, field(shippedItems.get(darkWeak), "colour"));

        String upstream = shipped().replaceFirst("(?s)(code:DARK_WEAK.*?color:)Light Dark", "$1L_DARK");
        ParseResult<Projection> result = load("l-dark.txt", upstream);

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(ColourEnum.COLOUR_WHITE, field(result.items().get(darkWeak), "colour"));
    }

    /**
     * C's colour lookups: a name of two or more characters matches case-insensitively, a single
     * character is an index letter and is case-sensitive ({@code s} is Slate, {@code S} matches
     * nothing), a space is Dark, and anything that matches nothing is White and never an error.
     */
    @Test
    void colourLookupFollowsCsNameLetterAndFallbackRules() throws Exception {
        Object[][] cases = {
                {"Slate", ColourEnum.COLOUR_SLATE}, {"SLATE", ColourEnum.COLOUR_SLATE},
                {"sLaTe", ColourEnum.COLOUR_SLATE}, {"s", ColourEnum.COLOUR_SLATE},
                {"S", ColourEnum.COLOUR_WHITE}, {"D", ColourEnum.COLOUR_LIGHT_DARK},
                {"d", ColourEnum.COLOUR_DARK}, {" ", ColourEnum.COLOUR_DARK},
                {"sl", ColourEnum.COLOUR_WHITE}, {"Mauve", ColourEnum.COLOUR_WHITE},
                {"Light Dark", ColourEnum.COLOUR_LIGHT_DARK},
        };
        for (Object[] c : cases) {
            ParseResult<Projection> result = load("colour.txt", shipped().replaceFirst("color:Slate", "color:" + c[0]));
            assertFalse(result.hasErrors(), c[0] + ": " + result.errors());
            assertEquals(c[1], field(result.items().getFirst(), "colour"), "color:" + c[0]);
        }
    }

    /**
     * {@code parse_random} forms, with the values C gives. A form with no dice, sides or bonus
     * stays a plain integer.
     */
    @Test
    void denominatorFormsGiveTheValuesOfCsParseRandom() throws Exception {
        Object[][] cases = {
                {"3", 3, 0, 0, 0}, {"0", 0, 0, 0, 0}, {"08", 8, 0, 0, 0}, {"-3", -3, 0, 0, 0},
                {"5 ", 5, 0, 0, 0}, {"d4", 0, 1, 4, 0}, {"1d4", 0, 1, 4, 0}, {"5+1d4", 5, 1, 4, 0},
                {"5+d4", 5, 1, 4, 0}, {"2d6M3", 0, 2, 6, 3}, {"5+d4M2", 5, 1, 4, 2},
                {"M2", 0, 0, 0, 2}, {"5+M2", 5, 0, 0, 2}, {"-5+1d4", -10, 1, 4, 0},
                {"-d4", -5, 1, 4, 0}, {"-2d6M3", -17, 2, 6, 3}, {"-M2", -2, 0, 0, 2},
        };
        for (Object[] c : cases) {
            ParseResult<Projection> result = load("den.txt", shipped().replaceFirst("denominator:3\n", "denominator:" + c[0] + "\n"));
            assertFalse(result.hasErrors(), c[0] + ": " + result.errors());
            assertDenominator(result.items().getFirst(), (int) c[1], (int) c[2], (int) c[3], (int) c[4],
                    "denominator:" + c[0]);
        }
    }

    /**
     * C reads {@code obvious:} and {@code wake:} as a {@code uint} and tests it against 1, so any
     * other number, including 2 and the padded 01, is true only for 1.
     */
    @Test
    void obviousAndWakeAreTrueOnlyForTheNumberOne() throws Exception {
        String text = shipped().replaceFirst("obvious:1", "obvious:2").replaceFirst("wake:1", "wake:01");
        ParseResult<Projection> result = load("bool.txt", text);

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertFalse((boolean) field(result.items().getFirst(), "isObvious"));
        assertTrue((boolean) field(result.items().getFirst(), "willWake"));
    }

    // ---- Where the grammar differs from C (docs/grammar_issues.md) -----------------------------

    @Test
    void aRepeatedOrMisplacedDirectiveIsAHardErrorWhereCTakesAnyOrder() throws IOException {
        // C replaces a repeated string and takes the directives in any order.
        assertHardError(load("dup-desc.txt", shipped().replaceFirst("(?m)^(desc:.*)$", "$1\n$1x")));
        assertHardError(load("late-name.txt", shipped().replaceFirst("(?m)^(type:element)\n", "$1\nname:acid\n")));
        assertHardError(load("pre-code.txt", shipped().replaceFirst("(?m)^code:ACID", "name:x\ncode:ACID")));
    }

    @Test
    void aLeftOutTypeDescBlindDescObviousOrColourIsAHardErrorWhereCLeavesItZero() throws IOException {
        for (String directive : List.of("type:", "desc:", "blind-desc:", "obvious:", "color:")) {
            ParseResult<Projection> result = load("missing.txt",
                    shipped().replaceFirst("(?m)^" + directive + ".*\n", ""));
            assertHardError(result);
            assertTrue(result.errors().getFirst().contains("mismatched input"), directive + ": " + result.errors());
        }
    }

    @Test
    void theUpstreamFileWithNoRecordCountHeaderIsAHardError() throws IOException {
        // The header is Java-only. The shipped file has it added to the upstream text.
        ParseResult<Projection> result = load("headerless.txt", shipped().replace("record-count:56\n", ""));

        assertHardError(result);
        assertTrue(result.errors().getFirst().contains("expecting 'record-count:'"), result.errors()::toString);
    }

    @Test
    void aHeaderWithNoRecordsAndAnEmptyFileAreHardErrors() throws IOException {
        assertHardError(load("header-only.txt", "record-count:0\n"));
        assertHardError(load("empty.txt", ""));
    }

    @Test
    void aLowerCaseOrNumericMsgtIsAHardErrorWhereCAcceptsBoth() throws IOException {
        // message_lookup_by_name matches names with my_stricmp and takes a number below MSG_MAX.
        for (String msgt : List.of("br_acid", "20")) {
            ParseResult<Projection> result = load("msgt.txt", shipped().replaceFirst("msgt:BR_ACID", "msgt:" + msgt));
            assertHardError(result);
            assertTrue(result.errors().getFirst().contains("token recognition error"), msgt + ": " + result.errors());
        }
    }

    @Test
    void aBlankAfterCodeOrMsgtIsDroppedWhereCKeepsItAndRejectsTheLine() throws IOException {
        // C keeps the trailing blank in a str or sym: "ACID " is not element_names[0], and
        // "BR_ACID " is not a message name.
        for (String edit : List.of("code:ACID", "msgt:BR_ACID")) {
            ParseResult<Projection> result = load("blank.txt", shipped().replaceFirst(edit, edit + " "));
            assertFalse(result.hasErrors(), edit + ": " + result.errors());
            assertEquals(56, result.items().size(), edit);
        }
    }

    @Test
    void aBlankAfterATypeOrColourIsKeptAsCKeepsIt() throws Exception {
        // "element " is not "element", so ACID stops counting as an element and the load stops on the
        // 25-element rule, as in C's finish_parse_projection. "Slate " matches no colour name: White.
        DataFileParseException type = loadAndStop("type-blank.txt", shipped().replaceFirst("type:element", "type:element "));
        assertTrue(type.getMessage().contains("expected: 25 got: 24"), type.getMessage());

        ParseResult<Projection> colour = load("colour-blank.txt", shipped().replaceFirst("color:Slate", "color:Slate "));
        assertEquals(ColourEnum.COLOUR_WHITE, field(colour.items().getFirst(), "colour"));
    }

    @Test
    void aPlusSignHexPrefixTrailingTextOrLeadingBlankInAnIntegerIsAHardErrorWhereCAcceptsIt() throws IOException {
        // strtoul with base 0 skips leading blanks, takes +5 and 0x10, and ignores trailing text.
        for (String value : List.of("+1", "0x1", "1abc", " 1")) {
            assertHardError(load("int.txt", shipped().replaceFirst("numerator:1", "numerator:" + value)));
        }
    }

    @Test
    void aLeadingZeroUintReadsAsOctalAsCDoes() throws Exception {
        // strtoul base 0 reads 010 as 8, and the assembler reads a uint field the same way.
        ParseResult<Projection> result = load("octal.txt", shipped().replaceFirst("numerator:1", "numerator:010"));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(8, (int) field(result.items().getFirst(), "numerator"));
    }

    @Test
    void aLeadingBlankInADenominatorIsAHardErrorAndALowerCaseMIsAcceptedWhereCTakesTheOpposite() throws Exception {
        // parse_random skips leading blanks (strtoul) and takes only an upper-case M.
        assertHardError(load("den-blank.txt", shipped().replaceFirst("denominator:3\n", "denominator: 3\n")));

        ParseResult<Projection> result = load("den-m.txt", shipped().replaceFirst("denominator:3\n", "denominator:5+1d4m2\n"));
        assertFalse(result.hasErrors(), result.errors()::toString);
        assertDenominator(result.items().getFirst(), 5, 1, 4, 2, "denominator:5+1d4m2");
    }

    @Test
    void anIndentedDirectiveIsAHardErrorWhereCSkipsTheIndent() throws IOException {
        assertHardError(load("indent.txt", shipped().replaceFirst("(?m)^desc:acid", "  desc:acid")));
    }

    // ---- Where the assembler differs from C (docs/Assembler_Issues.md) -------------------------

    @Test
    void aLineLeftOutIsNullAsCHoldsNull() throws Exception {
        // LIGHT_WEAK has no name: and no lash-desc:, so C's strings are NULL.
        Projection lightWeak = new ProjectionReader().parseWithResults(REAL_FILE).items()
                .get(ProjectionEnum.PROJ_LIGHT_WEAK.ordinal() - 1);

        assertNull(field(lightWeak, "name"));
        assertNull(field(lightWeak, "lashDescription"));
    }

    @Test
    void aNumeratorAboveIntMaxIsClampedWhereCWrapsIt() throws Exception {
        // C reads a uint with strtoul and stores it in an int with no range check, so 2147483648
        // wraps to a negative number. The port clamps to the int range (the ParseUtils entry in
        // docs/Accepted_Deviations.md), so the record loads with Integer.MAX_VALUE.
        ParseResult<Projection> result = load("big.txt", shipped().replaceFirst("numerator:1", "numerator:2147483648"));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(Integer.MAX_VALUE, (int) field(result.items().getFirst(), "numerator"));
    }

    @Test
    void msgtNoneIsRefusedAsCHasNoSuchName() throws Exception {
        // message_names is built from list-message.h, which has no NONE (it does have MAX), so
        // message_lookup_by_name returns -1 for NONE and the parser quits, as it does for any
        // unknown name.
        DataFileParseException none = loadAndStop("msgt-none.txt", shipped().replaceFirst("msgt:BR_ACID", "msgt:NONE"));
        assertTrue(none.getMessage().contains("an illegal message value: NONE"), none.getMessage());

        ParseResult<Projection> max = load("msgt-max.txt", shipped().replaceFirst("msgt:BR_ACID", "msgt:MAX"));
        assertEquals(MessageType.MSG_MAX, field(max.items().getFirst(), "msgt"));

        DataFileParseException unknown = loadAndStop("msgt-unknown.txt",
                shipped().replaceFirst("msgt:BR_ACID", "msgt:NOT_A_MSG"));
        assertTrue(unknown.getMessage().contains("an illegal message value: NOT_A_MSG"), unknown.getMessage());
    }

    /**
     * The assembler throws {@link DataFileParseException} for a misplaced element, and
     * {@code GrammarDriver} lets it through, so it comes out of {@code parseWithResults} with the
     * position, the ordinal it found and the errors gathered so far. C quits at that line.
     */
    @Test
    void aSwappedElementBlockStopsTheLoadWithTheMismatchInTheMessage() throws IOException {
        String swapped = shipped().replaceFirst("(?s)(code:ACID.*?color:Slate\n)(.*?)(code:ELEC.*?\n\n)", "$3$2$1");

        DataFileParseException e = loadAndStop("swapped.txt", swapped);

        assertTrue(e.getMessage().contains("Input ordinal: 2 is not equal to expected ordinal: 1"), e.getMessage());
    }

    @Test
    void anEmptyCodeStopsTheLoadAtAnyPosition() throws IOException {
        // An empty code: is PARSE_ERROR_MISSING_FIELD in C's parser_parse, a hard error wherever it
        // is. The grammar rejects it first, so it comes back as a hard error with no items; the
        // assembler's own empty-code stop is only reached with hand-built records.
        String empty = "\ncode:\ntype:monster\ndesc:x\nblind-desc:x\nobvious:1\ncolor:Slate\n";

        ParseResult<Projection> first = load("empty-first.txt",
                shipped().replaceFirst("code:ACID", "code:"));
        ParseResult<Projection> after = load("empty-after.txt",
                shipped().replace("record-count:56", "record-count:57") + empty);

        assertHardError(first);
        assertHardError(after);
    }

    // ---- Expected values, worked out from the upstream projection.txt ---------------------------

    private record Row(String code, String name, String type, String desc, String playerDesc,
                       String blindDesc, String lashDesc, int numerator, int denBase, int denDice,
                       int denSides, int denMBonus, int divisor, int damageCap, String msgt,
                       boolean obvious, boolean wake, String colour) {
    }

    private static Row row(String code, String name, String type, String desc, String playerDesc,
                           String blindDesc, String lashDesc, int numerator, int denBase, int denDice,
                           int denSides, int denMBonus, int divisor, int damageCap, String msgt,
                           boolean obvious, boolean wake, String colour) {
        return new Row(code, name, type, desc, playerDesc, blindDesc, lashDesc, numerator, denBase,
                denDice, denSides, denMBonus, divisor, damageCap, msgt, obvious, wake, colour);
    }

    /**
     * One row per record of the upstream file, in file order. The colour is C's answer, so
     * {@code DARK_WEAK} is White here and the test above adjusts for the shipped file.
     */
    private static final List<Row> ROWS = List.of(
            row("ACID", "acid", "element", "acid", "acid", "acid", "acid", 1, 3, 0, 0, 0, 3, 1600, "MSG_BR_ACID", true, true, "Slate"),
            row("ELEC", "lightning", "element", "lightning", "lightning", "lightning", "lightning", 1, 3, 0, 0, 0, 3, 1600, "MSG_BR_ELEC", true, true, "Blue"),
            row("FIRE", "fire", "element", "fire", "fire", "fire", "fire", 1, 3, 0, 0, 0, 3, 1600, "MSG_BR_FIRE", true, true, "Red"),
            row("COLD", "cold", "element", "cold", "frost", "cold", "frost", 1, 3, 0, 0, 0, 3, 1600, "MSG_BR_FROST", true, true, "White"),
            row("POIS", "poison", "element", "poison", "poison gas", "poison", "venom", 1, 3, 0, 0, 0, 3, 800, "MSG_BR_GAS", true, true, "Green"),
            row("LIGHT", "light", "element", "light", "light", "something", "brightness", 6, 8, 1, 4, 0, 6, 500, "MSG_BR_LIGHT", true, true, "Orange"),
            row("DARK", "dark", "element", "dark", "dark", "something", "blackness", 6, 8, 1, 4, 0, 6, 500, "MSG_BR_DARK", true, true, "Light Dark"),
            row("SOUND", "sound", "element", "sound", "sound", "noise", "sound", 6, 8, 1, 4, 0, 6, 500, "MSG_BR_SOUND", true, true, "Yellow"),
            row("SHARD", "shards", "element", "shards", "shards", "something sharp", "razors", 6, 8, 1, 4, 0, 6, 500, "MSG_BR_SHARDS", true, true, "Umber"),
            row("NEXUS", "nexus", "element", "nexus", "nexus", "something strange", "dislocation", 6, 8, 1, 4, 0, 6, 400, "MSG_BR_NEXUS", true, true, "Light Red"),
            row("NETHER", "nether", "element", "nether", "nether", "something cold", "withering", 6, 8, 1, 4, 0, 6, 600, "MSG_BR_NETHER", true, true, "Light Green"),
            row("CHAOS", "chaos", "element", "chaos", "chaos", "something strange", "confusion", 6, 8, 1, 4, 0, 6, 600, "MSG_BR_CHAOS", true, true, "Violet"),
            row("DISEN", "disenchantment", "element", "disenchantment", "disenchantment", "something strange", "unmagic", 6, 8, 1, 4, 0, 6, 500, "MSG_BR_DISEN", true, true, "Violet"),
            row("WATER", "water", "element", "water", "water", "water", "water", 0, 0, 0, 0, 0, 6, 0, "MSG_GENERIC", true, true, "Slate"),
            row("ICE", "ice", "element", "ice", "ice", "something sharp", "icicles", 1, 3, 0, 0, 0, 6, 0, "MSG_GENERIC", true, true, "White"),
            row("GRAVITY", "gravity", "element", "gravity", "gravity", "something strange", "gravity", 0, 0, 0, 0, 0, 3, 200, "MSG_BR_GRAVITY", true, true, "Light Slate"),
            row("INERTIA", "inertia", "element", "inertia", "inertia", "something strange", "lassitude", 0, 0, 0, 0, 0, 6, 200, "MSG_BR_INERTIA", true, true, "Light Slate"),
            row("FORCE", "force", "element", "force", "force", "something hard", "impact", 0, 0, 0, 0, 0, 6, 200, "MSG_BR_FORCE", true, true, "Umber"),
            row("TIME", "time", "element", "time", "time", "something strange", "ruination", 0, 0, 0, 0, 0, 3, 150, "MSG_BR_TIME", true, true, "Light Blue"),
            row("PLASMA", "plasma", "element", "plasma", "plasma", "something", "living fire", 0, 0, 0, 0, 0, 6, 150, "MSG_BR_PLASMA", true, true, "Red"),
            row("METEOR", "meteors", "element", "a meteor", "meteor", "something", "meteoric iron", 0, 0, 0, 0, 0, 6, 0, "MSG_GENERIC", true, true, "Red"),
            row("MISSILE", "magic missiles", "element", "a missile", "magical energy", "something", null, 0, 0, 0, 0, 0, 6, 0, "MSG_GENERIC", true, true, "Violet"),
            row("MANA", "mana", "element", "mana", "the elements", "something", "raw magic", 0, 0, 0, 0, 0, 3, 1600, "MSG_BR_ELEMENTS", true, true, "Light Dark"),
            row("HOLY_ORB", "holy power", "element", "a holy orb", "holy power", "something", "holy power", 0, 0, 0, 0, 0, 6, 0, "MSG_GENERIC", true, true, "Light Dark"),
            row("ARROW", "arrows", "element", "an arrow", "arrows", "something sharp", null, 0, 0, 0, 0, 0, 6, 0, "MSG_GENERIC", true, true, "White"),
            row("LIGHT_WEAK", null, "environs", "light", "weak light", "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, false, "Orange"),
            row("DARK_WEAK", null, "environs", "darkness", "darkness", "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("KILL_WALL", null, "environs", "rock remover", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, false, "White"),
            row("KILL_DOOR", null, "environs", "destroys doors", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("KILL_TRAP", null, "environs", "disables traps", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("MAKE_DOOR", null, "environs", "creates doors", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("MAKE_TRAP", null, "environs", "creates traps", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("AWAY_UNDEAD", null, "monster", "teleports undead away", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("AWAY_EVIL", null, "monster", "teleports evil monsters away", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("AWAY_SPIRIT", null, "monster", "teleports monsters with a spirit away", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("AWAY_ALL", null, "monster", "teleports monsters away", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, true, "White"),
            row("TURN_UNDEAD", null, "monster", "turns undead", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("TURN_EVIL", null, "monster", "frightens evil monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("TURN_LIVING", null, "monster", "scares living monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("TURN_ALL", null, "monster", "causes monsters to flee", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, true, "White"),
            row("DISP_UNDEAD", null, "monster", "damages undead", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("DISP_EVIL", null, "monster", "damages evil monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("DISP_ALL", null, "monster", "damages all monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, true, "White"),
            row("SLEEP_UNDEAD", null, "monster", "attempts to put undead to sleep", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("SLEEP_EVIL", null, "monster", "attempts to put evil monsters to sleep", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("SLEEP_ALL", null, "monster", "attempts to put all monsters to sleep", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("MON_CLONE", null, "monster", "hastes, heals and magically duplicates monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, false, "White"),
            row("MON_POLY", null, "monster", "polymorphs monsters into other kinds of creatures", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("MON_HEAL", null, "monster", "heals monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, true, "White"),
            row("MON_SPEED", null, "monster", "hastes monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, true, "White"),
            row("MON_SLOW", null, "monster", "attempts to slow monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, true, "White"),
            row("MON_CONF", null, "monster", "attempts to confuse monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("MON_HOLD", null, "monster", "attempts to hold monsters still", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("MON_STUN", null, "monster", "attempts to stun monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", false, false, "White"),
            row("MON_DRAIN", null, "monster", "damages living monsters", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, true, "White"),
            row("MON_CRUSH", null, "monster", "kills monsters below a hitpoint threshold", null, "something", null, 0, 0, 0, 0, 0, 0, 0, "MSG_GENERIC", true, false, "White")
    );
}
