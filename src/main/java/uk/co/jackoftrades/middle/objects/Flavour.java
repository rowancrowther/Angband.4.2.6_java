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

import uk.co.jackoftrades.frontend.colour.enums.ColourType;

/**
 * A randomised "flavour" for an unidentified object kind — the disguising name
 * (e.g. a potion's colour) and the glyph/colour it is shown with until
 * identified. This is the Java port of the C original's {@code struct flavor}
 * ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class Flavour {
    /**
     * The flavour text shown for the unidentified object (e.g. "Azure").
     *
     * @author Rowan Crowther
     */
    private String text;

    /**
     * The sub-type <em>symbol</em> a fixed flavour binds to (e.g. "Ring of
     * Power"), exactly as written in the data file. Only fixed flavours carry
     * one; it is {@code null} for a randomly-assigned flavour. Resolved to the
     * numeric {@link #sVal} against the object-kind table, the way C's
     * {@code lookup_sval} does.
     *
     * @author Rowan Crowther
     */
    private String sValStr;

    /**
     * The resolved numeric sub-type value. For a fixed flavour this is the sval
     * of the kind {@link #sValStr} names; for a random flavour it stays 0, which
     * is C's {@code SV_UNKNOWN} ({@code obj-tval.h}).
     *
     * @author Rowan Crowther
     */
    private int sVal;

    /**
     * The colour the flavoured object is drawn in until identified. The glyph is
     * shared across the whole block and lives on the owning {@link FlavourKind}.
     *
     * @author Rowan Crowther
     */
    private ColourType colour;

    /**
     * The flavour's index within the file ({@code fidx} in C) — the stable
     * identity used to pair a flavour with the objects it disguises.
     *
     * @author Rowan Crowther
     */
    private int index;

    /**
     * Whether this is a {@code fixed:} flavour (pinned to a named sub-type) as
     * opposed to a randomly-assigned {@code flavor:} one.
     *
     * @author Rowan Crowther
     */
    private boolean isFixed;

    /**
     * Constructs a fixed flavour — one bound to a specific object sub-type.
     *
     * @param text   the displayed flavour text
     * @param sVal   the sub-type symbol this flavour is pinned to (unresolved)
     * @param colour the colour the object is drawn in
     * @param index  the flavour's file index
     * @author Rowan Crowther
     */
    public Flavour(String text, String sVal, ColourType colour, int index) {
        this.text = text;
        this.sValStr = sVal;
        this.colour = colour;
        this.index = index;
        this.isFixed = true;
    }

    /**
     * Constructs a random flavour — one assigned to an unidentified sub-type at
     * random. It carries no sval symbol ({@link #sValStr} stays null) and its
     * numeric sval stays unknown (0).
     *
     * @param text   the displayed flavour text ({@code null} when the file omits
     *               it, as scrolls do)
     * @param colour the colour the object is drawn in
     * @param index  the flavour's file index
     * @author Rowan Crowther
     */
    public Flavour(String text, ColourType colour, int index) {
        this.text = text;
        this.colour = colour;
        this.index = index;
        this.isFixed = false;
    }

    /**
     * Sets the resolved numeric sub-type value, once {@link #sValStr} has been
     * looked up against the object-kind table.
     *
     * @param sVal the resolved sval
     * @author Rowan Crowther
     */
    public void setsVal(int sVal) {
        this.sVal = sVal;
    }

    /**
     * @return the displayed flavour text, or {@code null} if the file omitted it
     * @author Rowan Crowther
     */
    public String getText() {
        return text;
    }

    /**
     * @return the unresolved sub-type symbol for a fixed flavour, or {@code null}
     * for a random one
     * @author Rowan Crowther
     */
    public String getsValStr() {
        return sValStr;
    }

    /**
     * @return the resolved numeric sub-type value (0 = {@code SV_UNKNOWN} for a
     * random flavour)
     * @author Rowan Crowther
     */
    public int getsVal() {
        return sVal;
    }

    /**
     * @return the colour the flavoured object is drawn in
     * @author Rowan Crowther
     */
    public ColourType getColour() {
        return colour;
    }

    /**
     * @return the flavour's file index ({@code fidx})
     * @author Rowan Crowther
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return {@code true} for a fixed flavour, {@code false} for a random one
     * @author Rowan Crowther
     */
    public boolean isFixed() {
        return isFixed;
    }
}
