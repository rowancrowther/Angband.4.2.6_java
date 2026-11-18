// Parser for quest.txt (C: player-quest.c, init_parse_quest and its four parse_quest_* hooks).
//
// C registers four independent one-line directives: only `name:` starts a quest, and `level:`,
// `race:` and `number:` fill the quest started by the last `name:`, in any order, any number of times
// (the last wins) or not at all. `quest` below groups the lines the same way, so `name` is mandatory
// and the other three are optional and repeatable. A file with no quest at all is legal, as in C.
// A level, race or number line ahead of the first name is a syntax error here, where C asserts on a
// null quest (accepted deviation, docs/Accepted_Deviations.md, Quest, 261118).
//
// Every value is passed on as raw text inside a Directive; QuestAssembler does the conversion.
// Each directive's line is the line of its keyword token. Commented 261118.
parser grammar QuestGrammar;

options { tokenVocab = QuestLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.quest.QuestParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
}

name
        returns[Directive nameDirective]
        :   NAME STRING { 
                String nameStr = $STRING.getText();
                int line = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(nameStr);
                $nameDirective = new Directive("name", values, line);
            }
        ;

level
        returns[Directive levelDirective]
        :   LEVEL INTEGER { 
                String levelStr = $INTEGER.getText();
                int line = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(levelStr);
                $levelDirective = new Directive("level", values, line); 
            }
        ;

race
        returns[Directive raceDirective]
        :   RACE STRING { 
                String monRace = $STRING.getText();
                int line = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(monRace);
                $raceDirective = new Directive("race", values, line); 
            }
        ;

number
        returns[Directive numberDirective]
        :   NUMBER INTEGER { 
                String value = $INTEGER.getText();
                int line = $start.getLine();
                List<String> values = new ArrayList<>();
                values.add(value);
                $numberDirective = new Directive("number", values, line);
            }
        ;

quest
        returns[List<Directive> q]
        @init {
            $q = new ArrayList<>();
        }
        :   name { $q.add($name.nameDirective); }
        (   level { $q.add($level.levelDirective); }
        |   race { $q.add($race.raceDirective); }
        |   number { $q.add($number.numberDirective); })*
        ;

file
        returns[List<QuestParseRecord> quests]
        @init {
            $quests = new ArrayList<>();
        }
        :   (quest { $quests.add(new QuestParseRecord($quest.q)); })* EOF
        ;