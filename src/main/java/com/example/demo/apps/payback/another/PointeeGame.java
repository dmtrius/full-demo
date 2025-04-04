package com.example.demo.apps.payback.another;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointeeGame {
    private static final int BOARD_SIZE = 15;
    private List<Pointee> pointees;
    private int[][] couponPoints;

    public PointeeGame() {
        initializeBoard();
    }

    /**
     * Initialize the board with Pointees on each square.
     */
    private void initializeBoard() {
        pointees = new ArrayList<>();
        couponPoints = new int[BOARD_SIZE][BOARD_SIZE];

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                pointees.add(new Pointee(row, col));
            }
        }
    }

    /**
     * Simulate a round where a bird flies over and Pointees jump.
     */
    public void playRound() {
        List<Pointee> newPositions = new ArrayList<>();

        for (Pointee pointee : pointees) {
            int currentRow = pointee.getRow();
            int currentCol = pointee.getCol();

            // Possible moves: up, down, left, right
            int[][] moves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            // Randomly select a move
            int[] move = moves[new Random().nextInt(moves.length)];

            int newRow = currentRow + move[0];
            int newCol = currentCol + move[1];

            // Ensure Pointee stays on the board
            if (newRow >= 0 && newRow < BOARD_SIZE &&
                    newCol >= 0 && newCol < BOARD_SIZE) {
                pointee.setPosition(newRow, newCol);
                newPositions.add(pointee);
            } else {
                // If move would take Pointee off board, stay in place
                newPositions.add(pointee);
            }
        }

        pointees = newPositions;
        updateCouponPoints();
    }

    /**
     * Update points for each coupon based on Pointee positions.
     */
    private void updateCouponPoints() {
        // Reset points
        couponPoints = new int[BOARD_SIZE][BOARD_SIZE];

        // Count Pointees on each square
        for (Pointee pointee : pointees) {
            couponPoints[pointee.getRow()][pointee.getCol()]++;
        }
    }

    /**
     * Get points for a specific coupon.
     * @param row Row of the coupon
     * @param col Column of the coupon
     * @return Points for the coupon
     */
    public int getCouponPoints(int row, int col) {
        return couponPoints[row][col];
    }

    /**
     * Find coupons with the highest points.
     * @return List of coordinates with highest points
     */
    public List<int[]> getHighestPointCoupons() {
        int maxPoints = 0;
        List<int[]> highestCoupons = new ArrayList<>();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (couponPoints[row][col] > maxPoints) {
                    maxPoints = couponPoints[row][col];
                    highestCoupons.clear();
                    highestCoupons.add(new int[]{row, col});
                } else if (couponPoints[row][col] == maxPoints) {
                    highestCoupons.add(new int[]{row, col});
                }
            }
        }

        return highestCoupons;
    }

    /**
     * Simulate game rounds and return results.
     * @param rounds Number of rounds to play
     * @return GameResult containing coupon points and highest point coupons
     */
    public GameResult playGame(int rounds) {
        for (int i = 0; i < rounds; i++) {
            playRound();
        }

        return new GameResult(couponPoints, getHighestPointCoupons());
    }
}