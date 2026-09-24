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
 * <p>{@link #boundEntries} is this class's one field with no direct counterpart on the C struct.
 * C's {@code bindui:} directive is handled by {@code parse_object_property_bindui}
 * ({@code obj-init.c:3388-3404}), which calls {@code bind_object_property_to_ui_entry_by_name}
 * ({@code ui-entry.c:213-249}) to push a {@code struct bound_object_property} straight onto the
 * named {@code struct ui_entry}'s own growable array — the property itself keeps no record of what
 * it is bound to. The port keeps the list here instead, because {@link #boundEntries}'s parse-time
 * building needs no {@code struct ui_entry} to exist yet (see the class Javadoc note on
 * {@code GameConstants.init(Core)}, which reads this list back only once every UI entry the front
 * end owns has arrived over the channel); C has no such ordering problem, since its parser can
 * search {@code entries[]} for the named entry the moment {@code bindui:} is read.
 *
 * <p>Class ObjectProperty coded before 260827, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
public class ObjectProperty {
    /**
     * Logger (reserved for diagnostics).
     *
     * <p>Field logger coded before 260827, commented in full on 260924.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The property's category (flag, stat/modifier, or element relation) — C's {@code obj_property
     * ->type}, and the discriminant that decides how {@link #payload} is to be read.
     *
     * <p>Field type coded before 260827, commented in full on 260924.
     */
    private ObjPropertyType type;
    /**
     * The property's sub-type identifier — C's {@code obj_property->subtype}. For a flag property
     * this distinguishes sustains, protections and the like, and marks the subtypes that are not
     * learnable properties at all.
     *
     * <p>Field subtype coded before 260827, commented in full on 260924.
     */
    private ObjectFlagType subtype;
    /**
     * How this property is identified by the player — C's {@code obj_property->id_type}.
     *
     * <p>Field idType coded before 260827, commented in full on 260924.
     */
    private ObjectFlagID idType;
    /**
     * The typed payload (flag/modifier/element) this property represents — C's
     * {@code obj_property->index}, wrapped so the port keeps a real enum constant rather than a bare
     * index whose meaning depends on {@link #type}.
     *
     * <p>Field payload coded before 260827, commented in full on 260924.
     */
    private ObjectPropertyTypeWrapper payload;
    /**
     * The property's base power for item valuation, or zero for a property the power calculation
     * does not price — C's {@code obj_property->power}.
     *
     * <p>Field power coded before 260827, commented in full on 260924.
     */
    private int power;
    /**
     * How heavily this property counts towards the combined ability bonus that
     * {@code ItemObject.modifierPower} inhibits on — C's {@code obj_property->mult}. Not a damage
     * multiplier, and unrelated to {@link #typeMults}.
     *
     * <p>Field mult coded before 260827, commented in full on 260924.
     */
    private int mult;
    /**
     * Per-object-type multipliers applied to the property's base power — C's
     * {@code obj_property->type_mult[TV_MAX]}, read through {@link #getTypeMult(TValue)}. Only the
     * types the data file names a {@code type-mult:} figure for are stored; every other type falls
     * back to 1, matching C's array-wide default.
     *
     * <p>Field typeMults coded before 260827, commented in full on 260924.
     */
    private Map<TValue, Integer> typeMults;
    /**
     * The property's name, as shown to the player — C's {@code obj_property->name}.
     *
     * <p>Field name coded before 260827, commented in full on 260924.
     */
    private String name;
    /**
     * Adjective describing the positive form of the property — C's {@code obj_property->adjective}.
     *
     * <p>Field adjective coded before 260827, commented in full on 260924.
     */
    private String adjective;
    /**
     * Adjective describing the negative form of the property — C's {@code obj_property->neg_adj}.
     *
     * <p>Field negAdjective coded before 260827, commented in full on 260924.
     */
    private String negAdjective;
    /**
     * Message shown when the player notices this property on an object, or {@code null} where the
     * data file gives none — C's {@code obj_property->msg}.
     *
     * <p>Field message coded before 260827, commented in full on 260924.
     */
    private String message;
    /**
     * Human-readable description of the property, shown in object info screens — C's
     * {@code obj_property->desc}.
     *
     * <p>Field description coded before 260827, commented in full on 260924.
     */
    private String description;
    /**
     * The UI entries this property is bound to, one {@link UIBinding} per {@code bindui:} line in
     * this property's {@code object_property.txt} record. See the class Javadoc for why this list
     * lives here rather than on a {@code struct ui_entry} as it does in C.
     *
     * <p>Field boundEntries coded before 260919, commented in full on 260924.
     */
    private List<UIBinding> boundEntries;
    /**
     * Build an object property from its parsed data-file fields — the Java form of the field
     * assignments C's {@code object_property.txt} parser makes across
     * {@code parse_object_property_name} through {@code parse_object_property_desc}
     * ({@code obj-init.c:3155-3387}) onto one {@code struct obj_property}, with
     * {@link #boundEntries} standing in for the {@code bindui:} lines those C parser functions instead
     * push straight onto a named {@code struct ui_entry} (see the class Javadoc).
     *
     * @param type         property category
     * @param subtype      sub-type identifier
     * @param idType       identification method
     * @param payload      typed payload
     * @param power        base power
     * @param mult         value multiplier
     * @param typeMults    per-type value multipliers
     * @param name         property name
     * @param adjective    positive-form adjective
     * @param negAdjective negative-form adjective
     * @param message      notice message
     * @param description  description
     * @param boundEntries bound UI entries
     *
     *                     <p>Function ObjectProperty coded before 260827, commented in full on
     *                     260924.
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
     *
     * <p>Function getType commented in full on 260924.
     */
    public ObjPropertyType getType() {
        return type;
    }

    /**
     * @return the property's sub-type, which for flags distinguishes sustains, protections and the
     * like, and marks those that are not learnable properties at all
     *
     * <p>Function getSubtype commented in full on 260924.
     */
    public ObjectFlagType getSubtype() {
        return subtype;
    }

    /**
     * @return the typed payload identifying which flag, modifier or element this property describes
     *
     * <p>Function getPayload commented in full on 260924.
     */
    public ObjectPropertyTypeWrapper getPayload() {
        return payload;
    }

    /**
     * @return the property's name, as shown to the player
     *
     * <p>Function getName commented in full on 260924.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the message shown when the player notices this property on an object, or {@code null}
     * where the data file gives none - C's {@code obj_property->msg}
     *
     * <p>Function getNoticeMessage commented in full on 260924.
     */
    public String getNoticeMessage() {
        return message;
    }

    /**
     * @return how heavily this property counts towards the combined ability bonus that
     *         {@code ItemObject.modifierPower} inhibits on - C's {@code obj_property->mult}. Not a
     *         damage multiplier, and unrelated to {@link #getTypeMult(TValue)}
     *
     * <p>Function getMultiplier commented in full on 260924.
     */
    public int getMultiplier() {
        return mult;
    }

    /**
     * @return this property's base power before the per-type multiplier is applied, or zero for a
     * property the power calculation does not price - C's {@code obj_property->power}
     *
     * <p>Function getPower commented in full on 260924.
     */
    public int getPower() {
        return power;
    }

    /**
     * Returns every UI entry this property is bound to — the port's read side of C's
     * {@code bindui:} handling. Where C's {@code bind_object_property_to_ui_entry_by_name}
     * ({@code ui-entry.c:213-249}) pushes each binding onto the named {@code struct ui_entry}
     * directly, so nothing on the C {@code struct obj_property} itself records what it is bound to,
     * this list is the port's own record of the same bindings, read back once by
     * {@code GameConstants.init(Core)} to build the equivalent {@code UIEntryValueRegistry} entries
     * after every UI entry the front end owns has arrived — see the class Javadoc for why the two
     * ports have to do this in a different order.
     *
     * <p>Live, not a copy: the list is fixed at construction and never mutated afterwards, so sharing
     * it costs nothing.
     *
     * <p>Function getBoundEntries commented in full on 260924.
     *
     * @return this property's {@code bindui:} bindings, in data-file order
     */
    public List<UIBinding> getBoundEntries() {
        return boundEntries;
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

    /**
     * One binding of this property to a UI display slot — the Java form of one element of C's
     * {@code struct bound_object_property} ({@code ui-entry.c:61-67}: {@code type}, {@code index},
     * {@code value}, {@code have_value}, {@code isaux}), built from a single {@code bindui:} line in
     * {@code object_property.txt} ({@code obj-init.c:3388-3404}). The {@code type}/{@code index}
     * pair C carries per binding is not repeated here, since every {@link UIBinding} in
     * {@link #boundEntries} already belongs to the one {@link ObjectProperty} whose type and index
     * it shares.
     *
     * @param entry the UI entry this property is bound to, C's {@code name} parameter to
     *              {@code bind_object_property_to_ui_entry_by_name}
     * @param value the value presented to the UI entry when the property is present, or
     *              {@code null} when the data file gives no {@code uival} — C's {@code have_value}
     *              is this field's presence, and {@code value} is its content
     * @param aux   whether this property should be treated as an auxiliary value for the UI entry,
     *              C's {@code isaux}
     * @author Rowan Crowther
     *
     * <p>Record UIBinding coded before 260919, commented in full on 260924.
     */
    public record UIBinding(String entry, @Nullable Integer value, boolean aux) {
    }
}