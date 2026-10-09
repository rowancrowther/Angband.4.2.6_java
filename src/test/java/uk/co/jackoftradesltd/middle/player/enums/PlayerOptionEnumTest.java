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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerOptionEnum} against the rows of C's {@code list-options.h}. The expected
 * table below is typed out from that file, in C order, with Oxford spelling in the descriptions
 * ("flavours", "Colour:", "coloured") where C uses US spelling; it is deliberately not derived from
 * the Java enum.
 *
 * <p>Test class written 261009.
 */
class PlayerOptionEnumTest {

    /** One row of {@code list-options.h}: name (without prefix), description, type, normal. */
    private record Row(String name, String description, PlayerOptionTypes type, boolean normal) {
    }

    private static final PlayerOptionTypes SPECIAL = PlayerOptionTypes.SPECIAL;
    private static final PlayerOptionTypes INTERFACE = PlayerOptionTypes.INTERFACE;
    private static final PlayerOptionTypes CHEAT = PlayerOptionTypes.CHEAT;
    private static final PlayerOptionTypes SCORE = PlayerOptionTypes.SCORE;
    private static final PlayerOptionTypes BIRTH = PlayerOptionTypes.BIRTH;

    /** The 46 rows of {@code list-options.h}, in file order (the C {@code OPT_xxx} index order). */
    private static final Row[] C_TABLE = {
            new Row("none", "", SPECIAL, false),
            new Row("rogue_like_commands", "Use the roguelike command keyset", INTERFACE, false),
            new Row("autoexplore_commands", "Use autoexplore commands", INTERFACE, false),
            new Row("use_sound", "Use sound", INTERFACE, false),
            new Row("show_damage", "Show damage player deals to monsters", INTERFACE, false),
            new Row("use_old_target", "Use old target by default", INTERFACE, false),
            new Row("pickup_always", "Always pickup items", INTERFACE, false),
            new Row("pickup_inven", "Always pickup items matching inventory", INTERFACE, true),
            new Row("show_flavors", "Show flavours in object descriptions", INTERFACE, false),
            new Row("show_target", "Highlight target with cursor", INTERFACE, true),
            new Row("highlight_player", "Highlight player with cursor between turns", INTERFACE, false),
            new Row("disturb_near", "Disturb whenever viewable monster moves", INTERFACE, true),
            new Row("solid_walls", "Show walls as solid blocks", INTERFACE, false),
            new Row("hybrid_walls", "Show walls with shaded background", INTERFACE, false),
            new Row("view_yellow_light", "Colour: Illuminate torchlight in yellow", INTERFACE, false),
            new Row("animate_flicker", "Colour: Shimmer multi-coloured things", INTERFACE, false),
            new Row("center_player", "Center map continuously", INTERFACE, false),
            new Row("purple_uniques", "Colour: Show unique monsters in purple", INTERFACE, false),
            new Row("auto_more", "Automatically clear '-more-' prompts", INTERFACE, false),
            new Row("hp_changes_color", "Colour: Player colour indicates % hit points", INTERFACE, true),
            new Row("mouse_movement", "Allow mouse clicks to move the player", INTERFACE, true),
            new Row("notify_recharge", "Notify on object recharge", INTERFACE, false),
            new Row("effective_speed", "Show effective speed as multiplier", INTERFACE, false),
            new Row("cheat_hear", "Cheat: Peek into monster creation", CHEAT, false),
            new Row("score_hear", "Score: Peek into monster creation", SCORE, false),
            new Row("cheat_room", "Cheat: Peek into dungeon creation", CHEAT, false),
            new Row("score_room", "Score: Peek into dungeon creation", SCORE, false),
            new Row("cheat_xtra", "Cheat: Peek into something else", CHEAT, false),
            new Row("score_xtra", "Score: Peek into something else", SCORE, false),
            new Row("cheat_live", "Cheat: Allow player to avoid death", CHEAT, false),
            new Row("score_live", "Score: Allow player to avoid death", SCORE, false),
            new Row("birth_randarts", "Generate a new, random artifact set", BIRTH, false),
            new Row("birth_connect_stairs", "Generate connected stairs", BIRTH, true),
            new Row("birth_force_descend", "Force player descent (never make up stairs)", BIRTH, false),
            new Row("birth_no_recall", "Word of Recall has no effect", BIRTH, false),
            new Row("birth_no_artifacts", "Restrict creation of artifacts", BIRTH, false),
            new Row("birth_stacking", "Stack objects on the floor", BIRTH, true),
            new Row("birth_lose_arts", "Lose artifacts when leaving level", BIRTH, false),
            new Row("birth_feelings", "Show level feelings", BIRTH, true),
            new Row("birth_no_selling", "Increase gold drops but disable selling", BIRTH, true),
            new Row("birth_start_kit", "Start with a kit of useful gear", BIRTH, true),
            new Row("birth_ai_learn", "Monsters learn from their mistakes", BIRTH, true),
            new Row("birth_know_runes", "Know all runes on birth", BIRTH, false),
            new Row("birth_know_flavors", "Know all flavours on birth", BIRTH, false),
            new Row("birth_levels_persist", "Persistent levels (experimental)", BIRTH, false),
            new Row("birth_percent_damage", "To-damage is a percentage of dice (experimental)", BIRTH, false),
    };

