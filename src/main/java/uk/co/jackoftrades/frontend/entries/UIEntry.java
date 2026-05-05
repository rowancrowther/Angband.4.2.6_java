package uk.co.jackoftrades.frontend.entries;

import uk.co.jackoftrades.frontend.entries.enums.EntryFlag;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
import uk.co.jackoftrades.middle.enums.StatElementEnum;

import java.util.ArrayList;

public class UIEntry {
    private String name;
    private StatElemType parameter;
    private StatElementEnum statOrElement;
    private UIEntryRenderer renderer;
    private CombinerName combineType;
    private ArrayList<String> category;
    private int priorityNum;
    private String priorityStr;
    private EntryFlag entryFlag;
    private String description;
    private String label;
    private String label2;
    private String label5;
    private ArrayList<String> categories;
    private String template;

    public UIEntry(String name,
                   StatElementEnum statOrElement,
                   StatElemType parameter,
                   UIEntryRenderer renderer,
                   CombinerName combineType,
                   ArrayList<String> categories,
                   int priorityNum,
                   String priorityStr,
                   EntryFlag entryFlag,
                   String description,
                   String label,
                   String label2,
                   String label5,
                   String template) {
        this.name = name;
        this.statOrElement = statOrElement;
        this.parameter = parameter;
        this.renderer = renderer;
        this.combineType = combineType;
        this.priorityNum = priorityNum;
        this.priorityStr = priorityStr;
        this.entryFlag = entryFlag;
        this.description = description;
        this.label = label;
        this.label2 = label2;
        this.label5 = label5;
        this.categories = categories;
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public enum StatElemType {
        STAT,
        ELEMENT
    }

    @Override
    public String toString() {
        return "UIEntry{" +
                "name='" + name + '\'' +
                ", parameter=" + parameter +
                ", statOrElement=" + statOrElement +
                ", renderer=" + renderer +
                ", combineType=" + combineType +
                ", category=" + category +
                ", priorityNum=" + priorityNum +
                ", priorityStr='" + priorityStr + '\'' +
                ", entryFlag=" + entryFlag +
                ", description='" + description + '\'' +
                ", label='" + label + '\'' +
                ", label2='" + label2 + '\'' +
                ", label5='" + label5 + '\'' +
                ", categories=" + categories +
                ", template='" + template + '\'' +
                '}';
    }
}