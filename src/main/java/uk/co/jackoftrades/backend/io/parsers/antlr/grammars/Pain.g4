grammar Pain;

@header {
    import java.util.ArrayList;

    import uk.co.jackoftrades.middle.monsters.MonsterPain;
}

type
        returns[int index]
        :   TYPE NUMBER {
            $index = Integer.parseInt($NUMBER.getText());
        };

message
        returns[String msg]
        :   MESSAGE TEXT {
            $msg = $TEXT.getText();
        };

entry
        returns[MonsterPain pain]
        :   type
            msg1=message
            msg2=message
            msg3=message
            msg4=message
            msg5=message
            msg6=message
            msg7=message {

            ArrayList<String> messages = new ArrayList<>();
            messages.add($msg1.msg);
            messages.add($msg2.msg);
            messages.add($msg3.msg);
            messages.add($msg4.msg);
            messages.add($msg5.msg);
            messages.add($msg6.msg);
            messages.add($msg7.msg);

            $pain = new MonsterPain($type.index, messages);
        };

painMessages
        returns[ArrayList<MonsterPain> monsterPainMessages]
        @init{
        $monsterPainMessages = new ArrayList<>();
        }
        :   (entry {
            $monsterPainMessages.add($entry.pain);
        })+;

COMMENT
        :   '#' ~':' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* ('\r'?'\n') -> skip
        ;
TYPE
        :   'type:'
        ;

MESSAGE
        :   'message:'
        ;

TEXT
        :   ([ a-zA-Z.|]|'['|']')+
        ;

NUMBER
        :   [0-9]+
        ;