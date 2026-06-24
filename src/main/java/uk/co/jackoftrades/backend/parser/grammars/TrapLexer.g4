// Lexer for lib/gamedata/trap.txt — Angband's trap/rune definitions (glyph of
// warding, decoy, door lock, pits, dart traps, summoning runes, the Mouth of
// Sauron, etc). Paired with TrapGrammar.g4 (parser grammar, tokenVocab =
// TrapLexer). Each token here corresponds 1:1 with a directive registered in
// the original C parser, src/init.c:init_parse_trap() (around line 1907),
// which builds a `struct trap_kind` per record; the struct file_parser
// `trap_parser` itself is defined at src/init.c:1989.
//
// POTENTIAL PROBLEMS (see also TrapGrammar.g4, which has more/worse issues):
//
//   1. Missing tokens for "effect-yx:" / "effect-yx-xtra:". The C parser
//      registers these (parse_trap_effect_yx / parse_trap_effect_yx_xtra,
//      init.c:1617 and :1737) to set a y/x coordinate on the *last* effect
//      in the current chain. lib/gamedata/trap.txt doesn't currently use
//      either directive, so this lexer happens to get away without them —
//      but if a future trap definition adds one, the lexer has no token for
//      it at all and the file will fail to parse with no useful error.
//
//   2. GRAPHICS_COLOUR (below) mixes single-character colour codes with
//      multi-word literal alternatives ("light yellow", "light slate",
//      "light purple", "blue", "yellow") - both forms are real and needed:
//      the multi-word names are each used once, unquoted, in trap.txt
//      ("light yellow" line 517, "light slate" line 530, "light purple"
//      line 545, "yellow" line 560, "blue" line 575 - the blinding
//      flash/blinding/mana drain/knife/petrifying traps), so they aren't
//      dead branches.
//
// See the bottom of TrapGrammar.g4 for proposed solutions covering both
// grammars (this lexer's fixes are folded in there since they're driven by
// the same root cause: the trap.txt format support is incomplete).

lexer grammar TrapLexer;

import DiceStrings, CommentsAndEol, AngbandDisplayCharacter;

// "name:" - introduces parse_trap_name: internal name + short description,
// e.g. "name:glyph of warding:glyph of warding".
NAME
        :   'name:'
        ;

// "appear:" - introduces parse_trap_appear: rarity : min depth : max number
// on level, e.g. "appear:0:0:0".
APPEAR
        :   'appear:'
        ;

// "visibility:" - introduces parse_trap_visibility: a dice string for the
// trap's detectability (stored as t->power in the C struct). Switches into
// DICE_STRING_MODE since the value is a dice expression, not plain text.
VISIBILITY
        :   'visibility:'
        ;

// "desc:" - introduces parse_trap_desc: the free-text flavour description
// shown to the player.
DESC
        :   'desc:'
        ;

// "flags:" - introduces parse_trap_flags: '|'-separated TRF_* flag names.
// Switches into FLAG_MODE for the '|'-separated flag-name token stream.
FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

// "msg:" - introduces parse_trap_msg: message shown when the trap triggers.
MSG
        :   'msg:'
        ;

// "save:" - introduces parse_trap_save_flags: '|'-separated OF_* object
// flags that grant the player a saving throw against this trap.
SAVE
        :   'save:' -> pushMode(FLAG_MODE)
        ;

// "msg-good:" - introduces parse_trap_msg_good: message shown when the
// player successfully saves against the trap.
MSG_GOOD
        :   'msg-good:'
        ;

// "msg-bad:" - introduces parse_trap_msg_bad: message shown when the player
// fails to save against the trap.
MSG_BAD
        :   'msg-bad:'
        ;

// "effect:" - introduces parse_trap_effect: the primary effect(s) run when
// the trap triggers and isn't avoided, e.g. "effect:DAMAGE" or
// "effect:SPOT:FIRE:0". Each occurrence starts a new effect node in the C
// struct's effect linked list (see TrapGrammar.g4 problem #5).
EFFECT
        :   'effect:'
        ;

// "dice:" - introduces parse_trap_dice: the dice string for the
// most-recently-seen effect:/effect-xtra: line. Switches into
// DICE_STRING_MODE for the dice-expression token.
DICE
        :   'dice:'
        ;

