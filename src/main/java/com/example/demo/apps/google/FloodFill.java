package com.example.demo.apps.google;

public class FloodFill {

    // Function to perform flood fill
    public static void floodFill(int[][] screen, int x, int y, int newColor) {
        int prevColor = screen[x][y];
        if (prevColor == newColor) return; // No need to fill if colors are same
        floodFillUtil(screen, x, y, prevColor, newColor);
    }

    // Utility function for flood fill
    private static void floodFillUtil(int[][] screen, int x, int y, int prevColor, int newColor) {
        // Base cases
        if (x < 0 || x >= screen.length || y < 0 || y >= screen[0].length) return; // Out of bounds
        if (screen[x][y] != prevColor) return; // Not matching target color

        // Replace the color at (x, y)
        screen[x][y] = newColor;

        // Recur for north, south, east and west
        floodFillUtil(screen, x + 1, y, prevColor, newColor); // South
        floodFillUtil(screen, x - 1, y, prevColor, newColor); // North
        floodFillUtil(screen, x, y + 1, prevColor, newColor); // East
        floodFillUtil(screen, x, y - 1, prevColor, newColor); // West
    }

    public static void main(String[] args) {
        int[][] screen = {
                {1, 1, 1, 1},
                {1, 1, 0, 0},
                {1, 0, 0, 0},
                {0, 0, 2, 2}
        };

        int x = 1; // Starting x-coordinate
        int y = 1; // Starting y-coordinate
        int newColor = 3; // New color to fill

        floodFill(screen, x, y, newColor);

        System.out.println("Updated screen after call to floodFill:");
        for (int[] row : screen) {
            for (int col : row) {
                System.out.print(col + " ");
            }
            System.out.println();
        }
    }
}
