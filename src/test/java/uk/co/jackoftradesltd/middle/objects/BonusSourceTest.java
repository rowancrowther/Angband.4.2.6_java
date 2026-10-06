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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.cave.Loc;

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
 * reads it in {@code calc_bonuses()} ({@code player-calcs.c}). The port cannot rebind one variable across
 * two unrelated types — an {@link ItemObject} and a {@link Curse} — so the two passes became two classes. Everything that could go
 * wrong in that translation is invisible at the call site — the loop body compiles and runs
 * whichever implementation it is handed, and a wrong constant simply produces a slightly wrong
 * character.
 *
 * <p>The curse side is where the risk lives. A curse is read through its own curse object
 * ({@code curse->obj} in C), and two of its answers are constants because that object never
 * carries them: base armour class is zero and the digger test is {@code false}. Every {@code known*}
 * answer is <em>data</em>, read from the curse object's known counterpart, which
 * {@code player_know_object()} fills from the player's rune knowledge exactly as it does for an
 * item ({@code write_curse_kinds()} in {@code obj-init.c} marks it assessed so that it can be
 * fully known). Only a curse with no known counterpart at all, which the port tolerates and C
 * cannot reach, answers zero and empty. If someone later "fixes" {@link CurseSource#knownToAC()} to
 * return a constant, a curse's learned armour bonus would vanish under {@code knownOnly}, and
 * only these tests would say so.
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
        Flag<ObjectFlag> flagSet = new Flag<>(ObjectFlag.class);
        if (!flags.isEmpty()) flagSet.set(flags);
        ItemObject curseObject = new ItemObject();
        curseObject.setFlagsTo(flagSet);
        curseObject.setModifiers(modifiers);
        curseObject.setElInfo(elInfo);
        curseObject.setToHit(toHit);
        curseObject.setToDam(toDam);
        curseObject.setToAC(toAc);
        return new Curse("test curse", List.of(), curseObject, List.of(), new Flag<>(ObjectFlag.class), "", 0);
    }

    /**
     * A curse whose curse object has a known counterpart, as {@code write_curse_kinds()} leaves it
     * in C. The known object is filled by hand with the values {@code player_know_object()} would
     * have written, so each test states which runes the player is imagined to know.
     *
     * @param flags      the curse object's flags
     * @param knownFlags the flags the player has learned
     * @param modifiers  the curse object's modifiers
     * @param elInfo     the curse object's per-element resistances
     * @param toHit      the curse's real to-hit
     * @param toDam      the curse's real to-damage
     * @param toAc       the curse's real to-armour
     * @param knownToHit the to-hit the player has learned, zero if the rune is unknown
     * @param knownToDam the to-damage the player has learned
     * @param knownToAc  the to-armour the player has learned
     * @return the curse
     */
    private static Curse curseWithKnown(List<ObjectFlag> flags, List<ObjectFlag> knownFlags,
                                        Map<ObjectModifier, Integer> modifiers,
                                        Map<ElementEnum, ElementInfo> elInfo,
                                        int toHit, int toDam, int toAc,
                                        int knownToHit, int knownToDam, int knownToAc) {
        return curseWithKnown(flags, knownFlags, modifiers, elInfo, toHit, toDam, toAc,
                knownToHit, knownToDam, knownToAc, Map.of());
    }

    /**
     * As the shorter overload, with the known object's element info given too.
     *
     * @param knownElInfo the per-element levels the player has learned
     * @return the curse
     * @see #curseWithKnown(List, List, Map, Map, int, int, int, int, int, int)
     */
    private static Curse curseWithKnown(List<ObjectFlag> flags, List<ObjectFlag> knownFlags,
                                        Map<ObjectModifier, Integer> modifiers,
                                        Map<ElementEnum, ElementInfo> elInfo,
                                        int toHit, int toDam, int toAc,
                                        int knownToHit, int knownToDam, int knownToAc,
                                        Map<ElementEnum, ElementInfo> knownElInfo) {
        Curse curse = curse(flags, modifiers, elInfo, toHit, toDam, toAc);
        Flag<ObjectFlag> knownFlagSet = new Flag<>(ObjectFlag.class);
        if (!knownFlags.isEmpty()) knownFlagSet.set(knownFlags);
        ItemObject known = new ItemObject();
        known.setFlagsTo(knownFlagSet);
        known.setElInfo(knownElInfo);
        known.setToHit(knownToHit);
        known.setToDam(knownToDam);
        known.setToAC(knownToAc);
        curse.getItemObject().setKnown(known);
        return curse;
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
         * that order. Three distinct values, because a transposition
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
         * A curse object with no known counterpart answers zero for every {@code known*} value and
         * an empty flag set. C has no such case — {@code write_curse_kinds()} allocates a known
         * object for every curse — so this pins the port's guard only, and says nothing about what a
         * player who has learned the runes sees; the tests below cover that.
         */
        @Test
        @DisplayName("a curse object with no known counterpart answers zero and empty")
        void noKnownCounterpart() {
            BonusSource source = new CurseSource(curse(List.of(ObjectFlag.OF_AGGRAVATE), Map.of(),
                    Map.of(ElementEnum.ELEM_FIRE, res(2)), 1, 2, 3));

            assertAll(
                    () -> assertEquals(0, source.knownToAC()),
                    () -> assertEquals(0, source.knownToHit()),
                    () -> assertEquals(0, source.knownToDam()),
                    () -> assertEquals(0, source.knownResLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(source.flagsKnown().isEmpty()));
        }

        /**
         * A learned rune makes the known counterpart carry the curse's value. C's
         * {@code player_know_object()} sets {@code known->to_a/to_h/to_d} to the object's own figure
         * when the player knows the rune, so the {@code known*} accessors must report it. The three
         * figures are distinct, so a transposition between them cannot pass.
         */
        @Test
        @DisplayName("known combat values read through from the known counterpart")
        void knownCombatValuesLearned() {
            BonusSource source = new CurseSource(curseWithKnown(
                    List.of(), List.of(), Map.of(), Map.of(), 4, 5, 6, 4, 5, 6));

            assertAll(
                    () -> assertEquals(6, source.knownToAC()),
                    () -> assertEquals(4, source.knownToHit()),
                    () -> assertEquals(5, source.knownToDam()));
        }

        /**
         * An unlearned rune leaves the known figure at zero while the real one stays put. C writes
         * {@code p->obj_k->to_a * obj->to_a}, which is zero until the rune is known, and the caller's
         * {@code !known_only || obj->known->to_a} test then drops the bonus. The real and known
         * values must stay independent for that gate to mean anything.
         */
        @Test
        @DisplayName("an unlearned combat rune leaves the known value zero and the real one intact")
        void knownCombatValuesNotLearned() {
            BonusSource source = new CurseSource(curseWithKnown(
                    List.of(), List.of(), Map.of(), Map.of(), 4, 5, 6, 0, 0, 0));

            assertAll(
                    () -> assertEquals(4, source.toHit()),
                    () -> assertEquals(5, source.toDam()),
                    () -> assertEquals(6, source.toAC()),
                    () -> assertEquals(0, source.knownToAC()),
                    () -> assertEquals(0, source.knownToHit()),
                    () -> assertEquals(0, source.knownToDam()));
        }

        /**
         * A known element entry is reported, and an element the known object says nothing about is
         * zero. C copies an element's level into {@code known->el_info} only where the player knows
         * that element's rune and writes zero elsewhere, so the real level (here vulnerable) and the
         * known level (here unlearned) can differ for the same element.
         */
        @Test
        @DisplayName("known resistance levels read from the known counterpart only")
        void knownResistance() {
            BonusSource source = new CurseSource(curseWithKnown(
                    List.of(), List.of(), Map.of(),
                    Map.of(ElementEnum.ELEM_FIRE, res(-1), ElementEnum.ELEM_COLD, res(1)),
                    0, 0, 0, 0, 0, 0,
                    Map.of(ElementEnum.ELEM_FIRE, res(-1))));

            assertAll(
                    () -> assertEquals(-1, source.knownResLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertEquals(0, source.knownResLevel(ElementEnum.ELEM_COLD),
                            "the curse resists cold but the player has not learned it"),
                    () -> assertEquals(1, source.resLevel(ElementEnum.ELEM_COLD)));
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
         * gated on the player's rune knowledge in the caller, not on anything held by the curse.
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
         * {@code flagsKnown} answers with the known counterpart's flags and not the curse's own. C's
         * {@code object_flags_known()} intersects the object's flags with the known flags and the
         * curse kind adds none back, so a flag the curse carries but the player has not learned
         * must be absent, and the caller must be handed a copy it can change freely.
         */
        @Test
        @DisplayName("flagsKnown returns the learned flags only, as a detached copy")
        void flagsKnownIsLearnedSubset() {
            Curse curse = curseWithKnown(List.of(ObjectFlag.OF_AGGRAVATE, ObjectFlag.OF_SEE_INVIS),
                    List.of(ObjectFlag.OF_AGGRAVATE), Map.of(), Map.of(), 0, 0, 0, 0, 0, 0);
            BonusSource source = new CurseSource(curse);

            Flag<ObjectFlag> first = source.flagsKnown();
            first.on(ObjectFlag.OF_FREE_ACT);

            assertAll(
                    () -> assertTrue(source.flagsKnown().has(ObjectFlag.OF_AGGRAVATE)),
                    () -> assertFalse(source.flagsKnown().has(ObjectFlag.OF_SEE_INVIS),
                            "carried by the curse but not yet learned"),
                    () -> assertFalse(source.flagsKnown().has(ObjectFlag.OF_FREE_ACT),
                            "the caller's edit must not reach the known object"),
                    () -> assertNotSame(first, source.flagsKnown()));
        }

        /**
         * {@code flagsKnown} must hand back a fresh empty set rather than do nothing at all when the
         * curse object has no known counterpart. The distinction matters because
         * {@code calcBonuses} keeps one flag variable across the passes of a slot: a no-op would leave
         * the previous source's flags standing and union them into the total a second time.
         */
        @Test
        @DisplayName("flagsKnown with no known counterpart is a fresh empty set")
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
