grammar WorldReader;

@header {
            import java.util.HashMap;
            import java.util.ArrayList;
        }

levelNum
        returns[int num]
        :   NUMBER COLON
            {
                $num = Integer.parseInt($NUMBER.getText());
            }
        ;

levelName
        returns[String name]
        :   NAME COLON
            {
                $name = $NAME.getText();
            }
        ;

upAndDown
        returns[String previous, String next]
        :   (down=NAME) COLON (up=NAME)
            {
                $previous = $down.getText();
                if ($previous.equals("None")) $previous = null;

                $next = $up.getText();
                if ($next.equals("None")) $next = null;
            }
        ;

line    returns[int lev, ArrayList<String> names]
        @init {
            $names = new ArrayList<>();
        }
        :   LEVEL levelNum levelName upAndDown
            {
                $lev = $levelNum.num;
                $names.add($levelName.name);
                $names.add($upAndDown.previous);
                $names.add($upAndDown.next);
            }
        ;

file    returns[HashMap<Integer, ArrayList<String>> levels]
        @init {
            $levels = new HashMap<Integer, ArrayList<String>>();
        }
        :   (line
            {
                $levels.put($line.lev, $line.names);
            })+
            EOF
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL     :   '\r'? '\n' -> skip
        ;

LEVEL   :   'level:'
        ;

COLON   :   ':'
        ;

NUMBER  :   ('0'..'9')+
        ;

NAME    :   ('A'..'Z'|'a'..'z'|' '|'0'..'9')+
        ;