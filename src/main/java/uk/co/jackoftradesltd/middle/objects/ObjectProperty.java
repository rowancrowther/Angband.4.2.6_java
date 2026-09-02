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

package uk.co.jackoftradesltd.middle.objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagID;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.List;
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
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * One binding of this property to a UI display slot (a {@code bindui:} line).
     *
     * @param entry the UI entry this property is displayed in
     * @param value the value threshold associated with the binding, or {@code null} if unbounded
     * @param aux   whether this is an auxiliary binding variant
     * @author Rowan Crowther
     */
    public record UIBinding(UIEntry entry, @Nullable Integer value, boolean aux) {
    }

    /**
     * The property's category.
     */
    private ObjPropertyType type;
    /**
     * The property's sub-type identifier.
     */
    private ObjectFlagType subtype;
    /**
     * How this property is identified by the player.
     */
    private ObjectFlagID idType;
    /**
     * The typed payload (flag/modifier/element) this property represents.
     */
    private ObjectPropertyTypeWrapper payload;
    /**
     * The property's base power (for item valuation).
     */
    private int power;
    /**
     * The property's value multiplier.
     */
    private int mult;
    /**
     * Per-item-type multipliers applied to the property's value.
     */
    private Map<TValue, Integer> typeMults;
    /**
     * The property's name.
     */
    private String name;
    /**
     * Adjective describing the positive form of the property.
     */
    private String adjective;
    /**
     * Adjective describing the negative form of the property.
     */
    private String negAdjective;
    /**
     * Message shown when the property is noticed.
     */
    private String message;
    /**
     * Human-readable description of the property.
     */
    private String description;
    /**
     * The UI entries this property contributes to, with their payloads.
     */
    private List<UIBinding> boundEntries;

    /**
     * Build an object property from its parsed data-file fields.
     *
     * @param type         property category
     * @param subtype      sub-type identifier
     * @param idType       identification method
     * @param payload        typed payload
     * @param power        base power
     * @param mult         value multiplier
     * @param typeMults    per-type value multipliers
     * @param name         property name
     * @param adjective    positive-form adjective
     * @param negAdjective negative-form adjective
     * @param message      notice message
     * @param description  description
     * @param boundEntries bound UI entries
     */
    public ObjectProperty(ObjPropertyType type, ObjectFlagType subtype,
                          ObjectFlagID idType, ObjectPropertyTypeWrapper payload,
                          int power, int mult, Map<TValue, Integer> typeMults,
                          String name, String adjective, String negAdjective,
                          String message, String description,
                          List<UIBinding> boundEntries) {
        this.type = type;
        this.subtype = subtype;
        this.idType = idType;
        this.payload = payload;
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

    /**
     * @return the property's category, which decides how {@link #getPayload} is to be read
     */
    public ObjPropertyType getType() {
        return type;
    }

    /**
     * @return the property's sub-type, which for flags distinguishes sustains, protections and the
     * like, and marks those that are not learnable properties at all
     */
    public ObjectFlagType getSubtype() {
        return subtype;
    }

    /**
     * @return the typed payload identifying which flag, modifier or element this property describes
     */
    public ObjectPropertyTypeWrapper getPayload() {
        return payload;
    }

    /**
     * @return the property's name, as shown to the player
     */
    public String getName() {
        return name;
    }

    /**
     * @return the message shown when the player notices this property on an object, or {@code null}
     * where the data file gives none - C's {@code obj_property->msg}
     */
    public String getNoticeMessage() {
        return message;
    }

    /**
     * @return how heavily this property counts towards the combined ability bonus that
     *         {@code ItemObject.modifierPower} inhibits on - C's {@code obj_property->mult}. Not a
     *         damage multiplier, and unrelated to {@link #getTypeMult(TValue)}
     */
    public int getMultiplier() {
        return mult;
    }

    /**
     * @return this property's base power before the per-type multiplier is applied, or zero for a
     * property the power calculation does not price - C's {@code obj_property->power}
     */
    public int getPower() {
        return power;
    }

    /**
     * Returns how much this property is worth on one kind of object - the port of reading C's
     * {@code obj_property->type_mult[tval]} ({@code obj-power.c:556}, {@code obj-power.c:602}).
     *
     * <p>The same property is worth different amounts on different objects: extra blows are worth
     * three times as much on a ring as on a weapon, and nothing at all on a bow. The multiplier is
     * the figure that says so, and the power code multiplies the base power by it.
     *
     * <p><b>An unlisted type multiplies by one.</b> C fills every slot of the array with 1 before
     * parsing any {@code type-mult:} line ({@code obj-init.c:3186-3189}), so a type the data file
     * does not name is priced normally rather than at nothing. The port stores only the named types
     * and supplies the same default here - which is why the fallback is 1 and not 0.
     *
     * <p>Curses reach this with {@code TV_NONE}, the tval a curse object carries, and get the
     * default back for the same reason.
     *
     * <p>Function getTypeMult commented in full on 260827.
     *
     * @param tValue the object type being priced
     * @return the multiplier for that type, or 1 if the data file names no figure for it
     */
    public int getTypeMult(TValue tValue) {
        if (typeMults.containsKey(tValue))
            return typeMults.get(tValue);

        return 1;
    }
}