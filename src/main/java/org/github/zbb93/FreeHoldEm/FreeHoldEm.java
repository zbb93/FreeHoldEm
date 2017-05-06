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

import java.util.List;

/**
 * @author Zachary Bowen
 */
public class FreeHoldEm {
	enum State {
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
	private State round;
	/**
	 * Integer representing the current amount of chips that have been bet on
	 * this hand.
	 */
	private int pot = 0;

	private final Deck deck = new Deck();

	private int currentBet = 0;
	private int bigBlindPlayer = 1;

	/**
	 * This variable controls whether or not AI hands are printed during gameplay. Should only be true for development
	 * purposes unless you want ez mode.
	 */
	private boolean developmentMode = false;

	/**
	 * This variable will be null until the FreeHoldEm#pickWinner method is called. If there is a winner then this
	 * variable will be initialized and can be used to construct the game state string that is displayed to the user.
	 * If the user elects to play another game this variable will be nulled out during initialization.
	 */
	private Player winner;

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
		winner = null;
		players = Lists.newArrayListWithCapacity(numPlayers);
		players.add(new HumanPlayer(playerName));
		for (int i = 1; i < numPlayers; i++) {
			players.add(new ArtificialPlayer("CPU" +
					String.valueOf(i)));
		}
	}
	
	void dealHands() {
		for (Player player : players) {
			if (!player.isFolded()) {
				player.setCard(0, deck.getNextCard());
				player.setCard(1, deck.getNextCard());
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
	 */
	void bet() {
		Round currentRound = new Round(players, bigBlindPlayer, round);
		currentRound.bet(cardsOnTable);
		pot += currentRound.sumBets();
		rotateBlinds();
	}

	private void rotateBlinds() {
		if (bigBlindPlayer < players.size() - 1) {
			bigBlindPlayer++;
		} else {
			bigBlindPlayer = 0;
		}
	}

	void pickWinner() {
		round = State.CLEAN_UP;
		//Initialize final hand for human player. AI players have their final hand set when placing their bet.
		HandEvaluator.findBestHand(cardsOnTable, players.get(0));
		Player winner = null;
		for (Player player : players) {
			if (!player.isFolded()) {
				if (winner == null) {
					winner = player;
				} else if (winner.getHand().compareTo(player.getHand()) == -1) {
					winner = player;
					winner.setChips(winner.getChips() + pot);
				}
			}
		}

	}
	
	void newHand() {
		pot = 0;
		bigBlindPlayer++;
		for (Player player : players) {
			if (player.getChips() > 0) {
				player.unFold();
			}
			else {
				player.fold();
			}
		}
	}

	List<Card> getCardsOnTable() {
	  return this.cardsOnTable;
	}
	
	int getPlayerScore() {
		return players.get(0).getChips();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String gameState;
		switch(round) {
			case INIT:
				gameState = playerHandsAsStrings();
				break;
			case FLOP:
				gameState = String.format("Flop: %s %s %s\n%sCurrentBet: %d\nCash in pot: %d\n",
						cardsOnTable.get(0), cardsOnTable.get(1), cardsOnTable.get(2), playerHandsAsStrings(), currentBet, pot);
				break;
			case TURN:
				gameState = String.format("Turn: %s %s %s %s\n%sCurrentBet: %d\nCash in pot: %d\n",
						cardsOnTable.get(0), cardsOnTable.get(1), cardsOnTable.get(2), cardsOnTable.get(3),
						playerHandsAsStrings(), currentBet, pot);
				break;
			case RIVER:
				gameState = String.format("River: %s %s %s %s %s\n%sCurrentBet: %d\nCash in pot: %d\n",
						cardsOnTable.get(0), cardsOnTable.get(1), cardsOnTable.get(2), cardsOnTable.get(3),
						playerHandsAsStrings(), cardsOnTable.get(4), currentBet, pot);
				break;
			case CLEAN_UP:
				if (winner == null) {
					gameState = "Everyone folded";
				} else {
					gameState = String.format("\n%s wins with a \n%s\nWinnings: %d\n",
							winner.getName(), winner.getHand().toString(), pot);
				}
				break;
			default:
				throw new IllegalStateException("Unrecognized FreeHoldEm.State: " + round);
		}
		sb.append(gameState);
		return sb.toString();
	}

	private String playerHandsAsStrings() {
		StringBuilder sb = new StringBuilder();
		for (Player player : players) {
			if (player.isHuman()) {
				sb.append("Your Cards: ");
				List<Card> cards = player.getCards();
				sb.append(cards.get(0).toString()).append(" ");
				sb.append(cards.get(1).toString()).append("\n");
			} else if (developmentMode) {
				sb.append(player.getName()).append("'s Cards: ");
				List<Card> cards = player.getCards();
				sb.append(cards.get(0).toString()).append(" ");
				sb.append(cards.get(1).toString()).append("\n");
			}
		}
		return sb.toString();
	}
}
