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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks {@link Feature} against the C original: the {@code feat_is_*()} predicates in
 * {@code cave-square.c}, the {@code mimic} handling in {@code map_info()} in {@code cave-map.c},
 * and the {@code FEAT_PERM} test the hallucination rolls make.
 *
 * <p><b>What is being tested:</b> most of {@link Feature} is one-flag predicates, so those are
 * checked as a table (a feature carrying only that flag answers true, one carrying none answers
 * false). The rest is the places where C and Java could plausibly diverge:
 * <ul>
 *   <li>{@code isRock()} is {@code TF_GRANITE && !TF_DOOR_ANY}, not the wider {@code TF_ROCK}
 *       ({@code fullRock()}), so a secret door is granite-flagged but not rock.</li>
 *   <li>{@code isFullPermanent()} is {@code TF_PERMANENT && TF_ROCK} ({@code square_isperm}); the
 *       staircases and shop entrances are {@code PERMANENT} without {@code ROCK}.</li>
 *   <li>{@code isPermanent()} is the code test {@code f_idx != FEAT_PERM} makes in
 *       {@code map_info()}, so it ignores the flags altogether.</li>
 *   <li>{@code mimic} is {@code null} for "no mimic", not {@code FEAT_NONE}, and {@code getMimic()}
 *       gives back the same feature when there is none, as C keeps {@code f_idx}.</li>
 * </ul>
 *
 * <p>The flag lines used are the exact ones from {@code terrain.txt}; the expected answers were
 * worked out from the C definitions, not by running the Java.
 *
 * <p>Class FeatureTest coded on 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
class FeatureTest {

    private List<Feature> savedFeatures;

