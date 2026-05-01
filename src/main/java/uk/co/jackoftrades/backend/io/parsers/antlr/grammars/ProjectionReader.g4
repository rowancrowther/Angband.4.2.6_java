grammar ProjectionReader;

@header {
            import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
            import uk.co.jackoftrades.middle.combat.enums.ProjectionType;
            import uk.co.jackoftrades.backend.numerics.Random;
            import uk.co.jackoftrades.middle.enums.MessageEnum;
            import uk.co.jackoftrades.backend.colour.enums.ColourType;
            import uk.co.jackoftrades.middle.game.Projection;

            import java.util.ArrayList;
        }

code    returns[ProjectionEnum projectionCode]
        :   CODE UCASE {
            String projectionEnumString = "PROJ_" + $UCASE.getText();
            $projectionCode = ProjectionEnum.valueOf(projectionEnumString);
        }
        ;

name    returns[String projectionName]
        :   NAME LWORDS {
            $projectionName = $LWORDS.getText();
        }

        ;

type    returns[ProjectionType projectionType]
        :   TYPE res=('element'|'environs'|'monster') {
            String projectionTypeString = "PT_" + $res.getText().toUpperCase();
            $projectionType = ProjectionType.valueOf(projectionTypeString);
        }
        ;

desc    returns[String description]
        :   DESC LWORDS {
            $description = $LWORDS.getText();
        }
        ;

playerDesc
        returns[String playerDescription]
        :   PLAYER_DESC LWORDS {
            $playerDescription = $LWORDS.getText();
        }
        ;

blindDesc
        returns[String blindDescription]
        :   BLIND_DESC LWORDS {
            $blindDescription = $LWORDS.getText();
        }
        ;

lashDesc
        returns[String lashDescription]
        :   LASH_DESC LWORDS {
            $lashDescription = $LWORDS.getText();
        }
        ;

numerator
        returns [int num]
        :   NUMERATOR NUMBER {
            $num = Integer.parseInt($NUMBER.getText());
        }
        ;

dice    returns [Random die]
        :   base=NUMBER '+' diceType=NUMBER 'd' sides=NUMBER {
            $die = new Random(
                Integer.parseInt($base.getText()), 1,
                Integer.parseInt($diceType.getText()),
                Integer.parseInt($sides.getText()), false);
        }
        ;

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

divisor returns[int div]
        :   DIVISOR NUMBER {
            $div = Integer.parseInt($NUMBER.getText());
        }
        ;

damageCap
        returns[int damCap]
        :   DAMAGE_CAP NUMBER {
            $damCap = Integer.parseInt($NUMBER.getText());
        }
        ;

msgt    returns[MessageEnum sound]
        :   MSGT UCASE {
            String enumTag = "MSG_" + $UCASE.getText();
            $sound = MessageEnum.valueOf(enumTag);
        }
        ;

obvious returns[boolean isObvious]
        :   OBVIOUS NUMBER {
            int number = Integer.parseInt($NUMBER.getText());
            $isObvious = (number == 1);
        }
        ;

wake    returns[boolean willWake]
        :   WAKE NUMBER {
            int number = Integer.parseInt($NUMBER.getText());
            $willWake = (number == 1);
        }
        ;

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

                MessageEnum msg = ($msgt.ctx == null) ? null : $msgt.sound;

                boolean obv = $obvious.isObvious;

                boolean wak = ($wake.ctx == null) ? false : $wake.willWake;

                ColourType col = $colour.col;

                if (isRandom)
                    $proj = new Projection(codeInit, nameInit, typeInit, descInit, pDesc, bDesc, lDesc, num, dice, div, dam, msg, obv, wak, col);
                else
                    $proj = new Projection(codeInit, nameInit, typeInit, descInit, pDesc, bDesc, lDesc, num, den, div, dam, msg, obv, wak, col);
            }
        ;

file    returns[ArrayList<Projection> projections]
        @init {
            $projections = new ArrayList<>();
        }
        :   (projection {
                $projections.add($projection.proj);
            })+
            EOF
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL     :   ' '* '\r'? '\n' -> skip
        ;

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

UCASE   :   ('A'..'Z'|'_')+
        ;

LWORDS  :   ('a'..'z') ('a'..'z'|' '|',')*
        ;

LCASE   :   ('a'..'z'|'-')+
        ;

TCASE   :   ('A'..'Z') ('a'..'z'|'A'..'Z'|' ')*
        ;

NUMBER  :   ('0'..'9')+
        ;