// Parser+lexer for lib/gamedata/projection.txt - every "projection" (element
// damage types like fire/cold, plus non-element ones like turn-undead,
// stinking cloud, etc): descriptions, damage formula (numerator/
// denominator/divisor/cap), message type, and display colour. Cf.
// src/obj-init.c: struct file_parser projection_parser (obj-init.c:470),
// directive table at obj-init.c:400-414 (parse_projection_code/_name/
// _type/_desc/_player_desc/_blind_desc/_lash_desc/_numerator/_denominator/
// _divisor/_damage_cap/_message_type/_obvious/_wake/_color).
//
// No significant problems found - verified the mandatory-vs-optional split
// in `projection`'s sequence (code/type/desc/blindDesc/obvious/colour
// mandatory, everything else '?') against every record in projection.txt:
// the mandatory fields are present everywhere, and every field marked
// optional here is indeed missing from at least one real record (e.g.
// TURN_UNDEAD/TURN_LIVING have no name/player-desc/lash-desc/numerator/
// denominator/divisor/damage-cap/msgt/wake at all). `denominator`'s dice
// alternative also matches the only dice shape actually used ("8+1d4").

grammar Projection;

@header {
            import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
            import uk.co.jackoftrades.middle.combat.enums.ProjectionType;
            import uk.co.jackoftrades.backend.numerics.Random;
            import uk.co.jackoftrades.middle.enums.MessageType;
            import uk.co.jackoftrades.frontend.colour.enums.ColourType;
            import uk.co.jackoftrades.middle.game.Projection;

            import java.util.ArrayList;
            import java.util.List;
        }

// "code:<PROJ_CODE>" - starts a new projection record; for type:element
// projections must match list-elements.h's ordering, otherwise list-projections.h's.
code    returns[ProjectionEnum projectionCode]
        :   CODE UCASE {
            String projectionEnumString = "PROJ_" + $UCASE.getText();
            $projectionCode = ProjectionEnum.valueOf(projectionEnumString);
        }
        ;

// "name:<text>" - human-readable name; links brand.txt entries to this projection.
name    returns[String projectionName]
        :   NAME LWORDS {
            $projectionName = $LWORDS.getText();
        }

        ;

// "type:element|environs|monster" - what kind of projection this is.
type    returns[ProjectionType projectionType]
        :   TYPE res=('element'|'environs'|'monster') {
            String projectionTypeString = "PT_" + $res.getText().toUpperCase();
            $projectionType = ProjectionType.valueOf(projectionTypeString);
        }
        ;

// "desc:<text>" - general flavour description.
desc    returns[String description]
        :   DESC LWORDS {
            $description = $LWORDS.getText();
        }
        ;

// "player-desc:<text>" - description used when the player causes this projection.
playerDesc
        returns[String playerDescription]
        :   PLAYER_DESC LWORDS {
            $playerDescription = $LWORDS.getText();
        }
        ;

// "blind-desc:<text>" - description used when the player is blind.
blindDesc
        returns[String blindDescription]
        :   BLIND_DESC LWORDS {
            $blindDescription = $LWORDS.getText();
        }
        ;

// "lash-desc:<text>" - description used for a "lash" (whip-like) attack of
// this type.
lashDesc
        returns[String lashDescription]
        :   LASH_DESC LWORDS {
            $lashDescription = $LWORDS.getText();
        }
        ;

// "numerator:<value>" - damage multiplier numerator.
numerator
        returns [int num]
        :   NUMERATOR NUMBER {
            $num = Integer.parseInt($NUMBER.getText());
        }
        ;

// A "<base>+<dice>d<sides>" dice expression - the only form denominator:
// actually uses when it isn't a bare number (e.g. "8+1d4").
dice    returns [Random die]
        :   base=NUMBER '+' diceType=NUMBER 'd' sides=NUMBER {
            $die = new Random(
                Integer.parseInt($base.getText()), 1,
                Integer.parseInt($diceType.getText()),
                Integer.parseInt($sides.getText()), false);
        }
        ;

// "denominator:<value>|<dice>" - damage divisor, either a literal number or
// a dice expression (e.g. "8+1d4").
denominator
        returns [int den, Random denDice, boolean isDice]
        :   DENOMINATOR (NUMBER | dice) {
            if ($NUMBER == null) {
                $denDice = $dice.die;
                $isDice = true;
                $den = -1;
            } else {
                $den = Integer.parseInt($NUMBER.getText());
                $isDice = false;
                $denDice = null;
            }
        }
        ;

// "divisor:<value>" - HP divisor (e.g. for breath attacks scaled to the
// breather's HP).
divisor returns[int div]
        :   DIVISOR NUMBER {
            $div = Integer.parseInt($NUMBER.getText());
        }
        ;

// "damage-cap:<value>" - maximum damage this projection can deal.
damageCap
        returns[int damCap]
        :   DAMAGE_CAP NUMBER {
            $damCap = Integer.parseInt($NUMBER.getText());
        }
        ;

// "msgt:<MSG_TYPE>" - message type/sound used when this projection hits.
msgt    returns[MessageType sound]
        :   MSGT UCASE {
            String enumTag = "MSG_" + $UCASE.getText();
            $sound = MessageType.valueOf(enumTag);
        }
        ;

