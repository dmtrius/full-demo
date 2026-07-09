package com.example.demo.apps;

import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;

@Log4j2
public class App52 {

    void main() {
        log.info("App52: Java 26 features demo");
        List<Transaction> transactions = generateTransactions();
        CustomerProfile profile = generateCustomerProfile();
        IO.println(transactions);
        IO.println(profile);

        transactions.forEach(tx -> {
            IO.println(tx);
            var risk = assessRisk(tx, profile);
            IO.println(risk);
        });
    }

    private static final String TRUSTED = "TRUSTED";
    private static final String UNTRUSTED = "UNTRUSTED";
    private static final String FLAG_UNUSUAL_AMOUNT = "Unusual Amount";
    private static final String FLAG_UNUSUAL_GEO = "Unusual Geo";
    private static final String FLAG_VELOCITY_FRAUD = "Velocity Fraud";
    private static final int LOWER_BOUND = 33;
    private static final int UPPER_BOUND = 68;
    private static final int DELTA = 33;
    private static final int MAX_TX_IN_WINDOW = 5;
    private static final BigDecimal Z_SCORE_THRESHOLD = BigDecimal.valueOf(3);
    private static final Duration VELOCITY_WINDOW = Duration.ofMinutes(2);

    @SuppressWarnings("unused")
    private RiskAssessment assessRisk(final Transaction tx, final CustomerProfile profile) {
        boolean trusted = isTrusted(tx, profile);
        int delta = trusted ? -DELTA : DELTA;

        var flags = rules.stream()
            .filter(rule -> !rule.predicate().test(tx, profile))
            .map(RiskRule::flag)
            .toList();

        int score = (trusted ? 100 : 0) + flags.size() * delta;

        score = Math.clamp(score, 0, 100);

        return assessmentCalc(score, flags);
    }

    private final List<RiskRule> rules = List.of(
        new RiskRule(this::isTrusted, TRUSTED),
        new RiskRule(this::isVelocityFraud2, FLAG_VELOCITY_FRAUD),
        new RiskRule(this::isUnusualAmount3, FLAG_UNUSUAL_AMOUNT),
        new RiskRule(this::isUnusualGeo, FLAG_UNUSUAL_GEO)
    );

    private RiskAssessment assessmentCalc(int score, List<String> flags) {
        int category = Integer.compare(Math.clamp(score, LOWER_BOUND, UPPER_BOUND), score);
        return switch (category) {
            case -1 -> new RiskAssessment(score, flags, Action.BLOCK);
            case 1 -> new RiskAssessment(score, flags, Action.ALLOW);
            default -> new RiskAssessment(score, flags, Action.REVIEW);
        };
    }

    private boolean isTrusted(@NonNull Transaction tx, @NonNull CustomerProfile profile) {
        return TRUSTED.equals(tx.merchantCategory());
    }

    private boolean isUnusualGeo(@NonNull Transaction tx, @NonNull CustomerProfile profile) {
        return !profile.knownCountries().contains(tx.country());
    }

    @SuppressWarnings("unused")
    private boolean isUnusualAmount(@NonNull Transaction tx, @NonNull CustomerProfile profile) {
        BigDecimal threshold = profile.avgTransactionAmount().add(profile.stdDevAmount());
        return tx.amount().compareTo(threshold) > 0;
    }

    @SuppressWarnings("unused")
    private boolean isUnusualAmount2(@NonNull Transaction tx, @NonNull CustomerProfile profile) {

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
    private boolean isUnusualAmount3(@NonNull Transaction tx, @NonNull CustomerProfile profile) {

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
    private boolean isVelocityFraud(@NonNull Transaction tx, @NonNull CustomerProfile profile) {

        Instant now = tx.timestamp();

        long txCountInWindow = profile.recentTransactions().stream()
            .filter(t -> !t.timestamp().isBefore(now.minus(VELOCITY_WINDOW)))
            .filter(t -> t.timestamp().isBefore(now)) // only past events
            .count();

        return txCountInWindow >= MAX_TX_IN_WINDOW;
    }

    @SuppressWarnings("unused")
    private boolean isVelocityFraud2(@NonNull Transaction tx, @NonNull CustomerProfile profile) {

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
        BiPredicate<Transaction, CustomerProfile> predicate, String flag) {
    }

    // Our banking platform needs to flag potentially fraudulent transactions in real-time. Implement a function that evaluates a transaction against a customer's recent history and returns a risk score. The function should consider: unusual amounts, geographic anomalies, and velocity (too many transactions in short time).
    private record Transaction(String id, String customerId, BigDecimal amount,
                               String merchantCategory, String country, Instant timestamp) {
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

    private static final String CUST_123 = "cust123";
    private static final String US = "US";
    private static final String CA = "CA";
    private static final String UK = "UK";
    private static final String CN = "CN";
    private static final Set<String> knownCountries = Set.of(US, CA, UK);

    private static final Random rand = new Random(1000L);

    private String getIsTrusted() {
        return rand.nextBoolean() ? TRUSTED : UNTRUSTED;
    }

    private String getTxId() {
        return UUID.randomUUID().toString();
    }

    private CustomerProfile generateCustomerProfile() {
        // Generate recent transactions (last 30 days)
        List<Transaction> recentTransactions = List.of(
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(150.00), getIsTrusted(), US, Instant.now().minus(Duration.ofDays(5))),
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(200.50), getIsTrusted(), US, Instant.now().minus(Duration.ofDays(4))),
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(175.25), getIsTrusted(), US, Instant.now().minus(Duration.ofDays(3))),
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(160.00), getIsTrusted(), US, Instant.now().minus(Duration.ofDays(2))),
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(190.75), getIsTrusted(), CA, Instant.now().minus(Duration.ofDays(1)))
        );

        // Calculate average transaction amount
        BigDecimal sum = recentTransactions.stream()
            .map(Transaction::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgAmount = sum.divide(BigDecimal.valueOf(recentTransactions.size()), RoundingMode.HALF_UP);

        // Calculate standard deviation
        BigDecimal variance = recentTransactions.stream()
            .map(tx -> tx.amount().subtract(avgAmount).pow(2))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(recentTransactions.size()), RoundingMode.HALF_UP);
        BigDecimal stdDev = BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));

        return new CustomerProfile(avgAmount, stdDev, knownCountries, recentTransactions);
    }

    private List<Transaction> generateTransactions() {
        Instant now = Instant.now();

        return List.of(
            // Normal transactions
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(150.00), getIsTrusted(), US, now.minus(Duration.ofDays(5))),
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(200.50), getIsTrusted(), CA, now.minus(Duration.ofDays(4))),
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(175.25), getIsTrusted(), UK, now.minus(Duration.ofDays(3))),
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(160.00), getIsTrusted(), US, now.minus(Duration.ofDays(2))),

            // Unusual amount - high
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(5000.00), getIsTrusted(), US, now.minus(Duration.ofDays(1))),

            // Unusual geography
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(190.75), getIsTrusted(), CN, now.minus(Duration.ofMinutes(30))),

            // Velocity fraud - multiple transactions in short time window
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(100.00), getIsTrusted(), US, now.minus(Duration.ofMinutes(5))),
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(120.00), getIsTrusted(), UK, now.minus(Duration.ofMinutes(3))),
            new Transaction(getTxId(), CUST_123, BigDecimal.valueOf(110.00), getIsTrusted(), CA, now.minus(Duration.ofMinutes(1)))
        );
    }
}
