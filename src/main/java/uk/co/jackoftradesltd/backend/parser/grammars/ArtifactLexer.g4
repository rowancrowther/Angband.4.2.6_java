// Lexer for lib/gamedata/artifact.txt - the artifacts (one record per artifact: its name, base
// object, level, weight, cost, allocation, combat figures, flags, activation, modifier values,
// brands, slays, curses and description). Cf. the C original's artifact parser, init_parse_artifact
// in src/obj-init.c.
//
// Every directive is "keyword:value", and a record opens with name:. The default mode holds the
// keywords, the ':' between numeric fields and the integer tokens. Each keyword whose value is
// text or a structured list pushes a mode of its own, so the characters of a value are never
// mis-tokenized as keywords:
//   * ARTIFACT_FREE_TEXT - the rest of the line, taken verbatim (name:, act:, msg:, brand:, slay:,
//                          desc:).
//   * DELIMITED_STRINGS  - colon-separated fields (base-object: and curse:).
//   * ATTACK_MODE        - the dice string after attack:.
//   * TIME_MODE          - the dice string after time:.
//   * ALLOC_INTRO and ALLOC_MODE - the "N:N to N" figures after alloc:.
//   * FLAGS_MODE         - the bar-separated names after flags:.
//   * VALUES_INTRO, VALUES_MODE and VALUES_BREAK - the "LABEL[figure] | LABEL[figure]" list after
//                          values:.
// Each mode ends with its own *_EOL, END_SKIP or *_POP rule, which pops back to the default mode
// and skips the newline, so no end-of-line token reaches the grammar. The newline is the NL
// fragment from CommentsAndEol, which takes any blanks in front of it too.
//
// The graphics: keyword and its glyph and colour modes come from the imported
// AngbandDisplayCharacter, which leaves the lexer in the default mode at the end of the line.
// Comment, indent and blank-line skipping comes from CommentsAndEol, the FLAG_BODY fragment from
// Flags, and the SIMPLE_DICE_STRING token from DiceStrings.
//
// The lexer keeps every value as raw text. Looking a name up, reading a number, range checking
// and building the artifact happen later, in ArtifactAssembler. The keyword armor: is spelled
// with the American "o" because the data file spells it so.
lexer grammar ArtifactLexer;

import AngbandDisplayCharacter, CommentsAndEol, Flags, DiceStrings;

// "name:<text>" - opens a record; the name runs to the end of the line, taken as free text. C
// registers it as a "str" and starts a new zeroed artifact, with every base element marked
// ignore.
//
// Token NAME coded before 261112, commented in full on 261112.
NAME
        :   'name:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

// "base-object:<tval>:<sval>" - the type and sub-type of the item the artifact is built on. C
// registers it as two "sym"s, each cut at the next ':'. The two names are lexed in
// DELIMITED_STRINGS and resolved by the assembler, which makes a new special kind for an sval
// that names no existing kind.
//
// Token BASE_OBJECT coded before 261112, commented in full on 261112.
BASE_OBJECT
        :   'base-object:' -> pushMode(DELIMITED_STRINGS)
        ;

// "level:<n>" - the artifact's difficulty level for activation (not a depth). The figure is the
// ARTIFACT_INTEGER that follows, in the default mode, so there is no mode to push.
//
// Token LEVEL coded before 261112, commented in full on 261112.
LEVEL
        :   'level:'
        ;

// "weight:<n>" - the weight in tenths of a pound, as an ARTIFACT_INTEGER.
//
// Token WEIGHT coded before 261112, commented in full on 261112.
WEIGHT
        :   'weight:'
        ;

// "cost:<n>" - the artifact's value in gold, as an ARTIFACT_INTEGER.
//
// Token COST coded before 261112, commented in full on 261112.
COST
        :   'cost:'
        ;

