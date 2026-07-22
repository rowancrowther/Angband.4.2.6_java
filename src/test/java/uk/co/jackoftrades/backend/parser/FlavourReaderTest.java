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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.Flavour;
import uk.co.jackoftrades.middle.objects.FlavourKind;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.TValue;

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
 * {@code FlavourGrammar} -> {@link uk.co.jackoftrades.backend.parser.flavour.FlavourKindParseRecord}
 * -> {@link uk.co.jackoftrades.backend.parser.flavour.FlavourKindAssembler} (which delegates the
 * per-record leg to {@link uk.co.jackoftrades.backend.parser.flavour.FlavourAssembler}) ->
 * {@link FlavourKind}/{@link Flavour}.
 *
 * <p><b>Why the heavy seeding.</b> A {@code fixed:} line names its object sub-type by symbol
 * (e.g. {@code fixed:1:Ring of Power:...}), and the assembler resolves that symbol the way C's
 * {@code lookup_sval} does — against the loaded {@code ObjectKind} table
 * ({@link GameConstants#lookupObjectKind(TValue, String)}). Crucially, none of the eight
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
     *
     * @author Rowan Crowther
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
            GameConstants.addObjectKind(kind);
        }
        savedObjectBaseKindMax = setStatic("objectBaseKindMax", GameConstants.getObjectKindCount());
        new ArtifactReader().parse(ARTIFACT_FILE);

        result = new FlavourReader().parseWithResults(FLAVOUR_FILE);
    }

    /**
     * Restores the registries mutated by {@link #seed()} so shared static state on
     * {@link GameConstants} does not leak into other suites.
     *
     * @author Rowan Crowther
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
        Field f = GameConstants.class.getDeclaredField(field);
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
        assertSame(ColourType.COLOUR_TYPE_YELLOW, plainGold.getColour());

        ObjectKind ringOfPower = GameConstants.lookupObjectKind(TValue.TV_RING, "Ring of Power");
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
        assertSame(ColourType.COLOUR_TYPE_GREEN, alexandrite.getColour());
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
            assertSame(ColourType.COLOUR_TYPE_WHITE, f.getColour());
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
}
