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

package uk.co.jackoftradesltd.middle.player.enums;

/**
 * The set of user-configurable game options, each pairing a human-readable description
 * with the option category it belongs to and its default ("normal") value.
 *
 * <p>Ports the C option table, which {@code list-options.h} expands through the {@code OP()}
 * macro in two places: {@code option.h} turns each row into an {@code OPT_xxx} index, and
 * {@code option.c} turns each row into an entry of the {@code options[]} array holding the
 * name, description, type and default. Here each option is a single enum constant carrying
 * the description, type and default inline, so the data and its metadata cannot drift out of
 * step. All 46 rows of {@code list-options.h} are present, in the same order, with the same
 * type and default; {@link #ordinal()} therefore equals the C {@code OPT_xxx} index.
 *
 * <p><b>Why the description and default live on the constant:</b> options are surfaced in
 * menus and persisted in preferences, so every option needs its label, its grouping
 * ({@link PlayerOptionTypes} — interface / birth / cheat / score / special) and the value a
 * fresh game starts with. Bundling that triple onto the constant keeps it authoritative in
 * one place. {@code OP_none} is the index-0 placeholder mirroring the C {@code OPT_none}
 * sentinel; the C {@code OPT_MAX} terminator is not needed because {@code values().length}
 * supplies it.
 *
 * <p><b>Known differences from C:</b>
 * <ul>
 *   <li>The constants are prefixed {@code OP_} where C uses {@code OPT_}, and C's separate
 *       name string ({@code #a} in the macro, the text written to the savefile and to
 *       preference files) is not stored: it is the constant's {@link #name()} without the
 *       {@code OP_} prefix.</li>
 *   <li>Descriptions use Oxford spelling ("flavours", "Colour:", "multi-coloured") where C
 *       uses US ("flavors", "Color:", "multi-colored"). "Center map continuously" is
 *       unchanged from C.</li>
 *   <li>{@link PlayerOptionTypes} declares its constants in a different order from the C
 *       {@code OP_INTERFACE .. OP_SPECIAL} enum, so its ordinals do not match the C values.
 *       Nothing in this enum depends on them.</li>
 * </ul>
 *
 * <p>The 46 constants are not documented individually: each is a verbatim row of
 * {@code list-options.h}, and the row is its documentation.
 *
 * <p>Class coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public enum PlayerOptionEnum {
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
    OP_show_flavors("Show flavours in object descriptions",
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
    OP_view_yellow_light("Colour: Illuminate torchlight in yellow",
            PlayerOptionTypes.INTERFACE, false),
    OP_animate_flicker("Colour: Shimmer multi-coloured things",
            PlayerOptionTypes.INTERFACE, false),
    OP_center_player("Center map continuously",
            PlayerOptionTypes.INTERFACE, false),
    OP_purple_uniques("Colour: Show unique monsters in purple",
            PlayerOptionTypes.INTERFACE, false),
    OP_auto_more("Automatically clear '-more-' prompts",
            PlayerOptionTypes.INTERFACE, false),
    OP_hp_changes_color("Colour: Player colour indicates % hit points",
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
    OP_birth_force_descend("Force player descent (never make up stairs)",
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
    OP_birth_know_flavors("Know all flavours on birth",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_levels_persist("Persistent levels (experimental)",
            PlayerOptionTypes.BIRTH, false),
    OP_birth_percent_damage("To-damage is a percentage of dice (experimental)",
            PlayerOptionTypes.BIRTH, false);

    /**
     * Menu label shown to the player (C: the {@code description} member of {@code struct
     * option_entry} in {@code option.c}, the second macro argument in {@code list-options.h}). Oxford spelling,
     * so it can differ from the C text in "flavours" and "colour".
     *
     * <p>Field coded before 261009, commented in full on 261009.
     */
    private final String description;
    /**
     * Which option group this belongs to, controlling where and whether it is shown (C: the
     * {@code type} member, the {@code OP_INTERFACE} .. {@code OP_SPECIAL} value in the third
     * macro argument).
     *
     * <p>Field coded before 261009, commented in full on 261009.
     */
    private final PlayerOptionTypes playerOptionType;
    /**
     * The default value applied at birth and on a reset-to-defaults (C: the {@code normal}
     * member, the fourth macro argument). Not the current value: that lives per player, in
     * {@link uk.co.jackoftradesltd.middle.player.PlayerOptions}.
     *
     * <p>Field coded before 261009, commented in full on 261009.
     */
    private final boolean normal;

    /**
     * Binds an option to its display text, category and default state.
     *
     * <p>Constructor coded before 261009, commented in full on 261009.
     *
     * @param description      the menu label
     * @param playerOptionType the option's category
     * @param normal           the default value used for a new character
     */
    private PlayerOptionEnum(String description, PlayerOptionTypes playerOptionType, boolean normal) {
        this.description = description;
        this.playerOptionType = playerOptionType;
        this.normal = normal;
    }

    /**
     * Returns the menu label for this option (C: {@code option_desc()}).
     *
     * <p>Method getDescription coded before 261009, commented in full on 261009.
     *
     * @return the label, empty for {@link #OP_none}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the group this option belongs to (C: {@code option_type()}).
     *
     * <p>Method getPlayerOptionType coded before 261009, commented in full on 261009.
     *
     * @return the option's category
     */
    public PlayerOptionTypes getPlayerOptionType() {
        return playerOptionType;
    }

    /**
     * Returns the default value for this option (the {@code normal} member in C's
     * {@code options[]} table, copied into the player's options by {@code options_init_defaults()}
     * in {@code option.c}).
     *
     * <p>Method isNormal coded before 261009, commented in full on 261009.
     *
     * @return {@code true} if the option starts switched on
     */
    public boolean isNormal() {
        return normal;
    }

    /**
     * Tests whether this is one of the {@code cheat_} options. Equivalent to C's static
     * {@code option_is_cheat()} in {@code option.c}, which is {@code option_type(opt) == OP_CHEAT}.
     *
     * <p>Method isCheat coded before 261009, commented in full on 261009.
     *
     * @return {@code true} if the category is {@link PlayerOptionTypes#CHEAT}
     */
    public boolean isCheat() {
        return playerOptionType == PlayerOptionTypes.CHEAT;
    }
}
