package com.example.demo.apps.algo;

import smile.neighbor.KDTree;

import java.util.Arrays;

import static java.lang.IO.println;

public class KDTree2 {
    void main() {
        m1();
    }

    void m1() {
        double[][] points = {
                {2, 3, 4},
                {5, 4, 2},
                {9, 6, 7},
                {4, 7, 9},
                {8, 1, 5}
        };

        KDTree<double[]> tree = KDTree.of(points);

        var neighbors = tree.search(new double[]{6, 3, 5}, 3);
        for (var n : neighbors) {
            println(Arrays.toString(n.key()));
        }
    }
}
