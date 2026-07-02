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

/*
 * @author Rowan Crowther
 *
 * Standalone lexer grammar for the Angband colour vocabulary - the single
 * source of truth for the game's colour table as it appears in the gamedata
 * files. Ported from the C original's colour table (src/ui-prefs.c's
 * color_table, mirrored in the Java port by ColourType).
 *
 * It has no @header instructions or java-domain-type coupling - it exports
 * only fragments, so importing it contributes NO token types of its own.
 * Each consumer wraps these fragments in whatever token shape it needs
 * (a single COLOUR_CHAR, a one-or-more COLOURCHARS palette, a char-or-name
 * graphics colour, ...), matching the pattern DiceStrings uses for its
 * dice-string bodies. This keeps every grammar in control of its own token
 * names and lexer precedence while removing the copy-pasted colour alphabet.
 *
 * Colours can be written two ways in the data files: as a single-character
 * code (COLOUR_CODE) or as a full colour name in either minuscule or Title
 * case (COLOUR_NAME). The two are kept as separate fragments so consumers
 * that only ever see one form (e.g. color:<code>) need not admit the other.
 */
lexer grammar Colours;

/*
 * @author Rowan Crowther
 *
 * The 28 printable single-character colour codes, in canonical colour-table
 * order. Each maps to one ColourType constant:
 *
 *   d Dark          w White         s Slate         o Orange
 *   r Red           g Green         b Blue          u Umber
 *   D Light Dark    W Light Slate   P Light Purple  y Yellow
 *   R Light Red     G Light Green   B Light Blue    U Light Umber
 *   p Purple        v Violet        t Teal          m Mud
 *   Y Light Yellow  i Magenta-Pink  T Light Teal    V Light Violet
 *   I Light Pink    M Mustard       z Blue Slate    Z Deep Light Blue
 *
 * The 29th colour, Shade, has a space (' ') for its code; a bare space is not
 * usable as a colour token in any data file, so it is deliberately omitted
 * here (matching every historical hand-written copy of this alphabet). Shade
 * is still reachable by its name via COLOUR_NAME below.
 */
fragment COLOUR_CODE
        :   [dwsorgbuDWPyRGBUpvtmYiTVIMzZ]
        ;

/*
 * @author Rowan Crowther
 *
 * The full colour names, accepted in both minuscule (as written in most
 * gamedata) and Title case (as written in a few preference-style lines).
 * Ordered to mirror COLOUR_CODE above, including Shade, whose code has no
 * printable form. ColourType.getColourType/findColourType resolve either
 * case, so the two variants are equivalent to the consuming grammar.
 */
fragment COLOUR_NAME
        :   (   'dark' | 'white' | 'slate' | 'orange' | 'red' | 'green' | 'blue'
            |   'umber' | 'light dark' | 'light slate' | 'light purple' | 'yellow'
            |   'light red' | 'light green' | 'light blue' | 'light umber'
            |   'purple' | 'violet' | 'teal' | 'mud' | 'light yellow'
            |   'magenta-pink' | 'light teal' | 'light violet' | 'light pink'
            |   'mustard' | 'blue slate' | 'deep light blue' | 'shade'

            |   'Dark' | 'White' | 'Slate' | 'Orange' | 'Red' | 'Green' | 'Blue'
            |   'Umber' | 'Light Dark' | 'Light Slate' | 'Light Purple' | 'Yellow'
            |   'Light Red' | 'Light Green' | 'Light Blue' | 'Light Umber'
            |   'Purple' | 'Violet' | 'Teal' | 'Mud' | 'Light Yellow'
            |   'Magenta-Pink' | 'Light Teal' | 'Light Violet' | 'Light Pink'
            |   'Mustard' | 'Blue Slate' | 'Deep Light Blue' | 'Shade')
        ;
