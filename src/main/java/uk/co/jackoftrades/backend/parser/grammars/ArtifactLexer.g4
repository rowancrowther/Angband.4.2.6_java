lexer grammar ArtifactLexer;

import AngbandDisplayCharacter, CommentsAndEol, Flags, DiceStrings;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

BASE_OBJECT
        :   'base-object:' -> pushMode(DELIMITED_STRINGS)
        ;

LEVEL
        :   'level:'
        ;

WEIGHT
        :   'weight:'
        ;

COST
        :   'cost:'
        ;

ALLOC
        :   'alloc:' -> pushMode(ALLOC_MODE)
        ;

ATTACK
        :   'attack:'
        ;

ARMOUR
        :   'armor:'
        ;

FLAGS
        :   'flags:' ' '? -> pushMode(FLAGS_MODE)
        ;

ACT
        :   'act:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

TIME
        :   'time:'
        ;

MSG
        :   'msg:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

VALUES
        :   'values:' -> pushMode(VALUES_MODE)
        ;

BRAND
        :   'brand:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

SLAY
        :   'slay:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

CURSE
        :   'curse:' -> pushMode(DELIMITED_STRINGS)
        ;

DESC
        :   'desc:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

ARTIFACT_COLON
        :   ':'
        ;

ARTIFACT_INTEGER
        :   INT_FRAGMENT
        ;

fragment INT_FRAGMENT
        :   '-'? ('0'..'9')+
        ;

fragment EOL_POP_MODE
        :   '\r'* '\n'
        ;

mode ALLOC_MODE;

ALLOC_INT
        :   INT_FRAGMENT
        ;

ALLOC_COLON
        :   ':'
        ;

ALLOC_TO
        :   ' to '
        ;

ALLOC_EOL
        :   EOL_POP_MODE -> skip, popMode
        ;

mode ARTIFACT_FREE_TEXT;

STRING
        :   ~('\n' | '\r')+
        ;

ARTIFACT_FREE_TEXT_EOL
        :   EOL_POP_MODE -> skip, popMode
        ;

mode DELIMITED_STRINGS;

DELIM_INTEGER
        :   '-'? ('0'..'9')+
        ;

STRING_BETWEEN_COLONS
        :   ~('\n' | '\r' | ':')+
        ;

DELIM_COLON
        :   ':'
        ;

END_SKIP
        :   EOL_POP_MODE -> skip, popMode
        ;

mode FLAGS_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   EOL_POP_MODE -> skip, popMode
        ;

mode VALUES_MODE;

VALUES_STRING
        :   FLAG_BODY
        ;

VALUES_LBRACKET
        :   '['
        ;

VALUES_RBRACKET
        :   ']'
        ;

VALUES_INTEGER
        :   INT_FRAGMENT
        ;

VALUES_OR
        :   ' '? '|' ' '?
        ;

VALUES_EOL
        :   EOL_POP_MODE -> skip, popMode
        ;
