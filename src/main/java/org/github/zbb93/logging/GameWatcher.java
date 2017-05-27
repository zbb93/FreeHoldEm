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
package org.github.zbb93.logging;

import java.io.IOException;

/**
 * This Interface is essentially a logger. The rationale behind this approach instead of using an established logging
 * framework is mainly that I want the output from the game to be as human readable as possible. Additionally there
 * is no reason to change the logging level while the application is running. The same information should be collected
 * for every game. Message templates are encapsulated in the GameWatcherTemplates class.
 * @see GameWatcherTemplates
 * Created by zbb on 5/27/17.
 */
public interface GameWatcher {

	void recordGameState(String gameState);

	/**
	 * Adds a record of a bet to the current output batch. Additionally this method determines whether
	 * @param playerName the name of the player that placed this bet.
	 * @param amountBet the amount the player bet.
	 * @throws IOException if an error occurs while writing to the file. This exception should be caught and the user will
	 * 										 be asked if they would like to continue.
	 */
	void playerBet(String playerName, int amountBet) throws IOException;

	void exception(Throwable e, String gameState);
}
