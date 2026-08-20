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

package uk.co.jackoftrades.middle.game.globals.registry;

/**
 * Hard-coded stat-adjustment lookup tables — the Java port of the {@code adj_*}
 * arrays in the C original's {@code src/player-calcs.c}. Each table maps a stat's
 * <em>compressed index</em> (see {@link uk.co.jackoftrades.middle.player.PlayerState#getStatInd},
 * {@code 0..}{@link #STAT_RANGE}{@code -1}, not the raw stat value) to a small
 * adjustment. These are game constants, not data-file driven.
 *
 * <p>Each table below names the C call sites that read it. None of them has a consumer in the
 * port yet — the calculations that will use them (derived skills, blows, maximum hit points,
 * mana) are not ported — so the C references are the only guide to what each is for, and are
 * given for that reason rather than as trivia.
 *
 * <p>Class StatTables coded before 260815; the per-table comments written on 260818.
 *
 * @author Rowan Crowther
 */
public class StatTables {
    /**
     * Number of distinct compressed stat rungs — the port of C's {@code STAT_RANGE}.
     */
    public static final int STAT_RANGE = 38;

    /**
     * CON → base regeneration rate (the port of C's {@code adj_con_fix}). Indexed by
     * the CON stat index; drives how fast bleeding, poison and stun recover in
     * {@code decrease_timeouts}. Consumers add one to the looked-up value.
     */
    public static final int[] adjConFix = {
            0    /* 3 */,
            0    /* 4 */,
            0    /* 5 */,
            0    /* 6 */,
            0    /* 7 */,
            0    /* 8 */,
            0    /* 9 */,
            0    /* 10 */,
            0    /* 11 */,
            0    /* 12 */,
            0    /* 13 */,
            1    /* 14 */,
            1    /* 15 */,
            1    /* 16 */,
            1    /* 17 */,
            2    /* 18/00-18/09 */,
            2    /* 18/10-18/19 */,
            2    /* 18/20-18/29 */,
            2    /* 18/30-18/39 */,
            2    /* 18/40-18/49 */,
            3    /* 18/50-18/59 */,
            3    /* 18/60-18/69 */,
            3    /* 18/70-18/79 */,
            3    /* 18/80-18/89 */,
            3    /* 18/90-18/99 */,
            4    /* 18/100-18/109 */,
            4    /* 18/110-18/119 */,
            5    /* 18/120-18/129 */,
            6    /* 18/130-18/139 */,
            6    /* 18/140-18/149 */,
            7    /* 18/150-18/159 */,
            7    /* 18/160-18/169 */,
            8    /* 18/170-18/179 */,
            8    /* 18/180-18/189 */,
            8    /* 18/190-18/199 */,
            9    /* 18/200-18/209 */,
            9    /* 18/210-18/219 */,
            9    /* 18/220+ */
    };

    /**
     * INT → magic-device skill (the port of C's {@code adj_int_dev}). Indexed by the
     * INT stat index; added to {@code SKILL_DEVICE} when derived skills are recomputed
     * ({@code player-calcs.c:2242}).
     */
    public static final int[] adjIntDev = {
            0    /* 3 */,
            0    /* 4 */,
            0    /* 5 */,
            0    /* 6 */,
            0    /* 7 */,
            1    /* 8 */,
            1    /* 9 */,
            1    /* 10 */,
            1    /* 11 */,
            1    /* 12 */,
            1    /* 13 */,
            1    /* 14 */,
            2    /* 15 */,
            2    /* 16 */,
            2    /* 17 */,
            3    /* 18/00-18/09 */,
            3    /* 18/10-18/19 */,
            3    /* 18/20-18/29 */,
            3    /* 18/30-18/39 */,
            3    /* 18/40-18/49 */,
            4    /* 18/50-18/59 */,
            4    /* 18/60-18/69 */,
            5    /* 18/70-18/79 */,
            5    /* 18/80-18/89 */,
            6    /* 18/90-18/99 */,
            6    /* 18/100-18/109 */,
            7    /* 18/110-18/119 */,
            7    /* 18/120-18/129 */,
            8    /* 18/130-18/139 */,
            8    /* 18/140-18/149 */,
            9    /* 18/150-18/159 */,
            9    /* 18/160-18/169 */,
            10    /* 18/170-18/179 */,
            10    /* 18/180-18/189 */,
            11    /* 18/190-18/199 */,
            11    /* 18/200-18/209 */,
            12    /* 18/210-18/219 */,
            13    /* 18/220+ */
    };

