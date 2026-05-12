// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/SummonFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.summon;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SummonFormatterParser extends Parser {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			COMMENT = 1, EOL = 2, NAME = 3, MSGTAG = 4, UNIQUES = 5, BASE = 6, RACE_FLAG = 7, FALLBACK = 8,
			DESC = 9, WORD = 10, BOOLEAN = 11;
	public static final int
			RULE_name = 0, RULE_msgt = 1, RULE_uniques = 2, RULE_base = 3, RULE_raceFlag = 4,
			RULE_fallback = 5, RULE_desc = 6, RULE_summon = 7, RULE_file = 8;

	private static String[] makeRuleNames() {
		return new String[]{
				"name", "msgt", "uniques", "base", "raceFlag", "fallback", "desc", "summon",
				"file"
		};
	}

	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'name:'", "'msgt:'", "'uniques:'", "'base:'", "'race-flag:'",
				"'fallback:'", "'desc:'"
		};
	}

	private static final String[] _LITERAL_NAMES = makeLiteralNames();

	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "NAME", "MSGTAG", "UNIQUES", "BASE", "RACE_FLAG",
				"FALLBACK", "DESC", "WORD", "BOOLEAN"
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
		return "SummonFormatter.g4";
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

	public SummonFormatterParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token WORD;

		public TerminalNode NAME() {
			return getToken(SummonFormatterParser.NAME, 0);
		}

		public TerminalNode WORD() {
			return getToken(SummonFormatterParser.WORD, 0);
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
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterName(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitName(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof SummonFormatterVisitor)
				return ((SummonFormatterVisitor<? extends T>) visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(18);
				match(NAME);
				setState(19);
				((NameContext) _localctx).WORD = match(WORD);

				((NameContext) _localctx).nameStr = ((NameContext) _localctx).WORD.getText();

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
	public static class MsgtContext extends ParserRuleContext {
		public String msgtStr;
		public Token WORD;

		public TerminalNode MSGTAG() {
			return getToken(SummonFormatterParser.MSGTAG, 0);
		}

		public TerminalNode WORD() {
			return getToken(SummonFormatterParser.WORD, 0);
		}

		public MsgtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_msgt;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterMsgt(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitMsgt(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof SummonFormatterVisitor)
				return ((SummonFormatterVisitor<? extends T>) visitor).visitMsgt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgtContext msgt() throws RecognitionException {
		MsgtContext _localctx = new MsgtContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_msgt);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(22);
				match(MSGTAG);
				setState(23);
				((MsgtContext) _localctx).WORD = match(WORD);

				((MsgtContext) _localctx).msgtStr = ((MsgtContext) _localctx).WORD.getText();

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
	public static class UniquesContext extends ParserRuleContext {
		public boolean unique;
		public Token BOOLEAN;

		public TerminalNode UNIQUES() {
			return getToken(SummonFormatterParser.UNIQUES, 0);
		}

		public TerminalNode BOOLEAN() {
			return getToken(SummonFormatterParser.BOOLEAN, 0);
		}

		public UniquesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_uniques;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterUniques(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitUniques(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof SummonFormatterVisitor)
				return ((SummonFormatterVisitor<? extends T>) visitor).visitUniques(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UniquesContext uniques() throws RecognitionException {
		UniquesContext _localctx = new UniquesContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_uniques);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(26);
				match(UNIQUES);
				setState(27);
				((UniquesContext) _localctx).BOOLEAN = match(BOOLEAN);

				((UniquesContext) _localctx).unique = ((UniquesContext) _localctx).BOOLEAN.getText().equals("1");

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
	public static class BaseContext extends ParserRuleContext {
		public MonsterBase mBase;
		public Token WORD;

		public TerminalNode BASE() {
			return getToken(SummonFormatterParser.BASE, 0);
		}

		public TerminalNode WORD() {
			return getToken(SummonFormatterParser.WORD, 0);
		}

		public BaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_base;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterBase(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitBase(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof SummonFormatterVisitor)
				return ((SummonFormatterVisitor<? extends T>) visitor).visitBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BaseContext base() throws RecognitionException {
		BaseContext _localctx = new BaseContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_base);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(30);
				match(BASE);
				setState(31);
				((BaseContext) _localctx).WORD = match(WORD);

				String baseWord = ((BaseContext) _localctx).WORD.getText();
				((BaseContext) _localctx).mBase = GameConstants.getBaseFromName(baseWord);

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
	public static class RaceFlagContext extends ParserRuleContext {
		public MonsterRaceFlag mrFlag;
		public Token WORD;

		public TerminalNode RACE_FLAG() {
			return getToken(SummonFormatterParser.RACE_FLAG, 0);
		}

		public TerminalNode WORD() {
			return getToken(SummonFormatterParser.WORD, 0);
		}

		public RaceFlagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_raceFlag;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterRaceFlag(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitRaceFlag(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof SummonFormatterVisitor)
				return ((SummonFormatterVisitor<? extends T>) visitor).visitRaceFlag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RaceFlagContext raceFlag() throws RecognitionException {
		RaceFlagContext _localctx = new RaceFlagContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_raceFlag);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(34);
				match(RACE_FLAG);
				setState(35);
				((RaceFlagContext) _localctx).WORD = match(WORD);

				String flag = "RF_" + ((RaceFlagContext) _localctx).WORD.getText().trim();
				((RaceFlagContext) _localctx).mrFlag = MonsterRaceFlag.valueOf(flag);

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
	public static class FallbackContext extends ParserRuleContext {
		public String fallBackName;
		public Token WORD;

		public TerminalNode FALLBACK() {
			return getToken(SummonFormatterParser.FALLBACK, 0);
		}

		public TerminalNode WORD() {
			return getToken(SummonFormatterParser.WORD, 0);
		}

		public FallbackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_fallback;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterFallback(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitFallback(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof SummonFormatterVisitor)
				return ((SummonFormatterVisitor<? extends T>) visitor).visitFallback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FallbackContext fallback() throws RecognitionException {
		FallbackContext _localctx = new FallbackContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_fallback);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(38);
				match(FALLBACK);
				setState(39);
				((FallbackContext) _localctx).WORD = match(WORD);

				((FallbackContext) _localctx).fallBackName = ((FallbackContext) _localctx).WORD.getText();

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
		public String description;
		public Token WORD;

		public TerminalNode DESC() {
			return getToken(SummonFormatterParser.DESC, 0);
		}

		public TerminalNode WORD() {
			return getToken(SummonFormatterParser.WORD, 0);
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
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterDesc(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitDesc(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof SummonFormatterVisitor)
				return ((SummonFormatterVisitor<? extends T>) visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(42);
				match(DESC);
				setState(43);
				((DescContext) _localctx).WORD = match(WORD);

				((DescContext) _localctx).description = ((DescContext) _localctx).WORD.getText();

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
	public static class SummonContext extends ParserRuleContext {
		public Summon smn;
		public NameContext name;
		public MsgtContext msgt;
		public UniquesContext uniques;
		public BaseContext base;
		public RaceFlagContext raceFlag;
		public FallbackContext fallback;
		public DescContext desc;

		public NameContext name() {
			return getRuleContext(NameContext.class, 0);
		}

		public MsgtContext msgt() {
			return getRuleContext(MsgtContext.class, 0);
		}

		public UniquesContext uniques() {
			return getRuleContext(UniquesContext.class, 0);
		}

		public DescContext desc() {
			return getRuleContext(DescContext.class, 0);
		}

		public List<BaseContext> base() {
			return getRuleContexts(BaseContext.class);
		}

		public BaseContext base(int i) {
			return getRuleContext(BaseContext.class, i);
		}

		public RaceFlagContext raceFlag() {
			return getRuleContext(RaceFlagContext.class, 0);
		}

		public FallbackContext fallback() {
			return getRuleContext(FallbackContext.class, 0);
		}

		public SummonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_summon;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterSummon(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitSummon(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof SummonFormatterVisitor)
				return ((SummonFormatterVisitor<? extends T>) visitor).visitSummon(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SummonContext summon() throws RecognitionException {
		SummonContext _localctx = new SummonContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_summon);

		String nameTag = "";
		String msgTag = "";
		boolean unique = false;
		ArrayList<MonsterBase> mb = new ArrayList<>();
		MonsterRaceFlag raceFlg = MonsterRaceFlag.RF_NONE;
		String fallBack = "";
		String descStr = "";

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(46);
				((SummonContext) _localctx).name = name();
				nameTag = ((SummonContext) _localctx).name.nameStr;
				setState(48);
				((SummonContext) _localctx).msgt = msgt();
				msgTag = ((SummonContext) _localctx).msgt.msgtStr;
				setState(50);
				((SummonContext) _localctx).uniques = uniques();
				unique = ((SummonContext) _localctx).uniques.unique;
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == BASE) {
					{
						{
							setState(52);
							((SummonContext) _localctx).base = base();
							mb.add(((SummonContext) _localctx).base.mBase);
						}
					}
					setState(59);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == RACE_FLAG) {
					{
						setState(60);
						((SummonContext) _localctx).raceFlag = raceFlag();
						raceFlg = ((SummonContext) _localctx).raceFlag.mrFlag;
					}
				}

				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == FALLBACK) {
					{
						setState(65);
						((SummonContext) _localctx).fallback = fallback();
						fallBack = ((SummonContext) _localctx).fallback.fallBackName;
					}
				}

				setState(70);
				((SummonContext) _localctx).desc = desc();
				descStr = ((SummonContext) _localctx).desc.description;
			}
			_ctx.stop = _input.LT(-1);

			((SummonContext) _localctx).smn = new Summon(nameTag, msgTag, unique, mb, raceFlg, fallBack, descStr);

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
		public ArrayList<Summon> summons;
		public SummonContext summon;

		public TerminalNode EOF() {
			return getToken(SummonFormatterParser.EOF, 0);
		}

		public List<SummonContext> summon() {
			return getRuleContexts(SummonContext.class);
		}

		public SummonContext summon(int i) {
			return getRuleContext(SummonContext.class, i);
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
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterFile(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitFile(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof SummonFormatterVisitor)
				return ((SummonFormatterVisitor<? extends T>) visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_file);

		((FileContext) _localctx).summons = new ArrayList<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						{
							setState(73);
							((FileContext) _localctx).summon = summon();

							_localctx.summons.add(((FileContext) _localctx).summon.smn);

						}
					}
					setState(78);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while (_la == NAME);
				setState(80);
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
			"\u0004\u0001\u000bS\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
					"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
					"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
					"\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001" +
					"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
					"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004" +
					"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005" +
					"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
					"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
					"\u0001\u0007\u0001\u0007\u0005\u00078\b\u0007\n\u0007\f\u0007;\t\u0007" +
					"\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007@\b\u0007\u0001\u0007" +
					"\u0001\u0007\u0001\u0007\u0003\u0007E\b\u0007\u0001\u0007\u0001\u0007" +
					"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0004\bM\b\b\u000b\b\f\bN\u0001\b" +
					"\u0001\b\u0001\b\u0000\u0000\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010" +
					"\u0000\u0000M\u0000\u0012\u0001\u0000\u0000\u0000\u0002\u0016\u0001\u0000" +
					"\u0000\u0000\u0004\u001a\u0001\u0000\u0000\u0000\u0006\u001e\u0001\u0000" +
					"\u0000\u0000\b\"\u0001\u0000\u0000\u0000\n&\u0001\u0000\u0000\u0000\f" +
					"*\u0001\u0000\u0000\u0000\u000e.\u0001\u0000\u0000\u0000\u0010L\u0001" +
					"\u0000\u0000\u0000\u0012\u0013\u0005\u0003\u0000\u0000\u0013\u0014\u0005" +
					"\n\u0000\u0000\u0014\u0015\u0006\u0000\uffff\uffff\u0000\u0015\u0001\u0001" +
					"\u0000\u0000\u0000\u0016\u0017\u0005\u0004\u0000\u0000\u0017\u0018\u0005" +
					"\n\u0000\u0000\u0018\u0019\u0006\u0001\uffff\uffff\u0000\u0019\u0003\u0001" +
					"\u0000\u0000\u0000\u001a\u001b\u0005\u0005\u0000\u0000\u001b\u001c\u0005" +
					"\u000b\u0000\u0000\u001c\u001d\u0006\u0002\uffff\uffff\u0000\u001d\u0005" +
					"\u0001\u0000\u0000\u0000\u001e\u001f\u0005\u0006\u0000\u0000\u001f \u0005" +
					"\n\u0000\u0000 !\u0006\u0003\uffff\uffff\u0000!\u0007\u0001\u0000\u0000" +
					"\u0000\"#\u0005\u0007\u0000\u0000#$\u0005\n\u0000\u0000$%\u0006\u0004" +
					"\uffff\uffff\u0000%\t\u0001\u0000\u0000\u0000&\'\u0005\b\u0000\u0000\'" +
					"(\u0005\n\u0000\u0000()\u0006\u0005\uffff\uffff\u0000)\u000b\u0001\u0000" +
					"\u0000\u0000*+\u0005\t\u0000\u0000+,\u0005\n\u0000\u0000,-\u0006\u0006" +
					"\uffff\uffff\u0000-\r\u0001\u0000\u0000\u0000./\u0003\u0000\u0000\u0000" +
					"/0\u0006\u0007\uffff\uffff\u000001\u0003\u0002\u0001\u000012\u0006\u0007" +
					"\uffff\uffff\u000023\u0003\u0004\u0002\u000039\u0006\u0007\uffff\uffff" +
					"\u000045\u0003\u0006\u0003\u000056\u0006\u0007\uffff\uffff\u000068\u0001" +
					"\u0000\u0000\u000074\u0001\u0000\u0000\u00008;\u0001\u0000\u0000\u0000" +
					"97\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:?\u0001\u0000\u0000" +
					"\u0000;9\u0001\u0000\u0000\u0000<=\u0003\b\u0004\u0000=>\u0006\u0007\uffff" +
					"\uffff\u0000>@\u0001\u0000\u0000\u0000?<\u0001\u0000\u0000\u0000?@\u0001" +
					"\u0000\u0000\u0000@D\u0001\u0000\u0000\u0000AB\u0003\n\u0005\u0000BC\u0006" +
					"\u0007\uffff\uffff\u0000CE\u0001\u0000\u0000\u0000DA\u0001\u0000\u0000" +
					"\u0000DE\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000FG\u0003\f\u0006" +
					"\u0000GH\u0006\u0007\uffff\uffff\u0000H\u000f\u0001\u0000\u0000\u0000" +
					"IJ\u0003\u000e\u0007\u0000JK\u0006\b\uffff\uffff\u0000KM\u0001\u0000\u0000" +
					"\u0000LI\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000NL\u0001\u0000" +
					"\u0000\u0000NO\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000PQ\u0005" +
					"\u0000\u0000\u0001Q\u0011\u0001\u0000\u0000\u0000\u00049?DN";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());

	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}