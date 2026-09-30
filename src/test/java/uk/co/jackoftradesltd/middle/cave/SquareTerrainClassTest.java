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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Runs every terrain class in {@code terrain.txt} that carries a rock-related flag through the six
 * {@link Square} predicates whose C definitions read {@code TF_ROCK}, {@code TF_GRANITE} or both,
 * and checks the answers against the definitions in {@code cave-square.c}.
 *
 * <p><b>The trap being tested:</b> C has two different questions that both get called "rock".
 * {@code square_isrock} is {@code TF_GRANITE && !TF_DOOR_ANY}, so it is plain granite only. The
 * {@code TF_ROCK} flag is wider: it is set on granite, magma, quartz, permanent walls, secret doors
 * and rubble alike. {@code square_isperm}, {@code square_isrubble}, {@code square_issecretdoor} and
 * {@code square_seemslikewall} all test the flag, and {@code square_ismineral} tests the granite
 * question. A port that answers both with the granite test gets the first four wrong for every
 * terrain that has {@code ROCK} but not {@code GRANITE}: permanent walls, rubble and veins.
 *
 * <p>Each row below carries the exact flag line from {@code terrain.txt} and the answers derived by
 * hand from the C definitions, not by running the Java.
 *
 * <p>Class SquareTerrainClassTest coded on 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class SquareTerrainClassTest {

    private static final TerrainFeatureFlags FLOOR = TerrainFeatureFlags.TF_FLOOR;
    private static final TerrainFeatureFlags PASSABLE = TerrainFeatureFlags.TF_PASSABLE;
    private static final TerrainFeatureFlags WALL = TerrainFeatureFlags.TF_WALL;
    private static final TerrainFeatureFlags ROCK = TerrainFeatureFlags.TF_ROCK;
    private static final TerrainFeatureFlags GRANITE = TerrainFeatureFlags.TF_GRANITE;
    private static final TerrainFeatureFlags DOOR_ANY = TerrainFeatureFlags.TF_DOOR_ANY;
    private static final TerrainFeatureFlags DOOR_CLOSED = TerrainFeatureFlags.TF_DOOR_CLOSED;
    private static final TerrainFeatureFlags CLOSABLE = TerrainFeatureFlags.TF_CLOSABLE;
    private static final TerrainFeatureFlags PERMANENT = TerrainFeatureFlags.TF_PERMANENT;
    private static final TerrainFeatureFlags MAGMA = TerrainFeatureFlags.TF_MAGMA;
    private static final TerrainFeatureFlags QUARTZ = TerrainFeatureFlags.TF_QUARTZ;
    private static final TerrainFeatureFlags GOLD = TerrainFeatureFlags.TF_GOLD;
    private static final TerrainFeatureFlags STAIR = TerrainFeatureFlags.TF_STAIR;
    private static final TerrainFeatureFlags FIERY = TerrainFeatureFlags.TF_FIERY;

    private static Row row(String name, boolean isRock, boolean isPerm, boolean isRubble,
                           boolean secretDoor, boolean seemsWall, boolean mineral,
                           TerrainFeatureFlags... flags) {
        return new Row(name, flags, isRock, isPerm, isRubble, secretDoor, seemsWall, mineral);
    }

    /**
     * Every terrain in {@code terrain.txt} whose flags touch these predicates, plus the floor,
     * doors and stairs that must answer false for all of them.
     */
    private static List<Row> rows() {
        return List.of(
                // name                            rock   perm   rubble secret seems  mineral
                row("open floor", false, false, false, false, false, false, FLOOR, PASSABLE),
                row("closed door", false, false, false, false, false, false, DOOR_ANY, DOOR_CLOSED),
                row("open door", false, false, false, false, false, false, DOOR_ANY, PASSABLE, CLOSABLE),
                // PERMANENT without ROCK: a staircase is permanent but not a permanent wall
                row("up staircase", false, false, false, false, false, false, PASSABLE, PERMANENT, STAIR),
                // DOOR_ANY and ROCK: hides as granite, so it is a secret door and seems like a wall
                row("secret door", false, false, false, true, true, false, WALL, ROCK, DOOR_ANY, GRANITE),
                // ROCK without WALL: rubble
                row("pile of rubble", false, false, true, false, true, false, ROCK),
                row("pile of passable rubble", false, false, true, false, true, false, ROCK, PASSABLE),
                // ROCK without GRANITE: the veins are mineral through MAGMA / QUARTZ, not through isrock
                row("magma vein", false, false, false, false, true, true, WALL, ROCK, MAGMA),
                row("quartz vein", false, false, false, false, true, true, WALL, ROCK, QUARTZ),
                row("magma vein with treasure", false, false, false, false, true, true, WALL, ROCK, GOLD, MAGMA),
                row("quartz vein with treasure", false, false, false, false, true, true, WALL, ROCK, GOLD, QUARTZ),
                // GRANITE and no door: the only terrain square_isrock accepts
                row("granite wall", true, false, false, false, true, true, WALL, ROCK, GRANITE),
                // PERMANENT and ROCK, no GRANITE: perm is true, isrock and mineral are not
                row("permanent wall", false, true, false, false, true, false, WALL, ROCK, PERMANENT),
                row("lava", false, false, false, false, false, false, PASSABLE, FIERY));
    }

    private static Square square(TerrainFeatureFlags... flags) {
        Flag<TerrainFeatureFlags> set = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : flags) {
            set.on(flag);
        }
        Feature feature = new Feature(null, "test", "", null, 0, 0, set, null, "", "", "", "", "", "", "",
                new Flag<>(MonsterRaceFlag.class), 0);
        return new Square(feature, 0, 0);
    }

    /**
     * Each of the six predicates, on each terrain, against the C-derived answer.
     */
    @Test
    @DisplayName("the rock predicates match cave-square.c for every rocky terrain")
    void rockPredicatesMatchC() {
        List<Executable> checks = new ArrayList<>();
        for (Row r : rows()) {
            Square s = square(r.flags());
            checks.add(() -> assertEquals(r.isRock(), s.isRock(), r.name() + ": square_isrock"));
            checks.add(() -> assertEquals(r.isPerm(), s.isPerm(), r.name() + ": square_isperm"));
            checks.add(() -> assertEquals(r.isRubble(), s.isRubble(), r.name() + ": square_isrubble"));
            checks.add(() -> assertEquals(r.secretDoor(), s.isSecretDoor(), r.name() + ": square_issecretdoor"));
            checks.add(() -> assertEquals(r.seemsWall(), s.featSeemsLikeWall(), r.name() + ": square_seemslikewall"));
            checks.add(() -> assertEquals(r.mineral(), s.isMineral(), r.name() + ": square_ismineral"));
        }
        assertAll(checks);
    }

    /**
     * {@code Feature.fullRock()} is the {@code TF_ROCK} flag and {@code Feature.isRock()} is C's
     * {@code square_isrock}; they must differ on exactly the terrains that have one and not the other.
     */
    @Test
    @DisplayName("Feature separates the ROCK flag from the granite test")
    void featureSeparatesTheTwoQuestions() {
        Feature perm = square(WALL, ROCK, PERMANENT).getFeature();
        Feature granite = square(WALL, ROCK, GRANITE).getFeature();
        Feature secret = square(WALL, ROCK, DOOR_ANY, GRANITE).getFeature();
        assertAll(
                () -> assertFalse(perm.isRock(), "permanent wall has no GRANITE"),
                () -> assertEquals(true, perm.fullRock(), "permanent wall has ROCK"),
                () -> assertEquals(true, granite.isRock()),
                () -> assertEquals(true, granite.fullRock()),
                () -> assertFalse(secret.isRock(), "a door is excluded from isrock"),
                () -> assertEquals(true, secret.fullRock()));
    }

    /**
     * One terrain class and the six answers C gives for it.
     *
     * @param name       the {@code name:} line of the terrain in {@code terrain.txt}
     * @param flags      the terrain's {@code flags:} line, restricted to the flags these predicates read
     * @param isRock     C's {@code square_isrock}: granite that is not a door
     * @param isPerm     C's {@code square_isperm}: permanent and rocky
     * @param isRubble   C's {@code square_isrubble}: rocky but not a wall
     * @param secretDoor C's {@code square_issecretdoor}: a door that is rocky
     * @param seemsWall  C's {@code square_seemslikewall}: rocky
     * @param mineral    C's {@code square_ismineral}: rock, magma or quartz
     */
    private record Row(String name, TerrainFeatureFlags[] flags, boolean isRock, boolean isPerm,
                       boolean isRubble, boolean secretDoor, boolean seemsWall, boolean mineral) {
    }
}
