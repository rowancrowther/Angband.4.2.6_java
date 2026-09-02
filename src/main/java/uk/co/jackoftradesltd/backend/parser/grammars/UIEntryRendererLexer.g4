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
 * Lexer for lib/gamedata/ui_entry_renderer.txt, the 5 backend renderers
 * used by ui_entry.txt elements which native renderer code to bind to,
 * colour/label-colour/symbol palettes, digit count, and sign display.
 * Cf. src/ui-entry-renderers.c's struct file_parser
 * ui_entry_renderer_parser (ui-entry-renderers.c:1727).
 */

lexer grammar UIEntryRendererLexer;

import CommentsAndEol, Colours, Flags;

/*
 * @author Rowan Crowther
 *
 * record-count directive
 * cf UIEntryRendererGrammar.g4's 'recordCount' rule
 */
RECORD_COUNT
        :   'record-count:'
        ;

/*
 * @author Rowan Crowther
 *
 * name directive
 * cf UIEntryRendererGrammar.g4's 'name' rule
 */
NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * code directive
 * cf UIEntryRendererGrammar.g4's 'code' rule
 */
CODE
        :   'code:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * colours directive. Note American spelling
 * cf UIEntryRendererGrammar.g4's 'colours' rule
 */
COLOURS
        :   'colors:'
        ;

/*
 * @author Rowan Crowther
 *
 * label-colors directive. Note American spelling
 * cf UIEntryRendererGrammar.g4's 'labelcolours' rule
 */
LABELCOLOURS
        :   'labelcolors:'
        ;

/*
 * @author Rowan Crowther
 *
 * symbols directive
 * cf UIEntryRendererGrammar.g4's 'symbols' rule
 */
SYMBOLS
        :   'symbols:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * ndigit directive
 * cf UIEntryRendererGrammar.g4's 'ndigit' rule
 */
NDIGITS
        :   'ndigit:'
        ;

/*
 * @author Rowan Crowther
 *
 * sign directive
 * cf UIEntryRendererGrammar.g4's 'sign' rule
 */
SIGN
        :   'sign:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * One or more Angband colour-code characters - used for colors:/labelcolors:.
 * Alphabet from the shared Colours import.
 */
COLOURCHARS
        :    COLOUR_CODE+
        ;

/*
 * @author Rowan Crowther
 *
 * A single digit - used for ndigit:. Expanded to allow potential multiple
 * digits and a sign to conform to the 'INTEGER' format across the grammars
 */
INTEGER
        :   '-'? ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * A mode to bring in the FLAG lexer token which will match any FLAG enum in
 * the system.
 */
mode FLAG_MODE;

/*
 * @author Rowan Crowther
 *
 * A string representative of an enum, downstream converted to the value of
 * the enum
 */
FLAG
        :   FLAG_BODY -> popMode
        ;

/*
 * @author Rowan Crowther
 *
 * A mode to bring in free-text STRING lexer token which matches everything
 * up to the end of the line
 */
mode FREE_TEXT_MODE;

/*
 * @author Rowan Crowther
 *
 * String token - matches everything up to the end of the line
 */
STRING
        :   ~('\r' | '\n')+ -> popMode
        ;