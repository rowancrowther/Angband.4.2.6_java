// Lexer for lib/gamedata/object_property.txt (see ObjectProperty2Grammar for the
// full file description). Directive keywords are matched in the default mode;
// nine other modes handle the single-word, free-form, numeric and colon-delimited value shapes. The
// lexer only splits each line into tokens: it decides nothing about what a value means,
// so a number is just text that looks like one until the assembler reads it.
//
//   LCASE_MODE and UCASE_MODE - the value of type: (one lower-case word, LCASE) and of code: (one
//     upper-case name, UCASE, digits allowed as in DIG_1). Each ends only on a bare line end
//     (NEWLINE), where the other modes end on NL, which also takes blanks. A blank or tab after the
//     value therefore has no rule, and is a lexer error that refuses the file. C reads both fields
//     as "str", which keeps the blank, so "flag " and "FREE_ACT " match no table entry and C
//     refuses them too.
//   FREE_TEXT_MODE - the value is arbitrary text to end-of-line (STRING). Entered
//     by name:/subtype:/id-type:/adjective:/neg-adjective:/msg:/desc:, whose values contain
//     spaces, capitals and punctuation (e.g. "sustain strength", "misc ability", "curse-only",
//     "Slows your metabolism") that LCASE_MODE and UCASE_MODE cannot match. A trailing blank is
//     part of the value, as it is for C's "str".
//   DELIMITER_INTRO and DELIMITER_POWER - the colon-separated "sym : int" of type-mult:. The symbol
//     may itself contain a space ("dragon armor") or be a number, so it is one token, DELIM_STRING,
//     and the figure after the colon is another, DELIM_INTEGER.
//   INTEGER_MODE - the value of power:/mult: is one INTEGER (optional sign, optional 0x
//     prefix, then digits or hex letters), after which the lexer pops back to the default
//     mode. Whether the text is a number C's strtol would read is decided later, by
//     ParseUtils in the assembler.
//   BINDUI_INTRO_MODE, BINDUI_TAG_MODE and BINDUI_INTEGER_MODE - the three stages of
//     bindui:<name>[<TAG>]:<aux>[:<uival>]. The entry name comes first (any text up to a ':' or
//     a '<'), then an optional tag between '<' and '>', then one or two colon-separated integers;
//     the newline returns to the default mode. The separators are skipped, so the parser
//     sees only the name, the tag and the integers. C reads the whole of "name<TAG>" as one
//     sym; the assembler joins the two back together.
//
// FLAG_BODY (an upper-case name allowing digits, e.g. DIG_1/LIGHT_2) comes from the
// shared Flags import and backs UCASE; CommentsAndEol supplies comment/EOL skipping. Its NL
// fragment takes any blanks and tabs before a line end and, since 261115, blanks that run to the
// end of a file with no final newline. That is why a trailing blank after a figure is accepted, as
// C's strtol accepts it, wherever a rule below ends on NL.
lexer grammar ObjectProperty2Lexer;

import CommentsAndEol, Flags;

// "name:<text>" - opens a record. The value is free text to the end of the line, so the lexer pushes
// FREE_TEXT_MODE. C registers it as a "str".
//
// Token NAME coded on 261114, commented in full on 261114.
NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

// "type:<word>" - the category. The value is one lower-case word, so the lexer pushes LCASE_MODE,
// which reads it as LCASE and refuses a blank after it.
//
// Token TYPE coded on 261114, commented in full on 261114 (updated 261115 for LCASE_MODE).
TYPE
        :   'type:' -> pushMode(LCASE_MODE)
        ;

// "subtype:<text>" - the grouping of a flag. The value may hold a space or a hyphen ("misc ability",
// "curse-only"), so the lexer pushes FREE_TEXT_MODE.
//
// Token SUBTYPE coded on 261114, commented in full on 261114.
SUBTYPE
        :   'subtype:' -> pushMode(FREE_TEXT_MODE)
        ;

