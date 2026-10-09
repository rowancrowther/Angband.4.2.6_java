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

import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.List;

/**
 * One summon type from {@code summon.txt} - the kind of monster a "summon" effect may call up
 * (e.g. {@code ANIMAL}, {@code HI_UNDEAD}, {@code UNIQUE}). A summon type constrains which monster
 * races are eligible via any combination of allowed {@link MonsterBase} types and a single
 * restricting {@link MonsterRaceFlag}, records whether uniques may be chosen, and optionally names
 * another summon type to fall back to when it can find no valid monster.
 * <p>
 * Java port of {@code struct summon}, declared in {@code mon-summon.h} and filled by the
 * {@code parse_summon_*} functions and {@code finish_parse_summon} in {@code mon-summon.c}. Instances
 * are built by {@code SummonAssembler} from the raw {@code SummonParseRecord}s; see {@link #fallback} /
 * {@link #fallbackName} for how the fallback cross-reference is resolved in a second pass.
 * <p>
 * The record is read by {@code summon_specific_okay} in {@code mon-summon.c}, which rejects a unique
 * unless {@link #uniquesAllowed} is set, rejects a race whose base is not in {@link #bases} (when that
 * list is not empty), and rejects a race that lacks {@link #raceFlag} (when it is not
 * {@link MonsterRaceFlag#RF_NONE}). The special {@code KIN} test in that function is made by name,
 * not by any field held here.
 * <p>
 * Differences from the C struct:
 * <ul>
 *     <li>{@code next} is not held. C chains the records in reverse file order and copies them into
 *     the {@code summons} array; the Java records stay in file order in a {@code List}.</li>
 *     <li>{@code message_type} is a {@link MessageType} rather than an index into the message table.</li>
 *     <li>{@code fallback} is a {@code Summon} reference, or {@code null}, rather than an index into
 *     {@code summons} with {@code -1} for none.</li>
 *     <li>{@code bases} is a {@code List} in file order, where C prepends each {@code base:} line to a
 *     linked list and so holds them in reverse. Only membership is tested, so the order has no effect.</li>
 *     <li>{@code race_flag} is a {@link MonsterRaceFlag} with {@code RF_NONE} for C's zero.</li>
 * </ul>
 *
 * <p>Class Summon coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class Summon {
    /**
     * The summon type's name, such as {@code KIN} or {@code HI_UNDEAD}: the code that other records
     * and the {@code SUMMON} effect use to refer to it. It is the {@code name:} line of
     * {@code summon.txt}, and is what {@code summon_name_to_idx} in {@code mon-summon.c} (the Java
     * {@code MonsterRegistry.lookupSummon}) matches exactly and case-sensitively. Corresponds to
     * {@code name} in {@code struct summon}.
     *
     * <p>Field name coded before 261009, commented in full on 261009.
     */
    private String name;
    /**
     * The message category used when this summon occurs, which may select the sound played. It is the
     * {@code msgt:} line, resolved by the assembler to a {@link MessageType}, and is what
     * {@code summon_message_type} in {@code mon-summon.c} returns. Corresponds to {@code message_type}
     * in {@code struct summon}, which holds the index into the message table instead.
     *
     * <p>Field messageType coded before 261009, commented in full on 261009.
     */
    private MessageType messageType;
    /**
     * Whether unique monsters may be chosen by this summon. {@code summon_specific_okay} in
     * {@code mon-summon.c} refuses a unique race when this is {@code false}. It is the {@code uniques:}
     * line; C sets {@code unique_allowed} for any non-zero integer, where the assembler sets this only
     * for the token {@code 1}. The shipped {@code summon.txt} uses only {@code 0} and {@code 1}, so
     * the two agree on it. Corresponds to {@code unique_allowed} in {@code struct summon}.
     *
     * <p>Field uniquesAllowed coded before 261009, commented in full on 261009.
     */
    private boolean uniquesAllowed;
    /**
     * The monster base types this summon may draw from, one per {@code base:} line in file order.
     * An empty list means any base is allowed. {@code summon_specific_okay} in {@code mon-summon.c}
     * accepts a race whose base is in the list. A kept {@code Summon} never holds a {@code null}
     * element, because the assembler drops a record whose base does not resolve. Corresponds to the
     * {@code bases} linked list in {@code struct summon}, which C holds in reverse file order.
     *
     * <p>Field bases coded before 261009, commented in full on 261009.
     */
    private List<MonsterBase> bases;
    /**
     * A race flag restricting which monsters this summon may choose, or
     * {@link MonsterRaceFlag#RF_NONE} when the {@code race-flag:} line is absent. When it is set,
     * {@code summon_specific_okay} in {@code mon-summon.c} refuses a race that lacks the flag.
     * Corresponds to {@code race_flag} in {@code struct summon}, where zero means no restriction.
     *
     * <p>Field raceFlag coded before 261009, commented in full on 261009.
     */
    private MonsterRaceFlag raceFlag;
    /**
     * The summon type to fall back to when this one can find no valid monster, or {@code null} if
     * it has none. This is the resolved reference; it is left {@code null} at construction and wired
     * up later by {@code SummonAssembler}'s second pass (see {@link #fallbackName} for why).
     * Corresponds to {@code fallback} in {@code struct summon}, which is an index into {@code summons}
     * with {@code -1} for none, and is what {@code summon_fallback_type} in {@code mon-summon.c} returns.
     *
     * <p>Field fallback coded before 261009, commented in full on 261009.
     */
    private Summon fallback;

    /**
     * The raw name of the fallback summon type as read from the data file (empty string if none).
     * Retained because a summon may reference another that is defined later in the file, so the
     * fallback cross-references cannot be resolved during construction. The assembler builds every
     * summon first, then makes a second pass turning each {@code fallbackName} into the
     * {@link #fallback} reference above via {@link #setFallback}. Corresponds to {@code fallback_name}
     * in {@code struct summon}, which is {@code NULL} when there is no {@code fallback:} line.
     *
     * <p>Field fallbackName coded before 261009, commented in full on 261009.
     */
    private String fallbackName;
    /**
     * Description of the summon, used in messages as a noun phrase such as {@code a monster} or
     * {@code similar monsters}. It is the {@code desc:} line, and is what {@code summon_desc} in
     * {@code mon-summon.c} returns. Corresponds to {@code desc} in {@code struct summon}.
     *
     * <p>Field description coded before 261009, commented in full on 261009.
     */
    private String description;

    /**
     * Build a summon type from its parsed data-file fields.
     * <p>
     * {@code fallback} is normally passed as {@code null}: the assembler cannot resolve the fallback
     * reference until every summon exists, so it supplies the raw {@code fallbackName} here and fills
     * in the reference afterwards through {@link #setFallback}. The arguments are stored as given: the
     * list is not copied, and nothing is checked for {@code null}.
     *
     * <p>Function Summon coded before 261009, commented in full on 261009.
     *
     * @param name          summon name
     * @param messageType    message category used when this summon occurs
     * @param uniquesAllowed whether uniques may be summoned
     * @param bases          allowed monster base types (empty for no base restriction)
     * @param raceFlag       restricting race flag, or {@link MonsterRaceFlag#RF_NONE} for none
     * @param fallback       the resolved fallback summon, or {@code null} (usually resolved later)
     * @param fallbackName   the raw fallback summon name, or {@code ""}; see {@link #fallbackName}
     * @param description    description used in messages
     */
    public Summon(String name, MessageType messageType, boolean uniquesAllowed, List<MonsterBase> bases,
                  MonsterRaceFlag raceFlag, Summon fallback, String fallbackName, String description) {
        this.name = name;
        this.messageType = messageType;
        this.uniquesAllowed = uniquesAllowed;
        this.bases = bases;
        this.raceFlag = raceFlag;
        this.fallback = fallback;
        this.fallbackName = fallbackName;
        this.description = description;
    }

    /**
     * Set the resolved fallback summon. Called by {@code SummonAssembler}'s second pass once every
     * summon has been built, to link this summon's {@link #fallbackName} to its actual target. The
     * field is mutable precisely because these references can point forward (or form cycles) and so
     * cannot all be satisfied at construction time - the original C achieves the same effect by
     * back-patching a fallback index in {@code finish_parse_summon}.
     *
     * <p>Function setFallback coded before 261009, commented in full on 261009.
     *
     * @param fallback the summon this one falls back to, or {@code null} for none
     */
    public void setFallback(Summon fallback) {
        this.fallback = fallback;
    }

    /**
     * Gets the summon type's name, the {@code name:} line of {@code summon.txt}.
     *
     * <p>Function getName coded before 261009, commented in full on 261009.
     *
     * @return the summon type's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the message category used when this summon occurs; the Java form of
     * {@code summon_message_type} in {@code mon-summon.c}, which returns the message index.
     *
     * <p>Function getMessageType coded before 261009, commented in full on 261009.
     *
     * @return the message category used when this summon occurs
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Gets whether unique monsters may be summoned, the {@code unique_allowed} test in
     * {@code summon_specific_okay} in {@code mon-summon.c}.
     *
     * <p>Function isUniquesAllowed coded before 261009, commented in full on 261009.
     *
     * @return whether unique monsters may be summoned
     */
    public boolean isUniquesAllowed() {
        return uniquesAllowed;
    }

    /**
     * Gets the monster base types this summon may draw from, in file order. The list is the live one,
     * not a copy, and is empty when any base is allowed.
     *
     * <p>Function getBases coded before 261009, commented in full on 261009.
     *
     * @return the monster base types this summon may draw from
     */
    public List<MonsterBase> getBases() {
        return bases;
    }

    /**
     * Gets the race flag a summoned monster must have, the {@code race_flag} test in
     * {@code summon_specific_okay} in {@code mon-summon.c}.
     *
     * <p>Function getRaceFlag coded before 261009, commented in full on 261009.
     *
     * @return the restricting race flag, or {@link MonsterRaceFlag#RF_NONE} for no restriction
     */
    public MonsterRaceFlag getRaceFlag() {
        return raceFlag;
    }

    /**
     * Gets the summon to try when this one finds no monster; the Java form of
     * {@code summon_fallback_type} in {@code mon-summon.c}, which returns the fallback's index, or
     * {@code -1} for none.
     *
     * <p>Function getFallback coded before 261009, commented in full on 261009.
     *
     * @return the resolved fallback summon, or {@code null} if this summon has no fallback
     */
    public Summon getFallback() {
        return fallback;
    }

    /**
     * Gets the fallback name exactly as the {@code fallback:} line gave it. It is non-empty even when
     * the name matched no summon and {@link #getFallback} is {@code null}.
     *
     * <p>Function getFallbackName coded before 261009, commented in full on 261009.
     *
     * @return the raw fallback summon name from the data file, or {@code ""} if none; see
     * {@link #fallbackName}
     */
    public String getFallbackName() {
        return fallbackName;
    }

    /**
     * Gets the summon's description; the Java form of {@code summon_desc} in {@code mon-summon.c}.
     * C also returns a null pointer for an index outside {@code 0..summon_max}; that test belongs to
     * the caller that holds the index, as this method is called on a record already found.
     *
     * <p>Function getDescription coded before 261009, commented in full on 261009.
     *
     * @return the summon's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Builds a debug string with no counterpart in C: the name, message type, {@code unique} when
     * uniques are allowed, {@code has bases} when the base list is not empty (the bases themselves are
     * not listed), the race flag, the fallback's name and the description. The fallback's name comes
     * from the resolved {@link #fallback} reference, so it is blank for an unresolved name even when
     * {@link #fallbackName} is not.
     *
     * <p>Function toString coded before 261009, commented in full on 261009.
     *
     * @return a debug string summarizing this summon type
     */
    @Override
    public String toString() {
        String fallbackName = fallback == null ? "" : fallback.getName();
        return name + " " + messageType + " " + (uniquesAllowed ? "unique " : "") +
                (!bases.isEmpty() ? "has bases " : "") +
                raceFlag + " " + fallbackName + " " + description;
    }
}