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

package uk.co.jackoftrades.middle.monsters.enums;

import uk.co.jackoftrades.backend.utils.Flag;

import static uk.co.jackoftrades.middle.monsters.enums.MonsterSpellTypeEnum.*;

/**
 * The complete set of monster spells/abilities (bolts, balls, breaths, summons,
 * teleports, annoyances, …), each tagged with the {@link MonsterSpellTypeEnum}
 * categories it belongs to (stored as a {@link Flag} set so a spell can have
 * several). This is the Java port of the C original's {@code RSF_*} spell list
 * ({@code src/list-mon-spells.h}); the constants are self-describing and
 * documented collectively here.
 *
 * @author Rowan Crowther
 */
public enum MonsterSpell {
    RSF_NONE(new MonsterSpellTypeEnum[]{}),
    RSF_SHRIEK(new MonsterSpellTypeEnum[]{RST_ANNOY, RST_INNATE}),
    RSF_WHIP(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE}),
    RSF_SPIT(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE}),
    RSF_SHOT(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE, RST_ARCHERY}),
    RSF_ARROW(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE, RST_ARCHERY}),
    RSF_BOLT(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE, RST_ARCHERY}),
    RSF_ACID(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_ELEC(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_FIRE(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_COLD(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_POIS(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_NETH(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_LIGHT(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_DARK(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_SOUN(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_CHAO(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_DISE(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_NEXU(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_TIME(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_INER(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_GRAV(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_SHAR(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_PLAS(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_WALL(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_MANA(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_ACID(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_ELEC(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_FIRE(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_COLD(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_POIS(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_NETH(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_LIGHT(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_DARK(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_SOUN(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_CHAO(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_DISE(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_NEXU(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_TIME(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_INER(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_GRAV(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_SHAR(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_PLAS(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_WALL(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BR_MANA(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    RSF_BOULDER(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE}),
    RSF_WEAVE(new MonsterSpellTypeEnum[]{RST_ANNOY, RST_INNATE}),
    RSF_BA_ACID(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_ELEC(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_FIRE(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_COLD(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_POIS(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_SHAR(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_NETH(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_WATE(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_MANA(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_HOLY(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_DARK(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BA_LIGHT(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_STORM(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_DRAIN_MANA(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    RSF_MIND_BLAST(new MonsterSpellTypeEnum[]{RST_DIRECT, RST_ANNOY}),
    RSF_BRAIN_SMASH(new MonsterSpellTypeEnum[]{RST_DIRECT, RST_ANNOY}),
    RSF_WOUND(new MonsterSpellTypeEnum[]{RST_DIRECT}),
    RSF_BO_ACID(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BO_ELEC(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BO_FIRE(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BO_COLD(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BO_POIS(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BO_NETH(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BO_WATE(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BO_MANA(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BO_PLAS(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BO_ICE(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_MISSILE(new MonsterSpellTypeEnum[]{RST_BOLT}),
    RSF_BE_ELEC(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_BE_NETH(new MonsterSpellTypeEnum[]{RST_BALL}),
    RSF_SCARE(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    RSF_BLIND(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    RSF_CONF(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    RSF_SLOW(new MonsterSpellTypeEnum[]{RST_ANNOY, RST_HASTE}),
    RSF_HOLD(new MonsterSpellTypeEnum[]{RST_ANNOY, RST_HASTE}),
    RSF_HASTE(new MonsterSpellTypeEnum[]{RST_HASTE}),
    RSF_HEAL(new MonsterSpellTypeEnum[]{RST_HEAL}),
    RSF_HEAL_KIN(new MonsterSpellTypeEnum[]{RST_HEAL_OTHER}),
    RSF_BLINK(new MonsterSpellTypeEnum[]{RST_TACTIC, RST_ESCAPE}),
    RSF_TPORT(new MonsterSpellTypeEnum[]{RST_ESCAPE}),
    RSF_TELE_TO(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    RSF_TELE_SELF_TO(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    RSF_TELE_AWAY(new MonsterSpellTypeEnum[]{RST_ESCAPE}),
    RSF_TELE_LEVEL(new MonsterSpellTypeEnum[]{RST_ESCAPE}),
    RSF_DARKNESS(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    RSF_TRAPS(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    RSF_FORGET(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    RSF_SHAPECHANGE(new MonsterSpellTypeEnum[]{RST_TACTIC}),
    RSF_S_KIN(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_HI_DEMON(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_MONSTER(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_MONSTERS(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_ANIMAL(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_SPIDER(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_HOUND(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_HYDRA(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_AINU(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_DEMON(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_UNDEAD(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_DRAGON(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_HI_UNDEAD(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_HI_DRAGON(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_WRAITH(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_S_UNIQUE(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    RSF_MAX(new MonsterSpellTypeEnum[]{}),
    ;

    /**
     * The set of spell-type categories this spell belongs to.
     *
     * @author Rowan Crowther
     */
    private final Flag<MonsterSpellTypeEnum> spellTypes;

    /**
     * Bind a spell to its categories, building the {@link Flag} set from the
     * given array.
     *
     * @param types the spell-type categories this spell belongs to
     * @author Rowan Crowther
     */
    private MonsterSpell(MonsterSpellTypeEnum[] types) {
        spellTypes = new Flag<>(MonsterSpellTypeEnum.class);

        for (MonsterSpellTypeEnum type : types) {
            spellTypes.on(type);
        }
    }

    /**
     * @return the set of spell-type categories this spell belongs to
     * @author Rowan Crowther
     */
    private Flag<MonsterSpellTypeEnum> getTypes() {
        return spellTypes;
    }
}
