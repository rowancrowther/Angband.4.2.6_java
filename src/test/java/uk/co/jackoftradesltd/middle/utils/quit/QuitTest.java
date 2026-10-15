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

package uk.co.jackoftradesltd.middle.utils.quit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.utils.ControlUtils;
import uk.co.jackoftradesltd.middle.utils.quit.Quit.GameQuitException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Quit}, the port of {@code quit()} in the C source's {@code z-util.c}.
 *
 * <p>C's {@code quit(const char *str)} has exactly two behaviours, told apart by whether
 * {@code str} is {@code NULL}: {@code quit(NULL)} logs nothing and exits with success, and any
 * string is passed to {@code plog()} and exits with failure - the empty string included, since
 * only {@code NULL} is special. The port cannot end the process from inside a library method, so
 * both become a thrown {@link GameQuitException}, and the clean/failure distinction survives as
 * whether that exception has a message. The expected values here come from that C contract and
 * from real {@code quit()} / {@code quit_fmt()} call sites in the C tree, not from reading the
 * Java body.
 *
 * <p>The log is asserted on because it is half of what {@code quit()} does: a clean quit that
 * logged a fatal line, or a failure that logged nothing, would be the exact mistake this class
 * has already made once.
 *
 * @author Rowan Crowther
 */
class QuitTest {

    /**
     * One captured log record: the level it was logged at and its formatted text.
     */
    private record Logged(Level level, String text) {
    }

    /**
     * A log4j appender that keeps what {@link Quit} logs instead of writing it anywhere.
     */
    private static final class Recorder extends AbstractAppender {
        private final List<Logged> events = new CopyOnWriteArrayList<>();

        Recorder() {
            super("QuitTestRecorder", null, null, true, Property.EMPTY_ARRAY);
        }

        @Override
        public void append(LogEvent event) {
            events.add(new Logged(event.getLevel(), event.getMessage().getFormattedMessage()));
        }
    }

    private Logger quitLogger;
    private Recorder recorder;

    @BeforeEach
    void attachRecorder() {
        quitLogger = (Logger) LogManager.getLogger(Quit.class);
        recorder = new Recorder();
        recorder.start();
        quitLogger.addAppender(recorder);
    }

    @AfterEach
    void detachRecorder() {
        quitLogger.removeAppender(recorder);
        recorder.stop();
    }

    /**
     * Runs {@code quit(message)} and hands back the exception it must throw.
     */
    private GameQuitException quitWith(String message) {
        return assertThrows(GameQuitException.class, () -> new Quit().quit(message));
    }

    @Nested
    class CleanQuit {

        @Test
        void nullMessageThrowsWithNoMessage() {
            // C: quit(NULL) -> exit(EXIT_SUCCESS)
            assertNull(quitWith(null).getMessage());
        }

        @Test
        void nullMessageLogsNothing() {
            // C: quit(NULL) never reaches plog()
            quitWith(null);

            assertTrue(recorder.events.isEmpty(), "a clean quit must not log: " + recorder.events);
        }
    }

    @Nested
    class FailureQuit {

        @Test
        void messageIsLoggedOnceAtFatal() {
            // A real C call site: datafile.c, quit(format("Cannot open '%s.txt'", filename))
            quitWith("Cannot open 'object.txt'");

            assertEquals(List.of(new Logged(Level.FATAL, "Cannot open 'object.txt'")), recorder.events);
        }

        @Test
        void exceptionCarriesTheSameMessage() {
            assertEquals("Cannot open 'object.txt'", quitWith("Cannot open 'object.txt'").getMessage());
        }

        @Test
        void emptyStringIsAFailureNotACleanQuit() {
            // C: only NULL is special, so quit("") is plog("") and a failure exit.
            GameQuitException thrown = quitWith("");

            assertNotNull(thrown.getMessage(), "the empty string must not read as a clean quit");
            assertEquals("", thrown.getMessage());
            assertEquals(List.of(new Logged(Level.FATAL, "")), recorder.events);
        }

        @Test
        void successTextIsStillAFailure() {
            // C tells the cases apart by NULL, which no string can collide with. A message that
            // merely says "Success" must therefore still be a failure.
            GameQuitException thrown = quitWith("Success");

            assertEquals("Success", thrown.getMessage());
            assertEquals(1, recorder.events.size());
        }

        @Test
        void formatCharactersAreLoggedLiterally() {
            // plog() receives an already-formatted string, so %s, % and {} must pass through as text.
            String message = "Not enough flavors for tval %s: 100% {} done";

            assertEquals(message, quitWith(message).getMessage());
            assertEquals(List.of(new Logged(Level.FATAL, message)), recorder.events);
        }

        @Test
        void eachCallLogsExactlyOnce() {
            quitWith("first");
            quitWith("second");

            assertEquals(List.of(new Logged(Level.FATAL, "first"), new Logged(Level.FATAL, "second")),
                    recorder.events);
        }
    }

    @Nested
    class Contract {

        @Test
        void exceptionIsUnchecked() {
            assertInstanceOf(RuntimeException.class, quitWith("x"));
        }

        @Test
        void worksThroughTheQuitAuxInterface() {
            QuitAux aux = new Quit();

            assertThrows(GameQuitException.class, () -> aux.quit("via interface"));
        }
    }

    /**
     * {@code ControlUtils} is where C's {@code quit()} / {@code quit_fmt()} call sites land. The
     * no-argument {@code quit()} is C's {@code quit(NULL)}, which is the case that was wrong.
     */
    @Nested
    class ThroughControlUtils {

        @Test
        void quitWithNoArgumentIsACleanQuit() {
            GameQuitException thrown = assertThrows(GameQuitException.class, ControlUtils::quit);

            assertNull(thrown.getMessage());
            assertTrue(recorder.events.isEmpty());
        }

        @Test
        void quitWithStringIsAFailure() {
            GameQuitException thrown = assertThrows(GameQuitException.class,
                    () -> ControlUtils.quit("No town monsters!"));

            assertEquals("No town monsters!", thrown.getMessage());
            assertEquals(List.of(new Logged(Level.FATAL, "No town monsters!")), recorder.events);
        }

        @Test
        void quitFmtFormatsBeforeQuitting() {
            // C: quit_fmt("Failed pickup of %s", obj->kind->name) in cmd-pickup.c
            GameQuitException thrown = assertThrows(GameQuitException.class,
                    () -> ControlUtils.quitFmt("Failed pickup of %s", "Potion of Cure Light Wounds"));

            assertEquals("Failed pickup of Potion of Cure Light Wounds", thrown.getMessage());
            assertEquals(List.of(new Logged(Level.FATAL, "Failed pickup of Potion of Cure Light Wounds")),
                    recorder.events);
        }
    }
}
