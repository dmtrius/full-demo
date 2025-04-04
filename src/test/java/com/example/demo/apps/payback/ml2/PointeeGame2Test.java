package com.example.demo.apps.payback.ml2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PointeeGame2Test {

    @Test
    public void testInitialization() {
        PointeeGame game = new PointeeGame();
        Assertions.assertEquals(0, game.getCurrentRound());

        Map<String, Integer> values = game.getCouponValues();
        Assertions.assertEquals(225, values.size()); // 15x15 = 225

        // All coupons should have 1 Pointee initially
        for (int value : values.values()) {
            Assertions.assertEquals(1, value);
        }
    }

    @Test
    public void testOneRoundSimulation() {
        PointeeGame game = new PointeeGame();
        game.simulateRounds(1);
        Assertions.assertEquals(1, game.getCurrentRound());

        Map<String, Integer> values = game.getCouponValues();

        // After one round, center squares should have more Pointees
        int centerValue = values.get("(8,8)");
        Assertions.assertTrue(centerValue > 1);

        // Edge squares should have fewer Pointees
        int edgeValue = values.get("(1,1)");
        Assertions.assertTrue(edgeValue >= 1 && edgeValue <= 2);
    }

    @Test
    public void testEdgeMovement() {
        // Test that Pointees don't move off the board
        PointeeGame game = new PointeeGame();
        game.simulateRounds(1);

        Map<String, Integer> values = game.getCouponValues();

        // Top-left corner (1,1) should only have possible moves to (1,2) and (2,1)
        // So after one round, it should have between 0 and 2 Pointees
        int cornerValue = values.get("(1,1)");
        assertTrue(cornerValue >= 0 && cornerValue <= 2);
    }

    @Test
    public void testSaveAndLoad() throws Exception {
        PointeeGame original = new PointeeGame();
        original.simulateRounds(10);

        String filename = "test_save.sav";
        original.saveGame(filename);

        PointeeGame loaded = PointeeGame.loadGame(filename);
        Assertions.assertEquals(original.getCurrentRound(), loaded.getCurrentRound());

        Map<String, Integer> originalValues = original.getCouponValues();
        Map<String, Integer> loadedValues = loaded.getCouponValues();

        Assertions.assertEquals(originalValues, loadedValues);

        // Clean up
        new java.io.File(filename).delete();
    }
}
