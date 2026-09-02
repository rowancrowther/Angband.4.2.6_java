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

lexer grammar PlayerPropertyLexer;
import CommentsAndEol, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

TYPE
        :   'type:'
        ;

CODE
        :   'code:' -> pushMode(FLAG_MODE)
        ;

BINDUI
        :   'bindui:'
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

mode FREE_TEXT_MODE;

STRING
    :   ~('\r' | '\n')+ -> popMode
    ;

mode FLAG_MODE;

FLAG
        :   FLAG_BODY -> popMode
        ;
