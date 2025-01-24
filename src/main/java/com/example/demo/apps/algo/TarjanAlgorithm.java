package com.example.demo.apps.algo;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class TarjanAlgorithm {
    private int index = 0; // To keep track of the index assigned to each vertex
    private final int[] indices; // To store the index of each vertex
    private final int[] lowLinks; // To store the low-link values
    private final boolean[] onStack; // To check if a vertex is on the stack
    private final Deque<Integer> stack; // Stack to hold the current path of vertices
    private final List<List<Integer>> stronglyConnectedComponents; // List to hold the SCCs

    public TarjanAlgorithm(int numberOfVertices) {
        indices = new int[numberOfVertices];
        lowLinks = new int[numberOfVertices];
        onStack = new boolean[numberOfVertices];
        stack = new LinkedList<>();
        stronglyConnectedComponents = new ArrayList<>();

        // Initialize the indices and low-links to -1 (indicating unvisited)
        for (int i = 0; i < numberOfVertices; i++) {
            indices[i] = -1;
            lowLinks[i] = -1;
        }
    }

    public List<List<Integer>> findStronglyConnectedComponents(List<List<Integer>> graph) {
        for (int v = 0; v < graph.size(); v++) {
            if (indices[v] == -1) {
                strongConnect(v, graph);
            }
        }
        return stronglyConnectedComponents;
    }

    private void strongConnect(int v, List<List<Integer>> graph) {
        // Set the depth index for v to the smallest unused index
        indices[v] = index;
        lowLinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;

        // Consider successors of v
        for (int w : graph.get(v)) {
            if (indices[w] == -1) {
                // Successor w has not yet been visited; recurse on it
                strongConnect(w, graph);
                lowLinks[v] = Math.min(lowLinks[v], lowLinks[w]);
            } else if (onStack[w]) {
                // Successor w is in stack and hence in the current SCC
                lowLinks[v] = Math.min(lowLinks[v], indices[w]);
            }
        }

        // If v is a root node, pop the stack and generate an SCC
        if (lowLinks[v] == indices[v]) {
            List<Integer> scc = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                scc.add(w);
            } while (w != v);
            stronglyConnectedComponents.add(scc);
        }
    }

    @SuppressWarnings("unused")
    public static void main(String... args) {
        // Example usage:

        // Create a directed graph using an adjacency list representation
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            graph.add(new ArrayList<>());
        }

        graph.get(0).add(1);
        graph.get(1).add(2);
        graph.get(2).add(0);
        graph.get(1).add(3);
        graph.get(3).add(4);

        TarjanAlgorithm tarjan = new TarjanAlgorithm(graph.size());

        List<List<Integer>> sccs = tarjan.findStronglyConnectedComponents(graph);

        log.info("Strongly Connected Components:");
        for (List<Integer> scc : sccs) {
            log.info(scc.toString());
        }
    }
}
