package com.example.demo.apps;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class App15 {
    public static void main(String... args) {
        Currency usd = Currency.getInstance("USD");
        Money price = new Money(new BigDecimal("19.99"), usd);
        Money tax = new Money(new BigDecimal("1.40"), usd);
        Money total = price.add(tax);

        System.out.println("Total: " + total); // Output: Total: USD 21.39
        
        Currency cad = Currency.getInstance("CAD");
        Money price2 = new Money(new BigDecimal("11.23"), cad);

        System.out.println(price);
        System.out.println(price2);
        
        System.out.println(price.add(price2)); // Output: CAD 21.39
    }
}

@Getter
class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currencies do not match");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    @Override
    public String toString() {
        return currency.getSymbol() + " " + amount;
    }
}