// "id-type:<text>" - how a property is identified ("on effect", "timed", "on wield"). The value
// holds spaces, so the lexer pushes FREE_TEXT_MODE.
//
// Token ID_TYPE coded on 261114, commented in full on 261114.
ID_TYPE
        :   'id-type:' -> pushMode(FREE_TEXT_MODE)
        ;

// "code:<NAME>" - the stat, modifier, flag or element the property stands for. The value is one
// upper-case name, so the lexer pushes UCASE_MODE, which reads it as UCASE and refuses a blank
// after it.
//
// Token CODE coded on 261114, commented in full on 261114 (updated 261115 for UCASE_MODE).
CODE
        :   'code:' -> pushMode(UCASE_MODE)
        ;

// "power:<n>" - the figure that follows is read in INTEGER_MODE.
//
// Token POWER coded on 261114, commented in full on 261114.
POWER
        :   'power:' -> pushMode(INTEGER_MODE)
        ;

// "mult:<n>" - the figure that follows is read in INTEGER_MODE.
//
// Token MULT coded on 261114, commented in full on 261114.
MULT
        :   'mult:' -> pushMode(INTEGER_MODE)
        ;

// "type-mult:<tval>:<n>" - the tval name and the multiplier are read in DELIMITER mode.
//
// Token TYPE_MULT coded on 261114, commented in full on 261114.
TYPE_MULT
        :   'type-mult:' -> mode(DELIMITER_INTRO)
        ;

// "adjective:<text>" - the value is free text to the end of the line (C's str), so the lexer pushes
// FREE_TEXT_MODE. An adjective may hold capitals, blanks and hyphens.
//
// Token ADJECTIVE coded on 261114, commented in full on 261114.
ADJECTIVE
        :   'adjective:' -> pushMode(FREE_TEXT_MODE)
        ;

// "neg-adjective:<text>" - the value is free text to the end of the line (C's str), so the lexer
// pushes FREE_TEXT_MODE, as for adjective:.
//
// Token NEG_ADJECTIVE coded on 261114, commented in full on 261114.
NEG_ADJECTIVE
        :   'neg-adjective:' -> pushMode(FREE_TEXT_MODE)
        ;

// "msg:<text>" - the message printed on noticing the property. The value is free text, so the lexer
// pushes FREE_TEXT_MODE.
//
// Token MSG coded on 261114, commented in full on 261114.
MSG
        :   'msg:' -> pushMode(FREE_TEXT_MODE)
        ;

// bindui: switches with mode(), not pushMode(): nothing is pushed, and BINDUI_NL returns
// the lexer to the default mode itself when the line ends.
//
// Token BINDUI coded on 261114, commented in full on 261114.
BINDUI
        :   'bindui:' -> mode(BINDUI_INTRO_MODE)
        ;

// "desc:<text>" - the extra text for the object information screens. The value is free text, so
// the lexer pushes FREE_TEXT_MODE.
//
// Token DESC coded on 261114, commented in full on 261114.
DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

// A colon in the default mode. No parser rule uses it, so one in directive position is a
// syntax error.
//
// Token COLON coded on 261114, commented in full on 261114.
COLON
        :   ':'
        ;

// LCASE_MODE - entered by type: (pushed). It holds the one word and the line end, then pops back to
// the default mode.
//
// Mode LCASE_MODE coded on 261115, commented in full on 261115.
mode LCASE_MODE;

// The value of type: lower-case letters, digits and underscores. Whether it is one of the seven
// type words is the assembler's to decide.
//
// Token LCASE coded on 261114, commented in full on 261115 (moved into LCASE_MODE 261115).
LCASE
        :   ('a'..'z' | '0'..'9' | '_')+
        ;

// The end of the type: line: a bare line end, skipped, and the lexer pops back to the default mode.
// It is NEWLINE, not NL, so a blank or tab before the line end matches no rule in this mode and is
// a lexer error, as C refuses "type:flag ". A last line with no final newline needs no rule: the
// lexer simply reaches the end of the file.
//
// Token LCASE_EOL coded on 261115, commented in full on 261115.
LCASE_EOL
        :   NEWLINE -> skip, popMode
        ;

