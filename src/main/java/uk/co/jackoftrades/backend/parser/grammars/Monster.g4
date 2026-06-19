grammar Monster;

@header {
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
}

name
        returns[String nameStr]
        :   NAME LCASE { $nameStr = $LCASE.getText(); }
        ;

plural
        returns[String pluralStr]
        :   PLURAL LCASE { $pluralStr = $LCASE.getText(); }
        ;

base
        returns[MonsterBase baseObj]
        :   BASE LCASE {
                String raw = $LCASE.getText();
                $baseObj = GameConstants.getMonsterBase(raw);
            }
        ;

glyph
        returns[char glyphChr]
        :   GLYPH {
                String [] strings = $GLYPH.getText().split(":");
                glyphChr = strings[1].charAt(0);
            }
        ;

colour
        returns[ColourType colourEnum]
        :   COLOUR COLOUR_CHAR {
                String raw = $COLOUR_CHAR.getText();
                $colourEnum = ColourType.findColourType(raw.charAt(0));
            }
        ;

colourCycle
        :   COLOUR_CYCLE LCASE COLON LCASE
        ;

speed
        :   SPEED INTEGER
        ;

hitPoints
        :   HIT_POINTS INTEGER
        ;

light
        :   LIGHT INTEGER
        ;

hearing
        :   HEARING INTEGER
        ;

smell
        :   SMELL INTEGER
        ;

armourClass
        :   ARMOUR_CLASS INTEGER
        ;

sleepiness
        :   SLEEPINESS INTEGER
        ;

depth
        :   DEPTH INTEGER
        ;

rarity
        :   RARITY INTEGER
        ;

experience
        :   EXPERIENCE INTEGER
        ;

blow
        :   BLOW UCASE COLON UCASE COLON DICE_STRING
        |   BLOW UCASE COLON UCASE
        |   BLOW UCASE
        ;

flags
        :   FLAGS
        ;

flagsOff
        :   FLAGS_OFF UCASE (OR UCASE)*
        ;

innateFreq
        :   INNATE_FREQ INTEGER
        ;

spellFreq
        :   SPELL_FREQ INTEGER
        ;

spellPower
        :   SPELL_POWER INTEGER
        ;

spells
        :   SPELLS
        ;

messageVis
        :   MESSAGE_VIS UCASE COLON LCASE
        ;

messageInvis
        :   MESSAGE_INVIS UCASE COLON LCASE
        ;

messageMiss
        :   MESSAGE_MISS UCASE COLON LCASE
        ;

desc
        :   DESC LCASE
        ;

shape
        :   SHAPE LCASE
        ;

drop
        :   DROP LCASE COLON LCASE COLON INTEGER COLON INTEGER COLON INTEGER
        ;

dropBase
        :   DROP_BASE LCASE COLON INTEGER COLON INTEGER COLON INTEGER
        ;

mimic
        :   MIMIC LCASE COLON LCASE
        ;

friends
        :   FRIENDS
        ;

friendsBase
        :   FRIENDS_BASE
        ;

monster
        :   name
            plural?
            base
            glyph?
            colour
        (   colourCycle
        |   speed
        |   hitPoints
        |   light
        |   hearing
        |   smell
        |   armourClass
        |   sleepiness
        |   depth
        |   rarity
        |   experience
        |   blow
        |   flags
        |   flagsOff
        |   innateFreq
        |   spellFreq
        |   spellPower
        |   spells
        |   messageVis
        |   messageInvis
        |   messageMiss
        |   drop
        |   dropBase
        |   mimic
        |   friends
        |   friendsBase
        |   desc
        |   shape)*
        ;

file
        :   monster+ EOF
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

PLURAL
        :   'plural:'
        ;

BASE
        :   'base:'
        ;

GLYPH
        :   'glyph:' ('!' | '?' | '=' | '~')
        ;

COLOUR
        :   'color:'
        ;

SPEED
        :   'speed:'
        ;

HIT_POINTS
        :   'hit-points:'
        ;

LIGHT
        :   'light:'
        ;

HEARING
        :   'hearing:'
        ;

SMELL
        :   'smell:'
        ;

ARMOUR_CLASS
        :   'armor-class:'
        ;

SLEEPINESS
        :   'sleepiness:'
        ;

DEPTH
        :   'depth:'
        ;

RARITY
        :   'rarity:'
        ;

EXPERIENCE
        :   'experience:'
        ;

BLOW
        :   'blow:'
        ;

FLAGS
        :   'flags:' UCASE (' | ' UCASE)*
        ;

FLAGS_OFF
        :   'flags-off:'
        ;

INNATE_FREQ
        :   'innate-freq:'
        ;

SPELL_FREQ
        :   'spell-freq:'
        ;

SPELL_POWER
        :   'spell-power:'
        ;

SPELLS
        :   'spells:' UCASE (' | ' UCASE)*
        ;

MESSAGE_VIS
        :   'message-vis:'
        ;

MESSAGE_INVIS
        :   'message-invis:'
        ;

MESSAGE_MISS
        :   'message-miss:'
        ;

DESC
        :   'desc:'
        ;

DROP
        :   'drop:'
        ;

DROP_BASE
        :   'drop-base:'
        ;

MIMIC
        :   'mimic:'
        ;

SHAPE
        :   'shape:'
        ;

FRIENDS
        :   'friends:' INTEGER COLON DICE_STRING COLON LCASE COLON LCASE
        |   'friends:' INTEGER COLON DICE_STRING COLON LCASE
        ;

FRIENDS_BASE
        :   'friends-base:' INTEGER COLON DICE_STRING COLON LCASE COLON LCASE
        |   'friends-base:' INTEGER COLON DICE_STRING COLON LCASE
        ;

COLOUR_CYCLE
        :   'color-cycle:'
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

DICE_STRING
        :   INTEGER PLUS INTEGER D INTEGER M INTEGER
        |   INTEGER PLUS D INTEGER M INTEGER
        |   INTEGER D INTEGER M INTEGER
        |   D INTEGER M INTEGER
        |   INTEGER M INTEGER
        |   INTEGER PLUS INTEGER D INTEGER
        |   INTEGER PLUS D INTEGER
        |   INTEGER D INTEGER
        |   D INTEGER
        |   INTEGER
        ;

COLON
        :   ':'
        ;

OR
        :   ' | '
        ;

COLOUR_CHAR
        :   ('d' | 'w' | 's' | 'o' | 'r' | 'g' | 'b' | 'u' | 'D' | 'W' | 'P' | 'y' | 'R'
            | 'G' | 'B' | 'U' | 'p' | 'v' | 't' | 'm' | 'Y' | 'i' | 'T' | 'V' | 'I' | 'M' | 'z' | 'Z')
        ;

UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

LCASE
        :   ('a'..'z' | ' ' | 'A'..'Z' | ',' | '<' | '>' | '.' | '-' |
             '\'' | '!' | '?' | 'é' | '{' | '}' | 'á' | ';' | 'ú' | 'ó' |
             '2'..'9' | '*' | '"' | 'î' | 'ë' | 'û' | '(' | ')' | 'ö' |
             'ô')+
        ;

DOLLAR
        :   '$'
        ;

M
        :   'm' | 'M'
        ;

D
        :   'd'
        ;

PLUS
        :   '+'
        ;