    /**
     * WIS → saving throw (the port of C's {@code adj_wis_sav}). Indexed by the WIS
     * stat index; added to {@code SKILL_SAVE} ({@code player-calcs.c:2243}).
     */
    public static final int[] adjWisSav = {
            0    /* 3 */,
            0    /* 4 */,
            0    /* 5 */,
            0    /* 6 */,
            0    /* 7 */,
            1    /* 8 */,
            1    /* 9 */,
            1    /* 10 */,
            1    /* 11 */,
            1    /* 12 */,
            1    /* 13 */,
            1    /* 14 */,
            2    /* 15 */,
            2    /* 16 */,
            2    /* 17 */,
            3    /* 18/00-18/09 */,
            3    /* 18/10-18/19 */,
            3    /* 18/20-18/29 */,
            3    /* 18/30-18/39 */,
            3    /* 18/40-18/49 */,
            4    /* 18/50-18/59 */,
            4    /* 18/60-18/69 */,
            5    /* 18/70-18/79 */,
            5    /* 18/80-18/89 */,
            6    /* 18/90-18/99 */,
            7    /* 18/100-18/109 */,
            8    /* 18/110-18/119 */,
            9    /* 18/120-18/129 */,
            10    /* 18/130-18/139 */,
            11    /* 18/140-18/149 */,
            12    /* 18/150-18/159 */,
            13    /* 18/160-18/169 */,
            14    /* 18/170-18/179 */,
            15    /* 18/180-18/189 */,
            16    /* 18/190-18/199 */,
            17    /* 18/200-18/209 */,
            18    /* 18/210-18/219 */,
            19    /* 18/220+ */
    };

    /**
     * DEX → physical disarming (the port of C's {@code adj_dex_dis}). Indexed by the
     * DEX stat index; added to {@code SKILL_DISARM_PHYS} ({@code player-calcs.c:2240}).
     * The magical half of disarming is {@link #adjIntDis}.
     */
    public static final int adjDexDis[] = {
            0    /* 3 */,
            0    /* 4 */,
            0    /* 5 */,
            0    /* 6 */,
            0    /* 7 */,
            1    /* 8 */,
            1    /* 9 */,
            1    /* 10 */,
            1    /* 11 */,
            1    /* 12 */,
            1    /* 13 */,
            1    /* 14 */,
            2    /* 15 */,
            2    /* 16 */,
            2    /* 17 */,
            3    /* 18/00-18/09 */,
            3    /* 18/10-18/19 */,
            3    /* 18/20-18/29 */,
            4    /* 18/30-18/39 */,
            4    /* 18/40-18/49 */,
            5    /* 18/50-18/59 */,
            6    /* 18/60-18/69 */,
            7    /* 18/70-18/79 */,
            8    /* 18/80-18/89 */,
            9    /* 18/90-18/99 */,
            10    /* 18/100-18/109 */,
            10    /* 18/110-18/119 */,
            11    /* 18/120-18/129 */,
            12    /* 18/130-18/139 */,
            13    /* 18/140-18/149 */,
            14    /* 18/150-18/159 */,
            15    /* 18/160-18/169 */,
            16    /* 18/170-18/179 */,
            17    /* 18/180-18/189 */,
            18    /* 18/190-18/199 */,
            19    /* 18/200-18/209 */,
            19    /* 18/210-18/219 */,
            19    /* 18/220+ */
    };

    /**
     * INT → magical disarming (the port of C's {@code adj_int_dis}). Indexed by the
     * INT stat index; added to {@code SKILL_DISARM_MAGIC} ({@code player-calcs.c:2241}).
     * The physical half is {@link #adjDexDis}.
     */
    public static final int[] adjIntDis = {
            0    /* 3 */,
            0    /* 4 */,
            0    /* 5 */,
            0    /* 6 */,
            0    /* 7 */,
            1    /* 8 */,
            1    /* 9 */,
            1    /* 10 */,
            1    /* 11 */,
            1    /* 12 */,
            1    /* 13 */,
            1    /* 14 */,
            2    /* 15 */,
            2    /* 16 */,
            2    /* 17 */,
            3    /* 18/00-18/09 */,
            3    /* 18/10-18/19 */,
            3    /* 18/20-18/29 */,
            4    /* 18/30-18/39 */,
            4    /* 18/40-18/49 */,
            5    /* 18/50-18/59 */,
            6    /* 18/60-18/69 */,
            7    /* 18/70-18/79 */,
            8    /* 18/80-18/89 */,
            9    /* 18/90-18/99 */,
            10    /* 18/100-18/109 */,
            10    /* 18/110-18/119 */,
            11    /* 18/120-18/129 */,
            12    /* 18/130-18/139 */,
            13    /* 18/140-18/149 */,
            14    /* 18/150-18/159 */,
            15    /* 18/160-18/169 */,
            16    /* 18/170-18/179 */,
            17    /* 18/180-18/189 */,
            18    /* 18/190-18/199 */,
            19    /* 18/200-18/209 */,
            19    /* 18/210-18/219 */,
            19    /* 18/220+ */
    };

