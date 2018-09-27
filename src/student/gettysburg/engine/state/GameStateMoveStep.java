package student.gettysburg.engine.state;

import gettysburg.common.*;
import student.gettysburg.engine.common.CoordinateImpl;
import student.gettysburg.engine.common.GettysburgEngine;
import student.gettysburg.engine.validation.ValidatorFactory;

public class GameStateMoveStep extends GbgGameState {

	public GameStateMoveStep(GbgGameStep theStep, GbgGameStep nextStep, ArmyID army, int turn) {
		super(theStep, nextStep, army, turn, GbgGameStatus.IN_PROGRESS);
	}

	/**
	 * Override the endMoveStep functionality to allow it in this state only.
	 */
	@Override
	public GbgGameState endMoveStep() {
		return this.endStep();
	}
	
	/**
	 * Override the onEndStep functionality to include removing stacked units.
	 */
	@Override
	public void onEndStep() {
		GettysburgEngine.getTheGame().getGameBoard().removeStackedUnits();
	}
	
	/**
	 * Override the moveUnit functionality to allow it in this state only.
	 */
	@Override
	public void moveUnit(GbgUnit unit, CoordinateImpl from, CoordinateImpl to) {
		
		ValidatorFactory.getMoveValidators().forEach(av -> av.validate(unit, from, to));
		GettysburgEngine.getTheGame().getGameBoard().moveUnit(unit, to);
	}
	
	/**
	 * Override the setUnitFacing functionality to allow it in this state only.
	 */
	@Override
	public void setUnitFacing(GbgUnit unit, Direction d) {
		ValidatorFactory.getTurnValidators().forEach(av -> av.validate(unit, d));
		GettysburgEngine.getTheGame().getGameBoard().turnUnit(unit, d);
	}
}
