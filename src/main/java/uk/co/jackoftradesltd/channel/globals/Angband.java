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

package uk.co.jackoftradesltd.channel.globals;

public class Angband {
    /**
     * The game's name - C's {@code VERSION_NAME} ({@code [C] src/buildid.h}).
     *
     * <p>Field versionName coded before 260915, commented in full on 260915.
     */
    public static final String versionName = "Angband";
    /**
     * The game's version number - C's {@code VERSION_STRING} ({@code [C] src/buildid.c}), which
     * this port always resolves to the fallback literal since neither of C's build-time overrides
     * ({@code HAVE_VERSION_H}, {@code BUILD_ID}) is ported.
     *
     * <p>Field versionString coded before 260915, commented in full on 260915.
     */
    public static final String versionString = "4.2.6";

    /**
     * Combined build identifier (name + version) - C's {@code buildid} ({@code [C] src/buildid.c}).
     *
     * <p>Field buildId coded before 260915, commented in full on 260915.
     */
    public static final String buildId = versionName + " " + versionString;
    /**
     * Build version (same as {@link #versionString}) - C's {@code buildver}
     * ({@code [C] src/buildid.c}).
     *
     * <p>Field buildVer coded before 260915, commented in full on 260915.
     */
    public static final String buildVer = versionString;

    /**
     * The full copyright and licence notice shown to the player - C's {@code copyright}
     * ({@code [C] src/buildid.c}), with one line appended crediting the Java port.
     *
     * <p>Field copyright coded before 260915, commented in full on 260915.
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
