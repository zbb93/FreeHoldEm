package org.github.zbb93.FreeHoldEm;
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

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * @author Zachary Bowen
 */
public class FreeHoldEm {
	private enum State {
		INIT, FIRST, FLOP, TURN, RIVER, CLEAN_UP
	}
	/**
	 * Array of players. As players run out of chips they are not removed from the
	 * array, but they are permanently folded.
	 */
	private List<Player> players;
	/**
	 * Community cards currently in play. Note this array does not contain the
	 * cards that each player has been dealt
	 */
	private List<Card> cardsOnTable;
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

	private final HandEvaluator he = new HandEvaluator();
	
	private final Deck deck = new Deck();

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
	public FreeHoldEm(List<Card> cards) {
		this.cardsOnTable = Lists.newArrayList(cards);
	}

	FreeHoldEm(int numPlayers, String playerName) {
		cardsOnTable = Lists.newArrayListWithCapacity(5);
		initPlayers(numPlayers, playerName);
		round = State.INIT;
	}

	private void initPlayers(int numPlayers, String playerName) {
		players = Lists.newArrayListWithCapacity(numPlayers);
		players.add(new Player(playerName, true));
		for (int i = 1; i < players.size(); i++) {
			players.add(new Player("CPU" +
					String.valueOf(i)));
		}
	}
	
	void dealHands() {
		for (Player player : players) {
			if (!player.checkFold()) {
				player.setCards(0, deck.getNextCard());
				player.setCards(1, deck.getNextCard());
			}
		}
	}
	
	void dealFlop() {
		round = State.FLOP;
		cardsOnTable.add(deck.getNextCard());
		cardsOnTable.add(deck.getNextCard());
		cardsOnTable.add(deck.getNextCard());
	}
	
	void dealTurn() {
		round = State.TURN;
		cardsOnTable.add(deck.getNextCard());
	}
	
	void dealRiver() {
		round = State.RIVER;
		cardsOnTable.add(deck.getNextCard());
	}

