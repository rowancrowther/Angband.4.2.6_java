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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link ArtifactIndex}, the port of the {@code ART_IDX_*} enum that
 * {@code obj-randart.h} builds from the X-macro list {@code list-randart-properties.h}.
 *
 * <p>The expected values are copied from {@code list-randart-properties.h}, not from the Java. The
 * order matters because C indexes {@code art_probs} by the enum's integer value, so the tests pin
 * the full list, the ordinal of the {@code ART_IDX_TOTAL} sentinel, and the supercharge column.
 * The supercharge flag has no accessor, so it is read by reflection.
 *
 * <p>Class ArtifactIndexTest coded on 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
class ArtifactIndexTest {

    /**
     * Every row of {@code list-randart-properties.h}, in file order, with the {@code ART_IDX_}
     * prefix the C macro pastes on.
     */
    private static final List<String> C_ORDER = List.of(
            "ART_IDX_BOW_SHOTS", "ART_IDX_BOW_MIGHT", "ART_IDX_BOW_BRAND", "ART_IDX_BOW_SLAY",
            "ART_IDX_WEAPON_HIT", "ART_IDX_WEAPON_DAM", "ART_IDX_NONWEAPON_HIT", "ART_IDX_NONWEAPON_DAM",
            "ART_IDX_NONWEAPON_HIT_DAM", "ART_IDX_NONWEAPON_BRAND", "ART_IDX_NONWEAPON_SLAY", "ART_IDX_NONWEAPON_BLOWS",
            "ART_IDX_NONWEAPON_SHOTS", "ART_IDX_MELEE_BLESS", "ART_IDX_MELEE_BRAND", "ART_IDX_MELEE_SLAY",
            "ART_IDX_MELEE_SINV", "ART_IDX_MELEE_BLOWS", "ART_IDX_MELEE_AC", "ART_IDX_MELEE_DICE",
            "ART_IDX_MELEE_WEIGHT", "ART_IDX_MELEE_TUNN", "ART_IDX_ALLARMOR_WEIGHT", "ART_IDX_BOOT_AC",
            "ART_IDX_BOOT_FEATHER", "ART_IDX_BOOT_STEALTH", "ART_IDX_BOOT_TRAP_IMM", "ART_IDX_BOOT_SPEED",
            "ART_IDX_BOOT_MOVES", "ART_IDX_GLOVE_AC", "ART_IDX_GLOVE_FA", "ART_IDX_GLOVE_DEX",
            "ART_IDX_GLOVE_HIT_DAM", "ART_IDX_HELM_AC", "ART_IDX_HELM_RBLIND", "ART_IDX_HELM_ESP",
            "ART_IDX_HELM_SINV", "ART_IDX_HELM_WIS", "ART_IDX_HELM_INT", "ART_IDX_SHIELD_AC",
            "ART_IDX_SHIELD_LRES", "ART_IDX_CLOAK_AC", "ART_IDX_CLOAK_STEALTH", "ART_IDX_ARMOR_AC",
            "ART_IDX_ARMOR_STEALTH", "ART_IDX_ARMOR_HLIFE", "ART_IDX_ARMOR_CON", "ART_IDX_ARMOR_LRES",
            "ART_IDX_ARMOR_ALLRES", "ART_IDX_ARMOR_HRES", "ART_IDX_GEN_STAT", "ART_IDX_GEN_SUST",
            "ART_IDX_GEN_STEALTH", "ART_IDX_GEN_SEARCH", "ART_IDX_GEN_INFRA", "ART_IDX_GEN_SPEED",
            "ART_IDX_GEN_IMMUNE", "ART_IDX_GEN_FA", "ART_IDX_GEN_HLIFE", "ART_IDX_GEN_FEATHER",
            "ART_IDX_GEN_LIGHT", "ART_IDX_GEN_SINV", "ART_IDX_GEN_ESP", "ART_IDX_GEN_SDIG",
            "ART_IDX_GEN_REGEN", "ART_IDX_GEN_LRES", "ART_IDX_GEN_RPOIS", "ART_IDX_GEN_RFEAR",
            "ART_IDX_GEN_RLIGHT", "ART_IDX_GEN_RDARK", "ART_IDX_GEN_RBLIND", "ART_IDX_GEN_RCONF",
            "ART_IDX_GEN_RSOUND", "ART_IDX_GEN_RSHARD", "ART_IDX_GEN_RNEXUS", "ART_IDX_GEN_RNETHER",
            "ART_IDX_GEN_RCHAOS", "ART_IDX_GEN_RDISEN", "ART_IDX_GEN_AC", "ART_IDX_GEN_TUNN",
            "ART_IDX_GEN_ACTIV", "ART_IDX_GEN_PSTUN", "ART_IDX_GEN_DAM_RED", "ART_IDX_GEN_MOVES",
            "ART_IDX_GEN_TRAP_IMM", "ART_IDX_WEAPON_AGGR", "ART_IDX_NONWEAPON_AGGR", "ART_IDX_MELEE_DICE_SUPER",
            "ART_IDX_BOW_SHOTS_SUPER", "ART_IDX_BOW_MIGHT_SUPER", "ART_IDX_GEN_SPEED_SUPER", "ART_IDX_MELEE_BLOWS_SUPER",
            "ART_IDX_MELEE_AC_SUPER", "ART_IDX_GEN_AC_SUPER", "ART_IDX_TOTAL");

