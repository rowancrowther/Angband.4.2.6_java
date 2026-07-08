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

// Reader+lexer for lib/gamedata/player_property.txt - the catalogue of every
// player/object flag and elemental resist/vuln/immunity, each with display
// name, description, and an optional binding to a UI element (ui_entry.txt).
// Cf. src/init.c: struct file_parser player_property_parser (init.c:1406),
// directive table registered in init_parse_player_prop() (init.c:1308-1314):
// type/code/desc/name/value/bindui -> parse_player_prop_type/_code/_desc/
// _name/_value/_bindui.
//
// Verified against real data: `code`'s player-vs-object dispatch, `bindui`'s
// TAG -> ElementEnum-vs-Stat dispatch (e.g. "bindui:resist_ui_compact_0<DARK>..."
// under type:player resolves the tag as an ElementEnum; "bindui:stat_mod_ui_
// compact_0<STR>..." under type:object resolves it as a Stat), and `value`'s
// -1/1/3 switch all match the file's actual content and its own header
// documentation - no bugs found there.
//
// POTENTIAL PROBLEMS:
//
//   1. The file's own header says bindui: "can appear multiple times for
//      the same entry to bind it to multiple user interface elements", and
//      parse_player_prop_bindui (init.c:1262) allocates a fresh
//      player_bound_ui node every call (consistent with a list the C side
//      can append to repeatedly) - but `property` below matches `bindui`
//      with `?` (at most once). No current player_property.txt entry
//      actually has two bindui: lines back to back, so this is latent
//      rather than active.
//
//   2. Worth double-checking rather than a confirmed bug: the C parser
//      registers bindui's first field as a single sym ("bindui sym ui int
//      aux sym uival", init.c:1313), so the whole "resist_ui_compact_0
//      <DARK>" text - tag included - is one string on the C side. This
//      grammar instead lexes that into two tokens, TEXT ("resist_ui_
//      compact_0") and TAG ("<DARK>"), and looks up the UI entry via
//      GameConstants.getUIEntry() on the TEXT portion alone. Splitting
//      pre-lookup matches the documented intent ("the name will be
//      parameterized with the element name"), but I couldn't find where
//      the C side's combined string actually gets the tag stripped/
//      substituted (no `player_bound_ui`/`bound_ui` usage outside init.c in
//      this checkout) to confirm the two behave the same way.
//
// @author Rowan Crowther

parser grammar PlayerPropertyGrammar;
options { tokenVocab = PlayerPropertyLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.playerproperty.PlayerPropertyParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER {
                $count = $INTEGER.getText();
            }
        ;

type
        returns[String propertyType, int lineNo]
        :   TYPE TYPE_VALUE {
                $propertyType = $TYPE_VALUE.getText();
                $lineNo = $start.getLine();
            }
        ;

// "code:<NAME>" - the engine-internal flag name (PF_*/OF_* depending on
// `type`); absent for type:element records (see player_property.txt's own
// "ElementEnum codes are templates" note). Cf. parse_player_prop_code (init.c).
code
        returns[String codeName]
        :   CODE FLAG { $codeName = $FLAG.getText(); }
        ;

// "bindui:<ui entry name>[<TAG>]:<aux 0|1>:<value|special>" - binds this
// property to a UI element in ui_entry.txt; under type:player the optional
// <TAG> names an ElementEnum, under type:object it names a Stat (see top-of-
// file note #2 re: the TEXT/TAG split). Cf. parse_player_prop_bindui
// (init.c:1262). See top-of-file note #1 re: the multi-occurrence gap.
binduiEntry
        returns[List<String> entry]
        @init {
            String tagText = "";
        }
        :   BINDUI LCASE (TAG { tagText = $TAG.getText(); })?
            COLON aux = INTEGER
            COLON val = (INTEGER | LCASE)
            {
                $entry = List.of($LCASE.getText(), tagText, $aux.getText(),
                    $val.getText());
            }
        ;

bindUI
        returns[List<List<String>> bindUIs]
        @init {
            $bindUIs = new ArrayList<>();
        }
        :   (binduiEntry { $bindUIs.add($binduiEntry.entry); })*
        ;

// "name:<display name>" - cf. parse_player_prop_name (init.c).
name
        returns[String nameStr]
        :   NAME STRING {$nameStr = $STRING.getText(); }
        ;

// "desc:<text>" - can repeat to build up a multi-line description (see
// `property`'s `(desc {...})*`) - cf. parse_player_prop_desc (init.c).
desc
        returns[String descStr]
        @init {
            StringBuilder sb = new StringBuilder();
        }
        @after {
            $descStr = sb.toString();
        }
        :   (DESC STRING { sb.append($STRING.getText()); })+
        ;

// "value:-1|1|3" - vulnerability/resistance/immunity, for type:element
// records only - cf. parse_player_prop_value (init.c).
value
        returns[String valueInt]
        :   VALUE INTEGER {
            $valueInt = $INTEGER.getText();
        };

// One full property record: type, then any mix of code/bindui/name/desc*/
// value in the order they appear in the file.
property
        returns[PlayerPropertyParseRecord playerProp]
        @init {
            String typeInit = "";
            String codeInit = "";
            List<List<String>> bindUIInit = new ArrayList<>();
            String nameInit = "";
            String descInit = "";
            String valueInit = "";
            int line = 0;
        }
        @after {
            $playerProp = new PlayerPropertyParseRecord(typeInit, codeInit,
            bindUIInit, nameInit, descInit, valueInit, line);
        }
        :   type {
                line = $type.lineNo;
                typeInit = $type.propertyType;
            }
            (code {
                codeInit = $code.codeName;
            })?
            bindUI {
                bindUIInit = $bindUI.bindUIs;
            }
            name { nameInit = $name.nameStr; }
            (desc { descInit = $desc.descStr; })?
            (value { valueInit = $value.valueInt; })?
        ;

// Top-level rule: the whole file is one or more property records.
file
        returns[List<PlayerPropertyParseRecord> properties, String declaredRecords]
        @init {
            $properties = new ArrayList<>();
        }
        :   recordCount { $declaredRecords = $recordCount.count; }
            (property {
                $properties.add($property.playerProp);
            })+ EOF
        ;
