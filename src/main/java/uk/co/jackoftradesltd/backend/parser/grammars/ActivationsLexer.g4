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

/*
 * @author Rowan Crowther
 *
 * Lexer for lib/gamedata/activation.txt: Activatable effects used by
 * objects. Includes whether it needs aiming <boolean>, difficulty level
 * <integer>, power <integer>, effect (from EffectBlock), and a description
 * <String>.
 *
 * Cf. src/obj-init.c: struct file_parser act_parser (obj-init.c:1678),
 * directive table around obj-init.c:1600-1630, and finish_parse_act
 * (obj-init.c:1635) which assigns each activation a sequential index
 * used for array-based lookups elsewhere in the engine.
 *
 * This index is removed, as in Java we are using the object itself from
 * a fixed List, not an array index to a [] array.
 */
lexer grammar ActivationsLexer;
import EffectBlockLexer, CommentsAndEol;

/*
 * @author Rowan Crowther
 *
 * NAME, literal directive keyword followed by pushing into NAME_MODE.
 * cf Activations.g4 name rule
 */
NAME
        :   'name:' -> pushMode(NAME_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * AIM, literal directive keyword - stays in same mode. cf Activations.g4
 * aim rule
 */
AIM
        :   'aim:'
        ;

/*
 * @author Rowan Crowther
 *
 * LEVEL, literal directive keyword - stays in same mode. cf Activations.g4
 * level rule
 */
LEVEL
        :   'level:'
        ;

/*
 * @author Rowan Crowther
 *
 * POWER, literal directive keyword - stays in same mode. cf
 * Activations.g4 power rule
 */
POWER
        :   'power:'
        ;

/*
 * @author Rowan Crowther
 *
 * MSG, literal directive keyword followed by pushing into DESC_MODE
 * This shares DESC_MODE with DESC, as both require a free-text
 * string grabbed up to the end of the line.
 * cf Activations.g4 msg rule
 */
MSG
        :   'msg:' -> pushMode(DESC_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * DESC, literal directive keyword followed by pushing into DESC_MODE
 * This shares DESC_MODE with MSG, as both require a free-text
 * string grabbed up to the end of the line. cf Activations.g4 desc
 * rule
 */
DESC
        :   'desc:' -> pushMode(DESC_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * NAME_MODE a mode to pull in a NAME_STRING which consists of uppercase letters
 * numbers, and the underscore character ('_'). Once that has been pulled in the
 * mode-stack is popped back to DEFAULT_MODE
 */
mode NAME_MODE;

/*
 * @author Rowan Crowther
 *
 * NAME_STRING, a token which consists of uppercase letters
 * numbers, and the underscore character ('_'). Once that has been pulled in the
 * mode-stack is popped back to DEFAULT_MODE. cf Activations.g4 name rule
 */
NAME_STRING
        :   ('A'..'Z' | '0'..'9' | '_')+ -> popMode
        ;

/*
 * @author Rowan Crowther
 *
 * DESC_MODE a mode to pull in a DESC_STRING which is based on a
 * ACTIVATION_FREE_STRING_FRAGMENT wrapped in a DESC_STRING.
 *
 * The FREE_STRING_FRAGMENT has had ACTIVATION_ appended to its
 * front to ensure that it doesn't cause confusion with other
 * grammars going forward.
 */
mode DESC_MODE;

/*
 * @author Rowan Crowther
 *
 * Fragment which pulls everything up to, but not including, the
 * new line character.
 */
fragment ACTIVATION_FREE_STRING_FRAGMENT
        :   (~('\n' | '\r'))+
        ;

/*
 * @author Rowan Crowther
 *
 * Token wrapping ACTIVATION_FREE_STRING_FRAGMENT. cf
 * Activations.g4 desc and msg rules
 */
DESC_STRING
        :   ACTIVATION_FREE_STRING_FRAGMENT -> popMode
        ;