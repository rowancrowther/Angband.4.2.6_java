package uk.co.jackoftrades.background.io;

import uk.co.jackoftrades.background.io.enums.FileModeEnum;
import uk.co.jackoftrades.background.io.enums.FileTypeEnum;

import java.nio.file.Files;
import java.nio.file.Paths;

public class AngFile {
    private final FileModeEnum mode;
    private final FileHandler fileHandler;
    private final String filename;

    /**
     * Constructor
     * @param filename the name of the file
     * @param mode the mode the file is to be dealt with, i.e. Write only...
     */
    public AngFile(String filename, FileModeEnum mode) {
        this.mode = mode;
        this.filename = filename;
        fileHandler = new FileHandler(filename);
    }

    /**
     * Open up the file associated with this AngFile.
     * @param type the FiileType that this file should
     *             be opened as
     * @return true if the file was opened successfully,
     *         false otherwise.
     */
    public boolean open(FileTypeEnum type) {
        if (fileHandler.fileExists()) {
            fileHandler.open(mode);
        } else {
            if (!fileHandler.createFile())
                    return false;
            fileHandler.open(mode);
        }

        return true;
    }

    /**
     * Close the file - not needed in Java as the file is only open when a stream is attached to it. Pass this
     * through to the FileHandler to get it to close any open streams.
     * @return True
     */
    public boolean close() {
        return fileHandler.close();
    }

    /**
     * Skip a particular number of bytes (in a binary file)
     * @param bytesToSkip The number of bytes to skip
     * @return True if the skip was successful and didn't go past the end of the file, false otherwise.
     */
    public boolean fileSkip(long bytesToSkip) {
        return fileHandler.skipBytes(bytesToSkip);
    }

    /**
     * Gets the next character in the stream of this file
     * @return The next character (as an int) in the stream of this file
     */
    public int readChar() {
        if (!fileHandler.isOpen())
            fileHandler.open(FileModeEnum.MODE_READ);

        return fileHandler.readCharacter();
    }

    /**
     * Determines whether this AngFile is open or not
     * @return Whether the FileHandler attached to this AngFile is open or not
     */
    public boolean isOpen() {
        return fileHandler.isOpen();
    }

    /**
     * Attempt to write a single character to a file.
     * @param c the character to write
     * @return true if the character was written, false if an exception occurred
     */
    public boolean writeChar(int c) {
        if (!isOpen())
            fileHandler.open(FileModeEnum.MODE_WRITE);

        return fileHandler.writeCharacter(c);
    }

    /**
     * Read a string of a given size from the file
     * @param size The number of bytes to read
     * @return A string containing the next size of bytes from the file, or
     * a null value if an Exception occurred.
     */
    public String read(int size) {
        if (!isOpen()) fileHandler.open(FileModeEnum.MODE_READ);

        return fileHandler.read(size);
    }

    /**
     * Append a string to a file
     * @param toWrite The string to append
     * @return true if the string was written, false if an error occurred
     */
    public boolean write(String toWrite) {
        return fileHandler.write(toWrite);
    }

    /**
     * Gets a line of text upto a new line/carrage return from the input stream and returns it.
     * @return The line of text that was read
     */
    public String getLine() {
        if (!fileHandler.isOpen())
            fileHandler.open(FileModeEnum.MODE_READ);

        return fileHandler.getLine();
    }

    /**
     * Write a line to this file
     * @param line the line to write
     * @return true if the line was written, false otherwise
     */
    public boolean putLine(String line) {
        return fileHandler.putLine(line);
    }

    /**
     * Send a string to be formatted to the file outputStream
     * @param line The line to be formatted based on the args, and then written to the outputStream
     * @param args The arguments to the format
     * @return true if the line was written, false otherwise
     */
    public boolean putFormattedLine(String line, Object...args) {
        String formattedString = String.format(line, args);
        return putLine(formattedString);
    }

    /**
     * Returns whether a given directory path exists
     * @param path The directory path
     * @return true if the directory path exists, false otherwise
     */
    public boolean dirExists(String path) {
        return Files.exists(Paths.get(path));
    }

    /**
     * Create a given directory, returning whether it was created or not
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