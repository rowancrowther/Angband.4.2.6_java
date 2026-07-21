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

// Reader for lib/user/lore.txt - the player's per-save monster-knowledge
// record (sightings/kills/deaths, which blows/flags/spells/drops/friends/
// mimicry have been observed for each monster). Paired with LoreLexer.g4.
// Cf. src/mon-init.c's lore_parser (mon-init.c:2646) and its directive
// table (~mon-init.c:2520-2540) - this is also the format used by
// src/tests/parse/lore.c, which reuses monster.txt's own parser with the
// lore-specific finish/cleanup swapped in.

parser grammar LoreGrammar;
options { tokenVocab = LoreLexer; }

@header {
    import uk.co.jackoftrades.backend.numerics.Random;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.combat.BlowMethod;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.BlowEffect;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterBlow;
    import uk.co.jackoftrades.middle.monsters.MonsterDrop;
    import uk.co.jackoftrades.middle.monsters.MonsterFriends;
    import uk.co.jackoftrades.middle.monsters.MonsterFriendsBase;
    import uk.co.jackoftrades.middle.monsters.MonsterLore;
    import uk.co.jackoftrades.middle.monsters.MonsterMimic;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.objects.ObjectKind;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
}

// "name:<text>" - starts a new monster lore record; identifies the
// monster.txt race this lore belongs to.
name
        returns[String nameString]
        :   NAME MONSTER_NAME { $nameString = $MONSTER_NAME.getText(); }
        ;

// "counts:<sightings>:<deaths>:<kills>:<wake>:<ignore>:<innate casts>:<spell casts>".
counts
        returns[int sightings, int deaths, int kills, int wake, int ignore, int innate, int spell]
        :   COUNTS sig=INTEGER COLON dea=INTEGER COLON kil=INTEGER COLON wak=INTEGER COLON ign=INTEGER COLON
            inn=INTEGER COLON spe=INTEGER {
                $sightings = Integer.parseInt($sig.getText());
                $deaths = Integer.parseInt($dea.getText());
                $kills = Integer.parseInt($kil.getText());
                $wake = Integer.parseInt($wak.getText());
                $ignore = Integer.parseInt($ign.getText());
                $innate = Integer.parseInt($inn.getText());
                $spell = Integer.parseInt($spe.getText());
            }
        ;

// "blow:<METHOD>:<EFFECT>:<dice>:<times seen>:<blow index>" - one observed
// attack; can repeat (see `monsterLore`'s blows map).
// Blow index field is never used as we are using actual instantiated objects
// instead of an index to the relevant list
blow
        returns[MonsterBlow monBlow, Integer timesSeen]
        :   BLOW BLOW_MODE_VALUES {
                String raw = $BLOW_MODE_VALUES.getText();
                String [] parts = raw.split(":");
                BlowMethod blowMethod = GameConstants.lookupBlowMethod(parts[0]);
                BlowEffect blowEffect = GameConstants.lookupBlowEffect(parts[1]);
                Random dice = Random.parseStr(parts[2]);
                $timesSeen = Integer.parseInt(parts[3]);

                $monBlow = new MonsterBlow(blowMethod, blowEffect, dice);
            }
        ;

// "flags:<RF_FLAG> [| <RF_FLAG> ...]" - race flags observed for this monster.
flags
        returns[Flag<MonsterRaceFlag> loreFlags]
        @init {
            $loreFlags = new Flag<>(MonsterRaceFlag.class);
        }
        :   FLAGS f1=FLAG
            {
                String raw1 = "RF_" + $f1.getText().trim();
                $loreFlags.on(MonsterRaceFlag.valueOf(raw1));
            } (OR f2=FLAG {
                String raw2 = "RF_" + $f2.getText().trim();
                $loreFlags.on(MonsterRaceFlag.valueOf(raw2));
            })*
        ;

