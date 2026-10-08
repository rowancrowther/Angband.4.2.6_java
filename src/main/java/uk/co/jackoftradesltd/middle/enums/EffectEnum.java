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

package uk.co.jackoftradesltd.middle.enums;

import org.jetbrains.annotations.Contract;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeEnum;

/**
 * The master table of every effect the game can apply (damage, heal, cure,
 * teleport, summon, detect, the various bolt/ball/beam projections, …). Each
 * constant bundles the effect's sub-type, whether it must be aimed, how many
 * arguments it takes, the {@link EffectInfoEnum} formatting category, and the
 * description/menu-format templates used to describe it to the player. This is
 * the Java port of the C original's {@code list-effects.h}, together with the
 * {@code EF_NONE} and {@code EF_MAX} entries that {@code effects.h} wraps around
 * it. The sub-type column is not in {@code list-effects.h}; it records which
 * kind of name {@code effect_subtype()} in {@code effects.c} resolves for that
 * effect. The C {@code effect_kind} table in {@code effects.c} keeps only the
 * aim, info label, description and menu format, so the argument count and info
 * category are Java-side data taken straight from {@code list-effects.h}.
 * <p>
 * Class EffectEnum coded before 260815, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public enum EffectEnum {
    /**
     * Placeholder for "no effect" at index 0. Like the C {@code EF_NONE} in {@code effects.h} it has no
     * row in {@code list-effects.h}; it is declared ahead of the table rows so that ordinals line up.
     * <p>
     * Constant EF_NONE coded before 260815, commented in full on 261001.
     */
    EF_NONE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    /**
     * Effect {@code RANDOM} from {@code list-effects.h}. Description template: {@code "randomly "}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_RANDOM coded before 260815, commented in full on 261001.
     */
    EF_RANDOM(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "randomly ", ""),
    /**
     * Effect {@code DAMAGE} from {@code list-effects.h}. Description template: {@code "does %s damage to
     * the player"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: {@code "hurt"}. Info category: {@link
     * EffectInfoEnum#EFINFO_DICE}. Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_DAMAGE coded before 260815, commented in full on 261001.
     */
    EF_DAMAGE(EffectSubTypeEnum.EST_NONE, false, "hurt", 1, EffectInfoEnum.EFINFO_DICE, "does %s damage to the player", ""),
    /**
     * Effect {@code HEAL_HP} from {@code list-effects.h}. Description template: {@code "heals %s
     * hitpoints%s"}.
     * <p>
     * Aimed: no. Arguments: 2. Info label: {@code "heal"}. Info category: {@link
     * EffectInfoEnum#EFINFO_HEAL}. Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "heal
     * self"}.
     * <p>
     * Constant EF_HEAL_HP coded before 260815, commented in full on 261001.
     */
    EF_HEAL_HP(EffectSubTypeEnum.EST_NONE, false, "heal", 2, EffectInfoEnum.EFINFO_HEAL, "heals %s hitpoints%s", "heal self"),
    /**
     * Effect {@code MON_HEAL_HP} from {@code list-effects.h}. Description template: {@code "heals monster
     * hitpoints"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_MON_HEAL_HP coded before 260815, commented in full on 261001.
     */
    EF_MON_HEAL_HP(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "heals monster hitpoints", ""),
    /**
     * Effect {@code MON_HEAL_KIN} from {@code list-effects.h}. Description template: {@code "heals fellow
     * monster hitpoints"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_MON_HEAL_KIN coded before 260815, commented in full on 261001.
     */
    EF_MON_HEAL_KIN(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "heals fellow monster hitpoints", ""),
    /**
     * Effect {@code NOURISH} from {@code list-effects.h}. Description template: {@code "%s for %s turns
     * (%s percent)"}.
     * <p>
     * Aimed: no. Arguments: 3. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_FOOD}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NOURISH}. Menu format: {@code "%s %s"}.
     * <p>
     * Constant EF_NOURISH coded before 260815, commented in full on 261001.
     */
    EF_NOURISH(EffectSubTypeEnum.EST_NOURISH, false, "", 3, EffectInfoEnum.EFINFO_FOOD, "%s for %s turns (%s percent)", "%s %s"),
    /**
     * Effect {@code CRUNCH} from {@code list-effects.h}. Description template: {@code "crunches"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_CRUNCH coded before 260815, commented in full on 261001.
     */
    EF_CRUNCH(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "crunches", ""),
    /**
     * Effect {@code CURE} from {@code list-effects.h}. Description template: {@code "cures %s"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_CURE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_TMD}. Menu format: {@code "cure %s"}.
     * <p>
     * Constant EF_CURE coded before 260815, commented in full on 261001.
     */
    EF_CURE(EffectSubTypeEnum.EST_TMD, false, "", 1, EffectInfoEnum.EFINFO_CURE, "cures %s", "cure %s"),
    /**
     * Effect {@code TIMED_SET} from {@code list-effects.h}. Description template: {@code "administers %s
     * for %s turns"}.
     * <p>
     * Aimed: no. Arguments: 2. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_TIMED}.
     * Sub-type: {@link EffectSubTypeEnum#EST_TMD}. Menu format: {@code "administer %s"}.
     * <p>
     * Constant EF_TIMED_SET coded before 260815, commented in full on 261001.
     */
    EF_TIMED_SET(EffectSubTypeEnum.EST_TMD, false, "", 2, EffectInfoEnum.EFINFO_TIMED, "administers %s for %s turns", "administer %s"),
    /**
     * Effect {@code TIMED_INC} from {@code list-effects.h}. Description template: {@code "extends %s for
     * %s turns"}.
     * <p>
     * Aimed: no. Arguments: 2. Info label: {@code "dur"}. Info category: {@link
     * EffectInfoEnum#EFINFO_TIMED}. Sub-type: {@link EffectSubTypeEnum#EST_TMD}. Menu format: {@code
     * "extend %s"}.
     * <p>
     * Constant EF_TIMED_INC coded before 260815, commented in full on 261001.
     */
    EF_TIMED_INC(EffectSubTypeEnum.EST_TMD, false, "dur", 2, EffectInfoEnum.EFINFO_TIMED, "extends %s for %s turns", "extend %s"),
    /**
     * Effect {@code TIMED_INC_NO_RES} from {@code list-effects.h}. Description template: {@code "extends
     * %s for %s turns (unresistable)"}.
     * <p>
     * Aimed: no. Arguments: 2. Info label: {@code "dur"}. Info category: {@link
     * EffectInfoEnum#EFINFO_TIMED}. Sub-type: {@link EffectSubTypeEnum#EST_TMD}. Menu format: {@code
     * "extend %s"}.
     * <p>
     * Constant EF_TIMED_INC_NO_RES coded before 260815, commented in full on 261001.
     */
    EF_TIMED_INC_NO_RES(EffectSubTypeEnum.EST_TMD, false, "dur", 2, EffectInfoEnum.EFINFO_TIMED, "extends %s for %s turns (unresistable)", "extend %s"),
    /**
     * Effect {@code MON_TIMED_INC} from {@code list-effects.h}. Description template: {@code "increases
     * monster %s by %s turns"}.
     * <p>
     * Aimed: no. Arguments: 2. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_TIMED}.
     * Sub-type: {@link EffectSubTypeEnum#EST_MON_TMD}. Menu format: none.
     * <p>
     * Constant EF_MON_TIMED_INC coded before 260815, commented in full on 261001.
     */
    EF_MON_TIMED_INC(EffectSubTypeEnum.EST_MON_TMD, false, "", 2, EffectInfoEnum.EFINFO_TIMED, "increases monster %s by %s turns", ""),
    /**
     * Effect {@code TIMED_DEC} from {@code list-effects.h}. Description template: {@code "reduces length
     * of %s by %s turns"}.
     * <p>
     * Aimed: no. Arguments: 2. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_TIMED}.
     * Sub-type: {@link EffectSubTypeEnum#EST_TMD}. Menu format: {@code "reduce %s"}.
     * <p>
     * Constant EF_TIMED_DEC coded before 260815, commented in full on 261001.
     */
    EF_TIMED_DEC(EffectSubTypeEnum.EST_TMD, false, "", 2, EffectInfoEnum.EFINFO_TIMED, "reduces length of %s by %s turns", "reduce %s"),
    /**
     * Effect {@code GLYPH} from {@code list-effects.h}. Description template: {@code "inscribes a glyph
     * beneath you"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_GLYPH}. Menu format: {@code "inscribe a glyph"}.
     * <p>
     * Constant EF_GLYPH coded before 260815, commented in full on 261001.
     */
    EF_GLYPH(EffectSubTypeEnum.EST_GLYPH, false, "", 1, EffectInfoEnum.EFINFO_NONE, "inscribes a glyph beneath you", "inscribe a glyph"),
    /**
     * Effect {@code WEB} from {@code list-effects.h}. Description template: {@code "creates a web"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "create a web"}.
     * <p>
     * Constant EF_WEB coded before 260815, commented in full on 261001.
     */
    EF_WEB(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates a web", "create a web"),
    /**
     * Effect {@code RESTORE_STAT} from {@code list-effects.h}. Description template: {@code "restores your
     * %s"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_STAT}.
     * Sub-type: {@link EffectSubTypeEnum#EST_STAT}. Menu format: {@code "restore %s"}.
     * <p>
     * Constant EF_RESTORE_STAT coded before 260815, commented in full on 261001.
     */
    EF_RESTORE_STAT(EffectSubTypeEnum.EST_STAT, false, "", 1, EffectInfoEnum.EFINFO_STAT, "restores your %s", "restore %s"),
    /**
     * Effect {@code DRAIN_STAT} from {@code list-effects.h}. Description template: {@code "reduces your
     * %s"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_STAT}.
     * Sub-type: {@link EffectSubTypeEnum#EST_STAT}. Menu format: none.
     * <p>
     * Constant EF_DRAIN_STAT coded before 260815, commented in full on 261001.
     */
    EF_DRAIN_STAT(EffectSubTypeEnum.EST_STAT, false, "", 1, EffectInfoEnum.EFINFO_STAT, "reduces your %s", ""),
    /**
     * Effect {@code LOSE_RANDOM_STAT} from {@code list-effects.h}. Description template: {@code "reduces a
     * stat other than %s"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_STAT}.
     * Sub-type: {@link EffectSubTypeEnum#EST_STAT}. Menu format: none.
     * <p>
     * Constant EF_LOSE_RANDOM_STAT coded before 260815, commented in full on 261001.
     */
    EF_LOSE_RANDOM_STAT(EffectSubTypeEnum.EST_STAT, false, "", 1, EffectInfoEnum.EFINFO_STAT, "reduces a stat other than %s", ""),
    /**
     * Effect {@code GAIN_STAT} from {@code list-effects.h}. Description template: {@code "increases your
     * %s"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_STAT}.
     * Sub-type: {@link EffectSubTypeEnum#EST_STAT}. Menu format: none.
     * <p>
     * Constant EF_GAIN_STAT coded before 260815, commented in full on 261001.
     */
    EF_GAIN_STAT(EffectSubTypeEnum.EST_STAT, false, "", 1, EffectInfoEnum.EFINFO_STAT, "increases your %s", ""),
    /**
     * Effect {@code RESTORE_EXP} from {@code list-effects.h}. Description template: {@code "restores your
     * experience"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "restore experience"}.
     * <p>
     * Constant EF_RESTORE_EXP coded before 260815, commented in full on 261001.
     */
    EF_RESTORE_EXP(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "restores your experience", "restore experience"),
    /**
     * Effect {@code GAIN_EXP} from {@code list-effects.h}. Description template: {@code "grants %d
     * experience points"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_CONST}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_GAIN_EXP coded before 260815, commented in full on 261001.
     */
    EF_GAIN_EXP(EffectSubTypeEnum.EST_NONE, false, "", 1, EffectInfoEnum.EFINFO_CONST, "grants %d experience points", ""),
    /**
     * Effect {@code DRAIN_LIGHT} from {@code list-effects.h}. Description template: {@code "drains your
     * light source"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_DRAIN_LIGHT coded before 260815, commented in full on 261001.
     */
    EF_DRAIN_LIGHT(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "drains your light source", ""),
    /**
     * Effect {@code DRAIN_MANA} from {@code list-effects.h}. Description template: {@code "drains some
     * mana"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_DRAIN_MANA coded before 260815, commented in full on 261001.
     */
    EF_DRAIN_MANA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "drains some mana", ""),
    /**
     * Effect {@code RESTORE_MANA} from {@code list-effects.h}. Description template: {@code "restores some
     * mana"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "restore some mana"}.
     * <p>
     * Constant EF_RESTORE_MANA coded before 260815, commented in full on 261001.
     */
    EF_RESTORE_MANA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "restores some mana", "restore some mana"),
    /**
     * Effect {@code REMOVE_CURSE} from {@code list-effects.h}. Description template: {@code "attempts
     * power %s removal of a single curse on an object"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_DICE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "remove curse"}.
     * <p>
     * Constant EF_REMOVE_CURSE coded before 260815, commented in full on 261001.
     */
    EF_REMOVE_CURSE(EffectSubTypeEnum.EST_NONE, false, "", 1, EffectInfoEnum.EFINFO_DICE, "attempts power %s removal of a single curse on an object", "remove curse"),
    /**
     * Effect {@code RECALL} from {@code list-effects.h}. Description template: {@code "returns you from
     * the dungeon or takes you to the dungeon after a short delay"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "recall"}.
     * <p>
     * Constant EF_RECALL coded before 260815, commented in full on 261001.
     */
    EF_RECALL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "returns you from the dungeon or takes you to the dungeon after a short delay", "recall"),
    /**
     * Effect {@code DEEP_DESCENT} from {@code list-effects.h}. Description template: {@code "teleports you
     * up to five dungeon levels lower than the lowest point you have reached so far"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "descend to the depths"}.
     * <p>
     * Constant EF_DEEP_DESCENT coded before 260815, commented in full on 261001.
     */
    EF_DEEP_DESCENT(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "teleports you up to five dungeon levels lower than the lowest point you have reached so far", "descend to the depths"),
    /**
     * Effect {@code ALTER_REALITY} from {@code list-effects.h}. Description template: {@code "creates a
     * new dungeon level"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "alter reality"}.
     * <p>
     * Constant EF_ALTER_REALITY coded before 260815, commented in full on 261001.
     */
    EF_ALTER_REALITY(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates a new dungeon level", "alter reality"),
    /**
     * Effect {@code MAP_AREA} from {@code list-effects.h}. Description template: {@code "maps the area
     * around you"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "map surroundings"}.
     * <p>
     * Constant EF_MAP_AREA coded before 260815, commented in full on 261001.
     */
    EF_MAP_AREA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "maps the area around you", "map surroundings"),
    /**
     * Effect {@code READ_MINDS} from {@code list-effects.h}. Description template: {@code "maps the area
     * around recently detected monsters"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "read minds"}.
     * <p>
     * Constant EF_READ_MINDS coded before 260815, commented in full on 261001.
     */
    EF_READ_MINDS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "maps the area around recently detected monsters", "read minds"),
    /**
     * Effect {@code DETECT_TRAPS} from {@code list-effects.h}. Description template: {@code "detects traps
     * nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect traps"}.
     * <p>
     * Constant EF_DETECT_TRAPS coded before 260815, commented in full on 261001.
     */
    EF_DETECT_TRAPS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects traps nearby", "detect traps"),
    /**
     * Effect {@code DETECT_DOORS} from {@code list-effects.h}. Description template: {@code "detects doors
     * nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect doors"}.
     * <p>
     * Constant EF_DETECT_DOORS coded before 260815, commented in full on 261001.
     */
    EF_DETECT_DOORS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects doors nearby", "detect doors"),
    /**
     * Effect {@code DETECT_STAIRS} from {@code list-effects.h}. Description template: {@code "detects
     * stairs nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect stairs"}.
     * <p>
     * Constant EF_DETECT_STAIRS coded before 260815, commented in full on 261001.
     */
    EF_DETECT_STAIRS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects stairs nearby", "detect stairs"),
    /**
     * Effect {@code DETECT_ORE} from {@code list-effects.h}. Description template: {@code "detects veins
     * nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect veins"}.
     * <p>
     * Constant EF_DETECT_ORE coded before 260815, commented in full on 261001.
     */
    EF_DETECT_ORE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects veins nearby", "detect veins"),
    /**
     * Effect {@code SENSE_GOLD} from {@code list-effects.h}. Description template: {@code "senses gold
     * nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "sense gold"}.
     * <p>
     * Constant EF_SENSE_GOLD coded before 260815, commented in full on 261001.
     */
    EF_SENSE_GOLD(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "senses gold nearby", "sense gold"),
    /**
     * Effect {@code DETECT_GOLD} from {@code list-effects.h}. Description template: {@code "detects gold
     * nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect gold"}.
     * <p>
     * Constant EF_DETECT_GOLD coded before 260815, commented in full on 261001.
     */
    EF_DETECT_GOLD(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects gold nearby", "detect gold"),
    /**
     * Effect {@code SENSE_OBJECTS} from {@code list-effects.h}. Description template: {@code "senses
     * objects nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "sense objects"}.
     * <p>
     * Constant EF_SENSE_OBJECTS coded before 260815, commented in full on 261001.
     */
    EF_SENSE_OBJECTS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "senses objects nearby", "sense objects"),
    /**
     * Effect {@code DETECT_OBJECTS} from {@code list-effects.h}. Description template: {@code "detects
     * objects nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect objects"}.
     * <p>
     * Constant EF_DETECT_OBJECTS coded before 260815, commented in full on 261001.
     */
    EF_DETECT_OBJECTS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects objects nearby", "detect objects"),
    /**
     * Effect {@code DETECT_LIVING_MONSTERS} from {@code list-effects.h}. Description template: {@code
     * "detects living creatures nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect living"}.
     * <p>
     * Constant EF_DETECT_LIVING_MONSTERS coded before 260815, commented in full on 261001.
     */
    EF_DETECT_LIVING_MONSTERS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects living creatures nearby", "detect living"),
    /**
     * Effect {@code DETECT_VISIBLE_MONSTERS} from {@code list-effects.h}. Description template: {@code
     * "detects visible creatures nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect visible"}.
     * <p>
     * Constant EF_DETECT_VISIBLE_MONSTERS coded before 260815, commented in full on 261001.
     */
    EF_DETECT_VISIBLE_MONSTERS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects visible creatures nearby", "detect visible"),
    /**
     * Effect {@code DETECT_INVISIBLE_MONSTERS} from {@code list-effects.h}. Description template: {@code
     * "detects invisible creatures nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect invisible"}.
     * <p>
     * Constant EF_DETECT_INVISIBLE_MONSTERS coded before 260815, commented in full on 261001.
     */
    EF_DETECT_INVISIBLE_MONSTERS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects invisible creatures nearby", "detect invisible"),
    /**
     * Effect {@code DETECT_FEARFUL_MONSTERS} from {@code list-effects.h}. Description template: {@code
     * "detects creatures nearby which are susceptible to fear"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect fearful"}.
     * <p>
     * Constant EF_DETECT_FEARFUL_MONSTERS coded before 260815, commented in full on 261001.
     */
    EF_DETECT_FEARFUL_MONSTERS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects creatures nearby which are susceptible to fear", "detect fearful"),
    /**
     * Effect {@code IDENTIFY} from {@code list-effects.h}. Description template: {@code "identifies a
     * single unknown rune on a selected item"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "identify"}.
     * <p>
     * Constant EF_IDENTIFY coded before 260815, commented in full on 261001.
     */
    EF_IDENTIFY(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "identifies a single unknown rune on a selected item", "identify"),
    /**
     * Effect {@code DETECT_EVIL} from {@code list-effects.h}. Description template: {@code "detects evil
     * creatures nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect evil"}.
     * <p>
     * Constant EF_DETECT_EVIL coded before 260815, commented in full on 261001.
     */
    EF_DETECT_EVIL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects evil creatures nearby", "detect evil"),
    /**
     * Effect {@code DETECT_SOUL} from {@code list-effects.h}. Description template: {@code "detects
     * creatures with a spirit nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "detect souls"}.
     * <p>
     * Constant EF_DETECT_SOUL coded before 260815, commented in full on 261001.
     */
    EF_DETECT_SOUL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects creatures with a spirit nearby", "detect souls"),
    /**
     * Effect {@code CREATE_STAIRS} from {@code list-effects.h}. Description template: {@code "creates a
     * staircase beneath your feet"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "create stairs"}.
     * <p>
     * Constant EF_CREATE_STAIRS coded before 260815, commented in full on 261001.
     */
    EF_CREATE_STAIRS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates a staircase beneath your feet", "create stairs"),
    /**
     * Effect {@code DISENCHANT} from {@code list-effects.h}. Description template: {@code "disenchants one
     * of your wielded items"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "disenchant item"}.
     * <p>
     * Constant EF_DISENCHANT coded before 260815, commented in full on 261001.
     */
    EF_DISENCHANT(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "disenchants one of your wielded items", "disenchant item"),
    /**
     * Effect {@code ENCHANT} from {@code list-effects.h}. Description template: {@code "attempts to
     * magically enhance an item"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_ENCHANT}. Menu format: {@code "enchant item"}.
     * <p>
     * Constant EF_ENCHANT coded before 260815, commented in full on 261001.
     */
    EF_ENCHANT(EffectSubTypeEnum.EST_ENCHANT, false, "", 0, EffectInfoEnum.EFINFO_NONE, "attempts to magically enhance an item", "enchant item"),
    /**
     * Effect {@code RECHARGE} from {@code list-effects.h}. Description template: {@code "tries to recharge
     * a wand or staff, destroying the wand or staff on failure"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: {@code "power"}. Info category: {@link
     * EffectInfoEnum#EFINFO_NONE}. Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code
     * "recharge"}.
     * <p>
     * Constant EF_RECHARGE coded before 260815, commented in full on 261001.
     */
    EF_RECHARGE(EffectSubTypeEnum.EST_NONE, false, "power", 0, EffectInfoEnum.EFINFO_NONE, "tries to recharge a wand or staff, destroying the wand or staff on failure", "recharge"),
    /**
     * Effect {@code PROJECT_LOS} from {@code list-effects.h}. Description template: {@code "%s which are
     * in line of sight"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: {@code "power"}. Info category: {@link
     * EffectInfoEnum#EFINFO_SEEN}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "%s
     * in line of sight"}.
     * <p>
     * Constant EF_PROJECT_LOS coded before 260815, commented in full on 261001.
     */
    EF_PROJECT_LOS(EffectSubTypeEnum.EST_PROJ, false, "power", 1, EffectInfoEnum.EFINFO_SEEN, "%s which are in line of sight", "%s in line of sight"),
    /**
     * Effect {@code PROJECT_LOS_AWARE} from {@code list-effects.h}. Description template: {@code "%s which
     * are in line of sight"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: {@code "power"}. Info category: {@link
     * EffectInfoEnum#EFINFO_SEEN}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "%s
     * in line of sight"}.
     * <p>
     * Constant EF_PROJECT_LOS_AWARE coded before 260815, commented in full on 261001.
     */
    EF_PROJECT_LOS_AWARE(EffectSubTypeEnum.EST_PROJ, false, "power", 1, EffectInfoEnum.EFINFO_SEEN, "%s which are in line of sight", "%s in line of sight"),
    /**
     * Effect {@code ACQUIRE} from {@code list-effects.h}. Description template: {@code "creates good items
     * nearby"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "create good items"}.
     * <p>
     * Constant EF_ACQUIRE coded before 260815, commented in full on 261001.
     */
    EF_ACQUIRE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates good items nearby", "create good items"),
    /**
     * Effect {@code WAKE} from {@code list-effects.h}. Description template: {@code "awakens all nearby
     * sleeping monsters"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "awaken all"}.
     * <p>
     * Constant EF_WAKE coded before 260815, commented in full on 261001.
     */
    EF_WAKE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "awakens all nearby sleeping monsters", "awaken all"),
    /**
     * Effect {@code SUMMON} from {@code list-effects.h}. Description template: {@code "summons %s at the
     * current dungeon level"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_SUMM}.
     * Sub-type: {@link EffectSubTypeEnum#EST_SUMMON}. Menu format: {@code "summon %s"}.
     * <p>
     * Constant EF_SUMMON coded before 260815, commented in full on 261001.
     */
    EF_SUMMON(EffectSubTypeEnum.EST_SUMMON, false, "", 1, EffectInfoEnum.EFINFO_SUMM, "summons %s at the current dungeon level", "summon %s"),
    /**
     * Effect {@code BANISH} from {@code list-effects.h}. Description template: {@code "removes all of a
     * given creature type from the level"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "banish"}.
     * <p>
     * Constant EF_BANISH coded before 260815, commented in full on 261001.
     */
    EF_BANISH(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "removes all of a given creature type from the level", "banish"),
    /**
     * Effect {@code MASS_BANISH} from {@code list-effects.h}. Description template: {@code "removes all
     * nearby creatures"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "banish all"}.
     * <p>
     * Constant EF_MASS_BANISH coded before 260815, commented in full on 261001.
     */
    EF_MASS_BANISH(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "removes all nearby creatures", "banish all"),
    /**
     * Effect {@code PROBE} from {@code list-effects.h}. Description template: {@code "gives you
     * information on the health and abilities of monsters you can see"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "probe"}.
     * <p>
     * Constant EF_PROBE coded before 260815, commented in full on 261001.
     */
    EF_PROBE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "gives you information on the health and abilities of monsters you can see", "probe"),
    /**
     * Effect {@code TELEPORT} from {@code list-effects.h}. Description template: {@code "teleports %s
     * randomly %s"}.
     * <p>
     * Aimed: no. Arguments: 2. Info label: {@code "range"}. Info category: {@link
     * EffectInfoEnum#EFINFO_TELE}. Sub-type: {@link EffectSubTypeEnum#EST_TELEPORT}. Menu format: {@code
     * "teleport %s %s"}.
     * <p>
     * Constant EF_TELEPORT coded before 260815, commented in full on 261001.
     */
    EF_TELEPORT(EffectSubTypeEnum.EST_TELEPORT, false, "range", 2, EffectInfoEnum.EFINFO_TELE, "teleports %s randomly %s", "teleport %s %s"),
    /**
     * Effect {@code TELEPORT_TO} from {@code list-effects.h}. Description template: {@code "teleports
     * toward a target"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_TELEPORT_TO}. Menu format: {@code "teleport to target"}.
     * <p>
     * Constant EF_TELEPORT_TO coded before 260815, commented in full on 261001.
     */
    EF_TELEPORT_TO(EffectSubTypeEnum.EST_TELEPORT_TO, false, "", 0, EffectInfoEnum.EFINFO_NONE, "teleports toward a target", "teleport to target"),
    /**
     * Effect {@code TELEPORT_LEVEL} from {@code list-effects.h}. Description template: {@code "teleports
     * you one level up or down"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "teleport level"}.
     * <p>
     * Constant EF_TELEPORT_LEVEL coded before 260815, commented in full on 261001.
     */
    EF_TELEPORT_LEVEL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "teleports you one level up or down", "teleport level"),
    /**
     * Effect {@code RUBBLE} from {@code list-effects.h}. Description template: {@code "causes rubble to
     * fall around you"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_RUBBLE coded before 260815, commented in full on 261001.
     */
    EF_RUBBLE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "causes rubble to fall around you", ""),
    /**
     * Effect {@code GRANITE} from {@code list-effects.h}. Description template: {@code "causes a granite
     * wall to fall behind you"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_GRANITE coded before 260815, commented in full on 261001.
     */
    EF_GRANITE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "causes a granite wall to fall behind you", ""),
    /**
     * Effect {@code DESTRUCTION} from {@code list-effects.h}. Description template: {@code "destroys an
     * area around you in the shape of a circle radius %d, and blinds you for 1d10+10 turns"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_QUAKE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "destroy area"}.
     * <p>
     * Constant EF_DESTRUCTION coded before 260815, commented in full on 261001.
     */
    EF_DESTRUCTION(EffectSubTypeEnum.EST_PROJ, false, "", 1, EffectInfoEnum.EFINFO_QUAKE, "destroys an area around you in the shape of a circle radius %d, and blinds you for 1d10+10 turns", "destroy area"),
    /**
     * Effect {@code EARTHQUAKE} from {@code list-effects.h}. Description template: {@code "causes an
     * earthquake around you of radius %d"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_QUAKE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_EARTHQUAKE}. Menu format: {@code "cause earthquake"}.
     * <p>
     * Constant EF_EARTHQUAKE coded before 260815, commented in full on 261001.
     */
    EF_EARTHQUAKE(EffectSubTypeEnum.EST_EARTHQUAKE, false, "", 1, EffectInfoEnum.EFINFO_QUAKE, "causes an earthquake around you of radius %d", "cause earthquake"),
    /**
     * Effect {@code LIGHT_LEVEL} from {@code list-effects.h}. Description template: {@code "completely
     * lights up and magically maps the level"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "light level"}.
     * <p>
     * Constant EF_LIGHT_LEVEL coded before 260815, commented in full on 261001.
     */
    EF_LIGHT_LEVEL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "completely lights up and magically maps the level", "light level"),
    /**
     * Effect {@code DARKEN_LEVEL} from {@code list-effects.h}. Description template: {@code "completely
     * darkens up and magically maps the level"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "darken level"}.
     * <p>
     * Constant EF_DARKEN_LEVEL coded before 260815, commented in full on 261001.
     */
    EF_DARKEN_LEVEL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "completely darkens up and magically maps the level", "darken level"),
    /**
     * Effect {@code LIGHT_AREA} from {@code list-effects.h}. Description template: {@code "lights up the
     * surrounding area"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "light area"}.
     * <p>
     * Constant EF_LIGHT_AREA coded before 260815, commented in full on 261001.
     */
    EF_LIGHT_AREA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "lights up the surrounding area", "light area"),
    /**
     * Effect {@code DARKEN_AREA} from {@code list-effects.h}. Description template: {@code "darkens the
     * surrounding area"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "darken area"}.
     * <p>
     * Constant EF_DARKEN_AREA coded before 260815, commented in full on 261001.
     */
    EF_DARKEN_AREA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "darkens the surrounding area", "darken area"),
    /**
     * Effect {@code SPOT} from {@code list-effects.h}. Description template: {@code "creates a ball of %s
     * with radius %d, centred on and hitting the player, with full intensity to radius %d, dealing %s
     * damage at the centre"}.
     * <p>
     * Aimed: no. Arguments: 4. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_SPOT}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "engulf with %s"}.
     * <p>
     * Constant EF_SPOT coded before 260815, commented in full on 261001.
     */
    EF_SPOT(EffectSubTypeEnum.EST_PROJ, false, "dam", 4, EffectInfoEnum.EFINFO_SPOT, "creates a ball of %s with radius %d, centred on and hitting the player, with full intensity to radius %d, dealing %s damage at the centre", "engulf with %s"),
    /**
     * Effect {@code SPHERE} from {@code list-effects.h}. Description template: {@code "creates a ball of
     * %s with radius %d, centred on the player, with full intensity to radius %d, dealing %s damage at the
     * centre"}.
     * <p>
     * Aimed: no. Arguments: 4. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_SPOT}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "project %s"}.
     * <p>
     * Constant EF_SPHERE coded before 260815, commented in full on 261001.
     */
    EF_SPHERE(EffectSubTypeEnum.EST_PROJ, false, "dam", 4, EffectInfoEnum.EFINFO_SPOT, "creates a ball of %s with radius %d, centred on the player, with full intensity to radius %d, dealing %s damage at the centre", "project %s"),
    /**
     * Effect {@code BALL} from {@code list-effects.h}. Description template: {@code "fires a ball of %s
     * with radius %d, dealing %s damage at the centre"}.
     * <p>
     * Aimed: yes. Arguments: 3. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BALL}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "fire
     * a ball of %s"}.
     * <p>
     * Constant EF_BALL coded before 260815, commented in full on 261001.
     */
    EF_BALL(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_BALL, "fires a ball of %s with radius %d, dealing %s damage at the centre", "fire a ball of %s"),
    /**
     * Effect {@code BREATH} from {@code list-effects.h}. Description template: {@code "breathes a cone of
     * %s with width %d degrees, dealing %s damage at the source"}.
     * <p>
     * Aimed: yes. Arguments: 3. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_BREATH}.
     * Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "breathe a cone of %s"}.
     * <p>
     * Constant EF_BREATH coded before 260815, commented in full on 261001.
     */
    EF_BREATH(EffectSubTypeEnum.EST_PROJ, true, "", 3, EffectInfoEnum.EFINFO_BREATH, "breathes a cone of %s with width %d degrees, dealing %s damage at the source", "breathe a cone of %s"),
    /**
     * Effect {@code ARC} from {@code list-effects.h}. Description template: {@code "produces a cone of %s
     * with width %d degrees, dealing %s damage at the source"}.
     * <p>
     * Aimed: yes. Arguments: 3. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BREATH}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "produce a cone of %s"}.
     * <p>
     * Constant EF_ARC coded before 260815, commented in full on 261001.
     */
    EF_ARC(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_BREATH, "produces a cone of %s with width %d degrees, dealing %s damage at the source", "produce a cone of %s"),
    /**
     * Effect {@code SHORT_BEAM} from {@code list-effects.h}. Description template: {@code "produces a beam
     * of %s with length %d, dealing %s damage"}.
     * <p>
     * Aimed: yes. Arguments: 3. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_SHORT}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "produce a beam of %s"}.
     * <p>
     * Constant EF_SHORT_BEAM coded before 260815, commented in full on 261001.
     */
    EF_SHORT_BEAM(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_SHORT, "produces a beam of %s with length %d, dealing %s damage", "produce a beam of %s"),
    /**
     * Effect {@code LASH} from {@code list-effects.h}. Description template: {@code "fires a beam of %s
     * length %d, dealing damage determined by blows"}.
     * <p>
     * Aimed: yes. Arguments: 2. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_LASH}.
     * Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "lash with %s"}.
     * <p>
     * Constant EF_LASH coded before 260815, commented in full on 261001.
     */
    EF_LASH(EffectSubTypeEnum.EST_PROJ, true, "", 2, EffectInfoEnum.EFINFO_LASH, "fires a beam of %s length %d, dealing damage determined by blows", "lash with %s"),
    /**
     * Effect {@code SWARM} from {@code list-effects.h}. Description template: {@code "fires a series of %s
     * balls of radius %d, dealing %s damage at the centre of each"}.
     * <p>
     * Aimed: yes. Arguments: 3. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BALL}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "fire
     * a swarm of %s balls"}.
     * <p>
     * Constant EF_SWARM coded before 260815, commented in full on 261001.
     */
    EF_SWARM(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_BALL, "fires a series of %s balls of radius %d, dealing %s damage at the centre of each", "fire a swarm of %s balls"),
    /**
     * Effect {@code STRIKE} from {@code list-effects.h}. Description template: {@code "creates a ball of
     * %s with radius %d, dealing %s damage at the centre"}.
     * <p>
     * Aimed: yes. Arguments: 3. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BALL}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "strike with %s"}.
     * <p>
     * Constant EF_STRIKE coded before 260815, commented in full on 261001.
     */
    EF_STRIKE(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_BALL, "creates a ball of %s with radius %d, dealing %s damage at the centre", "strike with %s"),
    /**
     * Effect {@code STAR} from {@code list-effects.h}. Description template: {@code "fires a line of %s in
     * all directions, each dealing %s damage"}.
     * <p>
     * Aimed: no. Arguments: 2. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BOLTD}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "fire a line of %s in all directions"}.
     * <p>
     * Constant EF_STAR coded before 260815, commented in full on 261001.
     */
    EF_STAR(EffectSubTypeEnum.EST_PROJ, false, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "fires a line of %s in all directions, each dealing %s damage", "fire a line of %s in all directions"),
    /**
     * Effect {@code STAR_BALL} from {@code list-effects.h}. Description template: {@code "fires balls of
     * %s with radius %d in all directions, dealing %s damage at the centre of each"}.
     * <p>
     * Aimed: no. Arguments: 3. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BALL}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "fire
     * balls of %s in all directions"}.
     * <p>
     * Constant EF_STAR_BALL coded before 260815, commented in full on 261001.
     */
    EF_STAR_BALL(EffectSubTypeEnum.EST_PROJ, false, "dam", 3, EffectInfoEnum.EFINFO_BALL, "fires balls of %s with radius %d in all directions, dealing %s damage at the centre of each", "fire balls of %s in all directions"),
    /**
     * Effect {@code BOLT} from {@code list-effects.h}. Description template: {@code "casts a bolt of %s
     * dealing %s damage"}.
     * <p>
     * Aimed: yes. Arguments: 2. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BOLTD}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "cast a bolt of %s"}.
     * <p>
     * Constant EF_BOLT coded before 260815, commented in full on 261001.
     */
    EF_BOLT(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a bolt of %s dealing %s damage", "cast a bolt of %s"),
    /**
     * Effect {@code BEAM} from {@code list-effects.h}. Description template: {@code "casts a beam of %s
     * dealing %s damage"}.
     * <p>
     * Aimed: yes. Arguments: 2. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BOLTD}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "cast a beam of %s"}.
     * <p>
     * Constant EF_BEAM coded before 260815, commented in full on 261001.
     */
    EF_BEAM(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a beam of %s dealing %s damage", "cast a beam of %s"),
    /**
     * Effect {@code BOLT_OR_BEAM} from {@code list-effects.h}. Description template: {@code "casts a bolt
     * or beam of %s dealing %s damage"}.
     * <p>
     * Aimed: yes. Arguments: 2. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BOLTD}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "cast a bolt or beam of %s"}.
     * <p>
     * Constant EF_BOLT_OR_BEAM coded before 260815, commented in full on 261001.
     */
    EF_BOLT_OR_BEAM(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a bolt or beam of %s dealing %s damage", "cast a bolt or beam of %s"),
    /**
     * Effect {@code LINE} from {@code list-effects.h}. Description template: {@code "creates a line of %s
     * dealing %s damage"}.
     * <p>
     * Aimed: yes. Arguments: 2. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BOLTD}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "create a line of %s"}.
     * <p>
     * Constant EF_LINE coded before 260815, commented in full on 261001.
     */
    EF_LINE(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "creates a line of %s dealing %s damage", "create a line of %s"),
    /**
     * Effect {@code ALTER} from {@code list-effects.h}. Description template: {@code "creates a line which
     * %s"}.
     * <p>
     * Aimed: yes. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_BOLT}.
     * Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "create a line which %s"}.
     * <p>
     * Constant EF_ALTER coded before 260815, commented in full on 261001.
     */
    EF_ALTER(EffectSubTypeEnum.EST_PROJ, true, "", 1, EffectInfoEnum.EFINFO_BOLT, "creates a line which %s", "create a line which %s"),
    /**
     * Effect {@code BOLT_STATUS} from {@code list-effects.h}. Description template: {@code "casts a bolt
     * which %s"}.
     * <p>
     * Aimed: yes. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_BOLT}.
     * Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "cast a bolt which %s"}.
     * <p>
     * Constant EF_BOLT_STATUS coded before 260815, commented in full on 261001.
     */
    EF_BOLT_STATUS(EffectSubTypeEnum.EST_PROJ, true, "", 1, EffectInfoEnum.EFINFO_BOLT, "casts a bolt which %s", "cast a bolt which %s"),
    /**
     * Effect {@code BOLT_STATUS_DAM} from {@code list-effects.h}. Description template: {@code "casts a
     * bolt which %s, dealing %s damage"}.
     * <p>
     * Aimed: yes. Arguments: 2. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_BOLTD}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "cast a bolt which %s"}.
     * <p>
     * Constant EF_BOLT_STATUS_DAM coded before 260815, commented in full on 261001.
     */
    EF_BOLT_STATUS_DAM(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a bolt which %s, dealing %s damage", "cast a bolt which %s"),
    /**
     * Effect {@code BOLT_AWARE} from {@code list-effects.h}. Description template: {@code "creates a bolt
     * which %s"}.
     * <p>
     * Aimed: yes. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_BOLT}.
     * Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "create a bolt which %s"}.
     * <p>
     * Constant EF_BOLT_AWARE coded before 260815, commented in full on 261001.
     */
    EF_BOLT_AWARE(EffectSubTypeEnum.EST_PROJ, true, "", 1, EffectInfoEnum.EFINFO_BOLT, "creates a bolt which %s", "create a bolt which %s"),
    /**
     * Effect {@code TOUCH} from {@code list-effects.h}. Description template: {@code "%s on all adjacent
     * squares"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_TOUCH}.
     * Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "%s all adjacent"}.
     * <p>
     * Constant EF_TOUCH coded before 260815, commented in full on 261001.
     */
    EF_TOUCH(EffectSubTypeEnum.EST_PROJ, false, "", 1, EffectInfoEnum.EFINFO_TOUCH, "%s on all adjacent squares", "%s all adjacent"),
    /**
     * Effect {@code TOUCH_AWARE} from {@code list-effects.h}. Description template: {@code "%s on all
     * adjacent squares"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_TOUCH}.
     * Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code "%s all adjacent"}.
     * <p>
     * Constant EF_TOUCH_AWARE coded before 260815, commented in full on 261001.
     */
    EF_TOUCH_AWARE(EffectSubTypeEnum.EST_PROJ, false, "", 1, EffectInfoEnum.EFINFO_TOUCH, "%s on all adjacent squares", "%s all adjacent"),
    /**
     * Effect {@code CURSE_ARMOR} from {@code list-effects.h}. Description template: {@code "curses your
     * worn armour"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "curse armour"}.
     * <p>
     * Constant EF_CURSE_ARMOR coded before 260815, commented in full on 261001.
     */
    EF_CURSE_ARMOR(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "curses your worn armour", "curse armour"),
    /**
     * Effect {@code CURSE_WEAPON} from {@code list-effects.h}. Description template: {@code "curses your
     * wielded melee weapon"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "curse weapon"}.
     * <p>
     * Constant EF_CURSE_WEAPON coded before 260815, commented in full on 261001.
     */
    EF_CURSE_WEAPON(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "curses your wielded melee weapon", "curse weapon"),
    /**
     * Effect {@code BRAND_WEAPON} from {@code list-effects.h}. Description template: {@code "brands your
     * wielded melee weapon"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "brand weapon"}.
     * <p>
     * Constant EF_BRAND_WEAPON coded before 260815, commented in full on 261001.
     */
    EF_BRAND_WEAPON(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "brands your wielded melee weapon", "brand weapon"),
    /**
     * Effect {@code BRAND_AMMO} from {@code list-effects.h}. Description template: {@code "brands a stack
     * of ammunition"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "brand ammunition"}.
     * <p>
     * Constant EF_BRAND_AMMO coded before 260815, commented in full on 261001.
     */
    EF_BRAND_AMMO(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "brands a stack of ammunition", "brand ammunition"),
    /**
     * Effect {@code BRAND_BOLTS} from {@code list-effects.h}. Description template: {@code "brands bolts
     * with fire, in an unbalanced fashion"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "brand bolts"}.
     * <p>
     * Constant EF_BRAND_BOLTS coded before 260815, commented in full on 261001.
     */
    EF_BRAND_BOLTS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "brands bolts with fire, in an unbalanced fashion", "brand bolts"),
    /**
     * Effect {@code CREATE_ARROWS} from {@code list-effects.h}. Description template: {@code "uses a staff
     * to create a stack of arrows"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "use staff for arrows"}.
     * <p>
     * Constant EF_CREATE_ARROWS coded before 260815, commented in full on 261001.
     */
    EF_CREATE_ARROWS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "uses a staff to create a stack of arrows", "use staff for arrows"),
    /**
     * Effect {@code TAP_DEVICE} from {@code list-effects.h}. Description template: {@code "drains magical
     * energy from a staff or wand"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "tap device"}.
     * <p>
     * Constant EF_TAP_DEVICE coded before 260815, commented in full on 261001.
     */
    EF_TAP_DEVICE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "drains magical energy from a staff or wand", "tap device"),
    /**
     * Effect {@code TAP_UNLIFE} from {@code list-effects.h}. Description template: {@code "drains %s mana
     * from the closest undead monster, damaging it"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_DICE}. Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "tap
     * unlife"}.
     * <p>
     * Constant EF_TAP_UNLIFE coded before 260815, commented in full on 261001.
     */
    EF_TAP_UNLIFE(EffectSubTypeEnum.EST_NONE, false, "dam", 1, EffectInfoEnum.EFINFO_DICE, "drains %s mana from the closest undead monster, damaging it", "tap unlife"),
    /**
     * Effect {@code SHAPECHANGE} from {@code list-effects.h}. Description template: {@code "changes the
     * player's shape"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_SHAPECHANGE}. Menu format: {@code "change shape"}.
     * <p>
     * Constant EF_SHAPECHANGE coded before 260815, commented in full on 261001.
     */
    EF_SHAPECHANGE(EffectSubTypeEnum.EST_SHAPECHANGE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "changes the player's shape", "change shape"),
    /**
     * Effect {@code CURSE} from {@code list-effects.h}. Description template: {@code "damages a monster
     * directly"}.
     * <p>
     * Aimed: yes. Arguments: 0. Info label: {@code "dam"}. Info category: {@link
     * EffectInfoEnum#EFINFO_NONE}. Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code
     * "curse"}.
     * <p>
     * Constant EF_CURSE coded before 260815, commented in full on 261001.
     */
    EF_CURSE(EffectSubTypeEnum.EST_NONE, true, "dam", 0, EffectInfoEnum.EFINFO_NONE, "damages a monster directly", "curse"),
    /**
     * Effect {@code COMMAND} from {@code list-effects.h}. Description template: {@code "takes control of a
     * monster"}.
     * <p>
     * Aimed: yes. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "command"}.
     * <p>
     * Constant EF_COMMAND coded before 260815, commented in full on 261001.
     */
    EF_COMMAND(EffectSubTypeEnum.EST_NONE, true, "", 0, EffectInfoEnum.EFINFO_NONE, "takes control of a monster", "command"),
    /**
     * Effect {@code JUMP_AND_BITE} from {@code list-effects.h}. Description template: {@code "jumps the
     * player to the closest living monster and bites it"}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "jump and bite"}.
     * <p>
     * Constant EF_JUMP_AND_BITE coded before 260815, commented in full on 261001.
     */
    EF_JUMP_AND_BITE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "jumps the player to the closest living monster and bites it", "jump and bite"),
    /**
     * Effect {@code MOVE_ATTACK} from {@code list-effects.h}. Description template: {@code "moves the
     * player up to 4 spaces and executes up to %d melee blows"}.
     * <p>
     * Aimed: yes. Arguments: 1. Info label: {@code "blows"}. Info category: {@link
     * EffectInfoEnum#EFINFO_DICE}. Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "move
     * and attack"}.
     * <p>
     * Constant EF_MOVE_ATTACK coded before 260815, commented in full on 261001.
     */
    EF_MOVE_ATTACK(EffectSubTypeEnum.EST_NONE, true, "blows", 1, EffectInfoEnum.EFINFO_DICE, "moves the player up to 4 spaces and executes up to %d melee blows", "move and attack"),
    /**
     * Effect {@code SINGLE_COMBAT} from {@code list-effects.h}. Description template: {@code "engages a
     * monster in single combat"}.
     * <p>
     * Aimed: yes. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "engage in single combat"}.
     * <p>
     * Constant EF_SINGLE_COMBAT coded before 260815, commented in full on 261001.
     */
    EF_SINGLE_COMBAT(EffectSubTypeEnum.EST_NONE, true, "", 0, EffectInfoEnum.EFINFO_NONE, "engages a monster in single combat", "engage in single combat"),
    /**
     * Effect {@code MELEE_BLOWS} from {@code list-effects.h}. Description template: {@code "strikes %d
     * blows against an adjacent monster"}.
     * <p>
     * Aimed: yes. Arguments: 1. Info label: {@code "blows"}. Info category: {@link
     * EffectInfoEnum#EFINFO_DICE}. Sub-type: {@link EffectSubTypeEnum#EST_PROJ}. Menu format: {@code
     * "pummel"}.
     * <p>
     * Constant EF_MELEE_BLOWS coded before 260815, commented in full on 261001.
     */
    EF_MELEE_BLOWS(EffectSubTypeEnum.EST_PROJ, true, "blows", 1, EffectInfoEnum.EFINFO_DICE, "strikes %d blows against an adjacent monster", "pummel"),
    /**
     * Effect {@code SWEEP} from {@code list-effects.h}. Description template: {@code "strikes %d blows
     * against all adjacent monsters"}.
     * <p>
     * Aimed: no. Arguments: 1. Info label: {@code "blows"}. Info category: {@link
     * EffectInfoEnum#EFINFO_DICE}. Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code
     * "sweep"}.
     * <p>
     * Constant EF_SWEEP coded before 260815, commented in full on 261001.
     */
    EF_SWEEP(EffectSubTypeEnum.EST_NONE, false, "blows", 1, EffectInfoEnum.EFINFO_DICE, "strikes %d blows against all adjacent monsters", "sweep"),
    /**
     * Effect {@code BIZARRE} from {@code list-effects.h}. Description template: {@code "does bizarre
     * things"}.
     * <p>
     * Aimed: yes. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "do bizarre things"}.
     * <p>
     * Constant EF_BIZARRE coded before 260815, commented in full on 261001.
     */
    EF_BIZARRE(EffectSubTypeEnum.EST_NONE, true, "", 0, EffectInfoEnum.EFINFO_NONE, "does bizarre things", "do bizarre things"),
    /**
     * Effect {@code WONDER} from {@code list-effects.h}. Description template: {@code "creates random and
     * unpredictable effects"}.
     * <p>
     * Aimed: yes. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: {@code "create random effects"}.
     * <p>
     * Constant EF_WONDER coded before 260815, commented in full on 261001.
     */
    EF_WONDER(EffectSubTypeEnum.EST_NONE, true, "", 0, EffectInfoEnum.EFINFO_NONE, "creates random and unpredictable effects", "create random effects"),
    /**
     * Effect {@code SELECT} from {@code list-effects.h}. Description template: {@code "selects one of "}.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_SELECT coded before 260815, commented in full on 261001.
     */
    EF_SELECT(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "selects one of ", ""),
    /**
     * Effect {@code SET_VALUE} from {@code list-effects.h}. Description template: none.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_SET_VALUE coded before 260815, commented in full on 261001.
     */
    EF_SET_VALUE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    /**
     * Effect {@code CLEAR_VALUE} from {@code list-effects.h}. Description template: none.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_CLEAR_VALUE coded before 260815, commented in full on 261001.
     */
    EF_CLEAR_VALUE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    /**
     * Effect {@code SCRAMBLE_STATS} from {@code list-effects.h}. Description template: none.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_SCRAMBLE_STATS coded before 260815, commented in full on 261001.
     */
    EF_SCRAMBLE_STATS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    /**
     * Effect {@code UNSCRAMBLE_STATS} from {@code list-effects.h}. Description template: none.
     * <p>
     * Aimed: no. Arguments: 0. Info label: none. Info category: {@link EffectInfoEnum#EFINFO_NONE}.
     * Sub-type: {@link EffectSubTypeEnum#EST_NONE}. Menu format: none.
     * <p>
     * Constant EF_UNSCRAMBLE_STATS coded before 260815, commented in full on 261001.
     */
    EF_UNSCRAMBLE_STATS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    /**
     * Count sentinel, one past the last real effect. Like the C {@code EF_MAX} in {@code effects.h} it has
     * no row in {@code list-effects.h} and is never a valid effect.
     * <p>
     * Constant EF_MAX coded before 260815, commented in full on 261001.
     */
    EF_MAX(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", "");

    /**
     * The effect's sub-type, selecting the family of behaviour it belongs to.
     * <p>
     * Field subType coded before 260815, commented in full on 261001.
     */
    private final EffectSubTypeEnum subType;
    /**
     * Whether the effect must be aimed at a target (the {@code aim} column of {@code list-effects.h}).
     * <p>
     * Field requiresAiming coded before 260815, commented in full on 261001.
     */
    private final boolean requiresAiming;
    /**
     * Short label naming the effect's primary numeric parameter (e.g. "dam", "dur"). The empty string
     * stands for the {@code NULL} the C table uses when there is no label.
     * <p>
     * Field infoLabel coded before 260815, commented in full on 261001.
     */
    private final String infoLabel;
    /**
     * How many arguments the effect's description takes (the {@code args} column of {@code list-effects.h}).
     * <p>
     * Field numberOfArguments coded before 260815, commented in full on 261001.
     */
    private final int numberOfArguments; // May need to change this
    /**
     * The formatting category used when building the effect's description (the {@code info flags}
     * column of {@code list-effects.h}).
     * <p>
     * Field effectInfoEnum coded before 260815, commented in full on 261001.
     */
    private final EffectInfoEnum effectInfoEnum;
    /**
     * Description-string template (with {@code %s}/{@code %d} placeholders).
     * <p>
     * Field description coded before 260815, commented in full on 261001.
     */
    private final String description;
    /**
     * Menu-format template used when the effect is shown in a selection menu. The empty string means
     * the effect is not meant to be used from a menu, as in {@code list-effects.h}.
     * <p>
     * Field menuFormat coded before 260815, commented in full on 261001.
     */
    private final String menuFormat;

    /**
     * Build an effect descriptor from its data fields.
     * <p>
     * Constructor EffectEnum coded before 260815, commented in full on 261001.
     *
     * @param subType        the effect's sub-type
     * @param aim            whether it must be aimed
     * @param infoLabel      label for its primary parameter
     * @param arguments      number of arguments consumed
     * @param effectInfoEnum description formatting category
     * @param text           description template
     * @param menuText       menu-format template
     */
    @Contract(mutates = "this")
    EffectEnum(EffectSubTypeEnum subType,
               boolean aim,
               String infoLabel,
               int arguments,
               EffectInfoEnum effectInfoEnum,
               String text,
               String menuText) {
        this.subType = subType;
        requiresAiming = aim;
        this.infoLabel = infoLabel;
        numberOfArguments = arguments;
        this.effectInfoEnum = effectInfoEnum;
        description = text;
        menuFormat = menuText;
    }

    /**
     * Read accessor for the field of the same name.
     * <p>
     * Function getSubType coded before 260815, commented in full on 261001.
     *
     * @return the effect's sub-type
     */
    @Contract(pure = true)
    public EffectSubTypeEnum getSubType() {
        return subType;
    }

    /**
     * Read accessor for the field of the same name.
     * <p>
     * Function getAim coded before 260815, commented in full on 261001.
     *
     * @return whether the effect must be aimed
     */
    @Contract(pure = true)
    public boolean getAim() {
        return requiresAiming;
    }

    /**
     * Read accessor for the field of the same name.
     * <p>
     * Function getInfoLabel coded before 260815, commented in full on 261001.
     *
     * @return the label for the effect's primary parameter
     */
    @Contract(pure = true)
    public String getInfoLabel() {
        return infoLabel;
    }

    /**
     * Read accessor for the field of the same name.
     * <p>
     * Function getNumberOfArguments coded before 260815, commented in full on 261001.
     *
     * @return the number of arguments the effect consumes
     */
    @Contract(pure = true)
    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    /**
     * Read accessor for the field of the same name.
     * <p>
     * Function getEffectInfo coded before 260815, commented in full on 261001.
     *
     * @return the description formatting category
     */
    @Contract(pure = true)
    public EffectInfoEnum getEffectInfo() {
        return effectInfoEnum;
    }

    /**
     * Read accessor for the field of the same name.
     * <p>
     * Function getDescription coded before 260815, commented in full on 261001.
     *
     * @return the description-string template
     */
    @Contract(pure = true)
    public String getDescription() {
        return description;
    }

    /**
     * Read accessor for the field of the same name.
     * <p>
     * Function getMenuFormat coded before 260815, commented in full on 261001.
     *
     * @return the menu-format template
     */
    @Contract(pure = true)
    public String getMenuFormat() {
        return menuFormat;
    }
}