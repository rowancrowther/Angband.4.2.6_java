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

package uk.co.jackoftrades.frontend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftrades.frontend.screen.Window;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * What is left of {@code SwingUI}'s shutdown once stage 3 has taken the rest away.
 *
 * <p>{@link SwingUI#closeDown()} used to stop the game thread and call {@code System.exit}; it now
 * disposes windows and nothing else, and the two things it no longer does are as much the subject
 * here as the one it still does. The exit in particular is worth an assertion of a peculiar kind:
 * a {@code System.exit} anywhere on this path would take the test JVM with it, so these tests
 * completing at all is the evidence. Said plainly here because a passing test whose real assertion
 * is "the runner did not die" deserves to say so.
 *
 * <p>Building any {@code SwingUI} builds a {@code JFrame}, so everything here is skipped where
 * there is no display. Nothing is ever made visible - {@code pack()} realises the frame, which is
 * all {@code dispose()} needs to have something to undo.
 *
 * <p>{@code init} is not covered. It measures fonts, installs a core-side status display into a
 * global holder, and starts two threads, which is a wiring test needing a real front end rather
 * than a unit test; the traffic it sets up is covered from the other side, in
 * {@code UILoopTest} and {@code EnterInitWiringTest}.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SwingUITest {

    /**
     * Builds a front end with its window realised, so there is something for a disposal to do.
     *
     * <p>A {@code null} runner is deliberate: nothing on the shutdown path touches it any more,
     * and a test that passed a real one would hide it if that changed back.
     *
     * @author Rowan Crowther
     */
    private static SwingUI realisedFrontEnd() throws Exception {
        SwingUI swingUI = new SwingUI(null);

        SwingUtilities.invokeAndWait(() -> swingUI.getActiveWindow().pack());

        return swingUI;
    }

    @BeforeEach
    void requireADisplay() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: constructing SwingUI builds a JFrame");
    }

    /**
     * The window is disposed. This is the whole of what the method promises now, and the last step
     * of the shutdown handshake - by the time it runs, the core has already reported that it has
     * finished.
     *
     * @author Rowan Crowther
     */
    @Test
    void closeDownDisposesTheWindow() throws Exception {
        SwingUI swingUI = realisedFrontEnd();
        Window window = swingUI.getActiveWindow();

        assertTrue(window.isDisplayable(), "the fixture should start with a realised window");

        SwingUtilities.invokeAndWait(swingUI::closeDown);

        assertFalse(window.isDisplayable(), "closeDown must dispose the window");
    }

    /**
     * And the process survives it. With no {@code System.exit} on the path, disposing the last
     * window leaves the JVM to end on its own once no non-daemon thread remains - which is what
     * lets the core finish saving first.
     *
     * <p>The assertion is the line after the call being reached at all. There is no way to catch
     * an exit, so a regression here does not fail this test: it kills the test JVM, and the build
     * reports the whole worker as having disappeared. Odd-looking, but a genuine signal, and a
     * loud one.
     *
     * @author Rowan Crowther
     */
    @Test
    void closeDownDoesNotExitTheProcess() throws Exception {
        SwingUI swingUI = realisedFrontEnd();

        SwingUtilities.invokeAndWait(swingUI::closeDown);

        assertTrue(true, "reaching this line is the assertion: closeDown did not exit the JVM");
    }

    /**
     * Disposing twice is harmless. The handshake should deliver exactly one {@code STOPPED}, but a
     * shutdown path that only works once is a bad shutdown path - and this is cheap insurance
     * against the day something retries.
     *
     * @author Rowan Crowther
     */
    @Test
    void closeDownIsSafeToRepeat() throws Exception {
        SwingUI swingUI = realisedFrontEnd();
        Window window = swingUI.getActiveWindow();

        SwingUtilities.invokeAndWait(swingUI::closeDown);
        SwingUtilities.invokeAndWait(swingUI::closeDown);

        assertFalse(window.isDisplayable(), "the window should stay disposed");
    }
}