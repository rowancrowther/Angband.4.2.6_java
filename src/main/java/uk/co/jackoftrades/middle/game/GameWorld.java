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

package uk.co.jackoftrades.middle.game;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Generate;
import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
import uk.co.jackoftrades.middle.effect.EffectUtil;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
import uk.co.jackoftrades.middle.game.enums.CommandContext;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.monsters.MonsterTurn;
import uk.co.jackoftrades.middle.monsters.MonsterUtils;
import uk.co.jackoftrades.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.ObjectUtils;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerUtils;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

/**
 * The game-clock and turn-loop machinery — the port of C's {@code game-world.c}.
 *
 * <p>This is where the passage of game time and the per-turn processing live: how much energy an
 * actor banks each game turn for its speed, the day/night cycle, and (as the port grows) the
 * {@code process_world} / {@code process_player} / {@code run_game_loop} pass that drives every
 * creature's turn. It is deliberately kept separate from {@link GameState}: {@code GameState} owns the mutable
 * "current game" <em>data</em> (the turn counter, day count, RNG seeds, character-stage flags),
 * while {@code GameWorld} owns the <em>behaviour</em> that reads and advances it. In C both sat in
 * one file only because C uses file-scope globals as its singleton.
 *
 * <p>The one purely constant piece of this subsystem — the speed-to-energy lookup table — is
 * modelled here as {@link #extractEnergy}; unlike the values in {@link GameConstants} it is baked
 * into the source rather than loaded from {@code lib/gamedata}, exactly as in the original.
 *
 * @author Rowan Crowther
 */
public class GameWorld {
    /**
     * The player being driven this game, cached from {@link GameState#getPlayer()} at construction —
     * the port of C's file-scope {@code player} global. Stands in for the pointer that every
     * {@code game-world.c} function dereferences.
     */
    private Player player;
    /**
     * The level the player currently occupies, cached from {@link GameState#getCave()} at
     * construction — the port of C's file-scope {@code cave} global.
     *
     * <p><b>Known limitation:</b> C re-reads its {@code cave} global on every loop iteration, so it
     * always sees the freshly generated level after {@link Generate#prepareNextLevel(Player)}. This
     * cached copy goes stale across a level regeneration and must be refreshed (or read live from
     * {@link GameState#getCave()}) once {@link #processWorld()} does real work.
     */
    private Chunk currentCave;

    /**
     * Whether a playable dungeon level currently exists — the port of C's {@code character_dungeon}
     * global. Guards the level-teardown path in {@link #runGameLoop()} so {@link #onLeaveLevel()}
     * runs only when there is a level to leave. Set elsewhere (character birth / save load) once
     * those subsystems are ported; until then it stays {@code false}.
     */
    private boolean characterDungeon;

    /**
     * Energy gained per game turn as a function of speed, indexed directly by the speed value
     * (0–199, with 110 being normal speed) — the port of C's {@code extract_energy[200]}.
     *
     * <p>The scale is non-linear and deliberately so: below normal, speed is cheap and most
     * indices collapse to a single point of energy; above normal, each further step buys less, so
     * a normal actor banks 10 per turn, {@code +10} speed banks 20 (a true doubling), but the
     * gains flatten out and cap at 49 near the top of the table. Values are looked up rather than
     * computed to preserve those hand-tuned break-points exactly.
     */
    private static final int[] extractEnergy = {
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* S-50 */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* S-40 */     2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            /* S-30 */     2, 2, 2, 2, 2, 2, 2, 3, 3, 3,
            /* S-20 */     3, 3, 3, 3, 3, 4, 4, 4, 4, 4,
            /* S-10 */     5, 5, 5, 5, 6, 6, 7, 7, 8, 9,
            /* Norm */    10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            /* F+10 */    20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            /* F+20 */    30, 31, 32, 33, 34, 35, 36, 36, 37, 37,
            /* F+30 */    38, 38, 39, 39, 40, 40, 40, 41, 41, 41,
            /* F+40 */    42, 42, 42, 43, 43, 43, 44, 44, 44, 44,
            /* F+50 */    45, 45, 45, 45, 45, 46, 46, 46, 46, 46,
            /* F+60 */    47, 47, 47, 47, 47, 48, 48, 48, 48, 48,
            /* F+70 */    49, 49, 49, 49, 49, 49, 49, 49, 49, 49,
            /* Fast */    49, 49, 49, 49, 49, 49, 49, 49, 49, 49,
    };

