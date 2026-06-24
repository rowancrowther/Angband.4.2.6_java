// Combined lexer+parser for Angband's dice-string mini-language (e.g. "2d8",
// "1+1d3", "M80", "4d$S", "$Dd5"). Unlike the other grammars in this
// directory, this one isn't tied to a single lib/gamedata file or a single
// C file_parser - it's a shared building block. Every other grammar reaches
// it indirectly: uk.co.jackoftrades.backend.numerics.Random.parseStr(String)
// (Random.java:95) constructs a RandomReader, which drives this grammar's
// generated RandomLexer/RandomParser (see RandomReader.java) and is itself
// called from dice:/visibility:/radius-style fields all over the other
// grammars (e.g. TrapGrammar.g4's `dice`/`visibility` rules). The C
// equivalent is the dice_t/dice_parse_string() state machine in
// src/z-dice.c (doc comment at z-dice.c:294-308) - note its own comment
// admits "the parser isn't complex", i.e. it deliberately avoided a formal
// grammar for this because the base/dice/sides/bonus permutation space (each
// independently a literal, a variable, or absent) gets unwieldy fast. This
// grammar enumerates that permutation space by hand as ~25 alternatives,
// which is exactly where the gaps below come from.
//
// POTENTIAL PROBLEMS:
//
//   1. No alternative allows a DOLLAR_LETTER as the *dice count* (only as
//      base or sides) - every alt with `diceNum=` binds it to NUMBER. But
//      "$Dd4", "$Dd6", "$Dd7", "$Dd8", "$Dd12" (dice count given as a bound
//      variable, fixed integer sides) appear repeatedly in class.txt and
//      monster_spell.txt, and trap.txt itself uses "dice:$Dd5" (line ~466).
//      None of these can be parsed by this grammar today.
//
//   2. "$Dd$S" (both dice count AND sides as variables, no literal numbers
//      at all) appears in class.txt (e.g. line 1021) and is unsupported for
//      the same reason - there's no diceNum=DOLLAR_LETTER alternative to
//      pair with sides=DOLLAR_LETTER.

grammar Random;

@header {
    import uk.co.jackoftrades.backend.numerics.Random;
}

