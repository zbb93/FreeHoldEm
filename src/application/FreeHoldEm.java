package application;
/*
 * FreeHoldEm
 * Copyright 2015 by Zachary Bowen
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
       
import java.io.IOException;
import java.util.ArrayList;
/*
 * TODO: Test ace low implementation	  
 * TODO: Create window containing background image (poker table)
 * TODO: Use player personality to randomize computer controlled 
 *       player bets.
 * TODO: Improve AI logic so that computer controlled players are
 *       able to ignore hands based solely on cards on the table
 *       (example: a pair of fives on the river shouldn't keep you
 *       from folding if there is nothing else in your hand.)
 */
import java.util.Scanner;
/**
 * @author Zachary Bowen
 */
public class FreeHoldEm {
	
    /**
     * Array of players. As players run out of chips they are not removed from the
     * array, but they are permanently folded.   
     */
	private static Player[] players;
	/**
	 * Community cards currently in play. Note this array does not contain the
	 * cards that each player has been dealt
	 */
	private static Card[] cardsOnTable = new Card[5];
	/**
	 * Integer used to describe the current phase of the game. Used by the AI to
	 * determine when the AI should fold when they have no hand.
	 */
	private static int round = 1;
	/**
	 * Integer representing the current amount of chips that have been bet on
	 * this hand.
	 */
	private static int pot = 0;
	/**
	 * Scanner used to get input from user.
	 */
	private static Scanner sc = new Scanner(System.in);
	/**
	 * Array to store the current high scores. It is initialized in assignHighScores function.
	 */
	private static HighScore[] highScores;
    /**
     * Handles highScore file I/O.   
     */
	private static HighScoreFile highScoreFile;
	
	
	public static void main(String[] args) throws IOException {
		//Creates a new file or loads a existing file. 
		highScoreFile = new HighScoreFile("high_scores.dat");
		//Creates dummy highscores if there are no previous highscores.
		highScoreFile.writeDummyHighScoresIfNecessary();
		assignHighScores(highScoreFile);
		
		System.out.print("Number of players (2 - 8): ");
		int numberPlayers = sc.nextInt();                
		players = new Player[numberPlayers];
		players[0] = new Player("human");
		for (int i = 1; i < players.length; i++) {
			players[i] = new Player("CPU" +
					String.valueOf(i));
		}
		play();
	}
	/**
	 * Assigns loaded highscores into highScores array.
	 * @param hf = HighScoreFile
	 */
    private static void assignHighScores(HighScoreFile hf) {

    	ArrayList<HighScore> list = hf.getHighScoreList();
    	//Create a dynamic highscore array based on loaded highscores.
    	highScores = new HighScore[list.size()];
    	int i = 0;
    	for (HighScore highScore : list) {
			highScores[i] = highScore;
			i++;
		}
    }
	private static void play() {
		Deck deck = new Deck();
		deal(deck);
	}
       
