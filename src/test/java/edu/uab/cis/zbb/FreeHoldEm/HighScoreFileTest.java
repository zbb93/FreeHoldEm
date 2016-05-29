package edu.uab.cis.zbb.FreeHoldEm;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/*
import edu.uab.cis.zbb.FreeHoldEm.HighScore;
import edu.uab.cis.zbb.FreeHoldEm.HighScoreFile;*/
//An example of testing.
public class HighScoreFileTest {
	
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
		HighScore h = new HighScore("Mark", 65);
		HighScore h2 = new HighScore("John", 555);
		HighScore h3 = new HighScore("Will", 11);
		
		file.addHighScore(h);
		file.addHighScore(h2);
		file.addHighScore(h3);
		file.sortHighScores();
		
		ArrayList<HighScore> excpectedList = new ArrayList<>();
		excpectedList.add(h2);
		excpectedList.add(h);
		excpectedList.add(h3);

		assertArrayEquals(excpectedList.toArray(), file.getHighScoreList().toArray());
		
	}
	@Test
	public void testHighScoreReading() {
		file.addHighScore(new HighScore("Mark", 555));
		file.writePlayersIntoFile();
		file.loadFile();
		assertEquals("Mark" + HighScore.getHighScoreDelimiter() + "555\n", file.getHighScoreByPlayer("Mark"));
	}
	
	@After
	public void destroyFile() {
		if(file.exists()) {
			file.delete();
		}
	}
}
