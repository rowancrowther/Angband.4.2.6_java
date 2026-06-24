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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle;

import uk.co.jackoftrades.middle.effect.Effect;

import java.util.List;

/**
 * A named, reusable activation (as loaded from {@code activation.txt}) — the
 * triggerable power shared by artifacts and some items, defined by the list of
 * {@link Effect}s it runs plus its level, power, aim requirement and flavour
 * messages. This is the Java port of the C original's {@code struct activation}
 * ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public class Activation {
    /**
     * The activation's name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * Index of this activation in the global activation table.
     *
     * @author ClaudeCode
     */
    private int index;
    /**
     * Whether using this activation requires aiming at a target.
     *
     * @author ClaudeCode
     */
    private boolean aim;
    /**
     * The minimum level associated with this activation (for rating/value).
     *
     * @author ClaudeCode
     */
    private int level;
    /**
     * The activation's power rating.
     *
     * @author ClaudeCode
     */
    private int power;
    /**
     * The chain of effects this activation runs when used.
     *
     * @author ClaudeCode
     */
    private List<Effect> effects;
    /**
     * The message shown when the activation is used.
     *
     * @author ClaudeCode
     */
    private String message;
    /**
     * Human-readable description of the activation.
     *
     * @author ClaudeCode
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
     * @author ClaudeCode
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
     * @return this activation's name
     * @author ClaudeCode
     */
    public String getName() {
        return name;
    }
}