    /**
     * DEX → bonus to armour class (the port of C's {@code adj_dex_ta}). Indexed by the
     * DEX stat index; added to the state's {@code to_a} ({@code player-calcs.c:2233}).
     */
    public static final int[] adjDexTa =
            {
                    -4    /* 3 */,
                    -3    /* 4 */,
                    -2    /* 5 */,
                    -1    /* 6 */,
                    0    /* 7 */,
                    0    /* 8 */,
                    0    /* 9 */,
                    0    /* 10 */,
                    0    /* 11 */,
                    0    /* 12 */,
                    0    /* 13 */,
                    0    /* 14 */,
                    1    /* 15 */,
                    1    /* 16 */,
                    1    /* 17 */,
                    2    /* 18/00-18/09 */,
                    2    /* 18/10-18/19 */,
                    2    /* 18/20-18/29 */,
                    2    /* 18/30-18/39 */,
                    2    /* 18/40-18/49 */,
                    3    /* 18/50-18/59 */,
                    3    /* 18/60-18/69 */,
                    3    /* 18/70-18/79 */,
                    4    /* 18/80-18/89 */,
                    5    /* 18/90-18/99 */,
                    6    /* 18/100-18/109 */,
                    7    /* 18/110-18/119 */,
                    8    /* 18/120-18/129 */,
                    9    /* 18/130-18/139 */,
                    9    /* 18/140-18/149 */,
                    10    /* 18/150-18/159 */,
                    11    /* 18/160-18/169 */,
                    12    /* 18/170-18/179 */,
                    13    /* 18/180-18/189 */,
                    14    /* 18/190-18/199 */,
                    15    /* 18/200-18/209 */,
                    15    /* 18/210-18/219 */,
                    15    /* 18/220+ */
            };

    /**
     * STR → bonus to damage (the port of C's {@code adj_str_td}). Indexed by the STR
     * stat index; added to the state's {@code to_d} ({@code player-calcs.c:2234}), and again
     * to the damage of a shield bash ({@code player-attack.c:939}).
     */
    public static final int[] adjStrTd = {
            -2    /* 3 */,
            -2    /* 4 */,
            -1    /* 5 */,
            -1    /* 6 */,
            0    /* 7 */,
            0    /* 8 */,
            0    /* 9 */,
            0    /* 10 */,
            0    /* 11 */,
            0    /* 12 */,
            0    /* 13 */,
            0    /* 14 */,
            0    /* 15 */,
            1    /* 16 */,
            2    /* 17 */,
            2    /* 18/00-18/09 */,
            2    /* 18/10-18/19 */,
            3    /* 18/20-18/29 */,
            3    /* 18/30-18/39 */,
            3    /* 18/40-18/49 */,
            3    /* 18/50-18/59 */,
            3    /* 18/60-18/69 */,
            4    /* 18/70-18/79 */,
            5    /* 18/80-18/89 */,
            5    /* 18/90-18/99 */,
            6    /* 18/100-18/109 */,
            7    /* 18/110-18/119 */,
            8    /* 18/120-18/129 */,
            9    /* 18/130-18/139 */,
            10    /* 18/140-18/149 */,
            11    /* 18/150-18/159 */,
            12    /* 18/160-18/169 */,
            13    /* 18/170-18/179 */,
            14    /* 18/180-18/189 */,
            15    /* 18/190-18/199 */,
            16    /* 18/200-18/209 */,
            18    /* 18/210-18/219 */,
            20    /* 18/220+ */
    };

