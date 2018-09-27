/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2016 Gary F. Pollice
 *******************************************************************************/

package master_tests;

import gettysburg.common.*;

/**
 * Unit class for testing
 * @version Oct 4, 2017
 */
class TestUnit implements GbgUnit
{
	private ArmyID armyID;
	private int combatFactor;
	private Direction facing;
	private String leader;
	private int movementFactor;
	private UnitSize unitSize;
	private UnitType unitType;
	
	public TestUnit()
	{
		armyID = null;
		combatFactor = 0;
		facing = null;
		leader = null;
		movementFactor = 0;
		unitSize = null;
		unitType = null;
	}
	
	public static GbgUnit makeUnit(ArmyID armyID, int combatFactor, Direction facing, 
			String leader, int movementFactor)
	{
		final TestUnit unit = new TestUnit();
		unit.armyID = armyID;
		unit.combatFactor = combatFactor;
		unit.facing = facing;
		unit.leader = leader;
		unit.movementFactor = movementFactor;
		return unit;
	}
	
	@Override
	public ArmyID getArmy() { return armyID; }

	@Override
	public int getCombatFactor() { return combatFactor; }

	@Override
	public Direction getFacing() { return facing; }

	@Override
	public void setFacing(Direction newFacing) { facing = newFacing; }

	@Override
	public String getLeader() { return leader; }

	@Override
	public int getMovementFactor() { return movementFactor; }

	@Override
	public UnitSize getSize() { return unitSize; }

	@Override
	public UnitType getType() { return unitType; }
}
