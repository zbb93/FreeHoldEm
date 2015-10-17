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
		}
		else {
			try {
				highScoreFile.createNewFile();
				System.out.println("Creating new highscore file: " + this.path.toString());
				System.out.println("Adding example players..");
				this.writeFewPlayers();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("Cannot create a new highscore file.");
			}
		}
		
	}
	public void addHighScore(HighScore h) {
		highscores.add(h);
	}
	
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
	public void delete() {
		highScoreFile.delete();
	}
	public String getHighScore(String name) {
		if(!highscores.isEmpty()) {
			for (Iterator<HighScore> iterator = highscores.iterator(); iterator.hasNext();) {
				HighScore highScore = (HighScore) iterator.next();

				if ( highScore.getName().equals(name)) {
					return highScore.toString();
				}
			}
		}
		return "No highscore found.";
	}
	public void writePlayersIntoFile() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			for (Iterator<HighScore> iterator = highscores.iterator(); iterator.hasNext();) {
				HighScore highScore = (HighScore) iterator.next();
				writer.write(highScore.toString() );
				writer.newLine();
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
					System.err.println(e);
				}
			}
		}
	}
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
			System.err.println(e);
		}
		
		return highscores;
	}
	private void writeFewPlayers() {
		BufferedWriter writer = null;
		try {
			
			String [] strs = new String[]{"Alice", "Bob", "Jill", "Justin", "David", "Tyler", "John", "Tricia", "John", "Chris", "Becca"};
			int [] bets = new int[]{666, 100, 250, 500, 750, 875, 950, 1075, 1200, 1340, 1500};
			writer = new BufferedWriter(new FileWriter(this.path));
			for (int i = 0; i < bets.length; i++) {
				writer.write(new HighScore(strs[i], bets[i]).toString() );
				writer.newLine();
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
					System.err.println(e);
				}
			}
		}
	}
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
}
