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

package uk.co.jackoftrades.backend.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A thin wrapper around a single filesystem directory that snapshots its
 * contents on construction and lets callers pull file names out one at a time.
 * <p>
 * This is the Java port of the directory-handle abstraction in the original C
 * source ({@code ang_dir} / {@code my_dopen}/{@code my_dread} in
 * {@code src/z-file.c}), which Angband uses to enumerate data files without
 * exposing the platform's native directory APIs. Sub-directories are kept in
 * the same list but tagged with a {@code "DIR:"} prefix so the simple
 * {@link #read()} iterator can skip over them.
 *
 * @author Rowan Crowther
 */
public class AngDir {
    /**
     * Path of the directory this handle was opened on.
     *
     * @author Rowan Crowther
     */
    private final Path directoryPath;
    /**
     * Snapshot of the directory's entries taken at construction time. Plain
     * files are stored by name; sub-directories are stored prefixed with
     * {@code "DIR:" + pathSeparator} so they can be told apart from files.
     *
     * @author Rowan Crowther
     */
    private final List<String> dirFiles;
    /**
     * Name of the first plain (non-directory) file found, or empty if none.
     *
     * @author Rowan Crowther
     */
    private String firstFileName;
    /**
     * True when the very first entry encountered was a sub-directory.
     *
     * @author Rowan Crowther
     */
    private boolean firstFileIsDirectory;
    /**
     * When true, iteration is intended to yield files only (not directories).
     *
     * @author Rowan Crowther
     */
    private boolean onlyFiles;
    /**
     * Platform path separator, used to build/detect the {@code "DIR:"} tag.
     *
     * @author Rowan Crowther
     */
    private final char pathSeparator = File.pathSeparatorChar;

    /**
     * Constructor
     * @param directoryName the name of the directory we are dealing with
     */
    public AngDir(String directoryName) {
        directoryPath = Paths.get(directoryName);
        firstFileIsDirectory = false;
        onlyFiles = true;
        dirFiles = new ArrayList<>();
        int count = 0;

        try (
            DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    dirFiles.add(path.getFileName().toString());
                } else if (Files.isDirectory(path)) {
                    if (count == 0) firstFileIsDirectory = true;
                    dirFiles.add("DIR:" + pathSeparator + path.getFileName().toString());
                }
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        firstFileName = "";

        for (String filename : dirFiles) {
            if (!filename.startsWith("DIR:" + pathSeparator) && firstFileName.isEmpty()) {
                firstFileName = filename;
            }
        }
    }

    /**
     * Change the value of onlyFiles, and return the old value
     * @param newValue the value to change onlyFiles to
     * @return the old value of onlyFiles
     */
    public boolean alterOnlyFiles(boolean newValue) {
        boolean oldValue = onlyFiles;
        onlyFiles = newValue;
        return oldValue;
    }

    /**
     * Get the first filename in the array of filenames and
     * remove it from the list
     * @return A string containing the first file name in the directory
     */
    public String read() {
        for (String path : dirFiles) {
            if (!path.startsWith("DIR:" + pathSeparator)) {
                dirFiles.remove(path);
                return path;
            }
        }

        return "";
    }
}