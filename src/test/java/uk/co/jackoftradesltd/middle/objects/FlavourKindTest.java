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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link FlavourKind} against the way C builds its flavour list ({@code init.c}, functions
 * {@code parse_flavor_kind} and {@code parse_flavor_flavor}). C has no such type, so the expected
 * values are what the C parse leaves behind for the same {@code flavor.txt} block: every
 * {@code struct flavor} carries the block's tval ({@code f->tval}) and glyph ({@code f->d_char}),
 * and each new flavour is pushed onto the head of {@code flavors}
 * ({@code f->next = h; parser_setpriv(p, f)}), so a block read as A, B, C ends up in the list as
 * C, B, A.
 *
 * <p>The three passes that read the result are covered by the {@code ObjectUtilsFlavour*} tests;
 * this class pins the grouping they walk.
 *
 * <p>Class FlavourKindTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class FlavourKindTest {

    private static Flavour random(String text, int index) {
        return new Flavour(text, ColourEnum.COLOUR_BLUE, index);
    }

    private static Flavour fixed(String text, int index) {
        return new Flavour(text, "Ring of Power", ColourEnum.COLOUR_YELLOW, index);
    }

    @Nested
    class TvalAndGlyph {
        @Test
        void areStoredOnceForTheBlock() {
            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', List.of(random("Azure", 1)));

            assertEquals(TValue.TV_POTION, kind.getValue());
            assertEquals('!', kind.getGlyph());
        }

        @Test
        void eachFileGlyphSurvivesUnchanged() {
            // the eight kind: lines of flavor.txt
            assertEquals('=', new FlavourKind(TValue.TV_RING, '=', List.of()).getGlyph());
            assertEquals('"', new FlavourKind(TValue.TV_AMULET, '"', List.of()).getGlyph());
            assertEquals('_', new FlavourKind(TValue.TV_STAFF, '_', List.of()).getGlyph());
            assertEquals('-', new FlavourKind(TValue.TV_WAND, '-', List.of()).getGlyph());
            assertEquals('-', new FlavourKind(TValue.TV_ROD, '-', List.of()).getGlyph());
            assertEquals(',', new FlavourKind(TValue.TV_MUSHROOM, ',', List.of()).getGlyph());
            assertEquals('!', new FlavourKind(TValue.TV_POTION, '!', List.of()).getGlyph());
            assertEquals('?', new FlavourKind(TValue.TV_SCROLL, '?', List.of()).getGlyph());
        }
    }

    @Nested
    class FlavourOrder {
        @Test
        void runsLastFileEntryFirstAsCHeadInsertionLeavesIt() {
            Flavour a = random("A", 1);
            Flavour b = random("B", 2);
            Flavour c = random("C", 3);

            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', List.of(a, b, c));

            assertEquals(List.of(c, b, a), kind.getFlavours());
        }

        @Test
        void singleFlavourIsItsOwnReverse() {
            Flavour only = random("Only", 1);

            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', List.of(only));

            assertEquals(List.of(only), kind.getFlavours());
        }

        @Test
        void emptyBlockHoldsNoFlavours() {
            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', List.of());

            assertTrue(kind.getFlavours().isEmpty());
        }

        @Test
        void mixedFixedAndRandomEntriesKeepTheirRelativeReversedOrder() {
            // a ring block interleaves fixed: and flavor: lines
            Flavour power = fixed("Plain Gold", 1);
            Flavour ruby = random("Ruby", 2);
            Flavour jade = random("Jade", 3);

            FlavourKind kind = new FlavourKind(TValue.TV_RING, '=', List.of(power, ruby, jade));

            assertEquals(List.of(jade, ruby, power), kind.getFlavours());
        }

        @Test
        void firstDrawOfTheRandomPassLandsOnTheLastFileEntry() {
            // flavor_assign_random walks the list from the head and takes the entry where the
            // draw reaches zero, so a draw of 0 names the head, which is the last file entry
            Flavour first = random("First", 1);
            Flavour last = random("Last", 2);

            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', List.of(first, last));

            assertSame(last, kind.getFlavours().get(0));
        }
    }

    @Nested
    class Ownership {
        @Test
        void everyFlavourIsToldItsOwner() {
            Flavour a = random("A", 1);
            Flavour b = random("B", 2);

            FlavourKind kind = new FlavourKind(TValue.TV_WAND, '-', List.of(a, b));

            assertSame(kind, a.getFlavourKind());
            assertSame(kind, b.getFlavourKind());
        }

        @Test
        void ownerReachesTheSharedGlyph() {
            Flavour a = random("A", 1);

            new FlavourKind(TValue.TV_SCROLL, '?', List.of(a));

            assertEquals('?', a.getFlavourKind().getGlyph());
            assertEquals(TValue.TV_SCROLL, a.getFlavourKind().getValue());
        }

        @Test
        void adoptingAFlavourReplacesItsPreviousOwner() {
            Flavour a = random("A", 1);
            FlavourKind first = new FlavourKind(TValue.TV_POTION, '!', List.of(a));
            FlavourKind second = new FlavourKind(TValue.TV_SCROLL, '?', List.of(a));

            assertSame(second, a.getFlavourKind());
            assertNotSame(first, a.getFlavourKind());
        }
    }

    @Nested
    class SharingAndCopying {
        @Test
        void listPassedInCanBeChangedAfterwardsWithoutReachingTheBlock() {
            Flavour a = random("A", 1);
            Flavour b = random("B", 2);
            List<Flavour> given = new ArrayList<>(List.of(a, b));

            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', given);
            given.clear();

            assertEquals(List.of(b, a), kind.getFlavours());
        }

        @Test
        void flavoursAreTheCallersOwnObjectsNotCopies() {
            Flavour a = random("A", 1);

            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', List.of(a));

            assertSame(a, kind.getFlavours().get(0));
        }

        @Test
        void flavoursStayMutableThroughTheBlock() {
            // flavor_assign_random writes f->sval and, for scrolls, f->text through the list
            Flavour a = random(null, 1);
            FlavourKind kind = new FlavourKind(TValue.TV_SCROLL, '?', List.of(a));

            Flavour fromBlock = kind.getFlavours().get(0);
            fromBlock.setsVal(7);
            fromBlock.setText("Foo Bar");

            assertEquals(7, a.getsVal());
            assertEquals("Foo Bar", a.getText());
        }
    }

    @Nested
    class FlavoursView {
        @Test
        void cannotBeModified() {
            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', List.of(random("A", 1)));
            List<Flavour> view = kind.getFlavours();

            assertThrows(UnsupportedOperationException.class, () -> view.add(random("B", 2)));
            assertThrows(UnsupportedOperationException.class, view::clear);
            assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        }
    }

    @Nested
    class NullList {
        @Test
        void throwsNullPointerException() {
            assertThrows(NullPointerException.class,
                    () -> new FlavourKind(TValue.TV_POTION, '!', null));
        }
    }
}
