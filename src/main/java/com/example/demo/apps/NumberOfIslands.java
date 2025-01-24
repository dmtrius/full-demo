package com.example.demo.apps;

import java.util.LinkedList;
import java.util.Queue;

public class NumberOfIslands {

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int numRows = grid.length;
        int numCols = grid[0].length;
        int numIslands = 0;

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (grid[r][c] == '1') {
                    numIslands++;
                    bfs(grid, r, c);
//                    dfs(grid, r, c);
                }
            }
        }

        return numIslands;
    }

    private void dfs(char[][] grid, int r, int c) {
        int numRows = grid.length;
        int numCols = grid[0].length;

        if (r < 0 || c < 0 || r >= numRows || c >= numCols || grid[r][c] == '0') {
            return;
        }

        grid[r][c] = '0'; // Mark the cell as visited

        // Visit all adjacent cells
        dfs(grid, r - 1, c); // Up
        dfs(grid, r + 1, c); // Down
        dfs(grid, r, c - 1); // Left
        dfs(grid, r, c + 1); // Right
    }

    private void bfs(char[][] grid, int startRow, int startCol) {
        int numRows = grid.length;
        int numCols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startRow, startCol});
        grid[startRow][startCol] = '0'; // Mark the starting cell as visited

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int row = cell[0];
            int col = cell[1];

            for (int[] direction : directions) {
                int newRow = row + direction[0];
                int newCol = col + direction[1];

                if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols && grid[newRow][newCol] == '1') {
                    queue.add(new int[]{newRow, newCol});
                    grid[newRow][newCol] = '0'; // Mark the cell as visited
                }
            }
        }
    }

    public static void main(String[] args) {
        char[][] grid = {
                {'1', '1', '1', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}
        };

        NumberOfIslands solution = new NumberOfIslands();
        int numIslands = solution.numIslands(grid);
        System.out.println("Number of islands: " + numIslands);
    }
}
