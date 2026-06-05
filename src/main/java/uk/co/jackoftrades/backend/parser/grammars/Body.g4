grammar Body;

@header {
    import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
    import uk.co.jackoftrades.middle.player.PlayerBody;
    import uk.co.jackoftrades.middle.objects.ItemObject;

    import java.util.Map;
    import java.util.HashMap;
    import java.util.List;
    import java.util.ArrayList;
}

@members {
    public record BodySlot(EquipmentSlotsEnum slotType, String name){}
}

body
        returns[String bodyName]
        :   BODY STRING { $bodyName = $STRING.getText(); }
        ;

slot
        returns[BodySlot bodySlot]
        :   SLOT UCASE COLON STRING {
                String raw = $UCASE.getText().toUpperCase().trim();
                EquipmentSlotsEnum equipSlot = EquipmentSlotsEnum.valueOf("EQUIP_" + raw);
                String locationStr = $STRING.getText();
                $bodySlot = new BodySlot(equipSlot, locationStr);
            }
        ;

entry
        returns[PlayerBody playerBody]
        @init {
            int countInit = 0;
            ItemObject objInit = null;
            Map<BodySlot, ItemObject> slotsInit = new HashMap<>();
            String bodyNameInit = "";
        }
        @after {
            $playerBody = new PlayerBody(bodyNameInit, countInit, slotsInit);
        }
        :   body { bodyNameInit = $body.bodyName; }
            (slot {
                slotsInit.put($slot.bodySlot, objInit);
            })+
        ;

file
        returns[List<PlayerBody> bodies]
        @init {
            $bodies = new ArrayList<>();
        }
        :   (entry {
                $bodies.add($entry.playerBody);
            })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

BODY
        :   'body:'
        ;

SLOT
        :   'slot:'
        ;

COLON
        :   ':'
        ;

UCASE
        :   ('A'..'Z' | '_')+
        ;

STRING
        :   ('a'..'z' | 'A'..'Z' | ' ')+
        ;