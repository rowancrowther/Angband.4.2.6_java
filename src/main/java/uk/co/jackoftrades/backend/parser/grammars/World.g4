grammar World;

@header {
            import java.util.ArrayList;
            import java.util.List;
        }

@members
        {
            public record ParsedWorld(int level, String levelName, String levelUp, String levelDown) {}
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

line    returns[ParsedWorld world]
        :   LEVEL levelNum levelName upAndDown
            {
                $world = new ParsedWorld($levelNum.num, $levelName.name, $upAndDown.previous, $upAndDown.next);
            }
        ;

file    returns[List<ParsedWorld> levels]
        @init {
            $levels = new ArrayList<>();
        }
        :   (line
            {
                $levels.add($line.world);
            })+ EOF
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