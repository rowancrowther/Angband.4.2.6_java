package uk.co.jackoftrades.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.io.enums.FileModeEnum;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Check this works after we have more in place - currently it is throwing a "The process cannot access the file
//       because it is being used by another process" error. Despite this, all the tests pass.

class FileHandlerTest {
    FileHandler handler;
    final char fileSeparator = File.separatorChar;
    final String homeDirectory = System.getProperty("user.home");
    String path = homeDirectory + fileSeparator + "Test.txt";
    String movedPath = homeDirectory + fileSeparator + "Moved.txt";
    int inputByte = 27;
    int outputByte = 27;
    Logger logger = LogManager.getLogger();

//    private void open(FileModeEnum mode) {
//        handler = new FileHandler(path);
//        handler.open(mode);
//    }

    @BeforeEach
    void setUp() {
        handler = new FileHandler(path);

        try {
            //handler.close();
            if (Files.exists(Paths.get(path))) Files.delete(Paths.get(path));
            File file = new File(path);
            file.createNewFile();
            if (Files.exists(Paths.get(movedPath))) Files.delete(Paths.get(movedPath));
        } catch (Exception e) {
            logger.error("An error has occurred: " + e.getMessage(), e);
        }
    }

    @AfterEach
    public void tearDown() {
        if (Files.exists(Paths.get(path))) {
            handler.close();
            try {
                Files.delete(Paths.get(path));
            } catch (IOException e) {
                logger.error("An error has occurred: " + e.getMessage(), e);
            }
        }
    }

    @Test
    void isOpen() {
        handler.open(FileModeEnum.MODE_READ);

        assertTrue(handler.isOpen());
    }

    @Test
    void delete() {
        boolean created = Files.exists(Paths.get(path));
        handler.delete();
        boolean deleted = !Files.exists(Paths.get(path));

        assertAll(
                () -> assertTrue(created),
                () -> assertTrue(deleted)
        );
    }

    @Test
    void fileMove() {
        // open(FileModeEnum.MODE_WRITE);
        boolean result = handler.fileMove(Paths.get(movedPath));
        boolean pathMoved = Files.exists(Paths.get(path));
        boolean movedExists = Files.exists(Paths.get(movedPath));

        assertAll(
                () -> assertTrue(result),
                () -> assertFalse(pathMoved),
                () -> assertTrue(movedExists)
        );
    }

    @Test
    void fileExists() {
        assertEquals(Files.exists(Paths.get(path)), handler.fileExists());
    }

    /**
     * I don't think this is a correct implementation, but we will test what the C looked like anyway
     */
    @Test
    void fileGetSavefile() {
        assertEquals("Filesave.sav", handler.fileGetSavefile("Filesave", "sav"));
    }

    @Test
    void fileNewer() {
        // Create the newer file
        File newerFile = null;
        try {
            newerFile = Files.createFile(Paths.get(movedPath)).toFile();
        } catch (IOException e) {
            logger.error("An error has occurred: " + e.getMessage());
        }

        assertTrue(handler.fileNewer(path, movedPath));

        try {
            newerFile.delete();
        } catch (Exception e) {
            logger.error("An error has occurred: " + e.getMessage(), e);
        }
    }

    @Test
    void open() {
        handler.open(FileModeEnum.MODE_READ);
        boolean isOpen = handler.isOpen();
        handler.close();
        boolean isClose = handler.isOpen();

        assertAll(
                () -> assertTrue(isOpen),
                () -> assertFalse(isClose)
        );
    }

    @Test
    void skipBytes() {
        // Cannot at present test this
        // TODO: Work out where this is used and whether it is going to be continued
        //       to be used going in the Java version of this
        assertAll(
                () -> assertFalse(handler.skipBytes(1)),
                () -> assertFalse(handler.skipBytes(-1))
        );
    }

    @Test
    void close() {
        open();
    }

