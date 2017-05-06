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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class HighScoreFile {
	private final File highScoreFile;
	private List<HighScore> highscores;
	private String path;
	
	public HighScoreFile(String path) {
		highscores = Lists.newArrayList();
		highScoreFile = new File(path);
		this.path = path;

		if (highScoreFile.exists()) {
			loadFile();
			sortHighScores();
		} else {
			try {
				if(!highScoreFile.createNewFile()) {
					handleIoException(new IOException("Cannot create a new highscore file."));
				}
			} catch (IOException e) {
				handleIoException(e);
			}
		}
	}

	private void handleIoException(IOException e) {
		e.printStackTrace();
		System.out.println(e.getMessage());
	}
	
	/**
	 * Returns List of Highscores
	 */
	public List<HighScore> getHighScoreList() {
		return highscores;
	}

	public void addHighScore(HighScore h) {
		highscores.add(h);
	}

	public int numberOfHighScores() {
		return highscores.size();
	}

	/**
	 * Sorts loaded highscores by descending order.
	 */
	public void sortHighScores() {
		
		highscores.sort((h1, h2) -> {
			//Descending order. If you want ascending order return first - second.
			int first = h1.getScore();
			int second = h2.getScore();
			return second - first;
		});
	}
	public boolean exists() {
		return highScoreFile.exists();
	}
	/**
	 * Deletes highscore file.
	 */
	public void delete() {
		if(highScoreFile.exists()) {
			if(!highScoreFile.delete()) {
				handleIoException(new IOException("An error occurred while attempting to delete the file."));
			}
		}
		
	}
	/**
	 * Gets first highscore by player name.
	 */
	public String getHighScoreByPlayer(String name) {
		if(!highscores.isEmpty()) {
			for (HighScore highScore : highscores)
				if (highScore.getName().equals(name)) {
					return highScore.toString();
				}
		}
		return "No highscore found.";
	}
	/**
	 * Writes all highscores from variable "highscores" to file.
	 */
	public void writePlayersIntoFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))){
			for (HighScore hs : highscores) {
			  writer.write(hs.toString());
			}

		} catch (IOException e) {
			// todo we can stop printing the throwable once we start using a logging system.
			//noinspection ThrowablePrintedToSystemOut
			System.err.println(e);
		}
	}
	/**
	 * Loads saved highscores from file.
	 */
	private List<HighScore> loadHighScores(Scanner scoreScanner) {
		List<HighScore> highscores = Lists.newArrayListWithCapacity(10);
		while (scoreScanner.hasNext()) {
			String line = scoreScanner.nextLine();
			//parts[0] is name, parts[1] is score
			String[] parts = line.split(HighScore.HIGHSCORE_DELIMITER);
			highscores.add(new HighScore(parts[0], Integer.parseInt(parts[1])));
		}
		return highscores;
	}
	/**
	 * Creates dummy highscores if there are no existing ones.
	 */
	public void writeDummyHighScoresIfNecessary() {
		if (highscores.size() > 0 ) {
			return;
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))){
			Map<String, Integer> playersToScores = getDummyPlayerMap();
			for (Map.Entry<String, Integer> entry : playersToScores.entrySet()) {
				HighScore h = new HighScore(entry.getKey(), entry.getValue());
				writer.write(h.toString());
				highscores.add(h);
			}
		} catch (IOException e) {
		  e.printStackTrace();
		}
	}

	private Map<String, Integer> getDummyPlayerMap() {
		Map<String, Integer> toReturn = Maps.newLinkedHashMap();
		toReturn.put("Alice", 100);
		toReturn.put("Bob", 250);
		toReturn.put("Jill", 500);
		toReturn.put("Justin", 666);
		toReturn.put("David", 750);
		toReturn.put("Tyler", 875);
		toReturn.put("John", 950);
		toReturn.put("Tricia", 1075);
		toReturn.put("John", 1200);
		toReturn.put("Chris", 1340);
		toReturn.put("Becca", 1500);
		return ImmutableMap.copyOf(toReturn);
	}

	/**
	 * Loads saved highscores from file.
	 */
	public void loadFile() {
		try (Scanner scoreScanner = new Scanner(highScoreFile)){
			this.highscores = loadHighScores(scoreScanner);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
	  StringBuilder sb = new StringBuilder();
	  for (HighScore highScore : highscores) {
	    sb.append(highScore.toString());
	  }
	  return sb.toString();
	}
	
}
