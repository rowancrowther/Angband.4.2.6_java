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
import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.itemobject.ItemObjectGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.itemobject.ItemObjectLexer;
import uk.co.jackoftradesltd.backend.parser.itemobject.ItemObjectParseRecord;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests of {@link ItemObjectParseRecord}: what {@code ItemObjectGrammar} puts in each component
 * for a given {@code object.txt} text, before {@code ItemObjectAssembler} resolves anything. The
 * grammar is driven directly, so no registry is seeded and no kind is made.
 *
 * <p>The expected values come from C's {@code parse_object_*} handlers in {@code obj-init.c}: a
 * directive that is absent leaves the zero-filled {@code struct object_kind} value, so the record
 * must hold the sentinel that stands for it ({@code ""}, an empty list or map, or {@code null}
 * for the three accumulated texts); {@code msg:}, {@code vis-msg:} and {@code desc:} are joined
 * with {@code string_append}; a later scalar write replaces an earlier one; and
 * {@code brand:}/{@code slay:} set a flag in a per-kind array, so a repeat is harmless there.
 *
 * <p>Class ItemObjectParseRecordTest coded on 261107, commented in full on 261107.
 *
 * @author Rowan Crowther
 */
class ItemObjectParseRecordTest {

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
     * Runs {@code ItemObjectLexer} and {@code ItemObjectGrammar} over {@code text} and returns
     * the records, in file order.
     *
     * <p>Function parse coded on 261107, commented in full on 261107.
     *
     * @param text the object file text
     * @return the grammar's records
     */
    private static List<ItemObjectParseRecord> parse(String text) {
        ItemObjectLexer lexer = new ItemObjectLexer(CharStreams.fromString(text));
        lexer.removeErrorListeners();
        lexer.addErrorListener(THROWING);
        ItemObjectGrammar parser = new ItemObjectGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(THROWING);
        return parser.file().itemObjects;
    }

    /**
     * The record's components are exactly the {@code object.txt} directives C registers in
     * {@code init_parse_object}, plus the source line. {@code attack}, {@code armor},
     * {@code alloc} and {@code pile} each fill several components, and the four effect
     * directives share one list, so the expected names are listed per directive.
     *
     * <p>Function componentsCoverEveryCDirective coded on 261107, commented in full on 261107.
     */
    @Test
    void componentsCoverEveryCDirective() {
        Set<String> expected = Set.of(
                "name",                                          // name
                "tValue",                                        // type
                "glyph", "colour",                               // graphics
                "level", "weight", "cost",                       // level, weight, cost
                "allocCommon", "allocLower", "allocUpper",       // alloc
                "attackBaseDamage", "attackToh", "attackTod",    // attack
                "armourBaseAC", "armourToa",                     // armor
                "charges",                                       // charges
                "pileChance", "pileNumber",                      // pile
                "flags",                                         // flags
                "power",                                         // power
                "effects",                                       // effect, effect-yx, dice, expr
                "message", "visMessage",                         // msg, vis-msg
                "time",                                          // time
                "pVal",                                          // pval
                "values",                                        // values
                "desc",                                          // desc
                "slay", "brand",                                 // slay, brand
                "curse",                                         // curse
                "line");                                         // source line, not a directive

        Set<String> actual = Arrays.stream(ItemObjectParseRecord.class.getRecordComponents())
                .map(RecordComponent::getName)
                .collect(Collectors.toSet());

        assertEquals(expected, actual);
        assertEquals(31, ItemObjectParseRecord.class.getRecordComponents().length);
    }

