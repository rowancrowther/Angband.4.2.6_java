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

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.middle.combat.enums.Element;
import uk.co.jackoftrades.middle.enums.StatElementEnum;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

public class PlayerProperty {
    private static final Logger logger = LogManager.getLogger();

    private PlayerPropertyType playerPropertyType;
    private PlayerFlag pCode;
    private ObjectFlag oCode;
    private UIEntry uiEntry;
    private StatElementEnum statElement;
    private Stats stats;
    private Element element;
    private boolean passType;
    private int passValue;
    private boolean special;
    private String name;
    private String description;
    private PlayerPropertyValue value;

    public PlayerProperty(PlayerPropertyType playerPropertyType,
                          PlayerFlag pCode,
                          ObjectFlag oCode,
                          UIEntry uiEntry,
                          Stats stats,
                          Element element,
                          String binduiExtras,
                          String name,
                          String description,
                          PlayerPropertyValue value) {
        this.playerPropertyType = playerPropertyType;
        this.oCode = oCode;
        this.pCode = pCode;
        this.uiEntry = uiEntry;
        this.stats = stats;
        this.element = element;
        parseBindUIExtras(binduiExtras);
        this.name = name;
        this.description = description;
        this.value = value;
    }

    // TODO: Sort this out
    private void parseBindUIExtras(String binduiExtras) {
//        // String is of format "':' (0 | 1) ':' (int | 'special')"
//        String toParse = binduiExtras.substring(1);
//        String[] split = toParse.split(":");
//        if (split.length != 2) {
//            IllegalArgumentException exception = new IllegalArgumentException("Invalid bindui extras: " + toParse);
//            logger.fatal(exception.getMessage(), exception);
//            throw exception;
//        }
//
//        passType = !(split[0].equals("0"));
//
//        if (split[1].equals("special")) {
//            special = true;
//            passValue = 0;
//        } else {
//            special = false;
//            passValue = Integer.parseInt(split[1]);
//        }
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