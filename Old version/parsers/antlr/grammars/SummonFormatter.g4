grammar SummonFormatter;

@header     {   import uk.co.jackoftrades.backend.io.parsers.antlr.summon.*;
                import uk.co.jackoftrades.middle.game.GameEnginee.Game;
                import uk.co.jackoftrades.middle.monsters.Summon;
                import uk.co.jackoftrades.middle.monsters.MonsterBase;
                import uk.co.jackoftrades.middle.monsters.MonsterBases;
                import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

                import java.util.ArrayList;
            }

name
        returns[String nameStr]
        :   NAME WORD {
            $nameStr = $WORD.getText();
        };

msgt
        returns[String msgtStr]
        :   MSGTAG WORD {
            $msgtStr = $WORD.getText();
        };

uniques
        returns[boolean unique]
        :   UNIQUES BOOLEAN {
            $unique = $BOOLEAN.getText().equals("1");
        };

base
        returns[MonsterBase mBase]
        :   BASE WORD {
            String baseWord = $WORD.getText();
            $mBase = Game.getBaseFromName(baseWord);
        };

raceFlag
        returns[MonsterRaceFlag mrFlag]
        :   RACE_FLAG WORD {
            String flag = "RF_" + $WORD.getText().trim();
            $mrFlag = MonsterRaceFlag.valueOf(flag);
        };

fallback
        returns[String fallBackName]
        :   FALLBACK WORD {
            $fallBackName = $WORD.getText();
        };

desc
        returns[String description]
        : DESC WORD {
            $description = $WORD.getText();
        };

summon      returns[Summon smn]
            @init {
                String nameTag = "";
                String msgTag = "";
                boolean unique = false;
                ArrayList<MonsterBase> mb = new ArrayList<>();
                MonsterRaceFlag raceFlg = MonsterRaceFlag.RF_NONE;
                String fallBack = "";
                String descStr = "";
            }
            @after {
                $smn = new Summon(nameTag, msgTag, unique, mb, raceFlg, fallBack, descStr);
            }
            :   name        { nameTag  = $name.nameStr; }
                msgt        { msgTag   = $msgt.msgtStr; }
                uniques     { unique   = $uniques.unique; }
                (base       { mb.add($base.mBase); })*
                (raceFlag   { raceFlg  = $raceFlag.mrFlag; })?
                (fallback   { fallBack = $fallback.fallBackName; })?
                desc        { descStr  = $desc.description; }
            ;

file        returns[ArrayList<Summon> summons]
            @init {
                $summons = new ArrayList<>();
            }
            :   (summon {
                $summons.add($summon.smn);
            })+ EOF
            ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   '\r'* '\n' ' '* -> skip
        ;

NAME
        :   'name:'
        ;

MSGTAG
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

WORD
        :   [a-zA-Z_ ]+
        ;

BOOLEAN
        :   ('1' | '0')
        ;