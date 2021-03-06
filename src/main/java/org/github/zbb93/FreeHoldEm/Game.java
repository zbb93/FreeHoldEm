package org.github.zbb93.FreeHoldEm;

import org.jetbrains.annotations.NotNull;

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

	public static void main(String[] args) {
		//Creates a new file or loads a existing file. 
		highScoreFile = new HighScoreFile("high_scores.dat");
		highScoreFile.writeDummyHighScoresIfNecessary();
		assignHighScores(highScoreFile);
		String playerName = getPlayerName();
		int numberPlayers = getNumberOfPlayers();
		FreeHoldEm game = new FreeHoldEm(numberPlayers, playerName);
		boolean playing = true;
		while (playing) {
			game.dealHands();
			System.out.println(game.toString());
			game.flop();
			System.out.println(game.toString());
			game.turn();
			System.out.println(game.toString());
			game.river();
			System.out.println(game.toString());
			game.pickWinner();
			System.out.println(game.toString());
			if (playAnotherGame()) {
				game.newHand();
			} else {
				playing = false;
			}
		}
		if (newHighScore(game.getPlayerScore())) {
			writeNewScore(game.getPlayerScore());
		}
	}

	@NotNull
	private static String getPlayerName() {
		System.out.print("Enter your name: ");
		return getUserInputString();
	}

	private static int getNumberOfPlayers() {
		System.out.print("Number of players (2 - 8): ");
		return getUserInputInt();
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

	private static boolean newHighScore(int playerScore) {
		return playerScore > highScores.get(highScores.size() - 1).getScore();
	}

	private static boolean getResponseFromPlayer() {
		String response = getUserInputString();
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
		int bet = getUserInputInt();
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
		String name = getUserInputString();
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

	/**
	 * Opens a Scanner on System.in and returns the value the user inputs as a String.
	 * @return value entered by user as a String.
	 */
	private static String getUserInputString() {
		try (Scanner sc = new Scanner(System.in)) {
			return sc.next();
		}
	}

	/**
	 * Opens a Scanner on System.in and returns the next Integer entered by the user.
	 * @return next Integer entered by the user.
	 */
	private static int getUserInputInt() {
		try (Scanner sc = new Scanner(System.in)) {
			return sc.nextInt();
		}
	}
}