    /**
     * DEX → bonus to hit (the port of C's {@code adj_dex_th}). Indexed by the DEX stat
     * index; added to the state's {@code to_h} ({@code player-calcs.c:2235}), and consulted
     * again when a shield bash lands ({@code player-attack.c:906, 970}). Paired with
     * {@link #adjStrTh} — both stats contribute to the same to-hit total.
     */
    public static final int[] adjDexTh = {
            -3    /* 3 */,
            -2    /* 4 */,
            -2    /* 5 */,
            -1    /* 6 */,
            -1    /* 7 */,
            0    /* 8 */,
            0    /* 9 */,
            0    /* 10 */,
            0    /* 11 */,
            0    /* 12 */,
            0    /* 13 */,
            0    /* 14 */,
            0    /* 15 */,
            1    /* 16 */,
            2    /* 17 */,
            3    /* 18/00-18/09 */,
            3    /* 18/10-18/19 */,
            3    /* 18/20-18/29 */,
            3    /* 18/30-18/39 */,
            3    /* 18/40-18/49 */,
            4    /* 18/50-18/59 */,
            4    /* 18/60-18/69 */,
            4    /* 18/70-18/79 */,
            4    /* 18/80-18/89 */,
            5    /* 18/90-18/99 */,
            6    /* 18/100-18/109 */,
            7    /* 18/110-18/119 */,
            8    /* 18/120-18/129 */,
            9    /* 18/130-18/139 */,
            9    /* 18/140-18/149 */,
            10    /* 18/150-18/159 */,
            11    /* 18/160-18/169 */,
            12    /* 18/170-18/179 */,
            13    /* 18/180-18/189 */,
            14    /* 18/190-18/199 */,
            15    /* 18/200-18/209 */,
            15    /* 18/210-18/219 */,
            15    /* 18/220+ */
    };

    /**
     * STR → bonus to hit (the port of C's {@code adj_str_th}). Indexed by the STR stat
     * index; added to the state's {@code to_h} alongside {@link #adjDexTh}
     * ({@code player-calcs.c:2236}).
     */
    public static final int[] adjStrTh = {
            -3    /* 3 */,
            -2    /* 4 */,
            -1    /* 5 */,
            -1    /* 6 */,
            0    /* 7 */,
            0    /* 8 */,
            0    /* 9 */,
            0    /* 10 */,
            0    /* 11 */,
            0    /* 12 */,
            0    /* 13 */,
            0    /* 14 */,
            0    /* 15 */,
            0    /* 16 */,
            0    /* 17 */,
            1    /* 18/00-18/09 */,
            1    /* 18/10-18/19 */,
            1    /* 18/20-18/29 */,
            1    /* 18/30-18/39 */,
            1    /* 18/40-18/49 */,
            1    /* 18/50-18/59 */,
            1    /* 18/60-18/69 */,
            2    /* 18/70-18/79 */,
            3    /* 18/80-18/89 */,
            4    /* 18/90-18/99 */,
            5    /* 18/100-18/109 */,
            6    /* 18/110-18/119 */,
            7    /* 18/120-18/129 */,
            8    /* 18/130-18/139 */,
            9    /* 18/140-18/149 */,
            10    /* 18/150-18/159 */,
            11    /* 18/160-18/169 */,
            12    /* 18/170-18/179 */,
            13    /* 18/180-18/189 */,
            14    /* 18/190-18/199 */,
            15    /* 18/200-18/209 */,
            15    /* 18/210-18/219 */,
            15    /* 18/220+ */
    };

    /**
     * STR → carrying capacity, in deca-pounds (the port of C's {@code adj_str_wgt}).
     * Indexed by the STR stat index. C multiplies the entry by 100 to get the weight at which
     * the player starts to be slowed ({@code player-calcs.c:1746}) and by 60 for the
     * encumbrance check ({@code :1761}), so the stored value is a tenth of a pound-limit
     * rather than a limit itself.
     */
    public static final int[] adjStrWgt = {
            5    /* 3 */,
            6    /* 4 */,
            7    /* 5 */,
            8    /* 6 */,
            9    /* 7 */,
            10    /* 8 */,
            11    /* 9 */,
            12    /* 10 */,
            13    /* 11 */,
            14    /* 12 */,
            15    /* 13 */,
            16    /* 14 */,
            17    /* 15 */,
            18    /* 16 */,
            19    /* 17 */,
            20    /* 18/00-18/09 */,
            22    /* 18/10-18/19 */,
            24    /* 18/20-18/29 */,
            26    /* 18/30-18/39 */,
            28    /* 18/40-18/49 */,
            30    /* 18/50-18/59 */,
            30    /* 18/60-18/69 */,
            30    /* 18/70-18/79 */,
            30    /* 18/80-18/89 */,
            30    /* 18/90-18/99 */,
            30    /* 18/100-18/109 */,
            30    /* 18/110-18/119 */,
            30    /* 18/120-18/129 */,
            30    /* 18/130-18/139 */,
            30    /* 18/140-18/149 */,
            30    /* 18/150-18/159 */,
            30    /* 18/160-18/169 */,
            30    /* 18/170-18/179 */,
            30    /* 18/180-18/189 */,
            30    /* 18/190-18/199 */,
            30    /* 18/200-18/209 */,
            30    /* 18/210-18/219 */,
            30    /* 18/220+ */
    };

