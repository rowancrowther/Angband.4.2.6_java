grammar UIEntry;

@header{
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
    import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
    import uk.co.jackoftrades.frontend.entries.UIEntry;
    import uk.co.jackoftrades.frontend.entries.UIEntry.StatElemType;
    import uk.co.jackoftrades.frontend.entries.UIEntryBase;
    import uk.co.jackoftrades.frontend.entries.enums.EntryFlag;

    import java.util.List;
    import java.util.ArrayList;
}

tag
        returns[ElementEnum elemEnum]
        :   TAG {
                String raw = $TAG.getText();
                String elemStr = "ELEM_" + raw.substring(1, raw.length() - 1);
                $elemEnum = ElementEnum.valueOf(elemStr);
            }
        ;

name
        returns[String nameStr, ElementEnum elemEnum]
        @init{
            $elemEnum = ElementEnum.ELEM_NONE;
        }
        :   NAME TEXT { $nameStr = $TEXT.getText(); } (tag { $elemEnum = $tag.elemEnum; })?
        ;

parameter
        returns[boolean isElement]
        :   PARAMETER PARAMETERTYPE { $isElement = $PARAMETERTYPE.getText().equals("element"); }
        ;

renderer
        returns[UIEntryRenderer uiEntryRenderer]
        :   RENDERER TEXT {
                $uiEntryRenderer = GameConstants.getUIEntryRenderer($TEXT.getText());
        };

combine
        returns[CombinerName combiner]
        :   COMBINE TEXT { $combiner = CombinerName.valueOf($TEXT.getText()); }
        ;

priority
        returns[String word, int num]
        @init {
            $word = "";
            $num = 0;
        }
        :   PRIORITY ((PRIORITYWORD { $word = $PRIORITYWORD.getText(); })
                    | (PRIORITYNUM { $num = Integer.parseInt($PRIORITYNUM.getText()); }))
        ;

category
        returns[String categoryStr]
        :   CATEGORY TEXT { $categoryStr = $TEXT.getText(); }
        ;

flags
        returns[EntryFlag entryFlagEnum]
        :   FLAGS TEXT {
                $entryFlagEnum = EntryFlag.valueOf("ENTRY_FLAG_" + $TEXT.getText());
        };

desc
        returns[String descStr]
        :   DESC TEXT { $descStr = $TEXT.getText(); }
        ;

label
        returns[String labelStr]
        :   LABEL TEXT { $labelStr = $TEXT.getText(); }
        ;

label5
        returns[String label5Str]
        :   LABEL5 TEXT { $label5Str = $TEXT.getText(); }
        ;

label2
        returns[String label2Str]
        :   LABEL2 TEXT { $label2Str = $TEXT.getText(); }
        ;

template
        returns[UIEntryBase uiEntryBase]
        :   TEMPLATE TEXT {
                String raw = $TEXT.getText();
                $uiEntryBase = GameConstants.getUIEntryBase(raw);
        };

uiEntry
        returns[UIEntry entry]
        @init{
            String          nameInit        = "";
            UIEntryBase     templateInit    = null;
            String          labelInit       = "";
            String          label5Init      = "";
            String          label2Init      = "";
            List<String>    categoryInit    = new ArrayList<>();
            ElementEnum     parameterInit   = ElementEnum.ELEM_NONE;
            boolean         parmIsElement   = false;
            UIEntryRenderer rendererInit    = null;
            CombinerName    combinerInit    = CombinerName.NONE;
            String          priorityStrInit = "";
            int             priorityNumInit = 0;
            EntryFlag       flagInit        = EntryFlag.ENTRY_FLAG_TEMPLATE_ONLY;
            String          descInit        = "";
        }
        @after {
                UIEntry.StatElemType type = parmIsElement
                                          ? StatElemType.ELEMENT
                                          : StatElemType.STAT;
                $entry = new UIEntry(nameInit, parameterInit, type,
                                     rendererInit, combinerInit,
                                     categoryInit, priorityNumInit,
                                     priorityStrInit, flagInit,
                                     descInit, labelInit, label5Init,
                                     label2Init, templateInit);
        }
        :   name { nameInit = $name.nameStr;
                   parameterInit = $name.elemEnum; }
            (template { templateInit = $template.uiEntryBase; })?
            (label { labelInit = $label.labelStr; })?
            (label5 { label5Init = $label5.label5Str; })?
            (label2 { label2Init = $label2.label2Str; })?
            ((category {categoryInit.add($category.categoryStr); })+)?
            (parameter { parmIsElement = $parameter.isElement; })?
            (renderer { rendererInit = $renderer.uiEntryRenderer; })?
            (combine { combinerInit = $combine.combiner; })?
            (priority { priorityStrInit = $priority.word;
                        priorityNumInit = $priority.num; })?
            ((category {categoryInit.add($category.categoryStr); })+)?
            (flags { flagInit = $flags.entryFlagEnum; })?
            (desc { descInit = descInit + $desc.descStr; })*
        ;

file
        returns[List<UIEntry> entries]
        @init {
            $entries = new ArrayList<>();
        }
        :   (uiEntry { $entries.add($uiEntry.entry); })+
            EOF
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

PARAMETER
        :   'parameter:'
        ;

PARAMETERTYPE
        :   ('stat' | 'element')
        ;

RENDERER
        :   'renderer:'
        ;

COMBINE
        :   'combine:'
        ;

PRIORITY
        :   'priority:'
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

PRIORITYWORD
        :   'negative_index'
        ;

PRIORITYNUM
        :   '-'? ('0'..'9')+
        ;

TEXT
        :   ('a'..'z' | 'A'..'Z' | '.' | ' ' | ',' | '\'' | '_' | '0' | '1')+
        ;

TAG
        :   '<' ('A'..'Z')+ '>'
        ;