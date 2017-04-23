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
		p.setCard(0, card1);
		p.setCard(1, card2);
		FreeHoldEm game = new FreeHoldEm(Lists.newArrayList(card3, card4, card5, card6, card7));
		HandEvaluator.findBestHand(game, p);
		List<Card> expectedCards = Lists.newArrayList(card7, card5, card3, card2, card1);
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
		p.setCard(0, card1);
		p.setCard(1, card2);
		FreeHoldEm game = new FreeHoldEm(Lists.newArrayList(card3, card4, card5, card6, card7));
		HandEvaluator.findBestHand(game, p);
		List<Card> expectedCards = Lists.newArrayList(card7, card5, card3, card2, card1);
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
		p.setCard(0, card1);
		p.setCard(1, card2);
		FreeHoldEm game = new FreeHoldEm(Lists.newArrayList(card3, card4, card5, card6, card7));
		HandEvaluator.findBestHand(game, p);
		List<Card> expectedCards = Lists.newArrayList(card1, card3, card5, card7, card4);
		Hand expectedHand = new Hand(expectedCards);
		assertEquals(0, expectedHand.compareTo(p.getHand()));
	}
}
