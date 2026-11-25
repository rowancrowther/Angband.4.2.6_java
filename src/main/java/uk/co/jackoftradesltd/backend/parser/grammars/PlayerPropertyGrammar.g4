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
// Cf. src/init.c: struct file_parser player_property_parser,
// directive table registered in init_parse_player_prop():
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
// DIRECTIVE ORDER AND REPEATS (Stage 7a, matched to C on 261118):
//
//   C takes the directives of a record in any order and any number of times,
//   each handler acting on the open record: code, name and value replace what
//   is there, desc appends (string_append) and bindui adds a binding. `property`
//   below does the same, so a record may carry any mix of the five directives
//   after its `type:`, or none at all, and a file may hold no records.
//
//   1. Repeated bindui: (resolved 261118). The file's own header says bindui:
//      "can appear multiple times for the same entry to bind it to multiple
//      user interface elements", and parse_player_prop_bindui (init.c) adds a
//      binding for every line. `property` now collects every binduiEntry into
//      the record's list in file order, wherever the lines fall in the record.
//      C prepends each line and so binds the last one first; the order is an
//      accepted deviation, recorded in PlayerPropertyAssembler.java.
//
//   2. The name/tag split (resolved 261118). The C parser registers bindui's
//      first field as a single sym ("bindui sym ui int aux sym uival"), so
//      "resist_ui_compact_0<DARK>" is one string on the C side. This grammar
//      lexes it into two tokens, LCASE ("resist_ui_compact_0") and TAG
//      ("<DARK>"), and PlayerPropertyAssembler.java joins them again, name
//      followed by tag, when it builds the binding, so the string Java keeps is
//      the one C passes to bind_player_ability_to_ui_entry_by_name. Nothing at
//      this stage looks the name up in the UI-entry registry.
//
//   Accepted deviations, recorded in the class Javadoc of
//   PlayerPropertyAssembler.java: the order of a record's bindings,
//   and C's NULL for an absent name or desc, which Java holds as the empty
//   string.
//
// @author Rowan Crowther

parser grammar PlayerPropertyGrammar;
options { tokenVocab = PlayerPropertyLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

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
// (init.c). Repeats are collected by `property`; see top-of-file note #1.
binduiEntry
        returns[List<String> entry]
        @init {
            String tagText = "";
        }
        :   BINDUI FIRST_STRING (TAG_START t=BINDUI_TAG TAG_END { tagText = $t.getText(); })?
            BINDUI_COLON aux=AUX_HEX
            AUX_COLON val=AUX_TO_EOL
            {
                $entry = List.of($FIRST_STRING.getText(), tagText, $aux.getText(),
                    $val.getText());
            }
        ;

bindUI
        returns[List<List<String>> bindUIs]
        @init {
            $bindUIs = new ArrayList<>();
        }
        :   binduiEntry { $bindUIs.add($binduiEntry.entry); }
        ;

// "name:<display name>" - cf. parse_player_prop_name (init.c).
name
        returns[String nameStr]
        :   NAME STRING {$nameStr = $STRING.getText(); }
        ;

// "desc:<text>" - can repeat, anywhere in the record, to build up a multi-line
// description: `property` appends each line to the text so far, as C's
// string_append does in parse_player_prop_desc (init.c).
desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

// "value:-1|1|3" - vulnerability/resistance/immunity, for type:element
// records only - cf. parse_player_prop_value (init.c).
value
        returns[String valueInt]
        :   VALUE INTEGER {
            $valueInt = $INTEGER.getText();
        };

// One full property record: type, then any mix of code/bindui/name/desc/value,
// in any order and any number of times. code, name and value keep the last one
// given; desc lines are appended and bindui lines are all kept, in file order.
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
        (   code {
                codeInit = $code.codeName;
            }
        |   bindUI {
                bindUIInit.addAll($bindUI.bindUIs);
            }
        |   name { nameInit = $name.nameStr; }
        |   desc { descInit = descInit + $desc.descStr; }
        |   value { valueInit = $value.valueInt; })*
        ;

// Top-level rule: the whole file is zero or more property records.
file
        returns[List<PlayerPropertyParseRecord> properties]
        @init {
            $properties = new ArrayList<>();
        }
        :   (property {
                $properties.add($property.playerProp);
            })* EOF
        ;
