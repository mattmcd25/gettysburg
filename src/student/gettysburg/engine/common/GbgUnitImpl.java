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

import gettysburg.common.ArmyID;
import gettysburg.common.Direction;
import gettysburg.common.GbgUnit;
import gettysburg.common.UnitSize;
import gettysburg.common.UnitType;

/**
 * An implementation of the GbgUnit interface. This is basically a data structure
 * that contains properties used in the game engine and information that can
 * be used for a client with a user interface to provide information about the 
 * unit to the player.
 * 
 * @version Jun 11, 2017
 */
public class GbgUnitImpl implements GbgUnit
{
	private ArmyID armyID;
	private int combatFactor;
	private Direction facing;
	private String leader;
	private int movementFactor;
	private UnitSize unitSize;
	private UnitType unitType;
	
	/**
	 * Default constructor needed for JSON processing. When creating
	 * a unit directly, use the factory method.
	 */
	private GbgUnitImpl()
	{
		armyID = null;
		combatFactor = 0;
		facing = null;
		leader = null;
		movementFactor = 0;
		unitSize = null;
		unitType = null;
	}
	
	/**
	 * Factory method to create a unit implementation from scratch.
	 * @param armyID
	 * @param combatFactor
	 * @param facing
	 * @param leader
	 * @param movementFactor
	 * @param name
	 * @param unitSize
	 * @param unitType
	 * @return the unitImpl
	 */
	public static GbgUnitImpl makeUnit(ArmyID armyID, int combatFactor, Direction facing, 
			String leader, int movementFactor, UnitSize unitSize, UnitType unitType)
	{
		final GbgUnitImpl unit = new GbgUnitImpl();
		unit.armyID = armyID;
		unit.combatFactor = combatFactor;
		unit.facing = facing;
		unit.leader = leader;
		unit.movementFactor = movementFactor;
		unit.unitSize = unitSize;
		unit.unitType = unitType;
		return unit;
	}
	
	// ==============================================================================
	//						Public GbgUnit interface implementations
	// ==============================================================================
	/*
	 * @see gettysburg.common.GbgUnit#getArmy()
	 */
	@Override
	public ArmyID getArmy()
	{
		return armyID;
	}

	/*
	 * @see gettysburg.common.GbgUnit#getCombatFactor()
	 */
	@Override
	public int getCombatFactor()
	{
		return combatFactor;
	}

	/*
	 * @see gettysburg.common.GbgUnit#getFacing()
	 */
	@Override
	public Direction getFacing()
	{
		return facing;
	}

	/*
	 * @see gettysburg.common.GbgUnit#setFacing(gettysburg.common.Direction)
	 */
	@Override
	public void setFacing(Direction newFacing)
	{
		this.facing = newFacing;
	}

	/*
	 * @see gettysburg.common.GbgUnit#getLeader()
	 */
	@Override
	public String getLeader()
	{
		return leader;
	}

	/*
	 * @see gettysburg.common.GbgUnit#getMovementFactor()
	 */
	@Override
	public int getMovementFactor()
	{
		return movementFactor;
	}

	/*
	 * @see gettysburg.common.GbgUnit#getSize()
	 */
	@Override
	public UnitSize getSize()
	{
		return unitSize;
	}

	/*
	 * @see gettysburg.common.GbgUnit#getType()
	 */
	@Override
	public UnitType getType()
	{
		return unitType;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((armyID == null) ? 0 : armyID.hashCode());
		result = prime * result + ((leader == null) ? 0 : leader.hashCode());
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GbgUnitImpl)) {
			return false;
		}
		GbgUnitImpl other = (GbgUnitImpl) obj;
		if (armyID != other.armyID) {
			return false;
		}
		if (leader == null) {
			if (other.leader != null) {
				return false;
			}
		} else if (!leader.equals(other.leader)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return this.getLeader();
	}
}
