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

package uk.co.jackoftradesltd.middle.numerics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.*;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.backend.parser.RandomReader;

import java.io.IOException;
import java.util.List;

/**
 * A parameterised random value of the classic Angband form
 * {@code base + dice 'd' sides + m_bonus}, where the {@code m_bonus} term is a ceiling on a bonus
 * that grows with dungeon level. This is the Java port of the {@code random_value} struct from
 * {@code z-rand.h} and of {@code randcalc()}, {@code randcalc_valid()} and {@code randcalc_varies()}
 * from {@code z-rand.c}; it is used throughout the data files to express damage rolls, durations
 * and quantities.
 *
 * <p>C's struct is four bare {@code int} fields that any caller assigns directly, and the setters
 * here follow that: they store what they are given, with only a negative dice or sides count
 * clamped to 0. The die arithmetic itself ({@code damcalc()}, {@code m_bonus_calc()}) lives in
 * {@link RandomValueUtils}, and this class composes it.
 *
 * <p>A negated value ({@code "-1d4"} in a data file) is built from its positive parts and then has
 * its base shifted so that the whole range flips sign. C does the same in {@code parse_random()} in
 * {@code parser.c}, because the random components are always positive; here the shift is
 * {@link #negate()}, and it happens once.
 *
 * <p>Class Random coded before 260815, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
public class Random {
    /**
     * The shared prototype behind {@link #Zero()}: base 0, no level bonus and zero dice of zero sides,
     * so it rolls 0 under every {@link DamageAspect}.
     *
     * <p>The C original has no named constant for this. It writes the value out as a
     * {@code random_value} literal {@code { 0, 0, 0, 0 }} wherever a "no dice" starting point is
     * needed, for example in {@code effects.c} and {@code obj-info.c}. Java hands out copies of this
     * prototype instead, so a caller that mutates its result through a setter cannot corrupt the
     * shared instance. This field is private for that reason: callers never see it, only a copy.
     *
     * <p>Field ZERO coded on 261003, commented in full on 261003.
     */
    private final static Random ZERO = new Random(0, 0, 0, 0, false);
    /**
     * The shared prototype behind {@link #One()}: base 0, no level bonus and one die of one side
     * ({@code 1d1}), so it rolls exactly 1 under every {@link DamageAspect} and never varies.
     *
     * <p>The C original has no counterpart; there is no {@code { 0, 0, 1, 1 }} literal anywhere in
     * {@code src/}. The value is a Java-side convenience, and it follows the {@code NdM} reading of
     * {@code z-rand.c}, function {@code randcalc()}: the dice contribute {@code 1} when minimised,
     * maximised or averaged, and {@code damroll(1, 1)} cannot roll anything else. The constructor
     * takes base, bonus, dice, sides, whereas the C struct orders them base, dice, sides,
     * {@code m_bonus}; the transposition cannot matter here because the two zeroes and the two ones
     * are interchangeable.
     *
     * <p>Field ONE coded on 261003, commented in full on 261003.
     */
    private final static Random ONE = new Random(0, 0, 1, 1, false);
    /**
     * Shared logger, used by {@link #parseStr(String)} to report strings it refuses or cannot read.
     *
     * <p>Field logger coded before 260815, commented in full on 261005.
     */
    private final static Logger logger = LogManager.getLogger(Random.class.getName());
    /**
     * The unresolved text of the base term, kept by the constructor that takes the base as an
     * expression such as a {@code $} variable rather than a number. Nothing in this class reads it
     * back; the resolved {@link #base} stays 0 for such a value.
     *
     * <p>Field baseStr coded before 260815, commented in full on 261005.
     */
    private String baseStr;
    /**
     * The flat term of the roll, added to the dice and the level bonus. It is negative for a negated
     * value, as {@link #negate()} shifts it.
     *
     * <p>Field base coded before 260815, commented in full on 261005.
     */
    private int base;
    /**
     * The number of dice rolled, the {@code N} in {@code NdM}. The count may be 0, as in C's
     * {@code { 0, 0, 0, 0 }} and in a plain constant such as {@code "5"}.
     *
     * <p>Field dice coded before 260815, commented in full on 261005.
     */
    private int dice;
    /**
     * The unresolved text of the sides term, kept by the constructor that takes the sides as an
     * expression rather than a number. Nothing in this class reads it back; the resolved
     * {@link #sides} stays 0 for such a value.
     *
     * <p>Field sidesStr coded before 260815, commented in full on 261005.
     */
    private String sidesStr;
    /**
     * The number of faces on each die, the {@code M} in {@code NdM}.
     *
     * <p>Field sides coded before 260815, commented in full on 261005.
     */
    private int sides;
    /**
     * C's {@code m_bonus} field: the ceiling of a bonus that scales with level, not a multiplier.
     * {@link #randCalc(int, DamageAspect)} hands it to {@code m_bonus_calc()} as {@code max}, which
     * gives the whole of it when maximised, none of it when minimised, and {@code max * level /
     * MAX_RAND_DEPTH} when averaged. A value of 0 means no bonus. A few effect handlers in
     * {@code effect-handler-attack.c} borrow the field for a percentage or a count instead.
     *
     * <p>Field mBonus coded before 260815, commented in full on 261005.
     */
    private int mBonus;
    /**
     * Set when the value is to represent a negated range; {@link #negate()} acts on it once and
     * clears it. The public constructor applies it immediately, so it stays true on a finished object
     * only if {@link #setToNegate(boolean)} was called afterwards.
     *
     * <p>Field toNegate coded before 260815, commented in full on 261005.
     */
    private boolean toNegate;
    /**
     * Set once {@link #negate()} has shifted the base, so a second call cannot shift it again. C
     * needs no such flag because {@code parse_random()} negates exactly once, as it builds the value.
     *
     * <p>Field negated coded before 260815, commented in full on 261005.
     */
    private boolean negated;

    /**
     * Builds a random value from its four resolved terms, optionally negating the whole range.
     *
     * <p>The value is {@code base + dice 'd' sides + m_bonus}. Note that the parameter order is base,
     * bonus, dice, sides, whereas C's struct declares base, dice, sides, {@code m_bonus}. When
     * {@code toNegate} is true the constructor calls {@link #negate()} straight away, so the finished
     * object already holds the shifted base, exactly as {@code parse_random()} in {@code parser.c}
     * leaves a struct after a leading {@code -}. The terms are expected to be positive when negating.
     *
     * <p>Constructor Random(int, int, int, int, boolean) coded before 260815, commented in full on
     * 261005.
     *
     * @param base     the flat term; for a value to be negated, the positive base before the shift
     * @param mBonus   the ceiling of the level-scaled bonus, 0 for none
     * @param dice     the number of dice to roll
     * @param sides    the number of faces on each die
     * @param toNegate true to flip the whole range to its negative as the value is built
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public Random(int base, int mBonus, int dice, int sides, boolean toNegate) {
        //if (debug) logger.traceEntry("Random.constructor with values base: {} mBonus: {} dice: {} sides: {} toNegate: {}", base, mBonus, dice, sides, toNegate);
        this.base = base;
        this.dice = dice;
        this.sides = sides;
        this.mBonus = mBonus;
        this.toNegate = toNegate;
        negate();

//        // The below lines are put in to deal with the issue of negative bases not being caught by the parser.
//        // TODO: Check this in the upcoming Reader sweep
//        if (this.base < 0) {
//            this.toNegate = true;
//            this.base = this.base * -1;
//        }
//        negated = false;
    }

    /**
     * Builds a random value whose base arrives as expression text, such as a {@code $} variable,
     * rather than a number. The text is kept in {@link #baseStr} and the resolved {@link #base}
     * is left at 0.
     *
     * <p>There is no counterpart in C's {@code random_value}, which holds only integers; C resolves
     * variable dice in {@code z-dice.c} instead. This constructor does not negate, and
     * {@link #parseStr(String)} refuses a negated value containing a {@code $}.
     *
     * <p>Constructor Random(String, int, int, int) coded before 260815, commented in full on 261005.
     *
     * @param base   the base term as unresolved expression text
     * @param mBonus the ceiling of the level-scaled bonus, 0 for none
     * @param dice   the number of dice
     * @param sides  the number of faces on each die
     */
    public Random(String base, int mBonus, int dice, int sides) {
        this.baseStr = base;
        this.dice = dice;
        this.sides = sides;
        this.mBonus = mBonus;
    }
    //private final boolean debug = false;

    /**
     * Builds a random value whose sides arrive as expression text, such as a {@code $} variable,
     * rather than a number. The text is kept in {@link #sidesStr} and the resolved {@link #sides}
     * is left at 0.
     *
     * <p>As with the string-base constructor there is no C counterpart in {@code random_value}; see
     * {@code z-dice.c} for how C resolves variable dice. This constructor does not negate.
     *
     * <p>Constructor Random(int, int, int, String) coded before 260815, commented in full on 261005.
     *
     * @param base   the flat term
     * @param mBonus the ceiling of the level-scaled bonus, 0 for none
     * @param dice   the number of dice
     * @param sides  the number of faces on each die as unresolved expression text
     */
    public Random(int base, int mBonus, int dice, String sides) {
        this.base = base;
        this.dice = dice;
        this.mBonus = mBonus;
        this.sidesStr = sides;
    }

    /**
     * Returns a new random value that always rolls 0, the equivalent of C's {@code { 0, 0, 0, 0 }}
     * {@code random_value} literal.
     *
     * <p>Each call returns an independent copy of {@link #ZERO} via {@link #copy()}, so the caller
     * may use the setters freely. As {@link #copy()} documents, the copy carries only the resolved
     * integer terms, which is all this value has.
     *
     * <p>Function Zero coded on 261003, commented in full on 261003.
     *
     * @return a fresh {@code 0} random value, never {@code null}
     */
    public static Random Zero() {
        return ZERO.copy();
    }

    /**
     * Returns a new random value that always rolls 1 ({@code 1d1} with no base or level bonus).
     *
     * <p>Each call returns an independent copy of {@link #ONE} via {@link #copy()}. There is no C
     * original for this value; see {@link #ONE}.
     *
     * <p>Function One coded on 261003, commented in full on 261003.
     *
     * @return a fresh {@code 1} random value, never {@code null}
     */
    public static Random One() {
        return ONE.copy();
    }

    /**
     * Turns a data-file random string such as {@code "2+1d4M3"} or {@code "-5"} into a
     * {@code Random}, the counterpart of {@code parse_random()} in {@code parser.c}.
     *
     * <p>A leading {@code -} is stripped before the rest is handed to {@code RandomReader}, then the
     * result is negated, so the reader only ever sees the positive form, as in C. An empty string, a
     * lone {@code -}, a reader failure or an empty result all return {@code null}. A negated string
     * containing {@code $} is refused with a warning, as C has no such form. The grammar rules for the
     * string itself belong to {@code RandomReader}, not to this method.
     *
     * <p>Function parseStr coded before 260815, commented in full on 261005.
     *
     * @param randomString the random string to parse
     * @return the parsed value, or {@code null} if the string was empty or could not be read
     */
    @Nullable
    @CheckReturnValue
    public static Random parseStr(@NotNull String randomString) {
        if (randomString.isEmpty()) return null;

        boolean negFound = randomString.charAt(0) == '-';
        boolean complex = randomString.contains("$");

        // neg and complex - not allowed
        if (negFound && complex) {
            logger.warn("negated complex variable dice not supported: {}", randomString);
            return null;
        }

        // neg & simple
        if (negFound) {
            randomString = randomString.substring(1);
            if (randomString.isEmpty()) return null;
        }

        // randomString is always positive at this point

        RandomReader reader = new RandomReader();
        List<Random> randoms;
        try {
            randoms = reader.parse(randomString);
        } catch (IOException e) {
            logger.error("Error while parsing random string: {}", randomString, e);
            return null;
        }

        if (randoms.isEmpty()) return null;

        Random random = randoms.getFirst();
        if (random == null) return null;

        if (negFound) {
            random.toNegate = true;
            random.negate();
        }

        return random;
    }

    /**
     * Marks this value as due for negation, for tests that need to stage it. The flag does nothing
     * until {@link #negate()} is called, and it is forced to false once the value has already been
     * negated, so the base cannot be shifted twice. There is no C counterpart.
     *
     * <p>Function setToNegate coded before 260815, commented in full on 261005.
     *
     * @param toNegate whether this value should be negated by the next call to {@link #negate()}
     */
    @Contract(mutates = "this")
    @TestOnly
    public void setToNegate(boolean toNegate) {
        if (negated)
            this.toNegate = false;
        else
            this.toNegate = toNegate;
    }

    /**
     * Returns the flat term of the roll.
     *
     * <p>This is not the minimum of the roll, since each die adds at least 1; use
     * {@link #randCalc(int, DamageAspect)} with {@code MINIMIZE} for that. For a negated value it
     * is the shifted, negative base.
     *
     * <p>Function getBase coded before 260815, commented in full on 261005.
     *
     * @return the base term, as stored
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getBase() {
        return base;
    }

    /**
     * Sets the flat term, storing the value as given. A negative base is legitimate, since a negated
     * value carries one; C assigns {@code v.base} directly with no check, and so does this.
     *
     * <p>Function setBase coded before 260815, commented in full on 261005.
     *
     * @param base the new flat term of the roll
     */
    @Contract(mutates = "this")
    public void setBase(int base) {
        this.base = base;
    }

    /**
     * Returns the number of dice this value rolls, C's {@code v.dice}.
     *
     * <p>Function getDice coded before 260815, commented in full on 261005.
     *
     * @return the number of dice, 0 for a constant
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getDice() {
        return dice;
    }

    /**
     * Sets the number of dice. A negative count is stored as 0; 0 itself is kept, because C allows
     * it (a constant value, or {@code { 0, 0, 0, 0 }}) and assigns {@code v.dice} directly. C
     * never produces a negative count, so the clamp is a Java guard with no C counterpart.
     *
     * <p>Function setDice coded before 260815, commented in full on 261005.
     *
     * @param dice the number of dice to roll
     */
    @Contract(mutates = "this")
    public void setDice(int dice) {
        if (dice < 0) dice = 0;
        this.dice = dice;
    }

    /**
     * Returns the number of faces on each die, C's {@code v.sides}.
     *
     * <p>Function getSides coded before 260815, commented in full on 261005.
     *
     * @return the number of faces on each die, 0 for a constant
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getSides() {
        return sides;
    }

    /**
     * Sets the number of faces on each die. A negative count is stored as 0; 0 itself is kept, as in
     * C, where {@code damroll()} treats a die with no sides as rolling 0. C never produces a negative
     * count, so the clamp is a Java guard with no C counterpart.
     *
     * <p>Function setSides coded before 260815, commented in full on 261005.
     *
     * @param sides the number of faces on each die, i.e. 4 for a d4
     */
    @Contract(mutates = "this")
    public void setSides(int sides) {
        if (sides < 0) sides = 0;
        this.sides = sides;
    }

    /**
     * Returns the ceiling of the level-scaled bonus, C's {@code v.m_bonus}. It is not a multiplier;
     * see {@link #mBonus}.
     *
     * <p>Function getMBonus coded before 260815, commented in full on 261005.
     *
     * @return the {@code m_bonus}, 0 for none
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getMBonus() {
        return mBonus;
    }

    /**
     * Sets the ceiling of the level-scaled bonus, storing the value as given. C assigns
     * {@code v.m_bonus} directly, and 0, meaning no bonus, is the usual value.
     *
     * <p>Function setMBonus coded before 260815, commented in full on 261005.
     *
     * @param mBonus the new {@code m_bonus}
     */
    @Contract(mutates = "this")
    public void setMBonus(int mBonus) {
        this.mBonus = mBonus;
    }

    /**
     * Flips the whole range of this value to its negative, if {@link #toNegate} is set and it has
     * not been negated already; otherwise it does nothing.
     *
     * <p>The new base is {@code -base - m_bonus - dice * (sides + 1)}, which is the negation block of
     * {@code parse_random()} in {@code parser.c}. The dice and bonus stay positive, so the base must
     * absorb their maximum. For {@code "-1d4"} the base becomes -5 and the range runs from -4 to -1.
     * The {@link #negated} flag is Java's addition, since C negates once as it parses.
     *
     * <p>Function negate coded before 260815, commented in full on 261005.
     */
    @Contract(mutates = "this")
    public void negate() {
        //if (debug) logger.traceEntry("Random.negate() oldBase: {}", base);

        if (toNegate && !negated) {
            base = base * -1;
            base = base - mBonus;
            base = base - (dice * (sides + 1));
            toNegate = false;
            negated = true;
        }

        // if (debug) logger.traceExit("Random.negate() newBase: {}", base);
    }

    /**
     * Returns a single line showing the negation flags and the four terms, for diagnostics in tests.
     * There is no C counterpart, and the string form is not the data-file syntax that
     * {@link #parseStr(String)} reads.
     *
     * <p>Function toString coded before 260815, commented in full on 261005.
     *
     * @return the flags and terms of this value on one line
     */
    @CheckReturnValue
    @Contract(pure = true)
    @TestOnly
    @Override
    public String toString() {
        return "Random{" +
                "negated=" + negated +
                ", toNegate=" + toNegate +
                ", mBonus=" + mBonus +
                ", sides=" + sides +
                ", dice=" + dice +
                ", base=" + base +
                '}';
    }

    /**
     * Evaluates this value for a level and an aspect, the port of {@code randcalc()} in
     * {@code z-rand.c}.
     *
     * <p>For every aspect but {@link DamageAspect#EXTREMIFY} the result is the base plus the dice
     * term plus the level bonus, with each term evaluated for that aspect: {@code RANDOMIZE} rolls,
     * {@code MINIMIZE} gives the floor, {@code MAXIMIZE} the ceiling and {@code AVERAGE} the mean.
     * {@code EXTREMIFY} evaluates both ends and returns whichever is further from zero, preferring
     * the maximum when they are equally far. That matters for a negated value, where the minimum is
     * the larger magnitude: {@code "-1d4"} extremifies to -4, not -1. The level affects only the
     * bonus term.
     *
     * <p>Function randCalc coded before 260815, commented in full on 261005.
     *
     * @param level  the dungeon level, which scales the {@code m_bonus} term
     * @param aspect how to evaluate each term
     * @return the base plus the dice and bonus terms for that aspect
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int randCalc(int level, DamageAspect aspect) {
        if (aspect == DamageAspect.EXTREMIFY) {
            int min = randCalc(level, DamageAspect.MINIMIZE);
            int max = randCalc(level, DamageAspect.MAXIMIZE);

            return Math.abs(min) > Math.abs(max) ? min : max;
        }

        int damage = RandomValueUtils.damCalc(dice, sides, aspect);
        int bonus = RandomValueUtils.mBonusCalc(mBonus, level, aspect);
        return base + damage + bonus;
    }

    /**
     * Tests whether a value lies within the range this one can produce, the port of
     * {@code randcalc_valid()} in {@code z-rand.c}.
     *
     * <p>Both ends are inclusive, and both are taken at level 0, so the level-scaled bonus counts as
     * 0 at the bottom of the range and as the whole {@code m_bonus} at the top.
     *
     * <p>Function isValid coded before 260815, commented in full on 261005.
     *
     * @param test the value to check
     * @return true if {@code test} is no lower than the minimum and no higher than the maximum
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isValid(int test) {
        return test >= randCalc(0, DamageAspect.MINIMIZE) && test <= randCalc(0, DamageAspect.MAXIMIZE);
    }

    /**
     * Tests whether this value can produce more than one result, the port of
     * {@code randcalc_varies()} in {@code z-rand.c}.
     *
     * <p>It compares the minimum and maximum at level 0. A single die of one face ({@code 1d1}) or
     * any constant therefore does not vary, while a value whose only variation is a non-zero
     * {@code m_bonus} does.
     *
     * <p>Function varies coded before 260815, commented in full on 261005.
     *
     * @return true if the minimum and maximum differ, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean varies() {
        int min = randCalc(0, DamageAspect.MINIMIZE);
        int max = randCalc(0, DamageAspect.MAXIMIZE);
        return min != max;
    }

    /**
     * Tests whether the flat term is present. C has no such function and tests the field inline,
     * as in {@code if (value.m_bonus)} in {@code effects-info.c}; the description code uses these
     * tests to decide which terms are worth printing.
     *
     * <p>Function hasBase coded before 260815, commented in full on 261005.
     *
     * @return true if the base is not zero, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasBase() {
        return base != 0;
    }

    /**
     * Tests whether the dice term is present. A constant such as {@code "5"} has no dice, so this
     * is false for it, and for C's {@code { 0, 0, 0, 0 }}.
     *
     * <p>Function hasDice coded before 260815, commented in full on 261005.
     *
     * @return true if the dice count is not zero, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasDice() {
        return dice != 0;
    }

    /**
     * Tests whether the dice have any faces. A constant has none, so this is false for it, and for
     * a value built with the string-sides constructor, whose resolved {@link #sides} stays 0.
     *
     * <p>Function hasSides coded before 260815, commented in full on 261005.
     *
     * @return true if the sides count is not zero, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasSides() {
        return sides != 0;
    }

    /**
     * Tests whether the level-scaled bonus is present: the same truthiness test as
     * {@code if (value.m_bonus)} in {@code effects-info.c} and {@code player-spell.c}. An
     * {@code m_bonus} of 0 means no bonus, so this is false for {@link #Zero()} and for any
     * ordinary dice value.
     *
     * <p>Function hasBonus coded before 260815, commented in full on 261005.
     *
     * @return true if {@code m_bonus} is not zero, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasBonus() {
        return mBonus != 0;
    }

    /**
     * Returns an independent copy of this random value.
     *
     * <p>Copies the four resolved terms and passes {@code false} for the negate flag, because
     * negation is applied when the value is built rather than stored as state - the base and bonus
     * already carry their signs by the time they reach here.
     *
     * <p><b>The unresolved string forms are not carried across.</b> {@link #baseStr} and
     * {@link #sidesStr} hold the expression text for values whose terms were not numbers in the data
     * file; a copy made through this method holds only what those expressions resolved to. That is
     * right for the callers that copy a live object's dice, and would be wrong for anything hoping
     * to re-resolve the expression afterwards.
     *
     * <p>Function copy commented in full on 260827.
     *
     * @return a new random value with the same resolved terms
     */
    public Random copy() {
        return new Random(this.base, this.mBonus, this.dice, this.sides, false);
    }
}