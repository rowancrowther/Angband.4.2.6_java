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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.Brand;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.Slay;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#playerKnowsBrand} and {@link Player#playerKnowsSlay}, the ports of C's
 * {@code player_knows_brand} and {@code player_knows_slay} ({@code obj-knowledge.c}).
 *
 * <p>Both C functions are a bare array read, {@code p->obj_k->brands[i]} and
 * {@code p->obj_k->slays[i]}, so what is worth pinning is not the lookup but its two edges. The
 * first is that the answer follows the player's own knowledge object and nothing else: a brand
 * another player has learned, or the registry holds, is not known. The second is that there is no
 * grouping on the read side. The grouping C does is in {@code player_learn_rune}, which marks every
 * brand of the same name (and every slay that kills the same monsters) in one go; the read sees the
 * result, so asking about either strength of a learned brand answers {@code true} and asking about
 * a different brand answers {@code false}.
 *
 * <p>The fixtures are hand-built, as in {@code KnownObjectTest}: two strengths of acid, one of
 * fire, and three slays of which two kill the same monsters. The registry is process-wide, so it is
 * seeded through its setters and put back afterwards.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerKnowsBrandSlayTest {

    /**
     * The registry fields this suite overwrites, saved and put back verbatim; see
     * {@code KnownObjectTest} for why they are handled by reflection.
     */
    private static final List<String> SAVED_FIELDS = List.of("brands", "slays");

    /**
     * The values those fields held before the suite ran.
     */
    private static final Map<String, Object> SAVED = new HashMap<>();

    private static Brand weakAcid;
    private static Brand strongAcid;
    private static Brand weakFire;
    private static Slay evil3;
    private static Slay evil5;
    private static Slay undead3;

    /**
     * The player under test, given its own knowledge object before each test.
     */
    private Player player;

    /**
     * Seeds the object registry's brand and slay lists.
     *
     * @throws Exception if the registry cannot be reached
     */
    @BeforeAll
    static void seed() throws Exception {
        for (String name : SAVED_FIELDS) {
            SAVED.put(name, field(name).get(null));
        }

        weakAcid = new Brand("ACID_2", "acid", "burns", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE,
                17, 3, 15);
        strongAcid = new Brand("ACID_3", "acid", "burns", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE,
                17, 3, 15);
        weakFire = new Brand("FIRE_2", "fire", "burns", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE,
                17, 3, 15);
        evil3 = new Slay("EVIL_3", "evil", null, "smites", "smites", MonsterRaceFlag.RF_EVIL, 17, 3, 15);
        evil5 = new Slay("EVIL_5", "evil", null, "smites", "smites", MonsterRaceFlag.RF_EVIL, 17, 3, 15);
        undead3 = new Slay("UNDEAD_3", "undead", null, "smites", "smites", MonsterRaceFlag.RF_UNDEAD, 17, 3, 15);

        ObjectRegistry.setBrands(List.of(weakAcid, strongAcid, weakFire));
        ObjectRegistry.setSlays(List.of(evil3, evil5, undead3));
    }

    /**
     * Puts the registry's lists back as they were.
     *
     * @throws Exception if the registry cannot be reached
     */
    @AfterAll
    static void restore() throws Exception {
        for (String name : SAVED_FIELDS) {
            field(name).set(null, SAVED.get(name));
        }
    }

    /**
     * Resolves one of {@link ObjectRegistry}'s private static fields.
     *
     * @param name the field's declared name
     * @return the field, already made accessible
     * @throws NoSuchFieldException if the registry no longer declares it
     */
    private static Field field(String name) throws NoSuchFieldException {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    /**
     * A fresh player with an empty knowledge object, since knowledge only accumulates.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
        player.setItemKnowledge(new KnownObject());
    }

    /**
     * The state a character starts play in: C's zeroed {@code obj_k} knows nothing.
     */
    @Nested
    @DisplayName("a player who has learned nothing")
    class Nothing {

        @Test
        @DisplayName("knows no brand")
        void knowsNoBrand() {
            assertFalse(player.playerKnowsBrand(weakAcid));
            assertFalse(player.playerKnowsBrand(strongAcid));
            assertFalse(player.playerKnowsBrand(weakFire));
        }

        @Test
        @DisplayName("knows no slay")
        void knowsNoSlay() {
            assertFalse(player.playerKnowsSlay(evil3));
            assertFalse(player.playerKnowsSlay(evil5));
            assertFalse(player.playerKnowsSlay(undead3));
        }
    }

    /**
     * The read follows the knowledge object, and the grouping the player sees was done when the
     * rune was learned.
     */
    @Nested
    @DisplayName("a player who has learned a rune")
    class Learned {

        /**
         * Learning one strength of acid marks both, as {@code player_learn_rune} does, and the read
         * simply reports it; fire, a different name, is untouched.
         */
        @Test
        @DisplayName("knows every strength of a learned brand and nothing else")
        void knowsTheWholeBrandGroup() {
            player.getItemKnowledge().learnBrand(weakAcid);

            assertTrue(player.playerKnowsBrand(weakAcid));
            assertTrue(player.playerKnowsBrand(strongAcid));
            assertFalse(player.playerKnowsBrand(weakFire));
        }

        /**
         * Two slays that kill the same monsters share one rune; undead is a different group.
         */
        @Test
        @DisplayName("knows every slay that kills the same monsters and nothing else")
        void knowsTheWholeSlayGroup() {
            player.getItemKnowledge().learnSlay(evil5);

            assertTrue(player.playerKnowsSlay(evil5));
            assertTrue(player.playerKnowsSlay(evil3));
            assertFalse(player.playerKnowsSlay(undead3));
        }

        /**
         * Brands and slays are separate tables in {@code obj_k}; learning one kind does not make
         * the other known, even where a name could coincide.
         */
        @Test
        @DisplayName("brand knowledge and slay knowledge are independent")
        void brandsAndSlaysAreIndependent() {
            player.getItemKnowledge().learnBrand(weakFire);

            assertTrue(player.playerKnowsBrand(weakFire));
            assertFalse(player.playerKnowsSlay(evil3));
            assertFalse(player.playerKnowsSlay(undead3));
        }

        /**
         * The answer belongs to this player's knowledge object: a second player who has learned
         * nothing is not told what the first one knows.
         */
        @Test
        @DisplayName("is per player, not shared through the registry")
        void isPerPlayer() {
            player.getItemKnowledge().learnBrand(weakAcid);
            player.getItemKnowledge().learnSlay(evil3);

            Player other = new Player();
            other.setItemKnowledge(new KnownObject());

            assertFalse(other.playerKnowsBrand(weakAcid));
            assertFalse(other.playerKnowsSlay(evil3));
        }

        /**
         * Replacing the knowledge object replaces the answer, since the read goes through the
         * player's current object each time rather than caching.
         */
        @Test
        @DisplayName("follows a replaced knowledge object")
        void followsReplacement() {
            player.getItemKnowledge().learnBrand(weakAcid);
            assertTrue(player.playerKnowsBrand(weakAcid));

            player.setItemKnowledge(new KnownObject());

            assertFalse(player.playerKnowsBrand(weakAcid));
        }
    }

    /**
     * The edge C does not have: the knowledge object is allocated by {@code init_player} before
     * anything can ask, but the port leaves it null until the registries are loaded, and the
     * methods document that they throw rather than answer {@code false}.
     */
    @Nested
    @DisplayName("a player whose knowledge object has not been built")
    class NoKnowledgeObject {

        @Test
        @DisplayName("playerKnowsBrand throws NullPointerException")
        void brandThrows() {
            Player fresh = new Player();

            assertThrows(NullPointerException.class, () -> fresh.playerKnowsBrand(weakAcid));
        }

        @Test
        @DisplayName("playerKnowsSlay throws NullPointerException")
        void slayThrows() {
            Player fresh = new Player();

            assertThrows(NullPointerException.class, () -> fresh.playerKnowsSlay(evil3));
        }
    }
}
