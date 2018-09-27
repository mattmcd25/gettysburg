package student.gettysburg.engine.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import gettysburg.common.ArmyID;
import gettysburg.common.BattleDescriptor;
import gettysburg.common.Direction;
import gettysburg.common.GbgUnit;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import student.gettysburg.engine.common.CoordinateImpl;
import student.gettysburg.engine.common.GettysburgEngine;

public class ValidatorFactory {
	// The pathfinder instance to be used
	private static Pathfinder pf = Pathfinder.getPathfinder("AStar");
	
	// =====================================================================================
	//					Factory methods for creating validator lists
	// =====================================================================================
	/**
	 * @return The validators used to check a move is valid
	 */
	public static List<MoveValidator> getMoveValidators() {
		return Arrays.asList((GbgUnit u, CoordinateImpl from, CoordinateImpl to) -> checkWrongArmy(u),
							 ValidatorFactory::checkAlreadyMoved,
							 ValidatorFactory::checkFromLocation,
							 ValidatorFactory::checkSameStart,
							 ValidatorFactory::checkValidPath,
							 ValidatorFactory::checkOccupiedSquare,
							 ValidatorFactory::checkNotInControl);
	}
	
	/**
	 * @return The validators used to check if setFacing is valid
	 */
	public static List<TurnValidator> getTurnValidators() {
		return Arrays.asList((GbgUnit u, Direction d) -> checkWrongArmy(u),
							 ValidatorFactory::checkAlreadyTurned);
	}
	
	/**
	 * @return The validators used to check if resolveBattle is valid
	 */
	public static List<BattleValidator> getBattleValidators() {
		return Arrays.asList(ValidatorFactory::checkAttackerArmy,
							 ValidatorFactory::checkDefenderArmy,
							 ValidatorFactory::checkAlreadyBattled,
							 ValidatorFactory::checkBattleRange);
	}
	
	// =====================================================================================
	// 						Generic validators for both moving and facing
	// =====================================================================================
	/** Check if the unit belongs to the wrong army **/
	private static void checkWrongArmy(GbgUnit unit) {
		if(!unit.getArmy().equals(GettysburgEngine.getTheGame().getGameState().getArmy())) 
			throw new GbgInvalidMoveException("Cannot modify enemy units during the " + GettysburgEngine.getTheGame().getGameState().getStep().name() + " step!");
	}
	
	// =====================================================================================
	// 							Validators for resolveBattle
	// =====================================================================================
	/** Check if any attackers belong to the wrong army **/
	private static void checkAttackerArmy(BattleDescriptor bd) {
		ArmyID attacker = GettysburgEngine.getTheGame().getGameState().getArmy();
		for(GbgUnit atk : bd.getAttackers()) {
			if(!atk.getArmy().equals(attacker))
				throw new GbgInvalidActionException("A unit from the " + atk.getArmy() + " cannot be an attacker during the " + GettysburgEngine.getTheGame().getGameState().getStep().name() + " step!");
		}
	}
	
	/** Check if any defenders belong to the wrong army **/
	private static void checkDefenderArmy(BattleDescriptor bd) {
		ArmyID defender = ArmyID.values()[1-GettysburgEngine.getTheGame().getGameState().getArmy().ordinal()];
		for(GbgUnit def : bd.getDefenders()) {
			if(!def.getArmy().equals(defender))
				throw new GbgInvalidActionException("A unit from the " + def.getArmy() + " cannot be an defender during the " + GettysburgEngine.getTheGame().getGameState().getStep().name() + " step!");
		}
	}
	
	/** Check that the defenders are all in range of the attackers **/
	private static void checkBattleRange(BattleDescriptor bd) {
		for(GbgUnit atk : bd.getAttackers()) {
			Set<CoordinateImpl> zoc = GettysburgEngine.getTheGame().getGameBoard().getUnitControl(atk);
			for(GbgUnit def : bd.getDefenders()) {
				if(!zoc.contains(GettysburgEngine.getTheGame().getGameBoard().whereIsUnit(def)))
					throw new GbgInvalidActionException("All defenders must be in the zone of control of all attackers!");
			}
		}
	}
	