// "alloc:<commonness>:<min> to <max>" - the chance of generation and the depth range. C
// registers the commonness as an "int" and the range as a "str" that it reads in
// grab_int_range with the separator "to". The figures are lexed in ALLOC_INTRO and ALLOC_MODE.
//
// Token ALLOC coded before 261112, commented in full on 261112.
ALLOC
        :   'alloc:' -> pushMode(ALLOC_INTRO)
        ;

// "attack:<dice>:<to-hit>:<to-dam>" - the base damage dice string, then the two bonuses. C
// registers the dice as a "rand" and the bonuses as "int"s. The dice string is lexed in
// ATTACK_MODE; the colons and bonuses are lexed back in the default mode.
//
// Token ATTACK coded before 261112, commented in full on 261112.
ATTACK
        :   'attack:' -> pushMode(ATTACK_MODE)
        ;

// "armor:<ac>:<to-ac>" - the base armour class and the armour bonus, as two ARTIFACT_INTEGERs
// with an ARTIFACT_COLON between them. The keyword is spelled "armor" in the data file.
//
// Token ARMOUR coded before 261112, commented in full on 261112.
ARMOUR
        :   'armor:'
        ;

// "flags:<flag> | <flag> ..." - the object flags and the IGNORE_ and HATES_ element flags. C
// registers it as an optional "str" split on spaces and bars. One space after the colon is
// taken as part of the keyword, as the data file writes "flags: SEE_INVIS". The names are lexed
// in FLAGS_MODE.
//
// Token FLAGS coded before 261112, commented in full on 261112.
FLAGS
        :   'flags:' ' '? -> pushMode(FLAGS_MODE)
        ;

// "act:<name>" - the activation, by name. C registers it as a "str" and looks it up in
// findact; here the name is free text and the assembler looks it up.
//
// Token ACT coded before 261112, commented in full on 261112.
ACT
        :   'act:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

// "time:<dice>" - the activation's recharge time. C registers it as a "rand". The dice string
// is lexed in TIME_MODE.
//
// Token TIME coded before 261112, commented in full on 261112.
TIME
        :   'time:' -> pushMode(TIME_MODE)
        ;

// "msg:<text>" - the message shown when the artifact is activated. C registers it as a "str" and
// appends to the text when the line is repeated; the grammar does the same.
//
// Token MSG coded before 261112, commented in full on 261112.
MSG
        :   'msg:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

// "values:<LABEL>[<n>] | <LABEL>[<n>] ..." - the object modifiers and RES_ element resistance
// levels. C registers it as a "str" split on spaces and bars. The list is lexed in
// VALUES_INTRO, VALUES_MODE and VALUES_BREAK.
//
// Token VALUES coded before 261112, commented in full on 261112.
VALUES
        :   'values:' -> pushMode(VALUES_INTRO)
        ;

// "brand:<code>" - a brand the artifact carries, by its code in brand.txt. C registers it as a
// "str" and looks it up; here it is free text and the assembler looks it up. The line may be
// repeated.
//
// Token BRAND coded before 261112, commented in full on 261112.
BRAND
        :   'brand:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

// "slay:<code>" - a slay the artifact carries, by its code in slay.txt. C registers it as a
// "str" and looks it up; here it is free text and the assembler looks it up. The line may be
// repeated.
//
// Token SLAY coded before 261112, commented in full on 261112.
SLAY
        :   'slay:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

// "curse:<name>:<power>" - a curse the artifact carries and its power. C registers it as a
// "sym" and an "int". The name may hold spaces ("air swing"). Both fields are lexed in
// DELIMITED_STRINGS.
//
// Token CURSE coded before 261112, commented in full on 261112.
CURSE
        :   'curse:' -> pushMode(DELIMITED_STRINGS)
        ;

// "desc:<text>" - the artifact's description. C registers it as a "str" and appends to the text
// when the line is repeated; the grammar does the same.
//
// Token DESC coded before 261112, commented in full on 261112.
DESC
        :   'desc:' -> pushMode(ARTIFACT_FREE_TEXT)
        ;

