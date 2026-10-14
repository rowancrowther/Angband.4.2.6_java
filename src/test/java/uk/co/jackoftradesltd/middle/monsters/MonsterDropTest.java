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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link MonsterDrop}, the Java form of C's {@code struct monster_drop} in {@code monster.h},
 * and the two records that {@code parse_monster_drop} and {@code parse_monster_drop_base} build in
 * {@code mon-init.c}.
 *
 * <p>The expected values come from the C source and data, not from the Java. A {@code drop:} line
 * sets {@code kind}, {@code percent_chance}, {@code min} and {@code max} and leaves {@code tval}
 * zero; a {@code drop-base:} line sets {@code tval} and the same three figures and leaves
 * {@code kind} {@code NULL}. {@code mon_create_drop} and the lore save tell the two apart by
 * testing {@code drop->kind}, which is what {@link MonsterDrop#isBase()} stands for. The real
 * records come from {@code lib/gamedata/monster.txt}: the cutpurse's
 * {@code drop:scroll:Phase Door:35:1:3}, the rogue's {@code drop:scroll:Phase Door:70:2:6},
 * Farmer Maggot's {@code drop-base:mushroom:100:1:2}, and 77 drop lines in all, 2 {@code drop:}
 * and 75 {@code drop-base:}. The file holds no race with both kinds, and a few with two identical
 * {@code drop-base:} lines (the blackguard's, the ranger's), both of which are kept.
 *
 * <p>The synthetic cases cover the bounds C never checks in the struct itself: a chance of 0 and
 * of 100, a minimum of 0, equal bounds and a maximum of 99, the most {@code parse_monster_drop}
 * accepts. The members are private with no getters, so they are read by reflection.
 *
 * <p>Class MonsterDropTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class MonsterDropTest {

    /**
     * Loads the real game data once, so that the object kinds and monster races exist.
     *
     * @throws IOException if the game data cannot be read
     */
    @BeforeAll
    static void bootstrap() throws IOException {
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> uiEntrySpecs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            uiEntrySpecs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(uiEntrySpecs));
        Core core = new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
        GameConstants.init(core);
    }

    /**
     * Clears the object tables the bootstrap loaded.
     */
    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
    }

    /**
     * Reads a private member of a drop.
     *
     * @param drop the drop to inspect
     * @param name the field's name
     * @return the field's current value
     * @throws Exception if the field cannot be reached
     */
    private static Object read(MonsterDrop drop, String name) throws Exception {
        Field field = MonsterDrop.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(drop);
    }

    /**
     * Reads a race's private drop list.
     *
     * @param name the race's name
     * @return its drops, in the order the Java list holds them
     * @throws Exception if the list cannot be reached
     */
    @SuppressWarnings("unchecked")
    private static List<MonsterDrop> dropsOf(String name) throws Exception {
        MonsterRace race = MonsterRegistry.lookupMonsterRace(name);
        assertNotNull(race, name);
        Field field = MonsterRace.class.getDeclaredField("drops");
        field.setAccessible(true);
        return (List<MonsterDrop>) field.get(race);
    }

    @Test
    @DisplayName("a specific drop keeps the kind and the four figures and is not a base drop")
    void specificConstructorStoresEverything() throws Exception {
        ObjectKind phaseDoor = ObjectRegistry.lookupObjectKind(TValue.TV_SCROLL, "Phase Door");
        assertNotNull(phaseDoor);

        MonsterDrop drop = new MonsterDrop(TValue.TV_SCROLL, phaseDoor, 35, 1, 3);

        assertFalse(drop.isBase());
        assertSame(phaseDoor, read(drop, "kind"));
        assertEquals(TValue.TV_SCROLL, read(drop, "tVal"));
        assertEquals(35, read(drop, "percentChance"));
        assertEquals(1, read(drop, "min"));
        assertEquals(3, read(drop, "max"));
    }

    @Test
    @DisplayName("a base drop has a null kind, is a base drop, and keeps the tval and the three figures")
    void baseConstructorLeavesTheKindNull() throws Exception {
        MonsterDrop drop = new MonsterDrop(TValue.TV_MUSHROOM, 100, 1, 2);

        assertTrue(drop.isBase());
        assertNull(read(drop, "kind"));
        assertEquals(TValue.TV_MUSHROOM, read(drop, "tVal"));
        assertEquals(100, read(drop, "percentChance"));
        assertEquals(1, read(drop, "min"));
        assertEquals(2, read(drop, "max"));
    }

    @Test
    @DisplayName("isBase follows the kind alone, as C's drop->kind test does, whatever the tval")
    void isBaseIsDecidedByTheKindAlone() {
        ObjectKind phaseDoor = ObjectRegistry.lookupObjectKind(TValue.TV_SCROLL, "Phase Door");

        assertFalse(new MonsterDrop(TValue.TV_SCROLL, phaseDoor, 50, 1, 1).isBase());
        assertTrue(new MonsterDrop(TValue.TV_SCROLL, 50, 1, 1).isBase());
        // A specific-drop constructor handed a null kind is indistinguishable from a base drop,
        // exactly as C's NULL kind makes the record one.
        assertTrue(new MonsterDrop(TValue.TV_SCROLL, null, 50, 1, 1).isBase());
    }

    @Test
    @DisplayName("the figures are stored as given at the bounds, with no clamping or reordering")
    void boundaryFiguresAreStoredVerbatim() throws Exception {
        int[][] cases = {
                // chance, min, max
                {0, 0, 0},      // never drops, zero quantity
                {100, 1, 1},    // always drops, equal bounds: randint0(0) + 1 in C
                {100, 0, 99},   // widest range parse_monster_drop accepts
                {101, 5, 5},    // above 100: C stores it, randint0(100) < 101 is always true
                {50, 7, 3},     // min above max: C stores it too, the draw is the caller's problem
        };
        for (int[] c : cases) {
            MonsterDrop drop = new MonsterDrop(TValue.TV_POTION, c[0], c[1], c[2]);
            String label = c[0] + ":" + c[1] + ":" + c[2];
            assertEquals(c[0], read(drop, "percentChance"), label);
            assertEquals(c[1], read(drop, "min"), label);
            assertEquals(c[2], read(drop, "max"), label);
        }
    }

    @Test
    @DisplayName("the cutpurse's drop:scroll:Phase Door:35:1:3 loads as a specific drop")
    void cutpurseDropLoadsAsASpecificDrop() throws Exception {
        List<MonsterDrop> drops = dropsOf("cutpurse");

        assertEquals(1, drops.size());
        MonsterDrop drop = drops.getFirst();
        assertFalse(drop.isBase());
        assertSame(ObjectRegistry.lookupObjectKind(TValue.TV_SCROLL, "Phase Door"), read(drop, "kind"));
        assertEquals(TValue.TV_SCROLL, read(drop, "tVal"));
        assertEquals(35, read(drop, "percentChance"));
        assertEquals(1, read(drop, "min"));
        assertEquals(3, read(drop, "max"));
    }

    @Test
    @DisplayName("the rogue's drop:scroll:Phase Door:70:2:6 loads as a specific drop")
    void rogueDropLoadsAsASpecificDrop() throws Exception {
        List<MonsterDrop> drops = dropsOf("rogue");

        assertEquals(1, drops.size());
        MonsterDrop drop = drops.getFirst();
        assertFalse(drop.isBase());
        assertSame(ObjectRegistry.lookupObjectKind(TValue.TV_SCROLL, "Phase Door"), read(drop, "kind"));
        assertEquals(70, read(drop, "percentChance"));
        assertEquals(2, read(drop, "min"));
        assertEquals(6, read(drop, "max"));
    }

    @Test
    @DisplayName("Farmer Maggot's drop-base:mushroom:100:1:2 loads as a base drop")
    void farmerMaggotDropLoadsAsABaseDrop() throws Exception {
        List<MonsterDrop> drops = dropsOf("Farmer Maggot");

        assertEquals(1, drops.size());
        MonsterDrop drop = drops.getFirst();
        assertTrue(drop.isBase());
        assertNull(read(drop, "kind"));
        assertEquals(TValue.TV_MUSHROOM, read(drop, "tVal"));
        assertEquals(100, read(drop, "percentChance"));
        assertEquals(1, read(drop, "min"));
        assertEquals(2, read(drop, "max"));
    }

    @Test
    @DisplayName("a race with two identical drop-base lines keeps both, as C's two list entries")
    void identicalDropBaseLinesAreBothKept() throws Exception {
        // monster.txt gives the blackguard drop-base:shadow book:8:1:1 twice
        List<MonsterDrop> drops = dropsOf("blackguard");

        assertEquals(2, drops.size());
        for (MonsterDrop drop : drops) {
            assertTrue(drop.isBase());
            assertEquals(TValue.TV_SHADOW_BOOK, read(drop, "tVal"));
            assertEquals(8, read(drop, "percentChance"));
            assertEquals(1, read(drop, "min"));
            assertEquals(1, read(drop, "max"));
        }
        assertNotSame(drops.get(0), drops.get(1));
    }

    @Test
    @DisplayName("monster.txt's 77 drop lines load as 2 specific and 75 base drops")
    void everyDropLineInTheFileIsLoaded() throws Exception {
        int specific = 0;
        int base = 0;
        for (MonsterRace race : MonsterRegistry.getMonsterRaces()) {
            Field field = MonsterRace.class.getDeclaredField("drops");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<MonsterDrop> drops = (List<MonsterDrop>) field.get(race);
            for (MonsterDrop drop : drops) {
                if (drop.isBase()) {
                    base++;
                } else {
                    specific++;
                }
            }
        }

        assertEquals(2, specific);
        assertEquals(75, base);
    }
}
