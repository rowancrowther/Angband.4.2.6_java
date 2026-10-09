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
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.UIEntryValueRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ResType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the table that {@link ElementSet} rows are built into at start-up against C's
 * {@code element_sets[]} in {@code obj-power.c}.
 *
 * <p>The other element-power suites build their own rows in a fixture, so none of them would notice
 * if the table {@code GameConstants.init} registers drifted from C. This one runs the real
 * {@code init} and reads the rows back from {@link ObjectRegistry#elementSets}. The expected figures
 * are C's initializer, read from {@code obj-power.c}, not from the Java.
 *
 * <p>C's rows, in order:
 * <pre>
 * { T_LRES, 3, 6, INHIBIT_POWER, 4, 0, "immunities" }
 * { T_LRES, 1, 1, 10,            4, 0, "low resists" }
 * { T_HRES, 1, 2, 10,            9, 0, "high resists" }
 * </pre>
 *
 * <p>Class ElementSetTest coded on 261009, commented in full on 261009.
 */
class ElementSetTest {

    /**
     * Runs the real start-up load, as {@code GameConstantsInitUIBindingTest} does: the front end's
     * entries have to be loaded and sent before {@code GameConstants.init} reaches the point where it
     * waits for them.
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
     * {@code init} fills {@link ObjectRegistry} and {@link UIEntryValueRegistry} in place; put both
     * back to the empty baseline so this heavy load does not leak into order-sensitive suites.
     */
    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
        UIEntryValueRegistry.clearEntryBindings();
    }

    /**
     * Asserts every field of one registered row against C's initializer.
     */
    private static void assertRow(ElementSet row, ResType type, int resLevel, int factor, int bonus, int size,
                                  String description) {
        assertNotNull(row);
        assertEquals(type, row.getType(), description + " type");
        assertEquals(resLevel, row.getResLevel(), description + " res_level");
        assertEquals(factor, row.getFactor(), description + " factor");
        assertEquals(bonus, row.getBonus(), description + " bonus");
        assertEquals(size, row.getSize(), description + " size");
        assertEquals(0, row.getCount(), description + " count starts at zero");
        assertEquals(description, row.getDescription());
    }

    /**
     * C has three rows and the loop in {@code element_power} walks all of them, so a fourth or a
     * missing one changes every price.
     */
    @Test
    @DisplayName("the table holds exactly C's three rows")
    void threeRows() {
        assertEquals(3, ObjectRegistry.elementSets.size());
    }

    /**
     * The immunities row demands level 3, multiplies by 6, and its full-set bonus is the refusal
     * figure rather than a price. {@code INHIBIT_POWER} is 20000 in {@code obj-power.h}.
     */
    @Test
    @DisplayName("row 0 is immunities: level 3, factor 6, INHIBIT_POWER, four to a set")
    void immunitiesRow() {
        assertEquals(20000, ObjectRegistry.INHIBIT_POWER);
        assertRow(ObjectRegistry.elementSets.get(0), ResType.T_LRES, 3, 6, 20000, 4, "immunities");
    }

    /**
     * The low resists row shares the immunities row's element group and size, but starts at level 1,
     * so an immunity counts towards it as well.
     */
    @Test
    @DisplayName("row 1 is low resists: level 1, factor 1, bonus 10, four to a set")
    void lowResistsRow() {
        assertRow(ObjectRegistry.elementSets.get(1), ResType.T_LRES, 1, 1, 10, 4, "low resists");
    }

    /**
     * The only row over the high group, and the only one with nine to a set.
     */
    @Test
    @DisplayName("row 2 is high resists: level 1, factor 2, bonus 10, nine to a set")
    void highResistsRow() {
        assertRow(ObjectRegistry.elementSets.get(2), ResType.T_HRES, 1, 2, 10, 9, "high resists");
    }

    /**
     * The rows are working state shared by reference, as C's static table is: a count written
     * through one lookup is read back through another. This is why two power calculations must not
     * interleave. The count is put back to zero so later tests see the registered baseline.
     */
    @Test
    @DisplayName("the registered rows are shared state, not copies")
    void rowsAreShared() {
        ElementSet row = ObjectRegistry.elementSets.get(1);
        try {
            row.setCount(3);

            assertSame(row, ObjectRegistry.elementSets.get(1));
            assertEquals(3, ObjectRegistry.elementSets.get(1).getCount());
        } finally {
            row.setCount(0);
        }
    }
}
