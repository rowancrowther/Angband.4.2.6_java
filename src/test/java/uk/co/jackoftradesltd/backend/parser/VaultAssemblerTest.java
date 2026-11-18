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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.backend.parser.vault.VaultAssembler;
import uk.co.jackoftradesltd.backend.parser.vault.VaultParseRecord;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
 * {@code MAX}, and every {@code flags:} line ORs into the one set; {@code max-depth:0} becomes
 * {@code z_info->max_depth}; and the list is built by prepending.
 *
 * <p>A record is now just its directives in file order, so the order-dependent behaviour C has
 * (rows and columns need a type, a {@code D:} line needs the columns, a later directive overwrites
 * an earlier one) is tested here by building the directives in the order under test.
 *
 * <p>Every error ends the load with a {@link DataFileParseException}, as a C handler error ends
 * the {@code vault.txt} load. The game constants are loaded once, because the rewrite of
 * {@code max-depth:0} reads the world's maximum depth. The same rewrite and list order are checked
 * against the shipped file in {@code VaultReaderTest}.
 *
 * @author Rowan Crowther
 */
class VaultAssemblerTest {

    private static final String FILE = "vault.txt";

    /**
     * The assembler turns a declared {@code max-depth:0} into the world maximum depth, so
     * {@code GameConstants} has to be loaded. Mirrors {@code VaultReaderTest.bootstrap()}.
     */
    @BeforeAll
    static void bootstrap() throws IOException {
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> uiEntrySpecs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            uiEntrySpecs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(uiEntrySpecs));
        Core core = new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
        GameConstants.init(core);
    }

    /** Resets the object-kind registries {@code GameConstants.init} filled, as the other suites do. */
    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
    }

    // --- builders ---

    /** One directive; its line number is set by {@link #rec(Directive...)}. */
    private static Directive d(String name, String... values) {
        return new Directive(name, new ArrayList<>(Arrays.asList(values)), 0);
    }

    /** A record of exactly these directives, in this order, numbered from line 1. */
    private static VaultParseRecord rec(Directive... directives) {
        List<Directive> numbered = new ArrayList<>();
        for (int i = 0; i < directives.length; i++) {
            numbered.add(new Directive(directives[i].name(), directives[i].values(), i + 1));
        }
        return new VaultParseRecord(numbered);
    }

    /**
     * A record with every directive given once, in the data file's own order. An empty
     * {@code flags} adds no {@code flags:} directive; otherwise they are one directive.
     */
    private static VaultParseRecord record(String name, String type, String rating, String rows,
                                           String cols, String minLevel, String maxLevel,
                                           List<String> flags, List<String> map) {
        List<Directive> ds = new ArrayList<>();
        ds.add(d("name", name));
        ds.add(d("type", type));
        ds.add(d("rating", rating));
        ds.add(d("rows", rows));
        ds.add(d("columns", cols));
        ds.add(d("min-depth", minLevel));
        ds.add(d("max-depth", maxLevel));
        if (!flags.isEmpty()) {
            ds.add(d("flag", flags.toArray(new String[0])));
        }
        for (String line : map) {
            ds.add(d("map", line));
        }
        return rec(ds.toArray(new Directive[0]));
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
        List<Vault> vaults = new VaultAssembler().assemble(List.of(record), FILE);
        assertEquals(1, vaults.size());
        return vaults.getFirst();
    }

    private static void assertThrowsFor(VaultParseRecord record) {
        assertThrows(DataFileParseException.class,
                () -> new VaultAssembler().assemble(List.of(record), FILE));
    }

    private static String messageFor(VaultParseRecord record) {
        return assertThrows(DataFileParseException.class,
                () -> new VaultAssembler().assemble(List.of(record), FILE)).getMessage();
    }

    // --- ordinary path and list order ---

    @Test
    void emptyRecordListGivesNoVaults() {
        assertTrue(new VaultAssembler().assemble(List.of(), FILE).isEmpty());
    }

    @Test
    void aPlainRecordCarriesEveryField() {
        Vault vault = new VaultAssembler().assemble(List.of(plain("Plain")), FILE).getFirst();

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
    }

    /** C's {@code parse_vault_name()} points each new vault at the previous head. */
    @Test
    void theListComesBackInReverseFileOrder() {
        List<Vault> vaults = new VaultAssembler().assemble(
                List.of(plain("First"), plain("Second"), plain("Third")), FILE);

        assertEquals(List.of("Third", "Second", "First"), vaults.stream().map(Vault::getName).toList());
    }

    /** Two records, A then B, come back as B then A. */
    @Test
    void twoRecordsComeBackSwapped() {
        List<Vault> vaults = new VaultAssembler().assemble(List.of(plain("A"), plain("B")), FILE);

        assertEquals("B", vaults.get(0).getName());
        assertEquals("A", vaults.get(1).getName());
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

    /**
     * An unknown type is only found out at {@code rows:}, so the error is at the {@code rows:}
     * line (3 here) and says the type is not a known one, rather than that it is missing.
     */
    @Test
    void anUnknownTypeIsReportedAtTheRowsLineAsNotKnown() {
        String message = messageFor(rec(d("name", "v"), d("type", "Bogus"), d("rows", "1")));

        assertTrue(message.contains("line: 3"), message);
        assertTrue(message.contains("known type"), message);
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

    /**
     * The columns-over-the-cap error names the line of the {@code columns:} directive (5 in a
     * {@link #record} record), not the directive object itself.
     */
    @Test
    void theColumnsCapErrorQuotesTheDirectivesLineNumber() {
        String message = messageFor(withColumns("Lesser vault", "99", 3));

        assertTrue(message.contains("line: 5"), message);
        assertFalse(message.contains("Directive["), message);
    }

    // --- values that wrap to a byte ---

    /**
     * {@code hgt} is a {@code uint8_t}, so {@code rows:278} is stored as 22 and passes the lesser
     * vault cap of 22. {@code rows:256} wraps to zero, which loads.
     */
    @Test
    void rowsWrapToAByteBeforeTheCapIsTested() {
        assertEquals(22, one(withRows("278")).getHeight());
        assertEquals(0, one(withRows("256")).getHeight());
    }

    /** {@code rows:279} wraps to 23, one over the lesser vault cap, so it ends the load. */
    @Test
    void rowsThatWrapToOverTheCapEndTheLoad() {
        assertThrowsFor(withRows("279"));
    }

    /**
     * {@code wid} is a {@code uint8_t} too: {@code columns:278} is 22, and the {@code D:} line is
     * then compared with 22, not 278.
     */
    @Test
    void columnsWrapToAByteBeforeTheCapAndTheLineWidthAreTested() {
        assertEquals(22, one(withColumns("Lesser vault", "278", 22)).getWidth());
        assertThrowsFor(withColumns("Lesser vault", "279", 23));
    }

    /** {@code min_lev} is a {@code uint8_t}: 260 is 4. */
    @Test
    void theMinimumDepthWrapsToAByte() {
        assertEquals(4, one(withDepths("260", "40")).getMinLevel());
        assertEquals(0, one(withDepths("256", "40")).getMinLevel());
    }

    /** {@code max_lev} is a {@code uint8_t}: 300 is 44. */
    @Test
    void theMaximumDepthWrapsToAByte() {
        assertEquals(44, one(withDepths("1", "300")).getMaxLevel());
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

    }

    /**
     * With no {@code D:} line at all C's {@code v->text} stays NULL, and the Java vault keeps its
     * {@code null} flat layout while the per-row list is empty.
     */
    @Test
    void aRecordWithNoMapLinesHasNoFlatLayout() {
        Vault vault = one(withMap(List.of()));

        assertNull(vault.getMapLines());
        assertTrue(vault.getMap().isEmpty());
    }

    // --- rating: ---

    /**
     * {@code rating} is read with {@code parser_getint}, so it is signed and in base 0, and then
     * stored in the {@code uint8_t rat}, so a negative rating wraps: -3 is 253.
     */
    @ParameterizedTest
    @CsvSource({"-3, 253", "0, 0", "010, 8", "0x10, 16", "+7, 7"})
    void theRatingIsASignedBaseZeroIntStoredAsAByte(String rating, int expected) {
        assertEquals(expected, one(withRating(rating)).getRating());
    }

    /** The rating wraps modulo 256, the width of {@code uint8_t rat}. */
    @ParameterizedTest
    @CsvSource({"255, 255", "256, 0", "261, 5", "-256, 0", "-1, 255"})
    void theRatingWrapsToAByte(String rating, int expected) {
        assertEquals(expected, one(withRating(rating)).getRating());
    }

    /**
     * Accepted deviation: C narrows {@code strtol}'s result to an {@code int}, which wraps;
     * {@code parseIntString} clamps to {@link Integer#MAX_VALUE}, and the byte wrap then makes
     * that 255.
     */
    @Test
    void aRatingOutsideTheIntRangeIsClampedThenWrapped() {
        assertEquals(255, one(withRating("99999999999")).getRating());
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

    /**
     * C's {@code parse_vault_max_depth} stores {@code z_info->max_depth} for a declared 0, which
     * is {@code world:max-depth} in {@code constants.txt}, so the vault never keeps a 0 maximum.
     */
    @Test
    void aMaximumDepthOfZeroBecomesTheWorldMaximum() {
        int worldMax = GameConstants.getWorldMaxDepth();

        assertNotEquals(0, worldMax, "the constants must be loaded for this test to mean anything");
        assertEquals(worldMax, one(withDepths("1", "0")).getMaxLevel());
        assertEquals(worldMax, one(withDepths("1", "0x0")).getMaxLevel(), "base 0 spelling of zero");
        assertEquals(worldMax, one(withDepths("1", "00")).getMaxLevel(), "octal spelling of zero");
    }

    /**
     * C tests {@code max-depth:} for zero on the raw value, before the narrowing, so 256 is not
     * rewritten to the world's maximum depth: it is stored as 0.
     */
    @Test
    void aMaximumDepthOf256IsStoredAsZeroNotRewritten() {
        assertEquals(0, one(withDepths("1", "256")).getMaxLevel());
    }

    /** Only the maximum is rewritten, and a real cap survives untouched. */
    @Test
    void aMinimumDepthOfZeroIsNotRewrittenWhenTheMaximumIs() {
        Vault vault = one(withDepths("0", "0"));

        assertEquals(0, vault.getMinLevel());
        assertEquals(GameConstants.getWorldMaxDepth(), vault.getMaxLevel());
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

    /** C zeroes a new vault, so one with no {@code flags:} line has an empty set, never null. */
    @Test
    void aRecordWithNoFlagsLineHasAnEmptySet() {
        Vault vault = one(withFlags(List.of()));

        assertNotNull(vault.getFlags());
        assertTrue(vault.getFlags().isEmpty());
    }

    @Test
    void aKnownFlagIsSet() {
        Vault vault = one(withFlags(List.of("FEW_ENTRANCES")));
        assertTrue(vault.getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertEquals(1, vault.getFlags().count());
    }

    /** One {@code flags:} line naming a flag twice sets it once. */
    @Test
    void aRepeatedFlagIsSetOnce() {
        assertEquals(1, one(withFlags(List.of("FEW_ENTRANCES", "FEW_ENTRANCES"))).getFlags().count());
    }

    /**
     * {@code grab_flag()} ORs into {@code v->flags}, so a second {@code flags:} line adds to the
     * first rather than replacing it.
     */
    @Test
    void severalFlagsLinesAreOrredTogether() {
        Vault vault = one(rec(d("name", "v"), d("type", "Lesser vault"),
                d("flag", "FEW_ENTRANCES"), d("flag", "MAX")));

        assertTrue(vault.getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertTrue(vault.getFlags().has(RoomFlags.MAX));
        assertEquals(2, vault.getFlags().count());
    }

    /** The same flag on two lines is still one flag. */
    @Test
    void theSameFlagOnTwoLinesIsSetOnce() {
        Vault vault = one(rec(d("name", "v"), d("flag", "FEW_ENTRANCES"), d("flag", "FEW_ENTRANCES")));

        assertEquals(1, vault.getFlags().count());
    }

    /** Flags are per vault: one record's flags never reach the next. */
    @Test
    void aRecordsFlagsDoNotLeakIntoTheNextRecord() {
        List<Vault> vaults = new VaultAssembler().assemble(List.of(
                rec(d("name", "Flagged"), d("flag", "FEW_ENTRANCES")),
                rec(d("name", "Plain"))), FILE);

        assertTrue(vaults.get(0).getFlags().isEmpty(), "Plain is first in C's reversed list");
        assertTrue(vaults.get(1).getFlags().has(RoomFlags.FEW_ENTRANCES));
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

    // --- directive order: a record is its directives in file order ---

    /** {@code parse_vault_rows()} searches the builders by {@code v->typ}; none yet is no match. */
    @Test
    void rowsBeforeTheTypeEndTheLoad() {
        assertThrowsFor(rec(d("name", "v"), d("rows", "3"), d("type", "Lesser vault")));
    }

    @Test
    void columnsBeforeTheTypeEndTheLoad() {
        assertThrowsFor(rec(d("name", "v"), d("columns", "3"), d("type", "Lesser vault")));
    }

    /** {@code wid} is still 0 when a {@code D:} line arrives before {@code columns:}. */
    @Test
    void aMapLineBeforeTheColumnsEndsTheLoad() {
        assertThrowsFor(rec(d("name", "v"), d("type", "Lesser vault"), d("map", "###"),
                d("columns", "3")));
    }

    /** An empty {@code D:} line does fit a width of 0, so it can come first. */
    @Test
    void anEmptyMapLineBeforeTheColumnsLoads() {
        Vault vault = one(rec(d("name", "v"), d("type", "Lesser vault"), d("map", ""),
                d("columns", "3")));

        assertEquals(List.of(""), vault.getMap());
        assertEquals(3, vault.getWidth());
    }

    /** The directives may come in any order once the type and columns are ahead of what needs them. */
    @Test
    void directivesInAnyOrderLoadWhenTheirDependenciesCome() {
        Vault vault = one(rec(d("name", "v"), d("max-depth", "40"), d("flag", "FEW_ENTRANCES"),
                d("rating", "7"), d("type", "Medium vault"), d("min-depth", "2"),
                d("columns", "2"), d("map", "##"), d("rows", "1")));

        assertEquals(RoomType.MEDIUM_VAULT, vault.getType());
        assertEquals(40, vault.getMaxLevel());
        assertEquals(2, vault.getMinLevel());
        assertEquals(7, vault.getRating());
        assertEquals(List.of("##"), vault.getMap());
        assertTrue(vault.getFlags().has(RoomFlags.FEW_ENTRANCES));
    }

    /** Each handler overwrites its field, so a repeated directive's last line wins. */
    @Test
    void aRepeatedDirectiveLastLineWins() {
        Vault vault = one(rec(d("name", "v"), d("type", "Lesser vault"), d("rows", "2"),
                d("rows", "3"), d("rating", "1"), d("rating", "9"), d("min-depth", "1"),
                d("min-depth", "4"), d("max-depth", "20"), d("max-depth", "30"),
                d("type", "Medium vault")));

        assertEquals(3, vault.getHeight());
        assertEquals(9, vault.getRating());
        assertEquals(4, vault.getMinLevel());
        assertEquals(30, vault.getMaxLevel());
        assertEquals(RoomType.MEDIUM_VAULT, vault.getType());
    }

    /** The cap is tested against the type at the moment of {@code rows:}; a later type is not rechecked. */
    @Test
    void aLaterTypeChangeIsNotRecheckedAgainstEarlierRows() {
        Vault vault = one(rec(d("name", "v"), d("type", "Lesser vault"), d("rows", "22"),
                d("type", "simple room")));

        assertEquals(22, vault.getHeight());
        assertEquals(RoomType.SIMPLE, vault.getType());
    }

    /**
     * C stores the {@code type:} text unchecked and compares it only in {@code parse_vault_rows()}
     * and {@code parse_vault_columns()}, so a record that names a type no builder has, and never
     * gives rows or columns, loads. The type is held as {@code null}.
     */
    @Test
    void anUnknownTypeWithNoRowsOrColumnsLoads() {
        Vault vault = one(rec(d("name", "v"), d("type", "Bogus"), d("rating", "4")));

        assertNull(vault.getType());
        assertEquals(4, vault.getRating());
    }

    /** {@code v->typ} is overwritten by the last {@code type:}, so a good one after a bad one rescues {@code rows:}. */
    @Test
    void aGoodTypeAfterAnUnknownOneLetsRowsLoad() {
        Vault vault = one(rec(d("name", "v"), d("type", "Bogus"), d("type", "Lesser vault"),
                d("rows", "22")));

        assertEquals(RoomType.LESSER_VAULT, vault.getType());
        assertEquals(22, vault.getHeight());
    }

    /** The reverse: an unknown {@code type:} after a good one leaves nothing for {@code rows:} to match. */
    @Test
    void anUnknownTypeAfterAGoodOneEndsTheLoadAtRows() {
        assertThrowsFor(rec(d("name", "v"), d("type", "Lesser vault"), d("type", "Bogus"),
                d("rows", "3")));
    }

    /** A record with only a name loads: C's zeroed struct simply keeps its defaults. */
    @Test
    void aRecordOfOnlyANameLoadsWithDefaults() {
        Vault vault = one(rec(d("name", "Bare")));

        assertEquals("Bare", vault.getName());
        assertNull(vault.getType());
        assertEquals(0, vault.getHeight());
        assertEquals(0, vault.getWidth());
        assertEquals(0, vault.getRating());
        assertEquals(0, vault.getMinLevel());
        assertEquals(0, vault.getMaxLevel());
        assertTrue(vault.getFlags().isEmpty());
        assertTrue(vault.getMap().isEmpty());
    }

    /** C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}: a directive with no vault started. */
    @ParameterizedTest
    @CsvSource({"type", "rating", "rows", "columns", "min-depth", "max-depth", "flag", "map"})
    void aDirectiveBeforeTheNameEndsTheLoad(String directive) {
        String message = messageFor(rec(d(directive, "1")));

        assertTrue(message.contains("before a name directive"), message);
    }

    @Test
    void anUnknownDirectiveEndsTheLoad() {
        assertThrowsFor(rec(d("name", "v"), d("colour", "red")));
    }

    // --- value counts and message wording ---

    /** Each directive takes exactly one value; none or two ends the load. */
    @ParameterizedTest
    @CsvSource({"name", "type", "rating", "rows", "columns", "min-depth", "max-depth", "map"})
    void aDirectiveWithTheWrongNumberOfValuesEndsTheLoad(String directive) {
        VaultParseRecord none = directive.equals("name")
                ? rec(d("name"))
                : rec(d("name", "v"), d("type", "Lesser vault"), d("columns", "0"), d(directive));
        VaultParseRecord two = directive.equals("name")
                ? rec(d("name", "a", "b"))
                : rec(d("name", "v"), d("type", "Lesser vault"), d("columns", "0"), d(directive, "1", "2"));

        assertThrowsFor(none);
        assertThrowsFor(two);
    }

    /** The count error names its own directive, not a copy-pasted neighbour. */
    @ParameterizedTest
    @CsvSource({"min-depth, min-depth", "max-depth, max-depth", "map, map", "rows, row",
            "columns, column", "rating, rating", "type, type"})
    void theValueCountErrorNamesItsOwnDirective(String directive, String expected) {
        String message = messageFor(rec(d("name", "v"), d(directive, "1", "2")));

        assertTrue(message.contains(expected + " values"), message);
        if (!directive.equals("min-depth")) {
            assertFalse(message.contains("min-depth"), message);
        }
    }

    // --- whole-file failure ---

    /** One bad record ends the load: no partial list comes back. */
    @Test
    void aBadRecordAfterGoodOnesEndsTheWholeLoad() {
        List<VaultParseRecord> records = List.of(plain("Good"), withRows("23"), plain("Also good"));
        assertThrows(DataFileParseException.class,
                () -> new VaultAssembler().assemble(records, FILE));
    }
}
