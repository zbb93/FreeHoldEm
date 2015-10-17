package application;
//So we can sort them in arrayList
public class HighScore {
	private final String name;
	private final int score;
	public final static String HIGHSCORE_DELIMITER = "     ";
	
	public HighScore(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getScore() {
		return this.score;
	}
	
	/**
	 * Converts a high score to a string representation.
	 * @return A string representation of high score with name first followed by
	 * a tab and then the score.
	 */
	@Override
	public String toString() {
		String asString = new String("");
		asString += name;
		asString += HIGHSCORE_DELIMITER;
		asString += Integer.toString(score);
		return asString;
	}

}
