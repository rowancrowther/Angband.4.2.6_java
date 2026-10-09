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

package uk.co.jackoftradesltd.middle.cave;

import uk.co.jackoftradesltd.middle.game.globals.registry.DungeonRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * The immutable template describing one type of trap (as loaded from
 * {@code trap.txt}) — its display glyph, depth/rarity, power, effect(s) and the
 * messages shown when it is saved against or triggers. Live traps on the map are
 * {@link Trap} instances referring back to a {@code TrapKind}. This is the Java
 * port of the C original's {@code struct trap_kind} ({@code trap.h}).
 *
 * <p>The C {@code next} link is dropped (kinds live in a {@code List}), and {@code d_attr}/{@code d_char} are
 * folded into one {@link AngbandDisplayCharacter}. As in C, index 0 of the trap-kind table is the real
 * {@code no trap} kind from the first record of {@code trap.txt}, not padding, and {@code lookup_trap()} skips it by
 * position.
 *
 * <p>Class coded before 260930, commented in full on 260930, index-0 note corrected on 261009.
 *
 * @author Rowan Crowther
 */
public class TrapKind {
    /**
     * The trap type's internal grouping name (C {@code name}, first field of the {@code name:} line). Not
     * unique across kinds; a {@code null} name marks an unused table slot and makes {@link #lookupTrap} skip it.
     */
    private String trapKindName;
    /**
     * The trap's flavour text (C {@code text}, from the {@code desc:} directive).
     */
    private String text;
    /**
     * Short description (C {@code desc}, second field of the {@code name:} line). This is the key
     * {@link #lookupTrap} matches against.
     */
    private String description;
    /**
     * Message shown to the player when the trap triggers (C {@code msg}, from the {@code msg:}
     * directive).
     */
    private String message;
    /**
     * Message shown when the player saves against (avoids) the trap.
     */
    private String messageOnSave;
    /**
     * Message shown when the player fails to avoid the trap.
     */
    private String messageOnFailure;
    /**
     * Message shown when the trap's extra effect fires.
     */
    private String messageOnExtraEffect;

    /**
     * This trap type's index in the global trap-kind table.
     */
    private int trapKindIndex;

    /**
     * The glyph and colour used to draw this trap.
     */
    private AngbandDisplayCharacter angbandDisplayCharacter;

    /**
     * Rarity weighting controlling how often this trap is chosen.
     */
    private int rarity;
    /**
     * Shallowest dungeon level this trap can appear on.
     */
    private int minDepth;
    /**
     * Maximum number of this trap allowed on a single level (C {@code max_num}). Unused by the
     * current game logic — kept for fidelity to the {@code appear:} line.
     */
    private int maxNum;
    /**
     * The trap's power (C {@code power}, "visibility of player trap"), as a dice/random expression.
     */
    private Random power;

    /**
     * Behavioural flags for this trap type.
     */
    private Flag<TrapEnum> flags;
    /**
     * Object flags that grant a saving throw against this trap.
     */
    private Flag<ObjectFlag> saveFlags;

    /**
     * The primary effect applied when the trap triggers.
     */
    private List<Effect> effect;
    /**
     * An optional secondary ("extra") effect.
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
     * @param trapKindIndex           this trap's index in the trap-kind table (its position among the records that assemble)
     * @param angbandDisplayCharacter display glyph and colour
     * @param rarity                  rarity weighting
     * @param minDepth                shallowest level it appears on
     * @param maxNum                  maximum number allowed on a single level (C {@code max_num})
     * @param power                   power/visibility as a random expression
     * @param flags                   behavioural trap flags
     * @param saveFlags               object flags that grant the player a save
     * @param effect                  primary effect(s) run when the trap triggers
     * @param effectXtra              optional secondary effect(s), each with a 50% chance to fire
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
     * Finds a trap kind from its short description, the Java form of {@code lookup_trap()} in {@code trap.c}. It
     * delegates to {@link TerrainRegistry#lookupTrap}, which walks the kinds in table order, skipping the kind at
     * index 0 (the {@code no trap} kind) and any kind with a {@code null} name. The first kind whose description
     * equals the argument exactly (case-sensitive) wins immediately; failing that, the first kind whose description
     * contains the argument case-insensitively (C {@code my_stristr}) is returned. An empty string therefore returns
     * the kind at index 1, and an argument with no match, or {@code "no trap"}, returns {@code null}.
     *
     * <p>Function lookupTrap coded before 260930, commented in full on 260930, delegation to the registry and
     * index-0 note updated on 261009.
     *
     * @param description the trap description to match, exactly or as a substring
     * @return the matching trap kind, or {@code null} if nothing matches
     */
    public static TrapKind lookupTrap(String description) {
        return TerrainRegistry.lookupTrap(description);
    }

