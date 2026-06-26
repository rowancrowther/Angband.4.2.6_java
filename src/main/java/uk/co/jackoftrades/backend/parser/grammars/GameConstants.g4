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

// Reader+lexer for lib/gamedata/constants.txt - Angband's miscellaneous
// engine-tuning constants (level/monster-generation maxima, critical-hit
// tables, dungeon-generation limits, etc). Cf. the C parser in src/init.c:
// struct file_parser constants_parser (init.c:1051), whose directive table
// is registered in init_parse_constants() (init.c:949-980). Every directive
// there has the shape "<category> sym label int value" EXCEPT the 4
// critical-hit-level ones, which instead carry several int fields plus a
// trailing str message - that 3-vs-4-numeric-field split is exactly what
// `furtherValue`/`multiValue` below exist to handle:
//   - "category:label:value"                       -> `section`
//     (level-max/mon-gen/mon-play/dun-gen/world/carry-cap/store/obj-make/
//     player/melee-critical/ranged-critical/o-melee-critical/
//     o-ranged-critical)
//   - "category:value:value:MSG_NAME"               -> `furtherValue`
//     (o-melee-critical-level: "uint chance uint dice str msg",
//      o-ranged-critical-level: same)
//   - "category:value:value:value:MSG_NAME"         -> `multiValue`
//     (melee-critical-level: "int cutoff int mult int add str msg",
//      ranged-critical-level: same)
// Downstream, uk.co.jackoftrades.middle.game.globals.GameConstants.
// loadGameConstants() dispatches on `Entry.key()` (the category) and each
// setXxx() helper re-splits `Entry.value()` on ':' to recover the
// individual fields - confirmed correct against this grammar's output by
// reading both sides, so the string-concatenate-then-re-split shape is a
// deliberate (if unusual) design rather than a bug.
//
// POTENTIAL PROBLEMS (minor - this file is otherwise in good shape):
//
//   1. FURTHER only matches an uppercase/underscore symbolic name
//      (e.g. "HIT_GOOD"), but the C registration types this field as a
//      general "str msg" - free text, not a restricted symbol set. Every
//      current value (HIT_GOOD/HIT_GREAT/HIT_SUPERB/HIT_HI_GREAT/
//      HIT_HI_SUPERB) happens to fit, so this is latent rather than active,
//      the same pattern as TrapLexer.g4's GRAPHICS_COLOUR.
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar GameConstants;

@header
        {
            import java.util.ArrayList;
        }

// One parsed constants.txt directive as a raw (category, rest-of-line)
// pair; see the file-level comment above for how `rest-of-line` gets
// re-split downstream.
@members
        {
            public record Entry(String key, String value) {}
        }

// "<category>:<label>:<value>" - the common case, e.g. "level-max:monsters:1024".
// Covers every constants.txt category except the 4 critical-hit-level ones.
section returns[String sect, String value]
        :   token1=TOKEN { $sect = $token1.getText(); }
            COLON token2=TOKEN
                         { $value = $token2.getText(); }
            COLON VALUE  { $value = $value + ":" + $VALUE.getText(); }
        ;

// "<category>:<value>:<value>:<MSG_NAME>" - the o-melee-critical-level/
// o-ranged-critical-level shape (chance, dice, message).
furtherValue
        returns[String sect, String further]
        :   TOKEN { $sect = $TOKEN.getText(); }
            COLON val1=VALUE
            COLON val2=VALUE
            COLON FURTHER {
                $further = $val1.getText() + ":" + $val2.getText() + ":" + $FURTHER.getText();
            }
        ;

// "<category>:<value>:<value>:<value>:<MSG_NAME>" - the melee-critical-level/
// ranged-critical-level shape (cutoff, mult, add, message).
multiValue
        returns[String sect, String multi]
        :   TOKEN {$sect = $TOKEN.getText(); }
            COLON val1=VALUE
            COLON val2=VALUE
            COLON val3=VALUE
            COLON FURTHER {
                $multi = $val1.getText() + ":" + $val2.getText() + ":" + $val3.getText() + ":" + $FURTHER.getText();
            }
        ;

// One directive line - whichever of the 3 shapes above matches.
line    returns[String sect, String val]
        :   section {
                $sect = $section.sect;
                $val  = $section.value;
        }
        |   furtherValue {
                $sect = $furtherValue.sect;
                $val  = $furtherValue.further;
        }
        |   multiValue {
                $sect = $multiValue.sect;
                $val  = $multiValue.multi;
        }
        ;

// Top-level rule: the whole file is zero or more directive lines.
file    returns[ArrayList<Entry> results]
        @init {
            $results = new ArrayList<>();
        }
        :   (line {
                        $results.add(new Entry($line.sect, $line.val));
                  })*
            EOF
        ;

// A directive's category/label name, e.g. "level-max", "monsters",
// "melee-critical-level" - lowercase words, optionally hyphenated.
TOKEN   :   ('a'..'z'|'-')+
        ;

// PROBLEM: see top-of-file comment #1 - narrower than the "str msg" field
// type it represents in the C parser, even though current data fits.
//
// The trailing message-name field on a critical-level line, e.g. "HIT_GOOD".
FURTHER :   ('A'..'Z'|'_')+
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

// Field separator used throughout every directive.
COLON   :   ':'
        ;

// A (possibly negative) literal integer field.
VALUE   :   '-'? ('0'..'9')+
        ;

// A blank line on its own (not part of a comment block).
EOL     :   '\r'? '\n' -> skip
        ;

// POTENTIAL SOLUTIONS
//
//   1. Only worth doing if a future critical-level message name needs
//      lowercase/punctuation/spaces - widen FURTHER to a general string
//      token (or reuse whatever free-text token other grammars in this
//      directory use for str-typed fields) at that point. No need to do
//      it pre-emptively since every current value already fits.