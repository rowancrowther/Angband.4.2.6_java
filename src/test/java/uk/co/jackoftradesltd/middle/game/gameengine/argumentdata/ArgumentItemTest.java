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

package uk.co.jackoftradesltd.middle.game.gameengine.argumentdata;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.game.gameengine.*;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ArgumentItem}, the port of the {@code struct object *obj} member of C's
 * {@code union cmd_arg_data} and its {@code arg_ITEM} tag (cmd-core.h). Expected values come from
 * {@code cmd_set_arg_item} and {@code cmd_get_arg_item} in cmd-core.c, which store and return the
 * pointer unchecked under the {@code arg_ITEM} tag, from {@code cmd_copy}, which copies a
 * non-string argument by plain struct assignment so that both commands hold the same object, and
 * from {@code cmd_disable_repeat_floor_item}, which forbids a repeat when an {@code arg_ITEM}
 * argument holds a non-NULL object whose grid is not (0, 0). C stores NULL under
 * {@code "original_item"} in cmd-wizard.c, so a null item is a legal value.
 *
 * <p>Not covered: reading a stored null back through {@code Command.getArgItem}, which is not part
 * of this record.
 *
 * <p>Class ArgumentItemTest coded on 261012, commented in full on 261012.
 */
@ExtendWith(SeededPlayerRegistry.class)
class ArgumentItemTest {

    private Player realPlayer;
    private CommandQueue realQueue;
    private Command cmd;

