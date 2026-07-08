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

// Parser for lib/gamedata/brand.txt (see BrandLexer for the token definitions
// and the C cross-reference). Extraction-only: every rule returns raw strings
// that BrandAssembler resolves into domain brands (the resist/vuln race flags
// and the numeric fields). Cf. init_parse_brand() (src/obj-init.c:959).
//
// @author Rowan Crowther

parser grammar BrandGrammar;

options { tokenVocab = BrandLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.brand.BrandParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

// "record-count:<n>" header; the declared count is handed to the reader to
// check against the number of records actually parsed.
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

// "code:<CODE>" - the record header that opens each brand. Also captures the
// source line number so the assembler can report errors against the record.
code
        returns[String codeStr, int lineNo]
        :   CODE STRING { $codeStr = $STRING.getText();
                          $lineNo = $start.getLine(); }
        ;

// "name:<text>" - the element's display name.
name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

// "multiplier:<n>" - standard-combat damage multiplier.
multiplier
        returns[String mult]
        :   MULTIPLIER INTEGER { $mult = $INTEGER.getText(); }
        ;

// "o-multiplier:<n>" - O-combat damage multiplier.
oMultiplier
        returns[String oMult]
        :   O_MULTIPLIER INTEGER { $oMult = $INTEGER.getText(); }
        ;

// "power:<n>" - object-power weighting.
power
        returns[String powerStr]
        :   POWER INTEGER { $powerStr = $INTEGER.getText(); }
        ;

// "verb:<text>" - verb shown on a susceptible hit.
verb
        returns[String verbStr]
        :   VERB STRING { $verbStr = $STRING.getText(); }
        ;

// "resist-flag:<FLAG>" - monster race flag granting resistance to the brand.
resistFlag
        returns[String rFlag]
        :   RESIST_FLAG FLAG { $rFlag = $FLAG.getText(); }
        ;

// "vuln-flag:<FLAG>" - monster race flag marking vulnerability to the brand.
vulnFlag
        returns[String vFlag]
        :   VULN_FLAG FLAG { $vFlag = $FLAG.getText(); }
        ;

// One brand record: a fixed sequence of code/name/verb/multiplier/o-multiplier/
// power/resist-flag, then an OPTIONAL vuln-flag. resist-flag and vuln-flag are
// independent (no exclusivity rule, unlike Slay's race-flag/base) - only six of
// ten shipped brands carry a vuln-flag.
brand
        returns[BrandParseRecord record]
        @init {
            int lineInit = 0;
            String codeInit = "";
            String nameInit = "";
            String multiplierInit = "";
            String oMultiplierInit = "";
            String powerInit = "";
            String verbInit = "";
            String resistFlagInit = "";
            String vulnFlagInit = "";
        }
        @after {
            $record = new BrandParseRecord(codeInit, nameInit, multiplierInit,
                    oMultiplierInit, powerInit, verbInit, resistFlagInit,
                    vulnFlagInit, lineInit);
        }
        :   code { lineInit = $code.lineNo;
                   codeInit = $code.codeStr; }
            name { nameInit = $name.nameStr; }
            verb { verbInit = $verb.verbStr; }
            multiplier { multiplierInit = $multiplier.mult; }
            oMultiplier { oMultiplierInit = $oMultiplier.oMult; }
            power { powerInit = $power.powerStr; }
            resistFlag { resistFlagInit = $resistFlag.rFlag; }
            (vulnFlag { vulnFlagInit = $vulnFlag.vFlag; })?
        ;

// Top-level rule: the record-count header followed by one or more brand records.
file
        returns[List<BrandParseRecord> records, String declaredRecordCount]
        @init {
            $records = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (brand { $records.add($brand.record); })+ EOF
        ;
