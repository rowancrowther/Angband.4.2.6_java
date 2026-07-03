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

/*
 * @author Rowan Crowther
 *
 * Reader for lib/gamedata/ui_entry_renderer.txt - the 5 backend renderers
 * used by ui_entry.txt elements: which native renderer code to bind to,
 * colour/label-colour/symbol palettes, digit count, and sign display.
 *Cf. src/ui-entry-renderers.c's struct file_parser
 * ui_entry_renderer_parser (ui-entry-renderers.c:1727).
 */

parser grammar UIEntryRendererGrammar;
options { tokenVocab = UIEntryRendererLexer; }

@header
        {
            import java.util.Arrays;
            import java.util.ArrayList;
            import java.util.List;
        }

/*
 * @author Rowan Crowther
 *
 * The declared number of records in the file
 */
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER {
                $count = $INTEGER.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * "name:<text>" - starts a new renderer record; referenced by
 * ui_entry.txt's renderer:.
 */
name
        returns[String nameStr]
        :   NAME STRING {
                $nameStr = $STRING.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * "code:<text>" - binds this renderer to a backend implementation
 * (list-ui-entry-renderers.h).
 */
code
        returns[String codeStr]
        :   CODE FLAG {
                $codeStr = $FLAG.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * "colors:<colour chars>" - palette of colours used for the value.
 */
colours
        returns[String colourCharStr]
        :   COLOURS COLOURCHARS {
                $colourCharStr = $COLOURCHARS.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * "labelcolors:<colour chars>" - palette of colours used for the label.
 */
labelcolours
        returns[String colourCharStr]
        :   LABELCOLOURS COLOURCHARS {
                $colourCharStr = $COLOURCHARS.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * "symbols:<symbol chars>" - palette of symbols used when converting
 * values to display characters.
 */
symbols
        returns[String symbolCharsStr]
        :   SYMBOLS STRING {
                $symbolCharsStr = $STRING.getText();
            }
        ;
/*
 * @author Rowan Crowther
 *
 * "ndigit:<digit>" - number of digits to display for a numeric value.
 */
ndigit
        returns[String numDigitsStr]
        :   NDIGITS INTEGER {
                $numDigitsStr = $INTEGER.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * "sign:NO_SIGN|ALWAYS_SIGN|NEGATIVE_SIGN" - whether/when a sign indicator is shown
 */
sign
        returns[String signEnum]
        :   SIGN FLAG {
                $signEnum = $FLAG.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * One full renderer record: a fixed sequence of name/code/colours/
 * labelcolours/symbols, then optional ndigit/sign.
 */
uiEntry
        returns[List<String> renderer]
        @init {
            String[] uiEntryRendererStrings = new String[7];
            Arrays.fill(uiEntryRendererStrings, "");
            int lineNumber = -1;
        }
        @after {
            $renderer = new ArrayList<>(Arrays.asList(uiEntryRendererStrings));
            $renderer.add(String.valueOf(lineNumber));
        }
        :   name
            {   uiEntryRendererStrings[0] = $name.nameStr;
                lineNumber = $start.getLine();
            }
            code
            { uiEntryRendererStrings[1] = $code.codeStr; }
            (colours
            { uiEntryRendererStrings[2] = $colours.colourCharStr; })?
            (labelcolours
            { uiEntryRendererStrings[3] = $labelcolours.colourCharStr; })?
            (symbols
            { uiEntryRendererStrings[4] = $symbols.symbolCharsStr; })?
            (ndigit
            { uiEntryRendererStrings[5] = $ndigit.numDigitsStr; })?
            (sign
            { uiEntryRendererStrings[6] = $sign.signEnum; })?
        ;

/*
 * @author Rowan Crowther
 *
 * Top-level rule: the whole file is one or more renderer records.
 */
file
        returns[List<List<String>> renderers, String record]
        @init {
            $renderers = new ArrayList<>();
        }
        :   recordCount { $record = $recordCount.count; }
            (uiEntry
            {
                $renderers.add($uiEntry.renderer);
            })+ EOF
        ;