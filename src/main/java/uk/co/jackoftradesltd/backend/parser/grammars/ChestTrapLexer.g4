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
 * Lexer for lib/gamedata/chest_trap.txt - the traps a chest can carry
 * (the "no trap" locked entry, gas traps, poison needles, summoning
 * runes and the explosion device): name, code, minimum chest level, one
 * or more effect blocks, and the activation/death messages. Cf.
 * src/obj-chest.c: struct file_parser chest_trap_parser (obj-chest.c:283),
 * directive table at obj-chest.c:245-254 (parse_chest_trap_name/_code/
 * _level/_effect/_dice/_expr/_destroy/_magic/_msg/_msg_death).
 *
 * Two notes on where this file departs from the C original.
 *
 * The C parser registers no "record-count" directive - chest_trap.txt is
 * read by parse_file_quit_not_found (obj-chest.c:259) with no declared
 * total - so RECORD_COUNT below is an addition of this port, matching the
 * convention the other gamedata files follow here.
 *
 * The C struct's pval field (object.h:73) has no directive either: it is
 * synthesised while parsing, one bit per record in file order
 * ("t->pval = h->pval * 2", obj-chest.c:64-72). Nothing in this grammar
 * can see it; ChestTrapCode carries it instead, as 1 << ordinal().
 */
lexer grammar ChestTrapLexer;

import CommentsAndEol, EffectBlockLexer;

/*
 * @author Rowan Crowther
 *
 * record-count directive
 * cf ChestTrapGrammar.g4's 'recordCount' rule
 */
RECORD_COUNT
        :   'record-count:'
        ;

/*
 * @author Rowan Crowther
 *
 * "name:<text>" - the trap's display name, and the directive that starts a
 * new record. Not unique: "gas trap" and "poison needle" each name two
 * different traps, so it is the code below - never the name - that
 * identifies one.
 *
 * Pushes REST_OF_LINE so the value is taken whole, spaces included.
 * cf ChestTrapGrammar.g4's 'name' rule
 */
NAME
        :   'name:' -> pushMode(REST_OF_LINE)
        ;

/*
 * @author Rowan Crowther
 *
 * "code:<CODE>" - the trap's stable identifier, and this port's key for it.
 *
 * The C game stores this string (obj-chest.c:85) but never reads it back:
 * a trap is identified there only by its positional pval bit. This port
 * resolves it to a ChestTrapCode instead, which is what carries the bit, so
 * the code is load-bearing here in a way it is not in C. The data file's
 * comment pointing at a "chest.txt" describes a file that 4.2.6 does not
 * ship.
 *
 * Pushes REST_OF_LINE rather than lexing an upper-case symbol, so an
 * unknown or malformed code reaches the assembler as text and is reported
 * there rather than failing as a token here.
 * cf ChestTrapGrammar.g4's 'code' rule
 */
CODE
        :   'code:' -> pushMode(REST_OF_LINE)
        ;

/*
 * @author Rowan Crowther
 *
 * "level:<integer>" - the minimum object level of chest this trap can
 * appear on. pick_one_chest_trap (obj-chest.c:359-375) counts only the
 * traps whose level is at or below the chest's, so this is the sole thing
 * gating which traps a given chest can draw.
 *
 * The integer is the INTEGER token inherited from EffectBlockLexer.
 * cf ChestTrapGrammar.g4's 'level' rule
 */
LEVEL
        :   'level:'
        ;

/*
 * @author Rowan Crowther
 *
 * "destroy:<integer>" - 1 if springing the trap destroys the chest's
 * contents. C stores it as a bool, treating any non-zero value as true
 * (obj-chest.c:parse_chest_trap_destroy); the data file documents only 1.
 * cf ChestTrapGrammar.g4's 'destroy' rule
 */
DESTROY
        :   'destroy:'
        ;

/*
 * @author Rowan Crowther
 *
 * "magic:<integer>" - 1 if the trap is magical rather than physical.
 * Same integer-to-bool treatment as destroy: above.
 * cf ChestTrapGrammar.g4's 'magic' rule
 */
MAGIC
        :   'magic:'
        ;

/*
 * @author Rowan Crowther
 *
 * "msg:<free text>" - shown when the trap is triggered.
 *
 * Note that C appends rather than replaces on a repeated msg: line
 * (string_append, obj-chest.c:parse_chest_trap_msg); no current data file
 * has one.
 *
 * Pushes REST_OF_LINE so punctuation and spaces are taken verbatim.
 * cf ChestTrapGrammar.g4's 'msg' rule
 */
MSG
        :   'msg:' -> pushMode(REST_OF_LINE)
        ;

/*
 * @author Rowan Crowther
 *
 * "msg-death:<free text>" - shown if the trap kills the character; the
 * phrase completing "killed by ...". Appends in C, as msg: does.
 *
 * Declared after MSG so the longer literal wins: 'msg-death:' is also a
 * prefix match for the inherited UCASE token, which admits '-'.
 * cf ChestTrapGrammar.g4's 'msgDeath' rule
 */
MSG_DEATH
        :   'msg-death:' -> pushMode(REST_OF_LINE)
        ;

/*
 * @author Rowan Crowther
 *
 * Entered after the four free-text directives (name:, code:, msg: and
 * msg-death:) to take the remainder of the line as one token, rather than
 * splitting it on the spaces and punctuation the messages contain.
 */
mode REST_OF_LINE;

/*
 * @author Rowan Crowther
 *
 * Everything up to, but not including, the end of the line.
 */
STRING
        :   ~('\n' | '\r')+
        ;

/*
 * @author Rowan Crowther
 *
 * End of a free-text line: pops back to the default mode and skips the
 * line ending. This is what closes the mode, so a directive with an empty
 * value pops here without producing a STRING.
 */
ROL_EOL
        :   '\r'* '\n' -> skip, popMode
        ;
