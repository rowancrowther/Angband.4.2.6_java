grammar PlayerShapeAntlr;

name
        :   NAME TEXT
        ;

combat
        :   COMBAT toHit=TEXT COLON toDam=TEXT COLON toAC=TEXT
        ;

skillDisarmPhys
        :   SKILL_DISARM_PHYS TEXT
        ;

skillDisarmMagic
        :   SKILL_DISARM_MAGIC TEXT
        ;

skillSave
        :   SKILL_SAVE TEXT
        ;

skillStealth
        :   SKILL_STEALTH TEXT
        ;

skillSearch
        :   SKILL_SEARCH TEXT
        ;

skillMelee
        :   SKILL_MELEE TEXT
        ;

skillThrow
        :   SKILL_THROW TEXT
        ;

skillDig
        :   SKILL_DIG TEXT
        ;

objFlags
        :   OBJ_FLAGS TEXT (OR TEXT)*
        ;

playerFlags
        :   PLAYER_FLAGS TEXT (OR TEXT)*
        ;

values
        :   VALUES TEXT LBRACKET TEXT RBRACKET (OR TEXT LBRACKET TEXT RBRACKET)*
        ;

effect
        :   EFFECT TEXT (COLON TEXT)?
        ;

dice
        :   DICE TEXT
        ;

expr
        :   EXPR TEXT COLON TEXT COLON TEXT
        ;

effectMsg
        :   EFFECT_MSG TEXT
        ;

blow
        :   BLOW TEXT
        ;

shape
        :   name
            combat?
            skillDisarmPhys?
            skillDisarmMagic?
            skillSave?
            skillStealth?
            skillSearch?
            skillMelee?
            skillThrow?
            skillDig?
            objFlags*
            playerFlags?
            values*
            effectMsg*
            effect*
            dice?
            expr?
            effect*
            effectMsg?
            effect*
            dice?
            blow*
        ;

shapes
        :   shape+ EOF
        ;

COMMENT
        :   '#' ~':' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:'
        ;

COMBAT
        :   'combat:'
        ;

SKILL_DISARM_PHYS
        :   'skill-disarm-phys:'
        ;

SKILL_DISARM_MAGIC
        :   'skill-disarm-magic:'
        ;

SKILL_SAVE
        :   'skill-save:'
        ;

SKILL_STEALTH
        :   'skill-stealth:'
        ;

SKILL_SEARCH
        :   'skill-search:'
        ;

SKILL_MELEE
        :   'skill-melee:'
        ;

SKILL_THROW
        :   'skill-throw:'
        ;

SKILL_DIG
        :   'skill-dig:'
        ;

OBJ_FLAGS
        :   'obj-flags:'
        ;

PLAYER_FLAGS
        :   'player-flags:'
        ;

VALUES
        :   'values:'
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

EFFECT_MSG
        :   'effect-msg:'
        ;

BLOW
        :   'blow:'
        ;

TEXT
        :   [a-z0-9A-Z _$/+-]+
        ;

COLON
        :   ':'
        ;

OR
        :   '|' | '| ' | ' | ' | ' |'
        ;

LBRACKET
        :   '['
        ;

RBRACKET
        :   ']'
        ;