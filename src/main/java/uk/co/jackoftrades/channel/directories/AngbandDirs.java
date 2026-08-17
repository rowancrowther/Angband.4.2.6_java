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

package uk.co.jackoftrades.channel.directories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

/**
 * Where the game's files live: one entry per data directory, each overridable from the command
 * line. This is the port of C's {@code ANGBAND_DIR_*} globals ({@code [C] src/init.c}), which are
 * mutable buffers {@code main()} rewrites from {@code -d} before {@code init_angband()} opens
 * anything.
 *
 * <p><b>The enum is the store.</b> {@link ANGBAND_DIRS} holds the live path for each directory and
 * is what the rest of the port reads; the private constants below only supply the start-up
 * defaults, copied into the constants once when the enum initialises. Nothing else may hold a
 * path, and that is the whole design: an earlier version had the mapping written in three places -
 * these fields, the enum, and a {@code switch} in {@link #setDirectory} - and produced two bugs of
 * the same shape. One name was spelled {@code "archives"} in the switch and {@code "archive"} in
 * the enum, so that override was accepted and silently dropped; and because the enum copied the
 * fields at class-init and {@code setDirectory} wrote the fields, an override never reached the
 * only copy anyone read. Both vanish once there is one place to write and one place to read.
 *
 * <p>Paths are absolute, built from the working directory, and every one ends in a separator so
 * callers can concatenate a file name directly.
 *
 * @author Rowan Crowther
 */
public class AngbandDirs {
    /**
     * Logger for rejected directory names.
     */
    private static final Logger logger = LogManager.getLogger(AngbandDirs.class);

    // The directory structure of Angband - OS neutral.
    // Note, if the user wants to save on a custom area, then we will have to amend the function BASE_DIR
    // to return that value. That's a future issue

    /**
     * The directory the game was launched from, which every default path is built on.
     */
    public static String BASE_DIR = System.getProperty("user.dir");
    /*
     * Start-up defaults, and nothing more. Each is read exactly once - by the ANGBAND_DIRS
     * constant that names it - and is final because an override goes to the enum, never here.
     * The two-tier layout mirrors the tree on disk: lib/ holds what ships with the game,
     * lib/user/ what a player accumulates.
     */

    private static final String libPath = File.separator + "lib" + File.separator;
    private static final String ANGBAND_DIR_ICONS = BASE_DIR + libPath + "icons" + File.separator;
    private static final String ANGBAND_DIR_SOUNDS = BASE_DIR + libPath + "sounds" + File.separator;
    private static final String ANGBAND_DIR_TILES = BASE_DIR + libPath + "tiles" + File.separator;
    private static final String ANGBAND_DIR_FONTS = BASE_DIR + libPath + "fonts" + File.separator;
    private static final String ANGBAND_DIR_SCREENS = BASE_DIR + libPath + "screens" + File.separator;
    private static final String ANGBAND_DIR_HELP = BASE_DIR + libPath + "help" + File.separator;
    private static final String ANGBAND_DIR_GAMEDATA = BASE_DIR + libPath + "gamedata" + File.separator;
    private static final String configPath = libPath + "config" + File.separator;
    private static final String ANGBAND_DIR_CUSTOMIZE = BASE_DIR + configPath + "customize" + File.separator;
    private static final String userPath = libPath + "user" + File.separator;
    private static final String ANGBAND_DIR_USER = BASE_DIR + userPath;
    private static final String ANGBAND_DIR_PANIC = BASE_DIR + userPath + "panic" + File.separator;
    private static final String ANGBAND_DIR_SAVE = BASE_DIR + userPath + "save" + File.separator;
    private static final String ANGBAND_DIR_SCORES = BASE_DIR + userPath + "scores" + File.separator;
    private static final String ANGBAND_DIR_ARCHIVE = BASE_DIR + userPath + "archives" + File.separator;

