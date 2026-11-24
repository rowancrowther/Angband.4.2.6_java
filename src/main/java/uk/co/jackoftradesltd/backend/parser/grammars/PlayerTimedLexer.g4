/*
 * @author Rowan Crowther
 *
 * Lexer for player_timed.txt. Each directive keyword (name:, grade:, fail:, ...) is its own token
 * that switches the lexer into the mode matching that directive's value shape, so the parser sees
 * cleanly typed value tokens. The modes are:
 *   - the default mode  : directive keywords, and the plain INTEGER of effect-dice: and
 *                         lower-bound:
 *   - FLAG_MODE         : upper-case flag or enum names (name:, msgt:, resist:, brand:, slay:,
 *                         flags:)
 *   - PT_FREE_TEXT_MODE : any text to the end of the line (desc:, on-end:, on-increase:,
 *                         on-decrease:, effect-msg:)
 *   - DELIMITED         : colon-separated fields (grade:, fail:, on-begin-effect:,
 *                         on-end-effect:, flag-synonym:)
 *   - YX_INTEGER_MODE   : the two numbers of effect-yx:
 *   - EXPR_MODE         : the three parts of effect-expr:
 *
 * Every mode other than the default ends on the end of the line, which it skips and then pops back
 * to the default mode. The directive keywords are only recognised in the default mode, so a value
 * never lexes as a keyword.
 *
 * The shared skip rules (comments, blank lines) and the FLAG_BODY and colour-code character
 * classes come from the imported CommentsAndEol, Flags and Colours grammars. DiceStrings supplies
 * the dice-string tokens that effect-dice: takes (SIMPLE_DICE_STRING, such as 1d4, and
 * COMPLEX_DICE_STRING, with $ variables), as C's dice_parse_string reads an effect's dice.
 */
lexer grammar PlayerTimedLexer;

import CommentsAndEol, Flags, Colours, DiceStrings;

/*
 * @author Rowan Crowther
 *
 * `effect-msg:`. The text to the end of the line is read in PT_FREE_TEXT_MODE. The directive adds
 * to the message of the effect opened last.
 *
 * Token EFFECT_MESSAGE coded before 261124, commented in full on 261124.
 */
