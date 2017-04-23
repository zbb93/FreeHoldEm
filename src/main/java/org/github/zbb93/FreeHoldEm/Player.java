/*
 * FreeHoldEm
 * Copyright 2015-2017 by Zachary Bowen
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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/*
  Represents a player in a card game, can be either
  CPU or human

  TODO: Randomly assign players an integer value
        that determines their personality. High
        value indicates a more aggressive player
        while lower values indicate more cautious
        players?
 */
public class Player {

	private final String name;
	private final List<Card> hand;
	private Hand finalHand;
	private int chips;
	private boolean isFolded;
	private final boolean isHuman;
	private int betThisRound;

	/**
	 * Constructor for AI player. Each player starts with 500 chips.
	 * @param name The name of the player
	 */
	public Player(String name) {
		this(name, false);
	}

	public Player(String name, boolean isHuman) {
		hand = Lists.newArrayListWithCapacity(2);
		this.name = name;
		this.chips = 500;
		this.isFolded = false;
		this.betThisRound = 0;
		this.isHuman = isHuman;
	}

	/**
	 * Method to update the cards a player has in their hand.
	 * @param index index in player card array to place the card.
	 * @param card the card to place into the players hand.
	 */
	public void setCard(int index, Card card) {
		hand.add(index, card);
	}
	/**
	 * Method to update the hand a player currently has (cards on table +
	 * cards in hand).
	 * @param hand the hand to give the player.
	 */
	public void setHand(Hand hand) {
		finalHand = hand;
	}
	/**
	 * Method to update the amount of chips a player has.
	 * @param newVal The updated amount of chips the player is to have.
	 */
	public void setChips(int newVal) {
		chips = newVal;
	}
	/**
	 * Returns the current hand of the player.
	 * @return The current hand of the player.
	 */
	public Hand getHand() {
		return this.finalHand;
	}
	/**
	 * Returns the name of the player
	 * @return name of the player as string.
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * Method to view the cards a player was dealt.
	 * @return Two element card array representing initial cards a player
	 * was dealt.
	 */
	public List<Card> getCards() {
		return this.hand;
	}
	/**
	 * Method to view current amount of chips a player has
	 * @return chips as an integer value.
	 */
	public int getChips() {
		return this.chips;
	}
	/**
	 * Method to cause a player to fold. Sets boolean isFolded to true.
	 */
	public void fold() {
		this.isFolded = true;
	}
	/**
	 * Method to 'unfold' a player before the next hand begins. Sets boolean
	 * isFolded to false.
	 */
	public void unFold() {
		this.isFolded = false;
	}
	/**
	 * Determines whether or not a player has chosen to fold.
	 * @return true if player is folded and false otherwise.
	 */
	public boolean checkFold() {
		return this.isFolded;
	}

	public boolean hasBet() {
		return (this.betThisRound > 0);
	}

	public void bet(int amount) {
		this.betThisRound += amount;
		this.chips -= amount;
	}

	public void resetBet() {
		this.betThisRound = 0;
	}

	public int getBetThisRound() {
		return this.betThisRound;
	}

	public void deductChips(int bet) {
		chips -= bet;
	}

	public boolean isHuman() {
		return isHuman;
	}



	/**
	 * Converts player hand to a string value.
	 * @return A string made of the string value of card1 followed by card2.
	 * Note currently only returns the cards that are in the players hand,
	 * not those that may be included in the best hand.
	 */
	@Override
	public String toString() {
		return Joiner.on(',').join(hand);
	}
}
