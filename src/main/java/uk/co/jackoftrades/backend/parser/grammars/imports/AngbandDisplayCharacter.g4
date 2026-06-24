lexer grammar AngbandDisplayCharacter;

GRAPHICS
        :   'graphics:' -> mode(GLYPH_MODE)
        ;

COLOUR_ONLY
        :   'color:' -> mode(COLOUR_MODE)
        ;

GLYPH_ONLY
        :   'glyph:' -> mode(GLYPH_MODE)
        ;

// The full Angband colour-code alphabet (one char per colour, including
// the extended/bright variants) - used for graphics:'s colour field.
fragment GRAPHICS_COLOUR_FRAGMENT

        :  ('D' | 'w' | 's' | 'o' | 'r' | 'g' | 'b' | 'u' | 'W' | 'P' | 'y' | 'R'
                | 'G' | 'B' | 'U' | 'p' | 'v' | 't' | 'm' | 'Y' | 'i' | 'T' | 'V'
                | 'I' | 'M' | 'z' | 'Z' | 'd'

                | 'dark' | 'white' | 'slate' | 'orange' | 'red' | 'green' | 'blue'
                | 'umber' | 'light dark' | 'light slate' | 'light purple' | 'yellow'
                | 'light red' | 'light green' | 'light blue' | 'light umber'
                | 'purple' | 'violet' | 'teal' | 'mud' | 'light yellow'
                | 'magenta-pink' | 'light teal' | 'light violet' | 'light pink'
                | 'mustard' | 'blue slate' | 'deep light blue' | 'shade'

                | 'Dark' | 'White' | 'Slate' | 'Orange' | 'Red' | 'Green' | 'Blue'
                | 'Umber' | 'Light Dark' | 'Light Slate' | 'Light Purple' | 'Yellow'
                | 'Light Red' | 'Light Green' | 'Light Blue' | 'Light Umber'
                | 'Purple' | 'Violet' | 'Teal' | 'Mud' | 'Light Yellow'
                | 'Magenta-Pink' | 'Light Teal' | 'Light Violet' | 'Light Pink'
                | 'Mustard' | 'Blue Slate' | 'Deep Light Blue' | 'Shade')
        ;

// The display glyph for graphics: - includes digits 1-8 for the numbered
// shop entrances.
// This is not a total list, others will be added
fragment GRAPHICS_SYMBOL_FRAGMENT
        :   ~(':' | '\r' | '\n')
        ;

mode COLOUR_MODE;

COLOUR_VALUES
        :   GRAPHICS_COLOUR_FRAGMENT -> mode(DEFAULT_MODE)
        ;

mode GLYPH_MODE;

GLYPH_VALUES
        :   GRAPHICS_SYMBOL_FRAGMENT
        ;

COLON_SWITCH
        :   ':' -> mode(COLOUR_MODE)
        ;
