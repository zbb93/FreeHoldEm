package edu.uab.cis.zbb.FreeHoldEm;

import static org.junit.Assert.*;
import org.junit.Test;
/*
import application.Card;
import application.FreeHoldEm;
import application.Hand;
import application.HandEvaluator;
import application.Player;
import application.Tuple;*/

public class StraightTest {
	
	@Test
	public void test10to5Straight() {
		HandEvaluator he = new HandEvaluator();
		Player p = new Player("test");		
		Card card1 = new Card("spades", 10);
		Card card2 = new Card("hearts", 9);
		Card card3 = new Card("spades", 8);
		Card card4 = new Card("spades", 7);
		Card card5 = new Card("diamonds", 6);
		Card card6 = new Card("clubs", 3);
		Card card7 = new Card("spades", 2);
		p.setCards(0, card1);
		p.setCards(1, card2);
		Card[] table = {card3, card4, card5, card6, card7};
		FreeHoldEm game = new FreeHoldEm(table);
		he.findBestHand(game, p);
		Card[] expectedCards = {card5, card4, card3, card2, card1};
		Hand expectedHand = new Hand(expectedCards);
		assertEquals(0, expectedHand.compareTo(p.getHand()));
	}
	
	@Test
	public void testAceHighStraight() {
		Card card1 = new Card("spades", 14);
		Card card2 = new Card("hearts", 13);
		Card card3 = new Card("spades", 12);
		Card card4 = new Card("spades", 7);
		Card card5 = new Card("diamonds", 11);
		Card card6 = new Card("clubs", 5);
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
		assertEquals(0, expectedHand.compareTo(p.getHand()));
	}
	
	@Test
	public void testAceLowStraight() {
		Card card1 = new Card("spades", 14);
		Card card2 = new Card("hearts", 10);
		Card card3 = new Card("spades", 2);
		Card card4 = new Card("spades", 5);
		Card card5 = new Card("diamonds", 3);
		Card card6 = new Card("clubs", 7);
		Card card7 = new Card("spades", 4);
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
		System.out.println(p.getHand().toString());
	}
}
