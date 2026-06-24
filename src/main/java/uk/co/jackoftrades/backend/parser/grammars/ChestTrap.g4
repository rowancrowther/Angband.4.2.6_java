grammar ChestTrap;
import CommentsAndEol;

name
        :   NAME LCASE
        ;

code
        :   CODE UCASE
        ;

level
        :   LEVEL INTEGER
        ;

effect
        :   EFFECT UCASE (COLON UCASE (LCASE COLON (LCASE)?)?)?
        ;

dice
        :   DICE DICE_STRING
        ;

expr    // Not appearing in this file
        :   EXPR LCASE
        ;

effectBlock
        :   effect
        (   dice
        |   expr
        )*;

destroy
        :   DESTROY BOOLEAN
        ;

magic
        :   MAGIC BOOLEAN
        ;

msg
        :   MSG LCASE
        ;

msgDeath
        :   MSG_DEATH LCASE
        ;

chestTrap
        :   name
            code
            level
        (   effect
        |   dice
        |   expr
        |   destroy
        |   magic
        |   msg
        |   msgDeath
        )*
        ;

file
        :   chestTrap+ EOF
        ;

NAME
        :   'name:'
        ;

CODE
        :   'code:'
        ;

LEVEL
        :   'level:'
        ;

EFFECT
        :   'effect:'
        ;

DICE
        :   'dice:'
        ;

EXPR
        :   'expr:'
        ;

DESTROY
        :   'destroy:'
        ;

MAGIC
        :   'magic:'
        ;

MSG
        :   'msg:'
        ;

MSG_DEATH
        :   'msg-death:'
        ;

UCASE
        :   ('A'..'Z' | '_')+
        ;

LCASE
        :   ('a'..'z' | ' ' | 'A'..'Z' | '!')+
        ;

BOOLEAN
        :   ('0' | '1')
        ;

INTEGER
        :   ('0'..'9')+
        ;

DICE_STRING
        :   INTEGER '+' INTEGER 'd' INTEGER
        |   INTEGER 'd' INTEGER
        |   INTEGER '+d' INTEGER
        ;

COLON
        :   ':'
        ;
