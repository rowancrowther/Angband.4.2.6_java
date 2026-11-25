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

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.backend.parser.grammars.objectproperty2.ObjectProperty2Grammar;
import uk.co.jackoftradesltd.backend.parser.grammars.objectproperty2.ObjectProperty2Lexer;
import uk.co.jackoftradesltd.backend.parser.grammars.quest.QuestGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.quest.QuestLexer;
import uk.co.jackoftradesltd.backend.parser.grammars.vault.VaultGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.vault.VaultLexer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pins the contract of {@link Directive} where it is built, in the three grammars that have no
 * grammar-level test of their own: {@code QuestGrammar}, {@code VaultGrammar} and
 * {@code ObjectProperty2Grammar}. {@code BlowEffect2GrammarTest} already pins it for the fourth.
 *
 * <p>What is pinned is what {@code parser_parse} in {@code parser.c} holds when it calls a hook:
 * the directive keyword ({@code dir}), the field values, and {@code p->lineno}. C counts a line
 * before it parses it, so the first line of a file is line 1; blank lines and comments still
 * count. The expected numbers below are counted by hand from the source text, not read back from
 * the grammar.
 *
 * <p>Class DirectiveFromGrammarsTest coded on 261125.
 *
 * @author Rowan Crowther
 */
class DirectiveFromGrammarsTest {