// The ':' that separates the numeric fields of attack: and armor: in the default mode. The
// colons inside the other directives belong to their own modes (ALLOC_COLON and DELIM_COLON).
//
// Token ARTIFACT_COLON coded before 261112, commented in full on 261112.
ARTIFACT_COLON
        :   ':'
        ;

// An integer in the default mode: the figure after level:, weight: and cost:, and each field of
// attack: and armor:. It is the INT_FRAGMENT number followed by whatever comes up to the next ':'
// or the end of the line, so a number with trailing text or a trailing blank (the data file has
// "armor:0:0 ") is one token. ParseUtils.parseIntString reads the figure from the front of the
// token's text, as C's strtol with base 0 does.
//
// Token ARTIFACT_INTEGER coded before 261112, commented in full on 261112.
ARTIFACT_INTEGER
        :   INT_FRAGMENT INT_TAIL_FRAGMENT
        ;

// The INT_FRAGMENT number alone, with no tail. No rule of ArtifactGrammar reads it.
//
// Token ARTIFACT_TAILLESS_INTEGER coded before 261112, commented in full on 261112.
ARTIFACT_TAILLESS_INTEGER
        :   INT_FRAGMENT
        ;

// An optionally signed run of decimal digits. Used by ALLOC_INT, which holds the figures of the
// alloc: range.
//
// Fragment DEC_FRAGMENT coded before 261112, commented in full on 261112.
fragment DEC_FRAGMENT
        :   ('-' | '+')? ('0'..'9')+
        ;

// An optionally signed number in the shape C's strtol with base 0 accepts: an optional 0x or 0X
// prefix, then one or more hexadecimal digits. The lexer only checks the shape; a figure that is
// not a number in the base its prefix chooses is refused later, by the assembler.
//
// Fragment INT_FRAGMENT coded before 261112, commented in full on 261112.
fragment INT_FRAGMENT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+
        ;

// Anything after a number up to the next ':' or the end of the line. It lets ARTIFACT_INTEGER
// carry trailing text or blanks as part of its token.
//
// Fragment INT_TAIL_FRAGMENT coded before 261112, commented in full on 261112.
fragment INT_TAIL_FRAGMENT
        :    ~('\r' | '\n' | ':')*
        ;

// ATTACK_MODE - entered by attack:. Holds the one dice string and leaves on reading it.
//
// Mode ATTACK_MODE coded before 261112, commented in full on 261112.
mode ATTACK_MODE;

// The base damage dice string, such as 2d5. Only the simple form is allowed, with no '$'
// variables, as C reads it with parse_random. The lexer then pops back to the default mode,
// where the colons and bonuses that follow are lexed.
//
// Token BASE_DICE_STRING coded before 261112, commented in full on 261112.
BASE_DICE_STRING
        :   SIMPLE_DICE_STRING -> popMode
        ;

// TIME_MODE - entered by time:. Holds the one dice string and leaves on reading it.
//
// Mode TIME_MODE coded before 261112, commented in full on 261112.
mode TIME_MODE;

// The recharge dice string, such as 50+d50. Only the simple form is allowed, with no '$'
// variables, as C reads it with parse_random. The lexer then pops back to the default mode,
// where the newline is skipped.
//
// Token SIMPLE_DICE_TIME_STRING coded before 261112, commented in full on 261112.
SIMPLE_DICE_TIME_STRING
        :   SIMPLE_DICE_STRING -> popMode
        ;

// ALLOC_INTRO - entered by alloc:. Holds only the commonness figure, which it hands to
// ALLOC_MODE by replacing itself with it, so the later pop still returns to the default mode.
//
// Mode ALLOC_INTRO coded before 261112, commented in full on 261112.
mode ALLOC_INTRO;

