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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntry.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
    import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
    import uk.co.jackoftrades.frontend.entries.UIEntry;
    import uk.co.jackoftrades.frontend.entries.UIEntry.StatElemType;
    import uk.co.jackoftrades.frontend.entries.UIEntryBase;
    import uk.co.jackoftrades.frontend.entries.enums.EntryFlag;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, NAME=3, PARAMETER=4, PARAMETERTYPE=5, RENDERER=6, COMBINE=7, 
		PRIORITY=8, CATEGORY=9, FLAGS=10, DESC=11, LABEL=12, LABEL5=13, LABEL2=14, 
		TEMPLATE=15, PRIORITYWORD=16, PRIORITYNUM=17, TEXT=18, TAG=19;
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
			null, null, null, "'name:'", "'parameter:'", null, "'renderer:'", "'combine:'", 
			"'priority:'", "'category:'", "'flags:'", "'desc:'", "'label:'", "'label5:'", 
			"'label2:'", "'template:'", "'negative_index'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "NAME", "PARAMETER", "PARAMETERTYPE", "RENDERER", 
			"COMBINE", "PRIORITY", "CATEGORY", "FLAGS", "DESC", "LABEL", "LABEL5", 
			"LABEL2", "TEMPLATE", "PRIORITYWORD", "PRIORITYNUM", "TEXT", "TAG"
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
	public String getGrammarFileName() { return "UIEntry.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public UIEntryParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TagContext extends ParserRuleContext {
		public ElementEnum elemEnum;
		public Token TAG;
		public TerminalNode TAG() { return getToken(UIEntryParser.TAG, 0); }
		public TagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitTag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitTag(this);
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
			((TagContext)_localctx).TAG = match(TAG);

			                String raw = ((TagContext)_localctx).TAG.getText();
			                String elemStr = "ELEM_" + raw.substring(1, raw.length() - 1);
			                ((TagContext)_localctx).elemEnum =  ElementEnum.valueOf(elemStr);
			            
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
		public ElementEnum elemEnum;
		public Token TEXT;
		public TagContext tag;
		public TerminalNode NAME() { return getToken(UIEntryParser.NAME, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public TagContext tag() {
			return getRuleContext(TagContext.class,0);
		}
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);

		            ((NameContext)_localctx).elemEnum =  ElementEnum.ELEM_NONE;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			match(NAME);
			setState(34);
			((NameContext)_localctx).TEXT = match(TEXT);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).TEXT.getText(); 
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TAG) {
				{
				setState(36);
				((NameContext)_localctx).tag = tag();
				 ((NameContext)_localctx).elemEnum =  ((NameContext)_localctx).tag.elemEnum; 
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
		public boolean isElement;
		public Token PARAMETERTYPE;
		public TerminalNode PARAMETER() { return getToken(UIEntryParser.PARAMETER, 0); }
		public TerminalNode PARAMETERTYPE() { return getToken(UIEntryParser.PARAMETERTYPE, 0); }
		public ParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParameterContext parameter() throws RecognitionException {
		ParameterContext _localctx = new ParameterContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_parameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(PARAMETER);
			setState(42);
			((ParameterContext)_localctx).PARAMETERTYPE = match(PARAMETERTYPE);
			 ((ParameterContext)_localctx).isElement =  ((ParameterContext)_localctx).PARAMETERTYPE.getText().equals("element"); 
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
		public UIEntryRenderer uiEntryRenderer;
		public Token TEXT;
		public TerminalNode RENDERER() { return getToken(UIEntryParser.RENDERER, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public RendererContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_renderer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterRenderer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitRenderer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitRenderer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RendererContext renderer() throws RecognitionException {
		RendererContext _localctx = new RendererContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_renderer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(RENDERER);
			setState(46);
			((RendererContext)_localctx).TEXT = match(TEXT);

			                ((RendererContext)_localctx).uiEntryRenderer =  GameConstants.getUIEntryRenderer(((RendererContext)_localctx).TEXT.getText());
			        
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
		public CombinerName combiner;
		public Token TEXT;
		public TerminalNode COMBINE() { return getToken(UIEntryParser.COMBINE, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public CombineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_combine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterCombine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitCombine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitCombine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombineContext combine() throws RecognitionException {
		CombineContext _localctx = new CombineContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_combine);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			match(COMBINE);
			setState(50);
			((CombineContext)_localctx).TEXT = match(TEXT);
			 ((CombineContext)_localctx).combiner =  CombinerName.valueOf(((CombineContext)_localctx).TEXT.getText()); 
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
		public int num;
		public Token PRIORITYWORD;
		public Token PRIORITYNUM;
		public TerminalNode PRIORITY() { return getToken(UIEntryParser.PRIORITY, 0); }
		public TerminalNode PRIORITYWORD() { return getToken(UIEntryParser.PRIORITYWORD, 0); }
		public TerminalNode PRIORITYNUM() { return getToken(UIEntryParser.PRIORITYNUM, 0); }
		public PriorityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_priority; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterPriority(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitPriority(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitPriority(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PriorityContext priority() throws RecognitionException {
		PriorityContext _localctx = new PriorityContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_priority);

		            ((PriorityContext)_localctx).word =  "";
		            ((PriorityContext)_localctx).num =  0;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			match(PRIORITY);
			setState(58);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PRIORITYWORD:
				{
				{
				setState(54);
				((PriorityContext)_localctx).PRIORITYWORD = match(PRIORITYWORD);
				 ((PriorityContext)_localctx).word =  ((PriorityContext)_localctx).PRIORITYWORD.getText(); 
				}
				}
				break;
			case PRIORITYNUM:
				{
				{
				setState(56);
				((PriorityContext)_localctx).PRIORITYNUM = match(PRIORITYNUM);
				 ((PriorityContext)_localctx).num =  Integer.parseInt(((PriorityContext)_localctx).PRIORITYNUM.getText()); 
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class CategoryContext extends ParserRuleContext {
		public String categoryStr;
		public Token TEXT;
		public TerminalNode CATEGORY() { return getToken(UIEntryParser.CATEGORY, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public CategoryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_category; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterCategory(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitCategory(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitCategory(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CategoryContext category() throws RecognitionException {
		CategoryContext _localctx = new CategoryContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_category);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(CATEGORY);
			setState(61);
			((CategoryContext)_localctx).TEXT = match(TEXT);
			 ((CategoryContext)_localctx).categoryStr =  ((CategoryContext)_localctx).TEXT.getText(); 
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
		public EntryFlag entryFlagEnum;
		public Token TEXT;
		public TerminalNode FLAGS() { return getToken(UIEntryParser.FLAGS, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_flags);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(FLAGS);
			setState(65);
			((FlagsContext)_localctx).TEXT = match(TEXT);

			                ((FlagsContext)_localctx).entryFlagEnum =  EntryFlag.valueOf("ENTRY_FLAG_" + ((FlagsContext)_localctx).TEXT.getText());
			        
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
		public Token TEXT;
		public TerminalNode DESC() { return getToken(UIEntryParser.DESC, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(DESC);
			setState(69);
			((DescContext)_localctx).TEXT = match(TEXT);
			 ((DescContext)_localctx).descStr =  ((DescContext)_localctx).TEXT.getText(); 
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
	public static class LabelContext extends ParserRuleContext {
		public String labelStr;
		public Token TEXT;
		public TerminalNode LABEL() { return getToken(UIEntryParser.LABEL, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(LABEL);
			setState(73);
			((LabelContext)_localctx).TEXT = match(TEXT);
			 ((LabelContext)_localctx).labelStr =  ((LabelContext)_localctx).TEXT.getText(); 
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
		public Token TEXT;
		public TerminalNode LABEL5() { return getToken(UIEntryParser.LABEL5, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public Label5Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label5; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterLabel5(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitLabel5(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitLabel5(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Label5Context label5() throws RecognitionException {
		Label5Context _localctx = new Label5Context(_ctx, getState());
		enterRule(_localctx, 20, RULE_label5);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(LABEL5);
			setState(77);
			((Label5Context)_localctx).TEXT = match(TEXT);
			 ((Label5Context)_localctx).label5Str =  ((Label5Context)_localctx).TEXT.getText(); 
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
		public Token TEXT;
		public TerminalNode LABEL2() { return getToken(UIEntryParser.LABEL2, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public Label2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label2; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterLabel2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitLabel2(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitLabel2(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Label2Context label2() throws RecognitionException {
		Label2Context _localctx = new Label2Context(_ctx, getState());
		enterRule(_localctx, 22, RULE_label2);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(LABEL2);
			setState(81);
			((Label2Context)_localctx).TEXT = match(TEXT);
			 ((Label2Context)_localctx).label2Str =  ((Label2Context)_localctx).TEXT.getText(); 
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
		public UIEntryBase uiEntryBase;
		public Token TEXT;
		public TerminalNode TEMPLATE() { return getToken(UIEntryParser.TEMPLATE, 0); }
		public TerminalNode TEXT() { return getToken(UIEntryParser.TEXT, 0); }
		public TemplateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_template; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterTemplate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitTemplate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitTemplate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TemplateContext template() throws RecognitionException {
		TemplateContext _localctx = new TemplateContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_template);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(TEMPLATE);
			setState(85);
			((TemplateContext)_localctx).TEXT = match(TEXT);

			                String raw = ((TemplateContext)_localctx).TEXT.getText();
			                ((TemplateContext)_localctx).uiEntryBase =  GameConstants.getUIEntryBase(raw);
			        
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
		public UIEntry entry;
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
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public List<CategoryContext> category() {
			return getRuleContexts(CategoryContext.class);
		}
		public CategoryContext category(int i) {
			return getRuleContext(CategoryContext.class,i);
		}
		public UiEntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uiEntry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterUiEntry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitUiEntry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitUiEntry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UiEntryContext uiEntry() throws RecognitionException {
		UiEntryContext _localctx = new UiEntryContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_uiEntry);

		            String          nameInit        = "";
		            UIEntryBase     templateInit    = null;
		            String          labelInit       = "";
		            String          label5Init      = "";
		            String          label2Init      = "";
		            List<String>    categoryInit    = new ArrayList<>();
		            ElementEnum     parameterInit   = ElementEnum.ELEM_NONE;
		            boolean         parmIsElement   = false;
		            UIEntryRenderer rendererInit    = null;
		            CombinerName    combinerInit    = CombinerName.NONE;
		            String          priorityStrInit = "";
		            int             priorityNumInit = 0;
		            EntryFlag       flagInit        = EntryFlag.ENTRY_FLAG_TEMPLATE_ONLY;
		            String          descInit        = "";
		        
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			((UiEntryContext)_localctx).name = name();
			 nameInit = ((UiEntryContext)_localctx).name.nameStr;
			                   parameterInit = ((UiEntryContext)_localctx).name.elemEnum; 
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==TEMPLATE) {
				{
				setState(90);
				((UiEntryContext)_localctx).template = template();
				 templateInit = ((UiEntryContext)_localctx).template.uiEntryBase; 
				}
			}

			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LABEL) {
				{
				setState(95);
				((UiEntryContext)_localctx).label = label();
				 labelInit = ((UiEntryContext)_localctx).label.labelStr; 
				}
			}

			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LABEL5) {
				{
				setState(100);
				((UiEntryContext)_localctx).label5 = label5();
				 label5Init = ((UiEntryContext)_localctx).label5.label5Str; 
				}
			}

			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LABEL2) {
				{
				setState(105);
				((UiEntryContext)_localctx).label2 = label2();
				 label2Init = ((UiEntryContext)_localctx).label2.label2Str; 
				}
			}

			setState(117);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(113); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(110);
						((UiEntryContext)_localctx).category = category();
						categoryInit.add(((UiEntryContext)_localctx).category.categoryStr); 
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(115); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			}
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PARAMETER) {
				{
				setState(119);
				((UiEntryContext)_localctx).parameter = parameter();
				 parmIsElement = ((UiEntryContext)_localctx).parameter.isElement; 
				}
			}

			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RENDERER) {
				{
				setState(124);
				((UiEntryContext)_localctx).renderer = renderer();
				 rendererInit = ((UiEntryContext)_localctx).renderer.uiEntryRenderer; 
				}
			}

			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMBINE) {
				{
				setState(129);
				((UiEntryContext)_localctx).combine = combine();
				 combinerInit = ((UiEntryContext)_localctx).combine.combiner; 
				}
			}

			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PRIORITY) {
				{
				{
				setState(134);
				((UiEntryContext)_localctx).priority = priority();
				 priorityStrInit = ((UiEntryContext)_localctx).priority.word;
				                        priorityNumInit = ((UiEntryContext)_localctx).priority.num; 
				}
				}
				setState(141);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(149);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CATEGORY) {
				{
				setState(145); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(142);
					((UiEntryContext)_localctx).category = category();
					categoryInit.add(((UiEntryContext)_localctx).category.categoryStr); 
					}
					}
					setState(147); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==CATEGORY );
				}
			}

			setState(154);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FLAGS) {
				{
				setState(151);
				((UiEntryContext)_localctx).flags = flags();
				 flagInit = ((UiEntryContext)_localctx).flags.entryFlagEnum; 
				}
			}

			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DESC) {
				{
				{
				setState(156);
				((UiEntryContext)_localctx).desc = desc();
				 descInit = descInit + ((UiEntryContext)_localctx).desc.descStr; 
				}
				}
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			                UIEntry.StatElemType type = parmIsElement
			                                          ? StatElemType.ELEMENT
			                                          : StatElemType.STAT;
			                ((UiEntryContext)_localctx).entry =  new UIEntry(nameInit, parameterInit, type,
			                                     rendererInit, combinerInit,
			                                     categoryInit, priorityNumInit,
			                                     priorityStrInit, flagInit,
			                                     descInit, labelInit, label5Init,
			                                     label2Init, templateInit);
			        
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
		public List<UIEntry> entries;
		public UiEntryContext uiEntry;
		public TerminalNode EOF() { return getToken(UIEntryParser.EOF, 0); }
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
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryListener ) ((UIEntryListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryVisitor ) return ((UIEntryVisitor<? extends T>)visitor).visitFile(this);
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
			setState(167); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(164);
				((FileContext)_localctx).uiEntry = uiEntry();
				 _localctx.entries.add(((FileContext)_localctx).uiEntry.entry); 
				}
				}
				setState(169); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(171);
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
		"\u0004\u0001\u0013\u00ae\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0003\u0001(\b\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0003\u0005;\b\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t"+
		"\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r"+
		"\u0001\r\u0003\r^\b\r\u0001\r\u0001\r\u0001\r\u0003\rc\b\r\u0001\r\u0001"+
		"\r\u0001\r\u0003\rh\b\r\u0001\r\u0001\r\u0001\r\u0003\rm\b\r\u0001\r\u0001"+
		"\r\u0001\r\u0004\rr\b\r\u000b\r\f\rs\u0003\rv\b\r\u0001\r\u0001\r\u0001"+
		"\r\u0003\r{\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u0080\b\r\u0001\r\u0001"+
		"\r\u0001\r\u0003\r\u0085\b\r\u0001\r\u0001\r\u0001\r\u0005\r\u008a\b\r"+
		"\n\r\f\r\u008d\t\r\u0001\r\u0001\r\u0001\r\u0004\r\u0092\b\r\u000b\r\f"+
		"\r\u0093\u0003\r\u0096\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u009b\b\r\u0001"+
		"\r\u0001\r\u0001\r\u0005\r\u00a0\b\r\n\r\f\r\u00a3\t\r\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0004\u000e\u00a8\b\u000e\u000b\u000e\f\u000e\u00a9"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0000\u0000\u000f\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u0000\u0000"+
		"\u00af\u0000\u001e\u0001\u0000\u0000\u0000\u0002!\u0001\u0000\u0000\u0000"+
		"\u0004)\u0001\u0000\u0000\u0000\u0006-\u0001\u0000\u0000\u0000\b1\u0001"+
		"\u0000\u0000\u0000\n5\u0001\u0000\u0000\u0000\f<\u0001\u0000\u0000\u0000"+
		"\u000e@\u0001\u0000\u0000\u0000\u0010D\u0001\u0000\u0000\u0000\u0012H"+
		"\u0001\u0000\u0000\u0000\u0014L\u0001\u0000\u0000\u0000\u0016P\u0001\u0000"+
		"\u0000\u0000\u0018T\u0001\u0000\u0000\u0000\u001aX\u0001\u0000\u0000\u0000"+
		"\u001c\u00a7\u0001\u0000\u0000\u0000\u001e\u001f\u0005\u0013\u0000\u0000"+
		"\u001f \u0006\u0000\uffff\uffff\u0000 \u0001\u0001\u0000\u0000\u0000!"+
		"\"\u0005\u0003\u0000\u0000\"#\u0005\u0012\u0000\u0000#\'\u0006\u0001\uffff"+
		"\uffff\u0000$%\u0003\u0000\u0000\u0000%&\u0006\u0001\uffff\uffff\u0000"+
		"&(\u0001\u0000\u0000\u0000\'$\u0001\u0000\u0000\u0000\'(\u0001\u0000\u0000"+
		"\u0000(\u0003\u0001\u0000\u0000\u0000)*\u0005\u0004\u0000\u0000*+\u0005"+
		"\u0005\u0000\u0000+,\u0006\u0002\uffff\uffff\u0000,\u0005\u0001\u0000"+
		"\u0000\u0000-.\u0005\u0006\u0000\u0000./\u0005\u0012\u0000\u0000/0\u0006"+
		"\u0003\uffff\uffff\u00000\u0007\u0001\u0000\u0000\u000012\u0005\u0007"+
		"\u0000\u000023\u0005\u0012\u0000\u000034\u0006\u0004\uffff\uffff\u0000"+
		"4\t\u0001\u0000\u0000\u00005:\u0005\b\u0000\u000067\u0005\u0010\u0000"+
		"\u00007;\u0006\u0005\uffff\uffff\u000089\u0005\u0011\u0000\u00009;\u0006"+
		"\u0005\uffff\uffff\u0000:6\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000"+
		"\u0000;\u000b\u0001\u0000\u0000\u0000<=\u0005\t\u0000\u0000=>\u0005\u0012"+
		"\u0000\u0000>?\u0006\u0006\uffff\uffff\u0000?\r\u0001\u0000\u0000\u0000"+
		"@A\u0005\n\u0000\u0000AB\u0005\u0012\u0000\u0000BC\u0006\u0007\uffff\uffff"+
		"\u0000C\u000f\u0001\u0000\u0000\u0000DE\u0005\u000b\u0000\u0000EF\u0005"+
		"\u0012\u0000\u0000FG\u0006\b\uffff\uffff\u0000G\u0011\u0001\u0000\u0000"+
		"\u0000HI\u0005\f\u0000\u0000IJ\u0005\u0012\u0000\u0000JK\u0006\t\uffff"+
		"\uffff\u0000K\u0013\u0001\u0000\u0000\u0000LM\u0005\r\u0000\u0000MN\u0005"+
		"\u0012\u0000\u0000NO\u0006\n\uffff\uffff\u0000O\u0015\u0001\u0000\u0000"+
		"\u0000PQ\u0005\u000e\u0000\u0000QR\u0005\u0012\u0000\u0000RS\u0006\u000b"+
		"\uffff\uffff\u0000S\u0017\u0001\u0000\u0000\u0000TU\u0005\u000f\u0000"+
		"\u0000UV\u0005\u0012\u0000\u0000VW\u0006\f\uffff\uffff\u0000W\u0019\u0001"+
		"\u0000\u0000\u0000XY\u0003\u0002\u0001\u0000Y]\u0006\r\uffff\uffff\u0000"+
		"Z[\u0003\u0018\f\u0000[\\\u0006\r\uffff\uffff\u0000\\^\u0001\u0000\u0000"+
		"\u0000]Z\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^b\u0001\u0000"+
		"\u0000\u0000_`\u0003\u0012\t\u0000`a\u0006\r\uffff\uffff\u0000ac\u0001"+
		"\u0000\u0000\u0000b_\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000"+
		"cg\u0001\u0000\u0000\u0000de\u0003\u0014\n\u0000ef\u0006\r\uffff\uffff"+
		"\u0000fh\u0001\u0000\u0000\u0000gd\u0001\u0000\u0000\u0000gh\u0001\u0000"+
		"\u0000\u0000hl\u0001\u0000\u0000\u0000ij\u0003\u0016\u000b\u0000jk\u0006"+
		"\r\uffff\uffff\u0000km\u0001\u0000\u0000\u0000li\u0001\u0000\u0000\u0000"+
		"lm\u0001\u0000\u0000\u0000mu\u0001\u0000\u0000\u0000no\u0003\f\u0006\u0000"+
		"op\u0006\r\uffff\uffff\u0000pr\u0001\u0000\u0000\u0000qn\u0001\u0000\u0000"+
		"\u0000rs\u0001\u0000\u0000\u0000sq\u0001\u0000\u0000\u0000st\u0001\u0000"+
		"\u0000\u0000tv\u0001\u0000\u0000\u0000uq\u0001\u0000\u0000\u0000uv\u0001"+
		"\u0000\u0000\u0000vz\u0001\u0000\u0000\u0000wx\u0003\u0004\u0002\u0000"+
		"xy\u0006\r\uffff\uffff\u0000y{\u0001\u0000\u0000\u0000zw\u0001\u0000\u0000"+
		"\u0000z{\u0001\u0000\u0000\u0000{\u007f\u0001\u0000\u0000\u0000|}\u0003"+
		"\u0006\u0003\u0000}~\u0006\r\uffff\uffff\u0000~\u0080\u0001\u0000\u0000"+
		"\u0000\u007f|\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000"+
		"\u0080\u0084\u0001\u0000\u0000\u0000\u0081\u0082\u0003\b\u0004\u0000\u0082"+
		"\u0083\u0006\r\uffff\uffff\u0000\u0083\u0085\u0001\u0000\u0000\u0000\u0084"+
		"\u0081\u0001\u0000\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085"+
		"\u008b\u0001\u0000\u0000\u0000\u0086\u0087\u0003\n\u0005\u0000\u0087\u0088"+
		"\u0006\r\uffff\uffff\u0000\u0088\u008a\u0001\u0000\u0000\u0000\u0089\u0086"+
		"\u0001\u0000\u0000\u0000\u008a\u008d\u0001\u0000\u0000\u0000\u008b\u0089"+
		"\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u0095"+
		"\u0001\u0000\u0000\u0000\u008d\u008b\u0001\u0000\u0000\u0000\u008e\u008f"+
		"\u0003\f\u0006\u0000\u008f\u0090\u0006\r\uffff\uffff\u0000\u0090\u0092"+
		"\u0001\u0000\u0000\u0000\u0091\u008e\u0001\u0000\u0000\u0000\u0092\u0093"+
		"\u0001\u0000\u0000\u0000\u0093\u0091\u0001\u0000\u0000\u0000\u0093\u0094"+
		"\u0001\u0000\u0000\u0000\u0094\u0096\u0001\u0000\u0000\u0000\u0095\u0091"+
		"\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u009a"+
		"\u0001\u0000\u0000\u0000\u0097\u0098\u0003\u000e\u0007\u0000\u0098\u0099"+
		"\u0006\r\uffff\uffff\u0000\u0099\u009b\u0001\u0000\u0000\u0000\u009a\u0097"+
		"\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u00a1"+
		"\u0001\u0000\u0000\u0000\u009c\u009d\u0003\u0010\b\u0000\u009d\u009e\u0006"+
		"\r\uffff\uffff\u0000\u009e\u00a0\u0001\u0000\u0000\u0000\u009f\u009c\u0001"+
		"\u0000\u0000\u0000\u00a0\u00a3\u0001\u0000\u0000\u0000\u00a1\u009f\u0001"+
		"\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u001b\u0001"+
		"\u0000\u0000\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a4\u00a5\u0003"+
		"\u001a\r\u0000\u00a5\u00a6\u0006\u000e\uffff\uffff\u0000\u00a6\u00a8\u0001"+
		"\u0000\u0000\u0000\u00a7\u00a4\u0001\u0000\u0000\u0000\u00a8\u00a9\u0001"+
		"\u0000\u0000\u0000\u00a9\u00a7\u0001\u0000\u0000\u0000\u00a9\u00aa\u0001"+
		"\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000\u00ab\u00ac\u0005"+
		"\u0000\u0000\u0001\u00ac\u001d\u0001\u0000\u0000\u0000\u0011\':]bglsu"+
		"z\u007f\u0084\u008b\u0093\u0095\u009a\u00a1\u00a9";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}