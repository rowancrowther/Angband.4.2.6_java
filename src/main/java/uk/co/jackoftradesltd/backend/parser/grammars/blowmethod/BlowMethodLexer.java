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
// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/BlowMethodLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.blowmethod;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowMethodLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, CUT=2, STUN=3, MISS=4, PHYS=5, MSG=6, ACT=7, DESC=8, INTEGER=9, 
		COMMENT=10, EOL=11, STRING=12, FREE_TEXT_EOL=13;
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
			"NAME", "CUT", "STUN", "MISS", "PHYS", "MSG", "ACT", "DESC", "INTEGER", 
			"COMMENT", "EOL", "STRING", "FREE_TEXT_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'cut:'", "'stun:'", "'miss:'", "'phys:'", "'msg:'", 
			"'act:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "CUT", "STUN", "MISS", "PHYS", "MSG", "ACT", "DESC", "INTEGER", 
			"COMMENT", "EOL", "STRING", "FREE_TEXT_EOL"
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


	public BlowMethodLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BlowMethodLexer.g4"; }

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
		"\u0004\u0000\r\u0081\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n"+
		"\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0003\bS\b\b\u0001\b\u0004"+
		"\bV\b\b\u000b\b\f\bW\u0001\t\u0001\t\u0005\t\\\b\t\n\t\f\t_\t\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\n\u0005\nf\b\n\n\n\f\ni\t\n\u0001\n\u0003"+
		"\nl\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0004\u000bs\b\u000b"+
		"\u000b\u000b\f\u000bt\u0001\f\u0005\fx\b\f\n\f\f\f{\t\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0000\u0000\r\u0002\u0001\u0004\u0002\u0006"+
		"\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007\u0010\b\u0012\t\u0014\n\u0016"+
		"\u000b\u0018\f\u001a\r\u0002\u0000\u0001\u0002\u0001\u0000\n\n\u0002\u0000"+
		"\n\n\r\r\u0086\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000"+
		"\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000"+
		"\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000"+
		"\u0000\u000e\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000\u0000"+
		"\u0000\u0012\u0001\u0000\u0000\u0000\u0000\u0014\u0001\u0000\u0000\u0000"+
		"\u0000\u0016\u0001\u0000\u0000\u0000\u0001\u0018\u0001\u0000\u0000\u0000"+
		"\u0001\u001a\u0001\u0000\u0000\u0000\u0002\u001c\u0001\u0000\u0000\u0000"+
		"\u0004$\u0001\u0000\u0000\u0000\u0006)\u0001\u0000\u0000\u0000\b/\u0001"+
		"\u0000\u0000\u0000\n5\u0001\u0000\u0000\u0000\f;\u0001\u0000\u0000\u0000"+
		"\u000eB\u0001\u0000\u0000\u0000\u0010I\u0001\u0000\u0000\u0000\u0012R"+
		"\u0001\u0000\u0000\u0000\u0014Y\u0001\u0000\u0000\u0000\u0016g\u0001\u0000"+
		"\u0000\u0000\u0018r\u0001\u0000\u0000\u0000\u001ay\u0001\u0000\u0000\u0000"+
		"\u001c\u001d\u0005n\u0000\u0000\u001d\u001e\u0005a\u0000\u0000\u001e\u001f"+
		"\u0005m\u0000\u0000\u001f \u0005e\u0000\u0000 !\u0005:\u0000\u0000!\""+
		"\u0001\u0000\u0000\u0000\"#\u0006\u0000\u0000\u0000#\u0003\u0001\u0000"+
		"\u0000\u0000$%\u0005c\u0000\u0000%&\u0005u\u0000\u0000&\'\u0005t\u0000"+
		"\u0000\'(\u0005:\u0000\u0000(\u0005\u0001\u0000\u0000\u0000)*\u0005s\u0000"+
		"\u0000*+\u0005t\u0000\u0000+,\u0005u\u0000\u0000,-\u0005n\u0000\u0000"+
		"-.\u0005:\u0000\u0000.\u0007\u0001\u0000\u0000\u0000/0\u0005m\u0000\u0000"+
		"01\u0005i\u0000\u000012\u0005s\u0000\u000023\u0005s\u0000\u000034\u0005"+
		":\u0000\u00004\t\u0001\u0000\u0000\u000056\u0005p\u0000\u000067\u0005"+
		"h\u0000\u000078\u0005y\u0000\u000089\u0005s\u0000\u00009:\u0005:\u0000"+
		"\u0000:\u000b\u0001\u0000\u0000\u0000;<\u0005m\u0000\u0000<=\u0005s\u0000"+
		"\u0000=>\u0005g\u0000\u0000>?\u0005:\u0000\u0000?@\u0001\u0000\u0000\u0000"+
		"@A\u0006\u0005\u0000\u0000A\r\u0001\u0000\u0000\u0000BC\u0005a\u0000\u0000"+
		"CD\u0005c\u0000\u0000DE\u0005t\u0000\u0000EF\u0005:\u0000\u0000FG\u0001"+
		"\u0000\u0000\u0000GH\u0006\u0006\u0000\u0000H\u000f\u0001\u0000\u0000"+
		"\u0000IJ\u0005d\u0000\u0000JK\u0005e\u0000\u0000KL\u0005s\u0000\u0000"+
		"LM\u0005c\u0000\u0000MN\u0005:\u0000\u0000NO\u0001\u0000\u0000\u0000O"+
		"P\u0006\u0007\u0000\u0000P\u0011\u0001\u0000\u0000\u0000QS\u0005-\u0000"+
		"\u0000RQ\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SU\u0001\u0000"+
		"\u0000\u0000TV\u000209\u0000UT\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000"+
		"\u0000WU\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000X\u0013\u0001"+
		"\u0000\u0000\u0000Y]\u0005#\u0000\u0000Z\\\b\u0000\u0000\u0000[Z\u0001"+
		"\u0000\u0000\u0000\\_\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000"+
		"]^\u0001\u0000\u0000\u0000^`\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000"+
		"\u0000`a\u0005\n\u0000\u0000ab\u0001\u0000\u0000\u0000bc\u0006\t\u0001"+
		"\u0000c\u0015\u0001\u0000\u0000\u0000df\u0005 \u0000\u0000ed\u0001\u0000"+
		"\u0000\u0000fi\u0001\u0000\u0000\u0000ge\u0001\u0000\u0000\u0000gh\u0001"+
		"\u0000\u0000\u0000hk\u0001\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000"+
		"jl\u0005\r\u0000\u0000kj\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000"+
		"lm\u0001\u0000\u0000\u0000mn\u0005\n\u0000\u0000no\u0001\u0000\u0000\u0000"+
		"op\u0006\n\u0001\u0000p\u0017\u0001\u0000\u0000\u0000qs\b\u0001\u0000"+
		"\u0000rq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tr\u0001\u0000"+
		"\u0000\u0000tu\u0001\u0000\u0000\u0000u\u0019\u0001\u0000\u0000\u0000"+
		"vx\u0005\r\u0000\u0000wv\u0001\u0000\u0000\u0000x{\u0001\u0000\u0000\u0000"+
		"yw\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000z|\u0001\u0000\u0000"+
		"\u0000{y\u0001\u0000\u0000\u0000|}\u0005\n\u0000\u0000}~\u0001\u0000\u0000"+
		"\u0000~\u007f\u0006\f\u0001\u0000\u007f\u0080\u0006\f\u0002\u0000\u0080"+
		"\u001b\u0001\u0000\u0000\u0000\t\u0000\u0001RW]gkty\u0003\u0005\u0001"+
		"\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}