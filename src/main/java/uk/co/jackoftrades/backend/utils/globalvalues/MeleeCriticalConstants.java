package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

/**
 * This class holds constants for non-O critical calculations.<br>
 * In general, those calculations compute the chance for a critical from a linear combination of the to-hit bonuses,
 * the player's to-hit skill, the player's level, and the weapon or missile's weight.  If a critical happens, they then
 * compute the power (i.e. severity of the critical) from a random term and the weapon or missile's weight.
 * <br><br>
 * Non-O criticals are Vanilla Angband style and have the following system:
 * <ul>
 *   <li>Criticals are calculated based on <strong>weight of the weapon</strong> and your <strong>skill</strong>
 *   (to-hit value).</li>
 *   <li>A random roll is made using <code>weight + skill</code>, and if it exceeds certain thresholds, you get a
 *   critical of increasing severity (good, excellent, superb, etc.).</li>
 *   <li><strong>Heavier weapons</strong> have a significant built-in advantage for scoring criticals, because raw
 *   weapon weight directly inflates the roll.</li>
 *   <li>This means a heavy two-handed sword will crit far more often than a dagger, purely due to mass.</li>
 * </ul>
 */
public class MeleeCriticalConstants {
    private final static String tag = "melee-critical";

    private static int debuffToh;
    private static int chanceWeightScale;
    private static int chanceTohScale;
    private static int chanceLevelScale;
    private static int chanceTohSkillScale;
    private static int chanceOffset;
    private static int chanceRange;
    private static int powerWeightScale;
    private static int powerRandom;

    private final static Logger logger = LogManager.getLogger();

    public static void setValue(String value) throws InvalidTokenFoundDuringParse {
        String[] values = value.split(":");

        if (values.length != 2) {
            String message = "Invalid number of tokens read from constants.txt. Token was: "
                    + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = values[0];
        int val;

        try {
            val = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number format in string parsed from constants.txt. Tag was: "
                    + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        switch (name) {
            case "debuff-toh":
                setDebuffToh(val);
                break;

            case "chance-weight-scale":
                setChanceWeightScale(val);
                break;

            case "chance-toh-scale":
                setChanceTohScale(val);
                break;

            case "chance-level-scale":
                setChanceLevelScale(val);
                break;

            case "chance-toh-skill-scale":
                setChanceTohSkillScale(val);
                break;

            case "chance-offset":
                setChanceOffset(val);
                break;

            case "chance-range":
                setChanceRange(val, name);
                break;

            case "power-weight-scale":
                setPowerWeightScale(val);
                break;

            case "power-random":
                setPowerRandom(val);
                break;

            default:
                String message = "Invalid tag received in parse of constants.txt. Token was: "
                        + tag + ":" + value;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Get the amount added to the to-hit value for calculating the chance of a mêlée critical when the target is
     * debuffed.
     *
     * @return Amount to add to to-hit for a critical against debuffed targets
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getDebuffToh() {
        return debuffToh;
    }

    private static void setDebuffToh(int debuffToh) {
        // No checks on this value - just let it through
        MeleeCriticalConstants.debuffToh = debuffToh;
    }

    /**
     * Gets the scale factor for the weapon's weight in the chance for a mêlée critical
     *
     * @return the scale factor for the weapon's weight in the chance for a mêlée critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceWeightScale() {
        return chanceWeightScale;
    }

    private static void setChanceWeightScale(int chanceWeightScale) throws InvalidTokenFoundDuringParse {
        // No checks on this value - just let it through
        MeleeCriticalConstants.chanceWeightScale = chanceWeightScale;
    }

    /**
     * Get the scale factor for the overall to-hit value when calculating the chance for a mêlée critical
     *
     * @return the scale factor for the overall to-hit value when calculating the chance for a mêlée critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceTohScale() {
        return chanceTohScale;
    }

    private static void setChanceTohScale(int chanceTohScale) {
        // No checks on this value - just let it through
        MeleeCriticalConstants.chanceTohScale = chanceTohScale;
    }

    /**
     * Get the scale factor for player level when calculating the chance for a mêlée critical
     *
     * @return the scale factor for player level when calculating the chance for a mêlée critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceLevelScale() {
        return chanceLevelScale;
    }

    private static void setChanceLevelScale(int chanceLevelScale) {
        // No checks on this value - just let it through
        MeleeCriticalConstants.chanceLevelScale = chanceLevelScale;
    }

    /**
     * Get the scale factor for the to-hit scale when calculating the chance for a mêlée critical
     *
     * @return the scale factor for the to-hit scale when calculating the chance for a mêlée critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceTohSkillScale() {
        return chanceTohSkillScale;
    }

    private static void setChanceTohSkillScale(int chanceTohSkillScale) {
        // No checks on this value - just let it through
        MeleeCriticalConstants.chanceTohSkillScale = chanceTohSkillScale;
    }

    /**
     * Return the value added to the chance for a mêlée critical
     *
     * @return the value added to the chance for a mêlée critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceOffset() {
        return chanceOffset;
    }

    private static void setChanceOffset(int chanceOffset) {
        // No checks on this value - just let it through
        MeleeCriticalConstants.chanceOffset = chanceOffset;
    }

    /**
     * Get the maximum range for mêlée critical chance<br><br>
     * <p>
     * If the chance is N and the maximum range is M, then the probability a critical will happen is
     * min(max(0, N), M) / M<br><br>
     * <p>
     * To avoid extra inaccuracies for the damage shown within object descriptions, this should be a multiple of 100
     *
     * @return the maximum range for mêlée critical chance
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceRange() {
        return chanceRange;
    }

    private static void setChanceRange(int chanceRange, String name) throws InvalidTokenFoundDuringParse {
        // We must have a non-zero number which is a multiple of 100

        if (chanceRange == 0) {
            String message = "Invalid chance-range value, must be non-zero. Token was: "
                    + tag + ":" + name + ":" + chanceRange;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        } else if ((chanceRange % 100) != 0) {
            String message = "Invalid chance-range value, should be a multiple of 100. Token was: "
                    + tag + ":" + name + ":" + chanceRange;
            logger.warn(message);
        }

        MeleeCriticalConstants.chanceRange = chanceRange;
    }

    /**
     * Get the scale factor for the weapon weight in the power of a mêlée critical
     *
     * @return the scale factor for the weapon weight in the power of a mêlée critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getPowerWeightScale() {
        return powerWeightScale;
    }

    private static void setPowerWeightScale(int powerWeightScale) {
        // No checks on this value - just let it through
        MeleeCriticalConstants.powerWeightScale = powerWeightScale;
    }

    /**
     * Get the maximum of the random part of the power for a mêlée critical
     *
     * @return the maximum of the random part of the power for a mêlée critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getPowerRandom() {
        return powerRandom;
    }

    private static void setPowerRandom(int powerRandom) {
        // No checks on this value - just let it through
        MeleeCriticalConstants.powerRandom = powerRandom;
    }
}