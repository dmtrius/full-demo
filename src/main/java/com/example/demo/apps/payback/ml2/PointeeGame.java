package com.example.demo.apps.payback.ml2;

import java.util.*;
import java.io.*;

public class PointeeGame {
    private int[][] board;
    private int currentRound;
    private final int size = 15;

    public PointeeGame() {
        this.board = new int[size][size];
        // Initialize with one Pointee per square
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 1;
            }
        }
        this.currentRound = 0;
    }

    // For loading saved game
    public PointeeGame(int[][] board, int currentRound) {
        this.board = deepCopy(board);
        this.currentRound = currentRound;
    }

    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    public void simulateRounds(int rounds) {
        for (int r = 0; r < rounds; r++) {
            simulateOneRound();
            currentRound++;
        }
    }

    private void simulateOneRound() {
        int[][] newBoard = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] > 0) {
                    distributePointees(i, j, board[i][j], newBoard);
                }
            }
        }

        board = newBoard;
    }

    private void distributePointees(int row, int col, int count, int[][] newBoard) {
        List<int[]> possibleMoves = getPossibleMoves(row, col);
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int[] move = possibleMoves.get(random.nextInt(possibleMoves.size()));
            newBoard[move[0]][move[1]]++;
        }
    }

    private List<int[]> getPossibleMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();

        // Up
        if (row > 0) moves.add(new int[]{row - 1, col});
        // Down
        if (row < size - 1) moves.add(new int[]{row + 1, col});
        // Left
        if (col > 0) moves.add(new int[]{row, col - 1});
        // Right
        if (col < size - 1) moves.add(new int[]{row, col + 1});

        return moves;
    }

    public Map<String, Integer> getCouponValues() {
        Map<String, Integer> values = new HashMap<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                values.put(getCoordinateKey(i, j), board[i][j]);
            }
        }
        return values;
    }

    public List<String> getHighestValueCoupons() {
        int maxValue = 0;
        List<String> maxCoupons = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] > maxValue) {
                    maxValue = board[i][j];
                    maxCoupons.clear();
                    maxCoupons.add(getCoordinateKey(i, j));
                } else if (board[i][j] == maxValue) {
                    maxCoupons.add(getCoordinateKey(i, j));
                }
            }
        }

        return maxCoupons;
    }

    private String getCoordinateKey(int row, int col) {
        return String.format("(%d,%d)", row + 1, col + 1);
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void saveGame(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(board);
            oos.writeInt(currentRound);
        }
    }

    public static PointeeGame loadGame(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            int[][] board = (int[][]) ois.readObject();
            int currentRound = ois.readInt();
            return new PointeeGame(board, currentRound);
        }
    }
}
