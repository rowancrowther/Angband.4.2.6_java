parser grammar PainGrammar;

options { tokenVocab = PainLexer; }


@header {
    import uk.co.jackoftrades.backend.parser.pain.PainParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

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

// "message:<text>" - one graduated pain message; should appear exactly 7
// times per entry (see top-of-file problem #1).
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