	/**
	 * Betting begins with player after the big blind.
	 * If a bet has been made already the player can match the bet or raise
	 * If a player raises the minimum raise becomes their raise and all future raises must be at least this size.
	 * Betting continues and the remaining players must match the new bet.
	 *
	 */
	void initialBet() {
		//Create a collection of players that represents the betting order.
		Collection<Player> playersInOrder = sortPlayersIntoInitialBettingOrder();
		players.get(smallBlindPlayer).deductChips(LITTLE_BLIND);
		players.get(bigBlindPlayer).deductChips(BIG_BLIND);
		currentBet = BIG_BLIND;
		pot += BIG_BLIND + LITTLE_BLIND;
		for (Player p : playersInOrder) {
			//TODO: Ensure player bet is valid
			if (p.isHuman()) {
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

	private void bet(Collection<Player> playersInOrder) {
		//Create a collection of players that represents the betting order.
		for (Player p : playersInOrder) {
			//TODO: Ensure player bet is valid
			if (p.isHuman()) {
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


	void pickWinner() {
		round = State.CLEAN_UP;
		//Initialize final hand for human player. AI players have their final hand set when placing their bet.
		he.findBestHand(this, players.get(0));
		Player winner = null;
		for (Player player : players) {
			if (winner == null) {
				if (!player.checkFold()) {
					winner = player;
				}
			} else if (winner.getHand().compareTo(player.getHand()) == -1) {
				winner = player;
			}
		}

		if (winner == null) {
			System.out.println("Everyone folded!");
		} else {
			winner.setChips(winner.getChips() + pot);
			System.out.printf("\n%s wins with a \n%s\nWinnings: %d\n", winner.getName(),
					winner.getHand().toString(), pot);
		}
	}
	
	void newHand() {
		pot = 0;
		bigBlindPlayer++;
		smallBlindPlayer++;
		for (Player player : players) {
			if (player.getChips() > 0) {
				player.unFold();
			}
			else {
				player.fold();
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
		else if (round == State.INIT || round == State.FLOP) {
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
		List<Player> playersInOrder = Lists.newArrayList();
		int i;
		if (bigBlindPlayer < players.size() - 1) {
			i = bigBlindPlayer + 1;
		} else {
			i = 0;
		}
		boolean looped = false;
		while (playersInOrder.size() < players.size() && (!looped || i <= bigBlindPlayer)) {
			if (!players.get(i).checkFold()) {
				playersInOrder.add(players.get(i));
			}
			if (i < players.size() - 1) {
				i++;
			} else {
				i = 0;
				looped = true;
			}

		}
		return playersInOrder;
	}

	private Collection<Player> sortPlayersIntoBettingOrder(Player p) {
		//determine which player this is
		int indexOfPlayerThatRaised = -1;
		int indexOfPlayerToAdd = -1;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).equals(p)) {
				if (i == players.size() - 1) {
					indexOfPlayerThatRaised = i;
					indexOfPlayerToAdd = 0;
				} else {
					indexOfPlayerThatRaised = i;
					indexOfPlayerToAdd = i + 1;
				}
				break;
			}
		}
		List<Player> playersInOrder = Lists.newArrayList();
		//The player that made the raise will not get to bet again.
		while (playersInOrder.size() < players.size() - 1) {
			if (indexOfPlayerToAdd == indexOfPlayerThatRaised) {
				break;
			}
			if (!players.get(indexOfPlayerToAdd).checkFold()) {
				playersInOrder.add(players.get(indexOfPlayerToAdd));
			}
			if (indexOfPlayerToAdd < players.size() - 1) {
				indexOfPlayerToAdd++;
			} else {
				indexOfPlayerToAdd = 0;
			}

		}
		return playersInOrder;
	}

	
	List<Card> getCardsOnTable() {
	  return this.cardsOnTable;
	}
	
	int getPlayerScore() {
		return players.get(0).getChips();
	}

	@Override
	// todo use a StringBuilder
	public String toString() {
		//TODO: Display blinds and currentBet for all states except CLEAN_UP
		String gameAsString = "";
		switch(round) {
			case INIT:
				//TODO: add some type of flag so that computer hands aren't printed unless debug mode
				gameAsString += playerHandsAsStrings();
				break;
			case FLOP:
				gameAsString += "Flop: " + cardsOnTable.get(0).toString() + " " +
					cardsOnTable.get(1).toString() + " " + cardsOnTable.get(2).toString() + "\n";
				gameAsString += playerHandsAsStrings();
				gameAsString += "Current Bet: " + currentBet + "\n";
				gameAsString += "Cash in pot: " + pot + "\n";
				break;
			case TURN:
				gameAsString += "Turn: " + cardsOnTable.get(0).toString() + " " +
					cardsOnTable.get(1).toString() + " " + cardsOnTable.get(2).toString() + " " +
					cardsOnTable.get(3).toString() + "\n";
				gameAsString += playerHandsAsStrings();
				gameAsString += "Current Bet: " + currentBet + "\n";
				gameAsString += "Cash in pot: " + pot + "\n";
				break;
			case RIVER:
				gameAsString += "River: " + cardsOnTable.get(0).toString() + " " +
					cardsOnTable.get(1).toString() + " " + cardsOnTable.get(2).toString() + " " +
					cardsOnTable.get(3).toString() + " " + cardsOnTable.get(4) + "\n";
				gameAsString += playerHandsAsStrings();
				gameAsString += "Current Bet: " + currentBet + "\n";
				gameAsString += "Cash in pot: " + pot + "\n";
				break;
			case CLEAN_UP:
				//TODO: move output from pickWinner method here.
		}
		return gameAsString;
	}

	private String playerHandsAsStrings() {
		StringBuilder sb = new StringBuilder();
		for (Player player : players) {
			if (player.isHuman()) {
				sb.append("Your Cards: ");
				Card[] cards = player.getCards();
				sb.append(cards[0].toString()).append(" ");
				sb.append(cards[1].toString()).append("\n");
			} else {
				sb.append(player.getName()).append("'s Cards: ");
				Card[] cards = player.getCards();
				sb.append(cards[0].toString()).append(" ");
				sb.append(cards[1].toString()).append("\n");
			}
		}
		return sb.toString();
	}
}
