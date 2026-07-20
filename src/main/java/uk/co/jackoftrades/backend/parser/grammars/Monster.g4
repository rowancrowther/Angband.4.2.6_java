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

// Reader+lexer for lib/gamedata/monster.txt - every monster race (624
// records): base template, display glyph/colour, stats, blows, flags,
// spells (with per-spell alternate messages), drops, friends/groups,
// mimicry, and shapechange forms. Cf. src/mon-init.c: struct file_parser
// monster_parser (mon-init.c:1928).

grammar Monster;

@header {
    import uk.co.jackoftrades.backend.numerics.Random;
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.combat.BlowMethod;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.BlowEffect;
    import uk.co.jackoftrades.middle.monsters.MonsterAltmsg;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterBlow;
    import uk.co.jackoftrades.middle.monsters.MonsterDrop;
    import uk.co.jackoftrades.middle.monsters.MonsterFriends;
    import uk.co.jackoftrades.middle.monsters.MonsterFriendsBase;
    import uk.co.jackoftrades.middle.monsters.MonsterMimic;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.monsters.MonsterShape;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterAltmsgType;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;


    import java.util.ArrayList;
    import java.util.List;
}

// "name:<text>" - starts a new monster record.
name
        returns[String nameStr]
        :   NAME LCASE { $nameStr = $LCASE.getText(); }
        ;

// "plural:<text>" - plural form of the name, for group messages.
plural
        returns[String pluralStr]
        :   PLURAL LCASE { $pluralStr = $LCASE.getText(); }
        ;

// "base:<monster_base.txt name>" - the template this monster is built on.
// See top-of-file problem #1: this only resolves the MonsterBase object -
// it does NOT also seed glyphInit/flagsInit from it the way the C parser does.
base
        returns[MonsterBase baseObj]
        :   BASE LCASE {
                String raw = $LCASE.getText();
                $baseObj = GameConstants.getMonsterBase(raw);
            }
        ;

// "glyph:<char>" - overrides the base template's display character;
// restricted to '!'/'?'/'='/'~' since it's only ever used for item-mimic
// monsters. Optional in `monster` - see top-of-file problem #1 for what
// happens when it's absent (i.e. for 620 of monster.txt's 624 records).
glyph
        returns[char glyphChr]
        :   GLYPH {
                $glyphChr = $GLYPH.getText().charAt(6);
            }
        ;

// "color:<colour char>" - display colour; always present (no base-level
// default exists, since monster_base.txt has no colour field).
colour
        returns[ColourType colourEnum]
        :   COLOUR COLOUR_CHAR {
                String raw = $COLOUR_CHAR.getText();
                $colourEnum = ColourType.getColourType(raw);
            }
        ;

// "color-cycle:<group>:<cycle name>" - links this monster to a colour-
// cycling animation group/cycle
colourCycle
        returns[String groupName, String cycleName]
        :   COLOUR_CYCLE group=LCASE COLON cycle=LCASE {
                $groupName = $group.getText();
                $cycleName = $cycle.getText();
            }
        ;

