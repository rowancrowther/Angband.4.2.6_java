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

import org.jetbrains.annotations.Contract;

import java.util.Arrays;

/**
 * Flags applied to an object <em>kind</em> (the template), to an ego item or to an artifact's
 * kind — never to an individual object. They control random generation (the extra sustain, power
 * or resistance an ego item rolls), instant-artifact and quest-artifact status, display hints
 * (show dice, show multiplier), the ammunition a launcher fires, and whether a kind is "good"
 * for generation purposes. This is the Java port of the {@code KF_*} list in the C original's
 * {@code list-kind-flags.h}, in the same order, so the ordinal of each constant is the C flag
 * number.
 *
 * <p>The constants are {@code KF_NONE} (index 0, as in every C flag list), the fourteen flags
 * below and the {@code KF_MAX} count sentinel. The data-file spelling is the constant's name
 * without the {@code KF_} prefix, which is how the object, ego and object-base assemblers resolve
 * a {@code flags:} token: {@link #lookup} ({@code lookup("KF_" + token)}) in the object-kind
 * assembler, and {@code valueOf("KF_" + token)} in the ego and object-base assemblers.
 *
 * <p>The {@code IGNORE_FIRE}, {@code IGNORE_ACID}, {@code IGNORE_ELEC} and {@code IGNORE_COLD}
 * tokens that share the {@code flags:} line are <em>not</em> kind flags. C's
 * {@code grab_element_flag} ({@code obj-init.c}) turns them into {@code EL_INFO_IGNORE} on the
 * record's per-element info. The object-base, object-kind, ego-item and artifact assemblers do the
 * same, through {@code ParseUtils.grabElementFlag}, so the tokens have no constant here.
 * An earlier version of this enum carried four {@code KF_IGNORE_*} constants to swallow them;
 * those are gone, and the commented-out lines below mark where they were.
 *
 * <p>Enum ObjectKindFlag coded before 261012, commented in full on 261012, element-flag paragraph
 * corrected on 261105, lookup paragraph added on 261107.
 *
 * @author Rowan Crowther
 */
