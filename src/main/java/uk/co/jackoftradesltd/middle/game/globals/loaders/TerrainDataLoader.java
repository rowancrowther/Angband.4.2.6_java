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
import uk.co.jackoftradesltd.backend.parser.TerrainReader;
import uk.co.jackoftradesltd.backend.parser.TrapReader;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.TrapKind;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
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
 * <p>The two loaders stand in for C's {@code feat_parser} and {@code trap_parser} in {@code init.c},
 * which {@code init_arrays()} runs. Each follows the same shape: build the reader, parse the file
 * under {@code lib/gamedata}, and install the assembled list.
 *
 * <p>Every error is fatal, as in C, where a missing file quits in
 * {@code parse_file_quit_not_found} and a bad line quits the load. There are three ways a load
 * fails, and only the first is handled here:
 * <ul>
 *   <li>an {@link IOException} from the reader is logged at {@code FATAL} and rethrown wrapped in a
 *       {@link RuntimeException} naming the file;</li>
 *   <li>a grammar or lexer error is thrown by the reader as a {@code ParseCancellationException}
 *       ({@code ParseErrors.throwIfAny});</li>
 *   <li>a record the assembler refuses is thrown as a {@code DataFileParseException}.</li>
 * </ul>
 * The last two are unchecked and pass through these loaders unlogged and unwrapped;
 * {@code GameConstants.init()} catches them and names the file. Nothing is installed from a file
 * that fails, so the registry never holds a partial list. {@code loadTraps} adds two index-0 guards
 * of its own, described on the method.
 *
 * <p>They differ from C in four ways:
 * <ul>
 *   <li>A terrain file with fewer than 25 records loads. C does too, leaving the missing
 *   {@code f_info} slot zeroed; the Java list is in file order and holds only what is in the file,
 *   so {@code TerrainRegistry.lookupFeature} returns {@code null} for the missing code.</li>
 *   <li>The files are read from the gamedata directory only. C's {@code parse_file}
 *   ({@code datafile.c}) tries a customised copy in the user directory first.</li>
 *   <li>The terrain colour is checked more strictly than C: C's {@code color_text_to_attr} and
 *   {@code color_char_to_attr} turn an unknown colour into white, where the lexer has tokens only for
 *   the colours in C's table, so an unknown colour is a grammar error that stops the load.
 *   {@code TerrainFeatureAssembler} falls back to white as C does, should a token ever get past the
 *   lexer. This is accepted; see {@code docs/Accepted_Deviations.md}.</li>
 *   <li>C's {@code trap_info} array is allocated one element longer than {@code trap_max} and the
 *   extra slot is left blank; the Java list holds only the real kinds, and its count is the plain
 *   size, which equals {@code z_info->trap_max}.</li>
 * </ul>
 *
 * <p>Class TerrainDataLoader coded before 261009, commented in full on 261013, failure paragraph
 * and colour bullet rewritten on 261104 for the removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public class TerrainDataLoader {
    /**
     * Logger for both loaders: the {@code FATAL} line written before an IO failure or a failed
     * trap index-0 guard is thrown. Grammar and assembler errors are not logged here (see the class
     * description). C has no counterpart: it reports through {@code quit()} and the parser's own
     * error text.
     *
     * <p>Field logger coded before 261009, commented in full on 261013, updated on 261104 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger(TerrainDataLoader.class);

    /**
     * Load the trap kinds from {@code trap.txt} into {@link TerrainRegistry}, standing in for
     * C's {@code trap_parser} ({@code init_parse_trap}, {@code run_parse_trap} and
     * {@code finish_parse_trap} in {@code init.c}).
     *
     * <p>Every error stops start-up before the registry is touched. A file that cannot be read,
     * which C reaches through {@code parse_file_quit_not_found}, is logged at fatal and thrown as a
     * {@link RuntimeException}; a grammar or assembler error passes through unwrapped (see the class
     * description).
     *
     * <p>The kind at index 0 is checked as well. {@code lookup_trap()} in {@code trap.c} never
     * returns the first kind, which in {@code trap.txt} is {@code no trap}, so a file that does not
     * open with {@code no trap} would hand index 0 to a real kind and {@code lookupTrap} would skip
     * it. The load therefore also stops, logged at fatal and thrown as a {@link RuntimeException}, if
     * the assembled list is empty or its first kind is not named {@code no trap}. The registry is left
     * as it was. C has no such check. With no records ever dropped, the guard now only catches a file
     * written in the wrong order, not a bad record.
     *
     * <p>Indexes follow {@code finish_parse_trap}, which numbers the kinds from 0 in file order, so the
     * list is installed in file order. {@code TerrainRegistry.setTrapInfo} sets the count from the list
     * size, which equals C's {@code z_info->trap_max}: C allocates one extra blank element after the
     * last kind, and the Java list leaves it out. Calling this again replaces the registry list rather
     * than appending.
     *
     * <p>Function loadTraps coded before 261009, index-0 guard added on 261009, commented in full on
     * 261013, updated on 261104 for the removal of the soft-error path.
     *
     * @throws RuntimeException if {@code trap.txt} cannot be read, or the list is empty or its first
     *                          kind is not {@code no trap}; or, unwrapped, if the file holds a grammar
     *                          or assembler error
     */
    public static void loadTraps() {
        TrapReader parser = new TrapReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "trap.txt";

        try {
            ParseResult<TrapKind> result = parser.parseWithResults(filename);
            
            if (result.items().isEmpty()) {
                logger.fatal("No traps found");
                throw new RuntimeException("No traps found");
            }
            
            if (!"no trap".equals(result.items().getFirst().getTrapKindName())) {
                // Trap "no trap" not found at index 0, crash out of the game
                logger.fatal("Trap 'no trap' not found at traps index 0. Unable to continue");
                throw new RuntimeException("Trap 'no trap' not found at traps index 0.");
            }

            TerrainRegistry.setTrapInfo(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the terrain features from {@code terrain.txt} into {@link TerrainRegistry}, standing in for
     * C's {@code feat_parser} ({@code init_parse_feat}, {@code run_parse_feat} and
     * {@code finish_parse_feat} in {@code init.c}). {@code finish_parse_feat} does three things, split
     * across two classes: {@code TerrainFeatureAssembler} puts the trailing space on the look strings,
     * and this method numbers the shops and sets the store count, {@code z_info->store_max}.
     *
     * <p>Every error stops start-up before the registry is touched, and {@code GameConstants} keeps
     * its previous store count. A file that cannot be read, which C reaches through
     * {@code parse_file_quit_not_found}, is logged at fatal and thrown as a {@link RuntimeException};
     * a grammar or assembler error passes through unwrapped (see the class description). One
     * assembler error is a repeated {@code code:}, as C's {@code parse_feat_name} quits with
     * {@code PARSE_ERROR_REPEATED_DIRECTIVE}.
     *
     * <p>Shops are numbered from 1 in the order the features assembled, as {@code finish_parse_feat}
     * numbers them in {@code f_info} order; the two orders agree for the shipped file, which follows
     * {@code list-terrain.h}. Each feature with the {@code SHOP} flag gets the next number, and the
     * count handed out is stored through {@code GameConstants.setStoreMax}. The numbers and the count
     * are set after a clean parse and before the registry is installed.
     *
     * <p>There is no check on how many features assembled. C allocates {@code f_info} with exactly
     * {@code FEAT_MAX} slots, 25 in {@code list-terrain.h}, and files each record under its
     * {@code FEAT_} code, so a record missing from the file leaves a zeroed slot. The Java list is in
     * file order and holds only what is in the file. A short file therefore loads in both, but in
     * Java the missing code has no entry and {@code lookupFeature} returns {@code null} for it, where
     * C hands back the zeroed slot. A bad line stops the load in both. The shipped file has all 25
     * records, {@code FEAT_NONE} included.
     *
     * <p>The colour on each {@code graphics:} line is checked more strictly than C checks it. The lexer
     * has tokens only for the colours in C's table, so an unknown colour is a grammar error that stops
     * the load. If a token ever got past the lexer, {@code TerrainFeatureAssembler} would fall back to
     * white, as C does. Calling this again replaces the registry list.
     *
     * <p>Function loadTerrainFeatures coded before 261009, commented in full on 261013, updated on
     * 261104 for the removal of the soft-error path.
     *
     * @throws RuntimeException if {@code terrain.txt} cannot be read; or, unwrapped, if the file holds
     *                          a grammar or assembler error, a repeated {@code code:} among them
     */
    public static void loadTerrainFeatures() {
        TerrainReader parser = new TerrainReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "terrain.txt";

        try {
            ParseResult<Feature> results = parser.parseWithResults(filename);
            
            int shopNum = 1;
            for (Feature feature : results.items()) {
                if (feature.isShop()) {
                    feature.setShopNum(shopNum);
                    shopNum++;
                }
            }

            GameConstants.setStoreMax(shopNum - 1);

            TerrainRegistry.setFeatures(results.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }
}
