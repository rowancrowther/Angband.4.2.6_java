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

// Reader+lexer for lib/gamedata/pit.txt - every monster pit/nest profile
// (room type, allocation, required/forbidden monster flags and spells,
// specific base templates/colours/banned monsters). The data file and the
// C struct call these "pits" (pit_profile) even though the room type can
// be pit/nest/other - cf. src/mon-init.c: struct file_parser pit_parser
// (mon-init.c:2232). Despite the grammar's own filename, it's wired up via
// PitReader.java, not a "MonsterNestReader".
//
// No problems found - `pit`'s flexible `(...)+ ` loop correctly accumulates
// every repeatable directive (mon-base:/color:/mon-ban: via list .add(),
// flags-req:/flags-ban:/spell-req:/spell-ban: via Flag.union()), and
// room:/alloc:/obj-rarity: match the C struct's plain-uint fields with a
// small added enum translation (1/2/3 -> PIT_TYPE_PIT/NEST/OTHER) that C
// itself doesn't do at parse time.

grammar MonsterNest;

@header {
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.cave.PitProfile;
    import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;

    import java.util.ArrayList;
    import java.util.List;
}

// "name:<text>" - starts a new pit profile record.
name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

// "room:1|2|3" - room type (pit/nest/other).
room
        returns[PitRoomType roomTypeEnum]
        :   ROOM INTEGER {
                switch ($INTEGER.getText()) {
                    case "1":
                        $roomTypeEnum = PitRoomType.PIT_TYPE_PIT;
                        break;

                    case "2":
                        $roomTypeEnum = PitRoomType.PIT_TYPE_NEST;
                        break;

                    case "3":
                        $roomTypeEnum = PitRoomType.PIT_TYPE_OTHER;
                        break;

                    default:
                        $roomTypeEnum = PitRoomType.PIT_TYPE_NONE;
                        break;
                }
            }
        ;

// "alloc:<rarity>:<average level>".
alloc
        returns[int rarityInt, int levelInt]
        :   ALLOC rar=INTEGER COLON lev=INTEGER {
                $rarityInt = Integer.parseInt($rar.getText());
                $levelInt = Integer.parseInt($lev.getText());
            }
        ;

