parser grammar BodyGrammar;

options { tokenVocab = BodyLexer; }

@header {
    import java.util.List;
    import java.util.ArrayList;

    import uk.co.jackoftrades.backend.parser.body.BodyParseRecord;
    import uk.co.jackoftrades.backend.parser.body.BodyParseRecord.BodySlotRecord;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

body
        returns[String bodyName, int line]
        :   BODY STRING { $bodyName = $STRING.getText();
                          $line = $start.getLine(); }
        ;

slot
        returns[String slotType, String slotName]
        :   SLOT t=SLOT_NAME COLON n=STRING {
                $slotType = $t.getText();
                $slotName = $n.getText();
            }
        ;

bodyType
        returns[BodyParseRecord record]
        @init {
            String bodyNameInit = "";
            int line = 0;
            List<BodySlotRecord> slots = new ArrayList<>();
        }
        @after {
            $record = new BodyParseRecord(bodyNameInit, slots, line);
        }
        :   body {
                    bodyNameInit = $body.bodyName;
                    line = $body.line;
                 }
            (slot {
                String slotType = $slot.slotType;
                String slotName = $slot.slotName;
                slots.add(new BodySlotRecord(slotType, slotName));
            })+
        ;

file
        returns[String declaredRecordCount, List<BodyParseRecord> bodies]
        :   recordCount { $declaredRecordCount = $recordCount.count;
                          $bodies = new ArrayList<>(); }
            (bodyType { $bodies.add($bodyType.record); })+ EOF
        ;
