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

package uk.co.jackoftradesltd.middle;

import uk.co.jackoftradesltd.middle.effect.Effect;

import java.util.ArrayList;
import java.util.List;

/**
 * A named, reusable activation (as loaded from {@code activation.txt}) — the
 * triggerable power shared by artifacts and some items, defined by the list of
 * {@link Effect}s it runs plus its level, power, aim requirement and flavour
 * messages. This is the Java port of the C original's {@code struct activation}
 * ({@code src/object.h}).
 *
 * <p>Each activation also carries its {@link #index}, its slot in the registry's activation list,
 * which C's {@code finish_parse_act} ({@code obj-init.c}) numbers once the whole file is read and
 * the savefile code ({@code save.c}, {@code load.c}) writes and reads back to identify an
 * activation. {@code ActivationAssembler} numbers it through {@link #setIndex(int)}. There is no
 * getter yet; the savefile is not ported.
 *
 * <p>Class Activation coded before 261013, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
public class Activation {
    /**
     * The activation's name.
     */
    private String name;

    /**
     * Index of this activation in the global activation table — C's {@code activation->index}.
     *
     * <p>It is the activation's position in the registry's activation list, counting from 0 at the
     * head of the list, which holds the file's last record first. C counts from 1 for the same
     * records, because slot 0 of {@code activations[]} is a blank record the port does not keep (an
     * accepted deviation recorded in {@code ObjectRegistry}), so every Java index is C's minus one.
     * The constructor takes whatever it is given; {@code ActivationAssembler} passes 0 and sets the
     * real value with {@link #setIndex(int)} once the list is complete.
     *
     * <p>Field index coded before 261013, commented in full on 261013.
     */
    private int index;

    /**
     * Whether using this activation requires aiming at a target.
     */
    private boolean aim;

    /**
     * The minimum level associated with this activation (for rating/value).
     */
    private int level;

    /**
     * The activation's power rating.
     */
    private int power;

    /**
     * The chain of effects this activation runs when used.
     */
    private List<Effect> effects;

    /**
     * The message shown when the activation is used.
     */
    private String message;

    /**
     * Human-readable description of the activation.
     */
    private String desc;

    /**
     * Build an activation from its parsed data-file fields.
     *
     * @param name    activation name
     * @param index   index in the activation table
     * @param aim     whether it must be aimed
     * @param level   associated level
     * @param power   power rating
     * @param effects the effects it runs
     * @param message use message
     * @param desc    description
     */
    public Activation(String name, int index, boolean aim, int level, int power, List<Effect> effects, String message, String desc) {
        this.name = name;
        this.index = index;
        this.aim = aim;
        this.level = level;
        this.power = power;
        this.effects = effects;
        this.message = message;
        this.desc = desc;
    }

    /**
     * Getter for this activation's tag - its name
     * @return this activation's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an independent copy of this activation, effects included.
     *
     * <p>The effect list is rebuilt rather than shared: {@link Effect} is mutable, so handing the
     * same list to two activations would let a change to one show up on the other. Every other field
     * is a primitive or an immutable {@link String} and is simply passed through the constructor.
     *
     * <p>Function copy commented in full on 260827.
     *
     * @return a new activation equal to this one and sharing no mutable state with it
     */
    public Activation copy() {
        List<Effect> effects = new ArrayList<>();

        for (Effect e : this.effects) {
            effects.add(e.copy());
        }

        return new Activation(this.name, this.index, this.aim, this.level,
                this.power, effects, this.message, this.desc);
    }

    /**
     * @return the power this activation adds to an object that carries it - C's
     * {@code activation->power}, read by {@code ItemObject.effectsPower}
     */
    public int getPower() {
        return power;
    }

    /**
     * Sets this activation's slot number — the port of the assignment
     * {@code activations[count].index = count} in C's {@code finish_parse_act}
     * ({@code obj-init.c}), which has no function of its own.
     *
     * <p>It is a separate step from construction because C numbers an activation only after the
     * whole file is read and the list is in its final order, so the number cannot be known when the
     * record is assembled. The value is stored as given, with no range check, as C stores it. The
     * caller counts in the list's own order from 0; see {@link #index} for how that differs from
     * C's count.
     *
     * <p>{@link #copy()} carries the index across, so a copy keeps its original's slot.
     *
     * <p>Function setIndex coded on 261013, commented in full on 261013.
     *
     * @param index the activation's position in the registry's activation list, from 0
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
