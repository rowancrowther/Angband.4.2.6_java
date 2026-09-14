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

import uk.co.jackoftradesltd.backend.io.enums.FileModeEnum;
import uk.co.jackoftradesltd.backend.io.enums.FileTypeEnum;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * High-level handle for a single game file, pairing a fixed access {@link
 * FileModeEnum} with the lower-level {@link FileHandler} that performs the
 * actual stream I/O.
 * <p>
 * This is the Java port of the {@code ang_file} abstraction from the original C
 * source ({@code src/z-file.c}). It deliberately keeps a narrow, intention-named
 * API ({@link #getLine()}, {@link #putLine(String)}, {@link #readChar()}, …) so
 * the rest of the game can read and write data files without touching
 * {@code java.nio} directly. Most methods lazily open the underlying handler in
 * the appropriate mode if it is not already open, mirroring the C code's habit
 * of treating an {@code ang_file} as "open on demand".
 * <p>
 * Class AngFile coded before 260914, commented in full on 260914.
 *
 * @author Rowan Crowther
 */
public class AngFile {
    /**
     * The access mode (read/write/append) this file was created for. Every
     * lazy-open call site in this class opens with this mode rather than a
     * hardcoded one, so a file created for {@link FileModeEnum#MODE_APPEND}
     * still appends on its first write even if {@link #open(FileTypeEnum)}
     * was never called explicitly.
     * <p>
     * Field mode coded before 260914, commented in full on 260914.
     */
    private final FileModeEnum mode;
    /**
     * The lower-level handler that owns the actual input/output streams.
     * <p>
     * Field fileHandler coded before 260914, commented in full on 260914.
     */
    private final FileHandler fileHandler;

    /**
     * Constructor
     * <p>
     * Function AngFile coded before 260914, commented in full on 260914.
     * @param filename the name of the file
     * @param mode the mode the file is to be dealt with, i.e. Write only...
     */
    public AngFile(String filename, FileModeEnum mode) {
        this.mode = mode;
        fileHandler = new FileHandler(filename);
    }

    /**
     * Open up the file associated with this AngFile.
     * <p>
     * If the file already exists, it is opened directly in {@link #mode}. If it
     * doesn't exist, it is created and then opened only when {@link #mode} isn't
     * {@link FileModeEnum#MODE_READ} — mirroring the C original ({@code
     * src/z-file.c}), where {@code fopen(buf, "rb")} fails outright on a missing
     * file while {@code "wb"}/{@code "a+"} create one. Opening a nonexistent file
     * for reading therefore fails here too, rather than silently creating an
     * empty one and reporting success.
     * <p>
     * Function open coded before 260914, updated on 260914 to gate file creation
     * on mode so MODE_READ fails on a missing file instead of creating it,
     * commented in full on 260914.
     * @param type the FileTypeEnum that this file should
     *             be opened as
     * @return true if the file was opened successfully,
     *         false otherwise.
     */
    public boolean open(FileTypeEnum type) {
        if (fileHandler.fileExists()) {
            return fileHandler.open(mode, type);
        } else if (mode != FileModeEnum.MODE_READ) {
            fileHandler.createFile(mode, type);
            return fileHandler.open(mode, type);
        }
        return false;
    }

    /**
     * Close the file - not needed in Java as the file is only open when a stream is attached to it. Pass this
     * through to the FileHandler to get it to close any open streams.
     * <p>
     * Function close coded before 260914, commented in full on 260914.
     * @return True
     */
    public boolean close() {
        return fileHandler.close();
    }

    /**
     * Skip a particular number of bytes (in a binary file)
     * <p>
     * Function fileSkip coded before 260914, commented in full on 260914.
     * @param bytesToSkip The number of bytes to skip
     * @return True if the skip was successful and didn't go past the end of the file, false otherwise.
     */
    public boolean fileSkip(long bytesToSkip) {
        return fileHandler.skipBytes(bytesToSkip);
    }

    /**
     * Gets the next character in the stream of this file, opening it for reading
     * first if it isn't already open.
     * <p>
     * Returns an empty {@link Optional} when that lazy open fails, rather than a
     * sentinel {@code int}. A raw {@code int} can't tell a failed open apart from
     * a genuine read result, since a successful read can itself legitimately be
     * {@code -1} (end of file, from {@link FileHandler#readCharacter()}) or
     * {@code 0} (a NUL byte) — either of which would collide with a would-be
     * failure sentinel using that same value.
     * <p>
     * Function readChar coded before 260914, updated on 260914 to return {@code
     * Optional<Integer>} instead of {@code int} so a failed open no longer
     * collides with a valid -1 (EOF) or 0 (NUL) read result, commented in full on
     * 260914.
     * @return An Optional containing the next character (as an int) in the stream
     * of this file, or an empty Optional if the file could not be opened
     */
    public Optional<Integer> readChar() {
        boolean open = true;

        if (!fileHandler.isOpen())
            open = fileHandler.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT);

        if (open)
            return Optional.of(fileHandler.readCharacter());

        return Optional.empty();
    }

    /**
     * Determines whether this AngFile is open or not
     * <p>
     * Function isOpen coded before 260914, commented in full on 260914.
     * @return Whether the FileHandler attached to this AngFile is open or not
     */
    public boolean isOpen() {
        return fileHandler.isOpen();
    }

    /**
     * Attempt to write a single character to a file, opening it for writing
     * first — in this file's own {@link #mode}, not a hardcoded one — if it
     * isn't already open. Refuses outright, without touching the file, when this
     * AngFile was created with {@link FileModeEnum#MODE_READ}.
     * <p>
     * If the file doesn't already exist, it is created first — mirroring C's
     * {@code fopen("wb")}/{@code "a+"} creating a missing file — so this works
     * on a brand-new AngFile without an explicit {@link #open(FileTypeEnum)}
     * call first.
     * <p>
     * Function writeChar coded before 260914, updated on 260914 to open with
     * this file's own mode instead of a hardcoded MODE_WRITE, to refuse on
     * MODE_READ, and to create the file first when it doesn't already exist,
     * commented in full on 260914.
     * @param c the character to write
     * @return true if the character was written, false if this file is
     * read-only or an exception occurred
     */
    public boolean writeChar(int c) {
        if (mode == FileModeEnum.MODE_READ)
            return false;

        if (!isOpen()) {
            if (!fileHandler.fileExists())
                fileHandler.createFile(mode, FileTypeEnum.FTYPE_TEXT);

            fileHandler.open(mode, FileTypeEnum.FTYPE_TEXT);
        }

        return fileHandler.writeCharacter(c);
    }

    /**
     * Read a string of a given size from the file, opening it for reading first
     * if it isn't already open.
     * <p>
     * Function read coded before 260914, updated on 260914 to check the lazy
     * open's result and return null when it fails instead of always attempting
     * the read, commented in full on 260914.
     * @param size The number of bytes to read
     * @return A string containing the next size of bytes from the file, or
     * a null value if the file could not be opened or an Exception occurred.
     */
    public String read(int size) {
        boolean open = true;

        if (!isOpen())
            open = fileHandler.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT);

        if (open)
            return fileHandler.read(size);

        return null;
    }

    /**
     * Append a string to a file, opening it for writing first — in this file's
     * own {@link #mode} — if it isn't already open. Refuses outright, without
     * touching the file, when this AngFile was created with {@link
     * FileModeEnum#MODE_READ}.
     * <p>
     * If the file doesn't already exist, it is created first — mirroring C's
     * {@code fopen("wb")}/{@code "a+"} creating a missing file — so this works
     * on a brand-new AngFile without an explicit {@link #open(FileTypeEnum)}
     * call first.
     * <p>
     * Function write coded before 260914, updated on 260914 to open with this
     * file's own mode instead of relying on FileHandler's default, to refuse
     * on MODE_READ, and to create the file first when it doesn't already exist,
     * commented in full on 260914.
     * @param toWrite The string to append
     * @return true if the string was written, false if this file is read-only or
     * an error occurred
     */
    public boolean write(String toWrite) {
        if (mode == FileModeEnum.MODE_READ)
            return false;

        if (!fileHandler.isOpen()) {
            if (!fileHandler.fileExists())
                fileHandler.createFile(mode, FileTypeEnum.FTYPE_TEXT);

            fileHandler.open(mode, FileTypeEnum.FTYPE_TEXT);
        }

        return fileHandler.write(toWrite);
    }

    /**
     * Gets a line of text upto a new line/carriage return from the input stream
     * and returns it, opening the file for reading first if it isn't already
     * open.
     * <p>
     * Function getLine coded before 260914, updated on 260914 to check the lazy
     * open's result and return null when it fails instead of always attempting
     * the read, commented in full on 260914.
     * @return The line of text that was read, or null if the file could not be
     * opened or an Exception occurred
     */
    public String getLine() {
        boolean open = true;

        if (!fileHandler.isOpen())
            open = fileHandler.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT);

        if (open)
            return fileHandler.getLine();

        return null;
    }

    /**
     * Write a line to this file, opening it for writing first — in this file's
     * own {@link #mode} — if it isn't already open. Refuses outright, without
     * touching the file, when this AngFile was created with {@link
     * FileModeEnum#MODE_READ}.
     * <p>
     * If the file doesn't already exist, it is created first — mirroring C's
     * {@code fopen("wb")}/{@code "a+"} creating a missing file — so this works
     * on a brand-new AngFile without an explicit {@link #open(FileTypeEnum)}
     * call first.
     * <p>
     * Function putLine coded before 260914, updated on 260914 to open with this
     * file's own mode, refuse on MODE_READ, and create the file first when it
     * doesn't already exist, matching {@link #write(String)}, commented in full
     * on 260914.
     * @param line the line to write
     * @return true if the line was written, false if this file is read-only or
     * an exception occurred
     */
    public boolean putLine(String line) {
        if (mode == FileModeEnum.MODE_READ)
            return false;

        if (!fileHandler.isOpen()) {
            if (!fileHandler.fileExists())
                fileHandler.createFile(mode, FileTypeEnum.FTYPE_TEXT);

            fileHandler.open(mode, FileTypeEnum.FTYPE_TEXT);
        }

        return fileHandler.putLine(line);
    }

    /**
     * Send a string to be formatted to the file outputStream
     * <p>
     * Function putFormattedLine coded before 260914, commented in full on
     * 260914.
     * @param line The line to be formatted based on the args, and then written to the outputStream
     * @param args The arguments to the format
     * @return true if the line was written, false otherwise
     */
    public boolean putFormattedLine(String line, Object...args) {
        String formattedString = String.format(line, args);
        return putLine(formattedString);
    }

    /**
     * Returns whether a given directory path exists.
     * <p>
     * Requires both {@link Files#exists} and {@link Files#isDirectory}, so a
     * path that exists but names a plain file returns false — matching the
     * original C {@code dir_exists} ({@code src/z-file.c}), which checks the
     * {@code S_IFDIR} bit from {@code stat} rather than mere existence.
     * <p>
     * Function dirExists coded before 260914, updated on 260914 to require
     * Files.isDirectory as well as Files.exists, commented in full on 260914.
     * @param path The directory path
     * @return true if the directory path exists, false otherwise
     */
    public boolean dirExists(String path) {
        return Files.exists(Paths.get(path)) && Files.isDirectory(Paths.get(path));
    }

    /**
     * Create a given directory, returning whether it was created or not
     * <p>
     * Function dirCreate coded before 260914, commented in full on 260914.
     * @param path The directory path to create, including all parent directories which
     *             are not currently created
     * @return true if the directory was created, false if an exception occurred
     */
    public boolean dirCreate(String path) {
        try {
            Files.createDirectories(Paths.get(path));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}