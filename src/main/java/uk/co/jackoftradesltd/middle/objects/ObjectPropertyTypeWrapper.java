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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

/**
 * The typed form of C's {@code obj_property->index}. In C that field is a bare {@code int} whose meaning depends on
 * the sibling {@code type} field: an index into {@code obj_mods} for a stat or mod, into {@code obj_flags} for a flag,
 * and into {@code element_names} for an ignore, resist, vulnerability or immunity. {@code struct obj_property} lives in
 * {@code obj-properties.h}, and {@code parse_object_property_code()} in {@code obj-init.c} fills the index from the
 * {@code code:} line. Java holds the enum constant itself instead, in whichever of three slots matches, so a reader
 * never has to know which table an index points into.
 *
 * <p>Each constructor accepts only the payload type valid for its discriminator, and throws
 * {@link IllegalArgumentException} otherwise. C reaches the same refusal by a different route: it looks the
 * {@code code:} text up in the table the type selects, so a name from the wrong table is simply not found and
 * {@code parse_object_property_code} returns {@code PARSE_ERROR_INVALID_OBJ_PROP_CODE}. A {@code null} payload is
 * refused for every real type. The two unused slots stay {@code null}, except for the NONE wrapper described below. The discriminator is stored here as well as on {@link ObjectProperty},
 * which C does not do, so it is deliberately left out of {@link #equals(Object)}.
 *
 * <p>The typed getters check only that the type the caller asks for belongs to the right family, not that it matches
 * the stored {@link #type}. So {@code getModifier(OBJ_PROPERTY_MOD)} on a stat wrapper returns the modifier, which is
 * what lets stats count as mods, and a getter asked about the wrong family of wrapper returns {@code null} rather than
 * throwing.
 *
 * <p><b>The NONE wrapper.</b> A record with no {@code type:} line keeps C's zero type, {@code OBJ_PROPERTY_NONE}, and
 * its {@code index} stays 0, because {@code parse_object_property_code} refuses to run while the type is 0. The port
 * holds that as a wrapper of type {@code OBJ_PROPERTY_NONE} whose element slot is {@code ELEM_NONE} and whose modifier
 * and flag slots are {@code null}. All three constructors build it, and each does so only when handed a
 * {@code null} payload: a NONE (or {@code null}) type with any non-{@code null} payload is refused with
 * {@link IllegalArgumentException}, which is what C does when a {@code code:} line arrives before any {@code type:} line
 * ({@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE}). The payload is never silently dropped, and {@code ELEM_NONE} counts as a
 * payload. A {@code null} type is treated as NONE. Nothing looks one up: {@code ObjectRegistry.lookupObjectProperty}
 * tests the type before the payload, and every caller in the port passes {@code OBJ_PROPERTY_MOD} or
 * {@code OBJ_PROPERTY_FLAG}, so C's own rule that a lookup never matches type 0 holds here too. The assembler never
 * gives a real property {@code ELEM_NONE}, so a NONE wrapper equals only another NONE wrapper.
 *
 * <p>Class ObjectPropertyTypeWrapper coded before 260827, commented in full on 261115 (NONE wrapper added 261113,
 * payload refusal described 261115).
 *
 * @author Rowan Crowther
 */
public class ObjectPropertyTypeWrapper {
    /**
     * Logger used to report a constructor being handed a payload that does not suit its discriminator.
     *
     * <p>Field logger coded before 260827, commented in full on 261005.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The discriminator: which kind of property payload is stored, or {@code OBJ_PROPERTY_NONE} for a record with no
     * {@code type:} line. C keeps this only on {@code obj_property->type}.
     *
     * <p>Field type coded before 260827, commented in full on 261113.
     */
    private ObjPropertyType type;
    /**
     * Payload for a {@code FLAG} property, C's {@code obj_property->index} read as an {@code OF_*} index. {@code null}
     * for every other type.
     *
     * <p>Field flag coded before 260827, commented in full on 261005.
     */
    private ObjectFlag flag;
    /**
     * Payload for a {@code STAT} or {@code MOD} property, C's {@code obj_property->index} read as an index into
     * {@code obj_mods}, held here as its {@link ObjectModifier} constant. A stat is stored as its modifier, as in C,
     * where both types index {@code obj_mods}. {@code null} for every other type, the NONE wrapper included.
     *
     * <p>Field modifier coded before 260827, commented in full on 261113.
     */
    private ObjectModifier modifier;
    /**
     * Payload for an element relation ({@code IGNORE}/{@code RESIST}/{@code VULN}/{@code IMM}), C's
     * {@code obj_property->index} read as an index into {@code element_names}. For the NONE wrapper it is
     * {@code ELEM_NONE}, the one non-{@code null} value this slot holds outside an element relation. {@code null} for a
     * flag, a stat and a modifier.
     *
     * <p>Field element coded before 260827, commented in full on 261113.
     */
    private ElementEnum element;

