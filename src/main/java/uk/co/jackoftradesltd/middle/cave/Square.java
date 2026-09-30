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

import org.jetbrains.annotations.*;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.SquareEnum;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.Pile;
import uk.co.jackoftradesltd.middle.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A single dungeon grid's contents: its terrain {@link Feature}, the per-grid
 * {@link SquareEnum} info flags, lighting, the occupying monster (or player), the
 * object {@link Pile} and any {@link Trap}s. The large family of {@code isXxx()}
 * predicates are convenience tests over the feature and info flags. This is the
 * Java port of the C original's {@code struct square} and the {@code square_*}
 * predicates ({@code src/cave.h} / {@code src/cave-square.c}).
 *
 * <p>The predicates fall into three groups, mirroring the section headings in
 * {@code cave-square.c}. <em>Feature</em> predicates ({@link #isFloor()}, {@link #isRock()},
 * {@link #isDoor()} and so on) read the terrain's flags. <em>Info</em> predicates
 * ({@link #isRoom()}, {@link #isVault()}, {@link #isSeen()} and so on) read the per-grid
 * {@link SquareEnum} flags. <em>Behaviour</em> predicates ({@link #isOpen()}, {@link #isEmpty()},
 * {@link #isArrivable()}) combine the other two with the occupant, the traps and the object pile.
 * Where C takes a chunk and a grid, the port is an instance method on the {@code Square} at that
 * grid, so the chunk and grid parameters disappear; {@link #isMemoryBad(Chunk, Loc)} is the one
 * predicate that still needs them, because it compares two chunks.
 *
 * <p>Where C asserts that a grid is in bounds, the port has nothing to assert: a {@code Square}
 * exists only for a valid grid, and {@code Chunk} does the bounds test before it hands one out.
 *
 * <p>Class Square coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class Square {
    /**
     * Per-grid info flags (seen, view, room, vault, generation hints, …), C's
     * {@code square->info}. The info predicates read it through {@link Flag#has}, and it is written
     * only through {@link #sqInfoOn(SquareEnum)} and {@link #sqInfoOff(SquareEnum)}.
     *
     * <p>Field info coded before 260930, commented in full on 260930.
     */
    private final Flag<SquareEnum> info;
    /**
     * The terrain feature occupying this grid, C's {@code square->feat}. C stores an index into
     * {@code f_info[]}; the port holds the {@link Feature} itself, so every feature predicate is a
     * call on it rather than a flag lookup through the index.
     *
     * <p>Field feat coded before 260930, commented in full on 260930.
     */
    private Feature feat;
    /**
     * Current light intensity of this grid (&gt;0 means lit), C's {@code square->light}. It is the
     * light actually falling on the grid, recomputed by {@code Chunk.calcLighting}, and is not the
     * same thing as the permanent {@code SQUARE_GLOW} flag. It can be negative, since darkness
     * sources subtract.
     *
     * <p>Field light coded before 260930, commented in full on 260930.
     */
    private int light;
    /**
     * Occupant index, C's {@code square->mon}: positive for a monster, negative for the player, 0 if
     * empty. Every occupancy predicate is a different comparison against this one value.
     *
     * <p>Field monsterIndex coded before 260930, commented in full on 260930.
     */
    private int monsterIndex;
    /**
     * The pile of objects lying on this grid, C's {@code square->obj}. C holds the head of a linked
     * list; the port holds a {@link Pile}, whose last element is C's head.
     *
     * <p>Field objectPile coded before 260930, commented in full on 260930.
     */
    private Pile objectPile;
    /**
     * The traps present on this grid, C's {@code square->trap} chain. C keeps the head pointer and
     * walks {@code trap->next}; the port keeps a list, and its first element is the head. C
     * currently permits only one trap per grid, but every scan here walks the whole list as the C
     * scans do.
     *
     * <p>Field traps coded before 260930, commented in full on 260930.
     */
    private List<Trap> traps;

    /**
     * Build a square with the given feature, light level and occupant, starting
     * with empty info flags, an empty object pile and no traps.
     *
     * <p>There is no single C counterpart: C squares are the zeroed entries of
     * {@code c->squares[][]}, so this constructor is where the port's per-grid defaults are set.
     *
     * <p>Function Square coded before 260930, commented in full on 260930.
     *
     * @param feature      the terrain feature
     * @param light        the initial light level
     * @param monsterIndex the occupant index (monster &gt; 0, player &lt; 0, 0 if empty)
     */
    public Square(Feature feature, int light, int monsterIndex) {
        this.feat = feature;
        this.light = light;
        this.monsterIndex = monsterIndex;

        info = new Flag<>(SquareEnum.class);
        objectPile = new Pile();
        traps = new ArrayList<>();
    }

    /**
     * Check the square info field to see if a particular flag is set on it, the port of C's
     * {@code sqinfo_has} ({@code cave.h}) applied to {@code square->info}. Every info predicate
     * ({@link #isRoom()}, {@link #isVault()} and the rest) is this test with one flag fixed.
     *
     * <p>Function hasInfoFlag coded before 260930, commented in full on 260930.
     *
     * @param squareInfo the flag we are checking for
     * @return true if the flag is set on the info field
     */
    boolean hasInfoFlag(SquareEnum squareInfo) {
        return info.has(squareInfo);
    }

    /**
     * Excise an object from a floor pile, leaving it orphaned (and hence potential bait for the garbage collector),
     * the port of C's {@code square_excise_object} ({@code cave-square.c}).
     *
     * <p>Only the pile membership changes: the object is unlinked from this grid's pile and is
     * otherwise untouched. Deleting it, and the bookkeeping that goes with that, is
     * {@code Chunk.objectDelete}'s job, as it is C's {@code object_delete}'s.
     *
     * <p>Function pileExcise coded before 260930, commented in full on 260930.
     *
     * @param item The item we are removing.
     */
    public void pileExcise(ItemObject item) {
        objectPile.excise(item);
    }

    /**
     * Gets the top most object on this square, the port of C's {@code square_object}
     * ({@code cave-square.c}), which returns the head of the grid's object list or {@code null}
     * when there is none.
     *
     * <p>The head of C's list is the last element of the port's {@link Pile}, because
     * {@link Pile#insert} pushes at the tail where C's {@code pile_insert} links in at the head.
     * C callers then walk on with {@code obj->next}; the port has no next pointer, so a caller
     * wanting the rest of the pile uses {@link #getSquarePileIterator()} or {@link #getObjectPile()}.
     *
     * <p>TODO: Deal with returning the next object from the square as at present this is impossible
     *
     * <p>Function getTopObject coded before 260930, commented in full on 260930.
     *
     * @return the top most object on this square, or {@code null} if the pile is empty
     */
    @CheckReturnValue
    @Contract(pure = true)
    public @Nullable ItemObject getTopObject() {
        if (objectPile.isEmpty()) return null;
        return objectPile.peekLastItem();
    }

    /**
     * Gets the top most trap on a square, the port of C's {@code square_trap}
     * ({@code cave-square.c}), which returns {@code square->trap}, the head of the trap chain, or
     * {@code null} for an empty grid.
     *
     * <p>C's {@code square_trap} also returns {@code null} for an out-of-bounds grid; here the
     * bounds test belongs to {@code Chunk}. Can have multiple traps on a square, but currently not
     * allowed in C code. TODO check if this needs to be kept in
     *
     * <p>Function getTrap coded before 260930, commented in full on 260930.
     *
     * @return the top most trap on a square, or {@code null} if there is none
     */
    @CheckReturnValue
    @Contract(pure = true)
    public @Nullable Trap getTrap() {
        if (traps.isEmpty()) return null;
        return traps.getFirst();
    }

    /**
     * Get the current light status of this square, the port of C's {@code square_light}
     * ({@code cave-square.c}), which returns {@code square->light}.
     *
     * <p>Function getLight coded before 260930, commented in full on 260930.
     *
     * @return the current light status of this square: zero is dark, positive is lit
     */
    public int getLight() {
        return light;
    }

    /**
     * Checks whether a given object is in this square's pile, the port of C's
     * {@code square_holds_object} ({@code cave-square.c}).
     *
     * <p>Identity, not equality of kind: the question is whether <em>this</em> object is here, not
     * whether something like it is. Two Flasks of Oil on the floor are distinct objects and the test
     * distinguishes them, which is what makes it usable as a location check — C uses it to decide
     * whether a known object is still attached to the pile it thinks it is on, and
     * {@code PlayerKnowledge.knowObject} uses it to tell an object under the
     * player's feet from one elsewhere on the level.
     *
     * <p>Function holdsObject coded on 260816, commented in full on 260816.
     *
     * @param object the object we are looking for
     * @return {@code true} if the object is in this square's pile
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean holdsObject(@NotNull ItemObject object) {
        return objectPile.contains(object);
    }

    /**
     * Checks to see the current lighting level of this square, the port of C's
     * {@code square_islit} ({@code cave-square.c}). This is the light actually falling on the grid
     * from every source — glow, the player's light, monster light — as recomputed by
     * {@code calcLighting}, not the {@code SQUARE_GLOW} flag tested by {@link #isGlow()}.
     *
     * <p>Function isLit coded before 260930, commented in full on 260930.
     *
     * @return true if the square's light level is above zero
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isLit() {
        return light > 0;
    }

    /**
     * Test for normal open floor, the port of C's {@code square_isfloor} ({@code cave-square.c}),
     * which is {@code feat_is_floor} on the grid's terrain: the {@code TF_FLOOR} flag.
     *
     * <p>Only the terrain is consulted. A floor grid with a monster, an object or a trap on it is
     * still a floor; {@link #isOpen()} and {@link #isEmpty()} are the predicates that look at what
     * is standing on it.
     *
     * <p>Function isFloor coded before 260930, commented in full on 260930.
     *
     * @return true if the square is normal open floor
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isFloor() {
        return feat.isFloor();
    }

    /**
     * Tests for the ability to hold a trap, the port of C's {@code square_istrappable}
     * ({@code cave-square.c}): {@code feat_is_trap_holding}, the {@code TF_TRAP} flag.
     *
     * <p>This is a property of the terrain, not of the grid's current contents; a grid that already
     * carries a trap is still trappable.
     *
     * <p>Function isTrappable coded before 260930, commented in full on 260930.
     *
     * @return true if the square can hold a trap
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isTrappable() {
        return feat.isTrapHolding();
    }

    /**
     * Tests for whether the square can hold an object, the port of C's
     * {@code square_isobjectholding} ({@code cave-square.c}): {@code feat_is_object_holding}, the
     * {@code TF_OBJECT} flag.
     *
     * <p>Like {@link #isTrappable()} this is about the terrain, not the pile: it says an object
     * may lie here, not that none does. {@link #canPutItem()} adds those conditions.
     *
     * <p>Function isObjectHolding coded before 260930, commented in full on 260930.
     *
     * @return true if the square can hold an object
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isObjectHolding() {
        return feat.isObjectHolding();
    }

    /**
     * Check to see if the square is a granite wall, the port of C's {@code square_isrock}
     * ({@code cave-square.c}): {@code TF_GRANITE} and not {@code TF_DOOR_ANY}.
     *
     * <p>The door exclusion is the point. A secret door is built from granite so that it looks like
     * a wall, and this predicate says <em>no</em> for it even though it behaves as rock. Use
     * {@link #featSeemsLikeWall()} for the question of how the grid behaves, and
     * {@link #isGranite()} for the bare flag with no door exclusion.
     *
     * <p>Function isRock coded before 260930, commented in full on 260930.
     *
     * @return true if the square is a granite wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isRock() {
        return feat.isGranite() && !feat.hasAnyDoor();
    }

    /**
     * Tests whether the square seems like a wall or not, the port of C's
     * {@code square_seemslikewall} ({@code cave-square.c}): the {@code TF_ROCK} flag.
     *
     * <p>{@code TF_ROCK} is set on walls, rubble and secret doors alike, so this is true for a
     * grid the player cannot yet tell from a wall. It is not {@code TF_WALL}; see
     * {@link #isRubble()}, which needs the two apart.
     *
     * <p>Function featSeemsLikeWall coded before 260930, commented in full on 260930.
     *
     * @return true if this square seems like a wall to the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featSeemsLikeWall() {
        return feat.fullRock();
    }

    /**
     * Tests for whether we have an interesting feat or not, the port of C's
     * {@code square_isinteresting} ({@code cave-square.c}): the {@code TF_INTERESTING} flag. C's
     * pathfinding ({@code player-path.c}) and targeting ({@code target.c}, {@code ui-target.c})
     * use it to decide which grids are worth noticing.
     *
     * <p>Function featIsIntersting coded before 260930, commented in full on 260930.
     *
     * @return true if the feat is interesting
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsIntersting() {
        return feat.isInteresting();
    }

    /**
     * Tests to see if this is granite, the port of C's {@code square_isgranite}
     * ({@code cave-square.c}): {@code feat_is_granite}, the bare {@code TF_GRANITE} flag.
     *
     * <p>Unlike {@link #isRock()} a secret door answers true here, as it is made of granite.
     *
     * <p>Function isGranite coded before 260930, commented in full on 260930.
     *
     * @return true if the square is granite
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isGranite() {
        return feat.isGranite();
    }

    /**
     * Test to see if the feature is a permanent wall, the port of C's {@code square_isperm}
     * ({@code cave-square.c}): {@code TF_PERMANENT} and {@code TF_ROCK}.
     *
     * <p>Both flags are needed. Permanent terrain that is not rock, such as a shop entrance or a
     * staircase, is not a permanent <em>wall</em>. {@code Feature.isFullPermanent} already tests
     * both flags, so the trailing {@code fullRock()} here repeats one of them; that is harmless and
     * matches C's two-flag test.
     *
     * <p>Function isPerm coded before 260930, commented in full on 260930.
     *
     * @return true for a permanent wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isPerm() {
        return feat.isFullPermanent() && feat.fullRock();
    }

    /**
     * Checks to see if there is an artefact on this square.
     *
     * <p>There is no single C function for this. It is the loop in {@code square_changeable}
     * ({@code cave-square.c}), which walks {@code square_object(c, grid)} by {@code obj->next}
     * refusing the grid if any {@code obj->artifact} is set, lifted into a predicate on the pile.
     *
     * <p>Function hasObjectArtifact coded before 260930, commented in full on 260930.
     *
     * @return true if this square contains an artefact
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean hasObjectArtifact() {
        return objectPile.hasArtifact();
    }

    /**
     * Test for magma (Stef beware!), the port of C's {@code square_ismagma}
     * ({@code cave-square.c}): {@code feat_is_magma}, the {@code TF_MAGMA} flag. Treasure-bearing
     * veins are still magma; see {@link #hasGoldVein()} for that.
     *
     * <p>Function isMagma coded before 260930, commented in full on 260930.
     *
     * @return true if the feature is magma
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMagma() {
        return feat.isMagma();
    }

    /**
     * Tests for Quartz, the port of C's {@code square_isquartz} ({@code cave-square.c}):
     * {@code feat_is_quartz}, the {@code TF_QUARTZ} flag.
     *
     * <p>Function isQuartz coded before 260930, commented in full on 260930.
     *
     * @return true if this square is quartz
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isQuartz() {
        return feat.isQuartz();
    }

    /**
     * Tests for minerals, the port of C's {@code square_ismineral} ({@code cave-square.c}):
     * {@code square_isrock || square_ismagma || square_isquartz}.
     *
     * <p>The rock arm is {@link #isRock()}, granite <em>excluding</em> doors, so a secret door is
     * not mineral even though it looks like granite. Rubble and permanent walls are not mineral
     * either, which is what {@code square_isdiggable} and {@code square_isstrongwall} rely on when
     * they add those cases separately.
     *
     * <p>Function isMineral coded before 260930, commented in full on 260930.
     *
     * @return true if this square is rock, quartz or magma
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMineral() {
        return feat.isRock() || feat.isQuartz() || feat.isMagma();
    }

    /**
     * Tests for gold veins, the port of C's {@code square_hasgoldvein} ({@code cave-square.c}):
     * the {@code TF_GOLD} flag, which marks both magma and quartz with treasure.
     *
     * <p>Function hasGoldVein coded before 260930, commented in full on 260930.
     *
     * @return true if there is a gold vein here
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean hasGoldVein() {
        return feat.isTreasure();
    }

    /**
     * Tests for rubble, defined as rock which isn't in a wall, the port of C's
     * {@code square_isrubble} ({@code cave-square.c}): {@code TF_ROCK} without {@code TF_WALL}.
     *
     * <p>The {@code TF_ROCK} half is the full-rock test, not the granite test, which is why
     * {@code Feature.fullRock} exists: a granite-only test would find no rubble at all.
     *
     * <p>Function isRubble coded before 260930, commented in full on 260930.
     *
     * @return true if this square has rubble in it
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isRubble() {
        return !feat.isWall() && feat.fullRock();
    }

    /**
     * Get an iterator through the pile, the port of the {@code for (obj = square_object(c, grid);
     * obj; obj = obj->next)} walk that C callers write by hand ({@code cave-square.c}, for example
     * in {@code square_changeable}).
     *
     * <p>The iterator runs in the pile's own order, which is the reverse of C's head-first list
     * order; a caller that depends on the order should check.
     *
     * <p>Function getSquarePileIterator coded before 260930, commented in full on 260930.
     *
     * @return an Iterator&lt;ItemObject&gt; for the pile of objects on this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    Iterator<ItemObject> getSquarePileIterator() {
        return objectPile.getIterator();
    }

    /**
     * Tests for secret doors, the port of C's {@code square_issecretdoor}
     * ({@code cave-square.c}): {@code TF_DOOR_ANY} and {@code TF_ROCK}.
     *
     * <p>These appear as if they were granite, when detected they are replaced by a closed door.
     * A visible door has {@code TF_DOOR_ANY} without {@code TF_ROCK}, which is what separates the
     * two.
     *
     * <p>Function isSecretDoor coded before 260930, commented in full on 260930.
     *
     * @return true if this square contains a secret door
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isSecretDoor() {
        return feat.hasAnyDoor() && feat.fullRock();
    }

    /**
     * Tests for open doors, the port of C's {@code square_isopendoor} ({@code cave-square.c}).
     * C tests {@code TF_CLOSABLE}: a door that can still be closed is, by definition, open. A
     * broken door is passable but not closable, so it is not an open door.
     *
     * <p>Function isOpenDoor coded before 260930, commented in full on 260930.
     *
     * @return true if a door is open here
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isOpenDoor() {
        return feat.isOpenDoor();
    }

    /**
     * Test to see if this is a closed door (locked/jammed are also closed), the port of C's
     * {@code square_iscloseddoor} ({@code cave-square.c}): the {@code TF_DOOR_CLOSED} flag.
     *
     * <p>Function isClosedDoor coded before 260930, commented in full on 260930.
     *
     * @return true for a closed door
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isClosedDoor() {
        return feat.isClosedDoor();
    }

    /**
     * Tests for a broken door, the port of C's {@code square_isbrokendoor}
     * ({@code cave-square.c}): {@code TF_DOOR_ANY} and {@code TF_PASSABLE} but not
     * {@code TF_CLOSABLE}.
     *
     * <p>Function isBrokenDoor coded before 260930, commented in full on 260930.
     *
     * @return true if this door is broken
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isBrokenDoor() {
        return feat.hasAnyDoor() && feat.isPassable() && !feat.isCloseable();
    }

    /**
     * Test to see if this square is a locked door, the port of C's {@code square_islockeddoor}
     * ({@code cave-square.c}): the lock's power is greater than zero.
     *
     * <p>{@link #squareDoorPower()} answers zero for anything that is not a closed door, so an
     * open door, or a floor grid carrying a stray lock trap, is never locked.
     *
     * <p>Function isLockedDoor coded before 260930, commented in full on 260930.
     *
     * @return true if this square contains a door of power greater than 0
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isLockedDoor() {
        return squareDoorPower() > 0;
    }

    /**
     * Test to see if this square is an unlocked door, the port of C's
     * {@code square_isunlockeddoor} ({@code cave-square.c}): a closed door whose lock power is
     * zero.
     *
     * <p>The closed-door test is needed here because {@link #squareDoorPower()} is zero for every
     * grid that is not a door, so the power alone would call a floor an unlocked door.
     *
     * <p>Function isUnlockedDoor coded before 260930, commented in full on 260930.
     *
     * @return true if this square contains a closed door of power 0
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isUnlockedDoor() {
        return isClosedDoor() && squareDoorPower() == 0;
    }

    /**
     * The current power of the lock on the door of this square, the port of C's
     * {@code square_door_power} ({@code trap.c}).
     *
     * <p>Zero unless the grid is a closed door carrying a "door lock" trap, in which case it is
     * that trap's power. The checks run in C's order, with one addition: the {@link #isTrap()}
     * test comes before the registry lookup so a grid with no traps never reaches
     * {@code TerrainRegistry}. {@link #trapSpecific(TrapKind)} repeats that test, which is
     * harmless. The scan matches the lock by {@link TrapKind} identity, as C compares
     * {@code trap->kind == lock}.
     *
     * <p>Function squareDoorPower coded before 260930, commented in full on 260930.
     *
     * @return the current door lock power
     */
    @CheckReturnValue
    @Contract(pure = true)
    private int squareDoorPower() {
        if (!isClosedDoor()) return 0;

        // Confirm there is a trap before actually looking it up in the registry
        if (!isTrap()) return 0;
        TrapKind lock = TrapKind.lookupTrap("door lock");

        if (!trapSpecific(lock)) return 0;

        for (Trap trap : traps) {
            if (trap.getKind() == lock) {
                return trap.getPower();
            }
        }

        return 0;
    }

    /**
     * Tests for any door including open, closed, and hidden, the port of C's
     * {@code square_isdoor} ({@code cave-square.c}): the {@code TF_DOOR_ANY} flag.
     *
     * <p>Function isDoor coded before 260930, commented in full on 260930.
     *
     * @return true for any door
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isDoor() {
        return feat.hasAnyDoor();
    }

    /**
     * Tests for any type of staircase, the port of C's {@code square_isstairs}
     * ({@code cave-square.c}): the {@code TF_STAIR} flag.
     *
     * <p>Function isStairs coded before 260930, commented in full on 260930.
     *
     * @return true for any type of staircase
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isStairs() {
        return feat.isStair();
    }

    /**
     * Tests for an upward staircase, the port of C's {@code square_isupstairs}
     * ({@code cave-square.c}): the {@code TF_UPSTAIR} flag.
     *
     * <p>Function isUpStairs coded before 260930, commented in full on 260930.
     *
     * @return true for an up staircase
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isUpStairs() {
        return feat.isUpStair();
    }

    /**
     * Tests for the presence of a downward going staircase, the port of C's
     * {@code square_isdownstairs} ({@code cave-square.c}): the {@code TF_DOWNSTAIR} flag.
     *
     * <p>Function isDownStairs coded before 260930, commented in full on 260930.
     *
     * @return true for downstairs
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isDownStairs() {
        return feat.isDownStair();
    }

    /**
     * Test for shop entrance, the port of C's {@code square_isshop} ({@code cave-square.c}):
     * {@code feat_is_shop}, the {@code TF_SHOP} flag.
     *
     * <p>Function isShop coded before 260930, commented in full on 260930.
     *
     * @return true if this is a shop entrance
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isShop() {
        return feat.isShop();
    }

    /**
     * Test for the location of the player, the port of C's {@code square_isplayer}
     * ({@code cave-square.c}): {@code square->mon < 0}.
     *
     * <p>C stores the player as a negative occupant index, so any negative value counts, not just
     * {@code -1}. Zero and every positive (monster) index answer false.
     *
     * <p>Function isPlayer coded before 260930, commented in full on 260930.
     *
     * @return true if the player is here
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isPlayer() {
        return monsterIndex < 0;
    }

    /**
     * Tests if a mob or the player is in this square, the port of C's {@code square_isoccupied}
     * ({@code cave-square.c}): {@code square->mon != 0}.
     *
     * <p>Function isOccupied coded before 260930, commented in full on 260930.
     *
     * @return true if the square contains either a mob or the player
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isOccupied() {
        return monsterIndex != 0;
    }

    /**
     * Tests to see if a square is free of any occupant. C has no function of this name; it is the
     * {@code !square(c, grid)->mon} half of {@code square_isopen} ({@code cave-square.c}) given a
     * name, and is the exact negation of {@link #isOccupied()}.
     *
     * <p>Function isFree coded before 260930, commented in full on 260930.
     *
     * @return true if the square doesn't contain a monster or the player
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isFree() {
        return monsterIndex == 0;
    }
//
//    /**
//     * Tests if this square is known by the player
//     *
//     * @param c    The chunk we are examining, should be the owning chunk of this square
//     * @param grid The location in that chunk of this square
//     * @return True if the information known about this square is also known by the player
//     */
//    @Contract(pure = true)
//    @CheckReturnValue
//    public boolean isKnown(Chunk c, Loc grid) {
//        Chunk mainCave = GameConstants.cave;
//        Player mainPlayer = GameConstants.mainPlayer;
//        if (!c.equals(mainCave) && (!c.equals(mainPlayer.getCave())))
//            return false;
//
//        if (mainPlayer.getCave() == null)
//            return false;
//
//        return !mainPlayer.getCave().getSquare(grid).feat.isNoFeat();
//    }

    /**
     * Tests to see if the player's memory of this square has failed, the port of C's
     * {@code square_ismemorybad} ({@code cave-square.c}): the grid is unknown to the player, or the
     * feature the player remembers differs from the real one.
     *
     * <p>Neither answer reads {@code this}: like C, it looks the grid up in the chunk it is given,
     * in the player's remembered cave and in the live cave, so it can be called on any
     * {@code Square}. The known test is {@code Chunk.isKnown}, which is where C's
     * {@code square_isknown} lives, and it runs first so that a missing player cave is never
     * dereferenced. The features are compared with {@link Feature#equals}, which stands in for C's
     * comparison of two feature indexes.
     *
     * <p>Function isMemoryBad coded before 260930, commented in full on 260930.
     *
     * @param c    The chunk we are examining
     * @param grid the grid in that chunk which points to this square in the other grids
     * @return true if the grid is unknown, or the features of the player's chunk square and the live square differ
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMemoryBad(Chunk c, Loc grid) {
        Chunk cave = GameState.getCave();
        Player mainPlayer = GameState.getPlayer();

        return !c.isKnown(grid) || !(mainPlayer.getCave().getSquare(grid).feat.equals(cave.getSquare(grid).feat));
    }

    /*
     * Square predicates
     */

    /**
     * Tests to see if this square is marked, the port of C's {@code square_ismark}
     * ({@code cave-square.c}): the {@code SQUARE_MARK} info flag.
     *
     * <p>Function isMark coded before 260930, commented in full on 260930.
     *
     * @return true if this square is marked
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMark() {
        return info.has(SquareEnum.SQUARE_MARK);
    }

    /**
     * Tests for the permanent glow flag, the port of C's {@code square_isglow}
     * ({@code cave-square.c}). This is the terrain's own illumination — a lit room, a daylit
     * surface grid — and is independent of the transient light level tested by {@link #isLit()}.
     *
     * <p>Function isGlow coded before 260930, commented in full on 260930.
     *
     * @return true if the square carries {@code SQUARE_GLOW}
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isGlow() {
        return info.has(SquareEnum.SQUARE_GLOW);
    }

    /**
     * Tests to see if this room is part of a vault, not the role it plays in that vault, the port
     * of C's {@code square_isvault} ({@code cave-square.c}): the {@code SQUARE_VAULT} info flag.
     *
     * <p>Function isVault coded before 260930, commented in full on 260930.
     *
     * @return true if the square is part of a vault
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isVault() {
        return info.has(SquareEnum.SQUARE_VAULT);
    }

    /**
     * Tests to see if this is part of a room, the port of C's {@code square_isroom}
     * ({@code cave-square.c}): the {@code SQUARE_ROOM} info flag.
     *
     * <p>Function isRoom coded before 260930, commented in full on 260930.
     *
     * @return true if it is part of a room
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isRoom() {
        return info.has(SquareEnum.SQUARE_ROOM);
    }

    /**
     * Tests whether the player has seen this square, the port of C's {@code square_isseen}
     * ({@code cave-square.c}): the {@code SQUARE_SEEN} info flag. Not to be confused with
     * {@link #isView()}, which is about what can be seen right now.
     *
     * <p>Function isSeen coded before 260930, commented in full on 260930.
     *
     * @return true if the player has seen this square
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isSeen() {
        return info.has(SquareEnum.SQUARE_SEEN);
    }

    /**
     * Tests to see whether the player can currently see this square, the port of C's
     * {@code square_isview} ({@code cave-square.c}): the {@code SQUARE_VIEW} info flag, rebuilt
     * whenever the view is recalculated.
     *
     * <p>Function isView coded before 260930, commented in full on 260930.
     *
     * @return true if this square is in view
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isView() {
        return info.has(SquareEnum.SQUARE_VIEW);
    }

    /**
     * Tests if this square was seen before the current update, the port of C's
     * {@code square_wasseen} ({@code cave-square.c}): the {@code SQUARE_WASSEEN} info flag, set by
     * {@code Chunk.markWasSeen} so the view update can tell what has changed.
     *
     * <p>Function wasSeen coded before 260930, commented in full on 260930.
     *
     * @return true if the square was seen
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean wasSeen() {
        return info.has(SquareEnum.SQUARE_WASSEEN);
    }

    /**
     * Tests if this square triggers a feeling, the port of C's {@code square_isfeel}
     * ({@code cave-square.c}): the {@code SQUARE_FEEL} info flag.
     *
     * <p>Function isFeel coded before 260930, commented in full on 260930.
     *
     * @return true if this square triggers a feeling
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isFeel() {
        return info.has(SquareEnum.SQUARE_FEEL);
    }

    /**
     * Tests if this square has a known trap, the port of C's {@code square_istrap}
     * ({@code cave-square.c}): the {@code SQUARE_TRAP} info flag.
     *
     * <p>It is a marker flag, not a search of the trap list, and every trap scan in this class
     * tests it first, as the C scans do. A grid whose {@link #getTraps()} list is non-empty but
     * whose flag is off answers false to every trap predicate.
     *
     * <p>Function isTrap coded before 260930, commented in full on 260930.
     *
     * @return true if this square has a known trap
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isTrap() {
        return info.has(SquareEnum.SQUARE_TRAP);
    }

    /**
     * Get all the traps associated with this square, C's {@code square->trap} chain
     * ({@code square_trap} returns its head; {@code trap->next} links the rest).
     *
     * <p>Live, not a copy, in the same way as {@link #getObjectPile()}; callers add and remove
     * traps through it. Nothing keeps the {@code SQUARE_TRAP} flag in step with the list.
     *
     * <p>Function getTraps coded before 260930, commented in full on 260930.
     *
     * @return the traps on this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public List<Trap> getTraps() {
        return traps;
    }

    /**
     * Tests to see if this square has an unknown trap, the port of C's {@code square_isinvis}
     * ({@code cave-square.c}): the {@code SQUARE_INVIS} info flag.
     *
     * <p>Function isInvis coded before 260930, commented in full on 260930.
     *
     * @return true if this square has an unknown trap
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isInvis() {
        return info.has(SquareEnum.SQUARE_INVIS);
    }

    /**
     * Tests to see if this square in an inner wall (generation), the port of C's
     * {@code square_iswall_inner} ({@code cave-square.c}): the {@code SQUARE_WALL_INNER} info flag.
     *
     * <p>Function isWallInner coded before 260930, commented in full on 260930.
     *
     * @return true if this square is an inner wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isWallInner() {
        return info.has(SquareEnum.SQUARE_WALL_INNER);
    }

    /**
     * Tests to see if this square is an outer wall (generation), the port of C's
     * {@code square_iswall_outer} ({@code cave-square.c}): the {@code SQUARE_WALL_OUTER} info flag.
     *
     * <p>Function isWallOuter coded before 260930, commented in full on 260930.
     *
     * @return true if this square is an outer wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isWallOuter() {
        return info.has(SquareEnum.SQUARE_WALL_OUTER);
    }

    /**
     * Tests to see if this square is a solid wall (generation), the port of C's
     * {@code square_iswall_solid} ({@code cave-square.c}): the {@code SQUARE_WALL_SOLID} info flag.
     *
     * <p>Function isWallSolid coded before 260930, commented in full on 260930.
     *
     * @return true if this square is a solid wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isWallSolid() {
        return info.has(SquareEnum.SQUARE_WALL_SOLID);
    }

    /**
     * Tests to see if there are monster restrictions on this square (generation), the port of C's
     * {@code square_ismon_restrict} ({@code cave-square.c}): the {@code SQUARE_MON_RESTRICT} info
     * flag.
     *
     * <p>Function isMonRestrict coded before 260930, commented in full on 260930.
     *
     * @return true for monster restrictions on this square
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMonRestrict() {
        return info.has(SquareEnum.SQUARE_MON_RESTRICT);
    }

    /**
     * Tests to see if the square cannot be teleported FROM by the player, the port of C's
     * {@code square_isno_teleport} ({@code cave-square.c}): the {@code SQUARE_NO_TELEPORT} info
     * flag.
     *
     * <p>Function isNoTeleport coded before 260930, commented in full on 260930.
     *
     * @return true if the player cannot teleport from this square
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isNoTeleport() {
        return info.has(SquareEnum.SQUARE_NO_TELEPORT);
    }

    /**
     * Tests if this square cannot be magically mapped by the player, the port of C's
     * {@code square_isno_map} ({@code cave-square.c}): the {@code SQUARE_NO_MAP} info flag.
     *
     * <p>Function isNoMap coded before 260930, commented in full on 260930.
     *
     * @return true if this square CANNOT be magically mapped
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isNoMap() {
        return info.has(SquareEnum.SQUARE_NO_MAP);
    }

    /**
     * Tests if the square can't be detected by player ESP, the port of C's
     * {@code square_isno_esp} ({@code cave-square.c}): the {@code SQUARE_NO_ESP} info flag.
     *
     * <p>Function isNoEsp coded before 260930, commented in full on 260930.
     *
     * @return true if the player cannot detect this square by ESP
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isNoEsp() {
        return info.has(SquareEnum.SQUARE_NO_ESP);
    }

    /**
     * Tests to see if this square is marked for projection processing, the port of C's
     * {@code square_isproject} ({@code cave-square.c}): the {@code SQUARE_PROJECT} info flag. It
     * is a per-grid marker and is unrelated to the terrain's own {@code TF_PROJECT} flag tested by
     * {@link #featIsProjectable()}.
     *
     * <p>Function isProject coded before 260930, commented in full on 260930.
     *
     * @return true if this square is marked for projection processing
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isProject() {
        return info.has(SquareEnum.SQUARE_PROJECT);
    }

    /**
     * Tests to see if this square has been detected for traps, the port of C's
     * {@code square_isdtrap} ({@code cave-square.c}): the {@code SQUARE_DTRAP} info flag.
     *
     * <p>Function isDTrap coded before 260930, commented in full on 260930.
     *
     * @return true if the player has detected for traps here
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isDTrap() {
        return info.has(SquareEnum.SQUARE_DTRAP);
    }

    /**
     * Tests to see if the square is inappropriate to place stairs, the port of C's
     * {@code square_isno_stairs} ({@code cave-square.c}): the {@code SQUARE_NO_STAIRS} info flag.
     *
     * <p>Function isNoStairs coded before 260930, commented in full on 260930.
     *
     * @return true if this square is inappropriate to place stairs
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isNoStairs() {
        return info.has(SquareEnum.SQUARE_NO_STAIRS);
    }

    /**
     * Check for the location of a player trap on this square, the port of C's
     * {@code square_isplayertrap} ({@code cave-square.c}): {@code square_trap_flag} with
     * {@code TRF_TRAP}, known or unknown.
     *
     * <p>Function isPlayerTrap coded before 260930, commented in full on 260930.
     *
     * @return true if this square contains a player trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isPlayerTrap() {
        return trapFlag(TrapEnum.TRF_TRAP);
    }

    /**
     * Check whether this square has a web trap on it, the port of C's {@code square_iswebbed}
     * ({@code cave-square.c}): {@code square_trap_specific} for the kind {@code lookup_trap("web")}.
     *
     * <p>The {@link #isTrap()} test runs before the registry lookup, so a trapless grid never
     * touches {@code TerrainRegistry}; C looks the kind up first and cannot afford that test.
     *
     * <p>Function isWebbed coded before 260930, commented in full on 260930.
     *
     * @return true if this square has a web trap on it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isWebbed() {
        if (!isTrap()) return false;
        TrapKind webTrap = TrapKind.lookupTrap("web");
        return trapSpecific(webTrap);
    }

    /**
     * Checks for a decoy trap, the port of C's {@code square_isdecoyed} ({@code cave-square.c}):
     * {@code square_trap_specific} for the kind {@code lookup_trap("decoy")}.
     *
     * <p>Unlike {@link #isWebbed()} this looks the kind up before testing the grid, as C does, so
     * it needs the trap registry loaded even for a trapless square.
     *
     * <p>Function isDecoyed coded before 260930, commented in full on 260930.
     *
     * @return true if this square has a decoy trap on it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isDecoyed() {
        TrapKind decoyedTrap = TrapKind.lookupTrap("decoy");
        return trapSpecific(decoyedTrap);
    }

    /**
     * Checks for a warded trap, the port of C's {@code square_iswarded} ({@code cave-square.c}):
     * {@code square_trap_specific} for the kind {@code lookup_trap("glyph of warding")}.
     *
     * <p>As with {@link #isDecoyed()}, the kind is looked up first, so the trap registry must be
     * loaded.
     *
     * <p>Function isWarded coded before 260930, commented in full on 260930.
     *
     * @return true if this square has a warded trap on it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isWarded() {
        TrapKind wardedTrap = TrapKind.lookupTrap("glyph of warding");
        return trapSpecific(wardedTrap);
    }

    /**
     * Check for a specific kind of trap on a square, the port of C's {@code square_trap_specific}
     * ({@code trap.c}).
     *
     * <p>The kinds are compared by their index in the trap-kind table, C's {@code t_idx}, which is
     * {@link TrapKind#getTrapKindIndex()}. Comparing descriptions is not equivalent, because
     * several kinds in {@code trap.txt} share one: three of the dart traps read "A trap which
     * shoots damaging darts." The {@link #isTrap()} marker is tested first, and the whole trap
     * list is scanned rather than only the first entry.
     *
     * <p>Function trapSpecific coded before 260930, commented in full on 260930, updated on 260930
     * when the match moved from the description text to the kind index.
     *
     * @param kind the kind of trap we are checking for
     * @return true if one of the traps on this square is the same kind of trap as the incoming kind
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean trapSpecific(TrapKind kind) {
        if (!isTrap()) return false;

        for (Trap trap : traps) {
            if (trap.getKind().getTrapKindIndex() == kind.getTrapKindIndex()) return true;
        }

        return false;
    }

    /**
     * Checks if there is a visible trap on this square, the port of C's
     * {@code square_isvisibletrap} ({@code cave-square.c}): {@code square_trap_flag} with
     * {@code TRF_VISIBLE}.
     *
     * <p>Function isVisibleTrap coded before 260930, commented in full on 260930.
     *
     * @return true for the existance of visible traps
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isVisibleTrap() {
        return trapFlag(TrapEnum.TRF_VISIBLE);
    }

    /**
     * Check for the existance of a trap with a given flag on this square, the port of C's
     * {@code square_trap_flag} ({@code trap.c}). The {@link #isTrap()} marker is tested first, then
     * every trap on the grid is scanned and the answer is true if any carries the flag.
     *
     * <p>Function trapFlag coded before 260930, commented in full on 260930.
     *
     * @param trapFlag the flag to check for
     * @return if there is a trap on this square with the given flag set
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean trapFlag(TrapEnum trapFlag) {
        if (!isTrap())
            return false;

        for (Trap trap : traps) {
            if (trap.hasTrap(trapFlag)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the remaining time for a trap of a given kind to be disabled, the port of C's
     * {@code square_trap_timeout} ({@code trap.c}). Note, the first matching trap on the square
     * with a non-zero timeout is used.
     *
     * <p>The argument is a trap-kind index, C's {@code t_idx}, matched against
     * {@link TrapKind#getTrapKindIndex()}. A negative value means "any kind", which is how
     * {@code square_isdisabledtrap} calls it with {@code -1}. Traps of the wrong kind are skipped,
     * a trap whose timeout is zero is skipped rather than ending the scan, and zero is returned if
     * no trap qualifies. Unlike {@link #trapFlag(TrapEnum)} it does not test the
     * {@link #isTrap()} marker first, as in C.
     *
     * <p>Function trapTimeout coded before 260930, commented in full on 260930, updated on 260930
     * when the match moved from the trap's list position to the kind index.
     *
     * @param trapIndex the index of the trap kind, or a negative number to accept any kind
     * @return the number of turns until this trap disarms
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int trapTimeout(int trapIndex) {
        for (Trap trap : traps) {
            if (trapIndex >= 0 && trapIndex != trap.getKind().getTrapKindIndex())
                continue;

            if (trap.getTimeout() != 0)
                return trap.getTimeout();
        }

        return 0;
    }

    /**
     * Checks to see if this square is open, a floor square not occupied by a monster, the port of
     * C's {@code square_isopen} ({@code cave-square.c}): {@link #isFloor()} and no occupant.
     *
     * <p>The player counts as an occupant, since C tests {@code !square->mon} and the player is a
     * negative index. Objects and traps do not matter here; see {@link #isEmpty()}.
     *
     * <p>Function isOpen coded before 260930, commented in full on 260930.
     *
     * @return true for an empty square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isOpen() {
        return isFloor() && isFree();
    }

    /**
     * Tests to see if this square is empty, (an open square without any items), the port of C's
     * {@code square_isempty} ({@code cave-square.c}).
     *
     * <p>The checks run in C's order: a player trap or a web vetoes first, then the grid must be
     * {@link #isOpen()} with an empty object pile.
     *
     * <p>Function isEmpty coded before 260930, commented in full on 260930.
     *
     * @return true if the square doesn't contain any items and is open
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isEmpty() {
        if (isPlayerTrap() || isWebbed()) return false;
        return isOpen() && (objectPile.isEmpty());
    }

    /**
     * Check to see if a monster or the player could be placed on this square, the port of C's
     * {@code square_isarrivable} ({@code cave-square.c}).
     *
     * <p>Any occupant, player trap or web refuses the grid; otherwise floor and stairs are
     * accepted and everything else, doors included, is not. C carries a comment wondering about
     * open doors, and the answer is left as it is. Unlike {@link #isEmpty()} this ignores the
     * object pile.
     *
     * <p>Function isArrivable coded before 260930, commented in full on 260930.
     *
     * @return true if this square can be arrived at
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isArrivable() {
        if (isOccupied() || isPlayerTrap() || isWebbed()) return false;
        if (isFloor() || isStairs()) return true;
        return false;
    }

    /**
     * Checks to see if this square is monster walkable, the port of C's
     * {@code square_is_monster_walkable} ({@code cave-square.c}): {@code feat_is_monster_walkable},
     * which tests {@code TF_PASSABLE}, the same flag as {@link #featIsPassable()}.
     *
     * <p>C uses it for polymorphing, when a monster may be standing on terrain that is not an empty
     * space. The {@code null} guard is the port's own: it makes a feature-less square answer false
     * rather than throw.
     *
     * <p>Function featIsMonsterWalkable coded before 260930, commented in full on 260930.
     *
     * @return true if a monster can walk through this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsMonsterWalkable() {
        return feat != null && feat.isMonsterWalkable();
    }

    /**
     * Checks to see if the player can walk through this square, the port of C's
     * {@code square_ispassable} ({@code cave-square.c}): {@code feat_is_passable}, the
     * {@code TF_PASSABLE} flag. The {@code null} guard is the port's own.
     *
     * <p>Function featIsPassable coded before 260930, commented in full on 260930.
     *
     * @return true if the square is passable by the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsPassable() {
        return feat != null && feat.isPassable();
    }

    /**
     * Checks to see if a projectile can pass through this square, the port of C's
     * {@code square_isprojectable} ({@code cave-square.c}): {@code feat_is_projectable}, the
     * {@code TF_PROJECT} flag.
     *
     * <p>C also answers false for an out-of-bounds grid; here that test belongs to {@code Chunk},
     * and the {@code null} guard is the port's own. Not to be confused with the per-grid marker
     * {@link #isProject()}.
     *
     * <p>Function featIsProjectable coded before 260930, commented in full on 260930.
     *
     * @return true if this square can have a projectable in it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsProjectable() {
        return feat != null && feat.isProjectable();
    }

    /**
     * Checks to see if the feature of this square allows line of sight, the port of C's
     * {@code square_allowslos} ({@code cave-square.c}): {@code feat_is_los}, the {@code TF_LOS}
     * flag. The {@code null} guard is the port's own.
     *
     * <p>Function featAllowsLOS coded before 260930, commented in full on 260930.
     *
     * @return true if this square allows LoS
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featAllowsLOS() {
        return feat != null && feat.isLos();
    }

    /**
     * Checks to see if the feature of this square is a wall, the port of C's {@code feat_is_wall}
     * ({@code cave-square.c}) applied to this grid's terrain: the {@code TF_WALL} flag.
     *
     * <p>Rubble is rock but not a wall, so it answers false. The {@code null} guard is the port's
     * own.
     *
     * <p>Function featIsWall coded before 260930, commented in full on 260930.
     *
     * @return true if this square is a wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean featIsWall() {
        return feat != null && feat.isWall();
    }

    /**
     * Check to see if this square is internally lit, the port of C's {@code square_isbright}
     * ({@code cave-square.c}): {@code feat_is_bright}, the {@code TF_BRIGHT} flag. Bright terrain
     * lights itself, which is why {@code Chunk.calcLighting} adds light for it. The {@code null}
     * guard is the port's own.
     *
     * <p>Function featIsBright coded before 260930, commented in full on 260930.
     *
     * @return true if this square is internally lit
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsBright() {
        return feat != null && feat.isBright();
    }

    /**
     * Checks if this square is fire based, the port of C's {@code square_isfiery}
     * ({@code cave-square.c}): {@code feat_is_fiery}, the {@code TF_FIERY} flag. The {@code null}
     * guard is the port's own. See {@link #isDamaging()}, which asks the same question of the
     * terrain but reads as damage.
     *
     * <p>Function featIsFiery coded before 260930, commented in full on 260930.
     *
     * @return true if this square is lava
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsFiery() {
        return feat != null && feat.isFiery();
    }

    /**
     * Checks if the square doesn't allow monster flow information, the port of C's
     * {@code square_isnoflow} ({@code cave-square.c}): {@code feat_is_no_flow}, the
     * {@code TF_NO_FLOW} flag. The {@code null} guard is the port's own.
     *
     * <p>Function featIsNoFlow coded before 260930, commented in full on 260930.
     *
     * @return true if the square DOESN'T allow monster flow information
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsNoFlow() {
        return feat != null && feat.isNoFlow();
    }

    /**
     * Tests to see if this square carries player scent or not, the port of C's
     * {@code square_isnoscent} ({@code cave-square.c}): {@code feat_is_no_scent}, the
     * {@code TF_NO_SCENT} flag. The {@code null} guard is the port's own.
     *
     * <p>Function featIsNoScent coded before 260930, commented in full on 260930.
     *
     * @return true if this square DOESN'T carry player scent
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsNoScent() {
        return feat != null && feat.isNoScent();
    }

    /**
     * Check to see if this is an untrapped square without items, the port of C's
     * {@code square_canputitem} ({@code cave-square.c}).
     *
     * <p>The terrain must be able to hold an object ({@link #isObjectHolding()}), the grid must
     * not carry a known trap ({@link #isTrap()}, the marker, not the player-trap flag), and the
     * pile must be empty. Occupants are not considered, so a monster standing on bare floor does
     * not prevent an item being put there.
     *
     * <p>Function canPutItem coded before 260930, commented in full on 260930.
     *
     * @return true if this is an untrapped square without items
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean canPutItem() {
        if (!isObjectHolding() || isTrap()) return false;
        return objectPile.isEmpty();
    }

    /**
     * Check to see if the square can damage an individual - currently only lava, the port of C's
     * {@code square_isdamaging} ({@code cave-square.c}): {@code feat_is_fiery}.
     *
     * <p>Function isDamaging coded before 260930, commented in full on 260930.
     *
     * @return true if the square is lava
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isDamaging() {
        return feat.isFiery();
    }

    /**
     * True if a feeling can be used on this square, the port of C's {@code square_allowsfeel}
     * ({@code cave-square.c}): the terrain is passable and not damaging.
     *
     * <p>Function allowsFeel coded before 260930, commented in full on 260930.
     *
     * @return true if this square can be used for a feeling
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean allowsFeel() {
        return featIsPassable() && !isDamaging();
    }

    /**
     * Getter for the terrain of this square, the port of C's {@code square_feat}
     * ({@code cave-square.c}). C returns the {@code f_info[]} entry for the grid's feature index;
     * the port holds the {@link Feature} directly, so this hands back the field.
     *
     * <p>Function getFeature coded before 260930, commented in full on 260930.
     *
     * @return the feat of this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Feature getFeature() {
        return feat;
    }

    /**
     * Setter for the terrain of this square, package-private.
     *
     * <p>This is a plain assignment. C's {@code square_set_feat} ({@code cave-square.c}) does more
     * than write the field: it keeps the chunk's per-feature counts, turns on {@code SQUARE_GLOW}
     * for bright terrain, and once the level exists removes traps the new terrain cannot hold and
     * redraws the grid. None of that happens here, so it is for setting a feature up rather than
     * for changing terrain in play.
     *
     * <p>Function setFeature coded before 260930, commented in full on 260930.
     *
     * @param feature the feature to set this.feat to
     */
    void setFeature(@NotNull Feature feature) {
        feat = feature; }

    /**
     * Getter for the occupant index, reading C's {@code square->mon}: positive for a monster,
     * negative for the player, zero for nobody.
     *
     * <p>Function getMonsterIndex coded before 260930, commented in full on 260930.
     *
     * @return the int index of the monster on this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getMonsterIndex() {
        return monsterIndex;
    }

    /**
     * Test-only helper that populates this square with a known fixture. When
     * {@code full} is true the square becomes a lit floor occupied by a monster,
     * carrying three objects, a trap and all info flags set; otherwise it becomes
     * an empty, dark, unknown square holding the player.
     *
     * <p>Function setUpTest coded before 260930, commented in full on 260930.
     *
     * @param full whether to build the fully-populated fixture
     */
    @TestOnly
    void setUpTest(boolean full) {
        if (objectPile == null)
            objectPile = new Pile();

        if (full) {
            feat = TerrainRegistry.lookupFeature(TerrainFlags.FEAT_FLOOR);
            light = 2;
            monsterIndex = 1;
            objectPile.clear();
            objectPile.insert(new ItemObject());
            objectPile.insert(new ItemObject());
            objectPile.insert(new ItemObject());
            traps.clear();
            traps.addFirst(new Trap());
            info.clear();
            info.negate();
        } else {
            feat = TerrainRegistry.lookupFeature(TerrainFlags.FEAT_NONE);
            light = 0;
            monsterIndex = -1;
            objectPile.clear();
            traps.clear();
            info.clear();
        }
    }

    /**
     * Returns the objects lying on this square, the port of reading C's {@code square(c, grid)->obj}.
     *
     * <p>Live, not a copy — this is how objects are added to and taken from the floor, so a snapshot
     * would be useless. C reaches the same pile through {@code square_object}, which hands back the
     * head of a linked list that callers then walk by {@code obj->next}; the port keeps a
     * {@link Pile}, which is why iteration here goes through {@link Pile#getIterator} and why
     * {@link #holdsObject} can be a single containment test rather than a walk.
     *
     * <p>Function getObjectPile commented in full on 260816.
     *
     * @return this square's object pile, shared with this instance
     */
    public Pile getObjectPile() {
        return objectPile;
    }

    /**
     * Redraws this square on screen, the port of C's {@code square_light_spot}
     * ({@code cave-view.c}).
     *
     * <p>A display refresh rather than a change of state: nothing about the square is altered, the
     * player is simply shown it again because something that decides how it is drawn has moved on.
     * {@code Player.flavourAware} is the current caller — becoming aware of a kind can change the
     * glyph its items are drawn with, so every floor square holding one is refreshed.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the display side of the rework; takes no action,
     * so callers currently make their decisions correctly and simply leave the screen stale.
     *
     * <p>Function lightSpot coded before 260817, commented in full on 260817.
     */
    public void lightSpot() {
        // STUB function. TODO: Implement
    }

    /**
     * Turns one info flag on for this square, the port of C's {@code sqinfo_on}
     * ({@code cave.h}).
     *
     * <p>The info field is a set of independent flags rather than a value, so this adds the one
     * named and leaves the rest of the field alone. Setting a flag already set is not an error and
     * changes nothing — callers such as {@code Chunk.markWasSeen} sweep whole levels and set as
     * they go rather than testing first, which only works because the operation is idempotent.
     *
     * <p>The write goes through this method rather than exposing the field because the info flags
     * are the square's own state: a caller handed the {@link Flag} itself could keep it and write
     * to the square long after it stopped looking like a caller.
     *
     * <p>The return value distinguishes the two cases the method itself treats alike: {@code true}
     * means the square changed, {@code false} that the flag was already on and the call did
     * nothing. It matches C, where {@code sqinfo_on} resolves to {@code flag_on}
     * ({@code z-bitflag.c}) and reports the same thing. Nothing in C reads it, so a caller here has
     * no ported precedent to follow — it is available for the redraw question, "did this call
     * actually change what the player would see", which is otherwise only answerable by testing the
     * flag first.
     *
     * <p>Function sqInfoOn coded before 260827, commented in full on 260827, updated on 260827 when
     * the return type changed from void to boolean.
     *
     * @param flag the info flag to set on this square
     * @return true if the flag was off and is now on, false if it was already on and nothing
     * changed
     */
    public boolean sqInfoOn(SquareEnum flag) {
        return info.on(flag);
    }

    /**
     * Turns one info flag off for this square, the port of C's {@code sqinfo_off}
     * ({@code cave.h}).
     *
     * <p>The counterpart to {@link #sqInfoOn(SquareEnum)}, and equally narrow: it clears the one
     * flag named and no other. That matters more here than it does for setting, because the info
     * field mixes flags with very different lifetimes — {@code SQUARE_VIEW} is rebuilt every time
     * the player moves, while {@code SQUARE_MARK} records what they have explored and must survive
     * the whole game. Clearing per flag is what lets the visibility sweep run over every grid on
     * the level without erasing the map.
     *
     * <p>Clearing a flag that is not set is not an error and changes nothing.
     *
     * <p>The return value says which of those happened: {@code true} that the flag was on and has
     * been cleared, {@code false} that it was already off. As with {@link #sqInfoOn(SquareEnum)}
     * this matches C's {@code flag_off} ({@code z-bitflag.c}). It is worth more here than on the
     * setting side, because clearing is the operation done in bulk — the visibility sweep clears
     * three flags from every grid on the level, and the great majority of those calls return
     * {@code false} because there was nothing there to clear.
     *
     * <p>Function sqInfoOff coded before 260827, commented in full on 260827, updated on 260827
     * when the return type changed from void to boolean.
     *
     * @param flag the info flag to clear from this square
     * @return true if the flag was on and is now off, false if it was already off and nothing
     * changed
     */
    public boolean sqInfoOff(SquareEnum flag) {
        return info.off(flag);
    }

    /**
     * Sets the light falling on this square, the port of writing C's
     * {@code c->squares[y][x].light} ({@code cave-view.c}).
     *
     * <p>C has no setter for this: {@code calc_lighting} and {@code add_light} assign the field
     * directly, and {@code square_light} ({@code cave-square.c}) is the only accessor of the pair.
     * The port needs the write to go through a method because the field is private, so this is a
     * plain assignment with no C counterpart to match beyond the assignments themselves.
     *
     * <p>The value is a light <em>intensity</em>, not a flag, and it is not validated or clamped
     * here. Zero is dark and anything above it is lit, which is all {@link #isLit()} asks; higher
     * values matter to the display, which draws a brightly lit grid differently from a dimly lit
     * one. Negative values are legitimate — a monster with negative {@code light} radiates darkness
     * and {@code addLight} subtracts, so a grid can finish a recalculation below zero. Clamping
     * here would silently diverge from C, which lets the arithmetic stand.
     *
     * <p>The caller is {@code Chunk.calcLighting}, which owns the field's whole lifetime:
     * it resets every grid on the level to 1 or 0 from permanent glow alone, then accumulates
     * bright terrain, the player's light and each monster's light on top. Nothing else should write
     * a light level, because a value set outside that sweep survives only until the next one runs.
     *
     * <p>Function setLight coded before 260828, commented in full on 260828.
     *
     * @param level the new light intensity for this square: zero for dark, positive for lit, negative
     *              for a grid the darkness sources have taken below zero
     */
    public void setLight(int level) {
        light = level;
    }

    /**
     * Sets the occupant index, the port of C's {@code square_set_mon} ({@code cave-square.c}),
     * which is the single assignment {@code c->squares[y][x].mon = midx}.
     *
     * <p>The value is not validated or cross-checked against the monster list: positive is a
     * monster's index, negative is the player, zero clears the grid, and keeping the chunk's
     * monster table consistent with it is the caller's job, as in C.
     *
     * <p>Function setMon coded before 260930, commented in full on 260930.
     *
     * @param monIndex the new occupant index: positive for a monster, negative for the player, zero for nobody
     */
    public void setMon(int monIndex) {
        this.monsterIndex = monIndex;
    }
}