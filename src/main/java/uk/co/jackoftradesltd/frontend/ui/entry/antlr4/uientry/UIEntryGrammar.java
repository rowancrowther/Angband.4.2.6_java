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

// Generated from UIEntryGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.frontend.ui.entry.antlr4.uientry;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, PARAMETER=2, RENDERER=3, COMBINE=4, PRIORITY=5, CATEGORY=6, FLAGS=7, 
		DESC=8, LABEL=9, LABEL5=10, LABEL2=11, TEMPLATE=12, LTHAN=13, GTHAN=14, 
		INTEGER=15, LCASE=16, PARAMETER_VALUE=17, COMMENT=18, INDENT=19, EOL=20, 
		FLAG=21, FLAG_OR=22, FLAG_EOL=23, STRING=24;
	public static final int
		RULE_tag = 0, RULE_name = 1, RULE_parameter = 2, RULE_renderer = 3, RULE_combine = 4, 
		RULE_priority = 5, RULE_category = 6, RULE_flags = 7, RULE_desc = 8, RULE_label = 9, 
		RULE_label5 = 10, RULE_label2 = 11, RULE_template = 12, RULE_uiEntry = 13, 
		RULE_file = 14;
	private static String[] makeRuleNames() {
		return new String[] {
			"tag", "name", "parameter", "renderer", "combine", "priority", "category", 
			"flags", "desc", "label", "label5", "label2", "template", "uiEntry", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'parameter:'", "'renderer:'", "'combine:'", "'priority:'", 
			"'category:'", "'flags:'", "'desc:'", "'label:'", "'label5:'", "'label2:'", 
			"'template:'", "'<'", "'>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "PARAMETER", "RENDERER", "COMBINE", "PRIORITY", "CATEGORY", 
			"FLAGS", "DESC", "LABEL", "LABEL5", "LABEL2", "TEMPLATE", "LTHAN", "GTHAN", 
			"INTEGER", "LCASE", "PARAMETER_VALUE", "COMMENT", "INDENT", "EOL", "FLAG", 
			"FLAG_OR", "FLAG_EOL", "STRING"
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
	public String getGrammarFileName() { return "UIEntryGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public UIEntryGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TagContext extends ParserRuleContext {
		public String elementOrStat;
		public Token PARAMETER_VALUE;
		public TerminalNode LTHAN() { return getToken(UIEntryGrammar.LTHAN, 0); }
		public TerminalNode PARAMETER_VALUE() { return getToken(UIEntryGrammar.PARAMETER_VALUE, 0); }
		public TerminalNode GTHAN() { return getToken(UIEntryGrammar.GTHAN, 0); }
		public TagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitTag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TagContext tag() throws RecognitionException {
		TagContext _localctx = new TagContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_tag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(LTHAN);
			setState(31);
			((TagContext)_localctx).PARAMETER_VALUE = match(PARAMETER_VALUE);
			setState(32);
			match(GTHAN);

			                ((TagContext)_localctx).elementOrStat =  ((TagContext)_localctx).PARAMETER_VALUE.getText();
			            
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
		public String elemOrStat;
		public Token LCASE;
		public TagContext tag;
		public TerminalNode NAME() { return getToken(UIEntryGrammar.NAME, 0); }
		public TerminalNode LCASE() { return getToken(UIEntryGrammar.LCASE, 0); }
		public TagContext tag() {
			return getRuleContext(TagContext.class,0);
		}
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			match(NAME);
			setState(36);
			((NameContext)_localctx).LCASE = match(LCASE);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).LCASE.getText(); 
			setState(41);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LTHAN) {
				{
				setState(38);
				((NameContext)_localctx).tag = tag();
				 ((NameContext)_localctx).elemOrStat =  ((NameContext)_localctx).tag.elementOrStat;
				                   ((NameContext)_localctx).nameStr =  _localctx.nameStr + (((NameContext)_localctx).tag!=null?_input.getText(((NameContext)_localctx).tag.start,((NameContext)_localctx).tag.stop):null); 
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
	public static class ParameterContext extends ParserRuleContext {
		public String isElement;
		public Token STRING;
		public TerminalNode PARAMETER() { return getToken(UIEntryGrammar.PARAMETER, 0); }
		public TerminalNode STRING() { return getToken(UIEntryGrammar.STRING, 0); }
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			match(PARAMETER);
			setState(44);
			((ParameterContext)_localctx).STRING = match(STRING);
			 ((ParameterContext)_localctx).isElement =  ((ParameterContext)_localctx).STRING.getText(); 
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
	public static class RendererContext extends ParserRuleContext {
		public String uiEntryRenderer;
		public Token STRING;
		public TerminalNode RENDERER() { return getToken(UIEntryGrammar.RENDERER, 0); }
		public TerminalNode STRING() { return getToken(UIEntryGrammar.STRING, 0); }
		public RendererContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_renderer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterRenderer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitRenderer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitRenderer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RendererContext renderer() throws RecognitionException {
		RendererContext _localctx = new RendererContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_renderer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			match(RENDERER);
			setState(48);
			((RendererContext)_localctx).STRING = match(STRING);

			                ((RendererContext)_localctx).uiEntryRenderer =  ((RendererContext)_localctx).STRING.getText();
			        
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
	public static class CombineContext extends ParserRuleContext {
		public String combiner;
		public Token FLAG;
		public TerminalNode COMBINE() { return getToken(UIEntryGrammar.COMBINE, 0); }
		public TerminalNode FLAG() { return getToken(UIEntryGrammar.FLAG, 0); }
		public CombineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_combine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterCombine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitCombine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitCombine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombineContext combine() throws RecognitionException {
		CombineContext _localctx = new CombineContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_combine);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(COMBINE);
			setState(52);
			((CombineContext)_localctx).FLAG = match(FLAG);
			 ((CombineContext)_localctx).combiner =  ((CombineContext)_localctx).FLAG.getText(); 
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
	public static class PriorityContext extends ParserRuleContext {
		public String word;
		public Token STRING;
		public TerminalNode PRIORITY() { return getToken(UIEntryGrammar.PRIORITY, 0); }
		public TerminalNode STRING() { return getToken(UIEntryGrammar.STRING, 0); }
		public PriorityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_priority; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterPriority(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitPriority(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitPriority(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PriorityContext priority() throws RecognitionException {
		PriorityContext _localctx = new PriorityContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_priority);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			match(PRIORITY);
			setState(56);
			((PriorityContext)_localctx).STRING = match(STRING);
			 ((PriorityContext)_localctx).word =  ((PriorityContext)_localctx).STRING.getText(); 
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
	public static class CategoryContext extends ParserRuleContext {
		public List<String> categoryStr;
		public Token STRING;
		public List<TerminalNode> CATEGORY() { return getTokens(UIEntryGrammar.CATEGORY); }
		public TerminalNode CATEGORY(int i) {
			return getToken(UIEntryGrammar.CATEGORY, i);
		}
		public List<TerminalNode> STRING() { return getTokens(UIEntryGrammar.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(UIEntryGrammar.STRING, i);
		}
		public CategoryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_category; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterCategory(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitCategory(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitCategory(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CategoryContext category() throws RecognitionException {
		CategoryContext _localctx = new CategoryContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_category);

		            ((CategoryContext)_localctx).categoryStr =  new ArrayList<>();
		        
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(62); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(59);
					match(CATEGORY);
					setState(60);
					((CategoryContext)_localctx).STRING = match(STRING);
					 _localctx.categoryStr.add(((CategoryContext)_localctx).STRING.getText()); 
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(64); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
		public List<String> entryFlagEnum;
		public Token FLAG;
		public TerminalNode FLAGS() { return getToken(UIEntryGrammar.FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(UIEntryGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(UIEntryGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(UIEntryGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(UIEntryGrammar.FLAG_OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_flags);

		            ((FlagsContext)_localctx).entryFlagEnum =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			match(FLAGS);
			setState(67);
			((FlagsContext)_localctx).FLAG = match(FLAG);

			            _localctx.entryFlagEnum.add(((FlagsContext)_localctx).FLAG.getText());
			        
			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(69);
				match(FLAG_OR);
				setState(70);
				((FlagsContext)_localctx).FLAG = match(FLAG);

				            _localctx.entryFlagEnum.add(((FlagsContext)_localctx).FLAG.getText());
				        
				}
				}
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
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
		public String descStr;
		public Token STRING;
		public List<TerminalNode> DESC() { return getTokens(UIEntryGrammar.DESC); }
		public TerminalNode DESC(int i) {
			return getToken(UIEntryGrammar.DESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(UIEntryGrammar.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(UIEntryGrammar.STRING, i);
		}
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_desc);
		 StringBuilder sb = new StringBuilder(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(77);
				match(DESC);
				setState(78);
				((DescContext)_localctx).STRING = match(STRING);
				 sb.append(((DescContext)_localctx).STRING.getText()); 
				}
				}
				setState(82); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DESC );
			}
			_ctx.stop = _input.LT(-1);
			 ((DescContext)_localctx).descStr =  sb.toString(); 
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
	public static class LabelContext extends ParserRuleContext {
		public String labelStr;
		public Token STRING;
		public TerminalNode LABEL() { return getToken(UIEntryGrammar.LABEL, 0); }
		public TerminalNode STRING() { return getToken(UIEntryGrammar.STRING, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(LABEL);
			setState(85);
			((LabelContext)_localctx).STRING = match(STRING);
			 ((LabelContext)_localctx).labelStr =  ((LabelContext)_localctx).STRING.getText(); 
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
	public static class Label5Context extends ParserRuleContext {
		public String label5Str;
		public Token STRING;
		public TerminalNode LABEL5() { return getToken(UIEntryGrammar.LABEL5, 0); }
		public TerminalNode STRING() { return getToken(UIEntryGrammar.STRING, 0); }
		public Label5Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label5; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterLabel5(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitLabel5(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitLabel5(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Label5Context label5() throws RecognitionException {
		Label5Context _localctx = new Label5Context(_ctx, getState());
		enterRule(_localctx, 20, RULE_label5);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(LABEL5);
			setState(89);
			((Label5Context)_localctx).STRING = match(STRING);
			 ((Label5Context)_localctx).label5Str =  ((Label5Context)_localctx).STRING.getText(); 
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
	public static class Label2Context extends ParserRuleContext {
		public String label2Str;
		public Token STRING;
		public TerminalNode LABEL2() { return getToken(UIEntryGrammar.LABEL2, 0); }
		public TerminalNode STRING() { return getToken(UIEntryGrammar.STRING, 0); }
		public Label2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label2; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterLabel2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitLabel2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitLabel2(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Label2Context label2() throws RecognitionException {
		Label2Context _localctx = new Label2Context(_ctx, getState());
		enterRule(_localctx, 22, RULE_label2);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			match(LABEL2);
			setState(93);
			((Label2Context)_localctx).STRING = match(STRING);
			 ((Label2Context)_localctx).label2Str =  ((Label2Context)_localctx).STRING.getText(); 
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
	public static class TemplateContext extends ParserRuleContext {
		public String uiEntryBase;
		public Token STRING;
		public TerminalNode TEMPLATE() { return getToken(UIEntryGrammar.TEMPLATE, 0); }
		public TerminalNode STRING() { return getToken(UIEntryGrammar.STRING, 0); }
		public TemplateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_template; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterTemplate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitTemplate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitTemplate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TemplateContext template() throws RecognitionException {
		TemplateContext _localctx = new TemplateContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_template);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(TEMPLATE);
			setState(97);
			((TemplateContext)_localctx).STRING = match(STRING);

			                ((TemplateContext)_localctx).uiEntryBase =  ((TemplateContext)_localctx).STRING.getText();
			        
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
	public static class UiEntryContext extends ParserRuleContext {
		public UIEntryParseRecord entry;
		public NameContext name;
		public TemplateContext template;
		public LabelContext label;
		public Label5Context label5;
		public Label2Context label2;
		public CategoryContext category;
		public ParameterContext parameter;
		public RendererContext renderer;
		public CombineContext combine;
		public PriorityContext priority;
		public FlagsContext flags;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TemplateContext template() {
			return getRuleContext(TemplateContext.class,0);
		}
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public Label5Context label5() {
			return getRuleContext(Label5Context.class,0);
		}
		public Label2Context label2() {
			return getRuleContext(Label2Context.class,0);
		}
		public List<CategoryContext> category() {
			return getRuleContexts(CategoryContext.class);
		}
		public CategoryContext category(int i) {
			return getRuleContext(CategoryContext.class,i);
		}
		public ParameterContext parameter() {
			return getRuleContext(ParameterContext.class,0);
		}
		public RendererContext renderer() {
			return getRuleContext(RendererContext.class,0);
		}
		public CombineContext combine() {
			return getRuleContext(CombineContext.class,0);
		}
		public List<PriorityContext> priority() {
			return getRuleContexts(PriorityContext.class);
		}
		public PriorityContext priority(int i) {
			return getRuleContext(PriorityContext.class,i);
		}
		public FlagsContext flags() {
			return getRuleContext(FlagsContext.class,0);
		}
		public DescContext desc() {
			return getRuleContext(DescContext.class,0);
		}
		public UiEntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uiEntry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterUiEntry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitUiEntry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitUiEntry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UiEntryContext uiEntry() throws RecognitionException {
		UiEntryContext _localctx = new UiEntryContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_uiEntry);

		            int             line            = 0;
		            String          nameInit        = "";
		            String          templateInit    = "";
		            String          labelInit       = "";
		            String          label5Init      = "";
		            String          label2Init      = "";
		            List<String>    categoryInit1   = new ArrayList<>();
		            String          parameterInit   = "";
		            List<String>    categoryInit2   = new ArrayList<>();
		            String          rendererInit    = "";
		            String          combinerInit    = "";
		            String          priorityInit    = "";
		            List<String>    flagInit        = new ArrayList<>();
		            String          descInit        = "";
		            String          nameTagInit     = "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			((UiEntryContext)_localctx).name = name();
			 nameInit = ((UiEntryContext)_localctx).name.nameStr;
			                   if (((UiEntryContext)_localctx).name.elemOrStat != null) nameTagInit = ((UiEntryContext)_localctx).name.elemOrStat;
			                   line = _localctx.start.getLine(); 
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TEMPLATE) {
				{
				setState(102);
				((UiEntryContext)_localctx).template = template();
				 templateInit = ((UiEntryContext)_localctx).template.uiEntryBase; 
				}
			}

			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LABEL) {
				{
				setState(107);
				((UiEntryContext)_localctx).label = label();
				 labelInit = ((UiEntryContext)_localctx).label.labelStr; 
				}
			}

			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LABEL5) {
				{
				setState(112);
				((UiEntryContext)_localctx).label5 = label5();
				 label5Init = ((UiEntryContext)_localctx).label5.label5Str; 
				}
			}

			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LABEL2) {
				{
				setState(117);
				((UiEntryContext)_localctx).label2 = label2();
				 label2Init = ((UiEntryContext)_localctx).label2.label2Str; 
				}
			}

			setState(125);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(122);
				((UiEntryContext)_localctx).category = category();
				 categoryInit1.addAll(((UiEntryContext)_localctx).category.categoryStr); 
				}
				break;
			}
			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARAMETER) {
				{
				setState(127);
				((UiEntryContext)_localctx).parameter = parameter();
				 parameterInit = ((UiEntryContext)_localctx).parameter.isElement; 
				}
			}

			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RENDERER) {
				{
				setState(132);
				((UiEntryContext)_localctx).renderer = renderer();
				 rendererInit = ((UiEntryContext)_localctx).renderer.uiEntryRenderer; 
				}
			}

			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMBINE) {
				{
				setState(137);
				((UiEntryContext)_localctx).combine = combine();
				 combinerInit = ((UiEntryContext)_localctx).combine.combiner; 
				}
			}

			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PRIORITY) {
				{
				{
				setState(142);
				((UiEntryContext)_localctx).priority = priority();
				 priorityInit = ((UiEntryContext)_localctx).priority.word; 
				}
				}
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CATEGORY) {
				{
				setState(150);
				((UiEntryContext)_localctx).category = category();
				categoryInit2.addAll(((UiEntryContext)_localctx).category.categoryStr); 
				}
			}

			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FLAGS) {
				{
				setState(155);
				((UiEntryContext)_localctx).flags = flags();
				 flagInit = ((UiEntryContext)_localctx).flags.entryFlagEnum; 
				}
			}

			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DESC) {
				{
				setState(160);
				((UiEntryContext)_localctx).desc = desc();
				 descInit = descInit + ((UiEntryContext)_localctx).desc.descStr; 
				}
			}

			}
			_ctx.stop = _input.LT(-1);

			            ((UiEntryContext)_localctx).entry =  new UIEntryParseRecord(nameInit, templateInit, labelInit,
			            label5Init, label2Init, categoryInit1, parameterInit, rendererInit, combinerInit,
			            priorityInit, categoryInit2, flagInit, descInit, line);
			        
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
		public List<UIEntryParseRecord> entries;
		public UiEntryContext uiEntry;
		public TerminalNode EOF() { return getToken(UIEntryGrammar.EOF, 0); }
		public List<UiEntryContext> uiEntry() {
			return getRuleContexts(UiEntryContext.class);
		}
		public UiEntryContext uiEntry(int i) {
			return getRuleContext(UiEntryContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryGrammarListener ) ((UIEntryGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryGrammarVisitor ) return ((UIEntryGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_file);

		            ((FileContext)_localctx).entries =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(165);
				((FileContext)_localctx).uiEntry = uiEntry();
				 _localctx.entries.add(((FileContext)_localctx).uiEntry.entry); 
				}
				}
				setState(170); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(172);
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
		"\u0004\u0001\u0018\u00af\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001*\b\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0004\u0006?\b\u0006\u000b\u0006\f\u0006@\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007I\b\u0007"+
		"\n\u0007\f\u0007L\t\u0007\u0001\b\u0001\b\u0001\b\u0004\bQ\b\b\u000b\b"+
		"\f\bR\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\rj\b\r\u0001"+
		"\r\u0001\r\u0001\r\u0003\ro\b\r\u0001\r\u0001\r\u0001\r\u0003\rt\b\r\u0001"+
		"\r\u0001\r\u0001\r\u0003\ry\b\r\u0001\r\u0001\r\u0001\r\u0003\r~\b\r\u0001"+
		"\r\u0001\r\u0001\r\u0003\r\u0083\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u0088"+
		"\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u008d\b\r\u0001\r\u0001\r\u0001\r"+
		"\u0005\r\u0092\b\r\n\r\f\r\u0095\t\r\u0001\r\u0001\r\u0001\r\u0003\r\u009a"+
		"\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u009f\b\r\u0001\r\u0001\r\u0001\r"+
		"\u0003\r\u00a4\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0004\u000e\u00a9"+
		"\b\u000e\u000b\u000e\f\u000e\u00aa\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0000\u0000\u000f\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014"+
		"\u0016\u0018\u001a\u001c\u0000\u0000\u00b0\u0000\u001e\u0001\u0000\u0000"+
		"\u0000\u0002#\u0001\u0000\u0000\u0000\u0004+\u0001\u0000\u0000\u0000\u0006"+
		"/\u0001\u0000\u0000\u0000\b3\u0001\u0000\u0000\u0000\n7\u0001\u0000\u0000"+
		"\u0000\f>\u0001\u0000\u0000\u0000\u000eB\u0001\u0000\u0000\u0000\u0010"+
		"P\u0001\u0000\u0000\u0000\u0012T\u0001\u0000\u0000\u0000\u0014X\u0001"+
		"\u0000\u0000\u0000\u0016\\\u0001\u0000\u0000\u0000\u0018`\u0001\u0000"+
		"\u0000\u0000\u001ad\u0001\u0000\u0000\u0000\u001c\u00a8\u0001\u0000\u0000"+
		"\u0000\u001e\u001f\u0005\r\u0000\u0000\u001f \u0005\u0011\u0000\u0000"+
		" !\u0005\u000e\u0000\u0000!\"\u0006\u0000\uffff\uffff\u0000\"\u0001\u0001"+
		"\u0000\u0000\u0000#$\u0005\u0001\u0000\u0000$%\u0005\u0010\u0000\u0000"+
		"%)\u0006\u0001\uffff\uffff\u0000&\'\u0003\u0000\u0000\u0000\'(\u0006\u0001"+
		"\uffff\uffff\u0000(*\u0001\u0000\u0000\u0000)&\u0001\u0000\u0000\u0000"+
		")*\u0001\u0000\u0000\u0000*\u0003\u0001\u0000\u0000\u0000+,\u0005\u0002"+
		"\u0000\u0000,-\u0005\u0018\u0000\u0000-.\u0006\u0002\uffff\uffff\u0000"+
		".\u0005\u0001\u0000\u0000\u0000/0\u0005\u0003\u0000\u000001\u0005\u0018"+
		"\u0000\u000012\u0006\u0003\uffff\uffff\u00002\u0007\u0001\u0000\u0000"+
		"\u000034\u0005\u0004\u0000\u000045\u0005\u0015\u0000\u000056\u0006\u0004"+
		"\uffff\uffff\u00006\t\u0001\u0000\u0000\u000078\u0005\u0005\u0000\u0000"+
		"89\u0005\u0018\u0000\u00009:\u0006\u0005\uffff\uffff\u0000:\u000b\u0001"+
		"\u0000\u0000\u0000;<\u0005\u0006\u0000\u0000<=\u0005\u0018\u0000\u0000"+
		"=?\u0006\u0006\uffff\uffff\u0000>;\u0001\u0000\u0000\u0000?@\u0001\u0000"+
		"\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000A\r\u0001"+
		"\u0000\u0000\u0000BC\u0005\u0007\u0000\u0000CD\u0005\u0015\u0000\u0000"+
		"DJ\u0006\u0007\uffff\uffff\u0000EF\u0005\u0016\u0000\u0000FG\u0005\u0015"+
		"\u0000\u0000GI\u0006\u0007\uffff\uffff\u0000HE\u0001\u0000\u0000\u0000"+
		"IL\u0001\u0000\u0000\u0000JH\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000"+
		"\u0000K\u000f\u0001\u0000\u0000\u0000LJ\u0001\u0000\u0000\u0000MN\u0005"+
		"\b\u0000\u0000NO\u0005\u0018\u0000\u0000OQ\u0006\b\uffff\uffff\u0000P"+
		"M\u0001\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000RP\u0001\u0000\u0000"+
		"\u0000RS\u0001\u0000\u0000\u0000S\u0011\u0001\u0000\u0000\u0000TU\u0005"+
		"\t\u0000\u0000UV\u0005\u0018\u0000\u0000VW\u0006\t\uffff\uffff\u0000W"+
		"\u0013\u0001\u0000\u0000\u0000XY\u0005\n\u0000\u0000YZ\u0005\u0018\u0000"+
		"\u0000Z[\u0006\n\uffff\uffff\u0000[\u0015\u0001\u0000\u0000\u0000\\]\u0005"+
		"\u000b\u0000\u0000]^\u0005\u0018\u0000\u0000^_\u0006\u000b\uffff\uffff"+
		"\u0000_\u0017\u0001\u0000\u0000\u0000`a\u0005\f\u0000\u0000ab\u0005\u0018"+
		"\u0000\u0000bc\u0006\f\uffff\uffff\u0000c\u0019\u0001\u0000\u0000\u0000"+
		"de\u0003\u0002\u0001\u0000ei\u0006\r\uffff\uffff\u0000fg\u0003\u0018\f"+
		"\u0000gh\u0006\r\uffff\uffff\u0000hj\u0001\u0000\u0000\u0000if\u0001\u0000"+
		"\u0000\u0000ij\u0001\u0000\u0000\u0000jn\u0001\u0000\u0000\u0000kl\u0003"+
		"\u0012\t\u0000lm\u0006\r\uffff\uffff\u0000mo\u0001\u0000\u0000\u0000n"+
		"k\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000os\u0001\u0000\u0000"+
		"\u0000pq\u0003\u0014\n\u0000qr\u0006\r\uffff\uffff\u0000rt\u0001\u0000"+
		"\u0000\u0000sp\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tx\u0001"+
		"\u0000\u0000\u0000uv\u0003\u0016\u000b\u0000vw\u0006\r\uffff\uffff\u0000"+
		"wy\u0001\u0000\u0000\u0000xu\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000"+
		"\u0000y}\u0001\u0000\u0000\u0000z{\u0003\f\u0006\u0000{|\u0006\r\uffff"+
		"\uffff\u0000|~\u0001\u0000\u0000\u0000}z\u0001\u0000\u0000\u0000}~\u0001"+
		"\u0000\u0000\u0000~\u0082\u0001\u0000\u0000\u0000\u007f\u0080\u0003\u0004"+
		"\u0002\u0000\u0080\u0081\u0006\r\uffff\uffff\u0000\u0081\u0083\u0001\u0000"+
		"\u0000\u0000\u0082\u007f\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000"+
		"\u0000\u0000\u0083\u0087\u0001\u0000\u0000\u0000\u0084\u0085\u0003\u0006"+
		"\u0003\u0000\u0085\u0086\u0006\r\uffff\uffff\u0000\u0086\u0088\u0001\u0000"+
		"\u0000\u0000\u0087\u0084\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000"+
		"\u0000\u0000\u0088\u008c\u0001\u0000\u0000\u0000\u0089\u008a\u0003\b\u0004"+
		"\u0000\u008a\u008b\u0006\r\uffff\uffff\u0000\u008b\u008d\u0001\u0000\u0000"+
		"\u0000\u008c\u0089\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000"+
		"\u0000\u008d\u0093\u0001\u0000\u0000\u0000\u008e\u008f\u0003\n\u0005\u0000"+
		"\u008f\u0090\u0006\r\uffff\uffff\u0000\u0090\u0092\u0001\u0000\u0000\u0000"+
		"\u0091\u008e\u0001\u0000\u0000\u0000\u0092\u0095\u0001\u0000\u0000\u0000"+
		"\u0093\u0091\u0001\u0000\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000"+
		"\u0094\u0099\u0001\u0000\u0000\u0000\u0095\u0093\u0001\u0000\u0000\u0000"+
		"\u0096\u0097\u0003\f\u0006\u0000\u0097\u0098\u0006\r\uffff\uffff\u0000"+
		"\u0098\u009a\u0001\u0000\u0000\u0000\u0099\u0096\u0001\u0000\u0000\u0000"+
		"\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u009e\u0001\u0000\u0000\u0000"+
		"\u009b\u009c\u0003\u000e\u0007\u0000\u009c\u009d\u0006\r\uffff\uffff\u0000"+
		"\u009d\u009f\u0001\u0000\u0000\u0000\u009e\u009b\u0001\u0000\u0000\u0000"+
		"\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a3\u0001\u0000\u0000\u0000"+
		"\u00a0\u00a1\u0003\u0010\b\u0000\u00a1\u00a2\u0006\r\uffff\uffff\u0000"+
		"\u00a2\u00a4\u0001\u0000\u0000\u0000\u00a3\u00a0\u0001\u0000\u0000\u0000"+
		"\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u001b\u0001\u0000\u0000\u0000"+
		"\u00a5\u00a6\u0003\u001a\r\u0000\u00a6\u00a7\u0006\u000e\uffff\uffff\u0000"+
		"\u00a7\u00a9\u0001\u0000\u0000\u0000\u00a8\u00a5\u0001\u0000\u0000\u0000"+
		"\u00a9\u00aa\u0001\u0000\u0000\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000"+
		"\u00aa\u00ab\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000\u0000\u0000"+
		"\u00ac\u00ad\u0005\u0000\u0000\u0001\u00ad\u001d\u0001\u0000\u0000\u0000"+
		"\u0011)@JRinsx}\u0082\u0087\u008c\u0093\u0099\u009e\u00a3\u00aa";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}