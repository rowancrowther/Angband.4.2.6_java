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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.uichannel.UIEntryValue;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.CurseData;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Probe, not a permanent test: C's {@code el_info} on a curse object is a zero-initialised array of
 * {@code ELEM_MAX}, so {@code compute_ui_entry_values_for_object} reads a resistance of 0 for any
 * element the curse does not mention. The port's curse object is built by {@code CurseAssembler}
 * with only the elements the curse data names. Expected values derive from C.
 */
@ExtendWith(SeededPlayerRegistry.class)
class CurseSparseElInfoProbeTest {
    private static final String ENTRY = "PROBE_ENTRY";

    private static ObjectProperty elementProperty(ObjPropertyType type, ElementEnum element) {
        return new ObjectProperty(type, null, null,
                new ObjectPropertyTypeWrapper(type, element), 0, 0, Map.of(),
                "test", "", "", "", "", List.of(new ObjectProperty.UIBinding(ENTRY, null, false)));
    }

    private static UIEntryValue computeWithSparseCurse(ObjPropertyType type) {
        UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(elementProperty(type, ElementEnum.ELEM_FIRE)),
                List.of(), CombinerName.ADD, new Flag<>(ChannelEntryFlag.class));

        // The curse names no elements at all, as for a curse.txt record with only a flag.
        ItemObject curseObject = new ItemObject();
        curseObject.setFlag(ObjectFlag.OF_FREE_ACT);
        Curse curse = new Curse("Probe Curse", List.of(), curseObject, List.of(),
                new Flag<>(ObjectFlag.class), "", 1);
        LinkedHashMap<Curse, CurseData> curses = new LinkedHashMap<>();
        curses.put(curse, new CurseData(1, 0));

        // The base item carries a full el_info for fire so only the curse can go wrong.
        Map<ElementEnum, ElementInfo> baseEl = new HashMap<>();
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        baseEl.put(ElementEnum.ELEM_FIRE, fire);
        ItemObject item = new ItemObject(null, null, null, null, uk.co.jackoftradesltd.middle.cave.Loc.zero,
                uk.co.jackoftradesltd.middle.objects.enums.TValue.TV_SWORD, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), new HashMap<>(), baseEl, new HashSet<>(), new HashSet<>(), curses,
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice.class), 0, 0,
                uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum.ORIGIN_NONE, 0, null, "");

        return UIEntryValueRegistry.computeForObject(ENTRY, item, null, new ObjectValueCache());
    }

    @BeforeEach
    void reset() {
        UIEntryValueRegistry.clearEntryBindings();
    }

    @Test
    @DisplayName("a resist bound to the entry: a powered curse that names no elements reads 0, base still counts")
    void resistOnSparseCurse() {
        UIEntryValue result = computeWithSparseCurse(ObjPropertyType.OBJ_PROPERTY_RESIST);
        assertEquals(1, result.val());
    }

    @Test
    @DisplayName("an ignore bound to the entry: a powered curse that names no elements reads 0")
    void ignoreOnSparseCurse() {
        UIEntryValue result = computeWithSparseCurse(ObjPropertyType.OBJ_PROPERTY_IGNORE);
        assertEquals(0, result.val());
    }
}
