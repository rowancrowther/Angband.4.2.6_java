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

package uk.co.jackoftradesltd.channel;

import uk.co.jackoftradesltd.channel.directories.AngbandDirs;

import java.util.List;

/**
 * The command line, parsed: everything {@code -} switches said, in one immutable value.
 * It corresponds to the block of {@code arg_*} globals C keeps in {@code src/main.c}
 * ({@code arg_force_name}, {@code arg_force_roguelike} and friends), which C's {@code main()}
 * fills in as it walks {@code argv} and every later stage reads.
 *
 * <p>Lives outside Main on purpose. It is consumed by the front end and, later, by the
 * game loop, and nesting it in the entry point would make both of them depend on the class that
 * happens to hold {@code main()}. As a top-level record it is data that anyone may read and
 * nobody may change.
 *
 * <p>Deliberately holds no behaviour. An earlier version carried the quit hook as an eighth
 * component; that moved out because a collaborator among the values stops this being a value -
 * it cannot be compared or logged as one, and only the front end could ever have reached the
 * hook, while C's {@code quit_aux} is reachable from everywhere. It also once carried the
 * {@code -d} overrides as a map, which became redundant when
 * {@link AngbandDirs#setDirectory} started applying them
 * as they are parsed.
 *
 * <p>Not everything here is wanted at the same moment, which is worth knowing before threading it
 * further in. {@code requestGraphicsMode} is a display decision the front end needs as it builds;
 * the savefile group - {@code selectSavefile}, {@code startNewCharacter},
 * {@code resurrectDeadCharacter}, {@code useSpecificCharacter} - is not read until the game
 * starts, and {@code selectSavefile} positively requires a display to already exist, since it
 * draws a menu.
 *
 * @param selectSavefile         {@code -c}: pick the savefile from a menu at start-up, overriding
 *                               {@code startNewCharacter}
 * @param startNewCharacter      {@code -n}: roll a new character rather than loading the existing
 *                               savefile
 * @param resurrectDeadCharacter {@code -w}: bring a dead character back, marking the savefile
 * @param requestGraphicsMode    {@code -g}: ask for tiles instead of plain text
 * @param useSpecificCharacter   {@code -u<who>}: the savefile to use; empty when unset
 * @param useSoundModule         {@code -s<mod>}: the sound backend to use. Always empty - the
 *                               option is not parsed yet
 * @param useModuleSystem        {@code -m<sys>}: the front-end modules to use. Always empty; C
 *                               picks a front end from this list, which this port does not need
 *                               while Swing is the only one
 * @author Rowan Crowther
 */
public record StartupOptions(boolean selectSavefile,
                             boolean startNewCharacter,
                             boolean resurrectDeadCharacter,
                             boolean requestGraphicsMode,
                             String useSpecificCharacter,
                             String useSoundModule,
                             List<String> useModuleSystem) {
}