// "expr:" - introduces parse_trap_expr: binds a dice-string variable (e.g.
// $S/$B/$D) in the most-recent dice: line to a named base value and
// operation, e.g. "expr:S:DUNGEON_LEVEL:/ 2". Switches into EXPR_MODE.
EXPR
        :   'expr:' -> pushMode(EXPR_MODE)
        ;

// "msg-xtra:" - introduces parse_trap_msg_xtra: message shown when the
// extra (50%-chance) effect below triggers.
MSG_XTRA
        :   'msg-xtra:'
        ;

// "effect-xtra:" - introduces parse_trap_effect_xtra: a secondary effect
// that has a 50% chance of also running. Same per-line "new node" semantics
// as EFFECT, but appended to a separate effect_xtra list.
EFFECT_XTRA
        :   'effect-xtra:'
        ;

// "dice-xtra:" - introduces parse_trap_dice_xtra: dice string for the
// most-recently-seen effect-xtra: line.
DICE_XTRA
        :   'dice-xtra:'
        ;

// "expr-xtra:" - introduces parse_trap_expr_xtra: expression binding for the
// most-recent dice-xtra: line.
EXPR_XTRA
        :   'expr-xtra:' -> pushMode(EXPR_MODE)
        ;

// A bare non-negative integer - used by appear: (rarity/mindepth/maxnum)
// and by effect:/effect-xtra:'s optional radius/other-parameter fields.
INTEGER
        :   ('0'..'9')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES symbolic name - used for effect:/
// effect-xtra:'s type/subtype segments (e.g. "DAMAGE", "TIMED_INC", "CUT").
UCASE
        :   ('A'..'Z' | '_')+
        ;

// Free-running lower/mixed-case text with common punctuation - used for
// name:, desc:, msg:, msg-good:, msg-bad: and msg-xtra:'s free-text values.
LCASE
        :   ('a'..'z' | ' ' | '.' | 'A'..'Z' | ',' | '!' | '\'' | '-')+
        ;

// Field separator used throughout every directive above.
COLON
        :   ':'
        ;

// Entered after flags:/save: to tokenize a '|'-separated flag-name list.
mode FLAG_MODE;

// A single TRF_*/OF_* flag name (the "TRF_"/"OF_" prefix itself is added in
// TrapGrammar.g4's actions, not present in the raw text).
FLAG_MODE_UCASE
        :   ('A'..'Z' | '_')+
        ;

// The " | " separator between flag names on a flags:/save: line.
FLAG_OR
        :   ' | '
        ;

// End of a flags:/save: line - pops back to the default mode.
FLAG_EOL
        :   '\r'* '\n' -> popMode, skip
        ;

// Entered after expr:/expr-xtra: to tokenize "letter:BASE_NAME:operation".
mode EXPR_MODE;

// The single-letter dice variable this expr: line binds a value to (must
// match the '$'-prefixed letter used in the corresponding dice: string).
EXPR_CHAR
        :   ('S' | 'D' | 'M' | 'B')
        ;

// Field separator within an expr:/expr-xtra: line.
EXPR_COLON
        :   ':'
        ;

// The EffectBaseType name (e.g. "DUNGEON_LEVEL") supplying the base value
// for this expression (the "EFB_" prefix is added in TrapGrammar.g4).
EXPR_UCASE
        :   ('A'..'Z' | '_')+
        ;

// The operation string applied to the base value, e.g. "/ 2" or "/ 10 + 1" -
// greedily consumes everything to end of line after the leading operator.
EXPR_OP
        :   ('/' | '*' | '+' | '-') ~('\r' | '\n')+
        ;

// End of an expr:/expr-xtra: line - pops back to the default mode.
EXPR_EOL
        :   '\r'* '\n' -> popMode, skip
        ;

// POTENTIAL SOLUTIONS
//
//   1. effect-yx / effect-yx-xtra: add
//          EFFECT_YX      : 'effect-yx:';
//          EFFECT_YX_XTRA : 'effect-yx-xtra:';
//      (plain INTEGER COLON INTEGER follows, like `appear:`/`appear:` — no
//      new mode needed) only if/when trap.txt actually gains a use of them;
//      otherwise leave unsupported but keep this noted so a future "file
//      won't parse" report doesn't start from zero.