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

// Reader+lexer for lib/gamedata/pain.txt - the monster pain-message sets
// shown as a monster takes damage, indexed by a serial "type" number and
// each holding a graduated list of messages from "shrugs it off" to
// "near death". Cf. src/mon-init.c: struct file_parser pain_parser
// (mon-init.c:569).
//
// POTENTIAL PROBLEMS (latent - not currently triggered):
//
//   1. pain.txt's own header says "Each entry must have 7 messages", but
//      `painEntry` matches message: with `+` (one or more, no upper
//      bound) rather than requiring exactly 7. Every current entry does
//      have exactly 7, so this is a missing validation rather than an
//      active bug.
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar Pain;

@header {
    import uk.co.jackoftrades.middle.monsters.MonsterPain;

    import java.util.List;
    import java.util.ArrayList;
}

// "type:<serial number>" - starts a new pain-message set; should increase
// for each new entry (not validated by this grammar).
type
        returns[int typeNum]
        :   TYPE NUMBER {
                $typeNum = Integer.parseInt($NUMBER.getText());
            }
        ;

// "message:<text>" - one graduated pain message; should appear exactly 7
// times per entry (see top-of-file problem #1).
message
        returns[String msgStr]
        :   MESSAGE TEXT {
                $msgStr = $TEXT.getText();
            }
        ;

// One full pain-message set: a type: header followed by its messages.
painEntry
        returns[MonsterPain monPain]
        @init {
            int monTypeNum = 0;
            List<String> messageList = new ArrayList<>();
        }
        @after {
            $monPain = new MonsterPain(monTypeNum, messageList);
        }
        :   type { monTypeNum = $type.typeNum; }
            (msg=message { messageList.add($msg.msgStr); })+
        ;

// Top-level rule: the whole file is one or more pain-message sets.
file
        returns[List<MonsterPain> monsterPain]
        @init {
            $monsterPain = new ArrayList<>();
        }
        : (painEntry {
            $monsterPain.add($painEntry.monPain);
        })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

TYPE
        :   'type:'
        ;

MESSAGE
        :   'message:'
        ;

// A bare non-negative integer.
NUMBER
        :   ('0'..'9')+
        ;

// Free-running message text, including the "[s]"/"[ies|y]" pluralization-
// suffix and '|' alternation syntax pain messages use (e.g. "cr[ies|y] out
// in pain").
TEXT
        :   ('A'..'Z' | 'a'..'z' | '[' | ']' | '.' | ' ' | '|')+
        ;

// POTENTIAL SOLUTIONS
//
//   1. Only worth enforcing if a malformed pain entry is a real risk -
//      change `(msg=message {...})+` to require exactly 7 (e.g. repeat the
//      reference 7 times, or count matches in @after and throw if != 7).