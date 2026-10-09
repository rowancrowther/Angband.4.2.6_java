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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import uk.co.jackoftradesltd.middle.game.Hint;
import uk.co.jackoftradesltd.middle.game.Name;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;
import uk.co.jackoftradesltd.middle.player.enums.RandnameType;

import java.util.*;

/**
 * Runtime holder for the loose "misc" game data that does not belong to any of the larger domain
 * slices — the loading hints, the random name lists, and the object flavours (the randomized
 * appearance descriptions for unidentified potions, rings, and the like).
 *
 * <p>This is the read side of the misc slice: it is populated once at startup by
 * {@link uk.co.jackoftradesltd.middle.game.globals.loaders.MiscDataLoader} and thereafter only read.
 * It was split out of {@code GameConstants} as one domain slice of the loader/registry refactor,
 * grouping the three otherwise-homeless data types rather than leaving them among every other type.
 *
 * <p>Each getter returns an unmodifiable view of its list and assumes the loader has already run.
 * Because these are whole-list getters rather than searches, a query before load simply throws a
 * {@link NullPointerException} from {@code unmodifiableList} rather than silently masking the
 * missing load — so no explicit "not initialized" guard is carried here (contrast the search-style
 * lookups in the other registries, where an unloaded list would degrade to a false "not found").
 *
 * <p>Each of the three lists stands in for a C global that the matching {@code finish_parse_*}
 * function ({@code init.c}) fills when the data file has been read:
 * <ul>
 *   <li>{@code hints} ({@code store.c}), set by {@code finish_parse_hints} — here {@link #setHints};</li>
 *   <li>{@code name_sections} ({@code randname.c}), built by {@code finish_parse_names} — here
 *       {@link #setNames}, read back through {@link #getNameSection};</li>
 *   <li>{@code flavors} ({@code obj-util.c}), set by {@code finish_parse_flavor} — here
 *       {@link #setFlavours}.</li>
 * </ul>
 * C's {@code cleanup_*} counterparts have no equivalent: nothing here is freed, and a second call to
 * a setter simply replaces what the first stored.
 *
 * <p>None of the three lists has a {@code z_info} counter in C, so the blank-slot deviation that
 * applies to the larger registries (a C list that starts or ends on an empty record, with its
 * counter one above the record count) does not arise here.
 *
 * <p>Class MiscRegistry coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class MiscRegistry {
    /**
     * Reports a name file section that names no usable {@link RandnameType}.
     *
     * <p>Field logger commented in full on 261009.
     */
    private static final Logger logger = LogManager.getLogger(MiscRegistry.class);

    /**
     * Every word of the name file, flattened to one list per section — the stand-in for C's
     * {@code name_sections} ({@code randname.c}), an array of word lists indexed by section number.
     * Keyed by {@link RandnameType}, so the {@link RandnameType#RANDNAME_NUM_TYPES} marker has an
     * entry of its own, always empty. Rebuilt in full by {@link #setNames}, and empty before the
     * first call; read through {@link #getNameSection}.
     *
     * <p>Field nameSections coded on 260831, commented in full on 261009.
     */
    private static final Map<RandnameType, List<String>> nameSections = new HashMap<>();

    /**
     * The loaded loading hints, in file order — C's {@code hints} ({@code store.c}), which holds the
     * same lines in reverse file order. Held by reference, not copied. {@code null} until
     * {@link #setHints} runs; C's {@code hints} is {@code NULL} until then, and also when the hint
     * file has no {@code H:} line, which here is an empty list instead.
     *
     * <p>Field hints coded before 261009, commented in full on 261009.
     */
    private static List<Hint> hints;
    /**
     * The loaded random name records, one per {@code section:} block of the name file, exactly as
     * {@link #setNames} was given them. C keeps no such list — it flattens the records straight into
     * {@code name_sections} — so this is the Java-only record the {@link #nameSections} map is built
     * from. Held by reference, not copied. {@code null} until {@link #setNames} runs.
     *
     * <p>Field names coded before 261009, commented in full on 261009.
     */
    private static List<Name> names;
    /**
     * The loaded object flavours (randomized appearances of unidentified items), grouped one
     * {@link FlavourKind} per {@code kind:} block of {@code flavor.txt}. C's {@code flavors}
     * ({@code obj-util.c}) is a single flat list of {@code struct flavor}, each carrying its own
     * tval and glyph; the grouping here hoists those two shared values up, as {@link FlavourKind}
     * explains. Held by reference, not copied. {@code null} until {@link #setFlavours} runs.
     *
     * <p>Field flavours coded before 261009, commented in full on 261009.
     */
    private static List<FlavourKind> flavours;

    /**
     * The loading hints — the list C's {@code random_hint} ({@code ui-store.c}) draws from when a
     * shopkeeper offers a remark.
     *
     * <p>The view is of the list {@link #setHints} stored, in file order where C's is reversed; the
     * only reader, {@code random_hint}, treats every position alike, so the order makes no
     * difference. An empty hint file gives an empty list, where C would hold {@code NULL} and
     * {@code prt_welcome} would skip the remark. Asking before the loader has run throws a
     * {@link NullPointerException}.
     *
     * <p>Method getHints coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the loaded loading hints
     */
    @Unmodifiable
    @Contract(pure = true)
    @CheckReturnValue
    public static List<Hint> getHints() {
        return Collections.unmodifiableList(hints);
    }

    /**
     * The random name records as {@link #setNames} received them, one per {@code section:} block of
     * the name file. Callers wanting the words of one section should use {@link #getNameSection},
     * which has them already flattened; this is the unprocessed form, with no C counterpart.
     *
     * <p>Asking before the loader has run throws a {@link NullPointerException}.
     *
     * <p>Method getNames coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the loaded random name records
     */
    @Unmodifiable
    @Contract(pure = true)
    @CheckReturnValue
    public static List<Name> getNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * The object flavours, grouped by the object type they disguise — the Java form of C's flat
     * {@code flavors} list ({@code obj-util.c}), which {@code flavor_assign_fixed},
     * {@code flavor_assign_random} and {@code flavor_reset_fixed} each walk.
     *
     * <p>C's list is newest-first, so its kinds come out in reverse file order as well as its
     * flavours. Here the order inside a {@link FlavourKind} is reversed to match (see that class),
     * but the kinds themselves are in whatever order the loader stored. That cannot change a result:
     * every walk filters by tval, and the shipped {@code flavor.txt} gives each of its eight tvals
     * one {@code kind:} block. Asking before the loader has run throws a
     * {@link NullPointerException}.
     *
     * <p>Method getFlavours coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the loaded object flavours
     */
    @Unmodifiable
    @Contract(pure = true)
    @CheckReturnValue
    public static List<FlavourKind> getFlavours() {
        return Collections.unmodifiableList(flavours);
    }

    /**
     * Stores the loaded loading hints; set once by {@code MiscDataLoader}. This is the port of C's
     * {@code finish_parse_hints} ({@code init.c}), which does no more than assign the list the
     * parser built to the global {@code hints}.
     *
     * <p>The list is kept by reference, not copied, so a later change to it shows through
     * {@link #getHints}, and a second call replaces the first list outright. C's list is built by
     * prepending each {@code H:} line, so it holds them in reverse file order; this one keeps file
     * order, which {@code random_hint} ({@code ui-store.c}) cannot tell apart.
     *
     * <p>Method setHints coded before 261009, commented in full on 261009.
     *
     * @param hints the assembled hints, in file order
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
     * never read — an unread entry that keeps the map three wide, as C's array is, with the unread
     * one at the end where C's is at the front.
     *
     * <p>The check on each record's section happens while the words are being filed, after
     * {@code names} has been stored and the word lists cleared. A record that fails therefore leaves
     * the earlier records filed and the later ones not, a half-built state only a caller that
     * catches the exception can see. C's parse error stops the load, and the list is not used;
     * {@code MiscDataLoader.loadNames} does not catch the exception, so it reaches
     * {@code GameConstants.init}, which stops start-up the same way. Because {@code names} is stored
     * before the check, it still holds the bad record afterwards.
     *
     * <p>Words are appended in file order. C prepends each word to a linked list and then walks
     * that list, so its sections come out in reverse file order; the difference is invisible
     * because the only consumer, {@code build_prob} ({@code randname.c}), counts letter
     * transitions and so is indifferent to the order the words arrive in.
     *
     * <p>Method setNames coded on 260831, commented in full on 261009.
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
     * <p>A {@code null} section, like a call before the loader has run, throws a
     * {@link NullPointerException}.
     *
     * <p>Method getNameSection coded on 260831, commented in full on 261009.
     *
     * @param section the section wanted
     * @return an unmodifiable view of that section's words
     */
    public static List<String> getNameSection(RandnameType section) {
        return Collections.unmodifiableList(nameSections.get(section));
    }

    /**
     * Stores the loaded object flavours; set once by {@code MiscDataLoader}. This is the port of C's
     * {@code finish_parse_flavor} ({@code init.c}), which does no more than assign the list the
     * parser built to the global {@code flavors}.
     *
     * <p>The list is kept by reference, not copied, and a second call replaces the first list
     * outright. It is the one place the flavour data enters the game: {@code flavor_init} and its
     * helpers in {@code obj-util.c} read it back through {@link #getFlavours}, so a test that wants
     * a particular set of flavours seeds them here.
     *
     * <p>Method setFlavours coded before 261009, commented in full on 261009.
     *
     * @param flavours the assembled flavours, one {@link FlavourKind} per {@code kind:} block
     */
    public static void setFlavours(@NotNull List<FlavourKind> flavours) {
        MiscRegistry.flavours = flavours;
    }
}
