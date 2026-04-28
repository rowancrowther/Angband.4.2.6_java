package uk.co.jackoftrades.middle.objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.HashMap;

/**
 * STEM CLASS
 */

public class ObjectProperty {
    private static final Logger logger = LogManager.getLogger();

    private ObjPropertyType type;
    private String subtype;
    private String idType;
    private ObjectModifier index;
    private int power;
    private int mult;
    private HashMap<TValue, Integer> typeMults;
    private String adjective;
    private String negAdjective;

}