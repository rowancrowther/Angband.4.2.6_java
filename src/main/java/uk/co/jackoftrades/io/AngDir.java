package uk.co.jackoftrades.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AngDir {
    private final Path directoryPath;
    private final List<String> dirFiles;
    private String firstFileName;
    private boolean firstFileIsDirectory;
    private boolean onlyFiles;
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