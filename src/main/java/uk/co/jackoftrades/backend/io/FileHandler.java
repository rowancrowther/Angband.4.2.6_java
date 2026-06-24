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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.enums.FileModeEnum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;

/**
 * The interface class between the file system and
 * Angband. This should be system neutral.
 */
@SuppressWarnings("ALL")
public class FileHandler {
    /**
     * Path of the file this handler operates on; may be updated by {@link #fileMove(Path)}.
     *
     * @author ClaudeCode
     */
    private Path filePath;
    /**
     * Active read stream, or {@code null} when the file is not open for reading.
     *
     * @author ClaudeCode
     */
    private FileInputStream inputStream;
    /**
     * Active write stream, or {@code null} when the file is not open for writing.
     *
     * @author ClaudeCode
     */
    private FileOutputStream outputStream;
    /**
     * Default open option retained from construction. Largely vestigial now that
     * the concrete mode is chosen per-{@link #open(FileModeEnum)} call.
     *
     * @author ClaudeCode
     */
    private final StandardOpenOption option;
    /**
     * Logger used to report I/O failures without throwing to callers.
     *
     * @author ClaudeCode
     */
    private final Logger logger = LogManager.getRootLogger();

    /**
     * Creates a new FileHandler from a string path
     * @param path The path to create this FileHandler from.
     */
    public FileHandler(String path) {
        filePath = Paths.get(path);
        option = StandardOpenOption.WRITE;
    }

    /**
     * Check to see if this fileHandler is open
     * @return true if either the inputStream or the outputStream is not null, false if they are
     * both null
     */
    public boolean isOpen() {
        return (inputStream != null || outputStream != null);
    }

    /**
     * Delete the file this is a FileHandler of
     * @return True if the file was deleted, false otherwise.
     */
    public boolean delete() {
        File file = new File(filePath.toString());
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        } catch (Exception e) {
            return false;
        }

