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
package student.gettysburg.engine;

import java.util.Collection;

import gettysburg.common.BattleDescriptor;
import gettysburg.common.Coordinate;
import gettysburg.common.GbgGame;
import gettysburg.common.GbgUnit;
import gettysburg.common.TestGbgGame;
import gettysburg.engine.common.TestGettysburgEngine;
import student.gettysburg.engine.common.BattleDescriptorImpl;
import student.gettysburg.engine.common.CoordinateImpl;
import student.gettysburg.engine.common.GettysburgEngine;

/**
 * A factory class that contains creation methods for all of the components of
 * the Gettysburg game, including the game itself.
 * @version Jul 30, 2017
 */
public final class GettysburgFactory
{
	/**
	 * Creation method for a new Gettysburg game instance.
	 * @return a new Gettysburg game
	 */
	public static GbgGame makeGame()
	{
		return GettysburgEngine.makeNewGame();
	}
	
	/**
	 * Creation method for a test Gettysburg game
	 * @return the TestGbgGame instance
	 */
	public static TestGbgGame makeTestGame()
	{
		return TestGettysburgEngine.makeNewGame();
	}
	
	/**
	 * Factory method for creating Coordinates. This method makes a Coordinate
	 * instance that is used by the GettysburgEngine implementation and internal
	 * to the engine's classes. There is no requirement by the client that objects
	 * of this type are used to implement the Coordinate interface.
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the Coordinate object as implemented by the Gettysburg engine.
	 */
	public static Coordinate makeCoordinate(int x, int y)
	{
		return CoordinateImpl.makeCoordinate(x, y);
	}
	
	/**
	 * Factory method for creating a BattleDescriptor.
	 * @param attackers a collection of attacking units
	 * @param defenders a collection of defending units
	 * @return the BattleDescriptor that is created by this method
	 */
	public static BattleDescriptor makeBattleDescriptor(
			Collection<GbgUnit> attackers, Collection<GbgUnit> defenders)
	{
		return BattleDescriptorImpl.makeBattleDescriptorImpl(attackers, defenders);
	}
}
