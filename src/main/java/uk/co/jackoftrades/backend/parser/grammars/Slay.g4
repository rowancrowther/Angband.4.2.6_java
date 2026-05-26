grammar Slay;

@header {
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.objects.Slay;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;

    import java.util.List;
    import java.util.ArrayList;
}

code
        returns[String codeStr]
        :   CODE FLAG { $codeStr = $FLAG.getText(); }
        ;

name
        returns[String nameStr]
        :   NAME TEXT { $nameStr = $TEXT.getText(); }
        ;

race_flag
        returns[MonsterRaceFlag monRaceFlag]
        :   RACE_FLAG FLAG { $monRaceFlag = MonsterRaceFlag.valueOf("RF_" + $FLAG.getText().trim()); }
        ;

base
        returns[MonsterBase monBase]
        :   BASE BASE_FLAG { $monBase = GameConstants.getMonsterBase($BASE_FLAG.getText()); }
        ;

multiplier
        returns[int multNum]
        :   MULTIPLIER NUMBER { $multNum = Integer.parseInt($NUMBER.getText()); }
        ;

o_multiplier
        returns[int oMultNum]
        :   O_MULTIPLIER NUMBER { $oMultNum = Integer.parseInt($NUMBER.getText()); }
        ;

power
        returns[int powerNum]
        :   POWER NUMBER { $powerNum = Integer.parseInt($NUMBER.getText()); }
        ;

melee_verb
        returns[String meleeVerbStr]
        :   MELEE_VERB TEXT { $meleeVerbStr = $TEXT.getText(); }
        ;

ranged_verb
        returns[String rangedVerbStr]
        :   RANGE_VERB TEXT { $rangedVerbStr = $TEXT.getText(); }
        ;

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
            (race_flag { raceInit = $race_flag.monRaceFlag; })?
            (base { baseInit = $base.monBase; })?
            multiplier { multInit = $multiplier.multNum; }
            o_multiplier { oMultInit = $o_multiplier.oMultNum; }
            power { powerInit = $power.powerNum; }
            melee_verb { meleeInit = $melee_verb.meleeVerbStr; }
            ranged_verb { rangedInit = $ranged_verb.rangedVerbStr; }
        ;

file
        returns[List<Slay> slays]
        @init {
            $slays = new ArrayList<>();
        }
        :   (slay {
            $slays.add($slay.slayObj);
        })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

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

NUMBER
        :   ('0'..'9')+
        ;

FLAG
        :   ('A'..'Z' | '0'..'9' | '_')+
        ;

TEXT
        :   ('a'..'z' | ' ')+
        ;

BASE_FLAG
        :   ('A'..'Z' | 'a'..'z' | ' ')+
        ;