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

package uk.co.jackoftrades.middle.objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.Map;

/**
 * A generic object property definition (as loaded from {@code object_property.txt})
 * — describing one property an object can have (a flag, stat/modifier, or element
 * relation, captured by {@link ObjectPropertyTypeWrapper}) along with its power,
 * value multipliers, the adjectives/messages used to describe it, and the UI
 * entries it binds to. This is the Java port of the C original's
 * {@code struct obj_property} ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class ObjectProperty {
    /**
     * Logger (reserved for diagnostics).
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The property's category.
     *
     * @author Rowan Crowther
     */
    private ObjPropertyType type;
    /**
     * The property's sub-type identifier.
     *
     * @author Rowan Crowther
     */
    private String subtype;
    /**
     * How this property is identified by the player.
     *
     * @author Rowan Crowther
     */
    private String idType;
    /**
     * The typed payload (flag/modifier/element) this property represents.
     *
     * @author Rowan Crowther
     */
    private ObjectPropertyTypeWrapper index;
    /**
     * The property's base power (for item valuation).
     *
     * @author Rowan Crowther
     */
    private int power;
    /**
     * The property's value multiplier.
     *
     * @author Rowan Crowther
     */
    private int mult;
    /**
     * Per-item-type multipliers applied to the property's value.
     *
     * @author Rowan Crowther
     */
    private Map<TValue, Integer> typeMults;
    /**
     * The property's name.
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * Adjective describing the positive form of the property.
     *
     * @author Rowan Crowther
     */
    private String adjective;
    /**
     * Adjective describing the negative form of the property.
     *
     * @author Rowan Crowther
     */
    private String negAdjective;
    /**
     * Message shown when the property is noticed.
     *
     * @author Rowan Crowther
     */
    private String message;
    /**
     * Human-readable description of the property.
     *
     * @author Rowan Crowther
     */
    private String description;
    /**
     * The UI entries this property contributes to, with their payloads.
     *
     * @author Rowan Crowther
     */
    private Map<UIEntry, ObjectPropertyTypeWrapper> boundEntries;

    /**
     * Build an object property from its parsed data-file fields.
     *
     * @param type         property category
     * @param subtype      sub-type identifier
     * @param idType       identification method
     * @param index        typed payload
     * @param power        base power
     * @param mult         value multiplier
     * @param typeMults    per-type value multipliers
     * @param name         property name
     * @param adjective    positive-form adjective
     * @param negAdjective negative-form adjective
     * @param message      notice message
     * @param description  description
     * @param boundEntries bound UI entries
     * @author Rowan Crowther
     */
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