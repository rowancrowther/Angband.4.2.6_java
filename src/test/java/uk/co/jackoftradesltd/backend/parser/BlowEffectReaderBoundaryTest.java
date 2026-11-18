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

package uk.co.jackoftradesltd.backend.parser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.monsters.BlowEffect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Boundary tests for {@link BlowEffectReader} itself, as distinct from the assembler behind it:
 * how the reader hands records on, and which of the three exits (a list, a
 * {@link ParseCancellationException}, an {@link IOException}) a file takes. Every file here uses
 * only {@code name:} lines, so the tests hold whatever the assembler does with the other
 * directives; {@code BlowEffectReaderTest} covers those end to end.
 *
 * <p>Expected values come from C's {@code finish_parse_eff} and {@code run_parse_eff}
 * ({@code mon-init.c}) and {@code parse_file_quit_not_found} ({@code datafile.c}): records come
 * back in file order, a file with no records is a clean load of nothing, and the first error ends
 * the load with nothing kept.
 *
 * <p>Class BlowEffectReaderBoundaryTest coded on 261116.
 *
 * @author Rowan Crowther
 */
class BlowEffectReaderBoundaryTest {

    private static final String PROJECTION_FILE = "lib/gamedata/projection.txt";

    private static Object savedProjections;

    /**
     * Loads the projection table into {@code WorldRegistry}. Every assembled record starts from
     * {@code new BlowEffect()}, whose default lash looks up the ACID projection, so a file of
     * {@code name:} lines needs the table too.
     *
     * <p>Function seed coded on 261118, commented in full on 261118.
     */
    @BeforeAll
    static void seed() throws Exception {
        List<Projection> projections = new ProjectionReader().parseWithResults(PROJECTION_FILE).items();
        Field field = RegistrySeeding.resolve("projections");
        field.setAccessible(true);
        savedProjections = field.get(null);
        field.set(null, projections);
    }

    /**
     * Puts the registry's projection list back as it was found.
     *
     * <p>Function restore coded on 261118, commented in full on 261118.
     */
    @AfterAll
    static void restore() throws Exception {
        Field field = RegistrySeeding.resolve("projections");
        field.setAccessible(true);
        field.set(null, savedProjections);
    }

    private static String write(Path dir, String text) throws IOException {
        Path file = dir.resolve("blow_effects.txt");
        Files.writeString(file, text);
        return file.toString();
    }

    private static List<String> names(String file) throws IOException {
        return new BlowEffectReader().parseWithResults(file).items().stream()
                .map(BlowEffect::getName).toList();
    }

    // ---- what a clean file gives -----------------------------------------

    @Test
    void anEmptyFileIsACleanLoadOfNothing(@TempDir Path dir) throws IOException {
        // C's finish_parse_eff counts zero records: blow_effects_max ends at 1, the blank slot only.
        assertEquals(List.of(), names(write(dir, "")));
    }

    @Test
    void aCommentOnlyFileIsACleanLoadOfNothing(@TempDir Path dir) throws IOException {
        assertEquals(List.of(), names(write(dir, "# File blow_effects.txt\n#\n\n# nothing else\n")));
    }

    @Test
    void recordsComeBackInFileOrderNotReverseOrder(@TempDir Path dir) throws IOException {
        // C's parse_eff_name pushes each record on the head of a list, so the list is in reverse;
        // finish_parse_eff fills the array from the top down to put it back in file order.
        assertEquals(List.of("C", "A", "B"), names(write(dir, "name:C\n\nname:A\n\nname:B\n")));
    }

    @Test
    void crlfLineEndingsGiveTheSameNames(@TempDir Path dir) throws IOException {
        assertEquals(List.of("A", "B"), names(write(dir, "name:A\r\n\r\nname:B\r\n")));
    }

    @Test
    void aMissingFinalNewlineStillYieldsTheLastRecord(@TempDir Path dir) throws IOException {
        assertEquals(List.of("A", "B"), names(write(dir, "name:A\nname:B")));
    }

    @Test
    void parseGivesAListTheCallerMayChange(@TempDir Path dir) throws IOException {
        // The records are copied out of the parse tree, so the list is the caller's to keep.
        List<BlowEffect> items = new BlowEffectReader().parse(write(dir, "name:A\n"));

        assertDoesNotThrow(() -> items.add(items.get(0)));
        assertEquals(2, items.size());
    }

    // ---- the first error ends the load ------------------------------------

    @Test
    void aSyntaxErrorAfterGoodRecordsKeepsNoneOfThem(@TempDir Path dir) throws IOException {
        // C quits on the first error, so a caller never sees the earlier records of a bad file.
        String file = write(dir, "name:A\n\nname:B\n\nbogus:1\n");

        assertThrows(ParseCancellationException.class, () -> new BlowEffectReader().parseWithResults(file));
        assertThrows(ParseCancellationException.class, () -> new BlowEffectReader().parse(file));
    }

    @Test
    void aLexerErrorEndsTheLoad(@TempDir Path dir) throws IOException {
        // power: wants digits; the text after it matches no token at all.
        String file = write(dir, "name:A\npower:abc\n");

        assertThrows(ParseCancellationException.class, () -> new BlowEffectReader().parseWithResults(file));
    }

    @Test
    void aSyntaxErrorBeyondARefusedRecordIsTheOneReported(@TempDir Path dir) throws IOException {
        // The documented difference from C. C reads line by line and stops at line 3, where
        // effect-type:theft makes resist: PARSE_ERROR_MISSING_BLOW_EFF_TYPE. The port parses the
        // whole file first, so the syntax error on line 5 is thrown before any record is assembled.
        String file = write(dir, "name:A\neffect-type:theft\nresist:POIS\n\nbogus:1\n");

        assertThrows(ParseCancellationException.class, () -> new BlowEffectReader().parseWithResults(file));
    }

    // ---- file handling ----------------------------------------------------

    @Test
    void aMissingFileIsAnIoExceptionFromBothEntryPoints(@TempDir Path dir) {
        // C: parse_file_quit_not_found() quits with "Cannot open 'blow_effects.txt'".
        String missing = dir.resolve("absent.txt").toString();

        assertThrows(IOException.class, () -> new BlowEffectReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new BlowEffectReader().parse(missing));
    }

    @Test
    void aDirectoryIsNotAReadableFile(@TempDir Path dir) {
        assertThrows(IOException.class, () -> new BlowEffectReader().parse(dir.toString()));
    }
}
