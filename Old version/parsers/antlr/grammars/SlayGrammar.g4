grammar SlayGrammar;

@header {
    import java.util.ArrayList;

    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.objects.Slay;
}

code
        returns[String codeStr]
        :   CODE TEXT {
            $codeStr = $TEXT.getText();
        };

name
        returns[String nameStr]
        :   NAME TEXT {
            $nameStr = $TEXT.getText();
        };

raceFlag
        returns[MonsterRaceFlag raceFlagEnum]
        :   RACE_FLAG TEXT {
            MonsterRaceFlag flag = MonsterRaceFlag.RF_NONE;
            String flagText = "RF_" + $TEXT.getText();
            try {
                flag = MonsterRaceFlag.valueOf(flagText);
            } catch (Exception e) {
                // Do nothing
            }
            $raceFlagEnum = flag;
        };

multiplier
        returns[int multiplierNum]
        :   MULTIPLIER NUMBER {
            $multiplierNum = Integer.parseInt($NUMBER.getText());
        };

oMultiplier
        returns[int oMultiplierNum]
        :   O_MULTIPLIER NUMBER {
            $oMultiplierNum = Integer.parseInt($NUMBER.getText());
        };

power
        returns[int powerNum]
        :   POWER NUMBER {
            $powerNum = Integer.parseInt($NUMBER.getText());
        };

meleeVerb
        returns[String melee]
        :   MELEE_VERB TEXT {
            $melee = $TEXT.getText();
        };

rangeVerb
        returns[String ranged]
        :   RANGE_VERB TEXT {
            $ranged = $TEXT.getText();
        };

slay
        returns[Slay slayObject]
        :   code
            name
            raceFlag
            multiplier
            oMultiplier
            power
            meleeVerb
            rangeVerb
            {
            $slayObject = new Slay($code.codeStr, $name.nameStr, null,
                        $meleeVerb.melee, $rangeVerb.ranged,
                        $raceFlag.raceFlagEnum, $multiplier.multiplierNum,
                        $oMultiplier.oMultiplierNum, $power.powerNum);
        };

slays
        returns[ArrayList<Slay> slayList]
        @init {
            $slayList = new ArrayList<>();
        }
        :   (slay {
            $slayList.add($slay.slayObject);
        })+ EOF
        ;

COMMENT
        :   '#' ~':' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* ('\r'?'\n') -> skip
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
        : [0-9]+
        ;

TEXT
        :   [a-zA-Z] [a-zA-Z0-9_ ]*
        ;