EFFECT_MESSAGE
        :   'effect-msg:' -> pushMode(PT_FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `name:`. The effect name is read in FLAG_MODE. Opens a record.
 *
 * Token NAME coded before 261124, commented in full on 261124.
 */
NAME
        :   'name:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `desc:`. The text to the end of the line is read in PT_FREE_TEXT_MODE.
 *
 * Token DESC coded before 261124, commented in full on 261124.
 */
DESC
        :   'desc:' -> pushMode(PT_FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `grade:`. The colour, maximum, status and messages are read in DELIMITED mode.
 *
 * Token GRADE coded before 261124, commented in full on 261124.
 */
GRADE
        :   'grade:' -> pushMode(DELIMITED)
        ;

/*
 * @author Rowan Crowther
 *
 * `on-end:`. The text to the end of the line is read in PT_FREE_TEXT_MODE.
 *
 * Token ON_END coded before 261124, commented in full on 261124.
 */
ON_END
        :   'on-end:' -> pushMode(PT_FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `on-increase:`. The text to the end of the line is read in PT_FREE_TEXT_MODE.
 *
 * Token ON_INCREASE coded before 261124, commented in full on 261124.
 */
ON_INCREASE
        :   'on-increase:' -> pushMode(PT_FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `on-decrease:`. The text to the end of the line is read in PT_FREE_TEXT_MODE.
 *
 * Token ON_DECREASE coded before 261124, commented in full on 261124.
 */
ON_DECREASE
        :   'on-decrease:' -> pushMode(PT_FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `msgt:`. The message-type name is read in FLAG_MODE.
 *
 * Token MSGT coded before 261124, commented in full on 261124.
 */
MSGT
        :   'msgt:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `fail:`. The code and flag name are read in DELIMITED mode.
 *
 * Token FAIL coded before 261124, commented in full on 261124.
 */
FAIL
        :   'fail:' -> pushMode(DELIMITED)
        ;

/*
 * @author Rowan Crowther
 *
 * `on-begin-effect:`. The effect name and sub-type are read in DELIMITED mode. Opens a new effect
 * on the begin chain.
 *
 * Token ON_BEGIN_EFFECT coded before 261124, commented in full on 261124.
 */
ON_BEGIN_EFFECT
        :   'on-begin-effect:' -> pushMode(DELIMITED)
        ;

/*
 * @author Rowan Crowther
 *
 * `on-end-effect:`. The effect name and sub-type are read in DELIMITED mode. Opens a new effect on
 * the end chain.
 *
 * Token ON_END_EFFECT coded before 261124, commented in full on 261124.
 */
ON_END_EFFECT
        :   'on-end-effect:' -> pushMode(DELIMITED)
        ;

/*
 * @author Rowan Crowther
 *
 * `effect-dice:`. It stays in the default mode, so the dice string that follows is one of INTEGER,
 * SIMPLE_DICE_STRING or COMPLEX_DICE_STRING (the last two from the DiceStrings import). The line
 * ends on the skip rule of the default mode. Sets the dice of the effect opened last.
 *
 * Token EFFECT_DICE coded before 261124, commented in full on 261124.
 */
EFFECT_DICE
        :   'effect-dice:'
        ;

/*
 * @author Rowan Crowther
 *
 * `resist:`. The element name is read in FLAG_MODE.
 *
 * Token RESIST coded before 261124, commented in full on 261124.
 */
RESIST
        :   'resist:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `brand:`. The brand code is read in FLAG_MODE.
 *
 * Token BRAND coded before 261124, commented in full on 261124.
 */
BRAND
        :   'brand:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `slay:`. The slay code is read in FLAG_MODE.
 *
 * Token SLAY coded before 261124, commented in full on 261124.
 */
SLAY
        :   'slay:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `flag-synonym:`. The object flag code and the "exact" integer are read in DELIMITED mode.
 *
 * Token FLAG_SYNONYM coded before 261124, commented in full on 261124.
 */
FLAG_SYNONYM
        :   'flag-synonym:' -> pushMode(DELIMITED)
        ;

/*
 * @author Rowan Crowther
 *
 * `lower-bound:`. It stays in the default mode, so the number that follows is an INTEGER.
 *
 * Token LOWER_BOUND coded before 261124, commented in full on 261124.
 */
LOWER_BOUND
        :   'lower-bound:'
        ;

/*
 * @author Rowan Crowther
 *
 * `flags:`. The flag names, and the blanks and bars between them, are read in FLAG_MODE.
 *
 * Token FLAGS coded before 261124, commented in full on 261124.
 */
FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `effect-yx:`. The two numbers are read in YX_INTEGER_MODE. Sets y and x of the effect opened
 * last.
 *
 * Token EFFECT_YX coded before 261124, commented in full on 261124.
 */
EFFECT_YX
        :   'effect-yx:' -> pushMode(YX_INTEGER_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * `effect-expr:`. The variable, base and operations are read in EXPR_MODE. The spelling has a
 * hyphen, as in C; `effect_expr:` with an underscore is not a directive. Binds a $ variable of the
 * dice of the effect opened last.
 *
 * Token EFFECT_EXPR coded before 261124, commented in full on 261124.
 */
EFFECT_EXPR
        :   'effect-expr:' -> pushMode(EXPR_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * An optionally negative whole number in the default mode: the value of `effect-dice:` when it is
 * a plain number, and of `lower-bound:`.
 *
 * Token INTEGER coded before 261124, commented in full on 261124.
 */
INTEGER
        :   '-'? ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * The three parts of an `effect-expr:` line: a one-letter variable, a base name and an operations
 * string, separated by colons, ending on the end of the line.
 *
 * Mode EXPR_MODE coded before 261124, commented in full on 261124.
 */
mode EXPR_MODE;

/*
 * @author Rowan Crowther
 *
 * The single-letter variable (normally one of B, S, D or M) of an `effect-expr:` line. It picks
 * which $ variable of the dice string is replaced by the value computed from the base and
 * operations that follow.
 *
 * Token EXPR_CHAR coded before 261124, commented in full on 261124.
 */
EXPR_CHAR
        :   ('A'..'Z')
        ;

/*
 * @author Rowan Crowther
 *
 * The colon between the fields of an `effect-expr:` line.
 *
 * Token EXPR_COLON coded before 261124, commented in full on 261124.
 */
EXPR_COLON
        :   ':'
        ;

/*
 * @author Rowan Crowther
 *
 * The base name of an `effect-expr:` line: capital letters and underscores. The lexer does not
 * check it against the known base types; that happens when the effect is assembled.
 *
 * Token EXPR_UCASE coded before 261124, commented in full on 261124.
 */
EXPR_UCASE
        :   ('A'..'Z' | '_')+
        ;

/*
 * @author Rowan Crowther
 *
 * The operations of an `effect-expr:` line: one of / * + - and then everything to the end of the
 * line, kept as one token.
 *
 * Token EXPR_OP coded before 261124, commented in full on 261124.
 */
EXPR_OP
        :   ('/' | '*' | '+' | '-') ~('\r' | '\n')+
        ;

/*
 * @author Rowan Crowther
 *
 * The end of an `effect-expr:` line. Pops back to the default mode and skips the line end.
 *
 * Token EXPR_EOL coded before 261124, commented in full on 261124.
 */
EXPR_EOL
        :   NL -> popMode, skip
        ;

/*
 * @author Rowan Crowther
 *
 * Upper-case flag or enum names, and the blanks and bars that separate them in a `flags:` list,
 * ending on the end of the line. It is shared by the single-value directives (name:, msgt:,
 * resist:, brand:, slay:), so a second name on one of those lines lexes as FLAG_OR then FLAG and
 * the parser refuses it.
 *
 * Mode FLAG_MODE coded before 261124, commented in full on 261124.
 */
mode FLAG_MODE;

/*
 * @author Rowan Crowther
 *
 * One flag or enum name, as FLAG_BODY from the imported Flags grammar defines it.
 *
 * Token FLAG coded before 261124, commented in full on 261124.
 */
FLAG
        :   FLAG_BODY
        ;

/*
 * @author Rowan Crowther
 *
 * The separator between flags: blanks, bars, or both in either order, so `A B`, `A|B`, `A | B` and
 * `A||B` all separate. A run with no flag beside it is still a FLAG_OR, which is why the `flags`
 * rule allows one before the first name or after the last.
 *
 * Token FLAG_OR coded before 261124, commented in full on 261124.
 */
FLAG_OR
        :   ' '+ '|'* ' '*
        |   ' '* '|'+ ' '*
        ;

/*
 * @author Rowan Crowther
 *
 * The end of a flag-mode line. Pops back to the default mode and skips the line end.
 *
 * Token FLAG_EOL coded before 261124, commented in full on 261124.
 */
FLAG_EOL
        :   NL -> skip, popMode
        ;

/*
 * @author Rowan Crowther
 *
 * Any text from here to the end of the line.
 *
 * Mode PT_FREE_TEXT_MODE coded before 261124, commented in full on 261124.
 */
mode PT_FREE_TEXT_MODE;

/*
 * @author Rowan Crowther
 *
 * The text of a line, from the first character to the end of the line (not including it).
 *
 * Token STRING coded before 261124, commented in full on 261124.
 */
STRING
        :   ~('\r' | '\n')+
        ;

/*
 * @author Rowan Crowther
 *
 * The end of a free-text line. Pops back to the default mode and skips the line end.
 *
 * Token PT_FREE_TEXT_MODE_EOL coded before 261124, commented in full on 261124.
 */
PT_FREE_TEXT_MODE_EOL
        :   NL -> skip, popMode
        ;

/*
 * @author Rowan Crowther
 *
 * Colon-separated fields. A field that is only a number is a DELIM_INTEGER, and one that is a
 * single colour-code letter, such as B, is a DELIM_COLOUR_CODE. Both rules come before
 * DELIM_BETWEEN_COLONS and win the tie, so `on-end-effect:A:B` lexes B as a colour code and the
 * parser, which expects a name there, refuses it. A is not a colour code and lexes as
 * DELIM_BETWEEN_COLONS.
 *
 * Mode DELIMITED coded before 261124, commented in full on 261124.
 */
mode DELIMITED;

/*
 * @author Rowan Crowther
 *
 * An optionally negative whole number between colons.
 *
 * Token DELIM_INTEGER coded before 261124, commented in full on 261124.
 */
DELIM_INTEGER
        :   '-'? ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * A colour code, as COLOUR_CODE from the imported Colours grammar defines it.
 *
 * Token DELIM_COLOUR_CODE coded before 261124, commented in full on 261124.
 */
DELIM_COLOUR_CODE
        :   COLOUR_CODE
        ;

/*
 * @author Rowan Crowther
 *
 * Any run of characters up to the next colon or the end of the line.
 *
 * Token DELIM_BETWEEN_COLONS coded before 261124, commented in full on 261124.
 */
DELIM_BETWEEN_COLONS
        :   ~('\n' | '\r' | ':')+
        ;

/*
 * @author Rowan Crowther
 *
 * The colon between the fields of a delimited line.
 *
 * Token DELIM_COLON coded before 261124, commented in full on 261124.
 */
DELIM_COLON
        :   ':'
        ;

/*
 * @author Rowan Crowther
 *
 * The end of a delimited line. Pops back to the default mode and skips the line end.
 *
 * Token DELIM_EOL coded before 261124, commented in full on 261124.
 */
DELIM_EOL
        :   NL -> skip, popMode
        ;

/*
 * @author Rowan Crowther
 *
 * The two numbers of an `effect-yx:` line, separated by a colon (which is skipped) and ending on
 * the end of the line.
 *
 * Mode YX_INTEGER_MODE coded before 261124, commented in full on 261124.
 */
mode YX_INTEGER_MODE;

/*
 * @author Rowan Crowther
 *
 * One number of an `effect-yx:` line: an optional sign, an optional 0x prefix and hexadecimal
 * digits, then anything up to the next colon or the end of the line. Whether the whole token is a
 * valid number is decided when the effect is assembled, not here.
 *
 * Token YX_INTEGER coded before 261124, commented in full on 261124.
 */
YX_INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

/*
 * @author Rowan Crowther
 *
 * The colon between the y and x numbers. Skipped.
 *
 * Token YX_COLON coded before 261124, commented in full on 261124.
 */
YX_COLON
        :   ':' -> skip
        ;

/*
 * @author Rowan Crowther
 *
 * The end of an `effect-yx:` line. Pops back to the default mode and skips the line end.
 *
 * Token YX_EOL coded before 261124, commented in full on 261124.
 */
YX_EOL
        :   NL -> skip, popMode
        ;
