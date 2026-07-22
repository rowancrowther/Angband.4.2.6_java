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

package uk.co.jackoftrades.middle.cave;

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The immutable template describing one type of trap (as loaded from
 * {@code trap.txt}) — its display glyph, depth/rarity, power, effect(s) and the
 * messages shown when it is saved against or triggers. Live traps on the map are
 * {@link Trap} instances referring back to a {@code TrapKind}. This is the Java
 * port of the C original's {@code struct trap_kind} ({@code src/trap.h}).
 *
 * @author Rowan Crowther
 */
public class TrapKind {
    /**
     * The trap type's internal name.
     *
     * @author Rowan Crowther
     */
    private String trapKindName;
    /**
     * The trap's display text/title.
     *
     * @author Rowan Crowther
     */
    private String text;
    /**
     * Human-readable description of the trap.
     *
     * @author Rowan Crowther
     */
    private String description;
    /**
     * Message shown to the player when the trap triggers (C {@code msg}, from the {@code msg:}
     * directive).
     *
     * @author Rowan Crowther
     */
    private String message;
    /**
     * Message shown when the player saves against (avoids) the trap.
     *
     * @author Rowan Crowther
     */
    private String messageOnSave;
    /**
     * Message shown when the player fails to avoid the trap.
     *
     * @author Rowan Crowther
     */
    private String messageOnFailure;
    /**
     * Message shown when the trap's extra effect fires.
     *
     * @author Rowan Crowther
     */
    private String messageOnExtraEffect;

    /**
     * This trap type's index in the global trap-kind table.
     *
     * @author Rowan Crowther
     */
    private int trapKindIndex;

    /**
     * The glyph and colour used to draw this trap.
     *
     * @author Rowan Crowther
     */
    private AngbandDisplayCharacter angbandDisplayCharacter;

    /**
     * Rarity weighting controlling how often this trap is chosen.
     *
     * @author Rowan Crowther
     */
    private int rarity;
    /**
     * Shallowest dungeon level this trap can appear on.
     *
     * @author Rowan Crowther
     */
    private int minDepth;
    /**
     * Maximum number of this trap allowed on a single level (C {@code max_num}). Unused by the
     * current game logic — kept for fidelity to the {@code appear:} line.
     *
     * @author Rowan Crowther
     */
    private int maxNum;
    /**
     * The trap's power, as a dice/random expression.
     *
     * @author Rowan Crowther
     */
    private Random power;

    /**
     * Behavioural flags for this trap type.
     *
     * @author Rowan Crowther
     */
    private Flag<TrapEnum> flags;
    /**
     * Object flags that grant a saving throw against this trap.
     *
     * @author Rowan Crowther
     */
    private Flag<ObjectFlag> saveFlags;

    /**
     * The primary effect applied when the trap triggers.
     *
     * @author Rowan Crowther
     */
    private List<Effect> effect;
    /**
     * An optional secondary ("extra") effect.
     *
     * @author Rowan Crowther
     */
    private List<Effect> effectXtra;

    /**
     * Build a trap-kind template from its parsed data-file fields.
     *
     * @param trapKindName            internal grouping name — C {@code name}, the {@code name:}
     *                                line's first field; not unique across traps
     * @param text                    flavour description — C {@code text}, from the {@code desc:}
     *                                directive
     * @param description             the trap's short description — C {@code desc}, the
     *                                {@code name:} line's second field and the {@code lookupTrap} key
     * @param message                 message shown when the trap triggers (the {@code msg:} line)
     * @param messageOnSave           message on a successful save
     * @param messageOnFailure        message on a failed save
     * @param messageOnExtraEffect    message when the extra effect fires
     * @param trapKindIndex           this trap's index in the trap-kind table (its file position)
     * @param angbandDisplayCharacter display glyph and colour
     * @param rarity                  rarity weighting
     * @param minDepth                shallowest level it appears on
     * @param maxNum                  maximum number allowed on a single level (C {@code max_num})
     * @param power                   power/visibility as a random expression
     * @param flags                   behavioural trap flags
     * @param saveFlags               object flags that grant the player a save
     * @param effect                  primary effect(s) run when the trap triggers
     * @param effectXtra              optional secondary effect(s), each with a 50% chance to fire
     * @author Rowan Crowther
     */
    public TrapKind(String trapKindName, String text, String description, String message,
                    String messageOnSave, String messageOnFailure,
                    String messageOnExtraEffect, int trapKindIndex,
                    AngbandDisplayCharacter angbandDisplayCharacter, int rarity, int minDepth, int maxNum,
                    Random power, Flag<TrapEnum> flags, Flag<ObjectFlag> saveFlags,
                    List<Effect> effect, List<Effect> effectXtra) {
        this.trapKindName = trapKindName;
        this.text = text;
        this.description = description;
        this.message = message;
        this.messageOnSave = messageOnSave;
        this.messageOnFailure = messageOnFailure;
        this.messageOnExtraEffect = messageOnExtraEffect;
        this.trapKindIndex = trapKindIndex;
        this.angbandDisplayCharacter = angbandDisplayCharacter;
        this.rarity = rarity;
        this.minDepth = minDepth;
        this.maxNum = maxNum;
        this.power = power;
        this.flags = flags;
        this.saveFlags = saveFlags;
        this.effect = effect;
        this.effectXtra = effectXtra;
    }

