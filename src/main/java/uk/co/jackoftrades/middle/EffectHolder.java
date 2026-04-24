package uk.co.jackoftrades.middle;

/**
 * STUB CLASS: TODO Code, comment and test this
 */

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public class EffectHolder {
    private final List<Effect> effects;

    /**
     * Constructor - create a blank EffectHolder
     */
    @Contract(pure = true)
    public EffectHolder() {
        effects = new ArrayList<>();
    }

    /**
     * Add a new effect to this EffectHolder
     *
     * @param effect the effect to add
     */
    public void add(Effect effect) {
        effects.add(effect);
    }

    /**
     * Determine if all the effects in this Holder are valid
     *
     * @return true if all the effects in this Holder are valid, false otherwise
     */
    public boolean isValid() {
        for (Effect effect : effects) {
            if (!effect.isValid()) return false;
        }

        return true;
    }

    /**
     * Determine if there is a single effect in this Holder which has the EF_AIM flag
     *
     * @return true if one of the effects indices in this Holder is EF_AIM, false otherwise
     */
    public boolean aim() {
        if (!isValid())
            return false;

        for (Effect effect : effects) {
            if (effect.isAim())
                return true;
        }

        return false;
    }
}