    /**
     * Build a stat/modifier-payload wrapper, the Java form of {@code parse_object_property_code()} in {@code obj-init.c}
     * resolving a {@code code:} line against {@code obj_mods} for a stat or mod property. A {@code null} or
     * {@code OBJ_PROPERTY_NONE} type with a {@code null} modifier builds the NONE wrapper; with a non-{@code null}
     * modifier it is refused. A real type with a {@code null} modifier is refused, and so is any type other than stat or
     * mod. Each refusal is logged and thrown.
     *
     * <p>Constructor ObjectPropertyTypeWrapper(ObjPropertyType, ObjectModifier) coded before 260827, commented in full
     * on 261115.
     *
     * @param type     must be {@code OBJ_PROPERTY_STAT} or {@code OBJ_PROPERTY_MOD}, or {@code null}/
     *                 {@code OBJ_PROPERTY_NONE} when {@code modifier} is {@code null}
     * @param modifier the modifier payload
     * @throws IllegalArgumentException if {@code type} is not a stat/mod type, if {@code type} is NONE or {@code null}
     *                                  and {@code modifier} is not {@code null}, or if {@code type} is a real type and
     *                                  {@code modifier} is {@code null}
     */
    public ObjectPropertyTypeWrapper(ObjPropertyType type, ObjectModifier modifier) throws IllegalArgumentException {
        if ((type == null || type == ObjPropertyType.OBJ_PROPERTY_NONE) && modifier != null) {
            String message = "Illegal null or OBJ_PROPERTY_NONE type with value: " + modifier.name() + " received.";
            IllegalArgumentException ex = new IllegalArgumentException(message);
            logger.error(message, ex);
            throw ex;
        }
        
        if (type == null || type == ObjPropertyType.OBJ_PROPERTY_NONE) {
            this.type = ObjPropertyType.OBJ_PROPERTY_NONE;
            saveValue();
            return;
        }
        
        if (modifier == null) {
            String message = "Illegal null value received.";
            IllegalArgumentException ex = new IllegalArgumentException(message);
            logger.error(message, ex);
            throw ex;
        }
        
        switch (type) {
            case OBJ_PROPERTY_STAT:
            case OBJ_PROPERTY_MOD:
                this.type = type;
                saveValue(modifier);
                break;

            default:
                String message = "Illegal type of ObjectProperty passed to constructor.\n"
                        + "Expected OBJ_PROPERTY_STAT or OBJ_PROPERTY_MOD, received " + type.name();
                IllegalArgumentException ex = new IllegalArgumentException(message);
                logger.error(message, ex);
                throw ex;
        }
    }

