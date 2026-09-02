/*
 * @author Rowan Crowther
 *
 * Lexer for room_template.txt. Every directive on a room record is a fixed literal prefix;
 * three of them (name:, tval:, D:) hand the rest of their line to REST_OF_LINE as free text,
 * and flags: hands off to FLAG_MODE to tokenize a `|`-separated flag list. Everything else
 * (type:/rating:/rows:/columns:/doors:) is followed by a plain INTEGER in the default mode.
 *
 * Comments and blank lines are handled by the imported CommentsAndEol rules, which only apply
 * in the default mode — inside REST_OF_LINE or FLAG_MODE a leading '#' is just ordinary text
 * (relevant for D: lines, whose room-layout symbols include '#' for granite).
 */
lexer grammar RoomProfileLexer;

import CommentsAndEol, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(REST_OF_LINE)
        ;

TYPE
        :   'type:'
        ;

RATING
        :   'rating:'
        ;

ROWS
        :   'rows:'
        ;

COLUMNS
        :   'columns:'
        ;

DOORS
        :   'doors:'
        ;

/*
 * @author Rowan Crowther
 *
 * tval: values in the data are text, not always numbers — "0" alongside "rod", "wand",
 * "amulet" and so on — so this hands off to REST_OF_LINE like NAME/DLINE rather than being
 * followed by INTEGER. Resolving that text to a tval (numeric or by name) is
 * RoomProfileAssembler's job, mirroring C's tval_find_idx.
 */
TVAL
        :   'tval:' -> pushMode(REST_OF_LINE)
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

DLINE
        :   'D:' -> pushMode(REST_OF_LINE)
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

mode FLAG_MODE;

/*
 * @author Rowan Crowther
 *
 * A single flag name (e.g. FEW_ENTRANCES). FLAG_BODY is the shared fragment from the Flags
 * import — see that file for why it can never collide with INTEGER.
 */
FLAG
        :   FLAG_BODY
        ;

/*
 * @author Rowan Crowther
 *
 * The `|` separator between flag names in a flags: list, with optional surrounding spaces.
 */
FLAG_OR
        :   ' '? '|' ' '?
        ;

/*
 * @author Rowan Crowther
 *
 * End of the flags: line — pops back to the default mode so the next line's directive is
 * lexed normally.
 */
FLAG_EOF
        :   '\r'* '\n' -> skip, popMode
        ;

mode REST_OF_LINE;

/*
 * @author Rowan Crowther
 *
 * Everything up to (but not including) the line ending — the free-text payload of a
 * name:/tval:/D: directive.
 */
STRING
        :   ~('\n' | '\r')+
        ;

/*
 * @author Rowan Crowther
 *
 * End of a name:/tval:/D: line — pops back to the default mode.
 */
END_OF_LINE
        :   '\r'* '\n' -> skip, popMode
        ;
