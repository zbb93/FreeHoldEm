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

/**
 * This class holds String constants that compose the bulk of the message text used by the GameWatcher class.
 * These Strings should be formatted so that String.format(...) can be used to replace tokens in the GameWatcher class.
 * Created by zbb on 5/21/17.
 */
class GameWatcherTemplates {
	static final String PLAYER_BET = "%s bets %d chips\n";
	static final String EXCEPTION_HAS_OCCURRED = "An exception has occured:\n %s\n\n Current game state: %s\n\n";
}
