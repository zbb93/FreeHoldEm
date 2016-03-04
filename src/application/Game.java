package application;

import java.util.Scanner;
public class Game {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.print("Number of players (2 - 8): ");
		int numberPlayers = sc.nextInt();                
		FreeHoldEm game = new FreeHoldEm(numberPlayers);
		
	}
}