    /**
     * The amount of energy gained in one game turn by an actor moving at the given speed — the
     * port of C's {@code turn_energy}.
     *
     * <p>Scales the raw table value from {@link #extractEnergy} by the world's move-energy
     * constant ({@link GameConstants#getWorldMoveEnergy}, C's {@code z_info->move_energy}) so the
     * cost of a "move" is data-driven while the speed curve stays fixed. The division is integer
     * division, matching the C semantics exactly.
     *
     * @param speed the actor's speed, used directly as the index into {@link #extractEnergy}
     *              (0–199, 110 = normal)
     * @return the energy banked this game turn at that speed
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int turnEnergy(int speed) {
        return extractEnergy[speed] * GameConstants.getWorldMoveEnergy() / 100;
    }

    /**
     * Binds this world to the current game by caching the live {@link GameState} player and cave —
     * the two globals ({@code player}, {@code cave}) that C's {@code game-world.c} reaches for
     * directly.
     */
    public GameWorld() {
        player = GameState.getPlayer();
        currentCave = GameState.getCave();
    }

    /**
     * The main game loop — the port of C's {@code run_game_loop} ({@code game-world.c}).
     *
     * <p>Runs one pass of the game clock: it finishes the player's just-issued command, then keeps
     * time moving until the player needs to enter another command, the character dies, or the game
     * stops. It is a pure orchestrator — every step delegates to a subsystem ({@link #processPlayer()},
     * {@link MonsterTurn#processMonsters(int)}, {@link #processWorld()}) — so the value here is the
     * <em>scheduling</em>: when each actor gets a turn, and in what order, driven entirely by banked
     * energy and a handful of upkeep flags.
     *
     * <p>The pass has five phases: tidy up after the last command
     * ({@link #processPlayerCleanup()}); run the player until they spend energy; let a fast (hasted)
     * player take extra turns ahead of the world while they still have {@code >= move_energy} banked;
     * then the {@code while (true)} world loop — monsters act, the world is processed every tenth
     * turn, the player banks energy and the turn counter advances, and a new level is generated when
     * requested — repeating until the player must act again.
     *
     * <p>The recurring {@code break}-versus-{@code return} idiom in the player sub-loops is the
     * subtle part: {@code break} means the player spent energy and the world should carry on;
     * {@code return} yields control back to the UI because a player pass used no energy and fresh
     * input is needed.
     */
    public void runGameLoop() {
        // Tidy up after the player's command
        processPlayerCleanup();

        // Keep processing the player until they use some energy or
        // another command is needed
        while (player.getPlayerUpkeep().isPlaying()) {
            processPlayer();
            if (player.getPlayerUpkeep().energyUse())
                break;
            else
                return;
        }

        // The player may still have enough energy to move, so run a player turn
        // before processing the rest of the world
        while (player.getEnergy() >= GameConstants.getWorldMoveEnergy()) {
            // Do any necessary animations
            GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_ANIMATE);

            // Process monster with more energy first
            MonsterTurn.processMonsters(player.getEnergy() + 1);
            if (player.isDead() || !player.getPlayerUpkeep().isPlaying() || player.getPlayerUpkeep().generateLevel())
                break;

            // process the player until they use some energy
            while (player.getPlayerUpkeep().isPlaying()) {
                processPlayer();
                if (player.getPlayerUpkeep().energyUse())
                    break;
                else
                    return;
            }
        }

