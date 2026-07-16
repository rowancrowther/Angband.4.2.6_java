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

/*
 * @author Rowan Crowther
 *
 * Standalone lexer grammar for the Angband dice-string mini-language (e.g.
 * "2d8", "1+1d3", "M80", "4d$S", "$Dd5", "$Dd$S", "$B+d$S", "-3d5").
 *
 * It has no @header instructions or java-domain-type coupling.
 *
 * === Two C parsers, one grammar ===
 *
 * The gamedata contains dice strings validated by TWO different C parsers,
 * which accept overlapping but NOT identical languages:
 *
 *   - parse_random (parser.c:115-203) validates every field the C parser specs
 *     declare as `rand` - e.g. artifact.txt's "attack:" damage, and object.txt's
 *     "attack:" damage AND its to-hit/to-dam.
 *   - dice_parse_string / dice_parse_state_transition (z-dice.c) validates the
 *     "dice:" and "time:" lines of an effect block, and is the only one of the
 *     two that understands '$'-variables.
 *
 * Where the two disagree, this grammar follows whichever parser can actually
 * reach the alternative in question - which is not always the obvious one. See
 * "Simple vs complex" and "Where the two C parsers disagree" below.
 *
 * === The dice-string shape ===
 *
 * Ignoring the two features no gamedata file currently uses (see "Deliberate
 * omissions" below), the format is:
 *
 *     '-'?   [base '+']   [count] 'd' sides   [('M'|'m') bonus]
 *
 * where every one of the four numeric slots (base, count, sides, bonus) is
 * independently either a literal integer or a '$'-variable, and:
 *
 *   - the leading '-' is a prefix on the WHOLE string, NOT part of the base.
 *     Both C parsers strip it before any structure is parsed (parse_random at
 *     parser.c:122-125; z-dice's state table takes '-' from DICE_STATE_START,
 *     z-dice.c:133). This is why '-' appears on nearly every alternative below
 *     rather than only the base-bearing ones - see "The leading '-'" for what it
 *     MEANS, and the per-fragment comments for the one deliberate exception;
 *   - a base is ONLY ever present when immediately followed by '+' (a lone
 *     leading number with nothing after it is a base; a leading number
 *     followed by 'd' is a dice COUNT, not a base - this is why the C needs a
 *     lookahead state machine);
 *   - 'd' with no count in front of it means one die (C's z-dice.c:414-419);
 *   - the whole thing collapses to a bare base ("5"), a bare bonus ("M4"), or
 *     any combination of the three segments.
 *
 * === The leading '-' ===
 *
 * A leading '-' does NOT mean "the base is negative". It means NEGATE THE WHOLE
 * RANDOM RESULT - i.e. multiply whatever follows by -1. parse_random back-solves
 * that into the base field (parser.c:196-200):
 *
 *     base *= -1;  base -= m_bonus;  base -= dice * (sides + 1);
 *
 * so "-3d5" yields base=-18, dice=3, sides=5 - the range -18..-3, which is
 * exactly the negation of 3d5's 3..18. A port of parse_random MUST reproduce
 * this arithmetic; treating '-' as a sign on the base silently produces the
 * wrong distribution rather than an error.
 *
 * IMPORTANT EXCEPTION: "values:" lines do NOT go through parse_random, and there
 * a leading '-' DOES simply set the sign of the base (object.txt:44-49). So
 * "-d4" is meaningful on an "attack:" line but is a data bug on a "values:"
 * line, where "-5+1d4" is the documented spelling. This grammar cannot tell the
 * two apart - the distinction is per-directive and belongs to the consumer.
 *
 * === Simple vs complex ===
 *
 * Two flavours of the SAME structure are exposed:
 *
 *   - SIMPLE_DICE_STRING(_BODY): every numeric slot is a literal integer
 *     (DICE_SIMPLE_NUMBER). A simple value carries no forward reference and can
 *     be evaluated immediately (e.g. Random.parseStr()).
 *   - COMPLEX_DICE_STRING(_BODY): every numeric slot may ALSO be a '$'-variable
 *     (DICE_ANY_NUMBER), which refers forward to an expr: line and so cannot be
 *     evaluated until that line is resolved.
 *
 * Note what that split IS: "does this string need expr: resolution before it can
 * be evaluated?" Note what it is NOT: "which C parser validates this string?"
 * The two do not line up, and the difference drives the '-' rules below. A
 * "dice:" line is z-dice territory, but a "dice:" value with no '$' in it (e.g.
 * "2d6", or "-M4") is claimed by the SIMPLE token, because DICE_SIMPLE_VALUE is
 * declared first in EffectBlockLexer's DICE_STRING_MODE. Hence:
 *
 *   - SIMPLE_* is the workhorse: it serves BOTH C parsers, so it must admit
 *     everything parse_random admits, and it is what a variable-free z-dice
 *     string lands on too.
 *   - COMPLEX_* only ever sees strings containing a '$', which are z-dice's
 *     alone. It can therefore afford to follow z-dice exactly, and does so.
 *
 * The two bodies share one structural skeleton (the four alternatives), written
 * once per flavour and differing ONLY in which "number" fragment fills the slots
 * - plus the single deliberate '-' asymmetry on alternative 3, explained on
 * COMPLEX_DICE_STRING_BODY. This replaces the previous hand-enumerated
 * permutation list, which had to spell out every literal-vs-variable combination
 * by hand across all four slots and, being a flat list, silently omitted the
 * "$base + d$sides" case ("$B+d$S") that eight spells in class.txt use.
 * Factoring the slot into a fragment makes every combination fall out of the
 * grammar by construction, so no combination can be missed.
 *
 * Note that DICE_ANY_NUMBER admits a literal integer too, so COMPLEX_* will
 * also match a variable-free string. That is intentional and safe BECAUSE the
 * simple token is always declared before the complex token wherever the two
 * are consumed together (here, and in the DICE_STRING_MODE of the importing
 * lexers). ANTLR takes the longest match and breaks ties by declaration order,
 * so a variable-free string is claimed by SIMPLE_* (equal length, declared
 * first), and COMPLEX_* only wins when a '$' makes the simple body fail to
 * cover the whole string.
 *
 * === Where the two C parsers disagree ===
 *
 *   form     parse_random   z-dice        this grammar
 *   -----    ------------   ----------    ------------
 *   -5       accepts        accepts       accepts
 *   -5+1d4   accepts        accepts       accepts
 *   -2d6     accepts        accepts       accepts
 *   -d4      accepts        accepts       accepts
 *   -M4      accepts        REJECTS       accepts, via SIMPLE_* only
 *   -m$M     n/a            REJECTS       REJECTS
 *   -$B      n/a            REJECTS       accepts  (over-permissive)
 *   -$Dd6    n/a            REJECTS       accepts  (over-permissive)
 *
 * z-dice rejects the last four because '-' lands it in DICE_STATE_BASE_DIGIT,
 * from which 'm' and '$' are both invalid transitions (z-dice.c:134).
 *
 * "-M4" is accepted because it MUST be: parse_random allows it, and SIMPLE_* -
 * which serves parse_random - is also what a variable-free z-dice string lands
 * on. Refusing it would break "attack:" lines to protect "dice:" lines that no
 * data file contains. "-m$M" carries a '$', so it reaches COMPLEX_*, which
 * follows z-dice and rejects it correctly.
 *
 * The two remaining over-acceptances ("-$B", "-$Dd6") would need '-' to bind
 * only to integer-headed forms, splitting the skeleton further for no
 * reachable gain: no gamedata file contains either, and if one ever does it
 * will fail downstream in dice evaluation rather than being silently mis-read.
 * That is the right direction to err in - a lexer too NARROW splits a valid
 * string into two tokens with NO error at all (as "-3d5" once did to
 * object.txt:2273), which is far harder to diagnose than a loud failure later.
 *
 * Exact per-parser validation therefore belongs to the consumer - Random's
 * parse_random port, and the effect-block dice evaluator - not here.
 *
 * === Deliberate omissions vs the full C grammar ===
 *
 *   - the '&' rounding marker (C accepts it after sides / after a variable);
 *   - multi-character variable names (C's DICE_DOLLAR_LETTER equivalent can run
 *     on: "$AB..."). DICE_DOLLAR_LETTER here is a single upper-case letter.
 *
 * Neither appears in any current lib/gamedata file, and neither was supported
 * by the previous version of this grammar; they are called out here so a future
 * data file that needs them has a documented starting point.
 */
