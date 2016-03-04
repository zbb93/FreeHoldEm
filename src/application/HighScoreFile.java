package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;


public class HighScoreFile {
	private File highScoreFile;
	private ArrayList<HighScore> highscores = new ArrayList<HighScore>();
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
				highScoreFile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("Cannot create a new highscore file.");
			}
		}
		
	}
	
	/**
	 * Returns List of Highscores
	 */
	public ArrayList<HighScore> getHighScoreList() {
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
		
		Collections.sort(highscores, new Comparator<HighScore>() {
	        @Override
	        public int compare(HighScore  h1, HighScore  h2)
	        {
	        	//Descending order. If you want ascending order return first - second.
	        	int first = h1.getScore();
	        	int second = h2.getScore();
	          return  second - first;
	        }
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
			highScoreFile.delete();
		}
		
	}
	/**
	 * Gets first highscore by player name.
	 */
	public String getHighScoreByPlayer(String name) {
		if(!highscores.isEmpty()) {
		  Iterator<HighScore> iterator = highscores.iterator();
		  while (iterator.hasNext()) {
		    HighScore highScore = iterator.next();
		    if (highScore.getName().equals(name)) {
		      return highScore.toString();
		    }
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
			/*
			for (Iterator<HighScore> iterator = highscores.iterator(); iterator.hasNext();) {
				HighScore highScore = (HighScore) iterator.next();
				writer.write(highScore.toString() );
				writer.newLine();
			}*/
			int count = 0;
			Iterator<HighScore> iterator = highscores.iterator();
			while (iterator.hasNext() && count < 10) {
			  HighScore hs = iterator.next();
			  writer.write(hs.toString());
			  count++;
			}

		}
		catch (IOException e) {
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
	public ArrayList<HighScore> loadHighScores(Scanner scoreScanner) {
		ArrayList<HighScore> highscores = new ArrayList<HighScore>(10);
		//Read previous highscores
		try {
			while ( scoreScanner.hasNext() ) {
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
			int [] bets = new int[]{666, 100, 250, 500, 750, 875, 950, 1075, 1200, 1340, 1500};
			writer = new BufferedWriter(new FileWriter(this.path));
			for (int i = 0; i < bets.length; i++) {
				HighScore h = new HighScore(strs[i], bets[i]);
				writer.write(h.toString() );
				writer.newLine();
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
	  Iterator<HighScore> iterator = highscores.iterator();
	  String hsAsString = "";
	  while (iterator.hasNext()) {
	    hsAsString += iterator.next().toString();
	  }
	  return hsAsString;
	}
	
}
