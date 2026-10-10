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

import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One {@code kind:} block from {@code flavor.txt}: an object type together with
 * the display glyph and the set of {@link Flavour}s that can disguise objects of
 * that type. This groups what C keeps flat — in the original every
 * {@code struct flavor} copies the block's tval and glyph onto itself
 * ({@code init.c}, function {@code parse_flavor_flavor}); the port hoists the shared tval and
 * glyph up here so each {@link Flavour} need only hold what varies (text, colour, index, sval).
 *
 * <p>There is no C type to port. C's {@code parse_flavor_kind} ({@code init.c}) keeps the current
 * block's tval and glyph in two file-scope statics, {@code flavor_tval} and {@code flavor_glyph},
 * and {@code parse_flavor_flavor} stamps them onto each {@code struct flavor} it allocates, which
 * it pushes onto the head of the single global list {@code flavors} (finished by
 * {@code finish_parse_flavor}). This class is what those statics become once the block is a
 * value: the tval and glyph are stored once, and the flavours that C would have stamped hang
 * beneath them.
 *
 * <p>Two C behaviours are kept deliberately:
 * <ul>
 *   <li>The flavours run newest-first within the block, as C's list does; see {@link #flavours}.
 *       C's list also runs newest-first across blocks, but every consumer
 *       ({@code flavor_assign_fixed}, {@code flavor_assign_random}, {@code flavor_reset_fixed},
 *       all in {@code obj-util.c}) filters by tval, so only the order inside one type is
 *       observable and the registry keeps the blocks in file order.</li>
 *   <li>The flavours stay mutable after the block is built. {@link #getFlavours()} hides the
 *       <em>list</em>, not the {@link Flavour}s in it, because the assignment passes write each
 *       flavour's sval and (for scrolls) text, as C writes them through the shared
 *       {@code struct flavor} pointers.</li>
 * </ul>
 *
 * <p>The block carries no validation of its own: an unknown tval or a glyph that is not a single
 * character is rejected earlier, by {@code FlavourKindAssembler}, which drops the whole block
 * (C instead fails the parse with {@code PARSE_ERROR_UNRECOGNISED_TVAL}). Nothing here stops two
 * blocks sharing a tval; {@code flavor.txt} gives each tval one block, and
 * {@code ObjectUtils.flavourAssignRandom} records that it relies on this.
 *
 * <p>Class FlavourKind coded before 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class FlavourKind {
    /**
     * The object type these flavours disguise (rings, potions, scrolls, ...), C's
     * {@code flavor_tval} static as copied onto each {@code struct flavor} as {@code tval}. Set
     * once by the constructor and never changed. Read by the three {@code ObjectUtils} passes that
     * port {@code flavor_assign_fixed}, {@code flavor_assign_random} and
     * {@code flavor_reset_fixed}, each of which matches on it where C matches on
     * {@code f->tval}.
     *
     * <p>Field value coded before 261010, commented in full on 261010.
     */
    private TValue value;

    /**
     * The glyph every object of this type is drawn with while unidentified, C's
     * {@code flavor_glyph} static as copied onto each {@code struct flavor} as {@code d_char}.
     * C holds a {@code wchar_t}; the port holds a {@code char}, so a glyph outside the Basic
     * Multilingual Plane cannot be represented, but {@code FlavourKindAssembler} only accepts a
     * one-{@code char} string and the glyphs in {@code flavor.txt} are all ASCII. Set once by the
     * constructor and never changed.
     *
     * <p>Field glyph coded before 261010, commented in full on 261010.
     */
    private char glyph;

    /**
     * The flavours available to this type, stored last file entry first. C threads every
     * {@code struct flavor} onto the head of its global list as it is parsed, so the list runs
     * newest-first, and the random-assignment pass counts down it; the constructor reverses the
     * list it is given so that the same draw names the same flavour. It is a copy, so changing the
     * list passed to the constructor afterwards does not reach this field.
     *
     * <p>Field flavours commented in full on 261009.
     */
    private List<Flavour> flavours;

    /**
     * Builds the block from its parts, which is the point at which C's per-flavour copying of the
     * tval and glyph happens: instead of stamping each flavour, the constructor stores the two
     * values once and tells every flavour who owns it.
     *
     * <p>Each flavour in {@code flavours} is handed this block as its owner through
     * {@code Flavour.setFlavourKind}, replacing any owner it had; that is how
     * {@code Flavour.getFlavourKind} reaches the glyph. The list is then copied in reverse, so the
     * stored order is last file entry first, as C's head-insertion leaves its list (see
     * {@link #flavours}). Given {@code [A, B, C]} in file order the block holds {@code [C, B, A]},
     * and an empty list gives an empty block. The {@link Flavour}s themselves are shared with the
     * caller, not copied.
     *
     * <p>A {@code null} list throws {@link NullPointerException}; C has no equivalent, since its
     * list is built one node at a time and an empty block is simply no nodes.
     *
     * <p>Constructor FlavourKind coded before 261010, commented in full on 261010.
     *
     * @param value    the object type these flavours belong to
     * @param glyph    the shared display glyph
     * @param flavours the flavours for this type, in file order
     */
    public FlavourKind(TValue value, char glyph, List<Flavour> flavours) {
        this.value = value;
        this.glyph = glyph;
        for (Flavour flavour : flavours) {
            flavour.setFlavourKind(this);
        }
        this.flavours = new ArrayList<Flavour>(flavours.reversed());
    }

    /**
     * Returns the object type the block disguises, which C reads as {@code f->tval} on each
     * {@code struct flavor}. The three {@code ObjectUtils} flavour passes use it to pick the blocks
     * for the tval they are working on.
     *
     * <p>Function getValue coded before 261010, commented in full on 261010.
     *
     * @return the object type these flavours disguise
     */
    public TValue getValue() {
        return value;
    }

    /**
     * Returns the glyph shared by every flavour of the block, which C reads as {@code f->d_char}.
     * {@code ItemObject.objectKindChar} reaches it through the object kind's flavour, so the
     * answer is the same whichever flavour of the block the kind was dealt.
     *
     * <p>Function getGlyph coded before 261010, commented in full on 261010.
     *
     * @return the shared display glyph
     */
    public char getGlyph() {
        return glyph;
    }

    /**
     * Returns the block's flavours, last file entry first (see {@link #flavours}), wrapped so the
     * caller cannot add, remove or reorder them. It is a live view of the stored list rather than a
     * snapshot, but nothing changes that list after construction. The {@link Flavour}s inside are
     * the real ones, not copies: writing a flavour's sval or text through them is how the
     * assignment passes work.
     *
     * <p>Function getFlavours coded before 261010, commented in full on 261010.
     *
     * @return the flavours for this type, as an unmodifiable list
     */
    public List<Flavour> getFlavours() {
        return Collections.unmodifiableList(flavours);
    }
}