lexer grammar DiceStrings;

/*
 * @author Rowan Crowther
 *
 * Dice-count/sides separator, always lowercase 'd'
 */
fragment DICE_D
        :   'd'
        ;

/*
 * @author Rowan Crowther
 *
 * M-bonus marker, normally 'M' or 'm'
 */
fragment DICE_M
        :   'M'
        |   'm'
        ;

/*
 * @author Rowan Crowther
 *
 * Literal non-negative (unsigned) DICE_INTEGER.
 *
 * Unsigned is deliberate and matches both C parsers: every numeric slot is read
 * with strtoul, and an embedded sign is rejected outright (parser.c:161). The
 * only '-' either parser accepts is the whole-string prefix carried by the
 * alternatives of the two *_DICE_STRING_BODY fragments below.
 */
fragment DICE_INTEGER
        :   ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * Bound, dollar-char variable such as '$M'.
 */
fragment DICE_DOLLAR_LETTER
        :   '$' ('A'..'Z')
        ;

/*
 * @author Rowan Crowther
 *
 * A single numeric slot in a SIMPLE dice string: a literal integer only, with
 * no forward '$'-variable reference. Used to fill every slot of
 * SIMPLE_DICE_STRING_BODY.
 */
fragment DICE_SIMPLE_NUMBER
        :   DICE_INTEGER
        ;

