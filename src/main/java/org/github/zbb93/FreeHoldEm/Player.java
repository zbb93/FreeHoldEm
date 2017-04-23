package org.github.zbb93.FreeHoldEm;
/*
  Represents a player in a card game, can be either
  CPU or human

  TODO: Randomly assign players an integer value
        that determines their personality. High
        value indicates a more aggressive player
        while lower values indicate more cautious
        players?
 */
public class Player {

	private final String name;
	private final Card[] hand = new Card[2];
	private Hand finalHand;
	private int chips;
	private boolean isFolded;
	private final boolean isHuman;
	private int betThisRound;

	/**
	 * Constructor for AI player. Each player starts with 500 chips.
	 * @param name The name of the player
	 */
	public Player(String name) {
		this(name, false);
	}

	public Player(String name, boolean isHuman) {
		this.name = name;
		this.chips = 500;
		this.isFolded = false;
		this.betThisRound = 0;
		this.isHuman = isHuman;
	}

	/**
	 * Method to update the cards a player has in their hand.
	 * @param index index in player card array to place the card.
	 * @param card the card to place into the players hand.
	 */
	public void setCards(int index, Card card) {

		this.hand[index] = card;
	}
	/**
	 * Method to update the hand a player currently has (cards on table +
	 * cards in hand).
	 * @param hand the hand to give the player.
	 */
	public void setHand(Hand hand) {

		finalHand = hand;
	}
	/**
	 * Method to update the amount of chips a player has.
	 * @param newVal The updated amount of chips the player is to have.
	 */
	public void setChips(int newVal) {

		chips = newVal;
	}
	/**
	 * Returns the current hand of the player.
	 * @return The current hand of the player.
	 */
	public Hand getHand() {

		return this.finalHand;
	}
	/**
	 * Returns the name of the player
	 * @return name of the player as string.
	 */
	public String getName() {

		return this.name;
	}
	/**
	 * Method to view the cards a player was dealt.
	 * @return Two element card array representing initial cards a player
	 * was dealt.
	 */
	public Card[] getCards() {

		return this.hand;
	}
	/**
	 * Method to view current amount of chips a player has
	 * @return chips as an integer value.
	 */
	public int getChips() {

		return this.chips;
	}
	/**
	 * Method to cause a player to fold. Sets boolean isFolded to true.
	 */
	public void fold() {

		this.isFolded = true;
	}
	/**
	 * Method to 'unfold' a player before the next hand begins. Sets boolean
	 * isFolded to false.
	 */
	public void unFold() {

		this.isFolded = false;
	}
	/**
	 * Determines whether or not a player has chosen to fold.
	 * @return true if player is folded and false otherwise.
	 */
	public boolean checkFold() {
		return this.isFolded;
	}

	public boolean hasBet() {
		return (this.betThisRound > 0);
	}

	public void bet(int amount) {
		this.betThisRound += amount;
		this.chips -= amount;
	}

	public void resetBet() {
		this.betThisRound = 0;
	}

	public int getBetThisRound() {
		return this.betThisRound;
	}

	public void deductChips(int bet) {
		chips -= bet;
	}

	public boolean isHuman() {
		return isHuman;
	}



	/**
	 * Converts player hand to a string value.
	 * @return A string made of the string value of card1 followed by card2.
	 * Note currently only returns the cards that are in the players hand,
	 * not those that may be included in the best hand.
	 */
	@Override
	public String toString() {

		String card1 = hand[0].toString();
		String card2 = hand[1].toString();
		return (card1 + ", " + card2);
	}
}
