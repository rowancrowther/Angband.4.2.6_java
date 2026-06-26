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

// Reader+lexer for lib/gamedata/ui_entry_renderer.txt - the 5 backend
// renderers used by ui_entry.txt elements: which native renderer code to
// bind to, colour/label-colour/symbol palettes, digit count, and sign
// display. Cf. src/ui-entry-renderers.c's struct file_parser
// ui_entry_renderer_parser (ui-entry-renderers.c:1727).
//
// POTENTIAL PROBLEMS (both latent - not currently triggered, since all 5
// renderers in this repo's ui_entry_renderer.txt happen to provide every
// field this grammar treats as mandatory and none use the missing ones):
//
//   1. The file's own header documents colors:/labelcolors:/symbols: as
//      all optional ("Both are optional; if not set, default values from
//      the backend are used" / "It is optional" for symbols), but
//      `uiEntry` matches `colours`/`labelcolours`/`symbols` with no '?' -
//      all mandatory. Every one of the 5 current renderer records happens
//      to set all three anyway.
//
//   2. The header also documents combiner:/units:/combined-renderer:
//      directives that this grammar has no tokens or rules for at all.
//      No current renderer record uses any of them.
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar UIEntryRendererGrammar;

@header
        {
            import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
            import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
            import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

            import java.util.ArrayList;
            import java.util.List;
        }

// "name:<text>" - starts a new renderer record; referenced by ui_entry.txt's renderer:.
name
        returns[String nameStr]
        :   NAME LCASEWORD {
                $nameStr = $LCASEWORD.getText();
            }
        ;

// "code:<text>" - binds this renderer to a backend implementation
// (list-ui-entry-renderers.h).
code
        returns[UIEntryRendererEnum codeEnum]
        :   CODE UCASEWORD {
                String flag = "UI_ENTRY_RENDERER_" + $UCASEWORD.getText();
                $codeEnum = UIEntryRendererEnum.valueOf(flag);
            }
        ;

// "colors:<colour chars>" - palette of colours used for the value. See
// top-of-file problem #1 re: this being mandatory here despite being
// documented as optional.
colours
        returns[String colourCharStr]
        :   COLOURS COLOURCHARS {
                $colourCharStr = $COLOURCHARS.getText();
            }
        ;

// "labelcolors:<colour chars>" - palette of colours used for the label.
// See top-of-file problem #1.
labelcolours
        returns[String colourCharStr]
        :   LABELCOLOURS COLOURCHARS {
                $colourCharStr = $COLOURCHARS.getText();
            }
        ;

// "symbols:<symbol chars>" - palette of symbols used when converting
// values to display characters. See top-of-file problem #1.
symbols
        returns[String symbolCharsStr]
        :   SYMBOLS SYMBOLCHARS {
                $symbolCharsStr = $SYMBOLCHARS.getText();
            }
        ;

// "ndigit:<digit>" - number of digits to display for a numeric value.
ndigit
        returns[int numDigitsInt]
        :   NDIGITS DIGIT {
                $numDigitsInt = Integer.parseInt($DIGIT.getText());
            }
        ;

// "sign:NO_SIGN|ALWAYS_SIGN|NEGATIVE_SIGN" - whether/when a sign indicator is shown.
sign
        returns[UIEntryEnum signEnum]
        :   SIGN UCASEWORD {
                String flag = "UI_ENTRY_" + $UCASEWORD.getText();
                $signEnum = UIEntryEnum.valueOf(flag);
            }
        ;

// One full renderer record: a fixed sequence of name/code/colours/
// labelcolours/symbols, then optional ndigit/sign.
uiEntry
        returns[UIEntryRenderer renderer]
        @init {
            String nameInit = "";
            UIEntryRendererEnum codeInit = UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE;
            String colourCharsInit = "";
            String labelColourCharsInit = "";
            String symbolCharsInit = "";
            int nDigitInit = 1;
            UIEntryEnum signInit = UIEntryEnum.UI_ENTRY_NO_SIGN;
        }
        @after {
            $renderer = new UIEntryRenderer(nameInit,
                        codeInit, colourCharsInit,
                        labelColourCharsInit, symbolCharsInit,
                        nDigitInit, signInit);
        }
        :   name
            { nameInit = $name.nameStr; }
            code
            { codeInit = $code.codeEnum; }
            colours
            { colourCharsInit = $colours.colourCharStr; }
            labelcolours
            { labelColourCharsInit = $labelcolours.colourCharStr; }
            symbols
            { symbolCharsInit = $symbols.symbolCharsStr; }
            (ndigit
            { nDigitInit = $ndigit.numDigitsInt; })?
            (sign
            { signInit = $sign.signEnum; })?
        ;

// Top-level rule: the whole file is one or more renderer records.
file
        returns[List<UIEntryRenderer> renderers]
        @init {
            $renderers = new ArrayList<>();
        }
        :   (uiEntry
            {
                $renderers.add($uiEntry.renderer);
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

// NAME through SIGN below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
NAME
        :   'name:'
        ;

CODE
        :   'code:'
        ;

COLOURS
        :   'colors:'
        ;

LABELCOLOURS
        :   'labelcolors:'
        ;

SYMBOLS
        :   'symbols:'
        ;

NDIGITS
        :   'ndigit:'
        ;

SIGN
        :   'sign:'
        ;

// One or more Angband colour-code characters - used for colors:/labelcolors:.
COLOURCHARS
        :    ('d'|'w'|'s'|'o'|'r'|'g'|'b'|'u'|'D'|'W'|'P'|'y'|'R'|'G'|'B'|'U'|'p'|'v'|'t'|'m'|'Y'|'i'|'T'|'V'|'I'|'M'|'z'|'Z')+
        ;

// One or more display-symbol characters - used for symbols:.
SYMBOLCHARS
        :   ('?'|' '|'.'|'s'|'*'|'='|'+'|'!'|'-'|'^'|'%'|'~')+
        ;

// A single digit - used for ndigit:.
DIGIT
        :   ('0'..'9')
        ;

// A lowercase_with_underscores symbolic name (digit '1' also allowed) -
// used for name:.
LCASEWORD
        :   ('a'..'z'|'1'|'_')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES symbolic name - used for code:/sign:.
UCASEWORD
        :   ('A'..'Z'|'_')+
        ;

// POTENTIAL SOLUTIONS
//
//   1. Only worth relaxing if a future renderer omits colors:/labelcolors:/
//      symbols: - mark them `?` and let the constructed UIEntryRenderer
//      fall back to backend defaults (mirroring the documented behaviour)
//      when absent.
//
//   2. Only worth adding if a renderer actually needs combiner:/units:/
//      combined-renderer: - add COMBINER/UNITS/COMBINED_RENDERER tokens
//      and corresponding optional rules/fields.