package application;
/*
 * Class to handle evaluation of hands. One publicly exposed method accepts a player and a 
 * collection(specify) of cards. This method then calls the private methods to determine what
 * hand, if any, the player has and then sets the players hand to the best possible hand they can have.
 */
public class HandEvaluator {
	public HandEvaluator() {

	}

  public void findBestHand(FreeHoldEm game, Player player) {
	  Card[] cards = new Card[7];
	  Card[] cardsOnTable = game.getCardsOnTable();
	  Card[] playerCards = player.getCards();
	  cards[0] = playerCards[0];
	  cards[1] = playerCards[1];
	  for (int i = 2; i < cards.length; i++) {
		  cards[i] = cardsOnTable[i - 2];
	  }
	  Card[] sortedCards = sortHand(cards);
	  /*
	   *Begin searching for hands (starting with best)
	   *If a hand is found the hand is constructed and
	   *any necessary high cards are also grabbed
	   *If no hand is found a hand is constructed from high cards
	   */
	  Tuple indices = containsStraightFlush(sortedCards);
	  //normal straight
	  if (indices.x == 1 && indices.y > 0) {
		  Card[] hand = new Card[5];
		  for (int i = 0; i < hand.length; i++) {
			  hand[i] = sortedCards[indices.y - i];
		  }
		  buildHand(hand, player);
		  return;
	  }
	  //ace low straight
	  else if (indices.x == 2 && indices.y > 0) {
		  Card[] hand = new Card[5];
		  hand[0] = sortedCards[0];
		  for (int i = 1; i < hand.length; i++) {
			  hand[i] = sortedCards[indices.y - (i -1)];
		  }
		  buildHand(hand, player);
		  return;
	  }
	  int index = containsFourOfAKind(sortedCards);
	  if (index >= 0) {
		  Card[] hand = new Card[5];
		  for (int i = 0; i < 4; i++) {
			  //Grab the four equal cards
			  hand[i] = sortedCards[index - i];
		  }
		  //Determine next highest card and grab it
		  if (index - 3 == 0) {
			  hand[4] = sortedCards[index + 1];
		  }
		  else {
			  hand[4] = sortedCards[0];
		  }
		  buildHand(hand, player);
		  return;
	  }

	  indices = containsFullHouse(sortedCards);
	  if (indices.x != -1 && indices.y != -1) {
		  Card[] hand = new Card[5];
		  for (int i = 0; i < 3; i++) {
			  hand[i] = sortedCards[indices.x - i];
		  }
		  for (int i = 0; i < 2; i++) {
			  hand[i + 3] = sortedCards[indices.y - i];
		  }
		  buildHand(hand, player);
		  return;
	  }

	  indices = containsStraight(sortedCards);
	  //normal straight
	  if (indices.x == 1) {
		  Card[] hand = new Card[5];
		  for (int i = 0; i < hand.length; i++) {
			  System.out.printf("Player: %s", player.getName());
			  System.out.printf("Tuple: %d %d\n", indices.x, indices.y);
			  hand[i] = sortedCards[indices.y - i];
		  }
		  buildHand(hand, player);
		  return;
	  }
	  //ace low straight
	  else if (indices.x == 2) {
		  Card[] hand = new Card[5];
		  hand[0] = sortedCards[0];
		  for (int i = 1; i < hand.length; i++) {
			  hand[i] = sortedCards[indices.y - (i - 1)];
		  }
		  buildHand(hand, player);
		  return;
	  }

	  String suit = containsFlush(sortedCards);
	  if (suit != null) {
		  Card[] hand = new Card[5];
		  int counter = 0;
		  for (int i = 0; i < sortedCards.length; i++) {
			  if (counter == 5)
				  break;
			  if (sortedCards[i].getSuit() == suit) {
				  hand[counter] = sortedCards[i];
				  counter++;
			  }
		  }
		  buildHand(hand, player);
		  return;
	  }

	  index = containsThreeOfAKind(sortedCards);
	  if (index >= 0) {
		  Card[] hand = new Card[5];
		  for (int i = 0; i < 3; i++) {
			  hand[i] = sortedCards[index - i];
		  }
		  if (index - 2 == 0) {
			  hand[3] = sortedCards[index + 1];
			  hand[4] = sortedCards[index + 2];
		  }
		  else if (index - 2 == 1) {
			  hand[3] = sortedCards[0];
			  hand[4] = sortedCards[index + 1];
		  }
		  else {
			  hand[3] = sortedCards[0];
			  hand[4] = sortedCards[1];
		  }
		  buildHand(hand, player);
		  return;
	  }

	  indices = containsTwoPair(sortedCards);
	  if (indices.x != -1 && indices.y != -1) {
		  Card[] hand = new Card[5];
		  for (int i = 0; i < 4; i += 2) {
			  hand[i] = sortedCards[indices.x - i / 2];
			  hand[i + 1] = sortedCards[indices.y - i / 2];            		 
		  }
		  for (int i = 0; i < sortedCards.length; i++) {
			  if ((indices.x != i && indices.x - 1 != i) && 
					  (indices.y != i && indices.y -1 != i)) {
				  hand[4] = sortedCards[i];
				  break;
			  }
		  }		
		  buildHand(hand, player);
		  return;
	  }

	  index = containsPair(sortedCards);
	  if (index >= 0) {
		  Card[] hand = new Card[5];
		  for (int i = 0; i < 2; i++) {
			  hand[i] = sortedCards[index - i];
		  }
		  if (index - 1 == 0) {
			  hand[2] = sortedCards[index + 1];
			  hand[3] = sortedCards[index + 2];
			  hand[4] = sortedCards[index + 3];
		  }
		  else if (index - 1 == 1) {
			  hand[2] = sortedCards[0];
			  hand[3] = sortedCards[index + 1];
			  hand[4] = sortedCards[index + 2];
		  }
		  else if (index - 1 == 2) {
			  hand[2] = sortedCards[0];
			  hand[3] = sortedCards[1];
			  hand[4] = sortedCards[index + 1];
		  }
		  else {
			  hand[2] = sortedCards[0];
			  hand[3] = sortedCards[1];
			  hand[4] = sortedCards[2];
		  }
		  buildHand(hand, player);
		  return;
	  }

	  //High card hand
	  Card[] hand = new Card[5];
	  for (int i = 0; i < hand.length; i++) {
		  if (sortedCards[i] != null) {
			  hand[i] = sortedCards[i];  
		  }
	  }
	  buildHand(hand, player);
	  return;
  }
  
