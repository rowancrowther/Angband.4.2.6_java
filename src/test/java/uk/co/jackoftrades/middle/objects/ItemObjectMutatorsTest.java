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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject}'s mutators and the small accessors around them.
 *
 * <p>The brand and slay mutators exist for one reason, and it is the reason they are worth testing:
 * the getters answer an immutable empty collection for an item whose set has never been created, so
 * a caller cannot add through them. Each mutator therefore creates the set on demand — and the
 * distinction between "no set" and "an empty set" is preserved deliberately elsewhere, so a mutator
 * that normalised one to the other would change what {@link ItemObject#copy} carries.
 *
 * <p>The flag mutators are the other pair worth pinning: {@code setFlags} adds and
 * {@code setFlagsTo} replaces, and the names are close enough to confuse.
 *
 * @author Rowan Crowther
 */
class ItemObjectMutatorsTest {

    /**
     * The item under test, fresh each time.
     */
    private ItemObject item;

    /**
     * Builds an item the way the parser does, with every collection present and empty.
     *
     * @return the item
     */
    private static ItemObject loadedItem() {
        return new ItemObject(new ObjectKind(), null, null, null, Loc.zero, TValue.TV_SWORD, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, null);
    }

    /**
     * A brand, for the set mutators.
     *
     * @param code the brand's code
     * @return the brand
     */
    private static Brand brand(String code) {
        return new Brand(code, "fire", "burns", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, 2, 3, 20);
    }

    /**
     * A slay, likewise.
     *
     * @param code the slay's code, which it parses its own family and level out of
     * @return the slay
     */
    private static Slay slay(String code) {
        return new Slay(code, "evil", null, "smites", "smites", MonsterRaceFlag.RF_EVIL, 3, 3, 15);
    }

    /**
     * A loaded item, as the data-file constructor leaves it: flag sets, maps and collections all
     * present and empty.
     *
     * <p>Not the no-argument constructor, which leaves the flag sets, the notice set and the
     * modifier map null — that shape is for the knowledge code, which fills them in, and most of
     * the mutators here would throw on it. Two tests below cover that state deliberately.
     */
    @BeforeEach
    void newItem() {
        item = loadedItem();
    }

    /**
     * The brand and slay sets, which are created on demand.
     */
    @Nested
    @DisplayName("brands and slays")
    class BrandsAndSlays {

        /**
         * A freshly loaded item has empty sets, and an item built the short way has none at all —
         * the getters answer an empty collection for both, which is what lets every reader iterate
         * without checking.
         */
        @Test
        @DisplayName("an item with no brands reads as having none")
        void bareItemHasNone() {
            assertTrue(item.getBrands().isEmpty());
            assertTrue(item.getSlays().isEmpty());

            ItemObject bare = new ItemObject();
            assertTrue(bare.getBrands().isEmpty(), "and an item with no set at all reads the same");
            assertTrue(bare.getSlays().isEmpty());
        }

        /**
         * Adding creates the set, which is the whole point of the mutator: the empty collection the
         * getter hands out would refuse the write.
         */
        @Test
        @DisplayName("adding creates the set")
        void addingCreatesTheSet() {
            Brand fire = brand("FIRE_2");
            item.addBrand(fire);

            assertEquals(1, item.getBrands().size());
            assertSame(fire, item.getBrands().iterator().next());
        }

        /**
         * The same for slays, which are counted separately from brands by the power calculation.
         */
        @Test
        @DisplayName("slays are added independently of brands")
        void slaysAreIndependent() {
            item.addBrand(brand("FIRE_2"));
            item.addSlay(slay("EVIL_3"));

            assertEquals(1, item.getBrands().size());
            assertEquals(1, item.getSlays().size());
        }

        /**
         * Removing takes one out and leaves the rest.
         */
        @Test
        @DisplayName("removing takes out one and leaves the rest")
        void removingLeavesTheRest() {
            Brand fire = brand("FIRE_2");
            Brand cold = brand("COLD_2");
            item.addBrand(fire);
            item.addBrand(cold);

            item.removeBrand(fire);

            assertEquals(1, item.getBrands().size());
            assertSame(cold, item.getBrands().iterator().next());
        }

        /**
         * Clearing empties the set without destroying it, so the item is left holding an empty set
         * rather than none — and a later add does not have to recreate it.
         */
        @Test
        @DisplayName("clearing empties the set")
        void clearingEmptiesTheSet() {
            item.addBrand(brand("FIRE_2"));
            item.addSlay(slay("EVIL_3"));

            item.clearBrands();
            item.clearSlays();

            assertTrue(item.getBrands().isEmpty());
            assertTrue(item.getSlays().isEmpty());
        }

        /**
         * Clearing an item that never had a set is harmless — the mutator creates one and empties
         * it, rather than refusing.
         */
        @Test
        @DisplayName("clearing an item with no set is harmless")
        void clearingBareItemIsHarmless() {
            item = new ItemObject();
            item.clearBrands();
            item.clearSlays();

            assertTrue(item.getBrands().isEmpty());
        }
    }

    /**
     * The flag mutators, whose names are one preposition apart and whose behaviours are not.
     */
    @Nested
    @DisplayName("flag mutators")
    class Flags {

        /**
         * {@code setFlag} switches one on and reports whether it was new, which is how the
         * rune-learning code tells a first sighting from a repeat.
         */
        @Test
        @DisplayName("setting one flag reports whether it was new")
        void singleFlagReportsNovelty() {
            assertTrue(item.setFlag(ObjectFlag.OF_FEATHER), "the first time is new");
            assertFalse(item.setFlag(ObjectFlag.OF_FEATHER), "the second time is not");
        }

        /**
         * {@code setFlags} adds a whole set, leaving what was already there.
         */
        @Test
        @DisplayName("setFlags adds, leaving what was there")
        void setFlagsAdds() {
            item.setFlag(ObjectFlag.OF_FEATHER);

            Flag<ObjectFlag> more = new Flag<>(ObjectFlag.class);
            more.set(List.of(ObjectFlag.OF_AGGRAVATE));
            item.setFlags(more);

            assertTrue(item.hasFlag(ObjectFlag.OF_FEATHER), "the earlier flag survived");
            assertTrue(item.hasFlag(ObjectFlag.OF_AGGRAVATE));
        }

        /**
         * {@code setFlagsTo} replaces, so what was there is gone. This is the one that catches
         * people out.
         */
        @Test
        @DisplayName("setFlagsTo replaces, discarding what was there")
        void setFlagsToReplaces() {
            item.setFlag(ObjectFlag.OF_FEATHER);

            Flag<ObjectFlag> replacement = new Flag<>(ObjectFlag.class);
            replacement.set(List.of(ObjectFlag.OF_AGGRAVATE));
            item.setFlagsTo(replacement);

            assertFalse(item.hasFlag(ObjectFlag.OF_FEATHER), "the earlier flag was discarded");
            assertTrue(item.hasFlag(ObjectFlag.OF_AGGRAVATE));
        }

        /**
         * And it copies in rather than adopting the caller's set, so the two do not share afterwards
         * — the reason a known counterpart can differ from its item.
         */
        @Test
        @DisplayName("setFlagsTo copies in rather than adopting the set")
        void setFlagsToCopiesIn() {
            Flag<ObjectFlag> source = new Flag<>(ObjectFlag.class);
            source.set(List.of(ObjectFlag.OF_AGGRAVATE));
            item.setFlagsTo(source);

            source.on(ObjectFlag.OF_FEATHER);

            assertFalse(item.hasFlag(ObjectFlag.OF_FEATHER),
                    "a later change to the caller's set does not reach the item");
        }
    }

    /**
     * The modifier map, which is created on demand like the brand sets.
     */
    @Nested
    @DisplayName("modifiers")
    class Modifiers {

        /**
         * A modifier the item does not carry reads as zero, which is what C's fixed array gives.
         */
        @Test
        @DisplayName("an absent modifier reads as zero")
        void absentModifierIsZero() {
            assertEquals(0, item.getModifierValue(ObjectModifier.OM_STEALTH));
        }

        /**
         * On an item built the short way there is no map at all, and this accessor reads the field
         * directly rather than through the null-safe getter beside it — so it throws where
         * {@code getModifiers()} would answer an empty map. Worth pinning: the two are one line
         * apart and disagree about the same state.
         */
        @Test
        @DisplayName("an item with no modifier map at all throws rather than reading zero")
        void bareItemThrowsOnModifierRead() {
            ItemObject bare = new ItemObject();

            assertTrue(bare.getModifiers().isEmpty(), "the null-safe getter answers empty");
            assertThrows(NullPointerException.class,
                    () -> bare.getModifierValue(ObjectModifier.OM_STEALTH));
        }

        /**
         * Storing one creates the map and reads back.
         */
        @Test
        @DisplayName("storing a modifier creates the map")
        void storingCreatesTheMap() {
            item.putModifier(ObjectModifier.OM_STEALTH, 3);

            assertEquals(3, item.getModifierValue(ObjectModifier.OM_STEALTH));
        }

        /**
         * The stat-named overload translates a characteristic into its modifier, so the two ways of
         * asking agree. That translation is by name, which makes it worth pinning: a stat and its
         * modifier are separate enums that happen to share a suffix.
         */
        @Test
        @DisplayName("asking by stat agrees with asking by modifier")
        void statOverloadAgrees() {
            item.putModifier(ObjectModifier.OM_STR, 2);

            assertEquals(2, item.getModifierValue(Stats.STAT_STR));
            assertEquals(item.getModifierValue(ObjectModifier.OM_STR), item.getModifierValue(Stats.STAT_STR));
        }
    }

    /**
     * The plain scalar setters, checked as a group because they are adjacent same-typed members.
     */
    @Nested
    @DisplayName("scalar setters")
    class Scalars {

        /**
         * Each writes its own field. The values are all different so a crossed pair cannot pass.
         */
        @Test
        @DisplayName("each scalar setter writes its own field")
        void scalarsAreDistinct() {
            item.setNumber(4);
            item.setpValue(5);
            item.setsValue(6);
            item.setDamageDice(7);
            item.setDamageSides(8);

            assertEquals(4, item.getNumber());
            assertEquals(5, item.getpValue());
            assertEquals(6, item.getsValue());
            assertEquals(7, item.getDamageDice());
            assertEquals(8, item.getDamageSides());
        }
    }

    /**
     * The knowledge accessors, which answer about the item's known half rather than the item.
     */
    @Nested
    @DisplayName("knowledge")
    class Knowledge {

        /**
         * A freshly loaded item has no known half, so it is not known and its ego cannot be either.
         */
        @Test
        @DisplayName("a freshly loaded item is unknown and has no ego")
        void bareItemIsUnknown() {
            assertFalse(item.isKnown());
            assertFalse(item.isEgo());
            assertNull(item.getEgo());
        }

        /**
         * Clearing the known half is how the absorb code detaches knowledge before disposing of an
         * object, and it leaves the item reporting unknown.
         *
         * <p>The known half has no setter — it is assigned by the constructor and by the knowledge
         * code — so the test attaches one by reflection to have something to clear.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("clearing the known half makes the item unknown")
        void clearingKnownMakesItUnknown() throws Exception {
            Field field = ItemObject.class.getDeclaredField("known");
            field.setAccessible(true);
            field.set(item, new ItemObject());

            assertTrue(item.isKnown());

            item.nullKnown();

            assertFalse(item.isKnown());
            assertNull(item.getKnown());
        }

        /**
         * The notice flags start empty — nothing has been noticed about a bare item.
         */
        @Test
        @DisplayName("a freshly loaded item has noticed nothing")
        void noticeStartsEmpty() {
            assertTrue(item.getNotice().isEmpty());
        }

        /**
         * And a notice raised is readable, which is how the ignore code learns an item has been
         * assessed.
         */
        @Test
        @DisplayName("a raised notice is readable")
        void noticeIsReadable() {
            item.orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);

            assertTrue(item.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED));
        }
    }

    /**
     * The per-element resistance mutator, which has no C function behind it: C assigns
     * {@code obj->el_info[i].res_level} inline, into a {@code struct element_info el_info[ELEM_MAX]}
     * embedded in the object and zero-filled by {@code object_new}. Every expectation below is that
     * array's behaviour — a slot always exists, the write always lands, and it touches
     * {@code res_level} only.
     */
    @Nested
    @DisplayName("element resistance levels")
    class ElementResistances {

        /**
         * C's {@code equip_learn_element} writes {@code obj->known->el_info[element].res_level = 1}
         * ({@code obj-knowledge.c:2153}) onto a counterpart object, which is a blank
         * {@code object_new} struct. The Java counterpart is the no-argument constructor, whose map
         * does not exist at all, so the write has to create it rather than be dropped.
         */
        @Test
        @DisplayName("writing to an item with no map at all creates it")
        void bareItemGetsTheMap() {
            ItemObject bare = new ItemObject();

            bare.setElInfoResLevel(ElementEnum.ELEM_FIRE, 1);

            assertEquals(1, bare.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
        }

        /**
         * And an element absent from a map that does exist. C has no such state — the slot is there
         * at zero — so the value must land, and the entry created for it starts with no flags, which
         * is what the zero-fill leaves.
         */
        @Test
        @DisplayName("an element with no entry gets one, with empty flags")
        void absentElementGetsAnEntry() {
            item.setElInfoResLevel(ElementEnum.ELEM_COLD, 3);

            ElementInfo cold = item.getElInfo().get(ElementEnum.ELEM_COLD);
            assertEquals(3, cold.getResLevel());
            assertFalse(cold.has(ElementInfoEnum.EL_INFO_IGNORE), "a fresh entry carries no flags");
            assertFalse(cold.has(ElementInfoEnum.EL_INFO_HATES));
        }

        /**
         * Overwriting is the ordinary path — {@code obj-curse.c:519} raises an existing level of 1 to
         * 3 — and the C assignment names one field, so the flags alongside it survive.
         */
        @Test
        @DisplayName("an existing level is replaced, and the flags survive")
        void existingEntryKeepsItsFlags() {
            ElementInfo acid = new ElementInfo();
            acid.setResLevel(1);
            acid.on(ElementInfoEnum.EL_INFO_IGNORE);
            item.putElInfo(ElementEnum.ELEM_ACID, acid);

            item.setElInfoResLevel(ElementEnum.ELEM_ACID, 3);

            assertEquals(3, item.getElInfo().get(ElementEnum.ELEM_ACID).getResLevel());
            assertTrue(item.getElInfo().get(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE),
                    "the C assignment names res_level only");
            assertSame(acid, item.getElInfo().get(ElementEnum.ELEM_ACID), "and writes in place");
        }

        /**
         * The scale is C's and nothing here interprets it: {@code obj-curse.c:521} parks
         * {@code -32768} in the field as a marker while curses merge, and {@code obj-init.c:2058}
         * lets the data files write a plain vulnerability of -1.
         */
        @Test
        @DisplayName("negative levels and C's -32768 marker pass through unchanged")
        void negativeLevelsArePassedThrough() {
            item.setElInfoResLevel(ElementEnum.ELEM_ELEC, -1);
            assertEquals(-1, item.getElInfo().get(ElementEnum.ELEM_ELEC).getResLevel());

            item.setElInfoResLevel(ElementEnum.ELEM_ELEC, Short.MIN_VALUE);
            assertEquals(-32768, item.getElInfo().get(ElementEnum.ELEM_ELEC).getResLevel(),
                    "the vulnerable-and-resistant marker is a value like any other");
        }

        /**
         * Zero is neutral, not absent. {@code obj-knowledge.c:1062} writes it deliberately to blank a
         * counterpart's knowledge, and {@code obj-curse.c:564} to clear the merge marker, so it has
         * to be storable rather than treated as "no entry".
         */
        @Test
        @DisplayName("zero is stored, not skipped")
        void zeroIsStored() {
            item.setElInfoResLevel(ElementEnum.ELEM_FIRE, 3);
            item.setElInfoResLevel(ElementEnum.ELEM_FIRE, 0);

            assertEquals(0, item.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
        }

        /**
         * The C write indexes one slot, so the neighbouring elements are untouched — including
         * staying absent, which the map represents and the array cannot.
         */
        @Test
        @DisplayName("only the named element is touched")
        void otherElementsAreUntouched() {
            item.setElInfoResLevel(ElementEnum.ELEM_FIRE, 3);
            item.setElInfoResLevel(ElementEnum.ELEM_COLD, 1);

            assertEquals(3, item.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
            assertEquals(1, item.getElInfo().get(ElementEnum.ELEM_COLD).getResLevel());
            assertNull(item.getElInfo().get(ElementEnum.ELEM_ACID), "acid was never written");
        }

        /**
         * The map the mutator creates has to stay writable afterwards, or the next write — through
         * {@code putElInfo}, which guards on the map being null and so would no longer create one —
         * would fail on an item this method had already touched.
         */
        @Test
        @DisplayName("the created map still accepts later writes")
        void createdMapStaysWritable() {
            ItemObject bare = new ItemObject();
            bare.setElInfoResLevel(ElementEnum.ELEM_FIRE, 1);

            bare.putElInfo(ElementEnum.ELEM_ACID, new ElementInfo());
            bare.setElInfoResLevel(ElementEnum.ELEM_COLD, 1);

            assertEquals(3, bare.getElInfo().size());
        }
    }
}
