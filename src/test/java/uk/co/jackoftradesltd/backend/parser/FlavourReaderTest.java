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
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Flavour;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link FlavourReader}: file text -> {@code FlavourLexer}/
 * {@code FlavourGrammar} -> {@link uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindParseRecord}
 * -> {@link uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindAssembler} (which delegates the
 * per-record leg to {@link uk.co.jackoftradesltd.backend.parser.flavour.FlavourAssembler}) ->
 * {@link FlavourKind}/{@link Flavour}.
 *
 * <p><b>Why the heavy seeding.</b> A {@code fixed:} line names its object sub-type by symbol
 * (e.g. {@code fixed:1:Ring of Power:...}), and the assembler resolves that symbol the way C's
 * {@code lookup_sval} does — against the loaded {@code ObjectKind} table
 * ({@link ObjectRegistry#lookupObjectKind(TValue, String)}). Crucially, none of the eight
 * {@code fixed:} sval names exist in {@code object.txt}; every one is an artifact base-object
 * ({@code base-object:ring:Ring of Power}, {@code base-object:amulet:Amulet}, ...) that
 * {@code artifact.txt} <em>synthesises</em> a kind for. So {@link #seed()} has to reproduce the
 * real {@code init()} load order up to and including artifacts — object.txt kinds registered via
 * {@code addObjectKind}, then {@code artifact.txt} run so its synthesis appends the eight special
 * kinds — before flavours can resolve. This mirrors {@code init()}, where {@code loadFlavours()}
 * runs after {@code loadArtifacts()}. Registries are restored in {@link #restore()} so no global
 * state leaks into other suites.
 *
 * <p>The real {@code flavor.txt} carries no {@code record-count} header upstream; the port adds one
 * (the port-wide convention), and it counts the eight {@code kind:} blocks — the top-level records
 * the grammar's {@code file} rule returns — not the 302 individual flavours.
 *
 * @author Rowan Crowther
 */
class FlavourReaderTest {

    private static final String FLAVOUR_FILE = "lib/gamedata/flavor.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";
    private static final String SLAY_FILE = "lib/gamedata/slay.txt";
    private static final String BRAND_FILE = "lib/gamedata/brand.txt";
    private static final String OBJECT_BASE_FILE = "lib/gamedata/object_base.txt";
    private static final String OBJECT_FILE = "lib/gamedata/object.txt";
    private static final String SHAPE_FILE = "lib/gamedata/shape.txt";
    private static final String CURSE_FILE = "lib/gamedata/curse.txt";
    private static final String ACTIVATION_FILE = "lib/gamedata/activation.txt";
    private static final String ARTIFACT_FILE = "lib/gamedata/artifact.txt";

    private static Object savedPains, savedBases, savedSummons, savedSlays,
            savedBrands, savedObjectBases, savedCurses, savedActivations,
            savedObjectKinds, savedKindsByTvalSval, savedObjectBaseKindMax, savedPlayerShapes;

    /**
     * The one shared load of the real file; the happy-path tests assert against this.
     */
    private static ParseResult<FlavourKind> result;

    @TempDir
    Path tempDir;

    /**
     * Reproduces the real {@code init()} load order up to artifacts, then loads the real
     * {@code flavor.txt} once. The object-kind table must hold the artifact-synthesised special
     * kinds before flavours run, or every {@code fixed:} sval lookup returns null.
     */
    @BeforeAll
    static void seed() throws Exception {
        // Dependency order for object.txt + artifact.txt: pains <- bases <- summons/slays;
        // object bases + summons <- curses; activations for artifact act: lines.
        savedPains = setStatic("monsterPains", new PainReader().parse(PAIN_FILE));
        savedBases = setStatic("monsterBases", new MonsterBaseReader().parse(BASE_FILE));
        savedSummons = setStatic("summons", new SummonReader().parse(SUMMON_FILE));
        savedSlays = setStatic("slays", new SlayReader().parse(SLAY_FILE));
        savedBrands = setStatic("brands", new BrandReader().parse(BRAND_FILE));
        savedObjectBases = setStatic("objectBases", new ObjectBaseReader().parse(OBJECT_BASE_FILE));
        savedCurses = setStatic("curses", new CurseReader().parse(CURSE_FILE));
        savedActivations = setStatic("activations", new ActivationReader().parse(ACTIVATION_FILE));

        // object.txt's effects include EST_SHAPECHANGE, whose subtype resolves against playerShapes.
        savedPlayerShapes = setStatic("playerShapes", new ShapeReader().parse(SHAPE_FILE));

        // Register object.txt kinds exactly as loadItemObjects() does (addObjectKind allocates the
        // sval + kind index), record the ordinary-kind ceiling, then run artifact.txt so its
        // synthesis appends the special kinds the fixed: flavours point at.
        savedObjectKinds = setStatic("objectKinds", new ArrayList<ObjectKind>());
        savedKindsByTvalSval = setStatic("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        for (ObjectKind kind : new ItemObjectReader().parse(OBJECT_FILE)) {
            ObjectRegistry.addObjectKind(kind);
        }
        savedObjectBaseKindMax = setStatic("objectBaseKindMax", ObjectRegistry.getObjectKindCount());
        new ArtifactReader().parse(ARTIFACT_FILE);

        result = new FlavourReader().parseWithResults(FLAVOUR_FILE);
    }

    /**
     * Restores the registries mutated by {@link #seed()} so shared static state on
     * the registries do not leak into other suites.
     */
    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
        setStatic("summons", savedSummons);
        setStatic("slays", savedSlays);
        setStatic("brands", savedBrands);
        setStatic("objectBases", savedObjectBases);
        setStatic("curses", savedCurses);
        setStatic("activations", savedActivations);
        setStatic("objectKinds", savedObjectKinds);
        setStatic("kindsByTvalSval", savedKindsByTvalSval);
        setStatic("objectBaseKindMax", savedObjectBaseKindMax);
        setStatic("playerShapes", savedPlayerShapes);
    }

    // ---- fixture + helpers ------------------------------------------------

    /**
     * Writes {@code content} to a temp file and drives it through the reader.
     */
    private ParseResult<FlavourKind> load(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return new FlavourReader().parseWithResults(file.toString());
    }

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * True if any collected error contains {@code fragment}.
     */
    private static boolean hasError(ParseResult<FlavourKind> r, String fragment) {
        return r.errors().stream().anyMatch(s -> s.contains(fragment));
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    /**
     * The kind block for {@code tval} in the shared real-file result, or null.
     */
    private static FlavourKind kind(TValue tval) {
        for (FlavourKind k : result.items()) {
            if (k.getValue() == tval) return k;
        }
        return null;
    }

    /**
     * The one flavour in {@code block} whose displayed text equals {@code text}, or null.
     */
    private static Flavour byText(FlavourKind block, String text) {
        for (Flavour f : block.getFlavours()) {
            if (text.equals(f.getText())) return f;
        }
        return null;
    }

    // =====================================================================
    // (1) HAPPY PATH
    // =====================================================================

    /**
     * A clean load is the end-to-end proof: an unresolved fixed sval, unknown colour or bad tval
     * would each have appended a soft error. Eight kind blocks, 302 flavours, the right glyph on
     * each block.
     */
    @Test
    void theRealFileLoadsEightKindsAnd302FlavoursWithNoErrors() {
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(8, result.items().size());

        assertGlyphAndCount(TValue.TV_RING, '=', 43);
        assertGlyphAndCount(TValue.TV_AMULET, '"', 24);
        assertGlyphAndCount(TValue.TV_STAFF, '_', 35);
        assertGlyphAndCount(TValue.TV_WAND, '-', 35);
        assertGlyphAndCount(TValue.TV_ROD, '-', 35);
        assertGlyphAndCount(TValue.TV_MUSHROOM, ',', 20);
        assertGlyphAndCount(TValue.TV_POTION, '!', 59);
        assertGlyphAndCount(TValue.TV_SCROLL, '?', 51);

        int total = result.items().stream().mapToInt(k -> k.getFlavours().size()).sum();
        assertEquals(302, total);
    }

    private static void assertGlyphAndCount(TValue tval, char glyph, int count) {
        FlavourKind block = kind(tval);
        assertNotNull(block, () -> tval + " block missing");
        assertEquals(glyph, block.getGlyph(), () -> tval + " glyph");
        assertEquals(count, block.getFlavours().size(), () -> tval + " flavour count");
    }

    /**
     * {@code fixed:1:Ring of Power:Yellow:Plain Gold} — a fixed flavour keeps its sval symbol and
     * resolves it to the artifact-synthesised "Ring of Power" kind, so its numeric sval matches
     * that kind's.
     */
    @Test
    void fixedFlavourResolvesItsSvalAgainstTheSynthesisedKind() {
        Flavour plainGold = byText(kind(TValue.TV_RING), "Plain Gold");
        assertNotNull(plainGold);
        assertTrue(plainGold.isFixed());
        assertEquals(1, plainGold.getIndex());
        assertEquals("Ring of Power", plainGold.getsValStr());
        assertSame(ColourEnum.COLOUR_YELLOW, plainGold.getColour());

        ObjectKind ringOfPower = ObjectRegistry.lookupObjectKind(TValue.TV_RING, "Ring of Power");
        assertNotNull(ringOfPower, "the One Ring's base kind is synthesised from artifact.txt");
        assertEquals(ringOfPower.getsVal(), plainGold.getsVal(),
                "resolved sval must equal the synthesised kind's sval");
    }

    /**
     * {@code flavor:2:Green:Alexandrite} — a random (non-fixed) flavour carries no sval symbol and
     * its numeric sval stays 0, which is C's {@code SV_UNKNOWN}.
     */
    @Test
    void randomFlavourHasNoSvalSymbolAndUnknownSval() {
        Flavour alexandrite = byText(kind(TValue.TV_RING), "Alexandrite");
        assertNotNull(alexandrite);
        assertFalse(alexandrite.isFixed());
        assertNull(alexandrite.getsValStr());
        assertEquals(0, alexandrite.getsVal(), "SV_UNKNOWN is 0 in 4.2.6");
        assertSame(ColourEnum.COLOUR_GREEN, alexandrite.getColour());
    }

    /**
     * Scroll flavours are colour-only ({@code flavor:252:White}); their names are generated at
     * runtime from name fragments, so the optional description is absent and {@code text} is null.
     */
    @Test
    void scrollFlavoursAreWhiteAndHaveNullText() {
        FlavourKind scrolls = kind(TValue.TV_SCROLL);
        assertNotNull(scrolls);
        assertEquals(51, scrolls.getFlavours().size());
        for (Flavour f : scrolls.getFlavours()) {
            assertNull(f.getText(), () -> "scroll flavour " + f.getIndex() + " should have no text");
            assertFalse(f.isFixed());
            assertSame(ColourEnum.COLOUR_WHITE, f.getColour());
        }
    }

    // =====================================================================
    // (2) SOFT-ERROR SITES  (one reachable failure per test)
    // =====================================================================

    /**
     * An unrecognised tval on a {@code kind:} line drops that whole block (with its flavours) and
     * records an error.
     */
    @Test
    void unknownTvalDropsTheKindBlock() throws IOException {
        ParseResult<FlavourKind> r = load("badtval.txt", withHeader(1,
                "kind:notatval:=\nflavor:1:Green:Foo\n"));
        assertTrue(hasError(r, "unknown tValue"));
        assertTrue(r.items().isEmpty());
    }

    /**
     * A {@code kind:} glyph must be a single character; a multi-character glyph drops the block.
     */
    @Test
    void multiCharacterGlyphDropsTheKindBlock() throws IOException {
        ParseResult<FlavourKind> r = load("badglyph.txt", withHeader(1,
                "kind:ring:==\nflavor:1:Green:Foo\n"));
        assertTrue(hasError(r, "multi-character glyph"));
        assertTrue(r.items().isEmpty());
    }

    /**
     * An unknown colour on a flavour is a soft error; that flavour is dropped by
     * {@code FlavourAssembler} before the block is assembled.
     */
    @Test
    void unknownColourIsReported() throws IOException {
        ParseResult<FlavourKind> r = load("badcolour.txt", withHeader(1,
                "kind:ring:=\nflavor:2:Notacolour:Foo\n"));
        assertTrue(hasError(r, "unknown colour"));
    }

    /**
     * A non-numeric flavour index fails {@code Integer.parseInt} and is reported. (The index lexes
     * as a plain string, so a non-digit value reaches the parseInt rather than being a syntax error.)
     */
    @Test
    void malformedIndexIsReported() throws IOException {
        ParseResult<FlavourKind> r = load("badindex.txt", withHeader(1,
                "kind:ring:=\nflavor:abc:Green:Foo\n"));
        assertTrue(hasError(r, "malformed integer"));
    }

    /**
     * A {@code fixed:} sval that resolves to no object kind is reported, and — because the
     * assembler treats a broken fixed mapping as fatal to the block — the entire kind block is
     * dropped, taking the otherwise-valid sibling flavour with it.
     */
    @Test
    void unresolvableFixedSvalDropsTheWholeBlock() throws IOException {
        ParseResult<FlavourKind> r = load("badsval.txt", withHeader(1,
                "kind:ring:=\nfixed:1:Nonexistent Ring:Yellow:Foo\nflavor:2:Green:Bar\n"));
        assertTrue(hasError(r, "Invalid object sValue"));
        assertTrue(r.items().isEmpty(), "a bad fixed sval drops the whole block");
    }

    // =====================================================================
    // (3) HEADER / STRUCTURE
    // =====================================================================

    /**
     * The soft record-count check compares the declared header against the number of kind blocks
     * parsed (not the number of flavours); a wrong declaration is reported.
     */
    @Test
    void wrongRecordCountIsReported() throws IOException {
        ParseResult<FlavourKind> r = load("badcount.txt", withHeader(5,
                "kind:ring:=\nflavor:2:Green:Foo\n"));   // one block, header says five
        assertTrue(hasError(r, "record-count header declares 5"));
    }

    /**
     * The {@code file} rule requires the {@code record-count} header first; a file without it is a
     * hard syntax error, so no items come back and the failure is surfaced.
     */
    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<FlavourKind> r = load("noheader.txt",
                "kind:ring:=\nflavor:2:Green:Foo\n");
        assertTrue(r.hasErrors());
        assertTrue(r.items().isEmpty());
    }

    // =====================================================================
    // (4) READER CONTRACT  (init_parse_flavor / run_parse_flavor / finish_parse_flavor)
    // =====================================================================

    /**
     * Expected figures worked out from the upstream {@code flavor.txt} with awk, not from the port:
     * per {@code kind:} block, the flavour count, how many of them are {@code fixed:}, the sum of
     * the {@code index} column, and the {@code index} of the first and of the last line in the file.
     * {@code FlavourKind} stores a block last file line first, so the first and last are swapped
     * when read back.
     */
    @Test
    void everyShippedBlockAgreesWithTheUpstreamFile() {
        //                    tval              flavours fixed indexSum firstInFile lastInFile
        assertBlock(TValue.TV_RING,     43, 4,   946,   1,  43);
        assertBlock(TValue.TV_AMULET,   24, 4,  1332,  52,  67);
        assertBlock(TValue.TV_STAFF,    35, 0,  2975,  68, 102);
        assertBlock(TValue.TV_WAND,     35, 0,  4200, 103, 137);
        assertBlock(TValue.TV_ROD,      35, 0,  5425, 138, 172);
        assertBlock(TValue.TV_MUSHROOM, 20, 0,  3650, 173, 192);
        assertBlock(TValue.TV_POTION,   59, 0, 13098, 193, 251);
        assertBlock(TValue.TV_SCROLL,   51, 0, 14127, 252, 302);
    }

    private static void assertBlock(TValue tval, int count, int fixed, int indexSum,
                                    int firstInFile, int lastInFile) {
        FlavourKind block = kind(tval);
        assertNotNull(block, () -> tval + " block missing");
        assertEquals(count, block.getFlavours().size(), () -> tval + " flavours");
        assertEquals(fixed, block.getFlavours().stream().filter(Flavour::isFixed).count(), () -> tval + " fixed");
        assertEquals(indexSum, block.getFlavours().stream().mapToInt(Flavour::getIndex).sum(),
                () -> tval + " index sum");
        assertEquals(lastInFile, block.getFlavours().getFirst().getIndex(), () -> tval + " stored first");
        assertEquals(firstInFile, block.getFlavours().getLast().getIndex(), () -> tval + " stored last");
    }

    /**
     * C threads each {@code struct flavor} onto the head of {@code flavors}, so a block runs last file
     * line first; {@code FlavourKind} reverses what it is given to match, so the reader's items come
     * back newest-first within a block.
     */
    @Test
    void aBlockComesBackLastFileLineFirst() throws IOException {
        ParseResult<FlavourKind> r = load("order.txt", withHeader(1,
                "kind:ring:=\nflavor:2:Green:First\nflavor:3:Violet:Second\nflavor:4:Red:Third\n"));
        assertFalse(r.hasErrors(), () -> r.errors().toString());
        assertEquals(java.util.List.of(4, 3, 2),
                r.items().getFirst().getFlavours().stream().map(Flavour::getIndex).toList());
    }

    /**
     * {@code parse} is {@code parseWithResults(...).items()}: the soft errors are dropped, and the
     * flavours that did assemble still come back.
     */
    @Test
    void parseReturnsTheItemsAndDiscardsTheSoftErrors() throws IOException {
        Path file = tempDir.resolve("parse.txt");
        Files.writeString(file, withHeader(7, "kind:ring:=\nflavor:2:Green:Foo\nflavor:3:Notacolour:Bar\n"));
        java.util.List<FlavourKind> items = new FlavourReader().parse(file.toString());
        assertEquals(1, items.size());
        assertEquals(1, items.getFirst().getFlavours().size());
        assertEquals("Foo", items.getFirst().getFlavours().getFirst().getText());
    }

    /**
     * C's {@code run_parse_flavor} is {@code parse_file_quit_not_found}, which quits when
     * {@code flavor.txt} is missing; the reader raises an {@link IOException} and leaves the quit to
     * the loader.
     */
    @Test
    void aMissingFileIsAnIOException() {
        assertThrows(IOException.class,
                () -> new FlavourReader().parseWithResults(tempDir.resolve("nothere.txt").toString()));
    }

    /**
     * A description is the rest of the line after the colour, so its spaces and hyphens survive:
     * {@code flavor:67:Light Slate:Mother-of-Pearl} and {@code fixed:1:Ring of Power:Yellow:Plain Gold}.
     */
    @Test
    void descriptionsKeepTheirSpacesAndHyphens() {
        assertNotNull(byText(kind(TValue.TV_AMULET), "Mother-of-Pearl"));
        assertNotNull(byText(kind(TValue.TV_RING), "Plain Gold"));
    }

    /**
     * Comment lines, blank lines and CRLF line ends are skipped, as {@code parser_parse} skips
     * them.
     */
    @Test
    void commentsBlankLinesAndCrlfAreSkipped() throws IOException {
        ParseResult<FlavourKind> r = load("crlf.txt",
                "# header\r\n\r\nrecord-count:1\r\n# note\r\nkind:ring:=\r\n\r\nflavor:2:Green:Foo\r\n");
        assertFalse(r.hasErrors(), () -> r.errors().toString());
        assertEquals(1, r.items().size());
        assertEquals("Foo", r.items().getFirst().getFlavours().getFirst().getText());
    }

    /**
     * C keeps one flat {@code flavors} list, so two {@code kind:} lines for one tval simply add
     * flavours to it; the port makes two {@code FlavourKind} blocks, and the record-count header
     * counts both.
     */
    @Test
    void twoBlocksForOneTvalStayTwoBlocks() throws IOException {
        ParseResult<FlavourKind> r = load("twice.txt", withHeader(2,
                "kind:ring:=\nflavor:2:Green:Foo\nkind:ring:=\nflavor:3:Violet:Bar\n"));
        assertFalse(r.hasErrors(), () -> r.errors().toString());
        assertEquals(2, r.items().size());
    }

    // =====================================================================
    // (5) WHERE THE GRAMMAR DIFFERS FROM C  (docs/grammar_issues.md)
    // =====================================================================

    private void assertHardError(String name, String body) throws IOException {
        ParseResult<FlavourKind> r = load(name, body);
        assertTrue(r.hasErrors(), () -> name + " should be rejected");
        assertTrue(r.items().isEmpty(), () -> name + " should give no items");
    }

    /**
     * {@code parse_flavor_kind} just sets the tval and glyph, so C accepts a {@code kind:} with no
     * flavour under it; the {@code section} rule needs one or more.
     */
    @Test
    void aKindWithNoFlavoursIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("emptykind.txt", withHeader(1, "kind:ring:=\n"));
    }

    /**
     * Before any {@code kind:} line C's {@code flavor_tval} and {@code flavor_glyph} are zero and a
     * {@code flavor:} line is accepted with them; the {@code file} rule needs a {@code kind:} first.
     */
    @Test
    void aFlavourBeforeAnyKindIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("nokind.txt", withHeader(1, "flavor:2:Green:Foo\n"));
    }

    /**
     * {@code desc} is a {@code str}, which {@code parser_parse} does not split on {@code :}, so C
     * keeps {@code A: B} whole; {@code STRING} stops at the colon and the rule then sees a stray
     * {@code COLON}.
     */
    @Test
    void aColonInTheDescriptionIsAHardErrorHereWhereCKeepsItInTheText() throws IOException {
        assertHardError("colontext.txt", withHeader(1, "kind:ring:=\nflavor:2:Green:A: B\n"));
    }

    /**
     * With nothing after the last colon {@code parser_parse} finds no token for the optional
     * {@code desc} and stops, so C reads {@code flavor:2:Green:} as a flavour with no description;
     * the {@code flavour} rule wants a {@code STRING} after the colon.
     */
    @Test
    void aTrailingColonAfterTheColourIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("trailingcolon.txt", withHeader(1, "kind:ring:=\nflavor:2:Green:\n"));
    }

    /**
     * {@code parser_parse} skips leading whitespace; {@code CommentsAndEol.g4} does not, so an
     * indented directive is a syntax error. The shared gap is already recorded for the realm
     * reader.
     */
    @Test
    void anIndentedLineIsAHardErrorHereWhereCSkipsLeadingWhitespace() throws IOException {
        assertHardError("indent.txt", withHeader(1, "kind:ring:=\n  flavor:2:Green:Foo\n"));
    }

    /**
     * The {@code glyph} is a {@code char}: {@code parser_parse} takes one character and, if a colon
     * follows, skips it and ignores the rest of the line, so C reads {@code kind:ring:=:extra} as
     * glyph {@code =}. {@code FlavourGrammar} sees a stray {@code COLON STRING}.
     */
    @Test
    void textAfterTheGlyphIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError("glyphextra.txt", withHeader(1, "kind:ring:=:extra\nflavor:2:Green:Foo\n"));
    }

    /**
     * {@code record-count:0} with no {@code kind:} line: C reads an empty file and ends with an empty
     * {@code flavors} list; the {@code file} rule needs one or more sections.
     */
    @Test
    void anEmptyFileWithAZeroCountIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("empty.txt", "record-count:0\n");
    }

    /**
     * A directive C does not register is {@code PARSE_ERROR_UNDEFINED_DIRECTIVE}, which quits; here
     * the lexer has no token for it and the parse is a hard error with no items.
     */
    @Test
    void anUnknownDirectiveIsAHardError() throws IOException {
        assertHardError("unknown.txt", withHeader(1, "kind:ring:=\nname:Foo\n"));
    }

    // =====================================================================
    // (6) WHERE THE ASSEMBLERS DIFFER FROM C  (docs/Assembler_Issues.md)
    // =====================================================================

    /**
     * {@code color_text_to_attr} falls back to {@code COLOUR_WHITE} for a name it does not know, so
     * C paints {@code flavor:2:Notacolour:Foo} white; {@code ColourEnum.fromCode} returns null and
     * the flavour is dropped with an error. Only a one-character code that matches nothing is an
     * error in C ({@code PARSE_ERROR_INVALID_COLOR}).
     */
    @Test
    void anUnknownColourNameDropsTheFlavourHereWhereCPaintsItWhite() throws IOException {
        ParseResult<FlavourKind> r = load("whitefallback.txt", withHeader(1,
                "kind:ring:=\nflavor:2:Notacolour:Foo\nflavor:3:Green:Bar\n"));
        assertTrue(hasError(r, "unknown colour"));
        assertEquals(1, r.items().getFirst().getFlavours().size());
    }

    /**
     * {@code index} is a {@code uint}: {@code parser_parse} rejects a leading {@code -} with
     * {@code PARSE_ERROR_NOT_NUMBER}, and reads the digits with {@code strtoul} base 0, so
     * {@code 0x10} is 16. {@code Integer.parseInt} takes {@code -5} and refuses {@code 0x10}.
     */
    @Test
    void aNegativeIndexLoadsHereWhereCRejectsItAndAHexIndexFailsWhereCReadsIt() throws IOException {
        ParseResult<FlavourKind> negative = load("negindex.txt", withHeader(1,
                "kind:ring:=\nflavor:-5:Green:Foo\n"));
        assertFalse(negative.hasErrors(), () -> negative.errors().toString());
        assertEquals(-5, negative.items().getFirst().getFlavours().getFirst().getIndex());

        ParseResult<FlavourKind> hex = load("hexindex.txt", withHeader(1,
                "kind:ring:=\nflavor:0x10:Green:Foo\n"));
        assertTrue(hasError(hex, "malformed integer"));
    }
}
