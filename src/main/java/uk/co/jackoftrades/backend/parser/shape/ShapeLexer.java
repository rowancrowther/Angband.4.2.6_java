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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Shape.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.shape;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ShapeLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
	public static final int
            COMMENT = 1, EOL = 2, NAME = 3, COMBAT = 4, SKILL_DISARM_PHYS = 5, SKILL_DISARM_MAGIC = 6,
            SKILL_SAVE = 7, SKILL_STEALTH = 8, SKILL_SEARCH = 9, SKILL_MELEE = 10, SKILL_THROW = 11,
            SKILL_DIG = 12, OBJ_FLAGS = 13, PLAYER_FLAGS = 14, VALUES = 15, EFFECT = 16, DICE = 17,
            EXPR = 18, EFFECT_MSG = 19, BLOW = 20, LBRACKET = 21, RBRACKET = 22, COLON = 23, OR = 24,
            FLAG = 25, STRING = 26;
	public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
            "DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "NAME", "COMBAT", "SKILL_DISARM_PHYS", "SKILL_DISARM_MAGIC",
                "SKILL_SAVE", "SKILL_STEALTH", "SKILL_SEARCH", "SKILL_MELEE", "SKILL_THROW",
                "SKILL_DIG", "OBJ_FLAGS", "PLAYER_FLAGS", "VALUES", "EFFECT", "DICE",
                "EXPR", "EFFECT_MSG", "BLOW", "LBRACKET", "RBRACKET", "COLON", "OR",
                "FLAG", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'combat:'", "'skill-disarm-phys:'", "'skill-disarm-magic:'",
                "'skill-save:'", "'skill-stealth:'", "'skill-search:'", "'skill-melee:'",
                "'skill-throw:'", "'skill-dig:'", "'obj-flags:'", "'player-flags:'",
                "'values:'", "'effect:'", "'dice:'", "'expr:'", "'effect-msg:'", "'blow:'",
                "'['", "']'", "':'", "' | '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "COMBAT", "SKILL_DISARM_PHYS", "SKILL_DISARM_MAGIC",
                "SKILL_SAVE", "SKILL_STEALTH", "SKILL_SEARCH", "SKILL_MELEE", "SKILL_THROW",
                "SKILL_DIG", "OBJ_FLAGS", "PLAYER_FLAGS", "VALUES", "EFFECT", "DICE",
                "EXPR", "EFFECT_MSG", "BLOW", "LBRACKET", "RBRACKET", "COLON", "OR",
                "FLAG", "STRING"
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


	public ShapeLexer(CharStream input) {
		super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@Override
    public String getGrammarFileName() {
        return "Shape.g4";
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
            "\u0004\u0000\u001a\u012e\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
                    "\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
                    "\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
                    "\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
                    "\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
                    "\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011" +
                    "\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014" +
                    "\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017" +
                    "\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0001\u0000\u0001\u0000" +
                    "\u0005\u00008\b\u0000\n\u0000\f\u0000;\t\u0000\u0001\u0000\u0004\u0000" +
                    ">\b\u0000\u000b\u0000\f\u0000?\u0001\u0000\u0001\u0000\u0001\u0001\u0005" +
                    "\u0001E\b\u0001\n\u0001\f\u0001H\t\u0001\u0001\u0001\u0003\u0001K\b\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014" +
                    "\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0017" +
                    "\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0004\u0018\u0126\b\u0018" +
                    "\u000b\u0018\f\u0018\u0127\u0001\u0019\u0004\u0019\u012b\b\u0019\u000b" +
                    "\u0019\f\u0019\u012c\u0000\u0000\u001a\u0001\u0001\u0003\u0002\u0005\u0003" +
                    "\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015" +
                    "\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012" +
                    "%\u0013\'\u0014)\u0015+\u0016-\u0017/\u00181\u00193\u001a\u0001\u0000" +
                    "\u0003\u0001\u0000\n\n\u0002\u0000AZ__\t\u0000  $$++--/9BBPPSSaz\u0133" +
                    "\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000" +
                    "\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000" +
                    "\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000" +
                    "\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011" +
                    "\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015" +
                    "\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019" +
                    "\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d" +
                    "\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001" +
                    "\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000" +
                    "\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000" +
                    "\u0000+\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/" +
                    "\u0001\u0000\u0000\u0000\u00001\u0001\u0000\u0000\u0000\u00003\u0001\u0000" +
                    "\u0000\u0000\u00015\u0001\u0000\u0000\u0000\u0003F\u0001\u0000\u0000\u0000" +
                    "\u0005P\u0001\u0000\u0000\u0000\u0007V\u0001\u0000\u0000\u0000\t^\u0001" +
                    "\u0000\u0000\u0000\u000bq\u0001\u0000\u0000\u0000\r\u0085\u0001\u0000" +
                    "\u0000\u0000\u000f\u0091\u0001\u0000\u0000\u0000\u0011\u00a0\u0001\u0000" +
                    "\u0000\u0000\u0013\u00ae\u0001\u0000\u0000\u0000\u0015\u00bb\u0001\u0000" +
                    "\u0000\u0000\u0017\u00c8\u0001\u0000\u0000\u0000\u0019\u00d3\u0001\u0000" +
                    "\u0000\u0000\u001b\u00de\u0001\u0000\u0000\u0000\u001d\u00ec\u0001\u0000" +
                    "\u0000\u0000\u001f\u00f4\u0001\u0000\u0000\u0000!\u00fc\u0001\u0000\u0000" +
                    "\u0000#\u0102\u0001\u0000\u0000\u0000%\u0108\u0001\u0000\u0000\u0000\'" +
                    "\u0114\u0001\u0000\u0000\u0000)\u011a\u0001\u0000\u0000\u0000+\u011c\u0001" +
                    "\u0000\u0000\u0000-\u011e\u0001\u0000\u0000\u0000/\u0120\u0001\u0000\u0000" +
                    "\u00001\u0125\u0001\u0000\u0000\u00003\u012a\u0001\u0000\u0000\u00005" +
                    "9\u0005#\u0000\u000068\b\u0000\u0000\u000076\u0001\u0000\u0000\u00008" +
                    ";\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u00009:\u0001\u0000\u0000" +
                    "\u0000:=\u0001\u0000\u0000\u0000;9\u0001\u0000\u0000\u0000<>\u0005\n\u0000" +
                    "\u0000=<\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?=\u0001\u0000" +
                    "\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000AB\u0006" +
                    "\u0000\u0000\u0000B\u0002\u0001\u0000\u0000\u0000CE\u0005 \u0000\u0000" +
                    "DC\u0001\u0000\u0000\u0000EH\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000" +
                    "\u0000FG\u0001\u0000\u0000\u0000GJ\u0001\u0000\u0000\u0000HF\u0001\u0000" +
                    "\u0000\u0000IK\u0005\r\u0000\u0000JI\u0001\u0000\u0000\u0000JK\u0001\u0000" +
                    "\u0000\u0000KL\u0001\u0000\u0000\u0000LM\u0005\n\u0000\u0000MN\u0001\u0000" +
                    "\u0000\u0000NO\u0006\u0001\u0000\u0000O\u0004\u0001\u0000\u0000\u0000" +
                    "PQ\u0005n\u0000\u0000QR\u0005a\u0000\u0000RS\u0005m\u0000\u0000ST\u0005" +
                    "e\u0000\u0000TU\u0005:\u0000\u0000U\u0006\u0001\u0000\u0000\u0000VW\u0005" +
                    "c\u0000\u0000WX\u0005o\u0000\u0000XY\u0005m\u0000\u0000YZ\u0005b\u0000" +
                    "\u0000Z[\u0005a\u0000\u0000[\\\u0005t\u0000\u0000\\]\u0005:\u0000\u0000" +
                    "]\b\u0001\u0000\u0000\u0000^_\u0005s\u0000\u0000_`\u0005k\u0000\u0000" +
                    "`a\u0005i\u0000\u0000ab\u0005l\u0000\u0000bc\u0005l\u0000\u0000cd\u0005" +
                    "-\u0000\u0000de\u0005d\u0000\u0000ef\u0005i\u0000\u0000fg\u0005s\u0000" +
                    "\u0000gh\u0005a\u0000\u0000hi\u0005r\u0000\u0000ij\u0005m\u0000\u0000" +
                    "jk\u0005-\u0000\u0000kl\u0005p\u0000\u0000lm\u0005h\u0000\u0000mn\u0005" +
                    "y\u0000\u0000no\u0005s\u0000\u0000op\u0005:\u0000\u0000p\n\u0001\u0000" +
                    "\u0000\u0000qr\u0005s\u0000\u0000rs\u0005k\u0000\u0000st\u0005i\u0000" +
                    "\u0000tu\u0005l\u0000\u0000uv\u0005l\u0000\u0000vw\u0005-\u0000\u0000" +
                    "wx\u0005d\u0000\u0000xy\u0005i\u0000\u0000yz\u0005s\u0000\u0000z{\u0005" +
                    "a\u0000\u0000{|\u0005r\u0000\u0000|}\u0005m\u0000\u0000}~\u0005-\u0000" +
                    "\u0000~\u007f\u0005m\u0000\u0000\u007f\u0080\u0005a\u0000\u0000\u0080" +
                    "\u0081\u0005g\u0000\u0000\u0081\u0082\u0005i\u0000\u0000\u0082\u0083\u0005" +
                    "c\u0000\u0000\u0083\u0084\u0005:\u0000\u0000\u0084\f\u0001\u0000\u0000" +
                    "\u0000\u0085\u0086\u0005s\u0000\u0000\u0086\u0087\u0005k\u0000\u0000\u0087" +
                    "\u0088\u0005i\u0000\u0000\u0088\u0089\u0005l\u0000\u0000\u0089\u008a\u0005" +
                    "l\u0000\u0000\u008a\u008b\u0005-\u0000\u0000\u008b\u008c\u0005s\u0000" +
                    "\u0000\u008c\u008d\u0005a\u0000\u0000\u008d\u008e\u0005v\u0000\u0000\u008e" +
                    "\u008f\u0005e\u0000\u0000\u008f\u0090\u0005:\u0000\u0000\u0090\u000e\u0001" +
                    "\u0000\u0000\u0000\u0091\u0092\u0005s\u0000\u0000\u0092\u0093\u0005k\u0000" +
                    "\u0000\u0093\u0094\u0005i\u0000\u0000\u0094\u0095\u0005l\u0000\u0000\u0095" +
                    "\u0096\u0005l\u0000\u0000\u0096\u0097\u0005-\u0000\u0000\u0097\u0098\u0005" +
                    "s\u0000\u0000\u0098\u0099\u0005t\u0000\u0000\u0099\u009a\u0005e\u0000" +
                    "\u0000\u009a\u009b\u0005a\u0000\u0000\u009b\u009c\u0005l\u0000\u0000\u009c" +
                    "\u009d\u0005t\u0000\u0000\u009d\u009e\u0005h\u0000\u0000\u009e\u009f\u0005" +
                    ":\u0000\u0000\u009f\u0010\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005s\u0000" +
                    "\u0000\u00a1\u00a2\u0005k\u0000\u0000\u00a2\u00a3\u0005i\u0000\u0000\u00a3" +
                    "\u00a4\u0005l\u0000\u0000\u00a4\u00a5\u0005l\u0000\u0000\u00a5\u00a6\u0005" +
                    "-\u0000\u0000\u00a6\u00a7\u0005s\u0000\u0000\u00a7\u00a8\u0005e\u0000" +
                    "\u0000\u00a8\u00a9\u0005a\u0000\u0000\u00a9\u00aa\u0005r\u0000\u0000\u00aa" +
                    "\u00ab\u0005c\u0000\u0000\u00ab\u00ac\u0005h\u0000\u0000\u00ac\u00ad\u0005" +
                    ":\u0000\u0000\u00ad\u0012\u0001\u0000\u0000\u0000\u00ae\u00af\u0005s\u0000" +
                    "\u0000\u00af\u00b0\u0005k\u0000\u0000\u00b0\u00b1\u0005i\u0000\u0000\u00b1" +
                    "\u00b2\u0005l\u0000\u0000\u00b2\u00b3\u0005l\u0000\u0000\u00b3\u00b4\u0005" +
                    "-\u0000\u0000\u00b4\u00b5\u0005m\u0000\u0000\u00b5\u00b6\u0005e\u0000" +
                    "\u0000\u00b6\u00b7\u0005l\u0000\u0000\u00b7\u00b8\u0005e\u0000\u0000\u00b8" +
                    "\u00b9\u0005e\u0000\u0000\u00b9\u00ba\u0005:\u0000\u0000\u00ba\u0014\u0001" +
                    "\u0000\u0000\u0000\u00bb\u00bc\u0005s\u0000\u0000\u00bc\u00bd\u0005k\u0000" +
                    "\u0000\u00bd\u00be\u0005i\u0000\u0000\u00be\u00bf\u0005l\u0000\u0000\u00bf" +
                    "\u00c0\u0005l\u0000\u0000\u00c0\u00c1\u0005-\u0000\u0000\u00c1\u00c2\u0005" +
                    "t\u0000\u0000\u00c2\u00c3\u0005h\u0000\u0000\u00c3\u00c4\u0005r\u0000" +
                    "\u0000\u00c4\u00c5\u0005o\u0000\u0000\u00c5\u00c6\u0005w\u0000\u0000\u00c6" +
                    "\u00c7\u0005:\u0000\u0000\u00c7\u0016\u0001\u0000\u0000\u0000\u00c8\u00c9" +
                    "\u0005s\u0000\u0000\u00c9\u00ca\u0005k\u0000\u0000\u00ca\u00cb\u0005i" +
                    "\u0000\u0000\u00cb\u00cc\u0005l\u0000\u0000\u00cc\u00cd\u0005l\u0000\u0000" +
                    "\u00cd\u00ce\u0005-\u0000\u0000\u00ce\u00cf\u0005d\u0000\u0000\u00cf\u00d0" +
                    "\u0005i\u0000\u0000\u00d0\u00d1\u0005g\u0000\u0000\u00d1\u00d2\u0005:" +
                    "\u0000\u0000\u00d2\u0018\u0001\u0000\u0000\u0000\u00d3\u00d4\u0005o\u0000" +
                    "\u0000\u00d4\u00d5\u0005b\u0000\u0000\u00d5\u00d6\u0005j\u0000\u0000\u00d6" +
                    "\u00d7\u0005-\u0000\u0000\u00d7\u00d8\u0005f\u0000\u0000\u00d8\u00d9\u0005" +
                    "l\u0000\u0000\u00d9\u00da\u0005a\u0000\u0000\u00da\u00db\u0005g\u0000" +
                    "\u0000\u00db\u00dc\u0005s\u0000\u0000\u00dc\u00dd\u0005:\u0000\u0000\u00dd" +
                    "\u001a\u0001\u0000\u0000\u0000\u00de\u00df\u0005p\u0000\u0000\u00df\u00e0" +
                    "\u0005l\u0000\u0000\u00e0\u00e1\u0005a\u0000\u0000\u00e1\u00e2\u0005y" +
                    "\u0000\u0000\u00e2\u00e3\u0005e\u0000\u0000\u00e3\u00e4\u0005r\u0000\u0000" +
                    "\u00e4\u00e5\u0005-\u0000\u0000\u00e5\u00e6\u0005f\u0000\u0000\u00e6\u00e7" +
                    "\u0005l\u0000\u0000\u00e7\u00e8\u0005a\u0000\u0000\u00e8\u00e9\u0005g" +
                    "\u0000\u0000\u00e9\u00ea\u0005s\u0000\u0000\u00ea\u00eb\u0005:\u0000\u0000" +
                    "\u00eb\u001c\u0001\u0000\u0000\u0000\u00ec\u00ed\u0005v\u0000\u0000\u00ed" +
                    "\u00ee\u0005a\u0000\u0000\u00ee\u00ef\u0005l\u0000\u0000\u00ef\u00f0\u0005" +
                    "u\u0000\u0000\u00f0\u00f1\u0005e\u0000\u0000\u00f1\u00f2\u0005s\u0000" +
                    "\u0000\u00f2\u00f3\u0005:\u0000\u0000\u00f3\u001e\u0001\u0000\u0000\u0000" +
                    "\u00f4\u00f5\u0005e\u0000\u0000\u00f5\u00f6\u0005f\u0000\u0000\u00f6\u00f7" +
                    "\u0005f\u0000\u0000\u00f7\u00f8\u0005e\u0000\u0000\u00f8\u00f9\u0005c" +
                    "\u0000\u0000\u00f9\u00fa\u0005t\u0000\u0000\u00fa\u00fb\u0005:\u0000\u0000" +
                    "\u00fb \u0001\u0000\u0000\u0000\u00fc\u00fd\u0005d\u0000\u0000\u00fd\u00fe" +
                    "\u0005i\u0000\u0000\u00fe\u00ff\u0005c\u0000\u0000\u00ff\u0100\u0005e" +
                    "\u0000\u0000\u0100\u0101\u0005:\u0000\u0000\u0101\"\u0001\u0000\u0000" +
                    "\u0000\u0102\u0103\u0005e\u0000\u0000\u0103\u0104\u0005x\u0000\u0000\u0104" +
                    "\u0105\u0005p\u0000\u0000\u0105\u0106\u0005r\u0000\u0000\u0106\u0107\u0005" +
                    ":\u0000\u0000\u0107$\u0001\u0000\u0000\u0000\u0108\u0109\u0005e\u0000" +
                    "\u0000\u0109\u010a\u0005f\u0000\u0000\u010a\u010b\u0005f\u0000\u0000\u010b" +
                    "\u010c\u0005e\u0000\u0000\u010c\u010d\u0005c\u0000\u0000\u010d\u010e\u0005" +
                    "t\u0000\u0000\u010e\u010f\u0005-\u0000\u0000\u010f\u0110\u0005m\u0000" +
                    "\u0000\u0110\u0111\u0005s\u0000\u0000\u0111\u0112\u0005g\u0000\u0000\u0112" +
                    "\u0113\u0005:\u0000\u0000\u0113&\u0001\u0000\u0000\u0000\u0114\u0115\u0005" +
                    "b\u0000\u0000\u0115\u0116\u0005l\u0000\u0000\u0116\u0117\u0005o\u0000" +
                    "\u0000\u0117\u0118\u0005w\u0000\u0000\u0118\u0119\u0005:\u0000\u0000\u0119" +
                    "(\u0001\u0000\u0000\u0000\u011a\u011b\u0005[\u0000\u0000\u011b*\u0001" +
                    "\u0000\u0000\u0000\u011c\u011d\u0005]\u0000\u0000\u011d,\u0001\u0000\u0000" +
                    "\u0000\u011e\u011f\u0005:\u0000\u0000\u011f.\u0001\u0000\u0000\u0000\u0120" +
                    "\u0121\u0005 \u0000\u0000\u0121\u0122\u0005|\u0000\u0000\u0122\u0123\u0005" +
                    " \u0000\u0000\u01230\u0001\u0000\u0000\u0000\u0124\u0126\u0007\u0001\u0000" +
                    "\u0000\u0125\u0124\u0001\u0000\u0000\u0000\u0126\u0127\u0001\u0000\u0000" +
                    "\u0000\u0127\u0125\u0001\u0000\u0000\u0000\u0127\u0128\u0001\u0000\u0000" +
                    "\u0000\u01282\u0001\u0000\u0000\u0000\u0129\u012b\u0007\u0002\u0000\u0000" +
                    "\u012a\u0129\u0001\u0000\u0000\u0000\u012b\u012c\u0001\u0000\u0000\u0000" +
                    "\u012c\u012a\u0001\u0000\u0000\u0000\u012c\u012d\u0001\u0000\u0000\u0000" +
                    "\u012d4\u0001\u0000\u0000\u0000\u0007\u00009?FJ\u0127\u012c\u0001\u0006" +
                    "\u0000\u0000";
	public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}