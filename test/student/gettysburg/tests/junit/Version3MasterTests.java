package student.gettysburg.tests.junit;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.Direction.*;
import static gettysburg.common.GbgGameStatus.IN_PROGRESS;
import static gettysburg.common.GbgGameStep.*;
import static gettysburg.common.UnitSize.*;
import static gettysburg.common.UnitType.*;
import static gettysburg.common.BattleResult.*;
import static org.junit.Assert.*;
import static student.gettysburg.engine.GettysburgFactory.makeCoordinate;
import static student.gettysburg.engine.GettysburgFactory.makeTestGame;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import student.gettysburg.engine.GettysburgFactory;
import student.gettysburg.engine.common.*;
import student.gettysburg.engine.state.GbgGameState;
import student.gettysburg.engine.utility.configure.BattleOrder;
import student.gettysburg.engine.validation.Path;
import student.gettysburg.engine.validation.Pathfinder;

public class Version3MasterTests {
	private GbgGame game;
	private TestGbgGame testGame;
	@SuppressWarnings("unused")
	private GbgUnit gamble, devin, heth, rowley, schurz, rodes, dance, hampton;
		
	@Before
	public void setup()
	{
		game = testGame = makeTestGame();
		gamble = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
		devin = game.getUnitsAt(makeCoordinate(13, 9)).iterator().next();
		heth = game.getUnitsAt(makeCoordinate(8, 8)).iterator().next();
		rowley = GbgUnitImpl.makeUnit(UNION, 3, NORTHEAST, "Rowley", 2, DIVISION, INFANTRY);
		schurz = GbgUnitImpl.makeUnit(UNION, 2, NORTH, "Schurz", 2, DIVISION, INFANTRY);
		rodes = GbgUnitImpl.makeUnit(CONFEDERATE, 4, SOUTH, "Rodes", 2, DIVISION, INFANTRY);
		dance = GbgUnitImpl.makeUnit(CONFEDERATE, 2, EAST, "Dance", 4, BATTALION, ARTILLERY);
		hampton = GbgUnitImpl.makeUnit(CONFEDERATE, 1, SOUTH, "Hampton", 4, BRIGADE, CAVALRY);
		
		gamble.setFacing(WEST);
		devin.setFacing(SOUTH);
		heth.setFacing(EAST);
	}
	
	// ===============================================================================================================
	// ===============================================================================================================
	// ===============================================================================================================
	// 									Test Driven Development for final version features
	// ===============================================================================================================
	// ===============================================================================================================
	// ===============================================================================================================
	// Feature: Paths can't let enemy into units zone of control either
	@Test(expected=GbgInvalidMoveException.class)
	public void testUnitControlBlocksMoving()
	{	
		// _XXX_   C=confederate
		// __C__   X=control
		// U___O   U=gamble (4 move power)
		// __C__   O=goal  
		// _XXX_
		GbgUnit confed2 = BattleOrder.getBattleOrder(CONFEDERATE).get(2).unit;
		testGame.putUnitAt(heth, 3, 2, NORTH);
		testGame.putUnitAt(confed2, 3, 4, SOUTH);
		testGame.putUnitAt(gamble, 1, 3, EAST);
		game.moveUnit(gamble, gc(1,3), gc(5,3));
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void testUnitControlBlocksMoving2()
	{	
		// _XXX_   C=confederate
		// __C__   X=control
		// U____   U=gamble (4 move power)
		// ___O_
		// __C__   O=goal  
		// _XXX_
		GbgUnit confed2 = BattleOrder.getBattleOrder(CONFEDERATE).get(2).unit;
		testGame.putUnitAt(heth, 3, 2, NORTH);
		testGame.putUnitAt(confed2, 3, 5, SOUTH);
		testGame.putUnitAt(gamble, 1, 3, EAST);
		game.moveUnit(gamble, gc(1,3), gc(4,4));
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void testUnitControlBlocksMoving3()
	{	
		// OCU__   C=confederate
		// XXX__   X=control
		// _C___   U=gamble (4 move power)
		GbgUnit confed2 = BattleOrder.getBattleOrder(CONFEDERATE).get(2).unit;
		testGame.putUnitAt(heth, 2, 1, SOUTH);
		testGame.putUnitAt(confed2, 2, 3, NORTH);
		testGame.putUnitAt(gamble, 3, 1, EAST);
		game.moveUnit(gamble, gc(3,1), gc(1,1));
	}
	
	// Feature: Randomization in battle resolution
	// Start by developing a function to convery from battleRatio to an index in the table of results
	@Test
	public void testRatioToIndex()
	{
		assertEquals(0, BattleResolver.ratioToIndex(9.0));
		assertEquals(0, BattleResolver.ratioToIndex(6.0));
		assertEquals(1, BattleResolver.ratioToIndex(5.2));
		assertEquals(1, BattleResolver.ratioToIndex(5.0));
		assertEquals(2, BattleResolver.ratioToIndex(4.2));
		assertEquals(2, BattleResolver.ratioToIndex(4.0));
		assertEquals(3, BattleResolver.ratioToIndex(3.2));
		assertEquals(3, BattleResolver.ratioToIndex(3.0));
		assertEquals(4, BattleResolver.ratioToIndex(2.2));
		assertEquals(4, BattleResolver.ratioToIndex(2.0));
		assertEquals(5, BattleResolver.ratioToIndex(1.2));
		assertEquals(5, BattleResolver.ratioToIndex(1.0));
		
		assertEquals(6, BattleResolver.ratioToIndex(0.75));
		assertEquals(6, BattleResolver.ratioToIndex(0.5));
		assertEquals(7, BattleResolver.ratioToIndex(0.4));
		assertEquals(7, BattleResolver.ratioToIndex(1.0/3.0));
		assertEquals(8, BattleResolver.ratioToIndex(0.3));
		assertEquals(8, BattleResolver.ratioToIndex(0.25));
		assertEquals(9, BattleResolver.ratioToIndex(0.22));
		assertEquals(9, BattleResolver.ratioToIndex(0.20));
		assertEquals(10, BattleResolver.ratioToIndex(0.18));
		assertEquals(10, BattleResolver.ratioToIndex(1.0/6.0));
		assertEquals(11, BattleResolver.ratioToIndex(0.10));
		assertEquals(11, BattleResolver.ratioToIndex(0));
	}
	
	@Test
	public void testRandomBattleResults()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 18, 18, SOUTH);
		testGame.putUnitAt(rowley, 5, 7, NORTH);
		testGame.putUnitAt(hampton, 5, 5, EAST);
		testGame.putUnitAt(gamble, 20, 18, WEST);
		TestBattleDescriptor bd = new TestBattleDescriptor();
		bd.addAttacker(rowley); // CF = 3
		bd.addDefender(hampton); // CF = 1
		TestBattleDescriptor bd1 = new TestBattleDescriptor();
		bd1.addAttacker(gamble); // CF = 1
		bd1.addDefender(heth); // CF = 4
		game.moveUnit(rowley, makeCoordinate(5, 7), makeCoordinate(5, 6));
		game.moveUnit(gamble, makeCoordinate(20, 18), makeCoordinate(19, 18));
		game.endStep();	// CBATTLE

		// Since the result is random, we can only check that it is _A_ valid result
		List<BattleResult> expected1 = Arrays.asList(DELIM, DBACK, EXCHANGE); // ratio = 3.0
		List<BattleResult> expected2 = Arrays.asList(ABACK, AELIM); // ratio = 0.25
		BattleResult result1 = game.resolveBattle(bd).getBattleResult();
		BattleResult result2 = game.resolveBattle(bd1).getBattleResult();
		System.out.println("testRandomBattleResults(): " + result1.name() + ", " + result2.name());
		assertTrue(expected1.contains(result1));
		assertTrue(expected2.contains(result2));
	}
	
