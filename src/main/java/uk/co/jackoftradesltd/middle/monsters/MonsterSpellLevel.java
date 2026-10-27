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

package uk.co.jackoftradesltd.middle.monsters;

import org.jetbrains.annotations.Contract;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;

/**
 * One power tier of a monster spell: the minimum power at which it applies, the
 * lore description and its colours (normal / when resisted / when immune), and
 * the messages shown when it is cast (seen, blind, miss, save). This is the Java
 * port of the C original's {@code struct monster_spell_level} in {@code monster.h}.
 * <p>
 * C links the tiers of a spell through a {@code next} pointer, so a spell owns a chain with its
 * level 0 at the head. Java has no {@code next}: the tiers live in the {@code levels} list of
 * {@link MonsterSpellType}, in the same order, and {@code MonsterSpellLevelAssembler} builds
 * them. C's parser edits the last tier of the chain in place as it reads {@code lore:} and
 * {@code message-*:} lines, whereas the assembler replaces the last tier with a freshly built one,
 * which is why no field has a setter.
 * <p>
 * The consumers in {@code mon-spell.c} ({@code spell_message()}, {@code do_mon_spell()} and
 * {@code mon_spell_lore_description()}) start at the head and step to {@code next} while the
 * monster's {@code spell_power} is at least the {@code power} of the following tier, so the tier
 * used is the last one whose power does not exceed it. The power of the head tier is never
 * compared. Nothing in Java consumes the tiers yet.
 * <p>
 * Absent text is {@code null}, as C's unset {@code char *}, and an unset colour is
 * {@link ColourEnum#COLOUR_DARK}, as C's zeroed {@code uint8_t}.
 *
 * <p>Class MonsterSpellLevel coded before 260915, commented in full on 261027.
 *
 * @author Rowan Crowther
 */
public class MonsterSpellLevel {
    /**
     * The minimum spell power at which this level applies; C's {@code power}, "spell power at
     * which this level starts". The head tier of a spell is 0 and its value is never compared.
     *
     * <p>Field power coded before 260915, commented in full on 261027.
     */
    private int power;
    /**
     * Lore description of the spell at this level; C's {@code lore_desc}. {@code null} when unset.
     *
     * <p>Field loreDesc coded before 260915, commented in full on 261027.
     */
    private String loreDesc;
    /**
     * Colour used for the lore entry normally; C's {@code lore_attr}. {@link ColourEnum#COLOUR_DARK}
     * when no {@code lore-color-base:} line named one.
     *
     * <p>Field loreAttr coded before 260915, commented in full on 261027.
     */
    private ColourEnum loreAttr;
    /**
     * Colour used for the lore entry when the player resists; C's {@code lore_attr_resist}.
     * {@link ColourEnum#COLOUR_DARK} when unset.
     *
     * <p>Field loreAttrResist coded before 260915, commented in full on 261027.
     */
    private ColourEnum loreAttrResist;
    /**
     * Colour used for the lore entry when the player is immune ("resisted strongly" in C's
     * comment); C's {@code lore_attr_immune}. {@link ColourEnum#COLOUR_DARK} when unset.
     *
     * <p>Field loreAttrImmune coded before 260915, commented in full on 261027.
     */
    private ColourEnum loreAttrImmune;
    /**
     * Message shown when the spell is cast and seen; C's {@code message}. {@code null} when unset.
     * Package-private, unlike the five fields above; the public getter is the access route.
     *
     * <p>Field message coded before 260915, commented in full on 261027.
     */
    private String message;
    /**
     * Message shown when the spell is cast and the caster is unseen; C's {@code blind_message},
     * "description of the attack if unseen". {@code null} when unset. Package-private.
     *
     * <p>Field blindMessage coded before 260915, commented in full on 261027.
     */
    private String blindMessage;
    /**
     * Message shown when the spell misses; C's {@code miss_message}. {@code null} when unset.
     * Package-private.
     *
     * <p>Field missMessage coded before 260915, commented in full on 261027.
     */
    private String missMessage;
    /**
     * Message shown when the player passes a saving throw against the spell; C's
     * {@code save_message}. {@code null} when the spell has none, and C only attempts the
     * save when it is non-null. Package-private.
     *
     * <p>Field saveMessage coded before 260915, commented in full on 261027.
     */
    private String saveMessage;

