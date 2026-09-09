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

public enum ArtifactIndex {
    ART_IDX_BOW_SHOTS(false),
    ART_IDX_BOW_MIGHT(false),
    ART_IDX_BOW_BRAND(false),
    ART_IDX_BOW_SLAY(false),
    ART_IDX_WEAPON_HIT(false),
    ART_IDX_WEAPON_DAM(false),
    ART_IDX_NONWEAPON_HIT(false),
    ART_IDX_NONWEAPON_DAM(false),
    ART_IDX_NONWEAPON_HIT_DAM(false),
    ART_IDX_NONWEAPON_BRAND(false),
    ART_IDX_NONWEAPON_SLAY(false),
    ART_IDX_NONWEAPON_BLOWS(false),
    ART_IDX_NONWEAPON_SHOTS(false),
    ART_IDX_MELEE_BLESS(false),
    ART_IDX_MELEE_BRAND(false),
    ART_IDX_MELEE_SLAY(false),
    ART_IDX_MELEE_SINV(false),
    ART_IDX_MELEE_BLOWS(false),
    ART_IDX_MELEE_AC(false),
    ART_IDX_MELEE_DICE(false),
    ART_IDX_MELEE_WEIGHT(false),
    ART_IDX_MELEE_TUNN(false),
    ART_IDX_ALLARMOR_WEIGHT(false),
    ART_IDX_BOOT_AC(false),
    ART_IDX_BOOT_FEATHER(false),
    ART_IDX_BOOT_STEALTH(false),
    ART_IDX_BOOT_TRAP_IMM(false),
    ART_IDX_BOOT_SPEED(false),
    ART_IDX_BOOT_MOVES(false),
    ART_IDX_GLOVE_AC(false),
    ART_IDX_GLOVE_FA(false),
    ART_IDX_GLOVE_DEX(false),
    ART_IDX_GLOVE_HIT_DAM(false),
    ART_IDX_HELM_AC(false),
    ART_IDX_HELM_RBLIND(false),
    ART_IDX_HELM_ESP(false),
    ART_IDX_HELM_SINV(false),
    ART_IDX_HELM_WIS(false),
    ART_IDX_HELM_INT(false),
    ART_IDX_SHIELD_AC(false),
    ART_IDX_SHIELD_LRES(false),
    ART_IDX_CLOAK_AC(false),
    ART_IDX_CLOAK_STEALTH(false),
    ART_IDX_ARMOR_AC(false),
    ART_IDX_ARMOR_STEALTH(false),
    ART_IDX_ARMOR_HLIFE(false),
    ART_IDX_ARMOR_CON(false),
    ART_IDX_ARMOR_LRES(false),
    ART_IDX_ARMOR_ALLRES(false),
    ART_IDX_ARMOR_HRES(false),
    ART_IDX_GEN_STAT(false),
    ART_IDX_GEN_SUST(false),
    ART_IDX_GEN_STEALTH(false),
    ART_IDX_GEN_SEARCH(false),
    ART_IDX_GEN_INFRA(false),
    ART_IDX_GEN_SPEED(false),
    ART_IDX_GEN_IMMUNE(false),
    ART_IDX_GEN_FA(false),
    ART_IDX_GEN_HLIFE(false),
    ART_IDX_GEN_FEATHER(false),
    ART_IDX_GEN_LIGHT(false),
    ART_IDX_GEN_SINV(false),
    ART_IDX_GEN_ESP(false),
    ART_IDX_GEN_SDIG(false),
    ART_IDX_GEN_REGEN(false),
    ART_IDX_GEN_LRES(false),
    ART_IDX_GEN_RPOIS(false),
    ART_IDX_GEN_RFEAR(false),
    ART_IDX_GEN_RLIGHT(false),
    ART_IDX_GEN_RDARK(false),
    ART_IDX_GEN_RBLIND(false),
    ART_IDX_GEN_RCONF(false),
    ART_IDX_GEN_RSOUND(false),
    ART_IDX_GEN_RSHARD(false),
    ART_IDX_GEN_RNEXUS(false),
    ART_IDX_GEN_RNETHER(false),
    ART_IDX_GEN_RCHAOS(false),
    ART_IDX_GEN_RDISEN(false),
    ART_IDX_GEN_AC(false),
    ART_IDX_GEN_TUNN(false),
    ART_IDX_GEN_ACTIV(false),
    ART_IDX_GEN_PSTUN(false),
    ART_IDX_GEN_DAM_RED(false),
    ART_IDX_GEN_MOVES(false),
    ART_IDX_GEN_TRAP_IMM(false),
    ART_IDX_WEAPON_AGGR(false),
    ART_IDX_NONWEAPON_AGGR(false),
    ART_IDX_MELEE_DICE_SUPER(true),
    ART_IDX_BOW_SHOTS_SUPER(true),
    ART_IDX_BOW_MIGHT_SUPER(true),
    ART_IDX_GEN_SPEED_SUPER(true),
    ART_IDX_MELEE_BLOWS_SUPER(true),
    ART_IDX_MELEE_AC_SUPER(true),
    ART_IDX_GEN_AC_SUPER(true),
    ART_IDX_TOTAL(false);

    private final boolean value;

    ArtifactIndex(boolean value) {
        this.value = value;
    }
}
