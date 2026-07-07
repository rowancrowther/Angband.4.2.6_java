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

package uk.co.jackoftrades.backend.parser.playerproperty;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerProperty;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

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
     * resolved. See the class comment for the two-tier skip contract.
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
                default -> null;
            };
            if (ppt == null) {
                errors.add("Record starting at line: " + line + " has illegal type");
                continue;
            }
            ObjectFlag oFlag = ObjectFlag.OF_NONE;
            PlayerFlag pFlag = PlayerFlag.PF_NONE;
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
            }
            List<PlayerProperty.BindUI> bindings = new ArrayList<>();
            for (List<String> b : record.bindui()) {
                boolean aux = !b.get(2).equals("0");
                boolean special = b.get(3).equals("special");
                int value = 0;
                try {
                    value = special ? 0 : Integer.parseInt(b.get(3));
                } catch (NumberFormatException e) {
                    errors.add("Record starting at line: " + line + " has illegal integer value " +
                            "as part of bindui: " + record.bindui());
                    continue;
                }
                UIEntry entry = GameConstants.getUIEntry(b.get(0) + b.get(1));
                if (entry == null) {
                    errors.add("Record starting at line: " + line + " has illegal UIEntry: "
                            + record.bindui());
                    continue;
                }

                bindings.add(new PlayerProperty.BindUI(entry, value, special, aux));
            }
            String name = record.name();
            String desc = record.desc();
            PlayerProperty.PlayerPropertyValue ppv = switch (record.value()) {
                case "1" -> PlayerProperty.PlayerPropertyValue.RESISTANCE;
                case "3" -> PlayerProperty.PlayerPropertyValue.IMMUNITY;
                case "-1" -> PlayerProperty.PlayerPropertyValue.VULNERABILITY;
                default -> PlayerProperty.PlayerPropertyValue.NONE;
            };

            results.add(new PlayerProperty(
                    ppt, pFlag, oFlag, bindings, name, desc, ppv)
            );
        }

        return results;
    }
}