// UCASE_MODE - entered by code: (pushed). It holds the one name and the line end, then pops back to
// the default mode.
//
// Mode UCASE_MODE coded on 261115, commented in full on 261115.
mode UCASE_MODE;

// The value of code: (e.g. STR, FREE_ACT, DIG_1). FLAG_BODY allows digits after the first letter,
// which several codes carry. Which table the name is looked up in depends on the type, so the
// assembler resolves it.
//
// Token UCASE coded on 261114, commented in full on 261115 (moved into UCASE_MODE 261115).
UCASE
        :   FLAG_BODY
        ;

// The end of the code: line, handled exactly as LCASE_EOL handles the end of the type: line.
//
// Token UCASE_EOL coded on 261115, commented in full on 261115.
UCASE_EOL
        :   NEWLINE -> skip, popMode
        ;

// BINDUI_INTRO_MODE - entered by bindui: (by mode(), so nothing is pushed). It reads the entry name,
// then either a colon (no tag) or '<' (a tag follows), and hands over to the next mode.
//
// Mode BINDUI_INTRO_MODE coded on 261114, commented in full on 261114.
mode BINDUI_INTRO_MODE;

// The UI entry's name after bindui:, as written: any text up to a ':' or a '<', blanks and capitals
// included, because C's sym is any text up to the colon. A '<' ends the name so that the tag after it can
// be read separately; a name with a stray '<' of its own is refused (an accepted deviation).
//
// Token BINDUI_STRING coded on 261114, commented in full on 261114 (name and shape corrected 261114).
BINDUI_STRING
        :   ~('\n' | '\r' | ':' | '<')+
        ;

// A colon straight after the entry name means there is no tag: it is skipped and the
// aux figure follows.
//
// Token BINDUI_INTRO_COLON coded on 261114, commented in full on 261114.
BINDUI_INTRO_COLON
        :   ':' -> skip, mode(BINDUI_INTEGER_MODE)
        ;

// '<' opens the tag: it is skipped and BINDUI_TAG_MODE reads the tag itself.
//
// Token BINDUI_LTHAN coded on 261114, commented in full on 261114.
BINDUI_LTHAN
        :   '<' -> skip, mode(BINDUI_TAG_MODE)
        ;

// BINDUI_TAG_MODE - reads the tag between '<' and '>' on a bindui: line.
//
// Mode BINDUI_TAG_MODE coded on 261114, commented in full on 261114.
mode BINDUI_TAG_MODE;

// The tag between '<' and '>' (e.g. STR, ACID): any text up to a ':' or a '>', so it is not limited to
// upper case. At least one character, so an empty "<>" gives no tag token.
//
// Token BINDUI_TAG coded on 261114, commented in full on 261114 (name and shape corrected 261114).
BINDUI_TAG
        :   ~('\n' | '\r' | ':' | '>')+
        ;

// '>' closes the tag and moves to BINDUI_INTEGER_MODE. The colon after it is skipped by BINDUI_COLON there.
//
// Token BINDUI_GTHAN coded on 261114, commented in full on 261114.
BINDUI_GTHAN
        :   '>' -> skip, mode(BINDUI_INTEGER_MODE)
        ;

// BINDUI_INTEGER_MODE - reads the aux figure and the optional explicit value, up to the end of the
// bindui: line.
//
// Mode BINDUI_INTEGER_MODE coded on 261114, commented in full on 261114.
mode BINDUI_INTEGER_MODE;

// The aux figure, then the optional explicit value: a sign, an optional 0x prefix, then
// digits or hex letters. Whether it is a number C's strtol would read is decided later,
// in the assembler.
//
// Token BINDUI_INTEGER coded on 261114, commented in full on 261114.
BINDUI_INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+
        ;

// The colon between the aux figure and the optional value.
//
// Token BINDUI_COLON coded on 261114, commented in full on 261114.
BINDUI_COLON
        :   ':' -> skip
        ;

