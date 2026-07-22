parser grammar QuestGrammar;

options { tokenVocab = QuestLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.quest.QuestParseRecord;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText();
                          $line = $start.getLine(); }
        ;

level
        returns[String levelStr]
        :   LEVEL INTEGER { $levelStr = $INTEGER.getText(); }
        ;

race
        returns[String monRace]
        :   RACE STRING { $monRace = $STRING.getText(); }
        ;

number
        returns[String num]
        :   NUMBER INTEGER { $num = $INTEGER.getText(); }
        ;

quest
        returns[QuestParseRecord q]
        @init {
            String nameInit = "";
            String levelInit = "";
            String raceInit = "";
            String numberInit = "";
            int line = 0;
        }
        @after {
            $q = new QuestParseRecord(nameInit, levelInit,
                    raceInit, numberInit, line);
        }
        :   name { line = $name.line; nameInit = $name.nameStr;}
            level { levelInit = $level.levelStr; }
            race { raceInit = $race.monRace; }
            number { numberInit = $number.num; }
        ;

file
        returns[String declaredRecordCount, List<QuestParseRecord> quests]
        @init {
            $quests = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (quest { $quests.add($quest.q); })+ EOF
        ;