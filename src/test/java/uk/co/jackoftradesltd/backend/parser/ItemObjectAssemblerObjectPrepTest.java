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
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.ObjMakeData;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.ObjectUtils;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs {@link ObjectUtils#objectPrep} over every kind {@link ItemObjectReader} builds from the
 * shipped {@code object.txt}, to show that no kind reaches a null dice field.
 *
 * <p>C holds a zero {@code struct random} for every dice field a record does not set, and
 * {@code object_prep} rolls them without a guard. The assembler starts each of the five dice
 * ({@code pval}, to-hit, to-dam, to-armour and the base attack) at a zero {@code Random} for the
 * same reason; before that, a ration ({@code & Ration~ of Food}) and {@code Cure Light Wounds}
 * threw a {@link NullPointerException} here. {@link DamageAspect#MAXIMIZE} is used because it never
 * reads {@link GameConstants} dice state.
 *
 * @author Rowan Crowther
 */
class ItemObjectAssemblerObjectPrepTest {

    private static final String REAL_FILE = "lib/gamedata/object.txt";

    private static Object savedPains, savedBases, savedSummons, savedSlays,
            savedBrands, savedObjectBases, savedCurses, savedShapes, savedConstants;

    @BeforeAll
    static void seed() throws Exception {
        savedPains = setStatic("monsterPains", new PainReader().parse("lib/gamedata/pain.txt"));
        savedBases = setStatic("monsterBases", new MonsterBaseReader().parse("lib/gamedata/monster_base.txt"));
        savedSummons = setStatic("summons", new SummonReader().parse("lib/gamedata/summon.txt"));
        savedSlays = setStatic("slays", new SlayReader().parse("lib/gamedata/slay.txt"));
        savedBrands = setStatic("brands", new BrandReader().parse("lib/gamedata/brand.txt"));
        savedObjectBases = setStatic("objectBases", new ObjectBaseReader().parse("lib/gamedata/object_base.txt"));
        savedCurses = setStatic("curses", new CurseReader().parse("lib/gamedata/curse.txt"));
        savedShapes = setStatic("playerShapes", new ShapeReader().parse("lib/gamedata/shape.txt"));

        // objectPrep's light-fuel default reads GameConstants whatever the aspect.
        ObjMakeData objMake = new ObjMakeData(0, 0, 0, 5000, 0, 7500);
        GameConstantsData data = new GameConstantsData(
                null, null, null, null, null, null, null, objMake, null,
                null, null, null, null, null, null, null, null);
        Field f = GameConstants.class.getDeclaredField("data");
        f.setAccessible(true);
        savedConstants = f.get(null);
        f.set(null, data);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
        setStatic("summons", savedSummons);
        setStatic("slays", savedSlays);
        setStatic("brands", savedBrands);
        setStatic("objectBases", savedObjectBases);
        setStatic("curses", savedCurses);
        setStatic("playerShapes", savedShapes);
        Field f = GameConstants.class.getDeclaredField("data");
        f.setAccessible(true);
        f.set(null, savedConstants);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    private static ObjectKind kindNamed(List<ObjectKind> kinds, String name, TValue tval) {
        return kinds.stream()
                .filter(k -> k.getName().equals(name) && k.gettValue() == tval)
                .findFirst().orElseThrow(() -> new AssertionError("no kind " + name + " / " + tval));
    }

    /**
     * The two kinds the issue names.
     */
    @Test
    void rationAndCureLightWoundsPrepWithoutThrowing() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        for (ObjectKind kind : List.of(kindNamed(kinds, "& Ration~ of Food", TValue.TV_FOOD),
                kindNamed(kinds, "Cure Light Wounds", TValue.TV_POTION))) {
            assertDoesNotThrow(() -> ObjectUtils.objectPrep(new ItemObject(), kind, 0, DamageAspect.MAXIMIZE),
                    kind.getName());
        }
    }

    /**
     * Every kind in the file, not just the two named: any other kind that still holds a null dice
     * would show here.
     */
    @Test
    void everyShippedKindPreps() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        assertFalse(kinds.isEmpty());

        List<String> failures = new ArrayList<>();
        for (ObjectKind kind : kinds) {
            try {
                ObjectUtils.objectPrep(new ItemObject(), kind, 0, DamageAspect.MAXIMIZE);
            } catch (RuntimeException e) {
                failures.add(kind.gettValue() + " " + kind.getName() + ": " + e);
            }
        }
        assertTrue(failures.isEmpty(), () -> failures.size() + " of " + kinds.size()
                + " kinds threw:\n" + String.join("\n", failures));
    }

    /**
     * The dice {@code objectPrep} rolls unconditionally are non-null on every kind: {@code pval},
     * to-hit, to-dam, to-armour and {@code time}.
     */
    @Test
    void noKindHoldsANullDice() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        List<String> nulls = new ArrayList<>();
        for (ObjectKind k : kinds) {
            if (k.getPVal() == null) nulls.add(k.getName() + " pval");
            if (k.getToH() == null) nulls.add(k.getName() + " toH");
            if (k.getToD() == null) nulls.add(k.getName() + " toD");
            if (k.getToA() == null) nulls.add(k.getName() + " toA");
            if (k.getTime() == null) nulls.add(k.getName() + " time");
        }
        assertEquals(List.of(), nulls);
    }

    /**
     * {@code objectPrep} reads {@code getCharge()} only for a kind whose tval can hold charges, so
     * every such kind must carry one.
     */
    @Test
    void everyKindThatCanHaveChargesHasChargeDice() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        List<String> nulls = new ArrayList<>();
        for (ObjectKind k : kinds) {
            if (k.gettValue().canHaveCharges() && k.getCharge() == null)
                nulls.add(k.gettValue() + " " + k.getName());
        }
        assertEquals(List.of(), nulls);
    }

    /**
     * {@link ObjectKind#copy} copies {@code charge}, {@code stackSize} and every effect, so no kind
     * built from {@code object.txt} may fail there. {@code EffectAssembler} leaves an effect's
     * {@code dice} {@code null} by design, and {@code Effect.copy} has to carry that through.
     */
    @Test
    void everyShippedKindCanBeCopied() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        List<String> failures = new ArrayList<>();
        int withEffects = 0;
        for (ObjectKind kind : kinds) {
            if (!kind.getEffect().isEmpty()) withEffects++;
            try {
                ObjectKind copy = kind.copy();
                assertEquals(kind.getEffect().size(), copy.getEffect().size(), kind.getName());
            } catch (RuntimeException e) {
                failures.add(kind.gettValue() + " " + kind.getName() + ": " + e);
            }
        }
        assertTrue(withEffects > 0);
        assertTrue(failures.isEmpty(), () -> failures.size() + " of " + kinds.size()
                + " kinds threw in copy():\n" + String.join("\n", failures.subList(0, Math.min(5, failures.size()))));
    }

    /**
     * {@code dd} and {@code ds} come from the base-damage dice; a kind with no {@code attack:} line
     * has 0 and 0, as C's zeroed struct does.
     */
    @Test
    void damageDiceAndSidesFollowTheBaseDamageDice() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        Field baseDamage = ObjectKind.class.getDeclaredField("baseDamage");
        baseDamage.setAccessible(true);
        int withAttack = 0;
        for (ObjectKind k : kinds) {
            Random dice = (Random) baseDamage.get(k);
            assertEquals(dice.getDice(), k.getDamageDice(), k.getName());
            assertEquals(dice.getSides(), k.getDamageSides(), k.getName());
            if (k.getDamageDice() > 0) withAttack++;
        }
        assertTrue(withAttack > 0);
        ObjectKind pile = kindNamed(kinds, "<pile>", TValue.TV_NONE);
        assertEquals(0, pile.getDamageDice());
        assertEquals(0, pile.getDamageSides());
    }
}
