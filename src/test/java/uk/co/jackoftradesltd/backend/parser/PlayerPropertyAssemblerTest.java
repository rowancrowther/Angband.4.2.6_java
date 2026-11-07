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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyAssembler;
import uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyParseRecord;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests of {@link PlayerPropertyAssembler} on hand-built {@link PlayerPropertyParseRecord}s, for the
 * number forms the {@code PlayerPropertyGrammar} lexer never lets through, so that
 * {@code PlayerPropertyReaderTest} cannot reach them. The expected values come from
 * {@code parse_player_prop_bindui} and {@code parse_player_prop_value} in {@code init.c} and from
 * the {@code int} reader in {@code parser.c}, function {@code parser_parse()}: an {@code aux} or
 * {@code value:} figure is read by {@code strtol} in base 0 and a {@code bindui} value by
 * {@code strtol} in base 10 with the whole text consumed.
 *
 * <p>Class PlayerPropertyAssemblerTest written on 261107.
 */
class PlayerPropertyAssemblerTest {

    private static PlayerPropertyParseRecord player(String type, String value, List<List<String>> bindui,
                                                    String valueLine) {
        return new PlayerPropertyParseRecord(type, "ROCK", bindui, "A", "", valueLine, 7);
    }

    private static List<PlayerProperty> assemble(PlayerPropertyParseRecord record) {
        return new PlayerPropertyAssembler().assemble(List.of(record), "hand.txt");
    }

    private static PlayerProperty.BindUI bindOf(String aux, String value) {
        List<PlayerProperty> items = assemble(player("player", "", List.of(List.of("a_b", "", aux, value)), ""));
        return items.get(0).getEntries().get(0);
    }

    /**
     * C: {@code isaux = (parser_getint(p, "aux") != 0)} where the int is {@code strtol} base 0, so a
     * hexadecimal figure and a sign are numbers, {@code 010} is eight, and {@code 08} is the zero
     * that {@code strtol} reads before the {@code 8}.
     */
    @ParameterizedTest
    @CsvSource({"1,true", "-1,true", "+1,true", "0x1,true", "0X1,true", "010,true", "2,true",
            "0,false", "00,false", "-0,false", "+0,false", "0x0,false", "08,false"})
    void anAuxIsReadAsStrtolBaseZeroAndIsAuxWhenNonZero(String aux, boolean expected) {
        assertEquals(expected, bindOf(aux, "1").aux());
    }

