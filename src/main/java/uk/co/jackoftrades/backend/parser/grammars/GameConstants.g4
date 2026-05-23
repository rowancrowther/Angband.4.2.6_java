grammar GameConstants;

@header
        {
            import java.util.ArrayList;
        }

@members
        {
            public record Entry(String key, String value) {}
        }

section returns[String sect, String value]
        :   token1=TOKEN { $sect = $token1.getText(); }
            COLON token2=TOKEN
                         { $value = $token2.getText(); }
            COLON VALUE  { $value = $value + ":" + $VALUE.getText(); }
        ;

furtherValue
        returns[String sect, String further]
        :   TOKEN { $sect = $TOKEN.getText(); }
            COLON val1=VALUE
            COLON val2=VALUE
            COLON FURTHER {
                $further = $val1.getText() + ":" + $val2.getText() + ":" + $FURTHER.getText();
            }
        ;

multiValue
        returns[String sect, String multi]
        :   TOKEN {$sect = $TOKEN.getText(); }
            COLON val1=VALUE
            COLON val2=VALUE
            COLON val3=VALUE
            COLON FURTHER {
                $multi = $val1.getText() + ":" + $val2.getText() + ":" + $val3.getText() + ":" + $FURTHER.getText();
            }
        ;

line    returns[String sect, String val]
        :   section {
                $sect = $section.sect;
                $val  = $section.value;
        }
        |   furtherValue {
                $sect = $furtherValue.sect;
                $val  = $furtherValue.further;
        }
        |   multiValue {
                $sect = $multiValue.sect;
                $val  = $multiValue.multi;
        }
        ;

file    returns[ArrayList<Entry> results]
        @init {
            $results = new ArrayList<>();
        }
        :   (line {
                        $results.add(new Entry($line.sect, $line.val));
                  })*
            EOF
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

EOL     :   '\r'? '\n' -> skip
        ;