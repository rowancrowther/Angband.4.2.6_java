grammar UIEntryBase;

@header
        {
            import uk.co.jackoftrades.middle.game.Game;
            import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
            import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
            import uk.co.jackoftrades.frontend.entries.UIEntryBase;

            import java.util.ArrayList;
        }

name
        returns[String nameString]
        :   NAME LCASE
            {
                $nameString = $LCASE.getText();
            }
        ;

renderer
        returns[UIEntryRenderer rend]
        :   RENDERER LCASE
            {
                $rend = Game.getUIEntryRenderer($LCASE.getText());
            }
        ;

combine
        returns[CombinerName combName]
        :   COMBINE UCASE
            {
                $combName = CombinerName.valueOf($UCASE.getText());
            }
        ;

category
        returns[ArrayList<String> categories]
        @init {
            $categories = new ArrayList<>();
        }
        :   (CATEGORY
            {
                String fromFile = $CATEGORY.getText();
                String toSave = fromFile.substring(9);
                $categories.add(toSave);
            })+
        ;

flags
        returns[String flag]
        :   FLAGS UCASE
            {
                $flag = $UCASE.getText();
            }
        ;

desc
        returns[String description]
        @init {
            $description = "";
        }
        :   (DESC TEXT
            {
                $description = $description + $TEXT.getText();
            })+
        ;

base
        returns[UIEntryBase uiBase]
        @init {
            String n;
            UIEntryRenderer r;
            CombinerName co;
            ArrayList<String> ca;
            String f;
            String d;
        }
        @after {
            $uiBase = new UIEntryBase(n, r, co, ca, f, d);
        }
        :   name
            {
                n = $name.nameString;
            }
            renderer
            {
                r = $renderer.rend;
            }
            combine
            {
                co = $combine.combName;
            }
            category
            {
                ca = $category.categories;
            }
            flags
            {
                f = $flags.flag;
            }
            desc
            {
                d = $desc.description;
            }
        ;

bases
        returns[ArrayList<UIEntryBase> allBases]
        @init {
            $allBases = new ArrayList<>();
        }
        :   (base {
                $allBases.add($base.uiBase);
            })+
            EOF
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL     :   ' '* '\r'? '\n' -> skip
        ;

NAME    :   'name:'
        ;

RENDERER
        :   'renderer:'
        ;

COMBINE
        :   'combine:'
        ;

CATEGORY
        :   'category:' [a-zA-Z1_]+
        ;

FLAGS
        :   'flags:'
        ;

DESC
        :   'desc:'
        ;

UCASE
        :   [A-Z_]+
        ;

TEXT
        :   [a-zA-Z. ]+
        ;

LCASE
        :   [a-z_0-9]+
        ;