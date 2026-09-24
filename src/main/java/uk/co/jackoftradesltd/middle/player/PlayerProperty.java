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

package uk.co.jackoftradesltd.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.util.List;

/**
 * The definition of one player property — a named characteristic (a player flag, an object flag,
 * or an elemental resistance) together with how it is presented in the UI.
 *
 * <p>Ports the C {@code struct player_ability} ({@code player.h}), built from
 * {@code player_property.txt}. In C a single {@code index} field is a hand-rolled union holding a
 * {@code PF_*}, {@code OF_*} or element index, disambiguated by the {@code type} string. Because
 * Java has no unions, the port splits that one field into a {@link #playerPropertyType}
 * discriminator plus separate, correctly-typed carriers — {@link #pCode} for a player flag,
 * {@link #oCode} for an object flag, and {@link #eCode} for an element. {@link #omFlag} is the
 * port's own addition, the carrier for {@link PlayerPropertyType#PROP_TYPE_OBJECT_MODIFIER} — see
 * that constant's own documentation for why {@code player_property.txt} never actually produces one.
 * Exactly one carrier is meaningful, per the discriminator. {@link #value} is unrelated to the
 * union — it is C's separate {@code player_ability.value} field, the resistance level an element
 * property confers.
 *
 * <p>Beyond the C struct the port also holds {@link #entries}: the resolved bindings from this
 * property to the {@code UIEntry} slots that display it (the {@code bindui:} lines), which is how a
 * property surfaces on the character screen.
 *
 * @author Rowan Crowther
 */
public class PlayerProperty {
    /**
     * Logger for this type.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * One binding of a player property to a UI display slot (a {@code bindui:} line).
     *
     * @param uiEntry the UI entry this property is displayed in
     * @param value   the value threshold associated with the binding
     * @param special whether this is a "special" binding variant
     * @param aux     whether this is an auxiliary binding variant
     * @author Rowan Crowther
     */
    public record BindUI(String uiEntry, int value, boolean special, boolean aux) {
    }

    /**
     * Discriminator selecting which flavour of property (and which code carrier) is live.
     */
    private PlayerPropertyType playerPropertyType;
    /** Payload when {@link #playerPropertyType} is {@code PROP_TYPE_PLAYER}: the player flag. */
    private PlayerFlag pCode;
    /** Payload when {@link #playerPropertyType} is {@code PROP_TYPE_OBJECT}: the object flag. */
    private ObjectFlag oCode;
    /**
     * Payload when {@link #playerPropertyType} is {@code PROP_TYPE_ELEMENT}: the element code.
     */
    private ElementEnum eCode;
    /**
     * Payload when {@link #playerPropertyType} is {@code PROP_TYPE_OBJECT_MODIFIER}: the object modifier.
     */
    private ObjectModifier omFlag;
    /** Resolved bindings from this property to the UI slots that display it (the {@code bindui:} lines). */
    private List<BindUI> entries;
    /** Display name of the property (C: {@code player_ability.name}). */
    private String name;
    /** Human-readable description of the property (C: {@code player_ability.desc}). */
    private String description;
    /** For an element property, the resistance level it confers (C: {@code player_ability.value}). */
    private PlayerPropertyValue value;

    /**
     * Builds a fully-resolved player property, as produced by the property reader/assembler from one
     * {@code player_property.txt} record.
     *
     * @param playerPropertyType the property flavour / code discriminator
     * @param pCode              the player flag (for {@code PROP_TYPE_PLAYER}; otherwise {@code null})
     * @param oCode              the object flag (for {@code PROP_TYPE_OBJECT}; otherwise {@code null})
     * @param eCode              the element (for {@code PROP_TYPE_ELEMENT}; otherwise {@code null})
     * @param omFlag             the object modifier (for {@code PROP_TYPE_OBJECT_MODIFIER}; otherwise
     *                           {@code null})
     * @param entries            the resolved UI bindings
     * @param name               display name
     * @param description        human-readable description
     * @param value              the resistance level (for element properties)
     */
    public PlayerProperty(PlayerPropertyType playerPropertyType,
                          PlayerFlag pCode,
                          ObjectFlag oCode,
                          ElementEnum eCode,
                          ObjectModifier omFlag,
                          List<BindUI> entries,
                          String name,
                          String description,
                          PlayerPropertyValue value) {
        this.playerPropertyType = playerPropertyType;
        this.oCode = oCode;
        this.pCode = pCode;
        this.eCode = eCode;
        this.omFlag = omFlag;
        this.entries = entries;
        this.name = name;
        this.description = description;
        this.value = value;
    }

