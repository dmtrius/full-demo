package com.example.demo.apps.payback;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaybackGame {
    private static final int BOARD_SIZE = 15;
    private static final int TOTAL_ROUNDS = 100;
    private int[][] board;

    public PaybackGame() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        // Initialize each square with one Pointee
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 1;
            }
        }
    }

    // Simulate one game round
    public void playRound() {
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int pointees = board[i][j];
                for (int k = 0; k < pointees; k++) {
                    // Randomly move each Pointee to an adjacent square
                    int newRow = Math.max(0, Math.min(BOARD_SIZE - 1, i + (int) (Math.random() * 3) - 1));
                    int newCol = Math.max(0, Math.min(BOARD_SIZE - 1, j + (int) (Math.random() * 3) - 1));
                    newBoard[newRow][newCol]++;
                }
            }
        }
        board = newBoard;
    }

    // Get points for each coupon
    public Map<String, Integer> calculatePoints() {
        Map<String, Integer> pointsMap = new HashMap<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                pointsMap.put("Coupon (" + (i + 1) + "," + (j + 1) + ")", board[i][j]);
            }
        }
        return pointsMap;
    }

    // Find coupons with the highest points
    public List<String> findHighestCoupons(Map<String, Integer> pointsMap) {
        int maxPoints = Collections.max(pointsMap.values());
        List<String> highestCoupons = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : pointsMap.entrySet()) {
            if (entry.getValue() == maxPoints) {
                highestCoupons.add(entry.getKey());
            }
        }
        return highestCoupons;
    }

    public static void main(String[] args) {
        PaybackGame game = new PaybackGame();
        int[] checkRounds = {25, 50, TOTAL_ROUNDS};

        for (int round = 1; round <= TOTAL_ROUNDS; round++) {
            game.playRound();
            final int finalRound = round;
            if (Arrays.stream(checkRounds).anyMatch(r -> r == finalRound)) {
                System.out.println("After " + round + " rounds:");
                Map<String, Integer> pointsMap = game.calculatePoints();
                List<String> highestCoupons = game.findHighestCoupons(pointsMap);

                System.out.println("Coupon Points: " + pointsMap);
                System.out.println("Highest Coupons: " + highestCoupons);
                System.out.println();
            }
        }
    }
}
