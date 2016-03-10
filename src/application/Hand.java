package application;
/*
 *  From http://www.codeproject.com/Articles/38821/Make-a-poker-hand-evalutator-in-Java
 *  with very few modifications
 */
public class Hand {

	private Card[] cards;
	private int[] value;

	public Hand(Card[] hand) {
		cards = new Card[hand.length];
		value = new int[6];
		for (int i = 0; i < hand.length; i++) {
			this.cards[i] = hand[i];
		}
		int[] ranks = new int[15];
		for (int i = 0; i < ranks.length; i++) {
			ranks[i] = 0;
		}
		for (int i = 0; i < cards.length; i++) {
			ranks[cards[i].getValue()]++;
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
		for (int i = 0; i < cards.length - 1; i++) {
			if (cards[i].getSuit() != cards[i+1].getSuit())
				flush = false;
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
				ranks[13] == 1 && ranks[1] == 1) {
			straight = true;
			straightHighVal = 14;
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
		String hand = "";
		for (int i = 0; i < this.cards.length; i++) {
			hand += cards[i].toString();
			hand += "\n";
		}
		switch (value[0]) {
		case 1:
			hand += "HIGH CARD\n";
			break;
		case 2:
			hand += "PAIR OF: " + valueOf(value[1]) + "'s\n";
			break;
		case 3:
			hand += "TWO PAIR: " + valueOf(value[1]) + "'s" + " and " + value[2] + "'s\n";
			break;
		case 4:
			hand += "THREE OF A KIND: " + valueOf(value[1]) +
			"'s\n";
			break;
		case 5:
			hand += "STRAIGHT: " + valueOf(value[1]) + " high\n";
			break;
		case 6:
			hand += "FLUSH\n";
			break;
		case 7:
			hand += "FULL HOUSE: Pair of " + valueOf(value[2])
			+ "'s\n" + "Three of a kind: " + valueOf(value[1])
			+ "'s\n";
			break;
		case 8:
			hand += "FOUR OF A KIND: " + valueOf(value[1]) + "'s\n";
			break;
		case 9:
			hand += "STRAIGHT FLUSH: " + valueOf(value[1]) + " high\n";
			break;
		}
		return hand;
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
