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

name
        returns[String nameStr]
        :   NAME FLAG { $nameStr = $FLAG.getText(); }
        ;

msgt
        returns[MessageType msgType]
        :   MSGT FLAG { $msgType = MessageType.valueOf("MSG_" + $FLAG.getText().trim()); }
        ;

uniques
        returns[boolean uniquesBool]
        :   UNIQUES BOOLEAN { $uniquesBool = $BOOLEAN.getText().equals("1"); }
        ;

base
        returns[MonsterBase baseObj]
        :   BASE TEXT { $baseObj = GameConstants.getBaseFromName($TEXT.getText()); }
        ;

race_flag
        returns[MonsterRaceFlag raceFlg]
        :   RACE_FLAG FLAG { $raceFlg = MonsterRaceFlag.valueOf("RF_" + $FLAG.getText().trim()); }
        ;

fallback
        returns[String fallbackStr]
        :   FALLBACK FLAG { $fallbackStr = $FLAG.getText(); }
        ;

desc
        returns[String descStr]
        :   DESC TEXT { $descStr = $TEXT.getText(); }
        ;

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

file
        returns[List<Summon> summons]
        @init {
            $summons = new ArrayList<>();
        }
        :   (summon {
            $summons.add($summon.summonObj);
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

BOOLEAN
        :   ('0' | '1')
        ;

FLAG
        :   ('A'..'Z' | '_')+
        ;

TEXT
        :   ('a'..'z' | ' ')+
        ;