    /**
     * STR → heaviest weapon wieldable without penalty, in pounds (the port of C's
     * {@code adj_str_hold}). Indexed by the STR stat index; a weapon heavier than this costs
     * blows ({@code player-calcs.c:2251}).
     */
    public static final int[] adjStrHold = {
            4    /* 3 */,
            5    /* 4 */,
            6    /* 5 */,
            7    /* 6 */,
            8    /* 7 */,
            10    /* 8 */,
            12    /* 9 */,
            14    /* 10 */,
            16    /* 11 */,
            18    /* 12 */,
            20    /* 13 */,
            22    /* 14 */,
            24    /* 15 */,
            26    /* 16 */,
            28    /* 17 */,
            30    /* 18/00-18/09 */,
            30    /* 18/10-18/19 */,
            35    /* 18/20-18/29 */,
            40    /* 18/30-18/39 */,
            45    /* 18/40-18/49 */,
            50    /* 18/50-18/59 */,
            55    /* 18/60-18/69 */,
            60    /* 18/70-18/79 */,
            65    /* 18/80-18/89 */,
            70    /* 18/90-18/99 */,
            80    /* 18/100-18/109 */,
            80    /* 18/110-18/119 */,
            80    /* 18/120-18/129 */,
            80    /* 18/130-18/139 */,
            80    /* 18/140-18/149 */,
            90    /* 18/150-18/159 */,
            90    /* 18/160-18/169 */,
            90    /* 18/170-18/179 */,
            90    /* 18/180-18/189 */,
            90    /* 18/190-18/199 */,
            100    /* 18/200-18/209 */,
            100    /* 18/210-18/219 */,
            100    /* 18/220+ */
    };

    /**
     * STR → digging skill (the port of C's {@code adj_str_dig}). Indexed by the STR
     * stat index; added to {@code SKILL_DIGGING} ({@code player-calcs.c:2244}).
     */
    public static final int[] adjStrDig = {
            0    /* 3 */,
            0    /* 4 */,
            1    /* 5 */,
            2    /* 6 */,
            3    /* 7 */,
            4    /* 8 */,
            4    /* 9 */,
            5    /* 10 */,
            5    /* 11 */,
            6    /* 12 */,
            6    /* 13 */,
            7    /* 14 */,
            7    /* 15 */,
            8    /* 16 */,
            8    /* 17 */,
            9    /* 18/00-18/09 */,
            10    /* 18/10-18/19 */,
            12    /* 18/20-18/29 */,
            15    /* 18/30-18/39 */,
            20    /* 18/40-18/49 */,
            25    /* 18/50-18/59 */,
            30    /* 18/60-18/69 */,
            35    /* 18/70-18/79 */,
            40    /* 18/80-18/89 */,
            45    /* 18/90-18/99 */,
            50    /* 18/100-18/109 */,
            55    /* 18/110-18/119 */,
            60    /* 18/120-18/129 */,
            65    /* 18/130-18/139 */,
            70    /* 18/140-18/149 */,
            75    /* 18/150-18/159 */,
            80    /* 18/160-18/169 */,
            85    /* 18/170-18/179 */,
            90    /* 18/180-18/189 */,
            95    /* 18/190-18/199 */,
            100    /* 18/200-18/209 */,
            100    /* 18/210-18/219 */,
            100    /* 18/220+ */
    };

