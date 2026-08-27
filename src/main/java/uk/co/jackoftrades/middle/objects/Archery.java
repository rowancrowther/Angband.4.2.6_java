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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.middle.objects.enums.TValue;

/**
 * One row of the launcher-and-ammo assumptions the power calculation prices missile weapons by —
 * the port of C's {@code struct archery} and its three-row table ({@code obj-power.c:47-57}).
 *
 * <p>Power cannot ask what a launcher will actually be fired with, so it assumes: a sling, bow or
 * crossbow is worth whatever damage its matching ammunition would average, and a piece of ammunition
 * is worth whatever a launcher would multiply it by. The three rows are shot, arrow and bolt, and
 * every field is one half of that bargain.
 *
 * <p>The figures assume ordinary — not seeker — ammunition enchanted to {@code +9}, which is why
 * {@link #getLaunchDamage()} is 9 on all three rows.
 *
 * <p><b>Keyed, not indexed.</b> C reaches the table by arithmetic on the tval — {@code obj->tval -
 * TV_SHOT} for ammunition and {@code obj->sval / 10} for launchers — and its comment warns that the
 * ammo tvals must stay consecutive for that to work. The port holds the rows in a map keyed by
 * {@link TValue} instead ({@code ObjectRegistry.archery}), so the ordering of the enum carries no
 * meaning and the warning does not apply.
 *
 * <p>Read only by the power code: {@code ItemObject.ammoDamagePower} prices a launcher from
 * {@link #getAmmoDamage()}, and {@code launcherAmmoDamagePower} prices ammunition from
 * {@link #getLaunchDamage()} and {@link #getLaunchMult()}.
 *
 * <p>Class Archery commented in full on 260827.
 *
 * @author Rowan Crowther
 */
public class Archery {
    /**
     * Which ammunition this row describes — {@code TV_SHOT}, {@code TV_ARROW} or {@code TV_BOLT}.
     * C's {@code ammo_tval}, and the port's map key.
     */
    private TValue ammoType;
    /**
     * The damage this ammunition is assumed to average, used to price the launcher that fires it.
     * C's {@code ammo_dam}.
     */
    private int ammoDamage;
    /**
     * The to-damage bonus a launcher is assumed to carry, used to price ego ammunition. C's
     * {@code launch_dam}, and 9 on every row.
     */
    private int launchDamage;
    /**
     * Twice the launcher's damage multiplier, used to price any ammunition. C's
     * {@code launch_mult}; doubled so that half-multipliers survive integer arithmetic, which is why
     * {@code launcherAmmoDamagePower} divides by {@code 2 * MAX_BLOWS} rather than {@code MAX_BLOWS}.
     */
    private int launchMult;

    /**
     * Build one row of the archery table from its four figures.
     *
     * <p>Constructor Archery commented in full on 260827.
     *
     * @param ammoType     the ammunition tval this row describes
     * @param ammoDamage   assumed average damage of that ammunition
     * @param launchDamage assumed to-damage bonus on the launcher
     * @param launchMult   twice the launcher's damage multiplier
     */
    public Archery(TValue ammoType, int ammoDamage, int launchDamage, int launchMult) {
        this.ammoType = ammoType;
        this.ammoDamage = ammoDamage;
        this.launchDamage = launchDamage;
        this.launchMult = launchMult;
    }

    /**
     * @return the ammunition tval this row describes - C's {@code archery[].ammo_tval}
     */
    public TValue getAmmoType() {
        return ammoType;
    }

    /**
     * @return the assumed average damage of this ammunition, for pricing the launcher that fires it
     */
    public int getAmmoDamage() {
        return ammoDamage;
    }

    /**
     * @return the assumed to-damage bonus on the launcher, for pricing ego ammunition
     */
    public int getLaunchDamage() {
        return launchDamage;
    }

    /**
     * @return twice the launcher's damage multiplier - the doubling is C's, and the callers divide
     * it back out
     */
    public int getLaunchMult() {
        return launchMult;
    }
}
