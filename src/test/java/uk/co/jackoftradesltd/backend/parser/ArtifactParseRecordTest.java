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

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.artifact.ArtifactParseRecord;
import uk.co.jackoftradesltd.backend.parser.artifact.ArtifactParseRecord.ArtifactBaseObjectParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.artifact.ArtifactGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.artifact.ArtifactLexer;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests of {@link ArtifactParseRecord}: what {@code ArtifactGrammar} puts in each component for
 * a given {@code artifact.txt} text, before {@code ArtifactAssembler} resolves anything. The
 * grammar is driven directly, so no registry is seeded and no kind is made.
 *
 * <p>The expected values come from C's {@code parse_artifact_*} handlers in {@code obj-init.c}:
 * a directive that is absent leaves the zero-filled {@code struct artifact} value, so the record
 * must hold the sentinel that stands for it ({@code ""}, an empty list or map, or {@code null}
 * for {@code desc}); {@code msg:} and {@code desc:} are joined with {@code string_append}; a
 * later scalar write replaces an earlier one; and {@code write_dummy_object_record} copies the
 * artifact's level at the moment {@code base-object:} is read, which is why each base-object
 * line carries the level in force at that line.
 *
 * <p>Class ArtifactParseRecordTest coded on 261107, commented in full on 261107.
 *
 * @author Rowan Crowther
 */
class ArtifactParseRecordTest {

