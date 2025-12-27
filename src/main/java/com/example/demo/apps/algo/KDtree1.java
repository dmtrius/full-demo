package com.example.demo.apps.algo;

import de.biomedical_imaging.edu.wlu.cs.levy.CG.KDTree;
import lombok.SneakyThrows;

import static java.lang.IO.println;

public class KDtree1 {
    void main() {
        m1();
    }

    @SneakyThrows
    void m1() {
        KDTree<String> tree = new KDTree<>(3);

        tree.insert(new double[]{2, 3, 4}, "A");
        tree.insert(new double[]{5, 4, 2}, "B");
        tree.insert(new double[]{9, 6, 7}, "C");
        tree.insert(new double[]{4, 7, 9}, "D");
        tree.insert(new double[]{8, 1, 5}, "E");

        double[] query = {6, 3, 5};

        String nearest = tree.nearest(query);
        println("Nearest: " + nearest);

        println("Neighbors:");
        var neighbors = tree.nearest(query, 3);
        neighbors.forEach(IO::println);

        println("Range:");
        double[] min = {3, 2, 1};
        double[] max = {9, 7, 8};
        var results = tree.range(min, max);
        results.forEach(IO::println);
    }
}
