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

// Reader+lexer for lib/gamedata/slay.txt - every "slay" an object can carry
// (Slay Evil, Slay Animal, Slay Orc, ...): which monsters it affects (by
// race flag or monster base), damage multipliers, power weighting, and
// combat verbs. Cf. src/obj-init.c: struct file_parser slay_parser
// (obj-init.c:851).
//
// POTENTIAL PROBLEMS:
//
//   1. slay.txt's own header documents race-flag: and base: as mutually
//      exclusive and exactly one of them required per slay ("can not be
//      used with another race-flag directive or a base directive for the
//      same slay"), but `slay` marks both independently optional
//      (`(race_flag {...})? (base {...})?`) with no check that exactly one
//      was matched. Every current slay.txt entry does have exactly one, so
//      this isn't an active bug, but a future entry with both (or neither)
//      would silently parse instead of being rejected.
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar Slay;

@header {
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.objects.Slay;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;

    import java.util.List;
    import java.util.ArrayList;
}

// "code:<CODE>" - starts a new slay record; this code is what artifact.txt/
// ego_item.txt/object.txt/player_timed.txt reference to grant the slay.
code
        returns[String codeStr]
        :   CODE FLAG { $codeStr = $FLAG.getText(); }
        ;

// "name:<text>" - name of the affected creature type, for object descriptions.
name
        returns[String nameStr]
        :   NAME TEXT { $nameStr = $TEXT.getText(); }
        ;

// "race-flag:<RF_FLAG>" - the monster race flag this slay affects; mutually
// exclusive with `base` (see top-of-file problem #1).
race_flag
        returns[MonsterRaceFlag monRaceFlag]
        :   RACE_FLAG FLAG { $monRaceFlag = MonsterRaceFlag.valueOf("RF_" + $FLAG.getText().trim()); }
        ;

// "base:<monster base name>" - the monster_base.txt base this slay affects;
// mutually exclusive with `race_flag` (see top-of-file problem #1).
base
        returns[MonsterBase monBase]
        :   BASE BASE_FLAG { $monBase = GameConstants.lookupMonsterBase($BASE_FLAG.getText()); }
        ;

// "multiplier:<value>" - damage-dice multiplier (normal combat).
multiplier
        returns[int multNum]
        :   MULTIPLIER NUMBER { $multNum = Integer.parseInt($NUMBER.getText()); }
        ;

// "o-multiplier:<value>" - damage-dice multiplier (O-combat variant).
o_multiplier
        returns[int oMultNum]
        :   O_MULTIPLIER NUMBER { $oMultNum = Integer.parseInt($NUMBER.getText()); }
        ;

// "power:<value>" - weighting in object power calculations (100 = neutral).
power
        returns[int powerNum]
        :   POWER NUMBER { $powerNum = Integer.parseInt($NUMBER.getText()); }
        ;

// "melee-verb:<text>" - verb used when hitting a susceptible monster in melee.
melee_verb
        returns[String meleeVerbStr]
        :   MELEE_VERB TEXT { $meleeVerbStr = $TEXT.getText(); }
        ;

// "range-verb:<text>" - verb used when hitting a susceptible monster at range.
ranged_verb
        returns[String rangedVerbStr]
        :   RANGE_VERB TEXT { $rangedVerbStr = $TEXT.getText(); }
        ;

// One full slay record: code/name header, exactly one of race-flag/base
// (in principle - see problem #1), then the mandatory multiplier/
// o-multiplier/power/melee-verb/range-verb fields, in that fixed order
// (verified to match every entry in slay.txt).
slay
        returns[Slay slayObj]
        @init {
            String codeInit = "";
            String nameInit = "";
            MonsterRaceFlag raceInit = MonsterRaceFlag.RF_NONE;
            MonsterBase baseInit = null;
            int multInit = 0;
            int oMultInit = 0;
            int powerInit = 0;
            String meleeInit = "";
            String rangedInit = "";
        }
        @after {
            $slayObj = new Slay(codeInit, nameInit, baseInit, meleeInit,
                                rangedInit, raceInit, multInit, oMultInit,
                                powerInit);
        }
        :   code { codeInit = $code.codeStr; }
            name { nameInit = $name.nameStr; }
            ((race_flag { raceInit = $race_flag.monRaceFlag; }) |
            (base { baseInit = $base.monBase; }))?
            multiplier { multInit = $multiplier.multNum; }
            o_multiplier { oMultInit = $o_multiplier.oMultNum; }
            power { powerInit = $power.powerNum; }
            melee_verb { meleeInit = $melee_verb.meleeVerbStr; }
            ranged_verb { rangedInit = $ranged_verb.rangedVerbStr; }
        ;

// Top-level rule: the whole file is one or more slay records.
file
        returns[List<Slay> slays]
        @init {
            $slays = new ArrayList<>();
        }
        :   (slay {
            $slays.add($slay.slayObj);
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

// CODE through RANGE_VERB below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
CODE
        :   'code:'
        ;

NAME
        :   'name:'
        ;

RACE_FLAG
        :   'race-flag:'
        ;

BASE
        :   'base:'
        ;

MULTIPLIER
        :   'multiplier:'
        ;

O_MULTIPLIER
        :   'o-multiplier:'
        ;

POWER
        :   'power:'
        ;

MELEE_VERB
        :   'melee-verb:'
        ;

RANGE_VERB
        :   'range-verb:'
        ;

// A bare non-negative integer.
NUMBER
        :   ('0'..'9')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES_OR_DIGITS symbolic name - used for code:/race-flag:.
FLAG
        :   ('A'..'Z' | '0'..'9' | '_')+
        ;

// Free-running lowercase text with spaces - used for name:/melee-verb:/range-verb:.
TEXT
        :   ('a'..'z' | ' ')+
        ;

// Mixed-case text with spaces - used for base:'s monster_base.txt name lookup.
BASE_FLAG
        :   ('A'..'Z' | 'a'..'z' | ' ')+
        ;

// POTENTIAL SOLUTIONS
//
//   1. Only worth adding if slay.txt risks gaining a malformed entry - have
//      `slay` check (in @after, or via a semantic predicate) that exactly
//      one of race_flag/base matched, throwing otherwise.