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
import uk.co.jackoftrades.middle.enums.DamageAspect;
import uk.co.jackoftrades.middle.numerics.RandomValueUtils;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.ChunkUtils;
import uk.co.jackoftrades.middle.cave.Generate;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;
import uk.co.jackoftrades.middle.combat.Target;
import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
import uk.co.jackoftrades.middle.effect.EffectUtil;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
import uk.co.jackoftrades.middle.game.enums.CommandContext;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.middle.game.event.projection.Source;
import uk.co.jackoftrades.middle.game.event.projection.SourceWhat;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.Food;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.registry.StatTables;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.monsters.MonsterTurn;
import uk.co.jackoftrades.middle.monsters.MonsterUtils;
import uk.co.jackoftrades.middle.monsters.enums.MonTimed;
import uk.co.jackoftrades.middle.monsters.enums.MonTimedFlags;
import uk.co.jackoftrades.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.CurseData;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectUtils;
import uk.co.jackoftrades.middle.objects.enums.ObjectDescription;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.EquipSlot;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerUtils;
import uk.co.jackoftrades.middle.player.enums.*;

import java.util.ArrayDeque;
import java.util.Map;

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
    private static boolean characterDungeon;

    /**
     * How many whole days have passed while the player has been below the town — the port of C's
     * {@code daycount} global ({@code game-world.c}).
     *
     * <p>Counted here and spent elsewhere. The stores restock a day at a time, but doing that while
     * the player is in the dungeon would let the knowledge menu show tomorrow's stock, so the days
     * are banked instead and worked off on the return to town: C's {@code store_update}
     * ({@code store.c:1421}) runs its maintenance loop {@code daycount} times and then zeroes the
     * counter. Only the dungeon arm of the turn increments it — in town the stores are simply kept
     * current.
     *
     * <p>Field dayCount coded before 260817, commented in full on 260817.
     */
    private int dayCount;

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
        dayCount = 0;
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
     * Advances the recharge timers on everything that has one — the port of C's
     * {@code recharge_objects} ({@code game-world.c:197-251}), run once per game turn.
     *
     * <p>Three groups are handled, and the differences between them are the point of the method.
     *
     * <p><b>Worn equipment</b> recharges unconditionally: an activatable item is the only thing in a
     * slot that can be on cooldown, so no type test is needed. Anything that becomes ready is
     * announced and the equipment display is marked dirty.
     *
     * <p><b>Carried items</b> are filtered to types that can hold a timeout — in practice rods,
     * whose stacks recharge one item at a time. Because a stack is a single object with a pooled
     * timeout, "did anything become ready" is not simply "did the timer reach zero": a stack of
     * three rods with one charge left still has two charging. So the count of charging items is
     * taken <em>before</em> the tick, and two separate cases are reported — the timer reaching zero
     * means the whole stack is ready ({@code all} true), while a stack that was fully exhausted
     * beforehand and is now not means one rod has come back ({@code all} false). Only that second
     * case needs the earlier reading, which is why it is captured up front. A recharge here also
     * requests a pack combine, since a newly charged rod may now stack with others.
     *
     * <p><b>Objects lying on the floor</b> tick down too, so rods dropped and picked up later are
     * not frozen mid-recharge, but nothing is announced: the player cannot see them.
     *
     * <p>The equipped/carried split is a single pass over the gear list in both trees, because C
     * keeps worn and carried objects on one {@code player->gear} chain and distinguishes them by
     * asking which slot holds them. Items with no kind are skipped; C asserts on them instead
     * ({@code game-world.c:206}), the port simply passes over them.
     */
    public void rechargeObjects() {
        boolean dischargedStack;

        for (ItemObject item : player.getGear()) {
            // Skip items with null kinds
            if (item.getKind() == null)
                continue;

            // Recharge equipment
            if (player.getPlayerBody().itemIsEquipped(item)) {
                // Recharge activatable objects
                if (item.rechargeTimeout()) {
                    // Message if an item recharged
                    rechargedNotice(item, true);

                    // Window stuff
                    player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_EQUIP);
                }
            } else {
                // Recharge the inventory
                dischargedStack = item.numberCharging() == item.getNumber();

                // Recharge rods, and update if any rods are recharged
                if (item.gettValue().canHaveTimeout() && item.rechargeTimeout()) {
                    // entire stack is recharged
                    if (item.getTimeout() == 0)
                        rechargedNotice(item, true);
                    else if (dischargedStack) // Previously exhasted stack has acquired a charge
                        rechargedNotice(item, false);

                    // Combine pack
                    player.getPlayerUpkeep().noticeFlagOn(PlayerNotice.PN_COMBINE);

                    // Redraw stuff
                    player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_INVEN);
                }
            }
        }

        // Recharge other level objects
        for (ItemObject item : currentCave.getObjects()) {
            if (item.gettValue().canHaveTimeout())
                item.rechargeTimeout();
        }
    }

    /**
     * Tells the player that an item has finished recharging — the port of C's
     * {@code recharged_notice} ({@code game-world.c:147-190}).
     *
     * <p>The player is only told if they asked to be. Either the {@code notify_recharge} option is
     * on, in which case every recharge is announced, or the item itself carries {@code "!!"}
     * somewhere in its inscription — the long-standing convention for "tell me when this is ready".
     * C finds it by walking the inscription for a {@code '!'} followed by another
     * ({@code game-world.c:157-172}); that loop is a substring search, so {@code "@w1!!"} and
     * {@code "!!kill"} both qualify, not just a bare {@code "!!"}. If neither applies the method
     * returns without describing the item, which is the common case and worth keeping cheap.
     *
     * <p>When it does report, it first disturbs the player, so a recharge interrupts resting or
     * running rather than scrolling past unseen. The wording distinguishes three cases: a stack
     * reports whether all of it or only one item recharged (hence {@code all}), a singleton artifact
     * takes "The" because its name is already definite, and any other singleton takes "Your".
     *
     * @param item the object that has just recharged
     * @param all  {@code true} if the whole stack is now charged, {@code false} if a previously
     *             exhausted stack has regained its first charge
     */
    private void rechargedNotice(ItemObject item, boolean all) {
        boolean notify = false;

        String itemNote = item.getNote();

        if (player.getPlayerOptions().has(PlayerOptionEnum.OP_notify_recharge))
            notify = true;
        else if (itemNote != null && itemNote.contains("!!")) {
            notify = true;
        }

        if (!notify) return;

        // Describe (briefly)
        Flag<ObjectDescription> flag = new Flag<>(ObjectDescription.class);
        String oName = ObjectUtils.objectDesc(item, flag, player);

        // Disturb the player
        PlayerUtils.disturb();

        // Notify the player
        if (item.getNumber() > 1) {
            if (all)
                Message.message("Your %s have recharged.", oName);
            else
                Message.message("One of your %s has recharged.", oName);
        } else if (item.isArtifact())
            Message.message("The %s has recharged.", oName);
        else
            Message.message("Your %s has recharged.", oName);
    }

    /**
     * Housekeeping on arriving on a new level — the port of C's {@code on_new_level}
     * ({@code game-world.c}). Called from {@link #runGameLoop()} immediately after the new level has
     * been generated and {@link #currentCave} refreshed, so it reads the already-current level rather
     * than regenerating anything.
     *
     * <p>For a genuine level change (not an arena, which is not really a new level) it plays the
     * ambient sound for the new location, cancels the target and the health-bar trackee, then — for
     * every arrival — disturbs the player and advances the tracked maximum player level and maximum /
     * recall depth. It flushes queued messages, signals the new-level display, marks the player for a
     * bonus/HP/spell/inventory recalculation and an inventory combine, runs the notice/update/redraw
     * passes, and signals a refresh.
     *
     * <p>Arena levels return here. Otherwise, if the player is in the dungeon (depth {@code != 0}) the
     * level feeling is announced, the surroundings are searched, and the player's energy is raised to
     * at least {@link GameConstants#getWorldMoveEnergy() move-energy} so they can act on arrival —
     * without ever <em>reducing</em> a higher value carried over from a savefile (hence
     * {@link Math#max}, matching C's {@code if (energy < move_energy) energy = move_energy}).
     */
    private void onNewLevel() {
        // Arena levels are not really a level change
        if (!player.getPlayerUpkeep().isArenaLevel()) {
            // Play ambient sound on a change of level
            playAmbientSound();

            // Cancel the target
            Target.setMonster(null);

            // Cancel the health bar
            player.getPlayerUpkeep().healthTrack(null);
        }

        // Disturb
        PlayerUtils.disturb();

        // Track maximum player level
        player.updateMaxLevel();

        // Track maximum dungeon level
        player.updateDungeonDepth();

        // Flush messages
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

        // Update display
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_NEW_LEVEL_DISPLAY);

        // Update player
        player.getPlayerUpkeep().updateFlagsOn(PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_HP,
                PlayerUpdateEnum.PU_SPELLS, PlayerUpdateEnum.PU_INVEN);
        player.getPlayerUpkeep().noticeFlagOn(PlayerNotice.PN_COMBINE);
        player.noticeStuff();
        player.updateStuff();
        player.redrawStuff();

        // Refresh
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_REFRESH);

        if (player.getPlayerUpkeep().isArenaLevel()) return;

        // Announce or repeat feeling
        //
        // currentCave was refreshed in the calling function so is safe to call here
        if (player.getDepth() != 0)
            currentCave.displayFeeling(false);

        // Check the surroundings
        PlayerUtils.search();

        // Give player minimum energy to start a new level (they arrive exhausted from
        // the stairs?) but do not reduce a higher level from savefile for a level in
        // progress.
        player.setEnergy(Math.max(player.getEnergy(), GameConstants.getWorldMoveEnergy()));
    }

    /**
     * Housekeeping on leaving a level — the port of C's {@code on_leave_level} ({@code game-world.c}).
     *
     * <p>Cancels any in-progress command ({@link TimedEffect#TMD_COMMAND}) and forbids repeating the
     * last command if it acted on a floor item — via {@link
     * uk.co.jackoftrades.middle.game.gameengine.CommandQueue#disableRepeatFloorItem()}, since leaving
     * the level may leave that item's reference dangling. It then runs the pending notice/update/redraw
     * passes (needed here because leaving may have changed inventory or state) and flushes queued
     * messages. Note it is deliberately {@code notice → update → redraw}, mirroring C's three separate
     * calls, rather than the bundled {@link Player#handleStuff()}.
     */
    private void onLeaveLevel() {
        // Cancel any command
        player.clearTimed(TimedEffect.TMD_COMMAND, false, false);

        // Don't allow command repeat if move away from item used.
        GameState.getCommandQueue().disableRepeatFloorItem();

        // Any pending processing
        player.noticeStuff();
        player.updateStuff();
        player.redrawStuff();

        // Flush messages
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);
    }

    /**
     * Whether a dungeon level currently exists for the character — the port of reading C's
     * {@code character_dungeon} global ({@code game-world.h:37}).
     *
     * <p>The flag distinguishes "a character is in play on a level" from the moments either side of
     * it: character creation, save loading, and the gap between levels. Two of {@code calcBonuses}'
     * final adjustments are conditioned on it — the {@code PF_UNLIGHT} dark resistance and the
     * {@code PF_EVIL} nether/holy-orb pair ({@code player-calcs.c:2042-2051}) — because both look
     * at the player's surroundings, and there are none to look at before a level exists.
     *
     * <p>Function hasCharacterDungeon commented in full on 260820.
     *
     * @return {@code true} once a level has been generated for the character
     */
    public static boolean hasCharacterDungeon() {
        return characterDungeon;
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
            PlayerUtils.takeTerrainDamage(player.getGrid());

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
        int index;
        int y;
        int x;

        // Compact the monster list if we're aproaching the limit
        if (currentCave.monsterCount() + 32 > GameConstants.getLevelMaxMonsters())
            currentCave.compactMonsters(64);

        // Compact the monster list if it is too sparce
        if (currentCave.monsterCount() + 32 < currentCave.getMonMax())
            currentCave.compactMonsters(0);

        // Check the time
        int turn = GameState.getTurn();

        // Play an ambient sound at regular intervals
        if ((turn % ((10L * GameConstants.getWorldDayLength()) / 4) == 0))
            playAmbientSound();

        // Handle stores and sunshine
        if (player.getDepth() == 0) {
            // daybreak/nightfall in town
            if ((turn % ((10L * GameConstants.getWorldDayLength()) / 2)) == 0) {
                // dawn or dusk
                boolean dawn = ((turn % (10L * GameConstants.getWorldDayLength())) == 0);

                if (dawn)
                    Message.message("The sun has risen.");
                else
                    Message.message("The sun has fallen.");

                currentCave.illuminate(dawn);
            }
        } else {
            /* Update the stores once a day (while in the dungeon).
               The changes are not actually made until return to town,
               to avoid giving details away in the knowledge menu. */
            if ((turn % (10L * GameConstants.getStoreTurns())) == 0) dayCount++;
        }

        // Check for light change
        if (player.hasPlayerFlag(PlayerFlag.PF_UNLIGHT))
            player.getPlayerUpkeep().updateFlagOn(PlayerUpdateEnum.PU_BONUS);

        // Check for creature generation
        if (RandomValueUtils.oneIn(GameConstants.getMonGenChance()))
            currentCave.pickAndPlaceDistantMonster(player.getGrid(),
                    GameConstants.getPlayerMaxSight() + 5, true, player.getDepth());

        // Damage or healing over time

        // From poison
        if (player.getTimedEffect(TimedEffect.TMD_POISONED) != 0) {
            PlayerUtils.takeHit(PlayerUtils.applyDamageReduction(1), "poison");
            if (player.isDead()) return;
        }

        // From cuts etc
        if (player.getTimedEffect(TimedEffect.TMD_CUT) != 0) {
            if (player.hasPlayerFlag(PlayerFlag.PF_ROCK))
                index = 0;
            else if (player.timedGradeEq(TimedEffect.TMD_CUT, "Mortal Wound") ||
                    player.timedGradeEq(TimedEffect.TMD_CUT, "Deep Gash"))
                index = 3;
            else if (player.timedGradeEq(TimedEffect.TMD_CUT, "Severe Cut"))
                index = 2;
            else
                index = 1;

            PlayerUtils.takeHit(PlayerUtils.applyDamageReduction(index), "a fatal wound");
            if (player.isDead()) return;
        }

        // Side effects of losing bloodlust
        if (player.getTimedEffect(TimedEffect.TMD_BLOODLUST) != 0) {
            Flag<PlayerOverExertion> overExertionFlag = new Flag<>(PlayerOverExertion.class);
            overExertionFlag.set(PlayerOverExertion.PY_EXERT_HP,
                    PlayerOverExertion.PY_EXERT_CUT, PlayerOverExertion.PY_EXERT_SLOW);
            PlayerUtils.overExert(overExertionFlag,
                    Math.max(0, 10 - player.getTimedEffect(TimedEffect.TMD_BLOODLUST)),
                    player.getCurrentHP() / 10);
            if (player.isDead()) return;
        }

        // Timed healing
        if (player.getTimedEffect(TimedEffect.TMD_HEAL) != 0) {
            boolean ident = false;
            Source playerSource = new Source(SourceWhat.SRC_PLAYER, null);
            EffectUtil.effectSimple(EffectEnum.EF_HEAL_HP, playerSource, "30",
                    EffectSubTypeEnum.EST_NONE, 0, 0, 0, 0, ident);
        }

        // Black Breath
        if (player.getTimedEffect(TimedEffect.TMD_BLACKBREATH) != 0) {
            if (RandomValueUtils.oneIn(2)) {
                Message.message("The Black Breath sickens you.");
                player.statDec(Stats.STAT_CON, false);
            }

            if (RandomValueUtils.oneIn(2)) {
                Message.message("The Black Breath saps your strength.");
                player.statDec(Stats.STAT_STR, false);
            }

            if (RandomValueUtils.oneIn(2)) {
                // Life drain
                int drain = 100 + (player.getExp() / 100) * GameConstants.getMonPlayLifeDrain();
                Message.message("The Black Breath dims your life force.");
                player.expLose(drain, false);
            }
        }

        // Check food and regenerate

        // Digest
        if (!player.timedGradeEq(TimedEffect.TMD_FOOD, "Full")) {
            // Digest normally
            if ((turn % 100) == 0) {
                // basic digestion rate based on speed
                int digestAmount = turnEnergy(player.getPlayerState().getSpeed());

                // Adjust for food value
                digestAmount = (digestAmount * 100) / GameConstants.getPlayerFoodValue();

                // Regenration takes more food
                if (player.hasObjectFlag(ObjectFlag.OF_REGEN)) digestAmount *= 2;

                // Slow digestion takes less food
                if (player.hasObjectFlag(ObjectFlag.OF_SLOW_DIGEST)) digestAmount /= 2;

                // Minimal digestion
                if (digestAmount < 1) digestAmount = 1;

                // Digest some food
                player.decTimed(TimedEffect.TMD_FOOD, digestAmount, false, true);
            }

            // Fast metabolism
            if (player.getTimedEffect(TimedEffect.TMD_HEAL) != 0) {
                player.decTimed(TimedEffect.TMD_FOOD, 8 * GameConstants.getPlayerFoodValue(), false, true);
                if (player.getTimedEffect(TimedEffect.TMD_FOOD) < Food.PY_FOOD_HUNGRY.getFoodValue()) {
                    player.setTimed(TimedEffect.TMD_HEAL, 0, true, true);
                }
            }

        } else { // Digest quicker when gorged
            player.decTimed(TimedEffect.TMD_FOOD, 5000 / GameConstants.getPlayerFoodValue(), false, true);
            player.getPlayerUpkeep().updateFlagOn(PlayerUpdateEnum.PU_BONUS);
        }

        // Faint or starving
        if (player.timedGradeEq(TimedEffect.TMD_FOOD, "Faint")) {
            // Faint occasionally
            if (player.getTimedEffect(TimedEffect.TMD_PARALYZED) == 0 && RandomValueUtils.oneIn(10)) {
                Message.message("You faint from the lack of food.");
                PlayerUtils.disturb();

                // Faint - bypass free action
                player.incTimed(TimedEffect.TMD_PARALYZED, 1 + RandomValueUtils.randInt0(5),
                        true, true, false);
            }
        } else if (player.timedGradeEq(TimedEffect.TMD_FOOD, "Starving")) {
            int damage = (Food.PY_FOOD_STARVING.getFoodValue() - player.getTimedEffect(TimedEffect.TMD_FOOD)) / 10;

            PlayerUtils.takeHit(PlayerUtils.applyDamageReduction(damage), "starvation");

            if (player.isDead()) return;
        }

        // Regenerate HP if needed
        if (player.getCurrentHP() < player.getMaxHP())
            PlayerUtils.regenHP();

        // Regenrate or lose mana
        PlayerUtils.regenMana();

        // Timeout various things
        decreaseTimeouts();

        // Proess light
        PlayerUtils.updateLight();

        // Update noise and scent if player isn't resting
        if (!player.isResting()) {
            makeNoise();
            updateScent();
        }

        // Process Inventory

        // Handle experience draining
        if (player.hasObjectFlag(ObjectFlag.OF_DRAIN_EXP)) {
            if (player.getExp() > 0 && RandomValueUtils.oneIn(10)) {
                int damage = RandomValueUtils.damRoll(10, 6) + (player.getExp() / 100) * GameConstants.getMonPlayLifeDrain();
                player.expLose(damage / 10, false);
            }

            player.equipLearnFlag(ObjectFlag.OF_DRAIN_EXP);
        }

        // Recharge activatable objects and rods
        rechargeObjects();

        // Notice things after time
        if (turn % 100 == 0)
            ObjectUtils.equipLearnAfterTime(player);

        // Decrease trap timeouts
        currentCave.decreaseTrapTimeout();

        // Involuntary movement
        if (player.getWordRecall() != 0 && !player.getPlayerUpkeep().isArenaLevel()) {
            player.decrementWordRecall();

            // recalled?
            if (player.getWordRecall() == 0) {
                // Disturb and flush command queue to avoid losing an action
                // on the new level
                PlayerUtils.disturb();
                GameState.getCommandQueue().flush();

                // Determine the level
                if (player.getDepth() != 0) {
                    Message.messageType(MessageType.MSG_TPLEVEL, "You feel yourself yanked upwards!");
                    PlayerUtils.dungeonChangeLevel(0);
                } else {
                    Message.messageType(MessageType.MSG_TPLEVEL, "You feel yourself yanked downwards!");
                    player.setRecallDepth();
                    PlayerUtils.dungeonChangeLevel(player.getRecallDepth());
                }
            }
        }

        // Delayed Deep Descent
        if (player.getDeepDescent() != 0) {
            // Count down towards descent
            player.decrementDeepDescent();

            // Activate descent
            if (player.getDeepDescent() == 0) {
                // Calculate the target depth
                int targetIncrement = (4 / GameConstants.getWorldStairSkip()) + 1;
                int targetDepth = PlayerUtils.dungeonGetNextLevel(player.getMaxDepth(), targetIncrement);
                PlayerUtils.disturb();

                // Determine the level
                if (targetDepth > player.getDepth()) {
                    Message.messageType(MessageType.MSG_TPLEVEL, "The floor opens beneath you!");
                    PlayerUtils.dungeonChangeLevel(targetDepth);
                } else { // Do something disastrous
                    Message.messageType(MessageType.MSG_TPLEVEL, "You aer thrown back in an explosion");
                    Source sourceNone = new Source(SourceWhat.SRC_NONE, null);
                    EffectUtil.effectSimple(EffectEnum.EF_DESTRUCTION, sourceNone, "0",
                            EffectSubTypeEnum.EST_NONE, 5, 0, 0, 0, null);
                }
            }
        }
    }

    /**
     * Age the player's timed effects and equipped-item curses by one game turn.
     * The Java port of the C original's {@code decrease_timeouts}
     * ({@code src/game-world.c}).
     *
     * <p>Two passes run:</p>
     * <ol>
     *   <li><b>Timed effects.</b> Every {@link TimedEffect} the player currently
     *       has is decremented, normally by one. A CON-derived {@code adjust}
     *       (from {@link StatTables#adjConFix} indexed by the player's CON stat
     *       index, plus one) sets the faster recovery rate for bleeding
     *       ({@code TMD_CUT}), poison and stun. Special cases: hunger
     *       ({@code TMD_FOOD}) is aged elsewhere so decrements by zero here; a
     *       "Mortal Wound" and {@code PF_ROCK} races do not bleed down; and
     *       {@code TMD_COMMAND} keeps the commanded monster's timer in step,
     *       breaking the command outright once the monster leaves line of
     *       sight.</li>
     *   <li><b>Curses.</b> For each occupied equipment slot, every active curse
     *       on the worn item ticks its {@link CurseData} timeout down by one;
     *       when it reaches zero the curse's effect fires (teaching the player
     *       the curse if it did anything visible) and the timeout is re-rolled
     *       from the curse template's interval.</li>
     * </ol>
     */
    private void decreaseTimeouts() {
        int adjust = (StatTables.adjConFix[player.getPlayerState().getStatInd(Stats.STAT_CON)] + 1);

        // Most timed effects decrement by 1
        for (TimedEffect effect : TimedEffect.values()) {
            int decrement = 1;
            if (player.getTimedEffect(effect) == 0)
                continue;

            // special cases
            switch (effect) {
                case TMD_FOOD -> decrement = 0; // handle separately
                case TMD_CUT -> {
                    // Check for truely mortal wounds
                    if (player.timedGradeEq(effect, "Mortal Wound"))
                        decrement = 0;
                    else
                        decrement = adjust;

                    // Rock players don't bleed
                    if (player.hasPlayerFlag(PlayerFlag.PF_ROCK))
                        decrement = 0;
                }
                case TMD_POISONED, TMD_STUN -> decrement = adjust;
                case TMD_COMMAND -> {
                    Monster monster = MonsterUtils.getCommandMonster();
                    if (!ChunkUtils.los(currentCave, player.getGrid(), monster.getGrid())) {
                        Flag<MonTimedFlags> notify = new Flag<>(MonTimedFlags.class);
                        notify.on(MonTimedFlags.MON_TMD_FLG_NOTIFY);
                        monster.clearTimed(MonTimed.MON_TMD_COMMAND, notify);
                        player.clearTimed(TimedEffect.TMD_COMMAND, true, true);
                    } else {
                        monster.decrementTimed(MonTimed.MON_TMD_COMMAND, decrement, new Flag<>(MonTimedFlags.class));
                    }
                }
            }

            // decrement the effect
            player.decTimed(effect, decrement, false, true);
        }

        // Curse effects always decrement by 1
        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            CurseData curseData = null;
            if (slot.getItem() == null) continue;

            Map<Curse, CurseData> curses = slot.getItem().getCurses();
            if (!curses.isEmpty()) {
                for (Curse curse : curses.keySet()) {
                    curseData = curses.get(curse);
                    if (curseData.getPower() != 0) {
                        curseData.decrementTimeout();
                        if (curseData.getTimeout() == 0) {
                            if (ObjectUtils.doCurseEffect(curse, slot.getItem()))
                                player.learnCurse(curse);
                            curseData.setTimeout(curse.getEffect().getTime().randCalc(0, DamageAspect.RANDOMIZE));
                        }
                    }
                }
            }
        }
    }

    /**
     * Plays the ambient background sound appropriate to where the player is. Ports C's
     * {@code play_ambient_sound} ({@code src/game-world.c}).
     * <p>
     * In the town (depth {@code 0}) the sound depends on the time of day — {@link
     * MessageType#MSG_AMBIENT_DAY} or {@link MessageType#MSG_AMBIENT_NITE} via {@link #isDaytime()}.
     * In the dungeon it depends on depth, in bands of 20 levels: {@code MSG_AMBIENT_DNG1} for the
     * first band (depth {@code 1}–{@code 20}) up to {@code MSG_AMBIENT_DNG5} beyond depth {@code 80}.
     * Each is emitted through {@link Message#sound}, which the front end hooks to play the audio.
     * This is purely a sound cue — it changes no game state.
     */
    private void playAmbientSound() {
        if (player.getDepth() == 0) {
            if (isDaytime())
                Message.sound(MessageType.MSG_AMBIENT_DAY, player);
            else
                Message.sound(MessageType.MSG_AMBIENT_NITE, player);
        } else if (player.getDepth() <= 20) {
            Message.sound(MessageType.MSG_AMBIENT_DNG1, player);
        } else if (player.getDepth() <= 40) {
            Message.sound(MessageType.MSG_AMBIENT_DNG2, player);
        } else if (player.getDepth() <= 60) {
            Message.sound(MessageType.MSG_AMBIENT_DNG3, player);
        } else if (player.getDepth() <= 80) {
            Message.sound(MessageType.MSG_AMBIENT_DNG4, player);
        } else {
            Message.sound(MessageType.MSG_AMBIENT_DNG5, player);
        }
    }

    /**
     * Rebuilds the level's sound map so monsters can hear and home in on the player. Ports C's
     * {@code make_noise} ({@code src/game-world.c}).
     * <p>
     * Every grid is assigned a noise "distance": the player's grid is {@code 0} and each
     * grid's value grows with its flow-distance from the player along sound-carrying terrain.
     * Monsters later track the player by stepping towards adjacent grids with lower noise,
     * following twisty tunnels and mazes rather than straight-line distance. A monster's
     * hearing is the largest noise value it can still detect.
     * <p>
     * The fill is a breadth-first flood from the player. The {@link ArrayDeque} is used as a
     * FIFO queue ({@code addLast} to enqueue, {@code pop}/{@code removeFirst} to dequeue), and
     * the noise level is only incremented once the front of the queue reaches the current
     * level — the sentinel-free "re-queue and bump" step that keeps the flood in level order.
     * {@link TimedEffect#TMD_COVERTRACKS} coarsens the increment from 1 to 4, shrinking the
     * range at which the noise stays low enough to be heard. Features that do not transmit
     * sound ({@link Chunk#squareIsNoFlow}) block propagation.
     */
    private void makeNoise() {
        Loc next = player.getGrid();
        int noise = 0;
        int noiseIncrement = (player.getTimedEffect(TimedEffect.TMD_COVERTRACKS) != 0) ? 4 : 1;
        ArrayDeque<Loc> queue = new ArrayDeque<>();

        // Set all the grids to silence
        currentCave.resetNoise();

        // Player makes noise
        currentCave.getNoise().setValue(next, noise);
        queue.addLast(next);
        noise += noiseIncrement;

        // Propogate noise
        while (!queue.isEmpty()) {
            // Get the next grid
            next = queue.pop();

            // If we've reached the current noise level, put it back and step
            if (currentCave.getNoise().getValue(next) == noise) {
                queue.addLast(next);
                noise += noiseIncrement;
                continue;
            }

            // Assign noise to the children and enquire them
            for (DirectionEnum direction : DirectionEnum.values()) {
                // Child location
                if (!direction.isStandard())
                    continue;

                Loc grid = next.sum(Loc.row(direction.ddy()).col(direction.ddx()));

                // TODO: Replace this by Square.
                if (!currentCave.inBounds(grid)) continue;

                // Ignore features that don't transmit sound
                if (currentCave.squareIsNoFlow(grid)) continue;

                // Skip grids that already have noise
                if (currentCave.getNoise().getValue(grid) != 0) continue;

                // Skip the player grid
                if (grid.equals(player.getGrid())) continue;

                // Save the noise
                currentCave.getNoise().setValue(grid, noise);

                // Enqueue that entry
                queue.addLast(grid);
            }
        }
    }

    /**
     * Ages the level's scent trails and lays down fresh scent around the player. Ports C's
     * {@code update_scent} ({@code src/game-world.c}).
     * <p>
     * Scent is how perceptive monsters track the player, and it is deliberately weaker than sound
     * (see {@link #makeNoise()}): it reaches only a small area, and monsters can use it to home in
     * but not to flee. Each grid's scent is valued by <em>age</em> — every call ages all existing
     * scent by one ({@link Chunk#updateScent()}), and lower numbers are fresher. A monster's
     * "smell" is the oldest scent it can still detect. Grids the player has never occupied stay at
     * {@code 0}, as does the player's own grid (harmless, since nothing smells the player's grid).
     * <p>
     * New scent is stamped over the 5x5 block centred on the player using the fixed {@code
     * scentStrength} template (freshest {@code 0} at the centre, rising to {@code 2} at the edges).
     * A block grid only receives scent if it is a valid, scent-carrying grid <em>and</em> it either
     * is the player's own grid or is adjacent to an already-closer scent grid ({@code newScent - 1})
     * — the adjacency test that keeps scent spreading along open floor rather than leaking through
     * walls. A player under {@link TimedEffect#TMD_COVERTRACKS} lays no new scent at all, so the
     * method returns after only the aging pass.
     */
    private void updateScent() {
        int[][] scentStrength = {
                {2, 2, 2, 2, 2},
                {2, 1, 1, 1, 2},
                {2, 1, 0, 1, 2},
                {2, 1, 1, 1, 2},
                {2, 2, 2, 2, 2},
        };

        // Update scent for all grids
        currentCave.updateScent();

        // scentless player
        if (player.getTimedEffect(TimedEffect.TMD_COVERTRACKS) != 0) return;

        // Lay down new scent around player
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                boolean addScent = false;

                int newScent = scentStrength[y][x];

                // Get the initial square
                Loc scent = Loc.row(y + player.getGrid().getY() - 2).col(x + player.getGrid().getX() - 2);

                // Ignore invalid or non-scent-carrying squares
                if (!currentCave.inBounds(scent)) continue;
                if (currentCave.squareIsNoScent(scent)) continue;

                // Check scent is spreading on floors, not going through walls
                for (DirectionEnum direction : DirectionEnum.values()) {
                    if (!direction.isStandard())
                        continue;

                    Loc adj = scent.sum(Loc.row(direction.ddy()).col(direction.ddx()));

                    if (!currentCave.inBounds(adj)) continue;

                    // Player grid is always valid
                    if (y == 2 && x == 2)
                        addScent = true;

                    // adjacent to a closer grid, so valid
                    if (currentCave.getScent().getValue(adj) == newScent - 1)
                        addScent = true;
                }

                // Not valid
                if (!addScent)
                    continue;

                currentCave.getScent().setValue(scent, newScent);
            }
        }
    }

    /**
     * Reports whether it is currently daytime in the game world. Ports C's {@code is_daytime}
     * ({@code src/game-world.c}).
     * <p>
     * A full day is {@code 10 * day_length} game turns. The current turn's position within that
     * cycle is taken modulo the day length; the first half is day and the second half is night.
     * The {@code 10L} keeps the arithmetic in {@code long} so the modulus does not overflow as the
     * turn count grows. This is a pure query on the global turn counter, used across the game (town
     * lighting, level generation, feature projection) to decide whether the surface is lit.
     *
     * @return {@code true} during the first half of the day/night cycle, {@code false} otherwise
     */
    public static boolean isDaytime() {
        int turn = GameState.getTurn();
        return ((turn % (10L * GameConstants.getWorldDayLength())) < ((10L * GameConstants.getWorldDayLength()) / 2));
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
     * {@link PlayerUtils#restingCompleteSpecial()}, {@link ObjectUtils#packOverflow},
     * {@link EffectUtil#effectSimple} (the ore-detection effect), and
     * {@link Player#timedGradeEq(TimedEffect, String)}.
     */
    private void processPlayer() {
        // check for interrupts
        PlayerUtils.restingCompleteSpecial();
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
                    player.timedGradeEq(TimedEffect.TMD_STUN, "Knocked Out")) {
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
}