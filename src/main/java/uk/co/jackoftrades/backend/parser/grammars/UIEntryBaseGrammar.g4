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
// Parser grammar for lib/gamedata/ui_entry_base.txt - reusable templates for
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
// and at least one desc: line).

parser grammar UIEntryBaseGrammar;
options { tokenVocab = UIEntryBaseLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.uientrybase.UIEntryBaseParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

// "name:<text>" - starts a new template record; referenced by ui_entry.txt.
name
        returns[String nameStr]
        :   NAME LCASE { $nameStr = $LCASE.getText(); }
        ;

// "renderer:<text>" - the ui_entry_renderer.txt renderer this template uses.
renderer
        returns[String entryRenderer]
        :   RENDERER LCASE {
                $entryRenderer = $LCASE.getText();
            }
        ;

// "combine:<CombinerName>" - how values from multiple sources combine
// (e.g. LOGICAL_OR, ADD).
combine
        returns[String combinerEnum]
        :   COMBINE FLAG {
                $combinerEnum = $FLAG.getText();
            }
        ;

// "category:<text>" - a UI category this template belongs to (either
// UPPER_CASE or lower_case - both forms are used); can repeat (see
// `entryBase`'s categoryInit list).
category
        returns[List<String> categoryStr]
        @init {
            $categoryStr = new ArrayList<>();
        }
        :   (CATEGORY STRING { $categoryStr.add($STRING.getText()); })+
        ;

// "flags:<text>" - flags affecting this template's behaviour.
flags
        returns[String flagsStr]
        :   FLAGS FLAG {
                $flagsStr = $FLAG.getText();
            }
        ;

// "desc:<text>" - description; can repeat to build up multiple lines.
desc
        returns[String descStr]
        @init { StringBuilder sb = new StringBuilder(); }
        @after { $descStr = sb.toString(); }
        :   (DESC STRING { sb.append($STRING.getText()); })+
        ;

// One full template record: a fixed sequence of name/renderer/combine,
// one-or-more category: lines, flags, then one-or-more desc: lines.
entryBase
        returns[UIEntryBaseParseRecord base]
        @init {
            String nameInit = "";
            String rendererInit = "";
            String combinerInit = "";
            List<String> categoryInit = new ArrayList<>();
            String flagsInit = "";
            String descInit = "";
            int lineNumber = 0;
        }
        @after {
            $base = new UIEntryBaseParseRecord(
                        nameInit, rendererInit,
                        combinerInit, flagsInit,
                        descInit, categoryInit,
                        lineNumber);
        }
        :   name {
                nameInit = $name.nameStr;
                lineNumber = $start.getLine();
            }
            renderer {
                rendererInit = $renderer.entryRenderer;
            }
            combine {
                combinerInit = $combine.combinerEnum;
            }
            category {
                categoryInit = $category.categoryStr;
            }
            flags {
                flagsInit = $flags.flagsStr;
            }
            desc {
                descInit = $desc.descStr;
            }
        ;

// Top-level rule: the whole file is one or more template records.
file
        returns[List<UIEntryBaseParseRecord> uiEntryBases, String declaredCount]
        @init{
            $uiEntryBases = new ArrayList<>();
        }
        :   recordCount { $declaredCount = $recordCount.count; }
            (entryBase {
            $uiEntryBases.add($entryBase.base);
        })+ EOF
        ;