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

package uk.co.jackoftrades.middle.player.enums;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.utils.Flag;

/**
 * The catalogue of timed player statuses — the {@code TMD_*} effects such as haste, fear,
 * poison, temporary resistances and the brand/attack buffs — each recording the screen
 * redraws and model recalculations its onset or expiry should trigger.
 *
 * <p>Ports the C {@code TMD_*} timed-effect table ({@code list-player-timed.h} /
 * {@code player-timed.c}). The numeric level of each status is stored on the player; this
 * enum captures the <em>static</em> per-effect metadata — specifically which
 * {@link PlayerRedraw} regions and which {@link PlayerUpkeepEnum} recalculations become dirty
 * whenever the effect's value changes.
 *
 * <p><b>Why carry the flag sets here:</b> when a timed status rises or falls the engine must
 * refresh exactly the affected display and derived state and nothing more (the dirty-region
 * discipline described on {@link PlayerRedraw} / {@link PlayerUpkeepEnum}). Attaching those
 * flag sets to the effect keeps the mapping declarative and in one place. Most effects only
 * touch the status line and the bonus recalculation ({@code PR_STATUS} + {@code PU_BONUS});
 * the notable exceptions are sense-altering statuses — e.g. {@code TMD_BLIND} and
 * {@code TMD_IMAGE} dirty the map and monster/item views, and see-invisible / infravision
 * additionally re-evaluate monster visibility.
 *
 * <p>Each constant declares its flags as plain arrays which the constructor folds into a
 * {@link uk.co.jackoftrades.backend.utils.Flag} bitset for compact storage and querying.
 *
 * @author Rowan Crowther
 */
public enum TimedEffect {
    TMD_NONE(new PlayerRedraw[]{}, new PlayerUpkeepEnum[]{}),
    TMD_FAST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_SLOW(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_BLIND(new PlayerRedraw[]{PlayerRedraw.PR_MAP}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_UPDATE_VIEW, PlayerUpkeepEnum.PU_MONSTERS}),
    TMD_PARALYZED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_CONFUSED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_AFRAID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_IMAGE(new PlayerRedraw[]{PlayerRedraw.PR_MAP, PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_POISONED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_CUT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_STUN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_FOOD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_PROTEVIL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_INVULN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_HERO(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_SHERO(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_SHIELD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_BLESSED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_SINVIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS, PlayerUpkeepEnum.PU_MONSTERS}),
    TMD_SINFRA(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS, PlayerUpkeepEnum.PU_MONSTERS}),
    TMD_OPP_ACID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_OPP_ELEC(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_OPP_FIRE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_OPP_COLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_OPP_POIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_OPP_CONF(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_AMNESIA(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_TELEPATHY(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_STONESKIN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_TERROR(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_SPRINT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_BOLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_SCRAMBLE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_TRAPSAFE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_FASTCAST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_ACID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_ELEC(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_FIRE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_COLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_POIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_CONF(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_EVIL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_DEMON(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_VAMP(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_HEAL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_COMMAND(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_ATT_RUN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_COVERTRACKS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_POWERSHOT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_TAUNT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_BLOODLUST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_BLACKBREATH(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_STEALTH(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS}),
    TMD_FREE_ACT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeepEnum[]{PlayerUpkeepEnum.PU_BONUS});

    /**
     * Screen regions to mark for redraw whenever this effect's level changes.
     */
    private final Flag<PlayerRedraw> redrawFlags;
    /** Derived quantities to mark for recalculation whenever this effect's level changes. */
    private final Flag<PlayerUpkeepEnum> upkeepFlags;

    /**
     * Folds the per-effect redraw/upkeep arrays into queryable bitsets.
     *
     * <p>The constants declare their flags as readable arrays; this constructor turns each
     * into a {@link Flag} so the runtime can test and combine them cheaply.
     *
     * @param redrawFlags the {@link PlayerRedraw} regions this effect dirties
     * @param upkeepFlags the {@link PlayerUpkeepEnum} recalculations this effect triggers
     */
    private TimedEffect(PlayerRedraw @NotNull [] redrawFlags, PlayerUpkeepEnum @NotNull [] upkeepFlags) {
        this.redrawFlags = new Flag<PlayerRedraw>(PlayerRedraw.class);
        this.upkeepFlags = new Flag<PlayerUpkeepEnum>(PlayerUpkeepEnum.class);

        for (PlayerRedraw redrawFlag : redrawFlags) {
            this.redrawFlags.on(redrawFlag);
        }

        for (PlayerUpkeepEnum upkeepFlag : upkeepFlags) {
            this.upkeepFlags.on(upkeepFlag);
        }
    }
}