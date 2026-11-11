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

// Generated from TrapGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.trap;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftradesltd.backend.parser.trap.TrapParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class TrapGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, APPEAR=2, VISIBILITY=3, DESC=4, FLAGS=5, MSG=6, SAVE=7, MSG_GOOD=8, 
		MSG_BAD=9, EFFECT_XTRA=10, EFFECT_YX_XTRA=11, DICE_XTRA=12, EXPR_XTRA=13, 
		MSG_XTRA=14, COMMENT=15, INDENT=16, EOL=17, EFFECT=18, EFFECT_MESSAGE=19, 
		DICE=20, TIME=21, EFFECT_YX=22, EXPR=23, COLON=24, UCASE=25, INTEGER=26, 
		SIMPLE_DICE_STRING=27, COMPLEX_DICE_STRING=28, GRAPHICS=29, COLOUR_ONLY=30, 
		GLYPH_ONLY=31, EXPR_XTRA_CHAR=32, EXPR_XTRA_COLON=33, EXPR_XTRA_UCASE=34, 
		EXPR_XTRA_OP=35, EXPR_XTRA_EOL=36, DELIMITER_COLON=37, DELIMITED_INTEGER=38, 
		DELIMITED_STRING=39, DELIMITED_EOL=40, STRING=41, REST_OF_LINE_EOL=42, 
		FLAG=43, FLAG_OR=44, FLAG_EOL=45, FREE_TEXT=46, DICE_SIMPLE_VALUE=47, 
		DICE_COMPLEX_VALUE=48, EXPR_CHAR=49, EXPR_COLON=50, EXPR_UCASE=51, EXPR_OP=52, 
		EXPR_EOL=53, COLOUR_VALUES=54, GLYPH_VALUES=55, GLYPH_COLON_SWITCH=56, 
		GLYPH_EOL=57;
	public static final int
		RULE_name = 0, RULE_graphics = 1, RULE_appear = 2, RULE_visibility = 3, 
		RULE_desc = 4, RULE_flags = 5, RULE_msg = 6, RULE_save = 7, RULE_msgGood = 8, 
		RULE_msgBad = 9, RULE_msgXtra = 10, RULE_effectXtra = 11, RULE_diceXtra = 12, 
		RULE_exprXtra = 13, RULE_effectYxXtra = 14, RULE_effectXtraBlock = 15, 
		RULE_trapRecord = 16, RULE_file = 17, RULE_effect = 18, RULE_effectYX = 19, 
		RULE_dice = 20, RULE_expr = 21, RULE_effectMsg = 22, RULE_time = 23, RULE_strayEffectLine = 24, 
		RULE_effectBlock = 25;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "graphics", "appear", "visibility", "desc", "flags", "msg", "save", 
			"msgGood", "msgBad", "msgXtra", "effectXtra", "diceXtra", "exprXtra", 
			"effectYxXtra", "effectXtraBlock", "trapRecord", "file", "effect", "effectYX", 
			"dice", "expr", "effectMsg", "time", "strayEffectLine", "effectBlock"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'appear:'", "'visibility:'", "'desc:'", "'flags:'", 
			"'msg:'", "'save:'", "'msg-good:'", "'msg-bad:'", "'effect-xtra:'", "'effect-yx-xtra:'", 
			"'dice-xtra:'", "'expr-xtra:'", "'msg-xtra:'", null, null, null, "'effect:'", 
			"'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'", "'expr:'", null, 
			null, null, null, null, "'graphics:'", "'color:'", "'glyph:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "APPEAR", "VISIBILITY", "DESC", "FLAGS", "MSG", "SAVE", 
			"MSG_GOOD", "MSG_BAD", "EFFECT_XTRA", "EFFECT_YX_XTRA", "DICE_XTRA", 
			"EXPR_XTRA", "MSG_XTRA", "COMMENT", "INDENT", "EOL", "EFFECT", "EFFECT_MESSAGE", 
			"DICE", "TIME", "EFFECT_YX", "EXPR", "COLON", "UCASE", "INTEGER", "SIMPLE_DICE_STRING", 
			"COMPLEX_DICE_STRING", "GRAPHICS", "COLOUR_ONLY", "GLYPH_ONLY", "EXPR_XTRA_CHAR", 
			"EXPR_XTRA_COLON", "EXPR_XTRA_UCASE", "EXPR_XTRA_OP", "EXPR_XTRA_EOL", 
			"DELIMITER_COLON", "DELIMITED_INTEGER", "DELIMITED_STRING", "DELIMITED_EOL", 
			"STRING", "REST_OF_LINE_EOL", "FLAG", "FLAG_OR", "FLAG_EOL", "FREE_TEXT", 
			"DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR", "EXPR_COLON", 
			"EXPR_UCASE", "EXPR_OP", "EXPR_EOL", "COLOUR_VALUES", "GLYPH_VALUES", 
			"GLYPH_COLON_SWITCH", "GLYPH_EOL"
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
	public String getGrammarFileName() { return "TrapGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	    // EffectParseRecord is a record, so "updating" the last effect means
	    // swapping in a copy with one or more fields changed
	    void replaceLast(List<EffectParseRecord> list, EffectParseRecord updated) {
	        list.set(list.size() - 1, updated);
	    }

	public TrapGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public String textStr;
		public int line;
		public Token n;
		public Token t;
		public TerminalNode NAME() { return getToken(TrapGrammar.NAME, 0); }
		public TerminalNode DELIMITER_COLON() { return getToken(TrapGrammar.DELIMITER_COLON, 0); }
		public List<TerminalNode> DELIMITED_STRING() { return getTokens(TrapGrammar.DELIMITED_STRING); }
		public TerminalNode DELIMITED_STRING(int i) {
			return getToken(TrapGrammar.DELIMITED_STRING, i);
		}
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(NAME);
			setState(53);
			((NameContext)_localctx).n = match(DELIMITED_STRING);
			setState(54);
			match(DELIMITER_COLON);
			setState(55);
			((NameContext)_localctx).t = match(DELIMITED_STRING);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).n.getText(); ((NameContext)_localctx).textStr =  ((NameContext)_localctx).t.getText(); ((NameContext)_localctx).line =  _localctx.start.getLine(); 
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
		public String glyph;
		public String colour;
		public Token g;
		public Token c;
		public TerminalNode GRAPHICS() { return getToken(TrapGrammar.GRAPHICS, 0); }
		public TerminalNode GLYPH_COLON_SWITCH() { return getToken(TrapGrammar.GLYPH_COLON_SWITCH, 0); }
		public TerminalNode GLYPH_VALUES() { return getToken(TrapGrammar.GLYPH_VALUES, 0); }
		public TerminalNode COLOUR_VALUES() { return getToken(TrapGrammar.COLOUR_VALUES, 0); }
		public GraphicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterGraphics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitGraphics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitGraphics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GraphicsContext graphics() throws RecognitionException {
		GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_graphics);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(GRAPHICS);
			setState(59);
			((GraphicsContext)_localctx).g = match(GLYPH_VALUES);
			setState(60);
			match(GLYPH_COLON_SWITCH);
			setState(61);
			((GraphicsContext)_localctx).c = match(COLOUR_VALUES);

			            ((GraphicsContext)_localctx).glyph =  ((GraphicsContext)_localctx).g.getText(); ((GraphicsContext)_localctx).colour =  ((GraphicsContext)_localctx).c.getText(); 
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
	public static class AppearContext extends ParserRuleContext {
		public String rarity;
		public String minDepth;
		public String maxNum;
		public Token r;
		public Token n;
		public Token x;
		public TerminalNode APPEAR() { return getToken(TrapGrammar.APPEAR, 0); }
		public List<TerminalNode> DELIMITER_COLON() { return getTokens(TrapGrammar.DELIMITER_COLON); }
		public TerminalNode DELIMITER_COLON(int i) {
			return getToken(TrapGrammar.DELIMITER_COLON, i);
		}
		public List<TerminalNode> DELIMITED_INTEGER() { return getTokens(TrapGrammar.DELIMITED_INTEGER); }
		public TerminalNode DELIMITED_INTEGER(int i) {
			return getToken(TrapGrammar.DELIMITED_INTEGER, i);
		}
		public AppearContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_appear; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterAppear(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitAppear(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitAppear(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AppearContext appear() throws RecognitionException {
		AppearContext _localctx = new AppearContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_appear);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(APPEAR);
			setState(65);
			((AppearContext)_localctx).r = match(DELIMITED_INTEGER);
			setState(66);
			match(DELIMITER_COLON);
			setState(67);
			((AppearContext)_localctx).n = match(DELIMITED_INTEGER);
			setState(68);
			match(DELIMITER_COLON);
			setState(69);
			((AppearContext)_localctx).x = match(DELIMITED_INTEGER);

			                ((AppearContext)_localctx).rarity =  ((AppearContext)_localctx).r.getText();
			                ((AppearContext)_localctx).minDepth =  ((AppearContext)_localctx).n.getText();
			                ((AppearContext)_localctx).maxNum =  ((AppearContext)_localctx).x.getText();
			            
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
	public static class VisibilityContext extends ParserRuleContext {
		public String vis;
		public Token d;
		public TerminalNode VISIBILITY() { return getToken(TrapGrammar.VISIBILITY, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(TrapGrammar.DICE_SIMPLE_VALUE, 0); }
		public TerminalNode DICE_COMPLEX_VALUE() { return getToken(TrapGrammar.DICE_COMPLEX_VALUE, 0); }
		public VisibilityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_visibility; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterVisibility(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitVisibility(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitVisibility(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VisibilityContext visibility() throws RecognitionException {
		VisibilityContext _localctx = new VisibilityContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_visibility);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(VISIBILITY);
			setState(73);
			((VisibilityContext)_localctx).d = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==DICE_SIMPLE_VALUE || _la==DICE_COMPLEX_VALUE) ) {
				((VisibilityContext)_localctx).d = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			 ((VisibilityContext)_localctx).vis =  ((VisibilityContext)_localctx).d.getText(); 
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
		public TerminalNode DESC() { return getToken(TrapGrammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(TrapGrammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(DESC);
			setState(77);
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
	public static class FlagsContext extends ParserRuleContext {
		public List<String> flagList;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS() { return getToken(TrapGrammar.FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(TrapGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(TrapGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(TrapGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(TrapGrammar.FLAG_OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_flags);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((FlagsContext)_localctx).flagList =  new ArrayList<>(); 
			setState(81);
			match(FLAGS);
			setState(82);
			((FlagsContext)_localctx).f1 = match(FLAG);
			 _localctx.flagList.add(((FlagsContext)_localctx).f1.getText()); 
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(84);
				match(FLAG_OR);
				setState(85);
				((FlagsContext)_localctx).f2 = match(FLAG);
				 _localctx.flagList.add(((FlagsContext)_localctx).f2.getText()); 
				}
				}
				setState(91);
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
	public static class MsgContext extends ParserRuleContext {
		public String msgStr;
		public Token STRING;
		public TerminalNode MSG() { return getToken(TrapGrammar.MSG, 0); }
		public TerminalNode STRING() { return getToken(TrapGrammar.STRING, 0); }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			match(MSG);
			setState(93);
			((MsgContext)_localctx).STRING = match(STRING);
			 ((MsgContext)_localctx).msgStr =  ((MsgContext)_localctx).STRING.getText(); 
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
	public static class SaveContext extends ParserRuleContext {
		public List<String> saveFlags;
		public Token f1;
		public Token f2;
		public TerminalNode SAVE() { return getToken(TrapGrammar.SAVE, 0); }
		public List<TerminalNode> FLAG() { return getTokens(TrapGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(TrapGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(TrapGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(TrapGrammar.FLAG_OR, i);
		}
		public SaveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_save; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterSave(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitSave(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitSave(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SaveContext save() throws RecognitionException {
		SaveContext _localctx = new SaveContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_save);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((SaveContext)_localctx).saveFlags =  new ArrayList<>(); 
			setState(97);
			match(SAVE);
			setState(98);
			((SaveContext)_localctx).f1 = match(FLAG);
			 _localctx.saveFlags.add(((SaveContext)_localctx).f1.getText()); 
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(100);
				match(FLAG_OR);
				setState(101);
				((SaveContext)_localctx).f2 = match(FLAG);
				 _localctx.saveFlags.add(((SaveContext)_localctx).f2.getText()); 
				}
				}
				setState(107);
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
	public static class MsgGoodContext extends ParserRuleContext {
		public String goodMsg;
		public Token STRING;
		public TerminalNode MSG_GOOD() { return getToken(TrapGrammar.MSG_GOOD, 0); }
		public TerminalNode STRING() { return getToken(TrapGrammar.STRING, 0); }
		public MsgGoodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msgGood; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterMsgGood(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitMsgGood(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitMsgGood(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgGoodContext msgGood() throws RecognitionException {
		MsgGoodContext _localctx = new MsgGoodContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_msgGood);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			match(MSG_GOOD);
			setState(109);
			((MsgGoodContext)_localctx).STRING = match(STRING);
			 ((MsgGoodContext)_localctx).goodMsg =  ((MsgGoodContext)_localctx).STRING.getText(); 
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
	public static class MsgBadContext extends ParserRuleContext {
		public String badMsg;
		public Token STRING;
		public TerminalNode MSG_BAD() { return getToken(TrapGrammar.MSG_BAD, 0); }
		public TerminalNode STRING() { return getToken(TrapGrammar.STRING, 0); }
		public MsgBadContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msgBad; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterMsgBad(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitMsgBad(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitMsgBad(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgBadContext msgBad() throws RecognitionException {
		MsgBadContext _localctx = new MsgBadContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_msgBad);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			match(MSG_BAD);
			setState(113);
			((MsgBadContext)_localctx).STRING = match(STRING);
			 ((MsgBadContext)_localctx).badMsg =  ((MsgBadContext)_localctx).STRING.getText(); 
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
	public static class MsgXtraContext extends ParserRuleContext {
		public String msgExtra;
		public Token STRING;
		public TerminalNode MSG_XTRA() { return getToken(TrapGrammar.MSG_XTRA, 0); }
		public TerminalNode STRING() { return getToken(TrapGrammar.STRING, 0); }
		public MsgXtraContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msgXtra; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterMsgXtra(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitMsgXtra(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitMsgXtra(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgXtraContext msgXtra() throws RecognitionException {
		MsgXtraContext _localctx = new MsgXtraContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_msgXtra);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			match(MSG_XTRA);
			setState(117);
			((MsgXtraContext)_localctx).STRING = match(STRING);
			 ((MsgXtraContext)_localctx).msgExtra =  ((MsgXtraContext)_localctx).STRING.getText(); 
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
	public static class EffectXtraContext extends ParserRuleContext {
		public String effectType;
		public String effectSubtype;
		public String radius;
		public String parameter;
		public Token t;
		public Token s;
		public Token r;
		public Token p;
		public TerminalNode EFFECT_XTRA() { return getToken(TrapGrammar.EFFECT_XTRA, 0); }
		public List<TerminalNode> UCASE() { return getTokens(TrapGrammar.UCASE); }
		public TerminalNode UCASE(int i) {
			return getToken(TrapGrammar.UCASE, i);
		}
		public List<TerminalNode> COLON() { return getTokens(TrapGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(TrapGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(TrapGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(TrapGrammar.INTEGER, i);
		}
		public EffectXtraContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectXtra; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterEffectXtra(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitEffectXtra(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitEffectXtra(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectXtraContext effectXtra() throws RecognitionException {
		EffectXtraContext _localctx = new EffectXtraContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_effectXtra);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((EffectXtraContext)_localctx).effectSubtype =  ""; ((EffectXtraContext)_localctx).radius =  ""; ((EffectXtraContext)_localctx).parameter =  ""; 
			setState(121);
			match(EFFECT_XTRA);
			setState(122);
			((EffectXtraContext)_localctx).t = match(UCASE);
			 ((EffectXtraContext)_localctx).effectType =  ((EffectXtraContext)_localctx).t.getText(); 
			setState(137);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(124);
				match(COLON);
				setState(125);
				((EffectXtraContext)_localctx).s = match(UCASE);
				 ((EffectXtraContext)_localctx).effectSubtype =  ((EffectXtraContext)_localctx).s.getText(); 
				setState(135);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(127);
					match(COLON);
					setState(128);
					((EffectXtraContext)_localctx).r = match(INTEGER);
					 ((EffectXtraContext)_localctx).radius =  ((EffectXtraContext)_localctx).r.getText(); 
					setState(133);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COLON) {
						{
						setState(130);
						match(COLON);
						setState(131);
						((EffectXtraContext)_localctx).p = match(INTEGER);
						 ((EffectXtraContext)_localctx).parameter =  ((EffectXtraContext)_localctx).p.getText(); 
						}
					}

					}
				}

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
	public static class DiceXtraContext extends ParserRuleContext {
		public String complexDiceValue;
		public Token d;
		public TerminalNode DICE_XTRA() { return getToken(TrapGrammar.DICE_XTRA, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(TrapGrammar.DICE_SIMPLE_VALUE, 0); }
		public TerminalNode DICE_COMPLEX_VALUE() { return getToken(TrapGrammar.DICE_COMPLEX_VALUE, 0); }
		public DiceXtraContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_diceXtra; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterDiceXtra(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitDiceXtra(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitDiceXtra(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceXtraContext diceXtra() throws RecognitionException {
		DiceXtraContext _localctx = new DiceXtraContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_diceXtra);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(DICE_XTRA);
			setState(140);
			((DiceXtraContext)_localctx).d = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==DICE_SIMPLE_VALUE || _la==DICE_COMPLEX_VALUE) ) {
				((DiceXtraContext)_localctx).d = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			 ((DiceXtraContext)_localctx).complexDiceValue =  ((DiceXtraContext)_localctx).d.getText(); 
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
	public static class ExprXtraContext extends ParserRuleContext {
		public String exprChar;
		public String baseName;
		public String op;
		public Token EXPR_XTRA_CHAR;
		public Token EXPR_XTRA_UCASE;
		public Token EXPR_XTRA_OP;
		public TerminalNode EXPR_XTRA() { return getToken(TrapGrammar.EXPR_XTRA, 0); }
		public TerminalNode EXPR_XTRA_CHAR() { return getToken(TrapGrammar.EXPR_XTRA_CHAR, 0); }
		public List<TerminalNode> EXPR_XTRA_COLON() { return getTokens(TrapGrammar.EXPR_XTRA_COLON); }
		public TerminalNode EXPR_XTRA_COLON(int i) {
			return getToken(TrapGrammar.EXPR_XTRA_COLON, i);
		}
		public TerminalNode EXPR_XTRA_UCASE() { return getToken(TrapGrammar.EXPR_XTRA_UCASE, 0); }
		public TerminalNode EXPR_XTRA_OP() { return getToken(TrapGrammar.EXPR_XTRA_OP, 0); }
		public ExprXtraContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprXtra; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterExprXtra(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitExprXtra(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitExprXtra(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprXtraContext exprXtra() throws RecognitionException {
		ExprXtraContext _localctx = new ExprXtraContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_exprXtra);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(EXPR_XTRA);
			setState(144);
			((ExprXtraContext)_localctx).EXPR_XTRA_CHAR = match(EXPR_XTRA_CHAR);
			setState(145);
			match(EXPR_XTRA_COLON);
			setState(146);
			((ExprXtraContext)_localctx).EXPR_XTRA_UCASE = match(EXPR_XTRA_UCASE);
			setState(147);
			match(EXPR_XTRA_COLON);
			setState(148);
			((ExprXtraContext)_localctx).EXPR_XTRA_OP = match(EXPR_XTRA_OP);

			                ((ExprXtraContext)_localctx).exprChar =  ((ExprXtraContext)_localctx).EXPR_XTRA_CHAR.getText();
			                ((ExprXtraContext)_localctx).baseName =  ((ExprXtraContext)_localctx).EXPR_XTRA_UCASE.getText();
			                ((ExprXtraContext)_localctx).op =  ((ExprXtraContext)_localctx).EXPR_XTRA_OP.getText();
			            
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
	public static class EffectYxXtraContext extends ParserRuleContext {
		public String y;
		public String x;
		public Token yVal;
		public Token xVal;
		public TerminalNode EFFECT_YX_XTRA() { return getToken(TrapGrammar.EFFECT_YX_XTRA, 0); }
		public TerminalNode COLON() { return getToken(TrapGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(TrapGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(TrapGrammar.INTEGER, i);
		}
		public EffectYxXtraContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectYxXtra; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterEffectYxXtra(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitEffectYxXtra(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitEffectYxXtra(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectYxXtraContext effectYxXtra() throws RecognitionException {
		EffectYxXtraContext _localctx = new EffectYxXtraContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_effectYxXtra);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			match(EFFECT_YX_XTRA);
			setState(152);
			((EffectYxXtraContext)_localctx).yVal = match(INTEGER);
			setState(153);
			match(COLON);
			setState(154);
			((EffectYxXtraContext)_localctx).xVal = match(INTEGER);
			 ((EffectYxXtraContext)_localctx).y =  ((EffectYxXtraContext)_localctx).yVal.getText(); ((EffectYxXtraContext)_localctx).x =  ((EffectYxXtraContext)_localctx).xVal.getText(); 
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
	public static class EffectXtraBlockContext extends ParserRuleContext {
		public String effectType;
		public String effectSubtype;
		public String radius;
		public String parameter;
		public String complexDiceValue;
		public String exprChar;
		public String baseName;
		public String op;
		public String msgExtra;
		public String yVal;
		public String xVal;
		public EffectXtraContext effectXtra;
		public EffectXtraContext effectXtra() {
			return getRuleContext(EffectXtraContext.class,0);
		}
		public EffectXtraBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectXtraBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterEffectXtraBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitEffectXtraBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitEffectXtraBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectXtraBlockContext effectXtraBlock() throws RecognitionException {
		EffectXtraBlockContext _localctx = new EffectXtraBlockContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_effectXtraBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((EffectXtraBlockContext)_localctx).complexDiceValue =  ""; ((EffectXtraBlockContext)_localctx).exprChar =  ""; ((EffectXtraBlockContext)_localctx).baseName =  ""; ((EffectXtraBlockContext)_localctx).op =  ""; ((EffectXtraBlockContext)_localctx).msgExtra =  "";
			              ((EffectXtraBlockContext)_localctx).yVal =  ""; ((EffectXtraBlockContext)_localctx).xVal =  ""; 
			setState(158);
			((EffectXtraBlockContext)_localctx).effectXtra = effectXtra();
			 ((EffectXtraBlockContext)_localctx).effectType =  ((EffectXtraBlockContext)_localctx).effectXtra.effectType;
			                          ((EffectXtraBlockContext)_localctx).effectSubtype =  ((EffectXtraBlockContext)_localctx).effectXtra.effectSubtype;
			                          ((EffectXtraBlockContext)_localctx).radius =  ((EffectXtraBlockContext)_localctx).effectXtra.radius;
			                          ((EffectXtraBlockContext)_localctx).parameter =  ((EffectXtraBlockContext)_localctx).effectXtra.parameter; 
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
	public static class TrapRecordContext extends ParserRuleContext {
		public TrapParseRecord trap;
		public NameContext name;
		public GraphicsContext graphics;
		public AppearContext appear;
		public VisibilityContext visibility;
		public FlagsContext flags;
		public SaveContext save;
		public DescContext desc;
		public MsgContext msg;
		public MsgGoodContext msgGood;
		public MsgBadContext msgBad;
		public MsgXtraContext msgXtra;
		public EffectBlockContext effectBlock;
		public EffectYXContext effectYX;
		public DiceContext dice;
		public ExprContext expr;
		public EffectXtraBlockContext effectXtraBlock;
		public EffectYxXtraContext effectYxXtra;
		public DiceXtraContext diceXtra;
		public ExprXtraContext exprXtra;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<GraphicsContext> graphics() {
			return getRuleContexts(GraphicsContext.class);
		}
		public GraphicsContext graphics(int i) {
			return getRuleContext(GraphicsContext.class,i);
		}
		public List<AppearContext> appear() {
			return getRuleContexts(AppearContext.class);
		}
		public AppearContext appear(int i) {
			return getRuleContext(AppearContext.class,i);
		}
		public List<VisibilityContext> visibility() {
			return getRuleContexts(VisibilityContext.class);
		}
		public VisibilityContext visibility(int i) {
			return getRuleContext(VisibilityContext.class,i);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class,i);
		}
		public List<SaveContext> save() {
			return getRuleContexts(SaveContext.class);
		}
		public SaveContext save(int i) {
			return getRuleContext(SaveContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public List<MsgContext> msg() {
			return getRuleContexts(MsgContext.class);
		}
		public MsgContext msg(int i) {
			return getRuleContext(MsgContext.class,i);
		}
		public List<MsgGoodContext> msgGood() {
			return getRuleContexts(MsgGoodContext.class);
		}
		public MsgGoodContext msgGood(int i) {
			return getRuleContext(MsgGoodContext.class,i);
		}
		public List<MsgBadContext> msgBad() {
			return getRuleContexts(MsgBadContext.class);
		}
		public MsgBadContext msgBad(int i) {
			return getRuleContext(MsgBadContext.class,i);
		}
		public List<MsgXtraContext> msgXtra() {
			return getRuleContexts(MsgXtraContext.class);
		}
		public MsgXtraContext msgXtra(int i) {
			return getRuleContext(MsgXtraContext.class,i);
		}
		public List<EffectBlockContext> effectBlock() {
			return getRuleContexts(EffectBlockContext.class);
		}
		public EffectBlockContext effectBlock(int i) {
			return getRuleContext(EffectBlockContext.class,i);
		}
		public List<EffectYXContext> effectYX() {
			return getRuleContexts(EffectYXContext.class);
		}
		public EffectYXContext effectYX(int i) {
			return getRuleContext(EffectYXContext.class,i);
		}
		public List<DiceContext> dice() {
			return getRuleContexts(DiceContext.class);
		}
		public DiceContext dice(int i) {
			return getRuleContext(DiceContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<EffectXtraBlockContext> effectXtraBlock() {
			return getRuleContexts(EffectXtraBlockContext.class);
		}
		public EffectXtraBlockContext effectXtraBlock(int i) {
			return getRuleContext(EffectXtraBlockContext.class,i);
		}
		public List<EffectYxXtraContext> effectYxXtra() {
			return getRuleContexts(EffectYxXtraContext.class);
		}
		public EffectYxXtraContext effectYxXtra(int i) {
			return getRuleContext(EffectYxXtraContext.class,i);
		}
		public List<DiceXtraContext> diceXtra() {
			return getRuleContexts(DiceXtraContext.class);
		}
		public DiceXtraContext diceXtra(int i) {
			return getRuleContext(DiceXtraContext.class,i);
		}
		public List<ExprXtraContext> exprXtra() {
			return getRuleContexts(ExprXtraContext.class);
		}
		public ExprXtraContext exprXtra(int i) {
			return getRuleContext(ExprXtraContext.class,i);
		}
		public TrapRecordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trapRecord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterTrapRecord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitTrapRecord(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitTrapRecord(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TrapRecordContext trapRecord() throws RecognitionException {
		TrapRecordContext _localctx = new TrapRecordContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_trapRecord);

		            String nameInit = "";
		            String descInit = "";
		            StringBuilder messageSB = new StringBuilder();
		            StringBuilder saveMessageSB = new StringBuilder();
		            StringBuilder failMessageSB = new StringBuilder();
		            StringBuilder xtraMessageSB = new StringBuilder();
		            String glyphInit = "";
		            String colourInit = "";
		            String rarityInit = "";
		            String minDepthInit = "";
		            String maxNumInit = "";
		            String powerInit = "";
		            StringBuilder descSB = new StringBuilder();
		            List<String> flagsInit = new ArrayList<>();
		            List<String> saveFlagsInit = new ArrayList<>();
		            List<EffectParseRecord> effectInit = new ArrayList<>();
		            List<EffectParseRecord> xtraEffectInit = new ArrayList<>();
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			((TrapRecordContext)_localctx).name = name();
			 line = ((TrapRecordContext)_localctx).name.line; nameInit = ((TrapRecordContext)_localctx).name.nameStr; descInit = ((TrapRecordContext)_localctx).name.textStr; 
			setState(217); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(217);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case GRAPHICS:
					{
					setState(163);
					((TrapRecordContext)_localctx).graphics = graphics();
					 glyphInit = ((TrapRecordContext)_localctx).graphics.glyph; colourInit = ((TrapRecordContext)_localctx).graphics.colour; 
					}
					break;
				case APPEAR:
					{
					setState(166);
					((TrapRecordContext)_localctx).appear = appear();
					 rarityInit = ((TrapRecordContext)_localctx).appear.rarity;
					                     minDepthInit = ((TrapRecordContext)_localctx).appear.minDepth;
					                     maxNumInit = ((TrapRecordContext)_localctx).appear.maxNum; 
					}
					break;
				case VISIBILITY:
					{
					setState(169);
					((TrapRecordContext)_localctx).visibility = visibility();
					 powerInit = ((TrapRecordContext)_localctx).visibility.vis; 
					}
					break;
				case FLAGS:
					{
					setState(172);
					((TrapRecordContext)_localctx).flags = flags();
					 flagsInit.addAll(((TrapRecordContext)_localctx).flags.flagList); 
					}
					break;
				case SAVE:
					{
					setState(175);
					((TrapRecordContext)_localctx).save = save();
					 saveFlagsInit.addAll(((TrapRecordContext)_localctx).save.saveFlags); 
					}
					break;
				case DESC:
					{
					setState(178);
					((TrapRecordContext)_localctx).desc = desc();
					 descSB.append(((TrapRecordContext)_localctx).desc.line); 
					}
					break;
				case MSG:
					{
					setState(181);
					((TrapRecordContext)_localctx).msg = msg();
					 messageSB.append(((TrapRecordContext)_localctx).msg.msgStr); 
					}
					break;
				case MSG_GOOD:
					{
					setState(184);
					((TrapRecordContext)_localctx).msgGood = msgGood();
					 saveMessageSB.append(((TrapRecordContext)_localctx).msgGood.goodMsg); 
					}
					break;
				case MSG_BAD:
					{
					setState(187);
					((TrapRecordContext)_localctx).msgBad = msgBad();
					 failMessageSB.append(((TrapRecordContext)_localctx).msgBad.badMsg); 
					}
					break;
				case MSG_XTRA:
					{
					setState(190);
					((TrapRecordContext)_localctx).msgXtra = msgXtra();
					 xtraMessageSB.append(((TrapRecordContext)_localctx).msgXtra.msgExtra); 
					}
					break;
				case EFFECT:
					{
					setState(193);
					((TrapRecordContext)_localctx).effectBlock = effectBlock();
					 effectInit.add(new EffectParseRecord(((TrapRecordContext)_localctx).effectBlock.typeInit,
					                                        ((TrapRecordContext)_localctx).effectBlock.subtypeWrapperInit, ((TrapRecordContext)_localctx).effectBlock.radius, ((TrapRecordContext)_localctx).effectBlock.other,
					                                        ((TrapRecordContext)_localctx).effectBlock.diceString, ((TrapRecordContext)_localctx).effectBlock.yVal, ((TrapRecordContext)_localctx).effectBlock.xVal,
					                                        ((TrapRecordContext)_localctx).effectBlock.expressionChars, ((TrapRecordContext)_localctx).effectBlock.expressionBase,
					                                        ((TrapRecordContext)_localctx).effectBlock.expressionOperation, 
					                                        ((TrapRecordContext)_localctx).effectBlock.effectMessage, (((TrapRecordContext)_localctx).effectBlock!=null?(((TrapRecordContext)_localctx).effectBlock.start):null).getLine())); 
					}
					break;
				case EFFECT_YX:
					{
					setState(196);
					((TrapRecordContext)_localctx).effectYX = effectYX();
					 if (!effectInit.isEmpty()) {
					                           EffectParseRecord last = effectInit.getLast();
					                           EffectParseRecord updated = last.amendYX(((TrapRecordContext)_localctx).effectYX.y, ((TrapRecordContext)_localctx).effectYX.x);
					                           replaceLast(effectInit, updated);
					                        }
					            
					}
					break;
				case DICE:
					{
					setState(199);
					((TrapRecordContext)_localctx).dice = dice();

					                if (!effectInit.isEmpty()) {
					                    EffectParseRecord last = effectInit.getLast();
					                    EffectParseRecord updated = last.amendExpression(((TrapRecordContext)_localctx).dice.diceString,
					                                                                     ((TrapRecordContext)_localctx).dice.exprChar,
					                                                                     ((TrapRecordContext)_localctx).dice.baseName,
					                                                                     ((TrapRecordContext)_localctx).dice.operation);
					                    replaceLast(effectInit, updated);
					                }
					            
					}
					break;
				case EXPR:
					{
					setState(202);
					((TrapRecordContext)_localctx).expr = expr();

					                if (!effectInit.isEmpty() && (!effectInit.getLast().diceString().isEmpty())) {
					                    EffectParseRecord last = effectInit.getLast();
					                    String lastExprChar = last.expressionChars().isEmpty() ? ((TrapRecordContext)_localctx).expr.exprChar 
					                                                                           : last.expressionChars() + "^" + ((TrapRecordContext)_localctx).expr.exprChar;
					                    String lastExprBase = last.expressionBases().isEmpty() ? ((TrapRecordContext)_localctx).expr.baseName
					                                                                           : last.expressionBases() + "^" + ((TrapRecordContext)_localctx).expr.baseName;
					                    String lastExprOps = last.expressionOperations().isEmpty() ? ((TrapRecordContext)_localctx).expr.operation 
					                                                                           : last.expressionOperations() + "^" + ((TrapRecordContext)_localctx).expr.operation;
					                    String lastDiceString = last.diceString();
					                    EffectParseRecord updated = last.amendExpression(lastDiceString,
					                                                                     lastExprChar,
					                                                                     lastExprBase,
					                                                                     lastExprOps);
					                    replaceLast(effectInit, updated);
					                }
					            
					}
					break;
				case EFFECT_XTRA:
					{
					setState(205);
					((TrapRecordContext)_localctx).effectXtraBlock = effectXtraBlock();
					 xtraEffectInit.add(new EffectParseRecord(
					                                        ((TrapRecordContext)_localctx).effectXtraBlock.effectType, ((TrapRecordContext)_localctx).effectXtraBlock.effectSubtype,
					                                        ((TrapRecordContext)_localctx).effectXtraBlock.radius, ((TrapRecordContext)_localctx).effectXtraBlock.parameter,
					                                        ((TrapRecordContext)_localctx).effectXtraBlock.complexDiceValue,
					                                        ((TrapRecordContext)_localctx).effectXtraBlock.yVal, ((TrapRecordContext)_localctx).effectXtraBlock.xVal,
					                                        ((TrapRecordContext)_localctx).effectXtraBlock.exprChar, ((TrapRecordContext)_localctx).effectXtraBlock.baseName,
					                                        ((TrapRecordContext)_localctx).effectXtraBlock.op, "", (((TrapRecordContext)_localctx).effectXtraBlock!=null?(((TrapRecordContext)_localctx).effectXtraBlock.start):null).getLine()));
					                            
					}
					break;
				case EFFECT_YX_XTRA:
					{
					setState(208);
					((TrapRecordContext)_localctx).effectYxXtra = effectYxXtra();
					 
					                if (!xtraEffectInit.isEmpty()) {
					                    EffectParseRecord last = xtraEffectInit.getLast();
					                    EffectParseRecord updated = last.amendYX(((TrapRecordContext)_localctx).effectYxXtra.y, ((TrapRecordContext)_localctx).effectYxXtra.x);
					                    replaceLast(xtraEffectInit, updated); 
					                }
					            
					}
					break;
				case DICE_XTRA:
					{
					setState(211);
					((TrapRecordContext)_localctx).diceXtra = diceXtra();

					                if (!xtraEffectInit.isEmpty()) {
					                    String complexDiceVal = ((TrapRecordContext)_localctx).diceXtra.complexDiceValue;
					                    EffectParseRecord last = xtraEffectInit.getLast();
					                    String chars = "";
					                    String base = "";
					                    String ops = "";
					                    EffectParseRecord updated = last.amendExpression(complexDiceVal, chars, base, ops);
					                    replaceLast(xtraEffectInit, updated);
					                }
					            
					}
					break;
				case EXPR_XTRA:
					{
					setState(214);
					((TrapRecordContext)_localctx).exprXtra = exprXtra();

					                if (!xtraEffectInit.isEmpty() && (!xtraEffectInit.getLast().diceString().isEmpty())) {
					                    EffectParseRecord last = xtraEffectInit.getLast();
					                    String diceVal = last.diceString();
					                    String chars = last.expressionChars() + (!last.expressionChars().isEmpty() ? "^" : "") + ((TrapRecordContext)_localctx).exprXtra.exprChar;
					                    String base = last.expressionBases() + (!last.expressionBases().isEmpty() ? "^" : "") + ((TrapRecordContext)_localctx).exprXtra.baseName;
					                    String op = last.expressionOperations() + (!last.expressionOperations().isEmpty() ? "^" : "") + ((TrapRecordContext)_localctx).exprXtra.op; 
					                    EffectParseRecord updated = last.amendExpression(diceVal, chars, base, op);
					                    replaceLast(xtraEffectInit, updated);            
					                }
					            
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(219); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 550797308L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            ((TrapRecordContext)_localctx).trap =  new TrapParseRecord(nameInit, descSB.toString(),
			                descInit, messageSB.toString(), saveMessageSB.toString(),
			                failMessageSB.toString(), xtraMessageSB.toString(),
			                glyphInit, colourInit, rarityInit,
			                minDepthInit, maxNumInit, powerInit, flagsInit,
			                saveFlagsInit, effectInit, xtraEffectInit, line);
			        
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
		public List<TrapParseRecord> traps;
		public TrapRecordContext trapRecord;
		public TerminalNode EOF() { return getToken(TrapGrammar.EOF, 0); }
		public List<TrapRecordContext> trapRecord() {
			return getRuleContexts(TrapRecordContext.class);
		}
		public TrapRecordContext trapRecord(int i) {
			return getRuleContext(TrapRecordContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((FileContext)_localctx).traps =  new ArrayList<>(); 
			setState(225); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(222);
				((FileContext)_localctx).trapRecord = trapRecord();
				 _localctx.traps.add(((FileContext)_localctx).trapRecord.trap); 
				}
				}
				setState(227); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(229);
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

	@SuppressWarnings("CheckReturnValue")
	public static class EffectContext extends ParserRuleContext {
		public String type;
		public String wrapper;
		public String radius;
		public String other;
		public Token t;
		public Token st;
		public Token rad;
		public Token oth;
		public TerminalNode EFFECT() { return getToken(TrapGrammar.EFFECT, 0); }
		public List<TerminalNode> UCASE() { return getTokens(TrapGrammar.UCASE); }
		public TerminalNode UCASE(int i) {
			return getToken(TrapGrammar.UCASE, i);
		}
		public List<TerminalNode> COLON() { return getTokens(TrapGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(TrapGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(TrapGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(TrapGrammar.INTEGER, i);
		}
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_effect);

		            ((EffectContext)_localctx).wrapper =  "";
		            ((EffectContext)_localctx).radius =  "";
		            ((EffectContext)_localctx).other =  "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			match(EFFECT);
			setState(232);
			((EffectContext)_localctx).t = match(UCASE);

			                ((EffectContext)_localctx).type =  ((EffectContext)_localctx).t.getText();
			            
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(234);
				match(COLON);
				setState(235);
				((EffectContext)_localctx).st = match(UCASE);

				                ((EffectContext)_localctx).wrapper =  ((EffectContext)_localctx).st.getText();
				            
				setState(245);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(237);
					match(COLON);
					setState(238);
					((EffectContext)_localctx).rad = match(INTEGER);

					                ((EffectContext)_localctx).radius =  ((EffectContext)_localctx).rad.getText();
					            
					setState(243);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COLON) {
						{
						setState(240);
						match(COLON);
						setState(241);
						((EffectContext)_localctx).oth = match(INTEGER);

						                ((EffectContext)_localctx).other =  ((EffectContext)_localctx).oth.getText();
						            
						}
					}

					}
				}

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
	public static class EffectYXContext extends ParserRuleContext {
		public String y;
		public String x;
		public Token yVal;
		public Token xVal;
		public TerminalNode EFFECT_YX() { return getToken(TrapGrammar.EFFECT_YX, 0); }
		public TerminalNode COLON() { return getToken(TrapGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(TrapGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(TrapGrammar.INTEGER, i);
		}
		public EffectYXContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectYX; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterEffectYX(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitEffectYX(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitEffectYX(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectYXContext effectYX() throws RecognitionException {
		EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_effectYX);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			match(EFFECT_YX);
			setState(250);
			((EffectYXContext)_localctx).yVal = match(INTEGER);
			setState(251);
			match(COLON);
			setState(252);
			((EffectYXContext)_localctx).xVal = match(INTEGER);

			                ((EffectYXContext)_localctx).y =  ((EffectYXContext)_localctx).yVal.getText();
			                ((EffectYXContext)_localctx).x =  ((EffectYXContext)_localctx).xVal.getText();
			            
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
	public static class DiceContext extends ParserRuleContext {
		public String diceString;
		public String exprChar;
		public String baseName;
		public String operation;
		public Token val;
		public ExprContext expr;
		public TerminalNode DICE() { return getToken(TrapGrammar.DICE, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(TrapGrammar.DICE_SIMPLE_VALUE, 0); }
		public TerminalNode DICE_COMPLEX_VALUE() { return getToken(TrapGrammar.DICE_COMPLEX_VALUE, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public DiceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_dice);

		            String charHolder = "";
		            String baseHolder = "";
		            String operHolder = "";
		            ((DiceContext)_localctx).diceString =  "";
		            ((DiceContext)_localctx).exprChar =  "";
		            ((DiceContext)_localctx).baseName =  "";
		            ((DiceContext)_localctx).operation =  "";
		        
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			match(DICE);
			setState(268);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DICE_COMPLEX_VALUE:
				{
				{
				setState(256);
				((DiceContext)_localctx).val = match(DICE_COMPLEX_VALUE);

				                ((DiceContext)_localctx).diceString =  ((DiceContext)_localctx).val.getText();
				            
				setState(263);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(258);
						((DiceContext)_localctx).expr = expr();

						                if (charHolder.isEmpty()) {
						                    charHolder = ((DiceContext)_localctx).expr.exprChar;
						                    baseHolder = ((DiceContext)_localctx).expr.baseName;
						                    operHolder = ((DiceContext)_localctx).expr.operation;
						                } else {
						                    charHolder = charHolder + "^" + ((DiceContext)_localctx).expr.exprChar;
						                    baseHolder = baseHolder + "^" + ((DiceContext)_localctx).expr.baseName;
						                    operHolder = operHolder + "^" + ((DiceContext)_localctx).expr.operation;
						                }
						            
						}
						} 
					}
					setState(265);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				}
				}
				}
				break;
			case DICE_SIMPLE_VALUE:
				{
				setState(266);
				((DiceContext)_localctx).val = match(DICE_SIMPLE_VALUE);

				                ((DiceContext)_localctx).diceString =  ((DiceContext)_localctx).val.getText();
				            
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((DiceContext)_localctx).exprChar =  charHolder;
			            ((DiceContext)_localctx).baseName =  baseHolder;
			            ((DiceContext)_localctx).operation =  operHolder;
			        
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
	public static class ExprContext extends ParserRuleContext {
		public String exprChar;
		public String baseName;
		public String operation;
		public Token ch;
		public Token base;
		public Token op;
		public TerminalNode EXPR() { return getToken(TrapGrammar.EXPR, 0); }
		public List<TerminalNode> EXPR_COLON() { return getTokens(TrapGrammar.EXPR_COLON); }
		public TerminalNode EXPR_COLON(int i) {
			return getToken(TrapGrammar.EXPR_COLON, i);
		}
		public TerminalNode EXPR_CHAR() { return getToken(TrapGrammar.EXPR_CHAR, 0); }
		public TerminalNode EXPR_UCASE() { return getToken(TrapGrammar.EXPR_UCASE, 0); }
		public TerminalNode EXPR_OP() { return getToken(TrapGrammar.EXPR_OP, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(270);
			match(EXPR);
			setState(271);
			((ExprContext)_localctx).ch = match(EXPR_CHAR);
			setState(272);
			match(EXPR_COLON);
			setState(273);
			((ExprContext)_localctx).base = match(EXPR_UCASE);
			setState(274);
			match(EXPR_COLON);
			setState(275);
			((ExprContext)_localctx).op = match(EXPR_OP);

			                ((ExprContext)_localctx).exprChar =  ((ExprContext)_localctx).ch.getText();
			                ((ExprContext)_localctx).baseName =  ((ExprContext)_localctx).base.getText();
			                ((ExprContext)_localctx).operation =  ((ExprContext)_localctx).op.getText();
			            
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
	public static class EffectMsgContext extends ParserRuleContext {
		public String message;
		public Token FREE_TEXT;
		public TerminalNode EFFECT_MESSAGE() { return getToken(TrapGrammar.EFFECT_MESSAGE, 0); }
		public TerminalNode FREE_TEXT() { return getToken(TrapGrammar.FREE_TEXT, 0); }
		public EffectMsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectMsg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterEffectMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitEffectMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitEffectMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectMsgContext effectMsg() throws RecognitionException {
		EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_effectMsg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(278);
			match(EFFECT_MESSAGE);
			setState(279);
			((EffectMsgContext)_localctx).FREE_TEXT = match(FREE_TEXT);
			 ((EffectMsgContext)_localctx).message =  ((EffectMsgContext)_localctx).FREE_TEXT.getText(); 
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
	public static class TimeContext extends ParserRuleContext {
		public String timeStr;
		public Token DICE_SIMPLE_VALUE;
		public TerminalNode TIME() { return getToken(TrapGrammar.TIME, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(TrapGrammar.DICE_SIMPLE_VALUE, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(282);
			match(TIME);
			setState(283);
			((TimeContext)_localctx).DICE_SIMPLE_VALUE = match(DICE_SIMPLE_VALUE);
			 ((TimeContext)_localctx).timeStr =  ((TimeContext)_localctx).DICE_SIMPLE_VALUE.getText(); 
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
	public static class StrayEffectLineContext extends ParserRuleContext {
		public EffectYXContext effectYX() {
			return getRuleContext(EffectYXContext.class,0);
		}
		public DiceContext dice() {
			return getRuleContext(DiceContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StrayEffectLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strayEffectLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterStrayEffectLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitStrayEffectLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitStrayEffectLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrayEffectLineContext strayEffectLine() throws RecognitionException {
		StrayEffectLineContext _localctx = new StrayEffectLineContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_strayEffectLine);
		try {
			setState(289);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EFFECT_YX:
				enterOuterAlt(_localctx, 1);
				{
				setState(286);
				effectYX();
				}
				break;
			case DICE:
				enterOuterAlt(_localctx, 2);
				{
				setState(287);
				dice();
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 3);
				{
				setState(288);
				expr();
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
	public static class EffectBlockContext extends ParserRuleContext {
		public String typeInit;
		public String subtypeWrapperInit;
		public String radius;
		public String other;
		public String diceString;
		public String yVal;
		public String xVal;
		public String expressionChars;
		public String expressionBase;
		public String expressionOperation;
		public String effectMessage;
		public int lineNo;
		public EffectContext effect;
		public EffectYXContext effectYX;
		public DiceContext dice;
		public EffectMsgContext effectMsg;
		public EffectContext effect() {
			return getRuleContext(EffectContext.class,0);
		}
		public DiceContext dice() {
			return getRuleContext(DiceContext.class,0);
		}
		public EffectYXContext effectYX() {
			return getRuleContext(EffectYXContext.class,0);
		}
		public EffectMsgContext effectMsg() {
			return getRuleContext(EffectMsgContext.class,0);
		}
		public EffectBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).enterEffectBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TrapGrammarListener ) ((TrapGrammarListener)listener).exitEffectBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TrapGrammarVisitor ) return ((TrapGrammarVisitor<? extends T>)visitor).visitEffectBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectBlockContext effectBlock() throws RecognitionException {
		EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_effectBlock);

		            String expressionString = "";
		            String baseString = "";
		            String opString = "";
		            ((EffectBlockContext)_localctx).diceString =  "";
		            ((EffectBlockContext)_localctx).yVal =  "";
		            ((EffectBlockContext)_localctx).xVal =  "";
		            ((EffectBlockContext)_localctx).effectMessage =  null;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			((EffectBlockContext)_localctx).effect = effect();

			                ((EffectBlockContext)_localctx).lineNo =  _localctx.start.getLine();
			                ((EffectBlockContext)_localctx).typeInit =  ((EffectBlockContext)_localctx).effect.type;
			                ((EffectBlockContext)_localctx).subtypeWrapperInit =  ((EffectBlockContext)_localctx).effect.wrapper;
			                ((EffectBlockContext)_localctx).radius =  ((EffectBlockContext)_localctx).effect.radius;
			                ((EffectBlockContext)_localctx).other =  ((EffectBlockContext)_localctx).effect.other;
			            
			setState(309);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				{
				setState(293);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				setState(295);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				}
				}
				break;
			case 2:
				{
				{
				setState(298);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				setState(300);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				}
				break;
			case 3:
				{
				setState(303);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				}
				break;
			case 4:
				{
				setState(306);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				break;
			}
			setState(314);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EFFECT_MESSAGE) {
				{
				setState(311);
				((EffectBlockContext)_localctx).effectMsg = effectMsg();
				 ((EffectBlockContext)_localctx).effectMessage =  ((EffectBlockContext)_localctx).effectMsg.message; 
				}
			}

			}
			_ctx.stop = _input.LT(-1);

			            ((EffectBlockContext)_localctx).expressionChars =  expressionString;
			            ((EffectBlockContext)_localctx).expressionBase =  baseString;
			            ((EffectBlockContext)_localctx).expressionOperation =  opString;
			        
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
		"\u0004\u00019\u013d\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0005\u0005X\b\u0005\n\u0005\f\u0005[\t\u0005\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007h\b\u0007"+
		"\n\u0007\f\u0007k\t\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u0086\b\u000b\u0003\u000b\u0088\b\u000b\u0003\u000b\u008a\b\u000b\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0004\u0010\u00da\b\u0010\u000b\u0010\f\u0010\u00db"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0004\u0011\u00e2\b\u0011"+
		"\u000b\u0011\f\u0011\u00e3\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00f4\b\u0012"+
		"\u0003\u0012\u00f6\b\u0012\u0003\u0012\u00f8\b\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u0106"+
		"\b\u0014\n\u0014\f\u0014\u0109\t\u0014\u0001\u0014\u0001\u0014\u0003\u0014"+
		"\u010d\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0003\u0018\u0122\b\u0018\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0136\b\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u013b\b\u0019\u0001\u0019"+
		"\u0000\u0000\u001a\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014"+
		"\u0016\u0018\u001a\u001c\u001e \"$&(*,.02\u0000\u0001\u0001\u0000/0\u0146"+
		"\u00004\u0001\u0000\u0000\u0000\u0002:\u0001\u0000\u0000\u0000\u0004@"+
		"\u0001\u0000\u0000\u0000\u0006H\u0001\u0000\u0000\u0000\bL\u0001\u0000"+
		"\u0000\u0000\nP\u0001\u0000\u0000\u0000\f\\\u0001\u0000\u0000\u0000\u000e"+
		"`\u0001\u0000\u0000\u0000\u0010l\u0001\u0000\u0000\u0000\u0012p\u0001"+
		"\u0000\u0000\u0000\u0014t\u0001\u0000\u0000\u0000\u0016x\u0001\u0000\u0000"+
		"\u0000\u0018\u008b\u0001\u0000\u0000\u0000\u001a\u008f\u0001\u0000\u0000"+
		"\u0000\u001c\u0097\u0001\u0000\u0000\u0000\u001e\u009d\u0001\u0000\u0000"+
		"\u0000 \u00a1\u0001\u0000\u0000\u0000\"\u00dd\u0001\u0000\u0000\u0000"+
		"$\u00e7\u0001\u0000\u0000\u0000&\u00f9\u0001\u0000\u0000\u0000(\u00ff"+
		"\u0001\u0000\u0000\u0000*\u010e\u0001\u0000\u0000\u0000,\u0116\u0001\u0000"+
		"\u0000\u0000.\u011a\u0001\u0000\u0000\u00000\u0121\u0001\u0000\u0000\u0000"+
		"2\u0123\u0001\u0000\u0000\u000045\u0005\u0001\u0000\u000056\u0005\'\u0000"+
		"\u000067\u0005%\u0000\u000078\u0005\'\u0000\u000089\u0006\u0000\uffff"+
		"\uffff\u00009\u0001\u0001\u0000\u0000\u0000:;\u0005\u001d\u0000\u0000"+
		";<\u00057\u0000\u0000<=\u00058\u0000\u0000=>\u00056\u0000\u0000>?\u0006"+
		"\u0001\uffff\uffff\u0000?\u0003\u0001\u0000\u0000\u0000@A\u0005\u0002"+
		"\u0000\u0000AB\u0005&\u0000\u0000BC\u0005%\u0000\u0000CD\u0005&\u0000"+
		"\u0000DE\u0005%\u0000\u0000EF\u0005&\u0000\u0000FG\u0006\u0002\uffff\uffff"+
		"\u0000G\u0005\u0001\u0000\u0000\u0000HI\u0005\u0003\u0000\u0000IJ\u0007"+
		"\u0000\u0000\u0000JK\u0006\u0003\uffff\uffff\u0000K\u0007\u0001\u0000"+
		"\u0000\u0000LM\u0005\u0004\u0000\u0000MN\u0005)\u0000\u0000NO\u0006\u0004"+
		"\uffff\uffff\u0000O\t\u0001\u0000\u0000\u0000PQ\u0006\u0005\uffff\uffff"+
		"\u0000QR\u0005\u0005\u0000\u0000RS\u0005+\u0000\u0000SY\u0006\u0005\uffff"+
		"\uffff\u0000TU\u0005,\u0000\u0000UV\u0005+\u0000\u0000VX\u0006\u0005\uffff"+
		"\uffff\u0000WT\u0001\u0000\u0000\u0000X[\u0001\u0000\u0000\u0000YW\u0001"+
		"\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z\u000b\u0001\u0000\u0000"+
		"\u0000[Y\u0001\u0000\u0000\u0000\\]\u0005\u0006\u0000\u0000]^\u0005)\u0000"+
		"\u0000^_\u0006\u0006\uffff\uffff\u0000_\r\u0001\u0000\u0000\u0000`a\u0006"+
		"\u0007\uffff\uffff\u0000ab\u0005\u0007\u0000\u0000bc\u0005+\u0000\u0000"+
		"ci\u0006\u0007\uffff\uffff\u0000de\u0005,\u0000\u0000ef\u0005+\u0000\u0000"+
		"fh\u0006\u0007\uffff\uffff\u0000gd\u0001\u0000\u0000\u0000hk\u0001\u0000"+
		"\u0000\u0000ig\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000j\u000f"+
		"\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000lm\u0005\b\u0000\u0000"+
		"mn\u0005)\u0000\u0000no\u0006\b\uffff\uffff\u0000o\u0011\u0001\u0000\u0000"+
		"\u0000pq\u0005\t\u0000\u0000qr\u0005)\u0000\u0000rs\u0006\t\uffff\uffff"+
		"\u0000s\u0013\u0001\u0000\u0000\u0000tu\u0005\u000e\u0000\u0000uv\u0005"+
		")\u0000\u0000vw\u0006\n\uffff\uffff\u0000w\u0015\u0001\u0000\u0000\u0000"+
		"xy\u0006\u000b\uffff\uffff\u0000yz\u0005\n\u0000\u0000z{\u0005\u0019\u0000"+
		"\u0000{\u0089\u0006\u000b\uffff\uffff\u0000|}\u0005\u0018\u0000\u0000"+
		"}~\u0005\u0019\u0000\u0000~\u0087\u0006\u000b\uffff\uffff\u0000\u007f"+
		"\u0080\u0005\u0018\u0000\u0000\u0080\u0081\u0005\u001a\u0000\u0000\u0081"+
		"\u0085\u0006\u000b\uffff\uffff\u0000\u0082\u0083\u0005\u0018\u0000\u0000"+
		"\u0083\u0084\u0005\u001a\u0000\u0000\u0084\u0086\u0006\u000b\uffff\uffff"+
		"\u0000\u0085\u0082\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000"+
		"\u0000\u0086\u0088\u0001\u0000\u0000\u0000\u0087\u007f\u0001\u0000\u0000"+
		"\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u008a\u0001\u0000\u0000"+
		"\u0000\u0089|\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000"+
		"\u008a\u0017\u0001\u0000\u0000\u0000\u008b\u008c\u0005\f\u0000\u0000\u008c"+
		"\u008d\u0007\u0000\u0000\u0000\u008d\u008e\u0006\f\uffff\uffff\u0000\u008e"+
		"\u0019\u0001\u0000\u0000\u0000\u008f\u0090\u0005\r\u0000\u0000\u0090\u0091"+
		"\u0005 \u0000\u0000\u0091\u0092\u0005!\u0000\u0000\u0092\u0093\u0005\""+
		"\u0000\u0000\u0093\u0094\u0005!\u0000\u0000\u0094\u0095\u0005#\u0000\u0000"+
		"\u0095\u0096\u0006\r\uffff\uffff\u0000\u0096\u001b\u0001\u0000\u0000\u0000"+
		"\u0097\u0098\u0005\u000b\u0000\u0000\u0098\u0099\u0005\u001a\u0000\u0000"+
		"\u0099\u009a\u0005\u0018\u0000\u0000\u009a\u009b\u0005\u001a\u0000\u0000"+
		"\u009b\u009c\u0006\u000e\uffff\uffff\u0000\u009c\u001d\u0001\u0000\u0000"+
		"\u0000\u009d\u009e\u0006\u000f\uffff\uffff\u0000\u009e\u009f\u0003\u0016"+
		"\u000b\u0000\u009f\u00a0\u0006\u000f\uffff\uffff\u0000\u00a0\u001f\u0001"+
		"\u0000\u0000\u0000\u00a1\u00a2\u0003\u0000\u0000\u0000\u00a2\u00d9\u0006"+
		"\u0010\uffff\uffff\u0000\u00a3\u00a4\u0003\u0002\u0001\u0000\u00a4\u00a5"+
		"\u0006\u0010\uffff\uffff\u0000\u00a5\u00da\u0001\u0000\u0000\u0000\u00a6"+
		"\u00a7\u0003\u0004\u0002\u0000\u00a7\u00a8\u0006\u0010\uffff\uffff\u0000"+
		"\u00a8\u00da\u0001\u0000\u0000\u0000\u00a9\u00aa\u0003\u0006\u0003\u0000"+
		"\u00aa\u00ab\u0006\u0010\uffff\uffff\u0000\u00ab\u00da\u0001\u0000\u0000"+
		"\u0000\u00ac\u00ad\u0003\n\u0005\u0000\u00ad\u00ae\u0006\u0010\uffff\uffff"+
		"\u0000\u00ae\u00da\u0001\u0000\u0000\u0000\u00af\u00b0\u0003\u000e\u0007"+
		"\u0000\u00b0\u00b1\u0006\u0010\uffff\uffff\u0000\u00b1\u00da\u0001\u0000"+
		"\u0000\u0000\u00b2\u00b3\u0003\b\u0004\u0000\u00b3\u00b4\u0006\u0010\uffff"+
		"\uffff\u0000\u00b4\u00da\u0001\u0000\u0000\u0000\u00b5\u00b6\u0003\f\u0006"+
		"\u0000\u00b6\u00b7\u0006\u0010\uffff\uffff\u0000\u00b7\u00da\u0001\u0000"+
		"\u0000\u0000\u00b8\u00b9\u0003\u0010\b\u0000\u00b9\u00ba\u0006\u0010\uffff"+
		"\uffff\u0000\u00ba\u00da\u0001\u0000\u0000\u0000\u00bb\u00bc\u0003\u0012"+
		"\t\u0000\u00bc\u00bd\u0006\u0010\uffff\uffff\u0000\u00bd\u00da\u0001\u0000"+
		"\u0000\u0000\u00be\u00bf\u0003\u0014\n\u0000\u00bf\u00c0\u0006\u0010\uffff"+
		"\uffff\u0000\u00c0\u00da\u0001\u0000\u0000\u0000\u00c1\u00c2\u00032\u0019"+
		"\u0000\u00c2\u00c3\u0006\u0010\uffff\uffff\u0000\u00c3\u00da\u0001\u0000"+
		"\u0000\u0000\u00c4\u00c5\u0003&\u0013\u0000\u00c5\u00c6\u0006\u0010\uffff"+
		"\uffff\u0000\u00c6\u00da\u0001\u0000\u0000\u0000\u00c7\u00c8\u0003(\u0014"+
		"\u0000\u00c8\u00c9\u0006\u0010\uffff\uffff\u0000\u00c9\u00da\u0001\u0000"+
		"\u0000\u0000\u00ca\u00cb\u0003*\u0015\u0000\u00cb\u00cc\u0006\u0010\uffff"+
		"\uffff\u0000\u00cc\u00da\u0001\u0000\u0000\u0000\u00cd\u00ce\u0003\u001e"+
		"\u000f\u0000\u00ce\u00cf\u0006\u0010\uffff\uffff\u0000\u00cf\u00da\u0001"+
		"\u0000\u0000\u0000\u00d0\u00d1\u0003\u001c\u000e\u0000\u00d1\u00d2\u0006"+
		"\u0010\uffff\uffff\u0000\u00d2\u00da\u0001\u0000\u0000\u0000\u00d3\u00d4"+
		"\u0003\u0018\f\u0000\u00d4\u00d5\u0006\u0010\uffff\uffff\u0000\u00d5\u00da"+
		"\u0001\u0000\u0000\u0000\u00d6\u00d7\u0003\u001a\r\u0000\u00d7\u00d8\u0006"+
		"\u0010\uffff\uffff\u0000\u00d8\u00da\u0001\u0000\u0000\u0000\u00d9\u00a3"+
		"\u0001\u0000\u0000\u0000\u00d9\u00a6\u0001\u0000\u0000\u0000\u00d9\u00a9"+
		"\u0001\u0000\u0000\u0000\u00d9\u00ac\u0001\u0000\u0000\u0000\u00d9\u00af"+
		"\u0001\u0000\u0000\u0000\u00d9\u00b2\u0001\u0000\u0000\u0000\u00d9\u00b5"+
		"\u0001\u0000\u0000\u0000\u00d9\u00b8\u0001\u0000\u0000\u0000\u00d9\u00bb"+
		"\u0001\u0000\u0000\u0000\u00d9\u00be\u0001\u0000\u0000\u0000\u00d9\u00c1"+
		"\u0001\u0000\u0000\u0000\u00d9\u00c4\u0001\u0000\u0000\u0000\u00d9\u00c7"+
		"\u0001\u0000\u0000\u0000\u00d9\u00ca\u0001\u0000\u0000\u0000\u00d9\u00cd"+
		"\u0001\u0000\u0000\u0000\u00d9\u00d0\u0001\u0000\u0000\u0000\u00d9\u00d3"+
		"\u0001\u0000\u0000\u0000\u00d9\u00d6\u0001\u0000\u0000\u0000\u00da\u00db"+
		"\u0001\u0000\u0000\u0000\u00db\u00d9\u0001\u0000\u0000\u0000\u00db\u00dc"+
		"\u0001\u0000\u0000\u0000\u00dc!\u0001\u0000\u0000\u0000\u00dd\u00e1\u0006"+
		"\u0011\uffff\uffff\u0000\u00de\u00df\u0003 \u0010\u0000\u00df\u00e0\u0006"+
		"\u0011\uffff\uffff\u0000\u00e0\u00e2\u0001\u0000\u0000\u0000\u00e1\u00de"+
		"\u0001\u0000\u0000\u0000\u00e2\u00e3\u0001\u0000\u0000\u0000\u00e3\u00e1"+
		"\u0001\u0000\u0000\u0000\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e4\u00e5"+
		"\u0001\u0000\u0000\u0000\u00e5\u00e6\u0005\u0000\u0000\u0001\u00e6#\u0001"+
		"\u0000\u0000\u0000\u00e7\u00e8\u0005\u0012\u0000\u0000\u00e8\u00e9\u0005"+
		"\u0019\u0000\u0000\u00e9\u00f7\u0006\u0012\uffff\uffff\u0000\u00ea\u00eb"+
		"\u0005\u0018\u0000\u0000\u00eb\u00ec\u0005\u0019\u0000\u0000\u00ec\u00f5"+
		"\u0006\u0012\uffff\uffff\u0000\u00ed\u00ee\u0005\u0018\u0000\u0000\u00ee"+
		"\u00ef\u0005\u001a\u0000\u0000\u00ef\u00f3\u0006\u0012\uffff\uffff\u0000"+
		"\u00f0\u00f1\u0005\u0018\u0000\u0000\u00f1\u00f2\u0005\u001a\u0000\u0000"+
		"\u00f2\u00f4\u0006\u0012\uffff\uffff\u0000\u00f3\u00f0\u0001\u0000\u0000"+
		"\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000\u00f4\u00f6\u0001\u0000\u0000"+
		"\u0000\u00f5\u00ed\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000\u0000"+
		"\u0000\u00f6\u00f8\u0001\u0000\u0000\u0000\u00f7\u00ea\u0001\u0000\u0000"+
		"\u0000\u00f7\u00f8\u0001\u0000\u0000\u0000\u00f8%\u0001\u0000\u0000\u0000"+
		"\u00f9\u00fa\u0005\u0016\u0000\u0000\u00fa\u00fb\u0005\u001a\u0000\u0000"+
		"\u00fb\u00fc\u0005\u0018\u0000\u0000\u00fc\u00fd\u0005\u001a\u0000\u0000"+
		"\u00fd\u00fe\u0006\u0013\uffff\uffff\u0000\u00fe\'\u0001\u0000\u0000\u0000"+
		"\u00ff\u010c\u0005\u0014\u0000\u0000\u0100\u0101\u00050\u0000\u0000\u0101"+
		"\u0107\u0006\u0014\uffff\uffff\u0000\u0102\u0103\u0003*\u0015\u0000\u0103"+
		"\u0104\u0006\u0014\uffff\uffff\u0000\u0104\u0106\u0001\u0000\u0000\u0000"+
		"\u0105\u0102\u0001\u0000\u0000\u0000\u0106\u0109\u0001\u0000\u0000\u0000"+
		"\u0107\u0105\u0001\u0000\u0000\u0000\u0107\u0108\u0001\u0000\u0000\u0000"+
		"\u0108\u010d\u0001\u0000\u0000\u0000\u0109\u0107\u0001\u0000\u0000\u0000"+
		"\u010a\u010b\u0005/\u0000\u0000\u010b\u010d\u0006\u0014\uffff\uffff\u0000"+
		"\u010c\u0100\u0001\u0000\u0000\u0000\u010c\u010a\u0001\u0000\u0000\u0000"+
		"\u010d)\u0001\u0000\u0000\u0000\u010e\u010f\u0005\u0017\u0000\u0000\u010f"+
		"\u0110\u00051\u0000\u0000\u0110\u0111\u00052\u0000\u0000\u0111\u0112\u0005"+
		"3\u0000\u0000\u0112\u0113\u00052\u0000\u0000\u0113\u0114\u00054\u0000"+
		"\u0000\u0114\u0115\u0006\u0015\uffff\uffff\u0000\u0115+\u0001\u0000\u0000"+
		"\u0000\u0116\u0117\u0005\u0013\u0000\u0000\u0117\u0118\u0005.\u0000\u0000"+
		"\u0118\u0119\u0006\u0016\uffff\uffff\u0000\u0119-\u0001\u0000\u0000\u0000"+
		"\u011a\u011b\u0005\u0015\u0000\u0000\u011b\u011c\u0005/\u0000\u0000\u011c"+
		"\u011d\u0006\u0017\uffff\uffff\u0000\u011d/\u0001\u0000\u0000\u0000\u011e"+
		"\u0122\u0003&\u0013\u0000\u011f\u0122\u0003(\u0014\u0000\u0120\u0122\u0003"+
		"*\u0015\u0000\u0121\u011e\u0001\u0000\u0000\u0000\u0121\u011f\u0001\u0000"+
		"\u0000\u0000\u0121\u0120\u0001\u0000\u0000\u0000\u01221\u0001\u0000\u0000"+
		"\u0000\u0123\u0124\u0003$\u0012\u0000\u0124\u0135\u0006\u0019\uffff\uffff"+
		"\u0000\u0125\u0126\u0003&\u0013\u0000\u0126\u0127\u0006\u0019\uffff\uffff"+
		"\u0000\u0127\u0128\u0003(\u0014\u0000\u0128\u0129\u0006\u0019\uffff\uffff"+
		"\u0000\u0129\u0136\u0001\u0000\u0000\u0000\u012a\u012b\u0003(\u0014\u0000"+
		"\u012b\u012c\u0006\u0019\uffff\uffff\u0000\u012c\u012d\u0003&\u0013\u0000"+
		"\u012d\u012e\u0006\u0019\uffff\uffff\u0000\u012e\u0136\u0001\u0000\u0000"+
		"\u0000\u012f\u0130\u0003(\u0014\u0000\u0130\u0131\u0006\u0019\uffff\uffff"+
		"\u0000\u0131\u0136\u0001\u0000\u0000\u0000\u0132\u0133\u0003&\u0013\u0000"+
		"\u0133\u0134\u0006\u0019\uffff\uffff\u0000\u0134\u0136\u0001\u0000\u0000"+
		"\u0000\u0135\u0125\u0001\u0000\u0000\u0000\u0135\u012a\u0001\u0000\u0000"+
		"\u0000\u0135\u012f\u0001\u0000\u0000\u0000\u0135\u0132\u0001\u0000\u0000"+
		"\u0000\u0135\u0136\u0001\u0000\u0000\u0000\u0136\u013a\u0001\u0000\u0000"+
		"\u0000\u0137\u0138\u0003,\u0016\u0000\u0138\u0139\u0006\u0019\uffff\uffff"+
		"\u0000\u0139\u013b\u0001\u0000\u0000\u0000\u013a\u0137\u0001\u0000\u0000"+
		"\u0000\u013a\u013b\u0001\u0000\u0000\u0000\u013b3\u0001\u0000\u0000\u0000"+
		"\u0010Yi\u0085\u0087\u0089\u00d9\u00db\u00e3\u00f3\u00f5\u00f7\u0107\u010c"+
		"\u0121\u0135\u013a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}