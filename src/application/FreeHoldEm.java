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
	private Player[] players;
	/**
	 * Community cards currently in play. Note this array does not contain the
	 * cards that each player has been dealt
	 */
	private Card[] cardsOnTable = new Card[5];
	/**
	 * Integer used to describe the current phase of the game. Used by the AI to
	 * determine when the AI should fold when they have no hand.
	 */
	private int round = 1;
	/**
	 * Integer representing the current amount of chips that have been bet on
	 * this hand.
	 */
	private int pot = 0;
	/**
	 * Scanner used to get input from user.
	 */
	private static Scanner sc = new Scanner(System.in);
	/**
	 * Array to store the current high scores. It is initialized in assignHighScores function.
	 */
	private HighScore[] highScores;
    /**
     * Handles highScore file I/O.   
     */
	private HighScoreFile highScoreFile;
	
	private HandEvaluator he = new HandEvaluator();
	
	public FreeHoldEm(int numPlayers) {
		//Creates a new file or loads a existing file. 
		highScoreFile = new HighScoreFile("high_scores.dat");
		//Creates dummy highscores if there are no previous highscores.
		highScoreFile.writeDummyHighScoresIfNecessary();
		assignHighScores(highScoreFile);
		players = new Player[numPlayers];
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
	private void assignHighScores(HighScoreFile hf) {

	  ArrayList<HighScore> list = hf.getHighScoreList();
	  //Create a dynamic highscore array based on loaded highscores.
	  highScores = new HighScore[list.size()];
	  int i = 0;
	  for (HighScore highScore : list) {
	    highScores[i] = highScore;
	    i++;
	  }
	}

	private void play() {
		Deck deck = new Deck();
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
				he.findBestHand(this, players[i]);
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
	private void startNewGame() {
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
	 * Prompts user for input and places user bet. Once the AI bets are determined
	 * with determineAIBet they are placed as well. If a player or CPU bets 0 chips
	 * this is considered folding.
	 */
	private void takeBets() {
		
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
	private int determineAIBet(Player player) {
		he.findBestHand(this, player);
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
	 * Called from startNewGame() if player declines to play another round or is
	 * out of chips. This method determines if the players score is a high score
	 * and if it is the writeNewScore method is called. If the player has not
	 * made a high score the high scores are displayed and the game exits.
	 */
	private void endGame() {
		//As they have sorted to descending order.
		if (players[0].getChips() > highScores[highScores.length-1].getScore()) {
			writeNewScore();
		}
		else {
			//display high scores
			System.out.println(highScoreFile.toString());
		}
		System.exit(0);
		
	}
	/**
	 * This method creates a new HighScore for the player and inserts it into the 
	 * proper position in the highScores array. The scores are then displayed and
	 * written to the high_scores.dat file.
	 */
	private void writeNewScore() {
		System.out.printf("High Score!\nEnter your name: ");
		String name = sc.next();
		/*
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
		}*/
		HighScore newHighScore = new HighScore(name, players[0].getChips());
		highScoreFile.addHighScore(newHighScore);
		//Sorts HighScores to descending order
		highScoreFile.sortHighScores();
		//Write highScores into file.
		highScoreFile.writePlayersIntoFile();
		System.out.println(highScoreFile.toString());
	}
	
	public Card[] getCardsOnTable() {
	  return this.cardsOnTable;
	}
}
