package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.background.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.background.utils.Flag;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;

public class ObjectBase {
    private String name;
    private TValue tVal;
    private AngbandDisplayCharacter angbandDisplayCharacter;

    private Flag<ObjectFlag> flags;
    private Flag<ObjectKindFlag> kindFlags;

    private int breakPerc;
    private int maxStack;
    private int numSvals;
}