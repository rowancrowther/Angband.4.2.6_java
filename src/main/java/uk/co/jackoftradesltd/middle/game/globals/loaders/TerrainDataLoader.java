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
import uk.co.jackoftradesltd.channel.parser.ErrorParsing;
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
 * under {@code lib/gamedata}, log the soft errors, and install what assembled. A file that cannot
 * be read, or whose parse leaves errors and no records, stops start-up with a
 * {@link RuntimeException} naming the file, which is the port of C's {@code quit()} (a missing file
 * in {@code parse_file_quit_not_found}).
 *
 * <p>They differ from C in five ways:
 * <ul>
 *   <li>Soft errors are not fatal while records assembled. C's parser stops at the first bad line
 *   and quits; here {@code ErrorParsing.reportAndCheck} logs every error and the surviving records
 *   are installed, as the other data loaders do. Only {@code loadTraps} adds a guard of its own,
 *   described on the method, because a dropped {@code no trap} record would shift every trap index.
 *   A repeated terrain code is the exception on the terrain side: {@code TerrainFeatureAssembler}
 *   throws, as C quits, so it never reaches the soft channel.</li>
 *   <li>A terrain file with fewer than 25 usable records loads. C does too, leaving the missing
 *   {@code f_info} slot zeroed; the Java list is in file order and holds only what assembled, so
 *   {@code TerrainRegistry.lookupFeature} returns {@code null} for the missing code.</li>
 *   <li>The files are read from the gamedata directory only. C's {@code parse_file}
 *   ({@code datafile.c}) tries a customised copy in the user directory first.</li>
 *   <li>The terrain colour is checked more strictly than C: C's {@code color_text_to_attr} and
 *   {@code color_char_to_attr} turn an unknown colour into white, where the port rejects it, first in
 *   the lexer and again in {@code TerrainFeatureAssembler}. This is accepted; see
 *   {@code docs/Accepted_Deviations.md}.</li>
 *   <li>C's {@code trap_info} array is allocated one element longer than {@code trap_max} and the
 *   extra slot is left blank; the Java list holds only the real kinds, and its count is the plain
 *   size, which equals {@code z_info->trap_max}.</li>
 * </ul>
 *
 * <p>Class TerrainDataLoader coded before 261009, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
public class TerrainDataLoader {
    /**
     * Logger for both loaders: the soft errors {@code ErrorParsing.reportAndCheck} finds in each
     * file, and the fatal line written before a load failure is thrown. C has no counterpart: it
     * reports through {@code quit()} and the parser's own error text.
     *
     * <p>Field logger coded before 261009, commented in full on 261013.
     */
    private static final Logger logger = LogManager.getLogger(TerrainDataLoader.class);

