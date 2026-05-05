package com.osrsflip.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlipScoreCalculatorTest {

    private final FlipScoreCalculator calculator = new FlipScoreCalculator();

    // --- calculateScore ---

    @Test
    void calculateScore_returnsMarginTimesVolume() {
        assertThat(calculator.calculateScore(1_000, 100)).isEqualTo(100_000L);
    }

    @Test
    void calculateScore_withZeroMargin_returnsZero() {
        assertThat(calculator.calculateScore(0, 500)).isEqualTo(0L);
    }

    @Test
    void calculateScore_withZeroBuyLimit_returnsZero() {
        assertThat(calculator.calculateScore(5_000, 0)).isEqualTo(0L);
    }

    @Test
    void calculateScore_withLargeValues_doesNotOverflow() {
        // 500_000 margin × 70_000 buy limit = 35B, exceeds Integer.MAX_VALUE (~2.1B)
        assertThat(calculator.calculateScore(500_000, 70_000)).isEqualTo(35_000_000_000L);
    }

    // --- calculatePotentialProfit ---

    @Test
    void calculatePotentialProfit_appliesOnePercentGeTax() {
        // 1_000 margin × 100 buy limit × 0.99 = 99_000
        assertThat(calculator.calculatePotentialProfit(1_000, 100)).isEqualTo(99_000L);
    }

    @Test
    void calculatePotentialProfit_withZeroBuyLimit_returnsZero() {
        assertThat(calculator.calculatePotentialProfit(5_000, 0)).isEqualTo(0L);
    }

    @Test
    void calculatePotentialProfit_isLessThanRawScore() {
        long raw = calculator.calculateScore(10_000, 50);
        long profit = calculator.calculatePotentialProfit(10_000, 50);
        assertThat(profit).isLessThan(raw);
    }
}