    @BeforeEach
    void setUp() {
        Player player = new Player();
        realPlayer = GameState.getPlayer();
        realQueue = GameState.getCommandQueue();
        GameState.setPlayer(player);
        GameState.setCommandQueue(new CommandQueue(player));
        cmd = new Command(CommandContext.CTX_GAME, CommandCode.CMD_NULL, 0, 0, new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        GameState.setPlayer(realPlayer);
        GameState.setCommandQueue(realQueue);
    }

    @Test
    @DisplayName("value() returns exactly the item the record was built with, and getValue() the same one")
    void valueRoundTrips() {
        ItemObject item = new ItemObject();

        ArgumentItem arg = new ArgumentItem(item);

        assertSame(item, arg.value());
        assertSame(item, arg.getValue());
    }

    @Test
    @DisplayName("a null item is storable, as C stores NULL under \"original_item\"")
    void nullIsStorable() {
        ArgumentItem arg = new ArgumentItem(null);

        assertNull(arg.value());
        assertNull(arg.getValue());
    }

    @Test
    @DisplayName("type() is arg_ITEM whatever the record holds, null included")
    void typeIsItem() {
        assertEquals(CommandArgumentType.arg_ITEM, new ArgumentItem(new ItemObject()).type());
        assertEquals(CommandArgumentType.arg_ITEM, new ArgumentItem(null).type());
    }

    @Test
    @DisplayName("equality is by the item's identity: the same object is equal, a different object is not")
    void equalityIsByReference() {
        ItemObject first = new ItemObject();
        ItemObject second = new ItemObject();

        assertEquals(new ArgumentItem(first), new ArgumentItem(first));
        assertEquals(new ArgumentItem(first).hashCode(), new ArgumentItem(first).hashCode());
        assertNotEquals(new ArgumentItem(first), new ArgumentItem(second));
        assertEquals(new ArgumentItem(null), new ArgumentItem(null));
        assertNotEquals(new ArgumentItem(first), new ArgumentItem(null));
    }

    @Test
    @DisplayName("the record holds the object, not a snapshot: a later change to the item is seen through it")
    void recordHoldsTheObjectItself() {
        ItemObject item = new ItemObject();
        ArgumentItem arg = new ArgumentItem(item);

        item.setGrid(Loc.row(4).col(7));

        assertEquals(Loc.row(4).col(7), arg.value().getGrid());
    }

    @Test
    @DisplayName("copy() is an ArgumentItem holding the very same item, equal to but not the same record")
    void copyHoldsSameItem() {
        ItemObject item = new ItemObject();
        ArgumentItem original = new ArgumentItem(item);

        CommandArgumentData copy = original.copy();

        assertInstanceOf(ArgumentItem.class, copy);
        assertSame(item, ((ArgumentItem) copy).value());
        assertEquals(original, copy);
        assertNotSame(original, copy);
        assertEquals(CommandArgumentType.arg_ITEM, copy.type());
    }

    @Test
    @DisplayName("copy() of a null item is a record holding null")
    void copyOfNull() {
        CommandArgumentData copy = new ArgumentItem(null).copy();

        assertInstanceOf(ArgumentItem.class, copy);
        assertNull(((ArgumentItem) copy).value());
    }

    @Test
    @DisplayName("a CommandArgument holding an item copies to an independent argument sharing the item")
    void commandArgumentCopyKeepsItem() {
        ItemObject item = new ItemObject();
        CommandArgument original = new CommandArgument(CommandArgumentType.arg_ITEM,
                new ArgumentItem(item), "item");

        CommandArgument copy = original.copy();

        assertNotSame(original, copy);
        assertNotSame(original.getData(), copy.getData());
        assertEquals(CommandArgumentType.arg_ITEM, copy.getType());
        assertEquals("item", copy.getName());
        assertSame(item, ((ArgumentItem) copy.getData()).value());
    }

    @Test
    @DisplayName("Command round trip: set then get returns the item, and a cloned command shares it")
    void commandRoundTripAndClone() {
        ItemObject item = new ItemObject();

        cmd.setArgItem("item", item);

        assertSame(item, cmd.getArgItem("item").orElseThrow());
        assertSame(item, cmd.clone().getArgItem("item").orElseThrow());
    }

    @Test
    @DisplayName("an item argument is not readable as a choice, number or direction, nor the reverse")
    void itemDoesNotCrossTypes() {
        cmd.setArgItem("i", new ItemObject());
        cmd.setArgChoice("c", 3);
        cmd.setArgNumber("n", 3);
        cmd.setArgDirection("d", DirectionEnum.DIR_N);

        assertTrue(cmd.getArgChoice("i").isEmpty());
        assertTrue(cmd.getArgNumber("i").isEmpty());
        assertTrue(cmd.getArgDirection("i").isEmpty());
        assertTrue(cmd.getArgItem("c").isEmpty());
        assertTrue(cmd.getArgItem("n").isEmpty());
        assertTrue(cmd.getArgItem("d").isEmpty());
        assertEquals(Optional.empty(), cmd.getArgItem("absent"));
    }

    private void execute(Command running) throws Exception {
        Field field = CommandQueue.class.getDeclaredField("currentCommand");
        field.setAccessible(true);
        field.set(GameState.getCommandQueue(), running);
    }

    private boolean repeatAllowed() throws Exception {
        Field field = CommandQueue.class.getDeclaredField("repeatPrevAllowed");
        field.setAccessible(true);
        return field.getBoolean(GameState.getCommandQueue());
    }

    private ItemObject itemAt(int x, int y) {
        ItemObject item = new ItemObject();
        item.setGrid(Loc.row(y).col(x));
        return item;
    }

    @Test
    @DisplayName("floor guard: an item argument on a non-origin grid shuts the repeat gate")
    void floorItemDisablesRepeat() throws Exception {
        cmd.setArgItem("item", itemAt(5, 3));
        execute(cmd);
        GameState.getCommandQueue().setRepeatPrevAllowed(true);

        GameState.getCommandQueue().disableRepeatFloorItem();

        assertFalse(repeatAllowed());
    }

    @Test
    @DisplayName("floor guard: a grid with only one non-zero coordinate still counts as the floor")
    void oneNonZeroCoordinateIsTheFloor() throws Exception {
        for (ItemObject item : new ItemObject[]{itemAt(5, 0), itemAt(0, 3)}) {
            Command running = new Command(CommandContext.CTX_GAME, CommandCode.CMD_NULL, 0, 0, new ArrayList<>());
            running.setArgItem("item", item);
            execute(running);
            GameState.getCommandQueue().setRepeatPrevAllowed(true);

            GameState.getCommandQueue().disableRepeatFloorItem();

            assertFalse(repeatAllowed());
        }
    }

    @Test
    @DisplayName("floor guard: an item at (0, 0), an item with no grid and a null item leave the gate open")
    void inventoryAndNullItemsKeepRepeat() throws Exception {
        for (ItemObject item : new ItemObject[]{itemAt(0, 0), new ItemObject(), null}) {
            Command running = new Command(CommandContext.CTX_GAME, CommandCode.CMD_NULL, 0, 0, new ArrayList<>());
            running.setArgItem("item", item);
            execute(running);
            GameState.getCommandQueue().setRepeatPrevAllowed(true);

            GameState.getCommandQueue().disableRepeatFloorItem();

            assertTrue(repeatAllowed());
        }
    }

    @Test
    @DisplayName("floor guard: a floor item in a later argument is found, and non-item arguments are ignored")
    void laterArgumentIsInspected() throws Exception {
        cmd.setArgChoice("spell", 2);
        cmd.setArgItem("book", new ItemObject());
        cmd.setArgItem("item", itemAt(1, 1));
        execute(cmd);
        GameState.getCommandQueue().setRepeatPrevAllowed(true);

        GameState.getCommandQueue().disableRepeatFloorItem();

        assertFalse(repeatAllowed());
    }

    @Test
    @DisplayName("floor guard: with the gate already shut it stays shut, and with no executing command it is a no-op")
    void gateAlreadyShutOrNoCommand() throws Exception {
        cmd.setArgItem("item", itemAt(5, 3));
        execute(cmd);
        GameState.getCommandQueue().setRepeatPrevAllowed(false);

        GameState.getCommandQueue().disableRepeatFloorItem();

        assertFalse(repeatAllowed());

        execute(null);
        GameState.getCommandQueue().setRepeatPrevAllowed(true);

        GameState.getCommandQueue().disableRepeatFloorItem();

        assertTrue(repeatAllowed());
    }
}
