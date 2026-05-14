grammar MonsterBaseReader;

@header {
    import java.util.ArrayList;

    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.game.GameEnginee.Game;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterPain;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
}

name
        returns[String nameStr]
        :   NAME TEXT {
            $nameStr = $TEXT.getText();
        };

glyph
        returns[char glyphChar]
        :   GLYPH SINGLE_CHAR {
            $glyphChar = $SINGLE_CHAR.getText().charAt(0);
        };

pain
        returns[MonsterPain painIndex]
        :   PAIN NUMBER {
            int index = Integer.parseInt($NUMBER.getText());
            $painIndex = Game.getPainFromIndex(index);
        };

flags
        returns[ArrayList<MonsterRaceFlag> flagList]
        @init {
            $flagList = new ArrayList<MonsterRaceFlag>();
        }
        :   (FLAGS flag1=TEXT {
            $flagList.add(MonsterRaceFlag.valueOf("RF_" + $flag1.getText().trim()));
        } (OR flag2=TEXT {
            $flagList.add(MonsterRaceFlag.valueOf("RF_" + $flag2.getText().trim()));
        })*)+
        ;

desc
        returns[String descStr]
        :   DESC TEXT {
            $descStr = $TEXT.getText();
        };

entry
        returns[MonsterBase monsterBase]
        @init {
            String nameInit = "";
            char glyphInit = '?';
            MonsterPain painInit = null;
            Flag<MonsterRaceFlag> flagsInit = new Flag<MonsterRaceFlag>(MonsterRaceFlag.class);
            String descInit = "";
        }
        @after {
            $monsterBase = new MonsterBase(nameInit,
            nameInit, flagsInit, glyphInit, painInit,
            descInit);
        }
        :   name {
                nameInit = $name.nameStr;
            }
            glyph {
                glyphInit = $glyph.glyphChar;
            }
            (pain {
                painInit = $pain.painIndex;
            })?
            (flags {
                for (MonsterRaceFlag flg : $flags.flagList) {
                    flagsInit.on(flg);
                }
            })*
            (desc {
                descInit = $desc.descStr;
            })?
        ;

monsterBases
        returns[ArrayList<MonsterBase> entries]
        @init {
            $entries = new ArrayList<>();
        }
        :   (entry {
            $entries.add($entry.monsterBase);
        })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' (' '* '\r'? '\n')* -> skip
        ;

NAME
        :   'name:'
        ;

GLYPH
        :   'glyph:'
        ;

PAIN
        :   'pain:'
        ;

FLAGS
        :   'flags:'
        ;

DESC
        :   'desc:'
        ;

OR
        :   '| '
        ;

NUMBER
        :   [0-9]+
        ;

SINGLE_CHAR
        :   [a-zA-Z0-9@$?,./\\]
        ;

TEXT
        :   [a-zA-Z0-9 ()/.,!'\-_]+
        ;