package uk.co.jackoftrades.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.io.enums.FileModeEnum;
import uk.co.jackoftrades.io.enums.FileTypeEnum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AngFileTest {
    private AngFile angFile;
    private final String homeDirectory = System.getProperty("user.home");
    private final char pathSeparator = File.separatorChar;
    private final String mainFile = homeDirectory + pathSeparator + "test.txt";
    private final Logger logger = LogManager.getLogger();

    @AfterEach
    void tearDown() {
        if (angFile == null) return;

        angFile.close();
        try {
            Files.deleteIfExists(Paths.get(mainFile));
        } catch (IOException e) {
            logger.error("Unable to delete file " + mainFile, e);
        }
    }

    @Test
    void open() {
        angFile = new AngFile(mainFile, FileModeEnum.MODE_READ);
        angFile.open(FileTypeEnum.FTYPE_TEXT);
        assertTrue(angFile.isOpen());
    }

    @Test
    void close() {
        angFile = new AngFile(mainFile, FileModeEnum.MODE_READ);
        boolean beforeOpen = angFile.isOpen();
        angFile.open(FileTypeEnum.FTYPE_TEXT);
        boolean afterOpen = angFile.isOpen();
        angFile.close();
        boolean afterClose = angFile.isOpen();

        assertAll(
                () -> assertFalse(beforeOpen),
                () -> assertTrue(afterOpen),
                () -> assertFalse(afterClose)
        );
    }

    @Test
    void fileSkip() {
        // This can't be tested atm. May need to implement this or not depending on the use of it.
    }

    @Test
    void readChar() {
        writeChar();
    }

    @Test
    void isOpen() {
        angFile = new AngFile(mainFile, FileModeEnum.MODE_READ);
        boolean afBeforeOpen = angFile.isOpen();
        angFile.open(FileTypeEnum.FTYPE_TEXT);
        boolean afAfterOpen = angFile.isOpen();

        assertAll(
                () -> assertFalse(afBeforeOpen),
                () -> assertTrue(afAfterOpen)
        );
    }

    @Test
    void writeChar() {
        char charToWrite = 'e';

        angFile = new AngFile(mainFile, FileModeEnum.MODE_WRITE);

        angFile.open(FileTypeEnum.FTYPE_TEXT);
        boolean writeResult = angFile.writeChar(charToWrite);
        angFile.close();
        angFile = new AngFile(mainFile, FileModeEnum.MODE_READ);

        boolean openResult = angFile.open(FileTypeEnum.FTYPE_TEXT);
        char result = (char) angFile.readChar();

        assertAll(
                () -> assertTrue(openResult),
                () -> assertTrue(writeResult),
                () -> assertEquals(charToWrite, result)
        );
    }

    @Test
    void read() {
        write();
    }

    @Test
    void write() {
        String testString = "This is a test string";
        angFile = new AngFile(mainFile, FileModeEnum.MODE_WRITE);
        angFile.open(FileTypeEnum.FTYPE_TEXT);
        boolean result = angFile.write(testString);
        angFile.close();

        angFile = new AngFile(mainFile, FileModeEnum.MODE_READ);
        angFile.open(FileTypeEnum.FTYPE_TEXT);
        String readString = angFile.read(testString.length());

        assertAll(
                () -> assertTrue(result),
                () -> assertEquals(testString, readString)
        );
    }

    @Test
    void getLine() {
        putLine();
    }

    @Test
    void putLine() {
        String line = "This is a long line to put in a file in order to test it.";
        angFile = new AngFile(mainFile, FileModeEnum.MODE_WRITE);
        angFile.open(FileTypeEnum.FTYPE_TEXT);
        boolean result = angFile.putLine(line);
        angFile.close();

        angFile = new AngFile(mainFile, FileModeEnum.MODE_READ);
        String readLine = angFile.getLine();

        assertAll(
                () -> assertTrue(result),
                () -> assertEquals(line, readLine)
        );
    }

    @Test
    void putFormattedLine() {
        String lineToFormat = "This is the $d line.";
        int intValue = 23;
        String expectedLine = String.format(lineToFormat, intValue);

        angFile = new AngFile(mainFile, FileModeEnum.MODE_WRITE);
        angFile.open(FileTypeEnum.FTYPE_TEXT);
        boolean result = angFile.putFormattedLine(lineToFormat, intValue);
        angFile.close();

        angFile = new AngFile(mainFile, FileModeEnum.MODE_READ);
        angFile.open(FileTypeEnum.FTYPE_TEXT);
        String readLine = angFile.getLine();

        assertAll(
                () -> assertTrue(result),
                () -> assertEquals(expectedLine, readLine)
        );
    }

    @Test
    void dirExists() {
        angFile = new AngFile(mainFile, FileModeEnum.MODE_WRITE);
        String fakeDirectory = homeDirectory + pathSeparator + UUID.randomUUID();

        assertAll(
                () -> assertTrue(angFile.dirExists(homeDirectory)),
                () -> assertFalse(angFile.dirExists(fakeDirectory))
        );
    }

    @Test
    void dirCreate() {
        angFile = new AngFile(mainFile, FileModeEnum.MODE_READ);
        String fakeDirectory = homeDirectory + pathSeparator + UUID.randomUUID();
        boolean result = angFile.dirCreate(fakeDirectory);

        boolean actualResult = Files.exists(Paths.get(fakeDirectory));

        assertAll(
                () -> assertTrue(result),
                () -> assertEquals(result, actualResult)
        );

        try {
            Files.delete(Paths.get(fakeDirectory));
        } catch (IOException e) {
            logger.error("Could not delete " + fakeDirectory, e);
        }
    }
}