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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.vault.VaultAssembler;
import uk.co.jackoftradesltd.backend.parser.vault.VaultParseRecord;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code VaultAssembler}, driven from hand-built {@link VaultParseRecord}s rather
 * than through the grammar.
 *
 * <p>Expected values come from the {@code parse_vault_*} handlers in {@code generate.c}:
 * {@code type:} is matched with {@code streq} against the {@code room_builders} names in
 * {@code list-rooms.h}; {@code rows:}, {@code columns:}, {@code min-depth:} and {@code max-depth:}
 * are {@code uint}s ({@code strtoul} in base 0, a leading {@code -} refused, {@code parser.c});
 * rows and columns fail only above the type's {@code max_height}/{@code max_width}; each
 * {@code D:} line must be exactly {@code wid} long and the number of lines is never checked;
 * {@code rating:} is a signed {@code int} ({@code strtol} in base 0); flags go through
 * {@code lookup_flag()} ({@code datafile.c}), which starts past {@code NONE} and finds
 * {@code MAX}; and the list is built by prepending.
 *
 * <p>Every error ends the load with a {@link DataFileParseException}, as a C handler error ends
 * the {@code vault.txt} load. Every record here declares a non-zero {@code max-depth:}, so the
 * rewrite of 0 to the world maximum, which needs {@code GameConstants} loaded, is left to
 * {@code VaultReaderTest}.
 *
 * @author Rowan Crowther
 */
class VaultAssemblerTest {

    private static final String FILE = "vault.txt";

    /** A record with every field given as text; the flat map is the lines joined. */
    private static VaultParseRecord record(String name, String type, String rating, String rows,
                                           String cols, String minLevel, String maxLevel,
                                           List<String> flags, List<String> map) {
        return new VaultParseRecord(name, type, String.join("", map), map, rating, rows, cols,
                minLevel, maxLevel, flags, 1);
    }

    /** A plain 3 by 3 lesser vault, rating 5, depths 1 to 40, no flags. */
    private static VaultParseRecord plain(String name) {
        return record(name, "Lesser vault", "5", "3", "3", "1", "40", List.of(),
                List.of("###", "#.#", "###"));
    }

    private static VaultParseRecord withType(String type, String rows, String cols, List<String> map) {
        return record("v", type, "5", rows, cols, "1", "40", List.of(), map);
    }

    private static VaultParseRecord withRows(String rows) {
        return record("v", "Lesser vault", "5", rows, "3", "1", "40", List.of(), List.of("###"));
    }

    private static VaultParseRecord withColumns(String type, String cols, int width) {
        return record("v", type, "5", "1", cols, "1", "40", List.of(), List.of("#".repeat(width)));
    }

    private static VaultParseRecord withMap(List<String> map) {
        return record("v", "Lesser vault", "5", "3", "3", "1", "40", List.of(), map);
    }

    private static VaultParseRecord withRating(String rating) {
        return record("v", "Lesser vault", rating, "1", "3", "1", "40", List.of(), List.of("###"));
    }

    private static VaultParseRecord withDepths(String minLevel, String maxLevel) {
        return record("v", "Lesser vault", "5", "1", "3", minLevel, maxLevel, List.of(),
                List.of("###"));
    }

    private static VaultParseRecord withFlags(List<String> flags) {
        return record("v", "Lesser vault", "5", "1", "3", "1", "40", flags, List.of("###"));
    }

    private static Vault one(VaultParseRecord record) {
        List<Vault> vaults = new VaultAssembler().assemble(List.of(record), new ArrayList<>(), FILE);
        assertEquals(1, vaults.size());
        return vaults.getFirst();
    }

    private static void assertThrowsFor(VaultParseRecord record) {
        assertThrows(DataFileParseException.class,
                () -> new VaultAssembler().assemble(List.of(record), new ArrayList<>(), FILE));
    }

    // --- ordinary path and list order ---

    @Test
    void emptyRecordListGivesNoVaults() {
        assertTrue(new VaultAssembler().assemble(List.of(), new ArrayList<>(), FILE).isEmpty());
    }

