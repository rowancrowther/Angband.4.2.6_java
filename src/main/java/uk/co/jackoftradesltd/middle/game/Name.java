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

package uk.co.jackoftradesltd.middle.game;

import java.util.Collections;
import java.util.List;

/**
 * One section of the name file — the domain form a {@code NamesParseRecord} becomes once
 * {@code uk.co.jackoftradesltd.backend.parser.names.NamesAssembler} has parsed its section number
 * into an {@code int} and copied its words. This is the per-record building block C assembles
 * straight into {@code name_sections} inside {@code finish_parse_names} ({@code init.c}); here the
 * assembly is left to {@code MiscRegistry.setNames}, which flattens every {@code Name} sharing a
 * section into that section's word list, keyed by
 * {@link uk.co.jackoftradesltd.middle.player.enums.RandnameType}.
 *
 * <p>{@code section} is carried as the raw {@code int} the name file wrote, not yet the
 * {@link uk.co.jackoftradesltd.middle.player.enums.RandnameType} it will become — validating and
 * converting it is {@link uk.co.jackoftradesltd.middle.player.enums.RandnameType#fromIndex}'s job,
 * called from {@code MiscRegistry.setNames} rather than here, so a record with a bad section can
 * still be built and only fails once it reaches the registry.
 *
 * <p>{@code word} is kept exactly as assembled, in file order. C's own list comes out reversed,
 * because {@code parse_names_word} ({@code init.c}) prepends each word to a linked list that
 * {@code finish_parse_names} then walks head first. The difference has no effect on either
 * version: the one consumer, {@code build_prob} ({@code randname.c}), counts letter transitions
 * per word and does not care what order the words arrive in.
 *
 * <p>Class Name coded on 260902, commented in full on 260908.
 *
 * @author Rowan Crowther
 */
public class Name {
    private int section;
    private List<String> word;

    /**
     * Builds one section record. Neither argument is validated or copied here — {@code word} is
     * kept by reference, and a {@code section} outside {@link
     * uk.co.jackoftradesltd.middle.player.enums.RandnameType}'s range is only rejected once
     * {@code MiscRegistry.setNames} tries to resolve it.
     *
     * <p>Constructor Name coded on 260902, commented in full on 260908.
     *
     * @param section the raw section number from the name file
     * @param word    the section's word fragments, in file order
     */
    public Name(int section, List<String> word) {
        this.section = section;
        this.word = word;
    }

    /**
     * The raw section number this record was filed under — C's {@code names_parse.section} at the
     * time this record's words were read, equivalently the value a {@link
     * uk.co.jackoftradesltd.middle.player.enums.RandnameType} would carry if this number is valid.
     *
     * <p>Method getSection coded on 260902, commented in full on 260908.
     *
     * @return the section number
     */
    public int getSection() {
        return section;
    }

    /**
     * This section's word fragments, in file order — C's {@code name_sections[section]}, minus the
     * terminating {@code NULL} entry the Java side has no need for.
     *
     * <p>Method getWord coded on 260902, commented in full on 260908.
     *
     * @return an unmodifiable view of the words
     */
    public List<String> getWord() {
        return Collections.unmodifiableList(word);
    }
}
