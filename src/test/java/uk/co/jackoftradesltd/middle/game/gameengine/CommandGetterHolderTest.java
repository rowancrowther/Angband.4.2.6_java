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

package uk.co.jackoftradesltd.middle.game.gameengine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link CommandGetterHolder}, the Java home of C's file-scope
 * {@code errr (*cmd_get_hook)(cmd_context c)} (declared in {@code cmd-core.h}, defined in
 * {@code cmd-core.c}).
 *
 * <p>The expected values come from C, not from the Java. In C the pointer starts {@code NULL}, a
 * front-end assigns it ({@code cmd_get_hook = textui_get_cmd} in {@code main.c}), and the one call
 * site, {@code cmd_get_hook(CTX_GAME)} in {@code play_game()} ({@code ui-game.c}), passes the
 * context straight through and ignores the {@code errr} it gets back. {@code textui_get_cmd} itself
 * returns 1 on every path, which is the value the always-installed default stub reports. The port
 * differs from C in two places that are pinned here: the stub replaces the {@code NULL} crash, and
 * {@code setInstance(null)} is rejected instead of unsetting the pointer.
 *
 * <p>Class CommandGetterHolderTest coded on 261014, commented in full on 261014.
 */
class CommandGetterHolderTest {

    /**
     * A fake front-end that records what it was asked for and answers with a fixed {@code errr}.
     *
     * <p>Class RecordingGetter coded on 261014, commented in full on 261014.
     */
    private static final class RecordingGetter implements CommandGetter {
        private final int status;
        private CommandContext lastContext;
        private CommandQueue lastQueue;
        private int calls;

        RecordingGetter(int status) {
            this.status = status;
        }

        @Override
        public int getCommand(CommandContext context, CommandQueue queue) {
            calls++;
            lastContext = context;
            lastQueue = queue;
            return status;
        }
    }

    @BeforeEach
    void startUnset() {
        CommandGetterHolder.resetInstance();
    }

    @AfterEach
    void restoreDefault() {
        CommandGetterHolder.resetInstance();
    }

    @Test
    @DisplayName("with nothing installed the instance is a DefaultCommandGetter, never null")
    void startsAsDefault() {
        CommandGetter getter = CommandGetterHolder.getInstance();

        assertNotNull(getter);
        assertInstanceOf(DefaultCommandGetter.class, getter);
    }

    @Test
    @DisplayName("the default reports errr 1 for every context, as textui_get_cmd does, and queues nothing")
    void defaultReportsFailureForEveryContext() {
        CommandQueue queue = new CommandQueue(null);

        for (CommandContext context : CommandContext.values()) {
            assertEquals(1, CommandGetterHolder.getInstance().getCommand(context, queue),
                    "context " + context);
        }
        assertNull(queue.commandQueuePeek());
    }

    @Test
    @DisplayName("setInstance makes getInstance return that exact object")
    void setInstanceInstalls() {
        CommandGetter fake = new RecordingGetter(0);

        CommandGetterHolder.setInstance(fake);

        assertSame(fake, CommandGetterHolder.getInstance());
    }

    @Test
    @DisplayName("an installed getter receives the context and queue unchanged and its errr comes back")
    void installedGetterIsCalledThrough() {
        RecordingGetter fake = new RecordingGetter(0);
        CommandQueue queue = new CommandQueue(null);
        CommandGetterHolder.setInstance(fake);

        int status = CommandGetterHolder.getInstance().getCommand(CommandContext.CTX_GAME, queue);

        assertEquals(0, status);
        assertEquals(1, fake.calls);
        assertSame(CommandContext.CTX_GAME, fake.lastContext);
        assertSame(queue, fake.lastQueue);
    }

    @Test
    @DisplayName("a non-zero errr from the installed getter is passed back untouched")
    void nonZeroStatusPassesThrough() {
        CommandGetterHolder.setInstance(new RecordingGetter(7));

        assertEquals(7, CommandGetterHolder.getInstance()
                .getCommand(CommandContext.CTX_GAME, new CommandQueue(null)));
    }

    @Test
    @DisplayName("installing a second implementation replaces the first")
    void secondInstallReplacesFirst() {
        CommandGetter first = new RecordingGetter(0);
        CommandGetter second = new RecordingGetter(1);

        CommandGetterHolder.setInstance(first);
        CommandGetterHolder.setInstance(second);

        assertSame(second, CommandGetterHolder.getInstance());
    }

    @Test
    @DisplayName("setInstance(null) throws IllegalArgumentException and leaves the installed instance alone")
    void nullIsRejectedAndInstanceKept() {
        CommandGetter fake = new RecordingGetter(0);
        CommandGetterHolder.setInstance(fake);

        assertThrows(IllegalArgumentException.class, () -> CommandGetterHolder.setInstance(null));

        assertSame(fake, CommandGetterHolder.getInstance());
    }

    @Test
    @DisplayName("setInstance(null) on a fresh holder leaves the default in place, so getInstance is still non-null")
    void nullOnFreshHolderKeepsDefault() {
        assertThrows(IllegalArgumentException.class, () -> CommandGetterHolder.setInstance(null));

        assertInstanceOf(DefaultCommandGetter.class, CommandGetterHolder.getInstance());
    }

    @Test
    @DisplayName("resetInstance discards the fake and restores a DefaultCommandGetter")
    void resetRestoresDefault() {
        CommandGetter fake = new RecordingGetter(0);
        CommandGetterHolder.setInstance(fake);

        CommandGetterHolder.resetInstance();

        CommandGetter restored = CommandGetterHolder.getInstance();
        assertNotSame(fake, restored);
        assertInstanceOf(DefaultCommandGetter.class, restored);
        assertEquals(1, restored.getCommand(CommandContext.CTX_GAME, new CommandQueue(null)));
    }

    @Test
    @DisplayName("the holder is final with a single private constructor, so it cannot be subclassed or instantiated")
    void lockedDown() {
        assertTrue(Modifier.isFinal(CommandGetterHolder.class.getModifiers()));

        Constructor<?>[] constructors = CommandGetterHolder.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
    }
}