/*
 * @author Rowan Crowther
 *
 * A single numeric slot in a COMPLEX dice string: either a literal integer or a
 * '$'-variable. Used to fill every slot of COMPLEX_DICE_STRING_BODY. This is the
 * sole point of difference between the simple and complex bodies.
 */
fragment DICE_ANY_NUMBER
        :   DICE_INTEGER
        |   DICE_DOLLAR_LETTER
        ;

/*
 * @author Rowan Crowther
 *
 * The complex-dice-string body: the canonical dice shape with every numeric
 * slot widened to DICE_ANY_NUMBER (integer OR '$'-variable). The four
 * alternatives are:
 *
 *   1. base '+' (dice and/or bonus)   e.g. "$B+d$S", "$B+3d6", "5+1d3", "$B+m$M"
 *   2. dice, no base                  e.g. "$Dd$S", "2d6", "d$S", "-2d$S"
 *   3. bonus only                     e.g. "M$M"          (never "-m$M")
 *   4. base only                      e.g. "$B", "-5"
 *
 * Because this body only ever CLAIMS strings containing a '$' (see "Simple vs
 * complex" at the top of the file), it can and does follow z-dice exactly on the
 * point where the two C parsers differ: alternative 3 deliberately has NO
 * leading '-', because z-dice refuses 'm' immediately after a '-'
 * (z-dice.c:134). Do not "restore" it for symmetry with
 * SIMPLE_DICE_STRING_BODY - the asymmetry is the whole point, and it is what
 * makes "-m$M" fail here as C does.
 *
 * Alternatives 2 and 4 keep '-' ahead of a DICE_ANY_NUMBER, so they still admit
 * "-$Dd6" and "-$B", which z-dice also rejects; that residual over-acceptance is
 * unreachable in the current gamedata and is discussed under "Where the two C
 * parsers disagree" at the top of the file.
 *
 * See the top-of-file note on why this also matches variable-free strings and
 * why that is harmless given simple-before-complex declaration order.
 */
fragment COMPLEX_DICE_STRING_BODY
        :   '-'? DICE_ANY_NUMBER '+' ( DICE_ANY_NUMBER? DICE_D DICE_ANY_NUMBER (DICE_M DICE_ANY_NUMBER)?
                                     | DICE_M DICE_ANY_NUMBER )
        |   '-'? DICE_ANY_NUMBER? DICE_D DICE_ANY_NUMBER (DICE_M DICE_ANY_NUMBER)?
        |   DICE_M DICE_ANY_NUMBER
        |   '-'? DICE_ANY_NUMBER
        ;

/*
 * @author Rowan Crowther
 *
 * The simple-dice-string body: the canonical dice shape of
 * COMPLEX_DICE_STRING_BODY with every numeric slot restricted to a literal
 * integer (DICE_SIMPLE_NUMBER) - i.e. no '$'-variables. Exposed as a fragment so
 * all grammars can reuse this exact body in their own mode-scoped wrapper
 * without redeclaring it.
 *
 * This body carries the whole of parse_random's language, so - unlike
 * COMPLEX_DICE_STRING_BODY - the leading '-' appears on ALL FOUR alternatives,
 * including the bonus-only alternative 3 ("-M4"). parse_random negates at
 * str[0] and then parses the remainder with no further restriction
 * (parser.c:122-125), so no alternative may omit it. "-3d5" is not theoretical:
 * object.txt:2273 uses it, and before alternative 2 gained its '-' the lexer
 * split that line into "-3" and "d5" silently.
 *
 * This is also the body that claims variable-free z-dice strings, so it is the
 * workhorse of the two and is knowingly wider than z-dice alone - see "Simple vs
 * complex" and "Where the two C parsers disagree" at the top of the file.
 */
fragment SIMPLE_DICE_STRING_BODY
        :   '-'? DICE_SIMPLE_NUMBER '+' ( DICE_SIMPLE_NUMBER? DICE_D DICE_SIMPLE_NUMBER (DICE_M DICE_SIMPLE_NUMBER)?
                                        | DICE_M DICE_SIMPLE_NUMBER )
        |   '-'? DICE_SIMPLE_NUMBER? DICE_D DICE_SIMPLE_NUMBER (DICE_M DICE_SIMPLE_NUMBER)?
        |   '-'? DICE_M DICE_SIMPLE_NUMBER
        |   '-'? DICE_SIMPLE_NUMBER
        ;

/*
 * @author Rowan Crowther
 *
 * A simple dice string token, as opposed to a fragment, allowing consumers
 * to swallow dice expressions as one token. Declared BEFORE COMPLEX_DICE_STRING
 * so a variable-free string is claimed here (see top-of-file note).
 */
SIMPLE_DICE_STRING
        :   SIMPLE_DICE_STRING_BODY
        ;

/*
 * @author Rowan Crowther
 *
 * A complex dice string token, as opposed to a fragment, allowing consumers
 * to swallow dice expressions as one token.
 */
COMPLEX_DICE_STRING
        :   COMPLEX_DICE_STRING_BODY
        ;
