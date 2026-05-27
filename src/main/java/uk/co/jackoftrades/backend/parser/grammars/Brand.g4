grammar Brand;

@header {
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.objects.Brand;

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

verb
        returns[String verbStr]
        :   VERB TEXT { $verbStr = $TEXT.getText(); }
        ;

resist_flag
        returns[MonsterRaceFlag resFlag]
        :   RESIST_FLAG FLAG { $resFlag = MonsterRaceFlag.valueOf("RF_" + $FLAG.getText().trim()); }
        ;

vuln_flag
        returns[MonsterRaceFlag vulnFlag]
        :   VULN_FLAG FLAG { $vulnFlag = MonsterRaceFlag.valueOf("RF_" + $FLAG.getText().trim()); }
        ;

brand
        returns[Brand brandObj]
        @init {
            String codeInit = "";
            String nameInit = "";
            String verbInit = "";
            MonsterRaceFlag resFlagInit = MonsterRaceFlag.RF_NONE;
            MonsterRaceFlag vulnFlagInit = MonsterRaceFlag.RF_NONE;
            int multInit = 0;
            int oMultInit = 0;
            int powerInit = 0;
        }
        @after {
            $brandObj = new Brand(codeInit, nameInit, verbInit, resFlagInit,
                                  vulnFlagInit, multInit, oMultInit, powerInit);
        }
        :   code { codeInit = $code.codeStr; }
            name { nameInit = $name.nameStr; }
            ( multiplier { multInit = $multiplier.multNum; }
            | o_multiplier { oMultInit = $o_multiplier.oMultNum; }
            | power { powerInit = $power.powerNum; }
            | verb {verbInit = $verb.verbStr; }
            | resist_flag { resFlagInit = $resist_flag.resFlag; }
            | vuln_flag { vulnFlagInit = $vuln_flag.vulnFlag; })*
        ;

file
        returns[List<Brand> brands]
        @init {
            $brands = new ArrayList<>();
        }
        :   (brand {
            $brands.add($brand.brandObj);
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

RESIST_FLAG
        :   'resist-flag:'
        ;

VULN_FLAG
        :   'vuln-flag:'
        ;

NUMBER
        :   ('0'..'9')+
        ;

FLAG
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

TEXT
        :   ('a'..'z')+
        ;