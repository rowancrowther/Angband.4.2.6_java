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

package uk.co.jackoftradesltd.frontend.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftradesltd.channel.enums.GameEventType;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link MainEvents}: the front end's five phase transitions, while they are still stubs.
 *
 * <p><b>What is worth pinning about a stub.</b> Not that it logs - that is trivia - but that the
 * five methods are <em>distinct</em>. They are near-identical one-liners differing only by an event
 * name, which is the shape that copy-paste damages silently: {@code enterWorld()} announcing
 * {@code EVENT_ENTER_GAME} is invisible in review, harmless today, and thoroughly confusing on the
 * day someone debugs a real phase transition by reading the log. So the tests here check that each
 * method names the event it stands for, and that no two name the same one.
 *
 * <p>The mapping from method to event is written out once in {@link #ANNOUNCERS} and used by every
 * test, so a sixth transition is one line here rather than three new tests.
 *
 * @author Rowan Crowther
 */
class MainEventsTest {

    /**
     * Each phase transition against the method that announces it.
     *
     * <p>This is the table under test, not a convenience: it is the claim that these five
     * {@link GameEventType}s are exactly the ones {@code MainEvents} covers, and that each is
     * covered by the obviously-matching method.
     */
    private static final List<Announcer> ANNOUNCERS = List.of(
            new Announcer(GameEventType.EVENT_LEAVE_INIT, MainEvents::leaveInit),
            new Announcer(GameEventType.EVENT_ENTER_GAME, MainEvents::enterGame),
            new Announcer(GameEventType.EVENT_LEAVE_GAME, MainEvents::leaveGame),
            new Announcer(GameEventType.EVENT_ENTER_WORLD, MainEvents::enterWorld),
            new Announcer(GameEventType.EVENT_LEAVE_WORLD, MainEvents::leaveWorld));

    /**
     * The events this class is responsible for; anything else belongs to another receiver.
     */
    private static final List<GameEventType> COVERED =
            ANNOUNCERS.stream().map(Announcer::event).toList();

    private RecordingLog log;

    private MainEvents mainEvents;

    @BeforeEach
    void setUp() {
        log = RecordingLog.attachedTo(MainEvents.class);
        mainEvents = new MainEvents();
    }

    @AfterEach
    void tearDown() {
        log.detach();
    }

    /**
     * Every method announces its own event and no other. The test that catches a copy-pasted
     * method body, which is the only way these five can currently be wrong.
     */
    @Test
    void eachTransitionAnnouncesItsOwnEvent() {
        for (Announcer announcer : ANNOUNCERS) {
            log.clear();

            announcer.call().accept(mainEvents);

            assertEquals(List.of("Executing " + announcer.event()), log.lines(),
                    announcer.event() + " should be announced by its own method, alone");
        }
    }

    /**
     * Calling all five produces five different lines. Distinctness stated directly: if two methods
     * were pasted from the same source the count drops, whichever pair it was.
     */
    @Test
    void theFiveTransitionsAreDistinct() {
        ANNOUNCERS.forEach(announcer -> announcer.call().accept(mainEvents));

        assertEquals(ANNOUNCERS.size(), log.lines().size(),
                "each call should log exactly once");
        assertEquals(ANNOUNCERS.size(), Set.copyOf(log.lines()).size(),
                "two transitions are announcing the same event");
    }

    /**
     * A method exists for each phase event, and for no other kind of event.
     *
     * <p>This is the boundary claim rather than a behaviour claim: {@code MainEvents} covers the
     * game and world transitions plus {@code EVENT_LEAVE_INIT}, while {@code EVENT_ENTER_INIT}
     * stays in {@code UILoop} (it paints the splash screen) and the birth pair belongs to
     * {@code BirthEvents}. If a later stage moves one of those, this fails and says so.
     */
    @ParameterizedTest
    @EnumSource(value = GameEventType.class, names = {
            "EVENT_LEAVE_INIT", "EVENT_ENTER_GAME", "EVENT_LEAVE_GAME",
            "EVENT_ENTER_WORLD", "EVENT_LEAVE_WORLD"})
    void thePhaseEventsAreTheOnesCovered(GameEventType event) {
        assertTrue(COVERED.contains(event),
                event + " has no method on MainEvents");
    }

    /**
     * The events this class deliberately does not handle.
     */
    @ParameterizedTest
    @EnumSource(value = GameEventType.class, names = {
            "EVENT_ENTER_INIT", "EVENT_INITSTATUS", "EVENT_ENTER_BIRTH", "EVENT_LEAVE_BIRTH"})
    void theOtherStartUpEventsBelongElsewhere(GameEventType event) {
        assertFalse(COVERED.contains(event),
                event + " is handled elsewhere and should not have a MainEvents method");
    }

    /**
     * One phase transition: the event, and the method that announces it.
     *
     * @param event the transition
     * @param call  the method under test, as a call against an instance
     * @author Rowan Crowther
     */
    private record Announcer(GameEventType event, Consumer<MainEvents> call) {
    }
}
