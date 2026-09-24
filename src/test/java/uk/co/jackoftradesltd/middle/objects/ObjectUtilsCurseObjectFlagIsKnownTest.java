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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectUtils#curseObjectFlagIsKnown}, the curse-shaped counterpart to
 * {@link ObjectUtils#objectFlagIsKnown} and, through it, of C's {@code object_flag_is_known}
 * ({@code obj-knowledge.c}). C never calls that function with a curse — see
 * {@link Curse#isFullyKnown()} — so this pins the port's own extension against the same three
 * routes to "yes" the item-shaped sibling uses, in the same order: the curse is fully known, the
 * player's own rune knowledge has the flag, or the curse's own known-shadow has it. The "neither
 * knows" case pins the fall-through.
 *
 * <p>Curses are built with the long constructor, following
 * {@link ObjectUtilsObjectFlagIsKnownTest}'s pattern for items. A "not fully known" fixture moves
 * the curse's real to-hit off the (default zero) known to-hit, which is enough to fail
 * {@link Curse#isFullyKnown()}'s first check without needing the flag or element halves to
 * disagree with anything.
 *
 * <p>Class ObjectUtilsCurseObjectFlagIsKnownTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectUtilsCurseObjectFlagIsKnownTest {

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
     * the flag or element halves say.
     */
    private static Curse notFullyKnownCurse() {
        return curse(3);
    }

    private static Player playerWithItemKnowledge() {
        Player player = new Player();
        player.setItemKnowledge(new KnownObject());
        return player;
    }

    /**
     * The first route: a curse that is fully known reports every flag as known, whatever the
     * player's own knowledge and the curse's known-shadow say — C's
     * {@code if (object_fully_known(obj)) return true;} runs before either other check.
     */
    @Nested
    @DisplayName("a fully known curse")
    class FullyKnownCurse {

        @Test
        @DisplayName("reports the flag as known even though neither the player nor the shadow has it")
        void reportsKnownRegardlessOfTheOtherTwoChecks() {
            Player player = playerWithItemKnowledge();
            Curse curse = fullyKnownCurse();

            assertTrue(ObjectUtils.curseObjectFlagIsKnown(player, curse, ObjectFlag.OF_FREE_ACT));
        }
    }

    /**
     * The second route: the player's own rune knowledge, {@code p->obj_k} / {@link KnownObject},
     * has the flag — C's {@code if (of_has(p->obj_k->flags, flag)) return true;}. Fires even though
     * the curse itself is not fully known.
     */
    @Nested
    @DisplayName("a not-fully-known curse whose player already knows the flag")
    class PlayerKnowsTheFlag {

        @Test
        @DisplayName("reports the flag as known")
        void reportsKnown() {
            Player player = playerWithItemKnowledge();
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FREE_ACT);
            Curse curse = notFullyKnownCurse();

            assertTrue(ObjectUtils.curseObjectFlagIsKnown(player, curse, ObjectFlag.OF_FREE_ACT));
        }
    }

    /**
     * The third route: the curse's own known-shadow, {@link Curse#getKnownObjectFlags()}, already
     * carries the flag — C's {@code if (of_has(obj->known->flags, flag)) return true;}. Fires even
     * though the player's own rune knowledge does not have it.
     */
    @Nested
    @DisplayName("a not-fully-known curse whose known-shadow already carries the flag")
    class ShadowKnowsTheFlag {

        @Test
        @DisplayName("reports the flag as known")
        void reportsKnown() {
            Player player = playerWithItemKnowledge();
            Curse curse = notFullyKnownCurse();
            Flag<ObjectFlag> shadowFlags = new Flag<>(ObjectFlag.class);
            shadowFlags.on(ObjectFlag.OF_FREE_ACT);
            curse.setKnownObjectFlags(shadowFlags);

            assertTrue(ObjectUtils.curseObjectFlagIsKnown(player, curse, ObjectFlag.OF_FREE_ACT));
        }
    }

    /**
     * The fall-through: none of the three routes fires, so the flag is reported unknown — C's bare
     * {@code return false;} at the end of the function.
     */
    @Nested
    @DisplayName("a not-fully-known curse that neither the player nor the shadow knows")
    class NeitherKnowsTheFlag {

        @Test
        @DisplayName("reports the flag as unknown")
        void reportsUnknown() {
            Player player = playerWithItemKnowledge();
            Curse curse = notFullyKnownCurse();

            assertFalse(ObjectUtils.curseObjectFlagIsKnown(player, curse, ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("checks only the flag asked about, not any other the shadow carries")
        void checksOnlyTheNamedFlag() {
            Player player = playerWithItemKnowledge();
            Curse curse = notFullyKnownCurse();
            Flag<ObjectFlag> shadowFlags = new Flag<>(ObjectFlag.class);
            shadowFlags.on(ObjectFlag.OF_SEE_INVIS);
            curse.setKnownObjectFlags(shadowFlags);

            assertFalse(ObjectUtils.curseObjectFlagIsKnown(player, curse, ObjectFlag.OF_FREE_ACT));
        }
    }
}
