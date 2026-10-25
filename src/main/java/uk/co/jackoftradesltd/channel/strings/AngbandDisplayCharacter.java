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
 * Three of the five constructors resolve a data-file colour spec to a {@link ColourEnum}: a
 * single character is the index code, anything longer is the display name, matched the same
 * way {@code [C] src/ui-prefs.c}'s {@code parse_prefs_message()} chooses between
 * {@code color_char_to_attr()} and {@code color_text_to_attr()} based on the length of the
 * string. See {@link ColourEnum#fromCode(String)} for that branch, and for the documented
 * departure from C on an unrecognized colour: C substitutes white, this class lets the
 * caller see {@code null} instead. The other two take a colour already resolved.
 *
 * <p>C holds the glyph in a single {@code wchar_t} ({@code d_char} in {@code object.h}), so
 * {@code 'a'} and {@code 97} are the same glyph however they were written. Java's {@code char}
 * is one UTF-16 unit and cannot hold a code point above {@code 0xFFFF}, so the glyph lives in
 * two fields: any code point up to {@code 0xFFFF} in {@link #character}, and a supplementary
 * one ({@code 0x10000} to {@code 0x10FFFF}) in {@link #wideCharacter} with {@link #character}
 * left at {@code '\0'}. The {@code int} constructors make that split once, and the {@code char}
 * ones are already on the narrow side of it, so a glyph built either way is {@link #equals}
 * to the same glyph built the other and hashes alike. An {@code int} outside
 * {@code 0..0x10FFFF} is refused with an {@link IllegalArgumentException} after a fatal log,
 * where C would store any {@code wchar_t} without a check. Read the glyph back whole with
 * {@link #getCharacter()}; the two field getters expose the halves of the split.
 *
 * <p>Class AngbandDisplayCharacter coded before 2026-09-15, commented in full on 2026-10-25.
 *
 * @author Rowan Crowther
 */
public final class AngbandDisplayCharacter {
    /**
     * Class logger. Used only by the two {@code int} constructors, to log the fatal message before
     * they refuse a code point outside {@code 0..0x10FFFF}.
     *
     * <p>Field logger coded before 2026-10-20, commented in full on 2026-10-25.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The glyph drawn for this cell when it fits in one UTF-16 unit, that is any code point up to
     * {@code 0xFFFF} - stored exactly as given, including {@code '\0'} and {@code ' '}, since this
     * class does no interpretation of the character itself. It is {@code '\0'} for a supplementary
     * glyph, which lives in {@link #wideCharacter} instead; a glyph that really is NUL has both
     * fields zero.
     *
     * <p>Field character coded before 2026-09-15, commented in full on 2026-10-25.
     */
    private final char character;

    /**
     * The glyph drawn for this cell when it is a supplementary code point ({@code 0x10000} to
     * {@code 0x10FFFF}) and so cannot be held in {@link #character}; {@code 0} for every other
     * glyph. Together with {@link #character} it is C's single {@code wchar_t} {@code d_char}.
     * Set only by the {@code int} constructors, which refuse a value that is neither.
     *
     * <p>Field wideCharacter coded before 2026-10-25, commented in full on 2026-10-25.
     */
    private final int wideCharacter;

    /**
     * The colour this glyph is drawn in, already resolved to a {@link ColourEnum} constant -
     * or {@code null} if the colour spec given at construction did not resolve to one (see
     * {@link ColourEnum#fromCode(char)} and {@link ColourEnum#fromCode(String)}).
     *
     * <p>Field attributeColour coded before 2026-09-15, commented in full on 2026-09-15.
     */
    private final ColourEnum attributeColour;

    /**
     * The shared empty cell: a space drawn in {@link ColourEnum#COLOUR_DARK}. Safe to hand out
     * by reference because instances are immutable. {@code TrapAssembler} uses it as the
     * default graphic for a trap record that supplies none, so nothing that draws the record
     * has to cope with a {@code null} cell.
     *
     * <p>Field blank coded before 2026-10-20, commented in full on 2026-10-20.
     */
    public static final AngbandDisplayCharacter blank = new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_DARK);

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
        this.wideCharacter = 0;
        this.attributeColour = colourType;
    }

    /**
     * Builds a display cell from a data-file colour code, resolving it the way
     * {@code [C] src/ui-prefs.c}'s {@code parse_prefs_message()} calls
     * {@code color_char_to_attr()} for a single-character colour spec. An unrecognized code
     * leaves {@link #attributeColour} {@code null} rather than substituting a default colour -
     * see {@link ColourEnum#fromCode(char)} for why that is a deliberate departure from the C
     * original, which defaults to white.
     *
     * <p>C's {@code color_char_to_attr()} ({@code [C] src/z-color.c}) returns
     * {@code COLOUR_DARK} for {@code ' '} and {@code '\0'} before it searches the colour table,
     * and {@link ColourEnum#fromCode(char)} does the same, so both resolve to
     * {@link ColourEnum#COLOUR_DARK} rather than leaving {@link #attributeColour} {@code null}.
     *
     * @param character the glyph to display
     * @param colour    the single-character colour index code, e.g. {@code 'r'} for
     *                  {@link ColourEnum#COLOUR_RED}
     *
     * <p>Constructor AngbandDisplayCharacter(char, char) coded before 2026-09-15, commented
     * in full on 2026-09-15, updated on 2026-10-20 to record the space and NUL handling.
     */
    public AngbandDisplayCharacter(char character, char colour) {
        this.character = character;
        this.wideCharacter = 0;
        this.attributeColour = ColourEnum.fromCode(colour);
    }

    /**
     * Builds a display cell from a data-file colour spec of either shape: a single character
     * is the index code, anything longer is treated as the colour's display name. This
     * mirrors the length check {@code [C] src/ui-prefs.c}'s {@code parse_prefs_message()}
     * makes before choosing between {@code color_char_to_attr()} and
     * {@code color_text_to_attr()}; the same branch is made once, in
     * {@link ColourEnum#fromCode(String)}. A one-character string therefore takes the
     * single-character path, including its space and NUL behaviour described on
     * {@link #AngbandDisplayCharacter(char, char)}.
     *
     * @param character the glyph to display
     * @param colour    a single-character colour index code, or a full colour display name
     *                  (matched case-insensitively)
     *
     * <p>Constructor AngbandDisplayCharacter(char, String) coded before 2026-09-15, commented
     * in full on 2026-09-15, updated on 2026-10-20 to point at the single-character path.
     */
    public AngbandDisplayCharacter(char character, @NotNull String colour) {
        this.character = character;
        this.wideCharacter = 0;
        this.attributeColour = ColourEnum.fromCode(colour);
    }

    /**
     * Builds a display cell from a code point and a data-file colour spec of either shape, as
     * {@link #AngbandDisplayCharacter(char, String)} does for the colour.
     *
     * <p>The code point is sorted once here. One that fits in a {@code char}
     * ({@link Character#isBmpCodePoint}) goes into {@link #character}, so it is the same cell the
     * {@code char} constructors build for it; a supplementary one goes into {@link #wideCharacter};
     * anything else, a negative or a value above {@code 0x10FFFF}, is logged as fatal and refused,
     * where C would store it in a {@code wchar_t} unchecked. A lone surrogate such as
     * {@code 0xD800} is a valid {@code char}, and C's {@code wchar_t} would hold it too, so it is
     * accepted.
     *
     * <p>Constructor AngbandDisplayCharacter(int, String) coded before 2026-10-25, commented in
     * full on 2026-10-25.
     *
     * @param character the code point of the glyph to display
     * @param colour    a single-character colour index code, or a full colour display name
     *                  (matched case-insensitively)
     * @throws IllegalArgumentException if {@code character} is not in {@code 0..0x10FFFF}
     */
    public AngbandDisplayCharacter(int character, @NotNull String colour) {
        if (Character.isBmpCodePoint(character)) {
            this.character = (char) character;
            this.wideCharacter = 0;
        } else if (Character.isSupplementaryCodePoint(character)) {
            this.wideCharacter = character;
            this.character = '\0';
        } else {
            String message = "Invalid character code in AngbandDisplayCharacter. Value: " + character;
            logger.fatal(message);
            throw new IllegalArgumentException(message);
        }
        this.attributeColour = ColourEnum.fromCode(colour);
    }

    /**
     * Builds a display cell from a code point and an already-resolved colour, as
     * {@link #AngbandDisplayCharacter(char, ColourEnum)} does for the colour. This is the
     * constructor for a glyph that arrives as an {@code int}, such as a flavour's, whose glyph may
     * lie above {@code 0xFFFF}.
     *
     * <p>The code point is sorted in the same way as in
     * {@link #AngbandDisplayCharacter(int, String)}: a {@code char}-sized value is stored narrow, a
     * supplementary one wide, and a value outside {@code 0..0x10FFFF} is logged as fatal and
     * refused.
     *
     * <p>Constructor AngbandDisplayCharacter(int, ColourEnum) coded before 2026-10-25, commented
     * in full on 2026-10-25.
     *
     * @param character the code point of the glyph to display
     * @param colour    the colour to draw it in
     * @throws IllegalArgumentException if {@code character} is not in {@code 0..0x10FFFF}
     */
    public AngbandDisplayCharacter(int character, @NotNull ColourEnum colour) {
        if (Character.isBmpCodePoint(character)) {
            this.character = (char) character;
            this.wideCharacter = 0;
        } else if (Character.isSupplementaryCodePoint(character)) {
            this.wideCharacter = character;
            this.character = '\0';
        } else {
            String message = "Invalid character code in AngbandDisplayCharacter. Value: " + character;
            logger.fatal(message);
            throw new IllegalArgumentException(message);
        }
        this.attributeColour = colour;
    }

    /**
     * The narrow half of the glyph: the code point itself when it fits in a {@code char}, and
     * {@code '\0'} when the glyph is supplementary (see {@link #getWideCharacter()}). A caller
     * that needs the glyph whole must not stop here, since a wide glyph and a NUL glyph both
     * answer {@code '\0'}; {@link #getCharacter()} tells them apart.
     *
     * <p>Function getNarrowCharacter coded before 2026-09-15, commented in full on 2026-10-25.
     *
     * @return the glyph as a {@code char}, or {@code '\0'} for a supplementary glyph
     */
    @CheckReturnValue
    @Contract(pure = true)
    public char getNarrowCharacter() {
        return character;
    }

    /**
     * The whole glyph as text, one code point long, whichever field holds it.
     *
     * <p>Reads {@link #wideCharacter} when {@link #character} is {@code '\0'}, which for a
     * glyph that really is NUL is {@code 0} too, so that glyph comes back as the one-character
     * string {@code "\0"} and not as an empty string. This is the nearest thing to reading C's
     * {@code wchar_t}, and the only getter that does not lose a supplementary glyph. It builds a
     * new {@code String} on every call, so compare cells with {@link #equals(Object)} rather than
     * through this.
     *
     * <p>Function getCharacter coded before 2026-10-25, commented in full on 2026-10-25.
     *
     * @return the glyph as a one-code-point string
     */
    public String getCharacter() {
        if (character == '\0')
            return Character.toString(wideCharacter);

        return Character.toString(character);
    }

    /**
     * The wide half of the glyph: the code point itself when it is supplementary
     * ({@code 0x10000} to {@code 0x10FFFF}), and {@code 0} for every glyph that fits in a
     * {@code char}, a {@code char}-sized glyph included. So a zero here means "look at
     * {@link #getNarrowCharacter()}", not "no glyph".
     *
     * <p>Function getWideCharacter coded before 2026-10-25, commented in full on 2026-10-25.
     *
     * @return the supplementary code point, or {@code 0}
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getWideCharacter() {
        return wideCharacter;
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
     * <p>The glyph is compared through both of its fields. The {@code int} constructors put a
     * {@code char}-sized code point on the narrow side, so {@code 'a'} and {@code 97} give equal
     * cells, as C's single {@code wchar_t} would; two different supplementary glyphs differ in
     * {@link #wideCharacter}. The colours are compared by identity, which is value comparison for
     * an enum.
     *
     * <p>Function equals coded before 2026-09-15, commented in full on 2026-10-25.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if {@code o} is an {@code AngbandDisplayCharacter} with the same
     * glyph and colour as this one
     */
    @CheckReturnValue
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AngbandDisplayCharacter that = (AngbandDisplayCharacter) o;
        return getNarrowCharacter() == that.getNarrowCharacter()
                && getAttributeColour() == that.getAttributeColour()
                && getWideCharacter() == that.getWideCharacter();
    }

    /**
     * Hash consistent with {@link #equals(Object)}: the narrow glyph, the colour when it is
     * present, and the wide glyph all contribute, so equal cells always hash alike and can be
     * used safely as set/map keys - including a cell built from an unrecognized colour code,
     * where {@link #attributeColour} is {@code null} and only the glyph contributes, the same
     * {@code null}-as-ordinary-value treatment {@link #equals(Object)} gives it via {@code ==}.
     *
     * <p>The wide glyph is mixed in after, and outside, the colour check, so two different
     * supplementary glyphs hash apart even when neither has a resolved colour. Every supplementary
     * glyph has a narrow half of {@code '\0'}, so leaving the wide half out would put them all in
     * one bucket.
     *
     * <p>Function hashCode coded before 2026-09-15, commented in full on 2026-10-25, updated
     * on 2026-09-15 to null-check {@link #attributeColour} rather than throwing
     * {@link NullPointerException} for an unrecognized colour.
     *
     * @return a hash code for this instance
     */
    @CheckReturnValue
    @Contract(pure = true)
    @Override
    public int hashCode() {
        int result = getNarrowCharacter();
        if (getAttributeColour() != null) {
            result = 31 * result + getAttributeColour().hashCode();
        }
        result = 31 * result + getWideCharacter();
        
        return result;
    }
}