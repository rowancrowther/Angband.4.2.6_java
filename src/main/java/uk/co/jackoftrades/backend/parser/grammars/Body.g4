// Parser+lexer for lib/gamedata/body.txt - the player race body plan(s):
// a body name followed by an ordered list of equipment slot types and their
// display names (e.g. "slot:WEAPON:weapon"). Cf. src/init.c: struct
// file_parser body_parser (init.c:2425), directives "body str name" and
// "slot sym slot sym name" -> parse_body_body/parse_body_slot
// (init.c:2310-2353).

grammar Body;

@header {
    import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
    import uk.co.jackoftrades.middle.player.PlayerBody;
    import uk.co.jackoftrades.middle.objects.ItemObject;

    import java.util.Map;
    import java.util.LinkedHashMap;
    import java.util.List;
    import java.util.ArrayList;
}

// One equipment slot definition: its type enum (EQUIP_WEAPON, EQUIP_RING,
// ...) and its display name (e.g. "weapon", "right hand").
@members {
    public record BodySlot(EquipmentSlotsEnum slotType, String name){}
}

// "body:<name>" - starts a new body record - cf. parse_body_body (init.c:2310).
body
        returns[String bodyName]
        :   BODY STRING { $bodyName = $STRING.getText(); }
        ;

// "slot:<EQUIP_TYPE>:<display name>" - one equipment slot, in file order -
// cf. parse_body_slot (init.c:2320). See problem #2 re: order being lost
// downstream.
slot
        returns[BodySlot bodySlot]
        :   SLOT UCASE COLON STRING {
                String raw = $UCASE.getText().toUpperCase().trim();
                EquipmentSlotsEnum equipSlot = EquipmentSlotsEnum.valueOf("EQUIP_" + raw);
                String locationStr = $STRING.getText();
                $bodySlot = new BodySlot(equipSlot, locationStr);
            }
        ;

// One full body record: a body: line followed by one or more slot: lines.
entry
        returns[PlayerBody playerBody]
        @init {
            // BUG: never reassigned below - every PlayerBody ends up with count == 0.
            int countInit = 0;
            ItemObject objInit = null;
            // PROBLEM: HashMap doesn't preserve the slot order body.txt defines;
            // see top-of-file note #2.
            Map<BodySlot, ItemObject> slotsInit = new LinkedHashMap<>();
            String bodyNameInit = "";
        }
        @after {
            $playerBody = new PlayerBody(bodyNameInit, slotsInit.size(), slotsInit);
        }
        :   body { bodyNameInit = $body.bodyName; }
            (slot {
                slotsInit.put($slot.bodySlot, objInit);
            })+
        ;

// Top-level rule: the whole file is one or more body records.
file
        returns[List<PlayerBody> bodies]
        @init {
            $bodies = new ArrayList<>();
        }
        :   (entry {
                $bodies.add($entry.playerBody);
            })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

BODY
        :   'body:'
        ;

SLOT
        :   'slot:'
        ;

// Field separator within a slot: line.
COLON
        :   ':'
        ;

// An equipment slot type name, e.g. "WEAPON", "RING" (the "EQUIP_" prefix
// is added in the `slot` rule's action).
UCASE
        :   ('A'..'Z' | '_')+
        ;

// Free-running letters/spaces - used for body:'s body name and slot:'s
// display-name field.
STRING
        :   ('a'..'z' | 'A'..'Z' | ' ')+
        ;