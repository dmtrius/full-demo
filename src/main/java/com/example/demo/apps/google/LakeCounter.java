package com.example.demo.apps.google;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class LakeCounter {
    private static final char WATER = '.';
    private static final char LAND = 'x';

    private static final int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, 1}, {1, 1}, {1, -1}, {-1, -1}
    };

    // Main function to count lakes in an island
    public static int countLakes(char[][] image, int[] coord) {
        // Validate input
        if (image == null || coord == null ||
                coord[0] < 0 || coord[0] >= image[0].length ||
                coord[1] < 0 || coord[1] >= image.length ||
                image[coord[1]][coord[0]] != LAND) {
            return 0;
        }

        // Find the entire island
        Set<int[]> islandCells = findIsland(image, coord);

        // Find all water cells within the island
        Set<int[]> waterCells = findInternalWater(image, islandCells);

        return waterCells.size();
    }

    // Find all cells that are part of the same island
    private static Set<int[]> findIsland(char[][] image, int[] startCoord) {
        Set<int[]> islandCells = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();

        queue.offer(startCoord);
        islandCells.add(startCoord);

        while (!queue.isEmpty()) {
            int[] current = queue.poll();

            for (int[] dir : directions) {
                int[] next = {current[0] + dir[0], current[1] + dir[1]};

                // Check if next is valid and part of the island
                if (isValidIslandCell(image, next) && !islandCells.contains(next)) {
                    queue.offer(next);
                    islandCells.add(next);
                }
            }
        }

        return islandCells;
    }

    // Find water cells that are internal to the island
    private static Set<int[]> findInternalWater(char[][] image, Set<int[]> islandCells) {
        Set<int[]> internalWater = new HashSet<>();
        Set<int[]> islandBorder = new HashSet<>();
    
        // Identify border cells of the island
        for (int[] cell : islandCells) {
            for (int[] dir : directions) {
                int[] neighbor = {cell[0] + dir[0], cell[1] + dir[1]};
                if (!islandCells.contains(neighbor)) {
                    islandBorder.add(neighbor);
                }
            }
        }
    
        // Find water cells within the island
        for (int[] cell : islandCells) {
            if (image[cell[1]][cell[0]] == WATER && !isAnyNeighborOutsideIsland(cell, islandBorder)) {
                internalWater.add(cell);
            }
        }
    
        return internalWater;
    }

    // Check if a cell is a valid part of the island
    private static boolean isValidIslandCell(char[][] image, int[] coord) {
        return coord[0] >= 0 && coord[0] < image[0].length &&
                coord[1] >= 0 && coord[1] < image.length &&
                image[coord[1]][coord[0]] == LAND;
    }

    // Check if any neighbor of a water cell is outside the island
    private static boolean isAnyNeighborOutsideIsland(int[] cell, Set<int[]> islandBorder) {
        for (int[] dir : directions) {
            int[] neighbor = {cell[0] + dir[0], cell[1] + dir[1]};
            if (islandBorder.contains(neighbor)) {
                return true;
            }
        }
        return false;
    }

    // Helper method to create image from string representation
    public static char[][] createImage(String[] imageRows) {
        char[][] image = new char[imageRows.length][imageRows[0].length()];
        for (int i = 0; i < imageRows.length; i++) {
            image[i] = imageRows[i].toCharArray();
        }
        return image;
    }

    // Main method to demonstrate usage
    public static void main(String[] args) {
//        String[] imageRows = {
//                "...x......x...",
//                "...xxxxx.x.x..",
//                "....xxx..x....",
//                "....xxxxxx....",
//                "......xxx.x.x.",
//                ".....xxxxxxx..",
//                ".....xxx....x.",
//                "............x."
//        };
//        String[] imageRows = {
//                "...|x|...|...|x.|",
//                "...|xxxxx|.|x.|x|",
//                "...|xxx|.|.|x|.|",
//                "......|xxxxxx|.|.|",
//                ".....|xxx|.|x|.|x|",
//                ".....|x|x|xxxx|x|.|",
//                ".....|xxx|......|x|.|",
//                ".....|......|.|x|.|"
//        };
        String[] imageRows = {
                "|.|.|.|x|.|.|.|.|.|.|.|.|.|.|.|.|.|.|x|.|",
                "|.|.|.|.|x|x|x|x|x|x|.|.|.|.|.|.|.|x|.|x|",
                "|.|.|x|x|x|.|.|.|.|.|x|.|.|.|.|.|.|.|x|.|",
                "|.|.|.|.|.|.|.|.|.|.|.|x|x|x|x|x|x|.|.|.|",
                "|.|.|.|.|.|x|x|x|.|.|.|x|.|x|.|.|x|.|.|.|",
                "|.|.|.|.|.|x|.|x|.|.|.|x|x|x|x|x|x|.|.|.|",
                "|.|.|.|.|.|x|x|x|.|.|.|.|.|.|.|.|x|.|.|.|",
                "|.|.|.|.|.|.|.|.|.|.|.|.|.|.|.|.|x|.|.|.|"
        };
        for (int i = 0; i < imageRows.length; i++) {
            System.out.println(imageRows[i]);
        }
        System.out.println();
        for (int i = 0; i < imageRows.length; i++) {
            imageRows[i] = imageRows[i].replace("|","");
            System.out.println(imageRows[i]);
        }

        char[][] image = createImage(imageRows);

        // Test cases
        System.out.println("Lakes at (2,2): " + countLakes(image, new int[]{2, 2})); // 0
        System.out.println("Lakes at (6,6): " + countLakes(image, new int[]{6, 6})); // 1
        System.out.println("Lakes at (12,5): " + countLakes(image, new int[]{12, 5})); // 2
    }
}
