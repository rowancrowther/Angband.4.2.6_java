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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/Terrain.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.terrainfeature;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class TerrainLexer extends Lexer {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			COMMENT = 1, EOL = 2, CODE = 3, NAME = 4, GRAPHICS = 5, MIMIC = 6, PRIORITY = 7, FLAGS = 8,
			DIGGING = 9, WALK_MSG = 10, RUN_MSG = 11, HURT_MSG = 12, DIE_MSG = 13, CONFUSED_MSG = 14,
			LOOK_PREFIX = 15, LOOK_IN_PREP = 16, RESIST_FLAG = 17, DESC = 18, COLON = 19, OR = 20,
			SINGLE_CHAR = 21, NUMBER = 22, TEXT = 23;
	public static String[] channelNames = {
			"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
			"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[]{
				"COMMENT", "EOL", "CODE", "NAME", "GRAPHICS", "MIMIC", "PRIORITY", "FLAGS",
				"DIGGING", "WALK_MSG", "RUN_MSG", "HURT_MSG", "DIE_MSG", "CONFUSED_MSG",
				"LOOK_PREFIX", "LOOK_IN_PREP", "RESIST_FLAG", "DESC", "COLON", "OR",
				"SINGLE_CHAR", "NUMBER", "TEXT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'code:'", "'name:'", "'graphics:'", "'mimic:'", "'priority:'",
				"'flags:'", "'digging:'", "'walk-msg:'", "'run-msg:'", "'hurt-msg:'",
				"'die-msg:'", "'confused-msg:'", "'look-prefix:'", "'look-in-preposition:'",
				"'resist-flag:'", "'desc:'", "':'", "'| '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "CODE", "NAME", "GRAPHICS", "MIMIC", "PRIORITY",
				"FLAGS", "DIGGING", "WALK_MSG", "RUN_MSG", "HURT_MSG", "DIE_MSG", "CONFUSED_MSG",
				"LOOK_PREFIX", "LOOK_IN_PREP", "RESIST_FLAG", "DESC", "COLON", "OR",
				"SINGLE_CHAR", "NUMBER", "TEXT"
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


	public TerrainLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@Override
	public String getGrammarFileName() {
		return "Terrain.g4";
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
			"\u0004\u0000\u0017\u00fd\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
					"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
					"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
					"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
					"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
					"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011" +
					"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014" +
					"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0001\u0000\u0001\u0000" +
					"\u0001\u0000\u0005\u00003\b\u0000\n\u0000\f\u00006\t\u0000\u0001\u0000" +
					"\u0004\u00009\b\u0000\u000b\u0000\f\u0000:\u0001\u0000\u0001\u0000\u0001" +
					"\u0001\u0005\u0001@\b\u0001\n\u0001\f\u0001C\t\u0001\u0001\u0001\u0003" +
					"\u0001F\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
					"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
					"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
					"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
					"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001" +
					"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001" +
					"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
					"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
					"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001" +
					"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001" +
					"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001" +
					"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b" +
					"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
					"\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
					"\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
					"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
					"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001" +
					"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001" +
					"\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
					"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
					"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
					"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001" +
					"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
					"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001" +
					"\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001" +
					"\u0014\u0001\u0015\u0004\u0015\u00f5\b\u0015\u000b\u0015\f\u0015\u00f6" +
					"\u0001\u0016\u0004\u0016\u00fa\b\u0016\u000b\u0016\f\u0016\u00fb\u0000" +
					"\u0000\u0017\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b" +
					"\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b" +
					"\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016" +
					"-\u0017\u0001\u0000\u0005\u0001\u0000::\u0001\u0000\n\n\n\u0000  ##%%" +
					"\'\'*,..18::<<>>\u0001\u000009\b\u0000 !,.09;;??AZ__az\u0102\u0000\u0001" +
					"\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005" +
					"\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001" +
					"\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000" +
					"\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000" +
					"\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000" +
					"\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000" +
					"\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000" +
					"\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000" +
					"\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000" +
					"\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001" +
					"\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0001/\u0001\u0000\u0000" +
					"\u0000\u0003A\u0001\u0000\u0000\u0000\u0005K\u0001\u0000\u0000\u0000\u0007" +
					"Q\u0001\u0000\u0000\u0000\tW\u0001\u0000\u0000\u0000\u000ba\u0001\u0000" +
					"\u0000\u0000\rh\u0001\u0000\u0000\u0000\u000fr\u0001\u0000\u0000\u0000" +
					"\u0011y\u0001\u0000\u0000\u0000\u0013\u0082\u0001\u0000\u0000\u0000\u0015" +
					"\u008c\u0001\u0000\u0000\u0000\u0017\u0095\u0001\u0000\u0000\u0000\u0019" +
					"\u009f\u0001\u0000\u0000\u0000\u001b\u00a8\u0001\u0000\u0000\u0000\u001d" +
					"\u00b6\u0001\u0000\u0000\u0000\u001f\u00c3\u0001\u0000\u0000\u0000!\u00d8" +
					"\u0001\u0000\u0000\u0000#\u00e5\u0001\u0000\u0000\u0000%\u00eb\u0001\u0000" +
					"\u0000\u0000\'\u00ed\u0001\u0000\u0000\u0000)\u00f0\u0001\u0000\u0000" +
					"\u0000+\u00f4\u0001\u0000\u0000\u0000-\u00f9\u0001\u0000\u0000\u0000/" +
					"0\u0005#\u0000\u000004\b\u0000\u0000\u000013\b\u0001\u0000\u000021\u0001" +
					"\u0000\u0000\u000036\u0001\u0000\u0000\u000042\u0001\u0000\u0000\u0000" +
					"45\u0001\u0000\u0000\u000058\u0001\u0000\u0000\u000064\u0001\u0000\u0000" +
					"\u000079\u0005\n\u0000\u000087\u0001\u0000\u0000\u00009:\u0001\u0000\u0000" +
					"\u0000:8\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;<\u0001\u0000" +
					"\u0000\u0000<=\u0006\u0000\u0000\u0000=\u0002\u0001\u0000\u0000\u0000" +
					">@\u0005 \u0000\u0000?>\u0001\u0000\u0000\u0000@C\u0001\u0000\u0000\u0000" +
					"A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BE\u0001\u0000\u0000" +
					"\u0000CA\u0001\u0000\u0000\u0000DF\u0005\r\u0000\u0000ED\u0001\u0000\u0000" +
					"\u0000EF\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GH\u0005\n\u0000" +
					"\u0000HI\u0001\u0000\u0000\u0000IJ\u0006\u0001\u0000\u0000J\u0004\u0001" +
					"\u0000\u0000\u0000KL\u0005c\u0000\u0000LM\u0005o\u0000\u0000MN\u0005d" +
					"\u0000\u0000NO\u0005e\u0000\u0000OP\u0005:\u0000\u0000P\u0006\u0001\u0000" +
					"\u0000\u0000QR\u0005n\u0000\u0000RS\u0005a\u0000\u0000ST\u0005m\u0000" +
					"\u0000TU\u0005e\u0000\u0000UV\u0005:\u0000\u0000V\b\u0001\u0000\u0000" +
					"\u0000WX\u0005g\u0000\u0000XY\u0005r\u0000\u0000YZ\u0005a\u0000\u0000" +
					"Z[\u0005p\u0000\u0000[\\\u0005h\u0000\u0000\\]\u0005i\u0000\u0000]^\u0005" +
					"c\u0000\u0000^_\u0005s\u0000\u0000_`\u0005:\u0000\u0000`\n\u0001\u0000" +
					"\u0000\u0000ab\u0005m\u0000\u0000bc\u0005i\u0000\u0000cd\u0005m\u0000" +
					"\u0000de\u0005i\u0000\u0000ef\u0005c\u0000\u0000fg\u0005:\u0000\u0000" +
					"g\f\u0001\u0000\u0000\u0000hi\u0005p\u0000\u0000ij\u0005r\u0000\u0000" +
					"jk\u0005i\u0000\u0000kl\u0005o\u0000\u0000lm\u0005r\u0000\u0000mn\u0005" +
					"i\u0000\u0000no\u0005t\u0000\u0000op\u0005y\u0000\u0000pq\u0005:\u0000" +
					"\u0000q\u000e\u0001\u0000\u0000\u0000rs\u0005f\u0000\u0000st\u0005l\u0000" +
					"\u0000tu\u0005a\u0000\u0000uv\u0005g\u0000\u0000vw\u0005s\u0000\u0000" +
					"wx\u0005:\u0000\u0000x\u0010\u0001\u0000\u0000\u0000yz\u0005d\u0000\u0000" +
					"z{\u0005i\u0000\u0000{|\u0005g\u0000\u0000|}\u0005g\u0000\u0000}~\u0005" +
					"i\u0000\u0000~\u007f\u0005n\u0000\u0000\u007f\u0080\u0005g\u0000\u0000" +
					"\u0080\u0081\u0005:\u0000\u0000\u0081\u0012\u0001\u0000\u0000\u0000\u0082" +
					"\u0083\u0005w\u0000\u0000\u0083\u0084\u0005a\u0000\u0000\u0084\u0085\u0005" +
					"l\u0000\u0000\u0085\u0086\u0005k\u0000\u0000\u0086\u0087\u0005-\u0000" +
					"\u0000\u0087\u0088\u0005m\u0000\u0000\u0088\u0089\u0005s\u0000\u0000\u0089" +
					"\u008a\u0005g\u0000\u0000\u008a\u008b\u0005:\u0000\u0000\u008b\u0014\u0001" +
					"\u0000\u0000\u0000\u008c\u008d\u0005r\u0000\u0000\u008d\u008e\u0005u\u0000" +
					"\u0000\u008e\u008f\u0005n\u0000\u0000\u008f\u0090\u0005-\u0000\u0000\u0090" +
					"\u0091\u0005m\u0000\u0000\u0091\u0092\u0005s\u0000\u0000\u0092\u0093\u0005" +
					"g\u0000\u0000\u0093\u0094\u0005:\u0000\u0000\u0094\u0016\u0001\u0000\u0000" +
					"\u0000\u0095\u0096\u0005h\u0000\u0000\u0096\u0097\u0005u\u0000\u0000\u0097" +
					"\u0098\u0005r\u0000\u0000\u0098\u0099\u0005t\u0000\u0000\u0099\u009a\u0005" +
					"-\u0000\u0000\u009a\u009b\u0005m\u0000\u0000\u009b\u009c\u0005s\u0000" +
					"\u0000\u009c\u009d\u0005g\u0000\u0000\u009d\u009e\u0005:\u0000\u0000\u009e" +
					"\u0018\u0001\u0000\u0000\u0000\u009f\u00a0\u0005d\u0000\u0000\u00a0\u00a1" +
					"\u0005i\u0000\u0000\u00a1\u00a2\u0005e\u0000\u0000\u00a2\u00a3\u0005-" +
					"\u0000\u0000\u00a3\u00a4\u0005m\u0000\u0000\u00a4\u00a5\u0005s\u0000\u0000" +
					"\u00a5\u00a6\u0005g\u0000\u0000\u00a6\u00a7\u0005:\u0000\u0000\u00a7\u001a" +
					"\u0001\u0000\u0000\u0000\u00a8\u00a9\u0005c\u0000\u0000\u00a9\u00aa\u0005" +
					"o\u0000\u0000\u00aa\u00ab\u0005n\u0000\u0000\u00ab\u00ac\u0005f\u0000" +
					"\u0000\u00ac\u00ad\u0005u\u0000\u0000\u00ad\u00ae\u0005s\u0000\u0000\u00ae" +
					"\u00af\u0005e\u0000\u0000\u00af\u00b0\u0005d\u0000\u0000\u00b0\u00b1\u0005" +
					"-\u0000\u0000\u00b1\u00b2\u0005m\u0000\u0000\u00b2\u00b3\u0005s\u0000" +
					"\u0000\u00b3\u00b4\u0005g\u0000\u0000\u00b4\u00b5\u0005:\u0000\u0000\u00b5" +
					"\u001c\u0001\u0000\u0000\u0000\u00b6\u00b7\u0005l\u0000\u0000\u00b7\u00b8" +
					"\u0005o\u0000\u0000\u00b8\u00b9\u0005o\u0000\u0000\u00b9\u00ba\u0005k" +
					"\u0000\u0000\u00ba\u00bb\u0005-\u0000\u0000\u00bb\u00bc\u0005p\u0000\u0000" +
					"\u00bc\u00bd\u0005r\u0000\u0000\u00bd\u00be\u0005e\u0000\u0000\u00be\u00bf" +
					"\u0005f\u0000\u0000\u00bf\u00c0\u0005i\u0000\u0000\u00c0\u00c1\u0005x" +
					"\u0000\u0000\u00c1\u00c2\u0005:\u0000\u0000\u00c2\u001e\u0001\u0000\u0000" +
					"\u0000\u00c3\u00c4\u0005l\u0000\u0000\u00c4\u00c5\u0005o\u0000\u0000\u00c5" +
					"\u00c6\u0005o\u0000\u0000\u00c6\u00c7\u0005k\u0000\u0000\u00c7\u00c8\u0005" +
					"-\u0000\u0000\u00c8\u00c9\u0005i\u0000\u0000\u00c9\u00ca\u0005n\u0000" +
					"\u0000\u00ca\u00cb\u0005-\u0000\u0000\u00cb\u00cc\u0005p\u0000\u0000\u00cc" +
					"\u00cd\u0005r\u0000\u0000\u00cd\u00ce\u0005e\u0000\u0000\u00ce\u00cf\u0005" +
					"p\u0000\u0000\u00cf\u00d0\u0005o\u0000\u0000\u00d0\u00d1\u0005s\u0000" +
					"\u0000\u00d1\u00d2\u0005i\u0000\u0000\u00d2\u00d3\u0005t\u0000\u0000\u00d3" +
					"\u00d4\u0005i\u0000\u0000\u00d4\u00d5\u0005o\u0000\u0000\u00d5\u00d6\u0005" +
					"n\u0000\u0000\u00d6\u00d7\u0005:\u0000\u0000\u00d7 \u0001\u0000\u0000" +
					"\u0000\u00d8\u00d9\u0005r\u0000\u0000\u00d9\u00da\u0005e\u0000\u0000\u00da" +
					"\u00db\u0005s\u0000\u0000\u00db\u00dc\u0005i\u0000\u0000\u00dc\u00dd\u0005" +
					"s\u0000\u0000\u00dd\u00de\u0005t\u0000\u0000\u00de\u00df\u0005-\u0000" +
					"\u0000\u00df\u00e0\u0005f\u0000\u0000\u00e0\u00e1\u0005l\u0000\u0000\u00e1" +
					"\u00e2\u0005a\u0000\u0000\u00e2\u00e3\u0005g\u0000\u0000\u00e3\u00e4\u0005" +
					":\u0000\u0000\u00e4\"\u0001\u0000\u0000\u0000\u00e5\u00e6\u0005d\u0000" +
					"\u0000\u00e6\u00e7\u0005e\u0000\u0000\u00e7\u00e8\u0005s\u0000\u0000\u00e8" +
					"\u00e9\u0005c\u0000\u0000\u00e9\u00ea\u0005:\u0000\u0000\u00ea$\u0001" +
					"\u0000\u0000\u0000\u00eb\u00ec\u0005:\u0000\u0000\u00ec&\u0001\u0000\u0000" +
					"\u0000\u00ed\u00ee\u0005|\u0000\u0000\u00ee\u00ef\u0005 \u0000\u0000\u00ef" +
					"(\u0001\u0000\u0000\u0000\u00f0\u00f1\u0007\u0002\u0000\u0000\u00f1\u00f2" +
					"\u0005:\u0000\u0000\u00f2*\u0001\u0000\u0000\u0000\u00f3\u00f5\u0007\u0003" +
					"\u0000\u0000\u00f4\u00f3\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000" +
					"\u0000\u0000\u00f6\u00f4\u0001\u0000\u0000\u0000\u00f6\u00f7\u0001\u0000" +
					"\u0000\u0000\u00f7,\u0001\u0000\u0000\u0000\u00f8\u00fa\u0007\u0004\u0000" +
					"\u0000\u00f9\u00f8\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000" +
					"\u0000\u00fb\u00f9\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000\u0000" +
					"\u0000\u00fc.\u0001\u0000\u0000\u0000\u0007\u00004:AE\u00f6\u00fb\u0001" +
					"\u0006\u0000\u0000";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}