	private static void deal(Deck deck) {
		int j = 0;
		//Deal player hands
		for (int i = 0; i < players.length; i++) {
			if (players[i].checkFold()) {
				System.out.printf("%s is out of chips!", players[i].getName());
			}
			else {
				players[i].setCards(0, deck.getCard(j));
				players[i].setCards(1, deck.getCard(j + 1));
				j = j + 2; 
				if (i == 0) {
					System.out.println("Your hand: " +
							players[i].toString()); 
				} 
				else {
					System.out.println(players[i].getName() +
							": " + players[i].toString()); 
				}      
			}
		}
		Card[] yourHand = players[0].getCards();
		System.out.printf("\n\n");
		takeBets();
		//Burn card before flop
		j++;
		//Deal flop                
		cardsOnTable[0] = deck.getCard(j); 
		cardsOnTable[1] = deck.getCard(j + 1); 
		cardsOnTable[2] = deck.getCard(j + 2);
		System.out.printf("Your Cards: %s, %s\n", yourHand[0].toString(), 
				yourHand[1].toString());
		System.out.printf("Flop: %s, %s, %s\n", cardsOnTable[0], 
				cardsOnTable[1], cardsOnTable[2]);
		System.out.println("Cash in pot: " + pot);
		round++;
		takeBets();
		System.out.printf("\n\n");
		//Burn card before turn
		j += 4;
		cardsOnTable[3] = deck.getCard(j);
		System.out.printf("Your Cards: %s, %s\n", yourHand[0].toString(), yourHand[1].toString());
		System.out.printf("Turn: %s, %s, %s, %s\n", cardsOnTable[0], cardsOnTable[1], 
				cardsOnTable[2], cardsOnTable[3]);
		System.out.println("Cash in pot: " + pot);
		round++;
		takeBets();
		System.out.printf("\n\n");
		//Burn card before river
		j += 2;                
		cardsOnTable[4] = deck.getCard(j);
		System.out.printf("Your Cards: %s, %s\n", yourHand[0].toString(), yourHand[1].toString());
		System.out.printf("River: %s, %s, %s, %s, %s\n", cardsOnTable[0], cardsOnTable[1], 
				cardsOnTable[2], cardsOnTable[3], cardsOnTable[4]);
		System.out.println("Cash in pot: " + pot);
		round++;
		takeBets();
		System.out.printf("\n\n");
		//Score player hands
		for (int i = 0; i < players.length; i++) {
			if (!(players[i].checkFold()))
				findBestHand(players[i]);
		}
		//Pick winner
		//Ensure a folded player cannot be chosen as the initial winner
		//TODO: Handle ties (split winnings?)
		int firstNotFoldedPlayer = -1;
		for (int i = 0; i < players.length; i++) {
			if (!(players[i].checkFold())) {
				firstNotFoldedPlayer = i;
				break;
			}
		}
		if (firstNotFoldedPlayer < 0) {
			System.out.println("Everyone folded!");
			startNewGame();
		}
		Player winner = players[firstNotFoldedPlayer];
		for (int i = firstNotFoldedPlayer + 1; i < players.length; i++) {
			if (players[i].checkFold()) {
				continue;
			}
			if (winner.getHand().compareTo(players[i].getHand()) == -1) {
				winner = players[i];
			}
		}
		winner.setChips(winner.getChips() + pot);
		System.out.printf("\n%s wins with a \n%s\nWinnings: %d\n", winner.getName(),
				winner.getHand().toString(), pot);
		startNewGame();
	}
    /**
     * Called at the end of each hand. If a player has folded, but still has
     * chips they are allowed back into the next hand. If a player has no chips
     * their fold flag is set to prevent them from playing in future games. 
     */
	private static void startNewGame() {
		pot = 0;
		for (int i = 0; i < players.length; i++) {
			if (players[i].getChips() > 0) {
				players[i].unFold();
			}
			else {
				players[i].fold();
			}
		}
		System.out.print("Would you like to play another hand? ");
		String ans = sc.next();
		if ((ans.equals("yes") || ans.equals("Yes")) && players[0].getChips() > 0) {
			round = 1;
			play();	
		}
		else if (players[0].getChips() <= 0) {
			System.out.println("You are out of chips!");
			//TODO: high score stuff
			endGame();
		}
		else {
			//TODO: high score stuff
			endGame();
		}
	}
	/**
	 * Runs player hand through all hand determination methods
	 * (from best to worst) to determine what hand the player has.
	 * Once the hand has been determined the player is given the hand.
	 * At any one time, the player should have the best possible hand available
	 * stored as their hand. This method handles most of the logic related to the
	 * construction of the best possible hand. buildHand merely gives the selected
	 * cards to the player as a hand.
	 * 
	 * @param player the player who's hand is to be evaluated
	 */	
	private static void findBestHand(Player player) {
		Card[] cards = new Card[7];
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
	private static void buildHand(Card[] hand, Player player) {
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
	 * Prompts user for input and places user bet. Once the AI bets are determined
	 * with determineAIBet they are placed as well. If a player or CPU bets 0 chips
	 * this is considered folding.
	 */
	private static void takeBets() {
		
		if ((!(players[0].checkFold())) && players[0].getChips() > 0) {
			System.out.println("Your chips: " + players[0].getChips());
			System.out.print("Place your bet (0 to fold): ");
			int playerBet = sc.nextInt();
			if (playerBet > 0) {
				pot += playerBet;
				players[0].setChips(players[0].getChips() - playerBet);
			}
			else {
				players[0].fold();
			}
		}
		else if (players[0].getChips() <= 0) {
			System.out.println("You are out of chips!");
		}
		for (int i = 1; i < players.length; i++) {
			if ((!(players[i].checkFold())) && players[i].getChips() > 0) {
				int bet = determineAIBet(players[i]);
				if (bet == 0) {
					System.out.println(players[i].getName() +"Folded!");
					players[i].fold();
				}
				pot += bet;
				players[i].setChips(players[i].getChips() - bet);
			}
		}
	}
	/**
	 * Determines the value of an AI's hand. The value is taken from the Hand class.
	 * Based on the value the AI places a certain bet or folds.
	 * @param player The AI that is currently betting.
	 * @return an integer value representing the AI's bet.
	 */
	private static int determineAIBet(Player player) {
		findBestHand(player);
		int handVal = player.getHand().quickVal();
		//Full house or better, bet big
		if (handVal > 7) {
			return 100;
		}
		//Tree of a kind - Straight
		else if (handVal > 3) {
			return 65;
		}
		//Two pair - Flush
		else if (handVal > 1) {
			return 30;
		}
		else if (round > 2){
			return 0;
		}
		else {
			return 10;
		}
   	    	 
	}
	/**
	 *Sorts the hand high -> low based on value of
	 *cards.
	 *
	 *@param hand The hand to be sorted.
	 *@return an array of cards sorted from high to low.
	 */
	private static Card[] sortHand(Card[] hand) {
		
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
	private static Tuple containsStraightFlush(Card[] hand) {
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
	private static int containsFourOfAKind(Card[] hand) {
       
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
	private static Tuple containsFullHouse(Card[] hand) {
       
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
    /**
     * Determines whether a hand contains a flush.   
     * @param hand The hand to be tested.
     * @return If there is a flush return a String representation of the
     * suit. If there is not a flush return null.
     */
	private static String containsFlush(Card[] hand) {
       
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
	private static int containsThreeOfAKind(Card[] hand) {
       
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
	private static Tuple containsTwoPair(Card[] hand) {
       
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
	private static int containsPair(Card[] hand) {
       
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
	/**
	 * Called from startNewGame() if player declines to play another round or is
	 * out of chips. This method determines if the players score is a high score
	 * and if it is the writeNewScore method is called. If the player has not
	 * made a high score the high scores are displayed and the game exits.
	 */
	private static void endGame() {
		//As they have sorted to descending order.
		if (players[0].getChips() > highScores[0].getScore()) {
			writeNewScore();
		}
		else {
			//display high scores
			for (int i = 0; i < highScores.length; i++) {
				System.out.println(highScores[i].toString());
			}
		}
	}
	/**
	 * This method creates a new HighScore for the player and inserts it into the 
	 * proper position in the highScores array. The scores are then displayed and
	 * written to the high_scores.dat file.
	 */
	private static void writeNewScore() {
		System.out.printf("High Score!\nEnter your name: ");
		String name = sc.next();
		HighScore prev = new HighScore(name, players[0].getChips());
		for (int i = 0; i < highScores.length; i++) {
			if (prev.getScore() > highScores[i].getScore()) {
				HighScore tmp = highScores[i];
				highScores[i] = prev;
				prev = tmp;
			}
		}
		for (int i = 0; i < highScores.length; i++) {
			System.out.println(highScores[i].toString());
		}
		highScoreFile.addHighScore(prev);
		//Sorts HighScores to descending order
		highScoreFile.sortHighScores();
		//Write highScores into file.
		highScoreFile.writePlayersIntoFile();
	}
}