    /**
     * STR → row index into the blows table (the port of C's {@code adj_str_blow}).
     * Indexed by the STR stat index. Not a blow count: it is scaled by weapon weight to form
     * the {@code str_index} that selects a row ({@code player-calcs.c:1718}), and is read
     * directly when working out how hard the player can throw ({@code player-attack.c:1366}).
     * Its partner is {@link #adjDexBlow}.
     */
    public static final int[] adjStrBlow = {
            3    /* 3 */,
            4    /* 4 */,
            5    /* 5 */,
            6    /* 6 */,
            7    /* 7 */,
            8    /* 8 */,
            9    /* 9 */,
            10    /* 10 */,
            11    /* 11 */,
            12    /* 12 */,
            13    /* 13 */,
            14    /* 14 */,
            15    /* 15 */,
            16    /* 16 */,
            17    /* 17 */,
            20 /* 18/00-18/09 */,
            30 /* 18/10-18/19 */,
            40 /* 18/20-18/29 */,
            50 /* 18/30-18/39 */,
            60 /* 18/40-18/49 */,
            70 /* 18/50-18/59 */,
            80 /* 18/60-18/69 */,
            90 /* 18/70-18/79 */,
            100 /* 18/80-18/89 */,
            110 /* 18/90-18/99 */,
            120 /* 18/100-18/109 */,
            130 /* 18/110-18/119 */,
            140 /* 18/120-18/129 */,
            150 /* 18/130-18/139 */,
            160 /* 18/140-18/149 */,
            170 /* 18/150-18/159 */,
            180 /* 18/160-18/169 */,
            190 /* 18/170-18/179 */,
            200 /* 18/180-18/189 */,
            210 /* 18/190-18/199 */,
            220 /* 18/200-18/209 */,
            230 /* 18/210-18/219 */,
            240 /* 18/220+ */
    };

    /**
     * DEX → column index into the blows table (the port of C's {@code adj_dex_blow}).
     * Indexed by the DEX stat index and clamped to 11 by the caller before use
     * ({@code player-calcs.c:1725}), because the table it indexes is narrower than this one
     * is long. Its partner is {@link #adjStrBlow}.
     */
    public static final int[] adjDexBlow = {
            0    /* 3 */,
            0    /* 4 */,
            0    /* 5 */,
            0    /* 6 */,
            0    /* 7 */,
            0    /* 8 */,
            0    /* 9 */,
            1    /* 10 */,
            1    /* 11 */,
            1    /* 12 */,
            1    /* 13 */,
            1    /* 14 */,
            1    /* 15 */,
            1    /* 16 */,
            2    /* 17 */,
            2    /* 18/00-18/09 */,
            2    /* 18/10-18/19 */,
            3    /* 18/20-18/29 */,
            3    /* 18/30-18/39 */,
            4    /* 18/40-18/49 */,
            4    /* 18/50-18/59 */,
            5    /* 18/60-18/69 */,
            5    /* 18/70-18/79 */,
            6    /* 18/80-18/89 */,
            6    /* 18/90-18/99 */,
            7    /* 18/100-18/109 */,
            7    /* 18/110-18/119 */,
            8    /* 18/120-18/129 */,
            8    /* 18/130-18/139 */,
            8    /* 18/140-18/149 */,
            9    /* 18/150-18/159 */,
            9    /* 18/160-18/169 */,
            9    /* 18/170-18/179 */,
            10    /* 18/180-18/189 */,
            10    /* 18/190-18/199 */,
            11    /* 18/200-18/209 */,
            11    /* 18/210-18/219 */,
            11    /* 18/220+ */
    };

    /**
     * DEX → percentage chance of avoiding theft and falling (the port of C's
     * {@code adj_dex_safe}). Indexed by the DEX stat index; consulted when a monster tries to
     * steal ({@code mon-blows.c:789, 854}) and when monster recall reports that the player is
     * now immune to a thief ({@code mon-lore.c:208}).
     */
    public static final int[] adjDexSafe = {
            0    /* 3 */,
            1    /* 4 */,
            2    /* 5 */,
            3    /* 6 */,
            4    /* 7 */,
            5    /* 8 */,
            5    /* 9 */,
            6    /* 10 */,
            6    /* 11 */,
            7    /* 12 */,
            7    /* 13 */,
            8    /* 14 */,
            8    /* 15 */,
            9    /* 16 */,
            9    /* 17 */,
            10    /* 18/00-18/09 */,
            10    /* 18/10-18/19 */,
            15    /* 18/20-18/29 */,
            15    /* 18/30-18/39 */,
            20    /* 18/40-18/49 */,
            25    /* 18/50-18/59 */,
            30    /* 18/60-18/69 */,
            35    /* 18/70-18/79 */,
            40    /* 18/80-18/89 */,
            45    /* 18/90-18/99 */,
            50    /* 18/100-18/109 */,
            60    /* 18/110-18/119 */,
            70    /* 18/120-18/129 */,
            80    /* 18/130-18/139 */,
            90    /* 18/140-18/149 */,
            100    /* 18/150-18/159 */,
            100    /* 18/160-18/169 */,
            100    /* 18/170-18/179 */,
            100    /* 18/180-18/189 */,
            100    /* 18/190-18/199 */,
            100    /* 18/200-18/209 */,
            100    /* 18/210-18/219 */,
            100    /* 18/220+ */
    };

