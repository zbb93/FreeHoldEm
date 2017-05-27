package org.github.zbb93.logging;

import java.io.IOException;

/**
 * This class is used when we are unable to initialize an StdGameWatcher and the user wishes to continue with logging
 * functionality disabled.
 * Created by zbb on 5/27/17.
 */
public class NoOpGameWatcher implements GameWatcher {

	@Override
	public void recordGameState(String gameState) {}

	@Override
	public void playerBet(String playerName, int amountBet) throws IOException {}

	@Override
	public void exception(Throwable e, String gameState) {}
}
