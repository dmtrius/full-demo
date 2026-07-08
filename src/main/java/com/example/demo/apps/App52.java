package com.example.demo.apps;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public class App52 {

    void main() {
        IO.println("App52: Java 26 features demo");
    }

    public static final String TRUSTED = "TRUSTED";
    private static final String FLAG_UNUSUAL_AMOUNT = "Unusual Amount";
    private static final String FLAG_UNUSUAL_GEO = "Unusual Geo";
    private static final int LOWER_BOUND = 33;
    private static final int UPPER_BOUND = 66;
    private static final int DELTA = 33;
    private static final BigDecimal Z_SCORE_THRESHOLD = BigDecimal.valueOf(3);
    private static final Duration VELOCITY_WINDOW = Duration.ofMinutes(2);
    private static final int MAX_TX_IN_WINDOW = 5;

    @SuppressWarnings("unused")
    private RiskAssessment assessRisk(final Transaction tx, final CustomerProfile profile) {
        boolean trusted = isTrusted(tx);
        int delta = trusted ? -1 * DELTA : DELTA;

        List<String> flags = RULES.stream()
                .filter(rule -> rule.predicate().test(tx, profile))
                .map(RiskRule::flag)
                .toList();

        int score = (trusted ? 100 : 0) + flags.size() * delta;

        return assessmentCalc(score, flags);
    }

    private final List<RiskRule> RULES = List.of(
            new RiskRule(this::isUnusualAmount, FLAG_UNUSUAL_AMOUNT),
            new RiskRule(this::isUnusualGeo, FLAG_UNUSUAL_GEO)
    );

    private RiskAssessment assessmentCalc(int score, List<String> flags) {
        int category = Integer.compare(Math.max(LOWER_BOUND, Math.min(score, UPPER_BOUND)), score);
        return switch (category) {
            case -1 -> new RiskAssessment(score, flags, Action.BLOCK);
            case 1 -> new RiskAssessment(score, flags, Action.ALLOW);
            default -> new RiskAssessment(score, flags, Action.REVIEW);
        };
    }

    private boolean isTrusted(Transaction tx) {
        return TRUSTED.equals(tx.merchantCategory());
    }

    private boolean isUnusualGeo(Transaction tx, CustomerProfile profile) {
        return !profile.knownCountries().contains(tx.country());
    }

    private boolean isUnusualAmount(Transaction tx, CustomerProfile profile) {
        BigDecimal threshold = profile.avgTransactionAmount().add(profile.stdDevAmount());
        return tx.amount().compareTo(threshold) > 0;
    }

    @SuppressWarnings("unused")
    private boolean isUnusualAmount2(
            Transaction tx,
            CustomerProfile profile) {

        BigDecimal stdDev = profile.stdDevAmount();

        // No variance in historical data -> compare directly to average
        if (stdDev == null || stdDev.signum() == 0) {
            return tx.amount().compareTo(
                    profile.avgTransactionAmount().multiply(BigDecimal.valueOf(2))
            ) > 0;
        }

        BigDecimal deviation =
                tx.amount().subtract(profile.avgTransactionAmount()).abs();

        BigDecimal threshold =
                stdDev.multiply(Z_SCORE_THRESHOLD);

        return deviation.compareTo(threshold) > 0;
    }

    @SuppressWarnings("unused")
    private boolean isUnusualAmount3(
            Transaction tx,
            CustomerProfile profile) {

        List<Transaction> recent = profile.recentTransactions();

        if (recent.isEmpty()) {
            return false;
        }

        BigDecimal avg = recent.stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(recent.size()),
                        RoundingMode.HALF_UP);

        return tx.amount().compareTo(avg.multiply(BigDecimal.valueOf(3))) > 0;
    }

    @SuppressWarnings("unused")
    private boolean velocityFraud(Transaction tx, CustomerProfile profile) {

        Instant now = tx.timestamp();

        long txCountInWindow = profile.recentTransactions().stream()
                .filter(t -> !t.timestamp().isBefore(now.minus(VELOCITY_WINDOW)))
                .filter(t -> t.timestamp().isBefore(now)) // only past events
                .count();

        return txCountInWindow >= MAX_TX_IN_WINDOW;
    }

    @SuppressWarnings("unused")
    private boolean velocityFraud2(Transaction tx, CustomerProfile profile) {

        Instant now = tx.timestamp();

        double score = profile.recentTransactions().stream()
                .filter(t -> !t.timestamp().isBefore(now.minus(VELOCITY_WINDOW)))
                .mapToDouble(t -> {
                    long secondsAgo = Duration.between(t.timestamp(), now).toSeconds();
                    return 1.0 / (1 + secondsAgo); // newer = higher weight
                })
                .sum();

        return score > 3.0; // tuned threshold
    }

    private record RiskRule(
            BiPredicate<Transaction, CustomerProfile> predicate,
            String flag) {
    }

    // Our banking platform needs to flag potentially fraudulent transactions in real-time. Implement a function that evaluates a transaction against a customer's recent history and returns a risk score. The function should consider: unusual amounts, geographic anomalies, and velocity (too many transactions in short time).
    private record Transaction(String id, String customerId, BigDecimal amount, String merchantCategory,
                               String country, Instant timestamp) {
    }

    private record CustomerProfile(BigDecimal avgTransactionAmount, BigDecimal stdDevAmount,
                                   Set<String> knownCountries, List<Transaction> recentTransactions
    ) {
    }

    private record RiskAssessment(int score,          // 0 - 100
                                  List<String> flags, // reasons for score
                                  Action recommended  // ALLOW, REVIEW, BLOCK
    ) {
    }

    private enum Action {
        ALLOW,
        REVIEW,
        BLOCK
    }
}