    /**
     * CON → extra hit points per level, in hundredths (the port of C's
     * {@code adj_con_mhp}). Indexed by the CON stat index. The hundredths are why the maximum
     * hit point calculation divides by 100 after multiplying by the character level
     * ({@code player-calcs.c:1568}).
     */
    public static final int[] adjConMhp = {
            -250    /* 3 */,
            -150    /* 4 */,
            -100    /* 5 */,
            -75    /* 6 */,
            -50        /* 7 */,
            -25    /* 8 */,
            -10        /* 9 */,
            -5    /* 10 */,
            0        /* 11 */,
            5    /* 12 */,
            10        /* 13 */,
            25    /* 14 */,
            50        /* 15 */,
            75    /* 16 */,
            100        /* 17 */,
            150    /* 18/00-18/09 */,
            175        /* 18/10-18/19 */,
            200    /* 18/20-18/29 */,
            225        /* 18/30-18/39 */,
            250    /* 18/40-18/49 */,
            275        /* 18/50-18/59 */,
            300    /* 18/60-18/69 */,
            350        /* 18/70-18/79 */,
            400    /* 18/80-18/89 */,
            450        /* 18/90-18/99 */,
            500    /* 18/100-18/109 */,
            550        /* 18/110-18/119 */,
            600    /* 18/120-18/129 */,
            650        /* 18/130-18/139 */,
            700    /* 18/140-18/149 */,
            750        /* 18/150-18/159 */,
            800    /* 18/160-18/169 */,
            900        /* 18/170-18/179 */,
            1000    /* 18/180-18/189 */,
            1100    /* 18/190-18/199 */,
            1250    /* 18/200-18/209 */,
            1250    /* 18/210-18/219 */,
            1250    /* 18/220+ */
    };

    /**
     * Spell stat → percentage of the class's spells that can be learned (the port of
     * C's {@code adj_mag_study}). Indexed by the stat index of the realm's casting stat, as
     * returned by C's {@code average_spell_stat} ({@code player-calcs.c:1297}) — not by a
     * fixed stat, since classes cast off different stats. Alone among these tables it carries
     * no comment in the C original.
     */
    public static final int[] adjMagStudy = {
            0    /* 3 */,
            0    /* 4 */,
            10    /* 5 */,
            20    /* 6 */,
            30    /* 7 */,
            40    /* 8 */,
            50    /* 9 */,
            60    /* 10 */,
            70    /* 11 */,
            80    /* 12 */,
            85    /* 13 */,
            90    /* 14 */,
            95    /* 15 */,
            100    /* 16 */,
            105    /* 17 */,
            110    /* 18/00-18/09 */,
            115    /* 18/10-18/19 */,
            120    /* 18/20-18/29 */,
            130    /* 18/30-18/39 */,
            140    /* 18/40-18/49 */,
            150    /* 18/50-18/59 */,
            160    /* 18/60-18/69 */,
            170    /* 18/70-18/79 */,
            180    /* 18/80-18/89 */,
            190    /* 18/90-18/99 */,
            200    /* 18/100-18/109 */,
            210    /* 18/110-18/119 */,
            220    /* 18/120-18/129 */,
            230    /* 18/130-18/139 */,
            240    /* 18/140-18/149 */,
            250    /* 18/150-18/159 */,
            250    /* 18/160-18/169 */,
            250    /* 18/170-18/179 */,
            250    /* 18/180-18/189 */,
            250    /* 18/190-18/199 */,
            250    /* 18/200-18/209 */,
            250    /* 18/210-18/219 */,
            250    /* 18/220+ */
    };

