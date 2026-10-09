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

package uk.co.jackoftradesltd.middle.game.globals.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.parser.ErrorParsing;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.backend.parser.TerrainReader;
import uk.co.jackoftradesltd.backend.parser.TrapReader;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.TrapKind;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;

import java.io.IOException;

/**
 * Startup loader for the terrain slice: parses the terrain-domain gamedata files
 * ({@code terrain.txt}, {@code trap.txt}) and populates
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry} through its setters.
 *
 * <p>This is the write side of the terrain slice, paired with {@code TerrainRegistry} (the read
 * side). Its loaders are invoked by {@code GameConstants.init()}; features and traps are
 * independent of each other. It was split out of {@code GameConstants} as one domain slice of the
 * loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class TerrainDataLoader {
    private static final Logger logger = LogManager.getLogger(TerrainDataLoader.class);

    /**
     * Load the trap kinds from {@code trap.txt} into {@link TerrainRegistry}.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the records that did
     * assemble are registered, per the partial-results contract: a single unusable record costs that
     * record, not the whole file. The one exception is the kind at index 0. {@code lookup_trap()} in
     * {@code trap.c} never returns the first kind, which in {@code trap.txt} is {@code no trap}, and a
     * dropped record takes no index, so a dropped {@code no trap} would hand index 0 to the next kind
     * and {@code lookupTrap} would skip it. The load therefore stops, with a fatal log and a
     * {@link RuntimeException}, if the assembled list is empty or its first kind is not named
     * {@code no trap}. The registry is left as it was. C has no such check: it quits on any bad record.
     *
     * <p>Function loadTraps coded before 261009, index-0 guard added on 261009, commented in full on 261009.
     *
     * @throws IOException      if {@code trap.txt} cannot be read
     * @throws RuntimeException if no kinds assembled or the first one is not {@code no trap}
     */
    public static void loadTraps() throws IOException {
        TrapReader parser = new TrapReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "trap.txt";

        try {
            ParseResult<TrapKind> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);
            
            if (result.items().isEmpty() 
                    || !"no trap".equals(result.items().getFirst().getTrapKindName())) {
                // Trap "no trap" not found at index 0, crash out of the game
                logger.fatal("Trap 'no trap' not found at traps index 0. Unable to continue");
                throw new RuntimeException("Trap 'no trap' not found at traps index 0.");
            }

            TerrainRegistry.setTrapInfo(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Terrain Feature information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadTerrainFeatures() throws IOException {
        TerrainReader parser = new TerrainReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "terrain.txt";

        try {
            ParseResult<Feature> results = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, results, logger);

            TerrainRegistry.setFeatures(results.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }
}
