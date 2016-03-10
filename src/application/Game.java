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
			game.takeBets();
			game.dealFlop();
			game.takeBets();
			game.dealTurn();
			game.takeBets();
			game.dealRiver();
			game.takeBets();
			game.pickWinner();
			System.out.print("Would you like to play another hand? (y/n):");
			String response = sc.next();
			if (response.equals("n")) {
				playing = false;
			}
		}
		game.cleanUp();
		
	}
}
