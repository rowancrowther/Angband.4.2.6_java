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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#wipe()} and {@link ItemObject#getObjectFlags()}.
 *
 * <p>{@code wipe} is the port of C's {@code object_wipe} ({@code obj-pile.c:704}): free the three
 * allocated arrays, then {@code memset} the whole struct to zero. The port has no frees to make —
 * the old collections are simply discarded — so what is worth pinning down is that every field C's
 * {@code memset} reaches is also reached here, landing on the value zero means for that field's
 * type: {@code null} for a reference, {@code 0} for a scalar, and — for the collection fields — a
 * fresh empty collection rather than {@code null}, since C's zeroed pointer and this port's
 * "nothing here yet" are both read the same way by every accessor.
 *
 * <p>Two fields are worth a dedicated test rather than a line in the group assertion:
 * {@code mimickingMIndex}, which a first pass at this method left unreset, and {@code origin}, which
 * a first pass reset to {@code null} rather than {@link ObjectOriginEnum#ORIGIN_NONE} — the value
 * C's zeroed {@code origin} byte actually lands on, and the one the no-arg constructor already uses
 * for the same blank-slate state.
 *
 * @author Rowan Crowther
 */
class ItemObjectWipeTest {

    /**
     * The item under test: fully populated, then wiped.
     */
    private ItemObject item;

    /**
     * Writes a private field on an {@link ItemObject} by reflection, for the fields with no getter.
     *
     * @param item the item to read
     * @param name the declared field name
     * @return the field's value
     */
    private static Object read(ItemObject item, String name) throws Exception {
        Field field = ItemObject.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(item);
    }

    /**
     * A minimal curse definition, distinguishable from another only by its name.
     *
     * @param name the curse's name
     * @return a curse with every other field empty
     */
    private static Curse curse(String name) {
        return new Curse(name, List.of(), 0, null, new Flag<>(ObjectFlag.class), Map.of(), Map.of(), 0, 0, 0,
                List.of(), new Flag<>(ObjectFlag.class), "", "");
    }

    /**
     * A minimal artifact definition, non-null and nothing more.
     *
     * @return an artifact with every field empty
     */
    private static Artifact artifact() {
        return new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(), new LinkedHashMap<>(),
                0, 0, 0, 0, null, null, null);
    }

    /**
     * A minimal ego item definition, non-null and nothing more.
     *
     * @return an ego item with every field empty
     */
    private static EgoItem ego() {
        return new EgoItem("of Testing", null, 0, 0, new Flag<>(ObjectFlag.class),
                new Flag<>(ObjectFlag.class), null, Map.of(), Map.of(), Map.of(),
                Set.of(), Set.of(), Map.of(), 0, 0, 0, 0, List.of(),
                null, null, null, 0, 0, 0, null, null, false);
    }

    /**
     * A brand, for the item's brand set.
     *
     * @param code the brand's code
     * @return the brand
     */
    private static Brand brand(String code) {
        return new Brand(code, "fire", "burns", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, 2, 3, 20);
    }

    /**
     * A slay, for the item's slay set.
     *
     * @param code the slay's code, which it parses its own family and level out of
     * @return the slay
     */
    private static Slay slay(String code) {
        return new Slay(code, "evil", null, "smites", "smites", MonsterRaceFlag.RF_EVIL, 3, 3, 15);
    }

    /**
     * Builds an item through the long constructor with every field given a distinct, non-blank
     * value, so that a wipe which missed one would leave a value this test can still see.
     *
     * @return the fully-populated item
     */
    private static ItemObject populatedItem() {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        flags.set(ObjectFlag.OF_SEE_INVIS);

        Flag<ObjectNotice> notice = new Flag<>(ObjectNotice.class);
        notice.set(ObjectNotice.OBJ_NOTICE_WORN);

        LinkedHashMap<Curse, CurseData> curses = new LinkedHashMap<>();
        curses.put(curse("Test Curse"), new CurseData(3, 5));

        return new ItemObject(new ObjectKind(), ego(), artifact(), new ItemObject(),
                Loc.row(3).col(2), TValue.TV_SWORD, 4, "5",
                6, 7, 8, 9, 10,
                "2d6", 11, 12,
                flags, Map.of(ObjectModifier.OM_STR, 1),
                Map.of(ElementEnum.ELEM_FIRE, new ElementInfo()),
                Set.of(brand("BRAND_FIRE")), Set.of(slay("EVIL_3")), curses,
                Arrays.asList((Effect) null), "It fires.",
                Arrays.asList((Activation) null), "1d8",
                13, 14,
                notice, 15, 16,
                ObjectOriginEnum.ORIGIN_STORE, 17, new MonsterRace(), "@i1");
    }

    @BeforeEach
    void buildAndWipe() {
        item = populatedItem();
        item.wipe();
    }

    /**
     * A sanity check on the fixture itself: every field this test cares about is non-blank before
     * {@code wipe} runs, so the assertions after it prove the method acted rather than finding
     * everything already at its resting value.
     */
    @Test
    @DisplayName("the fixture is non-blank before wipe, so wipe is what changes it")
    void fixtureStartsPopulated() throws Exception {
        ItemObject fresh = populatedItem();

        assertNotNull(fresh.getKind());
        assertTrue(fresh.isEgo());
        assertTrue(fresh.isArtifact());
        assertTrue(fresh.isKnown());
        assertNotNull(fresh.getGrid());
        assertNotNull(fresh.gettValue());
        assertEquals(4, fresh.getsValue());
        assertFalse(fresh.getObjectFlags().isEmpty());
        assertFalse(fresh.getBrands().isEmpty());
        assertFalse(fresh.getSlays().isEmpty());
        assertFalse(fresh.getCurses().isEmpty());
        assertEquals(16, read(fresh, "mimickingMIndex"));
        assertEquals(ObjectOriginEnum.ORIGIN_STORE, read(fresh, "origin"));
    }

    /**
     * Scalars and plain references — the fields C's {@code memset} zeroes to {@code 0} or a null
     * pointer, ported here to {@code 0} or {@code null}.
     */
    @Test
    @DisplayName("scalars and plain references go to zero or null")
    void resetsScalarsAndReferences() throws Exception {
        assertNull(item.getKind());
        assertNull(read(item, "ego"));
        assertFalse(item.isArtifact());
        assertFalse(item.isKnown());
        assertNull(item.getGrid());
        assertNull(item.gettValue());
        assertEquals(0, item.getsValue());
        assertEquals(0, item.getpValue());
        assertEquals(0, item.getWeight());
        assertEquals(0, item.getDamageDice());
        assertEquals(0, item.getDamageSides());
        assertEquals(0, item.getBaseAC());
        assertEquals(0, item.getToAC());
        assertNull(read(item, "baseDamage"));
        assertEquals(0, item.getToDam());
        assertEquals(0, item.getToHit());
        assertNull(item.getTime());
        assertEquals(0, item.getTimeout());
        assertEquals(0, item.getNumber());
        assertEquals(0, read(item, "heldMIndex"));
        assertEquals(0, read(item, "originDepth"));
        assertNull(read(item, "originRace"));
        assertNull(item.getNote());
        assertNull(read(item, "effectMessage"));
    }

    /**
     * The collection fields — C zeroes each to a null pointer, which every accessor here would read
     * as "nothing", but a null field is not what this port stores: {@code wipe} assigns a fresh
     * empty collection instead, matching the shape every other collection field already carries.
     */
    @Test
    @DisplayName("collections are replaced with fresh, empty ones - never null")
    void resetsCollectionsToEmptyNotNull() throws Exception {
        assertNotNull(item.getObjectFlags());
        assertTrue(item.getObjectFlags().isEmpty());

        assertNotNull(item.getModifiers());
        assertTrue(item.getModifiers().isEmpty());

        assertNotNull(item.getElInfo());
        assertTrue(item.getElInfo().isEmpty());

        assertNotNull(item.getBrands());
        assertTrue(item.getBrands().isEmpty());

        assertNotNull(item.getSlays());
        assertTrue(item.getSlays().isEmpty());

        assertNotNull(item.getCurses());
        assertTrue(item.getCurses().isEmpty());

        assertNotNull(item.getEffect());
        assertTrue(item.getEffect().isEmpty());

        assertNotNull(read(item, "activation"));
        assertTrue(((List<?>) read(item, "activation")).isEmpty());

        assertNotNull(item.getNotice());
        assertTrue(item.getNotice().isEmpty());
    }

    /**
     * The one field a first pass at this method left unreset: {@code mimickingMIndex}, C's
     * {@code mimicking_m_idx} ({@code object.h:466}), zeroed by the same {@code memset} as every
     * other scalar. A stale value here would misidentify a wiped object as a mimic disguise.
     */
    @Test
    @DisplayName("zeroes the mimicking-monster index")
    void resetsMimickingMIndex() throws Exception {
        assertEquals(0, read(item, "mimickingMIndex"));
    }

    /**
     * The other field a first pass got wrong: {@code origin} was reset to {@code null} rather than
     * {@link ObjectOriginEnum#ORIGIN_NONE}. C's zeroed {@code origin} byte lands on ordinal 0, which
     * is {@code ORIGIN_NONE} in both the C {@code ORIGIN(...)} list ({@code list-origins.h}) and this
     * enum — the same value the no-arg {@link ItemObject#ItemObject()} constructor already uses for
     * a blank object, so a wiped object and a freshly constructed one now agree.
     */
    @Test
    @DisplayName("resets origin to ORIGIN_NONE, not null")
    void resetsOriginToNoOrigin() throws Exception {
        assertEquals(ObjectOriginEnum.ORIGIN_NONE, read(item, "origin"));
    }

    /**
     * Wiping one item must not disturb a second item built from the same fixture — {@code wipe}
     * replaces this object's own fields, it does not touch anything shared by reference.
     */
    @Test
    @DisplayName("wiping one item leaves another, separately-built item untouched")
    void wipingOneItemDoesNotAffectAnother() {
        ItemObject other = populatedItem();

        assertNotNull(other.getKind());
        assertTrue(other.getObjectFlags().has(ObjectFlag.OF_SEE_INVIS));
        assertEquals(4, other.getsValue());
    }

    /**
     * Tests {@link ItemObject#getObjectFlags()} directly, separately from its role as the assertion
     * used throughout {@code wipe}'s tests above.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("getObjectFlags")
    class GetObjectFlags {

        @Test
        @DisplayName("returns this item's flags")
        void returnsTheItemsFlags() {
            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            flags.set(ObjectFlag.OF_FREE_ACT);
            ItemObject withFlags = new ItemObject(new ObjectKind(), null, null, null, Loc.zero,
                    TValue.TV_SWORD, 0, "0", 0, 0, 0, 0, 0, "0", 0, 0,
                    flags, Map.of(), Map.of(), Set.of(), Set.of(), new LinkedHashMap<>(),
                    List.of(), null, List.of(), "0", 0, 1,
                    new Flag<>(ObjectNotice.class), 0, 0,
                    ObjectOriginEnum.ORIGIN_NONE, 0, null, null);

            assertSame(flags, withFlags.getObjectFlags());
            assertTrue(withFlags.getObjectFlags().has(ObjectFlag.OF_FREE_ACT));
        }

        /**
         * Unlike {@link ItemObject#getFlags()}, which hands back a defensive copy, this accessor
         * hands back the live field: writing to the returned set reaches the item, and setting a
         * flag on the item is visible through the returned set without calling the accessor again.
         */
        @Test
        @DisplayName("returns the live set, unlike getFlags")
        void returnsTheLiveSetNotACopy() {
            ItemObject withFlags = new ItemObject(new ObjectKind(), null, null, null, Loc.zero,
                    TValue.TV_SWORD, 0, "0", 0, 0, 0, 0, 0, "0", 0, 0,
                    new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(), new LinkedHashMap<>(),
                    List.of(), null, List.of(), "0", 0, 1,
                    new Flag<>(ObjectNotice.class), 0, 0,
                    ObjectOriginEnum.ORIGIN_NONE, 0, null, null);

            withFlags.getObjectFlags().set(ObjectFlag.OF_HOLD_LIFE);
            assertTrue(withFlags.hasFlag(ObjectFlag.OF_HOLD_LIFE),
                    "writing through getObjectFlags() must reach the item");

            withFlags.setFlag(ObjectFlag.OF_FEATHER);
            assertTrue(withFlags.getObjectFlags().has(ObjectFlag.OF_FEATHER),
                    "setting a flag on the item must be visible through getObjectFlags()");
        }
    }
}
