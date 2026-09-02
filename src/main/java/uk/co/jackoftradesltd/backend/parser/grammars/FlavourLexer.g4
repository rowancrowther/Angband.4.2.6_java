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

// Lexer for lib/gamedata/flavor.txt - the table of randomised "flavours" that
// disguise unidentified objects (a potion's colour, a ring's material, a
// scroll's nonsense title) until they are identified. Cf. the C directive table
// in init_parse_flavor() (src/init.c:4242-4249) and struct flavor (src/object.h).
//
// The file is organised into blocks: a "kind:" line names an object type and the
// glyph its flavours draw with, and every "flavor:"/"fixed:" line that follows
// belongs to that block until the next "kind:". This lexer only tokenises; the
// kind/flavour grouping is expressed in FlavourGrammar and the tval/colour/sval
// interpretation is deferred to the assemblers.
//
// The value fields (index, sval symbol, colour, description) can all contain
// spaces and are read in a single ':'-delimited sub-mode (DELIM), so the lexer
// stays agnostic about which field is which - the parser positions them.
//
// @author Rowan Crowther

lexer grammar FlavourLexer;

// CommentsAndEol skips '#' comments and blank lines and supplies the default-mode
// end-of-line handling.
import CommentsAndEol;

// "record-count:<n>" - header declaring how many kind: blocks follow; checked
// against the number actually parsed by the reader.
RECORD_COUNT
        :   'record-count:'
        ;

// "kind:<tval>:<glyph>" - opens a new block, naming the object type and the
// display glyph its flavours use. Enters DELIM to read the two ':'-separated
// values.
KIND
        :   'kind:' -> pushMode(DELIM)
        ;

// "fixed:<index>:<sval>:<colour>:<text>" - a flavour pinned to one specific
// sub-type (e.g. the One Ring always looks like "Plain Gold"). The extra sval
// field is what distinguishes it from a plain flavor: line.
FIXED
        :   'fixed:' -> pushMode(DELIM)
        ;

// "flavor:<index>:<colour>:<text?>" - a randomly-assigned flavour. Spelt the
// American way to match the data file. The description is optional (scrolls omit
// it - their names are generated from name fragments at runtime).
FLAVOUR
        :   'flavor:' -> pushMode(DELIM)
        ;

// A literal integer for the record-count header. Sign-capable by shared suite
// convention; flavour counts are always non-negative.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Value mode entered after any kind:/fixed:/flavor: keyword. It reads the
// ':'-separated fields of one line and pops back at end-of-line so the next
// line's keyword is recognised in the default mode.
mode DELIM;

// One field's worth of text: everything up to the next ':' or end-of-line.
// Serves every value position - numeric index, sval symbol, colour name or code,
// and description - which are told apart by the parser, not here. Note this
// out-greedies a bare integer, so the index is lexed as STRING and parsed to an
// int in the assembler.
STRING
        :   ~('\r' | '\n' | ':')+
        ;

// The field separator within a kind:/fixed:/flavor: line.
COLON
        :   ':'
        ;

// End-of-line closes the value line: pop back to the default mode and discard the
// newline (records are delimited structurally by the next keyword).
DELIM_EOL
        :   '\r'* '\n' -> popMode, skip
        ;
