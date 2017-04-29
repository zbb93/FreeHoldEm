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

import java.util.List;

/**
 * This is the player object.
 * Created by zbb on 4/23/17.
 */
public class HumanPlayer extends Player {

	public HumanPlayer(String name) {
		super(name, true);
	}

	@Override
	public int bet(List<Card> cardsOnTable, FreeHoldEm.State round, int currentBet) {
		int bet = Game.getBetFromPlayer();
		if (bet == 0) {
			fold();
		} else if (bet >= currentBet) {
			deductChips(bet);
		} else {
			// todo invalid bet
		}
		return bet;
	}
}
