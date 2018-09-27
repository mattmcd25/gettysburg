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

import gettysburg.common.*;
import static gettysburg.common.ArmyID.*;
import static gettysburg.common.Direction.*;
import static gettysburg.common.UnitSize.*;
import static gettysburg.common.UnitType.*;
import java.util.*;

/**
 * This class is used for initialization. It has static methods that return the
 * battle order for the Union and Confederate armies.
 * @version Jul 4, 2017
 */
public class BattleOrder
{
	private static UnitInitializer[] confederateBattleOrder = {
		mcu(0, 8, 8, 4, EAST, "Heth", 2, DIVISION, INFANTRY),				// Turn 0: start of game
		mcu(2, 1, 5, 0, EAST, "Hill", 8, ARMY, HQ),						// Turn 2: 7/1 0700
		mcu(2, 1, 5, 4, EAST, "Pender", 2, DIVISION, INFANTRY),
		mcu(2, 1, 5, 2, EAST, "McIntosh", 4, BATTALION, ARTILLERY),
		mcu(2, 1, 5, 2, EAST, "Pegrom", 4, BATTALION, ARTILLERY),
		mcu(3, 16, 1, 0, SOUTH, "Ewell", 8, ARMY, HQ),					// Turn 3: 7/1 0800
		mcu(3, 16, 1, 4, SOUTH, "Rodes", 2, DIVISION, INFANTRY),
		mcu(7, 1, 5, 0, EAST, "R. E. Lee", 8, ARMY_GROUP, HQ),			// Turn 7: 7/1 1200
		mcu(7, 1, 5, 0, EAST, "Longstreet", 8, ARMY, HQ),
		mcu(7, 21, 1, 4, SOUTH, "Early", 2, DIVISION, INFANTRY),
		mcu(8, 1, 5, 2, EAST, "Dance", 4, BATTALION, ARTILLERY),			// Turn 8: 7/1 1300
		mcu(8, 1, 5, 2, EAST, "Nelson", 4, BATTALION, ARTILLERY),
		mcu(8, 1, 5, 4, EAST, "Anderson", 2, DIVISION, INFANTRY),
		mcu(10, 16, 1, 4, SOUTH, "Johnson", 2, DIVISION, INFANTRY),		// Turn 10: 7/1 1500
		mcu(12, 1, 5, 3, EAST, "McLaws", 2, DIVISION, INFANTRY),			// Turn 12: 7/1/ 1700
		mcu(12, 1, 5, 3, EAST, "Hood", 2, DIVISION, INFANTRY),
		mcu(19, 1, 5, 2, EAST, "Alexander", 4, BATTALION, ARTILLERY),		// Turn 19: 7/2 1000
		mcu(19, 1, 5, 2, EAST, "Esthelmon", 4, BATTALION, ARTILLERY),
		mcu(19, 1, 5, 3, EAST, "Pickett", 2, DIVISION, INFANTRY),
		mcu(21, 16, 1, 0, SOUTH, "Stuart", 8, ARMY, HQ),					// Turn 21: 7/2 1200
		mcu(21, 16, 1, 1, SOUTH, "Hampton", 4, BRIGADE, CAVALRY),
		mcu(21, 16, 1, 1, SOUTH, "F. Lee", 4, BRIGADE, CAVALRY),
		mcu(21, 16, 1, 1, SOUTH, "W. H. F. Lee", 4, BRIGADE, CAVALRY),
		mcu(21, 16, 1, 1, SOUTH, "Jenkins", 4, BRIGADE, CAVALRY),
		mcu(23, 1, 5, 1, EAST, "Imboden", 4, BRIGADE, CAVALRY),			// Turn 23: 7/2 1400
		mcu(23, 1, 5, 1, EAST, "Robertson", 4, BRIGADE, CAVALRY),
		mcu(23, 1, 5, 1, EAST, "Jones", 4, BRIGADE, CAVALRY),
	};
	
