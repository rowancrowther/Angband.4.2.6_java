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

/**
 * The learned-probability slots used by random artifact generation. It is the Java form of the
 * anonymous {@code ART_IDX_*} enum in {@code obj-randart.h}, which C builds by including the X-macro
 * list {@code list-randart-properties.h} with {@code ART_IDX(a, b)} defined as {@code ART_IDX_##a}.
 *
 * <p>Each constant names one property an artifact can carry, and indexes one counter in
 * {@code art_probs} (here the map behind {@code ArtifactSetData.getArtProbs}). The {@code count_*}
 * functions in {@code obj-randart.c} tally how often each property appears across the standard
 * artifact set, {@code adjust_freqs()} raises some of the tallies to floors and halves the two
 * aggravation ones, and the generator then picks new properties in proportion to them.
 *
 * <p>The prefix says which item group the counter is learned from: {@code BOW_}, {@code WEAPON_}
 * (bows and melee weapons both), {@code MELEE_}, {@code NONWEAPON_}, {@code ALLARMOR_},
 * {@code BOOT_}, {@code GLOVE_}, {@code HELM_} (helms and crowns), {@code SHIELD_}, {@code CLOAK_},
 * {@code ARMOR_} (soft, hard and dragon body armour) and {@code GEN_} (any item, used when no
 * item-specific slot claims the property). The {@code art_idx_*} arrays in {@code obj-randart.c}
 * group them the same way.
 *
 * <p>The constants are declared in the same order as the C list, so {@link #ordinal()} matches the
 * C integer value of each one. {@link #ART_IDX_TOTAL} is the count sentinel, not a property.
 *
 * <p>Enum ArtifactIndex coded before 261002, commented in full on 261002.
 */
