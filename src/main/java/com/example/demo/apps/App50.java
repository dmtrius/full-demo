package com.example.demo.apps;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class App50 {

    void main() {
        Deck deck = new Deck();
        deck.print("--- Initial ---", deck.cards(), Deck.CARDS_PER_RANKS);
        deck.print("--- Rank -> Suit ---", sortBy(deck.cards(), rankThenSuit), Deck.CARDS_PER_RANKS);
        deck.print("--- Suit -> Rank ---", sortBy(deck.cards(), suitThenRank), Deck.CARDS_PER_SUIT);
    }

    private List<Deck.Card> sortBy(@NonNull List<Deck.Card> cards,
                                   @NonNull Comparator<Deck.Card> comparator) {
        return cards.stream().sorted(comparator).toList();
    }

    private static final Comparator<Deck.Card> rankThenSuit = Comparator
        .comparing(Deck.Card::rank).thenComparing(Deck.Card::suit);
    private static final Comparator<Deck.Card> suitThenRank = Comparator
        .comparing(Deck.Card::suit).thenComparing(Deck.Card::rank);

    static class Deck {
        public record Card(Rank rank, Suit suit) {
        }

        private final Set<Card> cards;

        public Deck() {
            this.cards = new HashSet<>(Suit.values().length * Rank.values().length);
            createDeck();
        }

        public List<Card> cards() {
            return new ArrayList<>(this.cards);
        }

        private void createDeck() {
            for (Rank rank : Rank.values()) {
                for (Suit suit : Suit.values()) {
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
         * @param cardsInLine - number of cards per line
         */
        public void print(String header, @NonNull Collection<Card> list, int cardsInLine) {
            IO.println(RED + header + RESET + GREEN);
            int n = 0;
            for (Card card : list) {
                IO.print("%2s of %s".formatted(card.rank(), card.suit()));
                if (++n % cardsInLine == 0) {
                    IO.println();
                    n = 0;
                } else {
                    IO.print(SEPARATOR);
                }
            }
        }

        enum Suit {
            SPADES("♠"),
            HEARTS("♥"),
            CLUBS("♣"),
            DIAMONDS("♦");

            private final String symbol;

            Suit(String symbol) {
                this.symbol = symbol;
            }

            @Override
            public String toString() {
                return symbol;
            }
        }

        enum Rank {
            ACE("A"),
            TWO("2"),
            THREE("3"),
            FOUR("4"),
            FIVE("5"),
            SIX("6"),
            SEVEN("7"),
            EIGHT("8"),
            NINE("9"),
            TEN("10"),
            JACK("J"),
            QUEEN("Q"),
            KING("K");

            private final String symbol;

            Rank(String symbol) {
                this.symbol = symbol;
            }

            @Override
            public String toString() {
                return symbol;
            }
        }
    }
}