    /**
     * Build a flag-payload wrapper, the Java form of {@code parse_object_property_code()} in {@code obj-init.c}
     * resolving a {@code code:} line against {@code obj_flags} for a flag property. A {@code null} or
     * {@code OBJ_PROPERTY_NONE} type with a {@code null} flag builds the NONE wrapper; with a non-{@code null} flag it is
     * refused. A real type with a {@code null} flag is refused, and so is any type other than flag. Each refusal is
     * logged and thrown. {@code OF_NONE} is an ordinary non-{@code null} flag here, as {@code obj_flags} in C holds it
     * at index 0.
     *
     * <p>Constructor ObjectPropertyTypeWrapper(ObjPropertyType, ObjectFlag) coded before 260827, commented in full on
     * 261115.
     *
     * @param type must be {@code OBJ_PROPERTY_FLAG}, or {@code null}/{@code OBJ_PROPERTY_NONE} when {@code flag} is
     *             {@code null}
     * @param flag the flag payload
     * @throws IllegalArgumentException if {@code type} is not {@code OBJ_PROPERTY_FLAG}, if {@code type} is NONE or
     *                                  {@code null} and {@code flag} is not {@code null}, or if {@code type} is a real
     *                                  type and {@code flag} is {@code null}
     */
    public ObjectPropertyTypeWrapper(ObjPropertyType type, ObjectFlag flag) throws IllegalArgumentException {
        if ((type == null || type == ObjPropertyType.OBJ_PROPERTY_NONE) && flag != null) {
            String message = "Illegal null or OBJ_PROPERTY_NONE type with value: " + flag.name() + " received.";
            IllegalArgumentException ex = new IllegalArgumentException(message);
            logger.error(message, ex);
            throw ex;
        }

        if (type == null || type == ObjPropertyType.OBJ_PROPERTY_NONE) {
            this.type = ObjPropertyType.OBJ_PROPERTY_NONE;
            saveValue();
            return;
        }

        if (flag == null) {
            String message = "Illegal null value received.";
            IllegalArgumentException ex = new IllegalArgumentException(message);
            logger.error(message, ex);
            throw ex;
        }
        
        if (type == ObjPropertyType.OBJ_PROPERTY_FLAG) {
            this.type = type;
            saveValue(flag);
        } else {
            String message = "Illegal type of ObjectProperty passed to constructor.\n"
                    + "Expected OBJ_PROPERTY_FLAG, received " + type.name();
            IllegalArgumentException ex = new IllegalArgumentException(message);
            logger.error(message, ex);
            throw ex;
        }
    }

    /**
     * Build an element-relation-payload wrapper, the Java form of {@code parse_object_property_code()} in
     * {@code obj-init.c} resolving a {@code code:} line against {@code element_names} for an ignore, resist,
     * vulnerability or immunity property. A {@code null} or {@code OBJ_PROPERTY_NONE} type with a {@code null} element
     * builds the NONE wrapper for a record with no {@code type:} line, storing {@code ELEM_NONE}; with a non-{@code null}
     * element, {@code ELEM_NONE} included, it is refused, so a caller holding a NONE type passes {@code null}. A real
     * type with a {@code null} element is refused, and so is any type other than the four element relations. Each
     * refusal is logged and thrown.
     *
     * <p>Constructor ObjectPropertyTypeWrapper(ObjPropertyType, ElementEnum) coded before 260827, commented in full on
     * 261115 (NONE case added 261113).
     *
     * @param type    must be one of {@code IGNORE}/{@code RESIST}/{@code VULN}/{@code IMM}, or {@code null}/
     *                {@code OBJ_PROPERTY_NONE} when {@code element} is {@code null}
     * @param element the element payload
     * @throws IllegalArgumentException if {@code type} is not an element-relation type, if {@code type} is NONE or
     *                                  {@code null} and {@code element} is not {@code null}, or if {@code type} is a
     *                                  real type and {@code element} is {@code null}
     */
    public ObjectPropertyTypeWrapper(ObjPropertyType type, ElementEnum element) throws IllegalArgumentException {
        if ((type == null || type == ObjPropertyType.OBJ_PROPERTY_NONE) && element != null) {
            String message = "Illegal null or OBJ_PROPERTY_NONE type with value: " + element.name() + " received.";
            IllegalArgumentException ex = new IllegalArgumentException(message);
            logger.error(message, ex);
            throw ex;
        }

        if (type == null || type == ObjPropertyType.OBJ_PROPERTY_NONE) {
            this.type = ObjPropertyType.OBJ_PROPERTY_NONE;
            saveValue();
            return;
        }

        if (element == null) {
            String message = "Illegal null value received.";
            IllegalArgumentException ex = new IllegalArgumentException(message);
            logger.error(message, ex);
            throw ex;
        }
        
        switch (type) {
            case OBJ_PROPERTY_IGNORE:
            case OBJ_PROPERTY_RESIST:
            case OBJ_PROPERTY_VULN:
            case OBJ_PROPERTY_IMM:
                this.type = type;
                saveValue(element);
                break;

            default:
                String message = "Illegal type of ObjectProperty passed to constructor.\n"
                        + "Expected OBJ_PROPERTY_IGNORE, OBJ_PROPERTY_RESIST, " +
                        "OBJ_PROPERTY_VULN, or OBJ_PROPERTY_IMM received " + type.name();
                IllegalArgumentException ex = new IllegalArgumentException(message);
                logger.error(message, ex);
                throw ex;
        }
    }
    
