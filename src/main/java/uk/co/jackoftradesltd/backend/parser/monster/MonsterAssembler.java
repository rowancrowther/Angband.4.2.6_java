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

package uk.co.jackoftradesltd.backend.parser.monster;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.colour.ColourCycle;
import uk.co.jackoftradesltd.middle.combat.BlowMethod;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.*;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterGroupRole;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpellTypeEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Turns the raw {@link MonsterParseRecord}s parsed from {@code monster.txt} into fully-formed
 * {@link MonsterRace} domain objects. This is the assembler half of the monster loader: the grammar
 * collects each directive as strings, and this class resolves those strings against the game's loaded
 * registries via {@link GameConstants} — monster bases, blow methods and effects, object kinds and the
 * visuals cycler table — and layers them into a race the way the C original's {@code parse_monster_*}
 * functions do ({@code src/mon-init.c}).
 *
 * <p>Two conventions run through {@link #assemble}. Every failure stops the load: a bad
 * cross-reference, number, flag or glyph logs a message naming the record's line and the file and
 * throws a {@link DataFileParseException}, as C ends the load on the parser error its
 * {@code parse_monster_*} function returns. There is no soft error and no dropped record. And the
 * {@link MonsterBase} template is applied first, so a monster's own flags and glyph layer on top of
 * the ones it inherits from its base.
 *
 * <p>Friend and shape references to other <em>races</em> are deliberately not resolved here: those
 * names may point forward to races not yet loaded, so they are carried as strings and resolved in a
 * second pass ({@link MonsterRace#resolveFriends()} / {@link MonsterRace#resolveShapes()}) once every
 * race exists. References to <em>bases</em>, which are all loaded before monsters, are resolved inline.
 *
 * <p>The assembler sees a finished {@link MonsterParseRecord} with no line order, so it differs from
 * C, which acts on one line at a time, in four recorded ways (all in {@code docs/Accepted_Deviations.md},
 * "Monster"): the base's flags and glyph are applied first whatever the line order, a second
 * {@code base:} line replaces the first, a {@code spell-power:} line a later {@code depth:} line
 * overwrites is not checked for a minus, and a {@code friends:} name of {@code same} is resolved by
 * name. It also gives each race its own {@code color-cycle:} where C stores every cycle in slot 0.
 *
 * <p>Class MonsterAssembler coded before 260915, commented in full on 261026.
 *
 * @author Rowan Crowther
 */
public class MonsterAssembler implements Assembler<MonsterParseRecord, List<MonsterRace>> {
    /**
     * The logger every failure message is sent to, at {@code fatal}, just before the
     * {@link DataFileParseException} carrying the same text is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261026.
     */
    private static final Logger logger = LogManager.getLogger(MonsterAssembler.class);

    /**
     * Assemble each parsed record into a {@link MonsterRace}, standing in for the {@code parse_monster_*}
     * functions of {@code mon-init.c} (and the friend and shape conversion in {@code finish_parse_monster},
     * which {@link MonsterRace} finishes in its second pass). Per record, in this order:
     * <ul>
     *   <li>the {@code base:} name is looked up exactly (case-sensitively); an unknown base throws, and
     *       an absent one leaves the base {@code null}, as C does. The base's flags seed the flag set.</li>
     *   <li>{@code hit-points:}, {@code armor-class:}, {@code sleepiness:}, {@code hearing:},
     *       {@code smell:}, {@code speed:}, {@code light:}, {@code experience:}, {@code depth:} and
     *       {@code rarity:} are read as C's {@code int} fields, so an absent line is 0. Hearing and smell
     *       are scaled by {@code max_sight / 20} in integer arithmetic.</li>
     *   <li>{@code innate-freq:} and {@code spell-freq:} must be 1 to 100 and are stored as
     *       {@code 100 / value}. An absent line stays 0 unless the race has an innate spell (or a
     *       spell that is neither innate nor a breath), which makes it 4, as {@code parse_monster_spells}
     *       does.</li>
     *   <li>spell power is the {@code depth:} text unless a later {@code spell-power:} line replaced it.
     *       Only {@code spell-power:} is a {@code uint}, so only it refuses a leading minus; a negative
     *       {@code depth:} loads with the same negative spell power.</li>
     *   <li>{@code flags:} adds and {@code flags-off:} removes race flags, then {@code spells:} adds spell
     *       flags. {@code NONE} is refused for all three, as C's flag lookup starts after it. A race
     *       flag called {@code MAX} does not exist; a spell called {@code MAX} does and is accepted.</li>
     *   <li>each {@code blow:} needs a known method. An omitted effect is the {@code NONE} effect and an
     *       omitted damage is a {@code 0d0} die.</li>
     *   <li>the colour is dark when there is no {@code color:} line and white when its letter is unknown.
     *       The glyph is the {@code glyph:} line, else the base's, and must be exactly one code point.</li>
     *   <li>{@code message-vis:}, {@code message-invis:} and {@code message-miss:} name a spell by its
     *       {@code RSF_} name; {@code NONE} and {@code MAX} are both accepted, as C's {@code grab_name}
     *       scans every row. A message of only spaces and tabs becomes {@code ""}.</li>
     *   <li>{@code drop:} needs a known tval and kind, {@code drop-base:} a known tval only. The chance,
     *       minimum and maximum refuse a leading minus and the minimum and maximum must be 0 to 99.</li>
     *   <li>{@code friends:} and {@code friends-base:} refuse a leading minus on the chance, need a die,
     *       and take a role of exactly {@code servant} or {@code bodyguard}; a {@code friends-base:}
     *       name must be a known base. {@code mimic:} needs a known tval and kind.</li>
     *   <li>{@code shape:} names are carried unresolved, and a {@code color-cycle:} that names no known
     *       group or cycle is silently left {@code null}, as {@code visuals_cycler_set_cycle_for_race}
     *       leaves it.</li>
     * </ul>
     * The drop, friend, friend-base, mimic, shape and message lists keep the order the grammar built,
     * which is C's head-pushed (reverse file) order. Every failure logs and throws a
     * {@link DataFileParseException}.
     *
     * <p>Outstanding: {@code errors} is not used.
     *
     * <p>Function assemble coded before 260915, commented in full on 261026.
     *
     * @param records  the parsed monster records, in file order
     * @param errors   not used: no failure is collected, each one throws
     * @param filename the file the records were read from, named in every failure message
     * @return one {@link MonsterRace} per record, in file order
     * @throws DataFileParseException on the first record with a value C's parser would refuse
     */
    @Override
    public List<MonsterRace> assemble(@NotNull List<MonsterParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<MonsterRace> monsters = new ArrayList<>();

        for (MonsterParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            String text = record.desc().isEmpty() ? null : record.desc();
            String plural = record.plural();
            String baseType = record.base();
            MonsterBase base;
            if (!baseType.isEmpty()) {
                base = MonsterRegistry.lookupMonsterBase(baseType);
                if (base == null) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal base value: " + baseType;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            } else {
                base = null;
            }
            // Seed the flag set from the base template; the monster's own flags: and flags-off: lines
            // below then add to and subtract from this inherited set (mirroring C's rf_union followed
            // by grab_flag/remove_flag in parse_monster_base / _flags / _flags_off).
            // ACCEPTED DEVIATION (2026-10-26, docs/Accepted_Deviations.md, "Monster"): C does this
            // union when it reads the base: line, so a flags-off: line that comes BEFORE base: removes
            // nothing and the base then adds the flag back. This assembler sees the finished record
            // with no line order, so the base is always applied first and flags-off: always wins.
            // Shipped monster.txt puts base: before every flags: and flags-off: line.
            Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
            if (base != null)
                flags.union(base.getFlags());
            int hitPoints = ParseUtils.parseIntString("Monster", record.hitPoints(), line, filename, "hit point");
            int ac = ParseUtils.parseIntString("Monster", record.armourClass(), line, filename, "armour");
            int sleep = ParseUtils.parseIntString("Monster", record.sleepiness(), line, filename, "sleep");
            int hearing = ParseUtils.parseIntString("Monster", record.hearing(), line, filename, "hearing");
            hearing = hearing * GameConstants.getPlayerMaxSight() / 20;
            int smell = ParseUtils.parseIntString("Monster", record.smell(), line, filename, "smell");
            smell = smell * GameConstants.getPlayerMaxSight() / 20;
            int speed = ParseUtils.parseIntString("Monster", record.speed(), line, filename, "speed");
            int light = ParseUtils.parseIntString("Monster", record.light(), line, filename, "light");
            int mexp = ParseUtils.parseIntString("Monster", record.experience(), line, filename, "experience");
            int innatePct = 0;
            if (!record.innateFreq().isEmpty()) {
                int freqInnate = ParseUtils.parseIntString("Monster", record.innateFreq(), line, filename, "innate frequency");
                if (freqInnate < 1 || freqInnate > 100) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal out of range innate frequency value: " + record.innateFreq();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                innatePct = 100 / freqInnate;
            }
            int spellPct = 0;
            if (!record.spellFreq().isEmpty()) {
                int freqSpell = ParseUtils.parseIntString("Monster", record.spellFreq(), line, filename, "spell frequency");
                if (freqSpell < 1 || freqSpell > 100) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal out of range spell frequency value: " + record.spellFreq();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                spellPct = 100 / freqSpell;
            }
            // C seeds spell_power from depth: and lets a later spell-power: line override it, and only
            // spell-power: is a uint that refuses a leading '-' (a depth: of -5 loads, with spell power
            // -5). The grammar keeps the last of the two lines' text and tags which one it was.
            // ACCEPTED DEVIATION (2026-10-26, docs/Accepted_Deviations.md, "Monster"): the sign test
            // looks only at the winning line, so "spell-power:-1" followed by "depth:20" loads here,
            // where C refused the spell-power: line as it read it.
            int spellPower;
            if (record.spellPowerOrDepth().equals("spell") && ParseUtils.minusFound(record.spellPower())) {
                String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal spell power value: " + record.spellPower();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            } else {
                spellPower = ParseUtils.parseIntString("Monster", record.spellPower(), line, filename, "depth");
            }
            for (String flagString : record.flagsOn()) {
                try {
                    MonsterRaceFlag flag = MonsterRaceFlag.valueOf("RF_" + flagString);
                    if (flag == MonsterRaceFlag.RF_NONE) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel monster race flag value: " + flagString;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    flags.on(MonsterRaceFlag.valueOf("RF_" + flagString));
                } catch (IllegalArgumentException e) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal flag on value: " + flagString;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            for (String flagString : record.flagsOff()) {
                try {
                    MonsterRaceFlag flag = MonsterRaceFlag.valueOf("RF_" + flagString);
                    if (flag == MonsterRaceFlag.RF_NONE) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel monster race flag value: " + flagString;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    flags.off(flag);
                } catch (IllegalArgumentException e) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal flag off value: " + flagString;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            Flag<MonsterSpell> spells = new Flag<>(MonsterSpell.class);
            for (String spellString : record.spells()) {
                try {
                    MonsterSpell spellFlag = MonsterSpell.valueOf("RSF_" + spellString);
                    if (spellFlag == MonsterSpell.RSF_NONE) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel monster spell value: " + spellString;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);                        
                    }
                    spells.on(spellFlag);
                } catch (IllegalArgumentException e) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal spell value: " + spellString;
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            List<MonsterBlow> blows = new ArrayList<>();
            for (MonsterParseRecord.MonsterBlowParseRecord blow : record.blows()) {
                BlowEffect effect;
                Random blowDamage;
                BlowMethod method = MonsterRegistry.lookupBlowMethod(blow.method());
                if (method == null) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal blow method value: " + blow.method();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                // Effect and damage are both optional. An omitted effect resolves to the "NONE"
                // effect and an omitted damage to a 0d0 Random, so every assembled blow ends up with a
                // non-null effect and dice -- matching C's findeff("NONE") and its zeroed dice.
                if (!blow.effect().isEmpty()) {
                    effect = MonsterRegistry.lookupBlowEffect(blow.effect());
                    if (effect == null) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal blow effect value: " + blow.effect();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    if (!blow.damage().isEmpty()) {
                        blowDamage = Random.parseStr(blow.damage());
                        if (blowDamage == null) {
                            String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal blow damage value: " + blow.damage();
                            logger.fatal(msg);
                            throw new DataFileParseException(msg);
                        }
                    } else {
                        blowDamage = new Random(0, 0, 0, 0, false);
                    }
                } else {
                    effect = MonsterRegistry.lookupBlowEffect("NONE");
                    blowDamage = new Random(0, 0, 0, 0, false);
                }
                blows.add(new MonsterBlow(method, effect, blowDamage));
            }
            int level = ParseUtils.parseIntString("Monster", record.depth(), line, filename, "depth");
            int rarity = ParseUtils.parseIntString("Monster", record.rarity(), line, filename, "rarity");
            // Glyph is optional: an explicit glyph: line overrides, otherwise it is inherited from the
            // base (most monsters carry no glyph: line). A leftover '\0' means neither supplied one.
            // ACCEPTED DEVIATION (2026-10-26, docs/Accepted_Deviations.md, "Monster"): C copies the
            // base's glyph when it reads base:, so a glyph: line BEFORE base: is overwritten by the
            // base's. Here an explicit glyph: always wins, whatever the line order.
            String glyphStr = record.glyph();
            char glyph = '\0';
            int wideGlyph = 0;
            AngbandDisplayCharacter adc = null;
            ColourEnum colour;
            if (record.colour().isEmpty()) {
                colour = ColourEnum.COLOUR_DARK;
            } else {
                colour = ColourEnum.fromCode(record.colour());
                if (colour == null) {
                    colour = ColourEnum.COLOUR_WHITE;
                }
            }
            if (!glyphStr.isEmpty()) {
                if (glyphStr.codePointCount(0, glyphStr.length()) != 1) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal glyph value: " + glyphStr;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                if (Character.isSupplementaryCodePoint(glyphStr.codePointAt(0))) {
                    wideGlyph = glyphStr.codePointAt(0);
                } else {
                    glyph = glyphStr.charAt(0);
                }
            } else if (base != null) {
                glyph = base.getDefaultMonsterChar();
            }
            if (glyph != '\0') {
                adc = new AngbandDisplayCharacter(glyph, colour);
            } else {
                adc = new AngbandDisplayCharacter(wideGlyph, colour);
            }
            // Per-record message map keyed by spell name -- one MonsterSpellMessage per spell the
            // monster casts, holding its optional vis/invis/miss lines. Built fresh each record so
            // messages never leak between monsters.
            List<MonsterSpellText> spellMessages = new ArrayList<>();
            for (MonsterParseRecord.MessageTypeParseRecord messageType : record.spellMessages()) {
                try {
                    String visInvisMiss = (messageType.messageType() == null || ParseUtils.onlySpaceAndTab(messageType.messageType())) ? "" : messageType.messageType();
                    String spellName = (messageType.spellName() == null || ParseUtils.onlySpaceAndTab(messageType.spellName())) ? "" : messageType.spellName();
                    String messageString = (messageType.messageString() == null || ParseUtils.onlySpaceAndTab(messageType.messageString())) ? "" : messageType.messageString();
                    
                    MonsterSpell damageType = MonsterSpell.valueOf("RSF_" + spellName);
                    MonsterSpellText.MonsterSpellMessageType visType = 
                            MonsterSpellText.MonsterSpellMessageType.valueOf(visInvisMiss.toUpperCase());
                    
                    MonsterSpellText monMessage = new MonsterSpellText(visType, damageType, messageString);
                    spellMessages.add(monMessage);
                } catch (IllegalArgumentException e) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal spell message: message-" + messageType.messageType() + ":"
                            + messageType.spellName() + ":" + messageType.messageString();
                    logger.fatal(msg, e);
                    throw new DataFileParseException(msg);
                }
            }
            // drop: and drop-base: both feed this single list. A drop: resolves to a specific
            // ObjectKind; a drop-base: (the loop further down) leaves the kind null, meaning "any
            // object of this tval" -- the two are told apart later by whether the kind is null.
            List<MonsterDrop> drops = new ArrayList<>();
            for (MonsterParseRecord.MonsterDropParseRecord drop : record.drops()) {
                String dropTvalStr = drop.type();
                String dropName = drop.name();
                String chance = drop.chance();
                String minStr = drop.min();
                String maxStr = drop.max();
                if (!chance.isEmpty() && !minStr.isEmpty() && !maxStr.isEmpty()) {
                    TValue tValue = TValue.fromName(dropTvalStr);
                    if (tValue == null) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal drop type value: " + dropTvalStr;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    ObjectKind kind;
                    if (dropName.isEmpty())
                        kind = null;
                    else
                        kind = ObjectRegistry.lookupObjectKind(tValue, dropName);
                    if (kind == null && !dropName.isEmpty()) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal drop base value: " + dropName;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    if (ParseUtils.minusFound(chance)) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal percentage value: " + chance;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    int percentage = ParseUtils.parseIntString("Monster", chance, line, filename, "percentage");
                    if (ParseUtils.minusFound(minStr)) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal minimum value: " + minStr;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    int min = ParseUtils.parseIntString("Monster", minStr, line, filename, "minimum");
                    if (ParseUtils.minusFound(maxStr)) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal maximum value: " + maxStr;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    int max = ParseUtils.parseIntString("Monster", maxStr, line, filename, "maximum");
                    if (percentage < 0) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "a negative percentage value: " + chance;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    if (min < 0 || min > 99) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an out of bounds minimum value: " + minStr;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    if (max < 0 || max > 99) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an out of bounds maximum value: " + maxStr;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    if (kind != null)
                        drops.add(new MonsterDrop(tValue, kind, percentage, min, max));
                    else
                        drops.add(new MonsterDrop(tValue, percentage, min, max));
                } else {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal drops value: " + drop.type() + ":" + drop.name() + ":" + drop.chance() 
                            + ":" + drop.min() + ":" + drop.max();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            List<MonsterFriends> friends = new ArrayList<>();
            for (MonsterParseRecord.MonsterFriendsParseRecord friend : record.friends()) {
                String chanceStr = friend.chance();
                String numberStr = friend.number();
                String nameStr = friend.name();
                // A "same" friend (any case) refers to this race. Substitute its actual name now, so
                // the second-pass lookup in resolveFriends treats it like any other race reference.
                if (nameStr.equalsIgnoreCase("same"))
                    nameStr = name;
                String roleStr = friend.role();
                if (ParseUtils.minusFound(chanceStr)) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal chance value: " + chanceStr;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                int chance = ParseUtils.parseIntString("Monster", chanceStr, line, filename, "friends chance");
                if (chance < 0) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal chance value: " + chanceStr;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                Random numberDie = null;
                if (!numberStr.isEmpty()) {
                    numberDie = Random.parseStr(numberStr);
                    if (numberDie == null) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal friend die value: " + numberStr;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
                MonsterGroupRole role = MonsterGroupRole.MON_GROUP_MEMBER;
                if (!roleStr.isEmpty()) {
                    if (roleStr.equals("servant"))
                        role = MonsterGroupRole.MON_GROUP_SERVANT;
                    else if (roleStr.equals("bodyguard"))
                        role = MonsterGroupRole.MON_GROUP_BODYGUARD;
                    else {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal friends role value: " + roleStr;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
                if (numberDie != null) {
                    int dice = numberDie.getDice();
                    int sides = numberDie.getSides();
                    friends.add(new MonsterFriends(nameStr, role, chance, dice, sides));
                } else {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal friends die value: " + numberStr;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            List<MonsterFriendsBase> friendsBase = new ArrayList<>();
            for (MonsterParseRecord.MonsterFriendsParseRecord friend : record.friendsBase()) {
                String chanceStr = friend.chance();
                String numberStr = friend.number();
                String baseKind = friend.name();
                String roleStr = friend.role();
                if (ParseUtils.minusFound(friend.chance())) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal chance value: " + friend.chance();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                int chance = ParseUtils.parseIntString("Monster", friend.chance(), line, filename, "chance");
                if (chance < 0) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal chance value: " + chanceStr;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                MonsterBase monsterBase = MonsterRegistry.lookupMonsterBase(baseKind);
                if (monsterBase == null) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal friends base value: " + baseKind;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                Random numberDie = null;
                if (!numberStr.isEmpty()) {
                    numberDie = Random.parseStr(numberStr);
                    if (numberDie == null) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal friends base die value: " + numberStr;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
                MonsterGroupRole role = MonsterGroupRole.MON_GROUP_MEMBER;
                if (!roleStr.isEmpty()) {
                    if (roleStr.equals("servant"))
                        role = MonsterGroupRole.MON_GROUP_SERVANT;
                    else if (roleStr.equals("bodyguard"))
                        role = MonsterGroupRole.MON_GROUP_BODYGUARD;
                    else {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal friends role value: " + roleStr;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
                if (numberDie == null) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal number die value: " + numberStr;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else {
                    friendsBase.add(new MonsterFriendsBase(monsterBase, role, chance, numberDie.getDice(),
                            numberDie.getSides()));
                }
            }
            List<ObjectKind> mimics = new ArrayList<>();
            for (MonsterParseRecord.MonsterMimicParseRecord mimic : record.mimics()) {
                ObjectKind mimicKind;
                String tVal = mimic.tVal();
                String sVal = mimic.sVal();
                TValue tValue = TValue.fromName(tVal);
                if (tValue == null) {
                    String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal mimic value: " + tVal;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                } else {
                    mimicKind = ObjectRegistry.lookupObjectKind(tValue, sVal);
                    if (mimicKind == null) {
                        String msg = "Monster starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal mimic value: " + sVal;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
                mimics.add(mimicKind);
            }
            // Shape names are kept as bare strings and resolved (to a base or a race) in the second
            // pass, since a shape may name a race defined later in the file.
            List<MonsterShape> shapes = new ArrayList<>();
            for (String shapeBase : record.shape()) {
                shapes.add(new MonsterShape(shapeBase));
            }
            // ACCEPTED DEVIATION (2026-10-26, docs/Accepted_Deviations.md, "Monster"): C stores the
            // cycle at race->ridx while the race is still being parsed, when ridx is 0 for every race
            // (finish_parse_monster sets it later), so all nine color-cycle: lines land in slot 0 and
            // no real monster gets one. Here each race keeps the cycle its own line names.
            ColourCycle visualsCycler = null;
            String cyclerGroup = record.colourCycleGroup();
            String colourCycleName = record.colourCycleName();
            if (!colourCycleName.isEmpty() && !cyclerGroup.isEmpty()) {
                Map<String, ColourCycle> group = MonsterRegistry.getVisualsCyclerTable().getByGroup(cyclerGroup);
                if (group != null) {
                    visualsCycler = group.get(colourCycleName);
                }
            }
            
            boolean hasSpell = false;
            boolean hasInnate = false;
            
            for (MonsterSpell monsterSpell : spells) {
                if (monsterSpell.getTypes().has(MonsterSpellTypeEnum.RST_INNATE))
                    hasInnate = true;
                if (!monsterSpell.getTypes().has(MonsterSpellTypeEnum.RST_INNATE) 
                        && !monsterSpell.getTypes().has(MonsterSpellTypeEnum.RST_BREATH))
                    hasSpell = true;
            }
            
            if (innatePct == 0 && hasInnate) innatePct = 4;
            if (spellPct == 0 && hasSpell) spellPct = 4;

            monsters.add(new MonsterRace(name, text, plural, base, hitPoints, ac, sleep,
                    hearing, smell, speed, light, mexp, innatePct, spellPct,
                    spellPower, flags, spells, blows, level, rarity, adc, spellMessages,
                    drops, friends, friendsBase, mimics, shapes, shapes.size(), visualsCycler));
        }

        return monsters;
    }
}