    /**
     * Point a directory somewhere else for the rest of the run - the {@code -d<dir>=<path>}
     * switch, and the port of C rewriting an {@code ANGBAND_DIR_*} buffer in {@code main()}.
     *
     * <p>Looks the name up and writes through the constant it finds, so the override lands in the
     * one place {@link ANGBAND_DIRS#getPath()} reads from. There is no second table to keep in
     * step: an unknown name simply fails to resolve.
     *
     * <p>Overriding is only safe before anything has opened a file under the old path, which in
     * practice means during argument parsing.
     *
     * <p>An unknown name is fatal, which is blunt for a class this far from the command line: it
     * logs and kills the JVM rather than letting the caller decide, and so cannot be tested. The
     * caller already validates, so nothing reaches this in practice - see
     * {@code Main.main}, which checks {@link ANGBAND_DIRS#contains} first and reports its own
     * error.
     *
     * @param name      the directory's data-file name, as it appears in {@link ANGBAND_DIRS}
     * @param directory the absolute path to use instead
     */
    public static void setDirectory(String name, String directory) {
        ANGBAND_DIRS dir = ANGBAND_DIRS.getDirectory(name);

        if (dir == null) {
            logger.error("Invalid directory name: {}", name);
            System.exit(-1);
        } else dir.setPath(directory);
    }

    /**
     * The game's directories: one constant each, pairing the name the command line uses with the
     * path currently in force.
     *
     * <p>Unusually for an enum these constants are mutable - {@link #setPath} rewrites a path in
     * place. That is deliberate, and faithful: C's {@code ANGBAND_DIR_*} are mutable globals for
     * exactly this reason, and holding the path anywhere else would recreate the two-copies
     * problem described on the enclosing class. The identity of a constant never changes; only
     * where it points.
     *
     * <p>The {@code name} is the data-file spelling, not the constant's, and the two differ where
     * C's history left them differing - {@code PREF} is written {@code "pref"} but points at the
     * customise directory. Always match on {@link #getName()}, never {@link #name()}.
     *
     * @author Rowan Crowther
     */
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

        /**
         * The name this directory goes by on the command line and in data files. Fixed.
         */
        private final String name;
        /**
         * Where this directory currently points. Not final - {@code -d} replaces it during
         * argument parsing, and this field is the only copy anyone reads.
         */
        private String path;

        /**
         * @param name the data-file name for this directory
         * @param path the default path, taken from the enclosing class's constants
         */
        ANGBAND_DIRS(final String name, final String path) {
            this.name = name;
            this.path = path;
        }

        /**
         * @return the data-file name for this directory, which is what the command line matches on
         */
        public String getName() {
            return name;
        }

        /**
         * @return the path in force now, with a trailing separator; overrides included
         */
        public String getPath() {
            return path;
        }

        /**
         * Repoint this directory. Called by {@link AngbandDirs#setDirectory} for {@code -d}, and
         * by tests restoring what they changed - the constants are process-wide, so a test that
         * moves one has to put it back.
         *
         * @param path the absolute path to use, which should end in a separator like the defaults
         */
        public void setPath(String path) {
            this.path = path;
        }

        /**
         * Find the constant whose {@link #getName()} matches, or {@code null} if none does.
         *
         * @param name the data-file name to look for
         * @return the matching constant, or {@code null} if the name is not one of these
         */
        public static ANGBAND_DIRS getDirectory(@NotNull String name) {
            return Arrays.stream(ANGBAND_DIRS.values())
                    .filter(a -> a.getName().equals(name)).findFirst().orElse(null);
        }

        /**
         * Whether {@code dirName} names one of these directories - the check the command line
         * makes before accepting a {@code -d} override.
         *
         * <p>Answers the same question as {@link #getDirectory}, by the same rule. Delegating to
         * it would keep a single implementation.
         *
         * @param dirName the data-file name to test
         * @return {@code true} if some constant uses that name
         */
        public static boolean contains(String dirName) {
            for (ANGBAND_DIRS dir : ANGBAND_DIRS.values()) {
                if (dir.getName().equals(dirName))
                    return true;
            }

            return false;
        }
    }
}
