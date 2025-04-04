package com.example.demo.apps.payback;

import com.example.demo.apps.payback.another.GameResult;
import com.example.demo.apps.payback.another.PointeeGame;

public class PointeeGameTest {
    public static void main(String[] args) {
        // Test the game mechanics
        PointeeGame game = new PointeeGame();

        // Test rounds 25, 50, and 100
        int[] testRounds = {25, 50, 100};

        for (int rounds : testRounds) {
            System.out.println("Game Results after " + rounds + " rounds:");
            GameResult result = game.playGame(rounds);

            System.out.println("Highest Point Coupons:");
            for (int[] coupon : result.getHighestPointCoupons()) {
                System.out.printf("Coupon [%d, %d] - Points: %d%n",
                        coupon[0], coupon[1],
                        result.getCouponPoints()[coupon[0]][coupon[1]]);
            }
            System.out.println();
        }
    }
}
