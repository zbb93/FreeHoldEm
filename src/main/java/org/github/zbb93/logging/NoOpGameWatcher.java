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
 * This class is used when we are unable to initialize an StdGameWatcher and the user wishes to continue with logging
 * functionality disabled.
 * Created by zbb on 5/27/17.
 */
public class NoOpGameWatcher implements GameWatcher {

	@Override
	public void recordGameState(String gameState) {}

	@Override
	public void playerBet(String playerName, int amountBet) throws IOException {}

	@Override
	public void exception(Throwable e, String gameState) {}
}
