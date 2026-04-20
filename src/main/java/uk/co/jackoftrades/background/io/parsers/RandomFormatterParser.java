// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/io/parsers/grammars/RandomFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.background.numerics.Random;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RandomFormatterParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LINE_START=1, COLON=2, MINUS=3, INT=4, PLUS=5, MULT=6, DIE=7;
	public static final int
		RULE_offset = 0, RULE_mult_int = 1, RULE_mult = 2, RULE_dice_int = 3, 
		RULE_int_dice_int = 4, RULE_plus_int_dice_int = 5, RULE_plus_dice_int = 6, 
		RULE_line = 7, RULE_random = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"offset", "mult_int", "mult", "dice_int", "int_dice_int", "plus_int_dice_int", 
			"plus_dice_int", "line", "random"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "':'", "'-'", null, "'+'", "'*'", "'d'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LINE_START", "COLON", "MINUS", "INT", "PLUS", "MULT", "DIE"
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
	public String getGrammarFileName() { return "RandomFormatter.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RandomFormatterParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OffsetContext extends ParserRuleContext {
		public boolean negate;
		public int base;
		public Token MINUS;
		public Token INT;
		public TerminalNode INT() { return getToken(RandomFormatterParser.INT, 0); }
		public TerminalNode MINUS() { return getToken(RandomFormatterParser.MINUS, 0); }
		public OffsetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_offset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).enterOffset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).exitOffset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomFormatterVisitor ) return ((RandomFormatterVisitor<? extends T>)visitor).visitOffset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OffsetContext offset() throws RecognitionException {
		OffsetContext _localctx = new OffsetContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_offset);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==MINUS) {
				{
				setState(18);
				((OffsetContext)_localctx).MINUS = match(MINUS);
				 ((OffsetContext)_localctx).negate =  ((OffsetContext)_localctx).MINUS == null ? false : true; 
				}
			}

			setState(22);
			((OffsetContext)_localctx).INT = match(INT);
			 ((OffsetContext)_localctx).base =  (((OffsetContext)_localctx).INT!=null?Integer.valueOf(((OffsetContext)_localctx).INT.getText()):0);
			 if (_localctx.negate) ((OffsetContext)_localctx).base =  _localctx.base * -1; 
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
	public static class Mult_intContext extends ParserRuleContext {
		public int dice;
		public Token INT;
		public TerminalNode MULT() { return getToken(RandomFormatterParser.MULT, 0); }
		public TerminalNode INT() { return getToken(RandomFormatterParser.INT, 0); }
		public Mult_intContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mult_int; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).enterMult_int(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).exitMult_int(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomFormatterVisitor ) return ((RandomFormatterVisitor<? extends T>)visitor).visitMult_int(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Mult_intContext mult_int() throws RecognitionException {
		Mult_intContext _localctx = new Mult_intContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_mult_int);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			match(MULT);
			 ((Mult_intContext)_localctx).dice =  1; 
			setState(29);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INT) {
				{
				setState(27);
				((Mult_intContext)_localctx).INT = match(INT);
				 ((Mult_intContext)_localctx).dice =  (((Mult_intContext)_localctx).INT!=null?Integer.valueOf(((Mult_intContext)_localctx).INT.getText()):0); 
				}
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
	public static class MultContext extends ParserRuleContext {
		public int mBonus;
		public int dice;
		public Token INT;
		public Mult_intContext mult_int;
		public TerminalNode INT() { return getToken(RandomFormatterParser.INT, 0); }
		public Mult_intContext mult_int() {
			return getRuleContext(Mult_intContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(RandomFormatterParser.PLUS, 0); }
		public MultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mult; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).enterMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).exitMult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomFormatterVisitor ) return ((RandomFormatterVisitor<? extends T>)visitor).visitMult(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultContext mult() throws RecognitionException {
		MultContext _localctx = new MultContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_mult);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS) {
				{
				setState(31);
				match(PLUS);
				}
			}

			setState(34);
			((MultContext)_localctx).INT = match(INT);
			setState(35);
			((MultContext)_localctx).mult_int = mult_int();
			 ((MultContext)_localctx).mBonus =  (((MultContext)_localctx).INT!=null?Integer.valueOf(((MultContext)_localctx).INT.getText()):0); ((MultContext)_localctx).dice =  ((MultContext)_localctx).mult_int.dice; 
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
	public static class Dice_intContext extends ParserRuleContext {
		public int sides;
		public Token INT;
		public TerminalNode DIE() { return getToken(RandomFormatterParser.DIE, 0); }
		public TerminalNode INT() { return getToken(RandomFormatterParser.INT, 0); }
		public Dice_intContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dice_int; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).enterDice_int(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).exitDice_int(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomFormatterVisitor ) return ((RandomFormatterVisitor<? extends T>)visitor).visitDice_int(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Dice_intContext dice_int() throws RecognitionException {
		Dice_intContext _localctx = new Dice_intContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_dice_int);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(DIE);
			setState(39);
			((Dice_intContext)_localctx).INT = match(INT);
			 ((Dice_intContext)_localctx).sides =  (((Dice_intContext)_localctx).INT!=null?Integer.valueOf(((Dice_intContext)_localctx).INT.getText()):0); 
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
	public static class Int_dice_intContext extends ParserRuleContext {
		public int dice;
		public int sides;
		public Token INT;
		public Dice_intContext dice_int;
		public TerminalNode INT() { return getToken(RandomFormatterParser.INT, 0); }
		public Dice_intContext dice_int() {
			return getRuleContext(Dice_intContext.class,0);
		}
		public Int_dice_intContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_int_dice_int; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).enterInt_dice_int(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).exitInt_dice_int(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomFormatterVisitor ) return ((RandomFormatterVisitor<? extends T>)visitor).visitInt_dice_int(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Int_dice_intContext int_dice_int() throws RecognitionException {
		Int_dice_intContext _localctx = new Int_dice_intContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_int_dice_int);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			((Int_dice_intContext)_localctx).INT = match(INT);
			setState(43);
			((Int_dice_intContext)_localctx).dice_int = dice_int();
			 ((Int_dice_intContext)_localctx).dice =  (((Int_dice_intContext)_localctx).INT!=null?Integer.valueOf(((Int_dice_intContext)_localctx).INT.getText()):0); ((Int_dice_intContext)_localctx).sides =  ((Int_dice_intContext)_localctx).dice_int.sides; 
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
	public static class Plus_int_dice_intContext extends ParserRuleContext {
		public int dice;
		public int sides;
		public Token INT;
		public Dice_intContext dice_int;
		public TerminalNode PLUS() { return getToken(RandomFormatterParser.PLUS, 0); }
		public TerminalNode INT() { return getToken(RandomFormatterParser.INT, 0); }
		public Dice_intContext dice_int() {
			return getRuleContext(Dice_intContext.class,0);
		}
		public Plus_int_dice_intContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plus_int_dice_int; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).enterPlus_int_dice_int(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).exitPlus_int_dice_int(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomFormatterVisitor ) return ((RandomFormatterVisitor<? extends T>)visitor).visitPlus_int_dice_int(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Plus_int_dice_intContext plus_int_dice_int() throws RecognitionException {
		Plus_int_dice_intContext _localctx = new Plus_int_dice_intContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_plus_int_dice_int);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(PLUS);
			setState(47);
			((Plus_int_dice_intContext)_localctx).INT = match(INT);
			setState(48);
			((Plus_int_dice_intContext)_localctx).dice_int = dice_int();
			 ((Plus_int_dice_intContext)_localctx).dice =  (((Plus_int_dice_intContext)_localctx).INT!=null?Integer.valueOf(((Plus_int_dice_intContext)_localctx).INT.getText()):0); ((Plus_int_dice_intContext)_localctx).sides =  ((Plus_int_dice_intContext)_localctx).dice_int.sides; 
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
	public static class Plus_dice_intContext extends ParserRuleContext {
		public int dice;
		public int sides;
		public Dice_intContext dice_int;
		public TerminalNode PLUS() { return getToken(RandomFormatterParser.PLUS, 0); }
		public Dice_intContext dice_int() {
			return getRuleContext(Dice_intContext.class,0);
		}
		public Plus_dice_intContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plus_dice_int; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).enterPlus_dice_int(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).exitPlus_dice_int(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomFormatterVisitor ) return ((RandomFormatterVisitor<? extends T>)visitor).visitPlus_dice_int(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Plus_dice_intContext plus_dice_int() throws RecognitionException {
		Plus_dice_intContext _localctx = new Plus_dice_intContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_plus_dice_int);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(PLUS);
			setState(52);
			((Plus_dice_intContext)_localctx).dice_int = dice_int();
			 ((Plus_dice_intContext)_localctx).dice =  1; ((Plus_dice_intContext)_localctx).sides =  ((Plus_dice_intContext)_localctx).dice_int.sides; 
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
	public static class LineContext extends ParserRuleContext {
		public Random rand;
		public String route;
		public RandomContext random;
		public TerminalNode LINE_START() { return getToken(RandomFormatterParser.LINE_START, 0); }
		public RandomContext random() {
			return getRuleContext(RandomContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).exitLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomFormatterVisitor ) return ((RandomFormatterVisitor<? extends T>)visitor).visitLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_line);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			match(LINE_START);
			setState(56);
			((LineContext)_localctx).random = random();
			((LineContext)_localctx).rand =  ((LineContext)_localctx).random.randomDice; ((LineContext)_localctx).route =  ((LineContext)_localctx).random.route; 
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
	public static class RandomContext extends ParserRuleContext {
		public Random randomDice;
		public String route;
		public OffsetContext offset;
		public Int_dice_intContext int_dice_int;
		public MultContext mult;
		public Dice_intContext dice_int;
		public Plus_dice_intContext plus_dice_int;
		public Plus_int_dice_intContext plus_int_dice_int;
		public OffsetContext offset() {
			return getRuleContext(OffsetContext.class,0);
		}
		public Int_dice_intContext int_dice_int() {
			return getRuleContext(Int_dice_intContext.class,0);
		}
		public MultContext mult() {
			return getRuleContext(MultContext.class,0);
		}
		public Dice_intContext dice_int() {
			return getRuleContext(Dice_intContext.class,0);
		}
		public Plus_dice_intContext plus_dice_int() {
			return getRuleContext(Plus_dice_intContext.class,0);
		}
		public Plus_int_dice_intContext plus_int_dice_int() {
			return getRuleContext(Plus_int_dice_intContext.class,0);
		}
		public RandomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_random; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).enterRandom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RandomFormatterListener ) ((RandomFormatterListener)listener).exitRandom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RandomFormatterVisitor ) return ((RandomFormatterVisitor<? extends T>)visitor).visitRandom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RandomContext random() throws RecognitionException {
		RandomContext _localctx = new RandomContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_random);
		try {
			setState(85);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(59);
				((RandomContext)_localctx).offset = offset();

				        ((RandomContext)_localctx).randomDice =  new Random(((RandomContext)_localctx).offset.base, 1, 1, 0, ((RandomContext)_localctx).offset.negate);
				        ((RandomContext)_localctx).route =  "offset"; 
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(62);
				((RandomContext)_localctx).int_dice_int = int_dice_int();

				        ((RandomContext)_localctx).randomDice =  new Random(0, 1, ((RandomContext)_localctx).int_dice_int.dice, ((RandomContext)_localctx).int_dice_int.sides, false);
				        ((RandomContext)_localctx).route =  "int_dice_int"; 
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(65);
				((RandomContext)_localctx).mult = mult();
				setState(66);
				((RandomContext)_localctx).dice_int = dice_int();

				        ((RandomContext)_localctx).randomDice =  new Random(0, ((RandomContext)_localctx).mult.mBonus, ((RandomContext)_localctx).mult.dice, ((RandomContext)_localctx).dice_int.sides, false);
				        ((RandomContext)_localctx).route =  "mult dice_int"; 
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(69);
				((RandomContext)_localctx).offset = offset();
				setState(70);
				((RandomContext)_localctx).mult = mult();
				setState(71);
				((RandomContext)_localctx).dice_int = dice_int();

				        ((RandomContext)_localctx).randomDice =  new Random(((RandomContext)_localctx).offset.base, ((RandomContext)_localctx).mult.mBonus, ((RandomContext)_localctx).mult.dice, ((RandomContext)_localctx).dice_int.sides, ((RandomContext)_localctx).offset.negate);
				        ((RandomContext)_localctx).route =  "offset mult dice_int"; 
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(74);
				((RandomContext)_localctx).dice_int = dice_int();

				        ((RandomContext)_localctx).randomDice =  new Random(0, 1, 1, ((RandomContext)_localctx).dice_int.sides, false);
				        ((RandomContext)_localctx).route =  "dice_int"; 
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(77);
				((RandomContext)_localctx).offset = offset();
				setState(78);
				((RandomContext)_localctx).plus_dice_int = plus_dice_int();

				        ((RandomContext)_localctx).randomDice =  new Random(((RandomContext)_localctx).offset.base, 1, 1, ((RandomContext)_localctx).plus_dice_int.sides, ((RandomContext)_localctx).offset.negate);
				        ((RandomContext)_localctx).route =  "offset plus_dice_int"; 
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(81);
				((RandomContext)_localctx).offset = offset();
				setState(82);
				((RandomContext)_localctx).plus_int_dice_int = plus_int_dice_int();

				        ((RandomContext)_localctx).randomDice =  new Random(((RandomContext)_localctx).offset.base, 1, ((RandomContext)_localctx).plus_int_dice_int.dice, ((RandomContext)_localctx).plus_int_dice_int.sides, ((RandomContext)_localctx).offset.negate);
				        ((RandomContext)_localctx).route =  "offset plus_int_dice_int"; 
				}
				break;
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

	public static final String _serializedATN =
		"\u0004\u0001\u0007X\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0001\u0000\u0001\u0000\u0003\u0000\u0015\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0003\u0001\u001e\b\u0001\u0001\u0002\u0003\u0002!\b\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003"+
		"\bV\b\b\u0001\b\u0000\u0000\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0000\u0000W\u0000\u0014\u0001\u0000\u0000\u0000\u0002\u0019\u0001\u0000"+
		"\u0000\u0000\u0004 \u0001\u0000\u0000\u0000\u0006&\u0001\u0000\u0000\u0000"+
		"\b*\u0001\u0000\u0000\u0000\n.\u0001\u0000\u0000\u0000\f3\u0001\u0000"+
		"\u0000\u0000\u000e7\u0001\u0000\u0000\u0000\u0010U\u0001\u0000\u0000\u0000"+
		"\u0012\u0013\u0005\u0003\u0000\u0000\u0013\u0015\u0006\u0000\uffff\uffff"+
		"\u0000\u0014\u0012\u0001\u0000\u0000\u0000\u0014\u0015\u0001\u0000\u0000"+
		"\u0000\u0015\u0016\u0001\u0000\u0000\u0000\u0016\u0017\u0005\u0004\u0000"+
		"\u0000\u0017\u0018\u0006\u0000\uffff\uffff\u0000\u0018\u0001\u0001\u0000"+
		"\u0000\u0000\u0019\u001a\u0005\u0006\u0000\u0000\u001a\u001d\u0006\u0001"+
		"\uffff\uffff\u0000\u001b\u001c\u0005\u0004\u0000\u0000\u001c\u001e\u0006"+
		"\u0001\uffff\uffff\u0000\u001d\u001b\u0001\u0000\u0000\u0000\u001d\u001e"+
		"\u0001\u0000\u0000\u0000\u001e\u0003\u0001\u0000\u0000\u0000\u001f!\u0005"+
		"\u0005\u0000\u0000 \u001f\u0001\u0000\u0000\u0000 !\u0001\u0000\u0000"+
		"\u0000!\"\u0001\u0000\u0000\u0000\"#\u0005\u0004\u0000\u0000#$\u0003\u0002"+
		"\u0001\u0000$%\u0006\u0002\uffff\uffff\u0000%\u0005\u0001\u0000\u0000"+
		"\u0000&\'\u0005\u0007\u0000\u0000\'(\u0005\u0004\u0000\u0000()\u0006\u0003"+
		"\uffff\uffff\u0000)\u0007\u0001\u0000\u0000\u0000*+\u0005\u0004\u0000"+
		"\u0000+,\u0003\u0006\u0003\u0000,-\u0006\u0004\uffff\uffff\u0000-\t\u0001"+
		"\u0000\u0000\u0000./\u0005\u0005\u0000\u0000/0\u0005\u0004\u0000\u0000"+
		"01\u0003\u0006\u0003\u000012\u0006\u0005\uffff\uffff\u00002\u000b\u0001"+
		"\u0000\u0000\u000034\u0005\u0005\u0000\u000045\u0003\u0006\u0003\u0000"+
		"56\u0006\u0006\uffff\uffff\u00006\r\u0001\u0000\u0000\u000078\u0005\u0001"+
		"\u0000\u000089\u0003\u0010\b\u00009:\u0006\u0007\uffff\uffff\u0000:\u000f"+
		"\u0001\u0000\u0000\u0000;<\u0003\u0000\u0000\u0000<=\u0006\b\uffff\uffff"+
		"\u0000=V\u0001\u0000\u0000\u0000>?\u0003\b\u0004\u0000?@\u0006\b\uffff"+
		"\uffff\u0000@V\u0001\u0000\u0000\u0000AB\u0003\u0004\u0002\u0000BC\u0003"+
		"\u0006\u0003\u0000CD\u0006\b\uffff\uffff\u0000DV\u0001\u0000\u0000\u0000"+
		"EF\u0003\u0000\u0000\u0000FG\u0003\u0004\u0002\u0000GH\u0003\u0006\u0003"+
		"\u0000HI\u0006\b\uffff\uffff\u0000IV\u0001\u0000\u0000\u0000JK\u0003\u0006"+
		"\u0003\u0000KL\u0006\b\uffff\uffff\u0000LV\u0001\u0000\u0000\u0000MN\u0003"+
		"\u0000\u0000\u0000NO\u0003\f\u0006\u0000OP\u0006\b\uffff\uffff\u0000P"+
		"V\u0001\u0000\u0000\u0000QR\u0003\u0000\u0000\u0000RS\u0003\n\u0005\u0000"+
		"ST\u0006\b\uffff\uffff\u0000TV\u0001\u0000\u0000\u0000U;\u0001\u0000\u0000"+
		"\u0000U>\u0001\u0000\u0000\u0000UA\u0001\u0000\u0000\u0000UE\u0001\u0000"+
		"\u0000\u0000UJ\u0001\u0000\u0000\u0000UM\u0001\u0000\u0000\u0000UQ\u0001"+
		"\u0000\u0000\u0000V\u0011\u0001\u0000\u0000\u0000\u0004\u0014\u001d U";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}