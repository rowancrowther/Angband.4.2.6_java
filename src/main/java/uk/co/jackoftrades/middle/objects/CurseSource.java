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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

/**
 * The {@link BonusSource} view of one {@link Curse} on a worn item — the later passes of
 * {@code calcBonuses}' equipment walk, C's {@code obj = curses[index].obj}
 * ({@code player-calcs.c:2012}).
 *
 * <p>In C the object bound on those passes is not the item and not the curse record either: it is
 * the curse's shared template object, one per curse in the game, built once at
 * {@code obj-init.c:175-195}. The port has no such object — the parser flattens it into the
 * {@link Curse} itself — so the accessors below read the flattened fields, and the several
 * quantities that template object never carries are returned as constants:
 *
 * <ul>
 *   <li>{@link #baseAC} is zero. The template's kind is {@code <curse object>}, which has no
 *       armour, so a curse can never add base armour class.</li>
 *   <li>{@link #isDigger} is {@code false}. The template's tval is {@code none}.</li>
 *   <li>{@link #knownToAC}, {@link #knownToHit}, {@link #knownToDam} and {@link #knownResLevel}
 *       are zero, and {@link #flagsKnown} is empty, because the template's {@code known}
 *       counterpart is a zeroed object that only ever receives a kind and an sval
 *       ({@code obj-init.c:188-194}) and nothing in {@code obj-knowledge.c} fills it in.</li>
 * </ul>
 *
 * <p>The consequence is worth stating plainly, because it looks like a bug and is not: when
 * {@code calcBonuses} runs with {@code knownOnly} set, <b>a curse contributes its modifiers and
 * nothing else</b>. Its flags, resistances and combat bonuses are all gated on that blank known
 * object and so evaluate away. Modifiers survive because they are gated on the player's rune
 * knowledge instead, which the caller applies.
 *
 * <p>Class CurseSource coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 * @see ItemSource
 */
public class CurseSource implements BonusSource {
    /**
     * The curse this source reports on; never {@code null}.
     */
    private final Curse curse;

    /**
     * Wraps one curse as a bonus source.
     *
     * <p>The curse's power is not held here. Whether a curse contributes at all is the caller's
     * question — {@code calcBonuses} builds a source only for a curse whose {@link CurseData} power
     * is non-zero, which is C's {@code if (curse[index].power)} ({@code player-calcs.c:2009}).
     *
     * @param curse the curse, from the item's curse map
     */
    public CurseSource(@NotNull Curse curse) {
        this.curse = curse;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Built fresh from {@link Curse#getObjectFlags}, the port's spelling of the template
     * object's {@code obj->flags}.
     */
    @Override
    public Flag<ObjectFlag> flags() {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);

        for (ObjectFlag o : curse.getObjectFlags()) {
            result.on(o);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This is the one accessor that returns real data on every pass. A curse's modifiers are
     * gated on the player's rune knowledge, not on the blank known object, so they apply whether or
     * not {@code knownOnly} is set.
     */
    @Override
    public int modifier(ObjectModifier modifier) {
        return curse.getModifiers().getOrDefault(modifier, 0);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Real data, but only reachable when {@code knownOnly} is clear: with it set the caller
     * consults {@link #knownResLevel} first, which is always zero.
     */
    @Override
    public int resLevel(ElementEnum element) {
        ElementInfo elementInfo = curse.getElInfo().get(element);
        if (elementInfo == null) return 0;
        return elementInfo.getResLevel();
    }

    /**
     * {@inheritDoc}
     *
     * @return always zero — the curse template's known object carries no element information
     */
    @Override
    public int knownResLevel(ElementEnum element) {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @return always zero — the curse template's kind has no armour class
     */
    @Override
    public int baseAC() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The third field of the curse's {@code combat:} line ({@code obj-init.c:1091}).
     */
    @Override
    public int toAC() {
        return curse.getCombatAC();
    }

    /**
     * {@inheritDoc}
     *
     * @return always zero — so a curse's armour bonus is dropped entirely under {@code knownOnly}
     */
    @Override
    public int knownToAC() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The first field of the curse's {@code combat:} line ({@code obj-init.c:1089}).
     */
    @Override
    public int toHit() {
        return curse.getCombatToHit();
    }

    /**
     * {@inheritDoc}
     *
     * @return always zero — so a curse's to-hit bonus is dropped entirely under {@code knownOnly}
     */
    @Override
    public int knownToHit() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The second field of the curse's {@code combat:} line ({@code obj-init.c:1090}).
     */
    @Override
    public int toDam() {
        return curse.getCombatDam();
    }

    /**
     * {@inheritDoc}
     *
     * @return always zero — so a curse's damage bonus is dropped entirely under {@code knownOnly}
     */
    @Override
    public int knownToDam() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code false} — the curse template's tval is {@code none}, so the
     * {@code OF_DIG_*} flags are never read from a curse
     */
    @Override
    public boolean isDigger() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Always empty, and deliberately a fresh set rather than a no-op: the caller keeps one flag
     * variable across passes, so answering with nothing has to mean nothing, not "whatever the last
     * source left there".
     *
     * @return an empty flag set
     */
    @Override
    public Flag<ObjectFlag> flagsKnown() {
        return new Flag<>(ObjectFlag.class);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Reachable only through {@link #isDigger}, which a curse always answers {@code false}, so
     * in practice this is never consulted — it is implemented for completeness rather than use.
     */
    @Override
    public boolean flagSet(ObjectFlag flag) {
        return curse.getObjectFlags().contains(flag);
    }
}
