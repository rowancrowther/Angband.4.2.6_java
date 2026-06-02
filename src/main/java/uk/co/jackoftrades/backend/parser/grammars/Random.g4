grammar Random;

@header {
    import uk.co.jackoftrades.backend.numerics.Random;
}

dice
        returns[Random random]
        @init {
            int baseInit = 0;
            int diceInit = 0;
            int sidesInit = 1;
            int mBonusInit = 0;
        }
        @after {
            if (sidesInit < 1) sidesInit = 1;

            $random = new Random(baseInit, mBonusInit, diceInit, sidesInit, false);
        }
        :   (MINUS base=NUMBER PLUS diceNum=NUMBER D sides=NUMBER M m_bonus=NUMBER {
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
        |   base=NUMBER {
                baseInit = Integer.parseInt($base.getText());
            } ) EOF
        ;

D
        :   'd'
        ;

M
        :   'M'
        ;

PLUS
        :   '+'
        ;

MINUS
        :   '-'
        ;

NUMBER
        :   ('0'..'9')+
        ;