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

package uk.co.jackoftrades.middle.monsters;

import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

/**
 * One power tier of a monster spell: the minimum power at which it applies, the
 * lore description and its colours (normal / when resisted / when immune), and
 * the messages shown when it is cast (seen, blind, miss, save). This is the Java
 * port of the C original's {@code struct monster_spell_level} ({@code src/mon-spell.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterSpellLevel {
    /**
     * The minimum spell power at which this level applies.
     *
     * @author Rowan Crowther
     */
    private int power;
    /**
     * Lore description of the spell at this level.
     *
     * @author Rowan Crowther
     */
    private String loreDesc;
    /**
     * Colour used for the lore entry normally.
     *
     * @author Rowan Crowther
     */
    private ColourType loreAttr;
    /**
     * Colour used for the lore entry when the player resists.
     *
     * @author Rowan Crowther
     */
    private ColourType loreAttrResist;
    /**
     * Colour used for the lore entry when the player is immune.
     *
     * @author Rowan Crowther
     */
    private ColourType loreAttrImmune;
    /**
     * Message shown when the spell is cast and seen.
     *
     * @author Rowan Crowther
     */
    String message;
    /**
     * Message shown when the spell is cast and the player is blind.
     *
     * @author Rowan Crowther
     */
    String blindMessage;
    /**
     * Message shown when the spell misses.
     *
     * @author Rowan Crowther
     */
    String missMessage;
    /**
     * Message shown when the player saves against the spell.
     *
     * @author Rowan Crowther
     */
    String saveMessage;

    /**
     * Build a spell-level descriptor from its parsed fields.
     *
     * @param power          minimum power for this level
     * @param loreDesc       lore description
     * @param loreAttr       normal lore colour
     * @param loreAttrResist resisted lore colour
     * @param loreAttrImmune  immune lore colour
     * @param message        seen-cast message
     * @param blindMessage   blind-cast message
     * @param missMessage    miss message
     * @param saveMessage    save message
     * @author Rowan Crowther
     */
    @Contract(mutates = "this")
    public MonsterSpellLevel(int power, String loreDesc, ColourType loreAttr, ColourType loreAttrResist,
                             ColourType loreAttrImmune, String message, String blindMessage, String missMessage,
                             String saveMessage) {
        this.power = power;
        this.loreDesc = loreDesc;
        this.loreAttr = loreAttr;
        this.loreAttrResist = loreAttrResist;
        this.loreAttrImmune = loreAttrImmune;
        this.message = message;
        this.blindMessage = blindMessage;
        this.missMessage = missMessage;
        this.saveMessage = saveMessage;
    }
}