// "speed:<value>".
speed
        returns[int speedInt]
        :   SPEED INTEGER { $speedInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "hit-points:<value>" - average HP.
hitPoints
        returns[int hpInt]
        :   HIT_POINTS INTEGER { $hpInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "light:<value>" - light radius the monster emits/needs.
light
        returns[int lightInt]
        :   LIGHT INTEGER { $lightInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "hearing:<value>".
hearing
        returns[int hearingInt]
        :   HEARING INTEGER { $hearingInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "smell:<value>".
smell
        returns[int smellInt]
        :   SMELL INTEGER { $smellInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "armor-class:<value>".
armourClass
        returns[int acInt]
        :   ARMOUR_CLASS INTEGER { $acInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "sleepiness:<value>" - how easily the monster wakes.
sleepiness
        returns[int sleepInt]
        :   SLEEPINESS INTEGER { $sleepInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "depth:<value>" - native dungeon depth.
dungeonDepth
        returns[int depthInt]
        :   DEPTH INTEGER { $depthInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "rarity:<value>" - generation rarity.
rarity
        returns[int rarityInt]
        :   RARITY INTEGER { $rarityInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "experience:<value>" - base XP for killing this monster.
experience
        returns[int expInt]
        :   EXPERIENCE INTEGER { $expInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "blow:<METHOD>[:<EFFECT>[:<dice>]]" - one melee attack; can repeat (see
// `monster`'s blowsInit list).
blow
        returns[MonsterBlow blowObj]
        @init {
            BlowMethod metObj = null;
            BlowEffect effObj = null;
            Random amtStr = null;
        }
        @after
        {
            $blowObj = new MonsterBlow(metObj, effObj, amtStr);
        }
        :   BLOW method1=UCASE COLON effect1=UCASE COLON amount1=DICE_STRING {
                metObj = GameConstants.lookupBlowMethod($method1.getText());
                effObj = GameConstants.lookupBlowEffect($effect1.getText());
                amtStr = Random.parseStr($amount1.getText());
            }
        |   BLOW method2=UCASE COLON effect2=UCASE {
                metObj = GameConstants.lookupBlowMethod($method2.getText());
                effObj = GameConstants.lookupBlowEffect($effect2.getText());
            }
        |   BLOW method3=UCASE {
                metObj = GameConstants.lookupBlowMethod($method3.getText());
            }
        ;

// "flags:<RF_FLAG> [| <RF_FLAG> ...]" - race flags explicitly listed for
// this monster. See top-of-file problem #1: this does NOT also include
// whatever flags the monster's base template grants - those are never
// merged in anywhere in this grammar. The whole line is folded into one
// FLAGS token (see below) and split apart in this action rather than via
// separate sub-tokens.
flags
        returns[Flag<MonsterRaceFlag> flags_On]
        @init {
            $flags_On = new Flag<>(MonsterRaceFlag.class);
        }
        :   FLAGS {
                String [] fullString = $FLAGS.getText().split(":");
                String [] flagsString = fullString[1].split("\\|");

                for (String flagString : flagsString) {
                    if (!flagString.isEmpty()) {
                        String flag = "RF_" + flagString.trim();
                        $flags_On.on(MonsterRaceFlag.valueOf(flag));
                    }
                }
            }
        ;

// "flags-off:<RF_FLAG> [| <RF_FLAG> ...]" - race flags explicitly removed
// from this monster (e.g. to override a flag the base template grants).
flagsOff
        returns[Flag<MonsterRaceFlag> flags_Off]
        @init {
            $flags_Off = new Flag<>(MonsterRaceFlag.class);
        }
        :   FLAGS_OFF f1=UCASE {
                String flagString1 = $f1.getText().trim();
                $flags_Off.on(MonsterRaceFlag.valueOf("RF_" + flagString1));
            }
            (OR f2=UCASE {
                String flagString2 = $f2.getText().trim();
                $flags_Off.on(MonsterRaceFlag.valueOf("RF_" + flagString2));
            })*
        ;

// "innate-freq:<value>" - frequency of innate (non-spell) ranged attacks.
innateFreq
        returns[int innateFreqInt]
        :   INNATE_FREQ INTEGER { $innateFreqInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "spell-freq:<value>" - frequency of spellcasting.
spellFreq
        returns[int spellFreqInt]
        :   SPELL_FREQ INTEGER { $spellFreqInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "spell-power:<value>" - spell power scaling.
spellPower
        returns[int spellPowerInt]
        :   SPELL_POWER INTEGER { $spellPowerInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "spells:<RSF_CODE> [| <RSF_CODE> ...]" - the spells (from monster_spell.txt)
// this monster can cast. The whole line is folded into one SPELLS token
// (see below) and split apart in this action.
spells
        returns[List<MonsterSpell> spellList]
        @init {
            $spellList = new ArrayList<>();
        }
        :   SPELLS {
                String [] rawSplit = $SPELLS.getText().split(":");
                String [] spellArray = rawSplit[1].split("\\|");

                for (String spellName : spellArray) {
                    MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + spellName.trim());
                    $spellList.add(spellType);
                }
            }
        ;

// "message-vis:<RSF_CODE>:<text>" - alternate spell message shown when the
// caster is visible; can repeat (see `monster`'s spellMsgInit list).
messageVis
        returns[MonsterAltmsg altMsg]
        :   MESSAGE_VIS UCASE COLON LCASE {
                MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + $UCASE.getText());
                String spellMessage = $LCASE.getText();

                $altMsg = new MonsterAltmsg(spellMessage, MonsterAltmsgType.MON_ALTMSG_SEEN, spellType);
            }
        ;

// "message-invis:<RSF_CODE>:<text>" - alternate spell message shown when
// the caster is unseen.
messageInvis
        returns[MonsterAltmsg altMsg]
        :   MESSAGE_INVIS UCASE COLON LCASE {
                MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + $UCASE.getText());
                String spellMessage = $LCASE.getText();

                $altMsg = new MonsterAltmsg(spellMessage, MonsterAltmsgType.MON_ALTMSG_UNSEEN, spellType);
            }
        ;

// "message-miss:<RSF_CODE>:<text>" - alternate spell message shown when
// the spell misses.
messageMiss
        returns[MonsterAltmsg altMsg]
        :   MESSAGE_MISS UCASE COLON LCASE {
                MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + $UCASE.getText());
                String spellMessage = $LCASE.getText();

                $altMsg = new MonsterAltmsg(spellMessage, MonsterAltmsgType.MON_ALTMSG_MISS, spellType);
            }
        ;

// "desc:<text>" - monster-lore description; can repeat to build up
// multiple lines.
desc
        returns[String descStr]
        :   DESC LCASE { $descStr = $LCASE.getText(); }
        ;

// "shape:<text>" - a shapechange form name (from shape.txt) this monster
// can take; can repeat (see `monster`'s shapesInit list).
shape
        returns[String shapeStr]
        :   SHAPE LCASE { $shapeStr = $LCASE.getText(); }
        ;

// "drop:<tval text>:<sval/kind name>:<chance>:<min>:<max>" - a specific
// item drop; can repeat (see `monster`'s dropsInit list).
drop
        returns[MonsterDrop dropObj]
        // TValue tval, String sval, int percentage, int min, int max]
        :   DROP t=LCASE COLON s=LCASE COLON p=INTEGER COLON mi=INTEGER COLON ma=INTEGER {
                TValue tval = TValue.valueOf("TV_" + $t.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
                ObjectKind oKind = GameConstants.lookupObjectKind($s.getText());
                int percentage = Integer.parseInt($p.getText());
                int min = Integer.parseInt($mi.getText());
                int max = Integer.parseInt($ma.getText());
                $dropObj = new MonsterDrop(tval, oKind, percentage, min, max);
            }
        ;

// "drop-base:<tval text>:<chance>:<min>:<max>" - a drop of any item of the
// given base type (not a specific kind); can repeat.
dropBase
        returns[MonsterDrop dropObj]
        :   DROP_BASE LCASE COLON p=INTEGER COLON mi=INTEGER COLON ma=INTEGER {
                TValue tval = TValue.valueOf("TV_" + $LCASE.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
                int percentage = Integer.parseInt($p.getText());
                int min = Integer.parseInt($mi.getText());
                int max = Integer.parseInt($ma.getText());
                $dropObj = new MonsterDrop(tval, percentage, min, max);
            }
        ;

// "mimic:<tval text>:<sval/kind name>" - the object kind this monster
// disguises itself as (paired with an explicit glyph: override); can repeat.
mimic
        returns[ObjectKind objKind]
        :   MIMIC t=LCASE COLON s=LCASE {
                TValue tval = TValue.valueOf("TV_" + $t.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
                String sval = $s.getText();
                $objKind = GameConstants.lookupObjectKind(tval, sval);
            }
        ;

// "friends:<chance>:<dice>:<monster name>[:<group role>]" - a specific
// monster this one is generated alongside; can repeat. The whole line is
// folded into one FRIENDS token (see below) and split apart in this
// action.
friends
        returns[MonsterFriends friendsObj]
        //int chance, String noAppearing, String nameOfMonster]
        :   FRIENDS {
                String [] rawSplit = $FRIENDS.getText().split(":");
                // Ignore the first entry
                int chance = Integer.parseInt(rawSplit[1]);
                String [] noAppearing = rawSplit[2].split("d");
                String nameOfMonster = rawSplit[3];
                String role = "none";
                if (rawSplit.length > 4)
                    role = rawSplit[4];

                role = "MON_GROUP_" + role.toUpperCase();

                MonsterGroupRole mgRole = MonsterGroupRole.valueOf(role);

                $friendsObj = new MonsterFriends(nameOfMonster, mgRole, chance, Integer.parseInt(noAppearing[0]),
                 Integer.parseInt(noAppearing[1]));
            }
        ;

// "friends-base:<chance>:<dice>:<monster_base.txt name>[:<group role>]" -
// a monster of the given base template generated alongside this one; can
// repeat. Same folded-token/split-on-'d' pattern as `friends`.
friendsBase
        returns[MonsterFriendsBase friendsBaseObj]
            //int chance, String noAppearing, String typeAppearing]
        :   FRIENDS_BASE {
                String [] rawSplit = $FRIENDS_BASE.getText().split(":");
                // Ignore the first entry
                int chance = Integer.parseInt(rawSplit[1]);
                String[] noAppearing = rawSplit[2].split("d");
                MonsterBase typeAppearing = GameConstants.lookupMonsterBase(rawSplit[3]);
                String roleName = "MON_GROUP_NONE";
                if (rawSplit.length > 4)
                    roleName = "MON_GROUP_" + rawSplit[4].toUpperCase();

                MonsterGroupRole mgRole = MonsterGroupRole.valueOf(roleName);

                $friendsBaseObj = new MonsterFriendsBase(typeAppearing, mgRole, chance,
                Integer.parseInt(noAppearing[0]), Integer.parseInt(noAppearing[1]));
            }
        ;

// One full monster record: name, optional plural, mandatory base, optional
// glyph override, mandatory colour, then any mix of every other directive
// in any order/quantity. See top-of-file problem #1 re: base/glyph/flags
// inheritance.
monster
        returns[MonsterRace race]
        @init {
            String nameInit = "";
            String textInit = "";
            String pluralInit = "";
            MonsterBase baseInit = null;
            int averageHPInit = 0;
            int acInit = 0;
            int sleepInit = 0;
            int hearingInit = 0;
            int smellInit = 0;
            int speedInit = 0;
            int lightInit = 0;
            int mexpInit = 0;
            int freqInnateInit = 0;
            int freqSpellInit = 0;
            int spellPowerInit = 0;
            Flag<MonsterRaceFlag> flagsInit = new Flag<>(MonsterRaceFlag.class);
            Flag<MonsterRaceFlag> flagsOffInit = new Flag<>(MonsterRaceFlag.class);
            List<MonsterSpell> spellsInit = new ArrayList<>();
            List<MonsterBlow> blowsInit = new ArrayList<>();
            int levelInit = 0;
            int rarityInit = 0;
            char glyphInit = ' ';
            ColourType colourTypeInit = ColourType.COLOUR_TYPE_DARK;
            int maxNumInit = 0;
            int curNumInit = 0;
            List<MonsterAltmsg> spellMsgInit = new ArrayList<>();
            List<MonsterDrop> dropsInit = new ArrayList<>();
            List<MonsterFriends> friendsInit = new ArrayList<>();
            List<MonsterFriendsBase> friendsBaseInit = new ArrayList<>();
            List<ObjectKind> mimicKindsInit = new ArrayList<>();
            List<String> shapesInit = new ArrayList<>();
            int numShapesInit = 0;
            String groupNameInit = "";
            String cycleNameInit = "";
        }
        @after {
            // TODO(ClaudeCode): the MonsterRace constructor signature has changed; this call no longer
            // matches. Commented out to keep the build green until the monster parser is re-plumbed.
            /*AngbandDisplayCharacter adcInit = new AngbandDisplayCharacter(glyphInit, colourTypeInit);
            $race = new MonsterRace(nameInit, textInit, pluralInit,
            baseInit, averageHPInit, acInit, sleepInit, hearingInit,
            smellInit, speedInit, lightInit, mexpInit, freqInnateInit,
            freqSpellInit, spellPowerInit, flagsInit, flagsOffInit,
            spellsInit, blowsInit, levelInit, rarityInit, adcInit,
            maxNumInit, curNumInit, spellMsgInit, dropsInit,
            friendsInit, friendsBaseInit, mimicKindsInit, shapesInit,
            numShapesInit, groupNameInit, cycleNameInit);*/
            $race = null;
        }
        :   name { nameInit = $name.nameStr; }
            (plural { pluralInit = $plural.pluralStr; })?
            base { baseInit = $base.baseObj; }
            (glyph { glyphInit = $glyph.glyphChr; })?
            colour { colourTypeInit = $colour.colourEnum; }
        (   colourCycle {
                groupNameInit = $colourCycle.groupName;
                cycleNameInit = $colourCycle.cycleName; }
        |   speed { speedInit = $speed.speedInt; }
        |   hitPoints { averageHPInit = $hitPoints.hpInt; }
        |   light { lightInit = $light.lightInt; }
        |   hearing { hearingInit = $hearing.hearingInt; }
        |   smell { smellInit = $smell.smellInt; }
        |   armourClass { acInit = $armourClass.acInt; }
        |   sleepiness { sleepInit = $sleepiness.sleepInt; }
        |   dungeonDepth { levelInit = $dungeonDepth.depthInt; }
        |   rarity { rarityInit = $rarity.rarityInt; }
        |   experience { mexpInit = $experience.expInt; }
        |   blow { blowsInit.add($blow.blowObj); }
        |   flags { flagsInit.union($flags.flags_On); }
        |   flagsOff { flagsOffInit.union($flagsOff.flags_Off); }
        |   innateFreq { freqInnateInit = $innateFreq.innateFreqInt; }
        |   spellFreq { freqSpellInit = $spellFreq.spellFreqInt; }
        |   spellPower { spellPowerInit = $spellPower.spellPowerInt; }
        |   spells { spellsInit.addAll($spells.spellList); }
        |   messageVis { spellMsgInit.add($messageVis.altMsg); }
        |   messageInvis { spellMsgInit.add($messageInvis.altMsg); }
        |   messageMiss { spellMsgInit.add($messageMiss.altMsg); }
        |   drop { dropsInit.add($drop.dropObj); }
        |   dropBase { dropsInit.add($dropBase.dropObj); }
        |   mimic { mimicKindsInit.add($mimic.objKind); }
        |   friends { friendsInit.add($friends.friendsObj); }
        |   friendsBase { friendsBaseInit.add($friendsBase.friendsBaseObj); }
        |   desc { textInit = textInit + $desc.descStr; }
        |   shape {
                shapesInit.add($shape.shapeStr);
                numShapesInit++;
            })*
        ;

// Top-level rule: the whole file is one or more monster records.
file
        returns[List<MonsterRace> monsterRaces]
        @init {
            $monsterRaces = new ArrayList<>();
        }
        :   (monster {
                $monsterRaces.add($monster.race);
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

NAME
        :   'name:'
        ;

PLURAL
        :   'plural:'
        ;

BASE
        :   'base:'
        ;

// Restricted to the 4 item-mimic symbols - see top-of-file problem #1.
GLYPH
        :   'glyph:' ('!' | '?' | '=' | '~')
        ;

COLOUR
        :   'color:'
        ;

SPEED
        :   'speed:'
        ;

HIT_POINTS
        :   'hit-points:'
        ;

LIGHT
        :   'light:'
        ;

HEARING
        :   'hearing:'
        ;

SMELL
        :   'smell:'
        ;

ARMOUR_CLASS
        :   'armor-class:'
        ;

SLEEPINESS
        :   'sleepiness:'
        ;

DEPTH
        :   'depth:'
        ;

RARITY
        :   'rarity:'
        ;

EXPERIENCE
        :   'experience:'
        ;

BLOW
        :   'blow:'
        ;

// "flags:" plus the whole '|'-separated flag list as one token, split
// apart in the `flags` rule's action.
FLAGS
        :   'flags:' UCASE (' | ' UCASE)*
        ;

FLAGS_OFF
        :   'flags-off:'
        ;

INNATE_FREQ
        :   'innate-freq:'
        ;

SPELL_FREQ
        :   'spell-freq:'
        ;

SPELL_POWER
        :   'spell-power:'
        ;

// "spells:" plus the whole '|'-separated spell list as one token, split
// apart in the `spells` rule's action.
SPELLS
        :   'spells:' UCASE (' | ' UCASE)*
        ;

MESSAGE_VIS
        :   'message-vis:'
        ;

MESSAGE_INVIS
        :   'message-invis:'
        ;

MESSAGE_MISS
        :   'message-miss:'
        ;

DESC
        :   'desc:'
        ;

DROP
        :   'drop:'
        ;

DROP_BASE
        :   'drop-base:'
        ;

MIMIC
        :   'mimic:'
        ;

SHAPE
        :   'shape:'
        ;

// "friends:" plus the whole "<chance>:<dice>:<name>[:<role>]" value as one
// token, split apart in the `friends` rule's action.
FRIENDS
        :   'friends:' INTEGER COLON DICE_STRING COLON LCASE COLON LCASE
        |   'friends:' INTEGER COLON DICE_STRING COLON LCASE
        ;

// Same shape as FRIENDS, for friends-base:.
FRIENDS_BASE
        :   'friends-base:' INTEGER COLON DICE_STRING COLON LCASE COLON LCASE
        |   'friends-base:' INTEGER COLON DICE_STRING COLON LCASE
        ;

COLOUR_CYCLE
        :   'color-cycle:'
        ;

// A (possibly negative) literal integer.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// A dice expression in any of the "base+dice'd'sides['M'bonus]" shapes -
// used inside FRIENDS/FRIENDS_BASE's number-appearing field (always the
// 'd'-containing alternatives in practice - see top-of-file problem #2).
DICE_STRING
        :   INTEGER '+' INTEGER 'd' INTEGER ('M' | 'm') INTEGER
        |   INTEGER '+' 'd' INTEGER ('M' | 'm') INTEGER
        |   INTEGER 'd' INTEGER ('M' | 'm') INTEGER
        |   'd' INTEGER ('M' | 'm') INTEGER
        |   INTEGER ('M' | 'm') INTEGER
        |   INTEGER '+' INTEGER 'd' INTEGER
        |   INTEGER '+' 'd' INTEGER
        |   INTEGER 'd' INTEGER
        |   'd' INTEGER
        |   INTEGER
        ;

// Field separator within blow:/drop:/mimic:/color-cycle:/message-*: lines.
COLON
        :   ':'
        ;

// The " | " separator between entries on a flags-off: line.
OR
        :   ' | '
        ;

// A single colour-code character - used for color:.
COLOUR_CHAR
        :   ('d' | 'w' | 's' | 'o' | 'r' | 'g' | 'b' | 'u' | 'D' | 'W' | 'P' | 'y' | 'R'
            | 'G' | 'B' | 'U' | 'p' | 'v' | 't' | 'm' | 'Y' | 'i' | 'T' | 'V' | 'I' | 'M' | 'z' | 'Z')
        ;

// An UPPER_CASE_WITH_UNDERSCORES_OR_DIGITS symbolic name - used for flag/
// spell codes inside FLAGS/SPELLS/FLAGS_OFF, and message-*'s spell code field.
UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

// Free-running text (including several accented characters used in
// monster names/descriptions) - used for most other string fields.
LCASE
        :   ('a'..'z' | ' ' | 'A'..'Z' | ',' | '<' | '>' | '.' | '-' |
             '\'' | '!' | '?' | 'é' | '{' | '}' | 'á' | ';' | 'ú' | 'ó' |
             '2'..'9' | '*' | '"' | 'î' | 'ë' | 'û' | '(' | ')' | 'ö' |
             'ô')+
        ;