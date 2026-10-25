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
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindAssembler;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindParseRecord;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourParseRecord;
import uk.co.jackoftradesltd.middle.objects.Flavour;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the block-level half of {@link FlavourKindAssembler} against C's {@code parse_flavor_kind}
 * ({@code init.c}) and the field reading in {@code parser.c}: the tval check, the glyph check, the
 * order of a block's flavours and the order of the blocks.
 *
 * <p>C rejects a {@code kind:} line whose tval {@code tval_find_idx} ({@code obj-tval.c}) answers
 * with {@code <= 0}, which is every unknown name, every number outside 1 to {@code TV_MAX - 1}, and
 * {@code none} or {@code 0}. A {@code char} field takes one UTF-8 character and refuses further text
 * ({@code PARSE_ERROR_FIELD_TOO_LONG}) or none ({@code PARSE_ERROR_MISSING_FIELD}). Both refusals
 * end the load. No kind table is needed because every flavour here is a random one.
 *
 * <p>Class FlavourKindAssemblerBlockTest coded on 261025, commented in full on 261025.
 *
 * @author Rowan Crowther
 */
class FlavourKindAssemblerBlockTest {

    private static final String FILE = "flavor.txt";

    private static FlavourParseRecord random(String index) {
        return new FlavourParseRecord(index, null, "Green", "Bar", 3);
    }

    private static List<FlavourKind> assemble(FlavourKindParseRecord... blocks) {
        return new FlavourKindAssembler().assemble(List.of(blocks), new ArrayList<>(), FILE);
    }

    private static FlavourKindParseRecord block(String tval, String glyph, int line, FlavourParseRecord... flavours) {
        return new FlavourKindParseRecord(tval, glyph, List.of(flavours), line);
    }

    /** The data-file spellings of a tval, including the {@code armour} one {@code de_armour} rewrites. */
    @Test
    void aKnownTvalIsResolved() {
        assertEquals(TValue.TV_RING, assemble(block("ring", "=", 1, random("1"))).getFirst().getValue());
        assertEquals(TValue.TV_SOFT_ARMOR, assemble(block("soft armour", "(", 1, random("1"))).getFirst().getValue());
    }

    /** {@code tval <= 0} is refused: unknown names, {@code none}, {@code 0}, and a number past {@code TV_MAX}. */
    @ParameterizedTest(name = "tval [{0}] ends the load")
    @ValueSource(strings = {"bogus", "none", "0", "9999", "-1", ""})
    void anUnusableTvalEndsTheLoad(String tval) {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> assemble(block(tval, "=", 7, random("1"))));
        assertTrue(e.getMessage().contains("line: 7"), e.getMessage());
        assertTrue(e.getMessage().contains(FILE), e.getMessage());
        assertTrue(e.getMessage().contains("TValue"), e.getMessage());
    }

    /** More than one character, or none, ends the load. */
    @ParameterizedTest(name = "glyph [{0}] ends the load")
    @ValueSource(strings = {"==", "ab", ""})
    void aGlyphThatIsNotOneCharacterEndsTheLoad(String glyph) {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> assemble(block("ring", glyph, 9, random("1"))));
        assertTrue(e.getMessage().contains("line: 9"), e.getMessage());
        assertTrue(e.getMessage().contains("glyph"), e.getMessage());
    }

    /**
     * C keeps a whole {@code wchar_t}, so one character outside the Basic Multilingual Plane is one
     * glyph, held as its code point.
     */
    @Test
    void aGlyphOutsideTheBasicMultilingualPlaneIsOneCodePoint() {
        assertEquals(0x1F600, assemble(block("ring", "😀", 1, random("1"))).getFirst().getGlyph());
        assertEquals('=', assemble(block("ring", "=", 1, random("1"))).getFirst().getGlyph());
    }

    /** C's list runs newest-first, so file order 1, 2, 3 is held as 3, 2, 1. */
    @Test
    void aBlocksFlavoursAreHeldNewestFirst() {
        List<Flavour> flavours = assemble(block("ring", "=", 1, random("1"), random("2"), random("3")))
                .getFirst().getFlavours();
        assertEquals(List.of(3, 2, 1), flavours.stream().map(Flavour::getIndex).toList());
    }

    /** Blocks stay in file order, each with its own tval and glyph. */
    @Test
    void blocksStayInFileOrder() {
        List<FlavourKind> kinds = assemble(
                block("ring", "=", 1, random("1")),
                block("amulet", "\"", 5, random("1")));
        assertEquals(TValue.TV_RING, kinds.get(0).getValue());
        assertEquals(TValue.TV_AMULET, kinds.get(1).getValue());
        assertEquals('"', kinds.get(1).getGlyph());
    }

    /** A {@code kind:} line with no flavours under it makes C allocate nothing, and loads an empty block here. */
    @Test
    void aBlockWithNoFlavoursLoadsEmpty() {
        List<FlavourKind> kinds = assemble(block("ring", "=", 1));
        assertEquals(1, kinds.size());
        assertTrue(kinds.getFirst().getFlavours().isEmpty());
    }

    /** No records, no blocks. */
    @Test
    void noRecordsGiveNoBlocks() {
        assertTrue(assemble().isEmpty());
    }
}
