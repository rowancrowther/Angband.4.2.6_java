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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectValueCache}, the port of C's {@code struct cached_object_data} and its
 * once-only fill in {@code compute_ui_entry_values_for_object} ({@code ui-entry.c:676-698}).
 *
 * <p>Expected values are derived from the C original directly: {@code if (*cache == NULL)} fills
 * the cache from {@code object_flags_known} when {@code p} is non-null and from {@code object_flags}
 * otherwise, and never touches the cache again once it holds a non-null pointer — regardless of
 * what later calls pass for {@code obj} or {@code p}.
 *
 * <p>Class ObjectValueCacheTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectValueCacheTest {

    private static void assertSameFlags(Flag<ObjectFlag> expected, Flag<ObjectFlag> actual) {
        for (ObjectFlag flag : ObjectFlag.values()) {
            assertTrue(expected.has(flag) == actual.has(flag),
                    () -> "flag " + flag + " differs: expected " + expected.has(flag)
                            + " but was " + actual.has(flag));
        }
    }

    @Test
    void getResolvedFlagsIsNullBeforePopulateFlagsIsCalled() {
        ObjectValueCache cache = new ObjectValueCache();

        assertNull(cache.getResolvedFlags());
    }

    @Test
    void populateFlagsCachesRealFlagsWhenPlayerIsNull() {
        ItemObject item = ItemFixture.item(TValue.TV_SWORD).flags(ObjectFlag.OF_SEE_INVIS).build();
        ObjectValueCache cache = new ObjectValueCache();

        cache.populateFlags(item, null);

        assertSameFlags(item.getFlags(), cache.getResolvedFlags());
    }

    @Test
    void populateFlagsCachesKnownFlagsWhenPlayerIsGiven() {
        ItemObject item = ItemFixture.item(TValue.TV_SWORD).flags(ObjectFlag.OF_SEE_INVIS)
                .fullyKnown().build();
        ObjectValueCache cache = new ObjectValueCache();

        cache.populateFlags(item, new Player());

        assertSameFlags(item.flagsKnown(), cache.getResolvedFlags());
        assertTrue(cache.getResolvedFlags().has(ObjectFlag.OF_SEE_INVIS));
    }

    @Test
    void populateFlagsWithPlayerAndNoKnownCounterpartCachesNothing() {
        // ItemObject.flagsKnown() returns an empty set for an object with no known half —
        // exercising that branch here proves populateFlags really defers to it rather than
        // falling back to the real flags.
        ItemObject item = ItemFixture.item(TValue.TV_SWORD).flags(ObjectFlag.OF_SEE_INVIS).build();
        ObjectValueCache cache = new ObjectValueCache();

        cache.populateFlags(item, new Player());

        assertTrue(cache.getResolvedFlags().isEmpty());
    }

    @Test
    void populateFlagsIsANoOpOnceTheCacheIsFilled() {
        ItemObject first = ItemFixture.item(TValue.TV_SWORD).flags(ObjectFlag.OF_SEE_INVIS).build();
        ItemObject second = ItemFixture.item(TValue.TV_SWORD).flags(ObjectFlag.OF_FREE_ACT)
                .fullyKnown().build();
        ObjectValueCache cache = new ObjectValueCache();

        cache.populateFlags(first, null);
        cache.populateFlags(second, new Player());

        assertTrue(cache.getResolvedFlags().has(ObjectFlag.OF_SEE_INVIS));
        assertFalse(cache.getResolvedFlags().has(ObjectFlag.OF_FREE_ACT));
    }

    @Test
    void setResolvedFlagsBypassesTheOnceOnlyGuard() {
        ItemObject item = ItemFixture.item(TValue.TV_SWORD).flags(ObjectFlag.OF_FREE_ACT).build();
        ObjectValueCache cache = new ObjectValueCache();
        Flag<ObjectFlag> injected = new Flag<>(ObjectFlag.class);
        injected.on(ObjectFlag.OF_SEE_INVIS);

        cache.setResolvedFlags(injected);
        cache.populateFlags(item, null);

        assertTrue(cache.getResolvedFlags().has(ObjectFlag.OF_SEE_INVIS));
        assertFalse(cache.getResolvedFlags().has(ObjectFlag.OF_FREE_ACT));
    }
}
