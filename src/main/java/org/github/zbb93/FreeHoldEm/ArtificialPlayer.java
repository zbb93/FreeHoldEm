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
 * A computer controlled poker player.
 * Created by zbb on 4/23/17.
 */
public class ArtificialPlayer extends Player {
	public ArtificialPlayer(String name) {
		super(name, false);
	}

	@Override
	public int bet(List<Card> cardsOnTable, FreeHoldEm.State round, int currentBet) {
		HandEvaluator.findBestHand(cardsOnTable, this);
		int handVal = getHand().quickVal();
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
		else if (round == FreeHoldEm.State.INIT || round == FreeHoldEm.State.FLOP) {
			return 10;
		}
		else {
			return 0;
		}
	}
}
