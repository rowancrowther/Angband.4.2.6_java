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

// Parser for lib/gamedata/flavor.txt. Pairs with FlavourLexer. Cf. the C
// directive table in init_parse_flavor() (src/init.c:4242-4249).
//
// Grammar shape mirrors the file's block structure: a "kind:" line and the run
// of "flavor:"/"fixed:" lines beneath it form one section, and the file is a
// record-count header followed by one or more sections. Each rule builds the
// matching raw ParseRecord (all fields kept as their source strings); resolving
// tval, colour and the fixed: sval symbol is the assemblers' job.
//
// @author Rowan Crowther

parser grammar FlavourGrammar;

options { tokenVocab = FlavourLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.flavour.FlavourKindParseRecord;
    import uk.co.jackoftrades.backend.parser.flavour.FlavourParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

// The "record-count:<n>" header. Its declared count is carried up to the reader,
// which checks it against the number of sections actually parsed.
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

// A "kind:<tval>:<glyph>" line: the object type and the display glyph shared by
// every flavour in the block that follows.
kind
        returns[String tVal, String glyph]
        :   KIND t=STRING COLON g=STRING
            { $tVal = $t.getText(); $glyph = $g.getText(); }
        ;

// A "fixed:<index>:<sval>:<colour>:<text>" line - a flavour bound to a named
// sub-type. Unlike flavour, it carries an sval symbol and always a description.
fixed
        returns[String index, String sval, String colour, String text]
        :   FIXED i=STRING COLON s=STRING COLON
            c=STRING COLON t=STRING
            { $index = $i.getText(); $sval = $s.getText();
              $colour = $c.getText(); $text = $t.getText(); }
        ;

// A "flavor:<index>:<colour>:<text?>" line - a randomly-assigned flavour. The
// description is optional; when absent (scrolls), text stays null.
flavour
        returns[String index, String colour, String text]
        @init { $text = null; }
        :   FLAVOUR i=STRING COLON
            c=STRING { $index = $i.getText();
                       $colour = $c.getText(); }
            (COLON t=STRING { $text = $t.getText(); })?

        ;

// The run of flavour/fixed lines under one kind: header, collected in file order.
// Fixed records are distinguished downstream by carrying a non-null sval (plain
// flavours pass null here).
fixedOrFlavourBlock
        returns[List<FlavourParseRecord> flavours]
        @init { $flavours = new ArrayList<>(); }
        :   (fixed { $flavours.add(new FlavourParseRecord($fixed.index,
                    $fixed.sval, $fixed.colour, $fixed.text,
                    $fixed.start.getLine())); }
        |   flavour { $flavours.add(new FlavourParseRecord($flavour.index,
                    null, $flavour.colour, $flavour.text,
                    $flavour.start.getLine())); })+
        ;

// One "kind:" header plus its block of flavours - the top-level record the
// reader counts and the assembler turns into a FlavourKind.
section
        returns[FlavourKindParseRecord kinds]
        :   kind
            fixedOrFlavourBlock
            {
                $kinds = new FlavourKindParseRecord($kind.tVal, $kind.glyph,
                $fixedOrFlavourBlock.flavours, $kind.start.getLine());
            }
        ;

// The whole file: the record-count header followed by one or more kind sections.
file
        returns[String declaredRecordCount, List<FlavourKindParseRecord> sections]
        @init { $sections = new ArrayList<>(); }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (section { $sections.add($section.kinds); })+ EOF
        ;
