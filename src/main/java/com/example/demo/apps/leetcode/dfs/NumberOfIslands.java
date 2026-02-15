package com.example.demo.apps.leetcode.dfs;

import static java.lang.IO.println;

/**
 * DFS (<a href="https://leetcode.com/problems/number-of-islands/">200</a>)
 * <a href="https://algo.monster/liteproblems/200">200</a>
 * <h2>Number of Islands</h2>
 * Given an m x n 2D binary grid grid which represents a map of '1's (land) and '0's (water), return the number of islands.
 * <p>
 * An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.
 * <p>
 * <p>
 * <p>
 * Example 1:
 * <pre>
 * Input: grid = [
 * ["1","1","1","1","0"],
 * ["1","1","0","1","0"],
 * ["1","1","0","0","0"],
 * ["0","0","0","0","0"]
 * ]
 * Output: 1
 * </pre>
 * <p>Example 2:
 * <pre>
 * Input: grid = [
 * ["1","1","0","0","0"],
 * ["1","1","0","0","0"],
 * ["0","0","1","0","0"],
 * ["0","0","0","1","1"]
 * ]
 * Output: 3
 * </pre>
 */
public class NumberOfIslands {
    void main() {
        String[][] grid = {
                {"1", "1", "0", "0", "0"},
                {"1", "1", "0", "0", "0"},
                {"0", "0", "1", "0", "0"},
                {"0", "0", "0", "1", "1"}
        };
        println(numIslands(string2DToChar2D(grid)));
    }

    int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int rows = grid.length;
        int cols = grid[0].length;
        int count = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    dfs(grid, i, j);
                }
            }
        }
        return count;
    }

    private void dfs(char[][] grid, int i, int j) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Base cases
        if (i < 0 || i >= rows || j < 0 || j >= cols || grid[i][j] != '1') {
            return;
        }

        // Mark as visited
        grid[i][j] = '0';

        // Explore all 4 directions
        dfs(grid, i - 1, j); // up
        dfs(grid, i + 1, j); // down
        dfs(grid, i, j - 1); // left
        dfs(grid, i, j + 1); // right
    }

    char[][] string2DToChar2D(String[][] strGrid) {
        if (strGrid == null || strGrid.length == 0) {
            return new char[0][0];
        }

        int rows = strGrid.length;
        int cols = strGrid[0].length;
        char[][] charGrid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Convert each String to char array, take first char (for islands: '1'/'0')
                if (strGrid[i][j] != null && !strGrid[i][j].isEmpty()) {
                    charGrid[i][j] = strGrid[i][j].charAt(0);
                } else {
                    charGrid[i][j] = '0'; // Default for empty/null
                }
            }
        }
        return charGrid;
    }
}
