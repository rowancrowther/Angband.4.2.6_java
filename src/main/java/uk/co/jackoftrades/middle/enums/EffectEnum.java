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

package uk.co.jackoftrades.middle.enums;

import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;

/**
 * The master table of every effect the game can apply (damage, heal, cure,
 * teleport, summon, detect, the various bolt/ball/beam projections, …). Each
 * constant bundles the effect's sub-type, whether it must be aimed, how many
 * arguments it takes, the {@link EffectInfoEnum} formatting category, and the
 * description/menu-format templates used to describe it to the player. This is
 * the Java port of the C original's {@code effect_list}/{@code list-effects.h};
 * the constants are self-describing via their description strings and are
 * documented collectively here. {@code EF_MAX} is the count sentinel.
 *
 * @author ClaudeCode
 */
public enum EffectEnum {
    EF_NONE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_RANDOM(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "randomly ", ""),
    EF_DAMAGE(EffectSubTypeEnum.EST_NONE, false, "hurt", 1, EffectInfoEnum.EFINFO_DICE, "does %s damage to the player", ""),
    EF_HEAL_HP(EffectSubTypeEnum.EST_NONE, false, "heal", 2, EffectInfoEnum.EFINFO_HEAL, "heals %s hitpoints%s", "heal self"),
    EF_MON_HEAL_HP(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "heals monster hitpoints", ""),
    EF_MON_HEAL_KIN(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "heals fellow monster hitpoints", ""),
    EF_NOURISH(EffectSubTypeEnum.EST_NOURISH, false, "", 3, EffectInfoEnum.EFINFO_FOOD, "%s for %s turns _%s percent),", "%s %s"),
    EF_CRUNCH(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "crunches", ""),
    EF_CURE(EffectSubTypeEnum.EST_TMD, false, "", 1, EffectInfoEnum.EFINFO_CURE, "cures %s", "cure %s"),
    EF_TIMED_SET(EffectSubTypeEnum.EST_TMD, false, "", 2, EffectInfoEnum.EFINFO_TIMED, "administers %s for %s turns", "administer %s"),
    EF_TIMED_INC(EffectSubTypeEnum.EST_TMD, false, "dur", 2, EffectInfoEnum.EFINFO_TIMED, "extends %s for %s turns", "extend %s"),
    EF_TIMED_INC_NO_RES(EffectSubTypeEnum.EST_TMD, false, "dur", 2, EffectInfoEnum.EFINFO_TIMED, "extends %s for %s turns _unresistable),", "extend %s"),
    EF_MON_TIMED_INC(EffectSubTypeEnum.EST_MON_TMD, false, "", 2, EffectInfoEnum.EFINFO_TIMED, "increases monster %s by %s turns", ""),
    EF_TIMED_DEC(EffectSubTypeEnum.EST_TMD, false, "", 2, EffectInfoEnum.EFINFO_TIMED, "reduces length of %s by %s turns", "reduce %s"),
    EF_GLYPH(EffectSubTypeEnum.EST_GLYPH, false, "", 1, EffectInfoEnum.EFINFO_NONE, "inscribes a glyph beneath you", "inscribe a glyph"),
    EF_WEB(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates a web", "create a web"),
    EF_RESTORE_STAT(EffectSubTypeEnum.EST_STAT, false, "", 1, EffectInfoEnum.EFINFO_STAT, "restores your %s", "restore %s"),
    EF_DRAIN_STAT(EffectSubTypeEnum.EST_STAT, false, "", 1, EffectInfoEnum.EFINFO_STAT, "reduces your %s", ""),
    EF_LOSE_RANDOM_STAT(EffectSubTypeEnum.EST_STAT, false, "", 1, EffectInfoEnum.EFINFO_STAT, "reduces a stat other than %s", ""),
    EF_GAIN_STAT(EffectSubTypeEnum.EST_STAT, false, "", 1, EffectInfoEnum.EFINFO_STAT, "increases your %s", ""),
    EF_RESTORE_EXP(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "restores your experience", "restore experience"),
    EF_GAIN_EXP(EffectSubTypeEnum.EST_NONE, false, "", 1, EffectInfoEnum.EFINFO_CONST, "grants %d experience points", ""),
    EF_DRAIN_LIGHT(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "drains your light source", ""),
    EF_DRAIN_MANA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "drains some mana", ""),
    EF_RESTORE_MANA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "restores some mana", "restore some mana"),
    EF_REMOVE_CURSE(EffectSubTypeEnum.EST_NONE, false, "", 1, EffectInfoEnum.EFINFO_DICE, "attempts power %s removal of a single curse on an object", "remove curse"),
    EF_RECALL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "returns you from the dungeon or takes you to the dungeon after a short delay", "recall"),
    EF_DEEP_DESCENT(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "teleports you up to five dungeon levels lower than the lowest point you have reached so far", "descend to the depths"),
    EF_ALTER_REALITY(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates a new dungeon level", "alter reality"),
    EF_MAP_AREA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "maps the area around you", "map surroundings"),
    EF_READ_MINDS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "maps the area around recently detected monsters", "read minds"),
    EF_DETECT_TRAPS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects traps nearby", "detect traps"),
    EF_DETECT_DOORS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects doors nearby", "detect doors"),
    EF_DETECT_STAIRS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects stairs nearby", "detect stairs"),
    EF_DETECT_ORE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects veins nearby", "detect veins"),
    EF_SENSE_GOLD(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "senses gold nearby", "sense gold"),
    EF_DETECT_GOLD(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects gold nearby", "detect gold"),
    EF_SENSE_OBJECTS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "senses objects nearby", "sense objects"),
    EF_DETECT_OBJECTS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects objects nearby", "detect objects"),
    EF_DETECT_LIVING_MONSTERS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects living creatures nearby", "detect living"),
    EF_DETECT_VISIBLE_MONSTERS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects visible creatures nearby", "detect visible"),
    EF_DETECT_INVISIBLE_MONSTERS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects invisible creatures nearby", "detect invisible"),
    EF_DETECT_FEARFUL_MONSTERS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects creatures nearby which are susceptible to fear", "detect fearful"),
    EF_IDENTIFY(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "identifies a single unknown rune on a selected item", "identify"),
    EF_DETECT_EVIL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects evil creatures nearby", "detect evil"),
    EF_DETECT_SOUL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects creatures with a spirit nearby", "detect souls"),
    EF_CREATE_STAIRS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates a staircase beneath your feet", "create stairs"),
    EF_DISENCHANT(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "disenchants one of your wielded items", "disenchant item"),
    EF_ENCHANT(EffectSubTypeEnum.EST_ENCHANT, false, "", 0, EffectInfoEnum.EFINFO_NONE, "attempts to magically enhance an item", "enchant item"),
    EF_RECHARGE(EffectSubTypeEnum.EST_NONE, false, "power", 0, EffectInfoEnum.EFINFO_NONE, "tries to recharge a wand or staff, destroying the wand or staff on failure", "recharge"),
    EF_PROJECT_LOS(EffectSubTypeEnum.EST_PROJ, false, "power", 1, EffectInfoEnum.EFINFO_SEEN, "%s which are in line of sight", "%s in line of sight"),
    EF_PROJECT_LOS_AWARE(EffectSubTypeEnum.EST_PROJ, false, "power", 1, EffectInfoEnum.EFINFO_SEEN, "%s which are in line of sight", "%s in line of sight"),
    EF_ACQUIRE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates good items nearby", "create good items"),
    EF_WAKE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "awakens all nearby sleeping monsters", "awaken all"),
    EF_SUMMON(EffectSubTypeEnum.EST_SUMMON, false, "", 1, EffectInfoEnum.EFINFO_SUMM, "summons %s at the current dungeon level", "summon %s"),
    EF_BANISH(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "removes all of a given creature type from the level", "banish"),
    EF_MASS_BANISH(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "removes all nearby creatures", "banish all"),
    EF_PROBE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "gives you information on the health and abilities of monsters you can see", "probe"),
    EF_TELEPORT(EffectSubTypeEnum.EST_TELEPORT, false, "range", 2, EffectInfoEnum.EFINFO_TELE, "teleports %s randomly %s", "teleport %s %s"),
    EF_TELEPORT_TO(EffectSubTypeEnum.EST_TELEPORT_TO, false, "", 0, EffectInfoEnum.EFINFO_NONE, "teleports toward a target", "teleport to target"),
    EF_TELEPORT_LEVEL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "teleports you one level up or down", "teleport level"),
    EF_RUBBLE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "causes rubble to fall around you", ""),
    EF_GRANITE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "causes a granite wall to fall behind you", ""),
    EF_DESTRUCTION(EffectSubTypeEnum.EST_PROJ, false, "", 1, EffectInfoEnum.EFINFO_QUAKE, "destroys an area around you in the shape of a circle radius %d, and blinds you for 1d10+10 turns", "destroy area"),
    EF_EARTHQUAKE(EffectSubTypeEnum.EST_EARTHQUAKE, false, "", 1, EffectInfoEnum.EFINFO_QUAKE, "causes an earthquake around you of radius %d", "cause earthquake"),
    EF_LIGHT_LEVEL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "completely lights up and magically maps the level", "light level"),
    EF_DARKEN_LEVEL(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "completely darkens up and magically maps the level", "darken level"),
    EF_LIGHT_AREA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "lights up the surrounding area", "light area"),
    EF_DARKEN_AREA(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "darkens the surrounding area", "darken area"),
    EF_SPOT(EffectSubTypeEnum.EST_PROJ, false, "dam", 4, EffectInfoEnum.EFINFO_SPOT, "creates a ball of %s with radius %d, centred on and hitting the player, with full intensity to radius %d, dealing %s damage at the centre", "engulf with %s"),
    EF_SPHERE(EffectSubTypeEnum.EST_PROJ, false, "dam", 4, EffectInfoEnum.EFINFO_SPOT, "creates a ball of %s with radius %d, centred on the player, with full intensity to radius %d, dealing %s damage at the centre", "project %s"),
    EF_BALL(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_BALL, "fires a ball of %s with radius %d, dealing %s damage at the centre", "fire a ball of %s"),
    EF_BREATH(EffectSubTypeEnum.EST_PROJ, true, "", 3, EffectInfoEnum.EFINFO_BREATH, "breathes a cone of %s with width %d degrees, dealing %s damage at the source", "breathe a cone of %s"),
    EF_ARC(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_BREATH, "produces a cone of %s with width %d degrees, dealing %s damage at the source", "produce a cone of %s"),
    EF_SHORT_BEAM(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_SHORT, "produces a beam of %s with length %d, dealing %s damage", "produce a beam of %s"),
    EF_LASH(EffectSubTypeEnum.EST_PROJ, true, "", 2, EffectInfoEnum.EFINFO_LASH, "fires a beam of %s length %d, dealing damage determined by blows", "lash with %s"),
    EF_SWARM(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_BALL, "fires a series of %s balls of radius %d, dealing %s damage at the centre of each", "fire a swarm of %s balls"),
    EF_STRIKE(EffectSubTypeEnum.EST_PROJ, true, "dam", 3, EffectInfoEnum.EFINFO_BALL, "creates a ball of %s with radius %d, dealing %s damage at the centre", "strike with %s"),
    EF_STAR(EffectSubTypeEnum.EST_PROJ, false, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "fires a line of %s in all directions, each dealing %s damage", "fire a line of %s in all directions"),
    EF_STAR_BALL(EffectSubTypeEnum.EST_PROJ, false, "dam", 3, EffectInfoEnum.EFINFO_BALL, "fires balls of %s with radius %d in all directions, dealing %s damage at the centre of each", "fire balls of %s in all directions"),
    EF_BOLT(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a bolt of %s dealing %s damage", "cast a bolt of %s"),
    EF_BEAM(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a beam of %s dealing %s damage", "cast a beam of %s"),
    EF_BOLT_OR_BEAM(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a bolt or beam of %s dealing %s damage", "cast a bolt or beam of %s"),
    EF_LINE(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "creates a line of %s dealing %s damage", "create a line of %s"),
    EF_ALTER(EffectSubTypeEnum.EST_PROJ, true, "", 1, EffectInfoEnum.EFINFO_BOLT, "creates a line which %s", "create a line which %s"),
    EF_BOLT_STATUS(EffectSubTypeEnum.EST_PROJ, true, "", 1, EffectInfoEnum.EFINFO_BOLT, "casts a bolt which %s", "cast a bolt which %s"),
    EF_BOLT_STATUS_DAM(EffectSubTypeEnum.EST_PROJ, true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a bolt which %s, dealing %s damage", "cast a bolt which %s"),
    EF_BOLT_AWARE(EffectSubTypeEnum.EST_PROJ, true, "", 1, EffectInfoEnum.EFINFO_BOLT, "creates a bolt which %s", "create a bolt which %s"),
    EF_TOUCH(EffectSubTypeEnum.EST_PROJ, false, "", 1, EffectInfoEnum.EFINFO_TOUCH, "%s on all adjacent squares", "%s all adjacent"),
    EF_TOUCH_AWARE(EffectSubTypeEnum.EST_PROJ, false, "", 1, EffectInfoEnum.EFINFO_TOUCH, "%s on all adjacent squares", "%s all adjacent"),
    EF_CURSE_ARMOR(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "curses your worn armor", "curse armor"),
    EF_CURSE_WEAPON(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "curses your wielded melee weapon", "curse weapon"),
    EF_BRAND_WEAPON(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "brands your wielded melee weapon", "brand weapon"),
    EF_BRAND_AMMO(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "brands a stack of ammunition", "brand ammunition"),
    EF_BRAND_BOLTS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "brands bolts with fire, in an unbalanced fashion", "brand bolts"),
    EF_CREATE_ARROWS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "uses a staff to create a stack of arrows", "use staff for arrows"),
    EF_TAP_DEVICE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "drains magical energy from a staff or wand", "tap device"),
    EF_TAP_UNLIFE(EffectSubTypeEnum.EST_NONE, false, "dam", 1, EffectInfoEnum.EFINFO_DICE, "drains %s mana from the closest undead monster, damaging it", "tap unlife"),
    EF_SHAPECHANGE(EffectSubTypeEnum.EST_SHAPECHANGE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "changes the player's shape", "change shape"),
    EF_CURSE(EffectSubTypeEnum.EST_NONE, true, "dam", 0, EffectInfoEnum.EFINFO_NONE, "damages a monster directly", "curse"),
    EF_COMMAND(EffectSubTypeEnum.EST_NONE, true, "", 0, EffectInfoEnum.EFINFO_NONE, "takes control of a monster", "command"),
    EF_JUMP_AND_BITE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "jumps the player to the closest living monster and bites it", "jump and bite"),
    EF_MOVE_ATTACK(EffectSubTypeEnum.EST_NONE, true, "blows", 1, EffectInfoEnum.EFINFO_DICE, "moves the player up to 4 spaces and executes up to %d melee blows", "move and attack"),
    EF_SINGLE_COMBAT(EffectSubTypeEnum.EST_NONE, true, "", 0, EffectInfoEnum.EFINFO_NONE, "engages a monster in single combat", "engage in single combat"),
    EF_MELEE_BLOWS(EffectSubTypeEnum.EST_PROJ, true, "blows", 1, EffectInfoEnum.EFINFO_DICE, "strikes %d blows against an adjacent monster", "pummel"),
    EF_SWEEP(EffectSubTypeEnum.EST_NONE, false, "blows", 1, EffectInfoEnum.EFINFO_DICE, "strikes %d blows against all adjacent monsters", "sweep"),
    EF_BIZARRE(EffectSubTypeEnum.EST_NONE, true, "", 0, EffectInfoEnum.EFINFO_NONE, "does bizarre things", "do bizarre things"),
    EF_WONDER(EffectSubTypeEnum.EST_NONE, true, "", 0, EffectInfoEnum.EFINFO_NONE, "creates random and unpredictable effects", "create random effects"),
    EF_SELECT(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "selects one of ", ""),
    EF_SET_VALUE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_CLEAR_VALUE(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_SCRAMBLE_STATS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_UNSCRAMBLE_STATS(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_MAX(EffectSubTypeEnum.EST_NONE, false, "", 0, EffectInfoEnum.EFINFO_NONE, "", "");

    /**
     * The effect's sub-type, selecting the family of behaviour it belongs to.
     *
     * @author ClaudeCode
     */
    private final EffectSubTypeEnum subType;
    /**
     * Whether the effect must be aimed at a target.
     *
     * @author ClaudeCode
     */
    private final boolean requiresAiming;
    /**
     * Short label naming the effect's primary numeric parameter (e.g. "dam", "dur").
     *
     * @author ClaudeCode
     */
    private final String infoLabel;
    /**
     * How many arguments the effect consumes.
     *
     * @author ClaudeCode
     */
    private final int numberOfArguments; // May need to change this
    /**
     * The formatting category used when building the effect's description.
     *
     * @author ClaudeCode
     */
    private final EffectInfoEnum effectInfoEnum;
    /**
     * Description-string template (with {@code %s}/{@code %d} placeholders).
     *
     * @author ClaudeCode
     */
    private final String description;
    /**
     * Menu-format template used when the effect is shown in a selection menu.
     *
     * @author ClaudeCode
     */
    private final String menuFormat;

    /**
     * Build an effect descriptor from its data fields.
     *
     * @param subType        the effect's sub-type
     * @param aim            whether it must be aimed
     * @param infoLabel      label for its primary parameter
     * @param arguments      number of arguments consumed
     * @param effectInfoEnum description formatting category
     * @param text           description template
     * @param menuText       menu-format template
     * @author ClaudeCode
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
     * @return the effect's sub-type
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public EffectSubTypeEnum getSubType() {
        return subType;
    }

    /**
     * @return whether the effect must be aimed
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public boolean getAim() {
        return requiresAiming;
    }

    /**
     * @return the label for the effect's primary parameter
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public String getInfoLabel() {
        return infoLabel;
    }

    /**
     * @return the number of arguments the effect consumes
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    /**
     * @return the description formatting category
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public EffectInfoEnum getEffectInfo() {
        return effectInfoEnum;
    }

    /**
     * @return the description-string template
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public String getDescription() {
        return description;
    }

    /**
     * @return the menu-format template
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public String getMenuFormat() {
        return menuFormat;
    }
}