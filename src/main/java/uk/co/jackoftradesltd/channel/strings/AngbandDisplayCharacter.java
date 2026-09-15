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

package uk.co.jackoftradesltd.channel.strings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;

/**
 * Immutable pairing of a display glyph and the colour it is drawn in - one screen cell's
 * worth of data.
 * <p>
 * The C original has no struct for this pairing: it threads {@code attr} and {@code char}
 * as sibling fields or parser arguments instead, for example the {@code int attr int char}
 * pairs every {@code object}/{@code monster}/{@code feat}/{@code trap}/{@code GF}/
 * {@code flavor} line registers in {@code [C] src/ui-prefs.c}. This class gives that pairing
 * a name and value semantics on the Java side of the boundary.
 * <p>
 * Two of the three constructors resolve a data-file colour spec to a {@link ColourEnum}: a
 * single character is the index code, anything longer is the display name, matched the same
 * way {@code [C] src/ui-prefs.c}'s {@code parse_prefs_message()} chooses between
 * {@code color_char_to_attr()} and {@code color_text_to_attr()} based on the length of the
 * string. See {@link ColourEnum#fromCode(String)} for that branch, and for the documented
 * departure from C on an unrecognised colour: C substitutes white, this class lets the
 * caller see {@code null} instead.
 *
 * @author Rowan Crowther
 *
 * <p>Class AngbandDisplayCharacter coded before 2026-09-15, commented in full on 2026-09-15.
 */
public final class AngbandDisplayCharacter {
    private static final Logger logger = LogManager.getLogger();
    /**
     * The glyph drawn for this cell - stored exactly as given, including {@code '\0'} and
     * {@code ' '}, since this class does no interpretation of the character itself.
     *
     * <p>Field character coded before 2026-09-15, commented in full on 2026-09-15.
     */
    private final char character;
    /**
     * The colour this glyph is drawn in, already resolved to a {@link ColourEnum} constant -
     * or {@code null} if the colour spec given at construction did not resolve to one (see
     * {@link ColourEnum#fromCode(char)} and {@link ColourEnum#fromCode(String)}).
     *
     * <p>Field attributeColour coded before 2026-09-15, commented in full on 2026-09-15.
     */
    private final ColourEnum attributeColour;

    /**
     * Builds a display cell directly from an already-resolved colour - the constructor to
     * use when the caller already has a {@link ColourEnum} in hand rather than a data-file
     * spelling of one.
     *
     * @param character  the glyph to display
     * @param colourType the colour to draw it in
     *
     * <p>Constructor AngbandDisplayCharacter(char, ColourEnum) coded before 2026-09-15,
     * commented in full on 2026-09-15.
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public AngbandDisplayCharacter(char character, @NotNull ColourEnum colourType) {
        this.character = character;
        this.attributeColour = colourType;
    }

    /**
     * Builds a display cell from a data-file colour code, resolving it the way
     * {@code [C] src/ui-prefs.c}'s {@code parse_prefs_message()} calls
     * {@code color_char_to_attr()} for a single-character colour spec. An unrecognised code
     * leaves {@link #attributeColour} {@code null} rather than substituting a default colour -
     * see {@link ColourEnum#fromCode(char)} for why that is a deliberate departure from the C
     * original, which defaults to white.
     *
     * @param character the glyph to display
     * @param colour    the single-character colour index code, e.g. {@code 'r'} for
     *                  {@link ColourEnum#COLOUR_RED}
     *
     * <p>Constructor AngbandDisplayCharacter(char, char) coded before 2026-09-15, commented
     * in full on 2026-09-15.
     */
    public AngbandDisplayCharacter(char character, char colour) {
        this.character = character;
        this.attributeColour = ColourEnum.fromCode(colour);
    }

    /**
     * Builds a display cell from a data-file colour spec of either shape: a single character
     * is the index code, anything longer is treated as the colour's display name. This
     * mirrors the length check {@code [C] src/ui-prefs.c}'s {@code parse_prefs_message()}
     * makes before choosing between {@code color_char_to_attr()} and
     * {@code color_text_to_attr()}; the same branch is made once, in
     * {@link ColourEnum#fromCode(String)}.
     *
     * @param character the glyph to display
     * @param colour    a single-character colour index code, or a full colour display name
     *                  (matched case-insensitively)
     *
     * <p>Constructor AngbandDisplayCharacter(char, String) coded before 2026-09-15, commented
     * in full on 2026-09-15.
     */
    public AngbandDisplayCharacter(char character, @NotNull String colour) {
        this.character = character;
        this.attributeColour = ColourEnum.fromCode(colour);
    }

    /**
     * The glyph this cell displays.
     *
     * @return the character of this instance, stored verbatim from construction
     *
     * <p>Function getCharacter coded before 2026-09-15, commented in full on 2026-09-15.
     */
    @CheckReturnValue
    @Contract(pure = true)
    public char getCharacter() {
        return character;
    }

    /**
     * The colour this cell is drawn in.
     *
     * @return the colour of this character as a {@link ColourEnum}, or {@code null} if the
     * colour spec given at construction did not resolve to one
     *
     * <p>Function getAttributeColour coded before 2026-09-15, commented in full on
     * 2026-09-15.
     */
    @CheckReturnValue
    @Contract(pure = true)
    public ColourEnum getAttributeColour() {
        return attributeColour;
    }

    /**
     * Value equality: two display cells are equal when they are the same runtime class and
     * carry the same glyph and the same colour - a {@code null} {@link #attributeColour} on
     * both sides counts as a match.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if {@code o} is an {@code AngbandDisplayCharacter} with the same
     * character and colour as this one
     *
     * <p>Function equals coded before 2026-09-15, commented in full on 2026-09-15.
     */
    @CheckReturnValue
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AngbandDisplayCharacter that = (AngbandDisplayCharacter) o;
        return getCharacter() == that.getCharacter() && getAttributeColour() == that.getAttributeColour();
    }

    /**
     * Hash consistent with {@link #equals(Object)}: both the glyph and the colour
     * contribute when the colour is present, so equal cells always hash alike and can be used
     * safely as set/map keys - including a cell built from an unrecognised colour code, where
     * {@link #attributeColour} is {@code null} and only the glyph contributes, the same
     * {@code null}-as-ordinary-value treatment {@link #equals(Object)} gives it via {@code ==}.
     *
     * @return a hash code for this instance
     *
     * <p>Function hashCode coded before 2026-09-15, commented in full on 2026-09-15, updated
     * on 2026-09-15 to null-check {@link #attributeColour} rather than throwing
     * {@link NullPointerException} for an unrecognised colour.
     */
    @CheckReturnValue
    @Contract(pure = true)
    @Override
    public int hashCode() {
        int result = getCharacter();
        if (getAttributeColour() != null) {
            result = 31 * result + getAttributeColour().hashCode();
        }
        return result;
    }
}