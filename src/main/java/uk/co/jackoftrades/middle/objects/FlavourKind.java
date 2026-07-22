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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.Collections;
import java.util.List;

/**
 * One {@code kind:} block from {@code flavor.txt}: an object type together with
 * the display glyph and the set of {@link Flavour}s that can disguise objects of
 * that type. This groups what C keeps flat — in the original every
 * {@code struct flavor} copies the block's tval and glyph onto itself
 * (src/init.c parse_flavor_flavor); the port hoists the shared tval and glyph up
 * here so each {@link Flavour} need only hold what varies (text, colour, index,
 * sval).
 *
 * @author Rowan Crowther
 */
public class FlavourKind {
    /**
     * The object type these flavours disguise (rings, potions, scrolls, ...).
     *
     * @author Rowan Crowther
     */
    private TValue value;

    /**
     * The glyph every object of this type is drawn with while unidentified.
     *
     * @author Rowan Crowther
     */
    private char glyph;

    /**
     * The flavours available to this type, in file order.
     *
     * @author Rowan Crowther
     */
    private List<Flavour> flavours;

    /**
     * @param value    the object type these flavours belong to
     * @param glyph    the shared display glyph
     * @param flavours the flavours for this type
     * @author Rowan Crowther
     */
    public FlavourKind(TValue value, char glyph, List<Flavour> flavours) {
        this.value = value;
        this.glyph = glyph;
        this.flavours = flavours;
    }

    /**
     * @return the object type these flavours disguise
     * @author Rowan Crowther
     */
    public TValue getValue() {
        return value;
    }

    /**
     * @return the shared display glyph
     * @author Rowan Crowther
     */
    public char getGlyph() {
        return glyph;
    }

    /**
     * @return the flavours for this type, as an unmodifiable list
     * @author Rowan Crowther
     */
    public List<Flavour> getFlavours() {
        return Collections.unmodifiableList(flavours);
    }
}
