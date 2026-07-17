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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/op_probe/ObjectPropertyGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.objectproperty.ObjectPropertyParseRecord;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectPropertyGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RECORD_COUNT=1, NAME=2, TYPE=3, SUBTYPE=4, ID_TYPE=5, CODE=6, POWER=7, 
		MULT=8, TYPE_MULT=9, ADJECTIVE=10, NEG_ADJECTIVE=11, MSG=12, BINDUI=13, 
		DESC=14, COLON=15, INTEGER=16, LTHAN=17, GTHAN=18, LCASE=19, UCASE=20, 
		COMMENT=21, EOL=22, STRING=23, EOL_POP=24, DELIM_INTEGER=25, STRING_BETWEEN_COLONS=26, 
		DELIM_COLON=27, END_SKIP=28;
	public static final int
		RULE_recordCount = 0, RULE_name = 1, RULE_type = 2, RULE_subtype = 3, 
		RULE_idType = 4, RULE_codeVal = 5, RULE_power = 6, RULE_mult = 7, RULE_typeMult = 8, 
		RULE_adjective = 9, RULE_negAdjective = 10, RULE_msg = 11, RULE_bindui = 12, 
		RULE_desc = 13, RULE_objectProperty = 14, RULE_file = 15;
	private static String[] makeRuleNames() {
		return new String[] {
			"recordCount", "name", "type", "subtype", "idType", "codeVal", "power", 
			"mult", "typeMult", "adjective", "negAdjective", "msg", "bindui", "desc", 
			"objectProperty", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'record-count:'", "'name:'", "'type:'", "'subtype:'", "'id-type:'", 
			"'code:'", "'power:'", "'mult:'", "'type-mult:'", "'adjective:'", "'neg-adjective:'", 
			"'msg:'", "'bindui:'", "'desc:'", null, null, "'<'", "'>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "RECORD_COUNT", "NAME", "TYPE", "SUBTYPE", "ID_TYPE", "CODE", "POWER", 
			"MULT", "TYPE_MULT", "ADJECTIVE", "NEG_ADJECTIVE", "MSG", "BINDUI", "DESC", 
			"COLON", "INTEGER", "LTHAN", "GTHAN", "LCASE", "UCASE", "COMMENT", "EOL", 
			"STRING", "EOL_POP", "DELIM_INTEGER", "STRING_BETWEEN_COLONS", "DELIM_COLON", 
			"END_SKIP"
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
	public String getGrammarFileName() { return "ObjectPropertyGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ObjectPropertyGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecordCountContext extends ParserRuleContext {
		public String count;
		public Token INTEGER;
		public TerminalNode RECORD_COUNT() { return getToken(ObjectPropertyGrammar.RECORD_COUNT, 0); }
		public TerminalNode INTEGER() { return getToken(ObjectPropertyGrammar.INTEGER, 0); }
		public RecordCountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordCount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterRecordCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitRecordCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitRecordCount(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecordCountContext recordCount() throws RecognitionException {
		RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_recordCount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(RECORD_COUNT);
			setState(33);
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
	public static class NameContext extends ParserRuleContext {
		public String nameString;
		public int lineNo;
		public Token STRING;
		public TerminalNode NAME() { return getToken(ObjectPropertyGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(ObjectPropertyGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(NAME);
			setState(37);
			((NameContext)_localctx).STRING = match(STRING);
			 ((NameContext)_localctx).nameString =  ((NameContext)_localctx).STRING.getText(); ((NameContext)_localctx).lineNo =  _localctx.start.getLine(); 
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
		public String typeStr;
		public Token LCASE;
		public TerminalNode TYPE() { return getToken(ObjectPropertyGrammar.TYPE, 0); }
		public TerminalNode LCASE() { return getToken(ObjectPropertyGrammar.LCASE, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(TYPE);
			setState(41);
			((TypeContext)_localctx).LCASE = match(LCASE);
			 ((TypeContext)_localctx).typeStr =  ((TypeContext)_localctx).LCASE.getText(); 
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
	public static class SubtypeContext extends ParserRuleContext {
		public String subTypeStr;
		public Token STRING;
		public TerminalNode SUBTYPE() { return getToken(ObjectPropertyGrammar.SUBTYPE, 0); }
		public TerminalNode STRING() { return getToken(ObjectPropertyGrammar.STRING, 0); }
		public SubtypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterSubtype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitSubtype(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitSubtype(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SubtypeContext subtype() throws RecognitionException {
		SubtypeContext _localctx = new SubtypeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_subtype);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(SUBTYPE);
			setState(45);
			((SubtypeContext)_localctx).STRING = match(STRING);
			 ((SubtypeContext)_localctx).subTypeStr =  ((SubtypeContext)_localctx).STRING.getText();
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
	public static class IdTypeContext extends ParserRuleContext {
		public String idTypeStr;
		public Token STRING;
		public TerminalNode ID_TYPE() { return getToken(ObjectPropertyGrammar.ID_TYPE, 0); }
		public TerminalNode STRING() { return getToken(ObjectPropertyGrammar.STRING, 0); }
		public IdTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterIdType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitIdType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitIdType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdTypeContext idType() throws RecognitionException {
		IdTypeContext _localctx = new IdTypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_idType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(ID_TYPE);
			setState(49);
			((IdTypeContext)_localctx).STRING = match(STRING);
			 ((IdTypeContext)_localctx).idTypeStr =  ((IdTypeContext)_localctx).STRING.getText(); 
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
	public static class CodeValContext extends ParserRuleContext {
		public String codeStr;
		public Token UCASE;
		public TerminalNode CODE() { return getToken(ObjectPropertyGrammar.CODE, 0); }
		public TerminalNode UCASE() { return getToken(ObjectPropertyGrammar.UCASE, 0); }
		public CodeValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_codeVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterCodeVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitCodeVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitCodeVal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeValContext codeVal() throws RecognitionException {
		CodeValContext _localctx = new CodeValContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_codeVal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(CODE);
			setState(53);
			((CodeValContext)_localctx).UCASE = match(UCASE);
			 ((CodeValContext)_localctx).codeStr =  ((CodeValContext)_localctx).UCASE.getText(); 
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
	public static class PowerContext extends ParserRuleContext {
		public String powerVal;
		public Token INTEGER;
		public TerminalNode POWER() { return getToken(ObjectPropertyGrammar.POWER, 0); }
		public TerminalNode INTEGER() { return getToken(ObjectPropertyGrammar.INTEGER, 0); }
		public PowerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_power; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitPower(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PowerContext power() throws RecognitionException {
		PowerContext _localctx = new PowerContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_power);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(POWER);
			setState(57);
			((PowerContext)_localctx).INTEGER = match(INTEGER);
			 ((PowerContext)_localctx).powerVal =  ((PowerContext)_localctx).INTEGER.getText(); 
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
		public String multVal;
		public Token INTEGER;
		public TerminalNode MULT() { return getToken(ObjectPropertyGrammar.MULT, 0); }
		public TerminalNode INTEGER() { return getToken(ObjectPropertyGrammar.INTEGER, 0); }
		public MultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mult; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitMult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitMult(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultContext mult() throws RecognitionException {
		MultContext _localctx = new MultContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_mult);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(MULT);
			setState(61);
			((MultContext)_localctx).INTEGER = match(INTEGER);
			 ((MultContext)_localctx).multVal =  ((MultContext)_localctx).INTEGER.getText(); 
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
	public static class TypeMultContext extends ParserRuleContext {
		public String typeTval;
		public String multVal;
		public Token t;
		public Token v;
		public TerminalNode TYPE_MULT() { return getToken(ObjectPropertyGrammar.TYPE_MULT, 0); }
		public TerminalNode DELIM_COLON() { return getToken(ObjectPropertyGrammar.DELIM_COLON, 0); }
		public TerminalNode STRING_BETWEEN_COLONS() { return getToken(ObjectPropertyGrammar.STRING_BETWEEN_COLONS, 0); }
		public TerminalNode DELIM_INTEGER() { return getToken(ObjectPropertyGrammar.DELIM_INTEGER, 0); }
		public TypeMultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeMult; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterTypeMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitTypeMult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitTypeMult(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeMultContext typeMult() throws RecognitionException {
		TypeMultContext _localctx = new TypeMultContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_typeMult);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(TYPE_MULT);
			setState(65);
			((TypeMultContext)_localctx).t = match(STRING_BETWEEN_COLONS);
			setState(66);
			match(DELIM_COLON);
			setState(67);
			((TypeMultContext)_localctx).v = match(DELIM_INTEGER);

			                ((TypeMultContext)_localctx).typeTval =  ((TypeMultContext)_localctx).t.getText();
			                ((TypeMultContext)_localctx).multVal =  ((TypeMultContext)_localctx).v.getText(); 
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
	public static class AdjectiveContext extends ParserRuleContext {
		public String adj;
		public Token LCASE;
		public TerminalNode ADJECTIVE() { return getToken(ObjectPropertyGrammar.ADJECTIVE, 0); }
		public TerminalNode LCASE() { return getToken(ObjectPropertyGrammar.LCASE, 0); }
		public AdjectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_adjective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterAdjective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitAdjective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitAdjective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdjectiveContext adjective() throws RecognitionException {
		AdjectiveContext _localctx = new AdjectiveContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_adjective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(ADJECTIVE);
			setState(71);
			((AdjectiveContext)_localctx).LCASE = match(LCASE);
			 ((AdjectiveContext)_localctx).adj =  ((AdjectiveContext)_localctx).LCASE.getText(); 
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
	public static class NegAdjectiveContext extends ParserRuleContext {
		public String negAdj;
		public Token LCASE;
		public TerminalNode NEG_ADJECTIVE() { return getToken(ObjectPropertyGrammar.NEG_ADJECTIVE, 0); }
		public TerminalNode LCASE() { return getToken(ObjectPropertyGrammar.LCASE, 0); }
		public NegAdjectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negAdjective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterNegAdjective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitNegAdjective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitNegAdjective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NegAdjectiveContext negAdjective() throws RecognitionException {
		NegAdjectiveContext _localctx = new NegAdjectiveContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_negAdjective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(NEG_ADJECTIVE);
			setState(75);
			((NegAdjectiveContext)_localctx).LCASE = match(LCASE);
			 ((NegAdjectiveContext)_localctx).negAdj =  ((NegAdjectiveContext)_localctx).LCASE.getText(); 
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
	public static class MsgContext extends ParserRuleContext {
		public String msgLine;
		public Token STRING;
		public TerminalNode MSG() { return getToken(ObjectPropertyGrammar.MSG, 0); }
		public TerminalNode STRING() { return getToken(ObjectPropertyGrammar.STRING, 0); }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			match(MSG);
			setState(79);
			((MsgContext)_localctx).STRING = match(STRING);
			 ((MsgContext)_localctx).msgLine =  ((MsgContext)_localctx).STRING.getText(); 
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
	public static class BinduiContext extends ParserRuleContext {
		public String binding;
		public String tag;
		public String uiVal;
		public String aux;
		public Token b;
		public Token t;
		public Token a;
		public Token u;
		public TerminalNode BINDUI() { return getToken(ObjectPropertyGrammar.BINDUI, 0); }
		public List<TerminalNode> COLON() { return getTokens(ObjectPropertyGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(ObjectPropertyGrammar.COLON, i);
		}
		public TerminalNode LCASE() { return getToken(ObjectPropertyGrammar.LCASE, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(ObjectPropertyGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(ObjectPropertyGrammar.INTEGER, i);
		}
		public TerminalNode LTHAN() { return getToken(ObjectPropertyGrammar.LTHAN, 0); }
		public TerminalNode GTHAN() { return getToken(ObjectPropertyGrammar.GTHAN, 0); }
		public TerminalNode UCASE() { return getToken(ObjectPropertyGrammar.UCASE, 0); }
		public BinduiContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindui; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterBindui(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitBindui(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitBindui(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinduiContext bindui() throws RecognitionException {
		BinduiContext _localctx = new BinduiContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_bindui);

		            ((BinduiContext)_localctx).tag =  "";
		            ((BinduiContext)_localctx).uiVal =  null;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(BINDUI);
			setState(83);
			((BinduiContext)_localctx).b = match(LCASE);
			 ((BinduiContext)_localctx).binding =  ((BinduiContext)_localctx).b.getText(); 
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LTHAN) {
				{
				setState(85);
				match(LTHAN);
				setState(86);
				((BinduiContext)_localctx).t = match(UCASE);
				setState(87);
				match(GTHAN);
				 ((BinduiContext)_localctx).tag =  ((BinduiContext)_localctx).t.getText(); 
				}
			}

			setState(91);
			match(COLON);
			setState(92);
			((BinduiContext)_localctx).a = match(INTEGER);
			((BinduiContext)_localctx).aux =  ((BinduiContext)_localctx).a.getText(); 
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(94);
				match(COLON);
				setState(95);
				((BinduiContext)_localctx).u = match(INTEGER);
				((BinduiContext)_localctx).uiVal =  ((BinduiContext)_localctx).u.getText(); 
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
	public static class DescContext extends ParserRuleContext {
		public String line;
		public Token STRING;
		public TerminalNode DESC() { return getToken(ObjectPropertyGrammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(ObjectPropertyGrammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(DESC);
			setState(100);
			((DescContext)_localctx).STRING = match(STRING);
			 ((DescContext)_localctx).line =  ((DescContext)_localctx).STRING.getText(); 
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
	public static class ObjectPropertyContext extends ParserRuleContext {
		public ObjectPropertyParseRecord property;
		public NameContext name;
		public TypeContext type;
		public SubtypeContext subtype;
		public IdTypeContext idType;
		public CodeValContext codeVal;
		public PowerContext power;
		public MultContext mult;
		public TypeMultContext typeMult;
		public AdjectiveContext adjective;
		public NegAdjectiveContext negAdjective;
		public MsgContext msg;
		public BinduiContext bindui;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<SubtypeContext> subtype() {
			return getRuleContexts(SubtypeContext.class);
		}
		public SubtypeContext subtype(int i) {
			return getRuleContext(SubtypeContext.class,i);
		}
		public List<IdTypeContext> idType() {
			return getRuleContexts(IdTypeContext.class);
		}
		public IdTypeContext idType(int i) {
			return getRuleContext(IdTypeContext.class,i);
		}
		public List<CodeValContext> codeVal() {
			return getRuleContexts(CodeValContext.class);
		}
		public CodeValContext codeVal(int i) {
			return getRuleContext(CodeValContext.class,i);
		}
		public List<PowerContext> power() {
			return getRuleContexts(PowerContext.class);
		}
		public PowerContext power(int i) {
			return getRuleContext(PowerContext.class,i);
		}
		public List<MultContext> mult() {
			return getRuleContexts(MultContext.class);
		}
		public MultContext mult(int i) {
			return getRuleContext(MultContext.class,i);
		}
		public List<TypeMultContext> typeMult() {
			return getRuleContexts(TypeMultContext.class);
		}
		public TypeMultContext typeMult(int i) {
			return getRuleContext(TypeMultContext.class,i);
		}
		public List<AdjectiveContext> adjective() {
			return getRuleContexts(AdjectiveContext.class);
		}
		public AdjectiveContext adjective(int i) {
			return getRuleContext(AdjectiveContext.class,i);
		}
		public List<NegAdjectiveContext> negAdjective() {
			return getRuleContexts(NegAdjectiveContext.class);
		}
		public NegAdjectiveContext negAdjective(int i) {
			return getRuleContext(NegAdjectiveContext.class,i);
		}
		public List<MsgContext> msg() {
			return getRuleContexts(MsgContext.class);
		}
		public MsgContext msg(int i) {
			return getRuleContext(MsgContext.class,i);
		}
		public List<BinduiContext> bindui() {
			return getRuleContexts(BinduiContext.class);
		}
		public BinduiContext bindui(int i) {
			return getRuleContext(BinduiContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public ObjectPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objectProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterObjectProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitObjectProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitObjectProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectPropertyContext objectProperty() throws RecognitionException {
		ObjectPropertyContext _localctx = new ObjectPropertyContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_objectProperty);

		            String nameInit = "";
		            String typeInit = "";
		            String subTypeInit = "";
		            String idTypeInit = "";
		            String codeInit = "";
		            String powerInit = "";
		            String multInit = "";
		            Map<String, String> typeMultInit = new HashMap<>();
		            String adjectiveInit = "";
		            String negAdjectiveInit = "";
		            String msgInit = "";
		            String binduiInit = "";
		            String tagInit = "";
		            String valueInit = null;
		            String auxInit = "";
		            String descInit = "";
		            int line = 0;
		            StringBuilder msgSB = new StringBuilder();
		            StringBuilder descSB = new StringBuilder();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			((ObjectPropertyContext)_localctx).name = name();
			 line = ((ObjectPropertyContext)_localctx).name.lineNo; nameInit = ((ObjectPropertyContext)_localctx).name.nameString; 
			setState(105);
			((ObjectPropertyContext)_localctx).type = type();
			 typeInit = ((ObjectPropertyContext)_localctx).type.typeStr; 
			setState(140); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(140);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SUBTYPE:
					{
					setState(107);
					((ObjectPropertyContext)_localctx).subtype = subtype();
					 subTypeInit = ((ObjectPropertyContext)_localctx).subtype.subTypeStr; 
					}
					break;
				case ID_TYPE:
					{
					setState(110);
					((ObjectPropertyContext)_localctx).idType = idType();
					 idTypeInit = ((ObjectPropertyContext)_localctx).idType.idTypeStr; 
					}
					break;
				case CODE:
					{
					setState(113);
					((ObjectPropertyContext)_localctx).codeVal = codeVal();
					 codeInit = ((ObjectPropertyContext)_localctx).codeVal.codeStr; 
					}
					break;
				case POWER:
					{
					setState(116);
					((ObjectPropertyContext)_localctx).power = power();
					 powerInit = ((ObjectPropertyContext)_localctx).power.powerVal; 
					}
					break;
				case MULT:
					{
					setState(119);
					((ObjectPropertyContext)_localctx).mult = mult();
					 multInit = ((ObjectPropertyContext)_localctx).mult.multVal; 
					}
					break;
				case TYPE_MULT:
					{
					setState(122);
					((ObjectPropertyContext)_localctx).typeMult = typeMult();
					 typeMultInit.put(((ObjectPropertyContext)_localctx).typeMult.typeTval, ((ObjectPropertyContext)_localctx).typeMult.multVal); 
					}
					break;
				case ADJECTIVE:
					{
					setState(125);
					((ObjectPropertyContext)_localctx).adjective = adjective();
					 adjectiveInit = ((ObjectPropertyContext)_localctx).adjective.adj; 
					}
					break;
				case NEG_ADJECTIVE:
					{
					setState(128);
					((ObjectPropertyContext)_localctx).negAdjective = negAdjective();
					 negAdjectiveInit = ((ObjectPropertyContext)_localctx).negAdjective.negAdj; 
					}
					break;
				case MSG:
					{
					setState(131);
					((ObjectPropertyContext)_localctx).msg = msg();
					 msgSB.append(((ObjectPropertyContext)_localctx).msg.msgLine); 
					}
					break;
				case BINDUI:
					{
					setState(134);
					((ObjectPropertyContext)_localctx).bindui = bindui();
					 binduiInit = ((ObjectPropertyContext)_localctx).bindui.binding;
					                     tagInit = ((ObjectPropertyContext)_localctx).bindui.tag;
					                     valueInit = ((ObjectPropertyContext)_localctx).bindui.uiVal;
					                     auxInit = ((ObjectPropertyContext)_localctx).bindui.aux; 
					}
					break;
				case DESC:
					{
					setState(137);
					((ObjectPropertyContext)_localctx).desc = desc();
					 descSB.append(((ObjectPropertyContext)_localctx).desc.line); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(142); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 32752L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            descInit = descSB.toString();
			            msgInit = msgSB.toString();
			            ((ObjectPropertyContext)_localctx).property =  new ObjectPropertyParseRecord(nameInit, typeInit,
			                    subTypeInit, idTypeInit, codeInit, powerInit, multInit,
			                    typeMultInit, adjectiveInit, negAdjectiveInit,
			                    msgInit, binduiInit, tagInit, valueInit, auxInit,
			                    descInit, line);
			        
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
		public String declaredRecordCount;
		public List<ObjectPropertyParseRecord> properties;
		public RecordCountContext recordCount;
		public ObjectPropertyContext objectProperty;
		public RecordCountContext recordCount() {
			return getRuleContext(RecordCountContext.class,0);
		}
		public TerminalNode EOF() { return getToken(ObjectPropertyGrammar.EOF, 0); }
		public List<ObjectPropertyContext> objectProperty() {
			return getRuleContexts(ObjectPropertyContext.class);
		}
		public ObjectPropertyContext objectProperty(int i) {
			return getRuleContext(ObjectPropertyContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectPropertyGrammarListener ) ((ObjectPropertyGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectPropertyGrammarVisitor ) return ((ObjectPropertyGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_file);

		            ((FileContext)_localctx).properties =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			((FileContext)_localctx).recordCount = recordCount();
			((FileContext)_localctx).declaredRecordCount =  ((FileContext)_localctx).recordCount.count; 
			setState(149); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(146);
				((FileContext)_localctx).objectProperty = objectProperty();
				_localctx.properties.add(((FileContext)_localctx).objectProperty.property); 
				}
				}
				setState(151); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(153);
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
		"\u0004\u0001\u001c\u009c\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\fZ\b\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0003\fb\b\f\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0004\u000e\u008d\b\u000e\u000b\u000e\f\u000e\u008e\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0004\u000f\u0096"+
		"\b\u000f\u000b\u000f\f\u000f\u0097\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0000\u0000\u0010\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014"+
		"\u0016\u0018\u001a\u001c\u001e\u0000\u0000\u0099\u0000 \u0001\u0000\u0000"+
		"\u0000\u0002$\u0001\u0000\u0000\u0000\u0004(\u0001\u0000\u0000\u0000\u0006"+
		",\u0001\u0000\u0000\u0000\b0\u0001\u0000\u0000\u0000\n4\u0001\u0000\u0000"+
		"\u0000\f8\u0001\u0000\u0000\u0000\u000e<\u0001\u0000\u0000\u0000\u0010"+
		"@\u0001\u0000\u0000\u0000\u0012F\u0001\u0000\u0000\u0000\u0014J\u0001"+
		"\u0000\u0000\u0000\u0016N\u0001\u0000\u0000\u0000\u0018R\u0001\u0000\u0000"+
		"\u0000\u001ac\u0001\u0000\u0000\u0000\u001cg\u0001\u0000\u0000\u0000\u001e"+
		"\u0090\u0001\u0000\u0000\u0000 !\u0005\u0001\u0000\u0000!\"\u0005\u0010"+
		"\u0000\u0000\"#\u0006\u0000\uffff\uffff\u0000#\u0001\u0001\u0000\u0000"+
		"\u0000$%\u0005\u0002\u0000\u0000%&\u0005\u0017\u0000\u0000&\'\u0006\u0001"+
		"\uffff\uffff\u0000\'\u0003\u0001\u0000\u0000\u0000()\u0005\u0003\u0000"+
		"\u0000)*\u0005\u0013\u0000\u0000*+\u0006\u0002\uffff\uffff\u0000+\u0005"+
		"\u0001\u0000\u0000\u0000,-\u0005\u0004\u0000\u0000-.\u0005\u0017\u0000"+
		"\u0000./\u0006\u0003\uffff\uffff\u0000/\u0007\u0001\u0000\u0000\u0000"+
		"01\u0005\u0005\u0000\u000012\u0005\u0017\u0000\u000023\u0006\u0004\uffff"+
		"\uffff\u00003\t\u0001\u0000\u0000\u000045\u0005\u0006\u0000\u000056\u0005"+
		"\u0014\u0000\u000067\u0006\u0005\uffff\uffff\u00007\u000b\u0001\u0000"+
		"\u0000\u000089\u0005\u0007\u0000\u00009:\u0005\u0010\u0000\u0000:;\u0006"+
		"\u0006\uffff\uffff\u0000;\r\u0001\u0000\u0000\u0000<=\u0005\b\u0000\u0000"+
		"=>\u0005\u0010\u0000\u0000>?\u0006\u0007\uffff\uffff\u0000?\u000f\u0001"+
		"\u0000\u0000\u0000@A\u0005\t\u0000\u0000AB\u0005\u001a\u0000\u0000BC\u0005"+
		"\u001b\u0000\u0000CD\u0005\u0019\u0000\u0000DE\u0006\b\uffff\uffff\u0000"+
		"E\u0011\u0001\u0000\u0000\u0000FG\u0005\n\u0000\u0000GH\u0005\u0013\u0000"+
		"\u0000HI\u0006\t\uffff\uffff\u0000I\u0013\u0001\u0000\u0000\u0000JK\u0005"+
		"\u000b\u0000\u0000KL\u0005\u0013\u0000\u0000LM\u0006\n\uffff\uffff\u0000"+
		"M\u0015\u0001\u0000\u0000\u0000NO\u0005\f\u0000\u0000OP\u0005\u0017\u0000"+
		"\u0000PQ\u0006\u000b\uffff\uffff\u0000Q\u0017\u0001\u0000\u0000\u0000"+
		"RS\u0005\r\u0000\u0000ST\u0005\u0013\u0000\u0000TY\u0006\f\uffff\uffff"+
		"\u0000UV\u0005\u0011\u0000\u0000VW\u0005\u0014\u0000\u0000WX\u0005\u0012"+
		"\u0000\u0000XZ\u0006\f\uffff\uffff\u0000YU\u0001\u0000\u0000\u0000YZ\u0001"+
		"\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\\\u0005\u000f\u0000\u0000"+
		"\\]\u0005\u0010\u0000\u0000]a\u0006\f\uffff\uffff\u0000^_\u0005\u000f"+
		"\u0000\u0000_`\u0005\u0010\u0000\u0000`b\u0006\f\uffff\uffff\u0000a^\u0001"+
		"\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000b\u0019\u0001\u0000\u0000"+
		"\u0000cd\u0005\u000e\u0000\u0000de\u0005\u0017\u0000\u0000ef\u0006\r\uffff"+
		"\uffff\u0000f\u001b\u0001\u0000\u0000\u0000gh\u0003\u0002\u0001\u0000"+
		"hi\u0006\u000e\uffff\uffff\u0000ij\u0003\u0004\u0002\u0000j\u008c\u0006"+
		"\u000e\uffff\uffff\u0000kl\u0003\u0006\u0003\u0000lm\u0006\u000e\uffff"+
		"\uffff\u0000m\u008d\u0001\u0000\u0000\u0000no\u0003\b\u0004\u0000op\u0006"+
		"\u000e\uffff\uffff\u0000p\u008d\u0001\u0000\u0000\u0000qr\u0003\n\u0005"+
		"\u0000rs\u0006\u000e\uffff\uffff\u0000s\u008d\u0001\u0000\u0000\u0000"+
		"tu\u0003\f\u0006\u0000uv\u0006\u000e\uffff\uffff\u0000v\u008d\u0001\u0000"+
		"\u0000\u0000wx\u0003\u000e\u0007\u0000xy\u0006\u000e\uffff\uffff\u0000"+
		"y\u008d\u0001\u0000\u0000\u0000z{\u0003\u0010\b\u0000{|\u0006\u000e\uffff"+
		"\uffff\u0000|\u008d\u0001\u0000\u0000\u0000}~\u0003\u0012\t\u0000~\u007f"+
		"\u0006\u000e\uffff\uffff\u0000\u007f\u008d\u0001\u0000\u0000\u0000\u0080"+
		"\u0081\u0003\u0014\n\u0000\u0081\u0082\u0006\u000e\uffff\uffff\u0000\u0082"+
		"\u008d\u0001\u0000\u0000\u0000\u0083\u0084\u0003\u0016\u000b\u0000\u0084"+
		"\u0085\u0006\u000e\uffff\uffff\u0000\u0085\u008d\u0001\u0000\u0000\u0000"+
		"\u0086\u0087\u0003\u0018\f\u0000\u0087\u0088\u0006\u000e\uffff\uffff\u0000"+
		"\u0088\u008d\u0001\u0000\u0000\u0000\u0089\u008a\u0003\u001a\r\u0000\u008a"+
		"\u008b\u0006\u000e\uffff\uffff\u0000\u008b\u008d\u0001\u0000\u0000\u0000"+
		"\u008ck\u0001\u0000\u0000\u0000\u008cn\u0001\u0000\u0000\u0000\u008cq"+
		"\u0001\u0000\u0000\u0000\u008ct\u0001\u0000\u0000\u0000\u008cw\u0001\u0000"+
		"\u0000\u0000\u008cz\u0001\u0000\u0000\u0000\u008c}\u0001\u0000\u0000\u0000"+
		"\u008c\u0080\u0001\u0000\u0000\u0000\u008c\u0083\u0001\u0000\u0000\u0000"+
		"\u008c\u0086\u0001\u0000\u0000\u0000\u008c\u0089\u0001\u0000\u0000\u0000"+
		"\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000"+
		"\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u001d\u0001\u0000\u0000\u0000"+
		"\u0090\u0091\u0003\u0000\u0000\u0000\u0091\u0095\u0006\u000f\uffff\uffff"+
		"\u0000\u0092\u0093\u0003\u001c\u000e\u0000\u0093\u0094\u0006\u000f\uffff"+
		"\uffff\u0000\u0094\u0096\u0001\u0000\u0000\u0000\u0095\u0092\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0095\u0001\u0000"+
		"\u0000\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u0099\u0001\u0000"+
		"\u0000\u0000\u0099\u009a\u0005\u0000\u0000\u0001\u009a\u001f\u0001\u0000"+
		"\u0000\u0000\u0005Ya\u008c\u008e\u0097";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}