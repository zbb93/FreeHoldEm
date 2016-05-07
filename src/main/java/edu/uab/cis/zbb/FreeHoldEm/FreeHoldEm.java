package edu.uab.cis.zbb.FreeHoldEm;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;
/**
 * @author Zachary Bowen
 */
public class FreeHoldEm {
	enum State {
		FIRST, FLOP, TURN, RIVER
	}
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
	//private int round = 1;
	private State round;
	/**
	 * Integer representing the current amount of chips that have been bet on
	 * this hand.
	 */
	private int pot = 0;
	/**
	 * Scanner used to get input from user.
	 */
	private static Scanner sc = new Scanner(System.in);

	
	private HandEvaluator he = new HandEvaluator();
	
	private Deck deck = new Deck();
	
	private boolean[] blinds;
	
	private int currentBet = 0;
	private int bigBlindPlayer = 1;
	private int smallBlindPlayer = 0;
	private final int BIG_BLIND = 10;
	private final int LITTLE_BLIND = 5;
	
	/**
	 * Constructor used for testing purposes. The game will be initialized with
	 * the cards passed to the constructor.
	 * @param cards - Array of cards representing cards currently on the table.
	 */
	public FreeHoldEm(Card[] cards) {
		this.cardsOnTable = cards;
	}
	
	public FreeHoldEm(int numPlayers) {
		players = new Player[numPlayers];
		players[0] = new Player("human");
		for (int i = 1; i < players.length; i++) {
			players[i] = new Player("CPU" +
					String.valueOf(i));
		}
		blinds = new boolean[players.length];
		blinds[0] = true;
		blinds[1] = true;
		round = State.FIRST;
		//play();
	}
	
