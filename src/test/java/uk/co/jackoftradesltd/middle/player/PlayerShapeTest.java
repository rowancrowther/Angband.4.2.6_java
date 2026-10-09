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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the data holder {@link PlayerShape}, the port of C's {@code struct player_shape}
 * ({@code player.h}). {@link PlayerShapeModifierTest} covers {@code getModifier} for the real
 * stats; this class covers the rest.
 *
 * <p>The expected values are the ones C's {@code struct player_shape} would hold after parsing the
 * same {@code shape.txt} lines: the {@code combat:} line fills {@code to_h}, {@code to_d} and
 * {@code to_a} in that order, so a constructor that crossed the three would show up here, and the
 * values given are all different to catch it. The class stores what it is given and nothing more,
 * so the tests pin that and the zero default for anything absent, which stands in for C's
 * zero-filled arrays.
 *
 * <p>Class PlayerShapeTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class PlayerShapeTest {

    /**
     * A shape with every attribute distinct, so a crossed assignment cannot pass.
     *
     * @return the shape
     */
    private static PlayerShape fullShape() {
        Flag<ObjectFlag> of = new Flag<>(ObjectFlag.class);
        of.on(ObjectFlag.OF_FEATHER);
        Flag<PlayerFlag> pf = new Flag<>(PlayerFlag.class);
        pf.on(PlayerFlag.PF_UNLIGHT);
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(-1);
        return new PlayerShape("fox", 3, 1, 2,
                Map.of(PlayerSkill.SKILL_STEALTH, 7),
                of, pf,
                Map.of(ObjectModifier.OM_SPEED, 5),
                Map.of(ElementEnum.ELEM_FIRE, fire),
                List.of(), 2,
                List.of(new PlayerBlow("bite"), new PlayerBlow("claw")));
    }

    /**
     * Each constructor argument comes back from its own getter. C's {@code combat:} line orders
     * the three bonuses to-h, to-d, to-a, while the constructor takes to-a first, so the distinct
     * values 3, 1 and 2 would expose a swap.
     */
    @Test
    @DisplayName("constructor arguments round-trip through their getters")
    void constructorRoundTrip() {
        PlayerShape s = fullShape();

        assertAll(
                () -> assertEquals("fox", s.getName()),
                () -> assertEquals(3, s.getToAc()),
                () -> assertEquals(1, s.getToHit()),
                () -> assertEquals(2, s.getToDam()),
                () -> assertEquals(7, s.getSkills().get(PlayerSkill.SKILL_STEALTH)),
                () -> assertTrue(s.getFlags().has(ObjectFlag.OF_FEATHER)),
                () -> assertTrue(s.getPflags().has(PlayerFlag.PF_UNLIGHT)),
                () -> assertEquals(5, s.getObjectValueModifiers().get(ObjectModifier.OM_SPEED)),
                () -> assertEquals(-1, s.getElementValueModifiers()
                        .get(ElementEnum.ELEM_FIRE).getResLevel()),
                () -> assertTrue(s.getEffect().isEmpty()));
    }

    /**
     * The blow count and the names are held together, as C's {@code num_blows} and {@code blows}
     * chain are, and the names come back in the order given.
     */
    @Test
    @DisplayName("blow count and blow names are held as given")
    void blows() {
        PlayerShape s = fullShape();

        assertAll(
                () -> assertEquals(2, s.getNumBlows()),
                () -> assertEquals(2, s.getPlayerBlow().size()),
                () -> assertEquals("bite", s.getPlayerBlow().get(0).getBlowName()),
                () -> assertEquals("claw", s.getPlayerBlow().get(1).getBlowName()));
    }

    /**
     * A shape whose data file had no {@code blow:} lines has zero blows and an empty list;
     * C's {@code blows} is NULL and {@code num_blows} zero.
     */
    @Test
    @DisplayName("a shape with no blow lines has none")
    void noBlows() {
        PlayerShape s = new PlayerShape("plain", 0, 0, 0, Map.of(),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                Map.of(), Map.of(), List.of(), 0, List.of());

        assertAll(
                () -> assertEquals(0, s.getNumBlows()),
                () -> assertTrue(s.getPlayerBlow().isEmpty()));
    }

    /**
     * The getters hand out the stored collections themselves, not copies: the player's state
     * merges them directly, as C reads the arrays in place.
     */
    @Test
    @DisplayName("collection getters return the stored object")
    void gettersAreNotCopies() {
        Map<PlayerSkill, Integer> skills = Map.of(PlayerSkill.SKILL_SAVE, 4);
        Flag<ObjectFlag> of = new Flag<>(ObjectFlag.class);
        PlayerShape s = new PlayerShape("x", 0, 0, 0, skills, of,
                new Flag<>(PlayerFlag.class), Map.of(), Map.of(), List.of(), 0, List.of());

        assertAll(
                () -> assertSame(skills, s.getSkills()),
                () -> assertSame(of, s.getFlags()));
    }

    /**
     * Nothing is stored for the modifiers a shape does not name, and C's zero-filled
     * {@code modifiers[]} reads as zero for every one of them. Every stat of a shape with an empty
     * modifier map reads zero.
     */
    @Test
    @DisplayName("an empty modifier map reads zero for every stat")
    void emptyModifiersReadZero() {
        PlayerShape s = new PlayerShape("x", 0, 0, 0, Map.of(),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                Map.of(), Map.of(), List.of(), 0, List.of());

        assertAll(
                () -> assertEquals(0, s.getModifier(Stats.STAT_STR)),
                () -> assertEquals(0, s.getModifier(Stats.STAT_INT)),
                () -> assertEquals(0, s.getModifier(Stats.STAT_WIS)),
                () -> assertEquals(0, s.getModifier(Stats.STAT_DEX)),
                () -> assertEquals(0, s.getModifier(Stats.STAT_CON)));
    }

    /**
     * The sentinels resolve to {@code OM_NONE} and {@code OM_MAX}, which exist in
     * {@code ObjectModifier}, so they return zero rather than throwing. C has no value to compare
     * with here, since it would subscript outside the stat range; this pins the documented
     * behaviour so a change to it is deliberate.
     */
    @Test
    @DisplayName("the stat sentinels read zero rather than throwing")
    void sentinelsReadZero() {
        PlayerShape s = fullShape();

        assertAll(
                () -> assertEquals(0, s.getModifier(Stats.STAT_NONE)),
                () -> assertEquals(0, s.getModifier(Stats.STAT_MAX)));
    }
}
