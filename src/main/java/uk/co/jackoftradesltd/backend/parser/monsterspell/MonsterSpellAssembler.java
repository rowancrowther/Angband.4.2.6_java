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

package uk.co.jackoftradesltd.backend.parser.monsterspell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.monsters.MonsterSpellLevel;
import uk.co.jackoftradesltd.middle.monsters.MonsterSpellType;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link MonsterSpellParseRecord}s the grammar produced into domain
 * {@link MonsterSpellType} objects - the interpretation half of the {@code monster_spell.txt}
 * pipeline, where deliberately unconverted token text becomes enums, ints and effects. It stands
 * in for the handlers {@code parse_mon_spell_name}, {@code parse_mon_spell_message_type},
 * {@code parse_mon_spell_hit}, {@code parse_mon_spell_effect} and its {@code effect-yx},
 * {@code dice} and {@code expr} companions, and the {@code power-cutoff}, {@code lore} and
 * {@code message-*} handlers in {@code mon-init.c}, which fill one {@code struct monster_spell}
 * a line at a time.
 * <p>
 * Five conversions carry real risk of diverging from the C original, and each is commented at
 * the point it happens in {@link #assemble}:
 * <ul>
 *   <li>the spell <em>name</em>, which indexes {@code list-mon-spells.h} via the {@code RSF_}
 *       prefix and, as in C's {@code grab_name}, is matched in exact case. {@code NONE} and
 *       {@code MAX} resolve, as they are rows of that header;</li>
 *   <li>the <em>optional</em> {@code msgt:} directive - 49 of the 91 shipped records omit it,
 *       and C's {@code parse_mon_spell_message_type} simply never runs for an absent line. When
 *       it is present it goes through {@link MessageType#lookupByName}, the port of
 *       {@code message_lookup_by_name}, so it is case-blind, may be a number, and cannot be
 *       {@code NONE};</li>
 *   <li>the <em>unsigned</em> {@code hit:}, which C reads with {@code parser_getuint} and refuses
 *       if the text starts with {@code -}; an absent {@code hit:} is 0, the {@code mem_zalloc}
 *       value;</li>
 *   <li>the effect block, delegated to {@link EffectAssembler}, which throws on the first
 *       unresolvable effect; and</li>
 *   <li>the per-power levels, delegated to {@link MonsterSpellLevelAssembler}.</li>
 * </ul>
 * <p>
 * Every failure in this class is fatal, as in C, where each handler returns a parser error and
 * the load stops: an unknown spell name, an unknown message type, a negative {@code hit:} and a
 * level list that comes back {@code null} each log and throw {@link DataFileParseException}
 * naming the file and the line of the {@code name:} directive. Nothing here skips a record.
 * <p>
 * The spells come back in C's order, not file order. {@code parse_mon_spell_name} pushes each new
 * record on the head of the chain ({@code s->next = h}), so {@code monster_spells} starts with the
 * last record in the file, and the assembler puts each spell at the front of its list to match. It
 * matters only when a name is used twice: {@code monster_spell_by_index} in {@code mon-spell.c}
 * then answers the later record, and a first-match search over this list does the same.
 * <p>
 * Dice and expression text inside the effects is kept raw and checked when the roll engine
 * exists, not here (see {@link EffectAssembler}). The levels are only as strict as
 * {@link MonsterSpellLevelAssembler}, which still drops a level with an unknown lore colour and
 * does not add C's implicit first level.
 *
 * <p>Class MonsterSpellAssembler coded before 260915, commented in full on 261027.
 *
 * @author Rowan Crowther
 */
public class MonsterSpellAssembler implements Assembler<MonsterSpellParseRecord, List<MonsterSpellType>> {
    /**
     * Logger that records the reason just before a {@link DataFileParseException} is thrown, so the
     * failing record's line and value are in the log as well as in the exception.
     *
     * <p>Field logger coded before 260915, commented in full on 261027.
     */
    private static final Logger logger = LogManager.getLogger(MonsterSpellAssembler.class);

    /**
     * Assemble every parsed monster-spell record into a {@link MonsterSpellType}, in C's order.
     *
     * <p>For each record, in file order:
     * <ol>
     *   <li><b>Name.</b> {@code "RSF_" + name} is looked up in {@link MonsterSpell} with
     *       {@code valueOf}, which is exact-case as C's {@code streq} is. {@code NONE} and
     *       {@code MAX} resolve; anything else is fatal, as
     *       {@code PARSE_ERROR_INVALID_SPELL_NAME} is.</li>
     *   <li><b>Message type.</b> An empty value means the line was absent and gives
     *       {@link MessageType#MSG_GENERIC}, C's zeroed {@code msgt}. Otherwise
     *       {@link MessageType#lookupByName} decides, and {@code null} from it is fatal, as
     *       {@code PARSE_ERROR_INVALID_MESSAGE} is.</li>
     *   <li><b>Hit chance.</b> Text starting with {@code -} is fatal, as C's {@code uint} test is,
     *       even for {@code -0}. Otherwise {@link ParseUtils#parseIntString} reads it in base 0, so
     *       {@code 010} is 8, and empty text, an absent {@code hit:}, is 0. C does not range-check
     *       it, and neither does this.</li>
     *   <li><b>Effects.</b> {@link EffectAssembler#assemble} returns every effect or throws.</li>
     *   <li><b>Levels.</b> {@link MonsterSpellLevelAssembler#assemble} returns every level or
     *       {@code null}, and {@code null} is fatal.</li>
     * </ol>
     * The finished spell is added at the front of the result, so the last record in the file ends
     * up first, as in C's {@code monster_spells} chain.
     *
     * <p>{@code errors} is passed to the effect and level assemblers. The effect assembler never
     * writes to it; the level assembler may append a message for a bad {@code power-cutoff:}
     * before it returns {@code null}, and that message is not repeated in the exception thrown
     * here.
     *
     * <p>Function assemble coded before 260915, commented in full on 261027.
     *
     * @param records  the raw parse records, in file order
     * @param errors   passed down to the effect and level assemblers; this method adds nothing
     * @param filename the data file the records came from, for error messages
     * @return the assembled monster spells, one per record, last record first
     * @throws DataFileParseException if a name, message type, hit chance, effect or level fails
     *                                to resolve
     */
    @Override
    public List<MonsterSpellType> assemble(@NotNull List<MonsterSpellParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<MonsterSpellType> monsterSpellTypes = new ArrayList<>();
        MonsterSpellLevelAssembler assembler = new MonsterSpellLevelAssembler();

        for (MonsterSpellParseRecord record : records) {
            int line = record.line();
            String nameString = record.name();
            MonsterSpell nameType;
            try {
                nameType = MonsterSpell.valueOf("RSF_" + nameString);
            } catch (IllegalArgumentException e) {
                String msg = "Monster spell starting at line: " + line +" of file: " + filename + " has " +
                        "an illegal monster spell value: " + record.name();
                logger.fatal(msg, e);
                throw new DataFileParseException(msg);
            }
            // msgt: is optional. [C] parse_mon_spell_message_type never runs for an absent line,
            // leaving the mem_zalloc'd msgt of 0 - and list-message.h starts at MSG(GENERIC), so
            // zero is MSG_GENERIC. 49 of the 91 shipped records omit the directive, so treating
            // an empty value as an invalid message type silently discards most of the table.
            MessageType messageType = MessageType.MSG_GENERIC;
            if (!record.messageType().isEmpty()) {
                    messageType = MessageType.lookupByName(record.messageType());
                    if (messageType == null) {
                        String msg = "Monster spell starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal message type value: " + record.messageType();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
            }
            if (ParseUtils.minusFound(record.hit())) {
                String msg = "Monster spell starting at line: " + line +" of file: " + filename + " has " +
                        "an illegal monster hit value: " + record.hit();
                logger.fatal(msg);
                throw new DataFileParseException(msg);   
            }
            int hit = ParseUtils.parseIntString("Monster spell", record.hit(), line, filename, "hit");
            // EffectAssembler is all-or-nothing: it returns every effect in this spell's block or
            // throws on the first one that fails to resolve, so a spell is never built with one of
            // its effects missing. It never returns null.
            List<Effect> effects = EffectAssembler.assemble(record.effects(), errors, filename);
            List<MonsterSpellLevel> levels = assembler.assemble(record.levels(), errors, filename);
            if (levels == null) {
                String msg = "Monster spell starting at line: " + line +" of file: " + filename + " has " +
                        "an illegal empty spell level list.";
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            monsterSpellTypes.addFirst(new MonsterSpellType(nameType, messageType, hit, effects, levels));
        }

        return monsterSpellTypes;
    }
}
