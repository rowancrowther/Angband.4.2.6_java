package uk.co.jackoftrades.middle.player.enums;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.utils.Flag;

public enum TimedEffect {
    TMD_NULL(new PlayerRedraw[]{}, new PlayerUpkeep[]{}),
    TMD_FAST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_SLOW(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_BLIND(new PlayerRedraw[]{PlayerRedraw.PR_MAP}, new PlayerUpkeep[]{PlayerUpkeep.PU_UPDATE_VIEW, PlayerUpkeep.PU_MONSTERS}),
    TMD_PARALYZED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_CONFUSED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_AFRAID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_IMAGE(new PlayerRedraw[]{PlayerRedraw.PR_MAP, PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_POISONED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_CUT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_STUN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_FOOD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_PROTEVIL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_INVULN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_HERO(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_SHERO(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_SHIELD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_BLESSED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_SINVIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS, PlayerUpkeep.PU_MONSTERS}),
    TMD_SINFRA(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS, PlayerUpkeep.PU_MONSTERS}),
    TMD_OPP_ACID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_OPP_ELEC(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_OPP_FIRE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_OPP_COLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_OPP_POIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_OPP_CONF(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_AMNESIA(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_TELEPATHY(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_STONESKIN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_TERROR(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_SPRINT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_BOLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_SCRAMBLE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_TRAPSAFE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_FASTCAST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_ACID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_ELEC(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_FIRE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_COLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_POIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_CONF(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_EVIL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_DEMON(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_VAMP(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_HEAL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_COMMAND(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_ATT_RUN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_COVERTRACKS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_POWERSHOT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_TAUNT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_BLOODLUST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_BLACKBREATH(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_STEALTH(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS}),
    TMD_FREE_ACT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpkeep[]{PlayerUpkeep.PU_BONUS});

    private final Flag<PlayerRedraw> redrawFlags;
    private final Flag<PlayerUpkeep> upkeepFlags;

    private TimedEffect(PlayerRedraw @NotNull [] redrawFlags, PlayerUpkeep @NotNull [] upkeepFlags) {
        this.redrawFlags = new Flag<PlayerRedraw>(PlayerRedraw.class);
        this.upkeepFlags = new Flag<PlayerUpkeep>(PlayerUpkeep.class);

        for (PlayerRedraw redrawFlag : redrawFlags) {
            this.redrawFlags.on(redrawFlag);
        }

        for (PlayerUpkeep upkeepFlag : upkeepFlags) {
            this.upkeepFlags.on(upkeepFlag);
        }
    }
}