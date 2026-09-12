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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ObjectUtils#lookupKind(TValue, int)} and {@link ObjectUtils#lookupKind(TValue, String)},
 * the ports of C's {@code lookup_kind} and the {@code lookup_kind(tval, lookup_sval(tval, name))}
 * pattern C itself composes at some call sites ({@code obj-util.c}).
 *
 * <p>Both overloads delegate the actual search to {@link ObjectRegistry}, whose own tests
 * ({@code ObjectRegistryTest}) already pin the search semantics (the numeric index, and the
 * digit-or-name {@code lookup_sval} resolution). What is specific to {@link ObjectUtils} — and so
 * what these tests are for — is the miss behaviour: a "No object" message signalled through
 * {@link uk.co.jackoftradesltd.middle.Message} before {@code null} comes back, matching C's
 * {@code msg(...); return NULL;} shape.
 *
 * <p>The registry is global static state also touched by other suites, so {@link #snapshot()} and
 * {@link #restore()} save and put back every field {@link #isolate()} writes.
 *
 * <p>Class ObjectUtilsLookupKindTest coded on 260904, commented in full on 260904.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsLookupKindTest {

    private static final List<String> TOUCHED = List.of("objectKinds", "kindsByTvalSval", "objectBases");
    private static final Map<String, Object> saved = new HashMap<>();

    private ObjectBase swordBase;
    private EventsHandler realBus;
    private CapturingBus bus;

    @BeforeAll
    static void snapshot() throws Exception {
        for (String name : TOUCHED) {
            saved.put(name, get(name));
        }
    }

    @AfterAll
    static void restore() throws Exception {
        for (String name : TOUCHED) {
            set(name, saved.get(name));
        }
    }

    private static Field field(String name) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static Object get(String name) throws Exception {
        return field(name).get(null);
    }

    private static void set(String name, Object value) throws Exception {
        field(name).set(null, value);
    }

    private static ObjectKind kind(String name, ObjectBase base, String svalName) throws Exception {
        ObjectKind k = new ObjectKind();
        for (Map.Entry<String, Object> entry : Map.of(
                        "name", name, "base", base, "tValue", base.gettVal(), "sValueName", svalName)
                .entrySet()) {
            Field f = ObjectKind.class.getDeclaredField(entry.getKey());
            f.setAccessible(true);
            f.set(k, entry.getValue());
        }
        return k;
    }

    @BeforeEach
    void isolate() throws Exception {
        set("objectKinds", new ArrayList<ObjectKind>());
        set("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        swordBase = new ObjectBase(TValue.TV_SWORD, "sword", ColourEnum.COLOUR_WHITE,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), -1, -1);
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(swordBase)));

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    // ---- lookupKind(TValue, int) ------------------------------------------

    @Test
    void numericOverloadReturnsTheMatchingKindWithoutSignallingAMessage() throws Exception {
        ObjectKind dagger = kind("Dagger", swordBase, "Dagger");
        ObjectRegistry.addObjectKind(dagger);

        assertSame(dagger, ObjectUtils.lookupKind(TValue.TV_SWORD, dagger.getsVal()));
        assertTrue(bus.payloads.isEmpty(), "a hit signals nothing");
    }

    @Test
    void numericOverloadMissReturnsNullAndSignalsANoObjectMessage() throws Exception {
        ObjectRegistry.addObjectKind(kind("Dagger", swordBase, "Dagger"));

        ObjectKind result = ObjectUtils.lookupKind(TValue.TV_SWORD, 99);

        assertNull(result);
        assertEquals(GameEventType.EVENT_MESSAGE, bus.types.get(0));
        assertEquals(MessageType.MSG_GENERIC, bus.lastMessage().type());
        assertEquals("No object: sword:99", bus.lastMessage().message());
    }

    // ---- lookupKind(TValue, String) ----------------------------------------

    @Test
    void stringOverloadMatchesBySvalNameCaseInsensitively() throws Exception {
        ObjectKind dagger = kind("Dagger", swordBase, "Dagger");
        ObjectRegistry.addObjectKind(dagger);

        assertSame(dagger, ObjectUtils.lookupKind(TValue.TV_SWORD, "dAgGeR"));
        assertTrue(bus.payloads.isEmpty(), "a hit signals nothing");
    }

    @Test
    void stringOverloadTreatsAnAllDigitReferenceAsALiteralSval() throws Exception {
        ObjectKind dagger = kind("Dagger", swordBase, "Dagger");
        ObjectKind rapier = kind("Rapier", swordBase, "Rapier");
        ObjectRegistry.addObjectKind(dagger);
        ObjectRegistry.addObjectKind(rapier);

        assertSame(rapier, ObjectUtils.lookupKind(TValue.TV_SWORD, String.valueOf(rapier.getsVal())),
                "a digit string names the sval directly, the same result as the numeric overload");
        assertSame(ObjectUtils.lookupKind(TValue.TV_SWORD, rapier.getsVal()),
                ObjectUtils.lookupKind(TValue.TV_SWORD, String.valueOf(rapier.getsVal())));
    }

    @Test
    void stringOverloadMissReturnsNullAndSignalsANoObjectMessageWithTheRawReference() throws Exception {
        ObjectRegistry.addObjectKind(kind("Dagger", swordBase, "Dagger"));

        ObjectKind result = ObjectUtils.lookupKind(TValue.TV_SWORD, "no such kind");

        assertNull(result);
        assertEquals("No object: sword:no such kind", bus.lastMessage().message(),
                "unlike the numeric overload, the message names the unresolved reference verbatim");
    }

    /**
     * Records every dispatch so assertions can be made about what {@link ObjectUtils#lookupKind}
     * signalled on a miss — the same capture shape {@code MessageTest} uses.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<GameEventType> types = new ArrayList<>();
        private final List<GameEventData> payloads = new ArrayList<>();

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
            types.add(eventType);
            payloads.add(data);
        }

        private EventDataMessage lastMessage() {
            assertInstanceOf(EventDataMessage.class, payloads.get(payloads.size() - 1));
            return (EventDataMessage) payloads.get(payloads.size() - 1);
        }
    }
}
