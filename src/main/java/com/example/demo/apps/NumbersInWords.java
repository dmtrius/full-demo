package com.example.demo.apps;

public class NumbersInWords {
    private static String convertLessThanOneThousand(long number) {
        if (number == 0) {
            return "Zero";
        }

        StringBuilder current = new StringBuilder();

        if (number >= 100) {
            current.append(NumberToWords.ones[(int)number / 100])
                    .append(" Hundred");
            number %= 100;
            if (number > 0) {
                current.append(" And ");
            }
        }

        if (number >= 20) {
            current.append(NumberToWords.tens[(int)number / 10]);
            number %= 10;
            if (number > 0) {
                current.append("-")
                        .append(NumberToWords.ones[(int)number]);
            }
        } else if (number >= 10) {
            current.append(NumberToWords.teens[(int)number - 10]);
        } else if (number > 0) {
            current.append(NumberToWords.ones[(int)number]);
        }

        return current.toString().trim();
    }

    public static String numberToWords(long number) {
        if (number == 0) {
            return "Zero";
        }

        int thousandCounter = 0;
        String current = "";

        while (number > 0) {
            if (number % 1000 != 0) {
                current = convertLessThanOneThousand(
                        number % 1000) + " " + NumberToWords.thousands[thousandCounter] +
                        " " + current;
            }
            number /= 1000;
            thousandCounter++;
        }

        return current.trim();
    }

    public static void main(String[] args) {
        long number = 7_123_456_789L;
        System.out.println(numberToWords(number));
    }
}

class NumberToWords {
    static final String[] ones = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"
    };

    static final String[] teens = {
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    static final String[] thousands = {
            "", "Thousand", "Million", "Billion"
    };
}