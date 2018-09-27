package student.gettysburg.engine.state;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.GbgGameStep.CBATTLE;
import static gettysburg.common.GbgGameStep.CMOVE;
import static gettysburg.common.GbgGameStep.UBATTLE;
import static gettysburg.common.GbgGameStep.UMOVE;

import java.util.Collection;

import gettysburg.common.ArmyID;
import gettysburg.common.BattleDescriptor;
import gettysburg.common.BattleResolution;
import gettysburg.common.Direction;
import gettysburg.common.GbgGameStatus;
import gettysburg.common.GbgGameStep;
import gettysburg.common.GbgUnit;
import gettysburg.common.exceptions.GbgInvalidActionException;
import student.gettysburg.engine.common.CoordinateImpl;
import student.gettysburg.engine.common.GettysburgEngine;

public abstract class GbgGameState {
	protected int currentTurn;
	protected final GbgGameStatus currentStatus;
	
	protected final GbgGameStep currentStep;
	protected final GbgGameStep nextStep;
	protected final ArmyID theArmy;
	
	public GbgGameState(GbgGameStep theStep, GbgGameStep nextStep, ArmyID army, int turn, GbgGameStatus status) {
		this.currentStep = theStep;
		this.nextStep = nextStep;
		this.theArmy = army;
		this.currentTurn = turn;
		this.currentStatus = status;
	}
	
	
	// ============================================================================
	// 						Functionality for switching steps
	// ============================================================================
	/**
	 * Try to end battle step. Return the next state, or an exception if fails
	 */
	public GbgGameState endBattleStep() {
		throw new GbgInvalidActionException("Not currently in a battle step!");
	}
	
	/**
	 * Try to end move step. Return the next state, or an exception if fails
	 */
	public GbgGameState endMoveStep() {
		throw new GbgInvalidActionException("Not currently in a move step!");
	}
	
	/**
	 * Try to end step. Return the next step, or an exception if fails.
	 * Utilizes template pattern to have states change the functionality.
	 */
	public final GbgGameState endStep() {
		if(this.canEndStep()) {
			this.onEndStep();
			return makeGameState(this.nextStep, this.currentTurn);
		}
		else {
			throw new GbgInvalidActionException("Cannot end step at this time!");
		}
	}
	
	
	// ============================================================================
	//						Hooks to be overridden by states
	// ============================================================================
	/**
	 * @return whether or not the step can be ended right now
	 */
	protected boolean canEndStep() {
		return true;
	}
	
	/**
	 * Executes actions that happen at the end of the step
	 */
	protected abstract void onEndStep();

	
	// ============================================================================
	// 						Functionality for user actions 
	// ============================================================================
	/**
	 * Attempt to move a unit.
	 */
	public void moveUnit(GbgUnit unit, CoordinateImpl from, CoordinateImpl to) {
		throw new GbgInvalidActionException("Cannot move units during this step!");
	}
	
	/**
	 * Attempt to set a unit's facing
	 */
	public void setUnitFacing(GbgUnit unit, Direction d) {
		throw new GbgInvalidActionException("Cannot change unit facing during the battle step!");
	}
	
	/**
	 * Attempt to get the battles to resolve.
	 */
	public Collection<BattleDescriptor> getBattlesToResolve() {
		throw new GbgInvalidActionException("Cannot resolve battles during the move step!");
	}
	
	/**
	 * Attempt to resolve a battle.
	 */
	public BattleResolution resolveBattle(BattleDescriptor bd) {
		throw new GbgInvalidActionException("Cannot resolve battles during the move step!");
	}
	
	
	// ============================================================================
	// 						Generic functionality for all states
	// ============================================================================
	/**
	 * @return the army corresponding to this GameState
	 */
	public ArmyID getArmy() {
		return this.theArmy;
	}
	
	/**
	 * @return the enemy army corresponding to this GameState
	 */
	public ArmyID getEnemy() {
		return this.theArmy != null ? ArmyID.values()[1 - this.theArmy.ordinal()] : null;
	}
	
	/**
	 * @return the GbgGameStep corresponding to this GameState
	 */
	public GbgGameStep getStep() {
		return this.currentStep;
	}
	
	/**
	 * @return the GbgGameStatus corresponding to this GameState
	 */
	public GbgGameStatus getStatus() {
		return this.currentStatus;
	}
	
	/**
	 * Set the current turn number
	 */
	public void setTurn(int t) {
		this.currentTurn = t;
	}
	
	/**
	 * @return the current turn number
	 */
	public int getTurn() {
		return this.currentTurn;
	}
	
	/**
	 * Increment the current turn counter.
	 */
	public void incrementTurn() {
		this.currentTurn++;
		GettysburgEngine.getTheGame().getGameBoard().resetMovementTrackers();
	}
	
	
	// ============================================================================
	// 						Factory method for creating States
	// ============================================================================
	/**
	 * Creation method for GbgGameState based on the step
	 * @param g the GbgGameStep
	 * @param turn 
	 * @return the GbgGameState matching this step
	 */
	public static GbgGameState makeGameState(GbgGameStep g, int turn) {
		switch(g) {
			case UMOVE: return new GameStateMoveStep(UMOVE, UBATTLE, UNION, turn);
			case UBATTLE: return new GameStateBattleStep(UBATTLE, CMOVE, UNION, turn);
			case CMOVE: return new GameStateMoveStep(CMOVE, CBATTLE, CONFEDERATE, turn);
			case CBATTLE: return new GameStateBattleStep(CBATTLE, UMOVE, CONFEDERATE, turn);
			case GAME_OVER: return new GameStateGameOver(GbgGameStatus.UNION_WINS, turn);
			default: return null;
		}
	}
}
