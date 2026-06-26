/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

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
