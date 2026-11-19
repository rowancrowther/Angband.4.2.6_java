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

// Parser for lib/gamedata/slay.txt (see SlayLexer for the token definitions and
// the C cross-reference). Extraction-only: every rule returns raw strings that
// are assembled into domain slays - with race-flag / monster-base resolution
// and the refusal of a slay naming both race-flag and base - by SlayAssembler.
// Cf. init_parse_slay() in obj-init.c.
//
// @author Rowan Crowther

parser grammar SlayGrammar;

options { tokenVocab = SlayLexer; }

@header {
    import java.util.List;
    import java.util.ArrayList;

    import uk.co.jackoftradesltd.backend.parser.slay.SlayParseRecord;
}

// "code:<CODE>" - the record header that opens each slay. Also captures the
// source line number so the assembler can report errors against the record.
code
        returns[String codeStr, int lineNo]
        :   CODE STRING {
                $codeStr = $STRING.getText();
                $lineNo = $start.getLine();
            }
        ;

// "name:<text>" - name of the slain creatures.
name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

// "race-flag:<FLAG>" - monster race flag affected (one of the RF_ flags).
raceFlag
        returns[String rFlag]
        :   RACE_FLAG FLAG { $rFlag = $FLAG.getText(); }
        ;

// "base:<name>" - monster base affected; the alternative to race-flag.
base
        returns[String baseStr]
        :   BASE STRING { $baseStr = $STRING.getText(); }
        ;

// "multiplier:<n>" - standard-combat damage multiplier.
multiplier
        returns[String mult]
        :   MULTIPLIER UINT { $mult = $UINT.getText(); }
        ;

// "o-multiplier:<n>" - O-combat damage multiplier.
oMultiplier
        returns[String oMult]
        :   O_MULTIPLIER UINT { $oMult = $UINT.getText(); }
        ;

// "power:<n>" - object-power weighting.
power
        returns[String powerInt]
        :   POWER UINT { $powerInt = $UINT.getText(); }
        ;

// "melee-verb:<text>" - verb shown on a susceptible melee hit.
meleeVerb
        returns[String mVerb]
        :   MELEE_VERB STRING { $mVerb = $STRING.getText(); }
        ;

// "range-verb:<text>" - verb shown on a susceptible ranged hit.
rangeVerb
        returns[String rVerb]
        :   RANGE_VERB STRING { $rVerb = $STRING.getText(); }
        ;

// One slay record: a mandatory code header followed by its directives in any
// order and any number (mirrors the C parser's keyword dispatch). Each field
// defaults to the empty string when absent, and a repeated directive keeps the
// last value, as in C except for race-flag: and base:, which C refuses to
// repeat. The grammar intentionally does NOT refuse a slay naming both
// race-flag and base - that semantic constraint lives in SlayAssembler.
slay
        returns[SlayParseRecord record]
        @init {
            String codeInit = "";
            String nameInit = "";
            String raceFlagInit = "";
            String baseInit = "";
            String multInit = "";
            String oMultInit = "";
            String powerInit = "";
            String mVerbInit = "";
            String rVerbInit = "";
            int line = 0;
        }
        @after {
                $record = new SlayParseRecord(codeInit, nameInit,
                raceFlagInit, baseInit, multInit, oMultInit,
                powerInit, mVerbInit, rVerbInit, line);
            }
        :   code {
                codeInit = $code.codeStr;
                line = $code.lineNo;
            }
        (   name { nameInit = $name.nameStr; }
        |   raceFlag { 
                if (raceFlagInit.isEmpty())
                    raceFlagInit = $raceFlag.rFlag;
                else
                    raceFlagInit = "ERROR";
            }
        |   base {
                if (baseInit.isEmpty())
                    baseInit = $base.baseStr;
                else
                    baseInit = "ERROR"; 
            }
        |   multiplier { multInit = $multiplier.mult; }
        |   oMultiplier { oMultInit = $oMultiplier.oMult; }
        |   power { powerInit = $power.powerInt; }
        |   meleeVerb { mVerbInit = $meleeVerb.mVerb; }
        |   rangeVerb { rVerbInit = $rangeVerb.rVerb; }
        )*;

// Top-level rule: one or more slay records.
file
        returns[List<SlayParseRecord> records]
        @init {
            $records = new ArrayList<>();
        }
        :   (slay { $records.add($slay.record); })* EOF
        ;
