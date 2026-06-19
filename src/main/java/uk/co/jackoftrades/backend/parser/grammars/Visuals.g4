grammar Visuals;

@header {
    import uk.co.jackoftrades.frontend.colour.VisualsColourCycle;
    import uk.co.jackoftrades.frontend.colour.VisualsCycleGroup;
    import uk.co.jackoftrades.frontend.colour.VisualsCycler;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;

    import java.util.List;
    import java.util.ArrayList;
}

flicker
        :   FLICKER COLOUR_CHAR COLON LCASE
        ;

flickerColour
        :   FLICKER_COLOUR COLOUR_CHAR
        ;

flickerBlock
        :   flicker
            flickerColour+
        ;

cycle
        returns[String groupName, String cycleName]
        :   CYCLE (FLICKER { $groupName = "flicker"; }
            | FANCY { $groupName = "fancy"; }) (LCASE { $cycleName = $LCASE.getText(); }
            | COLOUR_CHAR { $cycleName = $COLOUR_CHAR.getText(); })
        ;

cycleColour
        returns[ColourType colourType]
        :   CYCLE_COLOUR COLOUR_CHAR {
                char rawChar = $COLOUR_CHAR.getText().charAt(0);
                $colourType = ColourType.findColourType(rawChar);
            }
        ;

cycleBlock
        returns[VisualsCycleGroup group]
        @init {
            String groupName = "";
            String cycleName = "";
            List<ColourType> colourTypes = new ArrayList<>();
            $group = new VisualsCycleGroup();
        }
        @after {
            VisualsColourCycle colourCycle = new VisualsColourCycle(cycleName, ColourType.COLOUR_TYPE_DARK);

            for (ColourType colType : colourTypes)
                colourCycle.addStep(colType);

            $group.setGroupName(groupName);
            $group.addCycle(colourCycle);
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

file
        returns[VisualsCycler cycler]
        @init {
            $cycler = new VisualsCycler();
        }
        :   flickerBlock+ // We are ignoring all the flicker blocks as that is a depricated system
            (cycleBlock {
                $cycler.addVisualsCycleGroup($cycleBlock.group);
            })+
            EOF
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

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

COLON
        :   ':'
        ;

COLOUR_CHAR
        :   [dwsorgbuDWPyRGBUpvtmYiTVIMzZ]
        ;

LCASE
        :   ('a'..'z' | ' ' | '-' | '/' | '(' | ')' | ',')+
        ;