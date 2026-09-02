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
 * Standalone lexer grammar for the two skip-rules used throughout most of
 * the gamedata data files.
 */
lexer grammar CommentsAndEol;

/*
 * @author Rowan Crowther
 *
 * Comment line, defined as a line beginning with a '#' character followed
 * by everything to the end of the line, and then one new line.
 * This is all skipped.
 */
COMMENT
        :   '#' (~'\n')* '\n' -> skip
        ;

/*
 * @author Rowan Crowther
 *
 * An end-of-line, defined as any number of spaces, a potential \r and
 * then a \n. This is all skipped.
 */
EOL
        :   ' '* '\r'? '\n' -> skip
        ;
