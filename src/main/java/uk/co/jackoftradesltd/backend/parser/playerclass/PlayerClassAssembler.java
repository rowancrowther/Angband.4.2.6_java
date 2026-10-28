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

package uk.co.jackoftradesltd.backend.parser.playerclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.StartItem;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The top-level assembler for {@code class.txt}: it turns each raw {@link PlayerClassParseRecord}
 * into a domain {@link PlayerClass}, delegating the nested structures to the specialized
 * assemblers — {@link ClassEquipAssembler} for starting gear and {@link ClassMagicAssembler} (which
 * fans out to books, spells and effects) for casters.
 *
 * <p>This is the port of the {@code parse_class_*} handlers in {@code init.c} that fill a
 * {@code struct player_class}: {@code parse_class_stats}, the nine {@code parse_class_skill_*}
 * handlers, {@code parse_class_hitdie}, {@code parse_class_exp}, {@code parse_class_max_attacks},
 * {@code parse_class_min_weight}, {@code parse_class_str_mult}, {@code parse_class_title},
 * {@code parse_class_obj_flags} and {@code parse_class_play_flags}. The {@code equip:} and
 * {@code magic:} handlers belong to the two nested assemblers. C builds the class in the parser
 * as the lines arrive; here the grammar has already gathered the whole class into a record, so
 * this class does the same work in one pass per record.
 *
 * <p>It resolves the file's own token names to enums ({@link Stats}, {@link PlayerSkill},
 * {@link ObjectFlag}, {@link PlayerFlag}) and parses every scalar. A fault is fatal, as it is in
 * C, where the matching {@code PARSE_ERROR_*} stops the parse: the problem is logged and a
 * {@link DataFileParseException} naming the line and file is thrown, so one broken class ends
 * the load. The {@code errors} list the {@link Assembler} contract passes in is never written to
 * here; it is handed on to {@link ClassMagicAssembler} and {@link ClassEquipAssembler}. The
 * checks run in a fixed order (stats, base skills, skill increments, the five scalars, magic,
 * equipment, object flags, player flags, titles), where C reports whichever line comes first in
 * the file; with several faults in one class only the message differs.
 *
 * <p>The classes come back in file order, which is C's {@code cidx} order: {@code class_parser}'s
 * {@code finish_parse_class} numbers the prepended list so the last class in the file holds the
 * highest index. A non-caster's {@link ClassMagic} is normalized to the {@link ClassMagic#NONE}
 * sentinel rather than left null.
 *
 * <p>Differences from C, none of which a shipped {@code class.txt} reaches:
 * <ul>
 *   <li>C zero-fills the class, so a stat, skill or scalar whose line is absent reads 0. Here an
 *       absent stat or skill has no map entry, and a missing scalar is the empty text that
 *       {@link ParseUtils#parseIntString(String, String, int, String, String)} turns into 0.
 *       {@link PlayerClass} answers 0 for a skill with no entry.</li>
 *   <li>A figure outside the range of an {@code int} is clamped, where C wraps it into its 32-bit
 *       field (see {@code docs/Accepted_Deviations.md}).</li>
 *   <li>Every message names the line of the class's {@code name:} line, where C names the line of
 *       the directive at fault.</li>
 *   <li>Outside this class, {@code PlayerClassGrammar.g4} and {@code PlayerClassLexer.g4} refuse
 *       several shapes C accepts, such as a class with only a name or an empty flag list (see
 *       {@code docs/grammar_issues.md}).</li>
 * </ul>
 *
 * <p>Class PlayerClassAssembler coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class PlayerClassAssembler implements Assembler<PlayerClassParseRecord, List<PlayerClass>> {
    /**
     * Receives the fatal message for each rejected class record before the
     * {@link DataFileParseException} carrying the same text is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261028.
     */
    private static final Logger logger = LogManager.getLogger(PlayerClassAssembler.class);

    /**
     * Assembles every parsed class record into a {@link PlayerClass}, resolving its stats, skills,
     * flags and scalars, and building its equipment and (for casters) magic through the nested
     * assemblers.
     *
     * <p><b>Stats and skills.</b> The grammar keys the stat map by {@code STAT_STR},
     * {@code STAT_INT}, {@code STAT_WIS}, {@code STAT_DEX} and {@code STAT_CON}, and the two skill
     * maps by the {@code SKILL_*} name of each {@code skill-*} line that appeared. Each key is
     * resolved with {@code valueOf} and each value read as C's {@code parser_getint} reads it
     * (base 0, signed, so {@code stats:-2:...} is allowed). The base goes into the class skills
     * and the increment into the extra skills, as {@code c_skills[]} and {@code x_skills[]}. A
     * repeated line overwrites the earlier one, as it does in C. The grammar only ever supplies
     * those fixed names, so the unknown-name arms guard against a hand-built record; they would
     * also pass {@code STAT_NONE}, {@code STAT_MAX}, {@code SKILL_NONE} and {@code SKILL_MAX},
     * which the grammar cannot produce.
     *
     * <p><b>Scalars.</b> {@code hitdie:}, {@code exp:}, {@code max-attacks:},
     * {@code min-weight:} and {@code strength-multiplier:} become C's {@code c_mhp}, {@code c_exp},
     * {@code max_attacks}, {@code min_weight} and {@code att_multiply}, all signed {@code int}.
     *
     * <p><b>Flags.</b> Each name has {@code OF_} or {@code PF_} put in front and is resolved with
     * {@code valueOf}, which like C's {@code lookup_flag} compares case-sensitively. C's lookup
     * starts at table index 1, so the {@code NONE} entry at the head of both flag tables can never
     * match; here {@code OF_NONE}, {@code OF_MAX} and {@code PF_NONE} are refused as sentinels
     * (the {@code PlayerFlag} enum has no {@code PF_MAX}, so that name falls to the unknown-flag
     * arm). Repeated {@code obj-flags:} and {@code player-flags:} lines accumulate, as in C.
     *
     * <p><b>Titles.</b> C's {@code title[]} holds ten entries, and the eleventh
     * {@code title:} line is {@code PARSE_ERROR_TOO_MANY_ENTRIES}. Ten or fewer are kept in file
     * order; fewer than ten is accepted.
     *
     * <p><b>Equipment and magic.</b> The record's equipment list, which holds the class-level and
     * the in-book {@code equip:} lines together, goes to {@link ClassEquipAssembler}; the result is
     * stored as it comes back, so its order is whatever that class documents. A {@code magic:}
     * record is wrapped in a one-element list for {@link ClassMagicAssembler}; if nothing comes
     * back the class stores {@link ClassMagic#NONE}. See {@code docs/Assembler_Issues.md} for the
     * case where every book of a caster is dropped.
     *
     * <p>A record is refused with a {@link DataFileParseException} when:
     * <ul>
     *   <li>a stat or skill name does not resolve, or a stat or skill value has no digit;</li>
     *   <li>the hit die, experience, maximum attacks, minimum weight or strength multiplier has no
     *       digit;</li>
     *   <li>an object flag or player flag is unknown or a sentinel; or</li>
     *   <li>the class has more than ten titles.</li>
     * </ul>
     * Faults inside the nested equipment and magic records are thrown by those assemblers.
     *
     * <p>Function assemble coded before 260915, commented in full on 261028 (soft errors made
     * fatal by 261028).
     *
     * @param records  the raw class records, in file order
     * @param errors   not written to by this method; it is passed on to {@link ClassMagicAssembler}
     *                 and {@link ClassEquipAssembler}
     * @param filename the class file being read, for the error messages
     * @return one {@link PlayerClass} per record, in file order
     * @throws DataFileParseException if a name, number, flag or title count is bad, as listed above
     */
    @Override
    public List<PlayerClass> assemble(@NotNull List<PlayerClassParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<PlayerClass> playerClasses = new ArrayList<>();
        List<StartItem> startItems;

        for (PlayerClassParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            Map<Stats, Integer> stats = new HashMap<>();
            for (String key : record.stats().keySet()) {
                Stats stat;
                try {
                    stat = Stats.valueOf(key);
                    int statVal = ParseUtils.parseIntString("Player class", record.stats().get(key), line, filename, "stat: " + key + " ");
                    stats.put(stat, statVal);
                } catch (IllegalArgumentException e) {
                    String msg = "Player class starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal stat name value: " + key;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Map<PlayerSkill, Integer> baseSkills = new HashMap<>();
            for (String key : record.baseSkills().keySet()) {
                PlayerSkill skill;
                try {
                    skill = PlayerSkill.valueOf(key);
                    int baseSkillVal = ParseUtils.parseIntString("Player class", record.baseSkills().get(key), line, filename, "skill: " + key + " ");
                    baseSkills.put(skill, baseSkillVal);
                } catch (IllegalArgumentException e) {
                    String msg = "Player class starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal base skill name value: " + key;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Map<PlayerSkill, Integer> extraSkills = new HashMap<>();
            for (String key : record.skillIncrements().keySet()) {
                PlayerSkill skill;
                try {
                    skill = PlayerSkill.valueOf(key);
                    int skillVal = ParseUtils.parseIntString("Player class", record.skillIncrements().get(key), line, filename, "skill: " + key + " ");
                    extraSkills.put(skill, skillVal);
                } catch (IllegalArgumentException e) {
                    String msg = "Player class starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal skill name value: " + key;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            int hpAdj = ParseUtils.parseIntString("Player class", record.hitdie(), line, filename, "hit die");
            int expAdj = ParseUtils.parseIntString("Player class", record.exp(), line, filename, "experience adjustment");
            int maxAttacks = ParseUtils.parseIntString("Player class", record.maxAttacks(), line, filename, "maximum attacks");
            int minWeight = ParseUtils.parseIntString("Player class", record.minWeight(), line, filename, "minimum weight");
            int strengthMult = ParseUtils.parseIntString("Player class", record.strengthMult(), line, filename, "strength multiplier");
            List<ClassMagic> magic = new ArrayList<>();
            if (record.magic() != null) {
                List<ClassMagicParseRecord> cmpr = new ArrayList<>();
                cmpr.add(record.magic());
                magic = new ClassMagicAssembler().assemble(cmpr, errors, filename);
            }

            ClassMagic classMagic = magic.isEmpty() ? ClassMagic.NONE : magic.getFirst();
            
            startItems = new ClassEquipAssembler().assemble(record.equipment(), errors, filename);
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            for (String oFlag : record.oFlags()) {
                String flagString = "OF_" + oFlag;
                try {
                    ObjectFlag flag = ObjectFlag.valueOf(flagString);
                    if (flag == ObjectFlag.OF_NONE || flag == ObjectFlag.OF_MAX) {
                        String msg = "Player class starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel object flag value: " + flagString;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    oFlags.on(flag);
                } catch (IllegalArgumentException e) {
                    String msg = "Player class starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal object flag value: " + flagString;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);
            for (String pFlag : record.pFlags()) {
                String flagString = "PF_" + pFlag;
                try {
                    PlayerFlag flag = PlayerFlag.valueOf(flagString);
                    if (flag == PlayerFlag.PF_NONE) {
                        String msg = "Player class starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel player flag value: " + flagString;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    pFlags.on(flag);
                } catch (IllegalArgumentException e) {
                    String msg = "Player class starting at line: " + line + " of file: " + filename + " has " +
                            "an unknown player flag value: " + pFlag;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            List<String> titles = record.titles();
            if (titles.size() > 10) {
                String msg = "Player class starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal number of titles: " + titles.size();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }

            playerClasses.add(new PlayerClass(name, titles, stats, baseSkills,
                    extraSkills, hpAdj, expAdj, oFlags, pFlags, maxAttacks,
                    minWeight, strengthMult, startItems, classMagic));
        }

        return playerClasses;
    }
}