// The commonness: the first figure of alloc:, the chance out of 100 that the artifact is made.
// C reads it as an "int", so it takes the INT_FRAGMENT shape, signed and with a possible 0x
// prefix, and has no tail.
//
// Token ALLOC_COMMONNESS_INT coded before 261112, commented in full on 261112.
ALLOC_COMMONNESS_INT
        :   INT_FRAGMENT -> mode(ALLOC_MODE)
        ;

// ALLOC_MODE - the rest of the alloc: line, after the commonness: ":<min> to <max>".
//
// Mode ALLOC_MODE coded before 261112, commented in full on 261112.
mode ALLOC_MODE;

// One end of the depth range. C reads each end with strtol in base 10, so it is the signed
// decimal DEC_FRAGMENT. The assembler refuses an end outside 0 to 255.
//
// Token ALLOC_INT coded before 261112, commented in full on 261112.
ALLOC_INT
        :   DEC_FRAGMENT
        ;

// The ':' between the commonness and the range.
//
// Token ALLOC_COLON coded before 261112, commented in full on 261112.
ALLOC_COLON
        :   ':'
        ;

// The separator between the two ends of the range, " to " with exactly one space on each side.
// C's grab_int_range accepts any run of blanks round the "to".
//
// Token ALLOC_TO coded before 261112, commented in full on 261112.
ALLOC_TO
        :   ' to '
        ;

// The end of the alloc: line. Skips the newline and pops back to the default mode.
//
// Token ALLOC_EOL coded before 261112, commented in full on 261112.
ALLOC_EOL
        :   NL -> skip, popMode
        ;

// ARTIFACT_FREE_TEXT - entered by name:, act:, msg:, brand:, slay: and desc:. Holds the rest of
// the line as one STRING.
//
// Mode ARTIFACT_FREE_TEXT coded before 261112, commented in full on 261112.
mode ARTIFACT_FREE_TEXT;

// The whole rest of the line, verbatim. Trailing blanks stay in the text, because this rule
// takes them before the end-of-line rule can.
//
// Token STRING coded before 261112, commented in full on 261112.
STRING
        :   ~('\n' | '\r')+
        ;

// The end of a free-text line. Skips the newline and pops back to the default mode.
//
// Token ARTIFACT_FREE_TEXT_EOL coded before 261112, commented in full on 261112.
ARTIFACT_FREE_TEXT_EOL
        :   NL -> skip, popMode
        ;

// DELIMITED_STRINGS - entered by base-object: and curse:. Holds colon-separated fields: two names
// for base-object:, and a name and a power for curse:.
//
// Mode DELIMITED_STRINGS coded before 261112, commented in full on 261112.
mode DELIMITED_STRINGS;

// A number in a delimited field. It is the power of a curse:, and it is also what a tval or sval
// made only of digits, or of the hex letters a to f, becomes: such a name is the same length as a
// STRING_BETWEEN_COLONS match, and this rule is declared first, so it wins the tie. The grammar's
// baseObject rule takes only STRING_BETWEEN_COLONS for the two names, so it refuses that name.
//
// Token DELIM_INTEGER coded before 261112, commented in full on 261112.
DELIM_INTEGER
        :   INT_FRAGMENT
        ;

// A name between colons: the tval and sval of base-object:, and the curse name of curse:. It may
// hold spaces.
//
// Token STRING_BETWEEN_COLONS coded before 261112, commented in full on 261112.
STRING_BETWEEN_COLONS
        :   ~('\n' | '\r' | ':')+
        ;

// The ':' between two delimited fields.
//
// Token DELIM_COLON coded before 261112, commented in full on 261112.
DELIM_COLON
        :   ':'
        ;

// The end of the line. Skips the newline and pops back to the default mode.
//
// Token END_SKIP coded before 261112, commented in full on 261112.
END_SKIP
        :   NL -> skip, popMode
        ;

// FLAGS_MODE - entered by flags:. Holds the flag names and the bars between them.
//
// Mode FLAGS_MODE coded before 261112, commented in full on 261112.
mode FLAGS_MODE;

