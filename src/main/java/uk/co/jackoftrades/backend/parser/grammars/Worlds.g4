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
 * Parser for lib/gamedata/world.txt - the dungeon's linear sequence of
 * levels (Town, Angband 1..127) and which level is up/down from each.
 * Cf. the C parser in src/init.c: struct file_parser world_parser
 * (init.c:1162), registered directive "level int depth sym name sym up sym
 * down" -> parse_world_level (init.c:1083-1107). "None" is the sentinel for
 * "no level in that direction" (the top of Town, the bottom of Angband 127)
 * on both sides - C maps it to NULL (init.c:1098-1099). This grammar extracts
 * every field verbatim as a String ("None" included); mapping "None" to null
 * is the reader's job.
 */
parser grammar Worlds;
options { tokenVocab = WorldsLexer; }

@header {
            import java.util.ArrayList;
            import java.util.List;
        }

/*
 * @author Rowan Crowther
 *
 * The declared number of records in the file
 */
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * The depth field of a "level:" line, e.g. the "0" in "level:0:Town:None:Angband 1"
 */
levelNum
        returns[String num]
        :   INTEGER COLON
            {
                $num = $INTEGER.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * The display name field, e.g. "Town" or "Angband 57"
 */
levelName
        returns[String name]
        :   NAME COLON
            {
                $name = $NAME.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * The trailing "<up>:<down>" pair - the names of the levels directly above
 * and below this one
 */
upAndDown
        returns[String up, String down]
        :   (u=NAME) COLON (d=NAME)
            {
                $up = $u.getText();
                $down = $d.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * One full 'world' "level:depth:name:up:down" record
 */
line    returns[List<String> world]
        :   LEVEL levelNum levelName upAndDown
            {
                $world = new ArrayList<>();
                $world.add($levelNum.num);
                $world.add($levelName.name);
                $world.add($upAndDown.up);
                $world.add($upAndDown.down);
                $world.add(String.valueOf($start.getLine()));
            }
        ;

/*
 * @author Rowan Crowther
 *
 * Top-level rule: the number of declared records from the top
 * of the file, followed by one or more level records, in depth order
 */
file    returns[List<List<String>> levels, String declaredCount]
        @init {
            $levels = new ArrayList<>();
        }
        :
            recordCount { $declaredCount = $recordCount.count; }
            (line
            {
                $levels.add($line.world);
            })+ EOF
        ;