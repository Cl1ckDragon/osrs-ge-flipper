package com.osrsflip.util;

import org.springframework.stereotype.Component;

@Component
public class FlipScoreCalculator {

    public long calculateScore(int margin, int buyLimit) {
        return (long) margin * buyLimit;
    }

    // Applies 1% GE tax: seller receives 99% of the high price.
    public long calculatePotentialProfit(int margin, int buyLimit) {
        return Math.round((long) margin * buyLimit * 0.99);
    }
}
