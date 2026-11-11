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
		NAME=1, GLYPH=2, PAIN=3, FLAGS=4, DESC=5, INTEGER=6, COMMENT=7, INDENT=8, 
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
			null, "NAME", "GLYPH", "PAIN", "FLAGS", "DESC", "INTEGER", "COMMENT", 
			"INDENT", "EOL", "SINGLE_CHAR", "STRING", "FLAG", "FLAG_OR", "FLAG_EOL"
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
		public Token INTEGER;
		public TerminalNode PAIN() { return getToken(MonsterBaseGrammar.PAIN, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterBaseGrammar.INTEGER, 0); }
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
			((PainContext)_localctx).INTEGER = match(INTEGER);

			            ((PainContext)_localctx).monPain =  ((PainContext)_localctx).INTEGER.getText();
			        
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
			setState(27);
			((FlagsContext)_localctx).flag1 = match(FLAG);

			                _localctx.raceFlags.add(((FlagsContext)_localctx).flag1.getText());
			            
			setState(34);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(29);
				match(FLAG_OR);
				setState(30);
				((FlagsContext)_localctx).flag2 = match(FLAG);

				                _localctx.raceFlags.add(((FlagsContext)_localctx).flag2.getText());
				            
				}
				}
				setState(36);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(37);
			match(FLAG_EOL);
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
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(39);
				match(DESC);
				setState(40);
				((DescContext)_localctx).STRING = match(STRING);
				 sb.append(((DescContext)_localctx).STRING.getText()); 
				}
				}
				setState(44); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DESC );
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
		public GlyphContext glyph() {
			return getRuleContext(GlyphContext.class,0);
		}
		public PainContext pain() {
			return getRuleContext(PainContext.class,0);
		}
		public DescContext desc() {
			return getRuleContext(DescContext.class,0);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class,i);
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
		            String descInit = "";
		            int lineNo = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			((MonsterBaseContext)_localctx).name = name();
			 nameInit = ((MonsterBaseContext)_localctx).name.nameStr;
			                   lineNo = ((MonsterBaseContext)_localctx).name.lineNo; 
			setState(48);
			((MonsterBaseContext)_localctx).glyph = glyph();
			 glyphInit = ((MonsterBaseContext)_localctx).glyph.glyphChar; 
			setState(50);
			((MonsterBaseContext)_localctx).pain = pain();
			 monPainInit = ((MonsterBaseContext)_localctx).pain.monPain; 
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAGS) {
				{
				{
				setState(52);
				((MonsterBaseContext)_localctx).flags = flags();

				                flagsInit.addAll(((MonsterBaseContext)_localctx).flags.raceFlags);
				            
				}
				}
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(60);
			((MonsterBaseContext)_localctx).desc = desc();
			 descInit = ((MonsterBaseContext)_localctx).desc.description; 
			}
			_ctx.stop = _input.LT(-1);

			            ((MonsterBaseContext)_localctx).base =  new MonsterBaseParseRecord(nameInit, descInit, flagsInit,
			                                    glyphInit, monPainInit, descInit, lineNo);
			        
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
			setState(66); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(63);
				((FileContext)_localctx).monsterBase = monsterBase();
				 _localctx.bases.add(((FileContext)_localctx).monsterBase.base); 
				}
				}
				setState(68); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(70);
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
		"\u0004\u0001\u000eI\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003!\b\u0003\n\u0003"+
		"\f\u0003$\t\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0004\u0004+\b\u0004\u000b\u0004\f\u0004,\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0005\u00058\b\u0005\n\u0005\f\u0005;\t\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0004\u0006"+
		"C\b\u0006\u000b\u0006\f\u0006D\u0001\u0006\u0001\u0006\u0001\u0006\u0000"+
		"\u0000\u0007\u0000\u0002\u0004\u0006\b\n\f\u0000\u0000E\u0000\u000e\u0001"+
		"\u0000\u0000\u0000\u0002\u0012\u0001\u0000\u0000\u0000\u0004\u0016\u0001"+
		"\u0000\u0000\u0000\u0006\u001a\u0001\u0000\u0000\u0000\b*\u0001\u0000"+
		"\u0000\u0000\n.\u0001\u0000\u0000\u0000\fB\u0001\u0000\u0000\u0000\u000e"+
		"\u000f\u0005\u0001\u0000\u0000\u000f\u0010\u0005\u000b\u0000\u0000\u0010"+
		"\u0011\u0006\u0000\uffff\uffff\u0000\u0011\u0001\u0001\u0000\u0000\u0000"+
		"\u0012\u0013\u0005\u0002\u0000\u0000\u0013\u0014\u0005\n\u0000\u0000\u0014"+
		"\u0015\u0006\u0001\uffff\uffff\u0000\u0015\u0003\u0001\u0000\u0000\u0000"+
		"\u0016\u0017\u0005\u0003\u0000\u0000\u0017\u0018\u0005\u0006\u0000\u0000"+
		"\u0018\u0019\u0006\u0002\uffff\uffff\u0000\u0019\u0005\u0001\u0000\u0000"+
		"\u0000\u001a\u001b\u0005\u0004\u0000\u0000\u001b\u001c\u0005\f\u0000\u0000"+
		"\u001c\"\u0006\u0003\uffff\uffff\u0000\u001d\u001e\u0005\r\u0000\u0000"+
		"\u001e\u001f\u0005\f\u0000\u0000\u001f!\u0006\u0003\uffff\uffff\u0000"+
		" \u001d\u0001\u0000\u0000\u0000!$\u0001\u0000\u0000\u0000\" \u0001\u0000"+
		"\u0000\u0000\"#\u0001\u0000\u0000\u0000#%\u0001\u0000\u0000\u0000$\"\u0001"+
		"\u0000\u0000\u0000%&\u0005\u000e\u0000\u0000&\u0007\u0001\u0000\u0000"+
		"\u0000\'(\u0005\u0005\u0000\u0000()\u0005\u000b\u0000\u0000)+\u0006\u0004"+
		"\uffff\uffff\u0000*\'\u0001\u0000\u0000\u0000+,\u0001\u0000\u0000\u0000"+
		",*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-\t\u0001\u0000\u0000"+
		"\u0000./\u0003\u0000\u0000\u0000/0\u0006\u0005\uffff\uffff\u000001\u0003"+
		"\u0002\u0001\u000012\u0006\u0005\uffff\uffff\u000023\u0003\u0004\u0002"+
		"\u000039\u0006\u0005\uffff\uffff\u000045\u0003\u0006\u0003\u000056\u0006"+
		"\u0005\uffff\uffff\u000068\u0001\u0000\u0000\u000074\u0001\u0000\u0000"+
		"\u00008;\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u00009:\u0001\u0000"+
		"\u0000\u0000:<\u0001\u0000\u0000\u0000;9\u0001\u0000\u0000\u0000<=\u0003"+
		"\b\u0004\u0000=>\u0006\u0005\uffff\uffff\u0000>\u000b\u0001\u0000\u0000"+
		"\u0000?@\u0003\n\u0005\u0000@A\u0006\u0006\uffff\uffff\u0000AC\u0001\u0000"+
		"\u0000\u0000B?\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000DB\u0001"+
		"\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000"+
		"FG\u0005\u0000\u0000\u0001G\r\u0001\u0000\u0000\u0000\u0004\",9D";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}