// Parser+lexer for lib/gamedata/ui_entry.txt - configures UI elements that
// combine one or more object/player properties for display (currently the
// second character screen and the equippable comparison): renderer,
// combiner, categories, priority, labels, and a description, optionally
// based on a ui_entry_base.txt template. Cf. src/ui-entry.c's struct
// file_parser ui_entry_parser (ui-entry.c:2355).
//
// `uiEntry`'s two separate `((category {...})+)?` slots are deliberate, not
// duplicated by mistake - verified against real data: generic entries list
// categories AFTER parameter/renderer/combine/priority (e.g.
// stat_mod_ui_compact_0), while the per-element/per-stat "specialized"
// entries generated from them (e.g. resist_ui_compact_0<ACID>) have only a
// name: followed directly by category: lines, with everything else
// inherited - so categories need to be reachable in both positions.
//
// POTENTIAL PROBLEMS (both latent - not currently triggered):
//
//   1. The file's own header says "The category and priority fields can be
//      set multiple times" (referring to both having repeat support), but
//      `priority` only appears as `(priority {...})?` - at most once. No
//      current entry has more than one priority: line.
//
//   2. The header also documents "label, label5, label3, and label2" as
//      the relevant abbreviated-label fields, but this grammar only has
//      LABEL/LABEL5/LABEL2 - no LABEL3 token/rule at all. No current entry
//      uses "label3:".
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

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

// A "<ELEMENT>"/"<STAT>" suffix on a specialized entry's name, e.g. the
// "<ACID>" in "resist_ui_compact_0<ACID>".
tag
        returns[ElementEnum elemEnum]
        :   TAG {
                String raw = $TAG.getText();
                String elemStr = "ELEM_" + raw.substring(1, raw.length() - 1);
                $elemEnum = ElementEnum.valueOf(elemStr);
            }
        ;

// "name:<text>[<TAG>]" - starts a new entry record; the optional tag marks
// a specialized per-element/per-stat entry generated from a generic one.
name
        returns[String nameStr, ElementEnum elemEnum]
        @init{
            $elemEnum = ElementEnum.ELEM_NONE;
        }
        :   NAME TEXT { $nameStr = $TEXT.getText(); } (tag { $elemEnum = $tag.elemEnum; })?
        ;

// "parameter:stat|element" - marks this as a generic entry, repeated for
// every known stat or element.
parameter
        returns[boolean isElement]
        :   PARAMETER PARAMETERTYPE { $isElement = $PARAMETERTYPE.getText().equals("element"); }
        ;

// "renderer:<text>" - the ui_entry_renderer.txt renderer this entry uses.
renderer
        returns[UIEntryRenderer uiEntryRenderer]
        :   RENDERER TEXT {
                $uiEntryRenderer = GameConstants.getUIEntryRenderer($TEXT.getText());
        };

// "combine:<CombinerName>" - how values from multiple sources combine
// (ADD, LOGICAL_OR, RESIST_0, etc).
combine
        returns[CombinerName combiner]
        :   COMBINE TEXT { $combiner = CombinerName.valueOf($TEXT.getText()); }
        ;

// "priority:<word>|<number>" - display priority, either the special word
// "negative_index" or a literal number. See top-of-file problem #1 re:
// this not being repeatable despite the header's claim.
priority
        returns[String word, int num]
        @init {
            $word = "";
            $num = 0;
        }
        :   PRIORITY ((PRIORITYWORD { $word = $PRIORITYWORD.getText(); })
                    | (PRIORITYNUM { $num = Integer.parseInt($PRIORITYNUM.getText()); }))
        ;

// "category:<text>" - a UI category this entry appears in; can repeat in
// either of the two positions `uiEntry` allows (see top-of-file comment).
category
        returns[String categoryStr]
        :   CATEGORY TEXT { $categoryStr = $TEXT.getText(); }
        ;

// "flags:<text>" - an entry flag (the "ENTRY_FLAG_" prefix is added here).
flags
        returns[EntryFlag entryFlagEnum]
        :   FLAGS TEXT {
                $entryFlagEnum = EntryFlag.valueOf("ENTRY_FLAG_" + $TEXT.getText());
        };

// "desc:<text>" - description; can repeat to build up multiple lines.
desc
        returns[String descStr]
        :   DESC TEXT { $descStr = $TEXT.getText(); }
        ;

// "label:<text>" - the label shown for this element.
label
        returns[String labelStr]
        :   LABEL TEXT { $labelStr = $TEXT.getText(); }
        ;

// "label5:<text>" - a 5-character abbreviated label override.
label5
        returns[String label5Str]
        :   LABEL5 TEXT { $label5Str = $TEXT.getText(); }
        ;

// "label2:<text>" - a 2-character abbreviated label override. See top-of-
// file problem #2 re: the missing "label3:" equivalent.
label2
        returns[String label2Str]
        :   LABEL2 TEXT { $label2Str = $TEXT.getText(); }
        ;

// "template:<text>" - the ui_entry_base.txt template supplying default
// values for this entry's unset fields.
template
        returns[UIEntryBase uiEntryBase]
        :   TEMPLATE TEXT {
                String raw = $TEXT.getText();
                $uiEntryBase = GameConstants.getUIEntryBase(raw);
        };

// One full entry record: name, then template/labels/categories/parameter/
// renderer/combine/priority/categories(again)/flags/desc, all optional and
// in this fixed relative order (see top-of-file comment re: the two
// category slots).
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
                        priorityNumInit = $priority.num; })*
            ((category {categoryInit.add($category.categoryStr); })+)?
            (flags { flagInit = $flags.entryFlagEnum; })?
            (desc { descInit = descInit + $desc.descStr; })*
        ;

// Top-level rule: the whole file is one or more entry records.
file
        returns[List<UIEntry> entries]
        @init {
            $entries = new ArrayList<>();
        }
        :   (uiEntry { $entries.add($uiEntry.entry); })+
            EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// NAME through TEMPLATE below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
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

// A (possibly negative) literal integer - priority:'s numeric form.
PRIORITYNUM
        :   '-'? ('0'..'9')+
        ;

// Free-running text - used for most other string fields (names, renderer/
// combiner/category/flag/label/template/description text).
TEXT
        :   ('a'..'z' | 'A'..'Z' | '.' | ' ' | ',' | '\'' | '_' | '0' | '1')+
        ;

// A "<ELEMENT>"/"<STAT>"-style suffix on a specialized entry's name.
TAG
        :   '<' ('A'..'Z')+ '>'
        ;

// POTENTIAL SOLUTIONS
//
//   1. Only worth doing if a priority: line ever needs to repeat - change
//      `(priority {...})?` to `(priority {...})*` and decide how multiple
//      priorities should combine/override.
//
//   2. Only worth doing if ui_entry.txt gains a "label3:" line - add a
//      LABEL3 token and `label3` rule mirroring LABEL2/`label2`.