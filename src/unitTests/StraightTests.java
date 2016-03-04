package unitTests;

import application.Card;
import application.Tuple;

public class StraightTests {
	
	public static void main(String[] args) {
		Card card1 = new Card("spades", 10);
		Card card2 = new Card("hearts", 9);
		Card card3 = new Card("spades", 8);
		Card card4 = new Card("spades", 7);
		Card card5 = new Card("diamonds", 6);
		Card card6 = new Card("clubs", 3);
		Card card7 = new Card("spades", 2);
		Card[] hand = {card1, card2, card3, card4, card5, card6, card7};
		Tuple straightTest = containsStraight(hand);
		boolean test = straightTest.x == 1 && straightTest.y == 4;
		System.out.println(test);
		System.out.printf("X = %d ; Y = %d\n", straightTest.x, straightTest.y);
	}

	private static Tuple containsStraight(Card[] hand) {
		int consecutiveVals = 0;
		int index = 0;
		for (int i = 1; i < hand.length; i++) {
			if (hand[i] == null)
				break;
			if ((hand[i].getValue() == hand[i - 1].getValue() - 1)) {
				consecutiveVals += 1;
				index = i;
			}
			else if (hand[i].getValue() == hand[i - 1].getValue()) {
				continue;
			}
			else {
				if (consecutiveVals >= 3)
					break;					
				else 
					consecutiveVals = 0;
			}
		}		
		if (consecutiveVals >= 4)
			return (new Tuple(1, index));
		/*
		 * Handle case of ace low straight
		 */
		else if (consecutiveVals == 3 &&
				hand[0].getValue() == 14 &&
				hand[index].getValue() == 2) {
			return (new Tuple(2, index));	
		}
		else
			return (new Tuple(-1, -1));
	}
}
