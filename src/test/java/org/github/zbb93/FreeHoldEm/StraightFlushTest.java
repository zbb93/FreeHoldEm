package org.github.zbb93.FreeHoldEm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
/*
import Card;
import FreeHoldEm;
import Hand;
import HandEvaluator;
import Player;*/

public class StraightFlushTest {

	@Test
	public void StraightFlushandStraightTest() {
		Card card1 = new Card(Card.Suit.SPADES, 14);
		Card card2 = new Card(Card.Suit.SPADES, 13);
		Card card3 = new Card(Card.Suit.SPADES, 12);
		Card card4 = new Card(Card.Suit.HEARTS, 7);
		Card card5 = new Card(Card.Suit.SPADES, 11);
		Card card6 = new Card(Card.Suit.CLUBS, 10);
		Card card7 = new Card(Card.Suit.SPADES, 10);
		Player p = new Player("test");
		p.setCards(0, card1);
		p.setCards(1, card2);
		Card[] table = {card3, card4, card5, card6, card7};
		FreeHoldEm game = new FreeHoldEm(table);
		HandEvaluator he = new HandEvaluator();
		he.findBestHand(game, p);
		Card[] expectedCards = {card7, card5, card3, card2, card1};
		Hand expectedHand = new Hand(expectedCards);
		assertEquals(0, expectedHand.compareTo(p.getHand()));
	}
	
	@Test
	public void testAceHighStraightFlush() {
		Card card1 = new Card(Card.Suit.SPADES, 14);
		Card card2 = new Card(Card.Suit.SPADES, 13);
		Card card3 = new Card(Card.Suit.SPADES, 12);
		Card card4 = new Card(Card.Suit.HEARTS, 7);
		Card card5 = new Card(Card.Suit.SPADES, 11);
		Card card6 = new Card(Card.Suit.CLUBS, 5);
		Card card7 = new Card(Card.Suit.SPADES, 10);
		Player p = new Player("test");
		p.setCards(0, card1);
		p.setCards(1, card2);
		Card[] table = {card3, card4, card5, card6, card7};
		FreeHoldEm game = new FreeHoldEm(table);
		HandEvaluator he = new HandEvaluator();
		he.findBestHand(game, p);
		Card[] expectedCards = {card7, card5, card3, card2, card1};
		Hand expectedHand = new Hand(expectedCards);
		assertEquals(0, expectedHand.compareTo(p.getHand()));
	}
	
	@Test
	public void testAceLowStraightFlush() {
		Card card1 = new Card(Card.Suit.SPADES, 14);
		Card card2 = new Card(Card.Suit.HEARTS, 10);
		Card card3 = new Card(Card.Suit.SPADES, 2);
		Card card4 = new Card(Card.Suit.SPADES, 5);
		Card card5 = new Card(Card.Suit.SPADES, 3);
		Card card6 = new Card(Card.Suit.CLUBS, 7);
		Card card7 = new Card(Card.Suit.SPADES, 4);
		Player p = new Player("test");
		p.setCards(0, card1);
		p.setCards(1, card2);
		Card[] table = {card3, card4, card5, card6, card7};
		FreeHoldEm game = new FreeHoldEm(table);
		HandEvaluator he = new HandEvaluator();
		he.findBestHand(game, p);
		Card[] expectedCards = {card1, card3, card5, card7, card4};
		Hand expectedHand = new Hand(expectedCards);
		assertEquals(0, expectedHand.compareTo(p.getHand()));
	}
}
