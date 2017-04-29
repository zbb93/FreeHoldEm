/*
 * FreeHoldEm
 * Copyright 2017 by Zachary Bowen
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
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class encapsulates the logic surrounding a round of betting.
 * Created by zbb on 4/23/17.
 */
public class Round {

	private @NotNull Map<Player, Integer> playersToBets;
	private int bet;
	private final @NotNull FreeHoldEm.State round;

	private static final int LITTLE_BLIND = 5;
	private static final int BIG_BLIND = 10;

	public Round(@NotNull List<Player> players, int bigBlindPlayer, @NotNull FreeHoldEm.State round) {
		playersToBets = initPlayerMap(sortPlayersIntoInitialBettingOrder(players, bigBlindPlayer));
		bet = 0;
		this.round = round;
	}

	/**
	 * This method sorts the players into the order they will bet in.
	 * Players that have folded are not added to the list.
	 * The player after the player that placed the big blind goes first and the big blind goes last.
	 * @return a collection of players in the order they will bet.
	 */
	@NotNull
	private List<Player> sortPlayersIntoInitialBettingOrder(@NotNull List<Player> players, int bigBlindPlayer) {
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

	@NotNull
	private Map<Player, Integer> initPlayerMap(@NotNull List<Player> players) {
		Map<Player, Integer> toReturn = Maps.newLinkedHashMap();
		for (Player player : players) {
			toReturn.put(player, 0);
		}
		return toReturn;
	}

	public int bet(@NotNull List<Card> cardsOnTable) {
		boolean first = true;
		for (Map.Entry<Player, Integer> entry : playersToBets.entrySet()) {
			Player player = entry.getKey();
			int playerBet = player.bet(cardsOnTable, round, bet);
			int currentPlayerBet = playerBet + entry.getValue();
			if (currentPlayerBet < bet) {
				player.fold();
			} else if (currentPlayerBet > bet) {
				// start over again
				entry.setValue(currentPlayerBet);
				if (!first) {
					prepareForNewBettingOrder(player);
					return bet(cardsOnTable);
				} else {
					bet = currentPlayerBet;
				}
			} else {
				entry.setValue(currentPlayerBet);
			}
			first = false;
		}
		return bet;
	}

	private void prepareForNewBettingOrder(Player playerThatRaised) {
		bet = playersToBets.get(playerThatRaised);
		List<Player> playersInNewBettingOrder = sortPlayersIntoNewBettingOrder(playerThatRaised);
		playersToBets = initPlayerMap(playersInNewBettingOrder);
	}

	@NotNull
	private List<Player> sortPlayersIntoNewBettingOrder(@NotNull Player playerThatRaised) {
		boolean foundPlayer = false;
		Set<Player> players = playersToBets.keySet();
		List<Player> newBettingOrder = Lists.newLinkedList();
		for (Player player : players) {
			if (foundPlayer) {
				newBettingOrder.add(player);
			}

			if (player.equals(playerThatRaised)) {
				foundPlayer = true;
			}
		}
		return newBettingOrder;
	}
}