    /**
     * @return the property flavour / code discriminator
     */
    public PlayerPropertyType getPlayerPropertyType() {
        return playerPropertyType;
    }

    /**
     * @return the player flag this property carries (meaningful for {@code PROP_TYPE_PLAYER})
     */
    public PlayerFlag getpCode() {
        return pCode;
    }

    /**
     * @return the object flag this property carries (meaningful for {@code PROP_TYPE_OBJECT})
     */
    public ObjectFlag getoCode() {
        return oCode;
    }

    /**
     * @return the element code this property carries (meaningful for {@code PROP_TYPE_ELEMENT})
     */
    public ElementEnum geteCode() {
        return eCode;
    }

    /**
     * @return the object modifier this property carries (meaningful for
     * {@code PROP_TYPE_OBJECT_MODIFIER})
     */
    public ObjectModifier getomCode() {
        return omFlag;
    }
    
    /**
     * @return the resolved UI bindings that display this property
     */
    public List<BindUI> getEntries() {
        return entries;
    }

    /**
     * @return the property's display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the property's human-readable description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the resistance level this property confers (meaningful for element properties)
     */
    public PlayerPropertyValue getValue() {
        return value;
    }

    /**
     * The flavour of a player property, discriminating which code carrier is live and how the
     * property is interpreted (C: the {@code type} string of {@code player_ability}).
     *
     * @author Rowan Crowther
     */
    public enum PlayerPropertyType {
        /** A player (class/race) flag property; the {@link #getpCode()} carrier is live. */
        PROP_TYPE_PLAYER,
        /** An object flag property; the {@link #getoCode()} carrier is live. */
        PROP_TYPE_OBJECT,
        /** An elemental resistance property; the {@link #getValue()} level is live. */
        PROP_TYPE_ELEMENT,

        /**
         * Reserved, currently unproduced. No {@code player_property.txt} record ever assembles to
         * this member — {@link uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyAssembler}
         * only ever builds {@code PROP_TYPE_PLAYER}, {@code PROP_TYPE_OBJECT}, {@code PROP_TYPE_ELEMENT}
         * or {@link #PROP_TYPE_OBJECT_MODIFIER}.
         */
        PROP_TYPE_PROPERTY,

        /**
         * An object-modifier property; the {@link #getomCode()} carrier is live. C's
         * {@code type} string has no member that maps here — this is what the assembler falls back to
         * for a {@code type:} value it does not otherwise recognise ({@code player}/{@code object}/
         * {@code element}), which today's {@code player_property.txt} never contains. Wherever this
         * type does occur, {@code UIEntryValueRegistry} does not dispatch on it and so contributes
         * nothing for it, mirroring C's switch having no default case for an unrecognised
         * {@code player_ability.type}.
         */
        PROP_TYPE_OBJECT_MODIFIER
    }

    /**
     * The resistance level an element property confers, from vulnerability through to immunity.
     *
     * @author Rowan Crowther
     */
    public enum PlayerPropertyValue {
        /** No resistance modifier. */
        NONE,
        /** Takes extra damage from the element. */
        VULNERABILITY,
        /** Takes reduced damage from the element. */
        RESISTANCE,
        /** Takes no damage from the element. */
        IMMUNITY
    }
}