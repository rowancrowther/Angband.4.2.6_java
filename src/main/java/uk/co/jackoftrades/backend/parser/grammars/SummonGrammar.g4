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
// Cf. init_parse_summon() (src/mon-summon.c:146).
//
// @author Rowan Crowther

parser grammar SummonGrammar;

options { tokenVocab = SummonLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.summon.SummonParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

// "record-count:<n>" header; the declared count is handed to the reader to
// check against the number of records actually parsed.
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

// "name:<CODE>" - the record header that opens each summon. Also captures the
// source line number so the assembler can report errors against the record.
name
        returns[String nameStr, int lineNo]
        :   NAME STRING { $nameStr = $STRING.getText();
                          $lineNo = $start.getLine(); }
        ;

// "msgt:<TYPE>" - message type symbol (e.g. SUM_MONSTER).
msgt
        returns[String msgtStr]
        :   MSGT FLAG { $msgtStr = $FLAG.getText(); }
        ;

// "uniques:<0|1>" - whether uniques may be summoned.
uniques
        returns[String uniquesStr]
        :   UNIQUES INTEGER { $uniquesStr = $INTEGER.getText(); }
        ;

// "base:<name>" [repeated] - the allowed monster bases. One or more base: lines
// accumulate into the list here; the caller wraps this rule in (base)? so a
// summon with no base restriction is also legal.
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

// "desc:<text>" - the summon type's description.
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

// One summon record: a fixed sequence of name/msgt/uniques, then the optional
// base(s)/race-flag/fallback block, then desc. base, race-flag and fallback are
// all optional and independent; base comes before race-flag (the only record
// with both, WRAITH, lists them in that order).
summon
        returns[SummonParseRecord record]
        @init {
            String nameInit = "";
            String msgTypeInit = "";
            String uniquesInit = "";
            List<String> basesInit = new ArrayList<>();
            String raceFlagInit = "";
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
            msgt { msgTypeInit = $msgt.msgtStr; }
            uniques { uniquesInit = $uniques.uniquesStr; }
            (base { basesInit = $base.baseStr; } )?
            (raceFlag { raceFlagInit = $raceFlag.flagStr; })?
            (fallback { fallbackInit = $fallback.fallbackStr; })?
            desc { descInit = $desc.description; }
        ;

// Top-level rule: the record-count header followed by one or more summon records.
file
        returns[List<SummonParseRecord> summons, String declaredRecordCount]
        @init {
            $summons = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (summon { $summons.add($summon.record); })+ EOF
        ;
