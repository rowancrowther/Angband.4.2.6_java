package uk.co.jackoftrades.backend.colour.enums;

import org.jetbrains.annotations.Contract;

/**
 * Colour translation enum used to support environments with a limited colour depth, as well as translating colours to
 * alternates for, for example, menu highlighting
 */
public enum ColourTranslation {
    /**
     * Full colour - keep the colour the same
     */
    ATTR_FULL(0),

    /**
     * Just black and white
     */
    ATTR_MONO(1),

    /**
     * VGA standard 16 colours
     */
    ATTR_VGA(2),

    /**
     * Colours to use if the character is "blind"
     */
    ATTR_BLIND(3),

    /**
     * Colour to use for torch light areas
     */
    ATTR_LIGHT(4),

    /**
     * Colours to use for dark areas
     */
    ATTR_DARK(5),

    /**
     * Colours for highlighted items
     */
    ATTR_HIGH(6),

    /**
     * Metallic colours
     */
    ATTR_METAL(7),

    /**
     * Miscellaneous colours
     */
    ATTR_MISC(8),

    /**
     * The maximum number of colour translations. If the index to ColourType.colourTable[] is greater or equal to this,
     * or less than 0, an index out of bounds error will be thrown.
     */
    ATTR_MAX(9);

    private final int value;

    @Contract(pure = true)
    ColourTranslation(int value) {
        this.value = value;
    }

    @Contract(pure = true)
    public int getValue() {
        return value;
    }
}
