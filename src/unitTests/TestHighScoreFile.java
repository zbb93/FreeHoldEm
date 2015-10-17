package unitTests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import application.HighScore;
import application.HighScoreFile;
//An example of testing.
public class TestHighScoreFile {
	
	private HighScoreFile file;
	@Before
	public void initFile() {
		file = new HighScoreFile("test_case_file.dat");
	}
	//Test functions to assure HighScoreFile class functionality.
	@Test
	public void testIfHighScoreFileExists() {
		assertEquals(true, file.exists());
	}
	@Test
	public void testSortingHighScoresDescendingOrder() {
		file.addHighScore(new HighScore("Mark", 65));
		file.addHighScore(new HighScore("John", 555));
		file.addHighScore(new HighScore("Will", 11));
		file.sortHighScores();
	
	}
	@Test
	public void testHighScoreReading() {
		file.addHighScore(new HighScore("Mark", 555));
		file.writePlayersIntoFile();
		file.loadFile();
		assertEquals("Mark" + HighScore.HIGHSCORE_DELIMITER + "555", file.getHighScore("Mark"));
	}
	@After
	public void destroyFile() {
		if(file.exists()) {
			file.delete();
		}
	}
}
