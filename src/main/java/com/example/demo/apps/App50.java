package com.example.demo.apps;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.IO.print;
import static java.lang.IO.println;

public class App50 {

    void main() {
        createDeck();
        printDeck("--- Rank -> Suit ---", sortBy(rankThenSuit), CARDS_PER_RANKS);
        printDeck("--- Suit -> Rank ---", sortBy(suitThenRank), CARDS_PER_SUIT);
    }

    private List<Card> sortBy(Comparator<Card> comparator) {
        return deck.stream().sorted(comparator).toList();
    }

    private static final Comparator<Card> rankThenSuit = Comparator.comparing(Card::rank).thenComparing(Card::suit);
    private static final Comparator<Card> suitThenRank = Comparator.comparing(Card::suit).thenComparing(Card::rank);

    void createDeck() {
        for (Ranks rank : Ranks.values()) {
            for (Suits suit : Suits.values()) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    private static final int CARDS_PER_RANKS = 4;
    private static final int CARDS_PER_SUIT = 13;
    private static final String SEPARATOR = " | ";
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";

    void printDeck(String header, List<Card> list, int lineLength) {
        println(RED + header + RESET + GREEN);
        int n = 0;
        for (Card card : list) {
            print(card.rank() + " of " + card.suit());
            if (++n % lineLength == 0) {
                println();
                n = 0;
            } else {
                print(SEPARATOR);
            }
        }
    }

    record Card(Ranks rank, Suits suit) {}

    private static final Set<Card> deck = new HashSet<>(52);

    enum Suits {
        HEARTS, DIAMONDS, SPADES, CLUBS
    }

    enum Ranks {
        TWO, THREE, FOUR, FIVE, SIX,
        SEVEN, EIGHT, NINE, TEN, JACK,
        QUEEN, KING, ACE
    }
}
