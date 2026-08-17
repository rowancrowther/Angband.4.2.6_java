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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link KnownObject}, the port of C's {@code p->obj_k} ({@code src/player.h}) and the store
 * behind {@code player_knows_rune} and {@code player_learn_rune} ({@code src/obj-knowledge.c}).
 *
 * <p>Two things here are worth more than the accessor round-trips. The first is that every
 * {@code learn} answers "was that new" rather than "is it known now": {@code player_learn_rune}
 * prints its discovery message only on a true, so a learner that always answered true would
 * announce the same rune on every hit, and one that always answered false would learn silently.
 * The second is the brand and slay fan-out, where learning one member of an equivalence class has
 * to mark the whole class — the case the shipped data hits on every single brand.
 *
 * <p>The fixtures are built here rather than read from {@code lib/gamedata}, because what is under
 * test is the grouping rule and not the data: a hand-built pair of same-named brands states the
 * case being checked in a way that {@code brand.txt} does not. {@code RuneInitTest} covers the real
 * files from the other direction. The registry is seeded through its public setters and put back in
 * {@link #restore()}, since it holds what C keeps in file-scope globals and is process-wide.
 *
 * @author Rowan Crowther
 */
class KnownObjectTest {

    /**
     * The registry fields this suite overwrites, saved and put back verbatim. The list fields are
     * read and written by reflection rather than through the accessors, because they are null until
     * something loads them and {@link ObjectRegistry#getBrands()} cannot return that — it wraps the
     * field in an unmodifiable view and throws. Restoring an empty list in place of the null would
     * leave the registry in a state it is never otherwise in.
     */
    private static final List<String> SAVED_FIELDS =
            List.of("brands", "slays", "curses", "brandMax", "slayMax", "curseMax");

    private static final Map<String, Object> SAVED = new HashMap<>();

    /**
     * Two strengths of acid, two of fire. This is the shape of the shipped data — {@code brand.txt}
     * holds ten brands that are five names twice over — reduced to the smallest fixture that can
     * tell a fan-out from a single mark.
     */
    private static Brand weakAcid;
    private static Brand strongAcid;
    private static Brand weakFire;
    private static Brand strongFire;

    /**
     * Three slays: two that kill the same monsters at different strengths, and one that does not.
     * Grouped by race flag and base rather than by name, so {@link #evil3} and {@link #evil5} share
     * a group while {@link #undead3} stands alone.
     */
    private static Slay evil3;
    private static Slay evil5;
    private static Slay undead3;

    /**
     * A slay with the same race flag as {@link #evil3} but a non-null base, so that the base half
     * of {@link Slay#sameMonsterSlain} is exercised rather than only the flag half.
     */
    private static Slay evilBased;

    private static Curse siren;
    private static Curse teleportation;

    private KnownObject knowledge;

    /**
     * Seeds the three registries {@link KnownObject} reads, keeping the previous contents.
     *
     * @author Rowan Crowther
     */
    @BeforeAll
    static void seed() throws Exception {
        for (String name : SAVED_FIELDS) {
            SAVED.put(name, field(name).get(null));
        }

        weakAcid = brand("ACID_2", "acid");
        strongAcid = brand("ACID_3", "acid");
        weakFire = brand("FIRE_2", "fire");
        strongFire = brand("FIRE_3", "fire");

        MonsterBase base = new MonsterBase("hydra");
        evil3 = slay("EVIL_3", "evil", null, MonsterRaceFlag.RF_EVIL);
        evil5 = slay("EVIL_5", "evil", null, MonsterRaceFlag.RF_EVIL);
        undead3 = slay("UNDEAD_3", "undead", null, MonsterRaceFlag.RF_UNDEAD);
        evilBased = slay("EVIL_4", "evil", base, MonsterRaceFlag.RF_EVIL);

        siren = curse("siren");
        teleportation = curse("teleportation");

        ObjectRegistry.setBrands(List.of(weakAcid, strongAcid, weakFire, strongFire));
        ObjectRegistry.setSlays(List.of(evil3, evil5, undead3, evilBased));
        ObjectRegistry.setCurses(List.of(siren, teleportation));
    }

    /**
     * @author Rowan Crowther
     */
    @AfterAll
    static void restore() throws Exception {
        for (String name : SAVED_FIELDS) {
            field(name).set(null, SAVED.get(name));
        }
    }

    /**
     * Resolves one of {@link ObjectRegistry}'s private static fields for save and restore.
     *
     * @param name the field's declared name
     * @return the field, already made accessible
     * @throws NoSuchFieldException if the registry no longer declares it
     * @author Rowan Crowther
     */
    private static Field field(String name) throws NoSuchFieldException {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static Brand brand(String code, String name) {
        return new Brand(code, name, "burns", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE,
                17, 3, 15);
    }

    private static Slay slay(String code, String name, MonsterBase base, MonsterRaceFlag raceFlag) {
        return new Slay(code, name, base, "smites", "smites", raceFlag, 17, 3, 15);
    }

    private static Curse curse(String name) {
        return new Curse(name, List.of(), 0, null, List.of(), Map.of(), Map.of(), 0, 0, 0,
                List.of(), List.of(), "does something unpleasant", "The curse fires.");
    }

    /**
     * A fresh instance per test. Knowledge only ever accumulates, so a shared one would let an
     * earlier test satisfy a later one.
     *
     * @author Rowan Crowther
     */
    @BeforeEach
    void newKnowledge() {
        knowledge = new KnownObject();
    }

    /**
     * The starting state, which is C's zeroing allocation and not the state a character begins play
     * in. The birth code raises {@code dd}, {@code ds} and {@code ac} to 1 and switches on the light
     * and digging flags ({@code player_outfit}, {@code src/player-birth.c}); none of that is the
     * constructor's job, and a constructor that helpfully did it would put a fresh character's
     * knowledge out of step with a loaded one.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("a new knowledge set")
    class Initial {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("knows no combat bonuses")
        void noCombatKnowledge() {
            assertFalse(knowledge.toHIsKnown());
            assertFalse(knowledge.toDIsKnown());
            assertFalse(knowledge.toAIsKnown());
        }

        /**
         * Zero rather than one, so that the multiplier reads an unknown value away. The birth code
         * is what makes these 1 for the whole of play.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("has the dice and armour multipliers at zero, not one")
        void multipliersStartAtZero() {
            assertEquals(0, knowledge.getAc());
            assertEquals(0, knowledge.getDd());
            assertEquals(0, knowledge.getDs());
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("knows no properties of any kind")
        void nothingIsKnown() {
            assertTrue(knowledge.getFlags().isEmpty());
            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_STR));
            assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
            assertFalse(knowledge.brandIsKnown(weakAcid));
            assertFalse(knowledge.slayIsKnown(evil3));
            assertFalse(knowledge.curseIsKnown(siren));
        }
    }

    /**
     * The three combat bonuses, C's {@code RUNE_VAR_COMBAT} arm.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("combat bonuses")
    class Combat {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning to-hit reports novelty once and then sticks")
        void toH() {
            assertTrue(knowledge.learnToH());
            assertTrue(knowledge.toHIsKnown());
            assertFalse(knowledge.learnToH());
            assertTrue(knowledge.toHIsKnown());
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning to-damage reports novelty once and then sticks")
        void toD() {
            assertTrue(knowledge.learnToD());
            assertTrue(knowledge.toDIsKnown());
            assertFalse(knowledge.learnToD());
            assertTrue(knowledge.toDIsKnown());
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning to-armour reports novelty once and then sticks")
        void toA() {
            assertTrue(knowledge.learnToA());
            assertTrue(knowledge.toAIsKnown());
            assertFalse(knowledge.learnToA());
            assertTrue(knowledge.toAIsKnown());
        }

        /**
         * The three are separate runes and separate fields. They were briefly written as one
         * copy-pasted block, which is exactly the code that passes a test of any one of them while
         * leaking into the other two.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the three do not affect each other")
        void areIndependent() {
            knowledge.learnToH();

            assertTrue(knowledge.toHIsKnown());
            assertFalse(knowledge.toDIsKnown());
            assertFalse(knowledge.toAIsKnown());
        }
    }

    /**
     * Elemental resistances, C's {@code RUNE_VAR_RESIST} arm reading
     * {@code obj_k->el_info[i].res_level}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("resistances")
    class Resistances {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning one reports novelty once")
        void learnsOnce() {
            assertTrue(knowledge.learnResistance(ElementEnum.ELEM_FIRE));
            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
            assertFalse(knowledge.learnResistance(ElementEnum.ELEM_FIRE));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("elements are learned separately")
        void elementsAreSeparate() {
            knowledge.learnResistance(ElementEnum.ELEM_FIRE);

            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
            assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_COLD));
        }

        /**
         * C indexes an array whose bounds are the real elements, so the two sentinels never arise
         * there. Here they are enum constants like any other and have to be turned away by hand, at
         * both ends — {@code ELEM_NONE} is what a caller gets from an unset field, and reporting it
         * known would make every unresisted element look readable.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the sentinels are never known and cannot be learned")
        void sentinelsAreRefused() {
            assertFalse(knowledge.learnResistance(ElementEnum.ELEM_NONE));
            assertFalse(knowledge.learnResistance(ElementEnum.ELEM_MAX));

            assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_NONE));
            assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_MAX));
        }

        /**
         * Every real element must be reachable. A loop that skipped the sentinels by index arithmetic
         * rather than by identity could quietly drop a neighbour of theirs, and
         * {@code ELEM_ACID} sits directly after {@code ELEM_NONE}.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("every non-sentinel element can be learned")
        void everyRealElementIsReachable() {
            for (ElementEnum element : ElementEnum.values()) {
                if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;

                assertTrue(knowledge.learnResistance(element), element + " should be learnable");
                assertTrue(knowledge.resistanceIsKnown(element), element + " should be known");
            }
        }
    }

    /**
     * Object flags and modifiers, both backed by a {@link Flag} whose {@code on} already answers the
     * novelty question.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("flags and modifiers")
    class FlagsAndModifiers {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a flag is learned once")
        void flagLearnedOnce() {
            assertTrue(knowledge.learnFlag(ObjectFlag.OF_SUST_STR));
            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertFalse(knowledge.learnFlag(ObjectFlag.OF_SUST_STR));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a modifier is learned once")
        void modifierLearnedOnce() {
            assertTrue(knowledge.learnModifier(ObjectModifier.OM_STR));
            assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR));
            assertFalse(knowledge.learnModifier(ObjectModifier.OM_STR));
        }

        /**
         * Flags and modifiers are separate sets over separate enums; this pins that they have not
         * been folded into one.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning a flag does not learn a modifier")
        void flagsAndModifiersAreSeparate() {
            knowledge.learnFlag(ObjectFlag.OF_SUST_STR);

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_STR));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("getFlags reports what has been learned")
        void getFlagsReportsLearned() {
            knowledge.learnFlag(ObjectFlag.OF_SUST_STR);
            knowledge.learnFlag(ObjectFlag.OF_SUST_DEX);

            Flag<ObjectFlag> flags = knowledge.getFlags();

            assertTrue(flags.has(ObjectFlag.OF_SUST_STR));
            assertTrue(flags.has(ObjectFlag.OF_SUST_DEX));
            assertFalse(flags.has(ObjectFlag.OF_SUST_INT));
        }

        /**
         * The copy matters more than it looks. {@code equip_learn_after_time} negates the set it
         * gets back to find the timed flags still unlearned; if that were the live set, inverting it
         * would leave the player marked as knowing everything they did not know a moment ago. C
         * dodges the same hazard by copying first — {@code object_flags(p->obj_k, f); of_negate(f);}
         * negates {@code f}, never the player's own flags.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("getFlags returns a copy, so negating it cannot corrupt the knowledge")
        void getFlagsIsACopy() {
            knowledge.learnFlag(ObjectFlag.OF_SUST_STR);

            Flag<ObjectFlag> first = knowledge.getFlags();
            first.negate();

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_INT));
            assertNotSame(first, knowledge.getFlags());
        }
    }

    /**
     * Brands, and the fan-out that is the whole reason {@link KnownObject#learnBrand} is not a
     * one-line {@code add}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("brands")
    class Brands {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning one reports novelty once")
        void learnsOnce() {
            assertTrue(knowledge.learnBrand(weakAcid));
            assertTrue(knowledge.brandIsKnown(weakAcid));
            assertFalse(knowledge.learnBrand(weakAcid));
        }

        /**
         * The case the whole fan-out exists for. Brands come in strengths sharing one rune, so
         * reading the rune off a weak acid brand must reveal the strong one too — C marks every
         * same-named entry in the same loop.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning one strength learns every strength of that name")
        void learningOneStrengthLearnsTheGroup() {
            knowledge.learnBrand(weakAcid);

            assertTrue(knowledge.brandIsKnown(weakAcid));
            assertTrue(knowledge.brandIsKnown(strongAcid));
        }

        /**
         * The fan-out runs the other way too — the representative a rune happens to hold may be
         * either member, and neither is privileged.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the group is learned whichever member is passed in")
        void directionDoesNotMatter() {
            knowledge.learnBrand(strongAcid);

            assertTrue(knowledge.brandIsKnown(weakAcid));
            assertTrue(knowledge.brandIsKnown(strongAcid));
        }

        /**
         * The other half of the claim: a fan-out that marked everything would pass every test above.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the fan-out stops at the name boundary")
        void otherNamesAreUntouched() {
            knowledge.learnBrand(weakAcid);

            assertFalse(knowledge.brandIsKnown(weakFire));
            assertFalse(knowledge.brandIsKnown(strongFire));
        }

        /**
         * Learning the second member of an already-known group is not news, and must not print a
         * discovery message. This is what the early return in {@code learnBrand} guards, and it
         * would still hold without it — every {@code add} would answer false — which is why it is
         * worth stating rather than assuming.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a second member of a known group is not new knowledge")
        void secondMemberIsNotNews() {
            knowledge.learnBrand(weakAcid);

            assertFalse(knowledge.learnBrand(strongAcid));
        }

        /**
         * A brand built outside the registry stands for its group: the group is marked, and the
         * caller's own instance is recognised through {@link Brand#equals} matching a registry twin.
         * Savefile loading and the parsers both produce brands this way.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a brand from outside the registry still learns its group")
        void unregisteredBrandLearnsItsGroup() {
            Brand loose = brand("ACID_2", "acid");

            assertTrue(knowledge.learnBrand(loose));

            assertTrue(knowledge.brandIsKnown(weakAcid));
            assertTrue(knowledge.brandIsKnown(strongAcid));
        }

        /**
         * A name with no registry entry marks nothing and reports nothing learned, rather than
         * quietly adding an orphan the rest of the game will never ask about.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a brand with no group learns nothing")
        void unknownNameLearnsNothing() {
            Brand nowhere = brand("NETHER_2", "nether");

            assertFalse(knowledge.learnBrand(nowhere));
            assertFalse(knowledge.brandIsKnown(nowhere));
        }
    }

    /**
     * Slays, whose grouping is by monsters slain rather than by name — the one place the two
     * fan-outs differ.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("slays")
    class Slays {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning one reports novelty once")
        void learnsOnce() {
            assertTrue(knowledge.learnSlay(evil3));
            assertTrue(knowledge.slayIsKnown(evil3));
            assertFalse(knowledge.learnSlay(evil3));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning one strength learns every slay killing the same monsters")
        void learningOneStrengthLearnsTheGroup() {
            knowledge.learnSlay(evil3);

            assertTrue(knowledge.slayIsKnown(evil3));
            assertTrue(knowledge.slayIsKnown(evil5));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a different race flag is a different group")
        void differentRaceFlagIsSeparate() {
            knowledge.learnSlay(evil3);

            assertFalse(knowledge.slayIsKnown(undead3));
        }

        /**
         * The half a name match would get wrong in the opposite direction: {@link #evilBased} is
         * named "evil" like the other two, but its non-null base means it kills a different set of
         * monsters and belongs to its own group. A fan-out keyed on the name — the brand rule —
         * would wrongly sweep it in.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the same name with a different base is a different group")
        void baseSeparatesGroupsThatShareAName() {
            knowledge.learnSlay(evil3);

            assertFalse(knowledge.slayIsKnown(evilBased));
        }

        /**
         * And the converse, so that the base is shown to join as well as to separate.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning the based slay leaves the baseless group alone")
        void basedSlayHasItsOwnGroup() {
            assertTrue(knowledge.learnSlay(evilBased));

            assertTrue(knowledge.slayIsKnown(evilBased));
            assertFalse(knowledge.slayIsKnown(evil3));
            assertFalse(knowledge.slayIsKnown(evil5));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a second member of a known group is not new knowledge")
        void secondMemberIsNotNews() {
            knowledge.learnSlay(evil3);

            assertFalse(knowledge.learnSlay(evil5));
        }
    }

    /**
     * Curses, the one property with no equivalence class — each has its own rune, so learning marks
     * exactly what it is given.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("curses")
    class Curses {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("learning one reports novelty once")
        void learnsOnce() {
            assertTrue(knowledge.learnCurse(siren));
            assertTrue(knowledge.curseIsKnown(siren));
            assertFalse(knowledge.learnCurse(siren));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("curses do not group")
        void cursesAreIndividual() {
            knowledge.learnCurse(siren);

            assertFalse(knowledge.curseIsKnown(teleportation));
        }

        /**
         * C's {@code player_knows_curse} indexes an array long enough to hold every curse, so the
         * question cannot arise there. Here a curse built outside the registry is absent from the
         * map, and the honest answer is that a curse the knowledge has never heard of is not one the
         * player recognises.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("an unregistered curse is not known")
        void unregisteredCurseIsNotKnown() {
            assertFalse(knowledge.curseIsKnown(curse("nowhere")));
        }
    }
}
