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

package uk.co.jackoftradesltd.backend.parser.playerclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.magic.MagicSpell;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.*;

/**
 * Turns the {@link ClassSpellBookParseRecord}s of one class into the {@link MagicBook}s of its
 * {@link uk.co.jackoftradesltd.middle.magic.ClassMagic}, and registers the {@link ObjectKind} that
 * backs each book. It does the work of four C parser callbacks in {@code init.c}, which fill a
 * {@code struct class_book} one {@code class.txt} line at a time: {@code parse_class_book} for the
 * {@code book:} line, {@code parse_class_book_graphics} and {@code parse_class_book_properties} for
 * the two optional lines after it, and {@code parse_class_spell} and its siblings for the spells
 * (here delegated to {@link ClassSpellAssembler}). The kind is the work of {@code write_book_kind},
 * which {@code parse_class_book} calls.
 *
 * <p><b>Book-kind synthesis.</b> Spellbooks are not in {@code object.txt}; C makes an object kind
 * for each book while it reads {@code class.txt}, and this assembler does the same. A book's kind is
 * found by name alone, whichever tval it has. If one is already registered, because an earlier class
 * lists the same book, it is reused and nothing about it is rebuilt; otherwise a new kind is built
 * and handed to {@link ObjectRegistry#addObjectKind}, which gives it the next sval under its base. A
 * new kind has damage dice of {@code 1d1} and a weight of 30, inherits the base's kind flags, and
 * takes the book's cost, {@code commonness} (as {@code alloc_prob}), minimum and maximum depth
 * (as {@code alloc_min} and {@code alloc_max}), and glyph and colour. Its {@code level} is the
 * minimum, the first number of the {@code minmax} range as written, so a range written
 * high-to-low gives the high number, as in C. The two depths are not compared with each other. A
 * dungeon book's kind also gets {@code KF_GOOD} and {@code EL_INFO_IGNORE} on the four base
 * elements (acid, electricity, fire, cold); only a kind built here gets them, and a reused kind
 * keeps what it has. The kind is registered before {@link MagicBook#setSVal()} runs, which is how
 * the book learns its sval.
 *
 * <p><b>Defaults.</b> The graphics and properties lines are optional, and a missing value arrives as
 * an empty string. A missing glyph becomes {@code *} and a missing colour red, which are the
 * defaults {@code write_book_kind} sets before the graphics line overwrites them. A missing cost,
 * commonness, minimum or maximum becomes 0. An unknown colour is not an error: it becomes white, as
 * C's {@code color_text_to_attr} and {@code color_char_to_attr} do.
 *
 * <p><b>Declared spell count.</b> The {@code spells} figure on the {@code book:} line is a cap, as
 * in C, where {@code parse_class_book} allocates that many slots and {@code parse_class_spell}
 * refuses the first {@code spell:} line past them. A book with more spells than declared stops the
 * load. A book with fewer is accepted, and the book holds the spells actually listed. C stops on the
 * extra {@code spell:} line; the message here cites the {@code book:} line instead, which is the
 * decided wording.
 *
 * <p><b>Differences from C.</b>
 * <ul>
 *   <li>A shared book's kind is built once, from the first class that lists it, and later listings
 *   do not touch it. C's graphics and properties callbacks rewrite the shared kind every time they
 *   run. In the shipped {@code class.txt} the later listings of a shared book omit both lines, so
 *   the result is the same, but a data file that gave a shared book different values in two places
 *   would keep the first.</li>
 *   <li>The graphics and properties are also copied onto the {@link MagicBook}, where nothing reads
 *   them. A second listing therefore carries the defaults there whatever the shared kind holds.</li>
 *   <li>C reads the {@code book:} line, then the graphics and properties lines in later callbacks,
 *   and checks the tval, the quality, the kind, the count and the realm in that order. Here one
 *   pass checks the tval, the count, the realm, the glyph, the figures, the spells and the base. As
 *   every failure ends the load, only the message that appears first can differ. The base is also
 *   looked up before the kind is, so a tval with no base stops the load even when the book's kind is
 *   already registered, where C's reuse path never touches the base.</li>
 *   <li>C hands the book the sval of the kind it found by name. Here {@link MagicBook#setSVal()}
 *   looks the kind up by the book's tval and name, so a name already registered under a different
 *   tval gives the book an sval of {@code -1}. No name in the shipped {@code class.txt} is shared
 *   across tvals.</li>
 *   <li>Any quality other than {@code dungeon} makes a town book. C tests only for {@code dungeon},
 *   so the two agree.</li>
 * </ul>
 *
 * <p><b>Errors.</b> Every failure ends the load: a message naming the book's source line and file is
 * logged as fatal and a {@link DataFileParseException} carrying it is thrown. The checks run in
 * this order: unknown tval; a declared count that is negative or not an integer; unknown realm (C
 * quits in {@code lookup_realm}); a glyph that is not exactly one character (C's parser refuses it
 * as too long); a cost, commonness, minimum or maximum that is not an integer; any error from
 * {@link ClassSpellAssembler} for the book's spells; more spells than declared; and a tval with no
 * object base. Nothing is added to the soft-error list: it is passed on to
 * {@link ClassSpellAssembler} and that is all. Looking up the book's kind throws
 * {@link IllegalStateException} if no object kinds are loaded, and looking up the realm does the
 * same if no realms are, so {@code object.txt} and the realms must be loaded before any class.
 *
 * <p>Class ClassSpellBookAssembler coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class ClassSpellBookAssembler implements Assembler<ClassSpellBookParseRecord, List<MagicBook>> {
    /**
     * Receives the fatal message for each rejected {@code book:} record before the
     * {@link DataFileParseException} carrying the same text is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261028.
     */
    private static final Logger logger = LogManager.getLogger(ClassSpellBookAssembler.class);

    /**
     * Assembles each book record into a {@link MagicBook}: it reads the scalars, resolves the realm
     * and object base, assembles the spells, builds and registers the backing {@link ObjectKind} if
     * none is registered under the book's name, and resolves the book's sval.
     *
     * <p>The steps run in a fixed order, and the first failure stops the load: tval, declared count,
     * realm, glyph, cost, commonness, minimum, maximum, spells, too many spells, base. The colour is
     * resolved after the glyph but cannot fail. Books are returned in file order, and the books of
     * one class are registered in that order, so their svals follow it. A book whose name is already
     * registered keeps that kind, and only the {@link MagicBook} is new. See the class block for the
     * defaults, the kind that is built, the declared-count cap, and where this differs from C.
     *
     * <p>Function assemble coded before 260915, commented in full on 261028.
     *
     * @param records  the raw book records for one class, in file order
     * @param errors   the soft-error list, passed on to {@link ClassSpellAssembler}; this method adds
     *                 nothing to it
     * @param filename the data file the records came from, named in every error message
     * @return a new list with one {@link MagicBook} for each record, in file order
     * @throws DataFileParseException if a record has an unknown tval, a negative or non-integer
     *                                declared count, an unknown realm, a glyph that is not one
     *                                character, a figure that is not an integer, a spell that
     *                                {@link ClassSpellAssembler} rejects, more spells than declared,
     *                                or a tval with no object base
     * @throws IllegalStateException  if no object kinds or no realms are loaded
     */
    @Override
    public List<MagicBook> assemble(@NotNull List<ClassSpellBookParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<MagicBook> magicBooks = new ArrayList<>();
        for (ClassSpellBookParseRecord record : records) {
            int line = record.line();
            String tValueString = record.oBase();
            TValue tValue = TValue.fromName(tValueString);
            if (tValue == null) {
                String msg = "Class spell book starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal tValue value: " + record.oBase();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            boolean dungeon = record.locFrom().equals("dungeon");
            String bookName = record.name();
            // the declared number of spells is a cap, checked below against the spells assembled
            if (ParseUtils.minusFound(record.noOfSpells())) {
                String msg = "Class spell book starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative number of spells value: " + record.noOfSpells();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int noOfSpells = ParseUtils.parseIntString("Class spell book", record.noOfSpells(), line, filename, "number of spells");
            MagicRealm realm;
            try {
                realm = PlayerRegistry.lookupRealm(record.realm());
            } catch (IllegalArgumentException e) {
                realm = null;
            }
            if (realm == null) {
                String msg = "Class spell book starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal realm value: " + record.realm();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            String glyph = record.glyph();
            String colour = record.colour();
            AngbandDisplayCharacter adc;
            char glyphChar = '*';
            int firstCP = 0;
            ColourEnum colourEnum = ColourEnum.COLOUR_RED;
            if (!glyph.isEmpty()) {
                if (Character.codePointCount(glyph, 0, glyph.length()) != 1) {
                    String msg = "Class spell book starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal glyph value: " + record.glyph();
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                firstCP = glyph.codePointAt(0);
                if (!Character.isSupplementaryCodePoint(firstCP)) {
                    glyphChar = glyph.charAt(0);
                } else {
                    glyphChar = '\0';
                }
            }
            if (!colour.isEmpty()) {
                colourEnum = ColourEnum.fromCode(colour);
                if (colourEnum == null) {
                    colourEnum = ColourEnum.COLOUR_WHITE;
                }
            }
            if (glyphChar == '\0')
                adc = new AngbandDisplayCharacter(firstCP, colourEnum);
            else
                adc = new AngbandDisplayCharacter(glyphChar, colourEnum);
            
            int cost = ParseUtils.parseIntString("Class spell book", record.cost(), line, filename, "cost");
            int commonness = ParseUtils.parseIntString("Class spell book", record.commonness(), line, filename, "commonness");
            int min = ParseUtils.parseIntString("Class spell book", record.min(), line, filename, "minimum");
            int max = ParseUtils.parseIntString("Class spell book", record.max(), line, filename, "maximum");
            List<MagicSpell> spells = new ClassSpellAssembler().assemble(record.spells(), errors, filename);
            if (spells.size() > noOfSpells) {
                String msg = "Class spell book starting at line: " + line + " of file: " + filename + " has " +
                        "too many spells declared. " + spells.size() + " spells found. " + noOfSpells + " declared";
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            
            for (MagicSpell spell : spells) {
                spell.setRealm(realm);
            }

            // Synthesize the object kind that backs this book (C: write_book_kind). Spellbooks are
            // not in object.txt, so the kind is built from the base plus this book's properties and
            // registered, making the book an obtainable item. Dungeon books are marked "good" and
            // set to ignore every base element, as in the C original.
            ObjectBase base = ObjectRegistry.getBaseFromTVal(tValue);
            if (base == null) {
                String msg = "Class spell book starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal object base value: " + record.oBase();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }

            Flag<ObjectKindFlag> oFlags = new Flag<>(ObjectKindFlag.class);

            oFlags.copyFrom(base.getKindFlags());
            Map<ElementEnum, ElementInfo> eFlags = new HashMap<>();

            ObjectKind kind = ObjectRegistry.lookupObjectKind(bookName);
            
            if (dungeon && kind == null) {
                oFlags.on(ObjectKindFlag.KF_GOOD);
                for (ElementEnum e : Arrays.stream(ElementEnum.values())
                        .filter(ElementEnum::isBase).toList()) {
                    ElementInfo info = eFlags.computeIfAbsent(e, k -> new ElementInfo());
                    Flag<ElementInfoEnum> iFlag = info.getFlags();
                    iFlag.on(ElementInfoEnum.EL_INFO_IGNORE);
                }
            }

            Flag<IgnoreFlag> ignoreFlags = new Flag<>(IgnoreFlag.class);

            if (kind == null) {
                kind = new ObjectKind(bookName, null,
                        base, 0, Random.Zero(), Random.Zero(), Random.Zero(),
                        Random.Zero(), 0, Random.Zero(), 1, 1,
                        30, cost, new Flag<>(ObjectFlag.class),
                        oFlags, new HashMap<>(), eFlags, new HashSet<>(),
                        new HashSet<>(), new HashMap<>(), adc,
                        commonness, min, max, min, new ArrayList<>(), new ArrayList<>(),
                        null, null, Random.Zero(), Random.Zero(), 0,
                        Random.Zero(), null, null, null,
                        false, false, ignoreFlags, false, tValue, 0);
                ObjectRegistry.addObjectKind(kind);
            }

            MagicBook book = new MagicBook(tValue, bookName, dungeon,
                    realm, adc, cost, commonness, min,
                    max, spells);
            book.setSVal();
            magicBooks.add(book);
        }

        return magicBooks;
    }
}
