package com.example.demo.apps.leetcode.matrix_traversal;

import java.util.Arrays;

import static com.example.demo.apps.leetcode.Utils.printMatrix;
import static java.lang.IO.println;

/**
 * Matrix Traversal (<a href="https://leetcode.com/problems/flood-fill/">733</a>)
 * <a href="https://algo.monster/liteproblems/733">733</a>
 * <h2>Flood Fill</h2>
 * You are given an image represented by an m x n grid of integers image, where image[i][j] represents the pixel value of the image. You are also given three integers sr, sc, and color. Your task is to perform a flood fill on the image starting from the pixel image[sr][sc].
 * <p>
 * To perform a flood fill:
 * <p>
 * Begin with the starting pixel and change its color to color.
 * Perform the same process for each pixel that is directly adjacent (pixels that share a side with the original pixel, either horizontally or vertically) and shares the same color as the starting pixel.
 * Keep repeating this process by checking neighboring pixels of the updated pixels and modifying their color if it matches the original color of the starting pixel.
 * The process stops when there are no more adjacent pixels of the original color to update.
 * Return the modified image after performing the flood fill.
 * <p>
 * Example 1:
 * <p>
 * Input: image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
 * <p>
 * Output: [[2,2,2],[2,2,0],[2,0,1]]
 * <p>
 * Explanation:
 * <p>
 * From the center of the image with position (sr, sc) = (1, 1) (i.e., the red pixel), all pixels connected by a path of the same color as the starting pixel (i.e., the blue pixels) are colored with the new color.
 * <p>
 * Note the bottom corner is not colored 2, because it is not horizontally or vertically connected to the starting pixel.
 * <p>
 * Example 2:
 * <p>
 * Input: image = [[0,0,0],[0,0,0]], sr = 0, sc = 0, color = 0
 * <p>
 * Output: [[0,0,0],[0,0,0]]
 * <p>
 * Explanation:
 * <p>
 * The starting pixel is already colored with 0, which is the same as the target color. Therefore, no changes are made to the image.
 */
public class FloodFill {
    void main() {
        int [][] image = {{1, 1, 1},{1, 1, 0},{1, 0, 1}};
        printMatrix(image);
        println();
        int sr = 1, sc = 1, color = 2;
        var result = floodFill(image, sr, sc, color);
        printMatrix(result);
    }

    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        // Get the original color at starting position
        int originalColor = image[sr][sc];

        // If the new color is same as original, no need to do anything
        if (originalColor == color) {
            return image;
        }

        // Perform DFS to fill connected pixels
        dfs(image, sr, sc, originalColor, color);

        return image;
    }

    private void dfs(int[][] image, int row, int col, int originalColor, int newColor) {
        // Check boundaries and if current pixel has the original color
        if (row < 0 || row >= image.length ||
                col < 0 || col >= image[0].length ||
                image[row][col] != originalColor) {
            return;
        }

        // Change the color
        image[row][col] = newColor;

        // Recursively fill in all 4 directions (up, down, left, right)
        dfs(image, row - 1, col, originalColor, newColor); // up
        dfs(image, row + 1, col, originalColor, newColor); // down
        dfs(image, row, col - 1, originalColor, newColor); // left
        dfs(image, row, col + 1, originalColor, newColor); // right
    }
}
