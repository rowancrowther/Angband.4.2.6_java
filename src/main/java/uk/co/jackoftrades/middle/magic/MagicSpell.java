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

package uk.co.jackoftrades.middle.magic;

import uk.co.jackoftrades.middle.effect.Effect;

import java.util.ArrayList;
import java.util.List;

/**
 * A single spell: its identity, the {@link MagicRealm} it belongs to, the
 * {@link Effect}s it produces when cast, and its casting parameters (required
 * level, mana cost, base failure rate and the experience awarded for first
 * casting it). This is the Java port of the C original's {@code struct class_spell}
 * ({@code src/player.h}).
 *
 * @author ClaudeCode
 */
public class MagicSpell {
    /**
     * The spell's name.
     *
     * @author ClaudeCode
     */
    private String spellName;
    /**
     * Human-readable description of the spell's effect.
     *
     * @author ClaudeCode
     */
    private String spellDescription;

    /**
     * The magic realm this spell belongs to.
     *
     * @author ClaudeCode
     */
    private MagicRealm realm;
    /**
     * The effects produced when the spell is cast.
     *
     * @author ClaudeCode
     */
    private List<Effect> effects;

    /**
     * Minimum class level required to learn/cast the spell.
     *
     * @author ClaudeCode
     */
    private int level;
    /**
     * Mana cost to cast the spell.
     *
     * @author ClaudeCode
     */
    private int mana;
    /**
     * Base failure rate (percent) before stat/level adjustment.
     *
     * @author ClaudeCode
     */
    private int fail;
    /**
     * Experience awarded the first time the spell is cast.
     *
     * @author ClaudeCode
     */
    private int exp;

    /**
     * Build a spell with its core casting parameters; effects are added later via
     * {@link #addEffect(Effect)}.
     *
     * @param spellName the spell's name
     * @param level     required level
     * @param fail      base failure rate
     * @param mana      mana cost
     * @param exp       first-cast experience
     * @author ClaudeCode
     */
    public MagicSpell(String spellName, int level, int fail, int mana, int exp) {
        this.spellName = spellName;
        this.level = level;
        this.fail = fail;
        this.mana = mana;
        this.exp = exp;
        effects = new ArrayList<>();
    }

    /**
     * Append an effect to this spell.
     *
     * @param effect the effect to add
     * @author ClaudeCode
     */
    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    /**
     * Set the spell's description.
     *
     * @param spellDescription the description text
     * @author ClaudeCode
     */
    public void setSpellDescription(String spellDescription) {
        this.spellDescription = spellDescription;
    }

    /**
     * Set the magic realm this spell belongs to.
     *
     * @param realm the magic realm
     * @author ClaudeCode
     */
    public void setRealm(MagicRealm realm) {
        this.realm = realm;
    }
}
