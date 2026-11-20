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

package uk.co.jackoftradesltd.backend.parser.bloweffect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.monsters.BlowEffect;
import uk.co.jackoftradesltd.middle.monsters.enums.BlowEffectType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw records of {@code blow_effects.txt} into {@link BlowEffect}s, one per record, in file
 * order. It is the Java form of C's {@code parse_eff_*} handlers in {@code mon-init.c}
 * ({@code init_parse_eff} registers them) and of the list that {@code finish_parse_eff} builds from
 * them.
 *
 * <p><b>Shape.</b> C runs each handler as its line is read, on the record {@code parse_eff_name} has
 * just allocated, so the first bad line ends the load there. Here {@code BlowEffect2Grammar} has
 * already cut the file into {@link BlowEffect2ParseRecord}s of raw {@link Directive}s, and this class
 * walks each record's directives in file order, which is the order C's handlers saw them in. The
 * first refusal throws a {@link DataFileParseException} that quotes the line and the file, and the
 * load ends, as C's first parser error ends the file.
 *
 * <p><b>Ordering.</b> A {@code resist:} is resolved when its directive is reached, against the
 * {@code effect-type:} read so far, as {@code parse_eff_resist} resolves it against the
 * {@code effect_type} string stored so far. Every directive other than {@code name:} is refused if
 * it arrives before a non-empty name.
 *
 * <p><b>Directive names.</b> The grammar writes {@code name}, {@code power}, {@code eval},
 * {@code desc}, {@code base-colour}, {@code resist-colour}, {@code immune-colour}, {@code effect},
 * {@code resist} and {@code lash}. Five of those differ from C's keywords ({@code lore-color-base},
 * {@code lore-color-resist}, {@code lore-color-immune}, {@code effect-type} and
 * {@code lash-type}); that is an accepted deviation, because only the grammar writes the names and
 * only this class reads them.
 *
 * <p><b>Accepted deviations from C</b>, all recorded in {@code docs/Accepted_Deviations.md} (Blow
 * effect section):
 * <ul>
 *   <li>an unknown {@code effect-type:} is refused, where C stores any string;</li>
 *   <li>a {@code resist:} before any {@code effect-type:} is refused, where C reads a null pointer;</li>
 *   <li>an unknown {@code resist:} name is stored as the NONE constants, where C stores -1;</li>
 *   <li>{@code lash-type:MAX} and an element {@code resist:MAX} find C's trailing {@code MAX} name
 *       but no projection record;</li>
 *   <li>a changed {@code effect-type:} clears the resist the new type does not use, where C keeps
 *       its one {@code resist} int;</li>
 *   <li>building a {@link BlowEffect} needs the projections loaded, where C's zeroed struct needs
 *       nothing.</li>
 * </ul>
 *
 * <p><b>Depends on the projections.</b> C keeps a {@code lash-type:} as a bare projection index and
 * loads {@code projection.txt} ahead of {@code blow_effects.txt} ({@code init.c} lists
 * {@code projections} before {@code blow effects}). Here the lash is a {@link Projection} record, so
 * the projections must already be loaded. Every record needs them, not only one with a
 * {@code lash:} directive: each record starts from {@code new BlowEffect()}, whose default lash is
 * the ACID projection (C's zeroed {@code lash_type} 0), found through
 * {@link WorldRegistry#lookupProjectionByCode}. With the projections missing, the first record
 * fails with an {@link IllegalStateException} before any of its directives is read. A
 * {@code lash:} directive then replaces that default, resolved through
 * {@link WorldRegistry#lookupProjectionByLash}.
 *
 * <p><b>The list.</b> It holds only the real records, in file order. C prepends each record to a
 * chain and {@code finish_parse_eff} turns the chain back into file order, with one blank slot after
 * the last record; the port has no blank slot and its count is the plain record count (accepted
 * deviation, decided 261009).
 *
 * <p>Class BlowEffect2Assembler coded before 261117, commented in full on 261118.
 *
 * @author Rowan Crowther
 */
public class BlowEffect2Assembler implements Assembler<BlowEffect2ParseRecord, List<BlowEffect>> {
    /**
     * Receives the message of each fatal path, at fatal level, just before the
     * {@link DataFileParseException} carrying the same text is thrown. Nothing else is logged.
     *
     * <p>Field logger coded on 261114, commented in full on 261114.
     */
    private static final Logger logger = LogManager.getLogger(BlowEffect2Assembler.class);

    /**
     * Builds one {@link BlowEffect} from each record, in the order the records are given. Each
     * record starts from a fresh {@code new BlowEffect()}, which is C's zero-filled
     * {@code mem_zalloc} in {@code parse_eff_name} (its lash the ACID projection, C's zeroed
     * {@code lash_type} 0, so the projections must be loaded), and its directives are applied in
     * file order by the handler for each {@link Directive#name()}:
     * <ul>
     *   <li>{@code name}: {@code parseName}, with no name check;</li>
     *   <li>{@code power}, {@code eval}, {@code desc}, {@code base-colour}, {@code resist-colour},
     *       {@code immune-colour}, {@code effect}, {@code resist} and {@code lash}: {@code checkForName}
     *       first, then {@code parsePower}, {@code parseEval}, {@code parseDesc},
     *       {@code parseBaseColour}, {@code parseResistColour}, {@code parseImmuneColour},
     *       {@code parseEffect}, {@code parseResist} and {@code parseLash}.</li>
     * </ul>
     * A directive name outside those ten throws a {@link DataFileParseException}
     * ("an unknown directive"). The grammar never writes one, so only a hand-built record reaches
     * that path.
     *
     * <p>The grammar starts a record at each {@code name:}, so a record holds one name. This method
     * does not split a hand-built record that holds two: the second {@code name} overwrites the
     * first, where C would start another record. A record with no directives at all gives a default
     * {@link BlowEffect} with an empty name; the grammar cannot build one, because it requires
     * {@code name:} first. An empty list of records gives an empty list.
     *
     * <p>Function assemble coded before 261117, commented in full on 261118.
     *
     * @param records  the records read from the file, in file order
     * @param filename the data file's name, quoted in every refusal message
     * @return one effect per record, in file order
     * @throws DataFileParseException if a directive is refused, which ends the load
     * @throws IllegalStateException  if there is at least one record and the projections have not
     *                                been loaded
     */
    @Override
    public List<BlowEffect> assemble(@NotNull List<BlowEffect2ParseRecord> records, String filename) {
        List<BlowEffect> results = new ArrayList<>();
        
        for (BlowEffect2ParseRecord record : records) {
            List<Directive> directives = record.directives();
            BlowEffect effect = new BlowEffect();
            for (Directive directive : directives) {
                switch (directive.name()) {
                    case "name" -> {
                        parseName(effect, directive, filename);
                    }
                    case "power" -> {
                        checkForName(effect, directive, filename);
                        parsePower(effect, directive, filename);
                    }
                    case "eval" -> {
                        checkForName(effect, directive, filename);
                        parseEval(effect, directive, filename);
                    }
                    case "desc" -> {
                        checkForName(effect, directive, filename);
                        parseDesc(effect, directive, filename);
                    }
                    case "base-colour" -> {
                        checkForName(effect, directive, filename);
                        parseBaseColour(effect, directive, filename);
                    }
                    case "resist-colour" -> {
                        checkForName(effect, directive, filename);
                        parseResistColour(effect, directive, filename);
                    }
                    case "immune-colour" -> {
                        checkForName(effect, directive, filename);
                        parseImmuneColour(effect, directive, filename);
                    }
                    case "effect" -> {
                        checkForName(effect, directive, filename);
                        parseEffect(effect, directive, filename);
                    }
                    case "resist" -> {
                        checkForName(effect, directive, filename);
                        parseResist(effect, directive, filename);
                    }
                    case "lash" -> {
                        checkForName(effect, directive, filename);
                        parseLash(effect, directive, filename);
                    }
                    default -> {
                        String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                                "an unknown directive: " + directive.name();
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                }
            }
            
            results.add(effect);
        }
        
        return results;
    }
    
    /**
     * Refuses a directive that arrives before the record's name. C's handlers need a record to work
     * on: {@code parse_eff_power}, {@code parse_eff_eval}, {@code parse_eff_desc},
     * {@code parse_eff_effect_type}, {@code parse_eff_resist} and {@code parse_eff_lash_type}
     * {@code assert(eff)}, and the three {@code parse_eff_lore_color*} handlers return
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER}. Either way nothing loads, which is what this refusal
     * does.
     *
     * <p>A record counts as named when its effect's name is non-empty. Of the three things that
     * could be null or empty here, {@code effect} is never null when {@code assemble} calls it, and
     * {@code effect.getName()} is the empty string from the {@code BlowEffect()} constructor until a
     * {@code name} directive sets it. It is null only if a hand-built {@code name} directive carried
     * a null value. The grammar cannot write an empty name, so an empty one is also a hand-built
     * case: every later directive in that record is then refused as "before the name directive".
     *
     * <p>Function checkForName coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built for the current record
     * @param directive the directive about to be applied, quoted in the message
     * @param filename  the data file's name, quoted in the message
     * @throws DataFileParseException if the effect has no name yet
     */
    private void checkForName(BlowEffect effect, Directive directive, String filename) {
        if (effect == null || effect.getName() == null || effect.getName().isEmpty()) {
            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "a directive: " + directive.name() + " before the name directive has been found";
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
    }
    
    /**
     * Sets the effect's name, as {@code parse_eff_name} does with {@code string_make}. C also
     * allocates the record and links it into the chain; here {@code assemble} has already built the
     * effect. Neither version checks for a duplicate name.
     *
     * <p>Exactly one value is required. With none, or two or more, the load is refused and the
     * message lists the values found, separated by a comma and a space, so none gives an empty
     * list. C refuses a missing field with {@code PARSE_ERROR_MISSING_FIELD}.
     *
     * <p>Function parseName coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built
     * @param directive the {@code name} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the directive does not carry exactly one value
     */
    private void parseName(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of name values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        effect.setName(directive.values().getFirst());
    }

    /**
     * Sets the effect's power, as {@code parse_eff_power} does with {@code parser_getint}. The text is
     * converted by {@code ParseUtils.parseIntString}, so the way a malformed or out-of-range number is
     * refused is that method's, not this one's. Exactly one value is required; see
     * {@link #parseName} for what a wrong count does. A repeated {@code power:} overwrites the
     * earlier one, in C and here.
     *
     * <p>Function parsePower coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built
     * @param directive the {@code power} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the count is wrong or the number is refused
     */
    private void parsePower(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of power values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        int power = ParseUtils.parseIntString("Blow Effect", directive.values().getFirst(), directive.line(),
                filename, "power");
        effect.setPower(power);
    }

    /**
     * Sets the effect's evaluation weight, as {@code parse_eff_eval} does with
     * {@code parser_getint}. It is read and stored exactly as {@link #parsePower} reads power; the
     * only difference is the word "power evaluation" in the refusal messages.
     *
     * <p>Function parseEval coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built
     * @param directive the {@code eval} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the count is wrong or the number is refused
     */
    private void parseEval(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of power evaluation values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        int eval = ParseUtils.parseIntString("Blow Effect", directive.values().getFirst(), directive.line(),
                filename, "power evaluation");
        effect.setEval(eval);
    }
    
    /**
     * Appends the value to the effect's description, as {@code parse_eff_desc} does with
     * {@code string_append}, so two {@code desc:} lines in one record join end to end with nothing
     * between them. The effect starts with the empty string where C starts with a null pointer, so a
     * record with no {@code desc:} line ends with the empty string. Exactly one value is required;
     * see {@link #parseName} for what a wrong count does.
     *
     * <p>Function parseDesc coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built
     * @param directive the {@code desc} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the directive does not carry exactly one value
     */
    private void parseDesc(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of desc values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        effect.setDesc(effect.getDesc() + directive.values().getFirst());
    }

    /**
     * Sets the colour used when the player resists the blow, as {@code parse_eff_lore_color_resist}
     * does for {@code lore_attr_resist}. The value is resolved by {@link #parseColour}. Exactly one
     * value is required; see {@link #parseName} for what a wrong count does.
     *
     * <p>Function parseResistColour coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built
     * @param directive the {@code resist-colour} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the directive does not carry exactly one value
     */
    private void parseResistColour(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of resist colour values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        ColourEnum value = parseColour(directive.values().getFirst());

        effect.setLoreAttrResist(value);
    }
    
    /**
     * Sets the colour used when no protection applies, as {@code parse_eff_lore_color} does for
     * {@code lore_attr}. The value is resolved by {@link #parseColour}. Exactly one value is
     * required; see {@link #parseName} for what a wrong count does.
     *
     * <p>Function parseBaseColour coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built
     * @param directive the {@code base-colour} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the directive does not carry exactly one value
     */
    private void parseBaseColour(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of base colour values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        ColourEnum value = parseColour(directive.values().getFirst());

        effect.setLoreAttr(value);
    }

    /**
     * Sets the colour used when the player is immune, as {@code parse_eff_lore_color_immune} does
     * for {@code lore_attr_immune}. The value is resolved by {@link #parseColour}. Exactly one value
     * is required; see {@link #parseName} for what a wrong count does.
     *
     * <p>Function parseImmuneColour coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built
     * @param directive the {@code immune-colour} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the directive does not carry exactly one value
     */
    private void parseImmuneColour(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of immune colour values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        ColourEnum value = parseColour(directive.values().getFirst());

        effect.setLoreAttrImmune(value);
    }
    
    /**
     * Resolves a lore colour as the three {@code parse_eff_lore_color*} handlers do: a value longer
     * than one character is a colour name ({@code color_text_to_attr}, ignoring case), and a
     * single character is an index code ({@code color_char_to_attr}, case-sensitive). The steps are:
     * <ol>
     *   <li>an empty value, or a null one, gives {@link ColourEnum#COLOUR_DARK}, which is what
     *       {@code color_char_to_attr} returns for the {@code '\0'} of an empty string;</li>
     *   <li>otherwise {@link ColourEnum#fromCode(String)} resolves it, so {@code "Light Green"},
     *       {@code "light green"} and {@code "G"} agree with C;</li>
     *   <li>a value that matches no colour gives {@link ColourEnum#COLOUR_WHITE}, as C's
     *       "Default to white" and "we assume white" do, where {@code fromCode} itself answers null.
     *       {@code "Shade"} is one such value: C has no colour of that name, and neither does
     *       {@link ColourEnum}.</li>
     * </ol>
     *
     * <p>The one refusal C has here, {@code PARSE_ERROR_INVALID_COLOR}, fires only for a
     * single-character value whose {@code char} is negative (a byte with the high bit set, on a
     * platform where {@code char} is signed), because {@code color_char_to_attr} hands a negative
     * character straight back. An unknown name is never refused in C. This method has no
     * counterpart for the negative case: a character that matches no colour is white.
     *
     * <p>Function parseColour coded before 261117, commented in full on 261117, updated on 261120
     * for the removal of {@code COLOUR_SHADE}.
     *
     * @param colour the raw colour text from the directive, which may be null or empty
     * @return the colour, never null
     */
    private ColourEnum parseColour(String colour) {
        if (colour == null || colour.isEmpty()) {
            return ColourEnum.COLOUR_DARK;
        }
        
        ColourEnum result = ColourEnum.fromCode(colour);
        if (result == null) {
            return ColourEnum.COLOUR_WHITE;
        }
        
        return result;
    }
    
    /**
     * Sets what kind of player attribute protects against the blow, as {@code parse_eff_effect_type}
     * stores {@code effect_type}. C keeps the text unchecked; the port resolves it, with
     * {@link BlowEffectType#getFromString}, to one of the seven spellings the data file uses
     * ({@code element}, {@code flag}, {@code drain}, {@code theft}, {@code eat-food},
     * {@code eat-light}, {@code all_sustains}) and refuses anything else, including a wrong case.
     * Exactly one value is required; see {@link #parseName} for what a wrong count does.
     *
     * <p>The type then decides which resist fields the effect keeps, where C keeps its one
     * {@code resist} int whatever the type:
     * <ul>
     *   <li>{@code element}: clears the object-flag resist and leaves the element and projection
     *       resists as they are;</li>
     *   <li>{@code flag}: clears the element and projection resists and leaves the object-flag
     *       resist;</li>
     *   <li>any other type: clears all three, because none of them is read.</li>
     * </ul>
     * A repeated line of the same type therefore keeps its resist, and a different type drops the
     * resist the earlier one set. Both the refusal of an unknown type and the clearing are accepted
     * deviations.
     *
     * <p>Function parseEffect coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built
     * @param directive the {@code effect} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the count is wrong or the type is not one of the seven
     */
    private void parseEffect(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of effect type values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        String value = directive.values().getFirst();
        
        BlowEffectType type = BlowEffectType.getFromString(value);
        if (type == null) {
            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an illegal effect-type value: " + value;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        effect.setEffectType(type);
        switch (type) {
            case BET_ELEMENT -> {
                effect.setObjectFlagResist(ObjectFlag.OF_NONE);
            }
            case BET_FLAG -> {
                effect.setProjectionResist(ProjectionEnum.PROJ_NONE);
                effect.setElementEnumResist(ElementEnum.ELEM_NONE);
            }
            default -> {
                effect.setProjectionResist(ProjectionEnum.PROJ_NONE);
                effect.setElementEnumResist(ElementEnum.ELEM_NONE);
                effect.setObjectFlagResist(ObjectFlag.OF_NONE);
            }
        }
    }
    
    /**
     * Resolves the {@code resist:} name against the effect's type, as {@code parse_eff_resist} does
     * against {@code effect_type}. C stores one int, a projection index for {@code element} and an
     * object-flag index for {@code flag}; the port keeps three typed fields and fills the ones the
     * type uses. Exactly one value is required; see {@link #parseName} for what a wrong count does.
     *
     * <p>The type read so far decides the branch:
     * <ul>
     *   <li>{@code flag}: the name is looked up with {@link ObjectFlag#lookup}, which is exact and
     *       case-sensitive, as C's {@code flag_index_by_name} is. A name it does not find, or
     *       {@code MAX}, is stored as {@link ObjectFlag#OF_NONE}, where C stores -1. The element and
     *       projection resists are cleared;</li>
     *   <li>{@code element}: the name is looked up as a projection, with
     *       {@link ProjectionEnum#lookup}, ignoring case, as {@code proj_name_to_idx} does, and
     *       as an element, with {@link ElementEnum#lookup(String, boolean)} ignoring case. A name
     *       that is no element, or {@code MAX}, stores {@link ElementEnum#ELEM_NONE}, and a name that is no
     *       projection stores {@link ProjectionEnum#PROJ_NONE}. The object-flag resist is cleared;</li>
     *   <li>any other type ({@code drain}, {@code theft}, {@code eat-food}, {@code eat-light},
     *       {@code all_sustains}): the load is refused, as C returns
     *       {@code PARSE_ERROR_MISSING_BLOW_EFF_TYPE}.</li>
     * </ul>
     *
     * <p>With no {@code effect-type:} read yet, the type is null and the load is refused. C reads the
     * missing type with {@code streq} on a null pointer and crashes, so it has no answer to match.
     * Which link is null here is {@code effect.getEffectType()}, the effect's own type, since the
     * effect itself is never null. The unknown-name and {@code MAX} outcomes, and this refusal, are
     * accepted deviations; the lookups' own differences from C are on {@code ObjectFlag.lookup},
     * {@code ElementEnum.lookup} and {@code ProjectionEnum.lookup}.
     *
     * <p>Function parseResist coded before 261117, commented in full on 261117.
     *
     * @param effect    the effect being built, whose type must already be set
     * @param directive the {@code resist} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the count is wrong, no type has been read, or the type is
     *                                neither {@code element} nor {@code flag}
     */
    private void parseResist(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of resist values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        BlowEffectType effectType = effect.getEffectType();
        String value = directive.values().getFirst();
        
        if (effectType == null) {
            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "a resist before a effect type has been found";
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        switch (effectType) {
            case BET_FLAG -> {
                ObjectFlag flag = ObjectFlag.lookup(value);
                if (flag == null || flag == ObjectFlag.OF_MAX) {
                    flag = ObjectFlag.OF_NONE;
                }
                
                effect.setObjectFlagResist(flag);
                effect.setElementEnumResist(ElementEnum.ELEM_NONE);
                effect.setProjectionResist(ProjectionEnum.PROJ_NONE);
            }
            case BET_ELEMENT -> {
                ProjectionEnum projectEnum = ProjectionEnum.lookup(value);
                ElementEnum elementEnum = ElementEnum.lookup(value, true);
                if (elementEnum == null) {
                    elementEnum = ElementEnum.ELEM_NONE;
                }
                if (elementEnum == ElementEnum.ELEM_MAX) {
                    elementEnum = ElementEnum.ELEM_NONE;
                }
                if (projectEnum == null) {
                    projectEnum = ProjectionEnum.PROJ_NONE;
                }
                
                effect.setElementEnumResist(elementEnum);
                effect.setObjectFlagResist(ObjectFlag.OF_NONE);
                effect.setProjectionResist(projectEnum);
            }
            default -> {
                String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                        "a resist value found on an illegal effect-type: " + effectType.name();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }
    
    /**
     * Sets the projection the effect becomes in its lash form, as {@code parse_eff_lash_type} does.
     * C turns the name into a projection index with {@code proj_name_to_idx} and stores
     * {@code PROJ_MISSILE} when there is none. The steps here are:
     * <ol>
     *   <li>{@link ProjectionEnum#lookup} finds the name, ignoring case; a name it does not find
     *       gives {@link ProjectionEnum#PROJ_MISSILE};</li>
     *   <li>{@link ProjectionEnum#PROJ_NONE}, which C's name list does not hold, also gives
     *       {@code PROJ_MISSILE}, so {@code NONE} and any unknown name end up the same in C and
     *       here;</li>
     *   <li>{@link WorldRegistry#lookupProjectionByLash} then finds the {@link Projection} record for
     *       that code and the effect stores it. If no record has the code, as with {@code MAX}, the
     *       stored lash is null, where C stores {@code MAX}'s index.</li>
     * </ol>
     * Exactly one value is required; see {@link #parseName} for what a wrong count does. A
     * record with no {@code lash:} line keeps the lash it started with, the ACID projection that
     * {@code new BlowEffect()} holds, which is what C's zeroed {@code lash_type} 0 names. A repeated
     * {@code lash:} line replaces the earlier one, in C and here.
     *
     * <p>The projections must already be loaded, and by the time this runs they are: the effect's
     * constructor has already looked up the ACID projection through {@code WorldRegistry}, which
     * throws an {@link IllegalStateException} otherwise. C has no such dependency because it stores
     * only the index.
     *
     * <p>Function parseLash coded before 261117, commented in full on 261118.
     *
     * @param effect    the effect being built
     * @param directive the {@code lash} directive
     * @param filename  the data file's name, quoted in a refusal
     * @throws DataFileParseException if the directive does not carry exactly one value
     * @throws IllegalStateException  if the projections have not been loaded, which in practice
     *                                {@code assemble} has already caught when it built the effect
     */
    private void parseLash(BlowEffect effect, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder values = new StringBuilder();
            for (String value : directive.values()) {
                values.append(value);
                values.append(", ");
            }
            if (values.length() > 1) values = new StringBuilder(values.substring(0, values.length() - 2));

            String msg = "Blow Effect at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of lash values (should be 1): " + values;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        ProjectionEnum lashProjectionEnum = ProjectionEnum.lookup(directive.values().getFirst());
        if (lashProjectionEnum == null) { 
            lashProjectionEnum = ProjectionEnum.PROJ_MISSILE;
        }
        if (lashProjectionEnum == ProjectionEnum.PROJ_NONE) {
            lashProjectionEnum = ProjectionEnum.PROJ_MISSILE;
        }
        
        Projection lashProjection = WorldRegistry.lookupProjectionByLash(lashProjectionEnum);
        
        effect.setLashType(lashProjection);
    }
}
