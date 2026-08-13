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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.objects.enums.ChestTrapCode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ChestTrap}, {@link ChestTrapCode} and the chest trap corner of
 * {@link ObjectRegistry} - the three pieces that between them replace C's {@code struct chest_trap}
 * and its global {@code chest_traps} list ({@code object.h:67-78}, {@code obj-chest.c:53}).
 *
 * <p>The pval arithmetic is what these mostly pin. C derives a trap's bit from its position in the
 * file while parsing ({@code t->pval = h->pval * 2}, {@code obj-chest.c:64-72}); this port derives
 * it from the enum's declaration order instead, so the enum and the data file have to agree - a
 * duty {@code ChestTrapAssembler} discharges and {@code ChestTrapReaderTest} covers. What is left to
 * check here is that the bits themselves are what C would have produced, and that they still fit the
 * 16-bit pval the savefile stores.
 *
 * @author Rowan Crowther
 */
class ChestTrapTest {

    private static ChestTrap trap(ChestTrapCode code) {
        return new ChestTrap("a trap", code, 5, new ArrayList<>(), false, false, "", "");
    }

    // ---- ChestTrapCode ----------------------------------------------------

    @SuppressWarnings("unchecked")
    private static List<ChestTrap> chestTraps() throws Exception {
        Field field = ObjectRegistry.class.getDeclaredField("chestTraps");
        field.setAccessible(true);
        return (List<ChestTrap>) field.get(null);
    }

    @Test
    void pvalsAreSuccessivePowersOfTwoFromOne() {
        // The same sequence C builds by doubling as it walks the file.
        int expected = 1;
        for (ChestTrapCode code : ChestTrapCode.values()) {
            assertEquals(expected, code.getPval(), code::name);
            expected *= 2;
        }
    }

    @Test
    void everyCodeOwnsADistinctBit() {
        Set<Integer> bits = new HashSet<>();
        for (ChestTrapCode code : ChestTrapCode.values()) {
            assertTrue(bits.add(code.getPval()), () -> "duplicate bit on " + code);
            assertEquals(1, Integer.bitCount(code.getPval()), code::name);
        }
    }

    @Test
    void noTrapIsFirstAndOwnsBitOne() {
        // C reserves pval 1 for the locked-but-untrapped chest and starts trap selection at
        // chest_traps->next, so this constant must lead the enum.
        assertEquals(ChestTrapCode.NO_TRAP, ChestTrapCode.values()[0]);
        assertEquals(1, ChestTrapCode.NO_TRAP.getPval());
    }

    @Test
    void theCodesFitTheSixteenBitPvalWithRoomForItsSignAndLowBits() {
        // chest_trap.txt: "There should be no more than 14 traps total" - the int16 pval, less its
        // lowest and highest bits.
        assertTrue(ChestTrapCode.values().length <= ChestTrapCode.getMaxTraps(),
                () -> ChestTrapCode.values().length + " codes exceeds " + ChestTrapCode.getMaxTraps());

        int all = Arrays.stream(ChestTrapCode.values()).mapToInt(ChestTrapCode::getPval)
                .reduce(0, (a, b) -> a | b);
        assertTrue(all > 0 && all <= Short.MAX_VALUE, () -> "pval mask " + all + " will not fit an int16");
    }

    // ---- ChestTrap --------------------------------------------------------

    @Test
    void aChestPvalIsTheOrOfTheTrapsItCarries() {
        // How pick_chest_traps builds a chest's pval, and how chest_trap_name reads it back.
        int chestPval = trap(ChestTrapCode.POISON).getPVal() | trap(ChestTrapCode.EXPLODE).getPVal();

        assertEquals(66, chestPval);
        assertNotEquals(0, chestPval & ChestTrapCode.POISON.getPval());
        assertNotEquals(0, chestPval & ChestTrapCode.EXPLODE.getPval());
        assertEquals(0, chestPval & ChestTrapCode.SUMMON.getPval());

        // More than one bit set is C's "multiple traps" case.
        assertTrue(Integer.bitCount(chestPval) > 1);
    }

    @Test
    void gettersReturnWhatTheConstructorWasGiven() {
        List<Effect> effects = new ArrayList<>();
        ChestTrap chestTrap = new ChestTrap("explosion device", ChestTrapCode.EXPLODE, 25, effects,
                true, false, "There is a sudden explosion!", "an exploding chest");

        assertEquals("explosion device", chestTrap.getName());
        assertEquals(ChestTrapCode.EXPLODE, chestTrap.getCode());
        assertEquals(25, chestTrap.getLevel());
        assertSame(effects, chestTrap.getEffect());
        assertTrue(chestTrap.isDestroy());
        assertFalse(chestTrap.isMagic());
        assertEquals("There is a sudden explosion!", chestTrap.getMessage());
        assertEquals("an exploding chest", chestTrap.getMessageDeath());
    }

    // ---- ObjectRegistry ---------------------------------------------------

    @Test
    void getPValDelegatesToTheCode() {
        // The bit is not a field here - it is asked of the code every time, so the two can never
        // drift apart the way C's stored pval could.
        for (ChestTrapCode code : ChestTrapCode.values()) {
            assertEquals(code.getPval(), trap(code).getPVal(), code::name);
        }
    }

    @Test
    void setChestTrapsCopiesAndReplaces() throws Exception {
        // Snapshot the contents, not the list: the registry holds its list by identity, so a
        // reference here would track the very mutations this test is about to make.
        List<ChestTrap> saved = new ArrayList<>(chestTraps());
        try {
            List<ChestTrap> first = new ArrayList<>(List.of(trap(ChestTrapCode.NO_TRAP)));
            ObjectRegistry.setChestTraps(first);
            assertEquals(1, chestTraps().size());

            // The registry copies rather than rebinding, so the caller's list is not its own.
            first.add(trap(ChestTrapCode.POISON));
            assertEquals(1, chestTraps().size(), "registry must not alias the list it was given");

            // A second call replaces rather than appends, so a re-initialisation cannot double up.
            ObjectRegistry.setChestTraps(List.of(trap(ChestTrapCode.NO_TRAP),
                    trap(ChestTrapCode.POISON), trap(ChestTrapCode.LOSE_STR)));
            assertEquals(3, chestTraps().size());
        } finally {
            ObjectRegistry.setChestTraps(saved);
        }
    }
}
