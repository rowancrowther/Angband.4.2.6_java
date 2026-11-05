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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.ui.entrybase.assembler.UIEntryBaseAssembler;
import uk.co.jackoftradesltd.frontend.ui.entrybase.reader.UIEntryBaseReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the {@code ui_entry_base.txt} pipeline: text file → ANTLR lexer/parser →
 * {@link UIEntryBaseReader} →
 * {@link UIEntryBaseAssembler} → {@code TEMPLATE_ONLY}
 * {@link UIEntry} objects, wrapped in a {@link ParseResult}.
 *
 * <p>These drive real fixtures through the whole chain. Every error stops the load, as C's
 * {@code run_parser()} quits on the first one:
 * <ul>
 *   <li>a grammar or lexer error (a record missing a mandatory field) is thrown as a
 *       {@link ParseCancellationException};</li>
 *   <li>a record the assembler refuses (an unknown {@code renderer}, an illegal {@code combine})
 *       is thrown as a {@link DataFileParseException}, at the first bad record.</li>
 * </ul>
 * The renderer registry the assembler resolves against is seeded via reflection (see
 * {@link UIEntryBaseAssemblerTest} for the same rationale).
 *
 * <p>Class UIEntryBaseReaderTest coded before 261104, updated on 261104 for the removal of the
 * soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
class UIEntryBaseReaderTest {

    private static final String REAL_FILE = "lib/gamedata/ui_entry_base.txt";

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seedRenderers() throws Exception {
        List<UIEntryRenderer> renderers = new UIEntryRendererReader()
                .parseWithResults("lib/gamedata/ui_entry_renderer.txt").items();
        Field field = UIRegistry.class.getDeclaredField("uiEntryRenderers");
        field.setAccessible(true);
        field.set(null, renderers);
    }

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    /**
     * A full, well-formed record with a renderer known to the real registry.
     */
    private static String record(String name, String renderer, String combine) {
        return "name:" + name + "\nrenderer:" + renderer + "\ncombine:" + combine
                + "\ncategory:CHAR_SCREEN1\nflags:TIMED_AS_AUX\ndesc:hello\n";
    }

    // ---- happy path -------------------------------------------------------------------------

    @Test
    void cleanLoadOfTheRealFileResolvesAllTemplates() throws IOException {
        ParseResult<UIEntry> result = new UIEntryBaseReader().parseWithResults(REAL_FILE);

        assertEquals(3, result.items().size());

        UIEntry first = result.items().get(0);
        assertEquals("good_flag_ui_compact_0", first.getName());
        assertEquals(CombinerName.LOGICAL_OR, first.getCombineType());
        assertEquals("char_screen1_flag_renderer", first.getRenderer().getName());

        UIEntry last = result.items().get(2);
        assertEquals("numeric_as_sign_ui_0", last.getName());
        assertEquals(CombinerName.ADD, last.getCombineType());
    }

    // ---- assembler errors ---------------------------------------------------------------------

    /**
     * An unknown renderer stops the load, naming the renderer.
     *
     * <p>Function unknownRendererStopsTheLoad coded on 261104, commented in full on 261104. It
     * replaces unknownRendererIsLoggedAndRecordSkipped.
     */
    @Test
    void unknownRendererStopsTheLoad() throws IOException {
        String path = tempFile("bad-renderer.txt",
                record("x", "no_such_renderer", "LOGICAL_OR"));

        DataFileParseException thrown =
                assertThrows(DataFileParseException.class, () -> new UIEntryBaseReader().parseWithResults(path));

        assertTrue(thrown.getMessage().contains("no_such_renderer"), thrown.getMessage());
    }

    // ---- grammar errors -----------------------------------------------------------------------

    @Test
    void recordMissingMandatoryDescFailsClosed() throws IOException {
        // desc: is mandatory; a record that stops after flags cannot complete the fixed sequence.
        String path = tempFile("no-desc.txt",
                "name:x\nrenderer:char_screen1_flag_renderer\ncombine:LOGICAL_OR\n"
                        + "category:CHAR_SCREEN1\nflags:TIMED_AS_AUX\n");

        assertThrows(ParseCancellationException.class, () -> new UIEntryBaseReader().parseWithResults(path));
    }

    // ---- several distinct failures at once ---------------------------------------------------

    /**
     * With two bad records among good ones, the load stops at the first bad one, the unknown
     * renderer, and the later illegal {@code combine} is never reached.
     *
     * <p>Function theFirstOfSeveralFailuresStopsTheLoad coded on 261104, commented in full on 261104.
     * It replaces multipleDistinctFailuresAreLoggedAndGoodRecordsSurvive.
     */
    @Test
    void theFirstOfSeveralFailuresStopsTheLoad() throws IOException {
        String path = tempFile("mixed.txt", record("good_first", "char_screen1_flag_renderer", "LOGICAL_OR")
                + record("bad_renderer", "no_such_renderer", "LOGICAL_OR")
                + record("bad_combine", "char_screen1_flag_renderer", "BOGUS")
                + record("good_last", "char_screen1_numeric_as_sign_renderer", "ADD"));

        DataFileParseException thrown =
                assertThrows(DataFileParseException.class, () -> new UIEntryBaseReader().parseWithResults(path));

        assertTrue(thrown.getMessage().contains("no_such_renderer"), thrown.getMessage());
        assertFalse(thrown.getMessage().contains("BOGUS"), thrown.getMessage());
    }
}
