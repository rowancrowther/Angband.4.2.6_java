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

// Generated from Random.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.random;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.middle.numerics.Random;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RandomParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SPACE=1, D=2, M=3, PLUS=4, MINUS=5, DOLLAR_LETTER=6, SPACE_NUMBER=7, NUMBER=8;
	public static final int
		RULE_bonus = 0, RULE_dice = 1;
	private static String[] makeRuleNames() {
		return new String[] {
			"bonus", "dice"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'d'", null, "'+'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "SPACE", "D", "M", "PLUS", "MINUS", "DOLLAR_LETTER", "SPACE_NUMBER", 
			"NUMBER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Random.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


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

	public RandomParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BonusContext extends ParserRuleContext {
		public int bonusVal;
		public Token m1;
		public Token m2;
		public List<TerminalNode> SPACE_NUMBER() { return getTokens(RandomParser.SPACE_NUMBER); }
		public TerminalNode SPACE_NUMBER(int i) {
			return getToken(RandomParser.SPACE_NUMBER, i);
		}
		public List<TerminalNode> M() { return getTokens(RandomParser.M); }
		public TerminalNode M(int i) {
			return getToken(RandomParser.M, i);
		}
		public BonusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bonus; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomListener ) ((RandomListener)listener).enterBonus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomListener ) ((RandomListener)listener).exitBonus(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomVisitor ) return ((RandomVisitor<? extends T>)visitor).visitBonus(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BonusContext bonus() throws RecognitionException {
		BonusContext _localctx = new BonusContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_bonus);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(7);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==M) {
				{
				{
				setState(4);
				match(M);
				}
				}
				setState(9);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(10);
			((BonusContext)_localctx).m1 = match(SPACE_NUMBER);
			 ((BonusContext)_localctx).bonusVal =  numberOrMissing(((BonusContext)_localctx).m1); 
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==M) {
				{
				{
				setState(13); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(12);
					match(M);
					}
					}
					setState(15); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==M );
				setState(17);
				((BonusContext)_localctx).m2 = match(SPACE_NUMBER);
				 ((BonusContext)_localctx).bonusVal =  numberOrMissing(((BonusContext)_localctx).m2); 
				}
				}
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DiceContext extends ParserRuleContext {
		public Random random;
		public Token b;
		public Token d;
		public Token s;
		public BonusContext bonus;
		public Token m1;
		public Token m2;
		public TerminalNode EOF() { return getToken(RandomParser.EOF, 0); }
		public TerminalNode PLUS() { return getToken(RandomParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(RandomParser.MINUS, 0); }
		public List<TerminalNode> SPACE_NUMBER() { return getTokens(RandomParser.SPACE_NUMBER); }
		public TerminalNode SPACE_NUMBER(int i) {
			return getToken(RandomParser.SPACE_NUMBER, i);
		}
		public TerminalNode SPACE() { return getToken(RandomParser.SPACE, 0); }
		public List<TerminalNode> D() { return getTokens(RandomParser.D); }
		public TerminalNode D(int i) {
			return getToken(RandomParser.D, i);
		}
		public BonusContext bonus() {
			return getRuleContext(BonusContext.class,0);
		}
		public List<TerminalNode> M() { return getTokens(RandomParser.M); }
		public TerminalNode M(int i) {
			return getToken(RandomParser.M, i);
		}
		public DiceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomListener ) ((RandomListener)listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomListener ) ((RandomListener)listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomVisitor ) return ((RandomVisitor<? extends T>)visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_dice);

		            String baseStr = "";
		            int baseInit = 0;
		            int diceInit = 0;
		            String sidesStr = "";
		            int sidesInit = 0;
		            int mBonusInit = 0;
		            boolean negated = false;
		            int bonusInit = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==MINUS) {
				{
				setState(24);
				match(MINUS);
				 negated = true; 
				}
			}

			setState(127);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				{
				setState(28);
				((DiceContext)_localctx).b = match(SPACE_NUMBER);
				 baseInit = numberOrMissing(((DiceContext)_localctx).b); 
				}
				break;
			case 2:
				{
				setState(30);
				((DiceContext)_localctx).b = match(SPACE_NUMBER);
				setState(31);
				match(PLUS);
				setState(32);
				((DiceContext)_localctx).d = match(SPACE_NUMBER);
				setState(36);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==D) {
					{
					{
					setState(33);
					match(D);
					}
					}
					setState(38);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(39);
				((DiceContext)_localctx).s = match(SPACE_NUMBER);
				setState(43);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==M || _la==SPACE_NUMBER) {
					{
					setState(40);
					((DiceContext)_localctx).bonus = bonus();
					mBonusInit = ((DiceContext)_localctx).bonus.bonusVal; 
					}
				}

				 baseInit = numberOrMissing(((DiceContext)_localctx).b);
				                diceInit = numberOrMissing(((DiceContext)_localctx).d);
				                sidesInit = numberOrMissing(((DiceContext)_localctx).s); 
				}
				break;
			case 3:
				{
				setState(46);
				((DiceContext)_localctx).b = match(SPACE_NUMBER);
				setState(47);
				match(PLUS);
				setState(49); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(48);
					match(D);
					}
					}
					setState(51); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==D );
				setState(53);
				((DiceContext)_localctx).s = match(SPACE_NUMBER);

				                baseInit = numberOrMissing(((DiceContext)_localctx).b);
				                sidesInit = numberOrMissing(((DiceContext)_localctx).s);
				                diceInit = 1;
				            
				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==M || _la==SPACE_NUMBER) {
					{
					setState(55);
					((DiceContext)_localctx).bonus = bonus();
					mBonusInit = ((DiceContext)_localctx).bonus.bonusVal; 
					}
				}

				}
				break;
			case 4:
				{
				setState(60);
				((DiceContext)_localctx).b = match(SPACE_NUMBER);
				setState(61);
				match(PLUS);
				setState(63); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(62);
					match(M);
					}
					}
					setState(65); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==M );
				setState(67);
				((DiceContext)_localctx).m1 = match(SPACE_NUMBER);
				 bonusInit = numberOrMissing(((DiceContext)_localctx).m1); 
				setState(78);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==M) {
					{
					{
					setState(70); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(69);
						match(M);
						}
						}
						setState(72); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==M );
					setState(74);
					((DiceContext)_localctx).m2 = match(SPACE_NUMBER);
					 bonusInit = numberOrMissing(((DiceContext)_localctx).m2); 
					}
					}
					setState(80);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}

				                baseInit = numberOrMissing(((DiceContext)_localctx).b);
				                mBonusInit = bonusInit;
				            
				}
				break;
			case 5:
				{
				setState(82);
				((DiceContext)_localctx).d = match(SPACE_NUMBER);
				setState(84); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(83);
					match(D);
					}
					}
					setState(86); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==D );
				setState(88);
				((DiceContext)_localctx).s = match(SPACE_NUMBER);
				setState(92);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==M || _la==SPACE_NUMBER) {
					{
					setState(89);
					((DiceContext)_localctx).bonus = bonus();
					mBonusInit = ((DiceContext)_localctx).bonus.bonusVal; 
					}
				}


				                diceInit = numberOrMissing(((DiceContext)_localctx).d);
				                sidesInit = numberOrMissing(((DiceContext)_localctx).s);
				            
				}
				break;
			case 6:
				{
				setState(96); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(95);
					match(D);
					}
					}
					setState(98); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==D );
				setState(100);
				((DiceContext)_localctx).s = match(SPACE_NUMBER);
				 sidesInit = numberOrMissing(((DiceContext)_localctx).s); diceInit = 1; 
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==M || _la==SPACE_NUMBER) {
					{
					setState(102);
					((DiceContext)_localctx).bonus = bonus();
					mBonusInit = ((DiceContext)_localctx).bonus.bonusVal; 
					}
				}

				}
				break;
			case 7:
				{
				setState(108); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(107);
					match(M);
					}
					}
					setState(110); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==M );
				setState(112);
				((DiceContext)_localctx).m1 = match(SPACE_NUMBER);
				 bonusInit = numberOrMissing(((DiceContext)_localctx).m1); 
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==M) {
					{
					{
					setState(115); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(114);
						match(M);
						}
						}
						setState(117); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==M );
					setState(119);
					((DiceContext)_localctx).m2 = match(SPACE_NUMBER);
					 bonusInit = numberOrMissing(((DiceContext)_localctx).m2); 
					}
					}
					setState(125);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}

				                mBonusInit = bonusInit;
				                bonusInit = 0;
				            
				}
				break;
			}
			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SPACE) {
				{
				setState(129);
				match(SPACE);
				}
			}

			setState(132);
			match(EOF);
			}
			_ctx.stop = _input.LT(-1);

			            if (mBonusInit == -1 || diceInit == -1 || sidesInit == -1 || baseInit == -1)
			                ((DiceContext)_localctx).random =  null;            
			            else if (baseStr.isEmpty() && sidesStr.isEmpty())
			                ((DiceContext)_localctx).random =  new Random(baseInit, mBonusInit, diceInit, sidesInit, negated);
			            else if (baseStr.isEmpty())
			                ((DiceContext)_localctx).random =  new Random(baseInit, mBonusInit, diceInit, sidesStr);
			            else
			                ((DiceContext)_localctx).random =  new Random(baseStr, mBonusInit, diceInit, sidesInit);
			        
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\b\u0087\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0001"+
		"\u0000\u0005\u0000\u0006\b\u0000\n\u0000\f\u0000\t\t\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0004\u0000\u000e\b\u0000\u000b\u0000\f\u0000"+
		"\u000f\u0001\u0000\u0001\u0000\u0005\u0000\u0014\b\u0000\n\u0000\f\u0000"+
		"\u0017\t\u0000\u0001\u0001\u0001\u0001\u0003\u0001\u001b\b\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0005"+
		"\u0001#\b\u0001\n\u0001\f\u0001&\t\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0003\u0001,\b\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0004\u00012\b\u0001\u000b\u0001\f\u00013\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001;\b\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0004\u0001@\b\u0001\u000b\u0001"+
		"\f\u0001A\u0001\u0001\u0001\u0001\u0001\u0001\u0004\u0001G\b\u0001\u000b"+
		"\u0001\f\u0001H\u0001\u0001\u0001\u0001\u0005\u0001M\b\u0001\n\u0001\f"+
		"\u0001P\t\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0004\u0001U\b\u0001"+
		"\u000b\u0001\f\u0001V\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0003\u0001]\b\u0001\u0001\u0001\u0001\u0001\u0004\u0001a\b\u0001\u000b"+
		"\u0001\f\u0001b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0003\u0001j\b\u0001\u0001\u0001\u0004\u0001m\b\u0001\u000b\u0001"+
		"\f\u0001n\u0001\u0001\u0001\u0001\u0001\u0001\u0004\u0001t\b\u0001\u000b"+
		"\u0001\f\u0001u\u0001\u0001\u0001\u0001\u0005\u0001z\b\u0001\n\u0001\f"+
		"\u0001}\t\u0001\u0001\u0001\u0003\u0001\u0080\b\u0001\u0001\u0001\u0003"+
		"\u0001\u0083\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0000\u0000\u0002"+
		"\u0000\u0002\u0000\u0000\u009d\u0000\u0007\u0001\u0000\u0000\u0000\u0002"+
		"\u001a\u0001\u0000\u0000\u0000\u0004\u0006\u0005\u0003\u0000\u0000\u0005"+
		"\u0004\u0001\u0000\u0000\u0000\u0006\t\u0001\u0000\u0000\u0000\u0007\u0005"+
		"\u0001\u0000\u0000\u0000\u0007\b\u0001\u0000\u0000\u0000\b\n\u0001\u0000"+
		"\u0000\u0000\t\u0007\u0001\u0000\u0000\u0000\n\u000b\u0005\u0007\u0000"+
		"\u0000\u000b\u0015\u0006\u0000\uffff\uffff\u0000\f\u000e\u0005\u0003\u0000"+
		"\u0000\r\f\u0001\u0000\u0000\u0000\u000e\u000f\u0001\u0000\u0000\u0000"+
		"\u000f\r\u0001\u0000\u0000\u0000\u000f\u0010\u0001\u0000\u0000\u0000\u0010"+
		"\u0011\u0001\u0000\u0000\u0000\u0011\u0012\u0005\u0007\u0000\u0000\u0012"+
		"\u0014\u0006\u0000\uffff\uffff\u0000\u0013\r\u0001\u0000\u0000\u0000\u0014"+
		"\u0017\u0001\u0000\u0000\u0000\u0015\u0013\u0001\u0000\u0000\u0000\u0015"+
		"\u0016\u0001\u0000\u0000\u0000\u0016\u0001\u0001\u0000\u0000\u0000\u0017"+
		"\u0015\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0005\u0000\u0000\u0019"+
		"\u001b\u0006\u0001\uffff\uffff\u0000\u001a\u0018\u0001\u0000\u0000\u0000"+
		"\u001a\u001b\u0001\u0000\u0000\u0000\u001b\u007f\u0001\u0000\u0000\u0000"+
		"\u001c\u001d\u0005\u0007\u0000\u0000\u001d\u0080\u0006\u0001\uffff\uffff"+
		"\u0000\u001e\u001f\u0005\u0007\u0000\u0000\u001f \u0005\u0004\u0000\u0000"+
		" $\u0005\u0007\u0000\u0000!#\u0005\u0002\u0000\u0000\"!\u0001\u0000\u0000"+
		"\u0000#&\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000$%\u0001\u0000"+
		"\u0000\u0000%\'\u0001\u0000\u0000\u0000&$\u0001\u0000\u0000\u0000\'+\u0005"+
		"\u0007\u0000\u0000()\u0003\u0000\u0000\u0000)*\u0006\u0001\uffff\uffff"+
		"\u0000*,\u0001\u0000\u0000\u0000+(\u0001\u0000\u0000\u0000+,\u0001\u0000"+
		"\u0000\u0000,-\u0001\u0000\u0000\u0000-\u0080\u0006\u0001\uffff\uffff"+
		"\u0000./\u0005\u0007\u0000\u0000/1\u0005\u0004\u0000\u000002\u0005\u0002"+
		"\u0000\u000010\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000031\u0001"+
		"\u0000\u0000\u000034\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u0000"+
		"56\u0005\u0007\u0000\u00006:\u0006\u0001\uffff\uffff\u000078\u0003\u0000"+
		"\u0000\u000089\u0006\u0001\uffff\uffff\u00009;\u0001\u0000\u0000\u0000"+
		":7\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;\u0080\u0001\u0000"+
		"\u0000\u0000<=\u0005\u0007\u0000\u0000=?\u0005\u0004\u0000\u0000>@\u0005"+
		"\u0003\u0000\u0000?>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000"+
		"A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000"+
		"\u0000CD\u0005\u0007\u0000\u0000DN\u0006\u0001\uffff\uffff\u0000EG\u0005"+
		"\u0003\u0000\u0000FE\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000"+
		"HF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000"+
		"\u0000JK\u0005\u0007\u0000\u0000KM\u0006\u0001\uffff\uffff\u0000LF\u0001"+
		"\u0000\u0000\u0000MP\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000"+
		"NO\u0001\u0000\u0000\u0000OQ\u0001\u0000\u0000\u0000PN\u0001\u0000\u0000"+
		"\u0000Q\u0080\u0006\u0001\uffff\uffff\u0000RT\u0005\u0007\u0000\u0000"+
		"SU\u0005\u0002\u0000\u0000TS\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000"+
		"\u0000VT\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000WX\u0001\u0000"+
		"\u0000\u0000X\\\u0005\u0007\u0000\u0000YZ\u0003\u0000\u0000\u0000Z[\u0006"+
		"\u0001\uffff\uffff\u0000[]\u0001\u0000\u0000\u0000\\Y\u0001\u0000\u0000"+
		"\u0000\\]\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^\u0080\u0006"+
		"\u0001\uffff\uffff\u0000_a\u0005\u0002\u0000\u0000`_\u0001\u0000\u0000"+
		"\u0000ab\u0001\u0000\u0000\u0000b`\u0001\u0000\u0000\u0000bc\u0001\u0000"+
		"\u0000\u0000cd\u0001\u0000\u0000\u0000de\u0005\u0007\u0000\u0000ei\u0006"+
		"\u0001\uffff\uffff\u0000fg\u0003\u0000\u0000\u0000gh\u0006\u0001\uffff"+
		"\uffff\u0000hj\u0001\u0000\u0000\u0000if\u0001\u0000\u0000\u0000ij\u0001"+
		"\u0000\u0000\u0000j\u0080\u0001\u0000\u0000\u0000km\u0005\u0003\u0000"+
		"\u0000lk\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000nl\u0001\u0000"+
		"\u0000\u0000no\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000pq\u0005"+
		"\u0007\u0000\u0000q{\u0006\u0001\uffff\uffff\u0000rt\u0005\u0003\u0000"+
		"\u0000sr\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000us\u0001\u0000"+
		"\u0000\u0000uv\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000wx\u0005"+
		"\u0007\u0000\u0000xz\u0006\u0001\uffff\uffff\u0000ys\u0001\u0000\u0000"+
		"\u0000z}\u0001\u0000\u0000\u0000{y\u0001\u0000\u0000\u0000{|\u0001\u0000"+
		"\u0000\u0000|~\u0001\u0000\u0000\u0000}{\u0001\u0000\u0000\u0000~\u0080"+
		"\u0006\u0001\uffff\uffff\u0000\u007f\u001c\u0001\u0000\u0000\u0000\u007f"+
		"\u001e\u0001\u0000\u0000\u0000\u007f.\u0001\u0000\u0000\u0000\u007f<\u0001"+
		"\u0000\u0000\u0000\u007fR\u0001\u0000\u0000\u0000\u007f`\u0001\u0000\u0000"+
		"\u0000\u007fl\u0001\u0000\u0000\u0000\u0080\u0082\u0001\u0000\u0000\u0000"+
		"\u0081\u0083\u0005\u0001\u0000\u0000\u0082\u0081\u0001\u0000\u0000\u0000"+
		"\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000"+
		"\u0084\u0085\u0005\u0000\u0000\u0001\u0085\u0003\u0001\u0000\u0000\u0000"+
		"\u0014\u0007\u000f\u0015\u001a$+3:AHNV\\binu{\u007f\u0082";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}