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

// Combined lexer+parser for the dice-string notation that Angband's data files use for every
// `rand` field, e.g. "2d8", "1+1d3", "M80", "-5" or " 2d6 ". It is not tied to one lib/gamedata
// file or to one C file_parser; it is a shared building block. The other readers reach it through
// Random.parseStr(String) in middle/numerics/Random.java, which hands the whole string, a leading
// '-' included, to a RandomReader (see RandomReader.java). The dice rule below reads that '-' and
// flags the value as negated. The RandomReader drives this grammar's generated RandomLexer and
// RandomParser. Callers include ArtifactAssembler (the
// attack: dice), MonsterAssembler (a blow's damage and a friends number), EgoItemAssembler (to-h,
// to-d, to-a and the timeout), TrapAssembler (a trap's power) and LoreGrammar.
//
// The C equivalent is parse_random() in parser.c. It reads the string once, left to right, with
// a counter that says which of base, dice, sides and bonus the next number belongs to. The notation
// it reads is:
//
//     '-'?  [base '+']  [dice] 'd' sides  ['M' bonus]
//
// where a bare number is the base, a number straight before 'd' is the dice count, 'd' with no count
// is one die, and the whole string may be just a base ("5"), just a bonus ("M4"), or any mix of the
// three parts. C is looser than that notation, and this grammar follows C where it is:
//
//   - 'd' and 'M' may be repeated: "dd1" is one die with 1 side, and in "1+2d3M4M5" the last
//     bonus wins, so the value is base 1, dice 2, sides 3, bonus 5.
//   - after the sides, a second number separated from them by blanks is the bonus even with no
//     'M': "2d6 5" is bonus 5. In the same way "5+3 4" is base 5, 3 dice and 4 sides, with no 'd'.
//   - a run of blanks is allowed directly before any number, and at the very end of the string,
//     and nowhere else. "2d 6", "M 3" and " 2d6 " are read; "2 d6", "1 +2d3" and "2d6 M3" are
//     refused. C gets this from strtoul(), which skips blanks in front of the digits.
//   - the leading '-' negates the whole value. C consumes it before it reads any number, so "-01"
//     is a minus followed by 01. A '-' anywhere else is read only as a signed zero, such as the
//     "-0" in "2d-0", because strtoul() reads "-0" as 0. See the SPACE_NUMBER token below.
//   - a number above INT_MAX is refused, with one exception listed below.
//
// Where this grammar differs from C today (checked on 261124 by comparing every string up to six
// characters over the characters 1 2 0 d M + - and a blank or tab with a copy of parse_random() in
// RandomReaderTest, for acceptance and for all four values):
//
//   - a lower-case 'm' is read as 'M'. parse_random() refuses it; dice_parse_string() in z-dice.c
//     takes it, and this one grammar serves both. See "Random" in docs/Accepted_Deviations.md.
//   - '$' variables are not read. parse_random() has none either. dice_parse_string() does read
//     them, for the dice: and time: lines of an effect, but in this project those lines are read by
//     the effect-block lexers through the DiceStrings import, not by this grammar.
//   - a number above INT_MAX that is followed by a later bonus is lost. "2d6M3000000000M1" is
//     refused by C, which stops at the first number, and read here as bonus 1, because each bonus
//     in the bonus rule overwrites the last one. "M3000000000M1" and "1+M3000000000M1" do the same.
//     Accepted on 261124; see "Random" in docs/Accepted_Deviations.md.
//
// Grammar commented in full on 261124.

grammar Random;

@header {
    import uk.co.jackoftradesltd.middle.numerics.Random;
}

@parser::members {
    /**
     * Reads the number held in a SPACE_NUMBER token, or returns -1 to mark it as unusable.
     *
     * <p>The token text is trimmed first, because a SPACE_NUMBER carries the blanks in front of its
     * digits. A signed zero such as {@code -0} reads as 0, and a {@code +} in front of the digits is
     * accepted by {@code Long.parseLong}. The result is -1 when the digits are above
     * {@code INT_MAX}, which {@code parse_random} in {@code parser.c} refuses, or too long to read
     * as a {@code long}, and when the token is one that ANTLR conjured during error recovery. A
     * conjured token has text such as {@code <missing SPACE_NUMBER>}, which is not a number, so it
     * reaches the {@code NumberFormatException} branch. The first test, for the text
     * {@code <missing NUMBER>}, names a token this grammar no longer produces and never matches. The
     * {@code l < 0} test cannot fire either: no SPACE_NUMBER has a negative value. The
     * {@code dice} rule's {@code @after} turns any -1 into a null {@code Random}.
     *
     * <p>Function numberOrMissing coded before 261124, commented in full on 261124 after the trim
     * was added.
     *
     * @param t the SPACE_NUMBER token to read
     * @return the value as an {@code int}, or -1 if it cannot be used
     */
    private static int numberOrMissing(Token t) {
        String text = t.getText().trim();
        if (text.equals("<missing NUMBER>"))
            return -1;
        
        long l;
        
        try {
            l = Long.parseLong(text);
        } catch (NumberFormatException e) {
            return -1;
        }
        
        if (l > Integer.MAX_VALUE || l < 0) 
            return -1;
        
        return Math.clamp(l, 0, Integer.MAX_VALUE);
    }
}

