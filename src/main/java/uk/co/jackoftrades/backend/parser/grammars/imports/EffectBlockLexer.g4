// Standalone lexer for the "effect:"/"dice:"/"expr:" directive shape
// duplicated across TrapGrammar.g4, PlayerClass.g4, Shape.g4,
// MonsterSpell.g4, Activations.g4, Curse.g4, and ItemObject.g4 (all in the
// parent grammars/ directory) - see EffectBlock.g4's top-of-file comment
// for the full per-file divergence survey. Imports DiceStrings (this same
// restructured/ package) for its DICE_STRING token rather than re-deriving
// the dice mini-language a third time in this codebase (Random.g4 and
// TrapLexer.g4 are the other two existing copies), and CommentsAndEol
// (also this package) for its COMMENT/EOL skip-rules rather than
// re-declaring those inline the way every one of the 39 grammars in the
// parent directory currently does. Paired with EffectBlock.g4 (parser
// grammar, tokenVocab = EffectBlockLexer).
//
// This is a from-scratch standalone module, not wired into any existing
// grammar, reader, or build script - see EffectBlock.g4's top-of-file
// comment for how a hypothetical future consumer would actually adopt it
// (via `import`, overriding `effect`/`effectBlock` as needed for their own
// segment-count/Effect-construction approach).

lexer grammar EffectBlockLexer;
import DiceStrings, CommentsAndEol;

// "effect:" - introduces the primary effect directive (TYPE[:SUBTYPE
// [:radius[:other]]] follows - see EffectBlock.g4's `effect` rule).
EFFECT
        :   'effect:'
        ;

EFFECT_MESSAGE
        :   'effect-msg:' -> pushMode(FREE_TEXT_MODE)
        ;

// "dice:" - introduces the dice string for the most-recently-seen effect:
// line. Switches into DICE_STRING_MODE so the value tokenizes as one
// DICE_STRING rather than separate INTEGER/'d'/'M' pieces - mirrors
// TrapLexer.g4's DICE token.
DICE
        :   'dice:' -> pushMode(DICE_STRING_MODE)
        ;

TIME
        :   'time:' -> pushMode(DICE_STRING_MODE)
        ;

EFFECT_YX
        :   'effect-yx:'
        ;

// "expr:" - introduces an expression binding a $-variable used in the
// most-recent dice: line to a named base value and operation. Switches
// into EXPR_MODE for the structured "letter:BASE_NAME:operation" value -
// mirrors TrapLexer.g4's EXPR token.
EXPR
        :   'expr:' -> pushMode(EXPR_MODE)
        ;

// Field separator used throughout the directives above.
COLON
        :   ':'
        ;

// An UPPER_CASE_WITH_UNDERSCORES symbolic name - used for effect:'s
// TYPE/SUBTYPE segments (e.g. "DAMAGE", "TIMED_INC", "CUT").
UCASE
        :   ('A'..'Z' | '_')+
        ;

// A bare non-negative integer - used for effect:'s optional radius/other
// trailing segments.
INTEGER
        :   '-'? ('0'..'9')+
        ;

mode FREE_TEXT_MODE;

FREE_TEXT
        :   ~('\r' | '\n')+ -> popMode
        ;

// Entered after dice: to tokenize a dice expression as a single token.
// DICE_STRING_BODY is the fragment imported from DiceStrings - this mode
// block exists so the popMode action can be attached to a wrapper rule
// here rather than modifying the imported grammar's own (mode-agnostic,
// reusable-by-anyone) DICE_STRING token.
mode DICE_STRING_MODE;

DICE_SIMPLE_VALUE
        :   SIMPLE_DICE_STRING_BODY -> popMode
        ;

DICE_COMPLEX_VALUE
        :   COMPLEX_DICE_STRING_BODY -> popMode
        ;

// Entered after expr: to tokenize "letter:BASE_NAME:operation" - same
// token shapes as TrapLexer.g4's own EXPR_MODE, redeclared independently
// here (this grammar doesn't import TrapLexer.g4 - these are new, distinct
// token definitions that merely follow the same naming convention for
// readability/familiarity).
mode EXPR_MODE;

// The single-letter dice variable this expr: line binds a value to.
EXPR_CHAR
        :   ('A'..'Z')
        ;

// Field separator within an expr: line.
EXPR_COLON
        :   ':'
        ;

// The base-value name supplying the value for this expression (e.g.
// "DUNGEON_LEVEL") - any prefix convention (e.g. "EFB_") is the consuming/
// importing grammar's job to add at the Java-action level, not this
// module's.
EXPR_UCASE
        :   ('A'..'Z' | '_')+
        ;

// The operation string applied to the base value, e.g. "/ 2" or "/ 10 + 1" -
// greedily consumes everything to end of line after the leading operator.
EXPR_OP
        :   ('/' | '*' | '+' | '-') ~('\r' | '\n')+
        ;

// End of an expr: line - pops back to the default mode.
EXPR_EOL
        :   '\r'* '\n' -> popMode, skip
        ;