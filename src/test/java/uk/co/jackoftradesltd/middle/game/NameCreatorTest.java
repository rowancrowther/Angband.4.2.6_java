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

package uk.co.jackoftradesltd.middle.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.player.enums.RandnameType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the one property of {@code NameCreator.randnameMake} — C's {@code randname_make} in
 * {@code randname.c} — that the older {@code PlayerNameRandnameMakeTest} cannot tell apart from
 * a simpler generator: the context is the <em>pair</em> of preceding letters, not the last
 * letter alone.
 *
 * <p>Expected values come from tracing {@code build_prob} and the walk in {@code randname_make}
 * by hand over a two-word list, not from running the Java.
 *
 * <p>Class NameCreatorTest coded on 261001, commented in full on 261001.
 */
@DisplayName("NameCreator.randnameMake — pair context")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
public class NameCreatorTest {
    /**
     * Enough draws that both first letters are certain to turn up.
     */
    private static final int MANY = 2000;

    @BeforeEach
    void setUp() {
        // Seeded only so a failure reproduces; no expectation below depends on the stream.
        RandomValueUtils.stateInit(261001L);
    }

    /**
     * {@code "abc"} and {@code "dbe"} share the middle letter {@code b} but not what follows it.
     * {@code build_prob} files the successors under {@code (a,b)→c} and {@code (d,b)→e}, so once
     * the walk has chosen a first letter the rest of the word is forced: {@code a} gives
     * {@code "abc"} and {@code d} gives {@code "dbe"}. A generator keyed on the last letter
     * alone would see {@code b→c} and {@code b→e} at equal weight and could return
     * {@code "abe"} or {@code "dbc"}; C cannot, because {@code c_prev} is part of the row.
     */
    @Test
    @DisplayName("keeps a shared middle letter from mixing two words")
    void sharedMiddleLetterDoesNotMix() {
        MiscRegistry.setNames(List.of(new Name(RandnameType.RANDNAME_TOLKIEN.ordinal() + 1,
                new ArrayList<>(List.of("abc", "dbe")))));
        Set<String> seen = new HashSet<>();

        for (int i = 0; i < MANY; i++) {
            seen.add(NameCreator.randnameMake(RandnameType.RANDNAME_TOLKIEN, 1, 8));
        }

        assertEquals(Set.of("abc", "dbe"), seen);
    }
}