// The bonus tail that may follow the sides in the dice rule: M* number (M+ number)*.
//
// The first number may follow any number of M markers, including none. With none, it is the blank-
// separated number of "2d6 5", and the blank is part of its SPACE_NUMBER token. Every later number
// needs at least one M in front of it, so "2d6M1M2" is read and "2d6M1 2" is not. Each number
// overwrites the one before, so the last number is the bonus, as in C, where each M N pair writes the
// same m_bonus slot. bonusVal is the value of the last number, or -1 if that number is unusable;
// see numberOrMissing, and the note in the header about an oversized number that a later one
// overwrites.
bonus
        returns[int bonusVal]
        :   M* m1=SPACE_NUMBER { $bonusVal = numberOrMissing($m1); } (M+ m2=SPACE_NUMBER { $bonusVal = numberOrMissing($m2); })*
        ;

// The entry rule: parses one whole dice string and builds a Random from it. It is followed by
// EOF, so the rule must use up the string, and the string may end in a run of blanks.
//
// Seven alternatives, one for each shape C reads. In the sketches, b is the base, n the dice count,
// s the sides and m a bonus, and the leading '-' is optional in front of every one and sets negated:
//
//     1.  b                       a bare base
//     2.  b + n [d..] s [bonus]   base, dice count and sides; the d may be a blank instead
//     3.  b + d.. s [bonus]       base and one die
//     4.  b + M.. m [M.. m]...    base and a bonus only
//     5.  n d.. s [bonus]         dice count and sides
//     6.  d.. s [bonus]           one die
//     7.  M.. m [M.. m]...        a bonus only
//
// Alternatives 3 and 6 set the dice count to 1, for a 'd' with no count in front of it.
//
// The locals in @init hold the four values as they are read:
//   baseInit, diceInit, sidesInit, mBonusInit   the numbers, each -1 if it could not be used
//   negated                                     set by a leading '-'
//   bonusInit                                   a scratch value for alternatives 4 and 7
//   baseStr, sidesStr                           the text of a '$' variable base or sides. No
//                                               alternative sets them any more, so both stay empty
// The @after block builds the Random, or leaves it null if any of the four numbers is -1. Because
// baseStr and sidesStr are always empty, it always takes the first constructor, with negated.
dice
        returns[Random random]
        @init {
            String baseStr = "";
            int baseInit = 0;
            int diceInit = 0;
            String sidesStr = "";
            int sidesInit = 0;
            int mBonusInit = 0;
            boolean negated = false;
            int bonusInit = 0;
        }
        @after {
            if (mBonusInit == -1 || diceInit == -1 || sidesInit == -1 || baseInit == -1)
                $random = null;            
            else if (baseStr.isEmpty() && sidesStr.isEmpty())
                $random = new Random(baseInit, mBonusInit, diceInit, sidesInit, negated);
            else if (baseStr.isEmpty())
                $random = new Random(baseInit, mBonusInit, diceInit, sidesStr);
            else
                $random = new Random(baseStr, mBonusInit, diceInit, sidesInit);
        }
        :   (MINUS { negated = true; })? (b=SPACE_NUMBER { baseInit = numberOrMissing($b); }
        |   b=SPACE_NUMBER PLUS d=SPACE_NUMBER D* s=SPACE_NUMBER 
            (bonus {mBonusInit = $bonus.bonusVal; })? 
                { baseInit = numberOrMissing($b);
                diceInit = numberOrMissing($d);
                sidesInit = numberOrMissing($s); }
        |   b=SPACE_NUMBER PLUS D+ s=SPACE_NUMBER {
                baseInit = numberOrMissing($b);
                sidesInit = numberOrMissing($s);
                diceInit = 1;
            }
            (bonus {mBonusInit = $bonus.bonusVal; })? 
        |   b=SPACE_NUMBER PLUS M+ m1=SPACE_NUMBER { bonusInit = numberOrMissing($m1); } (M+ m2=SPACE_NUMBER { bonusInit = numberOrMissing($m2); })*
            {
                baseInit = numberOrMissing($b);
                mBonusInit = bonusInit;
            }
        |   d=SPACE_NUMBER D+ s=SPACE_NUMBER
            (bonus {mBonusInit = $bonus.bonusVal; })? 
            {
                diceInit = numberOrMissing($d);
                sidesInit = numberOrMissing($s);
            }
        |   D+ s=SPACE_NUMBER { sidesInit = numberOrMissing($s); diceInit = 1; }
            (bonus {mBonusInit = $bonus.bonusVal; })? 
        |   M+ m1=SPACE_NUMBER { bonusInit = numberOrMissing($m1); } (M+ m2=SPACE_NUMBER { bonusInit = numberOrMissing($m2); })*
            {
                mBonusInit = bonusInit;
                bonusInit = 0;
            }
        )   SPACE? EOF
        ;