    /**
     * Store a flag payload and clear the element and modifier slots, so exactly one slot is ever non-{@code null}.
     *
     * <p>Function saveValue(ObjectFlag) coded before 260827, commented in full on 261115.
     *
     * @param flag the flag payload
     */
    private void saveValue(ObjectFlag flag) {
        this.element = null;
        this.modifier = null;
        this.flag = flag;
    }
    
    /**
     * Store an element payload and clear the modifier and flag slots, so exactly one slot is ever non-{@code null}.
     *
     * <p>Function saveValue(ElementEnum) coded before 260827, commented in full on 261115.
     *
     * @param element the element payload
     */
    private void saveValue(ElementEnum element) {
        this.element = element;
        this.modifier = null;
        this.flag = null;
    }
    
    /**
     * Store a modifier payload and clear the flag and element slots, so exactly one slot is ever non-{@code null}.
     *
     * <p>Function saveValue(ObjectModifier) coded before 260827, commented in full on 261115.
     *
     * @param modifier the modifier payload
     */
    private void saveValue(ObjectModifier modifier) {
        this.modifier = modifier;
        this.flag = null;
        this.element = null;
    }
    
    /**
     * Make this the NONE wrapper: type {@code OBJ_PROPERTY_NONE}, {@code ELEM_NONE} in the element slot and
     * {@code null} in the modifier and flag slots. This is the Java form of C's zeroed {@code obj_property}, whose type
     * and {@code index} are both 0 until a {@code type:} line sets them.
     *
     * <p>Function saveValue() coded before 260827, commented in full on 261115.
     */
    private void saveValue() {
        this.type = ObjPropertyType.OBJ_PROPERTY_NONE;
        this.element = ElementEnum.ELEM_NONE;
        this.modifier = null;
        this.flag = null;
    }

    /**
     * The discriminator this wrapper was built with, C's {@code obj_property->type}.
     *
     * <p>Function getType coded before 260827, commented in full on 261113.
     *
     * @return which kind of payload is stored, or {@code OBJ_PROPERTY_NONE} for the NONE wrapper
     */
    public ObjPropertyType getType() {
        return type;
    }

    /**
     * Retrieve the modifier payload. Only the family of {@code typeRequested} is checked, not that it equals the stored
     * {@link #type}, so a stat wrapper answers to {@code OBJ_PROPERTY_MOD}. A wrapper holding a flag or an element has
     * no modifier and returns {@code null}.
     *
     * <p>Function getModifier coded before 260827, commented in full on 261115.
     *
     * @param typeRequested the expected type (must be {@code OBJ_PROPERTY_MOD} or {@code OBJ_PROPERTY_STAT})
     * @return the stored modifier, or {@code null} if this wrapper holds a different kind of payload
     * @throws IllegalArgumentException if {@code typeRequested} is {@code null}, or neither MOD nor STAT
     */
    public ObjectModifier getModifier(ObjPropertyType typeRequested) {
        if (typeRequested == null) {
            throw new IllegalArgumentException("typeRequested is null");
        }
        
        if (typeRequested != ObjPropertyType.OBJ_PROPERTY_MOD &&
                typeRequested != ObjPropertyType.OBJ_PROPERTY_STAT) {
            throw new IllegalArgumentException("Illegal Type requested. Expected one of "
                    + " OBJ_PROPERTY_MOD, or" +
                    " OBJ_PROPERTY_STAT, received "
                    + typeRequested.name());
        }

        return modifier;
    }

