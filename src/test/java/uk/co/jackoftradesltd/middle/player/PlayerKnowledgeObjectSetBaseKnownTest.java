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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.Flavour;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerKnowledge#objectSetBaseKnown(Player, ItemObject)}, the port of C's
 * {@code object_set_base_known} ({@code obj-knowledge.c}) — what a player is entitled to know about
 * an object from recognizing what kind of thing it is, before any of its own enchantment is learned.
 *
 * <p>The expected values are read off C's function, clause by clause:
 *
 * <ul>
 *   <li>the kind, tval, sval, weight and number are always copied;</li>
 *   <li>{@code dd}, {@code ds} and {@code ac} are taken from the <em>kind</em>, multiplied by the
 *       player's 0/1 knowledge of each, and only where the counterpart still holds zero;</li>
 *   <li>{@code to_h} is written outright from the kind's base when the object's to-hit is standard
 *       for it (no multiplier and no emptiness test), and left alone otherwise;</li>
 *   <li>a launcher's {@code pval} is copied;</li>
 *   <li>{@code pval} and the effect are copied for an aware flavoured kind or an unflavoured
 *       non-wearable;</li>
 *   <li>a wearable's effect is copied when its kind has an effect and the player is aware of it.</li>
 * </ul>
 *
 * <p>The knowledge object's registries are seeded empty for the duration of each test, since it sizes
 * its maps from them, and put back afterwards.
 *
 * <p>Class PlayerKnowledgeObjectSetBaseKnownTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerKnowledgeObjectSetBaseKnownTest {

    private Object savedBrands;
    private Object savedSlays;
    private Object savedCurses;
    private Player player;
    private ObjectKind kind;
    private ItemObject item;
    private ItemObject known;

    private static Effect effect() {
        return new Effect(EffectEnum.EF_NONE, null, null, 0, 0, null, null, 0, 0, List.of(), null);
    }

    @BeforeEach
    void setUp() {
        savedBrands = ItemFixture.setStatic(ObjectRegistry.class, "brands", new ArrayList<>());
        savedSlays = ItemFixture.setStatic(ObjectRegistry.class, "slays", new ArrayList<>());
        savedCurses = ItemFixture.setStatic(ObjectRegistry.class, "curses", new ArrayList<>());

        player = new Player();
        player.setItemKnowledge(new KnownObject());
        build(TValue.TV_SWORD);
    }

    @AfterEach
    void tearDown() {
        ItemFixture.setStatic(ObjectRegistry.class, "brands", savedBrands);
        ItemFixture.setStatic(ObjectRegistry.class, "slays", savedSlays);
        ItemFixture.setStatic(ObjectRegistry.class, "curses", savedCurses);
    }

    /**
     * An item of the given type, with a blank counterpart, sharing one kind whose dice, armour class
     * and to-hit base are fixed figures that cannot be mistaken for each other.
     */
    private void build(TValue tValue) {
        kind = ItemFixture.kindWithDice(tValue);
        ItemFixture.set(kind, "damageDice", 2);
        ItemFixture.set(kind, "damageSides", 5);
        ItemFixture.set(kind, "ac", 7);
        item = new ItemObject();
        item.setKind(kind);
        item.settValue(tValue);
        known = new ItemObject();
        item.setKnown(known);
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("what is always copied")
    class AlwaysCopied {

        @Test
        @DisplayName("kind, tval, sval, weight and number come across")
        void identity() {
            item.setsValue(4);
            item.setWeight(120);
            item.setNumber(3);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertSame(kind, known.getKind());
            assertEquals(TValue.TV_SWORD, known.gettValue());
            assertEquals(4, known.getsValue());
            assertEquals(120, known.getWeight());
            assertEquals(3, known.getNumber());
        }

        @Test
        @DisplayName("a missing item or counterpart is a broken invariant, as C's assert is")
        void missing() {
            assertThrows(RuntimeException.class, () -> PlayerKnowledge.objectSetBaseKnown(player, null));

            item.setKnown(null);
            assertThrows(RuntimeException.class, () -> PlayerKnowledge.objectSetBaseKnown(player, item));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("generic dice and armour class")
    class DiceAndArmourClass {

        @Test
        @DisplayName("the kind's figures are multiplied by what the player can read")
        void multipliedByKnowledge() {
            player.getItemKnowledge().setDD(1);
            player.getItemKnowledge().setDS(0);
            player.getItemKnowledge().setAC(1);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(2, known.getDamageDice());
            assertEquals(0, known.getDamageSides());
            assertEquals(7, known.getBaseAC());
        }

        @Test
        @DisplayName("nothing is copied for a player who can read none of them")
        void nothingKnown() {
            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(0, known.getDamageDice());
            assertEquals(0, known.getDamageSides());
            assertEquals(0, known.getBaseAC());
        }

        @Test
        @DisplayName("a figure already on the counterpart is never overwritten by the kind's")
        void alreadyHeldIsKept() {
            player.getItemKnowledge().setDD(1);
            player.getItemKnowledge().setDS(1);
            player.getItemKnowledge().setAC(1);
            known.setDamageDice(9);
            known.setDamageSides(8);
            known.setBaseAC(6);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(9, known.getDamageDice());
            assertEquals(8, known.getDamageSides());
            assertEquals(6, known.getBaseAC());
        }

        @Test
        @DisplayName("the kind's figures are used, not the object's own")
        void kindNotObject() {
            player.getItemKnowledge().setDD(1);
            item.setDamageDice(4);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(2, known.getDamageDice());
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("to-hit")
    class ToHit {

        @Test
        @DisplayName("a standard to-hit is written outright, over whatever the counterpart held")
        void standardOverwrites() {
            // a sword is not body armour, so standard means a to-hit of zero; the kind's base is 0
            item.setToHit(0);
            known.setToHit(9);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(0, known.getToHit());
        }

        @Test
        @DisplayName("a non-standard to-hit is left exactly as it was")
        void nonStandardUntouched() {
            item.setToHit(3);
            known.setToHit(9);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(9, known.getToHit());
        }

        @Test
        @DisplayName("body armour's standard to-hit is its kind's fixed penalty, not zero")
        void bodyArmourStandard() {
            build(TValue.TV_SOFT_ARMOR);
            ItemFixture.set(kind, "toH", new Random(-2, 0, 0, 0, false));
            item.setToHit(-2);
            known.setToHit(9);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(-2, known.getToHit());
        }

        @Test
        @DisplayName("body armour carrying a different to-hit is not standard, and the counterpart is left alone")
        void bodyArmourNonStandard() {
            build(TValue.TV_SOFT_ARMOR);
            ItemFixture.set(kind, "toH", new Random(-2, 0, 0, 0, false));
            item.setToHit(-5);
            known.setToHit(9);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(9, known.getToHit());
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("pval and effect")
    class PvalAndEffect {

        @Test
        @DisplayName("a launcher's pval is copied")
        void launcherPval() {
            build(TValue.TV_BOW);
            item.setpValue(3);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(3, known.getpValue());
        }

        @Test
        @DisplayName("a wearable that is not a launcher keeps its pval hidden")
        void wearablePvalHidden() {
            item.setpValue(3);

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(0, known.getpValue());
        }

        @Test
        @DisplayName("an unflavoured non-wearable gives up its pval and effect at once")
        void unflavouredNonWearable() {
            build(TValue.TV_POTION);
            item.setpValue(5);
            item.setEffect(List.of(effect()));

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(5, known.getpValue());
            assertEquals(1, known.getEffect().size());
        }

        @Test
        @DisplayName("an aware flavoured kind gives up its pval and effect")
        void awareFlavour() {
            build(TValue.TV_POTION);
            ItemFixture.set(kind, "flavour", new Flavour("pink", "pink", ColourEnum.COLOUR_RED, 1));
            kind.setAware(true);
            item.setpValue(5);
            item.setEffect(List.of(effect()));

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(5, known.getpValue());
            assertEquals(1, known.getEffect().size());
        }

        @Test
        @DisplayName("a flavoured kind the player is unaware of keeps both hidden")
        void unawareFlavour() {
            build(TValue.TV_POTION);
            ItemFixture.set(kind, "flavour", new Flavour("pink", "pink", ColourEnum.COLOUR_RED, 1));
            item.setpValue(5);
            item.setEffect(List.of(effect()));

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(0, known.getpValue());
            assertTrue(known.getEffect().isEmpty());
        }

        @Test
        @DisplayName("a wearable's standard activation is known once the kind is aware")
        void wearableActivation() {
            ItemFixture.set(kind, "effect", new ArrayList<>(List.of(effect())));
            kind.setAware(true);
            item.setEffect(List.of(effect()));

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertEquals(1, known.getEffect().size());
        }

        @Test
        @DisplayName("a wearable's activation stays hidden while the kind is unaware")
        void wearableActivationUnaware() {
            ItemFixture.set(kind, "effect", new ArrayList<>(List.of(effect())));
            item.setEffect(List.of(effect()));

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertTrue(known.getEffect().isEmpty());
        }

        @Test
        @DisplayName("a wearable whose kind has no effect has nothing to know, aware or not")
        void wearableKindWithoutEffect() {
            ItemFixture.set(kind, "effect", new ArrayList<>());
            kind.setAware(true);
            item.setEffect(List.of(effect()));

            PlayerKnowledge.objectSetBaseKnown(player, item);

            assertTrue(known.getEffect().isEmpty());
        }
    }
}
