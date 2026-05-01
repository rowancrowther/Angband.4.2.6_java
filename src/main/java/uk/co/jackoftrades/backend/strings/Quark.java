package uk.co.jackoftrades.backend.strings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.AngbandModule;

import java.util.LinkedHashMap;
import java.util.Map;

public class Quark implements AngbandModule {
    private String name;
    private Map<Integer, String> quarks;
    private final Logger logger = LogManager.getLogger();

    /**
     * Add a string to this set of quarks and return a unique value to it
     *
     * @param string The string to add
     * @return a unique key for it where the key value is 1 higher than the previous highest value in this map.
     * This allows deletions from the middle of the map without looking at the deleted position by accident in the
     * future.
     */
    public int add(String string) throws NullPointerException {
        if (quarks == null) {
            String message = "Quark is null. String '" + string + "' cannot be added to a null quark.";
            logger.error(message);
            throw new NullPointerException(message);
        }

        int i = quarks.size();

        for (int index : quarks.keySet()) {
            if (index > i) i = index;
        }

        quarks.put(i + 1, string);
        return i + 1;
    }

    /**
     * Gets the value of the quark with given quark key
     *
     * @param key the key of the string we wish to obtain from the map
     * @return if the key is a valid key (contained in the key set), then return the appropriate string - otherwise
     * return null.
     */
    public String getQuark(int key) throws NullPointerException {
        if (quarks == null) {
            String message = "Quark is null. Key '" + key + "' will not be present.";
            logger.error(message);
            throw new NullPointerException(message);
        }

        if (quarks.containsKey(key)) return quarks.get(key);
        return null;
    }

    /**
     * @return the name of this module
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Initialize this module
     */
    @Override
    public void init() {
        quarks = new LinkedHashMap<>();
        name = "Quark";
    }

    /**
     * Cleanup set the linked hash map to null to allow for recycling
     */
    @Override
    public void cleanup() {
        quarks = null;
    }
}