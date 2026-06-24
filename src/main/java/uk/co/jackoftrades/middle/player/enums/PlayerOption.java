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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.player.enums;

/**
 * The set of user-configurable game options, each pairing a human-readable description
 * with the option category it belongs to and its default ("normal") value.
 *
 * <p>Ports the C option table (driven by {@code list-options.h} / {@code option.c}). In the
 * original an option is an index into parallel arrays of name, description, type and
 * default; here each option is a single enum constant carrying those attributes inline, so
 * the data and its metadata cannot drift out of step.
 *
 * <p><b>Why the description and default live on the constant:</b> options are surfaced in
 * menus and persisted in preferences, so every option needs its label, its grouping
 * ({@link PlayerOptionTypes} — interface / birth / cheat / score / special) and the value a
 * fresh game starts with. Bundling that triple onto the constant keeps it authoritative in
 * one place. {@code OP_none} is the index-0 placeholder mirroring the C sentinel.
 *
 * @author ClaudeCode
 */
public enum PlayerOption {
    OP_none("",
            PlayerOptionTypes.SPECIAL, false),
    OP_rogue_like_commands("Use the roguelike command keyset",
            PlayerOptionTypes.INTERFACE, false),
    OP_autoexplore_commands("Use autoexplore commands",
            PlayerOptionTypes.INTERFACE, false),
    OP_use_sound("Use sound",
            PlayerOptionTypes.INTERFACE, false),
    OP_show_damage("Show damage player deals to monsters",
            PlayerOptionTypes.INTERFACE, false),
    OP_use_old_target("Use old target by default",
            PlayerOptionTypes.INTERFACE, false),
    OP_pickup_always("Always pickup items",
            PlayerOptionTypes.INTERFACE, false),
    OP_pickup_inven("Always pickup items matching inventory",
            PlayerOptionTypes.INTERFACE, true),
    OP_show_flavors("Show flavors in object descriptions",
            PlayerOptionTypes.INTERFACE, false),
    OP_show_target("Highlight target with cursor",
            PlayerOptionTypes.INTERFACE, true),
    OP_highlight_player("Highlight player with cursor between turns",
            PlayerOptionTypes.INTERFACE, false),
    OP_disturb_near("Disturb whenever viewable monster moves",
            PlayerOptionTypes.INTERFACE, true),
    OP_solid_walls("Show walls as solid blocks",
            PlayerOptionTypes.INTERFACE, false),
    OP_hybrid_walls("Show walls with shaded background",
            PlayerOptionTypes.INTERFACE, false),
    OP_view_yellow_light("Color: Illuminate torchlight in yellow",
            PlayerOptionTypes.INTERFACE, false),
    OP_animate_flicker("Color: Shimmer multi-colored things",
            PlayerOptionTypes.INTERFACE, false),
    OP_center_player("Center map continuously",
            PlayerOptionTypes.INTERFACE, false),
    OP_purple_uniques("Color: Show unique monsters in purple",
            PlayerOptionTypes.INTERFACE, false),
    OP_auto_more("Automatically clear '-more-' prompts",
            PlayerOptionTypes.INTERFACE, false),
    OP_hp_changes_color("Color: Player color indicates % hit points",
            PlayerOptionTypes.INTERFACE, true),
    OP_mouse_movement("Allow mouse clicks to move the player",
            PlayerOptionTypes.INTERFACE, true),
    OP_notify_recharge("Notify on object recharge",
            PlayerOptionTypes.INTERFACE, false),
    OP_effective_speed("Show effective speed as multiplier",
            PlayerOptionTypes.INTERFACE, false),
    OP_cheat_hear("Cheat: Peek into monster creation",
            PlayerOptionTypes.CHEAT, false),
    OP_score_hear("Score: Peek into monster creation",
            PlayerOptionTypes.SCORE, false),
    OP_cheat_room("Cheat: Peek into dungeon creation",
            PlayerOptionTypes.CHEAT, false),
    OP_score_room("Score: Peek into dungeon creation",
            PlayerOptionTypes.SCORE, false),
    OP_cheat_xtra("Cheat: Peek into something else",
            PlayerOptionTypes.CHEAT, false),
    OP_score_xtra("Score: Peek into something else",
            PlayerOptionTypes.SCORE, false),
    OP_cheat_live("Cheat: Allow player to avoid death",
            PlayerOptionTypes.CHEAT, false),
    OP_score_live("Score: Allow player to avoid death",
            PlayerOptionTypes.SCORE, false),
    OP_birth_randarts("Generate a new, random artifact set",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_connect_stairs("Generate connected stairs",
            PlayerOptionTypes.BIRTH, true),
    OP_birth_force_descend("Force player descent (never make up stairs),",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_no_recall("Word of Recall has no effect",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_no_artifacts("Restrict creation of artifacts",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_stacking("Stack objects on the floor",
            PlayerOptionTypes.BIRTH, true),
    OP_birth_lose_arts("Lose artifacts when leaving level",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_feelings("Show level feelings",
            PlayerOptionTypes.BIRTH, true),
    OP_birth_no_selling("Increase gold drops but disable selling",
            PlayerOptionTypes.BIRTH, true),
    OP_birth_start_kit("Start with a kit of useful gear",
            PlayerOptionTypes.BIRTH, true),
    OP_birth_ai_learn("Monsters learn from their mistakes",
            PlayerOptionTypes.BIRTH, true),
    OP_birth_know_runes("Know all runes on birth",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_know_flavors("Know all flavors on birth",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_levels_persist("Persistent levels (experimental),",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_percent_damage("To-damage is a percentage of dice (experimental),",
            PlayerOptionTypes.BIRTH, false);

    /**
     * Menu label shown to the player (C: option description string).
     */
    private final String description;
    /** Which option group this belongs to, controlling where and whether it is shown. */
    private final PlayerOptionTypes playerOptionType;
    /** The default value applied at birth and on a reset-to-defaults. */
    private final boolean normal;

    /**
     * Binds an option to its display text, category and default state.
     *
     * @param description      the menu label
     * @param playerOptionType the option's category
     * @param normal           the default value used for a new character
     */
    private PlayerOption(String description, PlayerOptionTypes playerOptionType, boolean normal) {
        this.description = description;
        this.playerOptionType = playerOptionType;
        this.normal = normal;
    }
}