// "obvious:0|1" - whether this projection's effect is obvious to the player.
obvious returns[boolean isObvious]
        :   OBVIOUS NUMBER {
            int number = Integer.parseInt($NUMBER.getText());
            $isObvious = (number == 1);
        }
        ;

// "wake:0|1" - whether this projection wakes sleeping monsters.
wake    returns[boolean willWake]
        :   WAKE NUMBER {
            int number = Integer.parseInt($NUMBER.getText());
            $willWake = (number == 1);
        }
        ;

// "color:<name>" - display colour, either a single-letter/UPPER_CASE code
// (UCASE) or a Title Case full name (TCASE), e.g. "color:L_DARK" vs
// "color:Light dark".
colour  returns[ColourType col]
        :   COLOUR (tc=TCASE|uc=UCASE) {
            String colourName;

            if ($tc == null) {
                colourName = $uc.getText().toLowerCase();
            } else {
                colourName = $tc.getText().toLowerCase();
            }

            $col = ColourType.findColourType(colourName);
        }
        ;

// One full projection record - a fixed sequence with code/type/desc/
// blindDesc/obvious/colour mandatory and everything else optional,
// confirmed to match every record in projection.txt (see top-of-file note).
projection
        returns[Projection proj]
        :   code
            name?
            type
            desc
            playerDesc?
            blindDesc
            lashDesc?
            numerator?
            denominator?
            divisor?
            damageCap?
            msgt?
            obvious
            wake?
            colour {
                ProjectionEnum codeInit = $code.projectionCode;

                String nameInit;
                if ($name.ctx == null)
                    nameInit = "";
                else
                    nameInit = $name.projectionName;

                ProjectionType typeInit = $type.projectionType;

                String descInit = $desc.description;

                String pDesc = ($playerDesc.ctx == null) ? "" : $playerDesc.playerDescription;

                String bDesc = $blindDesc.blindDescription;

                String lDesc = ($lashDesc.ctx == null) ? "" : $lashDesc.lashDescription;

                int num = ($numerator.ctx == null) ? -1 : $numerator.num;

                int den = ($denominator.ctx == null) ? -1 : $denominator.den;
                Random dice =  ($denominator.ctx == null) ? null : $denominator.denDice;
                boolean isRandom = ($denominator.ctx == null) ? false : $denominator.isDice;

                int div = ($divisor.ctx == null) ? -1 : $divisor.div;

                int dam = ($damageCap.ctx == null) ? -1 : $damageCap.damCap;

                MessageType msg = ($msgt.ctx == null) ? null : $msgt.sound;

                boolean obv = $obvious.isObvious;

                boolean wak = ($wake.ctx == null) ? false : $wake.willWake;

                ColourType col = $colour.col;

                if (isRandom)
                    $proj = new Projection(codeInit, nameInit, typeInit, descInit, pDesc, bDesc, lDesc, num, dice, div, dam, msg, obv, wak, col);
                else
                    $proj = new Projection(codeInit, nameInit, typeInit, descInit, pDesc, bDesc, lDesc, num, den, div, dam, msg, obv, wak, col);
            }
        ;

// Top-level rule: the whole file is one or more projection records.
file    returns[List<Projection> projections]
        @init {
            $projections = new ArrayList<>();
        }
        :   (projection {
                $projections.add($projection.proj);
            })+
            EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL     :   ' '* '\r'? '\n' -> skip
        ;

// CODE through COLOUR below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
CODE    :   'code:'
        ;

NAME    :   'name:'
        ;

TYPE    :   'type:'
        ;

DESC    :   'desc:'
        ;

PLAYER_DESC
        :   'player-desc:'
        ;

BLIND_DESC
        :   'blind-desc:'
        ;

LASH_DESC
        :   'lash-desc:'
        ;

NUMERATOR
        :   'numerator:'
        ;

DENOMINATOR
        :   'denominator:'
        ;

DIVISOR :   'divisor:'
        ;

DAMAGE_CAP
        :   'damage-cap:'
        ;

MSGT    :   'msgt:'
        ;

OBVIOUS :   'obvious:'
        ;

WAKE    :   'wake:'
        ;

COLOUR  :   'color:'
        ;

// An UPPER_CASE_WITH_UNDERSCORES symbolic name - used for code:/msgt: and
// the all-caps form of color:.
UCASE   :   ('A'..'Z'|'_')+
        ;

// Free-running lowercase description text (with spaces/commas) - used for
// name:/desc:/player-desc:/blind-desc:/lash-desc:.
LWORDS  :   ('a'..'z') ('a'..'z'|' '|',')*
        ;

// All-lowercase text with hyphens - unused by any active rule (type:'s
// literal 'element'/'environs'/'monster' alternatives are matched directly
// as string literals, not via this token).
LCASE   :   ('a'..'z'|'-')+
        ;

// A Title Case (or mixed-case) colour name - the other form color: can take.
TCASE   :   ('A'..'Z') ('a'..'z'|'A'..'Z'|' ')*
        ;

// A bare non-negative integer.
NUMBER  :   ('0'..'9')+
        ;