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

package uk.co.jackoftrades.middle.game;

public class Angband {
    /**
     * The game's name.
     *
     * @author Rowan Crowther
     */
    public static final String versionName = "Angband";
    /**
     * The game's version number.
     *
     * @author Rowan Crowther
     */
    public static final String versionString = "4.2.6";

    /**
     * Combined build identifier (name + version).
     *
     * @author Rowan Crowther
     */
    public static final String buildId = versionName + " " + versionString;
    /**
     * Build version (same as {@link #versionString}).
     *
     * @author Rowan Crowther
     */
    public static final String buildVer = versionString;

    /**
     * The full copyright and licence notice shown to the player.
     *
     * @author Rowan Crowther
     */
    public static final String copyright =
            """
                    Copyright (c) 1987-2022 Angband contributors.
                    
                    This work is free software; you can redistribute it and/or modify it
                    under the terms of either:
                    
                    a) the GNU General Public License as published by the Free Software
                       Foundation, version 2, or
                    
                    b) the Angband licence:
                       This software may be copied and distributed for educational, research,
                       and not for profit purposes provided that this copyright and statement
                       are included in all such copies.  Other copyrights may also apply.
                    
                    Java code copyright (c) 2026 Rowan Crowther""";
}
