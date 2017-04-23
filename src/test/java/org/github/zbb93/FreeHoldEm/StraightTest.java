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
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
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
		Player p = new Player("test");
		Card card1 = new Card(Card.Suit.SPADES, 10);
		Card card2 = new Card(Card.Suit.HEARTS, 9);
		Card card3 = new Card(Card.Suit.SPADES, 8);
		Card card4 = new Card(Card.Suit.SPADES, 7);
		Card card5 = new Card(Card.Suit.DIAMONDS, 6);
		Card card6 = new Card(Card.Suit.CLUBS, 3);
		Card card7 = new Card(Card.Suit.SPADES, 2);
		p.setCard(0, card1);
		p.setCard(1, card2);
		FreeHoldEm game = new FreeHoldEm(Lists.newArrayList(card3, card4, card5, card6, card7));
		HandEvaluator.findBestHand(game, p);
		List<Card> expectedCards = Lists.newArrayList(card5, card4, card3, card2, card1);
		Hand expectedHand = new Hand(expectedCards);
		assertEquals(0, expectedHand.compareTo(p.getHand()));
	}
	
	@Test
	public void testAceHighStraight() {
		Card card1 = new Card(Card.Suit.SPADES, 14);
		Card card2 = new Card(Card.Suit.HEARTS, 13);
		Card card3 = new Card(Card.Suit.SPADES, 12);
		Card card4 = new Card(Card.Suit.SPADES, 7);
		Card card5 = new Card(Card.Suit.DIAMONDS, 11);
		Card card6 = new Card(Card.Suit.CLUBS, 5);
		Card card7 = new Card(Card.Suit.SPADES, 10);
		Player p = new Player("test");
		p.setCard(0, card1);
		p.setCard(1, card2);
		Card[] table = {card3, card4, card5, card6, card7};
		FreeHoldEm game = new FreeHoldEm(Lists.newArrayList(card3, card4, card5, card6, card7));
		HandEvaluator.findBestHand(game, p);
		List<Card> expectedCards = Lists.newArrayList(card7, card5, card3, card2, card1);
		Hand expectedHand = new Hand(expectedCards);
		assertEquals(0, expectedHand.compareTo(p.getHand()));
	}
	
	@Test
	public void testAceLowStraight() {
		Card card1 = new Card(Card.Suit.SPADES, 14);
		Card card2 = new Card(Card.Suit.HEARTS, 10);
		Card card3 = new Card(Card.Suit.SPADES, 2);
		Card card4 = new Card(Card.Suit.SPADES, 5);
		Card card5 = new Card(Card.Suit.DIAMONDS, 3);
		Card card6 = new Card(Card.Suit.CLUBS, 7);
		Card card7 = new Card(Card.Suit.SPADES, 4);
		Player p = new Player("test");
		p.setCard(0, card1);
		p.setCard(1, card2);
		Card[] table = {card3, card4, card5, card6, card7};
		FreeHoldEm game = new FreeHoldEm(Lists.newArrayList(card3, card4, card5, card6, card7));
		HandEvaluator.findBestHand(game, p);
		List<Card> expectedCards = Lists.newArrayList(card1, card3, card5, card7, card4);
		Hand expectedHand = new Hand(expectedCards);
		assertEquals(0, expectedHand.compareTo(p.getHand()));
		System.out.println(p.getHand().toString());
	}
}
