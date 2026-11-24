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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/scratch/pt9/imports/AngbandDisplayCharacter.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class AngbandDisplayCharacter extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		GRAPHICS=1, COLOUR_ONLY=2, GLYPH_ONLY=3, COMMENT=4, INDENT=5, EOL=6, COLOUR_VALUES=7, 
		GLYPH_VALUES=8, GLYPH_COLON_SWITCH=9, GLYPH_EOL=10;
	public static final int
		COLOUR_MODE=1, GLYPH_MODE=2, POST_GLYPH_MODE=3;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "COLOUR_MODE", "GLYPH_MODE", "POST_GLYPH_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"GRAPHICS", "COLOUR_ONLY", "GLYPH_ONLY", "COMMENT", "NL", "NEWLINE", 
			"INDENT", "EOL", "COLOUR_VALUES", "GLYPH_VALUES", "GLYPH_COLON_SWITCH", 
			"GLYPH_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'graphics:'", "'color:'", "'glyph:'", null, null, null, null, 
			null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "GRAPHICS", "COLOUR_ONLY", "GLYPH_ONLY", "COMMENT", "INDENT", "EOL", 
			"COLOUR_VALUES", "GLYPH_VALUES", "GLYPH_COLON_SWITCH", "GLYPH_EOL"
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


	public AngbandDisplayCharacter(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "AngbandDisplayCharacter.g4"; }

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

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 6:
			return INDENT_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean INDENT_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return  getCharPositionInLine() == 0 ;
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0000\n\u008b\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff"+
		"\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0005\u0003=\b\u0003\n\u0003\f\u0003@\t\u0003\u0001"+
		"\u0003\u0001\u0003\u0003\u0003D\b\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0005\u0004I\b\u0004\n\u0004\f\u0004L\t\u0004\u0001\u0004\u0004"+
		"\u0004O\b\u0004\u000b\u0004\f\u0004P\u0001\u0004\u0003\u0004T\b\u0004"+
		"\u0001\u0004\u0003\u0004W\b\u0004\u0001\u0004\u0004\u0004Z\b\u0004\u000b"+
		"\u0004\f\u0004[\u0001\u0004\u0003\u0004_\b\u0004\u0001\u0005\u0004\u0005"+
		"b\b\u0005\u000b\u0005\f\u0005c\u0001\u0005\u0003\u0005g\b\u0005\u0001"+
		"\u0005\u0003\u0005j\b\u0005\u0001\u0006\u0001\u0006\u0004\u0006n\b\u0006"+
		"\u000b\u0006\f\u0006o\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0004\by\b\b\u000b\b\f\bz\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0000\u0000\f\u0004"+
		"\u0001\u0006\u0002\b\u0003\n\u0004\f\u0000\u000e\u0000\u0010\u0005\u0012"+
		"\u0006\u0014\u0007\u0016\b\u0018\t\u001a\n\u0004\u0000\u0001\u0002\u0003"+
		"\u0002\u0002\u0000\n\n\r\r\u0002\u0000\t\t  \u0092\u0000\u0004\u0001\u0000"+
		"\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000"+
		"\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000\u0000"+
		"\u0000\u0012\u0001\u0000\u0000\u0000\u0001\u0014\u0001\u0000\u0000\u0000"+
		"\u0002\u0016\u0001\u0000\u0000\u0000\u0003\u0018\u0001\u0000\u0000\u0000"+
		"\u0003\u001a\u0001\u0000\u0000\u0000\u0004\u001c\u0001\u0000\u0000\u0000"+
		"\u0006(\u0001\u0000\u0000\u0000\b1\u0001\u0000\u0000\u0000\n:\u0001\u0000"+
		"\u0000\u0000\f^\u0001\u0000\u0000\u0000\u000ei\u0001\u0000\u0000\u0000"+
		"\u0010k\u0001\u0000\u0000\u0000\u0012s\u0001\u0000\u0000\u0000\u0014x"+
		"\u0001\u0000\u0000\u0000\u0016~\u0001\u0000\u0000\u0000\u0018\u0082\u0001"+
		"\u0000\u0000\u0000\u001a\u0086\u0001\u0000\u0000\u0000\u001c\u001d\u0005"+
		"g\u0000\u0000\u001d\u001e\u0005r\u0000\u0000\u001e\u001f\u0005a\u0000"+
		"\u0000\u001f \u0005p\u0000\u0000 !\u0005h\u0000\u0000!\"\u0005i\u0000"+
		"\u0000\"#\u0005c\u0000\u0000#$\u0005s\u0000\u0000$%\u0005:\u0000\u0000"+
		"%&\u0001\u0000\u0000\u0000&\'\u0006\u0000\u0000\u0000\'\u0005\u0001\u0000"+
		"\u0000\u0000()\u0005c\u0000\u0000)*\u0005o\u0000\u0000*+\u0005l\u0000"+
		"\u0000+,\u0005o\u0000\u0000,-\u0005r\u0000\u0000-.\u0005:\u0000\u0000"+
		"./\u0001\u0000\u0000\u0000/0\u0006\u0001\u0001\u00000\u0007\u0001\u0000"+
		"\u0000\u000012\u0005g\u0000\u000023\u0005l\u0000\u000034\u0005y\u0000"+
		"\u000045\u0005p\u0000\u000056\u0005h\u0000\u000067\u0005:\u0000\u0000"+
		"78\u0001\u0000\u0000\u000089\u0006\u0002\u0000\u00009\t\u0001\u0000\u0000"+
		"\u0000:>\u0005#\u0000\u0000;=\b\u0000\u0000\u0000<;\u0001\u0000\u0000"+
		"\u0000=@\u0001\u0000\u0000\u0000><\u0001\u0000\u0000\u0000>?\u0001\u0000"+
		"\u0000\u0000?C\u0001\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000AD\u0003"+
		"\f\u0004\u0000BD\u0005\u0000\u0000\u0001CA\u0001\u0000\u0000\u0000CB\u0001"+
		"\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0006\u0003\u0002\u0000"+
		"F\u000b\u0001\u0000\u0000\u0000GI\u0007\u0001\u0000\u0000HG\u0001\u0000"+
		"\u0000\u0000IL\u0001\u0000\u0000\u0000JH\u0001\u0000\u0000\u0000JK\u0001"+
		"\u0000\u0000\u0000KV\u0001\u0000\u0000\u0000LJ\u0001\u0000\u0000\u0000"+
		"MO\u0005\r\u0000\u0000NM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000"+
		"PN\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000QS\u0001\u0000\u0000"+
		"\u0000RT\u0005\n\u0000\u0000SR\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000"+
		"\u0000TW\u0001\u0000\u0000\u0000UW\u0005\n\u0000\u0000VN\u0001\u0000\u0000"+
		"\u0000VU\u0001\u0000\u0000\u0000W_\u0001\u0000\u0000\u0000XZ\u0007\u0001"+
		"\u0000\u0000YX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[Y\u0001"+
		"\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000"+
		"]_\u0005\u0000\u0000\u0001^J\u0001\u0000\u0000\u0000^Y\u0001\u0000\u0000"+
		"\u0000_\r\u0001\u0000\u0000\u0000`b\u0005\r\u0000\u0000a`\u0001\u0000"+
		"\u0000\u0000bc\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000cd\u0001"+
		"\u0000\u0000\u0000df\u0001\u0000\u0000\u0000eg\u0005\n\u0000\u0000fe\u0001"+
		"\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000gj\u0001\u0000\u0000\u0000"+
		"hj\u0005\n\u0000\u0000ia\u0001\u0000\u0000\u0000ih\u0001\u0000\u0000\u0000"+
		"j\u000f\u0001\u0000\u0000\u0000km\u0004\u0006\u0000\u0000ln\u0007\u0001"+
		"\u0000\u0000ml\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000om\u0001"+
		"\u0000\u0000\u0000op\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000\u0000"+
		"qr\u0006\u0006\u0002\u0000r\u0011\u0001\u0000\u0000\u0000st\u0003\f\u0004"+
		"\u0000tu\u0001\u0000\u0000\u0000uv\u0006\u0007\u0002\u0000v\u0013\u0001"+
		"\u0000\u0000\u0000wy\b\u0000\u0000\u0000xw\u0001\u0000\u0000\u0000yz\u0001"+
		"\u0000\u0000\u0000zx\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000"+
		"{|\u0001\u0000\u0000\u0000|}\u0006\b\u0003\u0000}\u0015\u0001\u0000\u0000"+
		"\u0000~\u007f\b\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000"+
		"\u0080\u0081\u0006\t\u0004\u0000\u0081\u0017\u0001\u0000\u0000\u0000\u0082"+
		"\u0083\u0005:\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0085"+
		"\u0006\n\u0001\u0000\u0085\u0019\u0001\u0000\u0000\u0000\u0086\u0087\u0003"+
		"\f\u0004\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u0089\u0006\u000b"+
		"\u0003\u0000\u0089\u008a\u0006\u000b\u0002\u0000\u008a\u001b\u0001\u0000"+
		"\u0000\u0000\u0011\u0000\u0001\u0002\u0003>CJPSV[^cfioz\u0005\u0002\u0002"+
		"\u0000\u0002\u0001\u0000\u0006\u0000\u0000\u0002\u0000\u0000\u0002\u0003"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}