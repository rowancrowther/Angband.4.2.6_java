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
 * struct file_parser constants_parser (init.c:1051), whose directive table
 * is registered in init_parse_constants() (init.c:949-980). Every directive
 * there has the shape "<category> sym label int value" EXCEPT the 4
 * critical-hit-level ones, which instead carry several int fields plus a
 * trailing str message.
 *
 * This lexer is deliberately structure-agnostic: it tokenises every line
 * uniformly as a category name (GC_NAME) followed by colon-separated fields
 * (GC_MSG / INTEGER, split by COLON), without distinguishing the directive
 * variants. Paired with GameConstantsGrammar.g4, whose 'line' rule collects
 * each line as a category plus a list of field strings; the per-category
 * field counts are enforced downstream in GameConstantsReader, not here.
 */
lexer grammar GameConstantsLexer;

import CommentsAndEol;

/*
 * @author Rowan Crowther
 *
 * A directive's category/label name, e.g. "level-max", "monsters",
 * "melee-critical-level" - lowercase words, optionally hyphenated.
 * cf GameConstantsGrammar.g4's 'line' and 'field' rules.
 */
GC_NAME
        :   ('a'..'z'|'-')+
        ;

/*
 * @author Rowan Crowther
 *
 * The trailing message-name field on a critical-level line, e.g. "HIT_GOOD".
 * Narrower than the C registration's free-text "str msg": every shipped message
 * name *must* be found in MessageType enum, so the pattern ('A'..'Z' | '_')+
 * should match that requirement. This token only shapes the field - the
 * authorative check that the field is valid is the reader's MessageType
 * lookup (GameConstantsReader, mirroring init.c's message_lookup_by_name ->
 * PARSE_ERROR_INVALID_MESSAGE).
 * cf GameConstantsGrammar.g4's 'field' rule.
 */
GC_MSG
        :   ('A'..'Z'|'_')+
        ;

/*
 * @author Rowan Crowther
 *
 * Field separator used throughout every directive.
 * cf GameConstantsGrammar.g4's 'line' rule.
 */
COLON   :   ':'
        ;

/*
 * @author Rowan Crowther
 *
 * A (possibly negative) literal integer field.
 * cf GameConstantsGrammar.g4's 'field' rule.
 */
INTEGER
       :   '-'? ('0'..'9')+
       ;
