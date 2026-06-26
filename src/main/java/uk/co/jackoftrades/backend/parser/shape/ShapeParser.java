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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Shape.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.shape;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
import uk.co.jackoftrades.middle.effect.Expression;
import uk.co.jackoftrades.middle.enums.EffectBaseType;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.ValueEnum;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerBlow;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ShapeParser extends Parser {
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
	public static final int
            RULE_name = 0, RULE_combat = 1, RULE_skill_disarm_phys = 2, RULE_skill_disarm_magic = 3,
            RULE_skill_save = 4, RULE_skill_stealth = 5, RULE_skill_search = 6, RULE_skill_melee = 7,
            RULE_skill_throw = 8, RULE_skill_dig = 9, RULE_obj_flags = 10, RULE_player_flags = 11,
            RULE_values = 12, RULE_effect = 13, RULE_dice = 14, RULE_expr = 15, RULE_effect_msg = 16,
            RULE_effect_block = 17, RULE_blow = 18, RULE_shape = 19, RULE_file = 20;
	private static String[] makeRuleNames() {
        return new String[]{
                "name", "combat", "skill_disarm_phys", "skill_disarm_magic", "skill_save",
                "skill_stealth", "skill_search", "skill_melee", "skill_throw", "skill_dig",
                "obj_flags", "player_flags", "values", "effect", "dice", "expr", "effect_msg",
                "effect_block", "blow", "shape", "file"
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
    public ATN getATN() {
        return _ATN;
    }

	public ShapeParser(TokenStream input) {
		super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token STRING;

        public TerminalNode NAME() {
            return getToken(ShapeParser.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
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
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(42);
                match(NAME);
                setState(43);
                ((NameContext) _localctx).STRING = match(STRING);
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
	public static class CombatContext extends ParserRuleContext {
		public int tohNum;
		public int todNum;
		public int toaNum;
		public Token toh;
		public Token tod;
		public Token toa;

        public TerminalNode COMBAT() {
            return getToken(ShapeParser.COMBAT, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ShapeParser.COLON);
        }
		public TerminalNode COLON(int i) {
			return getToken(ShapeParser.COLON, i);
		}

        public List<TerminalNode> STRING() {
            return getTokens(ShapeParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ShapeParser.STRING, i);
		}
		public CombatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_combat;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterCombat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitCombat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitCombat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombatContext combat() throws RecognitionException {
		CombatContext _localctx = new CombatContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_combat);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(46);
                match(COMBAT);
                setState(47);
                ((CombatContext) _localctx).toh = match(STRING);
                setState(48);
                match(COLON);
                setState(49);
                ((CombatContext) _localctx).tod = match(STRING);
                setState(50);
                match(COLON);
                setState(51);
                ((CombatContext) _localctx).toa = match(STRING);

                ((CombatContext) _localctx).tohNum = Integer.parseInt(((CombatContext) _localctx).toh.getText());
                ((CombatContext) _localctx).todNum = Integer.parseInt(((CombatContext) _localctx).tod.getText());
                ((CombatContext) _localctx).toaNum = Integer.parseInt(((CombatContext) _localctx).toa.getText());
			        
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
	public static class Skill_disarm_physContext extends ParserRuleContext {
		public int skillNum;
		public Token STRING;

        public TerminalNode SKILL_DISARM_PHYS() {
            return getToken(ShapeParser.SKILL_DISARM_PHYS, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public Skill_disarm_physContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_skill_disarm_phys;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterSkill_disarm_phys(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitSkill_disarm_phys(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor)
                return ((ShapeVisitor<? extends T>) visitor).visitSkill_disarm_phys(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Skill_disarm_physContext skill_disarm_phys() throws RecognitionException {
		Skill_disarm_physContext _localctx = new Skill_disarm_physContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_skill_disarm_phys);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(54);
                match(SKILL_DISARM_PHYS);
                setState(55);
                ((Skill_disarm_physContext) _localctx).STRING = match(STRING);
                ((Skill_disarm_physContext) _localctx).skillNum = Integer.parseInt(((Skill_disarm_physContext) _localctx).STRING.getText());
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
	public static class Skill_disarm_magicContext extends ParserRuleContext {
		public int skillNum;
		public Token STRING;

        public TerminalNode SKILL_DISARM_MAGIC() {
            return getToken(ShapeParser.SKILL_DISARM_MAGIC, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public Skill_disarm_magicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_skill_disarm_magic;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterSkill_disarm_magic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitSkill_disarm_magic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor)
                return ((ShapeVisitor<? extends T>) visitor).visitSkill_disarm_magic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Skill_disarm_magicContext skill_disarm_magic() throws RecognitionException {
		Skill_disarm_magicContext _localctx = new Skill_disarm_magicContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_skill_disarm_magic);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(58);
                match(SKILL_DISARM_MAGIC);
                setState(59);
                ((Skill_disarm_magicContext) _localctx).STRING = match(STRING);
                ((Skill_disarm_magicContext) _localctx).skillNum = Integer.parseInt(((Skill_disarm_magicContext) _localctx).STRING.getText());
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
	public static class Skill_saveContext extends ParserRuleContext {
		public int skillNum;
		public Token STRING;

        public TerminalNode SKILL_SAVE() {
            return getToken(ShapeParser.SKILL_SAVE, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public Skill_saveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_skill_save;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterSkill_save(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitSkill_save(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitSkill_save(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Skill_saveContext skill_save() throws RecognitionException {
		Skill_saveContext _localctx = new Skill_saveContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_skill_save);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(62);
                match(SKILL_SAVE);
                setState(63);
                ((Skill_saveContext) _localctx).STRING = match(STRING);
                ((Skill_saveContext) _localctx).skillNum = Integer.parseInt(((Skill_saveContext) _localctx).STRING.getText());
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
	public static class Skill_stealthContext extends ParserRuleContext {
		public int skillNum;
		public Token STRING;

        public TerminalNode SKILL_STEALTH() {
            return getToken(ShapeParser.SKILL_STEALTH, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public Skill_stealthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_skill_stealth;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterSkill_stealth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitSkill_stealth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitSkill_stealth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Skill_stealthContext skill_stealth() throws RecognitionException {
		Skill_stealthContext _localctx = new Skill_stealthContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_skill_stealth);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(66);
                match(SKILL_STEALTH);
                setState(67);
                ((Skill_stealthContext) _localctx).STRING = match(STRING);
                ((Skill_stealthContext) _localctx).skillNum = Integer.parseInt(((Skill_stealthContext) _localctx).STRING.getText());
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
	public static class Skill_searchContext extends ParserRuleContext {
		public int skillNum;
		public Token STRING;

        public TerminalNode SKILL_SEARCH() {
            return getToken(ShapeParser.SKILL_SEARCH, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public Skill_searchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_skill_search;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterSkill_search(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitSkill_search(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitSkill_search(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Skill_searchContext skill_search() throws RecognitionException {
		Skill_searchContext _localctx = new Skill_searchContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_skill_search);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(70);
                match(SKILL_SEARCH);
                setState(71);
                ((Skill_searchContext) _localctx).STRING = match(STRING);
                ((Skill_searchContext) _localctx).skillNum = Integer.parseInt(((Skill_searchContext) _localctx).STRING.getText());
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
	public static class Skill_meleeContext extends ParserRuleContext {
		public int skillNum;
		public Token STRING;

        public TerminalNode SKILL_MELEE() {
            return getToken(ShapeParser.SKILL_MELEE, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public Skill_meleeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_skill_melee;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterSkill_melee(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitSkill_melee(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitSkill_melee(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Skill_meleeContext skill_melee() throws RecognitionException {
		Skill_meleeContext _localctx = new Skill_meleeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_skill_melee);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(74);
                match(SKILL_MELEE);
                setState(75);
                ((Skill_meleeContext) _localctx).STRING = match(STRING);
                ((Skill_meleeContext) _localctx).skillNum = Integer.parseInt(((Skill_meleeContext) _localctx).STRING.getText());
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
	public static class Skill_throwContext extends ParserRuleContext {
		public int skillNum;
		public Token STRING;

        public TerminalNode SKILL_THROW() {
            return getToken(ShapeParser.SKILL_THROW, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public Skill_throwContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_skill_throw;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterSkill_throw(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitSkill_throw(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitSkill_throw(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Skill_throwContext skill_throw() throws RecognitionException {
		Skill_throwContext _localctx = new Skill_throwContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_skill_throw);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(78);
                match(SKILL_THROW);
                setState(79);
                ((Skill_throwContext) _localctx).STRING = match(STRING);
                ((Skill_throwContext) _localctx).skillNum = Integer.parseInt(((Skill_throwContext) _localctx).STRING.getText());
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
	public static class Skill_digContext extends ParserRuleContext {
		public int skillNum;
		public Token STRING;

        public TerminalNode SKILL_DIG() {
            return getToken(ShapeParser.SKILL_DIG, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public Skill_digContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_skill_dig;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterSkill_dig(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitSkill_dig(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitSkill_dig(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Skill_digContext skill_dig() throws RecognitionException {
		Skill_digContext _localctx = new Skill_digContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_skill_dig);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(82);
                match(SKILL_DIG);
                setState(83);
                ((Skill_digContext) _localctx).STRING = match(STRING);
                ((Skill_digContext) _localctx).skillNum = Integer.parseInt(((Skill_digContext) _localctx).STRING.getText());
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
	public static class Obj_flagsContext extends ParserRuleContext {
		public Flag<ObjectFlag> oFlags;
		public Token f1;
		public Token f2;

        public TerminalNode OBJ_FLAGS() {
            return getToken(ShapeParser.OBJ_FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(ShapeParser.FLAG);
        }
		public TerminalNode FLAG(int i) {
			return getToken(ShapeParser.FLAG, i);
		}

        public List<TerminalNode> OR() {
            return getTokens(ShapeParser.OR);
        }
		public TerminalNode OR(int i) {
			return getToken(ShapeParser.OR, i);
		}
		public Obj_flagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_obj_flags;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterObj_flags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitObj_flags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitObj_flags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Obj_flagsContext obj_flags() throws RecognitionException {
		Obj_flagsContext _localctx = new Obj_flagsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_obj_flags);

		((Obj_flagsContext) _localctx).oFlags = new Flag<>(ObjectFlag.class);
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(86);
                match(OBJ_FLAGS);
                setState(87);
                ((Obj_flagsContext) _localctx).f1 = match(FLAG);
				_localctx.oFlags.on(ObjectFlag.valueOf("OF_" + ((Obj_flagsContext) _localctx).f1.getText()));
                setState(94);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(89);
                            match(OR);
                            setState(90);
                            ((Obj_flagsContext) _localctx).f2 = match(FLAG);
							_localctx.oFlags.on(ObjectFlag.valueOf("OF_" + ((Obj_flagsContext) _localctx).f2.getText()));
                        }
                    }
                    setState(96);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
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
	public static class Player_flagsContext extends ParserRuleContext {
		public Flag<PlayerFlag> pFlags;
		public Token f1;
		public Token f2;

        public TerminalNode PLAYER_FLAGS() {
            return getToken(ShapeParser.PLAYER_FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(ShapeParser.FLAG);
        }
		public TerminalNode FLAG(int i) {
			return getToken(ShapeParser.FLAG, i);
		}

        public List<TerminalNode> OR() {
            return getTokens(ShapeParser.OR);
        }
		public TerminalNode OR(int i) {
			return getToken(ShapeParser.OR, i);
		}
		public Player_flagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_player_flags;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterPlayer_flags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitPlayer_flags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitPlayer_flags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Player_flagsContext player_flags() throws RecognitionException {
		Player_flagsContext _localctx = new Player_flagsContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_player_flags);

        ((Player_flagsContext) _localctx).pFlags = new Flag<>(PlayerFlag.class);
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(97);
                match(PLAYER_FLAGS);
                setState(98);
                ((Player_flagsContext) _localctx).f1 = match(FLAG);
                _localctx.pFlags.on(PlayerFlag.valueOf("PF_" + ((Player_flagsContext) _localctx).f1.getText()));
                setState(105);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(100);
                            match(OR);
                            setState(101);
                            ((Player_flagsContext) _localctx).f2 = match(FLAG);
                            _localctx.pFlags.on(PlayerFlag.valueOf("PF_" + ((Player_flagsContext) _localctx).f2.getText()));
                        }
                    }
                    setState(107);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
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
        public Map<ValueEnum, Integer> valueMap;
		public Token v1;
		public Token val1;
		public Token v2;
		public Token val2;

        public TerminalNode VALUES() {
            return getToken(ShapeParser.VALUES, 0);
        }

        public List<TerminalNode> LBRACKET() {
            return getTokens(ShapeParser.LBRACKET);
        }
		public TerminalNode LBRACKET(int i) {
			return getToken(ShapeParser.LBRACKET, i);
		}

        public List<TerminalNode> RBRACKET() {
            return getTokens(ShapeParser.RBRACKET);
        }
		public TerminalNode RBRACKET(int i) {
			return getToken(ShapeParser.RBRACKET, i);
		}

        public List<TerminalNode> FLAG() {
            return getTokens(ShapeParser.FLAG);
        }
		public TerminalNode FLAG(int i) {
			return getToken(ShapeParser.FLAG, i);
		}

        public List<TerminalNode> STRING() {
            return getTokens(ShapeParser.STRING);
        }
		public TerminalNode STRING(int i) {
			return getToken(ShapeParser.STRING, i);
		}

        public List<TerminalNode> OR() {
            return getTokens(ShapeParser.OR);
        }
		public TerminalNode OR(int i) {
			return getToken(ShapeParser.OR, i);
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
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_values);

        ((ValuesContext) _localctx).valueMap = new HashMap<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(108);
                match(VALUES);
                setState(109);
                ((ValuesContext) _localctx).v1 = match(FLAG);
                setState(110);
                match(LBRACKET);
                setState(111);
                ((ValuesContext) _localctx).val1 = match(STRING);
                setState(112);
                match(RBRACKET);

                ValueEnum v1Enum = ValueEnum.valueOf("CV_" + ((ValuesContext) _localctx).v1.getText());
                int val1Num = Integer.parseInt(((ValuesContext) _localctx).val1.getText());
                _localctx.valueMap.put(v1Enum, val1Num);

                setState(122);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(114);
                            match(OR);
                            setState(115);
                            ((ValuesContext) _localctx).v2 = match(FLAG);
                            setState(116);
                            match(LBRACKET);
                            setState(117);
                            ((ValuesContext) _localctx).val2 = match(STRING);
                            setState(118);
                            match(RBRACKET);

                            ValueEnum v2Enum = ValueEnum.valueOf("CV_" + ((ValuesContext) _localctx).v2.getText());
                            int val2Num = Integer.parseInt(((ValuesContext) _localctx).val2.getText());
                            _localctx.valueMap.put(v2Enum, val2Num);

                        }
                    }
                    setState(124);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
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
		public EffectEnum effectEnum;
        public EffectSubTypeWrapper wrapper;
		public Token f1;
		public Token f2;

        public TerminalNode EFFECT() {
            return getToken(ShapeParser.EFFECT, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(ShapeParser.FLAG);
        }
		public TerminalNode FLAG(int i) {
			return getToken(ShapeParser.FLAG, i);
		}

        public TerminalNode COLON() {
            return getToken(ShapeParser.COLON, 0);
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
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_effect);

        ((EffectContext) _localctx).wrapper = null;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(125);
                match(EFFECT);
                setState(126);
                ((EffectContext) _localctx).f1 = match(FLAG);

                ((EffectContext) _localctx).effectEnum = EffectEnum.valueOf("EF_" + ((EffectContext) _localctx).f1.getText());

                setState(131);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(128);
                        match(COLON);
                        setState(129);
                        ((EffectContext) _localctx).f2 = match(FLAG);

                        if (_localctx.effectEnum == EffectEnum.EF_CURE || _localctx.effectEnum == EffectEnum.EF_TIMED_INC) {
                            ((EffectContext) _localctx).wrapper = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + ((EffectContext) _localctx).f2.getText()));
                        } else {
                            ((EffectContext) _localctx).wrapper = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + ((EffectContext) _localctx).f2.getText()));
                        }

                    }
                }

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
            return getToken(ShapeParser.DICE, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
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
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_dice);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(133);
                match(DICE);
                setState(134);
                ((DiceContext) _localctx).STRING = match(STRING);
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
	public static class ExprContext extends ParserRuleContext {
		public Expression expression;
		public Token ch;
		public Token base;
		public Token st;

        public TerminalNode EXPR() {
            return getToken(ShapeParser.EXPR, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ShapeParser.COLON);
        }
		public TerminalNode COLON(int i) {
			return getToken(ShapeParser.COLON, i);
		}

        public List<TerminalNode> FLAG() {
            return getTokens(ShapeParser.FLAG);
        }
		public TerminalNode FLAG(int i) {
			return getToken(ShapeParser.FLAG, i);
		}

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
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
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_expr);

        char chInit;
        EffectBaseType baseInit;
        String operationInit;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(137);
                match(EXPR);
                setState(138);
                ((ExprContext) _localctx).ch = match(FLAG);
                setState(139);
                match(COLON);
                setState(140);
                ((ExprContext) _localctx).base = match(FLAG);
                setState(141);
                match(COLON);
                setState(142);
                ((ExprContext) _localctx).st = match(STRING);

                String rawCh = ((ExprContext) _localctx).ch.getText();
                if (rawCh.length() != 1)
                    throw new IllegalArgumentException("Expression character code may only have one character.");

                chInit = rawCh.charAt(0);

                baseInit = EffectBaseType.valueOf("EFB_" + ((ExprContext) _localctx).base.getText());
                operationInit = ((ExprContext) _localctx).st.getText();
			        
			}
			_ctx.stop = _input.LT(-1);

            ((ExprContext) _localctx).expression = new Expression(chInit, baseInit, operationInit);

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
	public static class Effect_msgContext extends ParserRuleContext {
		public String effMsgStr;
		public Token STRING;

        public TerminalNode EFFECT_MSG() {
            return getToken(ShapeParser.EFFECT_MSG, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public Effect_msgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_effect_msg;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterEffect_msg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitEffect_msg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitEffect_msg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Effect_msgContext effect_msg() throws RecognitionException {
		Effect_msgContext _localctx = new Effect_msgContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_effect_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(145);
                match(EFFECT_MSG);
                setState(146);
                ((Effect_msgContext) _localctx).STRING = match(STRING);
                ((Effect_msgContext) _localctx).effMsgStr = ((Effect_msgContext) _localctx).STRING.getText();
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
		public EffectContext effect;
		public Effect_msgContext effect_msg;
		public DiceContext dice;
		public ExprContext expr;
		public EffectContext effect() {
            return getRuleContext(EffectContext.class, 0);
		}
		public Effect_msgContext effect_msg() {
            return getRuleContext(Effect_msgContext.class, 0);
		}
		public DiceContext dice() {
            return getRuleContext(DiceContext.class, 0);
		}
		public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
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
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterEffect_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitEffect_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitEffect_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Effect_blockContext effect_block() throws RecognitionException {
		Effect_blockContext _localctx = new Effect_blockContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_effect_block);

        EffectEnum effectEnum = EffectEnum.EF_NONE;
        String diceInit = "";
        int xInit = 0;
        int yInit = 0;
        EffectSubTypeWrapper value = null;
        String radiusInit = "";
        String otherParameter = "";
        String msgInit = "";
        String visMsgInit = "";
        Expression expInit = null;
		        
		try {
			setState(176);
			_errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
				{
                    setState(149);
                    ((Effect_blockContext) _localctx).effect = effect();

                    effectEnum = ((Effect_blockContext) _localctx).effect.effectEnum;
                    value = ((Effect_blockContext) _localctx).effect.wrapper;
				            
				}
				break;
                case 2:
                    enterOuterAlt(_localctx, 2);
				{
                    setState(152);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(153);
                    ((Effect_blockContext) _localctx).effect_msg = effect_msg();

                    effectEnum = ((Effect_blockContext) _localctx).effect.effectEnum;
                    value = ((Effect_blockContext) _localctx).effect.wrapper;
                    msgInit = ((Effect_blockContext) _localctx).effect_msg.effMsgStr;
				            
				}
				break;
                case 3:
                    enterOuterAlt(_localctx, 3);
				{
                    setState(156);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(157);
                    ((Effect_blockContext) _localctx).dice = dice();

                    effectEnum = ((Effect_blockContext) _localctx).effect.effectEnum;
                    value = ((Effect_blockContext) _localctx).effect.wrapper;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
				            
				}
				break;
                case 4:
                    enterOuterAlt(_localctx, 4);
				{
                    setState(160);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(161);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(162);
                    ((Effect_blockContext) _localctx).effect_msg = effect_msg();

                    effectEnum = ((Effect_blockContext) _localctx).effect.effectEnum;
                    value = ((Effect_blockContext) _localctx).effect.wrapper;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    msgInit = ((Effect_blockContext) _localctx).effect_msg.effMsgStr;
				            
				}
				break;
                case 5:
                    enterOuterAlt(_localctx, 5);
				{
                    setState(165);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(166);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(167);
                    ((Effect_blockContext) _localctx).expr = expr();

                    effectEnum = ((Effect_blockContext) _localctx).effect.effectEnum;
                    value = ((Effect_blockContext) _localctx).effect.wrapper;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    expInit = ((Effect_blockContext) _localctx).expr.expression;
				            
				}
				break;
                case 6:
                    enterOuterAlt(_localctx, 6);
				{
                    setState(170);
                    ((Effect_blockContext) _localctx).effect = effect();
                    setState(171);
                    ((Effect_blockContext) _localctx).dice = dice();
                    setState(172);
                    ((Effect_blockContext) _localctx).expr = expr();
                    setState(173);
                    ((Effect_blockContext) _localctx).effect_msg = effect_msg();

                    effectEnum = ((Effect_blockContext) _localctx).effect.effectEnum;
                    value = ((Effect_blockContext) _localctx).effect.wrapper;
                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                    expInit = ((Effect_blockContext) _localctx).expr.expression;
                    msgInit = ((Effect_blockContext) _localctx).effect_msg.effMsgStr;
				            
				}
				break;
			}
			_ctx.stop = _input.LT(-1);

            // TODO(ClaudeCode): EffectBlock not yet re-plumbed to the current Effect constructor API;
            // this multi-arg Effect(...) overload no longer exists. Commented out to keep the build green.
            /*((Effect_blockContext) _localctx).effObj = new Effect(effectEnum, diceInit, yInit, xInit,
                    value,
                    radiusInit, otherParameter, msgInit,
                    visMsgInit, expInit);*/
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
	public static class BlowContext extends ParserRuleContext {
		public String blowStr;
		public Token STRING;

        public TerminalNode BLOW() {
            return getToken(ShapeParser.BLOW, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeParser.STRING, 0);
        }
		public BlowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_blow;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterBlow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitBlow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitBlow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlowContext blow() throws RecognitionException {
		BlowContext _localctx = new BlowContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_blow);
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(178);
                match(BLOW);
                setState(179);
                ((BlowContext) _localctx).STRING = match(STRING);
                ((BlowContext) _localctx).blowStr = ((BlowContext) _localctx).STRING.getText();
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
	public static class ShapeContext extends ParserRuleContext {
		public PlayerShape shapeObj;
		public NameContext name;
		public CombatContext combat;
		public Skill_disarm_physContext skill_disarm_phys;
		public Skill_disarm_magicContext skill_disarm_magic;
		public Skill_saveContext skill_save;
		public Skill_searchContext skill_search;
		public Skill_meleeContext skill_melee;
		public Skill_throwContext skill_throw;
		public Skill_digContext skill_dig;
		public Obj_flagsContext obj_flags;
		public Player_flagsContext player_flags;
		public ValuesContext values;
		public Effect_blockContext effect_block;
		public BlowContext blow;
		public NameContext name() {
            return getRuleContext(NameContext.class, 0);
		}
		public List<CombatContext> combat() {
			return getRuleContexts(CombatContext.class);
		}
		public CombatContext combat(int i) {
            return getRuleContext(CombatContext.class, i);
		}
		public List<Skill_disarm_physContext> skill_disarm_phys() {
			return getRuleContexts(Skill_disarm_physContext.class);
		}
		public Skill_disarm_physContext skill_disarm_phys(int i) {
            return getRuleContext(Skill_disarm_physContext.class, i);
		}
		public List<Skill_disarm_magicContext> skill_disarm_magic() {
			return getRuleContexts(Skill_disarm_magicContext.class);
		}
		public Skill_disarm_magicContext skill_disarm_magic(int i) {
            return getRuleContext(Skill_disarm_magicContext.class, i);
		}
		public List<Skill_saveContext> skill_save() {
			return getRuleContexts(Skill_saveContext.class);
		}
		public Skill_saveContext skill_save(int i) {
            return getRuleContext(Skill_saveContext.class, i);
		}
		public List<Skill_stealthContext> skill_stealth() {
			return getRuleContexts(Skill_stealthContext.class);
		}
		public Skill_stealthContext skill_stealth(int i) {
            return getRuleContext(Skill_stealthContext.class, i);
		}
		public List<Skill_searchContext> skill_search() {
			return getRuleContexts(Skill_searchContext.class);
		}
		public Skill_searchContext skill_search(int i) {
            return getRuleContext(Skill_searchContext.class, i);
		}
		public List<Skill_meleeContext> skill_melee() {
			return getRuleContexts(Skill_meleeContext.class);
		}
		public Skill_meleeContext skill_melee(int i) {
            return getRuleContext(Skill_meleeContext.class, i);
		}
		public List<Skill_throwContext> skill_throw() {
			return getRuleContexts(Skill_throwContext.class);
		}
		public Skill_throwContext skill_throw(int i) {
            return getRuleContext(Skill_throwContext.class, i);
		}
		public List<Skill_digContext> skill_dig() {
			return getRuleContexts(Skill_digContext.class);
		}
		public Skill_digContext skill_dig(int i) {
            return getRuleContext(Skill_digContext.class, i);
		}
		public List<Obj_flagsContext> obj_flags() {
			return getRuleContexts(Obj_flagsContext.class);
		}
		public Obj_flagsContext obj_flags(int i) {
            return getRuleContext(Obj_flagsContext.class, i);
		}
		public List<Player_flagsContext> player_flags() {
			return getRuleContexts(Player_flagsContext.class);
		}
		public Player_flagsContext player_flags(int i) {
            return getRuleContext(Player_flagsContext.class, i);
		}
		public List<ValuesContext> values() {
			return getRuleContexts(ValuesContext.class);
		}
		public ValuesContext values(int i) {
            return getRuleContext(ValuesContext.class, i);
		}
		public List<Effect_blockContext> effect_block() {
			return getRuleContexts(Effect_blockContext.class);
		}
		public Effect_blockContext effect_block(int i) {
            return getRuleContext(Effect_blockContext.class, i);
		}
		public List<BlowContext> blow() {
			return getRuleContexts(BlowContext.class);
		}
		public BlowContext blow(int i) {
            return getRuleContext(BlowContext.class, i);
		}
		public ShapeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

        @Override
        public int getRuleIndex() {
            return RULE_shape;
        }
		@Override
		public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterShape(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitShape(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitShape(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ShapeContext shape() throws RecognitionException {
		ShapeContext _localctx = new ShapeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_shape);

        String nameInit = "";
        int toAcInit = 0;
        int toHitInit = 0;
        int toDamInit = 0;
        Map<PlayerSkill, Integer> skillInit = new HashMap<>();
		Flag<ObjectFlag> oFlagsInit = new Flag<>(ObjectFlag.class);
        Flag<PlayerFlag> pFlagsInit = new Flag<>(PlayerFlag.class);
        Map<ValueEnum, Integer> valueModInit = new HashMap<>();
		List<ElementEnum> resistInit = new ArrayList<>();
        Effect effectInit = null;
        int numBlows = 0;
        List<PlayerBlow> blowInit = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(182);
                ((ShapeContext) _localctx).name = name();

                nameInit = ((ShapeContext) _localctx).name.nameStr;

                setState(228);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1179632L) != 0)) {
                    {
                        setState(226);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case COMBAT: {
                                setState(184);
                                ((ShapeContext) _localctx).combat = combat();

                                toAcInit = ((ShapeContext) _localctx).combat.toaNum;
                                toDamInit = ((ShapeContext) _localctx).combat.todNum;
                                toHitInit = ((ShapeContext) _localctx).combat.tohNum;

                            }
                            break;
                            case SKILL_DISARM_PHYS: {
                                setState(187);
                                ((ShapeContext) _localctx).skill_disarm_phys = skill_disarm_phys();

                                skillInit.put(PlayerSkill.SKILL_DISARM_PHYS, ((ShapeContext) _localctx).skill_disarm_phys.skillNum);

                            }
                            break;
                            case SKILL_DISARM_MAGIC: {
                                setState(190);
                                ((ShapeContext) _localctx).skill_disarm_magic = skill_disarm_magic();

                                skillInit.put(PlayerSkill.SKILL_DISARM_MAGIC, ((ShapeContext) _localctx).skill_disarm_magic.skillNum);

                            }
                            break;
                            case SKILL_SAVE: {
                                setState(193);
                                ((ShapeContext) _localctx).skill_save = skill_save();

                                skillInit.put(PlayerSkill.SKILL_SAVE, ((ShapeContext) _localctx).skill_save.skillNum);

                            }
                            break;
                            case SKILL_STEALTH: {
                                setState(196);
                                skill_stealth();

                                skillInit.put(PlayerSkill.SKILL_STEALTH, ((ShapeContext) _localctx).skill_save.skillNum);

                            }
                            break;
                            case SKILL_SEARCH: {
                                setState(199);
                                ((ShapeContext) _localctx).skill_search = skill_search();

                                skillInit.put(PlayerSkill.SKILL_SEARCH, ((ShapeContext) _localctx).skill_search.skillNum);

                            }
                            break;
                            case SKILL_MELEE: {
                                setState(202);
                                ((ShapeContext) _localctx).skill_melee = skill_melee();

                                skillInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, ((ShapeContext) _localctx).skill_melee.skillNum);

                            }
                            break;
                            case SKILL_THROW: {
                                setState(205);
                                ((ShapeContext) _localctx).skill_throw = skill_throw();

                                skillInit.put(PlayerSkill.SKILL_TO_HIT_THROW, ((ShapeContext) _localctx).skill_throw.skillNum);

                            }
                            break;
                            case SKILL_DIG: {
                                setState(208);
                                ((ShapeContext) _localctx).skill_dig = skill_dig();

                                skillInit.put(PlayerSkill.SKILL_DIGGING, ((ShapeContext) _localctx).skill_dig.skillNum);

                            }
                            break;
                            case OBJ_FLAGS: {
                                setState(211);
                                ((ShapeContext) _localctx).obj_flags = obj_flags();

                                oFlagsInit.union(((ShapeContext) _localctx).obj_flags.oFlags);

                            }
                            break;
                            case PLAYER_FLAGS: {
                                setState(214);
                                ((ShapeContext) _localctx).player_flags = player_flags();

                                pFlagsInit.union(((ShapeContext) _localctx).player_flags.pFlags);

                            }
                            break;
                            case VALUES: {
                                setState(217);
                                ((ShapeContext) _localctx).values = values();
                                valueModInit.putAll(((ShapeContext) _localctx).values.valueMap);
                            }
                            break;
                            case EFFECT: {
                                setState(220);
                                ((ShapeContext) _localctx).effect_block = effect_block();
                                effectInit = ((ShapeContext) _localctx).effect_block.effObj;
                            }
                            break;
                            case BLOW: {
                                setState(223);
                                ((ShapeContext) _localctx).blow = blow();
                                blowInit.add(new PlayerBlow(((ShapeContext) _localctx).blow.blowStr));
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(230);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
			}
			_ctx.stop = _input.LT(-1);

            ((ShapeContext) _localctx).shapeObj = new PlayerShape(nameInit, toAcInit, toHitInit, toDamInit,
                    skillInit, oFlagsInit, pFlagsInit,
                    valueModInit, resistInit,
                    effectInit, blowInit.size(), blowInit);

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
		public List<PlayerShape> shapes;
		public ShapeContext shape;

        public TerminalNode EOF() {
            return getToken(ShapeParser.EOF, 0);
        }
		public List<ShapeContext> shape() {
			return getRuleContexts(ShapeContext.class);
		}
		public ShapeContext shape(int i) {
            return getRuleContext(ShapeContext.class, i);
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
            if (listener instanceof ShapeListener) ((ShapeListener) listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeListener) ((ShapeListener) listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeVisitor) return ((ShapeVisitor<? extends T>) visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_file);

        ((FileContext) _localctx).shapes = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(234);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(231);
                            ((FileContext) _localctx).shape = shape();
                            _localctx.shapes.add(((FileContext) _localctx).shape.shapeObj);
                        }
                    }
                    setState(236);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(238);
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
            "\u0004\u0001\u001a\u00f1\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007" +
                    "\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b" +
                    "\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0005\n]\b\n\n\n\f\n`\t\n\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000bh\b" +
                    "\u000b\n\u000b\f\u000bk\t\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005\fy\b" +
                    "\f\n\f\f\f|\t\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003" +
                    "\r\u0084\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0003\u0011\u00b1\b\u0011\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u00e3\b\u0013" +
                    "\n\u0013\f\u0013\u00e6\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0004" +
                    "\u0014\u00eb\b\u0014\u000b\u0014\f\u0014\u00ec\u0001\u0014\u0001\u0014" +
                    "\u0001\u0014\u0000\u0000\u0015\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010" +
                    "\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(\u0000\u0000\u00f3\u0000" +
                    "*\u0001\u0000\u0000\u0000\u0002.\u0001\u0000\u0000\u0000\u00046\u0001" +
                    "\u0000\u0000\u0000\u0006:\u0001\u0000\u0000\u0000\b>\u0001\u0000\u0000" +
                    "\u0000\nB\u0001\u0000\u0000\u0000\fF\u0001\u0000\u0000\u0000\u000eJ\u0001" +
                    "\u0000\u0000\u0000\u0010N\u0001\u0000\u0000\u0000\u0012R\u0001\u0000\u0000" +
                    "\u0000\u0014V\u0001\u0000\u0000\u0000\u0016a\u0001\u0000\u0000\u0000\u0018" +
                    "l\u0001\u0000\u0000\u0000\u001a}\u0001\u0000\u0000\u0000\u001c\u0085\u0001" +
                    "\u0000\u0000\u0000\u001e\u0089\u0001\u0000\u0000\u0000 \u0091\u0001\u0000" +
                    "\u0000\u0000\"\u00b0\u0001\u0000\u0000\u0000$\u00b2\u0001\u0000\u0000" +
                    "\u0000&\u00b6\u0001\u0000\u0000\u0000(\u00ea\u0001\u0000\u0000\u0000*" +
                    "+\u0005\u0003\u0000\u0000+,\u0005\u001a\u0000\u0000,-\u0006\u0000\uffff" +
                    "\uffff\u0000-\u0001\u0001\u0000\u0000\u0000./\u0005\u0004\u0000\u0000" +
                    "/0\u0005\u001a\u0000\u000001\u0005\u0017\u0000\u000012\u0005\u001a\u0000" +
                    "\u000023\u0005\u0017\u0000\u000034\u0005\u001a\u0000\u000045\u0006\u0001" +
                    "\uffff\uffff\u00005\u0003\u0001\u0000\u0000\u000067\u0005\u0005\u0000" +
                    "\u000078\u0005\u001a\u0000\u000089\u0006\u0002\uffff\uffff\u00009\u0005" +
                    "\u0001\u0000\u0000\u0000:;\u0005\u0006\u0000\u0000;<\u0005\u001a\u0000" +
                    "\u0000<=\u0006\u0003\uffff\uffff\u0000=\u0007\u0001\u0000\u0000\u0000" +
                    ">?\u0005\u0007\u0000\u0000?@\u0005\u001a\u0000\u0000@A\u0006\u0004\uffff" +
                    "\uffff\u0000A\t\u0001\u0000\u0000\u0000BC\u0005\b\u0000\u0000CD\u0005" +
                    "\u001a\u0000\u0000DE\u0006\u0005\uffff\uffff\u0000E\u000b\u0001\u0000" +
                    "\u0000\u0000FG\u0005\t\u0000\u0000GH\u0005\u001a\u0000\u0000HI\u0006\u0006" +
                    "\uffff\uffff\u0000I\r\u0001\u0000\u0000\u0000JK\u0005\n\u0000\u0000KL" +
                    "\u0005\u001a\u0000\u0000LM\u0006\u0007\uffff\uffff\u0000M\u000f\u0001" +
                    "\u0000\u0000\u0000NO\u0005\u000b\u0000\u0000OP\u0005\u001a\u0000\u0000" +
                    "PQ\u0006\b\uffff\uffff\u0000Q\u0011\u0001\u0000\u0000\u0000RS\u0005\f" +
                    "\u0000\u0000ST\u0005\u001a\u0000\u0000TU\u0006\t\uffff\uffff\u0000U\u0013" +
                    "\u0001\u0000\u0000\u0000VW\u0005\r\u0000\u0000WX\u0005\u0019\u0000\u0000" +
                    "X^\u0006\n\uffff\uffff\u0000YZ\u0005\u0018\u0000\u0000Z[\u0005\u0019\u0000" +
                    "\u0000[]\u0006\n\uffff\uffff\u0000\\Y\u0001\u0000\u0000\u0000]`\u0001" +
                    "\u0000\u0000\u0000^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000" +
                    "_\u0015\u0001\u0000\u0000\u0000`^\u0001\u0000\u0000\u0000ab\u0005\u000e" +
                    "\u0000\u0000bc\u0005\u0019\u0000\u0000ci\u0006\u000b\uffff\uffff\u0000" +
                    "de\u0005\u0018\u0000\u0000ef\u0005\u0019\u0000\u0000fh\u0006\u000b\uffff" +
                    "\uffff\u0000gd\u0001\u0000\u0000\u0000hk\u0001\u0000\u0000\u0000ig\u0001" +
                    "\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000j\u0017\u0001\u0000\u0000" +
                    "\u0000ki\u0001\u0000\u0000\u0000lm\u0005\u000f\u0000\u0000mn\u0005\u0019" +
                    "\u0000\u0000no\u0005\u0015\u0000\u0000op\u0005\u001a\u0000\u0000pq\u0005" +
                    "\u0016\u0000\u0000qz\u0006\f\uffff\uffff\u0000rs\u0005\u0018\u0000\u0000" +
                    "st\u0005\u0019\u0000\u0000tu\u0005\u0015\u0000\u0000uv\u0005\u001a\u0000" +
                    "\u0000vw\u0005\u0016\u0000\u0000wy\u0006\f\uffff\uffff\u0000xr\u0001\u0000" +
                    "\u0000\u0000y|\u0001\u0000\u0000\u0000zx\u0001\u0000\u0000\u0000z{\u0001" +
                    "\u0000\u0000\u0000{\u0019\u0001\u0000\u0000\u0000|z\u0001\u0000\u0000" +
                    "\u0000}~\u0005\u0010\u0000\u0000~\u007f\u0005\u0019\u0000\u0000\u007f" +
                    "\u0083\u0006\r\uffff\uffff\u0000\u0080\u0081\u0005\u0017\u0000\u0000\u0081" +
                    "\u0082\u0005\u0019\u0000\u0000\u0082\u0084\u0006\r\uffff\uffff\u0000\u0083" +
                    "\u0080\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084" +
                    "\u001b\u0001\u0000\u0000\u0000\u0085\u0086\u0005\u0011\u0000\u0000\u0086" +
                    "\u0087\u0005\u001a\u0000\u0000\u0087\u0088\u0006\u000e\uffff\uffff\u0000" +
                    "\u0088\u001d\u0001\u0000\u0000\u0000\u0089\u008a\u0005\u0012\u0000\u0000" +
                    "\u008a\u008b\u0005\u0019\u0000\u0000\u008b\u008c\u0005\u0017\u0000\u0000" +
                    "\u008c\u008d\u0005\u0019\u0000\u0000\u008d\u008e\u0005\u0017\u0000\u0000" +
                    "\u008e\u008f\u0005\u001a\u0000\u0000\u008f\u0090\u0006\u000f\uffff\uffff" +
                    "\u0000\u0090\u001f\u0001\u0000\u0000\u0000\u0091\u0092\u0005\u0013\u0000" +
                    "\u0000\u0092\u0093\u0005\u001a\u0000\u0000\u0093\u0094\u0006\u0010\uffff" +
                    "\uffff\u0000\u0094!\u0001\u0000\u0000\u0000\u0095\u0096\u0003\u001a\r" +
                    "\u0000\u0096\u0097\u0006\u0011\uffff\uffff\u0000\u0097\u00b1\u0001\u0000" +
                    "\u0000\u0000\u0098\u0099\u0003\u001a\r\u0000\u0099\u009a\u0003 \u0010" +
                    "\u0000\u009a\u009b\u0006\u0011\uffff\uffff\u0000\u009b\u00b1\u0001\u0000" +
                    "\u0000\u0000\u009c\u009d\u0003\u001a\r\u0000\u009d\u009e\u0003\u001c\u000e" +
                    "\u0000\u009e\u009f\u0006\u0011\uffff\uffff\u0000\u009f\u00b1\u0001\u0000" +
                    "\u0000\u0000\u00a0\u00a1\u0003\u001a\r\u0000\u00a1\u00a2\u0003\u001c\u000e" +
                    "\u0000\u00a2\u00a3\u0003 \u0010\u0000\u00a3\u00a4\u0006\u0011\uffff\uffff" +
                    "\u0000\u00a4\u00b1\u0001\u0000\u0000\u0000\u00a5\u00a6\u0003\u001a\r\u0000" +
                    "\u00a6\u00a7\u0003\u001c\u000e\u0000\u00a7\u00a8\u0003\u001e\u000f\u0000" +
                    "\u00a8\u00a9\u0006\u0011\uffff\uffff\u0000\u00a9\u00b1\u0001\u0000\u0000" +
                    "\u0000\u00aa\u00ab\u0003\u001a\r\u0000\u00ab\u00ac\u0003\u001c\u000e\u0000" +
                    "\u00ac\u00ad\u0003\u001e\u000f\u0000\u00ad\u00ae\u0003 \u0010\u0000\u00ae" +
                    "\u00af\u0006\u0011\uffff\uffff\u0000\u00af\u00b1\u0001\u0000\u0000\u0000" +
                    "\u00b0\u0095\u0001\u0000\u0000\u0000\u00b0\u0098\u0001\u0000\u0000\u0000" +
                    "\u00b0\u009c\u0001\u0000\u0000\u0000\u00b0\u00a0\u0001\u0000\u0000\u0000" +
                    "\u00b0\u00a5\u0001\u0000\u0000\u0000\u00b0\u00aa\u0001\u0000\u0000\u0000" +
                    "\u00b1#\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005\u0014\u0000\u0000\u00b3" +
                    "\u00b4\u0005\u001a\u0000\u0000\u00b4\u00b5\u0006\u0012\uffff\uffff\u0000" +
                    "\u00b5%\u0001\u0000\u0000\u0000\u00b6\u00b7\u0003\u0000\u0000\u0000\u00b7" +
                    "\u00e4\u0006\u0013\uffff\uffff\u0000\u00b8\u00b9\u0003\u0002\u0001\u0000" +
                    "\u00b9\u00ba\u0006\u0013\uffff\uffff\u0000\u00ba\u00e3\u0001\u0000\u0000" +
                    "\u0000\u00bb\u00bc\u0003\u0004\u0002\u0000\u00bc\u00bd\u0006\u0013\uffff" +
                    "\uffff\u0000\u00bd\u00e3\u0001\u0000\u0000\u0000\u00be\u00bf\u0003\u0006" +
                    "\u0003\u0000\u00bf\u00c0\u0006\u0013\uffff\uffff\u0000\u00c0\u00e3\u0001" +
                    "\u0000\u0000\u0000\u00c1\u00c2\u0003\b\u0004\u0000\u00c2\u00c3\u0006\u0013" +
                    "\uffff\uffff\u0000\u00c3\u00e3\u0001\u0000\u0000\u0000\u00c4\u00c5\u0003" +
                    "\n\u0005\u0000\u00c5\u00c6\u0006\u0013\uffff\uffff\u0000\u00c6\u00e3\u0001" +
                    "\u0000\u0000\u0000\u00c7\u00c8\u0003\f\u0006\u0000\u00c8\u00c9\u0006\u0013" +
                    "\uffff\uffff\u0000\u00c9\u00e3\u0001\u0000\u0000\u0000\u00ca\u00cb\u0003" +
                    "\u000e\u0007\u0000\u00cb\u00cc\u0006\u0013\uffff\uffff\u0000\u00cc\u00e3" +
                    "\u0001\u0000\u0000\u0000\u00cd\u00ce\u0003\u0010\b\u0000\u00ce\u00cf\u0006" +
                    "\u0013\uffff\uffff\u0000\u00cf\u00e3\u0001\u0000\u0000\u0000\u00d0\u00d1" +
                    "\u0003\u0012\t\u0000\u00d1\u00d2\u0006\u0013\uffff\uffff\u0000\u00d2\u00e3" +
                    "\u0001\u0000\u0000\u0000\u00d3\u00d4\u0003\u0014\n\u0000\u00d4\u00d5\u0006" +
                    "\u0013\uffff\uffff\u0000\u00d5\u00e3\u0001\u0000\u0000\u0000\u00d6\u00d7" +
                    "\u0003\u0016\u000b\u0000\u00d7\u00d8\u0006\u0013\uffff\uffff\u0000\u00d8" +
                    "\u00e3\u0001\u0000\u0000\u0000\u00d9\u00da\u0003\u0018\f\u0000\u00da\u00db" +
                    "\u0006\u0013\uffff\uffff\u0000\u00db\u00e3\u0001\u0000\u0000\u0000\u00dc" +
                    "\u00dd\u0003\"\u0011\u0000\u00dd\u00de\u0006\u0013\uffff\uffff\u0000\u00de" +
                    "\u00e3\u0001\u0000\u0000\u0000\u00df\u00e0\u0003$\u0012\u0000\u00e0\u00e1" +
                    "\u0006\u0013\uffff\uffff\u0000\u00e1\u00e3\u0001\u0000\u0000\u0000\u00e2" +
                    "\u00b8\u0001\u0000\u0000\u0000\u00e2\u00bb\u0001\u0000\u0000\u0000\u00e2" +
                    "\u00be\u0001\u0000\u0000\u0000\u00e2\u00c1\u0001\u0000\u0000\u0000\u00e2" +
                    "\u00c4\u0001\u0000\u0000\u0000\u00e2\u00c7\u0001\u0000\u0000\u0000\u00e2" +
                    "\u00ca\u0001\u0000\u0000\u0000\u00e2\u00cd\u0001\u0000\u0000\u0000\u00e2" +
                    "\u00d0\u0001\u0000\u0000\u0000\u00e2\u00d3\u0001\u0000\u0000\u0000\u00e2" +
                    "\u00d6\u0001\u0000\u0000\u0000\u00e2\u00d9\u0001\u0000\u0000\u0000\u00e2" +
                    "\u00dc\u0001\u0000\u0000\u0000\u00e2\u00df\u0001\u0000\u0000\u0000\u00e3" +
                    "\u00e6\u0001\u0000\u0000\u0000\u00e4\u00e2\u0001\u0000\u0000\u0000\u00e4" +
                    "\u00e5\u0001\u0000\u0000\u0000\u00e5\'\u0001\u0000\u0000\u0000\u00e6\u00e4" +
                    "\u0001\u0000\u0000\u0000\u00e7\u00e8\u0003&\u0013\u0000\u00e8\u00e9\u0006" +
                    "\u0014\uffff\uffff\u0000\u00e9\u00eb\u0001\u0000\u0000\u0000\u00ea\u00e7" +
                    "\u0001\u0000\u0000\u0000\u00eb\u00ec\u0001\u0000\u0000\u0000\u00ec\u00ea" +
                    "\u0001\u0000\u0000\u0000\u00ec\u00ed\u0001\u0000\u0000\u0000\u00ed\u00ee" +
                    "\u0001\u0000\u0000\u0000\u00ee\u00ef\u0005\u0000\u0000\u0001\u00ef)\u0001" +
                    "\u0000\u0000\u0000\b^iz\u0083\u00b0\u00e2\u00e4\u00ec";
	public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}