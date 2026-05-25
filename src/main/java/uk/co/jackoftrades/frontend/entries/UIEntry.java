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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.frontend.entries;

import uk.co.jackoftrades.frontend.entries.enums.EntryFlag;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
import uk.co.jackoftrades.middle.enums.StatElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;

import java.util.List;

public class UIEntry {
    private String name;
    private ElementEnum parameter;
    private boolean parmIsElement;
    private StatElemType statOrElement;
    private UIEntryRenderer renderer;
    private CombinerName combineType;
    private int priorityNum;
    private String priorityStr;
    private EntryFlag entryFlag;
    private String description;
    private String label;
    private String label2;
    private String label5;
    private List<String> categories;
    private UIEntryBase template;

    public UIEntry(String name,
                   ElementEnum parameter,
                   StatElemType parmType,
                   UIEntryRenderer renderer,
                   CombinerName combineType,
                   List<String> categories,
                   int priorityNum,
                   String priorityStr,
                   EntryFlag entryFlag,
                   String description,
                   String label,
                   String label5,
                   String label2,
                   UIEntryBase template) {
        this.name = name;
        this.parameter = parameter;
        this.statOrElement = parmType;
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