grammar MonsterBase;

@header {
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterPain;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;

    import java.util.List;
    import java.util.ArrayList;
}

name
        returns[String nameStr]
        :   NAME TEXT { $nameStr = $TEXT.getText(); }
        ;

glyph
        returns[char glyphChar]
        :   GLYPH TEXT {
            String raw = $TEXT.getText();

            if (raw.length() != 1)
                throw new IllegalArgumentException(
                    "Glyph must be exactly one character while parsing MonsterBases"
                );

            $glyphChar = raw.charAt(0);
        }
        ;

pain
        returns[MonsterPain monPain]
        :   PAIN PAIN_NUMBER {
            int index = Integer.parseInt($PAIN_NUMBER.getText());
            $monPain = GameConstants.lookupMonsterPain(index);
        }
        ;

flags
        returns[Flag<MonsterRaceFlag> raceFlags]
        @init {
            $raceFlags = new Flag<>(MonsterRaceFlag.class);
        }
        :   FLAGS flag1=TEXT {
                $raceFlags.on(MonsterRaceFlag.valueOf("RF_" + $flag1.getText().trim()));
            }
            ('|' flag2=TEXT {
                $raceFlags.on(MonsterRaceFlag.valueOf("RF_" + $flag2.getText().trim()));
            })*
        ;

desc
        returns[String description]
        @init {
            $description = "";
        }
        :   DESC TEXT { $description = $description + $TEXT.getText(); }
        ;

monsterBase
        returns[MonsterBase base]
        @init {
            String nameInit = "";
            char glyphInit = '\0';
            MonsterPain monPainInit = null;
            Flag<MonsterRaceFlag> flagsInit = new Flag<>(MonsterRaceFlag.class);
            String descInit = "";
        }
        @after{
            $base = new MonsterBase(nameInit, nameInit, flagsInit,
                                    glyphInit, monPainInit, descInit);
        }
        :   name { nameInit = $name.nameStr; }
            glyph { glyphInit = $glyph.glyphChar; }
            pain { monPainInit = $pain.monPain; }
            (flags {
                flagsInit.union($flags.raceFlags);
            })*
            desc { descInit = descInit + $desc.description; }
        ;

file
        returns[List<MonsterBase> bases]
        @init {
            $bases = new ArrayList<>();
        }
        :   (monsterBase { $bases.add($monsterBase.base); })+ EOF
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
        : 'desc:'
        ;

PAIN_NUMBER
        :   ('0'..'9')+
        ;

TEXT
        :   ('a'..'z' | 'A'..'Z' | '/' | ' ' | '(' | ')' | '?' | ',' | '0'..'9' | '_' | '$' | '@')+
        ;