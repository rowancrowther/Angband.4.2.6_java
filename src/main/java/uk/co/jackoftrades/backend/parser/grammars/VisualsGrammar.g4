parser grammar VisualsGrammar;

options { tokenVocab = VisualsLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.visuals.VisualsFlickerParseRecord;
    import uk.co.jackoftrades.backend.parser.visuals.VisualsCycleParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

flicker
        returns[String colourChar, String blockName]
        :   FLICKER COLOUR_CHAR SWAP_COLON STRING
            { $colourChar = $COLOUR_CHAR.getText();
              $blockName = $STRING.getText(); }
        ;

flickerColour
        returns[String colourChar]
        :   FLICKER_COLOUR COLOUR_CHAR
            { $colourChar = $COLOUR_CHAR.getText(); }
        ;

flickerBlock
        returns[VisualsFlickerParseRecord flickerRecord]
        @init {
            ArrayList<String> colourChars = new ArrayList<>();
            int line = $start.getLine();
            String name = "";
            String colourChar = "";
        }
        @after {
            $flickerRecord = new VisualsFlickerParseRecord(name, colourChar, colourChars, line);
        }
        :   flicker { name = $flicker.blockName; colourChar = $flicker.colourChar; }
            (flickerColour { colourChars.add($flickerColour.colourChar); })+
        ;

cycle
        returns[String cycleGroup, String cycleName]
        :   CYCLE STRING_BETWEEN_COLON DELIM_COLON STRING
        {   $cycleGroup = $STRING_BETWEEN_COLON.getText();
            $cycleName = $STRING.getText(); }
        ;

cycleColour
        returns[String colour]
        :   CYCLE_COLOUR COLOUR_CHAR {$colour = $COLOUR_CHAR.getText(); }
        ;

cycleBlock
        returns[VisualsCycleParseRecord cycleRecord]
        @init {
            String cycleGroup;
            String cycleName;
            ArrayList<String> colours = new ArrayList<>();
            int line = $start.getLine();
        }
        @after {
            $cycleRecord = new VisualsCycleParseRecord(cycleGroup, cycleName, colours, line);
        }
        :   cycle { cycleGroup = $cycle.cycleGroup;
                    cycleName = $cycle.cycleName; }
            (cycleColour { colours.add($cycleColour.colour); })+
        ;

file
        returns[List<VisualsFlickerParseRecord> flickers,
                List<VisualsCycleParseRecord> cycles]
        @init { $flickers = new ArrayList<>();
                $cycles = new ArrayList<>(); }
        :   ((flickerBlock { $flickers.add($flickerBlock.flickerRecord); })
            | (cycleBlock { $cycles.add($cycleBlock.cycleRecord); }))+ EOF
        ;
