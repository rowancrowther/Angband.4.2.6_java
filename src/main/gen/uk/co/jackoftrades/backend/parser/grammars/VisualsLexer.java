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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Visuals.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.frontend.colour.VisualsColourCycle;
    import uk.co.jackoftrades.frontend.colour.VisualsCycleGroup;
    import uk.co.jackoftrades.frontend.colour.VisualsCycler;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;

    import java.util.Map;
    import java.util.List;
    import java.util.HashMap;
    import java.util.ArrayList;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class VisualsLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, FLICKER=3, FLICKER_COLOUR=4, CYCLE=5, FANCY=6, CYCLE_COLOUR=7, 
		COLON=8, COLOUR_CHAR=9, LCASE=10;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COMMENT", "EOL", "FLICKER", "FLICKER_COLOUR", "CYCLE", "FANCY", "CYCLE_COLOUR", 
			"COLON", "COLOUR_CHAR", "LCASE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'flicker:'", "'flicker-color:'", "'cycle:'", "'fancy:'", 
			"'cycle-color:'", "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "FLICKER", "FLICKER_COLOUR", "CYCLE", "FANCY", 
			"CYCLE_COLOUR", "COLON", "COLOUR_CHAR", "LCASE"
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


	public VisualsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Visuals.g4"; }

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
		"\u0004\u0000\nf\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0005"+
		"\u0000\u0018\b\u0000\n\u0000\f\u0000\u001b\t\u0000\u0001\u0000\u0004\u0000"+
		"\u001e\b\u0000\u000b\u0000\f\u0000\u001f\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0003\u0001%\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\t\u0004\tc\b\t\u000b\t\f\td\u0000\u0000\n\u0001\u0001"+
		"\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f"+
		"\b\u0011\t\u0013\n\u0001\u0000\u0003\u0001\u0000\n\n\u0011\u0000BBDDG"+
		"GIIMMPPRRTWYZbbddggiimmoprwyz\u0005\u0000  (),-//azi\u0000\u0001\u0001"+
		"\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001"+
		"\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000"+
		"\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000"+
		"\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0001\u0015\u0001\u0000\u0000"+
		"\u0000\u0003$\u0001\u0000\u0000\u0000\u0005*\u0001\u0000\u0000\u0000\u0007"+
		"3\u0001\u0000\u0000\u0000\tB\u0001\u0000\u0000\u0000\u000bI\u0001\u0000"+
		"\u0000\u0000\rP\u0001\u0000\u0000\u0000\u000f]\u0001\u0000\u0000\u0000"+
		"\u0011_\u0001\u0000\u0000\u0000\u0013b\u0001\u0000\u0000\u0000\u0015\u0019"+
		"\u0005#\u0000\u0000\u0016\u0018\b\u0000\u0000\u0000\u0017\u0016\u0001"+
		"\u0000\u0000\u0000\u0018\u001b\u0001\u0000\u0000\u0000\u0019\u0017\u0001"+
		"\u0000\u0000\u0000\u0019\u001a\u0001\u0000\u0000\u0000\u001a\u001d\u0001"+
		"\u0000\u0000\u0000\u001b\u0019\u0001\u0000\u0000\u0000\u001c\u001e\u0005"+
		"\n\u0000\u0000\u001d\u001c\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000"+
		"\u0000\u0000\u001f\u001d\u0001\u0000\u0000\u0000\u001f \u0001\u0000\u0000"+
		"\u0000 !\u0001\u0000\u0000\u0000!\"\u0006\u0000\u0000\u0000\"\u0002\u0001"+
		"\u0000\u0000\u0000#%\u0005\r\u0000\u0000$#\u0001\u0000\u0000\u0000$%\u0001"+
		"\u0000\u0000\u0000%&\u0001\u0000\u0000\u0000&\'\u0005\n\u0000\u0000\'"+
		"(\u0001\u0000\u0000\u0000()\u0006\u0001\u0000\u0000)\u0004\u0001\u0000"+
		"\u0000\u0000*+\u0005f\u0000\u0000+,\u0005l\u0000\u0000,-\u0005i\u0000"+
		"\u0000-.\u0005c\u0000\u0000./\u0005k\u0000\u0000/0\u0005e\u0000\u0000"+
		"01\u0005r\u0000\u000012\u0005:\u0000\u00002\u0006\u0001\u0000\u0000\u0000"+
		"34\u0005f\u0000\u000045\u0005l\u0000\u000056\u0005i\u0000\u000067\u0005"+
		"c\u0000\u000078\u0005k\u0000\u000089\u0005e\u0000\u00009:\u0005r\u0000"+
		"\u0000:;\u0005-\u0000\u0000;<\u0005c\u0000\u0000<=\u0005o\u0000\u0000"+
		"=>\u0005l\u0000\u0000>?\u0005o\u0000\u0000?@\u0005r\u0000\u0000@A\u0005"+
		":\u0000\u0000A\b\u0001\u0000\u0000\u0000BC\u0005c\u0000\u0000CD\u0005"+
		"y\u0000\u0000DE\u0005c\u0000\u0000EF\u0005l\u0000\u0000FG\u0005e\u0000"+
		"\u0000GH\u0005:\u0000\u0000H\n\u0001\u0000\u0000\u0000IJ\u0005f\u0000"+
		"\u0000JK\u0005a\u0000\u0000KL\u0005n\u0000\u0000LM\u0005c\u0000\u0000"+
		"MN\u0005y\u0000\u0000NO\u0005:\u0000\u0000O\f\u0001\u0000\u0000\u0000"+
		"PQ\u0005c\u0000\u0000QR\u0005y\u0000\u0000RS\u0005c\u0000\u0000ST\u0005"+
		"l\u0000\u0000TU\u0005e\u0000\u0000UV\u0005-\u0000\u0000VW\u0005c\u0000"+
		"\u0000WX\u0005o\u0000\u0000XY\u0005l\u0000\u0000YZ\u0005o\u0000\u0000"+
		"Z[\u0005r\u0000\u0000[\\\u0005:\u0000\u0000\\\u000e\u0001\u0000\u0000"+
		"\u0000]^\u0005:\u0000\u0000^\u0010\u0001\u0000\u0000\u0000_`\u0007\u0001"+
		"\u0000\u0000`\u0012\u0001\u0000\u0000\u0000ac\u0007\u0002\u0000\u0000"+
		"ba\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000db\u0001\u0000\u0000"+
		"\u0000de\u0001\u0000\u0000\u0000e\u0014\u0001\u0000\u0000\u0000\u0005"+
		"\u0000\u0019\u001f$d\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}