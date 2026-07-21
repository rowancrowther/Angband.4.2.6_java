parser grammar MonsterGrammar;

options { tokenVocab = MonsterLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.monster.MonsterParseRecord;
    import uk.co.jackoftrades.backend.parser.monster.MonsterParseRecord.*;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

recordCount
        returns[String count]
        :   RECORD_COUNT MON_INTEGER { $count = $MON_INTEGER.getText(); }
        ;
name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText(); $line = $start.getLine(); }
        ;

plural
        returns[String pluralStr]
        :   PLURAL STRING { $pluralStr = $STRING.getText(); }
        ;

base
        returns[String baseStr]
        :   BASE STRING { $baseStr = $STRING.getText(); }
        ;

glyph
        returns[String glyphStr]
        :   GLYPH STRING { $glyphStr = $STRING.getText(); }
        ;

colour
        returns[String colourStr]
        :   COLOUR COLOUR_CHAR { $colourStr = $COLOUR_CHAR.getText(); }
        ;

speed
        returns[String speedVal]
        :   SPEED MON_INTEGER { $speedVal = $MON_INTEGER.getText(); }
        ;

hitPoints
        returns[String hitPointsVal]
        :   HIT_POINTS MON_INTEGER { $hitPointsVal = $MON_INTEGER.getText(); }
        ;

light
        returns[String lightVal]
        :   LIGHT MON_INTEGER { $lightVal = $MON_INTEGER.getText(); }
        ;

hearing
        returns[String hearingVal]
        :   HEARING MON_INTEGER { $hearingVal = $MON_INTEGER.getText(); }
        ;

smell
        returns[String smellVal]
        :   SMELL MON_INTEGER { $smellVal = $MON_INTEGER.getText(); }
        ;

shape
        returns[String shapeStr]
        :   SHAPE STRING { $shapeStr = $STRING.getText(); }
        ;

colourCycle
        returns[String groupStr, String nameStr]
        :   COLOUR_CYCLE g=DELIMITED_STRING COLON n=DELIMITED_STRING {
            $groupStr = $g.getText(); $nameStr = $n.getText();
        };

armourClass
        returns[String armourClassVal]
        :   ARMOUR_CLASS MON_INTEGER { $armourClassVal = $MON_INTEGER.getText(); }
        ;

sleepiness
        returns[String sleepinessVal]
        :   SLEEPINESS MON_INTEGER { $sleepinessVal = $MON_INTEGER.getText(); }
        ;

depthLevel
        returns[String depthVal]
        :   DEPTH MON_INTEGER { $depthVal = $MON_INTEGER.getText(); }
        ;

rarity
        returns[String rarityVal]
        :   RARITY MON_INTEGER { $rarityVal = $MON_INTEGER.getText(); }
        ;

experience
        returns[String experienceVal]
        :   EXPERIENCE MON_INTEGER { $experienceVal = $MON_INTEGER.getText(); }
        ;

blow
        returns[String blowType, String blowSubType, String blowDice]
        :   BLOW t=BLOW_WORD { $blowType = $t.getText();
                               $blowSubType = "";
                               $blowDice = ""; }
            (BLOW_COLON s=BLOW_WORD { $blowSubType = $s.getText(); }
            (BLOW_COLON d=BLOW_DICESTRING { $blowDice = $d.getText(); })?)?
        ;

flags
        returns[List<String> flagsList]
        @init {
            $flagsList = new ArrayList<>();
        }
        :   FLAGS f1=FLAG { $flagsList.add($f1.getText()); }
            (FLAG_OR f2=FLAG { $flagsList.add($f2.getText()); })*
        ;

flagsOff
        returns[List<String> flagsList]
        @init {
            $flagsList = new ArrayList<>();
        }
        :   FLAGS_OFF f1=FLAG { $flagsList.add($f1.getText()); }
            (FLAG_OR f2=FLAG { $flagsList.add($f2.getText()); })*
        ;

innateFreq
        returns[String innateFreqVal]
        :   INNATE_FREQ MON_INTEGER { $innateFreqVal = $MON_INTEGER.getText(); }
        ;

spellFreq
        returns[String spellFreqVal]
        :   SPELL_FREQ MON_INTEGER { $spellFreqVal = $MON_INTEGER.getText(); }
        ;

spellPower
        returns[String spellPowerVal]
        :   SPELL_POWER MON_INTEGER { $spellPowerVal = $MON_INTEGER.getText(); }
        ;