	/** Check that none of the attackers or defenders have already battled this step **/
	private static void checkAlreadyBattled(BattleDescriptor bd) {
		for(GbgUnit atk : bd.getAttackers()) {
			if(GettysburgEngine.getTheGame().getGameBoard().unitAlreadyBattled(atk)) 
				throw new GbgInvalidActionException("Unit led by " + atk.getLeader() + " already participated in a battle this turn!");		
		}
		for(GbgUnit def : bd.getDefenders()) {
			if(GettysburgEngine.getTheGame().getGameBoard().unitAlreadyBattled(def)) 
				throw new GbgInvalidActionException("Unit led by " + def.getLeader() + " already participated in a battle this turn!");		
		}
	}
	
	// =====================================================================================
	// 								Validators for setFacing
	// =====================================================================================
	/** Check that the unit has not already turned this step **/
	private static void checkAlreadyTurned(GbgUnit unit, Direction d) {
		if(GettysburgEngine.getTheGame().getGameBoard().unitAlreadyTurned(unit)) 
			throw new GbgInvalidMoveException("Unit led by " + unit.getLeader() + " already moved this turn!");		
	}
	
	// =====================================================================================
	// 								Validators for moveUnit
	// =====================================================================================
	/** Check that a valid path exists for the unit, and is short enough **/
	private static void checkValidPath(GbgUnit unit, CoordinateImpl from, CoordinateImpl to) {
		Path p = pf.findPath(unit, from, to);
		if(p == null) 
			throw new GbgInvalidMoveException("There is no valid path from " + from + " to " + to + "!");
		if(p.getMoveDistance() > unit.getMovementFactor())
			throw new GbgInvalidMoveException("The shortest legal path from " + from + " to " + to + " is too far for " + unit.getLeader() + " to move!");
	}
	
	/** Check that the unit has not already moved this step **/
	private static void checkAlreadyMoved(GbgUnit unit, CoordinateImpl from, CoordinateImpl to) {
		if(GettysburgEngine.getTheGame().getGameBoard().unitAlreadyMoved(unit)) 
			throw new GbgInvalidMoveException("Unit led by " + unit.getLeader() + " already moved this turn!");			
	}
	
	/** Check that the from location is actually where the unit is right now **/
	private static void checkFromLocation(GbgUnit unit, CoordinateImpl from, CoordinateImpl to) {
		if(!GettysburgEngine.getTheGame().whereIsUnit(unit).equals(from)) 
			throw new GbgInvalidMoveException("Unit led by " + unit.getLeader() + " cannot be moved from a location it isn't in!");
	}
	
	/** Check that the unit isn't trying to move to the same spot **/
	private static void checkSameStart(GbgUnit unit, CoordinateImpl from, CoordinateImpl to) {
		if(to.equals(from)) 
			throw new GbgInvalidMoveException("Unit cannot be moved to a location it's already in!");
	}
	
	/** Check that the unit isn't trying to move into an occupied square **/
	private static void checkOccupiedSquare(GbgUnit unit, CoordinateImpl from, CoordinateImpl to) {
		if(GettysburgEngine.getTheGame().getUnitsAt(to) != null)
			throw new GbgInvalidMoveException("There is a unit at " + to + " already!");
	}
	
	/** Check that the unit is not already in a zone of control **/
	private static void checkNotInControl(GbgUnit unit, CoordinateImpl from, CoordinateImpl to) {
		if(GettysburgEngine.getTheGame().getGameBoard().getArmyControl(ArmyID.values()[1-unit.getArmy().ordinal()]).contains(from))
			throw new GbgInvalidMoveException("A unit in a zone of control cannot move!");
	}
	
//	/** Used in version one, before actual paths were taken into consideration. **/
//	@SuppressWarnings("unused")
//	@Deprecated
//	private static void checkMaxDistance(GbgUnit unit, CoordinateImpl from, CoordinateImpl to) {
//		if(from.distanceTo(to) > unit.getMovementFactor())
//			throw new GbgInvalidMoveException("Unit led by " + unit.getLeader() + " cannot move " + from.distanceTo(to) + " squares in one turn!");
//	}
}