grammar BrandReader;

@header {
    import java.util.ArrayList;

    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.objects.Brand;
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

resistFlag
        returns[MonsterRaceFlag resistFlagEnum]
        :   RESIST TEXT {
            MonsterRaceFlag flag = MonsterRaceFlag.RF_NONE;
            String flagText = "RF_" + $TEXT.getText();
            try {
                flag = MonsterRaceFlag.valueOf(flagText);
            } catch (Exception e) {
                // Do nothing
            }
            $resistFlagEnum = flag;
        };

vulnFlag
        returns[MonsterRaceFlag vulnFlagEnum]
        :   VULNERABLE TEXT {
            MonsterRaceFlag flag = MonsterRaceFlag.RF_NONE;
            String flagText = "RF_" + $TEXT.getText();
            try {
                flag = MonsterRaceFlag.valueOf(flagText);
            } catch (Exception e) {
                // Do nothing
            }
            $vulnFlagEnum = flag;
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

verb
        returns[String verbStr]
        :   VERB TEXT {
            $verbStr = $TEXT.getText();
        };

brand
        returns[Brand brandObject]
        :   code
            name
            verb
            multiplier
            oMultiplier
            power
            resistFlag
            vulnFlag?
            {
            if ($vulnFlag.ctx != null)
                $brandObject = new Brand($code.codeStr, $name.nameStr, $verb.verbStr,
                                        $resistFlag.resistFlagEnum,
                                        $vulnFlag.vulnFlagEnum,
                                        $multiplier.multiplierNum,
                                        $oMultiplier.oMultiplierNum, $power.powerNum);
            else
                $brandObject = new Brand($code.codeStr, $name.nameStr, $verb.verbStr,
                                        $resistFlag.resistFlagEnum,
                                        MonsterRaceFlag.RF_NONE,
                                        $multiplier.multiplierNum,
                                        $oMultiplier.oMultiplierNum, $power.powerNum);
        };

brands
        returns[ArrayList<Brand> brandList]
        @init {
            $brandList = new ArrayList<>();
        }
        :   (brand {
            $brandList.add($brand.brandObject);
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

RESIST
        :   'resist-flag:'
        ;

VULNERABLE
        :   'vuln-flag:'
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

VERB
        :   'verb:'
        ;

NUMBER
        : [0-9]+
        ;

TEXT
        :   [a-zA-Z] [a-zA-Z0-9_ ]*
        ;