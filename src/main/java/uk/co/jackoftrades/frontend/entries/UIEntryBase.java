package uk.co.jackoftrades.frontend.entries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;

import java.util.ArrayList;

public class UIEntryBase {
    private final static Logger logger = LogManager.getLogger();

    private String name;
    private UIEntryRenderer renderer;
    private CombinerName combine;
    private ArrayList<String> categories;
    private String flags;
    private String desc;

    public UIEntryBase(String name, UIEntryRenderer renderer, CombinerName combine, ArrayList<String> categories, String flags, String desc) {
        this.name = name;
        this.renderer = renderer;
        this.combine = combine;
        this.categories = categories;
        this.flags = flags;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UIEntryBase{" +
                "name='" + name + '\'' +
                ", renderer=" + renderer +
                ", combine=" + combine +
                ", categories=" + categories +
                ", flags='" + flags + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}