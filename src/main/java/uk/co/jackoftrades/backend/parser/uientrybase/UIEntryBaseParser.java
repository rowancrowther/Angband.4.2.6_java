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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryBase.g4 by ANTLR 4.13.2

package uk.co.jackoftrades.backend.parser.uientrybase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryBaseParser extends Parser {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			COMMENT = 1, EOL = 2, NAME = 3, RENDERER = 4, COMBINE = 5, CATEGORY = 6, FLAGS = 7,
			DESC = 8, LCASEWORD = 9, UCASEWORD = 10, TEXT = 11;
	public static final int
			RULE_name = 0, RULE_renderer = 1, RULE_combine = 2, RULE_category = 3,
			RULE_flags = 4, RULE_desc = 5, RULE_entryBase = 6, RULE_file = 7;
	private static String[] makeRuleNames() {
		return new String[]{
				"name", "renderer", "combine", "category", "flags", "desc", "entryBase",
				"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'name:'", "'renderer:'", "'combine:'", "'category:'",
				"'flags:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS",
				"DESC", "LCASEWORD", "UCASEWORD", "TEXT"
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
	public String getGrammarFileName() {
		return "UIEntryBase.g4";
	}

	@Override
	public String[] getRuleNames() {
		return ruleNames;
	}

	@Override
	public String getSerializedATN() {
		return _serializedATN;
	}

	@Override
	public ATN getATN() {
		return _ATN;
	}

	public UIEntryBaseParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token LCASEWORD;

		public TerminalNode NAME() {
			return getToken(UIEntryBaseParser.NAME, 0);
		}

		public TerminalNode LCASEWORD() {
			return getToken(UIEntryBaseParser.LCASEWORD, 0);
		}
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_name;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof UIEntryBaseVisitor)
				return ((UIEntryBaseVisitor<? extends T>) visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(16);
				match(NAME);
				setState(17);
				((NameContext) _localctx).LCASEWORD = match(LCASEWORD);
				((NameContext) _localctx).nameStr = ((NameContext) _localctx).LCASEWORD.getText();
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RendererContext extends ParserRuleContext {
		public UIEntryRenderer entryRenderer;
		public Token LCASEWORD;

		public TerminalNode RENDERER() {
			return getToken(UIEntryBaseParser.RENDERER, 0);
		}

		public TerminalNode LCASEWORD() {
			return getToken(UIEntryBaseParser.LCASEWORD, 0);
		}
		public RendererContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_renderer;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterRenderer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitRenderer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof UIEntryBaseVisitor)
				return ((UIEntryBaseVisitor<? extends T>) visitor).visitRenderer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RendererContext renderer() throws RecognitionException {
		RendererContext _localctx = new RendererContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_renderer);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(20);
				match(RENDERER);
				setState(21);
				((RendererContext) _localctx).LCASEWORD = match(LCASEWORD);

				((RendererContext) _localctx).entryRenderer = GameConstants.getUIEntryRenderer(((RendererContext) _localctx).LCASEWORD.getText());
			            
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CombineContext extends ParserRuleContext {
		public CombinerName combinerEnum;
		public Token UCASEWORD;

		public TerminalNode COMBINE() {
			return getToken(UIEntryBaseParser.COMBINE, 0);
		}

		public TerminalNode UCASEWORD() {
			return getToken(UIEntryBaseParser.UCASEWORD, 0);
		}
		public CombineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_combine;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterCombine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitCombine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof UIEntryBaseVisitor)
				return ((UIEntryBaseVisitor<? extends T>) visitor).visitCombine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombineContext combine() throws RecognitionException {
		CombineContext _localctx = new CombineContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_combine);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(24);
				match(COMBINE);
				setState(25);
				((CombineContext) _localctx).UCASEWORD = match(UCASEWORD);

				((CombineContext) _localctx).combinerEnum = CombinerName.valueOf(((CombineContext) _localctx).UCASEWORD.getText());
			            
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CategoryContext extends ParserRuleContext {
		public String categoryStr;
		public Token UCASEWORD;
		public Token LCASEWORD;

		public TerminalNode CATEGORY() {
			return getToken(UIEntryBaseParser.CATEGORY, 0);
		}

		public TerminalNode UCASEWORD() {
			return getToken(UIEntryBaseParser.UCASEWORD, 0);
		}

		public TerminalNode LCASEWORD() {
			return getToken(UIEntryBaseParser.LCASEWORD, 0);
		}
		public CategoryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_category;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterCategory(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitCategory(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof UIEntryBaseVisitor)
				return ((UIEntryBaseVisitor<? extends T>) visitor).visitCategory(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CategoryContext category() throws RecognitionException {
		CategoryContext _localctx = new CategoryContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_category);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(28);
				match(CATEGORY);
				setState(33);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
					case UCASEWORD: {
						setState(29);
						((CategoryContext) _localctx).UCASEWORD = match(UCASEWORD);
						((CategoryContext) _localctx).categoryStr = ((CategoryContext) _localctx).UCASEWORD.getText();
					}
					break;
					case LCASEWORD: {
						setState(31);
						((CategoryContext) _localctx).LCASEWORD = match(LCASEWORD);
						((CategoryContext) _localctx).categoryStr = ((CategoryContext) _localctx).LCASEWORD.getText();
					}
					break;
					default:
						throw new NoViableAltException(this);
				}
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FlagsContext extends ParserRuleContext {
		public String flagsStr;
		public Token UCASEWORD;

		public TerminalNode FLAGS() {
			return getToken(UIEntryBaseParser.FLAGS, 0);
		}

		public TerminalNode UCASEWORD() {
			return getToken(UIEntryBaseParser.UCASEWORD, 0);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_flags;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof UIEntryBaseVisitor)
				return ((UIEntryBaseVisitor<? extends T>) visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_flags);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(35);
				match(FLAGS);
				setState(36);
				((FlagsContext) _localctx).UCASEWORD = match(UCASEWORD);

				((FlagsContext) _localctx).flagsStr = ((FlagsContext) _localctx).UCASEWORD.getText();
			            
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DescContext extends ParserRuleContext {
		public String descStr;
		public Token TEXT;

		public TerminalNode DESC() {
			return getToken(UIEntryBaseParser.DESC, 0);
		}

		public TerminalNode TEXT() {
			return getToken(UIEntryBaseParser.TEXT, 0);
		}
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_desc;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof UIEntryBaseVisitor)
				return ((UIEntryBaseVisitor<? extends T>) visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(39);
				match(DESC);
				setState(40);
				((DescContext) _localctx).TEXT = match(TEXT);
				((DescContext) _localctx).descStr = ((DescContext) _localctx).TEXT.getText();
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EntryBaseContext extends ParserRuleContext {
		public UIEntryBase base;
		public NameContext name;
		public RendererContext renderer;
		public CombineContext combine;
		public CategoryContext category;
		public FlagsContext flags;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class, 0);
		}
		public RendererContext renderer() {
			return getRuleContext(RendererContext.class, 0);
		}
		public CombineContext combine() {
			return getRuleContext(CombineContext.class, 0);
		}
		public FlagsContext flags() {
			return getRuleContext(FlagsContext.class, 0);
		}
		public List<CategoryContext> category() {
			return getRuleContexts(CategoryContext.class);
		}
		public CategoryContext category(int i) {
			return getRuleContext(CategoryContext.class, i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class, i);
		}
		public EntryBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_entryBase;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterEntryBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitEntryBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof UIEntryBaseVisitor)
				return ((UIEntryBaseVisitor<? extends T>) visitor).visitEntryBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntryBaseContext entryBase() throws RecognitionException {
		EntryBaseContext _localctx = new EntryBaseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_entryBase);

		String nameInit = "";
		UIEntryRenderer rendererInit = null;
		CombinerName combinerInit = CombinerName.NONE;
		List<String> categoryInit = new ArrayList<>();
		String flagsInit = "";
		String descInit = "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(43);
				((EntryBaseContext) _localctx).name = name();

				nameInit = ((EntryBaseContext) _localctx).name.nameStr;

				setState(45);
				((EntryBaseContext) _localctx).renderer = renderer();

				rendererInit = ((EntryBaseContext) _localctx).renderer.entryRenderer;

				setState(47);
				((EntryBaseContext) _localctx).combine = combine();

				combinerInit = ((EntryBaseContext) _localctx).combine.combinerEnum;

				setState(52);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						{
							setState(49);
							((EntryBaseContext) _localctx).category = category();

							categoryInit.add(((EntryBaseContext) _localctx).category.categoryStr);

						}
					}
					setState(54);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while (_la == CATEGORY);
				setState(56);
				((EntryBaseContext) _localctx).flags = flags();

				flagsInit = ((EntryBaseContext) _localctx).flags.flagsStr;

				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						{
							setState(58);
							((EntryBaseContext) _localctx).desc = desc();

							descInit = descInit + ((EntryBaseContext) _localctx).desc.descStr;

						}
					}
					setState(63);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while (_la == DESC);
			}
			_ctx.stop = _input.LT(-1);

			((EntryBaseContext) _localctx).base = new UIEntryBase(nameInit, rendererInit,
					combinerInit, categoryInit,
					flagsInit, descInit);

		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FileContext extends ParserRuleContext {
		public List<UIEntryBase> uiEntryBases;
		public EntryBaseContext entryBase;

		public TerminalNode EOF() {
			return getToken(UIEntryBaseParser.EOF, 0);
		}
		public List<EntryBaseContext> entryBase() {
			return getRuleContexts(EntryBaseContext.class);
		}
		public EntryBaseContext entryBase(int i) {
			return getRuleContext(EntryBaseContext.class, i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_file;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof UIEntryBaseVisitor)
				return ((UIEntryBaseVisitor<? extends T>) visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_file);

		((FileContext) _localctx).uiEntryBases = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						{
							setState(65);
							((FileContext) _localctx).entryBase = entryBase();

							_localctx.uiEntryBases.add(((FileContext) _localctx).entryBase.base);

						}
					}
					setState(70);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while (_la == NAME);
				setState(72);
				match(EOF);
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
			"\u0004\u0001\u000bK\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
					"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
					"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001" +
					"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
					"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
					"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003\"\b" +
					"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001" +
					"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
					"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0004" +
					"\u00065\b\u0006\u000b\u0006\f\u00066\u0001\u0006\u0001\u0006\u0001\u0006" +
					"\u0001\u0006\u0001\u0006\u0004\u0006>\b\u0006\u000b\u0006\f\u0006?\u0001" +
					"\u0007\u0001\u0007\u0001\u0007\u0004\u0007E\b\u0007\u000b\u0007\f\u0007" +
					"F\u0001\u0007\u0001\u0007\u0001\u0007\u0000\u0000\b\u0000\u0002\u0004" +
					"\u0006\b\n\f\u000e\u0000\u0000F\u0000\u0010\u0001\u0000\u0000\u0000\u0002" +
					"\u0014\u0001\u0000\u0000\u0000\u0004\u0018\u0001\u0000\u0000\u0000\u0006" +
					"\u001c\u0001\u0000\u0000\u0000\b#\u0001\u0000\u0000\u0000\n\'\u0001\u0000" +
					"\u0000\u0000\f+\u0001\u0000\u0000\u0000\u000eD\u0001\u0000\u0000\u0000" +
					"\u0010\u0011\u0005\u0003\u0000\u0000\u0011\u0012\u0005\t\u0000\u0000\u0012" +
					"\u0013\u0006\u0000\uffff\uffff\u0000\u0013\u0001\u0001\u0000\u0000\u0000" +
					"\u0014\u0015\u0005\u0004\u0000\u0000\u0015\u0016\u0005\t\u0000\u0000\u0016" +
					"\u0017\u0006\u0001\uffff\uffff\u0000\u0017\u0003\u0001\u0000\u0000\u0000" +
					"\u0018\u0019\u0005\u0005\u0000\u0000\u0019\u001a\u0005\n\u0000\u0000\u001a" +
					"\u001b\u0006\u0002\uffff\uffff\u0000\u001b\u0005\u0001\u0000\u0000\u0000" +
					"\u001c!\u0005\u0006\u0000\u0000\u001d\u001e\u0005\n\u0000\u0000\u001e" +
					"\"\u0006\u0003\uffff\uffff\u0000\u001f \u0005\t\u0000\u0000 \"\u0006\u0003" +
					"\uffff\uffff\u0000!\u001d\u0001\u0000\u0000\u0000!\u001f\u0001\u0000\u0000" +
					"\u0000\"\u0007\u0001\u0000\u0000\u0000#$\u0005\u0007\u0000\u0000$%\u0005" +
					"\n\u0000\u0000%&\u0006\u0004\uffff\uffff\u0000&\t\u0001\u0000\u0000\u0000" +
					"\'(\u0005\b\u0000\u0000()\u0005\u000b\u0000\u0000)*\u0006\u0005\uffff" +
					"\uffff\u0000*\u000b\u0001\u0000\u0000\u0000+,\u0003\u0000\u0000\u0000" +
					",-\u0006\u0006\uffff\uffff\u0000-.\u0003\u0002\u0001\u0000./\u0006\u0006" +
					"\uffff\uffff\u0000/0\u0003\u0004\u0002\u000004\u0006\u0006\uffff\uffff" +
					"\u000012\u0003\u0006\u0003\u000023\u0006\u0006\uffff\uffff\u000035\u0001" +
					"\u0000\u0000\u000041\u0001\u0000\u0000\u000056\u0001\u0000\u0000\u0000" +
					"64\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u000078\u0001\u0000\u0000" +
					"\u000089\u0003\b\u0004\u00009=\u0006\u0006\uffff\uffff\u0000:;\u0003\n" +
					"\u0005\u0000;<\u0006\u0006\uffff\uffff\u0000<>\u0001\u0000\u0000\u0000" +
					"=:\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?=\u0001\u0000\u0000" +
					"\u0000?@\u0001\u0000\u0000\u0000@\r\u0001\u0000\u0000\u0000AB\u0003\f" +
					"\u0006\u0000BC\u0006\u0007\uffff\uffff\u0000CE\u0001\u0000\u0000\u0000" +
					"DA\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000" +
					"\u0000FG\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000HI\u0005\u0000" +
					"\u0000\u0001I\u000f\u0001\u0000\u0000\u0000\u0004!6?F";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}