// Parser+lexer for lib/gamedata/ui_entry_base.txt - reusable templates for
// ui_entry.txt: which renderer to use, how multiple sources combine, what
// category/categories it belongs to, flags, and a description. These
// templates don't appear directly in the UI - ui_entry.txt entries pull
// them in by name. Cf. src/ui-entry.c's parsing around the "ui_entry_base"
// file (ui-entry.c:2259) - part of the same parser machinery as
// ui_entry.txt rather than its own file_parser struct.
//
// No problems found - `entryBase`'s fixed sequence
// "name renderer combine category+ flags desc+" matches every record in
// ui_entry_base.txt (every record has at least one category:, a flags:,
// and at least one desc: line), and `category`'s UCASEWORD-or-LCASEWORD
// alternation is genuinely needed - real categories use both cases (e.g.
// "CHAR_SCREEN1" and "abilities" in the same record).

grammar UIEntryBase;

@header {
    package uk.co.jackoftrades.backend.parser.uientrybase;

    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
    import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
    import uk.co.jackoftrades.frontend.entries.UIEntryBase;

    import java.util.List;
    import java.util.ArrayList;
}

// "name:<text>" - starts a new template record; referenced by ui_entry.txt.
name
        returns[String nameStr]
        :   NAME LCASEWORD { $nameStr = $LCASEWORD.getText(); }
        ;

// "renderer:<text>" - the ui_entry_renderer.txt renderer this template uses.
renderer
        returns[UIEntryRenderer entryRenderer]
        :   RENDERER LCASEWORD {
                $entryRenderer = GameConstants.getUIEntryRenderer($LCASEWORD.getText());
            }
        ;

// "combine:<CombinerName>" - how values from multiple sources combine
// (e.g. LOGICAL_OR, ADD).
combine
        returns[CombinerName combinerEnum]
        :   COMBINE UCASEWORD {
                $combinerEnum = CombinerName.valueOf($UCASEWORD.getText());
            }
        ;

// "category:<text>" - a UI category this template belongs to (either
// UPPER_CASE or lower_case - both forms are used); can repeat (see
// `entryBase`'s categoryInit list).
category
        returns[String categoryStr]
        :   CATEGORY (UCASEWORD { $categoryStr = $UCASEWORD.getText(); }
                    | LCASEWORD { $categoryStr = $LCASEWORD.getText(); })
        ;

// "flags:<text>" - flags affecting this template's behaviour.
flags
        returns[String flagsStr]
        :   FLAGS UCASEWORD {
                $flagsStr = $UCASEWORD.getText();
            }
        ;

// "desc:<text>" - description; can repeat to build up multiple lines.
desc
        returns[String descStr]
        :   DESC TEXT { $descStr = $TEXT.getText(); }
        ;

// One full template record: a fixed sequence of name/renderer/combine,
// one-or-more category: lines, flags, then one-or-more desc: lines.
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

// Top-level rule: the whole file is one or more template records.
file
        returns[List<UIEntryBase> uiEntryBases]
        @init{
            $uiEntryBases = new ArrayList<>();
        }
        :   (entryBase {
            $uiEntryBases.add($entryBase.base);
        })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// NAME through DESC below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
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

// A lowercase_with_underscores_or_digits symbolic name - used for name:/renderer:/category:.
LCASEWORD
        :   ('a'..'z' | '_' | '0'..'9')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES_OR_DIGITS symbolic name - used for
// combine:/category:/flags:.
UCASEWORD
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

// Free-running text - used for desc:'s description text.
TEXT
        :   ('A'..'Z' | 'a'..'z' | ' ' | '.')+
        ;