    @Test
    @DisplayName("Java has exactly the 46 rows of list-options.h, no more, no fewer")
    void rowCount() {
        assertEquals(46, C_TABLE.length, "test table itself must hold every C row");
        assertEquals(C_TABLE.length, PlayerOptionEnum.values().length);
    }

    @Test
    @DisplayName("every constant sits at its C index and is named OP_ + the C name")
    void orderAndNames() {
        PlayerOptionEnum[] actual = PlayerOptionEnum.values();
        for (int i = 0; i < C_TABLE.length; i++) {
            assertEquals("OP_" + C_TABLE[i].name(), actual[i].name(), "index " + i);
            assertEquals(i, actual[i].ordinal());
        }
    }

    @Test
    @DisplayName("descriptions, types and defaults match list-options.h row for row")
    void attributesMatchC() {
        PlayerOptionEnum[] actual = PlayerOptionEnum.values();
        for (int i = 0; i < C_TABLE.length; i++) {
            Row row = C_TABLE[i];
            assertEquals(row.description(), actual[i].getDescription(), row.name() + " description");
            assertEquals(row.type(), actual[i].getPlayerOptionType(), row.name() + " type");
            assertEquals(row.normal(), actual[i].isNormal(), row.name() + " normal");
        }
    }

    @Test
    @DisplayName("OP_none is the SPECIAL, empty, off placeholder at index 0")
    void noneSentinel() {
        assertEquals(0, PlayerOptionEnum.OP_none.ordinal());
        assertEquals("", PlayerOptionEnum.OP_none.getDescription());
        assertEquals(PlayerOptionTypes.SPECIAL, PlayerOptionEnum.OP_none.getPlayerOptionType());
        assertFalse(PlayerOptionEnum.OP_none.isNormal());
    }

    @Test
    @DisplayName("exactly the eleven options default to on")
    void defaultOnSet() {
        int on = 0;
        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            if (option.isNormal()) {
                on++;
            }
        }
        // pickup_inven, show_target, disturb_near, hp_changes_color, mouse_movement (5 interface)
        // birth_connect_stairs, birth_stacking, birth_feelings, birth_no_selling,
        // birth_start_kit, birth_ai_learn (6 birth)
        assertEquals(11, on);
    }

    @Test
    @DisplayName("isCheat is true for the four cheat_ options and nothing else (score_ is not a cheat)")
    void isCheatMatchesOptionIsCheat() {
        int cheats = 0;
        for (int i = 0; i < C_TABLE.length; i++) {
            boolean expected = C_TABLE[i].type() == PlayerOptionTypes.CHEAT;
            assertEquals(expected, PlayerOptionEnum.values()[i].isCheat(), C_TABLE[i].name());
            if (expected) {
                cheats++;
            }
        }
        assertEquals(4, cheats);
        assertTrue(PlayerOptionEnum.OP_cheat_live.isCheat());
        assertFalse(PlayerOptionEnum.OP_score_live.isCheat());
    }

    @Test
    @DisplayName("each cheat option is immediately followed by its score option")
    void cheatFollowedByScore() {
        // list-options.h: "Cheat options need to be followed by corresponding score options"
        PlayerOptionEnum[] all = PlayerOptionEnum.values();
        for (int i = 0; i < all.length; i++) {
            if (all[i].isCheat()) {
                assertEquals(PlayerOptionTypes.SCORE, all[i + 1].getPlayerOptionType(), all[i].name());
                assertEquals(all[i].name().replace("cheat_", "score_"), all[i + 1].name());
            }
        }
    }

    @Test
    @DisplayName("per-type counts: 22 interface, 4 cheat, 4 score, 15 birth, 1 special")
    void typeCounts() {
        int[] counts = new int[PlayerOptionTypes.values().length];
        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            counts[option.getPlayerOptionType().ordinal()]++;
        }
        assertEquals(22, counts[PlayerOptionTypes.INTERFACE.ordinal()]);
        assertEquals(4, counts[PlayerOptionTypes.CHEAT.ordinal()]);
        assertEquals(4, counts[PlayerOptionTypes.SCORE.ordinal()]);
        assertEquals(15, counts[PlayerOptionTypes.BIRTH.ordinal()]);
        assertEquals(1, counts[PlayerOptionTypes.SPECIAL.ordinal()]);
    }

    @Test
    @DisplayName("PlayerOptionTypes names match C's option_type_name()")
    void typeNames() {
        assertEquals("interface", PlayerOptionTypes.INTERFACE.getName());
        assertEquals("birth", PlayerOptionTypes.BIRTH.getName());
        assertEquals("cheat", PlayerOptionTypes.CHEAT.getName());
        assertEquals("score", PlayerOptionTypes.SCORE.getName());
        assertEquals("special", PlayerOptionTypes.SPECIAL.getName());
    }
}
