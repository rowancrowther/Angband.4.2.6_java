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
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.security.InvalidParameterException;

/**
 * A type-safe tagged union for an object property's payload. An object property
 * can be a flag, a stat/modifier, or an element relation (ignore/resist/vuln/imm);
 * this wrapper stores the appropriate typed value and records which via
 * {@link #type}. Each typed getter validates that the requested type matches the
 * stored one, throwing if not, and each constructor accepts only the payload
 * types valid for its discriminator. This is the Java port of the C original's
 * union inside {@code struct obj_property} ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class ObjectPropertyTypeWrapper {
    /**
     * Logger used to report mismatched-type construction or access.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The discriminator: which kind of property payload is stored.
     *
     * @author Rowan Crowther
     */
    private ObjPropertyType type;
    /**
     * Payload for a {@code FLAG} property.
     *
     * @author Rowan Crowther
     */
    private ObjectFlag flag;
    /**
     * Payload for a {@code STAT}/{@code MOD} property.
     *
     * @author Rowan Crowther
     */
    private ObjectModifier modifier;
    /**
     * Payload for an element relation ({@code IGNORE}/{@code RESIST}/{@code VULN}/{@code IMM}).
     *
     * @author Rowan Crowther
     */
    private ElementEnum element;

    /**
     * The property's primary numeric value.
     *
     * @author Rowan Crowther
     */
    private int value;
    /**
     * An extra numeric parameter for the property.
     *
     * @author Rowan Crowther
     */
    private int extraParm;

    /**
     * Set the property's numeric value and extra parameter.
     *
     * @param value     the primary value
     * @param extraParm the extra parameter
     * @author Rowan Crowther
     */
    public void setValues(int value, int extraParm) {
        this.value = value;
        this.extraParm = extraParm;
    }

    /**
     * Retrieve the modifier payload.
     *
     * @param typeRequested the expected type (must be {@code OBJ_PROPERTY_MOD} or {@code OBJ_PROPERTY_STAT})
     * @return the stored modifier
     * @throws java.security.InvalidParameterException if {@code typeRequested} is neither MOD nor STAT
     * @author Rowan Crowther
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
     * Retrieve the element payload.
     *
     * @param typeRequested the expected type (must be one of {@code IGNORE}/{@code RESIST}/{@code VULN}/{@code IMM})
     * @return the stored element
     * @throws java.security.InvalidParameterException if {@code typeRequested} is not an element-relation type
     * @author Rowan Crowther
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
     * Retrieve the flag payload.
     *
     * @param typeRequested the expected type (must be {@code OBJ_PROPERTY_FLAG})
     * @return the stored flag
     * @throws java.security.InvalidParameterException if {@code typeRequested} is not {@code OBJ_PROPERTY_FLAG}
     * @author Rowan Crowther
     */
    public ObjectFlag getFlag(ObjPropertyType typeRequested) {
        if (typeRequested != ObjPropertyType.OBJ_PROPERTY_FLAG) {
            throw new InvalidParameterException("Illegal Type requested. Expected OBJ_PROPERTY_FLAG, received "
                    + typeRequested.name());
        }

        return this.flag;
    }

    /**
     * Build a stat/modifier-payload wrapper.
     *
     * @param type     must be {@code OBJ_PROPERTY_STAT} or {@code OBJ_PROPERTY_MOD}
     * @param modifier the modifier payload
     * @throws java.security.InvalidParameterException if {@code type} is not a stat/mod type
     * @author Rowan Crowther
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
     * Build a flag-payload wrapper.
     *
     * @param type must be {@code OBJ_PROPERTY_FLAG}
     * @param flag the flag payload
     * @throws InvalidParameterException if {@code type} is not {@code OBJ_PROPERTY_FLAG}
     * @author Rowan Crowther
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
     * Build an element-relation-payload wrapper.
     *
     * @param type    must be one of {@code IGNORE}/{@code RESIST}/{@code VULN}/{@code IMM}
     * @param element the element payload
     * @throws InvalidParameterException if {@code type} is not an element-relation type
     * @author Rowan Crowther
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
}