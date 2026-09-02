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
 * Lexer for lib/gamedata/dungeon_profile.txt - the recipes describing how each
 * style of level (and the town) is generated: how densely rooms may be packed,
 * how tunnels wander, how many mineral streamers are drawn, and which rooms
 * the style may contain.
 *
 * Cf. the C parser in src/generate.c: struct file_parser profile_parser
 * (generate.c:306), whose directives are registered in init_parse_profile
 * (generate.c:206-217).
 *
 * The file is two-level: a "name:" line opens a profile record, and any
 * "room:" lines that follow belong to it until the next "name:". Every
 * directive other than "name:" is optional, and they may appear in any order -
 * the town profile, for instance, carries "streamer:" before "params:" and has
 * no tunnel or room lines at all.
 *
 * Two value shapes need their own lexer mode so their characters are not
 * mis-tokenised:
 *   * REST_OF_LINE    - the profile name, taken verbatim to end of line.
 *   * DELIMITED_TEXT  - a room name, which runs to the next ':' and may hold
 *                       spaces, capitals and parentheses ("Greater vault
 *                       (new)"), so it cannot be lexed as a bare word.
 * Comment and blank-line skipping comes from the imported CommentsAndEol.
 */
lexer grammar DungeonProfileLexer;

import CommentsAndEol;

/*
 * @author Rowan Crowther
 *
 * The number of records in this file, declared by the file itself. A port-ism
 * with no counterpart in C, checked against the number of records actually
 * parsed.
 * cf DungeonProfileGrammar.g4's 'recordCount' rule.
 */
RECORD_COUNT
        :   'record-count:'
        ;

/*
 * @author Rowan Crowther
 *
 * Opens a profile record. C reads the value with parser_getstr, i.e. the whole
 * rest of the line, so the name may contain spaces ("hard centre") - hence the
 * switch into REST_OF_LINE.
 *
 * The name is not free-form: parse_profile_name (generate.c:93) matches it
 * against the cave_builders[] table and fails the parse if no builder has that
 * name. That check belongs to the assembler, not here.
 * cf DungeonProfileGrammar.g4's 'name' rule.
 */
NAME
        :   'name:' -> pushMode(REST_OF_LINE)
        ;

/*
 * @author Rowan Crowther
 *
 * "params:<block_size>:<rooms>:<unusual>:<rarity>" - the four numbers
 * governing how the level is laid out: the size of the blocks rooms are
 * allocated in, how many rooms to aim for, how strongly rarity is penalised,
 * and the highest room rarity this profile allows.
 * cf DungeonProfileGrammar.g4's 'params' rule.
 */
PARAMS
        :   'params:'
        ;

/*
 * @author Rowan Crowther
 *
 * "tunnel:<rnd>:<chg>:<con>:<pen>:<jct>" - five percentage chances steering
 * how corridors are dug.
 * cf DungeonProfileGrammar.g4's 'tunnel' rule.
 */
TUNNEL
        :   'tunnel:'
        ;

/*
 * @author Rowan Crowther
 *
 * "streamer:<den>:<rng>:<mag>:<mc>:<qua>:<qc>" - how the magma and quartz
 * veins running through the rock are drawn, and how often they hold treasure.
 * cf DungeonProfileGrammar.g4's 'streamer' rule.
 */
STREAMER
        :   'streamer:'
        ;

/*
 * @author Rowan Crowther
 *
 * "room:<name>:<rating>:<height>:<width>:<level>:<pit>:<rarity>:<cutoff>" -
 * one room this profile may contain. Repeats, and the repeats are ordered:
 * the cutoff scan walks them in file order, so the same room name may appear
 * more than once with different ratings.
 *
 * C reads the name with parser_getsym, which stops at the next ':' but allows
 * spaces within, so the value switches into DELIMITED_TEXT rather than being
 * lexed as a word.
 * cf DungeonProfileGrammar.g4's 'room' rule.
 */
ROOM
        :   'room:' -> pushMode(DELIMITED_TEXT)
        ;

/*
 * @author Rowan Crowther
 *
 * "min-level:<n>" - the shallowest depth at which this profile may be chosen.
 * Absent on the profiles usable at any depth.
 * cf DungeonProfileGrammar.g4's 'minLevel' rule.
 */
MIN_LEVEL
        :   'min-level:'
        ;

/*
 * @author Rowan Crowther
 *
 * "alloc:<n>" - the profile's selection weight against the other profiles legal
 * at that depth. Zero or less than -1 disables the profile; -1 means it can
 * only be reached by the hard-coded tests in generate.c, which is why the
 * value may be negative.
 * cf DungeonProfileGrammar.g4's 'alloc' rule.
 */
ALLOC
        :   'alloc:'
        ;

/*
 * @author Rowan Crowther
 *
 * A signed integer. Every numeric field in the file is one; the sign is needed
 * only by "alloc:", which carries -1 for town, labyrinth and moria.
 */
INTEGER
        :   '-'? ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * The ':' separating the numeric fields of a params/tunnel/streamer/room line.
 * Distinct from the ':' that ends a directive keyword, which each keyword
 * token consumes for itself.
 */
COLON
        :   ':'
        ;

/*
 * @author Rowan Crowther
 *
 * Mode for a room name: free text up to the ':' that ends it. Entered from
 * ROOM and left again one token later, so it spans only the name.
 */
mode DELIMITED_TEXT;

/*
 * @author Rowan Crowther
 *
 * The room name itself. Line ends are excluded as well as ':', so a malformed
 * room line missing its trailing ':' cannot swallow the rest of the file into
 * a single token; DELIMITER below recovers from that case.
 * cf DungeonProfileGrammar.g4's 'room' rule.
 */
TEXT_BETWEEN_COLON
        :   ~(':' | '\r' | '\n')+
        ;

/*
 * @author Rowan Crowther
 *
 * Ends the room name and returns to the default mode. The ':' is the normal
 * exit. A line end is the error-recovery exit - unreachable on well-formed
 * data, where a room name is always followed by ':' - and is skipped so that
 * one malformed line cannot leave every following line lexing in this mode.
 */
DELIMITER
        :   (':' | '\n' | '\r') -> skip, popMode
        ;

/*
 * @author Rowan Crowther
 *
 * Mode for a value taken verbatim to end of line - here only the profile name.
 */
mode REST_OF_LINE;

/*
 * @author Rowan Crowther
 *
 * The profile name, as-is, spaces included ("hard centre").
 * cf DungeonProfileGrammar.g4's 'name' rule.
 */
STRING
        :   ~('\n' | '\r')+
        ;

/*
 * @author Rowan Crowther
 *
 * Ends the name line: drop the line end and return to the default mode.
 */
END_OF_MODE
        :   '\r'* '\n' -> skip, popMode
        ;