spells
        returns[List<String> spellList]
        @init {
            $spellList = new ArrayList<>();
        }
        :   SPELLS s1=FLAG { $spellList.add($s1.getText()); }
            (FLAG_OR s2=FLAG { $spellList.add($s2.getText()); })*
        ;

messageVis
        returns[String spell, String line]
        :   MESSAGE_VIS s=DELIMITED_STRING { $spell = $s.getText(); }
            COLON l=DELIMITED_STRING { $line = $l.getText(); }
        ;

messageInvis
        returns[String spell, String line]
        :   MESSAGE_INVIS s=DELIMITED_STRING { $spell = $s.getText(); }
            COLON l=DELIMITED_STRING { $line = $l.getText(); }
        ;

messageMiss
        returns[String spell, String line]
        :   MESSAGE_MISS s=DELIMITED_STRING { $spell = $s.getText(); }
            COLON l=DELIMITED_STRING { $line = $l.getText(); }
        ;

desc
        returns[String line]
        :   DESC STRING { $line = $STRING.getText(); }
        ;

drop
        returns[String typeStr, String nameStr, String chanceVal, String min, String max]
        :   DROP t=DROP_STRING DROP_COLON s=DROP_STRING DROP_COLON
            c=DROP_INTEGER DROP_COLON n=DROP_INTEGER DROP_COLON
            x=DROP_INTEGER {
                $typeStr   = $t.getText();
                $nameStr   = $s.getText();
                $chanceVal = $c.getText();
                $min       = $n.getText();
                $max       = $x.getText();
            }
        ;

dropBase
        returns[String typeStr, String chanceVal, String min, String max]
        :   DROP_BASE t=DROP_STRING DROP_COLON
            c=DROP_INTEGER DROP_COLON n=DROP_INTEGER DROP_COLON
            x=DROP_INTEGER {
                $typeStr   = $t.getText();
                $chanceVal = $c.getText();
                $min       = $n.getText();
                $max       = $x.getText();
            }
        ;

mimic
        returns[String tVal, String sVal]
        :   MIMIC t=DELIMITED_STRING COLON s=DELIMITED_STRING {
            $tVal = $t.getText();
            $sVal = $s.getText(); }
        ;

friends
        returns[String chance, String number, String nameStr, String role]
        @init {
            $chance = "";
            $number = "";
            $nameStr = "";
            $role = "";
        }
        :   FRIENDS c=FRIEND_INTEGER FRIEND_COLON v=FRIEND_DICE FRIEND_COLON
            n=FRIEND_NAME { $chance  = $c.getText();
                            $number  = $v.getText();
                            $nameStr = $n.getText(); }
            (FRIEND_COLON r=FRIEND_NAME { $role = $r.getText(); })?
        ;

friendsBase
        returns[String chance, String number, String nameStr, String role]
        @init {
            $chance = "";
            $number = "";
            $nameStr = "";
            $role = "";
        }
        :   FRIENDS_BASE c=FRIEND_INTEGER FRIEND_COLON v=FRIEND_DICE FRIEND_COLON
            n=FRIEND_NAME { $chance  = $c.getText();
                            $number  = $v.getText();
                            $nameStr = $n.getText(); }
            (FRIEND_COLON r=FRIEND_NAME { $role = $r.getText(); })?
        ;

