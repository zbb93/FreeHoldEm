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

import com.google.common.collect.Lists;

import java.util.List;

/*
 *  From http://www.codeproject.com/Articles/38821/Make-a-poker-hand-evalutator-in-Java
 *  with very few modifications
 */
public class Hand {

	private final List<Card> cards;
	private final int[] value;

	public Hand(List<Card> hand) {
		cards = Lists.newArrayList(hand);
		value = new int[6];
		int[] ranks = new int[15];
		for (int i = 0; i < ranks.length; i++) {
			ranks[i] = 0;
		}
		for (Card card : cards) {
			ranks[card.getValue()]++;
		}
		//Evaluate hand for pairs, 3 of a kind, 4 of a kind, full house
		int sameCards = 1, sameCards2 = 1;
		int largeGroupRank = 0, smallGroupRank = 0;
		for (int i = ranks.length - 1; i > 0; i-- ) {
			if (ranks[i] > sameCards) {
				if (sameCards != 1) {
					sameCards2 = sameCards;
					smallGroupRank = largeGroupRank;
				}
				sameCards = ranks[i];
				largeGroupRank = i;
			}
			else if (ranks[i] > sameCards2) {
				sameCards2 = ranks[i];
				smallGroupRank = i;
			}
		}
		//Evaluate hand for flush
		boolean flush = true;
		for (int i = 0; i < cards.size() - 1; i++) {
			if (!cards.get(i).getSuit().equals(cards.get(i+1).getSuit())) {
				flush = false;
			}
		}
		//Evaluate hand for straight
		int straightHighVal = 0;
		boolean straight = false;
		//Can ace count high and low for straight?
		for (int i = 2; i <= 9; i++) {
			if (ranks[i] == 1 && ranks[i+1] == 1 && ranks[i+2] == 1 &&
					ranks[i+3] == 1 && ranks[i+4] == 1) {
				straight = true;
				straightHighVal = i + 4;
				break;
			}
		}
		if (ranks[10] == 1 && ranks[11] == 1 && ranks[12] == 1 && 
				ranks[13] == 1 && ranks[14] == 1) {
			straight = true;
			straightHighVal = 14;
		}
		if (ranks[14] == 1 && ranks[2] == 1 && ranks[3] == 1 && 
				ranks[4] == 1 && ranks[5] == 1) {
			straight = true;
			straightHighVal = 5;
		}
		int[] orderedRanks = new int[5];
		int index = 0;
		if (ranks[1] == 1) {
			orderedRanks[index] = 14;
			index++;
		}
		for (int i = ranks.length - 1; i > 1; i--) {
			if (ranks[i] == 1) {
				orderedRanks[index] = i;
				index++;
			}
		}

		//Begin scoring hand
		if (sameCards == 1) {
			value[0] = 1;
			value[1] = orderedRanks[0];
			value[2] = orderedRanks[1];
			value[3] = orderedRanks[2];
			value[4] = orderedRanks[3];
			value[5] = orderedRanks[4];
		}
		if (sameCards == 2 && sameCards2 == 1) {
			value[0] = 2;
			value[1] = largeGroupRank;
			value[2] = orderedRanks[0];
			value[3] = orderedRanks[1];
			value[4] = orderedRanks[2];
		}
		if (sameCards == 2 && sameCards2 == 2) {
			value[0] = 3;
			value[1] = largeGroupRank > smallGroupRank ? largeGroupRank : smallGroupRank;
			value[2] = largeGroupRank < smallGroupRank ? largeGroupRank : smallGroupRank;
			value[3] = orderedRanks[0];
		}
		if (sameCards == 3 && sameCards2 != 2) {
			value[0] = 4;
			value[1] = largeGroupRank;
			value[2] = orderedRanks[0];
			value[3] = orderedRanks[1];
		}
		if (straight) {
			value[0] = 5;
			value[1] = straightHighVal;
		}
		if (flush) {
			value[0] = 6;
			value[1] = orderedRanks[0];
			value[2] = orderedRanks[1];
			value[3] = orderedRanks[2];
			value[4] = orderedRanks[3];
			value[5] = orderedRanks[4];
		}
		if (sameCards == 3 && sameCards2 == 2) {
			value[0] = 7;
			value[1] = largeGroupRank;
			value[2] = smallGroupRank;
		}
		if (sameCards == 4) {
			value[0] = 8;
			value[1] = largeGroupRank;
			value[2] = orderedRanks[0];
		}
		if (straight && flush) {
			value[0] = 9;
			value[1] = straightHighVal;
		}
	}
	//TODO: Change 1, 11, 12, 13 to display ace, jack, king, queen.
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Card card : cards) {
			sb.append(card);
			sb.append("\n");
		}
		switch (value[0]) {
		case 1:
			sb.append("HIGH CARD\n");
			break;
		case 2:
			sb.append("PAIR OF: ").append(valueOf(value[1])).append("'s\n");
			break;
		case 3:
			sb.append("TWO PAIR: ").append(valueOf(value[1])).append("'s and ").append(value[2]).append("'s\n");
			break;
		case 4:
			sb.append("THREE OF A KIND: ").append(valueOf(value[1])).append("'s\n");
			break;
		case 5:
			sb.append("STRAIGHT: ").append(valueOf(value[1])).append(" high\n");
			break;
		case 6:
			sb.append("FLUSH\n");
			break;
		case 7:
			sb.append("FULL HOUSE: Pair of ").append(valueOf(value[2]))
			.append("'s\nThree of a kind: ").append(valueOf(value[1]))
			.append("'s\n");
			break;
		case 8:
			sb.append("FOUR OF A KIND: ").append(valueOf(value[1])).append("'s\n");
			break;
		case 9:
			sb.append("STRAIGHT FLUSH: ").append(valueOf(value[1])).append(" high\n");
			break;
		}
		return sb.toString();
	}
	/**
	 * Compares two hands to determine which is better.
	 * @param that Hand to compare this hand to.
	 * @return 1 if this hand is better than the hand being compared to, -1
	 * if it is worse, or 0 if they are the same hand.
	 */
	public int compareTo(Hand that) {
		for (int i = 0; i < value.length; i++) {
			if (this.value[i] > that.value[i])
				return 1;
			else if (this.value[i] != that.value[i])
				return -1;
		}
		return 0;
	}

	/**
	 * Simple getter to return value of the hand.
	 * @return hand ranked as an integer. 1 is a high card hand and
	 * 8 is a straight flush hand.
	 */
	public int quickVal() {
		return value[0];
	}

	/**
	 * Helper method used with toString. This method will return the string
	 * representation of faceValue. For integers 1-10 it is simply the integer
	 * as a string. For 11 it is jack, 12 is queen, 13 is king, and 14 is ace.
	 * @param faceValue integer representing face value of a card.
	 * @return String representation of integer faceValue
	 */
	private String valueOf(int faceValue) {
		if (faceValue < 11)
			return Integer.toString(faceValue);
		else {
			switch (faceValue) {
			case 11:
				return ("Jack");
			case 12:
				return ("Queen");
			case 13:
				return ("King");
			case 14:
				return ("Ace");
			}
		}
		return null;
	}

}