public enum ObjectKindFlag {
    /**
     * The "no flag" placeholder at index 0, C's {@code KF_NONE}. Never set on a kind.
     *
     * <p>Constant KF_NONE coded before 261012, commented in full on 261012.
     */
    KF_NONE(""),
    /**
     * An ego item with this flag gains one random high resist when it is made — C's
     * {@code KF_RAND_HI_RES}, tested in {@code ego_apply_magic} ({@code obj-make.c}), which marks
     * the chosen element {@code EL_INFO_RANDOM}. It is the last branch of the chain, so it only
     * applies when no sustain, power or base resist was chosen first.
     *
     * <p>Constant KF_RAND_HI_RES coded before 261012, commented in full on 261012.
     */
    KF_RAND_HI_RES(""),
    /**
     * An ego item with this flag gains one random sustain when it is made — C's
     * {@code KF_RAND_SUSTAIN}, the first branch tested in {@code ego_apply_magic}
     * ({@code obj-make.c}).
     *
     * <p>Constant KF_RAND_SUSTAIN coded before 261012, commented in full on 261012.
     */
    KF_RAND_SUSTAIN(""),
    /**
     * An ego item with this flag gains one random protection or miscellaneous power when it is
     * made — C's {@code KF_RAND_POWER}, tested in {@code ego_apply_magic} ({@code obj-make.c}).
     * A {@link #KF_RAND_RES_POWER} ego reaches the same branch when its roll comes up 1.
     *
     * <p>Constant KF_RAND_POWER coded before 261012, commented in full on 261012.
     */
    KF_RAND_POWER(""),
    /**
     * Marks a kind as the backing kind of an instant ("special") artifact — C's
     * {@code KF_INSTA_ART}. {@code write_dummy_object_record} ({@code obj-init.c}) sets it on the
     * kind it synthesizes for an artifact, and the artifact-making code in {@code obj-make.c},
     * the wizard-mode object lists and the ignore menu all use it to tell such a kind from an
     * ordinary one: ordinary generation skips it, and artifact generation requires it.
     *
     * <p>Constant KF_INSTA_ART coded before 261012, commented in full on 261012.
     */
    KF_INSTA_ART(""),
    /**
     * Marks a kind whose artifact is a quest artifact — C's {@code KF_QUEST_ART}. {@code mon-make.c}
     * looks for it when it picks the artifact a quest monster drops, and
     * {@code get_random_monster_object} ({@code mon-util.c}) skips a monster's held quest artifact
     * when choosing one of its carried objects at random.
     *
     * <p>Constant KF_QUEST_ART coded before 261012, commented in full on 261012.
     */
    KF_QUEST_ART(""),
    /**
     * An aware kind with this flag is fully known on sight — C's {@code KF_EASY_KNOW}, tested by
     * {@code easy_know} ({@code obj-knowledge.c}) only after the player is aware of the kind. Set
     * on whole bases from {@code object_base.txt}, and inherited by every kind of the base.
     *
     * <p>Constant KF_EASY_KNOW coded before 261012, commented in full on 261012.
     */
    KF_EASY_KNOW(""),
    /**
     * Marks a kind as "good" for generation — C's {@code KF_GOOD}, one of the tests in
     * {@code kind_is_good} ({@code obj-make.c}), which decides whether a good-object roll may use
     * the kind. {@code write_book_kind} ({@code init.c}) sets it on dungeon books.
     *
     * <p>Constant KF_GOOD coded before 261012, commented in full on 261012.
     */
    KF_GOOD(""),
    /**
     * Show the damage dice in the object's name, as {@code (2d5)} — C's {@code KF_SHOW_DICE},
     * tested in {@code obj-desc.c} (and only once the player knows the dice).
     *
     * <p>Constant KF_SHOW_DICE coded before 261012, commented in full on 261012.
     */
    KF_SHOW_DICE(""),
    /**
     * Show the launcher multiplier in the object's name, as {@code (x3)} — C's
     * {@code KF_SHOW_MULT}, tested in {@code obj-desc.c}.
     *
     * <p>Constant KF_SHOW_MULT coded before 261012, commented in full on 261012.
     */
    KF_SHOW_MULT(""),
    /**
     * The launcher fires shots (sling ammunition) — C's {@code KF_SHOOTS_SHOTS}, read in
     * {@code player-calcs.c} to set the player's ammunition type and by {@code ammo_damage_power}
     * ({@code obj-power.c}) when rating a launcher.
     *
     * <p>Constant KF_SHOOTS_SHOTS coded before 261012, commented in full on 261012.
     */
    KF_SHOOTS_SHOTS(""),
    /**
     * The launcher fires arrows — C's {@code KF_SHOOTS_ARROWS}; see {@link #KF_SHOOTS_SHOTS}.
     *
     * <p>Constant KF_SHOOTS_ARROWS coded before 261012, commented in full on 261012.
     */
    KF_SHOOTS_ARROWS(""),
    /**
     * The launcher fires bolts — C's {@code KF_SHOOTS_BOLTS}; see {@link #KF_SHOOTS_SHOTS}.
     *
     * <p>Constant KF_SHOOTS_BOLTS coded before 261012, commented in full on 261012.
     */
    KF_SHOOTS_BOLTS(""),
    /**
     * An ego item with this flag gains one random base resist (acid, lightning, fire or cold) when
     * it is made — C's {@code KF_RAND_BASE_RES}, tested in {@code ego_apply_magic}
     * ({@code obj-make.c}). A {@link #KF_RAND_RES_POWER} ego reaches the same branch when its roll
     * is 2 or 3.
     *
     * <p>Constant KF_RAND_BASE_RES coded before 261012, commented in full on 261012.
     */
    KF_RAND_BASE_RES(""),
    /**
     * An ego item with this flag rolls 1 to 3 when it is made: 1 gives it a random power (as
     * {@link #KF_RAND_POWER}), 2 or 3 a random base resist (as {@link #KF_RAND_BASE_RES}) — C's
     * {@code KF_RAND_RES_POWER}, the first test in {@code ego_apply_magic} ({@code obj-make.c}).
     * A {@link #KF_RAND_SUSTAIN} on the same ego still wins, because it is checked first.
     *
     * <p>Constant KF_RAND_RES_POWER coded before 261012, commented in full on 261012.
     */
    KF_RAND_RES_POWER(""),
//
//    // Added in to handle IGNORE flags
//    KF_IGNORE_FIRE(""),
//    KF_IGNORE_ACID(""),
//    KF_IGNORE_ELEC(""),
//    KF_IGNORE_COLD(""),

