grammar Random;

@header {
    import uk.co.jackoftrades.backend.numerics.Random;
}

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
        |   base=NUMBER PLUS M m_bonus=NUMBER {
                baseInit = Integer.parseInt($base.getText());
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }
        |   MINUS diceNum=NUMBER D sides=NUMBER {
                diceInit = -1 * Integer.parseInt($diceNum.getText());
                sidesInit = Integer.parseInt($sides.getText());
            }
        |   M m_bonus=NUMBER{
                mBonusInit = Integer.parseInt($m_bonus.getText());
            }) EOF
        ;

D
        :   'd'
        ;

M
        :   'M'
        |   'm'
        ;

PLUS
        :   '+'
        ;

MINUS
        :   '-'
        ;

DOLLAR_LETTER
        :   '$' ('A'..'Z')
        ;

NUMBER
        :   ('0'..'9')+
        ;