package com.example.demo.apps;


import java.util.Arrays;
import java.util.Random;

public class App4 {
    public static void main(String... args) {
        testArrs();
        int[] arr = new int[5];
        System.out.println(Arrays.toString(arr));
    }

    private static final Random rand = new Random();

    private static void testArrs() {
        int[][] arr = new int[10][10];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                int value = rand.nextInt(16);
                if (value < 10) {
                    value += 10;
                }
                arr[i][j] = value;
            }
        }
        System.out.println(printArray(arr));
    }

    private static String printArray(int[][] array) {
        return Arrays.deepToString(array)
                .replace("],","\n").replace(",","\t| ")
                .replaceAll("[\\[\\]]", " ");
    }
}
