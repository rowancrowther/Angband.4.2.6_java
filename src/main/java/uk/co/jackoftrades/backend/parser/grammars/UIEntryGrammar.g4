/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

// @author Rowan Crowther
//
// Parser grammar for lib/gamedata/ui_entry.txt - configures UI elements that
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

parser grammar UIEntryGrammar;
options { tokenVocab = UIEntryLexer; }

@header{
    import uk.co.jackoftrades.backend.parser.uientry.UIEntryParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

// A "<ELEMENT>"/"<STAT>" suffix on a specialized entry's name, e.g. the
// "<ACID>" in "resist_ui_compact_0<ACID>".
tag
        returns[String elementOrStat]
        :   LTHAN PARAMETER_VALUE GTHAN {
                $elementOrStat = $PARAMETER_VALUE.getText();
            }
        ;

// "name:<text>[<TAG>]" - starts a new entry record; the optional tag marks
// a specialized per-element/per-stat entry generated from a generic one.
name
        returns[String nameStr, String elemOrStat]
        :   NAME LCASE { $nameStr = $LCASE.getText(); } (tag { $elemOrStat = $tag.elementOrStat; })?
        ;

// "parameter:stat|element" - marks this as a generic entry, repeated for
// every known stat or element.
parameter
        returns[String isElement]
        :   PARAMETER STRING { $isElement = $STRING.getText(); }
        ;

// "renderer:<text>" - the ui_entry_renderer.txt renderer this entry uses.
renderer
        returns[String uiEntryRenderer]
        :   RENDERER STRING {
                $uiEntryRenderer = $STRING.getText();
        };

// "combine:<CombinerName>" - how values from multiple sources combine
// (ADD, LOGICAL_OR, RESIST_0, etc).
combine
        returns[String combiner]
        :   COMBINE FLAG { $combiner = $FLAG.getText(); }
        ;

// "priority:<word>|<number>" - display priority, either the special word
// "negative_index" or a literal number. See top-of-file problem #1 re:
// this not being repeatable despite the header's claim.
priority
        returns[String word]
        :   PRIORITY STRING { $word = $STRING.getText(); }
        ;

// "category:<text>" - a UI category this entry appears in; can repeat in
// either of the two positions `uiEntry` allows (see top-of-file comment).
category
        returns[List<String> categoryStr]
        @init {
            $categoryStr = new ArrayList<>();
        }
        :   (CATEGORY STRING { $categoryStr.add($STRING.getText()); })+
        ;

// "flags:<text>" - an entry flag (the "ENTRY_FLAG_" prefix is added here).
flags
        returns[List<String> entryFlagEnum]
        @init {
            $entryFlagEnum = new ArrayList<>();
        }
        :   FLAGS FLAG {
            $entryFlagEnum.add($FLAG.getText());
        } (FLAG_OR FLAG {
            $entryFlagEnum.add($FLAG.getText());
        })*;

// "desc:<text>" - description; can repeat to build up multiple lines.
desc
        returns[String descStr]
        @init { StringBuilder sb = new StringBuilder(); }
        @after { $descStr = sb.toString(); }
        :   (DESC STRING { sb.append($STRING.getText()); })+
        ;

// "label:<text>" - the label shown for this element.
label
        returns[String labelStr]
        :   LABEL STRING { $labelStr = $STRING.getText(); }
        ;

// "label5:<text>" - a 5-character abbreviated label override.
label5
        returns[String label5Str]
        :   LABEL5 STRING { $label5Str = $STRING.getText(); }
        ;

// "label2:<text>" - a 2-character abbreviated label override. See top-of-
// file problem #2 re: the missing "label3:" equivalent.
label2
        returns[String label2Str]
        :   LABEL2 STRING { $label2Str = $STRING.getText(); }
        ;

// "template:<text>" - the ui_entry_base.txt template supplying default
// values for this entry's unset fields.
template
        returns[String uiEntryBase]
        :   TEMPLATE STRING {
                $uiEntryBase = $STRING.getText();
        };

// One full entry record: name, then template/labels/categories/parameter/
// renderer/combine/priority/categories(again)/flags/desc, all optional and
// in this fixed relative order (see top-of-file comment re: the two
// category slots).
uiEntry
        returns[UIEntryParseRecord entry]
        @init{
            int             line            = 0;
            String          nameInit        = "";
            String          templateInit    = "";
            String          labelInit       = "";
            String          label5Init      = "";
            String          label2Init      = "";
            List<String>    categoryInit    = new ArrayList<>();
            String          parameterInit   = "";
            String          rendererInit    = "";
            String          combinerInit    = "";
            String          priorityInit    = "";
            List<String>    flagInit        = new ArrayList<>();
            String          descInit        = "";
        }
        @after {
            $entry = new UIEntryParseRecord(nameInit, templateInit, labelInit,
            label5Init, label2Init, parameterInit, rendererInit, combinerInit,
            priorityInit, categoryInit, flagInit, descInit, line);
        }
        :   name { nameInit = $name.nameStr;
                   if ($name.elemOrStat != null) parameterInit = $name.elemOrStat;
                   line = $start.getLine(); }
            (template { templateInit = $template.uiEntryBase; })?
            (label { labelInit = $label.labelStr; })?
            (label5 { label5Init = $label5.label5Str; })?
            (label2 { label2Init = $label2.label2Str; })?
            (category {categoryInit.addAll($category.categoryStr); })?
            (parameter { parameterInit = $parameter.isElement; })?
            (renderer { rendererInit = $renderer.uiEntryRenderer; })?
            (combine { combinerInit = $combine.combiner; })?
            (priority { priorityInit = $priority.word; })*
            (category {categoryInit.addAll($category.categoryStr); })?
            (flags { flagInit = $flags.entryFlagEnum; })?
            (desc { descInit = descInit + $desc.descStr; })?
        ;

// Top-level rule: the whole file is one or more entry records.
file
        returns[List<UIEntryParseRecord> entries, String declaredRecordCount]
        @init {
            $entries = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (uiEntry { $entries.add($uiEntry.entry); })+
            EOF
        ;