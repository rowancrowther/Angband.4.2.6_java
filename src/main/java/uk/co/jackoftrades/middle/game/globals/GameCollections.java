package uk.co.jackoftrades.middle.game.globals;

import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.combat.CriticalLevel;
import uk.co.jackoftrades.middle.combat.O_CriticalLevel;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.Trap;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlagName;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import java.util.ArrayList;
import java.util.HashMap;

public class GameCollections {
    private static final HashMap<EquipmentSlotsEnum, Object> slots = new HashMap<>();
    private static final HashMap<ObjectFlagName, Object> flags = new HashMap<>();
    private static final HashMap<ObjectModifier, Object> modifiers = new HashMap<>();
    private static final HashMap<EffectEnum, Object> effects = new HashMap<>();
    private static final HashMap<Trap, Object> traps = new HashMap<>();
    private static final HashMap<TerrainFeatureFlags, Object> terrainFlags = new HashMap<>();
    private static final HashMap<MonsterRaceFlag, Object> monsterRaceFlags = new HashMap<>();
    private static final HashMap<PlayerFlag, Object> playerInfoFlags = new HashMap<>();

    // non-O melee and ranged criticals
    private static final ArrayList<CriticalLevel> mCriticalLevels = new ArrayList<>();
    private static final ArrayList<CriticalLevel> rCriticalLevels = new ArrayList<>();

    // O melee and ranged criticals
    private static final ArrayList<O_CriticalLevel> mOCriticalLevels = new ArrayList<>();
    private static final ArrayList<O_CriticalLevel> rOCriticalLevels = new ArrayList<>();

    private GameCollections() {
    }

    public static void init() {

    }

    static void addMCriticalLevel(CriticalLevel criticalLevel) {
        mCriticalLevels.add(criticalLevel);
    }

    static void addRCriticalLevel(CriticalLevel criticalLevel) {
        rCriticalLevels.add(criticalLevel);
    }

    static void addMOCriticalLevel(O_CriticalLevel oCritLevel) {
        mOCriticalLevels.add(oCritLevel);
    }

    static void addROCritLevel(O_CriticalLevel oCritLevel) {
        rOCriticalLevels.add(oCritLevel);
    }
}