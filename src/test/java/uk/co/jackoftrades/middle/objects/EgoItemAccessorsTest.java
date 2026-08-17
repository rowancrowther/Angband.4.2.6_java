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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link EgoItem} accessors the ego-recognition code reads through —
 * {@code isEverSeen}, {@code getFlags}, {@code getModifier}, {@code getElInfo}, {@code getBrands},
 * {@code getSlays} and {@code getCurses}.
 *
 * <p><b>Plain reads, but two of them carry a decision.</b> The collection getters hand back the
 * template's own objects rather than copies, which is right — an ego template is loaded once and
 * read for the rest of the game, and copying its brand set on every recognition test would be waste
 * — but it is a decision that a later defensive copy would quietly reverse, so it is asserted here
 * by identity.
 *
 * <p>{@link EgoItem#getModifier} is the one with real behaviour. C reads
 * {@code ego->modifiers[i]} out of a full-length array, so a modifier the ego does not touch comes
 * back as a zeroed {@code random_value}; the port holds a {@link Map} and answers {@code null}
 * instead. That difference is what
 * {@code Player.knowsEgo} has to absorb, and the case is pinned
 * below so the shape of the answer is not in doubt.
 *
 * <p>Class EgoItemAccessorsTest coded on 260816, commented in full on 260816.
 *
 * @author Rowan Crowther
 */
class EgoItemAccessorsTest {

    /**
     * Builds an ego from the pieces a test cares about, defaulting the rest. The constructor takes
     * twenty-seven arguments because it is fed straight from the parsed data file; the fixture
     * exists so the tests below do not have to say so twenty-seven times.
     */
    private static EgoItem ego(Flag<ObjectFlag> flags,
                               Map<ObjectModifier, Random> modifiers,
                               Map<ElementEnum, ElementInfo> elInfo,
                               Set<Brand> brands,
                               Set<Slay> slays,
                               Map<Curse, CurseData> curses,
                               boolean everSeen) {
        return new EgoItem("of Testing", "a test ego", 1, 0,
                flags, new Flag<>(ObjectFlag.class), new Flag<>(ObjectKindFlag.class),
                modifiers, new HashMap<>(), elInfo, brands, slays, curses,
                0, 0, 0, 0, new ArrayList<ObjectKind>(),
                null, null, null, 0, 0, 0,
                null, null, everSeen);
    }

    /**
     * An ego with nothing on it, for the cases that only read one member.
     */
    private static EgoItem bareEgo() {
        return ego(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new HashMap<>(), false);
    }

    @Test
    @DisplayName("everseen reports what it was built with")
    void everSeenIsCarried() {
        assertFalse(bareEgo().isEverSeen());
        assertTrue(ego(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new HashMap<>(), true).isEverSeen());
    }

    /**
     * The flag set is the template's own, and its contents are readable — both halves matter, since
     * {@code knowsEgo} tests it for subset against the player's knowledge.
     */
    @Test
    @DisplayName("the flag set is shared, not copied")
    void flagsAreShared() {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        flags.on(ObjectFlag.OF_FEATHER);
        EgoItem item = ego(flags, new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new HashMap<>(), false);

        assertSame(flags, item.getFlags());
        assertTrue(item.getFlags().has(ObjectFlag.OF_FEATHER));
    }

    @Test
    @DisplayName("a modifier the ego grants comes back as its range")
    void grantedModifierIsReturned() {
        Random stealth = new Random(0, 0, 1, 4, false);
        Map<ObjectModifier, Random> modifiers = new HashMap<>();
        modifiers.put(ObjectModifier.OM_STEALTH, stealth);
        EgoItem item = ego(new Flag<>(ObjectFlag.class), modifiers, new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new HashMap<>(), false);

        assertSame(stealth, item.getModifier(ObjectModifier.OM_STEALTH));
    }

    /**
     * The divergence from C, stated as a test rather than left to be discovered. C would answer a
     * zeroed {@code random_value} here; the port answers {@code null}, and callers evaluating the
     * range at its extremes have to treat that null as the zero it stands for.
     */
    @Test
    @DisplayName("a modifier the ego does not touch comes back null, where C reads a zero")
    void untouchedModifierIsNull() {
        assertNull(bareEgo().getModifier(ObjectModifier.OM_STEALTH));
    }

    @Test
    @DisplayName("the element info is shared, not copied")
    void elInfoIsShared() {
        Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        elInfo.put(ElementEnum.ELEM_FIRE, fire);
        EgoItem item = ego(new Flag<>(ObjectFlag.class), new HashMap<>(), elInfo,
                new HashSet<>(), new HashSet<>(), new HashMap<>(), false);

        assertSame(elInfo, item.getElInfo());
        assertNotNull(item.getElInfo().get(ElementEnum.ELEM_FIRE));
        assertSame(fire, item.getElInfo().get(ElementEnum.ELEM_FIRE));
    }

    /**
     * The three remaining collections, all read the same way by {@code knowsEgo} and all sharing
     * rather than copying.
     */
    @Test
    @DisplayName("brands, slays and curses are shared, not copied")
    void collectionsAreShared() {
        Set<Brand> brands = new HashSet<>();
        Set<Slay> slays = new HashSet<>();
        Map<Curse, CurseData> curses = new HashMap<>();
        EgoItem item = ego(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                brands, slays, curses, false);

        assertSame(brands, item.getBrands());
        assertSame(slays, item.getSlays());
        assertSame(curses, item.getCurses());
    }

    /**
     * An ego that grants none of them reads as empty rather than null. Most egos are in this state
     * for most of these collections, and {@code knowsEgo} walks all three unconditionally — C guards
     * each with {@code if (ego->brands && …)} because its arrays may genuinely be absent, and the
     * port needs the empty collection so it does not have to.
     */
    @Test
    @DisplayName("an ego granting none of them reads as empty")
    void emptyCollectionsAreEmptyNotNull() {
        EgoItem item = bareEgo();

        assertTrue(item.getBrands().isEmpty());
        assertTrue(item.getSlays().isEmpty());
        assertTrue(item.getCurses().isEmpty());
        assertTrue(item.getElInfo().isEmpty());
    }

    /**
     * Because the collections are shared, a template loaded and later added to reads back the
     * addition. This is the useful side of sharing, and the reason it is not a defect.
     */
    @Test
    @DisplayName("a later addition to a shared collection is visible through the getter")
    void sharingIsLive() {
        Set<Slay> slays = new HashSet<>();
        EgoItem item = ego(new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), slays, new HashMap<>(), false);

        assertTrue(item.getSlays().isEmpty());
        slays.add(null);

        assertFalse(item.getSlays().isEmpty());
    }
}
