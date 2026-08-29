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

package uk.co.jackoftrades.middle.game.event.projection;

import uk.co.jackoftrades.middle.cave.Trap;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.objects.ChestTrap;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.Player;

/**
 * Where an effect came from. Effects and projections need to know who or what set them off, so
 * that they can name the culprit in a message, skip the caster when a ball spreads over its own
 * grid, or decide whether the player has learned anything from the result.
 *
 * <p>C's {@code struct source} is a tagged union: a {@code what} discriminant, and a {@code which}
 * union carrying whichever pointer or index that discriminant selects. The two halves are
 * reproduced here as a discriminant enum and a sealed {@link SourceWhich} hierarchy, so the pairing
 * C leaves to convention becomes something the compiler enforces — a {@code SRC_TRAP} source cannot
 * be built holding an object, and a switch over {@code which} has to name every case.
 *
 * <p>Two details of the C are worth recording:
 * <ul>
 *   <li>C leaves {@code which} entirely uninitialised for {@code SRC_NONE} and {@code SRC_PLAYER}
 *       — those two sources carry a member of the union that is never read. Java has no way to
 *       express "absent" in a sealed reference, so {@code null} stands for it. Nothing consults
 *       {@code which} for either discriminant, so the substitution is not observable.</li>
 *   <li>C's monster source holds an {@code int} index into {@code cave->monsters} rather than a
 *       pointer, and every read of it in the C goes through {@code cave_monster}, which answers
 *       {@code NULL} for any index of zero or less. This port holds the {@link Monster} itself and
 *       resolves the index at the call site instead, which means the sentinel indices — zero for
 *       no monster, and the negative value a monster's target carries when it is aiming at the
 *       player — arrive here as a {@code null} monster.</li>
 * </ul>
 *
 * <p>Record Source coded on 260829, commented in full on 260829.
 *
 * @param what  which kind of thing set the effect off; the discriminant that decides which shape
 *              {@code which} takes
 * @param which the thing itself, or {@code null} where the discriminant carries no payload
 */
public record Source(SourceWhat what,
                     SourceWhich which) {

    /**
     * A source belonging to nobody. Used where an effect has to be attributed to something but has
     * no originator worth naming — level feelings, and effects the game itself applies.
     *
     * <p>Function sourceNone coded on 260829, commented in full on 260829.
     *
     * @return a source discriminated {@code SRC_NONE}, carrying nothing
     */
    public static Source sourceNone() {
        return new Source(SourceWhat.SRC_NONE, null);
    }

    /**
     * A source that is a trap on the floor, the one the player has just stepped onto.
     *
     * <p>Function sourceTrap coded on 260829, commented in full on 260829.
     *
     * @param trap the trap that fired
     * @return a source discriminated {@code SRC_TRAP}, carrying that trap
     */
    public static Source sourceTrap(Trap trap) {
        SourceWhich whichTrap = new SourceWhich.TrapRecord(trap);
        return new Source(SourceWhat.SRC_TRAP, whichTrap);
    }

    /**
     * A source that is a monster — its spells, its breaths, and the side effects of its melee
     * blows.
     *
     * <p>C takes the monster's index into the level's monster list here, not the monster itself.
     * A {@code null} monster is the port's spelling of the indices C treats as no monster at all:
     * zero, and the negative index a monster's target carries when its target is the player.
     *
     * <p>Function sourceMonster coded on 260829, commented in full on 260829.
     *
     * @param monster the monster responsible, or {@code null} where C would have passed an index
     *                of zero or less
     * @return a source discriminated {@code SRC_MONSTER}, carrying that monster
     */
    public static Source sourceMonster(Monster monster) {
        SourceWhich whichMonster = new SourceWhich.MonsterRecord(monster);
        return new Source(SourceWhat.SRC_MONSTER, whichMonster);
    }

    /**
     * A source that is the player. There is only ever one {@link Player}, so the discriminant
     * alone says everything and no payload is carried.
     *
     * <p>Function sourcePlayer coded on 260829, commented in full on 260829.
     *
     * @return a source discriminated {@code SRC_PLAYER}, carrying nothing
     */
    public static Source sourcePlayer() {
        return new Source(SourceWhat.SRC_PLAYER, null);
    }

    /**
     * A source that is an object — a wand aimed, a potion drunk, a rod zapped, or an activation
     * from a wielded item.
     *
     * <p>Function sourceObject coded on 260829, commented in full on 260829.
     *
     * @param itemObject the object whose effect is being applied
     * @return a source discriminated {@code SRC_OBJECT}, carrying that object
     */
    public static Source sourceObject(ItemObject itemObject) {
        SourceWhich whichObject = new SourceWhich.ObjectRecord(itemObject);
        return new Source(SourceWhat.SRC_OBJECT, whichObject);
    }

    /**
     * A source that is the trap on a chest, sprung by opening or disarming it. Kept distinct from
     * {@link #sourceTrap} because a chest trap is a different type with its own effect list, not a
     * trap occupying a grid.
     *
     * <p>Function sourceChestTrap coded on 260829, commented in full on 260829.
     *
     * @param chestTrap the chest trap that fired
     * @return a source discriminated {@code SRC_CHEST_TRAP}, carrying that chest trap
     */
    public static Source sourceChestTrap(ChestTrap chestTrap) {
        SourceWhich whichChestTrap = new SourceWhich.ChestTrapRecord(chestTrap);
        return new Source(SourceWhat.SRC_CHEST_TRAP, whichChestTrap);
    }
}
