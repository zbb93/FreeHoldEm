/*
 * FreeHoldEm
 * Copyright 2017 by Zachary Bowen
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.github.zbb93.FreeHoldEm;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * This class can be used to quickly put together a deck for testing various AI strategies.
 * Created by zbb on 5/21/17.
 */
public class DeckBuilder {

	/**
	 * List of cards sorted into the order that they will be dealt to the players. Sorting occurs in the build method.
	 * Until the build method is called the List will be empty.
	 */
	private List<Card> cards;

	/**
	 * List of Lists of Cards. Every time the addPlayerHand method is called a new List is added to this List.
	 * In the build method we iterate over this List and add the cards to the cards List in the order they need to be
	 * dealt. When the build method is called if the size of this List is greater than eight an exception will be thrown.
	 */
	private List<List<Card>> playerCards;

	/**
	 * List of cards that represent the cards on the table. This List is populated by calling the addTableCards method.
	 * If the size of the List is not five an exception will be thrown.
	 */
	private List<Card> tableCards;

	public DeckBuilder() {
		cards = Lists.newLinkedList();
		tableCards = Lists.newLinkedList();
		playerCards = Lists.newArrayList();
	}

	/**
	 * Appends a copy of the passed in List to the playerCards List.
	 * @param playerHand List of cards to be added to playerCards collection.
	 * @throws IllegalArgumentException if the size of the playerHand List is not two.
	 */
	public void addPlayerHand(List<Card> playerHand) {
		Preconditions.checkArgument(playerHand.size() == 2);
		playerCards.add(ImmutableList.copyOf(playerHand));
	}

	/**
	 * Adds the passed in List to the table cards List.
	 * @param tableCards List of cards in the order they will be dealt on the table.
	 * @throws IllegalArgumentException if there are not five cards in the passed in List.
	 */
	public void addTableCards(List<Card> tableCards) {
		Preconditions.checkArgument(tableCards.size() == 5);
		this.tableCards.addAll(ImmutableList.copyOf(tableCards));
	}

	/**
	 * @return the result of combining the tableCards and playerCards List.
	 * @throws IllegalStateException if there are more players than the game supports (currently eight).
	 */
	public Deck build() {
		Preconditions.checkState(playerCards.size() <= 8);

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < playerCards.size(); j++) {
				cards.add(playerCards.get(i).get(j));
			}
		}
		cards.addAll(tableCards);

		return new Deck(cards);
	}
}
