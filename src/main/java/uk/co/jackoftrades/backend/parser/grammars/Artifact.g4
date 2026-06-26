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

// Reader+lexer for lib/gamedata/artifact.txt - every named artifact (the
// Phial of Galadriel, Narthanc, ...): base object kind, graphics, combat/
// armor stats, allocation, flags/modifiers/brands/slays/curses, activation,
// and description. Cf. obj-init.c's artifact_parser (obj-init.c:3112).
//
// No significant problems found. This grammar leans on baking the literal
// directive keyword into several tokens themselves (NAME/BASE_OBJECT/ACT/
// BRAND/SLAY/MSG/DESC all match 'keyword:' plus the value as one token,
// unlike most other grammars here which split them) and recovers the value
// via a hardcoded `.substring(N)` per rule - verified each N against its
// literal prefix length (e.g. "name:" -> substring(5), "base-object:" ->
// substring(12)) and all are correct. Also verified `artifact`'s two-phase
// structure - every non-desc field first, then one-or-more desc: lines
// strictly at the end - against every real record in artifact.txt (the
// only records that looked like they broke this rule had the later lines
// commented out with '#', which doesn't count).

grammar Artifact;

@header {
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.Activation;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.objects.Artifact;
    import uk.co.jackoftrades.middle.objects.Brand;
    import uk.co.jackoftrades.middle.objects.Curse;
    import uk.co.jackoftrades.middle.objects.CurseData;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.Slay;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

// "name:<text>" - starts a new artifact record.
name
        returns [String nameStr]
        :   NAME {
            String raw = $NAME.getText();
            $nameStr = raw.substring(5);
        }
        ;

// "base-object:<tval text>:<sval/kind name>" - the underlying object kind
// this artifact is based on.
baseObject
        returns[TValue tVal, String sVal]
        :   BASE_OBJECT COLON MCASE {
                String raw = $BASE_OBJECT.getText().substring(12);
                raw = raw.replace(' ', '_').replace("armour", "armor").toUpperCase();
                $sVal = $MCASE.getText();
                $tVal = TValue.valueOf("TV_" + raw);
            }
        ;

// "graphics:<symbol>:<colour>" - display character (defaults to the base
// kind's if absent).
graphics
        returns[AngbandDisplayCharacter adc]
        :   GRAPHICS {
                String raw = $GRAPHICS.getText();
                $adc = new AngbandDisplayCharacter(raw.charAt(9), raw.charAt(11));
            }
        ;

// "level:<value>" - native depth.
level
        returns[int lev]
        :   LEVEL INTEGER { $lev = Integer.parseInt($INTEGER.getText()); }
        ;

// "weight:<tenths of a pound>".
weight
        returns[int wgt]
        :   WEIGHT INTEGER { $wgt = Integer.parseInt($INTEGER.getText()); }
        ;

// "cost:<value>" - base shop price.
cost
        returns[int cst]
        :   COST INTEGER { $cst = Integer.parseInt($INTEGER.getText()); }
        ;

// "alloc:<rarity>:<min level> to <max level>".
alloc
        returns[int prob, int minRange, int maxRange]
        :   ALLOC p=INTEGER COLON min=INTEGER RANGE_TO max=INTEGER {
                $prob = Integer.parseInt($p.getText());
                $minRange = Integer.parseInt($min.getText());
                $maxRange = Integer.parseInt($max.getText());
            }
        ;

// "attack:<dice>:<to-hit>:<to-dam>" - melee weapon stats.
attack
        returns[String damage, int toH, int toD]
        :   ATTACK DICE_STRING COLON hit=INTEGER COLON dam=INTEGER {
                $damage = $DICE_STRING.getText();
                $toH = Integer.parseInt($hit.getText());
                $toD = Integer.parseInt($dam.getText());
            }
        ;

// "armor:<base AC>:<to-AC>".
armour
        returns[int baseAC, int toA]
        :   ARMOUR base=INTEGER COLON plus=INTEGER {
                $baseAC = Integer.parseInt($base.getText());
                $toA = Integer.parseInt($plus.getText());
            }
        ;

// "flags:<OF_FLAG> [| <OF_FLAG> ...]" - object flags this artifact has.
flags
        returns[Flag<ObjectFlag> flag]
        @init {
            $flag = new Flag<>(ObjectFlag.class);
        }
        :   FLAGS f1=MCASE {
                String flag1 = "OF_" + $f1.getText().trim();
                $flag.on(ObjectFlag.valueOf(flag1));
            } (OR f2=MCASE {
                String flag2 = "OF_" + $f2.getText().trim();
                $flag.on(ObjectFlag.valueOf(flag2));
            })* OR?
        ;

// "values:<OM_MODIFIER>[<value>] [| ...]" - object modifiers this artifact has.
values
        returns[Map <ObjectModifier, Integer> skills]
        @init {
            $skills = new HashMap<>();
        }
        :   VALUES ps1=MCASE LBRACKET v1=INTEGER RBRACKET {
                String raw1 = $ps1.getText().trim();
                int val1 = Integer.parseInt($v1.getText());
                $skills.put(ObjectModifier.valueOf("OM_" + raw1), val1);
            } (OR ps2=MCASE LBRACKET v2=INTEGER RBRACKET {
                String raw2 = $ps2.getText().trim();
                int val2 = Integer.parseInt($v2.getText());
                $skills.put(ObjectModifier.valueOf("OM_" + raw2), val2);
            })* OR?
        ;

// "act:<CODE>" - the activation this artifact grants, looked up in activation.txt.
act
        returns[Activation activation]
        :   ACT {
                String name = $ACT.getText().substring(4);
                $activation = GameConstants.lookupActivation(name);
            }
        ;

// "time:<dice or integer>" - recharge time for the activation.
time
        returns[String dice]
        :   TIME ((DICE_STRING {
                $dice = $DICE_STRING.getText();
            })  | (INTEGER {
                $dice = $INTEGER.getText();
            }))
        ;

// "desc:<text>" - flavour description; can repeat (and must be last - see
// `artifact`'s two-phase structure).
desc    returns[String description]
        :   DESC { $description = $DESC.getText().substring(5); }
        ;

// "brand:<CODE>" - a brand this artifact grants, looked up in brand.txt;
// can repeat (see `artifact`'s brandInit list).
brand
        returns[Brand b]
        :   BRAND {
                String raw = $BRAND.getText();
                $b = GameConstants.lookupBrandCode(raw.substring(6));
            }
        ;

// "slay:<CODE>" - a slay this artifact grants, looked up in slay.txt; can
// repeat (see `artifact`'s slayInit list).
slay
        returns[Slay s]
        :   SLAY {
                String raw = $SLAY.getText();
                $s = GameConstants.lookupSlay(raw.substring(5));
            }
        ;

// "curse:<curse name>:<power>" - a curse this artifact comes with; can
// repeat (see `artifact`'s curseInit list).
curse
        returns[Map<Curse, CurseData> curses]
        @init {
            $curses = new HashMap<>();
        }
        :   CURSE MCASE COLON INTEGER {
                String raw = $MCASE.getText();
                int val = Integer.parseInt($INTEGER.getText());
                Curse c = GameConstants.lookupCurse(raw);
                CurseData cd = new CurseData(val, -1);
                $curses.put(c, cd);
            }
        ;

// "msg:<text>" - activation message; can repeat to build up multiple lines.
msg
        returns[String message]
        :   MSG { $message = $MSG.getText().substring(4); }
        ;

// One full artifact record: name, then any mix of base-object/graphics/
// level/weight/cost/alloc/attack/armor/flags/brand/curse/slay/values/act/
// time/msg in any order, followed by one-or-more desc: lines at the end.
artifact
        returns[Artifact art]
        @init {
            String nameInit = "";
            TValue tValInit = TValue.TV_NONE;
            String sValInit = "";
            AngbandDisplayCharacter adcInit = null;
            int levelInit = 0;
            int weightInit = 0;
            int costInit = 0;
            int probInit = 0;
            int allocMin = 0;
            int allocMax = 0;
            String damageInit = "";
            int toHInit = 0;
            int toDInit = 0;
            int baseACInit = 0;
            int toAInit = 0;
            Flag<ObjectFlag> flagsInit = new Flag<>(ObjectFlag.class);
            Map<ObjectModifier, Integer> modifierInit = new HashMap<>();
            Activation actInit = null;
            String timeInit = "";
            String descInit = "";
            List<Brand> brandInit = new ArrayList<>();
            List<Slay> slayInit = new ArrayList<>();
            List<Map<Curse, CurseData>> curseInit = new ArrayList<>();
            String messageInit = "";

            ObjectKind kind = null;
        }
        @after {
            kind = GameConstants.lookupObjectKind(tValInit, nameInit);

            if (kind == null) {
                kind = new ObjectKind(adcInit, costInit, levelInit, allocMin, allocMax,
                                      nameInit, tValInit, nameInit, null, true);
            }

            $art = new Artifact(nameInit, descInit, 0, tValInit, sValInit, toHInit,
                                toDInit, toAInit, baseACInit, damageInit,
                                weightInit, costInit, flagsInit, modifierInit,
                                brandInit, slayInit, curseInit, levelInit,
                                probInit, allocMin, allocMax, actInit,
                                messageInit, timeInit);
        }
        :   name {
                nameInit = $name.nameStr;
            }
        (   baseObject {
                tValInit = $baseObject.tVal;
                sValInit = $baseObject.sVal;
            }
        |   graphics {
                adcInit = $graphics.adc;
            }
        |   level {
                levelInit = $level.lev;
            }
        |   weight {
                weightInit =$weight.wgt;
            }
        |   cost {
                costInit = $cost.cst;
            }
        |   alloc {
                probInit = $alloc.prob;
                allocMin = $alloc.minRange;
                allocMax = $alloc.maxRange;
            }
        |   attack {
                damageInit = $attack.damage;
                toHInit = $attack.toH;
                toDInit = $attack.toD;
            }
        |   armour {
                baseACInit = $armour.baseAC;
                toAInit = $armour.toA;
            }
        |   flags {
                flagsInit.union($flags.flag);
            }
        |   brand {
                brandInit.add($brand.b);
            }
        |   curse {
                curseInit.add($curse.curses);
            }
        |   slay {
                slayInit.add($slay.s);
            }
        |   values {
                for (ObjectModifier mod : $values.skills.keySet())
                    modifierInit.put(mod, $values.skills.get(mod));
            }
        |   act {
                actInit = $act.activation;
            }
        |   time {
                timeInit = $time.dice;
            }
        |   msg {
                messageInit = messageInit + $msg.message;
            }
        )+
            (desc {
                descInit = descInit + $desc.description;
            }
        )+
        ;

// Top-level rule: the whole file is one or more artifact records.
file
        returns[List<Artifact> artifacts]
        @init {
            $artifacts = new ArrayList<>();
        }
        :   (artifact {
                $artifacts.add($artifact.art);
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

// Free-running display/description text, including accented characters
// used in some artifact/flavour names (e.g. "Thráin", "Eärendil").
fragment MixedCase
        :   ('a'..'z' | 'A'..'Z' | 'ä' | ' ' | '\'' | '.' | ',' | 'á' | '_' | 'â' | '"'
            | '-' | 'ë' | 'û' | ';'| 'ú' | 'ö' | 'É' | '?' | 'ó' | '!' | 'é' | 'í')+
        ;

fragment LowerCase
        :   ('a'..'z')+
        ;

// "name:" plus the artifact's display name, as one token - see `name`'s
// substring(5).
NAME
        :   'name:' MixedCase
        ;

// "base-object:" plus the tval text, as one token - see `baseObject`'s substring(12).
BASE_OBJECT
        :   'base-object:' (LowerCase | ' ')+
        ;

// A whole "graphics:<symbol>:<colour>" value as one token, fixed-width so
// `graphics`'s action can index into it by character position.
GRAPHICS
        :   'graphics:' ('~' | '=' | '"') COLON ('y' | 'd' | 'g' | 'w')
        ;

// LEVEL through CURSE below: literal directive-keyword tokens matching the
// rule of the same purpose above (most as a bare keyword, ACT/BRAND/SLAY/
// MSG/DESC fold the value's charset into the token itself too).
LEVEL
        :   'level:'
        ;

WEIGHT
        :   'weight:'
        ;

COST
        :   'cost:'
        ;

ALLOC
        :   'alloc:'
        ;

ATTACK
        :   'attack:'
        ;

ARMOUR
        :   'armor:'
        ;

FLAGS
        :   'flags:'
        ;

ACT
        :   'act:' ('A'..'Z' | '_')+ ('0'..'9')*
        ;

TIME
        :   'time:'
        ;

MSG
        :   'msg:' ('{' | '}' | '.' | 'a'..'z' | 'A'..'Z' | ' ')+
        ;

VALUES
        :   'values:'
        ;

BRAND
        :   'brand:' ('A'..'Z')+ '_' ('0'..'9')+
        ;

SLAY
        :   'slay:' ('A'..'Z')+ '_' ('0'..'9')+
        ;

CURSE
        :   'curse:'
        ;

DESC
        :   'desc:' MixedCase
        ;

// The " to " separator in alloc:'s level-range field.
RANGE_TO
        :   ' to '
        ;

// A dice expression in the "NdM"/"N+dM"/"N+NdM" shapes used by attack:/time:.
DICE_STRING
        :   INTEGER 'd' INTEGER
        |   INTEGER '+d' INTEGER
        |   INTEGER '+' INTEGER 'd' INTEGER
        ;

// Mixed-case text - used for base-object:'s sval and values:/curse:'s name fields.
MCASE
        :   MixedCase
        ;

// A (possibly negative) literal integer.
INTEGER
        :   MINUS? ('0'..'9')+
        ;

// Field separator within base-object:/alloc:/attack:/armor:/curse: lines.
COLON
        :   ':'
        ;

// Brackets around a values: modifier's integer argument, e.g. "[4]".
LBRACKET
        :   '['
        ;

RBRACKET
        :   ']'
        |   '] '
        ;

// The '|' separator between entries on a flags:/values: line.
OR
        :   '|'
        ;

PLUS
        :   '+'
        ;

MINUS
        :   '-'
        ;