// The end of the bindui: line: skipped, and the lexer returns to the default mode. It is NL, so
// blanks and tabs after the last figure are taken with it, as C's strtol ignores them, and so are
// blanks that run to the end of a file with no final newline.
//
// Token BINDUI_NL coded on 261114, commented in full on 261114 (updated 261115: it was NEWLINE, which
// refused a trailing blank).
BINDUI_NL
        :   NL -> skip, mode(DEFAULT_MODE)
        ;

// INTEGER_MODE - entered by power: and mult:. It holds the one figure and leaves on reading it.
//
// Mode INTEGER_MODE coded on 261114, commented in full on 261114.
mode INTEGER_MODE;

// The value of power: or mult:, in the same shape as BINDUI_INTEGER. It is a single token,
// so the lexer pops back to the default mode straight after it.
//
// Token INTEGER coded on 261114, commented in full on 261114.
INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ -> popMode
        ;

// Free-form value text to end of line, popped back to the default mode on newline.
//
// Mode FREE_TEXT_MODE coded on 261114, commented in full on 261114.
mode FREE_TEXT_MODE;

// The value: everything up to, but not including, the end of the line, so any trailing blank or tab
// is part of it, as it is for C's "str". At least one character, so a directive with nothing after
// its colon gives the parser no STRING and is a syntax error.
//
// Token STRING coded on 261114, commented in full on 261114 (trailing blank added 261115).
STRING
        :   ~('\r' | '\n')+
        ;

// The end of the line: skipped, and the lexer pops back to the mode that pushed FREE_TEXT_MODE, always
// the default mode. STRING has already taken any trailing blank, so the blanks NL allows for are never
// left for this rule.
//
// Token EOL_POP coded on 261114, commented in full on 261114 (updated 261115).
EOL_POP
        :   NL -> popMode, skip
        ;

// DELIMITER_INTRO - entered by type-mult: (by mode(), so nothing is pushed). It reads the tval name,
// then the colon that hands over to DELIMITER_POWER. The name and the figure sit in separate modes so
// that a tval written as a number ("5") is still lexed as the name, not as the figure.
//
// Mode DELIMITER_INTRO coded on 261114, commented in full on 261114 (rewritten 261115: it named tokens
// that no longer exist).
mode DELIMITER_INTRO;

// The tval name after type-mult:, as written: any text up to a ':', so blanks and a bare number are
// both allowed ("dragon armor", "5"). Whether it names a tval is the assembler's to decide.
//
// Token DELIM_STRING coded on 261114, commented in full on 261115 (the old comment was filed under
// DELIM_INTEGER).
DELIM_STRING
        :   ~('\r' | '\n' | ':')+
        ;

// The colon between the tval name and the multiplier: skipped, and the lexer moves to DELIMITER_POWER.
//
// Token DELIM_INTRO_COLON coded on 261114, commented in full on 261115 (the old comment called it a
// kept token named DELIM_COLON; it is skipped).
DELIM_INTRO_COLON
        :   ':' -> skip, mode(DELIMITER_POWER)
        ;

// DELIMITER_POWER - reads the multiplier of a type-mult: line, then the end of the line.
//
// Mode DELIMITER_POWER coded on 261114, commented in full on 261115.
mode DELIMITER_POWER;

// The multiplier after the colon, in the same shape as INTEGER and BINDUI_INTEGER: a sign, an optional
// 0x prefix, then digits or hex letters. Whether it is a number C's strtol would read is decided later,
// in the assembler.
//
// Token DELIM_INTEGER coded on 261114, commented in full on 261115.
DELIM_INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+
        ;

// The end of the type-mult: line: skipped, and the lexer returns to the default mode. It is NL, so
// blanks and tabs after the figure are taken with it, as are blanks that run to the end of a file with
// no final newline.
//
// Token END_SKIP coded on 261114, commented in full on 261114 (updated 261115).
END_SKIP
        :   NL -> skip, mode(DEFAULT_MODE)
        ;