	private static UnitInitializer[] unionBattleOrder = {
		muu(0, 11, 11, 1, WEST, "Gamble", 4, BRIGADE, CAVALRY),			// Turn 0: start of game
		muu(0, 13, 9, 1, SOUTH, "Devin", 4, BRIGADE, CAVALRY),
		muu(0, 7, 28, 0, NORTHEAST, "Reynolds", 8, ARMY, HQ),
		muu(0, 7, 28, 3, NORTHEAST, "Wadsworth", 2, DIVISION, INFANTRY),
		muu(0, 7, 28, 3, NORTHEAST, "Robinson", 2, DIVISION, INFANTRY),
		muu(0, 7, 28, 3, NORTHEAST, "Rowley", 2, DIVISION, INFANTRY),
		muu(3, 14, 28, 0, NORTH, "Howard", 8, ARMY, HQ),					// Turn 3: 7/1 0800
		muu(3, 14, 28, 3, NORTH, "von Steinwehr", 2, DIVISION, INFANTRY),
		muu(3, 14, 28, 2, NORTH, "Schurz", 2, DIVISION, INFANTRY),
		muu(3, 7, 28, 3, NORTHEAST, "Barlow", 2, DIVISION, INFANTRY),
		muu(9, 22, 22, 0, NORTHWEST, "Slocum", 8, ARMY, HQ),				// Turn 9: 7/1 1400
		muu(9, 22, 22, 3, NORTHWEST, "Williams", 2, DIVISION, INFANTRY),
		muu(9, 22, 22, 2, NORTHWEST, "Geary", 2, DIVISION, INFANTRY),
		muu(9, 7, 28, 0, NORTHEAST, "Sickles", 8, ARMY, HQ),
		muu(9, 7, 28, 3, NORTHEAST, "Birney", 2, DIVISION, INFANTRY),
		muu(13, 14, 28, 0, NORTH, "Meade", 8, ARMY_GROUP, HQ),			// Turn 13: 7/1 1800
		muu(13, 14, 28, 0, NORTH, "Hancock", 8, ARMY, HQ),
		muu(13, 14, 28, 3, NORTH, "Caldwell", 2, DIVISION, INFANTRY),
		muu(13, 14, 28, 3, NORTH, "Gibbon", 2, DIVISION, INFANTRY),
		muu(13, 14, 28, 3, NORTH, "Hays", 2, DIVISION, INFANTRY),
		muu(13, 7, 28, 3, NORTHEAST, "Humphreys", 2, DIVISION, INFANTRY),
		muu(14, 14, 28, 2, NORTH, "1st Regular", 4, BATTALION, ARTILLERY), // Turn 14: 7/1 1900
		muu(14, 14, 28, 2, NORTH, "1st Volunteer", 4, BATTALION, ARTILLERY),
		muu(14, 14, 28, 2, NORTH, "2nd Volunteer", 4, BATTALION, ARTILLERY),
		muu(14, 14, 28, 2, NORTH, "3rd Volunteer", 4, BATTALION, ARTILLERY),
		muu(14, 14, 28, 2, NORTH, "4th Volunteer", 4, BATTALION, ARTILLERY),
		muu(14, 22, 22, 0, NORTHWEST, "Sykes", 8, ARMY, HQ),
		muu(14, 22, 22, 2, NORTHWEST, "Barnes", 2, DIVISION, INFANTRY),
		muu(14, 22, 22, 2, NORTHWEST, "Ayres", 2, DIVISION, INFANTRY),
		muu(14, 22, 22, 2, NORTHWEST, "Crawford", 2, DIVISION, INFANTRY),
		muu(18, 22, 22, 0, NORTHWEST, "Pleasonton", 8, ARMY, HQ),			// Turn 18: 7/2 0900
		muu(18, 22, 22, 1, NORTHWEST, "Custer", 4, BRIGADE, CAVALRY),
		muu(18, 22, 22, 1, NORTHWEST, "Farnsworth", 4, BRIGADE, CAVALRY),
		muu(18, 22, 22, 1, NORTHWEST, "McIntosh", 4, BRIGADE, CAVALRY),
		muu(18, 22, 22, 1, NORTHWEST, "Gregg", 4, BRIGADE, CAVALRY),
		muu(18, 22, 22, 1, NORTHWEST, "Huey", 4, BRIGADE, CAVALRY),
		muu(21, 22, 22, 0, NORTHWEST, "Sedgwick", 8, ARMY, HQ),			// Turn 21: 7/2 1200
		muu(21, 22, 22, 3, NORTHWEST, "Wirght", 2, DIVISION, INFANTRY),
		muu(21, 22, 22, 3, NORTHWEST, "Howe", 2, DIVISION, INFANTRY),
		muu(21, 22, 22, 3, NORTHWEST, "Newton", 2, DIVISION, INFANTRY),
		muu(31, 7, 28, 3, NORTHEAST, "Merritt", 2, DIVISION, INFANTRY),	// Turn 31: 7/3 0800
	};
	
	public static List<UnitInitializer> getBattleOrder(ArmyID army)
	{
		final UnitInitializer[] battleOrderArray =
				army == UNION ? unionBattleOrder : confederateBattleOrder;
		return new LinkedList<UnitInitializer>(Arrays.asList(battleOrderArray));
	}
	
	private static UnitInitializer mui(int turn, int x, int y, ArmyID id, int combatFactor, 
			Direction facing, String leader, int movementFactor,
			UnitSize unitSize, UnitType unitType)
	{
		return new UnitInitializer(turn, x, y, id, combatFactor, facing, leader, movementFactor,
				unitSize, unitType);
	}
	
	private static UnitInitializer mcu(int turn, int x, int y, int combatFactor, 
			Direction facing, String leader, int movementFactor,
			UnitSize unitSize, UnitType unitType)
	{
		return mui(turn, x, y, CONFEDERATE, combatFactor, facing, leader, movementFactor,
				unitSize, unitType);
	}
	
	private static UnitInitializer muu(int turn, int x, int y, int combatFactor, 
			Direction facing, String leader, int movementFactor,
			UnitSize unitSize, UnitType unitType)
	{
		return mui(turn, x, y, UNION, combatFactor, facing, leader, movementFactor,
				unitSize, unitType);
	}
}