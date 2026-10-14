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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

/**
 * The categories a monster spell can belong to, the Java form of C's {@code enum mon_spell_type}
 * (the {@code RST_*} bit flags) in {@code mon-spell.h}. A spell has one or more of them: C writes
 * them as an OR of bits in the second column of each row of {@code list-mon-spells.h}, for
 * example {@code RST_BOLT | RST_INNATE} for {@code WHIP}, and {@link MonsterSpell} holds the same
 * choice as a {@code Flag} set built from this enum.
 *
 * <p>All fourteen constants were compared with the C enum this pass and agree in name and order.
 * C gives each a single bit, {@code RST_BOLT} being {@code 0x0001} up to {@code RST_ARCHERY}
 * being {@code 0x1000}; here the constant itself is the member of the set, so no bit value is
 * stored and an ordinal is one more than the C bit's position, {@code RST_NONE} leading both
 * enums as 0.
 *
 * <p>The second column, {@link #isDamage()}, stands for C's {@code RST_DAMAGE} mask,
 * {@code RST_BOLT | RST_BALL | RST_BREATH | RST_DIRECT}. C tests a whole spell against that mask
 * ({@code mon_spell_has_damage()} in {@code mon-spell.c}, and the "desperate" filter in
 * {@code make_ranged_attack()} in {@code mon-attack.c}); here the flag belongs to a single type, so
 * a spell deals damage when any of its types does. {@code RST_DIRECT} spells such as
 * {@code MIND_BLAST} also carry {@code RST_ANNOY}, and are damaging because of the first.
 *
 * <p>The readers of the types are scattered through C: {@code create_mon_spell_mask()} callers in
 * {@code mon-predicate.c} ({@code monster_breathes}, {@code monster_has_innate_spells},
 * {@code monster_has_non_innate_spells}, {@code monster_loves_archery}), {@code mon-init.c}
 * ({@code parse_monster_spells}, which defaults the innate and spell frequencies),
 * {@code mon-lore.c} ({@code lore_append_spells}), {@code mon-util.c}
 * ({@code monster_change_shape}, for the summon mask) and {@code mon-attack.c}
 * ({@code make_ranged_attack}). Of these the Java code reads only {@code RST_INNATE} and
 * {@code RST_BREATH}, in {@code MonsterAssembler}'s frequency defaults; nothing calls
 * {@link #isDamage()} yet. {@code MonsterSpellTableTest} pins the number of spells carrying each
 * type, and {@code MonsterSpellTypeEnumTest} pins the order and the damage flag.
 *
 * <p>Enum MonsterSpellTypeEnum coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public enum MonsterSpellTypeEnum {
    /**
     * C's {@code RST_NONE}, {@code 0x0000}: no type. No row of {@code list-mon-spells.h} names it;
     * the {@code NONE} and {@code MAX} rows write a bare {@code 0}, which {@link MonsterSpell}
     * holds as an empty set. C also uses it as the terminator of the variable argument list of
     * {@code create_mon_spell_mask()}, which has no Java counterpart here. Not damaging.
     *
     * <p>Constant RST_NONE coded before 261014, commented in full on 261014.
     */
    RST_NONE(false),
    /**
     * C's {@code RST_BOLT}, {@code 0x0001}: a bolt, stopped by the first creature in its path.
     * Seventeen spells carry it, among them {@code WHIP}, {@code BOULDER}, the {@code BO_*}
     * bolts and {@code MISSILE}. Damaging.
     *
     * <p>Constant RST_BOLT coded before 261014, commented in full on 261014.
     */
    RST_BOLT(true),
    /**
     * C's {@code RST_BALL}, {@code 0x0002}: a ball, which the C comment says covers beams as well
     * ({@code BE_ELEC}, {@code BE_NETH}). Fifteen spells carry it, among them the {@code BA_*}
     * balls and {@code STORM}. Damaging.
     *
     * <p>Constant RST_BALL coded before 261014, commented in full on 261014.
     */
    RST_BALL(true),
    /**
     * C's {@code RST_BREATH}, {@code 0x0004}: a breath. Nineteen spells carry it, all the
     * {@code BR_*} breaths and each also {@link #RST_INNATE}. Damaging. C's
     * {@code monster_breathes()} and the breath half of the frequency defaults in
     * {@code parse_monster_spells} test it.
     *
     * <p>Constant RST_BREATH coded before 261014, commented in full on 261014.
     */
    RST_BREATH(true),
    /**
     * C's {@code RST_DIRECT}, {@code 0x0008}: a direct attack that is not projected, so it needs
     * no clear path. Three spells carry it: {@code MIND_BLAST}, {@code BRAIN_SMASH} and
     * {@code WOUND}. Damaging.
     *
     * <p>Constant RST_DIRECT coded before 261014, commented in full on 261014.
     */
    RST_DIRECT(true),
    /**
     * C's {@code RST_ANNOY}, {@code 0x0010}: an irritant, usually not fatal. Fifteen spells carry
     * it, among them {@code SHRIEK}, {@code SCARE}, {@code BLIND}, {@code TELE_TO},
     * {@code TRAPS} and {@code FORGET}. Not damaging, even on {@code MIND_BLAST} and
     * {@code BRAIN_SMASH}, which are damaging through {@link #RST_DIRECT}.
     *
     * <p>Constant RST_ANNOY coded before 261014, commented in full on 261014.
     */
    RST_ANNOY(false),
    /**
     * C's {@code RST_HASTE}, {@code 0x0020}: a spell that gives the caster a speed advantage.
     * Three spells carry it: {@code HASTE}, and {@code SLOW} and {@code HOLD}, which gain the
     * advantage by crippling the player and so also carry {@link #RST_ANNOY}. Not damaging.
     *
     * <p>Constant RST_HASTE coded before 261014, commented in full on 261014.
     */
    RST_HASTE(false),
    /**
     * C's {@code RST_HEAL}, {@code 0x0040}: the caster heals itself. Only {@code HEAL} carries it.
     * Not damaging.
     *
     * <p>Constant RST_HEAL coded before 261014, commented in full on 261014.
     */
    RST_HEAL(false),
    /**
     * C's {@code RST_HEAL_OTHER}, {@code 0x0080}: the caster heals another monster. Only
     * {@code HEAL_KIN} carries it. Not damaging.
     *
     * <p>Constant RST_HEAL_OTHER coded before 261014, commented in full on 261014.
     */
    RST_HEAL_OTHER(false),
    /**
     * C's {@code RST_TACTIC}, {@code 0x0100}: a spell that gets the caster a better position. Two
     * spells carry it: {@code BLINK}, which is also {@link #RST_ESCAPE}, and
     * {@code SHAPECHANGE}. Not damaging.
     *
     * <p>Constant RST_TACTIC coded before 261014, commented in full on 261014.
     */
    RST_TACTIC(false),
    /**
     * C's {@code RST_ESCAPE}, {@code 0x0200}: a spell the caster uses to get away. Four spells
     * carry it: {@code BLINK}, {@code TPORT}, {@code TELE_AWAY} and {@code TELE_LEVEL}. Not
     * damaging.
     *
     * <p>Constant RST_ESCAPE coded before 261014, commented in full on 261014.
     */
    RST_ESCAPE(false),
    /**
     * C's {@code RST_SUMMON}, {@code 0x0400}: a summons. Sixteen spells carry it, every
     * {@code S_*} spell from {@code S_KIN} to {@code S_UNIQUE}. Not damaging. C's
     * {@code make_ranged_attack()} drops them all when {@code summon_possible()} fails, and
     * {@code monster_change_shape()} uses the same mask.
     *
     * <p>Constant RST_SUMMON coded before 261014, commented in full on 261014.
     */
    RST_SUMMON(false),
    /**
     * C's {@code RST_INNATE}, {@code 0x0800}: an innate ability rather than a cast spell, the
     * line C's lore and frequency code draw between the two. Twenty-seven spells carry it: the
     * {@code BR_*} breaths, {@code SHRIEK}, {@code WHIP}, {@code SPIT}, {@code SHOT},
     * {@code ARROW}, {@code BOLT}, {@code BOULDER} and {@code WEAVE}. Not damaging in itself;
     * the breaths and bolts among them are damaging through their own type.
     *
     * <p>Constant RST_INNATE coded before 261014, commented in full on 261014.
     */
    RST_INNATE(false),
    /**
     * C's {@code RST_ARCHERY}, {@code 0x1000}: a ranged attack made by shooting. Three spells
     * carry it, {@code SHOT}, {@code ARROW} and {@code BOLT}, each also {@link #RST_BOLT} and
     * {@link #RST_INNATE}. Not damaging in itself; it is the bolt type that makes them so. C's
     * {@code monster_loves_archery()} tests it.
     *
     * <p>Constant RST_ARCHERY coded before 261014, commented in full on 261014.
     */
    RST_ARCHERY(false);

    /**
     * Whether spells of this type deal damage: true for exactly {@link #RST_BOLT},
     * {@link #RST_BALL}, {@link #RST_BREATH} and {@link #RST_DIRECT}, the four members of C's
     * {@code RST_DAMAGE} mask. C has no such column, it ORs the four bits into a mask; the field
     * is that mask turned into a per-constant answer. Set once in the constructor.
     *
     * <p>Field isDamage coded before 261014, commented in full on 261014.
     */
    private final boolean isDamage;

    /**
     * Bind a spell type to whether it is damaging.
     *
     * <p>Constructor MonsterSpellTypeEnum coded before 261014, commented in full on 261014.
     *
     * @param isDamage whether the type is one of C's {@code RST_DAMAGE} members
     */
    private MonsterSpellTypeEnum(boolean isDamage) {
        this.isDamage = isDamage;
    }


    /**
     * Whether spells of this type deal damage, the per-type form of testing a spell's bits against
     * C's {@code RST_DAMAGE}. A spell as a whole is damaging when any of its types answers true,
     * which is how C's {@code mon_spell_has_damage()} reads {@code type & RST_DAMAGE}. Nothing
     * outside the tests calls it yet.
     *
     * <p>Function isDamage coded before 261014, commented in full on 261014.
     *
     * @return whether spells of this type deal damage
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isDamage() {
        return isDamage;
    }
}