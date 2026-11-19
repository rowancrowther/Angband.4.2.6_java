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

package uk.co.jackoftradesltd.testsupport;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.TimedGrade;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A JUnit extension that guarantees {@code TMD_FOOD} has its six grades in the registry, so that
 * the {@code PY_FOOD_*} thresholds are the real ones rather than zero.
 *
 * <p><b>The problem it solves.</b> {@link PlayerRegistry#setPlayerTimedEffects} fills
 * {@code PY_FOOD_STARVE} to {@code PY_FOOD_MAX} only from the {@code FOOD} entry's grades, and
 * leaves them at zero when there is no such entry. {@link SeededPlayerRegistry} seeds an
 * <em>empty</em> timed-effect list, so a test that builds a character through
 * {@code PlayerBirth} gets food {@code getPyFoodFull() - 1}, which is -1, finds no grade for it,
 * decides the character is outside the fed range, and divides by {@code getPyFoodHungry()} in
 * {@code calcBonuses}: {@code ArithmeticException: / by zero}.
 *
 * <p><b>The grades.</b> They are {@code player_timed.txt}'s own, scaled by
 * {@code player:food-value:100} from {@code constants.txt} as {@code parse_player_timed_grade}
 * does, so the thresholds are the ones the game runs on: Starving 100, Faint 400, Weak 800,
 * Hungry 1500, Fed 9000 and Full 10000. The grade colours are left {@code null}, since nothing a
 * test calculates reads them, and the messages are left out for the same reason.
 *
 * <p><b>It defers to real data.</b> If the registry already holds a {@code FOOD} entry, because a
 * reader has loaded {@code player_timed.txt} earlier in the same JVM, nothing is changed. Otherwise
 * the entry is added, and afterwards it is taken out again and the six {@code PY_FOOD_*} ints are
 * put back to what they were, so a class using this extension cannot leave a stub behind for a
 * later class to find.
 *
 * <p><b>Order.</b> It does not matter whether this is registered before or after
 * {@link SeededPlayerRegistry}. If the timed-effect list is still {@code null} this extension
 * creates it, and clears it again afterwards; if the other extension got there first, this one adds
 * to that list and leaves the clearing to it.
 *
 * <p>Use it as {@code @ExtendWith({SeededPlayerRegistry.class, SeededFoodGrades.class})} on the
 * test class.
 *
 * <p>Class SeededFoodGrades coded on 261119, commented in full on 261119.
 *
 * @author Rowan Crowther
 */
public class SeededFoodGrades implements BeforeAllCallback, AfterAllCallback {

    /**
     * The names of {@code PlayerRegistry}'s six threshold fields, in the order the grades are
     * seeded, so that {@link #saved} lines up with them.
     */
    private static final String[] THRESHOLD_FIELDS = {
            "PY_FOOD_STARVE", "PY_FOOD_FAINT", "PY_FOOD_WEAK",
            "PY_FOOD_HUNGRY", "PY_FOOD_FULL", "PY_FOOD_MAX"};

    /**
     * Whether this extension created the timed-effect list, and so is responsible for clearing it.
     */
    private boolean createdList;
    /**
     * The list this extension added the food entry to, kept so that the entry is taken out of the
     * same list even if the registry's field has been replaced since. {@code null} when nothing was
     * added.
     */
    private List<PlayerTimedEffect> seededList;
    /**
     * The food entry this extension added, so that exactly that entry is removed again.
     */
    private PlayerTimedEffect seededFood;
    /**
     * The six {@code PY_FOOD_*} values before seeding, in {@link #THRESHOLD_FIELDS} order.
     */
    private final int[] saved = new int[THRESHOLD_FIELDS.length];

    /**
     * The six {@code TMD_FOOD} grades, as {@code parse_player_timed_grade} leaves them after
     * scaling by {@code food_value}. Grade numbers run from 1 as in the file.
     *
     * @return a fresh list of the six grades, in ascending order of {@code max}
     */
    public static List<TimedGrade> foodGrades() {
        List<TimedGrade> grades = new ArrayList<>();
        grades.add(new TimedGrade(1, null, 100, "Starving", null, null));
        grades.add(new TimedGrade(2, null, 400, "Faint", null, null));
        grades.add(new TimedGrade(3, null, 800, "Weak", null, null));
        grades.add(new TimedGrade(4, null, 1500, "Hungry", null, null));
        grades.add(new TimedGrade(5, null, 9000, "Fed", null, null));
        grades.add(new TimedGrade(6, null, 10000, "Full", null, null));
        return grades;
    }

    /**
     * A {@code FOOD} timed effect carrying only its grades and its lower bound of 1. Everything else
     * takes the value {@code PlayerTimedAssembler} gives a record that never sets it: the generic
     * message type, {@code OF_NONE} and {@code ELEM_NONE} for "no duplicated flag" and "no
     * resistance" (a {@code null} there would break {@code flagsTimed}), and no brand or slay.
     *
     * @return the effect
     */
    public static PlayerTimedEffect foodEffect() {
        return new PlayerTimedEffect(TimedEffect.TMD_FOOD, null, null, null, null, MessageType.MSG_GENERIC,
                new ArrayList<>(), foodGrades(), new ArrayList<>(), new ArrayList<>(),
                false, 1, ObjectFlag.OF_NONE, false, ElementEnum.ELEM_NONE, null, null);
    }

    /**
     * Reaches one of the registry's private static fields.
     *
     * @param name the field's name
     * @return the accessible field
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static Field registryField(String name) throws ReflectiveOperationException {
        Field field = PlayerRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void beforeAll(ExtensionContext context) throws Exception {
        // A nested test class re-enters here with the outer class's extension; the seed is still in place.
        if (seededFood != null) return;
        List<PlayerTimedEffect> list = (List<PlayerTimedEffect>) registryField("playerTimedEffects").get(null);
        if (list == null) {
            list = new ArrayList<>();
            registryField("playerTimedEffects").set(null, list);
            createdList = true;
        }
        if (list.stream().anyMatch(t -> t.getName() == TimedEffect.TMD_FOOD)) {
            // Real data is loaded; leave it, and leave the list for whoever made it.
            createdList = false;
            return;
        }
        for (int i = 0; i < THRESHOLD_FIELDS.length; i++) {
            saved[i] = registryField(THRESHOLD_FIELDS[i]).getInt(null);
        }
        seededFood = foodEffect();
        list.add(seededFood);
        seededList = list;
        // Re-installs the same list and fills the six thresholds from the entry just added.
        PlayerRegistry.setPlayerTimedEffects(list);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (seededFood != null) {
            seededList.remove(seededFood);
            for (int i = 0; i < THRESHOLD_FIELDS.length; i++) {
                registryField(THRESHOLD_FIELDS[i]).setInt(null, saved[i]);
            }
            seededFood = null;
            seededList = null;
        }
        if (createdList) {
            registryField("playerTimedEffects").set(null, null);
            createdList = false;
        }
    }
}
