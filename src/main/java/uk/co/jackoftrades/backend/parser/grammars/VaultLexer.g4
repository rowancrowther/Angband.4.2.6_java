/*
 * @author Rowan Crowther
 *
 * Lexer for vault.txt. Every directive on a vault record is a fixed literal prefix; three of
 * them (name:, type:, D:) hand the rest of their line to REST_OF_LINE as free text, and flags:
 * hands off to FLAG_MODE to tokenize a `|`-separated flag list. Everything else
 * (rating:/rows:/columns:/min-depth:/max-depth:) is followed by a plain INTEGER in the default
 * mode.
 *
 * type: goes to REST_OF_LINE rather than being a set of keywords because the seven vault type
 * names are ordinary prose with spaces and brackets — "Lesser vault", "Interesting room",
 * "Greater vault (new)". Matching one to a RoomType is VaultAssembler's job, mirroring C's
 * streq against room_builders[i].name ([C] src/generate.c:517).
 *
 * D: matters more than it looks. C requires every layout line to be exactly `columns` characters
 * (strlen(desc) != v->wid, [C] src/generate.c:588), and the real data pads short rows with
 * trailing spaces to reach that width. STRING therefore runs to the line ending and keeps that
 * padding verbatim; anything that trimmed it would silently corrupt the vault's shape.
 *
 * Comments and blank lines are handled by the imported CommentsAndEol rules, which only apply in
 * the default mode — inside REST_OF_LINE or FLAG_MODE a leading '#' is just ordinary text. That
 * is essential here: '#' is the granite symbol, so a D: line may begin with one.
 */
lexer grammar VaultLexer;

import CommentsAndEol, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(REST_OF_LINE)
        ;

/*
 * @author Rowan Crowther
 *
 * The vault's type: directive. Hands off to REST_OF_LINE because the names are free text (see
 * the top-of-file comment), not an enumerable token set.
 */
TYPE
        :   'type:' -> pushMode(REST_OF_LINE)
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

MIN_DEPTH
        :   'min-depth:'
        ;

MAX_DEPTH
        :   'max-depth:'
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * One line of the vault's layout. Hands off to REST_OF_LINE so the line is taken verbatim,
 * including the trailing spaces that pad it out to the declared width.
 */
D
        :   'D:' -> pushMode(REST_OF_LINE)
        ;

/*
 * @author Rowan Crowther
 *
 * Declared after the directives so the literals win the longest-match tie, and signed because
 * rating: is read with parser_getint in C ([C] src/generate.c:600) rather than parser_getuint —
 * a negative rating is legal input even though the shipped data has none.
 */
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
OR
        :   ' '? '|' ' '?
        ;

/*
 * @author Rowan Crowther
 *
 * End of the flags: line — pops back to the default mode so the next line's directive is lexed
 * normally.
 */
FLAG_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode REST_OF_LINE;

/*
 * @author Rowan Crowther
 *
 * Everything up to (but not including) the line ending — the free-text payload of a
 * name:/type:/D: directive. Greedy to the line ending on purpose: that is what preserves a D:
 * line's trailing padding.
 */
STRING
        :   ~('\n' | '\r')+
        ;

/*
 * @author Rowan Crowther
 *
 * End of a name:/type:/D: line — pops back to the default mode. vault.txt ends without a final
 * newline, so the last D: line never matches this; ANTLR emits EOF from REST_OF_LINE instead and
 * the parse still completes.
 */
END_OF_LINE
        :   '\r'* '\n' -> skip, popMode
        ;
