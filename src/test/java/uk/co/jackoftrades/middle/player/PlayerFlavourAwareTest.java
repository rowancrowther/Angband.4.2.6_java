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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.player.enums.PlayerNotice;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player}'s {@code flavourAware} and {@code isCarried} — the ports of C's
 * {@code object_flavor_aware} ({@code obj-knowledge.c:2262}) and {@code object_is_carried}
 * ({@code obj-util.c}).
 *
 * <p><b>The subject of awareness is the kind, and that is what these tests are mostly about.</b>
 * Every assertion about the flavour side reads state off the {@link ObjectKind} rather than off the
 * object handed in, because the object is only the occasion for the discovery — learning what the
 * pink potion is means learning it for every pink potion at once. A port that set the flag on the
 * object would pass a naively written test and lose the game's whole identification model.
 *
 * <p><b>What is deliberately not exercised.</b> The gear refresh at the foot of the method calls
 * {@code setBaseKnown} on every carried object, and that method is recorded as needing work in
 * {@code docs/implementation/260816_functions_implemented.md}; a fixture carrying objects would be
 * testing that instead of this. The pack is therefore left empty and the branch is covered only to
 * the extent of showing it is reached and survived. The floor sweep needs a level and a working
 * {@link uk.co.jackoftrades.middle.cave.Square#lightSpot}, which is currently an empty stub, so the
 * cave is left null — which is also a real case, since awareness can be gained before a level
 * exists.
 *
 * <p>Class PlayerFlavourAwareTest coded on 260816, commented in full on 260816.
 *
 * @author Rowan Crowther
 */
class PlayerFlavourAwareTest {

    private Player player;

    /**
     * Writes a private field declared on the given class, for state a running game would have
     * filled in and this suite does not run.
     *
     * @author Rowan Crowther
     */
    private static void poke(Class<?> owner, Object target, String name, Object value)
            throws Exception {
        Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * An object of an unaware, flavoured kind with a known counterpart and an effect — the state a
     * potion is in when it is first picked up.
     *
     * @author Rowan Crowther
     */
    private static ItemObject unknownPotion() throws Exception {
        ObjectKind kind = new ObjectKind();
        kind.setAware(false);

        ItemObject item = new ItemObject();
        item.setKind(kind);
        // An empty list, not a null: the assertions are about which list object the counterpart ends
        // up holding, and a fresh ItemObject leaves its own effect null, so identity is enough to
        // tell "learned it" from "did not".
        item.setEffect(new ArrayList<Effect>());
        poke(ItemObject.class, item, "known", new ItemObject());

        return item;
    }

    /**
     * Calls a private {@link Player} method taking one {@link ItemObject}. Both methods under test
     * are private and have no public caller that does not drag {@code knowObject} in with it, so
     * reaching them directly is what keeps these tests about them.
     *
     * @author Rowan Crowther
     */
    private Object invoke(String name, ItemObject item) throws Exception {
        Method m = Player.class.getDeclaredMethod(name, ItemObject.class);
        m.setAccessible(true);
        try {
            return m.invoke(player, item);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException runtime) throw runtime;
            throw e;
        }
    }

    /**
     * @author Rowan Crowther
     */
    @BeforeEach
    void setUp() {
        player = new Player();
    }

    /**
     * The awareness flag and the effect that comes with it.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("becoming aware")
    class BecomingAware {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the kind becomes aware, not the object")
        void kindBecomesAware() throws Exception {
            ItemObject potion = unknownPotion();

            invoke("flavourAware", potion);

            assertTrue(potion.getKind().isAware());
        }

        /**
         * An identified flavour is an identified effect, so the counterpart picks it up. C sets
         * {@code obj->known->effect = obj->effect} in the same breath as the flag.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the counterpart learns the effect")
        void counterpartLearnsEffect() throws Exception {
            ItemObject potion = unknownPotion();

            assertNull(potion.getKnown().getEffect());
            invoke("flavourAware", potion);

            assertSame(potion.getEffect(), potion.getKnown().getEffect());
        }

        /**
         * The re-run guard, and the reason the method is safe to call on every pass. C returns at
         * once on an already-aware kind; without that, {@code knowObject} would re-run the floor
         * sweep for every identified piece of jewellery the player owns, every time knowledge
         * changed.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("an already-aware kind is left alone")
        void alreadyAwareKindReturnsEarly() throws Exception {
            ItemObject potion = unknownPotion();
            potion.getKind().setAware(true);

            invoke("flavourAware", potion);

            assertNull(potion.getKnown().getEffect());
            assertTrue(player.getPlayerUpkeep().orNoticeFlag(PlayerNotice.PN_IGNORE));
        }

        /**
         * Neither guard is C's — it asserts on the counterpart and dereferences the kind — but both
         * are reachable in the port, and neither should be an exception.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("an object with no counterpart or no kind is not an error")
        void missingPiecesAreSurvivable() throws Exception {
            ItemObject noCounterpart = new ItemObject();
            noCounterpart.setKind(new ObjectKind());
            ItemObject noKind = new ItemObject();
            poke(ItemObject.class, noKind, "known", new ItemObject());

            assertDoesNotThrow(() -> invoke("flavourAware", noCounterpart));
            assertDoesNotThrow(() -> invoke("flavourAware", noKind));
        }
    }

    /**
     * The ignore fixup — the piece with a decision in it rather than a copy.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the ignore fixup")
    class IgnoreFixup {

        /**
         * The transition the two-flag design exists for. A player who chose to ignore unidentified
         * potions has, in effect, chosen to ignore this one; carrying the decision across is what
         * stops the pile they were stepping over reappearing under a name.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("ignored-while-unaware becomes ignored-while-aware")
        void ignoreDecisionIsCarriedAcross() throws Exception {
            ItemObject potion = unknownPotion();
            potion.getKind().setIgnoredUnaware(true);

            invoke("flavourAware", potion);

            assertTrue(potion.getKind().isIgnoredAware());
        }

        /**
         * And the other way: a kind the player never chose to ignore does not acquire the choice by
         * being identified.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a kind that was not ignored does not become ignored")
        void unignoredKindStaysUnignored() throws Exception {
            ItemObject potion = unknownPotion();

            invoke("flavourAware", potion);

            assertFalse(potion.getKind().isIgnoredAware());
        }

        /**
         * The request for the ignore pass goes out either way, because the set of objects the rules
         * apply to has changed whatever the player's setting for this kind. Reading it back as
         * "already set" is how a caller-free test sees that it was raised.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the ignore pass is requested")
        void ignorePassIsRequested() throws Exception {
            ItemObject potion = unknownPotion();

            invoke("flavourAware", potion);

            assertFalse(player.getPlayerUpkeep().orNoticeFlag(PlayerNotice.PN_IGNORE));
        }
    }

    /**
     * {@code isCarried} — the pack, quiver and worn items are one list, so one containment test
     * answers for all three.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("isCarried")
    class Carried {

        /**
         * @author Rowan Crowther
         */
        private void carrying(ItemObject... items) throws Exception {
            poke(Player.class, player, "gear", new ArrayList<>(List.of(items)));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("an object in the gear is carried")
        void gearObjectIsCarried() throws Exception {
            ItemObject sword = new ItemObject();
            carrying(sword);

            assertTrue((Boolean) invoke("isCarried", sword));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("an object not in the gear is not carried")
        void otherObjectIsNotCarried() throws Exception {
            carrying(new ItemObject());

            assertFalse((Boolean) invoke("isCarried", new ItemObject()));
        }

        /**
         * A fresh player carries nothing — {@link Player}'s constructor gives an empty list, not a
         * null, so the empty case answers rather than throws.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("an empty pack carries nothing")
        void emptyGearCarriesNothing() throws Exception {
            assertFalse((Boolean) invoke("isCarried", new ItemObject()));
        }

        /**
         * The distinction the method draws. An object on the floor is not carried however close the
         * player is standing to it — that is {@code Square.holdsObject}'s question, and the two
         * together are what pick between the two messages {@code knowObject} can print.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("an identical object that is not the carried one is not carried")
        void identicalButDistinctObjectIsNotCarried() throws Exception {
            ItemObject inPack = new ItemObject();
            ItemObject itsTwin = new ItemObject();
            carrying(inPack);

            assertTrue((Boolean) invoke("isCarried", inPack));
            assertFalse((Boolean) invoke("isCarried", itsTwin));
        }

        /**
         * Every object in the gear, not merely the first.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("every object in the gear is carried")
        void allGearObjectsAreCarried() throws Exception {
            ItemObject first = new ItemObject();
            ItemObject second = new ItemObject();
            ItemObject third = new ItemObject();
            carrying(first, second, third);

            assertTrue((Boolean) invoke("isCarried", first));
            assertTrue((Boolean) invoke("isCarried", second));
            assertTrue((Boolean) invoke("isCarried", third));
        }
    }
}
