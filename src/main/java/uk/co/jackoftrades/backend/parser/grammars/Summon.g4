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

// Reader+lexer for lib/gamedata/summon.txt - every summon type (ANY,
// UNIQUE, HI_UNDEAD, ...): message type, whether uniques are allowed,
// which monster bases/race flags restrict the pool, a fallback summon
// type, and a description. Cf. src/mon-summon.c: struct file_parser
// summon_parser (mon-summon.c:215).
//
// No problems found - verified `summon`'s `base` correctly accumulates
// into a list (HOUND and HI_UNDEAD genuinely have 2 and 3 base: lines
// respectively) while `race_flag` correctly stays a single scalar value
// (no summon type has more than one race-flag: line, matching the file's
// own header - "a possible race flag", singular - unlike base's plural
// "possible monster bases").

grammar Summon;

@header {
    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.enums.MessageType;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

    import java.util.List;
    import java.util.ArrayList;
}

// "name:<CODE>" - starts a new summon type record.
name
        returns[String nameStr]
        :   NAME FLAG { $nameStr = $FLAG.getText(); }
        ;

// "msgt:<MSG_TYPE>" - message type/sound for this summon happening.
msgt
        returns[MessageType msgType]
        :   MSGT FLAG { $msgType = MessageType.valueOf("MSG_" + $FLAG.getText().trim()); }
        ;

// "uniques:0|1" - whether unique monsters are eligible for this summon type.
uniques
        returns[boolean uniquesBool]
        :   UNIQUES BOOLEAN { $uniquesBool = $BOOLEAN.getText().equals("1"); }
        ;

// "base:<monster_base.txt name>" - restricts this summon to the given base
// template(s); can repeat (see `summon`'s basesInit list) - if absent, any
// base is eligible.
base
        returns[MonsterBase baseObj]
        :   BASE TEXT { $baseObj = GameConstants.getBaseFromName($TEXT.getText()); }
        ;

// "race-flag:<RF_FLAG>" - restricts this summon to monsters with the given
// race flag - if absent, there's no flag-based restriction.
race_flag
        returns[MonsterRaceFlag raceFlg]
        :   RACE_FLAG FLAG { $raceFlg = MonsterRaceFlag.valueOf("RF_" + $FLAG.getText().trim()); }
        ;

// "fallback:<summon type name>" - another summon type to try if this one
// fails to summon anything.
fallback
        returns[String fallbackStr]
        :   FALLBACK FLAG { $fallbackStr = $FLAG.getText(); }
        ;

// "desc:<text>" - description.
desc
        returns[String descStr]
        :   DESC TEXT { $descStr = $TEXT.getText(); }
        ;

// One full summon type record: name, then any mix of the directives above
// in any order/quantity.
summon
        returns[Summon summonObj]
        @init {
            String nameInit = "";
            MessageType msgTypeInit = MessageType.MSG_NONE;
            boolean uniquesInit = false;
            List<MonsterBase> basesInit = new ArrayList<>();
            MonsterRaceFlag raceFlagInit = MonsterRaceFlag.RF_NONE;
            String fallbackInit = "";
            String descInit = "";
        }
        @after {
            $summonObj = new Summon(nameInit, msgTypeInit, uniquesInit, basesInit,
                                    raceFlagInit, fallbackInit, descInit);
        }
        :   name { nameInit = $name.nameStr; }
          ( msgt { msgTypeInit = $msgt.msgType; }
          | uniques { uniquesInit = $uniques.uniquesBool; }
          | base { basesInit.add($base.baseObj); }
          | race_flag { raceFlagInit = $race_flag.raceFlg; }
          | fallback { fallbackInit = $fallback.fallbackStr; }
          | desc { descInit = $desc.descStr; }
          )+
        ;

// Top-level rule: the whole file is one or more summon type records.
file
        returns[List<Summon> summons]
        @init {
            $summons = new ArrayList<>();
        }
        :   (summon {
            $summons.add($summon.summonObj);
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

// NAME through DESC below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
NAME
        :   'name:'
        ;

MSGT
        :   'msgt:'
        ;

UNIQUES
        :   'uniques:'
        ;

BASE
        :   'base:'
        ;

RACE_FLAG
        :   'race-flag:'
        ;

FALLBACK
        :   'fallback:'
        ;

DESC
        :   'desc:'
        ;

// A single 0/1 digit - used for uniques:.
BOOLEAN
        :   ('0' | '1')
        ;

// An UPPER_CASE_WITH_UNDERSCORES symbolic name - used for name:/msgt:/race-flag:/fallback:.
FLAG
        :   ('A'..'Z' | '_')+
        ;

// Free-running lowercase text with spaces - used for base:/desc:.
TEXT
        :   ('a'..'z' | ' ')+
        ;