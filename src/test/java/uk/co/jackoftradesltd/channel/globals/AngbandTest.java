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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AngbandTest {

    @Test
    void versionNameMatchesCVersionName() {
        assertEquals("Angband", Angband.versionName);
    }

    @Test
    void versionStringMatchesCFallback() {
        // C's VERSION_STRING falls back to this literal when neither HAVE_VERSION_H nor
        // BUILD_ID is defined - the only path this port implements.
        assertEquals("4.2.6", Angband.versionString);
    }

    @Test
    void buildIdIsNameAndVersionJoinedBySpace() {
        // C: const char *buildid = VERSION_NAME " " VERSION_STRING;
        assertEquals("Angband 4.2.6", Angband.buildId);
        assertEquals(Angband.versionName + " " + Angband.versionString, Angband.buildId);
    }

    @Test
    void buildVerIsVersionStringAlone() {
        // C: const char *buildver = VERSION_STRING;
        assertEquals("4.2.6", Angband.buildVer);
        assertEquals(Angband.versionString, Angband.buildVer);
    }

    @Test
    void copyrightStartsWithCCopyrightNotice() {
        // C's copyright, verbatim (buildid.c), with its trailing "\n" dropped since that
        // paragraph break is what separates it from the Java port's appended line below.
        String cCopyright =
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
                        + "   are included in all such copies.  Other copyrights may also apply.";

        assertTrue(Angband.copyright.startsWith(cCopyright));
    }

    @Test
    void copyrightAppendsJavaPortAttribution() {
        // The one line C does not have - added by this port, per Angband.java's class Javadoc.
        assertTrue(Angband.copyright.endsWith("Java code copyright (c) 2026 Rowan Crowther"));
    }

    @Test
    void copyrightHasNoTrailingContentAfterAttribution() {
        // Guards against silent drift: the attribution line is the last thing in the string.
        assertEquals(
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
                        + "Java code copyright (c) 2026 Rowan Crowther",
                Angband.copyright);
    }
}
