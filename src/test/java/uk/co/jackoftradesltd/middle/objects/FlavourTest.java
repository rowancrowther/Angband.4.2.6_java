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
 * Tests {@link Flavour} against {@code struct flavor} ({@code object.h}) and the
 * {@code parse_flavor_flavor} parser ({@code init.c}). The expected values come from the C
 * source: a {@code flavor:} line leaves {@code sval} at {@code SV_UNKNOWN} (0, {@code obj-tval.h}),
 * a {@code fixed:} line carries a symbol that is resolved to a real sval, and {@code text} stays
 * {@code NULL} when the line has no description.
 *
 * <p>{@link Flavour#setText} has its own {@code FlavourSetTextTest}. The fixed and reset
 * behaviour of the {@code ObjectUtils} passes is covered by the {@code ObjectUtilsFlavour*} tests;
 * this class pins the record they read and write.
 *
 * @author Rowan Crowther
 */
class FlavourTest {

    @Nested
    class FixedConstructor {
        @Test
        void storesEveryFieldAndLeavesTheSvalUnresolved() {
            Flavour f = new Flavour("Plain Gold", "Ring of Power", ColourEnum.COLOUR_YELLOW, 1);

            assertEquals("Plain Gold", f.getText());
            assertEquals("Ring of Power", f.getsValStr());
            assertEquals(ColourEnum.COLOUR_YELLOW, f.getColour());
            assertEquals(1, f.getIndex());
            assertEquals(0, f.getsVal(), "the symbol is only resolved later, by the assembler");
            assertTrue(f.isFixed());
            assertNull(f.getFlavourKind(), "no owner until a FlavourKind adopts it");
        }

        @Test
        void nullTextIsKept() {
            Flavour f = new Flavour(null, "Ring of Power", ColourEnum.COLOUR_YELLOW, 1);

            assertNull(f.getText());
            assertTrue(f.isFixed());
        }
    }

    @Nested
    class RandomConstructor {
        @Test
        void hasNoSymbolAndAnUnknownSval() {
            Flavour f = new Flavour("Azure", ColourEnum.COLOUR_BLUE, 3);

            assertEquals("Azure", f.getText());
            assertNull(f.getsValStr(), "only fixed: lines carry a sval symbol");
            assertEquals(0, f.getsVal(), "SV_UNKNOWN");
            assertEquals(ColourEnum.COLOUR_BLUE, f.getColour());
            assertEquals(3, f.getIndex());
            assertFalse(f.isFixed());
            assertNull(f.getFlavourKind());
        }

        /** A scroll's {@code flavor:} line has no description, so C leaves {@code text} NULL. */
        @Test
        void aScrollWithNoDescriptionHasNullText() {
            Flavour f = new Flavour(null, ColourEnum.COLOUR_WHITE, 12);

            assertNull(f.getText());
            assertFalse(f.isFixed());
        }

        @Test
        void indexZeroIsAcceptedAsAnOrdinaryIndex() {
            Flavour f = new Flavour("Iron", ColourEnum.COLOUR_SLATE, 0);

            assertEquals(0, f.getIndex());
        }
    }

    @Nested
    class SVal {
        @Test
        void setsValWritesTheValueAndNothingElse() {
            Flavour f = new Flavour("Kellek", "Ring of Power", ColourEnum.COLOUR_RED, 7);

            f.setsVal(42);

            assertEquals(42, f.getsVal());
            assertEquals("Ring of Power", f.getsValStr(), "the unresolved symbol is untouched");
            assertEquals("Kellek", f.getText());
            assertTrue(f.isFixed());
        }

        /**
         * {@code flavor_assign_random} sets {@code f->sval} on a flavour that began as
         * {@code SV_UNKNOWN}; the flavour must not become "fixed" by being bound.
         */
        @Test
        void bindingARandomFlavourDoesNotMakeItFixed() {
            Flavour f = new Flavour("Azure", ColourEnum.COLOUR_BLUE, 3);

            f.setsVal(9);

            assertEquals(9, f.getsVal());
            assertFalse(f.isFixed());
        }

        /**
         * {@code flavor_reset_fixed} writes {@code SV_UNKNOWN} back to a flavour that came from
         * a {@code fixed:} line. C has no way to ask what line it came from afterwards; the port's
         * flag keeps answering true.
         */
        @Test
        void resettingAFixedFlavourToZeroKeepsItFlaggedFixed() {
            Flavour f = new Flavour("Ruby", "Ring of Fire", ColourEnum.COLOUR_RED, 28);
            f.setsVal(31);

            f.setsVal(0);

            assertEquals(0, f.getsVal());
            assertTrue(f.isFixed());
            assertEquals("Ring of Fire", f.getsValStr());
        }
    }

    @Nested
    class Ownership {
        @Test
        void flavourKindConstructorAdoptsEveryFlavour() {
            Flavour a = new Flavour("Azure", ColourEnum.COLOUR_BLUE, 1);
            Flavour b = new Flavour("Ruby", "Ring of Fire", ColourEnum.COLOUR_RED, 2);

            FlavourKind kind = new FlavourKind(TValue.TV_RING, '=', List.of(a, b));

            assertSame(kind, a.getFlavourKind());
            assertSame(kind, b.getFlavourKind());
            assertEquals('=', a.getFlavourKind().getGlyph(), "d_char lives on the block, not the flavour");
            assertEquals(TValue.TV_RING, b.getFlavourKind().getValue());
        }

        @Test
        void setFlavourKindReplacesTheOwner() {
            Flavour f = new Flavour("Azure", ColourEnum.COLOUR_BLUE, 1);
            FlavourKind first = new FlavourKind(TValue.TV_POTION, '!', new ArrayList<>(List.of(f)));
            FlavourKind second = new FlavourKind(TValue.TV_POTION, '!', new ArrayList<>());

            f.setFlavourKind(second);

            assertNotSame(first, f.getFlavourKind());
            assertSame(second, f.getFlavourKind());
        }
    }

    @Nested
    class Copy {
        @Test
        void copiesARandomFlavourFieldForField() {
            Flavour original = new Flavour("Azure", ColourEnum.COLOUR_BLUE, 3);
            original.setsVal(5);
            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', List.of(original));

            Flavour copy = original.copy();

            assertNotSame(original, copy);
            assertEquals("Azure", copy.getText());
            assertNull(copy.getsValStr());
            assertEquals(5, copy.getsVal());
            assertEquals(ColourEnum.COLOUR_BLUE, copy.getColour());
            assertEquals(3, copy.getIndex());
            assertFalse(copy.isFixed(), "the fixed constructor sets true; copy must put it back");
            assertSame(kind, copy.getFlavourKind());
        }

        @Test
        void copiesAFixedFlavourFieldForField() {
            Flavour original = new Flavour("Plain Gold", "Ring of Power", ColourEnum.COLOUR_YELLOW, 1);
            original.setsVal(2);
            FlavourKind kind = new FlavourKind(TValue.TV_RING, '=', List.of(original));

            Flavour copy = original.copy();

            assertEquals("Plain Gold", copy.getText());
            assertEquals("Ring of Power", copy.getsValStr());
            assertEquals(2, copy.getsVal());
            assertTrue(copy.isFixed());
            assertSame(kind, copy.getFlavourKind());
        }

        @Test
        void keepsAResetFixedFlavourFixed() {
            Flavour original = new Flavour("Ruby", "Ring of Fire", ColourEnum.COLOUR_RED, 28);
            original.setsVal(0);

            Flavour copy = original.copy();

            assertEquals(0, copy.getsVal());
            assertTrue(copy.isFixed());
        }

        @Test
        void keepsNullText() {
            Flavour original = new Flavour(null, ColourEnum.COLOUR_WHITE, 12);

            assertNull(original.copy().getText());
        }

        @Test
        void laterChangesToTheCopyDoNotReachTheOriginal() {
            Flavour original = new Flavour("Azure", ColourEnum.COLOUR_BLUE, 3);
            Flavour copy = original.copy();

            copy.setsVal(8);
            copy.setText("Foul");

            assertEquals(0, original.getsVal());
            assertEquals("Azure", original.getText());
        }

        @Test
        void theCopyIsNotAddedToTheOwnersList() {
            Flavour original = new Flavour("Azure", ColourEnum.COLOUR_BLUE, 3);
            FlavourKind kind = new FlavourKind(TValue.TV_POTION, '!', List.of(original));

            Flavour copy = original.copy();

            assertEquals(1, kind.getFlavours().size());
            assertFalse(kind.getFlavours().contains(copy));
        }
    }
}
