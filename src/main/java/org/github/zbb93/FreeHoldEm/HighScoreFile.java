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

import com.google.common.collect.Lists;

import java.io.*;
import java.util.List;
import java.util.Scanner;


public class HighScoreFile {
	private final File highScoreFile;
	private List<HighScore> highscores = Lists.newArrayList();
	private String path = "";
	
	public HighScoreFile(String path) {
		highScoreFile = new File(path);
		this.path = path;
		//If file exists, try to load highscores into highscores variable.
		if (highScoreFile.exists()) {
			loadFile();
			sortHighScores();
		}
		else {
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
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			for (HighScore hs : highscores) {
			  writer.write(hs.toString());
			}

		}
		catch (IOException e) {
			// todo we can stop printing the throwable once we start using a logging system.
			//noinspection ThrowablePrintedToSystemOut
			System.err.println(e);
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
				}
				catch (IOException e) {
					//System.err.println(e);
				  e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Loads saved highscores from file.
	 */
	private List<HighScore> loadHighScores(Scanner scoreScanner) {
		List<HighScore> highscores = Lists.newArrayListWithCapacity(10);
		//Read previous highscores
		try {
			while (scoreScanner.hasNext()) {
				//Read file line
				String line = scoreScanner.nextLine();
				//parts[0] is name, parts[1] is score
				String[] parts = line.split(HighScore.HIGHSCORE_DELIMITER);
				highscores.add( new HighScore(parts[0], Integer.parseInt(parts[1])) );
			}
		} catch (Exception e) {
			// TODO: handle exception
			//System.err.println(e);
		  e.printStackTrace();
		}	
		return highscores;
	}
	/**
	 * Creates dummy highscores if there is no previous ones.
	 */
	public void writeDummyHighScoresIfNecessary() {
		//If there is actual highscores already
		if (highscores.size() > 0 ) {
			return;
		}
		BufferedWriter writer = null;
		try {			
			String [] strs = new String[]{"Alice", "Bob", "Jill", "Justin", "David", "Tyler", "John", "Tricia", "John", "Chris", "Becca"};
			int [] scores = new int[]{100, 250, 500, 666, 750, 875, 950, 1075, 1200, 1340, 1500};
			writer = new BufferedWriter(new FileWriter(this.path));
			for (int i = 0; i < scores.length; i++) {
				HighScore h = new HighScore(strs[i], scores[i]);
				writer.write(h.toString() );
				//add highscore also in the list
				highscores.add(h);
			}
		}
		catch (IOException e) {
			//System.err.println(e);
		  e.printStackTrace();
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
				}
				catch (IOException e) {
				  e.printStackTrace();
					//System.err.println(e);
				}
			}
		}
	}
	/**
	 * Loads saved highscores from file.
	 */
	public void loadFile() {
		// TODO Auto-generated method stub
		Scanner scoreScanner;
		try {
			scoreScanner = new Scanner(highScoreFile);
			this.highscores = loadHighScores(scoreScanner);
			scoreScanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
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
