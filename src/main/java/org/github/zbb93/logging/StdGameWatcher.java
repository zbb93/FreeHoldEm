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

import com.google.common.base.Preconditions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Concrete implementation of GameWatcher Interface.
 * Created by zbb on 5/21/17.
 */
public class StdGameWatcher implements GameWatcher {

	/**
	 * This is the output file.
	 */
	private File gameFile;

	/**
	 * This variable is used to allow us to gather the write operations to the output file into manageable batches.
	 */
	private int numPlayers;

	/**
	 * The current batch of output. After each round of gameplay the buffer will be flushed.
	 */
	private StringBuffer batch;

	/**
	 * Used to keep track of the size of the current batch. When this variable equals numPlayers we know that the
	 * current round has concluded and we can flush the current batch to the file.
	 */
	private int currentBatchSize;

	private boolean gameStateRecorded;

	/**
	 * Initializes the logging system.
	 * @param outputFilePath path to save the output to at the end of the game.
	 * @throws IOException if an error occurs while creating the file. This exception should be caught and the user will
	 * 										 be asked if they would like to continue.
	 * @throws IllegalStateException if the file cannot be created, but an IOException does not occur. This exception
	 * 															 should be caught and the user will be asked if they would like to continue.
	 */
	public StdGameWatcher(String outputFilePath, int numPlayers) throws IOException, IllegalStateException {
		this.numPlayers = numPlayers;
		gameStateRecorded = false;
		batch = new StringBuffer();
		gameFile = new File(outputFilePath);
		Preconditions.checkState(gameFile.createNewFile(), "An error occurred while trying to create the " +
				"log file.");
	}

	public void recordGameState(String gameState) {
		Preconditions.checkState(currentBatchSize == 0, "This method should not be called after " +
				"player bets have been recorded.");
		batch.append(gameState);
		gameStateRecorded = true;
	}

	// todo this method needs to be refactored so that we can record each action taken, not just the total bet.
	public void playerBet(String playerName, int amountBet) throws IOException {
		currentBatchSize++;
		String output = String.format(GameWatcherTemplates.PLAYER_BET, playerName, amountBet);
		batch.append(output);

		if (!(currentBatchSize < numPlayers)) {
			Preconditions.checkState(gameStateRecorded);
			flush();
		}
	}

	public void exception(Throwable e, String gameState) {
		batch.append(String.format(GameWatcherTemplates.EXCEPTION_HAS_OCCURRED, e.getMessage(), gameState));
	}

	/**
	 * Flushes the output buffer.
	 * @throws IOException if an error occurs while writing to the file. This exception should be caught and the user will
	 * 										 be asked if they would like to continue.
	 */
	private void flush() throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(gameFile, true))) {
			batch.append("\n\n");
			bw.write(batch.toString());
		}
		batch = new StringBuffer();
		currentBatchSize = 0;
		gameStateRecorded = false;
	}
}
