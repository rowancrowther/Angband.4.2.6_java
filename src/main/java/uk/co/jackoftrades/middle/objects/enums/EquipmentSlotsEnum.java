package uk.co.jackoftrades.middle.objects.enums;

public enum EquipmentSlotsEnum {
    EQUIP_NONE(false, false, "", "", ""),
    EQUIP_WEAPON(false, false, "Wielding", "just lifting", "attacking monsters with"),
    EQUIP_BOW(false, false, "Shooting", "just holding", "shooting missiles with"),
    EQUIP_RING(false, true, "On %s", "", "wearing on your %s"),
    EQUIP_AMULET(false, true, "Around %s", "", "wearing around your %s"),
    EQUIP_LIGHT(false, false, "Light source", "", "using to light your way"),
    EQUIP_BODY_ARMOR(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_CLOAK(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_SHIELD(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_HAT(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_GLOVES(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_BOOTS(true, true, "On %s", "", "wearing on your %s");

    private final boolean acidResistant;
    private final boolean mentionName;
    private final String mentionString;
    private final String heavyDescribe;
    private final String describe;

    EquipmentSlotsEnum(boolean acidResistant, boolean mentionName, String mentionString, String heavyDescribe, String describe) {
        this.acidResistant = acidResistant;
        this.mentionName = mentionName;
        this.mentionString = mentionString;
        this.heavyDescribe = heavyDescribe;
        this.describe = describe;
    }
}