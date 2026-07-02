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
 * Standalone lexer grammar for the Angband Display Characters.
 *
 * It has no @header instructions or java-domain-type coupling.
 *
 * An AngbandDisplayCharacter consists of a character glyph and
 * a letter/colour name in the format
 * <line directive>:<glyph>:<colour>, Glyph can be any displayable
 * character, but at present is marked as any character not a new
 * line. Colour is one of 28 different colours, represented either
 * by a single character, or by a colour name (in minuscule or
 * title case).
 */
lexer grammar AngbandDisplayCharacter;

import Colours;

/*
 * @author Rowan Crowther
 *
 * 'graphics': token, used to mark and enter the GLYPH_MODE mode. Does
 * NOT assume only a glyph - could be followed by ':' colour
 */
GRAPHICS
        :   'graphics:' -> mode(GLYPH_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * 'color': token, used to mark and enter the COLOUR_MODE mode.
 * Assumes no glyph on this line.
 */
COLOUR_ONLY
        :   'color:' -> mode(COLOUR_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * 'glyph': token, used to mark and enter the GLYPH_MODE mode.
 * Does NOT assume a colour on this line.
 */
GLYPH_ONLY
        :   'glyph:' -> mode(GLYPH_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * GRAPHICS_COLOUR_FRAGMENT contains all possible single letter
 * values for the colour table, as well as the colour names, both
 * minuscule and title case. Both forms are sourced from the shared
 * Colours import so the colour alphabet lives in exactly one place.
 */
 fragment GRAPHICS_COLOUR_FRAGMENT
        :   COLOUR_CODE | COLOUR_NAME
        ;

/*
 * @author Rowan Crowther
 *
 * GRAPHICS_SYMBOL_FRAGMENT allows all possible characters apart from a :,
 * or a new line character.
 */
fragment GRAPHICS_SYMBOL_FRAGMENT
        :   ~(':' | '\r' | '\n')
        ;

/*
 * @author Rowan Crowther
 *
 * COLOUR_MODE - entered into either at the start of a colour-only line,
 * or after a GLYPH_MODE COLON_SWITCH token.
 */
mode COLOUR_MODE;


/*
 * @author Rowan Crowther
 *
 * Pulls in exactly one Graphics colour, either a char representation or
 * the full name of the character
 */
COLOUR_VALUES
        :   GRAPHICS_COLOUR_FRAGMENT -> mode(DEFAULT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * GLYPH_MODE - entered into either after a graphics:glyph:colour or as part of a
 * glyph:glyph command.
 *
 * This mode is left in two ways - one, if a colon is found after the glyph.
 * two, if an EOL is found after the glyph.
 */
mode GLYPH_MODE;

/*
 * @author Rowan Crowther
 *
 * Wrapper for a GRAPHICS_SYMBOL_FRAGMENT
 */
GLYPH_VALUES
        :   GRAPHICS_SYMBOL_FRAGMENT
        ;

/*
 * @author Rowan Crowther
 *
 * A colon found after a glyph symbol. Swaps to COLOUR_MODE.
 */
GLYPH_COLON_SWITCH
        :   ':' -> mode(COLOUR_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * An EOL found after a glyph symbol. Swaps to DEFAULT_MODE and
 * skips the EOL token
 */
GLYPH_EOL
        :   '\r'* '\n' -> mode(DEFAULT_MODE), skip
        ;