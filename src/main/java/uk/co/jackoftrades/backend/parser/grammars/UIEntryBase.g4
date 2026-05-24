grammar UIEntryBase;

@header {
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
    import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
    import uk.co.jackoftrades.frontend.entries.UIEntryBase;

    import java.util.List;
    import java.util.ArrayList;
}

name
        returns[String nameStr]
        :   NAME LCASEWORD { $nameStr = $LCASEWORD.getText(); }
        ;

renderer
        returns[UIEntryRenderer entryRenderer]
        :   RENDERER LCASEWORD {
                $entryRenderer = GameConstants.getUIEntryRenderer($LCASEWORD.getText());
            }
        ;

combine
        returns[CombinerName combinerEnum]
        :   COMBINE UCASEWORD {
                $combinerEnum = CombinerName.valueOf($UCASEWORD.getText());
            }
        ;

category
        returns[String categoryStr]
        :   CATEGORY (UCASEWORD { $categoryStr = $UCASEWORD.getText(); }
                    | LCASEWORD { $categoryStr = $LCASEWORD.getText(); })
        ;

flags
        returns[String flagsStr]
        :   FLAGS UCASEWORD {
                $flagsStr = $UCASEWORD.getText();
            }
        ;

desc
        returns[String descStr]
        :   DESC TEXT { $descStr = $TEXT.getText(); }
        ;

entryBase
        returns[UIEntryBase base]
        @init {
            String nameInit = "";
            UIEntryRenderer rendererInit = null;
            CombinerName combinerInit = CombinerName.NONE;
            List<String> categoryInit = new ArrayList<>();
            String flagsInit = "";
            String descInit = "";
        }
        @after {
            $base = new UIEntryBase(nameInit, rendererInit,
                                combinerInit, categoryInit,
                                flagsInit, descInit);
        }
        :   name {
                nameInit = $name.nameStr;
            }
            renderer {
                rendererInit = $renderer.entryRenderer;
            }
            combine {
                combinerInit = $combine.combinerEnum;
            }
            (category {
                categoryInit.add($category.categoryStr);
            })+
            flags {
                flagsInit = $flags.flagsStr;
            }
            (desc {
                descInit = descInit + $desc.descStr;
            })+
        ;

file
        returns[List<UIEntryBase> uiEntryBases]
        @init{
            $uiEntryBases = new ArrayList<UIEntryBase>();
        }
        :   (entryBase {
            $uiEntryBases.add($entryBase.base);
        })+ EOF
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

RENDERER
        :   'renderer:'
        ;

COMBINE
        :   'combine:'
        ;

CATEGORY
        :   'category:'
        ;

FLAGS
        :   'flags:'
        ;

DESC
        :   'desc:'
        ;

LCASEWORD
        :   ('a'..'z'|'_'|'0'..'9')+
        ;

UCASEWORD
        :   ('A'..'Z'|'_'|'0'..'9')+
        ;

TEXT
        :   ('A'..'Z'|'a'..'z'|' '|'.')+
        ;