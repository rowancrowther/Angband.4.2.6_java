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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffect2Assembler;
import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffect2ParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.monsters.BlowEffect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BlowEffect2Assembler}.
 *
 * <p>These build {@link BlowEffect2ParseRecord}s by hand, with no grammar and no reader, so a
 * {@link Directive} shape that the lexer can never produce (no values at all, two values, a
 * directive name the grammar does not write) still reaches the assembler. End-to-end cases, which
 * go through {@code blow_effects.txt} text, live in {@code BlowEffectReaderTest}.
 *
 * <p>The expected values are worked out from C's {@code mon-init.c} and {@code parser.c}, not from
 * the Java. {@code init_parse_eff} registers every one of the ten directives with a required field
 * ({@code name str name}, {@code power int power}, {@code eval int eval}, {@code desc str desc},
 * the three {@code lore-color-* sym color}, {@code effect-type str type}, {@code resist str resist}
 * and {@code lash-type str type}), and {@code parser_parse} returns {@code PARSE_ERROR_MISSING_FIELD}
 * for a required field with no text, which ends the load. So a directive with no value is a
 * refusal in C, never a record.
 *
 * <p>Class BlowEffect2AssemblerTest coded on 261117, commented in full on 261117.
 *
 * @author Rowan Crowther
 */
class BlowEffect2AssemblerTest {

    /**
     * The file name every test passes, which the assembler quotes in each error message.
     *
     * <p>Field FILE coded on 261117, commented in full on 261117.
     */
    private static final String FILE = "blow_effects.txt";

    /**
     * The real projection table, loaded from {@code projection.txt}.
     *
     * <p>Field PROJECTION_FILE coded on 261118, commented in full on 261118.
     */
    private static final String PROJECTION_FILE = "lib/gamedata/projection.txt";

    /**
     * The registry's own projection list, kept so {@link #restore()} can put it back.
     *
     * <p>Field savedProjections coded on 261118, commented in full on 261118.
     */
    private static Object savedProjections;

    /**
     * Loads the projection table into {@code WorldRegistry} before any test runs. Every record the
     * assembler builds starts from {@code new BlowEffect()}, whose default lash looks up the ACID
     * projection (C's zeroed {@code lash_type} 0), so even a record with no {@code lash:} line needs
     * the table. The dependency is an accepted deviation, because C stores only the index.
     *
     * <p>Function seed coded on 261118, commented in full on 261118.
     *
     * @throws Exception if the projection file cannot be read or the registry field reached
     */
    @BeforeAll
    static void seed() throws Exception {
        List<Projection> projections = new ProjectionReader().parseWithResults(PROJECTION_FILE).items();
        Field field = RegistrySeeding.resolve("projections");
        field.setAccessible(true);
        savedProjections = field.get(null);
        field.set(null, projections);
    }

    /**
     * Puts the registry's projection list back, so the suite's shared statics are as they were found.
     *
     * <p>Function restore coded on 261118, commented in full on 261118.
     *
     * @throws Exception if the registry field cannot be reached by reflection
     */
    @AfterAll
    static void restore() throws Exception {
        Field field = RegistrySeeding.resolve("projections");
        field.setAccessible(true);
        field.set(null, savedProjections);
    }

    /**
     * Runs the assembler over one record made of the given directives.
     *
     * <p>Function assemble coded on 261117, commented in full on 261117.
     *
     * @param directives the record's directives, in file order
     * @return the effects the assembler built, one per record
     */
    private static List<BlowEffect> assemble(List<Directive> directives) {
        return new BlowEffect2Assembler().assemble(List.of(new BlowEffect2ParseRecord(directives)), FILE);
    }

    /**
     * Each directive name the assembler reads, with the words its refusal message uses for the
     * value count. Only the directive under test is empty; a {@code name:} line comes first on
     * line 1 so that the check for a missing name does not fire ahead of it.
     *
     * <p>Function directiveNamesAndTheirNouns coded on 261117, commented in full on 261117.
     *
     * @return one row per directive: its assembler name, then the noun phrase in its message
     */
    private static Stream<Arguments> directiveNamesAndTheirNouns() {
        return Stream.of(
                Arguments.of("name", "name"),
                Arguments.of("power", "power"),
                Arguments.of("eval", "power evaluation"),
                Arguments.of("desc", "desc"),
                Arguments.of("base-colour", "base colour"),
                Arguments.of("resist-colour", "resist colour"),
                Arguments.of("immune-colour", "immune colour"),
                Arguments.of("effect", "effect type"),
                Arguments.of("resist", "resist"),
                Arguments.of("lash", "lash"));
    }

