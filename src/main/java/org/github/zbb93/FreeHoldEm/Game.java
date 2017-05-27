package org.github.zbb93.FreeHoldEm;

import java.util.List;
import java.util.Scanner;
public class Game {
	/**
	 * Array to store the current high scores. It is initialized in assignHighScores function.
	 */
	private static List<HighScore> highScores;
    /**
     * Handles highScore file I/O.   
     */
	private static HighScoreFile highScoreFile;

	// todo open and close as needed
	private static final Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		//Creates a new file or loads a existing file. 
		highScoreFile = new HighScoreFile("high_scores.dat");
		highScoreFile.writeDummyHighScoresIfNecessary();
		assignHighScores(highScoreFile);
		System.out.print("Enter your name: ");
		String playerName = sc.next();
		System.out.print("Number of players (2 - 8): ");
		int numberPlayers = sc.nextInt();                
		FreeHoldEm game = new FreeHoldEm(numberPlayers, playerName);
		boolean playing = true;
		while (playing) {
			game.dealHands();
			System.out.println(game.toString());
			game.bet();
			game.dealFlop();
			System.out.println(game.toString());
			game.bet();
			game.dealTurn();
			System.out.println(game.toString());
			game.bet();
			game.dealRiver();
			System.out.println(game.toString());
			game.bet();
			game.pickWinner();
			System.out.println(game.toString());
			if (playAnotherGame()) {
				game.newHand();
			} else {
				playing = false;
			}
		}
		if (game.getPlayerScore() > highScores.get(highScores.size() - 1).getScore()) {
			writeNewScore(game.getPlayerScore());
		}
		
	}

	private static boolean playAnotherGame() {
		boolean playAgain;
		System.out.print("Would you like to play another hand? (y/n):");
		try {
			playAgain = getResponseFromPlayer();
		} catch (IllegalArgumentException e) {
			System.out.println("Please enter 'y' or 'n'\n");
			return playAnotherGame();
		}
		return playAgain;
	}

	private static boolean getResponseFromPlayer() {
		String response = sc.next();
		boolean responseAsBoolean;
		if (response.equalsIgnoreCase("n") || response.equalsIgnoreCase("no")) {
			responseAsBoolean = false;
		} else if (response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes")){
			responseAsBoolean = true;
		} else {
			throw new IllegalArgumentException("Expected values are 'y' and 'n' received: " + response);
		}
		return responseAsBoolean;
	}

	static int getBetFromPlayer(int minimumBet) {
		System.out.println("Amount to call: " + minimumBet);
		System.out.print("Enter your bet: ");
		int bet = sc.nextInt();
		if (bet < minimumBet && bet != 0) {
			System.out.println("ERROR: You have entered an invalid bet!");
			System.out.println("You bet " + bet + " chips, but the required amount to call is "
					+ minimumBet + " chips. Please try again\n");
			return getBetFromPlayer(minimumBet);
		}
		return bet;
	}

	/**
	 * Assigns loaded highscores into highScores array.
	 * @param hf = HighScoreFile
	 */
	private static void assignHighScores(HighScoreFile hf) {
	  highScores = hf.getHighScoreList();
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

	/**
	 * @return true if the user answers y/yes; false for n/no.
	 */
	static boolean playWithoutLogging() {
		// todo ask the user if they want to continue playing
		return true;
	}
}

