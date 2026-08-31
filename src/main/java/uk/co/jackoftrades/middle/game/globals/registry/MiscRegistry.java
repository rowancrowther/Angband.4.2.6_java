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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import uk.co.jackoftrades.middle.game.Hint;
import uk.co.jackoftrades.middle.game.Name;
import uk.co.jackoftrades.middle.objects.FlavourKind;
import uk.co.jackoftrades.middle.player.enums.RandnameType;

import java.util.*;

/**
 * Runtime holder for the loose "misc" game data that does not belong to any of the larger domain
 * slices — the loading hints, the random name lists, and the object flavours (the randomised
 * appearance descriptions for unidentified potions, rings, and the like).
 *
 * <p>This is the read side of the misc slice: it is populated once at startup by
 * {@link uk.co.jackoftrades.middle.game.globals.loaders.MiscDataLoader} and thereafter only read.
 * It was split out of {@code GameConstants} as one domain slice of the loader/registry refactor,
 * grouping the three otherwise-homeless data types rather than leaving them among every other type.
 *
 * <p>Each getter returns an unmodifiable view of its list and assumes the loader has already run.
 * Because these are whole-list getters rather than searches, a query before load simply throws a
 * {@link NullPointerException} from {@code unmodifiableList} rather than silently masking the
 * missing load — so no explicit "not initialised" guard is carried here (contrast the search-style
 * lookups in the other registries, where an unloaded list would degrade to a false "not found").
 *
 * @author Rowan Crowther
 */
public class MiscRegistry {
    private static final Logger logger = LogManager.getLogger(MiscRegistry.class);

    private static final Map<RandnameType, List<String>> nameSections = new HashMap<>();

    /**
     * The loaded loading hints.
     */
    private static List<Hint> hints;
    /**
     * The loaded random name lists.
     */
    private static List<Name> names;
    /**
     * The loaded object flavours (randomised appearances of unidentified items).
     */
    private static List<FlavourKind> flavours;

    /**
     * @return an unmodifiable view of the loaded loading hints
     */
    @Unmodifiable
    @Contract(pure = true)
    @CheckReturnValue
    public static List<Hint> getHints() {
        return Collections.unmodifiableList(hints);
    }

    /**
     * @return an unmodifiable view of the loaded random name lists
     */
    @Unmodifiable
    @Contract(pure = true)
    @CheckReturnValue
    public static List<Name> getNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * @return an unmodifiable view of the loaded object flavours
     */
    @Unmodifiable
    @Contract(pure = true)
    @CheckReturnValue
    public static List<FlavourKind> getFlavours() {
        return Collections.unmodifiableList(flavours);
    }

    /**
     * Stores the loaded loading hints; set once by {@code MiscDataLoader}.
     */
    public static void setHints(@NotNull List<Hint> hints) {
        MiscRegistry.hints = hints;
    }

    /**
     * Stores the loaded random name lists and, from them, builds the per-section word lists the
     * random name generator learns from; set once by {@code MiscDataLoader}.
     *
     * <p>This is the port of C's {@code finish_parse_names} ({@code init.c}), which flattens the
     * words the parser gathered into {@code name_sections}, an array of word lists indexed by
     * section number. The map built here stands in for that array: one entry per
     * {@link RandnameType}, each holding every word of that section in one flat list, so a caller
     * asks for a section rather than walking the {@link Name} records itself. Every entry is
     * re-created on each call, so a second load replaces the previous word lists rather than
     * adding to them.
     *
     * <p>C's array is three wide and its slot zero is never read — {@code randname_make}
     * ({@code randname.c}) asserts a type above zero — yet {@code parse_names_section} accepts
     * section zero and files those words there. A section outside the usable range is rejected
     * here instead, by way of {@link RandnameType#fromIndex} returning {@code null}; the shipped
     * {@code names.txt} opens with {@code section:1} and uses only sections one and two, so no
     * real data file parts the two versions. The map likewise carries an entry for
     * {@link RandnameType#RANDNAME_NUM_TYPES}, the end-of-type marker, which stays empty and is
     * never read — the counterpart of C's unread slot zero.
     *
     * <p>Words are appended in file order. C prepends each word to a linked list and then walks
     * that list, so its sections come out in reverse file order; the difference is invisible
     * because the only consumer, {@code build_prob} ({@code randname.c}), counts letter
     * transitions and so is indifferent to the order the words arrive in.
     *
     * <p>Method setNames coded on 260831, commented in full on 260831.
     *
     * @param names the assembled name records, one per section of the name file
     * @throws IllegalArgumentException if a record carries a section number that names no usable
     *         {@link RandnameType} — C's {@code PARSE_ERROR_OUT_OF_BOUNDS}, raised at load rather
     *         than at parse
     */
    public static void setNames(@NotNull List<Name> names) {
        MiscRegistry.names = names;

        for (RandnameType type : RandnameType.values()) {
            nameSections.put(type, new ArrayList<>());
        }

        for (Name name : names) {
            RandnameType section = RandnameType.fromIndex(name.getSection());
            if (section == null) {
                String message = "Index out of bounds - Name section found outside valid range.";
                logger.error(message);
                throw new IllegalArgumentException(message);
            }

            for (String nameString : name.getWord()) {
                nameSections.get(section).add(nameString);
            }
        }
    }

    /**
     * Returns every word of one section of the name file — the lookup C spells as
     * {@code name_sections[name_type]}, the word list {@code build_prob} ({@code randname.c})
     * learns its letter frequencies from.
     *
     * <p>The list is the one {@link #setNames} flattened, in file order, without C's terminating
     * {@code NULL} entry. Asking for {@link RandnameType#RANDNAME_NUM_TYPES} gives an empty list,
     * that marker having no words of its own; asking before the loader has run throws, in keeping
     * with the whole-list getters above.
     *
     * <p>Method getNameSection coded on 260831, commented in full on 260831.
     *
     * @param section the section wanted
     * @return an unmodifiable view of that section's words
     */
    public static List<String> getNameSection(RandnameType section) {
        return Collections.unmodifiableList(nameSections.get(section));
    }

    /**
     * Stores the loaded object flavours; set once by {@code MiscDataLoader}.
     */
    public static void setFlavours(@NotNull List<FlavourKind> flavours) {
        MiscRegistry.flavours = flavours;
    }
}
