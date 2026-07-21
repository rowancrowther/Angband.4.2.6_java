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

package uk.co.jackoftrades.backend.parser.monster;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.ColourCycle;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.combat.BlowMethod;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.*;
import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
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
 * <p>Two conventions run through {@link #assemble}. Every field is resolved defensively: a bad
 * cross-reference or number appends a human-readable message to the shared {@code errors} list and
 * drops just that record (via {@code continue}), rather than failing the whole file. And the
 * {@link MonsterBase} template is applied first, so a monster's own flags and glyph layer on top of
 * the ones it inherits from its base.
 *
 * <p>Friend and shape references to other <em>races</em> are deliberately not resolved here: those
 * names may point forward to races not yet loaded, so they are carried as strings and resolved in a
 * second pass ({@link MonsterRace#resolveFriends()} / {@link MonsterRace#resolveShapes()}) once every
 * race exists. References to <em>bases</em>, which are all loaded before monsters, are resolved inline.
 *
 * @author Rowan Crowther
 */
public class MonsterAssembler implements Assembler<MonsterParseRecord, List<MonsterRace>> {
    /**
     * Assemble each parsed record into a {@link MonsterRace}, resolving its cross-references and
     * layering its base template. A record whose base, a cross-reference, or a numeric field fails to
     * resolve is reported to {@code errors} and skipped, so the returned list holds only the races
     * that assembled cleanly.
     *
     * @param records the parsed monster records, in file order
     * @param errors  collects one message per soft failure; the caller decides whether any is fatal
     * @return the successfully assembled races, in file order
     * @author Rowan Crowther
     */
    @Override
    public List<MonsterRace> assemble(@NotNull List<MonsterParseRecord> records, @NotNull List<String> errors) {
        List<MonsterRace> monsters = new ArrayList<>();

        for (MonsterParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            String text = record.desc();
            String plural = record.plural();
            String baseType = record.base();
            MonsterBase base = GameConstants.lookupMonsterBase(baseType);
            if (base == null) {
                errors.add("Monster at line: " + line + " has " +
                        "an invalid monster-base: " + baseType);
                continue;
            }
            // Seed the flag set from the base template; the monster's own flags: and flags-off: lines
            // below then add to and subtract from this inherited set (mirroring C's rf_union followed
            // by grab_flag/remove_flag in parse_monster_base / _flags / _flags_off).
            Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
            flags.union(base.getFlags());
            int hitPoints = 0;
            if (!record.hitPoints().isEmpty()) {
                try {
                    hitPoints = Integer.parseInt(record.hitPoints());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for hit-points: " + record.hitPoints());
                    continue;
                }
            }
            int ac = 0;
            if (!record.armourClass().isEmpty()) {
                try {
                    ac = Integer.parseInt(record.armourClass());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for armour class: " + record.armourClass());
                    continue;
                }
            }
            int sleep = 0;
            if (!record.sleepiness().isEmpty()) {
                try {
                    sleep = Integer.parseInt(record.sleepiness());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for sleepiness: " + record.sleepiness());
                    continue;
                }
            }
            int hearing = 0;
            if (!record.hearing().isEmpty()) {
                try {
                    hearing = Integer.parseInt(record.hearing());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for hearing: " + record.hearing());
                    continue;
                }
            }
            int smell = 0;
            if (!record.smell().isEmpty()) {
                try {
                    smell = Integer.parseInt(record.smell());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for smell: " + record.smell());
                    continue;
                }
            }
            int speed = 0;
            if (!record.speed().isEmpty()) {
                try {
                    speed = Integer.parseInt(record.speed());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for speed: " + record.speed());
                    continue;
                }
            }
            int light = 0;
            if (!record.light().isEmpty()) {
                try {
                    light = Integer.parseInt(record.light());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for light: " + record.light());
                    continue;
                }
            }
            int mexp = 0;
            if (!record.experience().isEmpty()) {
                try {
                    mexp = Integer.parseInt(record.experience());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for experience: " + record.experience());
                    continue;
                }
            }
            int freqInnate = 0;
            if (!record.innateFreq().isEmpty()) {
                try {
                    freqInnate = Integer.parseInt(record.innateFreq());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for innate-freq: " + record.innateFreq());
                    continue;
                }
            }
            int freqSpell = 0;
            if (!record.spellFreq().isEmpty()) {
                try {
                    freqSpell = Integer.parseInt(record.spellFreq());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for spell-freq: " + record.spellFreq());
                    continue;
                }
            }
            int spellPower = 0;
            if (!record.spellPower().isEmpty()) {
                try {
                    spellPower = Integer.parseInt(record.spellPower());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer for spell-power: " + record.spellPower());
                    continue;
                }
            }
            boolean illegalFlagOn = false;
            for (String flagString : record.flagsOn()) {
                try {
                    flags.on(MonsterRaceFlag.valueOf("RF_" + flagString));
                } catch (IllegalArgumentException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "an unknown flag name: " + flagString);
                    illegalFlagOn = true;
                }
            }
            if (illegalFlagOn) continue;
            boolean illegalFlagOff = false;
            for (String flagString : record.flagsOff()) {
                try {
                    flags.off(MonsterRaceFlag.valueOf("RF_" + flagString));
                } catch (IllegalArgumentException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "an unknown flag name: " + flagString);
                    illegalFlagOff = true;
                }
            }
            if (illegalFlagOff) continue;
            Flag<MonsterSpell> spells = new Flag<>(MonsterSpell.class);
            boolean illegalSpell = false;
            for (String spellString : record.spells()) {
                try {
                    spells.on(MonsterSpell.valueOf("RSF_" + spellString));
                } catch (IllegalArgumentException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "an unknown spell: " + spellString);
                    illegalSpell = true;
                }
            }
            if (illegalSpell) continue;
            boolean illegalBlows = false;
            List<MonsterBlow> blows = new ArrayList<>();
            for (MonsterParseRecord.MonsterBlowParseRecord blow : record.blows()) {
                BlowEffect effect;
                Random blowDamage;
                BlowMethod method = GameConstants.lookupBlowMethod(blow.method());
                if (method == null) {
                    errors.add("Monster at line: " + line + " has " +
                            "an unknown blow method: " + blow.method());
                    illegalBlows = true;
                }
                // Effect and damage are both optional. An omitted effect resolves to the "NONE"
                // effect and an omitted damage to a 0d0 Random, so every assembled blow ends up with a
                // non-null effect and dice -- matching C's findeff("NONE") and its zeroed dice.
                if (!blow.effect().isEmpty()) {
                    effect = GameConstants.lookupBlowEffect(blow.effect());
                    if (effect == null) {
                        errors.add("Monster at line: " + line + " has " +
                                "an unknown blow effect: " + blow.effect());
                        illegalBlows = true;
                    }
                    if (!blow.damage().isEmpty()) {
                        blowDamage = Random.parseStr(blow.damage());
                        if (blowDamage == null) {
                            errors.add("Monster at line: " + line + " has " +
                                    "a malformed damage dice expression: " + blow.damage());
                            illegalBlows = true;
                        }
                    } else {
                        blowDamage = new Random(0, 0, 0, 0, false);
                    }
                } else {
                    effect = GameConstants.lookupBlowEffect("NONE");
                    blowDamage = new Random(0, 0, 0, 0, false);
                }
                if (!illegalBlows) {
                    blows.add(new MonsterBlow(method, effect, blowDamage));
                }
            }
            if (illegalBlows) continue;
            int level = 0;
            if (!record.depth().isEmpty()) {
                try {
                    level = Integer.parseInt(record.depth());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed depth integer: " + record.depth());
                    continue;
                }
            }
            int rarity = 0;
            if (!record.rarity().isEmpty()) {
                try {
                    rarity = Integer.parseInt(record.rarity());
                } catch (NumberFormatException e) {
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed rarity integer: " + record.rarity());
                    continue;
                }
            }
            // Glyph is optional: an explicit glyph: line overrides, otherwise it is inherited from the
            // base (most monsters carry no glyph: line). A leftover '\0' means neither supplied one.
            String glyphStr = record.glyph();
            char glyph;
            if (!glyphStr.isEmpty()) {
                if (glyphStr.length() != 1) {
                    errors.add("Monster at line: " + line + " has " +
                            "an illegal glyph character: " + glyphStr);
                    continue;
                }
                glyph = glyphStr.charAt(0);
            } else {
                glyph = base.getDefaultMonsterChar();
            }
            ColourType colour = ColourType.getColourType(record.colour());
            if (colour == null) {
                errors.add("Monster at line: " + line + " has " +
                        "an unknown colour type: " + record.colour());
                continue;
            }
            AngbandDisplayCharacter adc = null;
            if (glyph != '\0') {
                adc = new AngbandDisplayCharacter(glyph, colour);
            }
            // Per-record message map keyed by spell name -- one MonsterSpellMessage per spell the
            // monster casts, holding its optional vis/invis/miss lines. Built fresh each record so
            // messages never leak between monsters.
            Map<String, MonsterSpellMessage> spellMessages = new HashMap<>();
            for (String spellName : record.spells()) {
                String visMsg = record.messageVis().get(spellName);
                String invisMsg = record.messageInvis().get(spellName);
                String missMsg = record.messageMiss().get(spellName);

                spellMessages.put(spellName, new MonsterSpellMessage(visMsg, invisMsg, missMsg));
            }
            // drop: and drop-base: both feed this single list. A drop: resolves to a specific
            // ObjectKind; a drop-base: (the loop further down) leaves the kind null, meaning "any
            // object of this tval" -- the two are told apart later by whether the kind is null.
            List<MonsterDrop> drops = new ArrayList<>();
            boolean badDrop = false;
            for (MonsterParseRecord.MonsterDropParseRecord drop : record.drops()) {
                String dropTvalStr = drop.type();
                String dropName = drop.name();
                String chance = drop.chance();
                String minStr = drop.min();
                String maxStr = drop.max();
                if (!chance.isEmpty() && !minStr.isEmpty() && !maxStr.isEmpty()) {
                    try {
                        TValue tValue = TValue.fromName("TV_" + dropTvalStr);
                        if (tValue == null) {
                            errors.add("Monster at line: " + line + " has " +
                                    "an unknown TV type: " + dropTvalStr);
                            badDrop = true;
                            continue;
                        }
                        ObjectKind kind = GameConstants.lookupObjectKind(tValue, dropName);
                        if (kind == null) {
                            errors.add("Monster at line: " + line + " has " +
                                    "an unknown drop base: " + dropName);
                            badDrop = true;
                            continue;
                        }
                        int percentage = Integer.parseInt(chance);
                        int min = Integer.parseInt(minStr);
                        int max = Integer.parseInt(maxStr);
                        drops.add(new MonsterDrop(tValue, kind, percentage, min, max));
                    } catch (NumberFormatException e) {
                        errors.add("Monster at line: " + line + " has " +
                                "a malformed integer in it's drop line: " + dropTvalStr);
                        badDrop = true;
                    }
                }
            }
            if (badDrop) continue;
            boolean badDropBase = false;
            for (MonsterParseRecord.MonsterDropBaseParseRecord dropBase : record.dropsBase()) {
                String dropTvalStr = dropBase.type();
                String chance = dropBase.chance();
                String minStr = dropBase.min();
                String maxStr = dropBase.max();
                if (!chance.isEmpty() && !minStr.isEmpty() && !maxStr.isEmpty()) {
                    try {
                        TValue tValue = TValue.fromName("TV_" + dropTvalStr);
                        if (tValue == null) {
                            errors.add("Monster at line: " + line + " has " +
                                    "an unknown TV type: " + dropTvalStr);
                            badDropBase = true;
                            continue;
                        }
                        int percentage = Integer.parseInt(chance);
                        int min = Integer.parseInt(minStr);
                        int max = Integer.parseInt(maxStr);
                        drops.add(new MonsterDrop(tValue, percentage, min, max));
                    } catch (NumberFormatException e) {
                        errors.add("Monster at line: " + line + " has " +
                                "a malformed integer in it's drop line: " + dropTvalStr);
                        badDropBase = true;
                    }
                }
            }
            if (badDropBase) continue;
            boolean badFriend = false;
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
                int chance = 0;
                try {
                    chance = Integer.parseInt(chanceStr);
                } catch (NumberFormatException e) {
                    String role = "";
                    if (roleStr != null) {
                        role = ":" + roleStr;
                    }
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer in it's friend line: " + chanceStr +
                            ":" + numberStr + ":" + nameStr + role);
                    badFriend = true;
                }
                if (chance < 0 || chance > 100) {
                    errors.add("Monster at line: " + line + " has " +
                            "a friends chance value out of bounds.");
                    badFriend = true;
                }
                Random numberDie = null;
                if (!numberStr.isEmpty()) {
                    numberDie = Random.parseStr(numberStr);
                    if (numberDie == null) {
                        errors.add("Monster at line: " + line + " has " +
                                "a malformed die in its friends line: " + numberStr);
                        badFriend = true;
                    }
                }
                MonsterGroupRole role = MonsterGroupRole.MON_GROUP_MEMBER;
                boolean badRole = false;
                if (!roleStr.isEmpty()) {
                    if (roleStr.equalsIgnoreCase("servant"))
                        role = MonsterGroupRole.MON_GROUP_SERVANT;
                    else if (roleStr.equalsIgnoreCase("bodyguard"))
                        role = MonsterGroupRole.MON_GROUP_BODYGUARD;
                    else {
                        errors.add("Monster at line: " + line + " has " +
                                "an unknown friends role: " + roleStr);
                        badRole = true;
                        badFriend = true;
                    }
                }
                if (!badRole)
                    friends.add(new MonsterFriends(nameStr, role, chance, numberDie.getDice(), numberDie.getSides()));
            }
            if (badFriend) continue;
            boolean badFriendBase = false;
            List<MonsterFriendsBase> friendsBase = new ArrayList<>();
            for (MonsterParseRecord.MonsterFriendsParseRecord friend : record.friendsBase()) {
                String chanceStr = friend.chance();
                String numberStr = friend.number();
                String baseKind = friend.name();
                String roleStr = friend.role();
                int chance = 0;
                try {
                    chance = Integer.parseInt(chanceStr);
                } catch (NumberFormatException e) {
                    String role = "";
                    if (roleStr != null) {
                        role = ":" + roleStr;
                    }
                    errors.add("Monster at line: " + line + " has " +
                            "a malformed integer in it's friend base line: " + chanceStr +
                            ":" + numberStr + ":" + baseKind + role);
                    badFriendBase = true;
                }
                if (chance < 0 || chance > 100) {
                    errors.add("Monster at line: " + line + " has " +
                            "a friends base chance value out of bounds.");
                    badFriendBase = true;
                }
                MonsterBase monsterBase = GameConstants.lookupMonsterBase(baseKind);
                if (monsterBase == null) {
                    errors.add("Monster at line: " + line + " has " +
                            "a unknown friends base: " + baseKind);
                    badFriendBase = true;
                }
                Random numberDie = null;
                if (!numberStr.isEmpty()) {
                    numberDie = Random.parseStr(numberStr);
                    if (numberDie == null) {
                        errors.add("Monster at line: " + line + " has " +
                                "a malformed die in its friends base line: " + numberStr);
                        badFriendBase = true;
                    }
                }
                MonsterGroupRole role = MonsterGroupRole.MON_GROUP_MEMBER;
                boolean badRole = false;
                if (!roleStr.isEmpty()) {
                    if (roleStr.equalsIgnoreCase("servant"))
                        role = MonsterGroupRole.MON_GROUP_SERVANT;
                    else if (roleStr.equalsIgnoreCase("bodyguard"))
                        role = MonsterGroupRole.MON_GROUP_BODYGUARD;
                    else {
                        errors.add("Monster at line: " + line + " has " +
                                "an unknown friends role: " + roleStr);
                        badRole = true;
                        badFriendBase = true;
                    }
                }
                if (!badRole)
                    friendsBase.add(new MonsterFriendsBase(monsterBase, role, chance, numberDie.getDice(), numberDie.getSides()));
            }
            if (badFriendBase) continue;
            List<ObjectKind> mimics = new ArrayList<>();
            boolean badMimic = false;
            for (MonsterParseRecord.MonsterMimicParseRecord mimic : record.mimics()) {
                ObjectKind mimicKind = null;
                String tVal = mimic.tVal();
                String sVal = mimic.sVal();
                TValue tValue = TValue.fromName("TV_" + tVal);
                if (tValue == null) {
                    errors.add("Monster at line: " + line + " has " +
                            "an unknown mimic type value: " + tVal);
                    badMimic = true;
                } else {
                    mimicKind = GameConstants.lookupObjectKind(tValue, sVal);
                    if (mimicKind == null) {
                        errors.add("Monster at line: " + line + " has " +
                                "an unknown mimic name: " + sVal);
                        badMimic = true;
                    }
                }
                if (!badMimic)
                    mimics.add(mimicKind);
            }
            if (badMimic) continue;
            // Shape names are kept as bare strings and resolved (to a base or a race) in the second
            // pass, since a shape may name a race defined later in the file.
            List<MonsterShape> shapes = new ArrayList<>();
            for (String shapeBase : record.shape()) {
                shapes.add(new MonsterShape(shapeBase));
            }
            ColourCycle visualsCycler = null;
            String cyclerGroup = record.colourCycleGroup();
            String colourCycleName = record.colourCycleName();
            if (!colourCycleName.isEmpty() && !cyclerGroup.isEmpty()) {
                Map<String, ColourCycle> group = GameConstants.getVisualsCyclerTable().getByGroup(cyclerGroup);
                if (group != null) {
                    visualsCycler = group.get(colourCycleName);
                } else {
                    errors.add("Monster at line: " + line + " has " +
                            "an unknown colour cycle name: " + cyclerGroup + ":" + colourCycleName);
                    continue;
                }
            }
            // An unset (0) innate or spell-cast frequency defaults to 4.
            if (freqInnate == 0) freqInnate = 4;
            if (freqSpell == 0) freqSpell = 4;

            monsters.add(new MonsterRace(name, text, plural, base, hitPoints, ac, sleep,
                    hearing, smell, speed, light, mexp, freqInnate, freqSpell,
                    spellPower, flags, spells, blows, level, rarity, adc, spellMessages,
                    drops, friends, friendsBase, mimics, shapes, shapes.size(), visualsCycler));
        }

        return monsters;
    }
}
