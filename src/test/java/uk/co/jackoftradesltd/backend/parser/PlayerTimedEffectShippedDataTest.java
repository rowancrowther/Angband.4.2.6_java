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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sweeps every {@link PlayerTimedEffect} the shipped {@code player_timed.txt} produces, checking
 * the fields the data file supplies against the lines of that file, effect by effect.
 *
 * <p>{@code PlayerTimedReaderTest} spot-checks one or two effects per directive. This class reads
 * the whole table instead, because a record that drops or crosses a field (a {@code flag-synonym}
 * landing on the wrong effect, a {@code msgt} lost to the default) only shows up when every row is
 * looked at. The expected values were transcribed from the {@code msgt:}, {@code flags:},
 * {@code flag-synonym:}, {@code lower-bound:}, {@code resist:}, {@code brand:}, {@code slay:},
 * {@code on-begin-effect:} and {@code on-end-effect:} lines of C's {@code lib/gamedata/player_timed.txt},
 * not read back from the Java.
 *
 * <p>Every effect the file does <em>not</em> name for a directive must hold the default C's static
 * {@code timed_effects[]} initializer leaves: message type 0 ({@code MSG_GENERIC}), not
 * non-stacking, lower bound 0, {@code OF_NONE} with no exact synonym, no resist, brand or slay, and
 * no begin or end effect (a {@code NULL} chain in C, an empty list here).
 *
 * <p>The brand and slay registries and the constants table are seeded and restored around the
 * class, as in {@code PlayerTimedReaderTest}, so no global state leaks to other suites.
 *
 * <p>Class PlayerTimedEffectShippedDataTest coded on 261029, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
class PlayerTimedEffectShippedDataTest {

    private static final String REAL_FILE = "lib/gamedata/player_timed.txt";
    private static final String BRAND_FILE = "lib/gamedata/brand.txt";
    private static final String SLAY_FILE = "lib/gamedata/slay.txt";

    /**
     * {@code msgt:} lines of the shipped file, by effect name; an effect absent here has no
     * {@code msgt:} line and so keeps C's default of {@code MSG_GENERIC}.
     */
    private static final Map<String, String> MSGT = Map.ofEntries(
            Map.entry("FAST", "SPEED"), Map.entry("SLOW", "SLOW"), Map.entry("BLIND", "BLIND"),
            Map.entry("PARALYZED", "PARALYZED"), Map.entry("CONFUSED", "CONFUSED"),
            Map.entry("AFRAID", "AFRAID"), Map.entry("IMAGE", "DRUGGED"),
            Map.entry("POISONED", "POISONED"), Map.entry("CUT", "CUT"), Map.entry("STUN", "STUN"),
            Map.entry("FOOD", "HUNGRY"), Map.entry("PROTEVIL", "PROT_EVIL"),
            Map.entry("INVULN", "INVULN"), Map.entry("HERO", "HERO"), Map.entry("SHERO", "BERSERK"),
            Map.entry("SHIELD", "SHIELD"), Map.entry("BLESSED", "BLESSED"),
            Map.entry("SINVIS", "SEE_INVIS"), Map.entry("SINFRA", "INFRARED"),
            Map.entry("OPP_ACID", "RES_ACID"), Map.entry("OPP_ELEC", "RES_ELEC"),
            Map.entry("OPP_FIRE", "RES_FIRE"), Map.entry("OPP_COLD", "RES_COLD"),
            Map.entry("OPP_POIS", "RES_POIS"), Map.entry("AMNESIA", "GENERIC"),
            Map.entry("TELEPATHY", "GENERIC"), Map.entry("STONESKIN", "GENERIC"),
            Map.entry("TERROR", "AFRAID"), Map.entry("SPRINT", "SPEED"), Map.entry("BOLD", "BOLD"),
            Map.entry("SCRAMBLE", "SCRAMBLE"));

    /**
     * {@code flag-synonym:} lines: the duplicated object flag, then the exact-synonym argument.
     */
    private static final Map<String, Object[]> SYNONYM = Map.ofEntries(
            Map.entry("AFRAID", new Object[]{"AFRAID", true}),
            Map.entry("HERO", new Object[]{"PROT_FEAR", false}),
            Map.entry("SHERO", new Object[]{"PROT_FEAR", false}),
            Map.entry("SINVIS", new Object[]{"SEE_INVIS", true}),
            Map.entry("OPP_CONF", new Object[]{"PROT_CONF", true}),
            Map.entry("TELEPATHY", new Object[]{"TELEPATHY", true}),
            Map.entry("TERROR", new Object[]{"AFRAID", false}),
            Map.entry("BOLD", new Object[]{"PROT_FEAR", true}),
            Map.entry("TRAPSAFE", new Object[]{"TRAP_IMMUNE", true}),
            Map.entry("FREE_ACT", new Object[]{"FREE_ACT", true}));

    /**
     * {@code resist:} lines.
     */
    private static final Map<String, String> RESIST = Map.of(
            "OPP_ACID", "ACID", "OPP_ELEC", "ELEC", "OPP_FIRE", "FIRE", "OPP_COLD", "COLD",
            "OPP_POIS", "POIS");

    /**
     * {@code brand:} lines.
     */
    private static final Map<String, String> BRAND = Map.of(
            "ATT_ACID", "ACID_3", "ATT_ELEC", "ELEC_3", "ATT_FIRE", "FIRE_3", "ATT_COLD", "COLD_3",
            "ATT_POIS", "POIS_3");

