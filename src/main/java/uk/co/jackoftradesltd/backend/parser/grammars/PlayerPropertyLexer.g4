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
//   is there, desc appends (string_append) and bindui adds a binding.
//   PlayerPropertyGrammar.g4's `property` rule does the same, so a record may
//   carry any mix of the five directives after its `type:`, or none at all,
//   and a file may hold no records.
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
//      "resist_ui_compact_0<DARK>" is one string on the C side. This lexer
//      splits it into two tokens, LCASE ("resist_ui_compact_0") and TAG
//      ("<DARK>"), and PlayerPropertyAssembler.java joins them again, name
//      followed by tag, when it builds the binding, so the string Java keeps is
//      the one C passes to bind_player_ability_to_ui_entry_by_name. Nothing at
//      this stage looks the name up in the UI-entry registry.
//
//   Not yet matched: the order of a record's bindings (above) and C's NULL for
//   an absent name or desc, which Java holds as the empty string.
//
// @author Rowan Crowther

lexer grammar PlayerPropertyLexer;
import CommentsAndEol, Flags;

TYPE
        :   'type:'
        ;

CODE
        :   'code:' -> pushMode(FLAG_MODE)
        ;

BINDUI
        :   'bindui:' -> mode(BINDUI_MODE)
        ;

NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

VALUE
        :   'value:'
        ;

// A (possibly negative) literal integer - used by value:.
INTEGER
        :   '-'? ('0'..'9')+
        ;

TYPE_VALUE
        :   'player' | 'object' | 'element'
        ;

LCASE
        :   ('a'..'z' | '0'..'9' | '_')+
        ;

TAG
        :   '<' ('A'..'Z')+ '>'
        ;

COLON
        :   ':'
        ;

mode BINDUI_MODE;

FIRST_STRING
        :   ~('\r' | '\n' | ':' | '<')+
        ;

TAG_START
        :   '<' -> mode(TAG_MODE)
        ;

BINDUI_COLON
        :   ':' -> mode(AUX_MODE)
        ;

mode AUX_MODE;

AUX_HEX
        :   ('-' | '+')? ('0' ('X' | 'x')?)? ('0'..'9' | 'a'..'f' | 'A'..'F')+
        ;

AUX_COLON
        :   ':' -> mode(VAL_MODE)
        ;

mode VAL_MODE;

AUX_TO_EOL
        :   ~('\r' | '\n')+
        ;

AUX_EOL
        :   NL -> skip, mode(DEFAULT_MODE)
        ;

mode TAG_MODE;

BINDUI_TAG
        :   ~('\n' | '\r' | '>')+
        ;

TAG_END
        :   '>' -> mode(BINDUI_MODE)
        ;

mode FREE_TEXT_MODE;

STRING
    :   ~('\r' | '\n')+ -> popMode
    ;

mode FLAG_MODE;

FLAG
        :   FLAG_BODY -> popMode
        ;
