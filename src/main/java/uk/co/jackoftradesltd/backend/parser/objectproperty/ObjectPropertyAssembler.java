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

package uk.co.jackoftradesltd.backend.parser.objectproperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assembles the raw {@link ObjectPropertyParseRecord}s parsed from {@code object_property.txt}
 * into finished {@link ObjectProperty} domain objects (reader &rarr; ParseRecord &rarr; assembler
 * &rarr; domain).
 *
 * <p>It stands in for the resolving half of the {@code parse_object_property_*} handlers in
 * {@code obj-init.c}: {@code parse_object_property_type}, {@code _subtype}, {@code _id_type},
 * {@code _code}, {@code _power}, {@code _mult}, {@code _type_mult} and {@code _bindui}. The
 * handlers that only copy a string ({@code name:}, {@code adjective:}, {@code neg-adjective:},
 * {@code msg:}, {@code desc:}) are passed through unchanged. {@code finish_parse_object_property},
 * which copies C's linked list into the {@code obj_properties} array, has no counterpart here: the
 * list this class returns is that array.
 *
 * <p>Every field that cannot be resolved ends the load. C's handler returns a parser error and
 * {@code init_angband} quits, so this class logs the message at fatal level and throws
 * {@link DataFileParseException} naming the record's line and the file; no record is skipped and
 * the {@code errors} list is never written to. The cases are an unrecognized {@code type:},
 * {@code subtype:} or {@code id-type:}, a {@code code:} that names nothing in the table its type
 * selects, a {@code type-mult:} tval that resolves to nothing, and a number that
 * {@link ParseUtils#parseIntString} cannot read.
 *
 * <p>The checks run in a different order from C's, which meets the lines of a record in file
 * order: here the type is checked first, then the subtype and id-type, the code, the two numbers,
 * the {@code type-mult:} lines and last the {@code bindui:} numbers. A record with more than one
 * fault therefore reports a different one first, and the load ends either way.
 *
 * <p><b>Type.</b> The seven tokens are matched exactly as C's {@code streq} chain matches them.
 * C refuses a {@code code:} line that arrives before any {@code type:} line
 * ({@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE}); the grammar makes {@code type:} the second line of
 * every record, so that case cannot reach this class.
 *
 * <p><b>Subtype and id-type.</b> C stores both on whatever kind of property the record is, and
 * only {@code flag} records carry them in the shipped data. This class does the same: it does
 * not reject a {@code subtype:} or {@code id-type:} on a stat, a modifier or an element. An absent
 * line gives {@code OFT_NONE} or {@code OFID_NONE}, the zero C's zero-filled allocation leaves.
 *
 * <p><b>Code.</b> The table is chosen by the type: {@code stat} and {@code mod} read
 * {@code obj_mods}, {@code flag} reads {@code obj_flags}, and {@code ignore}, {@code resistance},
 * {@code vulnerability} and {@code immunity} read {@code element_names}. Each Java enum has the
 * same members, in the same order, as the C table it stands for. Two sentinels are valid in C and
 * so here: {@code obj_flags} starts with {@code NONE} and its list ends with {@code OF(MAX, "")},
 * so {@code type:flag} accepts {@code code:NONE} and {@code code:MAX}. The other two tables hold
 * only real entries, so {@code NONE} and {@code MAX} on a stat, a modifier or an element end the
 * load. An absent {@code code:} line gives index 0 of the table, which is what C's zeroed
 * {@code index} reads as: {@code OF_NONE}, {@code OM_STR} or {@code ELEM_ACID}.
 *
 * <p><b>Numbers.</b> {@link ParseUtils#parseIntString} reads {@code power:}, {@code mult:} and
 * each {@code type-mult:} figure as {@code parser_getint} does, and an absent line gives 0, the
 * zero C's zero-filled allocation leaves. Accepted deviation ("An integer too big for its C type
 * is clamped, where C wraps it" in {@code docs/Accepted_Deviations.md}): a value outside the range
 * of an {@code int} is clamped to it, where C keeps the low 32 bits.
 *
 * <p><b>Type multipliers.</b> Each key is resolved by {@link TValue#fromName}, which stands in for
 * {@code tval_find_idx}, so a number, a display name in any case and either spelling of "armour"
 * all resolve. Only the tvals the record names are stored; {@link ObjectProperty#getTypeMult}
 * supplies the default of 1 that C writes into every slot before the first line is read. Accepted
 * deviation ("A {@code type-mult:} tval written twice under two spellings can keep the earlier
 * line, where C keeps the later one" in {@code docs/Accepted_Deviations.md}): the record keeps one
 * figure per spelling, so a tval named by two spellings with the first spelling repeated after the
 * second ends on the second's figure, where C ends on the last line.
 *
 * <p><b>The UI binding.</b> C's {@code parse_object_property_bindui} pushes a bound property
 * straight onto the named {@code struct ui_entry} and drops the bind silently if no entry has that
 * name. This class cannot do that, because no UI entry exists yet when the file is read, so it
 * only builds the {@link ObjectProperty.UIBinding} (entry name with its {@code <TAG>} folded in,
 * optional literal value, and the aux flag as {@code aux != 0}) and leaves the matching to
 * {@code GameConstants}. The parse record carries one {@code bindui:} line, so a record that
 * repeats the directive keeps only the last, where C binds every line; no shipped record repeats
 * it. C also binds with the {@code index} the property holds when the {@code bindui:} line is
 * read, so a line above {@code code:} would bind index 0; this class always uses the final
 * {@code code:}. No shipped record puts {@code bindui:} above {@code code:}.
 *
 * <p><b>The result.</b> The list is in file order and holds only real records. C's
 * {@code finish_parse_object_property} stores them from index 1 of {@code obj_properties}, leaves
 * slot 0 blank, and sets {@code z_info->property_max} one higher than the record count. Accepted
 * deviation (decided 261009, "for the time being"): the list here is base 0, has no blank slot,
 * and its size is the record count.
 *
 * <p>Class ObjectPropertyAssembler coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class ObjectPropertyAssembler implements Assembler<ObjectPropertyParseRecord, List<ObjectProperty>> {
    /**
     * Receives the message of each fatal path, at fatal level, just before the
     * {@link DataFileParseException} carrying the same text is thrown. Nothing else is logged.
     *
     * <p>Field logger coded before 260915, commented in full on 261028.
     */
    private static final Logger logger = LogManager.getLogger(ObjectPropertyAssembler.class);

    /**
     * Resolve every parsed {@code object_property.txt} record into an {@link ObjectProperty}: the
     * type, subtype and id-type tokens to their enums, the code to a
     * {@link ObjectPropertyTypeWrapper} against the table the type selects, the integer fields to
     * {@code int}s, the {@code type-mult:} lines to a {@link TValue} map, and, when the record has
     * a {@code bindui:} line, the one {@link ObjectProperty.UIBinding}. An unresolvable field ends
     * the load with a {@link DataFileParseException} (see the class comment for the cases and for
     * every place the result differs from C).
     *
     * <p>The properties come back in file order, one per record, with nothing appended. Where the
     * data file gives no {@code adjective:}, {@code neg-adjective:}, {@code msg:} or {@code desc:}
     * line the property holds the empty string, where C leaves a {@code NULL} pointer.
     *
     * <p>Function assemble coded before 260915, commented in full on 261028.
     *
     * @param records  the raw records extracted by the grammar, in file order
     * @param errors   unused: every fault ends the load, so nothing is appended
     * @param filename the file the records came from, named in the message of each failure
     * @return the resolved properties, in file order
     * @throws DataFileParseException if a type, subtype, id-type, code, tval or number cannot be
     *                                resolved
     */
    @Override
    public List<ObjectProperty> assemble(@NotNull List<ObjectPropertyParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<ObjectProperty> objectProperties = new ArrayList<>();

        for (ObjectPropertyParseRecord record : records) {
            int line = record.line();

            // type: selects both the property's category and the table its code resolves
            // against, so it resolves first. The grammar makes type the second line of every
            // record, so C's PARSE_ERROR_MISSING_OBJ_PROP_TYPE (code before type) cannot arise.
            // An unrecognized type ends the load, as C's PARSE_ERROR_INVALID_PROPERTY does.
            String type = record.type();
            ObjPropertyType objPropertyType;
            if (!type.isEmpty()) {
                objPropertyType = ObjPropertyType.fromValue(type);
                if (objPropertyType == null) {
                    String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal type value: " + type;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            } else {
                String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                        "no type value";
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }

            // subtype: C stores it on whatever the property's type is, so it is not rejected on a
            // non-flag. An empty token resolves to OFT_NONE (the no-subtype case), so
            // getFlagTypeFromSubtype returns null only for a genuinely unrecognized token, which
            // ends the load as C's PARSE_ERROR_INVALID_SUBTYPE does. id-type below is the same,
            // against PARSE_ERROR_INVALID_ID_TYPE.
            String subtype = record.subtype();
            ObjectFlagType flagSubtype = ObjectFlagType.getFlagTypeFromSubtype(subtype);
            if (flagSubtype == null) {
                String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal subtype value: " + subtype;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            ObjectFlagID idType = ObjectFlagID.getFlagID(record.idType());
            if (idType == null) {
                String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal id type value: " + record.idType();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }

            // code: -> the typed "index" wrapper. The table is chosen by type (stat/mod ->
            // ObjectModifier, flag -> ObjectFlag, ignore/resist/vuln/imm -> ElementEnum), so
            // the same code string can mean different things under different types (e.g. LIGHT
            // is a modifier or an element). Resolved unconditionally here, not in the bindui
            // block, so records with no bindui still get an index (mirrors C's code handler).
            // An absent code takes index 0 of the table, as C's zeroed index does. A name in no
            // table ends the load (PARSE_ERROR_INVALID_OBJ_PROP_CODE); NONE and MAX are valid for
            // a flag only, because C's obj_flags holds both and its other two tables neither.
            ObjectPropertyTypeWrapper index;
            String code = record.code();
            if (code.isEmpty()) {
                index = switch (objPropertyType) {
                    case OBJ_PROPERTY_FLAG -> new ObjectPropertyTypeWrapper(objPropertyType, ObjectFlag.OF_NONE);
                    case OBJ_PROPERTY_IGNORE, OBJ_PROPERTY_RESIST, OBJ_PROPERTY_VULN, OBJ_PROPERTY_IMM
                            -> new ObjectPropertyTypeWrapper(objPropertyType, ElementEnum.ELEM_ACID);
                    case OBJ_PROPERTY_MOD, OBJ_PROPERTY_STAT -> new ObjectPropertyTypeWrapper(objPropertyType, ObjectModifier.OM_STR);
                    default -> null;
                };
                if (index == null) {
                    String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal sentinel object property value: " + code;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            } else {
                try {
                    switch (objPropertyType) {
                        case OBJ_PROPERTY_STAT, OBJ_PROPERTY_MOD -> {
                            ObjectModifier om = ObjectModifier.valueOf("OM_" + code);
                            if (om == ObjectModifier.OM_MAX || om == ObjectModifier.OM_NONE) {
                                String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                                        "an illegal sentinel object modifier value: " + code;
                                logger.fatal(msg);
                                throw new DataFileParseException(msg);
                            }
                            index = new ObjectPropertyTypeWrapper(objPropertyType, om);
                        }
                        case OBJ_PROPERTY_FLAG -> {
                            ObjectFlag flag = ObjectFlag.valueOf("OF_" + code);
                            index = new ObjectPropertyTypeWrapper(objPropertyType, flag);
                        }
                        case OBJ_PROPERTY_IGNORE, OBJ_PROPERTY_RESIST,
                             OBJ_PROPERTY_VULN, OBJ_PROPERTY_IMM -> {
                            ElementEnum elementEnum = ElementEnum.valueOf("ELEM_" + code);
                            if (elementEnum == ElementEnum.ELEM_MAX || elementEnum == ElementEnum.ELEM_NONE) {
                                String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                                        "an illegal sentinel element value: " + code;
                                logger.fatal(msg);
                                throw new DataFileParseException(msg);
                            }
                            index = new ObjectPropertyTypeWrapper(objPropertyType, elementEnum);
                        }
                        default -> {
                            String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal property type value: " + record.type();
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal code value: " + code;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            int power = ParseUtils.parseIntString("Object property", record.power(), line, filename, "power");
            int mult = ParseUtils.parseIntString("Object property", record.mult(), line, filename, "mult");
            // type-mult: -> per-tval multiplier map. The key is the tval text as written in the
            // data file (e.g. "gloves", "dragon armor"), resolved by TValue.fromName as C's
            // tval_find_idx does; a key that resolves to nothing ends the load
            // (PARSE_ERROR_UNRECOGNISED_TVAL). Absent tvals are assumed 1 by consumers. Keys
            // arrive in file order, but a tval written under two spellings can still end on the
            // earlier line's figure (see the class comment).
            Map<TValue, Integer> typeMults = new HashMap<>();
            for (String key : record.typeMult().keySet()) {
                String value = record.typeMult().get(key);
                TValue tValue = TValue.fromName(key);
                if (tValue == null) {
                    String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal tValue value: " + key;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                int multVal = ParseUtils.parseIntString("Object property", value, line, filename, "tValue mult");
                typeMults.put(tValue, multVal);
            }
            String name = record.name();
            String adjective = record.adjective();
            String negAdjective = record.negAdjective();
            String message = record.msg();
            String description = record.desc();
            // bindui: -> at most one UI-entry binding. Guarded so a record with no bindui line
            // keeps an empty list rather than a binding to nothing (C only pushes a bound
            // property when the directive is present). The list shape allows more than one bind
            // as the file format permits, but the parse record carries a single bindui line, so
            // a repeated directive keeps only the last (see the class comment).
            List<ObjectProperty.UIBinding> boundEntries = new ArrayList<>();
            if (!record.bindui().isEmpty()) {
                // aux is the mandatory second param; C treats any non-zero value as the aux flag.
                int auxValue = ParseUtils.parseIntString("Object property", record.aux(), line, filename, "aux");
                boolean isAux = auxValue != 0;
                // value is the optional third param: null means "send the property's natural
                // value" (C's parser_hasval == false), a number means "send this literal value".
                Integer value = null;
                String val = record.value();
                if (val != null) {
                    value = ParseUtils.parseIntString("Object property", record.value(), line, filename, "optional property");
                }
                // The <TAG> is part of the target entry's name in ui_entry.txt (e.g.
                // "resist_ui_compact_0<ACID>"), not a separate field, so it is folded into the
                // lookup key. The name is not looked up here: GameConstants matches it later, and
                // a name that matches no entry is dropped there, as C silently drops the bind.
                String uiEntryName = record.bindui();
                if (!record.tag().isEmpty())
                    uiEntryName = uiEntryName + "<" + record.tag() + ">";
                // UIEntry uientry = UIRegistry.getUIEntry(uiEntryName);
                ObjectProperty.UIBinding uibinding = new ObjectProperty.UIBinding(uiEntryName, value, isAux);
                boundEntries.add(uibinding);
            }

            objectProperties.add(new ObjectProperty(objPropertyType, flagSubtype, idType,
                    index, power, mult, typeMults, name, adjective, negAdjective,
                    message, description, boundEntries));
        }

        return objectProperties;
    }
}
