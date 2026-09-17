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

package uk.co.jackoftradesltd.frontend.splash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.globals.Angband;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.frontend.SwingUI;
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Tests {@link SplashScreen} against the two halves of {@code show_splashscreen()}/
 * {@code splashscreen_note()} ({@code [C] src/ui-init.c}, {@code [C] src/ui-display.c}) it ports:
 * {@link SplashScreen#readAndParse} turning {@code news.txt} into a painted grid,
 * {@link SplashScreen#splashScreenNote(String, Screen)} bracketing and centring a progress note,
 * and {@link SplashScreen#splashScreenBirthNote(String)} stacking birth notes down the screen.
 *
 * <p>Expected values are worked out from the C source directly - {@code next_section}/
 * {@code text_out_e} ({@code [C] src/z-textblock.c}), {@code color_text_to_attr}
 * ({@code [C] src/z-color.c}), and {@code strnfmt} ({@code [C] src/z-form.c}) for the parsing
 * half, {@code format}/{@code Term_erase}/{@code Term_putstr} for the load-note half, and the
 * {@code ++y >= 24} wrap in {@code splashscreen_note}'s {@code MSG_BIRTH} branch for the birth-note
 * half - not from re-reading the port, so a bug shared between the two would still show up here.
 *
 * <p>Constructing a {@link SplashScreen} means constructing a {@link SwingUI}, the same reason
 * {@code JPanelAreaPutTest} needs a display; the {@code readAndParse}/{@code splashScreenNote}
 * tests below never touch the window that builds, but there is no other way to obtain a
 * {@code SplashScreen} to call them on.
 *
 * <p>Class SplashScreenTest coded on 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
class SplashScreenTest {

    @TempDir
    Path tempDir;

    private static SplashScreen newSplashScreen() {
        return new SplashScreen(new SwingUI(null, null, null));
    }

    /**
     * Reads row {@code row} of {@code grid}, columns {@code 0} to {@code grid.cols() - 1}, as a
     * plain string - blank cells (still {@code null}, never written) come back as a space so a
     * freshly built grid reads the same as one filled with literal spaces.
     */
    private static String rowText(CellGrid grid, int row) {
        StringBuilder text = new StringBuilder();
        for (int col = 0; col < grid.cols(); col++) {
            AngbandDisplayCharacter cell = grid.get(row, col);
            text.append(cell == null ? ' ' : cell.getCharacter());
        }
        return text.toString();
    }

    private static void resetBirthLine() throws Exception {
        Field field = SplashScreen.class.getDeclaredField("birthLine");
        field.setAccessible(true);
        field.set(null, 2);
    }

    private static AngbandDisplayCharacter[][] panelDisplay(SwingUI.JPanelArea area) throws Exception {
        Field field = SwingUI.JPanelArea.class.getDeclaredField("display");
        field.setAccessible(true);
        return (AngbandDisplayCharacter[][]) field.get(area);
    }

    private static String stringFrom(AngbandDisplayCharacter[][] display, int row, int length) {
        StringBuilder text = new StringBuilder();
        for (int col = 0; col < length; col++) {
            text.append(display[row][col].getCharacter());
        }
        return text.toString();
    }

    @BeforeEach
    void needsADisplay() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: constructing a SplashScreen constructs the SwingUI enclosing it");
    }

    private Path newsFile(String content) throws IOException {
        Path file = tempDir.resolve("news.txt");
        Files.writeString(file, content);
        return file;
    }

    // readAndParse() ports the parsing half of show_splashscreen() ([C] src/ui-init.c): each line
    // of news.txt is scanned for {colour}...{/} spans (next_section/text_out_e semantics,
    // [C] src/z-textblock.c) and a $VERSION marker (replaced via strnfmt("%-8s", buildver),
    // [C] src/z-form.c), and painted from row 0, column 0 (Term_gotoxy(0, (Term->hgt-23)/5) == 0
    // for a 24-row terminal).

    @Test
    @DisplayName("readAndParse() paints plain text in white starting at row 0, column 0")
    void readAndParsePaintsPlainTextInWhite() throws IOException {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        Path path = newsFile("Hello");

        newSplashScreen().readAndParse(path, screen);

        assertEquals("Hello" + " ".repeat(75), rowText(live, 0));
        assertEquals(ColourEnum.COLOUR_WHITE, live.get(0, 0).getAttributeColour());
    }

    @Test
    @DisplayName("readAndParse() colours text inside a {colour}...{/} span, matching color_text_to_attr")
    void readAndParseColoursTaggedSpan() throws IOException {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        Path path = newsFile("{red}Hi{/}there");

        newSplashScreen().readAndParse(path, screen);

        assertEquals("Hithere" + " ".repeat(73), rowText(live, 0));
        assertEquals(ColourEnum.COLOUR_RED, live.get(0, 0).getAttributeColour());
        assertEquals(ColourEnum.COLOUR_RED, live.get(0, 1).getAttributeColour());
        // "{/}" reverts to white, as C's text_out_e does for text outside any tag span.
        assertEquals(ColourEnum.COLOUR_WHITE, live.get(0, 2).getAttributeColour());
    }

    @Test
    @DisplayName("readAndParse() defaults an unrecognised colour name to white, matching color_text_to_attr")
    void readAndParseUnrecognisedColourDefaultsToWhite() throws IOException {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        Path path = newsFile("{nosuchcolour}X{/}");

        newSplashScreen().readAndParse(path, screen);

        // color_text_to_attr() ([C] src/z-color.c) returns COLOUR_WHITE for any name it doesn't
        // recognise, rather than rejecting the line.
        assertEquals(ColourEnum.COLOUR_WHITE, live.get(0, 0).getAttributeColour());
    }

    @Test
    @DisplayName("readAndParse() replaces $VERSION with the version string padded to eight characters")
    void readAndParseReplacesVersionTag() throws IOException {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        Path path = newsFile("$VERSION");

        newSplashScreen().readAndParse(path, screen);

        // strnfmt(version_marker, ..., "%-8s", buildver) ([C] src/z-form.c) left-pads "4.2.6" to
        // eight characters with trailing spaces.
        assertEquals(String.format("%-8s", Angband.versionString) + " ".repeat(72), rowText(live, 0));
    }

    @Test
    @DisplayName("readAndParse() discards the rest of the line after $VERSION, matching strnfmt's truncation")
    void readAndParseDiscardsTextAfterVersionTag() throws IOException {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        Path path = newsFile("$VERSIONtrailing");

        newSplashScreen().readAndParse(path, screen);

        // strnfmt() null-terminates the C buffer right after the 8-character substitution, so
        // "trailing" is never seen by text_out_e - it is discarded, not printed after the version
        // stamp the way the raw source line would suggest.
        assertEquals(String.format("%-8s", Angband.versionString) + " ".repeat(72), rowText(live, 0));
    }

    @Test
    @DisplayName("readAndParse() stops after 24 rows, matching the term's height")
    void readAndParseStopsAfter24Rows() throws IOException {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            lines.append("row").append(i).append('\n');
        }
        Path path = newsFile(lines.toString());

        newSplashScreen().readAndParse(path, screen);

        assertEquals("row23" + " ".repeat(75), rowText(live, 23));
    }

    @Test
    @DisplayName("readAndParse() clips a line wider than 80 columns instead of throwing")
    void readAndParseClipsLineWiderThan80Columns() throws IOException {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        Path path = newsFile("x".repeat(90));

        newSplashScreen().readAndParse(path, screen);

        assertEquals("x".repeat(80), rowText(live, 0));
    }

    // splashScreenNote() ports the non-birth branch of splashscreen_note() ([C] src/ui-display.c):
    // format("[%s]", msg), erase row (Term->hgt - 23) / 5 + 23 (== 23 for the standard 24-row
    // terminal this port assumes), then Term_putstr the bracketed string centred on
    // (Term->wid - strlen(s)) / 2, in COLOUR_WHITE.

    @Test
    @DisplayName("splashScreenNote() brackets the message and centres it on row 23")
    void splashScreenNoteBracketsAndCentresTheMessage() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());

        newSplashScreen().splashScreenNote("Initializing", screen);

        assertEquals(" ".repeat(33) + "[Initializing]" + " ".repeat(33), rowText(live, 23));
        assertEquals(ColourEnum.COLOUR_WHITE, live.get(23, 33).getAttributeColour());
    }

    @Test
    @DisplayName("splashScreenNote() erases the previous contents of row 23 before writing")
    void splashScreenNoteErasesRowBeforeWriting() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        screen.root().put(23, 5, 'Q', ColourEnum.COLOUR_RED);

        newSplashScreen().splashScreenNote("Initializing", screen);

        // Column 5 falls outside "[Initializing]"'s centred span (columns 33-46), so surviving
        // there would mean the erase - Term_erase(0, y, 255) in C - never happened.
        assertEquals(' ', live.get(23, 5).getCharacter());
    }

    @Test
    @DisplayName("splashScreenNote() leaves other rows untouched")
    void splashScreenNoteLeavesOtherRowsUntouched() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        screen.root().put(2, 0, 'Y', ColourEnum.COLOUR_WHITE);

        newSplashScreen().splashScreenNote("Initializing", screen);

        assertEquals('Y', live.get(2, 0).getCharacter());
        assertNull(live.get(22, 33));
    }

    @Test
    @DisplayName("splashScreenNote() clips a message too wide for the row instead of throwing")
    void splashScreenNoteClipsAnOverwideMessage() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());

        // Regression test: before this session's fix, this method wrote straight into a raw
        // display[][] array with no bounds checking, so a message this wide drove the centring
        // column negative and threw ArrayIndexOutOfBoundsException instead of clipping the way
        // C's Term_putstr does.
        newSplashScreen().splashScreenNote("x".repeat(88), screen);

        assertEquals("x".repeat(80), rowText(live, 23));
    }

    // splashScreenBirthNote() ports the MSG_BIRTH branch of splashscreen_note()
    // ([C] src/ui-display.c): prt(msg, y, 0) writes from column 0, uncentred and unerased, and
    // if (++y >= 24) y = 2 wraps the row counter after row 23 - rows 2 through 23 inclusive, the
    // same 22 rows this method's own static birthLine counter walks.

    @Test
    @DisplayName("splashScreenBirthNote() writes the message from column 0 on row 2 first, uncentred")
    void splashScreenBirthNoteWritesFromColumnZero() throws Exception {
        resetBirthLine();
        SwingUI swingUI = new SwingUI(null, null, null);
        swingUI.getActiveWindow().add(swingUI.new JPanelArea());

        new SplashScreen(swingUI).splashScreenBirthNote("Rolling stats");

        AngbandDisplayCharacter[][] display = panelDisplay(swingUI.getActiveWindow().getArea());
        assertEquals("Rolling stats", stringFrom(display, 2, "Rolling stats".length()));
    }

    @Test
    @DisplayName("splashScreenBirthNote() advances one row per call, matching C's ++y")
    void splashScreenBirthNoteAdvancesOneRowPerCall() throws Exception {
        resetBirthLine();
        SwingUI swingUI = new SwingUI(null, null, null);
        swingUI.getActiveWindow().add(swingUI.new JPanelArea());
        SplashScreen splashScreen = new SplashScreen(swingUI);

        splashScreen.splashScreenBirthNote("one");
        splashScreen.splashScreenBirthNote("two");

        AngbandDisplayCharacter[][] display = panelDisplay(swingUI.getActiveWindow().getArea());
        assertEquals("one", stringFrom(display, 2, 3));
        assertEquals("two", stringFrom(display, 3, 3));
    }

    @Test
    @DisplayName("splashScreenBirthNote() wraps from row 23 back to row 2, matching C's if (++y >= 24) y = 2")
    void splashScreenBirthNoteWrapsAtTwentyFour() throws Exception {
        resetBirthLine();
        SwingUI swingUI = new SwingUI(null, null, null);
        swingUI.getActiveWindow().add(swingUI.new JPanelArea());
        SplashScreen splashScreen = new SplashScreen(swingUI);

        // Rows 2 through 23 inclusive is 22 rows; the 22nd call lands on row 23, and the 23rd
        // wraps back to row 2 - matching C exactly. (An earlier version of this method's own
        // Javadoc claimed row 23 was never reached; it always was, in C and here.)
        for (int i = 0; i < 21; i++) {
            splashScreen.splashScreenBirthNote("filler");
        }
        splashScreen.splashScreenBirthNote("last");
        splashScreen.splashScreenBirthNote("wrapped");

        AngbandDisplayCharacter[][] display = panelDisplay(swingUI.getActiveWindow().getArea());
        assertEquals("last", stringFrom(display, 23, "last".length()));
        assertEquals("wrapped", stringFrom(display, 2, "wrapped".length()));
    }
}
