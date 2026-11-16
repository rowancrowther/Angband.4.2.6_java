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
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Assembles the {@link ObjectProperty2ParseRecord}s read from {@code object_property.txt} into
 * finished {@link ObjectProperty} objects (reader &rarr; parse record &rarr; assembler &rarr;
 * domain) by applying each record's {@link Directive}s one at a time, in the order the file gave
 * them. It is the line-ordered counterpart of {@link ObjectPropertyAssembler}, which is handed one
 * final value per field and so cannot tell which line came first.
 *
 * <p>The order matters because C's {@code parse_object_property_*} handlers ({@code obj-init.c})
 * each read and write the one {@code struct obj_property} as it stands when their line arrives.
 * A {@code code:} is resolved against the table the type selects <em>at that line</em>;
 * {@code code:} and {@code bindui:} refuse a record whose type is still 0
 * ({@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE}); every handler other than {@code name:} refuses a line
 * that comes before a {@code name:} ({@code PARSE_ERROR_MISSING_RECORD_HEADER}); and a later
 * {@code type:} only overwrites the type, leaving whatever index it finds. This class does the same
 * line by line, so the first fault in file order is the one reported, a repeated {@code msg:},
 * {@code desc:}, {@code adjective:}, {@code neg-adjective:}, {@code power:} or {@code mult:}
 * replaces the earlier value, a {@code type-mult:} line for a tval already seen replaces that
 * tval's figure, and every {@code bindui:} that names a UI entry binds.
 *
 * <p><b>Handlers.</b> Each directive has a method named for it, standing in for the C handler of
 * the same name: {@link #parseName} ({@code parse_object_property_name}), {@link #parseType},
 * {@link #parseSubtype}, {@link #parseIdType}, {@link #parseCode}, {@link #parsePower},
 * {@link #parseMult}, {@link #parseTypeMult}, {@link #parseAdjective},
 * {@link #parseNegAdjective} ({@code parse_object_property_neg_adj}), {@link #parseMsg},
 * {@link #parseBindui} and {@link #parseDesc}. The grammar does the other half of {@code name:},
 * the start of a new struct, by cutting the file into records. C's
 * {@code finish_parse_object_property}, which copies the linked list into the
 * {@code obj_properties} array, has no counterpart: the list {@link #assemble} returns is that array.
 *
 * <p><b>Fatal path.</b> A directive that cannot be resolved ends the load, as a parser error does
 * in C: the message is logged at fatal level and thrown as a {@link DataFileParseException} that
 * names the file and the line of the directive at fault (a record has no line of its own, so
 * "starting at line" in the text means that directive's line). No record is skipped. Each
 * {@code parseX} method first checks that its directive holds the number of values its grammar rule
 * builds: one, except two for {@code type-mult:} and four for {@code bindui:}. The grammar always
 * builds them, so only a hand-built {@link Directive} reaches that check, and one with no values at
 * all fails earlier, in the code that builds the message, with a
 * {@link StringIndexOutOfBoundsException}.
 *
 * <p><b>State.</b> {@link #nameFound}, {@link #typeFound} and {@link #prevCode} hold what C keeps in
 * the struct between lines. {@link #assemble} resets all three at the start of each record, so
 * nothing carries from one record to the next. One instance can assemble any number of files, one
 * after another, but not two at once.
 *
 * <p><b>Type and code.</b> The type chooses the table a code is read from: {@code stat} and
 * {@code mod} read {@link ObjectModifier}, {@code flag} reads {@link ObjectFlag}, and
 * {@code ignore}, {@code resistance}, {@code vulnerability} and {@code immunity} read
 * {@link ElementEnum}. C's {@code index} is a bare integer, so a {@code type:} line after a
 * {@code code:} line makes the same integer name an entry of the new table. Here the payload is a
 * typed constant, so every {@code type:} line rebuilds it from the carried index
 * ({@link #prevCode}) in the new type's table, and a {@code type:} line before any {@code code:}
 * builds the payload for index 0 of that table.
 *
 * <p><b>The UI binding.</b> C's {@code parse_object_property_bindui} has the parser read the aux
 * and value figures before the handler runs, then calls {@code bind_object_property_to_ui_entry_by_name},
 * which drops the bind silently if {@code ui_entry_search} finds no entry of that name. {@link #parseBindui}
 * keeps that order: it refuses a bad figure first, then asks {@link GameConstants#uiEntryExists}
 * whether an entry has the name (with any {@code <TAG>} folded in), and adds no binding if none
 * does. The names are the specs in {@link GameConstants#entries}, which {@code GameConstants.init}
 * fills from the front end's {@code UIEntriesLoaded} message before it loads
 * {@code object_property.txt}. This class reads that list and never the front end, so it stays on
 * the IO side of the boundary. With the list unset the lookup throws an
 * {@link IllegalStateException}, where C's {@code entries[]} is simply empty.
 *
 * <p><b>Where this class differs from C.</b>
 * <ul>
 *   <li>A {@code type:} line that carries an index past the end of the new type's table is
 *       refused, where C stores the index and loads. Accepted deviation, "A {@code type:} line that
 *       follows a {@code code:} line is refused when the carried index is past the new type's
 *       table, where C loads it" in {@code docs/Accepted_Deviations.md}.</li>
 *   <li>The carried index is the Java enum ordinal. {@link ObjectModifier} and {@link ElementEnum}
 *       each carry a {@code _NONE} placeholder at ordinal 0 that C's {@code obj_mods} and
 *       {@code element_names} do not have, so in those two tables the ordinal is C's index plus one.
 *       {@link ObjectFlag} agrees with C, whose {@code obj_flags} starts with {@code "NONE"}. A
 *       carried index therefore agrees with C within {@code stat}/{@code mod} and within the four
 *       element types, and is read one place apart from C when it crosses between the flag table and
 *       either of the others. The index-0 payload of a record with a type and no code is
 *       {@code OM_STR} or {@code ELEM_ACID}, as in C, because {@link #parsePrevCode} adds the
 *       1.</li>
 *   <li>Names are looked up with {@code lookup} on each enum, which takes the constant's full Java
 *       name ({@code OM_STR}, {@code OF_FREE_ACT}, {@code ELEM_FIRE}) as well as C's name without
 *       the prefix; C's tables accept only the second.</li>
 *   <li>A tval is resolved by {@link TValue#fromName(String)}, which takes more forms than
 *       {@code tval_find_idx}; see {@link #parseTypeMult}.</li>
 *   <li>Numbers are read by {@link ParseUtils#parseIntString}, which clamps a value outside the
 *       range of an {@code int}, where C keeps the low 32 bits. Accepted deviation, "An integer too
 *       big for its C type is clamped, where C wraps it" in {@code docs/Accepted_Deviations.md}.</li>
 *   <li>No defaults are filled in beyond the constructor's. A field with no directive keeps the
 *       value of the {@link ObjectProperty#ObjectProperty()} constructor: a record with no {@code type:} line has
 *       a {@code null} type and a {@code null} payload (C: {@code OBJ_PROPERTY_NONE} and index 0),
 *       and an absent {@code adjective:}, {@code neg-adjective:}, {@code msg:} or {@code desc:}
 *       is {@code null}. {@link ObjectPropertyAssembler} gives such a record the
 *       {@code OBJ_PROPERTY_NONE} type, the NONE wrapper and empty strings instead.</li>
 *   <li>The list is base 0 with no blank slot, where C's {@code obj_properties} starts at index 1.
 *       Accepted deviation, decided 261009, "for the time being".</li>
 * </ul>
 *
 * <p>Class ObjectProperty2Assembler coded on 261114, commented in full on 261114, updated on 261116
 * for the check of the {@code bindui:} entry name.
 *
 * @author Rowan Crowther
 */
public class ObjectProperty2Assembler implements Assembler<ObjectProperty2ParseRecord, List<ObjectProperty>> {
    /**
     * Receives the message of each fatal path, at fatal level, just before the
     * {@link DataFileParseException} carrying the same text is thrown. Nothing else is logged.
     *
     * <p>Field logger coded on 261114, commented in full on 261114.
     */
    private static final Logger logger = LogManager.getLogger(ObjectProperty2Assembler.class);

    /**
     * Whether the current record's {@code name:} line has been applied yet. Set by {@link #assemble}
     * when it reaches the {@code name} directive and cleared at the start of each record. Every
     * other directive tests it through {@link #checkForNameFound}, which stands in for the
     * {@code !prop} test at the top of each C handler.
     *
     * <p>Field nameFound coded on 261114, commented in full on 261114.
     */
    private boolean nameFound;
    /**
     * Whether a {@code type:} line has been applied to the current record yet. Set by
     * {@link #assemble} on reaching a {@code type} directive and cleared at the start of each
     * record. {@link #checkForTypeFound} tests it for {@code code:} and {@code bindui:}, standing in
     * for C's {@code !prop->type} test: once a {@code type:} line has been applied the type is never
     * {@code OBJ_PROPERTY_NONE} again, because {@link #parseType} cannot store it.
     *
     * <p>Field typeFound coded on 261114, commented in full on 261114.
     */
    private boolean typeFound;
    /**
     * The index the current record holds, C's {@code obj_property->index}: the position of the
     * constant the last {@code code:} line resolved to, in C's table for that type, or 0 until one
     * does. {@link #parseCode} sets it, {@link #parsePrevCode} reads it back through the table a new
     * {@code type:} selects, and {@link #assemble} resets it to 0 at the start of each record.
     *
     * <p>It is C's index, not the Java enum ordinal. {@link ObjectModifier} and {@link ElementEnum}
     * carry an {@code OM_NONE} or {@code ELEM_NONE} placeholder at ordinal 0 that C's
     * {@code obj_mods} and {@code element_names} do not have, so for those two the index is the
     * ordinal minus 1 ({@code STR} and {@code ACID} are index 0). {@link ObjectFlag} has no such gap,
     * because {@code OF_NONE} lines up with the {@code "NONE"} that opens C's {@code obj_flags}, so
     * its index is the ordinal.
     *
     * <p>Field prevCode coded on 261114, commented in full on 261114 (the index and ordinal
     * description corrected 261114).
     */
    private int prevCode;

    /**
     * Resolve every parsed record into an {@link ObjectProperty}, in file order, one property per
     * record. For each record a fresh property is started and the three state fields are reset;
     * then each directive is applied in the order it appears, by name:
     * <ul>
     *   <li>{@code name} sets {@link #nameFound} and runs {@link #parseName};</li>
     *   <li>{@code type} runs {@link #checkForNameFound}, sets {@link #typeFound} and runs
     *       {@link #parseType};</li>
     *   <li>{@code code} and {@code bindui} run {@link #checkForNameFound} and then
     *       {@link #checkForTypeFound}, before {@link #parseCode} or {@link #parseBindui};</li>
     *   <li>every other directive ({@code subtype}, {@code id-type}, {@code power}, {@code mult},
     *       {@code type-mult}, {@code adjective}, {@code neg-adjective}, {@code msg},
     *       {@code desc}) runs {@link #checkForNameFound} and then its own {@code parseX} method.</li>
     * </ul>
     * A directive with any other name ends the load ("an unknown directive"); the grammar cannot
     * build one.
     *
     * <p>The grammar always puts {@code name:} first and builds only the thirteen directives above,
     * so the name check and the unknown-directive arm are reached only by a hand-built record. A
     * second {@code name} directive in one record is not an error here: it replaces the name, where
     * C would start another record. The grammar cannot build that either, since a {@code name:} line
     * opens a new record.
     *
     * <p>Function assemble coded on 261114, commented in full on 261114.
     *
     * @param records  the raw records extracted by the grammar, in file order; none may be {@code null}
     * @param filename the file the records came from, named in the message of each failure
     * @return the resolved properties, in file order; empty if there are no records
     * @throws DataFileParseException if a directive is out of order (a directive before the
     *                                {@code name}, or a {@code code} or {@code bindui} before a
     *                                {@code type}), or any directive cannot be resolved; see
     *                                the {@code parseX} methods
     */
    @Override
    public List<ObjectProperty> assemble(@NotNull List<ObjectProperty2ParseRecord> records, String filename) {
        List<ObjectProperty> objectProperties = new ArrayList<>();
        
        for (ObjectProperty2ParseRecord directives : records) {
            ObjectProperty property = new ObjectProperty();
            typeFound = false;
            nameFound = false;
            prevCode = 0;
            
            for (Directive directive : directives.directives()) {
                switch (directive.name()) {
                    case "name" -> {
                        nameFound = true;
                        parseName(property, directive, filename);
                    }
                    case "type" -> {
                        checkForNameFound(directive, filename);
                        typeFound = true;
                        parseType(property, directive, filename);
                    }
                    case "subtype" -> {
                        checkForNameFound(directive, filename);
                        parseSubtype(property, directive, filename);
                    }
                    case "id-type" -> {
                        checkForNameFound(directive, filename);
                        parseIdType(property, directive, filename);
                    }
                    case "code" -> {
                        checkForNameFound(directive, filename);
                        checkForTypeFound(directive, filename);
                        parseCode(property, directive, filename);
                    }
                    case "power" -> {
                        checkForNameFound(directive, filename);
                        parsePower(property, directive, filename);
                    }
                    case "mult" -> {
                        checkForNameFound(directive, filename);
                        parseMult(property, directive, filename);
                    }
                    case "type-mult" -> {
                        checkForNameFound(directive, filename);
                        parseTypeMult(property, directive, filename);
                    }
                    case "adjective" -> {
                        checkForNameFound(directive, filename);
                        parseAdjective(property, directive, filename);
                    }
                    case "neg-adjective" -> {
                        checkForNameFound(directive, filename);
                        parseNegAdjective(property, directive, filename);
                    }
                    case "msg" -> {
                        checkForNameFound(directive, filename);
                        parseMsg(property, directive, filename);
                    }
                    case "bindui" -> {
                        checkForNameFound(directive, filename);
                        checkForTypeFound(directive, filename);
                        parseBindui(property, directive, filename);
                    }
                    case "desc" -> {
                        checkForNameFound(directive, filename);
                        parseDesc(property, directive, filename);
                    }
                    default -> {
                        String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                                "an unknown directive: " + directive.name();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
            }
            
            objectProperties.add(property);
        }
        
        return objectProperties;
    }
    
    /**
     * Refuse a directive that arrives before its record's {@code name:} line, as C's handlers
     * return {@code PARSE_ERROR_MISSING_RECORD_HEADER} when there is no current struct. The grammar
     * opens every record with {@code name:}, so this guards hand-built records only.
     *
     * <p>Function checkForNameFound coded on 261114, commented in full on 261114.
     *
     * @param directive the directive about to be applied; its name and line go in the message
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if {@link #nameFound} is still false
     */
    private void checkForNameFound(Directive directive, String filename) {
        if (!nameFound) {
            String msg = "Object property at line: " + directive.line() + " of file: " + filename + " has " +
                    "a directive: " + directive.name() + " before the name directive has been found";
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
    }
    
    /**
     * Refuse a {@code code:} or {@code bindui:} line that arrives while the record has no type, as
     * C's handlers return {@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE} while {@code prop->type} is 0.
     * This is the check that makes line order matter: a {@code type:} line further down the record
     * does not rescue the line.
     *
     * <p>The message always says "a code directive", including when the refused line is a
     * {@code bindui:}; the directive's own name is not in it.
     *
     * <p>Function checkForTypeFound coded on 261114, commented in full on 261114.
     *
     * @param directive the {@code code} or {@code bindui} directive about to be applied; its line
     *                  goes in the message
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if {@link #typeFound} is still false
     */
    private void checkForTypeFound(Directive directive, String filename) {
        if (!typeFound) {
            String msg = "Object property at line: " + directive.line() + " of file: " + filename + " has " +
                    "a code directive, with no type directive loaded yet";
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
    }
    
    /**
     * Take the {@code name:} line's one value, the text after the colon, as the property's name. The
     * C handler also allocates the struct and sets every type multiplier to 1; here the grammar
     * starts the record, and {@link ObjectProperty#getTypeMult} supplies the 1 for any tval the
     * record does not name.
     *
     * <p>Function parseName coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code name} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value
     */
    private void parseName(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder names = new StringBuilder();
            for (String name : directive.values()) {
                names.append(name);
                names.append(", ");
            }
            
            names = new StringBuilder(names.substring(0, names.length() - 2));
            
            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of name values (should be 1): " + names;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        String name = directive.values().getFirst();
        property.setName(name);
    }
    
    /**
     * Resolve a {@code type:} line: set the property's category, then bring the payload into step
     * with it. The seven tokens are matched exactly, as C's {@code streq} chain matches them:
     * {@code stat}, {@code mod}, {@code flag}, {@code ignore}, {@code resistance},
     * {@code vulnerability} and {@code immunity} give {@code OBJ_PROPERTY_STAT}, {@code _MOD},
     * {@code _FLAG}, {@code _IGNORE}, {@code _RESIST}, {@code _VULN} and {@code _IMM}.
     *
     * <p>The payload follows the type. With no payload yet (no {@code code:} line so far) it is built
     * for index 0 of the new type's table. With a payload of a different type it is rebuilt from the
     * carried index, {@link #prevCode}, in the new type's table by {@link #parsePrevCode}. With a
     * payload of the same type (a repeated {@code type:} line) it is left alone. The category and the
     * payload's own type therefore always agree afterwards.
     *
     * <p>Function parseType coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code type} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value, the token is
     *                                none of the seven (C: {@code PARSE_ERROR_INVALID_PROPERTY}),
     *                                or the carried index is past the end of the new type's table
     *                                (an accepted deviation, see the class comment)
     */
    private void parseType(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder types = new StringBuilder();
            for (String type : directive.values()) {
                types.append(type);
                types.append(", ");
            }
            
            types = new StringBuilder(types.substring(0, types.length() - 2));
            
            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of type values (should be 1): " + types;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        switch (directive.values().getFirst()) {
            case "stat" -> property.setType(ObjPropertyType.OBJ_PROPERTY_STAT); 
            case "mod" -> property.setType(ObjPropertyType.OBJ_PROPERTY_MOD);
            case "flag" -> property.setType(ObjPropertyType.OBJ_PROPERTY_FLAG);
            case "ignore" -> property.setType(ObjPropertyType.OBJ_PROPERTY_IGNORE);
            case "resistance" -> property.setType(ObjPropertyType.OBJ_PROPERTY_RESIST);
            case "vulnerability" -> property.setType(ObjPropertyType.OBJ_PROPERTY_VULN);
            case "immunity" -> property.setType(ObjPropertyType.OBJ_PROPERTY_IMM);
            default -> {
                String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                        "an unknown type: " + directive.values().getFirst();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
        
        if (property.getPayload() != null && property.getPayload().getType() != property.getType()) {
            parsePrevCode(property, filename, directive.line());
        } else if (property.getPayload() == null) {
            prevCode = 0;
            parsePrevCode(property, filename, directive.line());
        }
    }
    
    /**
     * Set the property's payload to the entry at index {@link #prevCode} of the table its current
     * type selects: {@link ObjectModifier} for {@code stat} and {@code mod}, {@link ObjectFlag} for
     * {@code flag}, {@link ElementEnum} for {@code ignore}, {@code resistance},
     * {@code vulnerability} and {@code immunity}. This is the Java form of C's {@code type:} handler
     * leaving {@code index} alone, so that the same integer is now read through another table.
     *
     * <p>{@link #prevCode} is C's index, so it is converted to a position in the Java enum. In
     * {@link ObjectModifier} and {@link ElementEnum} that is {@code prevCode + 1}, because the enum
     * has a placeholder at ordinal 0 that C's table does not: index 0 gives {@code OM_STR} or
     * {@code ELEM_ACID}. In {@link ObjectFlag} it is {@code prevCode}, and index 0 gives
     * {@code OF_NONE}, as C's {@code obj_flags} opens with {@code "NONE"}.
     *
     * <p>The cutoff is the last constant of the enum, not the last entry of C's table.
     * {@code OM_MAX} and {@code ELEM_MAX} sit one slot past C's last real entry (16 modifiers,
     * 25 elements), where C's table holds its terminating NULL, and an index that reads them loads.
     * An index beyond them is refused. The modifier and element checks therefore refuse
     * {@code prevCode >= values().length - 1}, and the flag check {@code prevCode >= values().length}.
     * The default arm, for {@code OBJ_PROPERTY_NONE}, cannot be reached, because {@link #parseType}
     * never stores that type.
     *
     * <p>Function parsePrevCode coded on 261114, commented in full on 261114 (the index and ordinal
     * description corrected 261114).
     *
     * @param property the property being built; its type has just been set, and its payload is replaced
     * @param filename the file being read, for the message
     * @param line     the line of the {@code type:} directive, for the message
     * @throws DataFileParseException if {@link #prevCode} is past the end of the new table ("a
     *                                redirected type value that is out of range"); C stores the
     *                                index and loads
     */
    private void parsePrevCode(ObjectProperty property, String filename, int line) {
        switch(property.getType()) {
            case OBJ_PROPERTY_STAT, OBJ_PROPERTY_MOD -> {
                if (prevCode >= ObjectModifier.values().length - 1) {
                    String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                            "a redirected type value that is out of range; " + prevCode;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                ObjectModifier value = ObjectModifier.values()[prevCode + 1];
                property.setPayload(new ObjectPropertyTypeWrapper(property.getType(), value));
            }
            case OBJ_PROPERTY_FLAG -> {
                if (prevCode >= ObjectFlag.values().length) {
                    String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                            "a redirected type value that is out of range; " + prevCode;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                ObjectFlag value = ObjectFlag.values()[prevCode];
                property.setPayload(new ObjectPropertyTypeWrapper(property.getType(), value));
            }
            case OBJ_PROPERTY_IGNORE, OBJ_PROPERTY_RESIST,
                 OBJ_PROPERTY_VULN, OBJ_PROPERTY_IMM -> {
                if (prevCode >= ElementEnum.values().length - 1) {
                    String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                            "a redirected type value that is out of range; " + prevCode;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                ElementEnum value = ElementEnum.values()[prevCode + 1];
                property.setPayload(new ObjectPropertyTypeWrapper(property.getType(), value));
            }
            default -> {
                String msg = "Object property starting at line: " + line + " of file: " + filename + " has " +
                        "found an illegal property values: " + property.getType() + " when parsing the " +
                        "a type directive following a code directive";
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }
    
    /**
     * Resolve a {@code subtype:} line to an {@link ObjectFlagType} by {@link ObjectFlagType#lookupFromSubtype},
     * the port of C's {@code streq} chain: {@code sustain}, {@code protection}, {@code misc ability},
     * {@code light}, {@code melee}, {@code bad}, {@code dig}, {@code throw} and {@code curse-only}.
     * C stores the subtype on whatever kind of property the record is, so this does not require the
     * type to be {@code flag}. A record with no {@code subtype:} line keeps {@code OFT_NONE}.
     *
     * <p>Function parseSubtype coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code subtype} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value, or the token
     *                                is none of the nine (C: {@code PARSE_ERROR_INVALID_SUBTYPE})
     */
    private void parseSubtype(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder subtypes = new StringBuilder();
            for (String subtype : directive.values()) {
                subtypes.append(subtype);
                subtypes.append(", ");
            }
            
            subtypes = new StringBuilder(subtypes.substring(0, subtypes.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of subtype values (should be 1): " + subtypes;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        ObjectFlagType result = ObjectFlagType.lookupFromSubtype(directive.values().getFirst());
        if (result == null) {
            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an unknown subtype value: " + directive.values().getFirst();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        property.setSubtype(result);
    }
    
    /**
     * Resolve an {@code id-type:} line to an {@link ObjectFlagID} by {@link ObjectFlagID#getFlagID},
     * the port of C's {@code streq} chain: {@code on effect}, {@code timed} and {@code on wield}. As
     * with the subtype, C stores it on any kind of property, so the type is not required to be
     * {@code flag}. A record with no {@code id-type:} line keeps {@code OFID_NONE}.
     *
     * <p>Function parseIdType coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code id-type} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value, or the token
     *                                is none of the three (C: {@code PARSE_ERROR_INVALID_ID_TYPE})
     */
    private void parseIdType(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder ids = new StringBuilder();
            for (String id : directive.values()) {
                ids.append(id);
                ids.append(", ");
            }
            
            ids = new StringBuilder(ids.substring(0, ids.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of id type values (should be 1): " + ids;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        ObjectFlagID result = ObjectFlagID.getFlagID(directive.values().getFirst());
        if (result == null) {
            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an unknown id-type value: " + directive.values().getFirst();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        property.setIdType(result);
    }
    
    /**
     * Resolve a {@code code:} line against the table the property's type selects <em>now</em>, and
     * make the result both the payload and the carried index ({@link #prevCode}). A later
     * {@code type:} line re-reads that index through its own table ({@link #parsePrevCode}); a later
     * {@code code:} line replaces it.
     *
     * <p>The tables follow C's {@code code_index_in_array} calls. {@code stat} and {@code mod} read
     * {@link ObjectModifier}, whose lookup is {@code ObjectModifier.lookup}; a name that finds the
     * {@code OM_NONE} or {@code OM_MAX} placeholder is refused ("an illegal sentinel"), because
     * C's {@code obj_mods} has neither. {@code ignore}, {@code resistance}, {@code vulnerability} and
     * {@code immunity} read {@link ElementEnum}, and refuse {@code ELEM_NONE} and {@code ELEM_MAX}
     * the same way. {@code flag} reads {@link ObjectFlag} and refuses nothing of the kind, because
     * C's {@code obj_flags} starts with {@code "NONE"} and ends with {@code "MAX"}, so both are
     * valid there. A name the table lacks is refused as unknown (C:
     * {@code PARSE_ERROR_INVALID_OBJ_PROP_CODE}). Each lookup also takes the constant's full Java
     * name, which C's tables do not.
     *
     * <p>The caller has already checked that a {@code type:} line came first, so the type is never
     * {@code OBJ_PROPERTY_NONE} here; the default arm is not reachable.
     *
     * <p>Function parseCode coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built; its type selects the table
     * @param directive the {@code code} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value, or the name
     *                                is a placeholder or is not in the selected table
     */
    private void parseCode(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder codes = new StringBuilder();
            for (String code : directive.values()) {
                codes.append(code);
                codes.append(", ");
            }

            codes = new StringBuilder(codes.substring(0, codes.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of code values (should be 1): " + codes;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        String code = directive.values().getFirst();
        
        switch (property.getType()) {
            case OBJ_PROPERTY_STAT, OBJ_PROPERTY_MOD -> {
                ObjectModifier value = ObjectModifier.lookup(code);
                if (value == ObjectModifier.OM_NONE || value == ObjectModifier.OM_MAX) {
                    String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                            "an illegal sentinel Object Modifier value: " + code;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                if (value == null) {
                    String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                            "an unknown Object Modifier value: " + code;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                prevCode = value.ordinal() - 1;
                property.setPayload(new ObjectPropertyTypeWrapper(property.getType(), value));
            }
            case OBJ_PROPERTY_FLAG -> {
                ObjectFlag value = ObjectFlag.lookup(code);
                if (value == null) {
                    String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                            "an unknown Object Property flag value: " + code;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                prevCode = value.ordinal();
                property.setPayload(new ObjectPropertyTypeWrapper(property.getType(), value));
            }             
            case OBJ_PROPERTY_IGNORE, OBJ_PROPERTY_RESIST,
                 OBJ_PROPERTY_VULN, OBJ_PROPERTY_IMM -> {
                ElementEnum value = ElementEnum.lookup(code);
                if (value == ElementEnum.ELEM_NONE || value == ElementEnum.ELEM_MAX) {
                    String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                            "an illegal sentinel Elemental value: " + code;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                if (value == null) {
                    String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                            "an unknown Elemental enum value: " + code;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                prevCode = value.ordinal() - 1;
                property.setPayload(new ObjectPropertyTypeWrapper(property.getType(), value));
            }
            default -> {
                String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                        "found an illegal property values: " + property.getType() + " when parsing the " +
                        "code: " + code;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }
    
    /**
     * Read a {@code power:} line's figure with {@link ParseUtils#parseIntString}, as
     * {@code parser_getint} reads it, and store it as the property's power. A record with no
     * {@code power:} line keeps 0, the zero C's zero-filled struct leaves. A figure outside the range
     * of an {@code int} is clamped to it, where C keeps the low 32 bits (see the class comment).
     *
     * <p>Function parsePower coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code power} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value, or the text
     *                                is not a number {@code strtol} would read
     */
    private void parsePower(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder powers = new StringBuilder();
            for (String power : directive.values()) {
                powers.append(power);
                powers.append(", ");
            }
            powers = new StringBuilder(powers.substring(0, powers.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of power values (should be 1): " + powers;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        int power = ParseUtils.parseIntString("Object property", directive.values().getFirst(), directive.line(), filename, "power");
        property.setPower(power);
    }
    
    /**
     * Read a {@code mult:} line's figure with {@link ParseUtils#parseIntString} and store it as the
     * property's multiplier, in the same way as {@link #parsePower}. A record with no {@code mult:}
     * line keeps 0. This is the property's own {@code mult}, not one of the per-tval figures, which
     * {@link #parseTypeMult} stores.
     *
     * <p>Function parseMult coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code mult} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value, or the text
     *                                is not a number {@code strtol} would read
     */
    private void parseMult(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder mults = new StringBuilder();
            for(String mult :  directive.values()) {
                mults.append(mult);
                mults.append(", ");
            }
            mults = new StringBuilder(mults.substring(0, mults.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of mult values (should be 1): " + mults;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        int mult = ParseUtils.parseIntString("Object property", directive.values().getFirst(), directive.line(), filename, "mult");
        property.setMult(mult);
    }
    
    /**
     * Resolve a {@code type-mult:} line, which holds two values: the tval as written (it may contain
     * a space, such as {@code dragon armor}) and the multiplier. A tval written as digits is read by
     * {@link ParseUtils#parseIntString} and resolved by number, and any other text is resolved by
     * {@link TValue#fromName(String)} as written, with no case change; {@code fromName} stands in for
     * C's {@code tval_find_idx}. The multiplier is read by {@link ParseUtils#parseIntString} (its
     * message names the token {@code tValue}). The pair is then stored with
     * {@link ObjectProperty#putTypeMults}.
     *
     * <p>Lines are applied in order, so a tval written twice, under any spelling, ends on the later
     * figure, as C's {@code type_mult[tval] = mult} does. Only the tvals a record names are stored;
     * {@link ObjectProperty#getTypeMult} supplies the 1 C writes into every slot.
     *
     * <p>{@link TValue#fromName(String)} also accepts a constant name such as {@code soft_armor},
     * which {@code tval_find_idx} does not. The text reaches it as written, so the British
     * {@code armour} in lower case is rewritten to {@code armor} there, as C's {@code de_armour}
     * does, and {@code dragon armour}, {@code soft armour} and {@code hard armour} resolve. The
     * refusal message quotes the text as written.
     *
     * <p>Function parseTypeMult coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code type-mult} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly two values, the tval
     *                                resolves to nothing (C: {@code PARSE_ERROR_UNRECOGNISED_TVAL}),
     *                                or the figure is not a number {@code strtol} would read
     */
    private void parseTypeMult(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 2) {
            StringBuilder typeMults = new StringBuilder();
            for (String typeMult : directive.values()) {
                typeMults.append(typeMult);
                typeMults.append(", ");
            }
            typeMults = new StringBuilder(typeMults.substring(0, typeMults.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of type-mult values (should be 2): " + typeMults;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        boolean isString = true;
        String tValString = directive.values().getFirst();
        int tValInt = 0;
        if (!ParseUtils.nonDigitFound(tValString, ParseUtils.Radix.DECIMAL)) {
            isString = false;
            tValInt = ParseUtils.parseIntString("Object property", directive.values().getFirst(), directive.line(), filename, "type-mult");
        }
        
        String intStr = directive.values().getLast();
        
        TValue tValue;
        if (isString) 
            tValue = TValue.fromName(tValString);
        else
            tValue = TValue.fromName(tValInt);
        
        if (tValue == null) {
            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an unknown TValue value: " + tValString;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        int intValue = ParseUtils.parseIntString("Object property", intStr, directive.line(), filename, "tValue");
        property.putTypeMults(tValue, intValue);
    }

    /**
     * Store an {@code adjective:} line's one value as the property's adjective, replacing any earlier
     * one, as C frees the old string and copies the new. The text is kept as the grammar read it. A
     * record with no {@code adjective:} line keeps {@code null}.
     *
     * <p>Function parseAdjective coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code adjective} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value
     */
    private void parseAdjective(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder adjectives = new StringBuilder();
            for (String adjective : directive.values()) {
                adjectives.append(adjective);
                adjectives.append(", ");
            }
            adjectives = new StringBuilder(adjectives.substring(0, adjectives.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of adjective values (should be 1): " + adjectives;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        property.setAdjective(directive.values().getFirst());
    }

    /**
     * Store a {@code neg-adjective:} line's one value as the property's negative adjective,
     * replacing any earlier one, as {@link #parseAdjective} does for the positive form. A record
     * with no {@code neg-adjective:} line keeps {@code null}.
     *
     * <p>Function parseNegAdjective coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code neg-adjective} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value
     */
    private void parseNegAdjective(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder negAdjectives = new StringBuilder();
            for (String negAdjective : directive.values()) {
                negAdjectives.append(negAdjective);
                negAdjectives.append(", ");
            }
            negAdjectives = new StringBuilder(negAdjectives.substring(0, negAdjectives.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of neg-adjective values (should be 1): " + negAdjectives;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        property.setNegAdjective(directive.values().getFirst());
    }
    
    /**
     * Store a {@code msg:} line's one value as the property's notice message, replacing any earlier
     * one: C frees the old string and copies the new, so a repeated {@code msg:} keeps the last
     * line and does not append. A record with no {@code msg:} line keeps {@code null}.
     *
     * <p>Function parseMsg coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code msg} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value
     */
    private void parseMsg(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder msgs = new StringBuilder();
            for (String msg : directive.values()) {
                msgs.append(msg);
                msgs.append(", ");
            }
            msgs = new StringBuilder(msgs.substring(0, msgs.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of msg values (should be 1): " + msgs;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        property.setMessage(directive.values().getFirst());
    }
    
    /**
     * Resolve a {@code bindui:} line, which always holds four values: the UI entry's name, its
     * {@code <TAG>} or {@code ""} if there is none, the aux figure, and the explicit value or
     * {@code null} if there is none. The tag is folded back into the entry name as {@code name<TAG>}.
     * The aux figure is read by {@link ParseUtils#isTrue}, so any figure {@code strtol} base 0 would
     * read as non-zero makes the binding aux. The explicit value is read by
     * {@link ParseUtils#parseIntString} (message token {@code optional value}); when there is none it
     * stays {@code null}, which keeps C's {@code parser_hasval} distinction between no value and a
     * real 0. The figures are read first, so a bad aux or value stops the load whatever the name, as
     * in C, where the parser reads every field before the handler runs. Only then is the name looked
     * up: the binding is added to the property's list only if an entry has that name, and a line
     * naming no entry adds nothing and raises no error.
     *
     * <p>C binds straight onto the named {@code struct ui_entry}, found by {@code ui_entry_search},
     * and drops the bind silently if no entry has that name. The port has no {@code struct ui_entry}
     * on this side, so it asks {@link GameConstants#uiEntryExists}, which searches the specs in
     * {@link GameConstants#entries}, the list the front end sent over the channel. The name searched
     * is the whole {@code name<TAG>}, compared exactly as C's {@code strcmp} does. This method
     * builds the {@link ObjectProperty.UIBinding}; {@code GameConstants} later groups the bindings
     * by entry.
     *
     * <p>The binding is frozen at its line, as in C, where
     * {@code bind_object_property_to_ui_entry_by_name} is called straight away with the property's
     * {@code type} and {@code index} as they are then. The binding takes the property's current type
     * and payload, which are replaced rather than changed in place by a later {@code type:} or
     * {@code code:}, so a later line does not alter it. A {@code bindui:} before any {@code code:}
     * takes the index-0 payload that {@link #parseType} built, which is C's index 0.
     *
     * <p>Function parseBindui coded on 261114, commented in full on 261114 (the type and code held
     * by the binding described 261116), updated on 261116 for the lookup of the entry name.
     *
     * @param property  the property being built
     * @param directive the {@code bindui} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly four values, the aux
     *                                figure is not a number {@code strtol} would read, or the
     *                                explicit value is not
     * @throws IllegalStateException  if {@link GameConstants#entries} has not been set, which is the
     *                                case until {@code GameConstants.init} has received the front
     *                                end's entries
     */
    private void parseBindui(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 4) {
            StringBuilder binduis = new StringBuilder();
            for (String bind : directive.values()) {
                binduis.append(bind);
                binduis.append(", ");
            }
            binduis = new StringBuilder(binduis.substring(0, binduis.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of bindui values (should be 4): " + binduis;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        String name = directive.values().getFirst();
        String tag = directive.values().get(1);
        String auxString = directive.values().get(2);
        String optionalUID = directive.values().get(3);
        
        String bindingTag = tag.isEmpty() ? "" : "<" + tag + ">";
        String bindingName = name + bindingTag;
                
        Optional<Boolean> isAux = ParseUtils.isTrue(auxString);
        if (isAux.isEmpty()) {
            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an unknown aux 'boolean' value: " + auxString;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        boolean bindingIsAux = isAux.get();
        Integer bindingValue = null;
        if (optionalUID != null) {
            bindingValue = ParseUtils.parseIntString("Object property", optionalUID, directive.line(), filename, "optional value");
        }

        // Check it's a valid UIEntry name
        boolean valid = GameConstants.uiEntryExists(bindingName);
        if (!valid) return;
        
        property.addBoundEntry(new ObjectProperty.UIBinding(bindingName, bindingValue, bindingIsAux, property.getPayload(), property.getType()));
    }
    
    /**
     * Store a {@code desc:} line's one value as the property's description, replacing any earlier
     * one: C frees the old string and copies the new, so a repeated {@code desc:} keeps the last
     * line and does not append. A record with no {@code desc:} line keeps {@code null}.
     *
     * <p>Function parseDesc coded on 261114, commented in full on 261114.
     *
     * @param property  the property being built
     * @param directive the {@code desc} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not hold exactly one value
     */
    private void parseDesc(ObjectProperty property, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder descs = new StringBuilder();
            for (String desc : directive.values()) {
                descs.append(desc);
                descs.append(", ");
            }
            descs = new StringBuilder(descs.substring(0, descs.length() - 2));

            String msg = "Object property starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of desc values (should be 1): " + descs;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        property.setDescription(directive.values().getFirst());
    }
}
