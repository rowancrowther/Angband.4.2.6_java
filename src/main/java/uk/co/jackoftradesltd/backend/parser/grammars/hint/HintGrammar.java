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

// Generated from HintGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.hint;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.hint.HintParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class HintGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		HINT=1, INTEGER=2, COMMENT=3, INDENT=4, EOL=5, STRING=6;
	public static final int
		RULE_hint = 0, RULE_hintRecord = 1, RULE_file = 2;
	private static String[] makeRuleNames() {
		return new String[] {
			"hint", "hintRecord", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'H:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "HINT", "INTEGER", "COMMENT", "INDENT", "EOL", "STRING"
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
	public String getGrammarFileName() { return "HintGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public HintGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HintContext extends ParserRuleContext {
		public String hintLine;
		public int line;
		public Token STRING;
		public TerminalNode HINT() { return getToken(HintGrammar.HINT, 0); }
		public TerminalNode STRING() { return getToken(HintGrammar.STRING, 0); }
		public HintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof HintGrammarListener ) ((HintGrammarListener)listener).enterHint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof HintGrammarListener ) ((HintGrammarListener)listener).exitHint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof HintGrammarVisitor ) return ((HintGrammarVisitor<? extends T>)visitor).visitHint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HintContext hint() throws RecognitionException {
		HintContext _localctx = new HintContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_hint);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(6);
			match(HINT);
			setState(7);
			((HintContext)_localctx).STRING = match(STRING);
			 ((HintContext)_localctx).hintLine =  ((HintContext)_localctx).STRING.getText();
			                          ((HintContext)_localctx).line =  _localctx.start.getLine(); 
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
	public static class HintRecordContext extends ParserRuleContext {
		public HintParseRecord record;
		public HintContext hint;
		public HintContext hint() {
			return getRuleContext(HintContext.class,0);
		}
		public HintRecordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hintRecord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof HintGrammarListener ) ((HintGrammarListener)listener).enterHintRecord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof HintGrammarListener ) ((HintGrammarListener)listener).exitHintRecord(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof HintGrammarVisitor ) return ((HintGrammarVisitor<? extends T>)visitor).visitHintRecord(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HintRecordContext hintRecord() throws RecognitionException {
		HintRecordContext _localctx = new HintRecordContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_hintRecord);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10);
			((HintRecordContext)_localctx).hint = hint();
			 ((HintRecordContext)_localctx).record =  new HintParseRecord(((HintRecordContext)_localctx).hint.hintLine,
			                ((HintRecordContext)_localctx).hint.line); 
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
	public static class FileContext extends ParserRuleContext {
		public List<HintParseRecord> hints;
		public HintRecordContext hintRecord;
		public TerminalNode EOF() { return getToken(HintGrammar.EOF, 0); }
		public List<HintRecordContext> hintRecord() {
			return getRuleContexts(HintRecordContext.class);
		}
		public HintRecordContext hintRecord(int i) {
			return getRuleContext(HintRecordContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof HintGrammarListener ) ((HintGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof HintGrammarListener ) ((HintGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof HintGrammarVisitor ) return ((HintGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_file);
		 ((FileContext)_localctx).hints =  new ArrayList<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==HINT) {
				{
				{
				setState(13);
				((FileContext)_localctx).hintRecord = hintRecord();
				 _localctx.hints.add(((FileContext)_localctx).hintRecord.record); 
				}
				}
				setState(20);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(21);
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
		"\u0004\u0001\u0006\u0018\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0005\u0002\u0011\b\u0002\n\u0002\f\u0002\u0014\t\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0000\u0000\u0003\u0000\u0002\u0004\u0000\u0000\u0015"+
		"\u0000\u0006\u0001\u0000\u0000\u0000\u0002\n\u0001\u0000\u0000\u0000\u0004"+
		"\u0012\u0001\u0000\u0000\u0000\u0006\u0007\u0005\u0001\u0000\u0000\u0007"+
		"\b\u0005\u0006\u0000\u0000\b\t\u0006\u0000\uffff\uffff\u0000\t\u0001\u0001"+
		"\u0000\u0000\u0000\n\u000b\u0003\u0000\u0000\u0000\u000b\f\u0006\u0001"+
		"\uffff\uffff\u0000\f\u0003\u0001\u0000\u0000\u0000\r\u000e\u0003\u0002"+
		"\u0001\u0000\u000e\u000f\u0006\u0002\uffff\uffff\u0000\u000f\u0011\u0001"+
		"\u0000\u0000\u0000\u0010\r\u0001\u0000\u0000\u0000\u0011\u0014\u0001\u0000"+
		"\u0000\u0000\u0012\u0010\u0001\u0000\u0000\u0000\u0012\u0013\u0001\u0000"+
		"\u0000\u0000\u0013\u0015\u0001\u0000\u0000\u0000\u0014\u0012\u0001\u0000"+
		"\u0000\u0000\u0015\u0016\u0005\u0000\u0000\u0001\u0016\u0005\u0001\u0000"+
		"\u0000\u0000\u0001\u0012";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}