    /**
     * Retrieve the element payload. Only the family of {@code typeRequested} is checked, not that it equals the stored
     * {@link #type}. A wrapper holding a flag or a modifier has no element and returns {@code null}. The NONE wrapper
     * answers {@code ELEM_NONE}, and asking for {@code OBJ_PROPERTY_NONE} itself is refused, as for any type outside the
     * element family.
     *
     * <p>Function getElement coded before 260827, commented in full on 261115.
     *
     * @param typeRequested the expected type (must be one of {@code IGNORE}/{@code RESIST}/{@code VULN}/{@code IMM})
     * @return the stored element, {@code ELEM_NONE} for the NONE wrapper, or {@code null} if this wrapper holds a flag
     * or a modifier
     * @throws IllegalArgumentException if {@code typeRequested} is {@code null} or not an element-relation type
     */
    public ElementEnum getElement(ObjPropertyType typeRequested) {
        if (typeRequested == null) {
            throw new IllegalArgumentException("typeRequested is null");
        } 
        
        if (typeRequested != ObjPropertyType.OBJ_PROPERTY_IGNORE &&
                typeRequested != ObjPropertyType.OBJ_PROPERTY_RESIST &&
                typeRequested != ObjPropertyType.OBJ_PROPERTY_VULN &&
                typeRequested != ObjPropertyType.OBJ_PROPERTY_IMM) {
            throw new IllegalArgumentException("Illegal Type requested. Expected one of "
                    + " OBJ_PROPERTY_IGNORE, OBJ_PROPERTY_RESIST, OBJ_PROPERTY_VULN, or" +
                    " OBJ_PROPERTY_IMM, received "
                    + typeRequested.name());
        }

        return this.element;
    }

    /**
     * Retrieve the flag payload. Only that {@code typeRequested} is {@code OBJ_PROPERTY_FLAG} is checked, not that the
     * wrapper itself holds a flag. A wrapper holding a modifier or an element has no flag and returns {@code null}.
     *
     * <p>Function getFlag coded before 260827, commented in full on 261115.
     *
     * @param typeRequested the expected type (must be {@code OBJ_PROPERTY_FLAG})
     * @return the stored flag, or {@code null} if this wrapper holds a different kind of payload
     * @throws IllegalArgumentException if {@code typeRequested} is {@code null} or not {@code OBJ_PROPERTY_FLAG}
     */
    public ObjectFlag getFlag(ObjPropertyType typeRequested) {
        if (typeRequested == null) {
            throw new IllegalArgumentException("typeRequested is null");
        }
        
        if (typeRequested != ObjPropertyType.OBJ_PROPERTY_FLAG) {
            throw new IllegalArgumentException("Illegal Type requested. Expected OBJ_PROPERTY_FLAG, received "
                    + typeRequested.name());
        }

        return this.flag;
    }

    /**
     * Equality over the three payload slots; the discriminator takes no part (see below). Comparing every slot rather
     * than just the one the discriminator selects is safe because the unused slots are always {@code null}: each
     * constructor sets exactly one. The NONE wrapper sets the element slot to {@code ELEM_NONE}, so it equals only
     * another NONE wrapper.
     *
     * <p>This is what makes the wrapper usable as a lookup key, which is how property definitions
     * are found — a caller builds a wrapper describing the flag or modifier it wants and matches it
     * against the loaded properties. There is no {@code hashCode} to match, so it is only sound for
     * linear searches, not hash-based ones.
     *
     * <p>The discriminator is left out on purpose. C's {@code lookup_obj_property()} in {@code obj-properties.c} tests
     * {@code prop->type == type} and {@code prop->index == index} as two separate clauses, then adds a second clause that
     * lets a mod request match a stat property. {@code ObjectRegistry.lookupObjectProperty} makes the type test, so this
     * method supplies only the index half: a wrapper built with {@code OBJ_PROPERTY_STAT} equals one built with
     * {@code OBJ_PROPERTY_MOD} over the same modifier.
     *
     * <p>Function equals coded before 260827, commented in full on 261113.
     *
     * @param obj the object to compare against
     * @return {@code true} if {@code obj} is a wrapper with the same payload in all three slots, whatever its
     * discriminator
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectPropertyTypeWrapper other)) return false;
//      The tag deliberately takes no part in identity, so the line below stays commented out. C keeps the tag and
//      the subject as two independent fields (struct obj_property in obj-properties.h) and compares them separately, which is what lets
//      it relax the tag for the "stats count as mods" special case. This port folded the tag into the payload, so
//      comparing it here as well made lookupObjectProperty compare it twice and killed that special case: a rune
//      asking for OBJ_PROPERTY_MOD/OM_STR could never match STR, which the data declares as type:stat. Comparing
//      subjects only leaves the tag to be compared exactly once, by lookupObjectProperty, as in C.
//        if (other.type != this.type) return false;
        if (other.flag != this.flag) return false;
        if (other.modifier != this.modifier) return false;
        return other.element == this.element;
    }
}