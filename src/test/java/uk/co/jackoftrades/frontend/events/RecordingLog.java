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

package uk.co.jackoftrades.frontend.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A log4j appender that keeps what was logged instead of writing it anywhere, attached to one
 * class's logger for the length of one test.
 *
 * <p><b>Asserting on log text is normally a bad habit, and here it is the only honest option.</b>
 * {@code MainEvents} and {@code BirthEvents} are stubs: they take no arguments, return nothing, and
 * hold no state, so the line they log is the whole of their observable behaviour. A test that
 * called {@code enterWorld()} and asserted nothing would pass on an empty method body, which is
 * exactly the bug worth catching in five methods that differ only by name.
 *
 * <p>When those methods gain real bodies this helper should lose its callers, not gain more: the
 * thing to assert then is the painting, and a test still reading the log at that point is testing
 * the scaffolding rather than the work.
 *
 * <p><b>Thread-safe on purpose.</b> {@code PhaseEventRoutingTest} drives a real {@code UILoop},
 * so the lines are appended on the loop's thread and read on the test's. The
 * {@link CopyOnWriteArrayList} is what makes that handover safe without the test having to
 * synchronise.
 *
 * @author Rowan Crowther
 */
final class RecordingLog extends AbstractAppender {

    /**
     * Serial number for appender names.
     *
     * <p><b>Not decoration.</b> Log4j holds a logger's appenders in a set keyed by name, so
     * attaching a second recorder named the same as one already there is silently ignored - the
     * new appender records nothing and the test sees an empty log rather than an error. Two
     * recorders on one logger is an ordinary thing for a test to want, so the name has to be
     * unique per instance rather than per class.
     */
    private static final AtomicInteger SERIAL = new AtomicInteger();

    /**
     * The formatted messages seen so far, oldest first.
     */
    private final List<String> lines = new CopyOnWriteArrayList<>();

    /**
     * The logger this is attached to, kept so {@link #detach()} can undo the attachment.
     */
    private final Logger target;

    private RecordingLog(Logger target) {
        super("RecordingLog-" + target.getName() + "-" + SERIAL.incrementAndGet(),
                null, null, true, Property.EMPTY_ARRAY);
        this.target = target;
    }

    /**
     * Starts recording everything the given class logs.
     *
     * <p>Casting to log4j's {@code core.Logger} is what allows an appender to be added at runtime;
     * it is safe because {@code log4j-core} is the implementation on the test classpath, and would
     * fail loudly at the first call rather than silently record nothing if that ever changed.
     *
     * @param type the class whose logger to capture
     * @return the recorder, which the caller must {@link #detach()}
     */
    static RecordingLog attachedTo(Class<?> type) {
        Logger target = (Logger) LogManager.getLogger(type);

        RecordingLog recorder = new RecordingLog(target);
        recorder.start();
        target.addAppender(recorder);

        return recorder;
    }

    /**
     * Stops recording. Left undone, the appender outlives its test and every later test in the
     * same JVM keeps feeding it - so this belongs in an {@code @AfterEach}, not at the end of a
     * test body where a failure would skip it.
     */
    void detach() {
        target.removeAppender(this);
        stop();
    }

    @Override
    public void append(LogEvent event) {
        lines.add(event.getMessage().getFormattedMessage());
    }

    /**
     * @return the messages logged so far, oldest first
     */
    List<String> lines() {
        return List.copyOf(lines);
    }

    /**
     * Forgets everything recorded so far, for a test that makes several calls and wants to judge
     * each on its own.
     */
    void clear() {
        lines.clear();
    }

    /**
     * Waits for a line to appear, for the tests that drive another thread.
     *
     * @param expected the exact message to wait for
     * @param millis   how long to wait before giving up
     * @return whether it arrived
     */
    boolean await(String expected, long millis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + millis;

        while (System.currentTimeMillis() < deadline) {
            if (lines.contains(expected)) {
                return true;
            }
            Thread.sleep(10);
        }

        return lines.contains(expected);
    }
}
