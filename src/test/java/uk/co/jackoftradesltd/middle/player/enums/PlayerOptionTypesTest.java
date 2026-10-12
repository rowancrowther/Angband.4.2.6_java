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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerOptionTypes} against {@code option.h}, {@code option.c} and the {@code type}
 * column of {@code list-options.h}. The expected values are typed out from the C source: the
 * enum values {@code OP_INTERFACE .. OP_SPECIAL}, the macros {@code OPT_PAGE_MAX} and
 * {@code OPT_PAGE_BIRTH}, the words {@code option_type_name()} returns, and the option names
 * each type collects when {@code init_options()} builds {@code option_page}.
 *
 * <p>The C numeric order is load-bearing (the type value is the options-menu page number), so
 * several tests pin {@link PlayerOptionTypes#ordinal()} to the C value.
 *
 * <p>Test class written 261012.
 */
class PlayerOptionTypesTest {

    /** {@code OPT_PAGE_MAX} in option.h: defined as {@code OP_SCORE}. */
    private static final int OPT_PAGE_MAX = 3;

    /** {@code OPT_PAGE_BIRTH} in option.h. */
    private static final int OPT_PAGE_BIRTH = 1;

    /** {@code OP_MAX} in option.h. */
    private static final int OP_MAX = 5;

    /**
     * The option names {@code list-options.h} gives each type, in table order, indexed by the C
     * type value (interface 0, birth 1, cheat 2, score 3, special 4).
     */
    private static final String[][] C_OPTIONS_BY_TYPE = {
            {"rogue_like_commands", "autoexplore_commands", "use_sound", "show_damage",
                    "use_old_target", "pickup_always", "pickup_inven", "show_flavors",
                    "show_target", "highlight_player", "disturb_near", "solid_walls",
                    "hybrid_walls", "view_yellow_light", "animate_flicker", "center_player",
                    "purple_uniques", "auto_more", "hp_changes_color", "mouse_movement",
                    "notify_recharge", "effective_speed"},
            {"birth_randarts", "birth_connect_stairs", "birth_force_descend", "birth_no_recall",
                    "birth_no_artifacts", "birth_stacking", "birth_lose_arts", "birth_feelings",
                    "birth_no_selling", "birth_start_kit", "birth_ai_learn", "birth_know_runes",
                    "birth_know_flavors", "birth_levels_persist", "birth_percent_damage"},
            {"cheat_hear", "cheat_room", "cheat_xtra", "cheat_live"},
            {"score_hear", "score_room", "score_xtra", "score_live"},
            {"none"}
    };

    /** What {@code init_options()} puts in {@code option_page[page]}, by option name. */
    private static List<String> optionPage(int page) {
        List<String> names = new ArrayList<>();
        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            if (option.getPlayerOptionType().ordinal() == page) {
                names.add(option.name().substring("OP_".length()));
            }
        }
        return names;
    }

    @Test
    @DisplayName("there are OP_MAX (5) types")
    void typeCount() {
        assertEquals(OP_MAX, PlayerOptionTypes.values().length);
    }

    @Test
    @DisplayName("ordinals equal the C values OP_INTERFACE=0 .. OP_SPECIAL=4")
    void ordinalsMatchC() {
        assertEquals(0, PlayerOptionTypes.INTERFACE.ordinal());
        assertEquals(1, PlayerOptionTypes.BIRTH.ordinal());
        assertEquals(2, PlayerOptionTypes.CHEAT.ordinal());
        assertEquals(3, PlayerOptionTypes.SCORE.ordinal());
        assertEquals(4, PlayerOptionTypes.SPECIAL.ordinal());
    }

    @Test
    @DisplayName("names are the lower-case words option_type_name() returns")
    void names() {
        assertEquals("interface", PlayerOptionTypes.INTERFACE.getName());
        assertEquals("birth", PlayerOptionTypes.BIRTH.getName());
        assertEquals("cheat", PlayerOptionTypes.CHEAT.getName());
        assertEquals("score", PlayerOptionTypes.SCORE.getName());
        assertEquals("special", PlayerOptionTypes.SPECIAL.getName());
    }

    @Test
    @DisplayName("names are distinct, non-empty and never C's \"unknown\" fallback")
    void namesDistinct() {
        Set<String> seen = new HashSet<>();
        for (PlayerOptionTypes type : PlayerOptionTypes.values()) {
            assertFalse(type.getName().isEmpty(), type.name());
            assertNotEquals("unknown", type.getName(), type.name());
            assertEquals(type.getName().toLowerCase(), type.getName(), type.name());
            assertTrue(seen.add(type.getName()), "duplicate name " + type.getName());
        }
    }

    @Test
    @DisplayName("OPT_PAGE_MAX is OP_SCORE: exactly interface, birth and cheat have a page")
    void pageTypes() {
        assertEquals(OPT_PAGE_MAX, PlayerOptionTypes.SCORE.ordinal());
        List<PlayerOptionTypes> paged = new ArrayList<>();
        for (PlayerOptionTypes type : PlayerOptionTypes.values()) {
            if (type.ordinal() < OPT_PAGE_MAX) {
                paged.add(type);
            }
        }
        assertEquals(List.of(PlayerOptionTypes.INTERFACE, PlayerOptionTypes.BIRTH,
                PlayerOptionTypes.CHEAT), paged);
    }

    @Test
    @DisplayName("OPT_PAGE_BIRTH is the page of the birth type")
    void birthPage() {
        assertEquals(OPT_PAGE_BIRTH, PlayerOptionTypes.BIRTH.ordinal());
    }

    @Test
    @DisplayName("option_page[0] holds the 22 interface options, in table order")
    void interfacePage() {
        assertEquals(List.of(C_OPTIONS_BY_TYPE[0]), optionPage(0));
    }

    @Test
    @DisplayName("option_page[1] holds the 15 birth options, in table order")
    void birthPageContents() {
        assertEquals(List.of(C_OPTIONS_BY_TYPE[1]), optionPage(OPT_PAGE_BIRTH));
    }

    @Test
    @DisplayName("option_page[2] holds the 4 cheat options, in table order")
    void cheatPage() {
        assertEquals(List.of(C_OPTIONS_BY_TYPE[2]), optionPage(2));
    }

    @Test
    @DisplayName("score and special options are not on any page; their groups match the table")
    void unpagedTypes() {
        assertEquals(List.of(C_OPTIONS_BY_TYPE[3]), optionPage(PlayerOptionTypes.SCORE.ordinal()));
        assertEquals(List.of(C_OPTIONS_BY_TYPE[4]), optionPage(PlayerOptionTypes.SPECIAL.ordinal()));
        assertTrue(PlayerOptionTypes.SCORE.ordinal() >= OPT_PAGE_MAX);
        assertTrue(PlayerOptionTypes.SPECIAL.ordinal() >= OPT_PAGE_MAX);
    }

    @Test
    @DisplayName("the five groups together cover every option exactly once (OPT_MAX = 46)")
    void groupsCoverEveryOption() {
        int total = 0;
        for (String[] group : C_OPTIONS_BY_TYPE) {
            total += group.length;
        }
        assertEquals(PlayerOptionEnum.values().length, total);
        assertEquals(46, total);
    }
}
