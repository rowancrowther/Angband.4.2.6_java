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

package uk.co.jackoftrades.middle.objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.Map;

public class ObjectProperty {
    private static final Logger logger = LogManager.getLogger();

    private ObjPropertyType type;
    private String subtype;
    private String idType;
    private ObjectPropertyTypeWrapper index;
    private int power;
    private int mult;
    private Map<TValue, Integer> typeMults;
    private String name;
    private String adjective;
    private String negAdjective;
    private String message;
    private String description;
    private Map<UIEntry, ObjectPropertyTypeWrapper> boundEntries;

    public ObjectProperty(ObjPropertyType type, String subtype, String idType, ObjectPropertyTypeWrapper index,
                          int power, int mult, Map<TValue, Integer> typeMults, String name, String adjective,
                          String negAdjective, String message, String description, Map<UIEntry, ObjectPropertyTypeWrapper> boundEntries) {
        this.type = type;
        this.subtype = subtype;
        this.idType = idType;
        this.index = index;
        this.power = power;
        this.mult = mult;
        this.typeMults = typeMults;
        this.name = name;
        this.adjective = adjective;
        this.negAdjective = negAdjective;
        this.message = message;
        this.description = description;
        this.boundEntries = boundEntries;
    }
}