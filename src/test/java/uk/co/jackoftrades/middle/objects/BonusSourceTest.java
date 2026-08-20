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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.cave.Loc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link BonusSource} through both its implementations — {@link ItemSource} and
 * {@link CurseSource} — the abstraction {@code calcBonuses} walks its equipment loop over.
 *
 * <p><b>Why this needs a test class at all.</b> The interface has no counterpart in C: there, one
 * pointer is rebound from the slot's item to each curse's template object and a single loop body
 * reads it ({@code player-calcs.c:1929-2020}). The port cannot do that, because a {@link Curse}
 * carries no object of its own, so the two passes became two classes. Everything that could go
 * wrong in that translation is invisible at the call site — the loop body compiles and runs
 * whichever implementation it is handed, and a wrong constant simply produces a slightly wrong
 * character.
 *
 * <p>The curse side is where the risk lives, and its answers look like stubs. A curse's template
 * object is built with the {@code <curse object>} kind and a blank known counterpart
 * ({@code obj-init.c:175-195}), so its base armour class, its digger test and every {@code known*}
 * value are constants rather than data. Those constants are C's behaviour, and the tests below pin
 * them as such: their consequence is that a curse under {@code knownOnly} contributes its modifiers
 * and nothing else. If someone later "fixes" {@link CurseSource#knownToAC()} to return the curse's
 * real armour bonus, a cursed item would start showing the player a bonus the original never did,
 * and only these tests would say so.
 *
 * <p>Class BonusSourceTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class BonusSourceTest {

    /**
     * Builds an item with a known counterpart, so both halves of every {@code known*} pair can be
     * set independently — which is the only way to tell a real value from a learned one.
     *
     * @param tValue the item's type, which decides the digger test
     * @param known  the item's known counterpart, or {@code null} for an item nothing is known about
     * @return a bare item with no flags, modifiers, elements or curses
     */
    private static ItemObject item(TValue tValue, ItemObject known) {
        ItemObject item = new ItemObject(new ObjectKind(), null, null, known, Loc.zero, tValue, 0,
                "0", 0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(), Set.of(), Set.of(),
                new LinkedHashMap<>(), List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, "");
        item.settValue(tValue);
        return item;
    }

    /**
     * A counterpart object — an item in its own right, used only as the "what the player knows"
     * half of a pair.
     *
     * @return a bare item with empty flags, modifiers and element info
     */
    private static ItemObject knownObject() {
        return item(TValue.TV_SWORD, null);
    }

    /**
     * A curse with the fields the bonus source reads and nothing else.
     *
     * @param flags     the curse's object flags
     * @param modifiers the curse's modifiers
     * @param elInfo    the curse's per-element resistances
     * @param toHit     the first field of the {@code combat:} line
     * @param toDam     the second
     * @param toAc      the third
     * @return the curse
     */
    private static Curse curse(List<ObjectFlag> flags, Map<ObjectModifier, Integer> modifiers,
                               Map<ElementEnum, ElementInfo> elInfo, int toHit, int toDam, int toAc) {
        return new Curse("test curse", List.of(), 0, null, flags, modifiers, elInfo,
                toHit, toDam, toAc, List.of(), List.of(), "", "");
    }

    /**
     * An element info at a given resistance level.
     *
     * @param level the level: {@code -1} vulnerable, {@code 0} neutral, higher resistant
     * @return the element info
     */
    private static ElementInfo res(int level) {
        ElementInfo info = new ElementInfo();
        info.setResLevel(level);
        return info;
    }

    /**
     * The item pass — the first iteration of C's {@code while (obj)}, where the source is the worn
     * item itself.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("ItemSource")
    class ItemSourceTests {

        /**
         * The four combat numbers and the base armour class come straight off the item. Base armour
         * and the armour bonus are separate quantities in C — {@code obj->ac} is added
         * unconditionally and {@code obj->to_a} only when known — and the port kept them apart, so
         * the test reads them apart.
         */
        @Test
        @DisplayName("combat values come from the item")
        void combatValues() {
            ItemObject item = item(TValue.TV_SWORD, null);
            item.setBaseAC(12);
            item.setToAC(3);
            item.setToHit(5);
            item.setToDam(7);
            BonusSource source = new ItemSource(item);

            assertAll(
                    () -> assertEquals(12, source.baseAC()),
                    () -> assertEquals(3, source.toAC()),
                    () -> assertEquals(5, source.toHit()),
                    () -> assertEquals(7, source.toDam()));
        }

        /**
         * The {@code known*} family reads the counterpart, not the item. Setting the two halves to
         * different numbers is the whole point: a test that gave them the same value would pass
         * whichever object the accessor actually consulted.
         */
        @Test
        @DisplayName("known combat values come from the known counterpart, not the item")
        void knownCombatValues() {
            ItemObject known = knownObject();
            known.setToAC(1);
            known.setToHit(2);
            known.setToDam(3);
            ItemObject item = item(TValue.TV_SWORD, known);
            item.setToAC(30);
            item.setToHit(40);
            item.setToDam(50);
            BonusSource source = new ItemSource(item);

            assertAll(
                    () -> assertEquals(1, source.knownToAC()),
                    () -> assertEquals(2, source.knownToHit()),
                    () -> assertEquals(3, source.knownToDam()));
        }

        /**
         * An item with no counterpart answers zero rather than throwing. C never reaches this state
         * — it dereferences {@code obj->known} unguarded — so the guard is the port's, and zero is
         * the right answer for it: nothing is known.
         */
        @Test
        @DisplayName("an item with no known counterpart knows nothing rather than throwing")
        void noKnownCounterpart() {
            ItemObject item = item(TValue.TV_SWORD, null);
            item.setToAC(30);
            item.setToHit(40);
            item.setToDam(50);
            item.putElInfo(ElementEnum.ELEM_FIRE, res(2));
            BonusSource source = new ItemSource(item);

            assertAll(
                    () -> assertEquals(0, source.knownToAC()),
                    () -> assertEquals(0, source.knownToHit()),
                    () -> assertEquals(0, source.knownToDam()),
                    () -> assertEquals(0, source.knownResLevel(ElementEnum.ELEM_FIRE)));
        }

        /**
         * Modifiers come off the item raw. The player's rune knowledge is deliberately not applied
         * here — C gates on {@code p->obj_k}, which belongs to the player and stays in
         * {@code calcBonuses} — so a modifier the player has never learned still reads at full value
         * through this accessor.
         */
        @Test
        @DisplayName("modifiers are returned raw, ungated by knowledge")
        void modifiersAreRaw() {
            ItemObject item = item(TValue.TV_SWORD, null);
            item.setModifiers(Map.of(ObjectModifier.OM_SPEED, 5));
            BonusSource source = new ItemSource(item);

            assertAll(
                    () -> assertEquals(5, source.modifier(ObjectModifier.OM_SPEED)),
                    () -> assertEquals(0, source.modifier(ObjectModifier.OM_STR)));
        }

        /**
         * An element the item says nothing about reads as neutral, matching the zeroed array C
         * subscribes into. The vulnerability value is carried through unchanged rather than clamped,
         * because {@code calcBonuses} needs to recognise the {@code -1} to remember it for later.
         */
        @Test
        @DisplayName("resistance levels pass through, including vulnerability")
        void resistanceLevels() {
            ItemObject item = item(TValue.TV_SWORD, null);
            item.putElInfo(ElementEnum.ELEM_FIRE, res(2));
            item.putElInfo(ElementEnum.ELEM_COLD, res(-1));
            BonusSource source = new ItemSource(item);

            assertAll(
                    () -> assertEquals(2, source.resLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertEquals(-1, source.resLevel(ElementEnum.ELEM_COLD)),
                    () -> assertEquals(0, source.resLevel(ElementEnum.ELEM_ACID)));
        }

        /**
         * The digger test is the item's tval, and a tval-less item is not a digger. C compares an
         * integer field that is always set, so the null case exists only in the port — and it has to
         * answer {@code false} rather than throw, because {@code calcBonuses} asks it of every worn
         * item.
         */
        @Test
        @DisplayName("only a digging tval is a digger, and a missing tval is not")
        void diggerTest() {
            assertAll(
                    () -> assertTrue(new ItemSource(item(TValue.TV_DIGGING, null)).isDigger()),
                    () -> assertFalse(new ItemSource(item(TValue.TV_SWORD, null)).isDigger()),
                    () -> assertFalse(new ItemSource(item(null, null)).isDigger()));
        }

        /**
         * {@code flagSet} asks the item's real flags, with no knowledge test, because
         * {@code calcBonuses} reads the {@code OF_DIG_*} flags raw
         * ({@code player-calcs.c:1959-1966}). The name sits beside the {@code known*} family and
         * means something different, which is exactly why it is worth pinning.
         */
        @Test
        @DisplayName("flagSet tests the real flags, not the known ones")
        void flagSetIgnoresKnowledge() {
            ItemObject known = knownObject();
            ItemObject item = item(TValue.TV_DIGGING, known);
            item.setFlag(ObjectFlag.OF_DIG_2);
            BonusSource source = new ItemSource(item);

            assertAll(
                    () -> assertTrue(source.flagSet(ObjectFlag.OF_DIG_2)),
                    () -> assertFalse(source.flagSet(ObjectFlag.OF_DIG_3)),
                    () -> assertFalse(known.hasFlag(ObjectFlag.OF_DIG_2),
                            "the known counterpart was never told, which is the point"));
        }

        /**
         * The flag set handed out is the caller's to keep. {@code calcBonuses} unions it into a
         * running set and reuses one variable across passes, so a source that handed back its own
         * storage would let the loop corrupt the item it is reading.
         */
        @Test
        @DisplayName("flags() hands out a set the caller may keep")
        void flagsAreDetached() {
            ItemObject item = item(TValue.TV_SWORD, null);
            item.setFlag(ObjectFlag.OF_FEATHER);
            BonusSource source = new ItemSource(item);

            Flag<ObjectFlag> first = source.flags();
            first.on(ObjectFlag.OF_SEE_INVIS);

            assertAll(
                    () -> assertNotSame(first, source.flags()),
                    () -> assertFalse(item.hasFlag(ObjectFlag.OF_SEE_INVIS),
                            "writing to the returned set must not reach the item"),
                    () -> assertTrue(source.flags().has(ObjectFlag.OF_FEATHER)));
        }

        /**
         * {@code flagsKnown} is the intersection of the item's flags with the counterpart's, so a
         * flag the item has but the player has not learned drops out — which is the whole of what
         * {@code knownOnly} buys.
         */
        @Test
        @DisplayName("flagsKnown keeps only the flags the counterpart also has")
        void flagsKnownIntersects() {
            ItemObject known = knownObject();
            known.setFlag(ObjectFlag.OF_FEATHER);
            ItemObject item = item(TValue.TV_SWORD, known);
            item.setFlag(ObjectFlag.OF_FEATHER);
            item.setFlag(ObjectFlag.OF_SEE_INVIS);
            BonusSource source = new ItemSource(item);

            Flag<ObjectFlag> flags = source.flagsKnown();

            assertAll(
                    () -> assertTrue(flags.has(ObjectFlag.OF_FEATHER)),
                    () -> assertFalse(flags.has(ObjectFlag.OF_SEE_INVIS)));
        }
    }

    /**
     * The curse passes — the later iterations of C's {@code while (obj)}, where the source is a
     * curse's shared template object rather than the item.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("CurseSource")
    class CurseSourceTests {

        /**
         * The three combat numbers are the three fields of the curse's {@code combat:} line, in
         * that order ({@code obj-init.c:1089-1091}). Three distinct values, because a transposition
         * between to-hit and to-damage would pass any test that used the same number twice.
         */
        @Test
        @DisplayName("combat values come from the curse's combat line, in order")
        void combatValues() {
            BonusSource source = new CurseSource(curse(List.of(), Map.of(), Map.of(), 1, 2, 3));

            assertAll(
                    () -> assertEquals(1, source.toHit()),
                    () -> assertEquals(2, source.toDam()),
                    () -> assertEquals(3, source.toAC()));
        }

        /**
         * Every {@code known*} value is zero, whatever the curse actually carries. This is not a
         * gap: the curse template's known object is allocated blank and never filled in
         * ({@code obj-init.c:188-194}), so under {@code knownOnly} the guards in
         * {@code calcBonuses} drop a curse's combat bonuses and resistances entirely.
         */
        @Test
        @DisplayName("nothing about a curse is ever known")
        void nothingIsKnown() {
            BonusSource source = new CurseSource(curse(List.of(), Map.of(),
                    Map.of(ElementEnum.ELEM_FIRE, res(2)), 1, 2, 3));

            assertAll(
                    () -> assertEquals(0, source.knownToAC()),
                    () -> assertEquals(0, source.knownToHit()),
                    () -> assertEquals(0, source.knownToDam()),
                    () -> assertEquals(0, source.knownResLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(source.flagsKnown().isEmpty()));
        }

        /**
         * A curse adds no base armour class, however heavy its {@code combat:} line. The template
         * object's kind is {@code <curse object>}, which has no armour — so {@code toAC} can be
         * non-zero while {@code baseAC} stays flat at zero, and the pair have to be checked
         * together to show they are genuinely different quantities.
         */
        @Test
        @DisplayName("a curse never contributes base armour class")
        void noBaseArmour() {
            BonusSource source = new CurseSource(curse(List.of(), Map.of(), Map.of(), 0, 0, 9));

            assertAll(
                    () -> assertEquals(0, source.baseAC()),
                    () -> assertEquals(9, source.toAC()));
        }

        /**
         * Modifiers are the one thing a curse really contributes on every pass, because they are
         * gated on the player's rune knowledge rather than on the blank known object.
         */
        @Test
        @DisplayName("modifiers are real data")
        void modifiers() {
            BonusSource source = new CurseSource(curse(List.of(),
                    Map.of(ObjectModifier.OM_SPEED, -5), Map.of(), 0, 0, 0));

            assertAll(
                    () -> assertEquals(-5, source.modifier(ObjectModifier.OM_SPEED)),
                    () -> assertEquals(0, source.modifier(ObjectModifier.OM_STR)));
        }

        /**
         * Resistances read through, and an element the curse says nothing about is neutral. They are
         * reachable only when {@code knownOnly} is clear, since {@link BonusSource#knownResLevel} is
         * flatly zero — but the value has to be right for the case that does reach it.
         */
        @Test
        @DisplayName("resistance levels pass through")
        void resistanceLevels() {
            BonusSource source = new CurseSource(curse(List.of(), Map.of(),
                    Map.of(ElementEnum.ELEM_FIRE, res(-1)), 0, 0, 0));

            assertAll(
                    () -> assertEquals(-1, source.resLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertEquals(0, source.resLevel(ElementEnum.ELEM_ACID)));
        }

        /**
         * A curse is never a digger — the template's tval is {@code none} — so the {@code OF_DIG_*}
         * flags are never read from one, even for a curse that names them.
         */
        @Test
        @DisplayName("a curse is never a digger")
        void neverADigger() {
            BonusSource source = new CurseSource(
                    curse(List.of(ObjectFlag.OF_DIG_3), Map.of(), Map.of(), 0, 0, 0));

            assertAll(
                    () -> assertFalse(source.isDigger()),
                    () -> assertTrue(source.flagSet(ObjectFlag.OF_DIG_3),
                            "the flag is there; it is the digger test that keeps it unread"));
        }

        /**
         * The curse's own flags become a real flag set, freshly built each call so the caller may
         * keep it — the same contract the item side honours.
         */
        @Test
        @DisplayName("flags() builds a detached set from the curse's flag list")
        void flagsAreDetached() {
            BonusSource source = new CurseSource(
                    curse(List.of(ObjectFlag.OF_AGGRAVATE), Map.of(), Map.of(), 0, 0, 0));

            Flag<ObjectFlag> first = source.flags();
            first.on(ObjectFlag.OF_SEE_INVIS);

            assertAll(
                    () -> assertTrue(source.flags().has(ObjectFlag.OF_AGGRAVATE)),
                    () -> assertFalse(source.flags().has(ObjectFlag.OF_SEE_INVIS)),
                    () -> assertNotSame(first, source.flags()));
        }

        /**
         * {@code flagsKnown} must hand back an empty set rather than do nothing at all. The
         * distinction matters because {@code calcBonuses} keeps one flag variable across the passes
         * of a slot: a no-op would leave the previous source's flags standing and union them into
         * the total a second time.
         */
        @Test
        @DisplayName("flagsKnown returns a fresh empty set, not the caller's previous one")
        void flagsKnownIsFreshAndEmpty() {
            BonusSource source = new CurseSource(
                    curse(List.of(ObjectFlag.OF_AGGRAVATE), Map.of(), Map.of(), 0, 0, 0));

            Flag<ObjectFlag> first = source.flagsKnown();
            first.on(ObjectFlag.OF_SEE_INVIS);

            assertAll(
                    () -> assertTrue(source.flagsKnown().isEmpty()),
                    () -> assertNotSame(first, source.flagsKnown()));
        }
    }
}
