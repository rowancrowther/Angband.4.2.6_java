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

import org.jetbrains.annotations.Contract;
import uk.co.jackoftradesltd.middle.player.enums.SustainStat;

/**
 * The boolean property flags an object can carry (sustains, protections,
 * telepathy, free action, light, digging, throwing, curses, …), each with the
 * short label C uses in its object flag display. Mirrors the C original's
 * {@code OF_*} object flags ({@code list-object-flags.h}); the constants are
 * self-describing and documented collectively here.
 *
 * <p>Order matters. C's header warns that changing flag order breaks savefiles, and the
 * constants here are in exactly the order of that header: {@code OF_NONE} first, standing in
 * for the zero C's {@code OF_NONE} holds, then the thirty-eight {@code OF()} entries, then
 * {@code OF_MAX}. {@code OF_NONE} and {@code OF_MAX} are sentinels, not real flags, so a loop
 * over every flag runs from {@code ordinal() == 1} up to but excluding {@code OF_MAX}, as C's
 * loops do from {@code 1} to {@code OF_MAX}.
 *
 * <p>The first five real flags, {@code OF_SUST_STR} to {@code OF_SUST_CON}, must stay in the
 * same order as the stats in {@code list-stats.h}, because C's {@code sustain_flag()}
 * ({@code obj-properties.c}) finds a stat's sustain by adding one to the stat's index.
 * {@link uk.co.jackoftradesltd.middle.player.enums.SustainStat} records that pairing and
 * {@link #getSustainStatFlag} reads it.
 *
 * <p>Class ObjectFlag coded before 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
public enum ObjectFlag {
    OF_NONE(""),
    OF_SUST_STR(" sStr"),
    OF_SUST_INT(" sInt"),
    OF_SUST_WIS(" sWis"),
    OF_SUST_DEX(" sDex"),
    OF_SUST_CON(" sCon"),
    OF_PROT_FEAR("pFear"),
    OF_PROT_BLIND("pBlnd"),
    OF_PROT_CONF("pConf"),
    OF_PROT_STUN("pStun"),
    OF_SLOW_DIGEST("S.Dig"),
    OF_FEATHER("Feath"),
    OF_REGEN("Regen"),
    OF_TELEPATHY("  ESP"),
    OF_SEE_INVIS("S.Inv"),
    OF_FREE_ACT("FrAct"),
    OF_HOLD_LIFE("HLife"),
    OF_IMPACT("Impct"),
    OF_BLESSED(" Bless"),
    OF_BURNS_OUT("BuOut"),
    OF_TAKES_FUEL("TFuel"),
    OF_NO_FUEL("NFuel"),
    OF_IMPAIR_HP("ImpHP"),
    OF_IMPAIR_MANA("ImpSP"),
    OF_AFRAID(" Fear"),
    OF_NO_TELEPORT("NoTel"),
    OF_AGGRAVATE("Aggrv"),
    OF_DRAIN_EXP("DrExp"),
    OF_STICKY("Stick"),
    OF_FRAGILE("Fragl"),
    OF_LIGHT_2("Lght2"),
    OF_LIGHT_3("Lght3"),
    OF_DIG_1(" Dig1"),
    OF_DIG_2(" Dig2"),
    OF_DIG_3(" Dig3"),
    OF_EXPLODE("Expld"),
    OF_TRAP_IMMUNE("TrpIm"),
    OF_THROWING("Throw"),
    OF_MULTIPLY_WEIGHT("MulWg"),
    OF_MAX("");

    /**
     * The short display label for this flag, the second argument of C's {@code OF()} entry in
     * {@code list-object-flags.h}. C's comment says at most the first five characters are
     * used; the labels are padded with a leading space where they are shorter, so a column of
     * them lines up. {@code OF_NONE} and {@code OF_MAX} carry the empty string.
     *
     * <p>Field flag coded before 261005, commented in full on 261005.
     */
    private String flag;

    /**
     * Bind an object flag to its display label.
     *
     * <p>Constructor ObjectFlag coded before 261005, commented in full on 261005.
     *
     * @param flag the display label
     */
    @Contract(pure = true)
    ObjectFlag(String flag) {
        this.flag = flag;
    }

    /**
     * The sustain flag that protects a given stat, the object-flag half of C's
     * {@code sustain_flag()} ({@code obj-properties.c}). C returns the stat index plus one as
     * a bare {@code int}; here the {@link SustainStat} already holds that number, and this
     * method turns it into the {@code OF_SUST_*} constant at that position.
     *
     * <p>{@code SUS_STAT_STR}, {@code _INT}, {@code _WIS}, {@code _DEX} and {@code _CON} give
     * {@code OF_SUST_STR}, {@code _INT}, {@code _WIS}, {@code _DEX} and {@code _CON}. The two
     * sentinels, {@code SUS_STAT_NONE} and {@code SUS_STAT_MAX}, give {@code null}, which
     * stands for the {@code -1} C returns when the stat is outside 0 to 4. A caller must test
     * for {@code null} where C tests {@code flag < 0}.
     *
     * <p>Method getSustainStatFlag coded before 261005, commented in full on 261005.
     *
     * @param sustainStat the stat whose sustain is wanted
     * @return the matching {@code OF_SUST_*} flag, or {@code null} for a sentinel
     */
    public static ObjectFlag getSustainStatFlag(SustainStat sustainStat) {
        return switch (sustainStat) {
            case SUS_STAT_CON -> OF_SUST_CON;
            case SUS_STAT_INT -> OF_SUST_INT;
            case SUS_STAT_WIS -> OF_SUST_WIS;
            case SUS_STAT_DEX -> OF_SUST_DEX;
            case SUS_STAT_STR -> OF_SUST_STR;
            default -> null;
        };
    }

    /**
     * The short display label for this flag, as C's {@code OF()} entry in
     * {@code list-object-flags.h} spells it, leading space included.
     *
     * <p>Method getFlag coded before 261005, commented in full on 261005.
     *
     * @return this flag's short display label
     */
    @Contract(pure = true)
    public String getFlag() {
        return flag;
    }
}
