grammar UIEntryGram;

@header {
    import uk.co.jackoftrades.middle.enums.StatElementEnum;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.enums.ElementNames;
    import uk.co.jackoftrades.middle.game.GameEnginee.Game;
    import uk.co.jackoftrades.frontend.entries.UIEntry;
    import uk.co.jackoftrades.frontend.entries.UIEntry.StatElemType;
    import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
    import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
    import uk.co.jackoftrades.frontend.screen.enums.EntryFlag;

    import java.util.ArrayList;
}

name
        returns[String nameText, StatElementEnum tag]
        :   NAME TEXT TAG? {
                $nameText = $TEXT.getText();
                if ($TAG != null) {
                    String t = $TAG.getText();
                    t = t.substring(1, t.length() - 1);
                    // Assume it's a stat and try to get it from Stats
                    String test = "STAT_" + t;
                    try {
                        Stats.valueOf(test);
                        $tag = StatElementEnum.valueOf("STAT_" + t);
                    } catch (IllegalArgumentException i) {
                        $tag = StatElementEnum.valueOf("ELEM_" + t);
                    }
                }
            }
        ;

parameter
        returns[StatElemType parameterType]
        :   PARAMETER PARAMETERTYPE {
                String parm = $PARAMETERTYPE.getText().toUpperCase();
                $parameterType = StatElemType.valueOf(parm);
            }
        ;

renderer
        returns[UIEntryRenderer rend]
        :   RENDERER TEXT {
                String rendName = $TEXT.getText();
                $rend = Game.getUIEntryRenderer(rendName);
            }
        ;

combine
        returns[CombinerName combineType]
        :   COMBINE COMBINETYPE {
                String combiner = $COMBINETYPE.getText();
                $combineType = CombinerName.valueOf(combiner);
            }
        ;

priority
        returns[String priorityString, int priorityNum]
        @init {
            $priorityString = "";
            $priorityNum = 0;
        }
        :   PRIORITY (TEXT
            {
                $priorityString = $TEXT.getText();
            }
                | MINUS? NUMBER
            {
                int negative = ($MINUS == null) ? 1 : -1;
                $priorityNum = negative * Integer.parseInt($NUMBER.getText());
            })
        ;

category
        returns[String cat]
        :   CATEGORY TEXT {
                $cat = $TEXT.getText();
            }
        ;

flags
        returns[String flagString]
        :   FLAGS TEXT {
                $flagString = $TEXT.getText();
            }
        ;

desc
        returns[String description]
        @init {
            $description = "";
        }
        :   DESC TEXT {
                $description = $description + $TEXT.getText();
            }
        ;

label
        returns[String labelText]
        :   LABEL TEXT {
                $labelText = $TEXT.getText();
            }
        ;

label2
        returns[String label2Text]
        :   LABEL2 TEXT {
                $label2Text = $TEXT.getText();
            }
        ;

label5
        returns[String label5Text]
        :   LABEL5 TEXT {
                $label5Text = $TEXT.getText();
            }
        ;

template
        returns[String templateText]
        :   TEMPLATE TEXT {
                $templateText = $TEXT.getText();
            }
        ;

entry   returns[UIEntry ent]
        @init {
            String nameStr = "";
            StatElementEnum statOrElem = null;
            StatElemType parameterType = null;
            UIEntryRenderer render = null;
            CombinerName combinerName = null;
            int priorityNum = 0;
            String priorityName = "";
            ArrayList<String> categories = new ArrayList<>();
            EntryFlag entryFlag = null;
            String description = "";
            String labelStr = "";
            String label2Str = "";
            String label5Str = "";
            String templateStr = "";
        }
        @after {
            $ent = new UIEntry (nameStr, statOrElem, parameterType,
                                render, combinerName, categories,
                                priorityNum, priorityName, entryFlag,
                                description, labelStr, label2Str,
                                label5Str, templateStr);
        }
        :   name {
                nameStr = $name.nameText;
                statOrElem = $name.tag;
            }
        (   parameter {
                parameterType = $parameter.parameterType;
            }
            renderer {
                render = $renderer.rend;
            }
            combine {
                combinerName = $combine.combineType;
            }
            priority {
                priorityNum = $priority.priorityNum;
                priorityName = $priority.priorityString;
            }
            (category {
                categories.add($category.cat);
            })+
            (flags {
                entryFlag = EntryFlag.valueOf("ENTRY_FLAG_" + $flags.flagString);
            })?
            (desc {
                description = description + $desc.description;
            })+
        |
            label {
                labelStr = $label.labelText;
            }
            (label5 {
                label5Str = $label5.label5Text;
            })?
            (label2 {
                label2Str = $label2.label2Text;
            })?
            (category {
                categories.add($category.cat);
            })+
        |
            template {
                templateStr = $template.templateText;
            }
            label {
                labelStr = $label.labelText;
            }
            (label5 {
                label5Str = $label5.label5Text;
            })?
            (label2 {
                label2Str = $label2.label2Text;
            })?
            priority {
                priorityNum = $priority.priorityNum;
                priorityName = $priority.priorityString;
            }
        );

entries
        returns[ArrayList<UIEntry> entryList]
        @init {
            $entryList = new ArrayList<>();
        }
        :   (entry {
                $entryList.add($entry.ent);
            })+
            EOF
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL     :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:'
        ;

PARAMETER
        :   'parameter:'
        ;

RENDERER
        :   'renderer:'
        ;

COMBINE
        :   'combine:'
        ;

CATEGORY
        :   'category:'
        ;

DESC
        :   'desc:'
        ;

FLAGS
        :   'flags:'
        ;

LABEL
        :   'label:'
        ;

LABEL5
        :   'label5:'
        ;

LABEL2
        :   'label2:'
        ;

TEMPLATE
        :   'template:'
        ;

PRIORITY
        :   'priority:'
        ;

PARAMETERTYPE
        :   'stat' | 'element'
        ;

COMBINETYPE
        :   'ADD' | 'RESIST_0'
        ;

NUMBER
        :   [0-9]+
        ;

MINUS
        :   '-'
        ;

TAG
        :   '<' [A-Z]+ '>'
        ;

TEXT
        :   [A-Za-z .',_0-9]+
        ;