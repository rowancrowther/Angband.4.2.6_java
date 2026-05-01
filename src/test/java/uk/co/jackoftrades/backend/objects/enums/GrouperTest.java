package uk.co.jackoftrades.backend.objects.enums;

import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.objects.Grouper;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrouperTest {
    private Grouper grouper = new Grouper(TValue.TV_AMULET, "Amulet");

    @Test
    void getName() {
        assertTrue("Amulet".equals(grouper.getName()));
    }

    @Test
    void gettValue() {
        assertEquals(TValue.TV_AMULET, grouper.gettValue());
    }
}