    @Test
    void aPlainRecordCarriesEveryField() {
        List<String> errors = new ArrayList<>();
        Vault vault = new VaultAssembler().assemble(List.of(plain("Plain")), errors, FILE).getFirst();

        assertEquals("Plain", vault.getName());
        assertEquals(RoomType.LESSER_VAULT, vault.getType());
        assertEquals(5, vault.getRating());
        assertEquals(3, vault.getHeight());
        assertEquals(3, vault.getWidth());
        assertEquals(1, vault.getMinLevel());
        assertEquals(40, vault.getMaxLevel());
        assertTrue(vault.getFlags().isEmpty());
        assertEquals(List.of("###", "#.#", "###"), vault.getMap());
        assertEquals("####.####", vault.getMapLines(), "C's v->text is the D: lines appended");
        assertTrue(errors.isEmpty(), "the errors list is no longer written");
    }

    /** C's {@code parse_vault_name()} points each new vault at the previous head. */
    @Test
    void theListComesBackInReverseFileOrder() {
        List<Vault> vaults = new VaultAssembler().assemble(
                List.of(plain("First"), plain("Second"), plain("Third")), new ArrayList<>(), FILE);

        assertEquals(List.of("Third", "Second", "First"), vaults.stream().map(Vault::getName).toList());
    }

    // --- type: every list-rooms.h name, case-exact ---

    /**
     * Every name in {@code list-rooms.h} is a legal {@code type:} in C, because
     * {@code parse_vault_rows()}/{@code parse_vault_columns()} search the whole
     * {@code room_builders} table. Rows and columns are 0 so that the nine builders whose caps are
     * 0 pass too, and no {@code D:} line is needed at width 0.
     */
    @ParameterizedTest
    @CsvSource({
            "staircase room, STAIRCASE",
            "simple room, SIMPLE",
            "moria room, MORIA",
            "large room, LARGE",
            "crossed room, CROSSED",
            "circular room, CIRCULAR",
            "overlap room, OVERLAP",
            "room template, TEMPLATE",
            "Interesting room, INTERESTING",
            "monster pit, PIT",
            "monster nest, NEST",
            "huge room, HUGE",
            "room of chambers, CHAMBERS",
            "Lesser vault, LESSER_VAULT",
            "Medium vault, MEDIUM_VAULT",
            "Greater vault, GREATER_VAULT",
            "Lesser vault (new), NEW_LESSER_VAULT",
            "Medium vault (new), NEW_MEDIUM_VAULT",
            "Greater vault (new), NEW_GREATER_VAULT"
    })
    void everyRoomBuilderNameResolves(String name, RoomType expected) {
        assertEquals(expected, one(withType(name, "0", "0", List.of())).getType());
    }

    /** {@code streq} is case-sensitive, so only the exact capitalisation matches. */
    @ParameterizedTest
    @CsvSource({"lesser vault", "LESSER VAULT", "Lesser Vault", "interesting room", "Simple room"})
    void aTypeInTheWrongCaseEndsTheLoad(String type) {
        assertThrowsFor(withType(type, "3", "3", List.of("###", "#.#", "###")));
    }

    /** No match is C's {@code PARSE_ERROR_NO_ROOM_FOUND}. */
    @ParameterizedTest
    @CsvSource({"Enormous vault", "'Lesser vault '", "Lesser", "LESSER_VAULT"})
    void anUnknownTypeEndsTheLoad(String type) {
        assertThrowsFor(withType(type, "3", "3", List.of("###", "#.#", "###")));
    }

    /** A builder with a 0 cap (simple room) refuses a single row: {@code 1 > 0}. */
    @Test
    void aZeroCapTypeRefusesAnyRows() {
        assertThrowsFor(withType("simple room", "1", "0", List.of()));
        assertThrowsFor(withType("simple room", "0", "1", List.of()));
    }

    /** room template's caps are 11 rows by 33 columns in {@code list-rooms.h}. */
    @Test
    void aRoomTemplateTypeUsesItsOwnCaps() {
        List<String> map = Collections.nCopies(11, "#".repeat(33));
        Vault vault = one(withType("room template", "11", "33", map));
        assertEquals(RoomType.TEMPLATE, vault.getType());

        assertThrowsFor(withType("room template", "12", "33", map));
        assertThrowsFor(withType("room template", "11", "34", map));
    }

