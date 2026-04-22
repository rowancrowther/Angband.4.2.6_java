package uk.co.jackoftrades.middle.enums;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

/**
 * TODO: Comment and test this
 */
public enum Effect {
    EFFECT_NONE(false, "", 0, EffectInfo.EFINFO_NONE, "", ""),
    EFFECT_RANDOM(false, "", 0, EffectInfo.EFINFO_NONE, "randomly ", ""),
    EFFECT_DAMAGE(false, "hurt", 1, EffectInfo.EFINFO_DICE, "does %s damage to the player", ""),
    EFFECT_HEAL_HP(false, "heal", 2, EffectInfo.EFINFO_HEAL, "heals %s hitpoints%s", "heal self"),
    EFFECT_MON_HEAL_HP(false, "", 0, EffectInfo.EFINFO_NONE, "heals monster hitpoints", ""),
    EFFECT_MON_HEAL_KIN(false, "", 0, EffectInfo.EFINFO_NONE, "heals fellow monster hitpoints", ""),
    EFFECT_NOURISH(false, "", 3, EffectInfo.EFINFO_FOOD, "%s for %s turns _%s percent),", "%s %s"),
    EFFECT_CRUNCH(false, "", 0, EffectInfo.EFINFO_NONE, "crunches", ""),
    EFFECT_CURE(false, "", 1, EffectInfo.EFINFO_CURE, "cures %s", "cure %s"),
    EFFECT_TIMED_SET(false, "", 2, EffectInfo.EFINFO_TIMED, "administers %s for %s turns", "administer %s"),
    EFFECT_TIMED_INC(false, "dur", 2, EffectInfo.EFINFO_TIMED, "extends %s for %s turns", "extend %s"),
    EFFECT_TIMED_INC_NO_RES(false, "dur", 2, EffectInfo.EFINFO_TIMED, "extends %s for %s turns _unresistable),", "extend %s"),
    EFFECT_MON_TIMED_INC(false, "", 2, EffectInfo.EFINFO_TIMED, "increases monster %s by %s turns", ""),
    EFFECT_TIMED_DEC(false, "", 2, EffectInfo.EFINFO_TIMED, "reduces length of %s by %s turns", "reduce %s"),
    EFFECT_GLYPH(false, "", 1, EffectInfo.EFINFO_NONE, "inscribes a glyph beneath you", "inscribe a glyph"),
    EFFECT_WEB(false, "", 0, EffectInfo.EFINFO_NONE, "creates a web", "create a web"),
    EFFECT_RESTORE_STAT(false, "", 1, EffectInfo.EFINFO_STAT, "restores your %s", "restore %s"),
    EFFECT_DRAIN_STAT(false, "", 1, EffectInfo.EFINFO_STAT, "reduces your %s", ""),
    EFFECT_LOSE_RANDOM_STAT(false, "", 1, EffectInfo.EFINFO_STAT, "reduces a stat other than %s", ""),
    EFFECT_GAIN_STAT(false, "", 1, EffectInfo.EFINFO_STAT, "increases your %s", ""),
    EFFECT_RESTORE_EXP(false, "", 0, EffectInfo.EFINFO_NONE, "restores your experience", "restore experience"),
    EFFECT_GAIN_EXP(false, "", 1, EffectInfo.EFINFO_CONST, "grants %d experience points", ""),
    EFFECT_DRAIN_LIGHT(false, "", 0, EffectInfo.EFINFO_NONE, "drains your light source", ""),
    EFFECT_DRAIN_MANA(false, "", 0, EffectInfo.EFINFO_NONE, "drains some mana", ""),
    EFFECT_RESTORE_MANA(false, "", 0, EffectInfo.EFINFO_NONE, "restores some mana", "restore some mana"),
    EFFECT_REMOVE_CURSE(false, "", 1, EffectInfo.EFINFO_DICE, "attempts power %s removal of a single curse on an object", "remove curse"),
    EFFECT_RECALL(false, "", 0, EffectInfo.EFINFO_NONE, "returns you from the dungeon or takes you to the dungeon after a short delay", "recall"),
    EFFECT_DEEP_DESCENT(false, "", 0, EffectInfo.EFINFO_NONE, "teleports you up to five dungeon levels lower than the lowest point you have reached so far", "descend to the depths"),
    EFFECT_ALTER_REALITY(false, "", 0, EffectInfo.EFINFO_NONE, "creates a new dungeon level", "alter reality"),
    EFFECT_MAP_AREA(false, "", 0, EffectInfo.EFINFO_NONE, "maps the area around you", "map surroundings"),
    EFFECT_READ_MINDS(false, "", 0, EffectInfo.EFINFO_NONE, "maps the area around recently detected monsters", "read minds"),
    EFFECT_DETECT_TRAPS(false, "", 0, EffectInfo.EFINFO_NONE, "detects traps nearby", "detect traps"),
    EFFECT_DETECT_DOORS(false, "", 0, EffectInfo.EFINFO_NONE, "detects doors nearby", "detect doors"),
    EFFECT_DETECT_STAIRS(false, "", 0, EffectInfo.EFINFO_NONE, "detects stairs nearby", "detect stairs"),
    EFFECT_DETECT_ORE(false, "", 0, EffectInfo.EFINFO_NONE, "detects veins nearby", "detect veins"),
    EFFECT_SENSE_GOLD(false, "", 0, EffectInfo.EFINFO_NONE, "senses gold nearby", "sense gold"),
    EFFECT_DETECT_GOLD(false, "", 0, EffectInfo.EFINFO_NONE, "detects gold nearby", "detect gold"),
    EFFECT_SENSE_OBJECTS(false, "", 0, EffectInfo.EFINFO_NONE, "senses objects nearby", "sense objects"),
    EFFECT_DETECT_OBJECTS(false, "", 0, EffectInfo.EFINFO_NONE, "detects objects nearby", "detect objects"),
    EFFECT_DETECT_LIVING_MONSTERS(false, "", 0, EffectInfo.EFINFO_NONE, "detects living creatures nearby", "detect living"),
    EFFECT_DETECT_VISIBLE_MONSTERS(false, "", 0, EffectInfo.EFINFO_NONE, "detects visible creatures nearby", "detect visible"),
    EFFECT_DETECT_INVISIBLE_MONSTERS(false, "", 0, EffectInfo.EFINFO_NONE, "detects invisible creatures nearby", "detect invisible"),
    EFFECT_DETECT_FEARFUL_MONSTERS(false, "", 0, EffectInfo.EFINFO_NONE, "detects creatures nearby which are susceptible to fear", "detect fearful"),
    EFFECT_IDENTIFY(false, "", 0, EffectInfo.EFINFO_NONE, "identifies a single unknown rune on a selected item", "identify"),
    EFFECT_DETECT_EVIL(false, "", 0, EffectInfo.EFINFO_NONE, "detects evil creatures nearby", "detect evil"),
    EFFECT_DETECT_SOUL(false, "", 0, EffectInfo.EFINFO_NONE, "detects creatures with a spirit nearby", "detect souls"),
    EFFECT_CREATE_STAIRS(false, "", 0, EffectInfo.EFINFO_NONE, "creates a staircase beneath your feet", "create stairs"),
    EFFECT_DISENCHANT(false, "", 0, EffectInfo.EFINFO_NONE, "disenchants one of your wielded items", "disenchant item"),
    EFFECT_ENCHANT(false, "", 0, EffectInfo.EFINFO_NONE, "attempts to magically enhance an item", "enchant item"),
    EFFECT_RECHARGE(false, "power", 0, EffectInfo.EFINFO_NONE, "tries to recharge a wand or staff, destroying the wand or staff on failure", "recharge"),
    EFFECT_PROJECT_LOS(false, "power", 1, EffectInfo.EFINFO_SEEN, "%s which are in line of sight", "%s in line of sight"),
    EFFECT_PROJECT_LOS_AWARE(false, "power", 1, EffectInfo.EFINFO_SEEN, "%s which are in line of sight", "%s in line of sight"),
    EFFECT_ACQUIRE(false, "", 0, EffectInfo.EFINFO_NONE, "creates good items nearby", "create good items"),
    EFFECT_WAKE(false, "", 0, EffectInfo.EFINFO_NONE, "awakens all nearby sleeping monsters", "awaken all"),
    EFFECT_SUMMON(false, "", 1, EffectInfo.EFINFO_SUMM, "summons %s at the current dungeon level", "summon %s"),
    EFFECT_BANISH(false, "", 0, EffectInfo.EFINFO_NONE, "removes all of a given creature type from the level", "banish"),
    EFFECT_MASS_BANISH(false, "", 0, EffectInfo.EFINFO_NONE, "removes all nearby creatures", "banish all"),
    EFFECT_PROBE(false, "", 0, EffectInfo.EFINFO_NONE, "gives you information on the health and abilities of monsters you can see", "probe"),
    EFFECT_TELEPORT(false, "range", 2, EffectInfo.EFINFO_TELE, "teleports %s randomly %s", "teleport %s %s"),
    EFFECT_TELEPORT_TO(false, "", 0, EffectInfo.EFINFO_NONE, "teleports toward a target", "teleport to target"),
    EFFECT_TELEPORT_LEVEL(false, "", 0, EffectInfo.EFINFO_NONE, "teleports you one level up or down", "teleport level"),
    EFFECT_RUBBLE(false, "", 0, EffectInfo.EFINFO_NONE, "causes rubble to fall around you", ""),
    EFFECT_GRANITE(false, "", 0, EffectInfo.EFINFO_NONE, "causes a granite wall to fall behind you", ""),
    EFFECT_DESTRUCTION(false, "", 1, EffectInfo.EFINFO_QUAKE, "destroys an area around you in the shape of a circle radius %d, and blinds you for 1d10+10 turns", "destroy area"),
    EFFECT_EARTHQUAKE(false, "", 1, EffectInfo.EFINFO_QUAKE, "causes an earthquake around you of radius %d", "cause earthquake"),
    EFFECT_LIGHT_LEVEL(false, "", 0, EffectInfo.EFINFO_NONE, "completely lights up and magically maps the level", "light level"),
    EFFECT_DARKEN_LEVEL(false, "", 0, EffectInfo.EFINFO_NONE, "completely darkens up and magically maps the level", "darken level"),
    EFFECT_LIGHT_AREA(false, "", 0, EffectInfo.EFINFO_NONE, "lights up the surrounding area", "light area"),
    EFFECT_DARKEN_AREA(false, "", 0, EffectInfo.EFINFO_NONE, "darkens the surrounding area", "darken area"),
    EFFECT_SPHERE(false, "dam", 4, EffectInfo.EFINFO_SPOT, "creates a ball of %s with radius %d, centred on the player, with full intensity to radius %d, dealing %s damage at the centre", "project %s"),
    EFFECT_BALL(true, "dam", 3, EffectInfo.EFINFO_BALL, "fires a ball of %s with radius %d, dealing %s damage at the centre", "fire a ball of %s"),
    EFFECT_BREATH(true, "", 3, EffectInfo.EFINFO_BREATH, "breathes a cone of %s with width %d degrees, dealing %s damage at the source", "breathe a cone of %s"),
    EFFECT_ARC(true, "dam", 3, EffectInfo.EFINFO_BREATH, "produces a cone of %s with width %d degrees, dealing %s damage at the source", "produce a cone of %s"),
    EFFECT_SHORT_BEAM(true, "dam", 3, EffectInfo.EFINFO_SHORT, "produces a beam of %s with length %d, dealing %s damage", "produce a beam of %s"),
    EFFECT_LASH(true, "", 2, EffectInfo.EFINFO_LASH, "fires a beam of %s length %d, dealing damage determined by blows", "lash with %s"),
    EFFECT_SWARM(true, "dam", 3, EffectInfo.EFINFO_BALL, "fires a series of %s balls of radius %d, dealing %s damage at the centre of each", "fire a swarm of %s balls"),
    EFFECT_STRIKE(true, "dam", 3, EffectInfo.EFINFO_BALL, "creates a ball of %s with radius %d, dealing %s damage at the centre", "strike with %s"),
    EFFECT_STAR(false, "dam", 2, EffectInfo.EFINFO_BOLTD, "fires a line of %s in all directions, each dealing %s damage", "fire a line of %s in all directions"),
    EFFECT_STAR_BALL(false, "dam", 3, EffectInfo.EFINFO_BALL, "fires balls of %s with radius %d in all directions, dealing %s damage at the centre of each", "fire balls of %s in all directions"),
    EFFECT_BOLT(true, "dam", 2, EffectInfo.EFINFO_BOLTD, "casts a bolt of %s dealing %s damage", "cast a bolt of %s"),
    EFFECT_BEAM(true, "dam", 2, EffectInfo.EFINFO_BOLTD, "casts a beam of %s dealing %s damage", "cast a beam of %s"),
    EFFECT_BOLT_OR_BEAM(true, "dam", 2, EffectInfo.EFINFO_BOLTD, "casts a bolt or beam of %s dealing %s damage", "cast a bolt or beam of %s"),
    EFFECT_LINE(true, "dam", 2, EffectInfo.EFINFO_BOLTD, "creates a line of %s dealing %s damage", "create a line of %s"),
    EFFECT_ALTER(true, "", 1, EffectInfo.EFINFO_BOLT, "creates a line which %s", "create a line which %s"),
    EFFECT_BOLT_STATUS(true, "", 1, EffectInfo.EFINFO_BOLT, "casts a bolt which %s", "cast a bolt which %s"),
    EFFECT_BOLT_STATUS_DAM(true, "dam", 2, EffectInfo.EFINFO_BOLTD, "casts a bolt which %s, dealing %s damage", "cast a bolt which %s"),
    EFFECT_BOLT_AWARE(true, "", 1, EffectInfo.EFINFO_BOLT, "creates a bolt which %s", "create a bolt which %s"),
    EFFECT_TOUCH(false, "", 1, EffectInfo.EFINFO_TOUCH, "%s on all adjacent squares", "%s all adjacent"),
    EFFECT_TOUCH_AWARE(false, "", 1, EffectInfo.EFINFO_TOUCH, "%s on all adjacent squares", "%s all adjacent"),
    EFFECT_CURSE_ARMOR(false, "", 0, EffectInfo.EFINFO_NONE, "curses your worn armor", "curse armor"),
    EFFECT_CURSE_WEAPON(false, "", 0, EffectInfo.EFINFO_NONE, "curses your wielded melee weapon", "curse weapon"),
    EFFECT_BRAND_WEAPON(false, "", 0, EffectInfo.EFINFO_NONE, "brands your wielded melee weapon", "brand weapon"),
    EFFECT_BRAND_AMMO(false, "", 0, EffectInfo.EFINFO_NONE, "brands a stack of ammunition", "brand ammunition"),
    EFFECT_BRAND_BOLTS(false, "", 0, EffectInfo.EFINFO_NONE, "brands bolts with fire, in an unbalanced fashion", "brand bolts"),
    EFFECT_CREATE_ARROWS(false, "", 0, EffectInfo.EFINFO_NONE, "uses a staff to create a stack of arrows", "use staff for arrows"),
    EFFECT_TAP_DEVICE(false, "", 0, EffectInfo.EFINFO_NONE, "drains magical energy from a staff or wand", "tap device"),
    EFFECT_TAP_UNLIFE(false, "dam", 1, EffectInfo.EFINFO_DICE, "drains %s mana from the closest undead monster, damaging it", "tap unlife"),
    EFFECT_SHAPECHANGE(false, "", 0, EffectInfo.EFINFO_NONE, "changes the player's shape", "change shape"),
    EFFECT_CURSE(true, "dam", 0, EffectInfo.EFINFO_NONE, "damages a monster directly", "curse"),
    EFFECT_COMMAND(true, "", 0, EffectInfo.EFINFO_NONE, "takes control of a monster", "command"),
    EFFECT_JUMP_AND_BITE(false, "", 0, EffectInfo.EFINFO_NONE, "jumps the player to the closest living monster and bites it", "jump and bite"),
    EFFECT_MOVE_ATTACK(true, "blows", 1, EffectInfo.EFINFO_DICE, "moves the player up to 4 spaces and executes up to %d melee blows", "move and attack"),
    EFFECT_SINGLE_COMBAT(true, "", 0, EffectInfo.EFINFO_NONE, "engages a monster in single combat", "engage in single combat"),
    EFFECT_MELEE_BLOWS(true, "blows", 1, EffectInfo.EFINFO_DICE, "strikes %d blows against an adjacent monster", "pummel"),
    EFFECT_SWEEP(false, "blows", 1, EffectInfo.EFINFO_DICE, "strikes %d blows against all adjacent monsters", "sweep"),
    EFFECT_BIZARRE(true, "", 0, EffectInfo.EFINFO_NONE, "does bizarre things", "do bizarre things"),
    EFFECT_WONDER(true, "", 0, EffectInfo.EFINFO_NONE, "creates random and unpredictable effects", "create random effects"),
    EFFECT_SELECT(false, "", 0, EffectInfo.EFINFO_NONE, "selects one of ", ""),
    EFFECT_SET_VALUE(false, "", 0, EffectInfo.EFINFO_NONE, "", ""),
    EFFECT_CLEAR_VALUE(false, "", 0, EffectInfo.EFINFO_NONE, "", ""),
    EFFECT_SCRAMBLE_STATS(false, "", 0, EffectInfo.EFINFO_NONE, "", ""),
    EFFECT_UNSCRAMBLE_STATS(false, "", 0, EffectInfo.EFINFO_NONE, "", ""),
    EFFECT_MAX(false, "", 0, EffectInfo.EFINFO_NONE, "", "");

    private final boolean requiresAiming;
    private final String infoLabel;
    private final int numberOfArguments; // May need to change this
    private final EffectInfo effectInfo;
    private final String description;
    private final String menuFormat;

    @Contract(pure = true)
    Effect(boolean aim, String infoLabel, int arguments, EffectInfo @NonNull effectInfo, String text, String menuText) {
        requiresAiming = aim;
        this.infoLabel = infoLabel;
        numberOfArguments = arguments;
        this.effectInfo = effectInfo;
        description = text;
        menuFormat = menuText;
    }

    @Contract(pure = true)
    public boolean getAim() {
        return requiresAiming;
    }

    @Contract(pure = true)
    public String getInfoLabel() {
        return infoLabel;
    }

    @Contract(pure = true)
    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    @Contract(pure = true)
    public EffectInfo getEffectInfo() {
        return effectInfo;
    }

    @Contract(pure = true)
    public String getDescription() {
        return description;
    }

    @Contract(pure = true)
    public String getMenuFormat() {
        return menuFormat;
    }
}