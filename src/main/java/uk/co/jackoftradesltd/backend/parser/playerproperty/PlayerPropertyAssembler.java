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

import org.jetbrains.annotations.NotNull;
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
 *   <li>each {@code bindui} into a {@link PlayerProperty.BindUI}, resolving its UI
 *       entry against the {@link GameConstants} registry by the combined
 *       {@code name + tag} string (e.g. {@code stat_mod_ui_compact_0<STR>}) and
 *       carrying the binding's value, {@code special} and {@code aux} flags;</li>
 *   <li>{@code value} - {@code 1}/{@code 3}/{@code -1} - into a
 *       {@link PlayerProperty.PlayerPropertyValue} (resistance/immunity/
 *       vulnerability), defaulting to {@link PlayerProperty.PlayerPropertyValue#NONE}.</li>
 * </ul>
 * <p>
 * An {@code element}-typed record is a template, not a finished property: C's
 * {@code finish_parse_player_prop} ({@code init.c:1326-1393}) expands one such record into
 * one {@code player_ability} per real element, and {@link #spreadPlayerPropertyOut} is that
 * expansion. For each {@link ElementEnum} it looks up the {@link Projection} the element
 * resolves to (by {@link ElementEnum#getProjectionEnum()}, since the port has no positional
 * array to index the way C indexes {@code projections[i]}) and builds the display name and
 * description from {@link Projection#getName()} exactly as C builds them from
 * {@code projections[i].name} - capitalising only the string's first character, the way
 * {@code my_strcap} ({@code z-util.c:529}) does, not every word. The record's own
 * {@code name:}/{@code desc:} text (e.g. {@code "Resistance"}/{@code "You resist"}) supplies
 * the fixed half of each of the two built strings, so the same template correctly yields
 * {@code "Cold Resistance"}, {@code "Fire Immunity"} and {@code "Poison Vulnerability"}
 * depending on which of {@code player_property.txt}'s three element-type records it came
 * from. Each expanded property's {@code bindui} entries are also re-suffixed with
 * {@code <ELEMENT_CODE>}, mirroring C's {@code list_element_names[i]} tag on the bound UI
 * entry name - note this is the element's own code, not the {@link Projection} name, so
 * {@code ELEM_ELEC} tags as {@code <ELEC>} even though it displays as "Lightning".
 * <p>
 * Assembly is best-effort and error-collecting rather than fail-fast, with two
 * tiers of skip depending on how load-bearing the unresolvable field is:
 * <ul>
 *   <li><b>whole-record skip</b> - an unresolvable {@code type}, or an unresolvable
 *       {@code code} for a {@code player}/{@code object} property, drops the entire
 *       record: the code <em>is</em> the property's flag identity, so without it the
 *       record is meaningless;</li>
 *   <li><b>single-binding skip</b> - a {@code bindui} with a non-integer value or an
 *       unresolvable UI-entry target drops only that one binding and the record still
 *       loads with its remaining bindings, mirroring the C side's silent discard of a
 *       failed bind ({@code bind_player_ability_to_ui_entry_by_name}'s return is
 *       {@code (void)}-ignored).</li>
 * </ul>
 * Every skip appends a message (quoting the record's source line) to {@code errors}
 * and processing continues, so one bad record or binding does not hide the rest.
 *
 * @author Rowan Crowther
 */
public class PlayerPropertyAssembler implements Assembler<PlayerPropertyParseRecord, List<PlayerProperty>> {
    /**
     * Resolve each {@link PlayerPropertyParseRecord} into a {@link PlayerProperty},
     * skipping (never throwing on) a record whose {@code type}/{@code code} cannot be
     * resolved and dropping individual bindings whose value or target cannot be
     * resolved. See the class comment for the two-tier skip contract. An
     * {@code element}-typed record is additionally expanded into one {@link PlayerProperty}
     * per real element via {@link #spreadPlayerPropertyOut}; a record whose element cannot be
     * matched to a {@link Projection} contributes nothing (see that method).
     *
     * <p>Function assemble coded before 260915, commented in full on 260924.
     *
     * @param records the raw parse records, in file order, from the grammar.
     * @param errors  the soft-error sink; one message is appended, quoting the
     *                record's source line, for each dropped record or binding.
     *                Mutated in place.
     * @return the successfully assembled {@link PlayerProperty} objects, in file
     * order, omitting any record that was skipped.
     */
    @Override
    public List<PlayerProperty> assemble(@NotNull List<PlayerPropertyParseRecord> records,
                                         @NotNull List<String> errors) {
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
                    } catch (IllegalArgumentException e) {
                        errors.add("Record starting at line: " + line + " has illegal code: " + record.code());
                        continue;
                    }
                } else if (ppt == PlayerProperty.PlayerPropertyType.PROP_TYPE_PLAYER) {
                    try {
                        pFlag = PlayerFlag.valueOf("PF_" + record.code());
                    } catch (IllegalArgumentException e) {
                        errors.add("Record starting at line: " + line + " has illegal code: " + record.code());
                        continue;
                    }
                } else if (ppt == PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT) {
                    try {
                        eCode = ElementEnum.valueOf("ELEM_" + record.code());
                    } catch (IllegalArgumentException e) {
                        errors.add("Record starting at line: " + line + " has illegal code: " + record.code());
                        continue;
                    }
                } else {
                    try {
                        omCode = ObjectModifier.valueOf("OM_" + record.code());
                    } catch (IllegalArgumentException e) {
                        errors.add("Record starting at line: " + line + " has illegal code: " + record.code());
                        continue;
                    }
                }
            }
            
            List<PlayerProperty.BindUI> bindings = new ArrayList<>();
            for (List<String> b : record.bindui()) {
                boolean aux = !b.get(2).equals("0");
                boolean special = b.get(3).equals("special");
                int value;
                try {
                    value = special ? 0 : Integer.parseInt(b.get(3));
                    if (value <= Integer.MIN_VALUE || value >= Integer.MAX_VALUE) {
                        errors.add("Record starting at line: " + line + " has illegal integer value " +
                                "as part of bindui: " + record.bindui());
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Record starting at line: " + line + " has illegal integer value " +
                            "as part of bindui: " + record.bindui());
                    continue;
                }

                bindings.add(new PlayerProperty.BindUI(b.get(0) + b.get(1), value, special, aux));
            }
            String name = record.name();
            String desc = record.desc();
            PlayerProperty.PlayerPropertyValue ppv = switch (record.value()) {
                case "1" -> PlayerProperty.PlayerPropertyValue.RESISTANCE;
                case "3" -> PlayerProperty.PlayerPropertyValue.IMMUNITY;
                case "-1" -> PlayerProperty.PlayerPropertyValue.VULNERABILITY;
                default -> PlayerProperty.PlayerPropertyValue.NONE;
            };

            if (extendToAllElements) {
                for (ElementEnum e : ElementEnum.values()) {
                    if (e == ElementEnum.ELEM_MAX || e == ElementEnum.ELEM_NONE)
                        continue;

                    PlayerProperty pp = new PlayerProperty(ppt, pFlag, oFlag, e, omCode, bindings, name, desc, ppv);

                    pp = spreadPlayerPropertyOut(pp, e);

                    if (pp != null)
                        results.add(pp);
                }
            } else {
                results.add(new PlayerProperty(
                        ppt, pFlag, oFlag, eCode, omCode, bindings, name, desc, ppv)
                );
            }
        }

        return results;
    }

    /**
     * Expands one {@code element}-typed template property into the concrete property for a
     * single element - the per-element body of C's expansion loop in
     * {@code finish_parse_player_prop} ({@code init.c:1332-1352}).
     *
     * <p>Re-suffixes every bound {@code bindui} entry's UI-entry name with {@code <ELEMENT>}
     * (C: {@code list_element_names[i]}, {@code init.c:1348}) and looks up the {@link Projection}
     * {@code e} resolves to via {@link ElementEnum#getProjectionEnum()} - the port's substitute
     * for C's positional {@code projections[i]}, since the two enums carry no shared array
     * index. If no such projection exists (the two enums have drifted apart; see
     * {@link ElementEnum#getProjectionEnum()}) this contributes nothing rather than building a
     * name from a missing lookup, which C cannot do because its indexing is guaranteed valid by
     * {@code parse_projection_code}'s element/order check.
     *
     * <p>Builds the finished display name and description the way C does at
     * {@code init.c:1338-1343}: the name is the projection's {@link Projection#getName()} with
     * only its first character capitalised (C: {@code my_strcap}, {@code z-util.c:529}) followed
     * by {@code pp}'s own name (e.g. {@code "Cold" + " " + "Resistance"}); the description is
     * {@code pp}'s own description followed by the projection name and a trailing period (C:
     * {@code format("%s %s.", ...)}), matching C's asymmetry of putting the period only on the
     * description, never the name.
     *
     * <p>Function spreadPlayerPropertyOut coded on 260924, commented in full on 260924.
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