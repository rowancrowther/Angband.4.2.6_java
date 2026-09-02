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
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
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
 * discriminator plus separate, correctly-typed carriers — {@link #pCode} for a player flag and
 * {@link #oCode} for an object flag (the element case is carried by {@link #value}). Exactly one
 * carrier is meaningful, per the discriminator.
 *
 * <p>Beyond the C struct the port also holds {@link #entries}: the resolved bindings from this
 * property to the {@link UIEntry} slots that display it (the {@code bindui:} lines), which is how a
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
    public record BindUI(UIEntry uiEntry, int value, boolean special, boolean aux) {
    }

    /**
     * Discriminator selecting which flavour of property (and which code carrier) is live.
     */
    private PlayerPropertyType playerPropertyType;
    /** Payload when {@link #playerPropertyType} is {@code PROP_TYPE_PLAYER}: the player flag. */
    private PlayerFlag pCode;
    /** Payload when {@link #playerPropertyType} is {@code PROP_TYPE_OBJECT}: the object flag. */
    private ObjectFlag oCode;
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
     * @param entries            the resolved UI bindings
     * @param name               display name
     * @param description        human-readable description
     * @param value              the resistance level (for element properties)
     */
    public PlayerProperty(PlayerPropertyType playerPropertyType,
                          PlayerFlag pCode,
                          ObjectFlag oCode,
                          List<BindUI> entries,
                          String name,
                          String description,
                          PlayerPropertyValue value) {
        this.playerPropertyType = playerPropertyType;
        this.oCode = oCode;
        this.pCode = pCode;
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
        PROP_TYPE_ELEMENT
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