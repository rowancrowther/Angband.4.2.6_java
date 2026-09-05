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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests {@link ObjectIgnore#autoinscribeGround}, the port of C's {@code autoinscribe_ground}
 * ({@code obj-ignore.c:340-347}).
 *
 * <p>{@link ObjectIgnore#applyAutoinscription} has its own suite
 * ({@code ObjectIgnoreApplyAutoinscriptionTest}) that pins its guards one at a time, including the
 * "don't inscribe unless carried" guard. A floor item is never carried, so that guard means a kind's
 * {@code noteAware}/{@code noteUnaware} can never land on a floor object through this method -
 * {@link Guards#aKindNoteIsNeverAppliedToAFloorItem} pins that. What the loop here actually
 * exercises in the ordinary case is the rune side, which runs before the carried guard is even
 * reached - {@link Success} covers that, the same fixture shape as
 * {@code ObjectIgnoreApplyAutoinscriptionTest.RuneOrdering}.
 *
 * <p>{@link Bounds#doesNothingWhenTheGridIsOutOfBounds} pins the guard this method didn't
 * originally have: an out-of-bounds grid is C's {@code square_object} answering {@code NULL} and
 * the loop never starting, not a crash.
 *
 * <p>Class ObjectIgnoreAutoinscribeGroundTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectIgnoreAutoinscribeGroundTest {

    /**
     * The bus this test listens on, for the messages a successful inscription sends.
     */
    private CapturingBus bus;

    /**
     * The bus the engine had before, put back afterwards.
     */
    private EventsHandler realBus;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The level the player stands on.
     */
    private Chunk level;

    /**
     * The cave the game held before, put back afterwards.
     */
    private Chunk savedCave;

    /**
     * The rune list as it stood before a test replaced it.
     */
    private List<Rune> savedRunes;

    /**
     * A kind with the given aware autoinscription, built via reflection since {@code noteAware}
     * has no public setter (mirroring {@code ObjectIgnoreApplyAutoinscriptionTest}).
     *
     * @param noteAware the aware autoinscription, or {@code null} for none
     * @return the kind
     */
    private static ObjectKind kind(String noteAware) {
        ObjectKind kind = new ObjectKind();
        kind.setAware(true);
        set(kind, "noteAware", noteAware);
        return kind;
    }

    /**
     * A bare item on the given kind, not yet in any pile.
     *
     * @param kind the kind to build on
     * @return the item
     */
    private static ItemObject item(ObjectKind kind) {
        return ItemFixture.item(TValue.TV_POTION).kind(kind).build();
    }

    /**
     * A modifier rune with the given auto-inscription note, the same fixture
     * {@code ObjectIgnoreApplyAutoinscriptionTest} uses.
     *
     * @param modifier the modifier this rune represents
     * @param note     the auto-inscription to give it
     * @return the rune
     */
    private static Rune modRune(ObjectModifier modifier, String note) {
        Rune rune = new Rune(new RuneVariety.ModKey(modifier, null));
        rune.setNote(note);
        return rune;
    }

    /**
     * A ring carrying the given modifier at a nonzero value, built by hand rather than through
     * {@link ItemFixture} so the modifier map holds exactly one entry - the shape
     * {@code ObjectIgnoreApplyAutoinscriptionTest.RuneOrdering} uses.
     *
     * @param modifier the modifier the ring carries
     * @return the item
     */
    private static ItemObject ringWith(ObjectModifier modifier) {
        return new ItemObject(kind(null), null, null, null, Loc.zero, TValue.TV_RING, 0,
                "0", 0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), Map.of(modifier, 2), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, null);
    }

    @BeforeEach
    void setUp() throws Exception {
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);

        player = new Player();
        level = new Chunk("level", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
        level.setCurrentLevel(level);

        savedCave = GameState.getCave();
        GameState.setCave(level);

        savedRunes = ObjectRegistry.getRunes();

        // Message coalesces a repeat of the newest entry into a "(xN)" count, and the log is static.
        Field log = uk.co.jackoftradesltd.middle.Message.class.getDeclaredField("messageLog");
        log.setAccessible(true);
        ((Deque<?>) log.get(null)).clear();
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
        GameState.setCave(savedCave);
        ObjectRegistry.setRunes(savedRunes);
    }

    @Test
    @DisplayName("does nothing on an empty square")
    void doesNothingOnAnEmptySquare() {
        ObjectIgnore.autoinscribeGround(player);

        assertTrue(bus.messages.isEmpty());
    }

    /**
     * An event bus that records the messages the middle layer raises, the same technique
     * {@code ObjectIgnoreApplyAutoinscriptionTest} uses.
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * The text of every message event seen, in order.
         */
        private final List<String> messages = new ArrayList<>();

        @Override
        public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandlerType(GameEventType eventType) {
        }

        @Override
        public void gameEventDispatch(GameEventType eventType, GameEventData data) {
            if (data instanceof EventDataMessage message) {
                messages.add(message.message());
            }
        }
    }

    /**
     * The guard that decides whether a kind-level autoinscription ever reaches a floor item.
     */
    @Nested
    @DisplayName("the carried guard")
    class Guards {

        /**
         * {@code applyAutoinscription}'s "don't inscribe unless carried" guard reads
         * {@code ObjectUtils.isCarried}, which tests {@code player.getGear()} - a floor item is
         * never in that pile, so the guard always stops the kind-note side here, whatever the
         * kind's autoinscription is configured to be.
         */
        @Test
        @DisplayName("a kind note is never applied to a floor item")
        void aKindNoteIsNeverAppliedToAFloorItem() {
            ItemObject obj = item(kind("{tried}"));
            level.getSquare(player.getGrid()).getObjectPile().insertEnd(obj);

            ObjectIgnore.autoinscribeGround(player);

            assertNull(obj.getNote());
            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * The rune side, which runs before the carried guard and so is the part of
     * {@code applyAutoinscription} a floor item can actually trigger.
     */
    @Nested
    @DisplayName("the ordinary path")
    class Success {

        /**
         * A single floor item with a rune the player knows is inscribed with that rune's note,
         * despite never passing the carried guard - {@code runesAutoinscribe} runs unconditionally
         * before that guard is reached.
         */
        @Test
        @DisplayName("a rune note reaches a single floor item")
        void aRuneNoteReachesASingleFloorItem() {
            ObjectRegistry.setRunes(List.of(modRune(ObjectModifier.OM_STR, "{str}")));
            KnownObject knowledge = new KnownObject();
            knowledge.learnModifier(ObjectModifier.OM_STR);
            set(player, "itemKnowledge", knowledge);

            ItemObject obj = ringWith(ObjectModifier.OM_STR);
            level.getSquare(player.getGrid()).getObjectPile().insertEnd(obj);

            ObjectIgnore.autoinscribeGround(player);

            assertEquals("{str}", obj.getNote());
        }

        /**
         * Two floor items, each carrying a different known rune, both get their own rune's note -
         * proving the loop visits every object on the square rather than stopping after the first.
         */
        @Test
        @DisplayName("every item on the floor is visited, not just the first")
        void everyItemOnTheFloorIsVisited() {
            ObjectRegistry.setRunes(List.of(
                    modRune(ObjectModifier.OM_STR, "{str}"),
                    modRune(ObjectModifier.OM_DEX, "{dex}")));
            KnownObject knowledge = new KnownObject();
            knowledge.learnModifier(ObjectModifier.OM_STR);
            knowledge.learnModifier(ObjectModifier.OM_DEX);
            set(player, "itemKnowledge", knowledge);

            ItemObject strRing = ringWith(ObjectModifier.OM_STR);
            ItemObject dexRing = ringWith(ObjectModifier.OM_DEX);
            level.getSquare(player.getGrid()).getObjectPile().insertEnd(strRing);
            level.getSquare(player.getGrid()).getObjectPile().insertEnd(dexRing);

            ObjectIgnore.autoinscribeGround(player);

            assertEquals("{str}", strRing.getNote());
            assertEquals("{dex}", dexRing.getNote());
        }
    }

    /**
     * The bounds guard - the one an earlier version of this method did not have.
     */
    @Nested
    @DisplayName("the bounds guard")
    class Bounds {

        /**
         * Before the guard, {@code cave.getSquare(player.getGrid())} answered {@code null} for an
         * out-of-bounds grid and the next call threw. C's {@code square_object} answers {@code NULL}
         * for the same grid, so its loop never starts; this pins the port at the same outcome -
         * quietly doing nothing - rather than crashing.
         */
        @Test
        @DisplayName("an out-of-bounds grid does nothing rather than throwing")
        void doesNothingWhenTheGridIsOutOfBounds() {
            set(player, "grid", Loc.zero.offset(-1, -1));

            ObjectIgnore.autoinscribeGround(player);

            assertTrue(bus.messages.isEmpty());
        }
    }
}
