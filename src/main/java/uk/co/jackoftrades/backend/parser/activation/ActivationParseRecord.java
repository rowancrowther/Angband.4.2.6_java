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

package uk.co.jackoftrades.backend.parser.activation;

import java.util.List;

/**
 * Thin parse-DTO produced by the Activations grammar for a single activation
 * record in lib/gamedata/activation.txt.
 *
 * <p>This class is the boundary between the grammar layer and the reader layer.
 * The grammar's only job is to extract raw text from the data file and populate
 * this record — no enum resolution, no domain-object construction, no calls to
 * GameConstants. All of that work happens in the reader (ActivationReader),
 * which converts a {@code List<ActivationParseRecord>} into the real domain
 * objects ({@code List<Activation>}) once the domain API is stable enough to
 * use.</p>
 *
 * <p>A DTO rather than returning the domain {@code Activation} directly
 * from the grammar, as the domain model is still being ported and its
 * constructors change frequently. Design decision to un-coupling a grammar
 * from a changing constructor. The DTO decouples the two: the grammar is
 * "done" once it parses correctly, regardless of whether the domain class
 * it feeds is ready.</p>
 *
 * <p>Field types are kept as primitives and Strings wherever possible.
 * {@code effects} is the one structured field: each inner list holds the
 * twelve string values extracted from a single {@code effectBlock} (in the
 * order defined by {@code EffectBlock.g4}'s {@code effectBlock} rule), because
 * a single activation can trigger multiple effects in sequence.</p>
 *
 * <p>Note: the sequential index assigned to each activation by the C engine's
 * {@code finish_parse_act} is intentionally absent here. The reader assigns it
 * from the record's position in the list, matching the C behaviour without
 * baking array-index logic into the grammar.</p>
 *
 * @author Rowan Crowther
 */
public class ActivationParseRecord {

    /**
     * The activation code name as it appears in activation.txt, e.g.
     * {@code CURE_POISON}. Other data files (artifact.txt, ego_item.txt)
     * reference this string to grant the activation to an object.
     */
    private final String name;

    /**
     * Whether this activation requires the player to aim it at a target.
     * Extracted from the {@code aim:0|1} directive; {@code true (1)} means
     * aiming is required.
     */
    private final boolean aim;

    /**
     * Activation difficulty, from the {@code level:<n>} directive. Used
     * by the reader to populate the domain object's difficulty field.
     */
    private final int level;

    /**
     * Relative power rating, from the {@code power:<n>} directive. Used
     * for object-power calculation and to derive the recharge time when
     * the activation is used by a random artifact.
     */
    private final int power;

    /**
     * The activation message shown to the player when the activation fires,
     * from the {@code msg:<text>} directive. May be an empty string if no
     * {@code msg:} line was present. Can contain {@code {kind}}, {@code {s}},
     * and similar substitution tags resolved at display time.
     */
    private final String message;

    /**
     * Short description of what the activation does, from the
     * {@code desc:<text>} directive (e.g. {@code "cures blindness"}).
     * Intended to be embedded in a larger sentence by the display layer.
     */
    private final String desc;

    /**
     * The effect blocks extracted for this activation, in parse order.
     * Each inner list holds the twelve string values returned by
     * {@code EffectBlock.g4}'s {@code effectBlock} rule:
     * <ol>
     *   <li>typeInit — the effect type name (maps to EffectEnum)</li>
     *   <li>subtypeWrapperInit — the subtype name (type depends on typeInit
     *   and used to wrap the value to a EffectSubTypeWrapper)</li>
     *   <li>radius — effect radius, or empty string if absent</li>
     *   <li>other — second parameter, or empty string if absent</li>
     *   <li>diceString — the dice expression, or empty string if absent</li>
     *   <li>yVal — y-coordinate for effect-yx, or empty string if absent</li>
     *   <li>xVal — x-coordinate for effect-yx, or empty string if absent</li>
     *   <li>expressionChars — caret-delimited expr variable letters</li>
     *   <li>expressionBase — caret-delimited expr base names</li>
     *   <li>expressionOperation — caret-delimited expr operations</li>
     *   <li>timeDiceString — the time: dice string, or empty string if absent</li>
     *   <li>effectMessage — the effect-msg: text, or empty string if absent</li>
     * </ol>
     * The reader is responsible for resolving each string to its domain type.
     */
    private final List<List<String>> effects;

    /**
     * Constructs a fully populated parse record. All fields are set at
     * construction and are immutable — the record is a snapshot of what
     * the parser extracted and is not modified afterwards.
     *
     * @param name    activation code name
     * @param aim     true if the activation requires aiming
     * @param level   difficulty level
     * @param power   relative power / recharge-time weight
     * @param message activation message (empty string if none)
     * @param desc    activation description
     * @param effects list of effect-block string tuples, in parse order
     */
    public ActivationParseRecord(String name, boolean aim, int level, int power,
                                 String message, String desc,
                                 List<List<String>> effects) {
        this.name = name;
        this.aim = aim;
        this.level = level;
        this.power = power;
        this.message = message;
        this.desc = desc;
        this.effects = effects;
    }

    /**
     * Simple getter for name of this Activation
     *
     * @return the name of this Activation
     */
    public String getName() {
        return name;
    }

    /**
     * Simple getter for whether this activation requires aiming or not
     *
     * @return whether this activation requires aiming or not
     */
    public boolean isAim() {
        return aim;
    }

    /**
     * Simple getter for the difficulty level of this activation
     *
     * @return the difficulty level of this activation
     */
    public int getLevel() {
        return level;
    }

    /**
     * Simple getter for the power of this activation
     *
     * @return the power of this activation
     */
    public int getPower() {
        return power;
    }

    /**
     * Simple getter for the message displayed when this activation triggers
     *
     * @return the message displayed when this activation triggers
     */
    public String getMessage() {
        return message;
    }

    /**
     * Simple getter for the description of this activation
     *
     * @return the description of this activation
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Simple getter for the effects of this activation, returned as a
     * List<List<String>>, where the inner list is a set of 12 strings for each effect,
     * and the outer list is the set of effects.
     *
     * @return a List<List<String>> containing the details of this activations
     * effects
     */
    public List<List<String>> getEffects() {
        return effects;
    }
}