/*
 * FreeHoldEm
 * Copyright 2015-2017 by Zachary Bowen
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.github.zbb93.FreeHoldEm;

public class HighScore {
	private final String name;
	private final int score;
	public final static String HIGHSCORE_DELIMITER = "\t\t\t";
	
	public HighScore(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
	/**
	 * Converts a high score to a string representation.
	 * @return A string representation of high score with name first followed by
	 * a tab and then the score.
	 */
	@Override
	public String toString() {
		String asString = "";
		asString += name;
		asString += HIGHSCORE_DELIMITER;
		asString += Integer.toString(score);
		asString += "\n";
		return asString;
	}

}
