package com.example.demo.apps.algo;

import lombok.Getter;

import java.util.*;

import static java.lang.IO.println;

public class TopologicalSort {

    // Class to represent a task
    @Getter
    static class Task {
        private final String name;
        private final Set<Task> dependencies;

        public Task(String name) {
            this.name = name;
            this.dependencies = new HashSet<>();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Task task = (Task) o;
            return Objects.equals(name, task.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Method 1: Kahn's Algorithm (using in-degree)
    private List<Task> topologicalSortKahn(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return new ArrayList<>();
        }

        // Build an adjacency list and calculate in-degrees
        Map<Task, List<Task>> graph = new HashMap<>();
        Map<Task, Integer> inDegree = new HashMap<>();

        // Initialize all tasks
        for (Task task : tasks) {
            graph.put(task, new ArrayList<>());
            inDegree.put(task, 0);
        }

        // Build graph and calculate in-degrees
        for (Task task : tasks) {
            for (Task dependency : task.getDependencies()) {
                // Check if the dependency exists in our task list
                if (graph.containsKey(dependency)) {
                    graph.get(dependency).add(task);
                    inDegree.put(task, inDegree.get(task) + 1);
                }
            }
        }

        // Find all nodes with in-degree 0
        Queue<Task> queue = new LinkedList<>();
        for (Task task : tasks) {
            if (inDegree.get(task) == 0) {
                queue.offer(task);
            }
        }

        List<Task> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            Task current = queue.poll();
            result.add(current);

            // Reduce in-degree of neighbors
            for (Task neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // Check for cycle
        if (result.size() != tasks.size()) {
            throw new IllegalArgumentException("Graph contains a cycle, topological sort not possible");
        }

        return result;
    }

    // Method 2: DFS-based topological sort
    private List<Task> topologicalSortDFS(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Task, List<Task>> graph = buildGraph(tasks);
        Set<Task> visited = new HashSet<>();
        Set<Task> visiting = new HashSet<>(); // For cycle detection
        Stack<Task> stack = new Stack<>();

        for (Task task : tasks) {
            if (!visited.contains(task)) {
                if (!dfs(task, graph, visited, visiting, stack)) {
                    throw new IllegalArgumentException("Graph contains a cycle, topological sort not possible");
                }
            }
        }

        // Convert stack to list (reverse order)
        List<Task> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        return result;
    }

    private boolean dfs(Task task, Map<Task, List<Task>> graph,
                        Set<Task> visited, Set<Task> visiting, Stack<Task> stack) {
        if (visiting.contains(task)) {
            return false; // Cycle detected
        }

        if (visited.contains(task)) {
            return true;
        }

        visiting.add(task);

        for (Task neighbor : graph.getOrDefault(task, Collections.emptyList())) {
            if (!dfs(neighbor, graph, visited, visiting, stack)) {
                return false;
            }
        }

        visiting.remove(task);
        visited.add(task);
        stack.push(task);

        return true;
    }

    // Helper method to build an adjacency list
    private Map<Task, List<Task>> buildGraph(List<Task> tasks) {
        Map<Task, List<Task>> graph = new HashMap<>();

        // Initialize all tasks
        for (Task task : tasks) {
            graph.put(task, new ArrayList<>());
        }

        // Build graph
        for (Task task : tasks) {
            for (Task dependency : task.getDependencies()) {
                if (graph.containsKey(dependency)) {
                    graph.get(dependency).add(task);
                }
            }
        }

        return graph;
    }

    // Utility method to add dependency between tasks
    private void addDependency(Task task, Task dependency) {
        task.getDependencies().add(dependency);
    }

    // Test the implementation
    void main() {
        TopologicalSort sorter = new TopologicalSort();

        // Create tasks
        Task taskA = new Task("A");
        Task taskB = new Task("B");
        Task taskC = new Task("C");
        Task taskD = new Task("D");
        Task taskE = new Task("E");

        // Set-up dependencies
        // A depends on B and C
        // B depends on D
        // C depends on D
        // E depends on A and C
        sorter.addDependency(taskA, taskB);
        sorter.addDependency(taskA, taskC);
        sorter.addDependency(taskB, taskD);
        sorter.addDependency(taskC, taskD);
        sorter.addDependency(taskE, taskA);
        sorter.addDependency(taskE, taskC);

        List<Task> tasks = Arrays.asList(taskA, taskB, taskC, taskD, taskE);

        try {
            println("Using Kahn's Algorithm:");
            List<Task> result1 = sorter.topologicalSortKahn(tasks);
            println(result1);

            println("\nUsing DFS Algorithm:");
            List<Task> result2 = sorter.topologicalSortDFS(tasks);
            println(result2);

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Test with cyclic dependency
        println("\nTesting with cyclic dependency:");
        Task taskX = new Task("X");
        Task taskY = new Task("Y");
        Task taskZ = new Task("Z");

        sorter.addDependency(taskX, taskY);
        sorter.addDependency(taskY, taskZ);
        sorter.addDependency(taskZ, taskX); // Creates cycle

        List<Task> cyclicTasks = Arrays.asList(taskX, taskY, taskZ);

        try {
            List<Task> result = sorter.topologicalSortKahn(cyclicTasks);
            println(result);
        } catch (IllegalArgumentException e) {
            println("Detected cycle: " + e.getMessage());
        }

        println(">>> tsTEST");
        tsTest();
    }

    void tsTest() {
        TopologicalSort sorter = new TopologicalSort();

        // Create tasks
        Task compile = new Task("Compile");
        Task test = new Task("Test");
        Task deploy = new Task("Deploy");

        // Set dependencies
        sorter.addDependency(test, compile);
        sorter.addDependency(deploy, test);

        List<Task> tasks = Arrays.asList(compile, test, deploy);
        println(tasks);
        List<Task> executionOrder = sorter.topologicalSortKahn(tasks);
        println(executionOrder);
    }
}