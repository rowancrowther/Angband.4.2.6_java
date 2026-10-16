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
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.enums.FileModeEnum;
import uk.co.jackoftradesltd.backend.io.enums.FileTypeEnum;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * The boundary between the game and the file system for one file. It holds the path and the
 * stream I/O behind {@link AngFile}, and carries the path-level helpers of {@code z-file.c}.
 * <p>
 * C's {@code ang_file} wraps a single {@code FILE *}. A handler holds a path and at most one
 * open stream at a time, either the read side ({@code inputStream}, a {@link PushbackInputStream}
 * over a {@link BufferedInputStream}) or the write side ({@code outputStream}). Opening one side
 * closes the other, once the new one has opened. Every byte-level read ({@link #readCharacter()},
 * {@link #read(int)}, {@link #skipBytes(long)}, {@link #getLine(int)}) goes through the one
 * read stream, so they can be mixed freely and each carries on from where the last stopped,
 * as C's {@code file_readc}, {@code file_read}, {@code file_skip} and {@code file_getl} do over
 * one {@code FILE *}.
 * <p>
 * The C functions map onto the methods like this:
 * <ul>
 *   <li>{@code file_delete} is {@link #delete()};</li>
 *   <li>{@code file_move} is {@link #fileMove(Path)};</li>
 *   <li>{@code file_exists} is {@link #fileExists()};</li>
 *   <li>{@code file_get_savefile} is {@link #fileGetSavefile(String, String)};</li>
 *   <li>{@code file_newer} is {@link #fileNewer(String, String)};</li>
 *   <li>{@code file_open} is {@link #open(FileModeEnum, FileTypeEnum)};</li>
 *   <li>{@code file_close} is {@link #close()};</li>
 *   <li>{@code file_skip} is {@link #skipBytes(long)};</li>
 *   <li>{@code file_readc} is {@link #readCharacter()};</li>
 *   <li>{@code file_writec} is {@link #writeCharacter(int)};</li>
 *   <li>{@code file_read} is {@link #read(int)};</li>
 *   <li>{@code file_write} is {@link #write(byte[])};</li>
 *   <li>{@code file_getl} is {@link #getLine(int)};</li>
 *   <li>{@code file_put} is {@link #putLine(String)}.</li>
 * </ul>
 * {@code file_putf} and {@code file_vputf} have no method here: {@link
 * AngFile#putFormattedLine(String, Object...)} formats the text and hands it to {@link
 * #putLine(String)}.
 * <p>
 * Where C takes a file name in each call, the path-level methods here act on the handler's own
 * path ({@link #delete()}, {@link #fileMove(Path)}, {@link #fileExists()}); the exceptions are
 * {@link #fileNewer(String, String)} and {@link #fileGetSavefile(String, String)}, which take
 * names and ignore it. C has no unopened state and no lazy open. Here the three write methods
 * ({@link #writeCharacter(int)}, {@link #write(byte[])}, {@link #putLine(String)}) open the
 * file for appending themselves if no write side is open yet, and C's crash on a {@code NULL}
 * handle becomes a {@code false}, {@code null} or {@code -1} result.
 * <p>
 * The divergences from C that have been accepted (the UTF-8 cut and the {@code len} floor of
 * {@link #getLine(int)}, the negative count of {@link #skipBytes(long)}, the full-precision
 * compare of {@link #fileNewer(String, String)} and the generator of {@link
 * #fileGetSavefile(String, String)}) are recorded in the FileHandler section of {@code
 * docs/Accepted_Deviations.md}. C's {@code file_lock}, {@code file_unlock}, {@code
 * file_get_tempfile} and {@code file_open_hook} have no counterpart here.
 * <p>
 * Class FileHandler coded before 260914, commented in full on 261016.
 */
@SuppressWarnings("ALL")
public class FileHandler {
    /**
     * True while the write side was opened by {@link #open(FileModeEnum, FileTypeEnum)} in {@link
     * FileModeEnum#MODE_APPEND}. Every call to {@code open} clears it first, and opening the read
     * side clears it too. {@link #getFileMode()} reads it to tell append from write. A write
     * side opened lazily by a write method appends but leaves this false.
     * <p>
     * Field appendFile coded before 261016, commented in full on 261016.
     */
    public boolean appendFile;

    /**
     * The seed {@link #fileGetSavefile(String, String)} gives its {@link Random}. Each call
     * overwrites it with the clock unless {@link #debug} is true, in which case it is used as
     * it stands.
     * <p>
     * Field seed coded before 261016, commented in full on 261016.
     */
    public long seed = System.currentTimeMillis();
    /**
     * A test hook. When true, {@link #fileGetSavefile(String, String)} keeps {@link #seed}
     * instead of re-reading the clock, so a test can fix the sequence of names. Leave it false
     * in the game.
     * <p>
     * Field debug coded before 261016, commented in full on 261016.
     */
    public boolean debug = false;

    /**
     * True when the last {@link #open(FileModeEnum, FileTypeEnum)} was for {@link
     * FileTypeEnum#FTYPE_SAVE}. C looks at the file type only while opening; here it is kept so
     * that a write method opening the file lazily makes a savefile with owner-only permissions
     * and not an ordinary file. False until the first {@code open}.
     * <p>
     * Field isSave coded before 261016, commented in full on 261016.
     */
    private boolean isSave = false;

    /**
     * Path of the file this handler operates on. Set by the constructor and replaced by {@link
     * #fileMove(Path)} after a successful move. {@link #delete()}, {@link #fileExists()} and
     * every open use it.
     * <p>
     * Field filePath coded before 260914, commented in full on 261016.
     */
    private Path filePath;
    /**
     * Active read stream, or {@code null} when the file is not open for reading. The four-byte
     * pushback lets {@link #getLine(int)} give back up to three bytes of an incomplete UTF-8
     * character, or the one byte that follows a lone {@code '\r'}, and lets {@link
     * #skipBytes(long)} test for the end of the file without losing a byte.
     * <p>
     * Field inputStream coded before 260914, commented in full on 261016.
     */
    private PushbackInputStream inputStream;

    /**
     * Active write stream, or {@code null} when the file is not open for writing. {@link
     * #putLine(String)} wraps it in {@link #bufferedWriter}; the other write methods use it
     * directly.
     * <p>
     * Field outputStream coded before 260914, commented in full on 261016.
     */
    private OutputStream outputStream;

    /**
     * UTF-8 writer over {@link #outputStream}, made by the first {@link #putLine(String)} and
     * flushed after every call, so nothing is held back and the other write methods can use
     * {@code outputStream} directly in between. {@code null} until then and again after any
     * close or reopen of the write side. When set, closing it closes {@code outputStream} too.
     * <p>
     * Field bufferedWriter coded before 261016, commented in full on 261016.
     */
    private BufferedWriter bufferedWriter = null;
    /**
     * Default open option retained from construction. Nothing reads it: the concrete options
     * are chosen on each {@link #open(FileModeEnum, FileTypeEnum)} call.
     * <p>
     * Field option coded before 260914, commented in full on 261016.
     */
    private final StandardOpenOption option;

    /**
     * Logger used to report I/O failures without throwing to callers.
     * <p>
     * Field logger coded before 260914, commented in full on 261016.
     */
    private final Logger logger = LogManager.getRootLogger();

    /**
     * Creates a new FileHandler from a string path. Nothing is opened, created or looked at on
     * disk: the handler is closed, and {@link #open(FileModeEnum, FileTypeEnum)} makes it
     * usable. C has no such step, as {@code file_open} returns a handle already open. The
     * path is only parsed, so a string the platform cannot parse throws {@link
     * InvalidPathException}.
     * <p>
     * Function FileHandler coded before 260914, commented in full on 261016.
     * @param path The path to create this FileHandler from.
     */
    public FileHandler(String path) {
        filePath = Paths.get(path);
        option = StandardOpenOption.WRITE;
        appendFile = false;
    }

    /**
     * Check to see if this fileHandler is open. C has no counterpart, as an {@code ang_file}
     * exists only while it is open.
     * <p>
     * Function isOpen coded before 260914, commented in full on 261016.
     * @return true if either the inputStream or the outputStream is not null, false if they are
     * both null
     */
    public boolean isOpen() {
        return (inputStream != null || outputStream != null);
    }

    /**
     * Delete the file this is a FileHandler of.
     * <p>
     * Port of {@code file_delete} in {@code z-file.c}, which is {@code remove()} on the name:
     * a missing path gives false, and an empty directory is removed as a file would be. Unlike
     * C, the handler closes itself first so that it does not hold the file open, and the result
     * of that close is ignored: the delete is tried even if the close failed.
     * <p>
     * Function delete coded before 260914, commented in full on 261016.
     * @return True if the file was deleted, false otherwise.
     */
    public boolean delete() {
        File file = new File(filePath.toString());
        try {
            close();
        } catch (Exception e) {
            return false;
        }

        return file.delete();
    }

    /**
     * Moves a file to a new location/name.
     * <p>
     * Port of {@code file_move} in {@code z-file.c}, which is {@code rename()}. An existing
     * target is replaced, and a missing source gives false. Off Windows the move is requested
     * as atomic, as {@code rename} is, so a move across file systems fails where C's
     * {@code rename} fails; on Windows it is an ordinary move that replaces the target.
     * <p>
     * A failed move changes nothing, as in C: an open handler stays open, on its old path,
     * and carries on reading or writing where it was. Two things after a successful move are
     * the handler's own. It closes itself, because the stream it held was opened on the old
     * name and C's handle would go on pointing at the moved file, and its path becomes
     * {@code newFilePath}, so the handler follows the file. A {@code null} target gives false
     * without touching anything.
     * <p>
     * Function fileMove coded before 260914, updated on 261016 to close only after the move
     * has worked, commented in full on 261016.
     * @param newFilePath The path to the new file
     * @return true if the movement occurred, false otherwise
     */
    public boolean fileMove(Path newFilePath) {
        if (newFilePath == null)
            return false;
        
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Path path;
            if (os.contains("win")) {
                path = Files.move(filePath, newFilePath, REPLACE_EXISTING);
            } else {
                path = Files.move(filePath, newFilePath, REPLACE_EXISTING, ATOMIC_MOVE);
            }
            
            if (path != null) {
                close();
                filePath = newFilePath;
                return true;
            }

            return false;
        } catch (Exception e) {
            logger.error("An exception has occurred: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Determines whether this file exists.
     * <p>
     * Port of {@code file_exists} in {@code z-file.c}, in the {@code stat()} form that Unix
     * builds use: any path that can be {@code stat}ed exists, so a directory counts, and a
     * symbolic link is followed. (C's Windows form refuses a directory; that form is not the
     * one ported.) C takes the name as an argument; here it is the handler's own path.
     * <p>
     * Function fileExists coded before 260914, commented in full on 261016.
     * @return true if the file exists on the system, false otherwise
     */
    public boolean fileExists() {
        return filePath.toFile().exists();
    }

    /**
     * Set filename to new filename based on an existing filename, using
     * the specified file extension. Resulting filename doesn't usually
     * exist yet.
     * <p>
     * Port of {@code file_get_savefile} in {@code z-file.c}. The first candidate is {@code
     * base}, a number from 0 to 999999, {@code '.'} and {@code ext}. While a candidate exists,
     * up to 100 times, the next is {@code base}, a fresh number, the retry count (1 to 100)
     * and {@code ext}. After the 100th retry the last candidate is returned even if it
     * exists, so the caller always gets a name. Existence is tested on the candidate as given,
     * from the current directory if it is relative, and the handler's own path plays no part.
     * C's shorter {@code DJGPP} naming is not ported.
     * <p>
     * The numbers come from a {@link Random} of its own, so naming a file never disturbs the
     * game's generator, as with C's {@code Rand_simple}. It is seeded from the clock on every
     * call unless {@link #debug} is true, which means two calls in the same millisecond
     * give the same number; C's static seed would not. C also cuts the name to the size of
     * the caller's buffer, which Java does not. Both are in the FileHandler section of {@code
     * docs/Accepted_Deviations.md}.
     * <p>
     * Function fileGetSavefile coded before 260914, commented in full on 261016.
     * @param base The first part of the path to this file
     * @param ext The extension of this file
     * @return The full filepath as a string
     */
    public String fileGetSavefile(String base, String ext) {
        String filename;
        Random random = new Random();
        if (!debug)
            seed = System.currentTimeMillis();
        
        random.setSeed(seed);
        int count = 0;
        
        filename = String.format(Locale.ROOT, "%s%d.%s", base, 
                random.nextLong(0, 1000000), ext);
        while (Files.exists(Paths.get(filename)) && count < 100) {
            count++;
            
            filename = String.format(Locale.ROOT, "%s%d%d.%s", base,
                    random.nextLong(0, 1000000), count,  ext);
        }
        
        return filename;
    }

    /**
     * Returns true if the first file was modified more recently than the second,
     * and false otherwise.
     * <p>
     * A missing first file is never newer, and a missing second file is always
     * older than an existing first one, so the existence checks come before any
     * comparison. When both files exist their last-modified times are compared
     * at the full precision the file system reports, with no rounding to whole
     * seconds. A file that cannot be inspected because of an {@link IOException}
     * is logged and treated as not newer.
     * <p>
     * Port of {@code file_newer} in {@code z-file.c}, which {@code stat}s both names and
     * compares {@code st_mtime}. The order of the tests and the strict "later than" are the
     * same; the whole-second compare of C is the one difference, and it is recorded in the
     * FileHandler section of {@code docs/Accepted_Deviations.md}. Both files are named by the
     * arguments and the handler's own path plays no part.
     * <p>
     * Function fileNewer coded before 260914, commented in full on 261016.
     *
     * @param firstFiLeName  the name of the file that is expected to be newer
     * @param secondFileName the name of the file it is compared against
     * @return false if the first file does not exist<br>
     * true if the first file exists and the second does not<br>
     * true if both exist and the first one's last-modified time is later than
     * the second's<br>
     * false if both exist and the first one's last-modified time is equal to or
     * earlier than the second's, or if reading either time throws an
     * {@link IOException}
     */
    public boolean fileNewer(String firstFiLeName, String secondFileName) {
        Path firstFilePath = Paths.get(firstFiLeName);
        Path secondFilePath = Paths.get(secondFileName);

        // Check for existence
        try {
            if (!Files.exists(firstFilePath)) return false;

            if (!Files.exists(secondFilePath)) return true;

            FileTime firstFileTime = Files.getLastModifiedTime(firstFilePath);
            FileTime secondFileTime = Files.getLastModifiedTime(secondFilePath);

            int result = firstFileTime.compareTo(secondFileTime);

            return result > 0;
        } catch (IOException e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Open the file for reading. This is the {@code MODE_READ} branch of {@code file_open} in
     * {@code z-file.c}, {@code fopen(buf, "rb")}. Any read stream already open is closed first,
     * so a second open starts again at the beginning of the file. The path is resolved with
     * {@code toRealPath}, so a missing file fails and a symbolic link is followed.
     * <p>
     * Only one direction is ever active at a time, so once the new stream has opened any
     * write stream is closed to avoid holding both handles to the same file, and {@link
     * #appendFile} is cleared. If the open fails the read stream is left {@code null} and the
     * write side, if there was one, is left as it was.
     * <p>
     * A directory cannot be opened here, where C's {@code fopen} of a directory succeeds on
     * Linux and only fails when it is read.
     * <p>
     * Function openInputStream coded before 260914, commented in full on 261016.
     * @return true if the read stream is open, false if the open failed (the reason is logged)
     */
    private boolean openInputStream() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            
            inputStream = new PushbackInputStream(
                          new BufferedInputStream(
                          new FileInputStream(filePath.toRealPath().toString())), 4);
            if (outputStream != null){
                if (bufferedWriter != null) bufferedWriter.close();
                else outputStream.close();
                outputStream = null;
                bufferedWriter = null;
                appendFile = false;
            }
            return true;
        } catch (IOException e) {
            logger.error("An exception has occurred: " + e.getMessage(), e);
            inputStream = null;
            return false;
        }
    }

    /**
     * Open the file for writing. This is the write and append branches of {@code file_open} in
     * {@code z-file.c}, and the open that the lazy writers use. Any write stream already open
     * is closed first, and {@link #bufferedWriter} is dropped with it ({@link
     * #putLine(String)} flushes after every call, so nothing is lost).
     * <p>
     * On a file system with POSIX permissions, {@link FileTypeEnum#FTYPE_SAVE} opens through a
     * byte channel that creates the file with {@code rw-------}, as C's {@code open()} with
     * {@code S_IRUSR | S_IWUSR} does. Every other type opens with {@link
     * Files#newOutputStream}, which gives a new file the default permissions, as {@code fopen}
     * does. {@code options} decide the rest: {@code CREATE_NEW} makes the open fail if the file
     * exists (C's {@code O_EXCL}), {@code TRUNCATE_EXISTING} is {@code "wb"}, and {@code APPEND}
     * is {@code "a+"}. As with {@link #openInputStream()}, only one direction is active at a
     * time, so once the write stream has opened any read stream is closed. If the open fails
     * the write stream is left {@code null} and the read side, if there was one, is left as it
     * was.
     * <p>
     * Function openOutputStream coded before 260914, commented in full on 261016.
     * @param type the file type; only {@code FTYPE_SAVE} changes how the file is created
     * @param options how to open the file
     * @return true if the write stream is open, false if the open failed (the reason is
     * logged)
     */
    private boolean openOutputStream(FileTypeEnum type, OpenOption... options) {
        try {
            if (outputStream != null) {
                outputStream.close();
                bufferedWriter = null;
                outputStream = null;
            }
            
            Set<OpenOption> optionSet = Set.of(options);
            Set<PosixFilePermission> attrs = PosixFilePermissions
                    .fromString("rw-------");
            
            // Deal with POSIX presence
            if (filePath.getFileSystem().supportedFileAttributeViews().contains("posix") 
                    && type == FileTypeEnum.FTYPE_SAVE) {
                outputStream = Channels.newOutputStream(Files.newByteChannel(filePath, optionSet,
                        PosixFilePermissions.asFileAttribute(attrs)));
            } else 
                outputStream = Files.newOutputStream(filePath, options);
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            return true;
        } catch (IOException e) {
            logger.error("An exception has occurred: " + e.getMessage(), e);
            outputStream = null;
            return false;
        }
    }

    /**
     * Open up a file for reading or writing. It will open up a Stream, either input
     * or output, and then close down the other stream if it exists.
     * <p>
     * Port of {@code file_open} in {@code z-file.c}, which returns an {@code ang_file} or
     * {@code NULL}; here the handler already exists, so success or failure comes back as a
     * boolean and a failure is logged. The modes follow C's:
     * <ul>
     *   <li>{@link FileModeEnum#MODE_READ} fails on a missing file ({@code "rb"});</li>
     *   <li>{@link FileModeEnum#MODE_WRITE} creates a missing file and truncates an existing
     *       one ({@code "wb"});</li>
     *   <li>{@link FileModeEnum#MODE_WRITE} with {@link FileTypeEnum#FTYPE_SAVE} fails if the
     *       file already exists and creates it owner-only otherwise ({@code O_CREAT | O_EXCL}
     *       with {@code S_IRUSR | S_IWUSR});</li>
     *   <li>{@link FileModeEnum#MODE_APPEND} creates a missing file and keeps an existing
     *       one's contents ({@code "a+"}), whatever the type, so a savefile opened for append
     *       gets ordinary permissions as in C.</li>
     * </ul>
     * Opening records {@link #appendFile} and {@link #isSave} before the attempt, so they
     * describe the last call even if it failed. C's {@code file_open_hook}, which C calls after
     * opening in any mode but read, is not ported, and a mode with no case here gives false
     * where C asserts.
     * <p>
     * Two small differences from C are recorded in the FileHandler section of {@code
     * docs/Accepted_Deviations.md}. A handler opened for append is write-only, where {@code
     * "a+"} also lets C read. And {@link FileModeEnum#MODE_READ} on a directory fails here (see
     * {@link #openInputStream()}).
     * <p>
     * Function open coded before 260914, commented in full on 261016.
     * @param mode The mode to open this file up
     * @param type The type of file; it matters only for {@code FTYPE_SAVE}
     * @return true if the file was opened, false otherwise (C returns {@code NULL})
     */
    public boolean open(@NotNull FileModeEnum mode, @NotNull FileTypeEnum type) {
        appendFile = false;
        isSave = type == FileTypeEnum.FTYPE_SAVE;
        
        switch (mode) {
            case FileModeEnum.MODE_READ:
                return openInputStream(); 
            case FileModeEnum.MODE_WRITE:
                if (type == FileTypeEnum.FTYPE_SAVE) {
                    return openOutputStream(type, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                }
                return openOutputStream(type, StandardOpenOption.WRITE, 
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            case FileModeEnum.MODE_APPEND:
                appendFile = true;
                if (type == FileTypeEnum.FTYPE_SAVE) {
                    return openOutputStream(FileTypeEnum.FTYPE_TEXT, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                }
                return openOutputStream(type, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        }

        return false;
    }

    /**
     * Skip a number of bytes in the open stream.
     * <p>
     * Port of {@code file_skip} in {@code z-file.c}, which is {@code fseek(fh, bytes,
     * SEEK_CUR)}. A zero count returns true without moving, and a skip past the end of the
     * file returns true as {@code fseek} does on a regular file, the next read reporting the
     * end. Differences from C: a negative count returns false and leaves the position alone,
     * because the read stream cannot move backwards (recorded in the FileHandler section of
     * {@code docs/Accepted_Deviations.md}); and only the read side can be skipped, so a
     * closed handler or one open for writing returns false, where {@code fseek} works on any
     * handle.
     * <p>
     * Function skipBytes coded before 260914, commented in full on 261016.
     * @param bytesToSkip The number of bytes to skip
     * @return true if the bytes were successfully skipped, false otherwise
     */
    public boolean skipBytes(long bytesToSkip) {
        if (inputStream == null) return false;
        if (bytesToSkip == 0) return true;

        try {
            long totalLeft = bytesToSkip;
            if (totalLeft > 0) {
                long skipped = inputStream.skip(bytesToSkip);
                totalLeft -= skipped;
                while (totalLeft > 0) {
                    int b = inputStream.read();
                    if (b != -1) {
                        inputStream.unread(b);
                        skipped = inputStream.skip(totalLeft);
                        totalLeft -= skipped;
                    } else
                        return true;
                }
                return true;
            } else return false;
        } catch (IOException e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Close down the two streams. (but don't cross them...)
     * <p>
     * Port of {@code file_close} in {@code z-file.c}, which is {@code fclose} and returns
     * false if that fails. The read side is closed and cleared first, then the write side
     * (through {@link #bufferedWriter} when there is one). A handler with nothing open returns
     * true, as C has no unopened handle to close. On an {@link IOException} the failure is
     * logged and false returned, and the side that failed is left set. {@link #appendFile} and
     * {@link #isSave} are not reset.
     * <p>
     * Function close coded before 260914, commented in full on 261016.
     * @return True if the streams were closed, false otherwise
     */
    public boolean close() {
        if (inputStream == null && outputStream == null 
                && bufferedWriter == null) return true;

        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (bufferedWriter != null) bufferedWriter.close();
            else if (outputStream != null) outputStream.close();
            outputStream = null;
            bufferedWriter = null;
        } catch (IOException e) {
            logger.error("An exception has occurred: " + e.getMessage(), e);
            return false;
        }

        return true;
    }
    
    /**
     * Says which way the handler is open, as C's {@code ang_file} carries it in its {@code
     * mode} field. The read side wins if it is open, then append, then write.
     * <p>
     * A write side opened lazily by {@link #writeCharacter(int)}, {@link #write(byte[])} or
     * {@link #putLine(String)} appends to the file but reports {@link FileModeEnum#MODE_WRITE},
     * because only {@link #open(FileModeEnum, FileTypeEnum)} sets {@link #appendFile}.
     * <p>
     * Function getFileMode coded before 260914, commented in full on 261016.
     * @return {@code MODE_READ}, {@code MODE_APPEND} or {@code MODE_WRITE} for the side that is
     * open, or {@code null} if neither is
     */
    public FileModeEnum getFileMode() {
        if (inputStream != null) return FileModeEnum.MODE_READ;
        if (appendFile && outputStream != null) return FileModeEnum.MODE_APPEND;
        if (outputStream != null) return FileModeEnum.MODE_WRITE;
        return null;
    }

    /**
     * Returns the next character in the input stream.
     * <p>
     * Port of {@code file_readc} in {@code z-file.c}, which returns a bool and hands the byte
     * back through a {@code uint8_t} pointer. Here the byte comes back as 0 to 255 and {@code
     * -1} stands in for C's false. {@code -1} is returned at the end of the file, when no read
     * stream is open (where C would use a {@code NULL} handle), and when an {@link
     * IOException} is logged; C cannot tell the end from an error either. It reads the one
     * stream that {@link #getLine(int)}, {@link #read(int)} and {@link #skipBytes(long)} use,
     * so it carries on from wherever they stopped.
     * <p>
     * Function readCharacter coded before 260914, commented in full on 261016.
     * @return The next character in the input stream, as 0 to 255, or -1
     */
    public int readCharacter() {
        int i = -1;

        if (inputStream == null) return i;

        try {
            i = inputStream.read();
        } catch (IOException e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return -1;
        }

        return i;
    }

    /**
     * Write a character input as an integer to the outputStream.
     * <p>
     * Port of {@code file_writec} in {@code z-file.c}, which is {@code file_write} of one
     * {@code uint8_t}. Only the low eight bits of {@code c} are written. The byte goes
     * straight to {@link #outputStream}, which is safe beside {@link #putLine(String)}
     * because that flushes after every call. Returns false if the read side is open.
     * <p>
     * C has no lazy open. If no write side is open yet, this opens the file for appending,
     * creating it if it is missing, as a savefile (owner-only) if the last {@link
     * #open(FileModeEnum, FileTypeEnum)} was {@code FTYPE_SAVE}; if that open fails the write
     * fails and false is returned.
     * <p>
     * Function writeCharacter coded before 260914, commented in full on 261016.
     * @param c the character to write
     * @return true if the character was written and no exception occurred, false otherwise
     */
    public boolean writeCharacter(int c) {
        if (inputStream != null)
            return false;
        
        if (outputStream == null) {
            if (isSave)
                openOutputStream(FileTypeEnum.FTYPE_SAVE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            else
                openOutputStream(FileTypeEnum.FTYPE_TEXT, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        try {
            outputStream.write(c);
        } catch (Exception e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * Read up to a number of raw bytes from the input stream, blocking until
     * {@code size} bytes have been read or the end of the file is reached. The
     * bytes are returned exactly as stored, with no character decoding, so values
     * above 127 survive unchanged.
     * <p>
     * Port of {@code file_read} in {@code z-file.c}. C fills a caller-supplied
     * buffer and returns the count, or -1 on error; here the array's length is
     * the count, so a short array (including a zero-length one at the end of the
     * file) means the file ran out, and {@code null} stands in for C's -1.
     * <p>
     * Two edges differ from C, where {@code size} is a {@code size_t} and a failed read
     * after some bytes still returns the count. A negative {@code size} throws {@link
     * IllegalArgumentException}, and an {@link IOException} part-way through returns {@code
     * null} and drops the bytes already read. Neither is reachable from a caller that passes
     * a block size. The negative size is recorded in the FileHandler section of {@code
     * docs/Accepted_Deviations.md}.
     * <p>
     * Function read coded before 260914, retyped from String to byte[] on 261015
     * so the bytes are no longer decoded with the platform's default charset,
     * commented in full on 261016.
     * @param size The number of bytes to read
     * @return The bytes read, which may be fewer than {@code size} (or none) at
     * the end of the file, or {@code null} if no input stream is open or an
     * IOException occurred
     */
    public byte [] read(int size) {
        if (inputStream == null) return null;

        try {
            return inputStream.readNBytes(size);
        } catch (IOException e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Attempt to append raw bytes to the current outputStream, exactly as given.
     * <p>
     * Port of {@code file_write} in {@code z-file.c}, which is {@code fwrite(buf, 1, n, fh) ==
     * n}. Here the whole array is written, so the array's length is C's {@code n}; a caller
     * with fewer bytes to write passes a shorter array. Returns false if the read side is
     * open or an exception occurs.
     * <p>
     * C has no lazy open. If no write side is open yet, this opens the file for appending,
     * creating it if it is missing, as a savefile (owner-only) if the last {@link
     * #open(FileModeEnum, FileTypeEnum)} was {@code FTYPE_SAVE}; if that open fails the write
     * fails and false is returned.
     * <p>
     * Function write coded before 260914, commented in full on 261016.
     * @param toWrite the bytes to append to the stream
     * @return true if the bytes are correctly written, false if an error occurs
     */
    public boolean write(byte [] toWrite) {
        if (inputStream != null)
            return false;
        
        if (outputStream == null) {
            if (isSave)
                openOutputStream(FileTypeEnum.FTYPE_SAVE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            else
                openOutputStream(FileTypeEnum.FTYPE_RAW, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
            
        try {
            outputStream.write(toWrite);
        } catch (Exception e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * Read a line from the inputStream, in at most {@code len - 1} bytes.
     * <p>
     * Port of {@code file_getl} in {@code z-file.c}, built as C builds it on the single-byte
     * read, so it mixes freely with {@link #readCharacter()}, {@link #read(int)} and {@link
     * #skipBytes(long)}. The bytes are collected and decoded once as UTF-8 at the end. Counts
     * are of bytes, as in C. A line ends at:
     * <ul>
     *   <li>{@code '\n'}, {@code "\r\n"} or a lone {@code '\r'}: the ending is consumed and not
     *       returned. Any run of {@code '\r'} before the next byte is swallowed, and the byte
     *       after it is pushed back unless it is {@code '\n'};</li>
     *   <li>the end of the file: the bytes so far are returned, or {@code null} if there are
     *       none (C returns false), so a lone {@code '\r'} at the end of the file is
     *       {@code null};</li>
     *   <li>{@code len - 1} bytes: the line is returned there and the rest comes on the next
     *       call. If a newline follows at once, the next call returns an empty line, as in
     *       C;</li>
     *   <li>a tab that does not fit: the tab is consumed and the line ends early.</li>
     * </ul>
     * A tab otherwise becomes spaces up to the next multiple of {@code TAB_COLUMNS}, counted
     * from the start of the line.
     * <p>
     * Three differences from C. The first two are in the FileHandler section of {@code
     * docs/Accepted_Deviations.md}. When the line stopped because the buffer was full (or at
     * the tab) and its last bytes are an incomplete UTF-8 character, those bytes are dropped
     * from the line and pushed back, so the next call starts on a whole character and a line
     * can be up to 3 bytes shorter than {@code len - 1}; C returns the cut character as
     * stray bytes. A {@code len} below 5 is refused with an {@link IllegalArgumentException},
     * checked before anything else, because a buffer under 4 bytes cannot hold the longest
     * character and the pushback would return an empty line without consuming a byte. C takes
     * any {@code len}. The third is not recorded there: a NUL byte stays in the returned
     * string, where C's buffer is a C string and the line ends at the NUL. Bytes that are not
     * valid UTF-8 decode to {@code U+FFFD}.
     * <p>
     * Function getLine coded before 260914, commented in full on 261016.
     * @param len the size of the buffer in bytes, as C's {@code len}; at most {@code len - 1}
     *            bytes of line are read. Must be 5 or more.
     * @return the line without its ending; {@code null} at the end of the file with nothing
     * read, when no read stream is open, or if an {@link IOException} occurred
     * @throws IllegalArgumentException if {@code len} is below 5
     */
    public String getLine(int len) {
        // check enough bytes are to be read so as to get the longest character
        if (len < 5) {
            String message = "Illegal length for getLine, must be greater than 4";
            logger.fatal(message);
            throw new IllegalArgumentException(message);
        }
        
        if (inputStream == null) return null;
                
        byte [] buffer = new byte[len - 1];
        int index = 0;
        boolean seenCr = false;
        
        try {
            while (index < len - 1) {
                int ch = readCharacter();
                
                // EOF
                if (ch == -1) {
                    if (index == 0)
                        return null;
                    return new String(buffer, 0, index, StandardCharsets.UTF_8);
                }

                // Part of \r\n?
                if (ch == '\r') {
                    seenCr = true;
                    continue;
                }                    

                // not part of \r\n
                if (seenCr && ch != '\n') {
                    inputStream.unread(ch);
                    return new String(buffer, 0, index, StandardCharsets.UTF_8);
                }
                
                // EOL
                if (ch == '\n') return new String(buffer, 0, index, StandardCharsets.UTF_8);
                
                // Tab
                if (ch == '\t') {
                    int tabStop = ((index + GameConstants.TAB_COLUMNS) 
                            / GameConstants.TAB_COLUMNS) 
                            * GameConstants.TAB_COLUMNS;
                    if (tabStop >= len) break;
                    for (int i = index; i < tabStop; i++) {
                        buffer[i] = (byte) ' ';
                    }
                    index = tabStop;
                    continue;
                }
                
                // Anything else
                buffer[index++] = (byte) ch;
            }
            
            // Divergence check
            int steps = 0;
            if (index == 0)
                return "";
            while ((buffer[index - (1 + steps)] & 0xC0) == 0x80) {
                steps++;
                if (index - (steps + 1) < 0) {
                    logger.warn("Malformed line found in file.");
                    return new String(buffer, 0, index, StandardCharsets.UTF_8);
                }
            }
            
            boolean pushedBack = false;
            
            if (steps > 3) {
                return new String(buffer, 0, index, StandardCharsets.UTF_8);
            } else {
                int characterLength = 0;

                if ((buffer[index - (1 + steps)] & 0xE0) == 0xC0) {
                    characterLength = 2;
                } else if ((buffer[index - (1 + steps)] & 0xF0) == 0xE0) {
                    characterLength = 3;
                } else if ((buffer[index - (1 + steps)] & 0xF8) == 0xF0) {
                    characterLength = 4;
                }

                if (steps + 1 < characterLength && characterLength != 0) {
                    for (int readback = -1; readback > -1 - (steps + 1); readback--) {
                        inputStream.unread(buffer[index + readback]);
                        pushedBack = true;
                    }
                }
                
                if (!pushedBack)
                    return new String(buffer, 0, index, StandardCharsets.UTF_8);
                
                return new String(buffer, 0, index - (steps + 1), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Append text to the outputStream exactly as given, encoded as UTF-8 and flushed
     * straight away. No line ending is added, so a caller that wants the text to end
     * a line must include its own {@code '\n'}.
     * <p>
     * Port of {@code file_put} in {@code z-file.c}, which writes its buffer verbatim;
     * {@code file_putf} callers likewise supply their own newline. If no output stream
     * is open yet, one is opened first, creating the file if it is missing and
     * appending to it otherwise (as a savefile, owner-only, if the last {@link
     * #open(FileModeEnum, FileTypeEnum)} was {@code FTYPE_SAVE}). C has no lazy open.
     * <p>
     * The text ends at the first NUL, as it does in C where {@code strlen} sets the length:
     * the NUL and everything after it are not written. A line that begins with a NUL writes
     * nothing and still returns true, as {@code fwrite} of zero bytes does.
     * <p>
     * Function putLine coded before 260914, updated on 261015 to stop appending a
     * {@code '\n'} so that it writes verbatim like C, updated on 261016 to stop the text at a
     * NUL like {@code strlen}, commented in full on 261016.
     * @param line the text to write, with any line ending already in it
     * @return true if the text was written, false if an exception occurred (including
     * the lazy open having failed, or {@code line} being {@code null})
     */
    public boolean putLine(String line) {
        if (inputStream != null)
            return false;
        
        if (line != null && line.contains(String.valueOf('\0'))) {
            int cut = line.indexOf('\0');
            line = line.substring(0, cut);
        }
        
        if (outputStream == null) {
            if (isSave)
                openOutputStream(FileTypeEnum.FTYPE_SAVE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            else
                openOutputStream(FileTypeEnum.FTYPE_TEXT, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        try {
            if (bufferedWriter == null)
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            bufferedWriter.write(line);
            bufferedWriter.flush();
            return true;
        } catch (Exception e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }
    }
}