    /**
     * Turns any lexer or parser complaint into an exception, so a test text the grammar cannot
     * take fails loudly instead of printing to the console and returning half a record.
     *
     * <p>Field THROWING coded on 261107, commented in full on 261107.
     */
    private static final BaseErrorListener THROWING = new BaseErrorListener() {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                int charPositionInLine, String msg, RecognitionException e) {
            throw new IllegalStateException("line " + line + ":" + charPositionInLine + " " + msg);
        }
    };

    /**
     * Runs {@code ArtifactLexer} and {@code ArtifactGrammar} over {@code text} and returns the
     * records, in file order.
     *
     * <p>Function parse coded on 261107, commented in full on 261107.
     *
     * @param text the artifact file text
     * @return the grammar's records
     */
    private static List<ArtifactParseRecord> parse(String text) {
        ArtifactLexer lexer = new ArtifactLexer(CharStreams.fromString(text));
        lexer.removeErrorListeners();
        lexer.addErrorListener(THROWING);
        ArtifactGrammar parser = new ArtifactGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(THROWING);
        return parser.file().records;
    }

    /**
     * A record with only {@code name:} and {@code base-object:} holds the sentinel for every
     * other directive. C's zero-filled artifact gives 0 and {@code NULL} there; the empty string
     * parses to 0, and {@code desc} alone is {@code null} because C's {@code text} starts
     * {@code NULL}.
     *
     * <p>Function absentDirectivesLeaveTheirSentinels coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void absentDirectivesLeaveTheirSentinels() {
        ArtifactParseRecord r = parse("name:of Bare\nbase-object:light:Phial\n").getFirst();

        assertEquals("of Bare", r.name());
        assertEquals(List.of(new ArtifactBaseObjectParseRecord("light", "Phial", "")),
                r.baseObjects());
        assertEquals("", r.glyph());
        assertEquals("", r.colour());
        assertEquals("", r.level());
        assertEquals("", r.weight());
        assertEquals("", r.cost());
        assertEquals("", r.commonness());
        assertEquals("", r.min());
        assertEquals("", r.max());
        assertEquals("", r.baseDamage());
        assertEquals("", r.toh());
        assertEquals("", r.tod());
        assertEquals("", r.baseAC());
        assertEquals("", r.toa());
        assertEquals("", r.activation());
        assertEquals("", r.time());
        assertEquals("", r.msg());
        assertTrue(r.flagList().isEmpty());
        assertTrue(r.values().isEmpty());
        assertTrue(r.brand().isEmpty());
        assertTrue(r.slay().isEmpty());
        assertTrue(r.curse().isEmpty());
        assertNull(r.desc());
        assertEquals(1, r.line());
    }

    /**
     * Every directive of a shipped-style record lands in its own component as raw text, with
     * the figures still unparsed.
     *
     * <p>Function everyDirectiveLandsInItsOwnComponent coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void everyDirectiveLandsInItsOwnComponent() {
        String text = """
                name:of Galadriel
                base-object:light:Phial
                graphics:~:y
                level:5
                weight:10
                cost:10000
                alloc:40:5 to 100
                attack:1d1:2:3
                armor:4:5
                flags:NO_FUEL
                values:LIGHT[4]
                act:ILLUMINATION
                time:10+d10
                msg:It shines.
                desc:A small crystal phial.
                """;
        ArtifactParseRecord r = parse(text).getFirst();

        assertEquals("of Galadriel", r.name());
        assertEquals(List.of(new ArtifactBaseObjectParseRecord("light", "Phial", "")),
                r.baseObjects());
        assertEquals("~", r.glyph());
        assertEquals("y", r.colour());
        assertEquals("5", r.level());
        assertEquals("10", r.weight());
        assertEquals("10000", r.cost());
        assertEquals("40", r.commonness());
        assertEquals("5", r.min());
        assertEquals("100", r.max());
        assertEquals("1d1", r.baseDamage());
        assertEquals("2", r.toh());
        assertEquals("3", r.tod());
        assertEquals("4", r.baseAC());
        assertEquals("5", r.toa());
        assertEquals(List.of("NO_FUEL"), r.flagList());
        assertEquals(Map.of("LIGHT", "4"), r.values());
        assertEquals("ILLUMINATION", r.activation());
        assertEquals("10+d10", r.time());
        assertEquals("It shines.", r.msg());
        assertEquals("A small crystal phial.", r.desc());
    }

    /**
     * {@code msg:} and {@code desc:} lines are joined in file order with no separator, as C's
     * {@code string_append} does.
     *
     * <p>Function msgAndDescLinesAreJoinedInFileOrder coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void msgAndDescLinesAreJoinedInFileOrder() {
        String text = """
                name:of Joined
                base-object:light:Phial
                msg:AB
                desc:12
                msg:CD
                desc:34
                """;
        ArtifactParseRecord r = parse(text).getFirst();

        assertEquals("ABCD", r.msg());
        assertEquals("1234", r.desc());
    }

    /**
     * A repeated scalar directive keeps its last line, as C's later write replaces the earlier.
     *
     * <p>Function aRepeatedScalarDirectiveKeepsTheLastLine coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void aRepeatedScalarDirectiveKeepsTheLastLine() {
        String text = """
                name:of Twice
                base-object:light:Phial
                level:1
                weight:7
                level:2
                weight:9
                """;
        ArtifactParseRecord r = parse(text).getFirst();

        assertEquals("2", r.level());
        assertEquals("9", r.weight());
    }

    /**
     * {@code flags:}, {@code brand:}, {@code slay:} and {@code curse:} lines add up in file
     * order, a duplicate included; C's flag bits and its per-code booleans accumulate the same
     * way, and the assembler is where a duplicate stops mattering.
     *
     * <p>Function accumulatingDirectivesAddUpInFileOrder coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void accumulatingDirectivesAddUpInFileOrder() {
        String text = """
                name:of Many
                base-object:light:Phial
                flags:NO_FUEL | FREE_ACT
                brand:ACID_3
                slay:EVIL_2
                flags:IGNORE_FIRE
                brand:FIRE_3
                brand:ACID_3
                slay:ORC_3
                curse:air swing:30
                curse:teleportation:0
                """;
        ArtifactParseRecord r = parse(text).getFirst();

        assertEquals(List.of("NO_FUEL", "FREE_ACT", "IGNORE_FIRE"), r.flagList());
        assertEquals(List.of("ACID_3", "FIRE_3", "ACID_3"), r.brand());
        assertEquals(List.of("EVIL_2", "ORC_3"), r.slay());
        assertEquals(2, r.curse().size());
        assertEquals("air swing", r.curse().get(0).name());
        assertEquals("30", r.curse().get(0).power());
        assertEquals("teleportation", r.curse().get(1).name());
        assertEquals("0", r.curse().get(1).power());
    }

    /**
     * {@code values:} lines add up, and a repeated label keeps its last figure, as C's later
     * write to {@code modifiers[]} or {@code el_info[].res_level} replaces the earlier. A
     * {@code RES_} label stays a separate key from the modifier of the same stem.
     *
     * <p>Function aRepeatedValuesLabelKeepsItsLastFigure coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void aRepeatedValuesLabelKeepsItsLastFigure() {
        String text = """
                name:of Values
                base-object:light:Phial
                values:STR[1] | RES_FIRE[1]
                values:DEX[2]
                values:STR[3]
                """;
        ArtifactParseRecord r = parse(text).getFirst();

        assertEquals(Map.of("STR", "3", "RES_FIRE", "1", "DEX", "2"), r.values());
    }

    /**
     * Each {@code base-object:} line carries the {@code level:} text in force at that line, as
     * {@code write_dummy_object_record} copies {@code art->level} when it makes the kind: a
     * {@code level:} line before it is seen, one after it is not, and a second
     * {@code base-object:} line sees the level set between the two. The record's own level is
     * the last {@code level:} line either way.
     *
     * <p>Function eachBaseObjectLineCarriesTheLevelInForceAtThatLine coded on 261107, commented
     * in full on 261107.
     */
    @Test
    void eachBaseObjectLineCarriesTheLevelInForceAtThatLine() {
        String text = """
                name:level first
                level:30
                base-object:light:Phial
                name:level after
                base-object:light:Star
                level:30
                name:level between
                base-object:light:Phial
                level:5
                base-object:light:Star
                level:30
                """;
        List<ArtifactParseRecord> records = parse(text);

        assertEquals(3, records.size());
        assertEquals(List.of(new ArtifactBaseObjectParseRecord("light", "Phial", "30")),
                records.get(0).baseObjects());
        assertEquals("30", records.get(0).level());
        assertEquals(List.of(new ArtifactBaseObjectParseRecord("light", "Star", "")),
                records.get(1).baseObjects());
        assertEquals("30", records.get(1).level());
        assertEquals(List.of(new ArtifactBaseObjectParseRecord("light", "Phial", ""),
                        new ArtifactBaseObjectParseRecord("light", "Star", "5")),
                records.get(2).baseObjects());
        assertEquals("30", records.get(2).level());
    }

    /**
     * Records come back in file order, each starting clean: nothing a record accumulated leaks
     * into the next, and {@code line} is the line of that record's own {@code name:} directive,
     * comments and blank lines counted.
     *
     * <p>Function recordsStartCleanAndKeepTheirNameLine coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void recordsStartCleanAndKeepTheirNameLine() {
        String text = """
                # a comment

                name:first
                base-object:light:Phial
                flags:NO_FUEL
                brand:ACID_3
                desc:kept
                values:STR[1]

                name:second
                base-object:light:Star
                """;
        List<ArtifactParseRecord> records = parse(text);

        assertEquals(2, records.size());
        assertEquals("first", records.get(0).name());
        assertEquals(3, records.get(0).line());
        assertEquals("second", records.get(1).name());
        assertEquals(10, records.get(1).line());
        assertTrue(records.get(1).flagList().isEmpty());
        assertTrue(records.get(1).brand().isEmpty());
        assertTrue(records.get(1).values().isEmpty());
        assertNull(records.get(1).desc());
    }
}