    /**
     * The rows whose supercharge column reads {@code true} in {@code list-randart-properties.h}.
     */
    private static final Set<String> C_SUPERCHARGED = Set.of(
            "ART_IDX_MELEE_DICE_SUPER", "ART_IDX_BOW_SHOTS_SUPER", "ART_IDX_BOW_MIGHT_SUPER",
            "ART_IDX_GEN_SPEED_SUPER", "ART_IDX_MELEE_BLOWS_SUPER", "ART_IDX_MELEE_AC_SUPER",
            "ART_IDX_GEN_AC_SUPER");

    /**
     * The constants match C's list name for name and position for position, with no extra
     * {@code NONE} entry at the front.
     *
     * <p>Function constantsAreInTheCOrder coded on 261002, commented in full on 261002.
     */
    @Test
    void constantsAreInTheCOrder() {
        List<String> actual = Arrays.stream(ArtifactIndex.values()).map(Enum::name).toList();
        assertEquals(C_ORDER, actual);
    }

    /**
     * C's enum starts at 0, so {@code ART_IDX_TOTAL} is 94, the size of {@code art_probs}. A few
     * interior values are pinned as well, so a swap that keeps the count still fails.
     *
     * <p>Function ordinalsMatchTheCIntegerValues coded on 261002, commented in full on 261002.
     */
    @Test
    void ordinalsMatchTheCIntegerValues() {
        assertEquals(95, ArtifactIndex.values().length);
        assertEquals(0, ArtifactIndex.ART_IDX_BOW_SHOTS.ordinal());
        assertEquals(50, ArtifactIndex.ART_IDX_GEN_STAT.ordinal());
        assertEquals(86, ArtifactIndex.ART_IDX_NONWEAPON_AGGR.ordinal());
        assertEquals(87, ArtifactIndex.ART_IDX_MELEE_DICE_SUPER.ordinal());
        assertEquals(93, ArtifactIndex.ART_IDX_GEN_AC_SUPER.ordinal());
        assertEquals(94, ArtifactIndex.ART_IDX_TOTAL.ordinal());
        assertSame(ArtifactIndex.ART_IDX_TOTAL, ArtifactIndex.values()[ArtifactIndex.values().length - 1]);
    }

    /**
     * Each constant's supercharge flag matches the second column of
     * {@code list-randart-properties.h}: true for exactly the seven {@code _SUPER} rows, false for
     * every other row including {@code TOTAL}.
     *
     * <p>Function superchargeFlagMatchesTheCColumn coded on 261002, commented in full on 261002.
     *
     * @param index the constant under test
     * @throws ReflectiveOperationException if the private flag field cannot be read
     */
    @ParameterizedTest
    @EnumSource(ArtifactIndex.class)
    void superchargeFlagMatchesTheCColumn(ArtifactIndex index) throws ReflectiveOperationException {
        Field flag = ArtifactIndex.class.getDeclaredField("value");
        flag.setAccessible(true);
        assertEquals(C_SUPERCHARGED.contains(index.name()), flag.getBoolean(index), index.name());
    }

    /**
     * The supercharged rows are the last seven before the sentinel, as in C.
     *
     * <p>Function superchargedRowsSitJustBeforeTheSentinel coded on 261002, commented in full on 261002.
     */
    @Test
    void superchargedRowsSitJustBeforeTheSentinel() {
        assertEquals(C_SUPERCHARGED.size(), 7);
        for (ArtifactIndex index : ArtifactIndex.values()) {
            int ordinal = index.ordinal();
            assertEquals(ordinal >= 87 && ordinal <= 93, C_SUPERCHARGED.contains(index.name()),
                    index.name());
        }
    }
}
