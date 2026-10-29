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

package uk.co.jackoftradesltd.backend.parser.playerproperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Second stage of the {@code player_property.txt} pipeline: turns the raw string
 * {@link PlayerPropertyParseRecord}s emitted by the {@code PlayerPropertyGrammar}
 * parser into fully resolved {@link PlayerProperty} domain objects. This is where
 * the look-ups the grammar deliberately avoids finally happen (Java port of the
 * {@code parse_player_prop_*} handlers in {@code init.c}):
 * <ul>
 *   <li>{@code type} - {@code player}/{@code object}/{@code element} - into a
 *       {@link PlayerProperty.PlayerPropertyType};</li>
 *   <li>{@code code} into a flag enum: for a {@code player} property the
 *       {@code PF_}-prefixed {@link PlayerFlag}, for an {@code object} property the
 *       {@code OF_}-prefixed {@link ObjectFlag} ({@code element} properties carry no
 *       code, so both default to their {@code _NONE} member);</li>
 *   <li>each {@code bindui} into a {@link PlayerProperty.BindUI} that carries the combined
 *       {@code name + tag} string (e.g. {@code stat_mod_ui_compact_0<STR>}) and the binding's
 *       value, {@code special} and {@code aux} flags. The entry name is kept raw: nothing here
 *       looks it up in the UI-entry registry (see {@link GameConstants}), so a name that matches
 *       no entry survives, where C's {@code bind_player_ability_to_ui_entry_by_name} discards
 *       the binding without a message;</li>
 *   <li>{@code value} - {@code 1} (resistance), {@code 3} (immunity) or {@code -1}
 *       (vulnerability) - into the {@code int} that {@link PlayerProperty#getValue()} returns,
 *       as {@code parse_player_prop_value} stores it with {@code parser_getint}. A record with no
 *       {@code value:} line keeps {@code 0}. C parses the line for every type but only an
 *       {@code element} record's value reaches the stored ability, so this keeps the value for
 *       {@code element} records and gives {@code 0} to the other two.</li>
 * </ul>
 * <p>
 * An {@code element}-typed record is a template, not a finished property: C's
 * {@code finish_parse_player_prop} in {@code init.c} expands one such record into
 * one {@code player_ability} per real element, and {@link #spreadPlayerPropertyOut} is that
 * expansion. For each {@link ElementEnum} it looks up the {@link Projection} the element
 * resolves to (by {@link ElementEnum#getProjectionEnum()}, since the port has no positional
 * array to index the way C indexes {@code projections[i]}) and builds the display name and
 * description from {@link Projection#getName()} exactly as C builds them from
 * {@code projections[i].name} - capitalizing only the string's first character, the way
 * {@code my_strcap} ({@code z-util.c}) does, not every word. The record's own
 * {@code name:}/{@code desc:} text (e.g. {@code "Resistance"}/{@code "You resist"}) supplies
 * the fixed half of each of the two built strings, so the same template correctly yields
 * {@code "Cold Resistance"}, {@code "Fire Immunity"} and {@code "Poison Vulnerability"}
 * depending on which of {@code player_property.txt}'s three element-type records it came
 * from. Each expanded property's {@code bindui} entries are also re-suffixed with
 * {@code <ELEMENT_CODE>}, mirroring C's {@code list_element_names[i]} tag on the bound UI
 * entry name - note this is the element's own code, not the {@link Projection} name, so
 * {@code ELEM_ELEC} tags as {@code <ELEC>} even though it displays as "Lightning".
 * <p>
 * Assembly is fail-fast, as the C parser is: the first bad field ends the load with a
 * {@link DataFileParseException} that names the record's source line and the file. These are
 * refused, each as C refuses it:
 * <ul>
 *   <li>a {@code code} that is not in the flag list for the record's type (object flags with
 *       {@code OF_MAX} refused as well, since C's list ends before it; {@code NONE} is in both
 *       of C's lists and loads);</li>
 *   <li>any {@code code} on an {@code element} record;</li>
 *   <li>a {@code value:} or a {@code bindui} aux that is not a number;</li>
 *   <li>a {@code bindui} value that is neither {@code special} nor a decimal number, or that
 *       is {@code INT_MAX} or beyond.</li>
 * </ul>
 * <p>
 * Accepted deviations, none of which the shipped data can reach:
 * <ul>
 *   <li>(decided 261027) a number outside the {@code int} range is refused or clamped, where C
 *       wraps it to 32 bits;</li>
 *   <li>(decided 261029) a {@code bindui} value of {@code -2147483648} or below loads as
 *       {@code -2147483647}, where C refuses it; only the positive limit is refused;</li>
 *   <li>(decided 261029) a record's bindings stay in file order. C prepends each
 *       {@code bindui:} line to a list and binds from the head, so it binds the last line first.
 *       The order is only observable when one property binds the same UI entry twice, and no
 *       shipped record does;</li>
 *   <li>a record with no {@code desc:} line has the empty string for its description, where C
 *       has {@code NULL}.</li>
 * </ul>
 * <p>
 * The {@code default} arm of the {@code type} switch, which builds a
 * {@link PlayerProperty.PlayerPropertyType#PROP_TYPE_OBJECT_MODIFIER} property and resolves an
 * {@code OM_} code, has no counterpart in C and cannot be reached through the grammar, whose
 * {@code TYPE_VALUE} token admits only {@code player}, {@code object} and {@code element}.
 * Only a hand-built {@link PlayerPropertyParseRecord} gets there.
 *
 * <p>Class PlayerPropertyAssembler coded before 260915, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
public class PlayerPropertyAssembler implements Assembler<PlayerPropertyParseRecord, List<PlayerProperty>> {
    /**
     * Sink for the {@code fatal} line logged just before each {@link DataFileParseException} is
     * thrown, so the message reaches the log as well as the caller.
     *
     * <p>Field logger coded before 260915, commented in full on 261029.
     */
    private static final Logger logger = LogManager.getLogger(PlayerPropertyAssembler.class);

    /**
     * Resolve each {@link PlayerPropertyParseRecord} into a {@link PlayerProperty}, in file order,
     * following the {@code parse_player_prop_*} handlers and {@code finish_parse_player_prop} in
     * {@code init.c}. Per record:
     * <ol>
     *   <li>the {@code type} picks the property type;</li>
     *   <li>a non-empty {@code code} is looked up as {@code PF_<code>} for a {@code player}
     *       record or {@code OF_<code>} for an {@code object} record. {@code OF_MAX} is refused
     *       and {@code NONE} is accepted for both, as C's two lists do. A code on an
     *       {@code element} record is refused;</li>
     *   <li>each {@code bindui} gives a {@link PlayerProperty.BindUI}: the aux is a flag when
     *       non-zero ({@code 00} and {@code -0} are not), {@code special} gives value 0 with the
     *       special flag, and any other value is read in base 10 and refused at {@code INT_MAX}
     *       or {@code INT_MIN} (the negative limit is the accepted deviation in the class
     *       block);</li>
     *   <li>the {@code value:} text is read as {@code parser_getint} does, with no text giving 0.
     *       An {@code element} record keeps it; the other two types store 0, as C leaves the
     *       field zeroed;</li>
     *   <li>an {@code element} record is expanded into one property per element,
     *       {@code ELEM_ACID} to {@code ELEM_ARROW} (C's {@code list_element_names} less its
     *       {@code NULL} end), through {@link #spreadPlayerPropertyOut}. A property whose element
     *       finds no {@link Projection} is left out, which C cannot meet.</li>
     * </ol>
     * Bindings keep file order, not C's reverse order (accepted deviation, class block).
     *
     * <p>Every refusal logs at {@code fatal} and throws, so the {@code errors} list is not
     * written to; it is part of the {@link Assembler} signature only.
     *
     * <p>Function assemble coded before 260915, commented in full on 261029.
     *
     * @param records  the raw parse records, in file order, from the grammar.
     * @param errors   unused here; no refusal is collected any more.
     * @param filename the file being read, quoted in each message.
     * @return the {@link PlayerProperty} objects, in file order, with each {@code element}
     * record expanded in place.
     * @throws DataFileParseException on the first field C refuses.
     */
    @Override
    public List<PlayerProperty> assemble(@NotNull List<PlayerPropertyParseRecord> records,
                                         @NotNull List<String> errors,
                                         @NotNull String filename) {
        List<PlayerProperty> results = new ArrayList<>();

        for (PlayerPropertyParseRecord record : records) {
            int line = record.line();
            PlayerProperty.PlayerPropertyType ppt = switch (record.type()) {
                case "element" -> PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT;
                case "object" -> PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT;
                case "player" -> PlayerProperty.PlayerPropertyType.PROP_TYPE_PLAYER;
                default -> PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT_MODIFIER;
            };
            ObjectFlag oFlag = ObjectFlag.OF_NONE;
            PlayerFlag pFlag = PlayerFlag.PF_NONE;
            ElementEnum eCode = ElementEnum.ELEM_NONE;
            ObjectModifier omCode = ObjectModifier.OM_NONE;
            boolean extendToAllElements = ppt == PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT;

            if (!record.code().isEmpty()) {
                if (ppt == PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT) {
                    try {
                        oFlag = ObjectFlag.valueOf("OF_" + record.code());
                        if (oFlag == ObjectFlag.OF_MAX) {
                            String msg = "Player property starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal sentinel object flag value: " + record.code();
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
                    } catch (IllegalArgumentException e) {
                        String msg = "Player property starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal object flag value: " + record.code();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                } else if (ppt == PlayerProperty.PlayerPropertyType.PROP_TYPE_PLAYER) {
                    try {
                        pFlag = PlayerFlag.valueOf("PF_" + record.code());
                    } catch (IllegalArgumentException e) {
                        String msg = "Player property starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal player flag value: " + record.code();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                } else if (ppt == PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT) {
                    String msg = "Player property starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal code for an element value: " + record.code();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else {
                    try {
                        omCode = ObjectModifier.valueOf("OM_" + record.code());
                        if (omCode == ObjectModifier.OM_NONE || omCode == ObjectModifier.OM_MAX) {
                            String msg = "Player property starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal sentinel object modifier value: " + record.code();
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
                    } catch (IllegalArgumentException e) {
                        String msg = "Player property starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal object modifier value: " + record.code();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
            }
            
            List<PlayerProperty.BindUI> bindings = new ArrayList<>();
            for (List<String> b : record.bindui()) {
                int auxVal = ParseUtils.parseIntString("Player property", b.get(2), line, filename, "aux part of bindui");
                boolean aux = auxVal != 0;
                int value;
                String b3 = b.get(3);
                boolean special = b3.equals("special");
                if (special) {
                    value = 0;
                } else {
                    int sign = ParseUtils.signFound(b3);
                    if (sign != 0) {
                        b3 = b3.substring(1);
                    }
                    
                    if (ParseUtils.onlyDigits(b3)) {
                        value = ParseUtils.parseIntString("Player property", b3, line, filename, "bindui", ParseUtils.Radix.DECIMAL);
                        if (sign != 0)
                            value = value * sign;
                    } else {
                        String msg = "Player property starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid bindui value: " + b.get(3);
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
                if (value == Integer.MIN_VALUE || value == Integer.MAX_VALUE) {
                    String msg = "Player property starting at line: " + line + " of file: " + filename + " has " +
                            "an out of range bindui value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }

                bindings.add(new PlayerProperty.BindUI(b.get(0) + b.get(1), value, special, aux));
            }
            String name = record.name();
            String desc = record.desc();
            int value = ParseUtils.parseIntString("Player property", record.value(), line, filename, "");

            if (extendToAllElements) {
                for (ElementEnum e : ElementEnum.values()) {
                    if (e == ElementEnum.ELEM_MAX || e == ElementEnum.ELEM_NONE)
                        continue;

                    PlayerProperty pp = new PlayerProperty(ppt, pFlag, oFlag, e, omCode, bindings, name, desc, value);

                    pp = spreadPlayerPropertyOut(pp, e);

                    if (pp != null)
                        results.add(pp);
                }
            } else {
                results.add(new PlayerProperty(
                        ppt, pFlag, oFlag, eCode, omCode, bindings, name, desc, 0)
                );
            }
        }

        return results;
    }

    /**
     * Expands one {@code element}-typed template property into the concrete property for a
     * single element - the per-element body of C's expansion loop in
     * {@code finish_parse_player_prop} in {@code init.c}.
     *
     * <p>Re-suffixes every bound {@code bindui} entry's UI-entry name with {@code <ELEMENT>}
     * (C: {@code list_element_names[i]}) and looks up the {@link Projection}
     * {@code e} resolves to via {@link ElementEnum#getProjectionEnum()} - the port's substitute
     * for C's positional {@code projections[i]}, since the two enums carry no shared array
     * index. If no such projection exists (the two enums have drifted apart; see
     * {@link ElementEnum#getProjectionEnum()}) this contributes nothing rather than building a
     * name from a missing lookup, which C cannot do because its indexing is guaranteed valid by
     * {@code parse_projection_code}'s element/order check.
     *
     * <p>Builds the finished display name and description the way C does in
     * {@code finish_parse_player_prop}: the name is the projection's {@link Projection#getName()} with
     * only its first character capitalized (C: {@code my_strcap}, {@code z-util.c}) followed
     * by {@code pp}'s own name (e.g. {@code "Cold" + " " + "Resistance"}); the description is
     * {@code pp}'s own description followed by the projection name and a trailing period (C:
     * {@code format("%s %s.", ...)}), matching C's asymmetry of putting the period only on the
     * description, never the name.
     *
     * <p>The bindings are re-suffixed in the order {@code pp} holds them, which is file order (the
     * accepted deviation in the class block; C walks its reversed list).
     *
     * <p>Function spreadPlayerPropertyOut coded on 260924, commented in full on 261029.
     *
     * @param pp the template property for one {@code type:element} record, carrying that
     *           record's own name/description/bindings/value ahead of expansion
     * @param e  the element to expand {@code pp} for
     * @return the property built for {@code e}, or {@code null} if {@code e} has no matching
     * {@link Projection}
     */
    private PlayerProperty spreadPlayerPropertyOut(PlayerProperty pp, ElementEnum e) {
        List<PlayerProperty.BindUI> newBindings = new ArrayList<>();

        String tag = e.name().substring(5);
        for (PlayerProperty.BindUI bindUI : pp.getEntries()) {
            PlayerProperty.BindUI newBindUI =
                    new PlayerProperty.BindUI(bindUI.uiEntry() + "<" + tag + ">",   // BINDUI<ACID>
                            bindUI.value(), bindUI.special(), bindUI.aux());
            newBindings.add(newBindUI);
        }

        ProjectionEnum projCode = e.getProjectionEnum();

        Projection projection = WorldRegistry.lookupProjectionByCode(projCode);

        if (projection == null)
            return null;

        String projName = projection.getName();

        String resistName = projName.substring(0, 1).toUpperCase() + projName.substring(1) + " "
                + pp.getName();

        String nameStr = pp.getDescription() + " " + projName + ".";

        return new PlayerProperty(pp.getPlayerPropertyType(), pp.getpCode(), pp.getoCode(),
                e, pp.getomCode(), newBindings, resistName, nameStr, pp.getValue());
    }
}