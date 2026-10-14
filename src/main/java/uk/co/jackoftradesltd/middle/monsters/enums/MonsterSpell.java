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

package uk.co.jackoftradesltd.middle.monsters.enums;

import uk.co.jackoftradesltd.channel.utils.Flag;

import static uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpellTypeEnum.*;

/**
 * The complete set of monster spells/abilities (bolts, balls, breaths, summons,
 * teleports, annoyances, …), each tagged with the {@link MonsterSpellTypeEnum}
 * categories it belongs to (stored as a {@link Flag} set so a spell can have
 * several).
 *
 * <p>This is the Java port of the 93 {@code RSF()} rows of C's {@code list-mon-spells.h}, in the
 * same order, so a constant's {@link #ordinal()} is the C {@code RSF_} index. {@link #RSF_NONE}
 * is index 0 and {@link #RSF_MAX} is the last row, as in C, where it is the count that sizes
 * {@code RSF_SIZE} and bounds every loop over the spell table. Each row's {@code RST_} bit
 * expression becomes the {@link Flag} set from the constructor: C ORs the bits into the
 * {@code type} field of its {@code mon_spell_types[]} table in {@code mon-spell.c}, which is
 * built from the same header, whereas here the table and the index are the one enum.
 *
 * <p>The names are also the {@code name:} values in {@code monster_spell.txt}, with the
 * {@code RSF_} prefix added; the {@code MonsterSpellAssembler} looks a record up with
 * {@code valueOf("RSF_" + name)}. C's {@code r_info_spell_flags[]} is built from the same
 * header, so it too accepts {@code NONE} and {@code MAX} as names, and so does the lookup here.
 *
 * <p>Each constant carries a one-line description taken from the {@code lore:} text of its
 * record in {@code monster_spell.txt}; the header has no descriptions of its own.
 * {@code MonsterSpellTableTest} pins the order, the names and the type sets against the header.
 *
 * <p>Class MonsterSpell coded before 261011, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public enum MonsterSpell {
    /** No spell. Index 0; C's {@code mon_spell_is_valid()} rejects it, as it does {@link #RSF_MAX}. */
    RSF_NONE(new MonsterSpellTypeEnum[]{}),
    /** Shriek for help. */
    RSF_SHRIEK(new MonsterSpellTypeEnum[]{RST_ANNOY, RST_INNATE}),
    /** Lash the player when nearby. */
    RSF_WHIP(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE}),
    /** Spit at the player from a distance. */
    RSF_SPIT(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE}),
    /** Fling pebbles or sling lead or seeker shots (an archery bolt). */
    RSF_SHOT(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE, RST_ARCHERY}),
    /** Fire small, large or seeker arrows (an archery bolt). */
    RSF_ARROW(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE, RST_ARCHERY}),
    /** Fire small, large or seeker crossbow bolts (an archery bolt). */
    RSF_BOLT(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE, RST_ARCHERY}),
    /** Breathe acid. */
    RSF_BR_ACID(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe lightning. */
    RSF_BR_ELEC(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe fire. */
    RSF_BR_FIRE(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe frost. */
    RSF_BR_COLD(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe poison. */
    RSF_BR_POIS(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe nether. */
    RSF_BR_NETH(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe light. */
    RSF_BR_LIGHT(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe darkness. */
    RSF_BR_DARK(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe sound. */
    RSF_BR_SOUN(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe chaos. */
    RSF_BR_CHAO(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe disenchantment. */
    RSF_BR_DISE(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe nexus. */
    RSF_BR_NEXU(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe time. */
    RSF_BR_TIME(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe inertia. */
    RSF_BR_INER(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe gravity. */
    RSF_BR_GRAV(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe shards. */
    RSF_BR_SHAR(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe plasma. */
    RSF_BR_PLAS(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe force. */
    RSF_BR_WALL(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Breathe mana. */
    RSF_BR_MANA(new MonsterSpellTypeEnum[]{RST_BREATH, RST_INNATE}),
    /** Throw boulders. */
    RSF_BOULDER(new MonsterSpellTypeEnum[]{RST_BOLT, RST_INNATE}),
    /** Weave webs. */
    RSF_WEAVE(new MonsterSpellTypeEnum[]{RST_ANNOY, RST_INNATE}),
    /** Produce acid balls. */
    RSF_BA_ACID(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Produce lightning balls. */
    RSF_BA_ELEC(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Produce fire balls (firestorms at higher power). */
    RSF_BA_FIRE(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Produce balls of cold (blizzards at higher power). */
    RSF_BA_COLD(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Produce poison balls (storms of poison at higher power). */
    RSF_BA_POIS(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Produce shardstorms (storms of knives at higher power). */
    RSF_BA_SHAR(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Produce nether balls (nether storms at higher power). */
    RSF_BA_NETH(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Produce water balls. */
    RSF_BA_WATE(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Invoke mana storms. */
    RSF_BA_MANA(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Cast orbs of draining. */
    RSF_BA_HOLY(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Produce balls of darkness (darkness storms at higher power). */
    RSF_BA_DARK(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Produce balls of light (starbursts at higher power). */
    RSF_BA_LIGHT(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Create storms (tempests at higher power). */
    RSF_STORM(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Drain mana. */
    RSF_DRAIN_MANA(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    /** Cause mind blasting. */
    RSF_MIND_BLAST(new MonsterSpellTypeEnum[]{RST_DIRECT, RST_ANNOY}),
    /** Cause brain smashing. */
    RSF_BRAIN_SMASH(new MonsterSpellTypeEnum[]{RST_DIRECT, RST_ANNOY}),
    /** Cause wounds, from light to mortal by spell power. */
    RSF_WOUND(new MonsterSpellTypeEnum[]{RST_DIRECT}),
    /** Produce acid bolts. */
    RSF_BO_ACID(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce lightning bolts. */
    RSF_BO_ELEC(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce fire bolts. */
    RSF_BO_FIRE(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce frost bolts. */
    RSF_BO_COLD(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce poison bolts. */
    RSF_BO_POIS(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce nether bolts. */
    RSF_BO_NETH(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce water bolts. */
    RSF_BO_WATE(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce mana bolts. */
    RSF_BO_MANA(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce plasma bolts. */
    RSF_BO_PLAS(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce ice bolts. */
    RSF_BO_ICE(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Produce magic missiles. */
    RSF_MISSILE(new MonsterSpellTypeEnum[]{RST_BOLT}),
    /** Shoot sparks of lightning (a beam). */
    RSF_BE_ELEC(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Hurl lances of nether, or shoot rays of death at higher power (a beam). */
    RSF_BE_NETH(new MonsterSpellTypeEnum[]{RST_BALL}),
    /** Terrify. */
    RSF_SCARE(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    /** Blind. */
    RSF_BLIND(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    /** Confuse. */
    RSF_CONF(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    /** Slow. */
    RSF_SLOW(new MonsterSpellTypeEnum[]{RST_ANNOY, RST_HASTE}),
    /** Paralyze. */
    RSF_HOLD(new MonsterSpellTypeEnum[]{RST_ANNOY, RST_HASTE}),
    /** Haste the caster. */
    RSF_HASTE(new MonsterSpellTypeEnum[]{RST_HASTE}),
    /** Heal the caster. */
    RSF_HEAL(new MonsterSpellTypeEnum[]{RST_HEAL}),
    /** Heal kin. */
    RSF_HEAL_KIN(new MonsterSpellTypeEnum[]{RST_HEAL_OTHER}),
    /** Blink the caster a short distance. */
    RSF_BLINK(new MonsterSpellTypeEnum[]{RST_TACTIC, RST_ESCAPE}),
    /** Teleport the caster. */
    RSF_TPORT(new MonsterSpellTypeEnum[]{RST_ESCAPE}),
    /** Teleport the player to the caster. */
    RSF_TELE_TO(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    /** Teleport the caster toward the player. */
    RSF_TELE_SELF_TO(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    /** Teleport the player away. */
    RSF_TELE_AWAY(new MonsterSpellTypeEnum[]{RST_ESCAPE}),
    /** Teleport the player to another level. */
    RSF_TELE_LEVEL(new MonsterSpellTypeEnum[]{RST_ESCAPE}),
    /** Create darkness. */
    RSF_DARKNESS(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    /** Create traps. */
    RSF_TRAPS(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    /** Cause amnesia. */
    RSF_FORGET(new MonsterSpellTypeEnum[]{RST_ANNOY}),
    /** Change shape. */
    RSF_SHAPECHANGE(new MonsterSpellTypeEnum[]{RST_TACTIC}),
    /** Summon similar monsters. */
    RSF_S_KIN(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon greater demons. */
    RSF_S_HI_DEMON(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon a monster. */
    RSF_S_MONSTER(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon monsters. */
    RSF_S_MONSTERS(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon animals. */
    RSF_S_ANIMAL(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon spiders. */
    RSF_S_SPIDER(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon hounds. */
    RSF_S_HOUND(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon hydras. */
    RSF_S_HYDRA(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon an ainu. */
    RSF_S_AINU(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon a demon. */
    RSF_S_DEMON(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon an undead. */
    RSF_S_UNDEAD(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon a dragon. */
    RSF_S_DRAGON(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon greater undead. */
    RSF_S_HI_UNDEAD(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon ancient dragons. */
    RSF_S_HI_DRAGON(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon ringwraiths. */
    RSF_S_WRAITH(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Summon uniques. */
    RSF_S_UNIQUE(new MonsterSpellTypeEnum[]{RST_SUMMON}),
    /** Count sentinel, one past the last real spell; the bound of every C loop over the spell table and the basis of {@code RSF_SIZE}. Not a spell. */
    RSF_MAX(new MonsterSpellTypeEnum[]{}),
    ;

    /**
     * The set of spell-type categories this spell belongs to: the Java form of the {@code RST_}
     * bit expression in the spell's row of {@code list-mon-spells.h}, held as a set of enum
     * values rather than as the OR of C's bit values (a {@code Flag} test replaces C's
     * {@code type & RST_X}). Empty for {@link #RSF_NONE} and {@link #RSF_MAX}, the two rows C
     * gives a type of 0. Built once in the constructor and not changed afterwards, but the set
     * itself is returned by {@link #getTypes()} rather than a copy.
     *
     * <p>Field spellTypes coded before 261011, commented in full on 261014.
     */
    private final Flag<MonsterSpellTypeEnum> spellTypes;

    /**
     * Bind a spell to its categories, building the {@link Flag} set from the
     * given array. An empty array gives an empty set, which is how {@link #RSF_NONE} and
     * {@link #RSF_MAX} carry C's type of 0.
     *
     * <p>Constructor MonsterSpell coded before 261011, commented in full on 261014.
     *
     * @param types the spell-type categories this spell belongs to
     */
    private MonsterSpell(MonsterSpellTypeEnum[] types) {
        spellTypes = new Flag<>(MonsterSpellTypeEnum.class);

        for (MonsterSpellTypeEnum type : types) {
            spellTypes.on(type);
        }
    }

    /**
     * The spell-type categories this spell belongs to, the equivalent of the {@code type} column
     * for this spell in C's {@code mon_spell_types[]}. The caller gets the stored set, not a copy,
     * so it should only test it (for example {@code has(RST_INNATE)}), as
     * {@code MonsterAssembler} does when it defaults a race's innate and spell frequencies.
     *
     * <p>Function getTypes coded before 261011, commented in full on 261014.
     *
     * @return the set of spell-type categories this spell belongs to
     */
    public Flag<MonsterSpellTypeEnum> getTypes() {
        return spellTypes;
    }
}
