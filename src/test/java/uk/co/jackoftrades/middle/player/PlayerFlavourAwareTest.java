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
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.ObjectUtils;
import uk.co.jackoftrades.middle.player.enums.PlayerNotice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

/**
 * Tests {@link PlayerKnowledge#flavourAware} and {@link ObjectUtils#isCarried} — the ports of C's
 * {@code object_flavor_aware} ({@code obj-knowledge.c:2262}) and {@code object_is_carried}
 * ({@code obj-util.c}).
 *
 * <p><b>Both have left {@link Player}, and each is now called directly.</b>
 * {@code flavourAware} has moved onto {@link PlayerKnowledge}, where it is public and static and
 * takes the player, the level and the gear as arguments rather than reading them off {@code this};
 * that is why the fixtures below hand over {@code player.getCave()} and {@code player.getGear()} —
 * the same two fields the method used to read for itself. {@code isCarried} has moved the other
 * way, onto {@link ObjectUtils} with the object subsystem it belongs to, and became public and
 * static in the process, so the reflection this suite used to need for it has gone.
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
 * <p>Class PlayerFlavourAwareTest coded on 260816, commented in full on 260816, followed onto
 * {@link PlayerKnowledge} and {@link ObjectUtils} on 260901.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerFlavourAwareTest {

    private Player player;

    /**
     * Writes a private field declared on the given class, for state a running game would have
     * filled in and this suite does not run.
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
     * Makes the player aware of the given object's kind, with the level and the pack they currently
     * hold. A one-line wrapper so the tests below read as they did before the method moved, and so
     * that a later change to its argument list is edited in one place.
     */
    private void flavourAware(ItemObject item) {
        PlayerKnowledge.flavourAware(player, player.getCave(), player.getGear(), item);
    }

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

        @Test
        @DisplayName("the kind becomes aware, not the object")
        void kindBecomesAware() throws Exception {
            ItemObject potion = unknownPotion();

            flavourAware(potion);

            assertTrue(potion.getKind().isAware());
        }

        /**
         * An identified flavour is an identified effect, so the counterpart picks it up. C sets
         * {@code obj->known->effect = obj->effect} in the same breath as the flag.
         */
        @Test
        @DisplayName("the counterpart learns the effect")
        void counterpartLearnsEffect() throws Exception {
            ItemObject potion = unknownPotion();

            assertNull(potion.getKnown().getEffect());
            flavourAware(potion);

            assertSame(potion.getEffect(), potion.getKnown().getEffect());
        }

        /**
         * The re-run guard, and the reason the method is safe to call on every pass. C returns at
         * once on an already-aware kind; without that, {@code knowObject} would re-run the floor
         * sweep for every identified piece of jewellery the player owns, every time knowledge
         * changed.
         */
        @Test
        @DisplayName("an already-aware kind is left alone")
        void alreadyAwareKindReturnsEarly() throws Exception {
            ItemObject potion = unknownPotion();
            potion.getKind().setAware(true);

            flavourAware(potion);

            assertNull(potion.getKnown().getEffect());
            assertTrue(player.getPlayerUpkeep().orNoticeFlag(PlayerNotice.PN_IGNORE));
        }

        /**
         * Neither guard is C's — it asserts on the counterpart and dereferences the kind — but both
         * are reachable in the port, and neither should be an exception.
         */
        @Test
        @DisplayName("an object with no counterpart or no kind is not an error")
        void missingPiecesAreSurvivable() throws Exception {
            ItemObject noCounterpart = new ItemObject();
            noCounterpart.setKind(new ObjectKind());
            ItemObject noKind = new ItemObject();
            poke(ItemObject.class, noKind, "known", new ItemObject());

            assertDoesNotThrow(() -> flavourAware(noCounterpart));
            assertDoesNotThrow(() -> flavourAware(noKind));
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
         */
        @Test
        @DisplayName("ignored-while-unaware becomes ignored-while-aware")
        void ignoreDecisionIsCarriedAcross() throws Exception {
            ItemObject potion = unknownPotion();
            potion.getKind().setIgnoredUnaware(true);

            flavourAware(potion);

            assertTrue(potion.getKind().isIgnoredAware());
        }

        /**
         * And the other way: a kind the player never chose to ignore does not acquire the choice by
         * being identified.
         */
        @Test
        @DisplayName("a kind that was not ignored does not become ignored")
        void unignoredKindStaysUnignored() throws Exception {
            ItemObject potion = unknownPotion();

            flavourAware(potion);

            assertFalse(potion.getKind().isIgnoredAware());
        }

        /**
         * The request for the ignore pass goes out either way, because the set of objects the rules
         * apply to has changed whatever the player's setting for this kind. Reading it back as
         * "already set" is how a caller-free test sees that it was raised.
         */
        @Test
        @DisplayName("the ignore pass is requested")
        void ignorePassIsRequested() throws Exception {
            ItemObject potion = unknownPotion();

            flavourAware(potion);

            assertFalse(player.getPlayerUpkeep().orNoticeFlag(PlayerNotice.PN_IGNORE));
        }
    }

    /**
     * {@link ObjectUtils#isCarried} — the pack, quiver and worn items are one list, so one
     * containment test answers for all three.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("isCarried")
    class Carried {

        private void carrying(ItemObject... items) throws Exception {
            poke(Player.class, player, "gear", new ArrayList<>(List.of(items)));
        }

        @Test
        @DisplayName("an object in the gear is carried")
        void gearObjectIsCarried() throws Exception {
            ItemObject sword = new ItemObject();
            carrying(sword);

            assertTrue(ObjectUtils.isCarried(player, sword));
        }

        @Test
        @DisplayName("an object not in the gear is not carried")
        void otherObjectIsNotCarried() throws Exception {
            carrying(new ItemObject());

            assertFalse(ObjectUtils.isCarried(player, new ItemObject()));
        }

        /**
         * A fresh player carries nothing — {@link Player}'s constructor gives an empty list, not a
         * null, so the empty case answers rather than throws.
         */
        @Test
        @DisplayName("an empty pack carries nothing")
        void emptyGearCarriesNothing() throws Exception {
            assertFalse(ObjectUtils.isCarried(player, new ItemObject()));
        }

        /**
         * The distinction the method draws. An object on the floor is not carried however close the
         * player is standing to it — that is {@code Square.holdsObject}'s question, and the two
         * together are what pick between the two messages {@code knowObject} can print.
         */
        @Test
        @DisplayName("an identical object that is not the carried one is not carried")
        void identicalButDistinctObjectIsNotCarried() throws Exception {
            ItemObject inPack = new ItemObject();
            ItemObject itsTwin = new ItemObject();
            carrying(inPack);

            assertTrue(ObjectUtils.isCarried(player, inPack));
            assertFalse(ObjectUtils.isCarried(player, itsTwin));
        }

        /**
         * Every object in the gear, not merely the first.
         */
        @Test
        @DisplayName("every object in the gear is carried")
        void allGearObjectsAreCarried() throws Exception {
            ItemObject first = new ItemObject();
            ItemObject second = new ItemObject();
            ItemObject third = new ItemObject();
            carrying(first, second, third);

            assertTrue(ObjectUtils.isCarried(player, first));
            assertTrue(ObjectUtils.isCarried(player, second));
            assertTrue(ObjectUtils.isCarried(player, third));
        }
    }
}
