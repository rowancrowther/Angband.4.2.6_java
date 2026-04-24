package uk.co.jackoftrades.middle.monsters;

import java.util.HashMap;
import java.util.Map;

/**
 * STUB CLASS: TODO Code, comment and test this
 */

public class MonsterBases {
    private Map<String, MonsterBase> bases;

    public MonsterBases() {
        bases = new HashMap<>();
    }

    public int size() {
        return bases.size();
    }

    public void put(MonsterBase base) {
        bases.put(base.getName(), base);
    }
}
