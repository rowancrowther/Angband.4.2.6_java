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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link ItemObject#getItemObjectADC()} and the private {@code useFlavourGlyph} decision
 * behind it, the port of C's {@code object_char}/{@code object_attr} calling
 * {@code use_flavor_glyph} ({@code [C] ui-object.c}).
 *
 * <p>C's condition is {@code kind->flavor && !(kind->tval == TV_SCROLL && kind->aware)}: a
 * flavoured kind is drawn by its flavour's glyph/colour unless it is <em>both</em> a scroll and
 * identified, the one case where the kind's own glyph wins back out. Distributing the negation
 * gives {@code flavor && (!isScroll || !isAware)} - a shape easy to mis-transcribe as
 * {@code flavor && (!isScroll && isAware)}, which agrees with C only on two of the four
 * (scroll, aware) combinations. All four are exercised here for exactly that reason.
 *
 * <p>Built with the no-argument constructors and private-field pokes, the same pattern
 * {@link ItemObjectStandardToHTest} uses, rather than the long parsed-data-file constructors -
 * only the tval, flavour and aware flag matter to this decision.
 *
 * <p>Class ItemObjectEquippyGlyphTest coded on 260928, commented in full on 260928.
 *
 * @author Rowan Crowther
 */
class ItemObjectEquippyGlyphTest {

    private static final AngbandDisplayCharacter KIND_GLYPH =
            new AngbandDisplayCharacter('!', ColourEnum.COLOUR_WHITE);
    private static final char FLAVOUR_GLYPH = '?';
    private static final ColourEnum FLAVOUR_COLOUR = ColourEnum.COLOUR_RED;

    /**
     * Writes a private field on anything, since neither {@link ItemObject} nor {@link ObjectKind}
     * offers setters for every field under test.
     *
     * @param target the object to modify
     * @param name   the declared field name
     * @param value  the value to write
     */
    private static void poke(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * A flavoured kind of the given type and awareness. The flavour is wrapped in a
     * {@link FlavourKind} carrying {@link #FLAVOUR_GLYPH}, exactly as {@link FlavourKind}'s
     * constructor sets each flavour's owning block.
     *
     * @param tValue the item type, which decides whether the scroll branch applies
     * @param aware  whether the player is aware of (has identified) this kind
     * @return a flavoured kind with the given type and awareness
     */
    private static ObjectKind flavouredKind(TValue tValue, boolean aware) throws Exception {
        ObjectKind kind = new ObjectKind();
        poke(kind, "tValue", tValue);
        poke(kind, "aware", aware);
        poke(kind, "character", KIND_GLYPH);

        Flavour flavour = new Flavour(null, FLAVOUR_COLOUR, 1);
        new FlavourKind(tValue, FLAVOUR_GLYPH, List.of(flavour));
        poke(kind, "flavour", flavour);

        return kind;
    }

    /**
     * An item of the given kind.
     *
     * @param kind the kind this item is an instance of
     * @return the item
     */
    private static ItemObject item(ObjectKind kind) throws Exception {
        ItemObject item = new ItemObject();
        poke(item, "kind", kind);
        return item;
    }

    @Test
    @DisplayName("a flavoured, unaware, non-scroll kind is drawn by its flavour")
    void nonScrollUnawareUsesFlavourGlyph() throws Exception {
        ItemObject wand = item(flavouredKind(TValue.TV_WAND, false));

        AngbandDisplayCharacter adc = wand.getItemObjectADC();

        assertEquals(FLAVOUR_GLYPH, adc.getCharacter());
        assertEquals(FLAVOUR_COLOUR, adc.getAttributeColour());
    }

    @Test
    @DisplayName("a flavoured, aware, non-scroll kind is still drawn by its flavour")
    void nonScrollAwareUsesFlavourGlyph() throws Exception {
        ItemObject potion = item(flavouredKind(TValue.TV_POTION, true));

        AngbandDisplayCharacter adc = potion.getItemObjectADC();

        assertEquals(FLAVOUR_GLYPH, adc.getCharacter());
        assertEquals(FLAVOUR_COLOUR, adc.getAttributeColour());
    }

    @Test
    @DisplayName("a flavoured, unaware scroll is drawn by its flavour")
    void scrollUnawareUsesFlavourGlyph() throws Exception {
        ItemObject scroll = item(flavouredKind(TValue.TV_SCROLL, false));

        AngbandDisplayCharacter adc = scroll.getItemObjectADC();

        assertEquals(FLAVOUR_GLYPH, adc.getCharacter());
        assertEquals(FLAVOUR_COLOUR, adc.getAttributeColour());
    }

    /**
     * The one case where identification turns the flavour glyph back <em>off</em>: an aware
     * scroll is drawn by its kind's own glyph, matching C's {@code !(tval == TV_SCROLL && aware)}
     * going false. This is the combination the previously inverted condition got wrong.
     */
    @Test
    @DisplayName("a flavoured, aware scroll is drawn by its kind's own glyph, not its flavour")
    void scrollAwareUsesKindsOwnGlyph() throws Exception {
        ItemObject scroll = item(flavouredKind(TValue.TV_SCROLL, true));

        AngbandDisplayCharacter adc = scroll.getItemObjectADC();

        assertEquals(KIND_GLYPH.getCharacter(), adc.getCharacter());
        assertEquals(KIND_GLYPH.getAttributeColour(), adc.getAttributeColour());
    }

    @Test
    @DisplayName("a kind with no flavour is always drawn by its own glyph")
    void kindWithNoFlavourAlwaysUsesItsOwnGlyph() throws Exception {
        ObjectKind kind = new ObjectKind();
        poke(kind, "tValue", TValue.TV_POTION);
        poke(kind, "aware", true);
        poke(kind, "character", KIND_GLYPH);
        // flavour left null

        AngbandDisplayCharacter adc = item(kind).getItemObjectADC();

        assertEquals(KIND_GLYPH.getCharacter(), adc.getCharacter());
        assertEquals(KIND_GLYPH.getAttributeColour(), adc.getAttributeColour());
    }
}