    @ParameterizedTest
    @ValueSource(strings = {"x", "", "-", "+", "abc"})
    void anAuxWithNoDigitIsRefusedAsNotANumber(String aux) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> bindOf(aux, "1"));
        assertTrue(thrown.getMessage().contains("starting at line: 7 of file: hand.txt"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("invalid bindui auxiliary value: " + aux), thrown::getMessage);
    }

    /**
     * C: {@code strtol(value, &end, 10)} then {@code *end == 0}. A leading zero is still decimal and
     * a leading plus is a sign.
     */
    @ParameterizedTest
    @CsvSource({"5,5", "+5,5", "-5,-5", "0,0", "007,7", "-007,-7", "+0,0", "2147483646,2147483646",
            "-2147483647,-2147483647"})
    void aBindingValueIsReadInBaseTen(String text, int expected) {
        PlayerProperty.BindUI bind = bindOf("0", text);

        assertEquals(expected, bind.value());
        assertFalse(bind.special());
    }

    /**
     * C: no digits ({@code ""}, {@code "-"}, {@code "+"}, {@code "--5"}), text left over after the
     * digits ({@code "5abc"}, {@code "0x10"} stops at the {@code x} in base 10, {@code "1.5"}) are all
     * {@code PARSE_ERROR_NOT_NUMBER}.
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "-", "+", "--5", "+-5", "5abc", "0x10", "1.5", "abc", "Special", "special "})
    void aBindingValueThatIsNotAWholeBaseTenNumberIsRefused(String text) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> bindOf("0", text));
        assertTrue(thrown.getMessage().contains("invalid bindui value: " + text), thrown::getMessage);
    }

    /**
     * C: {@code v >= INT_MAX} is {@code PARSE_ERROR_INVALID_VALUE}, and with a 64-bit {@code long} a
     * figure far beyond the {@code int} range is refused the same way, not wrapped.
     */
    @ParameterizedTest
    @ValueSource(strings = {"2147483647", "+2147483647", "2147483648", "99999999999", "99999999999999999999"})
    void aBindingValueAtOrBeyondIntMaxIsRefusedAsOutOfRange(String text) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> bindOf("0", text));
        assertTrue(thrown.getMessage().contains("out of range bindui value"), thrown::getMessage);
    }

    @Test
    void aSpecialBindingNeedsTheExactWord() {
        PlayerProperty.BindUI bind = bindOf("0", "special");

        assertTrue(bind.special());
        assertEquals(0, bind.value());
    }

    /**
     * C: {@code parse_player_prop_value} runs for every type, so a bad figure on a {@code player}
     * record ends the load even though {@code finish_parse_player_prop} then drops it.
     */
    @Test
    void aValueThatIsNotANumberIsRefusedOnAPlayerRecordToo() {
        assertThrows(DataFileParseException.class, () -> assemble(player("player", "", List.of(), "abc")));
    }

    /**
     * C: {@code parser_getint} reads the value in base 0, so {@code 0x10} is sixteen; the player
     * branch of {@code finish_parse_player_prop} never copies it, so the stored value is 0.
     */
    @Test
    void aBaseZeroValueOnAPlayerRecordLoadsAndIsDropped() {
        assertEquals(0, assemble(player("player", "", List.of(), "0x10")).get(0).getValue());
    }

    @Test
    void aValueOnAnObjectRecordIsDropped() {
        assertEquals(0, assemble(new PlayerPropertyParseRecord("object", "PROT_FEAR", List.of(), "A", "", "3", 7))
                .get(0).getValue());
    }

    /**
     * C: an {@code element} type never has a code ({@code code_index_in_array} is not called for it
     * and the index stays -1), so every code, the sentinels included, ends the load.
     */
    @ParameterizedTest
    @ValueSource(strings = {"NONE", "MAX", "ROCK", "PROT_FEAR"})
    void anyCodeOnAnElementRecordIsRefused(String code) {
        PlayerPropertyParseRecord record = new PlayerPropertyParseRecord("element", code, List.of(), "A", "", "1", 7);

        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> assemble(record));
        assertTrue(thrown.getMessage().contains("illegal code for an element value: " + code), thrown::getMessage);
    }

    /**
     * C: {@code player_info_flags} has {@code NONE} but no {@code MAX}; {@code list_obj_flag_names}
     * has both. The match is {@code streq}, so case counts and a flag from the other list fails.
     */
    @ParameterizedTest
    @CsvSource({"player,NONE,true", "player,MAX,false", "player,ROCK,true", "player,PROT_FEAR,false",
            "player,rock,false", "player,PF_ROCK,false",
            "object,NONE,true", "object,MAX,true", "object,PROT_FEAR,true", "object,ROCK,false",
            "object,prot_fear,false", "object,OF_PROT_FEAR,false"})
    void aCodeIsFoundOnlyInTheListForItsType(String type, String code, boolean loads) {
        PlayerPropertyParseRecord record = new PlayerPropertyParseRecord(type, code, List.of(), "A", "", "", 7);

        if (loads) {
            assertEquals(1, assemble(record).size());
        } else {
            DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> assemble(record));
            assertTrue(thrown.getMessage().contains("starting at line: 7 of file: hand.txt"), thrown::getMessage);
        }
    }

    /**
     * C: the binding is only prepended to a list, so a refusal on the second binding stops the load
     * even though the first was good, and no property is returned.
     */
    @Test
    void aBadSecondBindingEndsTheLoad() {
        PlayerPropertyParseRecord record = player("player", "",
                List.of(List.of("a_b", "", "0", "1"), List.of("c_d", "", "0", "x")), "");

        assertThrows(DataFileParseException.class, () -> assemble(record));
    }

    @Test
    void theTagAndTheNameJoinIntoOneEntryName() {
        List<PlayerProperty> items = assemble(player("player", "",
                List.of(List.of("stat_mod_ui_compact_0", "<STR>", "0", "2")), ""));

        assertEquals("stat_mod_ui_compact_0<STR>", items.get(0).getEntries().get(0).uiEntry());
        assertEquals(2, items.get(0).getEntries().get(0).value());
    }
}
