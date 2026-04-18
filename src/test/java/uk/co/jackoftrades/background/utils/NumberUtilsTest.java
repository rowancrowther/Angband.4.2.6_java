package uk.co.jackoftrades.background.utils;

import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.background.Rational;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NumberUtilsTest {

    @Test
    void addGuardI() {
        int largeNumber = Integer.MAX_VALUE - 3;
        int plusNumber = 7;
        int smallNumber = Integer.MIN_VALUE + 5;
        int minusNumber = -8;

        assertAll(
                () -> assertEquals(Integer.MAX_VALUE, NumberUtils.addGuardI(largeNumber, plusNumber)),
                () -> assertEquals(Integer.MIN_VALUE, NumberUtils.addGuardI(smallNumber, minusNumber)),
                () -> assertEquals(plusNumber + minusNumber, NumberUtils.addGuardI(plusNumber, minusNumber))
        );
    }

    @Test
    void subGuardI() {
        int smallNumber = Integer.MIN_VALUE + 12;
        int minusNumber = 24;
        int largeNumber = Integer.MAX_VALUE - 16;
        int plusNumber = -72;

        assertAll(
                () -> assertEquals(Integer.MIN_VALUE, NumberUtils.subGuardI(smallNumber, minusNumber)),
                () -> assertEquals(Integer.MAX_VALUE, NumberUtils.subGuardI(largeNumber, plusNumber)),
                () -> assertEquals(minusNumber - plusNumber, NumberUtils.subGuardI(minusNumber, plusNumber))
        );
    }

    @Test
    void addGuardI16() {
        int max = 32767;
        int min = -32768;
        int largeNumber = max - 7;
        int addNumber = 8;
        int smallNumber = min + 12;
        int subNumber = -29;

        assertAll(
                () -> assertEquals(max, NumberUtils.addGuardI16(largeNumber, addNumber)),
                () -> assertEquals(min, NumberUtils.addGuardI16(smallNumber, subNumber)),
                () -> assertEquals(addNumber + subNumber, NumberUtils.addGuardI16(addNumber, subNumber))
        );
    }

    @Test
    void subGuardI16() {
        int max = 32767;
        int min = -32768;
        int largeNumber = max - 12;
        int minusNumber = -23;
        int smallNumber = min + 11;
        int plusNumber = 27;

        assertAll(
                () -> assertEquals(max, NumberUtils.subGuardI16(largeNumber, minusNumber)),
                () -> assertEquals(min, NumberUtils.subGuardI16(smallNumber, plusNumber)),
                () -> assertEquals(plusNumber - minusNumber, NumberUtils.subGuardI16(plusNumber, minusNumber)),
                () -> assertEquals(minusNumber - plusNumber, NumberUtils.subGuardI16(minusNumber, plusNumber))
        );
    }

    @Test
    void mean() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(17);

        Rational rf2 = new Rational(7,2);
        Rational r3 = new Rational(4);
        Rational r1 = new Rational(3);
        Rational r6 = new Rational(29, 4);

        Rational resf2 = NumberUtils.mean(list,2);

        assertAll(
                () -> assertTrue(Rational.zero.equals(NumberUtils.mean(list, 0))),
                () -> assertTrue(rf2.equals(resf2)),
                () -> assertTrue(r3.equals(NumberUtils.mean(list, 3))),
                () -> assertTrue(r1.equals(NumberUtils.mean(list, 1))),
                () -> assertTrue(r6.equals(NumberUtils.mean(list, 6)))
        );
    }

    @Test
    void variance() {
        ArrayList<Integer> data = new ArrayList<>();
        data.add(2);
        data.add(3);
        data.add(4);
        data.add(5);

        Rational var1 = NumberUtils.variance(data, 0, true, false);
        Rational var2 = NumberUtils.variance(data, 2, true, false);
        Rational var3 = NumberUtils.variance(data, 3, true, false);
        Rational var4 = NumberUtils.variance(data, 4, false, false);
        Rational var5 = NumberUtils.variance(data, 6, true, false);
        Rational var3a = NumberUtils.variance(data, 3, true, true);
        Rational var3b = NumberUtils.variance(data, 4, false, true);

        assertAll(
                () -> assertTrue(Rational.zero.equals(var1)),
                () -> assertTrue(new Rational(1, 2).equals(var2)),
                () -> assertTrue(new Rational(1, 1).equals(var3)),
                () -> assertTrue(new Rational(5, 4).equals(var4)),
                () -> assertTrue(new Rational(5, 3).equals(var5)),
                () -> assertTrue(new Rational(1, 3).equals(var3a)),
                () -> assertTrue(new Rational(5, 16).equals(var3b))
        );
    }
}