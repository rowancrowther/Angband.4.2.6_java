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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ChestTrapCode;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ChestTrap}, {@link ChestTrapCode} and the chest trap corner of
 * {@link ObjectRegistry} - the three pieces that between them replace C's {@code struct chest_trap}
 * and its global {@code chest_traps} list ({@code object.h}, {@code obj-chest.c}).
 *
 * <p>The pval arithmetic is what these mostly pin. C derives a trap's bit from its position in the
 * file while parsing ({@code t->pval = h->pval * 2} in {@code parse_chest_trap_name}); this port derives
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

    // ---- Behaviour C reads off the fields ---------------------------------

    /**
     * The seven records of {@code chest_trap.txt} as C reads them. Built by hand from the file so
     * the expected values come from C's data, not from the Java loader.
     */
    private static List<ChestTrap> shippedTraps() {
        return List.of(
                new ChestTrap("locked", ChestTrapCode.NO_TRAP, 1, new ArrayList<>(), false, false, "", ""),
                new ChestTrap("gas trap", ChestTrapCode.POISON, 1, new ArrayList<>(), false, false,
                        "A puff of green gas surrounds you!", ""),
                new ChestTrap("poison needle", ChestTrapCode.LOSE_STR, 2, new ArrayList<>(), false, false,
                        "A small needle has pricked you!", "a poison needle"),
                new ChestTrap("poison needle", ChestTrapCode.LOSE_CON, 3, new ArrayList<>(), false, false,
                        "A small needle has pricked you!", "a poison needle"),
                new ChestTrap("summoning runes", ChestTrapCode.SUMMON, 15, new ArrayList<>(), false, true,
                        "You are enveloped in a cloud of smoke!", ""),
                new ChestTrap("gas trap", ChestTrapCode.PARALYZE, 19, new ArrayList<>(), false, false,
                        "A puff of yellow gas surrounds you!", ""),
                new ChestTrap("explosion device", ChestTrapCode.EXPLODE, 25, new ArrayList<>(), true, false,
                        "There is a sudden explosion! Everything inside the chest is destroyed!",
                        "an exploding chest"));
    }

    /** C's {@code chest_trap_name}, written against the Java accessors. */
    private static String trapName(List<ChestTrap> traps, int pval) {
        if (pval < 0) {
            return pval == -1 ? "unlocked" : "disarmed";
        } else if (pval > 0) {
            ChestTrap found = null;
            for (ChestTrap trap : traps) {
                if ((pval & trap.getPVal()) != 0) {
                    if (found != null) {
                        return "multiple traps";
                    }
                    found = trap;
                }
            }
            if (found != null) {
                return found.getName();
            }
        }
        return "empty";
    }

    /** C's {@code pick_one_chest_trap} candidate count: traps after the "locked" entry that fit. */
    private static int candidates(List<ChestTrap> traps, int chestLevel) {
        int count = 0;
        for (ChestTrap trap : traps.subList(1, traps.size())) {
            if (trap.getLevel() <= chestLevel) count++;
        }
        return count;
    }

    private static String disarmKind(List<ChestTrap> traps, int pval) {
        boolean magic = false;
        boolean physical = false;
        for (ChestTrap trap : traps) {
            if ((trap.getPVal() & pval) == 0) continue;
            if (trap.isMagic()) {
                magic = true;
            } else {
                physical = true;
            }
        }
        return magic ? (physical ? "both" : "magic") : "physical";
    }

    @Test
    void chestTrapNameFollowsCsBranches() {
        List<ChestTrap> traps = shippedTraps();

        assertEquals("unlocked", trapName(traps, -1));
        assertEquals("disarmed", trapName(traps, -2));
        assertEquals("disarmed", trapName(traps, -66));
        assertEquals("empty", trapName(traps, 0));
        // pval 1 is the "locked" record's own bit, so a locked untrapped chest is named "locked".
        assertEquals("locked", trapName(traps, 1));
        assertEquals("gas trap", trapName(traps, 2));
        assertEquals("poison needle", trapName(traps, 4));
        assertEquals("poison needle", trapName(traps, 8));
        assertEquals("summoning runes", trapName(traps, 16));
        assertEquals("explosion device", trapName(traps, 64));
        // Two or more bits is "multiple traps", whichever they are - even with "locked" among them.
        assertEquals("multiple traps", trapName(traps, 4 | 8));
        assertEquals("multiple traps", trapName(traps, 1 | 2));
        assertEquals("multiple traps", trapName(traps, 127));
        // A bit no record owns names nothing.
        assertEquals("empty", trapName(traps, 128));
    }

    @Test
    void theLevelGateCountsTrapsAfterLockedWhoseLevelIsAtMostTheChests() {
        List<ChestTrap> traps = shippedTraps();

        // Levels in the file after "locked": 1, 2, 3, 15, 19, 25. Inclusive at each boundary.
        assertEquals(0, candidates(traps, 0));
        assertEquals(1, candidates(traps, 1));
        assertEquals(2, candidates(traps, 2));
        assertEquals(3, candidates(traps, 3));
        assertEquals(3, candidates(traps, 14));
        assertEquals(4, candidates(traps, 15));
        assertEquals(4, candidates(traps, 18));
        assertEquals(5, candidates(traps, 19));
        assertEquals(5, candidates(traps, 24));
        assertEquals(6, candidates(traps, 25));
        assertEquals(6, candidates(traps, 55));
    }

    @Test
    void springingTheTrapsWalksTheFileInOrderAndStopsAtADestroyer() {
        // chest_trap: every trap whose bit is set fires in file order; a destroy trap ends the walk.
        List<ChestTrap> traps = shippedTraps();
        List<String> fired = new ArrayList<>();
        int pval = ChestTrapCode.POISON.getPval() | ChestTrapCode.SUMMON.getPval()
                | ChestTrapCode.EXPLODE.getPval();
        for (ChestTrap trap : traps) {
            if ((trap.getPVal() & pval) != 0) {
                fired.add(trap.getCode().name());
                if (trap.isDestroy()) break;
            }
        }
        assertEquals(List.of("POISON", "SUMMON", "EXPLODE"), fired);

        // Put the destroyer first and nothing after it fires.
        List<ChestTrap> reordered = List.of(
                new ChestTrap("a", ChestTrapCode.POISON, 1, new ArrayList<>(), true, false, "", ""),
                new ChestTrap("b", ChestTrapCode.LOSE_STR, 1, new ArrayList<>(), false, false, "", ""));
        fired.clear();
        for (ChestTrap trap : reordered) {
            if ((trap.getPVal() & 6) != 0) {
                fired.add(trap.getName());
                if (trap.isDestroy()) break;
            }
        }
        assertEquals(List.of("a"), fired);
    }

    @Test
    void theDisarmSkillIsChosenFromTheMagicFlagsOfTheCarriedTraps() {
        // do_cmd_disarm_chest: magic skill if all carried traps are magic, the average if mixed,
        // physical otherwise. "locked" is physical, so a chest with it and the runes is mixed.
        List<ChestTrap> traps = shippedTraps();

        assertEquals("physical", disarmKind(traps, ChestTrapCode.POISON.getPval()));
        assertEquals("magic", disarmKind(traps, ChestTrapCode.SUMMON.getPval()));
        assertEquals("both", disarmKind(traps,
                ChestTrapCode.POISON.getPval() | ChestTrapCode.SUMMON.getPval()));
        assertEquals("both", disarmKind(traps,
                ChestTrapCode.NO_TRAP.getPval() | ChestTrapCode.SUMMON.getPval()));
        // Only the summoning runes are magic in the shipped file.
        assertEquals(1, traps.stream().filter(ChestTrap::isMagic).count());
    }

    @Test
    void onlyThePoisonNeedlesAndTheExplosionCarryADeathMessage() {
        // msg-death: appears three times in chest_trap.txt; every other record gets "" for C's NULL.
        List<ChestTrapCode> withDeath = new ArrayList<>();
        for (ChestTrap trap : shippedTraps()) {
            if (!trap.getMessageDeath().isEmpty()) withDeath.add(trap.getCode());
        }
        assertEquals(List.of(ChestTrapCode.LOSE_STR, ChestTrapCode.LOSE_CON, ChestTrapCode.EXPLODE),
                withDeath);
    }
}
