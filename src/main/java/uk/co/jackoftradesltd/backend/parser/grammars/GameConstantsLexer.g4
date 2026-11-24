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
 * Lexer for lib/gamedata/constants.txt - Angband's miscellaneous
 * engine-tuning constants (level/monster-generation maxima, critical-hit
 * tables, dungeon-generation limits, etc). Cf. the C parser in src/init.c:
 * struct file_parser constants_parser, whose directive table
 * is registered in init_parse_constants(). Every directive
 * there has the shape "<category> sym label int value" EXCEPT the 4
 * critical-hit-level ones, which instead carry several int fields plus a
 * trailing str message.
 *
 * This lexer is deliberately structure-agnostic: it tokenizes every line
 * uniformly as a category name (GC_NAME) followed by colon-separated fields
 * (each a GC_MSG, split by COLON), without distinguishing the directive
 * variants. There is no INTEGER token: a number is a GC_MSG like any other
 * field, and the reader turns it into an int with C's strtol rules. Paired
 * with GameConstantsGrammar.g4, whose 'line' rule collects each line as a
 * category plus a list of field strings; the per-category field counts are
 * enforced downstream in GameConstantsReader, not here.
 *
 * The lexer has three modes, which decide what a piece of text is by where it
 * sits on the line, not by what it spells:
 *   - the default mode holds only GC_NAME (and the imported comment, blank
 *     line and indent rules), so the first token of a line is always the
 *     category, and leading blanks are skipped as C's parse_file skips them;
 *   - COLON_MODE follows a GC_NAME or a GC_MSG, and accepts only COLON, or
 *     the line break that ends the line;
 *   - MSG_MODE follows a COLON, and accepts a GC_MSG, or a line break that
 *     ends a line whose last field is empty.
 * A line break in either of the last two modes returns to the default mode.
 */
lexer grammar GameConstantsLexer;

import CommentsAndEol;

/*
 * @author Rowan Crowther
 *
 * A directive's category name, e.g. "level-max", "melee-critical-level" -
 * lowercase words, optionally hyphenated. Only the first token of a line is
 * a GC_NAME: it is the only token the default mode can produce, and it moves
 * the lexer to COLON_MODE. The label after the category ("monsters",
 * "max-depth") is a GC_MSG, not a GC_NAME, however it is spelled.
 * The narrow pattern is not a gap: C compares each category and label with a
 * fixed list, so both sides refuse anything outside this spelling.
 * cf GameConstantsGrammar.g4's 'line' rule.
 */
GC_NAME
        :   ('a'..'z'|'-')+ -> mode(COLON_MODE)
        ;

mode MSG_MODE;

/*
 * @author Rowan Crowther
 *
 * Any field after a colon: a label ("monsters"), a number ("1024", "-60",
 * "10.5", "0x10") or the trailing message name on a critical-level line
 * ("HIT_GOOD", "hit_good", "STORE3"). It is any text up to the next colon or
 * line end, as C reads an int or a str field, so this token only shapes the
 * field. The authoritative checks are downstream in GameConstantsReader: the
 * label against C's fixed list, the number with strtol rules (text after the
 * digits is ignored), and the message name with a case-blind MessageType
 * lookup that also accepts a number below MSG_MAX, mirroring init.c's
 * message_lookup_by_name -> PARSE_ERROR_INVALID_MESSAGE.
 * It exists only in MSG_MODE, so it can never match at the start of a line and
 * swallow an indent. cf GameConstantsGrammar.g4's 'field' rule.
 */
GC_MSG
        :   ~('\r' | '\n' | ':')+ -> mode(COLON_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * The line break after a colon with nothing after it, e.g. "world:max-depth:".
 * Skipped, and back to the default mode, so the line has a COLON with no
 * field and the grammar refuses it, as C does for an empty value.
 */
MSG_EOL
        :   NL -> skip, mode(DEFAULT_MODE)
        ;

mode COLON_MODE;

/*
 * @author Rowan Crowther
 *
 * Field separator used throughout every directive. Moves the lexer to
 * MSG_MODE, so the text after it is a GC_MSG.
 * cf GameConstantsGrammar.g4's 'line' rule.
 */
COLON   :   ':' -> mode(MSG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * The line break after a category or a field, i.e. the end of a normal line.
 * Skipped, and back to the default mode so the next line starts with a
 * GC_NAME. Without it the break would be a token recognition error and the
 * next line's category would be lost.
 */
COLON_EOL
        :   NL -> skip, mode(DEFAULT_MODE)
        ;
