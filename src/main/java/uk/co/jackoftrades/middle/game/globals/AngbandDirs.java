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

package uk.co.jackoftrades.middle.game.globals;

import java.io.File;

public class AngbandDirs {
    // The directory structure of Angband - OS neutral.
    // Note, if the user wants to save on a custom area, then we will have to amend the function BASE_DIR
    // to return that value. That's a future issue
    static final String BASE_DIR = System.getProperty("user.dir");
    private static final String libPath = File.separator + "lib" + File.separator;
    public static final String ANGBAND_DIR_ICONS = BASE_DIR + libPath + "icons" + File.separator;
    public static final String ANGBAND_DIR_SOUNDS = BASE_DIR + libPath + "sounds" + File.separator;
    public static final String ANGBAND_DIR_TILES = BASE_DIR + libPath + "tiles" + File.separator;
    public static final String ANGBAND_DIR_FONTS = BASE_DIR + libPath + "fonts" + File.separator;
    public static final String ANGBAND_DIR_SCREENS = BASE_DIR + libPath + "screens" + File.separator;
    public static final String ANGBAND_DIR_HELP = BASE_DIR + libPath + "help" + File.separator;
    public static final String ANGBAND_DIR_GAMEDATA = BASE_DIR + libPath + "gamedata" + File.separator;
    private static final String configPath = libPath + "config" + File.separator;
    public static final String ANGBAND_DIR_CUSTOMIZE = BASE_DIR + configPath + "customize" + File.separator;
    private static final String userPath = libPath + "user" + File.separator;
    public static final String ANGBAND_DIR_USER = BASE_DIR + userPath;
    public static final String ANGBAND_DIR_PANIC = BASE_DIR + userPath + "panic" + File.separator;
    public static final String ANGBAND_DIR_SAVE = BASE_DIR + userPath + "save" + File.separator;
    public static final String ANGBAND_DIR_SCORES = BASE_DIR + userPath + "scores" + File.separator;
    public static final String ANGBAND_DIR_ARCHIVE = BASE_DIR + userPath + "archives" + File.separator;

    public enum ANGBAND_DIRS {
        SCORES("scores", ANGBAND_DIR_SCORES),
        GAMEDATA("gamedata", ANGBAND_DIR_GAMEDATA),
        SCREENS("screens", ANGBAND_DIR_SCREENS),
        HELP("help", ANGBAND_DIR_HELP),
        PREF("pref", ANGBAND_DIR_CUSTOMIZE),
        FONTS("fonts", ANGBAND_DIR_FONTS),
        TILES("tiles", ANGBAND_DIR_TILES),
        SOUNDS("sounds", ANGBAND_DIR_SOUNDS),
        ICONS("icons", ANGBAND_DIR_ICONS),
        USER("user", ANGBAND_DIR_USER),
        SAVE("save", ANGBAND_DIR_SAVE),
        PANIC("panic", ANGBAND_DIR_PANIC),
        ARCHIVE("archive", ANGBAND_DIR_ARCHIVE);

        private final String name;
        private final String path;

        ANGBAND_DIRS(final String name, final String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public static boolean contains(String dirName) {
            for (ANGBAND_DIRS dir : ANGBAND_DIRS.values()) {
                if (dir.getName().equals(dirName))
                    return true;
            }

            return false;
        }
    }
}
