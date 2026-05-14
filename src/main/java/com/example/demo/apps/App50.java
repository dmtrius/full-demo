package com.example.demo.apps;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.IO.print;
import static java.lang.IO.println;

public class App50 {

    private static final String SEPARATOR = " | ";

    void main() {
        createDeck();
        sortByRankSuit();
        println("--- Rank -> Suit ---");
        printDeck(sortByRankSuit(), CARDS_PER_RANKS);
        println("--- Suit -> Rank ---");
        sortBySuitRank();
        printDeck(sortBySuitRank(), CARDS_PER_SUIT);
    }

    private static final Comparator<Card> rankSuit = Comparator.comparing(Card::rank).thenComparing(Card::suit);
    private static final Comparator<Card> suitRank = Comparator.comparing(Card::suit).thenComparing(Card::rank);

    private static List<Card> sortByRankSuit() {
        return deck.stream().sorted(rankSuit).toList();
    }

    private static List<Card> sortBySuitRank() {
        return deck.stream().sorted(suitRank).toList();
    }

    void createDeck() {
        for (Ranks rank : Ranks.values()) {
            for (Suits suit : Suits.values()) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    private static final int CARDS_PER_RANKS = 4;
    private static final int CARDS_PER_SUIT = 13;

    void printDeck(List<Card> list, int lineLength) {
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
