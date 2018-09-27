/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2016-2017 Gary F. Pollice
 *******************************************************************************/
package student.gettysburg.engine.common;

import static gettysburg.common.GbgGameStep.UMOVE;
import static student.gettysburg.engine.common.CoordinateImpl.makeCoordinate;

import java.util.Collection;

import gettysburg.common.ArmyID;
import gettysburg.common.BattleDescriptor;
import gettysburg.common.BattleResolution;
import gettysburg.common.Coordinate;
import gettysburg.common.Direction;
import gettysburg.common.GbgGame;
import gettysburg.common.GbgGameStatus;
import gettysburg.common.GbgGameStep;
import gettysburg.common.GbgUnit;
import student.gettysburg.engine.state.GbgGameBoard;
import student.gettysburg.engine.state.GbgGameState;
import student.gettysburg.engine.utility.configure.BattleOrder;

/**
 * This is the game engine master class that provides the interface to the game
 * implementation. DO NOT change the name of this file and do not change the
 * name ofthe methods that are defined here since they must be defined to implement the
 * GbgGame interface.
 * 
 * @version Jun 9, 2017
 */
public class GettysburgEngine implements GbgGame
{
	protected static GettysburgEngine theGame; 	// Singleton of the GbgGame
	
	protected GbgGameBoard gameBoard; // Information about the game board
	protected GbgGameState gameState; // Information about the state of the game
	
	protected GettysburgEngine() {
		// Initialize data structures
		this.gameBoard = new GbgGameBoard();
		
		// Populate initial board state
		this.gameBoard.summonReinforcements(BattleOrder.getBattleOrder(ArmyID.UNION), 0);
		this.gameBoard.summonReinforcements(BattleOrder.getBattleOrder(ArmyID.CONFEDERATE), 0);
		
		// Set global game state
		this.gameState = GbgGameState.makeGameState(UMOVE, 1);
	}
	
	// ============================================================================
	// 						Implementation of Singleton Pattern
	// ============================================================================
	/**
	 * @return the singleton GettysburgEngine
	 */
	public static GettysburgEngine getTheGame() {
		if(theGame == null) {
			return makeNewGame();
		}
		return theGame;
	}
	
	/**
	 * @return the newly created singleton GettysburgEngine
	 */
	public static GettysburgEngine makeNewGame() {
		//System.out.println("Warning: the game is being created *without* the test functionality!");
		theGame = new GettysburgEngine();
		return theGame;
	}
	
	// ============================================================================
	//							Miscellaneous extra methods
	// ============================================================================
	public GbgGameBoard getGameBoard() {
		return this.gameBoard;
	}
	
	public GbgGameState getGameState() {
		return this.gameState;
	}
	
	// ============================================================================
	// 						Public GbgGame Interface methods
	// ============================================================================
	/*
	 * @see gettysburg.common.GbgGame#endBattleStep()
	 */
	@Override
	@Deprecated
	public void endBattleStep()
	{
		this.gameState = this.gameState.endBattleStep();
	}

	/*
	 * @see gettysburg.common.GbgGame#endMoveStep()
	 */
	@Override
	@Deprecated
	public void endMoveStep()
	{
		this.gameState = this.gameState.endMoveStep();
	}

	/*
	 * @see gettysburg.common.GbgGame#endStep()
	 */
	@Override
	public GbgGameStep endStep()
	{
		this.gameState = this.gameState.endStep();
		return this.gameState.getStep();
	}

	/*
	 * @see gettysburg.common.GbgGame#getBattlesToResolve()
	 */
	@Override
	public Collection<BattleDescriptor> getBattlesToResolve()
	{
		return this.gameState.getBattlesToResolve();
	}

	/*
	 * @see gettysburg.common.GbgGame#getCurrentStep()
	 */
	@Override
	public GbgGameStep getCurrentStep()
	{
		return this.gameState.getStep();
	}
	
	/*
	 * @see gettysburg.common.GbgGame#getGameStatus()
	 */
	@Override
	public GbgGameStatus getGameStatus()
	{
		return this.gameState.getStatus();
	}
	
	/*
	 * @see gettysburg.common.GbgGame#getTurnNumber()
	 */
	@Override
	public int getTurnNumber()
	{
		return this.gameState.getTurn();
	}

	/*
	 * @see gettysburg.common.GbgGame#getUnitFacing(int)
	 */
	@Override
	public Direction getUnitFacing(GbgUnit unit)
	{
		return unit.getFacing();
	}

	/*
	 * @see gettysburg.common.GbgGame#getUnitsAt(gettysburg.common.Coordinate)
	 */
	@Override
	public Collection<GbgUnit> getUnitsAt(Coordinate where)
	{
		return this.gameBoard.getUnitsAt(where);
	}
	
	@Override
	public GbgUnit getUnit(String leader, ArmyID army) {
		return this.gameBoard.getUnit(leader, army);
	}

	/*
	 * @see gettysburg.common.GbgGame#moveUnit(gettysburg.common.GbgUnit, gettysburg.common.Coordinate, gettysburg.common.Coordinate)
	 */
	@Override
	public void moveUnit(GbgUnit unit, Coordinate from, Coordinate to)
	{
		CoordinateImpl toImpl = makeCoordinate(to);
		CoordinateImpl fromImpl = makeCoordinate(from);
		this.gameState.moveUnit(unit, fromImpl, toImpl);
	}

	/*
	 * @see gettysburg.common.GbgGame#resolveBattle(int)
	 */
	@Override
	public BattleResolution resolveBattle(BattleDescriptor battle)
	{
		return this.gameState.resolveBattle(battle);
	}

	/*
	 * @see gettysburg.common.GbgGame#setUnitFacing(gettysburg.common.GbgUnit, gettysburg.common.Direction)
	 */
	@Override
	public void setUnitFacing(GbgUnit unit, Direction direction)
	{
		this.gameState.setUnitFacing(unit, direction);
	}

	/*
	 * @see gettysburg.common.GbgGame#whereIsUnit(gettysburg.common.GbgUnit)
	 */
	@Override
	public Coordinate whereIsUnit(GbgUnit unit)
	{
		return this.gameBoard.whereIsUnit(unit);
	}

	/*
	 * @see gettysburg.common.GbgGame#whereIsUnit(java.lang.String, gettysburg.common.ArmyID)
	 */
	@Override
	public Coordinate whereIsUnit(String leader, ArmyID army)
	{
		return whereIsUnit(getUnit(leader, army));
	}

}
