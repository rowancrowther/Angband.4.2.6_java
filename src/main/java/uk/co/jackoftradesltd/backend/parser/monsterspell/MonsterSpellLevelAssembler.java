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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.monsters.MonsterSpellLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the per-power blocks of a monster spell into domain {@link MonsterSpellLevel} objects.
 * <p>
 * A "level" is one tier of lore and messages that applies from a given spell power upwards. In
 * {@code mon-init.c} the C original builds the chain in three steps. {@code parse_mon_spell_name()}
 * allocates a zeroed level (power 0, no text, every colour 0, which is {@code COLOUR_DARK}) the
 * moment it reads a {@code name:} line. {@code parse_mon_spell_power_cutoff()} appends a further
 * zeroed level carrying the power. Every {@code lore:}, {@code lore-color-*:} and
 * {@code message-*:} handler then works on the <em>last</em> level in the chain, so a spell always
 * has one level more than it has {@code power-cutoff:} lines.
 * <p>
 * The text directives append ({@code string_append()}) and the colour directives overwrite, which
 * is why a power-less record in the middle of the list is merged into the level before it rather
 * than opening a new one. The grammar produces such a record when {@code lore:} or
 * {@code message-*:} lines come back after an {@code effect:}, {@code hit:} or {@code msgt:}
 * line, and its {@code power} is null. At the head of the list the same null {@code power} is
 * the implicit level 0; a first record that does carry a {@code power-cutoff:} gets a blank level
 * 0 inserted in front of it.
 * <p>
 * Colours: a level with no {@code lore-color-*} directive keeps {@link ColourEnum#COLOUR_DARK},
 * C's zeroed {@code lore_attr}. A directive that names no colour reads as
 * {@link ColourEnum#COLOUR_WHITE}, because {@code color_text_to_attr()} and
 * {@code color_char_to_attr()} in {@code z-color.c} both default to white and the C handlers
 * only test for a negative result, which neither ever returns for a name or a printable
 * character. Absent text is {@code null}, as C's unset {@code char *}.
 * <p>
 * The only fatal path is an unreadable {@code power-cutoff:}, reported by
 * {@link ParseUtils#parseIntString} (a {@code strtol}-style reading that clamps an oversize
 * number where C wraps, as its own Javadoc records). Nothing is written to the {@code errors}
 * list, and the result is never {@code null}.
 *
 * <p>Class MonsterSpellLevelAssembler coded before 260915, commented in full on 261027.
 *
 * @author Rowan Crowther
 */
public class MonsterSpellLevelAssembler implements Assembler<MonsterSpellParseRecord.MonsterSpellLevelParseRecord, List<MonsterSpellLevel>> {    
    /**
     * Assemble every power tier of one monster spell, in file order, as the chain of
     * {@code struct monster_spell_level} that the {@code parse_mon_spell_*} handlers in
     * {@code mon-init.c} build. Each record takes one of four routes:
     * <ul>
     * <li>first record, no power: it is the implicit level 0 and is added as it stands;
     * <li>first record, with a power: a blank level 0 (power 0, no text, every colour
     * {@code COLOUR_DARK}) is added first, then the record as the next level;
     * <li>later record, no power: its text is appended to the last level's text, and any colour
     * it names replaces the last level's, as C's handlers work on the last level of the chain;
     * <li>later record, with a power: added as a new level, with no check that the powers rise.
     * </ul>
     * A spell with no records at all (nothing but {@code name:}, {@code hit:}, {@code msgt:} and
     * effects) still gets the single blank level 0, because C allocates it at {@code name:} time.
     * The result therefore always holds at least one level, and exactly one more than the number
     * of {@code power-cutoff:} lines.
     * <p>
     * An empty text field becomes {@code null} and an absent colour is {@code COLOUR_DARK}. A
     * merged level whose combined text is empty is {@code null} too.
     * <p>
     * The result is never {@code null}: a {@code power-cutoff:} that cannot be read throws
     * {@link uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException} from
     * {@link ParseUtils#parseIntString}, which stops the load as C's parse error does. This
     * once returned {@code null} to ask the caller to drop the spell, and the caller no longer
     * needs to test for it.
     *
     * <p>Function assemble coded before 260915, commented in full on 261027.
     *
     * @param records  the parsed level blocks for a single spell, possibly empty
     * @param errors   unused: nothing here is a soft error any longer
     * @param filename the file being read, for the message of a bad {@code power-cutoff:}
     * @return the assembled levels in file order, never {@code null} and never empty
     * @throws uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException if a
     *         {@code power-cutoff:} value has no digits
     */
    @Override
    public List<MonsterSpellLevel> assemble(@NotNull List<MonsterSpellParseRecord.MonsterSpellLevelParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<MonsterSpellLevel> monsterSpellLevels = new ArrayList<>();
        
        int recordIndex = -1;
        boolean hasPower;

        for (MonsterSpellParseRecord.MonsterSpellLevelParseRecord record : records) {
            hasPower = false;
            int line = record.pcLine();
            int powerLevel;
            if (record.power() == null)
                powerLevel = 0;
            else {
                hasPower = true;
                powerLevel = ParseUtils.parseIntString("Monster spell level", record.power(), line, filename, "power cutoff");
            }
            String lore = record.loreDesc().isEmpty() ? null : record.loreDesc();
            ColourEnum baseAttr = null;
            if (record.loreBaseColour() != null) {
                baseAttr = ColourEnum.fromCode(record.loreBaseColour());
                if (baseAttr == null)
                    baseAttr = ColourEnum.COLOUR_WHITE;
            }
            ColourEnum base = baseAttr == null ? ColourEnum.COLOUR_DARK : baseAttr;
            ColourEnum resistAttr = null;
            if (record.loreResistColour() != null) {
                resistAttr = ColourEnum.fromCode(record.loreResistColour());
                if (resistAttr == null)
                    resistAttr = ColourEnum.COLOUR_WHITE;
            }
            ColourEnum resist = resistAttr == null ? ColourEnum.COLOUR_DARK : resistAttr;
            ColourEnum immuneAttr = null;
            if (record.loreImmuneColour() != null) {
                immuneAttr = ColourEnum.fromCode(record.loreImmuneColour());
                if (immuneAttr == null)
                    immuneAttr = ColourEnum.COLOUR_WHITE;
            }
            ColourEnum immune = immuneAttr == null ? ColourEnum.COLOUR_DARK : immuneAttr;
            String msgSave = record.saveMessage().isEmpty() ? null : record.saveMessage();
            String msgVis = record.visMessage().isEmpty() ? null : record.visMessage();
            String msgInvis = record.invisMessage().isEmpty() ? null : record.invisMessage();
            String msgMiss = record.missMessage().isEmpty() ? null : record.missMessage();

            recordIndex++;
            
            if (recordIndex == 0 && !hasPower) {
                monsterSpellLevels.add(new MonsterSpellLevel(powerLevel, lore, base, resist,
                        immune, msgVis, msgInvis, msgMiss, msgSave));
            } else if (recordIndex == 0) { // hasPower
                ColourEnum attr = ColourEnum.COLOUR_DARK;
                monsterSpellLevels.add(new MonsterSpellLevel(0, null, attr, attr, attr,
                        null, null, null, null));
                recordIndex++;
                monsterSpellLevels.add(new MonsterSpellLevel(powerLevel, lore, base, resist, immune, msgVis,
                        msgInvis, msgMiss, msgSave));
            } else if (recordIndex >0 && !hasPower) {
                MonsterSpellLevel level = monsterSpellLevels.getLast();

                int newPower = level.getPower();
                String lorePrefix = level.getLoreDesc() == null ? "" : level.getLoreDesc();
                String loreSuffix = lore == null ? "" : lore;
                String newLore = lorePrefix + loreSuffix;
                if (newLore.isEmpty()) newLore = null;
                
                ColourEnum newBase = level.getLoreAttr();
                if (baseAttr != null)
                    newBase = baseAttr;
                
                ColourEnum newResist = level.getLoreAttrResist();
                if (resistAttr != null)
                    newResist = resistAttr;
                
                ColourEnum newImmune = level.getLoreAttrImmune();
                if (immuneAttr != null)
                    newImmune = immuneAttr;
                
                String visPrefix = level.getMessage() == null ? "" : level.getMessage();
                String visSuffix = msgVis == null ? "" : msgVis;
                String newVis = visPrefix + visSuffix;
                if (newVis.isEmpty()) newVis = null;
                String invisPrefix = level.getBlindMessage() == null ? "" : level.getBlindMessage();
                String invisSuffix = msgInvis == null ? "" : msgInvis;
                String newInvis = invisPrefix + invisSuffix;
                if (newInvis.isEmpty()) newInvis = null;
                String missPrefix = level.getMissMessage() == null ? "" : level.getMissMessage();
                String missSuffix = msgMiss == null ? "" : msgMiss;
                String newMiss = missPrefix + missSuffix;
                if (newMiss.isEmpty()) newMiss = null;
                String savePrefix = level.getSaveMessage() == null ? "" : level.getSaveMessage();
                String saveSuffix = msgSave == null ? "" : msgSave;
                String newSave = savePrefix + saveSuffix;
                if (newSave.isEmpty()) newSave = null;

                monsterSpellLevels.removeLast();
                monsterSpellLevels.add(new MonsterSpellLevel(newPower, newLore, newBase, newResist, newImmune,
                        newVis, newInvis, newMiss, newSave));
            } else {
                monsterSpellLevels.add(new MonsterSpellLevel(powerLevel, lore, base,
                        resist, immune, msgVis, msgInvis, msgMiss, msgSave));

            }
        }
        
        if (records.isEmpty()) {
            ColourEnum attr = ColourEnum.COLOUR_DARK;
            monsterSpellLevels.add(new MonsterSpellLevel(0, null, attr, attr,
                    attr, null, null, null, null));
        }

        return monsterSpellLevels;
    }
}