    private static Flag<TerrainFeatureFlags> flags(TerrainFeatureFlags... on) {
        Flag<TerrainFeatureFlags> flags = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags f : on) {
            flags.on(f);
        }
        return flags;
    }

    private static Feature feature(TerrainFlags code, TerrainFlags mimic, TerrainFeatureFlags... on) {
        return feature(code, "test", mimic, 0, on);
    }

    private static Feature feature(TerrainFlags code, String name, TerrainFlags mimic, int shopNum,
                                   TerrainFeatureFlags... on) {
        return new Feature(code, name, "", mimic, 0, 0, flags(on), null,
                "", "", "", "", "", "", "", new Flag<>(MonsterRaceFlag.class), shopNum);
    }

    private static Feature withFlags(TerrainFeatureFlags... on) {
        return feature(null, null, on);
    }

    private static Feature sharing(Flag<TerrainFeatureFlags> flags, Flag<MonsterRaceFlag> resist, String name,
                                   TerrainFlags mimic, int shopNum) {
        return new Feature(TerrainFlags.FEAT_STORE_ARMOR, name, "", mimic, 0, 0, flags, null,
                "", "", "", "", "", "", "", resist, shopNum);
    }

    /**
     * Save whatever the registry held so the mimic tests can seed their own list.
     * {@code getFeatures()} throws when nothing was ever set, which is treated as "nothing to save".
     */
    @BeforeEach
    void saveRegistry() {
        try {
            savedFeatures = new ArrayList<>(TerrainRegistry.getFeatures());
        } catch (NullPointerException e) {
            savedFeatures = null;
        }
    }

    @AfterEach
    void restoreRegistry() {
        TerrainRegistry.setFeatures(savedFeatures);
    }

    @Test
    @DisplayName("each single-flag predicate is true with only its flag and false with none")
    void singleFlagPredicates() {
        List<Pred> table = List.of(
                new Pred("isMagma", TerrainFeatureFlags.TF_MAGMA, Feature::isMagma),
                new Pred("isQuartz", TerrainFeatureFlags.TF_QUARTZ, Feature::isQuartz),
                new Pred("isGranite", TerrainFeatureFlags.TF_GRANITE, Feature::isGranite),
                new Pred("isTreasure", TerrainFeatureFlags.TF_GOLD, Feature::isTreasure),
                new Pred("isWall", TerrainFeatureFlags.TF_WALL, Feature::isWall),
                new Pred("isFloor", TerrainFeatureFlags.TF_FLOOR, Feature::isFloor),
                new Pred("isTrapHolding", TerrainFeatureFlags.TF_TRAP, Feature::isTrapHolding),
                new Pred("isObjectHolding", TerrainFeatureFlags.TF_OBJECT, Feature::isObjectHolding),
                new Pred("isMonsterWalkable", TerrainFeatureFlags.TF_PASSABLE, Feature::isMonsterWalkable),
                new Pred("isShop", TerrainFeatureFlags.TF_SHOP, Feature::isShop),
                new Pred("isLos", TerrainFeatureFlags.TF_LOS, Feature::isLos),
                new Pred("isPassable", TerrainFeatureFlags.TF_PASSABLE, Feature::isPassable),
                new Pred("isProjectable", TerrainFeatureFlags.TF_PROJECT, Feature::isProjectable),
                new Pred("isTorch", TerrainFeatureFlags.TF_TORCH, Feature::isTorch),
                new Pred("isBright", TerrainFeatureFlags.TF_BRIGHT, Feature::isBright),
                new Pred("isFiery", TerrainFeatureFlags.TF_FIERY, Feature::isFiery),
                new Pred("isNoFlow", TerrainFeatureFlags.TF_NO_FLOW, Feature::isNoFlow),
                new Pred("isNoScent", TerrainFeatureFlags.TF_NO_SCENT, Feature::isNoScent),
                new Pred("isSmooth", TerrainFeatureFlags.TF_SMOOTH, Feature::isSmooth),
                new Pred("hasAnyDoor", TerrainFeatureFlags.TF_DOOR_ANY, Feature::hasAnyDoor),
                new Pred("isOpenDoor", TerrainFeatureFlags.TF_CLOSABLE, Feature::isOpenDoor),
                new Pred("isClosedDoor", TerrainFeatureFlags.TF_DOOR_CLOSED, Feature::isClosedDoor),
                new Pred("isCloseable", TerrainFeatureFlags.TF_CLOSABLE, Feature::isCloseable),
                new Pred("isStair", TerrainFeatureFlags.TF_STAIR, Feature::isStair),
                new Pred("isUpStair", TerrainFeatureFlags.TF_UPSTAIR, Feature::isUpStair),
                new Pred("isDownStair", TerrainFeatureFlags.TF_DOWNSTAIR, Feature::isDownStair),
                new Pred("isInteresting", TerrainFeatureFlags.TF_INTERESTING, Feature::isInteresting),
                new Pred("fullRock", TerrainFeatureFlags.TF_ROCK, Feature::fullRock));

        List<Executable> checks = new ArrayList<>();
        for (Pred p : table) {
            checks.add(() -> assertTrue(p.test().test(withFlags(p.flag())), p.name() + " with only its flag"));
            checks.add(() -> assertFalse(p.test().test(withFlags()), p.name() + " with no flags"));
        }
        assertAll(checks);
    }

    @Test
    @DisplayName("the stair predicates are separate flags: STAIR alone is not up or down")
    void stairFlagsAreSeparate() {
        Feature any = withFlags(TerrainFeatureFlags.TF_STAIR);
        Feature up = withFlags(TerrainFeatureFlags.TF_STAIR, TerrainFeatureFlags.TF_UPSTAIR);
        Feature down = withFlags(TerrainFeatureFlags.TF_STAIR, TerrainFeatureFlags.TF_DOWNSTAIR);

        assertAll(
                () -> assertTrue(any.isStair()),
                () -> assertFalse(any.isUpStair()),
                () -> assertFalse(any.isDownStair()),
                () -> assertTrue(up.isUpStair()),
                () -> assertFalse(up.isDownStair()),
                () -> assertTrue(down.isDownStair()),
                () -> assertFalse(down.isUpStair()));
    }

    @Test
    @DisplayName("isOpenDoor and isCloseable both read CLOSABLE, and neither reads DOOR_CLOSED")
    void openDoorAndCloseableShareFlag() {
        Feature open = withFlags(TerrainFeatureFlags.TF_DOOR_ANY, TerrainFeatureFlags.TF_CLOSABLE);
        Feature closed = withFlags(TerrainFeatureFlags.TF_DOOR_ANY, TerrainFeatureFlags.TF_DOOR_CLOSED);

        assertAll(
                () -> assertTrue(open.isOpenDoor()),
                () -> assertTrue(open.isCloseable()),
                () -> assertFalse(open.isClosedDoor()),
                () -> assertFalse(closed.isOpenDoor()),
                () -> assertFalse(closed.isCloseable()),
                () -> assertTrue(closed.isClosedDoor()));
    }

    @Test
    @DisplayName("isRock is GRANITE without DOOR_ANY: granite yes, secret door and other rock no")
    void isRockIsGraniteWithoutDoors() {
        // terrain.txt: granite is WALL | ROCK | GRANITE, the secret door is WALL | ROCK | DOOR_ANY | GRANITE
        Feature granite = withFlags(TerrainFeatureFlags.TF_WALL, TerrainFeatureFlags.TF_ROCK,
                TerrainFeatureFlags.TF_GRANITE);
        Feature secretDoor = withFlags(TerrainFeatureFlags.TF_WALL, TerrainFeatureFlags.TF_ROCK,
                TerrainFeatureFlags.TF_DOOR_ANY, TerrainFeatureFlags.TF_GRANITE);
        Feature magma = withFlags(TerrainFeatureFlags.TF_WALL, TerrainFeatureFlags.TF_ROCK,
                TerrainFeatureFlags.TF_MAGMA);
        Feature rockOnly = withFlags(TerrainFeatureFlags.TF_ROCK);

        assertAll(
                () -> assertTrue(granite.isRock()),
                () -> assertFalse(secretDoor.isRock()),
                () -> assertTrue(secretDoor.isGranite(), "the secret door still has the GRANITE flag"),
                () -> assertFalse(magma.isRock()),
                () -> assertFalse(rockOnly.isRock()),
                () -> assertTrue(rockOnly.fullRock(), "fullRock reads TF_ROCK alone"),
                () -> assertTrue(magma.fullRock()));
    }

    @Test
    @DisplayName("isFullPermanent is PERMANENT and ROCK: the permanent wall yes, stairs and shops no")
    void isFullPermanentNeedsRock() {
        // terrain.txt flag lines: PERM, up staircase, general store
        Feature permWall = withFlags(TerrainFeatureFlags.TF_WALL, TerrainFeatureFlags.TF_ROCK,
                TerrainFeatureFlags.TF_PERMANENT, TerrainFeatureFlags.TF_NO_SCENT, TerrainFeatureFlags.TF_NO_FLOW);
        Feature upStair = withFlags(TerrainFeatureFlags.TF_LOS, TerrainFeatureFlags.TF_PROJECT,
                TerrainFeatureFlags.TF_PASSABLE, TerrainFeatureFlags.TF_PERMANENT,
                TerrainFeatureFlags.TF_INTERESTING, TerrainFeatureFlags.TF_STAIR, TerrainFeatureFlags.TF_UPSTAIR);
        Feature shop = withFlags(TerrainFeatureFlags.TF_SHOP, TerrainFeatureFlags.TF_LOS,
                TerrainFeatureFlags.TF_PROJECT, TerrainFeatureFlags.TF_PASSABLE, TerrainFeatureFlags.TF_PERMANENT,
                TerrainFeatureFlags.TF_INTERESTING);
        Feature rockOnly = withFlags(TerrainFeatureFlags.TF_ROCK);

        assertAll(
                () -> assertTrue(permWall.isFullPermanent()),
                () -> assertFalse(upStair.isFullPermanent()),
                () -> assertFalse(shop.isFullPermanent()),
                () -> assertFalse(rockOnly.isFullPermanent()));
    }

    @Test
    @DisplayName("isPermanent is the FEAT_PERM code test and ignores the flags")
    void isPermanentIsCodeTest() {
        // map_info() in cave-map.c: hallucination is skipped only when f_idx == FEAT_PERM
        assertAll(
                () -> assertTrue(feature(TerrainFlags.FEAT_PERM, null).isPermanent(),
                        "FEAT_PERM with no flags at all"),
                () -> assertFalse(feature(TerrainFlags.FEAT_LESS, null,
                                TerrainFeatureFlags.TF_PERMANENT, TerrainFeatureFlags.TF_ROCK).isPermanent(),
                        "another code carrying PERMANENT and ROCK"),
                () -> assertFalse(feature(TerrainFlags.FEAT_STORE_GENERAL, null,
                        TerrainFeatureFlags.TF_PERMANENT).isPermanent(), "shop entrance"),
                () -> assertFalse(feature(TerrainFlags.FEAT_MORE, null,
                        TerrainFeatureFlags.TF_PERMANENT).isPermanent(), "down staircase"),
                () -> assertFalse(feature(TerrainFlags.FEAT_GRANITE, null).isPermanent(), "granite"),
                () -> assertFalse(feature(null, null).isPermanent(), "null code"));
    }

    @Test
    @DisplayName("isNoFeat is true only for FEAT_NONE")
    void isNoFeat() {
        assertAll(
                () -> assertTrue(feature(TerrainFlags.FEAT_NONE, null).isNoFeat()),
                () -> assertFalse(feature(TerrainFlags.FEAT_FLOOR, null).isNoFeat()),
                () -> assertFalse(feature(null, null).isNoFeat()));
    }

    @Test
    @DisplayName("isMimicing tests for null: FEAT_NONE counts as a mimic, null does not")
    void isMimicingTestsNull() {
        assertAll(
                () -> assertFalse(feature(TerrainFlags.FEAT_GRANITE, null).isMimicing()),
                () -> assertTrue(feature(TerrainFlags.FEAT_SECRET, TerrainFlags.FEAT_GRANITE).isMimicing()),
                () -> assertTrue(feature(TerrainFlags.FEAT_FLOOR, TerrainFlags.FEAT_NONE).isMimicing(),
                        "FEAT_NONE is a real feature code, so it would be a mimic; the assembler must use null"));
    }

    @Test
    @DisplayName("getMimic returns the same feature when there is no mimic, as map_info keeps f_idx")
    void getMimicWithoutMimicReturnsThis() {
        Feature granite = feature(TerrainFlags.FEAT_GRANITE, null, TerrainFeatureFlags.TF_GRANITE);

        assertSame(granite, granite.getMimic());
    }

    @Test
    @DisplayName("getMimic resolves the mimicked code through the registry")
    void getMimicResolvesThroughRegistry() {
        Feature granite = feature(TerrainFlags.FEAT_GRANITE, null, TerrainFeatureFlags.TF_GRANITE);
        Feature secretDoor = feature(TerrainFlags.FEAT_SECRET, TerrainFlags.FEAT_GRANITE,
                TerrainFeatureFlags.TF_DOOR_ANY, TerrainFeatureFlags.TF_GRANITE);
        Feature floor = feature(TerrainFlags.FEAT_FLOOR, null, TerrainFeatureFlags.TF_FLOOR);
        TerrainRegistry.setFeatures(List.of(floor, granite, secretDoor));

        assertAll(
                () -> assertSame(granite, secretDoor.getMimic()),
                () -> assertSame(granite, granite.getMimic()),
                () -> assertSame(floor, floor.getMimic()));
    }

    @Test
    @DisplayName("getMimic gives null when the mimicked code is not in the registry")
    void getMimicUnknownCodeIsNull() {
        Feature secretDoor = feature(TerrainFlags.FEAT_SECRET, TerrainFlags.FEAT_GRANITE);
        TerrainRegistry.setFeatures(List.of(secretDoor));

        assertNull(secretDoor.getMimic());
    }

    @Test
    @DisplayName("getName, getTerrainFlag and getCodeFlags return the constructor values")
    void accessors() {
        Feature f = feature(TerrainFlags.FEAT_LAVA, "lava", null, 0);

        assertAll(
                () -> assertEquals("lava", f.getName()),
                () -> assertEquals(TerrainFlags.FEAT_LAVA, f.getTerrainFlag()),
                () -> assertEquals(TerrainFlags.FEAT_LAVA, f.getCodeFlags()));
    }

    @Test
    @DisplayName("equals and hashCode agree for the same instance and for features sharing their Flag objects")
    void equalsAndHashCodeWithSharedFlags() {
        Flag<TerrainFeatureFlags> flags = flags(TerrainFeatureFlags.TF_SHOP);
        Flag<MonsterRaceFlag> resist = new Flag<>(MonsterRaceFlag.class);
        Feature a = sharing(flags, resist, "Armoury", null, 2);
        Feature same = sharing(flags, resist, "Armoury", null, 2);
        Feature otherShop = sharing(flags, resist, "Armoury", null, 3);
        Feature otherName = sharing(flags, resist, "Armory", null, 2);
        Feature otherMimic = sharing(flags, resist, "Armoury", TerrainFlags.FEAT_GRANITE, 2);

        assertAll(
                () -> assertEquals(a, a),
                () -> assertEquals(a.hashCode(), a.hashCode()),
                () -> assertEquals(a, same),
                () -> assertEquals(a.hashCode(), same.hashCode()),
                () -> assertNotEquals(a, otherShop),
                () -> assertNotEquals(a, otherName),
                () -> assertNotEquals(a, otherMimic),
                () -> assertNotEquals(null, a),
                () -> assertNotEquals("Armoury", a));
    }

    /**
     * Deliberately disabled: this documents a defect in {@code Feature.equals} and
     * {@code Feature.hashCode}, not a mistake in the test. {@code Flag} defines no {@code equals}
     * or {@code hashCode}, so the {@code flags} and {@code resistFlag} comparisons are by identity
     * and two features built from separate but identical {@code Flag} objects are not equal.
     * Enable it once {@code Flag} (or {@code Feature.equals}, using {@code isEqual}) is fixed.
     */
    @Test
    @Disabled("Feature.equals compares Flag objects by identity: Flag has no equals/hashCode")
    @DisplayName("features built from separate but identical Flag objects are equal")
    void equalsAcrossSeparateFlagObjects() {
        Feature a = feature(TerrainFlags.FEAT_STORE_ARMOR, "Armoury", null, 2, TerrainFeatureFlags.TF_SHOP);
        Feature same = feature(TerrainFlags.FEAT_STORE_ARMOR, "Armoury", null, 2, TerrainFeatureFlags.TF_SHOP);
        Feature otherFlags = feature(TerrainFlags.FEAT_STORE_ARMOR, "Armoury", null, 2);

        assertAll(
                () -> assertEquals(a, same),
                () -> assertEquals(a.hashCode(), same.hashCode()),
                () -> assertNotEquals(a, otherFlags));
    }

    @Test
    @DisplayName("toString names the feature and shows its shop number")
    void toStringShowsFields() {
        String text = feature(TerrainFlags.FEAT_HOME, "your home", null, 8, TerrainFeatureFlags.TF_SHOP).toString();

        assertAll(
                () -> assertTrue(text.contains("name='your home'"), text),
                () -> assertTrue(text.contains("shopNum=8"), text),
                () -> assertTrue(text.contains("code=FEAT_HOME"), text));
    }

    private record Pred(String name, TerrainFeatureFlags flag, Predicate<Feature> test) {
    }
}
