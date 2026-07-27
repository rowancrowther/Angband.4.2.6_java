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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import uk.co.jackoftrades.middle.game.Hint;
import uk.co.jackoftrades.middle.game.Name;
import uk.co.jackoftrades.middle.objects.FlavourKind;

import java.util.Collections;
import java.util.List;

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
     * Stores the loaded random name lists; set once by {@code MiscDataLoader}.
     */
    public static void setNames(@NotNull List<Name> names) {
        MiscRegistry.names = names;
    }

    /**
     * Stores the loaded object flavours; set once by {@code MiscDataLoader}.
     */
    public static void setFlavours(@NotNull List<FlavourKind> flavours) {
        MiscRegistry.flavours = flavours;
    }
}