    /**
     * {@code slay:} lines.
     */
    private static final Map<String, String> SLAY = Map.of("ATT_EVIL", "EVIL_2", "ATT_DEMON", "DEMON_5");

    private static Object savedBrands;
    private static Object savedSlays;
    private static Object savedConstants;
    private static boolean seededConstants;

    private static List<PlayerTimedEffect> effects;

    @BeforeAll
    static void load() throws Exception {
        savedBrands = setStatic("brands", new BrandReader().parseWithResults(BRAND_FILE).items());
        savedSlays = setStatic("slays", new SlayReader().parseWithResults(SLAY_FILE).items());
        if (constantsField().get(null) == null) {
            constantsField().set(null, new GameConstantsData(
                    null, null, null, null, null, null, null, null,
                    new PlayerData(20, 20, 600, 100),
                    null, null, null, null, null, null, null, null));
            seededConstants = true;
        }
        effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("brands", savedBrands);
        setStatic("slays", savedSlays);
        if (seededConstants) {
            constantsField().set(null, savedConstants);
            seededConstants = false;
        }
    }

    private static Field constantsField() throws ReflectiveOperationException {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    /**
     * @param effect a loaded definition
     * @return the file's name for it, e.g. {@code FAST} for {@code TMD_FAST}
     */
    private static String fileName(PlayerTimedEffect effect) {
        return effect.getName().name().substring("TMD_".length());
    }

    @Test
    void everyEffectHoldsTheMessageTypeItsMsgtLineNamesOrGeneric() {
        for (PlayerTimedEffect effect : effects) {
            String expected = "MSG_" + MSGT.getOrDefault(fileName(effect), "GENERIC");

            assertEquals(MessageType.valueOf(expected), effect.getMsgT(), fileName(effect));
        }
    }

    @Test
    void onlyParalysisIsNonStacking() {
        for (PlayerTimedEffect effect : effects) {
            assertEquals(fileName(effect).equals("PARALYZED"), effect.isNonStacking(), fileName(effect));
        }
    }

    @Test
    void onlyFoodRaisesTheLowerBound() {
        for (PlayerTimedEffect effect : effects) {
            assertEquals(fileName(effect).equals("FOOD") ? 1 : 0, effect.getLowerBound(), fileName(effect));
        }
    }

    @Test
    void everyFlagSynonymLandsOnItsOwnEffectAndAllTheRestHoldNone() {
        for (PlayerTimedEffect effect : effects) {
            Object[] expected = SYNONYM.get(fileName(effect));
            if (expected == null) {
                assertEquals(ObjectFlag.OF_NONE, effect.getoFlagDup(), fileName(effect));
                assertFalse(effect.isoFlagExactlySyn(), fileName(effect));
            } else {
                assertEquals(ObjectFlag.valueOf("OF_" + expected[0]), effect.getoFlagDup(), fileName(effect));
                assertEquals(expected[1], effect.isoFlagExactlySyn(), fileName(effect));
            }
        }
    }

    @Test
    void everyResistLandsOnItsOwnEffectAndAllTheRestHoldNone() {
        for (PlayerTimedEffect effect : effects) {
            String expected = RESIST.get(fileName(effect));

            assertEquals(expected == null ? ElementEnum.ELEM_NONE : ElementEnum.valueOf("ELEM_" + expected),
                    effect.getTempResist(), fileName(effect));
        }
    }

    @Test
    void everyBrandAndSlayLandsOnItsOwnEffectAndAllTheRestHoldNull() {
        for (PlayerTimedEffect effect : effects) {
            String brand = BRAND.get(fileName(effect));
            String slay = SLAY.get(fileName(effect));

            if (brand == null) {
                assertNull(effect.getTempBrand(), fileName(effect));
            } else {
                assertNotNull(effect.getTempBrand(), fileName(effect));
                assertEquals(brand, effect.getTempBrand().getCode(), fileName(effect));
            }
            if (slay == null) {
                assertNull(effect.getTempSlay(), fileName(effect));
            } else {
                assertNotNull(effect.getTempSlay(), fileName(effect));
                assertEquals(slay, effect.getTempSlay().getCode(), fileName(effect));
            }
        }
    }

    /**
     * Only SCRAMBLE has a begin effect, and only SCRAMBLE and SPRINT an end effect, each of one
     * link; C leaves the other chains {@code NULL}.
     */
    @Test
    void onlyThreeEffectChainsExistAndEveryOtherIsEmpty() {
        Set<String> begin = Set.of("SCRAMBLE");
        Set<String> end = Set.of("SCRAMBLE", "SPRINT");

        for (PlayerTimedEffect effect : effects) {
            assertEquals(begin.contains(fileName(effect)) ? 1 : 0, effect.getOnBeginEffect().size(),
                    fileName(effect) + " begin");
            assertEquals(end.contains(fileName(effect)) ? 1 : 0, effect.getOnEndEffect().size(),
                    fileName(effect) + " end");
        }
    }

    @Test
    void everyEffectTheTableNamesIsLoadedOnceAndCarriesItsFlags() {
        assertEquals(53, effects.size());
        for (PlayerTimedEffect effect : effects) {
            assertFalse(effect.getFlagRedraw().isEmpty(), fileName(effect) + " redraw");
            assertFalse(effect.getFlagUpdate().isEmpty(), fileName(effect) + " update");
        }
        assertEquals(53, effects.stream().map(PlayerTimedEffect::getName).distinct().count());
    }
}
