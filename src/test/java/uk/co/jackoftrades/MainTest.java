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

package uk.co.jackoftrades;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftrades.channel.Channels;
import uk.co.jackoftrades.channel.EDTChannel;
import uk.co.jackoftrades.channel.StartupOptions;
import uk.co.jackoftrades.channel.UIChannel;
import uk.co.jackoftrades.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftrades.channel.enums.UILifecycleEvent;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.channel.messages.UIMessage;
import uk.co.jackoftrades.channel.directories.AngbandDirs;

import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Unit tests for {@code Main.checkDirectoryOption}, the validation behind the {@code -d<dir>=<path>}
 * switch - C's rewriting of an {@code ANGBAND_DIR_*} buffer in {@code main()}
 * ({@code [C] src/main.c}).
 *
 * <p>These exist because the three rejection branches were previously untestable. The check used to
 * live inline in {@code main}, ending each failure in {@code System.exit(1)}, so any test that
 * reached one took the test worker down with it. Splitting the decision (this method, which returns
 * a message) from the consequence ({@code main}, which logs, prints and exits) is what makes them
 * reachable, and these tests are the reason that split exists.
 *
 * <p>The method is a pure predicate over a string - it reads {@code ANGBAND_DIRS} and the file
 * system but writes nothing - so nothing here needs setup or teardown. {@link #aValidOptionChangesNothing}
 * pins that, since it is the property the separation depends on.
 *
 * <p>Several of these pin the behaviour of {@link String#split(String, int)} at the edges rather than anything
 * the port chose. That is deliberate: the arity check is the only thing standing between a
 * malformed argument and an {@code ArrayIndexOutOfBoundsException} two lines later in
 * {@code main}. The split takes a limit of 2, which changes those edges considerably from the
 * unlimited form - a trailing empty is kept rather than dropped, so {@code -dsave=} now reaches
 * the path check with an empty path instead of failing on arity, and a path containing
 * {@code '='} survives intact rather than splitting into three. Both are pinned below.
 *
 * @author Rowan Crowther
 */
class MainTest {

    // ---- accepted ---------------------------------------------------------

    /**
     * A well-formed override of a known directory onto a path that exists is accepted, and says so
     * by returning {@code null} rather than an empty string.
     *
     * @param tempDir a directory that certainly exists, supplied and removed by JUnit
     */
    @Test
    void aWellFormedOverrideOfAKnownDirectoryIsAccepted(@TempDir Path tempDir) {
        assertNull(Main.checkDirectoryOption("-dsave=" + tempDir),
                "a known name and an existing path is the whole of what -d requires");
    }

    /**
     * Every name in {@link AngbandDirs.ANGBAND_DIRS} is accepted on the command line.
     *
     * <p>This is the test that would have caught the drift the {@code AngbandDirs} class comment
     * describes, where one directory was spelled {@code "archives"} in the accepting switch and
     * {@code "archive"} in the enum, so that override was taken and silently dropped. Driving the
     * check from {@code values()} means a new directory is covered by existing.
     *
     * <p>Matches on {@link AngbandDirs.ANGBAND_DIRS#getName()}, never {@code name()} - the two
     * differ for {@code PREF}, which is written {@code "pref"} and points at the customise
     * directory.
     *
     * @param dir     the directory constant under test
     * @param tempDir a directory that certainly exists, supplied and removed by JUnit
     */
    @ParameterizedTest
    @EnumSource(AngbandDirs.ANGBAND_DIRS.class)
    void everyKnownDirectoryNameIsAcceptedOnTheCommandLine(AngbandDirs.ANGBAND_DIRS dir,
                                                           @TempDir Path tempDir) {
        assertNull(Main.checkDirectoryOption("-d" + dir.getName() + "=" + tempDir),
                dir.getName() + " is a real directory name and must be accepted");
    }

    /**
     * Checking an option does not apply it. The method reports and returns; {@code main} is what
     * acts on the answer.
     *
     * <p>The separation is the point of the extraction, and it is also what lets every other test
     * here run without teardown - {@code ANGBAND_DIRS} constants are process-wide and mutable, so a
     * check that quietly repointed one would leak into every test that ran afterwards in the same
     * JVM.
     *
     * @param tempDir a directory that certainly exists, supplied and removed by JUnit
     */
    @Test
    void aValidOptionChangesNothing(@TempDir Path tempDir) {
        String before = AngbandDirs.ANGBAND_DIRS.SAVE.getPath();

        Main.checkDirectoryOption("-dsave=" + tempDir);

        assertEquals(before, AngbandDirs.ANGBAND_DIRS.SAVE.getPath(),
                "checkDirectoryOption must not apply the override it is validating");
    }

    // ---- rejected: malformed ---------------------------------------------

    /**
     * An argument that does not split into exactly a name and a path is rejected, and the message
     * quotes what was typed and the shape that was wanted.
     *
     * <p>With a limit of 2 there is only one way to come back with fewer than two parts: no
     * {@code '='} anywhere. {@code -dsave} is that case with a name, {@code -d} is that case with
     * nothing at all - the substring is empty, and splitting an empty string yields one empty part
     * rather than none. Either would index past the end of the array in {@code main} if this check
     * let it through.
     *
     * <p>The two arguments that used to be here, {@code -dsave=} and {@code -d=}, no longer fail
     * arity: the limit keeps the trailing empty, so both yield two parts and fall through. They are
     * caught one line later instead, by the empty-path guard - see
     * {@link #anEmptyPathIsRejectedRatherThanTakenAsTheWorkingDirectory}.
     *
     * <p>Asserting on the quoted argument rather than the whole sentence: the contract worth pinning
     * is that the player is told what they typed, not the exact wording around it.
     *
     * @param arg the malformed argument
     */
    @ParameterizedTest
    @ValueSource(strings = {"-dsave", "-d"})
    void anArgumentThatIsNotNameEqualsPathIsRejected(String arg) {
        String message = Main.checkDirectoryOption(arg);

        assertNotNull(message, arg + " does not split into a name and a path");
        assertTrue(message.contains("'" + arg + "'"),
                "the message must quote what was actually typed, so it can be corrected: " + message);
        assertTrue(message.contains("-d<dir>=<path>"),
                "the message must show the shape that was wanted: " + message);
    }

    /**
     * A path containing an equals sign is accepted, because the split takes a limit of 2 and so
     * stops at the first separator.
     *
     * <p>This is the behaviour C has - it splits on the first {@code '='} and takes the rest of the
     * argument as the path - and it is a reversal of what this file pinned until 2026-08-10, when
     * the unlimited split rejected such an argument as having three parts. Such paths are legal on
     * every file system the game runs on, so accepting them is the better answer as well as the
     * matching one.
     *
     * <p>Note that {@code main} itself still splits without a limit before calling
     * {@code AngbandDirs.setDirectory}, so a path of this shape passes validation and is then
     * truncated at the {@code '='} on the way in. That is a live bug in {@code Main}, not in the
     * method under test, and this test does not cover it.
     *
     * @param tempDir a directory that certainly exists, supplied and removed by JUnit
     * @throws IOException if the temporary directory cannot be created
     */
    @Test
    void aPathContainingAnEqualsSignIsAccepted(@TempDir Path tempDir) throws IOException {
        Path awkward = Files.createDirectory(tempDir.resolve("a=b"));

        assertNull(Main.checkDirectoryOption("-dsave=" + awkward),
                "the limit of 2 stops at the first '=', so the rest is the path: " + awkward);
    }

    /**
     * An empty path is rejected outright, rather than resolving to the working directory.
     *
     * <p>A regression test for a hole that was open for part of 2026-08-10, between the split
     * gaining its limit of 2 and the {@code dirs[1].isEmpty()} guard being added. The unlimited
     * split had dropped the trailing empty, so {@code -dsave=} came back with one part and was
     * caught on arity; keeping it meant two parts, a name that checked out, and a path of
     * {@code ""}. The path check passed anyway, because {@code Paths.get("").toFile().isDirectory()}
     * is <em>true</em> - Java resolves the empty abstract pathname against the process working
     * directory, so the question being asked was "is the working directory a directory", which it
     * always is. {@code -dsave=} silently installed the launch directory as the save directory.
     *
     * <p>That is the {@link #aPlainFileIsRejectedWhereADirectoryIsRequired} failure mode again, and
     * worse for being plausible to type: the mistake surfaces later and elsewhere, when a loader
     * opens a file under the wrong root. Nothing about the resulting {@code File} distinguishes the
     * case, which is why the guard has to run before {@code Paths.get} is ever reached, and why the
     * ordering is what this test protects.
     *
     * <p>{@code -d=} is empty on both sides. It is here rather than with the name tests because the
     * path guard comes first, so this is the branch it actually takes.
     *
     * @param arg an argument whose path half is empty
     */
    @ParameterizedTest
    @ValueSource(strings = {"-dsave=", "-d="})
    void anEmptyPathIsRejectedRatherThanTakenAsTheWorkingDirectory(String arg) {
        String message = Main.checkDirectoryOption(arg);

        assertNotNull(message, arg + " names no path, and the empty path is not an override");
        assertTrue(message.contains("-d<dir>=<path>"),
                "the message must show the shape that was wanted: " + message);
    }

    // ---- rejected: unknown name ------------------------------------------

    /**
     * A name that is not one of the game's directories is rejected, and the message names it.
     *
     * <p>{@code SAVE} is here on purpose: the names are the data-file spellings and are matched
     * case-sensitively, so the upper-case form of a real directory is not a real directory. That is
     * worth pinning because it is the mistake a player is most likely to make.
     *
     * @param name the unknown directory name
     */
    @ParameterizedTest
    @ValueSource(strings = {"nosuchdir", "SAVE", "archives", "gamedata "})
    void anUnknownDirectoryNameIsRejectedAndNamed(String name) {
        String message = Main.checkDirectoryOption("-d" + name + "=/tmp");

        assertNotNull(message, name + " is not a directory this game knows");
        assertTrue(message.contains(name),
                "the message must name the directory it rejected: " + message);
    }

    /**
     * An empty name is rejected as unknown, not as malformed - {@code "=/tmp"} splits into two parts,
     * the first of them empty, so it clears the arity check and fails on the lookup instead.
     *
     * <p>The path has to be non-empty for this test to reach the lookup at all. The empty-path guard
     * runs first, so {@code -d=}, which is empty on both sides, is rejected for its path and never
     * reaches the name check - that ordering is pinned in
     * {@link #anEmptyPathIsRejectedRatherThanTakenAsTheWorkingDirectory}, not here.
     */
    @Test
    void anEmptyDirectoryNameIsRejected() {
        String message = Main.checkDirectoryOption("-d=/tmp");

        assertNotNull(message, "an empty name matches no directory");
        assertFalse(message.contains("-d<dir>=<path>"),
                "it splits into two parts with a usable path, so it is past both earlier checks: " + message);
    }

    // ---- rejected: missing path ------------------------------------------

    /**
     * A known directory pointed at a path that does not exist is rejected, and the message quotes
     * the path.
     *
     * <p>The path is built under a temporary directory rather than hard-coded, so the test does not
     * depend on some absolute path being absent from the machine it runs on.
     *
     * @param tempDir a directory that certainly exists, supplied and removed by JUnit
     */
    @Test
    void aPathThatDoesNotExistIsRejectedAndQuoted(@TempDir Path tempDir) {
        String missing = tempDir.resolve("no-such-directory").toString();

        String message = Main.checkDirectoryOption("-dsave=" + missing);

        assertNotNull(message, "the path does not exist, so the override cannot be applied");
        assertTrue(message.contains(missing),
                "the message must quote the path it could not find: " + message);
    }

    /**
     * The checks run in order: an unknown name is reported even when the path is also missing.
     *
     * <p>Worth pinning because reporting only the first problem is a choice. A player who typed both
     * wrong is told about the name, fixes it, and is then told about the path - which is the
     * behaviour C has too, since its checks are sequential and each one quits.
     *
     * @param tempDir a directory that certainly exists, supplied and removed by JUnit
     */
    @Test
    void theNameIsCheckedBeforeThePath(@TempDir Path tempDir) {
        String missing = tempDir.resolve("also-missing").toString();

        String message = Main.checkDirectoryOption("-dnosuchdir=" + missing);

        assertNotNull(message, "both halves are wrong, so this cannot be accepted");
        assertTrue(message.contains("nosuchdir"),
                "the name is checked first, so the name is what gets reported: " + message);
        assertFalse(message.contains(missing),
                "only the first problem found is reported: " + message);
    }

    // ---- known gap --------------------------------------------------------

    /**
     * A plain file is rejected where a directory is required - {@code checkDirectoryOption} asks
     * {@code File.isDirectory()}, not {@code File.exists()}.
     *
     * <p>A regression test for a hole that was open until 2026-08-09. {@code exists()} is true of a
     * regular file, so {@code -dsave=<some file>} used to be accepted and installed, and nothing
     * went wrong at the point of the mistake. It surfaced much later and much further away, when the
     * first loader built a name under it, opened {@code .../not-a-directory/constants.txt}, and
     * reported a missing data file with nothing left to connect that back to the {@code -d} that
     * caused it.
     *
     * <p>A deliberate divergence from C, which checks the path is usable rather than that it is a
     * directory, and so still has the hole.
     *
     * @param tempDir a directory that certainly exists, supplied and removed by JUnit
     * @throws IOException if the temporary file cannot be created
     */
    @Test
    void aPlainFileIsRejectedWhereADirectoryIsRequired(@TempDir Path tempDir) throws IOException {
        Path file = Files.createFile(tempDir.resolve("not-a-directory"));

        assertNotNull(Main.checkDirectoryOption("-dsave=" + file),
                "a regular file is not a directory: the check needs isDirectory(), not exists()");
    }

    /**
     * The UI half's crash path: what {@code startSwingUI}'s {@code try}/{@code catch}/{@code finally}
     * does when the loop it wraps dies, and what it does on the ordinary path as well.
     *
     * <p><b>Why this needs testing at all.</b> A thread that dies of an exception used to leave the
     * program running with nothing running in it - both halves gone, {@code main}'s joins returned,
     * and the event dispatch thread still holding an undisposed window, so the JVM never exited.
     * That was found by launching the game from the wrong working directory, where the data files
     * were not where the paths said; it cost a {@code kill}. The three tests below are that failure
     * turned into assertions, and the fourth pins the {@code finally} they share with the clean
     * path.
     *
     * <p><b>The body is reached by reflection, and that is a statement about the code rather than a
     * trick.</b> {@code startSwingUI} is {@code private static} and its {@code SwingUI} is a local
     * of the lambda it returns, so there is no seam: nothing can hand in a front end to observe, and
     * {@code main} itself blocks for the session. The alternative is
     * {@link org.jetbrains.annotations.VisibleForTesting} and package-private, which
     * {@code checkDirectoryOption} above already uses for the same reason - Rowan's call, and these
     * tests would simplify by three lines if it were taken.
     *
     * <p>The window is therefore observed globally, through {@link Frame#getFrames()}, filtered to
     * the frames that appeared after the test began. Coarse, but it is the same thing a person
     * checks, and it is what the earlier bug would have failed.
     *
     * <p>The crash is provoked with a misrouted message rather than a broken data directory: a
     * {@code UIMessage} that is not a close request arriving on the UI's own inbox means the wiring
     * is wrong, and {@code UILoop} throws on it by design. That keeps the trigger inside the
     * protocol - no files moved, no static directory state changed, nothing another test could
     * inherit.
     *
     * @author Rowan Crowther
     */
    @Nested
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    class TheUiHalfsCrashPath {

        /**
         * Long enough that a loaded machine does not fail a passing case, short enough that a real
         * hang is reported by the class timeout instead.
         */
        private static final long SHOULD_FINISH_MILLIS = 5_000;

        /**
         * Defaults for every switch. Nothing reads them yet; passed rather than {@code null} so the
         * body under test is built exactly as {@code main} builds it.
         */
        private static final StartupOptions NO_OPTIONS =
                new StartupOptions(false, false, false, false, "", "", List.of());

        private Channels channels;
        private Thread uiThread;

        /**
         * Anything that escaped the UI body, recorded by the thread's own handler. Staying
         * {@code null} is the assertion in {@link #aCrashEndsTheThreadByReturningNotThrowing}.
         */
        private final AtomicReference<Throwable> escaped = new AtomicReference<>();

        /**
         * The frames that already existed when the test began, so the ones this test creates can be
         * told apart from those another test class left on screen.
         */
        private Set<Frame> framesBefore;

        @BeforeEach
        void setUp() {
            assumeFalse(GraphicsEnvironment.isHeadless(),
                    "needs a display: the UI body builds a JFrame");

            channels = Channels.create();
            framesBefore = new HashSet<>(Arrays.asList(Frame.getFrames()));
        }

        /**
         * Leaves no thread blocked on a queue and no window on screen, however the test ended.
         */
        @AfterEach
        void tearDown() throws InterruptedException {
            if (uiThread != null && uiThread.isAlive()) {
                uiThread.interrupt();
                uiThread.join(SHOULD_FINISH_MILLIS);
            }
            for (Frame frame : framesCreatedByThisTest()) {
                SwingUtilities.invokeLater(frame::dispose);
            }
        }

        /**
         * Builds the real UI body and hands it a thread, as {@code main} does.
         *
         * @throws Exception if the private factory cannot be reached
         */
        private void startTheUiHalf() throws Exception {
            Method factory = Main.class.getDeclaredMethod("startSwingUI",
                    UIChannel.class, EDTChannel.class, StartupOptions.class);
            factory.setAccessible(true);

            Runnable body = (Runnable) factory.invoke(null,
                    channels.uiChannel(), channels.edtChannel(), NO_OPTIONS);

            uiThread = new Thread(body, "angband-ui-under-test");
            uiThread.setUncaughtExceptionHandler((thread, thrown) -> escaped.set(thrown));
            uiThread.start();
        }

        /**
         * @return the frames that appeared since this test started
         */
        private List<Frame> framesCreatedByThisTest() {
            return Arrays.stream(Frame.getFrames())
                    .filter(frame -> !framesBefore.contains(frame))
                    .toList();
        }

        /**
         * Waits for the window {@code init} puts up, since it is built on the event dispatch thread
         * and so is not there the moment the thread starts.
         *
         * @return the realised game window
         */
        private Frame awaitTheGameWindow() throws InterruptedException {
            long deadline = System.currentTimeMillis() + SHOULD_FINISH_MILLIS;
            while (System.currentTimeMillis() < deadline) {
                for (Frame frame : framesCreatedByThisTest()) {
                    if (frame.isDisplayable()) return frame;
                }
                Thread.sleep(20);
            }
            return fail("the UI body never put a window up");
        }

        /**
         * Waits for a window to be disposed, which also happens on the event dispatch thread.
         *
         * @param window the window that should go
         * @return whether it went within the timeout
         */
        private boolean awaitDisposalOf(Frame window) throws InterruptedException {
            long deadline = System.currentTimeMillis() + SHOULD_FINISH_MILLIS;
            while (System.currentTimeMillis() < deadline) {
                if (!window.isDisplayable()) return true;
                Thread.sleep(20);
            }
            return false;
        }

        /**
         * Kills the loop the way the protocol allows: a {@code UIMessage} that is not a close
         * request, arriving on the UI's own inbox, which {@code UILoop} rejects by throwing.
         */
        private void crashTheLoop() {
            channels.edtChannel().edtSender()
                    .send(new UIMessage.LifecycleUIMessage(UILifecycleEvent.START));
        }

        /**
         * Reads the core's inbox until the shutdown request arrives, ignoring the {@code START}
         * that {@code init} sends first.
         *
         * @return the request, once it comes
         */
        private UIMessage awaitSaveAndStop() throws InterruptedException {
            while (true) {
                UIMessage message = channels.coreChannel().coreReceiver().receive();
                if (message instanceof UIMessage.LifecycleUIMessage lifecycle
                        && lifecycle.event() == UILifecycleEvent.SAVE_AND_STOP) {
                    return message;
                }
            }
        }

        /**
         * A dying UI half unparks the core before it goes.
         *
         * <p>The first of the two things it owes. Without this the core sits in {@code receive()}
         * for ever, waiting for a front end that no longer exists - a thread that cannot be joined
         * and a program that cannot end.
         */
        @Test
        void aCrashSendsSaveAndStopToTheCore() throws Exception {
            startTheUiHalf();
            awaitTheGameWindow();

            crashTheLoop();

            assertNotNull(awaitSaveAndStop(),
                    "a UI half that dies must still tell the core to stop");
        }

        /**
         * And disposes its windows, which is the half that actually ends the process.
         *
         * <p>This is the assertion the original bug would have failed: the loop died, nothing
         * disposed anything, and the event dispatch thread kept a dead program alive.
         */
        @Test
        void aCrashDisposesTheWindow() throws Exception {
            startTheUiHalf();
            Frame window = awaitTheGameWindow();

            crashTheLoop();

            assertTrue(awaitDisposalOf(window),
                    "an undisposed window holds the EDT, and the JVM with it");
        }

        /**
         * The thread ends by returning, not by throwing.
         *
         * <p>Which is why the UI half needs no {@code UncaughtExceptionHandler}: the exception is
         * caught where the front end it has to shut down is in scope, so nothing reaches the
         * handler. A recorded throwable here means the {@code catch} has stopped covering what the
         * loop can throw.
         */
        @Test
        void aCrashEndsTheThreadByReturningNotThrowing() throws Exception {
            startTheUiHalf();
            awaitTheGameWindow();

            crashTheLoop();
            uiThread.join(SHOULD_FINISH_MILLIS);

            assertFalse(uiThread.isAlive(), "the UI thread must finish after its loop dies");
            assertNull(escaped.get(),
                    "the crash should be caught in the body, not escape to a handler");
        }

        /**
         * The clean path disposes too, through the same {@code finally}.
         *
         * <p>On this path {@code UILoop} has already queued a disposal of its own before returning,
         * so the {@code finally} makes it happen twice. That is the point of the test: disposing a
         * disposed window does nothing, while a missing disposal hangs the program, so the
         * duplicate is the right way to be wrong.
         */
        @Test
        void theCleanPathDisposesTheWindowThroughTheSameFinally() throws Exception {
            startTheUiHalf();
            Frame window = awaitTheGameWindow();

            channels.coreChannel().coreSender()
                    .send(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED));
            uiThread.join(SHOULD_FINISH_MILLIS);

            assertFalse(uiThread.isAlive(), "STOPPED ends the loop and so the thread");
            assertTrue(awaitDisposalOf(window), "the windows go on the ordinary path as well");
            assertNull(escaped.get(), "disposing an already-disposed window must not throw");
        }
    }

}
