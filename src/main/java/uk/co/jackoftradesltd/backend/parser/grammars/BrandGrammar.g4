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
// and the numeric fields). Cf. init_parse_brand() (src/obj-init.c).
//
// @author Rowan Crowther

parser grammar BrandGrammar;

options { tokenVocab = BrandLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.brand.BrandParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

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
        :   MULTIPLIER UINT { $mult = $UINT.getText(); }
        ;

// "o-multiplier:<n>" - O-combat damage multiplier.
oMultiplier
        returns[String oMult]
        :   O_MULTIPLIER UINT { $oMult = $UINT.getText(); }
        ;

// "power:<n>" - object-power weighting.
power
        returns[String powerStr]
        :   POWER UINT { $powerStr = $UINT.getText(); }
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

// One brand record: code: first, then any number of the other seven directives in
// any order, each optional, as C's init_parse_brand registers them as independent
// handlers. Absent values arrive as null (name and verb) or as an empty list (the
// other five) and the assembler turns them into C's zeroed fields, so a record of a
// code alone is legal. name and verb keep the last line written, as C's handlers
// overwrite their string. multiplier, o-multiplier, power, resist-flag and
// vuln-flag are kept line by line, in file order: C refuses a bad number or flag
// when it reads the line, so the assembler must see every one, and it takes the
// last as the value. resist-flag and vuln-flag are independent (no exclusivity
// rule, unlike Slay's race-flag/base) - four of the ten shipped brands carry a
// vuln-flag.
brand
        returns[BrandParseRecord record]
        @init {
            int lineInit = 0;
            String codeInit = "";
            String nameInit = null;
            List<String> multiplierInit = new ArrayList<>();
            List<String> oMultiplierInit = new ArrayList<>();
            List<String> powerInit = new ArrayList<>();
            String verbInit = null;
            List<String> resistFlagInit = new ArrayList<>();
            List<String> vulnFlagInit = new ArrayList<>();
        }
        @after {
            $record = new BrandParseRecord(codeInit, nameInit, multiplierInit,
                    oMultiplierInit, powerInit, verbInit, resistFlagInit,
                    vulnFlagInit, lineInit);
        }
        :   code { lineInit = $code.lineNo;
                   codeInit = $code.codeStr; }
        (   name { nameInit = $name.nameStr; }
        |   verb { verbInit = $verb.verbStr; }
        |   multiplier { multiplierInit.add($multiplier.mult); }
        |   oMultiplier { oMultiplierInit.add($oMultiplier.oMult); }
        |   power { powerInit.add($power.powerStr); }
        |   resistFlag { resistFlagInit.add($resistFlag.rFlag); }
        |   vulnFlag { vulnFlagInit.add($vulnFlag.vFlag); })*
        ;

// Top-level rule: any number of brand records, including none, as C accepts an
// empty file.
file
        returns[List<BrandParseRecord> records]
        @init {
            $records = new ArrayList<>();
        }
        :   (brand { $records.add($brand.record); })* EOF
        ;
