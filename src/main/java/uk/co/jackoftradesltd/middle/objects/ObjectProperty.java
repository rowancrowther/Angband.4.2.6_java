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
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A generic object property definition (as loaded from {@code object_property.txt})
 * — describing one property an object can have (a flag, stat/modifier, or element
 * relation, captured by {@link ObjectPropertyTypeWrapper}) along with its power,
 * value multipliers, the adjectives/messages used to describe it, and the UI
 * entries it binds to. This is the Java port of the C original's
 * {@code struct obj_property} ({@code src/obj-properties.h}).
 *
 * <p>Two things differ from the C struct. C's {@code next} pointer is gone: the parser chains the
 * records through it and {@code finish_parse_object_property} copies the chain into the
 * {@code obj_properties} array, whereas the port's assembler produces a {@link List} directly.
 * And {@link #boundEntries} has no counterpart on {@code struct obj_property} at all.
 *
 * <p>C's {@code bindui:} directive is handled by {@code parse_object_property_bindui}
 * ({@code obj-init.c}), which calls {@code bind_object_property_to_ui_entry_by_name}
 * ({@code ui-entry.c}) to push a {@code struct bound_object_property} straight onto the named
 * {@code struct ui_entry}'s own growable array — the property itself keeps no record of what it is
 * bound to. The port keeps the list here instead, because building it at parse time needs no
 * {@code struct ui_entry} to exist yet: {@code GameConstants} matches each binding against the
 * front end's UI entries only once they have arrived over the channel. C has no such ordering
 * problem, since its parser can search {@code entries[]} for the named entry the moment
 * {@code bindui:} is read.
 *
 * <p><b>A property with no type.</b> C accepts an {@code object_property.txt} record with no {@code type:} line:
 * {@code parse_object_property_name} zero-fills the struct, so {@code type} stays {@code OBJ_PROPERTY_NONE} (0) and
 * {@code index} stays 0, and {@code finish_parse_object_property} keeps the record in {@code obj_properties}. The port
 * does the same, holding {@code OBJ_PROPERTY_NONE} with a NONE {@link ObjectPropertyTypeWrapper} as the payload. Such a
 * property is inert. C's {@code lookup_obj_property} matches on {@code type} as well as {@code index}, and the port's
 * {@code ObjectRegistry.lookupObjectProperty} does too, with every caller passing {@code OBJ_PROPERTY_MOD} or
 * {@code OBJ_PROPERTY_FLAG}; {@code PlayerKnowledge} skips anything that is not a flag when it builds a flag mask. C also
 * refuses a {@code bindui:} line while the type is 0, so a record with no type never carries a binding.
 *
 * <p><b>Two ways to build one.</b> The full constructor takes every field at once, and is what
 * {@code ObjectPropertyAssembler} calls (the assembler {@code ObjectPropertyReader} runs). The no-argument constructor
 * is C's {@code mem_zalloc} in {@code parse_object_property_name}: a blank record, with the setters standing in for
 * the later {@code parse_object_property_*} handlers that fill it one line at a time. {@code ObjectProperty2Assembler},
 * the line-ordered assembler, builds that way. They differ in what an absent text line leaves behind: the
 * full-constructor route holds {@code ""}, the grammar's default, and the blank record holds {@code null}, which is
 * C's {@code NULL}.
 *
 * <p>Class ObjectProperty coded before 260827, commented in full on 261115 (struct location corrected and the
 * type-less property added 261113; the blank-record route added 261115).
 *
 * @author Rowan Crowther
 */
public class ObjectProperty {
    /**
     * Logger (reserved for diagnostics).
     *
     * <p>Field logger coded before 260827, commented in full on 261115.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The property's category (flag, stat/modifier, or element relation) — C's {@code obj_property
     * ->type}, and the discriminant that decides how {@link #payload} is to be read. A record with no
     * {@code type:} line holds {@code OBJ_PROPERTY_NONE}, C's zero.
     *
     * <p>Field type coded before 260827, commented in full on 261113.
     */
    private ObjPropertyType type;
    /**
     * The property's sub-type identifier — C's {@code obj_property->subtype}. Only a {@code flag}
     * property carries one: it distinguishes sustains, protections and the like, and the power
     * calculation counts how many flags of each subtype an object has. Every other category, and
     * a flag with no {@code subtype:} line, holds {@code OFT_NONE} — the zero C's zero-filled
     * allocation leaves — which the knowledge code skips, as it does {@code OFT_CURSE_ONLY}
     * (a flag only meaningful as part of a curse).
     *
     * <p>Field subtype coded before 260827, commented in full on 261115.
     */
    private ObjectFlagType subtype;
    /**
     * How this property is identified by the player — C's {@code obj_property->id_type}: on effect,
     * timed, or on wield. Only {@code flag} properties name one; any other property holds
     * {@code OFID_NONE}, C's zero-filled default.
     *
     * <p>Field idType coded before 260827, commented in full on 261115.
     */
    private ObjectFlagID idType;
    /**
     * The typed payload (flag/modifier/element) this property represents — C's
     * {@code obj_property->index}, wrapped so the port keeps a real enum constant rather than a bare
     * index whose meaning depends on {@link #type}. A property of type {@code OBJ_PROPERTY_NONE} holds the
     * NONE wrapper (see {@link ObjectPropertyTypeWrapper}), not {@code null}.
     *
     * <p>Field payload coded before 260827, commented in full on 261113.
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
     * <p>Field typeMults coded before 260827, commented in full on 261115.
     */
    private Map<TValue, Integer> typeMults;
    /**
     * The property's name, as shown to the player — C's {@code obj_property->name}. Set from the record's
     * {@code name:} line, which opens every record, so a property that came from the data file always has one; only
     * the blank record from the no-argument constructor holds {@code null}, until {@link #setName} runs.
     *
     * <p>Field name coded before 260827, commented in full on 261115.
     */
    private String name;
    /**
     * Adjective describing the positive form of the property — C's {@code obj_property->adjective}.
     * C reads it in {@code desc_stat} ({@code effect-handler-general.c}) for a {@code stat}
     * property, where it words a stat gain.
     *
     * <p>Where the data file gives no {@code adjective:} line, C leaves the pointer {@code NULL}.
     * Through {@code ObjectPropertyAssembler} the grammar's default is {@code ""} and it is passed
     * straight through, so the "no adjective" value is the empty string; a blank record built by
     * the no-argument constructor holds {@code null} as C does. Read through {@link #getAdjective};
     * nothing calls it until {@code desc_stat} is ported.
     *
     * <p>Field adjective coded before 260827, commented in full on 261115.
     */
    private String adjective;
    /**
     * Adjective describing the negative form of the property — C's {@code obj_property->neg_adj}.
     * C reads it in {@code desc_stat} ({@code effect-handler-general.c}) for a {@code stat}
     * property, where it words a stat loss.
     *
     * <p>Where the data file gives no {@code neg-adjective:} line, C leaves the pointer
     * {@code NULL}; the port holds {@code ""} or {@code null} as for {@link #adjective}. Read through
     * {@link #getNegAdjective}, which has no caller yet, for the same reason.
     *
     * <p>Field negAdjective coded before 260827, commented in full on 261115.
     */
    private String negAdjective;
    /**
     * Message shown when the player notices this property on an object — C's
     * {@code obj_property->msg}, with {@code {name}} as the placeholder for the object's name.
     *
     * <p>Where the data file gives no {@code msg:} line, C leaves the pointer {@code NULL} and
     * {@code flag_message} returns silently. Through {@code ObjectPropertyAssembler} the grammar's
     * default for an absent {@code msg:} is {@code ""}, passed straight through; a blank record built
     * by the no-argument constructor holds {@code null} as C does. {@code ItemObject.flagMessage}
     * treats both as "no message".
     *
     * <p>Field message coded before 260827, commented in full on 261115.
     */
    private String message;
    /**
     * Human-readable description of the property, shown in object info screens — C's
     * {@code obj_property->desc}. C reads it in {@code ui-knowledge.c}, where the knowledge menus
     * list the protection, ability, melee and bad-flag properties.
     *
     * <p>Where the data file gives no {@code desc:} line, C leaves the pointer {@code NULL}; the
     * port holds {@code ""} or {@code null} as for {@link #adjective}. Read through
     * {@link #getDescription}, which has no caller yet: the knowledge menus are a later chapter.
     *
     * <p>Field description coded before 260827, commented in full on 261115.
     */
    private String description;
    /**
     * The UI entries this property is bound to, one {@link UIBinding} per {@code bindui:} line in
     * this property's {@code object_property.txt} record. See the class Javadoc for why this list
     * lives here rather than on a {@code struct ui_entry} as it does in C.
     *
     * <p>The two assemblers fill it differently. {@code ObjectPropertyAssembler} builds from a parse
     * record that keeps only the last {@code bindui:} line of a record, so its list holds at most one
     * binding, where C binds every line. {@code ObjectProperty2Assembler} calls {@link #addBoundEntry}
     * once per {@code bindui:} line, so its list holds every binding, in file order.
     *
     * <p>Field boundEntries coded before 260919, commented in full on 261115.
     */
    private List<UIBinding> boundEntries;
    /**
     * Build an object property from its parsed data-file fields — the Java form of the field
     * assignments C's {@code object_property.txt} parser makes across
     * {@code parse_object_property_name} through {@code parse_object_property_desc}
     * ({@code obj-init.c}) onto one {@code struct obj_property}, with {@link #boundEntries}
     * standing in for the {@code bindui:} lines those C parser functions instead push straight onto
     * a named {@code struct ui_entry} (see the class Javadoc).
     *
     * <p>Nothing is copied or validated: the maps and lists passed in are kept as given, so the
     * caller (in practice {@code ObjectPropertyAssembler}) must not mutate them afterwards.
     * {@code typeMults} must be non-null — pass an empty map for a property that names no
     * {@code type-mult:} lines — because {@link #getTypeMult(TValue)} reads it unguarded.
     *
     * <p>Function ObjectProperty coded before 260827, commented in full on 261115.
     *
     * @param type         property category, {@code OBJ_PROPERTY_NONE} for a record with no {@code type:} line
     * @param subtype      sub-type identifier, {@code OFT_NONE} for anything but a flag
     * @param idType       identification method, {@code OFID_NONE} for anything but a flag
     * @param payload      typed payload (C's {@code index}); the NONE wrapper when {@code type} is
     *                     {@code OBJ_PROPERTY_NONE}
     * @param power        base power, 0 if the power calculation does not price it
     * @param mult         ability-bonus weight (C's {@code mult})
     * @param typeMults    per-type multipliers the data file named, empty if none
     * @param name         property name
     * @param adjective    positive-form adjective
     * @param negAdjective negative-form adjective
     * @param message      notice message; the assembler passes {@code ""} where the data file gives none
     * @param description  description for the object info screens
     * @param boundEntries bound UI entries, empty if the record has no {@code bindui:} line
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
     * Build a blank property — the Java form of the {@code mem_zalloc(sizeof *prop)} in
     * {@code parse_object_property_name} ({@code obj-init.c}), before any later line has filled it.
     * Every field takes the value C's zero-fill leaves: type {@code OBJ_PROPERTY_NONE}, subtype
     * {@code OFT_NONE}, id type {@code OFID_NONE}, the NONE payload wrapper, power and mult 0, and
     * {@code null} for the name and the four text fields.
     *
     * <p>C also sets every {@code type_mult} slot to 1 at that point. Here the type-multiplier map
     * starts empty and {@link #getTypeMult(TValue)} supplies the 1 for any type not in it, so the
     * observable result is the same. The bound-entry list starts empty.
     *
     * <p>The {@code parse_object_property_*} handlers are the setters, {@link #putTypeMults} and
     * {@link #addBoundEntry}; {@code ObjectProperty2Assembler} calls them in file order. Nothing is
     * validated here, so a property that never receives a {@code name:} has a {@code null} name.
     *
     * <p>Function ObjectProperty() coded before 261115, commented in full on 261115.
     */
    public ObjectProperty() {
        this.type = ObjPropertyType.OBJ_PROPERTY_NONE;
        this.subtype = ObjectFlagType.OFT_NONE;
        this.idType = ObjectFlagID.OFID_NONE;
        this.payload = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_NONE, (ObjectModifier) null);
        this.power = 0;
        this.mult = 0;
        this.typeMults = new HashMap<>();
        this.name = null;
        this.adjective = null;
        this.negAdjective = null;
        this.message = null;
        this.description = null;
        this.boundEntries = new ArrayList<>();
    }

    /**
     * Returns the property's category — C's {@code obj_property->type}. It is the discriminant for
     * {@link #getPayload}: only the accessor matching this category will answer. A record with no
     * {@code type:} line answers {@code OBJ_PROPERTY_NONE}, and none of the typed accessors on its payload has a
     * flag or a modifier to give.
     *
     * <p>Function getType coded before 260827, commented in full on 261113.
     *
     * @return the property's category, {@code OBJ_PROPERTY_NONE} if the record named none
     */
    public ObjPropertyType getType() {
        return type;
    }

    /**
     * Returns the property's sub-type — C's {@code obj_property->subtype}. For a flag this
     * distinguishes sustains, protections and the like; {@code OFT_NONE} for every other category,
     * and {@code OFT_CURSE_ONLY} for a flag only meaningful as part of a curse.
     *
     * <p>Function getSubtype coded before 260827, commented in full on 261005.
     *
     * @return the property's sub-type
     */
    public ObjectFlagType getSubtype() {
        return subtype;
    }

    /**
     * Returns the typed payload — the port of C's {@code obj_property->index}, which in C is a bare
     * {@code int} whose table (modifiers, flags or elements) depends on {@link #getType}. The
     * wrapper carries the real enum constant and the category it belongs to. For a property of type
     * {@code OBJ_PROPERTY_NONE} it is the NONE wrapper, which holds {@code ELEM_NONE} and no flag or modifier.
     *
     * <p>Function getPayload coded before 260827, commented in full on 261113.
     *
     * @return the payload identifying which flag, modifier or element this property describes; never {@code null}
     * for a property the assembler built
     */
    public ObjectPropertyTypeWrapper getPayload() {
        return payload;
    }

    /**
     * Returns the property's name — C's {@code obj_property->name}, from the record's {@code name:}
     * line.
     *
     * <p>Function getName coded before 260827, commented in full on 261005.
     *
     * @return the property's name, as shown to the player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the message shown when the player notices this property — C's
     * {@code obj_property->msg}, the string {@code flag_message} walks to substitute {@code {name}}.
     *
     * <p>Where the data file gives none, the port holds {@code ""} (the grammar's default) rather
     * than C's {@code NULL}; see the {@code message} field.
     *
     * <p>Function getNoticeMessage coded before 260827, commented in full on 261005.
     *
     * @return the notice message, with {@code {name}} still unsubstituted
     */
    public String getNoticeMessage() {
        return message;
    }

    /**
     * Returns how heavily this property counts towards the combined ability bonus — C's
     * {@code obj_property->mult}. {@code modifier_power} in {@code obj-power.c} sums
     * {@code modifier * mult} over every modifier and adds the inhibit power once the total passes
     * 249. Not a damage multiplier, and unrelated to {@link #getTypeMult(TValue)}.
     *
     * <p>Function getMultiplier coded before 260827, commented in full on 261005.
     *
     * @return the ability-bonus weight
     */
    public int getMultiplier() {
        return mult;
    }

    /**
     * Returns the property's base power — C's {@code obj_property->power}. {@code modifier_power}
     * and {@code flags_power} in {@code obj-power.c} both scale it by {@link #getTypeMult(TValue)}
     * (and {@code modifier_power} by the modifier's value as well), and skip the term entirely when
     * it is zero.
     *
     * <p>Function getPower coded before 260827, commented in full on 261005.
     *
     * @return the base power, or zero for a property the power calculation does not price
     */
    public int getPower() {
        return power;
    }

    /**
     * Returns every UI entry this property is bound to — the port's read side of C's
     * {@code bindui:} handling. Where C's {@code bind_object_property_to_ui_entry_by_name}
     * ({@code ui-entry.c}) pushes each binding onto the named {@code struct ui_entry} directly, so
     * nothing on the C {@code struct obj_property} itself records what it is bound to, this list is
     * the port's own record of the same bindings. {@code GameConstants} reads it to match
     * properties to the front end's UI entries once those have arrived, and
     * {@code UIEntryValueRegistry} filters it by entry name to find the binding for the entry it is
     * evaluating — see the class Javadoc for why the port does this in a different order.
     *
     * <p>Live, not a copy: the callers only read it, so sharing it costs nothing. The list does
     * change while a blank record is being assembled ({@link #addBoundEntry},
     * {@link #setBoundEntries}), but the assembler is finished with the property before any
     * reader sees it.
     *
     * <p>Function getBoundEntries coded before 260919, commented in full on 261115.
     *
     * @return this property's {@code bindui:} bindings, in data-file order; the list is empty for a property with no
     * {@code bindui:} line (see the {@link #boundEntries} field for how many each assembler gives)
     */
    public List<UIBinding> getBoundEntries() {
        return boundEntries;
    }

    /**
     * Returns how much this property is worth on one kind of object - the port of reading C's
     * {@code obj_property->type_mult[tval]}, as {@code modifier_power} and {@code flags_power} in
     * {@code obj-power.c} do.
     *
     * <p>The same property is worth different amounts on different objects: extra blows are worth
     * three times as much on a ring as on a weapon, and nothing at all on a bow. The multiplier is
     * the figure that says so, and the power code multiplies the base power by it.
     *
     * <p><b>An unlisted type multiplies by one.</b> C fills every slot of the array with 1 before
     * parsing any {@code type-mult:} line ({@code parse_object_property_name} in
     * {@code obj-init.c}), so a type the data file does not name is priced normally rather than at nothing. The port stores only the named types
     * and supplies the same default here - which is why the fallback is 1 and not 0.
     *
     * <p>Curses reach this with {@code TV_NONE}, the tval a curse object carries, and get the
     * default back for the same reason.
     *
     * <p>Function getTypeMult coded before 260827, commented in full on 261005.
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
     * Returns how the player identifies this property — C's {@code obj_property->id_type}, set from
     * the {@code id-type:} line ({@code on effect}, {@code timed} or {@code on wield}).
     * {@code PlayerKnowledge} compares it when it builds a mask of the flags learnable a given
     * way. {@code OFID_NONE} for any property that names none.
     *
     * <p>Function getIdType coded before 260827, commented in full on 261005.
     *
     * @return the identification method
     */
    public ObjectFlagID getIdType() {
        return idType;
    }

    /**
     * Returns the adjective for the positive form of the property — C's {@code obj_property->adjective}.
     * {@code desc_stat} in {@code effect-handler-general.c} reads it to word a stat gain; it has no caller
     * in the port yet.
     *
     * <p>Function getAdjective coded before 261115, commented in full on 261115.
     *
     * @return the adjective: {@code ""} through {@code ObjectPropertyAssembler} when the record has none,
     * {@code null} on a blank record that was never given one
     */
    public String getAdjective() {
        return adjective;
    }

    /**
     * Returns the property's description for the object info screens — C's {@code obj_property->desc}, which
     * {@code ui-knowledge.c} lists in the knowledge menus. It has no caller in the port yet.
     *
     * <p>Function getDescription coded before 261115, commented in full on 261115.
     *
     * @return the description: {@code ""} through {@code ObjectPropertyAssembler} when the record has none,
     * {@code null} on a blank record that was never given one
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the message shown when the player notices this property — C's {@code obj_property->msg}. The same
     * field as {@link #getNoticeMessage()}, under the name the setter uses.
     *
     * <p>Function getMessage coded before 261115, commented in full on 261115.
     *
     * @return the message with {@code {name}} unsubstituted: {@code ""} through {@code ObjectPropertyAssembler} when
     * the record has none, {@code null} on a blank record that was never given one
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the property's relative weight — C's {@code obj_property->mult}. The same field as
     * {@link #getMultiplier()}, under the name the setter uses.
     *
     * <p>Function getMult coded before 261115, commented in full on 261115.
     *
     * @return the ability-bonus weight
     */
    public int getMult() {
        return mult;
    }

    /**
     * Returns the adjective for the negative form of the property — C's {@code obj_property->neg_adj}.
     * {@code desc_stat} in {@code effect-handler-general.c} reads it to word a stat loss; it has no caller in the
     * port yet.
     *
     * <p>Function getNegAdjective coded before 261115, commented in full on 261115.
     *
     * @return the adjective: {@code ""} through {@code ObjectPropertyAssembler} when the record has none,
     * {@code null} on a blank record that was never given one
     */
    public String getNegAdjective() {
        return negAdjective;
    }

    /**
     * Returns the map of per-type multipliers itself — the port of C's {@code obj_property->type_mult[]} array,
     * holding only the types the record named. Live, not a copy. To price one type use
     * {@link #getTypeMult(TValue)}, which supplies C's default of 1 for a type the map lacks; this map answers
     * {@code null} for it.
     *
     * <p>Function getTypeMults coded before 261115, commented in full on 261115.
     *
     * @return the named types and their multipliers; empty if the record named none
     */
    public Map<TValue, Integer> getTypeMults() {
        return typeMults;
    }

    /**
     * Sets the positive-form adjective — the port of {@code parse_object_property_adjective} ({@code obj-init.c}),
     * which replaces the earlier text, so a repeated {@code adjective:} line keeps the last.
     *
     * <p>Function setAdjective coded before 261115, commented in full on 261115.
     *
     * @param adjective the adjective
     */
    public void setAdjective(String adjective) {
        this.adjective = adjective;
    }

    /**
     * Replaces the whole list of bound UI entries. Nothing is copied, so the caller must not mutate the list
     * afterwards. To add one binding use {@link #addBoundEntry}.
     *
     * <p>Function setBoundEntries coded before 261115, commented in full on 261115.
     *
     * @param boundEntries the new list; must be non-null, because {@link #addBoundEntry} appends to it unguarded
     */
    public void setBoundEntries(List<UIBinding> boundEntries) {
        this.boundEntries = boundEntries;
    }

    /**
     * Appends one binding — the port's side of {@code parse_object_property_bindui} ({@code obj-init.c}), which
     * binds every {@code bindui:} line it reads, so repeated lines each add one, in file order. C pushes the binding
     * onto the named {@code struct ui_entry}; see the class Javadoc for why the port records it here.
     *
     * <p>Function addBoundEntry coded before 261115, commented in full on 261115.
     *
     * @param uiBinding the binding to add
     */
    public void addBoundEntry(UIBinding uiBinding) {
        boundEntries.add(uiBinding);
    }

    /**
     * Sets the description — the port of {@code parse_object_property_desc} ({@code obj-init.c}), which replaces the
     * earlier text, so a repeated {@code desc:} line keeps the last.
     *
     * <p>Function setDescription coded before 261115, commented in full on 261115.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets how the player identifies the property — the port of {@code parse_object_property_id_type}
     * ({@code obj-init.c}), which sets {@code obj_property->id_type} from the {@code id-type:} token.
     *
     * <p>Function setIdType coded before 261115, commented in full on 261115.
     *
     * @param idType the identification method
     */
    public void setIdType(ObjectFlagID idType) {
        this.idType = idType;
    }

    /**
     * Sets the notice message — the port of {@code parse_object_property_msg} ({@code obj-init.c}), which replaces
     * the earlier text, so a repeated {@code msg:} line keeps the last.
     *
     * <p>Function setMessage coded before 261115, commented in full on 261115.
     *
     * @param message the message, with {@code {name}} as the placeholder for the object's name
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the relative weight — the port of {@code parse_object_property_mult} ({@code obj-init.c}), which replaces
     * the earlier figure.
     *
     * <p>Function setMult coded before 261115, commented in full on 261115.
     *
     * @param mult the ability-bonus weight
     */
    public void setMult(int mult) {
        this.mult = mult;
    }

    /**
     * Sets the property's name — the port of the {@code name} assignment in {@code parse_object_property_name}
     * ({@code obj-init.c}).
     *
     * <p>Function setName coded before 261115, commented in full on 261115.
     *
     * @param name the property's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the negative-form adjective — the port of {@code parse_object_property_neg_adj} ({@code obj-init.c}),
     * which replaces the earlier text, so a repeated {@code neg-adjective:} line keeps the last.
     *
     * <p>Function setNegAdjective coded before 261115, commented in full on 261115.
     *
     * @param negAdjective the adjective
     */
    public void setNegAdjective(String negAdjective) {
        this.negAdjective = negAdjective;
    }

    /**
     * Sets the typed payload — the port of the {@code index} assignment in {@code parse_object_property_code}
     * ({@code obj-init.c}). The wrapper must have been built for the property's current type, as that handler
     * resolves the code against the table the type selects; nothing here checks that.
     *
     * <p>Function setPayload coded before 261115, commented in full on 261115.
     *
     * @param payload the flag, modifier or element this property describes
     */
    public void setPayload(ObjectPropertyTypeWrapper payload) {
        this.payload = payload;
    }

    /**
     * Sets the base power — the port of {@code parse_object_property_power} ({@code obj-init.c}), which replaces the
     * earlier figure. Negative values are legal.
     *
     * <p>Function setPower coded before 261115, commented in full on 261115.
     *
     * @param power the base power rating
     */
    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Sets the sub-type — the port of {@code parse_object_property_subtype} ({@code obj-init.c}), which sets
     * {@code obj_property->subtype} from the {@code subtype:} token.
     *
     * <p>Function setSubtype coded before 261115, commented in full on 261115.
     *
     * @param subtype the sub-type
     */
    public void setSubtype(ObjectFlagType subtype) {
        this.subtype = subtype;
    }

    /**
     * Sets the category — the port of {@code parse_object_property_type} ({@code obj-init.c}), which overwrites the
     * type and leaves the index and everything else as it finds them, so a later {@code type:} line does not
     * re-resolve an earlier {@code code:}. This setter likewise leaves {@link #payload} alone.
     *
     * <p>Function setType coded before 261115, commented in full on 261115.
     *
     * @param type the property's category
     */
    public void setType(ObjPropertyType type) {
        this.type = type;
    }

    /**
     * Replaces the whole per-type multiplier map. Nothing is copied, so the caller must not mutate it afterwards;
     * it must be non-null because {@link #getTypeMult(TValue)} reads it unguarded. To set one type use
     * {@link #putTypeMults}.
     *
     * <p>Function setTypeMults coded before 261115, commented in full on 261115.
     *
     * @param typeMults the named types and their multipliers; empty for none
     */
    public void setTypeMults(Map<TValue, Integer> typeMults) {
        this.typeMults = typeMults;
    }

    /**
     * Sets the multiplier for one object type — the port of {@code parse_object_property_type_mult}
     * ({@code obj-init.c}), which writes {@code type_mult[tval]} and so replaces an earlier figure for the same type.
     * A negative or zero figure is stored as given.
     *
     * <p>Function putTypeMults coded before 261115, commented in full on 261115.
     *
     * @param tValue the object type the figure applies to
     * @param mult   the multiplier for that type
     */
    public void putTypeMults(TValue tValue, int mult) {
        this.typeMults.put(tValue, mult);
    }

    /**
     * One binding of this property to a UI display slot — the Java form of one element of C's
     * {@code struct bound_object_property} ({@code ui-entry.c}: {@code type}, {@code index},
     * {@code value}, {@code have_value}, {@code isaux}), built from a single {@code bindui:} line in
     * {@code object_property.txt} ({@code parse_object_property_bindui} in {@code obj-init.c}). The
     * {@code type}/{@code index} pair C carries per binding is not repeated here, since every
     * {@link UIBinding} in {@link #boundEntries} already belongs to the one {@link ObjectProperty}
     * whose type and index it shares.
     *
     * <p>Record UIBinding coded before 260919, commented in full on 261005.
     *
     * @param entry the UI entry this property is bound to, C's {@code name} parameter to
     *              {@code bind_object_property_to_ui_entry_by_name}
     * @param value the value presented to the UI entry when the property is present, or
     *              {@code null} when the data file gives no {@code uival} — C's {@code have_value}
     *              is this field's presence, and {@code value} is its content
     * @param aux   whether this property should be treated as an auxiliary value for the UI entry,
     *              C's {@code isaux}
     * @author Rowan Crowther
     */
    public record UIBinding(String entry, @Nullable Integer value, boolean aux) {
    }
}