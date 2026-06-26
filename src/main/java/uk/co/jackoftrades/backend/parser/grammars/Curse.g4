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

// Reader+lexer for lib/gamedata/curse.txt - every curse that can be applied
// to an object (Curse of Vulnerability, Curse of Siren Song, ...): which
// object bases it can afflict, weight/combat adjustments, a randomly-
// triggered effect, flags/value-modifiers it imposes, and other curses it
// conflicts with. Cf. src/obj-init.c: struct file_parser curse_parser
// (obj-init.c:1421), directive table at obj-init.c:1335-1344.
//
// `curse`'s `(...)+ ` loop correctly accumulates repeatable directives -
// type: via basesInit.add() (curses can afflict several object bases),
// flags:/conflict-flags: via addAll(), values: via putAll() - verified
// these are genuinely used more than once per curse in real data.
//
// POTENTIAL PROBLEMS (latent - not currently triggered):
//
//   1. `effect` only matches "effect:<TYPE>[:<SUBTYPE>]" (2 segments), but
//      the C parser registers 4: "effect sym eff ?sym type ?int radius
//      ?int other" (obj-init.c:1342) - the same shape as trap.txt/
//      shape.txt's effect: lines - and a separate "effect-yx:" directive
//      (obj-init.c:1343, parse_curse_effect_yx) that this grammar has no
//      token for at all. curse.txt's own header confirms radius/other are
//      meant to be optional trailing fields ("The others are optional...
//      that are unused can be omitted"), but no current curse actually
//      uses them.
//
//   2. `effect`'s subtype switch only special-cases "SUMMON" and
//      "TIMED_INC" - every effect type curse.txt actually uses with a
//      subtype (confirmed: only those two), so this works today, but a
//      future curse using e.g. a STAT/ENCHANT-subtyped effect would have
//      its subtype silently dropped (the switch has no default case).
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar Curse;

