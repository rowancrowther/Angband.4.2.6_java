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
 * <p>Two C members are not fields here. {@code next}, the link that chains the records into a list,
 * is replaced by the registry's own {@link List} of activations; and {@code effect}, the head of a
 * linked chain of {@code struct effect}, is a {@link List} of {@link Effect}s in the same order. C's
 * "no effect" is a {@code NULL} head, which is an empty list here: the assembler always passes a
 * non-null list, and {@link #copy()} would throw on a {@code null} one.
 *
 * <p>C's activation file parser is the source of every field's value: {@code parse_act_name} starts
 * a record, and {@code parse_act_aim}, {@code parse_act_level}, {@code parse_act_power},
 * {@code parse_act_msg} and {@code parse_act_desc} (all {@code obj-init.c}) fill the rest in. The
 * port of that reading is {@code ActivationAssembler}; this class only holds the result.
 *
 * <p>Class Activation coded before 261013, commented in full on 261125.
 *
 * @author Rowan Crowther
 */
public class Activation {
    /**
     * The activation's name — C's {@code activation->name}, the {@code name:} line that starts the
     * record in {@code activation.txt} ({@code parse_act_name}, {@code obj-init.c}).
     *
     * <p>It is the key other data files use to refer to the activation: C's {@code findact}
     * ({@code obj-init.c}) finds an activation by an exact {@code streq} on this name. It is set
     * once at construction and read through {@link #getName()}.
     *
     * <p>Field name coded before 261125, commented in full on 261125.
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
     * Whether using this activation requires aiming at a target — C's {@code activation->aim}.
     *
     * <p>C reads the {@code aim:} line as an unsigned number and stores {@code true} for any
     * non-zero value ({@code parse_act_aim}, {@code obj-init.c}). Nothing else in the C source
     * reads the stored value: whether an item needs a direction is decided from its effects
     * ({@code obj_needs_aim} in {@code obj-util.c} asks {@code effect_aim}), so this field is
     * carried for fidelity to the data file and has no reader in the port either. A leading
     * {@code -} on the data line is read as a sign, where C refuses it; that is an accepted
     * deviation recorded in {@code docs/Accepted_Deviations.md}, section Activation.
     *
     * <p>Field aim coded before 261125, commented in full on 261125.
     */
    private boolean aim;

    /**
     * The level associated with this activation — C's {@code activation->level}, read from the
     * {@code level:} line as a signed integer ({@code parse_act_level}, {@code obj-init.c}).
     *
     * <p>C uses it as the item level when the object has an activation but no artifact: the device
     * skill check in {@code cmd-obj.c} (activation use), the activation description in
     * {@code obj-info.c}, the object level in {@code obj-util.c}, and the level given to a
     * randomly made artifact in {@code obj-randart.c}. The port has no reader yet.
     *
     * <p>Field level coded before 261125, commented in full on 261125.
     */
    private int level;

    /**
     * The activation's power rating — C's {@code activation->power}, read from the {@code power:}
     * line as an unsigned integer ({@code parse_act_power}, {@code obj-init.c}).
     *
     * <p>C adds it to the power of an object that carries the activation ({@code obj-power.c}) and
     * compares it against the inhibit limit when the random artifact generator looks for the
     * strongest effect ({@code obj-randart.c}). The port reads it through {@link #getPower()}.
     * A negative value is refused at load by {@code ActivationAssembler}.
     *
     * <p>Field power coded before 261125, commented in full on 261125.
     */
    private int power;

    /**
     * The effects this activation runs when used, in file order — C's {@code activation->effect}.
     *
     * <p>C builds a linked chain by appending each {@code effect:} line to the tail
     * ({@code parse_act_effect}, {@code obj-init.c}), so the first effect in the file runs first.
     * The port holds the same order in a {@link List}; an activation with no {@code effect:} line
     * (C's {@code NULL} chain) holds an empty list, never {@code null}. {@link Effect} is mutable,
     * so {@link #copy()} rebuilds the list instead of sharing it. There is no getter yet.
     *
     * <p>Field effects coded before 261125, commented in full on 261125.
     */
    private List<Effect> effects;

    /**
     * The message shown when the activation is used — C's {@code activation->message}, the
     * {@code msg:} lines of the record.
     *
     * <p>C joins repeated {@code msg:} lines end to end with {@code string_append}
     * ({@code parse_act_msg}, {@code obj-init.c}) and leaves the pointer {@code NULL} when the
     * record has none; the port holds {@code null} in that case. C prints it when an activation is
     * used, unless the artifact has its own alternative message ({@code activation_message} in
     * {@code cmd-obj.c}). There is no getter yet.
     *
     * <p>Field message coded before 261125, commented in full on 261125.
     */
    private String message;

    /**
     * Human-readable description of the activation — C's {@code activation->desc}, the
     * {@code desc:} lines of the record.
     *
     * <p>C joins repeated {@code desc:} lines with {@code string_append} ({@code parse_act_desc},
     * {@code obj-init.c}) and leaves the pointer {@code NULL} when the record has none; the port
     * holds {@code null} in that case. C's item information screen shows it after "When activated,
     * it " ({@code obj-info.c}), and falls back to a generated description when it is {@code NULL}.
     * There is no getter yet.
     *
     * <p>Field desc coded before 261125, commented in full on 261125.
     */
    private String desc;

    /**
     * Build an activation from its parsed data-file fields.
     *
     * <p>The constructor stores every argument as given: no range check, no null check and no
     * defensive copy, so the activation holds the caller's {@code effects} list itself. C's
     * equivalent is a {@code mem_zalloc} of the struct filled in by the {@code parse_act_*}
     * handlers one line at a time ({@code obj-init.c}); the port collects the whole record first
     * and builds the activation in one step. {@code ActivationAssembler} passes {@code 0} as the
     * index and sets the real one afterwards with {@link #setIndex(int)}; {@link #copy()} passes
     * the original's index through.
     *
     * <p>Function Activation coded before 261125, commented in full on 261125.
     *
     * @param name    activation name; C's {@code name}
     * @param index   position in the registry's activation list; see {@link #index}
     * @param aim     whether it must be aimed; C's {@code aim}
     * @param level   associated level; C's {@code level}
     * @param power   power rating; C's {@code power}
     * @param effects the effects it runs, in file order, empty for none; must not be {@code null}
     *                if {@link #copy()} is to be used
     * @param message use message, or {@code null} for none; C's {@code message}
     * @param desc    description, or {@code null} for none; C's {@code desc}
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
     * Getter for this activation's tag - its name.
     *
     * <p>C reads the name straight from {@code activation->name}; the lookup that matches a name
     * against the whole list is {@code findact} ({@code obj-init.c}), which is not part of this
     * class.
     *
     * <p>Function getName coded before 261125, commented in full on 261125.
     *
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
     * <p>C has no function that copies a {@code struct activation}: an object holds a pointer to
     * the one record in the global {@code activations[]} table. The Java copy exists so that
     * {@code ObjectKind} and {@code EgoItem}, which duplicate their activation, never share
     * mutable {@link Effect}s with the registry. The copy keeps the original's index, so it still
     * names the same slot. It throws {@link NullPointerException} if the effect list is
     * {@code null}; an activation from the assembler never has one.
     *
     * <p>Function copy coded before 261125, commented in full on 261125.
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
     * Getter for this activation's power rating, the amount it adds to the power of an object that
     * carries it.
     *
     * <p>Function getPower coded before 261125, commented in full on 261125.
     *
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
