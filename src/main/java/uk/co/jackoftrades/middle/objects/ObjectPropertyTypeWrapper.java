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
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.security.InvalidParameterException;

public class ObjectPropertyTypeWrapper {
    private static final Logger logger = LogManager.getLogger();

    private ObjPropertyType type;
    private ObjectFlag flag;
    private ObjectModifier modifier;
    private ElementEnum element;

    private int value;
    private int extraParm;

    public void setValues(int value, int extraParm) {
        this.value = value;
        this.extraParm = extraParm;
    }

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

    public ObjectFlag getFlag(ObjPropertyType typeRequested) {
        if (typeRequested != ObjPropertyType.OBJ_PROPERTY_FLAG) {
            throw new InvalidParameterException("Illegal Type requested. Expected OBJ_PROPERTY_FLAG, received "
                    + typeRequested.name());
        }

        return this.flag;
    }

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