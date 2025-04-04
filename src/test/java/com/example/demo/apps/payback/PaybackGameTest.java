package com.example.demo.apps.payback;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaybackGameTest {

    @Test
    void testInitialization() {
        PaybackGame game = new PaybackGame();
        Map<String, Integer> pointsMap = game.calculatePoints();

        // Ensure all coupons start with exactly one Pointee
        assertEquals(225, pointsMap.values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void testPlayRound() {
        PaybackGame game = new PaybackGame();

        // Play one round and ensure total Pointees remain constant
        game.playRound();
        Map<String, Integer> pointsMap = game.calculatePoints();

        assertEquals(225, pointsMap.values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void testFindHighestCoupons() {
        PaybackGame game = new PaybackGame();

        // Simulate a scenario where one coupon has maximum Pointees
        Map<String, Integer> pointsMap = game.calculatePoints();

        String expectedCouponFormat = "Coupon";
        assertTrue(game.findHighestCoupons(pointsMap).get(0).contains(expectedCouponFormat));
    }
}
