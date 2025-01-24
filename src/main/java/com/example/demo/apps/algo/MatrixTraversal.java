package com.example.demo.apps.algo;

import java.util.*;

public class MatrixTraversal {
    // Directions for traversal: right, down, left, up
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    // Diagonal directions
    private static final int[][] DIAGONAL_DIRECTIONS = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    /**
     * DFS traversal using recursion
     */
    public static void dfsRecursive(int[][] matrix, int row, int col, boolean[][] visited) {
        // Check bounds and if already visited
        if (!isValid(matrix, row, col) || visited[row][col]) {
            return;
        }

        // Process current cell
        System.out.print(matrix[row][col] + " ");
        visited[row][col] = true;

        // Recursively visit all adjacent cells
        for (int[] dir : DIRECTIONS) {
            dfsRecursive(matrix, row + dir[0], col + dir[1], visited);
        }
    }

    /**
     * DFS traversal using stack
     */
    public static void dfsIterative(int[][] matrix, int startRow, int startCol) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Stack<int[]> stack = new Stack<>();

        // Add starting position
        stack.push(new int[]{startRow, startCol});

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int row = current[0];
            int col = current[1];

            // Skip if invalid or visited
            if (!isValid(matrix, row, col) || visited[row][col]) {
                continue;
            }

            // Process current cell
            System.out.print(matrix[row][col] + " ");
            visited[row][col] = true;

            // Add all adjacent cells to stack
            for (int[] dir : DIRECTIONS) {
                stack.push(new int[]{row + dir[0], col + dir[1]});
            }
        }
    }

    /**
     * BFS traversal using queue
     */
    public static void bfs(int[][] matrix, int startRow, int startCol) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();

        // Add starting position
        queue.offer(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            // Process current cell
            System.out.print(matrix[row][col] + " ");

            // Add all unvisited adjacent cells to queue
            for (int[] dir : DIRECTIONS) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isValid(matrix, newRow, newCol) && !visited[newRow][newCol]) {
                    queue.offer(new int[]{newRow, newCol});
                    visited[newRow][newCol] = true;
                }
            }
        }
    }

    /**
     * Spiral traversal
     */
    public static void spiralTraversal(int[][] matrix) {
        if (matrix == null || matrix.length == 0) return;

        int rows = matrix.length;
        int cols = matrix[0].length;
        int top = 0, bottom = rows - 1;
        int left = 0, right = cols - 1;

        while (top <= bottom && left <= right) {
            // Traverse right
            for (int j = left; j <= right; j++) {
                System.out.print(matrix[top][j] + " ");
            }
            top++;

            // Traverse down
            for (int i = top; i <= bottom; i++) {
                System.out.print(matrix[i][right] + " ");
            }
            right--;

            if (top <= bottom) {
                // Traverse left
                for (int j = right; j >= left; j--) {
                    System.out.print(matrix[bottom][j] + " ");
                }
                bottom--;
            }

            if (left <= right) {
                // Traverse up
                for (int i = bottom; i >= top; i--) {
                    System.out.print(matrix[i][left] + " ");
                }
                left++;
            }
        }
    }

    /**
     * Diagonal traversal
     */
    public static void diagonalTraversal(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        // Traverse all diagonals starting from the first row
        for (int col = 0; col < cols; col++) {
            int i = 0, j = col;
            while (i < rows && j < cols) {
                System.out.print(matrix[i][j] + " ");
                i++;
                j++;
            }
            System.out.println();
        }

        // Traverse remaining diagonals starting from the first column
        for (int row = 1; row < rows; row++) {
            int i = row, j = 0;
            while (i < rows && j < cols) {
                System.out.print(matrix[i][j] + " ");
                i++;
                j++;
            }
            System.out.println();
        }
    }

    /**
     * Boundary traversal
     */
    public static void boundaryTraversal(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        // Print the first row
        for (int j = 0; j < cols; j++) {
            System.out.print(matrix[0][j] + " ");
        }

        // Print last column
        for (int i = 1; i < rows; i++) {
            System.out.print(matrix[i][cols-1] + " ");
        }

        if (rows > 1) {
            // Print last row
            for (int j = cols-2; j >= 0; j--) {
                System.out.print(matrix[rows-1][j] + " ");
            }
        }

        if (cols > 1) {
            // Print first column
            for (int i = rows-2; i > 0; i--) {
                System.out.print(matrix[i][0] + " ");
            }
        }
    }

    /**
     * Helper method to check if position is valid
     */
    private static boolean isValid(int[][] matrix, int row, int col) {
        return row >= 0 && row < matrix.length && col >= 0 && col < matrix[0].length;
    }

    // Example usage
    public static void main(String[] args) {
        int[][] matrix = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };

        System.out.println("DFS Recursive:");
        dfsRecursive(matrix, 0, 0, new boolean[matrix.length][matrix[0].length]);

        System.out.println("\n\nDFS Iterative:");
        dfsIterative(matrix, 0, 0);

        System.out.println("\n\nBFS:");
        bfs(matrix, 0, 0);

        System.out.println("\n\nSpiral Traversal:");
        spiralTraversal(matrix);

        System.out.println("\n\nDiagonal Traversal:");
        diagonalTraversal(matrix);

        System.out.println("\nBoundary Traversal:");
        boundaryTraversal(matrix);
    }
}