    /**
     * Returns the short description, the key {@link #lookupTrap} matches on.
     *
     * <p>Function getDescription coded before 260930, commented in full on 260930.
     *
     * @return this trap type's short description (C {@code desc})
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the internal grouping name (C {@code name}); {@code null} for an unused table slot.
     *
     * <p>Function getTrapKindName coded before 260930, commented in full on 260930.
     *
     * @return this trap type's internal name
     */
    public String getTrapKindName() {
        return trapKindName;
    }

    /**
     * Returns this kind's index in the trap-kind table (C {@code tidx}). It is the 0-based position among the
     * records that assemble, so it matches the kind's place in {@link TerrainRegistry#getTrapKinds()}. Index 0 is
     * normally the {@code no trap} kind, which {@link #lookupTrap} never returns.
     *
     * <p>Function getTrapKindIndex coded before 261009, commented in full on 261009.
     *
     * @return this trap type's index in the trap-kind table
     */
    public int getTrapKindIndex() {
        return trapKindIndex;
    }

    /**
     * @return an unmodifiable view of this trap's secondary ("extra") effects — each has a 50%
     * chance of also firing when the trap triggers
     */
    public List<Effect> getEffectXtra() {
        return Collections.unmodifiableList(effectXtra);
    }

    /**
     * @return an unmodifiable view of this trap's primary effects, run in order when it triggers
     */
    public List<Effect> getEffect() {
        return Collections.unmodifiableList(effect);
    }

    /**
     * @return a defensive copy of this trap's behavioural flags; mutating it does not affect the kind
     */
    public Flag<TrapEnum> getFlags() {
        Flag<TrapEnum> flag = new Flag<>(TrapEnum.class);
        flag.copyFrom(flags);
        return flag;
    }

    /**
     * @return a defensive copy of the object flags that grant the player a saving throw against this
     * trap
     */
    public Flag<ObjectFlag> getSaveFlags() {
        Flag<ObjectFlag> flag = new Flag<>(ObjectFlag.class);
        flag.copyFrom(saveFlags);
        return flag;
    }

    /**
     * @return this trap's flavour description text (C {@code text}, from the {@code desc:} directive)
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
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TrapKind trapKind = (TrapKind) o;
        return trapKindIndex == trapKind.trapKindIndex && rarity == trapKind.rarity && minDepth == trapKind.minDepth
                && maxNum == trapKind.maxNum && Objects.equals(trapKindName, trapKind.trapKindName)
                && Objects.equals(text, trapKind.text) && Objects.equals(getDescription(), trapKind.getDescription())
                && Objects.equals(messageOnSave, trapKind.messageOnSave)
                && Objects.equals(messageOnFailure, trapKind.messageOnFailure)
                && Objects.equals(messageOnExtraEffect, trapKind.messageOnExtraEffect)
                && Objects.equals(angbandDisplayCharacter, trapKind.angbandDisplayCharacter)
                && Objects.equals(power, trapKind.power) && Objects.equals(flags, trapKind.flags)
                && Objects.equals(saveFlags, trapKind.saveFlags) && Objects.equals(effect, trapKind.effect)
                && Objects.equals(effectXtra, trapKind.effectXtra)
                && Objects.equals(message, trapKind.message);
    }

    /**
     * Hash code consistent with {@link #equals(Object)}, combining all fields.
     *
     * @return this trap kind's hash code
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(trapKindName);
        result = 31 * result + Objects.hashCode(text);
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + Objects.hashCode(message);
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