    // --- rows: and columns: ---

    /** C fails only on {@code hgt > max_height}: 22 is the lesser vault cap, so it loads. */
    @Test
    void rowsAtTheCapLoadAndOneMoreEndsTheLoad() {
        assertEquals(22, one(withRows("22")).getHeight());
        assertThrowsFor(withRows("23"));
    }

    /** There is no lower bound in C, so zero rows load. */
    @Test
    void zeroRowsLoadAsInC() {
        assertEquals(0, one(withRows("0")).getHeight());
    }

    /** {@code parser.c} refuses a {@code uint} token starting with {@code -}, "-0" included. */
    @ParameterizedTest
    @CsvSource({"-1", "-0", "-22"})
    void negativeRowsEndTheLoad(String rows) {
        assertThrowsFor(withRows(rows));
    }

    /** {@code strtoul} in base 0: a leading 0 is octal and {@code 0x} is hexadecimal. */
    @ParameterizedTest
    @CsvSource({"026, 22", "0x16, 22", "+5, 5", "007, 7"})
    void rowsAreReadInBaseZero(String rows, int expected) {
        assertEquals(expected, one(withRows(rows)).getHeight());
    }

    /** 027 is octal 23, one over the lesser vault cap. */
    @Test
    void anOctalRowCountOverTheCapEndsTheLoad() {
        assertThrowsFor(withRows("027"));
    }

    /** The column cap is per type: 22 for a lesser vault, 33 for a medium vault. */
    @Test
    void columnsAreCappedByTheTypesOwnWidth() {
        assertEquals(22, one(withColumns("Lesser vault", "22", 22)).getWidth());
        assertThrowsFor(withColumns("Lesser vault", "23", 23));
        assertEquals(33, one(withColumns("Medium vault", "33", 33)).getWidth());
        assertThrowsFor(withColumns("Medium vault", "34", 34));
        assertEquals(66, one(withColumns("Greater vault", "66", 66)).getWidth());
        assertThrowsFor(withColumns("Greater vault", "67", 67));
    }

    /** Zero columns load as in C; with zero width only empty {@code D:} lines would fit. */
    @Test
    void zeroColumnsLoadAsInC() {
        assertEquals(0, one(withColumns("Lesser vault", "0", 0)).getWidth());
    }

    @ParameterizedTest
    @CsvSource({"-1", "-0"})
    void negativeColumnsEndTheLoad(String cols) {
        assertThrowsFor(withColumns("Lesser vault", cols, 1));
    }

    // --- D: lines ---

    /** {@code strlen(desc) != v->wid} is {@code PARSE_ERROR_VAULT_DESC_WRONG_LENGTH}. */
    @Test
    void aMapLineOfTheWrongWidthEndsTheLoad() {
        assertThrowsFor(withMap(List.of("###", "#..#", "###")));
        assertThrowsFor(withMap(List.of("###", "#.", "###")));
        assertThrowsFor(withMap(List.of("####", "####", "####")));
    }

    /** Trailing spaces count towards the width, as they do for {@code strlen}. */
    @Test
    void trailingSpacesCountTowardsTheWidth() {
        assertEquals(List.of("###", "#. ", "###"), one(withMap(List.of("###", "#. ", "###"))).getMap());
        assertThrowsFor(withMap(List.of("###", "#.  ", "###")));
    }

    /** C never compares the number of {@code D:} lines with {@code rows}, in either direction. */
    @Test
    void theNumberOfMapLinesIsNotCheckedAgainstRows() {
        Vault fewer = one(withMap(List.of("###", "###")));
        assertEquals(3, fewer.getHeight());
        assertEquals("######", fewer.getMapLines());

        Vault more = one(withMap(List.of("###", "#.#", "#.#", "###")));
        assertEquals(3, more.getHeight());
        assertEquals(4, more.getMap().size());

        assertEquals("", one(withMap(List.of())).getMapLines());
    }

    // --- rating: ---

