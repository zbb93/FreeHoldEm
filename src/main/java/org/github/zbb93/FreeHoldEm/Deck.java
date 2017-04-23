//Copyright (C) 2017 Zachary Bowen
//This file is part of FreeHoldEm.
//
//FreeHoldEm is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//FreeHoldEm is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with FreeHoldEm.  If not, see <http://www.gnu.org/licenses/>.
package org.github.zbb93.FreeHoldEm;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * Represents traditional deck of 52 playing cards.
 */
public class Deck {

	/**
	 * Deck is represented by an array of 52 cards.
	 */
	private final @NotNull List<Card> cards;
	/**
	 * Integer value that represents the next card in the deck
	 */
	private int index = 0;

	/**
	 * Size of a standard deck of playing cards
	 */
	private static final int STANDARD_SIZE = 52;

	/**
	 * Initializes card values and then shuffles the deck.
	 */
	public Deck() {
		cards = Lists.newArrayListWithCapacity(STANDARD_SIZE);
		addCardsOfSuit(Card.Suit.HEARTS);
		addCardsOfSuit(Card.Suit.DIAMONDS);
		addCardsOfSuit(Card.Suit.SPADES);
		addCardsOfSuit(Card.Suit.CLUBS);
		shuffle();
	}

	/**
	 * Adds Cards with face value 2-Ace to the deck with the given suit
	 * @param suit The suit of the cards to be added to the deck
	 */
	private void addCardsOfSuit(Card.Suit suit) {
		for (int i = 2; i < 14; i++) {
			cards.add(new Card(suit, i));
		}
	}

	/**
	 * Fisher-Yates shuffle used to shuffle elements of card array
	 * in place.
	 */
	private void shuffle() {
		Random numGen = new Random();
		for (int i = cards.size() - 1; i > 0; i--) {
			int j = numGen.nextInt(i);
			Card tmp = cards.get(i);
			cards.add(i, cards.get(j));
			cards.add(j, tmp);
		}
	}

	/**
	 * Gets the card from the top of the deck. If the deck is empty then it is
	 * reshuffled and the index is reset to zero.
	 */
	@NotNull
	public Card getNextCard() {
		if (index < STANDARD_SIZE) {
			return cards.get(index++);
		} else {
			this.shuffle();
			index = 0;
			return cards.get(index);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Card card : cards) {
			sb.append(card.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
