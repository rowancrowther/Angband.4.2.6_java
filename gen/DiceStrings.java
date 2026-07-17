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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/op_probe/DiceStrings.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DiceStrings extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SIMPLE_DICE_STRING=1, COMPLEX_DICE_STRING=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"DICE_D", "DICE_M", "DICE_INTEGER", "DICE_DOLLAR_LETTER", "DICE_SIMPLE_NUMBER", 
			"DICE_ANY_NUMBER", "COMPLEX_DICE_STRING_BODY", "SIMPLE_DICE_STRING_BODY", 
			"SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING"
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


	public DiceStrings(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DiceStrings.g4"; }

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
		"\u0004\u0000\u0002\u0082\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0004\u0002\u001b\b\u0002\u000b\u0002"+
		"\f\u0002\u001c\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0003\u0005&\b\u0005\u0001\u0006\u0003\u0006"+
		")\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006.\b\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u00065\b"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006:\b\u0006\u0001"+
		"\u0006\u0003\u0006=\b\u0006\u0001\u0006\u0003\u0006@\b\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006G\b\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006M\b\u0006"+
		"\u0001\u0006\u0003\u0006P\b\u0006\u0001\u0007\u0003\u0007S\b\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0003\u0007X\b\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007_\b\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0003\u0007d\b\u0007\u0001\u0007\u0003"+
		"\u0007g\b\u0007\u0001\u0007\u0003\u0007j\b\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007q\b\u0007\u0001\u0007"+
		"\u0003\u0007t\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0003\u0007z\b\u0007\u0001\u0007\u0003\u0007}\b\u0007\u0001\b\u0001\b"+
		"\u0001\t\u0001\t\u0000\u0000\n\u0001\u0000\u0003\u0000\u0005\u0000\u0007"+
		"\u0000\t\u0000\u000b\u0000\r\u0000\u000f\u0000\u0011\u0001\u0013\u0002"+
		"\u0001\u0000\u0001\u0002\u0000MMmm\u0092\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0001\u0015\u0001\u0000\u0000"+
		"\u0000\u0003\u0017\u0001\u0000\u0000\u0000\u0005\u001a\u0001\u0000\u0000"+
		"\u0000\u0007\u001e\u0001\u0000\u0000\u0000\t!\u0001\u0000\u0000\u0000"+
		"\u000b%\u0001\u0000\u0000\u0000\rO\u0001\u0000\u0000\u0000\u000f|\u0001"+
		"\u0000\u0000\u0000\u0011~\u0001\u0000\u0000\u0000\u0013\u0080\u0001\u0000"+
		"\u0000\u0000\u0015\u0016\u0005d\u0000\u0000\u0016\u0002\u0001\u0000\u0000"+
		"\u0000\u0017\u0018\u0007\u0000\u0000\u0000\u0018\u0004\u0001\u0000\u0000"+
		"\u0000\u0019\u001b\u000209\u0000\u001a\u0019\u0001\u0000\u0000\u0000\u001b"+
		"\u001c\u0001\u0000\u0000\u0000\u001c\u001a\u0001\u0000\u0000\u0000\u001c"+
		"\u001d\u0001\u0000\u0000\u0000\u001d\u0006\u0001\u0000\u0000\u0000\u001e"+
		"\u001f\u0005$\u0000\u0000\u001f \u0002AZ\u0000 \b\u0001\u0000\u0000\u0000"+
		"!\"\u0003\u0005\u0002\u0000\"\n\u0001\u0000\u0000\u0000#&\u0003\u0005"+
		"\u0002\u0000$&\u0003\u0007\u0003\u0000%#\u0001\u0000\u0000\u0000%$\u0001"+
		"\u0000\u0000\u0000&\f\u0001\u0000\u0000\u0000\')\u0005-\u0000\u0000(\'"+
		"\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000"+
		"\u0000*+\u0003\u000b\u0005\u0000+9\u0005+\u0000\u0000,.\u0003\u000b\u0005"+
		"\u0000-,\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000./\u0001\u0000"+
		"\u0000\u0000/0\u0003\u0001\u0000\u000004\u0003\u000b\u0005\u000012\u0003"+
		"\u0003\u0001\u000023\u0003\u000b\u0005\u000035\u0001\u0000\u0000\u0000"+
		"41\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u00005:\u0001\u0000\u0000"+
		"\u000067\u0003\u0003\u0001\u000078\u0003\u000b\u0005\u00008:\u0001\u0000"+
		"\u0000\u00009-\u0001\u0000\u0000\u000096\u0001\u0000\u0000\u0000:P\u0001"+
		"\u0000\u0000\u0000;=\u0005-\u0000\u0000<;\u0001\u0000\u0000\u0000<=\u0001"+
		"\u0000\u0000\u0000=?\u0001\u0000\u0000\u0000>@\u0003\u000b\u0005\u0000"+
		"?>\u0001\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000"+
		"\u0000AB\u0003\u0001\u0000\u0000BF\u0003\u000b\u0005\u0000CD\u0003\u0003"+
		"\u0001\u0000DE\u0003\u000b\u0005\u0000EG\u0001\u0000\u0000\u0000FC\u0001"+
		"\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GP\u0001\u0000\u0000\u0000"+
		"HI\u0003\u0003\u0001\u0000IJ\u0003\u000b\u0005\u0000JP\u0001\u0000\u0000"+
		"\u0000KM\u0005-\u0000\u0000LK\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000"+
		"\u0000MN\u0001\u0000\u0000\u0000NP\u0003\u000b\u0005\u0000O(\u0001\u0000"+
		"\u0000\u0000O<\u0001\u0000\u0000\u0000OH\u0001\u0000\u0000\u0000OL\u0001"+
		"\u0000\u0000\u0000P\u000e\u0001\u0000\u0000\u0000QS\u0005-\u0000\u0000"+
		"RQ\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000"+
		"\u0000TU\u0003\t\u0004\u0000Uc\u0005+\u0000\u0000VX\u0003\t\u0004\u0000"+
		"WV\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000"+
		"\u0000YZ\u0003\u0001\u0000\u0000Z^\u0003\t\u0004\u0000[\\\u0003\u0003"+
		"\u0001\u0000\\]\u0003\t\u0004\u0000]_\u0001\u0000\u0000\u0000^[\u0001"+
		"\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_d\u0001\u0000\u0000\u0000"+
		"`a\u0003\u0003\u0001\u0000ab\u0003\t\u0004\u0000bd\u0001\u0000\u0000\u0000"+
		"cW\u0001\u0000\u0000\u0000c`\u0001\u0000\u0000\u0000d}\u0001\u0000\u0000"+
		"\u0000eg\u0005-\u0000\u0000fe\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000"+
		"\u0000gi\u0001\u0000\u0000\u0000hj\u0003\t\u0004\u0000ih\u0001\u0000\u0000"+
		"\u0000ij\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000kl\u0003\u0001"+
		"\u0000\u0000lp\u0003\t\u0004\u0000mn\u0003\u0003\u0001\u0000no\u0003\t"+
		"\u0004\u0000oq\u0001\u0000\u0000\u0000pm\u0001\u0000\u0000\u0000pq\u0001"+
		"\u0000\u0000\u0000q}\u0001\u0000\u0000\u0000rt\u0005-\u0000\u0000sr\u0001"+
		"\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000"+
		"uv\u0003\u0003\u0001\u0000vw\u0003\t\u0004\u0000w}\u0001\u0000\u0000\u0000"+
		"xz\u0005-\u0000\u0000yx\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000"+
		"z{\u0001\u0000\u0000\u0000{}\u0003\t\u0004\u0000|R\u0001\u0000\u0000\u0000"+
		"|f\u0001\u0000\u0000\u0000|s\u0001\u0000\u0000\u0000|y\u0001\u0000\u0000"+
		"\u0000}\u0010\u0001\u0000\u0000\u0000~\u007f\u0003\u000f\u0007\u0000\u007f"+
		"\u0012\u0001\u0000\u0000\u0000\u0080\u0081\u0003\r\u0006\u0000\u0081\u0014"+
		"\u0001\u0000\u0000\u0000\u0016\u0000\u001c%(-49<?FLORW^cfipsy|\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}