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

package uk.co.jackoftradesltd.middle.objects;

import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.objects.enums.ChestTrapCode;

import java.util.List;

/**
 * One kind of chest trap, loaded from {@code chest_trap.txt}. The port of C's
 * {@code struct chest_trap} ({@code object.h}).
 *
 * <p>C reads these records in {@code obj-chest.c} ({@code parse_chest_trap_*}) and uses them in
 * four places: {@code chest_trap_name} turns a chest's pval back into a name, {@code
 * pick_one_chest_trap} draws a trap whose level fits the chest, {@code chest_trap} springs every
 * trap whose bit is set in the chest's pval, in file order, and {@code do_cmd_disarm_chest} reads
 * each carried trap's {@code magic} flag. The killer text for a death by chest trap
 * ({@code SRC_CHEST_TRAP} in {@code project-player.c} and {@code effect-handler-attack.c}) is the
 * trap's {@code msg_death}.
 *
 * <p>The differences from the C struct:
 * <ul>
 *   <li>No {@code next} link. C threads the traps into one list headed by the global
 *   {@code chest_traps}; here the list is an ordinary {@code List} held by
 *   {@link uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry}.</li>
 *   <li>No stored {@code pval}. C's {@code pval} - the bit that says "this chest carries this
 *   trap" - is decided by the record's position in the file; it is derived from
 *   {@link ChestTrapCode} here, which is why {@link #getPVal} answers from the code.</li>
 *   <li>{@code code} is a {@link ChestTrapCode} rather than a string.</li>
 *   <li>{@code effect} is a {@code List}, not a chain of {@code struct effect}. A trap carries
 *   several: "poison needle" is {@code DAMAGE} followed by {@code DRAIN_STAT}, the chain
 *   {@code parse_chest_trap_effect} builds.</li>
 *   <li>{@code msg} and {@code msg_death} are {@code ""} where C holds {@code NULL}, so a caller
 *   that tests {@code if (trap->msg)} must test for an empty string here.</li>
 * </ul>
 *
 * <p>Every field is final and the instances are created only by {@code ChestTrapAssembler} at load
 * time, but the effect list is shared by reference, so a caller must not modify it.
 *
 * <p>Class ChestTrap coded before 260815, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class ChestTrap {
    /**
     * The trap's display name: C's {@code chest_trap.name}, which {@code chest_trap_name} returns
     * when a chest carries exactly one trap. Not unique - two traps are called "gas trap" and two
     * "poison needle" - so it identifies nothing; {@link #code} does.
     *
     * <p>Field name coded before 260815, commented in full on 261009.
     */
    private final String name;
    /**
     * The trap's identity, and the source of its pval bit. C's {@code chest_trap.code} is a string
     * it never reads again after parsing; here it keys the trap to its bit.
     *
     * <p>Field code coded before 260815, commented in full on 261009.
     */
    private final ChestTrapCode code;
    /**
     * The minimum object level of chest this trap can appear on: C's {@code chest_trap.level}. The
     * only thing gating which traps a chest may draw - {@code pick_one_chest_trap} counts the traps
     * after the "locked" entry whose level is at most the chest's, then picks one of them at random.
     *
     * <p>Field level coded before 260815, commented in full on 261009.
     */
    private final int level;
    /**
     * The effects fired when the trap springs, in file order: C's {@code chest_trap.effect}. Empty
     * for the "locked" entry, which has no effect at all, where C holds {@code NULL}; {@code
     * chest_trap} skips {@code effect_do} in that case.
     *
     * <p>Field effect coded before 260815, commented in full on 261009.
     */
    private final List<Effect> effect;
    /**
     * Whether springing the trap destroys the chest's contents: C's {@code chest_trap.destroy}. In
     * {@code chest_trap} it zeroes the chest's pval and stops the walk, so traps later in the file
     * do not fire. C sets it for any non-zero {@code destroy:} value.
     *
     * <p>Field destroy coded before 260815, commented in full on 261009.
     */
    private final boolean destroy;
    /**
     * Whether the trap is magical rather than physical: C's {@code chest_trap.magic}. The data file
     * sets it on the summoning runes only. {@code do_cmd_disarm_chest} reads it to pick the
     * disarming skill: the magic skill if every trap on the chest is magical, the average of the
     * magic and physical skills if the chest carries both kinds, the physical skill otherwise. C
     * sets it for any non-zero {@code magic:} value.
     *
     * <p>Field magic coded before 260815, commented in full on 261009.
     */
    private final boolean magic;
    /**
     * The message shown when the trap is triggered: C's {@code chest_trap.msg}. {@code ""} if the
     * record declared none, where C holds {@code NULL} and {@code chest_trap} tests for it before
     * calling {@code msg}.
     *
     * <p>Field message coded before 260815, commented in full on 261009.
     */
    private final String message;
    /**
     * The message shown if the trap kills the character - the phrase completing "killed by ...":
     * C's {@code chest_trap.msg_death}, used as the killer text for {@code SRC_CHEST_TRAP}.
     * {@code ""} if the record declared none, where C holds {@code NULL}. The shipped
     * {@code chest_trap.txt} declares one only for the two poison needles ("a poison needle") and
     * the explosion device ("an exploding chest"), the three that deal direct damage.
     *
     * <p>Field messageDeath coded before 260815, commented in full on 261009.
     */
    private final String messageDeath;

    /**
     * Builds one trap. Called only by {@code ChestTrapAssembler}, which has already resolved the
     * code, parsed the level and assembled the effects. Stores the arguments as given: the effect
     * list is held by reference, and nothing is derived, since the pval bit comes from the code.
     *
     * <p>Constructor ChestTrap coded before 260815, commented in full on 261009.
     *
     * @param name         the display name
     * @param code         the trap's identity, which also carries its pval bit
     * @param level        the minimum chest level this trap can appear on
     * @param effect       the effects fired when the trap springs, in file order
     * @param destroy      whether springing the trap destroys the chest's contents
     * @param magic        whether the trap is magical rather than physical
     * @param message      the message shown when the trap is triggered
     * @param messageDeath the message shown if the trap kills the character
     */
    public ChestTrap(String name, ChestTrapCode code, int level, List<Effect> effect, boolean destroy,
                     boolean magic, String message, String messageDeath) {
        this.name = name;
        this.code = code;
        this.level = level;
        this.effect = effect;
        this.destroy = destroy;
        this.magic = magic;
        this.message = message;
        this.messageDeath = messageDeath;
    }

    /**
     * The trap's display name. C's {@code chest_trap_name} returns this when a chest carries exactly
     * one trap.
     *
     * <p>Function getName coded before 260815, commented in full on 261009.
     *
     * @return the display name; never unique, so do not key on it
     */
    public String getName() {
        return name;
    }

    /**
     * The bit that marks this trap's presence in a chest's {@code pval}. A chest's pval is the OR of
     * the bits of the traps it carries, so this is what {@code pick_chest_traps} accumulates and
     * what {@code chest_trap_name}, {@code chest_trap} and {@code do_cmd_disarm_chest} test against.
     * Unlike C's stored {@code chest_trap.pval} it is not a field: it is asked of the code every
     * time.
     *
     * <p>Function getPVal coded before 260815, commented in full on 261009.
     *
     * @return this trap's pval bit, from its {@link ChestTrapCode}
     */
    public int getPVal() {
        return code.getPval();
    }

    /**
     * The trap's identity: the port of C's {@code chest_trap.code} string, as an enum.
     *
     * <p>Function getCode coded before 260815, commented in full on 261009.
     *
     * @return the trap's identity
     */
    public ChestTrapCode getCode() {
        return code;
    }

    /**
     * The level gate {@code pick_one_chest_trap} applies: a chest of object level {@code L} may draw
     * this trap when {@code getLevel() <= L}.
     *
     * <p>Function getLevel coded before 260815, commented in full on 261009.
     *
     * @return the minimum chest level this trap can appear on
     */
    public int getLevel() {
        return level;
    }

    /**
     * The effects {@code chest_trap} passes to {@code effect_do} when the trap springs. The list is
     * the live one the trap holds, not a copy.
     *
     * <p>Function getEffect coded before 260815, commented in full on 261009.
     *
     * @return the effects fired when the trap springs, in file order; empty for "locked"
     */
    public List<Effect> getEffect() {
        return effect;
    }

    /**
     * Whether springing this trap empties the chest: {@code chest_trap} sets the pval to zero and
     * stops checking the remaining traps.
     *
     * <p>Function isDestroy coded before 260815, commented in full on 261009.
     *
     * @return whether springing the trap destroys the chest's contents
     */
    public boolean isDestroy() {
        return destroy;
    }

    /**
     * Whether the trap counts as magical when {@code do_cmd_disarm_chest} chooses the disarming
     * skill.
     *
     * <p>Function isMagic coded before 260815, commented in full on 261009.
     *
     * @return whether the trap is magical rather than physical
     */
    public boolean isMagic() {
        return magic;
    }

    /**
     * The text {@code chest_trap} shows when it springs the trap. Test for {@code isEmpty()} where C
     * tests for {@code NULL}.
     *
     * <p>Function getMessage coded before 260815, commented in full on 261009.
     *
     * @return the message shown when the trap is triggered, or {@code ""} if it declared none
     */
    public String getMessage() {
        return message;
    }

    /**
     * The killer text for a death by this trap, which C's {@code SRC_CHEST_TRAP} arms in
     * {@code project-player.c} and {@code effect-handler-attack.c} format straight from
     * {@code msg_death}.
     *
     * <p>Function getMessageDeath coded before 260815, commented in full on 261009.
     *
     * @return the message shown if the trap kills the character, or {@code ""} if it declared none
     */
    public String getMessageDeath() {
        return messageDeath;
    }
}
