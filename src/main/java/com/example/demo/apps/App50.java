package com.example.demo.apps;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class App50 {

    void main() {
        Deck deck = new Deck();
        deck.printDeck("--- Initial ---", deck.cards(), Deck.CARDS_PER_RANKS);
        deck.printDeck("--- Rank -> Suit ---", sortBy(deck, rankThenSuit), Deck.CARDS_PER_RANKS);
        deck.printDeck("--- Suit -> Rank ---", sortBy(deck, suitThenRank), Deck.CARDS_PER_SUIT);
    }

    private List<Deck.Card> sortBy(@NonNull Deck deck, @NonNull Comparator<Deck.Card> comparator) {
        return deck.cards().stream().sorted(comparator).toList();
    }

    private static final Comparator<Deck.Card> rankThenSuit = Comparator
        .comparing(Deck.Card::rank).thenComparing(Deck.Card::suit);
    private static final Comparator<Deck.Card> suitThenRank = Comparator
        .comparing(Deck.Card::suit).thenComparing(Deck.Card::rank);

    static class Deck {
        public record Card(Ranks rank, Suits suit) {
        }

        private final Set<Card> cards;

        public Deck() {
            this.cards = new HashSet<>(52);
            createDeck();
        }

        public Set<Card> cards() {
            return this.cards;
        }

        private void createDeck() {
            for (Ranks rank : Ranks.values()) {
                for (Suits suit : Suits.values()) {
                    this.cards.add(new Card(rank, suit));
                }
            }
        }

        public static final int CARDS_PER_RANKS = 4;
        public static final int CARDS_PER_SUIT = 13;
        private static final String SEPARATOR = " | ";
        private static final String RESET = "\u001B[0m";
        private static final String RED = "\u001B[31m";
        private static final String GREEN = "\u001B[32m";

        /**
         * Print deck of cards
         *
         * @param header     - text
         * @param list       - list of cards
         * @param lineLength - number of cards per line
         */
        public void printDeck(String header, Collection<Card> list, int lineLength) {
            IO.println(RED + header + RESET + GREEN);
            int n = 0;
            for (Card card : list) {
                IO.print(card.rank() + " of " + card.suit());
                if (++n % lineLength == 0) {
                    IO.println();
                    n = 0;
                } else {
                    IO.print(SEPARATOR);
                }
            }
        }

        enum Suits {
            HEARTS, DIAMONDS, SPADES, CLUBS
        }

        enum Ranks {
            TWO, THREE, FOUR, FIVE, SIX,
            SEVEN, EIGHT, NINE, TEN, JACK,
            QUEEN, KING, ACE
        }
    }
}