        return file.delete();
    }

    /**
     * Moves a file to a new location/name
     * @param newFile The path to the new file
     * @return true if the movement occurred, false otherwise
     */
    public boolean fileMove(Path newFile) {
        //logger.traceEntry("fileMove: New filename = {}", newFile.toString());
        if (newFile.toFile().exists())
            return false;

        try {
            logger.debug("Before renameTo");
            Path path = Files.move(filePath, newFile);
            logger.debug("After renameTo, result: " + path);

            filePath = newFile;
            logger.debug("Renamed fileHandler.filePath");

            return true;
        } catch (Exception e) {
            logger.error("An exception has occurred: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Determines whether this file exists
     * @return true if the file exists on the system, false otherwise
     */
    public boolean fileExists() {
        return filePath.toFile().exists();
    }

    /**
     * Create the backing file on disk if it does not already exist. Existence is
     * treated as success so callers can use this idempotently before opening.
     *
     * @return true if the file now exists (created or already present),
     * false if creation failed with an {@link IOException}
     * @author ClaudeCode
     */
    public boolean createFile() {
        if (!Files.exists(filePath)){
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

//    /**
//     * Closes down any old file and reopens as a new one. This includes
//     * closing down all open streams
//     * Not used - use move instead
//     * @param newFileName The new file name
//     */
//    public void setFileName(String newFileName) {
//        try {
//            if (inputStream != null) inputStream.close();
//            if (outputStream != null) outputStream.close();
//        } catch (Exception e) {
//            logger.error("An exception has occured: " + e.getMessage(), e);
//        } finally {
//            this.filePath = Paths.get(newFileName);
//        }
//    }

    /**
     * Set filename to new filename based on an existing filename, using
     * the specified file extension. Resulting filename doesn't usually
     * exist yet.
     * <br><br>
     * TODO: Check how this is used and move it out into a different class if needed
     * @param base The first part of the path to this file
     * @param ext The extension of this file
     * @return The full filepath as a string
     */
    public String fileGetSavefile(String base, String ext) {
        return base + "." + ext;
    }

    /**
     * Returns true if the first file is newer than the second, or false otherwise
     * @param firstFiLeName The string name of the first file
     * @param secondFileName The string name of the second file
     * @return if the first file doesn't exist this returns false<br>
     * if the second file doesn't exist this returns true<br>
     * otherwise it compares the FileTime of the two files and returns
     * true if the FileTime of the first file is earlier than that of
     * the second one, and false if they are the same, or greater
     */
    public boolean fileNewer(String firstFiLeName, String secondFileName) {
        //logger.traceEntry("Entered fileNewer first: {} second: {}", firstFiLeName, secondFileName);

        Path firstFilePath = Paths.get(firstFiLeName);
        Path secondFilePath = Paths.get(secondFileName);

        // Check for existence
        try {
            if (!Files.exists(firstFilePath)) return false;

            if (!Files.exists(secondFilePath)) return true;

            FileTime firstFileTime = Files.getLastModifiedTime(firstFilePath);
            FileTime secondFileTime = Files.getLastModifiedTime(secondFilePath);

            int result = firstFileTime.compareTo(secondFileTime);

            return result < 0;
        } catch (IOException e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Open the file for reading and tear down any open write stream. Only one
     * direction is ever active at a time, so opening for input closes the
     * output stream to avoid holding both handles to the same file. On failure
     * the input stream is left {@code null} so {@link #isOpen()} stays accurate.
     *
     * @author ClaudeCode
     */
    private void openInputStream() {
        try {
            inputStream = new FileInputStream(filePath.toRealPath().toString());
            if (outputStream != null) outputStream.close();
        } catch (IOException e) {
            logger.error("An exception has occurred: " + e.getMessage(), e);
            inputStream = null;
        }
    }

    /**
     * Open the file for writing and tear down any open read stream. As with
     * {@link #openInputStream()}, only one direction is active at a time.
     *
     * @param append when true the stream appends to existing content; when
     *               false the file is truncated/overwritten
     * @author ClaudeCode
     */
    private void openOutputStream(boolean append) {
        try {
            outputStream = new FileOutputStream(filePath.toRealPath().toString(), append);
            if (inputStream != null) inputStream.close();
        } catch (IOException e) {
            logger.error("An exception has occurred: " + e.getMessage(), e);
            outputStream = null;
        }
    }

    /**
     * Open up a file for reading or writing. It will open up a Stream, either input
     * or output, and then close down the other stream if it exists.
     * @param mode The mode to open this file up
     */
    public void open(FileModeEnum mode) {
        try {
            switch (mode) {
                case FileModeEnum.MODE_READ:
                    //option = StandardOpenOption.READ;
                    openInputStream();
                    break;
                case FileModeEnum.MODE_WRITE:
                    //option = StandardOpenOption.WRITE;
                    openOutputStream(false);
                    if (inputStream != null) inputStream.close();
                    break;
                case FileModeEnum.MODE_APPEND:
                    //option = StandardOpenOption.APPEND;
                    openOutputStream(true);
                    if (inputStream != null) inputStream.close();
                    break;
            }
        } catch (IOException e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
        }
    }

    /**
     * Skip a number of bytes in the open stream
     * @param bytesToSkip The number of bytes to skip
     * @return true if the bytes were successfully skipped, false otherwise
     */
    public boolean skipBytes(long bytesToSkip) {
        if (inputStream == null) return false;
        if (bytesToSkip < 0) return false;

        try {
            long skipped = inputStream.skip(bytesToSkip);
            if (skipped < bytesToSkip)
                return false;
        } catch (IOException e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * Close down the two streams. (but don't cross them...)
     * @return True if the streams were closed, false otherwise
     */
    public boolean close() {
        if (inputStream == null && outputStream == null) return true;

        try {
            if (inputStream == null) {
                outputStream.close();
                outputStream = null;
            } else {
                inputStream.close();
                inputStream = null;
            }
        } catch (IOException e) {
            logger.error("An exception has occurred: " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * Returns the next character in the input stream
     * @return The next character in the input stream
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
     * Write a character input as an integer to the outputStream
     * @param c the character to write
     * @return true if the character was written and no exception occurred, false otherwise
     */
    public boolean writeCharacter(int c) {
        if (outputStream == null) openOutputStream(false);

        try {
            outputStream.write(c);
        } catch (Exception e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * Read a number of bytes from the input stream
     * @param size The number of bytes to read
     * @return A string consisting of the read bytes, or a null string if an
     * Exception occurred
     */
    public String read(int size) {
        if (inputStream == null) return null;

        try {
            byte [] inputBytes = inputStream.readNBytes(size);
            return new String(inputBytes);
        } catch (IOException e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Attempt to append a string to the current outputStream.
     * @param toWrite the string to append to the stream
     * @return true if the string is correctly written, false if an error occurs
     */
    public boolean write(String toWrite) {
        if (outputStream == null)
            openOutputStream(true);

        try {
            outputStream.write(toWrite.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * Read a line from the inputStream
     * @return the line read from the stream or null if there was an Exception
     */
    public String getLine() {
        if (inputStream == null) return null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            return reader.readLine();
        } catch (Exception e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Write a line to the outputStream
     * @param line the line to write
     * @return true if the line was correctly written, false if an exception occurred
     */
    public boolean putLine(String line) {
        if (outputStream == null)
            openOutputStream(true);

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(line);
            writer.close();
            return true;
        } catch (Exception e) {
            logger.error("An exception has occured: " + e.getMessage(), e);
            return false;
        }
    }
}