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
package gettysburg.engine.common;

import java.util.List;

import gettysburg.common.*;
import student.gettysburg.engine.common.*;
import student.gettysburg.engine.state.GbgGameState;

/**
 * Test implementation of the Gettysburg game.
 * @version Jul 31, 2017
 */
public class TestGettysburgEngine extends GettysburgEngine implements TestGbgGame
{
	private TestGettysburgEngine() {
		super();
	}
	
	public static TestGettysburgEngine getTheGame() {
		if(theGame == null) {
			return makeNewGame();
		}
		if(theGame instanceof TestGettysburgEngine) 
			return (TestGettysburgEngine)theGame;
		else
			throw new RuntimeException("The current game is not a test game!");
	}
	
	public static TestGettysburgEngine makeNewGame() {
		theGame = new TestGettysburgEngine();
		return (TestGettysburgEngine)theGame;
	}

	/*
	 * @see gettysburg.common.TestGbgGame#clearBoard()
	 */
	@Override
	public void clearBoard()
	{
		this.getGameBoard().clearBoard();
		this.setGameTurn(0);
	}

	/*
	 * @see gettysburg.common.TestGbgGame#putUnitAt(gettysburg.common.GbgUnit, int, int, gettysburg.common.Direction)
	 */
	@Override
	public void putUnitAt(GbgUnit arg0, int arg1, int arg2, Direction arg3)
	{
		CoordinateImpl c = CoordinateImpl.makeCoordinate(arg1, arg2);
		arg0.setFacing(arg3);
		this.getGameBoard().placeUnit(arg0, c);
	}

	@Override
	public void setBattleResults(List<BattleResult> results)  
	{
		BattleResolver.setOverride(results);
	}

	/*
	 * @see gettysburg.common.TestGbgGame#setGameStep(gettysburg.common.GbgGameStep)
	 */
	@Override
	public void setGameStep(GbgGameStep arg0)
	{
		this.gameState = GbgGameState.makeGameState(arg0, this.gameState.getTurn());
	}

	/*
	 * @see gettysburg.common.TestGbgGame#setGameTurn(int)
	 */
	@Override
	public void setGameTurn(int arg0)
	{
		this.gameState.setTurn(arg0);
	}

}
