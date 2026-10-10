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

package uk.co.jackoftradesltd.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A playable character class — Warrior, Mage, Priest, Ranger and so on — and the abilities,
 * proficiencies and combat parameters it grants.
 *
 * <p>Ports the C {@code struct player_class} ({@code player.h}), filled from {@code class.txt} by
 * the class parser in {@code init.c}. Class is the counterpart to {@link PlayerRace}: where race
 * sets innate physical traits, class determines the character's profession — its per-level titles,
 * stat and skill adjustments, hit-die and experience contributions, innate flags, melee blow
 * parameters, starting equipment, and (for casters) its spellcasting via {@link ClassMagic}.
 *
 * <p><b>Why the blow parameters live here:</b> {@link #maxAttacks}, {@link #minWeight} and
 * {@link #attMultiplier} are the class-specific inputs to the melee blows-per-round calculation
 * (heavier weapons and lighter classes yield fewer blows), so they are intrinsic to the class
 * rather than to any weapon. Keeping them with the rest of the class data gathers that formula's
 * inputs in one place.
 *
 * <p><b>Shared data, owned copy.</b> In C the loaded classes are read-only records in the
 * {@code classes} list, and every character holds a pointer to the one struct for its class. The
 * port keeps the loaded list in {@link PlayerRegistry#getPlayerClasses()} and gives each character
 * its own {@link #copy()}, so nothing a character does can write through to the template.
 *
 * <p><b>Differences from the C struct.</b>
 * <ul>
 *   <li>{@code next} and {@code cidx} are not stored. The registry list is in file order, and a
 *       class's position in it is its {@code cidx} (see {@link #getClassFromIndex(int)}).</li>
 *   <li>C's {@code title} is a fixed array of ten pointers, with a NULL for every title the file
 *       did not give. {@link #titles} holds only the titles that were loaded, at most ten.</li>
 *   <li>C's {@code c_adj}, {@code c_skills} and {@code x_skills} are arrays zeroed at allocation,
 *       so a stat or skill the file does not mention reads as 0. The Java maps may lack the key,
 *       and the accessors answer 0 for it to match.</li>
 *   <li>C's {@code magic} is an embedded struct that is all zero for a non-caster. The port holds
 *       {@link ClassMagic#NONE} instead of {@code null}.</li>
 * </ul>
 *
 * <p>Class PlayerClass coded before 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class PlayerClass {
    /**
     * Logger for this class. Nothing in this class logs through it at present.
     *
     * <p>Field logger commented in full on 261010.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * Display name of the class, e.g. {@code "Ranger"} — C's {@code player_class.name}, read from
     * the {@code name:} line of {@code class.txt}.
     *
     * <p>Field name commented in full on 261010.
     */
    private String name;

    /**
     * The class's rank titles, in level order — C's {@code player_class.title[10]}, from the
     * {@code title:} lines of {@code class.txt}.
     *
     * <p>Each title covers five levels: title 0 is levels 1 to 5, title 9 is levels 46 to 50 (see
     * {@link #getTitle(int)}). C's array always has ten slots and leaves a slot NULL when the file
     * gives fewer than ten titles, and the parser refuses an eleventh; this list holds only the
     * titles that were loaded, and the assembler refuses more than ten. Every shipped class has
     * exactly ten.
     *
     * <p>Field titles commented in full on 261010.
     */
    private List<String> titles;
    /**
     * The class's adjustment to each stat — C's {@code player_class.c_adj[STAT_MAX]}, from the
     * {@code stats:} line. It is added to the race's adjustment (see {@link #getStatsAdj}). A stat
     * with no entry reads as 0, as an unset slot of the zeroed C array does.
     *
     * <p>Field stats commented in full on 261010.
     */
    private Map<Stats, Integer> stats;
    /**
     * The class's flat contribution to each skill — C's {@code player_class.c_skills[SKILL_MAX]},
     * the {@code base} figure of each {@code skill-*:} line of {@code class.txt}. It does not grow
     * with level (see {@link #extraSkills} for the part that does). A skill with no entry reads
     * as 0.
     *
     * <p>Field classSkills commented in full on 261010.
     */
    private Map<PlayerSkill, Integer> classSkills;
    /**
     * The class's per-level contribution to each skill — C's
     * {@code player_class.x_skills[SKILL_MAX]}, the {@code incr} figure of each {@code skill-*:}
     * line. It is held in tenths of a point per level (see {@link #getXSkill}). A skill with no
     * entry reads as 0.
     *
     * <p>Field extraSkills commented in full on 261010.
     */
    private Map<PlayerSkill, Integer> extraSkills;

    /**
     * The class's contribution to the character's hit die — C's {@code player_class.c_mhp}, from
     * the {@code hitdie:} line. It is a die size, not a number of hit points, and it is added to
     * the race's (see {@link #getMaxHitDie()}).
     *
     * <p>Field hpAdj commented in full on 261010.
     */
    private int hpAdj;
    /**
     * The class's contribution to the character's experience factor — C's
     * {@code player_class.c_exp}, from the {@code exp:} line. It is a percentage addend to the
     * race's factor, not a multiplier, and it is 0 for every shipped class (see
     * {@link #getExpFactor()}).
     *
     * <p>Field expAdj commented in full on 261010.
     */
    private int expAdj;

    /**
     * The object flags the class confers innately — C's {@code player_class.flags}, from the
     * {@code obj-flags:} lines. The constructor replaces the initial empty set with the one it is
     * given.
     *
     * <p>Field oFlags commented in full on 261010.
     */
    private Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
    /**
     * The player flags the class has — C's {@code player_class.pflags}, from the
     * {@code player-flags:} lines (see {@link PlayerFlag}). The constructor replaces the initial
     * empty set with the one it is given.
     *
     * <p>Field pFlags commented in full on 261010.
     */
    private Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);

    /**
     * The greatest number of blows per turn the class can reach — C's
     * {@code player_class.max_attacks}, from the {@code max-attacks:} line. It is a whole number
     * of blows; the blows calculation works in hundredths and multiplies it by 100.
     *
     * <p>Field maxAttacks commented in full on 261010.
     */
    private int maxAttacks;
    /**
     * The weapon weight, in tenth-pounds, that the blows calculation treats as the lightest a
     * weapon can be — C's {@code player_class.min_weight}, from the {@code min-weight:} line. A
     * lighter weapon is counted as this weight; it is not a limit on what the class may wield.
     *
     * <p>Field minWeight commented in full on 261010.
     */
    private int minWeight;
    /**
     * The multiplier applied to the strength figure from {@code adj_str_blow} in the blows
     * calculation — C's {@code player_class.att_multiply}, from the {@code strength-multiplier:}
     * line.
     *
     * <p>Field attMultiplier commented in full on 261010.
     */
    private int attMultiplier;

    /**
     * The equipment a character of this class begins with — C's {@code player_class.start_items},
     * from the {@code equip:} lines (see {@link StartItem}). The list is in the order C's chain
     * runs, which is the reverse of file order, because C pushes each line onto the head of the
     * chain.
     *
     * <p>Field startItems commented in full on 261010.
     */
    private List<StartItem> startItems;

    /**
     * Spellcasting definition for the class (see {@link ClassMagic}) — C's
     * {@code player_class.magic}. Never {@code null}: a class with no magic holds
     * {@link ClassMagic#NONE}, whose book list is empty, as C's {@code magic.num_books} of 0 is.
     *
     * <p>Field magic commented in full on 261010.
     */
    private ClassMagic magic;

    /**
     * Builds a class from its parsed attributes; each parameter populates the like-named field
     * (see those fields for detail).
     *
     * <p>C has no constructor: {@code parse_class_*} in {@code init.c} fill a zeroed struct one
     * line at a time. The port's {@code PlayerClassAssembler} gathers the whole record first and
     * calls this once. The collections and flag sets are stored as given, not copied, so the caller
     * hands over ownership; {@link #copy()} is what makes an independent duplicate. A
     * {@code null} {@code magic} becomes {@link ClassMagic#NONE}.
     *
     * <p>Constructor PlayerClass commented in full on 261010.
     *
     * @param name          display name
     * @param titles        rank titles, at most ten, in level order
     * @param stats         per-stat adjustments
     * @param classSkills   flat skill contributions
     * @param extraSkills   per-level skill contributions, in tenths of a point
     * @param hpAdj         hit-die contribution
     * @param expAdj        experience-factor addend
     * @param oFlags        innate object flags
     * @param pFlags        player flags
     * @param maxAttacks    maximum melee blows per turn
     * @param minWeight     lightest weapon weight the blows calculation counts, in tenth-pounds
     * @param attMultiplier strength multiplier in the blows calculation
     * @param startItems    starting equipment, in C's chain order (reverse file order)
     * @param magic         spellcasting definition; {@code null} is turned into {@link ClassMagic#NONE}
     *                      for a non-caster
     */
    public PlayerClass(String name, List<String> titles, Map<Stats, Integer> stats,
                       Map<PlayerSkill, Integer> classSkills,
                       Map<PlayerSkill, Integer> extraSkills, int hpAdj, int expAdj,
                       Flag<ObjectFlag> oFlags, Flag<PlayerFlag> pFlags, int maxAttacks,
                       int minWeight, int attMultiplier,
                       List<StartItem> startItems,
                       ClassMagic magic) {
        this.name = name;
        this.titles = titles;
        this.stats = stats;
        this.classSkills = classSkills;
        this.extraSkills = extraSkills;
        this.hpAdj = hpAdj;
        this.expAdj = expAdj;
        this.oFlags = oFlags;
        this.pFlags = pFlags;
        this.maxAttacks = maxAttacks;
        this.minWeight = minWeight;
        this.attMultiplier = attMultiplier;
        this.startItems = startItems;
        if (magic == null)
            this.magic = ClassMagic.NONE;
        else
            this.magic = magic;
    }

    /**
     * The class's blow multiplier — C's {@code class->att_multiply}, the numerator of the
     * strength-versus-weight ratio behind blows per turn.
     *
     * <p>Scales how much a class gets out of its strength: {@code calc_blows} in
     * {@code player-calcs.c} computes {@code adj_str_blow[strength index] * this / weapon weight},
     * caps the result at 11, and uses it as the row of the blows table. A class with a higher
     * multiplier therefore reaches a faster row with the same weapon and arm. C reads the field
     * directly and in that one place; {@code PlayerCalcs.calcBlows} is its Java counterpart.
     *
     * <p>Function getAttMultiply commented in full on 261010.
     *
     * @return the class's attack multiplier
     */
    public int getAttMultiply() {
        return attMultiplier;
    }

    /**
     * The class's spellcasting definition — C's {@code class->magic}, the embedded
     * {@code struct class_magic} filled from the {@code magic:} and {@code book:} lines.
     *
     * <p>C tests {@code magic.total_spells} (and, for the books, {@code magic.num_books}) to ask
     * whether a class casts at all. A non-caster has no such record in the file, so C's struct is
     * all zero; the port answers {@link ClassMagic#NONE}, whose book list is empty and whose spell
     * total is 0, so callers never need a {@code null} check.
     *
     * <p>Function getMagic commented in full on 261010.
     *
     * @return the class's spellcasting definition; never {@code null}, a non-caster answers
     * {@link ClassMagic#NONE}, which has no books
     */
    public ClassMagic getMagic() {
        return magic;
    }

    /**
     * The object flags this class has innately — C's {@code class->flags}.
     *
     * <p>C's {@code player_flags} in {@code player.c} copies the race's object flags and unions
     * the class's onto them, so a flag from either source counts once; the port does the same in
     * {@code Player}. It then adds {@code OF_PROT_FEAR} for a {@code PF_BRAVERY_30} class at level
     * 30, a level threshold that lives in code rather than in {@code class.txt}. The flags here
     * are only the ones the file names.
     *
     * <p>The view is read-only: the class is shared template data, and a character holds its own
     * {@link #copy()}.
     *
     * <p>Function getoFlags commented in full on 261010.
     *
     * @return a read-only view of the class's innate object flags
     */
    public FlagView<ObjectFlag> getoFlags() {
        return oFlags;
    }

    /**
     * The class's flat contribution to one skill — C's {@code class->c_skills[skill]}.
     *
     * <p>The half of a skill's base that does not grow: {@code calc_bonuses} in
     * {@code player-calcs.c} starts each skill at the race's value plus this, and {@link #getXSkill}
     * supplies the part that scales with level. A skill the class has no entry for answers 0, as
     * the zeroed C array does; every skill of every shipped class is set, so the default is not
     * reached with the shipped data.
     *
     * <p>Function getSkill commented in full on 261010; defaulted to 0 for an unset skill on
     * 261010.
     *
     * @param skill the skill to read
     * @return the class's flat adjustment for that skill, or 0 if the class has none
     */
    public int getSkill(PlayerSkill skill) {
        return classSkills.getOrDefault(skill, 0);
    }

    /**
     * The player flags this class has — C's {@code class->pflags}, the class abilities named in
     * {@code class.txt}.
     *
     * <p>{@code calc_bonuses} in {@code player-calcs.c} unions these onto the race's in the
     * calculated state, and {@code class_has_ability} in {@code player-properties.c} tests them
     * (the Java counterpart is {@code PlayerProperty}). Some are conditional on level rather than
     * outright: {@code PF_BRAVERY_30} is carried from the start but only confers fear protection
     * at level 30, and the threshold lives in code rather than data.
     *
     * <p>Function getpFlags commented in full on 261010.
     *
     * @return a read-only view of the class's player flags
     */
    public FlagView<PlayerFlag> getpFlags() {
        return pFlags;
    }

    /**
     * The class's adjustment to one stat — C's {@code class->c_adj[stat]}. {@code calc_bonuses} in
     * {@code player-calcs.c} adds it to the race's adjustment and any equipment bonus, then applies
     * the sum through {@code modify_stat_value}. The birth screen ({@code ui-birth.c}) and
     * character sheet ({@code ui-player.c}) show it directly. A stat the class has no entry for
     * answers 0, as the zeroed C array does; every shipped class sets all five.
     *
     * <p>Function getStatsAdj commented in full on 261010; defaulted to 0 for an unset stat on
     * 261010.
     *
     * @param stat the stat to read
     * @return the class's adjustment in points, or 0 if the class has none
     */
    public int getStatsAdj(Stats stat) {
        return stats.getOrDefault(stat, 0);
    }

    /**
     * The weapon weight below which this class gains nothing further — C's
     * {@code class->min_weight}, in tenth-pounds.
     *
     * <p>A floor on the divisor rather than a limit on what may be wielded: {@code calc_blows} in
     * {@code player-calcs.c} divides by {@code max(weapon weight, this)}, so a weapon lighter than
     * this is treated as if it weighed this much and stops earning extra blows. It is what keeps a
     * weightless weapon (no weapon at all counts as weight 0) from dividing by zero, and what stops
     * classes with a high minimum from profiting endlessly by wielding daggers.
     *
     * <p>Function getMinWeight commented in full on 261010.
     *
     * @return the minimum effective weapon weight in tenth-pounds
     */
    public int getMinWeight() {
        return minWeight;
    }

    /**
     * The greatest number of blows per turn this class can reach — C's
     * {@code class->max_attacks}.
     *
     * <p>{@code calc_blows} in {@code player-calcs.c} caps the table result at 100 times this
     * before extra blows from equipment are added, so a modifier can carry a character past the
     * class ceiling where strength and dexterity alone cannot. Birth also reads it to guess a
     * class's role: {@code generate_stats} in {@code player-birth.c} calls a class with fewer than
     * five a caster and one with more than five a warrior when it buys the default stats.
     *
     * <p>Function getMaxAttacks commented in full on 261010.
     *
     * @return the class's maximum blows per turn, unscaled
     */
    public int getMaxAttacks() {
        return maxAttacks;
    }

    /**
     * The distinct magic realms this class draws on — the port of C's {@code class_magic_realms}
     * ({@code player-spell.c}).
     *
     * <p>A realm is a property of a spellbook, and a class's books may repeat one or span two, so
     * this walks the books and keeps one realm for each distinct name. C builds a freshly allocated
     * linked list and returns the count through a pointer; the caller frees the list. The port
     * returns a {@link List}, whose size is the count and which needs no freeing.
     *
     * <p>Deduplication is by realm <em>name</em>, not by identity, as C compares with
     * {@code streq} on the realm names. {@link MagicRealm} has no {@code equals}, so two objects
     * with one name are told apart only by that comparison. The first book to name a realm
     * supplies the object returned for it.
     *
     * <p><b>Order.</b> The list is in C's order, which is the reverse of the order the realms
     * first appear in the books: C prepends each new realm, so the first realm ends up last. A class
     * whose books name arcane then divine returns divine then arcane. It cannot show with the
     * shipped data, as no class has books from two realms, but the {@code ", "} and {@code " or "}
     * joins in {@code ui-birth.c}, {@code player-calcs.c} and {@code player-util.c} print the realms
     * in list order.
     *
     * <p>The result feeds {@code averageSpellStat}, which averages the governing stat's table index
     * across the realms; a class with two realms is therefore limited by the mean of two stats.
     * An empty list is returned for a non-caster (C returns NULL and a count of 0), and the caller
     * must not divide by its size.
     *
     * <p>Function magicRealm commented in full on 261010; returned a {@link List} in C's order on
     * 261010.
     *
     * @return the realms this class casts from, one per distinct name, in C's order; empty for a
     * non-caster
     */
    public List<MagicRealm> magicRealm() {
        if (getMagic().getTotalSpells() == 0)
            return List.of();

        List<MagicRealm> result = new ArrayList<>();
        for (MagicBook book : getMagic().getMagicBooks()) {
            boolean bookFound = false;
            for (MagicRealm mr : result) {
                if (book.getRealm().getName().equals(mr.getName())) {
                    bookFound = true;
                    break;
                }
            }
            if (!bookFound) 
                result.addFirst(book.getRealm());
        }
        
        return result;
    }

    /**
     * The class's per-level contribution to one skill — C's {@code class->x_skills[skill]}.
     *
     * <p>Applied as {@code value * level / 10} near the end of {@code calc_bonuses} in
     * {@code player-calcs.c}, with C's integer division, so the stored number is tenths of a point
     * per level and a class earns whole points of skill only every few levels. A skill the class
     * has no entry for answers 0, as the zeroed C array does; every skill of every shipped class
     * is set.
     *
     * <p>Function getXSkill commented in full on 261010; defaulted to 0 for an unset skill on
     * 261010.
     *
     * @param skill the skill to read
     * @return the class's per-level adjustment, in tenths of a point, or 0 if the class has none
     */
    public int getXSkill(PlayerSkill skill) {
        return extraSkills.getOrDefault(skill, 0);
    }

    /**
     * A private duplicate of this class definition, for a player to own.
     *
     * <p>There is no C counterpart: {@code player_generate} in {@code player-birth.c} simply
     * writes the shared pointer ({@code p->class = c}), because C's class records are read-only
     * data owned by the registry and every character of a class points at the same struct. The port
     * hands the player a copy instead ({@code PlayerBirth} calls this when it sets the class), so
     * that a player can never write through to the template behind every other character of the
     * same class — the boundary between shared game data and one character's state is drawn here
     * rather than trusted to convention.
     *
     * <p>The copy is deep exactly where it needs to be. The four collections (titles, stats, class
     * skills, extra skills) and the start-item list are rebuilt, so adding a title or a skill entry
     * to the copy cannot reach the original; their elements — {@link String}, {@link Integer} and
     * {@link StartItem}, which has no setters — are safely shared. Both flag sets are new
     * {@link Flag} instances filled by {@code copyFrom}, never the originals aliased.
     * {@link ClassMagic} is shared by reference: it holds the spell data that C also shares across
     * every caster of the class, and what a particular character knows lives on the player, not in
     * the book.
     *
     * <p>Function copy commented in full on 261010.
     *
     * @return a new {@code PlayerClass} equal to this one, sharing no mutable structure with it
     */
    public PlayerClass copy() {
        Flag<ObjectFlag> oFlagsClone = new Flag<>(ObjectFlag.class);
        oFlagsClone.copyFrom(oFlags);
        Flag<PlayerFlag> pFlagsClone = new Flag<>(PlayerFlag.class);
        pFlagsClone.copyFrom(pFlags);

        return new PlayerClass(name, new ArrayList<>(this.titles), new HashMap<>(this.stats),
                new HashMap<>(this.classSkills), new HashMap<>(extraSkills), this.hpAdj,
                this.expAdj, oFlagsClone, pFlagsClone, this.maxAttacks, this.minWeight,
                this.attMultiplier, new ArrayList<>(startItems), this.magic);
    }

    /**
     * The class's contribution to a character's experience factor.
     *
     * <p>C reads the field directly, and only in two places: {@code player_generate} in
     * {@code player-birth.c} sets {@code p->expfact = p->race->r_exp + p->class->c_exp}, and the
     * birth screen in {@code ui-birth.c} adds the same pair to show the figure. The class half is
     * added to, not multiplied by, the race's factor, and in 4.2.6 it is always zero: the parser
     * ({@code parse_class_exp} in {@code init.c}) reads an {@code exp:} line but no class in
     * {@code class.txt} carries one, so every shipped class leaves the whole factor to the race.
     * The field is therefore a hook for variant data rather than something the base game varies.
     *
     * <p>Despite the field name ({@code expAdj}, after that {@code exp:} line) the value is not an
     * adjustment applied to experience gained. It is one addend of the percentage that scales every
     * level threshold: {@code adjust_level} in {@code player.c} (the port's
     * {@code Player.adjustLevel}) multiplies a threshold by the factor and divides by 100 before
     * comparing, so a larger factor means each level costs proportionately more.
     *
     * <p>Function getExpFactor commented in full on 261010.
     *
     * @return the class's experience factor, a percentage addend in C's {@code c_exp}
     */
    public int getExpFactor() {
        return this.expAdj;
    }

    /**
     * The class's contribution to the size of the character's hit die.
     *
     * <p>C reads {@code c_mhp} directly, and only in two places: {@code player_generate} in
     * {@code player-birth.c} sets {@code p->hitdie = p->race->r_mhp + p->class->c_mhp}, and the
     * birth screen in {@code ui-birth.c} adds the same pair to show the figure. The class half is
     * added to, not multiplied by, the race's, and it is the smaller of the two — the shipped
     * classes run from 0 for a Mage to 9 for a Warrior, so the class chosen can more than double a
     * frail race's die or leave it untouched.
     *
     * <p>Despite the field name ({@code hpAdj}, after the {@code hitdie:} line that
     * {@code parse_class_hitdie} in {@code init.c} reads) the value is not hit points and not an
     * adjustment applied to them. It is one addend of a die size: {@link PlayerBirth#rollHP(Player)}
     * rolls {@code randint1(hitdie)} once per level above the first, so a larger figure widens
     * every roll of the character's career rather than granting a fixed amount.
     *
     * <p>Function getMaxHitDie commented in full on 261010.
     *
     * @return the class's hit-die contribution, C's {@code c_mhp}
     */
    public int getMaxHitDie() {
        return this.hpAdj;
    }

    /**
     * The class's display name, e.g. {@code "Ranger"} — C's {@code class->name}.
     *
     * <p>Read directly wherever C names the class: the character sheet and death screen
     * ({@code ui-player.c}, {@code ui-death.c}), the save file
     * ({@code save.c}, {@code save-charoutput.c}), preference file matching
     * ({@code ui-prefs.c}) and the status line ({@code ui-display.c}). The save file stores the
     * name as text, so a class is found again on load by name rather than by index.
     *
     * <p>Function getName commented in full on 261010.
     *
     * @return the class's display name
     */
    public String getName() {
        return name;
    }

    /**
     * The equipment a character of this class begins with — C's {@code class->start_items}.
     *
     * <p>C stores this as a singly-linked {@code struct start_item} chain, walked head-to-tail at
     * birth by {@code player_outfit} in {@code player-birth.c}. {@code parse_class_equip} in
     * {@code init.c} pushes each {@code equip:} line onto the head of the chain, so the walk meets
     * the last line of the file first. The port holds the same sequence as a {@link List} — the
     * assembler reverses the file order to build it — and {@link PlayerBirth} reads it front to
     * back. The order is player-visible, as it decides which item takes which pack letter.
     *
     * <p>The list is the class's own, not a copy, and is not to be modified.
     *
     * <p>Function getStartItems commented in full on 261010.
     *
     * @return the class's starting-equipment list, in the order it is granted at birth
     */
    public List<StartItem> getStartItems() {
        return this.startItems;
    }

    /**
     * Look up a player class by its position in load order — the port of C's
     * {@code player_id2class} ({@code player-class.c}).
     *
     * <p>C walks the {@code classes} linked list comparing each node's {@code cidx} — an
     * unsigned {@code guid} — against the requested id, and returns the first match or
     * {@code NULL} if the list runs out. The parser assigns {@code cidx} by prepending each
     * class as it is read, then counting down from the list head once parsing finishes
     * ({@code finish_parse_class} in {@code init.c}); because the list is built by prepending,
     * that count-down lands the data file's first class at {@code cidx} 0 and its last at
     * {@code cidx count - 1} — ascending file order. {@link PlayerRegistry#getPlayerClasses()}
     * keeps its list in that same file
     * order, so indexing straight into it reaches the class C's linked-list search would have
     * found, without needing to walk anything.
     *
     * <p>An index at or past the loaded class count returns {@code null}, matching the C loop
     * falling off the list tail. A negative index also returns {@code null}: C's {@code guid} is
     * unsigned, so a caller's negative intent arrives as a value no {@code cidx} can equal and
     * the C search still misses; the port guards for that explicitly rather than letting
     * {@link List#get} throw where C would have degraded to {@code NULL}.
     *
     * <p>C's callers are birth ({@code player-birth.c}, which asks for class 0 as the default and
     * for the player's menu choice), the birth menu ({@code ui-birth.c}) and the score list
     * ({@code ui-score.c}); {@link PlayerBirth} makes the first two calls in the port.
     *
     * <p>Function getClassFromIndex coded before 260907, commented in full on 261010; guarded
     * against negative indices on 260907.
     *
     * @param classIndex the class's position in load order, C's {@code cidx}
     * @return the matching {@link PlayerClass}, or {@code null} if no loaded class has that index
     */
    public static PlayerClass getClassFromIndex(int classIndex) {
        List<PlayerClass> classes = PlayerRegistry.getPlayerClasses();
        if (classIndex >= classes.size() || classIndex < 0)
            return null;
        return classes.get(classIndex);
    }

    /**
     * The class's rank title for a character level — C's
     * {@code class->title[(lev - 1) / 5]}.
     *
     * <p>Each title covers five levels, so levels 1 to 5 read title 0, 6 to 10 title 1, and 46 to
     * 50 title 9. C indexes its fixed array of ten and has no title to give for a slot the file
     * left empty, so there the slot is NULL; the port returns {@code null} when the index is past
     * the end of {@link #titles}, and also for a class that has no titles at all. The shipped
     * classes all have ten, so neither case arises with the shipped data.
     *
     * <p>C does not index above level 50 for the character sheet: {@code show_title} in
     * {@code ui-player.c} returns a wizard or winner string first, and a level above 50 is
     * treated as a winner. A ten-title class answers {@code null} here for level 51 and above. A
     * level below 1 is read as title 0, where C's index would run off the front of the array.
     *
     * <p>The port calls this when the level changes ({@code Player.setLevel}) and when the title
     * on the sidebar is redrawn ({@code PlayerCalcs}), where C reads the field at the point of
     * display.
     *
     * <p>Function getTitle coded before 261010, commented in full on 261010.
     *
     * @param level the character's level, 1 to 50
     * @return the title for that level, or {@code null} if the class has no title for it
     */
    public String getTitle(int level) {
        if (titles.isEmpty())
            return null;

        int index = (level - 1) / 5;

        if (index >= titles.size())
            return null;

        if (index < 0)
            index = 0;

        return titles.get(index);
    }

    /**
     * How many titles the class has.
     *
     * <p>There is no C counterpart: C's {@code title} is a fixed array of ten, and nothing in the C
     * source asks how many of the slots were filled. This answers the number of titles actually
     * loaded, at most ten and exactly ten for every shipped class. No production code calls it at
     * present.
     *
     * <p>Function getNoTitles coded before 261010, commented in full on 261010.
     *
     * @return the number of titles loaded for the class
     */
    public int getNoTitles() {
        return titles.size();
    }
}
