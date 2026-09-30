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

package uk.co.jackoftradesltd.middle.cave;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.Objects;

/**
 * The definition of one terrain feature type (as loaded from {@code terrain.txt})
 * — its code, name, display glyph, the {@link TerrainFeatureFlags} that give it
 * its behaviour, and the various messages shown when the player interacts with
 * it. This is the Java port of the C original's {@code struct feature}
 * ({@code cave.h}).
 *
 * <p>The C original holds these in the global {@code f_info} array and asks
 * questions of them through the {@code feat_is_*()} functions in
 * {@code cave-square.c}; the {@code square_is*()} functions there go through
 * the square's feature to the same flags. Here each {@code feat_is_*()} test
 * is an {@code isXxx()} predicate over {@link #flags}, and the square-level
 * tests live on {@code Square} and {@code Chunk}.
 *
 * <p>Two differences from the C struct are worth knowing. {@code mimic} is a
 * {@link TerrainFlags} code rather than a pointer, so {@code null} (not
 * {@code FEAT_NONE}) means "does not mimic" and {@link #getMimic()} resolves
 * the code through the {@link TerrainRegistry}. And {@code fidx} is not
 * carried: it is the feature's index in {@code f_info}, which is
 * {@code code.ordinal()} because {@link TerrainFlags} follows the order of
 * {@code list-terrain.h} (and of {@code terrain.txt}).
 *
 * <p>Class Feature coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class Feature {
    /**
     * The feature's behaviour flags (passable, wall, door, stair, …). C's
     * {@code flags}, a {@code TF_*} bitset. Package-private so the other
     * classes in the cave package can test a flag directly.
     *
     * <p>Field flags coded before 260930, commented in full on 260930.
     */
    Flag<TerrainFeatureFlags> flags;
    /**
     * The feature's terrain code (its {@link TerrainFlags} identity). This is
     * the Java form of the {@code code:} directive in {@code terrain.txt}, and
     * the equivalent of C's {@code fidx} through {@code code.ordinal()}.
     *
     * <p>Field code coded before 260930, commented in full on 260930.
     */
    private TerrainFlags code;
    /**
     * The feature's name, as shown when it is looked at. C's {@code name}.
     *
     * <p>Field name coded before 260930, commented in full on 260930.
     */
    private String name;
    /**
     * Human-readable description of the feature, taken from the {@code desc:}
     * lines of {@code terrain.txt}. C's {@code desc}.
     *
     * <p>Field description coded before 260930, commented in full on 260930.
     */
    private String description;
    /**
     * Index of this feature in the global feature table. C's {@code fidx}.
     *
     * <p>Unused: the constructor does not take it and nothing assigns it, so it
     * is always 0. Use {@code code.ordinal()} instead, which gives the same
     * value for {@code terrain.txt} because {@link TerrainFlags} follows the
     * order of {@code list-terrain.h}.
     *
     * <p>Field featureIndex coded before 260930, deprecated and commented in full on 260930.
     *
     * @deprecated use {@code code.ordinal()}
     */
    @Deprecated
    private int featureIndex;
    /**
     * The terrain this feature is displayed and described as, or {@code null}
     * if it does not mimic anything. C's {@code mimic}, which is a pointer that
     * is NULL for "no mimic"; a secret door has {@code mimic:GRANITE}. It is
     * deliberately {@code null}, not {@code FEAT_NONE}, when absent, because
     * {@link #isMimicing()} tests for {@code null}.
     *
     * <p>Field mimic coded before 260930, commented in full on 260930.
     */
    private TerrainFlags mimic;
    /**
     * Drawing priority when several features could be shown for a grid. C's
     * {@code priority}.
     *
     * <p>Field priority coded before 260930, commented in full on 260930.
     */
    private int priority;
    /**
     * Shop number when this feature is a shop entrance, or 0 when it is not.
     * C's {@code shopnum}, which {@code finish_parse_feat()} in {@code init.c}
     * assigns from 1 in table order to every feature carrying the SHOP flag;
     * the assembler does the same. It is one more than the store index.
     *
     * <p>Field shopNum coded before 260930, commented in full on 260930.
     */
    private int shopNum;
    /**
     * Difficulty of digging through this feature. C's {@code dig}, from the
     * {@code digging:} directive.
     *
     * <p>Field dig coded before 260930, commented in full on 260930.
     */
    private int dig;
    /**
     * The glyph and colour used to draw this feature. C keeps these as
     * {@code d_attr} and {@code d_char}; the value is {@code null} unless both
     * were supplied in {@code terrain.txt}.
     *
     * <p>Field displayCharacter coded before 260930, commented in full on 260930.
     */
    private AngbandDisplayCharacter displayCharacter;

    /**
     * Message shown when the player walks onto this feature. C's
     * {@code walk_msg}.
     *
     * <p>Field walkMsg coded before 260930, commented in full on 260930.
     */
    private String walkMsg;
    /**
     * Message shown when the player runs onto/through this feature. C's
     * {@code run_msg}.
     *
     * <p>Field runMsg coded before 260930, commented in full on 260930.
     */
    private String runMsg;
    /**
     * Message shown when the feature hurts the player. C's {@code hurt_msg}.
     *
     * <p>Field hurtMsg coded before 260930, commented in full on 260930.
     */
    private String hurtMsg;
    /**
     * Message shown when the player dies to this feature. C's {@code die_msg}.
     *
     * <p>Field dieMsg coded before 260930, commented in full on 260930.
     */
    private String dieMsg;
    /**
     * Message shown when a confused monster moves into this feature. C's
     * {@code confused_msg}.
     *
     * <p>Field confusedMsg coded before 260930, commented in full on 260930.
     */
    private String confusedMsg;
    /**
     * Prefix used when describing this feature in "look" output. C's
     * {@code look_prefix}; {@code finish_parse_feat()} appends a trailing space
     * when the data file's value lacks one.
     *
     * <p>Field lookPrefix coded before 260930, commented in full on 260930.
     */
    private String lookPrefix;
    /**
     * Preposition used when describing being "in" this feature in "look"
     * output. C's {@code look_in_preposition}; {@code finish_parse_feat()}
     * appends a trailing space when the data file's value lacks one.
     *
     * <p>Field lookInPreposition coded before 260930, commented in full on 260930.
     */
    private String lookInPreposition;
    /**
     * Monster race flag granting resistance to this feature's effects. C's
     * {@code resist_flag}, the monster resist flag for entering the feature.
     *
     * <p>Field resistFlag coded before 260930, commented in full on 260930.
     */
    private Flag<MonsterRaceFlag> resistFlag;

    /**
     * Build a feature definition from its parsed data-file fields. The
     * {@code TerrainFeatureAssembler} calls this once per {@code terrain.txt}
     * record.
     *
     * <p>Constructor Feature coded before 260930, commented in full on 260930.
     *
     * @param code              terrain code identity
     * @param name              feature name
     * @param description       human-readable description
     * @param mimic             terrain this feature mimics, or {@code null} for none
     * @param priority          drawing priority
     * @param dig               digging difficulty
     * @param flags             behaviour flags
     * @param displayCharacter  display glyph and colour
     * @param walkMsg           walk-onto message
     * @param runMsg            run-onto message
     * @param hurtMsg           hurt message
     * @param dieMsg            death message
     * @param confusedMsg       confused message
     * @param lookPrefix        look-output prefix
     * @param lookInPreposition look-output "in" preposition
     * @param resistFlag        race flag granting resistance
     * @param shopNum           shop number, or 0 when this is not a shop entrance
     */
    public Feature(TerrainFlags code,
                   String name,
                   String description,
                   TerrainFlags mimic,
                   int priority,
                   int dig,
                   Flag<TerrainFeatureFlags> flags,
                   AngbandDisplayCharacter displayCharacter,
                   String walkMsg,
                   String runMsg,
                   String hurtMsg,
                   String dieMsg,
                   String confusedMsg,
                   String lookPrefix,
                   String lookInPreposition,
                   Flag<MonsterRaceFlag> resistFlag,
                   int shopNum) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.mimic = mimic;
        this.priority = priority;
        this.dig = dig;
        this.flags = flags;
        this.displayCharacter = displayCharacter;
        this.walkMsg = walkMsg;
        this.runMsg = runMsg;
        this.hurtMsg = hurtMsg;
        this.dieMsg = dieMsg;
        this.confusedMsg = confusedMsg;
        this.lookPrefix = lookPrefix;
        this.lookInPreposition = lookInPreposition;
        this.resistFlag = resistFlag;
        this.shopNum = shopNum;
    }

    /**
     * Get this feature's terrain code, the value that identifies it in the
     * feature table.
     *
     * <p>Function getTerrainFlag coded before 260930, commented in full on 260930.
     *
     * @return this feature's terrain code
     */
    public TerrainFlags getTerrainFlag() {
        return code;
    }

    /*
     *   Feature predicates
     */

    /**
     * Tests whether this feature is displayed as some other terrain. C has no
     * single function for this: it tests the {@code mimic} pointer for non-NULL
     * inline, in {@code cave-map.c} ({@code map_info()}) and in the
     * {@code square_apparent_*()} functions of {@code cave-square.c}.
     *
     * <p>Function isMimicing coded before 260930, commented in full on 260930.
     *
     * @return true if this feature is not what it seems
     */
    public boolean isMimicing() {
        return mimic != null;
    }

    /**
     * Tests for magma. Port of {@code feat_is_magma()} in {@code cave-square.c},
     * which tests the MAGMA flag.
     *
     * <p>Function isMagma coded before 260930, commented in full on 260930.
     *
     * @return true if feature is magma
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isMagma() {
        return flags.has(TerrainFeatureFlags.TF_MAGMA);
    }

    /**
     * Tests for quartz. Port of {@code feat_is_quartz()} in
     * {@code cave-square.c}, which tests the QUARTZ flag.
     *
     * <p>Function isQuartz coded before 260930, commented in full on 260930.
     *
     * @return true if feature is quartz
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isQuartz() {
        return flags.has(TerrainFeatureFlags.TF_QUARTZ);
    }

    /**
     * Tests for granite. Port of {@code feat_is_granite()} in
     * {@code cave-square.c}, which tests the GRANITE flag. Secret doors carry
     * GRANITE too, so this is true for them; {@link #isRock()} excludes them.
     *
     * <p>Function isGranite coded before 260930, commented in full on 260930.
     *
     * @return true if feature is granite
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isGranite() {
        return flags.has(TerrainFeatureFlags.TF_GRANITE);
    }

    /**
     * Tests for a mineral wall with treasure. Port of {@code feat_is_treasure()}
     * in {@code cave-square.c}, which tests the GOLD flag.
     *
     * <p>Function isTreasure coded before 260930, commented in full on 260930.
     *
     * @return true if feature is mineral (magma/quartz) with gold
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isTreasure() {
        return flags.has(TerrainFeatureFlags.TF_GOLD);
    }

    /**
     * Tests for a solid wall (not rubble). Port of {@code feat_is_wall()} in
     * {@code cave-square.c}, which tests the WALL flag.
     *
     * <p>Function isWall coded before 260930, commented in full on 260930.
     *
     * @return true if the feature is a solid wall
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isWall() {
        return flags.has(TerrainFeatureFlags.TF_WALL);
    }

    /**
     * Tests for a floor. Port of {@code feat_is_floor()} in
     * {@code cave-square.c}, which tests the FLOOR flag.
     *
     * <p>Function isFloor coded before 260930, commented in full on 260930.
     *
     * @return true if the feature is floor
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isFloor() {
        return flags.has(TerrainFeatureFlags.TF_FLOOR);
    }

    /**
     * Tests whether the feature can hold a trap. Port of
     * {@code feat_is_trap_holding()} in {@code cave-square.c}, which tests the
     * TRAP flag.
     *
     * <p>Function isTrapHolding coded before 260930, commented in full on 260930.
     *
     * @return true if the feature can hold a trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isTrapHolding() {
        return flags.has(TerrainFeatureFlags.TF_TRAP);
    }

    /**
     * Tests whether the feature can hold an object. Port of
     * {@code feat_is_object_holding()} in {@code cave-square.c}, which tests the
     * OBJECT flag.
     *
     * <p>Function isObjectHolding coded before 260930, commented in full on 260930.
     *
     * @return true if the feature can hold an object
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isObjectHolding() {
        return flags.has(TerrainFeatureFlags.TF_OBJECT);
    }

    /**
     * Tests whether monsters can walk through this feature. Port of
     * {@code feat_is_monster_walkable()} in {@code cave-square.c}. It tests the
     * PASSABLE flag, the same as {@link #isPassable()}; C keeps two functions
     * so the intent at each call site is clear.
     *
     * <p>Function isMonsterWalkable coded before 260930, commented in full on 260930.
     *
     * @return true if this feature is passable
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isMonsterWalkable() {
        return flags.has(TerrainFeatureFlags.TF_PASSABLE);
    }

    /**
     * Tests whether the feature is a shop entrance. Port of
     * {@code feat_is_shop()} in {@code cave-square.c}, which tests the SHOP flag.
     *
     * <p>Function isShop coded before 260930, commented in full on 260930.
     *
     * @return true if the feature is a shop entrance
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isShop() {
        return flags.has(TerrainFeatureFlags.TF_SHOP);
    }

    /**
     * Tests whether the feature allows line of sight. Port of
     * {@code feat_is_los()} in {@code cave-square.c}, which tests the LOS flag.
     *
     * <p>Function isLos coded before 260930, commented in full on 260930.
     *
     * @return true if the feature allows line of sight
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isLos() {
        return flags.has(TerrainFeatureFlags.TF_LOS);
    }

    /**
     * Tests whether the player can pass through this feature. Port of
     * {@code feat_is_passable()} in {@code cave-square.c}, which tests the
     * PASSABLE flag.
     *
     * <p>Function isPassable coded before 260930, commented in full on 260930.
     *
     * @return true if the player can pass through this feature
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isPassable() {
        return flags.has(TerrainFeatureFlags.TF_PASSABLE);
    }

    /**
     * Tests whether projectables can pass through this feature. Port of
     * {@code feat_is_projectable()} in {@code cave-square.c}, which tests the
     * PROJECT flag.
     *
     * <p>Function isProjectable coded before 260930, commented in full on 260930.
     *
     * @return true if the feature allows projectables through it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isProjectable() {
        return flags.has(TerrainFeatureFlags.TF_PROJECT);
    }

    /**
     * Tests whether a feature can be lit by light sources. Port of
     * {@code feat_is_torch()} in {@code cave-square.c}, which tests the TORCH
     * flag.
     *
     * <p>Function isTorch coded before 260930, commented in full on 260930.
     *
     * @return true if this feature can be lit by light sources
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isTorch() {
        return flags.has(TerrainFeatureFlags.TF_TORCH);
    }

    /**
     * Tests for internally lit features. Port of {@code feat_is_bright()} in
     * {@code cave-square.c}, which tests the BRIGHT flag.
     *
     * <p>Function isBright coded before 260930, commented in full on 260930.
     *
     * @return true if this feature is bright
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isBright() {
        return flags.has(TerrainFeatureFlags.TF_BRIGHT);
    }

    /**
     * Tests for fire-based features such as lava. Port of
     * {@code feat_is_fiery()} in {@code cave-square.c}, which tests the FIERY
     * flag. (The C comment on that function is a copy of the one for
     * {@code feat_is_bright()} and says "internally lit"; the code tests FIERY.)
     *
     * <p>Function isFiery coded before 260930, commented in full on 260930.
     *
     * @return true if this feature is fiery
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isFiery() {
        return flags.has(TerrainFeatureFlags.TF_FIERY);
    }

    /**
     * Tests whether the feature carries no monster flow information. Port of
     * {@code feat_is_no_flow()} in {@code cave-square.c}, which tests the
     * NO_FLOW flag.
     *
     * <p>Function isNoFlow coded before 260930, commented in full on 260930.
     *
     * @return true if the feature DOESN'T carry monster flow information
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isNoFlow() {
        return flags.has(TerrainFeatureFlags.TF_NO_FLOW);
    }

    /**
     * Tests whether the feature carries no player scent. Port of
     * {@code feat_is_no_scent()} in {@code cave-square.c}, which tests the
     * NO_SCENT flag.
     *
     * <p>Function isNoScent coded before 260930, commented in full on 260930.
     *
     * @return true if the feature DOESN'T carry player scent information
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isNoScent() {
        return flags.has(TerrainFeatureFlags.TF_NO_SCENT);
    }

    /**
     * Tests whether the feature should have smooth boundaries (for dungeon
     * generation). Port of {@code feat_is_smooth()} in {@code cave-square.c},
     * which tests the SMOOTH flag.
     *
     * <p>Function isSmooth coded before 260930, commented in full on 260930.
     *
     * @return true if the feature should have smooth boundaries
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isSmooth() {
        return flags.has(TerrainFeatureFlags.TF_SMOOTH);
    }

    /**
     * Tests whether the feature is any kind of door. The C original has no
     * {@code feat_is_*()} function for this; {@code square_isdoor()} in
     * {@code cave-square.c} tests the DOOR_ANY flag directly. DOOR_ANY covers
     * open, closed, broken and secret doors.
     *
     * <p>Function hasAnyDoor coded before 260930, commented in full on 260930.
     *
     * @return true if this feature is any door type
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasAnyDoor() {
        return flags.has(TerrainFeatureFlags.TF_DOOR_ANY);
    }

    /**
     * Tests for a permanent <em>wall</em>: the PERMANENT flag together with the
     * ROCK flag. This is the test C's {@code square_isperm()} in
     * {@code cave-square.c} makes. PERMANENT alone is not enough, because the
     * staircases and shop entrances are PERMANENT without being ROCK. Compare
     * {@link #isPermanent()}, which is a code comparison.
     *
     * <p>Function isFullPermanent coded on 260930, commented in full on 260930.
     *
     * @return true if this is a permanent wall
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isFullPermanent() {
        return flags.has(TerrainFeatureFlags.TF_PERMANENT)
                && flags.has(TerrainFeatureFlags.TF_ROCK);
    }

    /**
     * Tests whether this feature is the permanent wall itself, by code. This is
     * the test C's {@code map_info()} in {@code cave-map.c} makes for the
     * hallucination rolls, {@code f_idx != FEAT_PERM}: only the outer wall is
     * excluded, and the staircases and shop entrances are not, though they carry
     * the PERMANENT flag. Compare {@link #isFullPermanent()}, which is a flag
     * test.
     *
     * <p>Function isPermanent coded on 260930, commented in full on 260930.
     *
     * @return true if this feature's code is {@code FEAT_PERM}
     */
    public boolean isPermanent() {
        return code == TerrainFlags.FEAT_PERM;
    }

    /**
     * Tests whether the feature is a normal granite rock wall: the GRANITE flag
     * without the DOOR_ANY flag, so secret doors (which look like granite) are
     * left out. Port of {@code square_isrock()} in {@code cave-square.c}.
     *
     * <p>Function isRock coded before 260930, commented in full on 260930.
     *
     * @return true if the feature is rock
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isRock() {
        return flags.has(TerrainFeatureFlags.TF_GRANITE) && !flags.has(TerrainFeatureFlags.TF_DOOR_ANY);
    }

    /**
     * Tests the ROCK flag alone, which is true for every rocky feature: the
     * mineral walls, permanent wall, rubble and secret doors. This is the test
     * C's {@code square_seemslikewall()} in {@code cave-square.c} makes; it is
     * also half of {@code square_isrubble()} (ROCK without WALL),
     * {@code square_issecretdoor()} (ROCK with DOOR_ANY) and
     * {@code square_isperm()} (ROCK with PERMANENT). Not to be confused with
     * {@link #isRock()}, which is granite only.
     *
     * <p>Function fullRock coded before 260930, commented in full on 260930.
     *
     * @return true if the feature has the ROCK flag
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean fullRock() {
        return flags.has(TerrainFeatureFlags.TF_ROCK);
    }

    /**
     * Tests whether the feature is an open door. Port of
     * {@code square_isopendoor()} in {@code cave-square.c}, which tests the
     * CLOSABLE flag. That is the same flag {@link #isCloseable()} tests.
     *
     * <p>Function isOpenDoor coded before 260930, commented in full on 260930.
     *
     * @return true for open doors
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isOpenDoor() {
        return flags.has(TerrainFeatureFlags.TF_CLOSABLE);
    }

    /**
     * Tests whether this feature is a closed door (possibly locked or jammed).
     * Port of {@code square_iscloseddoor()} in {@code cave-square.c}, which
     * tests the DOOR_CLOSED flag.
     *
     * <p>Function isClosedDoor coded before 260930, commented in full on 260930.
     *
     * @return true for closed doors
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isClosedDoor() {
        return flags.has(TerrainFeatureFlags.TF_DOOR_CLOSED);
    }

    /**
     * Tests whether the feature is a door which can be closed. The CLOSABLE
     * flag is set only on open doors, so this is the same test as
     * {@link #isOpenDoor()}.
     *
     * <p>Function isCloseable coded before 260930, commented in full on 260930.
     *
     * @return true for closeable doors
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isCloseable() {
        return flags.has(TerrainFeatureFlags.TF_CLOSABLE);
    }

    /**
     * Tests for any staircase. Port of {@code square_isstairs()} in
     * {@code cave-square.c}, which tests the STAIR flag.
     *
     * <p>Function isStair coded before 260930, commented in full on 260930.
     *
     * @return true for any staircase
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isStair() {
        return flags.has(TerrainFeatureFlags.TF_STAIR);
    }

    /**
     * Tests for an up staircase. Port of {@code square_isupstairs()} in
     * {@code cave-square.c}, which tests the UPSTAIR flag.
     *
     * <p>Function isUpStair coded before 260930, commented in full on 260930.
     *
     * @return true if this is an up staircase
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isUpStair() {
        return flags.has(TerrainFeatureFlags.TF_UPSTAIR);
    }

    /**
     * Tests for a down staircase. Port of {@code square_isdownstairs()} in
     * {@code cave-square.c}, which tests the DOWNSTAIR flag.
     *
     * <p>Function isDownStair coded before 260930, commented in full on 260930.
     *
     * @return true if this is a down staircase
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isDownStair() {
        return flags.has(TerrainFeatureFlags.TF_DOWNSTAIR);
    }

    /**
     * Tests whether this is the "nothing known" feature, {@code FEAT_NONE},
     * which is what an unknown grid shows. C has no function for this; it
     * compares the feature index with {@code FEAT_NONE} where it needs to.
     *
     * <p>Function isNoFeat coded before 260930, commented in full on 260930.
     *
     * @return true if NOTHING is known about this feature
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isNoFeat() {
        return code == TerrainFlags.FEAT_NONE;
    }

    /**
     * Tests whether this feature is interesting, meaning it is noticed when
     * looking around. Port of the test in {@code square_isinteresting()} in
     * {@code cave-square.c}, which tests the INTERESTING flag.
     *
     * <p>Function isInteresting coded before 260930, commented in full on 260930.
     *
     * @return true if this feature is flagged as interesting (noticed when looking around)
     */
    public boolean isInteresting() {
        return flags.has(TerrainFeatureFlags.TF_INTERESTING);
    }

    /**
     * Get the name of this feature. C's {@code name} field, which the
     * {@code square_apparent_name()} function of {@code cave-square.c} returns
     * after applying the mimic.
     *
     * <p>Function getName coded before 260930, commented in full on 260930.
     *
     * @return the name of this feature
     */
    public String getName() {
        return name;
    }

    /**
     * Get this feature's terrain code. Identical to {@link #getTerrainFlag()}.
     *
     * <p>Function getCodeFlags coded before 260930, commented in full on 260930.
     *
     * @return this feature's terrain code (same as {@link #getTerrainFlag()})
     */
    public TerrainFlags getCodeFlags() {
        return code;
    }

    /**
     * Resolve the feature this one is displayed as. C swaps in the mimicked
     * feature only when the {@code mimic} pointer is non-NULL and otherwise
     * keeps the feature it has (see {@code map_info()} in {@code cave-map.c},
     * and the {@code mimic ? mimic : &f_info[actual]} form in the
     * {@code square_apparent_*()} functions of {@code cave-square.c}); this does
     * the same. When {@link #isMimicing()} is true the mimicked feature is
     * looked up in the {@link TerrainRegistry}, and the result is {@code null}
     * if the registry has no feature with that code. When it is false, this
     * feature is returned unchanged, so the result can be assigned back over the
     * original without a guard.
     *
     * <p>Function getMimic coded before 260930, updated on 260930 to return
     * {@code this} when there is no mimic, commented in full on 260930.
     *
     * @return the mimicked {@link Feature}, or this feature if it does not mimic anything
     */
    public Feature getMimic() {
        if (isMimicing())
            return TerrainRegistry.lookupFeature(mimic);
        return this;
    }

    /**
     * Build a debug string listing this feature's fields.
     *
     * <p>Function toString coded before 260930, commented in full on 260930.
     *
     * @return a debug string listing this feature's fields
     */
    @Override
    public String toString() {
        return "Feature{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", mimic=" + mimic +
                ", priority=" + priority +
                ", shopNum=" + shopNum +
                ", dig=" + dig +
                ", flags=" + flags +
                ", displayCharacter=" + displayCharacter +
                ", walkMsg='" + walkMsg + '\'' +
                ", runMsg='" + runMsg + '\'' +
                ", hurtMsg='" + hurtMsg + '\'' +
                ", dieMsg='" + dieMsg + '\'' +
                ", confusedMsg='" + confusedMsg + '\'' +
                ", lookPrefix='" + lookPrefix + '\'' +
                ", lookInPreposition='" + lookInPreposition + '\'' +
                ", resistFlag=" + resistFlag +
                '}';
    }

    /**
     * Compares every field except the deprecated {@code featureIndex}. Java-only;
     * C compares features by pointer or index.
     *
     * <p><strong>Outstanding:</strong> {@code flags} is compared with
     * {@code Objects.equals} and {@code resistFlag} with {@code ==}, but
     * {@code Flag} defines neither {@code equals} nor {@code hashCode} (only
     * {@code isEqual}), so these are identity comparisons. Two features built
     * from separate {@code Flag} objects with identical contents are therefore
     * not equal, and only the same instance, or two features sharing the same
     * {@code Flag} objects, compare equal.
     *
     * <p>Function equals coded before 260930, commented in full on 260930.
     *
     * @param o the object to compare against
     * @return true if {@code o} is an equivalent {@code Feature}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;
        return priority == feature.priority && shopNum == feature.shopNum
                && dig == feature.dig && code == feature.code && Objects.equals(getName(), feature.getName())
                && Objects.equals(description, feature.description) && mimic == feature.mimic
                && Objects.equals(flags, feature.flags) && Objects.equals(displayCharacter, feature.displayCharacter)
                && Objects.equals(walkMsg, feature.walkMsg) && Objects.equals(runMsg, feature.runMsg)
                && Objects.equals(hurtMsg, feature.hurtMsg) && Objects.equals(dieMsg, feature.dieMsg)
                && Objects.equals(confusedMsg, feature.confusedMsg) && Objects.equals(lookPrefix, feature.lookPrefix)
                && Objects.equals(lookInPreposition, feature.lookInPreposition) && resistFlag == feature.resistFlag;
    }

    /**
     * Hash code combining the same fields as {@link #equals(Object)}. Because
     * {@code Flag} has no {@code hashCode} of its own, the {@code flags} and
     * {@code resistFlag} terms are identity hashes (see the note on
     * {@code equals}).
     *
     * <p>Function hashCode coded before 260930, commented in full on 260930.
     *
     * @return this feature's hash code
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(code);
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(mimic);
        result = 31 * result + priority;
        result = 31 * result + shopNum;
        result = 31 * result + dig;
        result = 31 * result + Objects.hashCode(flags);
        result = 31 * result + Objects.hashCode(displayCharacter);
        result = 31 * result + Objects.hashCode(walkMsg);
        result = 31 * result + Objects.hashCode(runMsg);
        result = 31 * result + Objects.hashCode(hurtMsg);
        result = 31 * result + Objects.hashCode(dieMsg);
        result = 31 * result + Objects.hashCode(confusedMsg);
        result = 31 * result + Objects.hashCode(lookPrefix);
        result = 31 * result + Objects.hashCode(lookInPreposition);
        result = 31 * result + Objects.hashCode(resistFlag);
        return result;
    }
}