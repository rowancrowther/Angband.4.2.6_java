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

package uk.co.jackoftradesltd.middle;

import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerOptions;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Test-side window onto {@link Message}'s private log, shared by {@link MessageTest} and
 * {@link MessageAddTest}. C's {@code message_get}/{@code message_str}/{@code message_count}/
 * {@code message_type} are not ported yet, so the log is read by reflection: entry 0 is the
 * newest, matching C's "age" numbering ({@code message_get(0)} is {@code messages->head}).
 *
 * @author Rowan Crowther
 */
@SuppressWarnings("unchecked")
final class MessageLogProbe {
    private MessageLogProbe() {
    }

    private static Deque<?> log() throws ReflectiveOperationException {
        Field f = Message.class.getDeclaredField("messageLog");
        f.setAccessible(true);
        return (Deque<?>) f.get(null);
    }

    /**
     * Empties the shared log so a test starts from C's freshly initialised {@code messages}.
     */
    static void clear() throws ReflectiveOperationException {
        log().clear();
    }

    /**
     * @return the number of entries in the log
     */
    static int size() throws ReflectiveOperationException {
        return log().size();
    }

    /**
     * @return the log's cap, C's {@code messages->max}
     */
    static int cap() throws ReflectiveOperationException {
        Field f = Message.class.getDeclaredField("queueSize");
        f.setAccessible(true);
        return f.getInt(null);
    }

    private static Object entry(int age) throws ReflectiveOperationException {
        int i = 0;
        for (Object o : log()) {
            if (i++ == age) return o;
        }
        throw new IllegalArgumentException("no entry of age " + age);
    }

    private static Object call(int age, String getter) throws ReflectiveOperationException {
        Object e = entry(age);
        Method m = e.getClass().getDeclaredMethod(getter);
        m.setAccessible(true);
        return m.invoke(e);
    }

    /**
     * @return C's {@code message_str(age)}
     */
    static String text(int age) throws ReflectiveOperationException {
        return (String) call(age, "getText");
    }

    /**
     * @return C's {@code message_count(age)}
     */
    static int count(int age) throws ReflectiveOperationException {
        return (int) call(age, "getCount");
    }

    /**
     * @return C's {@code message_type(age)}
     */
    static MessageType type(int age) throws ReflectiveOperationException {
        return (MessageType) call(age, "getType");
    }

    /**
     * Builds a bare {@link Player} whose only real state is its option set, with {@code use_sound}
     * switched on or off. {@code new Player()} needs the game constants loaded (the pack size
     * comes from {@code constants.txt}), which is far more than {@code sound()} reads, so the
     * constructor is bypassed and just the {@code options} field is filled in. C's {@code sound}
     * reads {@code OPT(player, use_sound)} and nothing else.
     */
    static Player playerWithSound(boolean on) throws ReflectiveOperationException {
        Field unsafeField = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Object unsafe = unsafeField.get(null);
        Player player = (Player) unsafe.getClass().getMethod("allocateInstance", Class.class)
                .invoke(unsafe, Player.class);

        PlayerOptions opts = new PlayerOptions();
        if (on) {
            Field f = PlayerOptions.class.getDeclaredField("options");
            f.setAccessible(true);
            Flag<PlayerOptionEnum> flags = (Flag<PlayerOptionEnum>) f.get(opts);
            flags.on(PlayerOptionEnum.OP_use_sound);
        }
        Field optionsField = Player.class.getDeclaredField("options");
        optionsField.setAccessible(true);
        optionsField.set(player, opts);
        return player;
    }

    /**
     * Records every dispatch, in order, so tests can assert on the sequence of events.
     *
     * @author Rowan Crowther
     */
    static final class CapturingBus implements EventsHandler {
        final List<GameEventType> types = new ArrayList<>();
        final List<GameEventData> payloads = new ArrayList<>();

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

        EventDataMessage payload(int i) {
            assertInstanceOf(EventDataMessage.class, payloads.get(i));
            return (EventDataMessage) payloads.get(i);
        }

        EventDataMessage last() {
            return payload(payloads.size() - 1);
        }
    }
}