	// Feature: Retreating
	@Test
	public void testRetreatSuccess()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(rowley, 5, 6, NORTH);
		game.endStep();
		BattleDescriptorImpl bd = BattleDescriptorImpl.makeBattleDescriptorImpl(Arrays.asList(rowley), Arrays.asList(heth));
		testGame.setBattleResults(Arrays.asList(ABACK));
		BattleResolution br = game.resolveBattle(bd);
		assertEquals(ABACK, br.getBattleResult());
		assertEquals(gc(4, 7), game.whereIsUnit(rowley));
	}
	
	@Test
	public void testRetreatFail()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 1, 2, NORTH);
		testGame.putUnitAt(hampton, 2, 2, NORTH);
		testGame.putUnitAt(rowley, 1, 1, SOUTH);
		game.endStep();
		BattleDescriptorImpl bd = BattleDescriptorImpl.makeBattleDescriptorImpl(Arrays.asList(rowley), Arrays.asList(heth));
		testGame.setBattleResults(Arrays.asList(ABACK));
		BattleResolution br = game.resolveBattle(bd);
		assertEquals(ABACK, br.getBattleResult());
		assertEquals(rowley, br.getEliminatedUnionUnits().iterator().next());
		assertTrue(br.getActiveUnionUnits().isEmpty());
		assertEquals(heth, br.getActiveConfederateUnits().iterator().next());
		assertNull(game.whereIsUnit(rowley)); // rowley was defeated
	}
	
	@Test
	public void testRetreatDBack()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 1, 2, NORTH);
		testGame.putUnitAt(rowley, 1, 1, SOUTH);
		game.endStep();
		BattleDescriptorImpl bd = BattleDescriptorImpl.makeBattleDescriptorImpl(Arrays.asList(rowley), Arrays.asList(heth));
		testGame.setBattleResults(Arrays.asList(DBACK));
		BattleResolution br = game.resolveBattle(bd);
		assertEquals(DBACK, br.getBattleResult());
		assertEquals(gc(1,3), game.whereIsUnit(heth));
	}
	
	@Test
	public void testMultipleRetreat()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 1, 2, NORTH);
		testGame.putUnitAt(hampton, 2, 2, NORTH);
		testGame.putUnitAt(rowley, 1, 1, SOUTH);
		game.endStep();
		BattleDescriptorImpl bd = BattleDescriptorImpl.makeBattleDescriptorImpl(Arrays.asList(rowley), Arrays.asList(heth, hampton));
		testGame.setBattleResults(Arrays.asList(DBACK));
		BattleResolution br = game.resolveBattle(bd);
		assertEquals(DBACK, br.getBattleResult());
		assertEquals(gc(2,3), game.whereIsUnit(hampton));
		assertEquals(gc(1,3), game.whereIsUnit(heth));
	}
	
	// ===============================================================================================================
	// ===============================================================================================================
	// ===============================================================================================================
	// 									Professor-given tests for version 2
	// ===============================================================================================================
	// ===============================================================================================================
	// ==============================================================================================================
	
	@Test
	public void unitsStackedProperlyAtStartOfGame()
	{
		// Move units at (7, 28) during UMOVE, turn 1
		Collection<GbgUnit> units = game.getUnitsAt(makeCoordinate(7, 28));
		assertEquals(4, units.size());
	}
	
	@Test
	public void allStackedUnitsAtStartOfGameMove()
	{
		Iterator<GbgUnit> units = game.getUnitsAt(makeCoordinate(7, 28)).iterator();
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(5, 28));
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(6, 28));
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(8, 28));
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(9, 28));
		Collection<GbgUnit> remaining = game.getUnitsAt(makeCoordinate(7, 28));
		assertTrue(remaining == null || remaining.isEmpty());
	}
	
	@Test
	public void someEntryUnitsRemainAndAreRemoved()
	{
		Iterator<GbgUnit> units = game.getUnitsAt(makeCoordinate(7, 28)).iterator();
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(5, 28));
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(6, 28));
		Collection<GbgUnit> remaining = game.getUnitsAt(makeCoordinate(7, 28));
		assertEquals(2, remaining.size());
		game.endStep();
		remaining = game.getUnitsAt(makeCoordinate(7, 28));
		assertTrue(remaining == null || remaining.isEmpty());
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void tryToMoveThroughEnemyZOC()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 10, 10, SOUTH);		// ZOC = [(9, 11), (10, 11), (11, 11)]
		testGame.putUnitAt(hampton, 13, 10, SOUTH);	// ZOC = [(12, 11), (13, 11), (14, 11)]
		testGame.putUnitAt(devin, 11, 12, SOUTH);
		testGame.setGameStep(UMOVE);
		game.moveUnit(devin, makeCoordinate(11, 12), makeCoordinate(11, 9));
	}
	
	// Battle tests
	
	@Test
	public void hethDefeatsDevinAndGamble()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(devin, 5, 7, SOUTH);
		testGame.putUnitAt(gamble, 6, 7, SOUTH);
		testGame.setGameStep(CMOVE);
		game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
		game.endStep();		// CBATTLE
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		testGame.setBattleResults(Arrays.asList(DELIM));
		assertEquals(DELIM, game.resolveBattle(battle).getBattleResult());
	}
	
	@Test
	public void twoBattles()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(devin, 5, 7, SOUTH);
		testGame.putUnitAt(rodes, 20, 20, NORTH);
		testGame.putUnitAt(schurz, 20, 18, SOUTHWEST);
		testGame.setGameStep(CMOVE);
		game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
		game.moveUnit(rodes, makeCoordinate(20, 20), makeCoordinate(20, 19));
		game.endStep();		// CBATTLE
		Collection<BattleDescriptor> battles = game.getBattlesToResolve();
		BattleDescriptor battle = battles.iterator().next();
		if (battles.size() == 1) {
			assertTrue(battle.getAttackers().contains(heth)); 
		} else {
			assertTrue(battle.getAttackers().contains(heth) || battle.getAttackers().contains(rodes));
		}
	}
	
	@Test
	public void fightTwoBattles()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(rowley, 5, 7, NORTH);
		testGame.putUnitAt(hampton, 18, 18, EAST);
		testGame.putUnitAt(gamble, 20, 18, WEST);
		TestBattleDescriptor bd = new TestBattleDescriptor();
		bd.addAttacker(rowley);
		bd.addDefender(heth);
		TestBattleDescriptor bd1 = new TestBattleDescriptor();
		bd1.addAttacker(gamble);
		bd1.addDefender(hampton);
		game.moveUnit(rowley, makeCoordinate(5, 7), makeCoordinate(5, 6));
		game.moveUnit(gamble, makeCoordinate(20, 18), makeCoordinate(19, 18));
		game.endStep();	// CBATTLE
		testGame.setBattleResults(Arrays.asList(EXCHANGE, EXCHANGE));
		assertEquals(EXCHANGE, game.resolveBattle(bd).getBattleResult());
		assertEquals(EXCHANGE, game.resolveBattle(bd1).getBattleResult());
	}
	
	@Test
	public void attackerGetsEliminated()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(devin, 5, 7, NORTH);
		testGame.setGameStep(UMOVE);
		assertEquals(heth, game.getUnitsAt(makeCoordinate(5, 5)).iterator().next());
		game.moveUnit(devin, makeCoordinate(5,7), makeCoordinate(5, 6));
		game.endStep();		// UBATTLE
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		testGame.setBattleResults(Arrays.asList(AELIM));
		assertEquals(AELIM, game.resolveBattle(battle).getBattleResult());
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void unitTriesToFightTwoBattesInSameTurn()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(hampton, 7, 5, SOUTH);
		testGame.putUnitAt(schurz, 6, 7, NORTH);
		game.moveUnit(schurz, makeCoordinate(6, 7), makeCoordinate(6, 6));
		game.endStep();	// UBATTLE
		TestBattleDescriptor bd = new TestBattleDescriptor();
		bd.addAttacker(schurz);
		bd.addDefender(heth);
		TestBattleDescriptor bd1 = new TestBattleDescriptor();
		bd1.addAttacker(schurz);
		bd1.addDefender(hampton);
		game.resolveBattle(bd);
		game.resolveBattle(bd1);
	}
	
	@Test(expected=Exception.class)
	public void notAllUnitsHaveFoughtAtEndOfBattleStep()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(hampton, 7, 5, SOUTH);
		testGame.putUnitAt(schurz, 6, 7, NORTH);
		game.moveUnit(schurz, makeCoordinate(6, 7), makeCoordinate(6, 6));
		game.endStep();	// UBATTLE
		TestBattleDescriptor bd = new TestBattleDescriptor();
		bd.addAttacker(schurz);
		bd.addDefender(heth);
		game.resolveBattle(bd);
		game.endStep(); 	// CMOVE: hampton did not engage
	}
	
	@Test(expected=Exception.class)
	public void improperBatteleDescriptor()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 22, 5, SOUTH);
		testGame.putUnitAt(hampton, 7, 5, SOUTH);
		testGame.putUnitAt(schurz, 6, 7, NORTH);
		game.moveUnit(schurz, makeCoordinate(6, 7), makeCoordinate(6, 6));
		game.endStep();	// UBATTLE
		TestBattleDescriptor bd = new TestBattleDescriptor();
		bd.addAttacker(schurz);
		bd.addDefender(heth);
		game.resolveBattle(bd);		// heth is not in schurz' ZOC
	}
	
	// ===============================================================================================================
	// ===============================================================================================================
	// ===============================================================================================================
	// 									Test Driven Development for version 2 features
	// ===============================================================================================================
	// ===============================================================================================================
	// ===============================================================================================================
	// Feature: Reinforcements get added at later turns
	@Test
	public void stackedEntryUnitIsAsCorrectLocation()
	{
	    testGame.setGameTurn(8);
	    testGame.setGameStep(CBATTLE);
	    game.endStep();  // step -> UMOVE, turn -> 9
	    assertEquals(makeCoordinate(22, 22), game.whereIsUnit("Geary", UNION));
	    assertEquals(gc(22,22), game.whereIsUnit("Williams", UNION));
	    game.endStep();
	}
	
	@Test
	public void noReinforcementsIfSquareOccupied()
	{
	    testGame.setGameTurn(8);
	    testGame.setGameStep(CBATTLE);
	    testGame.putUnitAt(gamble, 22, 22, SOUTH);
	    game.endStep();  // step -> UMOVE, turn -> 9
	    assertNull(game.whereIsUnit("Geary", UNION));
	    assertEquals(gc(22,22), game.whereIsUnit("Gamble", UNION));
	    game.endStep();
	}
	
	// Feature: Stacked units get removed at the end of the move phase
	@Test
	public void removeStackedUnits()
	{
	    testGame.setGameTurn(8);
	    testGame.setGameStep(CBATTLE);
	    game.endStep();  // step -> UMOVE, turn -> 9
	    game.moveUnit(game.getUnit("Geary", UNION), gc(22,22), gc(22,21));
	    game.endStep();
	    assertNull(game.getUnitsAt(gc(22,22)));
	    assertEquals(gc(22,21), game.whereIsUnit("Geary", UNION));
	}
	
	// Feature: Pathfinding instead of just checking distance
	@Test
	public void testGetNeighbors() 
	{
		List<Coordinate> expected = Arrays.asList(gc(1,1), gc(1,2), gc(1,3), gc(2,1), gc(2,3), gc(3,1), gc(3,2), gc(3,3));
		assertEquals(expected, gc(2,2).getNeighbors());
	}
	
	@Test
	public void testPathfinding()
	{
		Path p = Pathfinder.getPathfinder("AStar").findPath(null, gc(1,1), gc(3,3));
		assertEquals(2, p.getMoveDistance());
	}
	
	// Feature: Zones of control affecting movement
	@Test
	public void testNeighborDirection() 
	{
		assertEquals(gc(2,2).getNeighborInDirection(NORTHWEST), gc(1,1));
		assertEquals(gc(2,2).getNeighborInDirection(WEST), gc(1,2));
		assertEquals(gc(2,2).getNeighborInDirection(SOUTHWEST), gc(1,3));
		assertEquals(gc(2,2).getNeighborInDirection(NORTH), gc(2,1));
		assertEquals(gc(2,2).getNeighborInDirection(SOUTH), gc(2,3));
		assertEquals(gc(2,2).getNeighborInDirection(NORTHEAST), gc(3,1));
		assertEquals(gc(2,2).getNeighborInDirection(EAST), gc(3,2));
		assertEquals(gc(2,2).getNeighborInDirection(SOUTHEAST), gc(3,3));
	}
	
	@Test
	public void testZoneOfControl()
	{
		// Orthagonal
		Set<Coordinate> expected = new HashSet<Coordinate>(Arrays.asList(gc(1,1), gc(1,2), gc(1,3)));
		testGame.putUnitAt(devin, 2, 2, WEST);
		assertEquals(expected, GettysburgEngine.getTheGame().getGameBoard().getUnitControl(devin));
		
		// Diagonal
		expected = new HashSet<Coordinate>(Arrays.asList(gc(2,1), gc(1,1), gc(1,2)));
		game.setUnitFacing(devin, NORTHWEST);
		assertEquals(expected, GettysburgEngine.getTheGame().getGameBoard().getUnitControl(devin));
		
		// HQ
		expected = new HashSet<Coordinate>();
		GbgUnit hq = BattleOrder.getBattleOrder(CONFEDERATE).get(1).unit;
		testGame.putUnitAt(hq, 4, 4, NORTH);
		assertEquals(expected, GettysburgEngine.getTheGame().getGameBoard().getUnitControl(hq));
	}
	
	@Test
	public void testZoneOfControlChanges()
	{
		// Start
		testGame.clearBoard();
		testGame.putUnitAt(gamble, 2, 2, SOUTH);
		Set<Coordinate> expected = new HashSet<Coordinate>(Arrays.asList(gc(1,3), gc(2,3), gc(3,3), gc(2,2)));
		assertEquals(expected, GettysburgEngine.getTheGame().getGameBoard().getArmyControl(UNION));
		
		// Move unit
		game.moveUnit(gamble, gc(2,2), gc(2,3));
		expected = new HashSet<Coordinate>(Arrays.asList(gc(1,4), gc(2,4), gc(3,4), gc(2,3)));
		assertEquals(expected, GettysburgEngine.getTheGame().getGameBoard().getArmyControl(UNION));
		
		// Change facings
		game.setUnitFacing(gamble, EAST);
		expected = new HashSet<Coordinate>(Arrays.asList(gc(3,2), gc(3,3), gc(3,4), gc(2,3)));
		assertEquals(expected, GettysburgEngine.getTheGame().getGameBoard().getArmyControl(UNION));
	}
	
	@Test
	public void testAllControl() 
	{
		Set<CoordinateImpl> unionControl = GettysburgEngine.getTheGame().getGameBoard().getArmyControl(UNION);
		Set<Coordinate> expected = new HashSet<Coordinate>(Arrays.asList(
				gc(11,11), gc(10,10), gc(10,11), gc(10,12),
				gc(13,9),  gc(13,10), gc(12,10), gc(14,10),
				gc(7,28),  gc(7,27),  gc(8,27),  gc(8,28)
		));
		assertEquals(expected, unionControl);
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void testControlBlocksMoving()
	{	
		// using example from discussion board
		// _C___   C=confederate
		// XXXO_   X=control
		// U_C__   U=gamble (4 move power)
		// _XXX_   O=goal  
		GbgUnit confed2 = BattleOrder.getBattleOrder(CONFEDERATE).get(2).unit;
		testGame.putUnitAt(heth, 2, 1, SOUTH);
		testGame.putUnitAt(confed2, 3, 3, SOUTH);
		testGame.putUnitAt(gamble, 1, 3, EAST);
		game.moveUnit(gamble, gc(1,3), gc(4,2));
	}
	
	@Test
	public void testMoveIntoControl() 
	{
		testGame.putUnitAt(heth, 2, 2, SOUTH);
		testGame.putUnitAt(gamble, 2, 4, NORTH);
		game.moveUnit(gamble, game.whereIsUnit(gamble), gc(2,3));
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void testMoveOutOfControl()
	{
		testGame.putUnitAt(heth, 2, 2, SOUTH);
		testGame.putUnitAt(gamble, 1, 3, SOUTH);
		game.moveUnit(gamble, gc(1,3), gc(2,3));
	}
	
	// Feature: getBattlesToResolve
	@Test(expected=GbgInvalidActionException.class)
	public void testGetBattlesWrongPhase() 
	{
		game.getBattlesToResolve();
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void testGetBattles()
	{
		List<BattleDescriptor> expected1 = Arrays.asList(GettysburgFactory.makeBattleDescriptor(
														Arrays.asList(gamble),
														Arrays.asList(heth)));
		testGame.putUnitAt(gamble, 2, 2, SOUTH);
		testGame.putUnitAt(heth, 2, 3, SOUTH);
		game.endStep(); // ubattle 
		assertEquals(expected1, game.getBattlesToResolve());
		game.endStep();
		// should throw exception - battles havent been resolved
	}
	
	// Feature: resolveBattle
	@Test(expected=GbgInvalidActionException.class)
	public void testResolveBattleWrongPhase() 
	{
		game.resolveBattle(null);
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void testWrongAttackerArmy()
	{
		BattleDescriptor bd = GettysburgFactory.makeBattleDescriptor(Arrays.asList(heth), Arrays.asList());
		game.endStep();
		game.resolveBattle(bd);
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void testWrongDefenderArmy() 
	{
		BattleDescriptor bd = GettysburgFactory.makeBattleDescriptor(Arrays.asList(), Arrays.asList(gamble));
		game.endStep();
		game.resolveBattle(bd);
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void testArmiesTooFar()
	{
		testGame.putUnitAt(gamble, 1, 1, SOUTH);
		testGame.putUnitAt(heth, 7, 7, NORTH);
		game.endStep();
		BattleDescriptor bd = GettysburgFactory.makeBattleDescriptor(Arrays.asList(gamble), Arrays.asList(heth));
		game.resolveBattle(bd);
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void testArmiesNotFacing()
	{
		testGame.putUnitAt(gamble, 2, 2, SOUTH);
		testGame.putUnitAt(heth, 2, 1, SOUTH);
		game.endStep();
		BattleDescriptor bd = GettysburgFactory.makeBattleDescriptor(Arrays.asList(gamble), Arrays.asList(heth));
		game.resolveBattle(bd);
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void testArmyBattleTwice()
	{
		testGame.clearBoard();
		testGame.putUnitAt(gamble, 2, 2, NORTH);
		testGame.putUnitAt(heth, 2, 1, SOUTH);
		game.endStep();
		BattleDescriptor bd = GettysburgFactory.makeBattleDescriptor(Arrays.asList(gamble), Arrays.asList(heth));
		testGame.setBattleResults(Arrays.asList(AELIM));
		assertEquals(AELIM, game.resolveBattle(bd).getBattleResult());
		testGame.putUnitAt(gamble, 2, 2, NORTH);
		game.resolveBattle(bd);
		game.endStep(); // can end after resolved battle
	}
	
	@Test
	public void hethDefeatsDevin()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(devin, 5, 7, SOUTH);
		testGame.setGameStep(CMOVE);
		game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
		game.endStep();		// CBATTLE
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		testGame.setBattleResults(Arrays.asList(DELIM));
		assertEquals(DELIM, game.resolveBattle(battle).getBattleResult());
	}
	
	@Test
	public void testArmyExchange()
	{
		GbgUnit atk3 = GbgUnitImpl.makeUnit(UNION, 3, NORTH, "TEST", 4, UnitSize.ARMY, UnitType.CAVALRY);
		testGame.putUnitAt(atk3, 2, 2, NORTH);
		game.endStep();
		game.endStep();
		testGame.putUnitAt(heth, 2, 1, SOUTH);	
		game.endStep();
		BattleDescriptor bd = GettysburgFactory.makeBattleDescriptor(Arrays.asList(heth), Arrays.asList(atk3));
		testGame.setBattleResults(Arrays.asList(EXCHANGE));
		assertEquals(EXCHANGE, game.resolveBattle(bd).getBattleResult());
	}
	
	// ===============================================================================================================
	// ===============================================================================================================
	// ===============================================================================================================
	// 											Professor provided tests
	// ===============================================================================================================
	// ===============================================================================================================
	// ===============================================================================================================
	@Test
	public void goToTurn2()
	{
		game.endStep();
		game.endStep();
		game.endStep();
		game.endStep();
		assertEquals(2, game.getTurnNumber());
	}

	@Test
	public void startOfTurn2IsUMOVEStep()
	{
		game.endStep();
		game.endStep();
		game.endStep();
		game.endStep();
		assertEquals(UMOVE, game.getCurrentStep());
	}

	// Movement tests
	@Test
	public void devinMovesSouthUsingANonStandardCoordinate()
	{
		game.moveUnit(devin, new TestCoordinate(13, 9), makeCoordinate(13, 11));
		assertEquals(makeCoordinate(13, 11), game.whereIsUnit(devin));
	}
	
	// Tests requiring the test double
	public void stackedEntryUnitsAreNotMoved()
	{
	    testGame.setGameTurn(8);
	    testGame.setGameStep(CBATTLE);
	    game.endStep();  // step -> UMOVE, turn -> 9
	    game.endStep();  // step -> UBATTLE
	    game.endStep();
	    assertNull(game.getUnitsAt(gc(22,22)));
	}	
	
	// Tests from version 1 (minus ones that no longer apply)
	@Test
	public void factoryMakesGame()
	{
		assertNotNull(game);
	}

	// Initial setup tests
	@Test
	public void gameTurnIsOneOnInitializedGame()
	{
		assertEquals(1, game.getTurnNumber());
	}

	@Test
	public void initialGameStatusIsInProgress()
	{
		assertEquals(IN_PROGRESS, game.getGameStatus());
	}

	@Test
	public void gameStepOnInitializedGameIsUMOVE()
	{
		assertEquals(UMOVE, game.getCurrentStep());
	}

	@Test
	public void correctSquareForGambleUsingWhereIsUnit()
	{
		assertEquals(makeCoordinate(11, 11), game.whereIsUnit("Gamble", UNION));
	}

	@Test
	public void correctSquareForGambleUsingGetUnitsAt()
	{
		GbgUnit unit = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
		assertNotNull(unit);
		assertEquals("Gamble", unit.getLeader());
	}

	@Test
	public void correctSquareForDevinUsingWhereIsUnit()
	{
		assertEquals(makeCoordinate(13, 9), game.whereIsUnit(devin));
	}

	@Test
	public void correctSquareForDevinUsingGetUnitsAt()
	{
		GbgUnit unit = game.getUnitsAt(makeCoordinate(13, 9)).iterator().next();
		assertNotNull(unit);
		assertEquals("Devin", unit.getLeader());
	}

	@Test
	public void correctSquareForHethUsingWhereIsUnit()
	{
		assertEquals(makeCoordinate(8, 8), game.whereIsUnit("Heth", CONFEDERATE));
	}

	@Test
	public void correctSquareForHethUsingGetUnitsAt()
	{
		GbgUnit unit = game.getUnitsAt(makeCoordinate(8, 8)).iterator().next();
		assertNotNull(unit);
		assertEquals("Heth", unit.getLeader());
	}

	@Test
	public void gambleFacesWest()
	{
		assertEquals(WEST, game.getUnitFacing(gamble));
	}

	@Test
	public void devinFacesSouth()
	{
		assertEquals(SOUTH, game.getUnitFacing(devin));
	}

	@Test
	public void hethFacesEast()
	{
		assertEquals(EAST, heth.getFacing());
	}

	// Game step and turn tests
	@Test
	public void unionBattleFollowsUnionMove()
	{
		doEndMoveStep();
		assertEquals(UBATTLE, game.getCurrentStep());
	}

	@Test
	public void confederateMoveFollowsUnionBattle()
	{
		doEndMoveStep();
		doEndBattleStep();
		assertEquals(CMOVE, game.getCurrentStep());
	}

	@Test
	public void confederateBattleFollowsConfederateMove()
	{
		game.endStep();
		game.endStep();
		assertEquals(CBATTLE, game.endStep());
	}

	@Test
	public void turnOneDuringConfederateBattle()
	{
		game.endStep();
		game.endStep();
		game.endStep();
		assertEquals(1, game.getTurnNumber());
	}

	@Test
	public void endOfGameUnionWins()
	{
		game.endStep();
		game.endStep();
		game.endStep();
		game.endStep();
		//assertEquals(UNION_WINS, game.getGameStatus());
	}

	// Movement tests
	@Test
	public void gambleMovesNorth()
	{
		game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(11, 10));
		assertEquals(makeCoordinate(11, 10), game.whereIsUnit(gamble));
		assertNull(">> Documentation says this should be null, not an empty array", game.getUnitsAt(makeCoordinate(11, 11)));
	}

	@Test
	public void devinMovesSouth()
	{
		game.moveUnit(devin,  makeCoordinate(13, 9), makeCoordinate(13, 11));
		assertEquals(makeCoordinate(13, 11), game.whereIsUnit(devin));
	}

	@Test
	public void hethMovesEast()
	{
		game.endStep();
		game.endStep();
		game.moveUnit(heth,  makeCoordinate(8, 8), makeCoordinate(10, 8));
		assertEquals(heth, game.getUnitsAt(makeCoordinate(10, 8)).iterator().next());
	}

	@Test
	public void hethMovesWest()
	{
		game.endStep();
		game.endStep();
		game.moveUnit(heth,  makeCoordinate(8, 8), makeCoordinate(9, 8));
		assertEquals(heth, game.getUnitsAt(makeCoordinate(9, 8)).iterator().next());
	}

	@Test
	public void devinMovesNorthEast()
	{
		game.moveUnit(devin,  makeCoordinate(13, 9), makeCoordinate(16, 6));
		assertEquals(makeCoordinate(16, 6), game.whereIsUnit(devin));
	}

	@Test
	public void devinMovesSouthWest()
	{
		game.moveUnit(devin,  makeCoordinate(13, 9), makeCoordinate(9, 13));
		assertEquals(makeCoordinate(9, 13), game.whereIsUnit(devin));
	}

	@Test
	public void gambleMovesSouthEast()
	{
		game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(13, 12));
		assertEquals(makeCoordinate(13, 12), game.whereIsUnit(gamble));
	}

	@Test
	public void gambleMovesNorthWest()
	{
		game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(12, 10));
		assertEquals(makeCoordinate(12, 10), game.whereIsUnit(gamble));
	}

	@Test
	public void hethMovesAnL()
	{
		game.endStep();
		game.endStep();
		game.moveUnit(heth,  makeCoordinate(8, 8), makeCoordinate(9, 6));
		assertEquals(heth, game.getUnitsAt(makeCoordinate(9, 6)).iterator().next());
	}

	@Test(expected=GbgInvalidMoveException.class)
	public void attemptToMoveTooFar()
	{
		game.moveUnit(devin, makeCoordinate(13, 9), makeCoordinate(18, 9));
	}

	@Test(expected=GbgInvalidMoveException.class)
	public void attemptToMoveOntoAnotherUnit()
	{
		game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(13, 9));
	}

	@Test(expected=GbgInvalidMoveException.class)
	public void attemptToMoveFromEmptySquare()
	{
		game.moveUnit(gamble,  makeCoordinate(10, 10), makeCoordinate(10, 9));
	}

	@Test(expected=GbgInvalidMoveException.class)
	public void attemptToMoveWrongArmyUnit()
	{
		game.moveUnit(heth, makeCoordinate(8, 8), makeCoordinate(9, 6));
	}

	@Test(expected=GbgInvalidMoveException.class)
	public void attemptToMoveUnitTwiceInOneTurn()
	{
		game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(12, 10));
		game.moveUnit(gamble, makeCoordinate(12, 10), makeCoordinate(12, 11));
	}

	// Facing tests
	@Test
	public void gambleFacesNorth()
	{
		game.setUnitFacing(gamble, NORTH);
		assertEquals(NORTH, game.getUnitFacing(gamble));
	}

	@Test
	public void devinFacesSoutheastAfterMoving()
	{
		game.moveUnit(devin, makeCoordinate(13, 9), makeCoordinate(13, 10));
		game.setUnitFacing(devin, SOUTHEAST);
		assertEquals(SOUTHEAST, game.getUnitFacing(devin));
	}

	@Test(expected=GbgInvalidMoveException.class)
	public void hethAttemptsFacingChangeAtWrongTime()
	{
		game.setUnitFacing(heth, WEST);
	}

	@Test(expected=GbgInvalidMoveException.class)
	public void devinTriesToSetFacingTwice()
	{
		game.setUnitFacing(devin, NORTHWEST);
		game.moveUnit(devin, makeCoordinate(13, 9), makeCoordinate(13, 10));
		game.setUnitFacing(devin, SOUTHEAST);
	}

	// Other tests
	@Test(expected=Exception.class)
	public void queryInvalidSquare()
	{
		game.getUnitsAt(makeCoordinate(30, 30));
	}
	
	@Test
	public void gameOverAfterFirstFullTurn()
	{
		game.endStep();
		game.endStep();
		game.endStep();
		//assertEquals(">> Do not take points off if this fails", GAME_OVER, game.endStep());
	}

	@Test(expected=Exception.class)
	public void endMoveStepWhenNotInUnionMove()
	{
		game.endStep();		// to UBATTLE
		doEndMoveStep();
	}

	@Test(expected=Exception.class)
	public void endBattleStepWhenNotInConfederateBattle()
	{
		game.endStep();
		game.endStep();		// to CMOVE
		doEndBattleStep();
	}

	@Test(expected=GbgInvalidMoveException.class)
	public void moveToStartingSquare()
	{
		game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(11, 11));
	}
	
	@Test
	public void gameOverTest()
	{
		game.endStep();
		game.endStep();
		game.endStep();
		game.endStep();
		//assertEquals(GAME_OVER, game.getCurrentStep());
	}

	//////////////// Helper methods /////////////////////////
	private void doEndMoveStep()
	{
			game.endMoveStep();
	}

	private void doEndBattleStep()
	{
			game.endBattleStep();
	}

	// ===============================================================================================================
	// ===============================================================================================================
	// ===============================================================================================================
	// 											My tests from version 1
	// ===============================================================================================================
	// ===============================================================================================================
	// ===============================================================================================================
	// Tests for coordinates
	@Test
	public void testDirectionTo() {
		Coordinate d = gc(3,3);
		assertEquals(d.directionTo(gc(2,2)), NORTHWEST);
		assertEquals(d.directionTo(gc(3,2)), NORTH);
		assertEquals(d.directionTo(gc(4,2)), NORTHEAST);
		assertEquals(d.directionTo(gc(2,3)), WEST);
		assertEquals(d.directionTo(gc(3,3)), NONE);
		assertEquals(d.directionTo(gc(4,3)), EAST);
		assertEquals(d.directionTo(gc(2,4)), SOUTHWEST);
		assertEquals(d.directionTo(gc(3,4)), SOUTH);
		assertEquals(d.directionTo(gc(4,4)), SOUTHEAST);
	}
	
	@Test
	public void testCoordinateToString() {
		Coordinate d = gc(3,3);
		assertEquals(d.toString(), "(3, 3)");
	}
	
	// Tests for units
	@Test
	public void testUnitBasicGetters() {
		GbgUnit u = BattleOrder.getBattleOrder(UNION).get(0).unit;
		GbgUnit c = BattleOrder.getBattleOrder(CONFEDERATE).get(0).unit;
		assertEquals(u, u);
		assertNotEquals(u, c);
		assertEquals(u.getArmy(), ArmyID.UNION);
		assertEquals(u.getCombatFactor(), 1);
		assertEquals(u.getMovementFactor(), 4);
		assertEquals(u.getSize(), UnitSize.BRIGADE);
		assertEquals(u.getType(), UnitType.CAVALRY);
	}
	
	@Test
	public void testUnitFacing() {
		GbgUnit u = BattleOrder.getBattleOrder(UNION).get(0).unit;
		assertEquals(u.getFacing(), WEST);
		u.setFacing(EAST);
		assertEquals(game.getUnitFacing(u), EAST);
	}
	
	@Test
	public void testFullTurn() {
		assertEquals(IN_PROGRESS, game.getGameStatus());
		game.endStep();
		assertEquals(UBATTLE, game.getCurrentStep());
		game.endStep();
		assertEquals(CMOVE, game.getCurrentStep());
		game.endMoveStep();
		assertEquals(CBATTLE, game.getCurrentStep());
		assertEquals(IN_PROGRESS, game.getGameStatus());
		game.endBattleStep();
		//assertEquals(GAME_OVER, game.getCurrentStep());
		//assertEquals(UNION_WINS, game.getGameStatus());
		
		//game.endStep();
		//assertEquals(GAME_OVER, game.getCurrentStep());
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void unitFacingWrongTurn() {
		GbgUnit u = game.getUnit("Heth", CONFEDERATE);
		assertEquals(u.getFacing(), EAST);
		game.setUnitFacing(u, SOUTH);
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void unitFacingWrongPhase() {
		game.endStep();
		game.setUnitFacing(devin, SOUTH);
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void unitFacingTwice() {
		game.endStep();
		game.endStep();
		GbgUnit u = game.getUnit("Heth", CONFEDERATE);
		assertEquals(u.getFacing(), EAST);
		game.setUnitFacing(u, SOUTH);
		assertEquals(u.getFacing(), SOUTH);
		game.setUnitFacing(u, NORTH);
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void unitMoveBattlePhase() {
		GbgUnit u = game.getUnit("Devin", UNION);
		Coordinate c = game.whereIsUnit(u);
		game.endStep();
		game.moveUnit(u, c, null);
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void unitMoveWrongTurn() {
		GbgUnit u = game.getUnit("Devin", UNION);
		Coordinate c = game.whereIsUnit(u);
		game.endStep();
		game.endStep();
		game.moveUnit(u, c, null);
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void unitMoveTooFar() {
		GbgUnit u = game.getUnit("Devin", UNION);
		Coordinate c = game.whereIsUnit(u);
		game.moveUnit(u, c, gc(20,20));
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void unitMoveTwice() {
		GbgUnit u = game.getUnit("Devin", UNION);
		Coordinate c = game.whereIsUnit(u);
		game.setUnitFacing(u, NORTHEAST);
		game.moveUnit(u, c, gc(14,10));
		game.moveUnit(u, c, gc(15,11));
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void unitMoveWrongStart() {
		GbgUnit u = game.getUnit("Devin", UNION);
		game.moveUnit(u, null, null);
	}
	
	@Test(expected=GbgInvalidMoveException.class)
	public void unitMoveOntoAnother() {
		GbgUnit u = game.getUnit("Devin", UNION);
		Coordinate c = game.whereIsUnit(u);
		game.moveUnit(u, c, gc(11,11));
	}
	
	@Test
	public void getUnitDoesntExist() {
		assertNull(game.getUnit("McDonald", UNION));
	}
	
	@Test
	public void testGetUnit() {
		assertEquals(BattleOrder.getBattleOrder(UNION).get(0).unit, game.getUnit("Gamble", UNION));
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void testInvalidEndStep1() {
		game.endBattleStep();
	}
	
	@Test(expected=GbgInvalidActionException.class)
	public void testInvalidEndStep2() {
		game.endMoveStep();
		game.endMoveStep();
	}

	private static CoordinateImpl gc(int x, int y) {
		return CoordinateImpl.makeCoordinate(x, y);
	}
	
	@Test
	public void testMiscCoverage() {
		new GettysburgFactory(); 
		new BattleOrder();
		GettysburgFactory.makeGame();
		assertEquals("Atk: [] Def: []", GettysburgFactory.makeBattleDescriptor(null, null).toString());
		GbgGameState.makeGameState(GAME_OVER, 0).endStep();
		Pathfinder.getPathfinder("null");
	}
}

class TestCoordinate implements Coordinate
{
	private int x, y;
	
	/**
	 * @return the x
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY()
	{
		return y;
	}

	public TestCoordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
}