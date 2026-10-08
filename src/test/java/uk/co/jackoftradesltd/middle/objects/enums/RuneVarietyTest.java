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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.objects.Brand;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.Slay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Pins {@link RuneVariety#group()}, {@link RuneVariety#runeName()} and {@link RuneVariety#runeDesc()}
 * for each of the seven records against C's {@code rune_name} and {@code rune_desc}
 * ({@code obj-knowledge.c}) and {@code rune_group_text[]} ({@code ui-knowledge.c}).
 *
 * <p>Every expected string is the C format string filled with a name taken from the original data
 * files ({@code projection.txt}, {@code brand.txt}, {@code slay.txt}, {@code curse.txt},
 * {@code object_property.txt}), with the port's Oxford spelling where C's literals say "armor".
 *
 * <p>Test RuneVarietyTest written on 261008.
 */
class RuneVarietyTest {

    private static Projection projection(ProjectionEnum code, String name) {
        return new Projection(code, name, null, null, null, null, null, 1, 1, 1, 0, null, false,
                false, null);
    }

    private static ObjectProperty property(String name) {
        return new ObjectProperty(null, null, null, null, 0, 0, null, name, null, null, null, null,
                null);
    }

    // ---- group(): rune_group_text[] order ----

    private static Brand acidBrand() {
        return new Brand("ACID_3", "acid", "burns", null, null, 3, 3, 15);
    }

    // ---- combat: c_rune[] and the three literal sentences ----

    private static Slay animalSlay() {
        return new Slay("ANIMAL_2", "animals", null, "smites", "smites", null, 2, 2, 10);
    }

    private static Curse vulnerability() {
        return new Curse("vulnerability", null, null, null, null,
                "attracts opponents and weakens the defences", 1);
    }

    @Test
    void groupsFollowRuneGroupTextOrder() {
        assertEquals("Combat", new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A).group().getName());
        assertEquals("Modifiers",
                new RuneVariety.ModKey(ObjectModifier.OM_STR, property("strength")).group().getName());
        assertEquals("Resists", new RuneVariety.ResistKey(ElementEnum.ELEM_ACID,
                projection(ProjectionEnum.PROJ_ACID, "acid")).group().getName());
        assertEquals("Brands", new RuneVariety.BrandKey(acidBrand()).group().getName());
        assertEquals("Slays", new RuneVariety.SlayKey(animalSlay()).group().getName());
        assertEquals("Curses", new RuneVariety.CurseKey(vulnerability()).group().getName());
        assertEquals("Other", new RuneVariety.FlagKey(ObjectFlag.OF_PROT_FEAR,
                property("protection from fear")).group().getName());
    }

    // ---- modifiers and flags: unwrapped name ----

    @Test
    void combatNamesAreTheCRuneTableWithOxfordSpelling() {
        assertEquals("enchantment to armour", new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A).runeName());
        assertEquals("enchantment to hit", new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H).runeName());
        assertEquals("enchantment to damage", new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D).runeName());
    }

    @Test
    void combatDescriptionsHaveNoClosingFullStop() {
        assertEquals("Object magically increases the player's armour class",
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A).runeDesc());
        assertEquals("Object magically increases the player's chance to hit",
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H).runeDesc());
        assertEquals("Object magically increases the player's damage",
                new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D).runeDesc());
    }

    // ---- resist: name prefixed, description bare ----

    @Test
    void combatSentinelHasNoDescription() {
        // C's rune_desc breaks out of the combat case and returns NULL when the index matches none
        assertNull(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_MAX).runeDesc());
    }

    @Test
    void modifierNameIsUnwrappedAndDescriptionHasAFullStop() {
        RuneVariety.ModKey rune = new RuneVariety.ModKey(ObjectModifier.OM_STR, property("strength"));
        assertEquals("strength", rune.runeName());
        assertEquals("Object gives the player a magical bonus to strength.", rune.runeDesc());
    }

    // ---- brand and slay ----

    @Test
    void flagNameIsUnwrappedAndDescriptionHasAFullStop() {
        RuneVariety.FlagKey rune = new RuneVariety.FlagKey(ObjectFlag.OF_PROT_FEAR,
                property("protection from fear"));
        assertEquals("protection from fear", rune.runeName());
        assertEquals("Object gives the player the property of protection from fear.", rune.runeDesc());
    }

    @Test
    void resistNameIsPrefixedButDescriptionTakesTheBareProjectionName() {
        RuneVariety.ResistKey acid = new RuneVariety.ResistKey(ElementEnum.ELEM_ACID,
                projection(ProjectionEnum.PROJ_ACID, "acid"));
        assertEquals("resist acid", acid.runeName());
        assertEquals("Object affects the player's resistance to acid.", acid.runeDesc());
    }

    // ---- curse: description comes from desc, not name ----

    @Test
    void resistReadsTheProjectionNameNotTheElementName() {
        // projection.txt names ELEC "lightning" and DISEN "disenchantment"
        RuneVariety.ResistKey elec = new RuneVariety.ResistKey(ElementEnum.ELEM_ELEC,
                projection(ProjectionEnum.PROJ_ELEC, "lightning"));
        assertEquals("resist lightning", elec.runeName());
        assertEquals("Object affects the player's resistance to lightning.", elec.runeDesc());

        RuneVariety.ResistKey disen = new RuneVariety.ResistKey(ElementEnum.ELEM_DISEN,
                projection(ProjectionEnum.PROJ_DISEN, "disenchantment"));
        assertEquals("resist disenchantment", disen.runeName());
        assertEquals("Object affects the player's resistance to disenchantment.", disen.runeDesc());
    }

    @Test
    void brandNameIsSuffixedAndDescriptionTakesTheBareName() {
        RuneVariety.BrandKey rune = new RuneVariety.BrandKey(acidBrand());
        assertEquals("acid brand", rune.runeName());
        assertEquals("Object brands the player's attacks with acid.", rune.runeDesc());
    }

    @Test
    void slayNameIsPrefixedAndDescriptionTakesTheBareName() {
        RuneVariety.SlayKey rune = new RuneVariety.SlayKey(animalSlay());
        assertEquals("slay animals", rune.runeName());
        assertEquals("Object makes the player's attacks against animals more powerful.", rune.runeDesc());
    }

    @Test
    void curseNameUsesTheNameAndDescriptionUsesTheDescField() {
        RuneVariety.CurseKey rune = new RuneVariety.CurseKey(vulnerability());
        assertEquals("vulnerability curse", rune.runeName());
        assertEquals("Object attracts opponents and weakens the defences.", rune.runeDesc());
    }
}
