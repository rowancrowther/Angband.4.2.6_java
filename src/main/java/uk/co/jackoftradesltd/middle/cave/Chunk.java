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

package uk.co.jackoftradesltd.middle.cave;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.combat.Target;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.cave.enums.SquareEnum;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.monsters.MonsterGroup;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.Pile;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.*;
import java.util.stream.Stream;

import static uk.co.jackoftradesltd.middle.cave.ChunkUtils.los;

/**
 * A whole level (or self-contained piece of one): the 2D grid of {@link Square}s
 * plus everything that lives on it — monsters, objects, noise/scent flow maps,
 * generation metadata and level feeling. The large family of {@code squareXxx()}
 * methods are bounds-checked convenience accessors that delegate to the
 * {@link Square} at a {@link Loc}. This is the Java port of the C original's
 * {@code struct chunk} ({@code src/cave.h}), which represents both the live cave
 * and the player's remembered copy of it.
 *
 * <p>A running game holds two of these. One is the real level, C's global {@code cave}; the other
 * is what the player remembers of it, C's {@code player->cave}. C tells them apart by comparing
 * the pointer against the global, so this class keeps that global in {@link #currentLevel}, set
 * through {@link #setCurrentLevel(Chunk)}, and the knowledge accessors ({@link #isKnown},
 * {@link #squareMemorize}, {@link #squareSetKnownFeat}, {@link #squareForget}) do nothing useful
 * on a chunk that was never given it.
 *
 * <p>Grids are stored {@code [x][y]}, where C stores {@code [y][x]}; always go through
 * {@link #getSquare(Loc)} rather than indexing {@link #squares} directly. Where C asserts that a
 * grid is in bounds, the predicates here answer {@code false} (or {@code null} / {@code 0}) instead
 * of halting the game.
 *
 * <p>Several members are still stubs waiting on later chapters: {@link #deleteMonsterIndex(int)}
 * and {@link MonsterGroup#monsterGroupChangeIndex} on Chapter 6, and {@link #squareNoteSpot(Loc)},
 * {@link #squareRevealTrap(Loc, boolean, boolean)} and {@link #resetNoise()} on Chapter 4.
 * {@link #illuminate(boolean)}, {@link #pickAndPlaceDistantMonster},
 * {@link #squareMemorizeTraps(Loc)} and {@link #displayFeeling(boolean)} are stubs too. Each says
 * so in its own block.
 *
 * <p>Class Chunk coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class Chunk {
    /**
     * Logger used to report out-of-bounds access and similar errors. Java-only; C reports these
     * through {@code assert()} or {@code quit()}.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The chunk's name, C's {@code c->name}: the level or vault this chunk represents, or
     * {@code null} if unnamed. Read through {@link #getName()}, for example to recognise the arena.
     */
    private String name;
    /**
     * The game turn this chunk was created, C's {@code c->turn} (set from the global {@code turn}
     * in {@code cave_new()}, {@code cave.c}). Nothing in this class reads it after construction.
     */
    private int turn;
    /**
     * The dungeon depth of this chunk, C's {@code c->depth}. Nothing in this class reads it after
     * construction.
     */
    private int depth;

    /**
     * The packed level feeling, C's {@code c->feeling}: the object feeling is {@code feeling / 10}
     * and the monster feeling is {@code feeling % 10}. Written by {@link #setFeeling(int)}.
     */
    private int feeling;
    /**
     * Accumulated rating of the objects on this level, C's {@code c->obj_rating}. Nothing in this
     * class reads it yet; level generation will.
     */
    private int objectRating;
    /**
     * Accumulated rating of the monsters on this level, C's {@code c->mon_rating}. Nothing in this
     * class reads it yet; level generation will.
     */
    private int monsterRating;
    /**
     * Whether the level holds a notably good item, C's {@code c->good_item}. Nothing in this class
     * reads it yet.
     */
    private boolean goodItem;

    /**
     * Level height in rows, C's {@code c->height}. Valid {@code y} runs {@code 0 .. height - 1}.
     */
    private int height;
    /**
     * Level width in columns, C's {@code c->width}. Valid {@code x} runs {@code 0 .. width - 1}.
     */
    private int width;

    /**
     * How many feeling squares the player has seen so far, C's {@code c->feeling_squares}. It is
     * incremented in {@link #updateOne(Loc, Player)} and the level feeling is announced when it
     * reaches {@code world:feeling-need}.
     */
    private int feelingSquares;
    /**
     * Tally of grids per terrain feature, the counterpart of C's {@code c->feat_count}, which
     * {@code square_set_feat()} ({@code cave-square.c}) keeps current.
     *
     * <p>Nothing in this class fills or reads it yet, because {@code square_set_feat()} is not
     * ported. Note that C keys it by feature index, while this map is keyed by
     * {@link TerrainFeatureFlags}, so the key type will need settling when that function arrives.
     */
    private HashMap<TerrainFeatureFlags, Integer> featCount;

    /**
     * The grid of squares, indexed {@code [x][y]} — the reverse of C's {@code c->squares[y][x]}.
     * Reach it through {@link #getSquare(Loc)}, which bounds-checks and takes the {@link Loc}.
     */
    private Square[][] squares;
    /**
     * Noise flow map used for monster hearing, C's {@code c->noise}. See {@link #resetNoise()}.
     */
    private Heatmap noise;
    /**
     * Scent flow map used for monsters that track by smell, C's {@code c->scent}. See
     * {@link #updateScent()}.
     */
    private Heatmap scent;
    /**
     * Location of the player's decoy, C's {@code c->decoy}. {@link Loc#zero} means there is none,
     * as C uses {@code loc(0, 0)}. Nothing in this class reads it yet.
     */
    private Loc decoy;

    /**
     * Master list of the objects in this chunk, the counterpart of C's {@code c->objects} array.
     *
     * <p>C indexes that array by each object's {@code oidx} and nulls a slot to delist an object.
     * This is a plain list searched with {@code contains()}, because the port does not carry
     * {@code oidx}. Read it through {@link #getObjects()}.
     */
    private List<ItemObject> objects;
    /**
     * Highest object index in use, C's {@code c->obj_max}. Nothing in this class reads it, since
     * {@link #objects} is a list rather than a fixed array.
     */
    private int objMax;

    /**
     * The monsters present in this chunk, indexed by monster index, C's {@code c->monsters}.
     *
     * <p>Sized at construction from {@code level-max:monsters}, C's {@code z_info->level_monster_max}.
     * Slot {@code 0} is a reserved dummy and an empty slot is {@code null}, where C has a zeroed
     * struct with {@code race == NULL}.
     */
    private Monster[] monsters;
    /**
     * One past the highest monster index in use, the port of C's {@code c->mon_max}
     * ({@code struct chunk}, {@code src/cave.h}). C starts it at {@code 1}, since slot {@code 0} is
     * reserved, and {@link #compactMonsters(int)} lowers it as trailing holes are removed.
     *
     * <p>This is the high-water mark and not the capacity: the capacity is
     * {@code monsters.length}. Read it through {@link #getMonMax()}.
     */
    private int monMax;
    /**
     * Live monster count as supplied to the constructor, the port of C's {@code mon_cnt}
     * ({@code struct chunk}, {@code src/cave.h}).
     *
     * <p>Nothing reads or updates this field after construction. {@link #monsterCount()} derives the
     * live count by scanning {@link #monsters} instead, so this is a dead copy of the constructor
     * argument.
     *
     * <p>Field monCnt coded before 260929, commented in full on 260929.
     */
    private int monCnt;
    /**
     * Index of the monster currently taking its turn, the port of C's {@code mon_current}
     * ({@code struct chunk}, {@code src/cave.h}).
     *
     * <p>{@code -1} means no monster is acting, which is what C sets in {@code cave_new()}
     * ({@code cave.c}) and restores after each monster's turn in {@code process_monsters()}
     * ({@code mon-move.c}). Callers test {@code > 0} to ask "is a monster the cause of this?".
     *
     * <p>Field monCurrent coded before 260929, commented in full on 260929.
     */
    private int monCurrent;
    /**
     * Number of breeding monsters currently on the level, C's {@code c->num_repro}. C decrements it
     * in {@code delete_monster_idx()} for {@code RF_MULTIPLY} races. Nothing in this class reads it
     * yet.
     */
    private int numRepro;

    /**
     * The monster groups (packs) on this level, C's {@code c->monster_groups}. Nothing in this
     * class reads it yet; see {@link MonsterGroup#monsterGroupChangeIndex}.
     */
    private ArrayList<MonsterGroup> monsterGroups;

    /**
     * Connection points used when stitching this chunk into a larger level, C's {@code c->join}
     * list of {@code struct connector}. Nothing in this class reads it yet.
     */
    private ArrayList<Connector> join;

    /**
     * The player this chunk belongs to, standing in for C's global {@code player}. The knowledge
     * accessors reach the player's remembered cave through it (see {@link #isKnown} and
     * {@link #squareSetKnownFeat}). It may be {@code null}, in which case {@link #objectDelete} and
     * {@link #delistObject} skip the player-specific steps but {@link #isKnown} does not.
     */
    private Player player;
    /**
     * The live current level, standing in for C's global {@code cave}. It is <em>not</em> taken at
     * construction: it starts {@code null} and is set through {@link #setCurrentLevel(Chunk)}.
     * Several accessors compare {@code this} against it to tell whether this chunk is the real cave
     * or the player's remembered copy, since the two share the same {@code Chunk} type.
     */
    private Chunk currentLevel;

    /**
     * Builds a chunk of the given dimensions and metadata, the port of C's {@code cave_new()}
     * ({@code cave.c}). It allocates a grid of blank {@link Square}s, two zeroed flow maps, an empty
     * object list, empty group and connector lists, and a monster array of
     * {@code level-max:monsters} empty slots.
     *
     * <p>C's {@code cave_new()} takes only the height and width and fixes the rest itself:
     * {@code mon_max = 1}, {@code mon_current = -1} and {@code turn} from the global. Here the
     * caller supplies them, so a chunk meant to behave like C's should be given {@code monMax = 1}
     * and {@code monCurrent = -1}.
     *
     * <p>{@link #currentLevel} is left unset; the caller must follow up with
     * {@link #setCurrentLevel(Chunk)}. The {@code GameConstants} table must be loaded before this
     * runs, since the monster array is sized from it.
     *
     * <p>Constructor Chunk coded before 260930, commented in full on 260930.
     *
     * @param name           chunk name, or {@code null} if unnamed
     * @param turn           the game turn of creation
     * @param depth          dungeon depth
     * @param feeling        packed level feeling
     * @param objectRating   object rating
     * @param monsterRating  monster rating
     * @param goodItem       whether a notably good item is present
     * @param height         level height in rows
     * @param width          level width in columns
     * @param feelingSquares number of feeling squares already seen
     * @param objMax         highest object index
     * @param monMax         the initial high-water mark for monster indices; C uses {@code 1}
     * @param monCnt         live monster count; kept but never read afterwards
     * @param monCurrent     index of the monster taking its turn; C uses {@code -1} for none
     * @param numRepro       number of breeding monsters
     * @param player         the player this chunk belongs to; may be {@code null}
     */
    public Chunk(String name, int turn, int depth, int feeling, int objectRating, int monsterRating,
                 boolean goodItem, int height, int width, int feelingSquares, int objMax, int monMax,
                 int monCnt, int monCurrent, int numRepro, Player player) {
        this.name = name;
        this.turn = turn;
        this.depth = depth;
        this.feeling = feeling;
        this.objectRating = objectRating;
        this.monsterRating = monsterRating;
        this.goodItem = goodItem;
        this.height = height;
        this.width = width;
        this.feelingSquares = feelingSquares;
        this.featCount = new HashMap<>();

        this.squares = new Square[this.width][this.height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                squares[x][y] = new Square(new Feature(TerrainFlags.FEAT_NONE, "", "",
                        TerrainFlags.FEAT_NONE, 0, 0, new Flag<>(TerrainFeatureFlags.class),
                        null, "", "", "", "", "",
                        "", "", new Flag<>(MonsterRaceFlag.class)),
                        0, 0);
            }
        }

        this.noise = new Heatmap(width, height);
        this.scent = new Heatmap(width, height);
        this.decoy = Loc.zero;
        this.objects = new ArrayList<>();
        this.objMax = objMax;
        this.monsters = new Monster[GameConstants.getLevelMaxMonsters()];
        this.monMax = monMax;
        this.monCnt = monCnt;
        this.monCurrent = monCurrent;
        this.numRepro = numRepro;
        this.monsterGroups = new ArrayList<>();
        this.join = new ArrayList<>();
        this.player = player;
    }

    /**
     * Returns the chunk that is the live current level, C's global {@code cave}. A chunk compares
     * itself against this to tell whether it is the real level or the player's remembered copy,
     * since the two share this class. It is {@code null} until {@link #setCurrentLevel(Chunk)} has
     * been called.
     *
     * <p>Function getCurrentLevel coded before 260930, commented in full on 260930.
     *
     * @return the live current level, or {@code null} if none has been set
     */
    public Chunk getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Records which chunk is the live current level.
     *
     * <p>Must be called on every chunk once the real level exists - the real level points at itself,
     * and the player's remembered copy points at the real one. Several accessors here return early
     * or answer {@code false} while it is unset, so a chunk that never receives it is quietly inert
     * rather than obviously broken.
     *
     * <p>Exists because the answer cannot be taken at construction: the real level is built before
     * it has been installed as the current one, so a chunk reading the game state in its constructor
     * would capture whatever came before it.
     *
     * <p>Function setCurrentLevel commented in full on 260827.
     *
     * @param currentLevel the chunk that is the live current level
     */
    public void setCurrentLevel(Chunk currentLevel) {
        this.currentLevel = currentLevel;
    }

    /**
     * Tests whether a grid lies inside this chunk, the port of C's {@code square_in_bounds}
     * ({@code cave-square.c}). Valid {@code x} runs {@code 0 .. width - 1} and valid {@code y} runs
     * {@code 0 .. height - 1}, so the outermost ring of the level is inside the bounds; contrast
     * {@link #inBoundsFully(Loc)}.
     *
     * <p>Function inBounds coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is inside this chunk
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean inBounds(@NotNull Loc grid) {
        return grid.getX() >= 0 && grid.getX() < width
                && grid.getY() >= 0 && grid.getY() < height;
    }

    /**
     * Tests whether a grid lies inside this chunk and off its outermost ring, the port of C's
     * {@code square_in_bounds_fully} ({@code cave-square.c}). Valid {@code x} runs
     * {@code 1 .. width - 2} and valid {@code y} runs {@code 1 .. height - 2}, which is the region
     * where all eight neighbours of the grid also exist.
     *
     * <p>Function inBoundsFully coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is inside this chunk and not on its edge
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean inBoundsFully(@NotNull Loc grid) {
        return grid.getX() > 0 && grid.getX() < width - 1
                && grid.getY() > 0 && grid.getY() < height - 1;
    }

    /**
     * Tests whether a grid is marked, the port of C's {@code square_ismark} ({@code cave-square.c}).
     * Reads the {@code SQUARE_MARK} info flag. C asserts the grid is in bounds; this answers
     * {@code false} for an out-of-bounds grid instead.
     *
     * <p>Function squareIsMarked coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_MARK}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsMarked(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isMark();
    }

    /**
     * Tests whether a grid is permanently lit, the port of C's {@code square_isglow}
     * ({@code cave-square.c}). Reads the {@code SQUARE_GLOW} info flag, which is the standing light
     * of a lit room; it is not the same as the accumulated light level that
     * {@link #squareIsLit(Loc)} tests. C asserts the grid is in bounds; this answers {@code false}
     * instead.
     *
     * <p>Function squareIsGlow coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_GLOW}
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean squareIsGlow(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isGlow();
    }

    /**
     * Tests whether a grid's terrain damages whoever stands in it, the port of C's
     * {@code square_isdamaging} ({@code cave-square.c}). C's own comment says "only lava so far":
     * the test is the {@code TF_FIERY} terrain flag. C asserts the grid is in bounds; this answers
     * {@code false} instead.
     *
     * <p>Function squareIsDamaging coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain is fiery
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDamaging(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isDamaging();
    }

    /**
     * Tests whether a grid is part of a vault, the port of C's {@code square_isvault}
     * ({@code cave-square.c}). Reads the {@code SQUARE_VAULT} info flag, which says only that the
     * grid belongs to a vault and nothing about what kind of grid it is. C asserts the grid is in
     * bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsVault coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_VAULT}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsVault(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isVault();
    }

    /**
     * Tests whether a grid is currently seen by the player, the port of C's {@code square_isseen}
     * ({@code cave-square.c}). Reads the {@code SQUARE_SEEN} info flag, which
     * {@link #updateView(Player)} rebuilds on every recalculation, so it answers "visible now", not
     * "has ever been visited". C asserts the grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsSeen coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_SEEN}
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean squareIsSeen(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isSeen();
    }

    /**
     * Tests whether a grid's terrain carries no monster flow information, the port of C's
     * {@code square_isnoflow} ({@code cave-square.c}). Tests the {@code TF_NO_FLOW} terrain flag, so
     * {@code true} means the noise map does not propagate through the grid. C asserts the grid is in
     * bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsNoFlow coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain does NOT carry monster flow information
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean squareIsNoFlow(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsNoFlow();
    }

    /**
     * Tests whether a grid's terrain carries no player scent, the port of C's
     * {@code square_isnoscent} ({@code cave-square.c}). Tests the {@code TF_NO_SCENT} terrain flag,
     * so {@code true} means scent is not laid on the grid. C asserts the grid is in bounds; this
     * answers {@code false} instead.
     *
     * <p>Function squareIsNoScent coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain does NOT carry player scent
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean squareIsNoScent(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsNoScent();
    }

    /**
     * Tests whether a grid is in the player's line of sight, the port of C's {@code square_isview}
     * ({@code cave-square.c}). Reads the {@code SQUARE_VIEW} info flag. In view is not the same as
     * seen: a grid can have a line of sight to it and still be too dark to see, which is why
     * {@link #becomeViewable(Loc, Player, boolean)} sets this flag first and {@code SQUARE_SEEN}
     * only when there is light. C asserts the grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsView coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_VIEW}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsView(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isView();
    }

    /**
     * Tests whether a grid was seen before the current view update, the port of C's
     * {@code square_wasseen} ({@code cave-square.c}). Reads the {@code SQUARE_WASSEEN} info flag,
     * the snapshot that {@link #markWasSeen()} takes and {@link #updateOne(Loc, Player)} clears. It
     * is only meaningful in the middle of {@link #updateView(Player)}. C asserts the grid is in
     * bounds; this answers {@code false} instead.
     *
     * <p>Function squareWasSeen coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_WASSEEN}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareWasSeen(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).wasSeen();
    }

    /**
     * Tests whether a grid carries the trap marker, the port of C's {@code square_istrap}
     * ({@code cave-square.c}). Reads the {@code SQUARE_TRAP} info flag, which C's own comment calls
     * "a known trap". {@link #squareTrapFlag(Loc, TrapEnum)} tests this marker before it looks at
     * the traps themselves. C asserts the grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsTrap coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_TRAP}
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean squareIsTrap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isTrap();
    }

    /**
     * Tests whether a grid carries the unknown-trap marker, the port of C's {@code square_isinvis}
     * ({@code cave-square.c}). Reads the {@code SQUARE_INVIS} info flag. This is a flag test, not
     * the trap-list test that {@link #squareIsSecretTrap(Loc)} makes. C asserts the grid is in
     * bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsInvis coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_INVIS}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsInvis(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isInvis();
    }

    /**
     * Returns an iterator over the pile of objects lying on a grid. C has no such function; it walks
     * {@code square_object()} and follows each object's {@code next} pointer by hand.
     *
     * <p>Unlike the predicates around it, this does not check the bounds, so an out-of-bounds grid
     * throws a {@code NullPointerException} from {@link #getSquare(Loc)}.
     *
     * <p>Function getPileIterator coded before 260930, commented in full on 260930.
     *
     * @param grid the grid whose pile to walk; must be inside this chunk
     * @return an iterator over the objects on the grid, topmost first
     */
    @CheckReturnValue
    @Contract(pure = true)
    Iterator<ItemObject> getPileIterator(@NotNull Loc grid) {
        return getSquare(grid).getSquarePileIterator();
    }

    /**
     * Tests whether a grid holds a visible trap, the port of C's {@code square_isvisibletrap}
     * ({@code cave-square.c}), which asks {@code square_trap_flag()} for {@code TRF_VISIBLE}. That
     * helper first checks the {@code SQUARE_TRAP} marker and then scans the grid's whole trap list,
     * so a grid with the marker but no visible trap answers {@code false}. Out-of-bounds grids
     * answer {@code false}.
     *
     * <p>Function squareIsVisibleTrap coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if a trap on the grid is visible
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsVisibleTrap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isVisibleTrap();
    }

    /**
     * Tests whether a grid holds a player trap the player cannot see yet, the port of C's
     * {@code square_issecrettrap} ({@code cave-square.c}): no visible trap, but a player trap. C's
     * comment adds that such a grid "will appear as a floor tile". Both halves answer {@code false}
     * out of bounds, so this does too.
     *
     * <p>Function squareIsSecretTrap coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid holds an unseen player trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsSecretTrap(@NotNull Loc grid) {
        return !squareIsVisibleTrap(grid) && squareIsPlayerTrap(grid);
    }

    /**
     * Tests whether a grid holds a known player trap that is currently disabled, the port of C's
     * {@code square_isdisabledtrap} ({@code cave-square.c}): a visible trap whose timeout is above
     * zero. The timeout is read with {@code -1} as the trap index, which in C's
     * {@code square_trap_timeout()} means "any trap on the grid" rather than one specific kind.
     *
     * <p>Function squareIsDisabledTrap coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid holds a visible trap with a running timeout
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDisabledTrap(@NotNull Loc grid) {
        return inBounds(grid) && squareIsVisibleTrap(grid) && getSquare(grid).trapTimeout(-1) > 0;
    }

    /**
     * Tests whether a grid holds a trap the player can try to disarm, the port of C's
     * {@code square_isdisarmabletrap} ({@code cave-square.c}): a visible player trap that is not
     * currently disabled. A trap that has been disabled already cannot be disarmed again until its
     * timeout expires.
     *
     * <p>Function squareIsDisarmableTrap coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid holds a known, enabled player trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDisarmableTrap(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        if (squareIsDisabledTrap(grid)) return false;

        return squareIsVisibleTrap(grid) && squareIsPlayerTrap(grid);
    }

    /**
     * Tests whether a grid may be destroyed, the port of C's {@code square_changeable}
     * ({@code cave-square.c}). Used by the destruction spells and when placing stairs. A grid is
     * refused if it is permanent rock, a shop entrance or a staircase, or if any object lying on it
     * is an artifact; otherwise it is accepted.
     *
     * <p>C does not check the bounds; this answers {@code false} for an out-of-bounds grid.
     *
     * <p>Function squareChangeable coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid can be changed
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareChangeable(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        Square square = getSquare(grid);

        if (square.isPerm() || square.isShop() || square.isStairs()) return false;

        return !square.hasObjectArtifact();
    }

    /**
     * Tests whether a grid is on the inner edge of a trap-detection area, the port of C's
     * {@code square_dtrap_edge} ({@code cave-square.c}). The grid must itself be detected
     * ({@code SQUARE_DTRAP}), and at least one of its four orthogonal neighbours must be fully
     * inside the level and not detected. Diagonals are not consulted. The order in which the four
     * neighbours are tried does not affect the answer.
     *
     * <p>Function squareDTrapEdge coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is a detected grid next to an undetected one
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareDTrapEdge(@NotNull Loc grid) {
        if (!inBounds(grid) || !getSquare(grid).isDTrap()) return false;

        return Stream.of(DirectionEnum.DIR_N, DirectionEnum.DIR_S, DirectionEnum.DIR_E, DirectionEnum.DIR_W)
                .map(grid::nextGrid)
                .anyMatch(neighbour -> inBoundsFully(neighbour) && !squareIsDTrap(neighbour));
    }

    /**
     * Tests whether a grid is an inner room wall, the port of C's {@code square_iswall_inner}
     * ({@code cave-square.c}). Reads the {@code SQUARE_WALL_INNER} info flag, which only level
     * generation uses. C asserts the grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsWallInner coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_WALL_INNER}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsWallInner(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWallInner();
    }

    /**
     * Tests whether a grid is an outer room wall, the port of C's {@code square_iswall_outer}
     * ({@code cave-square.c}). Reads the {@code SQUARE_WALL_OUTER} info flag, which only level
     * generation uses. C asserts the grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsWallOuter coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_WALL_OUTER}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsWallOuter(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWallOuter();
    }

    /**
     * Tests whether a grid is a solid room wall, the port of C's {@code square_iswall_solid}
     * ({@code cave-square.c}). Reads the {@code SQUARE_WALL_SOLID} info flag, which only level
     * generation uses. C asserts the grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsWallSolid coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_WALL_SOLID}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsWallSolid(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWallSolid();
    }

    /**
     * Tests whether a grid is barred to randomly generated monsters, the port of C's
     * {@code square_ismon_restrict} ({@code cave-square.c}). Reads the {@code SQUARE_MON_RESTRICT}
     * info flag, which level generation sets on marked rooms; {@code pick_and_place_distant_monster}
     * skips such grids while the level is still being built. C asserts the grid is in bounds; this
     * answers {@code false} instead.
     *
     * <p>Function squareIsMonRestrict coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_MON_RESTRICT}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsMonRestrict(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isMonRestrict();
    }

    /**
     * Tests whether the player is barred from teleporting away from a grid, the port of C's
     * {@code square_isno_teleport} ({@code cave-square.c}). Reads the {@code SQUARE_NO_TELEPORT}
     * info flag. The flag forbids teleporting <em>out of</em> the grid, so {@code true} means the
     * player is held. C asserts the grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsNoTeleport coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_NO_TELEPORT}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsNoTeleport(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isNoTeleport();
    }

    /**
     * Tests whether magic mapping skips a grid, the port of C's {@code square_isno_map}
     * ({@code cave-square.c}). Reads the {@code SQUARE_NO_MAP} info flag, so {@code true} means the
     * grid CANNOT be magically mapped. C asserts the grid is in bounds; this answers {@code false}
     * instead.
     *
     * <p>Function squareIsNoMap coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_NO_MAP}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsNoMap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isNoMap();
    }

    /**
     * Tests whether the player's ESP skips a grid, the port of C's {@code square_isno_esp}
     * ({@code cave-square.c}). Reads the {@code SQUARE_NO_ESP} info flag, so {@code true} means the
     * grid CANNOT be detected by ESP. C asserts the grid is in bounds; this answers {@code false}
     * instead.
     *
     * <p>Function squareIsNoESP coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_NO_ESP}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsNoESP(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isNoEsp();
    }

    /**
     * Tests whether a grid is queued for projection processing, the port of C's
     * {@code square_isproject} ({@code cave-square.c}). Reads the {@code SQUARE_PROJECT} info flag,
     * a working mark set while a projection is being resolved. It is unrelated to whether the
     * terrain lets a projection through, which is {@link #squareIsProjectable(Loc)}. C asserts the
     * grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsProject coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_PROJECT}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsProject(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isProject();
    }

    /**
     * Tests whether a grid has been covered by trap detection, the port of C's
     * {@code square_isdtrap} ({@code cave-square.c}). Reads the {@code SQUARE_DTRAP} info flag.
     * {@link #squareDTrapEdge(Loc)} builds on it. C asserts the grid is in bounds; this answers
     * {@code false} instead.
     *
     * <p>Function squareIsDTrap coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_DTRAP}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsDTrap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isDTrap();
    }

    /**
     * Tests whether level generation must not put stairs on a grid, the port of C's
     * {@code square_isno_stairs} ({@code cave-square.c}). Reads the {@code SQUARE_NO_STAIRS} info
     * flag, so {@code true} means stairs are NOT allowed here. C asserts the grid is in bounds; this
     * answers {@code false} instead.
     *
     * <p>Function squareIsNoStairs coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid carries {@code SQUARE_NO_STAIRS}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsNoStairs(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isNoStairs();
    }

    /**
     * Tests whether a grid is open, the port of C's {@code square_isopen} ({@code cave-square.c}):
     * a floor grid with nobody in it. The occupant test is C's {@code square->mon}, which is
     * non-zero for a monster and negative for the player, so either one makes the grid not open.
     * Items and traps do not matter; contrast {@link #squareIsEmpty(Loc)}. Out-of-bounds grids
     * answer {@code false}.
     *
     * <p>Function squareIsOpen coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is floor with no monster or player on it
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsOpen(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isOpen();
    }

    /**
     * Tests whether a grid holds a glyph of warding, the port of C's {@code square_iswarded}
     * ({@code cave-square.c}). The glyph is a trap of the kind named {@code "glyph of warding"}, so
     * this looks for that specific trap rather than for a flag. Out-of-bounds grids answer
     * {@code false}.
     *
     * <p>Function squareIsWarded coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid holds a glyph of warding
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsWarded(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWarded();
    }

    /**
     * Tests whether a grid holds a decoy, the port of C's {@code square_isdecoyed}
     * ({@code cave-square.c}). The decoy is a trap of the kind named {@code "decoy"}, so this looks
     * for that specific trap rather than for any trap. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsDecoyed coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid holds a decoy
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDecoyed(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isDecoyed();
    }

    /**
     * Tests whether a grid holds a web, the port of C's {@code square_iswebbed}
     * ({@code cave-square.c}). The web is a trap of the kind named {@code "web"}, so this looks for
     * that specific trap rather than for any trap. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsWebbed coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid holds a web
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsWebbed(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWebbed();
    }

    /**
     * Tests whether a grid looks like rock, the port of C's {@code square_seemslikewall}
     * ({@code cave-square.c}). Tests the {@code TF_ROCK} terrain flag, which is set on walls, rubble
     * and secret doors alike: a secret door is not a wall, but it seems like one until the player
     * finds it. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareSeemsLikeWall coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain has {@code TF_ROCK}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareSeemsLikeWall(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featSeemsLikeWall();
    }

    /**
     * Tests whether a grid's terrain is worth the player's attention, the port of C's
     * {@code square_isinteresting} ({@code cave-square.c}). Tests the {@code TF_INTERESTING}
     * terrain flag. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsInteresting coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain has {@code TF_INTERESTING}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsInteresting(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsIntersting();
    }

    /**
     * Tests whether any trap on a grid carries a given trap flag, the port of C's
     * {@code square_trap_flag} ({@code trap.c}). C first checks the {@code SQUARE_TRAP} marker, so a
     * grid without it answers {@code false} whatever its trap list holds, and then scans every trap
     * on the grid for the flag. The visible-trap and player-trap tests are built on this.
     * Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareTrapFlag coded before 260930, commented in full on 260930.
     *
     * @param grid     the grid to test
     * @param trapFlag the trap flag to look for
     * @return true if a trap on the grid has the flag
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareTrapFlag(@NotNull Loc grid, @NotNull TrapEnum trapFlag) {
        return inBounds(grid) && getSquare(grid).trapFlag(trapFlag);
    }

    /**
     * Tests whether a grid is a closed, locked door, the port of C's {@code square_islockeddoor}
     * ({@code cave-square.c}). C's {@code square_door_power()} returns the power of the door-lock
     * trap and returns {@code 0} for anything that is not a closed door, so a locked door is one
     * whose lock power is above zero. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsLockedDoor coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is a closed door with a lock on it
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsLockedDoor(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isLockedDoor();
    }

    /**
     * Tests whether a grid is a closed door with no lock, the port of C's
     * {@code square_isunlockeddoor} ({@code cave-square.c}): a closed door whose lock power is
     * {@code 0}. An open or broken door is not counted, because it is not closed. Out-of-bounds
     * grids answer {@code false}.
     *
     * <p>Function squareIsUnlockedDoor coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is a closed, unlocked door
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsUnlockedDoor(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isUnlockedDoor();
    }

    /**
     * Tests whether a grid holds a player trap, known or not, the port of C's
     * {@code square_isplayertrap} ({@code cave-square.c}), which asks {@code square_trap_flag()} for
     * {@code TRF_TRAP}. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsPlayerTrap coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if a trap on the grid is a player trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsPlayerTrap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isPlayerTrap();
    }

    /**
     * Tests whether the player is standing on a grid, the port of C's {@code square_isplayer}
     * ({@code cave-square.c}). C stores the occupant in {@code square->mon}: a positive value is a
     * monster's index and a negative value means the player, so the test is {@code mon < 0}.
     * Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsPlayer coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the player occupies the grid
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean squareIsPlayer(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isPlayer();
    }

    /**
     * Tests whether a grid is empty, the port of C's {@code square_isempty} ({@code cave-square.c}).
     * Empty is stricter than open: the grid must hold no player trap and no web, be open floor with
     * nobody standing in it, and have no object lying on it. Several placement rules depend on it,
     * including the stair and summoning tests below. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsEmpty coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is open, untrapped, unwebbed and free of objects
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsEmpty(@NotNull Loc grid) {
        if (!inBounds(grid))
            return false;

        return getSquare(grid).isEmpty();
    }

    /**
     * Tests whether a grid is an acceptable place to arrive at, the port of C's
     * {@code square_isarrivable} ({@code cave-square.c}). The grid must have nobody in it, no player
     * trap and no web, and must be floor or stairs. Unlike {@link #squareIsEmpty(Loc)} it does not
     * mind objects lying there. C's doc comment on this function is a copy of the one on
     * {@code square_isempty} and does not describe it; the body is what is documented here, and it
     * carries a C comment wondering about allowing open doors. Out-of-bounds grids answer
     * {@code false}.
     *
     * <p>Function squareIsArrivable coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is unoccupied, untrapped, unwebbed floor or stairs
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsArrivable(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isArrivable();
    }

    /**
     * Tests whether an object may be dropped on a grid, the port of C's {@code square_canputitem}
     * ({@code cave-square.c}): the terrain must be able to hold objects, the grid must not carry the
     * {@code SQUARE_TRAP} marker, and no object may already lie there. Out-of-bounds grids answer
     * {@code false}.
     *
     * <p>Function squareCanPutItem coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid can take a new object
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareCanPutItem(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).canPutItem();
    }

    /**
     * Tests whether a grid can be dug, the port of C's {@code square_isdiggable}
     * ({@code cave-square.c}). That covers rubble, secret doors and the mineral walls (granite,
     * magma and quartz); permanent rock is excluded. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsDiggable coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is mineral, a secret door or rubble
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDiggable(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        Square square = getSquare(grid);
        return square.isMineral() || square.isSecretDoor() || square.isRubble();
    }

    /**
     * Tests whether a grid is normal open floor, the port of C's {@code square_isfloor}
     * ({@code cave-square.c}). Tests the {@code TF_FLOOR} terrain flag; occupants and objects are
     * ignored. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsFloor coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain is floor
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsFloor(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isFloor();
    }

    /**
     * Tests whether a spider could spin a web on a grid, the port of C's {@code square_iswebbable}
     * ({@code cave-square.c}): floor with no trap of any kind. C asks {@code square_trap()} for the
     * first trap and refuses if there is one; this asks whether the trap list is empty, which is the
     * same question. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareIsWebbable coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is floor with no traps on it
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsWebbable(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;
        if (!getSquare(grid).getTraps().isEmpty()) return false;
        return squareIsFloor(grid);
    }

    /**
     * Tests whether a monster can walk through a grid's terrain, the port of C's
     * {@code square_is_monster_walkable} ({@code cave-square.c}). C says this is needed for
     * polymorphing, since a monster may stand on terrain that is not plain floor. It tests the same
     * {@code TF_PASSABLE} flag as {@link #squareIsPassable(Loc)}. Out-of-bounds grids answer
     * {@code false}.
     *
     * <p>Function squareIsMonsterWalkable coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain has {@code TF_PASSABLE}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsMonsterWalkable(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsMonsterWalkable();
    }

    /**
     * Tests whether the player can walk through a grid's terrain, the port of C's
     * {@code square_ispassable} ({@code cave-square.c}). Tests the {@code TF_PASSABLE} flag and
     * ignores whatever is standing there. C asserts the grid is in bounds; this answers
     * {@code false} instead.
     *
     * <p>Function squareIsPassable coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain has {@code TF_PASSABLE}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsPassable(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsPassable();
    }

    /**
     * Tests whether a projection can pass through a grid, the port of C's
     * {@code square_isprojectable} ({@code cave-square.c}). Tests the {@code TF_PROJECT} terrain
     * flag. C already answers {@code false} for an out-of-bounds grid here, so the two agree.
     *
     * <p>Function squareIsProjectable coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain has {@code TF_PROJECT}
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean squareIsProjectable(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsProjectable();
    }

    /**
     * Tests whether a grid could be a level-feeling trigger square, the port of C's
     * {@code square_allowsfeel} ({@code cave-square.c}): passable terrain that does not damage its
     * occupant. This only says the grid is suitable; whether it <em>is</em> a trigger is the
     * {@code SQUARE_FEEL} flag that {@link #squareIsFeel(Loc)} reads. Out-of-bounds grids answer
     * {@code false}.
     *
     * <p>Function squareAllowsFeel coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is passable and not damaging
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareAllowsFeel(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).allowsFeel();
    }

    /**
     * Tests whether line of sight passes through a grid, the port of C's {@code square_allowslos}
     * ({@code cave-square.c}). Tests the {@code TF_LOS} terrain flag. The lighting code uses it
     * throughout as its definition of "not a wall". C asserts the grid is in bounds; this answers
     * {@code false} instead, which the lighting code relies on when it probes a neighbour that may
     * lie off the level.
     *
     * <p>Function squareAllowsLOS coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain has {@code TF_LOS}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareAllowsLOS(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featAllowsLOS();
    }

    /**
     * Tests whether a grid is a strong wall, the port of C's {@code square_isstrongwall}
     * ({@code cave-square.c}): a mineral wall (granite, magma or quartz) or permanent rock. Secret
     * doors and rubble are excluded. C asserts the grid is in bounds; this answers {@code false}
     * instead.
     *
     * <p>Function squareIsStrongWall coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is mineral or permanent rock
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsStrongWall(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        Square square = getSquare(grid);
        return square.isMineral() || square.isPerm();
    }

    /**
     * Tests whether a grid's terrain gives off light of its own, the port of C's
     * {@code square_isbright} ({@code cave-square.c}). Tests the {@code TF_BRIGHT} terrain flag.
     * {@link #calcLighting(Player)} gives such a grid intensity {@code 2} and lights its neighbours.
     * C asserts the grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareIsBright coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain has {@code TF_BRIGHT}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsBright(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsBright();
    }

    /**
     * Tests whether a grid's terrain is fire-based, the port of C's {@code square_isfiery}
     * ({@code cave-square.c}). Tests the {@code TF_FIERY} terrain flag, the same flag that
     * {@link #squareIsDamaging(Loc)} reads. C asserts the grid is in bounds; this answers
     * {@code false} instead.
     *
     * <p>Function squareIsFiery coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's terrain has {@code TF_FIERY}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsFiery(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsFiery();
    }

    /**
     * Tests whether the player believes a grid blocks projections, the port of C's
     * {@code square_isbelievedwall} ({@code cave-square.c}). The answer comes from the player's
     * memory, which may be wrong. A grid on the edge of the level or off it is always believed to be
     * a wall. A grid the player has no knowledge of is believed <em>not</em> to be one, so the
     * player assumes the way is clear. Otherwise the answer is the opposite of whether the
     * remembered terrain is projectable.
     *
     * <p>C ends with {@code square_isprojectable(player->cave, grid)}, which bounds-checks; this
     * calls the square's own test directly, which is safe because the fully-in-bounds test has
     * already passed.
     *
     * <p>Function squareIsBelievedWall coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the player believes the grid blocks projections
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsBelievedWall(@NotNull Loc grid) {
        if (!inBoundsFully(grid)) return true;

        if (!this.isKnown(grid)) return false;

        return !player.getCave().getSquare(grid).featIsProjectable();
    }

    /**
     * Tests whether the player knows a grid to be passable, the port of C's
     * {@code square_isknownpassable} ({@code cave-square.c}): the terrain must be known, and the
     * player's <em>remembered</em> terrain must be passable. The method name keeps the original
     * spelling "Passible". Out-of-bounds grids answer {@code false}.
     *
     * <p>Function isKnownPassible coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the player knows the grid and remembers it as passable
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean isKnownPassible(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        if (!isKnown(grid)) return false;

        return player.getCave().squareIsPassable(grid);
    }

    /**
     * Tests whether a grid is a good place for stairs because it is a dead end, the port of C's
     * {@code square_suits_stairs_well} ({@code cave-square.c}). Vault grids and grids flagged
     * no-stairs are refused; otherwise the grid must have exactly 3 walls among its four orthogonal
     * neighbours, all 4 diagonal neighbours walls, and be empty (see {@link #squareIsEmpty(Loc)}).
     * Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareSuitsStairsWell coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is an empty cul-de-sac
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareSuitsStairsWell(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        if (squareIsVault(grid) || squareIsNoStairs(grid)) return false;

        return squareNumWallsAdjacent(grid) == 3 && squareNumWallsDiagonal(grid) == 4 && squareIsEmpty(grid);
    }

    /**
     * Tests whether a grid is an acceptable place for stairs because it is in a corridor, the port
     * of C's {@code square_suits_stairs_ok} ({@code cave-square.c}). It is the second choice after
     * {@link #squareSuitsStairsWell(Loc)}: vault and no-stairs grids are refused, and the grid must
     * have exactly 2 orthogonal and all 4 diagonal neighbours as walls, and be empty. Out-of-bounds
     * grids answer {@code false}.
     *
     * <p>Function squareSuitsStairsOK coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid is an empty corridor grid
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareSuitsStairsOK(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        if (squareIsVault(grid) || squareIsNoStairs(grid)) return false;

        return squareNumWallsDiagonal(grid) == 4 && squareNumWallsAdjacent(grid) == 2 && squareIsEmpty(grid);
    }

    /**
     * Tests whether a summoned monster may be placed on a grid, the port of C's
     * {@code square_allows_summon} ({@code cave-square.c}): the grid must be empty and hold neither
     * a glyph of warding nor a decoy. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareAllowsSummoning coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if a summoned monster may appear there
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareAllowsSummoning(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        return squareIsEmpty(grid) && !squareIsWarded(grid) && !squareIsDecoyed(grid);
    }

    /**
     * Counts the walls among a grid's four orthogonal neighbours, the port of C's
     * {@code square_num_walls_adjacent} ({@code cave-square.c}). "Wall" is the {@code TF_WALL}
     * terrain flag, which excludes rubble and secret doors. A neighbour that falls off the level
     * counts as not a wall; C would read outside its array there, so this is a safe reading of an
     * undefined case. An out-of-bounds grid itself returns {@code 0}, where C asserts.
     *
     * <p>Function squareNumWallsAdjacent coded before 260930, commented in full on 260930.
     *
     * @param grid the grid whose neighbours are counted
     * @return the number of walls among the four orthogonal neighbours, {@code 0 .. 4}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private int squareNumWallsAdjacent(@NotNull Loc grid) {
        if (!inBounds(grid)) return 0;

        int count = 0;
        Square square = getSquare(grid.nextGrid(DirectionEnum.DIR_S));
        if (square != null && square.featIsWall())
            count++;
        square = getSquare(grid.nextGrid(DirectionEnum.DIR_E));
        if (square != null && square.featIsWall())
            count++;

        square = getSquare(grid.nextGrid(DirectionEnum.DIR_N));
        if (square != null && square.featIsWall())
            count++;

        square = getSquare(grid.nextGrid(DirectionEnum.DIR_W));
        if (square != null && square.featIsWall())
            count++;

        return count;
    }

    /**
     * Counts the walls among a grid's four diagonal neighbours, the port of C's
     * {@code square_num_walls_diagonal} ({@code cave-square.c}). It treats "wall" and off-level
     * neighbours exactly as {@link #squareNumWallsAdjacent(Loc)} does.
     *
     * <p>Function squareNumWallsDiagonal coded before 260930, commented in full on 260930.
     *
     * @param grid the grid whose neighbours are counted
     * @return the number of walls among the four diagonal neighbours, {@code 0 .. 4}
     */
    @Contract(pure = true)
    @CheckReturnValue
    private int squareNumWallsDiagonal(@NotNull Loc grid) {
        if (!inBounds(grid)) return 0;

        int count = 0;
        Square square = getSquare(grid.nextGrid(DirectionEnum.DIR_SW));
        if (square != null && square.featIsWall())
            count++;
        square = getSquare(grid.nextGrid(DirectionEnum.DIR_SE));
        if (square != null && square.featIsWall())
            count++;

        square = getSquare(grid.nextGrid(DirectionEnum.DIR_NE));
        if (square != null && square.featIsWall())
            count++;

        square = getSquare(grid.nextGrid(DirectionEnum.DIR_NW));
        if (square != null && square.featIsWall())
            count++;

        return count;
    }

    /**
     * Returns the square at a grid, the port of C's {@code square()} ({@code cave-square.c}), which
     * is {@code &c->squares[grid.y][grid.x]}. C asserts the grid is in bounds; this returns
     * {@code null} instead, so a caller that has not checked the bounds gets a
     * {@code NullPointerException} rather than a halt.
     *
     * <p>Function getSquare coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to look up
     * @return the square there, or {@code null} if the grid is out of bounds
     */
    @Contract(pure = true)
    @CheckReturnValue
    public Square getSquare(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        return squares[grid.getX()][grid.getY()];
    }

    /**
     * Returns the terrain feature of a grid, the port of C's {@code square_feat}
     * ({@code cave-square.c}), which is {@code &f_info[square(c, grid)->feat]}. C asserts the grid is
     * in bounds; this returns {@code null} instead.
     *
     * <p>Function squareFeature coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to look up
     * @return the feature there, or {@code null} if the grid is out of bounds
     */
    @CheckReturnValue
    @Contract(pure = true)
    private @Nullable Feature squareFeature(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        return getSquare(grid).getFeature();
    }

    /**
     * Returns the accumulated light level of a grid, the port of C's {@code square_light}
     * ({@code cave-square.c}). The level is rebuilt by {@link #calcLighting(Player)} and can go
     * negative where an unlight source overlaps lit terrain. C asserts the grid is in bounds; this
     * returns {@code 0} instead.
     *
     * <p>Function squareLight coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to look up
     * @return the light level, or {@code 0} if the grid is out of bounds
     */
    @CheckReturnValue
    @Contract(pure = true)
    private int squareLight(@NotNull Loc grid) {
        if (!inBounds(grid)) return 0;
        return getSquare(grid).getLight();
    }

    /**
     * Returns the monster standing on a grid, the port of C's {@code square_monster}
     * ({@code cave-square.c}). The answer is {@code null} for an out-of-bounds grid, for a grid
     * holding no monster (occupant index zero or negative, the latter being the player), and for a
     * slot whose monster is dead, meaning it has no race. That last check is why this is preferred
     * over {@link #getMonster(Loc)}.
     *
     * <p>Function squareMonster coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to look at
     * @return the live monster there, or {@code null}
     */
    @CheckReturnValue
    @Contract(pure = true)
    private @Nullable Monster squareMonster(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;

        Square square = getSquare(grid);
        int monsterIndex = square.getMonsterIndex();

        if (monsterIndex > 0) {
            Monster mon = caveMonster(monsterIndex);
            return mon != null && mon.getMonsterRace() != null ? mon : null;
        }

        return null;
    }

    /**
     * Returns the monster in a given slot, the port of C's {@code cave_monster}
     * ({@code cave.c}), which returns {@code NULL} for an index of zero or below and otherwise a
     * pointer to {@code &c->monsters[idx]}. Where C hands back a pointer to a zeroed struct for an
     * empty slot, this returns {@code null}, so callers must check for it. Index {@code 0} reads the
     * reserved dummy slot, which is {@code null}, so it agrees with C. A negative index, or one past
     * the end of the array, answers {@code null}. C returns {@code NULL} for the negative case, and
     * for the past-the-end case C reads out of bounds, so {@code null} is the safe reading.
     *
     * <p>Function caveMonster coded before 260930, commented in full on 260930, updated on 260930
     * when the out-of-range indices stopped throwing.
     *
     * @param index the monster index; {@code 0} is the reserved dummy slot
     * @return the monster in that slot, or {@code null} if it is empty or the index is out of range
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Monster caveMonster(int index) {
        if (index < 0 || index >= monsters.length)
            return null;
        
        return monsters[index];
    }

    /**
     * Returns whatever monster object is indexed by a grid's occupant. This has no C counterpart and
     * duplicates {@link #squareMonster(Loc)} without its dead-monster check: a slot that still holds
     * a monster with no race is returned as if it were live. Prefer {@link #squareMonster(Loc)}
     * unless that difference is wanted.
     *
     * <p>Function getMonster coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to look at
     * @return the monster in the occupant's slot, or {@code null} if the grid is out of bounds or
     * holds no monster
     */
    @Contract(pure = true)
    @CheckReturnValue
    Monster getMonster(@NotNull Loc grid) {
        return squareMonster(grid);
    }

    /**
     * Tests whether a grid carries a given info flag. C has no single function for this; it calls
     * {@code sqinfo_has(square(c, grid)->info, flag)} directly wherever it needs one, and the
     * predicates above are that call with the flag fixed. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function squareHasInfoFlag coded before 260930, commented in full on 260930.
     *
     * @param grid     the grid to test
     * @param infoFlag the {@code SQUARE_*} flag to look for
     * @return true if the grid has the flag set
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean squareHasInfoFlag(@NotNull Loc grid, @NotNull SquareEnum infoFlag) {
        return (inBounds(grid) && getSquare(grid).hasInfoFlag(infoFlag));
    }

    /**
     * Tests whether a grid has any light on it, the port of C's {@code square_islit}
     * ({@code cave-square.c}): the accumulated light level, from {@link #squareLight(Loc)}, is above
     * zero. This is the result of the lighting calculation, not the {@code SQUARE_GLOW} flag that
     * {@link #squareIsGlow(Loc)} reads. C asserts the grid is in bounds; this answers {@code false}
     * instead.
     *
     * <p>Function squareIsLit coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the grid's light level is positive
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean squareIsLit(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isLit();
    }

    /**
     * Returns the top object of the pile on a grid, the port of C's {@code square_object}
     * ({@code cave-square.c}), which returns {@code square->obj}, the head of the pile. Both C and
     * this answer {@code null} for an out-of-bounds grid.
     *
     * <p>Function squareObject coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to look at
     * @return the topmost object there, or {@code null} if there is none
     */
    @CheckReturnValue
    @Contract(pure = true)
    private @Nullable ItemObject squareObject(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        return getSquare(grid).getTopObject();
    }

    /**
     * Returns the first trap on a grid, the port of C's {@code square_trap} ({@code cave-square.c}),
     * which returns {@code square->trap}. C's own comment calls it "the first (and currently only)
     * trap". Both C and this answer {@code null} for an out-of-bounds grid.
     *
     * <p>Function squareTrap coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to look at
     * @return the first trap there, or {@code null} if there is none
     */
    @CheckReturnValue
    @Contract(pure = true)
    private @Nullable Trap squareTrap(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        return getSquare(grid).getTrap();
    }

    /**
     * Tests whether a given object lies in the pile on a grid, the port of C's
     * {@code square_holds_object} ({@code cave-square.c}), which is {@code pile_contains()} over the
     * grid's pile. C asserts the grid is in bounds; this answers {@code false} instead.
     *
     * <p>Function squareHoldsObject coded before 260930, commented in full on 260930.
     *
     * @param grid   the grid to look at
     * @param object the object to look for
     * @return true if the object is in the grid's pile
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareHoldsObject(@NotNull Loc grid, @NotNull ItemObject object) {
        if (!inBounds(grid)) return false;
        return getSquare(grid).holdsObject(object);
    }

    /**
     * Returns the number of columns in this chunk, C's {@code c->width}. Valid {@code x} values run
     * from {@code 0} to {@code getWidth() - 1}, matching {@link #inBounds(Loc)}.
     *
     * <p>Function getWidth coded before 260929, commented in full on 260929.
     *
     * @return the width of this chunk
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of rows in this chunk, C's {@code c->height}. Valid {@code y} values run
     * from {@code 0} to {@code getHeight() - 1}, matching {@link #inBounds(Loc)}.
     *
     * <p>Function getHeight coded before 260929, commented in full on 260929.
     *
     * @return the height of this chunk
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getHeight() {
        return height;
    }

    /**
     * Removes an object from a grid's floor pile and leaves it orphaned, the port of C's
     * {@code square_excise_object} ({@code cave-square.c}). The object is unlinked from the pile and
     * nothing else: it is not deleted, not delisted from {@link #objects} and not freed, so the
     * caller either re-homes it or follows up with {@link #delistObject(ItemObject)} and
     * {@link #objectDelete(Chunk, ItemObject)}.
     *
     * <p>C asserts the grid is in bounds and would halt the game. This logs a fatal error and
     * throws instead, so the failure is visible to a test.
     *
     * <p>Function squareExciseObject coded before 260930, commented in full on 260930.
     *
     * @param grid the grid whose pile the object is in
     * @param item the object to excise
     * @throws IndexOutOfBoundsException if the grid is outside the chunk's boundaries
     */
    @Contract(mutates = "this")
    public void squareExciseObject(@NotNull Loc grid, @NotNull ItemObject item) throws IndexOutOfBoundsException {
        if (!inBounds(grid)) {
            String message = "Location out of bounds, being thrown as a fatal error after logging";
            IndexOutOfBoundsException ex = new IndexOutOfBoundsException(message);
            logger.fatal(message, ex);
            throw ex;
        }

        getSquare(grid).pileExcise(item);
    }

    /**
     * Deletes an object, or orphans it if the player still remembers it, the port of C's
     * {@code object_delete} ({@code obj-pile.c}). The receiver is C's {@code c}, the chunk the
     * object belongs to, and {@code playerCave} is C's {@code p_c}, the matching known chunk (the
     * player's remembered cave when {@code c} is the live level).
     *
     * <p>Four things happen, in this order. First, the object leaves whichever {@link Pile} holds
     * it. Second, if the player is tracking this object in their upkeep, the tracking is dropped.
     * Third, if the receiver lists the object <em>and</em> {@code playerCave} lists the object's
     * known counterpart, the object is orphaned rather than deleted: its grid, holder and mimic
     * indices are zeroed, and the known counterpart is marked {@code OBJ_NOTICE_IMAGINED} because
     * it is now purely a figment of the player's memory. The method returns there, and both lists
     * still hold their entries. Fourth, otherwise the object is removed from {@code playerCave}, if
     * given, and from the receiver.
     *
     * <p>The pile step comes first and is unconditional, because both outcomes detach the object
     * from its pile: C unlinks it from its {@code prev} and {@code next} neighbours at the top of
     * {@code object_delete}, before the orphan test, and the orphan branch then clears those two
     * links as well. The orphan test must likewise be made before either list is changed, which is
     * why it sits ahead of the removals.
     *
     * <p>Differences from C: the objects here are list members, so removing one is a
     * {@code remove()} rather than nulling the slot at its {@code oidx}. C's pile is a linked list
     * threaded through the objects, so the unlink needs no grid; here the pile is found through
     * {@link ItemObject#getOwningPile()}, which every {@link Pile} insert sets, and
     * {@link Pile#excise(ItemObject)} removes the object and clears that owner. An object in no
     * pile has no owner and this step is skipped. It works for any pile, not only a floor pile,
     * and never reads the object's {@code grid}, so an object a monster carries is handled the same
     * way. A caller therefore does not need to call {@link #squareExciseObject(Loc, ItemObject)}
     * first, although doing so does no harm. Nothing is freed; the collector reclaims it.
     *
     * <p>Function objectDelete coded before 260930, commented in full on 260930, updated on 260930
     * when the pile removal moved to the object's owning pile.
     *
     * @param playerCave the known chunk to consult and clean, or {@code null} for none
     * @param item       the object to delete
     */
    @Contract(mutates = "this")
    public void objectDelete(@Nullable Chunk playerCave, @NotNull ItemObject item) {
        Chunk cave = this;

        Pile pile = item.getOwningPile();
        if (pile != null) {
            pile.excise(item);
        }
        
        // Remove the object from those tracked by the player upkeep
        if (player != null
                && player.getPlayerUpkeep() != null
                && item == player.getPlayerUpkeep().getObject())
            player.getPlayerUpkeep().setObject(null);

        if (playerCave != null
                && cave.objects.contains(item)
                && playerCave.objects.contains(item.getKnown())) {
            item.setGrid(Loc.zero);
            item.setHeldMIndex(0);
            item.setMimickingMIndex(0);

            item.getKnown().orNotice(ObjectNotice.OBJ_NOTICE_IMAGINED);
            return;
        }

        if (playerCave != null) {
            playerCave.objects.remove(item);
        }

        cave.objects.remove(item);
    }

    /**
     * Removes an object from this chunk's master list, the port of C's {@code delist_object}
     * ({@code cave.c}). C says the function is "robust against delisting of unlisted objects", and
     * so is this: an object that is not listed is ignored. It does not touch the square's floor
     * pile.
     *
     * <p>A real object stays listed while the player still remembers it. When this chunk is the live
     * level and the player's cave lists the object's known counterpart, the method returns without
     * removing anything, because deleting the real object would leave that memory pointing at
     * nothing. C also resets the object's {@code oidx}, which the port does not carry.
     *
     * <p>This dereferences {@link #player}, so it assumes a player was given at construction
     * whenever this chunk is the live level.
     *
     * <p>Function delistObject coded before 260930, commented in full on 260930.
     *
     * @param item the object to delist
     */
    @Contract(mutates = "this")
    public void delistObject(ItemObject item) {
        if (!objects.contains(item)) return;

        if (this.equals(currentLevel) && player.getCave() != null && player.getCave().objects.contains(item.getKnown()))
            return;

        objects.remove(item);
    }

    /**
     * Copies a grid's real terrain into the player's memory, the port of C's
     * {@code square_memorize} ({@code cave-square.c}). It does nothing unless this chunk is the live
     * level. The write is made by {@link #squareSetKnownFeat(Loc, Feature)}, which repeats that test.
     *
     * <p>Function squareMemorize coded before 260930, commented in full on 260930.
     *
     * @param grid the grid whose terrain the player now remembers; must be inside this chunk
     */
    void squareMemorize(@NotNull Loc grid) {
        if (this != currentLevel) return;
        squareSetKnownFeat(grid, getSquare(grid).getFeature());
    }

    /**
     * Writes a terrain feature into the player's remembered cave at a grid, the port of C's static
     * {@code square_set_known_feat} ({@code cave-square.c}). C's version returns unless the chunk is
     * the live cave, and this does the same; it also ignores an out-of-bounds grid, which C does not
     * check. Nothing is written to this chunk itself.
     *
     * <p>{@link #squareMemorize(Loc)} passes the real feature to remember, and
     * {@link #squareForget(Loc)} passes {@code FEAT_NONE}, which is what {@link #isKnown(Loc)} reads
     * as "not known". Dereferences {@link #player} and its cave, so both must be present on the live
     * level.
     *
     * <p>Function squareSetKnownFeat coded before 260930, commented in full on 260930.
     *
     * @param grid    the grid whose remembered terrain is set
     * @param feature the feature the player is to remember there
     */
    void squareSetKnownFeat(@NotNull Loc grid, Feature feature) {
        if (!inBounds(grid)) return;
        if (this != currentLevel) return;

        player.getCave().getSquare(grid).setFeature(feature);
    }

    /**
     * Tests whether the player knows the terrain at a grid, the port of C's {@code square_isknown}
     * ({@code cave-square.c}). It only answers for the two chunks that make up the player's world: a
     * chunk that is neither the live level nor the player's remembered cave gets {@code false}, and
     * so does a player with no remembered cave. Otherwise the terrain is known when the remembered
     * feature is anything but {@code FEAT_NONE}.
     *
     * <p>C tests {@code !player} first. This dereferences {@link #player} to reach the remembered
     * cave, so a chunk built with no player throws here. Out-of-bounds grids answer {@code false}.
     *
     * <p>Function isKnown coded before 260930, commented in full on 260930.
     *
     * @param grid the grid to test
     * @return true if the player's memory holds real terrain there
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean isKnown(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        if (this != currentLevel && this != player.getCave()) return false;

        if (player.getCave() == null) return false;

        return !player.getCave().getSquare(grid).getFeature().isNoFeat();
    }

    /**
     * Returns one past the highest monster index in use, the port of C's {@code cave_monster_max}
     * ({@code cave.c}), which returns {@code c->mon_max}. It is a high-water mark that starts at
     * {@code 1} in C and is lowered by {@link #compactMonsters(int)}. It is neither the number of
     * live monsters, which is {@link #monsterCount()}, nor the capacity of the array.
     *
     * <p>Function getMonMax coded before 260930, commented in full on 260930.
     *
     * @return the monster high-water mark
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getMonMax() {
        return monMax;
    }

    /**
     * Returns this level's name, the port of reading C's {@code c->name}. It is used, for example,
     * to recognise the arena level.
     *
     * <p>Function getName coded before 260930, commented in full on 260930.
     *
     * @return the name, or {@code null} if the chunk is unnamed
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the raw monster array, indexed by monster index — the port of reading C's
     * {@code cave->monsters}. Index 0 is a reserved dummy and unused slots are {@code null} (C's
     * empty, {@code race == NULL} slots), so callers iterating this must skip {@code null} entries.
     * The array is the full capacity, {@code level-max:monsters} long, so a loop that should stop at
     * the high-water mark must use {@link #getMonMax()} as its bound. The array is live, not a copy.
     *
     * <p>Function getMonsters coded before 260930, commented in full on 260930.
     *
     * @return the monster array
     */
    public Monster[] getMonsters() {
        return monsters;
    }

    /**
     * Flags a single grid as needing to be redrawn on the map — the port of C's
     * {@code square_light_spot} ({@code cave-map.c}). Sets the item-list redraw flag and signals the
     * map-update event for this grid, so the display recomputes the square (for example to reshow a
     * monster or a light change) on the next refresh.
     *
     * <p>Only the live level signals: C's guard is {@code c == cave && player->cave}, so a call on the
     * player's remembered copy, or before the player has a remembered cave, does nothing. C says the
     * grid must be "legal" and does not check; this ignores an out-of-bounds grid.
     *
     * <p>The event carries the coordinates as x then y, which is what C sends.
     *
     * <p>Function squareLightSpot coded before 260930, commented in full on 260930.
     *
     * @param grid the grid whose display needs refreshing
     */
    public void squareLightSpot(@NotNull Loc grid) {
        if (!inBounds(grid)) return;

        if (this == currentLevel && player.getCave() != null) {
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_ITEMLIST);
            // The data is passed x/y and not y/x on this line as that is what the C does.
            GameEngine.getEventsBusHandler().eventSignalPoint(GameEventType.EVENT_MAP, grid.getX(), grid.getY());
        }
    }

    /**
     * Counts the monsters currently alive on this chunk, the port of C's {@code cave_monster_count()}
     * ({@code cave.c}), which returns {@code c->mon_cnt}.
     *
     * <p>Where C keeps a running counter that {@code place_new_monster_one()} increments and
     * {@code delete_monster_idx()} decrements ({@code mon-make.c}), this scans {@link #monsters}
     * and counts the non-null slots. The two agree because C wipes a deleted monster's slot, which
     * is a {@code null} here, and never places a monster in slot {@code 0}. It is not the high-water
     * mark, which is {@link #getMonMax()}.
     *
     * <p>Function monsterCount coded before 260929, commented in full on 260929.
     *
     * @return the number of live monsters in this chunk
     */
    public int monsterCount() {
        int count = 0;
        for (Monster monster : monsters) {
            if (monster != null) count++;
        }

        return count;
    }

    /**
     * Culls monsters from the level to free up space, the port of C's {@code compact_monsters}
     * ({@code mon-make.c}). Passes over the monster list with escalating aggression each iteration —
     * raising the level cap and shrinking the distance threshold — deleting eligible monsters that
     * fail a saving throw, with quest monsters and uniques given progressively better odds of
     * surviving. Finally excises the dead entries and lowers the high-water mark, {@link #monMax}.
     *
     * <p>Each pass of the outer loop uses {@code maxLevel = 5 * iteration} as the highest monster
     * level it will touch and {@code minDistance = 5 * (20 - iteration)} as the nearest it will go
     * to the player, so both relax until enough have been culled. A monster gets a saving throw of
     * {@code 90}; a quest monster gets {@code 100} (never culled) while fewer than {@code 1000}
     * passes have run; and a unique gets {@code 99}, which takes over if a monster is both. It
     * survives when
     * {@code randint0(100)} falls below that figure. The excise loop then walks the list backwards
     * and moves the last monster into each hole, lowering the mark by one each time. Slot
     * {@code 0} is never examined.
     *
     * <p><b>Outstanding:</b> {@link #deleteMonsterIndex(int)} is a stub waiting on Chapter 6, so the
     * culling pass counts each victim as removed without actually removing it. The loop terminates
     * and the excise pass is correct, but no monster is deleted yet.
     *
     * <p>Function compactMonsters coded before 260930, commented in full on 260930.
     *
     * @param numToCompact the minimum number of monsters to remove; {@code 0} simply excises the
     *                     already-dead entries without a "Compacting monsters..." message
     */
    public void compactMonsters(int numToCompact) {
        int numCompacted;
        int iteration;

        int maxLevel;
        int minDistance;
        int chance;

        if (numToCompact != 0)
            Message.message("Compacting monsters...");

        // Compact at least numToCompact monsters
        for (numCompacted = 0, iteration = 1;
             numCompacted < numToCompact;
             iteration++) {
            // Get more vicious each iteration
            maxLevel = 5 * iteration;

            // Get closer each iteration
            minDistance = 5 * (20 - iteration);

            // Check the monsters
            for (int monIndex = 1; monIndex < caveMonsterMax(); monIndex++) {
                Monster monster = monsters[monIndex];
                if (monster == null) continue;

                // skip dead monsters
                if (monster.getMonsterRace() == null) continue;

                // High level monsters start out immune
                if (monster.getMonsterRace().getLevel() > maxLevel) continue;

                // Ignore nearby monsters
                if ((minDistance > 0) && (monster.getcDistance() < minDistance)) continue;

                // Base saving throw
                chance = 90;

                // Only compact quest monsters in an emergency
                if (monster.getMonsterRace().hasMonsterRaceFlag(MonsterRaceFlag.RF_QUESTOR) && (iteration < 1000))
                    chance = 100;

                // Try to save unique monsters
                if (monster.isUnique()) chance = 99;

                if (RandomValueUtils.randInt0(100) < chance)
                    continue;

                deleteMonster(monster.getGrid());

                numCompacted++;
            }
        }

        // Excise dead monsters (backwards)
        for (int monIndex = monMax - 1; monIndex >= 1; monIndex--) {
            Monster monster = monsters[monIndex];

            if (monster == null || monster.getMonsterRace() == null) {
                monsterIndexMove(monMax - 1, monIndex);
                monMax--;
            }
        }
    }

    /**
     * The port of C's {@code cave_monster_max} ({@code cave.c}): the monster high-water mark used as
     * the loop bound in {@link #compactMonsters(int)}. It returns {@link #monMax}, as does the
     * public {@link #getMonMax()}.
     *
     * <p>Function caveMonsterMax coded before 260930, commented in full on 260930.
     *
     * @return the monster high-water mark
     */
    private int caveMonsterMax() {
        return monMax;
    }

    /**
     * Moves a monster from one slot of the monster list to another, the port of C's
     * {@code monster_index_move} ({@code mon-make.c}). {@link #compactMonsters(int)} uses it to fill
     * the holes left by dead monsters. C says this must only be called when there is really a
     * monster in the source slot; here an empty source slot, or {@code fromIndex == toIndex}, is
     * quietly ignored.
     *
     * <p>Everything that refers to the monster by index is repaired, in C's order: the grid's
     * occupant, the monster's own {@code monIndex}, the pack it belongs to, the {@code held_m_idx}
     * of each object it carries, the {@code mimicking_m_idx} of any object it mimics, and the
     * player's target. Then the slots are swapped, leaving {@code null} behind.
     *
     * <p>C also repoints the health bar, whose {@code health_who} is a pointer to the monster's slot.
     * Here the bar holds the monster object itself and the object does not move, so there is nothing
     * to repair. The target is handled differently: {@link Target} stores an index, so it is
     * updated.
     *
     * <p><b>Outstanding:</b> {@link MonsterGroup#monsterGroupChangeIndex} is a stub waiting on
     * Chapter 6 that always reports success, so pack membership is not re-indexed yet and the
     * "Bad monster group info!" failure below cannot occur.
     *
     * <p>Function monsterIndexMove coded before 260930, commented in full on 260930.
     *
     * @param fromIndex the monster's current index
     * @param toIndex   the index to move it to
     * @throws IllegalStateException if the pack bookkeeping reports an inconsistency
     */
    public void monsterIndexMove(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) return;

        Monster monster = monsters[fromIndex];
        if (monster == null) return;

        // Update the cave
        squareSetMon(monster.getGrid(), toIndex);

        // Update the monster index
        monster.setMonIndex(toIndex);

        // Update group
        if (!MonsterGroup.monsterGroupChangeIndex(this, toIndex, fromIndex)) {
            logger.fatal("Bad monster group info!");
            throw new IllegalStateException("Bad monster group info!");
        }

        // Repair objects being held by monster
        for (ItemObject obj : monster.getHeldObjects()) {
            obj.setHeldMIndex(toIndex);
        }

        // Move mimicked objects
        if (monster.getMimickedObject() != null) {
            monster.getMimickedObject().setMimickingMIndex(toIndex);
        }

        // Change target to new monster index
        if (Target.getTargetMonster(this) == monster)
            Target.setTargetMonster(monster, toIndex);

        // Health bar points to a monster, not an index 
        // No need to update it

        // Move the monster
        monsters[toIndex] = monsters[fromIndex];
        monsters[fromIndex] = null;
    }

    /**
     * Records which monster occupies a grid, the port of C's {@code square_set_mon}
     * ({@code cave-square.c}). The value is a monster index; {@code 0} means nobody and a negative
     * value means the player. Neither C nor this checks the bounds, so the grid must be inside the
     * chunk.
     *
     * <p>Function squareSetMon coded before 260930, commented in full on 260930.
     *
     * @param grid    the grid to update
     * @param toIndex the occupant's monster index
     */
    private void squareSetMon(Loc grid, int toIndex) {
        Square square = getSquare(grid);
        square.setMon(toIndex);
    }

    /**
     * Deletes the monster occupying the given grid, if any — the port of C's {@code delete_monster}
     * ({@code mon-make.c}). Resolves the grid to its square and delegates to
     * {@link #deleteMonsterIndex(int)} if a monster is there; a grid with no monster, or with the
     * player (a negative occupant), is left alone. C asserts the grid is in bounds; this ignores an
     * out-of-bounds grid instead.
     *
     * <p><b>Outstanding:</b> the work is done by {@link #deleteMonsterIndex(int)}, a stub waiting on
     * Chapter 6, so this currently deletes nothing.
     *
     * <p>Function deleteMonster coded before 260930, commented in full on 260930.
     *
     * @param grid the map location to clear of its monster
     */
    private void deleteMonster(@NotNull Loc grid) {
        if (!inBounds(grid)) return;

        Square square = getSquare(grid);
        if (square.getMonsterIndex() > 0)
            deleteMonsterIndex(square.getMonsterIndex());
    }

    /**
     * Deletes the monster at the given array index, freeing its slot and clearing its square — the
     * port of C's {@code delete_monster_idx} ({@code mon-make.c}).
     *
     * <p>When ported this has to do what C does, in C's order: lower the race's live count (the
     * original race if the monster is a shapechanger), lower {@code numRepro} for a breeder, ask for
     * a view update if the race gives off light, clear the player's target and health-bar tracking
     * if they point at this monster, clear the command status, empty the square and remove the
     * monster from its pack, delete each carried object (with the artifact and known-object
     * bookkeeping) and any mimicked object, wipe the slot, lower the live count, and redraw the
     * grid.
     *
     * <p><b>Stub:</b> waiting on Chapter 6 (monsters and combat), because it touches the pack,
     * carried-object and health-tracking subsystems that arrive there. It does nothing, so
     * {@link #deleteMonster(Loc)} and {@link #compactMonsters(int)} delete nothing until it is done.
     *
     * <p>Function deleteMonsterIndex stubbed, commented in full on 260930.
     *
     * @param monsterIndex the index of the monster to delete
     */
    private void deleteMonsterIndex(int monsterIndex) {
        // Stub function : TODO: implement this
    }

    /**
     * Lights or darkens the town for day or night — the port of C's {@code cave_illuminate}
     * ({@code cave-map.c}), whose own comment is "Light or Darken the town".
     *
     * <p>C makes two sweeps. The first visits every grid: a grid with no floor or stairs in its 3x3
     * neighbourhood is skipped for memorising; by day, or for any grid that is not floor, it sets
     * {@code SQUARE_GLOW} and memorises the grid if it has floor or stairs near it; at night a floor
     * grid that is not bright terrain loses {@code SQUARE_GLOW} and is forgotten. The second sweep
     * lights and memorises the eight grids around every shop entrance. Both end by asking for a
     * full view update and redrawing the map, monster list and item list.
     *
     * <p><b>Stub:</b> not yet implemented; the level is left as it is.
     *
     * <p>Function illuminate stubbed, commented in full on 260930.
     *
     * @param daytime {@code true} for daylight; when ported this should come from
     *                {@code GameWorld.isDaytime} instead of being passed in
     */
    public void illuminate(boolean daytime) {
        // Stub function : TODO: implement this
        // TODO: When implementing this call GameWorld.isDaytime as opposed to taking in a boolean
    }

    /**
     * Places a new monster on the level at least a given distance from a grid — the port of C's
     * {@code pick_and_place_distant_monster} ({@code mon-make.c}), used to spawn wandering monsters
     * away from the player.
     *
     * <p>C picks random grids, up to {@code 10000} attempts, until it finds an empty one that is
     * strictly further than the distance from the grid to avoid, and that is not monster-restricted
     * while the level is still being generated. If it runs out of attempts it reports failure,
     * with a warning under the cheat options. Otherwise it places a monster there, allowing a group.
     *
     * <p><b>Stub:</b> not yet implemented; always reports failure.
     *
     * <p>Function pickAndPlaceDistantMonster stubbed, commented in full on 260930.
     *
     * @param toAvoid  the grid to keep the new monster away from
     * @param distance the distance the chosen grid must exceed
     * @param sleep    whether the placed monster starts asleep
     * @param depth    the depth to generate the monster at
     * @return {@code true} if a monster was placed
     */
    public boolean pickAndPlaceDistantMonster(Loc toAvoid, int distance, boolean sleep, int depth) {
        // Stub function : TODO: implement this
        return false;
    }

    /**
     * Marks the traps on a square as remembered by the player so they stay drawn — the port of C's
     * {@code square_memorize_traps} ({@code cave-square.c}).
     *
     * <p>{@link #decreaseTrapTimeout()} calls it when a trap's timeout expires on a grid the player
     * can see, so until it is ported that redraw still happens but the trap is not committed to the
     * player's memory.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * <p>Function squareMemorizeTraps stubbed, commented in full on 260930.
     *
     * @param grid the grid whose traps to memorize
     */
    public void squareMemorizeTraps(Loc grid) {
        // Stub function : TODO: implement this
    }

    /**
     * Ticks every trap on the level down by one turn, re-memorising and re-lighting any square whose
     * trap just became active again (timeout reaching zero) while it is in view. This is the
     * "Decrease trap timeouts" loop inside C's {@code process_world()} ({@code game-world.c}),
     * lifted out as its own method.
     *
     * <p>Every grid is visited and every trap on it is examined. A trap with a running timeout loses
     * one; a trap whose timeout has just reached zero marks its grid as changed, and a trap that
     * was already at zero is untouched. A changed grid is only memorised and redrawn if the player
     * can currently see it, so a trap re-arming out of sight waits until the player next sees it.
     *
     * <p>The local {@code width} and {@code height} shadow the fields of the same names; they come
     * from the array and equal the fields.
     *
     * <p><b>Outstanding:</b> {@link #squareMemorizeTraps(Loc)} is a stub, so the memorising step of
     * a change does nothing yet. The countdown and the redraw are complete.
     *
     * <p>Function decreaseTrapTimeout coded before 260930, commented in full on 260930.
     */
    public void decreaseTrapTimeout() {
        int width = squares.length;
        int height = squares[0].length;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Square square = squares[x][y];
                boolean changed = false;
                for (Trap trap : square.getTraps()) {
                    if (trap.getTimeout() > 0) {
                        trap.decrementTimeout();
                        if (trap.getTimeout() == 0) changed = true;
                    }
                }
                if (changed && square.isSeen()) {
                    squareMemorizeTraps(Loc.row(y).col(x));
                    squareLightSpot(Loc.row(y).col(x));

                }
            }
        }
    }

    /**
     * Returns a read-only view of this chunk's master object list, the counterpart of C's
     * {@code c->objects} array ({@code struct chunk}, {@code src/cave.h}). C has no accessor for it;
     * callers index the array directly.
     *
     * <p>The view is live: objects added or removed through the chunk show up in it, but attempting
     * to modify it throws {@link UnsupportedOperationException}, so changes have to go through the
     * chunk's own object methods.
     *
     * <p>Function getObjects coded before 260929, commented in full on 260929.
     *
     * @return an unmodifiable view of the objects lying on this chunk's floor
     */
    public List<ItemObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Clears this chunk's sound map back to silence, the first step of C's {@code make_noise}
     * ({@code game-world.c}). C sets every interior grid, {@code 1 .. dimension - 2} in each
     * direction, to {@code 0}, then marks the player's grid and spreads the noise outwards through
     * grids that do not carry {@code TF_NO_FLOW}. The border ring is left as it was: the spread can
     * write there when the terrain allows flow, so a port that zeroes the border as well is not
     * exactly equivalent.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the monster flow work in Chapter 4. It does
     * nothing, so the noise map keeps whatever it held. It is expected to zero the interior in
     * place rather than replace the map, which would also keep the reference returned by
     * {@link #getNoise()} valid.
     *
     * <p>Function resetNoise stubbed, commented in full on 260930.
     */
    public void resetNoise() {
        // Stubbed
        // TODO: Implement as part of Chapter 4 
    }

    /**
     * Ages every scent trail on the level by one turn, making all existing scent one step staler.
     * Ports the "update scent for all grids" loop that opens C's {@code update_scent} ({@code
     * src/game-world.c}): only grids that already carry scent ({@code > 0}) are incremented, so
     * never-visited grids stay at the {@code 0} baseline, and only the interior is scanned (the
     * outermost ring is skipped, matching the {@code 1 .. dimension - 2} bounds in C).
     *
     * <p>Only that first loop of {@code update_scent()} is ported here. C then returns early for a
     * player with covered tracks and otherwise lays fresh scent around the player from a 5x5
     * strength table; the caller is responsible for those steps.
     *
     * <p>Function updateScent coded before 260930, commented in full on 260930.
     */
    public void updateScent() {
        // ignore outside boundary of cave
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (scent.getValue(y, x) > 0) {
                    scent.setValue(y, x, scent.getValue(y, x) + 1);
                }
            }
        }
    }

    /**
     * Returns this chunk's sound map — the per-grid noise distances from the player used by
     * monster hearing to home in along passable terrain. Ports access to C's {@code
     * cave->noise} ({@code src/cave.h}). The map is live, not a copy. {@link #resetNoise()} is a
     * stub, so nothing here changes it yet.
     *
     * <p>Function getNoise coded before 260930, commented in full on 260930.
     *
     * @return the noise {@link Heatmap} for this chunk
     */
    public Heatmap getNoise() {
        return noise;
    }

    /**
     * Returns this chunk's scent map — the per-grid age of the player's scent trail, read by
     * monster smell to track the player along open floor. Ports access to C's {@code cave->scent}
     * ({@code src/cave.h}). The map is live, and {@link #updateScent()} ages it in place.
     *
     * <p>Function getScent coded before 260930, commented in full on 260930.
     *
     * @return the scent {@link Heatmap} for this chunk
     */
    public Heatmap getScent() {
        return scent;
    }

    /**
     * Tells the player how this level feels, the port of C's {@code display_feeling}
     * ({@code cave.c}).
     *
     * <p>Level feeling is Angband's way of hinting at what is on a level before the player has
     * walked it: one reading for danger and one for the quality of the loot. The two are learned at
     * different rates — the danger half is available on arrival, while the object half only firms up
     * once enough of the level has been explored — which is what the flag selects between.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the message and level-feeling subsystems; takes
     * no action, so the player currently arrives on a level without being told anything about it.
     *
     * <p>Function displayFeeling stubbed, commented in full on 260930.
     *
     * @param objectOnly {@code true} to report only the object half of the feeling, as C does when
     *                   the threshold for knowing it has just been crossed
     */
    public void displayFeeling(boolean objectOnly) {
        // Stub class TODO: Implement
    }

    /**
     * Recalculates everything the player can currently see, the port of C's {@code update_view}
     * ({@code cave-view.c}). This is the method that answers "what is visible from where the player
     * is standing now", and it is called whenever something could have changed that answer — a step
     * taken, a light lit or spent, terrain altered, blindness coming or going.
     *
     * <p>The visibility flags are rebuilt from scratch rather than edited. {@link #markWasSeen()}
     * first copies the current answer into {@code SQUARE_WASSEEN} and wipes {@code SQUARE_VIEW},
     * {@code SQUARE_SEEN} and {@code SQUARE_CLOSE_PLAYER} across the level, so the sweeps below
     * start from an empty board while the previous answer survives for comparison.
     * {@link #calcLighting(Player)} then recomputes every grid's light level, because what can be
     * seen depends on what is lit.
     *
     * <p>The player's own grid is handled before the sweeps and by hand. {@code SQUARE_VIEW} goes on
     * unconditionally — there is always a line of sight to where you are standing — but
     * {@code SQUARE_SEEN} and {@code SQUARE_CLOSE_PLAYER} only follow if there is something to see
     * by: a light being carried, terrain that is lit anyway, or the {@code PF_UNLIGHT} personality
     * that sees in the dark. A player in a dark corridor with a spent lantern is therefore in view
     * of their own grid without seeing it.
     *
     * <p>The blind clause that follows asks its two questions of two different chunks, exactly as C
     * does. Whether the grid is known is asked of this, the live level, while whether it is passable
     * is asked of {@link Player#getCave()}, the player's remembered copy — the terrain the player
     * believes is there. A blind player standing on a grid they remember as impassable is holding a
     * memory that reality has just disproved, since they are standing on it, so
     * {@link #squareForget(Loc)} drops the remembered terrain rather than leaving a wall drawn
     * underneath them. C notes that a variant with a timed effect allowing movement through
     * impassable terrain would have to revisit this, as the contradiction would no longer be one.
     *
     * <p>Two full-level sweeps then run in row-major order, and they are two rather than one.
     * {@link #updateViewOne(Loc, Player)} decides for each grid whether line of sight reaches it and
     * marks it viewed and perhaps seen; only once that has settled for every grid does
     * {@link #updateOne(Loc, Player)} sweep again to compare each grid against
     * {@code SQUARE_WASSEEN}, act on the grids whose visibility changed, and clear the snapshot flag
     * behind it. Keeping the passes separate means no grid is ever judged against a view that is
     * still half recalculated.
     *
     * <p>Function updateView coded before 260828, commented in full on 260828.
     *
     * @param player the player whose view is being recalculated; supplies the grid the view is
     *               centred on, the light carried, the blindness timer and the personality flags
     */
    public void updateView(Player player) {
        // Record the current view
        markWasSeen();

        // Calculate light levels
        calcLighting(player);

        // Assume we can view the player grid
        getSquare(player.getGrid()).sqInfoOn(SquareEnum.SQUARE_VIEW);

        if (player.getStateLight() > 0 || squareIsLit(player.getGrid())
                || player.hasPlayerFlag(PlayerFlag.PF_UNLIGHT)) {
            getSquare(player.getGrid()).sqInfoOn(SquareEnum.SQUARE_SEEN);
            getSquare(player.getGrid()).sqInfoOn(SquareEnum.SQUARE_CLOSE_PLAYER);
        }

        /*
         * If the player is blind and in terrain that was remembered to be
         * impassable, forget the remembered terrain.  This will have to be
         * modified in variants that have timed effects which allow a player
         * to move through impassable terrain.
         */
        if (player.getTimedEffect(TimedEffect.TMD_BLIND) != 0 && isKnown(player.getGrid())
                && !player.getCave().squareIsPassable(player.getGrid())) {
            squareForget(player.getGrid());
        }

        // Squares we have LoS to get marked as in the view, and perhaps seen
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                updateViewOne(Loc.row(y).col(x), player);
            }
        }

        // Update each grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                updateOne(Loc.row(y).col(x), player);
            }
        }
    }

    /**
     * Settles what one grid's visibility means now that the view has been recalculated, the port of
     * C's {@code update_one} ({@code cave-view.c}). {@link #updateView(Player)} sweeps every grid of
     * the level twice: the first sweep decides which grids line of sight reaches and marks them
     * seen, and this method is the second sweep, which compares that answer against the one recorded
     * before the recalculation and acts on the difference.
     *
     * <p>Blindness is applied first and overrides everything the light and sight calculation
     * decided: a blind player sees nothing, so both {@code SQUARE_SEEN} and
     * {@code SQUARE_CLOSE_PLAYER} come off here rather than being withheld earlier. A sighted player
     * instead gets each currently seen grid checked for a trap to reveal, which is how walking into
     * view of a trap is what discovers it.
     *
     * <p>The two comparisons that follow are deliberately not an if/else. Because the blind branch
     * above can clear {@code SQUARE_SEEN} between them, the seen and unseen tests are asked
     * independently of one another, and a grid can satisfy neither. A grid crossing from unseen to
     * seen is noted and redrawn; a grid crossing the other way is only redrawn, since there is
     * nothing new to learn about a grid that has just gone out of sight.
     *
     * <p>The level feeling is collected on the unseen-to-seen crossing. A grid carrying
     * {@code SQUARE_FEEL} counts once towards {@code feelingSquares} and then has the flag cleared,
     * so the same grid cannot be counted again on a later pass; the test, the count and the clear in
     * that order are what make {@link #squareIsFeel(Loc)} a pure read. The feeling is announced on
     * the exact pass that takes the count to {@code feelingNeed} — C tests equality, not
     * {@code >=}, so a count that somehow overshot would never announce — and is suppressed while
     * {@code onlyPartial} is set, which is the flag the interface raises when it is rebuilding a
     * character's state rather than playing a turn, so that the arrival on a new level makes the
     * announcement instead.
     *
     * <p>{@code SQUARE_WASSEEN} comes off unconditionally at the foot, on every grid and whichever
     * branches ran. That is what leaves the level clean for the next recalculation, which begins by
     * writing the record afresh in {@link #markWasSeen()}.
     *
     * <p><b>Outstanding:</b> {@link #squareRevealTrap(Loc, boolean, boolean)} and
     * {@link #squareNoteSpot(Loc)} are stubs awaiting chapter 4, and {@link #displayFeeling(boolean)}
     * awaits the message subsystem, so trap discovery, remembering the contents of a newly seen grid
     * and the feeling message itself take no effect yet. The counting, the flag work and the redraws
     * around them are complete.
     *
     * <p>Function updateOne coded on 260828, commented in full on 260828.
     *
     * @param grid   the grid to settle
     * @param player the player whose view has just been recalculated
     */
    private void updateOne(Loc grid, Player player) {
        // remove view if player is blind
        if (player.getTimedEffect(TimedEffect.TMD_BLIND) != 0) {
            getSquare(grid).sqInfoOff(SquareEnum.SQUARE_SEEN);
            getSquare(grid).sqInfoOff(SquareEnum.SQUARE_CLOSE_PLAYER);
        } else if (squareIsSeen(grid)) {
            squareRevealTrap(grid, false, true);
        }

        // square went from unseen -> seen
        if (squareIsSeen(grid) && !squareWasSeen(grid)) {
            if (squareIsFeel(grid)) {
                feelingSquares++;
                getSquare(grid).sqInfoOff(SquareEnum.SQUARE_FEEL);
                // Don't disaply feeling if it will display for the new level  
                if (feelingSquares == GameConstants.getWorldFeelingNeed()
                        && !player.getPlayerUpkeep().isOnlyPartial()) {
                    displayFeeling(true);
                    player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_FEELING);
                }
            }

            squareNoteSpot(grid);
            squareLightSpot(grid);
        }

        // Square went from seen -> unseen
        if (!squareIsSeen(grid) && squareWasSeen(grid))
            squareLightSpot(grid);

        getSquare(grid).sqInfoOff(SquareEnum.SQUARE_WASSEEN);
    }

    /**
     * Memorises whatever is interesting in a grid the player can now see, the port of C's
     * {@code square_note_spot} ({@code cave-map.c}). Seeing a grid and remembering it are two
     * different things: the view calculation decides what is currently visible, and this is the
     * method that writes what was visible into the player's own memory of the level, so that the
     * map still shows the staircase or the pile of loot after the player has walked away.
     *
     * <p>C guards on two conditions before doing anything. The chunk must be the level the player
     * is actually on, since memorising into the player's map from a chunk they are not standing in
     * would record a level they have never visited; and the grid must be seen, or else be the
     * player's own grid, which is what lets a blind player still know the square under their feet.
     * What follows is three separate acts of memory: the pile of objects is learned exactly, a
     * secret trap on the grid is revealed and then the traps are memorised, and finally the terrain
     * itself is memorised — but only if what is currently remembered about it is wrong, which is
     * the {@code square_ismemorybad} test. The object memory and the terrain memory are kept
     * deliberately apart so that picking a detected object off a dark floor does not memorise the
     * floor, and dropping an object into a remembered but unseen grid does not memorise the object.
     *
     * <p>The one caller here is {@link #updateOne(Loc, Player)}, on the pass where a grid crosses
     * from unseen to seen, which is C's primary call site too. C calls it from several others —
     * when an object is created or dropped, when terrain changes from floor to non-floor, and when
     * a trap is set — the general rule being that it is called whenever what the player ought to
     * remember about a grid has been called into question.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the object and trap subsystems in chapter 4; it
     * does nothing for every input. Until it is filled in, a newly seen grid is redrawn by
     * {@link #squareLightSpot(Loc)} but nothing about it is committed to the player's memory of the
     * level.
     *
     * <p>Function squareNoteSpot stubbed on 260828, commented in full on 260828.
     *
     * @param grid the grid whose contents are to be memorised
     */
    private void squareNoteSpot(Loc grid) {
        // STUB function to be implemented in chapter 4 
        // 
        // TODO: Implement in chapter 4
    }

    /**
     * Tests whether a grid is one of the level's feeling trigger squares, the port of C's
     * {@code square_isfeel} ({@code cave-square.c}). Level generation scatters a fixed number of
     * these markers across the interesting parts of a new level, and the player earns the level
     * feeling by walking far enough to see enough of them. This method only reads the marker; the
     * counting and the clearing of the flag belong to the caller.
     *
     * <p>The one caller is {@link #updateOne(Loc, Player)}, which asks the question exactly when a
     * grid crosses from unseen to seen, then clears {@code SQUARE_FEEL} so the same grid cannot be
     * counted twice. That ordering — test, count, clear — matches C's {@code update_one}
     * ({@code cave-view.c}), and it is why this method is a pure read with no side effects of its
     * own.
     *
     * <p>C asserts that the grid is in bounds, which would halt the game on a bad grid. Following
     * the boundary convention of the other square predicates here, an out-of-bounds grid answers
     * false instead, so a stray grid simply triggers no feeling.
     *
     * <p>Function squareIsFeel coded on 260828, commented in full on 260828.
     *
     * @param grid the Loc of the square to test
     * @return true if the square is a feeling trigger square
     */
    private boolean squareIsFeel(Loc grid) {
        if (!inBounds(grid)) return false;
        Square square = getSquare(grid);
        return square.isFeel();
    }

    /**
     * Reveals the player traps hidden in a grid, the port of C's {@code square_reveal_trap}
     * ({@code trap.c}). A trap set against the player starts out invisible, and it stays that way
     * until the player is good enough to spot it: the trap carries a power, the player carries a
     * searching skill, and the trap becomes visible the moment the skill reaches the power. There
     * is no searching command in 4.2 — the check is made for free, on every grid the player can
     * see, every time the view is recalculated.
     *
     * <p>C walks the grid's whole trap list rather than stopping at the first hit, skipping the
     * entries that are not player traps and, unless {@code always} is set, the ones whose power
     * outruns the player's searching skill. Each surviving invisible trap is turned visible, and
     * the grid's traps are then memorised into the player's own map. The count of newly revealed
     * traps is what drives the tail: if it is non-zero the grid is memorised and redrawn, and if
     * {@code domsg} is set the player is told, with the message choosing singular or plural on that
     * same count. C returns whether anything was found, which lets a caller such as the magic
     * mapping effect report that its detection actually turned something up.
     *
     * <p>The parameters carry C's {@code always} and {@code domsg} in that order. {@code always}
     * bypasses the skill test, so a grid can be stripped of its secrets outright — the trap
     * detection effect passes it true, while the view calculation passes false and lets the player's
     * skill decide. {@code domsg} governs only whether the discovery is announced, which is why the
     * view calculation asks for the message but the terrain projection code does not: a trap
     * revealed by a passing spell should not interrupt with a line about the player having found it.
     *
     * <p>The one caller here is {@link #updateOne(Loc, Player)}, which asks it of every grid the
     * player can currently see and is not blind for, passing {@code (false, true)} exactly as C's
     * {@code update_one} ({@code cave-view.c}) does. C has three further call sites — the same
     * {@code (false, true)} from {@code square_note_spot} ({@code cave-map.c}), {@code (false,
     * false)} from the terrain projection in {@code project-feat.c}, and {@code (true, false)} from
     * the detection effect in {@code effect-handler-general.c} — which will arrive with the
     * subsystems that own them.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the trap subsystem in chapter 4; it does nothing
     * for every input, so a trap the player is standing in front of is never revealed and no trap is
     * ever committed to their map. Two things are to be settled when it is filled in: the parameters
     * want C's names, and C's {@code bool} return is dropped here, which the detection effect will
     * need back when it arrives.
     *
     * <p>Function squareRevealTrap stubbed on 260828, commented in full on 260828.
     *
     * @param grid the grid whose player traps are to be revealed
     * @param b    C's {@code always}: true to reveal regardless of the player's searching skill
     * @param b1   C's {@code domsg}: true to announce the discovery to the player
     */
    private void squareRevealTrap(Loc grid, boolean b, boolean b1) {
        // STUB function to be implemented in chapter 4 
        // 
        // TODO: Implement in chapter 4
    }

    /**
     * Reports whether the player is standing in the dark, the port of C's {@code no_light}
     * ({@code cave-view.c}). The question is not how much light the player carries but whether
     * their own grid has ended up marked as seen by the most recent view calculation, so a player
     * with no light of their own standing in a lit room is not in the dark, and a player carrying a
     * torch is never in the dark. Callers pair it with blindness — C's several call sites all read
     * {@code p->timed[TMD_BLIND] || no_light(p)} — to decide whether a task that needs light can be
     * attempted at all.
     *
     * <p>C reads the global {@code cave} rather than a chunk handed to it, so the answer there is
     * always about the level the player is actually on; here the question is asked of whichever
     * chunk the method is called on, and it is the caller's business to ask the current level.
     *
     * <p>C asserts that the grid is in bounds. {@link #squareIsSeen(Loc)} answers false for an
     * out-of-bounds grid instead of failing, which makes an out-of-bounds player count as being in
     * the dark rather than halting the game.
     *
     * <p>Function noLight coded on 260828, commented in full on 260828.
     *
     * @param player the player whose grid is tested
     * @return true if the player's grid is not currently seen
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean noLight(Player player) {
        return (!squareIsSeen(player.getGrid()));
    }

    /**
     * Decides whether a single grid belongs in the player's current view, the port of C's
     * {@code update_view_one} ({@code cave-view.c}). The caller sweeps every grid of the chunk and
     * hands each one to this method; a grid that passes ends up in {@link #becomeViewable(Loc,
     * Player, boolean)}, and one that does not is simply left alone.
     *
     * <p>Two independent questions are answered here. The first is whether the grid is close enough
     * to be lit by the player themselves: {@code close} is the approximate distance measured against
     * the player's current light radius, and it is passed down rather than acted on here. The second
     * is whether line of sight reaches the grid at all, which is the {@link ChunkUtils#los(Chunk,
     * Loc, Loc)} call at the foot of the method. Distance is computed before the sight-range test so
     * that {@code close} exists whichever way the method exits, but a grid beyond
     * {@code maxSight} returns without ever consulting line of sight.
     *
     * <p>Players with {@link PlayerFlag#PF_UNLIGHT} carrying no real light of their own — a current
     * radius of one or zero — replace the ordinary radius test with a level-scaled one, so that a
     * character who sees in the dark gains reach as they gain levels, and loses it again as soon as
     * they pick up a light source. The division by six is integer division on a non-negative level,
     * so the radius grows one grid every six levels.
     *
     * <p>The bulk of the method is the wall-lighting special case. Line of sight is traced to the
     * grid's own centre, and for a wall that line has to pass through the wall itself, so the naive
     * test fails for the very grids the player is looking straight at:
     *
     * <pre>
     * #1#############
     * #............@#
     * ###############
     * </pre>
     *
     * <p>The wall marked {@code 1} is plainly visible, but the line to it runs into the adjacent
     * wall cell first. So a wall borrows the line of sight of the grid one step toward the player,
     * {@code checkX}/{@code checkY} stepping each coordinate independently and leaving a coordinate
     * alone when it already matches the player's, which keeps the borrowed grid adjacent and inside
     * the level. Two conditions cancel the loan. If the grid being borrowed from is itself a wall
     * the loan is refused, since otherwise both faces of a double-thickness wall would light up. And
     * if the grid was reached by a knight's move — offsets of two and one in either order — the loan
     * is refused when the wall is being approached around a corner, which is the pair of
     * {@code squareAllowsLOS} tests on the intervening grids. In both cases the check grid falls
     * back to the grid itself and the ordinary line-of-sight test decides.
     *
     * <p>Note that the borrowed line of sight decides only whether {@code becomeViewable} is called;
     * the grid handed to it is always the original one, never the check grid.
     *
     * <p>Function updateViewOne coded before 260828, commented in full on 260828.
     *
     * @param grid   the location being considered for the view
     * @param player the player whose view is being built, supplying the grid distances are measured
     *               from, the light radius, and the level used by the unlight radius
     */
    private void updateViewOne(Loc grid, Player player) {
        int x = grid.getX();
        int y = grid.getY();
        int checkX = x;
        int checkY = y;
        int distance = grid.distance(player.getGrid());
        boolean close = distance < player.getStateLight();

        // Too far away
        if (distance > GameConstants.getPlayerMaxSight()) return;

        // UNLIGHT players have a special radius of view
        if (player.hasPlayerFlag(PlayerFlag.PF_UNLIGHT) && player.getPlayerState().getCurLight() <= 1) {
            close = distance < (2 + player.getLevel() / 6 - player.getPlayerState().getCurLight());
        }

        /* Special case for wall lighting. If we are a wall and the square in
         * the direction of the player is in LOS, we are in LOS. This avoids
         * situations like:
         * #1#############
         * #............@#
         * ###############
         * where the wall cell marked '1' would not be lit because the LOS
         * algorithm runs into the adjacent wall cell.
         */
        if (!squareAllowsLOS(grid)) {
            int deltaX = x - player.getGrid().getX();
            int deltaY = y - player.getGrid().getY();
            int absX = Math.abs(deltaX);
            int absY = Math.abs(deltaY);
            int signX = deltaX > 0 ? 1 : -1;
            int signY = deltaY > 0 ? 1 : -1;
            int playerX = player.getGrid().getX();
            int playerY = player.getGrid().getY();

            checkX = (x < playerX) ? (x + 1) : (x > playerX) ? (x - 1) : x;
            checkY = (y < playerY) ? (y + 1) : (y > playerY) ? (y - 1) : y;

            // Check that the cell we're trying to steal LoS from isn't a
            // wall. If we don't do this, double-thickness walls will have
            // both sides visible.
            if (!squareAllowsLOS(Loc.row(checkY).col(checkX))) {
                checkX = x;
                checkY = y;
            }

            // Check if we got here via a 'knight's move', and if so
            // don't steal LoS
            if (absX == 2 && absY == 1) {
                if (squareAllowsLOS(Loc.row(y).col(x - signX))
                        && !squareAllowsLOS(Loc.row(y - signY).col(x - signX))) {
                    checkX = x;
                    checkY = y;
                }
            } else if (absX == 1 && absY == 2) {
                if (squareAllowsLOS(Loc.row(y - signY).col(x))
                        && !squareAllowsLOS(Loc.row(y - signY).col(x - signX))) {
                    checkX = x;
                    checkY = y;
                }
            }
        }

        if (los(this, player.getGrid(), Loc.row(checkY).col(checkX))) {
            becomeViewable(grid, player, close);
        }
    }

    /**
     * Adds a grid to the player's current view, the port of C's {@code become_viewable}
     * ({@code cave-view.c}).
     *
     * <p>Being in view and being seen are two different things, and this method is where they part
     * company. {@code SQUARE_VIEW} says only that line of sight reaches the grid; it is set for
     * every grid the caller has established a line to. {@code SQUARE_SEEN} — the flag that actually
     * decides whether the grid is drawn as its true terrain rather than from memory — needs light as
     * well, and is set by either of two independent routes: the grid is close enough to fall inside
     * the player's own light radius, which is what the {@code close} argument carries down from
     * {@link #updateViewOne(Loc, Player)}; or the grid is lit by anything at all, which is
     * {@link #squareIsLit(Loc)} testing the light level accumulated by {@code calcLighting} rather
     * than the {@code SQUARE_GLOW} flag. {@code SQUARE_CLOSE_PLAYER} rides along with the first
     * route only.
     *
     * <p>The early return on a grid already in view is what keeps this idempotent: the visibility
     * sweep can reach the same grid by more than one path, and without the guard a grid could pick
     * up {@code SQUARE_SEEN} on a later visit that the first visit had deliberately withheld.
     *
     * <p>Walls take the longer path through the lit branch. A wall is opaque, so light never reaches
     * the face the player is looking at from the wall's own grid — what matters is whether the grid
     * one step back toward the player is lit, since that is the light falling on the visible face.
     * The two nested conditionals step {@code checkX} and {@code checkY} one square toward the
     * player independently, leaving a coordinate alone when it already matches the player's, which
     * is why the check grid is always adjacent and can never leave the level. A lit wall whose
     * approach is dark stays unseen.
     *
     * <p>Function becomeViewable coded before 260828, commented in full on 260828.
     *
     * @param grid   the location being brought into view
     * @param player the player whose view is being built, supplying the grid the light is measured
     *               from
     * @param close  true if the grid lies within the player's light radius, in which case it is
     *               seen regardless of the level's own lighting
     */
    private void becomeViewable(Loc grid, Player player, boolean close) {
        int x = grid.getX();
        int y = grid.getY();

        // already visible - just return
        if (squareIsView(grid)) return;

        // Add the grid to the view, make it seen if it's close enough to the player
        getSquare(grid).sqInfoOn(SquareEnum.SQUARE_VIEW);
        if (close) {
            getSquare(grid).sqInfoOn(SquareEnum.SQUARE_SEEN);
            getSquare(grid).sqInfoOn(SquareEnum.SQUARE_CLOSE_PLAYER);
        }

        // Mark lit grids, and walls near to them, as seen
        if (squareIsLit(grid)) {
            if (!squareAllowsLOS(grid)) {
                // for walls, check for a lit grid closer to the player
                int checkX = (x < player.getGrid().getX() ? x + 1 :
                        (x > player.getGrid().getX() ? x - 1 : x));
                int checkY = (y < player.getGrid().getY() ? y + 1 :
                        (y > player.getGrid().getY() ? y - 1 : y));

                if (squareIsLit(Loc.row(checkY).col(checkX))) {
                    getSquare(grid).sqInfoOn(SquareEnum.SQUARE_SEEN);
                }
            } else {
                getSquare(grid).sqInfoOn(SquareEnum.SQUARE_SEEN);
            }
        }
    }

    /**
     * Forgets the terrain remembered at a grid, the port of C's {@code square_forget}
     * ({@code cave-square.c}).
     *
     * <p>Forgetting is not a flag being cleared: the player's remembered copy of the level simply
     * has its feature at this grid overwritten with {@code FEAT_NONE}, the "nothing/unknown"
     * terrain, which is what {@link #isKnown(Loc)} tests for. The real level is never touched, so
     * the grid keeps whatever terrain it actually has and only the player's memory of it is lost.
     *
     * <p>The guard is C's {@code if (c != cave) return;} — the operation is only meaningful when
     * invoked on the live level, since it is the live level's chunk that owns the boundary across to
     * the player's remembered copy. Called on the remembered copy itself, or on any other chunk, it
     * does nothing at all. {@link #squareSetKnownFeat(Loc, Feature)} repeats that same test, so the
     * guard here is C's belt and braces rather than the only thing standing between a stale chunk
     * and the player's memory.
     *
     * <p>Function squareForget coded before 260828, commented in full on 260828.
     *
     * @param grid the location whose remembered terrain is to be forgotten
     */
    private void squareForget(Loc grid) {
        if (currentLevel != this)
            return;

        Feature none = TerrainRegistry.lookupFeature(TerrainFlags.FEAT_NONE);
        squareSetKnownFeat(grid, none);
    }

    /**
     * Recomputes the light level of every grid on the level from scratch, the port of C's
     * {@code calc_lighting} ({@code cave-view.c}), which C in turn notes was taken from Sil.
     *
     * <p>Lighting is rebuilt in two stages. The first stage sweeps the whole chunk and assigns each
     * grid a base level from its terrain alone: {@code 1} for a grid marked as permanently glowing
     * that either lets light through or, being a wall, passes
     * {@link #glowCanLightWall(Player, Loc)}, and {@code 0} for everything else. Bright terrain then
     * adds {@code 2} on top of that base — so a glowing bright grid reaches {@code 3}, not
     * {@code 2} — and spills a further {@code 1} into each of the eight neighbours, subject to the
     * same rule that a wall is only brightened when {@link #sourceCanLightWall(Player, Loc, Loc)}
     * says the player is placed to see the face being lit. The neighbour set is C's
     * {@code ddgrid_ddd[0..7]}: the four cardinals and four diagonals, and deliberately not the
     * {@code (0, 0)} centre entry that closes that table, which would otherwise brighten the bright
     * grid a second time.
     *
     * <p>The sweep runs in row-major order and writes the base level with an assignment, so a
     * neighbour spill that lands on a grid the sweep has not reached yet is later overwritten when
     * that grid's own turn comes. This is C's behaviour rather than an oversight in the port, and
     * the ordering is kept so the two produce the same numbers grid for grid.
     *
     * <p>The second stage adds the moving sources on top through {@link #addLight(Player, Loc, int,
     * int)}: first the player's own light, then every monster on the level. A monster is skipped if
     * it is dead, if it is camouflaged and so not showing its light, if its race emits nothing, or
     * if it is far enough away that even its reach cannot come within the player's maximum sight.
     * Note C tests the intensity for zero only after computing the radius from it, and the order is
     * preserved here. Each source is passed {@code radius == |intensity| - 1}, so an unlight source
     * (a negative intensity) darkens grids by the same falloff rule that a lamp brightens them.
     *
     * <p>Finally the player's own grid is compared against the level it held on entry, and the
     * light indicator is flagged for redraw only if it actually changed.
     *
     * <p><em>Function calcLighting coded before 260828, commented in full on 260828.</em>
     *
     * @param player the player the lighting is calculated for; supplies the light radius carried,
     *               the grid it is centred on, and the viewpoint used to decide which wall faces
     *               are worth lighting
     */
    private void calcLighting(Player player) {
        int oldLight = squareLight(player.getGrid());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Loc grid = Loc.row(y).col(x);

                if (squareIsGlow(grid)
                        && (squareAllowsLOS(grid) || glowCanLightWall(player, grid))) {
                    getSquare(grid).setLight(1);
                } else
                    getSquare(grid).setLight(0);

                // Squares with bright terrain have intensity 2
                if (squareIsBright(grid)) {
                    getSquare(grid).setLight(getSquare(grid).getLight() + 2);
                    for (DirectionEnum direction : DirectionEnum.surroundingDirections()) {
                        Loc adjacentGrid = grid.sum(direction.ddgrid());
                        if (!inBounds(adjacentGrid)) continue;

                        /*
                         * Only brighten a wall if the player
                         * is in position to view the face
                         * that's lit up.
                         */
                        if (!squareAllowsLOS(adjacentGrid)
                                && !sourceCanLightWall(player, grid, adjacentGrid)) continue;

                        getSquare(adjacentGrid).setLight(getSquare(adjacentGrid).getLight() + 1);
                    }
                }
            }
        }

        // Light around the player
        int light = player.getStateLight();
        int radius = Math.abs(light) - 1;
        addLight(player, player.getGrid(), radius, light);

        // Scan monster list and add monster light or darkness
        for (Monster mon : getMonsters()) {
            // skip null or dead monsters
            if (mon == null || mon.getMonsterRace() == null) continue;

            if (mon.monsterIsCamouflaged()) continue;

            // Get light info for this monster
            light = mon.getMonsterRace().getLight();
            radius = Math.abs(light) - 1;

            // SKip monster not affecting light
            if (light == 0) continue;

            // Skip if the player can't see it
            if (player.getGrid().distance(mon.getGrid()) - radius > GameConstants.getPlayerMaxSight()) continue;

            addLight(player, mon.getGrid(), radius, light);
        }

        // Update light level indicator
        if (squareLight(player.getGrid()) != oldLight) {
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_LIGHT);
        }
    }

    /**
     * Adds the effect of one light source into the accumulated light levels of the grids around it.
     * The port of C's {@code add_light} ({@code cave-view.c}).
     *
     * <p>Light in Angband is accumulated rather than assigned: {@link #calcLighting(Player)} first
     * lays down a base level from permanently glowing and bright terrain, then each source in turn
     * adds its own contribution on top through this method. Every grid in the square of side
     * {@code 2 * radius + 1} centred on the source is visited, and the ones that survive three
     * filters have their light adjusted.
     *
     * <p>The filters, in C's order, are: the grid must lie inside the level; its
     * {@link Loc#distance(Loc)} from the source — the cheap integer approximation, not the true
     * Euclidean one — must not exceed {@code radius}, which rounds the visited square off to a
     * rough disc; and {@link ChunkUtils#los} must find an unbroken line from the source, so light
     * does not leak through walls. A wall grid itself then has to pass
     * {@link #sourceCanLightWall(Player, Loc, Loc)} as well, because a wall is only worth lighting
     * when the face this source lights is the face the player is looking at.
     *
     * <p>The contribution falls off with distance, and the arithmetic is written so that it does so
     * for darkness as well. A positive {@code inten} contributes {@code inten - dist}, brightest at
     * the source and fading outwards; a negative {@code inten} contributes {@code inten + dist},
     * darkest at the source and weakening outwards. Both reach zero at {@code dist == |inten|}, and
     * since callers pass {@code radius == |inten| - 1} the outermost ring visited still carries a
     * contribution of magnitude one. The value is added to whatever the grid already holds, so it
     * can be negative overall where an unlight source overlaps lit terrain.
     *
     * <p>C notes this is a brute-force approach: it sweeps the whole bounding square rather than
     * propagating outwards from the source and stopping at walls, and the port keeps that
     * behaviour so the resulting light levels match grid for grid.
     *
     * <p>A {@code radius} below zero makes both loops empty and the method a no-op, which is how a
     * player with no light at all is handled.
     *
     * <p><em>Function addLight coded before 260828, commented in full on 260828.</em>
     *
     * @param player     the player the lighting is calculated for; only used to judge which wall
     *                   faces are visible to them
     * @param sourceGrid the grid the light is emitted from
     * @param radius     the reach of the source in grids; grids further than this are untouched
     * @param inten      the intensity at the source, positive for light and negative for unlight
     */
    private void addLight(Player player, Loc sourceGrid, int radius, int inten) {
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                Loc grid = sourceGrid.sum(Loc.row(y).col(x));
                int dist = sourceGrid.distance(grid);
                if (!inBounds(grid)) continue;
                if (dist > radius) continue;
                if (!los(this, sourceGrid, grid)) continue;

                // Only light a wall if the face lit is possibly visible to the player
                if (!squareAllowsLOS(grid) && !sourceCanLightWall(player, sourceGrid, grid)) continue;

                // adjust the light level
                int currLight = getSquare(grid).getLight();
                if (inten > 0) {
                    // light decreasing further away
                    getSquare(grid).setLight(currLight + (inten - dist));
                } else {
                    // Light increasing further away
                    getSquare(grid).setLight(currLight + (inten + dist));
                }
            }
        }
    }

    /**
     * Decides whether a permanently glowing wall shows a <em>lit</em> face to the player, the port
     * of C's {@code glow_can_light_wall} ({@code cave-view.c}).
     *
     * <p>A wall is drawn as one grid but lit as a block with four faces, and the glow flag on the
     * wall itself says nothing about which of those faces is actually alight. What lights a face is
     * an open glowing grid standing against it, so the question this method answers is whether any
     * of the open grids on the player's side of the wall is glowing and placed to light the face
     * the player is looking at. The caller has already established that the wall carries the glow
     * flag; that is never re-tested here.
     *
     * <p>Both the wall's neighbours and the player are reduced to a single bearing.
     * {@code playerNext} is one step from the wall towards the player, obtained through
     * {@link Loc#motionDir(Loc)} and {@link Loc#nextGrid(DirectionEnum)}. Two cases settle
     * immediately: if that step lands back on the wall grid the player is standing in the wall,
     * sees every face, and one of them is lit; and if {@code playerNext} is itself an open glowing
     * grid, it lights the face turned towards the player.
     *
     * <p>Otherwise the two grids flanking {@code playerNext} are tried, and each must be open,
     * glowing, and pass {@link #sourceCanLightWall(Player, Loc, Loc)} — the flanker lights a face,
     * but not necessarily the face the player can see. Which pair the flankers are depends on the
     * bearing. When the step is diagonal, they are the two cardinal neighbours of the wall that lie
     * beside it, each built from one coordinate of the wall and one of {@code playerNext}; both are
     * therefore inside the chunk already and need no bounds test. When the step is a straight one
     * along a row or a column, the flankers are the grids to either side of {@code playerNext}
     * across that bearing, and those can fall off the edge of the map, so each is guarded by
     * {@link #inBounds(Loc)} first. C draws the same distinction, and the missing guards on the
     * diagonal pair are deliberate there rather than an omission carried over.
     *
     * <p>With every candidate exhausted the wall is glowing but has no lit face to show, and the
     * method returns {@code false}.
     *
     * <p><em>Function glowCanLightWall coded before 260828, commented in full on 260828.</em>
     *
     * @param player   the player the wall is being lit for; supplies the grid the lit face has to
     *                 be turned towards
     * @param wallGrid the location of the glowing wall under test
     * @return {@code true} if a face of the wall is both lit by a neighbouring glowing grid and
     * turned towards the player, {@code false} otherwise
     */
    private boolean glowCanLightWall(Player player, Loc wallGrid) {
        Loc playerNext = wallGrid.nextGrid(wallGrid.motionDir(player.getGrid()));
        Loc check;

        // If the player is in the wall grid, the player will see the lit face
        if (playerNext.equals(wallGrid)) return true;

        // If the grid in the direction of the player is not a wall, and is glowing
        // it'll illuminate the wall
        if (squareAllowsLOS(playerNext) && squareIsGlow(playerNext)) return true;

        // Try the two neighbouring squares adjacent to the one in the direction
        // of the player to see if one or more will illuminate the wall by
        // glowing. Those could be out of bounds if the direction isn't
        // diagonal.
        if (playerNext.getX() != wallGrid.getX()) {
            if (playerNext.getY() != wallGrid.getY()) {
                check = Loc.row(wallGrid.getY()).col(playerNext.getX());
                if (squareAllowsLOS(check) && squareIsGlow(check)
                        && sourceCanLightWall(player, check, wallGrid)) return true;
                check = Loc.row(playerNext.getY()).col(wallGrid.getX());
                if (squareAllowsLOS(check) && squareIsGlow(check)
                        && sourceCanLightWall(player, check, wallGrid)) return true;
            } else {
                check = Loc.row(wallGrid.getY() - 1).col(playerNext.getX());
                if (inBounds(check) && squareAllowsLOS(check) && squareIsGlow(check)
                        && sourceCanLightWall(player, check, wallGrid)) return true;
                check = Loc.row(wallGrid.getY() + 1).col(playerNext.getX());
                if (inBounds(check) && squareAllowsLOS(check) && squareIsGlow(check)
                        && sourceCanLightWall(player, check, wallGrid)) return true;
            }
        } else {
            check = Loc.row(playerNext.getY()).col(wallGrid.getX() - 1);
            if (inBounds(check) && squareAllowsLOS(check) && squareIsGlow(check)
                    && sourceCanLightWall(player, check, wallGrid)) return true;
            check = Loc.row(playerNext.getY()).col(wallGrid.getX() + 1);
            if (inBounds(check) && squareAllowsLOS(check) && squareIsGlow(check)
                    && sourceCanLightWall(player, check, wallGrid)) return true;
        }

        // Adjacent squares have all been tested and won't light the wall by glowing
        return false;
    }

    /**
     * Decides whether a wall would <em>appear</em> lit to the player when a light source sits at
     * {@code sourceGrid}, setting aside range and whether the line of sight is actually clear. The
     * port of C's {@code source_can_light_wall} ({@code cave-view.c}).
     *
     * <p>A wall is drawn as a single grid, but it is lit as a solid block with four faces. A light
     * source only ever illuminates the face pointing towards it, and the player only ever sees the
     * face pointing towards them, so the wall looks lit exactly when those are the same face — or
     * when one of the two is standing close enough to see or light more than one face at once.
     * That geometric question is all this method answers; the caller is left to decide whether the
     * light reaches that far and whether anything stands in the way.
     *
     * <p>Both parties are reduced to a bearing rather than a position. {@code sourceNext} is the
     * single step from the wall towards the light, and {@code playerNext} the single step from the
     * wall towards the player, each obtained through {@link Loc#motionDir(Loc)} and
     * {@link Loc#nextGrid}. Two positions anywhere along the same bearing therefore give the same
     * answer, which is what makes the test a cheap comparison of two adjacent grids instead of a
     * trace along a line.
     *
     * <p>A bearing of {@link uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum#DIR_NONE} carries a
     * zero offset, so the step lands back on the wall itself. That is the signal for "coincident
     * with the wall", and it is handled first for each of the two in turn: a light source inside
     * the wall lights every face, and a player inside the wall sees every face. Either way the
     * faces cannot disagree and the answer is true.
     *
     * <p>Otherwise the two bearings must share at least one component. Sharing both means the light
     * and the player are on the same side of the wall, looking at the one lit face, and the answer
     * is true outright. Sharing neither means they are looking at different faces, and the answer is
     * false. Sharing exactly one leaves a diagonal pair, and the shared component names the grid
     * beside the wall through which the player's view of the lit face has to pass:
     *
     * <pre>
     *  p
     * ###1#
     *  &#64;
     * </pre>
     *
     * <p>Here the light-emitting monster {@code p} and the player {@code @} both have line of sight
     * to the wall {@code 1}, but the lit face is hidden behind the wall immediately to the left of
     * {@code 1}. Testing that intervening grid with {@link #squareAllowsLOS(Loc)} is what rules the
     * case out.
     *
     * <p>That intervening grid is always orthogonally adjacent to the wall, so it can only fall
     * outside the level when the wall sits on the outermost row or column. C asserts in-bounds at
     * that point; {@link #squareAllowsLOS(Loc)} returns false instead, which leaves the wall unlit
     * — the same answer the level's permanent outer boundary would give anyway.
     *
     * <p><em>Function sourceCanLightWall coded before 260828, commented in full on 260828.</em>
     *
     * @param player     the player the appearance is judged for; only their grid is read
     * @param sourceGrid the grid the light is emitted from
     * @param wallGrid   the grid of the wall being lit
     * @return true if the lit face of the wall is the face the player is looking at, and nothing
     * beside the wall blocks their view of it; false otherwise
     */
    private boolean sourceCanLightWall(Player player, Loc sourceGrid, Loc wallGrid) {
        Loc sourceNext = wallGrid.nextGrid(wallGrid.motionDir(sourceGrid));

        /*
         * If the light source is coincident with the wall, all faces will be
         * lit, and the player can potentially see it if it's within range and
         * the line of sight isn't broken.
         */
        if (sourceNext.equals(wallGrid)) return true;

        /*
         * If the player is coincident with the wall, all faces of the wall are
         * visible to the player and the player can see whichever of those is
         * lit by the light source.
         */
        Loc playerNext = wallGrid.nextGrid(wallGrid.motionDir(player.getGrid()));
        if (playerNext.equals(wallGrid)) return true;

        Loc check;

        /*
         * For the lit face of the wall to be visible to the player, the
         * view directions from the wall to the player and the wall to the
         * light source must share at least one component.
         */
        if (sourceNext.getX() == playerNext.getX()) {
            /*
             * If the view directions share both components, the lit face
             * will be visible to the player if in range and the line of
             * sight isn't broken.
             */
            if (sourceNext.getY() == playerNext.getY()) return true;
            check = Loc.row(wallGrid.getY()).col(sourceNext.getX());
        } else if (sourceNext.getY() == playerNext.getY()) {
            check = Loc.row(sourceNext.getY()).col(wallGrid.getX());
        } else {
            /*
             * If the view directions don't share a component, the lit face
             * is not visible to the player.
             */
            return false;
        }

        /*
         * When only one component of the view directions is shared, take the
         * common component and test whether there's a wall there that would
         * block the player's view of the lit face.  That prevents instances
         * like this:
         *  p
         * ###1#
         *  @
         * where both the light-emitting monster, 'p', and the player, '@',
         * have line of sight to the wall, '1', but the face of '1' that would
         * be lit is blocked by the wall immediately to the left of '1'.
         */
        return squareAllowsLOS(check);
    }

    /**
     * Snapshots which grids the player can currently see, then wipes the live visibility flags
     * ready for them to be recalculated. The port of C's {@code mark_wasseen}
     * ({@code cave-view.c}).
     *
     * <p>Every grid that is seen right now has {@link SquareEnum#SQUARE_WASSEEN} turned on, and
     * then every grid on the level — seen or not — has {@link SquareEnum#SQUARE_VIEW},
     * {@link SquareEnum#SQUARE_SEEN} and {@link SquareEnum#SQUARE_CLOSE_PLAYER} turned off. The
     * three cleared flags are the answer to "what can be seen from where the player is standing",
     * and they are rebuilt from scratch on every update rather than edited, so the sweep that
     * follows starts from an empty board.
     *
     * <p>The snapshot is what makes that affordable. Redrawing the whole level after every step
     * would be wasteful, so {@code updateView} only redraws the grids whose visibility changed —
     * and a change can only be spotted by comparing the new value against the old one. Holding the
     * old value in {@code SQUARE_WASSEEN} is how the comparison survives the wipe: after the
     * recalculation, {@link #squareIsSeen(Loc)} answers for now and {@code squareWasSeen} answers
     * for a moment ago, and the two disagreeing is precisely the redraw condition.
     *
     * <p>Function markWasSeen coded before 260827, commented in full on 260827.
     */
    private void markWasSeen() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Loc grid = Loc.row(y).col(x);
                if (squareIsSeen(grid))
                    getSquare(grid).sqInfoOn(SquareEnum.SQUARE_WASSEEN);
                getSquare(grid).sqInfoOff(SquareEnum.SQUARE_VIEW);
                getSquare(grid).sqInfoOff(SquareEnum.SQUARE_SEEN);
                getSquare(grid).sqInfoOff(SquareEnum.SQUARE_CLOSE_PLAYER);
            }
        }
    }

    /**
     * Returns the index of the monster currently taking its turn, C's {@code c->mon_current}.
     * {@code -1} means no monster is acting, so callers ask "did a monster cause this?" with
     * {@code getMonCurrent() > 0}, as {@code player-timed.c} and {@code effects.c} do.
     *
     * <p>Function getMonCurrent coded before 260929, commented in full on 260929.
     *
     * @return the acting monster's index, or {@code -1} when none is
     */
    public int getMonCurrent() {
        return monCurrent;
    }

    /**
     * Stores the level feeling, C's {@code chunk->feeling = ...} assignment in
     * {@code cave_generate()} ({@code generate.c}).
     *
     * <p>The value packs two digits: the object feeling is {@code feeling / 10} and the monster
     * feeling is {@code feeling % 10}, which is how {@code ui-display.c} and {@code cmd-cave.c}
     * unpack it. The setter only records the number; turning it into text is a display concern.
     *
     * <p>Function setFeeling coded before 260929, commented in full on 260929.
     *
     * @param feeling the packed level feeling
     */
    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }
}