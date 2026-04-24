grammar ConstantsFormatter;

@header     {   import java.util.ArrayList;
            }

section returns[String sect, String value]
        :   token1=TOKEN { $sect = $token1.getText(); }
            COLON token2=TOKEN
                         { $value = $token2.getText() }
            COLON VALUE  { $value = $value + ":" + $VALUE.getText()); }
        ;

furtherValue
        returns[String sect, String further]
        :   TOKEN { $sect = $TOKEN.getText(); }
            COLON val1=VALUE
            COLON val2=VALUE
            COLON FURTHER {
                $further = $val1.getText() + ":" + val2.getText() + ":" + $FURTHER.getText();
            }
        ;

multiValue
        returns[String sect, String multi]
        :   TOKEN {$sect = TOKEN.getText(); }
            COLON val1=VALUE
            COLON val2=VALUE
            COLON val3=VALUE
            COLON FURTHER {
                $multi = $val1.getText() + ":" + $val2.getText() + ":" + $val3.getText() + ":" + $FURTHER.getText();
            }
        ;

line    returns[String sect, String val]
        :   section EOL* {
                $sect = $section.sect;
                $val  = $section.value;
        }
        |   furtherValue EOL* {
                $sect = $furtherValue.sect;
                $val  = $furtherValue.further;
        }
        |   multiValue EOL* {
                $sect = $multiValue.sect;
                $val  = $multiValue.multi;
        }
        ;

file    returns[ArrayList<String> constants]
        @init {
            $constants = new ArrayList<String>();
        }
        :   l1=line {   Constant constant = new Constant();
                        constant.setSection($l1.sect);
                        constant.setValue($l1.val);
                        $constants.add(constant);
            }
            (l2=line {   Constant constant = new Constant();
                             constant.setSection($l2.sect);
                             constant.setValue($l2.val);
                             $constants.add(constant);
            })*
            EOL* EOF
        ;

TOKEN   :   ('a'..'z'|'-')+
        ;

FURTHER :   ('A'..'Z'|'_')+
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

COLON   :   ':'
        ;

VALUE   :   '-'? ('0'..'9')+
        ;

EOL     :   '\r'? '\n'
        ;