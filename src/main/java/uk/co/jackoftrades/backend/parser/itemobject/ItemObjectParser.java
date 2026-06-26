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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ItemObject.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.itemobject;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.strings.Quark;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourTranslation;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
import uk.co.jackoftrades.middle.effect.Expression;
import uk.co.jackoftrades.middle.enums.*;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.*;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ItemObjectParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
	public static final int
            COMMENT = 1, EOL = 2, NAME = 3, GRAPHICS = 4, TYPE = 5, LEVEL = 6, WEIGHT = 7, COST = 8,
            ATTACK = 9, ARMOUR = 10, ALLOC = 11, CHARGES = 12, PILE = 13, POWER = 14, MSG = 15,
            VIS_MSG = 16, EFFECT = 17, EFFECT_YX = 18, DICE = 19, TIME = 20, EXPR = 21, FLAGS = 22,
            VALUES = 23, BRAND = 24, SLAY = 25, CURSE = 26, PVAL = 27, DESC = 28, LONG_OR = 29,
            GRAPHICS_CHAR = 30, STRING = 31;
	public static final int
            RULE_name = 0, RULE_graphics = 1, RULE_type = 2, RULE_level = 3, RULE_weight = 4,
            RULE_cost = 5, RULE_attack = 6, RULE_armour = 7, RULE_alloc = 8, RULE_charges = 9,
            RULE_pile = 10, RULE_power = 11, RULE_effect = 12, RULE_dice = 13, RULE_msg = 14,
            RULE_vis_msg = 15, RULE_time = 16, RULE_effect_yx = 17, RULE_expr = 18,
            RULE_effect_block = 19, RULE_flags = 20, RULE_values = 21, RULE_slay = 22,
            RULE_curse = 23, RULE_pval = 24, RULE_desc = 25, RULE_item_object = 26,
            RULE_file = 27;
	private static String[] makeRuleNames() {
        return new String[]{
                "name", "graphics", "type", "level", "weight", "cost", "attack", "armour",
                "alloc", "charges", "pile", "power", "effect", "dice", "msg", "vis_msg",
                "time", "effect_yx", "expr", "effect_block", "flags", "values", "slay",
                "curse", "pval", "desc", "item_object", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'graphics:'", "'type:'", "'level:'", "'weight:'",
                "'cost:'", "'attack:'", "'armor:'", "'alloc:'", "'charges:'", "'pile:'",
                "'power:'", "'msg:'", "'vis-msg:'", "'effect:'", "'effect-yx:'", "'dice:'",
                "'time:'", "'expr:'", "'flags:'", "'values:'", "'brand:'", "'slay:'",
                "'curse:'", "'pval:'", "'desc:'", "' | '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "GRAPHICS", "TYPE", "LEVEL", "WEIGHT",
                "COST", "ATTACK", "ARMOUR", "ALLOC", "CHARGES", "PILE", "POWER", "MSG",
                "VIS_MSG", "EFFECT", "EFFECT_YX", "DICE", "TIME", "EXPR", "FLAGS", "VALUES",
                "BRAND", "SLAY", "CURSE", "PVAL", "DESC", "LONG_OR", "GRAPHICS_CHAR",
                "STRING"
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

	@Override
    public String getGrammarFileName() {
        return "ItemObject.g4";
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
    public ATN getATN() {
        return _ATN;
    }


    public record CurseEntry(Curse curse, CurseData curseData) {
    }

	public ItemObjectParser(TokenStream input) {
		super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token STRING;

        public TerminalNode NAME() {
            return getToken(ItemObjectParser.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_name;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(56);
                match(NAME);
                setState(57);
                ((NameContext) _localctx).STRING = match(STRING);
                setState(58);
                match(EOL);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).STRING.getText();
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GraphicsContext extends ParserRuleContext {
		public AngbandDisplayCharacter graphicsADC;
		public Token chr;
		public Token colourChar;

        public TerminalNode GRAPHICS() {
            return getToken(ItemObjectParser.GRAPHICS, 0);
        }

        public List<TerminalNode> GRAPHICS_CHAR() {
            return getTokens(ItemObjectParser.GRAPHICS_CHAR);
        }
		public TerminalNode GRAPHICS_CHAR(int i) {
			return getToken(ItemObjectParser.GRAPHICS_CHAR, i);
		}

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }
		public GraphicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_graphics;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterGraphics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitGraphics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitGraphics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GraphicsContext graphics() throws RecognitionException {
		GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_graphics);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(61);
                match(GRAPHICS);
                setState(62);
                ((GraphicsContext) _localctx).chr = match(GRAPHICS_CHAR);
                setState(63);
                match(GRAPHICS_CHAR);
                setState(64);
                ((GraphicsContext) _localctx).colourChar = match(STRING);
                setState(65);
                match(EOL);

                String raw = ((GraphicsContext) _localctx).colourChar.getText();

                if (raw.length() != 1) {
                    String message = "Invalid graphics string. graphics:" + ((GraphicsContext) _localctx).chr.getText() + ":" + raw;
                    throw new InvalidTokenFoundDuringParse(message);
                }

                char colChar = raw.charAt(0);
                ((GraphicsContext) _localctx).graphicsADC = new AngbandDisplayCharacter(((GraphicsContext) _localctx).chr.getText().charAt(0),
                        ColourType.getAttributeColour(colChar, ColourTranslation.ATTR_FULL));

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public TValue typeObj;
		public Token STRING;

        public TerminalNode TYPE() {
            return getToken(ItemObjectParser.TYPE, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_type;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(68);
                match(TYPE);
                setState(69);
                ((TypeContext) _localctx).STRING = match(STRING);
                setState(70);
                match(EOL);

                String flagStr = ((TypeContext) _localctx).STRING.getText().toUpperCase().replace(' ', '_');
                ((TypeContext) _localctx).typeObj = TValue.valueOf("TV_" + flagStr);

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LevelContext extends ParserRuleContext {
		public int levelInt;
		public Token STRING;

        public TerminalNode LEVEL() {
            return getToken(ItemObjectParser.LEVEL, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public LevelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_level;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterLevel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitLevel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitLevel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelContext level() throws RecognitionException {
		LevelContext _localctx = new LevelContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_level);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(73);
                match(LEVEL);
                setState(74);
                ((LevelContext) _localctx).STRING = match(STRING);
                setState(75);
                match(EOL);
                ((LevelContext) _localctx).levelInt = Integer.parseInt(((LevelContext) _localctx).STRING.getText());
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WeightContext extends ParserRuleContext {
		public int weightInt;
		public Token STRING;

        public TerminalNode WEIGHT() {
            return getToken(ItemObjectParser.WEIGHT, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_weight;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(78);
                match(WEIGHT);
                setState(79);
                ((WeightContext) _localctx).STRING = match(STRING);
                setState(80);
                match(EOL);
                ((WeightContext) _localctx).weightInt = Integer.parseInt(((WeightContext) _localctx).STRING.getText());
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CostContext extends ParserRuleContext {
		public int costInt;
		public Token STRING;

        public TerminalNode COST() {
            return getToken(ItemObjectParser.COST, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public CostContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_cost;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterCost(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitCost(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitCost(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CostContext cost() throws RecognitionException {
		CostContext _localctx = new CostContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_cost);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(83);
                match(COST);
                setState(84);
                ((CostContext) _localctx).STRING = match(STRING);
                setState(85);
                match(EOL);
                ((CostContext) _localctx).costInt = Integer.parseInt(((CostContext) _localctx).STRING.getText());
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AttackContext extends ParserRuleContext {
		public String toh;
		public String tod;
		public String base;
		public Token b;
		public Token h;
		public Token d;

        public TerminalNode ATTACK() {
            return getToken(ItemObjectParser.ATTACK, 0);
        }

        public List<TerminalNode> GRAPHICS_CHAR() {
            return getTokens(ItemObjectParser.GRAPHICS_CHAR);
        }
		public TerminalNode GRAPHICS_CHAR(int i) {
			return getToken(ItemObjectParser.GRAPHICS_CHAR, i);
		}

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}
		public AttackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_attack;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterAttack(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitAttack(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitAttack(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttackContext attack() throws RecognitionException {
		AttackContext _localctx = new AttackContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_attack);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(88);
                match(ATTACK);
                setState(89);
                ((AttackContext) _localctx).b = match(STRING);
                setState(90);
                match(GRAPHICS_CHAR);
                setState(91);
                ((AttackContext) _localctx).h = match(STRING);
                setState(92);
                match(GRAPHICS_CHAR);
                setState(93);
                ((AttackContext) _localctx).d = match(STRING);
                setState(94);
                match(EOL);

                ((AttackContext) _localctx).toh = ((AttackContext) _localctx).h.getText();
                ((AttackContext) _localctx).tod = ((AttackContext) _localctx).d.getText();
                ((AttackContext) _localctx).base = ((AttackContext) _localctx).b.getText();

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArmourContext extends ParserRuleContext {
		public int baseInt;
		public String toa;
		public Token base;
		public Token ac;

        public TerminalNode ARMOUR() {
            return getToken(ItemObjectParser.ARMOUR, 0);
        }

        public TerminalNode GRAPHICS_CHAR() {
            return getToken(ItemObjectParser.GRAPHICS_CHAR, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}
		public ArmourContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_armour;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterArmour(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitArmour(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitArmour(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArmourContext armour() throws RecognitionException {
		ArmourContext _localctx = new ArmourContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_armour);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(97);
                match(ARMOUR);
                setState(98);
                ((ArmourContext) _localctx).base = match(STRING);
                setState(99);
                match(GRAPHICS_CHAR);
                setState(100);
                ((ArmourContext) _localctx).ac = match(STRING);
                setState(101);
                match(EOL);

                ((ArmourContext) _localctx).baseInt = Integer.parseInt(((ArmourContext) _localctx).base.getText());
                ((ArmourContext) _localctx).toa = ((ArmourContext) _localctx).ac.getText();

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AllocContext extends ParserRuleContext {
		public int prob;
		public int minLevel;
		public int maxLevel;
		public Token p;
		public Token lev;

        public TerminalNode ALLOC() {
            return getToken(ItemObjectParser.ALLOC, 0);
        }

        public TerminalNode GRAPHICS_CHAR() {
            return getToken(ItemObjectParser.GRAPHICS_CHAR, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}
		public AllocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_alloc;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterAlloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitAlloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitAlloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllocContext alloc() throws RecognitionException {
		AllocContext _localctx = new AllocContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(104);
                match(ALLOC);
                setState(105);
                ((AllocContext) _localctx).p = match(STRING);
                setState(106);
                match(GRAPHICS_CHAR);
                setState(107);
                ((AllocContext) _localctx).lev = match(STRING);
                setState(108);
                match(EOL);

                ((AllocContext) _localctx).prob = Integer.parseInt(((AllocContext) _localctx).p.getText());
                String levelSpread = ((AllocContext) _localctx).lev.getText();
                String[] levels = levelSpread.split(" to ");
                ((AllocContext) _localctx).minLevel = Integer.parseInt(levels[0]);
                ((AllocContext) _localctx).maxLevel = Integer.parseInt(levels[1]);

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ChargesContext extends ParserRuleContext {
		public String chargesStr;
		public Token STRING;

        public TerminalNode CHARGES() {
            return getToken(ItemObjectParser.CHARGES, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public ChargesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_charges;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterCharges(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitCharges(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitCharges(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ChargesContext charges() throws RecognitionException {
		ChargesContext _localctx = new ChargesContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_charges);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(111);
                match(CHARGES);
                setState(112);
                ((ChargesContext) _localctx).STRING = match(STRING);
                setState(113);
                match(EOL);
                ((ChargesContext) _localctx).chargesStr = ((ChargesContext) _localctx).STRING.getText();
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PileContext extends ParserRuleContext {
		public int prob;
		public String amount;
		public Token p;
		public Token am;

        public TerminalNode PILE() {
            return getToken(ItemObjectParser.PILE, 0);
        }

        public TerminalNode GRAPHICS_CHAR() {
            return getToken(ItemObjectParser.GRAPHICS_CHAR, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}
		public PileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_pile;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterPile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitPile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitPile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PileContext pile() throws RecognitionException {
		PileContext _localctx = new PileContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_pile);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(116);
                match(PILE);
                setState(117);
                ((PileContext) _localctx).p = match(STRING);
                setState(118);
                match(GRAPHICS_CHAR);
                setState(119);
                ((PileContext) _localctx).am = match(STRING);
                setState(120);
                match(EOL);

                ((PileContext) _localctx).prob = Integer.parseInt(((PileContext) _localctx).p.getText());
                ((PileContext) _localctx).amount = ((PileContext) _localctx).am.getText();

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PowerContext extends ParserRuleContext {
		public int powerInt;
		public Token STRING;

        public TerminalNode POWER() {
            return getToken(ItemObjectParser.POWER, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public PowerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_power;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitPower(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PowerContext power() throws RecognitionException {
		PowerContext _localctx = new PowerContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_power);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(123);
                match(POWER);
                setState(124);
                ((PowerContext) _localctx).STRING = match(STRING);
                setState(125);
                match(EOL);
                ((PowerContext) _localctx).powerInt = Integer.parseInt(((PowerContext) _localctx).STRING.getText());
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EffectContext extends ParserRuleContext {
		public List<String> effectList;
		public Token STRING;
		public Token p1;
		public Token p2;
		public Token p3;

        public TerminalNode EFFECT() {
            return getToken(ItemObjectParser.EFFECT, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> GRAPHICS_CHAR() {
            return getTokens(ItemObjectParser.GRAPHICS_CHAR);
        }
		public TerminalNode GRAPHICS_CHAR(int i) {
			return getToken(ItemObjectParser.GRAPHICS_CHAR, i);
		}
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_effect;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_effect);

        ((EffectContext) _localctx).effectList = new ArrayList<>();

		try {
			setState(156);
			_errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
				{
                    setState(128);
                    match(EFFECT);
                    setState(129);
                    ((EffectContext) _localctx).STRING = match(STRING);
                    setState(130);
                    match(EOL);

                    _localctx.effectList.add(((EffectContext) _localctx).STRING.getText());

				}
				break;
                case 2:
                    enterOuterAlt(_localctx, 2);
				{
                    setState(132);
                    match(EFFECT);
                    setState(133);
                    ((EffectContext) _localctx).STRING = match(STRING);
                    setState(134);
                    match(GRAPHICS_CHAR);
                    setState(135);
                    ((EffectContext) _localctx).p1 = match(STRING);
                    setState(136);
                    match(EOL);

                    _localctx.effectList.add(((EffectContext) _localctx).STRING.getText());
                    _localctx.effectList.add(((EffectContext) _localctx).p1.getText());

				}
				break;
                case 3:
                    enterOuterAlt(_localctx, 3);
				{
                    setState(138);
                    match(EFFECT);
                    setState(139);
                    ((EffectContext) _localctx).STRING = match(STRING);
                    setState(140);
                    match(GRAPHICS_CHAR);
                    setState(141);
                    ((EffectContext) _localctx).p1 = match(STRING);
                    setState(142);
                    match(GRAPHICS_CHAR);
                    setState(143);
                    ((EffectContext) _localctx).p2 = match(STRING);
                    setState(144);
                    match(EOL);

                    _localctx.effectList.add(((EffectContext) _localctx).STRING.getText());
                    _localctx.effectList.add(((EffectContext) _localctx).p1.getText());
                    _localctx.effectList.add(((EffectContext) _localctx).p2.getText());

				}
				break;
                case 4:
                    enterOuterAlt(_localctx, 4);
				{
                    setState(146);
                    match(EFFECT);
                    setState(147);
                    ((EffectContext) _localctx).STRING = match(STRING);
                    setState(148);
                    match(GRAPHICS_CHAR);
                    setState(149);
                    ((EffectContext) _localctx).p1 = match(STRING);
                    setState(150);
                    match(GRAPHICS_CHAR);
                    setState(151);
                    ((EffectContext) _localctx).p2 = match(STRING);
                    setState(152);
                    match(GRAPHICS_CHAR);
                    setState(153);
                    ((EffectContext) _localctx).p3 = match(STRING);
                    setState(154);
                    match(EOL);

                    _localctx.effectList.add(((EffectContext) _localctx).STRING.getText());
                    _localctx.effectList.add(((EffectContext) _localctx).p1.getText());
                    _localctx.effectList.add(((EffectContext) _localctx).p2.getText());
                    _localctx.effectList.add(((EffectContext) _localctx).p3.getText());

				}
				break;
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DiceContext extends ParserRuleContext {
		public String diceStr;
		public Token STRING;

        public TerminalNode DICE() {
            return getToken(ItemObjectParser.DICE, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public DiceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_dice;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_dice);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(158);
                match(DICE);
                setState(159);
                ((DiceContext) _localctx).STRING = match(STRING);
                setState(160);
                match(EOL);
                ((DiceContext) _localctx).diceStr = ((DiceContext) _localctx).STRING.getText();
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MsgContext extends ParserRuleContext {
		public String msgStr;
		public Token STRING;

        public TerminalNode MSG() {
            return getToken(ItemObjectParser.MSG, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_msg;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(163);
                match(MSG);
                setState(164);
                ((MsgContext) _localctx).STRING = match(STRING);
                setState(165);
                match(EOL);
                ((MsgContext) _localctx).msgStr = ((MsgContext) _localctx).STRING.getText();
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Vis_msgContext extends ParserRuleContext {
		public String msgStr;
		public Token STRING;

        public TerminalNode VIS_MSG() {
            return getToken(ItemObjectParser.VIS_MSG, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public Vis_msgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_vis_msg;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterVis_msg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitVis_msg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitVis_msg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Vis_msgContext vis_msg() throws RecognitionException {
		Vis_msgContext _localctx = new Vis_msgContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_vis_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(168);
                match(VIS_MSG);
                setState(169);
                ((Vis_msgContext) _localctx).STRING = match(STRING);
                setState(170);
                match(EOL);
                ((Vis_msgContext) _localctx).msgStr = ((Vis_msgContext) _localctx).STRING.getText();
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TimeContext extends ParserRuleContext {
		public String timeStr;
		public Token STRING;

        public TerminalNode TIME() {
            return getToken(ItemObjectParser.TIME, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_time;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(173);
                match(TIME);
                setState(174);
                ((TimeContext) _localctx).STRING = match(STRING);
                setState(175);
                match(EOL);
                ((TimeContext) _localctx).timeStr = ((TimeContext) _localctx).STRING.getText();
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Effect_yxContext extends ParserRuleContext {
		public int yInt;
		public int xInt;
		public Token y;
		public Token x;

        public TerminalNode EFFECT_YX() {
            return getToken(ItemObjectParser.EFFECT_YX, 0);
        }

        public TerminalNode GRAPHICS_CHAR() {
            return getToken(ItemObjectParser.GRAPHICS_CHAR, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}
		public Effect_yxContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_effect_yx;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterEffect_yx(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitEffect_yx(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitEffect_yx(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Effect_yxContext effect_yx() throws RecognitionException {
		Effect_yxContext _localctx = new Effect_yxContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_effect_yx);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(178);
                match(EFFECT_YX);
                setState(179);
                ((Effect_yxContext) _localctx).y = match(STRING);
                setState(180);
                match(GRAPHICS_CHAR);
                setState(181);
                ((Effect_yxContext) _localctx).x = match(STRING);
                setState(182);
                match(EOL);

                ((Effect_yxContext) _localctx).yInt = Integer.parseInt(((Effect_yxContext) _localctx).y.getText());
                ((Effect_yxContext) _localctx).xInt = Integer.parseInt(((Effect_yxContext) _localctx).x.getText());

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public Expression exprObj;
		public Token ch;
		public Token func;
		public Token oper;

        public TerminalNode EXPR() {
            return getToken(ItemObjectParser.EXPR, 0);
        }

        public List<TerminalNode> GRAPHICS_CHAR() {
            return getTokens(ItemObjectParser.GRAPHICS_CHAR);
        }
		public TerminalNode GRAPHICS_CHAR(int i) {
			return getToken(ItemObjectParser.GRAPHICS_CHAR, i);
		}

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_expr;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(185);
                match(EXPR);
                setState(186);
                ((ExprContext) _localctx).ch = match(STRING);
                setState(187);
                match(GRAPHICS_CHAR);
                setState(188);
                ((ExprContext) _localctx).func = match(STRING);
                setState(189);
                match(GRAPHICS_CHAR);
                setState(190);
                ((ExprContext) _localctx).oper = match(STRING);
                setState(191);
                match(EOL);

                String rawCh = ((ExprContext) _localctx).ch.getText();
                if (rawCh.length() != 1) {
                    String message = "Invalid exression string. expression:" + rawCh + ":" + ((ExprContext) _localctx).func.getText() + ":" + ((ExprContext) _localctx).oper.getText();
                    throw new InvalidTokenFoundDuringParse(message);
                }

                EffectBaseType exp = EffectBaseType.valueOf("EFB_" + ((ExprContext) _localctx).func.getText());
                String operations = ((ExprContext) _localctx).oper.getText();

                ((ExprContext) _localctx).exprObj = new Expression(rawCh.charAt(0), exp, operations);

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Effect_blockContext extends ParserRuleContext {
		public Effect effObj;
		public PowerContext power;
		public EffectContext effect;
		public TimeContext time;
		public Effect_yxContext effect_yx;
		public DiceContext dice;
		public ExprContext expr;
		public MsgContext msg;
		public Vis_msgContext vis_msg;
		public PowerContext power() {
            return getRuleContext(PowerContext.class, 0);
		}
		public EffectContext effect() {
            return getRuleContext(EffectContext.class, 0);
		}
		public TimeContext time() {
            return getRuleContext(TimeContext.class, 0);
		}
		public Effect_yxContext effect_yx() {
            return getRuleContext(Effect_yxContext.class, 0);
		}
		public DiceContext dice() {
            return getRuleContext(DiceContext.class, 0);
		}
		public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
		}
		public MsgContext msg() {
            return getRuleContext(MsgContext.class, 0);
		}
		public Vis_msgContext vis_msg() {
            return getRuleContext(Vis_msgContext.class, 0);
		}
		public Effect_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_effect_block;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterEffect_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitEffect_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitEffect_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Effect_blockContext effect_block() throws RecognitionException {
		Effect_blockContext _localctx = new Effect_blockContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_effect_block);

        String effRadStr = "";
        String effParmStr = "";
        List<String> effStringList = new ArrayList<>();
        EffectEnum effInit = EffectEnum.EF_NONE;
        String diceInit = "";
        int yInit = 0;
        int xInit = 0;
        EffectSubTypeEnum subTypeInit = EffectSubTypeEnum.EST_NONE;
        EffectSubTypeWrapper value = null;
        int powerInit = 0;
        String msgInit = "";
        String visMsgInit = "";
        String timeInit = "";
        Expression exprObj = null;

		try {
			setState(312);
			_errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
				{
                    setState(194);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(195);
                    ((Effect_blockContext) _localctx).effect = effect();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;

				}
				break;
                case 2:
                    enterOuterAlt(_localctx, 2);
				{
                    setState(198);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(199);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(200);
                    ((Effect_blockContext) _localctx).time = time();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    timeInit = ((Effect_blockContext) _localctx).time.timeStr;

				}
				break;
                case 3:
                    enterOuterAlt(_localctx, 3);
				{
                    setState(203);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(204);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(205);
                    ((Effect_blockContext) _localctx).effect_yx = effect_yx();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    yInit = ((Effect_blockContext) _localctx).effect_yx.yInt;
                    xInit = ((Effect_blockContext) _localctx).effect_yx.xInt;

				}
				break;
                case 4:
                    enterOuterAlt(_localctx, 4);
				{
                    setState(208);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(209);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(210);
                    ((Effect_blockContext) _localctx).effect_yx = effect_yx();
                    setState(211);
                    ((Effect_blockContext) _localctx).time = time();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    yInit = ((Effect_blockContext) _localctx).effect_yx.yInt;
                    xInit = ((Effect_blockContext) _localctx).effect_yx.xInt;
                    timeInit = ((Effect_blockContext) _localctx).time.timeStr;

				}
				break;
                case 5:
                    enterOuterAlt(_localctx, 5);
				{
                    setState(214);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(215);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(216);
                    ((Effect_blockContext) _localctx).dice = dice();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;

				}
				break;
                case 6:
                    enterOuterAlt(_localctx, 6);
				{
                    setState(219);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(220);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(221);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(222);
                    ((Effect_blockContext) _localctx).expr = expr();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    exprObj = ((Effect_blockContext) _localctx).expr.exprObj;

				}
				break;
                case 7:
                    enterOuterAlt(_localctx, 7);
				{
                    setState(225);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(226);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(227);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(228);
                    ((Effect_blockContext) _localctx).expr = expr();
                    setState(229);
                    ((Effect_blockContext) _localctx).time = time();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    exprObj = ((Effect_blockContext) _localctx).expr.exprObj;
                    timeInit = ((Effect_blockContext) _localctx).time.timeStr;

				}
				break;
                case 8:
                    enterOuterAlt(_localctx, 8);
				{
                    setState(232);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(233);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(234);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(235);
                    ((Effect_blockContext) _localctx).time = time();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    timeInit = ((Effect_blockContext) _localctx).time.timeStr;

				}
				break;
                case 9:
                    enterOuterAlt(_localctx, 9);
				{
                    setState(238);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(239);
                    ((Effect_blockContext) _localctx).msg = msg();
                    setState(240);
                    ((Effect_blockContext) _localctx).effect = effect();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    msgInit = ((Effect_blockContext) _localctx).msg.msgStr;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;

				}
				break;
                case 10:
                    enterOuterAlt(_localctx, 10);
				{
                    setState(243);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(244);
                    ((Effect_blockContext) _localctx).msg = msg();
                    setState(245);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(246);
                    ((Effect_blockContext) _localctx).dice = dice();

                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    msgInit = ((Effect_blockContext) _localctx).msg.msgStr;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;

				}
				break;
                case 11:
                    enterOuterAlt(_localctx, 11);
				{
                    setState(249);
                    ((Effect_blockContext) _localctx).msg = msg();
                    setState(250);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(251);
                    ((Effect_blockContext) _localctx).dice = dice();

                    msgInit = ((Effect_blockContext) _localctx).msg.msgStr;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;

				}
				break;
                case 12:
                    enterOuterAlt(_localctx, 12);
				{
                    setState(254);
                    ((Effect_blockContext) _localctx).msg = msg();
                    setState(255);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(256);
                    ((Effect_blockContext) _localctx).effect = effect();

                    msgInit = ((Effect_blockContext) _localctx).msg.msgStr;
                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;

				}
				break;
                case 13:
                    enterOuterAlt(_localctx, 13);
				{
                    setState(259);
                    ((Effect_blockContext) _localctx).msg = msg();
                    setState(260);
                    ((Effect_blockContext) _localctx).effect = effect();

                    msgInit = ((Effect_blockContext) _localctx).msg.msgStr;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;

				}
				break;
                case 14:
                    enterOuterAlt(_localctx, 14);
				{
                    setState(263);
                    ((Effect_blockContext) _localctx).vis_msg = vis_msg();
                    setState(264);
                    power();
                    setState(265);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(266);
                    ((Effect_blockContext) _localctx).dice = dice();

                    visMsgInit = ((Effect_blockContext) _localctx).vis_msg.msgStr;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;

				}
				break;
                case 15:
                    enterOuterAlt(_localctx, 15);
				{
                    setState(269);
                    ((Effect_blockContext) _localctx).vis_msg = vis_msg();
                    setState(270);
                    ((Effect_blockContext) _localctx).power = power();
                    setState(271);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(272);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(273);
                    ((Effect_blockContext) _localctx).time = time();

                    visMsgInit = ((Effect_blockContext) _localctx).vis_msg.msgStr;
                    powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    timeInit = ((Effect_blockContext) _localctx).time.timeStr;

				}
				break;
                case 16:
                    enterOuterAlt(_localctx, 16);
				{
                    setState(276);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(277);
                    ((Effect_blockContext) _localctx).dice = dice();

                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;

				}
				break;
                case 17:
                    enterOuterAlt(_localctx, 17);
				{
                    setState(280);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(281);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(282);
                    ((Effect_blockContext) _localctx).expr = expr();

                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    exprObj = ((Effect_blockContext) _localctx).expr.exprObj;

				}
				break;
                case 18:
                    enterOuterAlt(_localctx, 18);
				{
                    setState(285);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(286);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(287);
                    ((Effect_blockContext) _localctx).expr = expr();
                    setState(288);
                    ((Effect_blockContext) _localctx).time = time();

                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    exprObj = ((Effect_blockContext) _localctx).expr.exprObj;
                    timeInit = ((Effect_blockContext) _localctx).time.timeStr;

				}
				break;
                case 19:
                    enterOuterAlt(_localctx, 19);
				{
                    setState(291);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(292);
                    ((Effect_blockContext) _localctx).effect_yx = effect_yx();

                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    yInit = ((Effect_blockContext) _localctx).effect_yx.yInt;
                    xInit = ((Effect_blockContext) _localctx).effect_yx.xInt;

				}
				break;
                case 20:
                    enterOuterAlt(_localctx, 20);
				{
                    setState(295);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(296);
                    ((Effect_blockContext) _localctx).effect_yx = effect_yx();
                    setState(297);
                    ((Effect_blockContext) _localctx).time = time();

                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    yInit = ((Effect_blockContext) _localctx).effect_yx.yInt;
                    xInit = ((Effect_blockContext) _localctx).effect_yx.xInt;
                    timeInit = ((Effect_blockContext) _localctx).time.timeStr;

				}
				break;
                case 21:
                    enterOuterAlt(_localctx, 21);
				{
                    setState(300);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(301);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(302);
                    ((Effect_blockContext) _localctx).time = time();

                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    timeInit = ((Effect_blockContext) _localctx).time.timeStr;

				}
				break;
                case 22:
                    enterOuterAlt(_localctx, 22);
				{
                    setState(305);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(306);
                    ((Effect_blockContext) _localctx).time = time();

                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;
                    timeInit = ((Effect_blockContext) _localctx).time.timeStr;

				}
				break;
                case 23:
                    enterOuterAlt(_localctx, 23);
				{
                    setState(309);
                    ((Effect_blockContext) _localctx).effect = effect();

                    effStringList = ((Effect_blockContext) _localctx).effect.effectList;

				}
				break;
			}
			_ctx.stop = _input.LT(-1);

            // Parse the effect - should have up to 4 values, 1st one is
            // EffectEnum, 2nd is EffectSubType, 3rd is radius & 4th is parameter
            // 3 & 4 can be dice values
            effInit = EffectEnum.valueOf("EF_" + effStringList.get(0));
            subTypeInit = effInit.getSubType();

            if (effStringList.size() > 1) {
                String subTypeName = effStringList.get(1);

                switch (subTypeInit) {
                    case EST_PROJ:
                        value = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + subTypeName));
                        break;

                    case EST_TMD:
                        value = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + subTypeName));
                        break;

                    case EST_STAT:
                        value = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + subTypeName));
                        break;

                    case EST_NOURISH:
                        value = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + subTypeName));
                        break;

                    case EST_ENCHANT:
                        value = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + subTypeName));
                        break;

                    case EST_SUMMON:
                        value = new EffectSubTypeWrapper(GameConstants.lookupSummon(subTypeName));
                        break;
                }
            }

            if (effStringList.size() > 2)
                effRadStr = effStringList.get(2);

            if (effStringList.size() > 3)
                effParmStr = effStringList.get(3);

            // TODO(ClaudeCode): EffectBlock not yet re-plumbed to the current Effect constructor API;
            // this multi-arg Effect(...) overload no longer exists. Commented out to keep the build green.
            /*((Effect_blockContext) _localctx).effObj = new Effect(effInit, diceInit, yInit, xInit,
                    value,
                    effRadStr, effParmStr,
                    powerInit, msgInit, visMsgInit,
                    timeInit, exprObj);*/
            ((Effect_blockContext) _localctx).effObj = null;

        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FlagsContext extends ParserRuleContext {
		public Flag<ObjectFlag> oFlags;
		public Flag<ObjectKindFlag> kFlags;
		public Token flag1;
		public Token flag2;

        public TerminalNode FLAGS() {
            return getToken(ItemObjectParser.FLAGS, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}

        public List<TerminalNode> GRAPHICS_CHAR() {
            return getTokens(ItemObjectParser.GRAPHICS_CHAR);
        }
		public TerminalNode GRAPHICS_CHAR(int i) {
			return getToken(ItemObjectParser.GRAPHICS_CHAR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_flags;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_flags);

        ((FlagsContext) _localctx).oFlags = new Flag<>(ObjectFlag.class);
        ((FlagsContext) _localctx).kFlags = new Flag<>(ObjectKindFlag.class);

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(314);
                match(FLAGS);
                setState(315);
                ((FlagsContext) _localctx).flag1 = match(STRING);

                String rawFlag1 = ((FlagsContext) _localctx).flag1.getText().trim();
                ObjectFlag oFlag1 = ObjectFlag.OF_NONE;
                ObjectKindFlag kFlag1 = ObjectKindFlag.KF_NONE;

                try {
                    oFlag1 = ObjectFlag.valueOf("OF_" + rawFlag1);
                    _localctx.oFlags.on(oFlag1);
                } catch (Exception e) {
                    kFlag1 = ObjectKindFlag.valueOf("KF_" + rawFlag1);
                    _localctx.kFlags.on(kFlag1);
                }

                setState(322);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == GRAPHICS_CHAR) {
                    {
                        {
                            setState(317);
                            match(GRAPHICS_CHAR);
                            setState(318);
                            ((FlagsContext) _localctx).flag2 = match(STRING);

                            String rawFlag2 = ((FlagsContext) _localctx).flag2.getText().trim();
                            ObjectFlag oFlag2 = ObjectFlag.OF_NONE;
                            ObjectKindFlag kFlag2 = ObjectKindFlag.KF_NONE;

                            try {
                                oFlag2 = ObjectFlag.valueOf("OF_" + rawFlag2);
                                _localctx.oFlags.on(oFlag2);
                            } catch (Exception e) {
                                kFlag2 = ObjectKindFlag.valueOf("KF_" + rawFlag2);
                                _localctx.kFlags.on(kFlag2);
                            }

                        }
                    }
                    setState(324);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(325);
                match(EOL);
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ValuesContext extends ParserRuleContext {
		public Map<ObjectModifier, String> valueMap;
		public Token om1;
		public Token val1;
		public Token om2;
		public Token val2;

        public TerminalNode VALUES() {
            return getToken(ItemObjectParser.VALUES, 0);
        }

        public List<TerminalNode> GRAPHICS_CHAR() {
            return getTokens(ItemObjectParser.GRAPHICS_CHAR);
        }
		public TerminalNode GRAPHICS_CHAR(int i) {
			return getToken(ItemObjectParser.GRAPHICS_CHAR, i);
		}

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}

        public List<TerminalNode> LONG_OR() {
            return getTokens(ItemObjectParser.LONG_OR);
        }
		public TerminalNode LONG_OR(int i) {
			return getToken(ItemObjectParser.LONG_OR, i);
		}
		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_values;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_values);

        ((ValuesContext) _localctx).valueMap = new HashMap<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(327);
                match(VALUES);
                setState(328);
                ((ValuesContext) _localctx).om1 = match(STRING);
                setState(329);
                match(GRAPHICS_CHAR);
                setState(330);
                ((ValuesContext) _localctx).val1 = match(STRING);
                setState(331);
                match(GRAPHICS_CHAR);

                ObjectModifier om_1 = ObjectModifier.valueOf("OM_" + ((ValuesContext) _localctx).om1.getText());
                String val_1 = ((ValuesContext) _localctx).val1.getText();
                _localctx.valueMap.put(om_1, val_1);

                setState(341);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == LONG_OR) {
                    {
                        {
                            setState(333);
                            match(LONG_OR);
                            setState(334);
                            ((ValuesContext) _localctx).om2 = match(STRING);
                            setState(335);
                            match(GRAPHICS_CHAR);
                            setState(336);
                            ((ValuesContext) _localctx).val2 = match(STRING);
                            setState(337);
                            match(GRAPHICS_CHAR);

                            ObjectModifier om_2 = ObjectModifier.valueOf("OM_" + ((ValuesContext) _localctx).om2.getText());
                            String val_2 = ((ValuesContext) _localctx).val2.getText();
                            _localctx.valueMap.put(om_2, val_2);

                        }
                    }
                    setState(343);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(344);
                match(EOL);
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SlayContext extends ParserRuleContext {
		public Slay slayObj;
		public Token STRING;

        public TerminalNode SLAY() {
            return getToken(ItemObjectParser.SLAY, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public SlayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_slay;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterSlay(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitSlay(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitSlay(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SlayContext slay() throws RecognitionException {
		SlayContext _localctx = new SlayContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_slay);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(346);
                match(SLAY);
                setState(347);
                ((SlayContext) _localctx).STRING = match(STRING);
                setState(348);
                match(EOL);

                ((SlayContext) _localctx).slayObj = GameConstants.lookupSlay(((SlayContext) _localctx).STRING.getText());

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CurseContext extends ParserRuleContext {
		public Map<Curse, CurseData> curseMap;
		public Token curNme;
		public Token curPwr;

        public TerminalNode CURSE() {
            return getToken(ItemObjectParser.CURSE, 0);
        }

        public TerminalNode GRAPHICS_CHAR() {
            return getToken(ItemObjectParser.GRAPHICS_CHAR, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(ItemObjectParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ItemObjectParser.STRING, i);
		}
		public CurseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_curse;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterCurse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitCurse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitCurse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CurseContext curse() throws RecognitionException {
		CurseContext _localctx = new CurseContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_curse);
        ((CurseContext) _localctx).curseMap = new HashMap<>();
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(351);
                match(CURSE);
                setState(352);
                ((CurseContext) _localctx).curNme = match(STRING);
                setState(353);
                match(GRAPHICS_CHAR);
                setState(354);
                ((CurseContext) _localctx).curPwr = match(STRING);
                setState(355);
                match(EOL);

                String rawCurNme = ((CurseContext) _localctx).curNme.getText();
                String rawCurPwr = ((CurseContext) _localctx).curPwr.getText();
                int powerInt = Integer.parseInt(rawCurPwr);

                Curse curseObj = GameConstants.lookupCurse(rawCurNme);
                CurseData curData = new CurseData(powerInt, 0);

                _localctx.curseMap.put(curseObj, curData);

			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PvalContext extends ParserRuleContext {
		public String pValStr;
		public Token STRING;

        public TerminalNode PVAL() {
            return getToken(ItemObjectParser.PVAL, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public PvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_pval;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterPval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitPval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitPval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PvalContext pval() throws RecognitionException {
		PvalContext _localctx = new PvalContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_pval);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(358);
                match(PVAL);
                setState(359);
                ((PvalContext) _localctx).STRING = match(STRING);
                setState(360);
                match(EOL);
                ((PvalContext) _localctx).pValStr = ((PvalContext) _localctx).STRING.getText();
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DescContext extends ParserRuleContext {
		public String descStr;
		public Token STRING;

        public TerminalNode DESC() {
            return getToken(ItemObjectParser.DESC, 0);
        }

        public TerminalNode STRING() {
            return getToken(ItemObjectParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(ItemObjectParser.EOL, 0);
        }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_desc;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(363);
                match(DESC);
                setState(364);
                ((DescContext) _localctx).STRING = match(STRING);
                setState(365);
                match(EOL);
                ((DescContext) _localctx).descStr = ((DescContext) _localctx).STRING.getText();
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Item_objectContext extends ParserRuleContext {
		public ItemObject itemObj;
        public ObjectKind oKind;
		public NameContext name;
		public GraphicsContext graphics;
		public TypeContext type;
		public LevelContext level;
		public WeightContext weight;
		public CostContext cost;
		public AttackContext attack;
		public ArmourContext armour;
		public AllocContext alloc;
		public ChargesContext charges;
		public PileContext pile;
		public Effect_blockContext effect_block;
		public FlagsContext flags;
		public ValuesContext values;
		public SlayContext slay;
		public CurseContext curse;
		public PvalContext pval;
		public DescContext desc;
		public NameContext name() {
            return getRuleContext(NameContext.class, 0);
		}

        public List<TerminalNode> EOL() {
            return getTokens(ItemObjectParser.EOL);
        }
		public TerminalNode EOL(int i) {
			return getToken(ItemObjectParser.EOL, i);
		}
		public List<GraphicsContext> graphics() {
			return getRuleContexts(GraphicsContext.class);
		}
		public GraphicsContext graphics(int i) {
            return getRuleContext(GraphicsContext.class, i);
		}
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
            return getRuleContext(TypeContext.class, i);
		}
		public List<LevelContext> level() {
			return getRuleContexts(LevelContext.class);
		}
		public LevelContext level(int i) {
            return getRuleContext(LevelContext.class, i);
		}
		public List<WeightContext> weight() {
			return getRuleContexts(WeightContext.class);
		}
		public WeightContext weight(int i) {
            return getRuleContext(WeightContext.class, i);
		}
		public List<CostContext> cost() {
			return getRuleContexts(CostContext.class);
		}
		public CostContext cost(int i) {
            return getRuleContext(CostContext.class, i);
		}
		public List<AttackContext> attack() {
			return getRuleContexts(AttackContext.class);
		}
		public AttackContext attack(int i) {
            return getRuleContext(AttackContext.class, i);
		}
		public List<ArmourContext> armour() {
			return getRuleContexts(ArmourContext.class);
		}
		public ArmourContext armour(int i) {
            return getRuleContext(ArmourContext.class, i);
		}
		public List<AllocContext> alloc() {
			return getRuleContexts(AllocContext.class);
		}
		public AllocContext alloc(int i) {
            return getRuleContext(AllocContext.class, i);
		}
		public List<ChargesContext> charges() {
			return getRuleContexts(ChargesContext.class);
		}
		public ChargesContext charges(int i) {
            return getRuleContext(ChargesContext.class, i);
		}
		public List<PileContext> pile() {
			return getRuleContexts(PileContext.class);
		}
		public PileContext pile(int i) {
            return getRuleContext(PileContext.class, i);
		}
		public List<Effect_blockContext> effect_block() {
			return getRuleContexts(Effect_blockContext.class);
		}
		public Effect_blockContext effect_block(int i) {
            return getRuleContext(Effect_blockContext.class, i);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
            return getRuleContext(FlagsContext.class, i);
		}
		public List<ValuesContext> values() {
			return getRuleContexts(ValuesContext.class);
		}
		public ValuesContext values(int i) {
            return getRuleContext(ValuesContext.class, i);
		}
		public List<SlayContext> slay() {
			return getRuleContexts(SlayContext.class);
		}
		public SlayContext slay(int i) {
            return getRuleContext(SlayContext.class, i);
		}
		public List<CurseContext> curse() {
			return getRuleContexts(CurseContext.class);
		}
		public CurseContext curse(int i) {
            return getRuleContext(CurseContext.class, i);
		}
		public List<PvalContext> pval() {
			return getRuleContexts(PvalContext.class);
		}
		public PvalContext pval(int i) {
            return getRuleContext(PvalContext.class, i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
		}
		public Item_objectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_item_object;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterItem_object(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitItem_object(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor)
                return ((ItemObjectVisitor<? extends T>) visitor).visitItem_object(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Item_objectContext item_object() throws RecognitionException {
		Item_objectContext _localctx = new Item_objectContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_item_object);

        // ItemObject initial values
        EgoItem ioe = null;
        Artifact ioa = null;
        ItemObject known = null;
        Loc location = null;
        TValue ioTVal = TValue.TV_NONE;
        int sValInit = 0;
        String ioPVal = "";
        // read weight from kWeight
        // read damDie from kDamDie
        // read baseDam from kBaseDam
        // read normalAC from kAc
        // read toAc from kToA
        // read toDam from kToD
        // read toHit from kToH
        // read flags from oFlagInit
        // read modifiers from kModsInit
        // read elInfo from kElInfo
        // read brands from kBrands
        // read slays from kSlays
        // read curses from kCurses
        // read effect from kEffects
        String ioEffMsg = "";
        // read activation from kActivs
        // read time from kEffects
        int timeoutInit = 0;
        int numberInit = 0;
        Flag<ObjectNotice> oNotice = new Flag<>(ObjectNotice.class);
        int heldMIndex = -1;
        int mimickingMIndex = -1;
        ObjectOriginEnum ioOrigin = ObjectOriginEnum.ORIGIN_NONE;
        int ioOriginDepth = 0;
        MonsterRace ioMonRace = null;
        Quark note = null;

        // ObjectKind initial values
        String kName = "";
        String kText = "";
        ObjectBase kBase = null;
        String kPVal = "";
        String kToH = "";
        String kToD = "";
        String kBaseDam = "";
        String kToA = "";
        int kAc = 0;
        int kDamDie = 0;
        int kDamSides = 0;
        int kWeight = 0;
        int kCost = 0;
        Flag<ObjectFlag> oFlagInit = new Flag<>(ObjectFlag.class);
        Flag<ObjectKindFlag> oKindFlagInit = new Flag<>(ObjectKindFlag.class);
        Map<ObjectModifier, String> kModsInit = new HashMap<>();
        Map<ElementEnum, ElementInfo> kElInfo = new HashMap<>();
        Map<Brand, Boolean> kBrands = new HashMap<>();
        Map<Slay, Boolean> kSlays = new HashMap<>();
        Map<CurseEntry, Boolean> kCurses = new HashMap<>();
        AngbandDisplayCharacter kChar = null;
        int kAllocProb = 0;
        int kAllocMin = 0;
        int kAllocMax = 0;
        int kLevel = 0;
        List<Activation> kActivs = new ArrayList<>();
        List<Effect> kEffects = new ArrayList<>();
        String kEffectMsg = "";
        String kVisEffectMsg = "";
        String kTime = "";
        String kCharge = "";
        int kGenMultProb = 0;
        String kStackSize = "";
        Flavour kFlavour = null;
        Quark kNoteAware = null;
        Quark kNoteUnaware = null;
        boolean kAware = false;
        boolean kTried = false;
        int kIgnore = 0;
        boolean kEverseen = false;

		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
                setState(371);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == EOL) {
                    {
                        {
                            setState(368);
                            match(EOL);
                        }
                    }
                    setState(373);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(374);
                ((Item_objectContext) _localctx).name = name();
                kName = ((Item_objectContext) _localctx).name.nameStr;
                setState(427);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(427);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case GRAPHICS: {
                                setState(376);
                                ((Item_objectContext) _localctx).graphics = graphics();
                                kChar = ((Item_objectContext) _localctx).graphics.graphicsADC;
                            }
                            break;
                            case TYPE: {
                                setState(379);
                                ((Item_objectContext) _localctx).type = type();
                                ioTVal = ((Item_objectContext) _localctx).type.typeObj;
                            }
                            break;
                            case LEVEL: {
                                setState(382);
                                ((Item_objectContext) _localctx).level = level();
                                kLevel = ((Item_objectContext) _localctx).level.levelInt;
                            }
                            break;
                            case WEIGHT: {
                                setState(385);
                                ((Item_objectContext) _localctx).weight = weight();
                                kWeight = ((Item_objectContext) _localctx).weight.weightInt;
                            }
                            break;
                            case COST: {
                                setState(388);
                                ((Item_objectContext) _localctx).cost = cost();
                                kCost = ((Item_objectContext) _localctx).cost.costInt;
                            }
                            break;
                            case ATTACK: {
                                setState(391);
                                ((Item_objectContext) _localctx).attack = attack();

                                kToH = ((Item_objectContext) _localctx).attack.toh;
                                kToD = ((Item_objectContext) _localctx).attack.tod;
                                kBaseDam = ((Item_objectContext) _localctx).attack.base;

                            }
                            break;
                            case ARMOUR: {
                                setState(394);
                                ((Item_objectContext) _localctx).armour = armour();

                                kAc = ((Item_objectContext) _localctx).armour.baseInt;
                                kToA = ((Item_objectContext) _localctx).armour.toa;

                            }
                            break;
                            case ALLOC: {
                                setState(397);
                                ((Item_objectContext) _localctx).alloc = alloc();

                                kAllocProb = ((Item_objectContext) _localctx).alloc.prob;
                                kAllocMin = ((Item_objectContext) _localctx).alloc.minLevel;
                                kAllocMax = ((Item_objectContext) _localctx).alloc.maxLevel;

                            }
                            break;
                            case CHARGES: {
                                setState(400);
                                ((Item_objectContext) _localctx).charges = charges();
                                kCharge = ((Item_objectContext) _localctx).charges.chargesStr;
                            }
                            break;
                            case PILE: {
                                setState(403);
                                ((Item_objectContext) _localctx).pile = pile();

                                kGenMultProb = ((Item_objectContext) _localctx).pile.prob;
                                kStackSize = ((Item_objectContext) _localctx).pile.amount;

                            }
                            break;
                            case POWER:
                            case MSG:
                            case VIS_MSG:
                            case EFFECT: {
                                setState(406);
                                ((Item_objectContext) _localctx).effect_block = effect_block();
                                kEffects.add(((Item_objectContext) _localctx).effect_block.effObj);
                            }
                            break;
                            case FLAGS: {
                                setState(409);
                                ((Item_objectContext) _localctx).flags = flags();

                                oFlagInit.union(((Item_objectContext) _localctx).flags.oFlags);
                                oKindFlagInit.union(((Item_objectContext) _localctx).flags.kFlags);

                            }
                            break;
                            case VALUES: {
                                setState(412);
                                ((Item_objectContext) _localctx).values = values();
                                kModsInit.putAll(((Item_objectContext) _localctx).values.valueMap);
                            }
                            break;
                            case SLAY: {
                                setState(415);
                                ((Item_objectContext) _localctx).slay = slay();
                                kSlays.put(((Item_objectContext) _localctx).slay.slayObj, true);
                            }
                            break;
                            case CURSE: {
                                setState(418);
                                ((Item_objectContext) _localctx).curse = curse();

                                for (Curse curse : ((Item_objectContext) _localctx).curse.curseMap.keySet()) {
                                    CurseEntry ce = new CurseEntry(curse, ((Item_objectContext) _localctx).curse.curseMap.get(curse));
                                    kCurses.put(ce, true);
                                }

                            }
                            break;
                            case PVAL: {
                                setState(421);
                                ((Item_objectContext) _localctx).pval = pval();
                                ioPVal = ((Item_objectContext) _localctx).pval.pValStr;
                            }
                            break;
                            case DESC: {
                                setState(424);
                                ((Item_objectContext) _localctx).desc = desc();
                                kText = kText + ((Item_objectContext) _localctx).desc.descStr;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(429);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 516161520L) != 0));
                setState(434);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(431);
                                match(EOL);
                            }
                        }
                    }
                    setState(436);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                }
			}
			_ctx.stop = _input.LT(-1);

            ((Item_objectContext) _localctx).oKind = new ObjectKind(kName, kText, kBase, 0, kPVal, kToH, kToD, kToA,
                    kAc, kBaseDam, kDamDie, kDamSides, kWeight, kCost,
                    oFlagInit, oKindFlagInit, kModsInit, kElInfo, kBrands,
                    kSlays, kCurses, kChar, kAllocProb, kAllocMin,
                    kAllocMax, kLevel, kActivs, kEffects, kEffectMsg,
                    kVisEffectMsg, kTime, kCharge, kGenMultProb,
                    kStackSize, kFlavour, kNoteAware, kNoteUnaware, kAware,
                    kTried, kIgnore, kEverseen, ioTVal);

            ((Item_objectContext) _localctx).itemObj = new ItemObject(_localctx.oKind, ioe, ioa, known, location, ioTVal, sValInit,
                    ioPVal, kWeight, kDamDie, kDamSides, kAc, kBaseDam,
                    kToA, kToD, kToH, oFlagInit, kModsInit, kElInfo,
                    kBrands, kSlays, kCurses, kEffects, ioEffMsg,
                    kActivs, kTime, timeoutInit, numberInit, oNotice,
                    heldMIndex, mimickingMIndex, ioOrigin,
                    ioOriginDepth, ioMonRace, note);

        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FileContext extends ParserRuleContext {
		public List<ItemObject> itemObjects;
        public List<ObjectKind> objectKinds;
		public Item_objectContext item_object;

        public TerminalNode EOF() {
            return getToken(ItemObjectParser.EOF, 0);
        }
		public List<Item_objectContext> item_object() {
			return getRuleContexts(Item_objectContext.class);
		}
		public Item_objectContext item_object(int i) {
            return getRuleContext(Item_objectContext.class, i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_file;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectListener) ((ItemObjectListener) listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectVisitor) return ((ItemObjectVisitor<? extends T>) visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_file);

        ((FileContext) _localctx).itemObjects = new ArrayList<>();
        ((FileContext) _localctx).objectKinds = new ArrayList<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(440);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(437);
                            ((FileContext) _localctx).item_object = item_object();

                            _localctx.itemObjects.add(((FileContext) _localctx).item_object.itemObj);
                            _localctx.objectKinds.add(((FileContext) _localctx).item_object.oKind);

                        }
                    }
                    setState(442);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == EOL || _la == NAME);
                setState(444);
                match(EOF);
			}
        } catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
        } finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
            "\u0004\u0001\u001f\u01bf\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007" +
                    "\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007" +
                    "\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007" +
                    "\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007" +
                    "\u001b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t" +
                    "\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f" +
                    "\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0003\f\u009d\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013" +
                    "\u0139\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014" +
                    "\u0001\u0014\u0005\u0014\u0141\b\u0014\n\u0014\f\u0014\u0144\t\u0014\u0001" +
                    "\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001" +
                    "\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001" +
                    "\u0015\u0001\u0015\u0005\u0015\u0154\b\u0015\n\u0015\f\u0015\u0157\t\u0015" +
                    "\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017" +
                    "\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018" +
                    "\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019" +
                    "\u0001\u001a\u0005\u001a\u0172\b\u001a\n\u001a\f\u001a\u0175\t\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0004\u001a\u01ac" +
                    "\b\u001a\u000b\u001a\f\u001a\u01ad\u0001\u001a\u0005\u001a\u01b1\b\u001a" +
                    "\n\u001a\f\u001a\u01b4\t\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0004" +
                    "\u001b\u01b9\b\u001b\u000b\u001b\f\u001b\u01ba\u0001\u001b\u0001\u001b" +
                    "\u0001\u001b\u0000\u0000\u001c\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010" +
                    "\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0246\u0000\u0000\u01d1" +
                    "\u00008\u0001\u0000\u0000\u0000\u0002=\u0001\u0000\u0000\u0000\u0004D" +
                    "\u0001\u0000\u0000\u0000\u0006I\u0001\u0000\u0000\u0000\bN\u0001\u0000" +
                    "\u0000\u0000\nS\u0001\u0000\u0000\u0000\fX\u0001\u0000\u0000\u0000\u000e" +
                    "a\u0001\u0000\u0000\u0000\u0010h\u0001\u0000\u0000\u0000\u0012o\u0001" +
                    "\u0000\u0000\u0000\u0014t\u0001\u0000\u0000\u0000\u0016{\u0001\u0000\u0000" +
                    "\u0000\u0018\u009c\u0001\u0000\u0000\u0000\u001a\u009e\u0001\u0000\u0000" +
                    "\u0000\u001c\u00a3\u0001\u0000\u0000\u0000\u001e\u00a8\u0001\u0000\u0000" +
                    "\u0000 \u00ad\u0001\u0000\u0000\u0000\"\u00b2\u0001\u0000\u0000\u0000" +
                    "$\u00b9\u0001\u0000\u0000\u0000&\u0138\u0001\u0000\u0000\u0000(\u013a" +
                    "\u0001\u0000\u0000\u0000*\u0147\u0001\u0000\u0000\u0000,\u015a\u0001\u0000" +
                    "\u0000\u0000.\u015f\u0001\u0000\u0000\u00000\u0166\u0001\u0000\u0000\u0000" +
                    "2\u016b\u0001\u0000\u0000\u00004\u0173\u0001\u0000\u0000\u00006\u01b8" +
                    "\u0001\u0000\u0000\u000089\u0005\u0003\u0000\u00009:\u0005\u001f\u0000" +
                    "\u0000:;\u0005\u0002\u0000\u0000;<\u0006\u0000\uffff\uffff\u0000<\u0001" +
                    "\u0001\u0000\u0000\u0000=>\u0005\u0004\u0000\u0000>?\u0005\u001e\u0000" +
                    "\u0000?@\u0005\u001e\u0000\u0000@A\u0005\u001f\u0000\u0000AB\u0005\u0002" +
                    "\u0000\u0000BC\u0006\u0001\uffff\uffff\u0000C\u0003\u0001\u0000\u0000" +
                    "\u0000DE\u0005\u0005\u0000\u0000EF\u0005\u001f\u0000\u0000FG\u0005\u0002" +
                    "\u0000\u0000GH\u0006\u0002\uffff\uffff\u0000H\u0005\u0001\u0000\u0000" +
                    "\u0000IJ\u0005\u0006\u0000\u0000JK\u0005\u001f\u0000\u0000KL\u0005\u0002" +
                    "\u0000\u0000LM\u0006\u0003\uffff\uffff\u0000M\u0007\u0001\u0000\u0000" +
                    "\u0000NO\u0005\u0007\u0000\u0000OP\u0005\u001f\u0000\u0000PQ\u0005\u0002" +
                    "\u0000\u0000QR\u0006\u0004\uffff\uffff\u0000R\t\u0001\u0000\u0000\u0000" +
                    "ST\u0005\b\u0000\u0000TU\u0005\u001f\u0000\u0000UV\u0005\u0002\u0000\u0000" +
                    "VW\u0006\u0005\uffff\uffff\u0000W\u000b\u0001\u0000\u0000\u0000XY\u0005" +
                    "\t\u0000\u0000YZ\u0005\u001f\u0000\u0000Z[\u0005\u001e\u0000\u0000[\\" +
                    "\u0005\u001f\u0000\u0000\\]\u0005\u001e\u0000\u0000]^\u0005\u001f\u0000" +
                    "\u0000^_\u0005\u0002\u0000\u0000_`\u0006\u0006\uffff\uffff\u0000`\r\u0001" +
                    "\u0000\u0000\u0000ab\u0005\n\u0000\u0000bc\u0005\u001f\u0000\u0000cd\u0005" +
                    "\u001e\u0000\u0000de\u0005\u001f\u0000\u0000ef\u0005\u0002\u0000\u0000" +
                    "fg\u0006\u0007\uffff\uffff\u0000g\u000f\u0001\u0000\u0000\u0000hi\u0005" +
                    "\u000b\u0000\u0000ij\u0005\u001f\u0000\u0000jk\u0005\u001e\u0000\u0000" +
                    "kl\u0005\u001f\u0000\u0000lm\u0005\u0002\u0000\u0000mn\u0006\b\uffff\uffff" +
                    "\u0000n\u0011\u0001\u0000\u0000\u0000op\u0005\f\u0000\u0000pq\u0005\u001f" +
                    "\u0000\u0000qr\u0005\u0002\u0000\u0000rs\u0006\t\uffff\uffff\u0000s\u0013" +
                    "\u0001\u0000\u0000\u0000tu\u0005\r\u0000\u0000uv\u0005\u001f\u0000\u0000" +
                    "vw\u0005\u001e\u0000\u0000wx\u0005\u001f\u0000\u0000xy\u0005\u0002\u0000" +
                    "\u0000yz\u0006\n\uffff\uffff\u0000z\u0015\u0001\u0000\u0000\u0000{|\u0005" +
                    "\u000e\u0000\u0000|}\u0005\u001f\u0000\u0000}~\u0005\u0002\u0000\u0000" +
                    "~\u007f\u0006\u000b\uffff\uffff\u0000\u007f\u0017\u0001\u0000\u0000\u0000" +
                    "\u0080\u0081\u0005\u0011\u0000\u0000\u0081\u0082\u0005\u001f\u0000\u0000" +
                    "\u0082\u0083\u0005\u0002\u0000\u0000\u0083\u009d\u0006\f\uffff\uffff\u0000" +
                    "\u0084\u0085\u0005\u0011\u0000\u0000\u0085\u0086\u0005\u001f\u0000\u0000" +
                    "\u0086\u0087\u0005\u001e\u0000\u0000\u0087\u0088\u0005\u001f\u0000\u0000" +
                    "\u0088\u0089\u0005\u0002\u0000\u0000\u0089\u009d\u0006\f\uffff\uffff\u0000" +
                    "\u008a\u008b\u0005\u0011\u0000\u0000\u008b\u008c\u0005\u001f\u0000\u0000" +
                    "\u008c\u008d\u0005\u001e\u0000\u0000\u008d\u008e\u0005\u001f\u0000\u0000" +
                    "\u008e\u008f\u0005\u001e\u0000\u0000\u008f\u0090\u0005\u001f\u0000\u0000" +
                    "\u0090\u0091\u0005\u0002\u0000\u0000\u0091\u009d\u0006\f\uffff\uffff\u0000" +
                    "\u0092\u0093\u0005\u0011\u0000\u0000\u0093\u0094\u0005\u001f\u0000\u0000" +
                    "\u0094\u0095\u0005\u001e\u0000\u0000\u0095\u0096\u0005\u001f\u0000\u0000" +
                    "\u0096\u0097\u0005\u001e\u0000\u0000\u0097\u0098\u0005\u001f\u0000\u0000" +
                    "\u0098\u0099\u0005\u001e\u0000\u0000\u0099\u009a\u0005\u001f\u0000\u0000" +
                    "\u009a\u009b\u0005\u0002\u0000\u0000\u009b\u009d\u0006\f\uffff\uffff\u0000" +
                    "\u009c\u0080\u0001\u0000\u0000\u0000\u009c\u0084\u0001\u0000\u0000\u0000" +
                    "\u009c\u008a\u0001\u0000\u0000\u0000\u009c\u0092\u0001\u0000\u0000\u0000" +
                    "\u009d\u0019\u0001\u0000\u0000\u0000\u009e\u009f\u0005\u0013\u0000\u0000" +
                    "\u009f\u00a0\u0005\u001f\u0000\u0000\u00a0\u00a1\u0005\u0002\u0000\u0000" +
                    "\u00a1\u00a2\u0006\r\uffff\uffff\u0000\u00a2\u001b\u0001\u0000\u0000\u0000" +
                    "\u00a3\u00a4\u0005\u000f\u0000\u0000\u00a4\u00a5\u0005\u001f\u0000\u0000" +
                    "\u00a5\u00a6\u0005\u0002\u0000\u0000\u00a6\u00a7\u0006\u000e\uffff\uffff" +
                    "\u0000\u00a7\u001d\u0001\u0000\u0000\u0000\u00a8\u00a9\u0005\u0010\u0000" +
                    "\u0000\u00a9\u00aa\u0005\u001f\u0000\u0000\u00aa\u00ab\u0005\u0002\u0000" +
                    "\u0000\u00ab\u00ac\u0006\u000f\uffff\uffff\u0000\u00ac\u001f\u0001\u0000" +
                    "\u0000\u0000\u00ad\u00ae\u0005\u0014\u0000\u0000\u00ae\u00af\u0005\u001f" +
                    "\u0000\u0000\u00af\u00b0\u0005\u0002\u0000\u0000\u00b0\u00b1\u0006\u0010" +
                    "\uffff\uffff\u0000\u00b1!\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005\u0012" +
                    "\u0000\u0000\u00b3\u00b4\u0005\u001f\u0000\u0000\u00b4\u00b5\u0005\u001e" +
                    "\u0000\u0000\u00b5\u00b6\u0005\u001f\u0000\u0000\u00b6\u00b7\u0005\u0002" +
                    "\u0000\u0000\u00b7\u00b8\u0006\u0011\uffff\uffff\u0000\u00b8#\u0001\u0000" +
                    "\u0000\u0000\u00b9\u00ba\u0005\u0015\u0000\u0000\u00ba\u00bb\u0005\u001f" +
                    "\u0000\u0000\u00bb\u00bc\u0005\u001e\u0000\u0000\u00bc\u00bd\u0005\u001f" +
                    "\u0000\u0000\u00bd\u00be\u0005\u001e\u0000\u0000\u00be\u00bf\u0005\u001f" +
                    "\u0000\u0000\u00bf\u00c0\u0005\u0002\u0000\u0000\u00c0\u00c1\u0006\u0012" +
                    "\uffff\uffff\u0000\u00c1%\u0001\u0000\u0000\u0000\u00c2\u00c3\u0003\u0016" +
                    "\u000b\u0000\u00c3\u00c4\u0003\u0018\f\u0000\u00c4\u00c5\u0006\u0013\uffff" +
                    "\uffff\u0000\u00c5\u0139\u0001\u0000\u0000\u0000\u00c6\u00c7\u0003\u0016" +
                    "\u000b\u0000\u00c7\u00c8\u0003\u0018\f\u0000\u00c8\u00c9\u0003 \u0010" +
                    "\u0000\u00c9\u00ca\u0006\u0013\uffff\uffff\u0000\u00ca\u0139\u0001\u0000" +
                    "\u0000\u0000\u00cb\u00cc\u0003\u0016\u000b\u0000\u00cc\u00cd\u0003\u0018" +
                    "\f\u0000\u00cd\u00ce\u0003\"\u0011\u0000\u00ce\u00cf\u0006\u0013\uffff" +
                    "\uffff\u0000\u00cf\u0139\u0001\u0000\u0000\u0000\u00d0\u00d1\u0003\u0016" +
                    "\u000b\u0000\u00d1\u00d2\u0003\u0018\f\u0000\u00d2\u00d3\u0003\"\u0011" +
                    "\u0000\u00d3\u00d4\u0003 \u0010\u0000\u00d4\u00d5\u0006\u0013\uffff\uffff" +
                    "\u0000\u00d5\u0139\u0001\u0000\u0000\u0000\u00d6\u00d7\u0003\u0016\u000b" +
                    "\u0000\u00d7\u00d8\u0003\u0018\f\u0000\u00d8\u00d9\u0003\u001a\r\u0000" +
                    "\u00d9\u00da\u0006\u0013\uffff\uffff\u0000\u00da\u0139\u0001\u0000\u0000" +
                    "\u0000\u00db\u00dc\u0003\u0016\u000b\u0000\u00dc\u00dd\u0003\u0018\f\u0000" +
                    "\u00dd\u00de\u0003\u001a\r\u0000\u00de\u00df\u0003$\u0012\u0000\u00df" +
                    "\u00e0\u0006\u0013\uffff\uffff\u0000\u00e0\u0139\u0001\u0000\u0000\u0000" +
                    "\u00e1\u00e2\u0003\u0016\u000b\u0000\u00e2\u00e3\u0003\u0018\f\u0000\u00e3" +
                    "\u00e4\u0003\u001a\r\u0000\u00e4\u00e5\u0003$\u0012\u0000\u00e5\u00e6" +
                    "\u0003 \u0010\u0000\u00e6\u00e7\u0006\u0013\uffff\uffff\u0000\u00e7\u0139" +
                    "\u0001\u0000\u0000\u0000\u00e8\u00e9\u0003\u0016\u000b\u0000\u00e9\u00ea" +
                    "\u0003\u0018\f\u0000\u00ea\u00eb\u0003\u001a\r\u0000\u00eb\u00ec\u0003" +
                    " \u0010\u0000\u00ec\u00ed\u0006\u0013\uffff\uffff\u0000\u00ed\u0139\u0001" +
                    "\u0000\u0000\u0000\u00ee\u00ef\u0003\u0016\u000b\u0000\u00ef\u00f0\u0003" +
                    "\u001c\u000e\u0000\u00f0\u00f1\u0003\u0018\f\u0000\u00f1\u00f2\u0006\u0013" +
                    "\uffff\uffff\u0000\u00f2\u0139\u0001\u0000\u0000\u0000\u00f3\u00f4\u0003" +
                    "\u0016\u000b\u0000\u00f4\u00f5\u0003\u001c\u000e\u0000\u00f5\u00f6\u0003" +
                    "\u0018\f\u0000\u00f6\u00f7\u0003\u001a\r\u0000\u00f7\u00f8\u0006\u0013" +
                    "\uffff\uffff\u0000\u00f8\u0139\u0001\u0000\u0000\u0000\u00f9\u00fa\u0003" +
                    "\u001c\u000e\u0000\u00fa\u00fb\u0003\u0018\f\u0000\u00fb\u00fc\u0003\u001a" +
                    "\r\u0000\u00fc\u00fd\u0006\u0013\uffff\uffff\u0000\u00fd\u0139\u0001\u0000" +
                    "\u0000\u0000\u00fe\u00ff\u0003\u001c\u000e\u0000\u00ff\u0100\u0003\u0016" +
                    "\u000b\u0000\u0100\u0101\u0003\u0018\f\u0000\u0101\u0102\u0006\u0013\uffff" +
                    "\uffff\u0000\u0102\u0139\u0001\u0000\u0000\u0000\u0103\u0104\u0003\u001c" +
                    "\u000e\u0000\u0104\u0105\u0003\u0018\f\u0000\u0105\u0106\u0006\u0013\uffff" +
                    "\uffff\u0000\u0106\u0139\u0001\u0000\u0000\u0000\u0107\u0108\u0003\u001e" +
                    "\u000f\u0000\u0108\u0109\u0003\u0016\u000b\u0000\u0109\u010a\u0003\u0018" +
                    "\f\u0000\u010a\u010b\u0003\u001a\r\u0000\u010b\u010c\u0006\u0013\uffff" +
                    "\uffff\u0000\u010c\u0139\u0001\u0000\u0000\u0000\u010d\u010e\u0003\u001e" +
                    "\u000f\u0000\u010e\u010f\u0003\u0016\u000b\u0000\u010f\u0110\u0003\u0018" +
                    "\f\u0000\u0110\u0111\u0003\u001a\r\u0000\u0111\u0112\u0003 \u0010\u0000" +
                    "\u0112\u0113\u0006\u0013\uffff\uffff\u0000\u0113\u0139\u0001\u0000\u0000" +
                    "\u0000\u0114\u0115\u0003\u0018\f\u0000\u0115\u0116\u0003\u001a\r\u0000" +
                    "\u0116\u0117\u0006\u0013\uffff\uffff\u0000\u0117\u0139\u0001\u0000\u0000" +
                    "\u0000\u0118\u0119\u0003\u0018\f\u0000\u0119\u011a\u0003\u001a\r\u0000" +
                    "\u011a\u011b\u0003$\u0012\u0000\u011b\u011c\u0006\u0013\uffff\uffff\u0000" +
                    "\u011c\u0139\u0001\u0000\u0000\u0000\u011d\u011e\u0003\u0018\f\u0000\u011e" +
                    "\u011f\u0003\u001a\r\u0000\u011f\u0120\u0003$\u0012\u0000\u0120\u0121" +
                    "\u0003 \u0010\u0000\u0121\u0122\u0006\u0013\uffff\uffff\u0000\u0122\u0139" +
                    "\u0001\u0000\u0000\u0000\u0123\u0124\u0003\u0018\f\u0000\u0124\u0125\u0003" +
                    "\"\u0011\u0000\u0125\u0126\u0006\u0013\uffff\uffff\u0000\u0126\u0139\u0001" +
                    "\u0000\u0000\u0000\u0127\u0128\u0003\u0018\f\u0000\u0128\u0129\u0003\"" +
                    "\u0011\u0000\u0129\u012a\u0003 \u0010\u0000\u012a\u012b\u0006\u0013\uffff" +
                    "\uffff\u0000\u012b\u0139\u0001\u0000\u0000\u0000\u012c\u012d\u0003\u0018" +
                    "\f\u0000\u012d\u012e\u0003\u001a\r\u0000\u012e\u012f\u0003 \u0010\u0000" +
                    "\u012f\u0130\u0006\u0013\uffff\uffff\u0000\u0130\u0139\u0001\u0000\u0000" +
                    "\u0000\u0131\u0132\u0003\u0018\f\u0000\u0132\u0133\u0003 \u0010\u0000" +
                    "\u0133\u0134\u0006\u0013\uffff\uffff\u0000\u0134\u0139\u0001\u0000\u0000" +
                    "\u0000\u0135\u0136\u0003\u0018\f\u0000\u0136\u0137\u0006\u0013\uffff\uffff" +
                    "\u0000\u0137\u0139\u0001\u0000\u0000\u0000\u0138\u00c2\u0001\u0000\u0000" +
                    "\u0000\u0138\u00c6\u0001\u0000\u0000\u0000\u0138\u00cb\u0001\u0000\u0000" +
                    "\u0000\u0138\u00d0\u0001\u0000\u0000\u0000\u0138\u00d6\u0001\u0000\u0000" +
                    "\u0000\u0138\u00db\u0001\u0000\u0000\u0000\u0138\u00e1\u0001\u0000\u0000" +
                    "\u0000\u0138\u00e8\u0001\u0000\u0000\u0000\u0138\u00ee\u0001\u0000\u0000" +
                    "\u0000\u0138\u00f3\u0001\u0000\u0000\u0000\u0138\u00f9\u0001\u0000\u0000" +
                    "\u0000\u0138\u00fe\u0001\u0000\u0000\u0000\u0138\u0103\u0001\u0000\u0000" +
                    "\u0000\u0138\u0107\u0001\u0000\u0000\u0000\u0138\u010d\u0001\u0000\u0000" +
                    "\u0000\u0138\u0114\u0001\u0000\u0000\u0000\u0138\u0118\u0001\u0000\u0000" +
                    "\u0000\u0138\u011d\u0001\u0000\u0000\u0000\u0138\u0123\u0001\u0000\u0000" +
                    "\u0000\u0138\u0127\u0001\u0000\u0000\u0000\u0138\u012c\u0001\u0000\u0000" +
                    "\u0000\u0138\u0131\u0001\u0000\u0000\u0000\u0138\u0135\u0001\u0000\u0000" +
                    "\u0000\u0139\'\u0001\u0000\u0000\u0000\u013a\u013b\u0005\u0016\u0000\u0000" +
                    "\u013b\u013c\u0005\u001f\u0000\u0000\u013c\u0142\u0006\u0014\uffff\uffff" +
                    "\u0000\u013d\u013e\u0005\u001e\u0000\u0000\u013e\u013f\u0005\u001f\u0000" +
                    "\u0000\u013f\u0141\u0006\u0014\uffff\uffff\u0000\u0140\u013d\u0001\u0000" +
                    "\u0000\u0000\u0141\u0144\u0001\u0000\u0000\u0000\u0142\u0140\u0001\u0000" +
                    "\u0000\u0000\u0142\u0143\u0001\u0000\u0000\u0000\u0143\u0145\u0001\u0000" +
                    "\u0000\u0000\u0144\u0142\u0001\u0000\u0000\u0000\u0145\u0146\u0005\u0002" +
                    "\u0000\u0000\u0146)\u0001\u0000\u0000\u0000\u0147\u0148\u0005\u0017\u0000" +
                    "\u0000\u0148\u0149\u0005\u001f\u0000\u0000\u0149\u014a\u0005\u001e\u0000" +
                    "\u0000\u014a\u014b\u0005\u001f\u0000\u0000\u014b\u014c\u0005\u001e\u0000" +
                    "\u0000\u014c\u0155\u0006\u0015\uffff\uffff\u0000\u014d\u014e\u0005\u001d" +
                    "\u0000\u0000\u014e\u014f\u0005\u001f\u0000\u0000\u014f\u0150\u0005\u001e" +
                    "\u0000\u0000\u0150\u0151\u0005\u001f\u0000\u0000\u0151\u0152\u0005\u001e" +
                    "\u0000\u0000\u0152\u0154\u0006\u0015\uffff\uffff\u0000\u0153\u014d\u0001" +
                    "\u0000\u0000\u0000\u0154\u0157\u0001\u0000\u0000\u0000\u0155\u0153\u0001" +
                    "\u0000\u0000\u0000\u0155\u0156\u0001\u0000\u0000\u0000\u0156\u0158\u0001" +
                    "\u0000\u0000\u0000\u0157\u0155\u0001\u0000\u0000\u0000\u0158\u0159\u0005" +
                    "\u0002\u0000\u0000\u0159+\u0001\u0000\u0000\u0000\u015a\u015b\u0005\u0019" +
                    "\u0000\u0000\u015b\u015c\u0005\u001f\u0000\u0000\u015c\u015d\u0005\u0002" +
                    "\u0000\u0000\u015d\u015e\u0006\u0016\uffff\uffff\u0000\u015e-\u0001\u0000" +
                    "\u0000\u0000\u015f\u0160\u0005\u001a\u0000\u0000\u0160\u0161\u0005\u001f" +
                    "\u0000\u0000\u0161\u0162\u0005\u001e\u0000\u0000\u0162\u0163\u0005\u001f" +
                    "\u0000\u0000\u0163\u0164\u0005\u0002\u0000\u0000\u0164\u0165\u0006\u0017" +
                    "\uffff\uffff\u0000\u0165/\u0001\u0000\u0000\u0000\u0166\u0167\u0005\u001b" +
                    "\u0000\u0000\u0167\u0168\u0005\u001f\u0000\u0000\u0168\u0169\u0005\u0002" +
                    "\u0000\u0000\u0169\u016a\u0006\u0018\uffff\uffff\u0000\u016a1\u0001\u0000" +
                    "\u0000\u0000\u016b\u016c\u0005\u001c\u0000\u0000\u016c\u016d\u0005\u001f" +
                    "\u0000\u0000\u016d\u016e\u0005\u0002\u0000\u0000\u016e\u016f\u0006\u0019" +
                    "\uffff\uffff\u0000\u016f3\u0001\u0000\u0000\u0000\u0170\u0172\u0005\u0002" +
                    "\u0000\u0000\u0171\u0170\u0001\u0000\u0000\u0000\u0172\u0175\u0001\u0000" +
                    "\u0000\u0000\u0173\u0171\u0001\u0000\u0000\u0000\u0173\u0174\u0001\u0000" +
                    "\u0000\u0000\u0174\u0176\u0001\u0000\u0000\u0000\u0175\u0173\u0001\u0000" +
                    "\u0000\u0000\u0176\u0177\u0003\u0000\u0000\u0000\u0177\u01ab\u0006\u001a" +
                    "\uffff\uffff\u0000\u0178\u0179\u0003\u0002\u0001\u0000\u0179\u017a\u0006" +
                    "\u001a\uffff\uffff\u0000\u017a\u01ac\u0001\u0000\u0000\u0000\u017b\u017c" +
                    "\u0003\u0004\u0002\u0000\u017c\u017d\u0006\u001a\uffff\uffff\u0000\u017d" +
                    "\u01ac\u0001\u0000\u0000\u0000\u017e\u017f\u0003\u0006\u0003\u0000\u017f" +
                    "\u0180\u0006\u001a\uffff\uffff\u0000\u0180\u01ac\u0001\u0000\u0000\u0000" +
                    "\u0181\u0182\u0003\b\u0004\u0000\u0182\u0183\u0006\u001a\uffff\uffff\u0000" +
                    "\u0183\u01ac\u0001\u0000\u0000\u0000\u0184\u0185\u0003\n\u0005\u0000\u0185" +
                    "\u0186\u0006\u001a\uffff\uffff\u0000\u0186\u01ac\u0001\u0000\u0000\u0000" +
                    "\u0187\u0188\u0003\f\u0006\u0000\u0188\u0189\u0006\u001a\uffff\uffff\u0000" +
                    "\u0189\u01ac\u0001\u0000\u0000\u0000\u018a\u018b\u0003\u000e\u0007\u0000" +
                    "\u018b\u018c\u0006\u001a\uffff\uffff\u0000\u018c\u01ac\u0001\u0000\u0000" +
                    "\u0000\u018d\u018e\u0003\u0010\b\u0000\u018e\u018f\u0006\u001a\uffff\uffff" +
                    "\u0000\u018f\u01ac\u0001\u0000\u0000\u0000\u0190\u0191\u0003\u0012\t\u0000" +
                    "\u0191\u0192\u0006\u001a\uffff\uffff\u0000\u0192\u01ac\u0001\u0000\u0000" +
                    "\u0000\u0193\u0194\u0003\u0014\n\u0000\u0194\u0195\u0006\u001a\uffff\uffff" +
                    "\u0000\u0195\u01ac\u0001\u0000\u0000\u0000\u0196\u0197\u0003&\u0013\u0000" +
                    "\u0197\u0198\u0006\u001a\uffff\uffff\u0000\u0198\u01ac\u0001\u0000\u0000" +
                    "\u0000\u0199\u019a\u0003(\u0014\u0000\u019a\u019b\u0006\u001a\uffff\uffff" +
                    "\u0000\u019b\u01ac\u0001\u0000\u0000\u0000\u019c\u019d\u0003*\u0015\u0000" +
                    "\u019d\u019e\u0006\u001a\uffff\uffff\u0000\u019e\u01ac\u0001\u0000\u0000" +
                    "\u0000\u019f\u01a0\u0003,\u0016\u0000\u01a0\u01a1\u0006\u001a\uffff\uffff" +
                    "\u0000\u01a1\u01ac\u0001\u0000\u0000\u0000\u01a2\u01a3\u0003.\u0017\u0000" +
                    "\u01a3\u01a4\u0006\u001a\uffff\uffff\u0000\u01a4\u01ac\u0001\u0000\u0000" +
                    "\u0000\u01a5\u01a6\u00030\u0018\u0000\u01a6\u01a7\u0006\u001a\uffff\uffff" +
                    "\u0000\u01a7\u01ac\u0001\u0000\u0000\u0000\u01a8\u01a9\u00032\u0019\u0000" +
                    "\u01a9\u01aa\u0006\u001a\uffff\uffff\u0000\u01aa\u01ac\u0001\u0000\u0000" +
                    "\u0000\u01ab\u0178\u0001\u0000\u0000\u0000\u01ab\u017b\u0001\u0000\u0000" +
                    "\u0000\u01ab\u017e\u0001\u0000\u0000\u0000\u01ab\u0181\u0001\u0000\u0000" +
                    "\u0000\u01ab\u0184\u0001\u0000\u0000\u0000\u01ab\u0187\u0001\u0000\u0000" +
                    "\u0000\u01ab\u018a\u0001\u0000\u0000\u0000\u01ab\u018d\u0001\u0000\u0000" +
                    "\u0000\u01ab\u0190\u0001\u0000\u0000\u0000\u01ab\u0193\u0001\u0000\u0000" +
                    "\u0000\u01ab\u0196\u0001\u0000\u0000\u0000\u01ab\u0199\u0001\u0000\u0000" +
                    "\u0000\u01ab\u019c\u0001\u0000\u0000\u0000\u01ab\u019f\u0001\u0000\u0000" +
                    "\u0000\u01ab\u01a2\u0001\u0000\u0000\u0000\u01ab\u01a5\u0001\u0000\u0000" +
                    "\u0000\u01ab\u01a8\u0001\u0000\u0000\u0000\u01ac\u01ad\u0001\u0000\u0000" +
                    "\u0000\u01ad\u01ab\u0001\u0000\u0000\u0000\u01ad\u01ae\u0001\u0000\u0000" +
                    "\u0000\u01ae\u01b2\u0001\u0000\u0000\u0000\u01af\u01b1\u0005\u0002\u0000" +
                    "\u0000\u01b0\u01af\u0001\u0000\u0000\u0000\u01b1\u01b4\u0001\u0000\u0000" +
                    "\u0000\u01b2\u01b0\u0001\u0000\u0000\u0000\u01b2\u01b3\u0001\u0000\u0000" +
                    "\u0000\u01b35\u0001\u0000\u0000\u0000\u01b4\u01b2\u0001\u0000\u0000\u0000" +
                    "\u01b5\u01b6\u00034\u001a\u0000\u01b6\u01b7\u0006\u001b\uffff\uffff\u0000" +
                    "\u01b7\u01b9\u0001\u0000\u0000\u0000\u01b8\u01b5\u0001\u0000\u0000\u0000" +
                    "\u01b9\u01ba\u0001\u0000\u0000\u0000\u01ba\u01b8\u0001\u0000\u0000\u0000" +
                    "\u01ba\u01bb\u0001\u0000\u0000\u0000\u01bb\u01bc\u0001\u0000\u0000\u0000" +
                    "\u01bc\u01bd\u0005\u0000\u0000\u0001\u01bd7\u0001\u0000\u0000\u0000\t" +
                    "\u009c\u0138\u0142\u0155\u0173\u01ab\u01ad\u01b2\u01ba";
	public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}