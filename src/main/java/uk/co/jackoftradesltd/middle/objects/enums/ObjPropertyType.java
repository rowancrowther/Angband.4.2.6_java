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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * The categories an object property can belong to — a stat boost, a modifier, a
 * flag, an ignore, a resistance, a vulnerability or an immunity. Mirrors the C
 * original's {@code OBJ_PROPERTY_*} types ({@code enum obj_property_type} in
 * {@code src/obj-properties.h}). {@code OBJ_PROPERTY_MAX} is the count sentinel
 * and {@code OBJ_PROPERTY_NONE} the zero placeholder, neither of which a data
 * file can name: {@link #fromValue} rejects both, exactly as C's
 * {@code parse_object_property_type} rejects any word it does not list.
 *
 * <p>The string beside each real constant is the exact {@code type:} token used in
 * {@code object_property.txt}, matched by {@link #fromValue}. These deliberately
 * mirror C's {@code parse_object_property_type} ({@code obj-init.c}), which
 * {@code streq}s the full words — hence {@code "resistance"}/{@code "vulnerability"}/
 * {@code "immunity"} in full rather than abbreviations. The tokens beside
 * {@code NONE} ({@code ""}) and {@code MAX} ({@code "max"}) exist only so every
 * constant has a value; no C branch accepts them.
 *
 * <p>The category also decides which table a property's {@code code:} resolves
 * against: {@code STAT} and {@code MOD} both index {@code obj_mods}, {@code FLAG}
 * indexes {@code obj_flags}, and {@code IGNORE}/{@code RESIST}/{@code VULN}/
 * {@code IMM} all index {@code element_names}.
 *
 * <p>Two lookups exist, and they differ on purpose. {@link #fromValue} is the data-file
 * path: it takes a {@code type:} token and refuses {@code NONE} and {@code MAX}.
 * {@link #lookup} is a Java-only helper with no C counterpart: it takes a constant's
 * own name and, being a plain name match, does resolve {@code NONE} and {@code MAX}.
 *
 * <p>Enum {@code ObjPropertyType} coded before 261009, updated on 261009 so that
 * {@code NONE} and {@code MAX} stop resolving, {@code lookup} added by 261115,
 * commented in full on 261115.
 *
 * @author Rowan Crowther
 */
public enum ObjPropertyType {    
    /**
     * C's {@code OBJ_PROPERTY_NONE}, the zero value a property holds before any
     * {@code type:} line has been read. Not a data-file token: {@link #fromValue}
     * returns {@code null} for it.
     *
     * <p>A record that never sets a type keeps this value and loads, as it does in C, where
     * {@code parse_object_property_name} zero-fills the struct. The assembler assigns the
     * constant itself when the record's type is absent, without calling {@link #fromValue},
     * and pairs it with the NONE {@code ObjectPropertyTypeWrapper}. A {@code code:} or
     * {@code bindui:} on such a record ends the load, as C's
     * {@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE} does.
     *
     * <p>Constant {@code OBJ_PROPERTY_NONE} coded before 261009, commented in full on 261113.
     */
    OBJ_PROPERTY_NONE(""),
    /**
     * A stat boost ({@code type:stat}); its {@code code:} indexes {@code obj_mods}.
     *
     * <p>Constant {@code OBJ_PROPERTY_STAT} coded before 261009, commented in full on 261009.
     */
    OBJ_PROPERTY_STAT("stat"),
    /**
     * A non-stat modifier ({@code type:mod}); its {@code code:} indexes {@code obj_mods}.
     *
     * <p>Constant {@code OBJ_PROPERTY_MOD} coded before 261009, commented in full on 261009.
     */
    OBJ_PROPERTY_MOD("mod"),
    /**
     * An object flag ({@code type:flag}); its {@code code:} indexes {@code obj_flags}.
     * The only category that may carry a {@code subtype:}.
     *
     * <p>Constant {@code OBJ_PROPERTY_FLAG} coded before 261009, commented in full on 261009.
     */
    OBJ_PROPERTY_FLAG("flag"),
    /**
     * An ignore on an element ({@code type:ignore}); its {@code code:} indexes
     * {@code element_names}.
     *
     * <p>Constant {@code OBJ_PROPERTY_IGNORE} coded before 261009, commented in full on 261009.
     */
    OBJ_PROPERTY_IGNORE("ignore"),
    /**
     * A resistance to an element ({@code type:resistance}); its {@code code:} indexes
     * {@code element_names}.
     *
     * <p>Constant {@code OBJ_PROPERTY_RESIST} coded before 261009, commented in full on 261009.
     */
    OBJ_PROPERTY_RESIST("resistance"),
    /**
     * A vulnerability to an element ({@code type:vulnerability}); its {@code code:}
     * indexes {@code element_names}.
     *
     * <p>Constant {@code OBJ_PROPERTY_VULN} coded before 261009, commented in full on 261009.
     */
    OBJ_PROPERTY_VULN("vulnerability"),
    /**
     * An immunity to an element ({@code type:immunity}); its {@code code:} indexes
     * {@code element_names}.
     *
     * <p>Constant {@code OBJ_PROPERTY_IMM} coded before 261009, commented in full on 261009.
     */
    OBJ_PROPERTY_IMM("immunity"),
    /**
     * C's {@code OBJ_PROPERTY_MAX}, the count sentinel. Not a data-file token:
     * {@link #fromValue} returns {@code null} for {@code "max"}, as C's {@code streq}
     * chain does.
     *
     * <p>Constant {@code OBJ_PROPERTY_MAX} coded before 261009, commented in full on 261009.
     */
    OBJ_PROPERTY_MAX("max");

    /**
     * Logs a rejected {@code type:} token from {@link #fromValue}. The log line is
     * diagnostic only: the caller (the object-property assembler) is what throws
     * {@code DataFileParseException} and ends the load when {@code fromValue} returns
     * {@code null}. {@link #lookup} never logs.
     *
     * <p>Field {@code logger} coded on 261009, commented in full on 261115.
     */
    private final static Logger logger = LogManager.getLogger(ObjPropertyType.class);

    /**
     * The {@code type:} token this constant is written as in the data file. Only the
     * seven real categories are tokens C accepts; {@code NONE} and {@code MAX} carry
     * {@code ""} and {@code "max"} as placeholders that {@link #fromValue} refuses.
     *
     * <p>Field {@code value} coded before 261009, commented in full on 261009.
     */
    private final String value;

    /**
     * Bind a category to its data-file token.
     *
     * <p>Constructor coded before 261009, commented in full on 261009.
     *
     * @param value the {@code type:} token
     */
    ObjPropertyType(String value) {
        this.value = value;
    }

    /**
     * Resolve a data-file {@code type:} token to its category. Case-sensitive and
     * exact, mirroring the {@code streq} chain in C's {@code parse_object_property_type}
     * ({@code obj-init.c}): {@code "stat"}, {@code "mod"}, {@code "flag"},
     * {@code "ignore"}, {@code "resistance"}, {@code "vulnerability"} and
     * {@code "immunity"} resolve; everything else does not.
     *
     * <p>Everything else includes {@code ""} and {@code "max"}, which would otherwise
     * match the placeholder tokens of {@code NONE} and {@code MAX}: C falls through to
     * {@code PARSE_ERROR_INVALID_PROPERTY} for both, so both return {@code null} here
     * (and log an error). {@code "Stat"} and the abbreviation {@code "resist"} are
     * likewise rejected, and a {@code null} token returns {@code null}.
     *
     * <p>Function fromValue coded before 261009, updated on 261009 to reject
     * {@code NONE} and {@code MAX}, commented in full on 261009, {@code @return} updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param value the token from {@code object_property.txt}
     * @return the matching category, or {@code null} if the token is unrecognized
     * or names {@code NONE}/{@code MAX} (the caller throws {@code DataFileParseException}
     * and ends the load, as C returns {@code PARSE_ERROR_INVALID_PROPERTY})
     */
    public static ObjPropertyType fromValue(String value) {
        ObjPropertyType type = Arrays.stream(values())
                .filter(op -> op.getValue().equals(value))
                .findFirst().orElse(null);
        
        if (type == null || type == OBJ_PROPERTY_NONE 
                || type == OBJ_PROPERTY_MAX) {
            logger.error("Invalid property type string: " + value);
            return null;
        }
        
        return type;
    }

    /**
     * Resolve a constant's own name to the constant. Java-only: C has no counterpart, as it
     * indexes {@code enum obj_property_type} directly and its one string-to-type path is
     * {@code parse_object_property_type} ({@code obj-init.c}), which {@link #fromValue}
     * ports.
     *
     * <p>The name may be given in full ({@code "OBJ_PROPERTY_STAT"}) or without its
     * {@code OBJ_PROPERTY_} prefix ({@code "STAT"}); both return
     * {@code OBJ_PROPERTY_STAT}. Matching is case-sensitive and exact, so {@code "stat"},
     * which is the data-file token and not a name, returns {@code null}, as does any
     * other unknown string.
     *
     * <p>Unlike {@link #fromValue}, this is a plain name match: {@code "NONE"} and
     * {@code "MAX"} resolve to {@code OBJ_PROPERTY_NONE} and {@code OBJ_PROPERTY_MAX}.
     * It does not log a miss. A {@code null} argument returns {@code null}, as
     * {@link #fromValue} does for a {@code null} token.
     *
     * <p>Function lookup coded on 261115, commented in full on 261115.
     *
     * @param type the constant's name, with or without the {@code OBJ_PROPERTY_} prefix
     * @return the matching constant, or {@code null} if no constant has that name or the
     * argument is {@code null}
     */
    public static ObjPropertyType lookup(String type) {
        if (type == null) return null;
        
        String searchString;
        if (!type.startsWith("OBJ_PROPERTY_")) searchString = "OBJ_PROPERTY_" + type;
        else searchString = type;
        
        return Arrays.stream(ObjPropertyType.values())
                .filter(op -> op.name().equals(searchString))
                .findFirst().orElse(null);
    }

    /**
     * The token this category is written as, used by {@link #fromValue} to match.
     * Returns the placeholder for {@code NONE} ({@code ""}) and {@code MAX}
     * ({@code "max"}), so callers must not treat a returned token as proof the data
     * file may use it.
     *
     * <p>Function getValue coded before 261009, commented in full on 261009.
     *
     * @return the {@code type:} token for this category
     */
    private String getValue() {
        return value;
    }
}