    /**
     * A directive with no values is refused with a {@link DataFileParseException}, as C refuses a
     * required field with no text ({@code PARSE_ERROR_MISSING_FIELD}). Before the guard on the
     * message builder this threw {@link StringIndexOutOfBoundsException} instead, from
     * {@code substring(0, -2)} on an empty builder, so the fatal log line and the quoted line number
     * never appeared. The lexer cannot produce this shape, because {@code STRING} and {@code INTEGER}
     * each need at least one character, so only a hand-built record reaches it.
     *
     * <p>Function aDirectiveWithNoValuesIsRefusedNotCrashed coded on 261117, commented in full on
     * 261117.
     *
     * @param directiveName the assembler's name for the directive
     * @param noun          the words its message uses for the value count
     */
    @ParameterizedTest(name = "{0} with no values is refused")
    @MethodSource("directiveNamesAndTheirNouns")
    @DisplayName("a directive with no values is refused with a DataFileParseException, not a crash")
    void aDirectiveWithNoValuesIsRefusedNotCrashed(String directiveName, String noun) {
        boolean opensTheRecord = directiveName.equals("name");
        int line = opensTheRecord ? 1 : 2;
        List<Directive> directives = opensTheRecord
                ? List.of(new Directive("name", List.of(), 1))
                : List.of(new Directive("name", List.of("X"), 1), new Directive(directiveName, List.of(), 2));

        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> assemble(directives));

