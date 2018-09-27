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

import java.util.*;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidCoordinateException;

/**
 * Implementation of the gettysburg.common.Coordinate interface. Additional methods
 * used in this implementation are added to this class. Clients should <em>ONLY</em>
 * use the public Coordinate interface. Additional methods
 * are only for engine internal use.
 * 
 * @version Jun 9, 2017
 */
public class CoordinateImpl implements Coordinate
{
	private static HashMap<OrderedPair, CoordinateImpl> coordPool = new HashMap<OrderedPair, CoordinateImpl>();
	
	private final OrderedPair pos;
	
	/**
	 * Private constructor that is called by the factory method.
	 * @param x
	 * @param y
	 */
	private CoordinateImpl(OrderedPair pos)
	{
		this.pos = pos;
	}
	
	public static CoordinateImpl getCoordinate(int x, int y) {
		OrderedPair pos = new OrderedPair(x, y);
		if (!coordPool.containsKey(pos)) {
			CoordinateImpl c = new CoordinateImpl(pos);
			coordPool.put(pos, c);
		}
		return coordPool.get(pos);
	}
	
	/**
	 * Factory method for creating Coordinates.
	 * @param x
	 * @param y
	 * @return
	 */
	public static CoordinateImpl makeCoordinate(int x, int y)
	{
		if (x < 1 || x > GbgBoard.COLUMNS || y < 1 || y > GbgBoard.ROWS) {
			throw new GbgInvalidCoordinateException(
					"Coordinates for (" + x + ", " + y + ") are out of bounds.");
		}
		return getCoordinate(x, y);
	}
	
	/**
	 * Factory method for creating Coordinates using copy constructor.
	 */
	public static CoordinateImpl makeCoordinate(Coordinate c) 
	{
		if(c == null) return null;
		return makeCoordinate(c.getX(), c.getY());
	}
	
	
	// ==============================================================================
	//						Public Coordinate interface implementations
	// ==============================================================================
	/*
	 * @see gettysburg.common.Coordinate#directionTo(gettysburg.common.Coordinate)
	 */
	@Override
	public Direction directionTo(Coordinate coordinate)
	{
		 int dx = coordinate.getX() - this.getX();
		 int dy = coordinate.getY() - this.getY();
		 
		 if(dy > 0) {
			 if(dx > 0) return Direction.SOUTHEAST;
			 else if(dx < 0) return Direction.SOUTHWEST;
			 else return Direction.SOUTH;
		 }
		 else if(dy < 0) {
			 if(dx > 0) return Direction.NORTHEAST;
			 else if(dx < 0) return Direction.NORTHWEST;
			 else return Direction.NORTH;
		 }
		 else {
			 if(dx > 0) return Direction.EAST;
			 else if(dx < 0) return Direction.WEST;
			 else return Direction.NONE;
		 }
	}

	/*
	 * @see gettysburg.common.Coordinate#distanceTo(gettysburg.common.Coordinate)
	 */
	@Override
	public int distanceTo(Coordinate coordinate)
	{
		return Math.max(Math.abs(coordinate.getX() - this.getX()),
						Math.abs(coordinate.getY() - this.getY()));
	}

	/*
	 * @see gettysburg.common.Coordinate#getX()
	 */
	@Override
	public int getX()
	{
		return this.pos.x;
	}

	/*
	 * @see gettysburg.common.Coordinate#getY()
	 */
	@Override
	public int getY()
	{
		return this.pos.y;
	}

	/**
	 * toString for convenience
	 */
	@Override
	public String toString()
	{
		return pos.toString();
	}

	// ==============================================================================
	//					Additional methods for my custom CoordinateImpl
	// ==============================================================================
	/**
	 * @return the (legal and valid) neighbors surrounding this coordinate.
	 */
	public Collection<CoordinateImpl> getNeighbors() {
		ArrayList<CoordinateImpl> neighbors = new ArrayList<CoordinateImpl>();
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				try {
					if(x != 0 || y != 0) {
						CoordinateImpl c = makeCoordinate(this.getX() + x, this.getY() + y);
						neighbors.add(c);
					}
				}
				catch(GbgInvalidCoordinateException e) {};
			}
		}
		return neighbors;
	}
	
	/**
	 * @return the neighbors surrounding this coordinate that the specified unit would
	 *         be allowed to walk through on a path.
	 */
	public Collection<CoordinateImpl> getAllowedNeighbors(GbgUnit u, CoordinateImpl goal) {
		if(u == null) return getNeighbors();
		else {
			ArmyID enemy = ArmyID.values()[1-u.getArmy().ordinal()];
			Set<CoordinateImpl> enemyControl = GettysburgEngine.getTheGame().getGameBoard().getArmyControl(enemy);
			ArrayList<CoordinateImpl> result = new ArrayList<CoordinateImpl>();
			for(CoordinateImpl n: getNeighbors()) {
				if(goal.equals(n)) result.add(n);
				else if(enemyControl.contains(n)) continue;
				else {
					Set<CoordinateImpl> myControl = GettysburgEngine.getTheGame().getGameBoard().getZoneOfControl(n, u.getFacing());
					boolean OK = true;
					for(CoordinateImpl z : myControl) {
						Collection<GbgUnit> unitsAt = GettysburgEngine.getTheGame().getGameBoard().getUnitsAt(z);
						if(unitsAt != null && !unitsAt.isEmpty() && unitsAt.iterator().next().getArmy().equals(enemy)) {
							OK = false;
							break;
						}
					}
					if(OK) result.add(n);
				}
			}
			return result;
		}
	}
	
	/**
	 * @return the adjacent square in the specified direction. for Zone of Control
	 */
	public CoordinateImpl getNeighborInDirection(Direction d) {
		for(CoordinateImpl c : getNeighbors()) {
			if(this.directionTo(c).equals(d)) return c;
		}
		return null;
	}
}

class OrderedPair { 
	public final int x; 
	public final int y; 
	
	public OrderedPair(int x, int y) { 
		this.x = x; 
		this.y = y; 
	} 
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof OrderedPair))
			return false;
		OrderedPair other = (OrderedPair) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return"(" + x + ", " + y + ")";
	}
} 