    /** {@code rating} is {@code parser_getint}: signed, base 0. */
    @ParameterizedTest
    @CsvSource({"-3, -3", "0, 0", "010, 8", "0x10, 16", "+7, 7"})
    void theRatingIsASignedBaseZeroInt(String rating, int expected) {
        assertEquals(expected, one(withRating(rating)).getRating());
    }

    /**
     * Accepted deviation: C narrows {@code strtol}'s result to an {@code int}, which wraps;
     * {@code parseIntString} clamps to {@link Integer#MAX_VALUE}.
     */
    @Test
    void aRatingOutsideTheIntRangeIsClamped() {
        assertEquals(Integer.MAX_VALUE, one(withRating("99999999999")).getRating());
    }

    // --- min-depth: and max-depth: ---

    @Test
    void declaredDepthsAreKept() {
        Vault vault = one(withDepths("8", "16"));
        assertEquals(8, vault.getMinLevel());
        assertEquals(16, vault.getMaxLevel());
    }

    /** Both are {@code uint}s, read in base 0. */
    @Test
    void depthsAreReadInBaseZero() {
        Vault vault = one(withDepths("010", "0x20"));
        assertEquals(8, vault.getMinLevel());
        assertEquals(32, vault.getMaxLevel());
    }

    /** A zero minimum stays zero; only {@code max-depth:0} is rewritten. */
    @Test
    void aZeroMinimumDepthIsKept() {
        assertEquals(0, one(withDepths("0", "40")).getMinLevel());
    }

    @ParameterizedTest
    @CsvSource({"-5", "-0"})
    void aNegativeMinimumDepthEndsTheLoad(String minLevel) {
        assertThrowsFor(withDepths(minLevel, "40"));
    }

    @ParameterizedTest
    @CsvSource({"-5", "-0"})
    void aNegativeMaximumDepthEndsTheLoad(String maxLevel) {
        assertThrowsFor(withDepths("1", maxLevel));
    }

    // --- flags: ---

    @Test
    void aKnownFlagIsSet() {
        Vault vault = one(withFlags(List.of("FEW_ENTRANCES")));
        assertTrue(vault.getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertEquals(1, vault.getFlags().count());
    }

    /** Several {@code flags:} lines OR into one set, so a repeat is idempotent. */
    @Test
    void aRepeatedFlagIsSetOnce() {
        assertEquals(1, one(withFlags(List.of("FEW_ENTRANCES", "FEW_ENTRANCES"))).getFlags().count());
    }

    /**
     * {@code room_flags} holds {@code "MAX"} from {@code list-room-flags.h}, so
     * {@code lookup_flag()} finds it and {@code grab_flag()} sets it.
     */
    @Test
    void theMaxFlagIsAcceptedAsInC() {
        Vault vault = one(withFlags(List.of("MAX")));
        assertTrue(vault.getFlags().has(RoomFlags.MAX));
    }

    /** {@code NONE} is index 0, which {@code lookup_flag()} never reaches from {@code FLAG_START}. */
    @Test
    void theNoneFlagEndsTheLoad() {
        assertThrowsFor(withFlags(List.of("NONE")));
    }

    /** {@code lookup_flag()} matches with {@code streq}: case-exact, no unknown names. */
    @ParameterizedTest
    @CsvSource({"few_entrances", "Few_Entrances", "NOT_A_FLAG", "''"})
    void anUnknownFlagEndsTheLoad(String flag) {
        assertThrowsFor(withFlags(List.of(flag)));
    }

    /** A bad flag after a good one still ends the load. */
    @Test
    void aBadFlagAfterAGoodOneEndsTheLoad() {
        assertThrowsFor(withFlags(List.of("FEW_ENTRANCES", "NOT_A_FLAG")));
    }

    // --- whole-file failure ---

    /** One bad record ends the load: no partial list comes back. */
    @Test
    void aBadRecordAfterGoodOnesEndsTheWholeLoad() {
        List<VaultParseRecord> records = List.of(plain("Good"), withRows("23"), plain("Also good"));
        assertThrows(DataFileParseException.class,
                () -> new VaultAssembler().assemble(records, new ArrayList<>(), FILE));
    }
}
