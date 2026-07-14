package com.example.demo.apps.tasks;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.CountDownLatch;

/**
 * Demonstrates the use of {@link AtomicStampedReference} to solve the ABA problem
 * in concurrent programming. The ABA problem occurs when a thread is about to
 * perform a compare-and-set (CAS) operation, but the value it first read (A) has been
 * changed to another value (B) and then back to (A) by another thread. A simple
 * CAS would succeed, unaware that the underlying state has actually changed.
 *
 * <p>This example uses a simple BankAccount scenario to illustrate the concepts.
 */
public class AtomicStampedReferenceExample {

    private static final String ACC_001 = "ACC-001";
    private static final String ACC_002 = "ACC-002";
    private static final String ACC_003 = "ACC-003";

    /**
     * A simple, immutable representation of a bank account.
     * Being immutable is a good practice for objects managed by atomic references.
     */
    record BankAccount(String accountNumber, double balance) {

        @Override
        public @NonNull String toString() {
            return "Account[" + accountNumber + ", $" + balance + "]";
        }
    }

    /**
     * The AtomicStampedReference holds a reference to a BankAccount object and an integer "stamp".
     * Both the reference and the stamp are updated atomically.
     */
    private static AtomicStampedReference<BankAccount> accountRef;

    static void main() throws InterruptedException {
        // Initialize the account reference with an initial account and a starting stamp of 0.
        BankAccount initialAccount = new BankAccount(ACC_001, 1000.0);
        accountRef = new AtomicStampedReference<>(initialAccount, 0);

        IO.println("=== AtomicStampedReference Example ===\n");

        // Run the different demonstration scenarios.
        basicUsageExample();
        abaProblemExample();
        concurrentUpdatesExample();
    }

    /**
     * Demonstrates the basic API usage of AtomicStampedReference:
     * getting the current reference and stamp, and performing a successful update.
     */
    private static void basicUsageExample() {
        IO.println("1. Basic Usage:");

        // To get both the reference and the stamp, we pass an array to hold the stamp.
        int[] stampHolder = new int[1];
        BankAccount current = accountRef.get(stampHolder);
        int currentStamp = stampHolder[0];

        IO.println("Current: %s, Stamp: %d".formatted(current, currentStamp));

        // Create a new account state.
        BankAccount newAccount = new BankAccount(ACC_001, 1200.0);
        // Atomically set the new value if the current value and stamp match the expected ones.
        // The stamp is incremented on successful update to mark a new version.
        boolean updated = accountRef.compareAndSet(current, newAccount, currentStamp, currentStamp + 1);

        IO.println("Update successful: %b".formatted(updated));
        IO.println("New state: %s, Stamp: %d".formatted(accountRef.getReference(), accountRef.getStamp()));
        IO.println();
    }

    /**
     * Simulates the ABA problem and shows how AtomicStampedReference prevents it.
     * A thread reads a value (A), another thread changes it (A -> B -> A), and the first
     * thread's update fails because the stamp has changed, even though the value is the same.
     */
    private static void abaProblemExample() {
        System.out.println("2. ABA Problem Prevention:");

        // Reset the account reference for this specific demonstration.
        BankAccount account1000 = new BankAccount(ACC_002, 1000.0);
        accountRef = new AtomicStampedReference<>(account1000, 0);

        // --- Thread 1's perspective ---
        // It reads the initial state (Value=A, Stamp=0).
        int[] stampHolder1 = new int[1];
        BankAccount initialRead = accountRef.get(stampHolder1);
        int initialStamp = stampHolder1[0];
        IO.println("Thread 1 reads: %s, Stamp: %d".formatted(initialRead, initialStamp));

        // --- Intervening operations by another thread (Thread 2) ---
        // 1. Change A -> B: The balance changes from 1000 to 1500. Stamp becomes 1.
        BankAccount account1500 = new BankAccount(ACC_002, 1500.0);
        accountRef.compareAndSet(account1000, account1500, 0, 1);
        IO.println("Thread 2 changes to: %s, Stamp: %d".formatted(accountRef.getReference(), accountRef.getStamp()));

        // 2. Change B -> A: The balance changes back from 1500 to 1000. Stamp becomes 2.
        BankAccount account1000Again = new BankAccount(ACC_002, 1000.0);
        accountRef.compareAndSet(account1500, account1000Again, 1, 2);
        IO.println("Thread 2 changes back to: %s, Stamp: %d".formatted(accountRef.getReference(), accountRef.getStamp()));

        // --- Thread 1 attempts its update ---
        // It tries to update the account based on its initial read (Value=A, Stamp=0).
        // The CAS will fail because the current stamp is now 2, not 0.
        BankAccount account800 = new BankAccount(ACC_002, 800.0);
        boolean success = accountRef.compareAndSet(initialRead, account800, initialStamp, initialStamp + 1);
        IO.println("Thread 1 update attempt (should fail): %b".formatted(success));
        IO.println("Final state: %s, Stamp: %d".formatted(accountRef.getReference(), accountRef.getStamp()));
        IO.println();
    }

    /**
     * Demonstrates multiple threads concurrently trying to update the same AtomicStampedReference.
     * This shows a common pattern of "optimistic locking" where threads retry in a loop if their
     * CAS operation fails due to a concurrent modification by another thread.
     *
     * @throws InterruptedException if the main thread is interrupted while waiting for worker threads.
     */
    private static void concurrentUpdatesExample() throws InterruptedException {
        IO.println("3. Concurrent Updates:");

        // Reset the account reference for this demonstration.
        BankAccount initialAccount = new BankAccount(ACC_003, 500.0);
        accountRef = new AtomicStampedReference<>(initialAccount, 0);

        int numThreads = 5;
        // Use a CountDownLatch to make the main thread wait for all worker threads to complete.
        CountDownLatch latch = new CountDownLatch(numThreads);

        // Create and start multiple threads that will all try to deposit money.
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    // Each thread will attempt to deposit $100. It will retry up to 3 times.
                    for (int attempt = 0; attempt < 3; attempt++) {
                        // Read the current reference and stamp.
                        int[] stampHolder = new int[1];
                        BankAccount current = accountRef.get(stampHolder);
                        int currentStamp = stampHolder[0];

                        // Prepare the new state.
                        BankAccount newAccount = new BankAccount(
                            current.accountNumber(),
                            current.balance() + 100.0
                        );

                        // Attempt the atomic update.
                        if (accountRef.compareAndSet(current, newAccount, currentStamp, currentStamp + 1)) {
                            IO.println("Thread " + threadId + " successfully deposited $100. " +
                                "New balance: $" + newAccount.balance() +
                                ", Stamp: " + (currentStamp + 1));
                            break; // Exit the retry loop on success.
                        } else {
                            // If CAS failed, another thread updated the value. Retry after a short delay.
                            IO.println("Thread " + threadId + " failed attempt " + (attempt + 1) +
                                " - retrying...");
                            Thread.sleep(10); // Small delay to reduce contention.
                        }
                    }
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                } finally {
                    // Signal that this thread has finished.
                    latch.countDown();
                }
            }).start();
        }

        // Wait for all threads to finish their work.
        latch.await();
        IO.println("Final account state: %s, Final stamp: %d".formatted(accountRef.getReference(), accountRef.getStamp()));
    }
}
