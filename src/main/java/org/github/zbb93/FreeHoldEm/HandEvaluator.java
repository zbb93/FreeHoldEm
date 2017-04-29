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

/**
 * Class to handle evaluation of hands. One publicly exposed method accepts a player and a 
 * collection(specify) of cards. This method then calls the private methods to determine what
 * hand, if any, the player has and then sets the players hand to the best possible hand they can have.
 */
public class HandEvaluator {
	private HandEvaluator() {

	}

  public static void findBestHand(List<Card> cardsOnTable, Player player) {
	  List<Card> cards = Lists.newArrayListWithCapacity(7);
	  cards.addAll(cardsOnTable);
	  cards.addAll(player.getCards());
	  List<Card> sortedCards = sortHand(cards);
	  /*
	    Begin searching for hands (starting with best)
	    If a hand is found the hand is constructed and
	    any necessary high cards are also grabbed
	    If no hand is found a hand is constructed from high cards
	   */
	  List<Card> sfHand = containsStraightFlush(sortedCards);
	  if (sfHand != null) {
		  buildHand(sfHand, player);
		  return;
	  }
	  int index = containsFourOfAKind(sortedCards);
	  if (index >= 0) {
		  List<Card> hand = Lists.newArrayListWithCapacity(5);
		  for (int i = 0; i < 4; i++) {
			  //Grab the four equal cards
			  hand.add(sortedCards.get(index - i));
		  }
		  //Determine next highest card and grab it
		  if (index - 3 == 0) {
			  hand.add(sortedCards.get(index + 1));
		  }
		  else {
			  hand.add(sortedCards.get(0));
		  }
		  buildHand(hand, player);
		  return;
	  }

	  Tuple indices = containsFullHouse(sortedCards);
	  if (indices.x != -1 && indices.y != -1) {
		  List<Card> hand = Lists.newArrayListWithCapacity(5);
		  for (int i = 0; i < 3; i++) {
			  hand.add(sortedCards.get(indices.x - i));
		  }
		  for (int i = 0; i < 2; i++) {
			  hand.add(i + 3, sortedCards.get(indices.y - i));
		  }
		  buildHand(hand, player);
		  return;
	  }

	  indices = containsStraight(sortedCards);
	  //normal straight
	  if (indices.x == 1) {
		  List<Card> hand = Lists.newArrayListWithCapacity(5);
		  for (int i = 0; i < 5; i++) {
			  hand.add(sortedCards.get(indices.y - i));
		  }
		  buildHand(hand, player);
		  return;
	  }
	  //ace low straight
	  else if (indices.x == 2) {
		  List<Card> hand = Lists.newArrayListWithCapacity(5);
		  hand.add(sortedCards.get(0));
		  for (int i = 1; i < 5; i++) {
			  hand.add(sortedCards.get(indices.y - (i - 1)));
		  }
		  buildHand(hand, player);
		  return;
	  }
	  String suit = containsFlush(sortedCards);
	  if (suit != null) {
		  List<Card> hand = Lists.newArrayListWithCapacity(5);
		  int counter = 0;
		  for (Card card : cards) {
			  if (counter == 5)
				  break;
			  if (card.getSuit().equals(suit)) {
				  hand.add(counter, card);
				  counter++;
			  }
		  }
		  buildHand(hand, player);
		  return;
	  }

	  index = containsThreeOfAKind(sortedCards);
	  if (index >= 0) {
		  List<Card> hand = Lists.newArrayListWithCapacity(5);
		  for (int i = 0; i < 3; i++) {
			  hand.add(i, sortedCards.get(index - i));
		  }
		  if (index - 2 == 0) {
			  hand.add(3, sortedCards.get(index + 1));
			  hand.add(4, sortedCards.get(index + 2));
		  }
		  else if (index - 2 == 1) {
			  hand.add(3, sortedCards.get(0));
			  hand.add(4, sortedCards.get(index + 1));
		  }
		  else {
			  hand.add(3, sortedCards.get(0));
			  hand.add(4, sortedCards.get(1));
		  }
		  buildHand(hand, player);
		  return;
	  }

	  indices = containsTwoPair(sortedCards);
	  if (indices.x != -1 && indices.y != -1) {
		  List<Card> hand = Lists.newArrayListWithCapacity(5);
		  for (int i = 0; i < 4; i += 2) {
			  hand.add(i, sortedCards.get(indices.x - i / 2));
			  hand.add(i + 1, sortedCards.get(indices.y - i / 2));
		  }
		  for (int i = 0; i < sortedCards.size(); i++) {
			  if ((indices.x != i && indices.x - 1 != i) && 
					  (indices.y != i && indices.y -1 != i)) {
				  hand.add(4, sortedCards.get(i));
				  break;
			  }
		  }		
		  buildHand(hand, player);
		  return;
	  }

	  index = containsPair(sortedCards);
	  if (index >= 0) {
		  List<Card> hand = Lists.newArrayListWithCapacity(5);
		  for (int i = 0; i < 2; i++) {
			  hand.add(i, sortedCards.get(index - i));
		  }
		  if (index - 1 == 0) {
			  hand.add(2, sortedCards.get(index + 1));
			  hand.add(3, sortedCards.get(index + 2));
			  hand.add(4, sortedCards.get(index + 3));
		  }
		  else if (index - 1 == 1) {
			  hand.add(2, sortedCards.get(0));
			  hand.add(3, sortedCards.get(index + 1));
			  hand.add(4, sortedCards.get(index + 2));
		  }
		  else if (index - 1 == 2) {
			  hand.add(2, sortedCards.get(0));
			  hand.add(3, sortedCards.get(1));
			  hand.add(4, sortedCards.get(index + 1));
		  }
		  else {
			  hand.add(2, sortedCards.get(0));
			  hand.add(3, sortedCards.get(1));
			  hand.add(4, sortedCards.get(2));
		  }
		  buildHand(hand, player);
		  return;
	  }

	  //High card hand
	  List<Card> hand = Lists.newArrayListWithCapacity(5);
	  for (int i = 0; i < 5 && i < sortedCards.size(); i++) {
		  if (sortedCards.get(i) != null) {
			  hand.add(i, sortedCards.get(i));
		  }
	  }
	  buildHand(hand, player);
	}
  
