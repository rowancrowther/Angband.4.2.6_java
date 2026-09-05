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
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
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
 * Tests {@link ObjectIgnore#applyAutoinscription}, the port of C's {@code apply_autoinscription}
 * ({@code obj-ignore.c:242-288}).
 *
 * <p>Two of the cases here pin the bug the port went through before this suite existed: an earlier
 * version inverted the ignore guard, so that {@link Ignored#ignoredItemIsNotInscribed} would have
 * inscribed the ignored item instead of leaving it alone, and an ordinary carried item would have
 * been skipped instead of inscribed. {@link Ignored#unignoringStillInscribesAnIgnoredItem} checks
 * the guard from the other side — the unignoring toggle overrides the ignore mark, so the same
 * item that stays alone above gets inscribed once unignoring is on.
 *
 * <p>{@link ObjectUtils#objectDesc} is still a stub that always answers {@code ""}, so every
 * successful-inscription message here reads {@code "You autoinscribe ."} rather than naming the
 * item — a fact about the stub, not about this method.
 *
 * <p>Class ObjectIgnoreApplyAutoinscriptionTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectIgnoreApplyAutoinscriptionTest {

    /**
     * The bus this test listens on, for the message the successful path sends.
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
     * A kind with the given aware state and autoinscriptions, built via reflection since neither
     * note field has a public setter (mirroring {@code ObjectIgnoreGetAutoinscriptionTest}).
     *
     * @param aware       the kind's aware state
     * @param noteAware   the aware autoinscription, or {@code null} for none
     * @param noteUnaware the unaware autoinscription, or {@code null} for none
     * @return the kind
     */
    private static ObjectKind kind(boolean aware, String noteAware, String noteUnaware) {
        ObjectKind kind = new ObjectKind();
        kind.setAware(aware);
        set(kind, "noteAware", noteAware);
        set(kind, "noteUnaware", noteUnaware);
        return kind;
    }

    /**
     * A bare, uncarried item on the given kind — no known half, so {@code isIgnored} answers
     * {@code false} without needing one, matching the object with no known half in
     * {@code ObjectIgnoreTest}.
     *
     * @param kind the kind to build on
     * @return the item
     */
    private static ItemObject item(ObjectKind kind) {
        return ItemFixture.item(TValue.TV_POTION).kind(kind).build();
    }

    @BeforeEach
    void setUp() throws Exception {
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);

        player = new Player();

        // Message coalesces a repeat of the newest entry into a "(xN)" count, and the log is static.
        Field log = uk.co.jackoftradesltd.middle.Message.class.getDeclaredField("messageLog");
        log.setAccessible(true);
        ((Deque<?>) log.get(null)).clear();
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * Runs the method under test.
     *
     * @param obj the object to inscribe
     * @return the return value: {@code 1} if inscribed, {@code 0} otherwise
     */
    private int apply(ItemObject obj) {
        return ObjectIgnore.applyAutoinscription(player, obj);
    }

    /**
     * An event bus that records the messages the middle layer raises, the same technique
     * {@code ItemObjectMessagingTest} uses to assert on {@code Message.message}.
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
     * The four early-return guards, in C's order.
     */
    @Nested
    @DisplayName("the early-return guards")
    class Guards {

        /**
         * No autoinscription configured for either aware state — nothing to do, and nothing is
         * sent.
         */
        @Test
        @DisplayName("no configured note leaves the object alone")
        void noConfiguredNoteDoesNothing() {
            ItemObject obj = item(kind(true, null, null));
            player.getGear().insertEnd(obj);

            int result = apply(obj);

            assertEquals(0, result);
            assertNull(obj.getNote());
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * An object already carrying a note is never reinscribed, whatever the note is — and here
         * {@code aware} is {@code false}, so the clear-old-note guard above cannot be the reason;
         * this pins the reinscribe guard on its own.
         */
        @Test
        @DisplayName("an already-inscribed object is left untouched")
        void alreadyInscribedIsUntouched() {
            ObjectKind objKind = kind(false, null, "{tried}");
            ItemObject obj = item(objKind);
            obj.setNote("@w1");
            player.getGear().insertEnd(obj);

            int result = apply(obj);

            assertEquals(0, result);
            assertEquals("@w1", obj.getNote());
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * An object the player is not carrying is never inscribed, however its kind is set up.
         */
        @Test
        @DisplayName("an uncarried object is left alone")
        void uncarriedObjectIsLeftAlone() {
            ItemObject obj = item(kind(true, "{tried}", null));
            // deliberately not added to player.getGear()

            int result = apply(obj);

            assertEquals(0, result);
            assertNull(obj.getNote());
            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * The ignore guard — the one an earlier version of the port had inverted.
     */
    @Nested
    @DisplayName("the ignore guard")
    class Ignored {

        /**
         * An item the player is ignoring is left alone: the guard this suite exists to pin. With
         * the inverted guard this port once had, this item would have been inscribed instead.
         */
        @Test
        @DisplayName("an ignored item is not inscribed")
        void ignoredItemIsNotInscribed() {
            ItemObject obj = ItemFixture.item(TValue.TV_POTION)
                    .kind(kind(true, "{tried}", null)).fullyKnown().build();
            obj.getKnown().orNotice(ObjectNotice.OBJ_NOTICE_IGNORE);
            player.getGear().insertEnd(obj);

            int result = apply(obj);

            assertEquals(0, result);
            assertNull(obj.getNote());
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * The other side of the same guard: with unignoring on, the identical ignored item is
         * inscribed anyway, since {@code ignoreItemOK} answers {@code false} whenever unignoring
         * is set, before it even asks whether the item is marked.
         */
        @Test
        @DisplayName("unignoring still inscribes an ignored item")
        void unignoringStillInscribesAnIgnoredItem() {
            ItemObject obj = ItemFixture.item(TValue.TV_POTION)
                    .kind(kind(true, "{tried}", null)).fullyKnown().build();
            obj.getKnown().orNotice(ObjectNotice.OBJ_NOTICE_IGNORE);
            player.getGear().insertEnd(obj);
            set(player, "unignoring", 1);

            int result = apply(obj);

            assertEquals(1, result);
            assertEquals("{tried}", obj.getNote());
            assertEquals(1, bus.messages.size());
            assertEquals("You autoinscribe .", bus.messages.get(0));
        }
    }

    /**
     * The clear-old-note guard: an aware item's stale unaware inscription is dropped only when it
     * is still exactly that inscription.
     */
    @Nested
    @DisplayName("clearing the old unaware note")
    class ClearOldNote {

        /**
         * The note is exactly the unaware autoinscription, the item has become aware, and the
         * aware autoinscription differs — so the stale note is cleared and the aware one takes
         * its place in the same call.
         */
        @Test
        @DisplayName("a stale unaware note is cleared and replaced")
        void staleUnawareNoteIsClearedAndReplaced() {
            ObjectKind objKind = kind(true, "{special}", "{average}");
            ItemObject obj = item(objKind);
            obj.setNote("{average}");
            player.getGear().insertEnd(obj);

            int result = apply(obj);

            assertEquals(1, result);
            assertEquals("{special}", obj.getNote());
        }

        /**
         * A note the player wrote themselves — one that never equalled the unaware
         * autoinscription — survives, because the guard only clears a note that is exactly that
         * autoinscription. This is the case the port once got wrong by testing "differs from the
         * new note" instead of "equals the old one".
         */
        @Test
        @DisplayName("a custom note that was never the unaware note survives")
        void customNoteThatWasNeverUnawareSurvives() {
            ObjectKind objKind = kind(true, "{special}", "{average}");
            ItemObject obj = item(objKind);
            obj.setNote("@w1");
            player.getGear().insertEnd(obj);

            int result = apply(obj);

            assertEquals(0, result, "the reinscribe guard stops it, since the note was kept");
            assertEquals("@w1", obj.getNote());
        }

        /**
         * An unaware item never triggers the clear, whatever its note — the guard is gated on
         * {@code aware} before anything else is tested.
         */
        @Test
        @DisplayName("an unaware item never clears its note")
        void unawareItemNeverClears() {
            ObjectKind objKind = kind(false, "{special}", "{average}");
            ItemObject obj = item(objKind);
            obj.setNote("{average}");
            player.getGear().insertEnd(obj);

            int result = apply(obj);

            assertEquals(0, result);
            assertEquals("{average}", obj.getNote(), "still inscribed, so nothing new is written");
        }
    }

    /**
     * Rune autoinscription runs before the note guards — C's own comment marks this "for now"
     * rather than settled, but the ordering itself is fixed.
     */
    @Nested
    @DisplayName("rune ordering")
    class RuneOrdering {

        /**
         * The rune list as it stood before this test replaced it.
         */
        private List<Rune> savedRunes;

        @BeforeEach
        void saveRunes() {
            savedRunes = ObjectRegistry.getRunes();
        }

        @AfterEach
        void restoreRunes() {
            ObjectRegistry.setRunes(savedRunes);
        }

        /**
         * An object with no kind autoinscription at all still gets its rune autoinscription,
         * because {@code runesAutoinscribe} runs before the {@code note == null} early return —
         * only the return value shows the kind side did nothing.
         */
        @Test
        @DisplayName("a rune note is applied even when the kind has no autoinscription")
        void runeNoteAppliedDespiteNoKindNote() {
            ObjectRegistry.setRunes(List.of(modRune(ObjectModifier.OM_STR, "{str}")));
            ObjectKind objKind = kind(false, null, null);
            ItemObject obj = new ItemObject(objKind, null, null, null, Loc.zero, TValue.TV_RING, 0,
                    "0", 0, 0, 0, 0, 0, "0", 0, 0,
                    new Flag<>(ObjectFlag.class), Map.of(ObjectModifier.OM_STR, 2), new HashMap<>(),
                    new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(),
                    List.of(), null, List.of(), "0", 0, 1,
                    new Flag<>(ObjectNotice.class), 0, 0,
                    ObjectOriginEnum.ORIGIN_NONE, 0, null, null);
            KnownObject knowledge = new KnownObject();
            knowledge.learnModifier(ObjectModifier.OM_STR);
            set(player, "itemKnowledge", knowledge);
            player.getGear().insertEnd(obj);

            int result = apply(obj);

            assertEquals(0, result, "the kind side had nothing to inscribe");
            assertEquals("{str}", obj.getNote(), "but the rune side ran anyway");
        }

        /**
         * A modifier rune with the given auto-inscription note, the same fixture
         * {@code ObjectIgnoreRunesAutoinscribeTest} uses.
         *
         * @param modifier the modifier this rune represents
         * @param note     the auto-inscription to give it
         * @return the rune
         */
        private Rune modRune(ObjectModifier modifier, String note) {
            Rune rune = new Rune(new RuneVariety.ModKey(modifier, null));
            rune.setNote(note);
            return rune;
        }
    }

    /**
     * The ordinary path: every guard passed, the note written, the message sent.
     */
    @Nested
    @DisplayName("the ordinary path")
    class Success {

        /**
         * A carried, unmarked, unaware item with an unaware autoinscription is inscribed and
         * reported.
         */
        @Test
        @DisplayName("a carried item with a configured note is inscribed")
        void carriedItemIsInscribed() {
            ItemObject obj = item(kind(false, null, "{tried}"));
            player.getGear().insertEnd(obj);

            int result = apply(obj);

            assertEquals(1, result);
            assertEquals("{tried}", obj.getNote());
            assertEquals(1, bus.messages.size());
            assertEquals("You autoinscribe .", bus.messages.get(0));
        }
    }
}