    /**
     * Load the trap kinds from {@code trap.txt} into {@link TerrainRegistry}, standing in for
     * C's {@code trap_parser} ({@code init_parse_trap}, {@code run_parse_trap} and
     * {@code finish_parse_trap} in {@code init.c}).
     *
     * <p>Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the records that did
     * assemble are registered, per the partial-results contract: a single unusable record costs that
     * record, not the whole file. Two cases stop start-up instead, each logged at fatal and thrown as a
     * {@link RuntimeException} before the registry is touched: a file that cannot be read, which C
     * reaches through {@code parse_file_quit_not_found}, and a parse that leaves errors and no kinds.
     *
     * <p>The kind at index 0 is a third case. {@code lookup_trap()} in {@code trap.c} never returns the
     * first kind, which in {@code trap.txt} is {@code no trap}, and a dropped record takes no index, so a
     * dropped {@code no trap} would hand index 0 to the next kind and {@code lookupTrap} would skip it.
     * The load therefore also stops if the assembled list is empty or its first kind is not named
     * {@code no trap}. The registry is left as it was. C has no such check: it quits on any bad record.
     *
     * <p>Indexes follow {@code finish_parse_trap}, which numbers the kinds from 0 in file order, so the
     * list is installed in file order. {@code TerrainRegistry.setTrapInfo} sets the count from the list
     * size, which equals C's {@code z_info->trap_max}: C allocates one extra blank element after the
     * last kind, and the Java list leaves it out. Calling this again replaces the registry list rather
     * than appending.
     *
     * <p>Function loadTraps coded before 261009, index-0 guard added on 261009, commented in full on 261013.
     *
     * @throws RuntimeException if {@code trap.txt} cannot be read, the parse leaves errors and no kinds,
     *                          or the first assembled kind is not {@code no trap}
     */
    public static void loadTraps() {
        TrapReader parser = new TrapReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "trap.txt";

        try {
            ParseResult<TrapKind> result = parser.parseWithResults(filename);

            if (!ErrorParsing.reportAndCheck(filename, result, logger)) {
                String message = "Error while parsing " +  filename;
                logger.fatal(message);
                throw new RuntimeException(message);
            }
            
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
     * <p>Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the records that did
     * assemble are registered, per the partial-results contract: a single unusable record costs that
     * record, not the whole file. Two cases stop start-up instead, each logged at fatal and thrown as a
     * {@link RuntimeException} before the registry is touched: a file that cannot be read, which C
     * reaches through {@code parse_file_quit_not_found}, and a parse that leaves errors and no features.
     * A third comes from {@code TerrainFeatureAssembler}, which throws on a repeated {@code code:}, as
     * C's {@code parse_feat_name} quits with {@code PARSE_ERROR_REPEATED_DIRECTIVE}; nothing is
     * registered and {@code GameConstants} keeps its previous store count.
     *
     * <p>Shops are numbered from 1 in the order the features assembled, as {@code finish_parse_feat}
     * numbers them in {@code f_info} order; the two orders agree for the shipped file, which follows
     * {@code list-terrain.h}. Each feature with the {@code SHOP} flag gets the next number, and the
     * count handed out is stored through {@code GameConstants.setStoreMax}, so a shop entrance dropped
     * by the assembler is one store fewer. The numbers and the count are set after the error check
     * and before the registry is installed.
     *
     * <p>There is no check on how many features assembled. C allocates {@code f_info} with exactly
     * {@code FEAT_MAX} slots, 25 in {@code list-terrain.h}, and files each record under its
     * {@code FEAT_} code, so a record missing from the file leaves a zeroed slot and a record dropped
     * for a bad line quits the load. The Java list is in file order and holds only what assembled. A
     * short file therefore loads in both, but in Java the missing code has no entry and
     * {@code lookupFeature} returns {@code null} for it, where C hands back the zeroed slot. A record
     * dropped for a bad line does not stop the load here, as it does in C. The shipped file has all 25
     * records, {@code FEAT_NONE} included.
     *
     * <p>The colour on each {@code graphics:} line is checked more strictly than C checks it. The lexer
     * has tokens only for the colours in C's table, so an unknown colour is a hard grammar error that
     * fails the whole file and stops the load as a parse with errors and no features. If a token ever got
     * past the lexer, {@code TerrainFeatureAssembler} repeats the check and drops the record, which then
     * has no entry like any other dropped record. Calling this again replaces the registry list.
     *
     * <p>Function loadTerrainFeatures coded before 261009, commented in full on 261013.
     *
     * @throws RuntimeException if {@code terrain.txt} cannot be read, the parse leaves errors and no
     *                          features, or a {@code code:} appears twice
     */
    public static void loadTerrainFeatures() {
        TerrainReader parser = new TerrainReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "terrain.txt";

        try {
            ParseResult<Feature> results = parser.parseWithResults(filename);

            if (!ErrorParsing.reportAndCheck(filename, results, logger)) {
                String message = "Error while parsing " +  filename;
                logger.fatal(message);
                throw new RuntimeException(message);
            }
            
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
