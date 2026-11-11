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

// Generated from ObjectBaseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.objectbase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.objectbase.ObjectBaseParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectBaseGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		DEFAULT_MAX_STACK=1, DEFAULT_BREAK_CHANCE=2, NAME=3, GRAPHICS=4, BREAK=5, 
		MAX_STACK=6, FLAGS=7, INTEGER=8, COMMENT=9, INDENT=10, EOL=11, T_VAL=12, 
		T_VAL_COLON=13, NAME_EOL=14, STRING=15, STRING_EOL=16, FLAG=17, FLAG_OR=18, 
		FLAG_EOL=19;
	public static final int
		RULE_default_value = 0, RULE_name = 1, RULE_graphics = 2, RULE_break_chance = 3, 
		RULE_max_stack = 4, RULE_flags = 5, RULE_object_base = 6, RULE_file = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"default_value", "name", "graphics", "break_chance", "max_stack", "flags", 
			"object_base", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'default:max-stack:'", "'default:break-chance:'", "'name:'", "'graphics:'", 
			"'break:'", "'max-stack:'", "'flags:'", null, null, null, null, null, 
			"':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "DEFAULT_MAX_STACK", "DEFAULT_BREAK_CHANCE", "NAME", "GRAPHICS", 
			"BREAK", "MAX_STACK", "FLAGS", "INTEGER", "COMMENT", "INDENT", "EOL", 
			"T_VAL", "T_VAL_COLON", "NAME_EOL", "STRING", "STRING_EOL", "FLAG", "FLAG_OR", 
			"FLAG_EOL"
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
	public String getGrammarFileName() { return "ObjectBaseGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ObjectBaseGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Default_valueContext extends ParserRuleContext {
		public String maxStackNum;
		public String breakChanceNum;
		public Token val1;
		public Token val2;
		public TerminalNode DEFAULT_BREAK_CHANCE() { return getToken(ObjectBaseGrammar.DEFAULT_BREAK_CHANCE, 0); }
		public TerminalNode INTEGER() { return getToken(ObjectBaseGrammar.INTEGER, 0); }
		public TerminalNode DEFAULT_MAX_STACK() { return getToken(ObjectBaseGrammar.DEFAULT_MAX_STACK, 0); }
		public Default_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_default_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).enterDefault_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).exitDefault_value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectBaseGrammarVisitor ) return ((ObjectBaseGrammarVisitor<? extends T>)visitor).visitDefault_value(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Default_valueContext default_value() throws RecognitionException {
		Default_valueContext _localctx = new Default_valueContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_default_value);

		            ((Default_valueContext)_localctx).maxStackNum =  "";
		            ((Default_valueContext)_localctx).breakChanceNum =  "";
		        
		try {
			setState(22);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DEFAULT_BREAK_CHANCE:
				enterOuterAlt(_localctx, 1);
				{
				setState(16);
				match(DEFAULT_BREAK_CHANCE);
				setState(17);
				((Default_valueContext)_localctx).val1 = match(INTEGER);

				                ((Default_valueContext)_localctx).breakChanceNum =  ((Default_valueContext)_localctx).val1.getText();
				            
				}
				break;
			case DEFAULT_MAX_STACK:
				enterOuterAlt(_localctx, 2);
				{
				setState(19);
				match(DEFAULT_MAX_STACK);
				setState(20);
				((Default_valueContext)_localctx).val2 = match(INTEGER);

				                ((Default_valueContext)_localctx).maxStackNum =  ((Default_valueContext)_localctx).val2.getText();
				            
				}
				break;
			default:
				throw new NoViableAltException(this);
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
		public String tValue;
		public String nameStr;
		public int lineNo;
		public Token tval1;
		public Token nameIn;
		public Token tval2;
		public TerminalNode NAME() { return getToken(ObjectBaseGrammar.NAME, 0); }
		public TerminalNode T_VAL_COLON() { return getToken(ObjectBaseGrammar.T_VAL_COLON, 0); }
		public TerminalNode T_VAL() { return getToken(ObjectBaseGrammar.T_VAL, 0); }
		public TerminalNode STRING() { return getToken(ObjectBaseGrammar.STRING, 0); }
		public TerminalNode NAME_EOL() { return getToken(ObjectBaseGrammar.NAME_EOL, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectBaseGrammarVisitor ) return ((ObjectBaseGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			setState(35);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(24);
				match(NAME);
				setState(25);
				((NameContext)_localctx).tval1 = match(T_VAL);
				setState(26);
				match(T_VAL_COLON);
				setState(27);
				((NameContext)_localctx).nameIn = match(STRING);
				}

				                ((NameContext)_localctx).tValue =  ((NameContext)_localctx).tval1.getText();
				                ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).nameIn.getText();
				                ((NameContext)_localctx).lineNo =  _localctx.start.getLine();
				            
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(30);
				match(NAME);
				setState(31);
				((NameContext)_localctx).tval2 = match(T_VAL);
				setState(32);
				match(NAME_EOL);
				}

				                ((NameContext)_localctx).tValue =  ((NameContext)_localctx).tval2.getText();
				                ((NameContext)_localctx).nameStr =  "";
				                ((NameContext)_localctx).lineNo =  _localctx.start.getLine();
				            
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

	@SuppressWarnings("CheckReturnValue")
	public static class GraphicsContext extends ParserRuleContext {
		public String colour;
		public Token STRING;
		public TerminalNode GRAPHICS() { return getToken(ObjectBaseGrammar.GRAPHICS, 0); }
		public TerminalNode STRING() { return getToken(ObjectBaseGrammar.STRING, 0); }
		public GraphicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).enterGraphics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).exitGraphics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectBaseGrammarVisitor ) return ((ObjectBaseGrammarVisitor<? extends T>)visitor).visitGraphics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GraphicsContext graphics() throws RecognitionException {
		GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_graphics);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			match(GRAPHICS);
			setState(38);
			((GraphicsContext)_localctx).STRING = match(STRING);
			 ((GraphicsContext)_localctx).colour =  ((GraphicsContext)_localctx).STRING.getText(); 
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
	public static class Break_chanceContext extends ParserRuleContext {
		public String breakChance;
		public Token INTEGER;
		public TerminalNode BREAK() { return getToken(ObjectBaseGrammar.BREAK, 0); }
		public TerminalNode INTEGER() { return getToken(ObjectBaseGrammar.INTEGER, 0); }
		public Break_chanceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_break_chance; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).enterBreak_chance(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).exitBreak_chance(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectBaseGrammarVisitor ) return ((ObjectBaseGrammarVisitor<? extends T>)visitor).visitBreak_chance(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Break_chanceContext break_chance() throws RecognitionException {
		Break_chanceContext _localctx = new Break_chanceContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_break_chance);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(BREAK);
			setState(42);
			((Break_chanceContext)_localctx).INTEGER = match(INTEGER);

			                ((Break_chanceContext)_localctx).breakChance =  ((Break_chanceContext)_localctx).INTEGER.getText();
			            
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
	public static class Max_stackContext extends ParserRuleContext {
		public String maxStack;
		public Token INTEGER;
		public TerminalNode MAX_STACK() { return getToken(ObjectBaseGrammar.MAX_STACK, 0); }
		public TerminalNode INTEGER() { return getToken(ObjectBaseGrammar.INTEGER, 0); }
		public Max_stackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_max_stack; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).enterMax_stack(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).exitMax_stack(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectBaseGrammarVisitor ) return ((ObjectBaseGrammarVisitor<? extends T>)visitor).visitMax_stack(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Max_stackContext max_stack() throws RecognitionException {
		Max_stackContext _localctx = new Max_stackContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_max_stack);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(MAX_STACK);
			setState(46);
			((Max_stackContext)_localctx).INTEGER = match(INTEGER);

			                ((Max_stackContext)_localctx).maxStack =  ((Max_stackContext)_localctx).INTEGER.getText();
			            
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
		public List<String> flagsList;
		public Token flag1;
		public Token flag2;
		public TerminalNode FLAGS() { return getToken(ObjectBaseGrammar.FLAGS, 0); }
		public TerminalNode FLAG_EOL() { return getToken(ObjectBaseGrammar.FLAG_EOL, 0); }
		public List<TerminalNode> FLAG() { return getTokens(ObjectBaseGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(ObjectBaseGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(ObjectBaseGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(ObjectBaseGrammar.FLAG_OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectBaseGrammarVisitor ) return ((ObjectBaseGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_flags);

		            ((FlagsContext)_localctx).flagsList =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			match(FLAGS);
			setState(50);
			((FlagsContext)_localctx).flag1 = match(FLAG);

			                _localctx.flagsList.add(((FlagsContext)_localctx).flag1.getText());
			            
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(52);
				match(FLAG_OR);
				setState(53);
				((FlagsContext)_localctx).flag2 = match(FLAG);

				                _localctx.flagsList.add(((FlagsContext)_localctx).flag2.getText());
				            
				}
				}
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(60);
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
	public static class Object_baseContext extends ParserRuleContext {
		public ObjectBaseParseRecord objectBase;
		public NameContext name;
		public GraphicsContext graphics;
		public Break_chanceContext break_chance;
		public Max_stackContext max_stack;
		public FlagsContext flags;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public GraphicsContext graphics() {
			return getRuleContext(GraphicsContext.class,0);
		}
		public Break_chanceContext break_chance() {
			return getRuleContext(Break_chanceContext.class,0);
		}
		public Max_stackContext max_stack() {
			return getRuleContext(Max_stackContext.class,0);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class,i);
		}
		public Object_baseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_object_base; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).enterObject_base(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).exitObject_base(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectBaseGrammarVisitor ) return ((ObjectBaseGrammarVisitor<? extends T>)visitor).visitObject_base(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Object_baseContext object_base() throws RecognitionException {
		Object_baseContext _localctx = new Object_baseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_object_base);

		            String name = "";
		            String tVal = "";
		            String colour = "";
		            List<String> flags = new ArrayList<>();
		            String breakChance = "";
		            String maxStack = "";
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			((Object_baseContext)_localctx).name = name();

			                line = ((Object_baseContext)_localctx).name.lineNo;
			                name = ((Object_baseContext)_localctx).name.nameStr;
			                tVal = ((Object_baseContext)_localctx).name.tValue;
			            
			setState(64);
			((Object_baseContext)_localctx).graphics = graphics();

			                colour = ((Object_baseContext)_localctx).graphics.colour;
			            
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BREAK) {
				{
				setState(66);
				((Object_baseContext)_localctx).break_chance = break_chance();

				                breakChance = ((Object_baseContext)_localctx).break_chance.breakChance;
				            
				}
			}

			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==MAX_STACK) {
				{
				setState(71);
				((Object_baseContext)_localctx).max_stack = max_stack();

				                maxStack = ((Object_baseContext)_localctx).max_stack.maxStack;
				            
				}
			}

			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAGS) {
				{
				{
				setState(76);
				((Object_baseContext)_localctx).flags = flags();

				                flags.addAll(((Object_baseContext)_localctx).flags.flagsList);
				            
				}
				}
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((Object_baseContext)_localctx).objectBase =  new ObjectBaseParseRecord(
			                name, tVal, colour, flags, breakChance, maxStack, line);
			        
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
		public List<ObjectBaseParseRecord> objectBaseList;
		public Default_valueContext default_value;
		public Object_baseContext object_base;
		public TerminalNode EOF() { return getToken(ObjectBaseGrammar.EOF, 0); }
		public List<Default_valueContext> default_value() {
			return getRuleContexts(Default_valueContext.class);
		}
		public Default_valueContext default_value(int i) {
			return getRuleContext(Default_valueContext.class,i);
		}
		public List<Object_baseContext> object_base() {
			return getRuleContexts(Object_baseContext.class);
		}
		public Object_baseContext object_base(int i) {
			return getRuleContext(Object_baseContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ObjectBaseGrammarListener ) ((ObjectBaseGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ObjectBaseGrammarVisitor ) return ((ObjectBaseGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_file);

		            ((FileContext)_localctx).objectBaseList =  new ArrayList<>();
		            String defaultMaxStack = "";
		            String defaultBreakChance = "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFAULT_MAX_STACK || _la==DEFAULT_BREAK_CHANCE) {
				{
				{
				setState(84);
				((FileContext)_localctx).default_value = default_value();

				                if (((FileContext)_localctx).default_value.maxStackNum.isEmpty())
				                    defaultBreakChance = ((FileContext)_localctx).default_value.breakChanceNum;
				                else
				                    defaultMaxStack = ((FileContext)_localctx).default_value.maxStackNum;
				            
				}
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(95); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(92);
				((FileContext)_localctx).object_base = object_base();

				                ObjectBaseParseRecord base = ((FileContext)_localctx).object_base.objectBase;
				                ObjectBaseParseRecord newBase;

				                if (base.breakChance().isEmpty() || base.maxStack().isEmpty()) {
				                    newBase = new ObjectBaseParseRecord(base.name(), base.tVal(), base.colour(),
				                    base.flags(),
				                    base.breakChance().isEmpty() ? defaultBreakChance : base.breakChance(),
				                    base.maxStack().isEmpty()    ? defaultMaxStack    : base.maxStack(), 
				                    base.line());
				                } else {
				                    newBase = base;
				                }

				                // add it to the list
				                _localctx.objectBaseList.add(newBase);
				            
				}
				}
				setState(97); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(99);
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
		"\u0004\u0001\u0013f\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003"+
		"\u0000\u0017\b\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0003\u0001$\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0005\u00058\b\u0005\n\u0005\f\u0005;\t"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006F\b\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0003\u0006K\b\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0005\u0006P\b\u0006\n\u0006\f\u0006S\t\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0005\u0007X\b\u0007\n\u0007\f\u0007[\t"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0004\u0007`\b\u0007\u000b"+
		"\u0007\f\u0007a\u0001\u0007\u0001\u0007\u0001\u0007\u0000\u0000\b\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0000\u0000e\u0000\u0016\u0001\u0000\u0000"+
		"\u0000\u0002#\u0001\u0000\u0000\u0000\u0004%\u0001\u0000\u0000\u0000\u0006"+
		")\u0001\u0000\u0000\u0000\b-\u0001\u0000\u0000\u0000\n1\u0001\u0000\u0000"+
		"\u0000\f>\u0001\u0000\u0000\u0000\u000eY\u0001\u0000\u0000\u0000\u0010"+
		"\u0011\u0005\u0002\u0000\u0000\u0011\u0012\u0005\b\u0000\u0000\u0012\u0017"+
		"\u0006\u0000\uffff\uffff\u0000\u0013\u0014\u0005\u0001\u0000\u0000\u0014"+
		"\u0015\u0005\b\u0000\u0000\u0015\u0017\u0006\u0000\uffff\uffff\u0000\u0016"+
		"\u0010\u0001\u0000\u0000\u0000\u0016\u0013\u0001\u0000\u0000\u0000\u0017"+
		"\u0001\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0003\u0000\u0000\u0019"+
		"\u001a\u0005\f\u0000\u0000\u001a\u001b\u0005\r\u0000\u0000\u001b\u001c"+
		"\u0005\u000f\u0000\u0000\u001c\u001d\u0001\u0000\u0000\u0000\u001d$\u0006"+
		"\u0001\uffff\uffff\u0000\u001e\u001f\u0005\u0003\u0000\u0000\u001f \u0005"+
		"\f\u0000\u0000 !\u0005\u000e\u0000\u0000!\"\u0001\u0000\u0000\u0000\""+
		"$\u0006\u0001\uffff\uffff\u0000#\u0018\u0001\u0000\u0000\u0000#\u001e"+
		"\u0001\u0000\u0000\u0000$\u0003\u0001\u0000\u0000\u0000%&\u0005\u0004"+
		"\u0000\u0000&\'\u0005\u000f\u0000\u0000\'(\u0006\u0002\uffff\uffff\u0000"+
		"(\u0005\u0001\u0000\u0000\u0000)*\u0005\u0005\u0000\u0000*+\u0005\b\u0000"+
		"\u0000+,\u0006\u0003\uffff\uffff\u0000,\u0007\u0001\u0000\u0000\u0000"+
		"-.\u0005\u0006\u0000\u0000./\u0005\b\u0000\u0000/0\u0006\u0004\uffff\uffff"+
		"\u00000\t\u0001\u0000\u0000\u000012\u0005\u0007\u0000\u000023\u0005\u0011"+
		"\u0000\u000039\u0006\u0005\uffff\uffff\u000045\u0005\u0012\u0000\u0000"+
		"56\u0005\u0011\u0000\u000068\u0006\u0005\uffff\uffff\u000074\u0001\u0000"+
		"\u0000\u00008;\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u00009:\u0001"+
		"\u0000\u0000\u0000:<\u0001\u0000\u0000\u0000;9\u0001\u0000\u0000\u0000"+
		"<=\u0005\u0013\u0000\u0000=\u000b\u0001\u0000\u0000\u0000>?\u0003\u0002"+
		"\u0001\u0000?@\u0006\u0006\uffff\uffff\u0000@A\u0003\u0004\u0002\u0000"+
		"AE\u0006\u0006\uffff\uffff\u0000BC\u0003\u0006\u0003\u0000CD\u0006\u0006"+
		"\uffff\uffff\u0000DF\u0001\u0000\u0000\u0000EB\u0001\u0000\u0000\u0000"+
		"EF\u0001\u0000\u0000\u0000FJ\u0001\u0000\u0000\u0000GH\u0003\b\u0004\u0000"+
		"HI\u0006\u0006\uffff\uffff\u0000IK\u0001\u0000\u0000\u0000JG\u0001\u0000"+
		"\u0000\u0000JK\u0001\u0000\u0000\u0000KQ\u0001\u0000\u0000\u0000LM\u0003"+
		"\n\u0005\u0000MN\u0006\u0006\uffff\uffff\u0000NP\u0001\u0000\u0000\u0000"+
		"OL\u0001\u0000\u0000\u0000PS\u0001\u0000\u0000\u0000QO\u0001\u0000\u0000"+
		"\u0000QR\u0001\u0000\u0000\u0000R\r\u0001\u0000\u0000\u0000SQ\u0001\u0000"+
		"\u0000\u0000TU\u0003\u0000\u0000\u0000UV\u0006\u0007\uffff\uffff\u0000"+
		"VX\u0001\u0000\u0000\u0000WT\u0001\u0000\u0000\u0000X[\u0001\u0000\u0000"+
		"\u0000YW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z_\u0001\u0000"+
		"\u0000\u0000[Y\u0001\u0000\u0000\u0000\\]\u0003\f\u0006\u0000]^\u0006"+
		"\u0007\uffff\uffff\u0000^`\u0001\u0000\u0000\u0000_\\\u0001\u0000\u0000"+
		"\u0000`a\u0001\u0000\u0000\u0000a_\u0001\u0000\u0000\u0000ab\u0001\u0000"+
		"\u0000\u0000bc\u0001\u0000\u0000\u0000cd\u0005\u0000\u0000\u0001d\u000f"+
		"\u0001\u0000\u0000\u0000\b\u0016#9EJQYa";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}