package com.example.demo.apps.payback.another;

import java.util.List;

public class GameResult {
    private final int[][] couponPoints;
    private final List<int[]> highestPointCoupons;

    public GameResult(int[][] couponPoints, List<int[]> highestPointCoupons) {
        this.couponPoints = couponPoints;
        this.highestPointCoupons = highestPointCoupons;
    }

    public int[][] getCouponPoints() {
        return couponPoints;
    }

    public List<int[]> getHighestPointCoupons() {
        return highestPointCoupons;
    }
}
