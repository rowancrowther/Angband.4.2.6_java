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

package uk.co.jackoftrades.middle.cave;

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

import java.util.Objects;

/**
 * The immutable template describing one type of trap (as loaded from
 * {@code trap.txt}) — its display glyph, depth/rarity, power, effect(s) and the
 * messages shown when it is saved against or triggers. Live traps on the map are
 * {@link Trap} instances referring back to a {@code TrapKind}. This is the Java
 * port of the C original's {@code struct trap_kind} ({@code src/trap.h}).
 *
 * @author ClaudeCode
 */
public class TrapKind {
    /**
     * The trap type's internal name.
     *
     * @author ClaudeCode
     */
    private String trapKindName;
    /**
     * The trap's display text/title.
     *
     * @author ClaudeCode
     */
    private String text;
    /**
     * Human-readable description of the trap.
     *
     * @author ClaudeCode
     */
    private String description;
    /**
     * Message shown when the player saves against (avoids) the trap.
     *
     * @author ClaudeCode
     */
    private String messageOnSave;
    /**
     * Message shown when the player fails to avoid the trap.
     *
     * @author ClaudeCode
     */
    private String messageOnFailure;
    /**
     * Message shown when the trap's extra effect fires.
     *
     * @author ClaudeCode
     */
    private String messageOnExtraEffect;

    /**
     * This trap type's index in the global trap-kind table.
     *
     * @author ClaudeCode
     */
    private int trapKindIndex;

    /**
     * The glyph and colour used to draw this trap.
     *
     * @author ClaudeCode
     */
    private AngbandDisplayCharacter angbandDisplayCharacter;

    /**
     * Rarity weighting controlling how often this trap is chosen.
     *
     * @author ClaudeCode
     */
    private int rarity;
    /**
     * Shallowest dungeon level this trap can appear on.
     *
     * @author ClaudeCode
     */
    private int minDepth;
    /**
     * Deepest dungeon level this trap can appear on.
     *
     * @author ClaudeCode
     */
    private int maxDepth;
    /**
     * The trap's power, as a dice/random expression.
     *
     * @author ClaudeCode
     */
    private Random power;

    /**
     * Behavioural flags for this trap type.
     *
     * @author ClaudeCode
     */
    private Flag<TrapEnum> flags;
    /**
     * Object flags that grant a saving throw against this trap.
     *
     * @author ClaudeCode
     */
    private Flag<ObjectFlag> saveFlags;

    /**
     * The primary effect applied when the trap triggers.
     *
     * @author ClaudeCode
     */
    private Effect effect;
    /**
     * An optional secondary ("extra") effect.
     *
     * @author ClaudeCode
     */
    private Effect effectXtra;

    /**
     * Build a trap-kind template from its parsed data-file fields.
     *
     * @param trapKindName            internal name
     * @param text                    display text/title
     * @param description             human-readable description
     * @param messageOnSave           message on a successful save
     * @param messageOnFailure        message on a failed save
     * @param messageOnExtraEffect    message when the extra effect fires
     * @param trapKindIndex           index in the trap-kind table
     * @param angbandDisplayCharacter display glyph and colour
     * @param rarity                  rarity weighting
     * @param minDepth                shallowest level it appears on
     * @param maxDepth                deepest level it appears on
     * @param power                   power as a random expression
     * @param flags                   behavioural flags
     * @param saveFlags               object flags granting a save
     * @param effect                  primary effect
     * @param effectXtra              optional secondary effect
     * @author ClaudeCode
     */
    public TrapKind(String trapKindName, String text, String description, String messageOnSave,
                    String messageOnFailure, String messageOnExtraEffect, int trapKindIndex,
                    AngbandDisplayCharacter angbandDisplayCharacter, int rarity, int minDepth, int maxDepth,
                    Random power, Flag<TrapEnum> flags, Flag<ObjectFlag> saveFlags, Effect effect,
                    Effect effectXtra) {
        this.trapKindName = trapKindName;
        this.text = text;
        this.description = description;
        this.messageOnSave = messageOnSave;
        this.messageOnFailure = messageOnFailure;
        this.messageOnExtraEffect = messageOnExtraEffect;
        this.trapKindIndex = trapKindIndex;
        this.angbandDisplayCharacter = angbandDisplayCharacter;
        this.rarity = rarity;
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
        this.power = power;
        this.flags = flags;
        this.saveFlags = saveFlags;
        this.effect = effect;
        this.effectXtra = effectXtra;
    }

    /**
     * @return this trap type's human-readable description
     * @author ClaudeCode
     */
    public String getDescription() {
        return description;
    }

    /**
     * Look up a trap kind by its description via the global constants table.
     *
     * @param description the trap description to match
     * @return the matching trap kind
     * @author ClaudeCode
     */
    public static TrapKind lookupTrap(String description) {
        return GameConstants.lookupTrap(description);
    }

    /**
     * Value equality across all fields (two trap kinds are equal when every
     * descriptive and behavioural field matches).
     *
     * @param o the object to compare against
     * @return true if {@code o} is an equivalent {@code TrapKind}
     * @author ClaudeCode
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TrapKind trapKind = (TrapKind) o;
        return trapKindIndex == trapKind.trapKindIndex && rarity == trapKind.rarity && minDepth == trapKind.minDepth && maxDepth == trapKind.maxDepth && Objects.equals(trapKindName, trapKind.trapKindName) && Objects.equals(text, trapKind.text) && Objects.equals(getDescription(), trapKind.getDescription()) && Objects.equals(messageOnSave, trapKind.messageOnSave) && Objects.equals(messageOnFailure, trapKind.messageOnFailure) && Objects.equals(messageOnExtraEffect, trapKind.messageOnExtraEffect) && Objects.equals(angbandDisplayCharacter, trapKind.angbandDisplayCharacter) && Objects.equals(power, trapKind.power) && Objects.equals(flags, trapKind.flags) && Objects.equals(saveFlags, trapKind.saveFlags) && Objects.equals(effect, trapKind.effect) && Objects.equals(effectXtra, trapKind.effectXtra);
    }

    /**
     * Hash code consistent with {@link #equals(Object)}, combining all fields.
     *
     * @return this trap kind's hash code
     * @author ClaudeCode
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(trapKindName);
        result = 31 * result + Objects.hashCode(text);
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(messageOnSave);
        result = 31 * result + Objects.hashCode(messageOnFailure);
        result = 31 * result + Objects.hashCode(messageOnExtraEffect);
        result = 31 * result + trapKindIndex;
        result = 31 * result + Objects.hashCode(angbandDisplayCharacter);
        result = 31 * result + rarity;
        result = 31 * result + minDepth;
        result = 31 * result + maxDepth;
        result = 31 * result + Objects.hashCode(power);
        result = 31 * result + Objects.hashCode(flags);
        result = 31 * result + Objects.hashCode(saveFlags);
        result = 31 * result + Objects.hashCode(effect);
        result = 31 * result + Objects.hashCode(effectXtra);
        return result;
    }
}