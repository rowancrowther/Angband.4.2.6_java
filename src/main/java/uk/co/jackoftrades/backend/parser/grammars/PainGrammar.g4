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

// Parser for lib/gamedata/pain.txt (see PainLexer for the token definitions and
// the C cross-reference). Extraction-only: each record is returned as its type
// number plus seven raw message strings; PainAssembler peels the type number
// off and builds the domain MonsterPain. Cf. init_parse_pain()
// (src/mon-init.c:516).
//
// @author Rowan Crowther

parser grammar PainGrammar;

options { tokenVocab = PainLexer; }


@header {
    import uk.co.jackoftrades.backend.parser.pain.PainParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

// "record-count:<n>" header; the declared count is handed to the reader to
// check against the number of records actually parsed.
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

// "type:<serial number>" - starts a new pain-message set; should increase
// for each new entry (not validated by this grammar).
type
        returns[String typeNum, int line]
        :   TYPE INTEGER {
                $line = $start.getLine();
                $typeNum = $INTEGER.getText();
            }
        ;

// "message:<text>" - one graduated pain message; exactly 7 are required per
// entry, which `painEntry` enforces structurally below.
message
        returns[String msgStr]
        :   MESSAGE STRING {
                $msgStr = $STRING.getText();
            }
        ;

// One full pain-message set: a type: header followed by its messages.
painEntry
        returns[PainParseRecord record]
        @init {
            List<String> monPain = new ArrayList<>();
            int lineNo = 0;
        }
        @after {
            $record = new PainParseRecord(monPain, lineNo);
        }
        :   type { monPain.add($type.typeNum);
                   lineNo = $type.line; }
            msg=message { monPain.add($msg.msgStr); }
            msg=message { monPain.add($msg.msgStr); }
            msg=message { monPain.add($msg.msgStr); }
            msg=message { monPain.add($msg.msgStr); }
            msg=message { monPain.add($msg.msgStr); }
            msg=message { monPain.add($msg.msgStr); }
            msg=message { monPain.add($msg.msgStr); }
        ;

// Top-level rule: the whole file is one or more pain-message sets.
file
        returns[List<PainParseRecord> monsterPain, String declaredRecords]
        @init {
            $monsterPain = new ArrayList<>();
        }
        :   recordCount { $declaredRecords = $recordCount.count; }
            (painEntry {
            $monsterPain.add($painEntry.record);
        })+ EOF
        ;
