package com.example.demo.apps;

import java.math.BigDecimal;

public class App53 {
    void main() {
        BigDecimal n = BigDecimal.TWO.pow(127).subtract(BigDecimal.ONE);
        IO.println(n);
        IO.println(isLeapYear(1900));
        IO.println(isLeapYear(2000));
        IO.println(isLeapYear(1976));
        IO.println(isLeapYear(2004));
        IO.println(isLeapYear(2002));
        IO.println();
        IO.println(isLeapYear2(1900));
        IO.println(isLeapYear2(2000));
        IO.println(isLeapYear2(1976));
        IO.println(isLeapYear2(2004));
        IO.println(isLeapYear2(2002));
    }

    boolean isLeapYear(int year) {
        IO.println("year: " + year);
        return ((year * 1073750999L) & 3221352463L) <= 126976;
    }

    boolean isLeapYear2(int year) {
        IO.println("year: " + year);
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