// A run of blanks or tabs. The only run that reaches the dice rule as a SPACE is the one at the
// very end of the string. A run in front of a number belongs to that number's SPACE_NUMBER token.
SPACE
        :   ('\t' | ' ')+
        ;

// The dice-count/sides separator, e.g. the 'd' in "2d8". Lower case only: C refuses a 'D'.
D
        :   'd'
        ;

// The bonus marker, e.g. the 'M' in "2d8M4". It accepts either case, as z-dice.c does for 'M' and
// 'm'. parse_random() in parser.c reads only 'M', so a lower-case 'm' is read here and refused
// there; see "Random" in docs/Accepted_Deviations.md.
M
        :   'M'
        |   'm'
        ;

// The '+' after a base, e.g. in "1+2d3". A '+' with blanks in front of it, as in " +1", is not this
// token; it belongs to a SPACE_NUMBER.
PLUS
        :   '+'
        ;

// The '-' at the very start of the string, which negates the whole value, e.g. in "-1+2d3". A '-'
// anywhere else is only ever part of a signed zero inside a SPACE_NUMBER, such as "2d-0".
MINUS
        :   '-'
        ;

// A '$' variable such as "$S", "$D" or "$B": '$' and one upper-case letter. No alternative of the
// dice rule uses it, so a '$' in the string is refused, as parse_random() refuses it.
DOLLAR_LETTER
        :   '$' ('A'..'Z')
        ;

// A number together with the blanks that C's strtoul() would skip in front of it. A SPACE_NUMBER
// never includes blanks after its digits. There are four forms:
//
//   1. blanks, then '-' and zeros, such as " -0": a signed zero, which strtoul() reads as 0. A digit
//      may not follow, so "-01" is not read as "-0" and then "1".
//   2. the same with no blanks, such as the "-0" in "2d-0". The predicate on token start stops it
//      matching at the very start of the string: there the '-' is the negation of the whole value,
//      which C consumes before it reads any number, so "-0+d00" is MINUS, then the number 0.
//   3. digits, with any blanks in front of them: the ordinary number, such as "5" or " 5".
//   4. one or more blanks, then '+' and digits, such as " +1". strtoul() reads the sign, but C
//      refuses a number that begins with '+' straight after the previous character, because it
//      checks before any blank is skipped. So a '+' is read as a sign only when a blank comes first.
//
// Whether a number is too large is not decided here; numberOrMissing refuses one above INT_MAX.
// Declared before NUMBER on purpose: a bare digit string matches both, and ANTLR gives a tie to
// the rule declared first, so such a string lexes as SPACE_NUMBER.
SPACE_NUMBER
        :   ('\t' | ' ')+ '-' '0'+ {!Character.isDigit(_input.LA(1))}?
        |   {_tokenStartCharIndex > 0}? '-' '0'+ {!Character.isDigit(_input.LA(1))}?
        |   ('\t' | ' ')* ('0'..'9')+
        |   ('\t' | ' ')+ PLUS ('0'..'9')+
        ;

// A string of digits. No alternative of the dice rule uses it, and the lexer never produces it:
// SPACE_NUMBER is declared first and matches the same text, so it wins every tie.
NUMBER
        :   ('0'..'9')+
        ;
