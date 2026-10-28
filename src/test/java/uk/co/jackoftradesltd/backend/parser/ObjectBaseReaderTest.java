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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.grammars.objectbase.ObjectBaseGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.objectbase.ObjectBaseLexer;
import uk.co.jackoftradesltd.backend.parser.objectbase.ObjectBaseParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link ObjectBaseReader} (grammar-suite reader track), driving
 * {@code ObjectBaseLexer}/{@code ObjectBaseGrammar} through the shared {@link GrammarDriver} into
 * {@link ObjectBase} objects.
 *
 * <p>These pin the behaviours we fought through while porting the assembler and domain class: the
 * flag {@code Flag}-set initialisation (an NPE regression), {@code dragon armor} tval resolution,
 * the file-wide break/max-stack default folding, colour resolution (not silently DARK), and the
 * stop-the-load contract of the assembler's fatal paths. Because {@link ObjectBase} exposes no accessor for its two
 * flag sets, the repeated-{@code flags:} <em>merge</em> is asserted one level down - directly on the
 * {@link ObjectBaseParseRecord} the grammar produces.
 *
 * <p>The second half of the class pins the reader's own contract against {@code obj-init.c}
 * ({@code run_parse_object_base} and {@code finish_parse_object_base}): all 34 records of the shipped
 * file, field by field, against values worked out from the upstream text; list order; the duplicate
 * tval rule; the two error channels; and each place the grammar or assembler differs from the C
 * handlers. A test whose name says "where C ..." pins a Java behaviour that differs from C, so it
 * will need to change with the fix recorded in {@code docs/grammar_issues.md} or
 * {@code docs/Assembler_Issues.md}.
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

    /**
     * The bases actually declared by the file, i.e. everything the assembler read from records.
     *
     * <p>{@link uk.co.jackoftradesltd.backend.parser.objectbase.ObjectBaseAssembler} appends one
     * synthetic {@link TValue#TV_NONE} base to every list it builds, which object_base.txt never
     * declares — see {@link #syntheticNoneBaseIsAppendedToEveryLoad()}. These tests are about the
     * records, so they filter it out rather than carry a +1 in every count.
     */
    private static List<ObjectBase> declared(ParseResult<ObjectBase> result) {
        return result.items().stream()
                .filter(b -> b.gettVal() != TValue.TV_NONE)
                .toList();
    }

    // ---- Happy path: the real shipped file -------------------------------

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAll34Bases() throws IOException {
        ParseResult<ObjectBase> result = new ObjectBaseReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(34, declared(result).size(), () -> "loaded: " + declared(result).size());
    }

    /**
     * C allocates {@code kb_info} across every tval, so a zeroed base exists for {@code TV_NONE}
     * even though no record declares one. object.txt's four "non-kind" records (&lt;pile&gt;,
     * &lt;unknown item&gt;, &lt;unknown treasure&gt;, &lt;curse object&gt;) are {@code type:none} and
     * hang off it; without it they would resolve to a null base instead of C's zeroed one.
     */
    @Test
    void syntheticNoneBaseIsAppendedToEveryLoad() throws IOException {
        ParseResult<ObjectBase> result = new ObjectBaseReader().parseWithResults(REAL_FILE);

        List<ObjectBase> none = result.items().stream()
                .filter(b -> b.gettVal() == TValue.TV_NONE)
                .toList();

        assertEquals(1, none.size(), "exactly one synthetic TV_NONE base");
        ObjectBase base = none.get(0);
        assertNull(base.getName(), "C's zeroed slot has no name");
        assertEquals(ColourEnum.COLOUR_DARK, base.getAttr(), "C's zeroed attr");
        assertTrue(base.getElementMap().isEmpty(), "no HATES_ elements");
        assertEquals(0, base.getBreakPerc());
        assertEquals(0, base.getMaxStack());
    }

    // ---- Minimal record + default folding --------------------------------

    @Test
    void minimalRecordLoadsAndInheritsTheFileDefaults() throws IOException {
        // No break:/max-stack: on the record, so both come from the file-wide defaults folded in
        // by the grammar's file rule.
        ParseResult<ObjectBase> result =
                load("minimal.txt", withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, declared(result).size());
        ObjectBase base = declared(result).get(0);
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
        assertEquals(TValue.TV_DRAG_ARMOR, result.items().get(0).gettVal());
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
        ColourEnum attr = result.items().get(0).getAttr();
        assertEquals(ColourEnum.fromCode("slate"), attr);
        assertNotEquals(ColourEnum.COLOUR_DARK, attr, "colour should resolve, not fall back to DARK");
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

    // ---- One test per assembler fatal branch -----------------------------

    /**
     * The load ends with a {@link DataFileParseException} naming the record's line (the
     * {@code name:} line, 4 in every fixture here, after the header) and the file, and quoting the
     * offending text. C's handler returns a parser error and {@code init_angband} quits.
     */
    private void assertFatal(String name, String content, String... expectedInMessage) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> load(name, content));
        assertTrue(thrown.getMessage().contains("line: 4"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(name), thrown::getMessage);
        for (String text : expectedInMessage) {
            assertTrue(thrown.getMessage().contains(text), thrown::getMessage);
        }
    }

    @Test
    void unknownTValEndsTheLoad() {
        // The message echoes the tval exactly as the file spelled it.
        assertFatal("bad-tval.txt", withHeader(1, "name:notatval:Thing~\ngraphics:white\n"),
                "illegal tValue value", "notatval");
    }

    @Test
    void unknownKindFlagEndsTheLoad() {
        assertFatal("bad-flag.txt",
                withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\nflags:NOTAFLAG\n"),
                "illegal flag value", "NOTAFLAG");
    }

    @Test
    void unknownHatesFlagEndsTheLoad() {
        assertFatal("bad-hates.txt",
                withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\nflags:HATES_NOTANELEMENT\n"),
                "illegal hates element flag value", "HATES_NOTANELEMENT");
    }

    @Test
    void breakChanceTooBigForAnIntIsClampedWhereCWraps() throws IOException {
        // Accepted deviation (docs/Accepted_Deviations.md): C keeps the low 32 bits of the strtol
        // result, ParseUtils.parseIntString clamps, so the record loads with Integer.MAX_VALUE.
        ParseResult<ObjectBase> result = load("big-break.txt",
                withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\nbreak:99999999999999999999\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(Integer.MAX_VALUE, declared(result).get(0).getBreakPerc());
    }

    // ---- Soft record-count mismatch --------------------------------------

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        // Header over-declares (5 vs the single record). Soft: the record still loads.
        ParseResult<ObjectBase> result = load("bad-count.txt",
                withHeader(5, "name:sword:Bladed weapon~\ngraphics:white\n"));

        assertEquals(1, declared(result).size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    // ---- FLAG token: digit-bearing tail ----------------------------------

    @Test
    void digitBearingFlagLexesAsASingleFlagAndReachesTheAssembler() {
        // FLAG_BODY allows a digit tail; SHOW_2 is not a real KF_ or OF_ flag, so it must surface as
        // the assembler's "illegal flag value: SHOW_2" - proof it lexed as one FLAG rather than a
        // syntax error splitting on the digit.
        assertFatal("digit-flag.txt",
                withHeader(1, "name:sword:Bladed weapon~\ngraphics:white\nflags:SHOW_2\n"),
                "illegal flag value", "SHOW_2");
    }

    // ======================================================================
    // Reader contract against obj-init.c (261017)
    // ======================================================================

    /**
     * Every record of the upstream {@code object_base.txt}, worked out by reading the file, not by
     * running the reader: {@code tval | name | colour | break | max-stack | kind flags | hated
     * elements}. The two defaults are {@code break-chance:10} and {@code max-stack:40}; no record
     * sets {@code max-stack:} (the commented-out lines in the file are not directives), so every
     * stack size is 40. {@code gold} has no name part, which the assembler turns into {@code null}.
     */
    private static final String[][] UPSTREAM = {
            {"TV_CHEST", "Chest~", "slate", "10", "", "ACID,FIRE"},
            {"TV_SHOT", "Shot~", "light umber", "0", "SHOW_DICE", ""},
            {"TV_ARROW", "Arrow~", "light umber", "35", "SHOW_DICE", "ACID,FIRE"},
            {"TV_BOLT", "Bolt~", "light umber", "20", "SHOW_DICE", "ACID"},
            {"TV_BOW", "Bow~", "umber", "10", "SHOW_MULT", "ACID,FIRE"},
            {"TV_DIGGING", "Digger~", "slate", "10", "SHOW_DICE", ""},
            {"TV_HAFTED", "Hafted weapon~", "white", "10", "SHOW_DICE", "ACID,FIRE"},
            {"TV_POLEARM", "Polearm~", "white", "10", "SHOW_DICE", "ACID,FIRE"},
            {"TV_SWORD", "Bladed weapon~", "white", "10", "SHOW_DICE", "ACID"},
            {"TV_BOOTS", "Boot~", "light umber", "10", "", "ACID,FIRE"},
            {"TV_GLOVES", "Glove~", "light umber", "10", "", "ACID,FIRE"},
            {"TV_HELM", "Helm~", "light umber", "10", "", "ACID"},
            {"TV_CROWN", "Crown~", "light umber", "10", "", "ACID"},
            {"TV_SHIELD", "Shield~", "light umber", "10", "", "ACID"},
            {"TV_CLOAK", "Cloak~", "light umber", "10", "", "ACID,FIRE"},
            {"TV_SOFT_ARMOR", "Soft Armor~", "slate", "10", "", "ACID,FIRE"},
            {"TV_HARD_ARMOR", "Hard Armor~", "slate", "10", "", "ACID"},
            {"TV_DRAG_ARMOR", "Dragon Armor~", "slate", "10", "", ""},
            {"TV_LIGHT", "Light~", "yellow", "50", "", "FIRE"},
            {"TV_AMULET", "Amulet~", "orange", "10", "", ""},
            {"TV_RING", "Ring~", "red", "10", "", "ELEC"},
            {"TV_STAFF", "Staff~", "light umber", "10", "EASY_KNOW", "ACID,FIRE"},
            {"TV_WAND", "Wand~", "green", "10", "EASY_KNOW", "ELEC"},
            {"TV_ROD", "Rod~", "light purple", "10", "EASY_KNOW", "ELEC"},
            {"TV_SCROLL", "Scroll~", "white", "10", "EASY_KNOW", "ACID,FIRE"},
            {"TV_POTION", "Potion~", "light blue", "100", "EASY_KNOW", "COLD,SOUND,SHARD,ICE,FORCE"},
            {"TV_FLASK", "Flask~", "yellow", "100", "EASY_KNOW", "COLD,SOUND,SHARD,ICE,FORCE"},
            {"TV_FOOD", "Food", "light umber", "100", "EASY_KNOW", ""},
            {"TV_MUSHROOM", "Mushroom~", "light umber", "100", "EASY_KNOW", ""},
            {"TV_MAGIC_BOOK", "Magic Book~", "light red", "10", "EASY_KNOW", "FIRE"},
            {"TV_PRAYER_BOOK", "Prayer Book~", "light green", "10", "EASY_KNOW", "FIRE"},
            {"TV_NATURE_BOOK", "Nature Book~", "yellow", "10", "EASY_KNOW", "FIRE"},
            {"TV_SHADOW_BOOK", "Shadow Book~", "light purple", "10", "EASY_KNOW", "FIRE"},
            {"TV_GOLD", "", "light yellow", "10", "", ""},
    };

    private static Set<String> commaSet(String list) {
        return list.isEmpty() ? Set.of() : Set.of(list.split(","));
    }

    private static Set<String> kindFlagNames(ObjectBase base) {
        Set<String> names = new java.util.HashSet<>();
        for (ObjectKindFlag flag : base.getKindFlags()) {
            names.add(flag.name().substring("KF_".length()));
        }
        return names;
    }

    private static Set<String> hatedElementNames(ObjectBase base) {
        Set<String> names = new java.util.HashSet<>();
        base.getElementMap().forEach((element, info) -> {
            if (info.has(ElementInfoEnum.EL_INFO_HATES)) {
                names.add(element.name().substring("ELEM_".length()));
            }
        });
        return names;
    }

    /**
     * C's {@code kb_info} entry for a tval is the record's defaults, overlaid by whatever its
     * directives set. Each of the 34 shipped records is checked against the table above.
     */
    @Test
    void everyShippedRecordAgreesWithTheUpstreamFile() throws IOException {
        assertEquals(34, UPSTREAM.length, "the table must cover the whole file");
        List<ObjectBase> bases = declared(new ObjectBaseReader().parseWithResults(REAL_FILE));
        assertEquals(UPSTREAM.length, bases.size());

        for (int i = 0; i < UPSTREAM.length; i++) {
            String[] row = UPSTREAM[i];
            ObjectBase base = bases.get(i);
            String where = "record " + i + " (" + row[0] + ")";

            assertEquals(TValue.valueOf(row[0]), base.gettVal(), where + " tval");
            assertEquals(row[1].isEmpty() ? null : row[1], base.getName(), where + " name");
            assertEquals(ColourEnum.fromCode(row[2]), base.getAttr(), where + " colour");
            assertEquals(Integer.parseInt(row[3]), base.getBreakPerc(), where + " break");
            assertEquals(40, base.getMaxStack(), where + " max-stack");
            assertEquals(commaSet(row[4]), kindFlagNames(base), where + " kind flags");
            assertEquals(commaSet(row[5]), hatedElementNames(base), where + " hated elements");
            assertTrue(base.getFlags().isEmpty(), where + " object flags");
            assertEquals(0, base.getNumSvals(), where + " num_svals starts at 0");
        }
    }

    /**
     * A hated element is stored as an {@code EL_INFO_HATES} entry and nothing else: no resistance
     * level, no {@code IGNORE} bit. C's {@code grab_element_flag} sets only that bit.
     */
    @Test
    void aHatedElementCarriesOnlyTheHatesBit() throws IOException {
        ObjectBase chest = declared(new ObjectBaseReader().parseWithResults(REAL_FILE)).get(0);

        assertEquals(Set.of(ElementEnum.ELEM_ACID, ElementEnum.ELEM_FIRE), chest.getElementMap().keySet());
        chest.getElementMap().values().forEach(info -> {
            assertEquals(0, info.getResLevel());
            assertEquals(List.of(ElementInfoEnum.EL_INFO_HATES), info.getFlags().toList());
        });
    }

    /**
     * The list is in file order, so it is not C's chain order, but C indexes {@code kb_info} by tval
     * and nothing reads the chain order afterwards.
     */
    @Test
    void realFileComesBackInFileOrderEndingWithTheSyntheticNone() throws IOException {
        List<ObjectBase> items = new ObjectBaseReader().parseWithResults(REAL_FILE).items();

        assertEquals(35, items.size());
        assertEquals(TValue.TV_CHEST, items.get(0).gettVal());
        assertEquals(TValue.TV_GOLD, items.get(33).gettVal());
        assertEquals(TValue.TV_NONE, items.get(34).gettVal());
    }

    @Test
    void parseReturnsTheSameItemsAsParseWithResults() throws IOException {
        ObjectBaseReader reader = new ObjectBaseReader();

        List<ObjectBase> viaParse = reader.parse(REAL_FILE);
        List<ObjectBase> viaResults = reader.parseWithResults(REAL_FILE).items();

        assertEquals(viaResults.size(), viaParse.size());
        for (int i = 0; i < viaResults.size(); i++) {
            assertEquals(viaResults.get(i).gettVal(), viaParse.get(i).gettVal());
            assertEquals(viaResults.get(i).getName(), viaParse.get(i).getName());
        }
    }

    // ---- The two error channels ------------------------------------------

    /**
     * C's {@code run_parse_object_base} calls {@code parse_file_quit_not_found}, which quits when
     * the file will not open. Java surfaces that as an {@link IOException} and lets the caller decide.
     */
    @Test
    void aMissingFileIsAnIOExceptionOnBothEntryPoints() {
        String missing = tempDir.resolve("no-such-file.txt").toString();

        assertThrows(IOException.class, () -> new ObjectBaseReader().parse(missing));
        assertThrows(IOException.class, () -> new ObjectBaseReader().parseWithResults(missing));
    }

    @Test
    void aSyntaxErrorReturnsNoItemsAndTheMessagesAndParseHidesThem() throws IOException {
        String file = tempFile("syntax.txt", withHeader(1, "name:sword:S~\nflags:SHOW_DICE\n"));

        ParseResult<ObjectBase> result = new ObjectBaseReader().parseWithResults(file);

        assertTrue(result.items().isEmpty(), "not even the synthetic TV_NONE base");
        assertTrue(result.hasErrors());
        assertTrue(new ObjectBaseReader().parse(file).isEmpty());
    }

    /**
     * C's first parser error ends the whole file, so a good record after a bad one does not rescue
     * the load, and no partial list is returned.
     */
    @Test
    void aBadRecordEndsTheLoadEvenWhenALaterRecordIsGood() {
        assertThrows(DataFileParseException.class, () -> load("bad-then-good.txt", withHeader(2,
                "name:notatval:T~\ngraphics:white\nname:sword:S~\ngraphics:white\n")));
    }

    // ---- Duplicate tval: C keeps the first in the file -------------------

    /**
     * C walks its chain from the last record to the first, copying each into {@code kb_info[tval]},
     * so for a repeated tval the earliest record is written last and wins. The Java list keeps both
     * in file order, and {@code ObjectRegistry.getBaseFromTVal} takes the first match, so the same
     * record wins.
     */
    @Test
    void aRepeatedTvalKeepsBothRecordsWithTheFirstOneFirst() throws IOException {
        ParseResult<ObjectBase> result = load("dup-tval.txt", withHeader(2,
                "name:sword:First~\ngraphics:white\nbreak:1\nname:sword:Second~\ngraphics:red\nbreak:2\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        List<ObjectBase> swords = declared(result);
        assertEquals(2, swords.size());
        assertEquals("First~", swords.get(0).getName());
        assertEquals(1, swords.get(0).getBreakPerc());
        assertEquals("Second~", swords.get(1).getName());
    }

    // ---- Directives and defaults (C: parse_object_base_*) ----------------

    /**
     * {@code parse_object_base_defaults}: a later {@code default:} line overwrites an earlier one
     * for the records that follow both.
     */
    @Test
    void aLaterDefaultOverwritesAnEarlierOne() throws IOException {
        ParseResult<ObjectBase> result = load("two-defaults.txt",
                "record-count:1\ndefault:break-chance:10\ndefault:break-chance:25\ndefault:max-stack:40\n"
                        + "name:sword:S~\ngraphics:white\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(25, declared(result).get(0).getBreakPerc());
    }

    /**
     * {@code parse_object_base_name} copies the defaults into a new record whole, so a record with
     * its own {@code break:} keeps it and still takes the default {@code max-stack}.
     */
    @Test
    void eachDefaultFillsOnlyTheFieldTheRecordLeftOut() throws IOException {
        ParseResult<ObjectBase> result = load("partial.txt", withHeader(2,
                "name:sword:S~\ngraphics:white\nbreak:3\nname:shot:T~\ngraphics:white\nmax-stack:7\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        ObjectBase sword = declared(result).get(0);
        ObjectBase shot = declared(result).get(1);
        assertEquals(3, sword.getBreakPerc());
        assertEquals(40, sword.getMaxStack());
        assertEquals(10, shot.getBreakPerc());
        assertEquals(7, shot.getMaxStack());
    }

    /** {@code parse_object_base_break} stores the int as read, so a negative value is not rejected. */
    @Test
    void aNegativeBreakChanceIsStoredAsGiven() throws IOException {
        ParseResult<ObjectBase> result = load("neg-break.txt",
                withHeader(1, "name:sword:S~\ngraphics:white\nbreak:-5\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(-5, declared(result).get(0).getBreakPerc());
    }

    /** {@code tval_find_idx} accepts a number below {@code TV_MAX}; sword is tval 9. */
    @Test
    void aNumericTvalResolvesAsInC() throws IOException {
        ParseResult<ObjectBase> result = load("numeric-tval.txt",
                withHeader(1, "name:9:S~\ngraphics:white\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(TValue.TV_SWORD, declared(result).get(0).gettVal());
    }

    /** {@code tval_find_idx} compares with {@code my_stricmp}, and so does the colour lookup. */
    @Test
    void tvalAndColourNamesMatchIgnoringCase() throws IOException {
        ParseResult<ObjectBase> result = load("case.txt",
                withHeader(1, "name:SWORD:S~\ngraphics:LIGHT UMBER\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(TValue.TV_SWORD, declared(result).get(0).gettVal());
        assertEquals(ColourEnum.fromCode("light umber"), declared(result).get(0).getAttr());
    }

    /**
     * {@code parse_object_base_graphics} sends a one-character colour to {@code color_char_to_attr},
     * which is case-sensitive: {@code w} is white and {@code W} is light slate.
     */
    @Test
    void aOneCharacterColourIsLookedUpByItsIndexCharacter() throws IOException {
        ParseResult<ObjectBase> result = load("char-colour.txt", withHeader(2,
                "name:sword:S~\ngraphics:w\nname:shot:T~\ngraphics:W\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(ColourEnum.COLOUR_WHITE, declared(result).get(0).getAttr());
        assertEquals(ColourEnum.COLOUR_LIGHT_SLATE, declared(result).get(1).getAttr());
    }

    /**
     * {@code parse_object_base_flags} splits on spaces and pipes, so {@code A|B} with no spaces is
     * two flags. Several {@code flags:} lines accumulate; the kind flags and the hated elements
     * end up in their own sets.
     */
    @Test
    void flagsSplitOnPipesWithOrWithoutSpacesAndAccumulateAcrossLines() throws IOException {
        ParseResult<ObjectBase> result = load("flags.txt", withHeader(1,
                "name:sword:S~\ngraphics:white\nflags:HATES_ACID|EASY_KNOW\nflags:HATES_COLD | SHOW_DICE\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        ObjectBase base = declared(result).get(0);
        assertEquals(Set.of("EASY_KNOW", "SHOW_DICE"), kindFlagNames(base));
        assertEquals(Set.of("ACID", "COLD"), hatedElementNames(base));
    }

    @Test
    void aNameWithNoPluralPartLoadsWithANullName() throws IOException {
        ParseResult<ObjectBase> result = load("no-plural.txt",
                withHeader(1, "name:gold\ngraphics:light yellow\n"));

        assertNull(declared(result).get(0).getName(), "C leaves the name NULL");
    }

    @Test
    void windowsLineEndingsLoadTheSameAsUnix() throws IOException {
        String body = withHeader(1, "name:sword:S~\ngraphics:white\nflags:HATES_ACID\n").replace("\n", "\r\n");

        ParseResult<ObjectBase> result = load("crlf.txt", body);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(Set.of("ACID"), hatedElementNames(declared(result).get(0)));
    }

    // ---- Where C accepts and the grammar does not (docs/grammar_issues.md) ----

    private void assertHardError(String name, String content) throws IOException {
        ParseResult<ObjectBase> result = load(name, content);

        assertTrue(result.items().isEmpty(), () -> "items: " + result.items().size());
        assertTrue(result.hasErrors());
    }

    /** C: any number of {@code default:} lines, including none. The grammar needs at least one. */
    @Test
    void aFileWithNoDefaultLineIsAHardErrorWhereCAcceptsIt() throws IOException {
        assertHardError("no-default.txt", "record-count:1\nname:sword:S~\ngraphics:white\n");
    }

    /**
     * C: a {@code default:} line anywhere, affecting only the records after it. The grammar takes
     * them only before the first {@code name:}.
     */
    @Test
    void aDefaultLineAfterARecordIsAHardErrorWhereCAcceptsIt() throws IOException {
        assertHardError("late-default.txt", withHeader(2,
                "name:sword:A~\ngraphics:white\ndefault:break-chance:99\nname:shot:B~\ngraphics:red\n"));
    }

    /** C: a file with no record at all leaves {@code kb_info} zeroed. The grammar needs one. */
    @Test
    void aFileWithNoRecordIsAHardErrorWhereCAcceptsIt() throws IOException {
        assertHardError("no-records.txt", "record-count:0\n" + DEFAULTS);
    }

    /** C: {@code graphics:} is optional, and a record without one keeps colour 0. */
    @Test
    void aRecordWithNoGraphicsLineIsAHardErrorWhereCAcceptsIt() throws IOException {
        assertHardError("no-graphics.txt", withHeader(1, "name:sword:S~\n"));
    }

    /** C: the record directives may come in any order. The grammar fixes it. */
    @Test
    void maxStackBeforeBreakIsAHardErrorWhereCAcceptsAnyOrder() throws IOException {
        assertHardError("order.txt", withHeader(1,
                "name:sword:S~\ngraphics:white\nmax-stack:5\nbreak:3\n"));
    }

    /** C: a repeated {@code break:} overwrites the first. The grammar allows it once. */
    @Test
    void aRepeatedBreakLineIsAHardErrorWhereCLetsItOverwrite() throws IOException {
        assertHardError("repeat-break.txt", withHeader(1,
                "name:sword:S~\ngraphics:white\nbreak:3\nbreak:4\n"));
    }

    /** C: flag names are separated by any run of spaces and pipes. The grammar needs a pipe. */
    @Test
    void flagsSeparatedBySpacesAloneAreAHardErrorWhereCAcceptsThem() throws IOException {
        assertHardError("space-flags.txt", withHeader(1,
                "name:sword:S~\ngraphics:white\nflags:HATES_ACID HATES_FIRE\n"));
    }

    // ---- Where C and Java agree on failing -------------------------------

    /** {@code PARSE_ERROR_MISSING_RECORD_HEADER}: a record directive before any {@code name:}. */
    @Test
    void aRecordDirectiveBeforeAnyNameIsAHardError() throws IOException {
        assertHardError("before-name.txt", withHeader(1, "break:5\nname:sword:S~\ngraphics:white\n"));
        assertHardError("before-name2.txt", withHeader(1, "graphics:white\nname:sword:S~\ngraphics:white\n"));
        assertHardError("before-name3.txt", withHeader(1, "flags:SHOW_DICE\nname:sword:S~\ngraphics:white\n"));
    }

    /** {@code PARSE_ERROR_UNDEFINED_DIRECTIVE}: a {@code default:} label other than the two C knows. */
    @Test
    void anUnknownDefaultLabelIsAHardError() throws IOException {
        assertHardError("bad-default.txt",
                "record-count:1\ndefault:weight:5\n" + DEFAULTS + "name:sword:S~\ngraphics:white\n");
    }

    /** A header the port adds: the upstream file has none, so a file without it is not C's. */
    @Test
    void aFileWithoutTheRecordCountHeaderIsAHardError() throws IOException {
        assertHardError("no-header.txt", DEFAULTS + "name:sword:S~\ngraphics:white\n");
    }

    // ---- Where the assembler once differed from C (docs/Assembler_Issues.md) ----

    /**
     * {@code color_text_to_attr} falls back to white for a name it does not know, with no error,
     * and so does the assembler.
     */
    @Test
    void anUnknownColourIsWhiteAsInC() throws IOException {
        ParseResult<ObjectBase> result = load("bad-colour.txt",
                withHeader(1, "name:sword:S~\ngraphics:nosuchcolour\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(ColourEnum.COLOUR_WHITE, declared(result).get(0).getAttr());
    }

    /**
     * {@code grab_element_flag} accepts {@code IGNORE_<element>} as well as {@code HATES_<element>},
     * setting {@code EL_INFO_IGNORE} on the element and nothing else.
     */
    @Test
    void anIgnoreElementFlagSetsTheIgnoreBitAsInC() throws IOException {
        ParseResult<ObjectBase> result = load("ignore.txt",
                withHeader(1, "name:sword:S~\ngraphics:white\nflags:IGNORE_ACID\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        ObjectBase base = declared(result).get(0);
        assertEquals(Set.of(ElementEnum.ELEM_ACID), base.getElementMap().keySet());
        assertEquals(List.of(ElementInfoEnum.EL_INFO_IGNORE),
                base.getElementMap().get(ElementEnum.ELEM_ACID).getFlags().toList());
    }

    /**
     * With only one of the two defaults given, a record that leaves out the other gets "" from the
     * grammar, which {@code ParseUtils.parseIntString} reads as 0, the value C's zeroed defaults
     * give it.
     */
    @Test
    void aMissingDefaultStoresZeroAsInC() throws IOException {
        ParseResult<ObjectBase> result = load("one-default.txt",
                "record-count:1\ndefault:break-chance:7\nname:sword:S~\ngraphics:white\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(7, declared(result).get(0).getBreakPerc());
        assertEquals(0, declared(result).get(0).getMaxStack());
    }
}
