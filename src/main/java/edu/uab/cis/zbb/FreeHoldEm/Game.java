package edu.uab.cis.zbb.FreeHoldEm;

import java.util.ArrayList;
import java.util.Scanner;
public class Game {
	/**
	 * Array to store the current high scores. It is initialized in assignHighScores function.
	 */
	private static HighScore[] highScores;
    /**
     * Handles highScore file I/O.   
     */
	private static HighScoreFile highScoreFile;
	
	private static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		//Creates a new file or loads a existing file. 
		highScoreFile = new HighScoreFile("high_scores.dat");
		//Creates dummy highscores if there are no previous highscores.
		highScoreFile.writeDummyHighScoresIfNecessary();
		assignHighScores(highScoreFile);
		System.out.print("Number of players (2 - 8): ");
		int numberPlayers = sc.nextInt();                
		FreeHoldEm game = new FreeHoldEm(numberPlayers);
		boolean playing = true;
		while (playing) {
			game.dealHands();
			System.out.println(game.toString());
			betLoop(game);
			game.dealFlop();
			System.out.println(game.toString());
			betLoop(game);
			game.dealTurn();
			System.out.println(game.toString());
			betLoop(game);
			game.dealRiver();
			System.out.println(game.toString());
			betLoop(game);
			game.pickWinner();
			System.out.print("Would you like to play another hand? (y/n):");
			String response = sc.next();
			if (response.equals("n")) {
				playing = false;
			} else {
				game.newHand();
			}
		}
		if (game.getPlayerScore() > highScores[highScores.length - 1].getScore()) {
			writeNewScore(game.getPlayerScore());
		}
		
	}
	private static void betLoop(FreeHoldEm game) {
		System.out.print("Enter your bet:");
		int playerBet = sc.nextInt();
		game.bet(playerBet);
		if (game.bettingComplete()) {
			return;
		} else {
			betLoop(game);
		}
	}
	
	/**
	 * Assigns loaded highscores into highScores array.
	 * @param hf = HighScoreFile
	 */
	private static void assignHighScores(HighScoreFile hf) {

	  ArrayList<HighScore> list = hf.getHighScoreList();
	  //Create a dynamic highscore array based on loaded highscores.
	  highScores = new HighScore[list.size()];
	  int i = 0;
	  for (HighScore highScore : list) {
	    highScores[i] = highScore;
	    i++;
	  }
	}
	
	/**
	 * This method creates a new HighScore for the player and inserts it into the 
	 * proper position in the highScores array. The scores are then displayed and
	 * written to the high_scores.dat file.
	 */
	private static void writeNewScore(int newScore) {
		System.out.printf("High Score!\nEnter your name: ");
		String name = sc.next();
		HighScore newHighScore = new HighScore(name, newScore);
		highScoreFile.addHighScore(newHighScore);
		//Sorts HighScores to descending order
		highScoreFile.sortHighScores();
		//Write highScores into file.
		highScoreFile.writePlayersIntoFile();
		System.out.println(highScoreFile.toString());
	}
}

