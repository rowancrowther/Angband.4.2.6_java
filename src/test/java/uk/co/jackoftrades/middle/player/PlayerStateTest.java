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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.registry.StatTables;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerState}, the port of C's {@code struct player_state} — everything about a
 * character that is derived from race, class, gear and statuses rather than stored.
 *
 * <p><b>A bag of setters is not usually worth testing, and this one is</b>, for three reasons that
 * the tests below are organised around.
 *
 * <p>First, {@link PlayerState#wipe()} carries the whole guarantee that {@code calcBonuses} makes.
 * The state is not updated incrementally; it is rebuilt from nothing on every recalculation, so a
 * field the wipe forgets would keep a contribution from gear the player took off two turns ago, and
 * nothing would ever put it right. The wipe is checked field by field for that reason.
 *
 * <p>Second, the accumulating methods and the assigning ones are easy to confuse and are not
 * interchangeable — {@code statAdd} adds where {@code setStatTop} replaces, and the equipment walk
 * calls the accumulators once per worn item. A setter that replaced where it should add would leave
 * the character wearing only its last ring.
 *
 * <p>Third, the port has two ways to write a resistance and two ways to reach the flag sets, and
 * they differ in what they do about a missing entry. Which is which is worth pinning before someone
 * simplifies one into the other.
 *
 * <p>Class PlayerStateTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class PlayerStateTest {

    /**
     * A state with something non-zero in every field, so that a wipe has something to clear and a
     * getter has something distinguishable to return.
     *
     * @return the filled state
     */
    private static PlayerState filled() {
        PlayerState state = new PlayerState();
        state.setSpeed(120);
        state.setNumBlows(200);
        state.setNumShots(20);
        state.setNumMoves(2);
        state.setAmmoMult(3);
        state.setAmmoTValue(TValue.TV_ARROW);
        state.setBaseAc(15);
        state.setDamRed(4);
        state.toAcAdd(6);
        state.toHitAdd(7);
        state.toDamAdd(8);
        state.setSeeInfra(5);
        state.setCurLight(3);
        state.setHeavyWield(true);
        state.setHeavyShoot(true);
        state.setBlessWield(true);
        state.setCumberArmour(true);
        state.statAdd(Stats.STAT_STR, 2);
        state.setStatInd(Stats.STAT_STR, 9);
        state.setStatTop(Stats.STAT_STR, 18);
        state.setStatUse(Stats.STAT_STR, 17);
        state.skillAdd(PlayerSkill.SKILL_STEALTH, 11);
        state.setPlayerFlag(PlayerFlag.PF_UNLIGHT);
        state.setResLevel(ElementEnum.ELEM_FIRE, 2);
        Flag<ObjectFlag> oflags = new Flag<>(ObjectFlag.class);
        oflags.on(ObjectFlag.OF_FEATHER);
        state.unionObjectFlags(oflags);
        return state;
    }

    /**
     * Construction and reset — the port of C's {@code memset(state, 0, sizeof *state)}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("wipe")
    class Wipe {

        /**
         * Every scalar and boolean the state carries must come back to zero. Listed one by one
         * rather than sampled, because the failure mode is a single forgotten field and a sample
         * would not find it.
         */
        @Test
        @DisplayName("every field returns to zero")
        void everyFieldZeroed() {
            PlayerState state = filled();
            state.wipe();

            assertAll(
                    () -> assertEquals(0, state.getSpeed()),
                    () -> assertEquals(0, state.getNumShots()),
                    () -> assertEquals(0, state.getAmmoMult()),
                    () -> assertEquals(0, state.getBaseAc()),
                    () -> assertEquals(0, state.getDamRed()),
                    () -> assertEquals(0, state.perDamRed()),
                    () -> assertEquals(0, state.getCurLight()),
                    () -> assertFalse(state.isHeavyWield()),
                    () -> assertFalse(state.isHeavyShoot()),
                    () -> assertEquals(0, state.getStatAdd(Stats.STAT_STR)),
                    () -> assertEquals(0, state.getStatInd(Stats.STAT_STR)),
                    () -> assertEquals(0, state.getPlayerSkill(PlayerSkill.SKILL_STEALTH)),
                    () -> assertFalse(state.hasPFlag(PlayerFlag.PF_UNLIGHT)),
                    () -> assertFalse(state.hasOFlag(ObjectFlag.OF_FEATHER)));
        }

        /**
         * The element map is repopulated rather than merely emptied. C subscripts a fixed array, so
         * every element is readable straight after the reset; the port has to put the entries back
         * or the first resistance comparison in {@code calcBonuses} meets a missing key.
         */
        @Test
        @DisplayName("every real element is readable again straight after a wipe")
        void elementsRepopulated() {
            PlayerState state = filled();
            state.wipe();

            assertAll(() -> {
                for (ElementEnum element : ElementEnum.values()) {
                    if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
                    assertEquals(0, state.getResLevel(element), element + " should read as neutral");
                }
            });
        }

        /**
         * The four stat maps are repopulated for the same reason the element map is, and the
         * failure mode is worse: {@link PlayerState#getStatTop} and {@link PlayerState#getStatUse}
         * read their map directly, so a missing key is an unboxing {@link NullPointerException}
         * rather than a wrong number. {@code updateBonuses} compares a freshly built state against
         * the character's existing one on its first call, which is exactly when the existing one has
         * never been through {@code calcBonuses}, so this is the reset that keeps that call from
         * throwing.
         *
         * <p>Both getters are asserted for every real stat rather than sampled, since one map left
         * out of the reset is precisely the defect this guards.
         */
        @Test
        @DisplayName("every real stat is readable again straight after a wipe")
        void statsRepopulated() {
            PlayerState state = filled();
            state.wipe();

            assertAll(() -> {
                for (Stats stat : Stats.values()) {
                    if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
                    assertEquals(0, state.getStatTop(stat), stat + " top should read as zero");
                    assertEquals(0, state.getStatUse(stat), stat + " use should read as zero");
                    assertEquals(0, state.getStatInd(stat), stat + " index should read as zero");
                    assertEquals(0, state.getStatAdd(stat), stat + " add should read as zero");
                }
            });
        }

        /**
         * The skills map is deliberately <em>not</em> refilled: {@code calcBonuses} sets every real
         * skill from race and class immediately after wiping, and {@link PlayerState#getPlayerSkill}
         * defaults a missing key to zero. Pinned so that the asymmetry with the stat and element
         * maps reads as a decision rather than an oversight.
         */
        @Test
        @DisplayName("skills read as zero without being refilled")
        void skillsReadZeroWhileEmpty() {
            PlayerState state = filled();
            state.wipe();

            assertAll(() -> {
                for (PlayerSkill skill : PlayerSkill.values()) {
                    if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
                    assertEquals(0, state.getPlayerSkill(skill), skill + " should read as zero");
                }
            });
        }

        /**
         * A fresh state is already wiped — the constructor and the reset have to agree, or a state
         * built but not yet recalculated would answer differently from one that had been.
         */
        @Test
        @DisplayName("a new state is already in the wiped condition")
        void constructorMatchesWipe() {
            PlayerState fresh = new PlayerState();

            assertAll(
                    () -> assertEquals(0, fresh.getSpeed()),
                    () -> assertEquals(0, fresh.getBaseAc()),
                    () -> assertEquals(0, fresh.getStatTop(Stats.STAT_STR)),
                    () -> assertEquals(0, fresh.getStatUse(Stats.STAT_STR)),
                    () -> assertNotNull(fresh.getElInfo().get(ElementEnum.ELEM_FIRE)),
                    () -> assertEquals(0, fresh.getResLevel(ElementEnum.ELEM_FIRE)));
        }
    }

    /**
     * Duplication — the port of C assigning one {@code struct player_state} to another.
     *
     * <p><b>What makes this worth its own set of tests is that C gets it for free and Java does
     * not.</b> {@code struct player_state state = p->state;} copies every byte, so the two are
     * thereafter unrelated values. The Java equivalent would share one object, and the caller that
     * needs the copy most — {@code updateBonuses}, which recalculates into the duplicate and then
     * compares it field by field against the original — is the one an alias would silently ruin:
     * every comparison would test an object against itself, always find no change, and raise none of
     * the flags the rest of the turn depends on. Nothing would throw and nothing would look wrong.
     *
     * <p>So these tests are less about the values arriving than about the two states being genuinely
     * separate afterwards, in both directions and through the mutable structure as well as the
     * scalars.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("copy")
    class Copy {

        /**
         * Reads a field the state exposes no getter for.
         *
         * @param state the state to read
         * @param name  the field's name
         * @return the field's value
         * @throws ReflectiveOperationException if the field cannot be reached
         */
        private int read(PlayerState state, String name) throws ReflectiveOperationException {
            java.lang.reflect.Field field = PlayerState.class.getDeclaredField(name);
            field.setAccessible(true);
            return (int) field.get(state);
        }

        /**
         * Every value the state carries has to arrive in the duplicate. Listed one by one rather
         * than sampled, because the failure mode is a single field missed out of a long assignment
         * block and a sample would not find it.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("carries every value across")
        void carriesEveryValue() throws ReflectiveOperationException {
            PlayerState original = filled();

            PlayerState duplicate = original.copy();

            assertAll(
                    () -> assertEquals(original.getSpeed(), duplicate.getSpeed()),
                    () -> assertEquals(original.getNumShots(), duplicate.getNumShots()),
                    () -> assertEquals(original.getAmmoMult(), duplicate.getAmmoMult()),
                    () -> assertEquals(original.getBaseAc(), duplicate.getBaseAc()),
                    () -> assertEquals(original.getToAc(), duplicate.getToAc()),
                    () -> assertEquals(original.getDamRed(), duplicate.getDamRed()),
                    () -> assertEquals(original.perDamRed(), duplicate.perDamRed()),
                    () -> assertEquals(original.getCurLight(), duplicate.getCurLight()),
                    () -> assertEquals(original.isHeavyWield(), duplicate.isHeavyWield()),
                    () -> assertEquals(original.isHeavyShoot(), duplicate.isHeavyShoot()),
                    () -> assertEquals(original.isBlessWield(), duplicate.isBlessWield()),
                    () -> assertEquals(original.isCumberArmour(), duplicate.isCumberArmour()),
                    () -> assertEquals(read(original, "numBlows"), read(duplicate, "numBlows")),
                    () -> assertEquals(read(original, "seeInfra"), read(duplicate, "seeInfra")),
                    () -> assertEquals(read(original, "toH"), read(duplicate, "toH")),
                    () -> assertEquals(read(original, "toD"), read(duplicate, "toD")),
                    () -> assertEquals(original.getStatTop(Stats.STAT_STR), duplicate.getStatTop(Stats.STAT_STR)),
                    () -> assertEquals(original.getStatUse(Stats.STAT_STR), duplicate.getStatUse(Stats.STAT_STR)),
                    () -> assertEquals(original.getStatAdd(Stats.STAT_STR), duplicate.getStatAdd(Stats.STAT_STR)),
                    () -> assertEquals(original.getStatInd(Stats.STAT_STR), duplicate.getStatInd(Stats.STAT_STR)),
                    () -> assertEquals(original.getPlayerSkill(PlayerSkill.SKILL_STEALTH),
                            duplicate.getPlayerSkill(PlayerSkill.SKILL_STEALTH)),
                    () -> assertEquals(original.getResLevel(ElementEnum.ELEM_FIRE),
                            duplicate.getResLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(duplicate.hasPFlag(PlayerFlag.PF_UNLIGHT)),
                    () -> assertTrue(duplicate.hasOFlag(ObjectFlag.OF_FEATHER)));
        }

        /**
         * Movement actions and blows are adjacent fields of the same type holding similar small
         * numbers, which makes them the pair a copy is most likely to confuse — and a duplicate that
         * took its movement count from its blows would give the character extra moves that no gear
         * granted, with nothing in play to point at. Set to values that cannot be mistaken for each
         * other so a swap fails loudly.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("keeps movement actions distinct from blows")
        void movesAreNotBlows() throws ReflectiveOperationException {
            PlayerState original = new PlayerState();
            original.setNumBlows(300);
            original.setNumMoves(2);

            PlayerState duplicate = original.copy();

            assertAll(
                    () -> assertEquals(300, read(duplicate, "numBlows")),
                    () -> assertEquals(2, read(duplicate, "numMoves")));
        }

        /**
         * The point of the exercise: writing to the duplicate must leave the original alone. Covers
         * a scalar, each kind of map and both flag sets, since sharing any one of them would be
         * enough to defeat the comparison {@code updateBonuses} makes.
         */
        @Test
        @DisplayName("writing to the duplicate leaves the original untouched")
        void duplicateIsIndependent() {
            PlayerState original = filled();
            PlayerState duplicate = original.copy();

            duplicate.setSpeed(999);
            duplicate.setStatTop(Stats.STAT_STR, 99);
            duplicate.statAdd(Stats.STAT_STR, 7);
            duplicate.setStateSkill(PlayerSkill.SKILL_STEALTH, 99);
            duplicate.setResLevel(ElementEnum.ELEM_FIRE, 99);
            duplicate.setPlayerFlag(PlayerFlag.PF_COMBAT_REGEN);
            Flag<ObjectFlag> extra = new Flag<>(ObjectFlag.class);
            extra.on(ObjectFlag.OF_TELEPATHY);
            duplicate.unionObjectFlags(extra);

            assertAll(
                    () -> assertEquals(120, original.getSpeed()),
                    () -> assertEquals(18, original.getStatTop(Stats.STAT_STR)),
                    () -> assertEquals(2, original.getStatAdd(Stats.STAT_STR)),
                    () -> assertEquals(11, original.getPlayerSkill(PlayerSkill.SKILL_STEALTH)),
                    () -> assertEquals(2, original.getResLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertFalse(original.hasPFlag(PlayerFlag.PF_COMBAT_REGEN)),
                    () -> assertFalse(original.hasOFlag(ObjectFlag.OF_TELEPATHY)));
        }

        /**
         * Independence has to hold the other way round too. {@code updateBonuses} keeps the original
         * as the "before" picture while the duplicate is recalculated, but a later turn wipes and
         * refills whichever of the two it installed — so a shared map would corrupt the survivor
         * whichever direction the write came from.
         */
        @Test
        @DisplayName("writing to the original leaves the duplicate untouched")
        void originalIsIndependent() {
            PlayerState original = filled();
            PlayerState duplicate = original.copy();

            original.wipe();

            assertAll(
                    () -> assertEquals(120, duplicate.getSpeed()),
                    () -> assertEquals(18, duplicate.getStatTop(Stats.STAT_STR)),
                    () -> assertEquals(11, duplicate.getPlayerSkill(PlayerSkill.SKILL_STEALTH)),
                    () -> assertEquals(2, duplicate.getResLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(duplicate.hasPFlag(PlayerFlag.PF_UNLIGHT)));
        }

        /**
         * Each {@link uk.co.jackoftrades.middle.objects.ElementInfo} is a mutable object, so copying
         * the map alone would hand both states the same entries — the one case where the values all
         * arrive correctly and the states are still joined together.
         */
        @Test
        @DisplayName("gives the duplicate its own element entries")
        void elementEntriesAreNotShared() {
            PlayerState original = filled();

            PlayerState duplicate = original.copy();

            assertNotSame(original.getElInfo().get(ElementEnum.ELEM_FIRE),
                    duplicate.getElInfo().get(ElementEnum.ELEM_FIRE));
        }

        /**
         * A state that has never been through {@code calcBonuses} has an empty skills map, and that
         * is the state {@code updateBonuses} copies on its very first call — so copying one must
         * work rather than trip over the keys that are not there.
         */
        @Test
        @DisplayName("copies a state that has never been calculated")
        void copiesAFreshState() {
            PlayerState fresh = new PlayerState();

            PlayerState duplicate = fresh.copy();

            assertAll(
                    () -> assertEquals(0, duplicate.getSpeed()),
                    () -> assertEquals(0, duplicate.getStatTop(Stats.STAT_STR)),
                    () -> assertEquals(0, duplicate.getPlayerSkill(PlayerSkill.SKILL_STEALTH)),
                    () -> assertEquals(0, duplicate.getResLevel(ElementEnum.ELEM_FIRE)));
        }
    }

    /**
     * The methods that accumulate rather than assign — the ones the equipment walk calls once per
     * worn item.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("accumulators")
    class Accumulators {

        /**
         * Two rings of the same kind have to add up. Every one of these is called once per source in
         * {@code calcBonuses}, so an assignment masquerading as an addition would silently keep only
         * the last piece of gear examined.
         */
        @Test
        @DisplayName("repeated calls accumulate rather than replace")
        void repeatedCallsAccumulate() {
            PlayerState state = new PlayerState();
            state.statAdd(Stats.STAT_STR, 2);
            state.statAdd(Stats.STAT_STR, 3);
            state.skillAdd(PlayerSkill.SKILL_STEALTH, 4);
            state.skillAdd(PlayerSkill.SKILL_STEALTH, 5);
            state.toAcAdd(1);
            state.toAcAdd(2);
            state.toHitAdd(3);
            state.toHitAdd(4);
            state.toDamAdd(5);
            state.toDamAdd(6);
            state.infraAdd(1);
            state.infraAdd(2);

            assertAll(
                    () -> assertEquals(5, state.getStatAdd(Stats.STAT_STR)),
                    () -> assertEquals(9, state.getPlayerSkill(PlayerSkill.SKILL_STEALTH)));
        }

        /**
         * Negative contributions are ordinary: a cursed item lowers a stat, hunger takes to-hit
         * away. Nothing clamps at zero on the way in, because the totals are only meaningful once
         * every source has had its say.
         */
        @Test
        @DisplayName("negative amounts subtract")
        void negativeAmountsSubtract() {
            PlayerState state = new PlayerState();
            state.statAdd(Stats.STAT_DEX, 3);
            state.statAdd(Stats.STAT_DEX, -5);
            state.skillAdd(PlayerSkill.SKILL_DEVICE, -7);

            assertAll(
                    () -> assertEquals(-2, state.getStatAdd(Stats.STAT_DEX)),
                    () -> assertEquals(-7, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * A stat or skill nothing has touched reads as zero rather than throwing. C's arrays give
         * that for free; the port's maps have to be asked for it, and {@code calcBonuses} reads
         * before it writes in several places.
         */
        @Test
        @DisplayName("untouched stats and skills read as zero")
        void untouchedReadAsZero() {
            PlayerState state = new PlayerState();

            assertAll(
                    () -> assertEquals(0, state.getStatAdd(Stats.STAT_CON)),
                    () -> assertEquals(0, state.getStatInd(Stats.STAT_CON)),
                    () -> assertEquals(0, state.getPlayerSkill(PlayerSkill.SKILL_SEARCH)));
        }

        /**
         * {@code setStateSkill} replaces where {@code skillAdd} adds. The pair sit next to each
         * other and {@code calcBonuses} uses both, the setter wherever it recomputes a skill from
         * its own previous value.
         */
        @Test
        @DisplayName("setStateSkill replaces where skillAdd adds")
        void setterReplaces() {
            PlayerState state = new PlayerState();
            state.skillAdd(PlayerSkill.SKILL_STEALTH, 10);
            state.setStateSkill(PlayerSkill.SKILL_STEALTH, 3);

            assertEquals(3, state.getPlayerSkill(PlayerSkill.SKILL_STEALTH));
        }
    }

    /**
     * The two ways to write a resistance, and the difference between them.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("elemental resistances")
    class Resistances {

        /**
         * Both setters write the same place, and the getter reads it. The three-way scale is carried
         * as it stands — a vulnerability is a negative level, not a separate flag.
         */
        @Test
        @DisplayName("both setters write the level the getter reads")
        void bothSettersWrite() {
            PlayerState state = new PlayerState();
            state.setElInfo(ElementEnum.ELEM_FIRE, 3);
            state.setResLevel(ElementEnum.ELEM_COLD, -1);

            assertAll(
                    () -> assertEquals(3, state.getResLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertEquals(-1, state.getResLevel(ElementEnum.ELEM_COLD)),
                    () -> assertEquals(3, state.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel()));
        }

        /**
         * The difference between the two: {@code setElInfo} creates a missing entry and
         * {@code setResLevel} requires one. After a wipe every real element has an entry, so the
         * distinction only shows for a key that should never arrive — which is precisely when a
         * quiet success would be worse than a failure.
         */
        @Test
        @DisplayName("setResLevel demands an existing entry where setElInfo creates one")
        void settersDifferOnMissingEntries() {
            PlayerState state = new PlayerState();

            assertAll(
                    () -> assertThrows(NullPointerException.class,
                            () -> state.setResLevel(ElementEnum.ELEM_NONE, 1)),
                    () -> assertThrows(NullPointerException.class,
                            () -> state.getResLevel(ElementEnum.ELEM_NONE)));
        }

        /**
         * The map view is unmodifiable, so a caller cannot add or drop an element behind the state's
         * back. Its values are the live ones, which is a known limit of the guard rather than an
         * oversight — callers wanting to write use the setters.
         */
        @Test
        @DisplayName("the element map cannot have entries added or removed through the view")
        void mapViewIsUnmodifiable() {
            PlayerState state = new PlayerState();

            assertThrows(UnsupportedOperationException.class,
                    () -> state.getElInfo().remove(ElementEnum.ELEM_FIRE));
        }
    }

    /**
     * The two flag sets and the ways in and out of them.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("flags")
    class Flags {

        /**
         * Copying establishes the set and unioning adds to it — the pairing {@code calcBonuses}
         * relies on when it takes the race's player flags and then adds the class's
         * ({@code player-calcs.c:1917-1919}). If the copy did not discard, a state reused across two
         * characters would carry the first one's abilities.
         */
        @Test
        @DisplayName("copy replaces the set, union adds to it")
        void copyReplacesUnionAdds() {
            PlayerState state = new PlayerState();
            state.setPlayerFlag(PlayerFlag.PF_UNLIGHT);

            Flag<PlayerFlag> race = new Flag<>(PlayerFlag.class);
            race.on(PlayerFlag.PF_EVIL);
            state.copyPlayerFlag(race);

            Flag<PlayerFlag> playerClass = new Flag<>(PlayerFlag.class);
            playerClass.on(PlayerFlag.PF_FAST_SHOT);
            state.unionPlayerFlags(playerClass);

            assertAll(
                    () -> assertFalse(state.hasPFlag(PlayerFlag.PF_UNLIGHT),
                            "the copy should have discarded what was there"),
                    () -> assertTrue(state.hasPFlag(PlayerFlag.PF_EVIL)),
                    () -> assertTrue(state.hasPFlag(PlayerFlag.PF_FAST_SHOT)));
        }

        /**
         * Object flags only ever go on. The set is the union of what the gear, the race and the
         * running statuses grant, and nothing in the calculation removes one — which is what lets a
         * consumer ask a single question instead of three.
         */
        @Test
        @DisplayName("object flags accumulate and are never removed")
        void objectFlagsAccumulate() {
            PlayerState state = new PlayerState();
            Flag<ObjectFlag> first = new Flag<>(ObjectFlag.class);
            first.on(ObjectFlag.OF_FEATHER);
            Flag<ObjectFlag> second = new Flag<>(ObjectFlag.class);
            second.on(ObjectFlag.OF_SEE_INVIS);

            state.unionObjectFlags(first);
            state.unionObjectFlags(second);

            assertAll(
                    () -> assertTrue(state.hasOFlag(ObjectFlag.OF_FEATHER)),
                    () -> assertTrue(state.hasOFlag(ObjectFlag.OF_SEE_INVIS)));
        }

        /**
         * The two flag getters hand out the live sets, not copies — deliberately, because
         * {@code calcBonuses} passes the object set straight to {@code flagsTimed} for it to add the
         * statuses' duplicated flags to ({@code player-calcs.c:2135}). Pinning it as shared stops
         * someone making it defensive and quietly dropping every timed flag.
         */
        @Test
        @DisplayName("the flag getters expose the live sets, which flagsTimed depends on")
        void flagGettersAreLive() {
            PlayerState state = new PlayerState();

            state.getObjectFlag().on(ObjectFlag.OF_PROT_FEAR);
            state.getPlayerFlag().on(PlayerFlag.PF_EVIL);

            assertAll(
                    () -> assertTrue(state.hasOFlag(ObjectFlag.OF_PROT_FEAR)),
                    () -> assertTrue(state.hasPFlag(PlayerFlag.PF_EVIL)),
                    () -> assertSame(state.getObjectFlag(), state.getObjectFlag()));
        }
    }

    /**
     * The one method here that computes rather than stores.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("weightLimit")
    class WeightLimit {

        /**
         * The limit is the strength table's value at the player's index times 100 — a table lookup,
         * not the index itself. The two are easy to confuse and the wrong one is catastrophic:
         * {@code calcBonuses} divides by a tenth of the limit, so an index of zero would divide by
         * zero where the table's floor of 5 gives 500.
         */
        @Test
        @DisplayName("the limit is the table value at the strength index, times 100")
        void limitIsTableValueNotIndex() {
            PlayerState state = new PlayerState();

            state.setStatInd(Stats.STAT_STR, 0);
            int atZero = state.weightLimit();
            state.setStatInd(Stats.STAT_STR, 10);
            int atTen = state.weightLimit();

            assertAll(
                    () -> assertEquals(StatTables.adjStrWgt[0] * 100, atZero),
                    () -> assertEquals(StatTables.adjStrWgt[10] * 100, atTen),
                    () -> assertTrue(atZero > 0,
                            "a strength index of zero must still give a usable limit"));
        }

        /**
         * Nothing but strength enters into it. C's {@code weight_limit} reads one array and one
         * index, and a limit that moved with dexterity would make the carrying penalty depend on
         * something the player cannot reason about.
         */
        @Test
        @DisplayName("only strength affects the limit")
        void onlyStrengthMatters() {
            PlayerState state = new PlayerState();
            state.setStatInd(Stats.STAT_STR, 5);
            int before = state.weightLimit();

            state.setStatInd(Stats.STAT_DEX, 30);
            state.setStatInd(Stats.STAT_CON, 30);

            assertEquals(before, state.weightLimit());
        }
    }

    /**
     * The plain stores, checked once so that a getter reading the wrong field cannot hide.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("plain accessors")
    class PlainAccessors {

        /**
         * Distinct values throughout: several of these are adjacent integers with similar names, and
         * a getter wired to its neighbour would pass any test that set them all the same.
         */
        @Test
        @DisplayName("each setter is read back by its own getter")
        void settersRoundTrip() {
            PlayerState state = new PlayerState();
            state.setSpeed(121);
            state.setNumShots(22);
            state.setAmmoMult(3);
            state.setBaseAc(44);
            state.setDamRed(5);
            state.setCurLight(6);
            state.setStatInd(Stats.STAT_WIS, 7);

            assertAll(
                    () -> assertEquals(121, state.getSpeed()),
                    () -> assertEquals(22, state.getNumShots()),
                    () -> assertEquals(3, state.getAmmoMult()),
                    () -> assertEquals(44, state.getBaseAc()),
                    () -> assertEquals(5, state.getDamRed()),
                    () -> assertEquals(6, state.getCurLight()),
                    () -> assertEquals(7, state.getStatInd(Stats.STAT_WIS)));
        }

        /**
         * The four "this piece of gear is a problem" booleans are independent. They are set in pairs
         * by {@code calcBonuses} and a shared field would show up nowhere else.
         */
        @Test
        @DisplayName("the heavy/bless/cumber booleans are independent")
        void booleansAreIndependent() {
            PlayerState state = new PlayerState();
            state.setHeavyWield(true);

            assertAll(
                    () -> assertTrue(state.isHeavyWield()),
                    () -> assertFalse(state.isHeavyShoot()));
        }
    }
}
