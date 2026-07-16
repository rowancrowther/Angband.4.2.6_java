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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/PainGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.pain.PainParseRecord;

    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PainGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RECORD_COUNT=1, TYPE=2, MESSAGE=3, INTEGER=4, COMMENT=5, EOL=6, STRING=7;
	public static final int
		RULE_recordCount = 0, RULE_type = 1, RULE_message = 2, RULE_painEntry = 3, 
		RULE_file = 4;
	private static String[] makeRuleNames() {
		return new String[] {
			"recordCount", "type", "message", "painEntry", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'record-count:'", "'type:'", "'message:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "RECORD_COUNT", "TYPE", "MESSAGE", "INTEGER", "COMMENT", "EOL", 
			"STRING"
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
	public String getGrammarFileName() { return "PainGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PainGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecordCountContext extends ParserRuleContext {
		public String count;
		public Token INTEGER;
		public TerminalNode RECORD_COUNT() { return getToken(PainGrammar.RECORD_COUNT, 0); }
		public TerminalNode INTEGER() { return getToken(PainGrammar.INTEGER, 0); }
		public RecordCountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordCount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).enterRecordCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).exitRecordCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainGrammarVisitor ) return ((PainGrammarVisitor<? extends T>)visitor).visitRecordCount(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecordCountContext recordCount() throws RecognitionException {
		RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_recordCount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10);
			match(RECORD_COUNT);
			setState(11);
			((RecordCountContext)_localctx).INTEGER = match(INTEGER);
			 ((RecordCountContext)_localctx).count =  ((RecordCountContext)_localctx).INTEGER.getText(); 
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
	public static class TypeContext extends ParserRuleContext {
		public String typeNum;
		public int line;
		public Token INTEGER;
		public TerminalNode TYPE() { return getToken(PainGrammar.TYPE, 0); }
		public TerminalNode INTEGER() { return getToken(PainGrammar.INTEGER, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainGrammarVisitor ) return ((PainGrammarVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(14);
			match(TYPE);
			setState(15);
			((TypeContext)_localctx).INTEGER = match(INTEGER);

			                ((TypeContext)_localctx).line =  _localctx.start.getLine();
			                ((TypeContext)_localctx).typeNum =  ((TypeContext)_localctx).INTEGER.getText();
			            
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
	public static class MessageContext extends ParserRuleContext {
		public String msgStr;
		public Token STRING;
		public TerminalNode MESSAGE() { return getToken(PainGrammar.MESSAGE, 0); }
		public TerminalNode STRING() { return getToken(PainGrammar.STRING, 0); }
		public MessageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).enterMessage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).exitMessage(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainGrammarVisitor ) return ((PainGrammarVisitor<? extends T>)visitor).visitMessage(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageContext message() throws RecognitionException {
		MessageContext _localctx = new MessageContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			match(MESSAGE);
			setState(19);
			((MessageContext)_localctx).STRING = match(STRING);

			                ((MessageContext)_localctx).msgStr =  ((MessageContext)_localctx).STRING.getText();
			            
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
	public static class PainEntryContext extends ParserRuleContext {
		public PainParseRecord record;
		public TypeContext type;
		public MessageContext msg;
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<MessageContext> message() {
			return getRuleContexts(MessageContext.class);
		}
		public MessageContext message(int i) {
			return getRuleContext(MessageContext.class,i);
		}
		public PainEntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_painEntry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).enterPainEntry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).exitPainEntry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainGrammarVisitor ) return ((PainGrammarVisitor<? extends T>)visitor).visitPainEntry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PainEntryContext painEntry() throws RecognitionException {
		PainEntryContext _localctx = new PainEntryContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_painEntry);

		            List<String> monPain = new ArrayList<>();
		            int lineNo = 0;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			((PainEntryContext)_localctx).type = type();
			 monPain.add(((PainEntryContext)_localctx).type.typeNum);
			                   lineNo = ((PainEntryContext)_localctx).type.line; 
			setState(24);
			((PainEntryContext)_localctx).msg = message();
			 monPain.add(((PainEntryContext)_localctx).msg.msgStr); 
			setState(26);
			((PainEntryContext)_localctx).msg = message();
			 monPain.add(((PainEntryContext)_localctx).msg.msgStr); 
			setState(28);
			((PainEntryContext)_localctx).msg = message();
			 monPain.add(((PainEntryContext)_localctx).msg.msgStr); 
			setState(30);
			((PainEntryContext)_localctx).msg = message();
			 monPain.add(((PainEntryContext)_localctx).msg.msgStr); 
			setState(32);
			((PainEntryContext)_localctx).msg = message();
			 monPain.add(((PainEntryContext)_localctx).msg.msgStr); 
			setState(34);
			((PainEntryContext)_localctx).msg = message();
			 monPain.add(((PainEntryContext)_localctx).msg.msgStr); 
			setState(36);
			((PainEntryContext)_localctx).msg = message();
			 monPain.add(((PainEntryContext)_localctx).msg.msgStr); 
			}
			_ctx.stop = _input.LT(-1);

			            ((PainEntryContext)_localctx).record =  new PainParseRecord(monPain, lineNo);
			        
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
		public List<PainParseRecord> monsterPain;
		public String declaredRecords;
		public RecordCountContext recordCount;
		public PainEntryContext painEntry;
		public RecordCountContext recordCount() {
			return getRuleContext(RecordCountContext.class,0);
		}
		public TerminalNode EOF() { return getToken(PainGrammar.EOF, 0); }
		public List<PainEntryContext> painEntry() {
			return getRuleContexts(PainEntryContext.class);
		}
		public PainEntryContext painEntry(int i) {
			return getRuleContext(PainEntryContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PainGrammarListener ) ((PainGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PainGrammarVisitor ) return ((PainGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_file);

		            ((FileContext)_localctx).monsterPain =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			((FileContext)_localctx).recordCount = recordCount();
			 ((FileContext)_localctx).declaredRecords =  ((FileContext)_localctx).recordCount.count; 
			setState(44); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(41);
				((FileContext)_localctx).painEntry = painEntry();

				            _localctx.monsterPain.add(((FileContext)_localctx).painEntry.record);
				        
				}
				}
				setState(46); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TYPE );
			setState(48);
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
		"\u0004\u0001\u00073\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0004\u0004-\b\u0004\u000b"+
		"\u0004\f\u0004.\u0001\u0004\u0001\u0004\u0001\u0004\u0000\u0000\u0005"+
		"\u0000\u0002\u0004\u0006\b\u0000\u0000.\u0000\n\u0001\u0000\u0000\u0000"+
		"\u0002\u000e\u0001\u0000\u0000\u0000\u0004\u0012\u0001\u0000\u0000\u0000"+
		"\u0006\u0016\u0001\u0000\u0000\u0000\b\'\u0001\u0000\u0000\u0000\n\u000b"+
		"\u0005\u0001\u0000\u0000\u000b\f\u0005\u0004\u0000\u0000\f\r\u0006\u0000"+
		"\uffff\uffff\u0000\r\u0001\u0001\u0000\u0000\u0000\u000e\u000f\u0005\u0002"+
		"\u0000\u0000\u000f\u0010\u0005\u0004\u0000\u0000\u0010\u0011\u0006\u0001"+
		"\uffff\uffff\u0000\u0011\u0003\u0001\u0000\u0000\u0000\u0012\u0013\u0005"+
		"\u0003\u0000\u0000\u0013\u0014\u0005\u0007\u0000\u0000\u0014\u0015\u0006"+
		"\u0002\uffff\uffff\u0000\u0015\u0005\u0001\u0000\u0000\u0000\u0016\u0017"+
		"\u0003\u0002\u0001\u0000\u0017\u0018\u0006\u0003\uffff\uffff\u0000\u0018"+
		"\u0019\u0003\u0004\u0002\u0000\u0019\u001a\u0006\u0003\uffff\uffff\u0000"+
		"\u001a\u001b\u0003\u0004\u0002\u0000\u001b\u001c\u0006\u0003\uffff\uffff"+
		"\u0000\u001c\u001d\u0003\u0004\u0002\u0000\u001d\u001e\u0006\u0003\uffff"+
		"\uffff\u0000\u001e\u001f\u0003\u0004\u0002\u0000\u001f \u0006\u0003\uffff"+
		"\uffff\u0000 !\u0003\u0004\u0002\u0000!\"\u0006\u0003\uffff\uffff\u0000"+
		"\"#\u0003\u0004\u0002\u0000#$\u0006\u0003\uffff\uffff\u0000$%\u0003\u0004"+
		"\u0002\u0000%&\u0006\u0003\uffff\uffff\u0000&\u0007\u0001\u0000\u0000"+
		"\u0000\'(\u0003\u0000\u0000\u0000(,\u0006\u0004\uffff\uffff\u0000)*\u0003"+
		"\u0006\u0003\u0000*+\u0006\u0004\uffff\uffff\u0000+-\u0001\u0000\u0000"+
		"\u0000,)\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000.,\u0001\u0000"+
		"\u0000\u0000./\u0001\u0000\u0000\u0000/0\u0001\u0000\u0000\u000001\u0005"+
		"\u0000\u0000\u00011\t\u0001\u0000\u0000\u0000\u0001.";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}