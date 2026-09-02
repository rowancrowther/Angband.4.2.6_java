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

package uk.co.jackoftradesltd.middle.monsters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.middle.player.PlayerState;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Monster#updateSmartLearn}, the port of C's {@code update_smart_learn}
 * ({@code mon-util.c:790}).
 *
 * <p>Expected values are read off the C, not off the port. The clauses that carry weight there are:
 * the sanity return, which asks about the flag and the element and pointedly <em>not</em> about the
 * pflag; the player's own learning, which happens before any of the monster's gates and so survives
 * all four of them; the four returns themselves, in C's order; and the three learning steps, each of
 * which writes an absence as readily as a presence.
 *
 * <p>Three of the gates are rolls, so every test seeds {@link RandomValueUtils} and the seeds are
 * chosen for what the next draw or two produce rather than for looking tidy. {@link #SEED_LEARNS}
 * clears both rolls on either path, {@link #SEED_ONE_IN_TWO} fails the one-in-two, and
 * {@link #SEED_RARE_FAILURE} fails the one-in-a-hundred on the first draw — which is what lets a
 * test show that the rare failure catches a smart monster too. The port does not reproduce C's
 * rejection sampling ({@code RandomValueUtils.randDiv}), so these are the port's draws; what is
 * taken from C is which roll is made, in what order, and what each outcome means.
 *
 * <p>Most tests leave the player unequipped, so the two player-side learning calls walk an empty
 * body and are inert. The pair that proves the ordering equips one item instead, and asserts on the
 * cheapest observable half of {@code equipLearnFlag}: an item that lacks the flag and is not fully
 * known has the flag written onto its known counterpart, marking that it had its chance to show the
 * property and did not.
 *
 * <p>Class MonsterUpdateSmartLearnTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
@DisplayName("Monster.updateSmartLearn")
class MonsterUpdateSmartLearnTest {

    /**
     * Both rolls pass: {@code oneIn(2)} draws 1 and the following {@code oneIn(100)} draws 83.
     */
    private static final long SEED_LEARNS = 20260831L;

    /**
     * The one-in-two fails first time: {@code oneIn(2)} draws 0, so a non-smart monster returns.
     */
    private static final long SEED_ONE_IN_TWO = 20260830L;

    /**
     * The first draw of {@code oneIn(100)} is 0, the rare failure that catches every monster.
     */
    private static final long SEED_RARE_FAILURE = 18L;

    private Player player;

    /**
     * The monster's picture of the player, read back after every call.
     */
    private PlayerState known;

    /**
     * Build a bare race carrying the given race flags. The two predicates read nothing else.
     *
     * @param raceFlags the race flags to switch on
     * @return a shell race carrying those flags
     */
    private static MonsterRace raceWith(MonsterRaceFlag... raceFlags) {
        Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
        for (MonsterRaceFlag flag : raceFlags) {
            flags.on(flag);
        }
        return new MonsterRace("test", "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                flags, null, List.of(), 0, 0, null, null, List.of(), List.of(), List.of(),
                List.of(), List.of(), 0, null);
    }

    private static void set(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Object get(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void poke(Object target, String name, Object value) throws Exception {
        set(target, name, value);
    }

    @BeforeEach
    void setUp() throws Exception {
        player = new Player();
        set(player, "state", new PlayerState());
        set(player, "itemKnowledge", new KnownObject());
        known = new PlayerState();
        RandomValueUtils.stateInit(SEED_LEARNS);
    }

    /**
     * A monster of the given race, holding {@link #known} as its picture of the player.
     */
    private Monster monsterOf(MonsterRace race) {
        return new Monster(race, null, null, 0, 0, null, 0, 0, 0,
                new Flag<>(MonsterFlag.class), null, null, null, known, null, null, null, 0, 0);
    }

    /**
     * A monster clever enough to skip the one-in-two roll.
     */
    private Monster smart() {
        return monsterOf(raceWith(MonsterRaceFlag.RF_SMART));
    }

    /**
     * A monster that is neither smart nor stupid, and so takes the one-in-two roll.
     */
    private Monster ordinary() {
        return monsterOf(raceWith());
    }

    /**
     * A monster too stupid to learn anything.
     */
    private Monster stupid() {
        return monsterOf(raceWith(MonsterRaceFlag.RF_STUPID));
    }

    /**
     * Switches the birth option that permits monster learning off.
     */
    private void turnLearningOff() throws Exception {
        Object options = get(player, "options");
        Field field = options.getClass().getDeclaredField("options");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Flag<PlayerOptionEnum> flags = (Flag<PlayerOptionEnum>) field.get(options);
        flags.off(PlayerOptionEnum.OP_birth_ai_learn);
    }

    /**
     * Equips one item that carries no flags and is not fully known, and returns its known
     * counterpart — the object {@code equipLearnFlag} writes to when an item fails to show a
     * property. The to-armour bonus is the wedge that keeps the two sides apart, the same lever
     * {@code PlayerEquipLearnElementTest} uses.
     *
     * @return the known counterpart of the equipped item
     */
    private ItemObject equipOneUnknownItem() throws Exception {
        ItemObject item = new ItemObject();
        ItemObject counterpart = new ItemObject();
        for (ItemObject each : List.of(item, counterpart)) {
            poke(each, "flags", new Flag<>(ObjectFlag.class));
            poke(each, "elInfo", new LinkedHashMap<ElementEnum, ElementInfo>());
            poke(each, "curses", new LinkedHashMap<>());
            poke(each, "effect", new ArrayList<>());
        }
        poke(item, "known", counterpart);
        poke(item, "toAC", 5);

        EquipSlot slot = new EquipSlot(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, "on your body");
        poke(slot, "item", item);
        List<EquipSlot> slots = new ArrayList<>();
        slots.add(slot);
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_CLOAK, "on your back"));
        set(player, "body", new PlayerBody("Humanoid", slots));

        return counterpart;
    }

    // ---------------------------------------------------------------- sanity check

    /**
     * C's {@code if (!flag && !element_ok) return;}. A call naming nothing has nothing to teach, and
     * the {@code known_pstate} is left exactly as it was.
     */
    @Test
    @DisplayName("neither a flag nor an element learns nothing")
    void neitherFlagNorElement() {
        known.oFlagOn(ObjectFlag.OF_FREE_ACT);

        smart().updateSmartLearn(player, ObjectFlag.OF_NONE, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertTrue(known.hasOFlag(ObjectFlag.OF_FREE_ACT),
                "the sanity return must leave an existing belief untouched");
    }

    /**
     * The sanity check asks about the flag and the element only. C never consults {@code pflag}
     * there, so a call naming a pflag alone returns before the pflag can be learned — the one place
     * the three arguments are not treated alike.
     */
    @Test
    @DisplayName("a player flag alone does not get past the sanity check")
    void playerFlagAloneIsNotEnough() {
        player.getPlayerState().playerFlagOn(PlayerFlag.PF_NO_MANA);

        smart().updateSmartLearn(player, ObjectFlag.OF_NONE, PlayerFlag.PF_NO_MANA, ElementEnum.ELEM_NONE);

        assertFalse(known.hasPFlag(PlayerFlag.PF_NO_MANA),
                "C returns before the pflag block when there is no flag and no valid element");
    }

    /**
     * C's bounds are {@code element >= 0 && element < ELEM_MAX}, so the end-marker is not an element.
     * Paired with no flag, it takes the sanity return.
     */
    @Test
    @DisplayName("ELEM_MAX is not a valid element")
    void elementMaxIsNotAnElement() {
        player.getPlayerState().playerFlagOn(PlayerFlag.PF_NO_MANA);

        smart().updateSmartLearn(player, ObjectFlag.OF_NONE, PlayerFlag.PF_NO_MANA, ElementEnum.ELEM_MAX);

        assertFalse(known.hasPFlag(PlayerFlag.PF_NO_MANA));
    }

    /**
     * A flag on its own is enough to get past the sanity check, C's first disjunct. This is the
     * shape of the {@code mon-blows.c:554} call, which passes a flag and {@code -1}.
     */
    @Test
    @DisplayName("a flag alone gets past the sanity check")
    void flagAloneIsEnough() {
        player.getPlayerState().oFlagOn(ObjectFlag.OF_FREE_ACT);

        smart().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertTrue(known.hasOFlag(ObjectFlag.OF_FREE_ACT));
    }

    /**
     * An element on its own is the other disjunct, and the shape of the {@code mon-blows.c:689}
     * call, which passes {@code 0} and an element.
     */
    @Test
    @DisplayName("an element alone gets past the sanity check")
    void elementAloneIsEnough() {
        player.getPlayerState().setResLevel(ElementEnum.ELEM_POIS, 1);

        smart().updateSmartLearn(player, ObjectFlag.OF_NONE, PlayerFlag.PF_NONE, ElementEnum.ELEM_POIS);

        assertEquals(1, known.getResLevel(ElementEnum.ELEM_POIS));
    }

    // ---------------------------------------------------------------- the learning steps

    /**
     * C writes the absence as readily as the presence — {@code of_off} in the else arm — because the
     * monster may already believe the player has the flag. Both directions, from both starting
     * beliefs, so that neither a write nor a clear can be mistaken for the state it started in.
     */
    @Test
    @DisplayName("a flag is learned present or absent, correcting an earlier belief")
    void flagIsLearnedBothWays() {
        known.oFlagOn(ObjectFlag.OF_FREE_ACT);

        smart().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertFalse(known.hasOFlag(ObjectFlag.OF_FREE_ACT),
                "the player does not have the flag, so the monster's belief must be cleared");

        player.getPlayerState().oFlagOn(ObjectFlag.OF_FREE_ACT);
        RandomValueUtils.stateInit(SEED_LEARNS);

        smart().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertTrue(known.hasOFlag(ObjectFlag.OF_FREE_ACT));
    }

    /**
     * The pflag block is reached only when something else got the call past the sanity check, so it
     * is exercised alongside a flag. It reads {@code p->state.pflags} rather than the object flags,
     * and writes into the pflag half of {@code known_pstate}.
     */
    @Test
    @DisplayName("a player flag is learned present or absent alongside a flag")
    void playerFlagIsLearnedBothWays() {
        known.playerFlagOn(PlayerFlag.PF_NO_MANA);

        smart().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NO_MANA, ElementEnum.ELEM_NONE);

        assertFalse(known.hasPFlag(PlayerFlag.PF_NO_MANA));

        player.getPlayerState().playerFlagOn(PlayerFlag.PF_NO_MANA);
        RandomValueUtils.stateInit(SEED_LEARNS);

        smart().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NO_MANA, ElementEnum.ELEM_NONE);

        assertTrue(known.hasPFlag(PlayerFlag.PF_NO_MANA));
    }

    /**
     * The element step is a copy of {@code res_level}, not a test of it, so a vulnerability travels
     * exactly as a resistance does and a zero corrects an earlier belief in a resistance.
     */
    @Test
    @DisplayName("the element resistance level is copied whatever it is")
    void elementLevelIsCopied() {
        player.getPlayerState().setResLevel(ElementEnum.ELEM_FIRE, 3);
        player.getPlayerState().setResLevel(ElementEnum.ELEM_COLD, -1);
        known.setResLevel(ElementEnum.ELEM_ACID, 3);

        Monster mon = smart();
        mon.updateSmartLearn(player, ObjectFlag.OF_NONE, PlayerFlag.PF_NONE, ElementEnum.ELEM_FIRE);
        RandomValueUtils.stateInit(SEED_LEARNS);
        mon.updateSmartLearn(player, ObjectFlag.OF_NONE, PlayerFlag.PF_NONE, ElementEnum.ELEM_COLD);
        RandomValueUtils.stateInit(SEED_LEARNS);
        mon.updateSmartLearn(player, ObjectFlag.OF_NONE, PlayerFlag.PF_NONE, ElementEnum.ELEM_ACID);

        assertAll(
                () -> assertEquals(3, known.getResLevel(ElementEnum.ELEM_FIRE), "a resistance"),
                () -> assertEquals(-1, known.getResLevel(ElementEnum.ELEM_COLD), "a vulnerability"),
                () -> assertEquals(0, known.getResLevel(ElementEnum.ELEM_ACID),
                        "a zero must overwrite a believed resistance"));
    }

    /**
     * The three steps are independent and all three run on a call that names all three, in C's order.
     */
    @Test
    @DisplayName("a flag, a player flag and an element are all learned in one call")
    void allThreeAtOnce() {
        player.getPlayerState().oFlagOn(ObjectFlag.OF_HOLD_LIFE);
        player.getPlayerState().playerFlagOn(PlayerFlag.PF_NO_MANA);
        player.getPlayerState().setResLevel(ElementEnum.ELEM_NETHER, 2);

        smart().updateSmartLearn(player, ObjectFlag.OF_HOLD_LIFE, PlayerFlag.PF_NO_MANA,
                ElementEnum.ELEM_NETHER);

        assertAll(
                () -> assertTrue(known.hasOFlag(ObjectFlag.OF_HOLD_LIFE)),
                () -> assertTrue(known.hasPFlag(PlayerFlag.PF_NO_MANA)),
                () -> assertEquals(2, known.getResLevel(ElementEnum.ELEM_NETHER)));
    }

    /**
     * An element the player has no unusual figure for is still learned, as a zero. C reads a fixed
     * {@code el_info[ELEM_MAX]} array where every index is present, and {@link PlayerState#wipe}
     * gives the port an entry for every real element to match.
     */
    @Test
    @DisplayName("a neutral element is learned as a zero")
    void neutralElementIsLearned() {
        known.setResLevel(ElementEnum.ELEM_SHARD, 1);

        smart().updateSmartLearn(player, ObjectFlag.OF_NONE, PlayerFlag.PF_NONE, ElementEnum.ELEM_SHARD);

        assertEquals(0, known.getResLevel(ElementEnum.ELEM_SHARD));
    }

    // ---------------------------------------------------------------- the four gates

    /**
     * C's {@code if (!OPT(p, birth_ai_learn)) return;} — the first gate, before either predicate.
     */
    @Test
    @DisplayName("the birth option off stops the monster learning")
    void optionOffStopsLearning() throws Exception {
        turnLearningOff();
        player.getPlayerState().oFlagOn(ObjectFlag.OF_FREE_ACT);

        smart().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertFalse(known.hasOFlag(ObjectFlag.OF_FREE_ACT));
    }

    /**
     * C's {@code if (monster_is_stupid(mon)) return;}, the second gate.
     */
    @Test
    @DisplayName("a stupid monster learns nothing")
    void stupidMonsterLearnsNothing() {
        player.getPlayerState().oFlagOn(ObjectFlag.OF_FREE_ACT);

        stupid().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertFalse(known.hasOFlag(ObjectFlag.OF_FREE_ACT));
    }

    /**
     * C's {@code if (!monster_is_smart(mon) && one_in_(2)) return;}. The pair of tests is the point:
     * the same seed, the same call, and only the race flag differing — the ordinary monster loses the
     * roll and the smart one never takes it.
     */
    @Test
    @DisplayName("an ordinary monster loses the one-in-two roll where a smart one is not asked")
    void oneInTwoAppliesOnlyToTheNotSmart() {
        player.getPlayerState().oFlagOn(ObjectFlag.OF_FREE_ACT);

        RandomValueUtils.stateInit(SEED_ONE_IN_TWO);
        ordinary().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);
        assertFalse(known.hasOFlag(ObjectFlag.OF_FREE_ACT),
                "the ordinary monster's one-in-two roll comes up and it returns");

        RandomValueUtils.stateInit(SEED_ONE_IN_TWO);
        smart().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);
        assertTrue(known.hasOFlag(ObjectFlag.OF_FREE_ACT),
                "the smart monster short-circuits before the roll is made at all");
    }

    /**
     * The other half of the one-in-two: an ordinary monster that wins the roll learns normally.
     */
    @Test
    @DisplayName("an ordinary monster that wins the one-in-two learns")
    void ordinaryMonsterCanLearn() {
        player.getPlayerState().oFlagOn(ObjectFlag.OF_FREE_ACT);

        ordinary().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertTrue(known.hasOFlag(ObjectFlag.OF_FREE_ACT));
    }

    /**
     * C's {@code if (one_in_(100)) return;}, the last gate, which is not guarded on the race flags —
     * "fail very rarely" applies to the cleverest monster in the game.
     */
    @Test
    @DisplayName("the one-in-a-hundred failure catches a smart monster too")
    void rareFailureCatchesSmartMonsters() {
        player.getPlayerState().oFlagOn(ObjectFlag.OF_FREE_ACT);
        RandomValueUtils.stateInit(SEED_RARE_FAILURE);

        smart().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertFalse(known.hasOFlag(ObjectFlag.OF_FREE_ACT));
    }

    // ---------------------------------------------------------------- the player's half

    /**
     * C's comment on the two learning calls is "anything a monster might learn, the player should
     * learn", and their position above every gate is what makes that true. With the option off the
     * monster learns nothing, and the player still learns: the equipped item lacks the flag and is
     * not fully known, so the flag is recorded on its known counterpart.
     */
    @Test
    @DisplayName("the player learns even when the option forbids the monster to")
    void playerLearnsThoughOptionIsOff() throws Exception {
        ItemObject counterpart = equipOneUnknownItem();
        turnLearningOff();

        smart().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertAll(
                () -> assertTrue(counterpart.hasFlag(ObjectFlag.OF_FREE_ACT),
                        "equipLearnFlag runs before the option is consulted"),
                () -> assertFalse(known.hasOFlag(ObjectFlag.OF_FREE_ACT),
                        "and the monster still learns nothing"));
    }

    /**
     * The same ordering against the second gate: a stupid monster teaches the player regardless.
     */
    @Test
    @DisplayName("the player learns even from a stupid monster")
    void playerLearnsFromStupidMonster() throws Exception {
        ItemObject counterpart = equipOneUnknownItem();

        stupid().updateSmartLearn(player, ObjectFlag.OF_FREE_ACT, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertTrue(counterpart.hasFlag(ObjectFlag.OF_FREE_ACT));
    }

    /**
     * The sanity return is above the player's learning too, so a call naming nothing teaches nobody.
     */
    @Test
    @DisplayName("the sanity return stops the player learning as well")
    void sanityReturnStopsThePlayerToo() throws Exception {
        ItemObject counterpart = equipOneUnknownItem();

        smart().updateSmartLearn(player, ObjectFlag.OF_NONE, PlayerFlag.PF_NONE, ElementEnum.ELEM_NONE);

        assertFalse(counterpart.hasFlag(ObjectFlag.OF_FREE_ACT));
    }
}
