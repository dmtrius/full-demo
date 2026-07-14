package com.example.demo.apps.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Cell {
    int parentI;
    int parentJ;
    double f;
    double g;
    double h;

    Cell() {
        this.parentI = 0;
        this.parentJ = 0;
        this.f = 0;
        this.g = 0;
        this.h = 0;
    }
}

public class AStar {

    private static final int ROW = 9;
    private static final int COL = 10;
    private static final String SOURCE_OR_DESTINATION_IS_INVALID = "Source or destination is invalid";
    private static final String SOURCE_OR_THE_DESTINATION_IS_BLOCKED = "Source or the destination is blocked";
    private static final String WE_ARE_ALREADY_AT_THE_DESTINATION = "We are already at the destination";
    private static final String THE_DESTINATION_CELL_IS_FOUND = "The destination cell is found";

    void main() {
        // Description of the Grid-
        // 1 -> The cell is not blocked
        // 0 -> The cell is blocked
        int[][] grid = {{1, 0, 1, 1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 0, 1, 1, 1, 0, 1, 1},
            {1, 1, 1, 0, 1, 1, 0, 1, 0, 1},
            {0, 0, 1, 0, 1, 0, 0, 0, 0, 1},
            {1, 1, 1, 0, 1, 1, 1, 0, 1, 0},
            {1, 0, 1, 1, 1, 1, 0, 1, 0, 0},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 0, 0, 0, 1, 0, 0, 1}};

        // The source is the left-most bottom-most corner
        int[] src = {8, 0};

        // Destination is the left-most top-most corner
        int[] dest = {0, 0};