    /**
     * Build a spell-level descriptor from its parsed fields, one argument per member of C's
     * {@code struct monster_spell_level} other than {@code next}. Nothing is validated or copied:
     * text may be {@code null} and no check is made that powers rise from one tier to the next.
     *
     * <p>Constructor MonsterSpellLevel coded before 260915, commented in full on 261027.
     *
     * @param power          minimum power for this level
     * @param loreDesc       lore description, or {@code null}
     * @param loreAttr       normal lore colour
     * @param loreAttrResist resisted lore colour
     * @param loreAttrImmune immune lore colour
     * @param message        seen-cast message, or {@code null}
     * @param blindMessage   unseen-cast message, or {@code null}
     * @param missMessage    miss message, or {@code null}
     * @param saveMessage    save message, or {@code null}
     */
    @Contract(mutates = "this")
    public MonsterSpellLevel(int power, String loreDesc, ColourEnum loreAttr, ColourEnum loreAttrResist,
                             ColourEnum loreAttrImmune, String message, String blindMessage, String missMessage,
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

    /**
     * The message for a cast the player cannot see, C's {@code blind_message}.
     *
     * <p>Function getBlindMessage coded before 260915, commented in full on 261027.
     *
     * @return the unseen-cast message, or {@code null} when unset
     */
    public String getBlindMessage() {
        return blindMessage;
    }

    /**
     * The normal lore colour, C's {@code lore_attr}.
     *
     * <p>Function getLoreAttr coded before 260915, commented in full on 261027.
     *
     * @return the colour, {@link ColourEnum#COLOUR_DARK} when none was named
     */
    public ColourEnum getLoreAttr() {
        return loreAttr;
    }

    /**
     * The lore colour used when the player is immune, C's {@code lore_attr_immune}.
     *
     * <p>Function getLoreAttrImmune coded before 260915, commented in full on 261027.
     *
     * @return the colour, {@link ColourEnum#COLOUR_DARK} when none was named
     */
    public ColourEnum getLoreAttrImmune() {
        return loreAttrImmune;
    }

    /**
     * The lore colour used when the player resists, C's {@code lore_attr_resist}.
     *
     * <p>Function getLoreAttrResist coded before 260915, commented in full on 261027.
     *
     * @return the colour, {@link ColourEnum#COLOUR_DARK} when none was named
     */
    public ColourEnum getLoreAttrResist() {
        return loreAttrResist;
    }

    /**
     * The lore description of the spell at this tier, C's {@code lore_desc}, which
     * {@code mon_spell_lore_description()} in {@code mon-spell.c} returns for the tier that
     * matches a race's spell power.
     *
     * <p>Function getLoreDesc coded before 260915, commented in full on 261027.
     *
     * @return the description, or {@code null} when unset
     */
    public String getLoreDesc() {
        return loreDesc;
    }

    /**
     * The message for a cast the player sees, C's {@code message}.
     *
     * <p>Function getMessage coded before 260915, commented in full on 261027.
     *
     * @return the seen-cast message, or {@code null} when unset
     */
    public String getMessage() {
        return message;
    }

    /**
     * The message for a spell that misses, C's {@code miss_message}.
     *
     * <p>Function getMissMessage coded before 260915, commented in full on 261027.
     *
     * @return the miss message, or {@code null} when unset
     */
    public String getMissMessage() {
        return missMessage;
    }

    /**
     * The spell power at which this tier starts, C's {@code power}. A caller choosing a tier
     * compares a race's {@code spell_power} against this with {@code >=}, so a tier applies at
     * exactly its own power.
     *
     * <p>Function getPower coded before 260915, commented in full on 261027.
     *
     * @return the starting power; 0 for the head tier
     */
    public int getPower() {
        return power;
    }

    /**
     * The message for passing a saving throw, C's {@code save_message}. {@code do_mon_spell()} in
     * {@code mon-spell.c} only rolls the save when this is non-null.
     *
     * <p>Function getSaveMessage coded before 260915, commented in full on 261027.
     *
     * @return the save message, or {@code null} when the spell allows no save
     */
    public String getSaveMessage() {
        return saveMessage;
    }
}