// One flag name: an upper-case letter then upper-case letters, digits and underscores (the
// FLAG_BODY fragment of Flags). It covers the object flags bare, without the OF_ prefix, and the
// IGNORE_ and HATES_ element flags. The assembler tells them apart.
//
// Token FLAG coded before 261112, commented in full on 261112.
FLAG
        :   FLAG_BODY
        ;

// The bar between two flag names, with one optional space on each side.
//
// Token FLAG_OR coded before 261112, commented in full on 261112.
FLAG_OR
        :   (' '+ '|'* ' '*
        |    ' '* '|'+ ' '*)
        ;

// The end of the flags: line. Skips the newline and pops back to the default mode.
//
// Token FLAG_EOL coded before 261112, commented in full on 261112.
FLAG_EOL
        :   NL -> skip, popMode
        ;

// VALUES_INTRO - entered by values: and again after each bar. Waits for the next label.
//
// Mode VALUES_INTRO coded before 261112, commented in full on 261112.
mode VALUES_INTRO;

// The bar between two value names, with one optional space on each side.
VALUES_INTRO_OR
        :   (' '+ '|'* ' '*
        |    ' '* '|'+ ' '*)
        ;

// A label: the name of an object modifier, or an element resistance with the RES_ prefix, in
// the FLAG_BODY shape of Flags. The lexer moves to VALUES_MODE for its figure.
//
// Token VALUES_STRING coded before 261112, commented in full on 261112.
VALUES_STRING
        :   FLAG_BODY -> mode(VALUES_MODE)
        ;

// A single character that cannot start a label and is not a colon or a line end. It is skipped
// and the lexer pops back to the default mode, so the values: line has no label for the grammar
// and is refused.
//
// Token ANYTHING_ELSE coded before 261112, commented in full on 261112.
ANYTHING_ELSE
        :   ~('\r' | '\n' | ':') -> skip, popMode
        ;

// The end of the line where a label was expected. Skips the newline and pops back to the default
// mode.
//
// Token VALUES_INTRO_EOL coded before 261112, commented in full on 261112.
VALUES_INTRO_EOL
        :   NL -> skip, popMode
        ;

// VALUES_MODE - between a label and the end of its bracketed figure: "[<n>]".
//
// Mode VALUES_MODE coded before 261112, commented in full on 261112.
mode VALUES_MODE;

// The '[' that opens a figure. Skipped, so the grammar sees only the label and the figure.
//
// Token VALUES_LBRACKET coded before 261112, commented in full on 261112.
VALUES_LBRACKET
        :   '[' -> skip
        ;

// The ']' that closes a figure. Skipped, and the lexer moves to VALUES_BREAK to wait for a bar
// or the end of the line.
//
// Token VALUES_RBRACKET coded before 261112, commented in full on 261112.
VALUES_RBRACKET
        :   ']' -> skip, mode(VALUES_BREAK)
        ;

// The figure inside the brackets, in the INT_FRAGMENT shape. The assembler reads a values:
// figure as a plain decimal, as C's find_value_arg does, and refuses one that is not.
//
// Token VALUES_INTEGER coded before 261112, commented in full on 261112.
VALUES_INTEGER
        :   INT_FRAGMENT
        ;

// VALUES_BREAK - after a figure's ']'. Waits for a bar, which starts the next label, or the end
// of the line.
//
// Mode VALUES_BREAK coded before 261112, commented in full on 261112.
mode VALUES_BREAK;

// The bar between two label and figure pairs, with one optional space on each side. The lexer
// goes back to VALUES_INTRO for the next label.
//
// Token VALUES_OR coded before 261112, commented in full on 261112.
VALUES_OR
        :   (' '+ '|'* ' '*
        |    ' '* '|'+ ' '*) -> mode(VALUES_INTRO)
        ;

// The end of the values: line. Skips the newline and pops back to the default mode.
//
// Token VALUES_EOL coded before 261112, commented in full on 261112.
VALUES_EOL
        :   NL -> skip, popMode
        ;