    /**
     * Spell stat → extra mana per level, in hundredths (the port of C's
     * {@code adj_mag_mana}). Indexed by the casting stat's index as {@link #adjMagStudy} is;
     * multiplied by the number of spell levels and divided by 100 to give the mana bonus
     * ({@code player-calcs.c:1496}).
     */
    public static final int[] adjMagMana = {
            0    /* 3 */,
            10    /* 4 */,
            20    /* 5 */,
            30    /* 6 */,
            40    /* 7 */,
            50    /* 8 */,
            60    /* 9 */,
            70    /* 10 */,
            80    /* 11 */,
            90    /* 12 */,
            100    /* 13 */,
            110    /* 14 */,
            120    /* 15 */,
            130    /* 16 */,
            140    /* 17 */,
            150    /* 18/00-18/09 */,
            160    /* 18/10-18/19 */,
            170    /* 18/20-18/29 */,
            180    /* 18/30-18/39 */,
            190    /* 18/40-18/49 */,
            200    /* 18/50-18/59 */,
            225    /* 18/60-18/69 */,
            250    /* 18/70-18/79 */,
            300    /* 18/80-18/89 */,
            350    /* 18/90-18/99 */,
            400    /* 18/100-18/109 */,
            450    /* 18/110-18/119 */,
            500    /* 18/120-18/129 */,
            550    /* 18/130-18/139 */,
            600    /* 18/140-18/149 */,
            650    /* 18/150-18/159 */,
            700    /* 18/160-18/169 */,
            750    /* 18/170-18/179 */,
            800    /* 18/180-18/189 */,
            800    /* 18/190-18/199 */,
            800    /* 18/200-18/209 */,
            800    /* 18/210-18/219 */,
            800    /* 18/220+ */
    };

    /**
     * Energy cost of one blow, indexed by a strength-versus-weapon-weight rung and then by a
     * dexterity rung — the port of C's {@code blows_table} ({@code player-calcs.c}).
     *
     * <p>Not a blow count: the number is the energy each blow consumes, so <em>lower</em> is faster.
     * {@code calcBlows} divides 10000 by it to get blows per turn scaled by 100, then caps that at
     * the class's maximum attacks ({@code player-calcs.c:1728-1730}).
     *
     * <p>Both subscripts are compressed and both saturate at 11. The first is
     * {@code adjStrBlow[STR] * class attack multiplier / max(weapon weight, class minimum weight)},
     * which is why a heavy weapon in a weak hand lands on a low rung; the second is
     * {@code adjDexBlow[DEX]}. The commentary rows at the head of the table give the dexterity value
     * each column stands for.
     */
    public static final int[][] blowsTable =
            {
                    /* P */
                    /* D:   0,   1,   2,   3,   4,   5,   6,   7,   8,   9,   10,  11+ */
                    /* DEX: 3,   10,  17,  /20, /40, /60, /80, /100,/120,/150,/180,/200 */

                    /* 0  */
                    {100, 100, 95, 85, 75, 60, 50, 42, 35, 30, 25, 23},

                    /* 1  */
                    {100, 95, 85, 75, 60, 50, 42, 35, 30, 25, 23, 21},

                    /* 2  */
                    {95, 85, 75, 60, 50, 42, 35, 30, 26, 23, 21, 20},

                    /* 3  */
                    {85, 75, 60, 50, 42, 36, 32, 28, 25, 22, 20, 19},

                    /* 4  */
                    {75, 60, 50, 42, 36, 33, 28, 25, 23, 21, 19, 18},

                    /* 5  */
                    {60, 50, 42, 36, 33, 30, 27, 24, 22, 21, 19, 17},

                    /* 6  */
                    {50, 42, 36, 33, 30, 27, 25, 23, 21, 20, 18, 17},

                    /* 7  */
                    {42, 36, 33, 30, 28, 26, 24, 22, 20, 19, 18, 17},

                    /* 8  */
                    {36, 33, 30, 28, 26, 24, 22, 21, 20, 19, 17, 16},

                    /* 9  */
                    {35, 32, 29, 26, 24, 22, 21, 20, 19, 18, 17, 16},

                    /* 10 */
                    {34, 30, 27, 25, 23, 22, 21, 20, 19, 18, 17, 16},

                    /* 11+ */
                    {33, 29, 26, 24, 22, 21, 20, 19, 18, 17, 16, 15},
                    /* DEX: 3,   10,  17,  /20, /40, /60, /80, /100,/120,/150,/180,/200 */
            };
}