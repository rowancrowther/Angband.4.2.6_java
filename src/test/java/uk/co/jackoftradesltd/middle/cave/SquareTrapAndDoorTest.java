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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.SquareEnum;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link Square} predicates that read the trap list and the door terrain — the trap scans
 * {@link Square#trapSpecific}, {@link Square#trapFlag} and {@link Square#trapTimeout}, the lock
 * predicates built on them, and the behaviour predicates that veto on a trap — against C's
 * {@code cave-square.c} and {@code trap.c}.
 *
 * <p><b>Trap kinds are matched by index, not by description.</b> C's {@code square_trap_specific}
 * and {@code square_trap_timeout} compare {@code trap->t_idx}, the kind's position in the table.
 * {@code trap.txt} gives several kinds the same description (three of the dart traps read "A trap
 * which shoots damaging darts."), so two kinds built here with one description and different
 * indexes are the case where a description comparison and C disagree. The fixtures use that on
 * purpose.
 *
 * <p><b>Every trap scan is gated on {@code SQUARE_TRAP}, except the timeout one.</b> C tests the
 * marker before it scans in {@code square_trap_specific} and {@code square_trap_flag}, but
 * {@code square_trap_timeout} walks the chain unconditionally. Each half is pinned, since the two
 * gates look alike and are easy to make uniform by accident.
 *
 * <p>The trap registry is seeded with kinds named as {@code trap.txt} names them and restored after
 * each test, so the lookups by name ({@code "web"}, {@code "decoy"}, {@code "door lock"},
 * {@code "glyph of warding"}) resolve to the fixtures. {@link Trap} has no constructor or setters
 * for its state, so the tests fill its private fields by reflection.
 *
 * <p>Class SquareTrapAndDoorTest coded on 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
class SquareTrapAndDoorTest {

    /**
     * Fixture kinds, indexed as their position in the seeded registry. The two dart kinds share a
     * description and differ only in index.
     */
    private TrapKind web;
    private TrapKind decoy;
    private TrapKind lock;
    private TrapKind glyph;
    private TrapKind dartA;
    private TrapKind dartB;

    private List<TrapKind> savedRegistry;

    private static TrapKind kind(String name, String description, int index) {
        return new TrapKind(name, "", description, "", "", "", "", index, null, 0, 0, 0, null,
                new Flag<>(TrapEnum.class), null, new ArrayList<>(), new ArrayList<>());
    }

    private static void set(Trap trap, String field, Object value) {
        try {
            Field f = Trap.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(trap, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * A trap of the given kind with the given power and timeout and the given trap flags on.
     */
    private static Trap trap(TrapKind kind, int power, int timeout, TrapEnum... flags) {
        Trap trap = new Trap();
        Flag<TrapEnum> set = new Flag<>(TrapEnum.class);
        for (TrapEnum flag : flags) {
            set.on(flag);
        }
        set(trap, "kind", kind);
        set(trap, "power", power);
        set(trap, "timeout", timeout);
        set(trap, "flags", set);
        return trap;
    }

    private static Feature feature(TerrainFeatureFlags... flags) {
        Flag<TerrainFeatureFlags> set = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : flags) {
            set.on(flag);
        }
        return new Feature(null, "test", "", null, 0, 0, set, null, "", "", "", "", "", "", "",
                new Flag<>(MonsterRaceFlag.class), 0);
    }

    /**
     * A square of the given terrain carrying the given traps, with {@code SQUARE_TRAP} on when
     * {@code marked} is true.
     */
    private static Square square(Feature feature, boolean marked, Trap... traps) {
        Square square = new Square(feature, 0, 0);
        square.getTraps().addAll(List.of(traps));
        if (marked) square.sqInfoOn(SquareEnum.SQUARE_TRAP);
        return square;
    }

    private static Square floorWith(boolean marked, Trap... traps) {
        return square(feature(TerrainFeatureFlags.TF_FLOOR), marked, traps);
    }

    private static Square closedDoorWith(Trap... traps) {
        return square(feature(TerrainFeatureFlags.TF_DOOR_ANY, TerrainFeatureFlags.TF_DOOR_CLOSED), true, traps);
    }

    /**
     * Seeds the trap registry with the six fixture kinds, remembering what was there.
     */
    @BeforeEach
    void seedRegistry() {
        savedRegistry = new ArrayList<>(TerrainRegistry.getTrapInfo());
        web = kind("web", "web", 0);
        decoy = kind("decoy", "decoy", 1);
        lock = kind("door lock", "door lock", 2);
        glyph = kind("glyph of warding", "glyph of warding", 3);
        dartA = kind("dart trap", "damaging darts", 4);
        dartB = kind("dart trap", "damaging darts", 5);
        TerrainRegistry.setTrapInfo(new ArrayList<>(List.of(web, decoy, lock, glyph, dartA, dartB)));
    }

    /**
     * Puts the registry back as the test found it.
     */
    @AfterEach
    void restoreRegistry() {
        TerrainRegistry.setTrapInfo(savedRegistry);
    }

    /**
     * {@code square_trap_specific}, {@code square_trap_flag} and {@code square_trap_timeout}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the trap scans")
    class Scans {

        /**
         * C compares {@code trap->t_idx == t_idx}. Two kinds with one description and different
         * indexes are different kinds, so asking for one must not find the other.
         */
        @Test
        @DisplayName("trapSpecific tells kinds apart by index, not by shared description")
        void specificUsesIndex() {
            Square s = floorWith(true, trap(dartA, 0, 0));
            assertAll(
                    () -> assertTrue(s.trapSpecific(dartA)),
                    () -> assertFalse(s.trapSpecific(dartB)),
                    () -> assertFalse(s.trapSpecific(web)));
        }

        /**
         * C walks {@code trap->next} until it finds a match, so a kind that is not first in the list
         * is still found.
         */
        @Test
        @DisplayName("trapSpecific scans the whole list")
        void specificScansAll() {
            Square s = floorWith(true, trap(web, 0, 0), trap(decoy, 0, 0));
            assertAll(
                    () -> assertTrue(s.trapSpecific(decoy)),
                    () -> assertTrue(s.trapSpecific(web)),
                    () -> assertFalse(s.trapSpecific(glyph)));
        }

        /**
         * C returns false at once when {@code square_istrap} is off, whatever the chain holds. A grid
         * with traps in the list but no marker must answer false to every specific test.
         */
        @Test
        @DisplayName("trapSpecific and trapFlag answer false when SQUARE_TRAP is off")
        void markerGatesSpecificAndFlag() {
            Square s = floorWith(false, trap(web, 0, 0, TrapEnum.TRF_TRAP, TrapEnum.TRF_VISIBLE));
            assertAll(
                    () -> assertFalse(s.trapSpecific(web)),
                    () -> assertFalse(s.trapFlag(TrapEnum.TRF_TRAP)),
                    () -> assertFalse(s.isPlayerTrap()),
                    () -> assertFalse(s.isVisibleTrap()));
        }

        /**
         * {@code square_isplayertrap} is {@code TRF_TRAP} and {@code square_isvisibletrap} is
         * {@code TRF_VISIBLE}; they are separate flags, so a hidden player trap is one without the
         * other.
         */
        @Test
        @DisplayName("player-trap and visible-trap are separate flags")
        void trapFlags() {
            Square hidden = floorWith(true, trap(dartA, 0, 0, TrapEnum.TRF_TRAP));
            Square shown = floorWith(true, trap(dartA, 0, 0, TrapEnum.TRF_TRAP, TrapEnum.TRF_VISIBLE));
            assertAll(
                    () -> assertTrue(hidden.isPlayerTrap()),
                    () -> assertFalse(hidden.isVisibleTrap()),
                    () -> assertTrue(shown.isPlayerTrap()),
                    () -> assertTrue(shown.isVisibleTrap()));
        }

        /**
         * The flag scan is over every trap: a flag carried only by the second trap is found.
         */
        @Test
        @DisplayName("trapFlag scans the whole list")
        void flagScansAll() {
            Square s = floorWith(true, trap(web, 0, 0), trap(dartA, 0, 0, TrapEnum.TRF_VISIBLE));
            assertTrue(s.trapFlag(TrapEnum.TRF_VISIBLE));
        }

        /**
         * With timeouts web 0, decoy 7, glyph 3, C's {@code square_trap_timeout(c, grid, -1)} skips
         * the zero timeout and returns the first non-zero one, 7. A specific index returns that
         * kind's timeout; a matching kind whose timeout is zero does not end the scan and gives 0
         * at the end of it; an index that no trap has gives 0.
         */
        @Test
        @DisplayName("trapTimeout matches by kind index and skips zero timeouts")
        void timeoutByKindIndex() {
            Square s = floorWith(true, trap(web, 0, 0), trap(decoy, 0, 7), trap(glyph, 0, 3));
            assertAll(
                    () -> assertEquals(7, s.trapTimeout(-1)),
                    () -> assertEquals(3, s.trapTimeout(glyph.getTrapKindIndex())),
                    () -> assertEquals(7, s.trapTimeout(decoy.getTrapKindIndex())),
                    () -> assertEquals(0, s.trapTimeout(web.getTrapKindIndex())),
                    () -> assertEquals(0, s.trapTimeout(dartA.getTrapKindIndex())));
        }

        /**
         * The index is the kind's, not the trap's place in the list. With the decoy (kind 1) first
         * and the glyph (kind 3) second, asking for 1 must not be answered by the trap at list
         * position 1.
         */
        @Test
        @DisplayName("trapTimeout's index is the kind index, not the list position")
        void timeoutIsNotListPosition() {
            Square s = floorWith(true, trap(decoy, 0, 5), trap(glyph, 0, 9));
            assertAll(
                    () -> assertEquals(5, s.trapTimeout(1)),
                    () -> assertEquals(9, s.trapTimeout(3)),
                    () -> assertEquals(0, s.trapTimeout(0)));
        }

        /**
         * Unlike the other two scans, C's {@code square_trap_timeout} never asks
         * {@code square_istrap}, so it answers from the chain even with the marker off.
         */
        @Test
        @DisplayName("trapTimeout does not consult the SQUARE_TRAP marker")
        void timeoutIgnoresMarker() {
            Square s = floorWith(false, trap(decoy, 0, 4));
            assertEquals(4, s.trapTimeout(-1));
        }

        /**
         * No traps at all: every scan answers as an empty chain, and {@code square_trap} is
         * {@code NULL}.
         */
        @Test
        @DisplayName("a trapless square answers false, zero and null")
        void empty() {
            Square s = floorWith(false);
            assertAll(
                    () -> assertFalse(s.trapSpecific(web)),
                    () -> assertEquals(0, s.trapTimeout(-1)),
                    () -> assertNull(s.getTrap()));
        }

        /**
         * {@code square_trap} returns the head of the chain, which is the port's first element.
         */
        @Test
        @DisplayName("getTrap is the first trap")
        void topTrap() {
            Trap first = trap(web, 0, 0);
            Square s = floorWith(true, first, trap(decoy, 0, 0));
            assertSame(first, s.getTrap());
        }
    }

    /**
     * {@code square_iswebbed}, {@code square_isdecoyed} and {@code square_iswarded}, which look a
     * kind up by name and scan for it.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the named traps")
    class NamedTraps {

        /**
         * Each predicate finds its own kind and none of the others.
         */
        @Test
        @DisplayName("web, decoy and glyph are found by name and only by name")
        void byName() {
            Square s = floorWith(true, trap(decoy, 0, 0));
            assertAll(
                    () -> assertTrue(s.isDecoyed()),
                    () -> assertFalse(s.isWebbed()),
                    () -> assertFalse(s.isWarded()));
            Square w = floorWith(true, trap(glyph, 0, 0));
            assertAll(
                    () -> assertTrue(w.isWarded()),
                    () -> assertFalse(w.isDecoyed()));
        }

        /**
         * A web, or a player trap, vetoes {@code square_isempty} and {@code square_isarrivable}, in
         * C's order and before the floor test; an ordinary trapless floor passes both.
         */
        @Test
        @DisplayName("a web or a player trap makes a floor neither empty nor arrivable")
        void vetoes() {
            Square clean = floorWith(false);
            Square webbed = floorWith(true, trap(web, 0, 0));
            Square trapped = floorWith(true, trap(dartA, 0, 0, TrapEnum.TRF_TRAP));
            assertAll(
                    () -> assertTrue(clean.isEmpty()),
                    () -> assertTrue(clean.isArrivable()),
                    () -> assertFalse(webbed.isEmpty()),
                    () -> assertFalse(webbed.isArrivable()),
                    () -> assertFalse(trapped.isEmpty()),
                    () -> assertFalse(trapped.isArrivable()));
        }

        /**
         * A decoy or a glyph is not a veto: only webs and player traps are in C's list, and the
         * glyph's kind is not flagged as a player trap here.
         */
        @Test
        @DisplayName("a decoy does not veto emptiness")
        void decoyDoesNotVeto() {
            Square s = floorWith(true, trap(decoy, 0, 0));
            assertTrue(s.isEmpty());
        }

        /**
         * {@code square_isempty} needs an empty pile as well as an open floor, and
         * {@code square_isarrivable} does not: an object on the floor spoils the first and leaves
         * the second.
         */
        @Test
        @DisplayName("an object makes a floor non-empty but still arrivable")
        void objectMattersToEmptyOnly() {
            Square s = floorWith(false);
            s.getObjectPile().insert(new ItemObject());
            assertAll(
                    () -> assertFalse(s.isEmpty()),
                    () -> assertTrue(s.isArrivable()));
        }

        /**
         * Stairs are arrivable, as C's second accepting arm says, but not empty, since they are not
         * floor; a closed door is neither.
         */
        @Test
        @DisplayName("stairs are arrivable but not empty; a door is neither")
        void stairsAndDoors() {
            Square stairs = square(feature(TerrainFeatureFlags.TF_STAIR), false);
            Square door = closedDoorWith();
            assertAll(
                    () -> assertTrue(stairs.isArrivable()),
                    () -> assertFalse(stairs.isEmpty()),
                    () -> assertFalse(door.isArrivable()),
                    () -> assertFalse(door.isEmpty()));
        }
    }

    /**
     * {@code square_door_power}, {@code square_islockeddoor}, {@code square_isunlockeddoor} and the
     * three door-state predicates.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("doors and locks")
    class Doors {

        /**
         * A closed door with a lock of power 5 is locked and not unlocked; with power 0 the reverse.
         * The lock is found by kind, so a lock among other traps is still found.
         */
        @Test
        @DisplayName("lock power decides locked against unlocked")
        void power() {
            Square locked = closedDoorWith(trap(web, 0, 0), trap(lock, 5, 0));
            Square slack = closedDoorWith(trap(lock, 0, 0));
            assertAll(
                    () -> assertTrue(locked.isLockedDoor()),
                    () -> assertFalse(locked.isUnlockedDoor()),
                    () -> assertFalse(slack.isLockedDoor()),
                    () -> assertTrue(slack.isUnlockedDoor()));
        }

        /**
         * A closed door with no traps has no lock, so its power is zero: unlocked, not locked.
         */
        @Test
        @DisplayName("a closed door with no lock is unlocked")
        void noLock() {
            Square s = closedDoorWith();
            assertAll(
                    () -> assertFalse(s.isLockedDoor()),
                    () -> assertTrue(s.isUnlockedDoor()));
        }

        /**
         * C returns zero from {@code square_door_power} unless the grid is a closed door, so a lock
         * trap on an open door or on plain floor does not make it locked. Neither is unlocked
         * either, because {@code square_isunlockeddoor} needs the closed-door test first.
         */
        @Test
        @DisplayName("a lock on anything but a closed door counts for nothing")
        void lockOnNonDoor() {
            Square open = square(feature(TerrainFeatureFlags.TF_DOOR_ANY, TerrainFeatureFlags.TF_CLOSABLE,
                    TerrainFeatureFlags.TF_PASSABLE), true, trap(lock, 5, 0));
            Square floor = floorWith(true, trap(lock, 5, 0));
            assertAll(
                    () -> assertFalse(open.isLockedDoor()),
                    () -> assertFalse(open.isUnlockedDoor()),
                    () -> assertFalse(floor.isLockedDoor()),
                    () -> assertFalse(floor.isUnlockedDoor()));
        }

        /**
         * C's {@code square_isopendoor} is {@code TF_CLOSABLE}; {@code square_isbrokendoor} is a
         * door that is passable and not closable; a secret door is {@code TF_DOOR_ANY} with
         * {@code TF_ROCK}. The three states do not overlap.
         */
        @Test
        @DisplayName("open, broken and secret doors are told apart")
        void doorStates() {
            Square open = square(feature(TerrainFeatureFlags.TF_DOOR_ANY, TerrainFeatureFlags.TF_CLOSABLE,
                    TerrainFeatureFlags.TF_PASSABLE), false);
            Square broken = square(feature(TerrainFeatureFlags.TF_DOOR_ANY, TerrainFeatureFlags.TF_PASSABLE), false);
            Square secret = square(feature(TerrainFeatureFlags.TF_DOOR_ANY, TerrainFeatureFlags.TF_ROCK), false);
            assertAll(
                    () -> assertTrue(open.isOpenDoor()),
                    () -> assertFalse(open.isBrokenDoor()),
                    () -> assertFalse(open.isSecretDoor()),
                    () -> assertTrue(broken.isBrokenDoor()),
                    () -> assertFalse(broken.isOpenDoor()),
                    () -> assertTrue(secret.isSecretDoor()),
                    () -> assertFalse(secret.isBrokenDoor()),
                    () -> assertTrue(open.isDoor() && broken.isDoor() && secret.isDoor()));
        }
    }

    /**
     * {@code square_canputitem}, {@code square_isdamaging}, {@code square_allowsfeel},
     * {@code square_object} and the {@code null}-feature guards.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the remaining behaviour predicates")
    class Behaviour {

        /**
         * C's {@code square_canputitem}: the terrain must hold objects, the grid must not carry the
         * {@code SQUARE_TRAP} marker, and the pile must be empty.
         */
        @Test
        @DisplayName("canPutItem needs object-holding terrain, no trap marker and an empty pile")
        void canPutItem() {
            Feature holds = feature(TerrainFeatureFlags.TF_OBJECT);
            Square ok = square(holds, false);
            Square trapped = square(holds, true);
            Square full = square(holds, false);
            full.getObjectPile().insert(new ItemObject());
            Square wall = square(feature(), false);
            assertAll(
                    () -> assertTrue(ok.canPutItem()),
                    () -> assertFalse(trapped.canPutItem()),
                    () -> assertFalse(full.canPutItem()),
                    () -> assertFalse(wall.canPutItem()));
        }

        /**
         * C's {@code square_allowsfeel} is passable and not damaging; lava is {@code TF_FIERY}, so
         * a passable fiery grid is refused and a passable plain one is not.
         */
        @Test
        @DisplayName("allowsFeel is passable and not fiery")
        void allowsFeel() {
            Square plain = square(feature(TerrainFeatureFlags.TF_PASSABLE), false);
            Square lava = square(feature(TerrainFeatureFlags.TF_PASSABLE, TerrainFeatureFlags.TF_FIERY), false);
            Square wall = square(feature(), false);
            assertAll(
                    () -> assertTrue(plain.allowsFeel()),
                    () -> assertFalse(lava.allowsFeel()),
                    () -> assertTrue(lava.isDamaging()),
                    () -> assertFalse(wall.allowsFeel()));
        }

        /**
         * C's {@code square_object} returns the head of the list, and {@code pile_insert} puts the
         * newest object at the head, so the object inserted last is the top one.
         */
        @Test
        @DisplayName("the top object is the one inserted last")
        void topObject() {
            Square s = floorWith(false);
            assertNull(s.getTopObject());
            ItemObject first = new ItemObject();
            ItemObject second = new ItemObject();
            s.getObjectPile().insert(first);
            s.getObjectPile().insert(second);
            assertSame(second, s.getTopObject());
        }

        /**
         * The port's {@code null} guard, which C has no equivalent for: a square with no feature
         * answers false to the feature predicates that carry it rather than throwing.
         */
        @Test
        @DisplayName("a square with no feature answers false to the guarded predicates")
        void nullFeature() {
            Square s = new Square(null, 0, 0);
            assertAll(
                    () -> assertFalse(s.featIsPassable()),
                    () -> assertFalse(s.featIsMonsterWalkable()),
                    () -> assertFalse(s.featIsProjectable()),
                    () -> assertFalse(s.featAllowsLOS()),
                    () -> assertFalse(s.featIsWall()),
                    () -> assertFalse(s.featIsBright()),
                    () -> assertFalse(s.featIsFiery()),
                    () -> assertFalse(s.featIsNoFlow()),
                    () -> assertFalse(s.featIsNoScent()),
                    () -> assertFalse(s.allowsFeel()));
        }
    }
}
