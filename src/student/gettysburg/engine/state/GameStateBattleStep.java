package student.gettysburg.engine.state;

import java.util.Collection;
import java.util.List;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidActionException;
import student.gettysburg.engine.common.GettysburgEngine;
import student.gettysburg.engine.utility.configure.BattleOrder;
import student.gettysburg.engine.utility.configure.UnitInitializer;
import student.gettysburg.engine.validation.ValidatorFactory;

public class GameStateBattleStep extends GbgGameState {

	public GameStateBattleStep(GbgGameStep thisStep, GbgGameStep nextStep, ArmyID army, int turn) {
		super(thisStep, nextStep, army, turn, GbgGameStatus.IN_PROGRESS);
	}

	/**
	 * Override the endBattleStep functionality to allow it in this state only.
	 */
	@Override
	public GbgGameState endBattleStep() {
		return this.endStep();
	}

	/**
	 * Override the canEndStep functionality, so the state can only be ended if all battles have been resolved.
	 */
	@Override
	protected boolean canEndStep() {
		if(!this.getBattlesToResolve().isEmpty())
			throw new GbgInvalidActionException("Cannot end turn until all battles have been resolved!");
		
		return true;
	}
	
	/**
	 * Override the onEndStep functionality so the turn is incremented, reinforcements are summoned,
	 * and the battle trackers are reset.
	 */
	@Override
	protected void onEndStep() {
		if(this.currentStep.equals(GbgGameStep.CBATTLE)) {
			this.incrementTurn();
		}
		
		GettysburgEngine.getTheGame().getGameBoard().resetBattleTrackers();
		List<UnitInitializer> uis = BattleOrder.getBattleOrder(this.getEnemy());
		int turn = GettysburgEngine.getTheGame().getTurnNumber();
		GettysburgEngine.getTheGame().getGameBoard().summonReinforcements(uis, turn);
	}

	/**
	 * Override the getBattlesToResolve functionality to allow it in this state only.
	 */
	@Override
	public Collection<BattleDescriptor> getBattlesToResolve() {
		return GettysburgEngine.getTheGame().getGameBoard().getBattlesToResolve(this.getArmy());
	}

	/**
	 * Override the resolveBattle functionality to allow it in this state only.
	 */
	@Override
	public BattleResolution resolveBattle(BattleDescriptor bd) {
		this.getBattlesToResolve();
		ValidatorFactory.getBattleValidators().forEach(av -> av.validate(bd));
		return GettysburgEngine.getTheGame().getGameBoard().resolveBattle(bd);
	}
}
