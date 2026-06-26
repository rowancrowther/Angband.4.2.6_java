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

// Reader+lexer for lib/gamedata/world.txt - the dungeon's linear sequence of
// levels (Town, Angband 1..127) and which level is "up"/"down" from each.
// Cf. the C parser in src/init.c: struct file_parser world_parser
// (init.c:1162), registered directive "level int depth sym name sym up sym
// down" -> parse_world_level (init.c:1083-1107). "None" is the sentinel for
// "no level in that direction" (the top of Town, the bottom of Angband 127)
// on both sides - C maps it to NULL (init.c:1098-1099), this grammar maps
// it to null in `upAndDown` below.
//
// POTENTIAL PROBLEMS:
//
//   1. Not a functional bug (the final ParsedWorld.levelUp/levelDown values
//      come out correctly - verified against parse_world_level's field
//      order), but `upAndDown`'s local variable names are swapped from
//      their meaning: `down=NAME` actually captures the *up* field (3rd
//      column) and `up=NAME` actually captures the *down* field (4th
//      column). The renaming to $previous/$next and then to
//      ParsedWorld's levelUp/levelDown happens to compensate, but anyone
//      reading `upAndDown` in isolation will read it backwards, and a
//      "cleanup" that aligns the names with their literal text without
//      re-deriving the column order would silently introduce a real bug.
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar World;

@header {
            import java.util.ArrayList;
            import java.util.List;
        }

// One parsed level record: depth, display name, and the (nullable) names of
// the levels above/below it.
@members
        {
            public record ParsedWorld(int level, String levelName, String levelUp, String levelDown) {}
        }

// The depth field of a "level:" line, e.g. the "0" in "level:0:Town:None:Angband 1".
levelNum
        returns[int num]
        :   NUMBER COLON
            {
                $num = Integer.parseInt($NUMBER.getText());
            }
        ;

// The display-name field, e.g. "Town" or "Angband 1".
levelName
        returns[String name]
        :   NAME COLON
            {
                $name = $NAME.getText();
            }
        ;

// The trailing "<up>:<down>" pair - the names of the levels directly above
// and below this one, with the "None" sentinel mapped to null. See problem
// #1 above re: the misleading local variable names.
upAndDown
        returns[String previous, String next]
        :   (down=NAME) COLON (up=NAME)
            {
                $previous = $down.getText();
                if ($previous.equals("None")) $previous = null;

                $next = $up.getText();
                if ($next.equals("None")) $next = null;
            }
        ;

// One full "level:depth:name:up:down" record.
line    returns[ParsedWorld world]
        :   LEVEL levelNum levelName upAndDown
            {
                $world = new ParsedWorld($levelNum.num, $levelName.name, $upAndDown.previous, $upAndDown.next);
            }
        ;

// Top-level rule: the whole file is one or more level records, in depth order.
file    returns[List<ParsedWorld> levels]
        @init {
            $levels = new ArrayList<>();
        }
        :   (line
            {
                $levels.add($line.world);
            })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL     :   '\r'? '\n' -> skip
        ;

// Literal directive keyword introducing every record.
LEVEL   :   'level:'
        ;

// Field separator used throughout a "level:" line.
COLON   :   ':'
        ;

// A bare non-negative integer - the depth field.
NUMBER  :   ('0'..'9')+
        ;

// Free-running letters/digits/spaces - used for the name/up/down fields
// (e.g. "Angband 1", "Town", "None").
NAME    :   ('A'..'Z'|'a'..'z'|' '|'0'..'9')+
        ;

// POTENTIAL SOLUTIONS
//
//   1. Rename `upAndDown`'s captures to match the columns they actually
//      hold - e.g. `(up=NAME) COLON (down=NAME)` - and update the body to
//      `$previous = $up.getText(); ... $next = $down.getText();` so the
//      rule reads correctly on its own, with no change in behaviour.