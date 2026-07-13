package com.example.demo.apps.algo;

import de.biomedical_imaging.edu.wlu.cs.levy.CG.KDTree;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeyDuplicateException;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeySizeException;

public class KDtree1 {
    void main() throws KeyDuplicateException, KeySizeException {
        m1();
    }

    void m1() throws KeyDuplicateException, KeySizeException {
        KDTree<String> tree = new KDTree<>(3);

        tree.insert(new double[]{2, 3, 4}, "A");
        tree.insert(new double[]{5, 4, 2}, "B");
        tree.insert(new double[]{9, 6, 7}, "C");
        tree.insert(new double[]{4, 7, 9}, "D");
        tree.insert(new double[]{8, 1, 5}, "E");

        double[] query = {6, 3, 5};

        String nearest = tree.nearest(query);
        IO.println("Nearest: " + nearest);

        IO.println("Neighbors:");
        var neighbors = tree.nearest(query, 3);
        neighbors.forEach(IO::println);

        IO.println("Range:");
        double[] min = {3, 2, 1};
        double[] max = {9, 7, 8};
        var results = tree.range(min, max);
        results.forEach(IO::println);
    }
}
