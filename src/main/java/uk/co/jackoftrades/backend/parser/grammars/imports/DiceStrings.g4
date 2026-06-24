// Standalone lexer grammar for Angband's dice-string mini-language (e.g.
// "2d8", "1+1d3", "M80", "4d$S", "$Dd5", "$Dd$S"). This is a strict
// superset of ../Random.g4 (the existing combined lexer+parser grammar in
// the parent grammars/ directory, used at the Java level via
// uk.co.jackoftrades.backend.numerics.Random.parseStr(String)): every
// alternative Random.g4 supports is supported here too, PLUS alternatives
// closing all 4 gaps documented in Random.g4's own top-of-file
// "POTENTIAL PROBLEMS" comment - see "GAPS CLOSED" below. This file is not
// wired into anything; it's standalone, importable infrastructure meant to
// demonstrate ANTLR4's `import` statement (grammar-level rule sharing,
// distinct from the tokenVocab lexer/parser pairing TrapGrammar.g4/
// TrapLexer.g4 already use elsewhere in this repo).
//
// Unlike Random.g4, this file has zero @header / Java-domain-type
// coupling - it's meant to be `import`ed by other LEXER grammars (e.g.
// EffectBlockLexer.g4 in this same restructured/ package) that want a
// DICE_STRING token without pulling in uk.co.jackoftrades.backend.
// numerics.Random or any other domain type at the lexer level. The main
// alternation is exposed as `fragment DICE_STRING_BODY` (not just inlined
// into `DICE_STRING` itself) specifically so an importing grammar can
// reuse the exact same matching logic inside its own mode-scoped wrapper
// rule (see EffectBlockLexer.g4's DICE_STRING_MODE) without redefining it -
// composing a fragment across an `import` is standard, low-risk ANTLR4
// usage, unlike trying to compose one token rule directly from another.
//
// GAPS CLOSED vs Random.g4 (see that file's "POTENTIAL PROBLEMS" #1-#5):
//
//   1. (Random.g4 problem #1) Dice-count itself may now be a $-variable,
//      e.g. "$Dd4"/"$Dd6"/"$Dd7"/"$Dd8"/"$Dd12" (used repeatedly in
//      class.txt and monster_spell.txt) and "$Dd5" (trap.txt, "dice:$Dd5").
//   2. (Random.g4 problem #2) Dice-count AND sides may both be $-variables
//      simultaneously, e.g. "$Dd$S" (class.txt).
//   3. (Random.g4 problem #3) N/A here - that problem was about a
//      duplicated/dead alternative in Random.g4's own rule, not a missing
//      feature; this file has no such duplicate to begin with.
//   4. (Random.g4 problem #4) DOLLAR_LETTER is widened from a single
//      uppercase letter to a full uppercase-and-underscore name (z-dice.c's
//      dice_parse_string() doc comment describes variables as "an
//      all-uppercase name", i.e. potentially multi-character).
//   5. (Random.g4 problem #5) A negative $-variable base is now supported,
//      e.g. "-$B+2d8", mirroring the existing "-? NUMBER ..." alternatives.
//
// None of gaps #4/#5 are currently exercised by any file in lib/gamedata
// (same as in Random.g4) - closed anyway since doing so costs nothing for
// a brand-new, currently-unused file with no existing behaviour to keep
// compatible with.

lexer grammar DiceStrings;

// The dice-count/sides separator, e.g. the 'd' in "2d8". A fragment, not a
// standalone token, since this file deliberately exposes nothing but
// DICE_STRING/DICE_STRING_BODY at the public-token level - there's no
// @after action here needing to bind 'd' individually the way Random.g4's
// parser rule does.
fragment D
        :   'd'
        ;

// The bonus marker, e.g. the 'M' in "2d8M4" - accepts either case.
fragment M
        :   'M'
        |   'm'
        ;

// A literal non-negative integer.
INTEGER
        :   ('0'..'9')+
        ;

// A bound dice-string variable: '$' followed by an uppercase, possibly
// multi-character, underscore-allowed name (e.g. "$S", "$D", "$B"; also
// "$DUNGEON_LEVEL"-shaped names, per gap #4 above, though no current
// gamedata file actually uses anything but a single letter).
DOLLAR_LETTER
        :   '$' ('A'..'Z' | '_')+
        ;

