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

package uk.co.jackoftradesltd.backend.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * A thin wrapper around a single filesystem directory handle, opened once and
 * read one entry at a time.
 * <p>
 * This is the Java port of the directory-handle abstraction in the original C
 * source ({@code ang_dir} / {@code my_dopen}/{@code alter_ang_dir_only_files}/
 * {@code my_dread} in {@code src/z-file.c}), which Angband uses to enumerate
 * data files without exposing the platform's native directory APIs. By
 * default {@link #read()} yields plain files only, skipping sub-directories,
 * the same boundary the C original draws with {@code only_files}; a caller
 * that flips that with {@link #alterOnlyFiles(boolean)} gets sub-directories
 * back too, plus the two synthetic {@code "."}/{@code ".."} entries C's
 * {@code readdir()} would otherwise contribute for the directory itself and
 * its parent.
 * <p>
 * Class AngDir coded before 260914, commented in full on 260914.
 *
 * @author Rowan Crowther
 */
public class AngDir {
    private static final Logger logger = LogManager.getLogger(AngDir.class);

    /**
     * Path of the directory this handle was opened on.
     */
    private final Path directoryPath;
    /**
     * When true, iteration is intended to yield files only (not directories).
     * <p>
     * Field onlyFiles coded before 260914, commented in full on 260914.
     */
    private boolean onlyFiles;

    /**
     * Inverse of {@link #onlyFiles}; kept as its own field, mirroring the C
     * original's {@code only_files}, rather than negating {@code onlyFiles}
     * at every use.
     * <p>
     * Field directories coded before 260914, commented in full on 260914.
     */
    private boolean directories;

    /**
     * The open handle backing {@link #iterator}, kept only so {@link #close()}
     * has something to close.
     * <p>
     * Field stream coded before 260914, commented in full on 260914.
     */
    private DirectoryStream<Path> stream;

    /**
     * Live cursor over {@link #stream}'s entries.
     * <p>
     * Field iterator coded before 260914, commented in full on 260914.
     */
    private Iterator<Path> iterator;

    /**
     * True once {@link #read()} has produced (or considered and skipped) the
     * synthetic {@code "."} entry.
     * <p>
     * Field currentRead coded on 260914, commented in full on 260914.
     */
    private boolean currentRead;

    /**
     * True once {@link #read()} has produced (or considered and skipped) the
     * synthetic {@code ".."} entry.
     * <p>
     * Field prevRead coded on 260914, commented in full on 260914.
     */
    private boolean prevRead;

    /**
     * Open the directory stream for {@code directoryName} and default to
     * files-only iteration, matching {@code my_dopen()} defaulting
     * {@code only_files} to {@code true} ({@code z-file.c}).
     *
     * @param directoryName the name of the directory we are dealing with
     * @throws IOException if the directory cannot be opened, propagated to
     *         {@link #angDirFactory(String)} which turns it into a
     *         {@code null} handle
     *
     * <p>Function AngDir (constructor) coded before 260914, commented in full on 260914.
     */
    private AngDir(String directoryName) throws IOException {
        directoryPath = Path.of(directoryName);
        onlyFiles = true;
        directories = false;
        currentRead = false;
        prevRead = false;

        stream = Files.newDirectoryStream(directoryPath);
        iterator = stream.iterator();
    }

    /**
     * Open a directory handle, the Java equivalent of the C original's
     * {@code my_dopen()}.
     *
     * @param directoryName the name of the directory to open
     * @return a handle ready for {@link #read()}, or {@code null} if the
     * directory could not be opened at all (mirroring {@code my_dopen}
     * returning {@code NULL})
     *
     * <p>Function angDirFactory coded before 260914, commented in full on 260914.
     */
    public static AngDir angDirFactory(String directoryName) {
        try {
            return new AngDir(directoryName);
        } catch (RuntimeException | IOException e) {
            return null;
        }
    }

    /**
     * Close the underlying directory stream, the Java equivalent of the C
     * original's {@code my_dclose()}. Unlike the C original, {@link #read()}
     * also calls this itself once the directory is exhausted, so callers do
     * not have to remember to close a fully-drained handle by hand.
     *
     * <p>Function close coded before 260914, commented in full on 260914.
     */
    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
            logger.error("Error while closing stream", e);
        }
    }

    /**
     * Change the value of onlyFiles, and return the old value. The Java
     * equivalent of the C original's {@code alter_ang_dir_only_files()}.
     * <p>
     * Flipping {@code newValue} to {@code false} also lets {@link #read()}
     * yield sub-directories and the synthetic {@code "."}/{@code ".."}
     * entries; back in C this is the same {@code only_files} flag that
     * {@code my_dread()} tests directly ({@code z-file.c}).
     *
     * @param newValue the value to change onlyFiles to
     * @return the old value of onlyFiles
     *
     * <p>Function alterOnlyFiles coded before 260914, commented in full on 260914.
     */
    public boolean alterOnlyFiles(boolean newValue) {
        boolean oldValue = onlyFiles;
        onlyFiles = newValue;
        directories = !newValue;
        return oldValue;
    }

    /**
     * Return the next directory entry, or {@code ""} once there are no more.
     * The Java equivalent of the C original's {@code my_dread()}, translated
     * from a {@code bool}-plus-out-parameter return into a single string,
     * with the empty string standing in for C's {@code false}.
     * <p>
     * The first two calls are special-cased to reproduce the {@code "."} and
     * {@code ".."} entries C's {@code readdir()} contributes for the current
     * and parent directory: they are returned only when {@link #onlyFiles} is
     * false, exactly mirroring how {@code my_dread()}'s {@code S_ISDIR} check
     * only lets {@code .}/{@code ..} survive when {@code only_files} is false
     * ({@code z-file.c}). Every other entry then comes from {@link #iterator}
     * directly; {@link Files#exists(Path, java.nio.file.LinkOption...)} plays
     * the role of the original's {@code stat()} guard against an entry that
     * vanished between being listed and being examined, and
     * {@link Files#isDirectory(Path, java.nio.file.LinkOption...)} plays the
     * role of {@code S_ISDIR} for every remaining entry.
     * <p>
     * Once the underlying stream is exhausted this closes it itself
     * (see {@link #close()}) before returning {@code ""}, so a caller looping
     * on {@link #read()} until it sees an empty string needs no explicit
     * clean-up call, unlike a C caller of {@code my_dread()}/{@code my_dclose()}.
     *
     * @return the next entry's bare file name, or {@code ""} when the
     *         directory is exhausted
     *
     * <p>Function read coded on 260914, commented in full on 260914.
     */
    public String read() {
        Path filePath = null;
        String path = "";

        if (!currentRead) {
            currentRead = true;

            if (directories)
                return ".";
        }

        if (!prevRead) {
            prevRead = true;

            if (directories) {
                return "..";
            }
        }

        while (iterator.hasNext()) {
            filePath = iterator.next();
            path = readFiles(filePath);

            if (!Files.exists(filePath)) {
                continue;
            }

            if (!directories && Files.isDirectory(filePath)) {
                continue;
            }

            if (path.isEmpty()) {
                continue;
            }

            return Paths.get(path).getFileName().toString();
        }

        this.close();
        return "";
    }
    
    /**
     * Resolve {@code filePath} to a bare file name, or {@code ""} if it fails
     * the same existence/directory checks {@link #read()} applies inline
     * just above the call site.
     *
     * @param filePath the candidate entry from {@link #iterator}
     * @return the entry's bare file name, or {@code ""} if it is filtered out
     *
     * <p>Function readFiles coded on 260914, commented in full on 260914.
     */
    private String readFiles(Path filePath) {
        String path = filePath.toString();

        if (!Files.exists(filePath))
            return "";

        if (!directories && Files.isDirectory(filePath))
            return "";

        return filePath.getFileName().toString();
    }
}