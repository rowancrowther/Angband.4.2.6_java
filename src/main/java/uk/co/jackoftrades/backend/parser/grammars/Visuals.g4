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

// Reader+lexer for lib/gamedata/visuals.txt - colour-cycling animation
// definitions (multi-hued monsters/objects): legacy "flicker" blocks
// (intentionally ignored - see `file` below) and the live "cycle" blocks,
// each a named sequence of colour steps. Cf. src/ui-visuals.c's
// static struct file_parser visuals_file_parser (ui-visuals.c:1092).

grammar Visuals;

@header {
    import uk.co.jackoftrades.frontend.colour.VisualsColourCycle;
    import uk.co.jackoftrades.frontend.colour.VisualsCycleGroup;
    import uk.co.jackoftrades.frontend.colour.VisualsCycler;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;

    import java.util.Map;
    import java.util.List;
    import java.util.HashMap;
    import java.util.ArrayList;
}

// "flicker:<colour char>:<text>" - a legacy flicker definition header.
// Matched but its content is discarded (see `file`'s comment).
flicker
        :   FLICKER COLOUR_CHAR COLON LCASE
        ;

// "flicker-color:<colour char>" - one colour step of a legacy flicker
// definition; can repeat. Also discarded.
flickerColour
        :   FLICKER_COLOUR COLOUR_CHAR
        ;

// One legacy flicker definition: a flicker: header plus its flicker-color:
// steps.
flickerBlock
        :   flicker
            flickerColour+
        ;

// "cycle:flicker|fancy:<name or colour char>" - starts a new colour-cycle
// definition, naming which group ("flicker" or "fancy") and cycle it belongs to.
cycle
        returns[String groupName, String cycleName]
        :   CYCLE (FLICKER { $groupName = "flicker"; }
            | FANCY { $groupName = "fancy"; }) (LCASE { $cycleName = $LCASE.getText(); }
            | COLOUR_CHAR { $cycleName = $COLOUR_CHAR.getText(); })
        ;

// "cycle-color:<colour char>" - one colour step of the current cycle; can
// repeat (see `cycleBlock`'s colourTypes list).
cycleColour
        returns[ColourType colourType]
        :   CYCLE_COLOUR COLOUR_CHAR {
                char rawChar = $COLOUR_CHAR.getText().charAt(0);
                $colourType = ColourType.findColourType(rawChar);
            }
        ;

// One full colour-cycle definition: a cycle: header plus its cycle-color:
// steps, wrapped in its own VisualsCycleGroup. See top-of-file problem #1
// re: this producing one group per cycle instead of one group per
// groupName containing all its cycles.
cycleBlock
        returns[String name, VisualsColourCycle colourCycleObj]
        @init {
            String groupName = "";
            String cycleName = "";
            List<ColourType> colourTypes = new ArrayList<>();
        }
        @after {
            $colourCycleObj = new VisualsColourCycle(cycleName, ColourType.COLOUR_TYPE_DARK);

            for (ColourType colType : colourTypes)
                $colourCycleObj.addStep(colType);

            $name = groupName;
        }
        :   cycle {
                groupName = $cycle.groupName;
                cycleName = $cycle.cycleName;
            }
            (cycleColour {
                ColourType colourType = $cycleColour.colourType;
                colourTypes.add(colourType);
            })+
        ;

// Top-level rule: legacy flicker blocks (matched but ignored - "We are
// ignoring all the flicker blocks as that is a deprecated system"),
// followed by one or more colour-cycle blocks.
file
        returns[VisualsCycler cycler]
        @init {
            Map<String, VisualsCycleGroup> cyclerMap = new HashMap<>();
            $cycler = new VisualsCycler();
        }
        @after {
            for (VisualsCycleGroup group : cyclerMap.values())
                $cycler.addVisualsCycleGroup(group);
        }
        :   flickerBlock+ // We are ignoring all the flicker blocks as that is a depricated system
            (cycleBlock {
                VisualsCycleGroup group = cyclerMap.computeIfAbsent($cycleBlock.name, name -> {
                    VisualsCycleGroup newGroup = new VisualsCycleGroup();
                    newGroup.setGroupName(name);
                    return newGroup;
                });
                group.addCycle($cycleBlock.colourCycleObj);
            })+
            EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL     :   '\r'? '\n' -> skip
        ;

FLICKER
        :   'flicker:'
        ;

FLICKER_COLOUR
        :   'flicker-color:'
        ;

CYCLE
        :   'cycle:'
        ;

FANCY
        :   'fancy:'
        ;

CYCLE_COLOUR
        :   'cycle-color:'
        ;

// Field separator within flicker:/cycle: lines.
COLON
        :   ':'
        ;

// A single colour-code character - used for flicker:/flicker-color:/
// cycle:/cycle-color:.
COLOUR_CHAR
        :   [dwsorgbuDWPyRGBUpvtmYiTVIMzZ]
        ;

// Free-running lowercase text - used for flicker:'s description and
// cycle:'s cycle-name field (e.g. "rainbow", "storm").
LCASE
        :   ('a'..'z' | ' ' | '-' | '/' | '(' | ')' | ',')+
        ;