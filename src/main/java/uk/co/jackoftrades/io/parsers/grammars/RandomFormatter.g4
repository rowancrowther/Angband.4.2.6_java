grammar RandomFormatter;

@header {import uk.co.jackoftrades.background.Random;}

// offset = '-'?INT
offset returns[boolean negate, int base] : (MINUS { $negate = $MINUS == null ? false : true; })? INT { $base = $INT.int;
 if ($negate) $base = $base * -1; };

// mult_int = '*'INT?
mult_int returns[int dice] : MULT { $dice = 1; } (INT { $dice = $INT.int; })? ;

// mult = '+'?INT'*'INT?
mult returns[int mBonus, int dice]   : PLUS? INT mult_int { $mBonus = $INT.int; $dice = $mult_int.dice; };

// dice_int = 'd'INT
dice_int returns [int sides] : DIE INT { $sides = $INT.int; } ;

// int_dice_int = INT'd'INT
int_dice_int returns[int dice, int sides] : INT dice_int { $dice = $INT.int; $sides = $dice_int.sides; } ;

// plus_int_dice_int = '+'INT'd'INT
plus_int_dice_int returns[int dice, int sides] : PLUS INT dice_int { $dice = $INT.int; $sides = $dice_int.sides; } ;

// plus_dice_int = '+d'INT
plus_dice_int returns[int dice, int sides] : PLUS dice_int { $dice = 1; $sides = $dice_int.sides; };

// line = 'dice:'random
line returns[Random rand, String route] : LINE_START random {$rand = $random.randomDice; $route = $random.route; };

// random is one of
//
//    - offset
//    - int_dice_int
//    - mult dice_int
//    - offset mult dice_int
//    - dice_int
//    - offset plus_dice_int
//    - offset plus_int_dice_int
random returns[Random randomDice, String route]
     : offset {
        $randomDice = new Random($offset.base, 1, 1, 0, $offset.negate);
        $route = "offset"; }
     | int_dice_int {
        $randomDice = new Random(0, 1, $int_dice_int.dice, $int_dice_int.sides, false);
        $route = "int_dice_int"; }
     | mult dice_int {
        $randomDice = new Random(0, $mult.mBonus, $mult.dice, $dice_int.sides, false);
        $route = "mult dice_int"; }
     | offset mult dice_int {
        $randomDice = new Random($offset.base, $mult.mBonus, $mult.dice, $dice_int.sides, $offset.negate);
        $route = "offset mult dice_int"; }
     | dice_int {
        $randomDice = new Random(0, 1, 1, $dice_int.sides, false);
        $route = "dice_int"; }
     | offset plus_dice_int {
        $randomDice = new Random($offset.base, 1, 1, $plus_dice_int.sides, $offset.negate);
        $route = "offset plus_dice_int"; }
     | offset plus_int_dice_int {
        $randomDice = new Random($offset.base, 1, $plus_int_dice_int.dice, $plus_int_dice_int.sides, $offset.negate);
        $route = "offset plus_int_dice_int"; };

LINE_START   : 'd' 'i' 'c' 'e' ':'           ;

COLON        : ':'                           ;

MINUS        : '-'                           ;

INT          : '0'..'9'+                     ;

PLUS         : '+'                           ;

MULT         : '*'                           ;

DIE          : 'd'                           ;