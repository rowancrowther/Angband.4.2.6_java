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
 * The categories a {@link PlayerOptionEnum} can belong to, controlling how and where an
 * option is presented and how it is treated by birth and scoring.
 *
 * <p>Ports the anonymous enum {@code OP_INTERFACE .. OP_SPECIAL} in {@code option.h}, together
 * with the name lookup {@code option_type_name()} in {@code option.c}. Every
 * {@link PlayerOptionEnum} constant carries exactly one of these types, copied from the
 * {@code type} column of {@code list-options.h}: 22 interface, 15 birth, 4 cheat, 4 score and
 * 1 special.
 *
 * <p><b>The order is load-bearing, so do not reorder the constants.</b> C uses the numeric
 * value of the type as the options-menu page number: {@code init_options()} in {@code option.c}
 * fills {@code option_page[page]} with the options whose type equals {@code page}, the macro
 * {@code OPT_PAGE_MAX} is defined as {@code OP_SCORE} so that only the types below it
 * (interface, birth, cheat) get a page, and {@code OPT_PAGE_BIRTH} is the literal 1. The
 * constants are declared in the C order, so {@link #ordinal()} equals the C value
 * (interface 0, birth 1, cheat 2, score 3, special 4).
 *
 * <p><b>Known differences from C:</b>
 * <ul>
 *   <li>{@code OP_MAX} is not stored; {@code values().length} supplies it.</li>
 *   <li>{@code OPT_PAGE_MAX} and {@code OPT_PAGE_BIRTH} have no Java equivalent yet. They belong
 *       to the options-menu and {@code init_options()} port, where an explicit list of the page
 *       types is clearer than relying on the declaration order.</li>
 *   <li>The {@code default} arm of {@code option_type_name()}, which returns {@code "unknown"}
 *       for an out-of-range value, is not ported: a closed enum cannot hold one.</li>
 * </ul>
 *
 * <p>Class coded before 261009, reordered into the C order on 261012, commented in full on
 * 261012.
 *
 * @author Rowan Crowther
 */
public enum PlayerOptionTypes {
    /**
     * Interface and display preferences. Ports C's {@code OP_INTERFACE} (0).
     *
     * <p>22 options, shown on options-menu page 0. Their customized defaults are saved to and
     * restored from {@code customized_interface_options.txt}.
     *
     * <p>Constant INTERFACE coded before 261009, commented in full on 261012.
     */
    INTERFACE("interface"),
    /**
     * Birth options, chosen at character creation and fixed for that game. Ports C's
     * {@code OP_BIRTH} (1).
     *
     * <p>15 options, shown on options-menu page 1 ({@code OPT_PAGE_BIRTH}), which is read-only
     * once the character exists. Their customized defaults are saved to and restored from
     * {@code customized_birth_options.txt}.
     *
     * <p>Constant BIRTH coded before 261009, commented in full on 261012.
     */
    BIRTH("birth"),
    /**
     * The "Cheat: Peek into ..." and "Cheat: Allow player to avoid death" options. Ports C's
     * {@code OP_CHEAT} (2).
     *
     * <p>4 options, shown on options-menu page 2. Turning one on through {@code option_set()}
     * in {@code option.c} also turns on the {@link #SCORE} option that follows it in the table.
     * {@code options_init_cheat()} clears both.
     *
     * <p>Constant CHEAT coded before 261009, commented in full on 261012.
     */
    CHEAT("cheat"),
    /**
     * The "Score: ..." options that pair with the cheat options. Ports C's {@code OP_SCORE} (3).
     *
     * <p>4 options, with no options-menu page of their own because {@code OPT_PAGE_MAX} is
     * {@code OP_SCORE}. In play one goes on as a side effect of its cheat option, and the
     * player cannot turn it off from the menu. In {@code score.c}, {@code enter_score()} refuses
     * to register a score while any of them is on.
     *
     * <p>Constant SCORE coded before 261009, commented in full on 261012.
     */
    SCORE("score"),
    /**
     * Internal options that are never shown to the player. Ports C's {@code OP_SPECIAL} (4).
     *
     * <p>The only member is the {@code OP_none} placeholder at option index 0, and it has no
     * options-menu page.
     *
     * <p>Constant SPECIAL coded before 261009, commented in full on 261012.
     */
    SPECIAL("special");

    /**
     * The short lower-case word for this type, as returned by C's {@code option_type_name()}.
     * C builds the {@code customized_<name>_options.txt} file names and the "Save as default
     * <name> options" menu label from it.
     *
     * <p>Field name coded before 261009, commented in full on 261012.
     */
    private final String name;

    /**
     * Binds a constant to its {@code option_type_name()} word.
     *
     * <p>Constructor coded before 261009, commented in full on 261012.
     *
     * @param name the lower-case word C returns for this type
     */
    PlayerOptionTypes(String name) {
        this.name = name;
    }

    /**
     * Returns the short lower-case word for this type, the port of C's
     * {@code option_type_name()} for an in-range value: {@code "interface"}, {@code "birth"},
     * {@code "cheat"}, {@code "score"} or {@code "special"}.
     *
     * <p>Function getName coded before 261009, commented in full on 261012.
     *
     * @return the type's name word, never {@code null}
     */
    public String getName() {
        return name;
    }
}