    /**
     * A record with only {@code name:} and {@code type:} holds the sentinel for every other
     * directive. C's zero-filled kind gives 0, an empty flag set and {@code NULL} text there; the
     * empty string parses to 0, and the three texts alone are {@code null} because C's
     * {@code text}, {@code effect_msg} and {@code vis_msg} start {@code NULL}.
     *
     * <p>Function absentDirectivesLeaveTheirSentinels coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void absentDirectivesLeaveTheirSentinels() {
        ItemObjectParseRecord r = parse("name:Bare\ntype:ring\n").getFirst();

        assertEquals("Bare", r.name());
        assertEquals("ring", r.tValue());
        assertEquals("", r.glyph());
        assertEquals("", r.colour());
        assertEquals("", r.level());
        assertEquals("", r.weight());
        assertEquals("", r.cost());
        assertEquals("", r.attackBaseDamage());
        assertEquals("", r.attackToh());
        assertEquals("", r.attackTod());
        assertEquals("", r.armourBaseAC());
        assertEquals("", r.armourToa());
        assertEquals("", r.allocCommon());
        assertEquals("", r.allocLower());
        assertEquals("", r.allocUpper());
        assertEquals("", r.charges());
        assertEquals("", r.pileChance());
        assertEquals("", r.pileNumber());
        assertEquals("", r.power());
        assertEquals("", r.pVal());
        assertEquals("", r.time());
        assertTrue(r.effects().isEmpty());
        assertTrue(r.flags().isEmpty());
        assertTrue(r.values().isEmpty());
        assertTrue(r.brand().isEmpty());
        assertTrue(r.slay().isEmpty());
        assertTrue(r.curse().isEmpty());
        assertNull(r.message());
        assertNull(r.visMessage());
        assertNull(r.desc());
        assertEquals(1, r.line());
    }

    /**
     * Every scalar directive of a shipped-style record lands in its own component as raw text,
     * with the figures still unparsed. The texts are the shapes C's {@code object.txt} uses
     * (a ring and a rod).
     *
     * <p>Function everyScalarDirectiveLandsInItsOwnComponent coded on 261107, commented in full
     * on 261107.
     */
    @Test
    void everyScalarDirectiveLandsInItsOwnComponent() {
        String text = """
                name:Protection
                type:ring
                graphics:=:d
                level:10
                weight:2
                cost:500
                alloc:50:10 to 40
                attack:1d2:3:4
                armor:7:5+d5M10
                pile:90:2d3
                power:3
                desc:It increases your armor class.
                """;
        ItemObjectParseRecord r = parse(text).getFirst();

        assertEquals("Protection", r.name());
        assertEquals("ring", r.tValue());
        assertEquals("=", r.glyph());
        assertEquals("d", r.colour());
        assertEquals("10", r.level());
        assertEquals("2", r.weight());
        assertEquals("500", r.cost());
        assertEquals("50", r.allocCommon());
        assertEquals("10", r.allocLower());
        assertEquals("40", r.allocUpper());
        assertEquals("1d2", r.attackBaseDamage());
        assertEquals("3", r.attackToh());
        assertEquals("4", r.attackTod());
        assertEquals("7", r.armourBaseAC());
        assertEquals("5+d5M10", r.armourToa());
        assertEquals("90", r.pileChance());
        assertEquals("2d3", r.pileNumber());
        assertEquals("3", r.power());
        assertEquals("It increases your armor class.", r.desc());
        assertEquals(1, r.line());
    }

    /**
     * The rod-shaped directives ({@code charges}, {@code time}, {@code pval}) and the list
     * directives ({@code flags}, {@code values}, {@code brand}, {@code slay}, {@code curse}) land
     * in their own components. {@code curse:} keeps its name and power and the grammar's
     * {@code "0"} timeout; {@code values:} carries both an object modifier and a {@code RES_}
     * element, as C's {@code parse_object_values} accepts.
     *
     * <p>Function listAndRodDirectivesLandInTheirOwnComponents coded on 261107, commented in
     * full on 261107.
     */
    @Test
    void listAndRodDirectivesLandInTheirOwnComponents() {
        String text = """
                name:Mixed
                type:wand
                charges:5+d5
                time:10+d10
                pval:2
                flags:EASY_KNOW | IGNORE_FIRE
                values:SPEED[2] | RES_FIRE[1]
                brand:FIRE_3
                slay:UNDEAD_3
                curse:teleportation:100
                """;
        ItemObjectParseRecord r = parse(text).getFirst();

        assertEquals("5+d5", r.charges());
        assertEquals("10+d10", r.time());
        assertEquals("2", r.pVal());
        assertEquals(List.of("EASY_KNOW", "IGNORE_FIRE"), r.flags());
        assertEquals(Map.of("SPEED", "2", "RES_FIRE", "1"), r.values());
        assertEquals(List.of("FIRE_3"), r.brand());
        assertEquals(List.of("UNDEAD_3"), r.slay());
        assertEquals(List.of(new FullCurseDataParseRecord("teleportation", "100", "0")),
                r.curse());
    }

