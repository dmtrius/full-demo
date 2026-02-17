package com.example.demo.apps.algo;


import lombok.SneakyThrows;

import module java.base;

public class DijkstraAlgorithm {
    // Graph represented as adjacency list using HashMap
    private final Map<Integer, List<Node>> adjacencyList;
    // Map to store the shortest path tree
    Map<Integer, Integer> previousVertices;


    // Constructor initializes an empty adjacency list
    public DijkstraAlgorithm() {
        this.adjacencyList = new HashMap<>();
        this.previousVertices = new HashMap<>();
    }

    // Add vertex to the graph
    public void addVertex(int vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    // Add edge with weight to the graph
    public void addEdge(int source, int destination, int weight) {
        // Add source vertex if it doesn't exist
        adjacencyList.putIfAbsent(source, new ArrayList<>());
        // Add destination vertex if it doesn't exist
        adjacencyList.putIfAbsent(destination, new ArrayList<>());
        // Add edge from source to destination
        adjacencyList.get(source).add(new Node(destination, weight));
        // For undirected graph, add reverse-edge
        // adjacencyList.get(destination).add(new Node(source, weight));
    }

    // Dijkstra's algorithm implementation
    public Map<Integer, Integer> findShortestPaths(int startVertex) {
        // Priority queue to store vertices and their current distances
        PriorityQueue<Node> pq = new PriorityQueue<>(
                Comparator.comparingInt(Node::weight)
        );

        // Map to store final shortest distances
        Map<Integer, Integer> distances = new HashMap<>();

        // Initialize all distances as infinity except start vertex
        for (int vertex : adjacencyList.keySet()) {
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(startVertex, 0);

        // Add start vertex to priority queue
        pq.offer(new Node(startVertex, 0));

        // Process vertices in priority queue
        while (!pq.isEmpty()) {
            // Get vertex with minimum distance
            Node current = pq.poll();
            int currentVertex = current.vertex();
            int currentWeight = current.weight();

            // Skip if we've found a better path already
            if (currentWeight > distances.get(currentVertex)) {
                continue;
            }

            // Process all adjacent vertices
            for (Node neighbor : adjacencyList.get(currentVertex)) {
                int distance = currentWeight + neighbor.weight();
                // Update distance if we found a shorter path
                if (distance < distances.get(neighbor.vertex())) {
                    distances.put(neighbor.vertex(), distance);
                    previousVertices.put(neighbor.vertex(), currentVertex);
                    pq.offer(new Node(neighbor.vertex(), distance));
                }
            }
        }

        return distances;
    }

    // Helper method to get a path to a specific vertex
    public List<Integer> getPath(int startVertex, int endVertex) {
        Map<Integer, Integer> distances = findShortestPaths(startVertex);

        if (distances.get(endVertex) == Integer.MAX_VALUE) {
            return List.of(); // No path exists
        }

        List<Integer> path = new ArrayList<>();
        int current = endVertex;

        while (current != startVertex) {
            path.addFirst(current);
            current = previousVertices.get(current);
        }
        path.addFirst(startVertex);

        return path;
    }

    // Example usage
    @SneakyThrows
    void main() {
        DijkstraAlgorithm graph = new DijkstraAlgorithm();

        // Add vertices
        for (int i = 0; i < 6; i++) {
            graph.addVertex(i);
        }

        // Add edges
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 2, 1);
        graph.addEdge(1, 3, 5);
        graph.addEdge(2, 3, 8);
        graph.addEdge(2, 4, 10);
        graph.addEdge(3, 4, 2);
        graph.addEdge(3, 5, 6);
        graph.addEdge(4, 5, 3);

        // Find the shortest paths from vertex 0
        Map<Integer, Integer> distances = graph.findShortestPaths(0);
        // Print results
        System.out.println("Shortest distances from vertex 0:");
        distances.forEach((vertex, distance) ->
                System.out.println("To vertex " + vertex + ": " + distance));
        // Get a specific path
        List<Integer> path = graph.getPath(0, 5);
        System.out.println("\nShortest path from 0 to 5: " + path);
    }
}

// Node class to represent vertices and their weights
record Node(int vertex, int weight) {}
