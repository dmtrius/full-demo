package com.example.demo.apps.patterns;

import lombok.Getter;

import java.util.Deque;
import java.util.LinkedList;

public class CommandExample {
    void main() {
        var account = new BankAccount(100);
        var invoker = new CommandInvoker();

        // Perform some operations
        invoker.executeCommand(new DepositCommand(account, 50));    // Deposit $50
        invoker.executeCommand(new WithdrawCommand(account, 30));   // Withdraw $30
        invoker.executeCommand(new WithdrawCommand(account, 150));  // Try to withdraw $150 (will fail)

        // Now undo the last two operations
        System.out.println("--- Undoing last operation ---");
        invoker.undoLast();  // undo withdraw $150 attempt
        System.out.println("--- Undoing previous operation ---");
        invoker.undoLast();  // undo withdraw $30
    }
}

// Invoker that keeps history for undo
class CommandInvoker {
    private final Deque<Command> history = new LinkedList<>();

    public void executeCommand(Command cmd) {
        cmd.execute();
        history.push(cmd);
    }

    public void undoLast() {
        if (!history.isEmpty()) {
            Command lastCommand = history.pop();
            lastCommand.undo();
        } else {
            System.out.println("No commands to undo");
        }
    }
}

// Receiver class
@Getter
class BankAccount {
    private Integer balance;

    public BankAccount(Integer balance) {
        this.balance = balance;
        System.out.println("Created new bank account with balance $" + balance);
    }

    void deposit(Integer amount) {
        balance += amount;
        System.out.println("Deposited $" + amount + ", new balance is $" + balance);
    }

    boolean withdraw(Integer amount) {
        if (amount <= balance) {
            balance -= amount;
            System.out.println("Withdrew $" + amount + ", new balance is $" + balance);
            return true;
        } else {
            System.out.println("Withdraw $" + amount + " failed, balance is only $" + balance);
            return false;
        }
    }
}

// Concrete Command for deposit
class DepositCommand implements Command {
    private final BankAccount account;
    private final Integer amount;

    public DepositCommand(BankAccount account, Integer amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        account.deposit(amount);
    }
    @Override
    public void undo() {
        // Undo deposit by withdrawing (assuming sufficient balance, which should hold true here)
        account.withdraw(amount);
        System.out.println("Undo Deposit: withdrew $" + amount + ", balance back to $" + account.getBalance());
    }
}

// Concrete Command for withdraw
class WithdrawCommand implements Command {
    private final BankAccount account;
    private final Integer amount;
    // Track if the withdraw actually happened (to handle undo properly)
    private boolean success = false;

    public WithdrawCommand(BankAccount account, Integer amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        success = account.withdraw(amount);
    }
    @Override
    public void undo() {
        if (success) {
            // Undo withdraw by depositing back
            account.deposit(amount);
            System.out.println("Undo Withdraw: deposited $" + amount + ", balance back to $" + account.getBalance());
        } else {
            System.out.println("Undo Withdraw: nothing to undo");
        }
    }
}

interface Command {
    void execute();
    void undo();
}
