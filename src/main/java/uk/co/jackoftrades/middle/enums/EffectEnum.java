package uk.co.jackoftrades.middle.enums;

import org.jetbrains.annotations.Contract;

/**
 * TODO: Comment and test this
 */

public enum EffectEnum {
    EF_NONE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_RANDOM(false, "", 0, EffectInfoEnum.EFINFO_NONE, "randomly ", ""),
    EF_DAMAGE(false, "hurt", 1, EffectInfoEnum.EFINFO_DICE, "does %s damage to the player", ""),
    EF_HEAL_HP(false, "heal", 2, EffectInfoEnum.EFINFO_HEAL, "heals %s hitpoints%s", "heal self"),
    EF_MON_HEAL_HP(false, "", 0, EffectInfoEnum.EFINFO_NONE, "heals monster hitpoints", ""),
    EF_MON_HEAL_KIN(false, "", 0, EffectInfoEnum.EFINFO_NONE, "heals fellow monster hitpoints", ""),
    EF_NOURISH(false, "", 3, EffectInfoEnum.EFINFO_FOOD, "%s for %s turns _%s percent),", "%s %s"),
    EF_CRUNCH(false, "", 0, EffectInfoEnum.EFINFO_NONE, "crunches", ""),
    EF_CURE(false, "", 1, EffectInfoEnum.EFINFO_CURE, "cures %s", "cure %s"),
    EF_TIMED_SET(false, "", 2, EffectInfoEnum.EFINFO_TIMED, "administers %s for %s turns", "administer %s"),
    EF_TIMED_INC(false, "dur", 2, EffectInfoEnum.EFINFO_TIMED, "extends %s for %s turns", "extend %s"),
    EF_TIMED_INC_NO_RES(false, "dur", 2, EffectInfoEnum.EFINFO_TIMED, "extends %s for %s turns _unresistable),", "extend %s"),
    EF_MON_TIMED_INC(false, "", 2, EffectInfoEnum.EFINFO_TIMED, "increases monster %s by %s turns", ""),
    EF_TIMED_DEC(false, "", 2, EffectInfoEnum.EFINFO_TIMED, "reduces length of %s by %s turns", "reduce %s"),
    EF_GLYPH(false, "", 1, EffectInfoEnum.EFINFO_NONE, "inscribes a glyph beneath you", "inscribe a glyph"),
    EF_WEB(false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates a web", "create a web"),
    EF_RESTORE_STAT(false, "", 1, EffectInfoEnum.EFINFO_STAT, "restores your %s", "restore %s"),
    EF_DRAIN_STAT(false, "", 1, EffectInfoEnum.EFINFO_STAT, "reduces your %s", ""),
    EF_LOSE_RANDOM_STAT(false, "", 1, EffectInfoEnum.EFINFO_STAT, "reduces a stat other than %s", ""),
    EF_GAIN_STAT(false, "", 1, EffectInfoEnum.EFINFO_STAT, "increases your %s", ""),
    EF_RESTORE_EXP(false, "", 0, EffectInfoEnum.EFINFO_NONE, "restores your experience", "restore experience"),
    EF_GAIN_EXP(false, "", 1, EffectInfoEnum.EFINFO_CONST, "grants %d experience points", ""),
    EF_DRAIN_LIGHT(false, "", 0, EffectInfoEnum.EFINFO_NONE, "drains your light source", ""),
    EF_DRAIN_MANA(false, "", 0, EffectInfoEnum.EFINFO_NONE, "drains some mana", ""),
    EF_RESTORE_MANA(false, "", 0, EffectInfoEnum.EFINFO_NONE, "restores some mana", "restore some mana"),
    EF_REMOVE_CURSE(false, "", 1, EffectInfoEnum.EFINFO_DICE, "attempts power %s removal of a single curse on an object", "remove curse"),
    EF_RECALL(false, "", 0, EffectInfoEnum.EFINFO_NONE, "returns you from the dungeon or takes you to the dungeon after a short delay", "recall"),
    EF_DEEP_DESCENT(false, "", 0, EffectInfoEnum.EFINFO_NONE, "teleports you up to five dungeon levels lower than the lowest point you have reached so far", "descend to the depths"),
    EF_ALTER_REALITY(false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates a new dungeon level", "alter reality"),
    EF_MAP_AREA(false, "", 0, EffectInfoEnum.EFINFO_NONE, "maps the area around you", "map surroundings"),
    EF_READ_MINDS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "maps the area around recently detected monsters", "read minds"),
    EF_DETECT_TRAPS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects traps nearby", "detect traps"),
    EF_DETECT_DOORS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects doors nearby", "detect doors"),
    EF_DETECT_STAIRS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects stairs nearby", "detect stairs"),
    EF_DETECT_ORE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects veins nearby", "detect veins"),
    EF_SENSE_GOLD(false, "", 0, EffectInfoEnum.EFINFO_NONE, "senses gold nearby", "sense gold"),
    EF_DETECT_GOLD(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects gold nearby", "detect gold"),
    EF_SENSE_OBJECTS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "senses objects nearby", "sense objects"),
    EF_DETECT_OBJECTS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects objects nearby", "detect objects"),
    EF_DETECT_LIVING_MONSTERS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects living creatures nearby", "detect living"),
    EF_DETECT_VISIBLE_MONSTERS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects visible creatures nearby", "detect visible"),
    EF_DETECT_INVISIBLE_MONSTERS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects invisible creatures nearby", "detect invisible"),
    EF_DETECT_FEARFUL_MONSTERS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects creatures nearby which are susceptible to fear", "detect fearful"),
    EF_IDENTIFY(false, "", 0, EffectInfoEnum.EFINFO_NONE, "identifies a single unknown rune on a selected item", "identify"),
    EF_DETECT_EVIL(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects evil creatures nearby", "detect evil"),
    EF_DETECT_SOUL(false, "", 0, EffectInfoEnum.EFINFO_NONE, "detects creatures with a spirit nearby", "detect souls"),
    EF_CREATE_STAIRS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates a staircase beneath your feet", "create stairs"),
    EF_DISENCHANT(false, "", 0, EffectInfoEnum.EFINFO_NONE, "disenchants one of your wielded items", "disenchant item"),
    EF_ENCHANT(false, "", 0, EffectInfoEnum.EFINFO_NONE, "attempts to magically enhance an item", "enchant item"),
    EF_RECHARGE(false, "power", 0, EffectInfoEnum.EFINFO_NONE, "tries to recharge a wand or staff, destroying the wand or staff on failure", "recharge"),
    EF_PROJECT_LOS(false, "power", 1, EffectInfoEnum.EFINFO_SEEN, "%s which are in line of sight", "%s in line of sight"),
    EF_PROJECT_LOS_AWARE(false, "power", 1, EffectInfoEnum.EFINFO_SEEN, "%s which are in line of sight", "%s in line of sight"),
    EF_ACQUIRE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "creates good items nearby", "create good items"),
    EF_WAKE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "awakens all nearby sleeping monsters", "awaken all"),
    EF_SUMMON(false, "", 1, EffectInfoEnum.EFINFO_SUMM, "summons %s at the current dungeon level", "summon %s"),
    EF_BANISH(false, "", 0, EffectInfoEnum.EFINFO_NONE, "removes all of a given creature type from the level", "banish"),
    EF_MASS_BANISH(false, "", 0, EffectInfoEnum.EFINFO_NONE, "removes all nearby creatures", "banish all"),
    EF_PROBE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "gives you information on the health and abilities of monsters you can see", "probe"),
    EF_TELEPORT(false, "range", 2, EffectInfoEnum.EFINFO_TELE, "teleports %s randomly %s", "teleport %s %s"),
    EF_TELEPORT_TO(false, "", 0, EffectInfoEnum.EFINFO_NONE, "teleports toward a target", "teleport to target"),
    EF_TELEPORT_LEVEL(false, "", 0, EffectInfoEnum.EFINFO_NONE, "teleports you one level up or down", "teleport level"),
    EF_RUBBLE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "causes rubble to fall around you", ""),
    EF_GRANITE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "causes a granite wall to fall behind you", ""),
    EF_DESTRUCTION(false, "", 1, EffectInfoEnum.EFINFO_QUAKE, "destroys an area around you in the shape of a circle radius %d, and blinds you for 1d10+10 turns", "destroy area"),
    EF_EARTHQUAKE(false, "", 1, EffectInfoEnum.EFINFO_QUAKE, "causes an earthquake around you of radius %d", "cause earthquake"),
    EF_LIGHT_LEVEL(false, "", 0, EffectInfoEnum.EFINFO_NONE, "completely lights up and magically maps the level", "light level"),
    EF_DARKEN_LEVEL(false, "", 0, EffectInfoEnum.EFINFO_NONE, "completely darkens up and magically maps the level", "darken level"),
    EF_LIGHT_AREA(false, "", 0, EffectInfoEnum.EFINFO_NONE, "lights up the surrounding area", "light area"),
    EF_DARKEN_AREA(false, "", 0, EffectInfoEnum.EFINFO_NONE, "darkens the surrounding area", "darken area"),
    EF_SPHERE(false, "dam", 4, EffectInfoEnum.EFINFO_SPOT, "creates a ball of %s with radius %d, centred on the player, with full intensity to radius %d, dealing %s damage at the centre", "project %s"),
    EF_BALL(true, "dam", 3, EffectInfoEnum.EFINFO_BALL, "fires a ball of %s with radius %d, dealing %s damage at the centre", "fire a ball of %s"),
    EF_BREATH(true, "", 3, EffectInfoEnum.EFINFO_BREATH, "breathes a cone of %s with width %d degrees, dealing %s damage at the source", "breathe a cone of %s"),
    EF_ARC(true, "dam", 3, EffectInfoEnum.EFINFO_BREATH, "produces a cone of %s with width %d degrees, dealing %s damage at the source", "produce a cone of %s"),
    EF_SHORT_BEAM(true, "dam", 3, EffectInfoEnum.EFINFO_SHORT, "produces a beam of %s with length %d, dealing %s damage", "produce a beam of %s"),
    EF_LASH(true, "", 2, EffectInfoEnum.EFINFO_LASH, "fires a beam of %s length %d, dealing damage determined by blows", "lash with %s"),
    EF_SWARM(true, "dam", 3, EffectInfoEnum.EFINFO_BALL, "fires a series of %s balls of radius %d, dealing %s damage at the centre of each", "fire a swarm of %s balls"),
    EF_STRIKE(true, "dam", 3, EffectInfoEnum.EFINFO_BALL, "creates a ball of %s with radius %d, dealing %s damage at the centre", "strike with %s"),
    EF_STAR(false, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "fires a line of %s in all directions, each dealing %s damage", "fire a line of %s in all directions"),
    EF_STAR_BALL(false, "dam", 3, EffectInfoEnum.EFINFO_BALL, "fires balls of %s with radius %d in all directions, dealing %s damage at the centre of each", "fire balls of %s in all directions"),
    EF_BOLT(true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a bolt of %s dealing %s damage", "cast a bolt of %s"),
    EF_BEAM(true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a beam of %s dealing %s damage", "cast a beam of %s"),
    EF_BOLT_OR_BEAM(true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a bolt or beam of %s dealing %s damage", "cast a bolt or beam of %s"),
    EF_LINE(true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "creates a line of %s dealing %s damage", "create a line of %s"),
    EF_ALTER(true, "", 1, EffectInfoEnum.EFINFO_BOLT, "creates a line which %s", "create a line which %s"),
    EF_BOLT_STATUS(true, "", 1, EffectInfoEnum.EFINFO_BOLT, "casts a bolt which %s", "cast a bolt which %s"),
    EF_BOLT_STATUS_DAM(true, "dam", 2, EffectInfoEnum.EFINFO_BOLTD, "casts a bolt which %s, dealing %s damage", "cast a bolt which %s"),
    EF_BOLT_AWARE(true, "", 1, EffectInfoEnum.EFINFO_BOLT, "creates a bolt which %s", "create a bolt which %s"),
    EF_TOUCH(false, "", 1, EffectInfoEnum.EFINFO_TOUCH, "%s on all adjacent squares", "%s all adjacent"),
    EF_TOUCH_AWARE(false, "", 1, EffectInfoEnum.EFINFO_TOUCH, "%s on all adjacent squares", "%s all adjacent"),
    EF_CURSE_ARMOR(false, "", 0, EffectInfoEnum.EFINFO_NONE, "curses your worn armor", "curse armor"),
    EF_CURSE_WEAPON(false, "", 0, EffectInfoEnum.EFINFO_NONE, "curses your wielded melee weapon", "curse weapon"),
    EF_BRAND_WEAPON(false, "", 0, EffectInfoEnum.EFINFO_NONE, "brands your wielded melee weapon", "brand weapon"),
    EF_BRAND_AMMO(false, "", 0, EffectInfoEnum.EFINFO_NONE, "brands a stack of ammunition", "brand ammunition"),
    EF_BRAND_BOLTS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "brands bolts with fire, in an unbalanced fashion", "brand bolts"),
    EF_CREATE_ARROWS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "uses a staff to create a stack of arrows", "use staff for arrows"),
    EF_TAP_DEVICE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "drains magical energy from a staff or wand", "tap device"),
    EF_TAP_UNLIFE(false, "dam", 1, EffectInfoEnum.EFINFO_DICE, "drains %s mana from the closest undead monster, damaging it", "tap unlife"),
    EF_SHAPECHANGE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "changes the player's shape", "change shape"),
    EF_CURSE(true, "dam", 0, EffectInfoEnum.EFINFO_NONE, "damages a monster directly", "curse"),
    EF_COMMAND(true, "", 0, EffectInfoEnum.EFINFO_NONE, "takes control of a monster", "command"),
    EF_JUMP_AND_BITE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "jumps the player to the closest living monster and bites it", "jump and bite"),
    EF_MOVE_ATTACK(true, "blows", 1, EffectInfoEnum.EFINFO_DICE, "moves the player up to 4 spaces and executes up to %d melee blows", "move and attack"),
    EF_SINGLE_COMBAT(true, "", 0, EffectInfoEnum.EFINFO_NONE, "engages a monster in single combat", "engage in single combat"),
    EF_MELEE_BLOWS(true, "blows", 1, EffectInfoEnum.EFINFO_DICE, "strikes %d blows against an adjacent monster", "pummel"),
    EF_SWEEP(false, "blows", 1, EffectInfoEnum.EFINFO_DICE, "strikes %d blows against all adjacent monsters", "sweep"),
    EF_BIZARRE(true, "", 0, EffectInfoEnum.EFINFO_NONE, "does bizarre things", "do bizarre things"),
    EF_WONDER(true, "", 0, EffectInfoEnum.EFINFO_NONE, "creates random and unpredictable effects", "create random effects"),
    EF_SELECT(false, "", 0, EffectInfoEnum.EFINFO_NONE, "selects one of ", ""),
    EF_SET_VALUE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_CLEAR_VALUE(false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_SCRAMBLE_STATS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_UNSCRAMBLE_STATS(false, "", 0, EffectInfoEnum.EFINFO_NONE, "", ""),
    EF_MAX(false, "", 0, EffectInfoEnum.EFINFO_NONE, "", "");

    private final boolean requiresAiming;
    private final String infoLabel;
    private final int numberOfArguments; // May need to change this
    private final EffectInfoEnum effectInfoEnum;
    private final String description;
    private final String menuFormat;

    @Contract(pure = true)
    EffectEnum(boolean aim,
               String infoLabel,
               int arguments,
               EffectInfoEnum effectInfoEnum,
               String text,
               String menuText) {
        requiresAiming = aim;
        this.infoLabel = infoLabel;
        numberOfArguments = arguments;
        this.effectInfoEnum = effectInfoEnum;
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
    public EffectInfoEnum getEffectInfo() {
        return effectInfoEnum;
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