    /**
     * {@code msg:}, {@code vis-msg:} and {@code desc:} lines are joined in file order with no
     * separator, as C's {@code string_append} does.
     *
     * <p>Function textLinesAreJoinedInFileOrder coded on 261107, commented in full on 261107.
     */
    @Test
    void textLinesAreJoinedInFileOrder() {
        String text = """
                name:Joined
                type:ring
                msg:AB
                vis-msg:12
                desc:wx
                msg:CD
                vis-msg:34
                desc:yz
                """;
        ItemObjectParseRecord r = parse(text).getFirst();

        assertEquals("ABCD", r.message());
        assertEquals("1234", r.visMessage());
        assertEquals("wxyz", r.desc());
    }

    /**
     * A later scalar directive replaces an earlier one, as each C handler overwrites the field
     * it sets.
     *
     * <p>Function laterScalarDirectiveWins coded on 261107, commented in full on 261107.
     */
    @Test
    void laterScalarDirectiveWins() {
        String text = """
                name:Twice
                type:ring
                level:5
                cost:100
                level:9
                """;
        ItemObjectParseRecord r = parse(text).getFirst();

        assertEquals("9", r.level());
        assertEquals("100", r.cost());
    }

    /**
     * A repeated {@code brand:} or {@code slay:} code is kept in file order. C sets a boolean in
     * a per-kind array, so a repeat is a no-op there; the record leaves the de-duplication to the
     * assembler.
     *
     * <p>Function repeatedBrandAndSlayCodesAreKeptInFileOrder coded on 261107, commented in full
     * on 261107.
     */
    @Test
    void repeatedBrandAndSlayCodesAreKeptInFileOrder() {
        String text = """
                name:Repeats
                type:sword
                brand:FIRE_3
                brand:COLD_3
                brand:FIRE_3
                slay:UNDEAD_3
                slay:UNDEAD_3
                """;
        ItemObjectParseRecord r = parse(text).getFirst();

        assertEquals(List.of("FIRE_3", "COLD_3", "FIRE_3"), r.brand());
        assertEquals(List.of("UNDEAD_3", "UNDEAD_3"), r.slay());
    }

    /**
     * The four effect directives fold into one {@code EffectParseRecord} per {@code effect:}
     * line, so a {@code dice:} line after an effect belongs to that effect, not to the record.
     * C's Cure Light Wounds potion has four effects, three of them followed by {@code dice:}.
     *
     * <p>Function effectBlocksFoldIntoOneRecordEach coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void effectBlocksFoldIntoOneRecordEach() {
        String text = """
                name:Cure Light Wounds
                type:potion
                effect:HEAL_HP
                dice:20
                effect:CURE:BLIND
                effect:TIMED_DEC:CUT
                dice:20
                effect:NOURISH:INC_BY
                dice:1
                """;
        ItemObjectParseRecord r = parse(text).getFirst();

        assertEquals(4, r.effects().size());
    }

    /**
     * Each record's {@code line} is the 1-based line of its own {@code name:} header, not the
     * first record's.
     *
     * <p>Function lineIsTheNameHeaderLineOfEachRecord coded on 261107, commented in full on
     * 261107.
     */
    @Test
    void lineIsTheNameHeaderLineOfEachRecord() {
        String text = """
                name:First
                type:ring
                level:1

                name:Second
                type:ring
                """;
        List<ItemObjectParseRecord> records = parse(text);

        assertEquals(2, records.size());
        assertEquals("First", records.get(0).name());
        assertEquals(1, records.get(0).line());
        assertEquals("Second", records.get(1).name());
        assertEquals(5, records.get(1).line());
    }
}
