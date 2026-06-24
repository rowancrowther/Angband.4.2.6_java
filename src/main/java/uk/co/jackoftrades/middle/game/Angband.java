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

package uk.co.jackoftrades.middle.game;

public class Angband {
    /**
     * The game's name.
     *
     * @author ClaudeCode
     */
    public static final String VERSION_NAME = "Angband";
    /**
     * The game's version number.
     *
     * @author ClaudeCode
     */
    public static final String VERSION_STRING = "4.2.6";

    /**
     * Combined build identifier (name + version).
     *
     * @author ClaudeCode
     */
    public static final String buildId = VERSION_NAME + " " + VERSION_STRING;
    /**
     * Build version (same as {@link #VERSION_STRING}).
     *
     * @author ClaudeCode
     */
    public static final String buildVer = VERSION_STRING;

    /**
     * The full copyright and licence notice shown to the player.
     *
     * @author ClaudeCode
     */
    public static final String copyright =
            "Copyright (c) 1987-2022 Angband contributors.\n"
                    + "\n"
                    + "This work is free software; you can redistribute it and/or modify it\n"
                    + "under the terms of either:\n"
                    + "\n"
                    + "a) the GNU General Public License as published by the Free Software\n"
                    + "   Foundation, version 2, or\n"
                    + "\n"
                    + "b) the Angband licence:\n"
                    + "   This software may be copied and distributed for educational, research,\n"
                    + "   and not for profit purposes provided that this copyright and statement\n"
                    + "   are included in all such copies.  Other copyrights may also apply.\n"
                    + "\n"
                    + "Java code copyright (c) 2026 Rowan Crowther";
}