// The full complex-dice-string permutation space: base (absent, literal, or
// $-variable) '+' dice-count (absent, literal, or $-variable) 'd' sides
// (literal or $-variable) ('M' bonus (literal or $-variable))? - plus the
// bare "Mbonus"-only and "$D"-only shapes. This is expanded from the
// simple-dice-string by the addition of potential $variables in it
// Exposed as a fragment (see
// top-of-file comment) so EffectBlockLexer.g4 can reuse this exact body in
// its own mode-scoped wrapper without redeclaring it.
fragment COMPLEX_DICE_STRING_BODY
        :   (   // $-variable base, literal dice-count/sides (and the
                // negative-base form closing gap #5)
                '-'? DOLLAR_LETTER '+' INTEGER D INTEGER (M INTEGER)?
            |   '-'? DOLLAR_LETTER '+' D INTEGER (M INTEGER)?
            |   DOLLAR_LETTER (M INTEGER)?

                // literal base, $-variable sides
            |   '-'? INTEGER '+' INTEGER D DOLLAR_LETTER (M INTEGER)?
            |   '-'? INTEGER '+' D DOLLAR_LETTER (M INTEGER)?
            |   INTEGER D DOLLAR_LETTER (M INTEGER)?
            |   D DOLLAR_LETTER (M INTEGER)?

                // literal base, literal dice-count/sides, $-variable bonus
            |   '-'? INTEGER '+' INTEGER D INTEGER M DOLLAR_LETTER
            |   INTEGER D INTEGER M DOLLAR_LETTER
            |   D INTEGER M DOLLAR_LETTER
            |   INTEGER M DOLLAR_LETTER
            |   M DOLLAR_LETTER

                // GAP CLOSED #1: dice-count itself as a $-variable
                // (e.g. "$Dd5", "4+$Dd6"), literal or $-variable sides
            |   '-'? INTEGER '+' DOLLAR_LETTER D INTEGER (M INTEGER)?
            |   '-'? DOLLAR_LETTER '+' DOLLAR_LETTER D INTEGER (M INTEGER)?
            |   DOLLAR_LETTER D INTEGER (M INTEGER)?
            |   DOLLAR_LETTER D INTEGER (M DOLLAR_LETTER)?

                // GAP CLOSED #2: dice-count AND sides both $-variables
                // (e.g. "$Dd$S")
            |   '-'? INTEGER '+' DOLLAR_LETTER D DOLLAR_LETTER (M INTEGER)?
            |   DOLLAR_LETTER D DOLLAR_LETTER (M INTEGER)?
            |   DOLLAR_LETTER D DOLLAR_LETTER (M DOLLAR_LETTER)?
            )
        ;

// @Author Rowan Crowther
// The full simple-dice-string permutation space: base (absent, literal)
// '+' dice-count (absent, literal) 'd' sides (literal) ('M' bonus (literal))?
// - plus the bare "Mbonus"-only and "$D"-only shapes. Exposed as a fragment (see
// top-of-file comment) so all grammars can reuse this exact body in
// its own mode-scoped wrapper without redeclaring it.
fragment SIMPLE_DICE_STRING_BODY
        :   ( // literal base, literal/absent dice-count, literal sides
                '-'? INTEGER '+' INTEGER D INTEGER (M INTEGER)?
            |   '-'? INTEGER '+' D INTEGER (M INTEGER)?
            |   INTEGER D INTEGER (M INTEGER)?
            |   D INTEGER (M INTEGER)?
            |   INTEGER (M INTEGER)?
            |   M INTEGER
            )
        ;

// One whole simple dice-string token, e.g. "2d8", "1+1d3", "M80".
// A single SIMPLE_DICE_STRING token so a consumer can swallow
// a whole dice expression as one token inside a pushMode(...)-entered mode,
// the same way TrapLexer.g4's VISIBILITY/DICE/DICE_XTRA tokens do.
SIMPLE_DICE_STRING
        :   SIMPLE_DICE_STRING_BODY
        ;

// @Author Rowan Crowther
// One whole complex dice-string token, including a $ token e.g. "4d$S", "$Dd5",
// "$Dd$S". A single COMPLEX_DICE_STRING token (mirrors TrapLexer.g4's DICE_STRING,
// not Random.g4's multi-token `dice` parser rule) so a consumer can swallow
// a whole complex dice expression as one token inside a pushMode(...)-entered mode,
// the same way TrapLexer.g4's VISIBILITY/DICE/DICE_XTRA tokens do.
COMPLEX_DICE_STRING
        :   COMPLEX_DICE_STRING_BODY
        ;
