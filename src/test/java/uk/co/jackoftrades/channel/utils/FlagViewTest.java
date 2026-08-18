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

package uk.co.jackoftrades.channel.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link FlagView}, the read-only face of a flag set, added on 260818.
 *
 * <p>The interface exists to restore something C has and Java does not. C passes flag arrays as
 * {@code const bitflag *} wherever a function only reads them, so the compiler enforces at every
 * call site that the callee cannot write. A Java method handed a {@link Flag} can do as it likes
 * whatever the author intended; declaring the parameter as a {@code FlagView} is what puts the
 * guarantee back.
 *
 * <p><b>The guarantee is only as good as the interface's method list, so that list is what is
 * tested here.</b> Adding a single mutator to {@code FlagView} would silently void every
 * read-only promise in the port — the callers would still compile, the types would still read as
 * before, and nothing else in the suite would notice. {@link #theInterfaceExposesNoMutators}
 * therefore names the ten methods the interface is allowed to have and fails on anything else,
 * whether added or removed.
 *
 * <p>The behavioural tests are deliberately thin: {@link Flag} is the only implementation and
 * {@link FlagTest} covers what these methods do. What is checked here is that they are all
 * reachable through the interface, which is the part a test written against {@code Flag} cannot
 * show.
 *
 * <p>Class FlagViewTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
class FlagViewTest {

    /**
     * Every method {@link FlagView} is allowed to declare.
     *
     * <p>Read-only, every one. A name appearing here that mutates, or a mutator appearing on the
     * interface without appearing here, is the failure this list exists to cause.
     */
    private static final Set<String> PERMITTED = Set.of(
            "has", "next", "count", "isEmpty", "isFull",
            "isInter", "isSubset", "isEqual", "test", "testAll");

    /**
     * Builds a populated flag set.
     *
     * @param members the flags to switch on
     * @return a new set holding exactly those flags
     */
    private static Flag<TestFlag> flagsOf(TestFlag... members) {
        Flag<TestFlag> result = new Flag<>(TestFlag.class);
        result.set(members);
        return result;
    }

    /**
     * The interface must not grow a way to change the set.
     */
    @Test
    void theInterfaceExposesNoMutators() {
        Set<String> declared = Arrays.stream(FlagView.class.getDeclaredMethods())
                .map(Method::getName)
                .collect(Collectors.toCollection(TreeSet::new));

        assertEquals(new TreeSet<>(PERMITTED), declared,
                "FlagView's method list has changed; a mutator here would void the read-only "
                        + "guarantee every FlagView-typed parameter in the port relies on");
    }

    @Test
    void flagIsTheOnlyThingThatNeedsToImplementIt() {
        assertTrue(FlagView.class.isAssignableFrom(Flag.class));
    }

    @Test
    void aViewIsIterableSoAnEnhancedForLoopWorks() {
        FlagView<TestFlag> view = flagsOf(TestFlag.ALPHA, TestFlag.GAMMA);

        List<TestFlag> seen = new java.util.ArrayList<>();
        for (TestFlag flag : view) {
            seen.add(flag);
        }

        assertEquals(List.of(TestFlag.ALPHA, TestFlag.GAMMA), seen);
    }

    @Test
    void theQueriesAreAllReachableThroughTheInterface() {
        FlagView<TestFlag> view = flagsOf(TestFlag.ALPHA, TestFlag.BETA);

        assertTrue(view.has(TestFlag.ALPHA));
        assertFalse(view.has(TestFlag.GAMMA));
        assertEquals(2, view.count());
        assertFalse(view.isEmpty());
        assertFalse(view.isFull());
        assertTrue(view.test(List.of(TestFlag.GAMMA, TestFlag.ALPHA)));
        assertFalse(view.testAll(List.of(TestFlag.GAMMA, TestFlag.ALPHA)));
        assertTrue(view.testAll(List.of(TestFlag.ALPHA, TestFlag.BETA)));
    }

    @Test
    void theComparisonsTakeAndAcceptAnotherView() {
        FlagView<TestFlag> view = flagsOf(TestFlag.ALPHA, TestFlag.BETA);
        FlagView<TestFlag> smaller = flagsOf(TestFlag.ALPHA);

        assertTrue(view.isInter(smaller));
        assertTrue(view.isSubset(smaller));
        assertFalse(smaller.isSubset(view));
        assertTrue(view.isEqual(flagsOf(TestFlag.ALPHA, TestFlag.BETA)));
    }

    @Test
    void anEmptyViewReportsItself() {
        FlagView<TestFlag> view = new Flag<>(TestFlag.class);

        assertTrue(view.isEmpty());
        assertEquals(0, view.count());
    }

    @Test
    void aFullViewReportsItself() {
        FlagView<TestFlag> view = flagsOf(TestFlag.values());

        assertTrue(view.isFull());
    }

    /**
     * A view reflects later changes to the set behind it, and is not a frozen value.
     *
     * <p>Worth pinning because the two are easy to conflate. A {@code FlagView} withholds mutation
     * from whoever holds the view; it says nothing about whoever holds the underlying
     * {@link Flag}. Callers that need a value which cannot change need a copy, which is why
     * {@link uk.co.jackoftrades.middle.player.PlayerUpkeep#getRedrawFlags} still takes one.
     */
    @Test
    void aViewTracksTheSetBehindItRatherThanFreezingIt() {
        Flag<TestFlag> backing = flagsOf(TestFlag.ALPHA);
        FlagView<TestFlag> view = backing;

        backing.on(TestFlag.BETA);

        assertTrue(view.has(TestFlag.BETA));
        assertEquals(2, view.count());
    }

    /**
     * A flag domain for the tests; nothing depends on the names, only on there being several.
     */
    private enum TestFlag {
        ALPHA, BETA, GAMMA
    }
}