  /**
   * Builds the best possible hand as determined by findBestHand and gives 
   * it to the proper player. Called from findBestHand.
   *    
   * @param hand The best hand found
   * @param player The player the hand belongs to.
   */
  private void buildHand(Card[] hand, Player player) {
    //Check for null values in hand array
    int nullAt = -1;
    for (int i = 0; i < hand.length; i++) {
      if (hand[i] == null) {
        nullAt = i;
        break;
      }
    }
    if (nullAt > 0) {
      Card[] withoutNull = new Card[nullAt];
      for (int i = 0; i < withoutNull.length; i++) {
        withoutNull[i] = hand[i];
      }
      Hand playerHand = new Hand(withoutNull);
      player.setHand(playerHand);
    }
    else {
      Hand playerHand = new Hand(hand);
      player.setHand(playerHand);
    }
  }
  
  /**
   *Sorts the hand high -> low based on value of
   *cards.
   *
   *@param hand The hand to be sorted.
   *@return an array of cards sorted from high to low.
   */
  private Card[] sortHand(Card[] hand) {
    
    for (int i = 1; i < hand.length; i++) {
      int j = i;
      if(hand[i] == null) {
        break;
      }
      while (j > 0 && hand[j-1].getValue() < hand[j].getValue()) {
        Card temp = hand[j];
        hand[j] = hand[j-1];
        hand[j-1] = temp;
        j = j - 1;
      }
    }
    return hand;
  }
  
  /**
   * Tests whether a hand is a straight flush.    
   * @param hand The hand to be tested.
   * @return A tuple of (-1,-1) if there is no straight flush (1, index) if there
   * is a normal straight. Index here is the index of the lowest card in the
   * straight. If an ace low straight is present return (2, index).
   */
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
}
  /**
   * Tests whether a hand contains four of the same card.   
   * @param hand The hand to be tested.
   * @return The index of the final card from the four of a kind.
   */
private int containsFourOfAKind(Card[] hand) {
     
  int index = 0;
  int matches = 0;
  for (int i = 1; i < hand.length; i++) {
    if (hand[i] == null)
      break;
    if (hand[i].getValue() == hand[i - 1].getValue()) {     
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
private Tuple containsFullHouse(Card[] hand) {
     
  int match1 = 0;
  int match1Val = 0;
  int index1 = -1;
  int match2 = 0;
  int index2 = -1;
  for (int i = 1; i < hand.length; i++) {
    if (hand[i] == null)
      break;
    if (hand[i].getValue() == hand[i - 1].getValue() && ((match1 < 1) || (match1Val == hand[i].getValue()))) {
      match1Val = hand[i].getValue();
      index1 = i;
      match1++;
    }
    else if (hand[i].getValue() == hand[i - 1].getValue() && hand[i].getValue() != match1Val) {
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
private Tuple containsStraight(Card[] hand) {
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
  /**
   * Determines whether a hand contains a flush.   
   * @param hand The hand to be tested.
   * @return If there is a flush return a String representation of the
   * suit. If there is not a flush return null.
   */
private String containsFlush(Card[] hand) {
     
  int hearts = 0;
  int spades = 0;
  int clubs = 0;
  int diamonds = 0;
  for (int i = 0; i < hand.length; i++) {
    if (hand[i] == null)
      break;
    if (hand[i].getSuit() == "hearts")
      hearts++;
    else if (hand[i].getSuit() == "spades")
      spades++;
    else if (hand[i].getSuit() == "clubs")
      clubs++;
    else if (hand[i].getSuit() == "diamonds")
      diamonds++;
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
private int containsThreeOfAKind(Card[] hand) {
     
  int matches = 0;
  int index = -1;
  for (int i = 1; i < hand.length; i++) {
    if (hand[i] == null)
      break;
    if (hand[i].getValue() == hand[i - 1].getValue()) {   
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
private Tuple containsTwoPair(Card[] hand) {
     
  int match1 = 0;
  int index1 = -1;
  int match2 = 0;
  int index2 = -1;
  for (int i = 1; i < hand.length; i++) {
    if (hand[i] == null)
      break;
    if (hand[i].getValue() == hand[i - 1].getValue() && match1 != 1) {
      match1++;
      index1 = i;
    }
    else if (hand[i].getValue() == hand[i - 1].getValue() && match1 == 1) {
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
private int containsPair(Card[] hand) {
     
  int index = -1;
  for (int i = 1; i < hand.length; i++) {
    if (hand[i] == null)
      break;
    if (hand[i].getValue() == hand[i - 1].getValue()) {
      index = i;
    }
  }
  return index;
}
}


