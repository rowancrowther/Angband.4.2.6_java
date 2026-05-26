grammar Pain;

@header {
    import uk.co.jackoftrades.middle.monsters.MonsterPain;

    import java.util.List;
    import java.util.ArrayList;
}

type
        returns[int typeNum]
        :   TYPE NUMBER {
                $typeNum = Integer.parseInt($NUMBER.getText());
            }
        ;

message
        returns[String msgStr]
        :   MESSAGE TEXT {
                $msgStr = $TEXT.getText();
            }
        ;

painEntry
        returns[MonsterPain monPain]
        @init {
            int monTypeNum = 0;
            List<String> messageList = new ArrayList<>();
        }
        @after {
            $monPain = new MonsterPain(monTypeNum, messageList);
        }
        :   type { monTypeNum = $type.typeNum; }
            (msg=message { messageList.add($msg.msgStr); })+
        ;

file
        returns[List<MonsterPain> monsterPain]
        @init {
            $monsterPain = new ArrayList<>();
        }
        : (painEntry {
            $monsterPain.add($painEntry.monPain);
        })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

TYPE
        :   'type:'
        ;

MESSAGE
        :   'message:'
        ;

NUMBER
        :   ('0'..'9')+
        ;

TEXT
        :   ('A'..'Z' | 'a'..'z' | '[' | ']' | '.' | ' ' | '|')+
        ;