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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.player.enums;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.utils.Flag;

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

    private final Flag<PlayerRedraw> redrawFlags;
    private final Flag<PlayerUpkeepEnum> upkeepFlags;

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