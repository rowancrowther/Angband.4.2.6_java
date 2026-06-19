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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/BlowEffect.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.bloweffect;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowEffectLexer extends Lexer {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			COMMENT = 1, EOL = 2, NAME = 3, POWER = 4, EVAL = 5, DESC = 6, LORE_COLOUR_BASE = 7,
			LORE_COLOUR_RESIST = 8, LORE_COLOUR_IMMUNE = 9, EFFECT_TYPE = 10, RESIST = 11,
			LASH_TYPE = 12, INTEGER = 13, UCASE = 14, LCASE = 15;
	public static String[] channelNames = {
			"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
			"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[]{
				"COMMENT", "EOL", "NAME", "POWER", "EVAL", "DESC", "LORE_COLOUR_BASE",
				"LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "EFFECT_TYPE", "RESIST",
				"LASH_TYPE", "INTEGER", "UCASE", "LCASE"
		};
	}

	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'name:'", "'power:'", "'eval:'", "'desc:'", "'lore-color-base:'",
				"'lore-color-resist:'", "'lore-color-immune:'", "'effect-type:'", "'resist:'",
				"'lash-type:'"
		};
	}

	private static final String[] _LITERAL_NAMES = makeLiteralNames();

	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "NAME", "POWER", "EVAL", "DESC", "LORE_COLOUR_BASE",
				"LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "EFFECT_TYPE", "RESIST",
				"LASH_TYPE", "INTEGER", "UCASE", "LCASE"
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


	public BlowEffectLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@Override
	public String getGrammarFileName() {
		return "BlowEffect.g4";
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
			"\u0004\u0000\u000f\u00b9\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
					"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
					"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
					"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
					"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
					"\u0001\u0000\u0001\u0000\u0005\u0000\"\b\u0000\n\u0000\f\u0000%\t\u0000" +
					"\u0001\u0000\u0004\u0000(\b\u0000\u000b\u0000\f\u0000)\u0001\u0000\u0001" +
					"\u0000\u0001\u0001\u0005\u0001/\b\u0001\n\u0001\f\u00012\t\u0001\u0001" +
					"\u0001\u0003\u00015\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
					"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
					"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
					"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
					"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
					"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
					"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
					"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
					"\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
					"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
					"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
					"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
					"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
					"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
					"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001" +
					"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b" +
					"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
					"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0004\f\u00ac\b\f\u000b\f" +
					"\f\f\u00ad\u0001\r\u0004\r\u00b1\b\r\u000b\r\f\r\u00b2\u0001\u000e\u0004" +
					"\u000e\u00b6\b\u000e\u000b\u000e\f\u000e\u00b7\u0000\u0000\u000f\u0001" +
					"\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007" +
					"\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d" +
					"\u000f\u0001\u0000\u0003\u0001\u0000\n\n\u0003\u000009AZ__\u0005\u0000" +
					"  --AZ__az\u00bf\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001" +
					"\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001" +
					"\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000" +
					"\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000" +
					"\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000" +
					"\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000" +
					"\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000" +
					"\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0001\u001f\u0001\u0000\u0000" +
					"\u0000\u00030\u0001\u0000\u0000\u0000\u0005:\u0001\u0000\u0000\u0000\u0007" +
					"@\u0001\u0000\u0000\u0000\tG\u0001\u0000\u0000\u0000\u000bM\u0001\u0000" +
					"\u0000\u0000\rS\u0001\u0000\u0000\u0000\u000fd\u0001\u0000\u0000\u0000" +
					"\u0011w\u0001\u0000\u0000\u0000\u0013\u008a\u0001\u0000\u0000\u0000\u0015" +
					"\u0097\u0001\u0000\u0000\u0000\u0017\u009f\u0001\u0000\u0000\u0000\u0019" +
					"\u00ab\u0001\u0000\u0000\u0000\u001b\u00b0\u0001\u0000\u0000\u0000\u001d" +
					"\u00b5\u0001\u0000\u0000\u0000\u001f#\u0005#\u0000\u0000 \"\b\u0000\u0000" +
					"\u0000! \u0001\u0000\u0000\u0000\"%\u0001\u0000\u0000\u0000#!\u0001\u0000" +
					"\u0000\u0000#$\u0001\u0000\u0000\u0000$\'\u0001\u0000\u0000\u0000%#\u0001" +
					"\u0000\u0000\u0000&(\u0005\n\u0000\u0000\'&\u0001\u0000\u0000\u0000()" +
					"\u0001\u0000\u0000\u0000)\'\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000" +
					"\u0000*+\u0001\u0000\u0000\u0000+,\u0006\u0000\u0000\u0000,\u0002\u0001" +
					"\u0000\u0000\u0000-/\u0005 \u0000\u0000.-\u0001\u0000\u0000\u0000/2\u0001" +
					"\u0000\u0000\u00000.\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u0000" +
					"14\u0001\u0000\u0000\u000020\u0001\u0000\u0000\u000035\u0005\r\u0000\u0000" +
					"43\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u000056\u0001\u0000\u0000" +
					"\u000067\u0005\n\u0000\u000078\u0001\u0000\u0000\u000089\u0006\u0001\u0000" +
					"\u00009\u0004\u0001\u0000\u0000\u0000:;\u0005n\u0000\u0000;<\u0005a\u0000" +
					"\u0000<=\u0005m\u0000\u0000=>\u0005e\u0000\u0000>?\u0005:\u0000\u0000" +
					"?\u0006\u0001\u0000\u0000\u0000@A\u0005p\u0000\u0000AB\u0005o\u0000\u0000" +
					"BC\u0005w\u0000\u0000CD\u0005e\u0000\u0000DE\u0005r\u0000\u0000EF\u0005" +
					":\u0000\u0000F\b\u0001\u0000\u0000\u0000GH\u0005e\u0000\u0000HI\u0005" +
					"v\u0000\u0000IJ\u0005a\u0000\u0000JK\u0005l\u0000\u0000KL\u0005:\u0000" +
					"\u0000L\n\u0001\u0000\u0000\u0000MN\u0005d\u0000\u0000NO\u0005e\u0000" +
					"\u0000OP\u0005s\u0000\u0000PQ\u0005c\u0000\u0000QR\u0005:\u0000\u0000" +
					"R\f\u0001\u0000\u0000\u0000ST\u0005l\u0000\u0000TU\u0005o\u0000\u0000" +
					"UV\u0005r\u0000\u0000VW\u0005e\u0000\u0000WX\u0005-\u0000\u0000XY\u0005" +
					"c\u0000\u0000YZ\u0005o\u0000\u0000Z[\u0005l\u0000\u0000[\\\u0005o\u0000" +
					"\u0000\\]\u0005r\u0000\u0000]^\u0005-\u0000\u0000^_\u0005b\u0000\u0000" +
					"_`\u0005a\u0000\u0000`a\u0005s\u0000\u0000ab\u0005e\u0000\u0000bc\u0005" +
					":\u0000\u0000c\u000e\u0001\u0000\u0000\u0000de\u0005l\u0000\u0000ef\u0005" +
					"o\u0000\u0000fg\u0005r\u0000\u0000gh\u0005e\u0000\u0000hi\u0005-\u0000" +
					"\u0000ij\u0005c\u0000\u0000jk\u0005o\u0000\u0000kl\u0005l\u0000\u0000" +
					"lm\u0005o\u0000\u0000mn\u0005r\u0000\u0000no\u0005-\u0000\u0000op\u0005" +
					"r\u0000\u0000pq\u0005e\u0000\u0000qr\u0005s\u0000\u0000rs\u0005i\u0000" +
					"\u0000st\u0005s\u0000\u0000tu\u0005t\u0000\u0000uv\u0005:\u0000\u0000" +
					"v\u0010\u0001\u0000\u0000\u0000wx\u0005l\u0000\u0000xy\u0005o\u0000\u0000" +
					"yz\u0005r\u0000\u0000z{\u0005e\u0000\u0000{|\u0005-\u0000\u0000|}\u0005" +
					"c\u0000\u0000}~\u0005o\u0000\u0000~\u007f\u0005l\u0000\u0000\u007f\u0080" +
					"\u0005o\u0000\u0000\u0080\u0081\u0005r\u0000\u0000\u0081\u0082\u0005-" +
					"\u0000\u0000\u0082\u0083\u0005i\u0000\u0000\u0083\u0084\u0005m\u0000\u0000" +
					"\u0084\u0085\u0005m\u0000\u0000\u0085\u0086\u0005u\u0000\u0000\u0086\u0087" +
					"\u0005n\u0000\u0000\u0087\u0088\u0005e\u0000\u0000\u0088\u0089\u0005:" +
					"\u0000\u0000\u0089\u0012\u0001\u0000\u0000\u0000\u008a\u008b\u0005e\u0000" +
					"\u0000\u008b\u008c\u0005f\u0000\u0000\u008c\u008d\u0005f\u0000\u0000\u008d" +
					"\u008e\u0005e\u0000\u0000\u008e\u008f\u0005c\u0000\u0000\u008f\u0090\u0005" +
					"t\u0000\u0000\u0090\u0091\u0005-\u0000\u0000\u0091\u0092\u0005t\u0000" +
					"\u0000\u0092\u0093\u0005y\u0000\u0000\u0093\u0094\u0005p\u0000\u0000\u0094" +
					"\u0095\u0005e\u0000\u0000\u0095\u0096\u0005:\u0000\u0000\u0096\u0014\u0001" +
					"\u0000\u0000\u0000\u0097\u0098\u0005r\u0000\u0000\u0098\u0099\u0005e\u0000" +
					"\u0000\u0099\u009a\u0005s\u0000\u0000\u009a\u009b\u0005i\u0000\u0000\u009b" +
					"\u009c\u0005s\u0000\u0000\u009c\u009d\u0005t\u0000\u0000\u009d\u009e\u0005" +
					":\u0000\u0000\u009e\u0016\u0001\u0000\u0000\u0000\u009f\u00a0\u0005l\u0000" +
					"\u0000\u00a0\u00a1\u0005a\u0000\u0000\u00a1\u00a2\u0005s\u0000\u0000\u00a2" +
					"\u00a3\u0005h\u0000\u0000\u00a3\u00a4\u0005-\u0000\u0000\u00a4\u00a5\u0005" +
					"t\u0000\u0000\u00a5\u00a6\u0005y\u0000\u0000\u00a6\u00a7\u0005p\u0000" +
					"\u0000\u00a7\u00a8\u0005e\u0000\u0000\u00a8\u00a9\u0005:\u0000\u0000\u00a9" +
					"\u0018\u0001\u0000\u0000\u0000\u00aa\u00ac\u000209\u0000\u00ab\u00aa\u0001" +
					"\u0000\u0000\u0000\u00ac\u00ad\u0001\u0000\u0000\u0000\u00ad\u00ab\u0001" +
					"\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u001a\u0001" +
					"\u0000\u0000\u0000\u00af\u00b1\u0007\u0001\u0000\u0000\u00b0\u00af\u0001" +
					"\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2\u00b0\u0001" +
					"\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000\u0000\u0000\u00b3\u001c\u0001" +
					"\u0000\u0000\u0000\u00b4\u00b6\u0007\u0002\u0000\u0000\u00b5\u00b4\u0001" +
					"\u0000\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u00b5\u0001" +
					"\u0000\u0000\u0000\u00b7\u00b8\u0001\u0000\u0000\u0000\u00b8\u001e\u0001" +
					"\u0000\u0000\u0000\b\u0000#)04\u00ad\u00b2\u00b7\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());

	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}