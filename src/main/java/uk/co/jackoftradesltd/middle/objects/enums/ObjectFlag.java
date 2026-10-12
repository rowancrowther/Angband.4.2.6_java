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
 * {@code OF_*} object flags, which {@code obj-properties.h} builds as an anonymous enum from
 * {@code list-object-flags.h}; each constant is described individually, and the rules that
 * apply to all of them are set out here.
 *
 * <p>This enum holds only the identity and display label of each flag. Everything else about a
 * flag lives in {@code lib/gamedata/object_property.txt}, where a {@code type:flag} entry's
 * {@code code:} (for example {@code SUST_STR}) is the constant's name without the {@code OF_}
 * prefix. That entry supplies the flag's group ({@code subtype:}, see {@link ObjectFlagType}),
 * how the player learns it ({@code id-type:}, see {@link ObjectFlagID}), its power rating, its
 * identification message and its object-information text; the per-constant notes below quote
 * the group, identification and text, and leave the power ratings to the data file. The flag
 * set operations C wraps in {@code of_has()}, {@code of_on()} and the other {@code of_*()}
 * macros are not part of this enum.
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
 * <p>Class ObjectFlag coded before 261005, commented in full on 261012.
 *
 * @author Rowan Crowther
 */
public enum ObjectFlag {
    /**
     * Sentinel for "no flag", standing in for the zero C's {@code OF_NONE} holds at the head of
     * its enum. It is not a real flag: the data file cannot name it, and C's flag loops start
     * after it. Its label is the empty string.
     */
    OF_NONE(""),
    /**
     * Sustains strength, so drain effects aimed at the stat fail. The first real flag, index 1.
     * Grouped as {@link ObjectFlagType#OFT_SUST}, identified {@link ObjectFlagID#OFID_NORMAL}
     * (on effect); the data file's message is "Your {name} glows.". Its position is tied to
     * {@code STAT_STR} by {@code sustain_flag()}.
     */
    OF_SUST_STR(" sStr"),
    /**
     * Sustains intelligence. Same group, identification and message as {@link #OF_SUST_STR}; index
     * 2, matching {@code STAT_INT}.
     */
    OF_SUST_INT(" sInt"),
    /**
     * Sustains wisdom. Same group, identification and message as {@link #OF_SUST_STR}; index 3,
     * matching {@code STAT_WIS}.
     */
    OF_SUST_WIS(" sWis"),
    /**
     * Sustains dexterity. Same group, identification and message as {@link #OF_SUST_STR}; index
     * 4, matching {@code STAT_DEX}.
     */
    OF_SUST_DEX(" sDex"),
    /**
     * Sustains constitution. Same group, identification and message as {@link #OF_SUST_STR}; the
     * last sustain, index 5, matching {@code STAT_CON}.
     */
    OF_SUST_CON(" sCon"),
    /**
     * Protection from fear. Grouped as {@link ObjectFlagType#OFT_PROT}, identified on effect;
     * the data file's message is "Your {name} strengthens your courage.".
     */
    OF_PROT_FEAR("pFear"),
    /**
     * Protection from blindness. Grouped as {@link ObjectFlagType#OFT_PROT}, identified on
     * effect; the message is "Your {name} soothes your eyes.".
     */
    OF_PROT_BLIND("pBlnd"),
    /**
     * Protection from confusion. Grouped as {@link ObjectFlagType#OFT_PROT}, identified on
     * effect; the message is "Your {name} clears your thoughts.".
     */
    OF_PROT_CONF("pConf"),
    /**
     * Protection from stunning. Grouped as {@link ObjectFlagType#OFT_PROT}, identified on
     * effect; the message is "Your {name} steadies you.".
     */
    OF_PROT_STUN("pStun"),
    /**
     * Slows the wearer's metabolism ("Slows your metabolism" in object information). A
     * {@link ObjectFlagType#OFT_MISC} ability, identified by the {@link ObjectFlagID#OFID_TIMED}
     * delay rather than at once.
     */
    OF_SLOW_DIGEST("S.Dig"),
    /**
     * Feather falling. A {@link ObjectFlagType#OFT_MISC} ability identified on effect; the
     * message is "Your {name} slows your fall.".
     */
    OF_FEATHER("Feath"),
    /**
     * Regeneration ("Speeds regeneration" in object information). A
     * {@link ObjectFlagType#OFT_MISC} ability identified by the timed delay. Not to be confused
     * with {@link #OF_IMPAIR_HP}, which is the bad flag that slows recovery.
     */
    OF_REGEN("Regen"),
    /**
     * Telepathy ("Grants telepathy"). A {@link ObjectFlagType#OFT_MISC} ability, identified
     * immediately on wielding ({@link ObjectFlagID#OFID_WIELD}). Its label {@code "  ESP"} is
     * padded with two spaces to five columns.
     */
    OF_TELEPATHY("  ESP"),
    /**
     * See invisible ("Grants the ability to see invisible things"). A
     * {@link ObjectFlagType#OFT_MISC} ability identified on wielding.
     */
    OF_SEE_INVIS("S.Inv"),
    /**
     * Free action ("Prevents paralysis"). A {@link ObjectFlagType#OFT_MISC} ability identified on
     * effect; the message is "Your {name} keeps you moving.".
     */
    OF_FREE_ACT("FrAct"),
    /**
     * Hold life ("Sustains your life force"), protecting against experience drain. A
     * {@link ObjectFlagType#OFT_MISC} ability identified on effect; the message is "Your {name}
     * warms your spirit.".
     */
    OF_HOLD_LIFE("HLife"),
    /**
     * Impact: the weapon sometimes creates an earthquake on impact. A
     * {@link ObjectFlagType#OFT_MELEE} flag, so it is meaningful only on melee weapons,
     * identified on effect; the message is "Your {name} causes an earthquake!".
     */
    OF_IMPACT("Impct"),
    /**
     * Blessed ("Blessed by the gods (combat bonuses for holy casters)"). A
     * {@link ObjectFlagType#OFT_MELEE} flag identified on wielding. Its label {@code " Bless"} is
     * six characters, one more than the five C's header says are used; it is copied as C has it.
     */
    OF_BLESSED(" Bless"),
    /**
     * The light source burns out. A
     * {@link ObjectFlagType#OFT_LIGHT} flag, so it applies only to light sources; the data file
     * gives it no player-visible description. Identified on wielding.
     */
    OF_BURNS_OUT("BuOut"),
    /**
     * The light source takes fuel and so can be refuelled. A {@link ObjectFlagType#OFT_LIGHT}
     * flag with no player-visible description, identified on wielding.
     */
    OF_TAKES_FUEL("TFuel"),
    /**
     * The light source needs no fuel. A {@link ObjectFlagType#OFT_LIGHT} flag with no
     * player-visible description, identified on wielding.
     */
    OF_NO_FUEL("NFuel"),
    /**
     * Impaired hitpoint recovery ("Impairs hitpoint recovery"). A {@link ObjectFlagType#OFT_BAD}
     * flag identified by the timed delay; the message is "You feel your {name} is slowing your
     * recovery.".
     */
    OF_IMPAIR_HP("ImpHP"),
    /**
     * Impaired mana recovery ("Impairs mana recovery"). A {@link ObjectFlagType#OFT_BAD} flag
     * identified by the timed delay; the message is "You feel your {name} is slowing your mana
     * recovery.".
     */
    OF_IMPAIR_MANA("ImpSP"),
    /**
     * Constant fear ("Makes you afraid of melee, and worse at shooting and casting spells"). A
     * {@link ObjectFlagType#OFT_BAD} flag identified on wielding. Its label {@code " Fear"} is
     * not the same as {@link #OF_PROT_FEAR}'s {@code "pFear"}.
     */
    OF_AFRAID(" Fear"),
    /**
     * Teleportation ban ("Prevents teleportation"). A {@link ObjectFlagType#OFT_BAD} flag
     * identified on effect; the message is "Your {name} prevents you teleporting.".
     */
    OF_NO_TELEPORT("NoTel"),
    /**
     * Aggravation ("Aggravates creatures nearby"). A {@link ObjectFlagType#OFT_BAD} flag
     * identified by the timed delay; the message is "You notice your {name} aggravating things
     * around you.".
     */
    OF_AGGRAVATE("Aggrv"),
    /**
     * Experience drain ("Drains experience"). A {@link ObjectFlagType#OFT_BAD} flag identified
     * by the timed delay; the message is "You sense your {name} is draining your life.". The
     * opposite number of {@link #OF_HOLD_LIFE}.
     */
    OF_DRAIN_EXP("DrExp"),
    /**
     * Stuck on: the object "Can't be removed" once worn. A {@link ObjectFlagType#OFT_BAD} flag
     * identified on wielding.
     */
    OF_STICKY("Stick"),
    /**
     * Fragile: the object "Can be destroyed if you attempt to remove its curses". A
     * {@link ObjectFlagType#OFT_BAD} flag identified on wielding.
     */
    OF_FRAGILE("Fragl"),
    /**
     * Light of intensity 2. A {@link ObjectFlagType#OFT_LIGHT} flag, so it applies only to light
     * sources; identified on wielding. {@link #OF_LIGHT_3} is the brighter step.
     */
    OF_LIGHT_2("Lght2"),
    /**
     * Light of intensity 3. A {@link ObjectFlagType#OFT_LIGHT} flag identified on wielding; the
     * stronger of the two light-intensity flags.
     */
    OF_LIGHT_3("Lght3"),
    /**
     * Digging power 1, the weakest of the three. Grouped as {@link ObjectFlagType#OFT_DIG},
     * identified on wielding.
     */
    OF_DIG_1(" Dig1"),
    /** Digging power 2. Grouped as {@link ObjectFlagType#OFT_DIG}, identified on wielding. */
    OF_DIG_2(" Dig2"),
    /**
     * Digging power 3, the strongest of the three. Grouped as {@link ObjectFlagType#OFT_DIG},
     * identified on wielding.
     */
    OF_DIG_3(" Dig3"),
    /**
     * The light source explodes. A {@link ObjectFlagType#OFT_LIGHT} flag with no player-visible
     * description, identified on wielding.
     */
    OF_EXPLODE("Expld"),
    /**
     * Trap immunity ("Gives immunity to traps"). A {@link ObjectFlagType#OFT_MISC} ability
     * identified on effect; the message is "Your {name} stops you from setting off the trap.".
     */
    OF_TRAP_IMMUNE("TrpIm"),
    /**
     * The object is suited to throwing. Grouped as {@link ObjectFlagType#OFT_THROW} and
     * identified on wielding; the data file gives it no player-visible description.
     */
    OF_THROWING("Throw"),
    /**
     * Multiplies the object's weight. Grouped as {@link ObjectFlagType#OFT_CURSE_ONLY}: the data
     * file notes it is only relevant as part of a curse, so it cannot be added directly to an
     * object. Identified on wielding. The last real flag, index 38.
     */
    OF_MULTIPLY_WEIGHT("MulWg"),
    /**
     * Count sentinel, the {@code OF_MAX} C's list ends on, one past the last real flag. It is
     * not a real flag, and the data file cannot name it; its label is the empty string.
     */
    OF_MAX("");

    /**
     * The short display label for this flag, the second argument of C's {@code OF()} entry in
     * {@code list-object-flags.h}. C's comment says at most the first five characters are
     * used; the labels are padded with a leading space where they are shorter, so a column of
     * them lines up. {@code OF_BLESSED} is the one exception, a six-character {@code " Bless"}
     * copied from C as it stands, so a caller that wants a five-column display must truncate.
     * {@code OF_NONE} and {@code OF_MAX} carry the empty string.
     *
     * <p>Field flag coded before 261005, commented in full on 261012.
     */
    private String flag;

    /**
     * Bind an object flag to its display label.
     *
     * <p>Constructor ObjectFlag coded before 261005, commented in full on 261012.
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
     * <p>Method getSustainStatFlag coded before 261005, commented in full on 261012.
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
     * <p>Method getFlag coded before 261005, commented in full on 261012.
     *
     * @return this flag's short display label
     */
    @Contract(pure = true)
    public String getFlag() {
        return flag;
    }
}
