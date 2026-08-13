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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.objects.enums.ChestTrapCode;

import java.util.List;

/**
 * One kind of chest trap, loaded from {@code chest_trap.txt}. The port of C's
 * {@code struct chest_trap} ({@code object.h:67-78}).
 *
 * <p>Two differences from the C struct are worth knowing. C threads the traps together with a
 * {@code next} pointer into one list headed by the global {@code chest_traps}; here the list is an
 * ordinary {@code List} held by
 * {@link uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry}, so the link field is
 * gone. And C's {@code pval} field - the bit that says "this chest carries this trap" - is not
 * stored per trap; it is derived from {@link ChestTrapCode}, which is why {@link #getPVal} answers
 * from the code rather than from a field.
 *
 * <p>A trap carries a <em>list</em> of effects, not one: "poison needle" is {@code DAMAGE} followed
 * by {@code DRAIN_STAT}, matching the effect chain C builds in
 * {@code parse_chest_trap_effect}. Instances are immutable and are created only by
 * {@code ChestTrapAssembler} at load time.
 *
 * @author Rowan Crowther
 */
public class ChestTrap {
    /**
     * The trap's display name, as shown by C's {@code chest_trap_name}. Not unique - two traps are
     * called "gas trap" and two "poison needle" - so it identifies nothing; {@link #code} does.
     */
    private final String name;
    /**
     * The trap's identity, and the source of its pval bit.
     */
    private final ChestTrapCode code;
    /**
     * The minimum object level of chest this trap can appear on. The only thing gating which traps
     * a given chest may draw - cf. {@code pick_one_chest_trap} ({@code obj-chest.c:359-375}).
     */
    private final int level;
    /**
     * The effects fired when the trap springs, in file order. Empty for the "locked" entry, which
     * has no effect at all.
     */
    private final List<Effect> effect;
    /**
     * Whether springing the trap destroys the chest's contents.
     */
    private final boolean destroy;
    /**
     * Whether the trap is magical rather than physical.
     */
    private final boolean magic;
    /**
     * The message shown when the trap is triggered; {@code ""} if the record declared none.
     */
    private final String message;
    /**
     * The message shown if the trap kills the character - the phrase completing "killed by ...";
     * {@code ""} if the record declared none.
     */
    private final String messageDeath;

    /**
     * Builds one trap. Called only by {@code ChestTrapAssembler}, which has already resolved the
     * code, parsed the level and assembled the effects.
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
     * @return the display name; never unique, so do not key on it
     */
    public String getName() {
        return name;
    }

    /**
     * The bit that marks this trap's presence in a chest's {@code pval}. A chest's pval is the OR of
     * the bits of the traps it carries, so this is what {@code pick_chest_traps} accumulates and
     * what {@code chest_trap_name} tests against.
     *
     * @return this trap's pval bit, from its {@link ChestTrapCode}
     */
    public int getPVal() {
        return code.getPval();
    }

    /**
     * @return the trap's identity
     */
    public ChestTrapCode getCode() {
        return code;
    }

    /**
     * @return the minimum chest level this trap can appear on
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the effects fired when the trap springs, in file order; empty for "locked"
     */
    public List<Effect> getEffect() {
        return effect;
    }

    /**
     * @return whether springing the trap destroys the chest's contents
     */
    public boolean isDestroy() {
        return destroy;
    }

    /**
     * @return whether the trap is magical rather than physical
     */
    public boolean isMagic() {
        return magic;
    }

    /**
     * @return the message shown when the trap is triggered, or {@code ""} if it declared none
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the message shown if the trap kills the character, or {@code ""} if it declared none
     */
    public String getMessageDeath() {
        return messageDeath;
    }
}
