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

package uk.co.jackoftradesltd.channel.colour;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The game's named colours - the Java form of C's {@code COLOUR_*} defines
 * ({@code [C] src/z-color.h}) together with the name and translation columns of its
 * {@code color_table[]} ({@code [C] src/z-color.c}).
 * <p>
 * Each constant carries the three things a {@code color_type} row holds: the one-character
 * index code used in data files ({@code index_char}), the display name ({@code name}), and a
 * nine-entry translation row saying what the colour becomes under each
 * {@link ColourTranslation} ({@code color_translate}). Declaration order is C's colour
 * numbering, so {@link #ordinal()} is the C colour index.
 * <p>
 * The translation rows differ from C in one respect. C stores each entry as a colour number;
 * here each entry is the target colour's index code, resolved at use through
 * {@link #fromCode(char)}. A mistyped code therefore shows up as {@code null} from
 * {@link #forTranslation(ColourTranslation)} rather than as a wrong colour.
 * <p>
 * The RGB values live elsewhere: in the frontend's {@code Colour} class, the Java form of C's
 * {@code angband_color_table[][]}.
 * <p>
 * {@link #COLOUR_SHADE} is the odd one out, because C gives it no {@code color_table[]} row.
 *
 * @author Rowan Crowther
 *
 * <p>Class ColourEnum coded before 2026-10-20, commented in full on 2026-10-20.
 */
public enum ColourEnum {
    COLOUR_DARK('d', "Dark",
            new char[]{'d', 'd', 'd', 'd', 'D', 'd', 'D', 'D', 'd'}),
    COLOUR_WHITE('w', "White",
            new char[]{'w', 'w', 'w', 'w', 'y', 'W', 'B', 'y', 'w'}),
    COLOUR_SLATE('s', "Slate",
            new char[]{'s', 'w', 's', 's', 'W', 'D', 'W', 'W', 's'}),
    COLOUR_ORANGE('o', "Orange",
            new char[]{'o', 'w', 'o', 'W', 'y', 's', 'y', 'y', 'o'}),
    COLOUR_RED('r', "Red",
            new char[]{'r', 'w', 'r', 's', 'R', 's', 'R', 'R', 'r'}),
    COLOUR_GREEN('g', "Green",
            new char[]{'g', 'w', 'g', 's', 'G', 's', 'G', 'G', 'g'}),
    COLOUR_BLUE('b', "Blue",
            new char[]{'b', 'w', 'b', 's', 'B', 's', 'B', 'B', 'b'}),
    COLOUR_UMBER('u', "Umber",
            new char[]{'u', 'w', 'u', 'D', 'U', 'D', 'U', 'U', 'u'}),
    COLOUR_LIGHT_DARK('D', "Light Dark",
            new char[]{'D', 'w', 'D', 'D', 's', 'D', 's', 's', 'D'}),
    COLOUR_LIGHT_SLATE('W', "Light Slate",
            new char[]{'W', 'w', 'W', 'W', 'w', 's', 'w', 'w', 's'}),
    COLOUR_LIGHT_PURPLE('P', "Light Purple",
            new char[]{'P', 'w', 'P', 's', 'y', 's', 'y', 'y', 'P'}),
    COLOUR_YELLOW('y', "Yellow",
            new char[]{'y', 'w', 'y', 'W', 'Y', 'W', 'w', 'w', 'y'}),
    COLOUR_LIGHT_RED('R', "Light Red",
            new char[]{'R', 'w', 'R', 'W', 'y', 'r', 'y', 'y', 'R'}),
    COLOUR_LIGHT_GREEN('G', "Light Green",
            new char[]{'G', 'w', 'G', 'W', 'y', 'g', 'y', 'y', 'G'}),
    COLOUR_LIGHT_BLUE('B', "Light Blue",
            new char[]{'B', 'w', 'B', 'W', 'y', 'b', 'y', 'y', 'B'}),
    COLOUR_LIGHT_UMBER('U', "Light Umber",
            new char[]{'U', 'w', 'U', 'W', 'y', 'u', 'y', 'y', 'U'}),
    COLOUR_PURPLE('p', "Purple",
            new char[]{'p', 'w', 'P', 's', 'P', 's', 'P', 'P', 'P'}),
    COLOUR_VIOLET('v', "Violet",
            new char[]{'v', 'w', 'P', 's', 'P', 's', 'P', 'P', 'P'}),
    COLOUR_TEAL('t', "Teal",
            new char[]{'t', 'w', 'b', 's', 'T', 's', 'T', 'T', 'B'}),
    COLOUR_MUD('m', "Mud",
            new char[]{'m', 'w', 'g', 's', 'M', 's', 'M', 'M', 'u'}),
    COLOUR_LIGHT_YELLOW('Y', "Light Yellow",
            new char[]{'Y', 'w', 'y', 'w', 'w', 'y', 'w', 'w', 'Y'}),
    COLOUR_MAGENTA_PINK('i', "Magenta-Pink",
            new char[]{'i', 'w', 'R', 's', 'I', 'r', 'I', 'I', 'P'}),
    COLOUR_LIGHT_TEAL('T', "Light Teal",
            new char[]{'T', 'w', 'B', 'W', 'y', 't', 'y', 'y', 'B'}),
    COLOUR_LIGHT_VIOLET('V', "Light Violet",
            new char[]{'V', 'w', 'P', 'W', 'y', 'v', 'y', 'y', 'P'}),
    COLOUR_LIGHT_PINK('I', "Light Pink",
            new char[]{'I', 'w', 'R', 'W', 'y', 'i', 'y', 'y', 'P'}),
    COLOUR_MUSTARD('M', "Mustard",
            new char[]{'M', 'w', 'y', 's', 'y', 's', 'y', 'y', 'y'}),
    COLOUR_BLUE_SLATE('z', "Blue Slate",
            new char[]{'z', 'w', 'W', 's', 'Z', 's', 'Z', 'Z', 'W'}),
    COLOUR_DEEP_LIGHT_BLUE('Z', "Deep Light Blue",
            new char[]{'Z', 'w', 'B', 'W', 'B', 'z', 'B', 'B', 'B'}),
    /**
     * The shaded-background colour, number 28 in C ({@code COLOUR_SHADE},
     * {@code [C] src/z-color.h}). C has an RGB row for it but no {@code color_table[]} row, so
     * its index character is {@code '\0'}, its name empty and its translation row all zeros.
     * <p>
     * Nothing can name it by character: {@link #fromCode(char)} turns both {@code ' '} and
     * {@code '\0'} into {@link #COLOUR_DARK} before it searches, the same early return C makes.
     * It is a fixed point only under {@link ColourTranslation#ATTR_FULL}; under every other
     * translation C reads the zero row and gets {@code COLOUR_DARK}, and the row here
     * ({@code 'd'} in each of the eight other columns) gives the same answer.
     * <p>
     * The code {@code ' '} and the name {@code "Shade"} are Java inventions. The name does
     * resolve through {@link #fromCode(String)}, where in C no colour name matches, and
     * {@link #attributeToString(ColourEnum)} returns it where C's {@code attr_to_text()}
     * returns the empty name of the zero row.
     *
     * <p>Constant COLOUR_SHADE coded before 2026-10-20, commented in full on 2026-10-20.
     */
    COLOUR_SHADE(' ', "Shade",
            new char[]{' ', 'd', 'd', 'd', 'd', 'd', 'd', 'd', 'd'});

    /**
     * The number of named colours, 29 - C's {@code BASIC_COLORS} ({@code [C] src/z-color.h}).
     * It counts every constant in this enum, {@link #COLOUR_SHADE} included, so the largest
     * valid {@link #ordinal()} is {@code basicColours - 1}. The frontend's
     * {@code Colour.basicColours} carries the same number for its RGB table.
     *
     * <p>Field basicColours coded before 2026-10-20, commented in full on 2026-10-20.
     */
    public final static int basicColours = 29;
    /**
     * Single-character code identifying this colour in data files and preferences - C's
     * {@code index_char}. Searched, case-sensitively, by {@link #fromCode(char)}.
     *
     * <p>Field colourCharacter coded before 2026-10-20, commented in full on 2026-10-20.
     */
    private final char colourCharacter;
    /**
     * Human-readable name of this colour, such as {@code "Light Green"} - C's {@code name}.
     * Searched, case-insensitively, by {@link #fromCode(String)} and returned by
     * {@link #attributeToString(ColourEnum)}.
     *
     * <p>Field colourName coded before 2026-10-20, commented in full on 2026-10-20.
     */
    private final String colourName;
    /**
     * What this colour becomes under each display mode: nine index codes, positioned by
     * {@link ColourTranslation#getValue()} - C's {@code color_translate}, with codes in place
     * of colour numbers. Column 0 ({@code ATTR_FULL}) is the colour's own code and is never
     * read, since {@link #forTranslation(ColourTranslation)} returns early for it.
     *
     * <p>Field colourTranslate coded before 2026-10-20, commented in full on 2026-10-20.
     */
    private final char[] colourTranslate;
    /**
     * Builds one colour-table entry. Called only by the constant declarations above, each of
     * which transcribes one {@code color_table[]} row.
     *
     * @param c      the single-character colour code
     * @param name   the colour's display name
     * @param table  the 9-entry per-context translation table, one code per
     *               {@link ColourTranslation}
     *
     * <p>Constructor ColourEnum(char, String, char[]) coded before 2026-10-20, commented in
     * full on 2026-10-20.
     */
    ColourEnum(char c, String name, char[] table) {
        colourCharacter = c;
        colourName = name;
        colourTranslate = table;
    }

    /**
     * The colour's display name, as data files spell it - the port of
     * {@code attr_to_text()} ({@code [C] src/z-color.c}).
     *
     * <p>C's version takes a colour number and answers {@code "Icky"} for one past
     * {@code BASIC_COLORS}; an enum constant cannot be out of range, so that branch has no
     * Java counterpart. The one other difference is {@link #COLOUR_SHADE}, which gets
     * {@code "Shade"} here and the empty name of its zero row in C.
     *
     * @param colour the colour to name
     * @return its display name, e.g. {@code "Light Green"}
     *
     * <p>Function attributeToString coded before 2026-10-20, commented in full on 2026-10-20.
     */
    @NotNull
    @Contract(pure = true)
    @CheckReturnValue
    public static String attributeToString(@NotNull ColourEnum colour) {
        return colour.colourName;
    }

    /**
     * Resolve a data-file colour to its constant, accepting either spelling: a single character is
     * the index code ({@code "y"}), anything longer is the display name ({@code "Light Green"}),
     * matched without regard to case. This pairs C's {@code color_char_to_attr()} and
     * {@code color_text_to_attr()} ({@code [C] src/z-color.c}) behind one entry point, since a
     * data file may use either.
     *
     * <p>An unrecognized name yields {@code null}, which is the whole point: it is what lets a
     * reader report {@code unknown colour} against the offending line. This is a deliberate
     * departure from C, whose {@code color_text_to_attr()} ends with {@code "Default to white"}
     * and so cannot tell a caller that a name failed to match. Here the caller decides what an
     * unknown colour means, and one that wants white says so itself.
     *
     * <p>This once returned {@code COLOUR_WHITE} for an unmatched name, as C does. That took the
     * choice away from every caller and silently recoloured bad data instead of rejecting it,
     * and left the readers unable to detect a colour they could not resolve.
     *
     * <p>A one-character string is handed to {@link #fromCode(char)}, so the blank handling
     * described there applies to it as well.
     *
     * @param code a one-character index code, or a full display name
     * @return the matching colour, or {@code null} if the code names none
     *
     * <p>Function fromCode(String) coded before 2026-10-20, commented in full on 2026-10-20.
     */
    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public static ColourEnum fromCode(@NotNull String code) {
        if (code.length() == 1) {
            return fromCode(code.charAt(0));
        }

        for (ColourEnum colour : ColourEnum.values()) {
            if (colour.colourName.equalsIgnoreCase(code)) {
                return colour;
            }
        }
        return null;
    }

    /**
     * Resolve a single index character to its colour - C's {@code color_char_to_attr()}
     * ({@code [C] src/z-color.c}).
     *
     * <p>Case matters, and catches people out: {@code 'w'} is White but {@code 'W'} is Light
     * Slate, {@code 'v'} is Violet but {@code 'V'} is Light Purple.
     *
     * <p>Like C, a space or a {@code '\0'} answers {@link #COLOUR_DARK} without searching, so
     * a blank colour field means "no colour" rather than failing. That early return is also
     * why {@link #COLOUR_SHADE} can never be named by character.
     *
     * <p>Unlike C, whose version ends with {@code "we assume white"}, a character that matches
     * no colour yields {@code null} so the caller can reject it - see
     * {@link #fromCode(String)} for the reasoning.
     *
     * @param c the index character from a data file
     * @return the matching colour, {@link #COLOUR_DARK} for a space or {@code '\0'}, or
     *         {@code null} if no colour uses that character
     *
     * <p>Function fromCode(char) coded before 2026-10-20, commented in full on 2026-10-20.
     */
    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public static ColourEnum fromCode(char c) {
        if (c == ' ' || c == '\0') {
            return ColourEnum.COLOUR_DARK;
        }
        
        for (ColourEnum colour : ColourEnum.values()) {
            if (colour.colourCharacter == c) {
                return colour;
            }
        }

        return null;
    }

    /**
     * What this colour becomes under one display mode - one step of C's {@code get_color()}
     * ({@code [C] src/z-color.c}), which reads {@code color_table[a].color_translate[attr]}.
     * <p>
     * {@link ColourTranslation#ATTR_FULL} answers this colour unchanged without consulting the
     * table, as {@code get_color()} does for a zero {@code attr}. Every other mode looks up the
     * target's index code in {@link #colourTranslate} and resolves it with
     * {@link #fromCode(char)}, so a row entry of {@code ' '} or {@code '\0'} would come back as
     * {@link #COLOUR_DARK}; {@link #COLOUR_SHADE}'s row uses {@code 'd'} for that reason.
     * <p>
     * C's {@code get_color()} also returns a graphical attr (high bit set) untouched; there is
     * no Java counterpart, since the high bit is not part of this enum.
     *
     * @param index the display mode to translate for
     * @return the translated colour, or {@code null} if the table row holds a code that names no
     *         colour - a transcription error, which {@code ColourEnumTest} checks cannot happen
     *
     * <p>Function forTranslation coded before 2026-10-20, commented in full on 2026-10-20.
     */
    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public ColourEnum forTranslation(ColourTranslation index) {
        if (index == ColourTranslation.ATTR_FULL)
            return this;
        
        return ColourEnum.fromCode(colourTranslate[index.getValue()]);
    }

    /**
     * Applies a display-mode translation repeatedly - the port of {@code get_color()}
     * ({@code [C] src/z-color.c}), whose {@code n} is {@code numberOfTimes}. The ui-map code
     * uses it to lighten or darken terrain, for example {@code ATTR_DARK} once for a lit grid
     * and twice for a dark one.
     * <p>
     * {@link ColourTranslation#ATTR_FULL} returns {@code startColour} at once, whatever
     * {@code numberOfTimes} is. For any other mode, {@code numberOfTimes} of zero or less
     * returns {@code startColour}, and a {@code null} anywhere in the chain stays {@code null}
     * rather than throwing.
     *
     * @param startColour   the colour to translate
     * @param translation   the display mode to translate for
     * @param numberOfTimes how many steps to apply
     * @return the colour after the steps, or {@code null} if a step reached an unresolvable code
     *
     * <p>Function translateColour coded before 2026-10-20, commented in full on 2026-10-20.
     */
    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public static ColourEnum translateColour(@NotNull ColourEnum startColour, @NotNull ColourTranslation translation,
                                             int numberOfTimes) {
        if (translation == ColourTranslation.ATTR_FULL)
            return startColour;
        
        ColourEnum currentColour = startColour;

        for (int i = 0; i < numberOfTimes; i++) {
            if (currentColour != null) {
                currentColour = currentColour.forTranslation(translation);
            }
        }

        return currentColour;
    }
}
