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
package student.gettysburg.engine.utility.configure;

import static student.gettysburg.engine.common.CoordinateImpl.makeCoordinate;
import static student.gettysburg.engine.common.GbgUnitImpl.makeUnit;
import gettysburg.common.*;
/**
 * A simple class with a single static method that returns the order of battle for
 * each side. It is to be used in initializing the game.
 * 
 * @version Jul 2, 2017
 */
public class UnitInitializer
{
	public int turn;
	public Coordinate where;
	public GbgUnit unit;
	
	public UnitInitializer(int turn, int x, int y, ArmyID id, int combatFactor, 
			Direction facing, String leader, int movementFactor,
			UnitSize unitSize, UnitType unitType)
	{
		this.turn = turn;
		this.where = makeCoordinate(x, y);
		this.unit = makeUnit(id, combatFactor, facing, leader, movementFactor, 
				unitSize, unitType);
	}
}
