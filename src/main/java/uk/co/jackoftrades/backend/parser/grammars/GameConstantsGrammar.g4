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

parser grammar GameConstantsGrammar;

options { tokenVocab = GameConstantsLexer; }

@header
        {
            import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsParseRecord;

            import java.util.List;
            import java.util.ArrayList;
        }

field
        returns[String fieldValue]
        :   GC_NAME { $fieldValue = $GC_NAME.getText(); }
        |   INTEGER { $fieldValue = $INTEGER.getText(); }
        |   GC_MSG { $fieldValue = $GC_MSG.getText(); }
        ;

line
        returns[String category, List<String> fields, int lineNo]
        @init {
            $fields = new ArrayList<>();
        }
        :   { $lineNo = $start.getLine(); }
            GC_NAME { $category = $GC_NAME.getText(); }
            (COLON f=field { $fields.add($f.fieldValue); })+
        ;

file    returns[ArrayList<GameConstantsParseRecord> results]
        @init {
            $results = new ArrayList<>();
        }
        :   (line {
                        $results.add(new GameConstantsParseRecord($line.category,
                                                                   $line.fields,
                                                                   $line.lineNo));
                  })*
            EOF
        ;