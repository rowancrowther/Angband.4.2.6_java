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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.List;

/**
 * One summon type from {@code summon.txt} - the kind of monster a "summon" effect may call up
 * (e.g. {@code ANIMAL}, {@code HI_UNDEAD}, {@code UNIQUE}). A summon type constrains which monster
 * races are eligible via any combination of allowed {@link MonsterBase} types and a single
 * restricting {@link MonsterRaceFlag}, records whether uniques may be chosen, and optionally names
 * another summon type to fall back to when it can find no valid monster.
 * <p>
 * Java port of {@code struct summon} in the original {@code mon-summon.c}. Instances are built by
 * {@code SummonAssembler} from the raw {@code SummonParseRecord}s; see {@link #fallback} /
 * {@link #fallbackName} for how the fallback cross-reference is resolved in a second pass.
 *
 * @author Rowan Crowther
 */
public class Summon {
    /**
     * The summon type's name.
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * The message/sound category used when this summon occurs.
     *
     * @author Rowan Crowther
     */
    private MessageType messageType;
    /**
     * Whether unique monsters may be chosen by this summon.
     *
     * @author Rowan Crowther
     */
    private boolean uniquesAllowed;
    /**
     * The monster base types this summon may draw from.
     *
     * @author Rowan Crowther
     */
    private List<MonsterBase> bases;
    /**
     * A race flag restricting which monsters this summon may choose.
     *
     * @author Rowan Crowther
     */
    private MonsterRaceFlag raceFlag;
    /**
     * The summon type to fall back to when this one can find no valid monster, or {@code null} if
     * it has none. This is the resolved reference; it is left {@code null} at construction and wired
     * up later by {@code SummonAssembler}'s second pass (see {@link #fallbackName} for why).
     *
     * @author Rowan Crowther
     */
    private Summon fallback;

    /**
     * The raw name of the fallback summon type as read from the data file (empty string if none).
     * Retained because a summon may reference another that is defined later in the file, so the
     * fallback cross-references cannot be resolved during construction. The assembler builds every
     * summon first, then makes a second pass turning each {@code fallbackName} into the
     * {@link #fallback} reference above via {@link #setFallback}.
     *
     * @author Rowan Crowther
     */
    private String fallbackName;
    /**
     * Human-readable description of the summon.
     *
     * @author Rowan Crowther
     */
    private String description;

    /**
     * Build a summon type from its parsed data-file fields.
     * <p>
     * {@code fallback} is normally passed as {@code null}: the assembler cannot resolve the fallback
     * reference until every summon exists, so it supplies the raw {@code fallbackName} here and fills
     * in the reference afterwards through {@link #setFallback}.
     *
     * @param name           summon name
     * @param messageType    message category used when this summon occurs
     * @param uniquesAllowed whether uniques may be summoned
     * @param bases          allowed monster base types (empty for no base restriction)
     * @param raceFlag       restricting race flag, or {@link MonsterRaceFlag#RF_NONE} for none
     * @param fallback       the resolved fallback summon, or {@code null} (usually resolved later)
     * @param fallbackName   the raw fallback summon name, or {@code ""}; see {@link #fallbackName}
     * @param description    description used in messages
     * @author Rowan Crowther
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
     * @param fallback the summon this one falls back to, or {@code null} for none
     * @author Rowan Crowther
     */
    public void setFallback(Summon fallback) {
        this.fallback = fallback;
    }

    /**
     * @return the summon type's name
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }

    /**
     * @return the message category used when this summon occurs
     * @author Rowan Crowther
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * @return whether unique monsters may be summoned
     * @author Rowan Crowther
     */
    public boolean isUniquesAllowed() {
        return uniquesAllowed;
    }

    /**
     * @return the monster base types this summon may draw from
     * @author Rowan Crowther
     */
    public List<MonsterBase> getBases() {
        return bases;
    }

    /**
     * @return the restricting race flag
     * @author Rowan Crowther
     */
    public MonsterRaceFlag getRaceFlag() {
        return raceFlag;
    }

    /**
     * @return the resolved fallback summon, or {@code null} if this summon has no fallback
     * @author Rowan Crowther
     */
    public Summon getFallback() {
        return fallback;
    }

    /**
     * @return the raw fallback summon name from the data file, or {@code ""} if none; see
     * {@link #fallbackName}
     * @author Rowan Crowther
     */
    public String getFallbackName() {
        return fallbackName;
    }

    /**
     * @return the summon's description
     * @author Rowan Crowther
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return a debug string summarising this summon type
     * @author Rowan Crowther
     */
    @Override
    public String toString() {
        String fallbackName = fallback == null ? "" : fallback.getName();
        return name + " " + messageType + " " + (uniquesAllowed ? "unique " : "") +
                (!bases.isEmpty() ? "has bases " : "") +
                raceFlag + " " + fallbackName + " " + description;
    }
}