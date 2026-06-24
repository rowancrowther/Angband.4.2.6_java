// Parser+lexer for lib/gamedata/monster_base.txt - the monster "templates"
// (dragon, orc, spider, ...) that monster.txt entries are built on: default
// glyph, pain-message set (from pain.txt), inherited race flags, and a
// lore description. Cf. src/mon-init.c: struct file_parser mon_base_parser
// (mon-init.c:1105).
//
// No problems found - verified every monster_base.txt entry has a pain:
// line (matching the mandatory `pain` step) and that `monsterBase`'s fixed
// sequence "name glyph pain flags* desc" matches every record.

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

// "name:<text>" - starts a new template record; this name is referenced by
// monster.txt's base: lines.
name
        returns[String nameStr]
        :   NAME TEXT { $nameStr = $TEXT.getText(); }
        ;

// "glyph:<char>" - default display character for monsters using this template.
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

// "pain:<index>" - which pain.txt message set monsters using this
// template use.
pain
        returns[MonsterPain monPain]
        :   PAIN PAIN_NUMBER {
            int index = Integer.parseInt($PAIN_NUMBER.getText());
            $monPain = GameConstants.lookupMonsterPain(index);
        }
        ;

// "flags:<RF_FLAG> [| <RF_FLAG> ...]" - race flags every monster using this
// template inherits; can repeat (see `monsterBase`'s union accumulation).
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

// "desc:<text>" - lore description shown by the '/' recall command.
desc
        returns[String description]
        @init {
            $description = "";
        }
        :   DESC TEXT { $description = $description + $TEXT.getText(); }
        ;

// One full template record: a fixed sequence of name/glyph/pain, zero-or-
// more flags: lines, then desc.
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

// Top-level rule: the whole file is one or more template records.
file
        returns[List<MonsterBase> bases]
        @init {
            $bases = new ArrayList<>();
        }
        :   (monsterBase { $bases.add($monsterBase.base); })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
// (Also covers the "##### Normal monster templates #####"-style banners.)
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
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

// A bare non-negative integer - the pain-message-set index.
PAIN_NUMBER
        :   ('0'..'9')+
        ;

// Free-running text - used for name:/glyph:/flags:/desc:'s value fields.
TEXT
        :   ('a'..'z' | 'A'..'Z' | '/' | ' ' | '(' | ')' | '?' | ',' | '0'..'9' | '_' | '$' | '@')+
        ;