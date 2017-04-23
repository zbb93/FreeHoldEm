package org.github.zbb93.FreeHoldEm;

import java.util.Objects;

/**
 * Simple class that represents a playing card.
 */

public class Card {

        private final String suit;
        private final int value;

        /**
         *Takes two parameters suit and face value of card.
         *@param value: The value of the card represented as an integer. An ace is 14, a jack
         *              is 11, a Queen is 12, and a King is 13.
         */
        public Card(String suit, int value) {
        	if (value == 1) {
        		this.value = 14;
        		this.suit = suit;
        	}
        	else {
        		this.suit = suit;
        		this.value = value;
        	}
        }

        /*
         * Getters, setters, toString, and equals implementations.
         */
        public String getSuit() {
                return this.suit;
        }

        public int getValue() {
                return this.value;
        }

        @Override
        public String toString() {
                if (value == 14)
                        return "Ace of " + suit;
                else if (value == 11)
                        return "Jack of " + suit;
                else if (value == 12)
                        return "Queen of " + suit;
                else if (value == 13)
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
