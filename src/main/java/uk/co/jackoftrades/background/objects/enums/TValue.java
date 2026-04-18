package uk.co.jackoftrades.background.objects.enums;

public enum TValue {
    /**
     * A null TValue - not yet assigned
     */
    TV_NULL("none"),

    /**
     * A chest
     */
    TV_CHEST("chest"),

    /**
     * a pebble or iron shot, normal or otherwise, used in a sling
     */
    TV_SHOT("shot"),

    /**
     * A normal or magic arrow, used in a bow
     */
    TV_ARROW("arrow"),

    /**
     * A crossbow bolt, magic or otherwise
     */
    TV_BOLT("bolt"),

    /**
     * A bow
     */
    TV_BOW("bow"),

    /**
     * A type of digging implement, such as shovel or pick
     */
    TV_DIGGING("digger"),

    /**
     * A weapon with a haft such as a halberd
     */
    TV_HAFTED("hafted"),

    /**
     * A polearm
     */
    TV_POLEARM("polearm"),

    /**
     * A sword
     */
    TV_SWORD("sword"),

    /**
     * Boots or sandals
     */
    TV_BOOTS("boots"),

    /**
     * Gloves
     */
    TV_GLOVES("gloves"),

    /**
     * Helm, or hat
     */
    TV_HELM("helm"),

    /**
     * A crown
     */
    TV_CROWN("crown"),

    /**
     * A shield
     */
    TV_SHIELD("shield"),

    /**
     * A cloak, worn on the back
     */
    TV_CLOAK("cloak"),

    /**
     * Soft armour, such as cloth or leather
     */
    TV_SOFT_ARMOUR("soft armour"),

    /**
     * Hard armour, such as chain or plate
     */
    TV_HARD_ARMOUR("hard armour"),

    /**
     * Armour fashioned from dragon scale
     */
    TV_DRAG_ARMOUR("dragon armour"),

    /**
     * A form of light, such as torches or an oil lamp
     */
    TV_LIGHT("light"),

    /**
     * An amulet to wear around your neck
     */
    TV_AMULET("amulet"),

    /**
     * A ring to wear around your finger
     */
    TV_RING("ring"),

    /**
     * A staff, knob available or not
     */
    TV_STAFF("staff"),

    /**
     * A wand to zap
     */
    TV_WAND("wand"),

    /**
     * A rod, different from a wand, honestly
     */
    TV_ROD("rod"),

    /**
     * A scroll to read
     */
    TV_SCROLL("scroll"),

    /**
     * A potion to quaff
     */
    TV_POTION("potion"),

    /**
     * A flask to drink from
     */
    TV_FLASK("flask"),

    /**
     * Food to eat
     */
    TV_FOOD("food"),

    /**
     * Magic mushrooms - no, not that kind
     */
    TV_MUSHROOM("mushroom"),

    /**
     * A spellbook for mages
     */
    TV_MAGIC_BOOK("magic book"),

    /**
     * A spellbook for priests
     */
    TV_PRAYER_BOOK("prayer book"),

    /**
     * A spellbook for druids
     */
    TV_NATURE_BOOK("nature book"),

    /**
     * A spell book of the dark arts
     */
    TV_SHADOW_BOOK("shadow book"),

    /**
     * Another type of spell book
     */
    TV_OTHER_BOOK("other book"),

    /**
     * Gold, gold, gold, gold, Gold, gold, gold, gold (repeat ad nauseam)
     */
    TV_GOLD("gold");

    private final String name;

    TValue(String name) {
        this.name = name;
    }

    /**
     * Get the string name of this TValue
     *
     * @return The type name of this TValue
     */
    public String getName() {
        return name;
    }
}