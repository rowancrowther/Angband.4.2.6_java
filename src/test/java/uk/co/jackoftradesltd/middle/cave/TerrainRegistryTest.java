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

package uk.co.jackoftradesltd.middle.cave;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link TerrainRegistry} against the C globals it stands in for: {@code z_info->trap_max}, set by
 * {@code finish_parse_trap()} in {@code init.c} to the number of trap records, {@code f_info} (indexed by
 * {@code FEAT_} constant), and the index-0 skip in {@code lookup_trap()} in {@code trap.c}.
 *
 * <p>The description matching of {@code lookup_trap()} itself is covered in {@code TrapKindTest}; this class pins what
 * the registry adds to it: the count, the lists held, and the feature lookup.
 *
 * <p>Class TerrainRegistryTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class TerrainRegistryTest {

    private List<TrapKind> savedTraps;
    private List<Feature> savedFeatures;

    private static TrapKind kind(String name, String desc, int index) {
        return new TrapKind(name, "", desc, "", "", "", "", index, null, 0, 0, 0, null,
                null, null, new ArrayList<>(), new ArrayList<>());
    }

    private static Feature feature(TerrainFlags code, String name) {
        return new Feature(code, name, "", null, 0, 0, new Flag<>(TerrainFeatureFlags.class), null,
                null, null, null, null, null, null, null, null, 0);
    }

    @BeforeEach
    void saveRegistry() {
        savedTraps = new ArrayList<>(TerrainRegistry.getTrapInfo());
        try {
            savedFeatures = new ArrayList<>(TerrainRegistry.getFeatures());
        } catch (NullPointerException notLoaded) {
            savedFeatures = null;
        }
    }

    @AfterEach
    void restoreRegistry() {
        TerrainRegistry.setTrapInfo(savedTraps);
        TerrainRegistry.setFeatures(savedFeatures);
    }

    @Test
    @DisplayName("trapMax is the number of trap records, counting the no trap kind at index 0")
    void trapMaxCountsEveryRecord() {
        TerrainRegistry.setTrapInfo(new ArrayList<>(List.of(
                kind("no trap", "no trap", 0),
                kind("glyph of warding", "glyph of warding", 1),
                kind("web", "web", 2))));
        assertEquals(3, TerrainRegistry.getTrapMax());
    }

    @Test
    @DisplayName("trapMax follows the latest list given to setTrapInfo, down to zero for an empty one")
    void trapMaxFollowsSetTrapInfo() {
        TerrainRegistry.setTrapInfo(new ArrayList<>(List.of(kind("no trap", "no trap", 0))));
        assertEquals(1, TerrainRegistry.getTrapMax());
        TerrainRegistry.setTrapInfo(new ArrayList<>());
        assertEquals(0, TerrainRegistry.getTrapMax());
    }

    @Test
    @DisplayName("getTrapInfo and getTrapKinds both expose the list in file order, read-only")
    void trapListsAreReadOnlyViews() {
        TrapKind noTrap = kind("no trap", "no trap", 0);
        TrapKind web = kind("web", "web", 1);
        TerrainRegistry.setTrapInfo(new ArrayList<>(List.of(noTrap, web)));
        assertAll(
                () -> assertEquals(List.of(noTrap, web), TerrainRegistry.getTrapInfo()),
                () -> assertEquals(List.of(noTrap, web), TerrainRegistry.getTrapKinds()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> TerrainRegistry.getTrapInfo().clear()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> TerrainRegistry.getTrapKinds().clear()));
    }

    @Test
    @DisplayName("lookupTrap skips the kind at index 0 and finds the one after it")
    void lookupTrapSkipsIndexZero() {
        TrapKind noTrap = kind("no trap", "no trap", 0);
        TrapKind trapDoor = kind("trap door", "trap door", 1);
        TerrainRegistry.setTrapInfo(new ArrayList<>(List.of(noTrap, trapDoor)));
        assertAll(
                () -> assertNull(TerrainRegistry.lookupTrap("no trap")),
                () -> assertSame(trapDoor, TerrainRegistry.lookupTrap("trap")),
                () -> assertSame(trapDoor, TerrainRegistry.lookupTrap("")),
                () -> assertSame(trapDoor, TrapKind.lookupTrap("trap door")));
    }

    @Test
    @DisplayName("lookupFeature returns the feature whose code matches, or null for a code nobody has")
    void lookupFeatureByCode() {
        Feature none = feature(TerrainFlags.FEAT_NONE, "unknown grid");
        Feature floor = feature(TerrainFlags.FEAT_FLOOR, "open floor");
        TerrainRegistry.setFeatures(List.of(none, floor));
        assertAll(
                () -> assertSame(none, TerrainRegistry.lookupFeature(TerrainFlags.FEAT_NONE)),
                () -> assertSame(floor, TerrainRegistry.lookupFeature(TerrainFlags.FEAT_FLOOR)),
                () -> assertNull(TerrainRegistry.lookupFeature(TerrainFlags.FEAT_GRANITE)));
    }

    @Test
    @DisplayName("lookupFeature before the features are loaded is an IllegalStateException")
    void lookupFeatureBeforeLoad() {
        TerrainRegistry.setFeatures(null);
        assertThrows(IllegalStateException.class, () -> TerrainRegistry.lookupFeature(TerrainFlags.FEAT_FLOOR));
    }

    @Test
    @DisplayName("getFeatures returns the features in order, read-only")
    void featuresAreAReadOnlyView() {
        Feature none = feature(TerrainFlags.FEAT_NONE, "unknown grid");
        Feature floor = feature(TerrainFlags.FEAT_FLOOR, "open floor");
        TerrainRegistry.setFeatures(List.of(none, floor));
        assertAll(
                () -> assertEquals(List.of(none, floor), TerrainRegistry.getFeatures()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> TerrainRegistry.getFeatures().clear()));
    }
}