monster
        returns[MonsterParseRecord monsterRecord]
        @init {
            String nameInit = "";
            String pluralInit = "";
            String baseInit = "";
            String glyphInit = "";
            String colourInit = "";
            String speedInit = "";
            String hitPointsInit = "";
            String lightInit = "";
            String hearingInit = "";
            String smellInit = "";
            List<String> shapeInit = new ArrayList<>();
            String colourCycleGroupInit = "";
            String colourCycleNameInit = "";
            String armourClassInit = "";
            String sleepinessInit = "";
            String depthInit = "";
            String rarityInit = "";
            String experienceInit = "";
            List<MonsterBlowParseRecord> blowsList = new ArrayList<>();
            List<String> flagsList = new ArrayList<>();
            List<String> flagsOffList = new ArrayList<>();
            String innateFreqInit = "";
            String spellFreqInit = "";
            String spellPowerInit = "";
            List<String> spellsList = new ArrayList<>();
            Map<String, String> visSB = new HashMap<>();
            Map<String, String> invisSB = new HashMap<>();
            Map<String, String> missSB = new HashMap<>();
            StringBuilder descSB = new StringBuilder();
            List<MonsterDropParseRecord> dropsList = new ArrayList<>();
            List<MonsterDropBaseParseRecord> dropsBaseList = new ArrayList<>();
            List<MonsterMimicParseRecord> mimics = new ArrayList();
            List<MonsterFriendsParseRecord> friendsList = new ArrayList<>();
            List<MonsterFriendsParseRecord> friendsBaseList = new ArrayList<>();
            int line = 0;
        }
        @after {
            $monsterRecord = new MonsterParseRecord(nameInit, pluralInit, baseInit,
                glyphInit, colourInit, speedInit, hitPointsInit, lightInit, hearingInit,
                smellInit, shapeInit, colourCycleGroupInit, colourCycleNameInit,
                armourClassInit, sleepinessInit, depthInit, rarityInit, experienceInit,
                blowsList, flagsList, flagsOffList, innateFreqInit, spellFreqInit,
                spellPowerInit, spellsList, visSB, invisSB, missSB,
                descSB.toString(), dropsList, dropsBaseList, mimics, friendsList,
                friendsBaseList, line);
        }
        :   name { nameInit = $name.nameStr; line = $start.getLine(); }
        (   plural { pluralInit = $plural.pluralStr; }
        |   base { baseInit = $base.baseStr; }
        |   glyph { glyphInit = $glyph.glyphStr; }
        |   colour { colourInit = $colour.colourStr; }
        |   speed { speedInit = $speed.speedVal; }
        |   hitPoints { hitPointsInit = $hitPoints.hitPointsVal; }
        |   light { lightInit = $light.lightVal; }
        |   hearing { hearingInit = $hearing.hearingVal; }
        |   smell { smellInit = $smell.smellVal; }
        |   shape { shapeInit.add($shape.shapeStr); }
        |   colourCycle { colourCycleGroupInit = $colourCycle.groupStr;
                          colourCycleNameInit = $colourCycle.nameStr; }
        |   armourClass { armourClassInit = $armourClass.armourClassVal; }
        |   sleepiness { sleepinessInit = $sleepiness.sleepinessVal; }
        |   depthLevel { depthInit = $depthLevel.depthVal; }
        |   rarity { rarityInit = $rarity.rarityVal; }
        |   experience { experienceInit = $experience.experienceVal; }
        |   blow { blowsList.add(new MonsterParseRecord.MonsterBlowParseRecord($blow.blowType,
                $blow.blowSubType, $blow.blowDice)); }
        |   flags { flagsList.addAll($flags.flagsList); }
        |   flagsOff { flagsOffList.addAll($flagsOff.flagsList); }
        |   innateFreq { innateFreqInit = $innateFreq.innateFreqVal; }
        |   spellFreq { spellFreqInit = $spellFreq.spellFreqVal; }
        |   spellPower { spellPowerInit = $spellPower.spellPowerVal; }
        |   spells { spellsList.addAll($spells.spellList); }
        |   messageVis { visSB.put($messageVis.spell, $messageVis.line); }
        |   messageInvis { invisSB.put($messageInvis.spell, $messageInvis.line); }
        |   messageMiss { missSB.put($messageMiss.spell, $messageMiss.line); }
        |   desc { descSB.append($desc.line); }
        |   drop { dropsList.add(new MonsterDropParseRecord($drop.typeStr,
                $drop.nameStr, $drop.chanceVal, $drop.min, $drop.max)); }
        |   dropBase  { dropsBaseList.add(new MonsterDropBaseParseRecord($dropBase.typeStr,
                $dropBase.chanceVal, $dropBase.min, $dropBase.max)); }
        |   mimic { mimics.add(new MonsterMimicParseRecord($mimic.tVal, $mimic.sVal)); }
        |   friends { friendsList.add(new MonsterFriendsParseRecord($friends.chance,
                $friends.number, $friends.nameStr, $friends.role)); }
        |   friendsBase { friendsBaseList.add(new MonsterFriendsParseRecord($friendsBase.chance,
                $friendsBase.number, $friendsBase.nameStr, $friendsBase.role)); }
        )+;

file
        returns[String declaredRecordCount, List<MonsterParseRecord> monsters]
        @init {
            $monsters = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (monster { $monsters.add($monster.monsterRecord); })+ EOF
        ;