    /** Turns any lexer or parser syntax error into an {@link AssertionError}. */
    private static final BaseErrorListener FAIL_FAST = new BaseErrorListener() {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                int charPositionInLine, String msg, RecognitionException e) {
            throw new AssertionError("syntax error at " + line + ":" + charPositionInLine + " - " + msg);
        }
    };

    // ---- helpers ---------------------------------------------------------

    /**
     * The directives of the first quest record in {@code source}.
     */
    private static List<Directive> quest(String source) {
        QuestLexer lexer = new QuestLexer(CharStreams.fromString(source));
        lexer.removeErrorListeners();
        lexer.addErrorListener(FAIL_FAST);
        QuestGrammar parser = new QuestGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(FAIL_FAST);
        return parser.file().quests.get(0).directives();
    }

    /**
     * The directives of the first vault record in {@code source}.
     */
    private static List<Directive> vault(String source) {
        VaultLexer lexer = new VaultLexer(CharStreams.fromString(source));
        lexer.removeErrorListeners();
        lexer.addErrorListener(FAIL_FAST);
        VaultGrammar parser = new VaultGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(FAIL_FAST);
        return parser.file().profiles.get(0).directives();
    }

    /**
     * The directives of the first object-property record in {@code source}.
     */
    private static List<Directive> objectProperty(String source) {
        ObjectProperty2Lexer lexer = new ObjectProperty2Lexer(CharStreams.fromString(source));
        lexer.removeErrorListeners();
        lexer.addErrorListener(FAIL_FAST);
        ObjectProperty2Grammar parser = new ObjectProperty2Grammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(FAIL_FAST);
        return parser.file().properties.get(0).directives();
    }

    private static List<Integer> lines(List<Directive> directives) {
        return directives.stream().map(Directive::line).toList();
    }

    private static List<String> names(List<Directive> directives) {
        return directives.stream().map(Directive::name).toList();
    }

    // ---- quest -----------------------------------------------------------

    /**
     * C counts the line before it parses it, so a directive on the first line of the file is line 1,
     * not line 0.
     */
    @Test
    void aQuestDirectiveOnTheFirstLineOfTheFileIsLineOne() {
        List<Directive> ds = quest("""
                name:Sauron
                level:99
                race:Sauron, the Sorcerer
                number:1
                """);

        assertEquals(List.of(1, 2, 3, 4), lines(ds));
    }

    /**
     * Comments and blank lines are read by {@code parser_parse} and still advance {@code p->lineno},
     * so the directive after them carries its real line in the file.
     */
    @Test
    void commentsAndBlankLinesStillCountTowardsAQuestDirectivesLine() {
        List<Directive> ds = quest("""
                # a comment

                name:Sauron
                # another
                level:99
                race:Sauron, the Sorcerer

                number:1
                """);

        assertEquals(List.of("name", "level", "race", "number"), names(ds));
        assertEquals(List.of(3, 5, 6, 8), lines(ds));
    }

    /**
     * {@code race} is a {@code str}, so C takes the rest of the line without cutting it at a colon
     * or a comma, and keeps the blanks inside it. That is one value.
     */
    @Test
    void aQuestStrDirectiveIsOneValueWithItsBlanksKept() {
        List<Directive> ds = quest("""
                name:Sauron
                level:99
                race:Sauron, the Sorcerer
                number:1
                """);

        assertEquals(List.of("Sauron, the Sorcerer"), ds.get(2).values());
        assertEquals(List.of("99"), ds.get(1).values());
    }

    // ---- vault -----------------------------------------------------------

    @Test
    void vaultDirectivesCarryTheirKeywordAndTheirOwnLine() {
        List<Directive> ds = vault("""
                # a comment

                name:Round
                type:Lesser vault
                rating:5
                rows:2
                columns:2
                min-depth:0
                max-depth:0
                D:%%
                D:%%
                """);

        assertEquals("name", ds.get(0).name());
        assertEquals(List.of("Round"), ds.get(0).values());
        assertEquals(3, ds.get(0).line());
        assertEquals("type", ds.get(1).name());
        assertEquals(List.of("Lesser vault"), ds.get(1).values());
        assertEquals(4, ds.get(1).line());
        assertEquals("rating", ds.get(2).name());
        assertEquals(5, ds.get(2).line());
    }

    /**
     * C registers the vault keywords {@code flags} and {@code D}. {@code VaultGrammar} reads those
     * two spellings from the file but labels the directives {@code flag} and {@code map}, which is
     * what {@code VaultAssembler} switches on (see "Vault" in {@code docs/Accepted_Deviations.md}).
     */
    @Test
    void aVaultFlagsLineAndDLineAreNamedFlagAndMapNotFlagsAndD() {
        List<Directive> ds = vault("""
                name:Round
                flags:FEW_ENTRANCES
                D:%%
                """);

        assertEquals(List.of("name", "flag", "map"), names(ds));
        assertEquals(List.of("FEW_ENTRANCES"), ds.get(1).values());
        assertEquals(List.of("%%"), ds.get(2).values());
    }

    /**
     * The label is internal: the data file must still say {@code flags:}, so C's keyword loads and
     * the grammar's own label does not.
     */
    @Test
    void theLabelFlagIsNotAKeywordInAVaultFile() {
        assertThrows(AssertionError.class, () -> vault("""
                name:Round
                flag:FEW_ENTRANCES
                """));
    }

    // ---- object property -------------------------------------------------

    /**
     * {@code type-mult} is the one directive with two fields in C, a {@code sym} and an {@code int},
     * so it is the one that arrives with two values, in file order.
     */
    @Test
    void aTypeMultDirectiveCarriesTwoValuesInFileOrder() {
        List<Directive> ds = objectProperty("""
                name:extra blows
                type:mod
                code:BLOWS
                power:0
                mult:50
                type-mult:bow:0
                type-mult:gloves:2
                """);

        assertEquals("type-mult", ds.get(5).name());
        assertEquals(List.of("bow", "0"), ds.get(5).values());
        assertEquals(List.of("gloves", "2"), ds.get(6).values());
    }

    @Test
    void objectPropertyDirectivesCarryTheirOwnLineNumbers() {
        List<Directive> ds = objectProperty("""
                name:extra blows
                type:mod
                # a comment

                code:BLOWS
                power:0
                mult:50
                type-mult:bow:0
                """);

        assertEquals(List.of("name", "type", "code", "power", "mult", "type-mult"), names(ds));
        assertEquals(List.of(1, 2, 5, 6, 7, 8), lines(ds));
    }
}
