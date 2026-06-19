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
    import uk.co.jackoftrades.frontend.colour.VisualsColourCyclesByRace;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;


    import java.util.ArrayList;
    import java.util.List;
}

name
        returns[String nameStr]
        :   NAME LCASE { $nameStr = $LCASE.getText(); }
        ;

plural
        returns[String pluralStr]
        :   PLURAL LCASE { $pluralStr = $LCASE.getText(); }
        ;

base
        returns[MonsterBase baseObj]
        :   BASE LCASE {
                String raw = $LCASE.getText();
                $baseObj = GameConstants.getMonsterBase(raw);
            }
        ;

glyph
        returns[char glyphChr]
        :   GLYPH {
                $glyphChr = $GLYPH.getText().charAt(6);
            }
        ;

colour
        returns[ColourType colourEnum]
        :   COLOUR COLOUR_CHAR {
                String raw = $COLOUR_CHAR.getText();
                $colourEnum = ColourType.findColourType(raw.charAt(0));
            }
        ;

colourCycle
        returns[String groupName, String cycleName]
        :   COLOUR_CYCLE group=LCASE COLON cycle=LCASE {
                $groupName = $group.getText();
                $cycleName = $cycle.getText();
            }
        ;

speed
        returns[int speedInt]
        :   SPEED INTEGER { $speedInt = Integer.parseInt($INTEGER.getText()); }
        ;

hitPoints
        returns[int hpInt]
        :   HIT_POINTS INTEGER { $hpInt = Integer.parseInt($INTEGER.getText()); }
        ;

light
        returns[int lightInt]
        :   LIGHT INTEGER { $lightInt = Integer.parseInt($INTEGER.getText()); }
        ;

hearing
        returns[int hearingInt]
        :   HEARING INTEGER { $hearingInt = Integer.parseInt($INTEGER.getText()); }
        ;

smell
        returns[int smellInt]
        :   SMELL INTEGER { $smellInt = Integer.parseInt($INTEGER.getText()); }
        ;

armourClass
        returns[int acInt]
        :   ARMOUR_CLASS INTEGER { $acInt = Integer.parseInt($INTEGER.getText()); }
        ;

sleepiness
        returns[int sleepInt]
        :   SLEEPINESS INTEGER { $sleepInt = Integer.parseInt($INTEGER.getText()); }
        ;

dungeonDepth
        returns[int depthInt]
        :   DEPTH INTEGER { $depthInt = Integer.parseInt($INTEGER.getText()); }
        ;

rarity
        returns[int rarityInt]
        :   RARITY INTEGER { $rarityInt = Integer.parseInt($INTEGER.getText()); }
        ;

experience
        returns[int expInt]
        :   EXPERIENCE INTEGER { $expInt = Integer.parseInt($INTEGER.getText()); }
        ;

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

innateFreq
        returns[int innateFreqInt]
        :   INNATE_FREQ INTEGER { $innateFreqInt = Integer.parseInt($INTEGER.getText()); }
        ;

spellFreq
        returns[int spellFreqInt]
        :   SPELL_FREQ INTEGER { $spellFreqInt = Integer.parseInt($INTEGER.getText()); }
        ;

spellPower
        returns[int spellPowerInt]
        :   SPELL_POWER INTEGER { $spellPowerInt = Integer.parseInt($INTEGER.getText()); }
        ;

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

messageVis
        returns[MonsterAltmsg altMsg]
        :   MESSAGE_VIS UCASE COLON LCASE {
                MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + $UCASE.getText());
                String spellMessage = $LCASE.getText();

                $altMsg = new MonsterAltmsg(spellMessage, MonsterAltmsgType.MON_ALTMSG_SEEN, spellType);
            }
        ;

messageInvis
        returns[MonsterAltmsg altMsg]
        :   MESSAGE_INVIS UCASE COLON LCASE {
                MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + $UCASE.getText());
                String spellMessage = $LCASE.getText();

                $altMsg = new MonsterAltmsg(spellMessage, MonsterAltmsgType.MON_ALTMSG_UNSEEN, spellType);
            }
        ;

messageMiss
        returns[MonsterAltmsg altMsg]
        :   MESSAGE_MISS UCASE COLON LCASE {
                MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + $UCASE.getText());
                String spellMessage = $LCASE.getText();

                $altMsg = new MonsterAltmsg(spellMessage, MonsterAltmsgType.MON_ALTMSG_MISS, spellType);
            }
        ;

desc
        returns[String descStr]
        :   DESC LCASE { $descStr = $LCASE.getText(); }
        ;

shape
        returns[String shapeStr]
        :   SHAPE LCASE { $shapeStr = $LCASE.getText(); }
        ;

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

mimic
        returns[ObjectKind objKind]
        :   MIMIC t=LCASE COLON s=LCASE {
                TValue tval = TValue.valueOf("TV_" + $t.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
                String sval = $s.getText();
                $objKind = GameConstants.lookupObjectKind(tval, sval);
            }
        ;

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
            AngbandDisplayCharacter adcInit = new AngbandDisplayCharacter(glyphInit, colourTypeInit);
            $race = new MonsterRace(nameInit, textInit, pluralInit,
            baseInit, averageHPInit, acInit, sleepInit, hearingInit,
            smellInit, speedInit, lightInit, mexpInit, freqInnateInit,
            freqSpellInit, spellPowerInit, flagsInit, flagsOffInit,
            spellsInit, blowsInit, levelInit, rarityInit, adcInit,
            maxNumInit, curNumInit, spellMsgInit, dropsInit,
            friendsInit, friendsBaseInit, mimicKindsInit, shapesInit,
            numShapesInit, groupNameInit, cycleNameInit);
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

file
        returns[List<MonsterRace> monsterRaces]
        @init {
            $monsterRaces = new ArrayList<>();
        }
        :   (monster {
                $monsterRaces.add($monster.race);
            })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

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

FRIENDS
        :   'friends:' INTEGER COLON DICE_STRING COLON LCASE COLON LCASE
        |   'friends:' INTEGER COLON DICE_STRING COLON LCASE
        ;

FRIENDS_BASE
        :   'friends-base:' INTEGER COLON DICE_STRING COLON LCASE COLON LCASE
        |   'friends-base:' INTEGER COLON DICE_STRING COLON LCASE
        ;

COLOUR_CYCLE
        :   'color-cycle:'
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

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

COLON
        :   ':'
        ;

OR
        :   ' | '
        ;

COLOUR_CHAR
        :   ('d' | 'w' | 's' | 'o' | 'r' | 'g' | 'b' | 'u' | 'D' | 'W' | 'P' | 'y' | 'R'
            | 'G' | 'B' | 'U' | 'p' | 'v' | 't' | 'm' | 'Y' | 'i' | 'T' | 'V' | 'I' | 'M' | 'z' | 'Z')
        ;

UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

LCASE
        :   ('a'..'z' | ' ' | 'A'..'Z' | ',' | '<' | '>' | '.' | '-' |
             '\'' | '!' | '?' | 'é' | '{' | '}' | 'á' | ';' | 'ú' | 'ó' |
             '2'..'9' | '*' | '"' | 'î' | 'ë' | 'û' | '(' | ')' | 'ö' |
             'ô')+
        ;