        assertEquals("Blow Effect at line: " + line + " of file: " + FILE + " has an incorrect number of "
                + noun + " values (should be 1): ", thrown.getMessage());
    }

    /**
     * Two values, or three, are refused too, and the message lists them in file order, separated by
     * a comma and a space, with no trailing separator. C cannot reach this shape: a {@code str} or
     * {@code sym} field takes one value, and the rest of the line is text after it.
     *
     * <p>Function moreThanOneValueIsRefusedAndListed coded on 261117, commented in full on 261117.
     */
    @Test
    @DisplayName("two or three values are refused and listed in file order without a trailing separator")
    void moreThanOneValueIsRefusedAndListed() {
        Directive name = new Directive("name", List.of("X"), 1);

        DataFileParseException two = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(name, new Directive("power", List.of("a", "b"), 2))));
        DataFileParseException three = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(name, new Directive("power", List.of("1", "2", "3"), 2))));

        assertTrue(two.getMessage().endsWith("(should be 1): a, b"), two.getMessage());
        assertTrue(three.getMessage().endsWith("(should be 1): 1, 2, 3"), three.getMessage());
    }

    /**
     * The one-value path is untouched by the guard: {@code name:X} then {@code power:40} loads one
     * effect with that name and power.
     *
     * <p>Function oneValueEachStillLoads coded on 261117, commented in full on 261117.
     */
    @Test
    @DisplayName("one value on each directive still loads")
    void oneValueEachStillLoads() {
        List<BlowEffect> effects = assemble(List.of(
                new Directive("name", List.of("X"), 1),
                new Directive("power", List.of("40"), 2)));

        assertEquals(1, effects.size());
        assertEquals("X", effects.get(0).getName());
        assertEquals(40, effects.get(0).getPower());
    }

    /**
     * Every directive except {@code name} with a value the assembler would accept, so that the only
     * thing wrong with the record is the missing name.
     *
     * <p>Function directivesThatNeedANameFirst coded on 261117, commented in full on 261117.
     *
     * @return one row per directive: its assembler name, then a legal value
     */
    private static Stream<Arguments> directivesThatNeedANameFirst() {
        return Stream.of(
                Arguments.of("power", "1"),
                Arguments.of("eval", "1"),
                Arguments.of("desc", "d"),
                Arguments.of("base-colour", "w"),
                Arguments.of("resist-colour", "w"),
                Arguments.of("immune-colour", "w"),
                Arguments.of("effect", "element"),
                Arguments.of("resist", "FIRE"),
                Arguments.of("lash", "FIRE"));
    }

    /**
     * A directive that arrives before any {@code name:} is refused. C's {@code parse_eff_power},
     * {@code parse_eff_eval}, {@code parse_eff_desc}, {@code parse_eff_effect_type},
     * {@code parse_eff_resist} and {@code parse_eff_lash_type} {@code assert(eff)}, and the three
     * {@code parse_eff_lore_color*} handlers return {@code PARSE_ERROR_MISSING_RECORD_HEADER}; none of
     * the nine loads a record. The grammar always writes {@code name:} first, so only a hand-built
     * record reaches this.
     *
     * <p>Function aDirectiveBeforeTheNameIsRefused coded on 261117, commented in full on 261117.
     *
     * @param directiveName the assembler's name for the directive
     * @param value         a legal value for it
     */
    @ParameterizedTest(name = "{0} before the name is refused")
    @MethodSource("directivesThatNeedANameFirst")
    @DisplayName("a directive before the name is refused")
    void aDirectiveBeforeTheNameIsRefused(String directiveName, String value) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(new Directive(directiveName, List.of(value), 1))));

        assertEquals("Blow Effect at line: 1 of file: " + FILE + " has a directive: " + directiveName
                + " before the name directive has been found", thrown.getMessage());
    }

    /**
     * A {@code name} directive whose value is the empty string leaves the effect unnamed, so the next
     * directive is refused as arriving before the name. The lexer cannot write an empty name, and C
     * refuses {@code name:} with nothing after it as a missing field, so only a hand-built record
     * reaches this.
     *
     * <p>Function anEmptyNameLeavesTheRecordUnnamed coded on 261117, commented in full on 261117.
     */
    @Test
    @DisplayName("an empty name leaves the record unnamed, so the next directive is refused")
    void anEmptyNameLeavesTheRecordUnnamed() {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(new Directive("name", List.of(""), 1),
                        new Directive("power", List.of("5"), 2))));

        assertEquals("Blow Effect at line: 2 of file: " + FILE + " has a directive: power"
                + " before the name directive has been found", thrown.getMessage());
    }

    /**
     * A directive name the assembler does not read is refused, and the refusal comes ahead of the
     * name check, so it is the same with or without a name before it. The grammar never writes such
     * a name. C's parser would refuse a keyword that {@code init_parse_eff} did not register.
     *
     * <p>Function anUnknownDirectiveNameIsRefused coded on 261117, commented in full on 261117.
     */
    @Test
    @DisplayName("an unknown directive name is refused, with or without a name before it")
    void anUnknownDirectiveNameIsRefused() {
        DataFileParseException afterName = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(new Directive("name", List.of("X"), 1),
                        new Directive("bogus", List.of("1"), 2))));
        DataFileParseException alone = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(new Directive("bogus", List.of("1"), 1))));

        assertEquals("Blow Effect at line: 2 of file: " + FILE + " has an unknown directive: bogus",
                afterName.getMessage());
        assertEquals("Blow Effect at line: 1 of file: " + FILE + " has an unknown directive: bogus",
                alone.getMessage());
    }

    /**
     * Builds an effect from {@code name:X} and one colour directive, and reads back all three lore
     * colours.
     *
     * <p>Function loreColours coded on 261117, commented in full on 261117.
     *
     * @param directiveName one of {@code base-colour}, {@code resist-colour}, {@code immune-colour}
     * @param value         the raw colour text
     * @return the base, resist and immune colours, in that order
     */
    private static ColourEnum[] loreColours(String directiveName, String value) {
        BlowEffect effect = assemble(List.of(new Directive("name", List.of("X"), 1),
                new Directive(directiveName, List.of(value), 2))).get(0);
        return new ColourEnum[]{effect.getLoreAttr(), effect.getLoreAttrResist(), effect.getLoreAttrImmune()};
    }

    /**
     * Each colour directive sets its own field and no other, as {@code parse_eff_lore_color},
     * {@code parse_eff_lore_color_resist} and {@code parse_eff_lore_color_immune} set
     * {@code lore_attr}, {@code lore_attr_resist} and {@code lore_attr_immune}. The other two keep
     * what {@code mem_zalloc} gave them, colour 0, which is {@code COLOUR_DARK}.
     *
     * <p>Function eachColourDirectiveSetsOnlyItsOwnField coded on 261117, commented in full on 261117.
     */
    @Test
    @DisplayName("each colour directive sets its own field and leaves the other two Dark")
    void eachColourDirectiveSetsOnlyItsOwnField() {
        ColourEnum dark = ColourEnum.COLOUR_DARK;
        ColourEnum lightGreen = ColourEnum.COLOUR_LIGHT_GREEN;

        assertEquals(List.of(lightGreen, dark, dark), List.of(loreColours("base-colour", "Light Green")));
        assertEquals(List.of(dark, lightGreen, dark), List.of(loreColours("resist-colour", "Light Green")));
        assertEquals(List.of(dark, dark, lightGreen), List.of(loreColours("immune-colour", "Light Green")));
    }

    /**
     * An empty colour, or a single space, is {@code COLOUR_DARK}: {@code color_char_to_attr} returns
     * it for a {@code '\0'} or a {@code ' '}, and an empty string's first character is {@code '\0'}.
     * A second line overwrites the first, so Dark replaces Light Green. The lexer cannot write an
     * empty value, and C refuses a {@code sym} field with no text as a missing field.
     *
     * <p>Function anEmptyOrBlankColourIsDark coded on 261117, commented in full on 261117.
     */
    @Test
    @DisplayName("an empty or blank colour is Dark and overwrites an earlier colour")
    void anEmptyOrBlankColourIsDark() {
        for (String blank : List.of("", " ")) {
            BlowEffect effect = assemble(List.of(
                    new Directive("name", List.of("X"), 1),
                    new Directive("base-colour", List.of("Light Green"), 2),
                    new Directive("base-colour", List.of(blank), 3))).get(0);

            assertEquals(ColourEnum.COLOUR_DARK, effect.getLoreAttr(), "value [" + blank + "]");
        }
    }

    /**
     * A single character that matches no colour is white, including a high-bit one. C's
     * {@code color_char_to_attr} hands a negative {@code char} straight back, and
     * {@code parse_eff_lore_color} then returns {@code PARSE_ERROR_INVALID_COLOR}, so a lone byte with
     * the high bit set ends the load on a platform where {@code char} is signed. The port has no
     * negative character: the characters a decoder gives for such a byte, U+00E9 from Latin-1 or
     * U+FFFD from UTF-8, are single unknown characters, which are white. Accepted deviation, decided
     * 261117.
     *
     * <p>Function aHighBitCharacterIsWhiteWhereCRefusesOnASignedCharPlatform coded on 261117,
     * commented in full on 261117.
     */
    @Test
    @DisplayName("a single high-bit character is white where C refuses it on a signed-char platform")
    void aHighBitCharacterIsWhiteWhereCRefusesOnASignedCharPlatform() {
        for (String character : List.of("é", "�")) {
            BlowEffect effect = assemble(List.of(
                    new Directive("name", List.of("X"), 1),
                    new Directive("base-colour", List.of(character), 2))).get(0);

            assertEquals(ColourEnum.COLOUR_WHITE, effect.getLoreAttr(), "character " + character);
        }
    }

    /**
     * An empty list of records gives an empty list, and a record with no directives gives one
     * default effect with an empty name, the shape {@code new BlowEffect()} has. The grammar cannot
     * write a record without {@code name:}, so only a hand-built record reaches the second.
     *
     * <p>Function emptyInputShapesGiveWhatTheJavadocSays coded on 261117, commented in full on
     * 261117.
     */
    @Test
    @DisplayName("no records give no effects; a record with no directives gives a default effect")
    void emptyInputShapesGiveWhatTheJavadocSays() {
        assertEquals(List.of(), new BlowEffect2Assembler().assemble(List.of(), FILE));

        List<BlowEffect> effects = assemble(List.of());

        assertEquals(1, effects.size());
        assertEquals("", effects.get(0).getName());
        assertEquals(0, effects.get(0).getPower());
    }

    /**
     * A second {@code name} directive in one record overwrites the first, where C would start a
     * second record. The grammar cannot write two, because {@code name} sits outside the repeating
     * part of its {@code blowEffect} rule.
     *
     * <p>Function aSecondNameInOneRecordOverwritesTheFirst coded on 261117, commented in full on
     * 261117.
     */
    @Test
    @DisplayName("a second name in one record overwrites the first and keeps the rest")
    void aSecondNameInOneRecordOverwritesTheFirst() {
        List<BlowEffect> effects = assemble(List.of(
                new Directive("name", List.of("A"), 1),
                new Directive("power", List.of("7"), 2),
                new Directive("name", List.of("B"), 3)));

        assertEquals(1, effects.size());
        assertEquals("B", effects.get(0).getName());
        assertEquals(7, effects.get(0).getPower());
    }

    /**
     * Any record, with or without a {@code lash} directive, fails with an
     * {@link IllegalStateException} from {@code WorldRegistry} when the projections are not loaded,
     * not a {@link DataFileParseException}. The effect is built with {@code new BlowEffect()}, whose
     * default lash looks up the ACID projection, so the failure comes before the first directive is
     * read. C has no counterpart, because it stores only the projection index (accepted deviation,
     * 261118). The registry's projection list is set to null for the call and put back afterwards,
     * so the suite's shared state is left as it was found.
     *
     * <p>Function aRecordBeforeTheProjectionsAreLoadedIsAnIllegalState coded on 261117, commented
     * in full on 261118.
     *
     * @throws Exception if the registry field cannot be reached by reflection
     */
    @Test
    @DisplayName("a record with no projections loaded is an IllegalStateException")
    void aRecordBeforeTheProjectionsAreLoadedIsAnIllegalState() throws Exception {
        Field projections = RegistrySeeding.resolve("projections");
        projections.setAccessible(true);
        Object saved = projections.get(null);
        projections.set(null, null);
        try {
            assertThrows(IllegalStateException.class, () -> assemble(List.of(
                    new Directive("name", List.of("X"), 1))));
            assertThrows(IllegalStateException.class, () -> assemble(List.of(
                    new Directive("name", List.of("X"), 1),
                    new Directive("lash", List.of("FIRE"), 2))));
        } finally {
            projections.set(null, saved);
        }
    }
}
