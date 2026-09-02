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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffectReasonType;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerTimed#incCheck}, the port of C's {@code player_inc_check}
 * ({@code src/player-timed.c:926}).
 *
 * <p>The expected values are read off the C, not off the port. The clauses that carry weight there
 * are: the walk itself, where the first condition that holds returns {@code false} and the rest are
 * never reached; the {@code lore} split, which decides <em>which</em> state is consulted and whether
 * equipment is learned from at all; the sign difference between the resist test
 * ({@code res_level > 0}) and the vulnerability test ({@code res_level < 0}); the two
 * monster-conditional steps inside the object-flag branch; and the timed-effect condition, which has
 * no {@code lore} split because a running effect is already on the player's status line.
 *
 * <p><b>Why {@code lore} is tested with the two states disagreeing.</b> A condition tested against a
 * player whose known and calculated states agree cannot tell the two branches apart — both answer
 * the same, and a port that read the wrong one would pass. So every {@code lore} case here sets one
 * state and leaves the other clear, and asserts the answer the branch under test would give.
 *
 * <p><b>Learning is a side effect worth asserting on.</b> The live branches call
 * {@link PlayerKnowledge#equipLearnFlag} and {@link PlayerKnowledge#equipLearnElement} before they test, so a
 * condition that ends up vetoing has still identified equipment on the way past; the lore branches
 * must not, since nothing happened to the character. {@link #unknownItem} is the lever: an item its
 * counterpart has not caught up with records the chance it was given, and that record is what these
 * tests read to say whether the learning ran.
 *
 * <p>The failure conditions are registered rather than parsed. {@code player_timed.txt} declares
 * only six distinct conditions across the whole file and just one of the timed-effect kind, so
 * driving the tests from the shipped data would leave most of this function uncovered; each test
 * registers the condition list it is about instead. One case does reproduce the shipped
 * {@code POISONED} pair verbatim, as the check that the parts compose the way the data expects.
 *
 * @author Rowan Crowther
 */
@ExtendWith(uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry.class)
class PlayerIncCheckTest {

    /**
     * Any seed will do: no assertion here depends on a roll, but the monster learning takes some.
     */
    private static final long SEED = 20260831L;

    /**
     * The effect under test throughout — the one the shipped data gives failure conditions to.
     */
    private static final TimedEffect SUBJECT = TimedEffect.TMD_POISONED;

    /**
     * The effect the timed-effect condition refers to, as {@code player_timed.txt} does.
     */
    private static final TimedEffect REFERENT = TimedEffect.TMD_OPP_POIS;

    /**
     * The timed-effect list as it was before this class replaced it.
     */
    private static Object savedEffects;

    private Player player;
    private PlayerState state;
    private PlayerState known;
    private CapturingBus bus;
    private EventsHandler realBus;

    @BeforeAll
    static void save() throws Exception {
        savedEffects = registryField().get(null);
    }

    @AfterAll
    static void restore() throws Exception {
        registryField().set(null, savedEffects);
    }

    private static Field registryField() throws NoSuchFieldException {
        Field field = PlayerRegistry.class.getDeclaredField("playerTimedEffects");
        field.setAccessible(true);
        return field;
    }

    /**
     * A definition carrying nothing but its identity and its failure conditions.
     */
    private static PlayerTimedEffect effect(TimedEffect name, List<TimedFailure> failures) {
        return new PlayerTimedEffect(name, "test", null, null, null, null, failures,
                List.of(), (Effect) null, (Effect) null, false, 0, ObjectFlag.OF_NONE, false,
                ElementEnum.ELEM_NONE, null, null);
    }

    private static TimedFailure objectFlag(ObjectFlag flag) {
        return new TimedFailure(flag, TimedEffectReasonType.TYPE_OBJECT_FLAG);
    }

    // ---------------------------------------------------------------- fixtures

    private static TimedFailure resist(ElementEnum element) {
        return new TimedFailure(element, TimedEffectReasonType.TYPE_RESIST);
    }

    private static TimedFailure vulnerability(ElementEnum element) {
        return new TimedFailure(element, TimedEffectReasonType.TYPE_VULN);
    }

    private static TimedFailure playerFlag(PlayerFlag flag) {
        return new TimedFailure(flag, TimedEffectReasonType.TYPE_PLAYER_FLAG);
    }

    private static TimedFailure timedEffect(TimedEffect referent) {
        return new TimedFailure(referent, TimedEffectReasonType.TYPE_TIMED_EFFECT);
    }

    /**
     * A race with no flags: neither smart nor stupid, which is all the learning reads here.
     */
    private static MonsterRace race() {
        return new MonsterRace("test", "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                new Flag<>(MonsterRaceFlag.class), null, List.of(), 0, 0, null, null, List.of(),
                List.of(), List.of(), List.of(), List.of(), 0, null);
    }

    /**
     * Whether the equipped item's counterpart was told about the element, i.e. learning ran.
     */
    private static boolean learnedAbout(ItemObject counterpart, ElementEnum element) {
        return counterpart.getElInfo().get(element) != null;
    }

    /**
     * Whether the equipped item's counterpart was told about the flag, i.e. learning ran.
     */
    private static boolean learnedAbout(ItemObject counterpart, ObjectFlag flag) {
        return counterpart.hasFlag(flag);
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

    @SuppressWarnings("unchecked")
    private static void clearMessageLog() throws Exception {
        Field field = Message.class.getDeclaredField("messageLog");
        field.setAccessible(true);
        ((java.util.Deque<Object>) field.get(null)).clear();
    }

    @BeforeEach
    void setUp() throws Exception {
        player = new Player();
        state = new PlayerState();
        known = new PlayerState();
        set(player, "state", state);
        set(player, "knownState", known);
        set(player, "itemKnowledge", new KnownObject());

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
        clearMessageLog();
        RandomValueUtils.stateInit(SEED);
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * Registers {@link #SUBJECT} with the given failure conditions, and {@link #REFERENT} with none,
     * so that both lookups in a test answer.
     *
     * @param failures the conditions, in the order the walk should meet them
     */
    private void conditions(TimedFailure... failures) {
        PlayerRegistry.setPlayerTimedEffects(List.of(
                effect(SUBJECT, List.of(failures)),
                effect(REFERENT, List.of())));
    }

    /**
     * Sets a timed effect's counter directly, without the announcing that {@code setTimed} does.
     */
    @SuppressWarnings("unchecked")
    private void running(TimedEffect effect, int turns) throws Exception {
        ((Map<TimedEffect, Integer>) get(player, "timed")).put(effect, turns);
    }

    /**
     * Equips one item that carries no properties at all and whose counterpart lags behind it, and
     * returns that counterpart. The to-armour bonus is the wedge that keeps
     * {@link ItemObject#isFullyKnown()} false, the same lever the neighbouring learning suites use.
     *
     * <p>An item like this is the quiet witness for whether learning ran: neither
     * {@code equipLearnFlag} nor {@code equipLearnElement} finds anything on it to announce, so both
     * take their else branch and mark the counterpart as having been given its chance. Nothing else
     * in {@code incCheck} writes there.
     *
     * @return the known counterpart of the equipped item
     */
    private ItemObject unknownItem() throws Exception {
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

    /**
     * Gives the player a cave with no acting monster — C's {@code cave->mon_current} at zero, which
     * is what a trap or a potion leaves it at.
     */
    private void caveWithoutActor() throws Exception {
        set(player, "cave", chunk(0));
    }

    /**
     * Gives the player a cave whose acting monster is a plain one at index one, the state C leaves
     * when a monster's blow is what applied the effect.
     */
    private void caveWithActor() throws Exception {
        Chunk cave = chunk(1);
        Monster mon = new Monster(race(), null, null, 0, 0, null, 0, 0, 0,
                new Flag<>(MonsterFlag.class), null, null, null, new PlayerState(), null, null,
                null, 0, 0);
        ((Monster[]) get(cave, "monsters"))[1] = mon;
        set(player, "cave", cave);
    }

    private Chunk chunk(int monCurrent) {
        return new Chunk("test", 0, 0, 0, 0, 0, false, 3, 3, 0, 0, 2, 1, monCurrent, 0, player);
    }

    private List<String> announced() {
        return bus.messages.stream().map(EventDataMessage::message).toList();
    }

    private boolean resistWasAnnounced() {
        return announced().contains("You resist the effect!");
    }

    // ---------------------------------------------------------------- the walk

    /**
     * Catches what the check announces, in place of the real bus, which is reached statically
     * through {@link GameEngine} and so is put back after every test.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<EventDataMessage> messages = new ArrayList<>();

        @Override
        public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandlerType(GameEventType eventType) {
        }

        @Override
        public void gameEventDispatch(GameEventType eventType, GameEventData data) {
            if (eventType != GameEventType.EVENT_MESSAGE) return;

            assertInstanceOf(EventDataMessage.class, data);
            messages.add((EventDataMessage) data);
        }
    }

    // ---------------------------------------------------------------- object flag

    /**
     * C's {@code while (f)} over a list that is empty, falling straight to
     * {@code return true}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the walk")
    class TheWalk {

        @Test
        @DisplayName("an effect with no conditions is never prevented")
        void noConditions() {
            conditions();

            assertAll(
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, false), "live check"),
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, true), "lore check"));
        }

        @Test
        @DisplayName("every condition must pass for the effect to be allowed")
        void allMustPass() {
            conditions(resist(ElementEnum.ELEM_FIRE), playerFlag(PlayerFlag.PF_UNLIGHT),
                    timedEffect(REFERENT));

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, false),
                    "none of the three conditions holds, so the walk reaches its return");
        }

        @Test
        @DisplayName("the last condition can veto as readily as the first")
        void lastConditionVetoes() {
            conditions(resist(ElementEnum.ELEM_FIRE), vulnerability(ElementEnum.ELEM_COLD));
            state.setElInfo(ElementEnum.ELEM_COLD, -1);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        /**
         * C returns from inside the switch, so a condition that holds ends the function rather than
         * the iteration. Anything the later conditions would have done never happens — and on the
         * live path those conditions have visible side effects, which is what makes this observable
         * at all.
         */
        @Test
        @DisplayName("a veto stops the walk, so later conditions never learn from equipment")
        void vetoStopsTheWalk() throws Exception {
            ItemObject counterpart = unknownItem();
            conditions(timedEffect(REFERENT), resist(ElementEnum.ELEM_FIRE));
            running(REFERENT, 5);

            assertAll(
                    () -> assertFalse(PlayerTimed.incCheck(player, SUBJECT, false), "the first condition holds"),
                    () -> assertFalse(learnedAbout(counterpart, ElementEnum.ELEM_FIRE),
                            "the resist condition was never reached, so nothing was learned"));
        }

        /**
         * The control for the case above: reached, the same condition does learn.
         */
        @Test
        @DisplayName("a condition that is reached learns from equipment on the way past")
        void reachedConditionLearns() throws Exception {
            ItemObject counterpart = unknownItem();
            conditions(timedEffect(REFERENT), resist(ElementEnum.ELEM_FIRE));

            assertAll(
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, false), "neither condition holds"),
                    () -> assertTrue(learnedAbout(counterpart, ElementEnum.ELEM_FIRE),
                            "the resist condition was reached and learned from the equipment"));
        }
    }

    // ---------------------------------------------------------------- resist and vulnerability

    /**
     * C's {@code TMD_FAIL_FLAG_OBJECT} ({@code player-timed.c:933-960}), the one condition whose two
     * branches do more than read a different state.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("an object-flag condition")
    class ObjectFlagCondition {

        @Test
        @DisplayName("lore: a flag the player knows they have prevents the effect")
        void loreKnown() {
            conditions(objectFlag(ObjectFlag.OF_FREE_ACT));
            known.getObjectFlag().on(ObjectFlag.OF_FREE_ACT);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, true));
        }

        @Test
        @DisplayName("lore reads the known state, not the calculated one")
        void loreIgnoresCalculatedState() {
            conditions(objectFlag(ObjectFlag.OF_FREE_ACT));
            state.getObjectFlag().on(ObjectFlag.OF_FREE_ACT);

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, true),
                    "the player does not know about the flag, so their lore says nothing stops it");
        }

        @Test
        @DisplayName("lore learns nothing from equipment")
        void loreDoesNotLearn() throws Exception {
            ItemObject counterpart = unknownItem();
            conditions(objectFlag(ObjectFlag.OF_FREE_ACT));

            PlayerTimed.incCheck(player, SUBJECT, true);

            assertFalse(learnedAbout(counterpart, ObjectFlag.OF_FREE_ACT),
                    "a lore check is a query and must leave the character alone");
        }

        @Test
        @DisplayName("live: a flag on the calculated state prevents the effect")
        void liveHeld() throws Exception {
            caveWithoutActor();
            conditions(objectFlag(ObjectFlag.OF_FREE_ACT));
            state.getObjectFlag().on(ObjectFlag.OF_FREE_ACT);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        @Test
        @DisplayName("live reads the calculated state, not the known one")
        void liveIgnoresKnownState() throws Exception {
            caveWithoutActor();
            conditions(objectFlag(ObjectFlag.OF_FREE_ACT));
            known.getObjectFlag().on(ObjectFlag.OF_FREE_ACT);

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, false),
                    "believing in a flag one does not have does not turn the effect aside");
        }

        @Test
        @DisplayName("live learns from equipment before testing, even when nothing prevents it")
        void liveLearns() throws Exception {
            ItemObject counterpart = unknownItem();
            caveWithoutActor();
            conditions(objectFlag(ObjectFlag.OF_FREE_ACT));

            assertAll(
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, false)),
                    () -> assertTrue(learnedAbout(counterpart, ObjectFlag.OF_FREE_ACT)));
        }

        /**
         * C guards the message on {@code if (mon)}: an effect the player turns aside is announced
         * only when there was a monster to turn it aside from. A trap leaves {@code mon_current} at
         * zero and says nothing.
         */
        @Test
        @DisplayName("resisting says nothing when no monster is acting")
        void silentWithoutAnActor() throws Exception {
            caveWithoutActor();
            conditions(objectFlag(ObjectFlag.OF_FREE_ACT));
            state.getObjectFlag().on(ObjectFlag.OF_FREE_ACT);

            assertAll(
                    () -> assertFalse(PlayerTimed.incCheck(player, SUBJECT, false)),
                    () -> assertFalse(resistWasAnnounced(), "no actor, so nothing is announced"));
        }

        @Test
        @DisplayName("resisting a monster's effect is announced")
        void announcedWithAnActor() throws Exception {
            caveWithActor();
            conditions(objectFlag(ObjectFlag.OF_FREE_ACT));
            state.getObjectFlag().on(ObjectFlag.OF_FREE_ACT);

            assertAll(
                    () -> assertFalse(PlayerTimed.incCheck(player, SUBJECT, false)),
                    () -> assertTrue(resistWasAnnounced(), "the monster's effect was resisted"));
        }

        @Test
        @DisplayName("not resisting says nothing, actor or no actor")
        void nothingAnnouncedWhenNotResisted() throws Exception {
            caveWithActor();
            conditions(objectFlag(ObjectFlag.OF_FREE_ACT));

            assertAll(
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, false)),
                    () -> assertFalse(resistWasAnnounced(),
                            "the message belongs to the resisting branch alone"));
        }
    }

    // ---------------------------------------------------------------- player flag

    /**
     * C's {@code TMD_FAIL_FLAG_RESIST} and {@code TMD_FAIL_FLAG_VULN}
     * ({@code player-timed.c:962-993}). The pair are the same shape with the comparison reversed,
     * which is what the crossed cases below pin down.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("an element condition")
    class ElementCondition {

        @Test
        @DisplayName("resist, lore: a resistance the player knows of prevents the effect")
        void resistLoreKnown() {
            conditions(resist(ElementEnum.ELEM_POIS));
            known.setElInfo(ElementEnum.ELEM_POIS, 1);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, true));
        }

        @Test
        @DisplayName("resist, lore reads the known state, not the calculated one")
        void resistLoreIgnoresCalculatedState() throws Exception {
            ItemObject counterpart = unknownItem();
            conditions(resist(ElementEnum.ELEM_POIS));
            state.setElInfo(ElementEnum.ELEM_POIS, 1);

            assertAll(
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, true),
                            "an unknown resistance is not part of the player's lore"),
                    () -> assertFalse(learnedAbout(counterpart, ElementEnum.ELEM_POIS),
                            "and the lore branch learns nothing"));
        }

        @Test
        @DisplayName("resist, live: a resistance on the calculated state prevents the effect")
        void resistLiveHeld() {
            conditions(resist(ElementEnum.ELEM_POIS));
            state.setElInfo(ElementEnum.ELEM_POIS, 1);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        @Test
        @DisplayName("resist, live: no resistance lets the effect through")
        void resistLiveAbsent() {
            conditions(resist(ElementEnum.ELEM_POIS));

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        /**
         * C tests {@code > 0}, so a negative level is not a resistance however it reads.
         */
        @Test
        @DisplayName("resist: a vulnerability is not a resistance")
        void resistIsStrictlyPositive() {
            conditions(resist(ElementEnum.ELEM_POIS));
            state.setElInfo(ElementEnum.ELEM_POIS, -1);
            known.setElInfo(ElementEnum.ELEM_POIS, -1);

            assertAll(
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, false), "live"),
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, true), "lore"));
        }

        @Test
        @DisplayName("vulnerability, lore: one the player knows of prevents the effect")
        void vulnLoreKnown() {
            conditions(vulnerability(ElementEnum.ELEM_POIS));
            known.setElInfo(ElementEnum.ELEM_POIS, -1);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, true));
        }

        @Test
        @DisplayName("vulnerability, lore reads the known state, not the calculated one")
        void vulnLoreIgnoresCalculatedState() {
            conditions(vulnerability(ElementEnum.ELEM_POIS));
            state.setElInfo(ElementEnum.ELEM_POIS, -1);

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, true));
        }

        @Test
        @DisplayName("vulnerability, live: one on the calculated state prevents the effect")
        void vulnLiveHeld() {
            conditions(vulnerability(ElementEnum.ELEM_POIS));
            state.setElInfo(ElementEnum.ELEM_POIS, -1);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        /**
         * C tests {@code < 0}, so a resistance is not a vulnerability.
         */
        @Test
        @DisplayName("vulnerability: a resistance is not a vulnerability")
        void vulnIsStrictlyNegative() {
            conditions(vulnerability(ElementEnum.ELEM_POIS));
            state.setElInfo(ElementEnum.ELEM_POIS, 1);
            known.setElInfo(ElementEnum.ELEM_POIS, 1);

            assertAll(
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, false), "live"),
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, true), "lore"));
        }

        @Test
        @DisplayName("neither condition holds at a neutral zero")
        void neutralIsNeither() {
            conditions(resist(ElementEnum.ELEM_POIS), vulnerability(ElementEnum.ELEM_POIS));
            state.setElInfo(ElementEnum.ELEM_POIS, 0);

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        @Test
        @DisplayName("vulnerability, live learns from equipment before testing")
        void vulnLiveLearns() throws Exception {
            ItemObject counterpart = unknownItem();
            conditions(vulnerability(ElementEnum.ELEM_POIS));

            assertAll(
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, false)),
                    () -> assertTrue(learnedAbout(counterpart, ElementEnum.ELEM_POIS)));
        }
    }

    // ---------------------------------------------------------------- timed effect

    /**
     * C's {@code TMD_FAIL_FLAG_PLAYER} ({@code player-timed.c:995-1006}). The plainest of the five:
     * two reads of the same flag from two different states, and no learning on either side, because
     * a class or race flag is not something equipment can teach.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("a player-flag condition")
    class PlayerFlagCondition {

        @Test
        @DisplayName("lore: a flag the player knows they have prevents the effect")
        void loreKnown() {
            conditions(playerFlag(PlayerFlag.PF_UNLIGHT));
            known.playerFlagOn(PlayerFlag.PF_UNLIGHT);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, true));
        }

        @Test
        @DisplayName("lore reads the known state, not the calculated one")
        void loreIgnoresCalculatedState() {
            conditions(playerFlag(PlayerFlag.PF_UNLIGHT));
            state.playerFlagOn(PlayerFlag.PF_UNLIGHT);

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, true));
        }

        @Test
        @DisplayName("live: a flag on the calculated state prevents the effect")
        void liveHeld() {
            conditions(playerFlag(PlayerFlag.PF_UNLIGHT));
            state.playerFlagOn(PlayerFlag.PF_UNLIGHT);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        @Test
        @DisplayName("live reads the calculated state, not the known one")
        void liveIgnoresKnownState() {
            conditions(playerFlag(PlayerFlag.PF_UNLIGHT));
            known.playerFlagOn(PlayerFlag.PF_UNLIGHT);

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        @Test
        @DisplayName("no flag on either side lets the effect through")
        void absent() {
            conditions(playerFlag(PlayerFlag.PF_UNLIGHT));

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, false));
        }
    }

    // ---------------------------------------------------------------- the shipped data

    /**
     * C's {@code TMD_FAIL_FLAG_TIMED_EFFECT} ({@code player-timed.c:1008-1019}), whose test is
     * {@code if (p->timed[f->idx])} — a counter being non-zero, on an array where every index always
     * exists.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("a timed-effect condition")
    class TimedEffectCondition {

        /**
         * The case a presence test gets wrong. {@link Player}'s counters live in a map that is
         * populated with a zero for every effect at construction, so "is there an entry" is true
         * before the referenced effect has ever run, and an effect carrying this condition could
         * never be applied at all.
         */
        @Test
        @DisplayName("a dormant referenced effect does not prevent anything")
        void dormantReferentAllows() {
            conditions(timedEffect(REFERENT));

            assertAll(
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, false), "live"),
                    () -> assertTrue(PlayerTimed.incCheck(player, SUBJECT, true), "lore"));
        }

        @Test
        @DisplayName("a running referenced effect prevents the effect")
        void runningReferentVetoes() throws Exception {
            conditions(timedEffect(REFERENT));
            running(REFERENT, 1);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        /**
         * C writes no {@code lore} branch here and says why: a running effect is on the status line,
         * so there is no gap between what is true and what the player knows.
         */
        @Test
        @DisplayName("lore answers the same, since a running effect is already known")
        void loreMakesNoDifference() throws Exception {
            conditions(timedEffect(REFERENT));
            running(REFERENT, 7);

            assertEquals(PlayerTimed.incCheck(player, SUBJECT, false), PlayerTimed.incCheck(player, SUBJECT, true));
        }

        @Test
        @DisplayName("a single turn left still counts as running")
        void oneTurnIsRunning() throws Exception {
            conditions(timedEffect(REFERENT));
            running(REFERENT, 1);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, true));
        }
    }

    // ---------------------------------------------------------------- the unreachable case

    /**
     * The one effect in {@code player_timed.txt} that carries a timed-effect condition, reproduced
     * as the file declares it — {@code fail:2:POIS} then {@code fail:5:OPP_POIS} on {@code POISONED}
     * ({@code lib/gamedata/player_timed.txt:252-253}).
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the shipped POISONED conditions")
    class ShippedPoisonConditions {

        @BeforeEach
        void declareAsTheDataDoes() {
            conditions(resist(ElementEnum.ELEM_POIS), timedEffect(REFERENT));
        }

        @Test
        @DisplayName("an ordinary character can be poisoned")
        void ordinaryCharacterIsPoisoned() {
            assertTrue(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        @Test
        @DisplayName("a permanent poison resistance turns it aside")
        void permanentResistance() {
            state.setElInfo(ElementEnum.ELEM_POIS, 1);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        @Test
        @DisplayName("temporary poison resistance turns it aside")
        void temporaryResistance() throws Exception {
            running(REFERENT, 20);

            assertFalse(PlayerTimed.incCheck(player, SUBJECT, false));
        }

        @Test
        @DisplayName("once the temporary resistance runs out, poison lands again")
        void expiredTemporaryResistance() throws Exception {
            running(REFERENT, 0);

            assertTrue(PlayerTimed.incCheck(player, SUBJECT, false));
        }
    }

    /**
     * C's {@code default: assert(0)}. No parser produces a condition with no category, and
     * {@link TimedFailure}'s constructors refuse to build one, so the state has to be forced to
     * reach the branch at all.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("a condition with no category")
    class NoCategory {

        @Test
        @DisplayName("throws, where C asserts")
        void throwsLikeCAsserts() throws Exception {
            TimedFailure malformed = timedEffect(REFERENT);
            poke(malformed, "index", TimedEffectReasonType.TYPE_NONE);
            conditions(malformed);

            assertThrows(RuntimeException.class, () -> PlayerTimed.incCheck(player, SUBJECT, false));
        }
    }
}
