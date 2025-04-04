package com.example.demo.apps.payback.ml2;

import java.io.IOException;
import java.util.*;

public class PAYBACKGameApp {
    public static void main(String[] args) {
        PointeeGame game;

        try {
            // Try to load saved game
            game = PointeeGame.loadGame("payback_game.sav");
            System.out.println("Resumed game from round " + game.getCurrentRound());
        } catch (Exception e) {
            // Start new game if no saved game exists
            game = new PointeeGame();
            System.out.println("Started new game");
        }

        // Simulate up to 100 rounds
        int targetRound = 100;
        int roundsToSimulate = targetRound - game.getCurrentRound();

        if (roundsToSimulate > 0) {
            System.out.println("Simulating " + roundsToSimulate + " rounds...");
            game.simulateRounds(roundsToSimulate);

            try {
                game.saveGame("payback_game.sav");
                System.out.println("Game saved after round " + game.getCurrentRound());
            } catch (IOException e) {
                System.err.println("Failed to save game: " + e.getMessage());
            }
        }

        // Check coupon values at specific rounds
        checkCouponValues(game, 25);
        checkCouponValues(game, 50);
        checkCouponValues(game, 100);
    }

    private static void checkCouponValues(PointeeGame game, int round) {
        if (game.getCurrentRound() >= round) {
            System.out.println("\nAfter " + round + " rounds:");

            // Get highest value coupons
            List<String> highestCoupons = game.getHighestValueCoupons();
            Map<String, Integer> values = game.getCouponValues();

            System.out.println("Highest value coupons (" + values.get(highestCoupons.get(0)) + " points):");
            for (String coupon : highestCoupons) {
                System.out.println("  " + coupon);
            }

            // Print all coupon values (commented out for brevity)
            // System.out.println("\nAll coupon values:");
            // values.forEach((k, v) -> System.out.println(k + ": " + v));
        }
    }
}
