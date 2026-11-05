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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/BlowEffectLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.bloweffect;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowEffectLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, POWER=2, EVAL=3, DESC=4, LORE_COLOUR_BASE=5, LORE_COLOUR_RESIST=6, 
		LORE_COLOUR_IMMUNE=7, EFFECT_TYPE=8, RESIST=9, LASH=10, INTEGER=11, COMMENT=12, 
		EOL=13, STRING=14, EOL_POP=15;
	public static final int
		FREE_TEXT_MODE=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "FREE_TEXT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "POWER", "EVAL", "DESC", "LORE_COLOUR_BASE", "LORE_COLOUR_RESIST", 
			"LORE_COLOUR_IMMUNE", "EFFECT_TYPE", "RESIST", "LASH", "INTEGER", "COMMENT", 
			"EOL", "STRING", "EOL_POP"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'power:'", "'eval:'", "'desc:'", "'lore-color-base:'", 
			"'lore-color-resist:'", "'lore-color-immune:'", "'effect-type:'", "'resist:'", 
			"'lash-type:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "POWER", "EVAL", "DESC", "LORE_COLOUR_BASE", "LORE_COLOUR_RESIST", 
			"LORE_COLOUR_IMMUNE", "EFFECT_TYPE", "RESIST", "LASH", "INTEGER", "COMMENT", 
			"EOL", "STRING", "EOL_POP"
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
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BlowEffectLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u000f\u00d0\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000"+
		"\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003"+
		"\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006"+
		"\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002"+
		"\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002"+
		"\u000e\u0007\u000e\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\n\u0003\n\u00a2\b\n\u0001\n\u0004\n\u00a5"+
		"\b\n\u000b\n\f\n\u00a6\u0001\u000b\u0001\u000b\u0005\u000b\u00ab\b\u000b"+
		"\n\u000b\f\u000b\u00ae\t\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\f\u0005\f\u00b5\b\f\n\f\f\f\u00b8\t\f\u0001\f\u0003\f\u00bb"+
		"\b\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0004\r\u00c2\b\r\u000b\r"+
		"\f\r\u00c3\u0001\u000e\u0005\u000e\u00c7\b\u000e\n\u000e\f\u000e\u00ca"+
		"\t\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0000"+
		"\u0000\u000f\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006"+
		"\u000e\u0007\u0010\b\u0012\t\u0014\n\u0016\u000b\u0018\f\u001a\r\u001c"+
		"\u000e\u001e\u000f\u0002\u0000\u0001\u0002\u0001\u0000\n\n\u0002\u0000"+
		"\n\n\r\r\u00d5\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000"+
		"\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000"+
		"\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000"+
		"\u0000\u000e\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000\u0000"+
		"\u0000\u0012\u0001\u0000\u0000\u0000\u0000\u0014\u0001\u0000\u0000\u0000"+
		"\u0000\u0016\u0001\u0000\u0000\u0000\u0000\u0018\u0001\u0000\u0000\u0000"+
		"\u0000\u001a\u0001\u0000\u0000\u0000\u0001\u001c\u0001\u0000\u0000\u0000"+
		"\u0001\u001e\u0001\u0000\u0000\u0000\u0002 \u0001\u0000\u0000\u0000\u0004"+
		"(\u0001\u0000\u0000\u0000\u0006/\u0001\u0000\u0000\u0000\b5\u0001\u0000"+
		"\u0000\u0000\n=\u0001\u0000\u0000\u0000\fP\u0001\u0000\u0000\u0000\u000e"+
		"e\u0001\u0000\u0000\u0000\u0010z\u0001\u0000\u0000\u0000\u0012\u0089\u0001"+
		"\u0000\u0000\u0000\u0014\u0093\u0001\u0000\u0000\u0000\u0016\u00a1\u0001"+
		"\u0000\u0000\u0000\u0018\u00a8\u0001\u0000\u0000\u0000\u001a\u00b6\u0001"+
		"\u0000\u0000\u0000\u001c\u00c1\u0001\u0000\u0000\u0000\u001e\u00c8\u0001"+
		"\u0000\u0000\u0000 !\u0005n\u0000\u0000!\"\u0005a\u0000\u0000\"#\u0005"+
		"m\u0000\u0000#$\u0005e\u0000\u0000$%\u0005:\u0000\u0000%&\u0001\u0000"+
		"\u0000\u0000&\'\u0006\u0000\u0000\u0000\'\u0003\u0001\u0000\u0000\u0000"+
		"()\u0005p\u0000\u0000)*\u0005o\u0000\u0000*+\u0005w\u0000\u0000+,\u0005"+
		"e\u0000\u0000,-\u0005r\u0000\u0000-.\u0005:\u0000\u0000.\u0005\u0001\u0000"+
		"\u0000\u0000/0\u0005e\u0000\u000001\u0005v\u0000\u000012\u0005a\u0000"+
		"\u000023\u0005l\u0000\u000034\u0005:\u0000\u00004\u0007\u0001\u0000\u0000"+
		"\u000056\u0005d\u0000\u000067\u0005e\u0000\u000078\u0005s\u0000\u0000"+
		"89\u0005c\u0000\u00009:\u0005:\u0000\u0000:;\u0001\u0000\u0000\u0000;"+
		"<\u0006\u0003\u0000\u0000<\t\u0001\u0000\u0000\u0000=>\u0005l\u0000\u0000"+
		">?\u0005o\u0000\u0000?@\u0005r\u0000\u0000@A\u0005e\u0000\u0000AB\u0005"+
		"-\u0000\u0000BC\u0005c\u0000\u0000CD\u0005o\u0000\u0000DE\u0005l\u0000"+
		"\u0000EF\u0005o\u0000\u0000FG\u0005r\u0000\u0000GH\u0005-\u0000\u0000"+
		"HI\u0005b\u0000\u0000IJ\u0005a\u0000\u0000JK\u0005s\u0000\u0000KL\u0005"+
		"e\u0000\u0000LM\u0005:\u0000\u0000MN\u0001\u0000\u0000\u0000NO\u0006\u0004"+
		"\u0000\u0000O\u000b\u0001\u0000\u0000\u0000PQ\u0005l\u0000\u0000QR\u0005"+
		"o\u0000\u0000RS\u0005r\u0000\u0000ST\u0005e\u0000\u0000TU\u0005-\u0000"+
		"\u0000UV\u0005c\u0000\u0000VW\u0005o\u0000\u0000WX\u0005l\u0000\u0000"+
		"XY\u0005o\u0000\u0000YZ\u0005r\u0000\u0000Z[\u0005-\u0000\u0000[\\\u0005"+
		"r\u0000\u0000\\]\u0005e\u0000\u0000]^\u0005s\u0000\u0000^_\u0005i\u0000"+
		"\u0000_`\u0005s\u0000\u0000`a\u0005t\u0000\u0000ab\u0005:\u0000\u0000"+
		"bc\u0001\u0000\u0000\u0000cd\u0006\u0005\u0000\u0000d\r\u0001\u0000\u0000"+
		"\u0000ef\u0005l\u0000\u0000fg\u0005o\u0000\u0000gh\u0005r\u0000\u0000"+
		"hi\u0005e\u0000\u0000ij\u0005-\u0000\u0000jk\u0005c\u0000\u0000kl\u0005"+
		"o\u0000\u0000lm\u0005l\u0000\u0000mn\u0005o\u0000\u0000no\u0005r\u0000"+
		"\u0000op\u0005-\u0000\u0000pq\u0005i\u0000\u0000qr\u0005m\u0000\u0000"+
		"rs\u0005m\u0000\u0000st\u0005u\u0000\u0000tu\u0005n\u0000\u0000uv\u0005"+
		"e\u0000\u0000vw\u0005:\u0000\u0000wx\u0001\u0000\u0000\u0000xy\u0006\u0006"+
		"\u0000\u0000y\u000f\u0001\u0000\u0000\u0000z{\u0005e\u0000\u0000{|\u0005"+
		"f\u0000\u0000|}\u0005f\u0000\u0000}~\u0005e\u0000\u0000~\u007f\u0005c"+
		"\u0000\u0000\u007f\u0080\u0005t\u0000\u0000\u0080\u0081\u0005-\u0000\u0000"+
		"\u0081\u0082\u0005t\u0000\u0000\u0082\u0083\u0005y\u0000\u0000\u0083\u0084"+
		"\u0005p\u0000\u0000\u0084\u0085\u0005e\u0000\u0000\u0085\u0086\u0005:"+
		"\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000\u0087\u0088\u0006\u0007"+
		"\u0000\u0000\u0088\u0011\u0001\u0000\u0000\u0000\u0089\u008a\u0005r\u0000"+
		"\u0000\u008a\u008b\u0005e\u0000\u0000\u008b\u008c\u0005s\u0000\u0000\u008c"+
		"\u008d\u0005i\u0000\u0000\u008d\u008e\u0005s\u0000\u0000\u008e\u008f\u0005"+
		"t\u0000\u0000\u008f\u0090\u0005:\u0000\u0000\u0090\u0091\u0001\u0000\u0000"+
		"\u0000\u0091\u0092\u0006\b\u0000\u0000\u0092\u0013\u0001\u0000\u0000\u0000"+
		"\u0093\u0094\u0005l\u0000\u0000\u0094\u0095\u0005a\u0000\u0000\u0095\u0096"+
		"\u0005s\u0000\u0000\u0096\u0097\u0005h\u0000\u0000\u0097\u0098\u0005-"+
		"\u0000\u0000\u0098\u0099\u0005t\u0000\u0000\u0099\u009a\u0005y\u0000\u0000"+
		"\u009a\u009b\u0005p\u0000\u0000\u009b\u009c\u0005e\u0000\u0000\u009c\u009d"+
		"\u0005:\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000\u009e\u009f\u0006"+
		"\t\u0000\u0000\u009f\u0015\u0001\u0000\u0000\u0000\u00a0\u00a2\u0005-"+
		"\u0000\u0000\u00a1\u00a0\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000"+
		"\u0000\u0000\u00a2\u00a4\u0001\u0000\u0000\u0000\u00a3\u00a5\u000209\u0000"+
		"\u00a4\u00a3\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000"+
		"\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000"+
		"\u00a7\u0017\u0001\u0000\u0000\u0000\u00a8\u00ac\u0005#\u0000\u0000\u00a9"+
		"\u00ab\b\u0000\u0000\u0000\u00aa\u00a9\u0001\u0000\u0000\u0000\u00ab\u00ae"+
		"\u0001\u0000\u0000\u0000\u00ac\u00aa\u0001\u0000\u0000\u0000\u00ac\u00ad"+
		"\u0001\u0000\u0000\u0000\u00ad\u00af\u0001\u0000\u0000\u0000\u00ae\u00ac"+
		"\u0001\u0000\u0000\u0000\u00af\u00b0\u0005\n\u0000\u0000\u00b0\u00b1\u0001"+
		"\u0000\u0000\u0000\u00b1\u00b2\u0006\u000b\u0001\u0000\u00b2\u0019\u0001"+
		"\u0000\u0000\u0000\u00b3\u00b5\u0005 \u0000\u0000\u00b4\u00b3\u0001\u0000"+
		"\u0000\u0000\u00b5\u00b8\u0001\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000"+
		"\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u00ba\u0001\u0000"+
		"\u0000\u0000\u00b8\u00b6\u0001\u0000\u0000\u0000\u00b9\u00bb\u0005\r\u0000"+
		"\u0000\u00ba\u00b9\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000"+
		"\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc\u00bd\u0005\n\u0000\u0000"+
		"\u00bd\u00be\u0001\u0000\u0000\u0000\u00be\u00bf\u0006\f\u0001\u0000\u00bf"+
		"\u001b\u0001\u0000\u0000\u0000\u00c0\u00c2\b\u0001\u0000\u0000\u00c1\u00c0"+
		"\u0001\u0000\u0000\u0000\u00c2\u00c3\u0001\u0000\u0000\u0000\u00c3\u00c1"+
		"\u0001\u0000\u0000\u0000\u00c3\u00c4\u0001\u0000\u0000\u0000\u00c4\u001d"+
		"\u0001\u0000\u0000\u0000\u00c5\u00c7\u0005\r\u0000\u0000\u00c6\u00c5\u0001"+
		"\u0000\u0000\u0000\u00c7\u00ca\u0001\u0000\u0000\u0000\u00c8\u00c6\u0001"+
		"\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\u00cb\u0001"+
		"\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00cb\u00cc\u0005"+
		"\n\u0000\u0000\u00cc\u00cd\u0001\u0000\u0000\u0000\u00cd\u00ce\u0006\u000e"+
		"\u0001\u0000\u00ce\u00cf\u0006\u000e\u0002\u0000\u00cf\u001f\u0001\u0000"+
		"\u0000\u0000\t\u0000\u0001\u00a1\u00a6\u00ac\u00b6\u00ba\u00c3\u00c8\u0003"+
		"\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}