// "obj-rarity:<value>" - rarity of object drops generated in this pit.
objRarity
        returns[int rarityInt]
        :   OBJ_RARITY INTEGER { $rarityInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "color:<colour char>" - an allowed monster colour for this pit; can
// repeat (see `pit`'s coloursInit list).
colour
        returns[ColourType colourType]
        :   COLOUR cc=COLOUR_CHAR { $colourType = ColourType.findColourType($cc.getText().charAt(0)); }
        ;

// "mon-base:<monster_base.txt name>" - an allowed monster base for this
// pit; can repeat (see `pit`'s basesInit list).
monBase
        returns[MonsterBase base]
        :   MON_BASE STRING {
                String raw = $STRING.getText();
                $base = GameConstants.getMonsterBase(raw);
            }
        ;

// "flags-req:<RF_FLAG> [| <RF_FLAG> ...]" - race flags a monster must have
// to appear in this pit.
flagsReq
        returns[Flag<MonsterRaceFlag> flags]
        @init {
            $flags = new Flag<>(MonsterRaceFlag.class);
        }
        :   FLAGS_REQ f1=STRING {
                String raw1 = "RF_" + $f1.getText().trim().toUpperCase();
                $flags.on(MonsterRaceFlag.valueOf(raw1));
            }
            (OR f2=STRING {
                String raw2 = "RF_" + $f2.getText().trim().toUpperCase();
                $flags.on(MonsterRaceFlag.valueOf(raw2));
            })*
        ;

// "flags-ban:<RF_FLAG> [| <RF_FLAG> ...]" - race flags that exclude a
// monster from this pit.
flagsBan
        returns[Flag<MonsterRaceFlag> flags]
        @init {
            $flags = new Flag<>(MonsterRaceFlag.class);
        }
        :   FLAGS_BAN f1=STRING {
                String raw1 = "RF_" + $f1.getText().trim().toUpperCase();
                $flags.on(MonsterRaceFlag.valueOf(raw1));
            }
            (OR f2=STRING{
                String raw2 = "RF_" + $f2.getText().trim().toUpperCase();
                $flags.on(MonsterRaceFlag.valueOf(raw2));
            })*
        ;

// "innate-freq:<value>" - minimum innate-attack frequency required.
innateFreq
        returns[int innateFreqInt]
        :   INNATE_FREQ INTEGER { $innateFreqInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "spell-req:<RSF_CODE> [| <RSF_CODE> ...]" - spells a monster must be
// able to cast to appear in this pit.
spellReq
        returns[Flag<MonsterSpell> spells]
        @init {
            $spells = new Flag<>(MonsterSpell.class);
        }
        :   SPELL_REQ s1=STRING {
                String raw1 = "RSF_" + $s1.getText().trim().toUpperCase();
                $spells.on(MonsterSpell.valueOf(raw1));
            } (OR s2=STRING {
                String raw2 = "RSF_" + $s2.getText().trim().toUpperCase();
                $spells.on(MonsterSpell.valueOf(raw2));
            })*
        ;

// "spell-ban:<RSF_CODE> [| <RSF_CODE> ...]" - spells that exclude a monster
// from this pit.
spellBan
        returns[Flag<MonsterSpell> spells]
        @init {
            $spells = new Flag<>(MonsterSpell.class);
        }
        :   SPELL_BAN s1=STRING {
                String raw1 = "RSF_" + $s1.getText().trim().toUpperCase();
                $spells.on(MonsterSpell.valueOf(raw1));
            } (OR s2=STRING {
                String raw2 = "RSF_" + $s2.getText().trim().toUpperCase();
                $spells.on(MonsterSpell.valueOf(raw2));
            })*
        ;

// "mon-ban:<monster name>" - a specific monster excluded from this pit;
// can repeat (see `pit`'s forbiddenMonstersInit list).
monBan
        returns[MonsterRace race]
        :   MON_BAN STRING { $race = GameConstants.lookupMonsterRace($STRING.getText()); }
        ;

// One full pit profile record: name, then any mix of the directives above
// in any order/quantity.
pit
        returns[PitProfile profile]
        @init {
            String nameInit = "";
            PitRoomType roomTypeInit = PitRoomType.PIT_TYPE_NONE;
            int aveInit = 0;
            int rarityInit = 0;
            int objectRarityInit = 0;
            Flag<MonsterRaceFlag> flagsInit = new Flag<>(MonsterRaceFlag.class);
            Flag<MonsterRaceFlag> forbiddenFlagsInit = new Flag<>(MonsterRaceFlag.class);
            int freqInnateInit = 0;
            Flag<MonsterSpell> spellFlags = new Flag<>(MonsterSpell.class);
            Flag<MonsterSpell> forbiddenSpellFlags = new Flag<>(MonsterSpell.class);
            List<MonsterBase> basesInit = new ArrayList<>();
            List<ColourType> coloursInit = new ArrayList<>();
            List<MonsterRace> forbiddenMonstersInit = new ArrayList<>();
        }
        @after {
            $profile = new PitProfile(nameInit, roomTypeInit, aveInit, rarityInit, objectRarityInit,
                                      flagsInit, forbiddenFlagsInit, freqInnateInit, spellFlags,
                                      forbiddenSpellFlags, basesInit, coloursInit, forbiddenMonstersInit);
        }
        :   name { nameInit = $name.nameStr; }
        (   room { roomTypeInit = $room.roomTypeEnum; }
        |   alloc {
                aveInit = $alloc.levelInt;
                rarityInit = $alloc.rarityInt;
            }
        |   objRarity {  objectRarityInit = $objRarity.rarityInt; }
        |   colour { coloursInit.add($colour.colourType); }
        |   monBase { basesInit.add($monBase.base); }
        |   flagsReq { flagsInit.union($flagsReq.flags); }
        |   flagsBan { forbiddenFlagsInit.union($flagsBan.flags); }
        |   innateFreq { freqInnateInit = $innateFreq.innateFreqInt; }
        |   spellReq { spellFlags.union($spellReq.spells); }
        |   spellBan { forbiddenSpellFlags.union($spellBan.spells); }
        |   monBan { forbiddenMonstersInit.add($monBan.race); })+
        ;

// Top-level rule: the whole file is one or more pit profile records.
file
        returns[List<PitProfile> pits]
        @init {
            $pits = new ArrayList<>();
        }
        :   (pit {
                $pits.add($pit.profile);
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

// NAME through MON_BAN below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
NAME
        :   'name:'
        ;

ROOM
        :   'room:'
        ;

ALLOC
        :   'alloc:'
        ;

OBJ_RARITY
        :   'obj-rarity:'
        ;

COLOUR
        :   'color:'
        ;

MON_BASE
        :   'mon-base:'
        ;

FLAGS_REQ
        :   'flags-req:'
        ;

FLAGS_BAN
        :   'flags-ban:'
        ;

INNATE_FREQ
        :   'innate-freq:'
        ;

SPELL_REQ
        :   'spell-req:'
        ;

SPELL_BAN
        :   'spell-ban:'
        ;

MON_BAN
        :   'mon-ban:'
        ;

// A bare non-negative integer.
INTEGER
        :   ('0'..'9')+
        ;

// A single colour-code character - used for color:.
COLOUR_CHAR
        :   [dwsorgbuDWPyRGBUpvtmYiTVIMzZ]
        ;

// Free-running text - used for name:/mon-base:/flags-*:/spell-*:/mon-ban:'s value fields.
STRING
        :   ('a'..'z' | 'A'..'Z' | ' ' | '-' | '_')+
        ;

// Field separator within alloc: lines.
COLON
        :   ':'
        ;

// The '|' separator between entries on a flags-req:/flags-ban:/spell-req:/spell-ban: line.
OR
        :   '|'
        ;
