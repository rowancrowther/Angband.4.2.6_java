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

package uk.co.jackoftradesltd.backend.io.savefiles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.backend.io.AngDir;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Enumerates the savefiles available to the current player, one call to
 * {@link #gotSavefile()} at a time.
 * <p>
 * This is the Java port of {@code struct savefile_getter_impl} together with
 * {@code got_savefile()} from the original C source ({@code src/ui-game.c}).
 * The C original threads its state through an opaque {@code savefile_getter}
 * pointer that a caller re-passes into {@code got_savefile()} on every call,
 * with {@code *pg == NULL} meaning "start a fresh enumeration"; this class
 * folds that into ordinary object state instead &mdash; construction is the
 * "start from scratch" branch, and every field the C struct carries
 * ({@code d}, {@code details}, {@code have_details}, {@code have_savedir})
 * has a matching field here. The one addition, {@link #directoryExhausted},
 * has no field of its own in C: the original tells "never opened" and "ran
 * out of entries" apart by which branch of {@code got_savefile()} it is in
 * when it finds {@code (*pg)->d == NULL} &mdash; the {@code *pg == NULL}
 * set-up branch for the former ({@code ui-game.c:1191-1200}), the {@code
 * else} branch for the latter ({@code ui-game.c:1211-1215}) &mdash; whereas
 * this class reuses {@link #directory} to record both causes of "nothing
 * left to read from" and so needs a separate flag to tell them apart.
 * <p>
 * Outstanding: {@code got_savefile_dir()}, {@code get_savefile_details()},
 * and {@code cleanup_savefile_getter()} ({@code ui-game.c:1262-1265,
 * 1275-1278, 1284-1294}) are not yet ported to this class.
 * <p>
 * Class SavefileGetterImpl coded on 260915, commented in full on 260915.
 *
 * @author Rowan Crowther
 */
public class SavefileGetterImpl {
    private static final Logger logger = LogManager.getLogger(SavefileGetterImpl.class);

    /**
     * Handle to the savefile directory, opened once in the constructor and
     * read one entry at a time by {@link #gotSavefile()}. Corresponds to
     * {@code d} in {@code struct savefile_getter_impl} ({@code ui-game.c}).
     * Set back to {@code null} once the directory is drained, mirroring the
     * C original's {@code (*pg)->d = NULL} ({@code ui-game.c:1252}); {@link
     * #directoryExhausted} is what then distinguishes that from never
     * having opened a directory at all.
     * <p>
     * Field directory coded on 260915, commented in full on 260915.
     */
    private AngDir directory;
    /**
     * The details of the savefile most recently found by {@link
     * #gotSavefile()}, meaningful only while {@link #haveDetails} is {@code
     * true}. Corresponds to {@code details} in {@code struct
     * savefile_getter_impl}.
     * <p>
     * Field details coded on 260915, commented in full on 260915.
     */
    private SavefileDetails details;
    /**
     * True once {@link #gotSavefile()} has populated {@link #details} for a
     * savefile it found. Corresponds to {@code have_details} in {@code
     * struct savefile_getter_impl}.
     * <p>
     * Field haveDetails coded on 260915, commented in full on 260915.
     */
    private boolean haveDetails;
    /**
     * True if the savefile directory was opened successfully, regardless of
     * whether it has since been drained. Corresponds to {@code have_savedir}
     * in {@code struct savefile_getter_impl}, which the C original sets
     * only inside the successful-open branch of {@code got_savefile()}
     * ({@code ui-game.c:1201}) and never clears afterwards.
     * <p>
     * Field haveSaveDir coded on 260915, commented in full on 260915.
     */
    private boolean haveSaveDir;
    /**
     * True once {@link #gotSavefile()} has read every entry in {@link
     * #directory} and closed it. Has no counterpart field in the C original;
     * see the class Javadoc for why this class needs one where C does not.
     * <p>
     * Field directoryExhausted coded on 260915, commented in full on 260915.
     */
    private boolean directoryExhausted;

    /**
     * Opens the savefile directory and starts a fresh enumeration, the Java
     * equivalent of the C original's {@code *pg == NULL} set-up branch
     * inside {@code got_savefile()} ({@code ui-game.c:1191-1210}). If the
     * directory cannot be opened, {@link #directory} is left {@code null}
     * and {@link #haveSaveDir} {@code false}, matching the C original
     * leaving {@code have_savedir} at its zero-initialised value when
     * {@code my_dopen()} fails ({@code ui-game.c:1198-1201}); {@link
     * #gotSavefile()} reports that failure on its first call rather than
     * here, since the C original has no separate construction step to
     * report it from. The {@code SETGID} player-prefix set-up in the C
     * original ({@code ui-game.c:1205-1210}) is not ported, as this
     * platform has no equivalent multi-user savefile-directory convention
     * to guard against.
     * <p>
     * Function SavefileGetterImpl coded on 260915, commented in full on
     * 260915.
     */
    public SavefileGetterImpl() {
        directoryExhausted = false;
        directory = AngDir.angDirFactory(AngbandDirs.ANGBAND_DIRS.SAVE.getPath());
        haveSaveDir = directory != null;
        details = new SavefileDetails();
    }

    /**
     * Advances the enumeration by one savefile, the Java equivalent of the
     * C original's {@code got_savefile()} ({@code ui-game.c:1187-1256}).
     * <p>
     * The C original tells apart the two reasons {@code (*pg)->d} can be
     * {@code NULL} on entry &mdash; the directory was never opened, or a
     * previous call already drained it &mdash; by which branch it is in:
     * the {@code *pg == NULL} branch for the former ({@code
     * ui-game.c:1191-1200}), the {@code assert(!(*pg)->have_details)}
     * branch for the latter ({@code ui-game.c:1211-1215}). This class folds
     * both causes into the one {@link #directory} field, so it draws the
     * same distinction with {@link #directoryExhausted} instead: {@link
     * #directory} {@code null} with {@link #directoryExhausted} still
     * {@code false} is reported with a logged error, matching the C
     * original's immediate failure return; {@link #directory} {@code null}
     * with {@link #directoryExhausted} {@code true} returns {@code false}
     * without logging, matching the C original's assert-guarded return for
     * an enumeration that has already finished.
     * <p>
     * Past those guards, this reads one directory entry at a time
     * (mirroring the C original's {@code while (1)} loop over {@code
     * my_dread()}, {@code ui-game.c:1218-1249}): an entry populates {@link
     * #details} and returns {@code true} immediately, without looking at
     * any further entries; running out closes {@link #directory}, sets it
     * back to {@code null}, and marks {@link #directoryExhausted}, mirroring
     * the C original's {@code my_dclose()}/{@code (*pg)->d = NULL} cleanup
     * ({@code ui-game.c:1251-1255}).
     * <p>
     * Function gotSavefile coded on 260915, commented in full on 260915.
     *
     * @return {@code true} if another savefile was found, in which case
     * {@link #details} now describes it; {@code false} if the
     * directory could not be opened or has been fully enumerated
     */
    public boolean gotSavefile() {
        if (directory == null && !directoryExhausted) {
            String message = "SavefileGetterImpl found with details for a null directory.";
            logger.error(message);
            return false;
        }
        if (directoryExhausted)
            return false;

        String fname = "";

        while (true) {
            fname = directory.read();
            boolean noEntry = (fname.isEmpty());

            if (noEntry) {
                directoryExhausted = true;
                break;
            }

            Path path = Paths.get(AngbandDirs.ANGBAND_DIRS.SAVE.getPath() + fname);
            String desc = Savefile.savefileGetDescription(path);
            details.setFileName(fname);
            details.setDescription(desc);
            haveDetails = true;
            return true;
        }

        directory.close();
        directory = null;
        haveDetails = false;
        return false;
    }
}
