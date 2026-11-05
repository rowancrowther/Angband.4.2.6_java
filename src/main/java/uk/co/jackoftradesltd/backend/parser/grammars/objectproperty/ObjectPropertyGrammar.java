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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/ObjectPropertyGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.objectproperty;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectPropertyParseRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectPropertyGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, TYPE=2, SUBTYPE=3, ID_TYPE=4, CODE=5, POWER=6, MULT=7, TYPE_MULT=8, 
		ADJECTIVE=9, NEG_ADJECTIVE=10, MSG=11, BINDUI=12, DESC=13, COLON=14, INTEGER=15, 
		LTHAN=16, GTHAN=17, LCASE=18, UCASE=19, COMMENT=20, EOL=21, STRING=22, 
		EOL_POP=23, DELIM_INTEGER=24, STRING_BETWEEN_COLONS=25, DELIM_COLON=26, 
		END_SKIP=27;
	public static final int
		RULE_name = 0, RULE_type = 1, RULE_subtype = 2, RULE_idType = 3, RULE_codeVal = 4, 
		RULE_power = 5, RULE_mult = 6, RULE_typeMult = 7, RULE_adjective = 8, 
		RULE_negAdjective = 9, RULE_msg = 10, RULE_bindui = 11, RULE_desc = 12, 
		RULE_objectProperty = 13, RULE_file = 14;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "type", "subtype", "idType", "codeVal", "power", "mult", "typeMult", 
			"adjective", "negAdjective", "msg", "bindui", "desc", "objectProperty", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'type:'", "'subtype:'", "'id-type:'", "'code:'", "'power:'", 
			"'mult:'", "'type-mult:'", "'adjective:'", "'neg-adjective:'", "'msg:'", 
			"'bindui:'", "'desc:'", null, null, "'<'", "'>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "TYPE", "SUBTYPE", "ID_TYPE", "CODE", "POWER", "MULT", 
			"TYPE_MULT", "ADJECTIVE", "NEG_ADJECTIVE", "MSG", "BINDUI", "DESC", "COLON", 
			"INTEGER", "LTHAN", "GTHAN", "LCASE", "UCASE", "COMMENT", "EOL", "STRING", 
			"EOL_POP", "DELIM_INTEGER", "STRING_BETWEEN_COLONS", "DELIM_COLON", "END_SKIP"
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
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(NAME);
			setState(31);
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
		enterRule(_localctx, 2, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			match(TYPE);
			setState(35);
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
		enterRule(_localctx, 4, RULE_subtype);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(SUBTYPE);
			setState(39);
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
		enterRule(_localctx, 6, RULE_idType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(ID_TYPE);
			setState(43);
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
		enterRule(_localctx, 8, RULE_codeVal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(CODE);
			setState(47);
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
		enterRule(_localctx, 10, RULE_power);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(POWER);
			setState(51);
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
		enterRule(_localctx, 12, RULE_mult);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			match(MULT);
			setState(55);
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
		enterRule(_localctx, 14, RULE_typeMult);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(TYPE_MULT);
			setState(59);
			((TypeMultContext)_localctx).t = match(STRING_BETWEEN_COLONS);
			setState(60);
			match(DELIM_COLON);
			setState(61);
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
		enterRule(_localctx, 16, RULE_adjective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(ADJECTIVE);
			setState(65);
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
		enterRule(_localctx, 18, RULE_negAdjective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(NEG_ADJECTIVE);
			setState(69);
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
		enterRule(_localctx, 20, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(MSG);
			setState(73);
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
		enterRule(_localctx, 22, RULE_bindui);

		            ((BinduiContext)_localctx).tag =  "";
		            ((BinduiContext)_localctx).uiVal =  null;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(BINDUI);
			setState(77);
			((BinduiContext)_localctx).b = match(LCASE);
			 ((BinduiContext)_localctx).binding =  ((BinduiContext)_localctx).b.getText(); 
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LTHAN) {
				{
				setState(79);
				match(LTHAN);
				setState(80);
				((BinduiContext)_localctx).t = match(UCASE);
				setState(81);
				match(GTHAN);
				 ((BinduiContext)_localctx).tag =  ((BinduiContext)_localctx).t.getText(); 
				}
			}

			setState(85);
			match(COLON);
			setState(86);
			((BinduiContext)_localctx).a = match(INTEGER);
			((BinduiContext)_localctx).aux =  ((BinduiContext)_localctx).a.getText(); 
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(88);
				match(COLON);
				setState(89);
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
		enterRule(_localctx, 24, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(DESC);
			setState(94);
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
		enterRule(_localctx, 26, RULE_objectProperty);

		            String nameInit = "";
		            String typeInit = "";
		            String subTypeInit = "";
		            String idTypeInit = "";
		            String codeInit = "";
		            String powerInit = "";
		            String multInit = "";
		            Map<String, String> typeMultInit = new LinkedHashMap<>();
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
			setState(97);
			((ObjectPropertyContext)_localctx).name = name();
			 line = ((ObjectPropertyContext)_localctx).name.lineNo; nameInit = ((ObjectPropertyContext)_localctx).name.nameString; 
			setState(99);
			((ObjectPropertyContext)_localctx).type = type();
			 typeInit = ((ObjectPropertyContext)_localctx).type.typeStr; 
			setState(134); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(134);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SUBTYPE:
					{
					setState(101);
					((ObjectPropertyContext)_localctx).subtype = subtype();
					 subTypeInit = ((ObjectPropertyContext)_localctx).subtype.subTypeStr; 
					}
					break;
				case ID_TYPE:
					{
					setState(104);
					((ObjectPropertyContext)_localctx).idType = idType();
					 idTypeInit = ((ObjectPropertyContext)_localctx).idType.idTypeStr; 
					}
					break;
				case CODE:
					{
					setState(107);
					((ObjectPropertyContext)_localctx).codeVal = codeVal();
					 codeInit = ((ObjectPropertyContext)_localctx).codeVal.codeStr; 
					}
					break;
				case POWER:
					{
					setState(110);
					((ObjectPropertyContext)_localctx).power = power();
					 powerInit = ((ObjectPropertyContext)_localctx).power.powerVal; 
					}
					break;
				case MULT:
					{
					setState(113);
					((ObjectPropertyContext)_localctx).mult = mult();
					 multInit = ((ObjectPropertyContext)_localctx).mult.multVal; 
					}
					break;
				case TYPE_MULT:
					{
					setState(116);
					((ObjectPropertyContext)_localctx).typeMult = typeMult();
					 typeMultInit.put(((ObjectPropertyContext)_localctx).typeMult.typeTval, ((ObjectPropertyContext)_localctx).typeMult.multVal); 
					}
					break;
				case ADJECTIVE:
					{
					setState(119);
					((ObjectPropertyContext)_localctx).adjective = adjective();
					 adjectiveInit = ((ObjectPropertyContext)_localctx).adjective.adj; 
					}
					break;
				case NEG_ADJECTIVE:
					{
					setState(122);
					((ObjectPropertyContext)_localctx).negAdjective = negAdjective();
					 negAdjectiveInit = ((ObjectPropertyContext)_localctx).negAdjective.negAdj; 
					}
					break;
				case MSG:
					{
					setState(125);
					((ObjectPropertyContext)_localctx).msg = msg();
					 msgSB.append(((ObjectPropertyContext)_localctx).msg.msgLine); 
					}
					break;
				case BINDUI:
					{
					setState(128);
					((ObjectPropertyContext)_localctx).bindui = bindui();
					 binduiInit = ((ObjectPropertyContext)_localctx).bindui.binding;
					                     tagInit = ((ObjectPropertyContext)_localctx).bindui.tag;
					                     valueInit = ((ObjectPropertyContext)_localctx).bindui.uiVal;
					                     auxInit = ((ObjectPropertyContext)_localctx).bindui.aux; 
					}
					break;
				case DESC:
					{
					setState(131);
					((ObjectPropertyContext)_localctx).desc = desc();
					 descSB.append(((ObjectPropertyContext)_localctx).desc.line); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(136); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 16376L) != 0) );
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
		public List<ObjectPropertyParseRecord> properties;
		public ObjectPropertyContext objectProperty;
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
		enterRule(_localctx, 28, RULE_file);

		            ((FileContext)_localctx).properties =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(138);
				((FileContext)_localctx).objectProperty = objectProperty();
				_localctx.properties.add(((FileContext)_localctx).objectProperty.property); 
				}
				}
				setState(143); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(145);
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
		"\u0004\u0001\u001b\u0094\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t"+
		"\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003"+
		"\u000bT\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0003\u000b\\\b\u000b\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0004\r\u0087\b\r\u000b\r\f\r\u0088\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0004\u000e\u008e\b\u000e\u000b\u000e\f\u000e\u008f\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0000\u0000\u000f\u0000\u0002\u0004\u0006"+
		"\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u0000\u0000\u0092"+
		"\u0000\u001e\u0001\u0000\u0000\u0000\u0002\"\u0001\u0000\u0000\u0000\u0004"+
		"&\u0001\u0000\u0000\u0000\u0006*\u0001\u0000\u0000\u0000\b.\u0001\u0000"+
		"\u0000\u0000\n2\u0001\u0000\u0000\u0000\f6\u0001\u0000\u0000\u0000\u000e"+
		":\u0001\u0000\u0000\u0000\u0010@\u0001\u0000\u0000\u0000\u0012D\u0001"+
		"\u0000\u0000\u0000\u0014H\u0001\u0000\u0000\u0000\u0016L\u0001\u0000\u0000"+
		"\u0000\u0018]\u0001\u0000\u0000\u0000\u001aa\u0001\u0000\u0000\u0000\u001c"+
		"\u008d\u0001\u0000\u0000\u0000\u001e\u001f\u0005\u0001\u0000\u0000\u001f"+
		" \u0005\u0016\u0000\u0000 !\u0006\u0000\uffff\uffff\u0000!\u0001\u0001"+
		"\u0000\u0000\u0000\"#\u0005\u0002\u0000\u0000#$\u0005\u0012\u0000\u0000"+
		"$%\u0006\u0001\uffff\uffff\u0000%\u0003\u0001\u0000\u0000\u0000&\'\u0005"+
		"\u0003\u0000\u0000\'(\u0005\u0016\u0000\u0000()\u0006\u0002\uffff\uffff"+
		"\u0000)\u0005\u0001\u0000\u0000\u0000*+\u0005\u0004\u0000\u0000+,\u0005"+
		"\u0016\u0000\u0000,-\u0006\u0003\uffff\uffff\u0000-\u0007\u0001\u0000"+
		"\u0000\u0000./\u0005\u0005\u0000\u0000/0\u0005\u0013\u0000\u000001\u0006"+
		"\u0004\uffff\uffff\u00001\t\u0001\u0000\u0000\u000023\u0005\u0006\u0000"+
		"\u000034\u0005\u000f\u0000\u000045\u0006\u0005\uffff\uffff\u00005\u000b"+
		"\u0001\u0000\u0000\u000067\u0005\u0007\u0000\u000078\u0005\u000f\u0000"+
		"\u000089\u0006\u0006\uffff\uffff\u00009\r\u0001\u0000\u0000\u0000:;\u0005"+
		"\b\u0000\u0000;<\u0005\u0019\u0000\u0000<=\u0005\u001a\u0000\u0000=>\u0005"+
		"\u0018\u0000\u0000>?\u0006\u0007\uffff\uffff\u0000?\u000f\u0001\u0000"+
		"\u0000\u0000@A\u0005\t\u0000\u0000AB\u0005\u0012\u0000\u0000BC\u0006\b"+
		"\uffff\uffff\u0000C\u0011\u0001\u0000\u0000\u0000DE\u0005\n\u0000\u0000"+
		"EF\u0005\u0012\u0000\u0000FG\u0006\t\uffff\uffff\u0000G\u0013\u0001\u0000"+
		"\u0000\u0000HI\u0005\u000b\u0000\u0000IJ\u0005\u0016\u0000\u0000JK\u0006"+
		"\n\uffff\uffff\u0000K\u0015\u0001\u0000\u0000\u0000LM\u0005\f\u0000\u0000"+
		"MN\u0005\u0012\u0000\u0000NS\u0006\u000b\uffff\uffff\u0000OP\u0005\u0010"+
		"\u0000\u0000PQ\u0005\u0013\u0000\u0000QR\u0005\u0011\u0000\u0000RT\u0006"+
		"\u000b\uffff\uffff\u0000SO\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000"+
		"\u0000TU\u0001\u0000\u0000\u0000UV\u0005\u000e\u0000\u0000VW\u0005\u000f"+
		"\u0000\u0000W[\u0006\u000b\uffff\uffff\u0000XY\u0005\u000e\u0000\u0000"+
		"YZ\u0005\u000f\u0000\u0000Z\\\u0006\u000b\uffff\uffff\u0000[X\u0001\u0000"+
		"\u0000\u0000[\\\u0001\u0000\u0000\u0000\\\u0017\u0001\u0000\u0000\u0000"+
		"]^\u0005\r\u0000\u0000^_\u0005\u0016\u0000\u0000_`\u0006\f\uffff\uffff"+
		"\u0000`\u0019\u0001\u0000\u0000\u0000ab\u0003\u0000\u0000\u0000bc\u0006"+
		"\r\uffff\uffff\u0000cd\u0003\u0002\u0001\u0000d\u0086\u0006\r\uffff\uffff"+
		"\u0000ef\u0003\u0004\u0002\u0000fg\u0006\r\uffff\uffff\u0000g\u0087\u0001"+
		"\u0000\u0000\u0000hi\u0003\u0006\u0003\u0000ij\u0006\r\uffff\uffff\u0000"+
		"j\u0087\u0001\u0000\u0000\u0000kl\u0003\b\u0004\u0000lm\u0006\r\uffff"+
		"\uffff\u0000m\u0087\u0001\u0000\u0000\u0000no\u0003\n\u0005\u0000op\u0006"+
		"\r\uffff\uffff\u0000p\u0087\u0001\u0000\u0000\u0000qr\u0003\f\u0006\u0000"+
		"rs\u0006\r\uffff\uffff\u0000s\u0087\u0001\u0000\u0000\u0000tu\u0003\u000e"+
		"\u0007\u0000uv\u0006\r\uffff\uffff\u0000v\u0087\u0001\u0000\u0000\u0000"+
		"wx\u0003\u0010\b\u0000xy\u0006\r\uffff\uffff\u0000y\u0087\u0001\u0000"+
		"\u0000\u0000z{\u0003\u0012\t\u0000{|\u0006\r\uffff\uffff\u0000|\u0087"+
		"\u0001\u0000\u0000\u0000}~\u0003\u0014\n\u0000~\u007f\u0006\r\uffff\uffff"+
		"\u0000\u007f\u0087\u0001\u0000\u0000\u0000\u0080\u0081\u0003\u0016\u000b"+
		"\u0000\u0081\u0082\u0006\r\uffff\uffff\u0000\u0082\u0087\u0001\u0000\u0000"+
		"\u0000\u0083\u0084\u0003\u0018\f\u0000\u0084\u0085\u0006\r\uffff\uffff"+
		"\u0000\u0085\u0087\u0001\u0000\u0000\u0000\u0086e\u0001\u0000\u0000\u0000"+
		"\u0086h\u0001\u0000\u0000\u0000\u0086k\u0001\u0000\u0000\u0000\u0086n"+
		"\u0001\u0000\u0000\u0000\u0086q\u0001\u0000\u0000\u0000\u0086t\u0001\u0000"+
		"\u0000\u0000\u0086w\u0001\u0000\u0000\u0000\u0086z\u0001\u0000\u0000\u0000"+
		"\u0086}\u0001\u0000\u0000\u0000\u0086\u0080\u0001\u0000\u0000\u0000\u0086"+
		"\u0083\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088"+
		"\u0086\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000\u0089"+
		"\u001b\u0001\u0000\u0000\u0000\u008a\u008b\u0003\u001a\r\u0000\u008b\u008c"+
		"\u0006\u000e\uffff\uffff\u0000\u008c\u008e\u0001\u0000\u0000\u0000\u008d"+
		"\u008a\u0001\u0000\u0000\u0000\u008e\u008f\u0001\u0000\u0000\u0000\u008f"+
		"\u008d\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000\u0000\u0000\u0090"+
		"\u0091\u0001\u0000\u0000\u0000\u0091\u0092\u0005\u0000\u0000\u0001\u0092"+
		"\u001d\u0001\u0000\u0000\u0000\u0005S[\u0086\u0088\u008f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}