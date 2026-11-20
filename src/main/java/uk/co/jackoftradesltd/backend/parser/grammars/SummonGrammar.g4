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

// Parser for lib/gamedata/summon.txt (see SummonLexer for the token definitions
// and the C cross-reference). Extraction-only: every rule returns raw strings
// that SummonAssembler resolves into domain summons (the message type, the
// monster bases, the race flag, and the fallback reference to a sibling summon).
// Cf. init_parse_summon() in mon-summon.c, which registers the seven directives
// as independent handlers: after name:, the others come in any order, may repeat
// and may be left out. The file itself may hold no records at all.
//
// @author Rowan Crowther

parser grammar SummonGrammar;

options { tokenVocab = SummonLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.summon.SummonParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

// "name:<CODE>" - the record header that opens each summon. Also captures the
// source line number so the assembler can report errors against the record.
name
        returns[String nameStr, int lineNo]
        :   NAME STRING { $nameStr = $STRING.getText();
                          $lineNo = $start.getLine(); }
        ;

// "msgt:<TYPE>" - message type name or number (e.g. SUM_MONSTER). The text is
// the whole rest of the line, kept raw; SummonAssembler skips leading colons,
// cuts at the next ':' and resolves what is left, as C does.
msgt
        returns[String msgtStr]
        :   MSGT msg=MSGT_STRING { $msgtStr = $msg.getText(); }
        ;

// "uniques:<number>" - whether uniques may be summoned. The text is kept raw;
// the assembler reads any non-zero number as true. A record may carry several.
uniques
        returns[String uniquesStr]
        :   UNIQUES INTEGER { $uniquesStr = $INTEGER.getText(); }
        ;

// "base:<name>" [repeated] - the allowed monster bases. A run of adjacent base:
// lines accumulates into one list here; the summon rule adds each run to the
// record's list, so base: lines split by other directives are all kept too.
base
        returns[List<String> baseStr]
        @init {
            $baseStr = new ArrayList<>();
        }
        :   (BASE STRING { $baseStr.add($STRING.getText()); })+
        ;

// "race-flag:<FLAG>" - an allowed monster race flag.
raceFlag
        returns[String flagStr]
        :   RACE_FLAG FLAG { $flagStr = $FLAG.getText(); }
        ;

// "fallback:<NAME>" - the name of the summon type to fall back to on failure.
fallback
        returns[String fallbackStr]
        :   FALLBACK STRING { $fallbackStr = $STRING.getText(); }
        ;

// "desc:<text>" - the summon type's description. A second desc: in the same
// record replaces the first, as in C.
desc
        returns[String description]
        @init {
            StringBuilder sb = new StringBuilder();
        }
        @after {
            $description = sb.toString();
        }
        :   ( DESC STRING { sb.append($STRING.getText()); })
        ;

// One summon record: name: first, then any number of the other six directives in
// any order, each optional. Absent values arrive as "" (fallback and desc) or as
// an empty list (the other four) and the assembler turns them into C's defaults,
// so a record of a name alone is legal. fallback and desc keep the last one
// written, as C's handlers overwrite their field. msgt, uniques, base and
// race-flag are kept line by line, in file order: C refuses a bad msgt, base or
// race-flag line when it reads it, so the assembler must see every one, and it
// takes the last msgt and race-flag as the value.
summon
        returns[SummonParseRecord record]
        @init {
            String nameInit = "";
            List<String> msgTypeInit = new ArrayList<>();
            List<String> uniquesInit = new ArrayList<>();
            List<String> basesInit = new ArrayList<>();
            List<String> raceFlagInit = new ArrayList<>();
            String fallbackInit = "";
            String descInit = "";
            int line = 0;
        }
        @after {
            $record = new SummonParseRecord(nameInit, msgTypeInit,
                uniquesInit, basesInit, raceFlagInit, fallbackInit,
                descInit, line);
        }
        :   name { line = $name.lineNo;
                   nameInit = $name.nameStr; }
        (   msgt { msgTypeInit.add($msgt.msgtStr); }
        |   uniques { uniquesInit.add($uniques.uniquesStr); }
        |   base { basesInit.addAll($base.baseStr); }
        |   raceFlag { raceFlagInit.add($raceFlag.flagStr); }
        |   fallback { fallbackInit = $fallback.fallbackStr; }
        |   desc { descInit = $desc.description; })*
        ;

// Top-level rule: any number of summon records, including none, as C accepts an
// empty file.
file
        returns[List<SummonParseRecord> summons]
        @init {
            $summons = new ArrayList<>();
        }
        :   (summon { $summons.add($summon.record); })* EOF
        ;