@header {
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.ValueEnum;
    import uk.co.jackoftrades.middle.objects.Curse;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

// "name:<text>" - starts a new curse record.
name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

// "type:<object base name>" - an object base this curse can afflict; can
// repeat (see `curse`'s basesInit list) since a curse may apply to several bases.
type
        returns[ObjectBase baseObj]
        :   TYPE STRING { $baseObj = GameConstants.lookupObjectBase($STRING.getText()); }
        ;

// "weight:<value>" - weight adjustment (additive in tenths of a pound, or
// multiplicative /100 if the MULTIPLY_WEIGHT flag is set) - defaults to 0 if absent.
weight
        returns[int weightAdj]
        :   WEIGHT STRING { $weightAdj = Integer.parseInt($STRING.getText()); }
        ;

// "combat:<to-hit>:<to-dam>:<to-ac>" - combat adjustments for the cursed
// object - default to 0 if absent.
combat
        returns[int toHit, int toDam, int toAC]
        :   COMBAT toh=STRING COLON tod=STRING COLON toa=STRING {
                $toHit = Integer.parseInt($toh.getText());
                $toDam = Integer.parseInt($tod.getText());
                $toAC  = Integer.parseInt($toa.getText());
            }
        ;

// "effect:<TYPE>[:<SUBTYPE>]" - the effect that randomly triggers on the
// cursed object. See top-of-file problems #1/#2 re: the missing radius/
// other-param/effect-yx support and the 2-case subtype switch.
effect
        returns[EffectEnum effectEnum, MonsterRaceFlag summonMon, TimedEffect timedEffect]
        @init {
            $summonMon   = MonsterRaceFlag.RF_NONE;
            $timedEffect = TimedEffect.TMD_NONE;
        }
        :   EFFECT eff=STRING {
                $effectEnum = EffectEnum.valueOf("EF_" + $eff.getText());
            } (COLON opt=STRING {
                $summonMon = MonsterRaceFlag.RF_NONE;
                $timedEffect = TimedEffect.TMD_NONE;

                switch ($eff.getText()) {
                    case "SUMMON":
                        $summonMon = MonsterRaceFlag.valueOf("RF_" + $opt.getText());
                        break;

                    case "TIMED_INC":
                        $timedEffect = TimedEffect.valueOf("TMD_" + $opt.getText());
                        break;
                }
            })?
        ;

// "dice:<dice string>" - dice for the preceding effect: line.
dice
        returns[String diceStr]
        :   DICE STRING { $diceStr = $STRING.getText(); }
        ;

// "expr:<letter>:<EFB_BASE>:<operation>" - binds a dice-string variable used
// in the preceding dice: line.
expr
        returns[char exprChar, EffectBaseType evBase, String effectString]
        :   EXPR ch=STRING COLON base=STRING COLON eff=STRING {
                String raw = $ch.getText();
                if (raw.length() != 1)
                    throw new IllegalArgumentException("Expression character code may only have one character.");

                $exprChar = $ch.getText().charAt(0);
                $evBase = EffectBaseType.valueOf("EFB_" + $base.getText());
                $effectString = $eff.getText();
            }
        ;

// "time:<dice string>" - average frequency of the random effect triggering.
time
        returns[String timeStr]
        :   TIME STRING { $timeStr = $STRING.getText(); }
        ;

// "flags:<OF_FLAG> [| <OF_FLAG> ...]" - object flags this curse imposes;
// can repeat per `curse`'s addAll() accumulation.
flags
        returns[List<ObjectFlag> flagList]
        @init { $flagList = new ArrayList<>(); }
        :   FLAGS flg1=STRING {
                $flagList.add(ObjectFlag.valueOf("OF_" + $flg1.getText().trim()));
            } (' | ' flg2=STRING {
                $flagList.add(ObjectFlag.valueOf("OF_" + $flg2.getText().trim()));
            })*
        ;

// "values:<CV_MODIFIER>[<value>] [| ...]" - object-modifier values this
// curse imposes; can repeat per `curse`'s putAll() accumulation.
values
        returns[Map<ValueEnum, Integer> vals]
        @init { $vals = new HashMap<>(); }
        :   VALUES cde1=STRING LBRACKET val1=STRING RBRACKET {
                ValueEnum c1 = ValueEnum.valueOf("CV_" + $cde1.getText().trim());
                int v1 = Integer.parseInt($val1.getText());
                $vals.put(c1, v1);
            } (' | ' cde2=STRING LBRACKET val2=STRING RBRACKET {
                ValueEnum c2 = ValueEnum.valueOf("CV_" + $cde2.getText().trim());
                int v2 = Integer.parseInt($val2.getText());
                $vals.put(c2, v2);
            })*
        ;

// "msg:<text>" - message shown when the random effect triggers.
msg
        returns[String msgStr]
        :   MSG STRING { $msgStr = $STRING.getText(); }
        ;

// "desc:<text>" - flavour description.
desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

// "conflict:<curse name>" - a curse name this one conflicts with (can't
// coexist on the same object).
conflict
        returns[String confStr]
        :   CONFLICT STRING { $confStr = $STRING.getText(); }
        ;

// "conflict-flags:<OF_FLAG> [| <OF_FLAG> ...]" - object flags that conflict
// with this curse; can repeat per `curse`'s addAll() accumulation.
conflict_flags
        returns[List<ObjectFlag> cFlags]
        @init { $cFlags = new ArrayList<>(); }
        :   CONFLICT_FLAGS flg1=STRING {
                $cFlags.add(ObjectFlag.valueOf("OF_" + $flg1.getText().trim()));
            }
        ('|' flg2=STRING {
                $cFlags.add(ObjectFlag.valueOf("OF_" + $flg2.getText().trim()));
            })*
        ;

// One full curse record: name, then any mix of the directives above in any
// order/quantity.
curse
        returns[Curse cur]
        @init {
            String nameInit = "";
            boolean possInit = false;
            List<ObjectBase> basesInit = new ArrayList<>();
            int weightInit = 0;
            int tohInit = 0;
            int todInit = 0;
            int toaInit = 0;
            EffectEnum effectEnumInit = EffectEnum.EF_NONE;
            MonsterRaceFlag summonMonInit = MonsterRaceFlag.RF_NONE;
            TimedEffect timedEffectInit = TimedEffect.TMD_NONE;
            String diceInit = "";
            char exprCharInit = '\0';
            EffectBaseType evBaseInit = EffectBaseType.EFB_NONE;
            String effectStringInit = "";
            String timeInit = "";
            List<ObjectFlag> flagsInit = new ArrayList<>();
            Map<ValueEnum, Integer> valsInit = new HashMap<>();
            String msgInit = "";
            String descInit = "";
            String conflictInit = "";
            List<ObjectFlag> confFlagsInit = new ArrayList<>();
        }
        @after {
            $cur = new Curse(nameInit, possInit, basesInit, weightInit, flagsInit,
                             conflictInit, confFlagsInit, diceInit, timeInit,
                             descInit, effectEnumInit, summonMonInit,
                             timedEffectInit, tohInit, todInit, toaInit,
                             exprCharInit, evBaseInit, effectStringInit, valsInit,
                             msgInit);
        }
        :   name { nameInit = $name.nameStr; }
        (   type { basesInit.add($type.baseObj); }
        |   weight { weightInit = $weight.weightAdj; }
        |   combat {
                tohInit = $combat.toHit;
                todInit = $combat.toDam;
                toaInit = $combat.toAC;
            }
        |   effect {
                effectEnumInit = $effect.effectEnum;
                summonMonInit = $effect.summonMon;
                timedEffectInit = $effect.timedEffect;
            }
        |   dice { diceInit = $dice.diceStr; }
        |   expr {
                exprCharInit = $expr.exprChar;
                evBaseInit = $expr.evBase;
                effectStringInit = $expr.effectString;
            }
        |   time { timeInit = $time.timeStr; }
        |   flags {
                flagsInit.addAll($flags.flagList);
            }
        |   values { valsInit.putAll($values.vals); }
        |   msg { msgInit = $msg.msgStr; }
        |   desc { descInit = $desc.descStr; }
        |   conflict { conflictInit = $conflict.confStr; }
        |   conflict_flags {
                confFlagsInit.addAll($conflict_flags.cFlags);
        })+;

// Top-level rule: the whole file is one or more curse records.
file
        returns[List<Curse> curses]
        @init {
            $curses = new ArrayList<>();
        }
        :   (curse { $curses.add($curse.cur); })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// NAME through CONFLICT_FLAGS below: one literal directive-keyword token
// each, matching the rule of the same purpose above.
NAME
        :   'name:'
        ;

TYPE
        :   'type:'
        ;

WEIGHT
        :   'weight:'
        ;

COMBAT
        :   'combat:'
        ;

EFFECT
        :   'effect:'
        ;

DICE
        :   'dice:'
        ;

EXPR
        :   'expr:'
        ;

TIME
        :   'time:'
        ;

FLAGS
        :   'flags:'
        ;

VALUES
        :   'values:'
        ;

MSG
        :   'msg:'
        ;

DESC
        :   'desc:'
        ;

CONFLICT
        :   'conflict:'
        ;

CONFLICT_FLAGS
        :   'conflict-flags:'
        ;

// Free-running general-purpose text - used for nearly every field's value
// (names, numbers-as-text, flag names, dice strings, messages).
STRING
        :   ('a'..'z' | ' ' | 'A'..'Z' | '!' | '+' | '0'..'9' | '-' | '.' | ',' | '_' | '$')+
        ;

// Field separator within combat:/effect:/expr: lines.
COLON
        :   ':'
        ;

// Brackets around a values: modifier's integer argument, e.g. "[1]".
LBRACKET
        : '['
        ;

RBRACKET
        : ']'
        ;

// POTENTIAL SOLUTIONS
//
//   1. Only worth doing if curse.txt gains an effect: line with a radius/
//      other param, or an effect-yx: line - extend `effect` with the same
//      optional int radius/int other fields TrapGrammar.g4's `effect` has,
//      and add an EFFECT_YX token/rule mirroring parse_curse_effect_yx.
//
//   2. Add the remaining EST_* cases (PROJ/STAT/NOURISH/ENCHANT/
//      EARTHQUAKE/...) to `effect`'s switch, or throw on an unrecognised
//      one, the way Class.g4/PlayerClass.g4's equivalents do, instead of
//      silently leaving both summonMon/timedEffect at their NONE defaults.