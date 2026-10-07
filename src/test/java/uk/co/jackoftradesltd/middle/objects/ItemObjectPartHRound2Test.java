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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Part H round-two checks on the {@link ItemObject} accessors and mutators: the never-null grid,
 * the flag and notice accessors on a missing set, the self-copy case of {@code setFlagsTo}, the
 * modifier readers, and {@code setEffectMessage}. Expected values are worked from
 * {@code struct object} in {@code object.h}, whose arrays are zero-filled so are never absent,
 * and from C's {@code of_wipe} then {@code of_copy}, not from the Java.
 *
 * @author Rowan Crowther
 */
class ItemObjectPartHRound2Test {

    private static void nullField(ItemObject item, String name) throws Exception {
        Field field = ItemObject.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(item, null);
    }

    private static Object field(ItemObject item, String name) throws Exception {
        Field field = ItemObject.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(item);
    }

    /**
     * C's {@code obj->grid} of an item not on the map is (0, 0), and {@code loc_is_zero} on it is
     * true. A bare item must answer that rather than {@code null}.
     */
    @Test
    @DisplayName("getGrid: a bare item reads as the zero grid")
    void bareItemGridIsZero() {
        ItemObject item = new ItemObject();

        assertNotNull(item.getGrid());
        assertTrue(item.getGrid().isZero());
    }

    /**
     * A grid that was set is read back; clearing it with {@code null} returns the item to (0, 0);
     * a wipe does the same.
     */
    @Test
    @DisplayName("getGrid: set, clear with null, and wipe")
    void gridSetClearWipe() {
        ItemObject item = new ItemObject();
        Loc spot = Loc.row(4).col(7);

        item.setGrid(spot);
        assertSame(spot, item.getGrid());
        assertEquals(7, item.getGrid().getX());
        assertEquals(4, item.getGrid().getY());
        assertFalse(item.getGrid().isZero());

        item.setGrid(null);
        assertTrue(item.getGrid().isZero());

        item.setGrid(spot);
        item.wipe();
        assertTrue(item.getGrid().isZero());
    }

    /**
     * The 65 accessors are pairs over distinct fields; a swapped pair would show as one value
     * landing in another's getter. Distinct values per field make a swap visible.
     */
    @Test
    @DisplayName("scalar accessors each read their own field")
    void scalarAccessorsDoNotCross() {
        ItemObject item = new ItemObject();
        item.setToHit(1);
        item.setToDam(2);
        item.setToAC(3);
        item.setBaseAC(4);
        item.setDamageDice(5);
        item.setDamageSides(6);
        item.setpValue(7);
        item.setsValue(8);
        item.setNumber(9);
        item.setWeight(10);
        item.setTimeout(11);

        assertEquals(1, item.getToHit());
        assertEquals(2, item.getToDam());
        assertEquals(3, item.getToAC());
        assertEquals(4, item.getBaseAC());
        assertEquals(5, item.getDamageDice());
        assertEquals(6, item.getDamageSides());
        assertEquals(7, item.getpValue());
        assertEquals(8, item.getsValue());
        assertEquals(9, item.getNumber());
        assertEquals(10, item.getWeight());
        assertEquals(11, item.getTimeout());
    }

    /**
     * A bare item's tval is C's zero, {@code TV_NONE}, and the setter replaces it.
     */
    @Test
    @DisplayName("tValue defaults to TV_NONE")
    void tValueDefault() {
        ItemObject item = new ItemObject();
        assertEquals(TValue.TV_NONE, item.gettValue());

        item.settValue(TValue.TV_SWORD);
        assertEquals(TValue.TV_SWORD, item.gettValue());
    }

    /**
     * {@code origin} starts at C's zero, {@code ORIGIN_NONE}; {@code setOrigin} writes it.
     */
    @Test
    @DisplayName("setOrigin from the zero origin")
    void originDefaultAndSet() throws Exception {
        ItemObject item = new ItemObject();
        assertSame(ObjectOriginEnum.ORIGIN_NONE, field(item, "origin"));

        item.setOrigin(ObjectOriginEnum.ORIGIN_FLOOR);
        assertSame(ObjectOriginEnum.ORIGIN_FLOOR, field(item, "origin"));
    }

