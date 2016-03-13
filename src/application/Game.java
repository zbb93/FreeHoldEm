package application;

import java.util.Scanner;
public class Game {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
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
		game.cleanUp();
		
	}
	private static void betLoop(FreeHoldEm game) {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter your bet:");
		int playerBet = sc.nextInt();
		game.bet(playerBet);
		if (game.bettingComplete()) {
			return;
		} else {
			betLoop(game);
		}
	}
}

