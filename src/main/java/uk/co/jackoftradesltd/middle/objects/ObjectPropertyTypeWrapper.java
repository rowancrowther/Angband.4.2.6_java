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

import java.security.InvalidParameterException;

/**
 * The typed form of C's {@code obj_property->index}. In C that field is a bare {@code int} whose meaning depends on
 * the sibling {@code type} field: an index into {@code obj_mods} for a stat or mod, into {@code obj_flags} for a flag,
 * and into {@code element_names} for an ignore, resist, vulnerability or immunity. {@code struct obj_property} lives in
 * {@code obj-properties.h}, and {@code parse_object_property_code()} in {@code obj-init.c} fills the index from the
 * {@code code:} line. Java holds the enum constant itself instead, in whichever of three slots matches, so a reader
 * never has to know which table an index points into.
 *
 * <p>Each constructor accepts only the payload type valid for its discriminator, and throws
 * {@link InvalidParameterException} otherwise, where C has no check at all. The two unused slots stay {@code null}.
 * The discriminator is stored here as well as on {@link ObjectProperty}, which C does not do, so it is deliberately left
 * out of {@link #equals(Object)}.
 *
 * <p>The typed getters check only that the type the caller asks for belongs to the right family, not that it matches
 * the stored {@link #type}. So {@code getModifier(OBJ_PROPERTY_MOD)} on a stat wrapper returns the modifier, which is
 * what lets stats count as mods, and a getter asked about the wrong family of wrapper returns {@code null} rather than
 * throwing.
 *
 * <p>Class ObjectPropertyTypeWrapper coded before 260827, commented in full on 261005.
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
     * The discriminator: which kind of property payload is stored. C keeps this only on {@code obj_property->type}.
     *
     * <p>Field type coded before 260827, commented in full on 261005.
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
     * Payload for a {@code STAT} or {@code MOD} property, C's {@code obj_property->index} read as an {@code OBJ_MOD_*}
     * index. A stat is stored as its modifier, as in C, where both types index {@code obj_mods}. {@code null} for every
     * other type.
     *
     * <p>Field modifier coded before 260827, commented in full on 261005.
     */
    private ObjectModifier modifier;
    /**
     * Payload for an element relation ({@code IGNORE}/{@code RESIST}/{@code VULN}/{@code IMM}), C's
     * {@code obj_property->index} read as an index into {@code element_names}. {@code null} for every other type.
     *
     * <p>Field element coded before 260827, commented in full on 261005.
     */
    private ElementEnum element;

    /**
     * Build a stat/modifier-payload wrapper, the Java form of {@code parse_object_property_code()} in {@code obj-init.c}
     * resolving a {@code code:} line against {@code obj_mods} for a stat or mod property. Any other type is rejected and
     * the rejection logged.
     *
     * <p>Constructor ObjectPropertyTypeWrapper(ObjPropertyType, ObjectModifier) coded before 260827, commented in full
     * on 261005.
     *
     * @param type    must be {@code OBJ_PROPERTY_STAT} or {@code OBJ_PROPERTY_MOD}
     * @param modifier the modifier payload
     * @throws java.security.InvalidParameterException if {@code type} is not a stat/mod type
     */
    public ObjectPropertyTypeWrapper(ObjPropertyType type, ObjectModifier modifier) {
        switch (type) {
            case OBJ_PROPERTY_STAT:
            case OBJ_PROPERTY_MOD:
                this.type = type;
                this.modifier = modifier;
                break;

            default:
                String message = "Illegal type of ObjectProperty passed to constructor.\n"
                        + "Expected ObjectModifier, received " + modifier.getClass().getSimpleName();
                InvalidParameterException ex = new InvalidParameterException(message);
                logger.error(message, ex);
                throw ex;

        }
    }

    /**
     * Build a flag-payload wrapper, the Java form of {@code parse_object_property_code()} in {@code obj-init.c}
     * resolving a {@code code:} line against {@code obj_flags} for a flag property. Any other type is rejected and the
     * rejection logged.
     *
     * <p>Constructor ObjectPropertyTypeWrapper(ObjPropertyType, ObjectFlag) coded before 260827, commented in full on
     * 261005.
     *
     * @param type must be {@code OBJ_PROPERTY_FLAG}
     * @param flag the flag payload
     * @throws InvalidParameterException if {@code type} is not {@code OBJ_PROPERTY_FLAG}
     */
    public ObjectPropertyTypeWrapper(ObjPropertyType type, ObjectFlag flag) throws InvalidParameterException {
        if (type == ObjPropertyType.OBJ_PROPERTY_FLAG) {
            this.type = type;
            this.flag = flag;
        } else {
            String message = "Illegal type of ObjectProperty passed to constructor.\n"
                    + "Expected ObjectFlag, received " + flag.getClass().getSimpleName();
            InvalidParameterException ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }
    }

    /**
     * Build an element-relation-payload wrapper, the Java form of {@code parse_object_property_code()} in
     * {@code obj-init.c} resolving a {@code code:} line against {@code element_names} for an ignore, resist,
     * vulnerability or immunity property. Any other type is rejected and the rejection logged.
     *
     * <p>Constructor ObjectPropertyTypeWrapper(ObjPropertyType, ElementEnum) coded before 260827, commented in full on
     * 261005.
     *
     * @param type   must be one of {@code IGNORE}/{@code RESIST}/{@code VULN}/{@code IMM}
     * @param element the element payload
     * @throws InvalidParameterException if {@code type} is not an element-relation type
     */
    public ObjectPropertyTypeWrapper(ObjPropertyType type, ElementEnum element) throws InvalidParameterException {
        switch (type) {
            case OBJ_PROPERTY_IGNORE:
            case OBJ_PROPERTY_RESIST:
            case OBJ_PROPERTY_VULN:
            case OBJ_PROPERTY_IMM:
                this.type = type;
                this.element = element;
                break;

            default:
                String message = "Illegal type of ObjectProperty passed to constructor.\n"
                        + "Expected ElementEnum, received " + element.getClass().getSimpleName();
                InvalidParameterException ex = new InvalidParameterException(message);
                logger.error(message, ex);
                throw ex;
        }
    }

    /**
     * The discriminator this wrapper was built with, C's {@code obj_property->type}.
     *
     * <p>Function getType coded before 260827, commented in full on 261005.
     *
     * @return which kind of payload is stored
     */
    public ObjPropertyType getType() {
        return type;
    }

    /**
     * Retrieve the modifier payload. Only the family of {@code typeRequested} is checked, not that it equals the stored
     * {@link #type}, so a stat wrapper answers to {@code OBJ_PROPERTY_MOD}. A wrapper holding a flag or an element has
     * no modifier and returns {@code null}.
     *
     * <p>Function getModifier coded before 260827, commented in full on 261005.
     *
     * @param typeRequested the expected type (must be {@code OBJ_PROPERTY_MOD} or {@code OBJ_PROPERTY_STAT})
     * @return the stored modifier, or {@code null} if this wrapper holds a different kind of payload
     * @throws java.security.InvalidParameterException if {@code typeRequested} is neither MOD nor STAT
     */
    public ObjectModifier getModifier(ObjPropertyType typeRequested) {
        if (typeRequested != ObjPropertyType.OBJ_PROPERTY_MOD &&
                typeRequested != ObjPropertyType.OBJ_PROPERTY_STAT) {
            throw new InvalidParameterException("Illegal Type requested. Expected one of "
                    + " OBJ_PROPERTY_MOD, or" +
                    " OBJ_PROPERTY_STAT, received "
                    + typeRequested.name());
        }

        return modifier;
    }

    /**
     * Retrieve the element payload. Only the family of {@code typeRequested} is checked, not that it equals the stored
     * {@link #type}. A wrapper holding a flag or a modifier has no element and returns {@code null}.
     *
     * <p>Function getElement coded before 260827, commented in full on 261005.
     *
     * @param typeRequested the expected type (must be one of {@code IGNORE}/{@code RESIST}/{@code VULN}/{@code IMM})
     * @return the stored element, or {@code null} if this wrapper holds a different kind of payload
     * @throws java.security.InvalidParameterException if {@code typeRequested} is not an element-relation type
     */
    public ElementEnum getElement(ObjPropertyType typeRequested) {
        if (typeRequested != ObjPropertyType.OBJ_PROPERTY_IGNORE &&
                typeRequested != ObjPropertyType.OBJ_PROPERTY_RESIST &&
                typeRequested != ObjPropertyType.OBJ_PROPERTY_VULN &&
                typeRequested != ObjPropertyType.OBJ_PROPERTY_IMM) {
            throw new InvalidParameterException("Illegal Type requested. Expected one of "
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
     * <p>Function getFlag coded before 260827, commented in full on 261005.
     *
     * @param typeRequested the expected type (must be {@code OBJ_PROPERTY_FLAG})
     * @return the stored flag, or {@code null} if this wrapper holds a different kind of payload
     * @throws java.security.InvalidParameterException if {@code typeRequested} is not {@code OBJ_PROPERTY_FLAG}
     */
    public ObjectFlag getFlag(ObjPropertyType typeRequested) {
        if (typeRequested != ObjPropertyType.OBJ_PROPERTY_FLAG) {
            throw new InvalidParameterException("Illegal Type requested. Expected OBJ_PROPERTY_FLAG, received "
                    + typeRequested.name());
        }

        return this.flag;
    }

    /**
     * Equality over the discriminator and all three payload slots. Comparing every slot rather than
     * just the one the discriminator selects is safe because the unused slots are always
     * {@code null}: each constructor sets exactly one.
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
     * <p>Function equals coded before 260827, commented in full on 261005.
     *
     * @param obj the object to compare against
     * @return {@code true} if {@code obj} is a wrapper with the same discriminator and payload
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