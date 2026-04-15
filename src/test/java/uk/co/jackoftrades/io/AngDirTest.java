package uk.co.jackoftrades.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class AngDirTest {
    private AngDir angDir;
    private final String homeDirectory = System.getProperty("user.home");
    private final char pathSeparator = File.separatorChar;

    @BeforeEach
    void setUp() {
        angDir = new AngDir(homeDirectory);
    }

    @Test
    void alterOnlyFiles() {
        // ignore the first run through.
        angDir.alterOnlyFiles(true);
        boolean resultTrue = angDir.alterOnlyFiles(false);
        boolean resultFalse = angDir.alterOnlyFiles(false);

        assertAll(
                () -> assertTrue(resultTrue),
                () -> assertFalse(resultFalse)
        );
    }

    @Test
    void read() {
        final String readString = homeDirectory + pathSeparator + angDir.read();
        String firstFile = null;

        try {
            DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(homeDirectory));
            Iterator<Path> iterator = paths.iterator();

            while (iterator.hasNext() && (firstFile == null || firstFile.isEmpty())) {
                Path p = iterator.next();
                if (!Files.isDirectory(p))
                    firstFile = p.toString();
            }
        } catch (IOException e) {
            fail ("IOException " + e.getMessage() + " was thrown");
        }

        String finalFirstFile = firstFile;
        assertAll(
                () -> assertEquals(finalFirstFile, readString),
                () -> assertTrue(Files.exists(Paths.get(readString)))
        );
    }
}