    /**
     * The count sentinel, C's {@code KF_MAX}. Its ordinal, 15, is the number of real kind flags plus
     * the {@link #KF_NONE} slot, which is what sizes C's {@code KF_SIZE} bit array and the Java
     * {@code Flag<ObjectKindFlag>} sets. Never set on a kind.
     *
     * <p>Constant KF_MAX coded before 261012, commented in full on 261012.
     */
    KF_MAX("");

    /**
     * The flag's message string — the second column of C's {@code KF(symbol, message)} entries in
     * {@code list-kind-flags.h}. Every entry there is the empty string, so every value here is too;
     * unlike the object-flag list, nothing in C ever prints a kind flag's message. The field is
     * carried only to keep the list's shape.
     *
     * <p>Field flag coded before 261012, commented in full on 261012.
     */
    private String flag;

    /**
     * Binds a kind flag to its message string, as the {@code KF(a, b)} macro does for each line of
     * {@code list-kind-flags.h}.
     *
     * <p>Constructor ObjectKindFlag coded before 261012, commented in full on 261012.
     *
     * @param flag the message string; empty for every kind flag
     */
    @Contract(pure = true)
    ObjectKindFlag(String flag) {
        this.flag = flag;
    }

    /**
     * Resolves a {@code KF_}-prefixed constant name to its kind flag, or {@code null} when the name
     * is not a kind flag. This is the Java side of C's {@code lookup_flag} ({@code datafile.c}) run
     * over the {@code kind_flags} name table in {@code obj-init.c}, which {@code grab_flag} uses for
     * each {@code flags:} token of an object kind, object base or ego item. C returns
     * {@code FLAG_END} (0) for "not found" and the assembler turns that into an invalid-flag error;
     * here {@code null} is the "not found" result, and the caller raises the error.
     *
     * <p>Two C details carry over. {@code lookup_flag} starts its search at {@code FLAG_START}
     * (1), so slot 0 is never matched: {@code "KF_NONE"} returns {@code null}, as {@code "NONE"}
     * is "not found" in C. The name table is built from the whole list, so its last entry is
     * {@code MAX}: {@code "KF_MAX"} returns {@link #KF_MAX}, as {@code "MAX"} resolves to flag 15
     * in C. The match is exact and case-sensitive ({@code streq} in C), and a {@code null} or
     * unprefixed name ({@code "EASY_KNOW"}) is simply not found. Unlike {@code valueOf}, nothing
     * is thrown for an unknown name.
     *
     * <p>Rejecting the {@code NONE} and {@code MAX} sentinels is the caller's job, not this
     * method's: {@code ItemObjectAssembler} rejects {@code NONE} before it calls here, and
     * {@code ObjectBaseAssembler} handles both tokens itself.
     *
     * <p>Method lookup coded before 261107, commented in full on 261107.
     *
     * @param flag the constant name including its {@code KF_} prefix, e.g. {@code "KF_GOOD"}
     * @return the matching kind flag, or {@code null} for {@code KF_NONE} or any unknown name
     */
    public static ObjectKindFlag lookup(String flag) {
        ObjectKindFlag result = Arrays.stream(ObjectKindFlag.values()).filter(okf -> okf.name().equals(flag)).findFirst().orElse(null);
        if (result == ObjectKindFlag.KF_NONE) return null;
        return result;
    }

    /**
     * Returns the flag's message string — the port of reading the second column of a
     * {@code KF()} entry. Always the empty string for a kind flag, and nothing in {@code src/main}
     * reads it.
     *
     * <p>Function getFlag coded before 261012, commented in full on 261012.
     *
     * @return this flag's message string
     */
    @Contract(pure = true)
    public String getFlag() {
        return flag;
    }
}