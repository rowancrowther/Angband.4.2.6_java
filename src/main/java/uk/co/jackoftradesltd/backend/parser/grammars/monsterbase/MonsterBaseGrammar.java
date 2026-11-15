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

// Generated from MonsterBaseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.monsterbase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterBaseGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, GLYPH=2, PAIN=3, FLAGS=4, DESC=5, UINT=6, COMMENT=7, INDENT=8, 
		EOL=9, SINGLE_CHAR=10, STRING=11, FLAG=12, FLAG_OR=13, FLAG_EOL=14;
	public static final int
		RULE_name = 0, RULE_glyph = 1, RULE_pain = 2, RULE_flags = 3, RULE_desc = 4, 
		RULE_monsterBase = 5, RULE_file = 6;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "glyph", "pain", "flags", "desc", "monsterBase", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'glyph:'", "'pain:'", "'flags:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "GLYPH", "PAIN", "FLAGS", "DESC", "UINT", "COMMENT", "INDENT", 
			"EOL", "SINGLE_CHAR", "STRING", "FLAG", "FLAG_OR", "FLAG_EOL"
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
	public String getGrammarFileName() { return "MonsterBaseGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MonsterBaseGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int lineNo;
		public Token STRING;
		public TerminalNode NAME() { return getToken(MonsterBaseGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(MonsterBaseGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterBaseGrammarVisitor ) return ((MonsterBaseGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(14);
			match(NAME);
			setState(15);
			((NameContext)_localctx).STRING = match(STRING);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).STRING.getText();
			                          ((NameContext)_localctx).lineNo =  _localctx.start.getLine(); 
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
	public static class GlyphContext extends ParserRuleContext {
		public String glyphChar;
		public Token SINGLE_CHAR;
		public TerminalNode GLYPH() { return getToken(MonsterBaseGrammar.GLYPH, 0); }
		public TerminalNode SINGLE_CHAR() { return getToken(MonsterBaseGrammar.SINGLE_CHAR, 0); }
		public GlyphContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_glyph; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).enterGlyph(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).exitGlyph(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterBaseGrammarVisitor ) return ((MonsterBaseGrammarVisitor<? extends T>)visitor).visitGlyph(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GlyphContext glyph() throws RecognitionException {
		GlyphContext _localctx = new GlyphContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_glyph);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			match(GLYPH);
			setState(19);
			((GlyphContext)_localctx).SINGLE_CHAR = match(SINGLE_CHAR);

			            ((GlyphContext)_localctx).glyphChar =  ((GlyphContext)_localctx).SINGLE_CHAR.getText();
			        
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
	public static class PainContext extends ParserRuleContext {
		public String monPain;
		public Token UINT;
		public TerminalNode PAIN() { return getToken(MonsterBaseGrammar.PAIN, 0); }
		public TerminalNode UINT() { return getToken(MonsterBaseGrammar.UINT, 0); }
		public PainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).enterPain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).exitPain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterBaseGrammarVisitor ) return ((MonsterBaseGrammarVisitor<? extends T>)visitor).visitPain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PainContext pain() throws RecognitionException {
		PainContext _localctx = new PainContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_pain);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			match(PAIN);
			setState(23);
			((PainContext)_localctx).UINT = match(UINT);

			            ((PainContext)_localctx).monPain =  ((PainContext)_localctx).UINT.getText();
			        
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
	public static class FlagsContext extends ParserRuleContext {
		public List<String> raceFlags;
		public Token flag1;
		public Token flag2;
		public TerminalNode FLAGS() { return getToken(MonsterBaseGrammar.FLAGS, 0); }
		public TerminalNode FLAG_EOL() { return getToken(MonsterBaseGrammar.FLAG_EOL, 0); }
		public TerminalNode EOF() { return getToken(MonsterBaseGrammar.EOF, 0); }
		public List<TerminalNode> FLAG() { return getTokens(MonsterBaseGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(MonsterBaseGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(MonsterBaseGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(MonsterBaseGrammar.FLAG_OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterBaseGrammarVisitor ) return ((MonsterBaseGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_flags);

		            ((FlagsContext)_localctx).raceFlags =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			match(FLAGS);
			setState(44);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FLAG_OR:
				{
				setState(28); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(27);
					match(FLAG_OR);
					}
					}
					setState(30); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==FLAG_OR );
				}
				break;
			case FLAG:
				{
				setState(32);
				((FlagsContext)_localctx).flag1 = match(FLAG);
				 _localctx.raceFlags.add(((FlagsContext)_localctx).flag1.getText()); 
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FLAG_OR) {
					{
					{
					setState(34);
					match(FLAG_OR);
					setState(37);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==FLAG) {
						{
						setState(35);
						((FlagsContext)_localctx).flag2 = match(FLAG);
						 _localctx.raceFlags.add(((FlagsContext)_localctx).flag2.getText()); 
						}
					}

					}
					}
					setState(43);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case EOF:
			case FLAG_EOL:
				break;
			default:
				break;
			}
			setState(46);
			_la = _input.LA(1);
			if ( !(_la==EOF || _la==FLAG_EOL) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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
	public static class DescContext extends ParserRuleContext {
		public String description;
		public Token STRING;
		public List<TerminalNode> DESC() { return getTokens(MonsterBaseGrammar.DESC); }
		public TerminalNode DESC(int i) {
			return getToken(MonsterBaseGrammar.DESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(MonsterBaseGrammar.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(MonsterBaseGrammar.STRING, i);
		}
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterBaseGrammarVisitor ) return ((MonsterBaseGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_desc);

		            StringBuilder sb = new StringBuilder();
		        
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(51); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(48);
					match(DESC);
					setState(49);
					((DescContext)_localctx).STRING = match(STRING);
					 sb.append(((DescContext)_localctx).STRING.getText()); 
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(53); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
			_ctx.stop = _input.LT(-1);

			            ((DescContext)_localctx).description =  sb.toString();
			        
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
	public static class MonsterBaseContext extends ParserRuleContext {
		public MonsterBaseParseRecord base;
		public NameContext name;
		public GlyphContext glyph;
		public PainContext pain;
		public FlagsContext flags;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<GlyphContext> glyph() {
			return getRuleContexts(GlyphContext.class);
		}
		public GlyphContext glyph(int i) {
			return getRuleContext(GlyphContext.class,i);
		}
		public List<PainContext> pain() {
			return getRuleContexts(PainContext.class);
		}
		public PainContext pain(int i) {
			return getRuleContext(PainContext.class,i);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public MonsterBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_monsterBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).enterMonsterBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).exitMonsterBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterBaseGrammarVisitor ) return ((MonsterBaseGrammarVisitor<? extends T>)visitor).visitMonsterBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MonsterBaseContext monsterBase() throws RecognitionException {
		MonsterBaseContext _localctx = new MonsterBaseContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_monsterBase);

		            String nameInit = "";
		            String glyphInit = "";
		            String monPainInit = "";
		            List<String> flagsInit = new ArrayList<>();
		            int lineNo = 0;
		            StringBuilder descInit = new StringBuilder(); 
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			((MonsterBaseContext)_localctx).name = name();
			 nameInit = ((MonsterBaseContext)_localctx).name.nameStr;
			                   lineNo = ((MonsterBaseContext)_localctx).name.lineNo; 
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 60L) != 0)) {
				{
				setState(69);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case GLYPH:
					{
					setState(57);
					((MonsterBaseContext)_localctx).glyph = glyph();
					 glyphInit = ((MonsterBaseContext)_localctx).glyph.glyphChar; 
					}
					break;
				case PAIN:
					{
					setState(60);
					((MonsterBaseContext)_localctx).pain = pain();
					 monPainInit = ((MonsterBaseContext)_localctx).pain.monPain; 
					}
					break;
				case FLAGS:
					{
					setState(63);
					((MonsterBaseContext)_localctx).flags = flags();
					 flagsInit.addAll(((MonsterBaseContext)_localctx).flags.raceFlags); 
					}
					break;
				case DESC:
					{
					setState(66);
					((MonsterBaseContext)_localctx).desc = desc();
					 descInit.append(((MonsterBaseContext)_localctx).desc.description); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            String descFinal = descInit.toString();
			            if (descFinal.isEmpty())
			                descFinal = null;
			            ((MonsterBaseContext)_localctx).base =  new MonsterBaseParseRecord(nameInit, descFinal, flagsInit,
			                                    glyphInit, monPainInit, descFinal,
			                                    lineNo);
			        
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
	public static class FileContext extends ParserRuleContext {
		public List<MonsterBaseParseRecord> bases;
		public MonsterBaseContext monsterBase;
		public TerminalNode EOF() { return getToken(MonsterBaseGrammar.EOF, 0); }
		public List<MonsterBaseContext> monsterBase() {
			return getRuleContexts(MonsterBaseContext.class);
		}
		public MonsterBaseContext monsterBase(int i) {
			return getRuleContext(MonsterBaseContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterBaseGrammarListener ) ((MonsterBaseGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterBaseGrammarVisitor ) return ((MonsterBaseGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_file);

		            ((FileContext)_localctx).bases =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(74);
				((FileContext)_localctx).monsterBase = monsterBase();
				 _localctx.bases.add(((FileContext)_localctx).monsterBase.base); 
				}
				}
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(82);
			match(EOF);
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
		"\u0004\u0001\u000eU\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0004"+
		"\u0003\u001d\b\u0003\u000b\u0003\f\u0003\u001e\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003&\b\u0003\u0005\u0003"+
		"(\b\u0003\n\u0003\f\u0003+\t\u0003\u0003\u0003-\b\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0004\u00044\b\u0004\u000b"+
		"\u0004\f\u00045\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005F\b\u0005\n\u0005"+
		"\f\u0005I\t\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006N\b\u0006"+
		"\n\u0006\f\u0006Q\t\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0000\u0000"+
		"\u0007\u0000\u0002\u0004\u0006\b\n\f\u0000\u0001\u0001\u0001\u000e\u000e"+
		"X\u0000\u000e\u0001\u0000\u0000\u0000\u0002\u0012\u0001\u0000\u0000\u0000"+
		"\u0004\u0016\u0001\u0000\u0000\u0000\u0006\u001a\u0001\u0000\u0000\u0000"+
		"\b3\u0001\u0000\u0000\u0000\n7\u0001\u0000\u0000\u0000\fO\u0001\u0000"+
		"\u0000\u0000\u000e\u000f\u0005\u0001\u0000\u0000\u000f\u0010\u0005\u000b"+
		"\u0000\u0000\u0010\u0011\u0006\u0000\uffff\uffff\u0000\u0011\u0001\u0001"+
		"\u0000\u0000\u0000\u0012\u0013\u0005\u0002\u0000\u0000\u0013\u0014\u0005"+
		"\n\u0000\u0000\u0014\u0015\u0006\u0001\uffff\uffff\u0000\u0015\u0003\u0001"+
		"\u0000\u0000\u0000\u0016\u0017\u0005\u0003\u0000\u0000\u0017\u0018\u0005"+
		"\u0006\u0000\u0000\u0018\u0019\u0006\u0002\uffff\uffff\u0000\u0019\u0005"+
		"\u0001\u0000\u0000\u0000\u001a,\u0005\u0004\u0000\u0000\u001b\u001d\u0005"+
		"\r\u0000\u0000\u001c\u001b\u0001\u0000\u0000\u0000\u001d\u001e\u0001\u0000"+
		"\u0000\u0000\u001e\u001c\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000"+
		"\u0000\u0000\u001f-\u0001\u0000\u0000\u0000 !\u0005\f\u0000\u0000!)\u0006"+
		"\u0003\uffff\uffff\u0000\"%\u0005\r\u0000\u0000#$\u0005\f\u0000\u0000"+
		"$&\u0006\u0003\uffff\uffff\u0000%#\u0001\u0000\u0000\u0000%&\u0001\u0000"+
		"\u0000\u0000&(\u0001\u0000\u0000\u0000\'\"\u0001\u0000\u0000\u0000(+\u0001"+
		"\u0000\u0000\u0000)\'\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000\u0000"+
		"*-\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000,\u001c\u0001\u0000"+
		"\u0000\u0000, \u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-.\u0001"+
		"\u0000\u0000\u0000./\u0007\u0000\u0000\u0000/\u0007\u0001\u0000\u0000"+
		"\u000001\u0005\u0005\u0000\u000012\u0005\u000b\u0000\u000024\u0006\u0004"+
		"\uffff\uffff\u000030\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u0000"+
		"53\u0001\u0000\u0000\u000056\u0001\u0000\u0000\u00006\t\u0001\u0000\u0000"+
		"\u000078\u0003\u0000\u0000\u00008G\u0006\u0005\uffff\uffff\u00009:\u0003"+
		"\u0002\u0001\u0000:;\u0006\u0005\uffff\uffff\u0000;F\u0001\u0000\u0000"+
		"\u0000<=\u0003\u0004\u0002\u0000=>\u0006\u0005\uffff\uffff\u0000>F\u0001"+
		"\u0000\u0000\u0000?@\u0003\u0006\u0003\u0000@A\u0006\u0005\uffff\uffff"+
		"\u0000AF\u0001\u0000\u0000\u0000BC\u0003\b\u0004\u0000CD\u0006\u0005\uffff"+
		"\uffff\u0000DF\u0001\u0000\u0000\u0000E9\u0001\u0000\u0000\u0000E<\u0001"+
		"\u0000\u0000\u0000E?\u0001\u0000\u0000\u0000EB\u0001\u0000\u0000\u0000"+
		"FI\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000"+
		"\u0000H\u000b\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000JK\u0003"+
		"\n\u0005\u0000KL\u0006\u0006\uffff\uffff\u0000LN\u0001\u0000\u0000\u0000"+
		"MJ\u0001\u0000\u0000\u0000NQ\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000"+
		"\u0000OP\u0001\u0000\u0000\u0000PR\u0001\u0000\u0000\u0000QO\u0001\u0000"+
		"\u0000\u0000RS\u0005\u0000\u0000\u0001S\r\u0001\u0000\u0000\u0000\b\u001e"+
		"%),5EGO";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}