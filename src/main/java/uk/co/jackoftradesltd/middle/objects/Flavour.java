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

import uk.co.jackoftradesltd.channel.colour.ColourEnum;

/**
 * One flavour: the disguise (a potion's colour word, a ring's gem) an object kind is shown under
 * while the player is unaware of it. This is the Java port of the C original's
 * {@code struct flavor} ({@code object.h}), read from {@code flavor.txt} by the
 * {@code flavor} parser in {@code init.c}.
 *
 * <p>The port changes the shape of the record in three ways, none of which change what a flavour
 * means:
 * <ul>
 *   <li>C's {@code tval} and {@code d_char} are copied onto every {@code struct flavor} from its
 *       {@code kind:} line; here they live once on the owning {@link FlavourKind}, reached through
 *       {@link #getFlavourKind()}.</li>
 *   <li>C's {@code next} pointer chains every flavour into the single global list {@code flavors}
 *       (built newest-first); here a flavour has no link, and {@link FlavourKind#getFlavours()}
 *       holds the block's flavours reversed, last file entry first, as C's list does.</li>
 *   <li>C's {@code d_attr} is a colour index; here {@link #colour} is a {@link ColourEnum}.</li>
 * </ul>
 *
 * <p>A flavour is mutable in exactly two fields once loaded, both written by
 * {@code ObjectUtils.flavourInit}: {@link #sVal}, which {@code flavor_assign_random} sets to the
 * sval of the kind it binds and {@code flavor_reset_fixed} puts back to 0, and {@link #text},
 * which {@code flavor_assign_random} replaces with a generated title for scrolls. Everything else
 * is fixed at construction.
 *
 * <p>Class Flavour commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class Flavour {
    /**
     * The flavour text shown for the unidentified object (e.g. "Azure"), C's {@code text}.
     * {@code null} when the data file gives no description, which is the case for every scroll
     * flavour: C leaves {@code text} unset there too, and {@code flavor_assign_random} fills it in
     * from {@code scroll_adj} (see {@link #setText}).
     *
     * <p>Field text commented in full on 261009.
     */
    private String text;

    /**
     * The sub-type <em>symbol</em> a fixed flavour binds to (e.g. "Ring of
     * Power"), exactly as written in the data file. Only fixed flavours carry
     * one; it is {@code null} for a randomly-assigned flavour. It has no field in C, which resolves
     * the symbol the moment it is parsed; the port keeps it because the tval needed to resolve it
     * is on the enclosing {@code FlavourKindParseRecord}, so {@code FlavourKindAssembler} does the
     * lookup one level up (C's {@code lookup_sval}) and stores the answer in {@link #sVal}.
     *
     * <p>Field sValStr commented in full on 261009.
     */
    private String sValStr;

    /**
     * The numeric sub-type value this flavour is bound to, C's {@code sval}, where 0 is
     * {@code SV_UNKNOWN} ({@code obj-tval.h}) and means "not bound to any kind". It changes over
     * the life of a flavour:
     * <ul>
     *   <li>a fixed flavour starts as the sval of the kind {@link #sValStr} names;</li>
     *   <li>a random flavour starts at 0, and {@code flavor_assign_random} sets it to the sval of
     *       the kind it is dealt to, so after {@code flavourInit} every random flavour in use
     *       holds a non-zero value;</li>
     *   <li>{@code flavor_reset_fixed} puts every flavour back to 0 for a randarts birth, except
     *       the One Ring's "Plain Gold".</li>
     * </ul>
     * That is why the random-assignment passes in {@code ObjectUtils} test {@code getsVal() == 0}
     * to mean "still on offer" rather than {@link #isFixed()}. C holds this as a {@code uint8_t};
     * the port holds an {@code int}, which cannot differ for any sval {@code object.txt} produces.
     *
     * <p>Field sVal commented in full on 261009.
     */
    private int sVal;

    /**
     * The colour the flavoured object is drawn in until identified, C's {@code d_attr}. The glyph
     * is shared across the whole block and lives on the owning {@link FlavourKind}.
     *
     * <p>Field colour commented in full on 261009.
     */
    private ColourEnum colour;

    /**
     * The flavour's index within the file ({@code fidx} in C), the number on its {@code flavor:}
     * or {@code fixed:} line. C records it and never reads it back, and so far nothing in the port
     * outside tests does either; list position, not this number, is what orders the flavours.
     *
     * <p>Field index commented in full on 261009.
     */
    private int index;

    /**
     * Whether this was read from a {@code fixed:} line (pinned to a named sub-type) as opposed to
     * a {@code flavor:} one. C has no such field: it parses both line types with the same function
     * and tells them apart by whether {@code sval} came out as {@code SV_UNKNOWN}. The port needs
     * the fact on its own because {@link #sVal} moves (see there), so the sval alone can no longer
     * say which kind of line a flavour came from. It is set by the constructor and never changes,
     * and so does not follow {@code flavor_reset_fixed}: a fixed flavour whose sval has been reset
     * to 0 still answers {@code true}. {@code FlavourKindAssembler} is its only reader, at load.
     *
     * <p>Field isFixed commented in full on 261009.
     */
    private boolean isFixed;

    /**
     * The {@link FlavourKind} block this flavour belongs to - the port's way of reaching the
     * shared tval and glyph {@link FlavourKind} hoists up out of each individual flavour (see
     * that class's Javadoc for why). C has nothing to port here: {@code struct flavor} carries
     * its own copy of the tval and glyph directly, so there is no parent link to walk back up.
     *
     * <p>Set once, by {@link FlavourKind}'s constructor, on every flavour it is given; never
     * reassigned afterwards except by {@link #copy()}, which carries the same owner across onto
     * the copy.
     *
     * <p>Field flavourKind coded on 260827, commented in full on 261009.
     */
    private FlavourKind flavourKind;

    /**
     * Constructs a fixed flavour — one read from a {@code fixed:} line, pinned to a named object
     * sub-type. The port of the {@code fixed} branch of {@code parse_flavor_flavor}
     * ({@code init.c}), minus the work that needs the enclosing block: the tval and glyph come from
     * the {@link FlavourKind} that is later handed this flavour, and the sval symbol is only stored
     * here, unresolved. {@link #sVal} starts at 0 until {@code FlavourKindAssembler} resolves
     * {@code sVal} against the object kinds and calls {@link #setsVal}. {@link #flavourKind} is
     * null until {@link FlavourKind}'s constructor adopts the flavour.
     *
     * <p>Constructor Flavour (fixed) commented in full on 261009.
     *
     * @param text   the displayed flavour text
     * @param sVal   the sub-type symbol this flavour is pinned to (unresolved)
     * @param colour the colour the object is drawn in
     * @param index  the flavour's file index
     */
    public Flavour(String text, String sVal, ColourEnum colour, int index) {
        this.text = text;
        this.sValStr = sVal;
        this.colour = colour;
        this.index = index;
        this.isFixed = true;
    }

    /**
     * Constructs a random flavour — one read from a {@code flavor:} line, to be dealt to an
     * unidentified sub-type at random. The port of the {@code flavor} branch of
     * {@code parse_flavor_flavor} ({@code init.c}), where {@code sval} is set to
     * {@code SV_UNKNOWN}: it carries no sval symbol ({@link #sValStr} stays null) and its numeric
     * sval starts unknown (0), until {@code flavor_assign_random} binds it. As for the fixed
     * constructor, the tval and glyph arrive with the owning {@link FlavourKind}.
     *
     * <p>Constructor Flavour (random) commented in full on 261009.
     *
     * @param text   the displayed flavour text ({@code null} when the file omits
     *               it, as scrolls do)
     * @param colour the colour the object is drawn in
     * @param index  the flavour's file index
     */
    public Flavour(String text, ColourEnum colour, int index) {
        this.text = text;
        this.colour = colour;
        this.index = index;
        this.isFixed = false;
    }

    /**
     * Sets the numeric sub-type value, C's direct write to {@code f->sval}. Three callers write
     * it, and each has a C counterpart: {@code FlavourKindAssembler} resolves a fixed flavour's
     * symbol (the {@code lookup_sval} call in {@code parse_flavor_flavor}), {@code flavourAssignRandom}
     * binds a random flavour to the kind it was dealt ({@code flavor_assign_random}), and
     * {@code flavourResetFixed} passes 0 ({@code flavor_reset_fixed}). Nothing is validated: 0 is
     * {@code SV_UNKNOWN}, and any other value is taken as a real sval.
     *
     * <p>Function setsVal commented in full on 261009.
     *
     * @param sVal the new sval, 0 for "not bound to any kind"
     */
    public void setsVal(int sVal) {
        this.sVal = sVal;
    }

    /**
     * Returns the {@link FlavourKind} block this flavour belongs to - see {@link #flavourKind}.
     * {@link ItemObject#objectKindChar()} is the one caller in the main code, reaching through
     * this to read the block's shared glyph, where C reads {@code d_char} off the flavour itself.
     * The value is null only for a flavour that has not yet been handed to a {@link FlavourKind}.
     *
     * <p>Function getFlavourKind coded on 260827, commented in full on 261009.
     *
     * @return the owning {@link FlavourKind}
     */
    public FlavourKind getFlavourKind() {
        return flavourKind;
    }

    /**
     * Sets the {@link FlavourKind} block this flavour belongs to. Package-private: only
     * {@link FlavourKind}'s constructor and {@link #copy()} call it, so a flavour cannot be
     * handed a different owner from outside this package.
     *
     * <p>Function setFlavourKind coded on 260827, commented in full on 261009.
     *
     * @param flavourKind the owning {@link FlavourKind}
     */
    void setFlavourKind(FlavourKind flavourKind) {
        this.flavourKind = flavourKind;
    }

    /**
     * Returns the flavour text, C's {@code text}. Reflects any {@link #setText} call, so a scroll
     * flavour reads {@code null} until {@code flavourAssignRandom} has dealt it a title.
     *
     * <p>Function getText commented in full on 261009.
     *
     * @return the displayed flavour text, or {@code null} if the file omitted it
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the unresolved sub-type symbol, which C has no field for (see {@link #sValStr}).
     * Unlike {@link #getsVal()} it never changes after construction.
     *
     * <p>Function getsValStr commented in full on 261009.
     *
     * @return the unresolved sub-type symbol for a fixed flavour, or {@code null}
     * for a random one
     */
    public String getsValStr() {
        return sValStr;
    }

    /**
     * Returns the sub-type value the flavour is currently bound to, C's {@code sval}. Read it as
     * "which kind has this flavour been tied to right now", not "what kind of line was this": see
     * {@link #sVal} for how it moves, and {@link #isFixed()} for the fact that does not.
     *
     * <p>Function getsVal commented in full on 261009.
     *
     * @return the sub-type value, 0 ({@code SV_UNKNOWN}) while the flavour is bound to no kind
     */
    public int getsVal() {
        return sVal;
    }

    /**
     * Returns the colour the flavoured object is drawn in, C's {@code d_attr}. Read by
     * {@code ItemObject}'s colour lookup for any flavoured kind except an aware scroll, so it
     * still applies to an identified potion or ring, not only to an unidentified one.
     *
     * <p>Function getColour commented in full on 261009.
     *
     * @return the colour the flavoured object is drawn in
     */
    public ColourEnum getColour() {
        return colour;
    }

    /**
     * Returns the file index, C's {@code fidx}. See {@link #index}: C never reads the field back,
     * and the port's only readers are tests.
     *
     * <p>Function getIndex commented in full on 261009.
     *
     * @return the flavour's file index ({@code fidx})
     */
    public int getIndex() {
        return index;
    }

    /**
     * Reports whether the flavour came from a {@code fixed:} line. C has no equivalent; see
     * {@link #isFixed} for why the port needs one and why it does not follow {@link #sVal}.
     *
     * <p>Function isFixed commented in full on 261009.
     *
     * @return {@code true} for a fixed flavour, {@code false} for a random one
     */
    public boolean isFixed() {
        return isFixed;
    }

    /**
     * Returns an independent copy of this flavour, carrying the same owning {@link FlavourKind}.
     * No C original: C never duplicates a {@code struct flavor}. Nothing in the main code or the
     * tests calls this method; in particular {@code ObjectKind.copy} shares its flavour reference
     * rather than calling it.
     *
     * <p>A shallow copy is enough: every field is a primitive, an enum or an immutable
     * {@link String}. The copy is built through the fixed constructor, which sets the fixed flag
     * true, so the flag is then overwritten with this flavour's own value; that route is also why
     * a random flavour's copy gets a null {@link #sValStr}, which is what it already has. The copy
     * is <em>not</em> added to the owner's {@link FlavourKind#getFlavours()} list, so it is
     * invisible to the assignment passes in {@code ObjectUtils}.
     *
     * <p>Function copy commented in full on 261009.
     *
     * @return a new flavour equal to this one
     */
    public Flavour copy() {
        Flavour copy = new Flavour(this.text, this.sValStr, this.colour, this.index);
        copy.setsVal(this.sVal);
        copy.setFlavourKind(this.getFlavourKind());
        copy.isFixed = this.isFixed;
        return copy;
    }

    /**
     * Sets the flavour text. This is the Java equivalent of the direct C struct-field
     * write {@code f->text = scroll_adj[...]} in {@code flavor_assign_random} ({@code obj-util.c}),
     * used when a random scroll flavour is assigned its title from the {@code scroll_adj}
     * table. C stores a pointer into that table, so scroll text is the one {@code text} that is
     * not heap-allocated (hence the scroll exception in {@code cleanup_flavor}); the port stores
     * an ordinary {@link String}, and that distinction does not exist here. Nothing is validated,
     * and {@code null} is accepted.
     *
     * <p>Function setText coded on 260907, commented in full on 261009.
     *
     * @param text the new flavour text
     */
    public void setText(String text) {
        this.text = text;
    }
}
