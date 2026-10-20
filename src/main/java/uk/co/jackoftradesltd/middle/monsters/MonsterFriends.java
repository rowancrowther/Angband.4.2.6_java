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

package uk.co.jackoftradesltd.middle.monsters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterGroupRole;

/**
 * A "friends" entry for a monster race: another specific {@link MonsterRace} that
 * may be generated alongside it, with the group role they take, the chance they
 * appear, and a dice expression for how many. This is the Java port of the C
 * original's {@code struct monster_friends} ({@code src/monster.h}), one
 * {@code friends:} line of {@code monster.txt}.
 *
 * <p>An entry is built in two steps, as in C. {@code MonsterAssembler} builds it with the
 * companion's name as text, because the file is not ordered and a race may name a companion
 * defined further down; {@link MonsterRace#resolveFriends()} then calls {@link #setRace(String)}
 * once every race exists, which is the friends half of the conversion loop in
 * {@code mon-init.c}, function {@code finish_parse_monster}. Until then {@link #race} is
 * {@code null}, as {@code f->race} is in C's zero-filled record.
 *
 * <p>The record is held in a {@code List<MonsterFriends>} on {@link MonsterRace}, where C
 * chains the entries with a {@code next} pointer, so that field has no counterpart here. C
 * prepends each entry as it parses, so its chain runs in reverse file order; the Java list is
 * in the same reverse order (see the class block of {@link MonsterRace}).
 *
 * <p>The class is a plain holder: nothing reads the role, chance or dice yet. Their readers
 * are C's {@code place_new_monster} in {@code mon-make.c}, which rolls the chance and the
 * count, and {@code lore_append_friends} in {@code mon-lore.c}, which are later chapters.
 *
 * <p>Class MonsterFriends coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class MonsterFriends {
    /**
     * Shared logger, used by {@link #setRace(String)} to record a companion name that matches no
     * race before the exception is thrown.
     *
     * <p>Field logger coded before 261014, commented in full on 261014.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The companion race's name as text, C's {@code f->name}. A {@code "same"} entry (any case)
     * holds the owning race's own name, substituted by {@code MonsterAssembler} where C tests for
     * it in {@code finish_parse_monster}. C frees this string once the race is found; here it is
     * kept, because {@link MonsterRace#resolveFriends()} reads it back through {@link #getName()}.
     *
     * <p>Field name coded before 261014, commented in full on 261014.
     */
    private String name;
    /**
     * The resolved companion race, C's {@code f->race}. It is {@code null} until
     * {@link #setRace(String)} runs, and is never {@code null} after it returns, because a name
     * that finds no race throws.
     *
     * <p>Field race coded before 261014, commented in full on 261014.
     */
    private MonsterRace race;
    /**
     * The group role the companions take, C's {@code f->role}. The assembler gives
     * {@code MON_GROUP_SERVANT} or {@code MON_GROUP_BODYGUARD} for the two role words the file
     * allows, and {@code MON_GROUP_MEMBER} when the line names none, as C does.
     *
     * <p>Field role coded before 261014, commented in full on 261014.
     */
    private MonsterGroupRole role;
    /**
     * Percentage chance the companions appear, C's {@code f->percent_chance}. C holds it as an
     * unsigned int and tests it as {@code randint0(100) >= percent_chance}, so a chance of 0 never
     * places them and 100 always does. {@code MonsterAssembler} keeps it within 0 to 100.
     *
     * <p>Field percentChance coded before 261014, commented in full on 261014.
     */
    private int percentChance;
    /**
     * Number of dice for the companion count, C's {@code f->number_dice}, the {@code N} of the
     * line's {@code NdM}. The count is rolled with {@code damroll(number_dice, number_side)}.
     *
     * <p>Field numberDice coded before 261014, commented in full on 261014.
     */
    private int numberDice;
    /**
     * Sides per die for the companion count, C's {@code f->number_side} (singular in C), the
     * {@code M} of the line's {@code NdM}. The base and level bonus of the parsed value are
     * dropped, as C drops them.
     *
     * <p>Field numberSides coded before 261014, commented in full on 261014.
     */
    private int numberSides;

    /**
     * Builds a friends entry from one {@code friends:} line. The race is left {@code null}; it is
     * found from {@code name} later by {@link #setRace(String)}. This takes the place of the
     * {@code parse_monster_friends} handler in {@code mon-init.c}, which fills the same five
     * fields; the chance, dice, role and name checks it makes are the assembler's job here.
     *
     * <p>Constructor MonsterFriends coded before 261014, commented in full on 261014.
     *
     * @param name          companion race name, with any {@code "same"} already replaced by the
     *                      owning race's name
     * @param role          group role of the companions
     * @param percentChance appearance chance, 0 to 100
     * @param numberDice    dice for the count
     * @param numberSides   sides per die for the count
     */
    public MonsterFriends(String name, MonsterGroupRole role, int percentChance, int numberDice,
                          int numberSides) {
        this.name = name;
        this.role = role;
        this.percentChance = percentChance;
        this.numberDice = numberDice;
        this.numberSides = numberSides;
    }

    /**
     * Resolves and stores this entry's companion race from a name, throwing if no such race exists.
     * The name is always concrete by this point — a {@code "same"} self-reference was already
     * substituted for the owning race's name in the assembler — so this is a plain global lookup.
     *
     * <p>This is the friends half of the conversion loop in {@code mon-init.c}, function
     * {@code finish_parse_monster}, minus its {@code "same"} test. C answers with
     * {@code lookup_monster} ({@code mon-util.c}): the first race whose name equals the text
     * ignoring case, otherwise the first whose name contains the text ignoring case.
     * {@link MonsterRegistry#lookupMonsterRace} applies the same rule, so a friend written
     * {@code Wolf} finds {@code Wolf} and not {@code White wolf} or {@code Werewolf}, and one
     * written {@code cave bear} finds {@code Cave bear}.
     *
     * <p>A name that matches no race logs the error and throws {@link IllegalArgumentException},
     * standing in for C's {@code quit_fmt}. The message names the missing companion but not the
     * race that asked for it, which C's does. {@link #race} is assigned the lookup result before
     * the check, so a failed call leaves it {@code null}, even on an entry that had been resolved.
     *
     * <p>The method does not store {@code raceName} in {@link #name}; the caller passes
     * {@link #getName()}, so the two agree.
     *
     * <p>Function setRace coded before 261014, commented in full on 261014.
     *
     * @param raceName the companion race's name, resolved via {@link MonsterRegistry#lookupMonsterRace}
     * @throws IllegalArgumentException if no race matches the name
     * @throws IllegalStateException    if the monster races have not been loaded yet, from
     *                                  {@link MonsterRegistry#lookupMonsterRace}
     */
    public void setRace(@NotNull String raceName) {
        this.race = MonsterRegistry.lookupMonsterRace(raceName);

        if (race == null) {
            String message = "Monster race not found: " + raceName;
            IllegalArgumentException e = new IllegalArgumentException(message);
            logger.error(message, e);
            throw e;
        }
    }

    /**
     * Returns the companion's name as text, C's {@code f->name}, which {@link MonsterRace#resolveFriends()}
     * hands to {@link #setRace(String)}. C frees the string after the lookup; here it stays readable.
     *
     * <p>Function getName coded before 261014, commented in full on 261014.
     *
     * @return the companion's name as written in {@code monster.txt} (or the owning race's name, if it
     * was originally the {@code "same"} self-reference)
     */
    public String getName() {
        return name;
    }
}
