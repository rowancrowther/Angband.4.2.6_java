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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.enums.GameEventType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link BirthEvents}: character creation's two ends, while they are still stubs.
 *
 * <p>The same claim as {@code MainEventsTest} makes about the phase transitions - each method
 * announces its own event, and the pair are distinct - over a much smaller surface. Kept as its own
 * class rather than folded in with the other five because the split between {@code ui-birth.c} and
 * {@code ui-display.c} is one the port deliberately keeps, and a shared test would quietly argue
 * the opposite.
 *
 * <p><b>What is deliberately not tested.</b> Nothing here asserts anything about quickstart, the
 * savefile name, or the menus, all of which C does at these two points. Those need the input
 * boundary that Chapter 3 brings, and a test written now would be a guess at an interface that
 * does not exist - which is the same reason the methods themselves are empty.
 *
 * @author Rowan Crowther
 */
class BirthEventsTest {

    private RecordingLog log;

    private BirthEvents birthEvents;

    @BeforeEach
    void setUp() {
        log = RecordingLog.attachedTo(BirthEvents.class);
        birthEvents = new BirthEvents();
    }

    @AfterEach
    void tearDown() {
        log.detach();
    }

    /**
     * Entering the birth screen announces entering, and nothing else.
     *
     * @author Rowan Crowther
     */
    @Test
    void enteringAnnouncesEnterBirth() {
        birthEvents.enterBirth();

        assertEquals(List.of("Executing " + GameEventType.EVENT_ENTER_BIRTH), log.lines());
    }

    /**
     * Leaving the birth screen announces leaving. Worth its own test next to the one above for the
     * reason two-line classes usually are: the pair is exactly where a copy-paste lands.
     *
     * @author Rowan Crowther
     */
    @Test
    void leavingAnnouncesLeaveBirth() {
        birthEvents.leaveBirth();

        assertEquals(List.of("Executing " + GameEventType.EVENT_LEAVE_BIRTH), log.lines());
    }

    /**
     * The two are not the same line. Implied by the two tests above, and stated anyway because it
     * is the actual claim: the log is how these transitions will be read while they are stubs, and
     * a log that cannot tell them apart is worse than no log.
     *
     * @author Rowan Crowther
     */
    @Test
    void theTwoEndsAreDistinct() {
        birthEvents.enterBirth();
        birthEvents.leaveBirth();

        assertEquals(List.of(
                        "Executing " + GameEventType.EVENT_ENTER_BIRTH,
                        "Executing " + GameEventType.EVENT_LEAVE_BIRTH),
                log.lines(),
                "the birth screen's two ends should announce themselves differently, in order");
    }
}
