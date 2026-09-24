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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectUtils#objectElementIsKnown(Player, Curse, ElementEnum)}, the curse-shaped
 * counterpart to C's {@code object_element_is_known} ({@code obj-knowledge.c}). C never calls that
 * function with a curse — see {@link Curse#isFullyKnown()} — so this pins the port's own extension
 * against the same four checks the function makes, in the same order: the sentinel bound, the
 * curse being fully known, the player's own rune knowledge, and the curse's own known-shadow. The
 * "neither knows" case pins the fall-through.
 *
 * <p>This also stands as the regression test for the bug fixed on 260924: the method's earlier
 * form took no {@link Player} and answered from a {@code containsKey} test on the curse's own
 * known-shadow, which meant an element recorded there with a resistance level of exactly zero read
 * as "known" instead of falling through — {@link NeitherKnowsTheElement#aKnownButZeroShadowEntryReportsUnknown()}
 * pins the corrected behaviour directly.
 *
 * <p>Curses are built with the long constructor, following
 * {@link ObjectUtilsCurseObjectFlagIsKnownTest}'s pattern. A "not fully known" fixture moves the
 * curse's real to-hit off the (default zero) known to-hit, which is enough to fail
 * {@link Curse#isFullyKnown()}'s first check without needing the element halves to disagree with
 * anything.
 *
 * <p>Class ObjectUtilsObjectElementIsKnownTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectUtilsObjectElementIsKnownTest {

    /**
     * A curse with every collection field live and empty, and the given real to-hit penalty; every
     * other field — modifiers, element info, object flags, conflicts — is left at its empty
     * default, so only the to-hit figure is under the test's control.
     *
     * @param combatToHit the curse's real to-hit penalty
     * @return the curse, with its known-shadow figures all still at their zero defaults
     */
    private static Curse curse(int combatToHit) {
        return new Curse("test curse", List.of(), 0, null,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                combatToHit, 0, 0, List.of(), new Flag<>(ObjectFlag.class),
                "test curse", "the test curse fires");
    }

    /**
     * A fully known curse: real to-hit matches the (zero) known to-hit, and every other field is
     * at its empty default, which satisfies every clause of {@link Curse#isFullyKnown()}.
     */
    private static Curse fullyKnownCurse() {
        return curse(0);
    }

    /**
     * A curse that is not fully known: the real to-hit is moved off the known to-hit, which stays
     * at its default zero, failing {@link Curse#isFullyKnown()}'s first check regardless of what
     * the element half says.
     */
    private static Curse notFullyKnownCurse() {
        return curse(3);
    }

    /**
     * An {@link ElementInfo} carrying the given resistance level and no flags, for writing onto a
     * curse's known-shadow through {@link Curse#putKnownElementInfo}.
     */
    private static ElementInfo elementInfo(int resLevel) {
        ElementInfo info = new ElementInfo();
        info.setResLevel(resLevel);
        return info;
    }

    private static Player playerWithItemKnowledge() {
        Player player = new Player();
        player.setItemKnowledge(new KnownObject());
        return player;
    }

    /**
     * The bound check: C's {@code if (element < 0 || element >= ELEM_MAX) return false;}, ported as
     * the two sentinel constants. Runs before every other check, so it answers false even for a
     * fully known curse.
     */
    @Nested
    @DisplayName("a sentinel element")
    class SentinelElement {

        @Test
        @DisplayName("ELEM_NONE reports unknown even on a fully known curse")
        void elemNoneReportsUnknown() {
            Player player = playerWithItemKnowledge();
            Curse curse = fullyKnownCurse();

            assertFalse(ObjectUtils.objectElementIsKnown(player, curse, ElementEnum.ELEM_NONE));
        }

        @Test
        @DisplayName("ELEM_MAX reports unknown even on a fully known curse")
        void elemMaxReportsUnknown() {
            Player player = playerWithItemKnowledge();
            Curse curse = fullyKnownCurse();

            assertFalse(ObjectUtils.objectElementIsKnown(player, curse, ElementEnum.ELEM_MAX));
        }
    }

    /**
     * The first route past the bound check: a curse that is fully known reports every element as
     * known, whatever the player's own knowledge and the curse's known-shadow say — C's
     * {@code if (object_fully_known(obj)) return true;} runs before either other check.
     */
    @Nested
    @DisplayName("a fully known curse")
    class FullyKnownCurse {

        @Test
        @DisplayName("reports the element as known even though neither the player nor the shadow has it")
        void reportsKnownRegardlessOfTheOtherTwoChecks() {
            Player player = playerWithItemKnowledge();
            Curse curse = fullyKnownCurse();

            assertTrue(ObjectUtils.objectElementIsKnown(player, curse, ElementEnum.ELEM_FIRE));
        }
    }

    /**
     * The second route: the player's own rune knowledge, {@code p->obj_k} / {@link KnownObject},
     * already covers this element — C's {@code if (p->obj_k->el_info[element].res_level) return
     * true;}, read through {@link KnownObject#getElementResistInfo()}. Fires even though the curse
     * itself is not fully known.
     */
    @Nested
    @DisplayName("a not-fully-known curse whose player already knows the element")
    class PlayerKnowsTheElement {

        @Test
        @DisplayName("reports the element as known")
        void reportsKnown() {
            Player player = playerWithItemKnowledge();
            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
            Curse curse = notFullyKnownCurse();

            assertTrue(ObjectUtils.objectElementIsKnown(player, curse, ElementEnum.ELEM_FIRE));
        }
    }

    /**
     * The third route: the curse's own known-shadow, {@link Curse#getKnownElInfo()}, already
     * carries a non-zero resistance level for this element — C's {@code if (obj->known->el_info
     * [element].res_level) return true;}. Fires even though the player's own rune knowledge does
     * not cover it.
     */
    @Nested
    @DisplayName("a not-fully-known curse whose known-shadow already carries the element")
    class ShadowKnowsTheElement {

        @Test
        @DisplayName("reports the element as known")
        void reportsKnown() {
            Player player = playerWithItemKnowledge();
            Curse curse = notFullyKnownCurse();
            curse.putKnownElementInfo(ElementEnum.ELEM_FIRE, elementInfo(1));

            assertTrue(ObjectUtils.objectElementIsKnown(player, curse, ElementEnum.ELEM_FIRE));
        }
    }

    /**
     * The fall-through: none of the three routes fires, so the element is reported unknown — C's
     * bare {@code return false;} at the end of the function.
     */
    @Nested
    @DisplayName("a not-fully-known curse that neither the player nor the shadow knows")
    class NeitherKnowsTheElement {

        @Test
        @DisplayName("reports the element as unknown")
        void reportsUnknown() {
            Player player = playerWithItemKnowledge();
            Curse curse = notFullyKnownCurse();

            assertFalse(ObjectUtils.objectElementIsKnown(player, curse, ElementEnum.ELEM_FIRE));
        }

        @Test
        @DisplayName("checks only the element asked about, not any other the shadow carries")
        void checksOnlyTheNamedElement() {
            Player player = playerWithItemKnowledge();
            Curse curse = notFullyKnownCurse();
            curse.putKnownElementInfo(ElementEnum.ELEM_COLD, elementInfo(1));

            assertFalse(ObjectUtils.objectElementIsKnown(player, curse, ElementEnum.ELEM_FIRE));
        }

        @Test
        @DisplayName("a known-shadow entry recorded at resistance level zero reports unknown")
        void aKnownButZeroShadowEntryReportsUnknown() {
            Player player = playerWithItemKnowledge();
            Curse curse = notFullyKnownCurse();
            curse.putKnownElementInfo(ElementEnum.ELEM_FIRE, elementInfo(0));

            assertFalse(ObjectUtils.objectElementIsKnown(player, curse, ElementEnum.ELEM_FIRE));
        }
    }
}
