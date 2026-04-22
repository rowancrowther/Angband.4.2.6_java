package uk.co.jackoftrades.middle.enums;

import org.jetbrains.annotations.Contract;

/**
 * The trap list
 */
public enum Trap {
    /**
     * No trap appears here
     */
    TRF_NONE(""),

    /**
     * This square/object is trapped by a glyph
     */
    TRF_GLYPH("Is a glyph"),

    /**
     * The player has set a trap here
     */
    TRF_TRAP("Is a player trap"),

    /**
     * This trap is visible
     */
    TRF_VISIBLE("Is visible"),

    /**
     * This trap is invisible - this flag is never used
     */
    TRF_INVISIBLE("Is invisible"), // UNUSED

    /**
     * This trap can be set on a floor
     */
    TRF_FLOOR("Can be set on a floor"),

    /**
     * This trap teleports the player down a level
     */
    TRF_DOWN("Takes the player down a level"),

    /**
     * This trap moves the player
     */
    TRF_PIT("Moves the player onto the trap"),

    /**
     * This trap disappears after being activated
     */
    TRF_ONETIME("Disappears after being activated"),

    /**
     * This trap is magical, if this trap flag is not set then the trap is physical
     */
    TRF_MAGICAL("Has magical activation _absence of this flag means physical),"),

    /**
     * The player can make a saving throw to avoid all effects
     */
    TRF_SAVE_THROW("Allows a save from all effects by standard saving throw"),

    /**
     * The player can avoid effects based on their AC
     */
    TRF_SAVE_ARMOUR("Allows a save from all effects due to AC"),

    /**
     * This trap is set on a lock on a door
     */
    TRF_LOCK("Is a door lock"),

    /**
     * This trap's effect occurs a certain time after it is tripped
     */
    TRF_DELAY("Has a delayed effect"),

    /**
     * This trap is a web
     */
    TRF_WEB("Is a web");

    private final String description;

    @Contract(pure = true)
    Trap(String description) {
        this.description = description;
    }

    /**
     * Gets the description string for this trap
     *
     * @return the description string for this trap
     */
    @Contract(pure = true)
    public String getDescription() {
        return description;
    }
}