    @Test
    void readCharacter() {
        handler.close();

        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            outputStream.write(outputByte);
            outputStream.close();
        } catch (IOException e) {
            logger.error("An error has occurred: " + e.getMessage());
        }

        handler.open(FileModeEnum.MODE_READ);

        assertEquals(outputByte, handler.readCharacter());
    }

    @Test
    void writeCharacter() {
        int actualInputByte = 63;
        handler.close();

        handler.open(FileModeEnum.MODE_WRITE);
        handler.writeCharacter(inputByte);
        handler.close();

        try {
            FileInputStream inputStream = new FileInputStream(path);
            actualInputByte = inputStream.read();
        } catch (IOException e) {
            logger.error("An error has occurred: " + e.getMessage());
        }

        assertEquals(inputByte, actualInputByte);
    }

    @Test
    void read() {
        handler.close();

        String outputString = "Long Output Values";
        byte [] outputValues = outputString.getBytes();

        try {
            OutputStream outputStream = new FileOutputStream(path);
            outputStream.write(outputValues);
            outputStream.close();
        } catch (IOException e) {
            logger.error("An error has occurred: " + e.getMessage());
        }

        handler.open(FileModeEnum.MODE_READ);
        String returnedValue = handler.read(outputString.length());

        handler.close();
        handler.open(FileModeEnum.MODE_READ);
        String fourValues = handler.read(4);

        assertAll(
                () -> assertEquals(new String(outputValues), returnedValue),
                () -> assertEquals(outputString.substring(0, 4), fourValues)
        );
    }

    @Test
    void write() {
        String stringToAppend = "Appended String";
        byte[] toWrite = stringToAppend.getBytes();
        byte[] readBytes;
        String readString = "";
        OutputStream outputStream;
        InputStream inputStream;
        final String appendString = "Append here:";
        int length = 0;
        handler.close();

        try {
            outputStream = new FileOutputStream(path);
            byte[] stringToWrite = appendString.getBytes();
            outputStream.write(stringToWrite);
            outputStream.close();
        } catch (Exception e) {
            logger.error("An error has occurred: " + e.getMessage());
        }

        handler.open(FileModeEnum.MODE_APPEND);
        boolean result = handler.write(new String(toWrite));
        handler.close();

        try {
            inputStream = new FileInputStream(path);
            length = appendString.length() + stringToAppend.length();
            readBytes = inputStream.readNBytes(length);
            readString = new String(readBytes);
        } catch (Exception e) {
            logger.error("An error has occurred: " + e.getMessage());
            readBytes = null;
        }

        String resultantString = readString;

        byte[] finalReadBytes = readBytes;
        int finalLength = length;

        handler.open(FileModeEnum.MODE_READ);
        assertAll(
                () -> assertTrue(result),
                () -> assertNotNull(finalReadBytes),
                () -> assertEquals(resultantString, handler.read(finalLength))
        );
    }

    @Test
    void getLine() {
        String stringToPut = "Read this line";
        handler.close();

        try {
            OutputStream outputStream = new FileOutputStream(path, false);
            PrintStream writer = new PrintStream(outputStream);
            writer.println(stringToPut);
            writer.close();
        } catch (Exception e) {
            logger.error("An error has occurred: " + e.getMessage());
        }

        handler.open(FileModeEnum.MODE_READ);
        String result = handler.getLine();

        assertEquals(stringToPut, result);
    }

    @Test
    void putLine() {
        String stringToPut = "Write this line";
        String readLine;
        handler.close();
        handler.open(FileModeEnum.MODE_WRITE);
        handler.putLine(stringToPut);

        try {
            InputStream inputStream = Files.newInputStream(Paths.get(path));
            BufferedReader reader = new BufferedReader(new InputStreamReader((inputStream)));
            readLine = reader.readLine();
        } catch (Exception e) {
            logger.error("An error has occurred: " + e.getMessage());
            readLine = "";
        }

        String finalReadLine = readLine;

        assertEquals(stringToPut, finalReadLine);
    }
}