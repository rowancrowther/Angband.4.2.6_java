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

package uk.co.jackoftradesltd.backend;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.strings.Quark;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AngbandModule}, the port of the C {@code struct init_module}
 * ({@code init.h}).
 *
 * <p>An interface has no behaviour of its own, so these tests pin the shape of the contract
 * against the C struct (a name plus an {@code init} and a {@code cleanup} entry) and the way
 * {@code init_angband()} and {@code cleanup_angband()} in {@code init.c} use it: both loops
 * walk the {@code modules[]} roster forwards, and {@code cleanup} is never walked in reverse.
 * The Java roster and its loops are not ported yet, so the walks here are written out in the
 * test against recording modules, as a statement of what the roster must do once it exists.
 *
 * <p>Class AngbandModuleTest coded on 261015, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
class AngbandModuleTest {

    /**
     * A module that records every call into a shared log, so a test can read back the order
     * the lifecycle methods ran in.
     * <p>Class RecordingModule coded on 261015, commented in full on 261015.
     */
    private static class RecordingModule implements AngbandModule {
        private final String name;
        private final List<String> log;

        RecordingModule(String name, List<String> log) {
            this.name = name;
            this.log = log;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void init() {
            log.add("init:" + name);
        }

        @Override
        public void cleanup() {
            log.add("cleanup:" + name);
        }
    }

    /**
     * A module shaped like {@code ui_player_module}: C's {@code init_ui_player()} has a body that
     * is only a comment, while {@code cleanup_ui_player()} releases the character-sheet config.
     * <p>Class LazyModule coded on 261015, commented in full on 261015.
     */
    private static class LazyModule implements AngbandModule {
        private boolean released;

        @Override
        public String getName() {
            return "ui-player";
        }

        @Override
        public void init() {
            // Nothing to do; lazy initialization, as in C.
        }

        @Override
        public void cleanup() {
            released = true;
        }
    }

    /**
     * The C struct has exactly three members - {@code name}, {@code init}, {@code cleanup} - and the
     * Java interface should carry exactly those three, no more.
     */
    @Test
    void interfaceDeclaresExactlyTheThreeStructMembers() {
        assertTrue(AngbandModule.class.isInterface());

        Set<String> names = new TreeSet<>();
        for (Method method : AngbandModule.class.getDeclaredMethods()) {
            assertTrue(Modifier.isAbstract(method.getModifiers()), method.getName() + " should be abstract");
            names.add(method.getName());
        }

        assertEquals(Set.of("getName", "init", "cleanup"), names);
    }

    /**
     * {@code name} is a label, {@code init} and {@code cleanup} are {@code void (*)(void)}: the Java
     * signatures should be a {@link String} getter and two no-argument void methods.
     */
    @Test
    void signaturesMatchTheStructMembers() throws NoSuchMethodException {
        Method getName = AngbandModule.class.getMethod("getName");
        Method init = AngbandModule.class.getMethod("init");
        Method cleanup = AngbandModule.class.getMethod("cleanup");

        assertEquals(String.class, getName.getReturnType());
        assertEquals(void.class, init.getReturnType());
        assertEquals(void.class, cleanup.getReturnType());
        assertEquals(0, getName.getParameterCount());
        assertEquals(0, init.getParameterCount());
        assertEquals(0, cleanup.getParameterCount());
    }

    /**
     * {@code init_angband()} walks {@code modules[]} from the first entry to the last, calling each
     * {@code init} in turn. The order of the C roster starts z-quark, messages, ui-visuals, so a
     * three-module roster in that order must initialize in that order.
     */
    @Test
    void initWalksTheRosterForwards() {
        List<String> log = new ArrayList<>();
        List<AngbandModule> roster = List.of(
                new RecordingModule("z-quark", log),
                new RecordingModule("messages", log),
                new RecordingModule("ui-visuals", log));

        for (AngbandModule module : roster) {
            module.init();
        }

        assertEquals(List.of("init:z-quark", "init:messages", "init:ui-visuals"), log);
    }

    /**
     * {@code cleanup_angband()} walks the same roster in the same forward order - it is not the
     * reverse of {@code init}, even though a stack-style teardown would be.
     */
    @Test
    void cleanupWalksTheRosterForwardsNotReversed() {
        List<String> log = new ArrayList<>();
        List<AngbandModule> roster = List.of(
                new RecordingModule("z-quark", log),
                new RecordingModule("messages", log),
                new RecordingModule("ui-visuals", log));

        for (AngbandModule module : roster) {
            module.init();
        }
        log.clear();
        for (AngbandModule module : roster) {
            module.cleanup();
        }

        assertEquals(List.of("cleanup:z-quark", "cleanup:messages", "cleanup:ui-visuals"), log);
    }

    /**
     * In C a module's {@code init} may be absent or do nothing; in Java the method must exist but may
     * have an empty body. Driving such a module through the full cycle must not fail, and only the
     * half with work to do should have an effect.
     */
    @Test
    void emptyInitHalfIsAcceptable() {
        LazyModule module = new LazyModule();

        module.init();
        assertFalse(module.released, "init of a lazy module must not do the cleanup's work");

        module.cleanup();
        assertTrue(module.released);
    }

    /**
     * Nothing in the contract forbids another init/cleanup cycle (the C game does this on
     * {@code play_again}), and the name is a label that survives it.
     */
    @Test
    void moduleCanBeCycledMoreThanOnce() {
        List<String> log = new ArrayList<>();
        AngbandModule module = new RecordingModule("messages", log);

        module.init();
        module.cleanup();
        module.init();
        module.cleanup();

        assertEquals("messages", module.getName());
        assertEquals(List.of("init:messages", "cleanup:messages", "init:messages", "cleanup:messages"), log);
    }

    /**
     * {@link Quark} is the one real implementor so far (C's {@code z_quark_module}, first in
     * {@code modules[]}). Used purely through the interface type it must come up with a name and be
     * torn down without error.
     */
    @Test
    void quarkConformsToTheContract() {
        AngbandModule module = new Quark();
        assertNull(module.getName(), "Quark has no name until init, unlike C's static .name literal");

        module.init();
        assertNotNull(module.getName());

        assertDoesNotThrow(module::cleanup);
    }
}
