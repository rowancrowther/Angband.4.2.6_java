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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/EgoItem.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.egoitem;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.CurseData;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class EgoItemLexer extends Lexer {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			COMMENT = 1, EOL = 2, NAME = 3, INFO = 4, ALLOC = 5, COMBAT = 6, MIN_COMBAT = 7, TYPE = 8,
			ITEM = 9, FLAGS = 10, FLAG_OFF = 11, VALUES = 12, MIN_VALUES = 13, ACT = 14, TIME = 15,
			BRAND = 16, SLAY = 17, CURSE = 18, DESC = 19, COLON = 20, DICE_STRING = 21, TEXT = 22,
			OR = 23;
	public static String[] channelNames = {
			"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
			"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[]{
				"COMMENT", "EOL", "NAME", "INFO", "ALLOC", "COMBAT", "MIN_COMBAT", "TYPE",
				"ITEM", "FLAGS", "FLAG_OFF", "VALUES", "MIN_VALUES", "ACT", "TIME", "BRAND",
				"SLAY", "CURSE", "DESC", "COLON", "DICE_STRING", "TEXT", "OR"
		};
	}

	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'name:'", "'info:'", "'alloc:'", "'combat:'", "'min-combat:'",
				"'type:'", "'item:'", "'flags:'", "'flags-off:'", "'values:'", "'min-values:'",
				"'act:'", "'time:'", "'brand:'", "'slay:'", "'curse:'", "'desc:'", "':'"
		};
	}

	private static final String[] _LITERAL_NAMES = makeLiteralNames();

	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "NAME", "INFO", "ALLOC", "COMBAT", "MIN_COMBAT",
				"TYPE", "ITEM", "FLAGS", "FLAG_OFF", "VALUES", "MIN_VALUES", "ACT", "TIME",
				"BRAND", "SLAY", "CURSE", "DESC", "COLON", "DICE_STRING", "TEXT", "OR"
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


	public record CurseEntry(Curse curse, CurseData curseData) {
	}

	private static final Logger logger = LogManager.getLogger();


	public EgoItemLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@Override
	public String getGrammarFileName() {
		return "EgoItem.g4";
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
	public String[] getChannelNames() {
		return channelNames;
	}

	@Override
	public String[] getModeNames() {
		return modeNames;
	}

	@Override
	public ATN getATN() {
		return _ATN;
	}

	public static final String _serializedATN =
			"\u0004\u0000\u0017\u00db\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
					"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
					"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
					"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
					"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
					"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011" +
					"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014" +
					"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0001\u0000\u0001\u0000" +
					"\u0005\u00002\b\u0000\n\u0000\f\u00005\t\u0000\u0001\u0000\u0004\u0000" +
					"8\b\u0000\u000b\u0000\f\u00009\u0001\u0000\u0001\u0000\u0001\u0001\u0005" +
					"\u0001?\b\u0001\n\u0001\f\u0001B\t\u0001\u0001\u0001\u0003\u0001E\b\u0001" +
					"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
					"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003" +
					"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004" +
					"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005" +
					"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
					"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
					"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
					"\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
					"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001" +
					"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001" +
					"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b" +
					"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
					"\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
					"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
					"\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
					"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
					"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
					"\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
					"\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012" +
					"\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0004\u0014" +
					"\u00cc\b\u0014\u000b\u0014\f\u0014\u00cd\u0001\u0015\u0004\u0015\u00d1" +
					"\b\u0015\u000b\u0015\f\u0015\u00d2\u0001\u0016\u0003\u0016\u00d6\b\u0016" +
					"\u0001\u0016\u0001\u0016\u0003\u0016\u00da\b\u0016\u0000\u0000\u0017\u0001" +
					"\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007" +
					"\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d" +
					"\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017\u0001" +
					"\u0000\u0003\u0001\u0000\n\n\u0006\u0000++--09MMddmm\t\u0000  \'.09A[" +
					"]]__az\u00f3\u00f3\u00fb\u00fb\u00e2\u0000\u0001\u0001\u0000\u0000\u0000" +
					"\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000" +
					"\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000" +
					"\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f" +
					"\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013" +
					"\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017" +
					"\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b" +
					"\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f" +
					"\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000" +
					"\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000" +
					"\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000\u0000\u0000\u0000" +
					"-\u0001\u0000\u0000\u0000\u0001/\u0001\u0000\u0000\u0000\u0003@\u0001" +
					"\u0000\u0000\u0000\u0005J\u0001\u0000\u0000\u0000\u0007P\u0001\u0000\u0000" +
					"\u0000\tV\u0001\u0000\u0000\u0000\u000b]\u0001\u0000\u0000\u0000\re\u0001" +
					"\u0000\u0000\u0000\u000fq\u0001\u0000\u0000\u0000\u0011w\u0001\u0000\u0000" +
					"\u0000\u0013}\u0001\u0000\u0000\u0000\u0015\u0084\u0001\u0000\u0000\u0000" +
					"\u0017\u008f\u0001\u0000\u0000\u0000\u0019\u0097\u0001\u0000\u0000\u0000" +
					"\u001b\u00a3\u0001\u0000\u0000\u0000\u001d\u00a8\u0001\u0000\u0000\u0000" +
					"\u001f\u00ae\u0001\u0000\u0000\u0000!\u00b5\u0001\u0000\u0000\u0000#\u00bb" +
					"\u0001\u0000\u0000\u0000%\u00c2\u0001\u0000\u0000\u0000\'\u00c8\u0001" +
					"\u0000\u0000\u0000)\u00cb\u0001\u0000\u0000\u0000+\u00d0\u0001\u0000\u0000" +
					"\u0000-\u00d5\u0001\u0000\u0000\u0000/3\u0005#\u0000\u000002\b\u0000\u0000" +
					"\u000010\u0001\u0000\u0000\u000025\u0001\u0000\u0000\u000031\u0001\u0000" +
					"\u0000\u000034\u0001\u0000\u0000\u000047\u0001\u0000\u0000\u000053\u0001" +
					"\u0000\u0000\u000068\u0005\n\u0000\u000076\u0001\u0000\u0000\u000089\u0001" +
					"\u0000\u0000\u000097\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000" +
					":;\u0001\u0000\u0000\u0000;<\u0006\u0000\u0000\u0000<\u0002\u0001\u0000" +
					"\u0000\u0000=?\u0005 \u0000\u0000>=\u0001\u0000\u0000\u0000?B\u0001\u0000" +
					"\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000AD\u0001" +
					"\u0000\u0000\u0000B@\u0001\u0000\u0000\u0000CE\u0005\r\u0000\u0000DC\u0001" +
					"\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000" +
					"FG\u0005\n\u0000\u0000GH\u0001\u0000\u0000\u0000HI\u0006\u0001\u0000\u0000" +
					"I\u0004\u0001\u0000\u0000\u0000JK\u0005n\u0000\u0000KL\u0005a\u0000\u0000" +
					"LM\u0005m\u0000\u0000MN\u0005e\u0000\u0000NO\u0005:\u0000\u0000O\u0006" +
					"\u0001\u0000\u0000\u0000PQ\u0005i\u0000\u0000QR\u0005n\u0000\u0000RS\u0005" +
					"f\u0000\u0000ST\u0005o\u0000\u0000TU\u0005:\u0000\u0000U\b\u0001\u0000" +
					"\u0000\u0000VW\u0005a\u0000\u0000WX\u0005l\u0000\u0000XY\u0005l\u0000" +
					"\u0000YZ\u0005o\u0000\u0000Z[\u0005c\u0000\u0000[\\\u0005:\u0000\u0000" +
					"\\\n\u0001\u0000\u0000\u0000]^\u0005c\u0000\u0000^_\u0005o\u0000\u0000" +
					"_`\u0005m\u0000\u0000`a\u0005b\u0000\u0000ab\u0005a\u0000\u0000bc\u0005" +
					"t\u0000\u0000cd\u0005:\u0000\u0000d\f\u0001\u0000\u0000\u0000ef\u0005" +
					"m\u0000\u0000fg\u0005i\u0000\u0000gh\u0005n\u0000\u0000hi\u0005-\u0000" +
					"\u0000ij\u0005c\u0000\u0000jk\u0005o\u0000\u0000kl\u0005m\u0000\u0000" +
					"lm\u0005b\u0000\u0000mn\u0005a\u0000\u0000no\u0005t\u0000\u0000op\u0005" +
					":\u0000\u0000p\u000e\u0001\u0000\u0000\u0000qr\u0005t\u0000\u0000rs\u0005" +
					"y\u0000\u0000st\u0005p\u0000\u0000tu\u0005e\u0000\u0000uv\u0005:\u0000" +
					"\u0000v\u0010\u0001\u0000\u0000\u0000wx\u0005i\u0000\u0000xy\u0005t\u0000" +
					"\u0000yz\u0005e\u0000\u0000z{\u0005m\u0000\u0000{|\u0005:\u0000\u0000" +
					"|\u0012\u0001\u0000\u0000\u0000}~\u0005f\u0000\u0000~\u007f\u0005l\u0000" +
					"\u0000\u007f\u0080\u0005a\u0000\u0000\u0080\u0081\u0005g\u0000\u0000\u0081" +
					"\u0082\u0005s\u0000\u0000\u0082\u0083\u0005:\u0000\u0000\u0083\u0014\u0001" +
					"\u0000\u0000\u0000\u0084\u0085\u0005f\u0000\u0000\u0085\u0086\u0005l\u0000" +
					"\u0000\u0086\u0087\u0005a\u0000\u0000\u0087\u0088\u0005g\u0000\u0000\u0088" +
					"\u0089\u0005s\u0000\u0000\u0089\u008a\u0005-\u0000\u0000\u008a\u008b\u0005" +
					"o\u0000\u0000\u008b\u008c\u0005f\u0000\u0000\u008c\u008d\u0005f\u0000" +
					"\u0000\u008d\u008e\u0005:\u0000\u0000\u008e\u0016\u0001\u0000\u0000\u0000" +
					"\u008f\u0090\u0005v\u0000\u0000\u0090\u0091\u0005a\u0000\u0000\u0091\u0092" +
					"\u0005l\u0000\u0000\u0092\u0093\u0005u\u0000\u0000\u0093\u0094\u0005e" +
					"\u0000\u0000\u0094\u0095\u0005s\u0000\u0000\u0095\u0096\u0005:\u0000\u0000" +
					"\u0096\u0018\u0001\u0000\u0000\u0000\u0097\u0098\u0005m\u0000\u0000\u0098" +
					"\u0099\u0005i\u0000\u0000\u0099\u009a\u0005n\u0000\u0000\u009a\u009b\u0005" +
					"-\u0000\u0000\u009b\u009c\u0005v\u0000\u0000\u009c\u009d\u0005a\u0000" +
					"\u0000\u009d\u009e\u0005l\u0000\u0000\u009e\u009f\u0005u\u0000\u0000\u009f" +
					"\u00a0\u0005e\u0000\u0000\u00a0\u00a1\u0005s\u0000\u0000\u00a1\u00a2\u0005" +
					":\u0000\u0000\u00a2\u001a\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005a\u0000" +
					"\u0000\u00a4\u00a5\u0005c\u0000\u0000\u00a5\u00a6\u0005t\u0000\u0000\u00a6" +
					"\u00a7\u0005:\u0000\u0000\u00a7\u001c\u0001\u0000\u0000\u0000\u00a8\u00a9" +
					"\u0005t\u0000\u0000\u00a9\u00aa\u0005i\u0000\u0000\u00aa\u00ab\u0005m" +
					"\u0000\u0000\u00ab\u00ac\u0005e\u0000\u0000\u00ac\u00ad\u0005:\u0000\u0000" +
					"\u00ad\u001e\u0001\u0000\u0000\u0000\u00ae\u00af\u0005b\u0000\u0000\u00af" +
					"\u00b0\u0005r\u0000\u0000\u00b0\u00b1\u0005a\u0000\u0000\u00b1\u00b2\u0005" +
					"n\u0000\u0000\u00b2\u00b3\u0005d\u0000\u0000\u00b3\u00b4\u0005:\u0000" +
					"\u0000\u00b4 \u0001\u0000\u0000\u0000\u00b5\u00b6\u0005s\u0000\u0000\u00b6" +
					"\u00b7\u0005l\u0000\u0000\u00b7\u00b8\u0005a\u0000\u0000\u00b8\u00b9\u0005" +
					"y\u0000\u0000\u00b9\u00ba\u0005:\u0000\u0000\u00ba\"\u0001\u0000\u0000" +
					"\u0000\u00bb\u00bc\u0005c\u0000\u0000\u00bc\u00bd\u0005u\u0000\u0000\u00bd" +
					"\u00be\u0005r\u0000\u0000\u00be\u00bf\u0005s\u0000\u0000\u00bf\u00c0\u0005" +
					"e\u0000\u0000\u00c0\u00c1\u0005:\u0000\u0000\u00c1$\u0001\u0000\u0000" +
					"\u0000\u00c2\u00c3\u0005d\u0000\u0000\u00c3\u00c4\u0005e\u0000\u0000\u00c4" +
					"\u00c5\u0005s\u0000\u0000\u00c5\u00c6\u0005c\u0000\u0000\u00c6\u00c7\u0005" +
					":\u0000\u0000\u00c7&\u0001\u0000\u0000\u0000\u00c8\u00c9\u0005:\u0000" +
					"\u0000\u00c9(\u0001\u0000\u0000\u0000\u00ca\u00cc\u0007\u0001\u0000\u0000" +
					"\u00cb\u00ca\u0001\u0000\u0000\u0000\u00cc\u00cd\u0001\u0000\u0000\u0000" +
					"\u00cd\u00cb\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000" +
					"\u00ce*\u0001\u0000\u0000\u0000\u00cf\u00d1\u0007\u0002\u0000\u0000\u00d0" +
					"\u00cf\u0001\u0000\u0000\u0000\u00d1\u00d2\u0001\u0000\u0000\u0000\u00d2" +
					"\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3" +
					",\u0001\u0000\u0000\u0000\u00d4\u00d6\u0005 \u0000\u0000\u00d5\u00d4\u0001" +
					"\u0000\u0000\u0000\u00d5\u00d6\u0001\u0000\u0000\u0000\u00d6\u00d7\u0001" +
					"\u0000\u0000\u0000\u00d7\u00d9\u0005|\u0000\u0000\u00d8\u00da\u0005 \u0000" +
					"\u0000\u00d9\u00d8\u0001\u0000\u0000\u0000\u00d9\u00da\u0001\u0000\u0000" +
					"\u0000\u00da.\u0001\u0000\u0000\u0000\n\u000039@D\u00cd\u00d0\u00d2\u00d5" +
					"\u00d9\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());

	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}