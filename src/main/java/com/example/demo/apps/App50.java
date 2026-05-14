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
        sortByRankSuit();
        println("-- RankSuit ---");
        printDeck(sortByRankSuit());
        println("-- SuitRank ---");
        sortBySuitRank();
        printDeck(sortBySuitRank());
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
        for (Ranks rank : ranks) {
            for (Suits suit : suits) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    void printDeck(List<Card> list) {
        int n = 0;
        for (Card card : list) {
            print(card.rank() + " of " + card.suit() + " ");
            if (++n % 4 == 0) {
                println();
                n = 0;
            }
        }
    }
    private static final List<Ranks> ranks = List.of(
            Ranks.TWO, Ranks.THREE, Ranks.FOUR, Ranks.FIVE, Ranks.SIX, Ranks.SEVEN, Ranks.EIGHT, Ranks.NINE,
            Ranks.TEN, Ranks.JACK, Ranks.QUEEN, Ranks.KING, Ranks.ACE);
    private static final List<Suits> suits = List.of(Suits.Karo, Suits.Kier, Suits.Pik, Suits.Trefl);

    record Card(Ranks rank, Suits suit) {}

    enum Suits {
        Kier, Karo, Pik, Trefl
    }

    enum Ranks {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6),
        SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(11),
        QUEEN(12), KING(13), ACE(14);

        final int value;

        Ranks(int value) {
            this.value = value;
        }
    }

    private static final Set<Card> deck = new HashSet<>(52);
}
