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

package uk.co.jackoftradesltd.middle.game.event.projection;

import uk.co.jackoftradesltd.middle.cave.Trap;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.objects.ChestTrap;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

/**
 * The payload of a {@link Source}: the one thing the effect came from. It is the Java form of the
 * anonymous {@code which} union inside {@code struct source} in {@code source.h}, whose members are
 * {@code trap}, {@code monster}, {@code object} and {@code chest_trap}.
 *
 * <p>C leaves the choice of member to convention: whoever reads {@code which} must already know,
 * from {@code what}, which member was written. Here each member is its own record, and the interface
 * is sealed, so the four shapes are the only ones that can exist and a switch over a
 * {@code SourceWhich} has to name every case. The pairing with {@link SourceWhat} is still made by
 * the {@link Source} factories rather than enforced here: nothing stops a {@code Source} being built
 * by hand with a mismatched discriminant.
 *
 * <p>There is deliberately no record for {@code SRC_NONE} or {@code SRC_PLAYER}. C never writes
 * {@code which} for those two, so {@link Source} carries {@code null} in its place.
 *
 * <p>The monster member differs in type from C: C stores an {@code int} index into the level's
 * monster list, and this port stores the {@link Monster} itself. See {@link Source} for how the
 * sentinel indices are handled.
 *
 * <p>Interface SourceWhich coded on 260829, commented in full on 261001.
 */
public sealed interface SourceWhich permits SourceWhich.TrapRecord, SourceWhich.MonsterRecord,
        SourceWhich.ObjectRecord, SourceWhich.ChestTrapRecord {
    /**
     * Payload for {@link SourceWhat#SRC_TRAP}: the {@code trap} member of the C union.
     *
     * <p>Record TrapRecord coded on 260829, commented in full on 261001.
     *
     * @param trap the trap on the floor that fired
     */
    record TrapRecord(Trap trap) implements SourceWhich {
    }

    /**
     * Payload for {@link SourceWhat#SRC_MONSTER}: the {@code monster} member of the C union.
     *
     * <p>C holds an {@code int} index here and every read of it goes through {@code cave_monster};
     * this record holds the resolved {@link Monster}, which is {@code null} where C's index would
     * have been zero or negative.
     *
     * <p>Record MonsterRecord coded on 260829, commented in full on 261001.
     *
     * @param monster the monster responsible, or {@code null} for no monster
     */
    record MonsterRecord(Monster monster) implements SourceWhich {
    }

    /**
     * Payload for {@link SourceWhat#SRC_OBJECT}: the {@code object} member of the C union.
     *
     * <p>Record ObjectRecord coded on 260829, commented in full on 261001.
     *
     * @param object the object whose effect is being applied
     */
    record ObjectRecord(ItemObject object) implements SourceWhich {
    }

    /**
     * Payload for {@link SourceWhat#SRC_CHEST_TRAP}: the {@code chest_trap} member of the C union.
     *
     * <p>Record ChestTrapRecord coded on 260829, commented in full on 261001.
     *
     * @param chestTrap the trap on a chest that fired
     */
    record ChestTrapRecord(ChestTrap chestTrap) implements SourceWhich {
    }
}
