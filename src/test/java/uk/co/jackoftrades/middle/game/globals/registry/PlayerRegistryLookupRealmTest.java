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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link PlayerRegistry#lookupRealm}, added on 260831.
 *
 * <p>The expectations come from C's {@code lookup_realm} ({@code player.c:130-143}), not from the
 * port: C walks the {@code realms} linked list in load order and compares each entry with
 * {@code my_stricmp}, which upper-cases both sides a character at a time — so the match is
 * case-insensitive, and the first entry to match wins. The four names the walk can meet are the
 * ones {@code realm.txt} defines: arcane, divine, nature and shadow.
 *
 * <p>The case fold is the reason this suite exists. A fold applied to only one side, or applied in
 * opposite directions on the two sides, still compiles and still passes an exact-spelling test, but
 * fails every real lookup — {@code class.txt} spells the realm however its author chose, and
 * {@code ClassSpellBookAssembler} hands that spelling straight through. So the mixed- and
 * upper-case cases below are the load-bearing ones.
 *
 * <p>A miss is fatal in C: the walk falls out of the loop into {@code quit_fmt("Failed to find %s
 * magic realm", name)}, which never returns. The port keeps the severity as an
 * {@link IllegalArgumentException} carrying that same message, so the tests assert the text as well
 * as the type. The uninitialised-registry case has no C counterpart at all — C's {@code realms} is
 * simply an empty list before parsing — and is a wiring fault rather than a missing datum, so it is
 * separated out as an {@link IllegalStateException}.
 *
 * <p>The registry is global static state shared with the other suites, so the loaded list is saved
 * and put back around every test.
 *
 * <p>Class PlayerRegistryLookupRealmTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
class PlayerRegistryLookupRealmTest {

    /**
     * Whatever the registry held before this test, put back afterwards.
     */
    private Object saved;

    /**
     * @return the registry's private list of loaded realms, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field field() throws Exception {
        Field f = PlayerRegistry.class.getDeclaredField("realms");
        f.setAccessible(true);
        return f;
    }

    /**
     * Builds a realm carrying the given name; the rest of the fields are flavour the lookup never
     * reads.
     *
     * @param name the realm's name
     * @return the realm
     */
    private static MagicRealm realm(String name) {
        return new MagicRealm(name, Stats.STAT_INT, "cast", "spell", TValue.TV_MAGIC_BOOK);
    }

    /**
     * Loads the registry with the four realms {@code realm.txt} defines, in file order.
     *
     * @throws Exception if the field cannot be reached
     */
    private static void loadTheGameRealms() throws Exception {
        List<MagicRealm> all = new ArrayList<>();
        all.add(realm("arcane"));
        all.add(realm("divine"));
        all.add(realm("nature"));
        all.add(realm("shadow"));
        field().set(null, all);
    }

    @BeforeEach
    void snapshot() throws Exception {
        saved = field().get(null);
    }

    @AfterEach
    void restore() throws Exception {
        field().set(null, saved);
    }

    @Test
    void findsARealmSpeltExactlyAsItWasLoaded() throws Exception {
        loadTheGameRealms();

        assertEquals("arcane", PlayerRegistry.lookupRealm("arcane").getName());
        assertEquals("divine", PlayerRegistry.lookupRealm("divine").getName());
        assertEquals("nature", PlayerRegistry.lookupRealm("nature").getName());
        assertEquals("shadow", PlayerRegistry.lookupRealm("shadow").getName());
    }

    @Test
    void findsARealmWhateverCaseTheCallerSpeltItIn() throws Exception {
        loadTheGameRealms();

        assertEquals("arcane", PlayerRegistry.lookupRealm("Arcane").getName());
        assertEquals("arcane", PlayerRegistry.lookupRealm("ARCANE").getName());
        assertEquals("divine", PlayerRegistry.lookupRealm("DiViNe").getName());
        assertEquals("shadow", PlayerRegistry.lookupRealm("SHADOW").getName());
    }

    @Test
    void findsARealmWhateverCaseItWasLoadedIn() throws Exception {
        field().set(null, List.of(realm("Arcane"), realm("NATURE")));

        assertEquals("Arcane", PlayerRegistry.lookupRealm("arcane").getName());
        assertEquals("NATURE", PlayerRegistry.lookupRealm("nature").getName());
    }

    @Test
    void handsBackTheStoredInstanceRatherThanACopy() throws Exception {
        MagicRealm stored = realm("arcane");
        field().set(null, List.of(realm("divine"), stored, realm("nature")));

        assertSame(stored, PlayerRegistry.lookupRealm("ARCANE"));
    }

    @Test
    void takesTheFirstMatchInLoadOrder() throws Exception {
        MagicRealm first = realm("arcane");
        MagicRealm second = realm("Arcane");
        field().set(null, List.of(first, second));

        assertSame(first, PlayerRegistry.lookupRealm("arcane"));
    }

    @Test
    void failsWithCsMessageWhenNoRealmBearsTheName() throws Exception {
        loadTheGameRealms();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> PlayerRegistry.lookupRealm("sorcery"));

        assertEquals("Failed to find sorcery magic realm", e.getMessage());
    }

    @Test
    void failsOnAPartialNameBecauseCComparesWholeStrings() throws Exception {
        loadTheGameRealms();

        assertThrows(IllegalArgumentException.class, () -> PlayerRegistry.lookupRealm("arc"));
        assertThrows(IllegalArgumentException.class, () -> PlayerRegistry.lookupRealm("arcanes"));
    }

    @Test
    void failsWhenNoRealmsWereLoadedAtAll() throws Exception {
        field().set(null, List.of());

        assertThrows(IllegalArgumentException.class, () -> PlayerRegistry.lookupRealm("arcane"));
    }

    @Test
    void refusesToReadTheRegistryBeforeItIsLoaded() throws Exception {
        field().set(null, null);

        assertThrows(IllegalStateException.class, () -> PlayerRegistry.lookupRealm("arcane"));
    }
}
