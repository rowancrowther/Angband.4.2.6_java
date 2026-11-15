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

// Generated from /tmp/claude-1000/-home-rowan-IdeaProjects-Angband-4-2-6-java/f8028824-ed88-432e-9012-db1970966e36/scratchpad/gen7/ObjectProperty2Grammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.objectproperty2;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectProperty2ParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectProperty2Grammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, TYPE=2, SUBTYPE=3, ID_TYPE=4, CODE=5, POWER=6, MULT=7, TYPE_MULT=8, 
		ADJECTIVE=9, NEG_ADJECTIVE=10, MSG=11, BINDUI=12, DESC=13, COLON=14, COMMENT=15, 
		INDENT=16, EOL=17, LCASE=18, LCASE_EOL=19, UCASE=20, UCASE_EOL=21, BINDUI_STRING=22, 
		BINDUI_INTRO_COLON=23, BINDUI_LTHAN=24, BINDUI_TAG=25, BINDUI_GTHAN=26, 
		BINDUI_INTEGER=27, BINDUI_COLON=28, BINDUI_NL=29, INTEGER=30, STRING=31, 
		EOL_POP=32, DELIM_STRING=33, DELIM_INTRO_COLON=34, DELIM_INTEGER=35, END_SKIP=36;
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
			"'bindui:'", "'desc:'", null, null, null, null, null, null, null, null, 
			null, null, "'<'", null, "'>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "TYPE", "SUBTYPE", "ID_TYPE", "CODE", "POWER", "MULT", 
			"TYPE_MULT", "ADJECTIVE", "NEG_ADJECTIVE", "MSG", "BINDUI", "DESC", "COLON", 
			"COMMENT", "INDENT", "EOL", "LCASE", "LCASE_EOL", "UCASE", "UCASE_EOL", 
			"BINDUI_STRING", "BINDUI_INTRO_COLON", "BINDUI_LTHAN", "BINDUI_TAG", 
			"BINDUI_GTHAN", "BINDUI_INTEGER", "BINDUI_COLON", "BINDUI_NL", "INTEGER", 
			"STRING", "EOL_POP", "DELIM_STRING", "DELIM_INTRO_COLON", "DELIM_INTEGER", 
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
	public String getGrammarFileName() { return "ObjectProperty2Grammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ObjectProperty2Grammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public Directive nameDirective;
		public Token STRING;
		public TerminalNode NAME() { return getToken(ObjectProperty2Grammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(ObjectProperty2Grammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitName(this);
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

			                String nameString = ((NameContext)_localctx).STRING.getText();
			                int lineNo = _localctx.start.getLine(); 
			                List<String> values = new ArrayList<>();
			                values.add(nameString);
			                ((NameContext)_localctx).nameDirective =  new Directive("name", values, lineNo);
			            
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
		public Directive typeDirective;
		public Token LCASE;
		public TerminalNode TYPE() { return getToken(ObjectProperty2Grammar.TYPE, 0); }
		public TerminalNode LCASE() { return getToken(ObjectProperty2Grammar.LCASE, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitType(this);
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
			 
			                String typeStr = ((TypeContext)_localctx).LCASE.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(typeStr);
			                ((TypeContext)_localctx).typeDirective =  new Directive("type", values, lineNo);
			            
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
		public Directive subTypeDirective;
		public Token STRING;
		public TerminalNode SUBTYPE() { return getToken(ObjectProperty2Grammar.SUBTYPE, 0); }
		public TerminalNode STRING() { return getToken(ObjectProperty2Grammar.STRING, 0); }
		public SubtypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterSubtype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitSubtype(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitSubtype(this);
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
			 
			                String subTypeStr = ((SubtypeContext)_localctx).STRING.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(subTypeStr);
			                ((SubtypeContext)_localctx).subTypeDirective =  new Directive("subtype", values, lineNo);
			            
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
		public Directive idTypeDirective;
		public Token STRING;
		public TerminalNode ID_TYPE() { return getToken(ObjectProperty2Grammar.ID_TYPE, 0); }
		public TerminalNode STRING() { return getToken(ObjectProperty2Grammar.STRING, 0); }
		public IdTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterIdType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitIdType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitIdType(this);
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
			 
			                String idTypeStr = ((IdTypeContext)_localctx).STRING.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(idTypeStr);
			                ((IdTypeContext)_localctx).idTypeDirective =  new Directive("id-type", values, lineNo);
			            
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
		public Directive codeDirective;
		public Token UCASE;
		public TerminalNode CODE() { return getToken(ObjectProperty2Grammar.CODE, 0); }
		public TerminalNode UCASE() { return getToken(ObjectProperty2Grammar.UCASE, 0); }
		public CodeValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_codeVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterCodeVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitCodeVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitCodeVal(this);
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

			                String codeStr = ((CodeValContext)_localctx).UCASE.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(codeStr);
			                ((CodeValContext)_localctx).codeDirective =  new Directive("code", values, lineNo);
			            
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
		public Directive powerDirective;
		public Token INTEGER;
		public TerminalNode POWER() { return getToken(ObjectProperty2Grammar.POWER, 0); }
		public TerminalNode INTEGER() { return getToken(ObjectProperty2Grammar.INTEGER, 0); }
		public PowerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_power; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitPower(this);
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

			                String powerVal = ((PowerContext)_localctx).INTEGER.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(powerVal);
			                ((PowerContext)_localctx).powerDirective =  new Directive("power", values, lineNo); 
			            
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
		public Directive multDirective;
		public Token INTEGER;
		public TerminalNode MULT() { return getToken(ObjectProperty2Grammar.MULT, 0); }
		public TerminalNode INTEGER() { return getToken(ObjectProperty2Grammar.INTEGER, 0); }
		public MultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mult; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitMult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitMult(this);
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

			                String multVal = ((MultContext)_localctx).INTEGER.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(multVal);
			                ((MultContext)_localctx).multDirective =  new Directive("mult", values, lineNo); 
			            
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
		public Directive typeMultDirective;
		public Token t;
		public Token v;
		public TerminalNode TYPE_MULT() { return getToken(ObjectProperty2Grammar.TYPE_MULT, 0); }
		public TerminalNode DELIM_STRING() { return getToken(ObjectProperty2Grammar.DELIM_STRING, 0); }
		public TerminalNode DELIM_INTEGER() { return getToken(ObjectProperty2Grammar.DELIM_INTEGER, 0); }
		public TypeMultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeMult; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterTypeMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitTypeMult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitTypeMult(this);
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
			((TypeMultContext)_localctx).t = match(DELIM_STRING);
			setState(60);
			((TypeMultContext)_localctx).v = match(DELIM_INTEGER);

			                String typeTval = ((TypeMultContext)_localctx).t.getText();
			                String multVal  = ((TypeMultContext)_localctx).v.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(typeTval);
			                values.add(multVal);
			                ((TypeMultContext)_localctx).typeMultDirective =  new Directive("type-mult", values, lineNo); 
			            
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
		public Directive adjDirective;
		public Token STRING;
		public TerminalNode ADJECTIVE() { return getToken(ObjectProperty2Grammar.ADJECTIVE, 0); }
		public TerminalNode STRING() { return getToken(ObjectProperty2Grammar.STRING, 0); }
		public AdjectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_adjective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterAdjective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitAdjective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitAdjective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdjectiveContext adjective() throws RecognitionException {
		AdjectiveContext _localctx = new AdjectiveContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_adjective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(ADJECTIVE);
			setState(64);
			((AdjectiveContext)_localctx).STRING = match(STRING);

			                String adj = ((AdjectiveContext)_localctx).STRING.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(adj);
			                ((AdjectiveContext)_localctx).adjDirective =  new Directive("adjective", values, lineNo); 
			            
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
		public Directive negAdjDirective;
		public Token STRING;
		public TerminalNode NEG_ADJECTIVE() { return getToken(ObjectProperty2Grammar.NEG_ADJECTIVE, 0); }
		public TerminalNode STRING() { return getToken(ObjectProperty2Grammar.STRING, 0); }
		public NegAdjectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negAdjective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterNegAdjective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitNegAdjective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitNegAdjective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NegAdjectiveContext negAdjective() throws RecognitionException {
		NegAdjectiveContext _localctx = new NegAdjectiveContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_negAdjective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(NEG_ADJECTIVE);
			setState(68);
			((NegAdjectiveContext)_localctx).STRING = match(STRING);

			                String negAdj = ((NegAdjectiveContext)_localctx).STRING.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(negAdj);
			                ((NegAdjectiveContext)_localctx).negAdjDirective =  new Directive("neg-adjective", values, lineNo);
			            
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
		public Directive msgLineDirective;
		public Token STRING;
		public TerminalNode MSG() { return getToken(ObjectProperty2Grammar.MSG, 0); }
		public TerminalNode STRING() { return getToken(ObjectProperty2Grammar.STRING, 0); }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(MSG);
			setState(72);
			((MsgContext)_localctx).STRING = match(STRING);
			 
			                String msgLine = ((MsgContext)_localctx).STRING.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(msgLine);
			                ((MsgContext)_localctx).msgLineDirective =  new Directive("msg", values, lineNo);
			            
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
		public Directive binduiDirective;
		public Token b;
		public Token t;
		public Token a;
		public Token u;
		public TerminalNode BINDUI() { return getToken(ObjectProperty2Grammar.BINDUI, 0); }
		public TerminalNode BINDUI_STRING() { return getToken(ObjectProperty2Grammar.BINDUI_STRING, 0); }
		public List<TerminalNode> BINDUI_INTEGER() { return getTokens(ObjectProperty2Grammar.BINDUI_INTEGER); }
		public TerminalNode BINDUI_INTEGER(int i) {
			return getToken(ObjectProperty2Grammar.BINDUI_INTEGER, i);
		}
		public TerminalNode BINDUI_TAG() { return getToken(ObjectProperty2Grammar.BINDUI_TAG, 0); }
		public BinduiContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindui; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterBindui(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitBindui(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitBindui(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinduiContext bindui() throws RecognitionException {
		BinduiContext _localctx = new BinduiContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_bindui);

		            String binding = "";
		            String tag = "";
		            String aux = "";
		            String uiVal = null;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(BINDUI);
			setState(76);
			((BinduiContext)_localctx).b = match(BINDUI_STRING);
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BINDUI_TAG) {
				{
				setState(77);
				((BinduiContext)_localctx).t = match(BINDUI_TAG);
				 tag = ((BinduiContext)_localctx).t.getText(); 
				}
			}

			setState(81);
			((BinduiContext)_localctx).a = match(BINDUI_INTEGER);
			setState(84);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BINDUI_INTEGER) {
				{
				setState(82);
				((BinduiContext)_localctx).u = match(BINDUI_INTEGER);
				 uiVal = ((BinduiContext)_localctx).u.getText(); 
				}
			}


			                binding = ((BinduiContext)_localctx).b.getText();
			                aux = ((BinduiContext)_localctx).a.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(binding);
			                values.add(tag);
			                values.add(aux);
			                values.add(uiVal);
			                ((BinduiContext)_localctx).binduiDirective =  new Directive("bindui", values, lineNo);
			            
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
		public Directive lineDirective;
		public Token STRING;
		public TerminalNode DESC() { return getToken(ObjectProperty2Grammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(ObjectProperty2Grammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(DESC);
			setState(89);
			((DescContext)_localctx).STRING = match(STRING);

			                String line = ((DescContext)_localctx).STRING.getText();
			                int lineNo = _localctx.start.getLine();
			                List<String> values = new ArrayList<>();
			                values.add(line);
			                ((DescContext)_localctx).lineDirective =  new Directive("desc", values, lineNo); 
			            
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
		public ObjectProperty2ParseRecord property;
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
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
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
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterObjectProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitObjectProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitObjectProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjectPropertyContext objectProperty() throws RecognitionException {
		ObjectPropertyContext _localctx = new ObjectPropertyContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_objectProperty);

		            List<Directive> directives = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			((ObjectPropertyContext)_localctx).name = name();
			 directives.add(((ObjectPropertyContext)_localctx).name.nameDirective); 
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 16380L) != 0)) {
				{
				setState(130);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case TYPE:
					{
					setState(94);
					((ObjectPropertyContext)_localctx).type = type();
					 directives.add(((ObjectPropertyContext)_localctx).type.typeDirective); 
					}
					break;
				case SUBTYPE:
					{
					setState(97);
					((ObjectPropertyContext)_localctx).subtype = subtype();
					 directives.add(((ObjectPropertyContext)_localctx).subtype.subTypeDirective); 
					}
					break;
				case ID_TYPE:
					{
					setState(100);
					((ObjectPropertyContext)_localctx).idType = idType();
					 directives.add(((ObjectPropertyContext)_localctx).idType.idTypeDirective); 
					}
					break;
				case CODE:
					{
					setState(103);
					((ObjectPropertyContext)_localctx).codeVal = codeVal();
					 directives.add(((ObjectPropertyContext)_localctx).codeVal.codeDirective); 
					}
					break;
				case POWER:
					{
					setState(106);
					((ObjectPropertyContext)_localctx).power = power();
					 directives.add(((ObjectPropertyContext)_localctx).power.powerDirective); 
					}
					break;
				case MULT:
					{
					setState(109);
					((ObjectPropertyContext)_localctx).mult = mult();
					 directives.add(((ObjectPropertyContext)_localctx).mult.multDirective); 
					}
					break;
				case TYPE_MULT:
					{
					setState(112);
					((ObjectPropertyContext)_localctx).typeMult = typeMult();
					 directives.add(((ObjectPropertyContext)_localctx).typeMult.typeMultDirective); 
					}
					break;
				case ADJECTIVE:
					{
					setState(115);
					((ObjectPropertyContext)_localctx).adjective = adjective();
					 directives.add(((ObjectPropertyContext)_localctx).adjective.adjDirective); 
					}
					break;
				case NEG_ADJECTIVE:
					{
					setState(118);
					((ObjectPropertyContext)_localctx).negAdjective = negAdjective();
					 directives.add(((ObjectPropertyContext)_localctx).negAdjective.negAdjDirective); 
					}
					break;
				case MSG:
					{
					setState(121);
					((ObjectPropertyContext)_localctx).msg = msg();
					 directives.add(((ObjectPropertyContext)_localctx).msg.msgLineDirective); 
					}
					break;
				case BINDUI:
					{
					setState(124);
					((ObjectPropertyContext)_localctx).bindui = bindui();
					 directives.add(((ObjectPropertyContext)_localctx).bindui.binduiDirective); 
					}
					break;
				case DESC:
					{
					setState(127);
					((ObjectPropertyContext)_localctx).desc = desc();
					 directives.add(((ObjectPropertyContext)_localctx).desc.lineDirective); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((ObjectPropertyContext)_localctx).property =  new ObjectProperty2ParseRecord(directives);
			        
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
		public List<ObjectProperty2ParseRecord> properties;
		public ObjectPropertyContext objectProperty;
		public TerminalNode EOF() { return getToken(ObjectProperty2Grammar.EOF, 0); }
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
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectProperty2GrammarListener ) ((ObjectProperty2GrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectProperty2GrammarVisitor ) return ((ObjectProperty2GrammarVisitor<? extends T>)visitor).visitFile(this);
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
			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(135);
				((FileContext)_localctx).objectProperty = objectProperty();
				_localctx.properties.add(((FileContext)_localctx).objectProperty.property); 
				}
				}
				setState(142);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(143);
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
		"\u0004\u0001$\u0092\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0003\u000bP\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0003\u000bU\b\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f"+
		"\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0005\r\u0083\b\r\n\r\f\r\u0086\t\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0005\u000e\u008b\b\u000e\n\u000e\f\u000e\u008e"+
		"\t\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0000\u0000\u000f\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u0000"+
		"\u0000\u0091\u0000\u001e\u0001\u0000\u0000\u0000\u0002\"\u0001\u0000\u0000"+
		"\u0000\u0004&\u0001\u0000\u0000\u0000\u0006*\u0001\u0000\u0000\u0000\b"+
		".\u0001\u0000\u0000\u0000\n2\u0001\u0000\u0000\u0000\f6\u0001\u0000\u0000"+
		"\u0000\u000e:\u0001\u0000\u0000\u0000\u0010?\u0001\u0000\u0000\u0000\u0012"+
		"C\u0001\u0000\u0000\u0000\u0014G\u0001\u0000\u0000\u0000\u0016K\u0001"+
		"\u0000\u0000\u0000\u0018X\u0001\u0000\u0000\u0000\u001a\\\u0001\u0000"+
		"\u0000\u0000\u001c\u008c\u0001\u0000\u0000\u0000\u001e\u001f\u0005\u0001"+
		"\u0000\u0000\u001f \u0005\u001f\u0000\u0000 !\u0006\u0000\uffff\uffff"+
		"\u0000!\u0001\u0001\u0000\u0000\u0000\"#\u0005\u0002\u0000\u0000#$\u0005"+
		"\u0012\u0000\u0000$%\u0006\u0001\uffff\uffff\u0000%\u0003\u0001\u0000"+
		"\u0000\u0000&\'\u0005\u0003\u0000\u0000\'(\u0005\u001f\u0000\u0000()\u0006"+
		"\u0002\uffff\uffff\u0000)\u0005\u0001\u0000\u0000\u0000*+\u0005\u0004"+
		"\u0000\u0000+,\u0005\u001f\u0000\u0000,-\u0006\u0003\uffff\uffff\u0000"+
		"-\u0007\u0001\u0000\u0000\u0000./\u0005\u0005\u0000\u0000/0\u0005\u0014"+
		"\u0000\u000001\u0006\u0004\uffff\uffff\u00001\t\u0001\u0000\u0000\u0000"+
		"23\u0005\u0006\u0000\u000034\u0005\u001e\u0000\u000045\u0006\u0005\uffff"+
		"\uffff\u00005\u000b\u0001\u0000\u0000\u000067\u0005\u0007\u0000\u0000"+
		"78\u0005\u001e\u0000\u000089\u0006\u0006\uffff\uffff\u00009\r\u0001\u0000"+
		"\u0000\u0000:;\u0005\b\u0000\u0000;<\u0005!\u0000\u0000<=\u0005#\u0000"+
		"\u0000=>\u0006\u0007\uffff\uffff\u0000>\u000f\u0001\u0000\u0000\u0000"+
		"?@\u0005\t\u0000\u0000@A\u0005\u001f\u0000\u0000AB\u0006\b\uffff\uffff"+
		"\u0000B\u0011\u0001\u0000\u0000\u0000CD\u0005\n\u0000\u0000DE\u0005\u001f"+
		"\u0000\u0000EF\u0006\t\uffff\uffff\u0000F\u0013\u0001\u0000\u0000\u0000"+
		"GH\u0005\u000b\u0000\u0000HI\u0005\u001f\u0000\u0000IJ\u0006\n\uffff\uffff"+
		"\u0000J\u0015\u0001\u0000\u0000\u0000KL\u0005\f\u0000\u0000LO\u0005\u0016"+
		"\u0000\u0000MN\u0005\u0019\u0000\u0000NP\u0006\u000b\uffff\uffff\u0000"+
		"OM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000"+
		"\u0000QT\u0005\u001b\u0000\u0000RS\u0005\u001b\u0000\u0000SU\u0006\u000b"+
		"\uffff\uffff\u0000TR\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000"+
		"UV\u0001\u0000\u0000\u0000VW\u0006\u000b\uffff\uffff\u0000W\u0017\u0001"+
		"\u0000\u0000\u0000XY\u0005\r\u0000\u0000YZ\u0005\u001f\u0000\u0000Z[\u0006"+
		"\f\uffff\uffff\u0000[\u0019\u0001\u0000\u0000\u0000\\]\u0003\u0000\u0000"+
		"\u0000]\u0084\u0006\r\uffff\uffff\u0000^_\u0003\u0002\u0001\u0000_`\u0006"+
		"\r\uffff\uffff\u0000`\u0083\u0001\u0000\u0000\u0000ab\u0003\u0004\u0002"+
		"\u0000bc\u0006\r\uffff\uffff\u0000c\u0083\u0001\u0000\u0000\u0000de\u0003"+
		"\u0006\u0003\u0000ef\u0006\r\uffff\uffff\u0000f\u0083\u0001\u0000\u0000"+
		"\u0000gh\u0003\b\u0004\u0000hi\u0006\r\uffff\uffff\u0000i\u0083\u0001"+
		"\u0000\u0000\u0000jk\u0003\n\u0005\u0000kl\u0006\r\uffff\uffff\u0000l"+
		"\u0083\u0001\u0000\u0000\u0000mn\u0003\f\u0006\u0000no\u0006\r\uffff\uffff"+
		"\u0000o\u0083\u0001\u0000\u0000\u0000pq\u0003\u000e\u0007\u0000qr\u0006"+
		"\r\uffff\uffff\u0000r\u0083\u0001\u0000\u0000\u0000st\u0003\u0010\b\u0000"+
		"tu\u0006\r\uffff\uffff\u0000u\u0083\u0001\u0000\u0000\u0000vw\u0003\u0012"+
		"\t\u0000wx\u0006\r\uffff\uffff\u0000x\u0083\u0001\u0000\u0000\u0000yz"+
		"\u0003\u0014\n\u0000z{\u0006\r\uffff\uffff\u0000{\u0083\u0001\u0000\u0000"+
		"\u0000|}\u0003\u0016\u000b\u0000}~\u0006\r\uffff\uffff\u0000~\u0083\u0001"+
		"\u0000\u0000\u0000\u007f\u0080\u0003\u0018\f\u0000\u0080\u0081\u0006\r"+
		"\uffff\uffff\u0000\u0081\u0083\u0001\u0000\u0000\u0000\u0082^\u0001\u0000"+
		"\u0000\u0000\u0082a\u0001\u0000\u0000\u0000\u0082d\u0001\u0000\u0000\u0000"+
		"\u0082g\u0001\u0000\u0000\u0000\u0082j\u0001\u0000\u0000\u0000\u0082m"+
		"\u0001\u0000\u0000\u0000\u0082p\u0001\u0000\u0000\u0000\u0082s\u0001\u0000"+
		"\u0000\u0000\u0082v\u0001\u0000\u0000\u0000\u0082y\u0001\u0000\u0000\u0000"+
		"\u0082|\u0001\u0000\u0000\u0000\u0082\u007f\u0001\u0000\u0000\u0000\u0083"+
		"\u0086\u0001\u0000\u0000\u0000\u0084\u0082\u0001\u0000\u0000\u0000\u0084"+
		"\u0085\u0001\u0000\u0000\u0000\u0085\u001b\u0001\u0000\u0000\u0000\u0086"+
		"\u0084\u0001\u0000\u0000\u0000\u0087\u0088\u0003\u001a\r\u0000\u0088\u0089"+
		"\u0006\u000e\uffff\uffff\u0000\u0089\u008b\u0001\u0000\u0000\u0000\u008a"+
		"\u0087\u0001\u0000\u0000\u0000\u008b\u008e\u0001\u0000\u0000\u0000\u008c"+
		"\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d"+
		"\u008f\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008f"+
		"\u0090\u0005\u0000\u0000\u0001\u0090\u001d\u0001\u0000\u0000\u0000\u0005"+
		"OT\u0082\u0084\u008c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}