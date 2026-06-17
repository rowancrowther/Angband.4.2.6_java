grammar BlowMethod;

@header {
    import uk.co.jackoftrades.middle.combat.BlowMethod;
    import uk.co.jackoftrades.middle.enums.MessageType;

    import java.util.List;
    import java.util.ArrayList;
}

name
        returns[String nameStr]
        :   NAME UCASE { $nameStr = $UCASE.getText(); }
        ;

cut
        returns[boolean cutBool]
        :   CUT BOOLEAN { $cutBool = $BOOLEAN.getText().equals("1"); }
        ;

stun
        returns[boolean stunBool]
        :   STUN BOOLEAN { $stunBool = $BOOLEAN.getText().equals("1"); }
        ;

miss
        returns[boolean missBool]
        :   MISS BOOLEAN { $missBool = $BOOLEAN.getText().equals("1"); }
        ;

phys
        returns[boolean physBool]
        :   PHYS BOOLEAN { $physBool = $BOOLEAN.getText().equals("1"); }
        ;

msg
        returns[MessageType message]
        :   MSG UCASE {
                String raw = $UCASE.getText();
                $message = MessageType.valueOf("MSG_" + raw);
            }
        ;

act
        returns[String actStr]
        :   ACT LCASE { $actStr = $LCASE.getText(); }
        ;

desc
        returns[String descStr]
        :   DESC LCASE { $descStr = $LCASE.getText(); }
        ;

blowMethod
        returns[BlowMethod method]
        @init {
            String nameInit = "";
            boolean cutInit = false;
            boolean stunInit = false;
            boolean missInit = false;
            boolean physInit = false;
            MessageType msgInit = MessageType.MSG_NONE;
            List<String> actInit = new ArrayList<>();
            String descInit = "";
        }
        @after {
            $method = new BlowMethod(nameInit, cutInit, stunInit, missInit, physInit,
                                     msgInit, actInit, descInit);
        }
        :   name { nameInit = $name.nameStr; }
            cut { cutInit = $cut.cutBool; }
            stun { stunInit = $stun.stunBool; }
            miss { missInit = $miss.missBool; }
            phys { physInit = $phys.physBool; }
            msg { msgInit = $msg.message; }
            (act { actInit.add($act.actStr); })+
            desc { descInit = $desc.descStr; }
        ;

file
        returns[List<BlowMethod> methods]
        @init {
            $methods = new ArrayList<>();
        }
        :   (blowMethod { $methods.add($blowMethod.method); })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:'
        ;

CUT
        :   'cut:'
        ;

STUN
        :   'stun:'
        ;

MISS
        :   'miss:'
        ;

PHYS
        :   'phys:'
        ;

MSG
        :   'msg:'
        ;

ACT
        :   'act:'
        ;

DESC
        :   'desc:'
        ;

BOOLEAN
        :   ('0' | '1')
        ;

UCASE
        :   ('A'..'Z' | '_')+
        ;

LCASE
        :   ('a'..'z' | '{' | '}' | ' ' | '!' | '\'' | 'A'..'Z' | '?')+
        ;