// Lexer for lib/gamedata/monster_spell.txt - the monster spells (one record per spell: its
// message type, to-hit chance, effects, and the lore text and messages for each spell-power
// tier). Cf. the C original's spell parser, init_parse_mon_spell in src/mon-init.c.
//
// Every directive is "keyword:value". The default mode holds only the keywords; each keyword
// that carries free text pushes MS_FREE_TEXT_MODE, so the characters of a value are never
// mis-tokenized as keywords:
//   * MS_FREE_TEXT_MODE - the rest of the line, taken verbatim (names, message type, lore,
//                         colour names, messages).
//   * HIT_MODE          - the unsigned number after hit:, read as UINT.
// Each mode ends with its own *_EOL rule, which pops back to the default mode and skips the
// newline, so no end-of-line token reaches the grammar.
//
// The effect directives (effect:, effect-yx:, dice:, expr: and their value modes) and the
// EFFECT_INTEGER token come from the imported EffectBlockLexer; the comment, indent and
// blank-line skipping comes from CommentsAndEol. power-cutoff: takes its value from that
// EFFECT_INTEGER, in the default mode, so it needs no mode of its own. EFFECT_INTEGER reads an
// "int" as C's strtol in base 0 does: an optional '+' or '-', an optional 0x or 0X prefix, then
// one or more digits (a-f included), then any tail up to a ':' or the end of the line. The
// assembler turns the text into a number and refuses one with no digit in it.
// effect-msg: and time: are also imported but the monster spell grammar has no rule for them.
//
// The lexer keeps every value as raw text. Looking a name up, resolving a colour and range
// checking happen later, in MonsterSpellAssembler.
lexer grammar MonsterSpellLexer;

import CommentsAndEol, EffectBlockLexer;

// "name:<spell>" - opens a record; the name runs to the end of the line. C reads it as a
// "str" and looks it up in its table of spell flags, refusing a name that is not in it. The
// lookup is the assembler's job here.
//
// Token NAME coded before 261112, commented in full on 261112.
NAME
        :   'name:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// "msgt:<type>" - the message type used for the spell's message-vis:, message-invis: and
// message-miss: lines. C registers it as a "sym", which it cuts at the next ':'; here the value
// is the rest of the line, as text, and the assembler looks it up.
//
// Token MSGT coded before 261112, commented in full on 261112.
MSGT
        :   'msgt:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// "hit:<n>" - the spell's base chance to hit (100 = always, 0 = never). The number is lexed in
// HIT_MODE as a UINT.
//
// Token HIT coded before 261112, commented in full on 261112.
HIT
        :   'hit:' -> pushMode(HIT_MODE)
        ;

// "power-cutoff:<n>" - the spell power at which the lore and messages that follow start to
// apply. C registers it as an "int". The number is the imported EFFECT_INTEGER, taken in the
// default mode, so there is no mode to push. As the only field on the line, it keeps whatever
// follows its digits up to a ':' (C ignores a ':' field after it; here the ':' is refused).
//
// Token POWER_CUTOFF coded before 261112, commented in full on 261112, updated on 261112 to
// name EFFECT_INTEGER.
POWER_CUTOFF
        :   'power-cutoff:'
        ;

// "lore:<text>" - the lore description for the current tier. C registers it as a "str" and
// appends to the text when the line is repeated; the grammar does the same.
//
// Token LORE coded before 261112, commented in full on 261112.
LORE
        :   'lore:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// "lore-color-base:<colour>" - the lore colour for the tier. American spelling, matching the
// data file. C registers it as a "sym": a single character is a colour code, longer text is a
// colour name, and anything else is refused. Here the value is the rest of the line, and the
// assembler resolves it.
//
// Token LORE_COLOUR_BASE coded before 261112, commented in full on 261112.
LORE_COLOUR_BASE
        :   'lore-color-base:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// "lore-color-resist:<colour>" - the lore colour for the tier when the spell is resisted (C: lore_attr_resist). Same
// value shape as lore-color-base:.
//
// Token LORE_COLOUR_RESIST coded before 261112, commented in full on 261112.
LORE_COLOUR_RESIST
        :   'lore-color-resist:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// "lore-color-immune:<colour>" - the lore colour for the tier when the player is immune (C: lore_attr_immune).
// Same value shape as lore-color-base:.
//
// Token LORE_COLOUR_IMMUNE coded before 261112, commented in full on 261112.
LORE_COLOUR_IMMUNE
        :   'lore-color-immune:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// "message-save:<text>" - the message shown when the player saves against the spell. C
// registers it as a "str" and appends to the text when the line is repeated.
//
// Token MESSAGE_SAVE coded before 261112, commented in full on 261112.
MESSAGE_SAVE
        :   'message-save:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// "message-vis:<text>" - the message shown when the player sees the monster cast the spell.
// C registers it as a "str" and appends to the text when the line is repeated.
//
// Token MESSAGE_VIS coded before 261112, commented in full on 261112.
MESSAGE_VIS
        :   'message-vis:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// "message-invis:<text>" - the message shown when the player cannot see the cast (C keeps it
// in the field it calls blind_message). C registers it as a "str" and appends to the text
// when the line is repeated.
//
// Token MESSAGE_INVIS coded before 261112, commented in full on 261112.
MESSAGE_INVIS
        :   'message-invis:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// "message-miss:<text>" - the message shown when the spell misses. C registers it as a "str"
// and appends to the text when the line is repeated.
//
// Token MESSAGE_MISS coded before 261112, commented in full on 261112.
MESSAGE_MISS
        :   'message-miss:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

// --- HIT_MODE: the number after hit: --------------------------------------
mode HIT_MODE;

// The value of "hit:<n>" - C's "hit uint index" field, read by parser_parse in
// parser.c with strtoul in base 0. The token follows that reading:
//  - an optional '-' or '+' sign. A '-' is kept so the assembler can refuse it,
//    "-0" included, as C does for a uint.
//  - an optional 0x or 0X prefix, then one or more digits. a-f are allowed with
//    or without the prefix, so "ff" lexes here; the assembler refuses it
//    because strtoul finds no digit in it.
//  - any tail up to the colon or the end of the line, because strtoul stops at
//    the first character it cannot use and ignores the rest ("5abc" is 5, and a
//    trailing blank or tab is part of the tail). A decimal digit run is the
//    same token, so 010 is read as octal by the assembler, not here.
//
// Token HIT_UINT coded before 261112, commented in full on 261112, renamed in the comment from
// UINT on 261112.
HIT_UINT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

// End of the hit: line: leave the mode and drop the newline. (The UINT tail already takes any
// blank before the newline.)
//
// Token HIT_TEXT_EOL coded before 261112, commented in full on 261112.
HIT_TEXT_EOL
        :   NL ->skip, popMode
        ;

// --- MS_FREE_TEXT_MODE: verbatim text to the end of the line -----------------
mode MS_FREE_TEXT_MODE;

// Everything up to the line end, taken as-is (a name, a message type, lore text, a colour or a
// message). C's "str" fields also keep the whole remainder of the line.
//
// Token STRING coded before 261112, commented in full on 261112.
STRING
        :   ~('\r' | '\n')+
        ;

// End of the free-text line: leave the mode and drop the newline.
//
// Token FREE_TEXT_EOL coded before 261112, commented in full on 261112.
FREE_TEXT_EOL
        :   NL  -> skip, popMode
        ;
