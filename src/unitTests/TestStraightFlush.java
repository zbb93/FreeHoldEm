package unitTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import application.Card;
import application.FreeHoldEm;
import application.Hand;
import application.HandEvaluator;
import application.Player;

public class TestStraightFlush {

	@Test
	public void StraightFlushandStraightTest() {
		Card card1 = new Card("spades", 14);
		Card card2 = new Card("spades", 13);
		Card card3 = new Card("spades", 12);
		Card card4 = new Card("hearts", 7);
		Card card5 = new Card("spades", 11);
		Card card6 = new Card("clubs", 10);
		Card card7 = new Card("spades", 10);
		Player p = new Player("test");
		p.setCards(0, card1);
		p.setCards(1, card2);
		Card[] table = {card3, card4, card5, card6, card7};
		FreeHoldEm game = new FreeHoldEm(table);
		HandEvaluator he = new HandEvaluator();
		he.findBestHand(game, p);
		Card[] expectedCards = {card7, card5, card3, card2, card1};
		Hand expectedHand = new Hand(expectedCards);
		System.out.println(p.getHand().toString());
		assertEquals(0, expectedHand.compareTo(p.getHand()));
	}
}
