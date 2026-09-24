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

package uk.co.jackoftradesltd.middle.player;

/**
 * One binding of a {@link PlayerProperty} to a UI entry, with the value that binding carries — the
 * port of C's {@code struct bound_player_ability} ({@code ui-entry.c:69-73}). A {@code ui_entry}
 * holds an array of these, filled by {@code bind_player_ability_to_ui_entry_by_name}
 * ({@code ui-entry.c:271-303}, the runtime home of the {@code bindui:} directive parsed from
 * {@code player_property.txt}).
 *
 * <p>{@link #haveValue} mirrors C's {@code have_value}, which selects between two presentation
 * modes: when true, {@link #value} is what the UI shows outright; when false, the ability has no
 * fixed value and {@code compute_ui_entry_values_for_player} falls back to special-case logic keyed
 * on the ability's index (for example {@code PF_FAST_SHOT} or {@code PF_BRAVERY_30}).
 *
 * @author Rowan Crowther
 */
public class BoundPlayerAbility {
    /**
     * The player property this binding presents (C: {@code bound_player_ability.ability}).
     */
    PlayerProperty ability;
    /**
     * The value to present when {@link #haveValue} is true (C: {@code bound_player_ability.value}).
     */
    int value;
    /**
     * Whether {@link #value} is a fixed figure to present, or whether the ability instead falls back
     * to special-case logic (C: {@code bound_player_ability.have_value}).
     */
    boolean haveValue;
    /**
     * Whether this binding is the auxiliary half of its UI entry (C: {@code bound_player_ability.isaux}).
     */
    boolean isAux;

    /**
     * Builds a binding as {@code bind_player_ability_to_ui_entry_by_name} would populate one entry
     * of a {@code ui_entry}'s {@code p_abilities} array.
     *
     * @param ability   the player property being bound
     * @param value     the value to present when {@code haveValue} is true
     * @param haveValue whether {@code value} is a fixed figure or the ability falls back to
     *                  special-case logic
     * @param isAux     whether this binding is the auxiliary half of its UI entry
     */
    public BoundPlayerAbility(PlayerProperty ability, int value, boolean haveValue, boolean isAux) {
        this.ability = ability;
        this.value = value;
        this.haveValue = haveValue;
        this.isAux = isAux;
    }

    /**
     * @return the player property this binding presents
     */
    public PlayerProperty getAbility() {
        return ability;
    }

    /**
     * @return the value to present when {@link #isHaveValue()} is true
     */
    public int getValue() {
        return value;
    }

    /**
     * @return true if {@link #getValue()} is a fixed figure to present, false if the ability instead
     * falls back to special-case logic
     */
    public boolean isHaveValue() {
        return haveValue;
    }

    /**
     * @return true if this binding is the auxiliary half of its UI entry
     */
    public boolean isAux() {
        return isAux;
    }
}
