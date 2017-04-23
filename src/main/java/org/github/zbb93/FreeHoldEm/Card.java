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

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Simple class that represents a playing card.
 */
public class Card {

	public enum Suit {
		CLUBS, DIAMONDS, HEARTS, SPADES
	}

	private final Suit suit;
	private final int value;

	public static final int ACE_LOW = 1;
	public static final int ACE_HIGH = 14;
	// todo not sure if two ace constants are needed, but the constructor was converting 1 to 14. so for now there are two.
	public static final int KING = 13;
	public static final int QUEEN = 12;
	public static final int JACK = 11;

	private static final String CLUBS = "clubs";
	private static final String DIAMONDS = "diamonds";
	private static final String HEARTS = "hearts";
	private static final String SPADES = "spades";

	/**
	 *Takes two parameters suit and face value of card.
	 *@param value: The value of the card represented as an integer. An ace is 14, a jack
	 *              is 11, a Queen is 12, and a King is 13. Values outside [1,14] will cause
	 *              an IllegalArgumentException to be thrown.
	 */
	public Card(@NotNull Suit suit, int value) {
		Preconditions.checkArgument(value > 0 && value < 15);
		if (value == ACE_LOW) {
			this.value = ACE_HIGH;
			this.suit = suit;
		} else {
			this.value = value;
			this.suit = suit;
		}
	}

	// Getters, setters, toString, and equals implementations.

	@NotNull
	public String getSuit() {
		return getSuitAsString(suit);
	}

	@NotNull
	private String getSuitAsString(Suit suit) {
		if (suit == Suit.CLUBS) {
			return CLUBS;
		} else if (suit == Suit.DIAMONDS) {
			return DIAMONDS;
		} else if (suit == Suit.HEARTS) {
			return HEARTS;
		} else if (suit == Suit.SPADES) {
			return SPADES;
		}
		throw new IllegalArgumentException("Unrecognized suit!");
	}

	public int getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		if (value == ACE_HIGH)
			return "Ace of " + suit;
		else if (value == JACK)
			return "Jack of " + suit;
		else if (value == QUEEN)
			return "Queen of " + suit;
		else if (value == KING)
			return "King of " + suit;
		else
			return String.valueOf(value) + " of " + suit;
	}

	/**
	 * Tests whether two cards are equal. Two cards are considered equal if
	 * they have the same suit and the same value.
	 * @param obj The object to compare this card to.
	 * @return true if the cards are equal, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Card))
			return false;
		Card other = (Card) obj;
		return Objects.equals(other.getSuit(), this.getSuit()) && other.getValue() == this.getValue();
	}
}
