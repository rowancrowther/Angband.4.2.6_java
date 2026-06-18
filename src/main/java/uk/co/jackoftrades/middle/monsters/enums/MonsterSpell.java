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

package uk.co.jackoftrades.middle.monsters.enums;

import uk.co.jackoftrades.backend.utils.Flag;

import static uk.co.jackoftrades.middle.monsters.enums.MonsterSpellType.*;

public enum MonsterSpell {
    RSF_NONE(new MonsterSpellType[]{}),
    RSF_SHRIEK(new MonsterSpellType[]{RST_ANNOY, RST_INNATE}),
    RSF_WHIP(new MonsterSpellType[]{RST_BOLT, RST_INNATE}),
    RSF_SPIT(new MonsterSpellType[]{RST_BOLT, RST_INNATE}),
    RSF_SHOT(new MonsterSpellType[]{RST_BOLT, RST_INNATE, RST_ARCHERY}),
    RSF_ARROW(new MonsterSpellType[]{RST_BOLT, RST_INNATE, RST_ARCHERY}),
    RSF_BOLT(new MonsterSpellType[]{RST_BOLT, RST_INNATE, RST_ARCHERY}),
    RSF_ACID(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_ELEC(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_FIRE(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_COLD(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_POIS(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_NETH(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_LIGHT(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_DARK(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_SOUN(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_CHAO(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_DISE(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_NEXU(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_TIME(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_INER(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_GRAV(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_SHAR(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_PLAS(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_WALL(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_MANA(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_ACID(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_ELEC(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_FIRE(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_COLD(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_POIS(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_NETH(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_LIGHT(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_DARK(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_SOUN(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_CHAO(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_DISE(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_NEXU(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_TIME(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_INER(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_GRAV(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_SHAR(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_PLAS(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_WALL(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BR_MANA(new MonsterSpellType[]{RST_BREATH, RST_INNATE}),
    RSF_BOULDER(new MonsterSpellType[]{RST_BOLT, RST_INNATE}),
    RSF_WEAVE(new MonsterSpellType[]{RST_ANNOY, RST_INNATE}),
    RSF_BA_ACID(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_ELEC(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_FIRE(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_COLD(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_POIS(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_SHAR(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_NETH(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_WATE(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_MANA(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_HOLY(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_DARK(new MonsterSpellType[]{RST_BALL}),
    RSF_BA_LIGHT(new MonsterSpellType[]{RST_BALL}),
    RSF_STORM(new MonsterSpellType[]{RST_BALL}),
    RSF_DRAIN_MANA(new MonsterSpellType[]{RST_ANNOY}),
    RSF_MIND_BLAST(new MonsterSpellType[]{RST_DIRECT, RST_ANNOY}),
    RSF_BRAIN_SMASH(new MonsterSpellType[]{RST_DIRECT, RST_ANNOY}),
    RSF_WOUND(new MonsterSpellType[]{RST_DIRECT}),
    RSF_BO_ACID(new MonsterSpellType[]{RST_BOLT}),
    RSF_BO_ELEC(new MonsterSpellType[]{RST_BOLT}),
    RSF_BO_FIRE(new MonsterSpellType[]{RST_BOLT}),
    RSF_BO_COLD(new MonsterSpellType[]{RST_BOLT}),
    RSF_BO_POIS(new MonsterSpellType[]{RST_BOLT}),
    RSF_BO_NETH(new MonsterSpellType[]{RST_BOLT}),
    RSF_BO_WATE(new MonsterSpellType[]{RST_BOLT}),
    RSF_BO_MANA(new MonsterSpellType[]{RST_BOLT}),
    RSF_BO_PLAS(new MonsterSpellType[]{RST_BOLT}),
    RSF_BO_ICE(new MonsterSpellType[]{RST_BOLT}),
    RSF_MISSILE(new MonsterSpellType[]{RST_BOLT}),
    RSF_BE_ELEC(new MonsterSpellType[]{RST_BALL}),
    RSF_BE_NETH(new MonsterSpellType[]{RST_BALL}),
    RSF_SCARE(new MonsterSpellType[]{RST_ANNOY}),
    RSF_BLIND(new MonsterSpellType[]{RST_ANNOY}),
    RSF_CONF(new MonsterSpellType[]{RST_ANNOY}),
    RSF_SLOW(new MonsterSpellType[]{RST_ANNOY, RST_HASTE}),
    RSF_HOLD(new MonsterSpellType[]{RST_ANNOY, RST_HASTE}),
    RSF_HASTE(new MonsterSpellType[]{RST_HASTE}),
    RSF_HEAL(new MonsterSpellType[]{RST_HEAL}),
    RSF_HEAL_KIN(new MonsterSpellType[]{RST_HEAL_OTHER}),
    RSF_BLINK(new MonsterSpellType[]{RST_TACTIC, RST_ESCAPE}),
    RSF_TPORT(new MonsterSpellType[]{RST_ESCAPE}),
    RSF_TELE_TO(new MonsterSpellType[]{RST_ANNOY}),
    RSF_TELE_SELF_TO(new MonsterSpellType[]{RST_ANNOY}),
    RSF_TELE_AWAY(new MonsterSpellType[]{RST_ESCAPE}),
    RSF_TELE_LEVEL(new MonsterSpellType[]{RST_ESCAPE}),
    RSF_DARKNESS(new MonsterSpellType[]{RST_ANNOY}),
    RSF_TRAPS(new MonsterSpellType[]{RST_ANNOY}),
    RSF_FORGET(new MonsterSpellType[]{RST_ANNOY}),
    RSF_SHAPECHANGE(new MonsterSpellType[]{RST_TACTIC}),
    RSF_S_KIN(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_HI_DEMON(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_MONSTER(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_MONSTERS(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_ANIMAL(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_SPIDER(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_HOUND(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_HYDRA(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_AINU(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_DEMON(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_UNDEAD(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_DRAGON(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_HI_UNDEAD(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_HI_DRAGON(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_WRAITH(new MonsterSpellType[]{RST_SUMMON}),
    RSF_S_UNIQUE(new MonsterSpellType[]{RST_SUMMON}),
    RSF_MAX(new MonsterSpellType[]{}),
    ;

    private final Flag<MonsterSpellType> spellTypes;

    private MonsterSpell(MonsterSpellType[] types) {
        spellTypes = new Flag<>(MonsterSpellType.class);

        for (MonsterSpellType type : types) {
            spellTypes.on(type);
        }
    }

    private Flag<MonsterSpellType> getTypes() {
        return spellTypes;
    }
}
