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

package uk.co.jackoftrades.middle.game.enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The full set of game options — the Java port of the C original's
 * {@code list-options.h}. Each constant bundles the option's user-facing
 * {@link #getDescription() description}, its {@link OptionType} category
 * (interface, birth, cheat, score, special) and its default on/off value
 * ({@link #isNormal() normal}). Birth options ({@link OptionType#OP_BIRTH}) are
 * the ones character creation consults — see {@code StartOptionExclusion} and
 * the class {@code equip}/{@code eopts} handling; the rest govern interface
 * behaviour, cheating and scoring.
 * <p>
 * Two constraints carried over from the C source: no more than 21 options of
 * any one {@link OptionType} (later ones are ignored), and each {@code cheat}
 * option must be immediately followed by its matching {@code score} option —
 * hence the paired {@code OPT_cheat_*}/{@code OPT_score_*} ordering below.
 *
 * @author Rowan Crowther
 */
public enum Options {

    /*
     * The options in C list-options.h order. Each constant is self-describing
     * through its description string; the tuple is
     * (description, OptionType category, default on/off value).
     */
    OPT_none("", OptionType.OP_SPECIAL, false),
    OPT_rogue_like_commands("Use the roguelike command keyset", OptionType.OP_INTERFACE, false),
    OPT_autoexplore_commands("Use autoexplore commands", OptionType.OP_INTERFACE, false),
    OPT_use_sound("Use sound", OptionType.OP_INTERFACE, false),
    OPT_show_damage("Show damage player deals to monsters", OptionType.OP_INTERFACE, false),
    OPT_use_old_target("Use old target by default", OptionType.OP_INTERFACE, false),
    OPT_pickup_always("Always pickup items", OptionType.OP_INTERFACE, false),
    OPT_pickup_inven("Always pickup items matching inventory", OptionType.OP_INTERFACE, true),
    OPT_show_flavors("Show flavors in object descriptions", OptionType.OP_INTERFACE, false),
    OPT_show_target("Highlight target with cursor", OptionType.OP_INTERFACE, true),
    OPT_highlight_player("Highlight player with cursor between turns", OptionType.OP_INTERFACE, false),
    OPT_disturb_near("Disturb whenever viewable monster moves", OptionType.OP_INTERFACE, true),
    OPT_solid_walls("Show walls as solid blocks", OptionType.OP_INTERFACE, false),
    OPT_hybrid_walls("Show walls with shaded background", OptionType.OP_INTERFACE, false),
    OPT_view_yellow_light("Color: Illuminate torchlight in yellow", OptionType.OP_INTERFACE, false),
    OPT_animate_flicker("Color: Shimmer multi-colored things", OptionType.OP_INTERFACE, false),
    OPT_center_player("Center map continuously", OptionType.OP_INTERFACE, false),
    OPT_purple_uniques("Color: Show unique monsters in purple", OptionType.OP_INTERFACE, false),
    OPT_auto_more("Automatically clear '-more-' prompts", OptionType.OP_INTERFACE, false),
    OPT_hp_changes_color("Color: Player color indicates % hit points", OptionType.OP_INTERFACE, true),
    OPT_mouse_movement("Allow mouse clicks to move the player", OptionType.OP_INTERFACE, true),
    OPT_notify_recharge("Notify on object recharge", OptionType.OP_INTERFACE, false),
    OPT_effective_speed("Show effective speed as multiplier", OptionType.OP_INTERFACE, false),
    OPT_cheat_hear("Cheat: Peek into monster creation", OptionType.OP_CHEAT, false),
    OPT_score_hear("Score: Peek into monster creation", OptionType.OP_SCORE, false),
    OPT_cheat_room("Cheat: Peek into dungeon creation", OptionType.OP_CHEAT, false),
    OPT_score_room("Score: Peek into dungeon creation", OptionType.OP_SCORE, false),
    OPT_cheat_xtra("Cheat: Peek into something else", OptionType.OP_CHEAT, false),
    OPT_score_xtra("Score: Peek into something else", OptionType.OP_SCORE, false),
    OPT_cheat_live("Cheat: Allow player to avoid death", OptionType.OP_CHEAT, false),
    OPT_score_live("Score: Allow player to avoid death", OptionType.OP_SCORE, false),
    OPT_birth_randarts("Generate a new, random artifact set", OptionType.OP_BIRTH, false),
    OPT_birth_connect_stairs("Generate connected stairs", OptionType.OP_BIRTH, true),
    OPT_birth_force_descend("Force player descent (never make up stairs)", OptionType.OP_BIRTH, false),
    OPT_birth_no_recall("Word of Recall has no effect", OptionType.OP_BIRTH, false),
    OPT_birth_no_artifacts("Restrict creation of artifacts", OptionType.OP_BIRTH, false),
    OPT_birth_stacking("Stack objects on the floor", OptionType.OP_BIRTH, true),
    OPT_birth_lose_arts("Lose artifacts when leaving level", OptionType.OP_BIRTH, false),
    OPT_birth_feelings("Show level feelings", OptionType.OP_BIRTH, true),
    OPT_birth_no_selling("Increase gold drops but disable selling", OptionType.OP_BIRTH, true),
    OPT_birth_start_kit("Start with a kit of useful gear", OptionType.OP_BIRTH, true),
    OPT_birth_ai_learn("Monsters learn from their mistakes", OptionType.OP_BIRTH, true),
    OPT_birth_know_runes("Know all runes on birth", OptionType.OP_BIRTH, false),
    OPT_birth_know_flavors("Know all flavors on birth", OptionType.OP_BIRTH, false),
    OPT_birth_levels_persist("Persistent levels (experimental)", OptionType.OP_BIRTH, false),
    OPT_birth_percent_damage("To-damage is a percentage of dice (experimental)", OptionType.OP_BIRTH, false);

    /**
     * The option's user-facing description, as shown in the options menu.
     *
     * @author Rowan Crowther
     */
    private final String description;
    /**
     * The category this option belongs to (interface, birth, cheat, score, special).
     *
     * @author Rowan Crowther
     */
    private final OptionType type;
    /**
     * The option's default value under normal play — {@code true} if it starts
     * switched on. Ports the C {@code normal} column of {@code list-options.h}.
     *
     * @author Rowan Crowther
     */
    private final boolean normal;

    /**
     * @param description the user-facing description
     * @param type        the option's category
     * @param optional    the default on/off value, stored as {@link #normal}
     * @author Rowan Crowther
     */
    Options(@NotNull String description, @NotNull OptionType type, boolean optional) {
        this.description = description;
        this.type = type;
        this.normal = optional;
    }

    /**
     * @return the option's user-facing description
     * @author Rowan Crowther
     */
    @NotNull
    @Contract(pure = true)
    public String getDescription() {
        return description;
    }

    /**
     * @return the option's category
     * @author Rowan Crowther
     */
    @NotNull
    @Contract(pure = true)
    public OptionType getType() {
        return type;
    }

    /**
     * @return {@code true} if this option defaults to on under normal play
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    public boolean isNormal() {
        return normal;
    }

    /**
     * The option's bare name with the {@code OPT_} prefix stripped — e.g.
     * {@code birth_randarts} — matching the token used in the game-data files
     * (class {@code eopts}, option menus).
     *
     * @return the prefix-free option name
     * @author Rowan Crowther
     */
    @NotNull
    @Contract(pure = true)
    public String getName() {
        return this.toString().substring(4);
    }
}
