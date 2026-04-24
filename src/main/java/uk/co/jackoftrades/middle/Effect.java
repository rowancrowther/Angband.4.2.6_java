package uk.co.jackoftrades.middle;

import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.background.numerics.Dice;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.EffectSubTypeEnum;

/**
 * STUB CLASS: TODO Code, comment and test this
 */

public class Effect {
    private EffectEnum index;
    private Dice dice;
    private int y;
    private int x;
    private EffectSubTypeEnum subType;
    private int radius;
    private Object other;
    private String msg;

    /**
     * Determines whether this Effect has a valid Effect index
     *
     * @return true if the index is not EF_NONE or EF_MAX, false otherwise
     */
    @Contract(pure = true)
    public boolean isValid() {
        return index != EffectEnum.EF_NONE && index != EffectEnum.EF_MAX;
    }

    /**
     * Determines whether this effect is aimed
     *
     * @return True if this effect is aimed, false otherwise
     */
    @Contract(pure = true)
    public boolean isAim() {
        if (!isValid())
            return false;

        return index.getAim();
    }

    /**
     * Get the information label for this effect
     *
     * @return the information label for this, or null if no label exists
     */
    @Contract(pure = true)
    public String getInfo() {
        if (!isValid())
            return null;

        return index.getInfoLabel();
    }

    /**
     * Get the description label for this effect
     *
     * @return the description label for this effect, or null if none exists
     */
    @Contract(pure = true)
    public String getDescription() {
        if (!isValid()) return null;

        return index.getDescription();
    }


}