  /**
   * Builds the best possible hand as determined by findBestHand and gives 
   * it to the proper player. Called from findBestHand.
   *    
   * @param hand The best hand found
   * @param player The player the hand belongs to.
   */
  private static void buildHand(List<Card> hand, Player player) {
		player.setHand(new Hand(hand));
  }
  
  /**
   *Sorts the hand high -> low based on value of
   *cards.
   *
   *@param hand The hand to be sorted.
   *@return an array of cards sorted from high to low.
   */
  private static List<Card> sortHand(List<Card> hand) {
    List<Card> sortedList = Lists.newArrayListWithCapacity(hand.size());
		while (!hand.isEmpty()) {
			Card highest = null;
			for (Card card : hand) {
				if (highest == null || card.getValue() > highest.getValue()) {
					highest = card;
				}
			}
			hand.remove(highest);
			sortedList.add(highest);
		}
    return sortedList;
  }
  
  /**
   * Tests whether a hand is a straight flush.    
   * @param hand The hand to be tested.
   * @return A tuple of (-1,-1) if there is no straight flush (1, index) if there
   * is a normal straight. Index here is the index of the lowest card in the
   * straight. If an ace low straight is present return (2, index).
   *
private Tuple containsStraightFlush(Card[] hand) {
  Tuple index = containsStraight(hand);
  if (index.y > 0 && index.x == 1) {
    //normal straight
    Card[] straight = new Card[5];
    for (int i = 0; i < straight.length; i++) {
      straight[i] = hand[index.y - i];
    } 
    if(containsFlush(straight) != null)
      return (new Tuple(1, index.y));
  }
  else if (index.y > 0 && index.x ==2) {
    //Ace low straight
    Card[] straight = new Card[5];
    straight[0] = hand[0];
    for (int i = 1; i < straight.length; i++) {
      straight[i] = hand[index.y - i];
    }
    if (containsFlush(straight) != null)
      return (new Tuple(2, index.y));
  }
  return (new Tuple(-1, -1));
}*/
  private static List<Card> containsStraightFlush(List<Card> hand) {
	  String suit = containsFlush(hand);
	  if (suit != null) {
		  List<Card> matchingSuit = Lists.newLinkedList();
		  for (Card card : hand) {
			  if (card.getSuit().equals(suit)) {
				  matchingSuit.add(card);
			  }
		  }

		  List<Card> sortedMatches = sortHand(matchingSuit);
		  Tuple index = containsStraight(sortedMatches);
		  if (index.y > 0 && index.x == 1) {
			  List<Card> newHand = Lists.newArrayListWithCapacity(5);
			  for (int i = 0; i < 5; i++) {
				  newHand.add(sortedMatches.get(index.y - i));
			  }
			  return newHand;
		  } else if(index.y > 0 && index.x == 2) {
			  List<Card> newHand = Lists.newArrayListWithCapacity(5);
			  newHand.add(sortedMatches.get(0));
			  for (int i = 1; i < 5; i++) {
				  newHand.add(sortedMatches.get(index.y - (i - 1)));
			  }
			  return newHand;
		  } else {
			  return null;			
		  }
	  } else {
		  return null;
	  }
  }
  /**
   * Tests whether a hand contains four of the same card.   
   * @param hand The hand to be tested.
   * @return The index of the final card from the four of a kind.
   */
private static int containsFourOfAKind(List<Card> hand) {
     
  int index = 0;
  int matches = 0;
  for (int i = 1; i < hand.size(); i++) {
    if (hand.get(i) == null)
      break;
    if (hand.get(i).getValue() == hand.get(i - 1).getValue()) {
      matches += 1;
      index = i;
    }
    else if (matches ==3) {
      break;
    }
    else {
      matches = 0;
    }
  }
  if (matches == 3)
    return index;
  else
    return -1;
}
  /**
   * Tests whether a hand contains a full house.    
   * @param hand The hand to be tested.
   * @return A tuple of (-1,-1) if there is no full house. If there is a full
   * house a non negative tuple is returned. The first index corresponds to the
   * last occurrence of the triple. The second index is the last occurrence of 
   * the double.
   */
private static Tuple containsFullHouse(List<Card> hand) {
     
  int match1 = 0;
  int match1Val = 0;
  int index1 = -1;
  int match2 = 0;
  int index2 = -1;
  for (int i = 1; i < hand.size(); i++) {
    if (hand.get(i) == null)
      break;
    if (hand.get(i).getValue() == hand.get(i - 1).getValue() && ((match1 < 1) || (match1Val == hand.get(i).getValue()))) {
      match1Val = hand.get(i).getValue();
      index1 = i;
      match1++;
    }
    else if (hand.get(i).getValue() == hand.get(i - 1).getValue() && hand.get(i).getValue() != match1Val) {
      index2 = i;
      match2++;
    }
    else if ((match1 == 2 && match2 == 1) || (match1 == 1 && match2 == 2)) {
      break;
    }
    else if (match1 >= 1) {
      match2 = 0;
    }
    else {
      match1 = 0;
    }
  }
  /*
   * Return tuple containing indices of pairs
     * Three of a kind comes first
     * Pair is second element of tuple
     */
  if (match1 == 2 && match2 == 1)
    return (new Tuple(index1, index2));
  else if (match2 ==2 && match1 == 1)
    return (new Tuple(index2, index1));
  else
    return (new Tuple(-1, -1));
}
    
     
  /**
   * Tests whether a hand is a straight.    
   * @param hand The hand to be tested.
   * @return A tuple of (-1,-1) if there is no straight (1, index) if there
   * is a normal straight. Index here is the index of the lowest card in the
   * straight. If an ace low straight is present return (2, index).
   */
private static Tuple containsStraight(List<Card> hand) {
  int consecutiveVals = 0;
  int index = 0;
  for (int i = 1; i < hand.size(); i++) {
    if (hand.get(i) == null)
      break;
    if ((hand.get(i).getValue() == hand.get(i - 1).getValue() - 1)) {
      consecutiveVals += 1;
      index = i;
    }
    else if (hand.get(i).getValue() == hand.get(i - 1).getValue()) {
    	// todo why is continue last statement in the loop? can this branch be removed?
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
      hand.get(0).getValue() == 14 &&
      hand.get(index).getValue() == 2) {
    return (new Tuple(2, index)); 
  }
  else
    return (new Tuple(-1, -1));
}
  /**
   * Determines whether a hand contains a flush.   
   * @param hand The hand to be tested.
   * @return If there is a flush return a String representation of the
   * suit. If there is not a flush return null.
   */
private static String containsFlush(List<Card> hand) {
     
  int hearts = 0;
  int spades = 0;
  int clubs = 0;
  int diamonds = 0;
  for (Card card : hand) {
    if ("hearts".equals(card.getSuit())) {
			hearts++;
		} else if ("spades".equals(card.getSuit())) {
			spades++;
		} else if ("clubs".equals(card.getSuit())) {
			clubs++;
		} else if ("diamonds".equals(card.getSuit())) {
			diamonds++;
		}
  }
  if (hearts >= 5) 
    return "hearts";
  else if (spades >= 5)
    return "spades";
  else if (clubs >= 5)
    return "clubs";
  else if (diamonds >= 5)
    return "diamonds";
  else
    return null;
}
  /**
   * Tests whether a hand contains a three of a kind.   
   * @param hand The hand to be tested.
   * @return An integer representing the final index of the triple. If a triple
   * is not present return -1.
   */
private static int containsThreeOfAKind(List<Card> hand) {
     
  int matches = 0;
  int index = -1;
  for (int i = 1; i < hand.size(); i++) {
    if (hand.get(i).getValue() == hand.get(i - 1).getValue()) {
      matches++;
      index = i;
    }
    else if (matches == 3)
      break;
    else
      matches = 0;
  }
  if (matches == 3)
    return index;
  else
    return -1;
}
  /**
   * Tests whether a hand contains two pairs.   
   * @param hand The hand to be tested.
   * @return If there is a pair return a tuple representing the indices. 
   * The index of the final element of the greater pair should be the first 
   * value of the tuple and the index of the final element of the smaller 
   * pair should be the second value of the tuple. If there are not two pairs
   * return (-1,-1).
   */
private static Tuple containsTwoPair(List<Card> hand) {
     
  int match1 = 0;
  int index1 = -1;
  int match2 = 0;
  int index2 = -1;
  for (int i = 1; i < hand.size(); i++) {
    if (hand.get(i).getValue() == hand.get(i - 1).getValue() && match1 != 1) {
      match1++;
      index1 = i;
    }
    else if (hand.get(i).getValue() == hand.get(i - 1).getValue() && match1 == 1) {
      match2++;
      index2 = i;
    }
    else if (match1 == 1)
      match2 = 0;
    else
      match1 = 0;
  }
  if (match1 == 1 && match2 == 1)
    return (new Tuple(index1, index2));
  else
    return (new Tuple(-1, -1));
}
  /**
   * Tests whether a hand contains a pair.   
   * @param hand The hand to be tested.
   * @return If the hand contains a pair return the index of the final element
   * of the pair. If there is not a pair return -1.
   */
private static int containsPair(List<Card> hand) {
     
  int index = -1;
  for (int i = 1; i < hand.size(); i++) {
    if (hand.get(i) == null)
      break;
    if (hand.get(i).getValue() == hand.get(i - 1).getValue()) {
      index = i;
    }
  }
  return index;
}
}


