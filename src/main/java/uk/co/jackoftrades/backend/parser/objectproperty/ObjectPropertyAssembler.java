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

package uk.co.jackoftrades.backend.parser.objectproperty;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.ObjectProperty;
import uk.co.jackoftrades.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns the raw {@link ObjectPropertyParseRecord}s from
 * {@code ObjectPropertyGrammar} into resolved
 * {@link ObjectProperty} domain objects — the type-checking half of the
 * object-property pipeline (reader → ParseRecord → Assembler), and the Java port
 * of C's {@code parse_object_property_*} directive handlers ({@code obj-init.c}).
 *
 * <p>Per record it resolves the {@code type}/{@code subtype}/{@code id-type}
 * tokens to their enums, resolves {@code code} to a typed
 * {@link ObjectPropertyTypeWrapper} against the table its {@code type} selects,
 * parses the integer fields, builds the per-tval {@code type-mult} map, and — only
 * when the record carries a {@code bindui} line — resolves the UI-entry binding.
 *
 * <p>Error handling is best-effort and per record, matching the rest of the suite:
 * an unresolvable value appends a message to {@code errors} and (where the value is
 * essential to the record's identity, such as {@code type} or {@code code})
 * {@code continue}s to the next record, so one bad record never aborts the load.
 *
 * @author Rowan Crowther
 */
public class ObjectPropertyAssembler implements Assembler<ObjectPropertyParseRecord, List<ObjectProperty>> {
    /**
     * Resolve every parsed record into an {@link ObjectProperty}, collecting any
     * soft errors rather than throwing.
     *
     * @param records the raw records extracted by the grammar
     * @param errors  out-parameter: one message is appended per unresolvable field
     * @return the resolved properties (records with an essential unresolvable field
     * are omitted and reported via {@code errors})
     * @author Rowan Crowther
     */
    @Override
    public List<ObjectProperty> assemble(@NotNull List<ObjectPropertyParseRecord> records, @NotNull List<String> errors) {
        List<ObjectProperty> objectProperties = new ArrayList<>();

        for (ObjectPropertyParseRecord record : records) {
            int line = record.line();

            // type: selects both the property's category and the table its code resolves
            // against, so it must resolve first (C errors PARSE_ERROR_MISSING_OBJ_PROP_TYPE
            // if code precedes type). An unrecognised type makes the record meaningless.
            String type = record.type();
            ObjPropertyType objPropertyType = ObjPropertyType.fromValue(type);
            if (objPropertyType == null) {
                errors.add("Object Property at line: " + line + " has " +
                        "an illegal type: " + type);
                continue;
            }

            // subtype: only ever appears on flags; reject it on any other category. An empty
            // token resolves to OFT_NONE (the no-subtype case), so getFlagTypeFromSubtype
            // returns null only for a genuinely unrecognised token.
            String subtype = record.subtype();
            if (!subtype.isEmpty() && objPropertyType != ObjPropertyType.OBJ_PROPERTY_FLAG) {
                errors.add("Object Property at line: " + line + " has " +
                        "a subtype on a non flag type: " + subtype);
                continue;
            }
            ObjectFlagType flagSubtype = ObjectFlagType.getFlagTypeFromSubtype(subtype);
            if (flagSubtype == null) {
                errors.add("Object Property at line: " + line + " has " +
                        "an unknown subtype: " + subtype);
                continue;
            }
            ObjectFlagID idType = ObjectFlagID.getFlagID(record.idType());
            if (idType == null) {
                errors.add("Object Property at line: " + line + " has " +
                        "an unknown idType: " + record.idType());
                continue;
            }

            // code: -> the typed "index" wrapper. The table is chosen by type (stat/mod ->
            // ObjectModifier, flag -> ObjectFlag, ignore/resist/vuln/imm -> ElementEnum), so
            // the same code string can mean different things under different types (e.g. LIGHT
            // is a modifier or an element). Resolved unconditionally here, not in the bindui
            // block, so records with no bindui still get an index (mirrors C's code handler).
            String code = record.code();
            ObjectPropertyTypeWrapper index = null;
            try {
                switch (objPropertyType) {
                    case OBJ_PROPERTY_STAT, OBJ_PROPERTY_MOD -> {
                        ObjectModifier om = ObjectModifier.valueOf("OM_" + code);
                        index = new ObjectPropertyTypeWrapper(objPropertyType, om);
                    }
                    case OBJ_PROPERTY_FLAG -> {
                        ObjectFlag flag = ObjectFlag.valueOf("OF_" + code);
                        index = new ObjectPropertyTypeWrapper(objPropertyType, flag);
                    }
                    case OBJ_PROPERTY_IGNORE, OBJ_PROPERTY_RESIST,
                         OBJ_PROPERTY_VULN, OBJ_PROPERTY_IMM -> {
                        ElementEnum elementEnum = ElementEnum.valueOf("ELEM_" + code);
                        index = new ObjectPropertyTypeWrapper(objPropertyType, elementEnum);
                    }
                    default -> {
                        errors.add("Object Property at line: " + line + " has " +
                                "an invalid property type: " + record.type());
                    }
                }
            } catch (IllegalArgumentException e) {
                // valueOf throws when the code is not a constant of the chosen enum.
                errors.add("Object Property at line: " + line + " has " +
                        "an invalid code: " + code);
                continue;
            }
            int power = 0;
            if (!record.power().isEmpty()) {
                try {
                    power = Integer.parseInt(record.power());
                } catch (NumberFormatException e) {
                    errors.add("Object Property at line: " + line + " has " +
                            "a malformed power integer: " + record.power());
                    continue;
                }
            }
            int mult = 0;
            if (!record.mult().isEmpty()) {
                try {
                    mult = Integer.parseInt(record.mult());
                } catch (NumberFormatException e) {
                    errors.add("Object Property at line: " + line + " has " +
                            "a malformed mult integer: " + record.mult());
                    continue;
                }
            }
            // type-mult: -> per-tval multiplier map. The key is the tval's data-file name
            // (e.g. "gloves", "dragon armor"), so it resolves via TValue.fromName, which
            // upper-cases and swaps spaces for underscores — fromName does not prepend "TV_"
            // itself, so the prefix here is required. Absent tvals are assumed 1 by consumers.
            Map<TValue, Integer> typeMults = new HashMap<>();
            for (String key : record.typeMult().keySet()) {
                String value = record.typeMult().get(key);
                try {
                    TValue tValue = TValue.fromName("TV_" + key);
                    int multVal = Integer.parseInt(value);
                    typeMults.put(tValue, multVal);
                } catch (NumberFormatException e) {
                    errors.add("Object Property at line: " + line + " has " +
                            "a malformed type-mult integer: " + value);
                    continue;
                } catch (IllegalArgumentException e) {
                    errors.add("Object Property at line: " + line + " has " +
                            "an invalid type-mult type: " + key);
                    continue;
                }
            }
            String name = record.name();
            String adjective = record.adjective();
            String negAdjective = record.negAdjective();
            String message = record.msg();
            String description = record.desc();
            // bindui: -> at most one UI-entry binding. Guarded so a record with no bindui line
            // keeps an empty list rather than a binding to nothing (C only pushes a bound
            // property when the directive is present). The list shape allows more than one bind
            // as the file format permits, though no current record uses more than one.
            List<ObjectProperty.UIBinding> boundEntries = new ArrayList<>();
            if (!record.bindui().isEmpty()) {
                // aux is the mandatory second param; C treats any non-zero value as the aux flag.
                boolean isAux = !record.aux().equals("0");
                // value is the optional third param: null means "send the property's natural
                // value" (C's parser_hasval == false), a number means "send this literal value".
                Integer value = null;
                String val = record.value();
                if (val != null) {
                    if (!val.isEmpty()) {
                        try {
                            value = Integer.parseInt(val);
                        } catch (NumberFormatException e) {
                            errors.add("Object Property at line: " + line + " has " +
                                    "a malformed bindui integer: " + val);
                            continue;
                        }
                    }
                } else {
                    value = null;
                }
                // The <TAG> is part of the target entry's name in ui_entry.txt (e.g.
                // "resist_ui_compact_0<ACID>"), not a separate field, so it is folded into the
                // lookup key. An unresolvable name yields a null entry (C silently drops the bind).
                String uiEntryName = record.bindui();
                if (!record.tag().isEmpty())
                    uiEntryName = uiEntryName + "<" + record.tag() + ">";
                UIEntry uientry = GameConstants.getUIEntry(uiEntryName);
                ObjectProperty.UIBinding uibinding = new ObjectProperty.UIBinding(uientry, value, isAux);
                boundEntries.add(uibinding);
            }

            objectProperties.add(new ObjectProperty(objPropertyType, flagSubtype, idType,
                    index, power, mult, typeMults, name, adjective, negAdjective,
                    message, description, boundEntries));
        }

        return objectProperties;
    }
}
