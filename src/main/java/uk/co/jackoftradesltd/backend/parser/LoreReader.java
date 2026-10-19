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

package uk.co.jackoftradesltd.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
//import uk.co.jackoftradesltd.backend.parser.lore.LoreGrammar;
//import uk.co.jackoftradesltd.backend.parser.lore.LoreLexer;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.monsters.MonsterLore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for {@code lib/user/lore.txt}, the player's per-save record of what they have learned about
 * each monster. It stands in for C's {@code lore_parser} in {@code mon-init.c}
 * ({@code init_parse_lore}, {@code run_parse_lore}, {@code finish_parse_lore} and
 * {@code cleanup_lore}), but today it is a parked stub: {@link #parse} returns an empty list and
 * reads nothing.
 *
 * <ul>
 *   <li>The parking is deliberate. Lore files are written by playing, so there is no real data to
 *       check a parser against yet, and the lore reader is the last item in
 *       {@code docs/ReaderPort.md}. It is not a port in progress.</li>
 *   <li>The body that would drive {@code LoreLexer} and {@code LoreGrammar} is commented out in
 *       {@link #parse}. No generated parser is committed under {@code backend/parser/lore}, and
 *       {@code GameConstants} keeps its {@code loadMonsterLore} call commented out, so nothing calls
 *       this class.</li>
 *   <li>Where the stub follows C: a missing lore file is not an error. {@code run_parse_lore}
 *       signals "No monster lore file found" and returns success, and the game carries on with the
 *       zeroed {@code l_list} that {@code init_parse_monster} allocated. An empty list for any
 *       filename is the same outcome for a missing file.</li>
 *   <li>Where it would stop following C if the commented body were restored: that body rethrows the
 *       {@code IOException}. Every other reader does the same because C quits on a missing data
 *       file, but lore is the exception, so a missing file would have to return an empty list
 *       instead.</li>
 *   <li>Work in C with no Java counterpart yet: the {@code parse_lore_*} handlers (the grammar's
 *       job), and everything in {@code finish_parse_lore}. That is merging each race's base flags
 *       into its lore flags, clearing the seen-count of any blow the race does not have, resolving
 *       friend names to races ({@code same} meaning the race itself), and calling
 *       {@code lore_update}. {@code cleanup_lore} only frees memory and needs no port.</li>
 *   <li>C's handlers write into the existing {@code l_list} record of the race named by
 *       {@code name:}, so a field the file omits keeps its earlier value. The grammar builds a new
 *       {@link MonsterLore} per record and hands it to the race instead.</li>
 * </ul>
 *
 * <p>Class LoreReader coded before 260915, commented in full on 261019.
 *
 * @author Rowan Crowther
 */
public class LoreReader implements Reader<MonsterLore> {
    /**
     * Logger the commented-out body of {@link #parse} would use to report a file that could not be
     * read. Nothing logs through it while the stub is in place.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Stub for C's {@code lore_parser} in {@code mon-init.c}: the lexer and grammar are not wired up
     * (see the commented-out body below), so this never opens {@code filename} and always returns a
     * new, empty, modifiable list. Parked deliberately until real game-generated lore data exists to
     * parse against; it is not a port in progress and not to be mistaken for one.
     *
     * <ul>
     *   <li>The result is the same whether the file is missing, empty or holds records, and the same
     *       as C gives for a missing file, where {@code run_parse_lore} reports "No monster lore
     *       file found" and carries on without error.</li>
     *   <li>For a file that does hold records C fills {@code l_list} and the stub does not. Nothing
     *       reaches {@code MonsterRace.setLore} from here.</li>
     *   <li>Nothing is thrown. The commented-out body would rethrow an {@code IOException}, which is
     *       what the other readers do for a missing file but not what C does for this one.</li>
     * </ul>
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
     *
     * @param filename the name of the file (never read - see above)
     * @return always a new empty list, never {@code null}, until the grammar is wired up
     * @throws IOException never, while the stub is in place
     */
    @Override
    public List<MonsterLore> parse(@NotNull String filename) throws IOException {
//        try {
//            CharStream stream = CharStreams.fromFileName(filename);
//            LoreLexer lexer = new LoreLexer(stream);
//            CommonTokenStream tokens = new CommonTokenStream(lexer);
//            LoreGrammar grammar = new LoreGrammar(tokens);
//            LoreGrammar.FileContext output = grammar.file();
//
//            return output.loreEntries;
//        } catch (IOException e) {
//            logger.error("Error while loading file {}", filename, e);
//            throw e;
//        }

        return new ArrayList<>();
    }
}
