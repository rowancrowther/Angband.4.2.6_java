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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.projection.Source;
import uk.co.jackoftradesltd.middle.game.event.projection.SourceWhat;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the half of {@code PlayerTimed.setTimed} that {@code PlayerSetTimedTest} leaves alone: the
 * begin and end effect chains, and the update and redraw flags an effect carries of its own. The
 * port of C's {@code player_set_timed} ({@code player-timed.c}).
 *
 * <p>Every expectation is read off that C. The chains are dispatched between the message and the
 * store, on two conditions: {@code v > 0 && !p->timed[idx]} starts the effect, and {@code v == 0}
 * ends it. The origin handed to {@code effect_do} is {@code source_none()} when the caller may
 * disturb and {@code source_player()} when not, and the chains run whether or not the change is
 * announced. Because {@code Effect.effectDo} is still a stub, each chain is observed through an
 * {@link Effect} subclass that records the call.
 *
 * <p>A chain reaching {@code effectDo} one effect at a time is how the port spells C's single
 * {@code effect_do(chain, ...)} call. What these tests pin is that each effect of the list is
 * reached once, in list order, on the right transition and with the right origin.
 *
 * <p>The effect under test is {@link TimedEffect#TMD_NONE}, which carries no flags of its own, except
 * in {@link Flags}, which uses {@link TimedEffect#TMD_BLIND} because it is the one effect whose
 * redraw set lacks {@code PR_STATUS}. {@link PlayerRegistry} is global static state, so whatever it
 * held is saved and put back around every test.
 *
 * <p>Class PlayerSetTimedTransitionTest coded on 261029, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
@ExtendWith(uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry.class)
@DisplayName("PlayerTimed.setTimed transitions and flags")
class PlayerSetTimedTransitionTest {

    /**
     * The effect most fixtures define, chosen because it carries no update or redraw flags.
     */
    private static final TimedEffect EFFECT = TimedEffect.TMD_NONE;

    /**
     * One recorded call to {@code effectDo}.
     *
     * @param label         which effect of which chain was reached
     * @param origin        the origin it was handed
     * @param counterAtCall the effect's stored counter at the moment of the call
     */
    private record Call(String label, Source origin, int counterAtCall) {
    }

    /**
     * An {@link Effect} whose {@code effectDo} records itself instead of doing nothing.
     */
    private class SpyEffect extends Effect {
        /**
         * What this effect calls itself in the log.
         */
        private final String label;

        SpyEffect(String label) {
            super(EffectEnum.EF_NONE, null, null, 0, 0, null, null, 0, 0, List.of(), null);
            this.label = label;
        }

        @Override
        public boolean effectDo(Source origin, ItemObject object, boolean identifiable, boolean aware,
                                DirectionEnum direction, int beamChance, int boost, Command cmd) {
            calls.add(new Call(label, origin, player.getTimedEffect(definedEffect)));
            return false;
        }
    }

    /**
     * Every {@code effectDo} reached during the test, in order.
     */
    private List<Call> calls;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The effect the current fixture defined.
     */
    private TimedEffect definedEffect;

    /**
     * Whatever the registry held before this test, put back afterwards.
     */
    private Object savedEffects;

    private static Field registryField() throws Exception {
        Field f = PlayerRegistry.class.getDeclaredField("playerTimedEffects");
        f.setAccessible(true);
        return f;
    }

    /**
     * Loads the registry with one definition: no messages, no lower bound, a single band topping out
     * at 30 behind the implicit "off" band, and the given chains.
     *
     * @param effect     the effect to define
     * @param lowerBound the floor the requested value is raised to
     * @param begin      the begin chain, or {@code null}
     * @param end        the end chain, or {@code null}
     * @throws Exception if the registry field cannot be reached
     */
    private void load(TimedEffect effect, int lowerBound, List<Effect> begin, List<Effect> end)
            throws Exception {
        definedEffect = effect;
        List<TimedGrade> grades = List.of(
                new TimedGrade(0, ColourEnum.COLOUR_DARK, 0, null, null, null),
                new TimedGrade(1, ColourEnum.COLOUR_WHITE, 30, "band", null, null));
        PlayerTimedEffect definition = new PlayerTimedEffect(effect, "test effect", null, null, null,
                MessageType.MSG_GENERIC, List.of(), grades, begin, end, false, lowerBound,
                ObjectFlag.OF_NONE, false, ElementEnum.ELEM_NONE, null, null);
        List<PlayerTimedEffect> all = new ArrayList<>();
        all.add(definition);
        registryField().set(null, all);
    }

    private void load(List<Effect> begin, List<Effect> end) throws Exception {
        load(EFFECT, 0, begin, end);
    }

    @BeforeEach
    void setUp() throws Exception {
        savedEffects = registryField().get(null);
        player = new Player();
        calls = new ArrayList<>();
    }

    @AfterEach
    void tearDown() throws Exception {
        registryField().set(null, savedEffects);
    }

    @SuppressWarnings("unchecked")
    private void startAt(int value) throws Exception {
        Field f = Player.class.getDeclaredField("timed");
        f.setAccessible(true);
        ((Map<TimedEffect, Integer>) f.get(player)).put(definedEffect, value);
    }

    private boolean set(int amount, boolean notify, boolean canDisturb) {
        return PlayerTimed.setTimed(player, definedEffect, amount, notify, canDisturb);
    }

    private List<String> labels() {
        return calls.stream().map(Call::label).toList();
    }

    /**
     * C's {@code if (v > 0 && !p->timed[idx])}: the begin chain, and only that.
     */
    @Nested
    @DisplayName("the begin chain")
    class Begin {

        @Test
        @DisplayName("fires when the counter leaves zero")
        void firesOnLeavingZero() throws Exception {
            load(List.of(new SpyEffect("begin")), List.of(new SpyEffect("end")));
            startAt(0);

            set(5, true, true);

            assertEquals(List.of("begin"), labels());
        }

        @Test
        @DisplayName("fires once per effect, in list order")
        void firesInListOrder() throws Exception {
            load(List.of(new SpyEffect("b1"), new SpyEffect("b2"), new SpyEffect("b3")), null);
            startAt(0);

            set(5, true, true);

            assertEquals(List.of("b1", "b2", "b3"), labels());
        }

        @Test
        @DisplayName("does not fire when an effect that is already running rises")
        void silentWhileAlreadyRunning() throws Exception {
            load(List.of(new SpyEffect("begin")), List.of(new SpyEffect("end")));
            startAt(5);

            set(9, true, true);

            assertTrue(calls.isEmpty());
        }

        @Test
        @DisplayName("does not fire when an effect that is already running falls")
        void silentWhileFalling() throws Exception {
            load(List.of(new SpyEffect("begin")), List.of(new SpyEffect("end")));
            startAt(9);

            set(5, true, true);

            assertTrue(calls.isEmpty());
        }

        @Test
        @DisplayName("fires for a request above the top, which is capped but still positive")
        void firesForACappedRequest() throws Exception {
            load(List.of(new SpyEffect("begin")), null);
            startAt(0);

            set(99, true, true);

            assertEquals(List.of("begin"), labels());
            assertEquals(30, player.getTimedEffect(definedEffect));
        }

        @Test
        @DisplayName("fires and announces even when the caller asked for silence, since leaving zero climbs a grade")
        void leavingZeroAlwaysAnnounces() throws Exception {
            load(List.of(new SpyEffect("begin")), null);
            startAt(0);

            assertTrue(set(5, false, false));

            assertEquals(List.of("begin"), labels());
        }

        @Test
        @DisplayName("does not fire when the request changes nothing")
        void silentOnNoChange() throws Exception {
            load(List.of(new SpyEffect("begin")), null);
            startAt(0);

            set(0, true, true);

            assertTrue(calls.isEmpty());
        }

        @Test
        @DisplayName("is reached before the new value is stored")
        void runsBeforeTheStore() throws Exception {
            load(List.of(new SpyEffect("begin")), null);
            startAt(0);

            set(5, true, true);

            assertEquals(0, calls.get(0).counterAtCall());
            assertEquals(5, player.getTimedEffect(definedEffect));
        }

        @Test
        @DisplayName("a lower bound lifts a request of 1 to the bound, and the chain still fires")
        void firesWithALowerBound() throws Exception {
            load(EFFECT, 5, List.of(new SpyEffect("begin")), null);
            startAt(0);

            set(1, true, true);

            assertEquals(List.of("begin"), labels());
            assertEquals(5, player.getTimedEffect(definedEffect));
        }
    }

    /**
     * C's {@code else if (v == 0)}: the end chain, and only that.
     */
    @Nested
    @DisplayName("the end chain")
    class End {

        @Test
        @DisplayName("fires when the counter reaches zero")
        void firesOnReachingZero() throws Exception {
            load(List.of(new SpyEffect("begin")), List.of(new SpyEffect("end")));
            startAt(5);

            set(0, true, true);

            assertEquals(List.of("end"), labels());
        }

        @Test
        @DisplayName("fires once per effect, in list order")
        void firesInListOrder() throws Exception {
            load(null, List.of(new SpyEffect("e1"), new SpyEffect("e2")));
            startAt(5);

            set(0, true, true);

            assertEquals(List.of("e1", "e2"), labels());
        }

        @Test
        @DisplayName("fires for a negative request, which the lower bound turns into zero")
        void firesForANegativeRequest() throws Exception {
            load(null, List.of(new SpyEffect("end")));
            startAt(5);

            set(-7, true, true);

            assertEquals(List.of("end"), labels());
            assertEquals(0, player.getTimedEffect(definedEffect));
        }

        @Test
        @DisplayName("fires on a silent change")
        void firesWhenSilent() throws Exception {
            load(null, List.of(new SpyEffect("end")));
            startAt(5);

            assertFalse(set(0, false, false));

            assertEquals(List.of("end"), labels());
        }

        @Test
        @DisplayName("does not fire while the effect is merely shortened")
        void silentWhileShortening() throws Exception {
            load(null, List.of(new SpyEffect("end")));
            startAt(9);

            set(2, true, true);

            assertTrue(calls.isEmpty());
        }

        @Test
        @DisplayName("does not fire for an effect already at zero, which is no change")
        void silentWhenAlreadyZero() throws Exception {
            load(null, List.of(new SpyEffect("end")));
            startAt(0);

            set(0, true, true);

            assertTrue(calls.isEmpty());
        }

        @Test
        @DisplayName("never fires for an effect whose lower bound is above zero")
        void neverFiresWithALowerBound() throws Exception {
            load(EFFECT, 5, null, List.of(new SpyEffect("end")));
            startAt(9);

            set(0, true, true);

            assertTrue(calls.isEmpty());
            assertEquals(5, player.getTimedEffect(definedEffect));
        }

        @Test
        @DisplayName("is reached before the zero is stored")
        void runsBeforeTheStore() throws Exception {
            load(null, List.of(new SpyEffect("end")));
            startAt(5);

            set(0, true, true);

            assertEquals(5, calls.get(0).counterAtCall());
            assertEquals(0, player.getTimedEffect(definedEffect));
        }
    }

    /**
     * C: {@code can_disturb ? source_none() : source_player()}, the same for both chains.
     */
    @Nested
    @DisplayName("the origin handed to the chain")
    class Origin {

        @Test
        @DisplayName("begin chain: a disturbing change is attributed to nobody")
        void beginDisturbingIsNone() throws Exception {
            load(List.of(new SpyEffect("begin")), null);
            startAt(0);

            set(5, true, true);

            assertEquals(SourceWhat.SRC_NONE, calls.get(0).origin().what());
        }

        @Test
        @DisplayName("begin chain: a non-disturbing change is attributed to the player")
        void beginQuietIsPlayer() throws Exception {
            load(List.of(new SpyEffect("begin")), null);
            startAt(0);

            set(5, true, false);

            assertEquals(SourceWhat.SRC_PLAYER, calls.get(0).origin().what());
        }

        @Test
        @DisplayName("end chain: a disturbing change is attributed to nobody")
        void endDisturbingIsNone() throws Exception {
            load(null, List.of(new SpyEffect("end")));
            startAt(5);

            set(0, true, true);

            assertEquals(SourceWhat.SRC_NONE, calls.get(0).origin().what());
        }

        @Test
        @DisplayName("end chain: a non-disturbing change is attributed to the player")
        void endQuietIsPlayer() throws Exception {
            load(null, List.of(new SpyEffect("end")));
            startAt(5);

            set(0, true, false);

            assertEquals(SourceWhat.SRC_PLAYER, calls.get(0).origin().what());
        }

        @Test
        @DisplayName("the origin follows can_disturb, not notify")
        void followsCanDisturbNotNotify() throws Exception {
            load(List.of(new SpyEffect("begin")), null);
            startAt(0);

            set(5, false, true);

            assertEquals(SourceWhat.SRC_NONE, calls.get(0).origin().what());
        }
    }

    /**
     * C: {@code p->upkeep->update |= effect->flag_update;
     * p->upkeep->redraw |= (PR_STATUS | effect->flag_redraw)}, for a notifying change only.
     */
    @Nested
    @DisplayName("the effect's own redraw flags")
    class Flags {

        @Test
        @DisplayName("a notifying change raises the effect's own flag and PR_STATUS")
        void notifyingRaisesBoth() throws Exception {
            load(TimedEffect.TMD_BLIND, 0, null, null);
            startAt(0);

            set(5, true, false);

            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_MAP));
            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_STATUS));
        }

        @Test
        @DisplayName("a silent change raises neither")
        void silentRaisesNeither() throws Exception {
            load(TimedEffect.TMD_BLIND, 0, null, null);
            startAt(3);

            assertFalse(set(8, false, false));

            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_MAP));
            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_STATUS));
        }
    }
}
