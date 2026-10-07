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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.backend.parser.ObjectPropertyReader;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.ui.entry.reader.UIEntryReader;
import uk.co.jackoftradesltd.frontend.ui.entrybase.reader.UIEntryBaseReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.CurseFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Round 2 additions for Part C of {@link ItemObject} - the value methods and the power driver -
 * covering what {@link ItemObjectValueTest} and {@link ItemObjectPowerCursesTest} leave open.
 *
 * <p>Two things are pinned. First, the split in {@code curse_power} between ordinary and
 * weight-affecting curses: a {@code MULTIPLY_WEIGHT} curse of exactly 100 means "no change", so it
 * is priced in the first pass like an additive curse of zero, and only the second pass sees one
 * that is not. Second, the visiting order: C walks {@code obj->curses} by index, and the port's
 * {@code TreeMap} under {@code CURSE_ORDER} must give the same answer however the curses were laid
 * on the item.
 *
 * <p>The item is a cloak, whose power with no base armour class is its {@code to_a}, so every
 * figure is plain arithmetic worked out from {@code curse_power} in {@code obj-power.c}.
 *
 * <p>Class ItemObjectPartCRound2Test coded on 261007, commented in full on 261007.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectPartCRound2Test {

    private static Object savedRenderers;
    private static Object savedUiBases;
    private static Object savedUiEntries;
    private static Object savedProperties;
    private static Object savedFlagSets;
    private static Object savedElementSets;
    private static Object savedElementPowers;
    private static Object savedCurses;
    private static ObjectKind cloakKind;
    private Player savedPlayer;

    /**
     * Loads the real object-property data and installs the power tables the calculation reads.
     */
    @BeforeAll
    static void seedRegistries() throws Exception {
        savedRenderers = ItemFixture.setStatic(UIRegistry.class, "uiEntryRenderers",
                new UIEntryRendererReader().parseWithResults("lib/gamedata/ui_entry_renderer.txt").items());
        savedUiBases = ItemFixture.setStatic(UIRegistry.class, "uiEntryBases",
                new UIEntryBaseReader().parseWithResults("lib/gamedata/ui_entry_base.txt").items());
        savedUiEntries = ItemFixture.setStatic(UIRegistry.class, "uiEntries",
                new UIEntryReader().parseWithResults("lib/gamedata/ui_entry.txt").items());
        savedProperties = ItemFixture.setStatic(ObjectRegistry.class, "objectProperties",
                new ObjectPropertyReader().parseWithResults("lib/gamedata/object_property.txt").items());

        Map<ObjectFlagType, FlagSet> flagSets = new HashMap<>();
        flagSets.put(ObjectFlagType.OFT_SUST, new FlagSet(ObjectFlagType.OFT_SUST, 1, 10, 5, 0, "sustains"));
        flagSets.put(ObjectFlagType.OFT_PROT, new FlagSet(ObjectFlagType.OFT_PROT, 3, 15, 4, 0, "protections"));
        flagSets.put(ObjectFlagType.OFT_MISC, new FlagSet(ObjectFlagType.OFT_MISC, 1, 25, 8, 0, "misc abilities"));
        savedFlagSets = ItemFixture.setStatic(ObjectRegistry.class, "flagSets", flagSets);

        savedElementSets = ItemFixture.setStatic(ObjectRegistry.class, "elementSets", new ArrayList<ElementSet>());
        savedElementPowers = ItemFixture.setStatic(ObjectRegistry.class, "elementPowers",
                new ArrayList<ElementPowers>());
        savedCurses = ItemFixture.setStatic(ObjectRegistry.class, "curses", new ArrayList<Curse>());

        cloakKind = ItemFixture.loadedKind(TValue.TV_CLOAK, "cloak", 40);
    }

    /**
     * Puts the registries back.
     */
    @AfterAll
    static void restoreRegistries() {
        ItemFixture.setStatic(UIRegistry.class, "uiEntryRenderers", savedRenderers);
        ItemFixture.setStatic(UIRegistry.class, "uiEntryBases", savedUiBases);
        ItemFixture.setStatic(UIRegistry.class, "uiEntries", savedUiEntries);
        ItemFixture.setStatic(ObjectRegistry.class, "objectProperties", savedProperties);
        ItemFixture.setStatic(ObjectRegistry.class, "flagSets", savedFlagSets);
        ItemFixture.setStatic(ObjectRegistry.class, "elementSets", savedElementSets);
        ItemFixture.setStatic(ObjectRegistry.class, "elementPowers", savedElementPowers);
        ItemFixture.setStatic(ObjectRegistry.class, "curses", savedCurses);
    }

    private static Curse curse(int index, int weight, boolean multiply, int toAC) {
        Flag<ObjectFlag> objectFlags = new Flag<>(ObjectFlag.class);
        if (multiply) {
            objectFlags.set(List.of(ObjectFlag.OF_MULTIPLY_WEIGHT));
        }
        return CurseFixture.curse("curse" + index, List.of(), weight, null, objectFlags,
                new HashMap<>(), new HashMap<>(), 0, 0, toAC, List.of(),
                new Flag<>(ObjectFlag.class), "", "", index);
    }

    /**
     * A cloak of weight 100 and the given to-armour bonus carrying the given curses, laid in the
     * order given, all registered.
     *
     * @param toAC the to-armour bonus
     * @param laid alternating curse and power
     * @return the cloak
     */
    private static ItemObject cloak(int toAC, Object... laid) {
        ItemFixture fixture = ItemFixture.item(TValue.TV_CLOAK).kind(cloakKind);
        List<Curse> registered = new ArrayList<>(ObjectRegistry.getCurses());
        for (int i = 0; i < laid.length; i += 2) {
            Curse laidCurse = (Curse) laid[i];
            fixture.curse(laidCurse, new CurseData((int) laid[i + 1], 0));
            if (!registered.contains(laidCurse)) {
                registered.add(laidCurse);
            }
        }
        ItemFixture.setStatic(ObjectRegistry.class, "curses", registered);
        ItemObject cloak = fixture.build();
        set(cloak, "weight", 100);
        set(cloak, "toAC", toAC);
        return cloak;
    }

    private static int cursePower(ItemObject item, int power) throws Exception {
        Method method = ItemObject.class.getDeclaredMethod("cursePower", int.class, boolean.class, String.class);
        method.setAccessible(true);
        try {
            return (int) method.invoke(item, power, false, null);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    /**
     * Installs a player and empties the registry's curse list.
     */
    @BeforeEach
    void installPlayer() {
        savedPlayer = GameState.getPlayer();
        GameState.setPlayer(new Player());
        ItemFixture.setStatic(ObjectRegistry.class, "curses", new ArrayList<Curse>());
    }

    /**
     * Puts the game's player back.
     */
    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    /**
     * A {@code MULTIPLY_WEIGHT} curse at exactly 100 is not weight-affecting, so the first pass
     * prices it: its own power of zero, less a tenth of strength 40, is -4, and the second pass
     * never runs.
     */
    @Test
    @DisplayName("a multiply-weight curse of 100 is priced as an ordinary curse")
    void multiplyHundredIsOrdinary() throws Exception {
        assertEquals(96, cursePower(cloak(0, curse(1, 100, true, 0), 40), 100));
    }

    /**
     * An ordinary multiply-100 curse carrying to_a of -3 and a weight-affecting curse of +30 with
     * to_a of -4, both at strength 100 except the first at 40.
     *
     * <p>First pass: the ordinary curse is {@code -3 - 40/10 = -7}. Second pass: every curse is
     * applied to the copy, so to_a is {@code 5 - 3 - 4 = -2}, and the weight goes 100 times 100
     * percent, then plus 30, for 130; the weight term is {@code (100 - 130) / 50}, which truncates
     * to zero, so that copy's power is -2. Holding the heavy curse back leaves to_a 2 and weight 100,
     * power 2. The gap is -4, scaled by strength 100 over 100, stays -4. Total {@code 100 - 7 - 4}.
     */
    @Test
    @DisplayName("both passes count when a multiply-100 curse sits beside a weight curse")
    void bothPasses() throws Exception {
        Curse ordinary = curse(1, 100, true, -3);
        Curse heavy = curse(2, 30, false, -4);

        assertEquals(89, cursePower(cloak(5, ordinary, 40, heavy, 100), 100));
    }

    /**
     * C walks the curses by index, so laying them on the item in the other order cannot change the
     * answer. Two weight-affecting curses with to_a of -2 (+30 weight) and -3 (times 1.5) on a
     * to_a of 5, at strength 100, price at 15 from a base of 20 either way.
     */
    @Test
    @DisplayName("the order the curses were laid in does not change the price")
    void layingOrderIrrelevant() throws Exception {
        Curse a = curse(1, 30, false, -2);
        Curse b = curse(2, 150, true, -3);

        assertEquals(15, cursePower(cloak(5, a, 100, b, 100), 20));
        assertEquals(15, cursePower(cloak(5, b, 100, a, 100), 20));
    }
}
