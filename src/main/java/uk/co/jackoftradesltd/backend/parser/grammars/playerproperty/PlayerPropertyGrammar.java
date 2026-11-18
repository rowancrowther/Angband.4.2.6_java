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

// Generated from PlayerPropertyGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.playerproperty;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerPropertyGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TYPE=1, CODE=2, BINDUI=3, NAME=4, DESC=5, VALUE=6, INTEGER=7, TYPE_VALUE=8, 
		LCASE=9, TAG=10, COLON=11, COMMENT=12, INDENT=13, EOL=14, STRING=15, FLAG=16;
	public static final int
		RULE_type = 0, RULE_code = 1, RULE_binduiEntry = 2, RULE_bindUI = 3, RULE_name = 4, 
		RULE_desc = 5, RULE_value = 6, RULE_property = 7, RULE_file = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"type", "code", "binduiEntry", "bindUI", "name", "desc", "value", "property", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'type:'", "'code:'", "'bindui:'", "'name:'", "'desc:'", "'value:'", 
			null, null, null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TYPE", "CODE", "BINDUI", "NAME", "DESC", "VALUE", "INTEGER", "TYPE_VALUE", 
			"LCASE", "TAG", "COLON", "COMMENT", "INDENT", "EOL", "STRING", "FLAG"
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
	public String getGrammarFileName() { return "PlayerPropertyGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PlayerPropertyGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public String propertyType;
		public int lineNo;
		public Token TYPE_VALUE;
		public TerminalNode TYPE() { return getToken(PlayerPropertyGrammar.TYPE, 0); }
		public TerminalNode TYPE_VALUE() { return getToken(PlayerPropertyGrammar.TYPE_VALUE, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerPropertyGrammarVisitor ) return ((PlayerPropertyGrammarVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			match(TYPE);
			setState(19);
			((TypeContext)_localctx).TYPE_VALUE = match(TYPE_VALUE);

			                ((TypeContext)_localctx).propertyType =  ((TypeContext)_localctx).TYPE_VALUE.getText();
			                ((TypeContext)_localctx).lineNo =  _localctx.start.getLine();
			            
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
	public static class CodeContext extends ParserRuleContext {
		public String codeName;
		public Token FLAG;
		public TerminalNode CODE() { return getToken(PlayerPropertyGrammar.CODE, 0); }
		public TerminalNode FLAG() { return getToken(PlayerPropertyGrammar.FLAG, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerPropertyGrammarVisitor ) return ((PlayerPropertyGrammarVisitor<? extends T>)visitor).visitCode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeContext code() throws RecognitionException {
		CodeContext _localctx = new CodeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_code);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			match(CODE);
			setState(23);
			((CodeContext)_localctx).FLAG = match(FLAG);
			 ((CodeContext)_localctx).codeName =  ((CodeContext)_localctx).FLAG.getText(); 
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
	public static class BinduiEntryContext extends ParserRuleContext {
		public List<String> entry;
		public Token LCASE;
		public Token TAG;
		public Token aux;
		public Token val;
		public TerminalNode BINDUI() { return getToken(PlayerPropertyGrammar.BINDUI, 0); }
		public List<TerminalNode> LCASE() { return getTokens(PlayerPropertyGrammar.LCASE); }
		public TerminalNode LCASE(int i) {
			return getToken(PlayerPropertyGrammar.LCASE, i);
		}
		public List<TerminalNode> COLON() { return getTokens(PlayerPropertyGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(PlayerPropertyGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(PlayerPropertyGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(PlayerPropertyGrammar.INTEGER, i);
		}
		public TerminalNode TAG() { return getToken(PlayerPropertyGrammar.TAG, 0); }
		public BinduiEntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binduiEntry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).enterBinduiEntry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).exitBinduiEntry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerPropertyGrammarVisitor ) return ((PlayerPropertyGrammarVisitor<? extends T>)visitor).visitBinduiEntry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BinduiEntryContext binduiEntry() throws RecognitionException {
		BinduiEntryContext _localctx = new BinduiEntryContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_binduiEntry);

		            String tagText = "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			match(BINDUI);
			setState(27);
			((BinduiEntryContext)_localctx).LCASE = match(LCASE);
			setState(30);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TAG) {
				{
				setState(28);
				((BinduiEntryContext)_localctx).TAG = match(TAG);
				 tagText = ((BinduiEntryContext)_localctx).TAG.getText(); 
				}
			}

			setState(32);
			match(COLON);
			setState(33);
			((BinduiEntryContext)_localctx).aux = match(INTEGER);
			setState(34);
			match(COLON);
			setState(35);
			((BinduiEntryContext)_localctx).val = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==INTEGER || _la==LCASE) ) {
				((BinduiEntryContext)_localctx).val = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}

			                ((BinduiEntryContext)_localctx).entry =  List.of(((BinduiEntryContext)_localctx).LCASE.getText(), tagText, ((BinduiEntryContext)_localctx).aux.getText(),
			                    ((BinduiEntryContext)_localctx).val.getText());
			            
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
	public static class BindUIContext extends ParserRuleContext {
		public List<List<String>> bindUIs;
		public BinduiEntryContext binduiEntry;
		public BinduiEntryContext binduiEntry() {
			return getRuleContext(BinduiEntryContext.class,0);
		}
		public BindUIContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindUI; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).enterBindUI(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).exitBindUI(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerPropertyGrammarVisitor ) return ((PlayerPropertyGrammarVisitor<? extends T>)visitor).visitBindUI(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BindUIContext bindUI() throws RecognitionException {
		BindUIContext _localctx = new BindUIContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_bindUI);

		            ((BindUIContext)_localctx).bindUIs =  new ArrayList<>();
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			((BindUIContext)_localctx).binduiEntry = binduiEntry();
			 _localctx.bindUIs.add(((BindUIContext)_localctx).binduiEntry.entry); 
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
		public TerminalNode NAME() { return getToken(PlayerPropertyGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(PlayerPropertyGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerPropertyGrammarVisitor ) return ((PlayerPropertyGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(NAME);
			setState(42);
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
	public static class DescContext extends ParserRuleContext {
		public String descStr;
		public Token STRING;
		public TerminalNode DESC() { return getToken(PlayerPropertyGrammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(PlayerPropertyGrammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerPropertyGrammarVisitor ) return ((PlayerPropertyGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(DESC);
			setState(46);
			((DescContext)_localctx).STRING = match(STRING);
			 ((DescContext)_localctx).descStr =  ((DescContext)_localctx).STRING.getText(); 
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
	public static class ValueContext extends ParserRuleContext {
		public String valueInt;
		public Token INTEGER;
		public TerminalNode VALUE() { return getToken(PlayerPropertyGrammar.VALUE, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerPropertyGrammar.INTEGER, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerPropertyGrammarVisitor ) return ((PlayerPropertyGrammarVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_value);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			match(VALUE);
			setState(50);
			((ValueContext)_localctx).INTEGER = match(INTEGER);

			            ((ValueContext)_localctx).valueInt =  ((ValueContext)_localctx).INTEGER.getText();
			        
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
	public static class PropertyContext extends ParserRuleContext {
		public PlayerPropertyParseRecord playerProp;
		public TypeContext type;
		public CodeContext code;
		public BindUIContext bindUI;
		public NameContext name;
		public DescContext desc;
		public ValueContext value;
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<CodeContext> code() {
			return getRuleContexts(CodeContext.class);
		}
		public CodeContext code(int i) {
			return getRuleContext(CodeContext.class,i);
		}
		public List<BindUIContext> bindUI() {
			return getRuleContexts(BindUIContext.class);
		}
		public BindUIContext bindUI(int i) {
			return getRuleContext(BindUIContext.class,i);
		}
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public PropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_property; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).enterProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).exitProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerPropertyGrammarVisitor ) return ((PlayerPropertyGrammarVisitor<? extends T>)visitor).visitProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PropertyContext property() throws RecognitionException {
		PropertyContext _localctx = new PropertyContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_property);

		            String typeInit = "";
		            String codeInit = "";
		            List<List<String>> bindUIInit = new ArrayList<>();
		            String nameInit = "";
		            String descInit = "";
		            String valueInit = "";
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			((PropertyContext)_localctx).type = type();

			                line = ((PropertyContext)_localctx).type.lineNo;
			                typeInit = ((PropertyContext)_localctx).type.propertyType;
			            
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 124L) != 0)) {
				{
				setState(70);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CODE:
					{
					setState(55);
					((PropertyContext)_localctx).code = code();

					                codeInit = ((PropertyContext)_localctx).code.codeName;
					            
					}
					break;
				case BINDUI:
					{
					setState(58);
					((PropertyContext)_localctx).bindUI = bindUI();

					                bindUIInit.addAll(((PropertyContext)_localctx).bindUI.bindUIs);
					            
					}
					break;
				case NAME:
					{
					setState(61);
					((PropertyContext)_localctx).name = name();
					 nameInit = ((PropertyContext)_localctx).name.nameStr; 
					}
					break;
				case DESC:
					{
					setState(64);
					((PropertyContext)_localctx).desc = desc();
					 descInit = descInit + ((PropertyContext)_localctx).desc.descStr; 
					}
					break;
				case VALUE:
					{
					setState(67);
					((PropertyContext)_localctx).value = value();
					 valueInit = ((PropertyContext)_localctx).value.valueInt; 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(74);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((PropertyContext)_localctx).playerProp =  new PlayerPropertyParseRecord(typeInit, codeInit,
			            bindUIInit, nameInit, descInit, valueInit, line);
			        
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
		public List<PlayerPropertyParseRecord> properties;
		public PropertyContext property;
		public TerminalNode EOF() { return getToken(PlayerPropertyGrammar.EOF, 0); }
		public List<PropertyContext> property() {
			return getRuleContexts(PropertyContext.class);
		}
		public PropertyContext property(int i) {
			return getRuleContext(PropertyContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerPropertyGrammarListener ) ((PlayerPropertyGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerPropertyGrammarVisitor ) return ((PlayerPropertyGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_file);

		            ((FileContext)_localctx).properties =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TYPE) {
				{
				{
				setState(75);
				((FileContext)_localctx).property = property();

				                _localctx.properties.add(((FileContext)_localctx).property.playerProp);
				            
				}
				}
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(83);
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
		"\u0004\u0001\u0010V\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0003\u0002\u001f\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007"+
		"G\b\u0007\n\u0007\f\u0007J\t\u0007\u0001\b\u0001\b\u0001\b\u0005\bO\b"+
		"\b\n\b\f\bR\t\b\u0001\b\u0001\b\u0001\b\u0000\u0000\t\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0000\u0001\u0002\u0000\u0007\u0007\t\tS\u0000"+
		"\u0012\u0001\u0000\u0000\u0000\u0002\u0016\u0001\u0000\u0000\u0000\u0004"+
		"\u001a\u0001\u0000\u0000\u0000\u0006&\u0001\u0000\u0000\u0000\b)\u0001"+
		"\u0000\u0000\u0000\n-\u0001\u0000\u0000\u0000\f1\u0001\u0000\u0000\u0000"+
		"\u000e5\u0001\u0000\u0000\u0000\u0010P\u0001\u0000\u0000\u0000\u0012\u0013"+
		"\u0005\u0001\u0000\u0000\u0013\u0014\u0005\b\u0000\u0000\u0014\u0015\u0006"+
		"\u0000\uffff\uffff\u0000\u0015\u0001\u0001\u0000\u0000\u0000\u0016\u0017"+
		"\u0005\u0002\u0000\u0000\u0017\u0018\u0005\u0010\u0000\u0000\u0018\u0019"+
		"\u0006\u0001\uffff\uffff\u0000\u0019\u0003\u0001\u0000\u0000\u0000\u001a"+
		"\u001b\u0005\u0003\u0000\u0000\u001b\u001e\u0005\t\u0000\u0000\u001c\u001d"+
		"\u0005\n\u0000\u0000\u001d\u001f\u0006\u0002\uffff\uffff\u0000\u001e\u001c"+
		"\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f \u0001"+
		"\u0000\u0000\u0000 !\u0005\u000b\u0000\u0000!\"\u0005\u0007\u0000\u0000"+
		"\"#\u0005\u000b\u0000\u0000#$\u0007\u0000\u0000\u0000$%\u0006\u0002\uffff"+
		"\uffff\u0000%\u0005\u0001\u0000\u0000\u0000&\'\u0003\u0004\u0002\u0000"+
		"\'(\u0006\u0003\uffff\uffff\u0000(\u0007\u0001\u0000\u0000\u0000)*\u0005"+
		"\u0004\u0000\u0000*+\u0005\u000f\u0000\u0000+,\u0006\u0004\uffff\uffff"+
		"\u0000,\t\u0001\u0000\u0000\u0000-.\u0005\u0005\u0000\u0000./\u0005\u000f"+
		"\u0000\u0000/0\u0006\u0005\uffff\uffff\u00000\u000b\u0001\u0000\u0000"+
		"\u000012\u0005\u0006\u0000\u000023\u0005\u0007\u0000\u000034\u0006\u0006"+
		"\uffff\uffff\u00004\r\u0001\u0000\u0000\u000056\u0003\u0000\u0000\u0000"+
		"6H\u0006\u0007\uffff\uffff\u000078\u0003\u0002\u0001\u000089\u0006\u0007"+
		"\uffff\uffff\u00009G\u0001\u0000\u0000\u0000:;\u0003\u0006\u0003\u0000"+
		";<\u0006\u0007\uffff\uffff\u0000<G\u0001\u0000\u0000\u0000=>\u0003\b\u0004"+
		"\u0000>?\u0006\u0007\uffff\uffff\u0000?G\u0001\u0000\u0000\u0000@A\u0003"+
		"\n\u0005\u0000AB\u0006\u0007\uffff\uffff\u0000BG\u0001\u0000\u0000\u0000"+
		"CD\u0003\f\u0006\u0000DE\u0006\u0007\uffff\uffff\u0000EG\u0001\u0000\u0000"+
		"\u0000F7\u0001\u0000\u0000\u0000F:\u0001\u0000\u0000\u0000F=\u0001\u0000"+
		"\u0000\u0000F@\u0001\u0000\u0000\u0000FC\u0001\u0000\u0000\u0000GJ\u0001"+
		"\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000"+
		"I\u000f\u0001\u0000\u0000\u0000JH\u0001\u0000\u0000\u0000KL\u0003\u000e"+
		"\u0007\u0000LM\u0006\b\uffff\uffff\u0000MO\u0001\u0000\u0000\u0000NK\u0001"+
		"\u0000\u0000\u0000OR\u0001\u0000\u0000\u0000PN\u0001\u0000\u0000\u0000"+
		"PQ\u0001\u0000\u0000\u0000QS\u0001\u0000\u0000\u0000RP\u0001\u0000\u0000"+
		"\u0000ST\u0005\u0000\u0000\u0001T\u0011\u0001\u0000\u0000\u0000\u0004"+
		"\u001eFHP";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}