        aStarSearch(grid, src, dest);
    }

    private static boolean isValid(int row, int col) {
        return (row >= 0) && (row < ROW) && (col >= 0)
            && (col < COL);
    }

    private static boolean isUnBlocked(int[][] grid, int row, int col) {
        return grid[row][col] == 1;
    }

    private static boolean isDestination(int row, int col, int[] dest) {
        return row == dest[0] && col == dest[1];
    }

    private static double calculateHValue(int row, int col, int[] dest) {
        return Math.sqrt((row - dest[0]) * (row - dest[0])
            + ((double)col - dest[1])
            * (col - dest[1]));
    }

    private static void tracePath(Cell[][] cellDetails, int[] dest) {
        IO.println("The Path is ");
        int row = dest[0];
        int col = dest[1];

        Map<int[], Boolean> path = new LinkedHashMap<>();

        while (!(cellDetails[row][col].parentI == row && cellDetails[row][col].parentJ == col)) {
            path.put(new int[]{row, col}, true);
            int tempRow = cellDetails[row][col].parentI;
            int tempCol = cellDetails[row][col].parentJ;
            row = tempRow;
            col = tempCol;
        }

        path.put(new int[]{row, col}, true);
        List<int[]> pathList
            = new ArrayList<>(path.keySet());
        pathList = pathList.reversed();

        pathList.forEach(p -> IO.print("-> (" + p[0] + ", " + (p[1]) + ")"));
        IO.println();
    }

    private static void aStarSearch(int[][] grid, int[] src, int[] dest) {
        if (!isValid(src[0], src[1]) || !isValid(dest[0], dest[1])) {
            IO.println(SOURCE_OR_DESTINATION_IS_INVALID);
            return;
        }

        if (!isUnBlocked(grid, src[0], src[1]) || !isUnBlocked(grid, dest[0], dest[1])) {
            IO.println(SOURCE_OR_THE_DESTINATION_IS_BLOCKED);
            return;
        }

        if (isDestination(src[0], src[1], dest)) {
            IO.println(WE_ARE_ALREADY_AT_THE_DESTINATION);
            return;
        }

        boolean[][] closedList = new boolean[ROW][COL];
        Cell[][] cellDetails = new Cell[ROW][COL];

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                cellDetails[i][j] = new Cell();
                cellDetails[i][j].f
                    = Double.POSITIVE_INFINITY;
                cellDetails[i][j].g
                    = Double.POSITIVE_INFINITY;
                cellDetails[i][j].h
                    = Double.POSITIVE_INFINITY;
                cellDetails[i][j].parentI = -1;
                cellDetails[i][j].parentJ = -1;
            }
        }

        int i = src[0];
        int j = src[1];
        cellDetails[i][j].f = 0;
        cellDetails[i][j].g = 0;
        cellDetails[i][j].h = 0;
        cellDetails[i][j].parentI = i;
        cellDetails[i][j].parentJ = j;

        Map<Double, int[]> openList = new HashMap<>();
        openList.put(0.0, new int[]{i, j});

        boolean foundDest = false;

        while (!openList.isEmpty()) {
            Map.Entry<Double, int[]> p
                = openList.entrySet().iterator().next();
            for (Map.Entry<Double, int[]> q : openList.entrySet()) {
                if (q.getKey() < p.getKey()) {
                    p = q;
                }
            }

            openList.remove(p.getKey());

            i = p.getValue()[0];
            j = p.getValue()[1];
            closedList[i][j] = true;

            double gNew;
            double hNew;
            double fNew;

            // 1st Successor (North)
            if (isValid(i - 1, j)) {
                if (isDestination(i - 1, j, dest)) {
                    cellDetails[i - 1][j].parentI = i;
                    cellDetails[i - 1][j].parentJ = j;
                    IO.println(THE_DESTINATION_CELL_IS_FOUND);
                    tracePath(cellDetails, dest);
                    return;
                } else if (!closedList[i - 1][j] && isUnBlocked(grid, i - 1, j)) {
                    gNew = cellDetails[i][j].g + 1;
                    hNew = calculateHValue(i - 1, j, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i - 1][j].f == Double.POSITIVE_INFINITY || cellDetails[i - 1][j].f > fNew) {
                        openList.put(fNew, new int[]{i - 1, j});

                        cellDetails[i - 1][j].f = fNew;
                        cellDetails[i - 1][j].g = gNew;
                        cellDetails[i - 1][j].h = hNew;
                        cellDetails[i - 1][j].parentI = i;
                        cellDetails[i - 1][j].parentJ = j;
                    }
                }
            }

            // 2nd Successor (South)
            if (isValid(i + 1, j)) {
                if (isDestination(i + 1, j, dest)) {
                    cellDetails[i + 1][j].parentI = i;
                    cellDetails[i + 1][j].parentJ = j;
                    IO.println(THE_DESTINATION_CELL_IS_FOUND);
                    tracePath(cellDetails, dest);
                    return;
                } else if (!closedList[i + 1][j] && isUnBlocked(grid, i + 1, j)) {
                    gNew = cellDetails[i][j].g + 1;
                    hNew = calculateHValue(i + 1, j, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i + 1][j].f == Double.POSITIVE_INFINITY || cellDetails[i + 1][j].f > fNew) {
                        openList.put(
                            fNew, new int[]{i + 1, j});

                        cellDetails[i + 1][j].f = fNew;
                        cellDetails[i + 1][j].g = gNew;
                        cellDetails[i + 1][j].h = hNew;
                        cellDetails[i + 1][j].parentI = i;
                        cellDetails[i + 1][j].parentJ = j;
                    }
                }
            }

            // 3rd Successor (East)
            if (isValid(i, j + 1)) {
                if (isDestination(i, j + 1, dest)) {
                    cellDetails[i][j + 1].parentI = i;
                    cellDetails[i][j + 1].parentJ = j;
                    IO.println(THE_DESTINATION_CELL_IS_FOUND);
                    tracePath(cellDetails, dest);
                    return;
                } else if (!closedList[i][j + 1] && isUnBlocked(grid, i, j + 1)) {
                    gNew = cellDetails[i][j].g + 1;
                    hNew = calculateHValue(i, j + 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i][j + 1].f == Double.POSITIVE_INFINITY || cellDetails[i][j + 1].f > fNew) {
                        openList.put(
                            fNew, new int[]{i, j + 1});

                        cellDetails[i][j + 1].f = fNew;
                        cellDetails[i][j + 1].g = gNew;
                        cellDetails[i][j + 1].h = hNew;
                        cellDetails[i][j + 1].parentI = i;
                        cellDetails[i][j + 1].parentJ = j;
                    }
                }
            }

            // 4th Successor (West)
            if (isValid(i, j - 1)) {
                if (isDestination(i, j - 1, dest)) {
                    cellDetails[i][j - 1].parentI = i;
                    cellDetails[i][j - 1].parentJ = j;
                    IO.println(THE_DESTINATION_CELL_IS_FOUND);
                    tracePath(cellDetails, dest);
                    return;
                } else if (!closedList[i][j - 1] && isUnBlocked(grid, i, j - 1)) {
                    gNew = cellDetails[i][j].g + 1;
                    hNew = calculateHValue(i, j - 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i][j - 1].f == Double.POSITIVE_INFINITY || cellDetails[i][j - 1].f > fNew) {
                        openList.put(fNew, new int[]{i, j - 1});

                        cellDetails[i][j - 1].f = fNew;
                        cellDetails[i][j - 1].g = gNew;
                        cellDetails[i][j - 1].h = hNew;
                        cellDetails[i][j - 1].parentI = i;
                        cellDetails[i][j - 1].parentJ = j;
                    }
                }
            }

            // 5th Successor (North-East)
            if (isValid(i - 1, j + 1)) {
                if (isDestination(i - 1, j + 1, dest)) {
                    cellDetails[i - 1][j + 1].parentI = i;
                    cellDetails[i - 1][j + 1].parentJ = j;
                    IO.println(THE_DESTINATION_CELL_IS_FOUND);
                    tracePath(cellDetails, dest);
                    return;
                } else if (!closedList[i - 1][j + 1] && isUnBlocked(grid, i - 1, j + 1)) {
                    gNew = cellDetails[i][j].g + 1.414;
                    hNew = calculateHValue(i - 1, j + 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i - 1][j + 1].f == Double.POSITIVE_INFINITY || cellDetails[i - 1][j + 1].f > fNew) {
                        openList.put(fNew, new int[]{i - 1, j + 1});

                        cellDetails[i - 1][j + 1].f = fNew;
                        cellDetails[i - 1][j + 1].g = gNew;
                        cellDetails[i - 1][j + 1].h = hNew;
                        cellDetails[i - 1][j + 1].parentI = i;
                        cellDetails[i - 1][j + 1].parentJ = j;
                    }
                }
            }

            // 6th Successor (North-West)
            if (isValid(i - 1, j - 1)) {
                if (isDestination(i - 1, j - 1, dest)) {
                    cellDetails[i - 1][j - 1].parentI = i;
                    cellDetails[i - 1][j - 1].parentJ = j;
                    IO.println(THE_DESTINATION_CELL_IS_FOUND);
                    tracePath(cellDetails, dest);
                    return;
                } else if (!closedList[i - 1][j - 1] && isUnBlocked(grid, i - 1, j - 1)) {
                    gNew = cellDetails[i][j].g + 1.414;
                    hNew = calculateHValue(i - 1, j - 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i - 1][j - 1].f == Double.POSITIVE_INFINITY || cellDetails[i - 1][j - 1].f > fNew) {
                        openList.put(fNew, new int[]{i - 1, j - 1});

                        cellDetails[i - 1][j - 1].f = fNew;
                        cellDetails[i - 1][j - 1].g = gNew;
                        cellDetails[i - 1][j - 1].h = hNew;
                        cellDetails[i - 1][j - 1].parentI = i;
                        cellDetails[i - 1][j - 1].parentJ = j;
                    }
                }
            }

            // 7th Successor (South-East)
            if (isValid(i + 1, j + 1)) {
                if (isDestination(i + 1, j + 1, dest)) {
                    cellDetails[i + 1][j + 1].parentI = i;
                    cellDetails[i + 1][j + 1].parentJ = j;
                    IO.println(THE_DESTINATION_CELL_IS_FOUND);
                    tracePath(cellDetails, dest);
                    return;
                } else if (!closedList[i + 1][j + 1] && isUnBlocked(grid, i + 1, j + 1)) {
                    gNew = cellDetails[i][j].g + 1.414;
                    hNew = calculateHValue(i + 1, j + 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i + 1][j + 1].f == Double.POSITIVE_INFINITY || cellDetails[i + 1][j + 1].f > fNew) {
                        openList.put(fNew, new int[]{i + 1, j + 1});

                        cellDetails[i + 1][j + 1].f = fNew;
                        cellDetails[i + 1][j + 1].g = gNew;
                        cellDetails[i + 1][j + 1].h = hNew;
                        cellDetails[i + 1][j + 1].parentI
                            = i;
                        cellDetails[i + 1][j + 1].parentJ
                            = j;
                    }
                }
            }

            // 8th Successor (South-West)
            if (isValid(i + 1, j - 1)) {
                if (isDestination(i + 1, j - 1, dest)) {
                    cellDetails[i + 1][j - 1].parentI = i;
                    cellDetails[i + 1][j - 1].parentJ = j;
                    IO.println(THE_DESTINATION_CELL_IS_FOUND);
                    tracePath(cellDetails, dest);
                    return;
                } else if (!closedList[i + 1][j - 1] && isUnBlocked(grid, i + 1, j - 1)) {
                    gNew = cellDetails[i][j].g + 1.414;
                    hNew = calculateHValue(i + 1, j - 1, dest);
                    fNew = gNew + hNew;

                    if (cellDetails[i + 1][j - 1].f == Double.POSITIVE_INFINITY || cellDetails[i + 1][j - 1].f > fNew) {
                        openList.put(fNew, new int[]{i + 1, j - 1});

                        cellDetails[i + 1][j - 1].f = fNew;
                        cellDetails[i + 1][j - 1].g = gNew;
                        cellDetails[i + 1][j - 1].h = hNew;
                        cellDetails[i + 1][j - 1].parentI = i;
                        cellDetails[i + 1][j - 1].parentJ = j;
                    }
                }
            }
        }

        if (!foundDest)
            IO.println("Failed to find the destination cell");
    }
}
