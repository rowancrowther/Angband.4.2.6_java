package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.middle.enums.StatElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

public class PlayerProperty {
    private PlayerPropertyType playerPropertyType;
    private PlayerFlag pCode;
    private ObjectFlag oCode;
    private UIEntry uiEntry;
    private StatElementEnum statElement;
    private boolean passType;
    private int passValue;
    private boolean special;
    private String playerName;
    private String description;
    private PlayerPropertyValue value;

    public PlayerProperty(PlayerPropertyType playerPropertyType,
                          PlayerFlag pCode,
                          ObjectFlag oCode,
                          UIEntry uiEntry,
                          StatElementEnum statElement,
                          boolean passType,
                          PlayerPropertyValue passValue,
                          boolean special,
                          String playerName,
                          String description,
                          PlayerPropertyValue value) {
        this.playerPropertyType = playerPropertyType;
        this.oCode = oCode;
        this.pCode = pCode;
        this.uiEntry = uiEntry;
        this.playerName = playerName;
        this.description = description;
        this.value = value;
    }

    @Override
    public String toString() {
        return "PlayerProperty{" +
                "playerPropertyType=" + playerPropertyType +
                ", pCode=" + pCode +
                ", oCode=" + oCode +
                ", uiEntry=" + uiEntry +
                ", statElement=" + statElement +
                ", passType=" + passType +
                ", passValue=" + passValue +
                ", special=" + special +
                ", playerName='" + playerName + '\'' +
                ", description='" + description + '\'' +
                ", value=" + value +
                '}';
    }

    public enum PlayerPropertyType {
        PROP_TYPE_PLAYER,
        PROP_TYPE_OBJECT,
        PROP_TYPE_ELEMENT
    }

    public enum PlayerPropertyValue {
        NONE,
        VULNERABILITY,
        RESISTANCE,
        IMMUNITY
    }
}