// "spells:<RSF_CODE> [| <RSF_CODE> ...]" - spells observed cast by this monster.
spells
        returns[Flag<MonsterSpell> spellFlags]
        @init {
            $spellFlags = new Flag<>(MonsterSpell.class);
        }
        :   SPELLS s1=FLAG
            {
                String raw1 = "RSF_" + $s1.getText().trim();
                $spellFlags.on(MonsterSpell.valueOf(raw1));
            } (SPELL_OR s2=FLAG {
                String raw2 = "RSF_" + $s2.getText().trim();
                $spellFlags.on(MonsterSpell.valueOf(raw2));
            })*
        ;

// "base:<monster_base.txt name>" - the observed base template, if known.
base
        returns[MonsterBase baseObj]
        :   BASE MONSTER_NAME {
                String raw = $MONSTER_NAME.getText();
                $baseObj = GameConstants.getBaseFromName(raw);
            }
        ;

// "drop:<tval text>:<sval/kind name>:<chance>:<min>:<max>" - a specific
// observed drop; can repeat (see `monsterLore`'s drops list).
drop
        returns[MonsterDrop monsterDrop]
        @init {
            TValue tval = TValue.TV_NONE;
            ObjectKind sval = null;
            int chance = 0;
            int min = 0;
            int max = 0;
        }
        @after {
            $monsterDrop = new MonsterDrop(tval, sval, chance, min, max);
        }
        :   DROP TVAL COLON STRING COLON ch=INTEGER COLON mn=INTEGER COLON mx=INTEGER
            {
                tval = TValue.valueOf("TV_" + $TVAL.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
                sval = GameConstants.lookupObjectKind(tval, $STRING.getText());
                chance = Integer.parseInt($ch.getText());
                min = Integer.parseInt($mn.getText());
                max = Integer.parseInt($mx.getText());
            }
        ;

// "drop-base:<tval text>:<chance>:<min>:<max>" - an observed drop of any
// item of the given base type; can repeat.
dropBase
        returns[MonsterDrop monsterDropBase]
        @init {
            TValue tval = TValue.TV_NONE;
            String sval = "";
            int chance = 0;
            int min = 0;
            int max = 0;
        }
        :   DROP_BASE TVAL COLON ch=INTEGER COLON mn=INTEGER COLON mx=INTEGER
            {
                tval = TValue.valueOf("TV_" + $TVAL.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
                chance = Integer.parseInt($ch.getText());
                min = Integer.parseInt($mn.getText());
                max = Integer.parseInt($mx.getText());
            }
        ;

// "friends:<chance>:<dice>:<monster name>[:<group role>]" - an observed
// group-member monster; can repeat.
friends
        returns[MonsterFriends monsterFriends]
        @init {
            String friendsName = "";
            MonsterGroupRole friendsRole = MonsterGroupRole.MON_GROUP_NONE;
            int percentChance = 0;
            String diceString = "";
            int numberOfDice = 0;
            int sidesOfDice = 0;
        }
        @after {
            $monsterFriends = new MonsterFriends(friendsName, friendsRole, percentChance,
                                                 numberOfDice, sidesOfDice);
        }
        :   FRIENDS chance=INTEGER COLON number=DICE_STRING COLON fName=FRIENDS_NAME {
                percentChance = Integer.parseInt($chance.getText());
                diceString = $number.getText();
                Random temp = Random.parseStr(diceString);
                numberOfDice = temp.getDice();
                sidesOfDice = temp.getSides();
                friendsName = $fName.getText();
            } (COLON role=FRIENDS_NAME {
                String raw = $role.getText().toUpperCase();
                friendsRole = MonsterGroupRole.valueOf("MON_GROUP_" + raw);
            })?
        ;

// "friends-base:<chance>:<dice>:<monster_base.txt name>[:<group role>]" -
// an observed group-member monster of a given base; can repeat.
friendsBase
        returns[MonsterFriendsBase baseFriend]
        @init {
            MonsterBase base = null;
            MonsterGroupRole role = MonsterGroupRole.MON_GROUP_NONE;
            int percentageChance = 0;
            int numberOfDice = 0;
            int numberOfSides = 0;
        }
        @after {
            $baseFriend = new MonsterFriendsBase(base, role, percentageChance, numberOfDice,
                                                 numberOfSides);
        }
        :   FRIENDS_BASE chance=INTEGER COLON dice=DICE_STRING COLON fName=FRIENDS_BASE_NAME {
                percentageChance = Integer.parseInt($chance.getText());
                Random temp = Random.parseStr($dice.getText());
                numberOfDice = temp.getDice();
                numberOfSides = temp.getSides();
                base = GameConstants.lookupMonsterBase($fName.getText());
            } (COLON fRole=FRIENDS_NAME {
                role = MonsterGroupRole.valueOf("MON_GROUP_" + $fRole.getText().toUpperCase());
            })?
        ;

// "mimic:<tval text>:<sval/kind name>" - an observed item-kind this
// monster has been seen disguised as; can repeat (see `monsterLore`'s
// mimics list).
mimic
        returns[ObjectKind kind]
        :   MIMIC TVAL COLON STRING {
                TValue tval = TValue.valueOf("TV_" + $TVAL.getText().toUpperCase().replace(" ", "_")
                                    .replace("ARMOUR", "ARMOR"));
                String sval = $STRING.getText();
                $kind = GameConstants.lookupObjectKind(tval, sval);
            }
        ;

// One full lore record: name, then any mix of the directives above in any
// order/quantity.
monsterLore
        returns[MonsterLore lore]
        @init {
            int sights = 0;
            int deaths = 0;
            int pkills = 0;
            int thefts = 0;
            int tkills = 0;
            int wake = 0;
            int ignore = 0;
            int innate = 0;
            int spell = 0;
            int maxGold = 0;
            int maxItem = 0;
            int castInnate = 0;
            int castSpell = 0;
            Map<MonsterBlow, Integer> blows = new HashMap<>();
            Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
            Flag<MonsterSpell> spells = new Flag<>(MonsterSpell.class);
            List<MonsterDrop> drops = new ArrayList<>();
            List<MonsterFriends> friends = new ArrayList<>();
            List<MonsterFriendsBase> baseFriends = new ArrayList<>();
            List<ObjectKind> mimics = new ArrayList<>();
            MonsterBase monsterBase = null;
            MonsterRace race = null;
        }
        @after {
            MonsterMimic mimicInit = new MonsterMimic(mimics);
            List<MonsterMimic> mm = new ArrayList<>();
            mm.add(mimicInit);
            $lore = new MonsterLore(sights, deaths, pkills, thefts,
                                    tkills, wake, ignore, maxGold,
                                    maxItem, castInnate, castSpell,
                                    flags, spells, drops,
                                    friends, baseFriends, mm,
                                    blows, monsterBase);
            race.setLore($Lore);
        }
        :   name { race = GameConstants.lookupMonsterRace($name.nameString); }
        (   counts {
                sights = $counts.sightings;
                deaths = $counts.deaths;
                tkills = $counts.kills;
                wake = $counts.wake;
                ignore = $counts.ignore;
                innate = $counts.innate;
                spell = $counts.spell;
            }
        |   base {
                monsterBase = $base.baseObj;
            }
        |   blow {
                blows.put($blow.monBlow, $blow.timesSeen);
            }
        |   flags {
                flags.union($flags.loreFlags);
            }
        |   spells {
                spells.union($spells.spellFlags);
            }
        |   drop {
                drops.add($drop.monsterDrop);
            }
        |   friends {
                friends.add($friends.monsterFriends);
            }
        |   friendsBase {
                baseFriends.add($friendsBase.baseFriend);
            }
        |   mimic {
                mimics.add($mimic.kind);
            }
        )+;

// Top-level rule: the whole file is one or more lore records.
//
// BUG: no @init initializes loreEntries and no action adds each
// monsterLore match to it - see top-of-file problem #1. $loreEntries is
// always null.
file
        returns[List<MonsterLore> loreEntries]
        @init {
            $loreEntries = new ArrayList<>();
        }
        :   (monsterLore { $loreEntries.add($monsterLore.lore); })+ EOF
        ;