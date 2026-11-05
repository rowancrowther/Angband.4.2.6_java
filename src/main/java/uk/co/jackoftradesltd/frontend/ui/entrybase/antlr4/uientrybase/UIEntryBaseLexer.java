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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/frontend/ui/entrybase/UIEntryBaseLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.frontend.ui.entrybase.antlr4.uientrybase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryBaseLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, RENDERER=2, COMBINE=3, CATEGORY=4, FLAGS=5, DESC=6, INTEGER=7, 
		LCASE=8, COMMENT=9, EOL=10, FLAG=11, OR=12, FLAG_EOL=13, STRING=14;
	public static final int
		FLAG_MODE=1, FREE_TEXT_MODE=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "FLAG_MODE", "FREE_TEXT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS", "DESC", "INTEGER", 
			"LCASE", "COMMENT", "EOL", "FLAG_BODY", "FLAG", "OR", "FLAG_EOL", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'renderer:'", "'combine:'", "'category:'", "'flags:'", 
			"'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS", "DESC", "INTEGER", 
			"LCASE", "COMMENT", "EOL", "FLAG", "OR", "FLAG_EOL", "STRING"
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


	public UIEntryBaseLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "UIEntryBaseLexer.g4"; }

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
		"\u0004\u0000\u000e\u00a3\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff"+
		"\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007"+
		"\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007"+
		"\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b"+
		"\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007"+
		"\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0003\u0006[\b\u0006\u0001\u0006\u0004\u0006^\b\u0006\u000b\u0006"+
		"\f\u0006_\u0001\u0007\u0004\u0007c\b\u0007\u000b\u0007\f\u0007d\u0001"+
		"\b\u0001\b\u0005\bi\b\b\n\b\f\bl\t\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\t\u0005\ts\b\t\n\t\f\tv\t\t\u0001\t\u0003\ty\b\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\n\u0001\n\u0005\n\u0081\b\n\n\n\f\n\u0084\t\n\u0001\u000b"+
		"\u0001\u000b\u0001\f\u0003\f\u0089\b\f\u0001\f\u0001\f\u0003\f\u008d\b"+
		"\f\u0001\r\u0005\r\u0090\b\r\n\r\f\r\u0093\t\r\u0001\r\u0003\r\u0096\b"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0004\u000e\u009e"+
		"\b\u000e\u000b\u000e\f\u000e\u009f\u0001\u000e\u0001\u000e\u0000\u0000"+
		"\u000f\u0003\u0001\u0005\u0002\u0007\u0003\t\u0004\u000b\u0005\r\u0006"+
		"\u000f\u0007\u0011\b\u0013\t\u0015\n\u0017\u0000\u0019\u000b\u001b\f\u001d"+
		"\r\u001f\u000e\u0003\u0000\u0001\u0002\u0004\u0003\u000009__az\u0001\u0000"+
		"\n\n\u0003\u000009AZ__\u0002\u0000\n\n\r\r\u00ab\u0000\u0003\u0001\u0000"+
		"\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000"+
		"\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000"+
		"\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000"+
		"\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000"+
		"\u0000\u0015\u0001\u0000\u0000\u0000\u0001\u0019\u0001\u0000\u0000\u0000"+
		"\u0001\u001b\u0001\u0000\u0000\u0000\u0001\u001d\u0001\u0000\u0000\u0000"+
		"\u0002\u001f\u0001\u0000\u0000\u0000\u0003!\u0001\u0000\u0000\u0000\u0005"+
		"\'\u0001\u0000\u0000\u0000\u00071\u0001\u0000\u0000\u0000\t<\u0001\u0000"+
		"\u0000\u0000\u000bH\u0001\u0000\u0000\u0000\rQ\u0001\u0000\u0000\u0000"+
		"\u000fZ\u0001\u0000\u0000\u0000\u0011b\u0001\u0000\u0000\u0000\u0013f"+
		"\u0001\u0000\u0000\u0000\u0015t\u0001\u0000\u0000\u0000\u0017~\u0001\u0000"+
		"\u0000\u0000\u0019\u0085\u0001\u0000\u0000\u0000\u001b\u0088\u0001\u0000"+
		"\u0000\u0000\u001d\u0091\u0001\u0000\u0000\u0000\u001f\u009d\u0001\u0000"+
		"\u0000\u0000!\"\u0005n\u0000\u0000\"#\u0005a\u0000\u0000#$\u0005m\u0000"+
		"\u0000$%\u0005e\u0000\u0000%&\u0005:\u0000\u0000&\u0004\u0001\u0000\u0000"+
		"\u0000\'(\u0005r\u0000\u0000()\u0005e\u0000\u0000)*\u0005n\u0000\u0000"+
		"*+\u0005d\u0000\u0000+,\u0005e\u0000\u0000,-\u0005r\u0000\u0000-.\u0005"+
		"e\u0000\u0000./\u0005r\u0000\u0000/0\u0005:\u0000\u00000\u0006\u0001\u0000"+
		"\u0000\u000012\u0005c\u0000\u000023\u0005o\u0000\u000034\u0005m\u0000"+
		"\u000045\u0005b\u0000\u000056\u0005i\u0000\u000067\u0005n\u0000\u0000"+
		"78\u0005e\u0000\u000089\u0005:\u0000\u00009:\u0001\u0000\u0000\u0000:"+
		";\u0006\u0002\u0000\u0000;\b\u0001\u0000\u0000\u0000<=\u0005c\u0000\u0000"+
		"=>\u0005a\u0000\u0000>?\u0005t\u0000\u0000?@\u0005e\u0000\u0000@A\u0005"+
		"g\u0000\u0000AB\u0005o\u0000\u0000BC\u0005r\u0000\u0000CD\u0005y\u0000"+
		"\u0000DE\u0005:\u0000\u0000EF\u0001\u0000\u0000\u0000FG\u0006\u0003\u0001"+
		"\u0000G\n\u0001\u0000\u0000\u0000HI\u0005f\u0000\u0000IJ\u0005l\u0000"+
		"\u0000JK\u0005a\u0000\u0000KL\u0005g\u0000\u0000LM\u0005s\u0000\u0000"+
		"MN\u0005:\u0000\u0000NO\u0001\u0000\u0000\u0000OP\u0006\u0004\u0000\u0000"+
		"P\f\u0001\u0000\u0000\u0000QR\u0005d\u0000\u0000RS\u0005e\u0000\u0000"+
		"ST\u0005s\u0000\u0000TU\u0005c\u0000\u0000UV\u0005:\u0000\u0000VW\u0001"+
		"\u0000\u0000\u0000WX\u0006\u0005\u0001\u0000X\u000e\u0001\u0000\u0000"+
		"\u0000Y[\u0005-\u0000\u0000ZY\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000"+
		"\u0000[]\u0001\u0000\u0000\u0000\\^\u000209\u0000]\\\u0001\u0000\u0000"+
		"\u0000^_\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000_`\u0001\u0000"+
		"\u0000\u0000`\u0010\u0001\u0000\u0000\u0000ac\u0007\u0000\u0000\u0000"+
		"ba\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000db\u0001\u0000\u0000"+
		"\u0000de\u0001\u0000\u0000\u0000e\u0012\u0001\u0000\u0000\u0000fj\u0005"+
		"#\u0000\u0000gi\b\u0001\u0000\u0000hg\u0001\u0000\u0000\u0000il\u0001"+
		"\u0000\u0000\u0000jh\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000"+
		"km\u0001\u0000\u0000\u0000lj\u0001\u0000\u0000\u0000mn\u0005\n\u0000\u0000"+
		"no\u0001\u0000\u0000\u0000op\u0006\b\u0002\u0000p\u0014\u0001\u0000\u0000"+
		"\u0000qs\u0005 \u0000\u0000rq\u0001\u0000\u0000\u0000sv\u0001\u0000\u0000"+
		"\u0000tr\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000ux\u0001\u0000"+
		"\u0000\u0000vt\u0001\u0000\u0000\u0000wy\u0005\r\u0000\u0000xw\u0001\u0000"+
		"\u0000\u0000xy\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000z{\u0005"+
		"\n\u0000\u0000{|\u0001\u0000\u0000\u0000|}\u0006\t\u0002\u0000}\u0016"+
		"\u0001\u0000\u0000\u0000~\u0082\u0002AZ\u0000\u007f\u0081\u0007\u0002"+
		"\u0000\u0000\u0080\u007f\u0001\u0000\u0000\u0000\u0081\u0084\u0001\u0000"+
		"\u0000\u0000\u0082\u0080\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000"+
		"\u0000\u0000\u0083\u0018\u0001\u0000\u0000\u0000\u0084\u0082\u0001\u0000"+
		"\u0000\u0000\u0085\u0086\u0003\u0017\n\u0000\u0086\u001a\u0001\u0000\u0000"+
		"\u0000\u0087\u0089\u0005 \u0000\u0000\u0088\u0087\u0001\u0000\u0000\u0000"+
		"\u0088\u0089\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000"+
		"\u008a\u008c\u0005|\u0000\u0000\u008b\u008d\u0005 \u0000\u0000\u008c\u008b"+
		"\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u001c"+
		"\u0001\u0000\u0000\u0000\u008e\u0090\u0005 \u0000\u0000\u008f\u008e\u0001"+
		"\u0000\u0000\u0000\u0090\u0093\u0001\u0000\u0000\u0000\u0091\u008f\u0001"+
		"\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0095\u0001"+
		"\u0000\u0000\u0000\u0093\u0091\u0001\u0000\u0000\u0000\u0094\u0096\u0005"+
		"\r\u0000\u0000\u0095\u0094\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0098\u0005\n\u0000"+
		"\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u009a\u0006\r\u0002\u0000"+
		"\u009a\u009b\u0006\r\u0003\u0000\u009b\u001e\u0001\u0000\u0000\u0000\u009c"+
		"\u009e\b\u0003\u0000\u0000\u009d\u009c\u0001\u0000\u0000\u0000\u009e\u009f"+
		"\u0001\u0000\u0000\u0000\u009f\u009d\u0001\u0000\u0000\u0000\u009f\u00a0"+
		"\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1\u00a2"+
		"\u0006\u000e\u0003\u0000\u00a2 \u0001\u0000\u0000\u0000\u000f\u0000\u0001"+
		"\u0002Z_djtx\u0082\u0088\u008c\u0091\u0095\u009f\u0004\u0005\u0001\u0000"+
		"\u0005\u0002\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}