    /**
     * @return this trap type's human-readable description
     * @author Rowan Crowther
     */
    public String getDescription() {
        return description;
    }

    /**
     * Look up a trap kind by its description via the global constants table.
     *
     * @param description the trap description to match
     * @return the matching trap kind
     * @author Rowan Crowther
     */
    public static TrapKind lookupTrap(String description) {
        return GameConstants.lookupTrap(description);
    }

    /**
     * @return this trap type's index in the trap-kind table (its 0-based file position, C
     * {@code tidx})
     * @author Rowan Crowther
     */
    public int getTrapKindIndex() {
        return trapKindIndex;
    }

    /**
     * @return an unmodifiable view of this trap's secondary ("extra") effects — each has a 50%
     * chance of also firing when the trap triggers
     * @author Rowan Crowther
     */
    public List<Effect> getEffectXtra() {
        return Collections.unmodifiableList(effectXtra);
    }

    /**
     * @return an unmodifiable view of this trap's primary effects, run in order when it triggers
     * @author Rowan Crowther
     */
    public List<Effect> getEffect() {
        return Collections.unmodifiableList(effect);
    }

    /**
     * @return a defensive copy of this trap's behavioural flags; mutating it does not affect the kind
     * @author Rowan Crowther
     */
    public Flag<TrapEnum> getFlags() {
        return flags.copy();
    }

    /**
     * @return a defensive copy of the object flags that grant the player a saving throw against this
     * trap
     * @author Rowan Crowther
     */
    public Flag<ObjectFlag> getSaveFlags() {
        return saveFlags.copy();
    }

    /**
     * @return this trap's flavour description text (C {@code text}, from the {@code desc:} directive)
     * @author Rowan Crowther
     */
    public String getText() {
        return text;
    }

    /**
     * Value equality across all fields (two trap kinds are equal when every
     * descriptive and behavioural field matches).
     *
     * @param o the object to compare against
     * @return true if {@code o} is an equivalent {@code TrapKind}
     * @author Rowan Crowther
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TrapKind trapKind = (TrapKind) o;
        return trapKindIndex == trapKind.trapKindIndex && rarity == trapKind.rarity && minDepth == trapKind.minDepth
                && maxNum == trapKind.maxNum && Objects.equals(trapKindName, trapKind.trapKindName) && Objects.equals(text, trapKind.text) && Objects.equals(getDescription(), trapKind.getDescription()) && Objects.equals(messageOnSave, trapKind.messageOnSave) && Objects.equals(messageOnFailure, trapKind.messageOnFailure) && Objects.equals(messageOnExtraEffect, trapKind.messageOnExtraEffect) && Objects.equals(angbandDisplayCharacter, trapKind.angbandDisplayCharacter) && Objects.equals(power, trapKind.power) && Objects.equals(flags, trapKind.flags) && Objects.equals(saveFlags, trapKind.saveFlags) && Objects.equals(effect, trapKind.effect) && Objects.equals(effectXtra, trapKind.effectXtra);
    }

    /**
     * Hash code consistent with {@link #equals(Object)}, combining all fields.
     *
     * @return this trap kind's hash code
     * @author Rowan Crowther
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
        result = 31 * result + maxNum;
        result = 31 * result + Objects.hashCode(power);
        result = 31 * result + Objects.hashCode(flags);
        result = 31 * result + Objects.hashCode(saveFlags);
        result = 31 * result + Objects.hashCode(effect);
        result = 31 * result + Objects.hashCode(effectXtra);
        return result;
    }
}