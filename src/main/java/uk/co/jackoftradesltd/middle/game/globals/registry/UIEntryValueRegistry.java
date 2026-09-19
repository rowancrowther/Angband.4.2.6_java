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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.jetbrains.annotations.TestOnly;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.uichannel.UIEntryValue;
import uk.co.jackoftradesltd.channel.utils.Combiner;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.channel.utils.UIEntryCombinerState;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIEntryValueRegistry {
    private static Map<String, EntryBinding> bindingByEntry;

    @TestOnly
    public static void clearEntryBindings() {
        bindingByEntry = new HashMap<>();
    }

    public static void addEntryBinding(String entryName, List<ObjectProperty> objProperties,
                                       List<PlayerProperty> playerProperties, CombinerName combinerName,
                                       FlagView<ChannelEntryFlag> entryFlags) {
        if (bindingByEntry == null) {
            bindingByEntry = new HashMap<>();
        }

        EntryBinding existing = bindingByEntry.get(entryName);
        if (objProperties == null && existing != null && existing.objectProperties() != null) {
            objProperties = existing.objectProperties();
        }

        if (playerProperties == null && existing != null && existing.playerProperties() != null) {
            playerProperties = existing.playerProperties();
        }

        bindingByEntry.put(entryName, new EntryBinding(objProperties, playerProperties, combinerName, entryFlags));
    }

    public static UIEntryValue computeForObject(String entryName, ItemObject item, Player player, ObjectValueCache cache) {
        EntryBinding props = bindingByEntry.getOrDefault(entryName, null);
        if (props == null) return new UIEntryValue(Combiner.UI_ENTRY_VALUE_NOT_PRESENT,
                Combiner.UI_ENTRY_VALUE_NOT_PRESENT, false);

        List<ObjectProperty> bound = props.objectProperties();

        if (item == null || bound == null || bound.isEmpty()) {
            return new UIEntryValue(Combiner.UI_ENTRY_VALUE_NOT_PRESENT,
                    Combiner.UI_ENTRY_VALUE_NOT_PRESENT, false);
        }

        cache.populateFlags(item, player);

        UIEntryCombinerState cst;

        Combiner combiner = props.combinerName().getCombiner();
        boolean first = true;
        boolean anyAux = false;
        boolean allAux = true;

        for (ObjectProperty prop : bound) {

            ObjectProperty.UIBinding binding = prop.getBoundEntries().stream()
                    .filter(b -> b.entry().equals(entryName)).findFirst().orElseThrow();
            int v;
            int a = 0;

            if (binding.aux()) {
                anyAux = true;
            } else {
                allAux = false;
            }

            switch (prop.getType()) {
                case OBJ_PROPERTY_STAT, OBJ_PROPERTY_MOD -> {
                    ObjectModifier modifier = prop.getPayload().getModifier(prop.getType());
                    if (player == null || player.getItemKnowledge().modifierIsKnown(modifier)
                            || item.getModifierValue(modifier) == 0) {
                        v = item.getModifierValue(modifier);
                        if (v != 0 && binding.value() != null) {
                            v = binding.value();
                        }
                    } else {
                        v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                        a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                    }
                }
                case OBJ_PROPERTY_FLAG -> {
                    ObjectFlag flag = prop.getPayload().getFlag(prop.getType());
                    if (player == null || player.getItemKnowledge().flagIsKnown(flag)) {
                        v = cache.getResolvedFlags().has(flag) ? 1 : 0;
                        if (v != 0 && binding.value() != null) {
                            v = binding.value();
                        }
                    } else {
                        v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                        a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                    }
                }
                case OBJ_PROPERTY_IGNORE -> {
                    ElementEnum element = prop.getPayload().getElement(prop.getType());
                    if (player == null || player.getItemKnowledge().objectElementIsKnown(player, item, element)) {
                        v = item.getElInfo().get(element).getFlags().has(ElementInfoEnum.EL_INFO_IGNORE) ? 1 : 0;
                        if (v != 0 && binding.value() != null) {
                            v = binding.value();
                        }
                    } else {
                        v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                        a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                    }
                }
                case OBJ_PROPERTY_RESIST, OBJ_PROPERTY_IMM, OBJ_PROPERTY_VULN -> {
                    ElementEnum element = prop.getPayload().getElement(prop.getType());
                    if (player == null || player.getItemKnowledge().objectElementIsKnown(player, item, element)) {
                        v = item.getElInfo().get(element).getResLevel();
                        if (v != 0 && binding.value() != null) {
                            v = binding.value();
                        }
                    } else {
                        v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                        a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                    }
                }
                default -> v = 0;
            }

            if (v != 0) {
                if (binding.aux()) {
                    int t = a;
                    a = v;
                    v = t;
                }
                if (first) {
                    combiner = bindingByEntry.get(entryName).combinerName.init(v, a);
                    first = false;
                } else {
                    combiner.accum(v, a);
                }
            }
        }

        // TODO curse chain — ui-entry.c:793-824. Each cursed sub-object gets its own
        // fresh, uncached flag lookup per call (C never persists cache2 across calls
        // for curses, ui-entry.c:799-806) — this loop must not reuse `cache`.

        int accum = 0;
        int accumAux = 0;

        if (!first) {
            cst = combiner.finish();
            accum = cst.getAccum();
            accumAux = cst.getAccumAux();
        }

        return new UIEntryValue(allAux ? 0 : accum,
                anyAux ? accumAux : 0, false);
    }

    public static UIEntryValue computeForPlayer(String entryName, Player player) {
        return null;
    }

    public static boolean isKnownRune(String entryName, Player player) {
        return false;
    }

    private record EntryBinding(List<ObjectProperty> objectProperties,
                                List<PlayerProperty> playerProperties,
                                CombinerName combinerName,
                                FlagView<ChannelEntryFlag> entryFlags) {
    }
}
