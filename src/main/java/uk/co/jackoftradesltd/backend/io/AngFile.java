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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * High-level handle for a single game file, pairing a fixed access {@link
 * FileModeEnum} with the lower-level {@link FileHandler} that performs the
 * actual stream I/O.
 * <p>
 * This is the Java port of the {@code ang_file} abstraction from the original C
 * source ({@code z-file.c}). It deliberately keeps a narrow, intention-named
 * API ({@link #getLine()}, {@link #putLine(String)}, {@link #readChar()}, …) so
 * the rest of the game can read and write data files without touching
 * {@code java.nio} directly.
 * <p>
 * The C functions map onto the methods like this:
 * <ul>
 *   <li>{@code file_open} is {@link #open(FileTypeEnum)};</li>
 *   <li>{@code file_close} is {@link #close()};</li>
 *   <li>{@code file_skip} is {@link #fileSkip(long)};</li>
 *   <li>{@code file_readc} is {@link #readChar()};</li>
 *   <li>{@code file_writec} is {@link #writeChar(int)};</li>
 *   <li>{@code file_read} is {@link #read(int)};</li>
 *   <li>{@code file_write} is {@link #write(byte[])};</li>
 *   <li>{@code file_getl} is {@link #getLine()};</li>
 *   <li>{@code file_put} is {@link #putLine(String)};</li>
 *   <li>{@code file_putf} and {@code file_vputf} are {@link #putFormattedLine(String, Object...)};</li>
 *   <li>{@code dir_exists} is {@link #dirExists(String)};</li>
 *   <li>{@code dir_create} is {@link #dirCreate(String)}.</li>
 * </ul>
 * C hands out an {@code ang_file} only once {@code file_open} has succeeded, so
 * it has no unopened state. Java splits that in two: the constructor records the
 * name and mode, and {@link #open(FileTypeEnum)} opens the stream. The three
 * read methods ({@link #readChar()}, {@link #read(int)}, {@link #getLine()})
 * open the file for reading themselves if it is not open yet, which C never
 * does; the write methods do not, and refuse until {@link #open(FileTypeEnum)}
 * has succeeded.
 * <p>
 * The divergences from C that have been accepted (the skip rules of {@link
 * #fileSkip(long)}, the format syntax and the UTF-8 cut of {@link
 * #putFormattedLine(String, Object...)}, and the trailing-slash and permission
 * behaviour of {@link #dirCreate(String)}) are recorded in the AngFile and
 * FileHandler sections of {@code docs/Accepted_Deviations.md}. C's {@code
 * file_lock}, {@code file_unlock} and {@code file_open_hook} have no
 * counterpart here.
 * <p>
 * Class AngFile coded before 260914, commented in full on 261016.
 *
 * @author Rowan Crowther
 */
public class AngFile {
    /**
     * The access mode (read/write/append) this file was created for, fixed for
     * the life of the object. It is what {@link #open(FileTypeEnum)} opens the
     * file with, and it decides which methods work: the read methods refuse
     * unless it is {@link FileModeEnum#MODE_READ}, and the write methods refuse
     * when it is. This is the {@code mode} field of C's {@code ang_file}.
     * <p>
     * Field mode coded before 260914, commented in full on 261016.
     */
    private final FileModeEnum mode;
    /**
     * The lower-level handler that owns the actual input/output streams. It
     * stands in for the {@code FILE *fh} of C's {@code ang_file}, and is the one
     * thing that says whether the file is currently open.
     * <p>
     * Field fileHandler coded before 260914, commented in full on 261016.
     */
    private final FileHandler fileHandler;

    /**
     * Records the file name and the access mode, and builds the {@link
     * FileHandler} that will do the I/O. Nothing is opened, created or touched
     * on disk here: C's {@code file_open} opens the file in one step, but here
     * that step is {@link #open(FileTypeEnum)}, which must be called before
     * any write.
     * <p>
     * Function AngFile coded before 260914, commented in full on 261016.
     * @param filename the path of the file
     * @param mode the mode the file is to be dealt with: read, write
     *             (truncating) or append
     */
    public AngFile(String filename, FileModeEnum mode) {
        this.mode = mode;
        fileHandler = new FileHandler(filename);
    }

    /**
     * Open up the file associated with this AngFile in its own {@link #mode}.
     * <p>
     * Port of {@code file_open} in {@code z-file.c}, which returns an {@code
     * ang_file} or {@code NULL}; here the file object already exists, so
     * success or failure comes back as a boolean. The outcomes follow C's
     * {@code fopen} modes:
     * <ul>
     *   <li>{@link FileModeEnum#MODE_READ} on a missing file fails ({@code
     *       "rb"}), and nothing is created;</li>
     *   <li>{@link FileModeEnum#MODE_WRITE} creates a missing file and
     *       truncates an existing one ({@code "wb"});</li>
     *   <li>{@link FileModeEnum#MODE_APPEND} creates a missing file and keeps
     *       an existing one's contents ({@code "a+"});</li>
     *   <li>{@link FileModeEnum#MODE_WRITE} with {@link
     *       FileTypeEnum#FTYPE_SAVE} fails if the file already exists, as C's
     *       {@code O_CREAT | O_EXCL} open does, so a savefile is never
     *       overwritten. {@code type} has no other effect.</li>
     * </ul>
     * Both branches of the body hand the same call to {@link FileHandler}; the
     * existence test only decides whether a missing file in {@code MODE_READ}
     * is refused. C's {@code file_open_hook}, which C calls after opening in
     * any mode but read, is not ported.
     * <p>
     * Function open coded before 260914, updated on 260914 to gate file creation
     * on mode so MODE_READ fails on a missing file instead of creating it,
     * commented in full on 261016.
     * @param type the FileTypeEnum that this file should
     *             be opened as
     * @return true if the file was opened successfully,
     *         false otherwise.
     */
    public boolean open(FileTypeEnum type) {
        if (fileHandler.fileExists()) {
            return fileHandler.open(mode, type);
        } else if (mode != FileModeEnum.MODE_READ) {
            return fileHandler.open(mode, type);
        }
        
        return false;
    }

    /**
     * Close the file by passing the call to the {@link FileHandler}, which closes whichever stream is
     * attached. Closing a file that is not open does nothing and succeeds. Unlike C, the object is not
     * freed, so the AngFile can be opened again afterwards.
     * <p>
     * Port of {@code file_close} in {@code z-file.c}, which returns false when {@code fclose} fails and
     * otherwise frees the {@code ang_file}.
     * <p>
     * Function close coded before 260914, commented in full on 261016.
     * @return true if the streams were closed or none was open, false if closing threw an
     * {@link IOException}
     */
    public boolean close() {
        return fileHandler.close();
    }

    /**
     * Skip forward a number of bytes in the open file, without opening it first.
     * <p>
     * Port of {@code file_skip} in {@code z-file.c}, which is {@code fseek(f->fh, bytes, SEEK_CUR)}.
     * Deliberate divergence, recorded in {@code docs/Accepted_Deviations.md}: C also seeks backwards for a
     * negative count, but the read stream here cannot move backwards, so a negative count returns false and
     * leaves the position alone.
     * <p>
     * As in C, a forward skip past the end of the file succeeds, and the next read reports the end of the
     * file. This holds whether or not a byte has already been read. The skip is repeated until the whole
     * distance has been covered, because the buffered stream underneath may move fewer bytes than asked.
     * A count of zero returns true and does not move. It returns false when the file is not open for reading.
     * <p>
     * Function fileSkip coded before 260914, commented in full on 261016.
     * @param bytesToSkip The number of bytes to skip
     * @return true if the skip succeeded, including a skip that runs past the end of the file; false for a
     * negative count or when the file is not open for reading
     */
    public boolean fileSkip(long bytesToSkip) {
        return fileHandler.skipBytes(bytesToSkip);
    }

    /**
     * Gets the next character in the stream of this file, opening it for reading
     * first if it isn't already open.
     * <p>
     * Port of {@code file_readc} in {@code z-file.c}, which returns false at end of
     * file and otherwise stores the byte through an out parameter. Here the byte comes
     * back as a value from 0 to 255, and end of file is reported as {@code
     * Optional.of(-1)} rather than as a failure. An empty {@link Optional} means the
     * request could not be made at all: this AngFile was not created with {@link
     * FileModeEnum#MODE_READ}, or the lazy open failed. A raw {@code int} could not
     * tell those apart, because a successful read can itself be {@code -1} (end of
     * file, from {@link FileHandler#readCharacter()}) or {@code 0} (a NUL byte), and
     * either would collide with a failure sentinel using the same value.
     * <p>
     * Function readChar coded before 260914, updated on 260914 to return {@code
     * Optional<Integer>} instead of {@code int} so a failed open no longer
     * collides with a valid -1 (EOF) or 0 (NUL) read result, commented in full on
     * 261016.
     * @return An Optional containing the next byte (0 to 255) in the stream of this
     * file, or -1 at the end of the file, or an empty Optional if this file is not in
     * read mode or could not be opened
     */
    public Optional<Integer> readChar() {
        if (mode != FileModeEnum.MODE_READ)
            return Optional.empty();
        
        boolean open = true;

        if (!fileHandler.isOpen())
            open = fileHandler.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT);

        if (open) {
            if (fileHandler.getFileMode() == FileModeEnum.MODE_READ)
                return Optional.of(fileHandler.readCharacter());
        }

        return Optional.empty();
    }

    /**
     * Determines whether this AngFile is open or not. C has no equivalent, since an {@code ang_file}
     * exists only while it is open; here it is how the write methods tell that {@link
     * #open(FileTypeEnum)} has been called, because they refuse until it is.
     * <p>
     * Function isOpen coded before 260914, commented in full on 261016.
     * @return true if the FileHandler attached to this AngFile has a stream open, false otherwise
     */
    public boolean isOpen() {
        return fileHandler.isOpen();
    }

    /**
     * Write a single character to the file. The file must already have been opened with {@link
     * #open(FileTypeEnum)}: there is no lazy open, and a call before it returns false without
     * touching the file. A file created with {@link FileModeEnum#MODE_READ} is refused too.
     * <p>
     * Port of {@code file_writec} in {@code z-file.c}, which takes an {@code ang_file} that {@code
     * file_open} has already produced and writes one byte. Only the low 8 bits of {@code c} are
     * written, as with C's {@code uint8_t} parameter.
     * <p>
     * Function writeChar coded before 260914, updated on 260914 to refuse on MODE_READ, updated on
     * 261016 to refuse on an unopened file, commented in full on 261016.
     * @param c the character to write
     * @return true if the character was written, false if this file is read-only, is not open, or
     * an exception occurred
     */
    public boolean writeChar(int c) {
        if (mode == FileModeEnum.MODE_READ || !isOpen())
            return false;

        return fileHandler.writeCharacter(c);
    }

    /**
     * Read up to a given number of raw bytes from the file, opening it for
     * reading first if it isn't already open. The bytes are returned exactly as
     * stored, with no character decoding; see {@link FileHandler#read(int)} for
     * the end-of-file behaviour.
     * <p>
     * Port of {@code file_read} in {@code z-file.c}. C fills a caller-supplied
     * buffer and returns the count, or -1 on error; here the array's length is the
     * count, so a short array (including an empty one at the end of the file) means
     * the file ran out, and {@code null} stands in for C's -1. A file not created
     * with {@link FileModeEnum#MODE_READ} gets {@code null}, as C's {@code fread}
     * on a write-only handle fails.
     * <p>
     * Function read coded before 260914, updated on 260914 to check the lazy
     * open's result and return null when it fails instead of always attempting
     * the read, retyped from String to byte[] on 261015 to follow
     * {@link FileHandler#read(int)}, commented in full on 261016.
     * @param size The number of bytes to read
     * @return The next bytes from the file, which may be fewer than
     * {@code size} (or none) at the end of the file, or {@code null} if this file
     * is not in read mode, could not be opened, or an Exception occurred.
     */
    public byte [] read(int size) {
        if (mode != FileModeEnum.MODE_READ)
            return null;
        
        boolean open = true;

        if (!isOpen())
            open = fileHandler.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT);

        if (open && fileHandler.getFileMode() == FileModeEnum.MODE_READ)
            return fileHandler.read(size);

        return null;
    }

    /**
     * Write the given bytes to the file exactly as they are, with no encoding and no line ending
     * added. The file must already have been opened with {@link #open(FileTypeEnum)}: there is no
     * lazy open, and a call before it returns false without touching the file. A file created
     * with {@link FileModeEnum#MODE_READ} is refused too.
     * <p>
     * Port of {@code file_write} in {@code z-file.c}, which is {@code fwrite(buf, 1, n, f->fh) == n};
     * here the count is the array's length, so an empty array writes nothing and succeeds.
     * <p>
     * Function write coded before 260914, updated on 260914 to refuse on MODE_READ, updated on
     * 261016 to refuse on an unopened file, commented in full on 261016.
     * @param toWrite The bytes to write
     * @return true if the bytes were written, false if this file is read-only, is not open, or an
     * error occurred
     */
    public boolean write(byte [] toWrite) {
        if (mode == FileModeEnum.MODE_READ || !isOpen())
            return false;

        return fileHandler.write(toWrite);
    }

    /**
     * Gets a line of text, up to the next line ending, from the input stream and returns it
     * without the line ending, opening the file for reading first if it isn't already open.
     * <p>
     * Port of {@code file_getl} in {@code z-file.c}, called with a buffer of 1024 bytes, which is
     * what its callers pass ({@code sizeof(buf)}). The rules are C's, and are carried out by
     * {@link FileHandler#getLine(int)}:
     * <ul>
     *   <li>{@code \r\n}, {@code \n} and a lone {@code \r} all end a line, and a character read
     *       after a {@code \r} is left for the next call;</li>
     *   <li>a tab becomes spaces up to the next multiple of {@code TAB_COLUMNS}, counted in bytes
     *       from the start of the line;</li>
     *   <li>a line is cut at 1023 bytes, and the rest of it comes back from the next call;</li>
     *   <li>end of file before any byte of a line is a null return, where C returns false.</li>
     * </ul>
     * Deliberate divergence, recorded in {@code docs/Accepted_Deviations.md}: when the cut falls
     * inside a multi-byte UTF-8 character, the incomplete bytes are left for the next call instead
     * of being returned as stray bytes.
     * <p>
     * Function getLine coded before 260914, updated on 260914 to check the lazy
     * open's result and return null when it fails instead of always attempting
     * the read, commented in full on 261016.
     * @return The line of text that was read, or null at the end of the file, if this file is not
     * in read mode, if it could not be opened, or if an Exception occurred
     */
    public String getLine() {
        if (mode != FileModeEnum.MODE_READ)
            return null;
        
        boolean open = true;

        if (!fileHandler.isOpen())
            open = fileHandler.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT);

        if (open && fileHandler.getFileMode() == FileModeEnum.MODE_READ)
            return fileHandler.getLine(1024);

        return null;
    }

    /**
     * Write the given text to the file exactly as it is, encoded as UTF-8. Despite the name, no
     * line ending is added, so a caller that wants the text to end a line must include its own
     * {@code '\n'}. The file must already have been opened with {@link #open(FileTypeEnum)}:
     * there is no lazy open, and a call before it returns false without touching the file. A file
     * created with {@link FileModeEnum#MODE_READ} is refused too.
     * <p>
     * Port of {@code file_put} in {@code z-file.c}, which is {@code file_write(f, buf, strlen(buf))}.
     * Its C comment mentions a system-dependent line ending, but the code adds none.
     * <p>
     * Function putLine coded before 260914, updated on 260914 to refuse on MODE_READ, updated on
     * 261016 to refuse on an unopened file, commented in full on 261016.
     * @param line the text to write, with any line ending already in it
     * @return true if the text was written, false if this file is read-only, is not open, or an
     * exception occurred
     */
    public boolean putLine(String line) {
        if (mode == FileModeEnum.MODE_READ || !isOpen())
            return false;

        return fileHandler.putLine(line);
    }

    /**
     * Format a line from a format string and its arguments, then write it to the file as UTF-8 with
     * no line ending added. As with {@link #write(byte[])}, the file must already be open and must
     * not be in read mode, or the call returns false.
     * <p>
     * Port of {@code file_putf} and {@code file_vputf} in {@code z-file.c}: C formats into a
     * 1024-byte buffer with {@code vstrnfmt} and passes it to {@code file_put}. Two deliberate
     * divergences are recorded in {@code docs/Accepted_Deviations.md}:
     * <ul>
     *   <li>the format string is read by {@link String#format}, not by {@code vstrnfmt}, so C
     *       forms such as {@code %lu}, {@code %ld} and {@code %*s} are rejected, and the
     *       exception is swallowed into a false return with nothing written;</li>
     *   <li>text longer than 1023 bytes is cut there, as C cuts its buffer, but a cut that would
     *       fall inside a multi-byte character is moved back so the whole character is dropped.</li>
     * </ul>
     * Formatting uses {@link Locale#ROOT}, not the JVM's default locale, so digits and the decimal
     * point come out as they do from {@code vstrnfmt} ({@code %.2f} gives {@code 1.50} even under
     * a locale that writes {@code 1,50}).
     * <p>
     * Function putFormattedLine coded before 260914, updated on 261016 to format with {@link
     * Locale#ROOT}, commented in full on 261016.
     * @param line The format string, in {@link String#format} syntax
     * @param args The arguments to the format
     * @return true if the line was written, false if the file is read-only or not open, the
     * formatting failed, or the write failed
     */
    public boolean putFormattedLine(String line, Object...args) {
        if (!isOpen() || mode == FileModeEnum.MODE_READ)
            return false;
        
        try {
            String formattedString = String.format(Locale.ROOT, line, args);
            byte[] buffer = formattedString.getBytes(StandardCharsets.UTF_8);
            if (buffer.length <= 1023)
                return write(buffer);

            int cut = 1023;
            while ((buffer[cut] & 0xC0) == 0x80) {
                cut--;
            }
            return write(Arrays.copyOf(buffer, cut));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns whether a given path exists and is a directory.
     * <p>
     * Port of {@code dir_exists} in {@code z-file.c}, which checks the {@code S_IFDIR} bit from
     * {@code stat} rather than mere existence. Requires both {@link Files#exists} and {@link
     * Files#isDirectory}, so a path that exists but names a plain file returns false. A symbolic
     * link is followed, as {@code stat} follows it. A null, empty or malformed path returns false,
     * as a failing {@code stat} does. C's {@code st_mode & S_IFDIR} is not masked with {@code
     * S_IFMT}, so it is also true for a block device or a socket; this method is false for those,
     * which no caller can reach.
     * <p>
     * Function dirExists coded before 260914, updated on 260914 to require
     * Files.isDirectory as well as Files.exists, commented in full on 261016.
     * @param path The directory path
     * @return true if the path exists and is a directory, false otherwise
     */
    public static boolean dirExists(String path) {
        if (path == null || path.isEmpty()) return false;
        
        try {
            Path p = Paths.get(path);
            return Files.exists(p) && Files.isDirectory(p);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Create a given directory, and any missing parent directories, returning whether the
     * directory is there afterwards.
     * <p>
     * Port of {@code dir_create} in {@code z-file.c}. A path that is already a directory returns
     * true at once, as in C. A path that exists but is not a directory (a plain file, say) returns
     * false, as C's final {@code mkdir} fails with {@code EEXIST}. A null or empty path returns
     * false. Two deliberate divergences are recorded in {@code docs/Accepted_Deviations.md}: a
     * fresh path given with a trailing slash returns true here where C returns false, and the
     * directories are made with the platform's default permissions rather than {@code 0755}.
     * <p>
     * Function dirCreate coded before 260914, commented in full on 261016.
     * @param path The directory path to create, including all parent directories which
     *             are not currently created
     * @return true if the directory already existed or was created, false if the path is null or
     * empty, names something that is not a directory, or an exception occurred
     */
    public static boolean dirCreate(String path) {
        if (path == null || path.isEmpty())
            return false;
        
        if (dirExists(path)) 
            return true;
        
        try {
            if (!Files.exists(Paths.get(path))) {
                Files.createDirectories(Paths.get(path));
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        
        return false;
    }
}