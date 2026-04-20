package uk.co.jackoftrades.io.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.background.io.enums.FileTypeEnum;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"GrazieInspectionRunner", "SpellCheckingInspection"})
class FileTypeEnumTest {
    private final FileTypeEnum text  = FileTypeEnum.FTYPE_TEXT;
    private final FileTypeEnum save  = FileTypeEnum.FTYPE_SAVE;
    private final FileTypeEnum raw   = FileTypeEnum.FTYPE_RAW;
    private final FileTypeEnum html  = FileTypeEnum.FTYPE_HTML;
    private final FileTypeEnum fnull = FileTypeEnum.FTYPE_NULL;
    private ArrayList<FileTypeEnum> allValues;

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();
        allValues.add(text);
        allValues.add(save);
        allValues.add(raw);
        allValues.add(html);
        allValues.add(fnull);
    }

    @Test
    void values() {
        FileTypeEnum [] values = FileTypeEnum.values();

        for (FileTypeEnum type : values) {
            if (!allValues.contains(type)) {
                fail(type.toString() + " not found in created ArrayList.");
            }
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(raw,   FileTypeEnum.valueOf("FTYPE_RAW")),
                () -> assertEquals(text,  FileTypeEnum.valueOf("FTYPE_TEXT")),
                () -> assertEquals(html,  FileTypeEnum.valueOf("FTYPE_HTML")),
                () -> assertEquals(save,  FileTypeEnum.valueOf("FTYPE_SAVE")),
                () -> assertEquals(fnull, FileTypeEnum.valueOf("FTYPE_NULL"))
        );
    }
}