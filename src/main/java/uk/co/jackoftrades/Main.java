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

package uk.co.jackoftrades;

import uk.co.jackoftrades.frontend.Frontend;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.gameengine.GameRunner;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private record StartupOptions(boolean selectSavefile,
                                  boolean startNewCharacter,
                                  boolean resurrectDeadCharacter,
                                  boolean requestGraphicsMode,
                                  String useSpecificCharacter,
                                  Map<String, String> overrideDirectoryPath,
                                  String useSoundModule,
                                  List<String> useModuleSystem) {
    }

    /**
     *
     * @throws Exception if something goes wrong
     */

    public void start() throws Exception {
        GameEngine gameEngine = GameEngine.getGame();
    }

    public static void main(String[] args) throws IOException {
        // Declare the start option defaults
        boolean selectSavefile = false;
        boolean startNewCharacter = false;
        boolean resurrectDeadCharacter = false;
        boolean requestGraphicsMode = false;
        String useSpecificCharacter = "";
        Map<String, String> overrideDirectoryPath = new HashMap<>();

        for (String arg : args) {
            if (arg.length() < 2 || arg.charAt(0) != '-') arg = "-h";
            switch (arg.charAt(1)) {
                case 'c' -> selectSavefile = true;
                case 'n' -> startNewCharacter = true;
                case 'l' -> listSaves();
                case 'w' -> resurrectDeadCharacter = true;
                case 'g' -> requestGraphicsMode = true;
                case 'u' -> {
                    useSpecificCharacter = arg.substring(2);
                    if (!useSpecificCharacter.isEmpty()) printUsage();
                }
                case 'd' -> {
                    String dirString = arg.substring(2);
                    String[] dirs = dirString.split("=");
                    // Check that the save file is correctly named
                    if (dirs.length != 2) {
                        System.out.println("Error: invalid directory path incorrect number of parameters supplied");
                        System.exit(1);
                    }
                    if (!AngbandDirs.ANGBAND_DIRS.contains(dirs[0])) {
                        System.out.println("Error: invalid directory path unknown directory name");
                        System.exit(1);
                    }
                    if (!Paths.get(dirs[1]).toFile().exists()) {
                        System.out.println("Error: invalid directory path does not exist");
                        System.exit(1);
                    }
                    overrideDirectoryPath.put(dirs[0], dirs[1]);
                }

                default -> printUsage();
            }
        }

        StartupOptions options = new StartupOptions(selectSavefile, startNewCharacter,
                resurrectDeadCharacter, requestGraphicsMode, useSpecificCharacter,
                overrideDirectoryPath, "", new ArrayList<>());

        SwingUtilities.invokeLater(startFrontend);
        System.out.println("Do other stuff");
    }

    private static void printUsage() {
        System.out.println("Usage: angband [options] [-- subopts]");
        System.out.println("  -c             Select savefile with a menu; overrides -n");
        System.out.println("  -n             Start a new character (WARNING: overwrites default savefile without -u)");
        System.out.println("  -l             Lists all savefiles you can play");
        System.out.println("  -w             Resurrect dead character (marks savefile)");
        System.out.println("  -g             Request graphics mode");
        System.out.println("  -u<who>        Use your <who> savefile");
        System.out.println("  -d<dir>=<path> Override a specific directory with <path>. <path> can be:");

        for (AngbandDirs.ANGBAND_DIRS dir : AngbandDirs.ANGBAND_DIRS.values()) {
            System.out.println(String.format("    %s (default is %s)", dir.getName(), dir.getPath()));
        }

        System.out.println("                 Multiple -d options are allowed.");
        //      System.out.println("  -s<mod>        Use sound module <sys>:");
        //      printSoundHelp();
        //      System.out.println("  -m<sys>        Use module <sys>, where <sys> can be:");

        System.exit(0);
    }

    static Runnable startFrontend = new Runnable() {
        @Override
        public void run() {
            GameRunner gameRunner = new GameRunner();
            Frontend frontend = new Frontend(gameRunner);
            frontend.init();
        }
    };

    private static void listSaves() throws IOException {
        // Stub function TODO: Implement
        System.exit(0);
    }
}