public enum ArtifactIndex {
    /**
     * Extra shots on a bow, one count per artifact with a shots bonus of up to one extra shot.
     */
    ART_IDX_BOW_SHOTS(false),
    /** Extra might on a bow, one count per artifact with a might bonus of one or two. */
    ART_IDX_BOW_MIGHT(false),
    /** Brands on a bow, counted per brand; {@code adjust_freqs()} floors it at 2. */
    ART_IDX_BOW_BRAND(false),
    /** Slays on a bow, counted per slay; {@code adjust_freqs()} floors it at 2. */
    ART_IDX_BOW_SLAY(false),
    /** To-hit above the kind's minimum on a bow or melee weapon, counted in hit increments. */
    ART_IDX_WEAPON_HIT(false),
    /** To-dam above the kind's minimum on a bow or melee weapon, counted in damage increments. */
    ART_IDX_WEAPON_DAM(false),
    /** To-hit (without to-dam) on a non-weapon, counted in hit increments. */
    ART_IDX_NONWEAPON_HIT(false),
    /** To-dam (without to-hit) on a non-weapon, counted in damage increments. */
    ART_IDX_NONWEAPON_DAM(false),
    /** Both to-hit and to-dam on a non-weapon other than gloves, counted in combined increments. */
    ART_IDX_NONWEAPON_HIT_DAM(false),
    /** Brands on a non-weapon, counted per brand; {@code adjust_freqs()} floors it at 2. */
    ART_IDX_NONWEAPON_BRAND(false),
    /** Slays on a non-weapon, counted per slay; {@code adjust_freqs()} floors it at 1. */
    ART_IDX_NONWEAPON_SLAY(false),
    /** Extra blows on a non-weapon; {@code adjust_freqs()} floors it at 1. */
    ART_IDX_NONWEAPON_BLOWS(false),
    /** Extra shots on a non-weapon; {@code adjust_freqs()} floors it at 1. */
    ART_IDX_NONWEAPON_SHOTS(false),
    /** A blessed melee weapon ({@code OF_BLESSED}). */
    ART_IDX_MELEE_BLESS(false),
    /** Brands on a melee weapon, counted per brand. */
    ART_IDX_MELEE_BRAND(false),
    /** Slays on a melee weapon, counted per slay. */
    ART_IDX_MELEE_SLAY(false),
    /** See invisible on a melee weapon. */
    ART_IDX_MELEE_SINV(false),
    /** Extra blows on a melee weapon, one or two of them. */
    ART_IDX_MELEE_BLOWS(false),
    /**
     * AC bonus of 20 or less on a melee weapon, counted in AC increments; {@code adjust_freqs()}
     * floors it at 5.
     */
    ART_IDX_MELEE_AC(false),
    /** One or two damage dice more than the base kind on a melee weapon. */
    ART_IDX_MELEE_DICE(false),
    /** A melee weapon whose weight differs from its base kind. */
    ART_IDX_MELEE_WEIGHT(false),
    /** A tunnelling bonus on a melee weapon. */
    ART_IDX_MELEE_TUNN(false),
    /** Any armour piece whose weight differs from its base kind. */
    ART_IDX_ALLARMOR_WEIGHT(false),
    /** AC bonus of 20 or less on boots, counted in AC increments. */
    ART_IDX_BOOT_AC(false),
    /** Feather fall on boots. */
    ART_IDX_BOOT_FEATHER(false),
    /** A stealth bonus on boots. */
    ART_IDX_BOOT_STEALTH(false),
    /** Trap immunity on boots. */
    ART_IDX_BOOT_TRAP_IMM(false),
    /** A speed bonus of 7 or less on boots. */
    ART_IDX_BOOT_SPEED(false),
    /** A movement-speed ({@code OBJ_MOD_MOVES}) bonus on boots. */
    ART_IDX_BOOT_MOVES(false),
    /** AC bonus of 20 or less on gloves, counted in AC increments. */
    ART_IDX_GLOVE_AC(false),
    /** Free action on gloves. */
    ART_IDX_GLOVE_FA(false),
    /** A DEX bonus on gloves, counted instead of one {@link #ART_IDX_GEN_STAT}. */
    ART_IDX_GLOVE_DEX(false),
    /** Both to-hit and to-dam on gloves, counted in combined increments. */
    ART_IDX_GLOVE_HIT_DAM(false),
    /** AC bonus of 20 or less on a helm or crown, counted in AC increments. */
    ART_IDX_HELM_AC(false),
    /** Protection from blindness on a helm or crown. */
    ART_IDX_HELM_RBLIND(false),
    /** Telepathy on a helm or crown. */
    ART_IDX_HELM_ESP(false),
    /** See invisible on a helm or crown. */
    ART_IDX_HELM_SINV(false),
    /** A WIS bonus on a helm or crown, counted instead of one {@link #ART_IDX_GEN_STAT}. */
    ART_IDX_HELM_WIS(false),
    /** An INT bonus on a helm or crown, counted instead of one {@link #ART_IDX_GEN_STAT}. */
    ART_IDX_HELM_INT(false),
    /** AC bonus of 20 or less on a shield, counted in AC increments. */
    ART_IDX_SHIELD_AC(false),
    /** Low (acid, elec, fire, cold) resists on a shield, counted per resist. */
    ART_IDX_SHIELD_LRES(false),
    /** AC bonus of 20 or less on a cloak, counted in AC increments. */
    ART_IDX_CLOAK_AC(false),
    /** A stealth bonus on a cloak. */
    ART_IDX_CLOAK_STEALTH(false),
    /** AC bonus of 20 or less on body armour, counted in AC increments. */
    ART_IDX_ARMOR_AC(false),
    /** A stealth bonus on body armour. */
    ART_IDX_ARMOR_STEALTH(false),
    /** Hold life on body armour. */
    ART_IDX_ARMOR_HLIFE(false),
    /** A CON bonus on body armour, counted instead of one {@link #ART_IDX_GEN_STAT}. */
    ART_IDX_ARMOR_CON(false),
    /** One to three low resists on body armour, counted per resist. */
    ART_IDX_ARMOR_LRES(false),
    /** All four low resists on body armour, counted once rather than as four {@link #ART_IDX_ARMOR_LRES}. */
    ART_IDX_ARMOR_ALLRES(false),
    /**
     * The total number of high resists and protections on body armour. Only the total is learned
     * here; which ones are chosen follows the {@code GEN_R*} frequencies.
     */
    ART_IDX_ARMOR_HRES(false),
    /** Stat bonuses not claimed by a helm, body armour or glove slot, counted per stat. */
    ART_IDX_GEN_STAT(false),
    /** Stat sustains, counted per sustain. */
    ART_IDX_GEN_SUST(false),
    /** A stealth bonus on anything other than boots, a cloak or body armour. */
    ART_IDX_GEN_STEALTH(false),
    /** A searching bonus. */
    ART_IDX_GEN_SEARCH(false),
    /** An infravision bonus. */
    ART_IDX_GEN_INFRA(false),
    /** A speed bonus of 7 or less on anything other than boots. */
    ART_IDX_GEN_SPEED(false),
    /** Immunities (acid, elec, fire, cold), counted per immunity. */
    ART_IDX_GEN_IMMUNE(false),
    /** Free action on anything other than gloves. */
    ART_IDX_GEN_FA(false),
    /** Hold life on anything other than body armour. */
    ART_IDX_GEN_HLIFE(false),
    /** Feather fall on anything other than boots. */
    ART_IDX_GEN_FEATHER(false),
    /** A light radius bonus. */
    ART_IDX_GEN_LIGHT(false),
    /** See invisible on anything other than a melee weapon, helm or crown. */
    ART_IDX_GEN_SINV(false),
    /** Telepathy on anything other than a helm or crown. */
    ART_IDX_GEN_ESP(false),
    /** Slow digestion. */
    ART_IDX_GEN_SDIG(false),
    /** Regeneration. */
    ART_IDX_GEN_REGEN(false),
    /** Low resists on anything other than a shield or body armour, counted per resist. */
    ART_IDX_GEN_LRES(false),
    /** Resist poison. */
    ART_IDX_GEN_RPOIS(false),
    /** Protection from fear; {@code adjust_freqs()} floors it at 5. */
    ART_IDX_GEN_RFEAR(false),
    /** Resist light. */
    ART_IDX_GEN_RLIGHT(false),
    /** Resist dark. */
    ART_IDX_GEN_RDARK(false),
    /** Protection from blindness on anything other than a helm or crown. */
    ART_IDX_GEN_RBLIND(false),
    /** Protection from confusion. */
    ART_IDX_GEN_RCONF(false),
    /** Resist sound. */
    ART_IDX_GEN_RSOUND(false),
    /** Resist shards. */
    ART_IDX_GEN_RSHARD(false),
    /** Resist nexus. */
    ART_IDX_GEN_RNEXUS(false),
    /** Resist nether. */
    ART_IDX_GEN_RNETHER(false),
    /** Resist chaos. */
    ART_IDX_GEN_RCHAOS(false),
    /** Resist disenchantment. */
    ART_IDX_GEN_RDISEN(false),
    /**
     * AC bonus on a bow, or on a non-weapon with no item-specific AC slot, counted in AC
     * increments; {@code adjust_freqs()} floors it at 5.
     */
    ART_IDX_GEN_AC(false),
    /** A tunnelling bonus on a non-weapon; {@code adjust_freqs()} floors it at 5. */
    ART_IDX_GEN_TUNN(false),
    /** An activation. */
    ART_IDX_GEN_ACTIV(false),
    /** Protection from stunning; {@code adjust_freqs()} floors it at 3. */
    ART_IDX_GEN_PSTUN(false),
    /** A damage reduction bonus. */
    ART_IDX_GEN_DAM_RED(false),
    /** A movement-speed ({@code OBJ_MOD_MOVES}) bonus on anything other than boots. */
    ART_IDX_GEN_MOVES(false),
    /** Trap immunity on anything other than boots. */
    ART_IDX_GEN_TRAP_IMM(false),
    /** Aggravation on a bow or melee weapon; {@code adjust_freqs()} halves it. */
    ART_IDX_WEAPON_AGGR(false),
    /** Aggravation on a non-weapon; {@code adjust_freqs()} halves it. */
    ART_IDX_NONWEAPON_AGGR(false),
    /**
     * Supercharged: three or more damage dice above the base kind on a melee weapon;
     * {@code adjust_freqs()} floors it at 5.
     */
    ART_IDX_MELEE_DICE_SUPER(true),
    /**
     * Supercharged: more than one extra shot on a bow (a shots modifier above 10, in tenths);
     * {@code adjust_freqs()} floors it at 5.
     */
    ART_IDX_BOW_SHOTS_SUPER(true),
    /** Supercharged: three or more extra might on a bow; {@code adjust_freqs()} floors it at 5. */
    ART_IDX_BOW_MIGHT_SUPER(true),
    /**
     * Supercharged: a speed bonus above 7 on any item, boots included; {@code adjust_freqs()}
     * floors it at 5.
     */
    ART_IDX_GEN_SPEED_SUPER(true),
    /** Supercharged: three or more extra blows on a melee weapon; {@code adjust_freqs()} floors it at 5. */
    ART_IDX_MELEE_BLOWS_SUPER(true),
    /** Supercharged: an AC bonus above 20 on a melee weapon, counted once. */
    ART_IDX_MELEE_AC_SUPER(true),
    /**
     * Supercharged: an AC bonus above 20 on a non-weapon, counted once; {@code adjust_freqs()}
     * floors it at 5.
     */
    ART_IDX_GEN_AC_SUPER(true),
    /**
     * Count sentinel, one past the last property: C sizes {@code art_probs} with it. It never
     * indexes a counter in C; the Java map carries an entry for it anyway.
     */
    ART_IDX_TOTAL(false);

    /**
     * The supercharge flag: the second column of {@code list-randart-properties.h}, true for the
     * seven {@code _SUPER} properties. C's only expansion of the {@code ART_IDX} macro discards this
     * column, so 4.2.6 never reads it, and nothing in this enum reads it either.
     *
     * <p>Field value coded before 261002, commented in full on 261002.
     */
    private final boolean value;

    /**
     * Binds a constant to its supercharge flag from {@code list-randart-properties.h}.
     *
     * <p>Constructor ArtifactIndex coded before 261002, commented in full on 261002.
     *
     * @param value true for a supercharged property
     */
    ArtifactIndex(boolean value) {
        this.value = value;
    }
}
