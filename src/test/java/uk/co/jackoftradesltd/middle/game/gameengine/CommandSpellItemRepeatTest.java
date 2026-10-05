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

package uk.co.jackoftradesltd.middle.game.gameengine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.gameinput.DefaultGameInput;
import uk.co.jackoftradesltd.middle.gameinput.GameInput;
import uk.co.jackoftradesltd.middle.gameinput.GameInputHolder;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.magic.MagicSpell;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.GetItemFlags;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the parts of {@link Command} that {@link CommandTest} leaves out: {@code cmd_get_spell},
 * the shapechange mask in {@code cmd_get_item}, the filter handling of a stored item, the
 * repeat-cancel rule that separates {@code cmd_get_direction} from {@code cmd_get_target}, and what
 * {@code cmd_copy} shares and does not share between two commands ({@code cmd-core.c}).
 *
 * <p>Expected values come from reading the C. {@code cmd_get_spell} accepts a stored choice only
 * while the filter still passes it, otherwise asks the UI, and on success stores both the book and
 * the spell's flattened index; a failed pick stores nothing. {@code cmd_get_item} drops the
 * equipment, inventory and quiver bits for a shapechanged player and leaves the floor bit.
 * {@code cmd_get_direction} calls {@code cmd_cancel_repeat} when the player backs out and
 * {@code cmd_get_target} does not. {@code cmd_copy} is {@code *dest = *src}, so an item argument is
 * the same object in both commands.
 *
 * <p>The caster has two books of two spells each, so the flattened index runs 0 and 1 in the first
 * book and 2 and 3 in the second; an index that only comes out right across the book boundary is the
 * case a per-book index would get wrong.
 *
 * <p>Not covered: the {@code DIR_TARGET} stale-target branch of {@code getTarget}, because
 * {@code GameState.targetOkay} is still a stub that always answers true.
 *
 * <p>Class CommandSpellItemRepeatTest coded on 261003, commented in full on 261003.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class CommandSpellItemRepeatTest {

    private static final BiPredicate<Player, MagicSpell> ANY_SPELL = (p, s) -> true;
    private static final Predicate<ItemObject> ANY_BOOK = i -> true;
    private ScriptedInput ui;
    private Player player;
    private Player realPlayer;
    private CommandQueue realQueue;
    private Command cmd;
    private MagicSpell spellA;
    private MagicSpell spellB;
    private MagicSpell spellC;
    private MagicSpell spellD;
    private ItemObject book;

    private static MagicSpell spell(String name) {
        return new MagicSpell(name, 1, 0, 0, 0, List.of(), name);
    }

    /**
     * A caster with two books of two spells each, so flattened indices 0-1 are book one and 2-3 book two.
     */
    private static PlayerClass casterClass(MagicSpell a, MagicSpell b, MagicSpell c, MagicSpell d) {
        MagicRealm arcane = new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell", TValue.TV_MAGIC_BOOK);
        MagicBook first = new MagicBook(TValue.TV_MAGIC_BOOK, "First", false, 2, arcane,
                null, 0, 0, 0, 0, new ArrayList<>(List.of(a, b)));
        MagicBook second = new MagicBook(TValue.TV_MAGIC_BOOK, "Second", false, 2, arcane,
                null, 0, 0, 0, 0, new ArrayList<>(List.of(c, d)));
        return classWith(new ClassMagic(1, 300, 2, List.of(first, second)));
    }

    private static PlayerClass classWith(ClassMagic magic) {
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<PlayerSkill, Integer> extra = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
            extra.put(skill, 0);
        }
        return new PlayerClass("Test Class", List.of(), stats, skills, extra, 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(), magic);
    }

    private static void setClass(Player target, PlayerClass playerClass) throws Exception {
        Field field = Player.class.getDeclaredField("playerClass");
        field.setAccessible(true);
        field.set(target, playerClass);
    }

    private static PlayerShape shape(String name) {
        return new PlayerShape(name, 0, 0, 0, Map.<PlayerSkill, Integer>of(),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                Map.<ObjectModifier, Integer>of(), Map.<ElementEnum, ElementInfo>of(), List.of(), 0, List.of());
    }

    private static Flag<GetItemFlags> everywhere() {
        Flag<GetItemFlags> mode = new Flag<>(GetItemFlags.class);
        mode.on(GetItemFlags.USE_EQUIP);
        mode.on(GetItemFlags.USE_INVEN);
        mode.on(GetItemFlags.USE_QUIVER);
        mode.on(GetItemFlags.USE_FLOOR);
        return mode;
    }

    @BeforeEach
    void setUp() throws Exception {
        ui = new ScriptedInput();
        GameInputHolder.setInstance(ui);

        spellA = spell("A");
        spellB = spell("B");
        spellC = spell("C");
        spellD = spell("D");
        book = new ItemObject();

        player = new Player();
        setClass(player, casterClass(spellA, spellB, spellC, spellD));

        realPlayer = GameState.getPlayer();
        realQueue = GameState.getCommandQueue();
        GameState.setPlayer(player);
        GameState.setCommandQueue(new CommandQueue(player));
        cmd = new Command(CommandContext.CTX_GAME, CommandCode.CMD_NULL, 0, 0, new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        GameInputHolder.resetInstance();
        GameState.setPlayer(realPlayer);
        GameState.setCommandQueue(realQueue);
    }

    /**
     * A scripted UI for the spell and item pickers that records how often each was asked.
     */
    private static final class ScriptedInput extends DefaultGameInput {
        Optional<GameInput.SpellSelection> picker = Optional.empty();
        Optional<MagicSpell> fromBook = Optional.empty();
        Optional<ItemObject> item = Optional.empty();
        Optional<DirectionEnum> repDir = Optional.empty();
        Optional<DirectionEnum> aimDir = Optional.empty();

        int pickerCalls = 0;
        int fromBookCalls = 0;
        int itemCalls = 0;
        Flag<GetItemFlags> modeSeen;

        @Override
        public Optional<GameInput.SpellSelection> getSpell(Player player, String verb,
                                                           Predicate<ItemObject> bookFilter, CommandCode commandCode,
                                                           String bookErrorMessage,
                                                           BiPredicate<Player, MagicSpell> spellFilter,
                                                           String spellError, ItemObject magicBook) {
            pickerCalls++;
            return picker;
        }

        @Override
        public Optional<MagicSpell> getSpellFromBook(Player player, String verb, ItemObject magicBook,
                                                     String errorMessage,
                                                     BiPredicate<Player, MagicSpell> spellFilter) {
            fromBookCalls++;
            return fromBook;
        }

        @Override
        public Optional<ItemObject> getItem(String prompt, String errorMessage, CommandCode code,
                                            Predicate<ItemObject> filter, Flag<GetItemFlags> mode) {
            itemCalls++;
            modeSeen = mode;
            return item;
        }

        @Override
        public Optional<DirectionEnum> getRepDir(boolean allow5) {
            return repDir;
        }

        @Override
        public Optional<DirectionEnum> getAimDir() {
            return aimDir;
        }
    }

    /**
     * {@code cmd_get_spell}.
     */
    @Nested
    @DisplayName("getSpell")
    class Spells {

        @Test
        @DisplayName("a stored choice that still passes the filter is returned without asking the UI")
        void storedPasses() {
            cmd.setArgChoice("s", 1);

            assertEquals(Optional.of(spellB), cmd.getSpell("s", "cast", ANY_BOOK, "no book", ANY_SPELL, "no spell"));
            assertEquals(0, ui.pickerCalls);
            assertEquals(0, ui.fromBookCalls);
        }

        @Test
        @DisplayName("the stored index runs across book boundaries: 2 is the second book's first spell")
        void storedIndexCrossesBooks() {
            cmd.setArgChoice("s", 2);
            assertEquals(Optional.of(spellC), cmd.getSpell("s", "cast", ANY_BOOK, null, ANY_SPELL, null));

            cmd.setArgChoice("s", 3);
            assertEquals(Optional.of(spellD), cmd.getSpell("s", "cast", ANY_BOOK, null, ANY_SPELL, null));
        }

        @Test
        @DisplayName("a stored choice the filter now rejects falls through to the picker, and the new pick is stored")
        void storedRejectedByFilter() {
            cmd.setArgChoice("s", 2);
            ui.picker = Optional.of(new GameInput.SpellSelection(book, spellD));

            Optional<MagicSpell> got = cmd.getSpell("s", "cast", ANY_BOOK, null, (p, s) -> s != spellC, null);

            assertEquals(Optional.of(spellD), got);
            assertEquals(1, ui.pickerCalls);
            assertEquals(Optional.of(3), cmd.getArgChoice("s"), "the flattened index of D");
            assertSame(book, cmd.getArgItem("book").orElseThrow());
        }

        @Test
        @DisplayName("a stored index outside the class's spells falls through to the picker")
        void storedOutOfRange() {
            cmd.setArgChoice("s", 99);
            ui.picker = Optional.of(new GameInput.SpellSelection(book, spellA));

            assertEquals(Optional.of(spellA), cmd.getSpell("s", "cast", ANY_BOOK, null, ANY_SPELL, null));
            assertEquals(1, ui.pickerCalls);
            assertEquals(Optional.of(0), cmd.getArgChoice("s"));
        }

        @Test
        @DisplayName("a queued book argument sends the choice to that book alone, not the full picker")
        void bookQueued() {
            cmd.setArgItem("book", book);
            ui.fromBook = Optional.of(spellB);

            assertEquals(Optional.of(spellB), cmd.getSpell("s", "study", ANY_BOOK, null, ANY_SPELL, null));
            assertEquals(1, ui.fromBookCalls);
            assertEquals(0, ui.pickerCalls);
            assertEquals(Optional.of(1), cmd.getArgChoice("s"));
            assertSame(book, cmd.getArgItem("book").orElseThrow());
        }

        @Test
        @DisplayName("a queued book that yields no spell is an abort and stores no choice")
        void bookQueuedAbort() {
            cmd.setArgItem("book", book);

            assertTrue(cmd.getSpell("s", "study", ANY_BOOK, null, ANY_SPELL, null).isEmpty());
            assertEquals(0, ui.pickerCalls);
            assertTrue(cmd.getArgChoice("s").isEmpty());
        }

        @Test
        @DisplayName("an abort of the full picker returns empty and stores neither book nor choice")
        void pickerAbort() {
            assertTrue(cmd.getSpell("s", "cast", ANY_BOOK, null, ANY_SPELL, null).isEmpty());
            assertEquals(1, ui.pickerCalls);
            assertTrue(cmd.getArgChoice("s").isEmpty());
            assertTrue(cmd.getArgItem("book").isEmpty());
        }

        @Test
        @DisplayName("a class with no books still asks the UI, and an abort is empty")
        void nonCaster() throws Exception {
            setClass(player, classWith(ClassMagic.NONE));
            cmd.setArgChoice("s", 0);

            assertTrue(cmd.getSpell("s", "cast", ANY_BOOK, null, ANY_SPELL, null).isEmpty());
            assertEquals(1, ui.pickerCalls);
        }

        @Test
        @DisplayName("a spell that is in none of the caster's books is empty and stores no choice")
        void spellNotInClass() {
            ui.picker = Optional.of(new GameInput.SpellSelection(book, spell("stranger")));

            assertTrue(cmd.getSpell("s", "cast", ANY_BOOK, null, ANY_SPELL, null).isEmpty());
            assertTrue(cmd.getArgChoice("s").isEmpty());
        }
    }

    /**
     * {@code cmd_get_item}: the stored-item filter test and the shapechange mask.
     */
    @Nested
    @DisplayName("getItem")
    class Items {

        @Test
        @DisplayName("a stored item that passes the filter is returned without prompting")
        void storedPasses() {
            ItemObject stored = new ItemObject();
            cmd.setArgItem("i", stored);

            Optional<ItemObject> got = cmd.getItem("i", "p", "r", i -> i == stored, everywhere());

            assertSame(stored, got.orElseThrow());
            assertEquals(0, ui.itemCalls);
        }

        @Test
        @DisplayName("a null filter accepts any stored item")
        void nullFilterAccepts() {
            ItemObject stored = new ItemObject();
            cmd.setArgItem("i", stored);

            assertSame(stored, cmd.getItem("i", "p", "r", null, everywhere()).orElseThrow());
            assertEquals(0, ui.itemCalls);
        }

        @Test
        @DisplayName("a stored item the filter rejects is re-prompted, and the new pick replaces it")
        void storedRejected() {
            ItemObject stale = new ItemObject();
            ItemObject fresh = new ItemObject();
            cmd.setArgItem("i", stale);
            ui.item = Optional.of(fresh);

            Optional<ItemObject> got = cmd.getItem("i", "p", "r", i -> i == fresh, everywhere());

            assertSame(fresh, got.orElseThrow());
            assertEquals(1, ui.itemCalls);
            assertSame(fresh, cmd.getArgItem("i").orElseThrow());
        }

        @Test
        @DisplayName("a rejected stored item with a UI abort is empty, and the stale item stays stored")
        void storedRejectedThenAbort() {
            ItemObject stale = new ItemObject();
            cmd.setArgItem("i", stale);

            assertTrue(cmd.getItem("i", "p", "r", i -> false, everywhere()).isEmpty());
            assertSame(stale, cmd.getArgItem("i").orElseThrow());
        }

        @Test
        @DisplayName("a shapechanged player is confined to the floor, and the caller's mode is untouched")
        void shapechangedMask() {
            player.setShape(shape("werewolf"));
            Flag<GetItemFlags> mode = everywhere();

            cmd.getItem("i", "p", "r", null, mode);

            assertTrue(ui.modeSeen.has(GetItemFlags.USE_FLOOR));
            assertFalse(ui.modeSeen.has(GetItemFlags.USE_EQUIP));
            assertFalse(ui.modeSeen.has(GetItemFlags.USE_INVEN));
            assertFalse(ui.modeSeen.has(GetItemFlags.USE_QUIVER));
            assertTrue(mode.has(GetItemFlags.USE_EQUIP), "C's mode is by value; the caller's flags survive");
            assertTrue(mode.has(GetItemFlags.USE_INVEN));
            assertTrue(mode.has(GetItemFlags.USE_QUIVER));
        }

        @Test
        @DisplayName("the normal shape is not shapechanged, so every location stays open")
        void normalShapeUnmasked() {
            player.setShape(shape("normal"));

            cmd.getItem("i", "p", "r", null, everywhere());

            assertTrue(ui.modeSeen.has(GetItemFlags.USE_EQUIP));
            assertTrue(ui.modeSeen.has(GetItemFlags.USE_INVEN));
            assertTrue(ui.modeSeen.has(GetItemFlags.USE_QUIVER));
            assertTrue(ui.modeSeen.has(GetItemFlags.USE_FLOOR));
        }

        @Test
        @DisplayName("a prompted item is stored under the name")
        void promptedIsStored() {
            ItemObject fresh = new ItemObject();
            ui.item = Optional.of(fresh);

            cmd.getItem("i", "p", "r", null, everywhere());

            assertSame(fresh, cmd.getArgItem("i").orElseThrow());
        }
    }

    /**
     * {@code cmd_cancel_repeat} is called by {@code cmd_get_direction} on an abort and not by
     * {@code cmd_get_target}.
     */
    @Nested
    @DisplayName("repeat cancellation")
    class Repeats {

        /**
         * Makes {@code cmd} the queue's executing command, as {@code commandPop} does, without
         * dispatching it.
         */
        private void execute(Command running) throws Exception {
            Field field = CommandQueue.class.getDeclaredField("currentCommand");
            field.setAccessible(true);
            field.set(GameState.getCommandQueue(), running);
        }

        @Test
        @DisplayName("an aborted direction zeroes the executing command's repeats")
        void directionAbortCancels() throws Exception {
            cmd.setNrepeats(3);
            execute(cmd);

            assertTrue(cmd.getDirection("d", false).isEmpty());

            assertEquals(0, cmd.getNrepeats());
        }

        @Test
        @DisplayName("an answered direction leaves the repeats alone")
        void directionAnswerKeeps() throws Exception {
            cmd.setNrepeats(3);
            execute(cmd);
            ui.repDir = Optional.of(DirectionEnum.DIR_N);

            assertTrue(cmd.getDirection("d", false).isPresent());

            assertEquals(3, cmd.getNrepeats());
        }

        @Test
        @DisplayName("a stored direction never reaches the abort path, so repeats survive")
        void storedDirectionKeeps() throws Exception {
            cmd.setNrepeats(3);
            execute(cmd);
            cmd.setArgDirection("d", DirectionEnum.DIR_E);

            cmd.getDirection("d", false);

            assertEquals(3, cmd.getNrepeats());
        }

        @Test
        @DisplayName("an aborted aim does not cancel the repeat")
        void targetAbortKeeps() throws Exception {
            cmd.setNrepeats(3);
            execute(cmd);

            assertTrue(cmd.getTarget("t").isEmpty());

            assertEquals(3, cmd.getNrepeats());
        }
    }

    /**
     * What {@code cmd_copy} (the {@code clone}) shares between the two commands.
     */
    @Nested
    @DisplayName("clone")
    class Clone {

        @Test
        @DisplayName("an item argument is the same object in both commands, as C copies the pointer")
        void itemShared() {
            ItemObject item = new ItemObject();
            cmd.setArgItem("i", item);

            Command copy = cmd.clone();

            assertSame(item, copy.getArgItem("i").orElseThrow());
        }

        @Test
        @DisplayName("every argument type survives the copy")
        void allTypesCopied() {
            cmd.setArgChoice("c", 3);
            cmd.setArgNumber("n", 7);
            cmd.setArgDirection("d", DirectionEnum.DIR_N);
            cmd.setArgTarget("t", DirectionEnum.DIR_TARGET);
            cmd.setArgString("s", "hi");
            cmd.setArgPoint("p", Loc.zero.offset(4, 5));

            Command copy = cmd.clone();

            assertEquals(Optional.of(3), copy.getArgChoice("c"));
            assertEquals(Optional.of(7), copy.getArgNumber("n"));
            assertEquals(Optional.of(DirectionEnum.DIR_N), copy.getArgDirection("d"));
            assertEquals(Optional.of(DirectionEnum.DIR_TARGET), copy.getArgTarget("t"));
            assertEquals(Optional.of("hi"), copy.getArgString("s"));
            assertEquals(4, copy.getArgPoint("p").orElseThrow().getX());
            assertEquals(5, copy.getArgPoint("p").orElseThrow().getY());
        }

        @Test
        @DisplayName("context, code, repeats and the background flag are all carried over")
        void scalarsCopied() {
            Command original = new Command(CommandContext.CTX_STORE, CommandCode.CMD_NULL, 4, 2,
                    new ArrayList<>());

            Command copy = original.clone();

            assertEquals(CommandContext.CTX_STORE, copy.getContext());
            assertEquals(original.getCode(), copy.getCode());
            assertEquals(4, copy.getNrepeats());
            assertEquals(2, copy.getBackgroundCommand());
        }

        @Test
        @DisplayName("adding an argument to the copy does not add it to the original")
        void argListIndependent() {
            Command copy = cmd.clone();
            copy.setArgNumber("extra", 1);

            assertTrue(cmd.getArgNumber("extra").isEmpty());
            assertEquals(0, cmd.getArgs().size());
        }
    }

    /**
     * The wrong-type rule for the readers the base suite does not reach.
     */
    @Nested
    @DisplayName("typed readers")
    class Readers {

        @Test
        @DisplayName("an item, point or target read as another type is empty")
        void wrongTypes() {
            cmd.setArgItem("i", new ItemObject());
            cmd.setArgPoint("p", Loc.zero.offset(1, 1));
            cmd.setArgTarget("t", DirectionEnum.DIR_N);

            assertTrue(cmd.getArgNumber("i").isEmpty());
            assertTrue(cmd.getArgItem("p").isEmpty());
            assertTrue(cmd.getArgPoint("t").isEmpty());
            assertTrue(cmd.getArgDirection("t").isEmpty(), "a target is not a direction");
            assertTrue(cmd.getArgTarget("p").isEmpty());
        }

        @Test
        @DisplayName("an item reads back as the same object")
        void itemRoundTrip() {
            ItemObject item = new ItemObject();
            cmd.setArgItem("i", item);

            assertSame(item, cmd.getArgItem("i").orElseThrow());
        }
    }
}
