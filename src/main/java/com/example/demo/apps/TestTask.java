package com.example.demo.apps;

public class TestTask {
    public int pairsCalculation(int[] a) {
        int result = 0;
        if (a.length == 1) {
            return result;
        }
        //int[] a = {1, 2, 3, -2, 5, -1};
//        (a)-> {
//
//        }

        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if ((a[i] + a[j]) == 0) {
                    ++result;
                }
            }
        }
        return result;
    }
}