	public void dealHands() {
		for (int i = 0; i < players.length; i++) {
			if (players[i].checkFold()) {
				System.out.printf("%s is out of chips!", players[i].getName());
			}
			else {
				players[i].setCards(0, deck.getNextCard());
				players[i].setCards(1, deck.getNextCard());
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
	}
	
	public void dealFlop() {
		round = State.FLOP;
		cardsOnTable[0] = deck.getNextCard(); 
		cardsOnTable[1] = deck.getNextCard(); 
		cardsOnTable[2] = deck.getNextCard();
		Card[] yourHand = players[0].getCards();
		System.out.printf("Your Cards: %s, %s\n", yourHand[0].toString(), 
				yourHand[1].toString());
		System.out.printf("Flop: %s, %s, %s\n", cardsOnTable[0], 
				cardsOnTable[1], cardsOnTable[2]);
		System.out.println("Cash in pot: " + pot);
	}
	
	public void dealTurn() {
		round = State.TURN;
		cardsOnTable[3] = deck.getNextCard();
		Card[] yourHand = players[0].getCards();
		System.out.printf("Your Cards: %s, %s\n", yourHand[0].toString(), yourHand[1].toString());
		System.out.printf("Turn: %s, %s, %s, %s\n", cardsOnTable[0], cardsOnTable[1], 
				cardsOnTable[2], cardsOnTable[3]);
		System.out.println("Cash in pot: " + pot);
	}
	
	public void dealRiver() {
		round = State.RIVER;
		Card[] yourHand = players[0].getCards();
		cardsOnTable[4] = deck.getNextCard();
		System.out.printf("Your Cards: %s, %s\n", yourHand[0].toString(), yourHand[1].toString());
		System.out.printf("River: %s, %s, %s, %s, %s\n", cardsOnTable[0], cardsOnTable[1], 
				cardsOnTable[2], cardsOnTable[3], cardsOnTable[4]);
		System.out.println("Cash in pot: " + pot);
	}

	/**
	 * Betting begins with player after the big blind.
	 * If a bet has been made already the player can match the bet or raise
	 * If a player raises the minimum raise becomes their raise and all future raises must be at least this size.
	 * Betting continues and the remaining players must match the new bet.
	 *
	 */
	public void initialBet() {
		//Create a collection of players that represents the betting order.
		Collection<Player> playersInOrder = sortPlayersIntoInitialBettingOrder();
		players[smallBlindPlayer].deductChips(LITTLE_BLIND);
		players[bigBlindPlayer].deductChips(BIG_BLIND);
		currentBet = BIG_BLIND;
		for (Player p : playersInOrder) {
			//TODO: Ensure player bet is valid
			if (p.getName().equals("human")) {
				int playerBet = Game.getBetFromPlayer();
				if (playerBet == 0) {
					p.fold();
				} else {
					p.deductChips(playerBet);
					pot += playerBet;
					if (playerBet > currentBet) {
						currentBet = playerBet;
						bet(sortPlayersIntoBettingOrder(p));
						break;
					}
				}
			} else {
				//Ensuring that the bet is valid is handled in determineAIBet method
				int AIBet = determineAIBet(p);
				if (AIBet == 0) {
					p.fold();
				} else if (AIBet < currentBet) {
					p.fold();
				} else {
					p.deductChips(AIBet);
					pot += AIBet;
					if (AIBet > currentBet) {
						currentBet = AIBet;
						bet(sortPlayersIntoBettingOrder(p));
						break;
					}
				}
			}
		}
	}

	public void bet(Collection<Player> playersInOrder) {
		//Create a collection of players that represents the betting order.
		for (Player p : playersInOrder) {
			//TODO: Ensure player bet is valid
			if (p.getName().equals("human")) {
				int playerBet = Game.getBetFromPlayer();
				if (playerBet == 0) {
					p.fold();
				} else {
					p.deductChips(playerBet);
					pot += playerBet;
					if (playerBet > currentBet) {
						currentBet = playerBet;
						bet(sortPlayersIntoBettingOrder(p));
						break;
					}
				}
			} else {
				//Ensuring that the bet is valid is handled in determineAIBet method
				int AIBet = determineAIBet(p);
				if (AIBet == 0) {
					p.fold();
				} else if (AIBet < currentBet) {
					p.fold();
				} else {
					p.deductChips(AIBet);
					pot += AIBet;
					if (AIBet > currentBet) {
						currentBet = AIBet;
						bet(sortPlayersIntoBettingOrder(p));
						break;
					}
				}
			}
		}
	}


	public void pickWinner() {
		int firstNotFoldedPlayer = -1;
		for (int i = 0; i < players.length; i++) {
			if (!(players[i].checkFold())) {
				firstNotFoldedPlayer = i;
				break;
			}
		}
		if (firstNotFoldedPlayer < 0) {
			System.out.println("Everyone folded!");
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
	}
	
	public void newHand() {
		pot = 0;
		bigBlindPlayer++;
		smallBlindPlayer++;
		for (int i = 0; i < players.length; i++) {
			if (players[i].getChips() > 0) {
				players[i].unFold();
			}
			else {
				players[i].fold();
			}
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
			if (currentBet < 100) {
				return 100;
			} else if (currentBet < 150) {
				return currentBet;
			} else {
				return 0;
			}
		}
		//Tree of a kind - Straight
		else if (handVal > 3) {
			if (currentBet < 65) {
				return 65;
			} else if (currentBet < 100) {
				return currentBet;
			} else {
				return 0;
			}
		}
		//Two pair - Flush
		else if (handVal > 1) {
			if (currentBet < 30) {
				return 30;
			} else if (currentBet < 45) {
				return currentBet;
			} else {
				return 0;
			}
		}
		else if (round == State.FIRST || round == State.FLOP) {
			return 10;
		}
		else {
			return 0;
		}
   	    	 
	}

	/**
	 * This method sorts the players into the order they will bet in.
	 * Players that have folded are not added to the list.
	 * The player after the player that placed the big blind goes first and the big blind goes last.
	 * @return a collection of players in the order they will bet.
	 */
	private Collection<Player> sortPlayersIntoInitialBettingOrder() {
		LinkedList<Player> playersInOrder = new LinkedList<>();
		int i;
		if (bigBlindPlayer < players.length - 1) {
			i = bigBlindPlayer + 1;
		} else {
			i = 0;
		}
		while (playersInOrder.size() < players.length) {
			if (!players[i].checkFold()) {
				playersInOrder.add(players[i]);
				//TODO: Maybe shouldn't be -1?
				if (i < players.length - 1) {
					i++;
				} else {
					i = 0;
				}
			}
		}
		return playersInOrder;
	}

	private Collection<Player> sortPlayersIntoBettingOrder(Player p) {
		//determine which player this is
		int indexOfPlayer = -1;
		for (int i = 0; i < players.length; i++) {
			if (players[i].equals(p)) {
				if (i == players.length - 1) {
					indexOfPlayer = 0;
				} else {
					indexOfPlayer = i + 1;
				}
			}
		}
		LinkedList<Player> playersInOrder = new LinkedList<>();
		while (playersInOrder.size() < players.length) {
			if (!players[indexOfPlayer].checkFold()) {
				playersInOrder.add(players[indexOfPlayer]);
				if (indexOfPlayer < players.length - 1) {
					indexOfPlayer++;
				} else {
					indexOfPlayer = 0;
				}
			}
		}
		return playersInOrder;
	}

	
	public Card[] getCardsOnTable() {
	  return this.cardsOnTable;
	}
	
	public int getPlayerScore() {
		return players[0].getChips();
	}
	
	public boolean bettingComplete() {
		return true;
	}
	

}
