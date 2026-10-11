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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.LevelMaxData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.testsupport.CurseFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerKnowledge#knowsEgo(Player, EgoItem, ItemObject)}, the port of C's
 * {@code player_knows_ego} ({@code obj-knowledge.c}) — whether the player could now put a name to an
 * ego from the properties they can already read.
 *
 * <p>Every expected value is read off C's function. The ego is known when every flag, modifier,
 * resistance, brand, slay and curse it always grants is a rune the player can read, and the whole
 * of the interest is in the modifier test, which is the only clause with a concession in it. C
 * evaluates each modifier's {@code random_value} at both extremes and asks whether the ego could
 * ever show that modifier ({@code modmax > 0 || modmin < 0}). If it could, and the player cannot
 * read the modifier, the ego is not known — <em>unless</em> a specific object is being tested, the
 * range reaches zero ({@code modmax * modmin <= 0}), and that object rolled zero for the modifier,
 * in which case the object has nothing to have failed to notice.
 *
 * <p>The ranges below are chosen to sit on each side of that test, with the extremes worked out
 * by hand from C's {@code randcalc}: {@code MINIMISE} gives {@code base + dice + 0} and
 * {@code MAXIMISE} gives {@code base + dice * sides + m_bonus}, the level being irrelevant to both.
 *
 * <ul>
 *   <li>{@code 3} gives min 3, max 3; {@code -2} gives -2, -2 — a fixed value never reaches zero;</li>
 *   <li>{@code -3 + 1d5} gives -2, 2 — straddles zero;</li>
 *   <li>{@code -1 + 1d3} gives 0, 2 and {@code -2 + 1d2} gives -1, 0 — the product is exactly zero
 *       on either side, which is the boundary of {@code modmax * modmin > 0};</li>
 *   <li>{@code m_bonus 5} gives 0, 5 — the level-scaled term counts in full at the top and not at
 *       all at the bottom.</li>
 * </ul>
 *
 * <p>The registries and the world's maximum depth are seeded for the duration of each test and put
 * back afterwards, since both are global static state.
 *
 * <p>Class PlayerKnowledgeKnowsEgoTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerKnowledgeKnowsEgoTest {

    private Brand acidWeak;
    private Brand acidStrong;
    private Brand fire;
    private Slay evil2;
    private Slay evil3;
    private Slay animal;
    private Curse siren;
    private Curse cowardice;
    private Object savedBrands;
    private Object savedSlays;
    private Object savedCurses;
    private Object savedConstants;
    private Player player;

    private static Flag<ObjectFlag> flags(ObjectFlag... flags) {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);
        if (flags.length > 0) result.set(List.of(flags));
        return result;
    }

    /**
     * An ego carrying only what a test names, defaulting the rest. The constructor is the one the
     * data-file assembler uses, so it takes twenty-seven arguments.
     */
    private static EgoItem ego(Flag<ObjectFlag> flags,
                               Map<ObjectModifier, Random> modifiers,
                               Map<ElementEnum, ElementInfo> elInfo,
                               Set<Brand> brands,
                               Set<Slay> slays,
                               Map<Curse, CurseData> curses) {
        return new EgoItem("of Testing", "a test ego", 1, 0,
                flags, new Flag<>(ObjectFlag.class), new Flag<>(ObjectKindFlag.class),
                modifiers, new HashMap<>(), elInfo, brands, slays, curses,
                0, 0, 0, 0, new ArrayList<ObjectKind>(),
                null, null, null, 0, 0, 0,
                null, null, false);
    }

    private static EgoItem bareEgo() {
        return ego(flags(), new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>(), new HashMap<>());
    }

    private static EgoItem egoWithModifier(Random range) {
        Map<ObjectModifier, Random> modifiers = new HashMap<>();
        modifiers.put(ObjectModifier.OM_STR, range);
        return ego(flags(), modifiers, new HashMap<>(), new HashSet<>(), new HashSet<>(), new HashMap<>());
    }

    private static EgoItem egoWithElement(int resLevel) {
        ElementInfo info = new ElementInfo();
        info.setResLevel(resLevel);
        Map<ElementEnum, ElementInfo> elements = new HashMap<>();
        elements.put(ElementEnum.ELEM_FIRE, info);
        return ego(flags(), new HashMap<>(), elements, new HashSet<>(), new HashSet<>(), new HashMap<>());
    }

    /**
     * An item whose Strength modifier is the given value, or one with no modifier entry at all for
     * {@code null}. A missing entry is the port's way of holding what C holds as a zero.
     */
    private static ItemObject itemWithStrength(Integer value) {
        ItemObject item = new ItemObject();
        Map<ObjectModifier, Integer> modifiers = new LinkedHashMap<>();
        if (value != null) modifiers.put(ObjectModifier.OM_STR, value);
        item.setModifiers(modifiers);
        return item;
    }

    @BeforeEach
    void setUp() {
        acidWeak = new Brand("ACID_2", "acid", "burns", null, null, 2, 2, 1);
        acidStrong = new Brand("ACID_3", "acid", "burns", null, null, 3, 3, 1);
        fire = new Brand("FIRE_2", "fire", "burns", null, null, 2, 2, 1);
        evil2 = new Slay("EVIL_2", "evil", null, "smite", "smite", MonsterRaceFlag.RF_EVIL, 2, 2, 1);
        evil3 = new Slay("EVIL_3", "evil", null, "smite", "smite", MonsterRaceFlag.RF_EVIL, 3, 3, 1);
        animal = new Slay("ANIMAL_2", "animal", null, "smite", "smite", MonsterRaceFlag.RF_ANIMAL, 2, 2, 1);
        siren = CurseFixture.curse("siren", List.of(), 0, null, flags(), Map.of(), Map.of(), 0, 0, 0,
                List.of(), flags(), "wakes monsters", "The curse fires.", 0);
        cowardice = CurseFixture.curse("cowardice", List.of(), 0, null, flags(), Map.of(), Map.of(), 0, 0, 0,
                List.of(), flags(), "unnerves the wearer", "The curse fires.", 1);

        savedBrands = ItemFixture.setStatic(ObjectRegistry.class, "brands",
                new ArrayList<>(List.of(acidWeak, acidStrong, fire)));
        savedSlays = ItemFixture.setStatic(ObjectRegistry.class, "slays",
                new ArrayList<>(List.of(evil2, evil3, animal)));
        savedCurses = ItemFixture.setStatic(ObjectRegistry.class, "curses",
                new ArrayList<>(List.of(siren, cowardice)));

        // knowsEgo evaluates ranges at world:max-depth; SeededPlayerRegistry leaves that section null
        savedConstants = ItemFixture.setStatic(GameConstants.class, "data", new GameConstantsData(
                new LevelMaxData(1024), null, null, null,
                new WorldData(128, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, null, null, null, null, null, null, null, null, null));

        player = new Player();
        player.setItemKnowledge(new KnownObject());
    }

    @AfterEach
    void tearDown() {
        ItemFixture.setStatic(ObjectRegistry.class, "brands", savedBrands);
        ItemFixture.setStatic(ObjectRegistry.class, "slays", savedSlays);
        ItemFixture.setStatic(ObjectRegistry.class, "curses", savedCurses);
        ItemFixture.setStatic(GameConstants.class, "data", savedConstants);
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("an ego that grants nothing")
    class NothingGranted {

        @Test
        @DisplayName("no ego at all is never known, whatever the player has learned")
        void noEgo() {
            assertFalse(PlayerKnowledge.knowsEgo(player, null, null));
            assertFalse(PlayerKnowledge.knowsEgo(player, null, new ItemObject()));
        }

        @Test
        @DisplayName("an ego with nothing to learn is known to a player who has learned nothing")
        void bareEgoIsKnown() {
            assertTrue(PlayerKnowledge.knowsEgo(player, bareEgo(), null));
            assertTrue(PlayerKnowledge.knowsEgo(player, bareEgo(), new ItemObject()));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("flags")
    class Flags {

        @Test
        @DisplayName("an unread flag keeps the ego unknown until it is learned")
        void flagMustBeKnown() {
            EgoItem ego = ego(flags(ObjectFlag.OF_FEATHER), new HashMap<>(), new HashMap<>(),
                    new HashSet<>(), new HashSet<>(), new HashMap<>());

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));

            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FEATHER);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("every flag must be read, not just one of them")
        void everyFlagMustBeKnown() {
            EgoItem ego = ego(flags(ObjectFlag.OF_FEATHER, ObjectFlag.OF_SEE_INVIS), new HashMap<>(),
                    new HashMap<>(), new HashSet<>(), new HashSet<>(), new HashMap<>());

            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FEATHER);
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));

            player.getItemKnowledge().learnFlag(ObjectFlag.OF_SEE_INVIS);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("knowing a flag the ego does not grant changes nothing")
        void unrelatedFlagIsIrrelevant() {
            EgoItem ego = ego(flags(ObjectFlag.OF_FEATHER), new HashMap<>(), new HashMap<>(),
                    new HashSet<>(), new HashSet<>(), new HashMap<>());

            player.getItemKnowledge().learnFlag(ObjectFlag.OF_SEE_INVIS);
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("modifiers")
    class Modifiers {

        @Test
        @DisplayName("a fixed positive modifier must be read, and no object can excuse it")
        void fixedPositive() {
            EgoItem ego = egoWithModifier(new Random(3, 0, 0, 0, false));

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
            // min * max = 9, so the concession is not available even to an object that rolled zero
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(0)));
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(3)));

            player.getItemKnowledge().learnModifier(ObjectModifier.OM_STR);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("a fixed negative modifier is also something to be read")
        void fixedNegative() {
            EgoItem ego = egoWithModifier(new Random(-2, 0, 0, 0, false));

            // modmax < 0 and modmin < 0: the second half of the test fires, and -2 * -2 = 4 > 0
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(0)));

            player.getItemKnowledge().learnModifier(ObjectModifier.OM_STR);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("a modifier that is always zero needs no rune")
        void alwaysZero() {
            EgoItem ego = egoWithModifier(Random.Zero());

            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("an ego with no entry for a modifier needs no rune for it")
        void absentModifier() {
            // C reads a zeroed random_value for an unmentioned modifier; the port's getModifier answers null
            assertTrue(PlayerKnowledge.knowsEgo(player, bareEgo(), itemWithStrength(5)));
        }

        @Test
        @DisplayName("a range straddling zero is unknown in general but can be excused for a zero roll")
        void straddlesZero() {
            // -3 + 1d5: min -2, max 2, product -4
            EgoItem ego = egoWithModifier(new Random(-3, 0, 1, 5, false));

            // no specific object: C skips the concession, so the ego is not known in general
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
            // this object rolled zero, so there was nothing to notice
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(0)));
            // an object with no entry reads as zero, as C's dense array does
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(null)));
            // rolled non-zero, either sign, and the player cannot read it
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(1)));
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(-1)));
        }

        @Test
        @DisplayName("once the modifier is read the straddling range no longer matters")
        void straddlesZeroKnown() {
            EgoItem ego = egoWithModifier(new Random(-3, 0, 1, 5, false));
            player.getItemKnowledge().learnModifier(ObjectModifier.OM_STR);

            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(2)));
        }

        @Test
        @DisplayName("a range whose minimum is exactly zero sits on the boundary and is excusable")
        void minimumIsZero() {
            // -1 + 1d3: min 0, max 2, product 0 — not > 0
            EgoItem ego = egoWithModifier(new Random(-1, 0, 1, 3, false));

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(0)));
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(2)));
        }

        @Test
        @DisplayName("a range whose maximum is exactly zero sits on the boundary and is excusable")
        void maximumIsZero() {
            // -2 + 1d2: min -1, max 0, product 0 — modmin < 0 is what makes it count at all
            EgoItem ego = egoWithModifier(new Random(-2, 0, 1, 2, false));

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(0)));
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(-1)));
        }

        @Test
        @DisplayName("a level-scaled bonus counts in full at the maximum and not at all at the minimum")
        void levelBonusRange() {
            // m_bonus 5, nothing else: max 5, min 0 — so it can show, and can be excused
            EgoItem ego = egoWithModifier(new Random(0, 5, 0, 0, false));

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(0)));
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, itemWithStrength(3)));
        }

        @Test
        @DisplayName("learning a different modifier does not help")
        void wrongModifierLearned() {
            EgoItem ego = egoWithModifier(new Random(3, 0, 0, 0, false));

            player.getItemKnowledge().learnModifier(ObjectModifier.OM_DEX);
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("elements")
    class Elements {

        @Test
        @DisplayName("a resistance the ego grants must be read")
        void resistanceMustBeKnown() {
            EgoItem ego = egoWithElement(1);

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));

            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("a vulnerability counts as well, since C tests for any non-zero level")
        void vulnerabilityMustBeKnown() {
            EgoItem ego = egoWithElement(-1);

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));

            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("an element named at level zero needs no rune")
        void levelZeroIsIgnored() {
            assertTrue(PlayerKnowledge.knowsEgo(player, egoWithElement(0), null));
        }

        @Test
        @DisplayName("learning a different element does not help")
        void wrongElementLearned() {
            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_COLD);

            assertFalse(PlayerKnowledge.knowsEgo(player, egoWithElement(1), null));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("brands and slays")
    class BrandsAndSlays {

        @Test
        @DisplayName("a brand the ego grants must be read")
        void brandMustBeKnown() {
            EgoItem ego = ego(flags(), new HashMap<>(), new HashMap<>(), new HashSet<>(Set.of(fire)),
                    new HashSet<>(), new HashMap<>());

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));

            player.getItemKnowledge().learnBrand(fire);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("a brand is known through its twin of another strength, because learning marks the group")
        void brandKnownThroughTwin() {
            EgoItem ego = ego(flags(), new HashMap<>(), new HashMap<>(), new HashSet<>(Set.of(acidStrong)),
                    new HashSet<>(), new HashMap<>());

            player.getItemKnowledge().learnBrand(acidWeak);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("a different brand is not the same brand")
        void otherBrand() {
            EgoItem ego = ego(flags(), new HashMap<>(), new HashMap<>(), new HashSet<>(Set.of(fire)),
                    new HashSet<>(), new HashMap<>());

            player.getItemKnowledge().learnBrand(acidWeak);
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("every brand must be read, not just one of them")
        void everyBrand() {
            EgoItem ego = ego(flags(), new HashMap<>(), new HashMap<>(), new HashSet<>(Set.of(fire, acidWeak)),
                    new HashSet<>(), new HashMap<>());

            player.getItemKnowledge().learnBrand(fire);
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));

            player.getItemKnowledge().learnBrand(acidWeak);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("a slay the ego grants must be read")
        void slayMustBeKnown() {
            EgoItem ego = ego(flags(), new HashMap<>(), new HashMap<>(), new HashSet<>(),
                    new HashSet<>(Set.of(animal)), new HashMap<>());

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));

            player.getItemKnowledge().learnSlay(animal);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("a slay is known through a slay that kills the same monsters")
        void slayKnownThroughTwin() {
            EgoItem ego = ego(flags(), new HashMap<>(), new HashMap<>(), new HashSet<>(),
                    new HashSet<>(Set.of(evil3)), new HashMap<>());

            player.getItemKnowledge().learnSlay(evil2);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("a slay of other monsters does not help")
        void otherSlay() {
            EgoItem ego = ego(flags(), new HashMap<>(), new HashMap<>(), new HashSet<>(),
                    new HashSet<>(Set.of(evil2)), new HashMap<>());

            player.getItemKnowledge().learnSlay(animal);
            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("curses")
    class Curses {

        private EgoItem egoWithCurse(Curse curse) {
            Map<Curse, CurseData> curses = new HashMap<>();
            curses.put(curse, new CurseData(20, 0));
            return ego(flags(), new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>(), curses);
        }

        @Test
        @DisplayName("a curse the ego carries must be recognized")
        void curseMustBeKnown() {
            EgoItem ego = egoWithCurse(siren);

            assertFalse(PlayerKnowledge.knowsEgo(player, ego, null));

            player.getItemKnowledge().learnCurse(siren);
            assertTrue(PlayerKnowledge.knowsEgo(player, ego, null));
        }

        @Test
        @DisplayName("recognizing a different curse does not help")
        void otherCurse() {
            player.getItemKnowledge().learnCurse(cowardice);

            assertFalse(PlayerKnowledge.knowsEgo(player, egoWithCurse(siren), null));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("everything together")
    class Combined {

        /**
         * One of each, so that any single clause failing is enough to make the whole answer false.
         */
        private EgoItem richEgo() {
            ElementInfo info = new ElementInfo();
            info.setResLevel(1);
            Map<ElementEnum, ElementInfo> elements = new HashMap<>();
            elements.put(ElementEnum.ELEM_FIRE, info);
            Map<ObjectModifier, Random> modifiers = new HashMap<>();
            modifiers.put(ObjectModifier.OM_STR, new Random(2, 0, 0, 0, false));
            Map<Curse, CurseData> curses = new HashMap<>();
            curses.put(siren, new CurseData(20, 0));
            return ego(flags(ObjectFlag.OF_FEATHER), modifiers, elements, new HashSet<>(Set.of(fire)),
                    new HashSet<>(Set.of(animal)), curses);
        }

        private void learnEverythingExcept(String skipped) {
            KnownObject known = player.getItemKnowledge();
            if (!skipped.equals("flag")) known.learnFlag(ObjectFlag.OF_FEATHER);
            if (!skipped.equals("modifier")) known.learnModifier(ObjectModifier.OM_STR);
            if (!skipped.equals("element")) known.learnResistance(ElementEnum.ELEM_FIRE);
            if (!skipped.equals("brand")) known.learnBrand(fire);
            if (!skipped.equals("slay")) known.learnSlay(animal);
            if (!skipped.equals("curse")) known.learnCurse(siren);
        }

        @Test
        @DisplayName("an ego is known once every one of its properties is read")
        void allKnown() {
            learnEverythingExcept("");

            assertTrue(PlayerKnowledge.knowsEgo(player, richEgo(), null));
            assertTrue(PlayerKnowledge.knowsEgo(player, richEgo(), itemWithStrength(2)));
        }

        @Test
        @DisplayName("any one unread property is enough to leave it unknown")
        void anyOneMissing() {
            for (String skipped : List.of("flag", "modifier", "element", "brand", "slay", "curse")) {
                player = new Player();
                player.setItemKnowledge(new KnownObject());
                learnEverythingExcept(skipped);

                assertFalse(PlayerKnowledge.knowsEgo(player, richEgo(), null),
                        "ego should be unknown with only the " + skipped + " unread");
            }
        }
    }
}
