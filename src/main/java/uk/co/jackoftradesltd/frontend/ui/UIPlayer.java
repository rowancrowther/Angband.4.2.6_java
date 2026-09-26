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

package uk.co.jackoftradesltd.frontend.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.globals.ChannelRegistry;
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryIterator;
import uk.co.jackoftradesltd.frontend.globals.UIGlobals;
import uk.co.jackoftradesltd.frontend.screen.Term;
import uk.co.jackoftradesltd.frontend.screen.TermData;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.frontend.ui.output.Region;
import uk.co.jackoftradesltd.frontend.ui.player.CharSheetConfig;
import uk.co.jackoftradesltd.frontend.ui.player.CharSheetResist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Character-sheet display driver — the partial Java port of C's {@code ui-player.c} ("character
 * screens and dumps").
 *
 * <p>Owns the resistance-panel layout cache ({@link #cachedConfig}) built by
 * {@link #configureCharSheet()}, reused across draws while {@link #haveValidCharSheetConfig()}
 * still holds, and consulted by {@link #displayPlayer(PlayerDisplayMode)} before each draw.
 *
 * <p>Also owns the five-entry {@link #panels} dispatch table built once by the constructor,
 * pairing each of the top-left/misc/mid-left/combat/skills panels with its screen
 * {@link Region} and builder — the Java form of C's file-scope {@code panels[]} table in
 * {@code ui-player.c}.
 *
 * <p>Class UIPlayer coded before 260925, commented in full on 260925.
 *
 * @author Rowan Crowther
 */
public class UIPlayer {
    /**
     * One-shot latch guarding {@link #spc}'s initialisation inside {@link #getUIEntryLabel}, mirroring
     * C's function-static {@code first_call} inside {@code get_ui_entry_label}
     * ({@code [C] ui-entry.c:342,347-352}). Set once and never reset, so {@link #spc} is only ever
     * populated on the first call across the life of the JVM.
     *
     * <p>Field firstCallOfGetEntryLabel coded before 260925, commented in full on 260925.
     */
    private static boolean firstCallOfGetEntryLabel = true;
    /**
     * The two-character space/terminator pair set on the first call to {@link #getUIEntryLabel}, the
     * Java form of C's function-static {@code wchar_t spc[2]} ({@code [C] ui-entry.c:343,348}):
     * {@code spc[0]} the padding space, {@code spc[1]} the terminating null. {@link #getUIEntryLabel}
     * pads with a literal {@code " "} instead of reading this array, so it is set but never consulted.
     *
     * <p>Field spc coded before 260925, commented in full on 260925.
     */
    private static char[] spc = new char[2];

    private static ColourEnum[] colourTable = new ColourEnum[]{ColourEnum.COLOUR_RED,
            ColourEnum.COLOUR_RED, ColourEnum.COLOUR_RED, ColourEnum.COLOUR_LIGHT_RED,
            ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW, ColourEnum.COLOUR_YELLOW,
            ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_LIGHT_GREEN,
            ColourEnum.COLOUR_LIGHT_BLUE};

    /**
     * The five character-sheet panel builders paired with their screen regions, built once by
     * the constructor and read by the (not yet ported) {@code display_panel} renderer — the
     * Java form of C's file-scope {@code panels[]} table in {@code ui-player.c}.
     *
     * <p>Field panels coded on 260925, commented in full on 260925.
     */
    private PanelRegions[] panels;

    /**
     * The cached resistance-panel layout for the current player, built by
     * {@link #configureCharSheet()} and read back by {@link #haveValidCharSheetConfig()} on every
     * draw — the Java form of C's function-static {@code cached_config}
     * ({@code [C] ui-player.c:142}).
     *
     * <p>{@code null} until the first {@link #configureCharSheet()} call, mirroring C's
     * {@code static struct char_sheet_config *cached_config = NULL;} initialisation. Once built,
     * it is reused across draws for as long as {@link #haveValidCharSheetConfig()} reports the
     * layout still matches the player's current body shape; when it does not, a fresh
     * {@link CharSheetConfig} replaces this field outright rather than being mutated in place.
     *
     * <p>Field cachedConfig coded before 260925, commented in full on 260925.
     */
    private CharSheetConfig cachedConfig = null;

    /**
     * The {@link TermData} this instance draws its character sheet onto, set once via
     * {@link #setTermData(TermData)}. Read by {@link #displayPlayer(PlayerDisplayMode)} for its
     * screen-clear and its {@link UIGlobals#getActiveTermData()} comparison, and by
     * {@link #displayPlayerStatInfo()} to reach the {@link Term} its {@code cPutStr}/{@code putStr}
     * calls draw with. C carries the equivalent state globally rather than per-instance, since
     * {@code display_player_stat_info} ({@code [C] ui-player.c:450-510}) draws to the single active
     * terminal directly rather than through a field.
     *
     * <p>Field termData coded before 260925, commented in full on 260925.
     */
    private TermData termData;

    /**
     * Builds the five character-sheet panel/region pairings, in the same order as C's
     * file-scope {@code panels[]} table in {@code ui-player.c}: top-left
     * (name/class/title/HP/SP), misc (age/height/weight/etc.), mid-left, combat, and skills.
     * Each {@link PanelRegions} pairs a {@link Region} (position and size) and a
     * flush-left/flush-right alignment flag with a method reference standing in for C's
     * {@code get_panel_*} function pointer, both read straight off C's brace-initialised rows.
     * Stores the five in {@link #panels}, the Java form of the same file-scope array.
     *
     * <p>Constructor UIPlayer coded on 260925, commented in full on 260925.
     */
    public UIPlayer() {
        Region region1 = new Region(1, 1, 40, 7);
        Region region2 = new Region(21, 1, 18, 3);
        Region region3 = new Region(1, 9, 24, 9);
        Region region4 = new Region(29, 9, 19, 9);
        Region region5 = new Region(52, 9, 20, 8);
        PanelRegions pr1 = new PanelRegions(region1, true, this::getPanelTopLeft);
        PanelRegions pr2 = new PanelRegions(region2, false, this::getPanelMisc);
        PanelRegions pr3 = new PanelRegions(region3, false, this::getPanelMidLeft);
        PanelRegions pr4 = new PanelRegions(region4, false, this::getPanelCombat);
        PanelRegions pr5 = new PanelRegions(region5, false, this::getPanelSkills);

        panels = new PanelRegions[]{pr1, pr2, pr3, pr4, pr5};
    }

    /**
     * Tests whether a UI entry belongs to both of the two categories named in {@code closure} —
     * the port of C's {@code check_for_two_categories} ({@code [C] ui-player.c:176-183}), used as
     * the iterator predicate throughout {@link #configureCharSheet()}.
     *
     * <p>A {@code null} entry is treated as not matching rather than passed on to
     * {@link UIEntry#uiEntryHasCategory(String)}, mirroring the fact that C's
     * {@code ui_entry_has_category} is never called with a null {@code entry} either — the
     * iterator only ever offers real entries to its predicate.
     *
     * <p>{@code closure} carries the two category names to test, in the same order on every call:
     * {@code closure[0]} is always {@code "CHAR_SCREEN1"} across every {@link #configureCharSheet()}
     * use, while {@code closure[1]} changes per call — {@code "stat_modifiers"} for the
     * stat-modifier pass, then in turn each of {@code "resistances"}, {@code "abilities"},
     * {@code "hindrances"} and {@code "modifiers"} for the four resistance-panel regions.
     *
     * <p>Method checkForTwoCategories coded before 260925, commented in full on 260925.
     *
     * @param closure a two-element array of category names to test against
     * @param entry   the UI entry to test, or {@code null}
     * @return {@code true} if {@code entry} is non-null and belongs to both named categories
     */
    private static boolean checkForTwoCategories(String[] closure,
                                                 UIEntry entry) {
        if (entry == null) return false;

        Optional<Integer> res1 = entry.uiEntryHasCategory(closure[0]);
        Optional<Integer> res2 = entry.uiEntryHasCategory(closure[1]);

        return (res1.isPresent() && res2.isPresent());
    }

    /**
     * Sets the {@link TermData} this instance draws its character sheet onto. C has no
     * equivalent setter, since {@code display_player_stat_info} and its neighbours write
     * straight to the active terminal rather than through a stored handle; see {@link #termData}
     * for why this instance carries one anyway.
     *
     * <p>Method setTermData coded before 260925, commented in full on 260925.
     *
     * @param termData the terminal this instance's character-sheet draws target from now on
     */
    public void setTermData(TermData termData) {
        this.termData = termData;
    }

    /**
     * Builds an empty {@link Panel} with room for {@code size} rows — the Java form of C's
     * {@code panel_allocate} ({@code ui-player.c}, function {@code panel_allocate}), which
     * {@code mem_zalloc}s a {@code struct panel} and then sets {@code len} to zero, {@code max} to
     * its {@code n} argument, and {@code lines} to a freshly {@code mem_zalloc}'d, {@code max}-element
     * array. This port sets {@link Panel#len} and {@link Panel#max} the same way, field for field,
     * but initialises {@link Panel#lines} to an empty, growable {@link ArrayList} rather than a
     * pre-sized, zero-filled C array — a difference that doesn't yet matter, since neither C's
     * {@code panel_line}/{@code panel_space} row-fillers nor a Java equivalent are ported.
     *
     * <p>Called once per panel by each of the five {@code getPanelX} builders that reach this
     * far — {@link #getPanelTopLeft()} and {@link #getPanelMidLeft()} both call it, and both now
     * go on to completely fill and return the {@link Panel} they get back; {@link #getPanelMisc()},
     * {@link #getPanelCombat()} and {@link #getPanelSkills()} are still {@code TODO} stubs that
     * return {@code null} without calling this method at all. C's
     * five {@code get_panel_topleft}/{@code get_panel_midleft}/{@code get_panel_combat}/
     * {@code get_panel_skills}/{@code get_panel_misc} functions all open with their own
     * {@code panel_allocate} call.
     *
     * <p>Method panelAllocate coded before 260925, commented in full on 260925.
     *
     * @param size the panel's fixed row capacity, the Java form of C's {@code n} argument
     * @return a freshly built {@link Panel} with {@link Panel#len} zero and {@link Panel#max} set to
     * {@code size}
     */
    private Panel panelAllocate(int size) {
        Panel panel = new Panel();
        panel.setLen(0);
        panel.setMax(size);
        panel.initLines();

        return panel;
    }

    /**
     * Builds the top-left character-sheet panel — name, race, class, title, HP and SP — the port
     * of C's {@code get_panel_topleft} ({@code [C] ui-player.c}, function {@code get_panel_topleft}).
     *
     * <p>Allocates a six-row {@link Panel} via {@link #panelAllocate(int)} and fills it, in the
     * same order as C, via six {@link Panel#panelLine(ColourEnum, String, String, Object...)}
     * calls, all in {@link ColourEnum#COLOUR_LIGHT_BLUE} matching C's {@code COLOUR_L_BLUE}: Name,
     * Race, Class and Title each a plain {@code "%s"} of
     * {@link PlayerEventStatusUpdate#getPlayerStatusView()}'s {@code name()}/{@code raceName()}/
     * {@code className()}/{@code title()}, standing in for C's {@code player->full_name}/
     * {@code player->race->name}/{@code player->class->name}/{@code show_title()}; HP and SP each
     * a {@code "%d/%d"} of the same view's current/maximum hit-point and spell-point pair,
     * standing in for C's {@code player->chp}/{@code mhp} and {@code csp}/{@code msp}.
     *
     * <p>The {@code topLeft.initLines()} call partway down this method re-runs the same
     * initialisation {@link #panelAllocate(int)} already performed just above; it replaces one
     * empty {@link Panel#lines} list with another, so it has no effect on the panel this method
     * returns.
     *
     * <p>Of the five {@code getPanelX} builders, this is the only one that both allocates and
     * completely fills its {@link Panel} before returning it. Called from the constructor as
     * {@code pr1}'s builder and, at draw time, by {@link #displayPlayer(PlayerDisplayMode)} via
     * {@link PanelRegions#getPanel()} for {@link PlayerDisplayMode#DISPLAY_FULL}; the returned
     * {@link Panel} is not yet drawn, since the {@code display_panel} renderer
     * ({@code [C] ui-player.c}, function {@code display_panel}) that would read it is not ported
     * yet.
     *
     * <p>Method getPanelTopLeft coded on 260925, commented in full on 260925.
     *
     * @return a freshly built, fully populated six-row {@link Panel} for the top-left name/
     * race/class/title/HP/SP block
     */
    private Panel getPanelTopLeft() {
        Panel topLeft = panelAllocate(6);

        topLeft.initLines();
        topLeft.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "Name", "%s",
                PlayerEventStatusUpdate.getPlayerStatusView().name());
        topLeft.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "Race", "%s",
                PlayerEventStatusUpdate.getPlayerStatusView().raceName());
        topLeft.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "Class", "%s",
                PlayerEventStatusUpdate.getPlayerStatusView().className());
        topLeft.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "Title", "%s",
                PlayerEventStatusUpdate.getPlayerStatusView().title());
        topLeft.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "HP", "%d/%d",
                PlayerEventStatusUpdate.getPlayerStatusView().chp(),
                PlayerEventStatusUpdate.getPlayerStatusView().mhp());
        topLeft.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "SP", "%d/%d",
                PlayerEventStatusUpdate.getPlayerStatusView().csp(),
                PlayerEventStatusUpdate.getPlayerStatusView().msp());

        return topLeft;
    }

    /**
     * Builds the misc character-sheet panel — age, height, weight and turn counts — the port of
     * C's {@code get_panel_misc} ({@code [C] ui-player.c}, function {@code get_panel_misc}).
     *
     * <p>Allocates a seven-row {@link Panel} via {@link #panelAllocate(int)} and fills it, in the
     * same order as C, via seven {@link Panel#panelLine(ColourEnum, String, String, Object...)}
     * calls, all in {@link ColourEnum#COLOUR_LIGHT_BLUE} matching C's {@code COLOUR_L_BLUE}: Age a
     * plain {@code "%d"} of {@link PlayerEventStatusUpdate#getPlayerCharSheetView()}'s
     * {@code age()}, standing in for C's {@code player->age}; Height a {@code "%d'%d\""} of that
     * same view's {@code height()} split into feet and inches ({@code height / 12} and
     * {@code height % 12}), matching C's {@code player->ht / 12}/{@code player->ht % 12} split and
     * its trailing literal inch mark; Weight a {@code "%dst %dlb"} of {@code weight()} split into
     * stone and pounds ({@code weight / 14} and {@code weight % 14}), matching C's
     * {@code player->wt / 14}/{@code player->wt % 14}; a label-only "Turns used:" spacer row with
     * no value; Game a plain {@code "%d"} of {@link PlayerEventStatusUpdate#getPlayerStatusView()}'s
     * {@code turn()}, standing in for C's global {@code turn}; Standard a plain {@code "%d"} of that
     * same view's {@code totalEnergy()} divided by 100, matching C's
     * {@code player->total_energy / 100}; and Resting a plain {@code "%d"} of
     * {@code restingTurn()}, matching C's {@code player->resting_turn}.
     *
     * <p>Of the five {@code getPanelX} builders, this is now the third, alongside
     * {@link #getPanelTopLeft()} and {@link #getPanelMidLeft()}, that both allocates and completely
     * fills its {@link Panel} before returning it — {@link #getPanelCombat()} and
     * {@link #getPanelSkills()} remain {@code TODO} stubs. Called from the constructor as
     * {@code pr2}'s builder; the returned {@link Panel} is not yet drawn, since the
     * {@code display_panel} renderer ({@code [C] ui-player.c}, function {@code display_panel})
     * that would read it is not ported yet.
     *
     * <p>Method getPanelMisc coded on 260926, commented in full on 260926.
     *
     * @return a freshly built, fully populated seven-row {@link Panel} for the age/height/weight/
     * turn-count block
     */
    private Panel getPanelMisc() {
        Panel misc = panelAllocate(7);
        ColourEnum attr = ColourEnum.COLOUR_LIGHT_BLUE;

        int height = PlayerEventStatusUpdate.getPlayerCharSheetView().height();
        int weight = PlayerEventStatusUpdate.getPlayerCharSheetView().weight();

        misc.initLines();
        misc.panelLine(attr, "Age", "%d",
                PlayerEventStatusUpdate.getPlayerCharSheetView().age());
        misc.panelLine(attr, "Height", "%d'%d\"", height / 12, height % 12);
        misc.panelLine(attr, "Weight", "%dst %dlb", weight / 14, weight % 14);
        misc.panelLine(attr, "Turns used:", "");
        misc.panelLine(attr, "Game", "%d",
                PlayerEventStatusUpdate.getPlayerStatusView().turn());
        misc.panelLine(attr, "Standard", "%d",
                PlayerEventStatusUpdate.getPlayerStatusView().totalEnergy() / 100);
        misc.panelLine(attr, "Resting", "%d",
                PlayerEventStatusUpdate.getPlayerStatusView().restingTurn());

        return misc;
    }

    /**
     * Builds the mid-left character-sheet panel — level, experience, gold, encumbrance and
     * dungeon depth — the port of C's {@code get_panel_midleft} ({@code [C] ui-player.c},
     * function {@code get_panel_midleft}).
     *
     * <p>Allocates a nine-row {@link Panel} via {@link #panelAllocate(int)}, computes
     * {@code diff} via {@link #weightRemaining()} and an {@code attr} colour from it
     * ({@link ColourEnum#COLOUR_LIGHT_RED} when {@code diff} is negative,
     * {@link ColourEnum#COLOUR_LIGHT_GREEN} otherwise), matching C's own
     * {@code weight_remaining(player)} call and {@code diff < 0} colour choice. Fills the panel
     * in the same order as C's nine {@code panel_line}/{@code panel_space} calls: Level and Cur
     * Exp (each coloured via {@link #maxColour(int, int)}/{@link #maxColour(long, long)} against
     * the player's recorded maximum), Max Exp, Adv Exp (via {@link #showAdvanceExperience()}), a
     * blank spacer row (via {@link Panel#space()}), Gold, Burden ({@code attr}-coloured, a
     * {@code "%.1f lb"} of {@link PlayerEventStatusUpdate#getPlayerCharSheetView()}'s
     * {@code totalWeight()} divided by ten), Overweight ({@code attr}-coloured, {@code diff}
     * split into whole and tenths via {@code -diff / 10} and {@code Math.abs(diff) % 10}), and
     * Max Depth (via {@link #showDepth()}).
     *
     * <p>Of the five {@code getPanelX} builders, this is now the second, alongside
     * {@link #getPanelTopLeft()}, that both allocates and completely fills its {@link Panel}
     * before returning it — {@link #getPanelMisc()}, {@link #getPanelCombat()} and
     * {@link #getPanelSkills()} remain {@code TODO} stubs. Called from the constructor as
     * {@code pr3}'s builder; the returned {@link Panel} is not yet drawn, since the
     * {@code display_panel} renderer ({@code [C] ui-player.c}, function {@code display_panel})
     * that would read it is not ported yet.
     *
     * <p>Method getPanelMidLeft coded before 260925, commented in full on 260926.
     *
     * @return a freshly built, fully populated nine-row {@link Panel} for the mid-left
     * level/experience/gold/encumbrance/depth block
     */
    private Panel getPanelMidLeft() {
        Panel midLeft = panelAllocate(9);
        int diff = weightRemaining();
        ColourEnum attr = diff < 0 ? ColourEnum.COLOUR_LIGHT_RED : ColourEnum.COLOUR_LIGHT_GREEN;

        midLeft.initLines();
        midLeft.panelLine(maxColour(PlayerEventStatusUpdate.getPlayerStatusView().level(),
                        PlayerEventStatusUpdate.getPlayerStatusView().maxLevel()), "Level", "%d",
                PlayerEventStatusUpdate.getPlayerStatusView().level());
        midLeft.panelLine(maxColour(PlayerEventStatusUpdate.getPlayerStatusView().experience(),
                        PlayerEventStatusUpdate.getPlayerStatusView().maxExperience()), "Cur Exp", "%d",
                PlayerEventStatusUpdate.getPlayerStatusView().experience());
        midLeft.panelLine(ColourEnum.COLOUR_LIGHT_GREEN, "Max Exp", "%d",
                PlayerEventStatusUpdate.getPlayerStatusView().maxExperience());
        midLeft.panelLine(ColourEnum.COLOUR_LIGHT_GREEN, "Adv Exp", "%s",
                showAdvanceExperience());
        midLeft.space();
        midLeft.panelLine(ColourEnum.COLOUR_LIGHT_GREEN, "Gold", "%d",
                PlayerEventStatusUpdate.getPlayerStatusView().gold());
        midLeft.panelLine(attr, "Burden", "%.1f lb",
                PlayerEventStatusUpdate.getPlayerCharSheetView().totalWeight() / 10.0F);
        midLeft.panelLine(attr, "Overweight", "%d.%d lb", -diff / 10,
                Math.abs(diff) % 10);
        midLeft.panelLine(ColourEnum.COLOUR_LIGHT_GREEN, "Max Depth", "%s", showDepth());

        return midLeft;
    }

    /**
     * Builds the "Max Depth" panel value — the port of C's {@code show_depth} ({@code [C]
     * ui-player.c}, function {@code show_depth}).
     *
     * <p>A recorded maximum depth of zero returns the literal {@code "Town"}, matching C's own
     * {@code player->max_depth == 0} early return. Otherwise returns a {@code "%d' (L%d)"} of the
     * depth in feet ({@code depth * 50}) and the depth in dungeon levels ({@code depth}), reading
     * {@link PlayerEventStatusUpdate#getPlayerStatusView()}'s {@code maxDepth()} in place of C's
     * direct {@code player->max_depth} field read.
     *
     * <p>Called from {@link #getPanelMidLeft()} to build the mid-left panel's "Max Depth" row.
     *
     * <p>Method showDepth coded before 260925, commented in full on 260926.
     *
     * @return the formatted depth figure, or {@code "Town"} at depth zero
     */
    private String showDepth() {
        int depth = PlayerEventStatusUpdate.getPlayerStatusView().maxDepth();
        if (depth == 0)
            return "Town";

        return String.format("%d' (L%d)", depth * 50, depth);
    }

    /**
     * Builds the "Adv Exp" panel value — the port of C's {@code show_adv_exp}
     * ({@code ui-player.c}, function {@code show_adv_exp}).
     *
     * <p>Below the level cap ({@link ChannelRegistry#getPYMaxLevel()}), returns the experience
     * still needed to reach the next level: the {@link PlayerEventStatusUpdate#getPlayerCharSheetView()}
     * {@code expToLevel()} table entry for the player's current level, indexed at
     * {@code level() - 1} to line a one-based {@link PlayerEventStatusUpdate#getPlayerStatusView()}
     * {@code level()} up with C's zero-based {@code player_exp[player->lev - 1]} lookup, scaled by
     * {@code expFactor()} and divided by 100, matching C's own
     * {@code player_exp[player->lev - 1] * player->expfact / 100L}. At or past the cap, returns the
     * literal {@code "********"} placeholder, matching C's cap branch exactly.
     *
     * <p>Called from {@link #getPanelMidLeft()} to build the mid-left panel's "Adv Exp" row.
     *
     * <p>Method showAdvanceExperience coded before 260925, commented in full on 260926.
     *
     * @return the formatted experience-still-needed figure, or {@code "********"} at or past the
     * level cap
     */
    private String showAdvanceExperience() {
        int maxLevel = ChannelRegistry.getPYMaxLevel();
        if (PlayerEventStatusUpdate.getPlayerStatusView().level() < maxLevel) {
            long advance = PlayerEventStatusUpdate.getPlayerCharSheetView()
                    .expToLevel()[PlayerEventStatusUpdate.getPlayerStatusView().level() - 1]
                    * PlayerEventStatusUpdate.getPlayerCharSheetView().expFactor() / 100L;
            return String.format("%d", advance);
        }
        return "********";
    }

    /**
     * Builds the combat character-sheet panel — armour class, melee and ranged figures — the port
     * of C's {@code get_panel_combat} ({@code [C] ui-player.c}, function {@code get_panel_combat}).
     *
     * <p>Allocates a nine-row {@link Panel} via {@link #panelAllocate(int)} and fills it, all in
     * {@link ColourEnum#COLOUR_LIGHT_BLUE} matching C's {@code COLOUR_L_BLUE}: Armour a
     * {@code "[%d,%+d]"} of {@link PlayerEventStatusUpdate#getPlayerStatusView()}'s
     * {@code armourClass()} and {@link PlayerEventStatusUpdate#getPlayerCharSheetView()}'s
     * {@code toA()}, standing in for C's {@code player->known_state.ac}/{@code to_a}; a blank
     * spacer row (via {@link Panel#space()}); Melee a {@code "%dd%d,%+d"} of {@code meleeDice()},
     * {@code meleeSides()} and {@code toD()}; To-hit a {@code "%d,%+d"} of a locally computed
     * {@code bth} ({@code meleeSkill() * 10 / bthPlusAdj()}, matching C's
     * {@code (player->state.skills[SKILL_TO_HIT_MELEE] * 10) / BTH_PLUS_ADJ}) divided by ten, and
     * {@code toH()}; Blows a {@code "%d.%d/turn"} of {@code numBlows()} split into whole and tenths
     * ({@code numBlows() / 100} and {@code numBlows() / 10 % 10}); another blank spacer row; Shoot
     * to-dam a {@code "%+d"} of a fixed zero, standing in for C's {@code dam = 0} default before its
     * own conditional weapon addition; To-hit again, of a freshly recomputed {@code bth} for the bow
     * ({@code shootSkill() * 10 / bthPlusAdj()}, matching C's own repeated {@code * 10} for
     * {@code SKILL_TO_HIT_BOW}) and the same {@code toH()} reused verbatim, matching C's own
     * {@code hit = player->known_state.to_h;} reassignment before the ranged section; and Shots a
     * {@code "%d.%d/turn"} of {@code numShots()} split into whole and tenths.
     *
     * <p>Unlike {@link #getPanelTopLeft()}, {@link #getPanelMisc()} and {@link #getPanelMidLeft()},
     * C's own {@code get_panel_combat} does not read flat {@code player} fields alone: it looks up
     * the equipped weapon and shooter itself via {@code equipped_item_by_slot_name} and, only when
     * one is equipped and identified, overrides {@code melee_dice}/{@code melee_sides} from the
     * weapon's {@code dd}/{@code ds} and adds {@code object_to_dam}/{@code object_to_hit} to
     * {@code dam}/{@code hit}, plus a further {@code +2} to melee {@code hit} when
     * {@code known_state.bless_wield} holds ({@code [C] ui-player.c}, function
     * {@code get_panel_combat}). This port keeps that lookup out of the method entirely: the
     * {@code meleeDice}/{@code meleeSides}/{@code toD}/{@code toH}/{@code meleeSkill}/
     * {@code shootSkill} fields this method reads off
     * {@link PlayerEventStatusUpdate#getPlayerCharSheetView()} are meant to already carry that same
     * computation by the time they are pushed into the view from the middle side, keeping this
     * method — like its three already-filled siblings — a pure formatter over whatever the view
     * hands it, and keeping {@link PlayerCharSheetView} itself no larger than it needs to be.
     *
     * <p>The "Armour" label is a deliberate British-spelling divergence from C's American "Armor"
     * ({@code [C] ui-player.c}, function {@code get_panel_combat}); every other label matches C's
     * text verbatim, including the lowercase "To-hit" shared by both the melee and ranged rows here,
     * matching C's own identical lowercase spelling in both its own melee and ranged
     * {@code panel_line} calls.
     *
     * <p>Of the five {@code getPanelX} builders, this is now the fourth, alongside
     * {@link #getPanelTopLeft()}, {@link #getPanelMidLeft()} and {@link #getPanelMisc()}, that both
     * allocates and completely fills its {@link Panel} before returning it — {@link #getPanelSkills()}
     * remains the last {@code TODO} stub. Called from the constructor as {@code pr4}'s builder; the
     * returned {@link Panel} is not yet drawn, since the {@code display_panel} renderer
     * ({@code [C] ui-player.c}, function {@code display_panel}) that would read it is not ported yet.
     *
     * <p>Method getPanelCombat coded on 260926, commented in full on 260926.
     *
     * @return a freshly built, fully populated nine-row {@link Panel} for the armour/melee/ranged
     * combat block
     */
    private Panel getPanelCombat() {
        Panel combat = panelAllocate(9);

        combat.initLines();
        combat.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "Armour", "[%d,%+d]",
                PlayerEventStatusUpdate.getPlayerStatusView().armourClass(),
                PlayerEventStatusUpdate.getPlayerCharSheetView().toA());

        // Melee
        int bth = PlayerEventStatusUpdate.getPlayerCharSheetView().meleeSkill() * 10
                / PlayerEventStatusUpdate.getPlayerCharSheetView().bthPlusAdj();
        int dam = PlayerEventStatusUpdate.getPlayerCharSheetView().toD();
        int hit = PlayerEventStatusUpdate.getPlayerCharSheetView().toH();
        int meleeDice = PlayerEventStatusUpdate.getPlayerCharSheetView().meleeDice();
        int meleeSides = PlayerEventStatusUpdate.getPlayerCharSheetView().meleeSides();
        int blows = PlayerEventStatusUpdate.getPlayerCharSheetView().numBlows();

        combat.space();
        combat.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "Melee", "%dd%d,%+d", meleeDice,
                meleeSides, dam);
        combat.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "To-hit", "%d,%+d", bth / 10, hit);
        combat.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "Blows", "%d.%d/turn",
                blows / 100, blows / 10 % 10);

        // Ranged
        bth = (PlayerEventStatusUpdate.getPlayerCharSheetView().shootSkill() * 10)
                / PlayerEventStatusUpdate.getPlayerCharSheetView().bthPlusAdj();
        dam = 0;
        hit = PlayerEventStatusUpdate.getPlayerCharSheetView().toH();

        combat.space();
        combat.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "Shoot to-dam", "%+d", dam);
        combat.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "To-hit", "%d,%+d", bth / 10, hit);
        combat.panelLine(ColourEnum.COLOUR_LIGHT_BLUE, "Shots", "%d.%d/turn",
                PlayerEventStatusUpdate.getPlayerCharSheetView().numShots() / 10,
                PlayerEventStatusUpdate.getPlayerCharSheetView().numShots() % 10);

        return combat;
    }

    private Panel getPanelSkills() {
        Panel skills = panelAllocate(8);

        int depth = PlayerEventStatusUpdate.getPlayerStatusView().depth();

        skills.initLines();
        // Saving throws
        int skill = Math.clamp(PlayerEventStatusUpdate.getPlayerCharSheetView().saveSkill(), 0, 100);
        skills.panelLine(colourTable[skill / 10], "Saving Throw", "%d%%", skill);

        // Stealth
        StringAndColour col = likert(PlayerEventStatusUpdate.getPlayerCharSheetView().stealthSkill(), 1);
        skills.panelLine(col.attr(), "Stealth", "%s", col.string());

        // Physical disarming - based on disarming a dungeon trap
        skill = Math.clamp(PlayerEventStatusUpdate.getPlayerCharSheetView().disarmPhysSkill() - depth / 5,
                2, 100);
        skills.panelLine(colourTable[skill / 10], "Disarm - phys.", "%d%%", skill);

        // Magical disarming 
        skill = Math.clamp(PlayerEventStatusUpdate.getPlayerCharSheetView().disarmMagicSkill() - depth / 5,
                2, 100);
        skills.panelLine(colourTable[skill / 10], "Disarm - magic", "%d%%", skill);

        // Magic devices
        skill = PlayerEventStatusUpdate.getPlayerCharSheetView().deviceSkill();
        skills.panelLine(colourTable[skill / 13], "Magic Devices", "%d", skill);

        // Searching ability
        skill = Math.clamp(PlayerEventStatusUpdate.getPlayerCharSheetView().searchSkill(), 0, 100);
        skills.panelLine(colourTable[skill / 10], "Searching", "%d%%", skill);

        // Infravision
        skills.panelLine(ColourEnum.COLOUR_LIGHT_GREEN, "Infravision", "%d ft",
                PlayerEventStatusUpdate.getPlayerCharSheetView().infra());

        // Speed
        skill = PlayerEventStatusUpdate.getPlayerCharSheetView().calcSpeed();
        ColourEnum attr = skill < 110 ? ColourEnum.COLOUR_LIGHT_UMBER
                : ColourEnum.COLOUR_LIGHT_GREEN;
        skills.panelLine(attr, "Speed", "%s", showSpeed());

        return skills;
    }

    private String showSpeed() {
        int tmp = PlayerEventStatusUpdate.getPlayerCharSheetView().calcSpeed();
        if (tmp == 110)
            return "Normal";

        int multiplier = 10 * ChannelRegistry.extractEnergy[tmp]
                / ChannelRegistry.extractEnergy[110];
        int intMul = multiplier / 10;
        int decMul = multiplier % 10;
        if (PlayerEventStatusUpdate.getPlayerCharSheetView().optionEffectiveSpeed())
            return String.format("$d.$dx (%dx)", intMul, decMul, tmp - 110);
        else
            return String.format("$d (%d.%d)", tmp - 110, intMul, decMul);
    }

    /**
     * Rates {@code skill} against {@code level} and pairs the rating with its display colour —
     * the Java form of C's {@code likert(int x, int y, uint8_t *attr)} in {@code ui-player.c},
     * which returns the rating string and sets {@code attr} to the same colour as a side effect.
     *
     * <p>Treats a non-positive {@code level} as {@code 1} first (C's "paranoia" guard against a
     * divide-by-zero), then a negative {@code skill} always rates "Very Bad" in red regardless of
     * {@code level}. Otherwise {@code skill / level} is bucketed into one of eleven named ratings
     * — "Bad" through "Legendary" — each with a fixed colour, matching {@code ui-player.c}'s
     * {@code switch} clause for clause, including the two bands ("Good" and "Very Good") that
     * are yellow rather than green.
     *
     * <p>Method likert coded on 260926, commented in full on 260926.
     *
     * @param skill the raw value being rated, e.g. the player's stealth skill
     * @param level the divisor applied to {@code skill} before bucketing; non-positive values are
     *              treated as {@code 1}
     * @return the rating string paired with the colour it should be drawn in
     */
    private StringAndColour likert(int skill, int level) {
        // Check for lower bounds
        if (level <= 0) level = 1;

        if (skill < 0) {
            return new StringAndColour("Very Bad", ColourEnum.COLOUR_RED);
        }

        // Analyze the value
        return switch (skill / level) {
            case 0, 1 -> new StringAndColour("Bad", ColourEnum.COLOUR_RED);
            case 2 -> new StringAndColour("Poor", ColourEnum.COLOUR_RED);
            case 3, 4 -> new StringAndColour("Fair", ColourEnum.COLOUR_YELLOW);
            case 5 -> new StringAndColour("Good", ColourEnum.COLOUR_YELLOW);
            case 6 -> new StringAndColour("Very Good", ColourEnum.COLOUR_YELLOW);
            case 7, 8 -> new StringAndColour("Excellent", ColourEnum.COLOUR_LIGHT_GREEN);
            case 9, 10, 11, 12, 13 -> new StringAndColour("Superb", ColourEnum.COLOUR_LIGHT_GREEN);
            case 14, 15, 16, 17 -> new StringAndColour("Heroic", ColourEnum.COLOUR_LIGHT_GREEN);
            default -> new StringAndColour("Legendary", ColourEnum.COLOUR_LIGHT_GREEN);
        };
    }

    /**
     * Computes how much more weight the player can carry before becoming burdened — the port
     * of C's {@code weight_remaining}, {@code player-calcs.c}.
     *
     * <p>{@code 60 * weightLimit - totalWeight - 1}, both operands in tenth-pounds. C recomputes
     * {@code adj_str_wgt[state.stat_ind[STAT_STR]]} directly here rather than routing through
     * its own {@code weight_limit} helper (also {@code player-calcs.c}), which returns that same
     * table entry multiplied by 100 for a different calculation (the carry-limit used for the
     * speed-penalty threshold). {@link PlayerEventStatusUpdate#getPlayerCharSheetView()}'s
     * {@code weightLimit} field carries the raw, unscaled table entry to match — not
     * {@link uk.co.jackoftradesltd.middle.player.PlayerState#weightLimit()}'s ×100 figure,
     * despite the shared name.
     *
     * <p>Called from {@link #getPanelMidLeft()} to build the panel's "Burden" line, though that
     * call's result ({@code diff}) is not yet consumed — {@link #getPanelMidLeft()} is still a
     * stub.
     *
     * <p>Method weightRemaining coded on 260925, commented in full on 260925.
     *
     * @return the tenth-pounds still available before the player is burdened; negative once over
     * the limit
     */
    private int weightRemaining() {
        return 60 * PlayerEventStatusUpdate.getPlayerCharSheetView().weightLimit() -
                PlayerEventStatusUpdate.getPlayerCharSheetView().totalWeight() - 1;
    }

    /**
     * Chooses the colour a value/maximum pair displays in — the port of C's {@code max_color}
     * ({@code [C] ui-player.c}, function {@code max_color}; the C name keeps the American
     * spelling even though this project's colour vocabulary elsewhere follows British spelling).
     *
     * <p>{@code val < max} — the value is still short of its recorded maximum — returns
     * {@link ColourEnum#COLOUR_YELLOW}; anything else, including {@code val == max}, returns
     * {@link ColourEnum#COLOUR_LIGHT_GREEN}, matching C's {@code val < max} test exactly (the
     * boundary sits on the green side in both).
     *
     * <p>In C, {@code get_panel_midleft} calls {@code max_color} twice, for the Level and Cur Exp
     * rows; {@link #getPanelMidLeft()} calls this overload the same way, for its Level row.
     *
     * <p>Method maxColour(int, int) coded on 260925, commented in full on 260925.
     *
     * @param val the value being displayed, e.g. the player's current level
     * @param max the value's recorded maximum, e.g. the player's max level
     * @return {@link ColourEnum#COLOUR_YELLOW} if {@code val} is below {@code max},
     * {@link ColourEnum#COLOUR_LIGHT_GREEN} if it is at or above it
     */
    private ColourEnum maxColour(int val, int max) {
        return val < max ? ColourEnum.COLOUR_YELLOW : ColourEnum.COLOUR_LIGHT_GREEN;
    }

    /**
     * Chooses the colour a value/maximum pair displays in — the {@code long} overload of
     * {@link #maxColour(int, int)}, itself the port of C's {@code max_color} ({@code [C]
     * ui-player.c}, function {@code max_color}). C's {@code max_color} only ever takes
     * {@code int} (both {@code player->lev}/{@code max_lev} and {@code player->exp}/{@code
     * max_exp} are {@code int32_t}); this overload exists because
     * {@link uk.co.jackoftradesltd.channel.messages.data.PlayerStatusView#experience()} and
     * {@link uk.co.jackoftradesltd.channel.messages.data.PlayerStatusView#maxExperience()} are
     * held as {@code long} on the Java side, so the Cur Exp row can call {@code maxColour}
     * without narrowing either argument first.
     *
     * <p>{@code val < max} — the value is still short of its recorded maximum — returns
     * {@link ColourEnum#COLOUR_YELLOW}; anything else, including {@code val == max}, returns
     * {@link ColourEnum#COLOUR_LIGHT_GREEN}, matching C's {@code val < max} test exactly (the
     * boundary sits on the green side in both).
     *
     * <p>In C, {@code get_panel_midleft} calls {@code max_color} twice, for the Level and Cur
     * Exp rows; {@link #getPanelMidLeft()} calls this overload for its Cur Exp row and the
     * {@code int} overload for its Level row.
     *
     * <p>Method maxColour(long, long) coded on 260925, commented in full on 260925.
     *
     * @param val the value being displayed, e.g. the player's current experience
     * @param max the value's recorded maximum, e.g. the player's max experience
     * @return {@link ColourEnum#COLOUR_YELLOW} if {@code val} is below {@code max},
     * {@link ColourEnum#COLOUR_LIGHT_GREEN} if it is at or above it
     */
    private ColourEnum maxColour(long val, long max) {
        return val < max ? ColourEnum.COLOUR_YELLOW : ColourEnum.COLOUR_LIGHT_GREEN;
    }

    public void displayPlayer(PlayerDisplayMode mode) {
        if (!haveValidCharSheetConfig()) {
            configureCharSheet();
        }

        // Erase the screen
        termData.getTerm().clearFrom(0);

        if (termData != UIGlobals.getActiveTermData()
                && !PlayerEventStatusUpdate.getPlayerCharSheetView().playerIsPlaying())
            return;

        displayPlayerStatInfo();

        if (mode == PlayerDisplayMode.DISPLAY_FULL) {
            Panel p = panels[0].getPanel();
            // TODO <-- Here
            // 
        } else {
            // displayPlayerXtraInfo();
        }

    }

    /**
     * Draws the six stat-block columns (current/self, race bonus, class bonus, equipment bonus,
     * modified maximum, and — when a stat is drained — the currently-used value) for every stat in
     * turn — the port of C's {@code display_player_stat_info} ({@code [C] ui-player.c:450-510}).
     *
     * <p>Column headers are written once at {@code row - 1}, then each of the
     * {@link ChannelRegistry#STAT_MAX} stats gets one row starting at {@code row = 2}: the stat's
     * name (from {@link UIRegistry#statNames} normally, {@link UIRegistry#statReducedNames} when
     * {@code currentStats[index] < maxStats[index]} indicates a drained stat, mirroring C's
     * {@code stat_cur[i] < stat_max[i]} choice between {@code stat_names} and
     * {@code stat_names_reduced}), a {@code "!"} marker when {@code maxStats[index]} equals
     * {@code 18 + 100} (C's natural-maximum indicator), the natural maximum and modified maximum
     * via {@link #cnvStat(int)}, and the race/class/equipment bonuses each formatted as a signed
     * three-digit number ({@code "%+3d"}, matching C's {@code strnfmt} calls). The drained-use
     * column is gated on the same {@code currentStats[index] < maxStats[index]} test as the
     * name-column choice, exactly as C tests {@code stat_cur[i] < stat_max[i]} twice rather than
     * caching the comparison.
     *
     * <p>All six stat arrays come from {@link PlayerEventStatusUpdate#getPlayerStatusView()}, the
     * Java stand-ins for C's direct {@code player->stat_cur}, {@code player->stat_max},
     * {@code player->race->r_adj}, {@code player->class->c_adj}, {@code player->state.stat_add} and
     * {@code player->state.stat_top}/{@code stat_use} field reads. Drawing itself goes through
     * {@link #termData}'s {@link Term}, standing in for C's implicit draw onto the active terminal.
     *
     * <p>Called from {@link #displayPlayer(PlayerDisplayMode)} as the second stage of the
     * character-sheet draw, after the screen clear and before the (not yet ported) resistance-panel
     * and stat-modifier passes.
     *
     * <p>Method displayPlayerStatInfo coded before 260925, commented in full on 260925.
     */
    private void displayPlayerStatInfo() {
        int[] currentStats = PlayerEventStatusUpdate.getPlayerStatusView().currentStats();
        int[] maxStats = PlayerEventStatusUpdate.getPlayerStatusView().maxStats();
        int[] raceStatBonuses = PlayerEventStatusUpdate.getPlayerCharSheetView().playerRaceStatBonuses();
        int[] classStatBonuses = PlayerEventStatusUpdate.getPlayerCharSheetView().playerClassStatBonuses();
        int[] equipStatBonuses = PlayerEventStatusUpdate.getPlayerCharSheetView().playerEquipStatBonuses();
        int[] modifiedMaxStatBonuses = PlayerEventStatusUpdate.getPlayerCharSheetView().playerTotalStatBonuses();
        int[] statUse = PlayerEventStatusUpdate.getPlayerCharSheetView().playerCurrModStat();

        Term term = termData.getTerm();

        int row = 2;
        int col = 42;

        // Print out the labels for the columns
        term.cPutStr(ColourEnum.COLOUR_WHITE, "  Self", row - 1, col + 5);
        term.cPutStr(ColourEnum.COLOUR_WHITE, " RB", row - 1, col + 12);
        term.cPutStr(ColourEnum.COLOUR_WHITE, " CB", row - 1, col + 16);
        term.cPutStr(ColourEnum.COLOUR_WHITE, " EB", row - 1, col + 20);
        term.cPutStr(ColourEnum.COLOUR_WHITE, "  Best", row - 1, col + 24);

        // Display the stats
        for (int index = 0; index < ChannelRegistry.STAT_MAX; index++) {
            // Reduced or normal
            if (currentStats[index] < maxStats[index]) {
                term.putStr(UIRegistry.statReducedNames[index], row + index, col);
            } else {
                term.putStr(UIRegistry.statNames[index], row + index, col);
            }

            // Indicate natural maximum
            if (maxStats[index] == 18 + 100)
                term.putStr("!", row + index, col + 3);

            // Internal "natural" maximum
            String max = cnvStat(maxStats[index]);
            term.cPutStr(ColourEnum.COLOUR_LIGHT_GREEN, max, row + index, col + 5);

            // Race bonuses
            String race = String.format("%+3d", raceStatBonuses[index]);
            term.cPutStr(ColourEnum.COLOUR_LIGHT_BLUE, race, row + index, col + 12);

            // Class bonuses
            String classBonus = String.format("%+3d", classStatBonuses[index]);
            term.cPutStr(ColourEnum.COLOUR_LIGHT_BLUE, classBonus, row + index, col + 16);

            // Equipment bonuses
            String equip = String.format("%+3d", equipStatBonuses[index]);
            term.cPutStr(ColourEnum.COLOUR_LIGHT_BLUE, equip, row + index, col + 20);

            // Resulting modified max value
            String total = cnvStat(modifiedMaxStatBonuses[index]);
            term.cPutStr(ColourEnum.COLOUR_LIGHT_GREEN, total, row + index, col + 24);

            // Only display statUse if there has been draining.
            if (currentStats[index] < maxStats[index]) {
                String use = cnvStat(statUse[index]);
                term.cPutStr(ColourEnum.COLOUR_YELLOW, use, row + index, col + 31);
            }
        }

    }

    /**
     * Converts a raw stat value into its six-character, right-justified display form — the port of
     * C's {@code cnv_stat} ({@code [C] ui-display.c:117-132}).
     *
     * <p>Stats at or below 18 render as a plain right-justified number ({@code "    %2d"}). Above
     * 18, {@code stat} carries a bonus ({@code stat - 18}) rendered after a {@code "18/"} prefix:
     * a bonus of 220 or more collapses to the literal {@code "18/***"} (C's cap on displaying
     * a stat past its practical maximum); a bonus of 100 or more prints as three digits
     * ({@code "18/%03d"}); anything below that prints as two digits with a leading space
     * ({@code " 18/%02d"}) so every branch's output stays six characters wide. The 100 threshold is
     * inclusive on both sides ({@code bonus >= 100}), matching C's {@code else if (bonus >= 100)} —
     * a bonus of exactly 100 (a stat of 118) takes the three-digit branch, not the padded one.
     *
     * <p>Method cnvStat coded before 260925, commented in full on 260925.
     *
     * @param stat the raw stat value to format
     * @return the six-character display string for {@code stat}
     */
    private String cnvStat(int stat) {
        // Stats above 18 need special treatment
        if (stat > 18) {
            int bonus = (stat - 18);

            if (bonus >= 220) {
                return "18/***";
            }
            if (bonus >= 100) {
                return String.format("18/%03d", bonus);
            }
            return String.format(" 18/%02d", bonus);
        } else {
            return String.format("    %2d", stat);
        }
    }

    /**
     * Builds and caches the character-sheet's resistance-panel layout for the current player —
     * the port of C's {@code configure_char_sheet} ({@code [C] ui-player.c:186-266}). Replaces
     * {@link #cachedConfig} outright with a freshly-populated {@link CharSheetConfig}; C instead
     * frees and reallocates {@code cached_config} via {@code release_char_sheet_config}
     * ({@code [C] ui-player.c:160-173, 198}), but the two reach the same end state, since neither
     * leaves any part of the previous layout observable afterwards.
     *
     * <p>First builds the stat-modifier entry list: every {@link UIEntry} in both the
     * {@code "CHAR_SCREEN1"} and {@code "stat_modifiers"} categories, capped at
     * {@link ChannelRegistry#STAT_MAX} because the stat-modifier display is hardwired to that many
     * rows — C applies the same {@code STAT_MAX} clamp for the same reason
     * ({@code [C] ui-player.c:211-213}).
     *
     * <p>Then, for each of the four resistance-panel regions ({@code "resistances"},
     * {@code "abilities"}, {@code "hindrances"}, {@code "modifiers"}, in that order): positions
     * the region's {@link Region} at column {@code index * (resCols + 1)}, row
     * {@code 2 + STAT_MAX} and width {@code resCols}; counts the matching entries and, if fitting
     * them plus two rows of chrome below that region's row would overflow row 22, shortens the
     * count to {@code 20 - row} instead — the same 22/20 bounds C uses to keep the panel inside
     * its display ({@code [C] ui-player.c:238-244}); then, for each matching entry in turn, builds
     * a {@link CharSheetResist} wrapping it, files it under that region's index and that entry's
     * position within the region via
     * {@link CharSheetConfig#setResistsByRegion(int, int, CharSheetResist)}, and computes its
     * display label via {@link #getUIEntryLabel} with a trailing {@code ":"} appended — standing
     * in for C's separate {@code get_ui_entry_label} plus {@code text_mbstowcs} call that writes
     * the colon into the last slot of the entry's own six-character label buffer
     * ({@code [C] ui-player.c:250-252}). {@link CharSheetConfig}'s resist storage, keyed by
     * (region, entry-within-region), is the Java form of C's {@code resists_by_region[i][j]} — an
     * array of pointers, each to a separately-sized array of per-region entries
     * ({@code [C] ui-player.c:135, 246}).
     *
     * <p>{@link CharSheetConfig#getResRows()} tracks the largest per-region entry count seen
     * across all four regions as the loop runs; once every region has been processed, every
     * region's {@code pageRows} is set to that maximum plus two, in a final pass mirroring C's own
     * closing loop ({@code [C] ui-player.c:262-265}).
     *
     * <p>Method configureCharSheet coded before 260925, commented in full on 260925.
     */
    private void configureCharSheet() {
        String[] regionCategories = {"resistances", "abilities", "hindrances", "modifiers"};
        cachedConfig = new CharSheetConfig();

        String[] testCategories = {"CHAR_SCREEN1", "stat_modifiers"};
        UIEntryIterator uiIter = UIEntryCode.initialiseUIEntryIterator(UIPlayer::checkForTwoCategories, testCategories,
                testCategories[1]);
        int num = Math.min(uiIter.getNum(), ChannelRegistry.STAT_MAX);

        cachedConfig.setNStatModEntries(num);
        cachedConfig.initStatModEntries(num);
        for (int index = 0; index < num; index++) {
            cachedConfig.setStatModEntry(index, uiIter.advance());
        }

        cachedConfig.setResNlabel(6);
        cachedConfig.setResCols(cachedConfig.getResNLabel() + 1
                + PlayerEventStatusUpdate.getPlayerCharSheetView().bodyCount());
        cachedConfig.setResRows(0);

        for (int index = 0; index < 4; index++) {
            cachedConfig.getResRegion(index).setCol(index *
                    (cachedConfig.getResCols() + 1));
            cachedConfig.getResRegion(index).setRow(2 + ChannelRegistry.STAT_MAX);
            cachedConfig.getResRegion(index).setWidth(cachedConfig.getResCols());

            testCategories[1] = regionCategories[index];
            uiIter = UIEntryCode.initialiseUIEntryIterator(UIPlayer::checkForTwoCategories, testCategories,
                    regionCategories[index]);
            num = uiIter.getNum();
            // Fit in a 23 row display; leave at least one row blank before prompt on last row.
            if (num + 2 + cachedConfig.getResRegion(index).getRow() > 22) {
                num = 20 - cachedConfig.getResRegion(index).getRow();
            }
            cachedConfig.setnResistsByRegion(index, num);
            for (int iterIndex = 0; iterIndex < num; iterIndex++) {
                UIEntry uiEntry = uiIter.advance();

                CharSheetResist resist = new CharSheetResist(uiEntry);
                cachedConfig.setResistsByRegion(index, iterIndex, resist);
                String label = getUIEntryLabel(uiEntry, cachedConfig.getResNLabel(), true,
                        cachedConfig.getResistsByRegion(index, iterIndex).getLabel()) + ":";
                cachedConfig.getResistsByRegion(index, iterIndex).setLabel(label);
            }

            if (cachedConfig.getResRows() < cachedConfig.getnResistsByRegion(index)) {
                cachedConfig.setResRows(cachedConfig.getnResistsByRegion(index));
            }
        }

        for (int index = 0; index < 4; index++) {
            cachedConfig.getResRegion(index).setPageRows(cachedConfig.getResRows() + 2);
        }
    }

    /**
     * Builds a label for a UI entry, padded or truncated to an exact display width — the port of
     * C's {@code get_ui_entry_label} ({@code [C] ui-entry.c:339-387}).
     *
     * <p>{@code length} counts the visible characters only; C's buffer additionally reserves a
     * terminating null, so where C computes padding and truncation against {@code length - 1},
     * the returned {@link String} (no terminator to reserve) reproduces that same
     * {@code length - 1} content width directly. A {@code length} of zero or less is a no-op
     * that returns {@code label} unchanged, matching C's early return before the buffer is ever
     * touched; a {@code length} of one returns the empty string, standing in for C's single
     * null-only buffer.
     *
     * <p>The source text comes from {@link UIEntry#getShortenedLabel(int)} when {@code length}
     * falls within {@link UIRegistry#MAX_SHORTENED} plus one, and from {@link UIEntry#getLabel()}
     * otherwise, exactly as C selects between {@code entry->shortened_labels} and
     * {@code entry->label}. If that source text is too long for the requested width it is
     * truncated to {@code length - 1} characters; if it is too short it is padded with spaces on
     * the side {@code padLeft} names, {@code length - 1 - numChars} of them, so the padded and
     * truncated cases both end up with the same content width.
     *
     * <p>{@link #firstCallOfGetEntryLabel} and {@link #spc} mirror C's function-static one-shot
     * space initialisation but are not themselves read by this method — the padding here is
     * built with a literal {@code " "} instead.
     *
     * <p>Called from {@link #configureCharSheet()} while laying out the resistance panel.
     *
     * <p>Method getUIEntryLabel coded before 260925, commented in full on 260925.
     *
     * @param entry   the UI entry whose label text is being formatted
     * @param length  the desired content width in characters; zero or less is a no-op, one
     *                returns the empty string
     * @param padLeft {@code true} pads on the left, {@code false} pads on the right
     * @param label   the fallback value returned unchanged when {@code length} is zero or less
     * @return the entry's label padded or truncated to {@code length - 1} characters, the empty
     * string when {@code length} is one, or {@code label} unchanged when {@code length} is zero
     * or less
     */
    private String getUIEntryLabel(UIEntry entry, int length, boolean padLeft, String label) {
        if (firstCallOfGetEntryLabel) {
            spc[0] = ' ';
            spc[1] = '\0';
            firstCallOfGetEntryLabel = false;
        }

        String src;
        int numChars;

        if (length <= 0) return label;

        if (length == 1) {
            return "";
        }

        if (length <= UIRegistry.MAX_SHORTENED + 1) {
            src = entry.getShortenedLabel(length - 2);
        } else {
            src = entry.getLabel();
        }
        numChars = src.length();
        if (numChars < length - 1) {
            if (padLeft) {
                String left = " ".repeat(length - numChars - 1);
                label = left + src;
            } else {
                label = src + " ".repeat(length - numChars - 1);
            }
        } else {
            label = src.substring(0, length - 1);
        }
        return label;
    }

    /**
     * Reports whether the cached character-sheet layout is still usable for the current
     * player — the port of C's {@code have_valid_char_sheet_config} ({@code ui-player.c:146-156}).
     *
     * <p>There is nothing to reuse until a layout has been built at least once, so a
     * {@code null} {@link #cachedConfig} fails outright. Once one exists, the check is
     * narrower: it only re-derives {@code resCols} from {@code resNLabel} and the player's
     * current body part count and compares that against the cached value. C computes
     * {@code res_cols} the same way in {@code configure_char_sheet} ({@code ui-player.c:223-224}),
     * so the two can only disagree when the player's body shape has changed since the
     * layout was cached — a race switch mid-game being the case that matters, since a
     * different race can bring a different equipment slot count.
     *
     * <p>{@link #displayPlayer(PlayerDisplayMode)} calls this as a boundary guard before
     * drawing; a stale cache should trigger a rebuild before rendering continues.
     *
     * <p>Method haveValidCharSheetConfig coded on 260911, commented in full on 260911.
     *
     * @return {@code true} if the cached layout's resistance-panel column count still
     * matches the current player's body, {@code false} if there is no cached layout
     * or it is stale
     */
    private boolean haveValidCharSheetConfig() {
        if (cachedConfig == null) {
            return false;
        }
        return cachedConfig.getResCols() == cachedConfig.getResNLabel() + 1
                + PlayerEventStatusUpdate.getPlayerCharSheetView().bodyCount();
    }

    /**
     * The two display modes {@link #displayPlayer(PlayerDisplayMode)} accepts — the Java form of
     * the raw {@code int mode} parameter C's {@code display_player} switches on
     * ({@code [C] ui-player.c:892, 907-920}), where C tests {@code mode} for truthiness rather than
     * naming the two states.
     *
     * <p>Neither branch this enum selects between is ported yet — both arms of
     * {@link #displayPlayer(PlayerDisplayMode)}'s {@code if (mode == PlayerDisplayMode.DISPLAY_FULL)}
     * are still stubs, so this type currently only fixes the vocabulary the eventual port will
     * dispatch on.
     *
     * <p>Enum PlayerDisplayMode coded before 260925, commented in full on 260925.
     */
    public enum PlayerDisplayMode {
        /**
         * C's truthy {@code mode} (called as {@code display_player(1)}), which draws the top-left
         * name/class/HP/SP panel followed by the stat-sustain and other-flag panels
         * ({@code [C] ui-player.c:907-916}) — described in C's own comment as the "special display
         * with equipment flags".
         *
         * <p>Enum constant DISPLAY_FULL coded before 260925, commented in full on 260925.
         */
        DISPLAY_FULL,
        /**
         * C's falsy {@code mode} (called as {@code display_player(0)}), which calls
         * {@code display_player_xtra_info} to draw the five summary panels plus the player's
         * history text ({@code [C] ui-player.c:918-919}) — C's own comment calls this the
         * "standard display with skills/history".
         *
         * <p>Enum constant DISPLAY_EXTRA coded before 260925, commented in full on 260925.
         */
        DISPLAY_EXTRA
    }

    /**
     * A rating string paired with the colour it should be drawn in — the Java form of the
     * two-part result C's {@code likert()} returns via its {@code const char *} return value
     * and its {@code uint8_t *attr} out-parameter, folded here into a single record so
     * {@link #likert(int, int)} can return both at once instead of writing through a pointer.
     *
     * <p>Record StringAndColour coded on 260926, commented in full on 260926.
     */
    private record StringAndColour(String string,
                                   ColourEnum attr) {
    }

    /**
     * A titled group of {@link PanelLine} rows awaiting layout on the character screen — the port
     * of C's {@code struct panel} ({@code [C] ui-player.c}, struct {@code panel}).
     *
     * <p>{@link #lines} takes the place of C's separately-tracked {@code struct panel_line *lines}
     * pointer plus {@link #len}/{@link #max} counters: C pre-allocates {@code max} slots via
     * {@code panel_allocate} and fills them one at a time via {@code panel_line}/{@code panel_space},
     * tracking how many of the {@code max} slots are filled in {@link #len}; a {@link List} could
     * grow on demand instead, but this class keeps both counters so the ports of
     * {@code panel_allocate}/{@code panel_line}/{@code panel_space} —
     * {@link UIPlayer#panelAllocate(int)}, {@link Panel#panelLine(ColourEnum, String, String,
     * Object...)} and {@link Panel#space()} — can mirror C's fixed-capacity assertions rather than
     * silently dropping them. {@link Panel#space()} also appends its own blank {@link PanelLine} to
     * {@link #lines}, standing in for the already-allocated zeroed slot that C's
     * {@code panel_space} merely walks past, so that {@link #lines}{@code .size()} keeps equalling
     * {@link #len} even after a spacer row.
     *
     * <p>Built by {@link UIPlayer#panelAllocate(int)}. {@link UIPlayer#getPanelTopLeft()} and
     * {@link UIPlayer#getPanelMidLeft()} each fill one completely before returning it — the
     * latter's fill includes one {@link Panel#space()} spacer row; {@link UIPlayer#getPanelMisc()},
     * {@link UIPlayer#getPanelCombat()} and
     * {@link UIPlayer#getPanelSkills()} never reach this class at all, since each is still a
     * {@code TODO} stub returning {@code null}. Whatever {@link Panel} does get built, nothing
     * reads it back yet — the {@code display_panel} renderer ({@code [C] ui-player.c}, function
     * {@code display_panel}) that would fill the screen from one is not ported yet.
     *
     * <p>Class Panel coded before 260925, commented in full on 260926.
     */
    private class Panel {
        private static final Logger logger = LogManager.getLogger(Panel.class);

        /**
         * The number of {@link #lines} entries filled so far — the Java form of C's {@code size_t len}
         * ({@code [C] ui-player.c}, struct {@code panel}), which both {@code panel_line} and
         * {@code panel_space} ({@code [C] ui-player.c}, functions {@code panel_line} and
         * {@code panel_space}) increment on every call. {@link Panel#panelLine} and
         * {@link Panel#space()} both reproduce that increment, and both keep {@link #lines} growing
         * in step so this field always equals {@link #lines}{@code .size()}.
         *
         * <p>Field len coded before 260925, commented in full on 260926.
         */
        private int len;
        /**
         * The panel's fixed capacity, set once at construction — the Java form of C's
         * {@code size_t max} ({@code [C] ui-player.c}, struct {@code panel}), which
         * {@code panel_allocate} sets from its {@code n} argument and which both {@code panel_line}
         * and {@code panel_space} ({@code [C] ui-player.c}, functions {@code panel_line} and
         * {@code panel_space}) assert {@link #len} never reaches. {@link Panel#panelLine} and
         * {@link Panel#space()} both reproduce that same bound check.
         *
         * <p>Field max coded before 260925, commented in full on 260926.
         */
        private int max;
        /**
         * The panel's rows in display order — the Java form of C's {@code struct panel_line *lines}
         * array pointer ({@code [C] ui-player.c}, struct {@code panel}), sized by
         * {@code panel_allocate} to {@link #max} zeroed entries and filled one at a time by
         * {@code panel_line}/{@code panel_space} ({@code [C] ui-player.c}, functions
         * {@code panel_line} and {@code panel_space}). C's array is pre-allocated, so
         * {@code panel_space} only has to walk past a slot that already exists, already zeroed to a
         * blank row; this growable {@link List} instead starts empty, so {@link Panel#space()} has
         * to append an explicit blank {@link PanelLine} of its own to keep this field's size equal
         * to {@link #len}.
         *
         * <p>Field lines coded before 260925, commented in full on 260926.
         */
        private List<PanelLine> lines;

        /**
         * Sets {@link #len} — the Java form of C's direct {@code p->len = 0} assignment inside
         * {@code panel_allocate} ({@code ui-player.c}, function {@code panel_allocate}). C also
         * writes this slot from {@code panel_line} on every call it makes, incrementing it as rows
         * are filled ({@code ui-player.c}, function {@code panel_line}); {@link Panel#panelLine}
         * reproduces that increment too, but by writing {@link #len} directly ({@code len++})
         * rather than by calling this setter.
         *
         * <p>Method setLen coded before 260925, commented in full on 260925.
         *
         * @param i the new {@link #len} value
         */
        public void setLen(int i) {
            this.len = i;
        }

        /**
         * Sets {@link #max} — the Java form of C's direct {@code p->max = n} assignment inside
         * {@code panel_allocate} ({@code ui-player.c}, function {@code panel_allocate}).
         *
         * <p>Method setMax coded before 260925, commented in full on 260925.
         *
         * @param i the new {@link #max} value
         */
        public void setMax(int i) {
            this.max = i;
        }

        /**
         * Initialises {@link #lines} to an empty, growable list — the Java form of C's
         * {@code p->lines = mem_zalloc(p->max * sizeof *p->lines)} inside {@code panel_allocate}
         * ({@code ui-player.c}, function {@code panel_allocate}), which instead allocates a
         * {@link #max}-element array of zeroed {@code struct panel_line}s ready to be filled by
         * index. {@link Panel#panelLine} and {@link Panel#space()} both append to the resulting
         * list on every call (see their own docs).
         *
         * <p>Method initLines coded before 260925, commented in full on 260926.
         */
        public void initLines() {
            this.lines = new ArrayList<PanelLine>();
        }

        /**
         * Formats and appends one row to this panel — the port of C's {@code panel_line}
         * ({@code [C] ui-player.c}, function {@code panel_line}).
         *
         * <p>Refuses to write past capacity: when {@link #len} has already reached {@link #max}
         * this logs an error and throws a {@link RuntimeException}, standing in for C's
         * {@code assert(p->len != p->max)}, which only aborts in a debug build; this Java port
         * enforces the bound unconditionally.
         *
         * <p>{@code format} and {@code formatArgs} are combined via {@link String#format}, the
         * Java form of C's {@code vstrnfmt(pl->value, sizeof pl->value, fmt, vp)} filling
         * {@code pl->value}'s fixed {@code char[20]} buffer from the same {@code fmt}/varargs
         * pair. {@code vstrnfmt} stops appending content once it has written 19 of that buffer's
         * 20 bytes, reserving the last for the null terminator, so C's displayed value never
         * exceeds 19 characters; this method reproduces that same 19-character cap by clamping
         * the formatted result to {@code Math.min(formatted.length(), 19)} before it is stored.
         *
         * <p>The clamped value, together with {@code attribute} and {@code label}, is wrapped in
         * a new {@link PanelLine}, appended to {@link #lines}, and {@link #len} is incremented —
         * the Java form of C's {@code pl = &p->lines[p->len++]; pl->attr = attr; pl->label =
         * label;}. C takes the slot (and so increments {@code p->len}) before filling it; this
         * port builds the complete {@link PanelLine} first and appends it afterwards, incrementing
         * {@link #len} last — a difference in ordering only, since both happen sequentially
         * within one call with no observer in between.
         *
         * <p>Called with real player data by {@link UIPlayer#getPanelTopLeft()}, which fills all
         * six of its rows this way, and by {@link UIPlayer#getPanelMidLeft()}, which fills eight
         * of its nine rows this way (the ninth is a blank spacer via {@link Panel#space()}).
         * {@link UIPlayer#getPanelMisc()}, {@link UIPlayer#getPanelCombat()} and
         * {@link UIPlayer#getPanelSkills()} are still {@code TODO} stubs that never reach this
         * method.
         *
         * <p>Method panelLine coded on 260925, commented in full on 260925.
         *
         * @param attribute  this row's display colour
         * @param label      this row's fixed label text
         * @param format     the {@link String#format} pattern used to build this row's value text
         * @param formatArgs the arguments {@code format} is applied to
         */
        public void panelLine(ColourEnum attribute, String label, String format, Object... formatArgs) {
            if (len == max) {
                logger.error("Too many lines requested for a panel.");
                throw new RuntimeException("Too many lines requested for a panel.");
            }

            String formatted = String.format(format, formatArgs);
            int stringLength = Math.min(formatted.length(), 19);
            PanelLine line = new PanelLine(attribute, label, formatted.substring(0, stringLength));
            lines.add(line);
            len++;
        }

        /**
         * Appends a blank spacer row to this panel and reserves its slot — the port of C's
         * {@code panel_space} ({@code [C] ui-player.c}, function {@code panel_space}).
         *
         * <p>Refuses to write past capacity: when {@link #len} has already reached {@link #max}
         * this logs a fatal message and throws a {@link RuntimeException}, standing in for C's
         * {@code assert(p->len != p->max)}, which only aborts in a debug build; this Java port
         * enforces the bound unconditionally, the same way {@link Panel#panelLine} does.
         *
         * <p>C's {@code panel_space} only increments {@code p->len}, because {@code p->lines} is a
         * fixed array {@code panel_allocate} pre-sizes to {@link #max} zeroed
         * {@code struct panel_line}s: walking past one leaves an already-existing blank slot behind
         * it, which the (not yet ported) {@code display_panel} renderer ({@code [C] ui-player.c},
         * function {@code display_panel}) skips printing but still counts as a row. Since
         * {@link #lines} is a growable {@link List} rather than a pre-sized array, this method
         * instead builds and appends that blank row itself — a {@link PanelLine} with an empty
         * {@code label} and {@code value} and an arbitrary {@link ColourEnum#COLOUR_WHITE} colour,
         * none of which C ever reads for a row {@code display_panel} skips — so that
         * {@link #lines}{@code .size()} keeps equalling {@link #len} and every row appended after a
         * spacer keeps its correct position.
         *
         * <p>Called once so far, by {@link UIPlayer#getPanelMidLeft()}, between its "Adv Exp" row
         * and its "Gold" row.
         *
         * <p>Method space coded on 260926, commented in full on 260926.
         */
        public void space() {
            if (len == max) {
                String message = "Trying to add a blank line after the end of the panel.";
                logger.fatal(message);
                throw new RuntimeException(message);
            }
            PanelLine line = new PanelLine(ColourEnum.COLOUR_WHITE, "", "");
            lines.add(line);
            len++;
        }
    }

    /**
     * One labelled row of a {@link Panel} — the port of C's {@code struct panel_line}
     * ({@code [C] ui-player.c:53-57}).
     *
     * <p>Built by {@link Panel#panelLine(ColourEnum, String, String, Object...)} on every call —
     * {@link UIPlayer#getPanelTopLeft()} builds six of these with real player data, and
     * {@link UIPlayer#getPanelMidLeft()} builds nine more: eight populated rows via
     * {@link Panel#panelLine(ColourEnum, String, String, Object...)} plus one blank spacer row via
     * {@link Panel#space()}. The {@code display_panel} renderer that would read a filled
     * {@link Panel} back and draw its {@link PanelLine}s to screen is not ported yet, so none
     * built so far reach the screen.
     *
     * <p>Class PanelLine coded before 260925, commented in full on 260925.
     */
    private class PanelLine {
        /**
         * The row's display colour — the Java form of C's {@code uint8_t attr}
         * ({@code [C] ui-player.c:54}), set from a {@code COLOUR_*} constant by every
         * {@code panel_line} call ({@code [C] ui-player.c:93, 105}).
         *
         * <p>Field attribute coded before 260925, commented in full on 260925.
         */
        ColourEnum attribute;
        /**
         * The row's fixed label text — the Java form of C's {@code const char *label}
         * ({@code [C] ui-player.c:55}), a caller-owned string literal {@code panel_line} stores by
         * reference rather than copying ({@code [C] ui-player.c:106}).
         *
         * <p>Field label coded before 260925, commented in full on 260925.
         */
        String label;
        /**
         * The row's formatted value text — the Java form of C's {@code char value[20]} field
         * ({@code [C] ui-player.c}, struct {@code panel_line}), a fixed 20-character buffer
         * {@code panel_line} fills via {@code vstrnfmt} from its {@code fmt}/varargs. {@code vstrnfmt}
         * ({@code z-form.c}, function {@code vstrnfmt}) stops appending content once it has written
         * 19 of that buffer's 20 bytes, reserving the last for the null terminator, so the value C
         * displays never exceeds 19 characters. {@link Panel#panelLine(ColourEnum, String, String,
         * Object...)} reproduces that same 19-character cap by clamping the {@link String#format}ted
         * value to {@code Math.min(formatted.length(), 19)} before storing it here.
         *
         * <p>Field value coded before 260925, commented in full on 260925.
         */
        String value;

        /**
         * Pairs a colour, label and already-formatted value into one row, storing all three as
         * given — the Java form of C's {@code panel_line} filling a freshly claimed
         * {@code struct panel_line} slot: {@code pl->attr = attr; pl->label = label;} plus the
         * {@code vstrnfmt} call that fills {@code pl->value}. Unlike C, this constructor does not
         * do the formatting or truncation itself — {@link Panel#panelLine(ColourEnum, String,
         * String, Object...)}, its only caller, has already built and clamped {@code value}
         * before calling this.
         *
         * <p>Constructor PanelLine coded on 260925, commented in full on 260925.
         *
         * @param attribute this row's display colour
         * @param label     this row's fixed label text
         * @param value     this row's already-formatted, already-truncated value text
         */
        public PanelLine(ColourEnum attribute, String label, String value) {
            this.attribute = attribute;
            this.label = label;
            this.value = value;
        }

        public ColourEnum getAttribute() {
            return attribute;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * One entry of the character-sheet panel layout table — the port of the anonymous
     * per-element struct type C declares inline for its file-scope {@code panels[]} array in
     * {@code ui-player.c}. Each instance pairs a screen region with the flag/builder needed to
     * fill it: {@link #bounds} and {@link #alignLeft} are read directly by the eventual port of
     * {@code display_panel}, while {@link #panelFunc} takes the place of C's function pointer,
     * called once per draw to obtain the single {@link Panel} that belongs in {@link #bounds}.
     *
     * <p>Not yet built or read anywhere — no code constructs a {@code panelRegions} or the
     * five-element table C assembles from it in {@code panels[]}, so there is no counterpart yet
     * to the {@code get_panel_topleft}/{@code get_panel_midleft}/{@code get_panel_combat}/
     * {@code get_panel_skills}/{@code get_panel_misc} suppliers C wires into that table.
     *
     * <p>Class panelRegions coded before 260925, commented in full on 260925.
     */
    private class PanelRegions {
        /**
         * The screen area this entry draws into — the Java form of C's {@code region bounds}
         * field inside {@code ui-player.c}'s {@code panels[]} element struct.
         *
         * <p>Field bounds coded before 260925, commented in full on 260925.
         */
        Region bounds;
        /**
         * Whether {@link #bounds} draws its {@link PanelLine} labels flush left rather than flush
         * right — the Java form of C's {@code bool align_left} field, read by the eventual port
         * of {@code display_panel} to choose which side of the region each label/value pair hugs.
         *
         * <p>Field alignLeft coded before 260925, commented in full on 260925.
         */
        boolean alignLeft;
        /**
         * Builds the single {@link Panel} that belongs in {@link #bounds} — the Java form of
         * C's function-pointer field {@code struct panel *(*panel)(void);}. C calls the pointer
         * directly at each draw ({@code panels[i].panel()}); {@link #getPanel()} calls
         * {@link Supplier#get()} in its place.
         *
         * <p>Field panelFunc coded before 260925, commented in full on 260925.
         */
        Supplier<Panel> panelFunc;

        /**
         * Pairs a region, its alignment, and its panel builder into one table entry, storing all
         * three by reference exactly as assigned — the Java form of C's brace-initialised
         * {@code panels[]} element ({@code ui-player.c}).
         *
         * <p>Constructor panelRegions coded before 260925, commented in full on 260925.
         *
         * @param bounds    the screen area this entry draws into
         * @param alignLeft whether this entry's labels align flush left rather than flush right
         * @param panelFunc the builder invoked to obtain this entry's {@link Panel}
         */
        public PanelRegions(Region bounds, boolean alignLeft, Supplier<Panel> panelFunc) {
            this.bounds = bounds;
            this.alignLeft = alignLeft;
            this.panelFunc = panelFunc;
        }

        /**
         * Returns this entry's screen area, the Java form of reading C's {@code region bounds}
         * field directly off a {@code panels[]} element.
         *
         * <p>Method getBounds coded before 260925, commented in full on 260925.
         *
         * @return the region this entry draws into
         */
        public Region getBounds() {
            return bounds;
        }

        /**
         * Returns whether this entry's labels align flush left, the Java form of reading C's
         * {@code bool align_left} field directly off a {@code panels[]} element.
         *
         * <p>Method isAlignLeft coded before 260925, commented in full on 260925.
         *
         * @return {@code true} if this entry's labels align flush left, {@code false} for flush
         * right
         */
        public boolean isAlignLeft() {
            return alignLeft;
        }

        /**
         * Invokes {@link #panelFunc} to build this entry's {@link Panel} — the Java form of C
         * calling its {@code panel} function pointer, e.g. {@code panels[i].panel()} in
         * {@code display_player_xtra_info} ({@code ui-player.c}).
         *
         * <p>Method getPanel coded before 260925, commented in full on 260925.
         *
         * @return a freshly built {@link Panel} for this entry's {@link #bounds}
         */
        public Panel getPanel() {
            return panelFunc.get();
        }
    }
}