    /**
     * {@code getFlags} and {@code getNotice} hand out independent copies: raising a flag on the
     * copy does not reach the item.
     */
    @Test
    @DisplayName("getFlags and getNotice answer copies")
    void flagAndNoticeGettersCopy() {
        ItemObject item = new ItemObject();
        item.setFlag(ObjectFlag.OF_FEATHER);
        item.orNotice(ObjectNotice.OBJ_NOTICE_WORN);

        Flag<ObjectFlag> flags = item.getFlags();
        flags.set(ObjectFlag.OF_BURNS_OUT);
        Flag<ObjectNotice> notice = item.getNotice();
        notice.on(ObjectNotice.OBJ_NOTICE_IGNORE);

        assertTrue(flags.has(ObjectFlag.OF_FEATHER));
        assertFalse(item.hasFlag(ObjectFlag.OF_BURNS_OUT));
        assertTrue(notice.has(ObjectNotice.OBJ_NOTICE_WORN));
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_IGNORE));
    }

    /**
     * The flag and notice sets are inline zeroed arrays in C, so they cannot be absent. With the
     * fields forced to null every accessor must behave as on an empty set and not throw.
     */
    @Test
    @DisplayName("flag and notice accessors on a missing set read as empty")
    void missingSetsReadAsEmpty() throws Exception {
        ItemObject item = new ItemObject();
        nullField(item, "flags");
        nullField(item, "notice");

        assertFalse(item.getFlags().has(ObjectFlag.OF_FEATHER));
        assertFalse(item.getNotice().has(ObjectNotice.OBJ_NOTICE_WORN));
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_WORN));
        assertNotNull(item.getObjectFlags());
        assertFalse(item.hasFlag(ObjectFlag.OF_FEATHER));
    }

    /**
     * The mutators on a missing set create it, then behave as C's {@code of_on}, {@code |=} and
     * {@code &= ~}: a first raise reports true, a repeat reports false.
     */
    @Test
    @DisplayName("flag and notice mutators on a missing set create it")
    void mutatorsOnMissingSets() throws Exception {
        ItemObject item = new ItemObject();
        nullField(item, "flags");
        nullField(item, "notice");

        assertTrue(item.setFlag(ObjectFlag.OF_FEATHER));
        assertFalse(item.setFlag(ObjectFlag.OF_FEATHER));
        assertTrue(item.hasFlag(ObjectFlag.OF_FEATHER));

        nullField(item, "flags");
        Flag<ObjectFlag> mask = new Flag<>(ObjectFlag.class);
        mask.set(ObjectFlag.OF_BURNS_OUT);
        assertTrue(item.setFlags(mask));
        assertTrue(item.hasFlag(ObjectFlag.OF_BURNS_OUT));

        assertTrue(item.setNoticeOn(ObjectNotice.OBJ_NOTICE_WORN));
        assertFalse(item.setNoticeOn(ObjectNotice.OBJ_NOTICE_WORN));
        assertTrue(item.setNoticeOff(ObjectNotice.OBJ_NOTICE_WORN));
        assertFalse(item.setNoticeOff(ObjectNotice.OBJ_NOTICE_WORN));

        nullField(item, "notice");
        item.orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);
        assertTrue(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_ASSESSED));
    }

    /**
     * {@code setFlagsTo} replaces rather than adds: C's {@code of_wipe} then {@code of_copy}.
     */
    @Test
    @DisplayName("setFlagsTo replaces and does not retain the argument")
    void setFlagsToReplaces() {
        ItemObject item = new ItemObject();
        item.setFlag(ObjectFlag.OF_FEATHER);
        Flag<ObjectFlag> replacement = new Flag<>(ObjectFlag.class);
        replacement.set(ObjectFlag.OF_BURNS_OUT);

        item.setFlagsTo(replacement);

        assertFalse(item.hasFlag(ObjectFlag.OF_FEATHER));
        assertTrue(item.hasFlag(ObjectFlag.OF_BURNS_OUT));
        replacement.set(ObjectFlag.OF_FEATHER);
        assertFalse(item.hasFlag(ObjectFlag.OF_FEATHER), "the argument must not be retained");
    }

    /**
     * Passing the item's own live set: C wipes the array and copies it onto itself, which leaves
     * it empty. The explicit wipe is what gives that result here.
     */
    @Test
    @DisplayName("setFlagsTo with the item's own live set empties it, as C does")
    void setFlagsToSelfEmpties() {
        ItemObject item = new ItemObject();
        item.setFlag(ObjectFlag.OF_FEATHER);
        item.setFlag(ObjectFlag.OF_BURNS_OUT);

        item.setFlagsTo(item.getObjectFlags());

        assertFalse(item.hasFlag(ObjectFlag.OF_FEATHER));
        assertFalse(item.hasFlag(ObjectFlag.OF_BURNS_OUT));
    }

    /**
     * A copy of the item's flags passed back in is the ordinary round trip and keeps them.
     */
    @Test
    @DisplayName("setFlagsTo with a copy of the item's own flags keeps them")
    void setFlagsToOwnCopyKeeps() {
        ItemObject item = new ItemObject();
        item.setFlag(ObjectFlag.OF_FEATHER);

        item.setFlagsTo(item.getFlags());

        assertTrue(item.hasFlag(ObjectFlag.OF_FEATHER));
    }

    /**
     * A null argument is read as an empty set.
     */
    @Test
    @DisplayName("setFlagsTo(null) empties the flags")
    void setFlagsToNull() {
        ItemObject item = new ItemObject();
        item.setFlag(ObjectFlag.OF_FEATHER);

        item.setFlagsTo(null);

        assertFalse(item.hasFlag(ObjectFlag.OF_FEATHER));
    }

    /**
     * {@code setFlags} is {@code of_union}: it adds, and reports false when the mask adds
     * nothing (an empty mask, or the item's own live set).
     */
    @Test
    @DisplayName("setFlags adds and reports novelty")
    void setFlagsAdds() {
        ItemObject item = new ItemObject();
        item.setFlag(ObjectFlag.OF_FEATHER);
        Flag<ObjectFlag> mask = new Flag<>(ObjectFlag.class);
        mask.set(ObjectFlag.OF_BURNS_OUT);

        assertTrue(item.setFlags(mask));
        assertTrue(item.hasFlag(ObjectFlag.OF_FEATHER));
        assertTrue(item.hasFlag(ObjectFlag.OF_BURNS_OUT));
        assertFalse(item.setFlags(mask), "nothing new the second time");
        assertFalse(item.setFlags(new Flag<>(ObjectFlag.class)));
        assertFalse(item.setFlags(item.getObjectFlags()), "the live set adds nothing to itself");
    }

    /**
     * {@code getObjectFlags} is the live set: a write through it reaches the item.
     */
    @Test
    @DisplayName("getObjectFlags is the live set")
    void getObjectFlagsIsLive() {
        ItemObject item = new ItemObject();
        item.getObjectFlags().set(ObjectFlag.OF_FEATHER);
        assertTrue(item.hasFlag(ObjectFlag.OF_FEATHER));
        assertSame(item.getObjectFlags(), item.getObjectFlags());
    }

    /**
     * The four notice flags are independent bits: raising one leaves the others down.
     */
    @Test
    @DisplayName("notice flags are independent")
    void noticeFlagsIndependent() {
        ItemObject item = new ItemObject();
        item.orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);

        assertTrue(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_ASSESSED));
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_WORN));
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_IGNORE));
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_IMAGINED));
    }

    /**
     * {@code modifiers[]} is zero-filled in C, so an absent entry, an absent map and the two
     * sentinels all read zero without a failure.
     */
    @Test
    @DisplayName("getModifierValue reads zero for absent, null map and sentinels")
    void modifierValueZeros() throws Exception {
        ItemObject item = new ItemObject();
        assertEquals(0, item.getModifierValue(ObjectModifier.OM_STR));
        assertEquals(0, item.getModifierValue(Stats.STAT_NONE));
        assertEquals(0, item.getModifierValue(Stats.STAT_MAX));

        item.putModifier(ObjectModifier.OM_STR, 3);
        assertEquals(3, item.getModifierValue(Stats.STAT_STR));
        assertEquals(0, item.getModifierValue(Stats.STAT_INT));

        nullField(item, "modifiers");
        assertEquals(0, item.getModifierValue(ObjectModifier.OM_STR));
        assertTrue(item.getModifiers().isEmpty());
    }

    /**
     * A negative modifier is passed through unchanged, as C stores a signed int16.
     */
    @Test
    @DisplayName("putModifier stores negative values")
    void putModifierNegative() {
        ItemObject item = new ItemObject();
        item.putModifier(ObjectModifier.OM_STEALTH, -2);
        assertEquals(-2, item.getModifierValue(ObjectModifier.OM_STEALTH));
    }

    /**
     * The map getters return the live map, so a write through it reaches the item, while a null
     * field answers an empty map that rejects writes.
     */
    @Test
    @DisplayName("getModifiers is live; a null field answers an immutable empty map")
    void modifiersLiveAndNull() throws Exception {
        ItemObject item = new ItemObject();
        item.getModifiers().put(ObjectModifier.OM_SPEED, 5);
        assertEquals(5, item.getModifierValue(ObjectModifier.OM_SPEED));

        nullField(item, "modifiers");
        nullField(item, "elInfo");
        assertTrue(item.getModifiers().isEmpty());
        assertTrue(item.getElInfo().isEmpty());
        assertThrows(UnsupportedOperationException.class,
                () -> item.getModifiers().put(ObjectModifier.OM_SPEED, 1));
    }

    /**
     * {@code setModifiers} stores the map by reference, as C's struct holds the array.
     */
    @Test
    @DisplayName("setModifiers stores the map it is given")
    void setModifiersStores() {
        ItemObject item = new ItemObject();
        Map<ObjectModifier, Integer> map = new HashMap<>();
        map.put(ObjectModifier.OM_DEX, 2);

        item.setModifiers(map);

        assertSame(map, item.getModifiers());
        assertEquals(2, item.getModifierValue(Stats.STAT_DEX));
    }

    /**
     * {@code getTime} gives out a copy and {@code setTime} stores one, so neither side can change
     * the other, as with C's {@code random_value} struct assignment.
     */
    @Test
    @DisplayName("getTime and setTime copy")
    void timeCopies() {
        ItemObject item = new ItemObject();
        uk.co.jackoftradesltd.middle.numerics.Random dice =
                uk.co.jackoftradesltd.middle.numerics.Random.parseStr("5+d10");

        item.setTime(dice);

        assertNotSame(dice, item.getTime());
        assertNotSame(item.getTime(), item.getTime());
        assertEquals(dice.toString(), item.getTime().toString());
    }

    /**
     * {@code setEffect} stores the shared list; a null becomes an empty list, so
     * {@code getEffect} is never null after it.
     */
    @Test
    @DisplayName("setEffect stores the list and turns null into empty")
    void effectStoresAndNullIsEmpty() {
        ItemObject item = new ItemObject();
        assertNotNull(item.getEffect());
        assertTrue(item.getEffect().isEmpty());

        item.setEffect(null);
        assertNotNull(item.getEffect());
        assertTrue(item.getEffect().isEmpty());

        java.util.List<uk.co.jackoftradesltd.middle.effect.Effect> list = new ArrayList<>();
        item.setEffect(list);
        assertSame(list, item.getEffect());
    }

    /**
     * {@code effect_msg} is a shared string pointer: stored as given, null included, carried by a
     * copy, and reset by a wipe.
     */
    @Test
    @DisplayName("setEffectMessage stores, copies across and wipes")
    void effectMessage() throws Exception {
        ItemObject item = new ItemObject();
        assertNull(field(item, "effectMessage"));

        item.setEffectMessage("You feel a malevolent aura.");
        assertEquals("You feel a malevolent aura.", field(item, "effectMessage"));
        assertEquals("You feel a malevolent aura.", field(item.copy(false), "effectMessage"));

        item.setEffectMessage(null);
        assertNull(field(item, "effectMessage"));

        item.setEffectMessage("again");
        item.wipe();
        assertNull(field(item, "effectMessage"));
    }

    /**
     * {@code isArtifact} tests the item's own artifact pointer against NULL. A bare item has none.
     */
    @Test
    @DisplayName("isArtifact is false for a bare item")
    void bareItemNotArtifact() {
        assertFalse(new ItemObject().isArtifact());
    }

    /**
     * The known half is a separate item: attaching it links one way only, and attaching null
     * detaches it, as C's pointer assignment does.
     */
    @Test
    @DisplayName("setKnown links one way and null detaches")
    void knownLinksOneWay() {
        ItemObject item = new ItemObject();
        ItemObject known = new ItemObject();

        item.setKnown(known);
        assertSame(known, item.getKnown());
        assertNull(known.getKnown());

        item.setKnown(null);
        assertNull(item.getKnown());
    }

    /**
     * The note, ego, kind and owning pile are plain reference fields: stored as given, null
     * included.
     */
    @Test
    @DisplayName("reference fields store what they are given")
    void referenceFields() {
        ItemObject item = new ItemObject();
        item.setNote("@w1");
        assertEquals("@w1", item.getNote());
        item.setNote(null);
        assertNull(item.getNote());

        assertNull(item.getEgo());
        item.setEgo(null);
        assertNull(item.getEgo());
        assertNull(item.getOwningPile());
        item.setOwningPile(null);
        assertNull(item.getOwningPile());

        item.setMimickingMIndex(3);
        assertEquals(3, item.getMimickingMIndex());
    }
}
