package student.gettysburg.engine.state;

import gettysburg.common.GbgGameStatus;
import gettysburg.common.GbgGameStep;

public class GameStateGameOver extends GbgGameState {

	public GameStateGameOver(GbgGameStatus status, int turn) {
		super(GbgGameStep.GAME_OVER, GbgGameStep.GAME_OVER, null, turn, status);
	}

	/**
	 * Override the onEndStep functionality to do nothing, as the game is over.
	 */
	@Override
	protected void onEndStep() {
		return;
	}
}
