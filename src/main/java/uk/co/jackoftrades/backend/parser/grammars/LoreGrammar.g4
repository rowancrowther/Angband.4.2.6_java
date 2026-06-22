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

name
        returns[String nameString]
        :   NAME MONSTER_NAME { $nameString = $MONSTER_NAME.getText(); }
        ;

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

base
        returns[MonsterBase baseObj]
        :   BASE MONSTER_NAME {
                String raw = $MONSTER_NAME.getText();
                $baseObj = GameConstants.getBaseFromName(raw);
            }
        ;

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
                String raw = $role.getText();
                friendsRole = MonsterGroupRole.valueOf("MON_GROUP_" + raw);
            })?
        ;

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
                base = GameConstants.getMonsterBase($fName.getText());
            } (COLON fRole=FRIENDS_NAME {
                role = MonsterGroupRole.valueOf("MON_GROUP_" + $fRole.getText().toUpperCase());
            })?
        ;

mimic
        returns[ObjectKind kind]
        :   MIMIC TVAL COLON STRING {
                TValue tval = TValue.valueOf("TV_" + $TVAL.getText().toUpperCase().replace(" ", "_")
                                    .replace("ARMOUR", "ARMOR"));
                String sval = $STRING.getText();
                $kind = GameConstants.lookupObjectKind(tval, sval);
            }
        ;

monsterLore
        returns[MonsterRace race, MonsterLore lore]
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
        }
        :   name { $race = GameConstants.lookupMonsterRace($name.nameString); }
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

file
        returns[List<MonsterLore> loreEntries]
        :   monsterLore+ EOF
        ;