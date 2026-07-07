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

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import java.util.List;

public class PlayerProperty {
    private static final Logger logger = LogManager.getLogger();

    public record BindUI(UIEntry uiEntry, int value, boolean special, boolean aux) {
    }

    private PlayerPropertyType playerPropertyType;
    private PlayerFlag pCode;
    private ObjectFlag oCode;
    private List<BindUI> entries;
    private String name;
    private String description;
    private PlayerPropertyValue value;

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

    public PlayerPropertyType getPlayerPropertyType() {
        return playerPropertyType;
    }

    public PlayerFlag getpCode() {
        return pCode;
    }

    public ObjectFlag getoCode() {
        return oCode;
    }

    public List<BindUI> getEntries() {
        return entries;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PlayerPropertyValue getValue() {
        return value;
    }

    public enum PlayerPropertyType {
        PROP_TYPE_PLAYER,
        PROP_TYPE_OBJECT,
        PROP_TYPE_ELEMENT
    }

    public enum PlayerPropertyValue {
        NONE,
        VULNERABILITY,
        RESISTANCE,
        IMMUNITY
    }
}