        // Player turn fully complete, run the main loop until the player input is needed again
        while (true) {
            player.noticeStuff();
            player.handleStuff();
            GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_REFRESH);

            // Process the rest of the world
            // Give the player energy & increment turn counter
            // unless we need to stop playing or generate a new level
            if (player.isDead() || !player.getPlayerUpkeep().isPlaying())
                return;
            else if (!player.getPlayerUpkeep().generateLevel()) {
                MonsterTurn.processMonsters(0);

                // mark all monsters as ready to act when they have the energy
                MonsterTurn.resetMonsters();

                // refresh
                player.noticeStuff();
                player.handleStuff();
                GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_REFRESH);

                if (player.isDead() || !player.getPlayerUpkeep().isPlaying())
                    return;

                // process the world for the next 10 turns
                if (GameState.getTurn() % 10 == 0 && !player.getPlayerUpkeep().generateLevel()) {
                    processWorld();

                    player.noticeStuff();
                    player.handleStuff();
                    GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_REFRESH);
                    if (player.isDead() || !player.getPlayerUpkeep().isPlaying())
                        return;
                }

                // Player gains energy
                player.setEnergy(player.getEnergy() + turnEnergy(player.getPlayerState().getSpeed()));

                GameState.incrementTurn();
            }

            // If a new level is requested make one
            if (player.getPlayerUpkeep().generateLevel()) {
                boolean arena = false;
                if (characterDungeon) {
                    onLeaveLevel();
                    if (currentCave.getName() != null && "arena".equals(currentCave.getName()))
                        arena = true;
                }

                // Create a new cave, and then pull it into the GameWord
                Generate.prepareNextLevel(player);
                currentCave = GameState.getCave();
                onNewLevel();

                player.getPlayerUpkeep().setGenerateLevel(false);

                // Kill arena monster
                if (arena) {
                    player.getPlayerUpkeep().setArenaLevel(false);
                    if (player.getPlayerUpkeep().healthWho())
                        MonsterUtils.killArenaMonster(player.getPlayerUpkeep().getHealthWho());
                }
            }


            // If the player has enough energy to move, they do so, after any
            // monsters with more energy take their turns
            while (player.getEnergy() >= GameConstants.getWorldMoveEnergy()) {
                // Animate where required
                GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_ANIMATE);

                // Monsters with more energy move first
                MonsterTurn.processMonsters(player.getEnergy() + 1);
                if (player.isDead() || !player.getPlayerUpkeep().isPlaying() || player.getPlayerUpkeep().generateLevel())
                    break;

                // Process the player until they use energy
                while (player.getPlayerUpkeep().isPlaying()) {
                    processPlayer();
                    if (player.getPlayerUpkeep().energyUse())
                        break;
                    else
                        return;
                }
            }
        }
    }

    /**
     * Housekeeping on arriving on a new level — the port of C's {@code on_new_level}
     * ({@code game-world.c}).
     *
     * <p><b>Stub:</b> not yet implemented. When ported it must, for a genuine level change (not an
     * arena), cancel the target and health-bar trackee and disturb the player; then track the
     * player's maximum level and maximum/recall depth; and finally flush messages and signal the
     * new-level display refresh.
     */
    private void onNewLevel() {
        // Stub class TODO: implement
    }

    /**
     * Housekeeping on leaving a level — the port of C's {@code on_leave_level} ({@code game-world.c}).
     *
     * <p>Cancels any in-progress command ({@link TimedEffect#TMD_COMMAND}), then runs the pending
     * notice/update/redraw passes (needed here because leaving may have changed inventory or state)
     * and flushes queued messages. Note it is deliberately {@code notice → update → redraw}, mirroring
     * C's three separate calls, rather than the bundled {@link Player#handleStuff()}.
     *
     * <p><b>Partial:</b> the {@code cmd_disable_repeat_floor_item} guard is not yet ported.
     */
    private void onLeaveLevel() {
        player.clearTimed(TimedEffect.TMD_COMMAND, false, false);

        // TODO implement
        // cmdDisableRepeatFloorItem();

        player.noticeStuff();
        player.updateStuff();
        player.redrawStuff();

        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);
    }

    /**
     * Process player commands from the command queue — the port of C's {@code process_player}
     * ({@code game-world.c}).
     *
     * <p>Finishes when a command actually uses energy (any normal game action), when the queue runs
     * dry and fresh input is needed, or when the character changes level, dies, or the game stops.
     * Each pass of the {@code do…while} refreshes the display, handles pack overflow, resets the
     * assumed-free-turn energy, applies the couple of automatic effects that fire before a command
     * (dwarven ore detection; a forced {@code CMD_SLEEP} while paralyzed or knocked out), then pulls
     * and dispatches one command via {@link uk.co.jackoftrades.middle.game.gameengine.CommandQueue#commandPop}.
     * It loops while no energy was spent and the player is neither dead nor awaiting a new level.
     *
     * <p>Several steps delegate to subsystems not yet ported and currently call stubs:
     * {@link PlayerUtils#restingCompleteSpecial(Player)}, {@link ObjectUtils#packOverflow},
     * {@link EffectUtil#effectSimple} (the ore-detection effect), and
     * {@link Player#timedGradeEqual(TimedEffect, String)}.
     */
    private void processPlayer() {
        // check for interrupts
        PlayerUtils.restingCompleteSpecial(player);
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_CHECK_INTERRUPT);

        // repeat until energy is reduced
        do {
            //refresh
            player.noticeStuff();
            player.handleStuff();
            GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_REFRESH);

            // Pack overflow
            ObjectUtils.packOverflow(null);

            // Assume free turn
            player.getPlayerUpkeep().setEnergyUse(0);

            // Detect treasure for dwarves
            if (player.hasPlayerFlag(PlayerFlag.PF_SEE_ORE)) {
                // if they are healthy
                if (player.getTimedEffect(TimedEffect.TMD_IMAGE) == 0 &&
                        player.getTimedEffect(TimedEffect.TMD_CONFUSED) == 0 &&
                        player.getTimedEffect(TimedEffect.TMD_AMNESIA) == 0 &&
                        player.getTimedEffect(TimedEffect.TMD_STUN) == 0 &&
                        player.getTimedEffect(TimedEffect.TMD_PARALYZED) == 0 &&
                        player.getTimedEffect(TimedEffect.TMD_TERROR) == 0 &&
                        player.getTimedEffect(TimedEffect.TMD_AFRAID) == 0) {
                    EffectUtil.effectSimple(EffectEnum.EF_DETECT_ORE, null, "0",
                            EffectSubTypeEnum.EST_NONE, 0, 0, 3, 3, null);
                }
            }

            // Paralyzed or knocked out players get no turn
            if (player.getTimedEffect(TimedEffect.TMD_PARALYZED) != 0 ||
                    player.timedGradeEqual(TimedEffect.TMD_STUN, "Knocked Out")) {
                GameState.getCommandQueue().push(CommandCode.CMD_SLEEP);
            }

            // Prepare for the next command
            if (GameState.getCommandQueue().getNrepeats() > 0)
                GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_COMMAND_REPEAT);
            else {
                // Check monster recall
                if (player.getPlayerUpkeep().getMonsterRace() != null)
                    player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_MONSTER);

                GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_REFRESH);
            }

            // Get a command from the queue if there is one
            if (!GameState.getCommandQueue().commandPop(CommandContext.CTX_GAME))
                break;

            if (!player.getPlayerUpkeep().isPlaying())
                break;

            processPlayerCleanup();
        } while (!player.getPlayerUpkeep().energyUse() &&
                !player.isDead() &&
                !player.getPlayerUpkeep().generateLevel());

        // If needed, notice stuff
        player.noticeStuff();
    }

    /**
     * Tidy up after the player's command — the port of C's {@code process_player_cleanup}
     * ({@code game-world.c}), called at the top of {@link #runGameLoop()} and after every dispatched
     * command inside {@link #processPlayer()}.
     *
     * <p><b>Stub:</b> not yet implemented. When ported it must, if the command actually used energy,
     * deduct that energy and add it to the running total, decay the bloodlust skip-coercion counter,
     * apply any terrain damage, and (unless the player auto-dropped) flag the map for hallucination
     * and refresh multi-hued / marked monsters. In all cases it clears each monster's per-turn
     * {@code SHOW} flag and the drop status, then runs the update and redraw passes.
     */
    private void processPlayerCleanup() {
        // Significant
        if (player.getPlayerUpkeep().energyUse()) {
            // Use some energy
            player.setEnergy(player.getEnergy() - player.getPlayerUpkeep().getEnergyUse());

            // increment the total energy counter
            player.setTotalEnergy(player.getTotalEnergy() + player.getPlayerUpkeep().getEnergyUse());

            /*
             * Since the player used energy, the command wasn't
             * cancelled.  Therefore, allow the bloodlust check on
             * the player's next command unless this was a background
             * command and the last player-issued command passed the
             * bloodlust check but was cancelled (skip_cmd_coercion is two
             * in that case).
             */
            if (player.getSkipCmdCoercion() != 0) {
                player.setSkipCmdCoercion(player.getSkipCmdCoercion() - 1);
            }

            // Has the player taken terrain damage
            PlayerUtils.takeTerrainDamage(player, player.getGrid());

            // Do nothing else if the player has auto-dropped stuff
            if (!player.getPlayerUpkeep().getDropping()) {
                if (player.getTimedEffect(TimedEffect.TMD_IMAGE) != 0)
                    player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_MAP);

                // Shimmer multi-hued monsters
                for (Monster monster : currentCave.getMonsters()) {
                    if (monster == null)
                        continue;
                    if (monster.getMonsterRace() == null)
                        continue;
                    if (!monster.getMonsterRace().hasMonsterRaceFlag(MonsterRaceFlag.RF_ATTR_MULTI))
                        continue;
                    currentCave.squareLightSpot(monster.getGrid());

                }

                // Clear the NICE flag and show marked monsters
                for (Monster monster : currentCave.getMonsters()) {
                    if (monster == null)
                        continue;
                    monster.monsterFlagOff(MonsterFlag.MFLAG_NICE);
                    if (monster.hasMonsterFlag(MonsterFlag.MFLAG_MARK)) {
                        if (!monster.hasMonsterFlag(MonsterFlag.MFLAG_SHOW)) {
                            monster.monsterFlagOff(MonsterFlag.MFLAG_MARK);
                            MonsterUtils.updateMonster(monster, currentCave, false);
                        }
                    }
                }
            }
        } else if (player.getSkipCmdCoercion() > 1) {
            /*
             * The last command was a backround command executing while
             * skipping the bloodlust check on the player's next command.
             * Set skip_cmd_coercion back to one in preparation for the
             * player's next turn.
             */
            player.setSkipCmdCoercion(1);
        }

        // Clear the SHOW flag and player drop status
        for (Monster monster : currentCave.getMonsters()) {
            if (monster != null) {
                monster.monsterFlagOff(MonsterFlag.MFLAG_SHOW);
            }
        }
        player.getPlayerUpkeep().setDropping(false);

        // HACK! update needed first because inventory may have changed
        player.updateStuff();
        player.redrawStuff();
    }

    /**
     * Process the passage of time on the level — the port of C's {@code process_world}
     * ({@code game-world.c}), run once every ten game turns from {@link #runGameLoop()}.
     *
     * <p><b>Stub:</b> not yet implemented. When ported it drives the slow, world-scale clock:
     * day/night and town-store restocking, the recharge of the player's light and regeneration,
     * timed-effect decay, random monster generation, and the other once-per-ten-turns upkeep.
     */
    private void processWorld() {
        // Stub class: TODO - Implement
    }
}