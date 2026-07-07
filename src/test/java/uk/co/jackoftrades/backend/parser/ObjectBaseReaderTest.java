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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.backend.parser.grammars.objectbase.ObjectBaseGrammar;
import uk.co.jackoftrades.backend.parser.grammars.objectbase.ObjectBaseLexer;
import uk.co.jackoftrades.backend.parser.objectbase.ObjectBaseParseRecord;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link ObjectBaseReader} (grammar-suite reader track), driving
 * {@code ObjectBaseLexer}/{@code ObjectBaseGrammar} through the shared {@link GrammarDriver} into
 * {@link ObjectBase} objects.
 *
 * <p>These pin the behaviours we fought through while porting the assembler and domain class: the
 * flag {@code Flag}-set initialisation (an NPE regression), {@code dragon armor} tval resolution,
 * the file-wide break/max-stack default folding, colour resolution (not silently DARK), and the
 * skip-and-continue soft-error contract. Because {@link ObjectBase} exposes no accessor for its two
 * flag sets, the repeated-{@code flags:} <em>merge</em> is asserted one level down - directly on the
 * {@link ObjectBaseParseRecord} the grammar produces.
 *
 * <p>Every synthetic fixture carries the header the {@code file} rule requires - a
 * {@code record-count:} line and at least one {@code default:} line - via {@link #withHeader}.
 *
 * @author Rowan Crowther
 */
class ObjectBaseReaderTest {

    private static final String REAL_FILE = "lib/gamedata/object_base.txt";

    /**
     * The two file-wide defaults the {@code file} rule requires ({@code (default_value)+}).
     */
    private static final String DEFAULTS = "default:break-chance:10\ndefault:max-stack:40\n";

    @TempDir
    Path tempDir;

    /**
     * Prefix a record body with a matching {@code record-count:} and the mandatory defaults.
     */
    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + DEFAULTS + body;
    }

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    private ParseResult<ObjectBase> load(String name, String content) throws IOException {
        return new ObjectBaseReader().parseWithResults(tempFile(name, content));
    }

    // ---- Happy path: the real shipped file -------------------------------

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAll34Bases() throws IOException {
        ParseResult<ObjectBase> result = new ObjectBaseReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(34, result.items().size(), () -> "loaded: " + result.items().size());
    }

    // ---- Minimal record + default folding --------------------------------

    @Test
    void minimalRecordLoadsAndInheritsTheFileDefaults() throws IOException {
        // No break:/max-stack: on the record, so both come from the file-wide defaults folded in
        // by the grammar's file rule.
        ParseResult<ObjectBase> result =
                load("minimal.txt", withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        ObjectBase base = result.items().get(0);
        assertEquals(TValue.TV_SWORD, base.gettVal());
        assertEquals(10, base.getBreakPerc(), "break chance should fall back to the default 10");
        assertEquals(40, base.getMaxStack(), "max stack should fall back to the default 40");
    }

    @Test
    void explicitBreakAndMaxStackOverrideTheDefaults() throws IOException {
        ParseResult<ObjectBase> result = load("override.txt",
                withHeader(1, "name:shot:Shot~\ngraphics:light umber\nbreak:0\nmax-stack:99\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        ObjectBase base = result.items().get(0);
        assertEquals(0, base.getBreakPerc());
        assertEquals(99, base.getMaxStack());
    }

    // ---- tval resolution edge cases --------------------------------------

    @Test
    void dragonArmorResolvesToItsTVal() throws IOException {
        // Regression for the "DRAG__ARMOR" double-underscore bug: "dragon armor" must resolve.
        ParseResult<ObjectBase> result = load("dragon.txt",
                withHeader(1, "name:dragon armor:Dragon Armor~\ngraphics:white\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(TValue.TV_DRAGON_ARMOR, result.items().get(0).gettVal());
    }

    @Test
    void noPluralNameResolvesTheTVal() throws IOException {
        // "name:gold" has no ":plural" part - exercises the second `name` alternative and the
        // T_VAL lexer rule that must stop at the newline.
        ParseResult<ObjectBase> result = load("gold.txt",
                withHeader(1, "name:gold\ngraphics:light yellow\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(TValue.TV_GOLD, result.items().get(0).gettVal());
    }

    // ---- Colour resolution (must not collapse to DARK) -------------------

    @Test
    void colourResolvesToTheNamedColourNotTheDarkDefault() throws IOException {
        ParseResult<ObjectBase> result = load("colour.txt",
                withHeader(1, "name:chest:Chest~\ngraphics:slate\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        ColourType attr = result.items().get(0).getAttr();
        assertEquals(ColourType.getColourType("slate"), attr);
        assertNotEquals(ColourType.COLOUR_TYPE_DARK, attr, "colour should resolve, not fall back to DARK");
    }

    // ---- Repeated flags: lines merge (asserted on the parse record) ------

    @Test
    void repeatedFlagsLinesMergeRatherThanReplace() {
        // The merge happens in the grammar (object_base's `(flags {...})*` addAll). ObjectBase
        // exposes no flag accessor, so assert on the ObjectBaseParseRecord the grammar builds.
        String src = withHeader(1,
                "name:arrow:Arrow~\ngraphics:light umber\nflags:HATES_ACID | HATES_FIRE\nflags:SHOW_DICE\n");
        ObjectBaseLexer lexer = new ObjectBaseLexer(CharStreams.fromString(src));
        ObjectBaseGrammar parser = new ObjectBaseGrammar(new CommonTokenStream(lexer));

        List<ObjectBaseParseRecord> records = parser.file().objectBaseList;

        assertEquals(1, records.size());
        assertTrue(records.get(0).flags().containsAll(List.of("HATES_ACID", "HATES_FIRE", "SHOW_DICE")),
                () -> "expected all three flags, got: " + records.get(0).flags());
    }

    // ---- One test per assembler soft-error branch ------------------------

    @Test
    void unknownTValIsReportedAndRecordSkipped() throws IOException {
        ParseResult<ObjectBase> result = load("bad-tval.txt",
                withHeader(1, "name:notatval:Thing~\ngraphics:white\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("invalid TValue") && e.contains("NOTATVAL")),
                result.errors()::toString);
    }

    @Test
    void unknownKindFlagIsReportedAndRecordSkipped() throws IOException {
        ParseResult<ObjectBase> result = load("bad-flag.txt",
                withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\nflags:NOTAFLAG\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("invalid non-HATES_ flag") && e.contains("NOTAFLAG")),
                result.errors()::toString);
    }

    @Test
    void unknownHatesFlagIsReportedAndRecordSkipped() throws IOException {
        ParseResult<ObjectBase> result = load("bad-hates.txt",
                withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\nflags:HATES_NOTANELEMENT\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("invalid HATES_ flag") && e.contains("HATES_NOTANELEMENT")),
                result.errors()::toString);
    }

    @Test
    void overflowingBreakChanceIsReportedAndRecordSkipped() throws IOException {
        // The INTEGER token guarantees digits, so the only route to the NumberFormatException
        // branch is a value too large for int.
        ParseResult<ObjectBase> result = load("bad-break.txt",
                withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\nbreak:99999999999999999999\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid break chance")),
                result.errors()::toString);
    }

    // ---- Soft record-count mismatch --------------------------------------

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        // Header over-declares (5 vs the single record). Soft: the record still loads.
        ParseResult<ObjectBase> result = load("bad-count.txt",
                withHeader(5, "name:sword:Bladed weapon~\ngraphics:white\n"));

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    // ---- FLAG token: digit-bearing tail ----------------------------------

    @Test
    void digitBearingFlagLexesAsASingleFlagAndReachesTheAssembler() throws IOException {
        // FLAG_BODY allows a digit tail; SHOW_2 is not a real KF_ flag, so it must surface as the
        // assembler's soft "invalid non-HATES_ flag: SHOW_2" - proof it lexed as one FLAG rather
        // than a hard parse error splitting on the digit.
        ParseResult<ObjectBase> result = load("digit-flag.txt",
                withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\nflags:SHOW_2\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("invalid non-HATES_ flag") && e.contains("SHOW_2")),
                result.errors()::toString);
    }
}