// The only rule in this grammar: parses one whole dice string as
// base/dice/sides/bonus, where each of the 4 fields is independently
// absent, a literal NUMBER, or (base/sides only - see problems 1/2/4/5) a
// DOLLAR_LETTER variable. The ~25 alternatives below are every currently-
// supported (presence x literal-or-variable) combination of those 4 fields,
// built by hand rather than via a state machine like z-dice.c's C
// equivalent - see top-of-file comment for why that matters.
dice
        returns[Random random]
        @init {
            String baseStr = "";
            int baseInit = 0;
            int diceInit = 0;
            String sidesStr = "";
            int sidesInit = 1;
            int mBonusInit = 0;
        }
        @after {
            if (sidesInit < 1) sidesInit = 1;

            if (baseStr.isEmpty() && sidesStr.isEmpty())
                $random = new Random(baseInit, mBonusInit, diceInit, sidesInit, false);
            else if (baseStr.isEmpty())
                $random = new Random(baseInit, mBonusInit, diceInit, sidesStr);
            else
                $random = new Random(baseStr, mBonusInit, diceInit, sidesInit);
        }
        :   (base=DOLLAR_LETTER PLUS diceNum=NUMBER D sides=NUMBER M m_bonus=NUMBER {
                baseStr = $base.getText();
                diceInit = Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   base=DOLLAR_LETTER PLUS diceNum=NUMBER D sides=NUMBER {
                baseStr = $base.getText();
                diceInit = Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
            }
        |   MINUS base=NUMBER PLUS diceNum=NUMBER D sides=NUMBER M m_bonus=NUMBER {
                baseInit = -1 * Integer.parseInt($base.getText());
                diceInit = Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   base=NUMBER PLUS diceNum=NUMBER D sides=NUMBER M m_bonus=NUMBER {
                baseInit = Integer.parseInt($base.getText());
                diceInit = Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   MINUS base=NUMBER PLUS D sides=NUMBER M m_bonus=NUMBER {
                baseInit = -1 * Integer.parseInt($base.getText());
                sidesInit = Integer.parseInt($sides.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   base=DOLLAR_LETTER PLUS D sides=NUMBER M m_bonus=NUMBER {
                baseStr=$base.getText();
                sidesInit = Integer.parseInt($sides.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   base=NUMBER PLUS D sides=NUMBER M m_bonus=NUMBER {
                baseInit = Integer.parseInt($base.getText());
                sidesInit = Integer.parseInt($sides.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   diceNum=NUMBER D sides=NUMBER M m_bonus=NUMBER {
                diceInit = Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   D sides=NUMBER M m_bonus=NUMBER {
                sidesInit = Integer.parseInt($sides.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   base=NUMBER M m_bonus=NUMBER {
                baseInit = Integer.parseInt($base.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   base=NUMBER PLUS M m_bonus=NUMBER {
                baseInit = Integer.parseInt($base.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   base=DOLLAR_LETTER M m_bonus=NUMBER {
                baseStr = $base.getText();
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   MINUS base=NUMBER PLUS diceNum=NUMBER D sides=NUMBER {
                baseInit = -1 * Integer.parseInt($base.getText());
                diceInit = Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
            }
        |   base=NUMBER PLUS diceNum=NUMBER D sides=NUMBER {
                baseInit = Integer.parseInt($base.getText());
                diceInit = Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
            }
        |   MINUS base=NUMBER PLUS D sides=NUMBER {
                baseInit = -1 * Integer.parseInt($base.getText());
                sidesInit = Integer.parseInt($sides.getText());
            }
        |   base=NUMBER PLUS D sides=NUMBER {
                baseInit = Integer.parseInt($base.getText());
                sidesInit = Integer.parseInt($sides.getText());
            }
        |   diceNum=NUMBER D sides=NUMBER {
                diceInit = Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
            }
        |   D sides=NUMBER {
                sidesInit = Integer.parseInt($sides.getText());
            }
        |   base=DOLLAR_LETTER {
                baseStr = $base.getText();
            }
        |   base=NUMBER {
                baseInit = Integer.parseInt($base.getText());
            }
        |   MINUS base=NUMBER {
                baseInit = -1 * Integer.parseInt($base.getText());
            }
        |   diceNum=NUMBER D sides=DOLLAR_LETTER {
                diceInit = Integer.parseInt($diceNum.getText());
                sidesStr = $sides.getText();
            }
        |   MINUS diceNum=NUMBER D sides=NUMBER {
                diceInit = -1 * Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
            }
        |   M m_bonus=NUMBER{
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }) EOF
        ;

// The dice-count/sides separator, e.g. the 'd' in "2d8".
D
        :   'd'
        ;

// The bonus marker, e.g. the 'M' in "2d8M4" - accepts either case, matching
// z-dice.c's handling of 'M' and 'm' as equivalent bonus markers.
M
        :   'M'
        |   'm'
        ;

// Separator between a base value and a dice expression, e.g. the '+' in
// "1+2d3".
PLUS
        :   '+'
        ;

// Marks a literal base or dice-count as negative, e.g. the '-' in "-1+2d3".
MINUS
        :   '-'
        ;

// A bound dice-string variable: '$' followed by a single uppercase letter
// (e.g. "$S", "$D", "$B") - see problem #4 re: the single-letter restriction.
DOLLAR_LETTER
        :   '$' ('A'..'Z')
        ;

// A literal non-negative integer, used for any of base/dice/sides/bonus.
NUMBER
        :   ('0'..'9')+
        ;

// POTENTIAL SOLUTIONS
//
//   1/2. Add the missing diceNum=DOLLAR_LETTER alternatives - at minimum
//      `diceNum=DOLLAR_LETTER D sides=NUMBER` and
//      `diceNum=DOLLAR_LETTER D sides=DOLLAR_LETTER` (the "$Dd$S" case),
//      each also needing a `base=... PLUS` - prefixed and/or
//      `M m_bonus=...` - suffixed copy to stay consistent with how every
//      other dice-count form is already duplicated across base/bonus
//      combinations. That consistency requirement is itself the warning
//      sign: this rule is already ~25 alternatives for 4 independently
//      optional fields (base, dice, sides, bonus) x (absent | literal |
//      variable), and it's still missing combinations. z-dice.c's own doc
//      comment for dice_parse_string() effectively says the same
//      permutation space is why the C side uses a flat per-character state
//      machine instead of a grammar. Worth considering whether `dice`
//      should be rewritten the same way conceptually: parse base/dice/
//      sides/bonus as 4 independent, optionally-'$'-prefixed tokens
//      separated by the fixed '+'/'d'/'M' punctuation, and let the @after
//      block's existing isEmpty()-based dispatch (already written to
//      handle "baseStr vs baseInit, sidesStr vs sidesInt") do the
//      literal-vs-variable decision, rather than hand-enumerating every
//      legal combination as a separate alternative.

//   4/5. Only worth doing if lib/gamedata ever grows a multi-character dice
//      variable name or a negative variable base - neither appears today,
//      so this can stay as a documented gap rather than an active fix.