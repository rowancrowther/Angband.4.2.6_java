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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins {@code hearing} as C's {@code parse_monster_hearing} ({@code mon-init.c}) leaves it: the
 * {@code hearing:} value times {@code z_info->max_sight}, divided by 20, in integer arithmetic.
 *
 * <p>C's own comment says it assumes {@code max_sight} is 20, which is why the shipped file cannot tell
 * the formula from a pass-through. These tests swap {@code player:max-sight} for other values after
 * {@code GameConstants.init} has run, then parse a one-race file, and restore the loaded constants
 * afterwards. The expected values are worked out by hand from the C expression, not from the Java.
 *
 * @author Rowan Crowther
 */
class MonsterAssemblerHearingTest {

    private static GameConstantsData loaded;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void bootstrap() throws Exception {
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
        loaded = constants();
    }

    @AfterAll
    static void cleanup() throws Exception {
        setConstants(loaded);
        ObjectRegistry.reset();
    }

    private static Field dataField() throws Exception {
        Field f = GameConstants.class.getDeclaredField("data");
        f.setAccessible(true);
        return f;
    }

    private static GameConstantsData constants() throws Exception {
        return (GameConstantsData) dataField().get(null);
    }

    private static void setConstants(GameConstantsData data) throws Exception {
        dataField().set(null, data);
    }

    /**
     * Rebuilds the loaded constants with only {@code player:max-sight} changed. The record is immutable,
     * so every component is read back and handed to the canonical constructor.
     */
    private static GameConstantsData withMaxSight(int maxSight) throws Exception {
        RecordComponent[] components = GameConstantsData.class.getRecordComponents();
        Class<?>[] types = new Class<?>[components.length];
        Object[] values = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            types[i] = components[i].getType();
            values[i] = components[i].getAccessor().invoke(loaded);
            if (values[i] instanceof PlayerData p) {
                values[i] = new PlayerData(maxSight, p.maxRange(), p.startGold(), p.foodValue());
            }
        }
        return GameConstantsData.class.getDeclaredConstructor(types).newInstance(values);
    }

    private static int hearingField(MonsterRace race) throws Exception {
        Field f = MonsterRace.class.getDeclaredField("hearing");
        f.setAccessible(true);
        return f.getInt(race);
    }

    /**
     * Parses a one-race file whose only {@code hearing:} line is {@code value}, with {@code player:max-sight}
     * set to {@code maxSight} for the duration, and returns the stored {@code hearing}.
     */
    private int hearingFor(int value, int maxSight) throws Exception {
        setConstants(withMaxSight(maxSight));
        try {
            Path file = tempDir.resolve("hearing.txt");
            Files.writeString(file, "record-count:1\nname:test\nbase:townsfolk\ncolor:w\nglyph:t\nhearing:"
                    + value + "\n");
            ParseResult<MonsterRace> r = new MonsterReader().parseWithResults(file.toString());
            assertTrue(r.errors().isEmpty(), r.errors().toString());
            return hearingField(r.items().get(0));
        } finally {
            setConstants(loaded);
        }
    }

    @Test
    @DisplayName("the constants swap is in effect while parsing and undone afterwards")
    void swapIsScopedToTheParse() throws Exception {
        assertEquals(20, GameConstants.getPlayerMaxSight(), "shipped constants.txt");
        setConstants(withMaxSight(25));
        assertEquals(25, GameConstants.getPlayerMaxSight());
        setConstants(loaded);
        assertEquals(20, GameConstants.getPlayerMaxSight());
    }

    @Test
    @DisplayName("max_sight 20 leaves hearing as written")
    void standardMaxSightIsAPassThrough() throws Exception {
        assertEquals(30, hearingFor(30, 20));
        assertEquals(4, hearingFor(4, 20));
    }

    @Test
    @DisplayName("hearing:30 at max_sight 25 is 37, not 37.5 or 36")
    void thirtyAtTwentyFiveIsThirtySeven() throws Exception {
        assertEquals(37, hearingFor(30, 25));
    }

    @Test
    @DisplayName("a smaller max_sight shrinks hearing: 30 at 10 is 15")
    void smallerMaxSightShrinksHearing() throws Exception {
        assertEquals(15, hearingFor(30, 10));
    }

    @Test
    @DisplayName("multiply before dividing: 3 at 25 is 3 (75 / 20), not 0 (3 / 20 * 25)")
    void multipliesBeforeDividing() throws Exception {
        assertEquals(3, hearingFor(3, 25));
    }

    @Test
    @DisplayName("integer division truncates: 6 at 15 is 4 (90 / 20 = 4.5), 1 at 25 is 1 (25 / 20)")
    void truncatesLikeC() throws Exception {
        assertEquals(4, hearingFor(6, 15));
        assertEquals(1, hearingFor(1, 25));
    }
}
