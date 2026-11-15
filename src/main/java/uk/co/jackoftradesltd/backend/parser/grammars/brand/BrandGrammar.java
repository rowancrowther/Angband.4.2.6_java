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
// Generated from BrandGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.brand;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.brand.BrandParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BrandGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CODE=1, NAME=2, MULTIPLIER=3, O_MULTIPLIER=4, POWER=5, VERB=6, RESIST_FLAG=7, 
		VULN_FLAG=8, UINT=9, COMMENT=10, INDENT=11, EOL=12, FLAG=13, STRING=14;
	public static final int
		RULE_code = 0, RULE_name = 1, RULE_multiplier = 2, RULE_oMultiplier = 3, 
		RULE_power = 4, RULE_verb = 5, RULE_resistFlag = 6, RULE_vulnFlag = 7, 
		RULE_brand = 8, RULE_file = 9;
	private static String[] makeRuleNames() {
		return new String[] {
			"code", "name", "multiplier", "oMultiplier", "power", "verb", "resistFlag", 
			"vulnFlag", "brand", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'code:'", "'name:'", "'multiplier:'", "'o-multiplier:'", "'power:'", 
			"'verb:'", "'resist-flag:'", "'vuln-flag:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CODE", "NAME", "MULTIPLIER", "O_MULTIPLIER", "POWER", "VERB", 
			"RESIST_FLAG", "VULN_FLAG", "UINT", "COMMENT", "INDENT", "EOL", "FLAG", 
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
	public String getGrammarFileName() { return "BrandGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public BrandGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CodeContext extends ParserRuleContext {
		public String codeStr;
		public int lineNo;
		public Token STRING;
		public TerminalNode CODE() { return getToken(BrandGrammar.CODE, 0); }
		public TerminalNode STRING() { return getToken(BrandGrammar.STRING, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitCode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeContext code() throws RecognitionException {
		CodeContext _localctx = new CodeContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_code);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			match(CODE);
			setState(21);
			((CodeContext)_localctx).STRING = match(STRING);
			 ((CodeContext)_localctx).codeStr =  ((CodeContext)_localctx).STRING.getText();
			                          ((CodeContext)_localctx).lineNo =  _localctx.start.getLine(); 
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
		public String nameStr;
		public Token STRING;
		public TerminalNode NAME() { return getToken(BrandGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(BrandGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(NAME);
			setState(25);
			((NameContext)_localctx).STRING = match(STRING);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).STRING.getText(); 
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
	public static class MultiplierContext extends ParserRuleContext {
		public String mult;
		public Token UINT;
		public TerminalNode MULTIPLIER() { return getToken(BrandGrammar.MULTIPLIER, 0); }
		public TerminalNode UINT() { return getToken(BrandGrammar.UINT, 0); }
		public MultiplierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterMultiplier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitMultiplier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitMultiplier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplierContext multiplier() throws RecognitionException {
		MultiplierContext _localctx = new MultiplierContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_multiplier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(MULTIPLIER);
			setState(29);
			((MultiplierContext)_localctx).UINT = match(UINT);
			 ((MultiplierContext)_localctx).mult =  ((MultiplierContext)_localctx).UINT.getText(); 
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
	public static class OMultiplierContext extends ParserRuleContext {
		public String oMult;
		public Token UINT;
		public TerminalNode O_MULTIPLIER() { return getToken(BrandGrammar.O_MULTIPLIER, 0); }
		public TerminalNode UINT() { return getToken(BrandGrammar.UINT, 0); }
		public OMultiplierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oMultiplier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterOMultiplier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitOMultiplier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitOMultiplier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OMultiplierContext oMultiplier() throws RecognitionException {
		OMultiplierContext _localctx = new OMultiplierContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_oMultiplier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(O_MULTIPLIER);
			setState(33);
			((OMultiplierContext)_localctx).UINT = match(UINT);
			 ((OMultiplierContext)_localctx).oMult =  ((OMultiplierContext)_localctx).UINT.getText(); 
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
		public String powerStr;
		public Token UINT;
		public TerminalNode POWER() { return getToken(BrandGrammar.POWER, 0); }
		public TerminalNode UINT() { return getToken(BrandGrammar.UINT, 0); }
		public PowerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_power; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitPower(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PowerContext power() throws RecognitionException {
		PowerContext _localctx = new PowerContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_power);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(POWER);
			setState(37);
			((PowerContext)_localctx).UINT = match(UINT);
			 ((PowerContext)_localctx).powerStr =  ((PowerContext)_localctx).UINT.getText(); 
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
	public static class VerbContext extends ParserRuleContext {
		public String verbStr;
		public Token STRING;
		public TerminalNode VERB() { return getToken(BrandGrammar.VERB, 0); }
		public TerminalNode STRING() { return getToken(BrandGrammar.STRING, 0); }
		public VerbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_verb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterVerb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitVerb(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitVerb(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VerbContext verb() throws RecognitionException {
		VerbContext _localctx = new VerbContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_verb);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(VERB);
			setState(41);
			((VerbContext)_localctx).STRING = match(STRING);
			 ((VerbContext)_localctx).verbStr =  ((VerbContext)_localctx).STRING.getText(); 
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
	public static class ResistFlagContext extends ParserRuleContext {
		public String rFlag;
		public Token FLAG;
		public TerminalNode RESIST_FLAG() { return getToken(BrandGrammar.RESIST_FLAG, 0); }
		public TerminalNode FLAG() { return getToken(BrandGrammar.FLAG, 0); }
		public ResistFlagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resistFlag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterResistFlag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitResistFlag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitResistFlag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResistFlagContext resistFlag() throws RecognitionException {
		ResistFlagContext _localctx = new ResistFlagContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_resistFlag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(RESIST_FLAG);
			setState(45);
			((ResistFlagContext)_localctx).FLAG = match(FLAG);
			 ((ResistFlagContext)_localctx).rFlag =  ((ResistFlagContext)_localctx).FLAG.getText(); 
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
	public static class VulnFlagContext extends ParserRuleContext {
		public String vFlag;
		public Token FLAG;
		public TerminalNode VULN_FLAG() { return getToken(BrandGrammar.VULN_FLAG, 0); }
		public TerminalNode FLAG() { return getToken(BrandGrammar.FLAG, 0); }
		public VulnFlagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vulnFlag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterVulnFlag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitVulnFlag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitVulnFlag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VulnFlagContext vulnFlag() throws RecognitionException {
		VulnFlagContext _localctx = new VulnFlagContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_vulnFlag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(VULN_FLAG);
			setState(49);
			((VulnFlagContext)_localctx).FLAG = match(FLAG);
			 ((VulnFlagContext)_localctx).vFlag =  ((VulnFlagContext)_localctx).FLAG.getText(); 
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
	public static class BrandContext extends ParserRuleContext {
		public BrandParseRecord record;
		public CodeContext code;
		public NameContext name;
		public VerbContext verb;
		public MultiplierContext multiplier;
		public OMultiplierContext oMultiplier;
		public PowerContext power;
		public ResistFlagContext resistFlag;
		public VulnFlagContext vulnFlag;
		public CodeContext code() {
			return getRuleContext(CodeContext.class,0);
		}
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public List<VerbContext> verb() {
			return getRuleContexts(VerbContext.class);
		}
		public VerbContext verb(int i) {
			return getRuleContext(VerbContext.class,i);
		}
		public List<MultiplierContext> multiplier() {
			return getRuleContexts(MultiplierContext.class);
		}
		public MultiplierContext multiplier(int i) {
			return getRuleContext(MultiplierContext.class,i);
		}
		public List<OMultiplierContext> oMultiplier() {
			return getRuleContexts(OMultiplierContext.class);
		}
		public OMultiplierContext oMultiplier(int i) {
			return getRuleContext(OMultiplierContext.class,i);
		}
		public List<PowerContext> power() {
			return getRuleContexts(PowerContext.class);
		}
		public PowerContext power(int i) {
			return getRuleContext(PowerContext.class,i);
		}
		public List<ResistFlagContext> resistFlag() {
			return getRuleContexts(ResistFlagContext.class);
		}
		public ResistFlagContext resistFlag(int i) {
			return getRuleContext(ResistFlagContext.class,i);
		}
		public List<VulnFlagContext> vulnFlag() {
			return getRuleContexts(VulnFlagContext.class);
		}
		public VulnFlagContext vulnFlag(int i) {
			return getRuleContext(VulnFlagContext.class,i);
		}
		public BrandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterBrand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitBrand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitBrand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrandContext brand() throws RecognitionException {
		BrandContext _localctx = new BrandContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_brand);

		            int lineInit = 0;
		            String codeInit = "";
		            String nameInit = null;
		            List<String> multiplierInit = new ArrayList<>();
		            List<String> oMultiplierInit = new ArrayList<>();
		            List<String> powerInit = new ArrayList<>();
		            String verbInit = null;
		            List<String> resistFlagInit = new ArrayList<>();
		            List<String> vulnFlagInit = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			((BrandContext)_localctx).code = code();
			 lineInit = ((BrandContext)_localctx).code.lineNo;
			                   codeInit = ((BrandContext)_localctx).code.codeStr; 
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 508L) != 0)) {
				{
				setState(75);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NAME:
					{
					setState(54);
					((BrandContext)_localctx).name = name();
					 nameInit = ((BrandContext)_localctx).name.nameStr; 
					}
					break;
				case VERB:
					{
					setState(57);
					((BrandContext)_localctx).verb = verb();
					 verbInit = ((BrandContext)_localctx).verb.verbStr; 
					}
					break;
				case MULTIPLIER:
					{
					setState(60);
					((BrandContext)_localctx).multiplier = multiplier();
					 multiplierInit.add(((BrandContext)_localctx).multiplier.mult); 
					}
					break;
				case O_MULTIPLIER:
					{
					setState(63);
					((BrandContext)_localctx).oMultiplier = oMultiplier();
					 oMultiplierInit.add(((BrandContext)_localctx).oMultiplier.oMult); 
					}
					break;
				case POWER:
					{
					setState(66);
					((BrandContext)_localctx).power = power();
					 powerInit.add(((BrandContext)_localctx).power.powerStr); 
					}
					break;
				case RESIST_FLAG:
					{
					setState(69);
					((BrandContext)_localctx).resistFlag = resistFlag();
					 resistFlagInit.add(((BrandContext)_localctx).resistFlag.rFlag); 
					}
					break;
				case VULN_FLAG:
					{
					setState(72);
					((BrandContext)_localctx).vulnFlag = vulnFlag();
					 vulnFlagInit.add(((BrandContext)_localctx).vulnFlag.vFlag); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((BrandContext)_localctx).record =  new BrandParseRecord(codeInit, nameInit, multiplierInit,
			                    oMultiplierInit, powerInit, verbInit, resistFlagInit,
			                    vulnFlagInit, lineInit);
			        
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
		public List<BrandParseRecord> records;
		public BrandContext brand;
		public TerminalNode EOF() { return getToken(BrandGrammar.EOF, 0); }
		public List<BrandContext> brand() {
			return getRuleContexts(BrandContext.class);
		}
		public BrandContext brand(int i) {
			return getRuleContext(BrandContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BrandGrammarListener ) ((BrandGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BrandGrammarVisitor ) return ((BrandGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_file);

		            ((FileContext)_localctx).records =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CODE) {
				{
				{
				setState(80);
				((FileContext)_localctx).brand = brand();
				 _localctx.records.add(((FileContext)_localctx).brand.record); 
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(88);
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
		"\u0004\u0001\u000e[\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0005\bL\b\b\n\b\f\bO\t\b\u0001\t\u0001\t\u0001\t\u0005"+
		"\tT\b\t\n\t\f\tW\t\t\u0001\t\u0001\t\u0001\t\u0000\u0000\n\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0012\u0000\u0000X\u0000\u0014\u0001\u0000"+
		"\u0000\u0000\u0002\u0018\u0001\u0000\u0000\u0000\u0004\u001c\u0001\u0000"+
		"\u0000\u0000\u0006 \u0001\u0000\u0000\u0000\b$\u0001\u0000\u0000\u0000"+
		"\n(\u0001\u0000\u0000\u0000\f,\u0001\u0000\u0000\u0000\u000e0\u0001\u0000"+
		"\u0000\u0000\u00104\u0001\u0000\u0000\u0000\u0012U\u0001\u0000\u0000\u0000"+
		"\u0014\u0015\u0005\u0001\u0000\u0000\u0015\u0016\u0005\u000e\u0000\u0000"+
		"\u0016\u0017\u0006\u0000\uffff\uffff\u0000\u0017\u0001\u0001\u0000\u0000"+
		"\u0000\u0018\u0019\u0005\u0002\u0000\u0000\u0019\u001a\u0005\u000e\u0000"+
		"\u0000\u001a\u001b\u0006\u0001\uffff\uffff\u0000\u001b\u0003\u0001\u0000"+
		"\u0000\u0000\u001c\u001d\u0005\u0003\u0000\u0000\u001d\u001e\u0005\t\u0000"+
		"\u0000\u001e\u001f\u0006\u0002\uffff\uffff\u0000\u001f\u0005\u0001\u0000"+
		"\u0000\u0000 !\u0005\u0004\u0000\u0000!\"\u0005\t\u0000\u0000\"#\u0006"+
		"\u0003\uffff\uffff\u0000#\u0007\u0001\u0000\u0000\u0000$%\u0005\u0005"+
		"\u0000\u0000%&\u0005\t\u0000\u0000&\'\u0006\u0004\uffff\uffff\u0000\'"+
		"\t\u0001\u0000\u0000\u0000()\u0005\u0006\u0000\u0000)*\u0005\u000e\u0000"+
		"\u0000*+\u0006\u0005\uffff\uffff\u0000+\u000b\u0001\u0000\u0000\u0000"+
		",-\u0005\u0007\u0000\u0000-.\u0005\r\u0000\u0000./\u0006\u0006\uffff\uffff"+
		"\u0000/\r\u0001\u0000\u0000\u000001\u0005\b\u0000\u000012\u0005\r\u0000"+
		"\u000023\u0006\u0007\uffff\uffff\u00003\u000f\u0001\u0000\u0000\u0000"+
		"45\u0003\u0000\u0000\u00005M\u0006\b\uffff\uffff\u000067\u0003\u0002\u0001"+
		"\u000078\u0006\b\uffff\uffff\u00008L\u0001\u0000\u0000\u00009:\u0003\n"+
		"\u0005\u0000:;\u0006\b\uffff\uffff\u0000;L\u0001\u0000\u0000\u0000<=\u0003"+
		"\u0004\u0002\u0000=>\u0006\b\uffff\uffff\u0000>L\u0001\u0000\u0000\u0000"+
		"?@\u0003\u0006\u0003\u0000@A\u0006\b\uffff\uffff\u0000AL\u0001\u0000\u0000"+
		"\u0000BC\u0003\b\u0004\u0000CD\u0006\b\uffff\uffff\u0000DL\u0001\u0000"+
		"\u0000\u0000EF\u0003\f\u0006\u0000FG\u0006\b\uffff\uffff\u0000GL\u0001"+
		"\u0000\u0000\u0000HI\u0003\u000e\u0007\u0000IJ\u0006\b\uffff\uffff\u0000"+
		"JL\u0001\u0000\u0000\u0000K6\u0001\u0000\u0000\u0000K9\u0001\u0000\u0000"+
		"\u0000K<\u0001\u0000\u0000\u0000K?\u0001\u0000\u0000\u0000KB\u0001\u0000"+
		"\u0000\u0000KE\u0001\u0000\u0000\u0000KH\u0001\u0000\u0000\u0000LO\u0001"+
		"\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000"+
		"N\u0011\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000PQ\u0003\u0010"+
		"\b\u0000QR\u0006\t\uffff\uffff\u0000RT\u0001\u0000\u0000\u0000SP\u0001"+
		"\u0000\u0000\u0000TW\u0001\u0000\u0000\u0000US\u0001\u0000\u0000\u0000"+
		"UV\u0001\u0000\u0000\u0000VX\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000"+
		"\u0000XY\u0005\u0000\u0000\u0001Y\u0013\u0001\u0000\u0000\u0000\u0003"+
		"KMU";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}