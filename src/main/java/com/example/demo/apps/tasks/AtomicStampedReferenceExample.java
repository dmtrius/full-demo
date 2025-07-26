package com.example.demo.apps.tasks;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.CountDownLatch;

public class AtomicStampedReferenceExample {

    // Bank account example to demonstrate ABA problem prevention
    @Getter
    static class BankAccount {
        private final String accountNumber;
        private final double balance;

        public BankAccount(String accountNumber, double balance) {
            this.accountNumber = accountNumber;
            this.balance = balance;
        }

        @Override
        public String toString() {
            return "Account[" + accountNumber + ", $" + balance + "]";
        }
    }

    // AtomicStampedReference to prevent ABA problem
    private static AtomicStampedReference<BankAccount> accountRef;

    @SuppressWarnings("unused")
    public static void main(String[] args) throws InterruptedException {
        // Initial account with stamp 0
        BankAccount initialAccount = new BankAccount("ACC-001", 1000.0);
        accountRef = new AtomicStampedReference<>(initialAccount, 0);

        System.out.println("=== AtomicStampedReference Example ===\n");

        // Example 1: Basic usage
        basicUsageExample();

        // Example 2: ABA problem demonstration
        abaProblemExample();

        // Example 3: Concurrent updates
        concurrentUpdatesExample();
    }

    private static void basicUsageExample() {
        System.out.println("1. Basic Usage:");

        // Get current reference and stamp
        int[] stampHolder = new int[1];
        BankAccount current = accountRef.get(stampHolder);
        int currentStamp = stampHolder[0];

        System.out.println("Current: " + current + ", Stamp: " + currentStamp);

        // Update with new stamp
        BankAccount newAccount = new BankAccount("ACC-001", 1200.0);
        boolean updated = accountRef.compareAndSet(current, newAccount, currentStamp, currentStamp + 1);

        System.out.println("Update successful: " + updated);
        System.out.println("New state: " + accountRef.getReference() + ", Stamp: " + accountRef.getStamp());
        System.out.println();
    }

    private static void abaProblemExample() {
        System.out.println("2. ABA Problem Prevention:");

        // Reset for demonstration
        BankAccount account1000 = new BankAccount("ACC-002", 1000.0);
        accountRef = new AtomicStampedReference<>(account1000, 0);

        // Thread 1n reads initial state
        int[] stampHolder1 = new int[1];
        BankAccount initialRead = accountRef.get(stampHolder1);
        int initialStamp = stampHolder1[0];
        System.out.println("Thread 1 reads: " + initialRead + ", Stamp: " + initialStamp);

        // Meanwhile, Thread 2 makes changes: 1000 -> 1500 -> 1000
        BankAccount account1500 = new BankAccount("ACC-002", 1500.0);
        accountRef.compareAndSet(account1000, account1500, 0, 1);
        System.out.println("Thread 2 changes to: " + accountRef.getReference() + ", Stamp: " + accountRef.getStamp());

        BankAccount account1000Again = new BankAccount("ACC-002", 1000.0);
        accountRef.compareAndSet(account1500, account1000Again, 1, 2);
        System.out.println("Thread 2 changes back to: " + accountRef.getReference() + ", Stamp: " + accountRef.getStamp());

        // Thread 1 tries to update based on old read - this will fail due to stamp mismatch
        BankAccount account800 = new BankAccount("ACC-002", 800.0);
        boolean success = accountRef.compareAndSet(initialRead, account800, initialStamp, initialStamp + 1);
        System.out.println("Thread 1 update attempt (should fail): " + success);
        System.out.println("Final state: " + accountRef.getReference() + ", Stamp: " + accountRef.getStamp());
        System.out.println();
    }

    private static void concurrentUpdatesExample() throws InterruptedException {
        System.out.println("3. Concurrent Updates:");

        // Reset for demonstration
        BankAccount initialAccount = new BankAccount("ACC-003", 500.0);
        accountRef = new AtomicStampedReference<>(initialAccount, 0);

        int numThreads = 5;
        CountDownLatch latch = new CountDownLatch(numThreads);

        // Create multiple threads trying to deposit money
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    // Each thread tries to add $100
                    for (int attempt = 0; attempt < 3; attempt++) {
                        int[] stampHolder = new int[1];
                        BankAccount current = accountRef.get(stampHolder);
                        int currentStamp = stampHolder[0];

                        BankAccount newAccount = new BankAccount(
                                current.getAccountNumber(),
                                current.getBalance() + 100.0
                        );

                        if (accountRef.compareAndSet(current, newAccount, currentStamp, currentStamp + 1)) {
                            System.out.println("Thread " + threadId + " successfully deposited $100. " +
                                    "New balance: $" + newAccount.getBalance() +
                                    ", Stamp: " + (currentStamp + 1));
                            break;
                        } else {
                            System.out.println("Thread " + threadId + " failed attempt " + (attempt + 1) +
                                    " - retrying...");
                            Thread.sleep(10); // Small delay before retry
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        System.out.println("Final account state: " + accountRef.getReference() +
                ", Final stamp: " + accountRef.getStamp());
    }
}
