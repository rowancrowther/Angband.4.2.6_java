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

// Parser for lib/gamedata/monster_base.txt (see MonsterBaseLexer for the token
// definitions and the C cross-reference). Extraction-only: every rule returns
// raw strings which MonsterBaseAssembler resolves into a domain MonsterBase
// (RF_ race flags, the glyph, and the pain: index looked up against pain.txt).
// Cf. init_parse_mon_base() (src/mon-init.c:1069).
//
// @author Rowan Crowther

parser grammar MonsterBaseGrammar;

options { tokenVocab = MonsterBaseLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

// "name:<text>" - starts a new template record; this name is referenced by
// monster.txt's base: lines.
name
        returns[String nameStr, int lineNo]
        :   NAME STRING { $nameStr = $STRING.getText();
                          $lineNo = $start.getLine(); }
        ;

// "glyph:<char>" - default display character for monsters using this template.
glyph
        returns[String glyphChar]
        :   GLYPH SINGLE_CHAR {
            $glyphChar = $SINGLE_CHAR.getText();
        }
        ;

// "pain:<index>" - which pain.txt message set monsters using this
// template use.
pain
        returns[String monPain]
        :   PAIN UINT {
            $monPain = $UINT.getText();
        }
        ;

// "flags:<RF_FLAG> [| <RF_FLAG> ...]" - race flags every monster using this
// template inherits; can repeat (see `monsterBase`'s union accumulation).
flags
        returns[List<String> raceFlags]
        @init {
            $raceFlags = new ArrayList<>();
        }
        :   FLAGS (FLAG_OR+
        |   flag1=FLAG { $raceFlags.add($flag1.getText()); }
            (FLAG_OR (flag2=FLAG { $raceFlags.add($flag2.getText()); })?)*        
            )? (FLAG_EOL | EOF)
        ;

// "desc:<text>" - lore description shown by the '/' recall command.
desc
        returns[String description]
        @init {
            StringBuilder sb = new StringBuilder();
        }
        @after {
            $description = sb.toString();
        }
        :   (DESC STRING { sb.append($STRING.getText()); })+
        ;

// One template record: name: first, then any number of the other four directives
// in any order, each optional. Absent values arrive as "" (glyph and pain), as null
// (desc) or as an empty list (flags) and the assembler turns them into C's defaults,
// so a record of a name alone is legal. glyph and pain keep the last one written, as
// C's handlers overwrite their field. flags lines accumulate into one list and
// desc lines are joined end to end, wherever they sit in the record, as C ORs each
// flags: line into the base and string_append joins each desc: line.
monsterBase
        returns[MonsterBaseParseRecord base]
        @init {
            String nameInit = "";
            String glyphInit = "";
            String monPainInit = "";
            List<String> flagsInit = new ArrayList<>();
            int lineNo = 0;
            StringBuilder descInit = new StringBuilder(); 
        }
        @after{
            String descFinal = descInit.toString();
            if (descFinal.isEmpty())
                descFinal = null;
            $base = new MonsterBaseParseRecord(nameInit, descFinal, flagsInit,
                                    glyphInit, monPainInit, descFinal,
                                    lineNo);
        }
        :   name { nameInit = $name.nameStr;
                   lineNo = $name.lineNo; }
        (   glyph { glyphInit = $glyph.glyphChar; }
        |   pain { monPainInit = $pain.monPain; }
        |   flags { flagsInit.addAll($flags.raceFlags); }
        |   desc { descInit.append($desc.description); } )*
        ;

// Top-level rule: any number of template records, including none, as C accepts an
// empty file.
file
        returns[List<MonsterBaseParseRecord> bases]
        @init {
            $bases = new ArrayList<>();
        }
        :   (monsterBase { $bases.add($monsterBase.base); })* EOF
        ;
