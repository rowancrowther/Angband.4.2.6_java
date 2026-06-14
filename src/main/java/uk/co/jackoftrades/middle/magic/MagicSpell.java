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

public class MagicSpell {
    private String spellName;
    private String spellDescription;

    private MagicRealm realm;
    private List<Effect> effects;

    private int level;
    private int mana;
    private int fail;
    private int exp;

    public MagicSpell(String spellName, int level, int fail, int mana, int exp) {
        this.spellName = spellName;
        this.level = level;
        this.fail = fail;
        this.mana = mana;
        this.exp = exp;
        effects = new ArrayList<>();
    }

    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    public void setSpellDescription(String spellDescription) {
        this.spellDescription = spellDescription;
    }

    public void setRealm(MagicRealm realm) {
        this.realm = realm;
    }
}
